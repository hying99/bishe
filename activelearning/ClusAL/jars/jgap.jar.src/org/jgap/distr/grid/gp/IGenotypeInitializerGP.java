package org.jgap.distr.grid.gp;

import java.io.Serializable;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.GPPopulation;

public interface IGenotypeInitializerGP extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.2 $";
  
  GPGenotype setupGenotype(JGAPRequestGP paramJGAPRequestGP, GPPopulation paramGPPopulation) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\IGenotypeInitializerGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */