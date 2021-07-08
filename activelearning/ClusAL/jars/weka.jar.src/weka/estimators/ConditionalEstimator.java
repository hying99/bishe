package weka.estimators;

public interface ConditionalEstimator {
  void addValue(double paramDouble1, double paramDouble2, double paramDouble3);
  
  Estimator getEstimator(double paramDouble);
  
  double getProbability(double paramDouble1, double paramDouble2);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\estimators\ConditionalEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */