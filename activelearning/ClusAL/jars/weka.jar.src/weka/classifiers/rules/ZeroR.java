package weka.classifiers.rules;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class ZeroR extends Classifier implements WeightedInstancesHandler {
  private double m_ClassValue;
  
  private double[] m_Counts;
  
  private Attribute m_Class;
  
  public String globalInfo() {
    return "Class for building and using a 0-R classifier. Predicts the mean (for a numeric class) or the mode (for a nominal class).";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    byte b;
    double d = 0.0D;
    this.m_Class = paramInstances.classAttribute();
    this.m_ClassValue = 0.0D;
    switch (paramInstances.classAttribute().type()) {
      case 0:
        this.m_Counts = null;
        break;
      case 1:
        this.m_Counts = new double[paramInstances.numClasses()];
        for (b = 0; b < this.m_Counts.length; b++)
          this.m_Counts[b] = 1.0D; 
        d = paramInstances.numClasses();
        break;
      default:
        throw new Exception("ZeroR can only handle nominal and numeric class attributes.");
    } 
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if (!instance.classIsMissing()) {
        if (paramInstances.classAttribute().isNominal()) {
          this.m_Counts[(int)instance.classValue()] = this.m_Counts[(int)instance.classValue()] + instance.weight();
        } else {
          this.m_ClassValue += instance.weight() * instance.classValue();
        } 
        d += instance.weight();
      } 
    } 
    if (paramInstances.classAttribute().isNumeric()) {
      if (Utils.gr(d, 0.0D))
        this.m_ClassValue /= d; 
    } else {
      this.m_ClassValue = Utils.maxIndex(this.m_Counts);
      Utils.normalize(this.m_Counts, d);
    } 
  }
  
  public double classifyInstance(Instance paramInstance) {
    return this.m_ClassValue;
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (this.m_Counts == null) {
      double[] arrayOfDouble = new double[1];
      arrayOfDouble[0] = this.m_ClassValue;
      return arrayOfDouble;
    } 
    return (double[])this.m_Counts.clone();
  }
  
  public String toString() {
    return (this.m_Class == null) ? "ZeroR: No model built yet." : ((this.m_Counts == null) ? ("ZeroR predicts class value: " + this.m_ClassValue) : ("ZeroR predicts class value: " + this.m_Class.value((int)this.m_ClassValue)));
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new ZeroR(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\ZeroR.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */