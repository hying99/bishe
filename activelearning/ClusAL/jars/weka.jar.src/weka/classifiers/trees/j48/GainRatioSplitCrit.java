package weka.classifiers.trees.j48;

import weka.core.Utils;

public final class GainRatioSplitCrit extends EntropyBasedSplitCrit {
  public final double splitCritValue(Distribution paramDistribution) {
    double d1 = oldEnt(paramDistribution) - newEnt(paramDistribution);
    if (Utils.eq(d1, 0.0D))
      return Double.MAX_VALUE; 
    double d2 = splitEnt(paramDistribution);
    return Utils.eq(d2, 0.0D) ? Double.MAX_VALUE : (d2 / d1);
  }
  
  public final double splitCritValue(Distribution paramDistribution, double paramDouble1, double paramDouble2) {
    double d = splitEnt(paramDistribution, paramDouble1);
    if (Utils.eq(d, 0.0D))
      return 0.0D; 
    d /= paramDouble1;
    return paramDouble2 / d;
  }
  
  private final double splitEnt(Distribution paramDistribution, double paramDouble) {
    double d1 = 0.0D;
    double d2 = paramDouble - paramDistribution.total();
    if (Utils.gr(paramDistribution.total(), 0.0D)) {
      for (byte b = 0; b < paramDistribution.numBags(); b++)
        d1 -= logFunc(paramDistribution.perBag(b)); 
      d1 -= logFunc(d2);
      d1 += logFunc(paramDouble);
    } 
    return d1;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\GainRatioSplitCrit.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */