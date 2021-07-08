package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SingleIndex;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class AddNoise extends Filter implements UnsupervisedFilter, OptionHandler {
  private SingleIndex m_AttIndex = new SingleIndex("last");
  
  private boolean m_UseMissing = false;
  
  private int m_Percent = 10;
  
  private int m_RandomSeed = 1;
  
  public String globalInfo() {
    return "An instance filter that changes a percentage of a given attributes values. The attribute must be nominal. Missing value can be treated as value itself.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tIndex of the attribute to be changed \n\t(default last attribute)", "C", 1, "-C <col>"));
    vector.addElement(new Option("\tTreat missing values as an extra value \n", "M", 1, "-M"));
    vector.addElement(new Option("\tSpecify the percentage of noise introduced \n\tto the data (default 10)", "P", 1, "-P <num>"));
    vector.addElement(new Option("\tSpecify the random number seed (default 1)", "S", 1, "-S <num>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('C', paramArrayOfString);
    if (str1.length() != 0) {
      setAttributeIndex(str1);
    } else {
      setAttributeIndex("last");
    } 
    if (Utils.getFlag('M', paramArrayOfString))
      setUseMissing(true); 
    String str2 = Utils.getOption('P', paramArrayOfString);
    if (str2.length() != 0) {
      setPercent((int)Double.valueOf(str2).doubleValue());
    } else {
      setPercent(10);
    } 
    String str3 = Utils.getOption('S', paramArrayOfString);
    if (str3.length() != 0) {
      setRandomSeed(Integer.parseInt(str3));
    } else {
      setRandomSeed(1);
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[7];
    byte b = 0;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + getAttributeIndex();
    if (getUseMissing())
      arrayOfString[b++] = "-M"; 
    arrayOfString[b++] = "-P";
    arrayOfString[b++] = "" + getPercent();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getRandomSeed();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String useMissingTipText() {
    return "Flag to set if missing values are used.";
  }
  
  public boolean getUseMissing() {
    return this.m_UseMissing;
  }
  
  public void setUseMissing(boolean paramBoolean) {
    this.m_UseMissing = paramBoolean;
  }
  
  public String randomSeedTipText() {
    return "Random number seed.";
  }
  
  public int getRandomSeed() {
    return this.m_RandomSeed;
  }
  
  public void setRandomSeed(int paramInt) {
    this.m_RandomSeed = paramInt;
  }
  
  public String percentTipText() {
    return "Percentage of introduced noise to data.";
  }
  
  public int getPercent() {
    return this.m_Percent;
  }
  
  public void setPercent(int paramInt) {
    this.m_Percent = paramInt;
  }
  
  public String attIndexSetTipText() {
    return "Index of the attribute that is to changed.";
  }
  
  public String getAttributeIndex() {
    return this.m_AttIndex.getSingleIndex();
  }
  
  public void setAttributeIndex(String paramString) {
    this.m_AttIndex.setSingleIndex(paramString);
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_AttIndex.setUpper(getInputFormat().numAttributes() - 1);
    if (!getInputFormat().attribute(this.m_AttIndex.getIndex()).isNominal())
      throw new Exception("Adding noise is not possible:Chosen attribute is numeric."); 
    if (getInputFormat().attribute(this.m_AttIndex.getIndex()).numValues() < 2 && !this.m_UseMissing)
      throw new Exception("Adding noise is not possible:Chosen attribute has less than two values."); 
    setOutputFormat(getInputFormat());
    this.m_NewBatch = true;
    return false;
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    if (getInputFormat() == null)
      throw new Exception("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    Instance instance = (Instance)paramInstance.copy();
    getInputFormat().add(paramInstance);
    return false;
  }
  
  public boolean batchFinished() throws Exception {
    if (getInputFormat() == null)
      throw new Exception("No input instance format defined"); 
    addNoise(getInputFormat(), this.m_RandomSeed, this.m_Percent, this.m_AttIndex.getIndex(), this.m_UseMissing);
    for (byte b = 0; b < getInputFormat().numInstances(); b++)
      push((Instance)getInputFormat().instance(b).copy()); 
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public void addNoise(Instances paramInstances, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    double d = paramInt2;
    int[] arrayOfInt1 = new int[paramInstances.numInstances()];
    for (byte b1 = 0; b1 < paramInstances.numInstances(); b1++)
      arrayOfInt1[b1] = b1; 
    Random random1 = new Random(paramInt1);
    int i;
    for (i = paramInstances.numInstances() - 1; i >= 0; i--) {
      int n = arrayOfInt1[i];
      int i1 = (int)(random1.nextDouble() * i);
      arrayOfInt1[i] = arrayOfInt1[i1];
      arrayOfInt1[i1] = n;
    } 
    i = paramInstances.attribute(paramInt3).numValues();
    int[] arrayOfInt2 = new int[i];
    int[] arrayOfInt3 = new int[i];
    byte b2 = 0;
    int j = 0;
    for (byte b3 = 0; b3 < i; b3++) {
      arrayOfInt2[b3] = 0;
      arrayOfInt3[b3] = 0;
    } 
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if (instance.isMissing(paramInt3)) {
        j++;
        continue;
      } 
      int n = (int)instance.value(paramInt3);
      arrayOfInt3[(int)instance.value(paramInt3)] = arrayOfInt3[(int)instance.value(paramInt3)] + 1;
    } 
    if (!paramBoolean) {
      j = b2;
    } else {
      j = (int)(j / 100.0D * d + 0.5D);
    } 
    int k = j;
    byte b4;
    for (b4 = 0; b4 < i; b4++) {
      arrayOfInt3[b4] = (int)(arrayOfInt3[b4] / 100.0D * d + 0.5D);
      k += arrayOfInt3[b4];
    } 
    b4 = 0;
    Random random2 = new Random(paramInt1);
    int m = paramInstances.attribute(paramInt3).numValues();
    for (byte b5 = 0; b5 < paramInstances.numInstances() && b4 < k; b5++) {
      Instance instance = paramInstances.instance(arrayOfInt1[b5]);
      if (instance.isMissing(paramInt3)) {
        if (b2 < j) {
          changeValueRandomly(random2, m, paramInt3, instance, paramBoolean);
          b2++;
          b4++;
        } 
      } else {
        int n = (int)instance.value(paramInt3);
        if (arrayOfInt2[n] < arrayOfInt3[n]) {
          changeValueRandomly(random2, m, paramInt3, instance, paramBoolean);
          arrayOfInt2[n] = arrayOfInt2[n] + 1;
          b4++;
        } 
      } 
    } 
  }
  
  private void changeValueRandomly(Random paramRandom, int paramInt1, int paramInt2, Instance paramInstance, boolean paramBoolean) {
    int i;
    if (paramInstance.isMissing(paramInt2)) {
      i = paramInt1;
    } else {
      i = (int)paramInstance.value(paramInt2);
    } 
    if (paramInt1 == 2 && !paramInstance.isMissing(paramInt2)) {
      paramInstance.setValue(paramInt2, ((i + 1) % 2));
    } else {
      while (true) {
        int j;
        if (paramBoolean) {
          j = (int)(paramRandom.nextDouble() * (paramInt1 + 1));
        } else {
          j = (int)(paramRandom.nextDouble() * paramInt1);
        } 
        if (j != i) {
          if (j == paramInt1) {
            paramInstance.setMissing(paramInt2);
          } else {
            paramInstance.setValue(paramInt2, j);
          } 
          return;
        } 
      } 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new AddNoise(), paramArrayOfString);
      } else {
        Filter.filterFile(new AddNoise(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\AddNoise.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */