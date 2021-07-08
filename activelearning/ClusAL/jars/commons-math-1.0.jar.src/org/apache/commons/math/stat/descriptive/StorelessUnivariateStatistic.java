package org.apache.commons.math.stat.descriptive;

public interface StorelessUnivariateStatistic extends UnivariateStatistic {
  void increment(double paramDouble);
  
  void incrementAll(double[] paramArrayOfdouble);
  
  void incrementAll(double[] paramArrayOfdouble, int paramInt1, int paramInt2);
  
  double getResult();
  
  long getN();
  
  void clear();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\StorelessUnivariateStatistic.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */