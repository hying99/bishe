package weka.gui.beans;

import java.beans.BeanDescriptor;

public class LoaderBeanInfo extends AbstractDataSourceBeanInfo {
  public BeanDescriptor getBeanDescriptor() {
    return new BeanDescriptor(Loader.class, LoaderCustomizer.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\LoaderBeanInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */