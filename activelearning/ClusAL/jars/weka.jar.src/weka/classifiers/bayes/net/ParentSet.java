package weka.classifiers.bayes.net;

import java.io.Serializable;
import weka.core.Instances;

public class ParentSet implements Serializable {
  private int[] m_nParents;
  
  private int m_nNrOfParents = 0;
  
  private int m_nCardinalityOfParents = 1;
  
  public int getParent(int paramInt) {
    return this.m_nParents[paramInt];
  }
  
  public void SetParent(int paramInt1, int paramInt2) {
    this.m_nParents[paramInt1] = paramInt2;
  }
  
  public int getNrOfParents() {
    return this.m_nNrOfParents;
  }
  
  public boolean contains(int paramInt) {
    for (byte b = 0; b < this.m_nNrOfParents; b++) {
      if (this.m_nParents[b] == paramInt)
        return true; 
    } 
    return false;
  }
  
  public int getCardinalityOfParents() {
    return this.m_nCardinalityOfParents;
  }
  
  public ParentSet() {
    this.m_nParents = new int[10];
    this.m_nNrOfParents = 0;
    this.m_nCardinalityOfParents = 1;
  }
  
  public ParentSet(int paramInt) {
    this.m_nParents = new int[paramInt];
    this.m_nNrOfParents = 0;
    this.m_nCardinalityOfParents = 1;
  }
  
  public ParentSet(ParentSet paramParentSet) {
    this.m_nNrOfParents = paramParentSet.m_nNrOfParents;
    this.m_nCardinalityOfParents = paramParentSet.m_nCardinalityOfParents;
    this.m_nParents = new int[this.m_nNrOfParents];
    for (byte b = 0; b < this.m_nNrOfParents; b++)
      this.m_nParents[b] = paramParentSet.m_nParents[b]; 
  }
  
  public void maxParentSetSize(int paramInt) {
    this.m_nParents = new int[paramInt];
  }
  
  public void addParent(int paramInt, Instances paramInstances) {
    if (this.m_nNrOfParents == 10) {
      int[] arrayOfInt = new int[50];
      for (byte b = 0; b < this.m_nNrOfParents; b++)
        arrayOfInt[b] = this.m_nParents[b]; 
      this.m_nParents = arrayOfInt;
    } 
    this.m_nParents[this.m_nNrOfParents] = paramInt;
    this.m_nNrOfParents++;
    this.m_nCardinalityOfParents *= paramInstances.attribute(paramInt).numValues();
  }
  
  public void addParent(int paramInt1, int paramInt2, Instances paramInstances) {
    if (this.m_nNrOfParents == 10) {
      int[] arrayOfInt = new int[50];
      for (byte b = 0; b < this.m_nNrOfParents; b++)
        arrayOfInt[b] = this.m_nParents[b]; 
      this.m_nParents = arrayOfInt;
    } 
    for (int i = this.m_nNrOfParents; i > paramInt2; i--)
      this.m_nParents[i] = this.m_nParents[i - 1]; 
    this.m_nParents[paramInt2] = paramInt1;
    this.m_nNrOfParents++;
    this.m_nCardinalityOfParents *= paramInstances.attribute(paramInt1).numValues();
  }
  
  public int deleteParent(int paramInt, Instances paramInstances) {
    byte b;
    for (b = 0; this.m_nParents[b] != paramInt && b < this.m_nNrOfParents; b++);
    byte b1 = -1;
    if (b < this.m_nNrOfParents)
      b1 = b; 
    if (b < this.m_nNrOfParents) {
      while (b < this.m_nNrOfParents - 1) {
        this.m_nParents[b] = this.m_nParents[b + 1];
        b++;
      } 
      this.m_nNrOfParents--;
      this.m_nCardinalityOfParents /= paramInstances.attribute(paramInt).numValues();
    } 
    return b1;
  }
  
  public void deleteLastParent(Instances paramInstances) {
    this.m_nNrOfParents--;
    this.m_nCardinalityOfParents /= paramInstances.attribute(this.m_nParents[this.m_nNrOfParents]).numValues();
  }
  
  public void copy(ParentSet paramParentSet) {
    this.m_nCardinalityOfParents = paramParentSet.m_nCardinalityOfParents;
    this.m_nNrOfParents = paramParentSet.m_nNrOfParents;
    for (byte b = 0; b < this.m_nNrOfParents; b++)
      this.m_nParents[b] = paramParentSet.m_nParents[b]; 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\ParentSet.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */