package org.jgap;

import java.util.List;

public interface IGeneticOperatorConstraint {
  public static final String CVS_REVISION = "$Revision: 1.2 $";
  
  boolean isValid(Population paramPopulation, List paramList, GeneticOperator paramGeneticOperator);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\IGeneticOperatorConstraint.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */