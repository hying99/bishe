package weka.gui.beans;

import java.beans.EventSetDescriptor;
import java.beans.SimpleBeanInfo;

public class ClassifierPerformanceEvaluatorBeanInfo extends SimpleBeanInfo {
  public EventSetDescriptor[] getEventSetDescriptors() {
    try {
      return new EventSetDescriptor[] { new EventSetDescriptor(ClassifierPerformanceEvaluator.class, "text", TextListener.class, "acceptText"), new EventSetDescriptor(ClassifierPerformanceEvaluator.class, "ThresholdData", ThresholdDataListener.class, "acceptDataSet") };
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClassifierPerformanceEvaluatorBeanInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */