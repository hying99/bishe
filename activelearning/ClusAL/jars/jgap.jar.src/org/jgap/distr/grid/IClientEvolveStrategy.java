package org.jgap.distr.grid;

import java.io.Serializable;
import org.homedns.dade.jcgrid.client.GridClient;
import org.jgap.Configuration;

public interface IClientEvolveStrategy extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.3 $";
  
  void initialize(GridClient paramGridClient, Configuration paramConfiguration, IClientFeedback paramIClientFeedback) throws Exception;
  
  JGAPRequest[] generateWorkRequests(JGAPRequest paramJGAPRequest, IRequestSplitStrategy paramIRequestSplitStrategy, Object paramObject) throws Exception;
  
  void resultReceived(JGAPResult paramJGAPResult) throws Exception;
  
  boolean isEvolutionFinished(int paramInt);
  
  void onFinished();
  
  void afterWorkRequestsSent() throws Exception;
  
  void evolve() throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\IClientEvolveStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */