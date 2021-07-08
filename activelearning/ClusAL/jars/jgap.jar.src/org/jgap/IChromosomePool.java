package org.jgap;

public interface IChromosomePool {
  public static final String CVS_REVISION = "$Revision: 1.3 $";
  
  IChromosome acquireChromosome();
  
  void releaseChromosome(IChromosome paramIChromosome);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\IChromosomePool.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */