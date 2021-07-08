package org.jgap;

import java.io.Serializable;
import org.jgap.util.ICloneable;

public interface IBreeder extends ICloneable, Serializable, Comparable {
  public static final String CVS_REVISION = "$Revision: 1.4 $";
  
  Population evolve(Population paramPopulation, Configuration paramConfiguration);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\IBreeder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */