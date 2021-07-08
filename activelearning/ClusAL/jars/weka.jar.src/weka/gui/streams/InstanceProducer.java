package weka.gui.streams;

import weka.core.Instance;
import weka.core.Instances;

public interface InstanceProducer {
  void addInstanceListener(InstanceListener paramInstanceListener);
  
  void removeInstanceListener(InstanceListener paramInstanceListener);
  
  Instances outputFormat() throws Exception;
  
  Instance outputPeek() throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\streams\InstanceProducer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */