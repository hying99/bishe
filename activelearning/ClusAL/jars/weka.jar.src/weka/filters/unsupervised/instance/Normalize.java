package weka.filters.unsupervised.instance;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class Normalize extends Filter implements UnsupervisedFilter, OptionHandler {
  protected double m_Norm = 1.0D;
  
  protected double m_LNorm = 2.0D;
  
  public String globalInfo() {
    return "An instance filter that normalize instances considering only numeric attributes and ignoring class index.";
  }
  
  public String LNormTipText() {
    return "The LNorm to use.";
  }
  
  public String normTipText() {
    return "The norm of the instances after normalization.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tSpecify the norm that each instance must have (default 1.0)", "N", 1, "-N <num>"));
    vector.addElement(new Option("\tSpecify L-norm to use (default 2.0)", "L", 1, "-L <num>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('N', paramArrayOfString);
    if (str1.length() != 0) {
      setNorm(Double.parseDouble(str1));
    } else {
      setNorm(1.0D);
    } 
    String str2 = Utils.getOption('L', paramArrayOfString);
    if (str2.length() != 0) {
      setLNorm(Double.parseDouble(str2));
    } else {
      setLNorm(2.0D);
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[4];
    byte b = 0;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + getNorm();
    arrayOfString[b++] = "-L";
    arrayOfString[b++] = "" + getLNorm();
    return arrayOfString;
  }
  
  public double getNorm() {
    return this.m_Norm;
  }
  
  public void setNorm(double paramDouble) {
    this.m_Norm = paramDouble;
  }
  
  public double getLNorm() {
    return this.m_LNorm;
  }
  
  public void setLNorm(double paramDouble) {
    this.m_LNorm = paramDouble;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    return true;
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    Instance instance = (Instance)paramInstance.copy();
    double d = 0.0D;
    byte b;
    for (b = 0; b < getInputFormat().numAttributes(); b++) {
      if (getInputFormat().classIndex() != b && getInputFormat().attribute(b).isNumeric())
        d += Math.pow(Math.abs(instance.value(b)), getLNorm()); 
    } 
    d = Math.pow(d, 1.0D / getLNorm());
    for (b = 0; b < getInputFormat().numAttributes(); b++) {
      if (getInputFormat().classIndex() != b && getInputFormat().attribute(b).isNumeric())
        instance.setValue(b, instance.value(b) / d * getNorm()); 
    } 
    push(instance);
    return true;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new Normalize(), paramArrayOfString);
      } else {
        Filter.filterFile(new Normalize(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\instance\Normalize.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */