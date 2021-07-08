package weka.gui.beans;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.Introspector;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Vector;
import javax.swing.JComponent;

public class BeanConnection implements Serializable {
  public static Vector CONNECTIONS = new Vector();
  
  private BeanInstance m_source;
  
  private BeanInstance m_target;
  
  private String m_eventName;
  
  public static void reset() {
    CONNECTIONS = new Vector();
  }
  
  public static Vector getConnections() {
    return CONNECTIONS;
  }
  
  public static void setConnections(Vector paramVector) {
    CONNECTIONS = paramVector;
  }
  
  private static boolean previousLink(BeanInstance paramBeanInstance1, BeanInstance paramBeanInstance2, int paramInt) {
    for (byte b = 0; b < CONNECTIONS.size(); b++) {
      BeanConnection beanConnection = CONNECTIONS.elementAt(b);
      BeanInstance beanInstance1 = beanConnection.getSource();
      BeanInstance beanInstance2 = beanConnection.getTarget();
      if (beanInstance1 == paramBeanInstance1 && beanInstance2 == paramBeanInstance2 && paramInt < b)
        return true; 
    } 
    return false;
  }
  
  public static void paintConnections(Graphics paramGraphics) {
    for (byte b = 0; b < CONNECTIONS.size(); b++) {
      BeanConnection beanConnection = CONNECTIONS.elementAt(b);
      BeanInstance beanInstance1 = beanConnection.getSource();
      BeanInstance beanInstance2 = beanConnection.getTarget();
      EventSetDescriptor eventSetDescriptor = beanConnection.getSourceEventSetDescriptor();
      BeanVisual beanVisual1 = (beanInstance1.getBean() instanceof Visible) ? ((Visible)beanInstance1.getBean()).getVisual() : null;
      BeanVisual beanVisual2 = (beanInstance2.getBean() instanceof Visible) ? ((Visible)beanInstance2.getBean()).getVisual() : null;
      if (beanVisual1 != null && beanVisual2 != null) {
        double d;
        Point point5;
        Point point1 = beanVisual1.getClosestConnectorPoint(new Point(beanInstance2.getX() + beanInstance2.getWidth() / 2, beanInstance2.getY() + beanInstance2.getHeight() / 2));
        Point point2 = beanVisual2.getClosestConnectorPoint(new Point(beanInstance1.getX() + beanInstance1.getWidth() / 2, beanInstance1.getY() + beanInstance1.getHeight() / 2));
        paramGraphics.setColor(Color.red);
        boolean bool = true;
        if (beanInstance1.getBean() instanceof EventConstraints && !((EventConstraints)beanInstance1.getBean()).eventGeneratable(eventSetDescriptor.getName())) {
          paramGraphics.setColor(Color.gray);
          bool = false;
        } 
        paramGraphics.drawLine((int)point1.getX(), (int)point1.getY(), (int)point2.getX(), (int)point2.getY());
        try {
          double d1 = (point1.getY() - point2.getY()) / (point1.getX() - point2.getX());
          d = Math.atan(d1);
        } catch (Exception exception) {
          d = 1.5707963267948966D;
        } 
        Point point3 = new Point(point2.x, point2.y);
        Point point4 = new Point((int)(7.0D * Math.cos(d)), (int)(7.0D * Math.sin(d)));
        if (point1.getX() >= point2.getX()) {
          point5 = new Point(point3.x + point4.x, point3.y + point4.y);
        } else {
          point5 = new Point(point3.x - point4.x, point3.y - point4.y);
        } 
        int[] arrayOfInt1 = { point3.x, point5.x + (int)(7.0D * Math.cos(d + 1.5707963267948966D)), point5.x + (int)(7.0D * Math.cos(d - 1.5707963267948966D)) };
        int[] arrayOfInt2 = { point3.y, point5.y + (int)(7.0D * Math.sin(d + 1.5707963267948966D)), point5.y + (int)(7.0D * Math.sin(d - 1.5707963267948966D)) };
        paramGraphics.fillPolygon(arrayOfInt1, arrayOfInt2, 3);
        int i = (int)point1.getX();
        i += (int)((point2.getX() - point1.getX()) / 2.0D);
        int j = (int)point1.getY();
        j += (int)((point2.getY() - point1.getY()) / 2.0D) - 2;
        paramGraphics.setColor(bool ? Color.blue : Color.gray);
        if (previousLink(beanInstance1, beanInstance2, b))
          j -= 15; 
        paramGraphics.drawString(eventSetDescriptor.getName(), i, j);
      } 
    } 
  }
  
  public static Vector getClosestConnections(Point paramPoint, int paramInt) {
    Vector vector = new Vector();
    for (byte b = 0; b < CONNECTIONS.size(); b++) {
      BeanConnection beanConnection = CONNECTIONS.elementAt(b);
      BeanInstance beanInstance1 = beanConnection.getSource();
      BeanInstance beanInstance2 = beanConnection.getTarget();
      EventSetDescriptor eventSetDescriptor = beanConnection.getSourceEventSetDescriptor();
      BeanVisual beanVisual1 = (beanInstance1.getBean() instanceof Visible) ? ((Visible)beanInstance1.getBean()).getVisual() : null;
      BeanVisual beanVisual2 = (beanInstance2.getBean() instanceof Visible) ? ((Visible)beanInstance2.getBean()).getVisual() : null;
      if (beanVisual1 != null && beanVisual2 != null) {
        Point point1 = beanVisual1.getClosestConnectorPoint(new Point(beanInstance2.getX() + beanInstance2.getWidth() / 2, beanInstance2.getY() + beanInstance2.getHeight() / 2));
        Point point2 = beanVisual2.getClosestConnectorPoint(new Point(beanInstance1.getX() + beanInstance1.getWidth() / 2, beanInstance1.getY() + beanInstance1.getHeight() / 2));
        int i = (int)Math.min(point1.getX(), point2.getX());
        int j = (int)Math.max(point1.getX(), point2.getX());
        int k = (int)Math.min(point1.getY(), point2.getY());
        int m = (int)Math.max(point1.getY(), point2.getY());
        if (paramPoint.getX() >= (i - paramInt) && paramPoint.getX() <= (j + paramInt) && paramPoint.getY() >= (k - paramInt) && paramPoint.getY() <= (m + paramInt)) {
          double d1 = point1.getY() - point2.getY();
          double d2 = point2.getX() - point1.getX();
          double d3 = point1.getX() * point2.getY() - point2.getX() * point1.getY();
          double d4 = Math.abs(d1 * paramPoint.getX() + d2 * paramPoint.getY() + d3);
          d4 /= Math.abs(Math.sqrt(d1 * d1 + d2 * d2));
          if (d4 <= paramInt)
            vector.addElement(beanConnection); 
        } 
      } 
    } 
    return vector;
  }
  
  public static void removeConnections(BeanInstance paramBeanInstance) {
    Vector vector = new Vector();
    byte b;
    for (b = 0; b < CONNECTIONS.size(); b++) {
      BeanConnection beanConnection = CONNECTIONS.elementAt(b);
      BeanInstance beanInstance1 = beanConnection.getTarget();
      BeanInstance beanInstance2 = beanConnection.getSource();
      EventSetDescriptor eventSetDescriptor = beanConnection.getSourceEventSetDescriptor();
      if (paramBeanInstance == beanInstance1) {
        try {
          Method method = eventSetDescriptor.getRemoveListenerMethod();
          Object object = beanInstance1.getBean();
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = object;
          method.invoke(beanInstance2.getBean(), arrayOfObject);
          System.err.println("Deregistering listener");
          vector.addElement(beanConnection);
        } catch (Exception exception) {
          exception.printStackTrace();
        } 
      } else if (paramBeanInstance == beanInstance2) {
        vector.addElement(beanConnection);
        if (beanInstance1.getBean() instanceof BeanCommon)
          ((BeanCommon)beanInstance1.getBean()).disconnectionNotification(eventSetDescriptor.getName(), beanInstance2.getBean()); 
      } 
    } 
    for (b = 0; b < vector.size(); b++) {
      System.err.println("removing connection");
      CONNECTIONS.removeElement(vector.elementAt(b));
    } 
  }
  
  public BeanConnection(BeanInstance paramBeanInstance1, BeanInstance paramBeanInstance2, EventSetDescriptor paramEventSetDescriptor) {
    this.m_source = paramBeanInstance1;
    this.m_target = paramBeanInstance2;
    this.m_eventName = paramEventSetDescriptor.getName();
    System.err.println(this.m_eventName);
    Method method = paramEventSetDescriptor.getAddListenerMethod();
    Object object = this.m_target.getBean();
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = object;
    Class clazz = paramEventSetDescriptor.getListenerType();
    if (clazz.isInstance(object)) {
      try {
        method.invoke(this.m_source.getBean(), arrayOfObject);
        if (object instanceof BeanCommon)
          ((BeanCommon)object).connectionNotification(paramEventSetDescriptor.getName(), this.m_source.getBean()); 
        CONNECTIONS.addElement(this);
      } catch (Exception exception) {
        System.err.println("Unable to connect beans (BeanConnection)");
        exception.printStackTrace();
      } 
    } else {
      System.err.println("Unable to connect beans (BeanConnection)");
    } 
  }
  
  public void remove() {
    EventSetDescriptor eventSetDescriptor = getSourceEventSetDescriptor();
    try {
      Method method = eventSetDescriptor.getRemoveListenerMethod();
      Object object = getTarget().getBean();
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = object;
      method.invoke(getSource().getBean(), arrayOfObject);
      System.err.println("Deregistering listener");
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    if (getTarget().getBean() instanceof BeanCommon)
      ((BeanCommon)getTarget().getBean()).disconnectionNotification(eventSetDescriptor.getName(), getSource().getBean()); 
    CONNECTIONS.remove(this);
  }
  
  protected BeanInstance getSource() {
    return this.m_source;
  }
  
  protected BeanInstance getTarget() {
    return this.m_target;
  }
  
  protected EventSetDescriptor getSourceEventSetDescriptor() {
    JComponent jComponent = (JComponent)this.m_source.getBean();
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(jComponent.getClass());
      if (beanInfo == null) {
        System.err.println("Error");
      } else {
        EventSetDescriptor[] arrayOfEventSetDescriptor = beanInfo.getEventSetDescriptors();
        for (byte b = 0; b < arrayOfEventSetDescriptor.length; b++) {
          if (arrayOfEventSetDescriptor[b].getName().compareTo(this.m_eventName) == 0)
            return arrayOfEventSetDescriptor[b]; 
        } 
      } 
    } catch (Exception exception) {
      System.err.println("Problem retrieving event set descriptor (BeanConnection)");
    } 
    return null;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\BeanConnection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */