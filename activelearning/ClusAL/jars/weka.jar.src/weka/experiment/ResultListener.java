package weka.experiment;

import java.io.Serializable;

public interface ResultListener extends Serializable {
  String[] determineColumnConstraints(ResultProducer paramResultProducer) throws Exception;
  
  void preProcess(ResultProducer paramResultProducer) throws Exception;
  
  void postProcess(ResultProducer paramResultProducer) throws Exception;
  
  void acceptResult(ResultProducer paramResultProducer, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) throws Exception;
  
  boolean isResultRequired(ResultProducer paramResultProducer, Object[] paramArrayOfObject) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\ResultListener.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */