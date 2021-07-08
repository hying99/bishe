package weka.classifiers.rules;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.part.MakeDecList;
import weka.classifiers.trees.j48.BinC45ModelSelection;
import weka.classifiers.trees.j48.C45ModelSelection;
import weka.classifiers.trees.j48.ModelSelection;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Summarizable;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class PART extends Classifier implements OptionHandler, WeightedInstancesHandler, Summarizable, AdditionalMeasureProducer {
  private MakeDecList m_root;
  
  private float m_CF = 0.25F;
  
  private int m_minNumObj = 2;
  
  private boolean m_reducedErrorPruning = false;
  
  private int m_numFolds = 3;
  
  private boolean m_binarySplits = false;
  
  private boolean m_unpruned = false;
  
  private int m_Seed = 1;
  
  public String globalInfo() {
    return "Class for generating a PART decision list. Uses separate-and-conquer. Builds a partial C4.5 decision tree in each iteration and makes the \"best\" leaf into a rule. For more information, see:\n\nEibe Frank and Ian H. Witten (1998). \"Generating Accurate Rule Sets Without Global Optimization.\"In Shavlik, J., ed., Machine Learning: Proceedings of the Fifteenth International Conference, Morgan Kaufmann Publishers.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    C45ModelSelection c45ModelSelection;
    if (this.m_binarySplits) {
      BinC45ModelSelection binC45ModelSelection = new BinC45ModelSelection(this.m_minNumObj, paramInstances);
    } else {
      c45ModelSelection = new C45ModelSelection(this.m_minNumObj, paramInstances);
    } 
    if (this.m_unpruned) {
      this.m_root = new MakeDecList((ModelSelection)c45ModelSelection, this.m_minNumObj);
    } else if (this.m_reducedErrorPruning) {
      this.m_root = new MakeDecList((ModelSelection)c45ModelSelection, this.m_numFolds, this.m_minNumObj, this.m_Seed);
    } else {
      this.m_root = new MakeDecList((ModelSelection)c45ModelSelection, this.m_CF, this.m_minNumObj);
    } 
    this.m_root.buildClassifier(paramInstances);
    if (this.m_binarySplits) {
      ((BinC45ModelSelection)c45ModelSelection).cleanup();
    } else {
      c45ModelSelection.cleanup();
    } 
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    return this.m_root.classifyInstance(paramInstance);
  }
  
  public final double[] distributionForInstance(Instance paramInstance) throws Exception {
    return this.m_root.distributionForInstance(paramInstance);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(7);
    vector.addElement(new Option("\tSet confidence threshold for pruning.\n\t(default 0.25)", "C", 1, "-C <pruning confidence>"));
    vector.addElement(new Option("\tSet minimum number of objects per leaf.\n\t(default 2)", "M", 1, "-M <minimum number of objects>"));
    vector.addElement(new Option("\tUse reduced error pruning.", "R", 0, "-R"));
    vector.addElement(new Option("\tSet number of folds for reduced error\n\tpruning. One fold is used as pruning set.\n\t(default 3)", "N", 1, "-N <number of folds>"));
    vector.addElement(new Option("\tUse binary splits only.", "B", 0, "-B"));
    vector.addElement(new Option("\tGenerate unpruned decision list.", "U", 0, "-U"));
    vector.addElement(new Option("\tSeed for random data shuffling (default 1).", "Q", 1, "-Q <seed>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    this.m_unpruned = Utils.getFlag('U', paramArrayOfString);
    this.m_reducedErrorPruning = Utils.getFlag('R', paramArrayOfString);
    this.m_binarySplits = Utils.getFlag('B', paramArrayOfString);
    String str1 = Utils.getOption('C', paramArrayOfString);
    if (str1.length() != 0) {
      if (this.m_reducedErrorPruning)
        throw new Exception("Setting CF doesn't make sense for reduced error pruning."); 
      this.m_CF = (new Float(str1)).floatValue();
      if (this.m_CF <= 0.0F || this.m_CF >= 1.0F)
        throw new Exception("CF has to be greater than zero and smaller than one!"); 
    } else {
      this.m_CF = 0.25F;
    } 
    String str2 = Utils.getOption('N', paramArrayOfString);
    if (str2.length() != 0) {
      if (!this.m_reducedErrorPruning)
        throw new Exception("Setting the number of folds does only make sense for reduced error pruning."); 
      this.m_numFolds = Integer.parseInt(str2);
    } else {
      this.m_numFolds = 3;
    } 
    String str3 = Utils.getOption('M', paramArrayOfString);
    if (str3.length() != 0) {
      this.m_minNumObj = Integer.parseInt(str3);
    } else {
      this.m_minNumObj = 2;
    } 
    String str4 = Utils.getOption('Q', paramArrayOfString);
    if (str4.length() != 0) {
      this.m_Seed = Integer.parseInt(str4);
    } else {
      this.m_Seed = 1;
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[11];
    byte b = 0;
    if (this.m_unpruned)
      arrayOfString[b++] = "-U"; 
    if (this.m_reducedErrorPruning)
      arrayOfString[b++] = "-R"; 
    if (this.m_binarySplits)
      arrayOfString[b++] = "-B"; 
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + this.m_minNumObj;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + this.m_CF;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + this.m_numFolds;
    arrayOfString[b++] = "-Q";
    arrayOfString[b++] = "" + this.m_Seed;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    return (this.m_root == null) ? "No classifier built" : ("PART decision list\n------------------\n\n" + this.m_root.toString());
  }
  
  public String toSummaryString() {
    return "Number of rules: " + this.m_root.numRules() + "\n";
  }
  
  public double measureNumRules() {
    return this.m_root.numRules();
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(1);
    vector.addElement("measureNumRules");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.compareToIgnoreCase("measureNumRules") == 0)
      return measureNumRules(); 
    throw new IllegalArgumentException(paramString + " not supported (PART)");
  }
  
  public String confidenceFactorTipText() {
    return "The confidence factor used for pruning (smaller values incur more pruning).";
  }
  
  public float getConfidenceFactor() {
    return this.m_CF;
  }
  
  public void setConfidenceFactor(float paramFloat) {
    this.m_CF = paramFloat;
  }
  
  public String minNumObjTipText() {
    return "The minimum number of instances per rule.";
  }
  
  public int getMinNumObj() {
    return this.m_minNumObj;
  }
  
  public void setMinNumObj(int paramInt) {
    this.m_minNumObj = paramInt;
  }
  
  public String reducedErrorPruningTipText() {
    return "Whether reduced-error pruning is used instead of C.4.5 pruning.";
  }
  
  public boolean getReducedErrorPruning() {
    return this.m_reducedErrorPruning;
  }
  
  public void setReducedErrorPruning(boolean paramBoolean) {
    this.m_reducedErrorPruning = paramBoolean;
  }
  
  public String unprunedTipText() {
    return "Whether pruning is performed.";
  }
  
  public boolean getUnpruned() {
    return this.m_unpruned;
  }
  
  public void setUnpruned(boolean paramBoolean) {
    this.m_unpruned = paramBoolean;
  }
  
  public String numFoldsTipText() {
    return "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the rules.";
  }
  
  public int getNumFolds() {
    return this.m_numFolds;
  }
  
  public void setNumFolds(int paramInt) {
    this.m_numFolds = paramInt;
  }
  
  public String seedTipText() {
    return "The seed used for randomizing the data when reduced-error pruning is used.";
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public String binarySplitsTipText() {
    return "Whether to use binary splits on nominal attributes when building the partial trees.";
  }
  
  public boolean getBinarySplits() {
    return this.m_binarySplits;
  }
  
  public void setBinarySplits(boolean paramBoolean) {
    this.m_binarySplits = paramBoolean;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new PART(), paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\PART.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */