package weka.classifiers.functions;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Matrix;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class LinearRegression extends Classifier implements OptionHandler, WeightedInstancesHandler {
  private double[] m_Coefficients;
  
  private boolean[] m_SelectedAttributes;
  
  private Instances m_TransformedData;
  
  private ReplaceMissingValues m_MissingFilter;
  
  private NominalToBinary m_TransformFilter;
  
  private double m_ClassStdDev;
  
  private double m_ClassMean;
  
  private int m_ClassIndex;
  
  private double[] m_Means;
  
  private double[] m_StdDevs;
  
  private boolean b_Debug;
  
  private int m_AttributeSelection;
  
  public static final int SELECTION_M5 = 0;
  
  public static final int SELECTION_NONE = 1;
  
  public static final int SELECTION_GREEDY = 2;
  
  public static final Tag[] TAGS_SELECTION = new Tag[] { new Tag(1, "No attribute selection"), new Tag(0, "M5 method"), new Tag(2, "Greedy method") };
  
  private boolean m_EliminateColinearAttributes = true;
  
  private boolean m_checksTurnedOff = false;
  
  private double m_Ridge = 1.0E-8D;
  
  public void turnChecksOff() {
    this.m_checksTurnedOff = true;
  }
  
  public void turnChecksOn() {
    this.m_checksTurnedOff = false;
  }
  
  public String globalInfo() {
    return "Class for using linear regression for prediction. Uses the Akaike criterion for model selection, and is able to deal with weighted instances.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (!this.m_checksTurnedOff) {
      if (!paramInstances.classAttribute().isNumeric())
        throw new UnsupportedClassTypeException("Class attribute has to be numeric for regression!"); 
      if (paramInstances.numInstances() == 0)
        throw new Exception("No instances in training file!"); 
      if (paramInstances.checkForStringAttributes())
        throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    } 
    if (!this.m_checksTurnedOff) {
      this.m_TransformFilter = new NominalToBinary();
      this.m_TransformFilter.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_TransformFilter);
      this.m_MissingFilter = new ReplaceMissingValues();
      this.m_MissingFilter.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_MissingFilter);
      paramInstances.deleteWithMissingClass();
    } else {
      this.m_TransformFilter = null;
      this.m_MissingFilter = null;
    } 
    this.m_ClassIndex = paramInstances.classIndex();
    this.m_TransformedData = paramInstances;
    this.m_SelectedAttributes = new boolean[paramInstances.numAttributes()];
    byte b;
    for (b = 0; b < paramInstances.numAttributes(); b++) {
      if (b != this.m_ClassIndex)
        this.m_SelectedAttributes[b] = true; 
    } 
    this.m_Coefficients = null;
    this.m_Means = new double[paramInstances.numAttributes()];
    this.m_StdDevs = new double[paramInstances.numAttributes()];
    for (b = 0; b < paramInstances.numAttributes(); b++) {
      if (b != paramInstances.classIndex()) {
        this.m_Means[b] = paramInstances.meanOrMode(b);
        this.m_StdDevs[b] = Math.sqrt(paramInstances.variance(b));
        if (this.m_StdDevs[b] == 0.0D)
          this.m_SelectedAttributes[b] = false; 
      } 
    } 
    this.m_ClassStdDev = Math.sqrt(paramInstances.variance(this.m_TransformedData.classIndex()));
    this.m_ClassMean = paramInstances.meanOrMode(this.m_TransformedData.classIndex());
    findBestModel();
    this.m_TransformedData = new Instances(paramInstances, 0);
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    Instance instance = paramInstance;
    if (!this.m_checksTurnedOff) {
      this.m_TransformFilter.input(instance);
      this.m_TransformFilter.batchFinished();
      instance = this.m_TransformFilter.output();
      this.m_MissingFilter.input(instance);
      this.m_MissingFilter.batchFinished();
      instance = this.m_MissingFilter.output();
    } 
    return regressionPrediction(instance, this.m_SelectedAttributes, this.m_Coefficients);
  }
  
  public String toString() {
    if (this.m_TransformedData == null)
      return "Linear Regression: No model built yet."; 
    try {
      StringBuffer stringBuffer = new StringBuffer();
      byte b1 = 0;
      boolean bool = true;
      stringBuffer.append("\nLinear Regression Model\n\n");
      stringBuffer.append(this.m_TransformedData.classAttribute().name() + " =\n\n");
      for (byte b2 = 0; b2 < this.m_TransformedData.numAttributes(); b2++) {
        if (b2 != this.m_ClassIndex && this.m_SelectedAttributes[b2]) {
          if (!bool) {
            stringBuffer.append(" +\n");
          } else {
            bool = false;
          } 
          stringBuffer.append(Utils.doubleToString(this.m_Coefficients[b1], 12, 4) + " * ");
          stringBuffer.append(this.m_TransformedData.attribute(b2).name());
          b1++;
        } 
      } 
      stringBuffer.append(" +\n" + Utils.doubleToString(this.m_Coefficients[b1], 12, 4));
      return stringBuffer.toString();
    } catch (Exception exception) {
      return "Can't print Linear Regression!";
    } 
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tProduce debugging output.\n\t(default no debugging output)", "D", 0, "-D"));
    vector.addElement(new Option("\tSet the attribute selection method to use. 1 = None, 2 = Greedy.\n\t(default 0 = M5' method)", "S", 1, "-S <number of selection method>"));
    vector.addElement(new Option("\tDo not try to eliminate colinear attributes.\n", "C", 0, "-C"));
    vector.addElement(new Option("\tSet ridge parameter (default 1.0e-8).\n", "R", 1, "-R <double>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('S', paramArrayOfString);
    if (str1.length() != 0) {
      setAttributeSelectionMethod(new SelectedTag(Integer.parseInt(str1), TAGS_SELECTION));
    } else {
      setAttributeSelectionMethod(new SelectedTag(0, TAGS_SELECTION));
    } 
    String str2 = Utils.getOption('R', paramArrayOfString);
    if (str2.length() != 0) {
      setRidge((new Double(str2)).doubleValue());
    } else {
      setRidge(1.0E-8D);
    } 
    setDebug(Utils.getFlag('D', paramArrayOfString));
    setEliminateColinearAttributes(!Utils.getFlag('C', paramArrayOfString));
  }
  
  public double[] coefficients() {
    double[] arrayOfDouble = new double[this.m_SelectedAttributes.length + 1];
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_SelectedAttributes.length; b2++) {
      if (this.m_SelectedAttributes[b2] && b2 != this.m_ClassIndex)
        arrayOfDouble[b2] = this.m_Coefficients[b1++]; 
    } 
    arrayOfDouble[this.m_SelectedAttributes.length] = this.m_Coefficients[b1];
    return arrayOfDouble;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[6];
    byte b = 0;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getAttributeSelectionMethod().getSelectedTag().getID();
    if (getDebug())
      arrayOfString[b++] = "-D"; 
    if (!getEliminateColinearAttributes())
      arrayOfString[b++] = "-C"; 
    arrayOfString[b++] = "-R";
    arrayOfString[b++] = "" + getRidge();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String ridgeTipText() {
    return "The value of the Ridge parameter.";
  }
  
  public double getRidge() {
    return this.m_Ridge;
  }
  
  public void setRidge(double paramDouble) {
    this.m_Ridge = paramDouble;
  }
  
  public String eliminateColinearAttributesTipText() {
    return "Eliminate colinear attributes.";
  }
  
  public boolean getEliminateColinearAttributes() {
    return this.m_EliminateColinearAttributes;
  }
  
  public void setEliminateColinearAttributes(boolean paramBoolean) {
    this.m_EliminateColinearAttributes = paramBoolean;
  }
  
  public int numParameters() {
    return this.m_Coefficients.length - 1;
  }
  
  public String attributeSelectionMethodTipText() {
    return "Set the method used to select attributes for use in the linear regression. Available methods are: no attribute selection, attribute selection using M5's method (step through the attributes removing the one with the smallest standardised coefficient until no improvement is observed in the estimate of the error given by the Akaike information criterion), and a greedy selection using the Akaike information metric.";
  }
  
  public void setAttributeSelectionMethod(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_SELECTION)
      this.m_AttributeSelection = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public SelectedTag getAttributeSelectionMethod() {
    return new SelectedTag(this.m_AttributeSelection, TAGS_SELECTION);
  }
  
  public String debugTipText() {
    return "Outputs debug information to the console.";
  }
  
  public void setDebug(boolean paramBoolean) {
    this.b_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.b_Debug;
  }
  
  private boolean deselectColinearAttributes(boolean[] paramArrayOfboolean, double[] paramArrayOfdouble) {
    double d = 1.5D;
    byte b = -1;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfboolean.length; b2++) {
      if (paramArrayOfboolean[b2]) {
        double d1 = Math.abs(paramArrayOfdouble[b1] * this.m_StdDevs[b2] / this.m_ClassStdDev);
        if (d1 > d) {
          d = d1;
          b = b2;
        } 
        b1++;
      } 
    } 
    if (b >= 0) {
      paramArrayOfboolean[b] = false;
      if (this.b_Debug)
        System.out.println("Deselected colinear attribute:" + (b + 1) + " with standardised coefficient: " + d); 
      return true;
    } 
    return false;
  }
  
  private void findBestModel() throws Exception {
    boolean bool;
    int i = this.m_TransformedData.numInstances();
    if (this.b_Debug)
      System.out.println((new Instances(this.m_TransformedData, 0)).toString()); 
    do {
      this.m_Coefficients = doRegression(this.m_SelectedAttributes);
    } while (this.m_EliminateColinearAttributes && deselectColinearAttributes(this.m_SelectedAttributes, this.m_Coefficients));
    byte b1 = 1;
    for (byte b2 = 0; b2 < this.m_SelectedAttributes.length; b2++) {
      if (this.m_SelectedAttributes[b2])
        b1++; 
    } 
    double d1 = calculateSE(this.m_SelectedAttributes, this.m_Coefficients);
    double d2 = (i - b1 + 2 * b1);
    if (this.b_Debug)
      System.out.println("Initial Akaike value: " + d2); 
    byte b3 = b1;
    switch (this.m_AttributeSelection) {
      case 2:
        do {
          boolean[] arrayOfBoolean = (boolean[])this.m_SelectedAttributes.clone();
          bool = false;
          b3--;
          for (byte b = 0; b < this.m_SelectedAttributes.length; b++) {
            if (arrayOfBoolean[b]) {
              arrayOfBoolean[b] = false;
              double[] arrayOfDouble = doRegression(arrayOfBoolean);
              double d3 = calculateSE(arrayOfBoolean, arrayOfDouble);
              double d4 = d3 / d1 * (i - b1) + (2 * b3);
              if (this.b_Debug)
                System.out.println("(akaike: " + d4); 
              if (d4 < d2) {
                if (this.b_Debug)
                  System.err.println("Removing attribute " + (b + 1) + " improved Akaike: " + d4); 
                bool = true;
                d2 = d4;
                System.arraycopy(arrayOfBoolean, 0, this.m_SelectedAttributes, 0, this.m_SelectedAttributes.length);
                this.m_Coefficients = arrayOfDouble;
              } 
              arrayOfBoolean[b] = true;
            } 
          } 
        } while (bool);
        break;
      case 0:
        do {
          bool = false;
          b3--;
          double d3 = 0.0D;
          byte b = -1;
          byte b4 = 0;
          for (byte b5 = 0; b5 < this.m_SelectedAttributes.length; b5++) {
            if (this.m_SelectedAttributes[b5]) {
              double d = Math.abs(this.m_Coefficients[b4] * this.m_StdDevs[b5] / this.m_ClassStdDev);
              if (!b4 || d < d3) {
                d3 = d;
                b = b5;
              } 
              b4++;
            } 
          } 
          if (b < 0)
            continue; 
          this.m_SelectedAttributes[b] = false;
          double[] arrayOfDouble = doRegression(this.m_SelectedAttributes);
          double d4 = calculateSE(this.m_SelectedAttributes, arrayOfDouble);
          double d5 = d4 / d1 * (i - b1) + (2 * b3);
          if (this.b_Debug)
            System.out.println("(akaike: " + d5); 
          if (d5 < d2) {
            if (this.b_Debug)
              System.err.println("Removing attribute " + (b + 1) + " improved Akaike: " + d5); 
            bool = true;
            d2 = d5;
            this.m_Coefficients = arrayOfDouble;
          } else {
            this.m_SelectedAttributes[b] = true;
          } 
        } while (bool);
        break;
    } 
  }
  
  private double calculateSE(boolean[] paramArrayOfboolean, double[] paramArrayOfdouble) throws Exception {
    double d = 0.0D;
    for (byte b = 0; b < this.m_TransformedData.numInstances(); b++) {
      double d1 = regressionPrediction(this.m_TransformedData.instance(b), paramArrayOfboolean, paramArrayOfdouble);
      double d2 = d1 - this.m_TransformedData.instance(b).classValue();
      d += d2 * d2;
    } 
    return d;
  }
  
  private double regressionPrediction(Instance paramInstance, boolean[] paramArrayOfboolean, double[] paramArrayOfdouble) throws Exception {
    double d = 0.0D;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInstance.numAttributes(); b2++) {
      if (this.m_ClassIndex != b2 && paramArrayOfboolean[b2]) {
        d += paramArrayOfdouble[b1] * paramInstance.value(b2);
        b1++;
      } 
    } 
    d += paramArrayOfdouble[b1];
    return d;
  }
  
  private double[] doRegression(boolean[] paramArrayOfboolean) throws Exception {
    if (this.b_Debug) {
      System.out.print("doRegression(");
      for (byte b = 0; b < paramArrayOfboolean.length; b++)
        System.out.print(" " + paramArrayOfboolean[b]); 
      System.out.println(" )");
    } 
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfboolean.length; b2++) {
      if (paramArrayOfboolean[b2])
        b1++; 
    } 
    Matrix matrix1 = null;
    Matrix matrix2 = null;
    double[] arrayOfDouble1 = null;
    if (b1 > 0) {
      matrix1 = new Matrix(this.m_TransformedData.numInstances(), b1);
      matrix2 = new Matrix(this.m_TransformedData.numInstances(), 1);
      byte b;
      for (b = 0; b < this.m_TransformedData.numInstances(); b++) {
        Instance instance = this.m_TransformedData.instance(b);
        byte b5 = 0;
        for (byte b6 = 0; b6 < this.m_TransformedData.numAttributes(); b6++) {
          if (b6 == this.m_ClassIndex) {
            matrix2.setElement(b, 0, instance.classValue());
          } else if (paramArrayOfboolean[b6]) {
            double d = instance.value(b6) - this.m_Means[b6];
            if (!this.m_checksTurnedOff)
              d /= this.m_StdDevs[b6]; 
            matrix1.setElement(b, b5, d);
            b5++;
          } 
        } 
      } 
      arrayOfDouble1 = new double[this.m_TransformedData.numInstances()];
      for (b = 0; b < arrayOfDouble1.length; b++)
        arrayOfDouble1[b] = this.m_TransformedData.instance(b).weight(); 
    } 
    double[] arrayOfDouble2 = new double[b1 + 1];
    if (b1 > 0) {
      double[] arrayOfDouble = matrix1.regression(matrix2, arrayOfDouble1, this.m_Ridge);
      System.arraycopy(arrayOfDouble, 0, arrayOfDouble2, 0, b1);
    } 
    arrayOfDouble2[b1] = this.m_ClassMean;
    byte b3 = 0;
    for (byte b4 = 0; b4 < this.m_TransformedData.numAttributes(); b4++) {
      if (b4 != this.m_TransformedData.classIndex() && paramArrayOfboolean[b4]) {
        if (!this.m_checksTurnedOff)
          arrayOfDouble2[b3] = arrayOfDouble2[b3] / this.m_StdDevs[b4]; 
        arrayOfDouble2[arrayOfDouble2.length - 1] = arrayOfDouble2[arrayOfDouble2.length - 1] - arrayOfDouble2[b3] * this.m_Means[b4];
        b3++;
      } 
    } 
    return arrayOfDouble2;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new LinearRegression(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\LinearRegression.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */