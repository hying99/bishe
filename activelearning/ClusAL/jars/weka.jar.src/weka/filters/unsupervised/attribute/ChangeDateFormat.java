package weka.filters.unsupervised.attribute;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class ChangeDateFormat extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  private static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  
  private SingleIndex m_AttIndex = new SingleIndex("last");
  
  private SimpleDateFormat m_DateFormat = DEFAULT_FORMAT;
  
  private Attribute m_OutputAttribute;
  
  public String globalInfo() {
    return "Changes the format used by a date attribute.";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_AttIndex.setUpper(paramInstances.numAttributes() - 1);
    if (!paramInstances.attribute(this.m_AttIndex.getIndex()).isDate())
      throw new UnsupportedAttributeTypeException("Chosen attribute not date."); 
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
    int i = this.m_AttIndex.getIndex();
    if (!instance.isMissing(i)) {
      double d = paramInstance.value(i);
      try {
        d = this.m_OutputAttribute.parseDate(this.m_OutputAttribute.formatDate(d));
      } catch (ParseException parseException) {
        throw new RuntimeException("Output date format couldn't parse its own output!!");
      } 
      instance.setValue(i, d);
    } 
    push(instance);
    return true;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tSets the attribute index (default last).", "C", 1, "-C <col>"));
    vector.addElement(new Option("\tSets the output date format string (default corresponds to ISO-8601).", "F", 1, "-F <value index>"));
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
      setDateFormat(str2);
    } else {
      setDateFormat(DEFAULT_FORMAT);
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[4];
    byte b = 0;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + getAttributeIndex();
    arrayOfString[b++] = "-F";
    arrayOfString[b++] = "" + getDateFormat().toPattern();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String attributeIndexTipText() {
    return "Sets which attribute to process. This attribute must be of type date (\"first\" and \"last\" are valid values)";
  }
  
  public String getAttributeIndex() {
    return this.m_AttIndex.getSingleIndex();
  }
  
  public void setAttributeIndex(String paramString) {
    this.m_AttIndex.setSingleIndex(paramString);
  }
  
  public String dateFormatTipText() {
    return "The date format to change to. This should be a format understood by Java's SimpleDateFormat class.";
  }
  
  public SimpleDateFormat getDateFormat() {
    return this.m_DateFormat;
  }
  
  public void setDateFormat(String paramString) {
    setDateFormat(new SimpleDateFormat(paramString));
  }
  
  public void setDateFormat(SimpleDateFormat paramSimpleDateFormat) {
    if (paramSimpleDateFormat == null)
      throw new NullPointerException(); 
    this.m_DateFormat = paramSimpleDateFormat;
  }
  
  private void setOutputFormat() {
    FastVector fastVector = new FastVector(getInputFormat().numAttributes());
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (b == this.m_AttIndex.getIndex()) {
        fastVector.addElement(new Attribute(attribute.name(), getDateFormat().toPattern()));
      } else {
        fastVector.addElement(attribute.copy());
      } 
    } 
    Instances instances = new Instances(getInputFormat().relationName(), fastVector, 0);
    instances.setClassIndex(getInputFormat().classIndex());
    this.m_OutputAttribute = instances.attribute(this.m_AttIndex.getIndex());
    setOutputFormat(instances);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new ChangeDateFormat(), paramArrayOfString);
      } else {
        Filter.filterFile(new ChangeDateFormat(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\ChangeDateFormat.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */