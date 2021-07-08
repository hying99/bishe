package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.SparseInstance;
import weka.core.Tag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;
import weka.filters.supervised.attribute.NominalToBinary;

public class RandomProjection extends Filter implements UnsupervisedFilter, OptionHandler {
  private int m_k = 10;
  
  private double m_percent = 0.0D;
  
  private boolean m_useGaussian = false;
  
  private static final int SPARSE1 = 1;
  
  private static final int SPARSE2 = 2;
  
  private static final int GAUSSIAN = 3;
  
  public static final Tag[] TAGS_DSTRS_TYPE = new Tag[] { new Tag(1, "Sparse 1"), new Tag(2, "Sparse 2"), new Tag(3, "Gaussian") };
  
  private int m_distribution = 1;
  
  private boolean m_replaceMissing = false;
  
  private boolean m_OutputFormatDefined = false;
  
  private Filter ntob;
  
  private Filter replaceMissing;
  
  private long m_rndmSeed = 42L;
  
  private double[][] rmatrix;
  
  private Random r;
  
  private static final int[] weights = new int[] { 1, 1, 4 };
  
  private static final int[] vals = new int[] { -1, 1, 0 };
  
  private static final int[] weights2 = new int[] { 1, 1 };
  
  private static final int[] vals2 = new int[] { -1, 1 };
  
  private static final double sqrt3 = Math.sqrt(3.0D);
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tThe number of dimensions (attributes) the data should be reduced to\n\t(exclusive of the class attribute, if it is set).", "N", 1, "-N <number>"));
    vector.addElement(new Option("\tThe distribution to use for calculating the random matrix.\n\tSparse1 is:\n\t  sqrt(3)*{-1 with prob(1/6), 0 with prob(2/3), +1 with prob(1/6)}\n\tSparse2 is:\n\t  {-1 with prob(1/2), +1 with prob(1/2)}\n", "D", 1, "-D [SPARSE1|SPARSE2|GAUSSIAN]"));
    vector.addElement(new Option("\tThe percentage of dimensions (attributes) the data should\n\tbe reduced to (exclusive of the class attribute, if it is set). This -N\n\toption is ignored if this option is present or is greater\n\tthan zero.", "P", 1, "-P <percent>"));
    vector.addElement(new Option("\tReplace missing values using the ReplaceMissingValues filter", "M", 0, "-M"));
    vector.addElement(new Option("\tThe random seed for the random number generator used for\n\tcalculating the random matrix.", "R", 0, "-R <num>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0) {
      setPercent(Double.parseDouble(str));
    } else {
      str = Utils.getOption('N', paramArrayOfString);
      if (str.length() != 0) {
        setNumberOfAttributes(Integer.parseInt(str));
      } else {
        setNumberOfAttributes(10);
      } 
    } 
    str = Utils.getOption('R', paramArrayOfString);
    if (str.length() != 0)
      setRandomSeed(Long.parseLong(str)); 
    str = Utils.getOption('D', paramArrayOfString);
    if (str.length() != 0)
      if (str.equalsIgnoreCase("sparse1")) {
        setDistribution(new SelectedTag(1, TAGS_DSTRS_TYPE));
      } else if (str.equalsIgnoreCase("sparse2")) {
        setDistribution(new SelectedTag(2, TAGS_DSTRS_TYPE));
      } else if (str.equalsIgnoreCase("gaussian")) {
        setDistribution(new SelectedTag(3, TAGS_DSTRS_TYPE));
      }  
    if (Utils.getFlag('M', paramArrayOfString)) {
      setReplaceMissingValues(true);
    } else {
      setReplaceMissingValues(false);
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[10];
    byte b = 0;
    if (getReplaceMissingValues())
      arrayOfString[b++] = "-M"; 
    double d = getNumberOfAttributes();
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + d;
    d = getPercent();
    arrayOfString[b++] = "-P";
    arrayOfString[b++] = "" + d;
    long l = getRandomSeed();
    arrayOfString[b++] = "-R";
    arrayOfString[b++] = "" + l;
    SelectedTag selectedTag = getDistribution();
    arrayOfString[b++] = "-D";
    arrayOfString[b++] = "" + selectedTag.getSelectedTag().getReadable();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String globalInfo() {
    return "Reduces the dimensionality of the data by projecting it onto a lower dimensional subspace using a random matrix with columns of unit length (i.e. It will reduce the number of attributes in the data while preserving much of its variation like PCA, but at a much less computational cost).\nIt first applies the  NominalToBinary filter to convert all attributes to numeric before reducing the dimension. It preserves the class attribute.";
  }
  
  public String numberOfAttributesTipText() {
    return "The number of dimensions (attributes) the data should be reduced to.";
  }
  
  public void setNumberOfAttributes(int paramInt) {
    this.m_k = paramInt;
  }
  
  public int getNumberOfAttributes() {
    return this.m_k;
  }
  
  public String percentTipText() {
    return " The percentage of dimensions (attributes) the data should be reduced to  (inclusive of the class attribute). This  NumberOfAttributes option is ignored if this option is present or is greater than zero.";
  }
  
  public void setPercent(double paramDouble) {
    if (paramDouble > 1.0D)
      paramDouble /= 100.0D; 
    this.m_percent = paramDouble;
  }
  
  public double getPercent() {
    return this.m_percent;
  }
  
  public String randomSeedTipText() {
    return "The random seed used by the random number generator used for generating the random matrix ";
  }
  
  public void setRandomSeed(long paramLong) {
    this.m_rndmSeed = paramLong;
  }
  
  public long getRandomSeed() {
    return this.m_rndmSeed;
  }
  
  public String distributionTipText() {
    return "The distribution to use for calculating the random matrix.\nSparse1 is:\n sqrt(3) * { -1 with prob(1/6), \n               0 with prob(2/3),  \n              +1 with prob(1/6) } \nSparse2 is:\n { -1 with prob(1/2), \n   +1 with prob(1/2) } ";
  }
  
  public void setDistribution(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_DSTRS_TYPE)
      this.m_distribution = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public SelectedTag getDistribution() {
    return new SelectedTag(this.m_distribution, TAGS_DSTRS_TYPE);
  }
  
  public String replaceMissingValuesTipText() {
    return "If set the filter uses weka.filters.unsupervised.attribute.ReplaceMissingValues to replace the missing values";
  }
  
  public void setReplaceMissingValues(boolean paramBoolean) {
    this.m_replaceMissing = paramBoolean;
  }
  
  public boolean getReplaceMissingValues() {
    return this.m_replaceMissing;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    byte b;
    for (b = 0; b < paramInstances.numAttributes(); b++) {
      if (b != paramInstances.classIndex() && paramInstances.attribute(b).isNominal()) {
        if (paramInstances.classIndex() >= 0) {
          this.ntob = (Filter)new NominalToBinary();
          break;
        } 
        this.ntob = new NominalToBinary();
        break;
      } 
    } 
    b = 1;
    if (this.replaceMissing != null) {
      this.replaceMissing = new ReplaceMissingValues();
      if (this.replaceMissing.setInputFormat(paramInstances)) {
        b = 1;
      } else {
        b = 0;
      } 
    } 
    if (this.ntob != null) {
      if (this.ntob.setInputFormat(paramInstances)) {
        setOutputFormat();
        return (b != 0);
      } 
      return false;
    } 
    setOutputFormat();
    return (b != 0);
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    Instance instance = null;
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    boolean bool = false;
    if (this.replaceMissing != null)
      if (this.replaceMissing.input(paramInstance)) {
        if (!this.m_OutputFormatDefined)
          setOutputFormat(); 
        instance = this.replaceMissing.output();
        bool = true;
      } else {
        return false;
      }  
    if (this.ntob != null) {
      if (!bool)
        instance = paramInstance; 
      if (this.ntob.input(instance)) {
        if (!this.m_OutputFormatDefined)
          setOutputFormat(); 
        instance = this.ntob.output();
        instance = convertInstance(instance);
        push(instance);
        return true;
      } 
      return false;
    } 
    if (!bool)
      instance = paramInstance; 
    instance = convertInstance(instance);
    push(instance);
    return true;
  }
  
  public boolean batchFinished() throws Exception {
    if (getInputFormat() == null)
      throw new NullPointerException("No input instance format defined"); 
    boolean bool = false;
    if (this.replaceMissing != null && this.replaceMissing.batchFinished()) {
      Instance instance;
      while ((instance = this.replaceMissing.output()) != null) {
        if (!this.m_OutputFormatDefined)
          setOutputFormat(); 
        if (this.ntob != null) {
          this.ntob.input(instance);
          continue;
        } 
        Instance instance1 = convertInstance(instance);
        push(instance1);
      } 
      if (this.ntob != null && this.ntob.batchFinished()) {
        while ((instance = this.ntob.output()) != null) {
          if (!this.m_OutputFormatDefined)
            setOutputFormat(); 
          Instance instance1 = convertInstance(instance);
          push(instance1);
        } 
        this.ntob = null;
      } 
      this.replaceMissing = null;
      bool = true;
    } 
    if (!bool && this.ntob != null && this.ntob.batchFinished()) {
      Instance instance;
      while ((instance = this.ntob.output()) != null) {
        if (!this.m_OutputFormatDefined)
          setOutputFormat(); 
        Instance instance1 = convertInstance(instance);
        push(instance1);
      } 
      this.ntob = null;
    } 
    this.m_OutputFormatDefined = false;
    return super.batchFinished();
  }
  
  private void setOutputFormat() {
    Instances instances1;
    if (this.ntob != null) {
      instances1 = this.ntob.getOutputFormat();
    } else {
      instances1 = getInputFormat();
    } 
    if (this.m_percent > 0.0D)
      this.m_k = (int)((getInputFormat().numAttributes() - 1) * this.m_percent); 
    int i = -1;
    FastVector fastVector = new FastVector();
    byte b;
    for (b = 0; b < this.m_k; b++)
      fastVector.addElement(new Attribute("K" + (b + 1))); 
    if (instances1.classIndex() != -1) {
      fastVector.addElement(instances1.attribute(instances1.classIndex()));
      i = fastVector.size() - 1;
    } 
    Instances instances2 = new Instances(instances1.relationName(), fastVector, 0);
    if (i != -1)
      instances2.setClassIndex(i); 
    this.m_OutputFormatDefined = true;
    this.r = new Random();
    this.r.setSeed(this.m_rndmSeed);
    this.rmatrix = new double[this.m_k][instances1.numAttributes()];
    if (this.m_distribution == 3) {
      for (b = 0; b < this.rmatrix.length; b++) {
        for (byte b1 = 0; b1 < (this.rmatrix[b]).length; b1++)
          this.rmatrix[b][b1] = this.r.nextGaussian(); 
      } 
    } else {
      b = (this.m_distribution == 1) ? 1 : 0;
      for (byte b1 = 0; b1 < this.rmatrix.length; b1++) {
        for (byte b2 = 0; b2 < (this.rmatrix[b1]).length; b2++)
          this.rmatrix[b1][b2] = rndmNum(b); 
      } 
    } 
    setOutputFormat(instances2);
  }
  
  private Instance convertInstance(Instance paramInstance) {
    Instance instance;
    double[] arrayOfDouble = new double[getOutputFormat().numAttributes()];
    int i = (this.ntob == null) ? getInputFormat().classIndex() : this.ntob.getOutputFormat().classIndex();
    int j = this.m_k;
    for (byte b = 0; b < j; b++) {
      boolean bool = false;
      for (byte b1 = 0; b1 < paramInstance.numValues(); b1++) {
        if ((i == -1 || b1 != i) && !paramInstance.isMissing(b1))
          arrayOfDouble[b] = arrayOfDouble[b] + this.rmatrix[b][b1] * paramInstance.value(b1); 
      } 
    } 
    if (i != -1)
      arrayOfDouble[this.m_k] = paramInstance.value(i); 
    if (paramInstance instanceof SparseInstance) {
      SparseInstance sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble);
    } else {
      instance = new Instance(paramInstance.weight(), arrayOfDouble);
    } 
    instance.setDataset(getOutputFormat());
    return instance;
  }
  
  private double rndmNum(boolean paramBoolean) {
    return paramBoolean ? (sqrt3 * vals[weightedDistribution(weights)]) : vals2[weightedDistribution(weights2)];
  }
  
  private int weightedDistribution(int[] paramArrayOfint) {
    int i = 0;
    int j;
    for (j = 0; j < paramArrayOfint.length; j++)
      i += paramArrayOfint[j]; 
    j = (int)Math.floor(this.r.nextDouble() * i);
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      j -= paramArrayOfint[b];
      if (j < 0)
        return b; 
    } 
    return -1;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new RandomProjection(), paramArrayOfString);
      } else {
        Filter.filterFile(new RandomProjection(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\RandomProjection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */