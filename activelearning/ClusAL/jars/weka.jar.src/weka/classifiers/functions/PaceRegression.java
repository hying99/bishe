package weka.classifiers.functions;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.pace.ChisqMixture;
import weka.classifiers.functions.pace.DoubleVector;
import weka.classifiers.functions.pace.IntVector;
import weka.classifiers.functions.pace.NormalMixture;
import weka.classifiers.functions.pace.PaceMatrix;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.UnassignedClassException;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.core.WekaException;

public class PaceRegression extends Classifier implements OptionHandler, WeightedInstancesHandler {
  Instances m_Model = null;
  
  private double[] m_Coefficients;
  
  private int m_ClassIndex;
  
  private boolean m_Debug;
  
  private static final int olsEstimator = 0;
  
  private static final int ebEstimator = 1;
  
  private static final int nestedEstimator = 2;
  
  private static final int subsetEstimator = 3;
  
  private static final int pace2Estimator = 4;
  
  private static final int pace4Estimator = 5;
  
  private static final int pace6Estimator = 6;
  
  private static final int olscEstimator = 7;
  
  private static final int aicEstimator = 8;
  
  private static final int bicEstimator = 9;
  
  private static final int ricEstimator = 10;
  
  public static final Tag[] TAGS_ESTIMATOR = new Tag[] { 
      new Tag(0, "Ordinary least squares"), new Tag(1, "Empirical Bayes"), new Tag(2, "Nested model selector"), new Tag(3, "Subset selector"), new Tag(4, "PACE2"), new Tag(5, "PACE4"), new Tag(6, "PACE6"), new Tag(7, "Ordinary least squares selection"), new Tag(8, "AIC"), new Tag(9, "BIC"), 
      new Tag(10, "RIC") };
  
  private int paceEstimator = 1;
  
  private double olscThreshold = 2.0D;
  
  public String globalInfo() {
    return "Class for building pace regression linear models and using them for prediction. \n\nUnder regularity conditions, pace regression is provably optimal when the number of coefficients tends to infinity. It consists of a group of estimators that are either overall optimal or optimal under certain conditions.\n\nThe current work of the pace regression theory, and therefore also this implementation, do not handle: \n\n- missing values \n- non-binary nominal attributes \n- the case that n - k is small where n is the number of instances and k is the number of coefficients (the threshold used in this implmentation is 20)\n\nFor more information see:\n\nWang, Y. (2000). A new approach to fitting linear models in high dimensional spaces. PhD Thesis. Department of Computer Science, University of Waikato, New Zealand. \n\nWang, Y. and Witten, I. H. (2002). Modeling for optimal probability prediction. Proceedings of ICML'2002. Sydney.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    try {
      if (!paramInstances.classAttribute().isNumeric())
        throw new UnsupportedClassTypeException("Class attribute has to be numeric for pace regression!"); 
    } catch (UnassignedClassException unassignedClassException) {
      System.err.println(paramInstances);
      System.err.println(paramInstances.classIndex());
    } 
    if (paramInstances.numInstances() == 0)
      throw new Exception("No instances in training file!"); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    if (checkForNonBinary(paramInstances))
      throw new UnsupportedAttributeTypeException("Can only deal with numeric and binary attributes!"); 
    if (checkForMissing(paramInstances))
      throw new NoSupportForMissingValuesException("Can't handle missing values!"); 
    if (paramInstances.numInstances() - paramInstances.numAttributes() < 20)
      throw new IllegalArgumentException("Not enough instances. Ratio of number of instances (n) to number of attributes (k) is too small (n - k < 20)."); 
    this.m_Model = new Instances(paramInstances, 0);
    this.m_ClassIndex = paramInstances.classIndex();
    double[][] arrayOfDouble = getTransformedDataMatrix(paramInstances, this.m_ClassIndex);
    double[] arrayOfDouble1 = paramInstances.attributeToDoubleArray(this.m_ClassIndex);
    this.m_Coefficients = null;
    this.m_Coefficients = pace(arrayOfDouble, arrayOfDouble1);
  }
  
  private double[] pace(double[][] paramArrayOfdouble, double[] paramArrayOfdouble1) {
    NormalMixture normalMixture;
    ChisqMixture chisqMixture;
    DoubleVector doubleVector6;
    byte b;
    PaceMatrix paceMatrix1 = new PaceMatrix(paramArrayOfdouble);
    PaceMatrix paceMatrix2 = new PaceMatrix(paramArrayOfdouble1, paramArrayOfdouble1.length);
    IntVector intVector = IntVector.seq(0, paceMatrix1.getColumnDimension() - 1);
    int i = paceMatrix1.getRowDimension();
    int j = paceMatrix1.getColumnDimension();
    paceMatrix1.lsqrSelection(paceMatrix2, intVector, 1);
    paceMatrix1.positiveDiagonal(paceMatrix2, intVector);
    int k = intVector.size();
    PaceMatrix paceMatrix3 = (PaceMatrix)paceMatrix2.clone();
    paceMatrix1.rsolve(paceMatrix3, intVector, intVector.size());
    DoubleVector doubleVector1 = paceMatrix3.getColumn(0).unpivoting(intVector, j);
    DoubleVector doubleVector2 = paceMatrix2.getColumn(intVector.size(), i - 1, 0);
    double d = Math.sqrt(doubleVector2.sum2() / doubleVector2.size());
    DoubleVector doubleVector3 = paceMatrix2.getColumn(0, intVector.size() - 1, 0).times(1.0D / d);
    DoubleVector doubleVector4 = null;
    switch (this.paceEstimator) {
      case 1:
      case 2:
      case 3:
        normalMixture = new NormalMixture();
        normalMixture.fit(doubleVector3, 1);
        if (this.paceEstimator == 1) {
          doubleVector4 = normalMixture.empiricalBayesEstimate(doubleVector3);
          break;
        } 
        if (this.paceEstimator == 1) {
          doubleVector4 = normalMixture.subsetEstimate(doubleVector3);
          break;
        } 
        doubleVector4 = normalMixture.nestedEstimate(doubleVector3);
        break;
      case 4:
      case 5:
      case 6:
        doubleVector5 = doubleVector3.square();
        chisqMixture = new ChisqMixture();
        chisqMixture.fit(doubleVector5, 1);
        if (this.paceEstimator == 6) {
          doubleVector6 = chisqMixture.pace6(doubleVector5);
        } else if (this.paceEstimator == 4) {
          doubleVector6 = chisqMixture.pace2(doubleVector5);
        } else {
          doubleVector6 = chisqMixture.pace4(doubleVector5);
        } 
        doubleVector4 = doubleVector6.sqrt().times(doubleVector3.sign());
        break;
      case 0:
        doubleVector4 = doubleVector3.copy();
        break;
      case 7:
      case 8:
      case 9:
      case 10:
        if (this.paceEstimator == 8) {
          this.olscThreshold = 2.0D;
        } else if (this.paceEstimator == 9) {
          this.olscThreshold = Math.log(i);
        } else if (this.paceEstimator == 10) {
          this.olscThreshold = 2.0D * Math.log(j);
        } 
        doubleVector4 = doubleVector3.copy();
        for (b = 0; b < doubleVector4.size(); b++) {
          if (Math.abs(doubleVector4.get(b)) < Math.sqrt(this.olscThreshold))
            doubleVector4.set(b, 0.0D); 
        } 
        break;
    } 
    PaceMatrix paceMatrix4 = new PaceMatrix((new PaceMatrix(doubleVector4)).times(d));
    paceMatrix1.rsolve(paceMatrix4, intVector, intVector.size());
    DoubleVector doubleVector5 = paceMatrix4.getColumn(0).unpivoting(intVector, j);
    return doubleVector5.getArrayCopy();
  }
  
  public boolean checkForMissing(Instances paramInstances) {
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = paramInstances.instance(b);
      for (byte b1 = 0; b1 < paramInstances.numAttributes(); b1++) {
        if (instance.isMissing(b1))
          return true; 
      } 
    } 
    return false;
  }
  
  public boolean checkForMissing(Instance paramInstance, Instances paramInstances) {
    for (byte b = 0; b < paramInstance.numAttributes(); b++) {
      if (b != paramInstances.classIndex() && paramInstance.isMissing(b))
        return true; 
    } 
    return false;
  }
  
  public boolean checkForNonBinary(Instances paramInstances) {
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      if (paramInstances.attribute(b).isNominal() && paramInstances.attribute(b).numValues() != 2)
        return true; 
    } 
    return false;
  }
  
  private double[][] getTransformedDataMatrix(Instances paramInstances, int paramInt) {
    int i = paramInstances.numInstances();
    int j = paramInstances.numAttributes();
    int k = paramInt;
    if (k < 0)
      k = j; 
    double[][] arrayOfDouble = new double[i][j];
    for (byte b = 0; b < i; b++) {
      Instance instance = paramInstances.instance(b);
      arrayOfDouble[b][0] = 1.0D;
      int m;
      for (m = 0; m < k; m++)
        arrayOfDouble[b][m + 1] = instance.value(m); 
      for (m = k + 1; m < j; m++)
        arrayOfDouble[b][m] = instance.value(m); 
    } 
    return arrayOfDouble;
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    if (this.m_Coefficients == null)
      throw new Exception("Pace Regression: No model built yet."); 
    if (checkForMissing(paramInstance, this.m_Model))
      throw new NoSupportForMissingValuesException("Can't handle missing values!"); 
    return regressionPrediction(paramInstance, this.m_Coefficients);
  }
  
  public String toString() {
    if (this.m_Coefficients == null)
      return "Pace Regression: No model built yet."; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\nPace Regression Model\n\n");
    stringBuffer.append(this.m_Model.classAttribute().name() + " =\n\n");
    byte b1 = 0;
    stringBuffer.append(Utils.doubleToString(this.m_Coefficients[0], 12, 4));
    for (byte b2 = 1; b2 < this.m_Coefficients.length; b2++) {
      if (b1 == this.m_ClassIndex)
        b1++; 
      if (this.m_Coefficients[b2] != 0.0D) {
        stringBuffer.append(" +\n");
        stringBuffer.append(Utils.doubleToString(this.m_Coefficients[b2], 12, 4) + " * ");
        stringBuffer.append(this.m_Model.attribute(b1).name());
      } 
      b1++;
    } 
    return stringBuffer.toString();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tProduce debugging output.\n\t(default no debugging output)", "D", 0, "-D"));
    vector.addElement(new Option("\tThe estimator can be one of the following:\n\t\teb\tEmpirical Bayes(default)\n\t\tnested\tOptimal nested model\n\t\tsubset\tOptimal subset\n\t\tpace2\tPACE2\n\t\tpace4\tPACE4\n\t\tpace6\tPACE6\n\n\t\tols\tOrdinary least squares\n\t\taic\tAIC\n\t\tbic\tBIC\n\t\tric\tRIC\n\t\tolsc\tOLSC", "E", 0, "-E <estimator>"));
    vector.addElement(new Option("\tThreshold value for the OLSC estimator", "S", 0, "-S <threshold value>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setDebug(Utils.getFlag('D', paramArrayOfString));
    String str1 = Utils.getOption('E', paramArrayOfString);
    if (str1.equals("ols")) {
      this.paceEstimator = 0;
    } else if (str1.equals("olsc")) {
      this.paceEstimator = 7;
    } else if (str1.equals("eb") || str1.equals("")) {
      this.paceEstimator = 1;
    } else if (str1.equals("nested")) {
      this.paceEstimator = 2;
    } else if (str1.equals("subset")) {
      this.paceEstimator = 3;
    } else if (str1.equals("pace2")) {
      this.paceEstimator = 4;
    } else if (str1.equals("pace4")) {
      this.paceEstimator = 5;
    } else if (str1.equals("pace6")) {
      this.paceEstimator = 6;
    } else if (str1.equals("aic")) {
      this.paceEstimator = 8;
    } else if (str1.equals("bic")) {
      this.paceEstimator = 9;
    } else if (str1.equals("ric")) {
      this.paceEstimator = 10;
    } else {
      throw new WekaException("unknown estimator " + str1 + " for -E option");
    } 
    String str2 = Utils.getOption('S', paramArrayOfString);
    if (!str2.equals(""))
      this.olscThreshold = Double.parseDouble(str2); 
  }
  
  public double[] coefficients() {
    double[] arrayOfDouble = new double[this.m_Coefficients.length];
    for (byte b = 0; b < arrayOfDouble.length; b++)
      arrayOfDouble[b] = this.m_Coefficients[b]; 
    return arrayOfDouble;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[6];
    byte b = 0;
    if (getDebug())
      arrayOfString[b++] = "-D"; 
    arrayOfString[b++] = "-E";
    switch (this.paceEstimator) {
      case 0:
        arrayOfString[b++] = "ols";
        break;
      case 7:
        arrayOfString[b++] = "olsc";
        arrayOfString[b++] = "-S";
        arrayOfString[b++] = "" + this.olscThreshold;
        break;
      case 1:
        arrayOfString[b++] = "eb";
        break;
      case 2:
        arrayOfString[b++] = "nested";
        break;
      case 3:
        arrayOfString[b++] = "subset";
        break;
      case 4:
        arrayOfString[b++] = "pace2";
        break;
      case 5:
        arrayOfString[b++] = "pace4";
        break;
      case 6:
        arrayOfString[b++] = "pace6";
        break;
      case 8:
        arrayOfString[b++] = "aic";
        break;
      case 9:
        arrayOfString[b++] = "bic";
        break;
      case 10:
        arrayOfString[b++] = "ric";
        break;
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public int numParameters() {
    return this.m_Coefficients.length - 1;
  }
  
  public String debugTipText() {
    return "Output debug information to the console.";
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public String estimatorTipText() {
    return "The estimator to use.\n\neb -- Empirical Bayes estimator for noraml mixture (default)\nnested -- Optimal nested model selector for normal mixture\nsubset -- Optimal subset selector for normal mixture\npace2 -- PACE2 for Chi-square mixture\npace4 -- PACE4 for Chi-square mixture\npace6 -- PACE6 for Chi-square mixture\nols -- Ordinary least squares estimator\naic -- AIC estimator\nbic -- BIC estimator\nric -- RIC estimator\nolsc -- Ordinary least squares subset selector with a threshold";
  }
  
  public SelectedTag getEstimator() {
    return new SelectedTag(this.paceEstimator, TAGS_ESTIMATOR);
  }
  
  public void setEstimator(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_ESTIMATOR)
      this.paceEstimator = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public String thresholdTipText() {
    return "Threshold for the olsc estimator.";
  }
  
  public void setThreshold(double paramDouble) {
    this.olscThreshold = paramDouble;
  }
  
  public double getThreshold() {
    return this.olscThreshold;
  }
  
  private double regressionPrediction(Instance paramInstance, double[] paramArrayOfdouble) throws Exception {
    byte b1 = 0;
    double d = paramArrayOfdouble[b1];
    for (byte b2 = 0; b2 < paramInstance.numAttributes(); b2++) {
      if (this.m_ClassIndex != b2)
        d += paramArrayOfdouble[++b1] * paramInstance.value(b2); 
    } 
    return d;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      PaceRegression paceRegression = new PaceRegression();
      System.out.println(Evaluation.evaluateModel(paceRegression, paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\PaceRegression.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */