package weka.classifiers.bayes.net.estimate;

import weka.estimators.DiscreteEstimator;

public class DiscreteEstimatorFullBayes extends DiscreteEstimatorBayes {
  public DiscreteEstimatorFullBayes(int paramInt, double paramDouble1, double paramDouble2, DiscreteEstimatorBayes paramDiscreteEstimatorBayes1, DiscreteEstimatorBayes paramDiscreteEstimatorBayes2, double paramDouble3) {
    super(paramInt, paramDouble3);
    for (byte b = 0; b < this.m_nSymbols; b++) {
      double d1 = paramDiscreteEstimatorBayes1.getProbability(b);
      double d2 = paramDiscreteEstimatorBayes2.getProbability(b);
      this.m_Counts[b] = paramDouble1 * d1 + paramDouble2 * d2;
      this.m_SumOfCounts += this.m_Counts[b];
    } 
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\estimate\DiscreteEstimatorFullBayes.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */