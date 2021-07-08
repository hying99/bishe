package org.jgap.gp;

import java.io.Serializable;
import org.jgap.gp.impl.GPGenotype;

public interface INaturalGPSelector extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.6 $";
  
  IGPProgram select(GPGenotype paramGPGenotype);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\INaturalGPSelector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */