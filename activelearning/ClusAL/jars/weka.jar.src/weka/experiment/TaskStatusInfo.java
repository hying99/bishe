package weka.experiment;

import java.io.Serializable;

public class TaskStatusInfo implements Serializable {
  public static final int TO_BE_RUN = 0;
  
  public static final int PROCESSING = 1;
  
  public static final int FAILED = 2;
  
  public static final int FINISHED = 3;
  
  private int m_ExecutionStatus = 0;
  
  private String m_StatusMessage = "New Task";
  
  private Object m_TaskResult = null;
  
  public void setExecutionStatus(int paramInt) {
    this.m_ExecutionStatus = paramInt;
  }
  
  public int getExecutionStatus() {
    return this.m_ExecutionStatus;
  }
  
  public void setStatusMessage(String paramString) {
    this.m_StatusMessage = paramString;
  }
  
  public String getStatusMessage() {
    return this.m_StatusMessage;
  }
  
  public void setTaskResult(Object paramObject) {
    this.m_TaskResult = paramObject;
  }
  
  public Object getTaskResult() {
    return this.m_TaskResult;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\TaskStatusInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */