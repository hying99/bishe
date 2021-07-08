package weka.gui.beans;

import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class ClassValuePickerBeanInfo extends SimpleBeanInfo {
  public EventSetDescriptor[] getEventSetDescriptors() {
    try {
      return new EventSetDescriptor[] { new EventSetDescriptor(ClassValuePicker.class, "dataSet", DataSourceListener.class, "acceptDataSet") };
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      PropertyDescriptor propertyDescriptor = new PropertyDescriptor("classValueIndex", ClassValuePicker.class);
      return new PropertyDescriptor[] { propertyDescriptor };
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  public BeanDescriptor getBeanDescriptor() {
    return new BeanDescriptor(ClassValuePicker.class, ClassValuePickerCustomizer.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClassValuePickerBeanInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */