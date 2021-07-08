package weka.filters.supervised.attribute;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;

public class ClassOrder extends Filter implements SupervisedFilter, OptionHandler {
  private long m_Seed = 1L;
  
  private Random m_Random = null;
  
  private int[] m_Converter = null;
  
  private Attribute m_ClassAttribute = null;
  
  private int m_ClassOrder = 0;
  
  public static final int FREQ_ASCEND = 0;
  
  public static final int FREQ_DESCEND = 1;
  
  public static final int RANDOM = 2;
  
  private double[] m_ClassCounts = null;
  
  public String globalInfo() {
    return "Changes the order of the classes so that the class values are no longer of in the order specified in the header. The values will be in the order specified by the user -- it could be either in ascending/descending order by the class frequency or in random order. Note that this filter currently does not change the header, only the class values of the instances, so there is not much point in using it in conjunction with the FilteredClassifier.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tSpecify the seed of randomization\n\tused to randomize the class\n\torder (default: 1)", "R", 1, "-R <seed>"));
    vector.addElement(new Option("\tSpecify the class order to be\n\tsorted, could be 0: ascending\n\t1: descending and 2: random.(default: 0)", "C", 1, "-C <order>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('R', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_Seed = Long.parseLong(str1);
    } else {
      this.m_Seed = 1L;
    } 
    String str2 = Utils.getOption('C', paramArrayOfString);
    if (str2.length() != 0) {
      this.m_ClassOrder = Integer.parseInt(str2);
    } else {
      this.m_ClassOrder = 0;
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
    this.m_Random = null;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[4];
    byte b = 0;
    arrayOfString[b++] = "-R";
    arrayOfString[b++] = "" + this.m_Seed;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + this.m_ClassOrder;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String seedTipText() {
    return "Specify the seed of randomization of the class order";
  }
  
  public long getSeed() {
    return this.m_Seed;
  }
  
  public void setSeed(long paramLong) {
    this.m_Seed = paramLong;
    this.m_Random = null;
  }
  
  public String classOrderTipText() {
    return "Specify the class order after the filtering";
  }
  
  public int getClassOrder() {
    return this.m_ClassOrder;
  }
  
  public void setClassOrder(int paramInt) {
    this.m_ClassOrder = paramInt;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(new Instances(paramInstances, 0));
    if (paramInstances.classIndex() < 0)
      throw new IllegalArgumentException("ClassOrder: No class index set."); 
    if (!paramInstances.classAttribute().isNominal())
      throw new IllegalArgumentException("ClassOrder: Class must be nominal."); 
    this.m_ClassAttribute = paramInstances.classAttribute();
    this.m_Random = new Random(this.m_Seed);
    this.m_Converter = null;
    int i = paramInstances.numClasses();
    this.m_ClassCounts = new double[i];
    return false;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (this.m_Converter != null) {
      Instance instance = (Instance)paramInstance.copy();
      if (!instance.isMissing(this.m_ClassAttribute))
        instance.setClassValue(this.m_Converter[(int)instance.classValue()]); 
      push(instance);
      return true;
    } 
    if (!paramInstance.isMissing(this.m_ClassAttribute))
      this.m_ClassCounts[(int)paramInstance.classValue()] = this.m_ClassCounts[(int)paramInstance.classValue()] + paramInstance.weight(); 
    bufferInput(paramInstance);
    return false;
  }
  
  public boolean batchFinished() throws Exception {
    Instances instances = getInputFormat();
    if (instances == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_Converter == null) {
      int[] arrayOfInt1 = new int[this.m_ClassCounts.length];
      int i;
      for (i = 0; i < arrayOfInt1.length; i++)
        arrayOfInt1[i] = i; 
      for (i = arrayOfInt1.length - 1; i > 0; i--) {
        int j = this.m_Random.nextInt(i + 1);
        int k = arrayOfInt1[i];
        arrayOfInt1[i] = arrayOfInt1[j];
        arrayOfInt1[j] = k;
      } 
      double[] arrayOfDouble = new double[this.m_ClassCounts.length];
      for (byte b1 = 0; b1 < arrayOfDouble.length; b1++)
        arrayOfDouble[b1] = this.m_ClassCounts[arrayOfInt1[b1]]; 
      if (this.m_ClassOrder == 2) {
        this.m_Converter = arrayOfInt1;
        this.m_ClassCounts = arrayOfDouble;
      } else {
        int[] arrayOfInt = Utils.sort(arrayOfDouble);
        this.m_Converter = new int[arrayOfInt.length];
        if (this.m_ClassOrder == 0) {
          for (byte b5 = 0; b5 < arrayOfInt.length; b5++)
            this.m_Converter[b5] = arrayOfInt1[arrayOfInt[b5]]; 
        } else if (this.m_ClassOrder == 1) {
          for (byte b5 = 0; b5 < arrayOfInt.length; b5++)
            this.m_Converter[b5] = arrayOfInt1[arrayOfInt[arrayOfInt.length - b5 - 1]]; 
        } else {
          throw new IllegalArgumentException("Class order not defined!");
        } 
        double[] arrayOfDouble1 = new double[this.m_ClassCounts.length];
        for (byte b = 0; b < this.m_Converter.length; b++)
          arrayOfDouble1[b] = this.m_ClassCounts[this.m_Converter[b]]; 
        this.m_ClassCounts = arrayOfDouble1;
      } 
      FastVector fastVector1 = new FastVector(instances.classAttribute().numValues());
      for (byte b2 = 0; b2 < instances.numClasses(); b2++)
        fastVector1.addElement(instances.classAttribute().value(this.m_Converter[b2])); 
      FastVector fastVector2 = new FastVector(instances.numAttributes());
      for (byte b3 = 0; b3 < instances.numAttributes(); b3++) {
        if (b3 == instances.classIndex()) {
          fastVector2.addElement(new Attribute(instances.classAttribute().name(), fastVector1, instances.classAttribute().getMetadata()));
        } else {
          fastVector2.addElement(instances.attribute(b3));
        } 
      } 
      Instances instances1 = new Instances(instances.relationName(), fastVector2, 0);
      instances1.setClassIndex(instances.classIndex());
      setOutputFormat(instances1);
      int[] arrayOfInt2 = new int[this.m_Converter.length];
      byte b4;
      for (b4 = 0; b4 < arrayOfInt2.length; b4++)
        arrayOfInt2[this.m_Converter[b4]] = b4; 
      this.m_Converter = arrayOfInt2;
      for (b4 = 0; b4 < instances.numInstances(); b4++) {
        Instance instance = instances.instance(b4);
        if (!instance.isMissing(instance.classIndex()))
          instance.setClassValue(this.m_Converter[(int)instance.classValue()]); 
        push(instance);
      } 
    } 
    flushInput();
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public double[] getClassCounts() {
    return this.m_ClassAttribute.isNominal() ? this.m_ClassCounts : null;
  }
  
  public double[] distributionsByOriginalIndex(double[] paramArrayOfdouble) {
    double[] arrayOfDouble = new double[this.m_Converter.length];
    for (byte b = 0; b < this.m_Converter.length; b++)
      arrayOfDouble[b] = paramArrayOfdouble[this.m_Converter[b]]; 
    return arrayOfDouble;
  }
  
  public double originalValue(double paramDouble) throws Exception {
    if (this.m_Converter == null)
      throw new IllegalStateException("Coverter table not defined yet!"); 
    for (byte b = 0; b < this.m_Converter.length; b++) {
      if ((int)paramDouble == this.m_Converter[b])
        return b; 
    } 
    return -1.0D;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new ClassOrder(), paramArrayOfString);
      } else {
        Filter.filterFile(new ClassOrder(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filters\supervised\attribute\ClassOrder.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */