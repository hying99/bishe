package weka.gui;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SysErrLog implements Logger {
  protected static String getTimestamp() {
    return (new SimpleDateFormat("yyyy.MM.dd hh:mm:ss")).format(new Date());
  }
  
  public void logMessage(String paramString) {
    System.err.println("LOG " + getTimestamp() + ": " + paramString);
  }
  
  public void statusMessage(String paramString) {
    System.err.println("STATUS: " + paramString);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\SysErrLog.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */