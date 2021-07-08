package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.filters.Filter;

public class PKIDiscretize extends Discretize {
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    this.m_FindNumBins = true;
    return super.setInputFormat(paramInstances);
  }
  
  protected void findNumBins(int paramInt) {
    Instances instances = getInputFormat();
    int i = instances.numInstances();
    for (byte b = 0; b < instances.numInstances(); b++) {
      if (instances.instance(b).isMissing(paramInt))
        i--; 
    } 
    this.m_NumBins = (int)Math.sqrt(i);
    if (this.m_NumBins > 0)
      calculateCutPointsByEqualFrequencyBinning(paramInt); 
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(7);
    vector.addElement(new Option("\tSpecifies list of columns to Discretize. First and last are valid indexes.\n\t(default: first-last)", "R", 1, "-R <col1,col2-col4,...>"));
    vector.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
    vector.addElement(new Option("\tOutput binary attributes for discretized attributes.", "D", 0, "-D"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setMakeBinary(Utils.getFlag('D', paramArrayOfString));
    setInvertSelection(Utils.getFlag('V', paramArrayOfString));
    String str = Utils.getOption('R', paramArrayOfString);
    if (str.length() != 0) {
      setAttributeIndices(str);
    } else {
      setAttributeIndices("first-last");
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[12];
    byte b = 0;
    if (getMakeBinary())
      arrayOfString[b++] = "-D"; 
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
  
  public String globalInfo() {
    return "Discretizes numeric attributes using equal frequency binning, where the number of bins is equal to the square root of the number of non-missing values.";
  }
  
  public String findNumBinsTipText() {
    return "Ignored.";
  }
  
  public boolean getFindNumBins() {
    return false;
  }
  
  public void setFindNumBins(boolean paramBoolean) {}
  
  public String useEqualFrequencyTipText() {
    return "Always true.";
  }
  
  public boolean getUseEqualFrequency() {
    return true;
  }
  
  public void setUseEqualFrequency(boolean paramBoolean) {}
  
  public String binsTipText() {
    return "Ignored.";
  }
  
  public int getBins() {
    return 0;
  }
  
  public void setBins(int paramInt) {}
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new PKIDiscretize(), paramArrayOfString);
      } else {
        Filter.filterFile(new PKIDiscretize(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\PKIDiscretize.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */