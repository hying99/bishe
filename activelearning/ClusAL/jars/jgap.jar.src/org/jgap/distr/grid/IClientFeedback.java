package org.jgap.distr.grid;

import java.io.Serializable;

public interface IClientFeedback extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.6 $";
  
  void setProgressMinimum(int paramInt);
  
  void setProgressMaximum(int paramInt);
  
  void setProgressValue(int paramInt);
  
  void beginWork();
  
  void sendingFragmentRequest(JGAPRequest paramJGAPRequest);
  
  void receivedFragmentResult(JGAPRequest paramJGAPRequest, JGAPResult paramJGAPResult, int paramInt);
  
  void endWork();
  
  void completeFrame(int paramInt);
  
  void error(String paramString, Exception paramException);
  
  void info(String paramString);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\IClientFeedback.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */