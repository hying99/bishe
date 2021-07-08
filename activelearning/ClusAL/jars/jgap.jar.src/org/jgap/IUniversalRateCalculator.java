package org.jgap;

import java.io.Serializable;

public interface IUniversalRateCalculator extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.7 $";
  
  int calculateCurrentRate();
  
  boolean toBePermutated(IChromosome paramIChromosome, int paramInt);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\IUniversalRateCalculator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */