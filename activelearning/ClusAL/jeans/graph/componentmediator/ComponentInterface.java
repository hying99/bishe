package jeans.graph.componentmediator;

import java.awt.event.ActionListener;

public interface ComponentInterface {
  void setEnabled(ComponentWrapper paramComponentWrapper, boolean paramBoolean);
  
  void setCheck(ComponentWrapper paramComponentWrapper, boolean paramBoolean);
  
  boolean isCheck(ComponentWrapper paramComponentWrapper);
  
  void addActionListener(ComponentWrapper paramComponentWrapper, ActionListener paramActionListener);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\componentmediator\ComponentInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */