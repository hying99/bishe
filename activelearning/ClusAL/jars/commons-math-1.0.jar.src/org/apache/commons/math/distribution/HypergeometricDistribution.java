package org.apache.commons.math.distribution;

public interface HypergeometricDistribution extends IntegerDistribution {
  int getNumberOfSuccesses();
  
  int getPopulationSize();
  
  int getSampleSize();
  
  void setNumberOfSuccesses(int paramInt);
  
  void setPopulationSize(int paramInt);
  
  void setSampleSize(int paramInt);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\HypergeometricDistribution.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */