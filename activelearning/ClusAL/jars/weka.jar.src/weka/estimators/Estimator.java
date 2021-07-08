package weka.estimators;

import java.io.Serializable;

public interface Estimator extends Serializable {
  void addValue(double paramDouble1, double paramDouble2);
  
  double getProbability(double paramDouble);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\estimators\Estimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */