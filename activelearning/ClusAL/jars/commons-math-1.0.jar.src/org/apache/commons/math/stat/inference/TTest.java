package org.apache.commons.math.stat.inference;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;

public interface TTest {
  double pairedT(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) throws IllegalArgumentException, MathException;
  
  double pairedTTest(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) throws IllegalArgumentException, MathException;
  
  boolean pairedTTest(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, double paramDouble) throws IllegalArgumentException, MathException;
  
  double t(double paramDouble, double[] paramArrayOfdouble) throws IllegalArgumentException;
  
  double t(double paramDouble, StatisticalSummary paramStatisticalSummary) throws IllegalArgumentException;
  
  double homoscedasticT(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) throws IllegalArgumentException;
  
  double t(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) throws IllegalArgumentException;
  
  double t(StatisticalSummary paramStatisticalSummary1, StatisticalSummary paramStatisticalSummary2) throws IllegalArgumentException;
  
  double homoscedasticT(StatisticalSummary paramStatisticalSummary1, StatisticalSummary paramStatisticalSummary2) throws IllegalArgumentException;
  
  double tTest(double paramDouble, double[] paramArrayOfdouble) throws IllegalArgumentException, MathException;
  
  boolean tTest(double paramDouble1, double[] paramArrayOfdouble, double paramDouble2) throws IllegalArgumentException, MathException;
  
  double tTest(double paramDouble, StatisticalSummary paramStatisticalSummary) throws IllegalArgumentException, MathException;
  
  boolean tTest(double paramDouble1, StatisticalSummary paramStatisticalSummary, double paramDouble2) throws IllegalArgumentException, MathException;
  
  double tTest(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) throws IllegalArgumentException, MathException;
  
  double homoscedasticTTest(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) throws IllegalArgumentException, MathException;
  
  boolean tTest(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, double paramDouble) throws IllegalArgumentException, MathException;
  
  boolean homoscedasticTTest(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, double paramDouble) throws IllegalArgumentException, MathException;
  
  double tTest(StatisticalSummary paramStatisticalSummary1, StatisticalSummary paramStatisticalSummary2) throws IllegalArgumentException, MathException;
  
  double homoscedasticTTest(StatisticalSummary paramStatisticalSummary1, StatisticalSummary paramStatisticalSummary2) throws IllegalArgumentException, MathException;
  
  boolean tTest(StatisticalSummary paramStatisticalSummary1, StatisticalSummary paramStatisticalSummary2, double paramDouble) throws IllegalArgumentException, MathException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\inference\TTest.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */