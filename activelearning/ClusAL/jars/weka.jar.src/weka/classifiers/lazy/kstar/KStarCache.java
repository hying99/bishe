package weka.classifiers.lazy.kstar;

import java.io.Serializable;

public class KStarCache implements Serializable {
  CacheTable m_Cache = new CacheTable();
  
  public void store(double paramDouble1, double paramDouble2, double paramDouble3) {
    if (!this.m_Cache.containsKey(paramDouble1))
      this.m_Cache.insert(paramDouble1, paramDouble2, paramDouble3); 
  }
  
  public boolean containsKey(double paramDouble) {
    return this.m_Cache.containsKey(paramDouble);
  }
  
  public TableEntry getCacheValues(double paramDouble) {
    return this.m_Cache.containsKey(paramDouble) ? this.m_Cache.getEntry(paramDouble) : null;
  }
  
  public class TableEntry implements Serializable {
    public int hash;
    
    public double key;
    
    public double value;
    
    public double pmiss;
    
    public TableEntry next;
    
    private final KStarCache this$0;
    
    public TableEntry(KStarCache this$0, int param1Int, double param1Double1, double param1Double2, double param1Double3, TableEntry param1TableEntry) {
      this.this$0 = this$0;
      this.next = null;
      this.hash = param1Int;
      this.key = param1Double1;
      this.value = param1Double2;
      this.pmiss = param1Double3;
      this.next = param1TableEntry;
    }
  }
  
  public class CacheTable implements Serializable {
    private KStarCache.TableEntry[] m_Table;
    
    private int m_Count;
    
    private int m_Threshold;
    
    private float m_LoadFactor;
    
    private final int DEFAULT_TABLE_SIZE = 101;
    
    private final float DEFAULT_LOAD_FACTOR = 0.75F;
    
    private final double EPSILON = 1.0E-5D;
    
    private final KStarCache this$0;
    
    public CacheTable(KStarCache this$0, int param1Int, float param1Float) {
      KStarCache.this = KStarCache.this;
      this.DEFAULT_TABLE_SIZE = 101;
      this.DEFAULT_LOAD_FACTOR = 0.75F;
      this.EPSILON = 1.0E-5D;
      this.m_Table = new KStarCache.TableEntry[param1Int];
      this.m_LoadFactor = param1Float;
      this.m_Threshold = (int)(param1Int * param1Float);
      this.m_Count = 0;
    }
    
    public CacheTable() {
      this(101, 0.75F);
    }
    
    public boolean containsKey(double param1Double) {
      KStarCache.TableEntry[] arrayOfTableEntry = this.m_Table;
      int i = hashCode(param1Double);
      int j = (i & Integer.MAX_VALUE) % arrayOfTableEntry.length;
      for (KStarCache.TableEntry tableEntry = arrayOfTableEntry[j]; tableEntry != null; tableEntry = tableEntry.next) {
        if (tableEntry.hash == i && Math.abs(tableEntry.key - param1Double) < 1.0E-5D)
          return true; 
      } 
      return false;
    }
    
    public void insert(double param1Double1, double param1Double2, double param1Double3) {
      KStarCache.TableEntry[] arrayOfTableEntry = this.m_Table;
      int i = hashCode(param1Double1);
      int j = (i & Integer.MAX_VALUE) % arrayOfTableEntry.length;
      for (KStarCache.TableEntry tableEntry1 = arrayOfTableEntry[j]; tableEntry1 != null; tableEntry1 = tableEntry1.next) {
        if (tableEntry1.hash == i && Math.abs(tableEntry1.key - param1Double1) < 1.0E-5D)
          return; 
      } 
      KStarCache.TableEntry tableEntry2 = new KStarCache.TableEntry(KStarCache.this, i, param1Double1, param1Double2, param1Double3, arrayOfTableEntry[j]);
      arrayOfTableEntry[j] = tableEntry2;
      this.m_Count++;
      if (this.m_Count >= this.m_Threshold)
        rehash(); 
    }
    
    public KStarCache.TableEntry getEntry(double param1Double) {
      KStarCache.TableEntry[] arrayOfTableEntry = this.m_Table;
      int i = hashCode(param1Double);
      int j = (i & Integer.MAX_VALUE) % arrayOfTableEntry.length;
      for (KStarCache.TableEntry tableEntry = arrayOfTableEntry[j]; tableEntry != null; tableEntry = tableEntry.next) {
        if (tableEntry.hash == i && Math.abs(tableEntry.key - param1Double) < 1.0E-5D)
          return tableEntry; 
      } 
      return null;
    }
    
    public int size() {
      return this.m_Count;
    }
    
    public boolean isEmpty() {
      return (this.m_Count == 0);
    }
    
    public void clear() {
      KStarCache.TableEntry[] arrayOfTableEntry = this.m_Table;
      int i = arrayOfTableEntry.length;
      while (--i >= 0)
        arrayOfTableEntry[i] = null; 
      this.m_Count = 0;
    }
    
    private void rehash() {
      int i = this.m_Table.length;
      KStarCache.TableEntry[] arrayOfTableEntry1 = this.m_Table;
      int j = i * 2 + 1;
      KStarCache.TableEntry[] arrayOfTableEntry2 = new KStarCache.TableEntry[j];
      this.m_Threshold = (int)(j * this.m_LoadFactor);
      this.m_Table = arrayOfTableEntry2;
      int k = i;
      while (k-- > 0) {
        KStarCache.TableEntry tableEntry = arrayOfTableEntry1[k];
        while (tableEntry != null) {
          KStarCache.TableEntry tableEntry1 = tableEntry;
          tableEntry = tableEntry.next;
          int m = (tableEntry1.hash & Integer.MAX_VALUE) % j;
          tableEntry1.next = arrayOfTableEntry2[m];
          arrayOfTableEntry2[m] = tableEntry1;
        } 
      } 
    }
    
    private int hashCode(double param1Double) {
      long l = Double.doubleToLongBits(param1Double);
      return (int)(l ^ l >> 32L);
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\lazy\kstar\KStarCache.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */