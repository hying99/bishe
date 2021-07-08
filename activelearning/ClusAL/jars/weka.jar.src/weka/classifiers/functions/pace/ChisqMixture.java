package weka.classifiers.functions.pace;

import java.util.Random;

public class ChisqMixture extends MixtureDistribution {
  protected double separatingThreshold = 0.05D;
  
  protected double trimingThreshold = 0.5D;
  
  protected double supportThreshold = 0.5D;
  
  protected int maxNumSupportPoints = 200;
  
  protected int fittingIntervalLength = 3;
  
  protected double fittingIntervalThreshold = 0.5D;
  
  public double getSeparatingThreshold() {
    return this.separatingThreshold;
  }
  
  public void setSeparatingThreshold(double paramDouble) {
    this.separatingThreshold = paramDouble;
  }
  
  public double getTrimingThreshold() {
    return this.trimingThreshold;
  }
  
  public void setTrimingThreshold(double paramDouble) {
    this.trimingThreshold = paramDouble;
  }
  
  public boolean separable(DoubleVector paramDoubleVector, int paramInt1, int paramInt2, double paramDouble) {
    DoubleVector doubleVector = paramDoubleVector.sqrt();
    double d = Math.sqrt(paramDouble);
    NormalMixture normalMixture = new NormalMixture();
    normalMixture.setSeparatingThreshold(this.separatingThreshold);
    return normalMixture.separable(doubleVector, paramInt1, paramInt2, d);
  }
  
  public DoubleVector supportPoints(DoubleVector paramDoubleVector, int paramInt) {
    DoubleVector doubleVector = new DoubleVector();
    doubleVector.setCapacity(paramDoubleVector.size() + 1);
    if (paramDoubleVector.get(0) < this.supportThreshold || paramInt != 0)
      doubleVector.addElement(0.0D); 
    for (byte b = 0; b < paramDoubleVector.size(); b++) {
      if (paramDoubleVector.get(b) > this.supportThreshold)
        doubleVector.addElement(paramDoubleVector.get(b)); 
    } 
    if (doubleVector.size() > this.maxNumSupportPoints)
      throw new IllegalArgumentException("Too many support points. "); 
    return doubleVector;
  }
  
  public PaceMatrix fittingIntervals(DoubleVector paramDoubleVector) {
    PaceMatrix paceMatrix = new PaceMatrix(paramDoubleVector.size() * 2, 2);
    DoubleVector doubleVector = paramDoubleVector.sqrt();
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < paramDoubleVector.size(); b2++) {
      double d1 = doubleVector.get(b2) - this.fittingIntervalLength;
      if (d1 < this.fittingIntervalThreshold)
        d1 = 0.0D; 
      d1 *= d1;
      double d2 = paramDoubleVector.get(b2);
      if (d2 < this.fittingIntervalThreshold)
        d2 = this.fittingIntervalThreshold; 
      paceMatrix.set(b1, 0, d1);
      paceMatrix.set(b1, 1, d2);
      b1++;
    } 
    for (b2 = 0; b2 < paramDoubleVector.size(); b2++) {
      double d1 = paramDoubleVector.get(b2);
      if (d1 < this.fittingIntervalThreshold)
        d1 = 0.0D; 
      double d2 = doubleVector.get(b2) + this.fittingIntervalThreshold;
      d2 *= d2;
      paceMatrix.set(b1, 0, d1);
      paceMatrix.set(b1, 1, d2);
      b1++;
    } 
    paceMatrix.setRowDimension(b1);
    return paceMatrix;
  }
  
  public PaceMatrix probabilityMatrix(DoubleVector paramDoubleVector, PaceMatrix paramPaceMatrix) {
    int i = paramDoubleVector.size();
    int j = paramPaceMatrix.getRowDimension();
    PaceMatrix paceMatrix = new PaceMatrix(j, i);
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++)
        paceMatrix.set(b, b1, Maths.pchisq(paramPaceMatrix.get(b, 1), paramDoubleVector.get(b1)) - Maths.pchisq(paramPaceMatrix.get(b, 0), paramDoubleVector.get(b1))); 
    } 
    return paceMatrix;
  }
  
  public double pace6(double paramDouble) {
    if (paramDouble > 100.0D)
      return paramDouble; 
    DoubleVector doubleVector1 = this.mixingDistribution.getPointValues();
    DoubleVector doubleVector2 = this.mixingDistribution.getFunctionValues();
    DoubleVector doubleVector3 = doubleVector1.sqrt();
    DoubleVector doubleVector4 = Maths.dchisqLog(paramDouble, doubleVector1);
    doubleVector4.minusEquals(doubleVector4.max());
    doubleVector4 = doubleVector4.map("java.lang.Math", "exp").timesEquals(doubleVector2);
    double d = doubleVector3.innerProduct(doubleVector4) / doubleVector4.sum();
    return d * d;
  }
  
  public DoubleVector pace6(DoubleVector paramDoubleVector) {
    DoubleVector doubleVector = new DoubleVector(paramDoubleVector.size());
    for (byte b = 0; b < paramDoubleVector.size(); b++)
      doubleVector.set(b, pace6(paramDoubleVector.get(b))); 
    trim(doubleVector);
    return doubleVector;
  }
  
  public DoubleVector pace2(DoubleVector paramDoubleVector) {
    DoubleVector doubleVector1 = new DoubleVector(paramDoubleVector.size());
    int i;
    for (i = 0; i < paramDoubleVector.size(); i++)
      doubleVector1.set(i, hf(paramDoubleVector.get(i))); 
    doubleVector1.cumulateInPlace();
    i = doubleVector1.indexOfMax();
    DoubleVector doubleVector2 = paramDoubleVector.copy();
    if (i < paramDoubleVector.size() - 1)
      doubleVector2.set(i + 1, paramDoubleVector.size() - 1, 0.0D); 
    trim(doubleVector2);
    return doubleVector2;
  }
  
  public DoubleVector pace4(DoubleVector paramDoubleVector) {
    DoubleVector doubleVector1 = h(paramDoubleVector);
    DoubleVector doubleVector2 = paramDoubleVector.copy();
    for (byte b = 0; b < paramDoubleVector.size(); b++) {
      if (doubleVector1.get(b) <= 0.0D)
        doubleVector2.set(b, 0.0D); 
    } 
    trim(doubleVector2);
    return doubleVector2;
  }
  
  public void trim(DoubleVector paramDoubleVector) {
    for (byte b = 0; b < paramDoubleVector.size(); b++) {
      if (paramDoubleVector.get(b) <= this.trimingThreshold)
        paramDoubleVector.set(b, 0.0D); 
    } 
  }
  
  public double hf(double paramDouble) {
    DoubleVector doubleVector1 = this.mixingDistribution.getPointValues();
    DoubleVector doubleVector2 = this.mixingDistribution.getFunctionValues();
    double d1 = Math.sqrt(paramDouble);
    DoubleVector doubleVector3 = doubleVector1.sqrt();
    DoubleVector doubleVector4 = Maths.dnormLog(d1, doubleVector3, 1.0D);
    double d2 = doubleVector4.max();
    doubleVector4.minusEquals(d2);
    DoubleVector doubleVector5 = Maths.dnormLog(-d1, doubleVector3, 1.0D);
    doubleVector5.minusEquals(d2);
    doubleVector4 = doubleVector4.map("java.lang.Math", "exp");
    doubleVector4.timesEquals(doubleVector2);
    doubleVector5 = doubleVector5.map("java.lang.Math", "exp");
    doubleVector5.timesEquals(doubleVector2);
    return (doubleVector1.minus(d1 / 2.0D).innerProduct(doubleVector4) - doubleVector1.plus(d1 / 2.0D).innerProduct(doubleVector5)) / (doubleVector4.sum() + doubleVector5.sum());
  }
  
  public double h(double paramDouble) {
    if (paramDouble == 0.0D)
      return 0.0D; 
    DoubleVector doubleVector1 = this.mixingDistribution.getPointValues();
    DoubleVector doubleVector2 = this.mixingDistribution.getFunctionValues();
    double d = Math.sqrt(paramDouble);
    DoubleVector doubleVector3 = doubleVector1.sqrt();
    DoubleVector doubleVector4 = Maths.dnorm(d, doubleVector3, 1.0D).timesEquals(doubleVector2);
    DoubleVector doubleVector5 = Maths.dnorm(-d, doubleVector3, 1.0D).timesEquals(doubleVector2);
    return doubleVector1.minus(d / 2.0D).innerProduct(doubleVector4) - doubleVector1.plus(d / 2.0D).innerProduct(doubleVector5);
  }
  
  public DoubleVector h(DoubleVector paramDoubleVector) {
    DoubleVector doubleVector = new DoubleVector(paramDoubleVector.size());
    for (byte b = 0; b < paramDoubleVector.size(); b++)
      doubleVector.set(b, h(paramDoubleVector.get(b))); 
    return doubleVector;
  }
  
  public double f(double paramDouble) {
    DoubleVector doubleVector1 = this.mixingDistribution.getPointValues();
    DoubleVector doubleVector2 = this.mixingDistribution.getFunctionValues();
    return Maths.dchisq(paramDouble, doubleVector1).timesEquals(doubleVector2).sum();
  }
  
  public DoubleVector f(DoubleVector paramDoubleVector) {
    DoubleVector doubleVector = new DoubleVector(paramDoubleVector.size());
    for (byte b = 0; b < paramDoubleVector.size(); b++)
      doubleVector.set(b, h(doubleVector.get(b))); 
    return doubleVector;
  }
  
  public String toString() {
    return this.mixingDistribution.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    byte b1 = 50;
    byte b2 = 50;
    double d1 = 0.0D;
    double d2 = 10.0D;
    double d3 = Math.sqrt(d1);
    double d4 = Math.sqrt(d2);
    DoubleVector doubleVector1 = Maths.rnorm(b1, d3, 1.0D, new Random());
    doubleVector1 = doubleVector1.cat(Maths.rnorm(b2, d4, 1.0D, new Random()));
    DoubleVector doubleVector2 = doubleVector1;
    doubleVector1 = doubleVector1.square();
    doubleVector1.sort();
    DoubleVector doubleVector3 = (new DoubleVector(b1, d3)).cat(new DoubleVector(b2, d4));
    System.out.println("==========================================================");
    System.out.println("This is to test the estimation of the mixing\ndistribution of the mixture of non-central Chi-square\ndistributions. The example mixture used is of the form: \n\n   0.5 * Chi^2_1(ncp1) + 0.5 * Chi^2_1(ncp2)\n");
    System.out.println("It also tests the PACE estimators. Quadratic losses of the\nestimators are given, measuring their performance.");
    System.out.println("==========================================================");
    System.out.println("ncp1 = " + d1 + " ncp2 = " + d2 + "\n");
    System.out.println(doubleVector1.size() + " observations are: \n\n" + doubleVector1);
    System.out.println("\nQuadratic loss of the raw data (i.e., the MLE) = " + doubleVector2.sum2(doubleVector3));
    System.out.println("==========================================================");
    ChisqMixture chisqMixture = new ChisqMixture();
    chisqMixture.fit(doubleVector1, 1);
    System.out.println("The estimated mixing distribution is\n" + chisqMixture);
    DoubleVector doubleVector4 = chisqMixture.pace2(doubleVector1.rev()).rev();
    System.out.println("\nThe PACE2 Estimate = \n" + doubleVector4);
    System.out.println("Quadratic loss = " + doubleVector4.sqrt().times(doubleVector2.sign()).sum2(doubleVector3));
    doubleVector4 = chisqMixture.pace4(doubleVector1);
    System.out.println("\nThe PACE4 Estimate = \n" + doubleVector4);
    System.out.println("Quadratic loss = " + doubleVector4.sqrt().times(doubleVector2.sign()).sum2(doubleVector3));
    doubleVector4 = chisqMixture.pace6(doubleVector1);
    System.out.println("\nThe PACE6 Estimate = \n" + doubleVector4);
    System.out.println("Quadratic loss = " + doubleVector4.sqrt().times(doubleVector2.sign()).sum2(doubleVector3));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\ChisqMixture.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */