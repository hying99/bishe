package jeans.graph.drawable;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public interface DrawableProvider {
  Graphics getDGraphics();
  
  Canvas getDCanvas();
  
  FontMetrics getDMetrics(Font paramFont);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\drawable\DrawableProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */