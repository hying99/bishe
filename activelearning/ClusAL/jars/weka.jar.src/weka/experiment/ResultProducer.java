package weka.experiment;

import java.io.Serializable;
import weka.core.Instances;

public interface ResultProducer extends Serializable {
  void setInstances(Instances paramInstances);
  
  void setResultListener(ResultListener paramResultListener);
  
  void setAdditionalMeasures(String[] paramArrayOfString);
  
  void preProcess() throws Exception;
  
  void postProcess() throws Exception;
  
  void doRun(int paramInt) throws Exception;
  
  void doRunKeys(int paramInt) throws Exception;
  
  String[] getKeyNames() throws Exception;
  
  Object[] getKeyTypes() throws Exception;
  
  String[] getResultNames() throws Exception;
  
  Object[] getResultTypes() throws Exception;
  
  String getCompatibilityState();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\ResultProducer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */