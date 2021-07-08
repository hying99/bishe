package org.jgap.supergenes;

import org.jgap.Gene;

public interface SupergeneValidator {
  public static final String CVS_REVISION = "$Revision: 1.3 $";
  
  boolean isValid(Gene[] paramArrayOfGene, Supergene paramSupergene);
  
  String getPersistent();
  
  void setFromPersistent(String paramString);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\supergenes\SupergeneValidator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */