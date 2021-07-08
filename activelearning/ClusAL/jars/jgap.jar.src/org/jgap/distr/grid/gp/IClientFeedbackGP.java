package org.jgap.distr.grid.gp;

import java.io.Serializable;

public interface IClientFeedbackGP extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.2 $";
  
  void setProgressMinimum(int paramInt);
  
  void setProgressMaximum(int paramInt);
  
  void setProgressValue(int paramInt);
  
  void beginWork();
  
  void sendingFragmentRequest(JGAPRequestGP paramJGAPRequestGP);
  
  void receivedFragmentResult(JGAPRequestGP paramJGAPRequestGP, JGAPResultGP paramJGAPResultGP, int paramInt);
  
  void endWork();
  
  void completeFrame(int paramInt);
  
  void error(String paramString, Exception paramException);
  
  void info(String paramString);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\IClientFeedbackGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */