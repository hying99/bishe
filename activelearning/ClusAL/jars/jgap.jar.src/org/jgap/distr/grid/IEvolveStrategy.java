package org.jgap.distr.grid;

import java.io.Serializable;
import org.jgap.Genotype;

public interface IEvolveStrategy extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.4 $";
  
  void evolve(Genotype paramGenotype);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\IEvolveStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */