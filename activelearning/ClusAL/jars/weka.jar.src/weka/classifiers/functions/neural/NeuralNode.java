package weka.classifiers.functions.neural;

import java.util.Random;

public class NeuralNode extends NeuralConnection {
  private double[] m_weights = new double[1];
  
  private double[] m_changeInWeights = new double[1];
  
  private Random m_random;
  
  private NeuralMethod m_methods;
  
  public NeuralNode(String paramString, Random paramRandom, NeuralMethod paramNeuralMethod) {
    super(paramString);
    this.m_random = paramRandom;
    this.m_weights[0] = this.m_random.nextDouble() * 0.1D - 0.05D;
    this.m_changeInWeights[0] = 0.0D;
    this.m_methods = paramNeuralMethod;
  }
  
  public void setMethod(NeuralMethod paramNeuralMethod) {
    this.m_methods = paramNeuralMethod;
  }
  
  public NeuralMethod getMethod() {
    return this.m_methods;
  }
  
  public double outputValue(boolean paramBoolean) {
    if (Double.isNaN(this.m_unitValue) && paramBoolean)
      this.m_unitValue = this.m_methods.outputValue(this); 
    return this.m_unitValue;
  }
  
  public double errorValue(boolean paramBoolean) {
    if (!Double.isNaN(this.m_unitValue) && Double.isNaN(this.m_unitError) && paramBoolean)
      this.m_unitError = this.m_methods.errorValue(this); 
    return this.m_unitError;
  }
  
  public void reset() {
    if (!Double.isNaN(this.m_unitValue) || !Double.isNaN(this.m_unitError)) {
      this.m_unitValue = Double.NaN;
      this.m_unitError = Double.NaN;
      this.m_weightsUpdated = false;
      for (byte b = 0; b < this.m_numInputs; b++)
        this.m_inputList[b].reset(); 
    } 
  }
  
  public double weightValue(int paramInt) {
    return (paramInt >= this.m_numInputs || paramInt < -1) ? Double.NaN : this.m_weights[paramInt + 1];
  }
  
  public double[] getWeights() {
    return this.m_weights;
  }
  
  public double[] getChangeInWeights() {
    return this.m_changeInWeights;
  }
  
  public void updateWeights(double paramDouble1, double paramDouble2) {
    if (!this.m_weightsUpdated && !Double.isNaN(this.m_unitError)) {
      this.m_methods.updateWeights(this, paramDouble1, paramDouble2);
      super.updateWeights(paramDouble1, paramDouble2);
    } 
  }
  
  protected boolean connectInput(NeuralConnection paramNeuralConnection, int paramInt) {
    if (!super.connectInput(paramNeuralConnection, paramInt))
      return false; 
    this.m_weights[this.m_numInputs] = this.m_random.nextDouble() * 0.1D - 0.05D;
    this.m_changeInWeights[this.m_numInputs] = 0.0D;
    return true;
  }
  
  protected void allocateInputs() {
    NeuralConnection[] arrayOfNeuralConnection = new NeuralConnection[this.m_inputList.length + 15];
    int[] arrayOfInt = new int[this.m_inputNums.length + 15];
    double[] arrayOfDouble1 = new double[this.m_weights.length + 15];
    double[] arrayOfDouble2 = new double[this.m_changeInWeights.length + 15];
    arrayOfDouble1[0] = this.m_weights[0];
    arrayOfDouble2[0] = this.m_changeInWeights[0];
    for (byte b = 0; b < this.m_numInputs; b++) {
      arrayOfNeuralConnection[b] = this.m_inputList[b];
      arrayOfInt[b] = this.m_inputNums[b];
      arrayOfDouble1[b + 1] = this.m_weights[b + 1];
      arrayOfDouble2[b + 1] = this.m_changeInWeights[b + 1];
    } 
    this.m_inputList = arrayOfNeuralConnection;
    this.m_inputNums = arrayOfInt;
    this.m_weights = arrayOfDouble1;
    this.m_changeInWeights = arrayOfDouble2;
  }
  
  protected boolean disconnectInput(NeuralConnection paramNeuralConnection, int paramInt) {
    int i = -1;
    boolean bool = false;
    do {
      i = -1;
      int j;
      for (j = 0; j < this.m_numInputs; j++) {
        if (paramNeuralConnection == this.m_inputList[j] && (paramInt == -1 || paramInt == this.m_inputNums[j])) {
          i = j;
          break;
        } 
      } 
      if (i < 0)
        continue; 
      for (j = i + 1; j < this.m_numInputs; j++) {
        this.m_inputList[j - 1] = this.m_inputList[j];
        this.m_inputNums[j - 1] = this.m_inputNums[j];
        this.m_weights[j] = this.m_weights[j + 1];
        this.m_changeInWeights[j] = this.m_changeInWeights[j + 1];
        this.m_inputList[j - 1].changeOutputNum(this.m_inputNums[j - 1], j - 1);
      } 
      this.m_numInputs--;
      bool = true;
    } while (paramInt == -1 && i != -1);
    return bool;
  }
  
  public void removeAllInputs() {
    super.removeAllInputs();
    double d1 = this.m_weights[0];
    double d2 = this.m_changeInWeights[0];
    this.m_weights = new double[1];
    this.m_changeInWeights = new double[1];
    this.m_weights[0] = d1;
    this.m_changeInWeights[0] = d2;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\neural\NeuralNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */