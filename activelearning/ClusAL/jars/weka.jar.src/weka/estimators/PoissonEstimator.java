package weka.estimators;

import weka.core.Utils;

public class PoissonEstimator implements Estimator {
  private double m_NumValues;
  
  private double m_SumOfValues;
  
  private double m_Lambda;
  
  private double logFac(double paramDouble) {
    double d1 = 0.0D;
    double d2;
    for (d2 = 2.0D; d2 <= paramDouble; d2++)
      d1 += Math.log(d2); 
    return d1;
  }
  
  private double Poisson(double paramDouble) {
    return Math.exp(-this.m_Lambda + paramDouble * Math.log(this.m_Lambda) - logFac(paramDouble));
  }
  
  public void addValue(double paramDouble1, double paramDouble2) {
    this.m_NumValues += paramDouble2;
    this.m_SumOfValues += paramDouble1 * paramDouble2;
    if (this.m_NumValues != 0.0D)
      this.m_Lambda = this.m_SumOfValues / this.m_NumValues; 
  }
  
  public double getProbability(double paramDouble) {
    return Poisson(paramDouble);
  }
  
  public String toString() {
    return "Poisson Lambda = " + Utils.doubleToString(this.m_Lambda, 4, 2) + "\n";
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0) {
        System.out.println("Please specify a set of instances.");
        return;
      } 
      PoissonEstimator poissonEstimator = new PoissonEstimator();
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        double d = Double.valueOf(paramArrayOfString[b]).doubleValue();
        System.out.println(poissonEstimator);
        System.out.println("Prediction for " + d + " = " + poissonEstimator.getProbability(d));
        poissonEstimator.addValue(d, 1.0D);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\estimators\PoissonEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */