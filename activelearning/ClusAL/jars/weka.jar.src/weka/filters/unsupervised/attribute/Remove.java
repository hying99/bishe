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
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class Remove extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  protected Range m_SelectCols = new Range();
  
  protected int[] m_SelectedAttributes;
  
  protected int[] m_InputStringIndex;
  
  public Remove() {
    this.m_SelectCols.setInvert(true);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tSpecify list of columns to delete. First and last are valid\n\tindexes. (default none)", "R", 1, "-R <index1,index2-index4,...>"));
    vector.addElement(new Option("\tInvert matching sense (i.e. only keep specified columns)", "V", 0, "-V"));
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
    this.m_SelectCols.setUpper(paramInstances.numAttributes() - 1);
    FastVector fastVector = new FastVector();
    int i = -1;
    this.m_SelectedAttributes = this.m_SelectCols.getSelection();
    byte b1 = 0;
    int[] arrayOfInt = new int[this.m_SelectedAttributes.length];
    for (byte b2 = 0; b2 < this.m_SelectedAttributes.length; b2++) {
      int j = this.m_SelectedAttributes[b2];
      if (paramInstances.classIndex() == j)
        i = fastVector.size(); 
      Attribute attribute = (Attribute)paramInstances.attribute(j).copy();
      if (attribute.type() == 2)
        arrayOfInt[b1++] = j; 
      fastVector.addElement(attribute);
    } 
    this.m_InputStringIndex = new int[b1];
    System.arraycopy(arrayOfInt, 0, this.m_InputStringIndex, 0, b1);
    Instances instances = new Instances(paramInstances.relationName(), fastVector, 0);
    instances.setClassIndex(i);
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
    if (getOutputFormat().numAttributes() == 0)
      return false; 
    double[] arrayOfDouble = new double[getOutputFormat().numAttributes()];
    for (byte b = 0; b < this.m_SelectedAttributes.length; b++) {
      int i = this.m_SelectedAttributes[b];
      arrayOfDouble[b] = paramInstance.value(i);
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
    return "An instance filter that removes a range of attributes from the dataset.";
  }
  
  public String invertSelectionTipText() {
    return "Determines whether action is to select or delete. If set to true, only the specified attributes will be kept; If set to false, specified attributes will be deleted.";
  }
  
  public boolean getInvertSelection() {
    return !this.m_SelectCols.getInvert();
  }
  
  public void setInvertSelection(boolean paramBoolean) {
    this.m_SelectCols.setInvert(!paramBoolean);
  }
  
  public String attributeIndicesTipText() {
    return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
  }
  
  public String getAttributeIndices() {
    return this.m_SelectCols.getRanges();
  }
  
  public void setAttributeIndices(String paramString) {
    this.m_SelectCols.setRanges(paramString);
  }
  
  public void setAttributeIndicesArray(int[] paramArrayOfint) {
    setAttributeIndices(Range.indicesToRangeList(paramArrayOfint));
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new Remove(), paramArrayOfString);
      } else {
        Filter.filterFile(new Remove(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\Remove.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */