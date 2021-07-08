package weka.classifiers.functions.pace;

public abstract class MixtureDistribution {
  protected DiscreteFunction mixingDistribution;
  
  public static final int NNMMethod = 1;
  
  public static final int PMMethod = 2;
  
  public DiscreteFunction getMixingDistribution() {
    return this.mixingDistribution;
  }
  
  public void setMixingDistribution(DiscreteFunction paramDiscreteFunction) {
    this.mixingDistribution = paramDiscreteFunction;
  }
  
  public void fit(DoubleVector paramDoubleVector) {
    fit(paramDoubleVector, 1);
  }
  
  public void fit(DoubleVector paramDoubleVector, int paramInt) {
    DoubleVector doubleVector1 = (DoubleVector)paramDoubleVector.clone();
    if (doubleVector1.unsorted())
      doubleVector1.sort(); 
    int i = doubleVector1.size();
    int j = 0;
    DiscreteFunction discreteFunction = new DiscreteFunction();
    for (byte b = 0; b < i - 1; b++) {
      if (separable(doubleVector1, j, b, doubleVector1.get(b + 1)) && separable(doubleVector1, b + 1, i - 1, doubleVector1.get(b))) {
        DoubleVector doubleVector = doubleVector1.subvector(j, b);
        discreteFunction.plusEquals(fitForSingleCluster(doubleVector, paramInt).timesEquals((b - j + 1)));
        j = b + 1;
      } 
    } 
    DoubleVector doubleVector2 = doubleVector1.subvector(j, i - 1);
    discreteFunction.plusEquals(fitForSingleCluster(doubleVector2, paramInt).timesEquals((i - j)));
    discreteFunction.sort();
    discreteFunction.normalize();
    this.mixingDistribution = discreteFunction;
  }
  
  public DiscreteFunction fitForSingleCluster(DoubleVector paramDoubleVector, int paramInt) {
    DoubleVector doubleVector2;
    if (paramDoubleVector.size() < 2)
      return new DiscreteFunction(paramDoubleVector); 
    DoubleVector doubleVector1 = supportPoints(paramDoubleVector, 0);
    PaceMatrix paceMatrix1 = fittingIntervals(paramDoubleVector);
    PaceMatrix paceMatrix2 = probabilityMatrix(doubleVector1, paceMatrix1);
    PaceMatrix paceMatrix3 = new PaceMatrix(empiricalProbability(paramDoubleVector, paceMatrix1).timesEquals(1.0D / paramDoubleVector.size()));
    IntVector intVector = IntVector.seq(0, doubleVector1.size() - 1);
    switch (paramInt) {
      case 1:
        doubleVector2 = paceMatrix2.nnls(paceMatrix3, intVector);
        break;
      case 2:
        doubleVector2 = paceMatrix2.nnlse1(paceMatrix3, intVector);
        break;
      default:
        throw new IllegalArgumentException("unknown method");
    } 
    DoubleVector doubleVector3 = new DoubleVector(intVector.size());
    for (byte b = 0; b < doubleVector3.size(); b++)
      doubleVector3.set(b, doubleVector1.get(intVector.get(b))); 
    DiscreteFunction discreteFunction = new DiscreteFunction(doubleVector3, doubleVector2);
    discreteFunction.sort();
    discreteFunction.normalize();
    return discreteFunction;
  }
  
  public abstract boolean separable(DoubleVector paramDoubleVector, int paramInt1, int paramInt2, double paramDouble);
  
  public abstract DoubleVector supportPoints(DoubleVector paramDoubleVector, int paramInt);
  
  public abstract PaceMatrix fittingIntervals(DoubleVector paramDoubleVector);
  
  public abstract PaceMatrix probabilityMatrix(DoubleVector paramDoubleVector, PaceMatrix paramPaceMatrix);
  
  public PaceMatrix empiricalProbability(DoubleVector paramDoubleVector, PaceMatrix paramPaceMatrix) {
    int i = paramDoubleVector.size();
    int j = paramPaceMatrix.getRowDimension();
    PaceMatrix paceMatrix = new PaceMatrix(j, 1, 0.0D);
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < j; b1++) {
        double d = 0.0D;
        if (paramPaceMatrix.get(b1, 0) == paramDoubleVector.get(b) || paramPaceMatrix.get(b1, 1) == paramDoubleVector.get(b)) {
          d = 0.5D;
        } else if (paramPaceMatrix.get(b1, 0) < paramDoubleVector.get(b) && paramPaceMatrix.get(b1, 1) > paramDoubleVector.get(b)) {
          d = 1.0D;
        } 
        paceMatrix.setPlus(b1, 0, d);
      } 
    } 
    return paceMatrix;
  }
  
  public String toString() {
    return "The mixing distribution:\n" + this.mixingDistribution.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\MixtureDistribution.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */