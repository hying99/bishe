package weka.classifiers.evaluation;

import java.io.Serializable;

public class NominalPrediction implements Prediction, Serializable {
  static final long serialVersionUID = -8871333992740492788L;
  
  private double[] m_Distribution;
  
  private double m_Actual = Prediction.MISSING_VALUE;
  
  private double m_Predicted = Prediction.MISSING_VALUE;
  
  private double m_Weight = 1.0D;
  
  public NominalPrediction(double paramDouble, double[] paramArrayOfdouble) {
    this(paramDouble, paramArrayOfdouble, 1.0D);
  }
  
  public NominalPrediction(double paramDouble1, double[] paramArrayOfdouble, double paramDouble2) {
    if (paramArrayOfdouble == null)
      throw new NullPointerException("Null distribution in NominalPrediction."); 
    this.m_Actual = paramDouble1;
    this.m_Distribution = paramArrayOfdouble;
    this.m_Weight = paramDouble2;
    updatePredicted();
  }
  
  public double[] distribution() {
    return this.m_Distribution;
  }
  
  public double actual() {
    return this.m_Actual;
  }
  
  public double predicted() {
    return this.m_Predicted;
  }
  
  public double weight() {
    return this.m_Weight;
  }
  
  public double margin() {
    if (this.m_Actual == Prediction.MISSING_VALUE || this.m_Predicted == Prediction.MISSING_VALUE)
      return Prediction.MISSING_VALUE; 
    double d1 = this.m_Distribution[(int)this.m_Actual];
    double d2 = 0.0D;
    for (byte b = 0; b < this.m_Distribution.length; b++) {
      if (b != this.m_Actual && this.m_Distribution[b] > d2)
        d2 = this.m_Distribution[b]; 
    } 
    return d1 - d2;
  }
  
  public static double[] makeDistribution(double paramDouble, int paramInt) {
    double[] arrayOfDouble = new double[paramInt];
    if (paramDouble == Prediction.MISSING_VALUE)
      return arrayOfDouble; 
    arrayOfDouble[(int)paramDouble] = 1.0D;
    return arrayOfDouble;
  }
  
  public static double[] makeUniformDistribution(int paramInt) {
    double[] arrayOfDouble = new double[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfDouble[b] = 1.0D / paramInt; 
    return arrayOfDouble;
  }
  
  private void updatePredicted() {
    byte b = -1;
    double d = 0.0D;
    for (byte b1 = 0; b1 < this.m_Distribution.length; b1++) {
      if (this.m_Distribution[b1] > d) {
        b = b1;
        d = this.m_Distribution[b1];
      } 
    } 
    if (b != -1) {
      this.m_Predicted = b;
    } else {
      this.m_Predicted = Prediction.MISSING_VALUE;
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("NOM: ").append(actual()).append(" ").append(predicted());
    stringBuffer.append(' ').append(weight());
    double[] arrayOfDouble = distribution();
    for (byte b = 0; b < arrayOfDouble.length; b++)
      stringBuffer.append(' ').append(arrayOfDouble[b]); 
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\evaluation\NominalPrediction.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */