package weka.classifiers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.rules.ZeroR;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class BVDecompose implements OptionHandler {
  protected boolean m_Debug;
  
  protected Classifier m_Classifier = (Classifier)new ZeroR();
  
  protected String[] m_ClassifierOptions;
  
  protected int m_TrainIterations = 50;
  
  protected String m_DataFileName;
  
  protected int m_ClassIndex = -1;
  
  protected int m_Seed = 1;
  
  protected double m_Bias;
  
  protected double m_Variance;
  
  protected double m_Sigma;
  
  protected double m_Error;
  
  protected int m_TrainPoolSize = 100;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(7);
    vector.addElement(new Option("\tThe index of the class attribute.\n\t(default last)", "c", 1, "-c <class index>"));
    vector.addElement(new Option("\tThe name of the arff file used for the decomposition.", "t", 1, "-t <name of arff file>"));
    vector.addElement(new Option("\tThe number of instances placed in the training pool.\n\tThe remainder will be used for testing. (default 100)", "T", 1, "-T <training pool size>"));
    vector.addElement(new Option("\tThe random number seed used.", "s", 1, "-s <seed>"));
    vector.addElement(new Option("\tThe number of training repetitions used.\n\t(default 50)", "x", 1, "-x <num>"));
    vector.addElement(new Option("\tTurn on debugging output.", "D", 0, "-D"));
    vector.addElement(new Option("\tFull class name of the learner used in the decomposition.\n\teg: weka.classifiers.bayes.NaiveBayes", "W", 1, "-W <classifier class name>"));
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to learner " + this.m_Classifier.getClass().getName() + ":"));
      Enumeration enumeration = this.m_Classifier.listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setDebug(Utils.getFlag('D', paramArrayOfString));
    String str1 = Utils.getOption('c', paramArrayOfString);
    if (str1.length() != 0) {
      if (str1.toLowerCase().equals("last")) {
        setClassIndex(0);
      } else if (str1.toLowerCase().equals("first")) {
        setClassIndex(1);
      } else {
        setClassIndex(Integer.parseInt(str1));
      } 
    } else {
      setClassIndex(0);
    } 
    String str2 = Utils.getOption('x', paramArrayOfString);
    if (str2.length() != 0) {
      setTrainIterations(Integer.parseInt(str2));
    } else {
      setTrainIterations(50);
    } 
    String str3 = Utils.getOption('T', paramArrayOfString);
    if (str3.length() != 0) {
      setTrainPoolSize(Integer.parseInt(str3));
    } else {
      setTrainPoolSize(100);
    } 
    String str4 = Utils.getOption('s', paramArrayOfString);
    if (str4.length() != 0) {
      setSeed(Integer.parseInt(str4));
    } else {
      setSeed(1);
    } 
    String str5 = Utils.getOption('t', paramArrayOfString);
    if (str5.length() == 0)
      throw new Exception("An arff file must be specified with the -t option."); 
    setDataFileName(str5);
    String str6 = Utils.getOption('W', paramArrayOfString);
    if (str6.length() == 0)
      throw new Exception("A learner must be specified with the -W option."); 
    setClassifier(Classifier.forName(str6, Utils.partitionOptions(paramArrayOfString)));
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler)
      arrayOfString1 = this.m_Classifier.getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 14];
    int i = 0;
    if (getDebug())
      arrayOfString2[i++] = "-D"; 
    arrayOfString2[i++] = "-c";
    arrayOfString2[i++] = "" + getClassIndex();
    arrayOfString2[i++] = "-x";
    arrayOfString2[i++] = "" + getTrainIterations();
    arrayOfString2[i++] = "-T";
    arrayOfString2[i++] = "" + getTrainPoolSize();
    arrayOfString2[i++] = "-s";
    arrayOfString2[i++] = "" + getSeed();
    if (getDataFileName() != null) {
      arrayOfString2[i++] = "-t";
      arrayOfString2[i++] = "" + getDataFileName();
    } 
    if (getClassifier() != null) {
      arrayOfString2[i++] = "-W";
      arrayOfString2[i++] = getClassifier().getClass().getName();
    } 
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public int getTrainPoolSize() {
    return this.m_TrainPoolSize;
  }
  
  public void setTrainPoolSize(int paramInt) {
    this.m_TrainPoolSize = paramInt;
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_Classifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_Classifier;
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public void setTrainIterations(int paramInt) {
    this.m_TrainIterations = paramInt;
  }
  
  public int getTrainIterations() {
    return this.m_TrainIterations;
  }
  
  public void setDataFileName(String paramString) {
    this.m_DataFileName = paramString;
  }
  
  public String getDataFileName() {
    return this.m_DataFileName;
  }
  
  public int getClassIndex() {
    return this.m_ClassIndex + 1;
  }
  
  public void setClassIndex(int paramInt) {
    this.m_ClassIndex = paramInt - 1;
  }
  
  public double getBias() {
    return this.m_Bias;
  }
  
  public double getVariance() {
    return this.m_Variance;
  }
  
  public double getSigma() {
    return this.m_Sigma;
  }
  
  public double getError() {
    return this.m_Error;
  }
  
  public void decompose() throws Exception {
    BufferedReader bufferedReader = new BufferedReader(new FileReader(this.m_DataFileName));
    Instances instances1 = new Instances(bufferedReader);
    if (this.m_ClassIndex < 0) {
      instances1.setClassIndex(instances1.numAttributes() - 1);
    } else {
      instances1.setClassIndex(this.m_ClassIndex);
    } 
    if (instances1.classAttribute().type() != 1)
      throw new Exception("Class attribute must be nominal"); 
    int i = instances1.numClasses();
    instances1.deleteWithMissingClass();
    if (instances1.checkForStringAttributes())
      throw new Exception("Can't handle string attributes!"); 
    if (instances1.numInstances() < 2 * this.m_TrainPoolSize)
      throw new Exception("The dataset must contain at least " + (2 * this.m_TrainPoolSize) + " instances"); 
    Random random = new Random(this.m_Seed);
    instances1.randomize(random);
    Instances instances2 = new Instances(instances1, 0, this.m_TrainPoolSize);
    Instances instances3 = new Instances(instances1, this.m_TrainPoolSize, instances1.numInstances() - this.m_TrainPoolSize);
    int j = instances3.numInstances();
    double[][] arrayOfDouble = new double[j][i];
    this.m_Error = 0.0D;
    byte b;
    for (b = 0; b < this.m_TrainIterations; b++) {
      if (this.m_Debug)
        System.err.println("Iteration " + (b + 1)); 
      instances2.randomize(random);
      Instances instances = new Instances(instances2, 0, this.m_TrainPoolSize / 2);
      Classifier classifier = Classifier.makeCopy(this.m_Classifier);
      classifier.buildClassifier(instances);
      for (byte b1 = 0; b1 < j; b1++) {
        int k = (int)classifier.classifyInstance(instances3.instance(b1));
        if (k != instances3.instance(b1).classValue())
          this.m_Error++; 
        arrayOfDouble[b1][k] = arrayOfDouble[b1][k] + 1.0D;
      } 
    } 
    this.m_Error /= (this.m_TrainIterations * j);
    this.m_Bias = 0.0D;
    this.m_Variance = 0.0D;
    this.m_Sigma = 0.0D;
    for (b = 0; b < j; b++) {
      Instance instance = instances3.instance(b);
      double[] arrayOfDouble1 = arrayOfDouble[b];
      double d1 = 0.0D;
      double d2 = 0.0D;
      double d3 = 0.0D;
      for (byte b1 = 0; b1 < i; b1++) {
        double d4 = (instance.classValue() == b1) ? 1.0D : 0.0D;
        double d5 = arrayOfDouble1[b1] / this.m_TrainIterations;
        d1 += (d4 - d5) * (d4 - d5) - d5 * (1.0D - d5) / (this.m_TrainIterations - 1);
        d2 += d5 * d5;
        d3 += d4 * d4;
      } 
      this.m_Bias += d1;
      this.m_Variance += 1.0D - d2;
      this.m_Sigma += 1.0D - d3;
    } 
    this.m_Bias /= (2 * j);
    this.m_Variance /= (2 * j);
    this.m_Sigma /= (2 * j);
    if (this.m_Debug)
      System.err.println("Decomposition finished"); 
  }
  
  public String toString() {
    String str = "\nBias-Variance Decomposition\n";
    if (getClassifier() == null)
      return "Invalid setup"; 
    str = str + "\nClassifier   : " + getClassifier().getClass().getName();
    if (getClassifier() instanceof OptionHandler)
      str = str + Utils.joinOptions(this.m_Classifier.getOptions()); 
    str = str + "\nData File    : " + getDataFileName();
    str = str + "\nClass Index  : ";
    if (getClassIndex() == 0) {
      str = str + "last";
    } else {
      str = str + getClassIndex();
    } 
    str = str + "\nTraining Pool: " + getTrainPoolSize();
    str = str + "\nIterations   : " + getTrainIterations();
    str = str + "\nSeed         : " + getSeed();
    str = str + "\nError        : " + Utils.doubleToString(getError(), 6, 4);
    str = str + "\nSigma^2      : " + Utils.doubleToString(getSigma(), 6, 4);
    str = str + "\nBias^2       : " + Utils.doubleToString(getBias(), 6, 4);
    str = str + "\nVariance     : " + Utils.doubleToString(getVariance(), 6, 4);
    return str + "\n";
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      BVDecompose bVDecompose = new BVDecompose();
      try {
        bVDecompose.setOptions(paramArrayOfString);
        Utils.checkForRemainingOptions(paramArrayOfString);
      } catch (Exception exception) {
        String str = exception.getMessage() + "\nBVDecompose Options:\n\n";
        Enumeration enumeration = bVDecompose.listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          str = str + option.synopsis() + "\n" + option.description() + "\n";
        } 
        throw new Exception(str);
      } 
      bVDecompose.decompose();
      System.out.println(bVDecompose.toString());
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\BVDecompose.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */