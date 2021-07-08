package weka.gui.beans;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.JPanel;

public abstract class AbstractDataSource extends JPanel implements DataSource, Visible, Serializable, BeanContextChild {
  protected boolean m_design;
  
  protected transient BeanContext m_beanContext = null;
  
  protected BeanContextChildSupport m_bcSupport = new BeanContextChildSupport(this);
  
  protected BeanVisual m_visual = new BeanVisual("AbstractDataSource", "weka/gui/beans/icons/DefaultDataSource.gif", "weka/gui/beans/icons/DefaultDataSource_animated.gif");
  
  protected Vector m_listeners;
  
  public AbstractDataSource() {
    useDefaultVisual();
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
    this.m_listeners = new Vector();
  }
  
  public synchronized void addDataSourceListener(DataSourceListener paramDataSourceListener) {
    this.m_listeners.addElement(paramDataSourceListener);
  }
  
  public synchronized void removeDataSourceListener(DataSourceListener paramDataSourceListener) {
    this.m_listeners.remove(paramDataSourceListener);
  }
  
  public synchronized void addInstanceListener(InstanceListener paramInstanceListener) {
    this.m_listeners.addElement(paramInstanceListener);
  }
  
  public synchronized void removeInstanceListener(InstanceListener paramInstanceListener) {
    this.m_listeners.remove(paramInstanceListener);
  }
  
  public void setVisual(BeanVisual paramBeanVisual) {
    this.m_visual = paramBeanVisual;
  }
  
  public BeanVisual getVisual() {
    return this.m_visual;
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/DefaultDataSource.gif", "weka/gui/beans/icons/DefaultDataSource_animated.gif");
  }
  
  public void setBeanContext(BeanContext paramBeanContext) {
    this.m_beanContext = paramBeanContext;
    this.m_design = this.m_beanContext.isDesignTime();
  }
  
  public BeanContext getBeanContext() {
    return this.m_beanContext;
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    this.m_bcSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    this.m_bcSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) {
    this.m_bcSupport.addVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) {
    this.m_bcSupport.removeVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\AbstractDataSource.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */