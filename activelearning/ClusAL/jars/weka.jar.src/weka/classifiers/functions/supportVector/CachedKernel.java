package weka.classifiers.functions.supportVector;

import weka.core.Instance;
import weka.core.Instances;

public abstract class CachedKernel extends Kernel {
  private int m_kernelEvals = 0;
  
  private int m_cacheHits = 0;
  
  private int m_cacheSize;
  
  private double[] m_storage;
  
  private long[] m_keys;
  
  private double[][] m_kernelMatrix;
  
  private int m_numInsts;
  
  private int m_cacheSlots = 4;
  
  protected CachedKernel(Instances paramInstances, int paramInt) {
    this.m_data = paramInstances;
    this.m_cacheSize = paramInt;
    if (paramInt > 0) {
      this.m_storage = new double[this.m_cacheSize * this.m_cacheSlots];
      this.m_keys = new long[this.m_cacheSize * this.m_cacheSlots];
    } 
    this.m_numInsts = this.m_data.numInstances();
  }
  
  protected abstract double evaluate(int paramInt1, int paramInt2, Instance paramInstance) throws Exception;
  
  public double eval(int paramInt1, int paramInt2, Instance paramInstance) throws Exception {
    double d = 0.0D;
    long l = -1L;
    int i = -1;
    if (paramInt1 >= 0) {
      if (this.m_cacheSize == 0) {
        if (this.m_kernelMatrix == null) {
          this.m_kernelMatrix = new double[this.m_data.numInstances()][];
          for (byte b1 = 0; b1 < this.m_data.numInstances(); b1++) {
            this.m_kernelMatrix[b1] = new double[b1 + 1];
            for (byte b2 = 0; b2 <= b1; b2++) {
              this.m_kernelEvals++;
              this.m_kernelMatrix[b1][b2] = evaluate(b1, b2, this.m_data.instance(b1));
            } 
          } 
        } 
        this.m_cacheHits++;
        return (paramInt1 > paramInt2) ? this.m_kernelMatrix[paramInt1][paramInt2] : this.m_kernelMatrix[paramInt2][paramInt1];
      } 
      if (paramInt1 > paramInt2) {
        l = paramInt1 + paramInt2 * this.m_numInsts;
      } else {
        l = paramInt2 + paramInt1 * this.m_numInsts;
      } 
      i = (int)(l % this.m_cacheSize) * this.m_cacheSlots;
      int j = i;
      for (byte b = 0; b < this.m_cacheSlots; b++) {
        long l1 = this.m_keys[j];
        if (l1 == 0L)
          break; 
        if (l1 == l + 1L) {
          this.m_cacheHits++;
          if (b > 0) {
            double d1 = this.m_storage[j];
            this.m_storage[j] = this.m_storage[i];
            this.m_keys[j] = this.m_keys[i];
            this.m_storage[i] = d1;
            this.m_keys[i] = l1;
            return d1;
          } 
          return this.m_storage[j];
        } 
        j++;
      } 
    } 
    d = evaluate(paramInt1, paramInt2, paramInstance);
    this.m_kernelEvals++;
    if (l != -1L) {
      System.arraycopy(this.m_keys, i, this.m_keys, i + 1, this.m_cacheSlots - 1);
      System.arraycopy(this.m_storage, i, this.m_storage, i + 1, this.m_cacheSlots - 1);
      this.m_storage[i] = d;
      this.m_keys[i] = l + 1L;
    } 
    return d;
  }
  
  public int numEvals() {
    return this.m_kernelEvals;
  }
  
  public int numCacheHits() {
    return this.m_cacheHits;
  }
  
  public void clean() {
    this.m_storage = null;
    this.m_keys = null;
    this.m_kernelMatrix = (double[][])null;
  }
  
  protected final double dotProd(Instance paramInstance1, Instance paramInstance2) throws Exception {
    double d = 0.0D;
    int i = paramInstance1.numValues();
    int j = paramInstance2.numValues();
    int k = this.m_data.classIndex();
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < i && b2 < j) {
      int m = paramInstance1.index(b1);
      int n = paramInstance2.index(b2);
      if (m == n) {
        if (m != k)
          d += paramInstance1.valueSparse(b1) * paramInstance2.valueSparse(b2); 
        b1++;
        b2++;
        continue;
      } 
      if (m > n) {
        b2++;
        continue;
      } 
      b1++;
    } 
    return d;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\supportVector\CachedKernel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */