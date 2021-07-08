package org.jgap.supergenes;

import org.jgap.Gene;
import org.jgap.ICompositeGene;

public interface Supergene extends Gene, ICompositeGene {
  public static final String CVS_REVISION = "$Revision: 1.14 $";
  
  boolean isValid();
  
  Gene[] getGenes();
  
  Gene geneAt(int paramInt);
  
  void setValidator(SupergeneValidator paramSupergeneValidator);
  
  SupergeneValidator getValidator();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\supergenes\Supergene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */