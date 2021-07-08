package weka.classifiers.functions.pace;

import java.util.Arrays;

public class IntVector implements Cloneable {
  int[] V;
  
  private int sizeOfVector;
  
  public IntVector() {
    this.V = new int[0];
    setSize(0);
  }
  
  public IntVector(int paramInt) {
    this.V = new int[paramInt];
    setSize(paramInt);
  }
  
  public IntVector(int paramInt1, int paramInt2) {
    this(paramInt1);
    set(paramInt2);
  }
  
  public IntVector(int[] paramArrayOfint) {
    if (paramArrayOfint == null) {
      this.V = new int[0];
      setSize(0);
    } else {
      this.V = new int[paramArrayOfint.length];
      setSize(paramArrayOfint.length);
      set(0, size() - 1, paramArrayOfint, 0);
    } 
  }
  
  public int size() {
    return this.sizeOfVector;
  }
  
  public void setSize(int paramInt) {
    if (paramInt > capacity())
      throw new IllegalArgumentException("insufficient capacity"); 
    this.sizeOfVector = paramInt;
  }
  
  public void set(int paramInt) {
    for (byte b = 0; b < size(); b++)
      set(b, paramInt); 
  }
  
  public void set(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    for (int i = paramInt1; i <= paramInt2; i++)
      set(i, paramArrayOfint[paramInt3 + i - paramInt1]); 
  }
  
  public void set(int paramInt1, int paramInt2, IntVector paramIntVector, int paramInt3) {
    for (int i = paramInt1; i <= paramInt2; i++)
      set(i, paramIntVector.get(paramInt3 + i - paramInt1)); 
  }
  
  public void set(IntVector paramIntVector) {
    set(0, paramIntVector.size() - 1, paramIntVector, 0);
  }
  
  public static IntVector seq(int paramInt1, int paramInt2) {
    if (paramInt2 < paramInt1)
      throw new IllegalArgumentException("i1 < i0 "); 
    IntVector intVector = new IntVector(paramInt2 - paramInt1 + 1);
    for (byte b = 0; b < paramInt2 - paramInt1 + 1; b++)
      intVector.set(b, b + paramInt1); 
    return intVector;
  }
  
  protected int[] getArray() {
    return this.V;
  }
  
  protected void setArray(int[] paramArrayOfint) {
    this.V = paramArrayOfint;
  }
  
  public void sort() {
    Arrays.sort(this.V, 0, size());
  }
  
  public int[] getArrayCopy() {
    int[] arrayOfInt = new int[size()];
    for (byte b = 0; b <= size() - 1; b++)
      arrayOfInt[b] = this.V[b]; 
    return arrayOfInt;
  }
  
  public int capacity() {
    return this.V.length;
  }
  
  public void setCapacity(int paramInt) {
    if (paramInt == capacity())
      return; 
    int[] arrayOfInt = this.V;
    int i = Math.min(paramInt, size());
    this.V = new int[paramInt];
    setSize(paramInt);
    set(0, i - 1, arrayOfInt, 0);
  }
  
  public void set(int paramInt1, int paramInt2) {
    this.V[paramInt1] = paramInt2;
  }
  
  public int get(int paramInt) {
    return this.V[paramInt];
  }
  
  public IntVector copy() {
    return (IntVector)clone();
  }
  
  public Object clone() {
    IntVector intVector = new IntVector(size());
    for (byte b = 0; b < size(); b++)
      intVector.V[b] = this.V[b]; 
    return intVector;
  }
  
  public IntVector subvector(int paramInt1, int paramInt2) {
    IntVector intVector = new IntVector(paramInt2 - paramInt1 + 1);
    intVector.set(0, paramInt2 - paramInt1, this, paramInt1);
    return intVector;
  }
  
  public IntVector subvector(IntVector paramIntVector) {
    IntVector intVector = new IntVector(paramIntVector.size());
    for (byte b = 0; b < paramIntVector.size(); b++)
      intVector.V[b] = this.V[paramIntVector.V[b]]; 
    return intVector;
  }
  
  public void swap(int paramInt1, int paramInt2) {
    if (paramInt1 == paramInt2)
      return; 
    int i = get(paramInt1);
    set(paramInt1, get(paramInt2));
    set(paramInt2, i);
  }
  
  public void shift(int paramInt1, int paramInt2) {
    if (paramInt1 == paramInt2)
      return; 
    if (paramInt1 < paramInt2) {
      int i = this.V[paramInt1];
      for (int j = paramInt1; j <= paramInt2 - 1; j++)
        this.V[j] = this.V[j + 1]; 
      this.V[paramInt2] = i;
    } else {
      shift(paramInt2, paramInt1);
    } 
  }
  
  public void shiftToEnd(int paramInt) {
    shift(paramInt, size() - 1);
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public String toString() {
    return toString(5, false);
  }
  
  public String toString(int paramInt, boolean paramBoolean) {
    if (isEmpty())
      return "null vector"; 
    StringBuffer stringBuffer = new StringBuffer();
    FlexibleDecimalFormat flexibleDecimalFormat = new FlexibleDecimalFormat(paramInt, paramBoolean);
    flexibleDecimalFormat.grouping(true);
    int i;
    for (i = 0; i < size(); i++)
      flexibleDecimalFormat.update(get(i)); 
    i = 0;
    byte b1 = 80;
    for (byte b2 = 0; b2 < size(); b2++) {
      String str = flexibleDecimalFormat.format(get(b2));
      i += 1 + str.length();
      if (i > b1 - 1) {
        stringBuffer.append('\n');
        i = 1 + str.length();
      } 
      stringBuffer.append(" " + str);
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    IntVector intVector1 = new IntVector();
    System.out.println(intVector1);
    IntVector intVector2 = seq(10, 25);
    System.out.println(intVector2);
    IntVector intVector3 = seq(25, 10);
    System.out.println(intVector3);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\IntVector.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */