package org.jgap;

public interface ICompositeGene extends Gene {
  public static final String CVS_REVISION = "$Revision: 1.4 $";
  
  void addGene(Gene paramGene);
  
  Gene geneAt(int paramInt);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\ICompositeGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */