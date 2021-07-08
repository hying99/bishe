package weka.gui.beans;

import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class StripChartBeanInfo extends SimpleBeanInfo {
  public EventSetDescriptor[] getEventSetDescriptors() {
    return new EventSetDescriptor[0];
  }
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      PropertyDescriptor propertyDescriptor1 = new PropertyDescriptor("xLabelFreq", StripChart.class);
      PropertyDescriptor propertyDescriptor2 = new PropertyDescriptor("refreshFreq", StripChart.class);
      return new PropertyDescriptor[] { propertyDescriptor1, propertyDescriptor2 };
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  public BeanDescriptor getBeanDescriptor() {
    return new BeanDescriptor(StripChart.class, StripChartCustomizer.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\StripChartBeanInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */