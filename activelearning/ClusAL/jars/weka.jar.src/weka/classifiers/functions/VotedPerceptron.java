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
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class VotedPerceptron extends Classifier implements OptionHandler {
  private int m_MaxK = 10000;
  
  private int m_NumIterations = 1;
  
  private double m_Exponent = 1.0D;
  
  private int m_K = 0;
  
  private int[] m_Additions = null;
  
  private boolean[] m_IsAddition = null;
  
  private int[] m_Weights = null;
  
  private Instances m_Train = null;
  
  private int m_Seed = 1;
  
  private NominalToBinary m_NominalToBinary;
  
  private ReplaceMissingValues m_ReplaceMissingValues;
  
  public String globalInfo() {
    return "Implementation of the voted perceptron algorithm by Freund and Schapire. Globally replaces all missing values, and transforms nominal attributes into binary ones. For more information, see:\n\nY. Freund and R. E. Schapire (1998). Large margin classification using the perceptron algorithm.  Proc. 11th Annu. Conf. on Comput. Learning Theory, pp. 209-217, ACM Press, New York, NY.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tThe number of iterations to be performed.\n\t(default 1)", "I", 1, "-I <int>"));
    vector.addElement(new Option("\tThe exponent for the polynomial kernel.\n\t(default 1)", "E", 1, "-E <double>"));
    vector.addElement(new Option("\tThe seed for the random number generation.\n\t(default 1)", "S", 1, "-S <int>"));
    vector.addElement(new Option("\tThe maximum number of alterations allowed.\n\t(default 10000)", "M", 1, "-M <int>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('I', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_NumIterations = Integer.parseInt(str1);
    } else {
      this.m_NumIterations = 1;
    } 
    String str2 = Utils.getOption('E', paramArrayOfString);
    if (str2.length() != 0) {
      this.m_Exponent = (new Double(str2)).doubleValue();
    } else {
      this.m_Exponent = 1.0D;
    } 
    String str3 = Utils.getOption('S', paramArrayOfString);
    if (str3.length() != 0) {
      this.m_Seed = Integer.parseInt(str3);
    } else {
      this.m_Seed = 1;
    } 
    String str4 = Utils.getOption('M', paramArrayOfString);
    if (str4.length() != 0) {
      this.m_MaxK = Integer.parseInt(str4);
    } else {
      this.m_MaxK = 10000;
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[8];
    byte b = 0;
    arrayOfString[b++] = "-I";
    arrayOfString[b++] = "" + this.m_NumIterations;
    arrayOfString[b++] = "-E";
    arrayOfString[b++] = "" + this.m_Exponent;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + this.m_Seed;
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + this.m_MaxK;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (paramInstances.numClasses() > 2)
      throw new Exception("Can only handle two-class datasets!"); 
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Can't handle a numeric class!"); 
    this.m_Train = new Instances(paramInstances);
    this.m_Train.deleteWithMissingClass();
    this.m_ReplaceMissingValues = new ReplaceMissingValues();
    this.m_ReplaceMissingValues.setInputFormat(this.m_Train);
    this.m_Train = Filter.useFilter(this.m_Train, (Filter)this.m_ReplaceMissingValues);
    this.m_NominalToBinary = new NominalToBinary();
    this.m_NominalToBinary.setInputFormat(this.m_Train);
    this.m_Train = Filter.useFilter(this.m_Train, (Filter)this.m_NominalToBinary);
    this.m_Train.randomize(new Random(this.m_Seed));
    this.m_Additions = new int[this.m_MaxK + 1];
    this.m_IsAddition = new boolean[this.m_MaxK + 1];
    this.m_Weights = new int[this.m_MaxK + 1];
    this.m_K = 0;
    byte b;
    label30: for (b = 0; b < this.m_NumIterations; b++) {
      for (byte b1 = 0; b1 < this.m_Train.numInstances(); b1++) {
        Instance instance = this.m_Train.instance(b1);
        if (!instance.classIsMissing()) {
          int i = makePrediction(this.m_K, instance);
          int j = (int)instance.classValue();
          if (i == j) {
            this.m_Weights[this.m_K] = this.m_Weights[this.m_K] + 1;
          } else {
            this.m_IsAddition[this.m_K] = (j == 1);
            this.m_Additions[this.m_K] = b1;
            this.m_K++;
            this.m_Weights[this.m_K] = this.m_Weights[this.m_K] + 1;
          } 
          if (this.m_K == this.m_MaxK)
            break label30; 
        } 
      } 
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    this.m_ReplaceMissingValues.input(paramInstance);
    this.m_ReplaceMissingValues.batchFinished();
    paramInstance = this.m_ReplaceMissingValues.output();
    this.m_NominalToBinary.input(paramInstance);
    this.m_NominalToBinary.batchFinished();
    paramInstance = this.m_NominalToBinary.output();
    double d1 = 0.0D;
    double d2 = 0.0D;
    if (this.m_K > 0)
      for (byte b = 0; b <= this.m_K; b++) {
        if (d2 < 0.0D) {
          d1 -= this.m_Weights[b];
        } else {
          d1 += this.m_Weights[b];
        } 
        if (this.m_IsAddition[b]) {
          d2 += innerProduct(this.m_Train.instance(this.m_Additions[b]), paramInstance);
        } else {
          d2 -= innerProduct(this.m_Train.instance(this.m_Additions[b]), paramInstance);
        } 
      }  
    double[] arrayOfDouble = new double[2];
    arrayOfDouble[1] = 1.0D / (1.0D + Math.exp(-d1));
    arrayOfDouble[0] = 1.0D - arrayOfDouble[1];
    return arrayOfDouble;
  }
  
  public String toString() {
    return "VotedPerceptron: Number of perceptrons=" + this.m_K;
  }
  
  public String maxKTipText() {
    return "The maximum number of alterations to the perceptron.";
  }
  
  public int getMaxK() {
    return this.m_MaxK;
  }
  
  public void setMaxK(int paramInt) {
    this.m_MaxK = paramInt;
  }
  
  public String numIterationsTipText() {
    return "Number of iterations to be performed.";
  }
  
  public int getNumIterations() {
    return this.m_NumIterations;
  }
  
  public void setNumIterations(int paramInt) {
    this.m_NumIterations = paramInt;
  }
  
  public String exponentTipText() {
    return "Exponent for the polynomial kernel.";
  }
  
  public double getExponent() {
    return this.m_Exponent;
  }
  
  public void setExponent(double paramDouble) {
    this.m_Exponent = paramDouble;
  }
  
  public String seedTipText() {
    return "Seed for the random number generator.";
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  private double innerProduct(Instance paramInstance1, Instance paramInstance2) throws Exception {
    double d = 0.0D;
    int i = paramInstance1.numValues();
    int j = paramInstance2.numValues();
    int k = this.m_Train.classIndex();
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < i && b2 < j) {
      int m = paramInstance1.index(b1);
      int n = paramInstance2.index(b2);
      if (m == n) {
        if (m != k)
          d += paramInstance1.valueSparse(b1) * paramInstance2.valueSparse(b2); 
        b1++;
        b2++;
        continue;
      } 
      if (m > n) {
        b2++;
        continue;
      } 
      b1++;
    } 
    d++;
    return (this.m_Exponent != 1.0D) ? Math.pow(d, this.m_Exponent) : d;
  }
  
  private int makePrediction(int paramInt, Instance paramInstance) throws Exception {
    double d = 0.0D;
    for (byte b = 0; b < paramInt; b++) {
      if (this.m_IsAddition[b]) {
        d += innerProduct(this.m_Train.instance(this.m_Additions[b]), paramInstance);
      } else {
        d -= innerProduct(this.m_Train.instance(this.m_Additions[b]), paramInstance);
      } 
    } 
    return (d < 0.0D) ? 0 : 1;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new VotedPerceptron(), paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\VotedPerceptron.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */