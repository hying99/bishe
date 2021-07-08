package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SingleIndex;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class MergeTwoValues extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  private SingleIndex m_AttIndex = new SingleIndex("last");
  
  private SingleIndex m_FirstIndex = new SingleIndex("first");
  
  private SingleIndex m_SecondIndex = new SingleIndex("last");
  
  public String globalInfo() {
    return "Merges two values of a nominal attribute into one value.";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_AttIndex.setUpper(paramInstances.numAttributes() - 1);
    this.m_FirstIndex.setUpper(paramInstances.attribute(this.m_AttIndex.getIndex()).numValues() - 1);
    this.m_SecondIndex.setUpper(paramInstances.attribute(this.m_AttIndex.getIndex()).numValues() - 1);
    if (!paramInstances.attribute(this.m_AttIndex.getIndex()).isNominal())
      throw new UnsupportedAttributeTypeException("Chosen attribute not nominal."); 
    if (paramInstances.attribute(this.m_AttIndex.getIndex()).numValues() < 2)
      throw new UnsupportedAttributeTypeException("Chosen attribute has less than two values."); 
    if (this.m_SecondIndex.getIndex() <= this.m_FirstIndex.getIndex())
      throw new Exception("The second index has to be greater than the first."); 
    setOutputFormat();
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    Instance instance = (Instance)paramInstance.copy();
    if ((int)instance.value(this.m_AttIndex.getIndex()) == this.m_SecondIndex.getIndex()) {
      instance.setValue(this.m_AttIndex.getIndex(), this.m_FirstIndex.getIndex());
    } else if ((int)instance.value(this.m_AttIndex.getIndex()) > this.m_SecondIndex.getIndex()) {
      instance.setValue(this.m_AttIndex.getIndex(), instance.value(this.m_AttIndex.getIndex()) - 1.0D);
    } 
    push(instance);
    return true;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tSets the attribute index (default last).", "C", 1, "-C <col>"));
    vector.addElement(new Option("\tSets the first value's index (default first).", "F", 1, "-F <value index>"));
    vector.addElement(new Option("\tSets the second value's index (default last).", "S", 1, "-S <value index>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('C', paramArrayOfString);
    if (str1.length() != 0) {
      setAttributeIndex(str1);
    } else {
      setAttributeIndex("last");
    } 
    String str2 = Utils.getOption('F', paramArrayOfString);
    if (str2.length() != 0) {
      setFirstValueIndex(str2);
    } else {
      setFirstValueIndex("first");
    } 
    String str3 = Utils.getOption('S', paramArrayOfString);
    if (str3.length() != 0) {
      setSecondValueIndex(str3);
    } else {
      setSecondValueIndex("last");
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[6];
    byte b = 0;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + getAttributeIndex();
    arrayOfString[b++] = "-F";
    arrayOfString[b++] = "" + getFirstValueIndex();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSecondValueIndex();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String attributeIndexTipText() {
    return "Sets which attribute to process. This attribute must be nominal (\"first\" and \"last\" are valid values)";
  }
  
  public String getAttributeIndex() {
    return this.m_AttIndex.getSingleIndex();
  }
  
  public void setAttributeIndex(String paramString) {
    this.m_AttIndex.setSingleIndex(paramString);
  }
  
  public String firstValueTipText() {
    return "Sets the first value to be merged. (\"first\" and \"last\" are valid values)";
  }
  
  public String getFirstValueIndex() {
    return this.m_FirstIndex.getSingleIndex();
  }
  
  public void setFirstValueIndex(String paramString) {
    this.m_FirstIndex.setSingleIndex(paramString);
  }
  
  public String secondValueTipText() {
    return "Sets the second value to be merged. (\"first\" and \"last\" are valid values)";
  }
  
  public String getSecondValueIndex() {
    return this.m_SecondIndex.getSingleIndex();
  }
  
  public void setSecondValueIndex(String paramString) {
    this.m_SecondIndex.setSingleIndex(paramString);
  }
  
  private void setOutputFormat() {
    boolean bool1 = false;
    boolean bool2 = false;
    StringBuffer stringBuffer = new StringBuffer();
    FastVector fastVector = new FastVector(getInputFormat().numAttributes());
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (b != this.m_AttIndex.getIndex()) {
        fastVector.addElement(attribute.copy());
      } else {
        if (attribute.value(this.m_FirstIndex.getIndex()).endsWith("'"))
          bool1 = true; 
        if (attribute.value(this.m_SecondIndex.getIndex()).endsWith("'"))
          bool2 = true; 
        if (bool1 || bool2)
          stringBuffer.append("'"); 
        if (bool1) {
          stringBuffer.append(attribute.value(this.m_FirstIndex.getIndex()).substring(1, attribute.value(this.m_FirstIndex.getIndex()).length() - 1));
        } else {
          stringBuffer.append(attribute.value(this.m_FirstIndex.getIndex()));
        } 
        stringBuffer.append('_');
        if (bool2) {
          stringBuffer.append(attribute.value(this.m_SecondIndex.getIndex()).substring(1, attribute.value(this.m_SecondIndex.getIndex()).length() - 1));
        } else {
          stringBuffer.append(attribute.value(this.m_SecondIndex.getIndex()));
        } 
        if (bool1 || bool2)
          stringBuffer.append("'"); 
        FastVector fastVector1 = new FastVector(attribute.numValues() - 1);
        for (byte b1 = 0; b1 < attribute.numValues(); b1++) {
          if (b1 == this.m_FirstIndex.getIndex()) {
            fastVector1.addElement(stringBuffer.toString());
          } else if (b1 != this.m_SecondIndex.getIndex()) {
            fastVector1.addElement(attribute.value(b1));
          } 
        } 
        fastVector.addElement(new Attribute(attribute.name(), fastVector1));
      } 
    } 
    Instances instances = new Instances(getInputFormat().relationName(), fastVector, 0);
    instances.setClassIndex(getInputFormat().classIndex());
    setOutputFormat(instances);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new MergeTwoValues(), paramArrayOfString);
      } else {
        Filter.filterFile(new MergeTwoValues(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\MergeTwoValues.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */