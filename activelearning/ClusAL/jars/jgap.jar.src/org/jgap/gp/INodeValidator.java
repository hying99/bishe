package org.jgap.gp;

import java.io.Serializable;
import org.jgap.gp.impl.ProgramChromosome;

public interface INodeValidator extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.6 $";
  
  boolean validate(ProgramChromosome paramProgramChromosome, CommandGene paramCommandGene1, CommandGene paramCommandGene2, int paramInt1, int paramInt2, int paramInt3, Class paramClass, CommandGene[] paramArrayOfCommandGene, int paramInt4, boolean paramBoolean1, int paramInt5, boolean paramBoolean2);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\INodeValidator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */