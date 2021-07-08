package weka.gui.beans;

import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.SimpleBeanInfo;

public class ClustererBeanInfo extends SimpleBeanInfo {
  public EventSetDescriptor[] getEventSetDescriptors() {
    try {
      return new EventSetDescriptor[] { new EventSetDescriptor(Clusterer.class, "batchClusterer", BatchClustererListener.class, "acceptClusterer"), new EventSetDescriptor(Clusterer.class, "graph", GraphListener.class, "acceptGraph"), new EventSetDescriptor(Clusterer.class, "text", TextListener.class, "acceptText") };
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  public BeanDescriptor getBeanDescriptor() {
    return new BeanDescriptor(Clusterer.class, ClustererCustomizer.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClustererBeanInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */