package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class RemoveUseless extends Filter implements UnsupervisedFilter, OptionHandler {
  protected Remove m_removeFilter = null;
  
  protected double m_maxVariancePercentage = 99.0D;
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_removeFilter = null;
    return false;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (this.m_removeFilter != null) {
      this.m_removeFilter.input(paramInstance);
      Instance instance = this.m_removeFilter.output();
      instance.setDataset(getOutputFormat());
      push(instance);
      return true;
    } 
    bufferInput(paramInstance);
    return false;
  }
  
  public boolean batchFinished() throws Exception {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_removeFilter == null) {
      Instances instances1 = getInputFormat();
      int[] arrayOfInt1 = new int[instances1.numAttributes()];
      byte b1 = 0;
      for (byte b2 = 0; b2 < instances1.numAttributes(); b2++) {
        if (b2 != instances1.classIndex()) {
          AttributeStats attributeStats = instances1.attributeStats(b2);
          if (attributeStats.distinctCount < 2) {
            arrayOfInt1[b1++] = b2;
          } else if (instances1.attribute(b2).isNominal()) {
            double d = attributeStats.distinctCount / attributeStats.totalCount * 100.0D;
            if (d > this.m_maxVariancePercentage)
              arrayOfInt1[b1++] = b2; 
          } 
        } 
      } 
      int[] arrayOfInt2 = new int[b1];
      System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, b1);
      this.m_removeFilter = new Remove();
      this.m_removeFilter.setAttributeIndicesArray(arrayOfInt2);
      this.m_removeFilter.setInvertSelection(false);
      this.m_removeFilter.setInputFormat(instances1);
      for (byte b3 = 0; b3 < instances1.numInstances(); b3++)
        this.m_removeFilter.input(instances1.instance(b3)); 
      this.m_removeFilter.batchFinished();
      Instances instances2 = this.m_removeFilter.getOutputFormat();
      instances2.setRelationName(instances1.relationName());
      setOutputFormat(instances2);
      Instance instance;
      while ((instance = this.m_removeFilter.output()) != null) {
        instance.setDataset(instances2);
        push(instance);
      } 
    } 
    flushInput();
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tMaximum variance percentage allowed (default 99)", "M", 1, "-M <max variance %>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('M', paramArrayOfString);
    if (str.length() != 0) {
      setMaximumVariancePercentageAllowed((int)Double.valueOf(str).doubleValue());
    } else {
      setMaximumVariancePercentageAllowed(99.0D);
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[2];
    byte b = 0;
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getMaximumVariancePercentageAllowed();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String globalInfo() {
    return "Removes constant attributes, along with nominal attributes that vary too much.";
  }
  
  public String maximumVariancePercentageAllowedTipText() {
    return "Set the threshold for the highest variance allowed before a nominal attribute will be deleted.Specifically, if (number_of_distinct_values / total_number_of_values * 100) is greater than this value then the attribute will be removed.";
  }
  
  public void setMaximumVariancePercentageAllowed(double paramDouble) {
    this.m_maxVariancePercentage = paramDouble;
  }
  
  public double getMaximumVariancePercentageAllowed() {
    return this.m_maxVariancePercentage;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new RemoveUseless(), paramArrayOfString);
      } else {
        Filter.filterFile(new RemoveUseless(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\RemoveUseless.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */