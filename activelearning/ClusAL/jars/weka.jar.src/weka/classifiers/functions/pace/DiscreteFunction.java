package weka.classifiers.functions.pace;

public class DiscreteFunction {
  protected DoubleVector points;
  
  protected DoubleVector values;
  
  public DiscreteFunction() {
    this(null, null);
  }
  
  public DiscreteFunction(DoubleVector paramDoubleVector) {
    this(paramDoubleVector, null);
  }
  
  public DiscreteFunction(DoubleVector paramDoubleVector1, DoubleVector paramDoubleVector2) {
    this.points = paramDoubleVector1;
    this.values = paramDoubleVector2;
    formalize();
  }
  
  private DiscreteFunction formalize() {
    if (this.points == null)
      this.points = new DoubleVector(); 
    if (this.values == null)
      this.values = new DoubleVector(); 
    if (this.points.isEmpty()) {
      if (!this.values.isEmpty())
        throw new IllegalArgumentException("sizes not match"); 
    } else {
      int i = this.points.size();
      if (this.values.isEmpty()) {
        this.values = new DoubleVector(i, 1.0D / i);
      } else if (this.values.size() != i) {
        throw new IllegalArgumentException("sizes not match");
      } 
    } 
    return this;
  }
  
  public DiscreteFunction normalize() {
    if (!this.values.isEmpty()) {
      double d = this.values.sum();
      if (d != 0.0D && d != 1.0D)
        this.values.timesEquals(1.0D / d); 
    } 
    return this;
  }
  
  public void sort() {
    IntVector intVector = this.points.sortWithIndex();
    this.values = this.values.subvector(intVector);
  }
  
  public Object clone() {
    DiscreteFunction discreteFunction = new DiscreteFunction();
    discreteFunction.points = (DoubleVector)this.points.clone();
    discreteFunction.values = (DoubleVector)this.values.clone();
    return discreteFunction;
  }
  
  public DiscreteFunction unique() {
    byte b1 = 0;
    if (size() < 2)
      return this; 
    for (byte b2 = 1; b2 <= size() - 1; b2++) {
      if (this.points.get(b1) != this.points.get(b2)) {
        this.points.set(++b1, this.points.get(b2));
        this.values.set(b1, this.values.get(b2));
      } else {
        this.values.set(b1, this.values.get(b1) + this.values.get(b2));
      } 
    } 
    this.points = this.points.subvector(0, b1);
    this.values = this.values.subvector(0, b1);
    return this;
  }
  
  public int size() {
    return (this.points == null) ? 0 : this.points.size();
  }
  
  public double getPointValue(int paramInt) {
    return this.points.get(paramInt);
  }
  
  public double getFunctionValue(int paramInt) {
    return this.values.get(paramInt);
  }
  
  public void setPointValue(int paramInt, double paramDouble) {
    this.points.set(paramInt, paramDouble);
  }
  
  public void setFunctionValue(int paramInt, double paramDouble) {
    this.values.set(paramInt, paramDouble);
  }
  
  protected DoubleVector getPointValues() {
    return this.points;
  }
  
  protected DoubleVector getFunctionValues() {
    return this.values;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public DiscreteFunction plus(DiscreteFunction paramDiscreteFunction) {
    return ((DiscreteFunction)clone()).plusEquals(paramDiscreteFunction);
  }
  
  public DiscreteFunction plusEquals(DiscreteFunction paramDiscreteFunction) {
    this.points = this.points.cat(paramDiscreteFunction.points);
    this.values = this.values.cat(paramDiscreteFunction.values);
    return this;
  }
  
  public DiscreteFunction timesEquals(double paramDouble) {
    this.values.timesEquals(paramDouble);
    return this;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    FlexibleDecimalFormat flexibleDecimalFormat1 = new FlexibleDecimalFormat(5);
    flexibleDecimalFormat1.grouping(true);
    FlexibleDecimalFormat flexibleDecimalFormat2 = new FlexibleDecimalFormat(5);
    flexibleDecimalFormat2.grouping(true);
    byte b;
    for (b = 0; b < size(); b++) {
      flexibleDecimalFormat1.update(this.points.get(b));
      flexibleDecimalFormat2.update(this.values.get(b));
    } 
    stringBuffer.append("\t" + flexibleDecimalFormat1.formatString("Points") + "\t" + flexibleDecimalFormat2.formatString("Values") + "\n\n");
    for (b = 0; b <= size() - 1; b++)
      stringBuffer.append("\t" + flexibleDecimalFormat1.format(this.points.get(b)) + "\t" + flexibleDecimalFormat2.format(this.values.get(b)) + "\n"); 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    double[] arrayOfDouble1 = { 2.0D, 1.0D, 2.0D, 3.0D, 3.0D };
    double[] arrayOfDouble2 = { 3.0D, 2.0D, 4.0D, 1.0D, 3.0D };
    DiscreteFunction discreteFunction = new DiscreteFunction(new DoubleVector(arrayOfDouble1), new DoubleVector(arrayOfDouble2));
    System.out.println(discreteFunction);
    discreteFunction.normalize();
    System.out.println("d (after normalize) = \n" + discreteFunction);
    arrayOfDouble1[1] = 10.0D;
    System.out.println("d (after setting [1]) = \n" + discreteFunction);
    discreteFunction.sort();
    System.out.println("d (after sorting) = \n" + discreteFunction);
    discreteFunction.unique();
    System.out.println("d (after unique) = \n" + discreteFunction);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\DiscreteFunction.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */