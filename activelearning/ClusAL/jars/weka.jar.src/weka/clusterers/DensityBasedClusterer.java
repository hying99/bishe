package weka.clusterers;

import weka.core.Instance;
import weka.core.SerializedObject;
import weka.core.Utils;

public abstract class DensityBasedClusterer extends Clusterer {
  public abstract double[] clusterPriors() throws Exception;
  
  public abstract double[] logDensityPerClusterForInstance(Instance paramInstance) throws Exception;
  
  public double logDensityForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = logJointDensitiesForInstance(paramInstance);
    double d1 = arrayOfDouble[Utils.maxIndex(arrayOfDouble)];
    double d2 = 0.0D;
    for (byte b = 0; b < arrayOfDouble.length; b++)
      d2 += Math.exp(arrayOfDouble[b] - d1); 
    return d1 + Math.log(d2);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    return Utils.logs2probs(logJointDensitiesForInstance(paramInstance));
  }
  
  public double[] logJointDensitiesForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble1 = logDensityPerClusterForInstance(paramInstance);
    double[] arrayOfDouble2 = clusterPriors();
    for (byte b = 0; b < arrayOfDouble1.length; b++) {
      if (arrayOfDouble2[b] > 0.0D) {
        arrayOfDouble1[b] = arrayOfDouble1[b] + Math.log(arrayOfDouble2[b]);
      } else {
        throw new IllegalArgumentException("Cluster empty!");
      } 
    } 
    return arrayOfDouble1;
  }
  
  public static DensityBasedClusterer[] makeCopies(DensityBasedClusterer paramDensityBasedClusterer, int paramInt) throws Exception {
    if (paramDensityBasedClusterer == null)
      throw new Exception("No model clusterer set"); 
    DensityBasedClusterer[] arrayOfDensityBasedClusterer = new DensityBasedClusterer[paramInt];
    SerializedObject serializedObject = new SerializedObject(paramDensityBasedClusterer);
    for (byte b = 0; b < arrayOfDensityBasedClusterer.length; b++)
      arrayOfDensityBasedClusterer[b] = (DensityBasedClusterer)serializedObject.getObject(); 
    return arrayOfDensityBasedClusterer;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\clusterers\DensityBasedClusterer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */