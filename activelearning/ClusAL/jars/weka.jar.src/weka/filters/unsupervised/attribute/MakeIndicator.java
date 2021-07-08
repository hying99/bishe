package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.SingleIndex;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class MakeIndicator extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  private SingleIndex m_AttIndex = new SingleIndex("last");
  
  private Range m_ValIndex = new Range("last");
  
  private boolean m_Numeric = true;
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_AttIndex.setUpper(paramInstances.numAttributes() - 1);
    this.m_ValIndex.setUpper(paramInstances.attribute(this.m_AttIndex.getIndex()).numValues() - 1);
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
      if (this.m_ValIndex.isInRange((int)instance.value(this.m_AttIndex.getIndex()))) {
        instance.setValue(this.m_AttIndex.getIndex(), 1.0D);
      } else {
        instance.setValue(this.m_AttIndex.getIndex(), 0.0D);
      }  
    push(instance);
    return true;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tSets the attribute index.", "C", 1, "-C <col>"));
    vector.addElement(new Option("\tSpecify the list of values to indicate. First and last are\n\tvalid indexes (default last)", "V", 1, "-V <index1,index2-index4,...>"));
    vector.addElement(new Option("\tSet if new boolean attribute nominal.", "N", 0, "-N <index>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('C', paramArrayOfString);
    if (str1.length() != 0) {
      setAttributeIndex(str1);
    } else {
      setAttributeIndex("last");
    } 
    String str2 = Utils.getOption('V', paramArrayOfString);
    if (str2.length() != 0) {
      setValueIndices(str2);
    } else {
      setValueIndices("last");
    } 
    setNumeric(!Utils.getFlag('N', paramArrayOfString));
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[5];
    byte b = 0;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + getAttributeIndex();
    arrayOfString[b++] = "-V";
    arrayOfString[b++] = getValueIndices();
    if (!getNumeric())
      arrayOfString[b++] = "-N"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String globalInfo() {
    return "A filter that creates a new dataset with a boolean attribute replacing a nominal attribute.  In the new dataset, a value of 1 is assigned to an instance that exhibits a particular range of attribute values, a 0 to an instance that doesn't. The boolean attribute is coded as numeric by default.";
  }
  
  public String attributeIndexTipText() {
    return "Sets which attribute should be replaced by the indicator. This attribute must be nominal.";
  }
  
  public String getAttributeIndex() {
    return this.m_AttIndex.getSingleIndex();
  }
  
  public void setAttributeIndex(String paramString) {
    this.m_AttIndex.setSingleIndex(paramString);
  }
  
  public Range getValueRange() {
    return this.m_ValIndex;
  }
  
  public String valueIndicesTipText() {
    return "Specify range of nominal values to act on. This is a comma separated list of attribute indices (numbered from 1), with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
  }
  
  public String getValueIndices() {
    return this.m_ValIndex.getRanges();
  }
  
  public void setValueIndices(String paramString) {
    this.m_ValIndex.setRanges(paramString);
  }
  
  public void setValueIndex(int paramInt) {
    setValueIndices("" + (paramInt + 1));
  }
  
  public void setValueIndicesArray(int[] paramArrayOfint) {
    setValueIndices(Range.indicesToRangeList(paramArrayOfint));
  }
  
  public String numericTipText() {
    return "Determines whether the output indicator attribute is numeric. If this is set to false, the output attribute will be nominal.";
  }
  
  public void setNumeric(boolean paramBoolean) {
    this.m_Numeric = paramBoolean;
  }
  
  public boolean getNumeric() {
    return this.m_Numeric;
  }
  
  private void setOutputFormat() {
    FastVector fastVector = new FastVector(getInputFormat().numAttributes());
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (b != this.m_AttIndex.getIndex()) {
        fastVector.addElement(attribute);
      } else if (this.m_Numeric) {
        fastVector.addElement(new Attribute(attribute.name()));
      } else {
        String str;
        int[] arrayOfInt = this.m_ValIndex.getSelection();
        if (arrayOfInt.length == 1) {
          str = attribute.value(arrayOfInt[0]);
        } else {
          str = this.m_ValIndex.getRanges().replace(',', '_');
        } 
        FastVector fastVector1 = new FastVector(2);
        fastVector1.addElement("neg_" + str);
        fastVector1.addElement("pos_" + str);
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
        Filter.batchFilterFile(new MakeIndicator(), paramArrayOfString);
      } else {
        Filter.filterFile(new MakeIndicator(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\MakeIndicator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */