package org.jgap.gp;

import java.io.Serializable;

public interface IGPFitnessEvaluator extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.6 $";
  
  boolean isFitter(double paramDouble1, double paramDouble2);
  
  boolean isFitter(IGPProgram paramIGPProgram1, IGPProgram paramIGPProgram2);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\IGPFitnessEvaluator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */