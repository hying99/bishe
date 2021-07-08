package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BeanVisual extends JPanel implements Serializable {
  public static final String ICON_PATH = "weka/gui/beans/icons/";
  
  public static final int NORTH_CONNECTOR = 0;
  
  public static final int SOUTH_CONNECTOR = 1;
  
  public static final int EAST_CONNECTOR = 2;
  
  public static final int WEST_CONNECTOR = 3;
  
  protected String m_iconPath;
  
  protected String m_animatedIconPath;
  
  protected transient ImageIcon m_icon;
  
  protected transient ImageIcon m_animatedIcon;
  
  protected String m_visualName;
  
  protected JLabel m_visualLabel;
  
  private boolean m_stationary = true;
  
  private PropertyChangeSupport m_pcs = new PropertyChangeSupport(this);
  
  private boolean m_displayConnectors = false;
  
  public BeanVisual(String paramString1, String paramString2, String paramString3) {
    loadIcons(paramString2, paramString3);
    this.m_visualName = paramString1;
    this.m_visualLabel = new JLabel(this.m_icon);
    setLayout(new BorderLayout());
    add(this.m_visualLabel, "Center");
    Dimension dimension1 = this.m_visualLabel.getPreferredSize();
    Dimension dimension2 = new Dimension((int)dimension1.getWidth() + 10, (int)dimension1.getHeight() + 10);
    setMinimumSize(dimension2);
    setPreferredSize(dimension2);
    setMaximumSize(dimension2);
  }
  
  public void scale(int paramInt) {
    if (this.m_icon != null) {
      removeAll();
      Image image = this.m_icon.getImage();
      int i = this.m_icon.getIconWidth();
      int j = this.m_icon.getIconHeight();
      int k = i / paramInt;
      i -= k;
      j -= k;
      image = image.getScaledInstance(i, j, 4);
      this.m_icon = new ImageIcon(image);
      this.m_visualLabel = new JLabel(this.m_icon);
      add(this.m_visualLabel, "Center");
      Dimension dimension1 = this.m_visualLabel.getPreferredSize();
      Dimension dimension2 = new Dimension((int)dimension1.getWidth() + 10, (int)dimension1.getHeight() + 10);
      setMinimumSize(dimension2);
      setPreferredSize(dimension2);
      setMaximumSize(dimension2);
    } 
  }
  
  public boolean loadIcons(String paramString1, String paramString2) {
    boolean bool = true;
    URL uRL = ClassLoader.getSystemResource(paramString1);
    if (uRL != null) {
      Image image = Toolkit.getDefaultToolkit().getImage(uRL);
      this.m_icon = new ImageIcon(image);
      if (this.m_visualLabel != null)
        this.m_visualLabel.setIcon(this.m_icon); 
    } 
    uRL = ClassLoader.getSystemResource(paramString2);
    if (uRL == null) {
      bool = false;
    } else {
      Image image = Toolkit.getDefaultToolkit().getImage(uRL);
      this.m_animatedIcon = new ImageIcon(image);
    } 
    this.m_iconPath = paramString1;
    this.m_animatedIconPath = paramString2;
    return bool;
  }
  
  public void setText(String paramString) {
    this.m_visualName = paramString;
    this.m_pcs.firePropertyChange("label", (Object)null, (Object)null);
  }
  
  public String getText() {
    return this.m_visualName;
  }
  
  public void setStatic() {
    this.m_visualLabel.setIcon(this.m_icon);
  }
  
  public void setAnimated() {
    this.m_visualLabel.setIcon(this.m_animatedIcon);
  }
  
  public Point getClosestConnectorPoint(Point paramPoint) {
    int i = getParent().getX();
    int j = getParent().getY();
    int k = getWidth();
    int m = getHeight();
    int n = i + k / 2;
    int i1 = j + m / 2;
    int i2 = (int)paramPoint.getX();
    int i3 = (int)paramPoint.getY();
    Point point = new Point();
    int i4 = (Math.abs(i2 - n) < Math.abs(i3 - i1)) ? n : ((i2 < n) ? i : (i + k));
    int i5 = (Math.abs(i3 - i1) < Math.abs(i2 - n)) ? i1 : ((i3 < i1) ? j : (j + m));
    point.setLocation(i4, i5);
    return point;
  }
  
  public Point getConnectorPoint(int paramInt) {
    int i = getParent().getX();
    int j = getParent().getY();
    int k = getWidth();
    int m = getHeight();
    int n = i + k / 2;
    int i1 = j + m / 2;
    switch (paramInt) {
      case 0:
        return new Point(n, j);
      case 1:
        return new Point(n, j + m);
      case 3:
        return new Point(i, i1);
      case 2:
        return new Point(i + k, i1);
    } 
    System.err.println("Unrecognised connectorPoint (BeanVisual)");
    return new Point(i, j);
  }
  
  public ImageIcon getStaticIcon() {
    return this.m_icon;
  }
  
  public ImageIcon getAnimatedIcon() {
    return this.m_animatedIcon;
  }
  
  public void setDisplayConnectors(boolean paramBoolean) {
    this.m_displayConnectors = paramBoolean;
    repaint();
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcs.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcs.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void paintComponent(Graphics paramGraphics) {
    super.paintComponent(paramGraphics);
    if (this.m_displayConnectors) {
      paramGraphics.setColor(Color.blue);
      int i = (int)(getWidth() / 2.0D);
      int j = (int)(getHeight() / 2.0D);
      paramGraphics.fillOval(i - 2, 0, 5, 5);
      paramGraphics.fillOval(i - 2, getHeight() - 5, 5, 5);
      paramGraphics.fillOval(0, j - 2, 5, 5);
      paramGraphics.fillOval(getWidth() - 5, j - 2, 5, 5);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    try {
      paramObjectInputStream.defaultReadObject();
      loadIcons(this.m_iconPath, this.m_animatedIconPath);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\BeanVisual.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */