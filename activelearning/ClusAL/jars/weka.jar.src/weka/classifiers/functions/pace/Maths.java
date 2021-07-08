package weka.classifiers.functions.pace;

import java.util.Random;
import weka.core.Statistics;

public class Maths {
  public static final double PSI = 0.3989422804014327D;
  
  public static final double logPSI = -0.9189385332046727D;
  
  public static final int undefinedDistribution = 0;
  
  public static final int normalDistribution = 1;
  
  public static final int chisqDistribution = 2;
  
  public static double hypot(double paramDouble1, double paramDouble2) {
    double d;
    if (Math.abs(paramDouble1) > Math.abs(paramDouble2)) {
      d = paramDouble2 / paramDouble1;
      d = Math.abs(paramDouble1) * Math.sqrt(1.0D + d * d);
    } else if (paramDouble2 != 0.0D) {
      d = paramDouble1 / paramDouble2;
      d = Math.abs(paramDouble2) * Math.sqrt(1.0D + d * d);
    } else {
      d = 0.0D;
    } 
    return d;
  }
  
  public static double square(double paramDouble) {
    return paramDouble * paramDouble;
  }
  
  public static double pnorm(double paramDouble) {
    return Statistics.normalProbability(paramDouble);
  }
  
  public static double pnorm(double paramDouble1, double paramDouble2, double paramDouble3) {
    if (paramDouble3 <= 0.0D)
      throw new IllegalArgumentException("standard deviation <= 0.0"); 
    return pnorm((paramDouble1 - paramDouble2) / paramDouble3);
  }
  
  public static DoubleVector pnorm(double paramDouble1, DoubleVector paramDoubleVector, double paramDouble2) {
    DoubleVector doubleVector = new DoubleVector(paramDoubleVector.size());
    for (byte b = 0; b < paramDoubleVector.size(); b++)
      doubleVector.set(b, pnorm(paramDouble1, paramDoubleVector.get(b), paramDouble2)); 
    return doubleVector;
  }
  
  public static double dnorm(double paramDouble) {
    return Math.exp(-paramDouble * paramDouble / 2.0D) * 0.3989422804014327D;
  }
  
  public static double dnorm(double paramDouble1, double paramDouble2, double paramDouble3) {
    if (paramDouble3 <= 0.0D)
      throw new IllegalArgumentException("standard deviation <= 0.0"); 
    return dnorm((paramDouble1 - paramDouble2) / paramDouble3);
  }
  
  public static DoubleVector dnorm(double paramDouble1, DoubleVector paramDoubleVector, double paramDouble2) {
    DoubleVector doubleVector = new DoubleVector(paramDoubleVector.size());
    for (byte b = 0; b < paramDoubleVector.size(); b++)
      doubleVector.set(b, dnorm(paramDouble1, paramDoubleVector.get(b), paramDouble2)); 
    return doubleVector;
  }
  
  public static double dnormLog(double paramDouble) {
    return -0.9189385332046727D - paramDouble * paramDouble / 2.0D;
  }
  
  public static double dnormLog(double paramDouble1, double paramDouble2, double paramDouble3) {
    if (paramDouble3 <= 0.0D)
      throw new IllegalArgumentException("standard deviation <= 0.0"); 
    return -Math.log(paramDouble3) + dnormLog((paramDouble1 - paramDouble2) / paramDouble3);
  }
  
  public static DoubleVector dnormLog(double paramDouble1, DoubleVector paramDoubleVector, double paramDouble2) {
    DoubleVector doubleVector = new DoubleVector(paramDoubleVector.size());
    for (byte b = 0; b < paramDoubleVector.size(); b++)
      doubleVector.set(b, dnormLog(paramDouble1, paramDoubleVector.get(b), paramDouble2)); 
    return doubleVector;
  }
  
  public static DoubleVector rnorm(int paramInt, double paramDouble1, double paramDouble2, Random paramRandom) {
    if (paramDouble2 < 0.0D)
      throw new IllegalArgumentException("standard deviation < 0.0"); 
    if (paramDouble2 == 0.0D)
      return new DoubleVector(paramInt, paramDouble1); 
    DoubleVector doubleVector = new DoubleVector(paramInt);
    for (byte b = 0; b < paramInt; b++)
      doubleVector.set(b, (paramRandom.nextGaussian() + paramDouble1) / paramDouble2); 
    return doubleVector;
  }
  
  public static double pchisq(double paramDouble) {
    double d = Math.sqrt(paramDouble);
    return pnorm(d) - pnorm(-d);
  }
  
  public static double pchisq(double paramDouble1, double paramDouble2) {
    double d1 = Math.sqrt(paramDouble2);
    double d2 = Math.sqrt(paramDouble1);
    return pnorm(d2 - d1) - pnorm(-d2 - d1);
  }
  
  public static DoubleVector pchisq(double paramDouble, DoubleVector paramDoubleVector) {
    int i = paramDoubleVector.size();
    DoubleVector doubleVector = new DoubleVector(i);
    double d = Math.sqrt(paramDouble);
    for (byte b = 0; b < i; b++) {
      double d1 = Math.sqrt(paramDoubleVector.get(b));
      doubleVector.set(b, pnorm(d - d1) - pnorm(-d - d1));
    } 
    return doubleVector;
  }
  
  public static double dchisq(double paramDouble) {
    if (paramDouble == 0.0D)
      return Double.POSITIVE_INFINITY; 
    double d = Math.sqrt(paramDouble);
    return dnorm(d) / d;
  }
  
  public static double dchisq(double paramDouble1, double paramDouble2) {
    if (paramDouble2 == 0.0D)
      return dchisq(paramDouble1); 
    double d1 = Math.sqrt(paramDouble1);
    double d2 = Math.sqrt(paramDouble2);
    return (dnorm(d1 - d2) + dnorm(-d1 - d2)) / 2.0D * d1;
  }
  
  public static DoubleVector dchisq(double paramDouble, DoubleVector paramDoubleVector) {
    int i = paramDoubleVector.size();
    DoubleVector doubleVector = new DoubleVector(i);
    double d = Math.sqrt(paramDouble);
    for (byte b = 0; b < i; b++) {
      double d1 = Math.sqrt(paramDoubleVector.get(b));
      if (paramDoubleVector.get(b) == 0.0D) {
        doubleVector.set(b, dchisq(paramDouble));
      } else {
        doubleVector.set(b, (dnorm(d - d1) + dnorm(-d - d1)) / 2.0D * d);
      } 
    } 
    return doubleVector;
  }
  
  public static double dchisqLog(double paramDouble) {
    if (paramDouble == 0.0D)
      return Double.POSITIVE_INFINITY; 
    double d = Math.sqrt(paramDouble);
    return dnormLog(d) - Math.log(d);
  }
  
  public static double dchisqLog(double paramDouble1, double paramDouble2) {
    if (paramDouble2 == 0.0D)
      return dchisqLog(paramDouble1); 
    double d1 = Math.sqrt(paramDouble1);
    double d2 = Math.sqrt(paramDouble2);
    return Math.log(dnorm(d1 - d2) + dnorm(-d1 - d2)) - Math.log(2.0D * d1);
  }
  
  public static DoubleVector dchisqLog(double paramDouble, DoubleVector paramDoubleVector) {
    DoubleVector doubleVector = new DoubleVector(paramDoubleVector.size());
    double d = Math.sqrt(paramDouble);
    for (byte b = 0; b < paramDoubleVector.size(); b++) {
      double d1 = Math.sqrt(paramDoubleVector.get(b));
      if (paramDoubleVector.get(b) == 0.0D) {
        doubleVector.set(b, dchisqLog(paramDouble));
      } else {
        doubleVector.set(b, Math.log(dnorm(d - d1) + dnorm(-d - d1)) - Math.log(2.0D * d));
      } 
    } 
    return doubleVector;
  }
  
  public static DoubleVector rchisq(int paramInt, double paramDouble, Random paramRandom) {
    DoubleVector doubleVector = new DoubleVector(paramInt);
    double d = Math.sqrt(paramDouble);
    for (byte b = 0; b < paramInt; b++) {
      double d1 = paramRandom.nextGaussian() + d;
      doubleVector.set(b, d1 * d1);
    } 
    return doubleVector;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\Maths.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */