package weka.filters.unsupervised.instance;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class Resample extends Filter implements UnsupervisedFilter, OptionHandler {
  private double m_SampleSizePercent = 100.0D;
  
  private int m_RandomSeed = 1;
  
  private boolean m_FirstBatchDone = false;
  
  public String globalInfo() {
    return "Produces a random subsample of a dataset using sampling withreplacement. The original dataset must fit entirely in memory. The number of instances in the generated dataset may be specified.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tSpecify the random number seed (default 1)", "S", 1, "-S <num>"));
    vector.addElement(new Option("\tThe size of the output dataset, as a percentage of\n\tthe input dataset (default 100)", "Z", 1, "-Z <num>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('S', paramArrayOfString);
    if (str1.length() != 0) {
      setRandomSeed(Integer.parseInt(str1));
    } else {
      setRandomSeed(1);
    } 
    String str2 = Utils.getOption('Z', paramArrayOfString);
    if (str2.length() != 0) {
      setSampleSizePercent(Double.valueOf(str2).doubleValue());
    } else {
      setSampleSizePercent(100.0D);
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[6];
    byte b = 0;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getRandomSeed();
    arrayOfString[b++] = "-Z";
    arrayOfString[b++] = "" + getSampleSizePercent();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String randomSeedTipText() {
    return "The seed used for random sampling.";
  }
  
  public int getRandomSeed() {
    return this.m_RandomSeed;
  }
  
  public void setRandomSeed(int paramInt) {
    this.m_RandomSeed = paramInt;
  }
  
  public String sampleSizePercentTipText() {
    return "Size of the subsample as a percentage of the original dataset.";
  }
  
  public double getSampleSizePercent() {
    return this.m_SampleSizePercent;
  }
  
  public void setSampleSizePercent(double paramDouble) {
    this.m_SampleSizePercent = paramDouble;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    this.m_FirstBatchDone = false;
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (this.m_FirstBatchDone) {
      push(paramInstance);
      return true;
    } 
    bufferInput(paramInstance);
    return false;
  }
  
  public boolean batchFinished() {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (!this.m_FirstBatchDone)
      createSubsample(); 
    flushInput();
    this.m_NewBatch = true;
    this.m_FirstBatchDone = true;
    return (numPendingOutput() != 0);
  }
  
  private void createSubsample() {
    int i = getInputFormat().numInstances();
    int j = (int)(i * this.m_SampleSizePercent / 100.0D);
    Random random = new Random(this.m_RandomSeed);
    for (byte b = 0; b < j; b++) {
      int k = random.nextInt(i);
      push((Instance)getInputFormat().instance(k).copy());
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new Resample(), paramArrayOfString);
      } else {
        Filter.filterFile(new Resample(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\instance\Resample.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */