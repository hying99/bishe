package weka.classifiers.functions;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Optimization;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.RemoveUseless;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class Logistic extends Classifier implements OptionHandler, WeightedInstancesHandler {
  protected double[][] m_Par;
  
  protected double[][] m_Data;
  
  protected int m_NumPredictors;
  
  protected int m_ClassIndex;
  
  protected int m_NumClasses;
  
  protected double m_Ridge = 1.0E-8D;
  
  private RemoveUseless m_AttFilter;
  
  private NominalToBinary m_NominalToBinary;
  
  private ReplaceMissingValues m_ReplaceMissingValues;
  
  protected boolean m_Debug;
  
  protected double m_LL;
  
  private int m_MaxIts = -1;
  
  public String globalInfo() {
    return "Class for building and using a multinomial logistic regression model with a ridge estimator.\n\nThere are some modifications, however, compared to the paper of leCessie and van Houwelingen(1992): \n\nIf there are k classes for n instances with m attributes, the parameter matrix B to be calculated will be an m*(k-1) matrix.\n\nThe probability for class j with the exception of the last class is\n\nPj(Xi) = exp(XiBj)/((sum[j=1..(k-1)]exp(Xi*Bj))+1) \n\nThe last class has probability\n\n1-(sum[j=1..(k-1)]Pj(Xi)) \n\t= 1/((sum[j=1..(k-1)]exp(Xi*Bj))+1)\n\nThe (negative) multinomial log-likelihood is thus: \n\nL = -sum[i=1..n]{\n\tsum[j=1..(k-1)](Yij * ln(Pj(Xi)))\n\t+(1 - (sum[j=1..(k-1)]Yij)) \n\t* ln(1 - sum[j=1..(k-1)]Pj(Xi))\n\t} + ridge * (B^2)\n\nIn order to find the matrix B for which L is minimised, a Quasi-Newton Method is used to search for the optimized values of the m*(k-1) variables.  Note that before we use the optimization procedure, we 'squeeze' the matrix B into a m*(k-1) vector.  For details of the optimization procedure, please check weka.core.Optimization class.\n\nAlthough original Logistic Regression does not deal with instance weights, we modify the algorithm a little bit to handle the instance weights.\n\nFor more information see:\n\nle Cessie, S. and van Houwelingen, J.C. (1992). Ridge Estimators in Logistic Regression.  Applied Statistics, Vol. 41, No. 1, pp. 191-201. \n\nNote: Missing values are replaced using a ReplaceMissingValuesFilter, and nominal attributes are transformed into numeric attributes using a NominalToBinaryFilter.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tTurn on debugging output.", "D", 0, "-D"));
    vector.addElement(new Option("\tSet the ridge in the log-likelihood.", "R", 1, "-R <ridge>"));
    vector.addElement(new Option("\tSet the maximum number of iterations (default -1, until convergence).", "M", 1, "-M <number>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setDebug(Utils.getFlag('D', paramArrayOfString));
    String str1 = Utils.getOption('R', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_Ridge = Double.parseDouble(str1);
    } else {
      this.m_Ridge = 1.0E-8D;
    } 
    String str2 = Utils.getOption('M', paramArrayOfString);
    if (str2.length() != 0) {
      this.m_MaxIts = Integer.parseInt(str2);
    } else {
      this.m_MaxIts = -1;
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[5];
    byte b = 0;
    if (getDebug())
      arrayOfString[b++] = "-D"; 
    arrayOfString[b++] = "-R";
    arrayOfString[b++] = "" + this.m_Ridge;
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + this.m_MaxIts;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String debugTipText() {
    return "Output debug information to the console.";
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public String ridgeTipText() {
    return "Set the Ridge value in the log-likelihood.";
  }
  
  public void setRidge(double paramDouble) {
    this.m_Ridge = paramDouble;
  }
  
  public double getRidge() {
    return this.m_Ridge;
  }
  
  public String maxItsTipText() {
    return "Maximum number of iterations to perform.";
  }
  
  public int getMaxIts() {
    return this.m_MaxIts;
  }
  
  public void setMaxIts(int paramInt) {
    this.m_MaxIts = paramInt;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.classAttribute().type() != 1)
      throw new UnsupportedClassTypeException("Class attribute must be nominal."); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    if (paramInstances.numInstances() == 0)
      throw new IllegalArgumentException("No train instances without missing class value!"); 
    this.m_ReplaceMissingValues = new ReplaceMissingValues();
    this.m_ReplaceMissingValues.setInputFormat(paramInstances);
    paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_ReplaceMissingValues);
    this.m_AttFilter = new RemoveUseless();
    this.m_AttFilter.setInputFormat(paramInstances);
    paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_AttFilter);
    this.m_NominalToBinary = new NominalToBinary();
    this.m_NominalToBinary.setInputFormat(paramInstances);
    paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_NominalToBinary);
    this.m_ClassIndex = paramInstances.classIndex();
    this.m_NumClasses = paramInstances.numClasses();
    int i = this.m_NumClasses - 1;
    int j = this.m_NumPredictors = paramInstances.numAttributes() - 1;
    int k = paramInstances.numInstances();
    this.m_Data = new double[k][j + 1];
    int[] arrayOfInt = new int[k];
    double[] arrayOfDouble1 = new double[j + 1];
    double[] arrayOfDouble2 = new double[j + 1];
    double[] arrayOfDouble3 = new double[i + 1];
    double[] arrayOfDouble4 = new double[k];
    double d = 0.0D;
    this.m_Par = new double[j + 1][i];
    if (this.m_Debug)
      System.out.println("Extracting data..."); 
    byte b1;
    for (b1 = 0; b1 < k; b1++) {
      Instance instance = paramInstances.instance(b1);
      arrayOfInt[b1] = (int)instance.classValue();
      arrayOfDouble4[b1] = instance.weight();
      d += arrayOfDouble4[b1];
      this.m_Data[b1][0] = 1.0D;
      byte b4 = 1;
      for (byte b5 = 0; b5 <= j; b5++) {
        if (b5 != this.m_ClassIndex) {
          double d1 = instance.value(b5);
          this.m_Data[b1][b4] = d1;
          arrayOfDouble1[b4] = arrayOfDouble1[b4] + arrayOfDouble4[b1] * d1;
          arrayOfDouble2[b4] = arrayOfDouble2[b4] + arrayOfDouble4[b1] * d1 * d1;
          b4++;
        } 
      } 
      arrayOfDouble3[arrayOfInt[b1]] = arrayOfDouble3[arrayOfInt[b1]] + 1.0D;
    } 
    if (d <= 1.0D && k > 1)
      throw new Exception("Sum of weights of instances less than 1, please reweight!"); 
    arrayOfDouble1[0] = 0.0D;
    arrayOfDouble2[0] = 1.0D;
    for (b1 = 1; b1 <= j; b1++) {
      arrayOfDouble1[b1] = arrayOfDouble1[b1] / d;
      if (d > 1.0D) {
        arrayOfDouble2[b1] = Math.sqrt(Math.abs(arrayOfDouble2[b1] - d * arrayOfDouble1[b1] * arrayOfDouble1[b1]) / (d - 1.0D));
      } else {
        arrayOfDouble2[b1] = 0.0D;
      } 
    } 
    if (this.m_Debug) {
      System.out.println("Descriptives...");
      for (b1 = 0; b1 <= i; b1++)
        System.out.println(arrayOfDouble3[b1] + " cases have class " + b1); 
      System.out.println("\n Variable     Avg       SD    ");
      for (b1 = 1; b1 <= j; b1++)
        System.out.println(Utils.doubleToString(b1, 8, 4) + Utils.doubleToString(arrayOfDouble1[b1], 10, 4) + Utils.doubleToString(arrayOfDouble2[b1], 10, 4)); 
    } 
    for (b1 = 0; b1 < k; b1++) {
      for (byte b = 0; b <= j; b++) {
        if (arrayOfDouble2[b] != 0.0D)
          this.m_Data[b1][b] = (this.m_Data[b1][b] - arrayOfDouble1[b]) / arrayOfDouble2[b]; 
      } 
    } 
    if (this.m_Debug)
      System.out.println("\nIteration History..."); 
    double[] arrayOfDouble5 = new double[(j + 1) * i];
    double[][] arrayOfDouble = new double[2][arrayOfDouble5.length];
    for (byte b2 = 0; b2 < i; b2++) {
      int m = b2 * (j + 1);
      arrayOfDouble5[m] = Math.log(arrayOfDouble3[b2] + 1.0D) - Math.log(arrayOfDouble3[i] + 1.0D);
      arrayOfDouble[0][m] = Double.NaN;
      arrayOfDouble[1][m] = Double.NaN;
      for (byte b = 1; b <= j; b++) {
        arrayOfDouble5[m + b] = 0.0D;
        arrayOfDouble[0][m + b] = Double.NaN;
        arrayOfDouble[1][m + b] = Double.NaN;
      } 
    } 
    OptEng optEng = new OptEng();
    optEng.setDebug(this.m_Debug);
    optEng.setWeights(arrayOfDouble4);
    optEng.setClassLabels(arrayOfInt);
    if (this.m_MaxIts == -1) {
      for (arrayOfDouble5 = optEng.findArgmin(arrayOfDouble5, arrayOfDouble); arrayOfDouble5 == null; arrayOfDouble5 = optEng.findArgmin(arrayOfDouble5, arrayOfDouble)) {
        arrayOfDouble5 = optEng.getVarbValues();
        if (this.m_Debug)
          System.out.println("200 iterations finished, not enough!"); 
      } 
      if (this.m_Debug)
        System.out.println(" -------------<Converged>--------------"); 
    } else {
      optEng.setMaxIteration(this.m_MaxIts);
      arrayOfDouble5 = optEng.findArgmin(arrayOfDouble5, arrayOfDouble);
      if (arrayOfDouble5 == null)
        arrayOfDouble5 = optEng.getVarbValues(); 
    } 
    this.m_LL = -optEng.getMinFunction();
    this.m_Data = (double[][])null;
    for (byte b3 = 0; b3 < i; b3++) {
      this.m_Par[0][b3] = arrayOfDouble5[b3 * (j + 1)];
      for (byte b = 1; b <= j; b++) {
        this.m_Par[b][b3] = arrayOfDouble5[b3 * (j + 1) + b];
        if (arrayOfDouble2[b] != 0.0D) {
          this.m_Par[b][b3] = this.m_Par[b][b3] / arrayOfDouble2[b];
          this.m_Par[0][b3] = this.m_Par[0][b3] - this.m_Par[b][b3] * arrayOfDouble1[b];
        } 
      } 
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    this.m_ReplaceMissingValues.input(paramInstance);
    paramInstance = this.m_ReplaceMissingValues.output();
    this.m_AttFilter.input(paramInstance);
    paramInstance = this.m_AttFilter.output();
    this.m_NominalToBinary.input(paramInstance);
    paramInstance = this.m_NominalToBinary.output();
    double[] arrayOfDouble = new double[this.m_NumPredictors + 1];
    byte b1 = 1;
    arrayOfDouble[0] = 1.0D;
    for (byte b2 = 0; b2 <= this.m_NumPredictors; b2++) {
      if (b2 != this.m_ClassIndex)
        arrayOfDouble[b1++] = paramInstance.value(b2); 
    } 
    return evaluateProbability(arrayOfDouble);
  }
  
  private double[] evaluateProbability(double[] paramArrayOfdouble) {
    double[] arrayOfDouble1 = new double[this.m_NumClasses];
    double[] arrayOfDouble2 = new double[this.m_NumClasses];
    byte b;
    for (b = 0; b < this.m_NumClasses - 1; b++) {
      for (byte b1 = 0; b1 <= this.m_NumPredictors; b1++)
        arrayOfDouble2[b] = arrayOfDouble2[b] + this.m_Par[b1][b] * paramArrayOfdouble[b1]; 
    } 
    arrayOfDouble2[this.m_NumClasses - 1] = 0.0D;
    for (b = 0; b < this.m_NumClasses; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < this.m_NumClasses - 1; b1++)
        d += Math.exp(arrayOfDouble2[b1] - arrayOfDouble2[b]); 
      arrayOfDouble1[b] = 1.0D / (d + Math.exp(-arrayOfDouble2[b]));
    } 
    return arrayOfDouble1;
  }
  
  public String toString() {
    int i = this.m_NumPredictors;
    String str = "Logistic Regression with ridge parameter of " + this.m_Ridge;
    if (this.m_Par == null)
      return str + ": No model built yet."; 
    str = str + "\nCoefficients...\nVariable      Coeff.\n";
    byte b;
    for (b = 1; b <= this.m_NumPredictors; b++) {
      str = str + Utils.doubleToString(b, 8, 0);
      for (byte b1 = 0; b1 < this.m_NumClasses - 1; b1++)
        str = str + " " + Utils.doubleToString(this.m_Par[b][b1], 12, 4); 
      str = str + "\n";
    } 
    str = str + "Intercept ";
    for (b = 0; b < this.m_NumClasses - 1; b++)
      str = str + " " + Utils.doubleToString(this.m_Par[0][b], 10, 4); 
    str = str + "\n";
    str = str + "\nOdds Ratios...\nVariable         O.R.\n";
    for (b = 1; b <= this.m_NumPredictors; b++) {
      str = str + Utils.doubleToString(b, 8, 0);
      for (byte b1 = 0; b1 < this.m_NumClasses - 1; b1++) {
        double d = Math.exp(this.m_Par[b][b1]);
        str = str + " " + ((d > 1.0E10D) ? ("" + d) : Utils.doubleToString(d, 12, 4));
      } 
      str = str + "\n";
    } 
    return str;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new Logistic(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  private class OptEng extends Optimization {
    private double[] weights;
    
    private int[] cls;
    
    private final Logistic this$0;
    
    private OptEng(Logistic this$0) {
      Logistic.this = Logistic.this;
    }
    
    public void setWeights(double[] param1ArrayOfdouble) {
      this.weights = param1ArrayOfdouble;
    }
    
    public void setClassLabels(int[] param1ArrayOfint) {
      this.cls = param1ArrayOfint;
    }
    
    protected double objectiveFunction(double[] param1ArrayOfdouble) {
      double d = 0.0D;
      int i = Logistic.this.m_NumPredictors + 1;
      byte b;
      for (b = 0; b < this.cls.length; b++) {
        double d3;
        double[] arrayOfDouble = new double[Logistic.this.m_NumClasses - 1];
        for (byte b1 = 0; b1 < Logistic.this.m_NumClasses - 1; b1++) {
          int j = b1 * i;
          for (byte b3 = 0; b3 < i; b3++)
            arrayOfDouble[b1] = arrayOfDouble[b1] + Logistic.this.m_Data[b][b3] * param1ArrayOfdouble[j + b3]; 
        } 
        double d1 = arrayOfDouble[Utils.maxIndex(arrayOfDouble)];
        double d2 = Math.exp(-d1);
        if (this.cls[b] == Logistic.this.m_NumClasses - 1) {
          d3 = -d1;
        } else {
          d3 = arrayOfDouble[this.cls[b]] - d1;
        } 
        for (byte b2 = 0; b2 < Logistic.this.m_NumClasses - 1; b2++)
          d2 += Math.exp(arrayOfDouble[b2] - d1); 
        d -= this.weights[b] * (d3 - Math.log(d2));
      } 
      for (b = 0; b < Logistic.this.m_NumClasses - 1; b++) {
        for (byte b1 = 1; b1 < i; b1++)
          d += Logistic.this.m_Ridge * param1ArrayOfdouble[b * i + b1] * param1ArrayOfdouble[b * i + b1]; 
      } 
      return d;
    }
    
    protected double[] evaluateGradient(double[] param1ArrayOfdouble) {
      double[] arrayOfDouble = new double[param1ArrayOfdouble.length];
      int i = Logistic.this.m_NumPredictors + 1;
      byte b;
      for (b = 0; b < this.cls.length; b++) {
        double[] arrayOfDouble1 = new double[Logistic.this.m_NumClasses - 1];
        for (byte b1 = 0; b1 < Logistic.this.m_NumClasses - 1; b1++) {
          double d = 0.0D;
          int j = b1 * i;
          for (byte b4 = 0; b4 < i; b4++)
            d += Logistic.this.m_Data[b][b4] * param1ArrayOfdouble[j + b4]; 
          arrayOfDouble1[b1] = d;
        } 
        double d1 = arrayOfDouble1[Utils.maxIndex(arrayOfDouble1)];
        double d2 = Math.exp(-d1);
        for (byte b2 = 0; b2 < Logistic.this.m_NumClasses - 1; b2++) {
          arrayOfDouble1[b2] = Math.exp(arrayOfDouble1[b2] - d1);
          d2 += arrayOfDouble1[b2];
        } 
        Utils.normalize(arrayOfDouble1, d2);
        byte b3;
        for (b3 = 0; b3 < Logistic.this.m_NumClasses - 1; b3++) {
          int j = b3 * i;
          double d = this.weights[b] * arrayOfDouble1[b3];
          for (byte b4 = 0; b4 < i; b4++)
            arrayOfDouble[j + b4] = arrayOfDouble[j + b4] + d * Logistic.this.m_Data[b][b4]; 
        } 
        if (this.cls[b] != Logistic.this.m_NumClasses - 1)
          for (b3 = 0; b3 < i; b3++)
            arrayOfDouble[this.cls[b] * i + b3] = arrayOfDouble[this.cls[b] * i + b3] - this.weights[b] * Logistic.this.m_Data[b][b3];  
      } 
      for (b = 0; b < Logistic.this.m_NumClasses - 1; b++) {
        for (byte b1 = 1; b1 < i; b1++)
          arrayOfDouble[b * i + b1] = arrayOfDouble[b * i + b1] + 2.0D * Logistic.this.m_Ridge * param1ArrayOfdouble[b * i + b1]; 
      } 
      return arrayOfDouble;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\Logistic.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */