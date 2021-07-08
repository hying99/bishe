package org.apache.commons.math.stat.inference;

import org.apache.commons.math.MathException;

public interface ChiSquareTest {
  double chiSquare(double[] paramArrayOfdouble, long[] paramArrayOflong) throws IllegalArgumentException;
  
  double chiSquareTest(double[] paramArrayOfdouble, long[] paramArrayOflong) throws IllegalArgumentException, MathException;
  
  boolean chiSquareTest(double[] paramArrayOfdouble, long[] paramArrayOflong, double paramDouble) throws IllegalArgumentException, MathException;
  
  double chiSquare(long[][] paramArrayOflong) throws IllegalArgumentException;
  
  double chiSquareTest(long[][] paramArrayOflong) throws IllegalArgumentException, MathException;
  
  boolean chiSquareTest(long[][] paramArrayOflong, double paramDouble) throws IllegalArgumentException, MathException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\inference\ChiSquareTest.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */