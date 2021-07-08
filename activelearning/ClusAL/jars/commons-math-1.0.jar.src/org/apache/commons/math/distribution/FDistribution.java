package org.apache.commons.math.distribution;

public interface FDistribution extends ContinuousDistribution {
  void setNumeratorDegreesOfFreedom(double paramDouble);
  
  double getNumeratorDegreesOfFreedom();
  
  void setDenominatorDegreesOfFreedom(double paramDouble);
  
  double getDenominatorDegreesOfFreedom();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\FDistribution.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */