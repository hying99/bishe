package weka.gui.beans;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.Beans;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.JComponent;

public class BeanInstance implements Serializable {
  private static Vector COMPONENTS = new Vector();
  
  public static final int IDLE = 0;
  
  public static final int BEAN_EXECUTING = 1;
  
  private Object m_bean;
  
  private int m_x;
  
  private int m_y;
  
  public static void reset(JComponent paramJComponent) {
    removeAllBeansFromContainer(paramJComponent);
    COMPONENTS = new Vector();
  }
  
  public static void removeAllBeansFromContainer(JComponent paramJComponent) {
    if (paramJComponent != null) {
      if (COMPONENTS != null)
        for (byte b = 0; b < COMPONENTS.size(); b++) {
          BeanInstance beanInstance = COMPONENTS.elementAt(b);
          Object object = beanInstance.getBean();
          if (Beans.isInstanceOf(object, JComponent.class))
            paramJComponent.remove((JComponent)object); 
        }  
      paramJComponent.revalidate();
    } 
  }
  
  public static void addAllBeansToContainer(JComponent paramJComponent) {
    if (paramJComponent != null) {
      if (COMPONENTS != null)
        for (byte b = 0; b < COMPONENTS.size(); b++) {
          BeanInstance beanInstance = COMPONENTS.elementAt(b);
          Object object = beanInstance.getBean();
          if (Beans.isInstanceOf(object, JComponent.class))
            paramJComponent.add((JComponent)object); 
        }  
      paramJComponent.revalidate();
    } 
  }
  
  public static Vector getBeanInstances() {
    return COMPONENTS;
  }
  
  public static void setBeanInstances(Vector paramVector, JComponent paramJComponent) {
    reset(paramJComponent);
    if (paramJComponent != null) {
      for (byte b = 0; b < paramVector.size(); b++) {
        Object object = ((BeanInstance)paramVector.elementAt(b)).getBean();
        if (Beans.isInstanceOf(object, JComponent.class))
          paramJComponent.add((JComponent)object); 
      } 
      paramJComponent.revalidate();
      paramJComponent.repaint();
    } 
    COMPONENTS = paramVector;
  }
  
  public static void paintLabels(Graphics paramGraphics) {
    paramGraphics.setFont(new Font("Monospaced", 0, 10));
    FontMetrics fontMetrics = paramGraphics.getFontMetrics();
    int i = fontMetrics.getAscent();
    for (byte b = 0; b < COMPONENTS.size(); b++) {
      BeanInstance beanInstance = COMPONENTS.elementAt(b);
      if (beanInstance.getBean() instanceof Visible) {
        int j = beanInstance.getX();
        int k = beanInstance.getY();
        int m = ((JComponent)beanInstance.getBean()).getWidth();
        int n = ((JComponent)beanInstance.getBean()).getHeight();
        String str = ((Visible)beanInstance.getBean()).getVisual().getText();
        int i1 = fontMetrics.stringWidth(str);
        if (i1 < m) {
          paramGraphics.drawString(str, j + m / 2 - i1 / 2, k + n + i + 2);
        } else {
          int i2 = str.length() / 2;
          int i3 = str.length();
          byte b1 = -1;
          for (byte b2 = 0; b2 < str.length(); b2++) {
            if (str.charAt(b2) < 'a' && Math.abs(i2 - b2) < i3) {
              i3 = Math.abs(i2 - b2);
              b1 = b2;
            } 
          } 
          if (b1 != -1) {
            String str1 = str.substring(0, b1);
            String str2 = str.substring(b1, str.length());
            if (str1.length() > 1 && str2.length() > 1) {
              paramGraphics.drawString(str1, j + m / 2 - fontMetrics.stringWidth(str1) / 2, k + n + i * 1 + 2);
              paramGraphics.drawString(str2, j + m / 2 - fontMetrics.stringWidth(str2) / 2, k + n + i * 2 + 2);
            } else {
              paramGraphics.drawString(str, j + m / 2 - fontMetrics.stringWidth(str) / 2, k + n + i * 1 + 2);
            } 
          } else {
            paramGraphics.drawString(str, j + m / 2 - fontMetrics.stringWidth(str) / 2, k + n + i * 1 + 2);
          } 
        } 
      } 
    } 
  }
  
  public static BeanInstance findInstance(Point paramPoint) {
    Rectangle rectangle = new Rectangle();
    for (byte b = 0; b < COMPONENTS.size(); b++) {
      BeanInstance beanInstance = COMPONENTS.elementAt(b);
      JComponent jComponent = (JComponent)beanInstance.getBean();
      rectangle = jComponent.getBounds(rectangle);
      if (rectangle.contains(paramPoint))
        return beanInstance; 
    } 
    return null;
  }
  
  public BeanInstance(JComponent paramJComponent, Object paramObject, int paramInt1, int paramInt2) {
    this.m_bean = paramObject;
    this.m_x = paramInt1;
    this.m_y = paramInt2;
    addBean(paramJComponent);
  }
  
  public BeanInstance(JComponent paramJComponent, String paramString, int paramInt1, int paramInt2) {
    this.m_x = paramInt1;
    this.m_y = paramInt2;
    try {
      this.m_bean = Beans.instantiate(null, paramString);
    } catch (Exception exception) {
      exception.printStackTrace();
      return;
    } 
    addBean(paramJComponent);
  }
  
  public void removeBean(JComponent paramJComponent) {
    for (byte b = 0; b < COMPONENTS.size(); b++) {
      if ((BeanInstance)COMPONENTS.elementAt(b) == this) {
        System.err.println("Removing bean");
        COMPONENTS.removeElementAt(b);
      } 
    } 
    if (paramJComponent != null) {
      paramJComponent.remove((JComponent)this.m_bean);
      paramJComponent.revalidate();
      paramJComponent.repaint();
    } 
  }
  
  private void addBean(JComponent paramJComponent) {
    if (!Beans.isInstanceOf(this.m_bean, JComponent.class)) {
      System.err.println("Component is invisible!");
      return;
    } 
    COMPONENTS.addElement(this);
    JComponent jComponent = (JComponent)this.m_bean;
    Dimension dimension = jComponent.getPreferredSize();
    int i = (int)(dimension.getWidth() / 2.0D);
    int j = (int)(dimension.getHeight() / 2.0D);
    this.m_x -= i;
    this.m_y -= j;
    jComponent.setLocation(this.m_x, this.m_y);
    jComponent.validate();
    if (paramJComponent != null) {
      paramJComponent.add(jComponent);
      paramJComponent.revalidate();
    } 
  }
  
  public Object getBean() {
    return this.m_bean;
  }
  
  public int getX() {
    return this.m_x;
  }
  
  public int getY() {
    return this.m_y;
  }
  
  public int getWidth() {
    return ((JComponent)this.m_bean).getWidth();
  }
  
  public int getHeight() {
    return ((JComponent)this.m_bean).getHeight();
  }
  
  public void setX(int paramInt) {
    this.m_x = paramInt;
    ((JComponent)this.m_bean).setLocation(this.m_x, this.m_y);
    ((JComponent)this.m_bean).validate();
  }
  
  public void setY(int paramInt) {
    this.m_y = paramInt;
    ((JComponent)this.m_bean).setLocation(this.m_x, this.m_y);
    ((JComponent)this.m_bean).validate();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\BeanInstance.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */