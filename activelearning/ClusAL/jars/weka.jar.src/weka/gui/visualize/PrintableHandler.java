package weka.gui.visualize;

import java.util.Hashtable;

public interface PrintableHandler {
  Hashtable getWriters();
  
  JComponentWriter getWriter(String paramString);
  
  void setSaveDialogTitle(String paramString);
  
  String getSaveDialogTitle();
  
  void setScale(double paramDouble1, double paramDouble2);
  
  double getXScale();
  
  double getYScale();
  
  void saveComponent();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\PrintableHandler.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */