package weka.experiment;

import java.io.Serializable;
import weka.core.Instances;

public interface SplitEvaluator extends Serializable {
  void setAdditionalMeasures(String[] paramArrayOfString);
  
  String[] getKeyNames();
  
  Object[] getKeyTypes();
  
  String[] getResultNames();
  
  Object[] getResultTypes();
  
  Object[] getKey();
  
  Object[] getResult(Instances paramInstances1, Instances paramInstances2) throws Exception;
  
  String getRawResultOutput();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\SplitEvaluator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */