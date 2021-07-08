package weka.classifiers.trees;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.j48.C45ModelSelection;
import weka.classifiers.trees.j48.ModelSelection;
import weka.classifiers.trees.lmt.LMTNode;
import weka.classifiers.trees.lmt.ResidualModelSelection;
import weka.core.AdditionalMeasureProducer;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class LMT extends Classifier implements OptionHandler, AdditionalMeasureProducer, Drawable {
  protected ReplaceMissingValues m_replaceMissing;
  
  protected NominalToBinary m_nominalToBinary;
  
  protected LMTNode m_tree;
  
  protected boolean m_fastRegression = true;
  
  protected boolean m_convertNominal;
  
  protected boolean m_splitOnResiduals;
  
  protected boolean m_errorOnProbabilities;
  
  protected int m_minNumInstances = 15;
  
  protected int m_numBoostingIterations = -1;
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    C45ModelSelection c45ModelSelection;
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("Nominal class, please."); 
    Instances instances = new Instances(paramInstances);
    instances.deleteWithMissingClass();
    if (paramInstances.numInstances() == 0)
      throw new Exception("No instances without missing class values in training file!"); 
    this.m_replaceMissing = new ReplaceMissingValues();
    this.m_replaceMissing.setInputFormat(instances);
    instances = Filter.useFilter(instances, (Filter)this.m_replaceMissing);
    if (this.m_convertNominal) {
      this.m_nominalToBinary = new NominalToBinary();
      this.m_nominalToBinary.setInputFormat(instances);
      instances = Filter.useFilter(instances, (Filter)this.m_nominalToBinary);
    } 
    byte b = 2;
    if (this.m_splitOnResiduals) {
      ResidualModelSelection residualModelSelection = new ResidualModelSelection(b);
    } else {
      c45ModelSelection = new C45ModelSelection(b, instances);
    } 
    this.m_tree = new LMTNode((ModelSelection)c45ModelSelection, this.m_numBoostingIterations, this.m_fastRegression, this.m_errorOnProbabilities, this.m_minNumInstances);
    this.m_tree.buildClassifier(instances);
    if (c45ModelSelection instanceof C45ModelSelection)
      c45ModelSelection.cleanup(); 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    this.m_replaceMissing.input(paramInstance);
    paramInstance = this.m_replaceMissing.output();
    if (this.m_convertNominal) {
      this.m_nominalToBinary.input(paramInstance);
      paramInstance = this.m_nominalToBinary.output();
    } 
    return this.m_tree.distributionForInstance(paramInstance);
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    double d = -1.0D;
    byte b1 = 0;
    double[] arrayOfDouble = distributionForInstance(paramInstance);
    for (byte b2 = 0; b2 < paramInstance.numClasses(); b2++) {
      if (Utils.gr(arrayOfDouble[b2], d)) {
        b1 = b2;
        d = arrayOfDouble[b2];
      } 
    } 
    return b1;
  }
  
  public String toString() {
    return (this.m_tree != null) ? ("Logistic model tree \n------------------\n" + this.m_tree.toString()) : "No tree build";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(8);
    vector.addElement(new Option("\tBinary splits (convert nominal attributes to binary ones)\n", "B", 0, "-B"));
    vector.addElement(new Option("\tSplit on residuals instead of class values\n", "R", 0, "-R"));
    vector.addElement(new Option("\tUse cross-validation for boosting at all nodes (i.e., disable heuristic)\n", "C", 0, "-C"));
    vector.addElement(new Option("\tUse error on probabilities instead of misclassification error for stopping criterion of LogitBoost.\n", "P", 0, "-P"));
    vector.addElement(new Option("\tSet fixed number of iterations for LogitBoost (instead of using cross-validation)\n", "I", 1, "-I <numIterations>"));
    vector.addElement(new Option("\tSet minimum number of instances at which a node can be split (default 15)\n", "M", 1, "-M <numInstances>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setConvertNominal(Utils.getFlag('B', paramArrayOfString));
    setSplitOnResiduals(Utils.getFlag('R', paramArrayOfString));
    setFastRegression(!Utils.getFlag('C', paramArrayOfString));
    setErrorOnProbabilities(Utils.getFlag('P', paramArrayOfString));
    String str = Utils.getOption('I', paramArrayOfString);
    if (str.length() != 0)
      setNumBoostingIterations((new Integer(str)).intValue()); 
    str = Utils.getOption('M', paramArrayOfString);
    if (str.length() != 0)
      setMinNumInstances((new Integer(str)).intValue()); 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[8];
    byte b = 0;
    if (getConvertNominal())
      arrayOfString[b++] = "-B"; 
    if (getSplitOnResiduals())
      arrayOfString[b++] = "-R"; 
    if (!getFastRegression())
      arrayOfString[b++] = "-C"; 
    if (getErrorOnProbabilities())
      arrayOfString[b++] = "-P"; 
    arrayOfString[b++] = "-I";
    arrayOfString[b++] = "" + getNumBoostingIterations();
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getMinNumInstances();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public boolean getConvertNominal() {
    return this.m_convertNominal;
  }
  
  public boolean getSplitOnResiduals() {
    return this.m_splitOnResiduals;
  }
  
  public boolean getFastRegression() {
    return this.m_fastRegression;
  }
  
  public boolean getErrorOnProbabilities() {
    return this.m_errorOnProbabilities;
  }
  
  public int getNumBoostingIterations() {
    return this.m_numBoostingIterations;
  }
  
  public int getMinNumInstances() {
    return this.m_minNumInstances;
  }
  
  public void setConvertNominal(boolean paramBoolean) {
    this.m_convertNominal = paramBoolean;
  }
  
  public void setSplitOnResiduals(boolean paramBoolean) {
    this.m_splitOnResiduals = paramBoolean;
  }
  
  public void setFastRegression(boolean paramBoolean) {
    this.m_fastRegression = paramBoolean;
  }
  
  public void setErrorOnProbabilities(boolean paramBoolean) {
    this.m_errorOnProbabilities = paramBoolean;
  }
  
  public void setNumBoostingIterations(int paramInt) {
    this.m_numBoostingIterations = paramInt;
  }
  
  public void setMinNumInstances(int paramInt) {
    this.m_minNumInstances = paramInt;
  }
  
  public int graphType() {
    return 1;
  }
  
  public String graph() throws Exception {
    return this.m_tree.graph();
  }
  
  public int measureTreeSize() {
    return this.m_tree.numNodes();
  }
  
  public int measureNumLeaves() {
    return this.m_tree.numLeaves();
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(2);
    vector.addElement("measureTreeSize");
    vector.addElement("measureNumLeaves");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.compareToIgnoreCase("measureTreeSize") == 0)
      return measureTreeSize(); 
    if (paramString.compareToIgnoreCase("measureNumLeaves") == 0)
      return measureNumLeaves(); 
    throw new IllegalArgumentException(paramString + " not supported (LMT)");
  }
  
  public String globalInfo() {
    return "Classifier for building 'logistic model trees', which are classification trees with logistic regression functions at the leaves. The algorithm can deal with binary and multi-class target variables, numeric and nominal attributes and missing values. For more information see: N.Landwehr, M.Hall, E. Frank 'Logistic Model Trees' (ECML 2003).";
  }
  
  public String convertNominalTipText() {
    return "Convert all nominal attributes to binary ones before building the tree. This means that all splits in the final tree will be binary.";
  }
  
  public String splitOnResidualsTipText() {
    return "Set splitting criterion based on the residuals of LogitBoost. There are two possible splitting criteria for LMT: the default is to use the C4.5 splitting criterion that uses information gain on the class variable. The other splitting criterion tries to improve the purity in the residuals produces when fitting the logistic regression functions. The choice of the splitting criterion does not usually affect classification accuracy much, but can produce different trees.";
  }
  
  public String fastRegressionTipText() {
    return "Use heuristic that avoids cross-validating the number of Logit-Boost iterations at every node. When fitting the logistic regression functions at a node, LMT has to determine the number of LogitBoost iterations to run. Originally, this number was cross-validated at every node in the tree. To save time, this heuristic cross-validates the number only once and then uses that number at every node in the tree. Usually this does not decrease accuracy but improves runtime considerably.";
  }
  
  public String errorOnProbabilitiesTipText() {
    return "Minimize error on probabilities instead of misclassification error when cross-validating the number of LogitBoost iterations. When set, the number of LogitBoost iterations is chosen that minimizes the root mean squared error instead of the misclassification error.";
  }
  
  public String numBoostingIterationsTipText() {
    return "Set a fixed number of iterations for LogitBoost. If >= 0, this sets a fixed number of LogitBoost iterations that is used everywhere in the tree. If < 0, the number is cross-validated.";
  }
  
  public String minNumInstancesTipText() {
    return "Set the minimum number of instances at which a node is considered for splitting. The default value is 15.";
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    System.out.println(Evaluation.evaluateModel(new LMT(), paramArrayOfString));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\LMT.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */