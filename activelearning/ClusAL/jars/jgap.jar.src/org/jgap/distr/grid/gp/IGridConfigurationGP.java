package org.jgap.distr.grid.gp;

import java.io.Serializable;
import org.homedns.dade.jcgrid.client.GridNodeClientConfig;
import org.jgap.gp.CommandGene;
import org.jgap.gp.impl.GPConfiguration;

public interface IGridConfigurationGP extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.4 $";
  
  IClientFeedbackGP getClientFeedback();
  
  IClientEvolveStrategyGP getClientEvolveStrategy();
  
  IRequestSplitStrategyGP getRequestSplitStrategy();
  
  GPConfiguration getConfiguration();
  
  void setConfiguration(GPConfiguration paramGPConfiguration);
  
  IWorkerEvolveStrategyGP getWorkerEvolveStrategy();
  
  void setWorkerEvolveStrategy(IWorkerEvolveStrategyGP paramIWorkerEvolveStrategyGP);
  
  void setClientEvolveStrategy(IClientEvolveStrategyGP paramIClientEvolveStrategyGP);
  
  void setClientFeedback(IClientFeedbackGP paramIClientFeedbackGP);
  
  void setRequestSplitStrategy(IRequestSplitStrategyGP paramIRequestSplitStrategyGP);
  
  IWorkerReturnStrategyGP getWorkerReturnStrategy();
  
  void setWorkerReturnStrategy(IWorkerReturnStrategyGP paramIWorkerReturnStrategyGP);
  
  IGenotypeInitializerGP getGenotypeInitializer();
  
  void setGenotypeInitializer(IGenotypeInitializerGP paramIGenotypeInitializerGP);
  
  void initialize(GridNodeClientConfig paramGridNodeClientConfig) throws Exception;
  
  void validate() throws Exception;
  
  void setTypes(Class[] paramArrayOfClass);
  
  void setArgTypes(Class[][] paramArrayOfClass);
  
  void setNodeSets(CommandGene[][] paramArrayOfCommandGene);
  
  void setMinDepths(int[] paramArrayOfint);
  
  void setMaxDepths(int[] paramArrayOfint);
  
  void setMaxNodes(int paramInt);
  
  Class[] getTypes();
  
  Class[][] getArgTypes();
  
  CommandGene[][] getNodeSets();
  
  int[] getMinDepths();
  
  int[] getMaxDepths();
  
  int getMaxNodes();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\IGridConfigurationGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */