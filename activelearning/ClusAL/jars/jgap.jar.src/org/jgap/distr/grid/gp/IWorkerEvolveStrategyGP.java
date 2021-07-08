package org.jgap.distr.grid.gp;

import java.io.Serializable;
import org.jgap.gp.impl.GPGenotype;

public interface IWorkerEvolveStrategyGP extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.2 $";
  
  void evolve(GPGenotype paramGPGenotype);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\IWorkerEvolveStrategyGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */