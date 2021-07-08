package weka.experiment;

import java.io.Serializable;

public class RemoteExperimentEvent implements Serializable {
  public boolean m_statusMessage;
  
  public boolean m_logMessage;
  
  public String m_messageString;
  
  public boolean m_experimentFinished;
  
  public RemoteExperimentEvent(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString) {
    this.m_statusMessage = paramBoolean1;
    this.m_logMessage = paramBoolean2;
    this.m_experimentFinished = paramBoolean3;
    this.m_messageString = paramString;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\RemoteExperimentEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */