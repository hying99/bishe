package weka.classifiers.trees.m5;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class RuleNode extends Classifier {
  private Instances m_instances;
  
  private int m_classIndex;
  
  protected int m_numInstances;
  
  private int m_numAttributes;
  
  private boolean m_isLeaf;
  
  private int m_splitAtt;
  
  private double m_splitValue;
  
  private PreConstructedLinearModel m_nodeModel = null;
  
  public int m_numParameters;
  
  private double m_rootMeanSquaredError;
  
  protected RuleNode m_left = null;
  
  protected RuleNode m_right = null;
  
  private RuleNode m_parent;
  
  private double m_splitNum = 4.0D;
  
  private double m_devFraction = 0.05D;
  
  private double m_pruningMultiplier = 2.0D;
  
  private int m_leafModelNum;
  
  private double m_globalDeviation;
  
  private double m_globalAbsDeviation;
  
  private int[] m_indices;
  
  private static final double SMOOTHING_CONSTANT = 15.0D;
  
  private int m_id;
  
  private boolean m_saveInstances = false;
  
  private boolean m_regressionTree;
  
  public RuleNode(double paramDouble1, double paramDouble2, RuleNode paramRuleNode) {
    this.m_parent = paramRuleNode;
    this.m_globalDeviation = paramDouble1;
    this.m_globalAbsDeviation = paramDouble2;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_rootMeanSquaredError = Double.MAX_VALUE;
    this.m_instances = paramInstances;
    this.m_classIndex = this.m_instances.classIndex();
    this.m_numInstances = this.m_instances.numInstances();
    this.m_numAttributes = this.m_instances.numAttributes();
    this.m_nodeModel = null;
    this.m_right = null;
    this.m_left = null;
    if (this.m_numInstances < this.m_splitNum || Rule.stdDev(this.m_classIndex, this.m_instances) < this.m_globalDeviation * this.m_devFraction) {
      this.m_isLeaf = true;
    } else {
      this.m_isLeaf = false;
    } 
    split();
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    double d = 0.0D;
    if (this.m_isLeaf) {
      if (this.m_nodeModel == null)
        throw new Exception("Classifier has not been built correctly."); 
      return this.m_nodeModel.classifyInstance(paramInstance);
    } 
    return (paramInstance.value(this.m_splitAtt) <= this.m_splitValue) ? this.m_left.classifyInstance(paramInstance) : this.m_right.classifyInstance(paramInstance);
  }
  
  protected static double smoothingOriginal(double paramDouble1, double paramDouble2, double paramDouble3) throws Exception {
    return (paramDouble1 * paramDouble2 + 15.0D * paramDouble3) / (paramDouble1 + 15.0D);
  }
  
  public void split() throws Exception {
    if (!this.m_isLeaf) {
      SplitEvaluate splitEvaluate = new YongSplitInfo(0, this.m_numInstances - 1, -1);
      YongSplitInfo yongSplitInfo = new YongSplitInfo(0, this.m_numInstances - 1, -1);
      byte b;
      for (b = 0; b < this.m_numAttributes; b++) {
        if (b != this.m_classIndex) {
          this.m_instances.sort(b);
          yongSplitInfo.attrSplit(b, this.m_instances);
          if (Math.abs(yongSplitInfo.maxImpurity() - splitEvaluate.maxImpurity()) > 1.0E-6D && yongSplitInfo.maxImpurity() > splitEvaluate.maxImpurity() + 1.0E-6D)
            splitEvaluate = yongSplitInfo.copy(); 
        } 
      } 
      if (splitEvaluate.splitAttr() < 0 || splitEvaluate.position() < 1 || splitEvaluate.position() > this.m_numInstances - 1) {
        this.m_isLeaf = true;
      } else {
        this.m_splitAtt = splitEvaluate.splitAttr();
        this.m_splitValue = splitEvaluate.splitValue();
        Instances instances1 = new Instances(this.m_instances, this.m_numInstances);
        Instances instances2 = new Instances(this.m_instances, this.m_numInstances);
        for (b = 0; b < this.m_numInstances; b++) {
          if (this.m_instances.instance(b).value(this.m_splitAtt) <= this.m_splitValue) {
            instances1.add(this.m_instances.instance(b));
          } else {
            instances2.add(this.m_instances.instance(b));
          } 
        } 
        instances1.compactify();
        instances2.compactify();
        this.m_left = new RuleNode(this.m_globalDeviation, this.m_globalAbsDeviation, this);
        this.m_left.setMinNumInstances(this.m_splitNum);
        this.m_left.setRegressionTree(this.m_regressionTree);
        this.m_left.setSaveInstances(this.m_saveInstances);
        this.m_left.buildClassifier(instances1);
        this.m_right = new RuleNode(this.m_globalDeviation, this.m_globalAbsDeviation, this);
        this.m_right.setMinNumInstances(this.m_splitNum);
        this.m_right.setRegressionTree(this.m_regressionTree);
        this.m_right.setSaveInstances(this.m_saveInstances);
        this.m_right.buildClassifier(instances2);
        if (!this.m_regressionTree) {
          boolean[] arrayOfBoolean = attsTestedBelow();
          arrayOfBoolean[this.m_classIndex] = true;
          byte b1 = 0;
          byte b2;
          for (b2 = 0; b2 < this.m_numAttributes; b2++) {
            if (arrayOfBoolean[b2])
              b1++; 
          } 
          int[] arrayOfInt = new int[b1];
          b1 = 0;
          for (b2 = 0; b2 < this.m_numAttributes; b2++) {
            if (arrayOfBoolean[b2] && b2 != this.m_classIndex)
              arrayOfInt[b1++] = b2; 
          } 
          arrayOfInt[b1] = this.m_classIndex;
          this.m_indices = arrayOfInt;
        } else {
          this.m_indices = new int[1];
          this.m_indices[0] = this.m_classIndex;
          this.m_numParameters = 1;
        } 
      } 
    } 
    if (this.m_isLeaf) {
      int[] arrayOfInt = new int[1];
      arrayOfInt[0] = this.m_classIndex;
      this.m_indices = arrayOfInt;
      this.m_numParameters = 1;
    } 
  }
  
  private void buildLinearModel(int[] paramArrayOfint) throws Exception {
    Instances instances = new Instances(this.m_instances);
    Remove remove = new Remove();
    remove.setInvertSelection(true);
    remove.setAttributeIndicesArray(paramArrayOfint);
    remove.setInputFormat(instances);
    instances = Filter.useFilter(instances, (Filter)remove);
    LinearRegression linearRegression = new LinearRegression();
    linearRegression.buildClassifier(instances);
    double[] arrayOfDouble1 = linearRegression.coefficients();
    double[] arrayOfDouble2 = new double[this.m_instances.numAttributes()];
    for (byte b = 0; b < arrayOfDouble1.length - 1; b++) {
      if (paramArrayOfint[b] != this.m_classIndex)
        arrayOfDouble2[paramArrayOfint[b]] = arrayOfDouble1[b]; 
    } 
    this.m_nodeModel = new PreConstructedLinearModel(arrayOfDouble2, arrayOfDouble1[arrayOfDouble1.length - 1]);
    this.m_nodeModel.buildClassifier(this.m_instances);
  }
  
  private boolean[] attsTestedAbove() {
    boolean[] arrayOfBoolean1 = new boolean[this.m_numAttributes];
    boolean[] arrayOfBoolean2 = null;
    if (this.m_parent != null)
      arrayOfBoolean2 = this.m_parent.attsTestedAbove(); 
    if (arrayOfBoolean2 != null)
      for (byte b = 0; b < this.m_numAttributes; b++)
        arrayOfBoolean1[b] = arrayOfBoolean2[b];  
    arrayOfBoolean1[this.m_splitAtt] = true;
    return arrayOfBoolean1;
  }
  
  private boolean[] attsTestedBelow() {
    boolean[] arrayOfBoolean1 = new boolean[this.m_numAttributes];
    boolean[] arrayOfBoolean2 = null;
    boolean[] arrayOfBoolean3 = null;
    if (this.m_right != null)
      arrayOfBoolean3 = this.m_right.attsTestedBelow(); 
    if (this.m_left != null)
      arrayOfBoolean2 = this.m_left.attsTestedBelow(); 
    for (byte b = 0; b < this.m_numAttributes; b++) {
      if (arrayOfBoolean2 != null)
        arrayOfBoolean1[b] = (arrayOfBoolean1[b] || arrayOfBoolean2[b]); 
      if (arrayOfBoolean3 != null)
        arrayOfBoolean1[b] = (arrayOfBoolean1[b] || arrayOfBoolean3[b]); 
    } 
    if (!this.m_isLeaf)
      arrayOfBoolean1[this.m_splitAtt] = true; 
    return arrayOfBoolean1;
  }
  
  public int numLeaves(int paramInt) {
    if (!this.m_isLeaf) {
      this.m_leafModelNum = 0;
      if (this.m_left != null)
        paramInt = this.m_left.numLeaves(paramInt); 
      if (this.m_right != null)
        paramInt = this.m_right.numLeaves(paramInt); 
    } else {
      this.m_leafModelNum = ++paramInt;
    } 
    return paramInt;
  }
  
  public String toString() {
    return printNodeLinearModel();
  }
  
  public String printNodeLinearModel() {
    return this.m_nodeModel.toString();
  }
  
  public String printLeafModels() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_isLeaf) {
      stringBuffer.append("\nLM num: " + this.m_leafModelNum);
      stringBuffer.append(this.m_nodeModel.toString());
      stringBuffer.append("\n");
    } else {
      stringBuffer.append(this.m_left.printLeafModels());
      stringBuffer.append(this.m_right.printLeafModels());
    } 
    return stringBuffer.toString();
  }
  
  public String nodeToString() {
    StringBuffer stringBuffer = new StringBuffer();
    System.out.println("In to string");
    stringBuffer.append("Node:\n\tnum inst: " + this.m_numInstances);
    if (this.m_isLeaf) {
      stringBuffer.append("\n\tleaf");
    } else {
      stringBuffer.append("\tnode");
    } 
    stringBuffer.append("\n\tSplit att: " + this.m_instances.attribute(this.m_splitAtt).name());
    stringBuffer.append("\n\tSplit val: " + Utils.doubleToString(this.m_splitValue, 1, 3));
    stringBuffer.append("\n\tLM num: " + this.m_leafModelNum);
    stringBuffer.append("\n\tLinear model\n" + this.m_nodeModel.toString());
    stringBuffer.append("\n\n");
    if (this.m_left != null)
      stringBuffer.append(this.m_left.nodeToString()); 
    if (this.m_right != null)
      stringBuffer.append(this.m_right.nodeToString()); 
    return stringBuffer.toString();
  }
  
  public String treeToString(int paramInt) {
    StringBuffer stringBuffer = new StringBuffer();
    if (!this.m_isLeaf) {
      stringBuffer.append("\n");
      byte b;
      for (b = 1; b <= paramInt; b++)
        stringBuffer.append("|   "); 
      if (this.m_instances.attribute(this.m_splitAtt).name().charAt(0) != '[') {
        stringBuffer.append(this.m_instances.attribute(this.m_splitAtt).name() + " <= " + Utils.doubleToString(this.m_splitValue, 1, 3) + " : ");
      } else {
        stringBuffer.append(this.m_instances.attribute(this.m_splitAtt).name() + " false : ");
      } 
      if (this.m_left != null) {
        stringBuffer.append(this.m_left.treeToString(paramInt + 1));
      } else {
        stringBuffer.append("NULL\n");
      } 
      for (b = 1; b <= paramInt; b++)
        stringBuffer.append("|   "); 
      if (this.m_instances.attribute(this.m_splitAtt).name().charAt(0) != '[') {
        stringBuffer.append(this.m_instances.attribute(this.m_splitAtt).name() + " >  " + Utils.doubleToString(this.m_splitValue, 1, 3) + " : ");
      } else {
        stringBuffer.append(this.m_instances.attribute(this.m_splitAtt).name() + " true : ");
      } 
      if (this.m_right != null) {
        stringBuffer.append(this.m_right.treeToString(paramInt + 1));
      } else {
        stringBuffer.append("NULL\n");
      } 
    } else {
      stringBuffer.append("LM" + this.m_leafModelNum);
      if (this.m_globalDeviation > 0.0D) {
        stringBuffer.append(" (" + this.m_numInstances + "/" + Utils.doubleToString(100.0D * this.m_rootMeanSquaredError / this.m_globalAbsDeviation, 1, 3) + "%)\n");
      } else {
        stringBuffer.append(" (" + this.m_numInstances + ")\n");
      } 
    } 
    return stringBuffer.toString();
  }
  
  public void installLinearModels() throws Exception {
    if (this.m_isLeaf) {
      buildLinearModel(this.m_indices);
    } else {
      if (this.m_left != null)
        this.m_left.installLinearModels(); 
      if (this.m_right != null)
        this.m_right.installLinearModels(); 
      buildLinearModel(this.m_indices);
    } 
    Evaluation evaluation = new Evaluation(this.m_instances);
    evaluation.evaluateModel(this.m_nodeModel, this.m_instances);
    this.m_rootMeanSquaredError = evaluation.rootMeanSquaredError();
    if (!this.m_saveInstances)
      this.m_instances = new Instances(this.m_instances, 0); 
  }
  
  public void installSmoothedModels() throws Exception {
    if (this.m_isLeaf) {
      double[] arrayOfDouble1 = new double[this.m_numAttributes];
      double[] arrayOfDouble2 = this.m_nodeModel.coefficients();
      RuleNode ruleNode = this;
      for (byte b = 0; b < arrayOfDouble2.length; b++) {
        if (b != this.m_classIndex)
          arrayOfDouble1[b] = arrayOfDouble2[b]; 
      } 
      double d = this.m_nodeModel.intercept();
      while (true) {
        if (ruleNode.m_parent != null) {
          PreConstructedLinearModel preConstructedLinearModel = ruleNode.m_parent.getModel();
          double d1 = ruleNode.m_numInstances;
          byte b1;
          for (b1 = 0; b1 < arrayOfDouble1.length; b1++)
            arrayOfDouble1[b1] = arrayOfDouble1[b1] * d1 / (d1 + 15.0D); 
          d = d * d1 / (d1 + 15.0D);
          arrayOfDouble2 = ruleNode.m_parent.getModel().coefficients();
          for (b1 = 0; b1 < arrayOfDouble2.length; b1++) {
            if (b1 != this.m_classIndex)
              arrayOfDouble1[b1] = arrayOfDouble1[b1] + 15.0D * arrayOfDouble2[b1] / (d1 + 15.0D); 
          } 
          d += 15.0D * ruleNode.m_parent.getModel().intercept() / (d1 + 15.0D);
          ruleNode = ruleNode.m_parent;
        } 
        if (ruleNode.m_parent == null) {
          this.m_nodeModel = new PreConstructedLinearModel(arrayOfDouble1, d);
          this.m_nodeModel.buildClassifier(this.m_instances);
          break;
        } 
      } 
    } 
    if (this.m_left != null)
      this.m_left.installSmoothedModels(); 
    if (this.m_right != null)
      this.m_right.installSmoothedModels(); 
  }
  
  public void prune() throws Exception {
    Evaluation evaluation = null;
    if (this.m_isLeaf) {
      buildLinearModel(this.m_indices);
      evaluation = new Evaluation(this.m_instances);
      evaluation.evaluateModel(this.m_nodeModel, this.m_instances);
      this.m_rootMeanSquaredError = evaluation.rootMeanSquaredError();
    } else {
      if (this.m_left != null)
        this.m_left.prune(); 
      if (this.m_right != null)
        this.m_right.prune(); 
      buildLinearModel(this.m_indices);
      evaluation = new Evaluation(this.m_instances);
      evaluation.evaluateModel(this.m_nodeModel, this.m_instances);
      double d1 = evaluation.rootMeanSquaredError();
      double d2 = d1 * pruningFactor(this.m_numInstances, this.m_nodeModel.numParameters() + 1);
      Evaluation evaluation1 = new Evaluation(this.m_instances);
      int i = 0;
      int j = 0;
      evaluation1.evaluateModel(this, this.m_instances);
      double d3 = evaluation1.rootMeanSquaredError();
      if (this.m_left != null)
        i = this.m_left.numParameters(); 
      if (this.m_right != null)
        j = this.m_right.numParameters(); 
      double d4 = d3 * pruningFactor(this.m_numInstances, i + j + 1);
      if (d2 <= d4 || d2 < this.m_globalDeviation * 1.0E-5D) {
        this.m_isLeaf = true;
        this.m_right = null;
        this.m_left = null;
        this.m_numParameters = this.m_nodeModel.numParameters() + 1;
        this.m_rootMeanSquaredError = d1;
      } else {
        this.m_numParameters = i + j + 1;
        this.m_rootMeanSquaredError = d3;
      } 
    } 
    if (!this.m_saveInstances)
      this.m_instances = new Instances(this.m_instances, 0); 
  }
  
  private double pruningFactor(int paramInt1, int paramInt2) {
    return (paramInt1 <= paramInt2) ? 10.0D : ((paramInt1 + this.m_pruningMultiplier * paramInt2) / (paramInt1 - paramInt2));
  }
  
  public void findBestLeaf(double[] paramArrayOfdouble, RuleNode[] paramArrayOfRuleNode) {
    if (!this.m_isLeaf) {
      if (this.m_left != null)
        this.m_left.findBestLeaf(paramArrayOfdouble, paramArrayOfRuleNode); 
      if (this.m_right != null)
        this.m_right.findBestLeaf(paramArrayOfdouble, paramArrayOfRuleNode); 
    } else if (this.m_numInstances > paramArrayOfdouble[0]) {
      paramArrayOfdouble[0] = this.m_numInstances;
      paramArrayOfRuleNode[0] = this;
    } 
  }
  
  public void returnLeaves(FastVector[] paramArrayOfFastVector) {
    if (this.m_isLeaf) {
      paramArrayOfFastVector[0].addElement(this);
    } else {
      if (this.m_left != null)
        this.m_left.returnLeaves(paramArrayOfFastVector); 
      if (this.m_right != null)
        this.m_right.returnLeaves(paramArrayOfFastVector); 
    } 
  }
  
  public RuleNode parentNode() {
    return this.m_parent;
  }
  
  public RuleNode leftNode() {
    return this.m_left;
  }
  
  public RuleNode rightNode() {
    return this.m_right;
  }
  
  public int splitAtt() {
    return this.m_splitAtt;
  }
  
  public double splitVal() {
    return this.m_splitValue;
  }
  
  public int numberOfLinearModels() {
    return this.m_isLeaf ? 1 : (this.m_left.numberOfLinearModels() + this.m_right.numberOfLinearModels());
  }
  
  public boolean isLeaf() {
    return this.m_isLeaf;
  }
  
  protected double rootMeanSquaredError() {
    return this.m_rootMeanSquaredError;
  }
  
  public PreConstructedLinearModel getModel() {
    return this.m_nodeModel;
  }
  
  public int getNumInstances() {
    return this.m_numInstances;
  }
  
  private int numParameters() {
    return this.m_numParameters;
  }
  
  public boolean getRegressionTree() {
    return this.m_regressionTree;
  }
  
  public void setMinNumInstances(double paramDouble) {
    this.m_splitNum = paramDouble;
  }
  
  public double getMinNumInstances() {
    return this.m_splitNum;
  }
  
  public void setRegressionTree(boolean paramBoolean) {
    this.m_regressionTree = paramBoolean;
  }
  
  public void printAllModels() {
    if (this.m_isLeaf) {
      System.out.println(this.m_nodeModel.toString());
    } else {
      System.out.println(this.m_nodeModel.toString());
      this.m_left.printAllModels();
      this.m_right.printAllModels();
    } 
  }
  
  protected int assignIDs(int paramInt) {
    int i = paramInt + 1;
    this.m_id = i;
    if (this.m_left != null)
      i = this.m_left.assignIDs(i); 
    if (this.m_right != null)
      i = this.m_right.assignIDs(i); 
    return i;
  }
  
  public void graph(StringBuffer paramStringBuffer) {
    assignIDs(-1);
    graphTree(paramStringBuffer);
  }
  
  protected void graphTree(StringBuffer paramStringBuffer) {
    paramStringBuffer.append("N" + this.m_id + (this.m_isLeaf ? (" [label=\"LM " + this.m_leafModelNum) : (" [label=\"" + this.m_instances.attribute(this.m_splitAtt).name())) + (this.m_isLeaf ? (" (" + ((this.m_globalDeviation > 0.0D) ? (this.m_numInstances + "/" + Utils.doubleToString(100.0D * this.m_rootMeanSquaredError / this.m_globalAbsDeviation, 1, 3) + "%)") : (this.m_numInstances + ")")) + "\" shape=box style=filled ") : "\"") + (this.m_saveInstances ? ("data=\n" + this.m_instances + "\n,\n") : "") + "]\n");
    if (this.m_left != null) {
      paramStringBuffer.append("N" + this.m_id + "->" + "N" + this.m_left.m_id + " [label=\"<=" + Utils.doubleToString(this.m_splitValue, 1, 3) + "\"]\n");
      this.m_left.graphTree(paramStringBuffer);
    } 
    if (this.m_right != null) {
      paramStringBuffer.append("N" + this.m_id + "->" + "N" + this.m_right.m_id + " [label=\">" + Utils.doubleToString(this.m_splitValue, 1, 3) + "\"]\n");
      this.m_right.graphTree(paramStringBuffer);
    } 
  }
  
  protected void setSaveInstances(boolean paramBoolean) {
    this.m_saveInstances = paramBoolean;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\m5\RuleNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */