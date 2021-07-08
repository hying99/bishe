package org.jgap.distr.grid.gp;

import java.io.Serializable;

public interface IRequestSplitStrategyGP extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.2 $";
  
  JGAPRequestGP[] split(JGAPRequestGP paramJGAPRequestGP) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\IRequestSplitStrategyGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */