package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

public interface Distribution {
  double cumulativeProbability(double paramDouble) throws MathException;
  
  double cumulativeProbability(double paramDouble1, double paramDouble2) throws MathException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\Distribution.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */