package weka.gui.beans;

import java.beans.EventSetDescriptor;
import java.beans.SimpleBeanInfo;

public class AbstractTrainAndTestSetProducerBeanInfo extends SimpleBeanInfo {
  public EventSetDescriptor[] getEventSetDescriptors() {
    try {
      return new EventSetDescriptor[] { new EventSetDescriptor(TrainingSetProducer.class, "trainingSet", TrainingSetListener.class, "acceptTrainingSet"), new EventSetDescriptor(TestSetProducer.class, "testSet", TestSetListener.class, "acceptTestSet") };
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\AbstractTrainAndTestSetProducerBeanInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */