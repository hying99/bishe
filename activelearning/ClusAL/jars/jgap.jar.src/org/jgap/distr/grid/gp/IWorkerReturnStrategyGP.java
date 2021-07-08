package org.jgap.distr.grid.gp;

import java.io.Serializable;
import org.jgap.gp.impl.GPGenotype;

public interface IWorkerReturnStrategyGP extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.2 $";
  
  JGAPResultGP assembleResult(JGAPRequestGP paramJGAPRequestGP, GPGenotype paramGPGenotype) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\IWorkerReturnStrategyGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */