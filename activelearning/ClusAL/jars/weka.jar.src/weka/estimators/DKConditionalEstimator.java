package weka.estimators;

public class DKConditionalEstimator implements ConditionalEstimator {
  private KernelEstimator[] m_Estimators;
  
  private DiscreteEstimator m_Weights;
  
  public DKConditionalEstimator(int paramInt, double paramDouble) {
    this.m_Estimators = new KernelEstimator[paramInt];
    for (byte b = 0; b < paramInt; b++)
      this.m_Estimators[b] = new KernelEstimator(paramDouble); 
    this.m_Weights = new DiscreteEstimator(paramInt, true);
  }
  
  public void addValue(double paramDouble1, double paramDouble2, double paramDouble3) {
    this.m_Estimators[(int)paramDouble1].addValue(paramDouble2, paramDouble3);
    this.m_Weights.addValue((int)paramDouble1, paramDouble3);
  }
  
  public Estimator getEstimator(double paramDouble) {
    DiscreteEstimator discreteEstimator = new DiscreteEstimator(this.m_Estimators.length, false);
    for (byte b = 0; b < this.m_Estimators.length; b++)
      discreteEstimator.addValue(b, this.m_Weights.getProbability(b) * this.m_Estimators[b].getProbability(paramDouble)); 
    return discreteEstimator;
  }
  
  public double getProbability(double paramDouble1, double paramDouble2) {
    return getEstimator(paramDouble2).getProbability(paramDouble1);
  }
  
  public String toString() {
    null = "DK Conditional Estimator. " + this.m_Estimators.length + " sub-estimators:\n";
    for (byte b = 0; b < this.m_Estimators.length; b++)
      null = null + "Sub-estimator " + b + ": " + this.m_Estimators[b]; 
    return null + "Weights of each estimator given by " + this.m_Weights;
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
      DKConditionalEstimator dKConditionalEstimator = new DKConditionalEstimator(j + 1, 1.0D);
      for (byte b2 = 0; b2 < paramArrayOfString.length - 1; b2 += 2) {
        i = Integer.parseInt(paramArrayOfString[b2]);
        k = Integer.parseInt(paramArrayOfString[b2 + 1]);
        System.out.println(dKConditionalEstimator);
        System.out.println("Prediction for " + i + '|' + k + " = " + dKConditionalEstimator.getProbability(i, k));
        dKConditionalEstimator.addValue(i, k, 1.0D);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\estimators\DKConditionalEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */