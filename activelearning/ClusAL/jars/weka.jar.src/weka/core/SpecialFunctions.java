package weka.core;

public final class SpecialFunctions {
  private static double log2 = Math.log(2.0D);
  
  public static double lnFactorial(double paramDouble) {
    return Statistics.lnGamma(paramDouble + 1.0D);
  }
  
  public static double log2Binomial(double paramDouble1, double paramDouble2) {
    if (Utils.gr(paramDouble2, paramDouble1))
      throw new ArithmeticException("Can't compute binomial coefficient."); 
    return (lnFactorial(paramDouble1) - lnFactorial(paramDouble2) - lnFactorial(paramDouble1 - paramDouble2)) / log2;
  }
  
  public static double log2Multinomial(double paramDouble, double[] paramArrayOfdouble) {
    double d = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      if (Utils.gr(paramArrayOfdouble[b], paramDouble))
        throw new ArithmeticException("Can't compute multinomial coefficient."); 
      d += lnFactorial(paramArrayOfdouble[b]);
    } 
    return (lnFactorial(paramDouble) - d) / log2;
  }
  
  public static void main(String[] paramArrayOfString) {
    double[] arrayOfDouble = { 1.0D, 2.0D, 3.0D };
    System.out.println("6!: " + Math.exp(lnFactorial(6.0D)));
    System.out.println("Binomial 6 over 2: " + Math.pow(2.0D, log2Binomial(6.0D, 2.0D)));
    System.out.println("Multinomial 6 over 1, 2, 3: " + Math.pow(2.0D, log2Multinomial(6.0D, arrayOfDouble)));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\SpecialFunctions.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */