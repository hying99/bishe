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
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class Add extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  protected int m_AttributeType = 0;
  
  protected String m_Name = "unnamed";
  
  private SingleIndex m_Insert = new SingleIndex("last");
  
  protected FastVector m_Labels = new FastVector(5);
  
  public String globalInfo() {
    return "An instance filter that adds a new attribute to the dataset. The new attribute will contain all missing values.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tSpecify where to insert the column. First and last\n\tare valid indexes.(default last)", "C", 1, "-C <index>"));
    vector.addElement(new Option("\tCreate nominal attribute with given labels\n\t(default numeric attribute)", "L", 1, "-L <label1,label2,...>"));
    vector.addElement(new Option("\tName of the new attribute.\n\t(default = 'Unnamed')", "N", 1, "-N <name>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setAttributeIndex(Utils.getOption('C', paramArrayOfString));
    setNominalLabels(Utils.getOption('L', paramArrayOfString));
    setAttributeName(Utils.getOption('N', paramArrayOfString));
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[6];
    byte b = 0;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = getAttributeName();
    if (this.m_AttributeType == 1) {
      arrayOfString[b++] = "-L";
      arrayOfString[b++] = getNominalLabels();
    } 
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + getAttributeIndex();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_Insert.setUpper(paramInstances.numAttributes());
    Instances instances = new Instances(paramInstances, 0);
    Attribute attribute = null;
    switch (this.m_AttributeType) {
      case 0:
        attribute = new Attribute(this.m_Name);
        break;
      case 1:
        attribute = new Attribute(this.m_Name, this.m_Labels);
        break;
      default:
        throw new IllegalArgumentException("Unknown attribute type in Add");
    } 
    if (this.m_Insert.getIndex() < 0 || this.m_Insert.getIndex() > getInputFormat().numAttributes())
      throw new IllegalArgumentException("Index out of range"); 
    instances.insertAttributeAt(attribute, this.m_Insert.getIndex());
    setOutputFormat(instances);
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
    copyStringValues(instance, true, instance.dataset(), getOutputFormat());
    instance.setDataset(null);
    instance.insertAttributeAt(this.m_Insert.getIndex());
    instance.setDataset(getOutputFormat());
    push(instance);
    return true;
  }
  
  public String attributeNameTipText() {
    return "Set the new attribute's name.";
  }
  
  public String getAttributeName() {
    return this.m_Name;
  }
  
  public void setAttributeName(String paramString) {
    String str = paramString.trim();
    if (str.indexOf(' ') >= 0) {
      if (str.indexOf('\'') != 0)
        str = str.replace('\'', ' '); 
      str = '\'' + str + '\'';
    } 
    if (str.equals(""))
      str = "unnamed"; 
    this.m_Name = str;
  }
  
  public String attributeIndexTipText() {
    return "The position (starting from 1) where the attribute will be inserted (first and last are valid indices).";
  }
  
  public String getAttributeIndex() {
    return this.m_Insert.getSingleIndex();
  }
  
  public void setAttributeIndex(String paramString) {
    this.m_Insert.setSingleIndex(paramString);
  }
  
  public String nominalLabelsTipText() {
    return "The list of value labels (nominal attribute creation only).  The list must be comma-separated, eg: \"red,green,blue\". If this is empty, the created attribute will be numeric.";
  }
  
  public String getNominalLabels() {
    String str = "";
    for (byte b = 0; b < this.m_Labels.size(); b++) {
      if (b == 0) {
        str = (String)this.m_Labels.elementAt(b);
      } else {
        str = str + "," + (String)this.m_Labels.elementAt(b);
      } 
    } 
    return str;
  }
  
  public void setNominalLabels(String paramString) {
    FastVector fastVector = new FastVector(10);
    int i;
    while ((i = paramString.indexOf(',')) >= 0) {
      String str1 = paramString.substring(0, i).trim();
      if (!str1.equals("")) {
        fastVector.addElement(str1);
      } else {
        throw new IllegalArgumentException("Invalid label list at " + paramString.substring(i));
      } 
      paramString = paramString.substring(i + 1);
    } 
    String str = paramString.trim();
    if (!str.equals(""))
      fastVector.addElement(str); 
    this.m_Labels = fastVector;
    if (fastVector.size() == 0) {
      this.m_AttributeType = 0;
    } else {
      this.m_AttributeType = 1;
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new Add(), paramArrayOfString);
      } else {
        Filter.filterFile(new Add(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\Add.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */