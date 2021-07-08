package weka.attributeSelection;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.Utils;

public class ExhaustiveSearch extends ASSearch implements StartSetHandler, OptionHandler {
  private int[] m_starting;
  
  private Range m_startRange;
  
  private BitSet m_bestGroup;
  
  private double m_bestMerit;
  
  private boolean m_hasClass;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private boolean m_verbose;
  
  private boolean m_stopAfterFirst;
  
  private int m_evaluations;
  
  public String globalInfo() {
    return "ExhaustiveSearch : \n\nPerforms an exhaustive search through the space of attribute subsets starting from the empty set of attrubutes. Reports the best subset found. If a start set is supplied, the algorithm searches backward from the start point and reports the smallest subset with as good or better evaluation as the start point.\n";
  }
  
  public ExhaustiveSearch() {
    resetOptions();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tSpecify a starting set of attributes.\n\tEg. 1,3,5-7.\n\tIf a start point is supplied,\n\tExhaustive search stops after\n\tfinding the smallest possible subset\n\twith merit as good as or better than\n\tthe start set.", "P", 1, "-P <start set>"));
    vector.addElement(new Option("\tOutput subsets as the search progresses.\n\t(default = false).", "V", 0, "-V"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0)
      setStartSet(str); 
    setVerbose(Utils.getFlag('V', paramArrayOfString));
  }
  
  public String startSetTipText() {
    return "Set the start point for the search. This is specified as a comma seperated list off attribute indexes starting at 1. It can include ranges. Eg. 1,2,5-9,17.";
  }
  
  public void setStartSet(String paramString) throws Exception {
    this.m_startRange.setRanges(paramString);
  }
  
  public String getStartSet() {
    return this.m_startRange.getRanges();
  }
  
  public String verboseTipText() {
    return "Print progress information. Sends progress info to the terminal as the search progresses.";
  }
  
  public void setVerbose(boolean paramBoolean) {
    this.m_verbose = paramBoolean;
  }
  
  public boolean getVerbose() {
    return this.m_verbose;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[3];
    byte b = 0;
    if (!getStartSet().equals("")) {
      arrayOfString[b++] = "-P";
      arrayOfString[b++] = "" + startSetToString();
    } 
    if (this.m_verbose)
      arrayOfString[b++] = "-V"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  private String startSetToString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_starting == null)
      return getStartSet(); 
    for (byte b = 0; b < this.m_starting.length; b++) {
      boolean bool = false;
      if (!this.m_hasClass || (this.m_hasClass == true && b != this.m_classIndex)) {
        stringBuffer.append(this.m_starting[b] + 1);
        bool = true;
      } 
      if (b == this.m_starting.length - 1) {
        stringBuffer.append("");
      } else if (bool) {
        stringBuffer.append(",");
      } 
    } 
    return stringBuffer.toString();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\tExhaustive Search.\n\tStart set: ");
    if (this.m_starting == null) {
      stringBuffer.append("no attributes\n");
    } else {
      stringBuffer.append(startSetToString() + "\n");
    } 
    stringBuffer.append("\tNumber of evaluations: " + this.m_evaluations + "\n");
    stringBuffer.append("\tMerit of best subset found: " + Utils.doubleToString(Math.abs(this.m_bestMerit), 8, 3) + "\n");
    return stringBuffer.toString();
  }
  
  public int[] search(ASEvaluation paramASEvaluation, Instances paramInstances) throws Exception {
    boolean bool = false;
    this.m_numAttribs = paramInstances.numAttributes();
    this.m_bestGroup = new BitSet(this.m_numAttribs);
    if (!(paramASEvaluation instanceof SubsetEvaluator))
      throw new Exception(paramASEvaluation.getClass().getName() + " is not a " + "Subset evaluator!"); 
    if (paramASEvaluation instanceof UnsupervisedSubsetEvaluator) {
      this.m_hasClass = false;
    } else {
      this.m_hasClass = true;
      this.m_classIndex = paramInstances.classIndex();
    } 
    SubsetEvaluator subsetEvaluator = (SubsetEvaluator)paramASEvaluation;
    this.m_numAttribs = paramInstances.numAttributes();
    this.m_startRange.setUpper(this.m_numAttribs - 1);
    if (!getStartSet().equals(""))
      this.m_starting = this.m_startRange.getSelection(); 
    if (this.m_starting != null) {
      this.m_stopAfterFirst = true;
      for (byte b = 0; b < this.m_starting.length; b++) {
        if (this.m_starting[b] != this.m_classIndex)
          this.m_bestGroup.set(this.m_starting[b]); 
      } 
    } 
    double d1 = subsetEvaluator.evaluateSubset(this.m_bestGroup);
    this.m_evaluations++;
    int i = countFeatures(this.m_bestGroup);
    if (this.m_verbose && this.m_stopAfterFirst)
      System.out.println("Initial subset (" + Utils.doubleToString(Math.abs(d1), 8, 5) + "): " + printSubset(this.m_bestGroup)); 
    BitSet bitSet = new BitSet(this.m_numAttribs);
    double d2 = subsetEvaluator.evaluateSubset(bitSet);
    if (this.m_verbose)
      System.out.println("Zero feature subset (" + Utils.doubleToString(Math.abs(d2), 8, 5) + ")"); 
    if (d2 >= d1) {
      int j = countFeatures(bitSet);
      if (d2 > d1 || j < i) {
        d1 = d2;
        this.m_bestGroup = (BitSet)bitSet.clone();
        i = j;
      } 
      if (this.m_stopAfterFirst)
        bool = true; 
    } 
    if (!bool)
      for (byte b = 1; b <= this.m_numAttribs; b++) {
        int j = 0;
        bitSet = new BitSet(this.m_numAttribs);
        for (byte b1 = 0; b1 < b; b1++) {
          j ^= 1 << b1;
          bitSet.set(b1);
          if (this.m_hasClass && b1 == this.m_classIndex)
            bitSet.clear(b1); 
        } 
        d2 = subsetEvaluator.evaluateSubset(bitSet);
        this.m_evaluations++;
        if (d2 >= d1) {
          int k = countFeatures(bitSet);
          if (d2 > d1 || k < i) {
            d1 = d2;
            this.m_bestGroup = (BitSet)bitSet.clone();
            i = k;
            if (this.m_verbose)
              System.out.println("New best subset (" + Utils.doubleToString(Math.abs(d1), 8, 5) + "): " + printSubset(this.m_bestGroup)); 
          } 
          if (this.m_stopAfterFirst) {
            bool = true;
            break;
          } 
        } 
        while (j > 0) {
          j = generateNextSubset(j, b, bitSet);
          if (j > 0) {
            d2 = subsetEvaluator.evaluateSubset(bitSet);
            this.m_evaluations++;
            if (d2 >= d1) {
              int k = countFeatures(bitSet);
              if (d2 > d1 || k < i) {
                d1 = d2;
                this.m_bestGroup = (BitSet)bitSet.clone();
                i = k;
                if (this.m_verbose)
                  System.out.println("New best subset (" + Utils.doubleToString(Math.abs(d1), 8, 5) + "): " + printSubset(this.m_bestGroup)); 
              } 
              if (this.m_stopAfterFirst) {
                bool = true;
                // Byte code: goto -> 819
              } 
            } 
          } 
        } 
      }  
    this.m_bestMerit = d1;
    return attributeList(this.m_bestGroup);
  }
  
  private int countFeatures(BitSet paramBitSet) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_numAttribs; b2++) {
      if (paramBitSet.get(b2))
        b1++; 
    } 
    return b1;
  }
  
  private String printSubset(BitSet paramBitSet) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.m_numAttribs; b++) {
      if (paramBitSet.get(b))
        stringBuffer.append((b + 1) + " "); 
    } 
    return stringBuffer.toString();
  }
  
  private int[] attributeList(BitSet paramBitSet) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_numAttribs; b2++) {
      if (paramBitSet.get(b2))
        b1++; 
    } 
    int[] arrayOfInt = new int[b1];
    b1 = 0;
    for (byte b3 = 0; b3 < this.m_numAttribs; b3++) {
      if (paramBitSet.get(b3))
        arrayOfInt[b1++] = b3; 
    } 
    return arrayOfInt;
  }
  
  private int generateNextSubset(int paramInt1, int paramInt2, BitSet paramBitSet) {
    byte b = 0;
    boolean bool = false;
    int i;
    for (i = 0; i < this.m_numAttribs; i++)
      paramBitSet.clear(i); 
    while (!bool && b < paramInt2) {
      for (i = this.m_numAttribs - 1 - b; i >= 0; i--) {
        if ((paramInt1 & 1 << i) != 0) {
          paramInt1 ^= 1 << i;
          if (i != this.m_numAttribs - 1 - b) {
            paramInt1 ^= 1 << i + 1;
            for (byte b1 = 0; b1 < b; b1++)
              paramInt1 ^= 1 << i + 2 + b1; 
            bool = true;
            break;
          } 
          b++;
          break;
        } 
      } 
    } 
    for (i = this.m_numAttribs - 1; i >= 0; i--) {
      if ((paramInt1 & 1 << i) != 0 && i != this.m_classIndex)
        paramBitSet.set(i); 
    } 
    return paramInt1;
  }
  
  private void resetOptions() {
    this.m_starting = null;
    this.m_startRange = new Range();
    this.m_stopAfterFirst = false;
    this.m_verbose = false;
    this.m_evaluations = 0;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\ExhaustiveSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */