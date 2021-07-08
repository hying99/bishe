package weka.gui.boundaryvisualizer;

import java.io.Serializable;

public class RemoteResult implements Serializable {
  private int m_rowNumber;
  
  private int m_rowLength;
  
  private double[][] m_probabilities;
  
  private int m_percentCompleted;
  
  public RemoteResult(int paramInt1, int paramInt2) {
    this.m_probabilities = new double[paramInt2][0];
  }
  
  public void setLocationProbs(int paramInt, double[] paramArrayOfdouble) {
    this.m_probabilities[paramInt] = paramArrayOfdouble;
  }
  
  public double[][] getProbabilities() {
    return this.m_probabilities;
  }
  
  public void setPercentCompleted(int paramInt) {
    this.m_percentCompleted = paramInt;
  }
  
  public int getPercentCompleted() {
    return this.m_percentCompleted;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\boundaryvisualizer\RemoteResult.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */