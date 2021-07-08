package org.jgap.gp;

import org.jgap.InvalidConfigurationException;

public interface IMutateable {
  public static final String CVS_REVISION = "$Revision: 1.4 $";
  
  CommandGene applyMutation(int paramInt, double paramDouble) throws InvalidConfigurationException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\IMutateable.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */