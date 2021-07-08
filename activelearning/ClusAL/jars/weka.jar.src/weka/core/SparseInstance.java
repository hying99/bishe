package weka.core;

import java.util.Enumeration;

public class SparseInstance extends Instance {
  protected int[] m_Indices;
  
  protected int m_NumAttributes;
  
  protected SparseInstance() {}
  
  public SparseInstance(Instance paramInstance) {
    this.m_Weight = paramInstance.m_Weight;
    this.m_Dataset = null;
    this.m_NumAttributes = paramInstance.numAttributes();
    if (paramInstance instanceof SparseInstance) {
      this.m_AttValues = ((SparseInstance)paramInstance).m_AttValues;
      this.m_Indices = ((SparseInstance)paramInstance).m_Indices;
    } else {
      double[] arrayOfDouble = new double[paramInstance.numAttributes()];
      int[] arrayOfInt = new int[paramInstance.numAttributes()];
      byte b1 = 0;
      for (byte b2 = 0; b2 < paramInstance.numAttributes(); b2++) {
        if (paramInstance.value(b2) != 0.0D) {
          arrayOfDouble[b1] = paramInstance.value(b2);
          arrayOfInt[b1] = b2;
          b1++;
        } 
      } 
      this.m_AttValues = new double[b1];
      this.m_Indices = new int[b1];
      System.arraycopy(arrayOfDouble, 0, this.m_AttValues, 0, b1);
      System.arraycopy(arrayOfInt, 0, this.m_Indices, 0, b1);
    } 
  }
  
  public SparseInstance(SparseInstance paramSparseInstance) {
    this.m_AttValues = paramSparseInstance.m_AttValues;
    this.m_Indices = paramSparseInstance.m_Indices;
    this.m_Weight = paramSparseInstance.m_Weight;
    this.m_NumAttributes = paramSparseInstance.m_NumAttributes;
    this.m_Dataset = null;
  }
  
  public SparseInstance(double paramDouble, double[] paramArrayOfdouble) {
    this.m_Weight = paramDouble;
    this.m_Dataset = null;
    this.m_NumAttributes = paramArrayOfdouble.length;
    double[] arrayOfDouble = new double[this.m_NumAttributes];
    int[] arrayOfInt = new int[this.m_NumAttributes];
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_NumAttributes; b2++) {
      if (paramArrayOfdouble[b2] != 0.0D) {
        arrayOfDouble[b1] = paramArrayOfdouble[b2];
        arrayOfInt[b1] = b2;
        b1++;
      } 
    } 
    this.m_AttValues = new double[b1];
    this.m_Indices = new int[b1];
    System.arraycopy(arrayOfDouble, 0, this.m_AttValues, 0, b1);
    System.arraycopy(arrayOfInt, 0, this.m_Indices, 0, b1);
  }
  
  public SparseInstance(double paramDouble, double[] paramArrayOfdouble, int[] paramArrayOfint, int paramInt) {
    byte b1 = 0;
    this.m_AttValues = new double[paramArrayOfdouble.length];
    this.m_Indices = new int[paramArrayOfint.length];
    for (byte b2 = 0; b2 < paramArrayOfdouble.length; b2++) {
      if (paramArrayOfdouble[b2] != 0.0D) {
        this.m_AttValues[b1] = paramArrayOfdouble[b2];
        this.m_Indices[b1] = paramArrayOfint[b2];
        b1++;
      } 
    } 
    if (b1 != paramArrayOfdouble.length) {
      double[] arrayOfDouble = new double[b1];
      System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, b1);
      this.m_AttValues = arrayOfDouble;
      int[] arrayOfInt = new int[b1];
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, b1);
      this.m_Indices = arrayOfInt;
    } 
    this.m_Weight = paramDouble;
    this.m_NumAttributes = paramInt;
    this.m_Dataset = null;
  }
  
  public SparseInstance(int paramInt) {
    this.m_AttValues = new double[paramInt];
    this.m_NumAttributes = paramInt;
    this.m_Indices = new int[paramInt];
    for (byte b = 0; b < this.m_AttValues.length; b++) {
      this.m_AttValues[b] = Double.NaN;
      this.m_Indices[b] = b;
    } 
    this.m_Weight = 1.0D;
    this.m_Dataset = null;
  }
  
  public Attribute attributeSparse(int paramInt) {
    if (this.m_Dataset == null)
      throw new UnassignedDatasetException("Instance doesn't have access to a dataset!"); 
    return this.m_Dataset.attribute(this.m_Indices[paramInt]);
  }
  
  public Object copy() {
    SparseInstance sparseInstance = new SparseInstance(this);
    sparseInstance.m_Dataset = this.m_Dataset;
    return sparseInstance;
  }
  
  public int index(int paramInt) {
    return this.m_Indices[paramInt];
  }
  
  public boolean isMissing(int paramInt) {
    return Double.isNaN(value(paramInt));
  }
  
  public int locateIndex(int paramInt) {
    int i = 0;
    int j = this.m_Indices.length - 1;
    if (j == -1)
      return -1; 
    while (this.m_Indices[i] <= paramInt && this.m_Indices[j] >= paramInt) {
      int k = (j + i) / 2;
      if (this.m_Indices[k] > paramInt) {
        j = k - 1;
        continue;
      } 
      if (this.m_Indices[k] < paramInt) {
        i = k + 1;
        continue;
      } 
      return k;
    } 
    return (this.m_Indices[j] < paramInt) ? j : (i - 1);
  }
  
  public Instance mergeInstance(Instance paramInstance) {
    double[] arrayOfDouble = new double[numValues() + paramInstance.numValues()];
    int[] arrayOfInt = new int[numValues() + paramInstance.numValues()];
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < numValues()) {
      arrayOfDouble[b1] = valueSparse(b2);
      arrayOfInt[b1] = index(b2);
      b2++;
      b1++;
    } 
    b2 = 0;
    while (b2 < paramInstance.numValues()) {
      arrayOfDouble[b1] = paramInstance.valueSparse(b2);
      arrayOfInt[b1] = paramInstance.index(b2) + paramInstance.numAttributes();
      b2++;
      b1++;
    } 
    return new SparseInstance(1.0D, arrayOfDouble, arrayOfInt, numAttributes() + paramInstance.numAttributes());
  }
  
  public int numAttributes() {
    return this.m_NumAttributes;
  }
  
  public int numValues() {
    return this.m_Indices.length;
  }
  
  public void replaceMissingValues(double[] paramArrayOfdouble) {
    if (paramArrayOfdouble == null || paramArrayOfdouble.length != this.m_NumAttributes)
      throw new IllegalArgumentException("Unequal number of attributes!"); 
    double[] arrayOfDouble = new double[this.m_AttValues.length];
    int[] arrayOfInt = new int[this.m_AttValues.length];
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_AttValues.length; b2++) {
      if (isMissingValue(this.m_AttValues[b2])) {
        if (paramArrayOfdouble[this.m_Indices[b2]] != 0.0D) {
          arrayOfDouble[b1] = paramArrayOfdouble[this.m_Indices[b2]];
          arrayOfInt[b1] = this.m_Indices[b2];
          b1++;
        } 
      } else {
        arrayOfDouble[b1] = this.m_AttValues[b2];
        arrayOfInt[b1] = this.m_Indices[b2];
        b1++;
      } 
    } 
    this.m_AttValues = new double[b1];
    this.m_Indices = new int[b1];
    System.arraycopy(arrayOfDouble, 0, this.m_AttValues, 0, b1);
    System.arraycopy(arrayOfInt, 0, this.m_Indices, 0, b1);
  }
  
  public void setValue(int paramInt, double paramDouble) {
    int i = locateIndex(paramInt);
    if (i >= 0 && this.m_Indices[i] == paramInt) {
      if (paramDouble != 0.0D) {
        double[] arrayOfDouble = new double[this.m_AttValues.length];
        System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, this.m_AttValues.length);
        arrayOfDouble[i] = paramDouble;
        this.m_AttValues = arrayOfDouble;
      } else {
        double[] arrayOfDouble = new double[this.m_AttValues.length - 1];
        int[] arrayOfInt = new int[this.m_Indices.length - 1];
        System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, i);
        System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i);
        System.arraycopy(this.m_AttValues, i + 1, arrayOfDouble, i, this.m_AttValues.length - i - 1);
        System.arraycopy(this.m_Indices, i + 1, arrayOfInt, i, this.m_Indices.length - i - 1);
        this.m_AttValues = arrayOfDouble;
        this.m_Indices = arrayOfInt;
      } 
    } else if (paramDouble != 0.0D) {
      double[] arrayOfDouble = new double[this.m_AttValues.length + 1];
      int[] arrayOfInt = new int[this.m_Indices.length + 1];
      System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, i + 1);
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i + 1);
      arrayOfInt[i + 1] = paramInt;
      arrayOfDouble[i + 1] = paramDouble;
      System.arraycopy(this.m_AttValues, i + 1, arrayOfDouble, i + 2, this.m_AttValues.length - i - 1);
      System.arraycopy(this.m_Indices, i + 1, arrayOfInt, i + 2, this.m_Indices.length - i - 1);
      this.m_AttValues = arrayOfDouble;
      this.m_Indices = arrayOfInt;
    } 
  }
  
  public void setValueSparse(int paramInt, double paramDouble) {
    if (paramDouble != 0.0D) {
      double[] arrayOfDouble = new double[this.m_AttValues.length];
      System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, this.m_AttValues.length);
      this.m_AttValues = arrayOfDouble;
      this.m_AttValues[paramInt] = paramDouble;
    } else {
      double[] arrayOfDouble = new double[this.m_AttValues.length - 1];
      int[] arrayOfInt = new int[this.m_Indices.length - 1];
      System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, paramInt);
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, paramInt);
      System.arraycopy(this.m_AttValues, paramInt + 1, arrayOfDouble, paramInt, this.m_AttValues.length - paramInt - 1);
      System.arraycopy(this.m_Indices, paramInt + 1, arrayOfInt, paramInt, this.m_Indices.length - paramInt - 1);
      this.m_AttValues = arrayOfDouble;
      this.m_Indices = arrayOfInt;
    } 
  }
  
  public double[] toDoubleArray() {
    double[] arrayOfDouble = new double[this.m_NumAttributes];
    for (byte b = 0; b < this.m_AttValues.length; b++)
      arrayOfDouble[this.m_Indices[b]] = this.m_AttValues[b]; 
    return arrayOfDouble;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('{');
    for (byte b = 0; b < this.m_Indices.length; b++) {
      if (b > 0)
        stringBuffer.append(","); 
      if (isMissingValue(this.m_AttValues[b])) {
        stringBuffer.append(this.m_Indices[b] + " ?");
      } else if (this.m_Dataset == null) {
        stringBuffer.append(this.m_Indices[b] + " " + Utils.doubleToString(this.m_AttValues[b], 6));
      } else if (this.m_Dataset.attribute(this.m_Indices[b]).isNominal() || this.m_Dataset.attribute(this.m_Indices[b]).isString()) {
        try {
          stringBuffer.append(this.m_Indices[b] + " " + Utils.quote(this.m_Dataset.attribute(this.m_Indices[b]).value((int)valueSparse(b))));
        } catch (Exception exception) {
          exception.printStackTrace();
          System.err.println(new Instances(this.m_Dataset, 0));
          System.err.println("Att:" + this.m_Indices[b] + " Val:" + valueSparse(b));
          throw new Error("This should never happen!");
        } 
      } else {
        stringBuffer.append(this.m_Indices[b] + " " + Utils.doubleToString(this.m_AttValues[b], 6));
      } 
    } 
    stringBuffer.append('}');
    return stringBuffer.toString();
  }
  
  public double value(int paramInt) {
    int i = locateIndex(paramInt);
    return (i >= 0 && this.m_Indices[i] == paramInt) ? this.m_AttValues[i] : 0.0D;
  }
  
  void forceDeleteAttributeAt(int paramInt) {
    int i = locateIndex(paramInt);
    this.m_NumAttributes--;
    if (i >= 0 && this.m_Indices[i] == paramInt) {
      int[] arrayOfInt = new int[this.m_Indices.length - 1];
      double[] arrayOfDouble = new double[this.m_AttValues.length - 1];
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i);
      System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, i);
      for (int j = i; j < this.m_Indices.length - 1; j++) {
        arrayOfInt[j] = this.m_Indices[j + 1] - 1;
        arrayOfDouble[j] = this.m_AttValues[j + 1];
      } 
      this.m_Indices = arrayOfInt;
      this.m_AttValues = arrayOfDouble;
    } else {
      int[] arrayOfInt = new int[this.m_Indices.length];
      double[] arrayOfDouble = new double[this.m_AttValues.length];
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i + 1);
      System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, i + 1);
      for (int j = i + 1; j < this.m_Indices.length; j++) {
        arrayOfInt[j] = this.m_Indices[j] - 1;
        arrayOfDouble[j] = this.m_AttValues[j];
      } 
      this.m_Indices = arrayOfInt;
      this.m_AttValues = arrayOfDouble;
    } 
  }
  
  void forceInsertAttributeAt(int paramInt) {
    int i = locateIndex(paramInt);
    this.m_NumAttributes++;
    if (i >= 0 && this.m_Indices[i] == paramInt) {
      int[] arrayOfInt = new int[this.m_Indices.length + 1];
      double[] arrayOfDouble = new double[this.m_AttValues.length + 1];
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i);
      System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, i);
      arrayOfInt[i] = paramInt;
      arrayOfDouble[i] = Double.NaN;
      for (int j = i; j < this.m_Indices.length; j++) {
        arrayOfInt[j + 1] = this.m_Indices[j] + 1;
        arrayOfDouble[j + 1] = this.m_AttValues[j];
      } 
      this.m_Indices = arrayOfInt;
      this.m_AttValues = arrayOfDouble;
    } else {
      int[] arrayOfInt = new int[this.m_Indices.length + 1];
      double[] arrayOfDouble = new double[this.m_AttValues.length + 1];
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i + 1);
      System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, i + 1);
      arrayOfInt[i + 1] = paramInt;
      arrayOfDouble[i + 1] = Double.NaN;
      for (int j = i + 1; j < this.m_Indices.length; j++) {
        arrayOfInt[j + 1] = this.m_Indices[j] + 1;
        arrayOfDouble[j + 1] = this.m_AttValues[j];
      } 
      this.m_Indices = arrayOfInt;
      this.m_AttValues = arrayOfDouble;
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Attribute attribute1 = new Attribute("length");
      Attribute attribute2 = new Attribute("weight");
      FastVector fastVector1 = new FastVector(3);
      fastVector1.addElement("first");
      fastVector1.addElement("second");
      fastVector1.addElement("third");
      Attribute attribute3 = new Attribute("position", fastVector1);
      FastVector fastVector2 = new FastVector(3);
      fastVector2.addElement(attribute1);
      fastVector2.addElement(attribute2);
      fastVector2.addElement(attribute3);
      Instances instances = new Instances("race", fastVector2, 0);
      instances.setClassIndex(attribute3.index());
      SparseInstance sparseInstance1 = new SparseInstance(3);
      sparseInstance1.setValue(attribute1, 5.3D);
      sparseInstance1.setValue(attribute2, 300.0D);
      sparseInstance1.setValue(attribute3, "first");
      sparseInstance1.setDataset(instances);
      System.out.println("The instance: " + sparseInstance1);
      System.out.println("First attribute: " + sparseInstance1.attribute(0));
      System.out.println("Class attribute: " + sparseInstance1.classAttribute());
      System.out.println("Class index: " + sparseInstance1.classIndex());
      System.out.println("Class is missing: " + sparseInstance1.classIsMissing());
      System.out.println("Class value (internal format): " + sparseInstance1.classValue());
      SparseInstance sparseInstance2 = (SparseInstance)sparseInstance1.copy();
      System.out.println("Shallow copy: " + sparseInstance2);
      sparseInstance2.setDataset(sparseInstance1.dataset());
      System.out.println("Shallow copy with dataset set: " + sparseInstance2);
      System.out.print("All stored values in internal format: ");
      byte b;
      for (b = 0; b < sparseInstance1.numValues(); b++) {
        if (b > 0)
          System.out.print(","); 
        System.out.print(sparseInstance1.valueSparse(b));
      } 
      System.out.println();
      System.out.print("All values set to zero: ");
      while (sparseInstance1.numValues() > 0)
        sparseInstance1.setValueSparse(0, 0.0D); 
      for (b = 0; b < sparseInstance1.numValues(); b++) {
        if (b > 0)
          System.out.print(","); 
        System.out.print(sparseInstance1.valueSparse(b));
      } 
      System.out.println();
      System.out.print("All values set to one: ");
      for (b = 0; b < sparseInstance1.numAttributes(); b++)
        sparseInstance1.setValue(b, 1.0D); 
      for (b = 0; b < sparseInstance1.numValues(); b++) {
        if (b > 0)
          System.out.print(","); 
        System.out.print(sparseInstance1.valueSparse(b));
      } 
      System.out.println();
      sparseInstance2.setDataset(null);
      sparseInstance2.deleteAttributeAt(0);
      sparseInstance2.insertAttributeAt(0);
      sparseInstance2.setDataset(sparseInstance1.dataset());
      System.out.println("Copy with first attribute deleted and inserted: " + sparseInstance2);
      sparseInstance2.setDataset(null);
      sparseInstance2.deleteAttributeAt(1);
      sparseInstance2.insertAttributeAt(1);
      sparseInstance2.setDataset(sparseInstance1.dataset());
      System.out.println("Copy with second attribute deleted and inserted: " + sparseInstance2);
      sparseInstance2.setDataset(null);
      sparseInstance2.deleteAttributeAt(2);
      sparseInstance2.insertAttributeAt(2);
      sparseInstance2.setDataset(sparseInstance1.dataset());
      System.out.println("Copy with third attribute deleted and inserted: " + sparseInstance2);
      System.out.println("Enumerating attributes (leaving out class):");
      Enumeration enumeration = sparseInstance1.enumerateAttributes();
      while (enumeration.hasMoreElements()) {
        Attribute attribute = enumeration.nextElement();
        System.out.println(attribute);
      } 
      System.out.println("Header of original and copy equivalent: " + sparseInstance1.equalHeaders(sparseInstance2));
      System.out.println("Length of copy missing: " + sparseInstance2.isMissing(attribute1));
      System.out.println("Weight of copy missing: " + sparseInstance2.isMissing(attribute2.index()));
      System.out.println("Length of copy missing: " + Instance.isMissingValue(sparseInstance2.value(attribute1)));
      System.out.println("Missing value coded as: " + Instance.missingValue());
      System.out.println("Number of attributes: " + sparseInstance2.numAttributes());
      System.out.println("Number of classes: " + sparseInstance2.numClasses());
      double[] arrayOfDouble = { 2.0D, 3.0D, 0.0D };
      sparseInstance2.replaceMissingValues(arrayOfDouble);
      System.out.println("Copy with missing value replaced: " + sparseInstance2);
      sparseInstance2.setClassMissing();
      System.out.println("Copy with missing class: " + sparseInstance2);
      sparseInstance2.setClassValue(0.0D);
      System.out.println("Copy with class value set to first value: " + sparseInstance2);
      sparseInstance2.setClassValue("third");
      System.out.println("Copy with class value set to \"third\": " + sparseInstance2);
      sparseInstance2.setMissing(1);
      System.out.println("Copy with second attribute set to be missing: " + sparseInstance2);
      sparseInstance2.setMissing(attribute1);
      System.out.println("Copy with length set to be missing: " + sparseInstance2);
      sparseInstance2.setValue(0, 0.0D);
      System.out.println("Copy with first attribute set to 0: " + sparseInstance2);
      sparseInstance2.setValue(attribute2, 1.0D);
      System.out.println("Copy with weight attribute set to 1: " + sparseInstance2);
      sparseInstance2.setValue(attribute3, "second");
      System.out.println("Copy with position set to \"second\": " + sparseInstance2);
      sparseInstance2.setValue(2, "first");
      System.out.println("Copy with last attribute set to \"first\": " + sparseInstance2);
      System.out.println("Current weight of instance copy: " + sparseInstance2.weight());
      sparseInstance2.setWeight(2.0D);
      System.out.println("Current weight of instance copy (set to 2): " + sparseInstance2.weight());
      System.out.println("Last value of copy: " + sparseInstance2.toString(2));
      System.out.println("Value of position for copy: " + sparseInstance2.toString(attribute3));
      System.out.println("Last value of copy (internal format): " + sparseInstance2.value(2));
      System.out.println("Value of position for copy (internal format): " + sparseInstance2.value(attribute3));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\SparseInstance.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */