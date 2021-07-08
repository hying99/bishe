package weka.classifiers.trees.j48;

import weka.core.Instance;
import weka.core.Instances;

public final class NoSplit extends ClassifierSplitModel {
  public NoSplit(Distribution paramDistribution) {
    this.m_distribution = new Distribution(paramDistribution);
    this.m_numSubsets = 1;
  }
  
  public final void buildClassifier(Instances paramInstances) throws Exception {
    this.m_distribution = new Distribution(paramInstances);
    this.m_numSubsets = 1;
  }
  
  public final int whichSubset(Instance paramInstance) {
    return 0;
  }
  
  public final double[] weights(Instance paramInstance) {
    return null;
  }
  
  public final String leftSide(Instances paramInstances) {
    return "";
  }
  
  public final String rightSide(int paramInt, Instances paramInstances) {
    return "";
  }
  
  public final String sourceExpression(int paramInt, Instances paramInstances) {
    return "true";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\NoSplit.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */