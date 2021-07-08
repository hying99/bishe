package weka.classifiers.evaluation;

import weka.core.Instance;

public interface Prediction {
  public static final double MISSING_VALUE = Instance.missingValue();
  
  double weight();
  
  double actual();
  
  double predicted();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\evaluation\Prediction.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */