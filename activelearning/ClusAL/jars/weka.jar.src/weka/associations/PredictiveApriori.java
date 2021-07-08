package weka.associations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeSet;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class PredictiveApriori extends Associator implements OptionHandler {
  protected int m_premiseCount;
  
  protected int m_numRules;
  
  protected static final int m_numRandRules = 1000;
  
  protected static final int m_numIntervals = 100;
  
  protected FastVector m_Ls;
  
  protected FastVector m_hashtables;
  
  protected FastVector[] m_allTheRules;
  
  protected Instances m_instances;
  
  protected Hashtable m_priors;
  
  protected double[] m_midPoints;
  
  protected double m_expectation;
  
  protected TreeSet m_best;
  
  protected boolean m_bestChanged;
  
  protected int m_count;
  
  protected PriorEstimation m_priorEstimator;
  
  public String globalInfo() {
    return "Finds association rules sorted by predictive accuracy.";
  }
  
  public PredictiveApriori() {
    resetOptions();
  }
  
  public void resetOptions() {
    this.m_numRules = 105;
    this.m_premiseCount = 1;
    this.m_best = new TreeSet();
    this.m_bestChanged = false;
    this.m_expectation = 0.0D;
    this.m_count = 1;
  }
  
  public void buildAssociations(Instances paramInstances) throws Exception {
    int i = this.m_premiseCount;
    int j = this.m_numRules - 5;
    if (paramInstances.checkForStringAttributes())
      throw new Exception("Can't handle string attributes!"); 
    this.m_instances = paramInstances;
    this.m_instances.setClassIndex(this.m_instances.numAttributes() - 1);
    this.m_priorEstimator = new PriorEstimation(this.m_instances, 1000, 100, false);
    this.m_priors = this.m_priorEstimator.estimatePrior();
    this.m_midPoints = this.m_priorEstimator.getMidPoints();
    this.m_Ls = new FastVector();
    this.m_hashtables = new FastVector();
    byte b;
    for (b = 1; b < this.m_instances.numAttributes(); b++) {
      this.m_bestChanged = false;
      findLargeItemSets(b);
      findRulesQuickly();
      if (this.m_bestChanged) {
        i = this.m_premiseCount;
        while (RuleGeneration.expectation(this.m_premiseCount, this.m_premiseCount, this.m_midPoints, this.m_priors) <= this.m_expectation) {
          this.m_premiseCount++;
          if (this.m_premiseCount > this.m_instances.numInstances())
            break; 
        } 
      } 
      if (this.m_premiseCount > this.m_instances.numInstances()) {
        this.m_allTheRules = new FastVector[3];
        this.m_allTheRules[0] = new FastVector();
        this.m_allTheRules[1] = new FastVector();
        this.m_allTheRules[2] = new FastVector();
        byte b1 = 0;
        while (this.m_best.size() > 0 && j > 0) {
          this.m_allTheRules[0].insertElementAt(((RuleItem)this.m_best.last()).premise(), b1);
          this.m_allTheRules[1].insertElementAt(((RuleItem)this.m_best.last()).consequence(), b1);
          this.m_allTheRules[2].insertElementAt(new Double(((RuleItem)this.m_best.last()).accuracy()), b1);
          boolean bool = this.m_best.remove(this.m_best.last());
          b1++;
          j--;
        } 
        return;
      } 
      if (i != this.m_premiseCount && this.m_Ls.size() > 0) {
        FastVector fastVector = (FastVector)this.m_Ls.lastElement();
        this.m_Ls.removeElementAt(this.m_Ls.size() - 1);
        fastVector = ItemSet.deleteItemSets(fastVector, this.m_premiseCount, 2147483647);
        this.m_Ls.addElement(fastVector);
      } 
    } 
    this.m_allTheRules = new FastVector[3];
    this.m_allTheRules[0] = new FastVector();
    this.m_allTheRules[1] = new FastVector();
    this.m_allTheRules[2] = new FastVector();
    b = 0;
    while (this.m_best.size() > 0 && j > 0) {
      this.m_allTheRules[0].insertElementAt(((RuleItem)this.m_best.last()).premise(), b);
      this.m_allTheRules[1].insertElementAt(((RuleItem)this.m_best.last()).consequence(), b);
      this.m_allTheRules[2].insertElementAt(new Double(((RuleItem)this.m_best.last()).accuracy()), b);
      boolean bool = this.m_best.remove(this.m_best.last());
      b++;
      j--;
    } 
  }
  
  public Enumeration listOptions() {
    String str = "\tThe required number of rules. (default = " + (this.m_numRules - 5) + ")";
    FastVector fastVector = new FastVector(1);
    fastVector.addElement(new Option(str, "N", 1, "-N <required number of rules output>"));
    return fastVector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('N', paramArrayOfString);
    if (str.length() != 0) {
      this.m_numRules = Integer.parseInt(str) + 5;
    } else {
      this.m_numRules = Integer.MAX_VALUE;
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[10];
    byte b = 0;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + (this.m_numRules - 5);
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_allTheRules[0].size() == 0)
      return "\nNo large itemsets and rules found!\n"; 
    stringBuffer.append("\nPredictiveApriori\n===================\n\n");
    stringBuffer.append("\nBest rules found:\n\n");
    for (byte b = 0; b < this.m_allTheRules[0].size(); b++) {
      stringBuffer.append(Utils.doubleToString(b + 1.0D, (int)(Math.log(this.m_numRules) / Math.log(10.0D) + 1.0D), 0) + ". " + ((ItemSet)this.m_allTheRules[0].elementAt(b)).toString(this.m_instances) + " ==> " + ((ItemSet)this.m_allTheRules[1].elementAt(b)).toString(this.m_instances) + "    acc:(" + Utils.doubleToString(((Double)this.m_allTheRules[2].elementAt(b)).doubleValue(), 5) + ")");
      stringBuffer.append('\n');
    } 
    return stringBuffer.toString();
  }
  
  public String numRulesTipText() {
    return "Number of rules to find.";
  }
  
  public int getNumRules() {
    return this.m_numRules - 5;
  }
  
  public void setNumRules(int paramInt) {
    this.m_numRules = paramInt + 5;
  }
  
  private void findLargeItemSets(int paramInt) throws Exception {
    FastVector fastVector = new FastVector();
    int i = 0;
    if (paramInt == 1) {
      fastVector = ItemSet.singletons(this.m_instances);
      ItemSet.upDateCounters(fastVector, this.m_instances);
      fastVector = ItemSet.deleteItemSets(fastVector, this.m_premiseCount, 2147483647);
      if (fastVector.size() == 0)
        return; 
      this.m_Ls.addElement(fastVector);
    } 
    if (paramInt > 1) {
      if (this.m_Ls.size() > 0)
        fastVector = (FastVector)this.m_Ls.lastElement(); 
      this.m_Ls.removeAllElements();
      i = paramInt - 2;
      FastVector fastVector1 = fastVector;
      fastVector = ItemSet.mergeAllItemSets(fastVector1, i, this.m_instances.numInstances());
      Hashtable hashtable = ItemSet.getHashtable(fastVector1, fastVector1.size());
      this.m_hashtables.addElement(hashtable);
      fastVector = ItemSet.pruneItemSets(fastVector, hashtable);
      ItemSet.upDateCounters(fastVector, this.m_instances);
      fastVector = ItemSet.deleteItemSets(fastVector, this.m_premiseCount, 2147483647);
      if (fastVector.size() == 0)
        return; 
      this.m_Ls.addElement(fastVector);
    } 
  }
  
  private void findRulesQuickly() throws Exception {
    for (byte b = 0; b < this.m_Ls.size(); b++) {
      FastVector fastVector = (FastVector)this.m_Ls.elementAt(b);
      Enumeration enumeration = fastVector.elements();
      while (enumeration.hasMoreElements()) {
        RuleGeneration ruleGeneration = new RuleGeneration(enumeration.nextElement());
        this.m_best = ruleGeneration.generateRules(this.m_numRules, this.m_midPoints, this.m_priors, this.m_expectation, this.m_instances, this.m_best, this.m_count);
        this.m_count = ruleGeneration.m_count;
        if (!this.m_bestChanged && ruleGeneration.m_change)
          this.m_bestChanged = true; 
        if (this.m_best.size() > 0) {
          this.m_expectation = ((RuleItem)this.m_best.first()).accuracy();
          continue;
        } 
        this.m_expectation = 0.0D;
      } 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer();
    PredictiveApriori predictiveApriori = new PredictiveApriori();
    try {
      stringBuffer.append("\n\nPredictiveApriori options:\n\n");
      stringBuffer.append("-t <training file>\n");
      stringBuffer.append("\tThe name of the training file.\n");
      Enumeration enumeration = predictiveApriori.listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        stringBuffer.append(option.synopsis() + '\n');
        stringBuffer.append(option.description() + '\n');
      } 
      String str = Utils.getOption('t', paramArrayOfString);
      if (str.length() == 0)
        throw new Exception("No training file given!"); 
      predictiveApriori.setOptions(paramArrayOfString);
      BufferedReader bufferedReader = new BufferedReader(new FileReader(str));
      predictiveApriori.buildAssociations(new Instances(bufferedReader));
      System.out.println(predictiveApriori);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println("\n" + exception.getMessage() + stringBuffer);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\PredictiveApriori.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */