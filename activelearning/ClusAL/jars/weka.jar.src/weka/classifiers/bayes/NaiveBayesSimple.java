package weka.classifiers.bayes;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

public class NaiveBayesSimple extends Classifier {
  private double[][][] m_Counts;
  
  private double[][] m_Means;
  
  private double[][] m_Devs;
  
  private double[] m_Priors;
  
  private Instances m_Instances;
  
  private static double NORM_CONST = Math.sqrt(6.283185307179586D);
  
  public String globalInfo() {
    return "Class for building and using a simple Naive Bayes classifier.Numeric attributes are modelled by a normal distribution. For more information, see\n\nRichard Duda and Peter Hart (1973). Pattern Classification and Scene Analysis. Wiley, New York.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    byte b1 = 0;
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Naive Bayes: Class is numeric!"); 
    this.m_Instances = new Instances(paramInstances, 0);
    this.m_Counts = new double[paramInstances.numClasses()][paramInstances.numAttributes() - 1][0];
    this.m_Means = new double[paramInstances.numClasses()][paramInstances.numAttributes() - 1];
    this.m_Devs = new double[paramInstances.numClasses()][paramInstances.numAttributes() - 1];
    this.m_Priors = new double[paramInstances.numClasses()];
    Enumeration enumeration1 = paramInstances.enumerateAttributes();
    while (enumeration1.hasMoreElements()) {
      Attribute attribute = enumeration1.nextElement();
      if (attribute.isNominal()) {
        for (byte b = 0; b < paramInstances.numClasses(); b++)
          this.m_Counts[b][b1] = new double[attribute.numValues()]; 
      } else {
        for (byte b = 0; b < paramInstances.numClasses(); b++)
          this.m_Counts[b][b1] = new double[1]; 
      } 
      b1++;
    } 
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if (!instance.classIsMissing()) {
        Enumeration enumeration3 = paramInstances.enumerateAttributes();
        for (b1 = 0; enumeration3.hasMoreElements(); b1++) {
          Attribute attribute = enumeration3.nextElement();
          if (!instance.isMissing(attribute))
            if (attribute.isNominal()) {
              this.m_Counts[(int)instance.classValue()][b1][(int)instance.value(attribute)] = this.m_Counts[(int)instance.classValue()][b1][(int)instance.value(attribute)] + 1.0D;
            } else {
              this.m_Means[(int)instance.classValue()][b1] = this.m_Means[(int)instance.classValue()][b1] + instance.value(attribute);
              this.m_Counts[(int)instance.classValue()][b1][0] = this.m_Counts[(int)instance.classValue()][b1][0] + 1.0D;
            }  
        } 
        this.m_Priors[(int)instance.classValue()] = this.m_Priors[(int)instance.classValue()] + 1.0D;
      } 
    } 
    Enumeration enumeration2 = paramInstances.enumerateAttributes();
    for (b1 = 0; enumeration2.hasMoreElements(); b1++) {
      Attribute attribute = enumeration2.nextElement();
      if (attribute.isNumeric())
        for (byte b = 0; b < paramInstances.numClasses(); b++) {
          if (this.m_Counts[b][b1][0] < 2.0D)
            throw new Exception("attribute " + attribute.name() + ": less than two values for class " + paramInstances.classAttribute().value(b)); 
          this.m_Means[b][b1] = this.m_Means[b][b1] / this.m_Counts[b][b1][0];
        }  
    } 
    enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if (!instance.classIsMissing()) {
        enumeration2 = paramInstances.enumerateAttributes();
        for (b1 = 0; enumeration2.hasMoreElements(); b1++) {
          Attribute attribute = enumeration2.nextElement();
          if (!instance.isMissing(attribute) && attribute.isNumeric())
            this.m_Devs[(int)instance.classValue()][b1] = this.m_Devs[(int)instance.classValue()][b1] + (this.m_Means[(int)instance.classValue()][b1] - instance.value(attribute)) * (this.m_Means[(int)instance.classValue()][b1] - instance.value(attribute)); 
        } 
      } 
    } 
    enumeration2 = paramInstances.enumerateAttributes();
    for (b1 = 0; enumeration2.hasMoreElements(); b1++) {
      Attribute attribute = enumeration2.nextElement();
      if (attribute.isNumeric())
        for (byte b = 0; b < paramInstances.numClasses(); b++) {
          if (this.m_Devs[b][b1] <= 0.0D)
            throw new Exception("attribute " + attribute.name() + ": standard deviation is 0 for class " + paramInstances.classAttribute().value(b)); 
          this.m_Devs[b][b1] = this.m_Devs[b][b1] / (this.m_Counts[b][b1][0] - 1.0D);
          this.m_Devs[b][b1] = Math.sqrt(this.m_Devs[b][b1]);
        }  
    } 
    enumeration2 = paramInstances.enumerateAttributes();
    for (b1 = 0; enumeration2.hasMoreElements(); b1++) {
      Attribute attribute = enumeration2.nextElement();
      if (attribute.isNominal())
        for (byte b = 0; b < paramInstances.numClasses(); b++) {
          double d1 = Utils.sum(this.m_Counts[b][b1]);
          for (byte b3 = 0; b3 < attribute.numValues(); b3++)
            this.m_Counts[b][b1][b3] = (this.m_Counts[b][b1][b3] + 1.0D) / (d1 + attribute.numValues()); 
        }  
    } 
    double d = Utils.sum(this.m_Priors);
    for (byte b2 = 0; b2 < paramInstances.numClasses(); b2++)
      this.m_Priors[b2] = (this.m_Priors[b2] + 1.0D) / (d + paramInstances.numClasses()); 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = new double[paramInstance.numClasses()];
    for (byte b = 0; b < paramInstance.numClasses(); b++) {
      arrayOfDouble[b] = 1.0D;
      Enumeration enumeration = paramInstance.enumerateAttributes();
      for (byte b1 = 0; enumeration.hasMoreElements(); b1++) {
        Attribute attribute = enumeration.nextElement();
        if (!paramInstance.isMissing(attribute))
          if (attribute.isNominal()) {
            arrayOfDouble[b] = arrayOfDouble[b] * this.m_Counts[b][b1][(int)paramInstance.value(attribute)];
          } else {
            arrayOfDouble[b] = arrayOfDouble[b] * normalDens(paramInstance.value(attribute), this.m_Means[b][b1], this.m_Devs[b][b1]);
          }  
      } 
      arrayOfDouble[b] = arrayOfDouble[b] * this.m_Priors[b];
    } 
    Utils.normalize(arrayOfDouble);
    return arrayOfDouble;
  }
  
  public String toString() {
    if (this.m_Instances == null)
      return "Naive Bayes (simple): No model built yet."; 
    try {
      StringBuffer stringBuffer = new StringBuffer("Naive Bayes (simple)");
      for (byte b = 0; b < this.m_Instances.numClasses(); b++) {
        stringBuffer.append("\n\nClass " + this.m_Instances.classAttribute().value(b) + ": P(C) = " + Utils.doubleToString(this.m_Priors[b], 10, 8) + "\n\n");
        Enumeration enumeration = this.m_Instances.enumerateAttributes();
        for (byte b1 = 0; enumeration.hasMoreElements(); b1++) {
          Attribute attribute = enumeration.nextElement();
          stringBuffer.append("Attribute " + attribute.name() + "\n");
          if (attribute.isNominal()) {
            byte b2;
            for (b2 = 0; b2 < attribute.numValues(); b2++)
              stringBuffer.append(attribute.value(b2) + "\t"); 
            stringBuffer.append("\n");
            for (b2 = 0; b2 < attribute.numValues(); b2++)
              stringBuffer.append(Utils.doubleToString(this.m_Counts[b][b1][b2], 10, 8) + "\t"); 
          } else {
            stringBuffer.append("Mean: " + Utils.doubleToString(this.m_Means[b][b1], 10, 8) + "\t");
            stringBuffer.append("Standard Deviation: " + Utils.doubleToString(this.m_Devs[b][b1], 10, 8));
          } 
          stringBuffer.append("\n\n");
        } 
      } 
      return stringBuffer.toString();
    } catch (Exception exception) {
      return "Can't print Naive Bayes classifier!";
    } 
  }
  
  private double normalDens(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d = paramDouble1 - paramDouble2;
    return 1.0D / NORM_CONST * paramDouble3 * Math.exp(-(d * d / 2.0D * paramDouble3 * paramDouble3));
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      NaiveBayesSimple naiveBayesSimple = new NaiveBayesSimple();
      System.out.println(Evaluation.evaluateModel(naiveBayesSimple, paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\NaiveBayesSimple.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */