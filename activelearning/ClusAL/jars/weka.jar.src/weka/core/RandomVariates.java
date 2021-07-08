package weka.core;

import java.util.Random;

public final class RandomVariates extends Random {
  public RandomVariates() {}
  
  public RandomVariates(long paramLong) {
    super(paramLong);
  }
  
  protected int next(int paramInt) {
    return super.next(paramInt);
  }
  
  public double nextExponential() {
    return -Math.log(1.0D - nextDouble());
  }
  
  public double nextErlang(int paramInt) throws Exception {
    if (paramInt < 1)
      throw new Exception("Shape parameter of Erlang distribution must be greater than 1!"); 
    double d = 1.0D;
    for (byte b = 1; b <= paramInt; b++)
      d *= nextDouble(); 
    return -Math.log(d);
  }
  
  public double nextGamma(double paramDouble) throws Exception {
    double d3;
    double d4;
    double d5;
    double d6;
    double d7;
    if (paramDouble <= 0.0D)
      throw new Exception("Shape parameter of Gamma distributionmust be greater than 0!"); 
    if (paramDouble == 1.0D)
      return nextExponential(); 
    if (paramDouble < 1.0D) {
      double d = 1.0D + Math.exp(-1.0D) * paramDouble;
      while (true) {
        double d20 = d * nextDouble();
        if (d20 < 1.0D) {
          d3 = Math.exp(Math.log(d20) / paramDouble);
          d4 = d3;
        } else {
          d3 = -Math.log((d - d20) / paramDouble);
          d4 = (1.0D - paramDouble) * Math.log(d3);
        } 
        if (nextExponential() >= d4)
          return d3; 
      } 
    } 
    double d1 = paramDouble - 1.0D;
    double d2 = Math.sqrt(d1);
    if (paramDouble <= 2.0D) {
      d3 = d1 / 2.0D;
      d4 = 0.0D;
      d5 = d3;
      d6 = -1.0D;
      d7 = 0.0D;
    } else {
      d3 = d2 - 0.5D;
      d5 = d1 - d3;
      d4 = d5 - d3;
      d6 = 1.0D - d1 / d4;
      d7 = Math.exp(d1 * Math.log(d4 / d1) + 2.0D * d3);
    } 
    double d8 = Math.exp(d1 * Math.log(d5 / d1) + d3);
    double d9 = d1 + d2;
    double d10 = d9 + d2;
    double d11 = 1.0D - d1 / d10;
    double d12 = Math.exp(d1 * Math.log(d9 / d1) - d2);
    double d13 = Math.exp(d1 * Math.log(d10 / d1) - 2.0D * d2);
    double d14 = 2.0D * d12 * d2;
    double d15 = 2.0D * d8 * d3 + d14;
    double d16 = d13 / d11 + d15;
    double d17 = -d7 / d6 + d16;
    double d18 = Double.MAX_VALUE;
    double d19 = d1;
    while (Math.log(d18) > d1 * Math.log(d19 / d1) + d1 - d19) {
      double d = nextDouble() * d17;
      if (d <= d14) {
        d18 = d / d2 - d12;
        if (d18 <= 0.0D)
          return d1 + d / d12; 
        if (d18 <= d13)
          return d9 + d18 * d2 / d13; 
        double d20 = nextDouble();
        d19 = d9 + d20 * d2;
        double d21 = 2.0D * d9 - d19;
        if (d18 >= d12 + (d12 - 1.0D) * (d19 - d9) / (d9 - d1))
          return d21; 
        if (d18 <= d12 + (d1 / d9 - 1.0D) * d12 * (d19 - d9))
          return d19; 
        if (d18 < 2.0D * d12 - 1.0D || d18 < 2.0D * d12 - Math.exp(d1 * Math.log(d21 / d1) + d1 - d21))
          continue; 
        return d21;
      } 
      if (d <= d15) {
        d18 = (d - d14) / d3 - d8;
        if (d18 <= 0.0D)
          return d1 - (d - d14) / d8; 
        if (d18 <= d7)
          return d4 + d18 * d3 / d7; 
        double d20 = nextDouble();
        d19 = d4 + d20 * d3;
        double d21 = 2.0D * d5 - d19;
        if (d18 >= d8 + (d8 - 1.0D) * (d19 - d5) / (d5 - d1))
          return d21; 
        if (d18 <= d8 * (d19 - d4) / d3)
          return d19; 
        if (d18 < 2.0D * d8 - 1.0D || d18 < 2.0D * d8 - Math.exp(d1 * Math.log(d21 / d1) + d1 - d21))
          continue; 
        return d21;
      } 
      if (d < d16) {
        d18 = nextDouble();
        d = (d16 - d) / (d16 - d15);
        d19 = d10 - Math.log(d) / d11;
        if (d18 <= (d11 * (d10 - d19) + 1.0D) / d)
          return d19; 
        d18 = d18 * d13 * d;
        continue;
      } 
      d18 = nextDouble();
      d = (d17 - d) / (d17 - d16);
      d19 = d4 - Math.log(d) / d6;
      if (d19 < 0.0D)
        continue; 
      if (d18 <= (d6 * (d4 - d19) + 1.0D) / d)
        return d19; 
      d18 = d18 * d7 * d;
    } 
    return d19;
  }
  
  public static void main(String[] paramArrayOfString) {
    int i = Integer.parseInt(paramArrayOfString[0]);
    if (i <= 0)
      i = 10; 
    long l = Long.parseLong(paramArrayOfString[1]);
    if (l <= 0L)
      l = 45L; 
    RandomVariates randomVariates = new RandomVariates(l);
    double[] arrayOfDouble = new double[i];
    try {
      System.out.println("Generate " + i + " values with std. exp dist:");
      byte b;
      for (b = 0; b < i; b++) {
        arrayOfDouble[b] = randomVariates.nextExponential();
        System.out.print("[" + b + "] " + arrayOfDouble[b] + ", ");
      } 
      System.out.println("\nMean is " + Utils.mean(arrayOfDouble) + ", Variance is " + Utils.variance(arrayOfDouble) + "\n\nGenerate " + i + " values with" + " std. Erlang-5 dist:");
      for (b = 0; b < i; b++) {
        arrayOfDouble[b] = randomVariates.nextErlang(5);
        System.out.print("[" + b + "] " + arrayOfDouble[b] + ", ");
      } 
      System.out.println("\nMean is " + Utils.mean(arrayOfDouble) + ", Variance is " + Utils.variance(arrayOfDouble) + "\n\nGenerate " + i + " values with" + " std. Gamma(4.5) dist:");
      for (b = 0; b < i; b++) {
        arrayOfDouble[b] = randomVariates.nextGamma(4.5D);
        System.out.print("[" + b + "] " + arrayOfDouble[b] + ", ");
      } 
      System.out.println("\nMean is " + Utils.mean(arrayOfDouble) + ", Variance is " + Utils.variance(arrayOfDouble) + "\n\nGenerate " + i + " values with" + " std. Gamma(0.5) dist:");
      for (b = 0; b < i; b++) {
        arrayOfDouble[b] = randomVariates.nextGamma(0.5D);
        System.out.print("[" + b + "] " + arrayOfDouble[b] + ", ");
      } 
      System.out.println("\nMean is " + Utils.mean(arrayOfDouble) + ", Variance is " + Utils.variance(arrayOfDouble) + "\n\nGenerate " + i + " values with" + " std. Gaussian(5, 2) dist:");
      for (b = 0; b < i; b++) {
        arrayOfDouble[b] = randomVariates.nextGaussian() * 2.0D + 5.0D;
        System.out.print("[" + b + "] " + arrayOfDouble[b] + ", ");
      } 
      System.out.println("\nMean is " + Utils.mean(arrayOfDouble) + ", Variance is " + Utils.variance(arrayOfDouble) + "\n");
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\RandomVariates.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */