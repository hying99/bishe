package weka.filters.supervised.instance;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;

public class SpreadSubsample extends Filter implements SupervisedFilter, OptionHandler {
  private int m_RandomSeed = 1;
  
  private int m_MaxCount;
  
  private boolean m_FirstBatchDone = false;
  
  private double m_DistributionSpread = 0.0D;
  
  private boolean m_AdjustWeights = false;
  
  public String globalInfo() {
    return "Produces a random subsample of a dataset. The original dataset must fit entirely in memory. This filter allows you to specify the maximum \"spread\" between the rarest and most common class. For example, you may specify that there be at most a 2:1 difference in class frequencies. When used in batch mode, subsequent batches are NOT resampled.";
  }
  
  public String adjustWeightsTipText() {
    return "Wether instance weights will be adjusted to maintain total weight per class.";
  }
  
  public boolean getAdjustWeights() {
    return this.m_AdjustWeights;
  }
  
  public void setAdjustWeights(boolean paramBoolean) {
    this.m_AdjustWeights = paramBoolean;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tSpecify the random number seed (default 1)", "S", 1, "-S <num>"));
    vector.addElement(new Option("\tThe maximum class distribution spread.\n\t0 = no maximum spread, 1 = uniform distribution, 10 = allow at most\n\ta 10:1 ratio between the classes (default 0)", "M", 1, "-M <num>"));
    vector.addElement(new Option("\tAdjust weights so that total weight per class is maintained.\n\tIndividual instance weighting is not preserved. (default no\n\tweights adjustment", "W", 0, "-W"));
    vector.addElement(new Option("\tThe maximum count for any class value (default 0 = unlimited).\n", "X", 0, "-X <num>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('S', paramArrayOfString);
    if (str1.length() != 0) {
      setRandomSeed(Integer.parseInt(str1));
    } else {
      setRandomSeed(1);
    } 
    String str2 = Utils.getOption('M', paramArrayOfString);
    if (str2.length() != 0) {
      setDistributionSpread(Double.valueOf(str2).doubleValue());
    } else {
      setDistributionSpread(0.0D);
    } 
    String str3 = Utils.getOption('X', paramArrayOfString);
    if (str3.length() != 0) {
      setMaxCount(Double.valueOf(str3).doubleValue());
    } else {
      setMaxCount(0.0D);
    } 
    setAdjustWeights(Utils.getFlag('W', paramArrayOfString));
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[7];
    byte b = 0;
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getDistributionSpread();
    arrayOfString[b++] = "-X";
    arrayOfString[b++] = "" + getMaxCount();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getRandomSeed();
    if (getAdjustWeights())
      arrayOfString[b++] = "-W"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String distributionSpreadTipText() {
    return "The maximum class distribution spread. (0 = no maximum spread, 1 = uniform distribution, 10 = allow at most a 10:1 ratio between the classes).";
  }
  
  public void setDistributionSpread(double paramDouble) {
    this.m_DistributionSpread = paramDouble;
  }
  
  public double getDistributionSpread() {
    return this.m_DistributionSpread;
  }
  
  public String maxCountTipText() {
    return "The maximum count for any class value (0 = unlimited).";
  }
  
  public void setMaxCount(double paramDouble) {
    this.m_MaxCount = (int)paramDouble;
  }
  
  public double getMaxCount() {
    return this.m_MaxCount;
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
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("The class attribute must be nominal."); 
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
    int i = getInputFormat().classIndex();
    getInputFormat().sort(i);
    int[] arrayOfInt1 = getClassIndices();
    int[] arrayOfInt2 = new int[getInputFormat().numClasses()];
    double[] arrayOfDouble = new double[getInputFormat().numClasses()];
    int j = -1;
    byte b1;
    for (b1 = 0; b1 < getInputFormat().numInstances(); b1++) {
      Instance instance = getInputFormat().instance(b1);
      if (!instance.classIsMissing()) {
        arrayOfInt2[(int)instance.classValue()] = arrayOfInt2[(int)instance.classValue()] + 1;
        arrayOfDouble[(int)instance.classValue()] = arrayOfDouble[(int)instance.classValue()] + instance.weight();
      } 
    } 
    for (b1 = 0; b1 < arrayOfInt2.length; b1++) {
      if (arrayOfInt2[b1] > 0)
        arrayOfDouble[b1] = arrayOfDouble[b1] / arrayOfInt2[b1]; 
    } 
    for (b1 = 0; b1 < arrayOfInt2.length; b1++) {
      if (j < 0 && arrayOfInt2[b1] > 0) {
        j = arrayOfInt2[b1];
      } else if (arrayOfInt2[b1] < j && arrayOfInt2[b1] > 0) {
        j = arrayOfInt2[b1];
      } 
    } 
    if (j < 0) {
      System.err.println("SpreadSubsample: *warning* none of the classes have any values in them.");
      return;
    } 
    int[] arrayOfInt3 = new int[getInputFormat().numClasses()];
    for (byte b2 = 0; b2 < arrayOfInt2.length; b2++) {
      arrayOfInt3[b2] = (int)Math.abs(Math.min(arrayOfInt2[b2], j * this.m_DistributionSpread));
      if (this.m_DistributionSpread == 0.0D)
        arrayOfInt3[b2] = arrayOfInt2[b2]; 
      if (this.m_MaxCount > 0)
        arrayOfInt3[b2] = Math.min(arrayOfInt3[b2], this.m_MaxCount); 
    } 
    Random random = new Random(this.m_RandomSeed);
    Hashtable hashtable = new Hashtable();
    for (byte b3 = 0; b3 < arrayOfInt3.length; b3++) {
      double d = 1.0D;
      if (this.m_AdjustWeights && arrayOfInt3[b3] > 0)
        d = arrayOfDouble[b3] * arrayOfInt2[b3] / arrayOfInt3[b3]; 
      byte b = 0;
      while (b < arrayOfInt3[b3]) {
        boolean bool = false;
        while (true) {
          int k = arrayOfInt1[b3] + Math.abs(random.nextInt()) % (arrayOfInt1[b3 + 1] - arrayOfInt1[b3]);
          if (hashtable.get("" + k) == null) {
            hashtable.put("" + k, "");
            bool = true;
            if (k >= 0) {
              Instance instance = (Instance)getInputFormat().instance(k).copy();
              if (this.m_AdjustWeights)
                instance.setWeight(d); 
              push(instance);
            } 
          } 
          if (bool)
            b++; 
        } 
      } 
    } 
  }
  
  private int[] getClassIndices() {
    int[] arrayOfInt = new int[getInputFormat().numClasses() + 1];
    int i = 0;
    arrayOfInt[i] = 0;
    int j;
    for (j = 0; j < getInputFormat().numInstances(); j++) {
      Instance instance = getInputFormat().instance(j);
      if (instance.classIsMissing()) {
        for (int k = i + 1; k < arrayOfInt.length; k++)
          arrayOfInt[k] = j; 
        break;
      } 
      if (instance.classValue() != i) {
        for (int k = i + 1; k <= instance.classValue(); k++)
          arrayOfInt[k] = j; 
        i = (int)instance.classValue();
      } 
    } 
    if (i <= getInputFormat().numClasses())
      for (j = i + 1; j < arrayOfInt.length; j++)
        arrayOfInt[j] = getInputFormat().numInstances();  
    return arrayOfInt;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new SpreadSubsample(), paramArrayOfString);
      } else {
        Filter.filterFile(new SpreadSubsample(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filters\supervised\instance\SpreadSubsample.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */