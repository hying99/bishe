package weka.gui.treevisualizer;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.util.Vector;

public class Edge {
  private String m_label;
  
  private String m_rsource;
  
  private String m_rtarget;
  
  private Node m_source;
  
  private Node m_target;
  
  private Vector m_lines;
  
  public Edge(String paramString1, String paramString2, String paramString3) {
    this.m_label = paramString1;
    this.m_rsource = paramString2;
    this.m_rtarget = paramString3;
    this.m_lines = new Vector(3, 2);
    breakupLabel();
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
  
  public String getRsource() {
    return this.m_rsource;
  }
  
  public void setRsource(String paramString) {
    this.m_rsource = paramString;
  }
  
  public String getRtarget() {
    return this.m_rtarget;
  }
  
  public void setRtarget(String paramString) {
    this.m_rtarget = paramString;
  }
  
  public Node getSource() {
    return this.m_source;
  }
  
  public void setSource(Node paramNode) {
    this.m_source = paramNode;
    paramNode.addChild(this);
  }
  
  public Node getTarget() {
    return this.m_target;
  }
  
  public void setTarget(Node paramNode) {
    this.m_target = paramNode;
    paramNode.setParent(this);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\treevisualizer\Edge.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */