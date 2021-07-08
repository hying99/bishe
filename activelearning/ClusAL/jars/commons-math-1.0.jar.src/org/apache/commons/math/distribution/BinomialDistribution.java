package org.apache.commons.math.distribution;

public interface BinomialDistribution extends IntegerDistribution {
  int getNumberOfTrials();
  
  double getProbabilityOfSuccess();
  
  void setNumberOfTrials(int paramInt);
  
  void setProbabilityOfSuccess(double paramDouble);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\BinomialDistribution.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */