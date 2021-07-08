package weka.classifiers.trees.j48;

import weka.core.Statistics;

public class Stats {
  public static double addErrs(double paramDouble1, double paramDouble2, float paramFloat) {
    if (paramFloat > 0.5D) {
      System.err.println("WARNING: confidence value for pruning  too high. Error estimate not modified.");
      return 0.0D;
    } 
    if (paramDouble2 < 1.0D) {
      double d = paramDouble1 * (1.0D - Math.pow(paramFloat, 1.0D / paramDouble1));
      return (paramDouble2 == 0.0D) ? d : (d + paramDouble2 * (addErrs(paramDouble1, 1.0D, paramFloat) - d));
    } 
    if (paramDouble2 + 0.5D >= paramDouble1)
      return Math.max(paramDouble1 - paramDouble2, 0.0D); 
    double d1 = Statistics.normalInverse((1.0F - paramFloat));
    double d2 = (paramDouble2 + 0.5D) / paramDouble1;
    double d3 = (d2 + d1 * d1 / 2.0D * paramDouble1 + d1 * Math.sqrt(d2 / paramDouble1 - d2 * d2 / paramDouble1 + d1 * d1 / 4.0D * paramDouble1 * paramDouble1)) / (1.0D + d1 * d1 / paramDouble1);
    return d3 * paramDouble1 - paramDouble2;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\Stats.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */