package weka.gui.beans;

import weka.gui.Logger;

public interface BeanCommon {
  void stop();
  
  void setLog(Logger paramLogger);
  
  boolean connectionAllowed(String paramString);
  
  void connectionNotification(String paramString, Object paramObject);
  
  void disconnectionNotification(String paramString, Object paramObject);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\BeanCommon.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */