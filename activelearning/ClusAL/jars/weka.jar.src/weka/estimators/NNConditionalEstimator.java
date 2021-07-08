package weka.estimators;

import java.util.Random;
import java.util.Vector;
import weka.core.Matrix;
import weka.core.Utils;

public class NNConditionalEstimator implements ConditionalEstimator {
  private Vector m_Values = new Vector();
  
  private Vector m_CondValues = new Vector();
  
  private Vector m_Weights = new Vector();
  
  private double m_SumOfWeights;
  
  private double m_CondMean;
  
  private double m_ValueMean;
  
  private Matrix m_Covariance;
  
  private boolean m_AllWeightsOne = true;
  
  private static double TWO_PI = 6.283185307179586D;
  
  private int findNearestPair(double paramDouble1, double paramDouble2) {
    int i = 0;
    int j = this.m_CondValues.size();
    int k = 0;
    while (i < j) {
      k = (i + j) / 2;
      double d = ((Double)this.m_CondValues.elementAt(k)).doubleValue();
      if (d == paramDouble1) {
        double d1 = ((Double)this.m_Values.elementAt(k)).doubleValue();
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
  
  private void calculateCovariance() {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b1 = 0; b1 < this.m_Values.size(); b1++) {
      d1 += ((Double)this.m_Values.elementAt(b1)).doubleValue() * ((Double)this.m_Weights.elementAt(b1)).doubleValue();
      d2 += ((Double)this.m_CondValues.elementAt(b1)).doubleValue() * ((Double)this.m_Weights.elementAt(b1)).doubleValue();
    } 
    this.m_ValueMean = d1 / this.m_SumOfWeights;
    this.m_CondMean = d2 / this.m_SumOfWeights;
    double d3 = 0.0D;
    double d4 = 0.0D;
    double d5 = 0.0D;
    double d6 = 0.0D;
    for (byte b2 = 0; b2 < this.m_Values.size(); b2++) {
      double d7 = ((Double)this.m_Values.elementAt(b2)).doubleValue();
      double d8 = ((Double)this.m_CondValues.elementAt(b2)).doubleValue();
      double d9 = ((Double)this.m_Weights.elementAt(b2)).doubleValue();
      d3 += (d7 - this.m_ValueMean) * (d7 - this.m_ValueMean) * d9;
      d4 += (d7 - this.m_ValueMean) * (d8 - this.m_CondMean) * d9;
      d6 += (d8 - this.m_CondMean) * (d8 - this.m_CondMean) * d9;
    } 
    d3 /= this.m_SumOfWeights - 1.0D;
    d4 /= this.m_SumOfWeights - 1.0D;
    d5 = d4;
    d6 /= this.m_SumOfWeights - 1.0D;
    this.m_Covariance = new Matrix(2, 2);
    this.m_Covariance.setElement(0, 0, d3);
    this.m_Covariance.setElement(0, 1, d4);
    this.m_Covariance.setElement(1, 0, d5);
    this.m_Covariance.setElement(1, 1, d6);
  }
  
  private double normalKernel(double paramDouble1, double paramDouble2) {
    return Math.exp(-paramDouble1 * paramDouble1 / 2.0D * paramDouble2) / Math.sqrt(paramDouble2 * TWO_PI);
  }
  
  public void addValue(double paramDouble1, double paramDouble2, double paramDouble3) {
    int i = findNearestPair(paramDouble2, paramDouble1);
    if (this.m_Values.size() <= i || ((Double)this.m_CondValues.elementAt(i)).doubleValue() != paramDouble2 || ((Double)this.m_Values.elementAt(i)).doubleValue() != paramDouble1) {
      this.m_CondValues.insertElementAt(new Double(paramDouble2), i);
      this.m_Values.insertElementAt(new Double(paramDouble1), i);
      this.m_Weights.insertElementAt(new Double(paramDouble3), i);
      if (paramDouble3 != 1.0D)
        this.m_AllWeightsOne = false; 
    } else {
      double d = ((Double)this.m_Weights.elementAt(i)).doubleValue();
      d += paramDouble3;
      this.m_Weights.setElementAt(new Double(d), i);
      this.m_AllWeightsOne = false;
    } 
    this.m_SumOfWeights += paramDouble3;
    this.m_Covariance = null;
  }
  
  public Estimator getEstimator(double paramDouble) {
    if (this.m_Covariance == null)
      calculateCovariance(); 
    return new MahalanobisEstimator(this.m_Covariance, paramDouble - this.m_CondMean, this.m_ValueMean);
  }
  
  public double getProbability(double paramDouble1, double paramDouble2) {
    return getEstimator(paramDouble2).getProbability(paramDouble1);
  }
  
  public String toString() {
    if (this.m_Covariance == null)
      calculateCovariance(); 
    null = "NN Conditional Estimator. " + this.m_CondValues.size() + " data points.  Mean = " + Utils.doubleToString(this.m_ValueMean, 4, 2) + "  Conditional mean = " + Utils.doubleToString(this.m_CondMean, 4, 2);
    return null + "  Covariance Matrix: \n" + this.m_Covariance;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      int i = 42;
      if (paramArrayOfString.length > 0)
        i = Integer.parseInt(paramArrayOfString[0]); 
      NNConditionalEstimator nNConditionalEstimator = new NNConditionalEstimator();
      Random random = new Random(i);
      int j = 50;
      if (paramArrayOfString.length > 2)
        j = Integer.parseInt(paramArrayOfString[2]); 
      int k;
      for (k = 0; k < j; k++) {
        int m = Math.abs(random.nextInt() % 100);
        int n = Math.abs(random.nextInt() % 100);
        System.out.println("# " + m + "  " + n);
        nNConditionalEstimator.addValue(m, n, 1.0D);
      } 
      if (paramArrayOfString.length > 1) {
        k = Integer.parseInt(paramArrayOfString[1]);
      } else {
        k = Math.abs(random.nextInt() % 100);
      } 
      System.out.println("## Conditional = " + k);
      Estimator estimator = nNConditionalEstimator.getEstimator(k);
      for (byte b = 0; b <= 100; b += 5)
        System.out.println(" " + b + "  " + estimator.getProbability(b)); 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\estimators\NNConditionalEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */