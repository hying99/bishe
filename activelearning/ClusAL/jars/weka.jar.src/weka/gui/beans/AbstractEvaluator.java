package weka.gui.beans;

import java.awt.BorderLayout;
import java.io.Serializable;
import javax.swing.JPanel;
import weka.gui.Logger;

public abstract class AbstractEvaluator extends JPanel implements Visible, BeanCommon, Serializable {
  protected BeanVisual m_visual = new BeanVisual("AbstractEvaluator", "weka/gui/beans/icons/DefaultEvaluator.gif", "weka/gui/beans/icons/DefaultEvaluator_animated.gif");
  
  protected Object m_listenee = null;
  
  protected transient Logger m_logger = null;
  
  public AbstractEvaluator() {
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
  }
  
  public void setVisual(BeanVisual paramBeanVisual) {
    this.m_visual = paramBeanVisual;
  }
  
  public BeanVisual getVisual() {
    return this.m_visual;
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/DefaultEvaluator.gif", "weka/gui/beans/icons/DefaultEvaluator_animated.gif");
  }
  
  public boolean connectionAllowed(String paramString) {
    return (this.m_listenee == null);
  }
  
  public synchronized void connectionNotification(String paramString, Object paramObject) {
    if (connectionAllowed(paramString))
      this.m_listenee = paramObject; 
  }
  
  public synchronized void disconnectionNotification(String paramString, Object paramObject) {
    if (this.m_listenee == paramObject)
      this.m_listenee = null; 
  }
  
  public void setLog(Logger paramLogger) {
    this.m_logger = paramLogger;
  }
  
  public abstract void stop();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\AbstractEvaluator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */