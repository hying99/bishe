package weka.classifiers.evaluation;

public class TwoClassStats {
  private static final String[] CATEGORY_NAMES = new String[] { "negative", "positive" };
  
  private double m_TruePos;
  
  private double m_FalsePos;
  
  private double m_TrueNeg;
  
  private double m_FalseNeg;
  
  public TwoClassStats(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    setTruePositive(paramDouble1);
    setFalsePositive(paramDouble2);
    setTrueNegative(paramDouble3);
    setFalseNegative(paramDouble4);
  }
  
  public void setTruePositive(double paramDouble) {
    this.m_TruePos = paramDouble;
  }
  
  public void setFalsePositive(double paramDouble) {
    this.m_FalsePos = paramDouble;
  }
  
  public void setTrueNegative(double paramDouble) {
    this.m_TrueNeg = paramDouble;
  }
  
  public void setFalseNegative(double paramDouble) {
    this.m_FalseNeg = paramDouble;
  }
  
  public double getTruePositive() {
    return this.m_TruePos;
  }
  
  public double getFalsePositive() {
    return this.m_FalsePos;
  }
  
  public double getTrueNegative() {
    return this.m_TrueNeg;
  }
  
  public double getFalseNegative() {
    return this.m_FalseNeg;
  }
  
  public double getTruePositiveRate() {
    return (0.0D == this.m_TruePos + this.m_FalseNeg) ? 0.0D : (this.m_TruePos / (this.m_TruePos + this.m_FalseNeg));
  }
  
  public double getFalsePositiveRate() {
    return (0.0D == this.m_FalsePos + this.m_TrueNeg) ? 0.0D : (this.m_FalsePos / (this.m_FalsePos + this.m_TrueNeg));
  }
  
  public double getPrecision() {
    return (0.0D == this.m_TruePos + this.m_FalsePos) ? 0.0D : (this.m_TruePos / (this.m_TruePos + this.m_FalsePos));
  }
  
  public double getRecall() {
    return getTruePositiveRate();
  }
  
  public double getFMeasure() {
    double d1 = getPrecision();
    double d2 = getRecall();
    return (d1 + d2 == 0.0D) ? 0.0D : (2.0D * d1 * d2 / (d1 + d2));
  }
  
  public double getFallout() {
    return (0.0D == this.m_TruePos + this.m_FalsePos) ? 0.0D : (this.m_FalsePos / (this.m_TruePos + this.m_FalsePos));
  }
  
  public ConfusionMatrix getConfusionMatrix() {
    ConfusionMatrix confusionMatrix = new ConfusionMatrix(CATEGORY_NAMES);
    confusionMatrix.setElement(0, 0, this.m_TrueNeg);
    confusionMatrix.setElement(0, 1, this.m_FalsePos);
    confusionMatrix.setElement(1, 0, this.m_FalseNeg);
    confusionMatrix.setElement(1, 1, this.m_TruePos);
    return confusionMatrix;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(getTruePositive()).append(' ');
    stringBuffer.append(getFalseNegative()).append(' ');
    stringBuffer.append(getTrueNegative()).append(' ');
    stringBuffer.append(getFalsePositive()).append(' ');
    stringBuffer.append(getFalsePositiveRate()).append(' ');
    stringBuffer.append(getTruePositiveRate()).append(' ');
    stringBuffer.append(getPrecision()).append(' ');
    stringBuffer.append(getRecall()).append(' ');
    stringBuffer.append(getFMeasure()).append(' ');
    stringBuffer.append(getFallout()).append(' ');
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\evaluation\TwoClassStats.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */