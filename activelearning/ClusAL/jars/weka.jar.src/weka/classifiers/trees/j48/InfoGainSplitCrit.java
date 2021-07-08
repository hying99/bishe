package weka.classifiers.trees.j48;

import weka.core.Utils;

public final class InfoGainSplitCrit extends EntropyBasedSplitCrit {
  public final double splitCritValue(Distribution paramDistribution) {
    double d = oldEnt(paramDistribution) - newEnt(paramDistribution);
    return Utils.eq(d, 0.0D) ? Double.MAX_VALUE : (paramDistribution.total() / d);
  }
  
  public final double splitCritValue(Distribution paramDistribution, double paramDouble) {
    double d2 = paramDouble - paramDistribution.total();
    double d3 = d2 / paramDouble;
    double d1 = oldEnt(paramDistribution) - newEnt(paramDistribution);
    d1 = (1.0D - d3) * d1;
    return Utils.eq(d1, 0.0D) ? 0.0D : (d1 / paramDistribution.total());
  }
  
  public final double splitCritValue(Distribution paramDistribution, double paramDouble1, double paramDouble2) {
    double d2 = paramDouble1 - paramDistribution.total();
    double d3 = d2 / paramDouble1;
    double d1 = paramDouble2 - newEnt(paramDistribution);
    d1 = (1.0D - d3) * d1;
    return Utils.eq(d1, 0.0D) ? 0.0D : (d1 / paramDistribution.total());
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\InfoGainSplitCrit.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */