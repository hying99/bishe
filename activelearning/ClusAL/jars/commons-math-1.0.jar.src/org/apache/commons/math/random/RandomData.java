package org.apache.commons.math.random;

import java.util.Collection;

public interface RandomData {
  String nextHexString(int paramInt);
  
  int nextInt(int paramInt1, int paramInt2);
  
  long nextLong(long paramLong1, long paramLong2);
  
  String nextSecureHexString(int paramInt);
  
  int nextSecureInt(int paramInt1, int paramInt2);
  
  long nextSecureLong(long paramLong1, long paramLong2);
  
  long nextPoisson(double paramDouble);
  
  double nextGaussian(double paramDouble1, double paramDouble2);
  
  double nextExponential(double paramDouble);
  
  double nextUniform(double paramDouble1, double paramDouble2);
  
  int[] nextPermutation(int paramInt1, int paramInt2);
  
  Object[] nextSample(Collection paramCollection, int paramInt);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\random\RandomData.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */