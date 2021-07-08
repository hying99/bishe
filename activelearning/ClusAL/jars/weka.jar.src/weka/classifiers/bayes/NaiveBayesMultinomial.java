package weka.classifiers.bayes;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SpecialFunctions;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class NaiveBayesMultinomial extends Classifier implements WeightedInstancesHandler {
  private double[][] probOfWordGivenClass;
  
  private double[] probOfClass;
  
  private int numAttributes;
  
  private int numClasses;
  
  private double[] lnFactorialCache = new double[] { 0.0D, 0.0D };
  
  Instances headerInfo;
  
  public String globalInfo() {
    return "Class for building and using a multinomial Naive Bayes classifier. For more information see,\n\nAndrew Mccallum, Kamal Nigam (1998) A Comparison of Event Models for Naive Bayes Text Classification";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.headerInfo = new Instances(paramInstances, 0);
    this.numClasses = paramInstances.numClasses();
    this.numAttributes = paramInstances.numAttributes();
    this.probOfWordGivenClass = new double[this.numClasses][];
    for (byte b1 = 0; b1 < this.numClasses; b1++) {
      this.probOfWordGivenClass[b1] = new double[this.numAttributes];
      for (byte b = 0; b < this.numAttributes; b++) {
        if (paramInstances.classIndex() == b) {
          if (!paramInstances.attribute(b).isNominal())
            throw new Exception("The class attribute is required to be nominal. This is currently not the case!"); 
        } else if (!paramInstances.attribute(b).isNumeric()) {
          throw new Exception("Attribute " + paramInstances.attribute(b).name() + " is not numeric! NaiveBayesMultinomial1 requires that all attributes (except the class attribute) are numeric.");
        } 
        this.probOfWordGivenClass[b1][b] = 1.0D;
      } 
    } 
    double[] arrayOfDouble1 = new double[this.numClasses];
    double[] arrayOfDouble2 = new double[this.numClasses];
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      int i = (int)instance.value(instance.classIndex());
      arrayOfDouble1[i] = arrayOfDouble1[i] + instance.weight();
      for (byte b = 0; b < instance.numValues(); b++) {
        if (instance.index(b) != instance.classIndex() && !instance.isMissing(b)) {
          double d1 = instance.valueSparse(b) * instance.weight();
          if (d1 < 0.0D)
            throw new Exception("Numeric attribute values must all be greater or equal to zero."); 
          arrayOfDouble2[i] = arrayOfDouble2[i] + d1;
          this.probOfWordGivenClass[i][instance.index(b)] = this.probOfWordGivenClass[i][instance.index(b)] + d1;
        } 
      } 
    } 
    for (byte b2 = 0; b2 < this.numClasses; b2++) {
      for (byte b = 0; b < this.numAttributes; b++)
        this.probOfWordGivenClass[b2][b] = Math.log(this.probOfWordGivenClass[b2][b] / (arrayOfDouble2[b2] + this.numAttributes - 1.0D)); 
    } 
    double d = paramInstances.sumOfWeights() + this.numClasses;
    this.probOfClass = new double[this.numClasses];
    for (byte b3 = 0; b3 < this.numClasses; b3++)
      this.probOfClass[b3] = (arrayOfDouble1[b3] + 1.0D) / d; 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble1 = new double[this.numClasses];
    double[] arrayOfDouble2 = new double[this.numClasses];
    for (byte b1 = 0; b1 < this.numClasses; b1++)
      arrayOfDouble2[b1] = probOfDocGivenClass(paramInstance, b1); 
    double d1 = arrayOfDouble2[Utils.maxIndex(arrayOfDouble2)];
    double d2 = 0.0D;
    for (byte b2 = 0; b2 < this.numClasses; b2++) {
      arrayOfDouble1[b2] = Math.exp(arrayOfDouble2[b2] - d1) * this.probOfClass[b2];
      d2 += arrayOfDouble1[b2];
    } 
    Utils.normalize(arrayOfDouble1, d2);
    return arrayOfDouble1;
  }
  
  private double probOfDocGivenClass(Instance paramInstance, int paramInt) {
    double d = 0.0D;
    for (byte b = 0; b < paramInstance.numValues(); b++) {
      if (paramInstance.index(b) != paramInstance.classIndex()) {
        double d1 = paramInstance.valueSparse(b);
        d += d1 * this.probOfWordGivenClass[paramInt][paramInstance.index(b)];
      } 
    } 
    return d;
  }
  
  public double lnFactorial(int paramInt) {
    if (paramInt < 0)
      return SpecialFunctions.lnFactorial(paramInt); 
    if (this.lnFactorialCache.length <= paramInt) {
      double[] arrayOfDouble = new double[paramInt + 1];
      System.arraycopy(this.lnFactorialCache, 0, arrayOfDouble, 0, this.lnFactorialCache.length);
      for (int i = this.lnFactorialCache.length; i < arrayOfDouble.length; i++)
        arrayOfDouble[i] = arrayOfDouble[i - 1] + Math.log(i); 
      this.lnFactorialCache = arrayOfDouble;
    } 
    return this.lnFactorialCache[paramInt];
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("The independent probability of a class\n--------------------------------------\n");
    byte b;
    for (b = 0; b < this.numClasses; b++)
      stringBuffer.append(this.headerInfo.classAttribute().value(b)).append("\t").append(Double.toString(this.probOfClass[b])).append("\n"); 
    stringBuffer.append("\nThe probability of a word given the class\n-----------------------------------------\n\t");
    for (b = 0; b < this.numClasses; b++)
      stringBuffer.append(this.headerInfo.classAttribute().value(b)).append("\t"); 
    stringBuffer.append("\n");
    for (b = 0; b < this.numAttributes; b++) {
      stringBuffer.append(this.headerInfo.attribute(b).name()).append("\t");
      for (byte b1 = 0; b1 < this.numClasses; b1++)
        stringBuffer.append(Double.toString(Math.exp(this.probOfWordGivenClass[b1][b]))).append("\t"); 
      stringBuffer.append("\n");
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new NaiveBayesMultinomial(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\NaiveBayesMultinomial.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */