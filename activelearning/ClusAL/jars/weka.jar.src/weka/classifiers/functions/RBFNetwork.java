package weka.classifiers.functions;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.clusterers.Clusterer;
import weka.clusterers.DensityBasedClusterer;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ClusterMembership;
import weka.filters.unsupervised.attribute.Standardize;

public class RBFNetwork extends Classifier implements OptionHandler {
  private Logistic m_logistic;
  
  private LinearRegression m_linear;
  
  private ClusterMembership m_basisFilter;
  
  private Standardize m_standardize;
  
  private int m_numClusters = 2;
  
  protected double m_ridge = 1.0E-8D;
  
  private int m_maxIts = -1;
  
  private int m_clusteringSeed = 1;
  
  private double m_minStdDev = 0.1D;
  
  public String globalInfo() {
    return "Class that implements a normalized Gaussian radial basisbasis function network. It uses the k-means clustering algorithm to provide the basis functions and learns either a logistic regression (discrete class problems) or linear regression (numeric class problems) on top of that. Symmetric multivariate Gaussians are fit to the data from each cluster. If the class is nominal it uses the given number of clusters per class.It standardizes all numeric attributes to zero mean and unit variance.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    if (paramInstances.numInstances() == 0)
      throw new Exception("No training instances without a missing class!"); 
    this.m_standardize = new Standardize();
    this.m_standardize.setInputFormat(paramInstances);
    paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_standardize);
    SimpleKMeans simpleKMeans = new SimpleKMeans();
    simpleKMeans.setNumClusters(this.m_numClusters);
    simpleKMeans.setSeed(this.m_clusteringSeed);
    MakeDensityBasedClusterer makeDensityBasedClusterer = new MakeDensityBasedClusterer();
    makeDensityBasedClusterer.setClusterer((Clusterer)simpleKMeans);
    makeDensityBasedClusterer.setMinStdDev(this.m_minStdDev);
    this.m_basisFilter = new ClusterMembership();
    this.m_basisFilter.setDensityBasedClusterer((DensityBasedClusterer)makeDensityBasedClusterer);
    this.m_basisFilter.setInputFormat(paramInstances);
    Instances instances = Filter.useFilter(paramInstances, (Filter)this.m_basisFilter);
    if (paramInstances.classAttribute().isNominal()) {
      this.m_linear = null;
      this.m_logistic = new Logistic();
      this.m_logistic.setRidge(this.m_ridge);
      this.m_logistic.setMaxIts(this.m_maxIts);
      this.m_logistic.buildClassifier(instances);
    } else {
      this.m_logistic = null;
      this.m_linear = new LinearRegression();
      this.m_linear.setAttributeSelectionMethod(new SelectedTag(1, LinearRegression.TAGS_SELECTION));
      this.m_linear.setRidge(this.m_ridge);
      this.m_linear.buildClassifier(instances);
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    this.m_standardize.input(paramInstance);
    this.m_basisFilter.input(this.m_standardize.output());
    Instance instance = this.m_basisFilter.output();
    return paramInstance.classAttribute().isNominal() ? this.m_logistic.distributionForInstance(instance) : this.m_linear.distributionForInstance(instance);
  }
  
  public String toString() {
    if (this.m_basisFilter == null)
      return "No classifier built yet!"; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Radial basis function network\n");
    stringBuffer.append((this.m_linear == null) ? "(Logistic regression " : "(Linear regression ");
    stringBuffer.append("applied to K-means clusters as basis functions):\n\n");
    stringBuffer.append((this.m_linear == null) ? this.m_logistic.toString() : this.m_linear.toString());
    return stringBuffer.toString();
  }
  
  public String maxItsTipText() {
    return "Maximum number of iterations for the logistic regression to perform. Only applied to discrete class problems.";
  }
  
  public int getMaxIts() {
    return this.m_maxIts;
  }
  
  public void setMaxIts(int paramInt) {
    this.m_maxIts = paramInt;
  }
  
  public String ridgeTipText() {
    return "Set the Ridge value for the logistic or linear regression.";
  }
  
  public void setRidge(double paramDouble) {
    this.m_ridge = paramDouble;
  }
  
  public double getRidge() {
    return this.m_ridge;
  }
  
  public String numClustersTipText() {
    return "The number of clusters for K-Means to generate.";
  }
  
  public void setNumClusters(int paramInt) {
    if (paramInt > 0)
      this.m_numClusters = paramInt; 
  }
  
  public int getNumClusters() {
    return this.m_numClusters;
  }
  
  public String clusteringSeedTipText() {
    return "The random seed to pass on to K-means.";
  }
  
  public void setClusteringSeed(int paramInt) {
    this.m_clusteringSeed = paramInt;
  }
  
  public int getClusteringSeed() {
    return this.m_clusteringSeed;
  }
  
  public String minStdDevTipText() {
    return "Sets the minimum standard deviation for the clusters.";
  }
  
  public double getMinStdDev() {
    return this.m_minStdDev;
  }
  
  public void setMinStdDev(double paramDouble) {
    this.m_minStdDev = paramDouble;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tSet the number of clusters (basis functions) to generate. (default = 2).", "B", 1, "-B <number>"));
    vector.addElement(new Option("\tSet the random seed to be used by K-means. (default = 1).", "S", 1, "-S <seed>"));
    vector.addElement(new Option("\tSet the ridge value for the logistic or linear regression.", "R", 1, "-R <ridge>"));
    vector.addElement(new Option("\tSet the maximum number of iterations for the logistic regression. (default -1, until convergence).", "M", 1, "-M <number>"));
    vector.addElement(new Option("\tSet the minimum standard deviation for the clusters. (default 0.1).", "W", 1, "-W <number>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setDebug(Utils.getFlag('D', paramArrayOfString));
    String str1 = Utils.getOption('R', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_ridge = Double.parseDouble(str1);
    } else {
      this.m_ridge = 1.0E-8D;
    } 
    String str2 = Utils.getOption('M', paramArrayOfString);
    if (str2.length() != 0) {
      this.m_maxIts = Integer.parseInt(str2);
    } else {
      this.m_maxIts = -1;
    } 
    String str3 = Utils.getOption('B', paramArrayOfString);
    if (str3.length() != 0)
      setNumClusters(Integer.parseInt(str3)); 
    String str4 = Utils.getOption('S', paramArrayOfString);
    if (str4.length() != 0)
      setClusteringSeed(Integer.parseInt(str4)); 
    String str5 = Utils.getOption('W', paramArrayOfString);
    if (str5.length() != 0)
      setMinStdDev(Double.parseDouble(str5)); 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[10];
    byte b = 0;
    arrayOfString[b++] = "-B";
    arrayOfString[b++] = "" + this.m_numClusters;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + this.m_clusteringSeed;
    arrayOfString[b++] = "-R";
    arrayOfString[b++] = "" + this.m_ridge;
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + this.m_maxIts;
    arrayOfString[b++] = "-W";
    arrayOfString[b++] = "" + this.m_minStdDev;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new RBFNetwork(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\RBFNetwork.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */