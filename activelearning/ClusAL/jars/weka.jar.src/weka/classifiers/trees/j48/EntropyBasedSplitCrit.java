package weka.classifiers.trees.j48;

public abstract class EntropyBasedSplitCrit extends SplitCriterion {
  protected static double log2 = Math.log(2.0D);
  
  public final double logFunc(double paramDouble) {
    return (paramDouble < 1.0E-6D) ? 0.0D : (paramDouble * Math.log(paramDouble) / log2);
  }
  
  public final double oldEnt(Distribution paramDistribution) {
    double d = 0.0D;
    for (byte b = 0; b < paramDistribution.numClasses(); b++)
      d += logFunc(paramDistribution.perClass(b)); 
    return logFunc(paramDistribution.total()) - d;
  }
  
  public final double newEnt(Distribution paramDistribution) {
    double d = 0.0D;
    for (byte b = 0; b < paramDistribution.numBags(); b++) {
      for (byte b1 = 0; b1 < paramDistribution.numClasses(); b1++)
        d += logFunc(paramDistribution.perClassPerBag(b, b1)); 
      d -= logFunc(paramDistribution.perBag(b));
    } 
    return -d;
  }
  
  public final double splitEnt(Distribution paramDistribution) {
    double d = 0.0D;
    for (byte b = 0; b < paramDistribution.numBags(); b++)
      d += logFunc(paramDistribution.perBag(b)); 
    return logFunc(paramDistribution.total()) - d;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\EntropyBasedSplitCrit.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */