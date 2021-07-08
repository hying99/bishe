package org.jgap.gp;

import java.io.Serializable;

public interface IGPInitStrategy extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.4 $";
  
  CommandGene init(IGPChromosome paramIGPChromosome, int paramInt) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\IGPInitStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */