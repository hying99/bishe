package weka.clusterers;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class FarthestFirst extends Clusterer implements OptionHandler {
  protected Instances m_instances;
  
  protected ReplaceMissingValues m_ReplaceMissingFilter;
  
  protected int m_NumClusters = 2;
  
  protected Instances m_ClusterCentroids;
  
  private double[] m_Min;
  
  private double[] m_Max;
  
  protected int m_Seed = 1;
  
  public String globalInfo() {
    return "Cluster data using the FarthestFirst algorithm";
  }
  
  public void buildClusterer(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new Exception("Can't handle string attributes!"); 
    this.m_ReplaceMissingFilter = new ReplaceMissingValues();
    this.m_ReplaceMissingFilter.setInputFormat(paramInstances);
    this.m_instances = Filter.useFilter(paramInstances, (Filter)this.m_ReplaceMissingFilter);
    initMinMax(this.m_instances);
    this.m_ClusterCentroids = new Instances(this.m_instances, this.m_NumClusters);
    int i = this.m_instances.numInstances();
    Random random = new Random(this.m_Seed);
    boolean[] arrayOfBoolean = new boolean[i];
    double[] arrayOfDouble = new double[i];
    int j;
    for (j = 0; j < i; j++)
      arrayOfDouble[j] = Double.MAX_VALUE; 
    j = random.nextInt(i);
    this.m_ClusterCentroids.add(this.m_instances.instance(j));
    arrayOfBoolean[j] = true;
    updateMinDistance(arrayOfDouble, arrayOfBoolean, this.m_instances, this.m_instances.instance(j));
    if (this.m_NumClusters > i)
      this.m_NumClusters = i; 
    for (byte b = 1; b < this.m_NumClusters; b++) {
      int k = farthestAway(arrayOfDouble, arrayOfBoolean);
      this.m_ClusterCentroids.add(this.m_instances.instance(k));
      arrayOfBoolean[k] = true;
      updateMinDistance(arrayOfDouble, arrayOfBoolean, this.m_instances, this.m_instances.instance(k));
    } 
    this.m_instances = new Instances(this.m_instances, 0);
  }
  
  protected void updateMinDistance(double[] paramArrayOfdouble, boolean[] paramArrayOfboolean, Instances paramInstances, Instance paramInstance) {
    for (byte b = 0; b < paramArrayOfboolean.length; b++) {
      if (!paramArrayOfboolean[b]) {
        double d = distance(paramInstance, paramInstances.instance(b));
        if (d < paramArrayOfdouble[b])
          paramArrayOfdouble[b] = d; 
      } 
    } 
  }
  
  protected int farthestAway(double[] paramArrayOfdouble, boolean[] paramArrayOfboolean) {
    double d = -1.0D;
    byte b = -1;
    for (byte b1 = 0; b1 < paramArrayOfboolean.length; b1++) {
      if (!paramArrayOfboolean[b1] && d < paramArrayOfdouble[b1]) {
        d = paramArrayOfdouble[b1];
        b = b1;
      } 
    } 
    return b;
  }
  
  protected void initMinMax(Instances paramInstances) {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokevirtual numAttributes : ()I
    //   5: newarray double
    //   7: putfield m_Min : [D
    //   10: aload_0
    //   11: aload_1
    //   12: invokevirtual numAttributes : ()I
    //   15: newarray double
    //   17: putfield m_Max : [D
    //   20: iconst_0
    //   21: istore_2
    //   22: iload_2
    //   23: aload_1
    //   24: invokevirtual numAttributes : ()I
    //   27: if_icmpge -> 52
    //   30: aload_0
    //   31: getfield m_Min : [D
    //   34: iload_2
    //   35: aload_0
    //   36: getfield m_Max : [D
    //   39: iload_2
    //   40: ldc2_w NaN
    //   43: dup2_x2
    //   44: dastore
    //   45: dastore
    //   46: iinc #2, 1
    //   49: goto -> 22
    //   52: iconst_0
    //   53: istore_2
    //   54: iload_2
    //   55: aload_1
    //   56: invokevirtual numInstances : ()I
    //   59: if_icmpge -> 77
    //   62: aload_0
    //   63: aload_1
    //   64: iload_2
    //   65: invokevirtual instance : (I)Lweka/core/Instance;
    //   68: invokespecial updateMinMax : (Lweka/core/Instance;)V
    //   71: iinc #2, 1
    //   74: goto -> 54
    //   77: return
  }
  
  private void updateMinMax(Instance paramInstance) {
    for (byte b = 0; b < paramInstance.numAttributes(); b++) {
      if (Double.isNaN(this.m_Min[b])) {
        this.m_Min[b] = paramInstance.value(b);
        this.m_Max[b] = paramInstance.value(b);
      } else if (paramInstance.value(b) < this.m_Min[b]) {
        this.m_Min[b] = paramInstance.value(b);
      } else if (paramInstance.value(b) > this.m_Max[b]) {
        this.m_Max[b] = paramInstance.value(b);
      } 
    } 
  }
  
  protected int clusterProcessedInstance(Instance paramInstance) {
    double d = Double.MAX_VALUE;
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_NumClusters; b2++) {
      double d1 = distance(paramInstance, this.m_ClusterCentroids.instance(b2));
      if (d1 < d) {
        d = d1;
        b1 = b2;
      } 
    } 
    return b1;
  }
  
  public int clusterInstance(Instance paramInstance) throws Exception {
    this.m_ReplaceMissingFilter.input(paramInstance);
    this.m_ReplaceMissingFilter.batchFinished();
    Instance instance = this.m_ReplaceMissingFilter.output();
    return clusterProcessedInstance(instance);
  }
  
  protected double distance(Instance paramInstance1, Instance paramInstance2) {
    double d = 0.0D;
    byte b1 = 0;
    byte b2 = 0;
    while (true) {
      if (b1 < paramInstance1.numValues() || b2 < paramInstance2.numValues()) {
        int i;
        int j;
        double d1;
        if (b1 >= paramInstance1.numValues()) {
          i = this.m_instances.numAttributes();
        } else {
          i = paramInstance1.index(b1);
        } 
        if (b2 >= paramInstance2.numValues()) {
          j = this.m_instances.numAttributes();
        } else {
          j = paramInstance2.index(b2);
        } 
        if (i == this.m_instances.classIndex()) {
          b1++;
          continue;
        } 
        if (j == this.m_instances.classIndex()) {
          b2++;
          continue;
        } 
        if (i == j) {
          d1 = difference(i, paramInstance1.valueSparse(b1), paramInstance2.valueSparse(b2));
          b1++;
          b2++;
        } else if (i > j) {
          d1 = difference(j, 0.0D, paramInstance2.valueSparse(b2));
          b2++;
        } else {
          d1 = difference(i, paramInstance1.valueSparse(b1), 0.0D);
          b1++;
        } 
        d += d1 * d1;
        continue;
      } 
      return Math.sqrt(d / this.m_instances.numAttributes());
    } 
  }
  
  protected double difference(int paramInt, double paramDouble1, double paramDouble2) {
    switch (this.m_instances.attribute(paramInt).type()) {
      case 1:
        return (Instance.isMissingValue(paramDouble1) || Instance.isMissingValue(paramDouble2) || (int)paramDouble1 != (int)paramDouble2) ? 1.0D : 0.0D;
      case 0:
        if (Instance.isMissingValue(paramDouble1) || Instance.isMissingValue(paramDouble2)) {
          double d;
          if (Instance.isMissingValue(paramDouble1) && Instance.isMissingValue(paramDouble2))
            return 1.0D; 
          if (Instance.isMissingValue(paramDouble2)) {
            d = norm(paramDouble1, paramInt);
          } else {
            d = norm(paramDouble2, paramInt);
          } 
          if (d < 0.5D)
            d = 1.0D - d; 
          return d;
        } 
        return norm(paramDouble1, paramInt) - norm(paramDouble2, paramInt);
    } 
    return 0.0D;
  }
  
  protected double norm(double paramDouble, int paramInt) {
    return (Double.isNaN(this.m_Min[paramInt]) || Utils.eq(this.m_Max[paramInt], this.m_Min[paramInt])) ? 0.0D : ((paramDouble - this.m_Min[paramInt]) / (this.m_Max[paramInt] - this.m_Min[paramInt]));
  }
  
  public int numberOfClusters() throws Exception {
    return this.m_NumClusters;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tnumber of clusters. (default = 2).", "N", 1, "-N <num>"));
    vector.addElement(new Option("\trandom number seed.\n (default 10)", "S", 1, "-S <num>"));
    return vector.elements();
  }
  
  public String numClustersTipText() {
    return "set number of clusters";
  }
  
  public void setNumClusters(int paramInt) throws Exception {
    if (paramInt < 0)
      throw new Exception("Number of clusters must be > 0"); 
    this.m_NumClusters = paramInt;
  }
  
  public int getNumClusters() {
    return this.m_NumClusters;
  }
  
  public String seedTipText() {
    return "random number seed";
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('N', paramArrayOfString);
    if (str.length() != 0)
      setNumClusters(Integer.parseInt(str)); 
    str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0)
      setSeed(Integer.parseInt(str)); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[4];
    byte b = 0;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + getNumClusters();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSeed();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\n FarthestFirst\n==============\n");
    stringBuffer.append("\nCluster centroids:\n");
    for (byte b = 0; b < this.m_NumClusters; b++) {
      stringBuffer.append("\nCluster " + b + "\n\t");
      for (byte b1 = 0; b1 < this.m_ClusterCentroids.numAttributes(); b1++) {
        if (this.m_ClusterCentroids.attribute(b1).isNominal()) {
          stringBuffer.append(" " + this.m_ClusterCentroids.attribute(b1).value((int)this.m_ClusterCentroids.instance(b).value(b1)));
        } else {
          stringBuffer.append(" " + this.m_ClusterCentroids.instance(b).value(b1));
        } 
      } 
    } 
    stringBuffer.append("\n\n");
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(ClusterEvaluation.evaluateClusterer(new FarthestFirst(), paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\clusterers\FarthestFirst.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */