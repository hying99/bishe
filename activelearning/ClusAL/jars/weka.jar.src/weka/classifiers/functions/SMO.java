package weka.classifiers.functions;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.supportVector.Kernel;
import weka.classifiers.functions.supportVector.NormalizedPolyKernel;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.functions.supportVector.SMOset;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.SelectedTag;
import weka.core.SerializedObject;
import weka.core.Tag;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

public class SMO extends Classifier implements WeightedInstancesHandler {
  public static final int FILTER_NORMALIZE = 0;
  
  public static final int FILTER_STANDARDIZE = 1;
  
  public static final int FILTER_NONE = 2;
  
  public static final Tag[] TAGS_FILTER = new Tag[] { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
  
  protected BinarySMO[][] m_classifiers = (BinarySMO[][])null;
  
  protected double m_exponent = 1.0D;
  
  protected boolean m_lowerOrder = false;
  
  protected double m_gamma = 0.01D;
  
  protected double m_C = 1.0D;
  
  protected double m_eps = 1.0E-12D;
  
  protected double m_tol = 0.001D;
  
  protected int m_filterType = 0;
  
  protected boolean m_featureSpaceNormalization = false;
  
  protected boolean m_useRBF = false;
  
  protected int m_cacheSize = 250007;
  
  protected NominalToBinary m_NominalToBinary;
  
  protected Filter m_Filter = null;
  
  protected ReplaceMissingValues m_Missing;
  
  protected boolean m_onlyNumeric;
  
  protected int m_classIndex = -1;
  
  protected Attribute m_classAttribute;
  
  protected boolean m_checksTurnedOff;
  
  protected static double m_Del = 4.94E-321D;
  
  protected boolean m_fitLogisticModels = false;
  
  protected int m_numFolds = -1;
  
  protected int m_randomSeed = 1;
  
  public String globalInfo() {
    return "Implements John Platt's sequential minimal optimization algorithm for training a support vector classifier.\n\nThis implementation globally replaces all missing values and transforms nominal attributes into binary ones. It also normalizes all attributes by default. (In that case the coefficients in the output are based on the normalized data, not the original data --- this is important for interpreting the classifier.)\n\nMulti-class problems are solved using pairwise classification.\n\nTo obtain proper probability estimates, use the option that fits logistic regression models to the outputs of the support vector machine. In the multi-class case the predicted probabilities are coupled using Hastie and Tibshirani's pairwise coupling method.\n\nNote: for improved speed normalization should be turned off when operating on SparseInstances.\n\nFor more information on the SMO algorithm, see\n\nJ. Platt (1998). \"Fast Training of Support Vector Machines using Sequential Minimal Optimization\". Advances in Kernel Methods - Support Vector Learning, B. Schoelkopf, C. Burges, and A. Smola, eds., MIT Press. \n\nS.S. Keerthi, S.K. Shevade, C. Bhattacharyya, K.R.K. Murthy,  \"Improvements to Platt's SMO Algorithm for SVM Classifier Design\".  Neural Computation, 13(3), pp 637-649, 2001.";
  }
  
  public void turnChecksOff() {
    this.m_checksTurnedOff = true;
  }
  
  public void turnChecksOn() {
    this.m_checksTurnedOff = false;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (!this.m_checksTurnedOff) {
      if (paramInstances.checkForStringAttributes())
        throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
      if (paramInstances.classAttribute().isNumeric())
        throw new UnsupportedClassTypeException("SMO can't handle a numeric class! UseSMOreg for performing regression."); 
      paramInstances = new Instances(paramInstances);
      paramInstances.deleteWithMissingClass();
      if (paramInstances.numInstances() == 0)
        throw new Exception("No training instances without a missing class!"); 
      Instances instances = new Instances(paramInstances, paramInstances.numInstances());
      for (byte b = 0; b < paramInstances.numInstances(); b++) {
        if (paramInstances.instance(b).weight() > 0.0D)
          instances.add(paramInstances.instance(b)); 
      } 
      if (instances.numInstances() == 0)
        throw new Exception("No training instances left after removing instance with either a weight null or a missing class!"); 
      paramInstances = instances;
    } 
    this.m_onlyNumeric = true;
    if (!this.m_checksTurnedOff)
      for (byte b = 0; b < paramInstances.numAttributes(); b++) {
        if (b != paramInstances.classIndex() && !paramInstances.attribute(b).isNumeric()) {
          this.m_onlyNumeric = false;
          break;
        } 
      }  
    if (!this.m_checksTurnedOff) {
      this.m_Missing = new ReplaceMissingValues();
      this.m_Missing.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_Missing);
    } else {
      this.m_Missing = null;
    } 
    if (!this.m_onlyNumeric) {
      this.m_NominalToBinary = new NominalToBinary();
      this.m_NominalToBinary.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_NominalToBinary);
    } else {
      this.m_NominalToBinary = null;
    } 
    if (this.m_filterType == 1) {
      this.m_Filter = (Filter)new Standardize();
      this.m_Filter.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, this.m_Filter);
    } else if (this.m_filterType == 0) {
      this.m_Filter = (Filter)new Normalize();
      this.m_Filter.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, this.m_Filter);
    } else {
      this.m_Filter = null;
    } 
    this.m_classIndex = paramInstances.classIndex();
    this.m_classAttribute = paramInstances.classAttribute();
    Instances[] arrayOfInstances = new Instances[paramInstances.numClasses()];
    byte b1;
    for (b1 = 0; b1 < paramInstances.numClasses(); b1++)
      arrayOfInstances[b1] = new Instances(paramInstances, paramInstances.numInstances()); 
    for (b1 = 0; b1 < paramInstances.numInstances(); b1++) {
      Instance instance = paramInstances.instance(b1);
      arrayOfInstances[(int)instance.classValue()].add(instance);
    } 
    for (b1 = 0; b1 < paramInstances.numClasses(); b1++)
      arrayOfInstances[b1].compactify(); 
    Random random = new Random(this.m_randomSeed);
    this.m_classifiers = new BinarySMO[paramInstances.numClasses()][paramInstances.numClasses()];
    for (byte b2 = 0; b2 < paramInstances.numClasses(); b2++) {
      for (int i = b2 + 1; i < paramInstances.numClasses(); i++) {
        this.m_classifiers[b2][i] = new BinarySMO(this);
        Instances instances = new Instances(paramInstances, paramInstances.numInstances());
        byte b;
        for (b = 0; b < arrayOfInstances[b2].numInstances(); b++)
          instances.add(arrayOfInstances[b2].instance(b)); 
        for (b = 0; b < arrayOfInstances[i].numInstances(); b++)
          instances.add(arrayOfInstances[i].instance(b)); 
        instances.compactify();
        instances.randomize(random);
        this.m_classifiers[b2][i].buildClassifier(instances, b2, i, this.m_fitLogisticModels, this.m_numFolds, this.m_randomSeed);
      } 
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (!this.m_checksTurnedOff) {
      this.m_Missing.input(paramInstance);
      this.m_Missing.batchFinished();
      paramInstance = this.m_Missing.output();
    } 
    if (!this.m_onlyNumeric) {
      this.m_NominalToBinary.input(paramInstance);
      this.m_NominalToBinary.batchFinished();
      paramInstance = this.m_NominalToBinary.output();
    } 
    if (this.m_Filter != null) {
      this.m_Filter.input(paramInstance);
      this.m_Filter.batchFinished();
      paramInstance = this.m_Filter.output();
    } 
    if (!this.m_fitLogisticModels) {
      double[] arrayOfDouble = new double[paramInstance.numClasses()];
      for (byte b1 = 0; b1 < paramInstance.numClasses(); b1++) {
        for (int i = b1 + 1; i < paramInstance.numClasses(); i++) {
          if ((this.m_classifiers[b1][i]).m_alpha != null || (this.m_classifiers[b1][i]).m_sparseWeights != null) {
            double d = this.m_classifiers[b1][i].SVMOutput(-1, paramInstance);
            if (d > 0.0D) {
              arrayOfDouble[i] = arrayOfDouble[i] + 1.0D;
            } else {
              arrayOfDouble[b1] = arrayOfDouble[b1] + 1.0D;
            } 
          } 
        } 
      } 
      Utils.normalize(arrayOfDouble);
      return arrayOfDouble;
    } 
    if (paramInstance.numClasses() == 2) {
      double[] arrayOfDouble = new double[2];
      arrayOfDouble[0] = this.m_classifiers[0][1].SVMOutput(-1, paramInstance);
      arrayOfDouble[1] = Instance.missingValue();
      return (this.m_classifiers[0][1]).m_logistic.distributionForInstance(new Instance(1.0D, arrayOfDouble));
    } 
    double[][] arrayOfDouble1 = new double[paramInstance.numClasses()][paramInstance.numClasses()];
    double[][] arrayOfDouble2 = new double[paramInstance.numClasses()][paramInstance.numClasses()];
    for (byte b = 0; b < paramInstance.numClasses(); b++) {
      for (int i = b + 1; i < paramInstance.numClasses(); i++) {
        if ((this.m_classifiers[b][i]).m_alpha != null || (this.m_classifiers[b][i]).m_sparseWeights != null) {
          double[] arrayOfDouble = new double[2];
          arrayOfDouble[0] = this.m_classifiers[b][i].SVMOutput(-1, paramInstance);
          arrayOfDouble[1] = Instance.missingValue();
          arrayOfDouble1[b][i] = (this.m_classifiers[b][i]).m_logistic.distributionForInstance(new Instance(1.0D, arrayOfDouble))[0];
          arrayOfDouble2[b][i] = (this.m_classifiers[b][i]).m_sumOfWeights;
        } 
      } 
    } 
    return pairwiseCoupling(arrayOfDouble2, arrayOfDouble1);
  }
  
  public double[] pairwiseCoupling(double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2) {
    double[] arrayOfDouble1 = new double[paramArrayOfdouble2.length];
    for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
      arrayOfDouble1[b1] = 1.0D / arrayOfDouble1.length; 
    double[][] arrayOfDouble = new double[paramArrayOfdouble2.length][paramArrayOfdouble2.length];
    for (byte b2 = 0; b2 < paramArrayOfdouble2.length; b2++) {
      for (int i = b2 + 1; i < paramArrayOfdouble2.length; i++)
        arrayOfDouble[b2][i] = 0.5D; 
    } 
    double[] arrayOfDouble2 = new double[arrayOfDouble1.length];
    byte b3;
    for (b3 = 0; b3 < arrayOfDouble1.length; b3++) {
      for (int i = b3 + 1; i < arrayOfDouble1.length; i++) {
        arrayOfDouble2[b3] = arrayOfDouble2[b3] + paramArrayOfdouble1[b3][i] * paramArrayOfdouble2[b3][i];
        arrayOfDouble2[i] = arrayOfDouble2[i] + paramArrayOfdouble1[b3][i] * (1.0D - paramArrayOfdouble2[b3][i]);
      } 
    } 
    while (true) {
      b3 = 0;
      double[] arrayOfDouble3 = new double[arrayOfDouble1.length];
      byte b;
      for (b = 0; b < arrayOfDouble1.length; b++) {
        for (int i = b + 1; i < arrayOfDouble1.length; i++) {
          arrayOfDouble3[b] = arrayOfDouble3[b] + paramArrayOfdouble1[b][i] * arrayOfDouble[b][i];
          arrayOfDouble3[i] = arrayOfDouble3[i] + paramArrayOfdouble1[b][i] * (1.0D - arrayOfDouble[b][i]);
        } 
      } 
      for (b = 0; b < arrayOfDouble1.length; b++) {
        if (arrayOfDouble2[b] == 0.0D || arrayOfDouble3[b] == 0.0D) {
          if (arrayOfDouble1[b] > 0.0D)
            b3 = 1; 
          arrayOfDouble1[b] = 0.0D;
        } else {
          double d1 = arrayOfDouble2[b] / arrayOfDouble3[b];
          double d2 = arrayOfDouble1[b];
          arrayOfDouble1[b] = arrayOfDouble1[b] * d1;
          if (Math.abs(d2 - arrayOfDouble1[b]) > 0.001D)
            b3 = 1; 
        } 
      } 
      Utils.normalize(arrayOfDouble1);
      for (b = 0; b < paramArrayOfdouble2.length; b++) {
        for (int i = b + 1; i < paramArrayOfdouble2.length; i++)
          arrayOfDouble[b][i] = arrayOfDouble1[b] / (arrayOfDouble1[b] + arrayOfDouble1[i]); 
      } 
      if (b3 == 0)
        return arrayOfDouble1; 
    } 
  }
  
  public int[] obtainVotes(Instance paramInstance) throws Exception {
    if (!this.m_checksTurnedOff) {
      this.m_Missing.input(paramInstance);
      this.m_Missing.batchFinished();
      paramInstance = this.m_Missing.output();
    } 
    if (!this.m_onlyNumeric) {
      this.m_NominalToBinary.input(paramInstance);
      this.m_NominalToBinary.batchFinished();
      paramInstance = this.m_NominalToBinary.output();
    } 
    if (this.m_Filter != null) {
      this.m_Filter.input(paramInstance);
      this.m_Filter.batchFinished();
      paramInstance = this.m_Filter.output();
    } 
    int[] arrayOfInt = new int[paramInstance.numClasses()];
    for (byte b = 0; b < paramInstance.numClasses(); b++) {
      for (int i = b + 1; i < paramInstance.numClasses(); i++) {
        double d = this.m_classifiers[b][i].SVMOutput(-1, paramInstance);
        if (d > 0.0D) {
          arrayOfInt[i] = arrayOfInt[i] + 1;
        } else {
          arrayOfInt[b] = arrayOfInt[b] + 1;
        } 
      } 
    } 
    return arrayOfInt;
  }
  
  public double[][][] sparseWeights() {
    int i = this.m_classAttribute.numValues();
    double[][][] arrayOfDouble = new double[i][i][];
    for (byte b = 0; b < i; b++) {
      for (int j = b + 1; j < i; j++)
        arrayOfDouble[b][j] = (this.m_classifiers[b][j]).m_sparseWeights; 
    } 
    return arrayOfDouble;
  }
  
  public int[][][] sparseIndices() {
    int i = this.m_classAttribute.numValues();
    int[][][] arrayOfInt = new int[i][i][];
    for (byte b = 0; b < i; b++) {
      for (int j = b + 1; j < i; j++)
        arrayOfInt[b][j] = (this.m_classifiers[b][j]).m_sparseIndices; 
    } 
    return arrayOfInt;
  }
  
  public double[][] bias() {
    int i = this.m_classAttribute.numValues();
    double[][] arrayOfDouble = new double[i][i];
    for (byte b = 0; b < i; b++) {
      for (int j = b + 1; j < i; j++)
        arrayOfDouble[b][j] = (this.m_classifiers[b][j]).m_b; 
    } 
    return arrayOfDouble;
  }
  
  public int numClassAttributeValues() {
    return this.m_classAttribute.numValues();
  }
  
  public String[] classAttributeNames() {
    int i = this.m_classAttribute.numValues();
    String[] arrayOfString = new String[i];
    for (byte b = 0; b < i; b++)
      arrayOfString[b] = this.m_classAttribute.value(b); 
    return arrayOfString;
  }
  
  public String[][][] attributeNames() {
    int i = this.m_classAttribute.numValues();
    String[][][] arrayOfString = new String[i][i][];
    for (byte b = 0; b < i; b++) {
      for (int j = b + 1; j < i; j++) {
        int k = (this.m_classifiers[b][j]).m_data.numAttributes();
        String[] arrayOfString1 = new String[k];
        for (byte b1 = 0; b1 < k; b1++)
          arrayOfString1[b1] = (this.m_classifiers[b][j]).m_data.attribute(b1).name(); 
        arrayOfString[b][j] = arrayOfString1;
      } 
    } 
    return arrayOfString;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(13);
    vector.addElement(new Option("\tThe complexity constant C. (default 1)", "C", 1, "-C <double>"));
    vector.addElement(new Option("\tThe exponent for the polynomial kernel. (default 1)", "E", 1, "-E <double>"));
    vector.addElement(new Option("\tGamma for the RBF kernel. (default 0.01)", "G", 1, "-G <double>"));
    vector.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither. (default 0=normalize)", "N", 1, "-N"));
    vector.addElement(new Option("\tFeature-space normalization (only for\n\tnon-linear polynomial kernels).", "F", 0, "-F"));
    vector.addElement(new Option("\tUse lower-order terms (only for non-linear\n\tpolynomial kernels).", "O", 0, "-O"));
    vector.addElement(new Option("\tUse RBF kernel. (default poly)", "R", 0, "-R"));
    vector.addElement(new Option("\tThe size of the kernel cache. (default 250007, use 0 for full cache)", "A", 1, "-A <int>"));
    vector.addElement(new Option("\tThe tolerance parameter. (default 1.0e-3)", "T", 1, "-T <double>"));
    vector.addElement(new Option("\tThe epsilon for round-off error. (default 1.0e-12)", "P", 1, "-P <double>"));
    vector.addElement(new Option("\tFit logistic models to SVM outputs. ", "M", 0, "-M"));
    vector.addElement(new Option("\tThe number of folds for the internal\n\tcross-validation. (default -1, use training data)", "V", 1, "-V <double>"));
    vector.addElement(new Option("\tThe random number seed. (default 1)", "W", 1, "-W <double>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('C', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_C = (new Double(str1)).doubleValue();
    } else {
      this.m_C = 1.0D;
    } 
    String str2 = Utils.getOption('E', paramArrayOfString);
    if (str2.length() != 0) {
      this.m_exponent = (new Double(str2)).doubleValue();
    } else {
      this.m_exponent = 1.0D;
    } 
    String str3 = Utils.getOption('G', paramArrayOfString);
    if (str3.length() != 0) {
      this.m_gamma = (new Double(str3)).doubleValue();
    } else {
      this.m_gamma = 0.01D;
    } 
    String str4 = Utils.getOption('A', paramArrayOfString);
    if (str4.length() != 0) {
      this.m_cacheSize = Integer.parseInt(str4);
    } else {
      this.m_cacheSize = 250007;
    } 
    String str5 = Utils.getOption('T', paramArrayOfString);
    if (str5.length() != 0) {
      this.m_tol = (new Double(str5)).doubleValue();
    } else {
      this.m_tol = 0.001D;
    } 
    String str6 = Utils.getOption('P', paramArrayOfString);
    if (str6.length() != 0) {
      this.m_eps = (new Double(str6)).doubleValue();
    } else {
      this.m_eps = 1.0E-12D;
    } 
    this.m_useRBF = Utils.getFlag('R', paramArrayOfString);
    String str7 = Utils.getOption('N', paramArrayOfString);
    if (str7.length() != 0) {
      setFilterType(new SelectedTag(Integer.parseInt(str7), TAGS_FILTER));
    } else {
      setFilterType(new SelectedTag(0, TAGS_FILTER));
    } 
    this.m_featureSpaceNormalization = Utils.getFlag('F', paramArrayOfString);
    if (this.m_useRBF && this.m_featureSpaceNormalization)
      throw new Exception("RBF machine doesn't require feature-space normalization."); 
    if (this.m_exponent == 1.0D && this.m_featureSpaceNormalization)
      throw new Exception("Can't use feature-space normalization with linear machine."); 
    this.m_lowerOrder = Utils.getFlag('O', paramArrayOfString);
    if (this.m_useRBF && this.m_lowerOrder)
      throw new Exception("Can't use lower-order terms with RBF machine."); 
    if (this.m_exponent == 1.0D && this.m_lowerOrder)
      throw new Exception("Can't use lower-order terms with linear machine."); 
    this.m_fitLogisticModels = Utils.getFlag('M', paramArrayOfString);
    String str8 = Utils.getOption('V', paramArrayOfString);
    if (str8.length() != 0) {
      this.m_numFolds = Integer.parseInt(str8);
    } else {
      this.m_numFolds = -1;
    } 
    String str9 = Utils.getOption('W', paramArrayOfString);
    if (str9.length() != 0) {
      this.m_randomSeed = Integer.parseInt(str9);
    } else {
      this.m_randomSeed = 1;
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[21];
    byte b = 0;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + this.m_C;
    arrayOfString[b++] = "-E";
    arrayOfString[b++] = "" + this.m_exponent;
    arrayOfString[b++] = "-G";
    arrayOfString[b++] = "" + this.m_gamma;
    arrayOfString[b++] = "-A";
    arrayOfString[b++] = "" + this.m_cacheSize;
    arrayOfString[b++] = "-T";
    arrayOfString[b++] = "" + this.m_tol;
    arrayOfString[b++] = "-P";
    arrayOfString[b++] = "" + this.m_eps;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + this.m_filterType;
    if (this.m_featureSpaceNormalization)
      arrayOfString[b++] = "-F"; 
    if (this.m_lowerOrder)
      arrayOfString[b++] = "-O"; 
    if (this.m_useRBF)
      arrayOfString[b++] = "-R"; 
    if (this.m_fitLogisticModels)
      arrayOfString[b++] = "-M"; 
    arrayOfString[b++] = "-V";
    arrayOfString[b++] = "" + this.m_numFolds;
    arrayOfString[b++] = "-W";
    arrayOfString[b++] = "" + this.m_randomSeed;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String exponentTipText() {
    return "The exponent for the polynomial kernel.";
  }
  
  public double getExponent() {
    return this.m_exponent;
  }
  
  public void setExponent(double paramDouble) {
    if (paramDouble == 1.0D) {
      this.m_featureSpaceNormalization = false;
      this.m_lowerOrder = false;
    } 
    this.m_exponent = paramDouble;
  }
  
  public String gammaTipText() {
    return "The value of the gamma parameter for RBF kernels.";
  }
  
  public double getGamma() {
    return this.m_gamma;
  }
  
  public void setGamma(double paramDouble) {
    this.m_gamma = paramDouble;
  }
  
  public String cTipText() {
    return "The complexity parameter C.";
  }
  
  public double getC() {
    return this.m_C;
  }
  
  public void setC(double paramDouble) {
    this.m_C = paramDouble;
  }
  
  public String toleranceParameterTipText() {
    return "The tolerance parameter (shouldn't be changed).";
  }
  
  public double getToleranceParameter() {
    return this.m_tol;
  }
  
  public void setToleranceParameter(double paramDouble) {
    this.m_tol = paramDouble;
  }
  
  public String epsilonTipText() {
    return "The epsilon for round-off error (shouldn't be changed).";
  }
  
  public double getEpsilon() {
    return this.m_eps;
  }
  
  public void setEpsilon(double paramDouble) {
    this.m_eps = paramDouble;
  }
  
  public String cacheSizeTipText() {
    return "The size of the kernel cache (should be a prime number). Use 0 for full cache.";
  }
  
  public int getCacheSize() {
    return this.m_cacheSize;
  }
  
  public void setCacheSize(int paramInt) {
    this.m_cacheSize = paramInt;
  }
  
  public String filterTypeTipText() {
    return "Determines how/if the data will be transformed.";
  }
  
  public SelectedTag getFilterType() {
    return new SelectedTag(this.m_filterType, TAGS_FILTER);
  }
  
  public void setFilterType(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_FILTER)
      this.m_filterType = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public String useRBFTipText() {
    return "Whether to use an RBF kernel instead of a polynomial one.";
  }
  
  public boolean getUseRBF() {
    return this.m_useRBF;
  }
  
  public void setUseRBF(boolean paramBoolean) {
    if (paramBoolean) {
      this.m_featureSpaceNormalization = false;
      this.m_lowerOrder = false;
    } 
    this.m_useRBF = paramBoolean;
  }
  
  public String featureSpaceNormalizationTipText() {
    return "Whether feature-space normalization is performed (only available for non-linear polynomial kernels).";
  }
  
  public boolean getFeatureSpaceNormalization() throws Exception {
    return this.m_featureSpaceNormalization;
  }
  
  public void setFeatureSpaceNormalization(boolean paramBoolean) throws Exception {
    if (this.m_useRBF || this.m_exponent == 1.0D) {
      this.m_featureSpaceNormalization = false;
    } else {
      this.m_featureSpaceNormalization = paramBoolean;
    } 
  }
  
  public String lowerOrderTermsTipText() {
    return "Whether lower order polyomials are also used (only available for non-linear polynomial kernels).";
  }
  
  public boolean getLowerOrderTerms() {
    return this.m_lowerOrder;
  }
  
  public void setLowerOrderTerms(boolean paramBoolean) {
    if (this.m_exponent == 1.0D || this.m_useRBF) {
      this.m_lowerOrder = false;
    } else {
      this.m_lowerOrder = paramBoolean;
    } 
  }
  
  public String buildLogisticModelsTipText() {
    return "Whether to fit logistic models to the outputs (for proper probability estimates).";
  }
  
  public boolean getBuildLogisticModels() {
    return this.m_fitLogisticModels;
  }
  
  public void setBuildLogisticModels(boolean paramBoolean) {
    this.m_fitLogisticModels = paramBoolean;
  }
  
  public String numFoldsTipText() {
    return "The number of folds for cross-validation used to generate training data for logistic models (-1 means use training data).";
  }
  
  public int getNumFolds() {
    return this.m_numFolds;
  }
  
  public void setNumFolds(int paramInt) {
    this.m_numFolds = paramInt;
  }
  
  public String randomSeedTipText() {
    return "Random number seed for the cross-validation.";
  }
  
  public int getRandomSeed() {
    return this.m_randomSeed;
  }
  
  public void setRandomSeed(int paramInt) {
    this.m_randomSeed = paramInt;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    boolean bool = false;
    if (this.m_classAttribute == null)
      return "SMO: No model built yet."; 
    try {
      stringBuffer.append("SMO\n\n");
      for (byte b = 0; b < this.m_classAttribute.numValues(); b++) {
        for (int i = b + 1; i < this.m_classAttribute.numValues(); i++) {
          stringBuffer.append("Classifier for classes: " + this.m_classAttribute.value(b) + ", " + this.m_classAttribute.value(i) + "\n\n");
          stringBuffer.append(this.m_classifiers[b][i]);
          if (this.m_fitLogisticModels) {
            stringBuffer.append("\n\n");
            if ((this.m_classifiers[b][i]).m_logistic == null) {
              stringBuffer.append("No logistic model has been fit.\n");
            } else {
              stringBuffer.append((this.m_classifiers[b][i]).m_logistic);
            } 
          } 
          stringBuffer.append("\n\n");
        } 
      } 
    } catch (Exception exception) {
      return "Can't print SMO classifier.";
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      SMO sMO = new SMO();
      System.out.println(Evaluation.evaluateModel(sMO, paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  protected class BinarySMO implements Serializable {
    protected double[] m_alpha;
    
    protected double m_b;
    
    protected double m_bLow;
    
    protected double m_bUp;
    
    protected int m_iLow;
    
    protected int m_iUp;
    
    protected Instances m_data;
    
    protected double[] m_weights;
    
    protected double[] m_sparseWeights;
    
    protected int[] m_sparseIndices;
    
    protected Kernel m_kernel;
    
    protected double[] m_class;
    
    protected double[] m_errors;
    
    protected SMOset m_I0;
    
    protected SMOset m_I1;
    
    protected SMOset m_I2;
    
    protected SMOset m_I3;
    
    protected SMOset m_I4;
    
    protected SMOset m_supportVectors;
    
    protected Logistic m_logistic;
    
    protected double m_sumOfWeights;
    
    private final SMO this$0;
    
    protected BinarySMO(SMO this$0) {
      this.this$0 = this$0;
      this.m_logistic = null;
      this.m_sumOfWeights = 0.0D;
    }
    
    protected void fitLogistic(Instances param1Instances, int param1Int1, int param1Int2, int param1Int3, Random param1Random) throws Exception {
      FastVector fastVector1 = new FastVector(2);
      fastVector1.addElement(new Attribute("pred"));
      FastVector fastVector2 = new FastVector(2);
      fastVector2.addElement(param1Instances.classAttribute().value(param1Int1));
      fastVector2.addElement(param1Instances.classAttribute().value(param1Int2));
      fastVector1.addElement(new Attribute("class", fastVector2));
      Instances instances = new Instances("data", fastVector1, param1Instances.numInstances());
      instances.setClassIndex(1);
      if (param1Int3 <= 0) {
        for (byte b = 0; b < param1Instances.numInstances(); b++) {
          Instance instance = param1Instances.instance(b);
          double[] arrayOfDouble = new double[2];
          arrayOfDouble[0] = SVMOutput(-1, instance);
          if (instance.classValue() == param1Int2)
            arrayOfDouble[1] = 1.0D; 
          instances.add(new Instance(instance.weight(), arrayOfDouble));
        } 
      } else {
        if (param1Int3 > param1Instances.numInstances())
          param1Int3 = param1Instances.numInstances(); 
        param1Instances = new Instances(param1Instances);
        param1Instances.randomize(param1Random);
        param1Instances.stratify(param1Int3);
        for (byte b = 0; b < param1Int3; b++) {
          Instances instances1 = param1Instances.trainCV(param1Int3, b, param1Random);
          SerializedObject serializedObject = new SerializedObject(this);
          BinarySMO binarySMO = (BinarySMO)serializedObject.getObject();
          binarySMO.buildClassifier(instances1, param1Int1, param1Int2, false, -1, -1);
          Instances instances2 = param1Instances.testCV(param1Int3, b);
          for (byte b1 = 0; b1 < instances2.numInstances(); b1++) {
            double[] arrayOfDouble = new double[2];
            arrayOfDouble[0] = binarySMO.SVMOutput(-1, instances2.instance(b1));
            if (instances2.instance(b1).classValue() == param1Int2)
              arrayOfDouble[1] = 1.0D; 
            instances.add(new Instance(instances2.instance(b1).weight(), arrayOfDouble));
          } 
        } 
      } 
      this.m_logistic = new Logistic();
      this.m_logistic.buildClassifier(instances);
    }
    
    protected void buildClassifier(Instances param1Instances, int param1Int1, int param1Int2, boolean param1Boolean, int param1Int3, int param1Int4) throws Exception {
      this.m_bUp = -1.0D;
      this.m_bLow = 1.0D;
      this.m_b = 0.0D;
      this.m_alpha = null;
      this.m_data = null;
      this.m_weights = null;
      this.m_errors = null;
      this.m_logistic = null;
      this.m_I0 = null;
      this.m_I1 = null;
      this.m_I2 = null;
      this.m_I3 = null;
      this.m_I4 = null;
      this.m_sparseWeights = null;
      this.m_sparseIndices = null;
      this.m_sumOfWeights = param1Instances.sumOfWeights();
      this.m_class = new double[param1Instances.numInstances()];
      this.m_iUp = -1;
      this.m_iLow = -1;
      byte b;
      for (b = 0; b < this.m_class.length; b++) {
        if ((int)param1Instances.instance(b).classValue() == param1Int1) {
          this.m_class[b] = -1.0D;
          this.m_iLow = b;
        } else if ((int)param1Instances.instance(b).classValue() == param1Int2) {
          this.m_class[b] = 1.0D;
          this.m_iUp = b;
        } else {
          throw new Exception("This should never happen!");
        } 
      } 
      if (this.m_iUp == -1 || this.m_iLow == -1) {
        if (this.m_iUp != -1) {
          this.m_b = -1.0D;
        } else if (this.m_iLow != -1) {
          this.m_b = 1.0D;
        } else {
          this.m_class = null;
          return;
        } 
        if (!this.this$0.m_useRBF && this.this$0.m_exponent == 1.0D) {
          this.m_sparseWeights = new double[0];
          this.m_sparseIndices = new int[0];
          this.m_class = null;
        } else {
          this.m_supportVectors = new SMOset(0);
          this.m_alpha = new double[0];
          this.m_class = new double[0];
        } 
        if (param1Boolean)
          fitLogistic(param1Instances, param1Int1, param1Int2, param1Int3, new Random(param1Int4)); 
        return;
      } 
      this.m_data = param1Instances;
      if (!this.this$0.m_useRBF && this.this$0.m_exponent == 1.0D) {
        this.m_weights = new double[this.m_data.numAttributes()];
      } else {
        this.m_weights = null;
      } 
      this.m_alpha = new double[this.m_data.numInstances()];
      this.m_supportVectors = new SMOset(this.m_data.numInstances());
      this.m_I0 = new SMOset(this.m_data.numInstances());
      this.m_I1 = new SMOset(this.m_data.numInstances());
      this.m_I2 = new SMOset(this.m_data.numInstances());
      this.m_I3 = new SMOset(this.m_data.numInstances());
      this.m_I4 = new SMOset(this.m_data.numInstances());
      this.m_sparseWeights = null;
      this.m_sparseIndices = null;
      this.m_errors = new double[this.m_data.numInstances()];
      this.m_errors[this.m_iLow] = 1.0D;
      this.m_errors[this.m_iUp] = -1.0D;
      if (this.this$0.m_useRBF) {
        this.m_kernel = (Kernel)new RBFKernel(this.m_data, this.this$0.m_cacheSize, this.this$0.m_gamma);
      } else if (this.this$0.m_featureSpaceNormalization) {
        this.m_kernel = (Kernel)new NormalizedPolyKernel(this.m_data, this.this$0.m_cacheSize, this.this$0.m_exponent, this.this$0.m_lowerOrder);
      } else {
        this.m_kernel = (Kernel)new PolyKernel(this.m_data, this.this$0.m_cacheSize, this.this$0.m_exponent, this.this$0.m_lowerOrder);
      } 
      for (b = 0; b < this.m_class.length; b++) {
        if (this.m_class[b] == 1.0D) {
          this.m_I1.insert(b);
        } else {
          this.m_I4.insert(b);
        } 
      } 
      b = 0;
      boolean bool = true;
      while (true) {
        if (b > 0 || bool) {
          b = 0;
          if (bool) {
            for (byte b1 = 0; b1 < this.m_alpha.length; b1++) {
              if (examineExample(b1))
                b++; 
            } 
          } else {
            for (byte b1 = 0; b1 < this.m_alpha.length; b1++) {
              if (this.m_alpha[b1] > 0.0D && this.m_alpha[b1] < this.this$0.m_C * this.m_data.instance(b1).weight()) {
                if (examineExample(b1))
                  b++; 
                if (this.m_bUp > this.m_bLow - 2.0D * this.this$0.m_tol) {
                  b = 0;
                  break;
                } 
              } 
            } 
          } 
          if (bool) {
            bool = false;
            continue;
          } 
          if (b == 0)
            bool = true; 
          continue;
        } 
        this.m_b = (this.m_bLow + this.m_bUp) / 2.0D;
        this.m_kernel.clean();
        this.m_errors = null;
        this.m_I0 = this.m_I1 = this.m_I2 = this.m_I3 = this.m_I4 = null;
        if (!this.this$0.m_useRBF && this.this$0.m_exponent == 1.0D) {
          this.m_supportVectors = null;
          this.m_class = null;
          if (!this.this$0.m_checksTurnedOff) {
            this.m_data = new Instances(this.m_data, 0);
          } else {
            this.m_data = null;
          } 
          double[] arrayOfDouble = new double[this.m_weights.length];
          int[] arrayOfInt = new int[this.m_weights.length];
          byte b1 = 0;
          for (byte b2 = 0; b2 < this.m_weights.length; b2++) {
            if (this.m_weights[b2] != 0.0D) {
              arrayOfDouble[b1] = this.m_weights[b2];
              arrayOfInt[b1] = b2;
              b1++;
            } 
          } 
          this.m_sparseWeights = new double[b1];
          this.m_sparseIndices = new int[b1];
          System.arraycopy(arrayOfDouble, 0, this.m_sparseWeights, 0, b1);
          System.arraycopy(arrayOfInt, 0, this.m_sparseIndices, 0, b1);
          this.m_weights = null;
          this.m_alpha = null;
        } 
        if (param1Boolean)
          fitLogistic(param1Instances, param1Int1, param1Int2, param1Int3, new Random(param1Int4)); 
        return;
      } 
    }
    
    protected double SVMOutput(int param1Int, Instance param1Instance) throws Exception {
      double d = 0.0D;
      if (!this.this$0.m_useRBF && this.this$0.m_exponent == 1.0D) {
        if (this.m_sparseWeights == null) {
          int i = param1Instance.numValues();
          for (byte b = 0; b < i; b++) {
            if (param1Instance.index(b) != this.this$0.m_classIndex)
              d += this.m_weights[param1Instance.index(b)] * param1Instance.valueSparse(b); 
          } 
        } else {
          int i = param1Instance.numValues();
          int j = this.m_sparseWeights.length;
          byte b1 = 0;
          byte b2 = 0;
          while (b1 < i && b2 < j) {
            int k = param1Instance.index(b1);
            int m = this.m_sparseIndices[b2];
            if (k == m) {
              if (k != this.this$0.m_classIndex)
                d += param1Instance.valueSparse(b1) * this.m_sparseWeights[b2]; 
              b1++;
              b2++;
              continue;
            } 
            if (k > m) {
              b2++;
              continue;
            } 
            b1++;
          } 
        } 
      } else {
        int i;
        for (i = this.m_supportVectors.getNext(-1); i != -1; i = this.m_supportVectors.getNext(i))
          d += this.m_class[i] * this.m_alpha[i] * this.m_kernel.eval(param1Int, i, param1Instance); 
      } 
      d -= this.m_b;
      return d;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      byte b = 0;
      if (this.m_alpha == null && this.m_sparseWeights == null)
        return "BinarySMO: No model built yet.\n"; 
      try {
        stringBuffer.append("BinarySMO\n\n");
        if (!this.this$0.m_useRBF && this.this$0.m_exponent == 1.0D) {
          stringBuffer.append("Machine linear: showing attribute weights, ");
          stringBuffer.append("not support vectors.\n\n");
          for (byte b1 = 0; b1 < this.m_sparseWeights.length; b1++) {
            if (this.m_sparseIndices[b1] != this.this$0.m_classIndex) {
              if (b) {
                stringBuffer.append(" + ");
              } else {
                stringBuffer.append("   ");
              } 
              stringBuffer.append(Utils.doubleToString(this.m_sparseWeights[b1], 12, 4) + " * ");
              if (this.this$0.m_filterType == 1) {
                stringBuffer.append("(standardized) ");
              } else if (this.this$0.m_filterType == 0) {
                stringBuffer.append("(normalized) ");
              } 
              if (!this.this$0.m_checksTurnedOff) {
                stringBuffer.append(this.m_data.attribute(this.m_sparseIndices[b1]).name() + "\n");
              } else {
                stringBuffer.append("attribute with index " + this.m_sparseIndices[b1] + "\n");
              } 
              b++;
            } 
          } 
        } else {
          for (byte b1 = 0; b1 < this.m_alpha.length; b1++) {
            if (this.m_supportVectors.contains(b1)) {
              double d = this.m_alpha[b1];
              if (this.m_class[b1] == 1.0D) {
                if (b > 0)
                  stringBuffer.append(" + "); 
              } else {
                stringBuffer.append(" - ");
              } 
              stringBuffer.append(Utils.doubleToString(d, 12, 4) + " * <");
              for (byte b2 = 0; b2 < this.m_data.numAttributes(); b2++) {
                if (b2 != this.m_data.classIndex())
                  stringBuffer.append(this.m_data.instance(b1).toString(b2)); 
                if (b2 != this.m_data.numAttributes() - 1)
                  stringBuffer.append(" "); 
              } 
              stringBuffer.append("> * X]\n");
              b++;
            } 
          } 
        } 
        if (this.m_b > 0.0D) {
          stringBuffer.append(" - " + Utils.doubleToString(this.m_b, 12, 4));
        } else {
          stringBuffer.append(" + " + Utils.doubleToString(-this.m_b, 12, 4));
        } 
        if (this.this$0.m_useRBF || this.this$0.m_exponent != 1.0D)
          stringBuffer.append("\n\nNumber of support vectors: " + this.m_supportVectors.numElements()); 
        int i = 0;
        int j = -1;
        if (this.m_kernel != null) {
          i = this.m_kernel.numEvals();
          j = this.m_kernel.numCacheHits();
        } 
        stringBuffer.append("\n\nNumber of kernel evaluations: " + i);
        if (j >= 0 && i > 0) {
          double d = 1.0D - i * 1.0D / (j + i);
          stringBuffer.append(" (" + Utils.doubleToString(d * 100.0D, 7, 3).trim() + "% cached)");
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
        return "Can't print BinarySMO classifier.";
      } 
      return stringBuffer.toString();
    }
    
    protected boolean examineExample(int param1Int) throws Exception {
      double d3;
      int i = -1;
      double d1 = this.m_class[param1Int];
      double d2 = this.m_alpha[param1Int];
      if (this.m_I0.contains(param1Int)) {
        d3 = this.m_errors[param1Int];
      } else {
        d3 = SVMOutput(param1Int, this.m_data.instance(param1Int)) + this.m_b - d1;
        this.m_errors[param1Int] = d3;
        if ((this.m_I1.contains(param1Int) || this.m_I2.contains(param1Int)) && d3 < this.m_bUp) {
          this.m_bUp = d3;
          this.m_iUp = param1Int;
        } else if ((this.m_I3.contains(param1Int) || this.m_I4.contains(param1Int)) && d3 > this.m_bLow) {
          this.m_bLow = d3;
          this.m_iLow = param1Int;
        } 
      } 
      boolean bool = true;
      if ((this.m_I0.contains(param1Int) || this.m_I1.contains(param1Int) || this.m_I2.contains(param1Int)) && this.m_bLow - d3 > 2.0D * this.this$0.m_tol) {
        bool = false;
        i = this.m_iLow;
      } 
      if ((this.m_I0.contains(param1Int) || this.m_I3.contains(param1Int) || this.m_I4.contains(param1Int)) && d3 - this.m_bUp > 2.0D * this.this$0.m_tol) {
        bool = false;
        i = this.m_iUp;
      } 
      if (bool)
        return false; 
      if (this.m_I0.contains(param1Int))
        if (this.m_bLow - d3 > d3 - this.m_bUp) {
          i = this.m_iLow;
        } else {
          i = this.m_iUp;
        }  
      if (i == -1)
        throw new Exception("This should never happen!"); 
      return takeStep(i, param1Int, d3);
    }
    
    protected boolean takeStep(int param1Int1, int param1Int2, double param1Double) throws Exception {
      double d7;
      double d8;
      double d14;
      double d15 = this.this$0.m_C * this.m_data.instance(param1Int1).weight();
      double d16 = this.this$0.m_C * this.m_data.instance(param1Int2).weight();
      if (param1Int1 == param1Int2)
        return false; 
      double d1 = this.m_alpha[param1Int1];
      double d2 = this.m_alpha[param1Int2];
      double d3 = this.m_class[param1Int1];
      double d4 = this.m_class[param1Int2];
      double d5 = this.m_errors[param1Int1];
      double d6 = d3 * d4;
      if (d3 != d4) {
        d7 = Math.max(0.0D, d2 - d1);
        d8 = Math.min(d16, d15 + d2 - d1);
      } else {
        d7 = Math.max(0.0D, d1 + d2 - d15);
        d8 = Math.min(d16, d1 + d2);
      } 
      if (d7 >= d8)
        return false; 
      double d9 = this.m_kernel.eval(param1Int1, param1Int1, this.m_data.instance(param1Int1));
      double d10 = this.m_kernel.eval(param1Int1, param1Int2, this.m_data.instance(param1Int1));
      double d11 = this.m_kernel.eval(param1Int2, param1Int2, this.m_data.instance(param1Int2));
      double d12 = 2.0D * d10 - d9 - d11;
      if (d12 < 0.0D) {
        d14 = d2 - d4 * (d5 - param1Double) / d12;
        if (d14 < d7) {
          d14 = d7;
        } else if (d14 > d8) {
          d14 = d8;
        } 
      } else {
        double d17 = SVMOutput(param1Int1, this.m_data.instance(param1Int1));
        double d18 = SVMOutput(param1Int2, this.m_data.instance(param1Int2));
        double d19 = d17 + this.m_b - d3 * d1 * d9 - d4 * d2 * d10;
        double d20 = d18 + this.m_b - d3 * d1 * d10 - d4 * d2 * d11;
        double d23 = d1 + d6 * d2;
        double d21 = d23 - d6 * d7 + d7 - 0.5D * d9 * (d23 - d6 * d7) * (d23 - d6 * d7) - 0.5D * d11 * d7 * d7 - d6 * d10 * (d23 - d6 * d7) * d7 - d3 * (d23 - d6 * d7) * d19 - d4 * d7 * d20;
        double d22 = d23 - d6 * d8 + d8 - 0.5D * d9 * (d23 - d6 * d8) * (d23 - d6 * d8) - 0.5D * d11 * d8 * d8 - d6 * d10 * (d23 - d6 * d8) * d8 - d3 * (d23 - d6 * d8) * d19 - d4 * d8 * d20;
        if (d21 > d22 + this.this$0.m_eps) {
          d14 = d7;
        } else if (d21 < d22 - this.this$0.m_eps) {
          d14 = d8;
        } else {
          d14 = d2;
        } 
      } 
      if (Math.abs(d14 - d2) < this.this$0.m_eps * (d14 + d2 + this.this$0.m_eps))
        return false; 
      if (d14 > d16 - SMO.m_Del * d16) {
        d14 = d16;
      } else if (d14 <= SMO.m_Del * d16) {
        d14 = 0.0D;
      } 
      double d13 = d1 + d6 * (d2 - d14);
      if (d13 > d15 - SMO.m_Del * d15) {
        d13 = d15;
      } else if (d13 <= SMO.m_Del * d15) {
        d13 = 0.0D;
      } 
      if (d13 > 0.0D) {
        this.m_supportVectors.insert(param1Int1);
      } else {
        this.m_supportVectors.delete(param1Int1);
      } 
      if (d13 > 0.0D && d13 < d15) {
        this.m_I0.insert(param1Int1);
      } else {
        this.m_I0.delete(param1Int1);
      } 
      if (d3 == 1.0D && d13 == 0.0D) {
        this.m_I1.insert(param1Int1);
      } else {
        this.m_I1.delete(param1Int1);
      } 
      if (d3 == -1.0D && d13 == d15) {
        this.m_I2.insert(param1Int1);
      } else {
        this.m_I2.delete(param1Int1);
      } 
      if (d3 == 1.0D && d13 == d15) {
        this.m_I3.insert(param1Int1);
      } else {
        this.m_I3.delete(param1Int1);
      } 
      if (d3 == -1.0D && d13 == 0.0D) {
        this.m_I4.insert(param1Int1);
      } else {
        this.m_I4.delete(param1Int1);
      } 
      if (d14 > 0.0D) {
        this.m_supportVectors.insert(param1Int2);
      } else {
        this.m_supportVectors.delete(param1Int2);
      } 
      if (d14 > 0.0D && d14 < d16) {
        this.m_I0.insert(param1Int2);
      } else {
        this.m_I0.delete(param1Int2);
      } 
      if (d4 == 1.0D && d14 == 0.0D) {
        this.m_I1.insert(param1Int2);
      } else {
        this.m_I1.delete(param1Int2);
      } 
      if (d4 == -1.0D && d14 == d16) {
        this.m_I2.insert(param1Int2);
      } else {
        this.m_I2.delete(param1Int2);
      } 
      if (d4 == 1.0D && d14 == d16) {
        this.m_I3.insert(param1Int2);
      } else {
        this.m_I3.delete(param1Int2);
      } 
      if (d4 == -1.0D && d14 == 0.0D) {
        this.m_I4.insert(param1Int2);
      } else {
        this.m_I4.delete(param1Int2);
      } 
      if (!this.this$0.m_useRBF && this.this$0.m_exponent == 1.0D) {
        Instance instance1 = this.m_data.instance(param1Int1);
        for (byte b1 = 0; b1 < instance1.numValues(); b1++) {
          if (instance1.index(b1) != this.m_data.classIndex())
            this.m_weights[instance1.index(b1)] = this.m_weights[instance1.index(b1)] + d3 * (d13 - d1) * instance1.valueSparse(b1); 
        } 
        Instance instance2 = this.m_data.instance(param1Int2);
        for (byte b2 = 0; b2 < instance2.numValues(); b2++) {
          if (instance2.index(b2) != this.m_data.classIndex())
            this.m_weights[instance2.index(b2)] = this.m_weights[instance2.index(b2)] + d4 * (d14 - d2) * instance2.valueSparse(b2); 
        } 
      } 
      int i;
      for (i = this.m_I0.getNext(-1); i != -1; i = this.m_I0.getNext(i)) {
        if (i != param1Int1 && i != param1Int2)
          this.m_errors[i] = this.m_errors[i] + d3 * (d13 - d1) * this.m_kernel.eval(param1Int1, i, this.m_data.instance(param1Int1)) + d4 * (d14 - d2) * this.m_kernel.eval(param1Int2, i, this.m_data.instance(param1Int2)); 
      } 
      this.m_errors[param1Int1] = this.m_errors[param1Int1] + d3 * (d13 - d1) * d9 + d4 * (d14 - d2) * d10;
      this.m_errors[param1Int2] = this.m_errors[param1Int2] + d3 * (d13 - d1) * d10 + d4 * (d14 - d2) * d11;
      this.m_alpha[param1Int1] = d13;
      this.m_alpha[param1Int2] = d14;
      this.m_bLow = -1.7976931348623157E308D;
      this.m_bUp = Double.MAX_VALUE;
      this.m_iLow = -1;
      this.m_iUp = -1;
      for (i = this.m_I0.getNext(-1); i != -1; i = this.m_I0.getNext(i)) {
        if (this.m_errors[i] < this.m_bUp) {
          this.m_bUp = this.m_errors[i];
          this.m_iUp = i;
        } 
        if (this.m_errors[i] > this.m_bLow) {
          this.m_bLow = this.m_errors[i];
          this.m_iLow = i;
        } 
      } 
      if (!this.m_I0.contains(param1Int1))
        if (this.m_I3.contains(param1Int1) || this.m_I4.contains(param1Int1)) {
          if (this.m_errors[param1Int1] > this.m_bLow) {
            this.m_bLow = this.m_errors[param1Int1];
            this.m_iLow = param1Int1;
          } 
        } else if (this.m_errors[param1Int1] < this.m_bUp) {
          this.m_bUp = this.m_errors[param1Int1];
          this.m_iUp = param1Int1;
        }  
      if (!this.m_I0.contains(param1Int2))
        if (this.m_I3.contains(param1Int2) || this.m_I4.contains(param1Int2)) {
          if (this.m_errors[param1Int2] > this.m_bLow) {
            this.m_bLow = this.m_errors[param1Int2];
            this.m_iLow = param1Int2;
          } 
        } else if (this.m_errors[param1Int2] < this.m_bUp) {
          this.m_bUp = this.m_errors[param1Int2];
          this.m_iUp = param1Int2;
        }  
      if (this.m_iLow == -1 || this.m_iUp == -1)
        throw new Exception("This should never happen!"); 
      return true;
    }
    
    protected void checkClassifier() throws Exception {
      double d = 0.0D;
      byte b;
      for (b = 0; b < this.m_alpha.length; b++) {
        if (this.m_alpha[b] > 0.0D)
          d += this.m_class[b] * this.m_alpha[b]; 
      } 
      System.err.println("Sum of y(i) * alpha(i): " + d);
      for (b = 0; b < this.m_alpha.length; b++) {
        double d1 = SVMOutput(b, this.m_data.instance(b));
        if (Utils.eq(this.m_alpha[b], 0.0D) && Utils.sm(this.m_class[b] * d1, 1.0D))
          System.err.println("KKT condition 1 violated: " + (this.m_class[b] * d1)); 
        if (Utils.gr(this.m_alpha[b], 0.0D) && Utils.sm(this.m_alpha[b], this.this$0.m_C * this.m_data.instance(b).weight()) && !Utils.eq(this.m_class[b] * d1, 1.0D))
          System.err.println("KKT condition 2 violated: " + (this.m_class[b] * d1)); 
        if (Utils.eq(this.m_alpha[b], this.this$0.m_C * this.m_data.instance(b).weight()) && Utils.gr(this.m_class[b] * d1, 1.0D))
          System.err.println("KKT condition 3 violated: " + (this.m_class[b] * d1)); 
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\SMO.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */