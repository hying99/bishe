package org.jgap.gp;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.impl.GPConfiguration;

public interface IProgramCreator {
  public static final String CVS_REVISION = "$Revision: 1.2 $";
  
  IGPProgram create(GPConfiguration paramGPConfiguration, int paramInt1, Class[] paramArrayOfClass, Class[][] paramArrayOfClass1, CommandGene[][] paramArrayOfCommandGene, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt2, int paramInt3, boolean paramBoolean, int paramInt4, boolean[] paramArrayOfboolean) throws InvalidConfigurationException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\IProgramCreator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */