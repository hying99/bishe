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

public class SwapValues extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  private SingleIndex m_AttIndex = new SingleIndex("last");
  
  private SingleIndex m_FirstIndex = new SingleIndex("first");
  
  private SingleIndex m_SecondIndex = new SingleIndex("last");
  
  public String globalInfo() {
    return "Swaps two values of a nominal attribute.";
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
    if (!instance.isMissing(this.m_AttIndex.getIndex()))
      if ((int)instance.value(this.m_AttIndex.getIndex()) == this.m_SecondIndex.getIndex()) {
        instance.setValue(this.m_AttIndex.getIndex(), this.m_FirstIndex.getIndex());
      } else if ((int)instance.value(this.m_AttIndex.getIndex()) == this.m_FirstIndex.getIndex()) {
        instance.setValue(this.m_AttIndex.getIndex(), this.m_SecondIndex.getIndex());
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
  
  public String firstValueIndexTipText() {
    return "The index of the first value.(\"first\" and \"last\" are valid values)";
  }
  
  public String getFirstValueIndex() {
    return this.m_FirstIndex.getSingleIndex();
  }
  
  public void setFirstValueIndex(String paramString) {
    this.m_FirstIndex.setSingleIndex(paramString);
  }
  
  public String secondValueIndexTipText() {
    return "The index of the second value.(\"first\" and \"last\" are valid values)";
  }
  
  public String getSecondValueIndex() {
    return this.m_SecondIndex.getSingleIndex();
  }
  
  public void setSecondValueIndex(String paramString) {
    this.m_SecondIndex.setSingleIndex(paramString);
  }
  
  private void setOutputFormat() {
    FastVector fastVector = new FastVector(getInputFormat().numAttributes());
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (b != this.m_AttIndex.getIndex()) {
        fastVector.addElement(attribute.copy());
      } else {
        FastVector fastVector1 = new FastVector(attribute.numValues());
        for (byte b1 = 0; b1 < attribute.numValues(); b1++) {
          if (b1 == this.m_FirstIndex.getIndex()) {
            fastVector1.addElement(attribute.value(this.m_SecondIndex.getIndex()));
          } else if (b1 == this.m_SecondIndex.getIndex()) {
            fastVector1.addElement(attribute.value(this.m_FirstIndex.getIndex()));
          } else {
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
        Filter.batchFilterFile(new SwapValues(), paramArrayOfString);
      } else {
        Filter.filterFile(new SwapValues(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\SwapValues.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */