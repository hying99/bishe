package org.apache.commons.math.analysis;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;

public interface UnivariateRealSolver {
  void setMaximalIterationCount(int paramInt);
  
  int getMaximalIterationCount();
  
  void resetMaximalIterationCount();
  
  void setAbsoluteAccuracy(double paramDouble);
  
  double getAbsoluteAccuracy();
  
  void resetAbsoluteAccuracy();
  
  void setRelativeAccuracy(double paramDouble);
  
  double getRelativeAccuracy();
  
  void resetRelativeAccuracy();
  
  void setFunctionValueAccuracy(double paramDouble);
  
  double getFunctionValueAccuracy();
  
  void resetFunctionValueAccuracy();
  
  double solve(double paramDouble1, double paramDouble2) throws ConvergenceException, FunctionEvaluationException;
  
  double solve(double paramDouble1, double paramDouble2, double paramDouble3) throws ConvergenceException, FunctionEvaluationException;
  
  double getResult();
  
  int getIterationCount();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\UnivariateRealSolver.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */