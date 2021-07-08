package weka.filters.supervised.instance;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;

public class Resample extends Filter implements SupervisedFilter, OptionHandler {
  private double m_SampleSizePercent = 100.0D;
  
  private int m_RandomSeed = 1;
  
  private double m_BiasToUniformClass = 0.0D;
  
  private boolean m_FirstBatchDone = false;
  
  public String globalInfo() {
    return "Produces a random subsample of a dataset using sampling with replacement.The original dataset must fit entirely in memory. The number of instances in the generated dataset may be specified. The dataset must have a nominal class attribute. If not, use the unsupervised version. The filter can be made to maintain the class distribution in the subsample, or to bias the class distribution toward a uniform distribution. When used in batch mode (i.e. in the FilteredClassifier), subsequent batches are NOTE resampled.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tSpecify the random number seed (default 1)", "S", 1, "-S <num>"));
    vector.addElement(new Option("\tThe size of the output dataset, as a percentage of\n\tthe input dataset (default 100)", "Z", 1, "-Z <num>"));
    vector.addElement(new Option("\tBias factor towards uniform class distribution.\n\t0 = distribution in input data -- 1 = uniform distribution.\n\t(default 0)", "B", 1, "-B <num>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('S', paramArrayOfString);
    if (str1.length() != 0) {
      setRandomSeed(Integer.parseInt(str1));
    } else {
      setRandomSeed(1);
    } 
    String str2 = Utils.getOption('B', paramArrayOfString);
    if (str2.length() != 0) {
      setBiasToUniformClass(Double.valueOf(str2).doubleValue());
    } else {
      setBiasToUniformClass(0.0D);
    } 
    String str3 = Utils.getOption('Z', paramArrayOfString);
    if (str3.length() != 0) {
      setSampleSizePercent(Double.valueOf(str3).doubleValue());
    } else {
      setSampleSizePercent(100.0D);
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[6];
    byte b = 0;
    arrayOfString[b++] = "-B";
    arrayOfString[b++] = "" + getBiasToUniformClass();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getRandomSeed();
    arrayOfString[b++] = "-Z";
    arrayOfString[b++] = "" + getSampleSizePercent();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String biasToUniformClassTipText() {
    return "Whether to use bias towards a uniform class. A value of 0 leaves the class distribution as-is, a value of 1 ensures the class distribution is uniform in the output data.";
  }
  
  public double getBiasToUniformClass() {
    return this.m_BiasToUniformClass;
  }
  
  public void setBiasToUniformClass(double paramDouble) {
    this.m_BiasToUniformClass = paramDouble;
  }
  
  public String randomSeedTipText() {
    return "Sets the random number seed for subsampling.";
  }
  
  public int getRandomSeed() {
    return this.m_RandomSeed;
  }
  
  public void setRandomSeed(int paramInt) {
    this.m_RandomSeed = paramInt;
  }
  
  public String sampeSizePercentTipText() {
    return "The subsample size as a percentage of the original set.";
  }
  
  public double getSampleSizePercent() {
    return this.m_SampleSizePercent;
  }
  
  public void setSampleSizePercent(double paramDouble) {
    this.m_SampleSizePercent = paramDouble;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    if (paramInstances.classIndex() < 0 || !paramInstances.classAttribute().isNominal())
      throw new IllegalArgumentException("Supervised resample requires nominal class"); 
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
    getInputFormat().sort(getInputFormat().classIndex());
    int[] arrayOfInt = new int[getInputFormat().numClasses() + 1];
    int k = 0;
    arrayOfInt[k] = 0;
    int m;
    for (m = 0; m < getInputFormat().numInstances(); m++) {
      Instance instance = getInputFormat().instance(m);
      if (instance.classIsMissing()) {
        for (int n = k + 1; n < arrayOfInt.length; n++)
          arrayOfInt[n] = m; 
        break;
      } 
      if (instance.classValue() != k) {
        for (int n = k + 1; n <= instance.classValue(); n++)
          arrayOfInt[n] = m; 
        k = (int)instance.classValue();
      } 
    } 
    if (k <= getInputFormat().numClasses())
      for (m = k + 1; m < arrayOfInt.length; m++)
        arrayOfInt[m] = getInputFormat().numInstances();  
    m = 0;
    for (byte b1 = 0; b1 < arrayOfInt.length - 1; b1++) {
      if (arrayOfInt[b1] != arrayOfInt[b1 + 1])
        m++; 
    } 
    Random random = new Random(this.m_RandomSeed);
    for (byte b2 = 0; b2 < j; b2++) {
      int n = 0;
      if (random.nextDouble() < this.m_BiasToUniformClass) {
        int i1 = random.nextInt(m);
        byte b3 = 0;
        byte b4 = 0;
        while (b3 < arrayOfInt.length - 1) {
          if (arrayOfInt[b3] != arrayOfInt[b3 + 1] && b4++ >= i1) {
            n = arrayOfInt[b3] + random.nextInt(arrayOfInt[b3 + 1] - arrayOfInt[b3]);
            break;
          } 
          b3++;
        } 
      } else {
        n = random.nextInt(i);
      } 
      push((Instance)getInputFormat().instance(n).copy());
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filters\supervised\instance\Resample.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */