package weka.classifiers.bayes.net.estimate;

import weka.classifiers.bayes.net.search.local.Scoreable;
import weka.core.Statistics;
import weka.core.Utils;
import weka.estimators.DiscreteEstimator;
import weka.estimators.Estimator;

public class DiscreteEstimatorBayes implements Estimator, Scoreable {
  protected double[] m_Counts;
  
  protected double m_SumOfCounts;
  
  protected int m_nSymbols = 0;
  
  protected double m_fPrior = 0.0D;
  
  public DiscreteEstimatorBayes(int paramInt, double paramDouble) {
    this.m_fPrior = paramDouble;
    this.m_nSymbols = paramInt;
    this.m_Counts = new double[this.m_nSymbols];
    for (byte b = 0; b < this.m_nSymbols; b++)
      this.m_Counts[b] = this.m_fPrior; 
    this.m_SumOfCounts = this.m_fPrior * this.m_nSymbols;
  }
  
  public void addValue(double paramDouble1, double paramDouble2) {
    this.m_Counts[(int)paramDouble1] = this.m_Counts[(int)paramDouble1] + paramDouble2;
    this.m_SumOfCounts += paramDouble2;
  }
  
  public double getProbability(double paramDouble) {
    return (this.m_SumOfCounts == 0.0D) ? 0.0D : (this.m_Counts[(int)paramDouble] / this.m_SumOfCounts);
  }
  
  public double getCount(double paramDouble) {
    return (this.m_SumOfCounts == 0.0D) ? 0.0D : this.m_Counts[(int)paramDouble];
  }
  
  public int getNumSymbols() {
    return (this.m_Counts == null) ? 0 : this.m_Counts.length;
  }
  
  public double logScore(int paramInt) {
    byte b;
    double d = 0.0D;
    switch (paramInt) {
      case 0:
        for (b = 0; b < this.m_nSymbols; b++)
          d += Statistics.lnGamma(this.m_Counts[b]); 
        d -= Statistics.lnGamma(this.m_SumOfCounts);
        if (this.m_fPrior != 0.0D) {
          d -= this.m_nSymbols * Statistics.lnGamma(this.m_fPrior);
          d += Statistics.lnGamma(this.m_nSymbols * this.m_fPrior);
        } 
        break;
      case 1:
        for (b = 0; b < this.m_nSymbols; b++)
          d += Statistics.lnGamma(this.m_Counts[b]); 
        d -= Statistics.lnGamma(this.m_SumOfCounts);
        d -= this.m_nSymbols * Statistics.lnGamma(1.0D);
        d += Statistics.lnGamma(this.m_nSymbols * 1.0D);
        break;
      case 2:
      case 3:
      case 4:
        for (b = 0; b < this.m_nSymbols; b++) {
          double d1 = getProbability(b);
          d += this.m_Counts[b] * Math.log(d1);
        } 
        break;
    } 
    return d;
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\estimate\DiscreteEstimatorBayes.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */