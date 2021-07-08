package weka.filters.unsupervised.instance;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class RemovePercentage extends Filter implements UnsupervisedFilter, OptionHandler {
  private int m_Percentage = 50;
  
  private boolean m_Inverse = false;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tSpecifies percentage of instances to select. (default 50)\n", "P", 1, "-P <percentage>"));
    vector.addElement(new Option("\tSpecifies if inverse of selection is to be output.\n", "V", 0, "-V"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0) {
      setPercentage(Integer.parseInt(str));
    } else {
      setPercentage(50);
    } 
    setInvertSelection(Utils.getFlag('V', paramArrayOfString));
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[5];
    byte b = 0;
    arrayOfString[b++] = "-P";
    arrayOfString[b++] = "" + getPercentage();
    if (getInvertSelection())
      arrayOfString[b++] = "-V"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String globalInfo() {
    return "A filter that removes a given percentage of a dataset.";
  }
  
  public String percentageTipText() {
    return "The percentage of the data to select.";
  }
  
  public int getPercentage() {
    return this.m_Percentage;
  }
  
  public void setPercentage(int paramInt) {
    if (paramInt < 0 || paramInt > 100)
      throw new IllegalArgumentException("Percentage must be between 0 and 100."); 
    this.m_Percentage = paramInt;
  }
  
  public String invertSelectionTipText() {
    return "Whether to invert the selection.";
  }
  
  public boolean getInvertSelection() {
    return this.m_Inverse;
  }
  
  public void setInvertSelection(boolean paramBoolean) {
    this.m_Inverse = paramBoolean;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    return true;
  }
  
  public boolean batchFinished() {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    Instances instances = getInputFormat();
    int i = instances.numInstances() * this.m_Percentage / 100;
    if (this.m_Inverse) {
      for (byte b = 0; b < i; b++)
        push(instances.instance(b)); 
    } else {
      for (int j = i; j < instances.numInstances(); j++)
        push(instances.instance(j)); 
    } 
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new RemovePercentage(), paramArrayOfString);
      } else {
        Filter.filterFile(new RemovePercentage(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\instance\RemovePercentage.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */