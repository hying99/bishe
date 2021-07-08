package weka.estimators;

import weka.core.Statistics;
import weka.core.Utils;

public class NormalEstimator implements Estimator {
  private double m_SumOfWeights;
  
  private double m_SumOfValues;
  
  private double m_SumOfValuesSq;
  
  private double m_Mean;
  
  private double m_StandardDev;
  
  private double m_Precision;
  
  private double round(double paramDouble) {
    return Math.rint(paramDouble / this.m_Precision) * this.m_Precision;
  }
  
  public NormalEstimator(double paramDouble) {
    this.m_Precision = paramDouble;
    this.m_StandardDev = this.m_Precision / 6.0D;
  }
  
  public void addValue(double paramDouble1, double paramDouble2) {
    if (paramDouble2 == 0.0D)
      return; 
    paramDouble1 = round(paramDouble1);
    this.m_SumOfWeights += paramDouble2;
    this.m_SumOfValues += paramDouble1 * paramDouble2;
    this.m_SumOfValuesSq += paramDouble1 * paramDouble1 * paramDouble2;
    if (this.m_SumOfWeights > 0.0D) {
      this.m_Mean = this.m_SumOfValues / this.m_SumOfWeights;
      double d = Math.sqrt(Math.abs(this.m_SumOfValuesSq - this.m_Mean * this.m_SumOfValues) / this.m_SumOfWeights);
      if (d > 1.0E-10D)
        this.m_StandardDev = Math.max(this.m_Precision / 6.0D, d); 
    } 
  }
  
  public double getProbability(double paramDouble) {
    paramDouble = round(paramDouble);
    double d1 = (paramDouble - this.m_Mean - this.m_Precision / 2.0D) / this.m_StandardDev;
    double d2 = (paramDouble - this.m_Mean + this.m_Precision / 2.0D) / this.m_StandardDev;
    double d3 = Statistics.normalProbability(d1);
    double d4 = Statistics.normalProbability(d2);
    return d4 - d3;
  }
  
  public String toString() {
    return "Normal Distribution. Mean = " + Utils.doubleToString(this.m_Mean, 4) + " StandardDev = " + Utils.doubleToString(this.m_StandardDev, 4) + " WeightSum = " + Utils.doubleToString(this.m_SumOfWeights, 4) + " Precision = " + this.m_Precision + "\n";
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0) {
        System.out.println("Please specify a set of instances.");
        return;
      } 
      NormalEstimator normalEstimator = new NormalEstimator(0.01D);
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        double d = Double.valueOf(paramArrayOfString[b]).doubleValue();
        System.out.println(normalEstimator);
        System.out.println("Prediction for " + d + " = " + normalEstimator.getProbability(d));
        normalEstimator.addValue(d, 1.0D);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\estimators\NormalEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */