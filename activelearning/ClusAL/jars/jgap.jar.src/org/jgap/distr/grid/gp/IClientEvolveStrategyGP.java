package org.jgap.distr.grid.gp;

import java.io.Serializable;
import org.homedns.dade.jcgrid.client.GridClient;
import org.jgap.gp.impl.GPConfiguration;

public interface IClientEvolveStrategyGP extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.2 $";
  
  void initialize(GridClient paramGridClient, GPConfiguration paramGPConfiguration, IClientFeedbackGP paramIClientFeedbackGP) throws Exception;
  
  JGAPRequestGP[] generateWorkRequests(JGAPRequestGP paramJGAPRequestGP, IRequestSplitStrategyGP paramIRequestSplitStrategyGP, Object paramObject) throws Exception;
  
  void resultReceived(JGAPResultGP paramJGAPResultGP) throws Exception;
  
  boolean isEvolutionFinished(int paramInt);
  
  void onFinished();
  
  void afterWorkRequestsSent() throws Exception;
  
  void evolve() throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\IClientEvolveStrategyGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */