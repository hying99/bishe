package weka.gui.boundaryvisualizer;

import weka.core.Instances;

public interface DataGenerator {
  void buildGenerator(Instances paramInstances) throws Exception;
  
  double[][] generateInstances(int[] paramArrayOfint) throws Exception;
  
  double[] getWeights() throws Exception;
  
  void setWeightingDimensions(boolean[] paramArrayOfboolean);
  
  void setWeightingValues(double[] paramArrayOfdouble);
  
  int getNumGeneratingModels();
  
  void setSeed(int paramInt);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\boundaryvisualizer\DataGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */