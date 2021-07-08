package weka.classifiers.functions.pace;

import java.lang.reflect.Method;
import java.util.Arrays;

public class DoubleVector implements Cloneable {
  double[] V;
  
  private int sizeOfVector;
  
  public DoubleVector() {
    this(0);
  }
  
  public DoubleVector(int paramInt) {
    this.V = new double[paramInt];
    setSize(paramInt);
  }
  
  public DoubleVector(int paramInt, double paramDouble) {
    this(paramInt);
    set(paramDouble);
  }
  
  public DoubleVector(double[] paramArrayOfdouble) {
    if (paramArrayOfdouble == null) {
      this.V = new double[0];
      setSize(0);
    } else {
      this.V = paramArrayOfdouble;
      setSize(paramArrayOfdouble.length);
    } 
  }
  
  public void set(int paramInt, double paramDouble) {
    this.V[paramInt] = paramDouble;
  }
  
  public void set(double paramDouble) {
    set(0, size() - 1, paramDouble);
  }
  
  public void set(int paramInt1, int paramInt2, double paramDouble) {
    for (int i = paramInt1; i <= paramInt2; i++)
      this.V[i] = paramDouble; 
  }
  
  public void set(int paramInt1, int paramInt2, double[] paramArrayOfdouble, int paramInt3) {
    for (int i = paramInt1; i <= paramInt2; i++)
      this.V[i] = paramArrayOfdouble[paramInt3 + i - paramInt1]; 
  }
  
  public void set(DoubleVector paramDoubleVector) {
    set(0, paramDoubleVector.size() - 1, paramDoubleVector, 0);
  }
  
  public void set(int paramInt1, int paramInt2, DoubleVector paramDoubleVector, int paramInt3) {
    for (int i = paramInt1; i <= paramInt2; i++)
      this.V[i] = paramDoubleVector.V[paramInt3 + i - paramInt1]; 
  }
  
  double[] getArray() {
    return this.V;
  }
  
  void setArray(double[] paramArrayOfdouble) {
    this.V = paramArrayOfdouble;
  }
  
  public double[] getArrayCopy() {
    double[] arrayOfDouble = new double[size()];
    for (byte b = 0; b < size(); b++)
      arrayOfDouble[b] = this.V[b]; 
    return arrayOfDouble;
  }
  
  public void sort() {
    Arrays.sort(this.V, 0, size());
  }
  
  public IntVector sortWithIndex() {
    IntVector intVector = IntVector.seq(0, size() - 1);
    sortWithIndex(0, size() - 1, intVector);
    return intVector;
  }
  
  public void sortWithIndex(int paramInt1, int paramInt2, IntVector paramIntVector) {
    if (paramInt1 < paramInt2) {
      int i = (paramInt1 + paramInt2) / 2;
      double d = Math.min(this.V[paramInt1], Math.max(this.V[i], this.V[paramInt2]));
      int j = paramInt1;
      int k = paramInt2;
      while (j < k) {
        while (this.V[j] < d && j < paramInt2)
          j++; 
        while (this.V[k] > d && k > paramInt1)
          k--; 
        if (j <= k) {
          swap(j, k);
          paramIntVector.swap(j, k);
          j++;
          k--;
        } 
      } 
      sortWithIndex(paramInt1, k, paramIntVector);
      sortWithIndex(j, paramInt2, paramIntVector);
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
  
  public int capacity() {
    return (this.V == null) ? 0 : this.V.length;
  }
  
  public void setCapacity(int paramInt) {
    if (paramInt == capacity())
      return; 
    double[] arrayOfDouble = this.V;
    int i = Math.min(paramInt, size());
    this.V = new double[paramInt];
    setSize(i);
    set(0, i - 1, arrayOfDouble, 0);
  }
  
  public double get(int paramInt) {
    return this.V[paramInt];
  }
  
  public void setPlus(int paramInt, double paramDouble) {
    this.V[paramInt] = this.V[paramInt] + paramDouble;
  }
  
  public void setTimes(int paramInt, double paramDouble) {
    this.V[paramInt] = this.V[paramInt] * paramDouble;
  }
  
  public void addElement(double paramDouble) {
    if (capacity() == 0)
      setCapacity(10); 
    if (size() == capacity())
      setCapacity(2 * capacity()); 
    this.V[size()] = paramDouble;
    setSize(size() + 1);
  }
  
  public DoubleVector square() {
    DoubleVector doubleVector = new DoubleVector(size());
    for (byte b = 0; b < size(); b++)
      doubleVector.V[b] = this.V[b] * this.V[b]; 
    return doubleVector;
  }
  
  public DoubleVector sqrt() {
    DoubleVector doubleVector = new DoubleVector(size());
    for (byte b = 0; b < size(); b++)
      doubleVector.V[b] = Math.sqrt(this.V[b]); 
    return doubleVector;
  }
  
  public DoubleVector copy() {
    return (DoubleVector)clone();
  }
  
  public Object clone() {
    int i = size();
    DoubleVector doubleVector = new DoubleVector(i);
    for (byte b = 0; b < i; b++)
      doubleVector.V[b] = this.V[b]; 
    return doubleVector;
  }
  
  public double innerProduct(DoubleVector paramDoubleVector) {
    if (size() != paramDoubleVector.size())
      throw new IllegalArgumentException("sizes unmatch"); 
    double d = 0.0D;
    for (byte b = 0; b < size(); b++)
      d += this.V[b] * paramDoubleVector.V[b]; 
    return d;
  }
  
  public DoubleVector sign() {
    DoubleVector doubleVector = new DoubleVector(size());
    for (byte b = 0; b < size(); b++) {
      if (this.V[b] > 0.0D) {
        doubleVector.V[b] = 1.0D;
      } else if (this.V[b] < 0.0D) {
        doubleVector.V[b] = -1.0D;
      } else {
        doubleVector.V[b] = 0.0D;
      } 
    } 
    return doubleVector;
  }
  
  public double sum() {
    double d = 0.0D;
    for (byte b = 0; b < size(); b++)
      d += this.V[b]; 
    return d;
  }
  
  public double sum2() {
    double d = 0.0D;
    for (byte b = 0; b < size(); b++)
      d += this.V[b] * this.V[b]; 
    return d;
  }
  
  public double norm1() {
    double d = 0.0D;
    for (byte b = 0; b < size(); b++)
      d += Math.abs(this.V[b]); 
    return d;
  }
  
  public double norm2() {
    return Math.sqrt(sum2());
  }
  
  public double sum2(DoubleVector paramDoubleVector) {
    return minus(paramDoubleVector).sum2();
  }
  
  public DoubleVector subvector(int paramInt1, int paramInt2) {
    DoubleVector doubleVector = new DoubleVector(paramInt2 - paramInt1 + 1);
    doubleVector.set(0, paramInt2 - paramInt1, this, paramInt1);
    return doubleVector;
  }
  
  public DoubleVector subvector(IntVector paramIntVector) {
    DoubleVector doubleVector = new DoubleVector(paramIntVector.size());
    for (byte b = 0; b < paramIntVector.size(); b++)
      doubleVector.V[b] = this.V[paramIntVector.V[b]]; 
    return doubleVector;
  }
  
  public DoubleVector unpivoting(IntVector paramIntVector, int paramInt) {
    if (paramIntVector.size() > paramInt)
      throw new IllegalArgumentException("index.size() > length "); 
    DoubleVector doubleVector = new DoubleVector(paramInt);
    for (byte b = 0; b < paramIntVector.size(); b++)
      doubleVector.V[paramIntVector.V[b]] = this.V[b]; 
    return doubleVector;
  }
  
  public DoubleVector plus(double paramDouble) {
    return copy().plusEquals(paramDouble);
  }
  
  public DoubleVector plusEquals(double paramDouble) {
    for (byte b = 0; b < size(); b++)
      this.V[b] = this.V[b] + paramDouble; 
    return this;
  }
  
  public DoubleVector plus(DoubleVector paramDoubleVector) {
    return copy().plusEquals(paramDoubleVector);
  }
  
  public DoubleVector plusEquals(DoubleVector paramDoubleVector) {
    for (byte b = 0; b < size(); b++)
      this.V[b] = this.V[b] + paramDoubleVector.V[b]; 
    return this;
  }
  
  public DoubleVector minus(double paramDouble) {
    return plus(-paramDouble);
  }
  
  public DoubleVector minusEquals(double paramDouble) {
    plusEquals(-paramDouble);
    return this;
  }
  
  public DoubleVector minus(DoubleVector paramDoubleVector) {
    return copy().minusEquals(paramDoubleVector);
  }
  
  public DoubleVector minusEquals(DoubleVector paramDoubleVector) {
    for (byte b = 0; b < size(); b++)
      this.V[b] = this.V[b] - paramDoubleVector.V[b]; 
    return this;
  }
  
  public DoubleVector times(double paramDouble) {
    return copy().timesEquals(paramDouble);
  }
  
  public DoubleVector timesEquals(double paramDouble) {
    for (byte b = 0; b < size(); b++)
      this.V[b] = this.V[b] * paramDouble; 
    return this;
  }
  
  public DoubleVector times(DoubleVector paramDoubleVector) {
    return copy().timesEquals(paramDoubleVector);
  }
  
  public DoubleVector timesEquals(DoubleVector paramDoubleVector) {
    for (byte b = 0; b < size(); b++)
      this.V[b] = this.V[b] * paramDoubleVector.V[b]; 
    return this;
  }
  
  public DoubleVector dividedBy(DoubleVector paramDoubleVector) {
    return copy().dividedByEquals(paramDoubleVector);
  }
  
  public DoubleVector dividedByEquals(DoubleVector paramDoubleVector) {
    for (byte b = 0; b < size(); b++)
      this.V[b] = this.V[b] / paramDoubleVector.V[b]; 
    return this;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public DoubleVector cumulate() {
    return copy().cumulateInPlace();
  }
  
  public DoubleVector cumulateInPlace() {
    for (byte b = 1; b < size(); b++)
      this.V[b] = this.V[b] + this.V[b - 1]; 
    return this;
  }
  
  public int indexOfMax() {
    byte b1 = 0;
    double d = this.V[0];
    for (byte b2 = 1; b2 < size(); b2++) {
      if (d < this.V[b2]) {
        d = this.V[b2];
        b1 = b2;
      } 
    } 
    return b1;
  }
  
  public boolean unsorted() {
    if (size() < 2)
      return false; 
    for (byte b = 1; b < size(); b++) {
      if (this.V[b - 1] > this.V[b])
        return true; 
    } 
    return false;
  }
  
  public DoubleVector cat(DoubleVector paramDoubleVector) {
    DoubleVector doubleVector = new DoubleVector(size() + paramDoubleVector.size());
    doubleVector.set(0, size() - 1, this, 0);
    doubleVector.set(size(), size() + paramDoubleVector.size() - 1, paramDoubleVector, 0);
    return doubleVector;
  }
  
  public void swap(int paramInt1, int paramInt2) {
    if (paramInt1 == paramInt2)
      return; 
    double d = this.V[paramInt1];
    this.V[paramInt1] = this.V[paramInt2];
    this.V[paramInt2] = d;
  }
  
  public double max() {
    if (size() < 1)
      throw new IllegalArgumentException("zero size"); 
    double d = this.V[0];
    if (size() < 2)
      return d; 
    for (byte b = 1; b < size(); b++) {
      if (this.V[b] > d)
        d = this.V[b]; 
    } 
    return d;
  }
  
  public DoubleVector map(String paramString1, String paramString2) {
    try {
      Class clazz = Class.forName(paramString1);
      Class[] arrayOfClass = new Class[1];
      arrayOfClass[0] = double.class;
      Method method = clazz.getMethod(paramString2, arrayOfClass);
      DoubleVector doubleVector = new DoubleVector(size());
      Object[] arrayOfObject = new Object[1];
      for (byte b = 0; b < size(); b++) {
        arrayOfObject[0] = new Double(this.V[b]);
        doubleVector.set(b, Double.parseDouble(method.invoke(null, arrayOfObject).toString()));
      } 
      return doubleVector;
    } catch (Exception exception) {
      exception.printStackTrace();
      System.exit(1);
      return null;
    } 
  }
  
  public DoubleVector rev() {
    int i = size();
    DoubleVector doubleVector = new DoubleVector(i);
    for (byte b = 0; b < i; b++)
      doubleVector.V[b] = this.V[i - b - 1]; 
    return doubleVector;
  }
  
  public static DoubleVector random(int paramInt) {
    DoubleVector doubleVector = new DoubleVector(paramInt);
    for (byte b = 0; b < paramInt; b++)
      doubleVector.V[b] = Math.random(); 
    return doubleVector;
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
      flexibleDecimalFormat.update(this.V[i]); 
    i = 0;
    byte b1 = 80;
    for (byte b2 = 0; b2 < size(); b2++) {
      String str = flexibleDecimalFormat.format(this.V[b2]);
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
    DoubleVector doubleVector1 = random(10);
    DoubleVector doubleVector2 = random(10);
    DoubleVector doubleVector3 = random(10);
    DoubleVector doubleVector4 = doubleVector3;
    System.out.println(random(10).plus(doubleVector2).plus(doubleVector4));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\DoubleVector.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */