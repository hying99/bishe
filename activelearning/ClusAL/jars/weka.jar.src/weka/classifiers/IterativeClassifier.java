package weka.classifiers;

import weka.core.Instances;

public interface IterativeClassifier {
  void initClassifier(Instances paramInstances) throws Exception;
  
  void next(int paramInt) throws Exception;
  
  void done() throws Exception;
  
  Object clone() throws CloneNotSupportedException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\IterativeClassifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */