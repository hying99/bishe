package weka.classifiers.trees.j48;

import java.io.Serializable;
import weka.core.Instances;

public abstract class ModelSelection implements Serializable {
  public abstract ClassifierSplitModel selectModel(Instances paramInstances) throws Exception;
  
  public ClassifierSplitModel selectModel(Instances paramInstances1, Instances paramInstances2) throws Exception {
    throw new Exception("Model selection method not implemented");
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\ModelSelection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */