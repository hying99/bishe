package weka.classifiers.trees.j48;

import java.io.Serializable;

public abstract class SplitCriterion implements Serializable {
  public double splitCritValue(Distribution paramDistribution) {
    return 0.0D;
  }
  
  public double splitCritValue(Distribution paramDistribution1, Distribution paramDistribution2) {
    return 0.0D;
  }
  
  public double splitCritValue(Distribution paramDistribution1, Distribution paramDistribution2, int paramInt) {
    return 0.0D;
  }
  
  public double splitCritValue(Distribution paramDistribution1, Distribution paramDistribution2, Distribution paramDistribution3) {
    return 0.0D;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\SplitCriterion.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */