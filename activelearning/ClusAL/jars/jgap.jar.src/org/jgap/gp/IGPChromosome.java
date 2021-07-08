package org.jgap.gp;

import java.io.Serializable;
import org.jgap.gp.impl.GPConfiguration;

public interface IGPChromosome extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.7 $";
  
  IGPProgram getIndividual();
  
  void setIndividual(IGPProgram paramIGPProgram);
  
  void cleanup();
  
  String toStringNorm(int paramInt);
  
  void redepth();
  
  int numTerminals();
  
  int numFunctions();
  
  int numTerminals(Class paramClass, int paramInt);
  
  int numFunctions(Class paramClass, int paramInt);
  
  CommandGene getNode(int paramInt);
  
  int getChild(int paramInt1, int paramInt2);
  
  int getTerminal(int paramInt);
  
  int getFunction(int paramInt);
  
  int getTerminal(int paramInt1, Class paramClass, int paramInt2);
  
  int getFunction(int paramInt1, Class paramClass, int paramInt2);
  
  CommandGene[] getFunctions();
  
  CommandGene[] getFunctionSet();
  
  GPConfiguration getGPConfiguration();
  
  String getPersistentRepresentation();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\IGPChromosome.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */