package org.jgap;

import java.io.Serializable;

public interface FitnessEvaluator extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.9 $";
  
  boolean isFitter(double paramDouble1, double paramDouble2);
  
  boolean isFitter(IChromosome paramIChromosome1, IChromosome paramIChromosome2);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\FitnessEvaluator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */