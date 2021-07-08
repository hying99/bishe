package org.jgap.distr.grid.gp;

public class NullClientFeedbackGP implements IClientFeedbackGP {
  private static final String CVS_REVISION = "$Revision: 1.2 $";
  
  public void setProgressMinimum(int min) {}
  
  public void setProgressMaximum(int max) {}
  
  public void setProgressValue(int val) {}
  
  public void beginWork() {}
  
  public void sendingFragmentRequest(JGAPRequestGP req) {}
  
  public void receivedFragmentResult(JGAPRequestGP req, JGAPResultGP res, int idx) {}
  
  public void endWork() {}
  
  public void completeFrame(int idx) {}
  
  public void error(String msg, Exception ex) {}
  
  public void info(String msg) {}
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\NullClientFeedbackGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */