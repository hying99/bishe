package weka.classifiers.functions;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class SimpleLinearRegression extends Classifier implements WeightedInstancesHandler {
  private Attribute m_attribute;
  
  private int m_attributeIndex;
  
  private double m_slope;
  
  private double m_intercept;
  
  private boolean m_suppressErrorMessage = false;
  
  public String globalInfo() {
    return "Learns a simple linear regression model. Picks the attribute that results in the lowest squared error. Missing values are not allowed. Can only deal with numeric attributes.";
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    if (this.m_attribute == null)
      return this.m_intercept; 
    if (paramInstance.isMissing(this.m_attribute.index()))
      throw new Exception("SimpleLinearRegression: No missing values!"); 
    return this.m_intercept + this.m_slope * paramInstance.value(this.m_attribute.index());
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (!paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Class attribute has to be numeric for regression!"); 
    if (paramInstances.numInstances() == 0)
      throw new Exception("No instances in training file!"); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    double d1 = paramInstances.meanOrMode(paramInstances.classIndex());
    double d2 = Double.MAX_VALUE;
    this.m_attribute = null;
    byte b = -1;
    double d3 = Double.NaN;
    double d4 = Double.NaN;
    for (byte b1 = 0; b1 < paramInstances.numAttributes(); b1++) {
      if (b1 != paramInstances.classIndex()) {
        if (!paramInstances.attribute(b1).isNumeric())
          throw new Exception("SimpleLinearRegression: Only numeric attributes!"); 
        this.m_attribute = paramInstances.attribute(b1);
        double d5 = paramInstances.meanOrMode(b1);
        double d6 = 0.0D;
        double d7 = 0.0D;
        this.m_slope = 0.0D;
        for (byte b2 = 0; b2 < paramInstances.numInstances(); b2++) {
          Instance instance = paramInstances.instance(b2);
          if (!instance.isMissing(b1) && !instance.classIsMissing()) {
            double d8 = instance.value(b1) - d5;
            double d9 = instance.classValue() - d1;
            double d10 = instance.weight() * d8;
            double d11 = instance.weight() * d9;
            this.m_slope += d10 * d9;
            d6 += d10 * d8;
            d7 += d11 * d9;
          } 
        } 
        if (d6 != 0.0D) {
          double d8 = this.m_slope;
          this.m_slope /= d6;
          this.m_intercept = d1 - this.m_slope * d5;
          double d9 = d7 - this.m_slope * d8;
          if (d9 < d2) {
            d2 = d9;
            b = b1;
            d3 = this.m_slope;
            d4 = this.m_intercept;
          } 
        } 
      } 
    } 
    if (b == -1) {
      if (!this.m_suppressErrorMessage)
        System.err.println("----- no useful attribute found"); 
      this.m_attribute = null;
      this.m_attributeIndex = 0;
      this.m_slope = 0.0D;
      this.m_intercept = d1;
    } else {
      this.m_attribute = paramInstances.attribute(b);
      this.m_attributeIndex = b;
      this.m_slope = d3;
      this.m_intercept = d4;
    } 
  }
  
  public boolean foundUsefulAttribute() {
    return (this.m_attribute != null);
  }
  
  public int getAttributeIndex() {
    return this.m_attributeIndex;
  }
  
  public double getSlope() {
    return this.m_slope;
  }
  
  public double getIntercept() {
    return this.m_intercept;
  }
  
  public void setSuppressErrorMessage(boolean paramBoolean) {
    this.m_suppressErrorMessage = paramBoolean;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_attribute == null) {
      stringBuffer.append("Predicting constant " + this.m_intercept);
    } else {
      stringBuffer.append("Linear regression on " + this.m_attribute.name() + "\n\n");
      stringBuffer.append(Utils.doubleToString(this.m_slope, 2) + " * " + this.m_attribute.name());
      if (this.m_intercept > 0.0D) {
        stringBuffer.append(" + " + Utils.doubleToString(this.m_intercept, 2));
      } else {
        stringBuffer.append(" - " + Utils.doubleToString(-this.m_intercept, 2));
      } 
    } 
    stringBuffer.append("\n");
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new SimpleLinearRegression(), paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\SimpleLinearRegression.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */