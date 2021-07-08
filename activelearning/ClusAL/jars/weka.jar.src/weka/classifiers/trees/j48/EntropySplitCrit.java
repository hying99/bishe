package weka.classifiers.trees.j48;

import weka.core.Utils;

public final class EntropySplitCrit extends EntropyBasedSplitCrit {
  public final double splitCritValue(Distribution paramDistribution) {
    return newEnt(paramDistribution);
  }
  
  public final double splitCritValue(Distribution paramDistribution1, Distribution paramDistribution2) {
    double d = 0.0D;
    byte b1 = 0;
    byte b3;
    for (b3 = 0; b3 < paramDistribution2.numClasses(); b3++) {
      if (Utils.gr(paramDistribution1.perClass(b3), 0.0D) || Utils.gr(paramDistribution2.perClass(b3), 0.0D))
        b1++; 
    } 
    for (byte b2 = 0; b2 < paramDistribution2.numBags(); b2++) {
      if (Utils.gr(paramDistribution2.perBag(b2), 0.0D)) {
        for (b3 = 0; b3 < paramDistribution2.numClasses(); b3++) {
          if (Utils.gr(paramDistribution2.perClassPerBag(b2, b3), 0.0D))
            d -= paramDistribution2.perClassPerBag(b2, b3) * Math.log(paramDistribution1.perClassPerBag(b2, b3) + 1.0D); 
        } 
        d += paramDistribution2.perBag(b2) * Math.log(paramDistribution1.perBag(b2) + b1);
      } 
    } 
    return d / log2;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\EntropySplitCrit.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */