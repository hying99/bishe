package weka.core;

import java.util.Enumeration;

public class BinarySparseInstance extends SparseInstance {
  public BinarySparseInstance(Instance paramInstance) {
    this.m_Weight = paramInstance.m_Weight;
    this.m_Dataset = null;
    this.m_NumAttributes = paramInstance.numAttributes();
    if (paramInstance instanceof SparseInstance) {
      this.m_AttValues = null;
      this.m_Indices = ((SparseInstance)paramInstance).m_Indices;
    } else {
      int[] arrayOfInt = new int[paramInstance.numAttributes()];
      byte b1 = 0;
      for (byte b2 = 0; b2 < paramInstance.numAttributes(); b2++) {
        if (paramInstance.value(b2) != 0.0D) {
          arrayOfInt[b1] = b2;
          b1++;
        } 
      } 
      this.m_AttValues = null;
      this.m_Indices = new int[b1];
      System.arraycopy(arrayOfInt, 0, this.m_Indices, 0, b1);
    } 
  }
  
  public BinarySparseInstance(SparseInstance paramSparseInstance) {
    this.m_AttValues = null;
    this.m_Indices = paramSparseInstance.m_Indices;
    this.m_Weight = paramSparseInstance.m_Weight;
    this.m_NumAttributes = paramSparseInstance.m_NumAttributes;
    this.m_Dataset = null;
  }
  
  public BinarySparseInstance(double paramDouble, double[] paramArrayOfdouble) {
    this.m_Weight = paramDouble;
    this.m_Dataset = null;
    this.m_NumAttributes = paramArrayOfdouble.length;
    int[] arrayOfInt = new int[this.m_NumAttributes];
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_NumAttributes; b2++) {
      if (paramArrayOfdouble[b2] != 0.0D) {
        arrayOfInt[b1] = b2;
        b1++;
      } 
    } 
    this.m_AttValues = null;
    this.m_Indices = new int[b1];
    System.arraycopy(arrayOfInt, 0, this.m_Indices, 0, b1);
  }
  
  public BinarySparseInstance(double paramDouble, int[] paramArrayOfint, int paramInt) {
    this.m_AttValues = null;
    this.m_Indices = paramArrayOfint;
    this.m_Weight = paramDouble;
    this.m_NumAttributes = paramInt;
    this.m_Dataset = null;
  }
  
  public BinarySparseInstance(int paramInt) {
    this.m_AttValues = null;
    this.m_NumAttributes = paramInt;
    this.m_Indices = new int[paramInt];
    for (byte b = 0; b < this.m_Indices.length; b++)
      this.m_Indices[b] = b; 
    this.m_Weight = 1.0D;
    this.m_Dataset = null;
  }
  
  public Object copy() {
    return new BinarySparseInstance(this);
  }
  
  public Instance mergeInstance(Instance paramInstance) {
    int[] arrayOfInt = new int[numValues() + paramInstance.numValues()];
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < numValues(); b2++)
      arrayOfInt[b1++] = index(b2); 
    for (b2 = 0; b2 < paramInstance.numValues(); b2++) {
      if (paramInstance.valueSparse(b2) != 0.0D)
        arrayOfInt[b1++] = paramInstance.index(b2) + paramInstance.numAttributes(); 
    } 
    if (b1 != arrayOfInt.length) {
      int[] arrayOfInt1 = new int[b1];
      System.arraycopy(arrayOfInt, 0, arrayOfInt1, 0, b1);
      arrayOfInt = arrayOfInt1;
    } 
    return new BinarySparseInstance(1.0D, arrayOfInt, numAttributes() + paramInstance.numAttributes());
  }
  
  public void replaceMissingValues(double[] paramArrayOfdouble) {}
  
  public void setValue(int paramInt, double paramDouble) {
    int i = locateIndex(paramInt);
    if (i >= 0 && this.m_Indices[i] == paramInt) {
      if (paramDouble == 0.0D) {
        int[] arrayOfInt = new int[this.m_Indices.length - 1];
        System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i);
        System.arraycopy(this.m_Indices, i + 1, arrayOfInt, i, this.m_Indices.length - i - 1);
        this.m_Indices = arrayOfInt;
      } 
    } else if (paramDouble != 0.0D) {
      int[] arrayOfInt = new int[this.m_Indices.length + 1];
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i + 1);
      arrayOfInt[i + 1] = paramInt;
      System.arraycopy(this.m_Indices, i + 1, arrayOfInt, i + 2, this.m_Indices.length - i - 1);
      this.m_Indices = arrayOfInt;
    } 
  }
  
  public void setValueSparse(int paramInt, double paramDouble) {
    if (paramDouble == 0.0D) {
      int[] arrayOfInt = new int[this.m_Indices.length - 1];
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, paramInt);
      System.arraycopy(this.m_Indices, paramInt + 1, arrayOfInt, paramInt, this.m_Indices.length - paramInt - 1);
      this.m_Indices = arrayOfInt;
    } 
  }
  
  public double[] toDoubleArray() {
    double[] arrayOfDouble = new double[this.m_NumAttributes];
    for (byte b = 0; b < this.m_AttValues.length; b++)
      arrayOfDouble[this.m_Indices[b]] = 1.0D; 
    return arrayOfDouble;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('{');
    for (byte b = 0; b < this.m_Indices.length; b++) {
      if (b > 0)
        stringBuffer.append(","); 
      if (this.m_Dataset == null) {
        stringBuffer.append(this.m_Indices[b] + " 1");
      } else if (this.m_Dataset.attribute(this.m_Indices[b]).isNominal() || this.m_Dataset.attribute(this.m_Indices[b]).isString()) {
        stringBuffer.append(this.m_Indices[b] + " " + Utils.quote(this.m_Dataset.attribute(this.m_Indices[b]).value(1)));
      } else {
        stringBuffer.append(this.m_Indices[b] + " 1");
      } 
    } 
    stringBuffer.append('}');
    return stringBuffer.toString();
  }
  
  public double value(int paramInt) {
    int i = locateIndex(paramInt);
    return (i >= 0 && this.m_Indices[i] == paramInt) ? 1.0D : 0.0D;
  }
  
  public final double valueSparse(int paramInt) {
    int i = this.m_Indices[paramInt];
    return 1.0D;
  }
  
  void forceDeleteAttributeAt(int paramInt) {
    int i = locateIndex(paramInt);
    this.m_NumAttributes--;
    if (i >= 0 && this.m_Indices[i] == paramInt) {
      int[] arrayOfInt = new int[this.m_Indices.length - 1];
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i);
      for (int j = i; j < this.m_Indices.length - 1; j++)
        arrayOfInt[j] = this.m_Indices[j + 1] - 1; 
      this.m_Indices = arrayOfInt;
    } else {
      int[] arrayOfInt = new int[this.m_Indices.length];
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i + 1);
      for (int j = i + 1; j < this.m_Indices.length - 1; j++)
        arrayOfInt[j] = this.m_Indices[j] - 1; 
      this.m_Indices = arrayOfInt;
    } 
  }
  
  void forceInsertAttributeAt(int paramInt) {
    int i = locateIndex(paramInt);
    this.m_NumAttributes++;
    if (i >= 0 && this.m_Indices[i] == paramInt) {
      int[] arrayOfInt = new int[this.m_Indices.length + 1];
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i);
      arrayOfInt[i] = paramInt;
      for (int j = i; j < this.m_Indices.length; j++)
        arrayOfInt[j + 1] = this.m_Indices[j] + 1; 
      this.m_Indices = arrayOfInt;
    } else {
      int[] arrayOfInt = new int[this.m_Indices.length + 1];
      System.arraycopy(this.m_Indices, 0, arrayOfInt, 0, i + 1);
      arrayOfInt[i + 1] = paramInt;
      for (int j = i + 1; j < this.m_Indices.length; j++)
        arrayOfInt[j + 1] = this.m_Indices[j] + 1; 
      this.m_Indices = arrayOfInt;
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Attribute attribute1 = new Attribute("length");
      Attribute attribute2 = new Attribute("weight");
      FastVector fastVector1 = new FastVector(3);
      fastVector1.addElement("first");
      fastVector1.addElement("second");
      Attribute attribute3 = new Attribute("position", fastVector1);
      FastVector fastVector2 = new FastVector(3);
      fastVector2.addElement(attribute1);
      fastVector2.addElement(attribute2);
      fastVector2.addElement(attribute3);
      Instances instances = new Instances("race", fastVector2, 0);
      instances.setClassIndex(attribute3.index());
      BinarySparseInstance binarySparseInstance = new BinarySparseInstance(3);
      binarySparseInstance.setValue(attribute1, 5.3D);
      binarySparseInstance.setValue(attribute2, 300.0D);
      binarySparseInstance.setValue(attribute3, "first");
      binarySparseInstance.setDataset(instances);
      System.out.println("The instance: " + binarySparseInstance);
      System.out.println("First attribute: " + binarySparseInstance.attribute(0));
      System.out.println("Class attribute: " + binarySparseInstance.classAttribute());
      System.out.println("Class index: " + binarySparseInstance.classIndex());
      System.out.println("Class is missing: " + binarySparseInstance.classIsMissing());
      System.out.println("Class value (internal format): " + binarySparseInstance.classValue());
      SparseInstance sparseInstance = (SparseInstance)binarySparseInstance.copy();
      System.out.println("Shallow copy: " + sparseInstance);
      sparseInstance.setDataset(binarySparseInstance.dataset());
      System.out.println("Shallow copy with dataset set: " + sparseInstance);
      System.out.print("All stored values in internal format: ");
      byte b;
      for (b = 0; b < binarySparseInstance.numValues(); b++) {
        if (b > 0)
          System.out.print(","); 
        System.out.print(binarySparseInstance.valueSparse(b));
      } 
      System.out.println();
      System.out.print("All values set to zero: ");
      while (binarySparseInstance.numValues() > 0)
        binarySparseInstance.setValueSparse(0, 0.0D); 
      for (b = 0; b < binarySparseInstance.numValues(); b++) {
        if (b > 0)
          System.out.print(","); 
        System.out.print(binarySparseInstance.valueSparse(b));
      } 
      System.out.println();
      System.out.print("All values set to one: ");
      for (b = 0; b < binarySparseInstance.numAttributes(); b++)
        binarySparseInstance.setValue(b, 1.0D); 
      for (b = 0; b < binarySparseInstance.numValues(); b++) {
        if (b > 0)
          System.out.print(","); 
        System.out.print(binarySparseInstance.valueSparse(b));
      } 
      System.out.println();
      sparseInstance.setDataset(null);
      sparseInstance.deleteAttributeAt(0);
      sparseInstance.insertAttributeAt(0);
      sparseInstance.setDataset(binarySparseInstance.dataset());
      System.out.println("Copy with first attribute deleted and inserted: " + sparseInstance);
      sparseInstance.setDataset(null);
      sparseInstance.deleteAttributeAt(1);
      sparseInstance.insertAttributeAt(1);
      sparseInstance.setDataset(binarySparseInstance.dataset());
      System.out.println("Copy with second attribute deleted and inserted: " + sparseInstance);
      sparseInstance.setDataset(null);
      sparseInstance.deleteAttributeAt(2);
      sparseInstance.insertAttributeAt(2);
      sparseInstance.setDataset(binarySparseInstance.dataset());
      System.out.println("Copy with third attribute deleted and inserted: " + sparseInstance);
      System.out.println("Enumerating attributes (leaving out class):");
      Enumeration enumeration = binarySparseInstance.enumerateAttributes();
      while (enumeration.hasMoreElements()) {
        Attribute attribute = enumeration.nextElement();
        System.out.println(attribute);
      } 
      System.out.println("Header of original and copy equivalent: " + binarySparseInstance.equalHeaders(sparseInstance));
      System.out.println("Length of copy missing: " + sparseInstance.isMissing(attribute1));
      System.out.println("Weight of copy missing: " + sparseInstance.isMissing(attribute2.index()));
      System.out.println("Length of copy missing: " + Instance.isMissingValue(sparseInstance.value(attribute1)));
      System.out.println("Missing value coded as: " + Instance.missingValue());
      System.out.println("Number of attributes: " + sparseInstance.numAttributes());
      System.out.println("Number of classes: " + sparseInstance.numClasses());
      double[] arrayOfDouble = { 2.0D, 3.0D, 0.0D };
      sparseInstance.replaceMissingValues(arrayOfDouble);
      System.out.println("Copy with missing value replaced: " + sparseInstance);
      sparseInstance.setClassMissing();
      System.out.println("Copy with missing class: " + sparseInstance);
      sparseInstance.setClassValue(0.0D);
      System.out.println("Copy with class value set to first value: " + sparseInstance);
      sparseInstance.setClassValue("second");
      System.out.println("Copy with class value set to \"second\": " + sparseInstance);
      sparseInstance.setMissing(1);
      System.out.println("Copy with second attribute set to be missing: " + sparseInstance);
      sparseInstance.setMissing(attribute1);
      System.out.println("Copy with length set to be missing: " + sparseInstance);
      sparseInstance.setValue(0, 0.0D);
      System.out.println("Copy with first attribute set to 0: " + sparseInstance);
      sparseInstance.setValue(attribute2, 1.0D);
      System.out.println("Copy with weight attribute set to 1: " + sparseInstance);
      sparseInstance.setValue(attribute3, "second");
      System.out.println("Copy with position set to \"second\": " + sparseInstance);
      sparseInstance.setValue(2, "first");
      System.out.println("Copy with last attribute set to \"first\": " + sparseInstance);
      System.out.println("Current weight of instance copy: " + sparseInstance.weight());
      sparseInstance.setWeight(2.0D);
      System.out.println("Current weight of instance copy (set to 2): " + sparseInstance.weight());
      System.out.println("Last value of copy: " + sparseInstance.toString(2));
      System.out.println("Value of position for copy: " + sparseInstance.toString(attribute3));
      System.out.println("Last value of copy (internal format): " + sparseInstance.value(2));
      System.out.println("Value of position for copy (internal format): " + sparseInstance.value(attribute3));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\BinarySparseInstance.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */