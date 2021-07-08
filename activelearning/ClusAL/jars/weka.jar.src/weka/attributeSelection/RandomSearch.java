package weka.attributeSelection;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.Utils;

public class RandomSearch extends ASSearch implements StartSetHandler, OptionHandler {
  private int[] m_starting;
  
  private Range m_startRange;
  
  private BitSet m_bestGroup;
  
  private double m_bestMerit;
  
  private boolean m_onlyConsiderBetterAndSmaller;
  
  private boolean m_hasClass;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private int m_seed;
  
  private double m_searchSize;
  
  private int m_iterations;
  
  private Random m_random;
  
  private boolean m_verbose;
  
  public String globalInfo() {
    return "RandomSearch : \n\nPerforms a Random search in the space of attribute subsets. If no start set is supplied, Random search starts from a random point and reports the best subset found. If a start set is supplied, Random searches randomly for subsets that are as good or better than the start point with the same or or fewer attributes. Using RandomSearch in conjunction with a start set containing all attributes equates to the LVF algorithm of Liu and Setiono (ICML-96).\n";
  }
  
  public RandomSearch() {
    resetOptions();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tSpecify a starting set of attributes.\n\tEg. 1,3,5-7.\n\tIf a start point is supplied,\n\trandom search evaluates the start\n\tpoint and then randomly looks for\n\tsubsets that are as good as or better\n\tthan the start point with the same\n\tor lower cardinality.", "P", 1, "-P <start set>"));
    vector.addElement(new Option("\tPercent of search space to consider.\n\t(default = 25%).", "F", 1, "-F <percent> "));
    vector.addElement(new Option("\tOutput subsets as the search progresses.\n\t(default = false).", "V", 0, "-V"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0)
      setStartSet(str); 
    str = Utils.getOption('F', paramArrayOfString);
    if (str.length() != 0)
      setSearchPercent((new Double(str)).doubleValue()); 
    setVerbose(Utils.getFlag('V', paramArrayOfString));
  }
  
  public String startSetTipText() {
    return "Set the start point for the search. This is specified as a comma seperated list off attribute indexes starting at 1. It can include ranges. Eg. 1,2,5-9,17. If specified, Random searches for subsets of attributes that are as good as or better than the start set with the same or lower cardinality.";
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
  
  public String searchPercentTipText() {
    return "Percentage of the search space to explore.";
  }
  
  public void setSearchPercent(double paramDouble) {
    paramDouble = Math.abs(paramDouble);
    if (paramDouble == 0.0D)
      paramDouble = 25.0D; 
    if (paramDouble > 100.0D)
      paramDouble = 100.0D; 
    this.m_searchSize = paramDouble / 100.0D;
  }
  
  public double getSearchPercent() {
    return this.m_searchSize;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[5];
    byte b = 0;
    if (this.m_verbose)
      arrayOfString[b++] = "-V"; 
    if (!getStartSet().equals("")) {
      arrayOfString[b++] = "-P";
      arrayOfString[b++] = "" + startSetToString();
    } 
    arrayOfString[b++] = "-F";
    arrayOfString[b++] = "" + this.m_searchSize;
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
    stringBuffer.append("\tRandom search.\n\tStart set: ");
    if (this.m_starting == null) {
      stringBuffer.append("no attributes\n");
    } else {
      stringBuffer.append(startSetToString() + "\n");
    } 
    stringBuffer.append("\tNumber of iterations: " + this.m_iterations + " (" + (this.m_searchSize * 100.0D) + "% of the search space)\n");
    stringBuffer.append("\tMerit of best subset found: " + Utils.doubleToString(Math.abs(this.m_bestMerit), 8, 3) + "\n");
    return stringBuffer.toString();
  }
  
  public int[] search(ASEvaluation paramASEvaluation, Instances paramInstances) throws Exception {
    double d;
    int i = this.m_numAttribs;
    this.m_bestGroup = new BitSet(this.m_numAttribs);
    this.m_onlyConsiderBetterAndSmaller = false;
    if (!(paramASEvaluation instanceof SubsetEvaluator))
      throw new Exception(paramASEvaluation.getClass().getName() + " is not a " + "Subset evaluator!"); 
    this.m_random = new Random(this.m_seed);
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
      for (j = 0; j < this.m_starting.length; j++) {
        if (this.m_starting[j] != this.m_classIndex)
          this.m_bestGroup.set(this.m_starting[j]); 
      } 
      this.m_onlyConsiderBetterAndSmaller = true;
      d = subsetEvaluator.evaluateSubset(this.m_bestGroup);
      i = countFeatures(this.m_bestGroup);
    } else {
      this.m_bestGroup = generateRandomSubset();
      d = subsetEvaluator.evaluateSubset(this.m_bestGroup);
    } 
    if (this.m_verbose)
      System.out.println("Initial subset (" + Utils.doubleToString(Math.abs(d), 8, 5) + "): " + printSubset(this.m_bestGroup)); 
    if (this.m_hasClass) {
      j = this.m_numAttribs - 1;
    } else {
      j = this.m_numAttribs;
    } 
    this.m_iterations = (int)(this.m_searchSize * Math.pow(2.0D, j));
    for (int j = 0; j < this.m_iterations; j++) {
      BitSet bitSet = generateRandomSubset();
      if (this.m_onlyConsiderBetterAndSmaller) {
        int k = countFeatures(bitSet);
        if (k <= i) {
          double d1 = subsetEvaluator.evaluateSubset(bitSet);
          if (d1 >= d) {
            i = k;
            this.m_bestGroup = bitSet;
            d = d1;
            if (this.m_verbose) {
              System.out.print("New best subset (" + Utils.doubleToString(Math.abs(d), 8, 5) + "): " + printSubset(this.m_bestGroup) + " :");
              System.out.println(Utils.doubleToString(j / this.m_iterations * 100.0D, 5, 1) + "% done");
            } 
          } 
        } 
      } else {
        double d1 = subsetEvaluator.evaluateSubset(bitSet);
        if (d1 > d) {
          this.m_bestGroup = bitSet;
          d = d1;
          if (this.m_verbose) {
            System.out.print("New best subset (" + Utils.doubleToString(Math.abs(d), 8, 5) + "): " + printSubset(this.m_bestGroup) + " :");
            System.out.println(Utils.doubleToString(j / this.m_iterations * 100.0D, 5, 1) + "% done");
          } 
        } 
      } 
    } 
    this.m_bestMerit = d;
    return attributeList(this.m_bestGroup);
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
  
  private BitSet generateRandomSubset() {
    BitSet bitSet = new BitSet(this.m_numAttribs);
    for (byte b = 0; b < this.m_numAttribs; b++) {
      double d = this.m_random.nextDouble();
      if (d <= 0.5D && (!this.m_hasClass || b != this.m_classIndex))
        bitSet.set(b); 
    } 
    return bitSet;
  }
  
  private int countFeatures(BitSet paramBitSet) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_numAttribs; b2++) {
      if (paramBitSet.get(b2))
        b1++; 
    } 
    return b1;
  }
  
  private void resetOptions() {
    this.m_starting = null;
    this.m_startRange = new Range();
    this.m_searchSize = 0.25D;
    this.m_seed = 1;
    this.m_onlyConsiderBetterAndSmaller = false;
    this.m_verbose = false;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\RandomSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */