package weka.estimators;

import weka.core.Utils;

public class DiscreteEstimator implements Estimator {
  private double[] m_Counts;
  
  private double m_SumOfCounts;
  
  public DiscreteEstimator(int paramInt, boolean paramBoolean) {
    this.m_Counts = new double[paramInt];
    this.m_SumOfCounts = 0.0D;
    if (paramBoolean) {
      for (byte b = 0; b < paramInt; b++)
        this.m_Counts[b] = 1.0D; 
      this.m_SumOfCounts = paramInt;
    } 
  }
  
  public DiscreteEstimator(int paramInt, double paramDouble) {
    this.m_Counts = new double[paramInt];
    for (byte b = 0; b < paramInt; b++)
      this.m_Counts[b] = paramDouble; 
    this.m_SumOfCounts = paramDouble * paramInt;
  }
  
  public void addValue(double paramDouble1, double paramDouble2) {
    this.m_Counts[(int)paramDouble1] = this.m_Counts[(int)paramDouble1] + paramDouble2;
    this.m_SumOfCounts += paramDouble2;
  }
  
  public double getProbability(double paramDouble) {
    return (this.m_SumOfCounts == 0.0D) ? 0.0D : (this.m_Counts[(int)paramDouble] / this.m_SumOfCounts);
  }
  
  public int getNumSymbols() {
    return (this.m_Counts == null) ? 0 : this.m_Counts.length;
  }
  
  public double getCount(double paramDouble) {
    return (this.m_SumOfCounts == 0.0D) ? 0.0D : this.m_Counts[(int)paramDouble];
  }
  
  public double getSumOfCounts() {
    return this.m_SumOfCounts;
  }
  
  public String toString() {
    String str = "Discrete Estimator. Counts = ";
    if (this.m_SumOfCounts > 1.0D) {
      for (byte b = 0; b < this.m_Counts.length; b++)
        str = str + " " + Utils.doubleToString(this.m_Counts[b], 2); 
      str = str + "  (Total = " + Utils.doubleToString(this.m_SumOfCounts, 2) + ")\n";
    } else {
      for (byte b = 0; b < this.m_Counts.length; b++)
        str = str + " " + this.m_Counts[b]; 
      str = str + "  (Total = " + this.m_SumOfCounts + ")\n";
    } 
    return str;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0) {
        System.out.println("Please specify a set of instances.");
        return;
      } 
      int i = Integer.parseInt(paramArrayOfString[0]);
      int j = i;
      for (byte b1 = 1; b1 < paramArrayOfString.length; b1++) {
        i = Integer.parseInt(paramArrayOfString[b1]);
        if (i > j)
          j = i; 
      } 
      DiscreteEstimator discreteEstimator = new DiscreteEstimator(j + 1, true);
      for (byte b2 = 0; b2 < paramArrayOfString.length; b2++) {
        i = Integer.parseInt(paramArrayOfString[b2]);
        System.out.println(discreteEstimator);
        System.out.println("Prediction for " + i + " = " + discreteEstimator.getProbability(i));
        discreteEstimator.addValue(i, 1.0D);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\estimators\DiscreteEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */