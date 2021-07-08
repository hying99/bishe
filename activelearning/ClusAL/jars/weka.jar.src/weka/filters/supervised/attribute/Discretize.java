package weka.filters.supervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.ContingencyTables;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.SparseInstance;
import weka.core.SpecialFunctions;
import weka.core.UnassignedClassException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;

public class Discretize extends Filter implements SupervisedFilter, OptionHandler, WeightedInstancesHandler {
  protected Range m_DiscretizeCols = new Range();
  
  protected double[][] m_CutPoints = (double[][])null;
  
  protected boolean m_MakeBinary = false;
  
  protected boolean m_UseBetterEncoding = false;
  
  protected boolean m_UseKononenko = false;
  
  public Discretize() {
    setAttributeIndices("first-last");
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(7);
    vector.addElement(new Option("\tSpecifies list of columns to Discretize. First and last are valid indexes.\n\t(default none)", "R", 1, "-R <col1,col2-col4,...>"));
    vector.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
    vector.addElement(new Option("\tOutput binary attributes for discretized attributes.", "D", 0, "-D"));
    vector.addElement(new Option("\tUse better encoding of split point for MDL.", "E", 0, "-E"));
    vector.addElement(new Option("\tUse Kononenko's MDL criterion.", "K", 0, "-K"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setMakeBinary(Utils.getFlag('D', paramArrayOfString));
    setUseBetterEncoding(Utils.getFlag('E', paramArrayOfString));
    setUseKononenko(Utils.getFlag('K', paramArrayOfString));
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
    if (getUseBetterEncoding())
      arrayOfString[b++] = "-E"; 
    if (getUseKononenko())
      arrayOfString[b++] = "-K"; 
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
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_DiscretizeCols.setUpper(paramInstances.numAttributes() - 1);
    this.m_CutPoints = (double[][])null;
    if (paramInstances.classIndex() < 0)
      throw new UnassignedClassException("Cannot use class-based discretization: no class assigned to the dataset"); 
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("Supervised discretization not possible: class is not nominal!"); 
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
    return "An instance filter that discretizes a range of numeric attributes in the dataset into nominal attributes. Discretization is by Fayyad & Irani's MDL method (the default).";
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
  
  public String useKononenkoTipText() {
    return "Use Kononenko's MDL criterion. If set to false uses the Fayyad & Irani criterion.";
  }
  
  public boolean getUseKononenko() {
    return this.m_UseKononenko;
  }
  
  public void setUseKononenko(boolean paramBoolean) {
    this.m_UseKononenko = paramBoolean;
  }
  
  public String useBetterEncodingTipText() {
    return "Uses a more efficient split point encoding.";
  }
  
  public boolean getUseBetterEncoding() {
    return this.m_UseBetterEncoding;
  }
  
  public void setUseBetterEncoding(boolean paramBoolean) {
    this.m_UseBetterEncoding = paramBoolean;
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
    Instances instances = null;
    this.m_CutPoints = new double[getInputFormat().numAttributes()][];
    for (int i = getInputFormat().numAttributes() - 1; i >= 0; i--) {
      if (this.m_DiscretizeCols.isInRange(i) && getInputFormat().attribute(i).isNumeric()) {
        if (instances == null)
          instances = new Instances(getInputFormat()); 
        calculateCutPointsByMDL(i, instances);
      } 
    } 
  }
  
  protected void calculateCutPointsByMDL(int paramInt, Instances paramInstances) {
    paramInstances.sort(paramInstances.attribute(paramInt));
    int i = paramInstances.numInstances();
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      if (paramInstances.instance(b).isMissing(paramInt)) {
        i = b;
        break;
      } 
    } 
    this.m_CutPoints[paramInt] = cutPointsForSubset(paramInstances, paramInt, 0, i);
  }
  
  private boolean KononenkosMDL(double[] paramArrayOfdouble, double[][] paramArrayOfdouble1, double paramDouble, int paramInt) {
    double d3 = 0.0D;
    double d4 = 0.0D;
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < paramArrayOfdouble.length; b2++) {
      if (paramArrayOfdouble[b2] > 0.0D)
        b1++; 
    } 
    double d1 = SpecialFunctions.log2Binomial(paramDouble + b1 - 1.0D, (b1 - 1));
    double d2 = SpecialFunctions.log2Multinomial(paramDouble, paramArrayOfdouble);
    double d5 = d2 + d1;
    for (b2 = 0; b2 < paramArrayOfdouble1.length; b2++) {
      double d = Utils.sum(paramArrayOfdouble1[b2]);
      d3 += SpecialFunctions.log2Binomial(d + b1 - 1.0D, (b1 - 1));
      d4 += SpecialFunctions.log2Multinomial(d, paramArrayOfdouble1[b2]);
    } 
    double d6 = Utils.log2(paramInt) + d3 + d4;
    return (d5 > d6);
  }
  
  private boolean FayyadAndIranisMDL(double[] paramArrayOfdouble, double[][] paramArrayOfdouble1, double paramDouble, int paramInt) {
    double d1 = ContingencyTables.entropy(paramArrayOfdouble);
    double d2 = ContingencyTables.entropyConditionedOnRows(paramArrayOfdouble1);
    double d3 = d1 - d2;
    byte b1 = 0;
    byte b4;
    for (b4 = 0; b4 < paramArrayOfdouble.length; b4++) {
      if (paramArrayOfdouble[b4] > 0.0D)
        b1++; 
    } 
    byte b3 = 0;
    for (b4 = 0; b4 < (paramArrayOfdouble1[0]).length; b4++) {
      if (paramArrayOfdouble1[0][b4] > 0.0D)
        b3++; 
    } 
    byte b2 = 0;
    for (b4 = 0; b4 < (paramArrayOfdouble1[1]).length; b4++) {
      if (paramArrayOfdouble1[1][b4] > 0.0D)
        b2++; 
    } 
    double d4 = ContingencyTables.entropy(paramArrayOfdouble1[0]);
    double d5 = ContingencyTables.entropy(paramArrayOfdouble1[1]);
    double d6 = Utils.log2(Math.pow(3.0D, b1) - 2.0D) - b1 * d1 - b2 * d5 - b3 * d4;
    return (d3 > (Utils.log2(paramInt) + d6) / paramDouble);
  }
  
  private double[] cutPointsForSubset(Instances paramInstances, int paramInt1, int paramInt2, int paramInt3) {
    double d1 = -1.7976931348623157E308D;
    double d2 = -1.0D;
    int i = -1;
    int j = 0;
    int k = 0;
    if (paramInt3 - paramInt2 < 2)
      return null; 
    double[][] arrayOfDouble1 = new double[2][paramInstances.numClasses()];
    int m;
    for (m = paramInt2; m < paramInt3; m++) {
      j = (int)(j + paramInstances.instance(m).weight());
      arrayOfDouble1[1][(int)paramInstances.instance(m).classValue()] = arrayOfDouble1[1][(int)paramInstances.instance(m).classValue()] + paramInstances.instance(m).weight();
    } 
    double[] arrayOfDouble = new double[paramInstances.numClasses()];
    System.arraycopy(arrayOfDouble1[1], 0, arrayOfDouble, 0, paramInstances.numClasses());
    double d4 = ContingencyTables.entropy(arrayOfDouble);
    double d3 = d4;
    double[][] arrayOfDouble2 = new double[2][paramInstances.numClasses()];
    for (m = paramInt2; m < paramInt3 - 1; m++) {
      arrayOfDouble1[0][(int)paramInstances.instance(m).classValue()] = arrayOfDouble1[0][(int)paramInstances.instance(m).classValue()] + paramInstances.instance(m).weight();
      arrayOfDouble1[1][(int)paramInstances.instance(m).classValue()] = arrayOfDouble1[1][(int)paramInstances.instance(m).classValue()] - paramInstances.instance(m).weight();
      if (paramInstances.instance(m).value(paramInt1) < paramInstances.instance(m + 1).value(paramInt1)) {
        d1 = (paramInstances.instance(m).value(paramInt1) + paramInstances.instance(m + 1).value(paramInt1)) / 2.0D;
        double d = ContingencyTables.entropyConditionedOnRows(arrayOfDouble1);
        if (d < d3) {
          d2 = d1;
          d3 = d;
          i = m;
          System.arraycopy(arrayOfDouble1[0], 0, arrayOfDouble2[0], 0, paramInstances.numClasses());
          System.arraycopy(arrayOfDouble1[1], 0, arrayOfDouble2[1], 0, paramInstances.numClasses());
        } 
        k++;
      } 
    } 
    if (!this.m_UseBetterEncoding)
      k = paramInt3 - paramInt2 - 1; 
    double d5 = d4 - d3;
    if (d5 <= 0.0D)
      return null; 
    if ((this.m_UseKononenko && KononenkosMDL(arrayOfDouble, arrayOfDouble2, j, k)) || (!this.m_UseKononenko && FayyadAndIranisMDL(arrayOfDouble, arrayOfDouble2, j, k))) {
      double[] arrayOfDouble5;
      double[] arrayOfDouble3 = cutPointsForSubset(paramInstances, paramInt1, paramInt2, i + 1);
      double[] arrayOfDouble4 = cutPointsForSubset(paramInstances, paramInt1, i + 1, paramInt3);
      if (arrayOfDouble3 == null && arrayOfDouble4 == null) {
        arrayOfDouble5 = new double[1];
        arrayOfDouble5[0] = d2;
      } else if (arrayOfDouble4 == null) {
        arrayOfDouble5 = new double[arrayOfDouble3.length + 1];
        System.arraycopy(arrayOfDouble3, 0, arrayOfDouble5, 0, arrayOfDouble3.length);
        arrayOfDouble5[arrayOfDouble3.length] = d2;
      } else if (arrayOfDouble3 == null) {
        arrayOfDouble5 = new double[1 + arrayOfDouble4.length];
        arrayOfDouble5[0] = d2;
        System.arraycopy(arrayOfDouble4, 0, arrayOfDouble5, 1, arrayOfDouble4.length);
      } else {
        arrayOfDouble5 = new double[arrayOfDouble3.length + arrayOfDouble4.length + 1];
        System.arraycopy(arrayOfDouble3, 0, arrayOfDouble5, 0, arrayOfDouble3.length);
        arrayOfDouble5[arrayOfDouble3.length] = d2;
        System.arraycopy(arrayOfDouble4, 0, arrayOfDouble5, arrayOfDouble3.length + 1, arrayOfDouble4.length);
      } 
      return arrayOfDouble5;
    } 
    return null;
  }
  
  protected void setOutputFormat() {
    if (this.m_CutPoints == null) {
      setOutputFormat(null);
      return;
    } 
    FastVector fastVector = new FastVector(getInputFormat().numAttributes());
    int i = getInputFormat().classIndex();
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      if (this.m_DiscretizeCols.isInRange(b) && getInputFormat().attribute(b).isNumeric()) {
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
      if (this.m_DiscretizeCols.isInRange(b2) && getInputFormat().attribute(b2).isNumeric()) {
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
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filters\supervised\attribute\Discretize.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */