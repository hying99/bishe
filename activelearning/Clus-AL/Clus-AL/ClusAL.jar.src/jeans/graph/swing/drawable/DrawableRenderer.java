package jeans.graph.swing.drawable;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public interface DrawableRenderer {
  void render(Graphics2D paramGraphics2D, FontMetrics paramFontMetrics, DrawableCanvas paramDrawableCanvas);
  
  Dimension getSize();
  
  void removeAll(DrawableCanvas paramDrawableCanvas);
  
  void addAll(DrawableCanvas paramDrawableCanvas);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\drawable\DrawableRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */