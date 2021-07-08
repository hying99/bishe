package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class Discretize extends PotentialClassIgnorer implements UnsupervisedFilter, OptionHandler, WeightedInstancesHandler {
  protected Range m_DiscretizeCols = new Range();
  
  protected int m_NumBins = 10;
  
  protected double m_DesiredWeightOfInstancesPerInterval = -1.0D;
  
  protected double[][] m_CutPoints = (double[][])null;
  
  protected boolean m_MakeBinary = false;
  
  protected boolean m_FindNumBins = false;
  
  protected boolean m_UseEqualFrequency = false;
  
  protected String m_DefaultCols = "first-last";
  
  public Discretize() {
    setAttributeIndices("first-last");
  }
  
  public Discretize(String paramString) {
    setAttributeIndices(paramString);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(7);
    vector.addElement(new Option("\tSpecifies the (maximum) number of bins to divide numeric attributes into.\n\t(default = 10)", "B", 1, "-B <num>"));
    vector.addElement(new Option("\tSpecifies the desired weight of instances per bin for\n\tequal-frequency binning. If this is set to a positive\n\tnumber then the -B option will be ignored.\n\t(default = -1)", "M", 1, "-M <num>"));
    vector.addElement(new Option("\tUse equal-frequency instead of equal-width discretization.", "F", 0, "-F"));
    vector.addElement(new Option("\tOptimize number of bins using leave-one-out estimate\n\tof estimated entropy (for equal-width discretization).\n\tIf this is set then the -B option will be ignored.", "O", 0, "-O"));
    vector.addElement(new Option("\tSpecifies list of columns to Discretize. First and last are valid indexes.\n\t(default: first-last)", "R", 1, "-R <col1,col2-col4,...>"));
    vector.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
    vector.addElement(new Option("\tOutput binary attributes for discretized attributes.", "D", 0, "-D"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setMakeBinary(Utils.getFlag('D', paramArrayOfString));
    setUseEqualFrequency(Utils.getFlag('F', paramArrayOfString));
    setFindNumBins(Utils.getFlag('O', paramArrayOfString));
    setInvertSelection(Utils.getFlag('V', paramArrayOfString));
    String str1 = Utils.getOption('M', paramArrayOfString);
    if (str1.length() != 0) {
      setDesiredWeightOfInstancesPerInterval((new Double(str1)).doubleValue());
    } else {
      setDesiredWeightOfInstancesPerInterval(-1.0D);
    } 
    String str2 = Utils.getOption('B', paramArrayOfString);
    if (str2.length() != 0) {
      setBins(Integer.parseInt(str2));
    } else {
      setBins(10);
    } 
    String str3 = Utils.getOption('R', paramArrayOfString);
    if (str3.length() != 0) {
      setAttributeIndices(str3);
    } else {
      setAttributeIndices(this.m_DefaultCols);
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[10];
    byte b = 0;
    if (getMakeBinary())
      arrayOfString[b++] = "-D"; 
    if (getUseEqualFrequency())
      arrayOfString[b++] = "-F"; 
    if (getFindNumBins())
      arrayOfString[b++] = "-O"; 
    if (getInvertSelection())
      arrayOfString[b++] = "-V"; 
    arrayOfString[b++] = "-B";
    arrayOfString[b++] = "" + getBins();
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getDesiredWeightOfInstancesPerInterval();
    if (!getAttributeIndices().equals("")) {
      arrayOfString[b++] = "-R";
      arrayOfString[b++] = getAttributeIndices();
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    if (this.m_MakeBinary && this.m_IgnoreClass)
      throw new IllegalArgumentException("Can't ignore class when changing the number of attributes!"); 
    super.setInputFormat(paramInstances);
    this.m_DiscretizeCols.setUpper(paramInstances.numAttributes() - 1);
    this.m_CutPoints = (double[][])null;
    if (getFindNumBins() && getUseEqualFrequency())
      throw new IllegalArgumentException("Bin number optimization in conjunction with equal-frequency binning not implemented."); 
    return false;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (this.m_CutPoints != null) {
      convertInstance(paramInstance);
      return true;
    } 
    bufferInput(paramInstance);
    return false;
  }
  
  public boolean batchFinished() {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_CutPoints == null) {
      calculateCutPoints();
      setOutputFormat();
      for (byte b = 0; b < getInputFormat().numInstances(); b++)
        convertInstance(getInputFormat().instance(b)); 
    } 
    flushInput();
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public String globalInfo() {
    return "An instance filter that discretizes a range of numeric attributes in the dataset into nominal attributes. Discretization is by simple binning. Skips the class attribute if set.";
  }
  
  public String findNumBinsTipText() {
    return "Optimize number of equal-width bins using leave-one-out. Doesn't work for equal-frequency binning";
  }
  
  public boolean getFindNumBins() {
    return this.m_FindNumBins;
  }
  
  public void setFindNumBins(boolean paramBoolean) {
    this.m_FindNumBins = paramBoolean;
  }
  
  public String makeBinaryTipText() {
    return "Make resulting attributes binary.";
  }
  
  public boolean getMakeBinary() {
    return this.m_MakeBinary;
  }
  
  public void setMakeBinary(boolean paramBoolean) {
    this.m_MakeBinary = paramBoolean;
  }
  
  public String desiredWeightOfInstancesPerIntervalTipText() {
    return "Sets the desired weight of instances per interval for equal-frequency binning.";
  }
  
  public double getDesiredWeightOfInstancesPerInterval() {
    return this.m_DesiredWeightOfInstancesPerInterval;
  }
  
  public void setDesiredWeightOfInstancesPerInterval(double paramDouble) {
    this.m_DesiredWeightOfInstancesPerInterval = paramDouble;
  }
  
  public String useEqualFrequencyTipText() {
    return "If set to true, equal-frequency binning will be used instead of equal-width binning.";
  }
  
  public boolean getUseEqualFrequency() {
    return this.m_UseEqualFrequency;
  }
  
  public void setUseEqualFrequency(boolean paramBoolean) {
    this.m_UseEqualFrequency = paramBoolean;
  }
  
  public String binsTipText() {
    return "Number of bins.";
  }
  
  public int getBins() {
    return this.m_NumBins;
  }
  
  public void setBins(int paramInt) {
    this.m_NumBins = paramInt;
  }
  
  public String invertSelectionTipText() {
    return "Set attribute selection mode. If false, only selected (numeric) attributes in the range will be discretized; if true, only non-selected attributes will be discretized.";
  }
  
  public boolean getInvertSelection() {
    return this.m_DiscretizeCols.getInvert();
  }
  
  public void setInvertSelection(boolean paramBoolean) {
    this.m_DiscretizeCols.setInvert(paramBoolean);
  }
  
  public String attributeIndicesTipText() {
    return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
  }
  
  public String getAttributeIndices() {
    return this.m_DiscretizeCols.getRanges();
  }
  
  public void setAttributeIndices(String paramString) {
    this.m_DiscretizeCols.setRanges(paramString);
  }
  
  public void setAttributeIndicesArray(int[] paramArrayOfint) {
    setAttributeIndices(Range.indicesToRangeList(paramArrayOfint));
  }
  
  public double[] getCutPoints(int paramInt) {
    return (this.m_CutPoints == null) ? null : this.m_CutPoints[paramInt];
  }
  
  protected void calculateCutPoints() {
    Object object = null;
    this.m_CutPoints = new double[getInputFormat().numAttributes()][];
    for (int i = getInputFormat().numAttributes() - 1; i >= 0; i--) {
      if (this.m_DiscretizeCols.isInRange(i) && getInputFormat().attribute(i).isNumeric() && getInputFormat().classIndex() != i)
        if (this.m_FindNumBins) {
          findNumBins(i);
        } else if (!this.m_UseEqualFrequency) {
          calculateCutPointsByEqualWidthBinning(i);
        } else {
          calculateCutPointsByEqualFrequencyBinning(i);
        }  
    } 
  }
  
  protected void calculateCutPointsByEqualWidthBinning(int paramInt) {
    double d1 = 0.0D;
    double d2 = 1.0D;
    for (byte b = 0; b < getInputFormat().numInstances(); b++) {
      Instance instance = getInputFormat().instance(b);
      if (!instance.isMissing(paramInt)) {
        double d = instance.value(paramInt);
        if (d1 < d2)
          d1 = d2 = d; 
        if (d > d1)
          d1 = d; 
        if (d < d2)
          d2 = d; 
      } 
    } 
    double d3 = (d1 - d2) / this.m_NumBins;
    double[] arrayOfDouble = null;
    if (this.m_NumBins > 1 && d3 > 0.0D) {
      arrayOfDouble = new double[this.m_NumBins - 1];
      for (byte b1 = 1; b1 < this.m_NumBins; b1++)
        arrayOfDouble[b1 - 1] = d2 + d3 * b1; 
    } 
    this.m_CutPoints[paramInt] = arrayOfDouble;
  }
  
  protected void calculateCutPointsByEqualFrequencyBinning(int paramInt) {
    double d2;
    Instances instances = new Instances(getInputFormat());
    instances.sort(paramInt);
    double d1 = 0.0D;
    for (byte b1 = 0; b1 < instances.numInstances() && !instances.instance(b1).isMissing(paramInt); b1++)
      d1 += instances.instance(b1).weight(); 
    double[] arrayOfDouble = new double[this.m_NumBins - 1];
    if (getDesiredWeightOfInstancesPerInterval() > 0.0D) {
      d2 = getDesiredWeightOfInstancesPerInterval();
      arrayOfDouble = new double[(int)(d1 / d2)];
    } else {
      d2 = d1 / this.m_NumBins;
      arrayOfDouble = new double[this.m_NumBins - 1];
    } 
    double d3 = 0.0D;
    double d4 = 0.0D;
    byte b2 = 0;
    byte b = -1;
    for (byte b3 = 0; b3 < instances.numInstances() - 1 && !instances.instance(b3).isMissing(paramInt); b3++) {
      d3 += instances.instance(b3).weight();
      d1 -= instances.instance(b3).weight();
      if (instances.instance(b3).value(paramInt) < instances.instance(b3 + 1).value(paramInt))
        if (d3 >= d2) {
          if (d2 - d4 < d3 - d2 && b != -1) {
            arrayOfDouble[b2] = (instances.instance(b).value(paramInt) + instances.instance(b + 1).value(paramInt)) / 2.0D;
            d3 -= d4;
            d4 = d3;
            b = b3;
          } else {
            arrayOfDouble[b2] = (instances.instance(b3).value(paramInt) + instances.instance(b3 + 1).value(paramInt)) / 2.0D;
            d3 = 0.0D;
            d4 = 0.0D;
            b = -1;
          } 
          d2 = (d1 + d3) / (arrayOfDouble.length + 1 - ++b2);
        } else {
          b = b3;
          d4 = d3;
        }  
    } 
    if (b2 < arrayOfDouble.length && b != -1) {
      arrayOfDouble[b2] = (instances.instance(b).value(paramInt) + instances.instance(b + 1).value(paramInt)) / 2.0D;
      b2++;
    } 
    if (b2 == 0) {
      this.m_CutPoints[paramInt] = null;
    } else {
      double[] arrayOfDouble1 = new double[b2];
      for (byte b4 = 0; b4 < b2; b4++)
        arrayOfDouble1[b4] = arrayOfDouble[b4]; 
      this.m_CutPoints[paramInt] = arrayOfDouble1;
    } 
  }
  
  protected void findNumBins(int paramInt) {
    double d1 = Double.MAX_VALUE;
    double d2 = -4.9E-324D;
    double d3 = 0.0D;
    double d4 = Double.MAX_VALUE;
    int i = 1;
    byte b;
    for (b = 0; b < getInputFormat().numInstances(); b++) {
      Instance instance = getInputFormat().instance(b);
      if (!instance.isMissing(paramInt)) {
        double d = instance.value(paramInt);
        if (d > d2)
          d2 = d; 
        if (d < d1)
          d1 = d; 
      } 
    } 
    for (b = 0; b < this.m_NumBins; b++) {
      double[] arrayOfDouble1 = new double[b + 1];
      d3 = (d2 - d1) / (b + 1);
      byte b1;
      for (b1 = 0; b1 < getInputFormat().numInstances(); b1++) {
        Instance instance = getInputFormat().instance(b1);
        if (!instance.isMissing(paramInt))
          for (byte b2 = 0; b2 < b + 1; b2++) {
            if (instance.value(paramInt) <= d1 + (b2 + 1.0D) * d3) {
              arrayOfDouble1[b2] = arrayOfDouble1[b2] + instance.weight();
              break;
            } 
          }  
      } 
      double d = 0.0D;
      for (b1 = 0; b1 < b + 1; b1++) {
        if (arrayOfDouble1[b1] < 2.0D) {
          d = Double.MAX_VALUE;
          break;
        } 
        d -= arrayOfDouble1[b1] * Math.log((arrayOfDouble1[b1] - 1.0D) / d3);
      } 
      if (d < d4) {
        d4 = d;
        i = b + 1;
      } 
    } 
    double[] arrayOfDouble = null;
    if (i > 1 && d3 > 0.0D) {
      arrayOfDouble = new double[i - 1];
      for (byte b1 = 1; b1 < i; b1++)
        arrayOfDouble[b1 - 1] = d1 + d3 * b1; 
    } 
    this.m_CutPoints[paramInt] = arrayOfDouble;
  }
  
  protected void setOutputFormat() {
    if (this.m_CutPoints == null) {
      setOutputFormat(null);
      return;
    } 
    FastVector fastVector = new FastVector(getInputFormat().numAttributes());
    int i = getInputFormat().classIndex();
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      if (this.m_DiscretizeCols.isInRange(b) && getInputFormat().attribute(b).isNumeric() && getInputFormat().classIndex() != b) {
        if (!this.m_MakeBinary) {
          FastVector fastVector1 = new FastVector(1);
          if (this.m_CutPoints[b] == null) {
            fastVector1.addElement("'All'");
          } else {
            for (byte b1 = 0; b1 <= (this.m_CutPoints[b]).length; b1++) {
              if (b1 == 0) {
                fastVector1.addElement("'(-inf-" + Utils.doubleToString(this.m_CutPoints[b][b1], 6) + "]'");
              } else if (b1 == (this.m_CutPoints[b]).length) {
                fastVector1.addElement("'(" + Utils.doubleToString(this.m_CutPoints[b][b1 - 1], 6) + "-inf)'");
              } else {
                fastVector1.addElement("'(" + Utils.doubleToString(this.m_CutPoints[b][b1 - 1], 6) + "-" + Utils.doubleToString(this.m_CutPoints[b][b1], 6) + "]'");
              } 
            } 
          } 
          fastVector.addElement(new Attribute(getInputFormat().attribute(b).name(), fastVector1));
        } else if (this.m_CutPoints[b] == null) {
          FastVector fastVector1 = new FastVector(1);
          fastVector1.addElement("'All'");
          fastVector.addElement(new Attribute(getInputFormat().attribute(b).name(), fastVector1));
        } else {
          if (b < getInputFormat().classIndex())
            i += (this.m_CutPoints[b]).length - 1; 
          for (byte b1 = 0; b1 < (this.m_CutPoints[b]).length; b1++) {
            FastVector fastVector1 = new FastVector(2);
            fastVector1.addElement("'(-inf-" + Utils.doubleToString(this.m_CutPoints[b][b1], 6) + "]'");
            fastVector1.addElement("'(" + Utils.doubleToString(this.m_CutPoints[b][b1], 6) + "-inf)'");
            fastVector.addElement(new Attribute(getInputFormat().attribute(b).name(), fastVector1));
          } 
        } 
      } else {
        fastVector.addElement(getInputFormat().attribute(b).copy());
      } 
    } 
    Instances instances = new Instances(getInputFormat().relationName(), fastVector, 0);
    instances.setClassIndex(i);
    setOutputFormat(instances);
  }
  
  protected void convertInstance(Instance paramInstance) {
    Instance instance;
    byte b1 = 0;
    double[] arrayOfDouble = new double[outputFormatPeek().numAttributes()];
    for (byte b2 = 0; b2 < getInputFormat().numAttributes(); b2++) {
      if (this.m_DiscretizeCols.isInRange(b2) && getInputFormat().attribute(b2).isNumeric() && getInputFormat().classIndex() != b2) {
        double d = paramInstance.value(b2);
        if (this.m_CutPoints[b2] == null) {
          if (paramInstance.isMissing(b2)) {
            arrayOfDouble[b1] = Instance.missingValue();
          } else {
            arrayOfDouble[b1] = 0.0D;
          } 
          b1++;
        } else if (!this.m_MakeBinary) {
          if (paramInstance.isMissing(b2)) {
            arrayOfDouble[b1] = Instance.missingValue();
          } else {
            byte b;
            for (b = 0; b < (this.m_CutPoints[b2]).length && d > this.m_CutPoints[b2][b]; b++);
            arrayOfDouble[b1] = b;
          } 
          b1++;
        } else {
          for (byte b = 0; b < (this.m_CutPoints[b2]).length; b++) {
            if (paramInstance.isMissing(b2)) {
              arrayOfDouble[b1] = Instance.missingValue();
            } else if (d <= this.m_CutPoints[b2][b]) {
              arrayOfDouble[b1] = 0.0D;
            } else {
              arrayOfDouble[b1] = 1.0D;
            } 
            b1++;
          } 
        } 
      } else {
        arrayOfDouble[b1] = paramInstance.value(b2);
        b1++;
      } 
    } 
    SparseInstance sparseInstance = null;
    if (paramInstance instanceof SparseInstance) {
      sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble);
    } else {
      instance = new Instance(paramInstance.weight(), arrayOfDouble);
    } 
    copyStringValues(instance, false, paramInstance.dataset(), getInputStringIndex(), getOutputFormat(), getOutputStringIndex());
    instance.setDataset(getOutputFormat());
    push(instance);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new Discretize(), paramArrayOfString);
      } else {
        Filter.filterFile(new Discretize(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\Discretize.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */