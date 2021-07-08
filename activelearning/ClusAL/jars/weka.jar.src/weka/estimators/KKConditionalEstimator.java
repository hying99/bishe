package weka.estimators;

import java.util.Random;
import weka.core.Statistics;
import weka.core.Utils;

public class KKConditionalEstimator implements ConditionalEstimator {
  private double[] m_Values = new double[50];
  
  private double[] m_CondValues = new double[50];
  
  private double[] m_Weights = new double[50];
  
  private int m_NumValues = 0;
  
  private double m_SumOfWeights = 0.0D;
  
  private double m_StandardDev = 0.0D;
  
  private boolean m_AllWeightsOne = true;
  
  private double m_Precision;
  
  private int findNearestPair(double paramDouble1, double paramDouble2) {
    int i = 0;
    int j = this.m_NumValues;
    int k = 0;
    while (i < j) {
      k = (i + j) / 2;
      double d = this.m_CondValues[k];
      if (d == paramDouble1) {
        double d1 = this.m_Values[k];
        if (d1 == paramDouble2)
          return k; 
        if (d1 > paramDouble2) {
          j = k;
        } else if (d1 < paramDouble2) {
          i = k + 1;
        } 
      } 
      if (d > paramDouble1) {
        j = k;
        continue;
      } 
      if (d < paramDouble1)
        i = k + 1; 
    } 
    return i;
  }
  
  private double round(double paramDouble) {
    return Math.rint(paramDouble / this.m_Precision) * this.m_Precision;
  }
  
  public KKConditionalEstimator(double paramDouble) {
    this.m_Precision = paramDouble;
  }
  
  public void addValue(double paramDouble1, double paramDouble2, double paramDouble3) {
    paramDouble1 = round(paramDouble1);
    paramDouble2 = round(paramDouble2);
    int i = findNearestPair(paramDouble2, paramDouble1);
    if (this.m_NumValues <= i || this.m_CondValues[i] != paramDouble2 || this.m_Values[i] != paramDouble1) {
      if (this.m_NumValues < this.m_Values.length) {
        int j = this.m_NumValues - i;
        System.arraycopy(this.m_Values, i, this.m_Values, i + 1, j);
        System.arraycopy(this.m_CondValues, i, this.m_CondValues, i + 1, j);
        System.arraycopy(this.m_Weights, i, this.m_Weights, i + 1, j);
        this.m_Values[i] = paramDouble1;
        this.m_CondValues[i] = paramDouble2;
        this.m_Weights[i] = paramDouble3;
        this.m_NumValues++;
      } else {
        double[] arrayOfDouble1 = new double[this.m_Values.length * 2];
        double[] arrayOfDouble2 = new double[this.m_Values.length * 2];
        double[] arrayOfDouble3 = new double[this.m_Values.length * 2];
        int j = this.m_NumValues - i;
        System.arraycopy(this.m_Values, 0, arrayOfDouble1, 0, i);
        System.arraycopy(this.m_CondValues, 0, arrayOfDouble2, 0, i);
        System.arraycopy(this.m_Weights, 0, arrayOfDouble3, 0, i);
        arrayOfDouble1[i] = paramDouble1;
        arrayOfDouble2[i] = paramDouble2;
        arrayOfDouble3[i] = paramDouble3;
        System.arraycopy(this.m_Values, i, arrayOfDouble1, i + 1, j);
        System.arraycopy(this.m_CondValues, i, arrayOfDouble2, i + 1, j);
        System.arraycopy(this.m_Weights, i, arrayOfDouble3, i + 1, j);
        this.m_NumValues++;
        this.m_Values = arrayOfDouble1;
        this.m_CondValues = arrayOfDouble2;
        this.m_Weights = arrayOfDouble3;
      } 
      if (paramDouble3 != 1.0D)
        this.m_AllWeightsOne = false; 
    } else {
      this.m_Weights[i] = this.m_Weights[i] + paramDouble3;
      this.m_AllWeightsOne = false;
    } 
    this.m_SumOfWeights += paramDouble3;
    double d = this.m_CondValues[this.m_NumValues - 1] - this.m_CondValues[0];
    this.m_StandardDev = Math.max(d / Math.sqrt(this.m_SumOfWeights), this.m_Precision / 6.0D);
  }
  
  public Estimator getEstimator(double paramDouble) {
    KernelEstimator kernelEstimator = new KernelEstimator(this.m_Precision);
    if (this.m_NumValues == 0)
      return kernelEstimator; 
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < this.m_NumValues; b++) {
      d1 = this.m_CondValues[b] - paramDouble;
      double d3 = (d1 - this.m_Precision / 2.0D) / this.m_StandardDev;
      double d4 = (d1 + this.m_Precision / 2.0D) / this.m_StandardDev;
      d2 = Statistics.normalProbability(d4) - Statistics.normalProbability(d3);
      kernelEstimator.addValue(this.m_Values[b], d2 * this.m_Weights[b]);
    } 
    return kernelEstimator;
  }
  
  public double getProbability(double paramDouble1, double paramDouble2) {
    return getEstimator(paramDouble2).getProbability(paramDouble1);
  }
  
  public String toString() {
    String str = "KK Conditional Estimator. " + this.m_NumValues + " Normal Kernels:\n" + "StandardDev = " + Utils.doubleToString(this.m_StandardDev, 4, 2) + "  \nMeans =";
    for (byte b = 0; b < this.m_NumValues; b++) {
      str = str + " (" + this.m_Values[b] + ", " + this.m_CondValues[b] + ")";
      if (!this.m_AllWeightsOne)
        str = str + "w=" + this.m_Weights[b]; 
    } 
    return str;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      int i = 42;
      if (paramArrayOfString.length > 0)
        i = Integer.parseInt(paramArrayOfString[0]); 
      KKConditionalEstimator kKConditionalEstimator = new KKConditionalEstimator(0.1D);
      Random random = new Random(i);
      int j = 50;
      if (paramArrayOfString.length > 2)
        j = Integer.parseInt(paramArrayOfString[2]); 
      int k;
      for (k = 0; k < j; k++) {
        int m = Math.abs(random.nextInt() % 100);
        int n = Math.abs(random.nextInt() % 100);
        System.out.println("# " + m + "  " + n);
        kKConditionalEstimator.addValue(m, n, 1.0D);
      } 
      if (paramArrayOfString.length > 1) {
        k = Integer.parseInt(paramArrayOfString[1]);
      } else {
        k = Math.abs(random.nextInt() % 100);
      } 
      System.out.println("## Conditional = " + k);
      Estimator estimator = kKConditionalEstimator.getEstimator(k);
      for (byte b = 0; b <= 100; b += 5)
        System.out.println(" " + b + "  " + estimator.getProbability(b)); 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\estimators\KKConditionalEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */