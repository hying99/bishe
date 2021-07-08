package weka.classifiers.functions.neural;

public class LinearUnit implements NeuralMethod {
  public double outputValue(NeuralNode paramNeuralNode) {
    double[] arrayOfDouble = paramNeuralNode.getWeights();
    NeuralConnection[] arrayOfNeuralConnection = paramNeuralNode.getInputs();
    double d = arrayOfDouble[0];
    for (byte b = 0; b < paramNeuralNode.getNumInputs(); b++)
      d += arrayOfNeuralConnection[b].outputValue(true) * arrayOfDouble[b + 1]; 
    return d;
  }
  
  public double errorValue(NeuralNode paramNeuralNode) {
    NeuralConnection[] arrayOfNeuralConnection = paramNeuralNode.getOutputs();
    int[] arrayOfInt = paramNeuralNode.getOutputNums();
    double d = 0.0D;
    for (byte b = 0; b < paramNeuralNode.getNumOutputs(); b++)
      d += arrayOfNeuralConnection[b].errorValue(true) * arrayOfNeuralConnection[b].weightValue(arrayOfInt[b]); 
    return d;
  }
  
  public void updateWeights(NeuralNode paramNeuralNode, double paramDouble1, double paramDouble2) {
    NeuralConnection[] arrayOfNeuralConnection = paramNeuralNode.getInputs();
    double[] arrayOfDouble1 = paramNeuralNode.getChangeInWeights();
    double[] arrayOfDouble2 = paramNeuralNode.getWeights();
    double d1 = 0.0D;
    d1 = paramDouble1 * paramNeuralNode.errorValue(false);
    double d2 = d1 + paramDouble2 * arrayOfDouble1[0];
    arrayOfDouble2[0] = arrayOfDouble2[0] + d2;
    arrayOfDouble1[0] = d2;
    int i = paramNeuralNode.getNumInputs() + 1;
    for (byte b = 1; b < i; b++) {
      d2 = d1 * arrayOfNeuralConnection[b - 1].outputValue(false);
      d2 += paramDouble2 * arrayOfDouble1[b];
      arrayOfDouble2[b] = arrayOfDouble2[b] + d2;
      arrayOfDouble1[b] = d2;
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\neural\LinearUnit.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */