package weka.classifiers.functions;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.instance.RemoveRange;

public class LeastMedSq extends Classifier implements OptionHandler {
  private double[] m_Residuals;
  
  private double[] m_weight;
  
  private double m_SSR;
  
  private double m_scalefactor;
  
  private double m_bestMedian = Double.POSITIVE_INFINITY;
  
  private LinearRegression m_currentRegression;
  
  private LinearRegression m_bestRegression;
  
  private LinearRegression m_ls;
  
  private Instances m_Data;
  
  private Instances m_RLSData;
  
  private Instances m_SubSample;
  
  private ReplaceMissingValues m_MissingFilter;
  
  private NominalToBinary m_TransformFilter;
  
  private RemoveRange m_SplitFilter;
  
  private int m_samplesize = 4;
  
  private int m_samples;
  
  private boolean m_israndom = false;
  
  private boolean m_debug = false;
  
  private Random m_random;
  
  private long m_randomseed = 0L;
  
  public String globalInfo() {
    return "Implements a least median sqaured linear regression utilising the existing weka LinearRegression class to form predictions. Least squared regression functions are generated from random subsamples of the data. The least squared regression with the lowest meadian squared error is chosen as the final model.\n\nThe basis of the algorithm is \n\nRobust regression and outlier detection Peter J. Rousseeuw, Annick M. Leroy. c1987";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (!paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Class attribute has to be numeric for regression!"); 
    if (paramInstances.numInstances() == 0)
      throw new Exception("No instances in training file!"); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    cleanUpData(paramInstances);
    getSamples();
    findBestRegression();
    buildRLSRegression();
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    Instance instance = paramInstance;
    this.m_TransformFilter.input(instance);
    instance = this.m_TransformFilter.output();
    this.m_MissingFilter.input(instance);
    instance = this.m_MissingFilter.output();
    return this.m_ls.classifyInstance(instance);
  }
  
  private void cleanUpData(Instances paramInstances) throws Exception {
    this.m_Data = paramInstances;
    this.m_TransformFilter = new NominalToBinary();
    this.m_TransformFilter.setInputFormat(this.m_Data);
    this.m_Data = Filter.useFilter(this.m_Data, (Filter)this.m_TransformFilter);
    this.m_MissingFilter = new ReplaceMissingValues();
    this.m_MissingFilter.setInputFormat(this.m_Data);
    this.m_Data = Filter.useFilter(this.m_Data, (Filter)this.m_MissingFilter);
    this.m_Data.deleteWithMissingClass();
  }
  
  private void getSamples() throws Exception {
    int[] arrayOfInt = { 500, 50, 22, 17, 15, 14 };
    int i = this.m_samplesize * 500;
    if (this.m_samplesize < 7) {
      if (this.m_Data.numInstances() < arrayOfInt[this.m_samplesize - 1]) {
        this.m_samples = combinations(this.m_Data.numInstances(), this.m_samplesize);
      } else {
        this.m_samples = this.m_samplesize * 500;
      } 
    } else {
      this.m_samples = 3000;
    } 
    if (this.m_debug) {
      System.out.println("m_samplesize: " + this.m_samplesize);
      System.out.println("m_samples: " + this.m_samples);
      System.out.println("m_randomseed: " + this.m_randomseed);
    } 
  }
  
  private void setRandom() {
    this.m_random = new Random(getRandomSeed());
  }
  
  private void findBestRegression() throws Exception {
    setRandom();
    this.m_bestMedian = Double.POSITIVE_INFINITY;
    if (this.m_debug)
      System.out.println("Starting:"); 
    byte b1 = 0;
    for (byte b2 = 0; b1 < this.m_samples; b2++) {
      if (this.m_debug && b1 % this.m_samples / 100 == 0)
        System.out.print("*"); 
      genRegression();
      getMedian();
      b1++;
    } 
    if (this.m_debug)
      System.out.println(""); 
    this.m_currentRegression = this.m_bestRegression;
  }
  
  private void genRegression() throws Exception {
    this.m_currentRegression = new LinearRegression();
    this.m_currentRegression.setOptions(new String[] { "-S", "1" });
    selectSubSample(this.m_Data);
    this.m_currentRegression.buildClassifier(this.m_SubSample);
  }
  
  private void findResiduals() throws Exception {
    this.m_SSR = 0.0D;
    this.m_Residuals = new double[this.m_Data.numInstances()];
    for (byte b = 0; b < this.m_Data.numInstances(); b++) {
      this.m_Residuals[b] = this.m_currentRegression.classifyInstance(this.m_Data.instance(b));
      this.m_Residuals[b] = this.m_Residuals[b] - this.m_Data.instance(b).value(this.m_Data.classAttribute());
      this.m_Residuals[b] = this.m_Residuals[b] * this.m_Residuals[b];
      this.m_SSR += this.m_Residuals[b];
    } 
  }
  
  private void getMedian() throws Exception {
    findResiduals();
    int i = this.m_Residuals.length;
    select(this.m_Residuals, 0, i - 1, i / 2);
    if (this.m_Residuals[i / 2] < this.m_bestMedian) {
      this.m_bestMedian = this.m_Residuals[i / 2];
      this.m_bestRegression = this.m_currentRegression;
    } 
  }
  
  public String toString() {
    return (this.m_ls == null) ? "model has not been built" : this.m_ls.toString();
  }
  
  private void buildWeight() throws Exception {
    findResiduals();
    this.m_scalefactor = 1.4826D * (1 + 5 / (this.m_Data.numInstances() - this.m_Data.numAttributes())) * Math.sqrt(this.m_bestMedian);
    this.m_weight = new double[this.m_Residuals.length];
    for (byte b = 0; b < this.m_Residuals.length; b++)
      this.m_weight[b] = (Math.sqrt(this.m_Residuals[b]) / this.m_scalefactor < 2.5D) ? 1.0D : 0.0D; 
  }
  
  private void buildRLSRegression() throws Exception {
    buildWeight();
    this.m_RLSData = new Instances(this.m_Data);
    byte b1 = 0;
    byte b2 = 0;
    int i = this.m_RLSData.numInstances();
    while (b2 < i) {
      if (this.m_weight[b1] == 0.0D) {
        this.m_RLSData.delete(b2);
        i = this.m_RLSData.numInstances();
        b2--;
      } 
      b1++;
      b2++;
    } 
    if (this.m_RLSData.numInstances() == 0) {
      System.err.println("rls regression unbuilt");
      this.m_ls = this.m_currentRegression;
    } else {
      this.m_ls = new LinearRegression();
      this.m_ls.setOptions(new String[] { "-S", "1" });
      this.m_ls.buildClassifier(this.m_RLSData);
      this.m_currentRegression = this.m_ls;
    } 
  }
  
  private static void select(double[] paramArrayOfdouble, int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 <= paramInt1)
      return; 
    int i = partition(paramArrayOfdouble, paramInt1, paramInt2);
    if (i > paramInt3)
      select(paramArrayOfdouble, paramInt1, i - 1, paramInt3); 
    if (i < paramInt3)
      select(paramArrayOfdouble, i + 1, paramInt2, paramInt3); 
  }
  
  private static int partition(double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    int i = paramInt1 - 1;
    int j = paramInt2;
    double d = paramArrayOfdouble[paramInt2];
    while (true) {
      if (paramArrayOfdouble[++i] < d)
        continue; 
      do {
      
      } while (d < paramArrayOfdouble[--j] && j != paramInt1);
      if (i >= j) {
        double d2 = paramArrayOfdouble[i];
        paramArrayOfdouble[i] = paramArrayOfdouble[paramInt2];
        paramArrayOfdouble[paramInt2] = d2;
        return i;
      } 
      double d1 = paramArrayOfdouble[i];
      paramArrayOfdouble[i] = paramArrayOfdouble[j];
      paramArrayOfdouble[j] = d1;
    } 
  }
  
  private void selectSubSample(Instances paramInstances) throws Exception {
    this.m_SplitFilter = new RemoveRange();
    this.m_SplitFilter.setInvertSelection(true);
    this.m_SubSample = paramInstances;
    this.m_SplitFilter.setInputFormat(this.m_SubSample);
    this.m_SplitFilter.setInstancesIndices(selectIndices(this.m_SubSample));
    this.m_SubSample = Filter.useFilter(this.m_SubSample, (Filter)this.m_SplitFilter);
  }
  
  private String selectIndices(Instances paramInstances) {
    StringBuffer stringBuffer = new StringBuffer();
    byte b = 0;
    int i = 0;
    while (true) {
      if (b < this.m_samplesize) {
        while (true) {
          i = (int)(this.m_random.nextDouble() * paramInstances.numInstances());
          if (i != 0) {
            stringBuffer.append(Integer.toString(i));
            if (b < this.m_samplesize - 1) {
              stringBuffer.append(",");
              break;
            } 
            stringBuffer.append("\n");
          } else {
            continue;
          } 
          b++;
        } 
      } else {
        break;
      } 
      b++;
    } 
    return stringBuffer.toString();
  }
  
  public String sampleSizeTipText() {
    return "Set the size of the random samples used to generate the least sqaured regression functions.";
  }
  
  public void setSampleSize(int paramInt) {
    this.m_samplesize = paramInt;
  }
  
  public int getSampleSize() {
    return this.m_samplesize;
  }
  
  public String randomSeedTipText() {
    return "Set the seed for selecting random subsamples of the training data.";
  }
  
  public void setRandomSeed(long paramLong) {
    this.m_randomseed = paramLong;
  }
  
  public long getRandomSeed() {
    return this.m_randomseed;
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_debug;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tSet sample size\n\t(default: 4)\n", "S", 4, "-S <sample size>"));
    vector.addElement(new Option("\tSet the seed used to generate samples\n\t(default: 0)\n", "G", 0, "-G <seed>"));
    vector.addElement(new Option("\tProduce debugging output\n\t(default no debugging output)\n", "D", 0, "-D"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0) {
      setSampleSize(Integer.parseInt(str));
    } else {
      setSampleSize(4);
    } 
    str = Utils.getOption('G', paramArrayOfString);
    if (str.length() != 0) {
      setRandomSeed(Long.parseLong(str));
    } else {
      setRandomSeed(0L);
    } 
    setDebug(Utils.getFlag('D', paramArrayOfString));
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[9];
    byte b = 0;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSampleSize();
    arrayOfString[b++] = "-G";
    arrayOfString[b++] = "" + getRandomSeed();
    if (getDebug())
      arrayOfString[b++] = "-D"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public static int combinations(int paramInt1, int paramInt2) throws Exception {
    null = 1;
    int i = 1;
    int j = 1;
    int k = paramInt2;
    if (paramInt2 > paramInt1)
      throw new Exception("r must be less that or equal to n."); 
    paramInt2 = Math.min(paramInt2, paramInt1 - paramInt2);
    for (byte b = 1; b <= paramInt2; b++) {
      j *= paramInt1 - b + 1;
      i *= b;
    } 
    return j / i;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new LeastMedSq(), paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\LeastMedSq.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */