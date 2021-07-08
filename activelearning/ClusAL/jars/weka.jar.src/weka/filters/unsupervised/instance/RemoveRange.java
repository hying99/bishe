package weka.filters.unsupervised.instance;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class RemoveRange extends Filter implements UnsupervisedFilter, OptionHandler {
  private Range m_Range = new Range("first-last");
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tSpecifies list of instances to select. First and last\n\tare valid indexes. (required)\n", "R", 1, "-R <inst1,inst2-inst4,...>"));
    vector.addElement(new Option("\tSpecifies if inverse of selection is to be output.\n", "V", 0, "-V"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('R', paramArrayOfString);
    if (str.length() != 0) {
      setInstancesIndices(str);
    } else {
      setInstancesIndices("first-last");
    } 
    setInvertSelection(Utils.getFlag('V', paramArrayOfString));
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[8];
    byte b = 0;
    if (getInvertSelection())
      arrayOfString[b++] = "-V"; 
    arrayOfString[b++] = "-R";
    arrayOfString[b++] = getInstancesIndices();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String globalInfo() {
    return "A filter that removes a given range of instances of a dataset.";
  }
  
  public String instancesIndicesTipText() {
    return "The range of instances to select. First and last are valid indexes.";
  }
  
  public String getInstancesIndices() {
    return this.m_Range.getRanges();
  }
  
  public void setInstancesIndices(String paramString) {
    this.m_Range.setRanges(paramString);
  }
  
  public String invertSelectionTipText() {
    return "Whether to invert the selection.";
  }
  
  public boolean getInvertSelection() {
    return this.m_Range.getInvert();
  }
  
  public void setInvertSelection(boolean paramBoolean) {
    this.m_Range.setInvert(paramBoolean);
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    return true;
  }
  
  public boolean batchFinished() {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    this.m_Range.setUpper(getInputFormat().numInstances() - 1);
    for (byte b = 0; b < getInputFormat().numInstances(); b++) {
      if (!this.m_Range.isInRange(b))
        push(getInputFormat().instance(b)); 
    } 
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new RemoveRange(), paramArrayOfString);
      } else {
        Filter.filterFile(new RemoveRange(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\instance\RemoveRange.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */