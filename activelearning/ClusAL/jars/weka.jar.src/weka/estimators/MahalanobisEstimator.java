package weka.estimators;

import weka.core.Matrix;
import weka.core.Utils;

public class MahalanobisEstimator implements Estimator {
  private Matrix m_CovarianceInverse = null;
  
  private double m_Determinant;
  
  private double m_ConstDelta;
  
  private double m_ValueMean;
  
  private static double TWO_PI = 6.283185307179586D;
  
  private double normalKernel(double paramDouble) {
    Matrix matrix = new Matrix(1, 2);
    matrix.setElement(0, 0, paramDouble);
    matrix.setElement(0, 1, this.m_ConstDelta);
    return Math.exp(-matrix.multiply(this.m_CovarianceInverse).multiply(matrix.transpose()).getElement(0, 0) / 2.0D) / Math.sqrt(TWO_PI) * this.m_Determinant;
  }
  
  public MahalanobisEstimator(Matrix paramMatrix, double paramDouble1, double paramDouble2) {
    if (paramMatrix.numRows() == 2 && paramMatrix.numColumns() == 2) {
      double d1 = paramMatrix.getElement(0, 0);
      double d2 = paramMatrix.getElement(0, 1);
      double d3 = paramMatrix.getElement(1, 0);
      double d4 = paramMatrix.getElement(1, 1);
      if (d1 == 0.0D) {
        d1 = d3;
        d3 = 0.0D;
        double d = d2;
        d2 = d4;
        d4 = d;
      } 
      if (d1 == 0.0D)
        return; 
      double d5 = d4 - d3 * d2 / d1;
      if (d5 == 0.0D)
        return; 
      this.m_Determinant = paramMatrix.getElement(0, 0) * paramMatrix.getElement(1, 1) - paramMatrix.getElement(1, 0) * paramMatrix.getElement(0, 1);
      this.m_CovarianceInverse = new Matrix(2, 2);
      this.m_CovarianceInverse.setElement(0, 0, 1.0D / d1 + d2 * d3 / d1 / d1 / d5);
      this.m_CovarianceInverse.setElement(0, 1, -d2 / d1 / d5);
      this.m_CovarianceInverse.setElement(1, 0, -d3 / d1 / d5);
      this.m_CovarianceInverse.setElement(1, 1, 1.0D / d5);
      this.m_ConstDelta = paramDouble1;
      this.m_ValueMean = paramDouble2;
    } 
  }
  
  public void addValue(double paramDouble1, double paramDouble2) {}
  
  public double getProbability(double paramDouble) {
    double d = paramDouble - this.m_ValueMean;
    return (this.m_CovarianceInverse == null) ? 0.0D : normalKernel(d);
  }
  
  public String toString() {
    return (this.m_CovarianceInverse == null) ? "No covariance inverse\n" : ("Mahalanovis Distribution. Mean = " + Utils.doubleToString(this.m_ValueMean, 4, 2) + "  ConditionalOffset = " + Utils.doubleToString(this.m_ConstDelta, 4, 2) + "\n" + "Covariance Matrix: Determinant = " + this.m_Determinant + "  Inverse:\n" + this.m_CovarianceInverse);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      double d1 = 0.5D;
      double d2 = 0.0D;
      double d3 = 0.0D;
      double d4 = 10.0D;
      Matrix matrix = new Matrix(2, 2);
      matrix.setElement(0, 0, 2.0D);
      matrix.setElement(0, 1, -3.0D);
      matrix.setElement(1, 0, -4.0D);
      matrix.setElement(1, 1, 5.0D);
      if (paramArrayOfString.length > 0)
        matrix.setElement(0, 0, Double.valueOf(paramArrayOfString[0]).doubleValue()); 
      if (paramArrayOfString.length > 1)
        matrix.setElement(0, 1, Double.valueOf(paramArrayOfString[1]).doubleValue()); 
      if (paramArrayOfString.length > 2)
        matrix.setElement(1, 0, Double.valueOf(paramArrayOfString[2]).doubleValue()); 
      if (paramArrayOfString.length > 3)
        matrix.setElement(1, 1, Double.valueOf(paramArrayOfString[3]).doubleValue()); 
      if (paramArrayOfString.length > 4)
        d1 = Double.valueOf(paramArrayOfString[4]).doubleValue(); 
      if (paramArrayOfString.length > 5)
        d2 = Double.valueOf(paramArrayOfString[5]).doubleValue(); 
      MahalanobisEstimator mahalanobisEstimator = new MahalanobisEstimator(matrix, d1, d2);
      if (paramArrayOfString.length > 6) {
        d3 = Double.valueOf(paramArrayOfString[6]).doubleValue();
        if (paramArrayOfString.length > 7)
          d4 = Double.valueOf(paramArrayOfString[7]).doubleValue(); 
        double d5 = (d4 - d3) / 50.0D;
        double d6;
        for (d6 = d3; d6 <= d4; d6 += d5)
          System.out.println(d6 + "  " + mahalanobisEstimator.getProbability(d6)); 
      } else {
        System.out.println("Covariance Matrix\n" + matrix);
        System.out.println(mahalanobisEstimator);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\estimators\MahalanobisEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */