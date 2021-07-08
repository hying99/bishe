package org.jgap.distr.grid;

import java.io.Serializable;
import org.jgap.Genotype;
import org.jgap.Population;

public interface IGenotypeInitializer extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.4 $";
  
  Genotype setupGenotype(JGAPRequest paramJGAPRequest, Population paramPopulation) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\IGenotypeInitializer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */