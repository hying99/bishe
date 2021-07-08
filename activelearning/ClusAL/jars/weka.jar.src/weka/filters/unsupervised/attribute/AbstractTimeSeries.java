package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Queue;
import weka.core.Range;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public abstract class AbstractTimeSeries extends Filter implements UnsupervisedFilter, OptionHandler {
  protected Range m_SelectedCols = new Range();
  
  protected boolean m_FillWithMissing;
  
  protected int m_InstanceRange = -1;
  
  protected Queue m_History;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tSpecify list of columns to translate in time. First and\n\tlast are valid indexes. (default none)", "R", 1, "-R <index1,index2-index4,...>"));
    vector.addElement(new Option("\tInvert matching sense (i.e. calculate for all non-specified columns)", "V", 0, "-V"));
    vector.addElement(new Option("\tThe number of instances forward to translate values\n\tbetween. A negative number indicates taking values from\n\ta past instance. (default -1)", "I", 1, "-I <num>"));
    vector.addElement(new Option("\tFor instances at the beginning or end of the dataset where\n\tthe translated values are not known, use missing values\n\t(default is to remove those instances).", "M", 0, "-M"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('R', paramArrayOfString);
    if (str1.length() != 0) {
      setAttributeIndices(str1);
    } else {
      setAttributeIndices("");
    } 
    setInvertSelection(Utils.getFlag('V', paramArrayOfString));
    setFillWithMissing(Utils.getFlag('M', paramArrayOfString));
    String str2 = Utils.getOption('I', paramArrayOfString);
    if (str2.length() != 0) {
      setInstanceRange(Integer.parseInt(str2));
    } else {
      setInstanceRange(-1);
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[6];
    byte b = 0;
    if (!getAttributeIndices().equals("")) {
      arrayOfString[b++] = "-R";
      arrayOfString[b++] = getAttributeIndices();
    } 
    if (getInvertSelection())
      arrayOfString[b++] = "-V"; 
    arrayOfString[b++] = "-I";
    arrayOfString[b++] = "" + getInstanceRange();
    if (getFillWithMissing())
      arrayOfString[b++] = "-M"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    resetHistory();
    this.m_SelectedCols.setUpper(paramInstances.numAttributes() - 1);
    return false;
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    if (getInputFormat() == null)
      throw new NullPointerException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
      resetHistory();
    } 
    Instance instance = historyInput(paramInstance);
    if (instance != null) {
      push(instance);
      return true;
    } 
    return false;
  }
  
  public boolean batchFinished() {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (getFillWithMissing() && this.m_InstanceRange > 0)
      while (!this.m_History.empty())
        push(mergeInstances((Instance)null, (Instance)this.m_History.pop()));  
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public String fillWithMissingTipText() {
    return "For instances at the beginning or end of the dataset where the translated values are not known, use missing values (default is to remove those instances)";
  }
  
  public boolean getFillWithMissing() {
    return this.m_FillWithMissing;
  }
  
  public void setFillWithMissing(boolean paramBoolean) {
    this.m_FillWithMissing = paramBoolean;
  }
  
  public String instanceRangeTipText() {
    return "The number of instances forward/backward to merge values between. A negative number indicates taking values from a past instance.";
  }
  
  public int getInstanceRange() {
    return this.m_InstanceRange;
  }
  
  public void setInstanceRange(int paramInt) {
    this.m_InstanceRange = paramInt;
  }
  
  public String invertSelectionTipText() {
    return "Invert matching sense. ie calculate for all non-specified columns.";
  }
  
  public boolean getInvertSelection() {
    return this.m_SelectedCols.getInvert();
  }
  
  public void setInvertSelection(boolean paramBoolean) {
    this.m_SelectedCols.setInvert(paramBoolean);
  }
  
  public String attributeIndicesTipText() {
    return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
  }
  
  public String getAttributeIndices() {
    return this.m_SelectedCols.getRanges();
  }
  
  public void setAttributeIndices(String paramString) {
    this.m_SelectedCols.setRanges(paramString);
  }
  
  public void setAttributeIndicesArray(int[] paramArrayOfint) {
    setAttributeIndices(Range.indicesToRangeList(paramArrayOfint));
  }
  
  protected void resetHistory() {
    if (this.m_History == null) {
      this.m_History = new Queue();
    } else {
      this.m_History.removeAllElements();
    } 
  }
  
  protected Instance historyInput(Instance paramInstance) {
    this.m_History.push(paramInstance);
    return (this.m_History.size() <= Math.abs(this.m_InstanceRange)) ? ((getFillWithMissing() && this.m_InstanceRange < 0) ? mergeInstances((Instance)null, paramInstance) : null) : ((this.m_InstanceRange < 0) ? mergeInstances((Instance)this.m_History.pop(), paramInstance) : mergeInstances(paramInstance, (Instance)this.m_History.pop()));
  }
  
  protected abstract Instance mergeInstances(Instance paramInstance1, Instance paramInstance2);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\AbstractTimeSeries.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */