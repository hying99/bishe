package org.jgap.gp;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.impl.GPPopulation;

public interface IPopulationCreator {
  public static final String CVS_REVISION = "$Revision: 1.2 $";
  
  void initialize(GPPopulation paramGPPopulation, Class[] paramArrayOfClass, Class[][] paramArrayOfClass1, CommandGene[][] paramArrayOfCommandGene, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt, boolean[] paramArrayOfboolean) throws InvalidConfigurationException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\IPopulationCreator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */