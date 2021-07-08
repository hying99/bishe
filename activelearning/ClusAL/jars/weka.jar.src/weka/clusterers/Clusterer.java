package weka.clusterers;

import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializedObject;
import weka.core.Utils;

public abstract class Clusterer implements Cloneable, Serializable {
  public abstract void buildClusterer(Instances paramInstances) throws Exception;
  
  public int clusterInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = distributionForInstance(paramInstance);
    if (arrayOfDouble == null)
      throw new Exception("Null distribution predicted"); 
    if (Utils.sum(arrayOfDouble) <= 0.0D)
      throw new Exception("Unable to cluster instance"); 
    return Utils.maxIndex(arrayOfDouble);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = new double[numberOfClusters()];
    arrayOfDouble[clusterInstance(paramInstance)] = 1.0D;
    return arrayOfDouble;
  }
  
  public abstract int numberOfClusters() throws Exception;
  
  public static Clusterer forName(String paramString, String[] paramArrayOfString) throws Exception {
    return (Clusterer)Utils.forName(Clusterer.class, paramString, paramArrayOfString);
  }
  
  public static Clusterer[] makeCopies(Clusterer paramClusterer, int paramInt) throws Exception {
    if (paramClusterer == null)
      throw new Exception("No model clusterer set"); 
    Clusterer[] arrayOfClusterer = new Clusterer[paramInt];
    SerializedObject serializedObject = new SerializedObject(paramClusterer);
    for (byte b = 0; b < arrayOfClusterer.length; b++)
      arrayOfClusterer[b] = (Clusterer)serializedObject.getObject(); 
    return arrayOfClusterer;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\clusterers\Clusterer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */