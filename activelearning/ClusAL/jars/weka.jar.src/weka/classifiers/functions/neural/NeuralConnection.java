package weka.classifiers.functions.neural;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

public abstract class NeuralConnection implements Serializable {
  public static final int UNCONNECTED = 0;
  
  public static final int PURE_INPUT = 1;
  
  public static final int PURE_OUTPUT = 2;
  
  public static final int INPUT = 4;
  
  public static final int OUTPUT = 8;
  
  public static final int CONNECTED = 16;
  
  protected NeuralConnection[] m_inputList;
  
  protected NeuralConnection[] m_outputList;
  
  protected int[] m_inputNums;
  
  protected int[] m_outputNums;
  
  protected int m_numInputs;
  
  protected int m_numOutputs;
  
  protected double m_unitValue;
  
  protected double m_unitError;
  
  protected boolean m_weightsUpdated;
  
  protected String m_id;
  
  protected int m_type;
  
  protected double m_x;
  
  protected double m_y;
  
  public NeuralConnection(String paramString) {
    this.m_id = paramString;
    this.m_inputList = new NeuralConnection[0];
    this.m_outputList = new NeuralConnection[0];
    this.m_inputNums = new int[0];
    this.m_outputNums = new int[0];
    this.m_numInputs = 0;
    this.m_numOutputs = 0;
    this.m_unitValue = Double.NaN;
    this.m_unitError = Double.NaN;
    this.m_weightsUpdated = false;
    this.m_x = 0.0D;
    this.m_y = 0.0D;
    this.m_type = 0;
  }
  
  public String getId() {
    return this.m_id;
  }
  
  public int getType() {
    return this.m_type;
  }
  
  public void setType(int paramInt) {
    this.m_type = paramInt;
  }
  
  public abstract void reset();
  
  public abstract double outputValue(boolean paramBoolean);
  
  public abstract double errorValue(boolean paramBoolean);
  
  public double weightValue(int paramInt) {
    return 1.0D;
  }
  
  public void updateWeights(double paramDouble1, double paramDouble2) {
    if (!this.m_weightsUpdated) {
      for (byte b = 0; b < this.m_numInputs; b++)
        this.m_inputList[b].updateWeights(paramDouble1, paramDouble2); 
      this.m_weightsUpdated = true;
    } 
  }
  
  public NeuralConnection[] getInputs() {
    return this.m_inputList;
  }
  
  public NeuralConnection[] getOutputs() {
    return this.m_outputList;
  }
  
  public int[] getInputNums() {
    return this.m_inputNums;
  }
  
  public int[] getOutputNums() {
    return this.m_outputNums;
  }
  
  public double getX() {
    return this.m_x;
  }
  
  public double getY() {
    return this.m_y;
  }
  
  public void setX(double paramDouble) {
    this.m_x = paramDouble;
  }
  
  public void setY(double paramDouble) {
    this.m_y = paramDouble;
  }
  
  public boolean onUnit(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = (int)(this.m_x * paramInt3);
    int j = (int)(this.m_y * paramInt4);
    return !(paramInt1 > i + 10 || paramInt1 < i - 10 || paramInt2 > j + 10 || paramInt2 < j - 10);
  }
  
  public void drawNode(Graphics paramGraphics, int paramInt1, int paramInt2) {
    if ((this.m_type & 0x8) == 8) {
      paramGraphics.setColor(Color.orange);
    } else {
      paramGraphics.setColor(Color.red);
    } 
    paramGraphics.fillOval((int)(this.m_x * paramInt1) - 9, (int)(this.m_y * paramInt2) - 9, 19, 19);
    paramGraphics.setColor(Color.gray);
    paramGraphics.fillOval((int)(this.m_x * paramInt1) - 5, (int)(this.m_y * paramInt2) - 5, 11, 11);
  }
  
  public void drawHighlight(Graphics paramGraphics, int paramInt1, int paramInt2) {
    drawNode(paramGraphics, paramInt1, paramInt2);
    paramGraphics.setColor(Color.yellow);
    paramGraphics.fillOval((int)(this.m_x * paramInt1) - 5, (int)(this.m_y * paramInt2) - 5, 11, 11);
  }
  
  public void drawInputLines(Graphics paramGraphics, int paramInt1, int paramInt2) {
    paramGraphics.setColor(Color.black);
    int i = (int)(this.m_x * paramInt1);
    int j = (int)(this.m_y * paramInt2);
    for (byte b = 0; b < this.m_numInputs; b++)
      paramGraphics.drawLine((int)(this.m_inputList[b].getX() * paramInt1), (int)(this.m_inputList[b].getY() * paramInt2), i, j); 
  }
  
  public void drawOutputLines(Graphics paramGraphics, int paramInt1, int paramInt2) {
    paramGraphics.setColor(Color.black);
    int i = (int)(this.m_x * paramInt1);
    int j = (int)(this.m_y * paramInt2);
    for (byte b = 0; b < this.m_numOutputs; b++)
      paramGraphics.drawLine(i, j, (int)(this.m_outputList[b].getX() * paramInt1), (int)(this.m_outputList[b].getY() * paramInt2)); 
  }
  
  protected boolean connectInput(NeuralConnection paramNeuralConnection, int paramInt) {
    for (byte b = 0; b < this.m_numInputs; b++) {
      if (paramNeuralConnection == this.m_inputList[b])
        return false; 
    } 
    if (this.m_numInputs >= this.m_inputList.length)
      allocateInputs(); 
    this.m_inputList[this.m_numInputs] = paramNeuralConnection;
    this.m_inputNums[this.m_numInputs] = paramInt;
    this.m_numInputs++;
    return true;
  }
  
  protected void allocateInputs() {
    NeuralConnection[] arrayOfNeuralConnection = new NeuralConnection[this.m_inputList.length + 15];
    int[] arrayOfInt = new int[this.m_inputNums.length + 15];
    for (byte b = 0; b < this.m_numInputs; b++) {
      arrayOfNeuralConnection[b] = this.m_inputList[b];
      arrayOfInt[b] = this.m_inputNums[b];
    } 
    this.m_inputList = arrayOfNeuralConnection;
    this.m_inputNums = arrayOfInt;
  }
  
  protected boolean connectOutput(NeuralConnection paramNeuralConnection, int paramInt) {
    for (byte b = 0; b < this.m_numOutputs; b++) {
      if (paramNeuralConnection == this.m_outputList[b])
        return false; 
    } 
    if (this.m_numOutputs >= this.m_outputList.length)
      allocateOutputs(); 
    this.m_outputList[this.m_numOutputs] = paramNeuralConnection;
    this.m_outputNums[this.m_numOutputs] = paramInt;
    this.m_numOutputs++;
    return true;
  }
  
  protected void allocateOutputs() {
    NeuralConnection[] arrayOfNeuralConnection = new NeuralConnection[this.m_outputList.length + 15];
    int[] arrayOfInt = new int[this.m_outputNums.length + 15];
    for (byte b = 0; b < this.m_numOutputs; b++) {
      arrayOfNeuralConnection[b] = this.m_outputList[b];
      arrayOfInt[b] = this.m_outputNums[b];
    } 
    this.m_outputList = arrayOfNeuralConnection;
    this.m_outputNums = arrayOfInt;
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
        this.m_inputList[j - 1].changeOutputNum(this.m_inputNums[j - 1], j - 1);
      } 
      this.m_numInputs--;
      bool = true;
    } while (paramInt == -1 && i != -1);
    return bool;
  }
  
  public void removeAllInputs() {
    for (byte b = 0; b < this.m_numInputs; b++)
      this.m_inputList[b].disconnectOutput(this, -1); 
    this.m_inputList = new NeuralConnection[0];
    setType(getType() & 0xFFFFFFFB);
    if (getNumOutputs() == 0)
      setType(getType() & 0xFFFFFFEF); 
    this.m_inputNums = new int[0];
    this.m_numInputs = 0;
  }
  
  protected void changeInputNum(int paramInt1, int paramInt2) {
    if (paramInt1 >= this.m_numInputs || paramInt1 < 0)
      return; 
    this.m_inputNums[paramInt1] = paramInt2;
  }
  
  protected boolean disconnectOutput(NeuralConnection paramNeuralConnection, int paramInt) {
    int i = -1;
    boolean bool = false;
    do {
      i = -1;
      int j;
      for (j = 0; j < this.m_numOutputs; j++) {
        if (paramNeuralConnection == this.m_outputList[j] && (paramInt == -1 || paramInt == this.m_outputNums[j])) {
          i = j;
          break;
        } 
      } 
      if (i < 0)
        continue; 
      for (j = i + 1; j < this.m_numOutputs; j++) {
        this.m_outputList[j - 1] = this.m_outputList[j];
        this.m_outputNums[j - 1] = this.m_outputNums[j];
        this.m_outputList[j - 1].changeInputNum(this.m_outputNums[j - 1], j - 1);
      } 
      this.m_numOutputs--;
      bool = true;
    } while (paramInt == -1 && i != -1);
    return bool;
  }
  
  public void removeAllOutputs() {
    for (byte b = 0; b < this.m_numOutputs; b++)
      this.m_outputList[b].disconnectInput(this, -1); 
    this.m_outputList = new NeuralConnection[0];
    this.m_outputNums = new int[0];
    setType(getType() & 0xFFFFFFF7);
    if (getNumInputs() == 0)
      setType(getType() & 0xFFFFFFEF); 
    this.m_numOutputs = 0;
  }
  
  protected void changeOutputNum(int paramInt1, int paramInt2) {
    if (paramInt1 >= this.m_numOutputs || paramInt1 < 0)
      return; 
    this.m_outputNums[paramInt1] = paramInt2;
  }
  
  public int getNumInputs() {
    return this.m_numInputs;
  }
  
  public int getNumOutputs() {
    return this.m_numOutputs;
  }
  
  public static boolean connect(NeuralConnection paramNeuralConnection1, NeuralConnection paramNeuralConnection2) {
    if (paramNeuralConnection1 == null || paramNeuralConnection2 == null)
      return false; 
    disconnect(paramNeuralConnection1, paramNeuralConnection2);
    if (paramNeuralConnection1 == paramNeuralConnection2)
      return false; 
    if ((paramNeuralConnection2.getType() & 0x1) == 1)
      return false; 
    if ((paramNeuralConnection1.getType() & 0x2) == 2)
      return false; 
    if ((paramNeuralConnection1.getType() & 0x1) == 1 && (paramNeuralConnection2.getType() & 0x2) == 2)
      return false; 
    if ((paramNeuralConnection2.getType() & 0x2) == 2 && paramNeuralConnection2.getNumInputs() > 0)
      return false; 
    if ((paramNeuralConnection2.getType() & 0x2) == 2 && (paramNeuralConnection1.getType() & 0x8) == 8)
      return false; 
    if (!paramNeuralConnection1.connectOutput(paramNeuralConnection2, paramNeuralConnection2.getNumInputs()))
      return false; 
    if (!paramNeuralConnection2.connectInput(paramNeuralConnection1, paramNeuralConnection1.getNumOutputs() - 1)) {
      paramNeuralConnection1.disconnectOutput(paramNeuralConnection2, paramNeuralConnection2.getNumInputs());
      return false;
    } 
    if ((paramNeuralConnection1.getType() & 0x1) == 1) {
      paramNeuralConnection2.setType(paramNeuralConnection2.getType() | 0x4);
    } else if ((paramNeuralConnection2.getType() & 0x2) == 2) {
      paramNeuralConnection1.setType(paramNeuralConnection1.getType() | 0x8);
    } 
    paramNeuralConnection2.setType(paramNeuralConnection2.getType() | 0x10);
    paramNeuralConnection1.setType(paramNeuralConnection1.getType() | 0x10);
    return true;
  }
  
  public static boolean disconnect(NeuralConnection paramNeuralConnection1, NeuralConnection paramNeuralConnection2) {
    if (paramNeuralConnection1 == null || paramNeuralConnection2 == null)
      return false; 
    boolean bool1 = paramNeuralConnection1.disconnectOutput(paramNeuralConnection2, -1);
    boolean bool2 = paramNeuralConnection2.disconnectInput(paramNeuralConnection1, -1);
    if (bool1 && bool2) {
      if ((paramNeuralConnection1.getType() & 0x1) == 1) {
        paramNeuralConnection2.setType(paramNeuralConnection2.getType() & 0xFFFFFFFB);
      } else if ((paramNeuralConnection2.getType() & 0x2) == 2) {
        paramNeuralConnection1.setType(paramNeuralConnection1.getType() & 0xFFFFFFF7);
      } 
      if (paramNeuralConnection1.getNumInputs() == 0 && paramNeuralConnection1.getNumOutputs() == 0)
        paramNeuralConnection1.setType(paramNeuralConnection1.getType() & 0xFFFFFFEF); 
      if (paramNeuralConnection2.getNumInputs() == 0 && paramNeuralConnection2.getNumOutputs() == 0)
        paramNeuralConnection2.setType(paramNeuralConnection2.getType() & 0xFFFFFFEF); 
    } 
    return (bool1 && bool2);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\neural\NeuralConnection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */