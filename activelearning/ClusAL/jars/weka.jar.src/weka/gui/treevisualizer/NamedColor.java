package weka.gui.treevisualizer;

import java.awt.Color;

public class NamedColor {
  public String m_name;
  
  public Color m_col;
  
  public NamedColor(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    this.m_name = paramString;
    this.m_col = new Color(paramInt1, paramInt2, paramInt3);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\treevisualizer\NamedColor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */