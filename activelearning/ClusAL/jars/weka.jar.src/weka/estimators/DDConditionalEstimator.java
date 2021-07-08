package weka.estimators;

public class DDConditionalEstimator implements ConditionalEstimator {
  private DiscreteEstimator[] m_Estimators;
  
  public DDConditionalEstimator(int paramInt1, int paramInt2, boolean paramBoolean) {
    this.m_Estimators = new DiscreteEstimator[paramInt2];
    for (byte b = 0; b < paramInt2; b++)
      this.m_Estimators[b] = new DiscreteEstimator(paramInt1, paramBoolean); 
  }
  
  public void addValue(double paramDouble1, double paramDouble2, double paramDouble3) {
    this.m_Estimators[(int)paramDouble2].addValue(paramDouble1, paramDouble3);
  }
  
  public Estimator getEstimator(double paramDouble) {
    return this.m_Estimators[(int)paramDouble];
  }
  
  public double getProbability(double paramDouble1, double paramDouble2) {
    return getEstimator(paramDouble2).getProbability(paramDouble1);
  }
  
  public String toString() {
    String str = "DD Conditional Estimator. " + this.m_Estimators.length + " sub-estimators:\n";
    for (byte b = 0; b < this.m_Estimators.length; b++)
      str = str + "Sub-estimator " + b + ": " + this.m_Estimators[b]; 
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
      int k = Integer.parseInt(paramArrayOfString[1]);
      int m = k;
      for (byte b1 = 2; b1 < paramArrayOfString.length - 1; b1 += 2) {
        i = Integer.parseInt(paramArrayOfString[b1]);
        k = Integer.parseInt(paramArrayOfString[b1 + 1]);
        if (i > j)
          j = i; 
        if (k > m)
          m = k; 
      } 
      DDConditionalEstimator dDConditionalEstimator = new DDConditionalEstimator(j + 1, m + 1, true);
      for (byte b2 = 0; b2 < paramArrayOfString.length - 1; b2 += 2) {
        i = Integer.parseInt(paramArrayOfString[b2]);
        k = Integer.parseInt(paramArrayOfString[b2 + 1]);
        System.out.println(dDConditionalEstimator);
        System.out.println("Prediction for " + i + '|' + k + " = " + dDConditionalEstimator.getProbability(i, k));
        dDConditionalEstimator.addValue(i, k, 1.0D);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\estimators\DDConditionalEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */