package org.apache.commons.math.analysis;

import org.apache.commons.math.MathException;

public interface UnivariateRealInterpolator {
  UnivariateRealFunction interpolate(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) throws MathException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\UnivariateRealInterpolator.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */