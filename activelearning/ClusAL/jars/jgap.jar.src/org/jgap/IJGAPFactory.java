package org.jgap;

import java.io.Serializable;
import java.util.Collection;

public interface IJGAPFactory extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.8 $";
  
  void setParameters(Collection paramCollection);
  
  RandomGenerator createRandomGenerator();
  
  ICloneHandler getCloneHandlerFor(Object paramObject, Class paramClass);
  
  int registerCloneHandler(ICloneHandler paramICloneHandler);
  
  IInitializer getInitializerFor(Object paramObject, Class paramClass);
  
  int registerInitializer(IInitializer paramIInitializer);
  
  void setGeneticOperatorConstraint(IGeneticOperatorConstraint paramIGeneticOperatorConstraint);
  
  IGeneticOperatorConstraint getGeneticOperatorConstraint();
  
  ICompareToHandler getCompareToHandlerFor(Object paramObject, Class paramClass);
  
  int registerCompareToHandler(ICompareToHandler paramICompareToHandler);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\IJGAPFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */