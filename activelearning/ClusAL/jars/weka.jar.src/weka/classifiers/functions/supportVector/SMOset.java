package weka.classifiers.functions.supportVector;

import java.io.Serializable;

public class SMOset implements Serializable {
  private int m_number;
  
  private int m_first;
  
  private boolean[] m_indicators;
  
  private int[] m_next;
  
  private int[] m_previous;
  
  public SMOset(int paramInt) {
    this.m_indicators = new boolean[paramInt];
    this.m_next = new int[paramInt];
    this.m_previous = new int[paramInt];
    this.m_number = 0;
    this.m_first = -1;
  }
  
  public boolean contains(int paramInt) {
    return this.m_indicators[paramInt];
  }
  
  public void delete(int paramInt) {
    if (this.m_indicators[paramInt]) {
      if (this.m_first == paramInt) {
        this.m_first = this.m_next[paramInt];
      } else {
        this.m_next[this.m_previous[paramInt]] = this.m_next[paramInt];
      } 
      if (this.m_next[paramInt] != -1)
        this.m_previous[this.m_next[paramInt]] = this.m_previous[paramInt]; 
      this.m_indicators[paramInt] = false;
      this.m_number--;
    } 
  }
  
  public void insert(int paramInt) {
    if (!this.m_indicators[paramInt]) {
      if (this.m_number == 0) {
        this.m_first = paramInt;
        this.m_next[paramInt] = -1;
        this.m_previous[paramInt] = -1;
      } else {
        this.m_previous[this.m_first] = paramInt;
        this.m_next[paramInt] = this.m_first;
        this.m_previous[paramInt] = -1;
        this.m_first = paramInt;
      } 
      this.m_indicators[paramInt] = true;
      this.m_number++;
    } 
  }
  
  public int getNext(int paramInt) {
    return (paramInt == -1) ? this.m_first : this.m_next[paramInt];
  }
  
  public void printElements() {
    int i;
    for (i = getNext(-1); i != -1; i = getNext(i))
      System.err.print(i + " "); 
    System.err.println();
    for (i = 0; i < this.m_indicators.length; i++) {
      if (this.m_indicators[i])
        System.err.print(i + " "); 
    } 
    System.err.println();
    System.err.println(this.m_number);
  }
  
  public int numElements() {
    return this.m_number;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\supportVector\SMOset.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */