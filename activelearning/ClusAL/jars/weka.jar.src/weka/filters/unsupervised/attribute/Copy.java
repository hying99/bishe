package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class Copy extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  protected Range m_CopyCols = new Range();
  
  protected int[] m_SelectedAttributes;
  
  protected int[] m_InputStringIndex;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tSpecify list of columns to copy. First and last are valid\n\tindexes. (default none)", "R", 1, "-R <index1,index2-index4,...>"));
    vector.addElement(new Option("\tInvert matching sense (i.e. copy all non-specified columns)", "V", 0, "-V"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('R', paramArrayOfString);
    if (str.length() != 0)
      setAttributeIndices(str); 
    setInvertSelection(Utils.getFlag('V', paramArrayOfString));
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[3];
    byte b = 0;
    if (getInvertSelection())
      arrayOfString[b++] = "-V"; 
    if (!getAttributeIndices().equals("")) {
      arrayOfString[b++] = "-R";
      arrayOfString[b++] = getAttributeIndices();
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_CopyCols.setUpper(paramInstances.numAttributes() - 1);
    Instances instances = new Instances(paramInstances, 0);
    this.m_SelectedAttributes = this.m_CopyCols.getSelection();
    byte b1 = 0;
    int[] arrayOfInt1 = new int[this.m_SelectedAttributes.length];
    for (byte b2 = 0; b2 < this.m_SelectedAttributes.length; b2++) {
      int i = this.m_SelectedAttributes[b2];
      Attribute attribute = paramInstances.attribute(i);
      instances.insertAttributeAt((Attribute)attribute.copy(), instances.numAttributes());
      instances.renameAttribute(instances.numAttributes() - 1, "Copy of " + attribute.name());
      if (attribute.type() == 2)
        arrayOfInt1[b1++] = i; 
    } 
    int[] arrayOfInt2 = getInputStringIndex();
    this.m_InputStringIndex = new int[arrayOfInt2.length + b1];
    System.arraycopy(arrayOfInt2, 0, this.m_InputStringIndex, 0, arrayOfInt2.length);
    System.arraycopy(arrayOfInt1, 0, this.m_InputStringIndex, arrayOfInt2.length, b1);
    setOutputFormat(instances);
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    Instance instance;
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    double[] arrayOfDouble = new double[outputFormatPeek().numAttributes()];
    int i;
    for (i = 0; i < getInputFormat().numAttributes(); i++)
      arrayOfDouble[i] = paramInstance.value(i); 
    i = getInputFormat().numAttributes();
    for (byte b = 0; b < this.m_SelectedAttributes.length; b++) {
      int j = this.m_SelectedAttributes[b];
      arrayOfDouble[b + i] = paramInstance.value(j);
    } 
    SparseInstance sparseInstance = null;
    if (paramInstance instanceof SparseInstance) {
      sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble);
    } else {
      instance = new Instance(paramInstance.weight(), arrayOfDouble);
    } 
    copyStringValues(instance, false, paramInstance.dataset(), this.m_InputStringIndex, getOutputFormat(), getOutputStringIndex());
    instance.setDataset(getOutputFormat());
    push(instance);
    return true;
  }
  
  public String globalInfo() {
    return "An instance filter that copies a range of attributes in the dataset. This is used in conjunction with other filters that overwrite attribute values during the course of their operation -- this filter allows the original attributes to be kept as well as the new attributes.";
  }
  
  public String invertSelectionTipText() {
    return "Sets copy selected vs unselected action. If set to false, only the specified attributes will be copied; If set to true, non-specified attributes will be copied.";
  }
  
  public boolean getInvertSelection() {
    return this.m_CopyCols.getInvert();
  }
  
  public void setInvertSelection(boolean paramBoolean) {
    this.m_CopyCols.setInvert(paramBoolean);
  }
  
  public String getAttributeIndices() {
    return this.m_CopyCols.getRanges();
  }
  
  public String attributeIndicesTipText() {
    return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
  }
  
  public void setAttributeIndices(String paramString) throws Exception {
    this.m_CopyCols.setRanges(paramString);
  }
  
  public void setAttributeIndicesArray(int[] paramArrayOfint) throws Exception {
    setAttributeIndices(Range.indicesToRangeList(paramArrayOfint));
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new Copy(), paramArrayOfString);
      } else {
        Filter.filterFile(new Copy(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\Copy.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */