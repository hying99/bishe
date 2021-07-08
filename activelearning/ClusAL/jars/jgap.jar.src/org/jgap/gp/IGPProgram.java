package org.jgap.gp;

import java.io.Serializable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.ICloneable;

public interface IGPProgram extends Serializable, Comparable, ICloneable {
  public static final String CVS_REVISION = "$Revision: 1.13 $";
  
  int execute_int(int paramInt, Object[] paramArrayOfObject);
  
  float execute_float(int paramInt, Object[] paramArrayOfObject);
  
  double execute_double(int paramInt, Object[] paramArrayOfObject);
  
  boolean execute_boolean(int paramInt, Object[] paramArrayOfObject);
  
  Object execute_object(int paramInt, Object[] paramArrayOfObject);
  
  void execute_void(int paramInt, Object[] paramArrayOfObject);
  
  int size();
  
  ProgramChromosome getChromosome(int paramInt);
  
  double getFitnessValue();
  
  String toStringNorm(int paramInt);
  
  void setChromosome(int paramInt, ProgramChromosome paramProgramChromosome);
  
  int getCommandOfClass(int paramInt, Class paramClass);
  
  void setFitnessValue(double paramDouble);
  
  void setTypes(Class[] paramArrayOfClass);
  
  Class[] getTypes();
  
  void setArgTypes(Class[][] paramArrayOfClass);
  
  Class[][] getArgTypes();
  
  void setNodeSets(CommandGene[][] paramArrayOfCommandGene);
  
  CommandGene[][] getNodeSets();
  
  void setMaxDepths(int[] paramArrayOfint);
  
  int[] getMaxDepths();
  
  void setMinDepths(int[] paramArrayOfint);
  
  int[] getMinDepths();
  
  void setMaxNodes(int paramInt);
  
  int getMaxNodes();
  
  GPConfiguration getGPConfiguration();
  
  void setApplicationData(Object paramObject);
  
  Object getApplicationData();
  
  String getPersistentRepresentation();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\IGPProgram.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */