package weka.gui.treevisualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.io.StringReader;
import java.util.Vector;
import weka.core.Instances;

public class Node {
  private int m_backstyle;
  
  private int m_shape;
  
  private Color m_color;
  
  private String m_label;
  
  private Vector m_lines;
  
  private double m_center;
  
  private double m_top;
  
  private boolean m_cVisible;
  
  private boolean m_visible;
  
  private boolean m_root;
  
  private Vector m_parent;
  
  private Vector m_children;
  
  private String m_refer;
  
  private String m_data;
  
  private Instances m_theData;
  
  public Node(String paramString1, String paramString2, int paramInt1, int paramInt2, Color paramColor, String paramString3) {
    this.m_label = paramString1;
    this.m_backstyle = paramInt1;
    this.m_shape = paramInt2;
    this.m_color = paramColor;
    this.m_refer = paramString2;
    this.m_center = 0.0D;
    this.m_top = 0.0D;
    this.m_cVisible = true;
    this.m_visible = true;
    this.m_root = false;
    this.m_parent = new Vector(1, 1);
    this.m_children = new Vector(20, 10);
    this.m_lines = new Vector(4, 2);
    breakupLabel();
    this.m_data = paramString3;
    this.m_theData = null;
  }
  
  public Instances getInstances() {
    if (this.m_theData == null && this.m_data != null) {
      try {
        this.m_theData = new Instances(new StringReader(this.m_data));
      } catch (Exception exception) {
        System.out.println("Error : " + exception);
      } 
      this.m_data = null;
    } 
    return this.m_theData;
  }
  
  public boolean getCVisible() {
    return this.m_cVisible;
  }
  
  private void childVis(Node paramNode) {
    paramNode.setVisible(true);
    if (paramNode.getCVisible()) {
      Edge edge;
      for (byte b = 0; (edge = paramNode.getChild(b)) != null; b++)
        childVis(edge.getTarget()); 
    } 
  }
  
  public void setCVisible(boolean paramBoolean) {
    this.m_cVisible = paramBoolean;
    if (paramBoolean) {
      childVis(this);
    } else if (!paramBoolean) {
      childInv(this);
    } 
  }
  
  private void childInv(Node paramNode) {
    Edge edge;
    for (byte b = 0; (edge = paramNode.getChild(b)) != null; b++) {
      Node node = edge.getTarget();
      node.setVisible(false);
      childInv(node);
    } 
  }
  
  public String getRefer() {
    return this.m_refer;
  }
  
  public void setRefer(String paramString) {
    this.m_refer = paramString;
  }
  
  public int getShape() {
    return this.m_shape;
  }
  
  public void setShape(int paramInt) {
    this.m_shape = paramInt;
  }
  
  public Color getColor() {
    return this.m_color;
  }
  
  public void setColor(Color paramColor) {
    this.m_color = paramColor;
  }
  
  public String getLabel() {
    return this.m_label;
  }
  
  private void breakupLabel() {
    int i = 0;
    byte b;
    for (b = 0; b < this.m_label.length(); b++) {
      if (this.m_label.charAt(b) == '\n') {
        this.m_lines.addElement(this.m_label.substring(i, b));
        i = b + 1;
      } 
    } 
    this.m_lines.addElement(this.m_label.substring(i, b));
  }
  
  public Dimension stringSize(FontMetrics paramFontMetrics) {
    Dimension dimension = new Dimension();
    int i = 0;
    byte b = 0;
    String str;
    while ((str = getLine(b)) != null) {
      b++;
      i = paramFontMetrics.stringWidth(str);
      if (i > dimension.width)
        dimension.width = i; 
    } 
    dimension.height = b * paramFontMetrics.getHeight();
    return dimension;
  }
  
  public String getLine(int paramInt) {
    return (paramInt < this.m_lines.size()) ? this.m_lines.elementAt(paramInt) : null;
  }
  
  public double getCenter() {
    return this.m_center;
  }
  
  public void setCenter(double paramDouble) {
    this.m_center = paramDouble;
  }
  
  public void adjustCenter(double paramDouble) {
    this.m_center += paramDouble;
  }
  
  public double getTop() {
    return this.m_top;
  }
  
  public void setTop(double paramDouble) {
    this.m_top = paramDouble;
  }
  
  public boolean getVisible() {
    return this.m_visible;
  }
  
  private void setVisible(boolean paramBoolean) {
    this.m_visible = paramBoolean;
  }
  
  public boolean getRoot() {
    return this.m_root;
  }
  
  public void setRoot(boolean paramBoolean) {
    this.m_root = paramBoolean;
  }
  
  public Edge getParent(int paramInt) {
    return (paramInt < this.m_parent.size()) ? this.m_parent.elementAt(paramInt) : null;
  }
  
  public void setParent(Edge paramEdge) {
    this.m_parent.addElement(paramEdge);
  }
  
  public Edge getChild(int paramInt) {
    return (paramInt < this.m_children.size()) ? this.m_children.elementAt(paramInt) : null;
  }
  
  public void addChild(Edge paramEdge) {
    this.m_children.addElement(paramEdge);
  }
  
  public static int getGCount(Node paramNode, int paramInt) {
    if (paramNode.getChild(0) != null && paramNode.getCVisible()) {
      paramInt++;
      Edge edge;
      for (byte b = 0; (edge = paramNode.getChild(b)) != null; b++)
        paramInt = getGCount(edge.getTarget(), paramInt); 
    } 
    return paramInt;
  }
  
  public static int getTotalGCount(Node paramNode, int paramInt) {
    if (paramNode.getChild(0) != null) {
      paramInt++;
      Edge edge;
      for (byte b = 0; (edge = paramNode.getChild(b)) != null; b++)
        paramInt = getTotalGCount(edge.getTarget(), paramInt); 
    } 
    return paramInt;
  }
  
  public static int getCount(Node paramNode, int paramInt) {
    paramInt++;
    Edge edge;
    for (byte b = 0; (edge = paramNode.getChild(b)) != null && paramNode.getCVisible(); b++)
      paramInt = getCount(edge.getTarget(), paramInt); 
    return paramInt;
  }
  
  public static int getTotalCount(Node paramNode, int paramInt) {
    paramInt++;
    Edge edge;
    for (byte b = 0; (edge = paramNode.getChild(b)) != null; b++)
      paramInt = getTotalCount(edge.getTarget(), paramInt); 
    return paramInt;
  }
  
  public static int getHeight(Node paramNode, int paramInt) {
    int i = ++paramInt;
    int j = 0;
    Edge edge;
    for (byte b = 0; (edge = paramNode.getChild(b)) != null && paramNode.getCVisible(); b++) {
      j = getHeight(edge.getTarget(), paramInt);
      if (j > i)
        i = j; 
    } 
    return i;
  }
  
  public static int getTotalHeight(Node paramNode, int paramInt) {
    int i = ++paramInt;
    int j = 0;
    Edge edge;
    for (byte b = 0; (edge = paramNode.getChild(b)) != null; b++) {
      j = getTotalHeight(edge.getTarget(), paramInt);
      if (j > i)
        i = j; 
    } 
    return i;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\treevisualizer\Node.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */