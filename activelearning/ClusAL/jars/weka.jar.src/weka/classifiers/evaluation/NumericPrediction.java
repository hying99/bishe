package weka.classifiers.evaluation;

import java.io.Serializable;

public class NumericPrediction implements Prediction, Serializable {
  private double m_Actual = Prediction.MISSING_VALUE;
  
  private double m_Predicted = Prediction.MISSING_VALUE;
  
  private double m_Weight = 1.0D;
  
  public NumericPrediction(double paramDouble1, double paramDouble2) {
    this(paramDouble1, paramDouble2, 1.0D);
  }
  
  public NumericPrediction(double paramDouble1, double paramDouble2, double paramDouble3) {
    this.m_Actual = paramDouble1;
    this.m_Predicted = paramDouble2;
    this.m_Weight = paramDouble3;
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
  
  public double error() {
    return (this.m_Actual == Prediction.MISSING_VALUE || this.m_Predicted == Prediction.MISSING_VALUE) ? Prediction.MISSING_VALUE : (this.m_Predicted - this.m_Actual);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("NUM: ").append(actual()).append(' ').append(predicted());
    stringBuffer.append(' ').append(weight());
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\evaluation\NumericPrediction.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */