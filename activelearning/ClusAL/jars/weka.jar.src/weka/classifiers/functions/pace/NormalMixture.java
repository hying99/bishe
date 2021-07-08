package weka.classifiers.functions.pace;

import java.util.Random;

public class NormalMixture extends MixtureDistribution {
  protected double separatingThreshold = 0.05D;
  
  protected double trimingThreshold = 0.7D;
  
  protected double fittingIntervalLength = 3.0D;
  
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
    double d = 0.0D;
    for (int i = paramInt1; i <= paramInt2; i++)
      d += Maths.pnorm(-Math.abs(paramDouble - paramDoubleVector.get(i))); 
    return (d < this.separatingThreshold);
  }
  
  public DoubleVector supportPoints(DoubleVector paramDoubleVector, int paramInt) {
    if (paramDoubleVector.size() < 2)
      throw new IllegalArgumentException("data size < 2"); 
    return paramDoubleVector.copy();
  }
  
  public PaceMatrix fittingIntervals(DoubleVector paramDoubleVector) {
    DoubleVector doubleVector1 = paramDoubleVector.cat(paramDoubleVector.minus(this.fittingIntervalLength));
    DoubleVector doubleVector2 = paramDoubleVector.plus(this.fittingIntervalLength).cat(paramDoubleVector);
    PaceMatrix paceMatrix = new PaceMatrix(doubleVector1.size(), 2);
    paceMatrix.setMatrix(0, doubleVector1.size() - 1, 0, doubleVector1);
    paceMatrix.setMatrix(0, doubleVector2.size() - 1, 1, doubleVector2);
    return paceMatrix;
  }
  
  public PaceMatrix probabilityMatrix(DoubleVector paramDoubleVector, PaceMatrix paramPaceMatrix) {
    int i = paramDoubleVector.size();
    int j = paramPaceMatrix.getRowDimension();
    PaceMatrix paceMatrix = new PaceMatrix(j, i);
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++)
        paceMatrix.set(b, b1, Maths.pnorm(paramPaceMatrix.get(b, 1), paramDoubleVector.get(b1), 1.0D) - Maths.pnorm(paramPaceMatrix.get(b, 0), paramDoubleVector.get(b1), 1.0D)); 
    } 
    return paceMatrix;
  }
  
  public double empiricalBayesEstimate(double paramDouble) {
    if (Math.abs(paramDouble) > 10.0D)
      return paramDouble; 
    DoubleVector doubleVector = Maths.dnormLog(paramDouble, this.mixingDistribution.getPointValues(), 1.0D);
    doubleVector.minusEquals(doubleVector.max());
    doubleVector = doubleVector.map("java.lang.Math", "exp");
    doubleVector.timesEquals(this.mixingDistribution.getFunctionValues());
    return this.mixingDistribution.getPointValues().innerProduct(doubleVector) / doubleVector.sum();
  }
  
  public DoubleVector empiricalBayesEstimate(DoubleVector paramDoubleVector) {
    DoubleVector doubleVector = new DoubleVector(paramDoubleVector.size());
    for (byte b = 0; b < paramDoubleVector.size(); b++)
      doubleVector.set(b, empiricalBayesEstimate(paramDoubleVector.get(b))); 
    trim(doubleVector);
    return doubleVector;
  }
  
  public DoubleVector nestedEstimate(DoubleVector paramDoubleVector) {
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
  
  public DoubleVector subsetEstimate(DoubleVector paramDoubleVector) {
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
      if (Math.abs(paramDoubleVector.get(b)) <= this.trimingThreshold)
        paramDoubleVector.set(b, 0.0D); 
    } 
  }
  
  public double hf(double paramDouble) {
    DoubleVector doubleVector1 = this.mixingDistribution.getPointValues();
    DoubleVector doubleVector2 = this.mixingDistribution.getFunctionValues();
    DoubleVector doubleVector3 = Maths.dnormLog(paramDouble, doubleVector1, 1.0D);
    doubleVector3.minusEquals(doubleVector3.max());
    doubleVector3 = doubleVector3.map("java.lang.Math", "exp");
    doubleVector3.timesEquals(doubleVector2);
    return doubleVector1.times(2.0D * paramDouble).minusEquals(paramDouble * paramDouble).innerProduct(doubleVector3) / doubleVector3.sum();
  }
  
  public double h(double paramDouble) {
    DoubleVector doubleVector1 = this.mixingDistribution.getPointValues();
    DoubleVector doubleVector2 = this.mixingDistribution.getFunctionValues();
    DoubleVector doubleVector3 = Maths.dnorm(paramDouble, doubleVector1, 1.0D).timesEquals(doubleVector2);
    return doubleVector1.times(2.0D * paramDouble).minusEquals(paramDouble * paramDouble).innerProduct(doubleVector3);
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
    double d2 = 5.0D;
    DoubleVector doubleVector1 = Maths.rnorm(b1, d1, 1.0D, new Random());
    doubleVector1 = doubleVector1.cat(Maths.rnorm(b2, d2, 1.0D, new Random()));
    DoubleVector doubleVector2 = (new DoubleVector(b1, d1)).cat(new DoubleVector(b2, d2));
    System.out.println("==========================================================");
    System.out.println("This is to test the estimation of the mixing\ndistribution of the mixture of unit variance normal\ndistributions. The example mixture used is of the form: \n\n   0.5 * N(mu1, 1) + 0.5 * N(mu2, 1)\n");
    System.out.println("It also tests three estimators: the subset\nselector, the nested model selector, and the empirical Bayes\nestimator. Quadratic losses of the estimators are given, \nand are taken as the measure of their performance.");
    System.out.println("==========================================================");
    System.out.println("mu1 = " + d1 + " mu2 = " + d2 + "\n");
    System.out.println(doubleVector1.size() + " observations are: \n\n" + doubleVector1);
    System.out.println("\nQuadratic loss of the raw data (i.e., the MLE) = " + doubleVector1.sum2(doubleVector2));
    System.out.println("==========================================================");
    NormalMixture normalMixture = new NormalMixture();
    normalMixture.fit(doubleVector1, 1);
    System.out.println("The estimated mixing distribution is:\n" + normalMixture);
    DoubleVector doubleVector3 = normalMixture.nestedEstimate(doubleVector1.rev()).rev();
    System.out.println("\nThe Nested Estimate = \n" + doubleVector3);
    System.out.println("Quadratic loss = " + doubleVector3.sum2(doubleVector2));
    doubleVector3 = normalMixture.subsetEstimate(doubleVector1);
    System.out.println("\nThe Subset Estimate = \n" + doubleVector3);
    System.out.println("Quadratic loss = " + doubleVector3.sum2(doubleVector2));
    doubleVector3 = normalMixture.empiricalBayesEstimate(doubleVector1);
    System.out.println("\nThe Empirical Bayes Estimate = \n" + doubleVector3);
    System.out.println("Quadratic loss = " + doubleVector3.sum2(doubleVector2));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\NormalMixture.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */