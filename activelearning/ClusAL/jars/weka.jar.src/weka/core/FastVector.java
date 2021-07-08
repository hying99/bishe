package weka.core;

import java.io.Serializable;
import java.util.Enumeration;

public class FastVector implements Copyable, Serializable {
  private Object[] m_Objects = new Object[0];
  
  private int m_Size = 0;
  
  private int m_CapacityIncrement = 1;
  
  private int m_CapacityMultiplier = 2;
  
  public FastVector() {}
  
  public FastVector(int paramInt) {}
  
  public final void addElement(Object paramObject) {
    if (this.m_Size == this.m_Objects.length) {
      Object[] arrayOfObject = new Object[this.m_CapacityMultiplier * (this.m_Objects.length + this.m_CapacityIncrement)];
      System.arraycopy(this.m_Objects, 0, arrayOfObject, 0, this.m_Size);
      this.m_Objects = arrayOfObject;
    } 
    this.m_Objects[this.m_Size] = paramObject;
    this.m_Size++;
  }
  
  public final int capacity() {
    return this.m_Objects.length;
  }
  
  public final Object copy() {
    FastVector fastVector = new FastVector(this.m_Objects.length);
    fastVector.m_Size = this.m_Size;
    fastVector.m_CapacityIncrement = this.m_CapacityIncrement;
    fastVector.m_CapacityMultiplier = this.m_CapacityMultiplier;
    System.arraycopy(this.m_Objects, 0, fastVector.m_Objects, 0, this.m_Size);
    return fastVector;
  }
  
  public final Object copyElements() {
    FastVector fastVector = new FastVector(this.m_Objects.length);
    fastVector.m_Size = this.m_Size;
    fastVector.m_CapacityIncrement = this.m_CapacityIncrement;
    fastVector.m_CapacityMultiplier = this.m_CapacityMultiplier;
    for (byte b = 0; b < this.m_Size; b++)
      fastVector.m_Objects[b] = ((Copyable)this.m_Objects[b]).copy(); 
    return fastVector;
  }
  
  public final Object elementAt(int paramInt) {
    return this.m_Objects[paramInt];
  }
  
  public final Enumeration elements() {
    return new FastVectorEnumeration(this, this);
  }
  
  public final Enumeration elements(int paramInt) {
    return new FastVectorEnumeration(this, this, paramInt);
  }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    for (byte b = 0; b < this.m_Objects.length; b++) {
      if (paramObject.equals(this.m_Objects[b]))
        return true; 
    } 
    return false;
  }
  
  public final Object firstElement() {
    return this.m_Objects[0];
  }
  
  public final int indexOf(Object paramObject) {
    for (byte b = 0; b < this.m_Size; b++) {
      if (paramObject.equals(this.m_Objects[b]))
        return b; 
    } 
    return -1;
  }
  
  public final void insertElementAt(Object paramObject, int paramInt) {
    if (this.m_Size < this.m_Objects.length) {
      System.arraycopy(this.m_Objects, paramInt, this.m_Objects, paramInt + 1, this.m_Size - paramInt);
      this.m_Objects[paramInt] = paramObject;
    } else {
      Object[] arrayOfObject = new Object[this.m_CapacityMultiplier * (this.m_Objects.length + this.m_CapacityIncrement)];
      System.arraycopy(this.m_Objects, 0, arrayOfObject, 0, paramInt);
      arrayOfObject[paramInt] = paramObject;
      System.arraycopy(this.m_Objects, paramInt, arrayOfObject, paramInt + 1, this.m_Size - paramInt);
      this.m_Objects = arrayOfObject;
    } 
    this.m_Size++;
  }
  
  public final Object lastElement() {
    return this.m_Objects[this.m_Size - 1];
  }
  
  public final void removeElementAt(int paramInt) {
    System.arraycopy(this.m_Objects, paramInt + 1, this.m_Objects, paramInt, this.m_Size - paramInt - 1);
    this.m_Size--;
  }
  
  public final void removeAllElements() {
    this.m_Objects = new Object[this.m_Objects.length];
    this.m_Size = 0;
  }
  
  public final void appendElements(FastVector paramFastVector) {
    setCapacity(size() + paramFastVector.size());
    System.arraycopy(paramFastVector.m_Objects, 0, this.m_Objects, size(), paramFastVector.size());
    this.m_Size = this.m_Objects.length;
  }
  
  public final Object[] toArray() {
    Object[] arrayOfObject = new Object[size()];
    System.arraycopy(this.m_Objects, 0, arrayOfObject, 0, size());
    return arrayOfObject;
  }
  
  public final void setCapacity(int paramInt) {
    Object[] arrayOfObject = new Object[paramInt];
    System.arraycopy(this.m_Objects, 0, arrayOfObject, 0, Math.min(paramInt, this.m_Size));
    this.m_Objects = arrayOfObject;
    if (this.m_Objects.length < this.m_Size)
      this.m_Size = this.m_Objects.length; 
  }
  
  public final void setElementAt(Object paramObject, int paramInt) {
    this.m_Objects[paramInt] = paramObject;
  }
  
  public final int size() {
    return this.m_Size;
  }
  
  public final void swap(int paramInt1, int paramInt2) {
    Object object = this.m_Objects[paramInt1];
    this.m_Objects[paramInt1] = this.m_Objects[paramInt2];
    this.m_Objects[paramInt2] = object;
  }
  
  public final void trimToSize() {
    Object[] arrayOfObject = new Object[this.m_Size];
    System.arraycopy(this.m_Objects, 0, arrayOfObject, 0, this.m_Size);
    this.m_Objects = arrayOfObject;
  }
  
  public class FastVectorEnumeration implements Enumeration {
    private int m_Counter;
    
    private FastVector m_Vector;
    
    private int m_SpecialElement;
    
    private final FastVector this$0;
    
    public FastVectorEnumeration(FastVector this$0, FastVector param1FastVector1) {
      this.this$0 = this$0;
      this.m_Counter = 0;
      this.m_Vector = param1FastVector1;
      this.m_SpecialElement = -1;
    }
    
    public FastVectorEnumeration(FastVector this$0, FastVector param1FastVector1, int param1Int) {
      this.this$0 = this$0;
      this.m_Vector = param1FastVector1;
      this.m_SpecialElement = param1Int;
      if (param1Int == 0) {
        this.m_Counter = 1;
      } else {
        this.m_Counter = 0;
      } 
    }
    
    public final boolean hasMoreElements() {
      return (this.m_Counter < this.m_Vector.size());
    }
    
    public final Object nextElement() {
      Object object = this.m_Vector.elementAt(this.m_Counter);
      this.m_Counter++;
      if (this.m_Counter == this.m_SpecialElement)
        this.m_Counter++; 
      return object;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\FastVector.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */