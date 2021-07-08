package weka.clusterers;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.estimators.DiscreteEstimator;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class MakeDensityBasedClusterer extends DensityBasedClusterer implements NumberOfClustersRequestable, OptionHandler, WeightedInstancesHandler {
  private Instances m_theInstances;
  
  private double[] m_priors;
  
  private double[][][] m_modelNormal;
  
  private DiscreteEstimator[][] m_model;
  
  private double m_minStdDev = 1.0E-6D;
  
  private Clusterer m_wrappedClusterer = new SimpleKMeans();
  
  private ReplaceMissingValues m_replaceMissing;
  
  private static double m_normConst = 0.5D * Math.log(6.283185307179586D);
  
  public MakeDensityBasedClusterer() {}
  
  public MakeDensityBasedClusterer(Clusterer paramClusterer) {
    setClusterer(paramClusterer);
  }
  
  public void setNumClusters(int paramInt) throws Exception {
    if (this.m_wrappedClusterer == null)
      throw new Exception("Can't set the number of clusters to generate - no clusterer has been set yet."); 
    if (!(this.m_wrappedClusterer instanceof NumberOfClustersRequestable))
      throw new Exception("Can't set the number of clusters to generate - wrapped clusterer does not support this facility."); 
    ((NumberOfClustersRequestable)this.m_wrappedClusterer).setNumClusters(paramInt);
  }
  
  public void buildClusterer(Instances paramInstances) throws Exception {
    this.m_replaceMissing = new ReplaceMissingValues();
    this.m_replaceMissing.setInputFormat(paramInstances);
    paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_replaceMissing);
    this.m_theInstances = new Instances(paramInstances, 0);
    if (this.m_wrappedClusterer == null)
      throw new Exception("No clusterer has been set"); 
    this.m_wrappedClusterer.buildClusterer(paramInstances);
    this.m_model = new DiscreteEstimator[this.m_wrappedClusterer.numberOfClusters()][paramInstances.numAttributes()];
    this.m_modelNormal = new double[this.m_wrappedClusterer.numberOfClusters()][paramInstances.numAttributes()][2];
    double[][] arrayOfDouble = new double[this.m_wrappedClusterer.numberOfClusters()][paramInstances.numAttributes()];
    this.m_priors = new double[this.m_wrappedClusterer.numberOfClusters()];
    for (byte b1 = 0; b1 < this.m_wrappedClusterer.numberOfClusters(); b1++) {
      for (byte b = 0; b < paramInstances.numAttributes(); b++) {
        if (paramInstances.attribute(b).isNominal())
          this.m_model[b1][b] = new DiscreteEstimator(paramInstances.attribute(b).numValues(), true); 
      } 
    } 
    Instance instance = null;
    int[] arrayOfInt = new int[paramInstances.numInstances()];
    byte b2;
    for (b2 = 0; b2 < paramInstances.numInstances(); b2++) {
      instance = paramInstances.instance(b2);
      int i = this.m_wrappedClusterer.clusterInstance(instance);
      this.m_priors[i] = this.m_priors[i] + instance.weight();
      for (byte b = 0; b < paramInstances.numAttributes(); b++) {
        if (!instance.isMissing(b))
          if (paramInstances.attribute(b).isNominal()) {
            this.m_model[i][b].addValue(instance.value(b), instance.weight());
          } else {
            this.m_modelNormal[i][b][0] = this.m_modelNormal[i][b][0] + instance.weight() * instance.value(b);
            arrayOfDouble[i][b] = arrayOfDouble[i][b] + instance.weight();
          }  
      } 
      arrayOfInt[b2] = i;
    } 
    for (b2 = 0; b2 < paramInstances.numAttributes(); b2++) {
      if (paramInstances.attribute(b2).isNumeric())
        for (byte b = 0; b < this.m_wrappedClusterer.numberOfClusters(); b++) {
          if (arrayOfDouble[b][b2] > 0.0D)
            this.m_modelNormal[b][b2][0] = this.m_modelNormal[b][b2][0] / arrayOfDouble[b][b2]; 
        }  
    } 
    for (b2 = 0; b2 < paramInstances.numInstances(); b2++) {
      instance = paramInstances.instance(b2);
      for (byte b = 0; b < paramInstances.numAttributes(); b++) {
        if (!instance.isMissing(b) && paramInstances.attribute(b).isNumeric()) {
          double d = this.m_modelNormal[arrayOfInt[b2]][b][0] - instance.value(b);
          this.m_modelNormal[arrayOfInt[b2]][b][1] = this.m_modelNormal[arrayOfInt[b2]][b][1] + instance.weight() * d * d;
        } 
      } 
    } 
    for (b2 = 0; b2 < paramInstances.numAttributes(); b2++) {
      if (paramInstances.attribute(b2).isNumeric())
        for (byte b = 0; b < this.m_wrappedClusterer.numberOfClusters(); b++) {
          if (arrayOfDouble[b][b2] > 0.0D) {
            this.m_modelNormal[b][b2][1] = Math.sqrt(this.m_modelNormal[b][b2][1] / arrayOfDouble[b][b2]);
          } else if (arrayOfDouble[b][b2] <= 0.0D) {
            this.m_modelNormal[b][b2][1] = Double.MAX_VALUE;
          } 
          if (this.m_modelNormal[b][b2][1] <= this.m_minStdDev) {
            this.m_modelNormal[b][b2][1] = (paramInstances.attributeStats(b2)).numericStats.stdDev;
            if (this.m_modelNormal[b][b2][1] <= this.m_minStdDev)
              this.m_modelNormal[b][b2][1] = this.m_minStdDev; 
          } 
        }  
    } 
    Utils.normalize(this.m_priors);
  }
  
  public double[] clusterPriors() {
    double[] arrayOfDouble = new double[this.m_priors.length];
    System.arraycopy(this.m_priors, 0, arrayOfDouble, 0, arrayOfDouble.length);
    return arrayOfDouble;
  }
  
  public double[] logDensityPerClusterForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = new double[this.m_wrappedClusterer.numberOfClusters()];
    this.m_replaceMissing.input(paramInstance);
    paramInstance = this.m_replaceMissing.output();
    for (byte b = 0; b < this.m_wrappedClusterer.numberOfClusters(); b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < paramInstance.numAttributes(); b1++) {
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
  
  private double logNormalDens(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d = paramDouble1 - paramDouble2;
    return -(d * d / 2.0D * paramDouble3 * paramDouble3) - m_normConst - Math.log(paramDouble3);
  }
  
  public int numberOfClusters() throws Exception {
    return this.m_wrappedClusterer.numberOfClusters();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("MakeDensityBasedClusterer: \n\nWrapped clusterer: " + this.m_wrappedClusterer.toString());
    stringBuffer.append("\nFitted estimators (with ML estimates of variance):\n");
    for (byte b = 0; b < this.m_priors.length; b++) {
      stringBuffer.append("\nCluster: " + b + " Prior probability: " + Utils.doubleToString(this.m_priors[b], 4) + "\n\n");
      for (byte b1 = 0; b1 < (this.m_model[0]).length; b1++) {
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
  
  public void setClusterer(Clusterer paramClusterer) {
    this.m_wrappedClusterer = paramClusterer;
  }
  
  public Clusterer getClusterer() {
    return this.m_wrappedClusterer;
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
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tminimum allowable standard deviation for normal density computation \n\t(default 1e-6)", "M", 1, "-M <num>"));
    vector.addElement(new Option("\tClusterer to wrap. (required)\n", "W", 1, "-W <clusterer name>"));
    if (this.m_wrappedClusterer != null && this.m_wrappedClusterer instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to clusterer " + this.m_wrappedClusterer.getClass().getName() + ":"));
      Enumeration enumeration = ((OptionHandler)this.m_wrappedClusterer).listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('M', paramArrayOfString);
    if (str1.length() != 0)
      setMinStdDev((new Double(str1)).doubleValue()); 
    String str2 = Utils.getOption('W', paramArrayOfString);
    if (str2.length() == 0)
      throw new Exception("A clusterer must be specified with the -W option."); 
    String[] arrayOfString = Utils.splitOptions(str2);
    if (arrayOfString.length == 0)
      throw new IllegalArgumentException("Invalid clusterer specification string"); 
    String str3 = arrayOfString[0];
    arrayOfString[0] = "";
    setClusterer(Clusterer.forName(str3, arrayOfString));
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_wrappedClusterer != null && this.m_wrappedClusterer instanceof OptionHandler)
      arrayOfString1 = ((OptionHandler)this.m_wrappedClusterer).getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 5];
    int i = 0;
    arrayOfString2[i++] = "-M";
    arrayOfString2[i++] = "" + getMinStdDev();
    if (getClusterer() != null) {
      arrayOfString2[i++] = "-W";
      arrayOfString2[i++] = getClusterer().getClass().getName();
    } 
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(ClusterEvaluation.evaluateClusterer(new MakeDensityBasedClusterer(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\clusterers\MakeDensityBasedClusterer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */