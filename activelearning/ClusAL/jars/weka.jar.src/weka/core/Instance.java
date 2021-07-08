package weka.core;

import java.io.Serializable;
import java.util.Enumeration;

public class Instance implements Copyable, Serializable {
  protected static final double MISSING_VALUE = NaND;
  
  protected Instances m_Dataset;
  
  protected double[] m_AttValues;
  
  protected double m_Weight;
  
  protected Instance(Instance paramInstance) {
    this.m_AttValues = paramInstance.m_AttValues;
    this.m_Weight = paramInstance.m_Weight;
    this.m_Dataset = null;
  }
  
  public Instance(double paramDouble, double[] paramArrayOfdouble) {
    this.m_AttValues = paramArrayOfdouble;
    this.m_Weight = paramDouble;
    this.m_Dataset = null;
  }
  
  public Instance(int paramInt) {
    this.m_AttValues = new double[paramInt];
    for (byte b = 0; b < this.m_AttValues.length; b++)
      this.m_AttValues[b] = Double.NaN; 
    this.m_Weight = 1.0D;
    this.m_Dataset = null;
  }
  
  public Attribute attribute(int paramInt) {
    if (this.m_Dataset == null)
      throw new UnassignedDatasetException("Instance doesn't have access to a dataset!"); 
    return this.m_Dataset.attribute(paramInt);
  }
  
  public Attribute attributeSparse(int paramInt) {
    if (this.m_Dataset == null)
      throw new UnassignedDatasetException("Instance doesn't have access to a dataset!"); 
    return this.m_Dataset.attribute(paramInt);
  }
  
  public Attribute classAttribute() {
    if (this.m_Dataset == null)
      throw new UnassignedDatasetException("Instance doesn't have access to a dataset!"); 
    return this.m_Dataset.classAttribute();
  }
  
  public int classIndex() {
    if (this.m_Dataset == null)
      throw new UnassignedDatasetException("Instance doesn't have access to a dataset!"); 
    return this.m_Dataset.classIndex();
  }
  
  public boolean classIsMissing() {
    if (classIndex() < 0)
      throw new UnassignedClassException("Class is not set!"); 
    return isMissing(classIndex());
  }
  
  public double classValue() {
    if (classIndex() < 0)
      throw new UnassignedClassException("Class is not set!"); 
    return value(classIndex());
  }
  
  public Object copy() {
    Instance instance = new Instance(this);
    instance.m_Dataset = this.m_Dataset;
    return instance;
  }
  
  public Instances dataset() {
    return this.m_Dataset;
  }
  
  public void deleteAttributeAt(int paramInt) {
    if (this.m_Dataset != null)
      throw new RuntimeException("Instance has access to a dataset!"); 
    forceDeleteAttributeAt(paramInt);
  }
  
  public Enumeration enumerateAttributes() {
    if (this.m_Dataset == null)
      throw new UnassignedDatasetException("Instance doesn't have access to a dataset!"); 
    return this.m_Dataset.enumerateAttributes();
  }
  
  public boolean equalHeaders(Instance paramInstance) {
    if (this.m_Dataset == null)
      throw new UnassignedDatasetException("Instance doesn't have access to a dataset!"); 
    return this.m_Dataset.equalHeaders(paramInstance.m_Dataset);
  }
  
  public boolean hasMissingValue() {
    if (this.m_Dataset == null)
      throw new UnassignedDatasetException("Instance doesn't have access to a dataset!"); 
    for (byte b = 0; b < numAttributes(); b++) {
      if (b != classIndex() && isMissing(b))
        return true; 
    } 
    return false;
  }
  
  public int index(int paramInt) {
    return paramInt;
  }
  
  public void insertAttributeAt(int paramInt) {
    if (this.m_Dataset != null)
      throw new RuntimeException("Instance has accesss to a dataset!"); 
    if (paramInt < 0 || paramInt > numAttributes())
      throw new IllegalArgumentException("Can't insert attribute: index out of range"); 
    forceInsertAttributeAt(paramInt);
  }
  
  public boolean isMissing(int paramInt) {
    return Double.isNaN(this.m_AttValues[paramInt]);
  }
  
  public boolean isMissingSparse(int paramInt) {
    return Double.isNaN(this.m_AttValues[paramInt]);
  }
  
  public boolean isMissing(Attribute paramAttribute) {
    return isMissing(paramAttribute.index());
  }
  
  public static boolean isMissingValue(double paramDouble) {
    return Double.isNaN(paramDouble);
  }
  
  public Instance mergeInstance(Instance paramInstance) {
    byte b1 = 0;
    double[] arrayOfDouble = new double[numAttributes() + paramInstance.numAttributes()];
    byte b2 = 0;
    while (b2 < numAttributes()) {
      arrayOfDouble[b1] = value(b2);
      b2++;
      b1++;
    } 
    b2 = 0;
    while (b2 < paramInstance.numAttributes()) {
      arrayOfDouble[b1] = paramInstance.value(b2);
      b2++;
      b1++;
    } 
    return new Instance(1.0D, arrayOfDouble);
  }
  
  public static double missingValue() {
    return Double.NaN;
  }
  
  public int numAttributes() {
    return this.m_AttValues.length;
  }
  
  public int numClasses() {
    if (this.m_Dataset == null)
      throw new UnassignedDatasetException("Instance doesn't have access to a dataset!"); 
    return this.m_Dataset.numClasses();
  }
  
  public int numValues() {
    return this.m_AttValues.length;
  }
  
  public void replaceMissingValues(double[] paramArrayOfdouble) {
    if (paramArrayOfdouble == null || paramArrayOfdouble.length != this.m_AttValues.length)
      throw new IllegalArgumentException("Unequal number of attributes!"); 
    freshAttributeVector();
    for (byte b = 0; b < this.m_AttValues.length; b++) {
      if (isMissing(b))
        this.m_AttValues[b] = paramArrayOfdouble[b]; 
    } 
  }
  
  public void setClassMissing() {
    if (classIndex() < 0)
      throw new UnassignedClassException("Class is not set!"); 
    setMissing(classIndex());
  }
  
  public void setClassValue(double paramDouble) {
    if (classIndex() < 0)
      throw new UnassignedClassException("Class is not set!"); 
    setValue(classIndex(), paramDouble);
  }
  
  public final void setClassValue(String paramString) {
    if (classIndex() < 0)
      throw new UnassignedClassException("Class is not set!"); 
    setValue(classIndex(), paramString);
  }
  
  public final void setDataset(Instances paramInstances) {
    this.m_Dataset = paramInstances;
  }
  
  public final void setMissing(int paramInt) {
    setValue(paramInt, Double.NaN);
  }
  
  public final void setMissing(Attribute paramAttribute) {
    setMissing(paramAttribute.index());
  }
  
  public void setValue(int paramInt, double paramDouble) {
    freshAttributeVector();
    this.m_AttValues[paramInt] = paramDouble;
  }
  
  public void setValueSparse(int paramInt, double paramDouble) {
    freshAttributeVector();
    this.m_AttValues[paramInt] = paramDouble;
  }
  
  public final void setValue(int paramInt, String paramString) {
    if (this.m_Dataset == null)
      throw new UnassignedDatasetException("Instance doesn't have access to a dataset!"); 
    if (!attribute(paramInt).isNominal() && !attribute(paramInt).isString())
      throw new IllegalArgumentException("Attribute neither nominal nor string!"); 
    int i = attribute(paramInt).indexOfValue(paramString);
    if (i == -1) {
      if (attribute(paramInt).isNominal())
        throw new IllegalArgumentException("Value not defined for given nominal attribute!"); 
      attribute(paramInt).forceAddValue(paramString);
      i = attribute(paramInt).indexOfValue(paramString);
    } 
    setValue(paramInt, i);
  }
  
  public final void setValue(Attribute paramAttribute, double paramDouble) {
    setValue(paramAttribute.index(), paramDouble);
  }
  
  public final void setValue(Attribute paramAttribute, String paramString) {
    if (!paramAttribute.isNominal() && !paramAttribute.isString())
      throw new IllegalArgumentException("Attribute neither nominal nor string!"); 
    int i = paramAttribute.indexOfValue(paramString);
    if (i == -1) {
      if (paramAttribute.isNominal())
        throw new IllegalArgumentException("Value not defined for given nominal attribute!"); 
      paramAttribute.forceAddValue(paramString);
      i = paramAttribute.indexOfValue(paramString);
    } 
    setValue(paramAttribute.index(), i);
  }
  
  public final void setWeight(double paramDouble) {
    this.m_Weight = paramDouble;
  }
  
  public final String stringValue(int paramInt) {
    if (this.m_Dataset == null)
      throw new UnassignedDatasetException("Instance doesn't have access to a dataset!"); 
    return stringValue(this.m_Dataset.attribute(paramInt));
  }
  
  public final String stringValue(Attribute paramAttribute) {
    int i = paramAttribute.index();
    switch (paramAttribute.type()) {
      case 1:
      case 2:
        return paramAttribute.value((int)value(i));
      case 3:
        return paramAttribute.formatDate(value(i));
    } 
    throw new IllegalArgumentException("Attribute isn't nominal, string or date!");
  }
  
  public double[] toDoubleArray() {
    double[] arrayOfDouble = new double[this.m_AttValues.length];
    System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, this.m_AttValues.length);
    return arrayOfDouble;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.m_AttValues.length; b++) {
      if (b > 0)
        stringBuffer.append(","); 
      stringBuffer.append(toString(b));
    } 
    return stringBuffer.toString();
  }
  
  public final String toString(int paramInt) {
    StringBuffer stringBuffer = new StringBuffer();
    if (isMissing(paramInt)) {
      stringBuffer.append("?");
    } else if (this.m_Dataset == null) {
      stringBuffer.append(Utils.doubleToString(this.m_AttValues[paramInt], 6));
    } else {
      switch (this.m_Dataset.attribute(paramInt).type()) {
        case 1:
        case 2:
        case 3:
          stringBuffer.append(Utils.quote(stringValue(paramInt)));
          return stringBuffer.toString();
        case 0:
          stringBuffer.append(Utils.doubleToString(value(paramInt), 6));
          return stringBuffer.toString();
      } 
      throw new IllegalStateException("Unknown attribute type");
    } 
    return stringBuffer.toString();
  }
  
  public final String toString(Attribute paramAttribute) {
    return toString(paramAttribute.index());
  }
  
  public double value(int paramInt) {
    return this.m_AttValues[paramInt];
  }
  
  public double valueSparse(int paramInt) {
    return this.m_AttValues[paramInt];
  }
  
  public double value(Attribute paramAttribute) {
    return value(paramAttribute.index());
  }
  
  public final double weight() {
    return this.m_Weight;
  }
  
  void forceDeleteAttributeAt(int paramInt) {
    double[] arrayOfDouble = new double[this.m_AttValues.length - 1];
    System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, paramInt);
    if (paramInt < this.m_AttValues.length - 1)
      System.arraycopy(this.m_AttValues, paramInt + 1, arrayOfDouble, paramInt, this.m_AttValues.length - paramInt + 1); 
    this.m_AttValues = arrayOfDouble;
  }
  
  void forceInsertAttributeAt(int paramInt) {
    double[] arrayOfDouble = new double[this.m_AttValues.length + 1];
    System.arraycopy(this.m_AttValues, 0, arrayOfDouble, 0, paramInt);
    arrayOfDouble[paramInt] = Double.NaN;
    System.arraycopy(this.m_AttValues, paramInt, arrayOfDouble, paramInt + 1, this.m_AttValues.length - paramInt);
    this.m_AttValues = arrayOfDouble;
  }
  
  protected Instance() {}
  
  private void freshAttributeVector() {
    this.m_AttValues = toDoubleArray();
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
      Instance instance1 = new Instance(3);
      instance1.setValue(attribute1, 5.3D);
      instance1.setValue(attribute2, 300.0D);
      instance1.setValue(attribute3, "first");
      instance1.setDataset(instances);
      System.out.println("The instance: " + instance1);
      System.out.println("First attribute: " + instance1.attribute(0));
      System.out.println("Class attribute: " + instance1.classAttribute());
      System.out.println("Class index: " + instance1.classIndex());
      System.out.println("Class is missing: " + instance1.classIsMissing());
      System.out.println("Class value (internal format): " + instance1.classValue());
      Instance instance2 = (Instance)instance1.copy();
      System.out.println("Shallow copy: " + instance2);
      instance2.setDataset(instance1.dataset());
      System.out.println("Shallow copy with dataset set: " + instance2);
      instance2.setDataset(null);
      instance2.deleteAttributeAt(0);
      instance2.insertAttributeAt(0);
      instance2.setDataset(instance1.dataset());
      System.out.println("Copy with first attribute deleted and inserted: " + instance2);
      System.out.println("Enumerating attributes (leaving out class):");
      Enumeration enumeration = instance1.enumerateAttributes();
      while (enumeration.hasMoreElements()) {
        Attribute attribute = enumeration.nextElement();
        System.out.println(attribute);
      } 
      System.out.println("Header of original and copy equivalent: " + instance1.equalHeaders(instance2));
      System.out.println("Length of copy missing: " + instance2.isMissing(attribute1));
      System.out.println("Weight of copy missing: " + instance2.isMissing(attribute2.index()));
      System.out.println("Length of copy missing: " + isMissingValue(instance2.value(attribute1)));
      System.out.println("Missing value coded as: " + missingValue());
      System.out.println("Number of attributes: " + instance2.numAttributes());
      System.out.println("Number of classes: " + instance2.numClasses());
      double[] arrayOfDouble = { 2.0D, 3.0D, 0.0D };
      instance2.replaceMissingValues(arrayOfDouble);
      System.out.println("Copy with missing value replaced: " + instance2);
      instance2.setClassMissing();
      System.out.println("Copy with missing class: " + instance2);
      instance2.setClassValue(0.0D);
      System.out.println("Copy with class value set to first value: " + instance2);
      instance2.setClassValue("third");
      System.out.println("Copy with class value set to \"third\": " + instance2);
      instance2.setMissing(1);
      System.out.println("Copy with second attribute set to be missing: " + instance2);
      instance2.setMissing(attribute1);
      System.out.println("Copy with length set to be missing: " + instance2);
      instance2.setValue(0, 0.0D);
      System.out.println("Copy with first attribute set to 0: " + instance2);
      instance2.setValue(attribute2, 1.0D);
      System.out.println("Copy with weight attribute set to 1: " + instance2);
      instance2.setValue(attribute3, "second");
      System.out.println("Copy with position set to \"second\": " + instance2);
      instance2.setValue(2, "first");
      System.out.println("Copy with last attribute set to \"first\": " + instance2);
      System.out.println("Current weight of instance copy: " + instance2.weight());
      instance2.setWeight(2.0D);
      System.out.println("Current weight of instance copy (set to 2): " + instance2.weight());
      System.out.println("Last value of copy: " + instance2.toString(2));
      System.out.println("Value of position for copy: " + instance2.toString(attribute3));
      System.out.println("Last value of copy (internal format): " + instance2.value(2));
      System.out.println("Value of position for copy (internal format): " + instance2.value(attribute3));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Instance.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */