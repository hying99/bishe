package org.apache.commons.math.util;

public interface DoubleArray {
  int getNumElements();
  
  double getElement(int paramInt);
  
  void setElement(int paramInt, double paramDouble);
  
  void addElement(double paramDouble);
  
  double addElementRolling(double paramDouble);
  
  double[] getElements();
  
  void clear();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\mat\\util\DoubleArray.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */