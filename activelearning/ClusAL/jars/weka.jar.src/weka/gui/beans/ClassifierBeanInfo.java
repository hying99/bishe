package weka.gui.beans;

import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.SimpleBeanInfo;

public class ClassifierBeanInfo extends SimpleBeanInfo {
  public EventSetDescriptor[] getEventSetDescriptors() {
    try {
      return new EventSetDescriptor[] { new EventSetDescriptor(Classifier.class, "batchClassifier", BatchClassifierListener.class, "acceptClassifier"), new EventSetDescriptor(Classifier.class, "graph", GraphListener.class, "acceptGraph"), new EventSetDescriptor(Classifier.class, "text", TextListener.class, "acceptText"), new EventSetDescriptor(Classifier.class, "incrementalClassifier", IncrementalClassifierListener.class, "acceptClassifier") };
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  public BeanDescriptor getBeanDescriptor() {
    return new BeanDescriptor(Classifier.class, ClassifierCustomizer.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClassifierBeanInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */