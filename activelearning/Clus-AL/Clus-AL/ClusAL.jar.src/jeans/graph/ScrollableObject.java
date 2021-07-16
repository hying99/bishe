package jeans.graph;

import java.awt.Dimension;
import java.awt.Point;

public interface ScrollableObject {
  Dimension getTotalSize();
  
  Dimension getVisibleSize();
  
  Point getOrigin();
  
  void setOrigin(int paramInt1, int paramInt2, boolean paramBoolean);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\ScrollableObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */