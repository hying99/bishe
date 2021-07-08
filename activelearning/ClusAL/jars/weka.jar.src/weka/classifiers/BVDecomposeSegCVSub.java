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

public class BVDecomposeSegCVSub implements OptionHandler {
  protected boolean m_Debug;
  
  protected Classifier m_Classifier = (Classifier)new ZeroR();
  
  protected String[] m_ClassifierOptions;
  
  protected int m_ClassifyIterations;
  
  protected String m_DataFileName;
  
  protected int m_ClassIndex = -1;
  
  protected int m_Seed = 1;
  
  protected double m_KWBias;
  
  protected double m_KWVariance;
  
  protected double m_KWSigma;
  
  protected double m_WBias;
  
  protected double m_WVariance;
  
  protected double m_Error;
  
  protected int m_TrainSize;
  
  protected double m_P;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(8);
    vector.addElement(new Option("\tThe index of the class attribute.\n\t(default last)", "c", 1, "-c <class index>"));
    vector.addElement(new Option("\tTurn on debugging output.", "D", 0, "-D"));
    vector.addElement(new Option("\tThe number of times each instance is classified.\n\t(default 10)", "l", 1, "-l <num>"));
    vector.addElement(new Option("\tThe average proportion of instances common between any two training sets\n", "p", 1, "-p <proportion of objects in common>"));
    vector.addElement(new Option("\tThe random number seed used.", "s", 1, "-s <seed>"));
    vector.addElement(new Option("\tThe name of the arff file used for the decomposition.", "t", 1, "-t <name of arff file>"));
    vector.addElement(new Option("\tThe number of instances in the training set.", "T", 1, "-T <number of instances in training set>"));
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
    String str2 = Utils.getOption('l', paramArrayOfString);
    if (str2.length() != 0) {
      setClassifyIterations(Integer.parseInt(str2));
    } else {
      setClassifyIterations(10);
    } 
    String str3 = Utils.getOption('p', paramArrayOfString);
    if (str3.length() != 0) {
      setP(Double.parseDouble(str3));
    } else {
      setP(-1.0D);
    } 
    String str4 = Utils.getOption('s', paramArrayOfString);
    if (str4.length() != 0) {
      setSeed(Integer.parseInt(str4));
    } else {
      setSeed(1);
    } 
    String str5 = Utils.getOption('t', paramArrayOfString);
    if (str5.length() != 0) {
      setDataFileName(str5);
    } else {
      throw new Exception("An arff file must be specified with the -t option.");
    } 
    String str6 = Utils.getOption('T', paramArrayOfString);
    if (str6.length() != 0) {
      setTrainSize(Integer.parseInt(str6));
    } else {
      setTrainSize(-1);
    } 
    String str7 = Utils.getOption('W', paramArrayOfString);
    if (str7.length() != 0) {
      setClassifier(Classifier.forName(str7, Utils.partitionOptions(paramArrayOfString)));
    } else {
      throw new Exception("A learner must be specified with the -W option.");
    } 
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
    arrayOfString2[i++] = "-l";
    arrayOfString2[i++] = "" + getClassifyIterations();
    arrayOfString2[i++] = "-p";
    arrayOfString2[i++] = "" + getP();
    arrayOfString2[i++] = "-s";
    arrayOfString2[i++] = "" + getSeed();
    if (getDataFileName() != null) {
      arrayOfString2[i++] = "-t";
      arrayOfString2[i++] = "" + getDataFileName();
    } 
    arrayOfString2[i++] = "-T";
    arrayOfString2[i++] = "" + getTrainSize();
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
  
  public void setClassifyIterations(int paramInt) {
    this.m_ClassifyIterations = paramInt;
  }
  
  public int getClassifyIterations() {
    return this.m_ClassifyIterations;
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
  
  public double getKWBias() {
    return this.m_KWBias;
  }
  
  public double getWBias() {
    return this.m_WBias;
  }
  
  public double getKWVariance() {
    return this.m_KWVariance;
  }
  
  public double getWVariance() {
    return this.m_WVariance;
  }
  
  public double getKWSigma() {
    return this.m_KWSigma;
  }
  
  public void setTrainSize(int paramInt) {
    this.m_TrainSize = paramInt;
  }
  
  public int getTrainSize() {
    return this.m_TrainSize;
  }
  
  public void setP(double paramDouble) {
    this.m_P = paramDouble;
  }
  
  public double getP() {
    return this.m_P;
  }
  
  public double getError() {
    return this.m_Error;
  }
  
  public void decompose() throws Exception {
    BufferedReader bufferedReader = new BufferedReader(new FileReader(this.m_DataFileName));
    Instances instances = new Instances(bufferedReader);
    if (this.m_ClassIndex < 0) {
      instances.setClassIndex(instances.numAttributes() - 1);
    } else {
      instances.setClassIndex(this.m_ClassIndex);
    } 
    if (instances.classAttribute().type() != 1)
      throw new Exception("Class attribute must be nominal"); 
    int m = instances.numClasses();
    instances.deleteWithMissingClass();
    if (instances.checkForStringAttributes())
      throw new Exception("Can't handle string attributes!"); 
    if (instances.numInstances() <= 2)
      throw new Exception("Dataset size must be greater than 2."); 
    if (this.m_TrainSize == -1) {
      this.m_TrainSize = (int)Math.floor(instances.numInstances() / 2.0D);
    } else if (this.m_TrainSize < 0 || this.m_TrainSize >= instances.numInstances() - 1) {
      throw new Exception("Training set size of " + this.m_TrainSize + " is invalid.");
    } 
    if (this.m_P == -1.0D) {
      this.m_P = this.m_TrainSize / (instances.numInstances() - 1.0D);
    } else if (this.m_P < this.m_TrainSize / (instances.numInstances() - 1.0D) || this.m_P >= 1.0D) {
      throw new Exception("Proportion is not in range: " + (this.m_TrainSize / (instances.numInstances() - 1.0D)) + " <= p < 1.0 ");
    } 
    int i = (int)Math.ceil(this.m_TrainSize / this.m_P + 1.0D);
    int j = (int)Math.ceil(i / (i - this.m_TrainSize));
    if (j > i)
      throw new Exception("The required number of folds is too many.Change p or the size of the training set."); 
    int k = (int)Math.floor(instances.numInstances() / i);
    double[][] arrayOfDouble = new double[instances.numInstances()][m];
    int[][] arrayOfInt = new int[j][2];
    Vector vector = new Vector(k + 1);
    Random random = new Random(this.m_Seed);
    instances.randomize(random);
    byte b1 = 0;
    int n;
    for (n = 1; n <= k + 1; n++) {
      if (n > k) {
        int[] arrayOfInt1 = new int[instances.numInstances() - k * i];
        byte b = 0;
        while (b < arrayOfInt1.length) {
          arrayOfInt1[b] = b1;
          b++;
          b1++;
        } 
        vector.add(arrayOfInt1);
      } else {
        int[] arrayOfInt1 = new int[i];
        byte b = 0;
        while (b < arrayOfInt1.length) {
          arrayOfInt1[b] = b1;
          b++;
          b1++;
        } 
        vector.add(arrayOfInt1);
      } 
    } 
    n = i % j;
    int i1 = (int)Math.ceil(i / j);
    int i2 = 0;
    byte b2;
    for (b2 = 0; b2 < j; b2++) {
      if (n != 0 && b2 == n)
        i1--; 
      arrayOfInt[b2][0] = i2;
      arrayOfInt[b2][1] = i1;
      i2 += i1;
    } 
    for (b2 = 0; b2 < this.m_ClassifyIterations; b2++) {
      for (byte b = 1; b <= k; b++) {
        int[] arrayOfInt1 = vector.get(b - 1);
        randomize(arrayOfInt1, random);
        for (byte b3 = 1; b3 <= j; b3++) {
          Instances instances1 = null;
          for (byte b4 = 1; b4 <= j; b4++) {
            if (b4 != b3) {
              int i6 = arrayOfInt[b4 - 1][0];
              i1 = arrayOfInt[b4 - 1][1];
              int i7 = i6 + i1 - 1;
              for (int i8 = i6; i8 <= i7; i8++) {
                if (instances1 == null) {
                  instances1 = new Instances(instances, arrayOfInt1[i8], 1);
                } else {
                  instances1.add(instances.instance(arrayOfInt1[i8]));
                } 
              } 
            } 
          } 
          instances1.randomize(random);
          if (getTrainSize() > instances1.numInstances())
            throw new Exception("The training set size of " + getTrainSize() + ", is greater than the training pool " + instances1.numInstances()); 
          Instances instances2 = new Instances(instances1, 0, this.m_TrainSize);
          Classifier classifier = Classifier.makeCopy(this.m_Classifier);
          classifier.buildClassifier(instances2);
          int i3 = arrayOfInt[b3 - 1][0];
          int i4 = arrayOfInt[b3 - 1][1];
          int i5 = i3 + i4 - 1;
          while (i3 <= i5) {
            Instance instance = instances.instance(arrayOfInt1[i3]);
            int i6 = (int)classifier.classifyInstance(instance);
            if (i6 != instance.classValue())
              this.m_Error++; 
            arrayOfDouble[arrayOfInt1[i3]][i6] = arrayOfDouble[arrayOfInt1[i3]][i6] + 1.0D;
            i3++;
          } 
          if (b == 1 && b3 == 1) {
            int[] arrayOfInt2 = vector.lastElement();
            for (byte b5 = 0; b5 < arrayOfInt2.length; b5++) {
              Instance instance = instances.instance(arrayOfInt2[b5]);
              int i6 = (int)classifier.classifyInstance(instance);
              if (i6 != instance.classValue())
                this.m_Error++; 
              arrayOfDouble[arrayOfInt2[b5]][i6] = arrayOfDouble[arrayOfInt2[b5]][i6] + 1.0D;
            } 
          } 
        } 
      } 
    } 
    this.m_Error /= (this.m_ClassifyIterations * instances.numInstances());
    this.m_KWBias = 0.0D;
    this.m_KWVariance = 0.0D;
    this.m_KWSigma = 0.0D;
    this.m_WBias = 0.0D;
    this.m_WVariance = 0.0D;
    for (b2 = 0; b2 < instances.numInstances(); b2++) {
      Instance instance = instances.instance(b2);
      double[] arrayOfDouble1 = arrayOfDouble[b2];
      double d1 = 0.0D;
      double d2 = 0.0D;
      double d3 = 0.0D;
      double d4 = 0.0D;
      double d5 = 0.0D;
      Vector vector1 = findCentralTendencies(arrayOfDouble1);
      if (vector1 == null)
        throw new Exception("Central tendency was null."); 
      byte b;
      for (b = 0; b < m; b++) {
        double d6 = (instance.classValue() == b) ? 1.0D : 0.0D;
        double d7 = arrayOfDouble1[b] / this.m_ClassifyIterations;
        d1 += (d6 - d7) * (d6 - d7) - d7 * (1.0D - d7) / (this.m_ClassifyIterations - 1);
        d2 += d7 * d7;
        d3 += d6 * d6;
      } 
      this.m_KWBias += d1;
      this.m_KWVariance += 1.0D - d2;
      this.m_KWSigma += 1.0D - d3;
      for (b = 0; b < vector1.size(); b++) {
        int i3 = 0;
        int i4 = 0;
        int i5 = ((Integer)vector1.get(b)).intValue();
        for (byte b3 = 0; b3 < m; b3++) {
          if (b3 != (int)instance.classValue() && b3 == i5)
            i3 = (int)(i3 + arrayOfDouble1[b3]); 
          if (b3 != (int)instance.classValue() && b3 != i5)
            i4 = (int)(i4 + arrayOfDouble1[b3]); 
        } 
        d4 += i3;
        d5 += i4;
      } 
      this.m_WBias += d4 / (vector1.size() * this.m_ClassifyIterations);
      this.m_WVariance += d5 / (vector1.size() * this.m_ClassifyIterations);
    } 
    this.m_KWBias /= 2.0D * instances.numInstances();
    this.m_KWVariance /= 2.0D * instances.numInstances();
    this.m_KWSigma /= 2.0D * instances.numInstances();
    this.m_WBias /= instances.numInstances();
    this.m_WVariance /= instances.numInstances();
    if (this.m_Debug)
      System.err.println("Decomposition finished"); 
  }
  
  public Vector findCentralTendencies(double[] paramArrayOfdouble) {
    int i = 0;
    int j = 0;
    boolean bool = false;
    Vector vector = new Vector();
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      j = (int)paramArrayOfdouble[b];
      if (j > i) {
        vector.clear();
        vector.addElement(new Integer(b));
        i = j;
      } else if (j != 0 && j == i) {
        vector.addElement(new Integer(b));
      } 
    } 
    return (i != 0) ? vector : null;
  }
  
  public String toString() {
    null = "\nBias-Variance Decomposition Segmentation, Cross Validation\nwith subsampling.\n";
    if (getClassifier() == null)
      return "Invalid setup"; 
    null = null + "\nClassifier    : " + getClassifier().getClass().getName();
    if (getClassifier() instanceof OptionHandler)
      null = null + Utils.joinOptions(this.m_Classifier.getOptions()); 
    null = null + "\nData File     : " + getDataFileName();
    null = null + "\nClass Index   : ";
    if (getClassIndex() == 0) {
      null = null + "last";
    } else {
      null = null + getClassIndex();
    } 
    null = null + "\nIterations    : " + getClassifyIterations();
    null = null + "\np             : " + getP();
    null = null + "\nTraining Size : " + getTrainSize();
    null = null + "\nSeed          : " + getSeed();
    null = null + "\n\nDefinition   : Kohavi and Wolpert";
    null = null + "\nError         :" + Utils.doubleToString(getError(), 4);
    null = null + "\nBias^2        :" + Utils.doubleToString(getKWBias(), 4);
    null = null + "\nVariance      :" + Utils.doubleToString(getKWVariance(), 4);
    null = null + "\nSigma^2       :" + Utils.doubleToString(getKWSigma(), 4);
    null = null + "\n\nDefinition   : Webb";
    null = null + "\nError         :" + Utils.doubleToString(getError(), 4);
    null = null + "\nBias          :" + Utils.doubleToString(getWBias(), 4);
    return null + "\nVariance      :" + Utils.doubleToString(getWVariance(), 4);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      BVDecomposeSegCVSub bVDecomposeSegCVSub = new BVDecomposeSegCVSub();
      try {
        bVDecomposeSegCVSub.setOptions(paramArrayOfString);
        Utils.checkForRemainingOptions(paramArrayOfString);
      } catch (Exception exception) {
        String str = exception.getMessage() + "\nBVDecompose Options:\n\n";
        Enumeration enumeration = bVDecomposeSegCVSub.listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          str = str + option.synopsis() + "\n" + option.description() + "\n";
        } 
        throw new Exception(str);
      } 
      bVDecomposeSegCVSub.decompose();
      System.out.println(bVDecomposeSegCVSub.toString());
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
  
  public final void randomize(int[] paramArrayOfint, Random paramRandom) {
    for (int i = paramArrayOfint.length - 1; i > 0; i--) {
      int j = paramRandom.nextInt(i + 1);
      int k = paramArrayOfint[i];
      paramArrayOfint[i] = paramArrayOfint[j];
      paramArrayOfint[j] = k;
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\BVDecomposeSegCVSub.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */