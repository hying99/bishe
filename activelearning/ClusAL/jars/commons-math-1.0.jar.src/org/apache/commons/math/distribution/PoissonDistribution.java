package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

public interface PoissonDistribution extends IntegerDistribution {
  double getMean();
  
  void setMean(double paramDouble);
  
  double normalApproximateProbability(int paramInt) throws MathException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\PoissonDistribution.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */