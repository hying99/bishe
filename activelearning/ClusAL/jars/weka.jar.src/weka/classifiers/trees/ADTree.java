package weka.classifiers.trees;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.IterativeClassifier;
import weka.classifiers.trees.adtree.PredictionNode;
import weka.classifiers.trees.adtree.ReferenceInstances;
import weka.classifiers.trees.adtree.Splitter;
import weka.classifiers.trees.adtree.TwoWayNominalSplit;
import weka.classifiers.trees.adtree.TwoWayNumericSplit;
import weka.core.AdditionalMeasureProducer;
import weka.core.Attribute;
import weka.core.Drawable;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.SerializedObject;
import weka.core.Tag;
import weka.core.UnassignedClassException;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class ADTree extends Classifier implements OptionHandler, Drawable, AdditionalMeasureProducer, WeightedInstancesHandler, IterativeClassifier {
  public static final int SEARCHPATH_ALL = 0;
  
  public static final int SEARCHPATH_HEAVIEST = 1;
  
  public static final int SEARCHPATH_ZPURE = 2;
  
  public static final int SEARCHPATH_RANDOM = 3;
  
  public static final Tag[] TAGS_SEARCHPATH = new Tag[] { new Tag(0, "Expand all paths"), new Tag(1, "Expand the heaviest path"), new Tag(2, "Expand the best z-pure path"), new Tag(3, "Expand a random path") };
  
  protected Instances m_trainInstances;
  
  protected PredictionNode m_root = null;
  
  protected Random m_random = null;
  
  protected int m_lastAddedSplitNum = 0;
  
  protected int[] m_numericAttIndices;
  
  protected int[] m_nominalAttIndices;
  
  protected double m_trainTotalWeight;
  
  protected ReferenceInstances m_posTrainInstances;
  
  protected ReferenceInstances m_negTrainInstances;
  
  protected PredictionNode m_search_bestInsertionNode;
  
  protected Splitter m_search_bestSplitter;
  
  protected double m_search_smallestZ;
  
  protected Instances m_search_bestPathPosInstances;
  
  protected Instances m_search_bestPathNegInstances;
  
  protected int m_nodesExpanded = 0;
  
  protected int m_examplesCounted = 0;
  
  protected int m_boostingIterations = 10;
  
  protected int m_searchPath = 0;
  
  protected int m_randomSeed = 0;
  
  protected boolean m_saveInstanceData = false;
  
  public String globalInfo() {
    return "Class for generating an alternating decision tree. The basic algorithm is based on:\n\nFreund, Y., Mason, L.: \"The alternating decision tree learning algorithm\". Proceeding of the Sixteenth International Conference on Machine Learning, Bled, Slovenia, (1999) 124-133.\n\nThis version currently only supports two-class problems. The number of boosting iterations needs to be manually tuned to suit the dataset and the desired complexity/accuracy tradeoff. Induction of the trees has been optimized, and heuristic search methods have been introduced to speed learning.";
  }
  
  public void initClassifier(Instances paramInstances) throws Exception {
    this.m_nodesExpanded = 0;
    this.m_examplesCounted = 0;
    this.m_lastAddedSplitNum = 0;
    if (paramInstances.classIndex() < 0)
      throw new UnassignedClassException("ADTree: Needs a class to be assigned"); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("ADTree: Can't handle string attributes"); 
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("ADTree: Class must be nominal"); 
    if (paramInstances.numClasses() != 2)
      throw new UnsupportedClassTypeException("ADTree: Must be a two-class problem"); 
    this.m_random = new Random(this.m_randomSeed);
    this.m_trainInstances = new Instances(paramInstances);
    this.m_trainInstances.deleteWithMissingClass();
    this.m_posTrainInstances = new ReferenceInstances(this.m_trainInstances, this.m_trainInstances.numInstances());
    this.m_negTrainInstances = new ReferenceInstances(this.m_trainInstances, this.m_trainInstances.numInstances());
    Enumeration enumeration = this.m_trainInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if ((int)instance.classValue() == 0) {
        this.m_negTrainInstances.addReference(instance);
        continue;
      } 
      this.m_posTrainInstances.addReference(instance);
    } 
    this.m_posTrainInstances.compactify();
    this.m_negTrainInstances.compactify();
    double d = calcPredictionValue((Instances)this.m_posTrainInstances, (Instances)this.m_negTrainInstances);
    this.m_root = new PredictionNode(d);
    updateWeights((Instances)this.m_posTrainInstances, (Instances)this.m_negTrainInstances, d);
    generateAttributeIndicesSingle();
  }
  
  public void next(int paramInt) throws Exception {
    boost();
  }
  
  public void boost() throws Exception {
    if (this.m_trainInstances == null || this.m_trainInstances.numInstances() == 0)
      throw new Exception("Trying to boost with no training data"); 
    searchForBestTestSingle();
    if (this.m_search_bestSplitter == null)
      return; 
    for (byte b = 0; b < 2; b++) {
      ReferenceInstances referenceInstances1 = this.m_search_bestSplitter.instancesDownBranch(b, this.m_search_bestPathPosInstances);
      ReferenceInstances referenceInstances2 = this.m_search_bestSplitter.instancesDownBranch(b, this.m_search_bestPathNegInstances);
      double d = calcPredictionValue((Instances)referenceInstances1, (Instances)referenceInstances2);
      PredictionNode predictionNode = new PredictionNode(d);
      updateWeights((Instances)referenceInstances1, (Instances)referenceInstances2, d);
      this.m_search_bestSplitter.setChildForBranch(b, predictionNode);
    } 
    this.m_search_bestInsertionNode.addChild(this.m_search_bestSplitter, this);
    this.m_search_bestPathPosInstances = null;
    this.m_search_bestPathNegInstances = null;
    this.m_search_bestSplitter = null;
  }
  
  private void generateAttributeIndicesSingle() {
    FastVector fastVector1 = new FastVector();
    FastVector fastVector2 = new FastVector();
    byte b;
    for (b = 0; b < this.m_trainInstances.numAttributes(); b++) {
      if (b != this.m_trainInstances.classIndex())
        if (this.m_trainInstances.attribute(b).isNumeric()) {
          fastVector2.addElement(new Integer(b));
        } else {
          fastVector1.addElement(new Integer(b));
        }  
    } 
    this.m_nominalAttIndices = new int[fastVector1.size()];
    for (b = 0; b < fastVector1.size(); b++)
      this.m_nominalAttIndices[b] = ((Integer)fastVector1.elementAt(b)).intValue(); 
    this.m_numericAttIndices = new int[fastVector2.size()];
    for (b = 0; b < fastVector2.size(); b++)
      this.m_numericAttIndices[b] = ((Integer)fastVector2.elementAt(b)).intValue(); 
  }
  
  private void searchForBestTestSingle() throws Exception {
    this.m_trainTotalWeight = this.m_trainInstances.sumOfWeights();
    this.m_search_smallestZ = Double.POSITIVE_INFINITY;
    searchForBestTestSingle(this.m_root, (Instances)this.m_posTrainInstances, (Instances)this.m_negTrainInstances);
  }
  
  private void searchForBestTestSingle(PredictionNode paramPredictionNode, Instances paramInstances1, Instances paramInstances2) throws Exception {
    if (paramInstances1.numInstances() == 0 || paramInstances2.numInstances() == 0)
      return; 
    if (calcZpure(paramInstances1, paramInstances2) >= this.m_search_smallestZ)
      return; 
    this.m_nodesExpanded++;
    this.m_examplesCounted += paramInstances1.numInstances() + paramInstances2.numInstances();
    for (byte b = 0; b < this.m_nominalAttIndices.length; b++)
      evaluateNominalSplitSingle(this.m_nominalAttIndices[b], paramPredictionNode, paramInstances1, paramInstances2); 
    if (this.m_numericAttIndices.length > 0) {
      Instances instances = new Instances(paramInstances1);
      Enumeration enumeration = paramInstances2.enumerateInstances();
      while (enumeration.hasMoreElements())
        instances.add(enumeration.nextElement()); 
      for (byte b1 = 0; b1 < this.m_numericAttIndices.length; b1++)
        evaluateNumericSplitSingle(this.m_numericAttIndices[b1], paramPredictionNode, paramInstances1, paramInstances2, instances); 
    } 
    if (paramPredictionNode.getChildren().size() == 0)
      return; 
    switch (this.m_searchPath) {
      case 0:
        goDownAllPathsSingle(paramPredictionNode, paramInstances1, paramInstances2);
        break;
      case 1:
        goDownHeaviestPathSingle(paramPredictionNode, paramInstances1, paramInstances2);
        break;
      case 2:
        goDownZpurePathSingle(paramPredictionNode, paramInstances1, paramInstances2);
        break;
      case 3:
        goDownRandomPathSingle(paramPredictionNode, paramInstances1, paramInstances2);
        break;
    } 
  }
  
  private void goDownAllPathsSingle(PredictionNode paramPredictionNode, Instances paramInstances1, Instances paramInstances2) throws Exception {
    Enumeration enumeration = paramPredictionNode.children();
    while (enumeration.hasMoreElements()) {
      Splitter splitter = enumeration.nextElement();
      for (byte b = 0; b < splitter.getNumOfBranches(); b++)
        searchForBestTestSingle(splitter.getChildForBranch(b), (Instances)splitter.instancesDownBranch(b, paramInstances1), (Instances)splitter.instancesDownBranch(b, paramInstances2)); 
    } 
  }
  
  private void goDownHeaviestPathSingle(PredictionNode paramPredictionNode, Instances paramInstances1, Instances paramInstances2) throws Exception {
    Splitter splitter = null;
    byte b = 0;
    double d = 0.0D;
    Enumeration enumeration = paramPredictionNode.children();
    while (enumeration.hasMoreElements()) {
      Splitter splitter1 = enumeration.nextElement();
      for (byte b1 = 0; b1 < splitter1.getNumOfBranches(); b1++) {
        double d1 = splitter1.instancesDownBranch(b1, paramInstances1).sumOfWeights() + splitter1.instancesDownBranch(b1, paramInstances2).sumOfWeights();
        if (d1 > d) {
          splitter = splitter1;
          b = b1;
          d = d1;
        } 
      } 
    } 
    if (splitter != null)
      searchForBestTestSingle(splitter.getChildForBranch(b), (Instances)splitter.instancesDownBranch(b, paramInstances1), (Instances)splitter.instancesDownBranch(b, paramInstances2)); 
  }
  
  private void goDownZpurePathSingle(PredictionNode paramPredictionNode, Instances paramInstances1, Instances paramInstances2) throws Exception {
    double d = this.m_search_smallestZ;
    PredictionNode predictionNode = null;
    ReferenceInstances referenceInstances1 = null;
    ReferenceInstances referenceInstances2 = null;
    Enumeration enumeration = paramPredictionNode.children();
    while (enumeration.hasMoreElements()) {
      Splitter splitter = enumeration.nextElement();
      for (byte b = 0; b < splitter.getNumOfBranches(); b++) {
        ReferenceInstances referenceInstances3 = splitter.instancesDownBranch(b, paramInstances1);
        ReferenceInstances referenceInstances4 = splitter.instancesDownBranch(b, paramInstances2);
        double d1 = calcZpure((Instances)referenceInstances3, (Instances)referenceInstances4);
        if (d1 < d) {
          d = d1;
          predictionNode = splitter.getChildForBranch(b);
          referenceInstances1 = referenceInstances3;
          referenceInstances2 = referenceInstances4;
        } 
      } 
    } 
    if (predictionNode != null)
      searchForBestTestSingle(predictionNode, (Instances)referenceInstances1, (Instances)referenceInstances2); 
  }
  
  private void goDownRandomPathSingle(PredictionNode paramPredictionNode, Instances paramInstances1, Instances paramInstances2) throws Exception {
    FastVector fastVector = paramPredictionNode.getChildren();
    Splitter splitter = (Splitter)fastVector.elementAt(getRandom(fastVector.size()));
    int i = getRandom(splitter.getNumOfBranches());
    searchForBestTestSingle(splitter.getChildForBranch(i), (Instances)splitter.instancesDownBranch(i, paramInstances1), (Instances)splitter.instancesDownBranch(i, paramInstances2));
  }
  
  private void evaluateNominalSplitSingle(int paramInt, PredictionNode paramPredictionNode, Instances paramInstances1, Instances paramInstances2) {
    double[] arrayOfDouble = findLowestZNominalSplit(paramInstances1, paramInstances2, paramInt);
    if (arrayOfDouble[1] < this.m_search_smallestZ) {
      this.m_search_smallestZ = arrayOfDouble[1];
      this.m_search_bestInsertionNode = paramPredictionNode;
      this.m_search_bestSplitter = (Splitter)new TwoWayNominalSplit(paramInt, (int)arrayOfDouble[0]);
      this.m_search_bestPathPosInstances = paramInstances1;
      this.m_search_bestPathNegInstances = paramInstances2;
    } 
  }
  
  private void evaluateNumericSplitSingle(int paramInt, PredictionNode paramPredictionNode, Instances paramInstances1, Instances paramInstances2, Instances paramInstances3) throws Exception {
    double[] arrayOfDouble = findLowestZNumericSplit(paramInstances3, paramInt);
    if (arrayOfDouble[1] < this.m_search_smallestZ) {
      this.m_search_smallestZ = arrayOfDouble[1];
      this.m_search_bestInsertionNode = paramPredictionNode;
      this.m_search_bestSplitter = (Splitter)new TwoWayNumericSplit(paramInt, arrayOfDouble[0]);
      this.m_search_bestPathPosInstances = paramInstances1;
      this.m_search_bestPathNegInstances = paramInstances2;
    } 
  }
  
  private double calcPredictionValue(Instances paramInstances1, Instances paramInstances2) {
    return 0.5D * Math.log((paramInstances1.sumOfWeights() + 1.0D) / (paramInstances2.sumOfWeights() + 1.0D));
  }
  
  private double calcZpure(Instances paramInstances1, Instances paramInstances2) {
    double d1 = paramInstances1.sumOfWeights();
    double d2 = paramInstances2.sumOfWeights();
    return 2.0D * (Math.sqrt(d1 + 1.0D) + Math.sqrt(d2 + 1.0D)) + this.m_trainTotalWeight - d1 + d2;
  }
  
  private void updateWeights(Instances paramInstances1, Instances paramInstances2, double paramDouble) {
    double d = Math.pow(Math.E, -paramDouble);
    Enumeration enumeration = paramInstances1.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      instance.setWeight(instance.weight() * d);
    } 
    d = Math.pow(Math.E, paramDouble);
    enumeration = paramInstances2.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      instance.setWeight(instance.weight() * d);
    } 
  }
  
  private double[] findLowestZNominalSplit(Instances paramInstances1, Instances paramInstances2, int paramInt) {
    double d1 = Double.MAX_VALUE;
    byte b1 = 0;
    double[] arrayOfDouble1 = attributeValueWeights(paramInstances1, paramInt);
    double[] arrayOfDouble2 = attributeValueWeights(paramInstances2, paramInt);
    double d2 = Utils.sum(arrayOfDouble1);
    double d3 = Utils.sum(arrayOfDouble2);
    int i = arrayOfDouble1.length;
    if (i == 2)
      i = 1; 
    for (byte b2 = 0; b2 < i; b2++) {
      double d4 = arrayOfDouble1[b2] + 1.0D;
      double d5 = arrayOfDouble2[b2] + 1.0D;
      double d6 = d2 - d4 + 2.0D;
      double d7 = d3 - d5 + 2.0D;
      double d8 = this.m_trainTotalWeight + 4.0D - d4 + d5 + d6 + d7;
      double d9 = 2.0D * (Math.sqrt(d4 * d5) + Math.sqrt(d6 * d7)) + d8;
      if (d9 < d1) {
        d1 = d9;
        b1 = b2;
      } 
    } 
    double[] arrayOfDouble3 = new double[2];
    arrayOfDouble3[0] = b1;
    arrayOfDouble3[1] = d1;
    return arrayOfDouble3;
  }
  
  private double[] attributeValueWeights(Instances paramInstances, int paramInt) {
    double[] arrayOfDouble = new double[paramInstances.attribute(paramInt).numValues()];
    for (byte b = 0; b < arrayOfDouble.length; b++)
      arrayOfDouble[b] = 0.0D; 
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if (!instance.isMissing(paramInt))
        arrayOfDouble[(int)instance.value(paramInt)] = arrayOfDouble[(int)instance.value(paramInt)] + instance.weight(); 
    } 
    return arrayOfDouble;
  }
  
  private double[] findLowestZNumericSplit(Instances paramInstances, int paramInt) throws Exception {
    double d1 = 0.0D;
    double d2 = Double.MAX_VALUE;
    byte b1 = 0;
    double[][] arrayOfDouble = new double[3][paramInstances.numClasses()];
    byte b2;
    for (b2 = 0; b2 < paramInstances.numInstances(); b2++) {
      Instance instance = paramInstances.instance(b2);
      if (!instance.isMissing(paramInt)) {
        arrayOfDouble[1][(int)instance.classValue()] = arrayOfDouble[1][(int)instance.classValue()] + instance.weight();
      } else {
        arrayOfDouble[2][(int)instance.classValue()] = arrayOfDouble[2][(int)instance.classValue()] + instance.weight();
        b1++;
      } 
    } 
    paramInstances.sort(paramInt);
    for (b2 = 0; b2 < paramInstances.numInstances() - b1 + 1; b2++) {
      Instance instance1 = paramInstances.instance(b2);
      Instance instance2 = paramInstances.instance(b2 + 1);
      arrayOfDouble[0][(int)instance1.classValue()] = arrayOfDouble[0][(int)instance1.classValue()] + instance1.weight();
      arrayOfDouble[1][(int)instance1.classValue()] = arrayOfDouble[1][(int)instance1.classValue()] - instance1.weight();
      if (Utils.sm(instance1.value(paramInt), instance2.value(paramInt))) {
        double d4 = (instance1.value(paramInt) + instance2.value(paramInt)) / 2.0D;
        double d3 = conditionedZOnRows(arrayOfDouble);
        if (d3 < d2) {
          d1 = d4;
          d2 = d3;
        } 
      } 
    } 
    double[] arrayOfDouble1 = new double[2];
    arrayOfDouble1[0] = d1;
    arrayOfDouble1[1] = d2;
    return arrayOfDouble1;
  }
  
  private double conditionedZOnRows(double[][] paramArrayOfdouble) {
    double d1 = paramArrayOfdouble[0][0] + 1.0D;
    double d2 = paramArrayOfdouble[0][1] + 1.0D;
    double d3 = paramArrayOfdouble[1][0] + 1.0D;
    double d4 = paramArrayOfdouble[1][1] + 1.0D;
    double d5 = this.m_trainTotalWeight + 4.0D - d1 + d2 + d3 + d4;
    return 2.0D * (Math.sqrt(d1 * d2) + Math.sqrt(d3 * d4)) + d5;
  }
  
  public double[] distributionForInstance(Instance paramInstance) {
    double d = predictionValueForInstance(paramInstance, this.m_root, 0.0D);
    double[] arrayOfDouble = new double[2];
    arrayOfDouble[0] = 1.0D / (1.0D + Math.pow(Math.E, d));
    arrayOfDouble[1] = 1.0D / (1.0D + Math.pow(Math.E, -d));
    return arrayOfDouble;
  }
  
  protected double predictionValueForInstance(Instance paramInstance, PredictionNode paramPredictionNode, double paramDouble) {
    paramDouble += paramPredictionNode.getValue();
    Enumeration enumeration = paramPredictionNode.children();
    while (enumeration.hasMoreElements()) {
      Splitter splitter = enumeration.nextElement();
      int i = splitter.branchInstanceGoesDown(paramInstance);
      if (i >= 0)
        paramDouble = predictionValueForInstance(paramInstance, splitter.getChildForBranch(i), paramDouble); 
    } 
    return paramDouble;
  }
  
  public String toString() {
    return (this.m_root == null) ? "ADTree not built yet" : ("Alternating decision tree:\n\n" + toString(this.m_root, 1) + "\nLegend: " + legend() + "\nTree size (total number of nodes): " + numOfAllNodes(this.m_root) + "\nLeaves (number of predictor nodes): " + numOfPredictionNodes(this.m_root));
  }
  
  protected String toString(PredictionNode paramPredictionNode, int paramInt) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(": " + Utils.doubleToString(paramPredictionNode.getValue(), 3));
    Enumeration enumeration = paramPredictionNode.children();
    while (enumeration.hasMoreElements()) {
      Splitter splitter = enumeration.nextElement();
      for (byte b = 0; b < splitter.getNumOfBranches(); b++) {
        PredictionNode predictionNode = splitter.getChildForBranch(b);
        if (predictionNode != null) {
          stringBuffer.append("\n");
          for (byte b1 = 0; b1 < paramInt; b1++)
            stringBuffer.append("|  "); 
          stringBuffer.append("(" + splitter.orderAdded + ")");
          stringBuffer.append(splitter.attributeString(this.m_trainInstances) + " " + splitter.comparisonString(b, this.m_trainInstances));
          stringBuffer.append(toString(predictionNode, paramInt + 1));
        } 
      } 
    } 
    return stringBuffer.toString();
  }
  
  public int graphType() {
    return 1;
  }
  
  public String graph() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("digraph ADTree {\n");
    graphTraverse(this.m_root, stringBuffer, 0, 0, this.m_trainInstances);
    return stringBuffer.toString() + "}\n";
  }
  
  protected void graphTraverse(PredictionNode paramPredictionNode, StringBuffer paramStringBuffer, int paramInt1, int paramInt2, Instances paramInstances) throws Exception {
    paramStringBuffer.append("S" + paramInt1 + "P" + paramInt2 + " [label=\"");
    paramStringBuffer.append(Utils.doubleToString(paramPredictionNode.getValue(), 3));
    if (paramInt1 == 0)
      paramStringBuffer.append(" (" + legend() + ")"); 
    paramStringBuffer.append("\" shape=box style=filled");
    if (paramInstances.numInstances() > 0)
      paramStringBuffer.append(" data=\n" + paramInstances + "\n,\n"); 
    paramStringBuffer.append("]\n");
    Enumeration enumeration = paramPredictionNode.children();
    while (enumeration.hasMoreElements()) {
      Splitter splitter = enumeration.nextElement();
      paramStringBuffer.append("S" + paramInt1 + "P" + paramInt2 + "->" + "S" + splitter.orderAdded + " [style=dotted]\n");
      paramStringBuffer.append("S" + splitter.orderAdded + " [label=\"" + splitter.orderAdded + ": " + splitter.attributeString(this.m_trainInstances) + "\"]\n");
      for (byte b = 0; b < splitter.getNumOfBranches(); b++) {
        PredictionNode predictionNode = splitter.getChildForBranch(b);
        if (predictionNode != null) {
          paramStringBuffer.append("S" + splitter.orderAdded + "->" + "S" + splitter.orderAdded + "P" + b + " [label=\"" + splitter.comparisonString(b, this.m_trainInstances) + "\"]\n");
          graphTraverse(predictionNode, paramStringBuffer, splitter.orderAdded, b, (Instances)splitter.instancesDownBranch(b, paramInstances));
        } 
      } 
    } 
  }
  
  public String legend() {
    Attribute attribute = null;
    if (this.m_trainInstances == null)
      return ""; 
    try {
      attribute = this.m_trainInstances.classAttribute();
    } catch (Exception exception) {}
    return "-ve = " + attribute.value(0) + ", +ve = " + attribute.value(1);
  }
  
  public String numOfBoostingIterationsTipText() {
    return "Sets the number of boosting iterations to perform. You will need to manually tune this parameter to suit the dataset and the desired complexity/accuracy tradeoff. More boosting iterations will result in larger (potentially more  accurate) trees, but will make learning slower. Each iteration will add 3 nodes (1 split + 2 prediction) to the tree unless merging occurs.";
  }
  
  public int getNumOfBoostingIterations() {
    return this.m_boostingIterations;
  }
  
  public void setNumOfBoostingIterations(int paramInt) {
    this.m_boostingIterations = paramInt;
  }
  
  public String searchPathTipText() {
    return "Sets the type of search to perform when building the tree. The default option (Expand all paths) will do an exhaustive search. The other search methods are heuristic, so they are not guaranteed to find an optimal solution but they are much faster. Expand the heaviest path: searches the path with the most heavily weighted instances. Expand the best z-pure path: searches the path determined by the best z-pure estimate. Expand a random path: the fastest method, simply searches down a single random path on each iteration.";
  }
  
  public SelectedTag getSearchPath() {
    return new SelectedTag(this.m_searchPath, TAGS_SEARCHPATH);
  }
  
  public void setSearchPath(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_SEARCHPATH)
      this.m_searchPath = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public String randomSeedTipText() {
    return "Sets the random seed to use for a random search.";
  }
  
  public int getRandomSeed() {
    return this.m_randomSeed;
  }
  
  public void setRandomSeed(int paramInt) {
    this.m_randomSeed = paramInt;
  }
  
  public String saveInstanceDataTipText() {
    return "Sets whether the tree is to save instance data - the model will take up more memory if it does. If enabled you will be able to visualize the instances at the prediction nodes when visualizing the tree.";
  }
  
  public boolean getSaveInstanceData() {
    return this.m_saveInstanceData;
  }
  
  public void setSaveInstanceData(boolean paramBoolean) {
    this.m_saveInstanceData = paramBoolean;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tNumber of boosting iterations.\n\t(Default = 10)", "B", 1, "-B <number of boosting iterations>"));
    vector.addElement(new Option("\tExpand nodes: -3(all), -2(weight), -1(z_pure), >=0 seed for random walk\n\t(Default = -3)", "E", 1, "-E <-3|-2|-1|>=0>"));
    vector.addElement(new Option("\tSave the instance data with the model", "D", 0, "-D"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('B', paramArrayOfString);
    if (str1.length() != 0)
      setNumOfBoostingIterations(Integer.parseInt(str1)); 
    String str2 = Utils.getOption('E', paramArrayOfString);
    if (str2.length() != 0) {
      int i = Integer.parseInt(str2);
      if (i >= 0) {
        setSearchPath(new SelectedTag(3, TAGS_SEARCHPATH));
        setRandomSeed(i);
      } else {
        setSearchPath(new SelectedTag(i + 3, TAGS_SEARCHPATH));
      } 
    } 
    setSaveInstanceData(Utils.getFlag('D', paramArrayOfString));
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[6];
    byte b = 0;
    arrayOfString[b++] = "-B";
    arrayOfString[b++] = "" + getNumOfBoostingIterations();
    arrayOfString[b++] = "-E";
    arrayOfString[b++] = "" + ((this.m_searchPath == 3) ? this.m_randomSeed : (this.m_searchPath - 3));
    if (getSaveInstanceData())
      arrayOfString[b++] = "-D"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public double measureTreeSize() {
    return numOfAllNodes(this.m_root);
  }
  
  public double measureNumLeaves() {
    return numOfPredictionNodes(this.m_root);
  }
  
  public double measureNumPredictionLeaves() {
    return numOfPredictionLeafNodes(this.m_root);
  }
  
  public double measureNodesExpanded() {
    return this.m_nodesExpanded;
  }
  
  public double measureExamplesProcessed() {
    return this.m_examplesCounted;
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(4);
    vector.addElement("measureTreeSize");
    vector.addElement("measureNumLeaves");
    vector.addElement("measureNumPredictionLeaves");
    vector.addElement("measureNodesExpanded");
    vector.addElement("measureExamplesProcessed");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.equalsIgnoreCase("measureTreeSize"))
      return measureTreeSize(); 
    if (paramString.equalsIgnoreCase("measureNumLeaves"))
      return measureNumLeaves(); 
    if (paramString.equalsIgnoreCase("measureNumPredictionLeaves"))
      return measureNumPredictionLeaves(); 
    if (paramString.equalsIgnoreCase("measureNodesExpanded"))
      return measureNodesExpanded(); 
    if (paramString.equalsIgnoreCase("measureExamplesProcessed"))
      return measureExamplesProcessed(); 
    throw new IllegalArgumentException(paramString + " not supported (ADTree)");
  }
  
  protected int numOfAllNodes(PredictionNode paramPredictionNode) {
    int i = 0;
    if (paramPredictionNode != null) {
      i++;
      Enumeration enumeration = paramPredictionNode.children();
      while (enumeration.hasMoreElements()) {
        i++;
        Splitter splitter = enumeration.nextElement();
        for (byte b = 0; b < splitter.getNumOfBranches(); b++)
          i += numOfAllNodes(splitter.getChildForBranch(b)); 
      } 
    } 
    return i;
  }
  
  protected int numOfPredictionNodes(PredictionNode paramPredictionNode) {
    int i = 0;
    if (paramPredictionNode != null) {
      i++;
      Enumeration enumeration = paramPredictionNode.children();
      while (enumeration.hasMoreElements()) {
        Splitter splitter = enumeration.nextElement();
        for (byte b = 0; b < splitter.getNumOfBranches(); b++)
          i += numOfPredictionNodes(splitter.getChildForBranch(b)); 
      } 
    } 
    return i;
  }
  
  protected int numOfPredictionLeafNodes(PredictionNode paramPredictionNode) {
    int i = 0;
    if (paramPredictionNode.getChildren().size() > 0) {
      Enumeration enumeration = paramPredictionNode.children();
      while (enumeration.hasMoreElements()) {
        Splitter splitter = enumeration.nextElement();
        for (byte b = 0; b < splitter.getNumOfBranches(); b++)
          i += numOfPredictionLeafNodes(splitter.getChildForBranch(b)); 
      } 
    } else {
      i = 1;
    } 
    return i;
  }
  
  protected int getRandom(int paramInt) {
    return this.m_random.nextInt(paramInt);
  }
  
  public int nextSplitAddedOrder() {
    return ++this.m_lastAddedSplitNum;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    initClassifier(paramInstances);
    for (byte b = 0; b < this.m_boostingIterations; b++)
      boost(); 
    if (!this.m_saveInstanceData)
      done(); 
  }
  
  public void done() {
    this.m_trainInstances = new Instances(this.m_trainInstances, 0);
    this.m_random = null;
    this.m_numericAttIndices = null;
    this.m_nominalAttIndices = null;
    this.m_posTrainInstances = null;
    this.m_negTrainInstances = null;
  }
  
  public Object clone() {
    ADTree aDTree = new ADTree();
    if (this.m_root != null) {
      aDTree.m_root = (PredictionNode)this.m_root.clone();
      aDTree.m_trainInstances = new Instances(this.m_trainInstances);
      if (this.m_random != null) {
        SerializedObject serializedObject = null;
        try {
          serializedObject = new SerializedObject(this.m_random);
        } catch (Exception exception) {}
        aDTree.m_random = (Random)serializedObject.getObject();
      } 
      aDTree.m_lastAddedSplitNum = this.m_lastAddedSplitNum;
      aDTree.m_numericAttIndices = this.m_numericAttIndices;
      aDTree.m_nominalAttIndices = this.m_nominalAttIndices;
      aDTree.m_trainTotalWeight = this.m_trainTotalWeight;
      if (this.m_posTrainInstances != null) {
        aDTree.m_posTrainInstances = new ReferenceInstances(this.m_trainInstances, this.m_posTrainInstances.numInstances());
        aDTree.m_negTrainInstances = new ReferenceInstances(this.m_trainInstances, this.m_negTrainInstances.numInstances());
        Enumeration enumeration = aDTree.m_trainInstances.enumerateInstances();
        while (enumeration.hasMoreElements()) {
          Instance instance = enumeration.nextElement();
          try {
            if ((int)instance.classValue() == 0) {
              aDTree.m_negTrainInstances.addReference(instance);
              continue;
            } 
            aDTree.m_posTrainInstances.addReference(instance);
          } catch (Exception exception) {}
        } 
      } 
    } 
    aDTree.m_nodesExpanded = this.m_nodesExpanded;
    aDTree.m_examplesCounted = this.m_examplesCounted;
    aDTree.m_boostingIterations = this.m_boostingIterations;
    aDTree.m_searchPath = this.m_searchPath;
    aDTree.m_randomSeed = this.m_randomSeed;
    return aDTree;
  }
  
  public void merge(ADTree paramADTree) throws Exception {
    if (this.m_root == null || paramADTree.m_root == null)
      throw new Exception("Trying to merge an uninitialized tree"); 
    this.m_root.merge(paramADTree.m_root, this);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new ADTree(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\ADTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */