package org.jgap;

import java.io.Serializable;

public interface INaturalSelector extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.13 $";
  
  void select(int paramInt, Population paramPopulation1, Population paramPopulation2);
  
  void empty();
  
  boolean returnsUniqueChromosomes();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\INaturalSelector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */