package weka.clusterers;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.estimators.DiscreteEstimator;
import weka.estimators.Estimator;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class EM extends DensityBasedClusterer implements NumberOfClustersRequestable, OptionHandler, WeightedInstancesHandler {
  private Estimator[][] m_model;
  
  private double[][][] m_modelNormal;
  
  private double m_minStdDev = 1.0E-6D;
  
  private double[][] m_weights;
  
  private double[] m_priors;
  
  private double m_loglikely;
  
  private Instances m_theInstances = null;
  
  private int m_num_clusters;
  
  private int m_initialNumClusters;
  
  private int m_num_attribs;
  
  private int m_num_instances;
  
  private int m_max_iterations;
  
  private double[] m_minValues;
  
  private double[] m_maxValues;
  
  private Random m_rr;
  
  private int m_rseed;
  
  private boolean m_verbose;
  
  private ReplaceMissingValues m_replaceMissing;
  
  private static double m_normConst = Math.log(Math.sqrt(6.283185307179586D));
  
  public String globalInfo() {
    return "Cluster data using expectation maximization";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tnumber of clusters. If omitted or\n\t-1 specified, then cross validation is used to\n\tselect the number of clusters.", "N", 1, "-N <num>"));
    vector.addElement(new Option("\tmax iterations.\n(default 100)", "I", 1, "-I <num>"));
    vector.addElement(new Option("\trandom number seed.\n(default 1)", "S", 1, "-S <num>"));
    vector.addElement(new Option("\tverbose.", "V", 0, "-V"));
    vector.addElement(new Option("\tminimum allowable standard deviation for normal density computation \n\t(default 1e-6)", "M", 1, "-M <num>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    setDebug(Utils.getFlag('V', paramArrayOfString));
    String str = Utils.getOption('I', paramArrayOfString);
    if (str.length() != 0)
      setMaxIterations(Integer.parseInt(str)); 
    str = Utils.getOption('N', paramArrayOfString);
    if (str.length() != 0)
      setNumClusters(Integer.parseInt(str)); 
    str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0)
      setSeed(Integer.parseInt(str)); 
    str = Utils.getOption('M', paramArrayOfString);
    if (str.length() != 0)
      setMinStdDev((new Double(str)).doubleValue()); 
  }
  
  public String minStdDevTipText() {
    return "set minimum allowable standard deviation";
  }
  
  public void setMinStdDev(double paramDouble) {
    this.m_minStdDev = paramDouble;
  }
  
  public double getMinStdDev() {
    return this.m_minStdDev;
  }
  
  public String seedTipText() {
    return "random number seed";
  }
  
  public void setSeed(int paramInt) {
    this.m_rseed = paramInt;
  }
  
  public int getSeed() {
    return this.m_rseed;
  }
  
  public String numClustersTipText() {
    return "set number of clusters. -1 to select number of clusters automatically by cross validation.";
  }
  
  public void setNumClusters(int paramInt) throws Exception {
    if (paramInt == 0)
      throw new Exception("Number of clusters must be > 0. (or -1 to select by cross validation)."); 
    if (paramInt < 0) {
      this.m_num_clusters = -1;
      this.m_initialNumClusters = -1;
    } else {
      this.m_num_clusters = paramInt;
      this.m_initialNumClusters = paramInt;
    } 
  }
  
  public int getNumClusters() {
    return this.m_initialNumClusters;
  }
  
  public String maxIterationsTipText() {
    return "maximum number of iterations";
  }
  
  public void setMaxIterations(int paramInt) throws Exception {
    if (paramInt < 1)
      throw new Exception("Maximum number of iterations must be > 0!"); 
    this.m_max_iterations = paramInt;
  }
  
  public int getMaxIterations() {
    return this.m_max_iterations;
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_verbose = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_verbose;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[9];
    byte b = 0;
    if (this.m_verbose)
      arrayOfString[b++] = "-V"; 
    arrayOfString[b++] = "-I";
    arrayOfString[b++] = "" + this.m_max_iterations;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + getNumClusters();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + this.m_rseed;
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getMinStdDev();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  private void EM_Init(Instances paramInstances) throws Exception {
    SimpleKMeans simpleKMeans = null;
    double d = Double.MAX_VALUE;
    byte b1;
    for (b1 = 0; b1 < 10; b1++) {
      SimpleKMeans simpleKMeans1 = new SimpleKMeans();
      simpleKMeans1.setSeed(this.m_rr.nextInt());
      simpleKMeans1.setNumClusters(this.m_num_clusters);
      simpleKMeans1.buildClusterer(paramInstances);
      if (simpleKMeans1.getSquaredError() < d) {
        d = simpleKMeans1.getSquaredError();
        simpleKMeans = simpleKMeans1;
      } 
    } 
    this.m_num_clusters = simpleKMeans.numberOfClusters();
    this.m_weights = new double[paramInstances.numInstances()][this.m_num_clusters];
    this.m_model = (Estimator[][])new DiscreteEstimator[this.m_num_clusters][this.m_num_attribs];
    this.m_modelNormal = new double[this.m_num_clusters][this.m_num_attribs][3];
    this.m_priors = new double[this.m_num_clusters];
    Instances instances1 = simpleKMeans.getClusterCentroids();
    Instances instances2 = simpleKMeans.getClusterStandardDevs();
    int[][][] arrayOfInt = simpleKMeans.getClusterNominalCounts();
    int[] arrayOfInt1 = simpleKMeans.getClusterSizes();
    for (b1 = 0; b1 < this.m_num_clusters; b1++) {
      Instance instance = instances1.instance(b1);
      for (byte b = 0; b < this.m_num_attribs; b++) {
        if (paramInstances.attribute(b).isNominal()) {
          this.m_model[b1][b] = (Estimator)new DiscreteEstimator(this.m_theInstances.attribute(b).numValues(), true);
          for (byte b3 = 0; b3 < paramInstances.attribute(b).numValues(); b3++)
            this.m_model[b1][b].addValue(b3, arrayOfInt[b1][b][b3]); 
        } else {
          double d1 = instance.isMissing(b) ? paramInstances.meanOrMode(b) : instance.value(b);
          this.m_modelNormal[b1][b][0] = d1;
          double d2 = instances2.instance(b1).isMissing(b) ? ((this.m_maxValues[b] - this.m_minValues[b]) / (2 * this.m_num_clusters)) : instances2.instance(b1).value(b);
          if (d2 < this.m_minStdDev) {
            d2 = (paramInstances.attributeStats(b)).numericStats.stdDev;
            if (d2 < this.m_minStdDev)
              d2 = this.m_minStdDev; 
          } 
          this.m_modelNormal[b1][b][1] = d2;
          this.m_modelNormal[b1][b][2] = 1.0D;
        } 
      } 
    } 
    for (byte b2 = 0; b2 < this.m_num_clusters; b2++)
      this.m_priors[b2] = arrayOfInt1[b2]; 
    Utils.normalize(this.m_priors);
  }
  
  private void estimate_priors(Instances paramInstances) throws Exception {
    byte b;
    for (b = 0; b < this.m_num_clusters; b++)
      this.m_priors[b] = 0.0D; 
    for (b = 0; b < paramInstances.numInstances(); b++) {
      for (byte b1 = 0; b1 < this.m_num_clusters; b1++)
        this.m_priors[b1] = this.m_priors[b1] + paramInstances.instance(b).weight() * this.m_weights[b][b1]; 
    } 
    Utils.normalize(this.m_priors);
  }
  
  private double logNormalDens(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d = paramDouble1 - paramDouble2;
    return -(d * d / 2.0D * paramDouble3 * paramDouble3) - m_normConst - Math.log(paramDouble3);
  }
  
  private void new_estimators() {
    for (byte b = 0; b < this.m_num_clusters; b++) {
      for (byte b1 = 0; b1 < this.m_num_attribs; b1++) {
        if (this.m_theInstances.attribute(b1).isNominal()) {
          this.m_model[b][b1] = (Estimator)new DiscreteEstimator(this.m_theInstances.attribute(b1).numValues(), true);
        } else {
          this.m_modelNormal[b][b1][2] = 0.0D;
          this.m_modelNormal[b][b1][1] = 0.0D;
          this.m_modelNormal[b][b1][0] = 0.0D;
        } 
      } 
    } 
  }
  
  private void M(Instances paramInstances) throws Exception {
    new_estimators();
    byte b1;
    for (b1 = 0; b1 < this.m_num_clusters; b1++) {
      for (byte b = 0; b < this.m_num_attribs; b++) {
        for (byte b3 = 0; b3 < paramInstances.numInstances(); b3++) {
          Instance instance = paramInstances.instance(b3);
          if (!instance.isMissing(b))
            if (paramInstances.attribute(b).isNominal()) {
              this.m_model[b1][b].addValue(instance.value(b), instance.weight() * this.m_weights[b3][b1]);
            } else {
              this.m_modelNormal[b1][b][0] = this.m_modelNormal[b1][b][0] + instance.value(b) * instance.weight() * this.m_weights[b3][b1];
              this.m_modelNormal[b1][b][2] = this.m_modelNormal[b1][b][2] + instance.weight() * this.m_weights[b3][b1];
              this.m_modelNormal[b1][b][1] = this.m_modelNormal[b1][b][1] + instance.value(b) * instance.value(b) * instance.weight() * this.m_weights[b3][b1];
            }  
        } 
      } 
    } 
    for (byte b2 = 0; b2 < this.m_num_attribs; b2++) {
      if (!paramInstances.attribute(b2).isNominal())
        for (b1 = 0; b1 < this.m_num_clusters; b1++) {
          if (this.m_modelNormal[b1][b2][2] <= 0.0D) {
            this.m_modelNormal[b1][b2][1] = Double.MAX_VALUE;
            this.m_modelNormal[b1][b2][0] = this.m_minStdDev;
          } else {
            this.m_modelNormal[b1][b2][1] = (this.m_modelNormal[b1][b2][1] - this.m_modelNormal[b1][b2][0] * this.m_modelNormal[b1][b2][0] / this.m_modelNormal[b1][b2][2]) / this.m_modelNormal[b1][b2][2];
            if (this.m_modelNormal[b1][b2][1] < 0.0D)
              this.m_modelNormal[b1][b2][1] = 0.0D; 
            this.m_modelNormal[b1][b2][1] = Math.sqrt(this.m_modelNormal[b1][b2][1]);
            if (this.m_modelNormal[b1][b2][1] <= this.m_minStdDev) {
              this.m_modelNormal[b1][b2][1] = (paramInstances.attributeStats(b2)).numericStats.stdDev;
              if (this.m_modelNormal[b1][b2][1] <= this.m_minStdDev)
                this.m_modelNormal[b1][b2][1] = this.m_minStdDev; 
            } 
            this.m_modelNormal[b1][b2][0] = this.m_modelNormal[b1][b2][0] / this.m_modelNormal[b1][b2][2];
          } 
        }  
    } 
  }
  
  private double E(Instances paramInstances, boolean paramBoolean) throws Exception {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = paramInstances.instance(b);
      d1 += instance.weight() * logDensityForInstance(instance);
      d2 += instance.weight();
      if (paramBoolean)
        this.m_weights[b] = distributionForInstance(instance); 
    } 
    if (paramBoolean)
      estimate_priors(paramInstances); 
    return d1 / d2;
  }
  
  public EM() {
    resetOptions();
  }
  
  protected void resetOptions() {
    this.m_minStdDev = 1.0E-6D;
    this.m_max_iterations = 100;
    this.m_rseed = 100;
    this.m_num_clusters = -1;
    this.m_initialNumClusters = -1;
    this.m_verbose = false;
  }
  
  public double[][][] getClusterModelsNumericAtts() {
    return this.m_modelNormal;
  }
  
  public double[] getClusterPriors() {
    return this.m_priors;
  }
  
  public String toString() {
    if (this.m_priors == null)
      return "No clusterer built yet!"; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\nEM\n==\n");
    if (this.m_initialNumClusters == -1) {
      stringBuffer.append("\nNumber of clusters selected by cross validation: " + this.m_num_clusters + "\n");
    } else {
      stringBuffer.append("\nNumber of clusters: " + this.m_num_clusters + "\n");
    } 
    for (byte b = 0; b < this.m_num_clusters; b++) {
      stringBuffer.append("\nCluster: " + b + " Prior probability: " + Utils.doubleToString(this.m_priors[b], 4) + "\n\n");
      for (byte b1 = 0; b1 < this.m_num_attribs; b1++) {
        stringBuffer.append("Attribute: " + this.m_theInstances.attribute(b1).name() + "\n");
        if (this.m_theInstances.attribute(b1).isNominal()) {
          if (this.m_model[b][b1] != null)
            stringBuffer.append(this.m_model[b][b1].toString()); 
        } else {
          stringBuffer.append("Normal Distribution. Mean = " + Utils.doubleToString(this.m_modelNormal[b][b1][0], 4) + " StdDev = " + Utils.doubleToString(this.m_modelNormal[b][b1][1], 4) + "\n");
        } 
      } 
    } 
    return stringBuffer.toString();
  }
  
  private void EM_Report(Instances paramInstances) {
    System.out.println("======================================");
    byte b1;
    for (b1 = 0; b1 < this.m_num_clusters; b1++) {
      for (byte b = 0; b < this.m_num_attribs; b++) {
        System.out.println("Clust: " + b1 + " att: " + b + "\n");
        if (this.m_theInstances.attribute(b).isNominal()) {
          if (this.m_model[b1][b] != null)
            System.out.println(this.m_model[b1][b].toString()); 
        } else {
          System.out.println("Normal Distribution. Mean = " + Utils.doubleToString(this.m_modelNormal[b1][b][0], 8, 4) + " StandardDev = " + Utils.doubleToString(this.m_modelNormal[b1][b][1], 8, 4) + " WeightSum = " + Utils.doubleToString(this.m_modelNormal[b1][b][2], 8, 4));
        } 
      } 
    } 
    for (byte b2 = 0; b2 < paramInstances.numInstances(); b2++) {
      int i = Utils.maxIndex(this.m_weights[b2]);
      System.out.print("Inst " + Utils.doubleToString(b2, 5, 0) + " Class " + i + "\t");
      for (b1 = 0; b1 < this.m_num_clusters; b1++)
        System.out.print(Utils.doubleToString(this.m_weights[b2][b1], 7, 5) + "  "); 
      System.out.println();
    } 
  }
  
  private void CVClusters() throws Exception {
    double d = -1.7976931348623157E308D;
    boolean bool = true;
    this.m_num_clusters = 1;
    byte b = (this.m_theInstances.numInstances() < 10) ? this.m_theInstances.numInstances() : 10;
    while (bool) {
      bool = false;
      Random random = new Random(this.m_rseed);
      Instances instances = new Instances(this.m_theInstances);
      instances.randomize(random);
      double d1 = 0.0D;
      for (byte b1 = 0; b1 < b; b1++) {
        Instances instances1 = instances.trainCV(b, b1, random);
        Instances instances2 = instances.testCV(b, b1);
        this.m_rr = new Random(this.m_rseed);
        EM_Init(instances1);
        iterate(instances1, false);
        double d2 = E(instances2, false);
        if (this.m_verbose)
          System.out.println("# clust: " + this.m_num_clusters + " Fold: " + b1 + " Loglikely: " + d2); 
        d1 += d2;
      } 
      d1 /= b;
      if (this.m_verbose)
        System.out.println("=================================================\n# clust: " + this.m_num_clusters + " Mean Loglikely: " + d1 + "\n================================" + "================="); 
      if (d1 > d) {
        d = d1;
        bool = true;
        this.m_num_clusters++;
      } 
    } 
    if (this.m_verbose)
      System.out.println("Number of clusters: " + (this.m_num_clusters - 1)); 
    this.m_num_clusters--;
  }
  
  public int numberOfClusters() throws Exception {
    if (this.m_num_clusters == -1)
      throw new Exception("Haven't generated any clusters!"); 
    return this.m_num_clusters;
  }
  
  private void updateMinMax(Instance paramInstance) {
    for (byte b = 0; b < this.m_theInstances.numAttributes(); b++) {
      if (!paramInstance.isMissing(b))
        if (Double.isNaN(this.m_minValues[b])) {
          this.m_minValues[b] = paramInstance.value(b);
          this.m_maxValues[b] = paramInstance.value(b);
        } else if (paramInstance.value(b) < this.m_minValues[b]) {
          this.m_minValues[b] = paramInstance.value(b);
        } else if (paramInstance.value(b) > this.m_maxValues[b]) {
          this.m_maxValues[b] = paramInstance.value(b);
        }  
    } 
  }
  
  public void buildClusterer(Instances paramInstances) throws Exception {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual checkForStringAttributes : ()Z
    //   4: ifeq -> 17
    //   7: new java/lang/Exception
    //   10: dup
    //   11: ldc 'Can't handle string attributes!'
    //   13: invokespecial <init> : (Ljava/lang/String;)V
    //   16: athrow
    //   17: aload_0
    //   18: new weka/filters/unsupervised/attribute/ReplaceMissingValues
    //   21: dup
    //   22: invokespecial <init> : ()V
    //   25: putfield m_replaceMissing : Lweka/filters/unsupervised/attribute/ReplaceMissingValues;
    //   28: aload_0
    //   29: getfield m_replaceMissing : Lweka/filters/unsupervised/attribute/ReplaceMissingValues;
    //   32: aload_1
    //   33: invokevirtual setInputFormat : (Lweka/core/Instances;)Z
    //   36: pop
    //   37: aload_1
    //   38: aload_0
    //   39: getfield m_replaceMissing : Lweka/filters/unsupervised/attribute/ReplaceMissingValues;
    //   42: invokestatic useFilter : (Lweka/core/Instances;Lweka/filters/Filter;)Lweka/core/Instances;
    //   45: astore_1
    //   46: aload_0
    //   47: aload_1
    //   48: putfield m_theInstances : Lweka/core/Instances;
    //   51: aload_0
    //   52: aload_0
    //   53: getfield m_theInstances : Lweka/core/Instances;
    //   56: invokevirtual numAttributes : ()I
    //   59: newarray double
    //   61: putfield m_minValues : [D
    //   64: aload_0
    //   65: aload_0
    //   66: getfield m_theInstances : Lweka/core/Instances;
    //   69: invokevirtual numAttributes : ()I
    //   72: newarray double
    //   74: putfield m_maxValues : [D
    //   77: iconst_0
    //   78: istore_2
    //   79: iload_2
    //   80: aload_0
    //   81: getfield m_theInstances : Lweka/core/Instances;
    //   84: invokevirtual numAttributes : ()I
    //   87: if_icmpge -> 112
    //   90: aload_0
    //   91: getfield m_minValues : [D
    //   94: iload_2
    //   95: aload_0
    //   96: getfield m_maxValues : [D
    //   99: iload_2
    //   100: ldc2_w NaN
    //   103: dup2_x2
    //   104: dastore
    //   105: dastore
    //   106: iinc #2, 1
    //   109: goto -> 79
    //   112: iconst_0
    //   113: istore_2
    //   114: iload_2
    //   115: aload_0
    //   116: getfield m_theInstances : Lweka/core/Instances;
    //   119: invokevirtual numInstances : ()I
    //   122: if_icmpge -> 143
    //   125: aload_0
    //   126: aload_0
    //   127: getfield m_theInstances : Lweka/core/Instances;
    //   130: iload_2
    //   131: invokevirtual instance : (I)Lweka/core/Instance;
    //   134: invokespecial updateMinMax : (Lweka/core/Instance;)V
    //   137: iinc #2, 1
    //   140: goto -> 114
    //   143: aload_0
    //   144: invokespecial doEM : ()V
    //   147: aload_0
    //   148: new weka/core/Instances
    //   151: dup
    //   152: aload_0
    //   153: getfield m_theInstances : Lweka/core/Instances;
    //   156: iconst_0
    //   157: invokespecial <init> : (Lweka/core/Instances;I)V
    //   160: putfield m_theInstances : Lweka/core/Instances;
    //   163: return
  }
  
  public double[] clusterPriors() {
    double[] arrayOfDouble = new double[this.m_priors.length];
    System.arraycopy(this.m_priors, 0, arrayOfDouble, 0, arrayOfDouble.length);
    return arrayOfDouble;
  }
  
  public double[] logDensityPerClusterForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = new double[this.m_num_clusters];
    this.m_replaceMissing.input(paramInstance);
    paramInstance = this.m_replaceMissing.output();
    for (byte b = 0; b < this.m_num_clusters; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < this.m_num_attribs; b1++) {
        if (!paramInstance.isMissing(b1))
          if (paramInstance.attribute(b1).isNominal()) {
            d += Math.log(this.m_model[b][b1].getProbability(paramInstance.value(b1)));
          } else {
            d += logNormalDens(paramInstance.value(b1), this.m_modelNormal[b][b1][0], this.m_modelNormal[b][b1][1]);
          }  
      } 
      arrayOfDouble[b] = d;
    } 
    return arrayOfDouble;
  }
  
  private void doEM() throws Exception {
    if (this.m_verbose)
      System.out.println("Seed: " + this.m_rseed); 
    this.m_rr = new Random(this.m_rseed);
    byte b;
    for (b = 0; b < 10; b++)
      this.m_rr.nextDouble(); 
    this.m_num_instances = this.m_theInstances.numInstances();
    this.m_num_attribs = this.m_theInstances.numAttributes();
    if (this.m_verbose)
      System.out.println("Number of instances: " + this.m_num_instances + "\nNumber of atts: " + this.m_num_attribs + "\n"); 
    if (this.m_initialNumClusters == -1)
      if (this.m_theInstances.numInstances() > 9) {
        CVClusters();
        this.m_rr = new Random(this.m_rseed);
        for (b = 0; b < 10; b++)
          this.m_rr.nextDouble(); 
      } else {
        this.m_num_clusters = 1;
      }  
    EM_Init(this.m_theInstances);
    this.m_loglikely = iterate(this.m_theInstances, this.m_verbose);
  }
  
  private double iterate(Instances paramInstances, boolean paramBoolean) throws Exception {
    double d1 = 0.0D;
    double d2 = 0.0D;
    if (paramBoolean)
      EM_Report(paramInstances); 
    for (byte b = 0; b < this.m_max_iterations; b++) {
      d1 = d2;
      d2 = E(paramInstances, true);
      if (paramBoolean)
        System.out.println("Loglikely: " + d2); 
      if (b > 0 && d2 - d1 < 1.0E-6D)
        break; 
      M(paramInstances);
    } 
    if (paramBoolean)
      EM_Report(paramInstances); 
    return d2;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(ClusterEvaluation.evaluateClusterer(new EM(), paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\clusterers\EM.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */