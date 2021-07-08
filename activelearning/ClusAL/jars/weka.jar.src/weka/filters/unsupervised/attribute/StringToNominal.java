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
import weka.filters.UnsupervisedFilter;

public class StringToNominal extends Filter implements UnsupervisedFilter, OptionHandler {
  private SingleIndex m_AttIndex = new SingleIndex("last");
  
  public String globalInfo() {
    return "Converts a string attribute (i.e. unspecified number of values) to nominal (i.e. set number of values). You should ensure that all string values that will appear are represented in the first batch of the data.";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_AttIndex.setUpper(paramInstances.numAttributes() - 1);
    if (!paramInstances.attribute(this.m_AttIndex.getIndex()).isString())
      throw new UnsupportedAttributeTypeException("Chosen attribute is not of type string."); 
    return false;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (isOutputFormatDefined()) {
      Instance instance = (Instance)paramInstance.copy();
      push(instance);
      return true;
    } 
    bufferInput(paramInstance);
    return false;
  }
  
  public boolean batchFinished() {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (!isOutputFormatDefined()) {
      setOutputFormat();
      for (byte b = 0; b < getInputFormat().numInstances(); b++)
        push((Instance)getInputFormat().instance(b).copy()); 
    } 
    flushInput();
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tSets the attribute index (default last).", "C", 1, "-C <col>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('C', paramArrayOfString);
    if (str.length() != 0) {
      setAttributeIndex(str);
    } else {
      setAttributeIndex("last");
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[6];
    byte b = 0;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + getAttributeIndex();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String attributeIndexTipText() {
    return "Sets which attribute to process. This attribute must be a string attribute (\"first\" and \"last\" are valid values)";
  }
  
  public String getAttributeIndex() {
    return this.m_AttIndex.getSingleIndex();
  }
  
  public void setAttributeIndex(String paramString) {
    this.m_AttIndex.setSingleIndex(paramString);
  }
  
  private void setOutputFormat() {
    FastVector fastVector = new FastVector(getInputFormat().numAttributes());
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (b != this.m_AttIndex.getIndex()) {
        fastVector.addElement(attribute);
      } else {
        FastVector fastVector1 = new FastVector(attribute.numValues());
        for (byte b1 = 0; b1 < attribute.numValues(); b1++)
          fastVector1.addElement(attribute.value(b1)); 
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
        Filter.batchFilterFile(new StringToNominal(), paramArrayOfString);
      } else {
        Filter.filterFile(new StringToNominal(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\StringToNominal.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */