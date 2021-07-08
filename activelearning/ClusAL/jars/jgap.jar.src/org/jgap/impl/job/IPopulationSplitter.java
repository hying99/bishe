package org.jgap.impl.job;

import org.jgap.Population;

public interface IPopulationSplitter {
  public static final String CVS_REVISION = "$Revision: 1.5 $";
  
  Population[] split(Population paramPopulation) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\job\IPopulationSplitter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */