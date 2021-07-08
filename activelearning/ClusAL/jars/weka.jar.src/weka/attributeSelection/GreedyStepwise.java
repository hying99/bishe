package weka.attributeSelection;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.Utils;

public class GreedyStepwise extends ASSearch implements RankedOutputSearch, StartSetHandler, OptionHandler {
  protected boolean m_hasClass;
  
  protected int m_classIndex;
  
  protected int m_numAttribs;
  
  protected boolean m_rankingRequested;
  
  protected boolean m_doRank;
  
  protected boolean m_doneRanking = false;
  
  protected double m_threshold = -1.7976931348623157E308D;
  
  protected int m_numToSelect = -1;
  
  protected int m_calculatedNumToSelect;
  
  protected double m_bestMerit;
  
  protected double[][] m_rankedAtts;
  
  protected int m_rankedSoFar;
  
  protected BitSet m_best_group;
  
  protected ASEvaluation m_ASEval;
  
  protected Instances m_Instances;
  
  protected Range m_startRange = new Range();
  
  protected int[] m_starting = null;
  
  protected boolean m_backward = false;
  
  public String globalInfo() {
    return "GreedyStepwise :\n\nPerforms a greedy forward or backward search through the space of attribute subsets. May start with no/all attributes or from an arbitrary point in the space. Stops when the addition/deletion of any remaining attributes results in a decrease in evaluation. Can also produce a ranked list of attributes by traversing the space from one side to the other and recording the order that attributes are selected.\n";
  }
  
  public GreedyStepwise() {
    resetOptions();
  }
  
  public String searchBackwardsTipText() {
    return "Search backwards rather than forwards.";
  }
  
  public void setSearchBackwards(boolean paramBoolean) {
    this.m_backward = paramBoolean;
    if (this.m_backward)
      setGenerateRanking(false); 
  }
  
  public boolean getSearchBackwards() {
    return this.m_backward;
  }
  
  public String thresholdTipText() {
    return "Set threshold by which attributes can be discarded. Default value results in no attributes being discarded. Use in conjunction with generateRanking";
  }
  
  public void setThreshold(double paramDouble) {
    this.m_threshold = paramDouble;
  }
  
  public double getThreshold() {
    return this.m_threshold;
  }
  
  public String numToSelectTipText() {
    return "Specify the number of attributes to retain. The default value (-1) indicates that all attributes are to be retained. Use either this option or a threshold to reduce the attribute set.";
  }
  
  public void setNumToSelect(int paramInt) {
    this.m_numToSelect = paramInt;
  }
  
  public int getNumToSelect() {
    return this.m_numToSelect;
  }
  
  public int getCalculatedNumToSelect() {
    if (this.m_numToSelect >= 0)
      this.m_calculatedNumToSelect = this.m_numToSelect; 
    return this.m_calculatedNumToSelect;
  }
  
  public String generateRankingTipText() {
    return "Set to true if a ranked list is required.";
  }
  
  public void setGenerateRanking(boolean paramBoolean) {
    this.m_rankingRequested = paramBoolean;
  }
  
  public boolean getGenerateRanking() {
    return this.m_rankingRequested;
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
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tUse a backward search instead  of a\n\tforward one.", "-B", 0, "-B"));
    vector.addElement(new Option("\tSpecify a starting set of attributes.\n\tEg. 1,3,5-7.", "P", 1, "-P <start set>"));
    vector.addElement(new Option("\tProduce a ranked list of attributes.", "R", 0, "-R"));
    vector.addElement(new Option("\tSpecify a theshold by which attributes\n\tmay be discarded from the ranking.\n\tUse in conjuction with -R", "T", 1, "-T <threshold>"));
    vector.addElement(new Option("\tSpecify number of attributes to select", "N", 1, "-N <num to select>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    setSearchBackwards(Utils.getFlag('B', paramArrayOfString));
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0)
      setStartSet(str); 
    setGenerateRanking(Utils.getFlag('R', paramArrayOfString));
    str = Utils.getOption('T', paramArrayOfString);
    if (str.length() != 0) {
      Double double_ = Double.valueOf(str);
      setThreshold(double_.doubleValue());
    } 
    str = Utils.getOption('N', paramArrayOfString);
    if (str.length() != 0)
      setNumToSelect(Integer.parseInt(str)); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[8];
    byte b = 0;
    if (getSearchBackwards())
      arrayOfString[b++] = "-B"; 
    if (!getStartSet().equals("")) {
      arrayOfString[b++] = "-P";
      arrayOfString[b++] = "" + startSetToString();
    } 
    if (getGenerateRanking())
      arrayOfString[b++] = "-R"; 
    arrayOfString[b++] = "-T";
    arrayOfString[b++] = "" + getThreshold();
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + getNumToSelect();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  protected String startSetToString() {
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
    stringBuffer.append("\tGreedy Stepwise (" + (this.m_backward ? "backwards)" : "forwards)") + ".\n\tStart set: ");
    if (this.m_starting == null) {
      if (this.m_backward) {
        stringBuffer.append("all attributes\n");
      } else {
        stringBuffer.append("no attributes\n");
      } 
    } else {
      stringBuffer.append(startSetToString() + "\n");
    } 
    if (!this.m_doneRanking)
      stringBuffer.append("\tMerit of best subset found: " + Utils.doubleToString(Math.abs(this.m_bestMerit), 8, 3) + "\n"); 
    if (this.m_threshold != -1.7976931348623157E308D && this.m_doneRanking)
      stringBuffer.append("\tThreshold for discarding attributes: " + Utils.doubleToString(this.m_threshold, 8, 4) + "\n"); 
    return stringBuffer.toString();
  }
  
  public int[] search(ASEvaluation paramASEvaluation, Instances paramInstances) throws Exception {
    double d = -1.7976931348623157E308D;
    byte b = 0;
    if (paramInstances != null) {
      resetOptions();
      this.m_Instances = paramInstances;
    } 
    this.m_ASEval = paramASEvaluation;
    this.m_numAttribs = this.m_Instances.numAttributes();
    if (this.m_best_group == null)
      this.m_best_group = new BitSet(this.m_numAttribs); 
    if (!(this.m_ASEval instanceof SubsetEvaluator))
      throw new Exception(this.m_ASEval.getClass().getName() + " is not a " + "Subset evaluator!"); 
    this.m_startRange.setUpper(this.m_numAttribs - 1);
    if (!getStartSet().equals(""))
      this.m_starting = this.m_startRange.getSelection(); 
    if (this.m_ASEval instanceof UnsupervisedSubsetEvaluator) {
      this.m_hasClass = false;
      this.m_classIndex = -1;
    } else {
      this.m_hasClass = true;
      this.m_classIndex = this.m_Instances.classIndex();
    } 
    SubsetEvaluator subsetEvaluator = (SubsetEvaluator)this.m_ASEval;
    if (this.m_rankedAtts == null) {
      this.m_rankedAtts = new double[this.m_numAttribs][2];
      this.m_rankedSoFar = 0;
    } 
    if (this.m_starting != null) {
      for (byte b1 = 0; b1 < this.m_starting.length; b1++) {
        if (this.m_starting[b1] != this.m_classIndex)
          this.m_best_group.set(this.m_starting[b1]); 
      } 
    } else if (this.m_backward) {
      byte b1 = 0;
      boolean bool = false;
      while (b1 < this.m_numAttribs) {
        if (b1 != this.m_classIndex)
          this.m_best_group.set(b1); 
        b1++;
      } 
    } 
    d = subsetEvaluator.evaluateSubset(this.m_best_group);
    boolean bool1 = false;
    boolean bool2 = false;
    while (!bool1) {
      BitSet bitSet = (BitSet)this.m_best_group.clone();
      double d1 = d;
      if (this.m_doRank)
        d1 = -1.7976931348623157E308D; 
      bool1 = true;
      bool2 = false;
      for (byte b1 = 0; b1 < this.m_numAttribs; b1++) {
        boolean bool;
        if (this.m_backward) {
          bool = (b1 != this.m_classIndex && bitSet.get(b1)) ? true : false;
        } else {
          bool = (b1 != this.m_classIndex && !bitSet.get(b1)) ? true : false;
        } 
        if (bool) {
          if (this.m_backward) {
            bitSet.clear(b1);
          } else {
            bitSet.set(b1);
          } 
          double d2 = subsetEvaluator.evaluateSubset(bitSet);
          if (this.m_backward) {
            bool = (d2 >= d1) ? true : false;
          } else {
            bool = (d2 > d1) ? true : false;
          } 
          if (bool) {
            d1 = d2;
            b = b1;
            bool2 = true;
            bool1 = false;
          } 
          if (this.m_backward) {
            bitSet.set(b1);
          } else {
            bitSet.clear(b1);
          } 
          if (this.m_doRank)
            bool1 = false; 
        } 
      } 
      if (bool2) {
        if (this.m_backward) {
          this.m_best_group.clear(b);
        } else {
          this.m_best_group.set(b);
        } 
        d = d1;
        this.m_rankedAtts[this.m_rankedSoFar][0] = b;
        this.m_rankedAtts[this.m_rankedSoFar][1] = d;
        this.m_rankedSoFar++;
      } 
    } 
    this.m_bestMerit = d;
    return attributeList(this.m_best_group);
  }
  
  public double[][] rankedAttributes() throws Exception {
    if (this.m_rankedAtts == null || this.m_rankedSoFar == -1)
      throw new Exception("Search must be performed before attributes can be ranked."); 
    this.m_doRank = true;
    search(this.m_ASEval, null);
    double[][] arrayOfDouble = new double[this.m_rankedSoFar][2];
    for (byte b = 0; b < this.m_rankedSoFar; b++) {
      arrayOfDouble[b][0] = this.m_rankedAtts[b][0];
      arrayOfDouble[b][1] = this.m_rankedAtts[b][1];
    } 
    resetOptions();
    this.m_doneRanking = true;
    if (this.m_numToSelect > arrayOfDouble.length)
      throw new Exception("More attributes requested than exist in the data"); 
    if (this.m_numToSelect <= 0)
      if (this.m_threshold == -1.7976931348623157E308D) {
        this.m_calculatedNumToSelect = arrayOfDouble.length;
      } else {
        determineNumToSelectFromThreshold(arrayOfDouble);
      }  
    return arrayOfDouble;
  }
  
  private void determineNumToSelectFromThreshold(double[][] paramArrayOfdouble) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfdouble.length; b2++) {
      if (paramArrayOfdouble[b2][1] > this.m_threshold)
        b1++; 
    } 
    this.m_calculatedNumToSelect = b1;
  }
  
  protected int[] attributeList(BitSet paramBitSet) {
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
  
  protected void resetOptions() {
    this.m_doRank = false;
    this.m_best_group = null;
    this.m_ASEval = null;
    this.m_Instances = null;
    this.m_rankedSoFar = -1;
    this.m_rankedAtts = (double[][])null;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\GreedyStepwise.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */