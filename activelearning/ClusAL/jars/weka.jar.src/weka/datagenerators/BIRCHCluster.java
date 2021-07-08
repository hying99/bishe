package weka.datagenerators;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class BIRCHCluster extends ClusterGenerator implements OptionHandler, Serializable {
  private int m_MinInstNum = 1;
  
  private int m_MaxInstNum = 50;
  
  private double m_MinRadius = 0.1D;
  
  private double m_MaxRadius = Math.sqrt(2.0D);
  
  public static final int GRID = 0;
  
  public static final int SINE = 1;
  
  public static final int RANDOM = 2;
  
  private int m_Pattern = 2;
  
  private double m_DistMult = 4.0D;
  
  private int m_NumCycles = 4;
  
  public static final int ORDERED = 0;
  
  public static final int RANDOMIZED = 1;
  
  private int m_InputOrder = 1;
  
  private double m_NoiseRate = 0.0D;
  
  private int m_Seed = 1;
  
  private Instances m_DatasetFormat = null;
  
  private Random m_Random = null;
  
  private int m_Debug = 0;
  
  private FastVector m_ClusterList;
  
  private int m_GridSize;
  
  private double m_GridWidth;
  
  public String globalInfo() {
    return "A data generator that produces data points in clusters.";
  }
  
  public void setInstNums(String paramString) {
    int i = paramString.indexOf("..");
    String str1 = paramString.substring(0, i);
    setMinInstNum(Integer.parseInt(str1));
    String str2 = paramString.substring(i + 2, paramString.length());
    setMaxInstNum(Integer.parseInt(str2));
  }
  
  public String getInstNums() {
    return "" + getMinInstNum() + ".." + getMaxInstNum();
  }
  
  public int getMinInstNum() {
    return this.m_MinInstNum;
  }
  
  public void setMinInstNum(int paramInt) {
    this.m_MinInstNum = paramInt;
  }
  
  public int getMaxInstNum() {
    return this.m_MaxInstNum;
  }
  
  public void setMaxInstNum(int paramInt) {
    this.m_MaxInstNum = paramInt;
  }
  
  public void setRadiuses(String paramString) {
    int i = paramString.indexOf("..");
    String str1 = paramString.substring(0, i);
    setMinRadius(Double.valueOf(str1).doubleValue());
    String str2 = paramString.substring(i + 2, paramString.length());
    setMaxRadius(Double.valueOf(str2).doubleValue());
  }
  
  public String getRadiuses() {
    return "" + Utils.doubleToString(getMinRadius(), 2) + ".." + Utils.doubleToString(getMaxRadius(), 2);
  }
  
  public double getMinRadius() {
    return this.m_MinRadius;
  }
  
  public void setMinRadius(double paramDouble) {
    this.m_MinRadius = paramDouble;
  }
  
  public double getMaxRadius() {
    return this.m_MaxRadius;
  }
  
  public void setMaxRadius(double paramDouble) {
    this.m_MaxRadius = paramDouble;
  }
  
  public boolean getGridFlag() {
    return (this.m_Pattern == 0);
  }
  
  public boolean getSineFlag() {
    return (this.m_Pattern == 1);
  }
  
  public int getPattern() {
    return this.m_Pattern;
  }
  
  public void setPattern(int paramInt) {
    this.m_Pattern = paramInt;
  }
  
  public double getDistMult() {
    return this.m_DistMult;
  }
  
  public void setDistMult(double paramDouble) {
    this.m_DistMult = paramDouble;
  }
  
  public int getNumCycles() {
    return this.m_NumCycles;
  }
  
  public void setNumCycles(int paramInt) {
    this.m_NumCycles = paramInt;
  }
  
  public int getInputOrder() {
    return this.m_InputOrder;
  }
  
  public void setInputOrder(int paramInt) {
    this.m_InputOrder = paramInt;
  }
  
  public boolean getOrderedFlag() {
    return (this.m_InputOrder == 0);
  }
  
  public double getNoiseRate() {
    return this.m_NoiseRate;
  }
  
  public void setNoiseRate(double paramDouble) {
    this.m_NoiseRate = paramDouble;
  }
  
  public Random getRandom() {
    if (this.m_Random == null)
      this.m_Random = new Random(getSeed()); 
    return this.m_Random;
  }
  
  public void setRandom(Random paramRandom) {
    this.m_Random = paramRandom;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public Instances getDatasetFormat() {
    return this.m_DatasetFormat;
  }
  
  public void setDatasetFormat(Instances paramInstances) {
    this.m_DatasetFormat = paramInstances;
  }
  
  public boolean getSingleModeFlag() {
    return false;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(5);
    vector.addElement(new Option("\tSet pattern to grid (default is random).", "G", 1, "-G"));
    vector.addElement(new Option("\tSet pattern to sine (default is random).", "S", 1, "-S"));
    vector.addElement(new Option("\tThe range of number of instances per cluster (default 1..50).", "N", 1, "-N <num>..<num>"));
    vector.addElement(new Option("\tThe range of radius per cluster (default 0.1..sqrt(2)).", "R", 1, "-R <num>..<num>"));
    vector.addElement(new Option("\tThe distance multiplier (default 4).", "M", 1, "-M <num>"));
    vector.addElement(new Option("\tThe number of cycles (default 4).", "C", 1, "-C <num>"));
    vector.addElement(new Option("\tSet input order to ordered (default is randomized).", "O", 1, "-O"));
    vector.addElement(new Option("\tThe noise rate in percent (default 0).", "P", 1, "-P <num>"));
    vector.addElement(new Option("\tThe Seed for random function (default 1).", "S", 1, "-S"));
    return vector.elements();
  }
  
  public void setDefaultOptions() {
    this.m_MinInstNum = 1;
    this.m_MaxInstNum = 50;
    this.m_MinRadius = 0.1D;
    this.m_MaxRadius = Math.sqrt(2.0D);
    this.m_Pattern = 2;
    this.m_DistMult = 4.0D;
    this.m_NumCycles = 4;
    this.m_InputOrder = 1;
    this.m_NoiseRate = 0.0D;
    this.m_Seed = 1;
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setDefaultOptions();
    String str2 = Utils.getOption('N', paramArrayOfString);
    if (str2.length() != 0)
      setInstNums(str2); 
    str2 = Utils.getOption('R', paramArrayOfString);
    if (str2.length() != 0)
      setRadiuses(str2); 
    boolean bool1 = Utils.getFlag('G', paramArrayOfString);
    boolean bool2 = Utils.getFlag('I', paramArrayOfString);
    if (bool1 && bool2)
      throw new Exception("Flags G and I can only be set mutually exclusiv."); 
    if (bool1)
      setPattern(0); 
    if (bool2)
      setPattern(1); 
    String str1 = Utils.getOption('M', paramArrayOfString);
    if (str1.length() != 0) {
      if (!bool1)
        throw new Exception("Option M can only be used with GRID pattern."); 
      setDistMult(Double.valueOf(str1).doubleValue());
    } 
    str1 = Utils.getOption('C', paramArrayOfString);
    if (str1.length() != 0) {
      if (!bool2)
        throw new Exception("Option C can only be used with SINE pattern."); 
      setNumCycles((int)Double.valueOf(str1).doubleValue());
    } 
    boolean bool3 = Utils.getFlag('O', paramArrayOfString);
    if (bool3)
      setInputOrder(0); 
    str1 = Utils.getOption('P', paramArrayOfString);
    if (str1.length() != 0)
      setNoiseRate(Double.valueOf(str1).doubleValue()); 
    str1 = Utils.getOption('S', paramArrayOfString);
    if (str1.length() != 0)
      setSeed(Integer.parseInt(str1)); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[20];
    byte b = 0;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + getInstNums();
    arrayOfString[b++] = "-R";
    arrayOfString[b++] = "" + getRadiuses();
    if (getGridFlag()) {
      arrayOfString[b++] = "-G";
      arrayOfString[b++] = "";
      arrayOfString[b++] = "-D";
      arrayOfString[b++] = "" + getDistMult();
    } 
    if (getSineFlag()) {
      arrayOfString[b++] = "-I";
      arrayOfString[b++] = "";
      arrayOfString[b++] = "-C";
      arrayOfString[b++] = "" + getNumCycles();
    } 
    if (getOrderedFlag()) {
      arrayOfString[b++] = "-O";
      arrayOfString[b++] = "";
    } 
    arrayOfString[b++] = "-P";
    arrayOfString[b++] = "" + getNoiseRate();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public Instances defineDataFormat() throws Exception {
    Random random = new Random(getSeed());
    setRandom(random);
    FastVector fastVector1 = new FastVector(3);
    boolean bool = getClassFlag();
    FastVector fastVector2 = null;
    if (bool)
      fastVector2 = new FastVector(this.m_NumClusters); 
    byte b;
    for (b = 0; b < getNumAttributes(); b++) {
      Attribute attribute = new Attribute("X" + b);
      fastVector1.addElement(attribute);
    } 
    if (bool) {
      for (b = 0; b < this.m_NumClusters; b++)
        fastVector2.addElement("c" + b); 
      Attribute attribute = new Attribute("class", fastVector2);
      fastVector1.addElement(attribute);
    } 
    Instances instances1 = new Instances(getRelationName(), fastVector1, 0);
    if (bool)
      instances1.setClassIndex(this.m_NumAttributes); 
    Instances instances2 = new Instances(instances1, 0);
    setDatasetFormat(instances2);
    this.m_ClusterList = defineClusters(random);
    System.out.println("dataset" + instances1.numAttributes());
    return instances1;
  }
  
  public Instance generateExample() throws Exception {
    throw new Exception("Examples cannot be generated one by one.");
  }
  
  public Instances generateExamples() throws Exception {
    Random random = getRandom();
    Instances instances = getDatasetFormat();
    if (instances == null)
      throw new Exception("Dataset format not defined."); 
    if (getOrderedFlag()) {
      instances = generateExamples(random, instances);
    } else {
      throw new Exception("RANDOMIZED is not yet implemented.");
    } 
    return instances;
  }
  
  public Instances generateExamples(Random paramRandom, Instances paramInstances) throws Exception {
    Instance instance = null;
    if (paramInstances == null)
      throw new Exception("Dataset format not defined."); 
    byte b = 0;
    Enumeration enumeration = this.m_ClusterList.elements();
    while (enumeration.hasMoreElements()) {
      Cluster cluster = enumeration.nextElement();
      double d = cluster.getStdDev();
      int i = cluster.getInstNum();
      double[] arrayOfDouble = cluster.getCenter();
      String str = "c" + b;
      for (byte b1 = 0; b1 < i; b1++) {
        instance = generateInstance(paramInstances, paramRandom, d, arrayOfDouble, str);
        if (instance != null)
          instance.setDataset(paramInstances); 
        paramInstances.add(instance);
      } 
      b++;
    } 
    return paramInstances;
  }
  
  private Instance generateInstance(Instances paramInstances, Random paramRandom, double paramDouble, double[] paramArrayOfdouble, String paramString) {
    int i = this.m_NumAttributes;
    if (getClassFlag())
      i++; 
    Instance instance = new Instance(i);
    instance.setDataset(paramInstances);
    for (byte b = 0; b < this.m_NumAttributes; b++)
      instance.setValue(b, paramRandom.nextGaussian() * paramDouble + paramArrayOfdouble[b]); 
    if (getClassFlag())
      instance.setClassValue(paramString); 
    return instance;
  }
  
  private FastVector defineClusters(Random paramRandom) throws Exception {
    return (this.m_Pattern == 0) ? defineClustersGRID(paramRandom) : defineClustersRANDOM(paramRandom);
  }
  
  private FastVector defineClustersGRID(Random paramRandom) throws Exception {
    FastVector fastVector = new FastVector(this.m_NumClusters);
    double d1 = (this.m_MaxInstNum - this.m_MinInstNum);
    double d2 = this.m_MinInstNum;
    double d3 = this.m_MaxRadius - this.m_MinRadius;
    double d4 = Math.pow(this.m_NumClusters, 1.0D / this.m_NumAttributes);
    if (d4 - (int)d4 > 0.0D) {
      this.m_GridSize = (int)(d4 + 1.0D);
    } else {
      this.m_GridSize = (int)d4;
    } 
    this.m_GridWidth = (this.m_MaxRadius + this.m_MinRadius) / 2.0D * this.m_DistMult;
    System.out.println("GridSize= " + this.m_GridSize);
    System.out.println("GridWidth= " + this.m_GridWidth);
    GridVector gridVector = new GridVector(this.m_NumAttributes, this.m_GridSize);
    for (byte b = 0; b < this.m_NumClusters; b++) {
      int i = (int)(paramRandom.nextDouble() * d1 + d2);
      double d = paramRandom.nextDouble() * d3 + this.m_MinRadius;
      Cluster cluster = new Cluster(i, d, gridVector.getGridVector(), this.m_GridWidth);
      fastVector.addElement(cluster);
      gridVector.addOne();
    } 
    return fastVector;
  }
  
  private FastVector defineClustersRANDOM(Random paramRandom) throws Exception {
    FastVector fastVector = new FastVector(this.m_NumClusters);
    double d1 = (this.m_MaxInstNum - this.m_MinInstNum);
    double d2 = this.m_MinInstNum;
    double d3 = this.m_MaxRadius - this.m_MinRadius;
    for (byte b = 0; b < this.m_NumClusters; b++) {
      int i = (int)(paramRandom.nextDouble() * d1 + d2);
      double d = paramRandom.nextDouble() * d3 + this.m_MinRadius;
      Cluster cluster = new Cluster(i, d, paramRandom);
      fastVector.addElement(cluster);
    } 
    return fastVector;
  }
  
  public String generateFinished() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    Instances instances = getDatasetFormat();
    stringBuffer.append("\n%\n%\n");
    return stringBuffer.toString();
  }
  
  public String generateStart() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\n%\n%\n");
    int i = 0;
    byte b = 0;
    Enumeration enumeration = this.m_ClusterList.elements();
    while (enumeration.hasMoreElements()) {
      Cluster cluster = enumeration.nextElement();
      stringBuffer.append("%\n");
      stringBuffer.append("% Cluster: c" + b + "\n");
      stringBuffer.append("% ----------------------------------------------\n");
      stringBuffer.append("% StandardDeviation: " + Utils.doubleToString(cluster.getStdDev(), 2) + "\n");
      stringBuffer.append("% Number of instances: " + cluster.getInstNum() + "\n");
      i += cluster.getInstNum();
      double[] arrayOfDouble = cluster.getCenter();
      stringBuffer.append("% ");
      for (byte b1 = 0; b1 < arrayOfDouble.length - 1; b1++)
        stringBuffer.append(Utils.doubleToString(arrayOfDouble[b1], 2) + ", "); 
      stringBuffer.append(Utils.doubleToString(arrayOfDouble[arrayOfDouble.length - 1], 2) + "\n");
      b++;
    } 
    stringBuffer.append("\n% ----------------------------------------------\n");
    stringBuffer.append("% Total number of instances: " + i + "\n");
    stringBuffer.append("%                            in " + b + " clusters\n");
    stringBuffer.append("% Pattern chosen           : ");
    if (getGridFlag()) {
      stringBuffer.append("GRID, distance multiplier = " + Utils.doubleToString(this.m_DistMult, 2) + "\n");
    } else if (getSineFlag()) {
      stringBuffer.append("SINE\n");
    } else {
      stringBuffer.append("RANDOM\n");
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      ClusterGenerator.makeData(new BIRCHCluster(), paramArrayOfString);
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
  
  private class GridVector implements Serializable {
    private int[] m_GridVector;
    
    private int m_Base;
    
    private int m_Size;
    
    private final BIRCHCluster this$0;
    
    private GridVector(BIRCHCluster this$0, int param1Int1, int param1Int2) {
      BIRCHCluster.this = BIRCHCluster.this;
      this.m_Size = param1Int1;
      this.m_Base = param1Int2;
      this.m_GridVector = new int[param1Int1];
      for (byte b = 0; b < param1Int1; b++)
        this.m_GridVector[b] = 0; 
    }
    
    private int[] getGridVector() {
      return this.m_GridVector;
    }
    
    private boolean overflow(int param1Int) {
      return (param1Int == 0);
    }
    
    private int addOne(int param1Int) {
      int i = param1Int + 1;
      if (i >= this.m_Base)
        i = 0; 
      return i;
    }
    
    private void addOne() {
      this.m_GridVector[0] = addOne(this.m_GridVector[0]);
      for (byte b = 1; overflow(this.m_GridVector[b - 1]) && b < this.m_Size; b++)
        this.m_GridVector[b] = addOne(this.m_GridVector[b]); 
    }
  }
  
  private class Cluster implements Serializable {
    private int m_InstNum;
    
    private double m_Radius;
    
    private double[] m_Center;
    
    private final BIRCHCluster this$0;
    
    private Cluster(BIRCHCluster this$0, int param1Int, double param1Double, Random param1Random) {
      BIRCHCluster.this = BIRCHCluster.this;
      this.m_InstNum = param1Int;
      this.m_Radius = param1Double;
      this.m_Center = new double[BIRCHCluster.this.m_NumAttributes];
      for (byte b = 0; b < BIRCHCluster.this.m_NumAttributes; b++)
        this.m_Center[b] = param1Random.nextDouble() * BIRCHCluster.this.m_NumClusters; 
    }
    
    private Cluster(BIRCHCluster this$0, int param1Int, double param1Double1, int[] param1ArrayOfint, double param1Double2) {
      BIRCHCluster.this = BIRCHCluster.this;
      this.m_InstNum = param1Int;
      this.m_Radius = param1Double1;
      this.m_Center = new double[BIRCHCluster.this.m_NumAttributes];
      for (byte b = 0; b < BIRCHCluster.this.m_NumAttributes; b++)
        this.m_Center[b] = (param1ArrayOfint[b] + 1.0D) * param1Double2; 
    }
    
    private int getInstNum() {
      return this.m_InstNum;
    }
    
    private double getRadius() {
      return this.m_Radius;
    }
    
    private double getVariance() {
      return Math.pow(this.m_Radius, 2.0D) / 2.0D;
    }
    
    private double getStdDev() {
      return this.m_Radius / Math.pow(2.0D, 0.5D);
    }
    
    private double[] getCenter() {
      return this.m_Center;
    }
    
    private double getCenterValue(int param1Int) throws Exception {
      if (param1Int >= this.m_Center.length)
        throw new Exception("Current system has only " + this.m_Center.length + " dimensions."); 
      return this.m_Center[param1Int];
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\datagenerators\BIRCHCluster.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */