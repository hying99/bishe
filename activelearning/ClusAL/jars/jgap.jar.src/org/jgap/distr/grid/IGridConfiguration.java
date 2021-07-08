package org.jgap.distr.grid;

import java.io.Serializable;
import org.homedns.dade.jcgrid.client.GridNodeClientConfig;
import org.jgap.Configuration;

public interface IGridConfiguration extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.3 $";
  
  IClientFeedback getClientFeedback();
  
  IClientEvolveStrategy getClientEvolveStrategy();
  
  IRequestSplitStrategy getRequestSplitStrategy();
  
  Configuration getConfiguration();
  
  IWorkerEvolveStrategy getWorkerEvolveStrategy();
  
  void setWorkerEvolveStrategy(IWorkerEvolveStrategy paramIWorkerEvolveStrategy);
  
  void setClientEvolveStrategy(IClientEvolveStrategy paramIClientEvolveStrategy);
  
  void setClientFeedback(IClientFeedback paramIClientFeedback);
  
  void setRequestSplitStrategy(IRequestSplitStrategy paramIRequestSplitStrategy);
  
  IWorkerReturnStrategy getWorkerReturnStrategy();
  
  void setWorkerReturnStrategy(IWorkerReturnStrategy paramIWorkerReturnStrategy);
  
  IGenotypeInitializer getGenotypeInitializer();
  
  void setGenotypeInitializer(IGenotypeInitializer paramIGenotypeInitializer);
  
  void initialize(GridNodeClientConfig paramGridNodeClientConfig) throws Exception;
  
  void validate() throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\IGridConfiguration.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */