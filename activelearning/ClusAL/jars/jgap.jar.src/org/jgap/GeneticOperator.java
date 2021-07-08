package org.jgap;

import java.io.Serializable;
import java.util.List;

public interface GeneticOperator extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.16 $";
  
  void operate(Population paramPopulation, List paramList);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\GeneticOperator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */