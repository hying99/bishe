package org.jgap;

import java.io.Serializable;
import org.jgap.util.ICloneable;

public interface IChromosome extends Comparable, ICloneable, Serializable {
  public static final String CVS_REVISION = "$Revision: 1.13 $";
  
  public static final String S_FITNESS_VALUE = "Fitness value";
  
  public static final String S_ALLELES = "Alleles";
  
  public static final String S_APPLICATION_DATA = "Application data";
  
  public static final String S_SIZE = "Size";
  
  Gene getGene(int paramInt);
  
  Gene[] getGenes();
  
  void setGenes(Gene[] paramArrayOfGene) throws InvalidConfigurationException;
  
  int size();
  
  void setFitnessValue(double paramDouble);
  
  void setFitnessValueDirectly(double paramDouble);
  
  double getFitnessValue();
  
  double getFitnessValueDirectly();
  
  void setIsSelectedForNextGeneration(boolean paramBoolean);
  
  boolean isSelectedForNextGeneration();
  
  void setConstraintChecker(IGeneConstraintChecker paramIGeneConstraintChecker) throws InvalidConfigurationException;
  
  void setApplicationData(Object paramObject);
  
  Object getApplicationData();
  
  void cleanup();
  
  Configuration getConfiguration();
  
  void increaseAge();
  
  void resetAge();
  
  int getAge();
  
  void increaseOperatedOn();
  
  void resetOperatedOn();
  
  int operatedOn();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\IChromosome.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */