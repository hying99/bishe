package weka.filters.unsupervised.attribute;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.SparseInstance;
import weka.core.Stopwords;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class StringToWordVector extends Filter implements UnsupervisedFilter, OptionHandler {
  private String delimiters = " \n\t.,:'\"()?!";
  
  protected Range m_SelectedRange = null;
  
  private TreeMap m_Dictionary = new TreeMap();
  
  private boolean m_FirstBatchDone = false;
  
  private boolean m_OutputCounts = false;
  
  private String m_Prefix = "";
  
  private int[] docsCounts;
  
  private int numInstances = -1;
  
  private double avgDocLength = -1.0D;
  
  private int m_WordsToKeep = 1000;
  
  private boolean m_TFTransform;
  
  private boolean m_normalizeDocLength;
  
  private boolean m_IDFTransform;
  
  private boolean m_onlyAlphabeticTokens;
  
  private boolean m_lowerCaseTokens;
  
  private boolean m_useStoplist;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tOutput word counts rather than boolean word presence.\n", "C", 0, "-C"));
    vector.addElement(new Option("\tString containing the set of delimiter characters\n\t(default: \" \\n\\t.,:'\\\"()?!\")", "D", 1, "-D <delimiter set>"));
    vector.addElement(new Option("\tSpecify list of string attributes to convert to words (as weka Range).\n\t(default: select all string attributes)", "R", 1, "-R <index1,index2-index4,...>"));
    vector.addElement(new Option("\tSpecify a prefix for the created attribute names.\n\t(default: \"\")", "P", 1, "-P <attribute name prefix>"));
    vector.addElement(new Option("\tSpecify approximate number of word fields to create.\n\tSurplus words will be discarded..\n\t(default: 1000)", "W", 1, "-W <number of words to keep>"));
    vector.addElement(new Option("\tTransform the word frequencies into log(1+fij)\n\twhere fij is the frequency of word i in jth document(instance).\n", "T", 0, "-T"));
    vector.addElement(new Option("\tTransform each word frequency into:\n\tfij*log(num of Documents/num of  documents containing word i)\n\t  where fij if frequency of word i in  jth document(instance)", "I", 0, "-I"));
    vector.addElement(new Option("\tNormalize word frequencies of each document(instance) to average length of documents.", "N", 0, "-N"));
    vector.addElement(new Option("\tOnly form tokens from contiguous alphabetic sequences (The delimiter string is ignored if this is set).", "A", 0, "-A"));
    vector.addElement(new Option("\tConvert all tokens to lowercase before adding to the dictionary.", "L", 0, "-L"));
    vector.addElement(new Option("\tIgnore words that are in the stoplist.", "S", 0, "-S"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('D', paramArrayOfString);
    if (str.length() != 0)
      setDelimiters(str); 
    str = Utils.getOption('R', paramArrayOfString);
    if (str.length() != 0)
      setSelectedRange(str); 
    str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0)
      setAttributeNamePrefix(str); 
    str = Utils.getOption('W', paramArrayOfString);
    if (str.length() != 0)
      setWordsToKeep(Integer.valueOf(str).intValue()); 
    setOutputWordCounts(Utils.getFlag('C', paramArrayOfString));
    setTFTransform(Utils.getFlag('T', paramArrayOfString));
    setIDFTransform(Utils.getFlag('I', paramArrayOfString));
    setNormalizeDocLength(Utils.getFlag('N', paramArrayOfString));
    setLowerCaseTokens(Utils.getFlag('L', paramArrayOfString));
    setOnlyAlphabeticTokens(Utils.getFlag('A', paramArrayOfString));
    setUseStoplist(Utils.getFlag('S', paramArrayOfString));
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[16];
    byte b = 0;
    arrayOfString[b++] = "-D";
    arrayOfString[b++] = getDelimiters();
    if (getSelectedRange() != null) {
      arrayOfString[b++] = "-R";
      this.m_SelectedRange.setUpper(getInputFormat().numAttributes() - 1);
      arrayOfString[b++] = getSelectedRange().getRanges();
    } 
    if (!"".equals(getAttributeNamePrefix())) {
      arrayOfString[b++] = "-P";
      arrayOfString[b++] = getAttributeNamePrefix();
    } 
    arrayOfString[b++] = "-W";
    arrayOfString[b++] = String.valueOf(getWordsToKeep());
    if (getOutputWordCounts())
      arrayOfString[b++] = "-C"; 
    if (getTFTransform())
      arrayOfString[b++] = "-T"; 
    if (getIDFTransform())
      arrayOfString[b++] = "-I"; 
    if (getNormalizeDocLength())
      arrayOfString[b++] = "-N"; 
    if (getLowerCaseTokens())
      arrayOfString[b++] = "-L"; 
    if (getOnlyAlphabeticTokens())
      arrayOfString[b++] = "-A"; 
    if (getUseStoplist())
      arrayOfString[b++] = "-S"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public StringToWordVector() {}
  
  public StringToWordVector(int paramInt) {
    this.m_WordsToKeep = paramInt;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_FirstBatchDone = false;
    return false;
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (this.m_FirstBatchDone) {
      convertInstance(paramInstance);
      return true;
    } 
    bufferInput(paramInstance);
    return false;
  }
  
  public boolean batchFinished() throws Exception {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (!this.m_FirstBatchDone)
      determineDictionary(); 
    if (!this.m_normalizeDocLength || this.m_FirstBatchDone == true) {
      for (byte b = 0; b < getInputFormat().numInstances(); b++)
        convertInstance(getInputFormat().instance(b)); 
      flushInput();
    } else {
      FastVector fastVector = new FastVector();
      int i = 0;
      Instances instances = getInputFormat();
      this.avgDocLength = 0.0D;
      byte b;
      for (b = 0; b < instances.numInstances(); b++)
        i = convertInstancewoDocNorm(instances.instance(b), fastVector); 
      for (b = 0; b < fastVector.size(); b++) {
        Instance instance = (Instance)fastVector.elementAt(b);
        double d1 = 0.0D;
        double d2 = 0.0D;
        byte b1;
        for (b1 = 0; b1 < instance.numValues(); b1++) {
          if (instance.index(b1) >= i) {
            d2 = instance.valueSparse(b1);
            d1 += d2 * d2;
          } 
        } 
        d1 = Math.sqrt(d1);
        this.avgDocLength += d1;
        for (b1 = 0; b1 < instance.numValues(); b1++) {
          if (instance.index(b1) >= i) {
            d2 = instance.valueSparse(b1);
            d2 /= d1;
            instance.setValueSparse(b1, d2);
            if (d2 == 0.0D) {
              System.err.println("setting value " + instance.index(b1) + " to zero.");
              b1--;
            } 
          } 
        } 
      } 
      this.avgDocLength /= instances.numInstances();
      for (b = 0; b < fastVector.size(); b++) {
        Instance instance = (Instance)fastVector.elementAt(b);
        double d = 0.0D;
        for (byte b1 = 0; b1 < instance.numValues(); b1++) {
          if (instance.index(b1) >= i) {
            d = instance.valueSparse(b1);
            d *= this.avgDocLength;
            instance.setValueSparse(b1, d);
            if (d == 0.0D) {
              System.err.println("setting value " + instance.index(b1) + " to zero.");
              b1--;
            } 
          } 
        } 
        push(instance);
      } 
      flushInput();
    } 
    this.m_NewBatch = true;
    this.m_FirstBatchDone = true;
    return (numPendingOutput() != 0);
  }
  
  public String globalInfo() {
    return "Converts String attributes into a set of attributes representing word occurrence information from the text contained in the strings. The set of words (attributes) is determined by the first batch filtered (typically training data).";
  }
  
  public boolean getOutputWordCounts() {
    return this.m_OutputCounts;
  }
  
  public void setOutputWordCounts(boolean paramBoolean) {
    this.m_OutputCounts = paramBoolean;
  }
  
  public String outputWordCountsTipText() {
    return "Output word counts rather than boolean 0 or 1(indicating presence or absence of a word).";
  }
  
  public String getDelimiters() {
    return this.delimiters;
  }
  
  public void setDelimiters(String paramString) {
    this.delimiters = paramString;
  }
  
  public String delimitersTipText() {
    return "Set of delimiter characters to use in tokenizing (default: \" \\n\\t.,:'\\\"()?!\"). This option is ignored if onlyAlphabeticTokens option is set to true.";
  }
  
  public Range getSelectedRange() {
    return this.m_SelectedRange;
  }
  
  public void setSelectedRange(String paramString) {
    this.m_SelectedRange = new Range(paramString);
  }
  
  public String getAttributeNamePrefix() {
    return this.m_Prefix;
  }
  
  public void setAttributeNamePrefix(String paramString) {
    this.m_Prefix = paramString;
  }
  
  public String attributeNamePrefixTipText() {
    return "Prefix for the created attribute names. (default: \"\")";
  }
  
  public int getWordsToKeep() {
    return this.m_WordsToKeep;
  }
  
  public void setWordsToKeep(int paramInt) {
    this.m_WordsToKeep = paramInt;
  }
  
  public String wordsToKeepTipText() {
    return "The number of words (per class if there is a class attribute assigned) to attempt to keep.";
  }
  
  public boolean getTFTransform() {
    return this.m_TFTransform;
  }
  
  public void setTFTransform(boolean paramBoolean) {
    this.m_TFTransform = paramBoolean;
  }
  
  public String TFTransformTipText() {
    return "Sets whether if the word frequencies should be transformed into:\n    log(1+fij) \n       where fij is the frequency of word i in document (instance) j.";
  }
  
  public boolean getIDFTransform() {
    return this.m_IDFTransform;
  }
  
  public void setIDFTransform(boolean paramBoolean) {
    this.m_IDFTransform = paramBoolean;
  }
  
  public String IDFTransformTipText() {
    return "Sets whether if the word frequencies in a document should be transformed into: \n   fij*log(num of Docs/num of Docs with word i) \n      where fij is the frequency of word i in document (instance) j.";
  }
  
  public boolean getNormalizeDocLength() {
    return this.m_normalizeDocLength;
  }
  
  public void setNormalizeDocLength(boolean paramBoolean) {
    this.m_normalizeDocLength = paramBoolean;
  }
  
  public String normalizeDocLengthTipText() {
    return "Sets whether if the word frequencies for a document (instance) should be normalized or not.";
  }
  
  public boolean getOnlyAlphabeticTokens() {
    return this.m_onlyAlphabeticTokens;
  }
  
  public void setOnlyAlphabeticTokens(boolean paramBoolean) {
    this.m_onlyAlphabeticTokens = paramBoolean;
  }
  
  public String onlyAlphabeticTokensTipText() {
    return "Sets whether if the word tokens are to be formed only from contiguous alphabetic sequences (The delimiter string is ignored if this option is set to true).";
  }
  
  public boolean getLowerCaseTokens() {
    return this.m_lowerCaseTokens;
  }
  
  public void setLowerCaseTokens(boolean paramBoolean) {
    this.m_lowerCaseTokens = paramBoolean;
  }
  
  public String lowerCaseTokensTipText() {
    return "If set then all the word tokens are converted to lower case before being added to the dictionary.";
  }
  
  public boolean getUseStoplist() {
    return this.m_useStoplist;
  }
  
  public void setUseStoplist(boolean paramBoolean) {
    this.m_useStoplist = paramBoolean;
  }
  
  public String useStoplistTipText() {
    return "Ignores all the words that are on the stoplist, if set to true.";
  }
  
  private static void sortArray(int[] paramArrayOfint) {
    int j = paramArrayOfint.length - 1;
    int i;
    for (i = 1; i <= j / 9; i = 3 * i + 1);
    while (i > 0) {
      for (int k = i + 1; k <= j; k++) {
        int n = paramArrayOfint[k];
        int m;
        for (m = k; m > i && paramArrayOfint[m - i] > n; m -= i)
          paramArrayOfint[m] = paramArrayOfint[m - i]; 
        paramArrayOfint[m] = n;
      } 
      i /= 3;
    } 
  }
  
  private void determineSelectedRange() {
    Instances instances = getInputFormat();
    if (this.m_SelectedRange == null) {
      StringBuffer stringBuffer1 = new StringBuffer();
      for (byte b1 = 0; b1 < instances.numAttributes(); b1++) {
        if (instances.attribute(b1).type() == 2)
          stringBuffer1.append((b1 + 1) + ","); 
      } 
      this.m_SelectedRange = new Range(stringBuffer1.toString());
    } 
    this.m_SelectedRange.setUpper(instances.numAttributes() - 1);
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < instances.numAttributes(); b++) {
      if (this.m_SelectedRange.isInRange(b) && instances.attribute(b).type() == 2)
        stringBuffer.append((b + 1) + ","); 
    } 
    this.m_SelectedRange.setRanges(stringBuffer.toString());
    this.m_SelectedRange.setUpper(instances.numAttributes() - 1);
  }
  
  private void determineDictionary() {
    int i = getInputFormat().classIndex();
    int j = 1;
    if (i != -1)
      j = getInputFormat().attribute(i).numValues(); 
    TreeMap[] arrayOfTreeMap = new TreeMap[j];
    int k;
    for (k = 0; k < j; k++)
      arrayOfTreeMap[k] = new TreeMap(); 
    determineSelectedRange();
    for (k = 0; k < getInputFormat().numInstances(); k++) {
      Instance instance = getInputFormat().instance(k);
      int i1 = 0;
      if (i != -1)
        i1 = (int)instance.classValue(); 
      Hashtable hashtable = new Hashtable();
      for (byte b = 0; b < instance.numAttributes(); b++) {
        if (this.m_SelectedRange.isInRange(b) && !instance.isMissing(b)) {
          AlphabeticStringTokenizer alphabeticStringTokenizer;
          if (!this.m_onlyAlphabeticTokens) {
            StringTokenizer stringTokenizer = new StringTokenizer(instance.stringValue(b), this.delimiters);
          } else {
            alphabeticStringTokenizer = new AlphabeticStringTokenizer(this, instance.stringValue(b));
          } 
          while (alphabeticStringTokenizer.hasMoreElements()) {
            String str = ((String)alphabeticStringTokenizer.nextElement()).intern();
            if (this.m_lowerCaseTokens == true)
              str = str.toLowerCase(); 
            if (this.m_useStoplist == true && Stopwords.isStopword(str))
              continue; 
            if (!hashtable.contains(str))
              hashtable.put(str, new Integer(0)); 
            Count count = arrayOfTreeMap[i1].get(str);
            if (count == null) {
              arrayOfTreeMap[i1].put(str, new Count(this, 1));
              continue;
            } 
            count.count++;
          } 
        } 
      } 
      Enumeration enumeration = hashtable.keys();
      while (enumeration.hasMoreElements()) {
        String str = enumeration.nextElement();
        Count count = arrayOfTreeMap[i1].get(str);
        if (count != null) {
          count.docCount++;
          continue;
        } 
        System.err.println("Warning: A word should definitely be in the dictionary.Please check the code");
      } 
    } 
    k = 0;
    int[] arrayOfInt = new int[j];
    for (byte b1 = 0; b1 < j; b1++) {
      k += arrayOfTreeMap[b1].size();
      int[] arrayOfInt1 = new int[arrayOfTreeMap[b1].size()];
      byte b = 0;
      for (String str : arrayOfTreeMap[b1].keySet()) {
        Count count = arrayOfTreeMap[b1].get(str);
        arrayOfInt1[b] = count.count;
        b++;
      } 
      sortArray(arrayOfInt1);
      if (arrayOfInt1.length < this.m_WordsToKeep) {
        arrayOfInt[b1] = 1;
      } else {
        arrayOfInt[b1] = Math.max(1, arrayOfInt1[arrayOfInt1.length - this.m_WordsToKeep]);
      } 
    } 
    FastVector fastVector = new FastVector(k + getInputFormat().numAttributes());
    int m = -1;
    for (byte b2 = 0; b2 < getInputFormat().numAttributes(); b2++) {
      if (!this.m_SelectedRange.isInRange(b2)) {
        if (getInputFormat().classIndex() == b2)
          m = fastVector.size(); 
        fastVector.addElement(getInputFormat().attribute(b2).copy());
      } 
    } 
    TreeMap treeMap = new TreeMap();
    int n = fastVector.size();
    for (byte b3 = 0; b3 < j; b3++) {
      for (String str : arrayOfTreeMap[b3].keySet()) {
        Count count = arrayOfTreeMap[b3].get(str);
        if (count.count >= arrayOfInt[b3] && treeMap.get(str) == null) {
          treeMap.put(str, new Integer(n++));
          fastVector.addElement(new Attribute(this.m_Prefix + str));
        } 
      } 
    } 
    this.docsCounts = new int[fastVector.size()];
    for (String str : treeMap.keySet()) {
      int i1 = ((Integer)treeMap.get(str)).intValue();
      int i2 = 0;
      for (byte b = 0; b < j; b++) {
        Count count = arrayOfTreeMap[b].get(str);
        if (count != null)
          i2 += count.docCount; 
      } 
      this.docsCounts[i1] = i2;
    } 
    fastVector.trimToSize();
    this.m_Dictionary = treeMap;
    this.numInstances = getInputFormat().numInstances();
    Instances instances = new Instances(getInputFormat().relationName(), fastVector, 0);
    instances.setClassIndex(m);
    setOutputFormat(instances);
  }
  
  private void convertInstance(Instance paramInstance) throws Exception {
    TreeMap treeMap = new TreeMap();
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < getInputFormat().numAttributes(); b2++) {
      if (!this.m_SelectedRange.isInRange(b2)) {
        if (getInputFormat().attribute(b2).type() != 2) {
          if (paramInstance.value(b2) != 0.0D)
            treeMap.put(new Integer(b1), new Double(paramInstance.value(b2))); 
        } else if (paramInstance.isMissing(b2)) {
          treeMap.put(new Integer(b1), new Double(Instance.missingValue()));
        } else {
          if (outputFormatPeek().attribute(b1).numValues() == 0)
            outputFormatPeek().attribute(b1).addStringValue("Hack to defeat SparseInstance bug"); 
          int i = outputFormatPeek().attribute(b1).addStringValue(paramInstance.stringValue(b2));
          treeMap.put(new Integer(b1), new Double(i));
        } 
        b1++;
      } 
    } 
    for (b2 = 0; b2 < paramInstance.numAttributes(); b2++) {
      if (this.m_SelectedRange.isInRange(b2) && !paramInstance.isMissing(b2)) {
        AlphabeticStringTokenizer alphabeticStringTokenizer;
        if (!this.m_onlyAlphabeticTokens) {
          StringTokenizer stringTokenizer = new StringTokenizer(paramInstance.stringValue(b2), this.delimiters);
        } else {
          alphabeticStringTokenizer = new AlphabeticStringTokenizer(this, paramInstance.stringValue(b2));
        } 
        while (alphabeticStringTokenizer.hasMoreElements()) {
          String str = alphabeticStringTokenizer.nextElement();
          if (this.m_lowerCaseTokens == true)
            str = str.toLowerCase(); 
          Integer integer = (Integer)this.m_Dictionary.get(str);
          if (integer != null) {
            if (this.m_OutputCounts) {
              Double double_ = (Double)treeMap.get(integer);
              if (double_ != null) {
                treeMap.put(integer, new Double(double_.doubleValue() + 1.0D));
                continue;
              } 
              treeMap.put(integer, new Double(1.0D));
              continue;
            } 
            treeMap.put(integer, new Double(1.0D));
          } 
        } 
      } 
    } 
    if (this.m_TFTransform == true) {
      Iterator iterator1 = treeMap.keySet().iterator();
      for (byte b = 0; iterator1.hasNext(); b++) {
        Integer integer = iterator1.next();
        if (integer.intValue() >= b1) {
          double d = ((Double)treeMap.get(integer)).doubleValue();
          d = Math.log(d + 1.0D);
          treeMap.put(integer, new Double(d));
        } 
      } 
    } 
    if (this.m_IDFTransform == true) {
      Iterator iterator1 = treeMap.keySet().iterator();
      for (byte b = 0; iterator1.hasNext(); b++) {
        Integer integer = iterator1.next();
        if (integer.intValue() >= b1) {
          double d = ((Double)treeMap.get(integer)).doubleValue();
          d *= Math.log(this.numInstances / this.docsCounts[integer.intValue()]);
          treeMap.put(integer, new Double(d));
        } 
      } 
    } 
    if (this.m_normalizeDocLength == true) {
      if (this.avgDocLength < 0.0D)
        throw new Exception("Error. Average Doc Length not defined yet."); 
      double d = 0.0D;
      Iterator iterator1 = treeMap.keySet().iterator();
      byte b;
      for (b = 0; iterator1.hasNext(); b++) {
        Integer integer = iterator1.next();
        if (integer.intValue() >= b1) {
          double d1 = ((Double)treeMap.get(integer)).doubleValue();
          d += d1 * d1;
        } 
      } 
      iterator1 = treeMap.keySet().iterator();
      for (b = 0; iterator1.hasNext(); b++) {
        Integer integer = iterator1.next();
        if (integer.intValue() >= b1) {
          double d1 = ((Double)treeMap.get(integer)).doubleValue();
          d1 /= Math.sqrt(d);
          d1 *= this.avgDocLength;
          treeMap.put(integer, new Double(d1));
        } 
      } 
    } 
    double[] arrayOfDouble = new double[treeMap.size()];
    int[] arrayOfInt = new int[treeMap.size()];
    Iterator iterator = treeMap.keySet().iterator();
    for (byte b3 = 0; iterator.hasNext(); b3++) {
      Integer integer = iterator.next();
      Double double_ = (Double)treeMap.get(integer);
      arrayOfDouble[b3] = double_.doubleValue();
      arrayOfInt[b3] = integer.intValue();
    } 
    SparseInstance sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble, arrayOfInt, outputFormatPeek().numAttributes());
    sparseInstance.setDataset(outputFormatPeek());
    push((Instance)sparseInstance);
  }
  
  private int convertInstancewoDocNorm(Instance paramInstance, FastVector paramFastVector) {
    TreeMap treeMap = new TreeMap();
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < getInputFormat().numAttributes(); b2++) {
      if (!this.m_SelectedRange.isInRange(b2)) {
        if (getInputFormat().attribute(b2).type() != 2) {
          if (paramInstance.value(b2) != 0.0D)
            treeMap.put(new Integer(b1), new Double(paramInstance.value(b2))); 
        } else if (paramInstance.isMissing(b2)) {
          treeMap.put(new Integer(b1), new Double(Instance.missingValue()));
        } else {
          if (outputFormatPeek().attribute(b1).numValues() == 0)
            outputFormatPeek().attribute(b1).addStringValue("Hack to defeat SparseInstance bug"); 
          int i = outputFormatPeek().attribute(b1).addStringValue(paramInstance.stringValue(b2));
          treeMap.put(new Integer(b1), new Double(i));
        } 
        b1++;
      } 
    } 
    for (b2 = 0; b2 < paramInstance.numAttributes(); b2++) {
      if (this.m_SelectedRange.isInRange(b2) && !paramInstance.isMissing(b2)) {
        AlphabeticStringTokenizer alphabeticStringTokenizer;
        if (!this.m_onlyAlphabeticTokens) {
          StringTokenizer stringTokenizer = new StringTokenizer(paramInstance.stringValue(b2), this.delimiters);
        } else {
          alphabeticStringTokenizer = new AlphabeticStringTokenizer(this, paramInstance.stringValue(b2));
        } 
        while (alphabeticStringTokenizer.hasMoreElements()) {
          String str = alphabeticStringTokenizer.nextElement();
          if (this.m_lowerCaseTokens == true)
            str = str.toLowerCase(); 
          Integer integer = (Integer)this.m_Dictionary.get(str);
          if (integer != null) {
            if (this.m_OutputCounts) {
              Double double_ = (Double)treeMap.get(integer);
              if (double_ != null) {
                treeMap.put(integer, new Double(double_.doubleValue() + 1.0D));
                continue;
              } 
              treeMap.put(integer, new Double(1.0D));
              continue;
            } 
            treeMap.put(integer, new Double(1.0D));
          } 
        } 
      } 
    } 
    if (this.m_TFTransform == true) {
      Iterator iterator1 = treeMap.keySet().iterator();
      for (byte b = 0; iterator1.hasNext(); b++) {
        Integer integer = iterator1.next();
        if (integer.intValue() >= b1) {
          double d = ((Double)treeMap.get(integer)).doubleValue();
          d = Math.log(d + 1.0D);
          treeMap.put(integer, new Double(d));
        } 
      } 
    } 
    if (this.m_IDFTransform == true) {
      Iterator iterator1 = treeMap.keySet().iterator();
      for (byte b = 0; iterator1.hasNext(); b++) {
        Integer integer = iterator1.next();
        if (integer.intValue() >= b1) {
          double d = ((Double)treeMap.get(integer)).doubleValue();
          d *= Math.log(this.numInstances / this.docsCounts[integer.intValue()]);
          treeMap.put(integer, new Double(d));
        } 
      } 
    } 
    double[] arrayOfDouble = new double[treeMap.size()];
    int[] arrayOfInt = new int[treeMap.size()];
    Iterator iterator = treeMap.keySet().iterator();
    for (byte b3 = 0; iterator.hasNext(); b3++) {
      Integer integer = iterator.next();
      Double double_ = (Double)treeMap.get(integer);
      arrayOfDouble[b3] = double_.doubleValue();
      arrayOfInt[b3] = integer.intValue();
    } 
    SparseInstance sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble, arrayOfInt, outputFormatPeek().numAttributes());
    sparseInstance.setDataset(outputFormatPeek());
    paramFastVector.addElement(sparseInstance);
    return b1;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new StringToWordVector(), paramArrayOfString);
      } else {
        Filter.filterFile(new StringToWordVector(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
  
  private class AlphabeticStringTokenizer implements Enumeration {
    private char[] str;
    
    int currentPos;
    
    private final StringToWordVector this$0;
    
    public AlphabeticStringTokenizer(StringToWordVector this$0, String param1String) {
      this.this$0 = this$0;
      this.currentPos = 0;
      this.str = new char[param1String.length()];
      param1String.getChars(0, param1String.length(), this.str, 0);
    }
    
    public boolean hasMoreElements() {
      int i;
      for (i = this.currentPos; i < this.str.length && (this.str[i] < 'a' || this.str[i] > 'z') && (this.str[i] < 'A' || this.str[i] > 'Z'); i++);
      this.currentPos = i;
      return (i < this.str.length && ((this.str[i] >= 'a' && this.str[i] <= 'z') || (this.str[i] >= 'A' && this.str[i] <= 'Z')));
    }
    
    public Object nextElement() {
      int i;
      for (i = this.currentPos; i < this.str.length && this.str[i] < 'a' && this.str[i] > 'z' && this.str[i] < 'A' && this.str[i] > 'Z'; i++);
      int j = i;
      if (i >= this.str.length)
        throw new NoSuchElementException("no more tokens present"); 
      while (j < this.str.length && ((this.str[j] >= 'a' && this.str[j] <= 'z') || (this.str[j] >= 'A' && this.str[j] <= 'Z')))
        j++; 
      String str = new String(this.str, i, j - this.currentPos);
      this.currentPos = j;
      return str;
    }
  }
  
  private class Count implements Serializable {
    public int count;
    
    public int docCount;
    
    private final StringToWordVector this$0;
    
    public Count(StringToWordVector this$0, int param1Int) {
      this.this$0 = this$0;
      this.count = param1Int;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\StringToWordVector.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */