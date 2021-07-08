package weka.attributeSelection;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class RankSearch extends ASSearch implements OptionHandler {
  private boolean m_hasClass;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private BitSet m_best_group;
  
  private ASEvaluation m_ASEval;
  
  private ASEvaluation m_SubsetEval;
  
  private Instances m_Instances;
  
  private double m_bestMerit;
  
  private int[] m_Ranking;
  
  public String globalInfo() {
    return "RankSearch : \n\nUses an attribute/subset evaluator to rank all attributes. If a subset evaluator is specified, then a forward selection search is used to generate a ranked list. From the ranked list of attributes, subsets of increasing size are evaluated, ie. The best attribute, the best attribute plus the next best attribute, etc.... The best attribute set is reported. RankSearch is linear in the number of attributes if a simple attribute evaluator is used such as GainRatioAttributeEval.\n";
  }
  
  public RankSearch() {
    resetOptions();
  }
  
  public String attributeEvaluatorTipText() {
    return "Attribute evaluator to use for generating a ranking.";
  }
  
  public void setAttributeEvaluator(ASEvaluation paramASEvaluation) {
    this.m_ASEval = paramASEvaluation;
  }
  
  public ASEvaluation getAttributeEvaluator() {
    return this.m_ASEval;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tclass name of attribute evaluator to\n\tuse for ranking. Place any\n\tevaluator options LAST on the\n\tcommand line following a \"--\".\n\teg. -A weka.attributeSelection.GainRatioAttributeEval ... -- -M", "A", 1, "-A <attribute evaluator>"));
    if (this.m_ASEval != null && this.m_ASEval instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific toevaluator " + this.m_ASEval.getClass().getName() + ":"));
      Enumeration enumeration = ((OptionHandler)this.m_ASEval).listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('A', paramArrayOfString);
    if (str.length() == 0)
      throw new Exception("An attribute evaluator  must be specified with-A option"); 
    setAttributeEvaluator(ASEvaluation.forName(str, Utils.partitionOptions(paramArrayOfString)));
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_ASEval != null && this.m_ASEval instanceof OptionHandler)
      arrayOfString1 = ((OptionHandler)this.m_ASEval).getOptions(); 
    String[] arrayOfString2 = new String[4 + arrayOfString1.length];
    int i = 0;
    if (getAttributeEvaluator() != null) {
      arrayOfString2[i++] = "-A";
      arrayOfString2[i++] = getAttributeEvaluator().getClass().getName();
    } 
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  protected void resetOptions() {
    this.m_ASEval = new GainRatioAttributeEval();
    this.m_Ranking = null;
  }
  
  public int[] search(ASEvaluation paramASEvaluation, Instances paramInstances) throws Exception {
    double d = -1.7976931348623157E308D;
    BitSet bitSet = null;
    if (!(paramASEvaluation instanceof SubsetEvaluator))
      throw new Exception(paramASEvaluation.getClass().getName() + " is not a " + "Subset evaluator!"); 
    this.m_SubsetEval = paramASEvaluation;
    this.m_Instances = paramInstances;
    this.m_numAttribs = this.m_Instances.numAttributes();
    if (this.m_ASEval instanceof UnsupervisedAttributeEvaluator || this.m_ASEval instanceof UnsupervisedSubsetEvaluator) {
      this.m_hasClass = false;
    } else {
      this.m_hasClass = true;
      this.m_classIndex = this.m_Instances.classIndex();
    } 
    if (this.m_ASEval instanceof AttributeEvaluator) {
      Ranker ranker = new Ranker();
      ((AttributeEvaluator)this.m_ASEval).buildEvaluator(this.m_Instances);
      if (this.m_ASEval instanceof AttributeTransformer) {
        this.m_Instances = ((AttributeTransformer)this.m_ASEval).transformedData();
        ((SubsetEvaluator)this.m_SubsetEval).buildEvaluator(this.m_Instances);
      } 
      this.m_Ranking = ranker.search(this.m_ASEval, this.m_Instances);
    } else {
      GreedyStepwise greedyStepwise = new GreedyStepwise();
      greedyStepwise.setGenerateRanking(true);
      ((SubsetEvaluator)this.m_ASEval).buildEvaluator(this.m_Instances);
      greedyStepwise.search(this.m_ASEval, this.m_Instances);
      double[][] arrayOfDouble = greedyStepwise.rankedAttributes();
      this.m_Ranking = new int[arrayOfDouble.length];
      for (byte b1 = 0; b1 < arrayOfDouble.length; b1++)
        this.m_Ranking[b1] = (int)arrayOfDouble[b1][0]; 
    } 
    for (byte b = 0; b < this.m_Ranking.length; b++) {
      BitSet bitSet1 = new BitSet(this.m_numAttribs);
      for (byte b1 = 0; b1 <= b; b1++)
        bitSet1.set(this.m_Ranking[b1]); 
      double d1 = ((SubsetEvaluator)this.m_SubsetEval).evaluateSubset(bitSet1);
      if (d1 > d) {
        d = d1;
        bitSet = bitSet1;
      } 
    } 
    this.m_bestMerit = d;
    return attributeList(bitSet);
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
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\tRankSearch :\n");
    stringBuffer.append("\tAttribute evaluator : " + getAttributeEvaluator().getClass().getName() + " ");
    if (this.m_ASEval instanceof OptionHandler) {
      String[] arrayOfString = new String[0];
      arrayOfString = ((OptionHandler)this.m_ASEval).getOptions();
      for (byte b = 0; b < arrayOfString.length; b++)
        stringBuffer.append(arrayOfString[b] + ' '); 
    } 
    stringBuffer.append("\n");
    stringBuffer.append("\tAttribute ranking : \n");
    int i = (int)(Math.log(this.m_Ranking.length) / Math.log(10.0D) + 1.0D);
    int j;
    for (j = 0; j < this.m_Ranking.length; j++)
      stringBuffer.append("\t " + Utils.doubleToString((this.m_Ranking[j] + 1), i, 0) + " " + this.m_Instances.attribute(this.m_Ranking[j]).name() + '\n'); 
    stringBuffer.append("\tMerit of best subset found : ");
    j = 3;
    double d = this.m_bestMerit - (int)this.m_bestMerit;
    if (Math.abs(this.m_bestMerit) > 0.0D)
      j = (int)Math.abs(Math.log(Math.abs(this.m_bestMerit)) / Math.log(10.0D)) + 2; 
    if (Math.abs(d) > 0.0D) {
      d = Math.abs(Math.log(Math.abs(d)) / Math.log(10.0D)) + 3.0D;
    } else {
      d = 2.0D;
    } 
    stringBuffer.append(Utils.doubleToString(Math.abs(this.m_bestMerit), j + (int)d, (int)d) + "\n");
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\RankSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */