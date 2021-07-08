package org.jgap;

import java.io.Serializable;

public interface IGeneConstraintChecker extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.8 $";
  
  boolean verify(Gene paramGene, Object paramObject, IChromosome paramIChromosome, int paramInt);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\IGeneConstraintChecker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */