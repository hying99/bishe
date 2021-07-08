package weka.attributeSelection;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Matrix;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SparseInstance;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class PrincipalComponents extends UnsupervisedAttributeEvaluator implements AttributeTransformer, OptionHandler {
  private Instances m_trainInstances;
  
  private Instances m_trainCopy;
  
  private Instances m_transformedFormat;
  
  private Instances m_originalSpaceFormat;
  
  private boolean m_hasClass;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private int m_numInstances;
  
  private double[][] m_correlation;
  
  private double[][] m_eigenvectors;
  
  private double[] m_eigenvalues = null;
  
  private int[] m_sortedEigens;
  
  private double m_sumOfEigenValues = 0.0D;
  
  private ReplaceMissingValues m_replaceMissingFilter;
  
  private Normalize m_normalizeFilter;
  
  private NominalToBinary m_nominalToBinFilter;
  
  private Remove m_attributeFilter;
  
  private Remove m_attribFilter;
  
  private int m_outputNumAtts = -1;
  
  private boolean m_normalize = true;
  
  private double m_coverVariance = 0.95D;
  
  private boolean m_transBackToOriginal = false;
  
  private int m_maxAttrsInName = 5;
  
  private double[][] m_eTranspose;
  
  public String globalInfo() {
    return "Performs a principal components analysis and transformation of the data. Use in conjunction with a Ranker search. Dimensionality reduction is accomplished by choosing enough eigenvectors to account for some percentage of the variance in the original data---default 0.95 (95%). Attribute noise can be filtered by transforming to the PC space, eliminating some of the worst eigenvectors, and then transforming back to the original space.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tDon't normalize input data.", "D", 0, "-D"));
    vector.addElement(new Option("\tRetain enough PC attributes to account \n\tfor this proportion of variance in the original data. (default = 0.95)", "R", 1, "-R"));
    vector.addElement(new Option("\tTransform through the PC space and \n\tback to the original space.", "O", 0, "-O"));
    vector.addElement(new Option("\tMaximum number of attributes to include in \ntransformed attribute names. (-1 = include all)", "A", 1, "-A"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('R', paramArrayOfString);
    if (str.length() != 0) {
      Double double_ = Double.valueOf(str);
      setVarianceCovered(double_.doubleValue());
    } 
    str = Utils.getOption('A', paramArrayOfString);
    if (str.length() != 0)
      setMaximumAttributeNames(Integer.parseInt(str)); 
    setNormalize(!Utils.getFlag('D', paramArrayOfString));
    setTransformBackToOriginal(Utils.getFlag('O', paramArrayOfString));
  }
  
  private void resetOptions() {
    this.m_coverVariance = 0.95D;
    this.m_normalize = true;
    this.m_sumOfEigenValues = 0.0D;
    this.m_transBackToOriginal = false;
  }
  
  public String normalizeTipText() {
    return "Normalize input data.";
  }
  
  public void setNormalize(boolean paramBoolean) {
    this.m_normalize = paramBoolean;
  }
  
  public boolean getNormalize() {
    return this.m_normalize;
  }
  
  public String varianceCoveredTipText() {
    return "Retain enough PC attributes to account for this proportion of variance.";
  }
  
  public void setVarianceCovered(double paramDouble) {
    this.m_coverVariance = paramDouble;
  }
  
  public double getVarianceCovered() {
    return this.m_coverVariance;
  }
  
  public void setMaximumAttributeNames(int paramInt) {
    this.m_maxAttrsInName = paramInt;
  }
  
  public int getMaximumAttributeNames() {
    return this.m_maxAttrsInName;
  }
  
  public String transformBackToOriginalTipText() {
    return "Transform through the PC space and back to the original space. If only the best n PCs are retained (by setting varianceCovered < 1) then this option will give a dataset in the original space but with less attribute noise.";
  }
  
  public void setTransformBackToOriginal(boolean paramBoolean) {
    this.m_transBackToOriginal = paramBoolean;
  }
  
  public boolean getTransformBackToOriginal() {
    return this.m_transBackToOriginal;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[4];
    byte b = 0;
    if (!getNormalize())
      arrayOfString[b++] = "-D"; 
    arrayOfString[b++] = "-R";
    arrayOfString[b++] = "" + getVarianceCovered();
    if (getTransformBackToOriginal())
      arrayOfString[b++] = "-O"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void buildEvaluator(Instances paramInstances) throws Exception {
    buildAttributeConstructor(paramInstances);
  }
  
  private void buildAttributeConstructor(Instances paramInstances) throws Exception {
    this.m_eigenvalues = null;
    this.m_outputNumAtts = -1;
    this.m_attributeFilter = null;
    this.m_nominalToBinFilter = null;
    this.m_sumOfEigenValues = 0.0D;
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    this.m_trainInstances = paramInstances;
    this.m_trainCopy = new Instances(this.m_trainInstances);
    this.m_replaceMissingFilter = new ReplaceMissingValues();
    this.m_replaceMissingFilter.setInputFormat(this.m_trainInstances);
    this.m_trainInstances = Filter.useFilter(this.m_trainInstances, (Filter)this.m_replaceMissingFilter);
    if (this.m_normalize) {
      this.m_normalizeFilter = new Normalize();
      this.m_normalizeFilter.setInputFormat(this.m_trainInstances);
      this.m_trainInstances = Filter.useFilter(this.m_trainInstances, (Filter)this.m_normalizeFilter);
    } 
    this.m_nominalToBinFilter = new NominalToBinary();
    this.m_nominalToBinFilter.setInputFormat(this.m_trainInstances);
    this.m_trainInstances = Filter.useFilter(this.m_trainInstances, (Filter)this.m_nominalToBinFilter);
    Vector vector = new Vector();
    for (byte b = 0; b < this.m_trainInstances.numAttributes(); b++) {
      if (this.m_trainInstances.numDistinctValues(b) <= 1)
        vector.addElement(new Integer(b)); 
    } 
    if (this.m_trainInstances.classIndex() >= 0) {
      this.m_hasClass = true;
      this.m_classIndex = this.m_trainInstances.classIndex();
      vector.addElement(new Integer(this.m_classIndex));
    } 
    if (vector.size() > 0) {
      this.m_attributeFilter = new Remove();
      int[] arrayOfInt = new int[vector.size()];
      for (byte b1 = 0; b1 < vector.size(); b1++)
        arrayOfInt[b1] = ((Integer)vector.elementAt(b1)).intValue(); 
      this.m_attributeFilter.setAttributeIndicesArray(arrayOfInt);
      this.m_attributeFilter.setInvertSelection(false);
      this.m_attributeFilter.setInputFormat(this.m_trainInstances);
      this.m_trainInstances = Filter.useFilter(this.m_trainInstances, (Filter)this.m_attributeFilter);
    } 
    this.m_numInstances = this.m_trainInstances.numInstances();
    this.m_numAttribs = this.m_trainInstances.numAttributes();
    fillCorrelation();
    double[] arrayOfDouble = new double[this.m_numAttribs];
    double[][] arrayOfDouble1 = new double[this.m_numAttribs][this.m_numAttribs];
    Matrix matrix = new Matrix(this.m_correlation);
    matrix.eigenvalueDecomposition(arrayOfDouble1, arrayOfDouble);
    this.m_eigenvectors = (double[][])arrayOfDouble1.clone();
    this.m_eigenvalues = (double[])arrayOfDouble.clone();
    int i;
    for (i = 0; i < this.m_eigenvalues.length; i++) {
      if (this.m_eigenvalues[i] < 0.0D)
        this.m_eigenvalues[i] = 0.0D; 
    } 
    this.m_sortedEigens = Utils.sort(this.m_eigenvalues);
    this.m_sumOfEigenValues = Utils.sum(this.m_eigenvalues);
    this.m_transformedFormat = setOutputFormat();
    if (this.m_transBackToOriginal) {
      this.m_originalSpaceFormat = setOutputFormatOriginal();
      i = (this.m_transformedFormat.classIndex() < 0) ? this.m_transformedFormat.numAttributes() : (this.m_transformedFormat.numAttributes() - 1);
      double[][] arrayOfDouble2 = new double[this.m_eigenvectors.length][i + 1];
      int j;
      for (j = this.m_numAttribs - 1; j > this.m_numAttribs - i - 1; j--) {
        for (byte b2 = 0; b2 < this.m_numAttribs; b2++)
          arrayOfDouble2[b2][this.m_numAttribs - j] = this.m_eigenvectors[b2][this.m_sortedEigens[j]]; 
      } 
      j = arrayOfDouble2.length;
      int k = (arrayOfDouble2[0]).length;
      this.m_eTranspose = new double[k][j];
      for (byte b1 = 0; b1 < k; b1++) {
        for (byte b2 = 0; b2 < j; b2++)
          this.m_eTranspose[b1][b2] = arrayOfDouble2[b2][b1]; 
      } 
    } 
  }
  
  public Instances transformedHeader() throws Exception {
    if (this.m_eigenvalues == null)
      throw new Exception("Principal components hasn't been built yet"); 
    return this.m_transBackToOriginal ? this.m_originalSpaceFormat : this.m_transformedFormat;
  }
  
  public Instances transformedData() throws Exception {
    Instances instances;
    if (this.m_eigenvalues == null)
      throw new Exception("Principal components hasn't been built yet"); 
    if (this.m_transBackToOriginal) {
      instances = new Instances(this.m_originalSpaceFormat);
    } else {
      instances = new Instances(this.m_transformedFormat);
    } 
    for (byte b = 0; b < this.m_trainCopy.numInstances(); b++) {
      Instance instance = convertInstance(this.m_trainCopy.instance(b));
      instances.add(instance);
    } 
    return instances;
  }
  
  public double evaluateAttribute(int paramInt) throws Exception {
    if (this.m_eigenvalues == null)
      throw new Exception("Principal components hasn't been built yet!"); 
    if (this.m_transBackToOriginal)
      return 1.0D; 
    double d = 0.0D;
    for (int i = this.m_numAttribs - 1; i >= this.m_numAttribs - paramInt - 1; i--)
      d += this.m_eigenvalues[this.m_sortedEigens[i]]; 
    return 1.0D - d / this.m_sumOfEigenValues;
  }
  
  private void fillCorrelation() {
    this.m_correlation = new double[this.m_numAttribs][this.m_numAttribs];
    double[] arrayOfDouble1 = new double[this.m_numInstances];
    double[] arrayOfDouble2 = new double[this.m_numInstances];
    for (byte b = 0; b < this.m_numAttribs; b++) {
      for (byte b1 = 0; b1 < this.m_numAttribs; b1++) {
        if (b == b1) {
          this.m_correlation[b][b1] = 1.0D;
        } else {
          for (byte b2 = 0; b2 < this.m_numInstances; b2++) {
            arrayOfDouble1[b2] = this.m_trainInstances.instance(b2).value(b);
            arrayOfDouble2[b2] = this.m_trainInstances.instance(b2).value(b1);
          } 
          double d = Utils.correlation(arrayOfDouble1, arrayOfDouble2, this.m_numInstances);
          this.m_correlation[b][b1] = d;
          this.m_correlation[b][b1] = d;
        } 
      } 
    } 
  }
  
  private String principalComponentsSummary() {
    StringBuffer stringBuffer = new StringBuffer();
    double d = 0.0D;
    Instances instances = null;
    int i = 0;
    try {
      instances = setOutputFormat();
      i = (instances.classIndex() < 0) ? instances.numAttributes() : (instances.numAttributes() - 1);
    } catch (Exception exception) {}
    stringBuffer.append("Correlation matrix\n" + matrixToString(this.m_correlation) + "\n\n");
    stringBuffer.append("eigenvalue\tproportion\tcumulative\n");
    int j;
    for (j = this.m_numAttribs - 1; j > this.m_numAttribs - i - 1; j--) {
      d += this.m_eigenvalues[this.m_sortedEigens[j]];
      stringBuffer.append(Utils.doubleToString(this.m_eigenvalues[this.m_sortedEigens[j]], 9, 5) + "\t" + Utils.doubleToString(this.m_eigenvalues[this.m_sortedEigens[j]] / this.m_sumOfEigenValues, 9, 5) + "\t" + Utils.doubleToString(d / this.m_sumOfEigenValues, 9, 5) + "\t" + instances.attribute(this.m_numAttribs - j - 1).name() + "\n");
    } 
    stringBuffer.append("\nEigenvectors\n");
    for (j = 1; j <= i; j++)
      stringBuffer.append(" V" + j + '\t'); 
    stringBuffer.append("\n");
    for (j = 0; j < this.m_numAttribs; j++) {
      for (int k = this.m_numAttribs - 1; k > this.m_numAttribs - i - 1; k--)
        stringBuffer.append(Utils.doubleToString(this.m_eigenvectors[j][this.m_sortedEigens[k]], 7, 4) + "\t"); 
      stringBuffer.append(this.m_trainInstances.attribute(j).name() + '\n');
    } 
    if (this.m_transBackToOriginal)
      stringBuffer.append("\nPC space transformed back to original space.\n(Note: can't evaluate attributes in the original space)\n"); 
    return stringBuffer.toString();
  }
  
  public String toString() {
    return (this.m_eigenvalues == null) ? "Principal components hasn't been built yet!" : ("\tPrincipal Components Attribute Transformer\n\n" + principalComponentsSummary());
  }
  
  private String matrixToString(double[][] paramArrayOfdouble) {
    StringBuffer stringBuffer = new StringBuffer();
    int i = paramArrayOfdouble.length - 1;
    for (byte b = 0; b <= i; b++) {
      for (byte b1 = 0; b1 <= i; b1++) {
        stringBuffer.append(Utils.doubleToString(paramArrayOfdouble[b][b1], 6, 2) + " ");
        if (b1 == i)
          stringBuffer.append('\n'); 
      } 
    } 
    return stringBuffer.toString();
  }
  
  private Instance convertInstanceToOriginal(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = null;
    if (this.m_hasClass) {
      arrayOfDouble = new double[this.m_numAttribs + 1];
    } else {
      arrayOfDouble = new double[this.m_numAttribs];
    } 
    if (this.m_hasClass)
      arrayOfDouble[this.m_numAttribs] = paramInstance.value(paramInstance.numAttributes() - 1); 
    for (byte b = 0; b < (this.m_eTranspose[0]).length; b++) {
      double d = 0.0D;
      for (byte b1 = 1; b1 < this.m_eTranspose.length; b1++)
        d += this.m_eTranspose[b1][b] * paramInstance.value(b1 - 1); 
      arrayOfDouble[b] = d;
    } 
    return (Instance)((paramInstance instanceof SparseInstance) ? new SparseInstance(paramInstance.weight(), arrayOfDouble) : new Instance(paramInstance.weight(), arrayOfDouble));
  }
  
  public Instance convertInstance(Instance paramInstance) throws Exception {
    if (this.m_eigenvalues == null)
      throw new Exception("convertInstance: Principal components not built yet"); 
    double[] arrayOfDouble = new double[this.m_outputNumAtts];
    Instance instance = (Instance)paramInstance.copy();
    if (!paramInstance.equalHeaders(this.m_trainCopy.instance(0)))
      throw new Exception("Can't convert instance: header's don't match: PrincipalComponents"); 
    this.m_replaceMissingFilter.input(instance);
    this.m_replaceMissingFilter.batchFinished();
    instance = this.m_replaceMissingFilter.output();
    if (this.m_normalize) {
      this.m_normalizeFilter.input(instance);
      this.m_normalizeFilter.batchFinished();
      instance = this.m_normalizeFilter.output();
    } 
    this.m_nominalToBinFilter.input(instance);
    this.m_nominalToBinFilter.batchFinished();
    instance = this.m_nominalToBinFilter.output();
    if (this.m_attributeFilter != null) {
      this.m_attributeFilter.input(instance);
      this.m_attributeFilter.batchFinished();
      instance = this.m_attributeFilter.output();
    } 
    if (this.m_hasClass)
      arrayOfDouble[this.m_outputNumAtts - 1] = paramInstance.value(paramInstance.classIndex()); 
    double d = 0.0D;
    for (int i = this.m_numAttribs - 1; i >= 0; i--) {
      double d1 = 0.0D;
      for (byte b = 0; b < this.m_numAttribs; b++)
        d1 += this.m_eigenvectors[b][this.m_sortedEigens[i]] * instance.value(b); 
      arrayOfDouble[this.m_numAttribs - i - 1] = d1;
      d += this.m_eigenvalues[this.m_sortedEigens[i]];
      if (d / this.m_sumOfEigenValues >= this.m_coverVariance)
        break; 
    } 
    return (Instance)(!this.m_transBackToOriginal ? ((paramInstance instanceof SparseInstance) ? new SparseInstance(paramInstance.weight(), arrayOfDouble) : new Instance(paramInstance.weight(), arrayOfDouble)) : ((paramInstance instanceof SparseInstance) ? convertInstanceToOriginal((Instance)new SparseInstance(paramInstance.weight(), arrayOfDouble)) : convertInstanceToOriginal(new Instance(paramInstance.weight(), arrayOfDouble))));
  }
  
  private Instances setOutputFormatOriginal() throws Exception {
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < this.m_numAttribs; b++) {
      String str = this.m_trainInstances.attribute(b).name();
      fastVector.addElement(new Attribute(str));
    } 
    if (this.m_hasClass)
      fastVector.addElement(this.m_trainCopy.classAttribute().copy()); 
    Instances instances = new Instances(this.m_trainCopy.relationName() + "->PC->original space", fastVector, 0);
    if (this.m_hasClass)
      instances.setClassIndex(instances.numAttributes() - 1); 
    return instances;
  }
  
  private Instances setOutputFormat() throws Exception {
    if (this.m_eigenvalues == null)
      return null; 
    double d = 0.0D;
    FastVector fastVector = new FastVector();
    for (int i = this.m_numAttribs - 1; i >= 0; i--) {
      int[] arrayOfInt;
      StringBuffer stringBuffer = new StringBuffer();
      double[] arrayOfDouble = new double[this.m_numAttribs];
      int j;
      for (j = 0; j < this.m_numAttribs; j++)
        arrayOfDouble[j] = -Math.abs(this.m_eigenvectors[j][this.m_sortedEigens[i]]); 
      j = (this.m_maxAttrsInName > 0) ? Math.min(this.m_numAttribs, this.m_maxAttrsInName) : this.m_numAttribs;
      if (this.m_numAttribs > 0) {
        arrayOfInt = Utils.sort(arrayOfDouble);
      } else {
        arrayOfInt = new int[this.m_numAttribs];
        for (byte b1 = 0; b1 < this.m_numAttribs; b1++)
          arrayOfInt[b1] = b1; 
      } 
      for (byte b = 0; b < j; b++) {
        double d1 = this.m_eigenvectors[arrayOfInt[b]][this.m_sortedEigens[i]];
        if (b > 0 && d1 >= 0.0D)
          stringBuffer.append("+"); 
        stringBuffer.append(Utils.doubleToString(d1, 5, 3) + this.m_trainInstances.attribute(arrayOfInt[b]).name());
      } 
      if (j < this.m_numAttribs)
        stringBuffer.append("..."); 
      fastVector.addElement(new Attribute(stringBuffer.toString()));
      d += this.m_eigenvalues[this.m_sortedEigens[i]];
      if (d / this.m_sumOfEigenValues >= this.m_coverVariance)
        break; 
    } 
    if (this.m_hasClass)
      fastVector.addElement(this.m_trainCopy.classAttribute().copy()); 
    Instances instances = new Instances(this.m_trainInstances.relationName() + "_principal components", fastVector, 0);
    if (this.m_hasClass)
      instances.setClassIndex(instances.numAttributes() - 1); 
    this.m_outputNumAtts = instances.numAttributes();
    return instances;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(AttributeSelection.SelectAttributes(new PrincipalComponents(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\PrincipalComponents.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */