package weka.estimators;

import weka.core.Statistics;
import weka.core.Utils;

public class KernelEstimator implements Estimator {
  private double[] m_Values = new double[50];
  
  private double[] m_Weights = new double[50];
  
  private int m_NumValues = 0;
  
  private double m_SumOfWeights = 0.0D;
  
  private double m_StandardDev;
  
  private double m_Precision;
  
  private boolean m_AllWeightsOne = true;
  
  private static double MAX_ERROR = 0.01D;
  
  private int findNearestValue(double paramDouble) {
    int i = 0;
    int j = this.m_NumValues;
    int k = 0;
    while (i < j) {
      k = (i + j) / 2;
      double d = this.m_Values[k];
      if (d == paramDouble)
        return k; 
      if (d > paramDouble) {
        j = k;
        continue;
      } 
      if (d < paramDouble)
        i = k + 1; 
    } 
    return i;
  }
  
  private double round(double paramDouble) {
    return Math.rint(paramDouble / this.m_Precision) * this.m_Precision;
  }
  
  public KernelEstimator(double paramDouble) {
    this.m_Precision = paramDouble;
    if (this.m_Precision < Utils.SMALL)
      this.m_Precision = Utils.SMALL; 
    this.m_StandardDev = this.m_Precision / 6.0D;
  }
  
  public void addValue(double paramDouble1, double paramDouble2) {
    if (paramDouble2 == 0.0D)
      return; 
    paramDouble1 = round(paramDouble1);
    int i = findNearestValue(paramDouble1);
    if (this.m_NumValues <= i || this.m_Values[i] != paramDouble1) {
      if (this.m_NumValues < this.m_Values.length) {
        int j = this.m_NumValues - i;
        System.arraycopy(this.m_Values, i, this.m_Values, i + 1, j);
        System.arraycopy(this.m_Weights, i, this.m_Weights, i + 1, j);
        this.m_Values[i] = paramDouble1;
        this.m_Weights[i] = paramDouble2;
        this.m_NumValues++;
      } else {
        double[] arrayOfDouble1 = new double[this.m_Values.length * 2];
        double[] arrayOfDouble2 = new double[this.m_Values.length * 2];
        int j = this.m_NumValues - i;
        System.arraycopy(this.m_Values, 0, arrayOfDouble1, 0, i);
        System.arraycopy(this.m_Weights, 0, arrayOfDouble2, 0, i);
        arrayOfDouble1[i] = paramDouble1;
        arrayOfDouble2[i] = paramDouble2;
        System.arraycopy(this.m_Values, i, arrayOfDouble1, i + 1, j);
        System.arraycopy(this.m_Weights, i, arrayOfDouble2, i + 1, j);
        this.m_NumValues++;
        this.m_Values = arrayOfDouble1;
        this.m_Weights = arrayOfDouble2;
      } 
      if (paramDouble2 != 1.0D)
        this.m_AllWeightsOne = false; 
    } else {
      this.m_Weights[i] = this.m_Weights[i] + paramDouble2;
      this.m_AllWeightsOne = false;
    } 
    this.m_SumOfWeights += paramDouble2;
    double d = this.m_Values[this.m_NumValues - 1] - this.m_Values[0];
    if (d > 0.0D)
      this.m_StandardDev = Math.max(d / Math.sqrt(this.m_SumOfWeights), this.m_Precision / 6.0D); 
  }
  
  public double getProbability(double paramDouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    double d5 = 0.0D;
    if (this.m_NumValues == 0) {
      d4 = (paramDouble - this.m_Precision / 2.0D) / this.m_StandardDev;
      d5 = (paramDouble + this.m_Precision / 2.0D) / this.m_StandardDev;
      return Statistics.normalProbability(d5) - Statistics.normalProbability(d4);
    } 
    double d6 = 0.0D;
    int i = findNearestValue(paramDouble);
    int j;
    for (j = i; j < this.m_NumValues; j++) {
      d1 = this.m_Values[j] - paramDouble;
      d4 = (d1 - this.m_Precision / 2.0D) / this.m_StandardDev;
      d5 = (d1 + this.m_Precision / 2.0D) / this.m_StandardDev;
      d3 = Statistics.normalProbability(d5) - Statistics.normalProbability(d4);
      d2 += d3 * this.m_Weights[j];
      d6 += this.m_Weights[j];
      if (d3 * (this.m_SumOfWeights - d6) < d2 * MAX_ERROR)
        break; 
    } 
    for (j = i - 1; j >= 0; j--) {
      d1 = this.m_Values[j] - paramDouble;
      d4 = (d1 - this.m_Precision / 2.0D) / this.m_StandardDev;
      d5 = (d1 + this.m_Precision / 2.0D) / this.m_StandardDev;
      d3 = Statistics.normalProbability(d5) - Statistics.normalProbability(d4);
      d2 += d3 * this.m_Weights[j];
      d6 += this.m_Weights[j];
      if (d3 * (this.m_SumOfWeights - d6) < d2 * MAX_ERROR)
        break; 
    } 
    return d2 / this.m_SumOfWeights;
  }
  
  public String toString() {
    String str = this.m_NumValues + " Normal Kernels. \nStandardDev = " + Utils.doubleToString(this.m_StandardDev, 6, 4) + " Precision = " + this.m_Precision;
    if (this.m_NumValues == 0) {
      str = str + "  \nMean = 0";
    } else {
      str = str + "  \nMeans =";
      byte b;
      for (b = 0; b < this.m_NumValues; b++)
        str = str + " " + this.m_Values[b]; 
      if (!this.m_AllWeightsOne) {
        str = str + "\nWeights = ";
        for (b = 0; b < this.m_NumValues; b++)
          str = str + " " + this.m_Weights[b]; 
      } 
    } 
    return str + "\n";
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length < 2) {
        System.out.println("Please specify a set of instances.");
        return;
      } 
      KernelEstimator kernelEstimator = new KernelEstimator(0.01D);
      for (byte b = 0; b < paramArrayOfString.length - 3; b += 2)
        kernelEstimator.addValue(Double.valueOf(paramArrayOfString[b]).doubleValue(), Double.valueOf(paramArrayOfString[b + 1]).doubleValue()); 
      System.out.println(kernelEstimator);
      double d1 = Double.valueOf(paramArrayOfString[paramArrayOfString.length - 2]).doubleValue();
      double d2 = Double.valueOf(paramArrayOfString[paramArrayOfString.length - 1]).doubleValue();
      double d3;
      for (d3 = d1; d3 < d2; d3 += (d2 - d1) / 50.0D)
        System.out.println("Data: " + d3 + " " + kernelEstimator.getProbability(d3)); 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\estimators\KernelEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */