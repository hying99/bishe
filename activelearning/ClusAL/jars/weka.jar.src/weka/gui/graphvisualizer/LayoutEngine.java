package weka.gui.graphvisualizer;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import weka.core.FastVector;

public interface LayoutEngine {
  void layoutGraph();
  
  void setNodesEdges(FastVector paramFastVector1, FastVector paramFastVector2);
  
  void setNodeSize(int paramInt1, int paramInt2);
  
  JPanel getControlPanel();
  
  JProgressBar getProgressBar();
  
  void addLayoutCompleteEventListener(LayoutCompleteEventListener paramLayoutCompleteEventListener);
  
  void removeLayoutCompleteEventListener(LayoutCompleteEventListener paramLayoutCompleteEventListener);
  
  void fireLayoutCompleteEvent(LayoutCompleteEvent paramLayoutCompleteEvent);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\graphvisualizer\LayoutEngine.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */