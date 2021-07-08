package weka.classifiers.functions.neural;

import java.io.Serializable;

public interface NeuralMethod extends Serializable {
  double outputValue(NeuralNode paramNeuralNode);
  
  double errorValue(NeuralNode paramNeuralNode);
  
  void updateWeights(NeuralNode paramNeuralNode, double paramDouble1, double paramDouble2);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\neural\NeuralMethod.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */