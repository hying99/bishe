package weka.attributeSelection;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.Utils;

public class Ranker extends ASSearch implements RankedOutputSearch, StartSetHandler, OptionHandler {
  private int[] m_starting;
  
  private Range m_startRange;
  
  private int[] m_attributeList;
  
  private double[] m_attributeMerit;
  
  private boolean m_hasClass;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private double m_threshold;
  
  private int m_numToSelect = -1;
  
  private int m_calculatedNumToSelect = -1;
  
  public String globalInfo() {
    return "Ranker : \n\nRanks attributes by their individual evaluations. Use in conjunction with attribute evaluators (ReliefF, GainRatio, Entropy etc).\n";
  }
  
  public Ranker() {
    resetOptions();
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
  
  public String thresholdTipText() {
    return "Set threshold by which attributes can be discarded. Default value results in no attributes being discarded. Use either this option or numToSelect to reduce the attribute set.";
  }
  
  public void setThreshold(double paramDouble) {
    this.m_threshold = paramDouble;
  }
  
  public double getThreshold() {
    return this.m_threshold;
  }
  
  public String generateRankingTipText() {
    return "A constant option. Ranker is only capable of generating  attribute rankings.";
  }
  
  public void setGenerateRanking(boolean paramBoolean) {}
  
  public boolean getGenerateRanking() {
    return true;
  }
  
  public String startSetTipText() {
    return "Specify a set of attributes to ignore.  When generating the ranking, Ranker will not evaluate the attributes  in this list. This is specified as a comma seperated list off attribute indexes starting at 1. It can include ranges. Eg. 1,2,5-9,17.";
  }
  
  public void setStartSet(String paramString) throws Exception {
    this.m_startRange.setRanges(paramString);
  }
  
  public String getStartSet() {
    return this.m_startRange.getRanges();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tSpecify a starting set of attributes.\n\tEg. 1,3,5-7.\t\nAny starting attributes specified are\t\nignored during the ranking.", "P", 1, "-P <start set>"));
    vector.addElement(new Option("\tSpecify a theshold by which attributes\tmay be discarded from the ranking.", "T", 1, "-T <threshold>"));
    vector.addElement(new Option("\tSpecify number of attributes to select", "N", 1, "-N <num to select>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0)
      setStartSet(str); 
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
    String[] arrayOfString = new String[6];
    byte b = 0;
    if (!getStartSet().equals("")) {
      arrayOfString[b++] = "-P";
      arrayOfString[b++] = "" + startSetToString();
    } 
    arrayOfString[b++] = "-T";
    arrayOfString[b++] = "" + getThreshold();
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + getNumToSelect();
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
  
  public int[] search(ASEvaluation paramASEvaluation, Instances paramInstances) throws Exception {
    if (!(paramASEvaluation instanceof AttributeEvaluator))
      throw new Exception(paramASEvaluation.getClass().getName() + " is not a" + "Attribute evaluator!"); 
    this.m_numAttribs = paramInstances.numAttributes();
    if (paramASEvaluation instanceof UnsupervisedAttributeEvaluator) {
      this.m_hasClass = false;
    } else {
      this.m_classIndex = paramInstances.classIndex();
      if (this.m_classIndex >= 0) {
        this.m_hasClass = true;
      } else {
        this.m_hasClass = false;
      } 
    } 
    if (paramASEvaluation instanceof AttributeTransformer) {
      paramInstances = ((AttributeTransformer)paramASEvaluation).transformedHeader();
      if (this.m_classIndex >= 0 && paramInstances.classIndex() >= 0) {
        this.m_classIndex = paramInstances.classIndex();
        this.m_hasClass = true;
      } 
    } 
    this.m_startRange.setUpper(this.m_numAttribs - 1);
    if (!getStartSet().equals(""))
      this.m_starting = this.m_startRange.getSelection(); 
    int i = 0;
    if (this.m_starting != null)
      i = this.m_starting.length; 
    if (this.m_starting != null && this.m_hasClass == true) {
      boolean bool = false;
      for (byte b = 0; b < i; b++) {
        if (this.m_starting[b] == this.m_classIndex) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        i++; 
    } else if (this.m_hasClass == true) {
      i++;
    } 
    this.m_attributeList = new int[this.m_numAttribs - i];
    this.m_attributeMerit = new double[this.m_numAttribs - i];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < this.m_numAttribs) {
      if (!inStarting(b1))
        this.m_attributeList[b2++] = b1; 
      b1++;
    } 
    AttributeEvaluator attributeEvaluator = (AttributeEvaluator)paramASEvaluation;
    for (b1 = 0; b1 < this.m_attributeList.length; b1++)
      this.m_attributeMerit[b1] = attributeEvaluator.evaluateAttribute(this.m_attributeList[b1]); 
    double[][] arrayOfDouble = rankedAttributes();
    int[] arrayOfInt = new int[this.m_attributeList.length];
    for (b1 = 0; b1 < this.m_attributeList.length; b1++)
      arrayOfInt[b1] = (int)arrayOfDouble[b1][0]; 
    return arrayOfInt;
  }
  
  public double[][] rankedAttributes() throws Exception {
    if (this.m_attributeList == null || this.m_attributeMerit == null)
      throw new Exception("Search must be performed before a ranked attribute list can be obtained"); 
    int[] arrayOfInt = Utils.sort(this.m_attributeMerit);
    double[][] arrayOfDouble = new double[arrayOfInt.length][2];
    int i = arrayOfInt.length - 1;
    byte b = 0;
    while (i >= 0) {
      arrayOfDouble[b++][0] = arrayOfInt[i];
      i--;
    } 
    for (i = 0; i < arrayOfDouble.length; i++) {
      int j = (int)arrayOfDouble[i][0];
      arrayOfDouble[i][0] = this.m_attributeList[j];
      arrayOfDouble[i][1] = this.m_attributeMerit[j];
    } 
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
  
  private void determineThreshFromNumToSelect(double[][] paramArrayOfdouble) throws Exception {
    if (this.m_numToSelect > paramArrayOfdouble.length)
      throw new Exception("More attributes requested than exist in the data"); 
    if (this.m_numToSelect == paramArrayOfdouble.length)
      return; 
    this.m_threshold = (paramArrayOfdouble[this.m_numToSelect - 1][1] + paramArrayOfdouble[this.m_numToSelect][1]) / 2.0D;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\tAttribute ranking.\n");
    if (this.m_starting != null) {
      stringBuffer.append("\tIgnored attributes: ");
      stringBuffer.append(startSetToString());
      stringBuffer.append("\n");
    } 
    if (this.m_threshold != -1.7976931348623157E308D)
      stringBuffer.append("\tThreshold for discarding attributes: " + Utils.doubleToString(this.m_threshold, 8, 4) + "\n"); 
    return stringBuffer.toString();
  }
  
  protected void resetOptions() {
    this.m_starting = null;
    this.m_startRange = new Range();
    this.m_attributeList = null;
    this.m_attributeMerit = null;
    this.m_threshold = -1.7976931348623157E308D;
  }
  
  private boolean inStarting(int paramInt) {
    if (this.m_hasClass == true && paramInt == this.m_classIndex)
      return true; 
    if (this.m_starting == null)
      return false; 
    for (byte b = 0; b < this.m_starting.length; b++) {
      if (this.m_starting[b] == paramInt)
        return true; 
    } 
    return false;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\Ranker.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */