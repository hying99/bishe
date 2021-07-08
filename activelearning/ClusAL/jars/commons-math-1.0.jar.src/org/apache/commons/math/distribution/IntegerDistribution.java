package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

public interface IntegerDistribution extends DiscreteDistribution {
  double probability(int paramInt);
  
  double cumulativeProbability(int paramInt) throws MathException;
  
  double cumulativeProbability(int paramInt1, int paramInt2) throws MathException;
  
  int inverseCumulativeProbability(double paramDouble) throws MathException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\IntegerDistribution.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */