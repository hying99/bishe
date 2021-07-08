package weka.classifiers.rules;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.AdditionalMeasureProducer;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Remove;

public class DecisionTable extends Classifier implements OptionHandler, WeightedInstancesHandler, AdditionalMeasureProducer {
  private Hashtable m_entries;
  
  private int[] m_decisionFeatures;
  
  private Filter m_disTransform;
  
  private Remove m_delTransform;
  
  private IBk m_ibk;
  
  private Instances m_theInstances;
  
  private int m_numAttributes;
  
  private int m_numInstances;
  
  private boolean m_classIsNominal;
  
  private boolean m_debug;
  
  private boolean m_useIBk;
  
  private boolean m_displayRules;
  
  private int m_maxStale;
  
  private int m_CVFolds;
  
  private Random m_rr;
  
  private double m_majority;
  
  public String globalInfo() {
    return "Class for building and using a simple decision table majority classifier. For more information see: \n\nKohavi R. (1995). \"The Power of Decision Tables.\" In Proc European Conference on Machine Learning.";
  }
  
  private void insertIntoTable(Instance paramInstance, double[] paramArrayOfdouble) throws Exception {
    hashKey hashKey;
    if (paramArrayOfdouble != null) {
      hashKey = new hashKey(paramArrayOfdouble);
    } else {
      hashKey = new hashKey(paramInstance, paramInstance.numAttributes());
    } 
    double[] arrayOfDouble = (double[])this.m_entries.get(hashKey);
    if (arrayOfDouble == null) {
      if (this.m_classIsNominal) {
        double[] arrayOfDouble1 = new double[this.m_theInstances.classAttribute().numValues()];
        arrayOfDouble1[(int)paramInstance.classValue()] = paramInstance.weight();
        this.m_entries.put(hashKey, arrayOfDouble1);
      } else {
        double[] arrayOfDouble1 = new double[2];
        arrayOfDouble1[0] = paramInstance.classValue() * paramInstance.weight();
        arrayOfDouble1[1] = paramInstance.weight();
        this.m_entries.put(hashKey, arrayOfDouble1);
      } 
    } else if (this.m_classIsNominal) {
      arrayOfDouble[(int)paramInstance.classValue()] = arrayOfDouble[(int)paramInstance.classValue()] + paramInstance.weight();
      this.m_entries.put(hashKey, arrayOfDouble);
    } else {
      arrayOfDouble[0] = arrayOfDouble[0] + paramInstance.classValue() * paramInstance.weight();
      arrayOfDouble[1] = arrayOfDouble[1] + paramInstance.weight();
      this.m_entries.put(hashKey, arrayOfDouble);
    } 
  }
  
  double classifyInstanceLeaveOneOut(Instance paramInstance, double[] paramArrayOfdouble) throws Exception {
    hashKey hashKey = new hashKey(paramArrayOfdouble);
    if (this.m_classIsNominal) {
      double[] arrayOfDouble1;
      if ((arrayOfDouble1 = (double[])this.m_entries.get(hashKey)) == null)
        throw new Error("This should never happen!"); 
      double[] arrayOfDouble2 = new double[arrayOfDouble1.length];
      System.arraycopy(arrayOfDouble1, 0, arrayOfDouble2, 0, arrayOfDouble1.length);
      arrayOfDouble2[(int)paramInstance.classValue()] = arrayOfDouble2[(int)paramInstance.classValue()] - paramInstance.weight();
      boolean bool = false;
      for (byte b = 0; b < arrayOfDouble2.length; b++) {
        if (!Utils.eq(arrayOfDouble2[b], 0.0D)) {
          bool = true;
          break;
        } 
      } 
      if (bool) {
        Utils.normalize(arrayOfDouble2);
        return Utils.maxIndex(arrayOfDouble2);
      } 
      return this.m_majority;
    } 
    double[] arrayOfDouble;
    if ((arrayOfDouble = (double[])this.m_entries.get(hashKey)) != null) {
      double[] arrayOfDouble1 = new double[arrayOfDouble.length];
      System.arraycopy(arrayOfDouble, 0, arrayOfDouble1, 0, arrayOfDouble.length);
      arrayOfDouble1[0] = arrayOfDouble1[0] - paramInstance.classValue() * paramInstance.weight();
      arrayOfDouble1[1] = arrayOfDouble1[1] - paramInstance.weight();
      return Utils.eq(arrayOfDouble1[1], 0.0D) ? this.m_majority : (arrayOfDouble1[0] / arrayOfDouble1[1]);
    } 
    throw new Error("This should never happen!");
  }
  
  double classifyFoldCV(Instances paramInstances, int[] paramArrayOfint) throws Exception {
    double[] arrayOfDouble2;
    byte b2 = 0;
    int i = paramInstances.numInstances();
    int j = this.m_theInstances.classAttribute().numValues();
    double[][] arrayOfDouble = new double[i][j];
    double[] arrayOfDouble1 = new double[paramArrayOfint.length];
    double d = 0.0D;
    int k = this.m_theInstances.classIndex();
    if (this.m_classIsNominal) {
      arrayOfDouble2 = new double[j];
    } else {
      arrayOfDouble2 = new double[2];
    } 
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      Instance instance = paramInstances.instance(b1);
      for (byte b = 0; b < paramArrayOfint.length; b++) {
        if (paramArrayOfint[b] == k) {
          arrayOfDouble1[b] = Double.MAX_VALUE;
        } else if (instance.isMissing(paramArrayOfint[b])) {
          arrayOfDouble1[b] = Double.MAX_VALUE;
        } else {
          arrayOfDouble1[b] = instance.value(paramArrayOfint[b]);
        } 
      } 
      hashKey hashKey = new hashKey(arrayOfDouble1);
      arrayOfDouble[b1] = (double[])this.m_entries.get(hashKey);
      if ((double[])this.m_entries.get(hashKey) == null)
        throw new Error("This should never happen!"); 
      if (this.m_classIsNominal) {
        arrayOfDouble[b1][(int)instance.classValue()] = arrayOfDouble[b1][(int)instance.classValue()] - instance.weight();
      } else {
        arrayOfDouble[b1][0] = arrayOfDouble[b1][0] - instance.classValue() * instance.weight();
        arrayOfDouble[b1][1] = arrayOfDouble[b1][1] - instance.weight();
      } 
      b2++;
    } 
    for (b1 = 0; b1 < i; b1++) {
      Instance instance = paramInstances.instance(b1);
      System.arraycopy(arrayOfDouble[b1], 0, arrayOfDouble2, 0, arrayOfDouble2.length);
      if (this.m_classIsNominal) {
        boolean bool = false;
        for (byte b = 0; b < arrayOfDouble2.length; b++) {
          if (!Utils.eq(arrayOfDouble2[b], 0.0D)) {
            bool = true;
            break;
          } 
        } 
        if (bool) {
          Utils.normalize(arrayOfDouble2);
          if (Utils.maxIndex(arrayOfDouble2) == instance.classValue())
            d += instance.weight(); 
        } else if (instance.classValue() == this.m_majority) {
          d += instance.weight();
        } 
      } else if (Utils.eq(arrayOfDouble2[1], 0.0D)) {
        d += instance.weight() * (this.m_majority - instance.classValue()) * instance.weight() * (this.m_majority - instance.classValue());
      } else {
        double d1 = arrayOfDouble2[0] / arrayOfDouble2[1];
        d += instance.weight() * (d1 - instance.classValue()) * instance.weight() * (d1 - instance.classValue());
      } 
    } 
    for (b1 = 0; b1 < i; b1++) {
      Instance instance = paramInstances.instance(b1);
      if (this.m_classIsNominal) {
        arrayOfDouble[b1][(int)instance.classValue()] = arrayOfDouble[b1][(int)instance.classValue()] + instance.weight();
      } else {
        arrayOfDouble[b1][0] = arrayOfDouble[b1][0] + instance.classValue() * instance.weight();
        arrayOfDouble[b1][1] = arrayOfDouble[b1][1] + instance.weight();
      } 
    } 
    return d;
  }
  
  private double estimateAccuracy(BitSet paramBitSet, int paramInt) throws Exception {
    int[] arrayOfInt = new int[paramInt];
    double d = 0.0D;
    double[] arrayOfDouble = new double[paramInt];
    int i = this.m_theInstances.classIndex();
    byte b2 = 0;
    byte b1;
    for (b1 = 0; b1 < this.m_numAttributes; b1++) {
      if (paramBitSet.get(b1))
        arrayOfInt[b2++] = b1; 
    } 
    this.m_entries = new Hashtable((int)(this.m_theInstances.numInstances() * 1.5D));
    for (b1 = 0; b1 < this.m_numInstances; b1++) {
      Instance instance = this.m_theInstances.instance(b1);
      for (byte b = 0; b < arrayOfInt.length; b++) {
        if (arrayOfInt[b] == i) {
          arrayOfDouble[b] = Double.MAX_VALUE;
        } else if (instance.isMissing(arrayOfInt[b])) {
          arrayOfDouble[b] = Double.MAX_VALUE;
        } else {
          arrayOfDouble[b] = instance.value(arrayOfInt[b]);
        } 
      } 
      insertIntoTable(instance, arrayOfDouble);
    } 
    if (this.m_CVFolds == 1) {
      for (b1 = 0; b1 < this.m_numInstances; b1++) {
        Instance instance = this.m_theInstances.instance(b1);
        for (byte b = 0; b < arrayOfInt.length; b++) {
          if (arrayOfInt[b] == i) {
            arrayOfDouble[b] = Double.MAX_VALUE;
          } else if (instance.isMissing(arrayOfInt[b])) {
            arrayOfDouble[b] = Double.MAX_VALUE;
          } else {
            arrayOfDouble[b] = instance.value(arrayOfInt[b]);
          } 
        } 
        double d1 = classifyInstanceLeaveOneOut(instance, arrayOfDouble);
        if (this.m_classIsNominal) {
          if (d1 == instance.classValue())
            d += instance.weight(); 
        } else {
          d += instance.weight() * (d1 - instance.classValue()) * instance.weight() * (d1 - instance.classValue());
        } 
      } 
    } else {
      this.m_theInstances.randomize(this.m_rr);
      this.m_theInstances.stratify(this.m_CVFolds);
      for (b1 = 0; b1 < this.m_CVFolds; b1++) {
        Instances instances = this.m_theInstances.testCV(this.m_CVFolds, b1);
        d += classifyFoldCV(instances, arrayOfInt);
      } 
    } 
    return this.m_classIsNominal ? (d / this.m_theInstances.sumOfWeights()) : -Math.sqrt(d / this.m_theInstances.sumOfWeights());
  }
  
  private String printSub(BitSet paramBitSet) {
    String str = "";
    for (byte b = 0; b < this.m_numAttributes; b++) {
      if (paramBitSet.get(b))
        str = str + " " + (b + 1); 
    } 
    return str;
  }
  
  private void best_first() throws Exception {
    byte b3 = 0;
    boolean bool1 = false;
    boolean bool2 = false;
    Hashtable hashtable = new Hashtable((int)(200.0D * this.m_numAttributes * 1.5D));
    LinkedList linkedList = new LinkedList(this);
    double[] arrayOfDouble = new double[1];
    arrayOfDouble[0] = 0.0D;
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 0;
    BitSet bitSet = new BitSet(this.m_numAttributes);
    int i = this.m_theInstances.classIndex();
    bitSet.set(i);
    arrayOfDouble[0] = estimateAccuracy(bitSet, 1);
    if (this.m_debug)
      System.out.println("Accuracy of initial subset: " + arrayOfDouble[0]); 
    linkedList.addToList(bitSet, arrayOfDouble[0]);
    hashtable.put(bitSet, "");
    while (arrayOfInt[0] < this.m_maxStale) {
      boolean bool = false;
      if (linkedList.size() == 0) {
        arrayOfInt[0] = this.m_maxStale;
        break;
      } 
      Link link = linkedList.getLinkAt(0);
      BitSet bitSet1 = (BitSet)link.getGroup().clone();
      linkedList.removeLinkAt(0);
      for (byte b = 0; b < this.m_numAttributes; b++) {
        boolean bool3 = (b != i && !bitSet1.get(b)) ? true : false;
        if (bool3) {
          bitSet1.set(b);
          BitSet bitSet2 = (BitSet)bitSet1.clone();
          if (!hashtable.containsKey(bitSet2)) {
            byte b4 = 0;
            for (byte b5 = 0; b5 < this.m_numAttributes; b5++) {
              if (bitSet2.get(b5))
                b4++; 
            } 
            double d = estimateAccuracy(bitSet2, b4);
            if (this.m_debug)
              System.out.println("evaluating: " + printSub(bitSet2) + " " + d); 
            bool3 = (d - arrayOfDouble[0] > 1.0E-5D) ? true : false;
            if (bool3) {
              if (this.m_debug)
                System.out.println("new best feature set: " + printSub(bitSet2) + " " + d); 
              bool = true;
              arrayOfInt[0] = 0;
              arrayOfDouble[0] = d;
              bitSet = (BitSet)bitSet1.clone();
            } 
            linkedList.addToList(bitSet2, d);
            hashtable.put(bitSet2, "");
            b3++;
          } 
          bitSet1.clear(b);
        } 
      } 
      if (!bool)
        arrayOfInt[0] = arrayOfInt[0] + 1; 
    } 
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < this.m_numAttributes) {
      if (bitSet.get(b1))
        b2++; 
      b1++;
    } 
    this.m_decisionFeatures = new int[b2];
    b1 = 0;
    b2 = 0;
    while (b1 < this.m_numAttributes) {
      if (bitSet.get(b1))
        this.m_decisionFeatures[b2++] = b1; 
      b1++;
    } 
  }
  
  protected void resetOptions() {
    this.m_entries = null;
    this.m_decisionFeatures = null;
    this.m_debug = false;
    this.m_useIBk = false;
    this.m_CVFolds = 1;
    this.m_maxStale = 5;
    this.m_displayRules = false;
  }
  
  public DecisionTable() {
    resetOptions();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(5);
    vector.addElement(new Option("\tNumber of fully expanded non improving subsets to consider\n\tbefore terminating a best first search.\n\tUse in conjunction with -B. (Default = 5)", "S", 1, "-S <number of non improving nodes>"));
    vector.addElement(new Option("\tUse cross validation to evaluate features.\n\tUse number of folds = 1 for leave one out CV.\n\t(Default = leave one out CV)", "X", 1, "-X <number of folds>"));
    vector.addElement(new Option("\tUse nearest neighbour instead of global table majority.\n", "I", 0, "-I"));
    vector.addElement(new Option("\tDisplay decision table rules.\n", "R", 0, "-R"));
    return vector.elements();
  }
  
  public String crossValTipText() {
    return "Sets the number of folds for cross validation (1 = leave one out).";
  }
  
  public void setCrossVal(int paramInt) {
    this.m_CVFolds = paramInt;
  }
  
  public int getCrossVal() {
    return this.m_CVFolds;
  }
  
  public String maxStaleTipText() {
    return "Sets the number of non improving decision tables to consider before abandoning the search.";
  }
  
  public void setMaxStale(int paramInt) {
    this.m_maxStale = paramInt;
  }
  
  public int getMaxStale() {
    return this.m_maxStale;
  }
  
  public String useIBkTipText() {
    return "Sets whether IBk should be used instead of the majority class.";
  }
  
  public void setUseIBk(boolean paramBoolean) {
    this.m_useIBk = paramBoolean;
  }
  
  public boolean getUseIBk() {
    return this.m_useIBk;
  }
  
  public String displayRulesTipText() {
    return "Sets whether rules are to be printed.";
  }
  
  public void setDisplayRules(boolean paramBoolean) {
    this.m_displayRules = paramBoolean;
  }
  
  public boolean getDisplayRules() {
    return this.m_displayRules;
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('X', paramArrayOfString);
    if (str.length() != 0)
      this.m_CVFolds = Integer.parseInt(str); 
    str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0)
      this.m_maxStale = Integer.parseInt(str); 
    this.m_useIBk = Utils.getFlag('I', paramArrayOfString);
    this.m_displayRules = Utils.getFlag('R', paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[7];
    byte b = 0;
    arrayOfString[b++] = "-X";
    arrayOfString[b++] = "" + this.m_CVFolds;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + this.m_maxStale;
    if (this.m_useIBk)
      arrayOfString[b++] = "-I"; 
    if (this.m_displayRules)
      arrayOfString[b++] = "-R"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_rr = new Random(1L);
    this.m_theInstances = new Instances(paramInstances);
    this.m_theInstances.deleteWithMissingClass();
    if (this.m_theInstances.numInstances() == 0)
      throw new Exception("No training instances without missing class!"); 
    if (this.m_theInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (this.m_theInstances.classAttribute().isNumeric()) {
      this.m_disTransform = (Filter)new Discretize();
      this.m_classIsNominal = false;
      ((Discretize)this.m_disTransform).setBins(10);
      ((Discretize)this.m_disTransform).setInvertSelection(true);
      String str = "";
      str = str + (this.m_theInstances.classIndex() + 1);
      ((Discretize)this.m_disTransform).setAttributeIndices(str);
    } else {
      this.m_disTransform = (Filter)new Discretize();
      ((Discretize)this.m_disTransform).setUseBetterEncoding(true);
      this.m_classIsNominal = true;
    } 
    this.m_disTransform.setInputFormat(this.m_theInstances);
    this.m_theInstances = Filter.useFilter(this.m_theInstances, this.m_disTransform);
    this.m_numAttributes = this.m_theInstances.numAttributes();
    this.m_numInstances = this.m_theInstances.numInstances();
    this.m_majority = this.m_theInstances.meanOrMode(this.m_theInstances.classAttribute());
    best_first();
    this.m_delTransform = new Remove();
    this.m_delTransform.setInvertSelection(true);
    this.m_delTransform.setAttributeIndicesArray(this.m_decisionFeatures);
    this.m_delTransform.setInputFormat(this.m_theInstances);
    this.m_theInstances = Filter.useFilter(this.m_theInstances, (Filter)this.m_delTransform);
    this.m_numAttributes = this.m_theInstances.numAttributes();
    this.m_entries = new Hashtable((int)(this.m_theInstances.numInstances() * 1.5D));
    for (byte b = 0; b < this.m_numInstances; b++) {
      Instance instance = this.m_theInstances.instance(b);
      insertIntoTable(instance, null);
    } 
    if (this.m_useIBk) {
      this.m_ibk = new IBk();
      this.m_ibk.buildClassifier(this.m_theInstances);
    } 
    this.m_theInstances = new Instances(this.m_theInstances, 0);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    this.m_disTransform.input(paramInstance);
    this.m_disTransform.batchFinished();
    paramInstance = this.m_disTransform.output();
    this.m_delTransform.input(paramInstance);
    this.m_delTransform.batchFinished();
    paramInstance = this.m_delTransform.output();
    hashKey hashKey = new hashKey(paramInstance, paramInstance.numAttributes());
    double[] arrayOfDouble;
    if ((arrayOfDouble = (double[])this.m_entries.get(hashKey)) == null) {
      if (this.m_useIBk) {
        arrayOfDouble = this.m_ibk.distributionForInstance(paramInstance);
      } else if (!this.m_classIsNominal) {
        arrayOfDouble = new double[1];
        arrayOfDouble[0] = this.m_majority;
      } else {
        arrayOfDouble = new double[this.m_theInstances.classAttribute().numValues()];
        arrayOfDouble[(int)this.m_majority] = 1.0D;
      } 
    } else if (!this.m_classIsNominal) {
      double[] arrayOfDouble1 = new double[1];
      arrayOfDouble1[0] = arrayOfDouble[0] / arrayOfDouble[1];
      arrayOfDouble = arrayOfDouble1;
    } else {
      double[] arrayOfDouble1 = new double[arrayOfDouble.length];
      System.arraycopy(arrayOfDouble, 0, arrayOfDouble1, 0, arrayOfDouble.length);
      Utils.normalize(arrayOfDouble1);
      arrayOfDouble = arrayOfDouble1;
    } 
    return arrayOfDouble;
  }
  
  public String printFeatures() {
    String str = "";
    for (byte b = 0; b < this.m_decisionFeatures.length; b++) {
      if (b == 0) {
        str = "" + (this.m_decisionFeatures[b] + 1);
      } else {
        str = str + "," + (this.m_decisionFeatures[b] + 1);
      } 
    } 
    return str;
  }
  
  public double measureNumRules() {
    return this.m_entries.size();
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(1);
    vector.addElement("measureNumRules");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.compareToIgnoreCase("measureNumRules") == 0)
      return measureNumRules(); 
    throw new IllegalArgumentException(paramString + " not supported (DecisionTable)");
  }
  
  public String toString() {
    if (this.m_entries == null)
      return "Decision Table: No model built yet."; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Decision Table:\n\nNumber of training instances: " + this.m_numInstances + "\nNumber of Rules : " + this.m_entries.size() + "\n");
    if (this.m_useIBk) {
      stringBuffer.append("Non matches covered by IB1.\n");
    } else {
      stringBuffer.append("Non matches covered by Majority class.\n");
    } 
    stringBuffer.append("Best first search for feature set,\nterminated after " + this.m_maxStale + " non improving subsets.\n");
    stringBuffer.append("Evaluation (for feature selection): CV ");
    if (this.m_CVFolds > 1) {
      stringBuffer.append("(" + this.m_CVFolds + " fold) ");
    } else {
      stringBuffer.append("(leave one out) ");
    } 
    stringBuffer.append("\nFeature set: " + printFeatures());
    if (this.m_displayRules) {
      int i = 0;
      for (byte b1 = 0; b1 < this.m_theInstances.numAttributes(); b1++) {
        if (this.m_theInstances.attribute(b1).name().length() > i)
          i = this.m_theInstances.attribute(b1).name().length(); 
        if (this.m_classIsNominal || b1 != this.m_theInstances.classIndex()) {
          Enumeration enumeration1 = this.m_theInstances.attribute(b1).enumerateValues();
          while (enumeration1.hasMoreElements()) {
            String str = enumeration1.nextElement();
            if (str.length() > i)
              i = str.length(); 
          } 
        } 
      } 
      stringBuffer.append("\n\nRules:\n");
      StringBuffer stringBuffer1 = new StringBuffer();
      byte b2;
      for (b2 = 0; b2 < this.m_theInstances.numAttributes(); b2++) {
        if (this.m_theInstances.classIndex() != b2) {
          int j = i - this.m_theInstances.attribute(b2).name().length();
          stringBuffer1.append(this.m_theInstances.attribute(b2).name());
          for (byte b = 0; b < j + 1; b++)
            stringBuffer1.append(" "); 
        } 
      } 
      stringBuffer1.append(this.m_theInstances.attribute(this.m_theInstances.classIndex()).name() + "  ");
      for (b2 = 0; b2 < stringBuffer1.length() + 10; b2++)
        stringBuffer.append("="); 
      stringBuffer.append("\n");
      stringBuffer.append(stringBuffer1);
      stringBuffer.append("\n");
      for (b2 = 0; b2 < stringBuffer1.length() + 10; b2++)
        stringBuffer.append("="); 
      stringBuffer.append("\n");
      Enumeration enumeration = this.m_entries.keys();
      while (enumeration.hasMoreElements()) {
        hashKey hashKey = enumeration.nextElement();
        stringBuffer.append(hashKey.toString(this.m_theInstances, i));
        double[] arrayOfDouble = (double[])this.m_entries.get(hashKey);
        if (this.m_classIsNominal) {
          int j = Utils.maxIndex(arrayOfDouble);
          try {
            stringBuffer.append(this.m_theInstances.classAttribute().value(j) + "\n");
          } catch (Exception exception) {
            System.out.println(exception.getMessage());
          } 
          continue;
        } 
        stringBuffer.append((arrayOfDouble[0] / arrayOfDouble[1]) + "\n");
      } 
      for (byte b3 = 0; b3 < stringBuffer1.length() + 10; b3++)
        stringBuffer.append("="); 
      stringBuffer.append("\n");
      stringBuffer.append("\n");
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      DecisionTable decisionTable = new DecisionTable();
      System.out.println(Evaluation.evaluateModel(decisionTable, paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
  
  public static class hashKey implements Serializable {
    private double[] attributes;
    
    private boolean[] missing;
    
    private String[] values;
    
    private int key;
    
    public hashKey(Instance param1Instance, int param1Int) throws Exception {
      int i = param1Instance.classIndex();
      this.key = -999;
      this.attributes = new double[param1Int];
      this.missing = new boolean[param1Int];
      for (byte b = 0; b < param1Int; b++) {
        if (b == i) {
          this.missing[b] = true;
        } else {
          this.missing[b] = param1Instance.isMissing(b);
          if (!param1Instance.isMissing(b))
            this.attributes[b] = param1Instance.value(b); 
        } 
      } 
    }
    
    public String toString(Instances param1Instances, int param1Int) {
      int i = param1Instances.classIndex();
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < this.attributes.length; b++) {
        if (b != i)
          if (this.missing[b]) {
            stringBuffer.append("?");
            for (byte b1 = 0; b1 < param1Int; b1++)
              stringBuffer.append(" "); 
          } else {
            String str = param1Instances.attribute(b).value((int)this.attributes[b]);
            StringBuffer stringBuffer1 = new StringBuffer(str);
            for (byte b1 = 0; b1 < param1Int - str.length() + 1; b1++)
              stringBuffer1.append(" "); 
            stringBuffer.append(stringBuffer1);
          }  
      } 
      return stringBuffer.toString();
    }
    
    public hashKey(double[] param1ArrayOfdouble) {
      int i = param1ArrayOfdouble.length;
      this.key = -999;
      this.attributes = new double[i];
      this.missing = new boolean[i];
      for (byte b = 0; b < i; b++) {
        if (param1ArrayOfdouble[b] == Double.MAX_VALUE) {
          this.missing[b] = true;
        } else {
          this.missing[b] = false;
          this.attributes[b] = param1ArrayOfdouble[b];
        } 
      } 
    }
    
    public int hashCode() {
      int i = 0;
      if (this.key != -999)
        return this.key; 
      for (byte b = 0; b < this.attributes.length; b++) {
        if (this.missing[b]) {
          i += b * 13;
        } else {
          i = (int)(i + (b * 5) * (this.attributes[b] + 1.0D));
        } 
      } 
      if (this.key == -999)
        this.key = i; 
      return i;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null || !param1Object.getClass().equals(getClass()))
        return false; 
      boolean bool = true;
      if (param1Object instanceof hashKey) {
        hashKey hashKey1 = (hashKey)param1Object;
        for (byte b = 0; b < this.attributes.length; b++) {
          boolean bool1 = hashKey1.missing[b];
          if (this.missing[b] || bool1) {
            if ((this.missing[b] && !bool1) || (!this.missing[b] && bool1)) {
              bool = false;
              break;
            } 
          } else if (this.attributes[b] != hashKey1.attributes[b]) {
            bool = false;
            break;
          } 
        } 
      } else {
        return false;
      } 
      return bool;
    }
    
    public void print_hash_code() {
      System.out.println("Hash val: " + hashCode());
    }
  }
  
  public class LinkedList extends FastVector {
    private final DecisionTable this$0;
    
    public LinkedList(DecisionTable this$0) {
      this.this$0 = this$0;
    }
    
    public void removeLinkAt(int param1Int) throws Exception {
      if (param1Int >= 0 && param1Int < size()) {
        removeElementAt(param1Int);
      } else {
        throw new Exception("index out of range (removeLinkAt)");
      } 
    }
    
    public DecisionTable.Link getLinkAt(int param1Int) throws Exception {
      if (size() == 0)
        throw new Exception("List is empty (getLinkAt)"); 
      if (param1Int >= 0 && param1Int < size())
        return (DecisionTable.Link)elementAt(param1Int); 
      throw new Exception("index out of range (getLinkAt)");
    }
    
    public void addToList(BitSet param1BitSet, double param1Double) {
      DecisionTable.Link link = new DecisionTable.Link(this.this$0, param1BitSet, param1Double);
      if (size() == 0) {
        addElement(link);
      } else if (param1Double > ((DecisionTable.Link)firstElement()).getMerit()) {
        insertElementAt(link, 0);
      } else {
        byte b = 0;
        int i = size();
        boolean bool = false;
        while (!bool && b < i) {
          if (param1Double > ((DecisionTable.Link)elementAt(b)).getMerit()) {
            insertElementAt(link, b);
            bool = true;
            continue;
          } 
          if (b == i - 1) {
            addElement(link);
            bool = true;
            continue;
          } 
          b++;
        } 
      } 
    }
  }
  
  public class Link {
    BitSet m_group;
    
    double m_merit;
    
    private final DecisionTable this$0;
    
    public Link(DecisionTable this$0, BitSet param1BitSet, double param1Double) {
      this.this$0 = this$0;
      this.m_group = (BitSet)param1BitSet.clone();
      this.m_merit = param1Double;
    }
    
    public BitSet getGroup() {
      return this.m_group;
    }
    
    public double getMerit() {
      return this.m_merit;
    }
    
    public String toString() {
      return "Node: " + this.m_group.toString() + "  " + this.m_merit;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\DecisionTable.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */