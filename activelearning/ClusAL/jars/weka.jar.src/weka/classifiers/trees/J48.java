package weka.classifiers.trees;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.Sourcable;
import weka.classifiers.trees.j48.BinC45ModelSelection;
import weka.classifiers.trees.j48.C45ModelSelection;
import weka.classifiers.trees.j48.C45PruneableClassifierTree;
import weka.classifiers.trees.j48.ClassifierTree;
import weka.classifiers.trees.j48.ModelSelection;
import weka.classifiers.trees.j48.PruneableClassifierTree;
import weka.core.AdditionalMeasureProducer;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Matchable;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Summarizable;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class J48 extends Classifier implements OptionHandler, Drawable, Matchable, Sourcable, WeightedInstancesHandler, Summarizable, AdditionalMeasureProducer {
  static final long serialVersionUID = -217733168393644444L;
  
  private ClassifierTree m_root;
  
  private boolean m_unpruned = false;
  
  private float m_CF = 0.25F;
  
  private int m_minNumObj = 2;
  
  private boolean m_useLaplace = false;
  
  private boolean m_reducedErrorPruning = false;
  
  private int m_numFolds = 3;
  
  private boolean m_binarySplits = false;
  
  private boolean m_subtreeRaising = true;
  
  private boolean m_noCleanup = false;
  
  private int m_Seed = 1;
  
  public String globalInfo() {
    return "Class for generating a pruned or unpruned C4.5 decision tree. For more information, see\n\nRoss Quinlan (1993). \"C4.5: Programs for Machine Learning\", Morgan Kaufmann Publishers, San Mateo, CA.\n\n";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    C45ModelSelection c45ModelSelection;
    if (this.m_binarySplits) {
      BinC45ModelSelection binC45ModelSelection = new BinC45ModelSelection(this.m_minNumObj, paramInstances);
    } else {
      c45ModelSelection = new C45ModelSelection(this.m_minNumObj, paramInstances);
    } 
    if (!this.m_reducedErrorPruning) {
      this.m_root = (ClassifierTree)new C45PruneableClassifierTree((ModelSelection)c45ModelSelection, !this.m_unpruned, this.m_CF, this.m_subtreeRaising, !this.m_noCleanup);
    } else {
      this.m_root = (ClassifierTree)new PruneableClassifierTree((ModelSelection)c45ModelSelection, !this.m_unpruned, this.m_numFolds, !this.m_noCleanup, this.m_Seed);
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
    return this.m_root.distributionForInstance(paramInstance, this.m_useLaplace);
  }
  
  public int graphType() {
    return 1;
  }
  
  public String graph() throws Exception {
    return this.m_root.graph();
  }
  
  public String prefix() throws Exception {
    return this.m_root.prefix();
  }
  
  public String toSource(String paramString) throws Exception {
    StringBuffer[] arrayOfStringBuffer = this.m_root.toSource(paramString);
    return "class " + paramString + " {\n\n" + "  public static double classify(Object [] i)\n" + "    throws Exception {\n\n" + "    double p = Double.NaN;\n" + arrayOfStringBuffer[0] + "    return p;\n" + "  }\n" + arrayOfStringBuffer[1] + "}\n";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(9);
    vector.addElement(new Option("\tUse unpruned tree.", "U", 0, "-U"));
    vector.addElement(new Option("\tSet confidence threshold for pruning.\n\t(default 0.25)", "C", 1, "-C <pruning confidence>"));
    vector.addElement(new Option("\tSet minimum number of instances per leaf.\n\t(default 2)", "M", 1, "-M <minimum number of instances>"));
    vector.addElement(new Option("\tUse reduced error pruning.", "R", 0, "-R"));
    vector.addElement(new Option("\tSet number of folds for reduced error\n\tpruning. One fold is used as pruning set.\n\t(default 3)", "N", 1, "-N <number of folds>"));
    vector.addElement(new Option("\tUse binary splits only.", "B", 0, "-B"));
    vector.addElement(new Option("\tDon't perform subtree raising.", "S", 0, "-S"));
    vector.addElement(new Option("\tDo not clean up after the tree has been built.", "L", 0, "-L"));
    vector.addElement(new Option("\tLaplace smoothing for predicted probabilities.", "A", 0, "-A"));
    vector.addElement(new Option("\tSeed for random data shuffling (default 1).", "Q", 1, "-Q <seed>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('M', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_minNumObj = Integer.parseInt(str1);
    } else {
      this.m_minNumObj = 2;
    } 
    this.m_binarySplits = Utils.getFlag('B', paramArrayOfString);
    this.m_useLaplace = Utils.getFlag('A', paramArrayOfString);
    this.m_unpruned = Utils.getFlag('U', paramArrayOfString);
    this.m_subtreeRaising = !Utils.getFlag('S', paramArrayOfString);
    this.m_noCleanup = Utils.getFlag('L', paramArrayOfString);
    if (this.m_unpruned && !this.m_subtreeRaising)
      throw new Exception("Subtree raising doesn't need to be unset for unpruned tree!"); 
    this.m_reducedErrorPruning = Utils.getFlag('R', paramArrayOfString);
    if (this.m_unpruned && this.m_reducedErrorPruning)
      throw new Exception("Unpruned tree and reduced error pruning can't be selected simultaneously!"); 
    String str2 = Utils.getOption('C', paramArrayOfString);
    if (str2.length() != 0) {
      if (this.m_reducedErrorPruning)
        throw new Exception("Setting the confidence doesn't make sense for reduced error pruning."); 
      if (this.m_unpruned)
        throw new Exception("Doesn't make sense to change confidence for unpruned tree!"); 
      this.m_CF = (new Float(str2)).floatValue();
      if (this.m_CF <= 0.0F || this.m_CF >= 1.0F)
        throw new Exception("Confidence has to be greater than zero and smaller than one!"); 
    } else {
      this.m_CF = 0.25F;
    } 
    String str3 = Utils.getOption('N', paramArrayOfString);
    if (str3.length() != 0) {
      if (!this.m_reducedErrorPruning)
        throw new Exception("Setting the number of folds doesn't make sense if reduced error pruning is not selected."); 
      this.m_numFolds = Integer.parseInt(str3);
    } else {
      this.m_numFolds = 3;
    } 
    String str4 = Utils.getOption('Q', paramArrayOfString);
    if (str4.length() != 0) {
      this.m_Seed = Integer.parseInt(str4);
    } else {
      this.m_Seed = 1;
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[14];
    byte b = 0;
    if (this.m_noCleanup)
      arrayOfString[b++] = "-L"; 
    if (this.m_unpruned) {
      arrayOfString[b++] = "-U";
    } else {
      if (!this.m_subtreeRaising)
        arrayOfString[b++] = "-S"; 
      if (this.m_reducedErrorPruning) {
        arrayOfString[b++] = "-R";
        arrayOfString[b++] = "-N";
        arrayOfString[b++] = "" + this.m_numFolds;
        arrayOfString[b++] = "-Q";
        arrayOfString[b++] = "" + this.m_Seed;
      } else {
        arrayOfString[b++] = "-C";
        arrayOfString[b++] = "" + this.m_CF;
      } 
    } 
    if (this.m_binarySplits)
      arrayOfString[b++] = "-B"; 
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + this.m_minNumObj;
    if (this.m_useLaplace)
      arrayOfString[b++] = "-A"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
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
  
  public String useLaplaceTipText() {
    return "Whether counts at leaves are smoothed based on Laplace.";
  }
  
  public boolean getUseLaplace() {
    return this.m_useLaplace;
  }
  
  public void setUseLaplace(boolean paramBoolean) {
    this.m_useLaplace = paramBoolean;
  }
  
  public String toString() {
    return (this.m_root == null) ? "No classifier built" : (this.m_unpruned ? ("J48 unpruned tree\n------------------\n" + this.m_root.toString()) : ("J48 pruned tree\n------------------\n" + this.m_root.toString()));
  }
  
  public String toSummaryString() {
    return "Number of leaves: " + this.m_root.numLeaves() + "\n" + "Size of the tree: " + this.m_root.numNodes() + "\n";
  }
  
  public double measureTreeSize() {
    return this.m_root.numNodes();
  }
  
  public double measureNumLeaves() {
    return this.m_root.numLeaves();
  }
  
  public double measureNumRules() {
    return this.m_root.numLeaves();
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(3);
    vector.addElement("measureTreeSize");
    vector.addElement("measureNumLeaves");
    vector.addElement("measureNumRules");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.compareToIgnoreCase("measureNumRules") == 0)
      return measureNumRules(); 
    if (paramString.compareToIgnoreCase("measureTreeSize") == 0)
      return measureTreeSize(); 
    if (paramString.compareToIgnoreCase("measureNumLeaves") == 0)
      return measureNumLeaves(); 
    throw new IllegalArgumentException(paramString + " not supported (j48)");
  }
  
  public String unprunedTipText() {
    return "Whether pruning is performed.";
  }
  
  public boolean getUnpruned() {
    return this.m_unpruned;
  }
  
  public void setUnpruned(boolean paramBoolean) {
    if (paramBoolean)
      this.m_reducedErrorPruning = false; 
    this.m_unpruned = paramBoolean;
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
    return "The minimum number of instances per leaf.";
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
    if (paramBoolean)
      this.m_unpruned = false; 
    this.m_reducedErrorPruning = paramBoolean;
  }
  
  public String numFoldsTipText() {
    return "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree.";
  }
  
  public int getNumFolds() {
    return this.m_numFolds;
  }
  
  public void setNumFolds(int paramInt) {
    this.m_numFolds = paramInt;
  }
  
  public String binarySplitsTipText() {
    return "Whether to use binary splits on nominal attributes when building the trees.";
  }
  
  public boolean getBinarySplits() {
    return this.m_binarySplits;
  }
  
  public void setBinarySplits(boolean paramBoolean) {
    this.m_binarySplits = paramBoolean;
  }
  
  public String subtreeRaisingTipText() {
    return "Whether to consider the subtree raising operation when pruning.";
  }
  
  public boolean getSubtreeRaising() {
    return this.m_subtreeRaising;
  }
  
  public void setSubtreeRaising(boolean paramBoolean) {
    this.m_subtreeRaising = paramBoolean;
  }
  
  public String saveInstanceDataTipText() {
    return "Whether to save the training data for visualization.";
  }
  
  public boolean getSaveInstanceData() {
    return this.m_noCleanup;
  }
  
  public void setSaveInstanceData(boolean paramBoolean) {
    this.m_noCleanup = paramBoolean;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new J48(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\J48.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */