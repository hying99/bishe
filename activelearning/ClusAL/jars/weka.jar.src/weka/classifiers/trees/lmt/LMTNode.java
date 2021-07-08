package weka.classifiers.trees.lmt;

import java.util.Collections;
import java.util.Vector;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.classifiers.trees.j48.ClassifierSplitModel;
import weka.classifiers.trees.j48.ModelSelection;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;

public class LMTNode extends LogisticBase {
  protected double m_totalInstanceWeight;
  
  protected int m_id;
  
  protected int m_leafModelNum;
  
  public double m_alpha;
  
  public double m_numIncorrectModel;
  
  public double m_numIncorrectTree;
  
  protected int m_minNumInstances;
  
  protected ModelSelection m_modelSelection;
  
  protected NominalToBinary m_nominalToBinary;
  
  protected SimpleLinearRegression[][] m_higherRegressions;
  
  protected int m_numHigherRegressions = 0;
  
  protected static int m_numFoldsPruning = 5;
  
  protected boolean m_fastRegression;
  
  protected int m_numInstances;
  
  protected ClassifierSplitModel m_localModel;
  
  protected LMTNode[] m_sons;
  
  protected boolean m_isLeaf;
  
  public LMTNode(ModelSelection paramModelSelection, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2) {
    this.m_modelSelection = paramModelSelection;
    this.m_fixedNumIterations = paramInt1;
    this.m_fastRegression = paramBoolean1;
    this.m_errorOnProbabilities = paramBoolean2;
    this.m_minNumInstances = paramInt2;
    this.m_maxIterations = 200;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (this.m_fastRegression && this.m_fixedNumIterations < 0)
      this.m_fixedNumIterations = tryLogistic(paramInstances); 
    Instances instances = new Instances(paramInstances);
    instances.stratify(m_numFoldsPruning);
    double[][] arrayOfDouble1 = new double[m_numFoldsPruning][];
    double[][] arrayOfDouble2 = new double[m_numFoldsPruning][];
    int i;
    for (i = 0; i < m_numFoldsPruning; i++) {
      Instances instances1 = instances.trainCV(m_numFoldsPruning, i);
      Instances instances2 = instances.testCV(m_numFoldsPruning, i);
      buildTree(instances1, (SimpleLinearRegression[][])null, instances1.numInstances());
      int n = getNumInnerNodes();
      arrayOfDouble1[i] = new double[n + 2];
      arrayOfDouble2[i] = new double[n + 2];
      prune(arrayOfDouble1[i], arrayOfDouble2[i], instances2);
    } 
    buildTree(paramInstances, (SimpleLinearRegression[][])null, paramInstances.numInstances());
    i = getNumInnerNodes();
    double[] arrayOfDouble3 = new double[i + 2];
    int j = prune(arrayOfDouble3, (double[])null, (Instances)null);
    double[] arrayOfDouble4 = new double[i + 2];
    int k;
    for (k = 0; k <= j; k++) {
      double d3 = Math.sqrt(arrayOfDouble3[k] * arrayOfDouble3[k + 1]);
      double d4 = 0.0D;
      for (byte b = 0; b < m_numFoldsPruning; b++) {
        byte b1;
        for (b1 = 0; arrayOfDouble1[b][b1] <= d3; b1++);
        d4 += arrayOfDouble2[b][b1 - 1];
      } 
      arrayOfDouble4[k] = d4;
    } 
    k = -1;
    double d1 = Double.MAX_VALUE;
    for (int m = j; m >= 0; m--) {
      if (arrayOfDouble4[m] < d1) {
        d1 = arrayOfDouble4[m];
        k = m;
      } 
    } 
    double d2 = Math.sqrt(arrayOfDouble3[k] * arrayOfDouble3[k + 1]);
    unprune();
    prune(d2);
    cleanup();
  }
  
  public void buildTree(Instances paramInstances, SimpleLinearRegression[][] paramArrayOfSimpleLinearRegression, double paramDouble) throws Exception {
    boolean bool;
    this.m_totalInstanceWeight = paramDouble;
    this.m_train = new Instances(paramInstances);
    this.m_isLeaf = true;
    this.m_sons = null;
    this.m_numInstances = this.m_train.numInstances();
    this.m_numClasses = this.m_train.numClasses();
    this.m_numericData = getNumericData(this.m_train);
    this.m_numericDataHeader = new Instances(this.m_numericData, 0);
    this.m_regressions = initRegressions();
    this.m_numRegressions = 0;
    if (paramArrayOfSimpleLinearRegression != null) {
      this.m_higherRegressions = paramArrayOfSimpleLinearRegression;
    } else {
      this.m_higherRegressions = new SimpleLinearRegression[this.m_numClasses][0];
    } 
    this.m_numHigherRegressions = (this.m_higherRegressions[0]).length;
    if (this.m_numInstances >= m_numFoldsBoosting)
      if (this.m_fixedNumIterations > 0) {
        performBoosting(this.m_fixedNumIterations);
      } else {
        performBoostingCV();
      }  
    this.m_regressions = selectRegressions(this.m_regressions);
    if (this.m_numInstances > this.m_minNumInstances) {
      if (this.m_modelSelection instanceof ResidualModelSelection) {
        double[][] arrayOfDouble1 = getProbs(getFs(this.m_numericData));
        double[][] arrayOfDouble2 = getYs(this.m_train);
        double[][] arrayOfDouble3 = getZs(arrayOfDouble1, arrayOfDouble2);
        double[][] arrayOfDouble4 = getWs(arrayOfDouble1, arrayOfDouble2);
        this.m_localModel = ((ResidualModelSelection)this.m_modelSelection).selectModel(this.m_train, arrayOfDouble3, arrayOfDouble4);
      } else {
        this.m_localModel = this.m_modelSelection.selectModel(this.m_train);
      } 
      bool = (this.m_localModel.numSubsets() > 1) ? true : false;
    } else {
      bool = false;
    } 
    if (bool) {
      this.m_isLeaf = false;
      Instances[] arrayOfInstances = this.m_localModel.split(this.m_train);
      this.m_sons = new LMTNode[this.m_localModel.numSubsets()];
      for (byte b = 0; b < this.m_sons.length; b++) {
        this.m_sons[b] = new LMTNode(this.m_modelSelection, this.m_fixedNumIterations, this.m_fastRegression, this.m_errorOnProbabilities, this.m_minNumInstances);
        this.m_sons[b].buildTree(arrayOfInstances[b], mergeArrays(this.m_regressions, this.m_higherRegressions), this.m_totalInstanceWeight);
        arrayOfInstances[b] = null;
      } 
    } 
  }
  
  public void prune(double paramDouble) throws Exception {
    CompareNode compareNode = new CompareNode();
    modelErrors();
    treeErrors();
    calculateAlphas();
    Vector vector = getNodes();
    boolean bool;
    for (bool = (vector.size() > 0) ? true : false; bool; bool = (vector.size() > 0) ? true : false) {
      LMTNode lMTNode = Collections.<LMTNode>min(vector, compareNode);
      if (lMTNode.m_alpha > paramDouble)
        break; 
      lMTNode.m_isLeaf = true;
      lMTNode.m_sons = null;
      treeErrors();
      calculateAlphas();
      vector = getNodes();
    } 
  }
  
  public int prune(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, Instances paramInstances) throws Exception {
    CompareNode compareNode = new CompareNode();
    modelErrors();
    treeErrors();
    calculateAlphas();
    Vector vector = getNodes();
    boolean bool = (vector.size() > 0) ? true : false;
    paramArrayOfdouble1[0] = 0.0D;
    if (paramArrayOfdouble2 != null) {
      Evaluation evaluation = new Evaluation(paramInstances);
      evaluation.evaluateModel(this, paramInstances);
      paramArrayOfdouble2[0] = evaluation.errorRate();
    } 
    byte b = 0;
    while (bool) {
      b++;
      LMTNode lMTNode = Collections.<LMTNode>min(vector, compareNode);
      lMTNode.m_isLeaf = true;
      paramArrayOfdouble1[b] = lMTNode.m_alpha;
      if (paramArrayOfdouble2 != null) {
        Evaluation evaluation = new Evaluation(paramInstances);
        evaluation.evaluateModel(this, paramInstances);
        paramArrayOfdouble2[b] = evaluation.errorRate();
      } 
      treeErrors();
      calculateAlphas();
      vector = getNodes();
      bool = (vector.size() > 0) ? true : false;
    } 
    paramArrayOfdouble1[b + 1] = 1.0D;
    return b;
  }
  
  protected void unprune() {
    if (this.m_sons != null) {
      this.m_isLeaf = false;
      for (byte b = 0; b < this.m_sons.length; b++)
        this.m_sons[b].unprune(); 
    } 
  }
  
  protected int tryLogistic(Instances paramInstances) throws Exception {
    Instances instances = new Instances(paramInstances);
    NominalToBinary nominalToBinary = new NominalToBinary();
    nominalToBinary.setInputFormat(instances);
    instances = Filter.useFilter(instances, (Filter)nominalToBinary);
    LogisticBase logisticBase = new LogisticBase(0, true, this.m_errorOnProbabilities);
    logisticBase.setMaxIterations(200);
    logisticBase.buildClassifier(instances);
    return logisticBase.getNumRegressions();
  }
  
  public int getNumInnerNodes() {
    if (this.m_isLeaf)
      return 0; 
    int i = 1;
    for (byte b = 0; b < this.m_sons.length; b++)
      i += this.m_sons[b].getNumInnerNodes(); 
    return i;
  }
  
  public int getNumLeaves() {
    boolean bool;
    if (!this.m_isLeaf) {
      bool = false;
      byte b1 = 0;
      for (byte b2 = 0; b2 < this.m_sons.length; b2++) {
        bool += this.m_sons[b2].getNumLeaves();
        if ((this.m_sons[b2]).m_isLeaf && !this.m_sons[b2].hasModels())
          b1++; 
      } 
      if (b1 > 1)
        bool -= b1 - 1; 
    } else {
      bool = true;
    } 
    return bool;
  }
  
  public void modelErrors() throws Exception {
    Evaluation evaluation = new Evaluation(this.m_train);
    if (!this.m_isLeaf) {
      this.m_isLeaf = true;
      evaluation.evaluateModel(this, this.m_train);
      this.m_isLeaf = false;
      this.m_numIncorrectModel = evaluation.incorrect();
      for (byte b = 0; b < this.m_sons.length; b++)
        this.m_sons[b].modelErrors(); 
    } else {
      evaluation.evaluateModel(this, this.m_train);
      this.m_numIncorrectModel = evaluation.incorrect();
    } 
  }
  
  public void treeErrors() {
    if (this.m_isLeaf) {
      this.m_numIncorrectTree = this.m_numIncorrectModel;
    } else {
      this.m_numIncorrectTree = 0.0D;
      for (byte b = 0; b < this.m_sons.length; b++) {
        this.m_sons[b].treeErrors();
        this.m_numIncorrectTree += (this.m_sons[b]).m_numIncorrectTree;
      } 
    } 
  }
  
  public void calculateAlphas() throws Exception {
    if (!this.m_isLeaf) {
      double d = this.m_numIncorrectModel - this.m_numIncorrectTree;
      if (d <= 0.0D) {
        this.m_isLeaf = true;
        this.m_sons = null;
        this.m_alpha = Double.MAX_VALUE;
      } else {
        d /= this.m_totalInstanceWeight;
        this.m_alpha = d / (getNumLeaves() - 1);
        for (byte b = 0; b < this.m_sons.length; b++)
          this.m_sons[b].calculateAlphas(); 
      } 
    } else {
      this.m_alpha = Double.MAX_VALUE;
    } 
  }
  
  protected SimpleLinearRegression[][] mergeArrays(SimpleLinearRegression[][] paramArrayOfSimpleLinearRegression1, SimpleLinearRegression[][] paramArrayOfSimpleLinearRegression2) {
    int i = (paramArrayOfSimpleLinearRegression1[0]).length;
    int j = (paramArrayOfSimpleLinearRegression2[0]).length;
    SimpleLinearRegression[][] arrayOfSimpleLinearRegression = new SimpleLinearRegression[this.m_numClasses][i + j];
    boolean bool = false;
    byte b;
    for (b = 0; b < this.m_numClasses; b++) {
      for (byte b1 = 0; b1 < i; b1++)
        arrayOfSimpleLinearRegression[b][b1] = paramArrayOfSimpleLinearRegression1[b][b1]; 
    } 
    for (b = 0; b < this.m_numClasses; b++) {
      for (byte b1 = 0; b1 < j; b1++)
        arrayOfSimpleLinearRegression[b][b1 + i] = paramArrayOfSimpleLinearRegression2[b][b1]; 
    } 
    return arrayOfSimpleLinearRegression;
  }
  
  public Vector getNodes() {
    Vector vector = new Vector();
    getNodes(vector);
    return vector;
  }
  
  public void getNodes(Vector paramVector) {
    if (!this.m_isLeaf) {
      paramVector.add(this);
      for (byte b = 0; b < this.m_sons.length; b++)
        this.m_sons[b].getNodes(paramVector); 
    } 
  }
  
  protected Instances getNumericData(Instances paramInstances) throws Exception {
    Instances instances = new Instances(paramInstances);
    this.m_nominalToBinary = new NominalToBinary();
    this.m_nominalToBinary.setInputFormat(instances);
    instances = Filter.useFilter(instances, (Filter)this.m_nominalToBinary);
    return super.getNumericData(instances);
  }
  
  protected double[] getFs(Instance paramInstance) throws Exception {
    double[] arrayOfDouble1 = new double[this.m_numClasses];
    double[] arrayOfDouble2 = super.getFs(paramInstance);
    for (byte b = 0; b < this.m_numHigherRegressions; b++) {
      double d = 0.0D;
      byte b1;
      for (b1 = 0; b1 < this.m_numClasses; b1++) {
        arrayOfDouble1[b1] = this.m_higherRegressions[b1][b].classifyInstance(paramInstance);
        d += arrayOfDouble1[b1];
      } 
      d /= this.m_numClasses;
      for (b1 = 0; b1 < this.m_numClasses; b1++)
        arrayOfDouble2[b1] = arrayOfDouble2[b1] + (arrayOfDouble1[b1] - d) * (this.m_numClasses - 1) / this.m_numClasses; 
    } 
    return arrayOfDouble2;
  }
  
  public boolean hasModels() {
    return (this.m_numRegressions > 0);
  }
  
  public double[] modelDistributionForInstance(Instance paramInstance) throws Exception {
    paramInstance = (Instance)paramInstance.copy();
    this.m_nominalToBinary.input(paramInstance);
    paramInstance = this.m_nominalToBinary.output();
    paramInstance.setDataset(this.m_numericDataHeader);
    return probs(getFs(paramInstance));
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble;
    if (this.m_isLeaf) {
      arrayOfDouble = modelDistributionForInstance(paramInstance);
    } else {
      int i = this.m_localModel.whichSubset(paramInstance);
      arrayOfDouble = this.m_sons[i].distributionForInstance(paramInstance);
    } 
    return arrayOfDouble;
  }
  
  public int numLeaves() {
    if (this.m_isLeaf)
      return 1; 
    int i = 0;
    for (byte b = 0; b < this.m_sons.length; b++)
      i += this.m_sons[b].numLeaves(); 
    return i;
  }
  
  public int numNodes() {
    if (this.m_isLeaf)
      return 1; 
    int i = 1;
    for (byte b = 0; b < this.m_sons.length; b++)
      i += this.m_sons[b].numNodes(); 
    return i;
  }
  
  public String toString() {
    assignLeafModelNumbers(0);
    try {
      StringBuffer stringBuffer = new StringBuffer();
      if (this.m_isLeaf) {
        stringBuffer.append(": ");
        stringBuffer.append("LM_" + this.m_leafModelNum + ":" + getModelParameters());
      } else {
        dumpTree(0, stringBuffer);
      } 
      stringBuffer.append("\n\nNumber of Leaves  : \t" + numLeaves() + "\n");
      stringBuffer.append("\nSize of the Tree : \t" + numNodes() + "\n");
      stringBuffer.append(modelsToString());
      return stringBuffer.toString();
    } catch (Exception exception) {
      return "Can't print logistic model tree";
    } 
  }
  
  public String getModelParameters() {
    StringBuffer stringBuffer = new StringBuffer();
    int i = this.m_numRegressions + this.m_numHigherRegressions;
    stringBuffer.append(this.m_numRegressions + "/" + i + " (" + this.m_numInstances + ")");
    return stringBuffer.toString();
  }
  
  protected void dumpTree(int paramInt, StringBuffer paramStringBuffer) throws Exception {
    for (byte b = 0; b < this.m_sons.length; b++) {
      paramStringBuffer.append("\n");
      for (byte b1 = 0; b1 < paramInt; b1++)
        paramStringBuffer.append("|   "); 
      paramStringBuffer.append(this.m_localModel.leftSide(this.m_train));
      paramStringBuffer.append(this.m_localModel.rightSide(b, this.m_train));
      if ((this.m_sons[b]).m_isLeaf) {
        paramStringBuffer.append(": ");
        paramStringBuffer.append("LM_" + (this.m_sons[b]).m_leafModelNum + ":" + this.m_sons[b].getModelParameters());
      } else {
        this.m_sons[b].dumpTree(paramInt + 1, paramStringBuffer);
      } 
    } 
  }
  
  public int assignIDs(int paramInt) {
    int i = paramInt + 1;
    this.m_id = i;
    if (this.m_sons != null)
      for (byte b = 0; b < this.m_sons.length; b++)
        i = this.m_sons[b].assignIDs(i);  
    return i;
  }
  
  public int assignLeafModelNumbers(int paramInt) {
    if (!this.m_isLeaf) {
      this.m_leafModelNum = 0;
      for (byte b = 0; b < this.m_sons.length; b++)
        paramInt = this.m_sons[b].assignLeafModelNumbers(paramInt); 
    } else {
      this.m_leafModelNum = ++paramInt;
    } 
    return paramInt;
  }
  
  protected double[][] getCoefficients() {
    double[][] arrayOfDouble = super.getCoefficients();
    for (byte b = 0; b < this.m_numClasses; b++) {
      for (byte b1 = 0; b1 < this.m_numHigherRegressions; b1++) {
        double d1 = this.m_higherRegressions[b][b1].getSlope();
        double d2 = this.m_higherRegressions[b][b1].getIntercept();
        int i = this.m_higherRegressions[b][b1].getAttributeIndex();
        arrayOfDouble[b][0] = arrayOfDouble[b][0] + d2;
        arrayOfDouble[b][i + 1] = arrayOfDouble[b][i + 1] + d1;
      } 
    } 
    return arrayOfDouble;
  }
  
  public String modelsToString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_isLeaf) {
      stringBuffer.append("LM_" + this.m_leafModelNum + ":" + super.toString());
    } else {
      for (byte b = 0; b < this.m_sons.length; b++)
        stringBuffer.append("\n" + this.m_sons[b].modelsToString()); 
    } 
    return stringBuffer.toString();
  }
  
  public String graph() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    assignIDs(-1);
    assignLeafModelNumbers(0);
    stringBuffer.append("digraph LMTree {\n");
    if (this.m_isLeaf) {
      stringBuffer.append("N" + this.m_id + " [label=\"LM_" + this.m_leafModelNum + ":" + getModelParameters() + "\" " + "shape=box style=filled");
      stringBuffer.append("]\n");
    } else {
      stringBuffer.append("N" + this.m_id + " [label=\"" + this.m_localModel.leftSide(this.m_train) + "\" ");
      stringBuffer.append("]\n");
      graphTree(stringBuffer);
    } 
    return stringBuffer.toString() + "}\n";
  }
  
  private void graphTree(StringBuffer paramStringBuffer) throws Exception {
    for (byte b = 0; b < this.m_sons.length; b++) {
      paramStringBuffer.append("N" + this.m_id + "->" + "N" + (this.m_sons[b]).m_id + " [label=\"" + this.m_localModel.rightSide(b, this.m_train).trim() + "\"]\n");
      if ((this.m_sons[b]).m_isLeaf) {
        paramStringBuffer.append("N" + (this.m_sons[b]).m_id + " [label=\"LM_" + (this.m_sons[b]).m_leafModelNum + ":" + this.m_sons[b].getModelParameters() + "\" " + "shape=box style=filled");
        paramStringBuffer.append("]\n");
      } else {
        paramStringBuffer.append("N" + (this.m_sons[b]).m_id + " [label=\"" + (this.m_sons[b]).m_localModel.leftSide(this.m_train) + "\" ");
        paramStringBuffer.append("]\n");
        this.m_sons[b].graphTree(paramStringBuffer);
      } 
    } 
  }
  
  public void cleanup() {
    super.cleanup();
    if (!this.m_isLeaf)
      for (byte b = 0; b < this.m_sons.length; b++)
        this.m_sons[b].cleanup();  
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\lmt\LMTNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */