package org.jgap.distr;

import org.jgap.Population;

public interface IPopulationMerger {
  public static final String CVS_REVISION = "$Revision: 1.5 $";
  
  Population mergePopulations(Population paramPopulation1, Population paramPopulation2, int paramInt);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\IPopulationMerger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */