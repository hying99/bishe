package weka.classifiers.functions;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class Winnow extends Classifier implements UpdateableClassifier {
  protected boolean m_Balanced;
  
  protected int m_numIterations = 1;
  
  protected double m_Alpha = 2.0D;
  
  protected double m_Beta = 0.5D;
  
  protected double m_Threshold = -1.0D;
  
  protected int m_Seed = 1;
  
  protected int m_Mistakes;
  
  protected double m_defaultWeight = 2.0D;
  
  private double[] m_predPosVector = null;
  
  private double[] m_predNegVector = null;
  
  private double m_actualThreshold;
  
  private Instances m_Train = null;
  
  private NominalToBinary m_NominalToBinary;
  
  private ReplaceMissingValues m_ReplaceMissingValues;
  
  public String globalInfo() {
    return "Implements Winnow and Balanced Winnow algorithms by Littlestone. For more information, see\n\nN. Littlestone (1988). \"Learning quickly when irrelevant attributes are abound: A new linear threshold algorithm\". Machine Learning 2, pp. 285-318.\n\nand\n\nN. Littlestone (1989). \"Mistake bounds and logarithmic  linear-threshold learning algorithms\". Technical report UCSC-CRL-89-11, University of California, Santa Cruz.\n\nDoes classification for problems with nominal attributes (which it converts into binary attributes).";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(7);
    vector.addElement(new Option("\tUse the baLanced version\n\t(default false)", "L", 0, "-L"));
    vector.addElement(new Option("\tThe number of iterations to be performed.\n\t(default 1)", "I", 1, "-I <int>"));
    vector.addElement(new Option("\tPromotion coefficient alpha.\n\t(default 2.0)", "A", 1, "-A <double>"));
    vector.addElement(new Option("\tDemotion coefficient beta.\n\t(default 0.5)", "B", 1, "-B <double>"));
    vector.addElement(new Option("\tPrediction threshold.\n\t(default -1.0 == number of attributes)", "H", 1, "-H <double>"));
    vector.addElement(new Option("\tStarting weights.\n\t(default 2.0)", "W", 1, "-W <double>"));
    vector.addElement(new Option("\tDefault random seed.\n\t(default 1)", "S", 1, "-S <int>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    this.m_Balanced = Utils.getFlag('L', paramArrayOfString);
    String str1 = Utils.getOption('I', paramArrayOfString);
    if (str1.length() != 0)
      this.m_numIterations = Integer.parseInt(str1); 
    String str2 = Utils.getOption('A', paramArrayOfString);
    if (str2.length() != 0)
      this.m_Alpha = (new Double(str2)).doubleValue(); 
    String str3 = Utils.getOption('B', paramArrayOfString);
    if (str3.length() != 0)
      this.m_Beta = (new Double(str3)).doubleValue(); 
    String str4 = Utils.getOption('H', paramArrayOfString);
    if (str4.length() != 0)
      this.m_Threshold = (new Double(str4)).doubleValue(); 
    String str5 = Utils.getOption('W', paramArrayOfString);
    if (str5.length() != 0)
      this.m_defaultWeight = (new Double(str5)).doubleValue(); 
    String str6 = Utils.getOption('S', paramArrayOfString);
    if (str6.length() != 0)
      this.m_Seed = Integer.parseInt(str6); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[20];
    byte b = 0;
    if (this.m_Balanced)
      arrayOfString[b++] = "-L"; 
    arrayOfString[b++] = "-I";
    arrayOfString[b++] = "" + this.m_numIterations;
    arrayOfString[b++] = "-A";
    arrayOfString[b++] = "" + this.m_Alpha;
    arrayOfString[b++] = "-B";
    arrayOfString[b++] = "" + this.m_Beta;
    arrayOfString[b++] = "-H";
    arrayOfString[b++] = "" + this.m_Threshold;
    arrayOfString[b++] = "-W";
    arrayOfString[b++] = "" + this.m_defaultWeight;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + this.m_Seed;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    if (paramInstances.numClasses() > 2)
      throw new Exception("Can only handle two-class datasets!"); 
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Can't handle a numeric class!"); 
    Enumeration enumeration = paramInstances.enumerateAttributes();
    while (enumeration.hasMoreElements()) {
      Attribute attribute = enumeration.nextElement();
      if (!attribute.isNominal())
        throw new UnsupportedAttributeTypeException("Winnow: only nominal attributes, please."); 
    } 
    this.m_Train = new Instances(paramInstances);
    this.m_Train.deleteWithMissingClass();
    this.m_ReplaceMissingValues = new ReplaceMissingValues();
    this.m_ReplaceMissingValues.setInputFormat(this.m_Train);
    this.m_Train = Filter.useFilter(this.m_Train, (Filter)this.m_ReplaceMissingValues);
    this.m_NominalToBinary = new NominalToBinary();
    this.m_NominalToBinary.setInputFormat(this.m_Train);
    this.m_Train = Filter.useFilter(this.m_Train, (Filter)this.m_NominalToBinary);
    if (this.m_Seed != -1)
      this.m_Train.randomize(new Random(this.m_Seed)); 
    this.m_predPosVector = new double[this.m_Train.numAttributes()];
    if (this.m_Balanced)
      this.m_predNegVector = new double[this.m_Train.numAttributes()]; 
    byte b;
    for (b = 0; b < this.m_Train.numAttributes(); b++)
      this.m_predPosVector[b] = this.m_defaultWeight; 
    if (this.m_Balanced)
      for (b = 0; b < this.m_Train.numAttributes(); b++)
        this.m_predNegVector[b] = this.m_defaultWeight;  
    if (this.m_Threshold < 0.0D) {
      this.m_actualThreshold = this.m_Train.numAttributes() - 1.0D;
    } else {
      this.m_actualThreshold = this.m_Threshold;
    } 
    this.m_Mistakes = 0;
    if (this.m_Balanced) {
      for (b = 0; b < this.m_numIterations; b++) {
        for (byte b1 = 0; b1 < this.m_Train.numInstances(); b1++)
          actualUpdateClassifierBalanced(this.m_Train.instance(b1)); 
      } 
    } else {
      for (b = 0; b < this.m_numIterations; b++) {
        for (byte b1 = 0; b1 < this.m_Train.numInstances(); b1++)
          actualUpdateClassifier(this.m_Train.instance(b1)); 
      } 
    } 
  }
  
  public void updateClassifier(Instance paramInstance) throws Exception {
    this.m_ReplaceMissingValues.input(paramInstance);
    this.m_ReplaceMissingValues.batchFinished();
    Instance instance = this.m_ReplaceMissingValues.output();
    this.m_NominalToBinary.input(instance);
    this.m_NominalToBinary.batchFinished();
    instance = this.m_NominalToBinary.output();
    if (this.m_Balanced) {
      actualUpdateClassifierBalanced(instance);
    } else {
      actualUpdateClassifier(instance);
    } 
  }
  
  private void actualUpdateClassifier(Instance paramInstance) throws Exception {
    if (!paramInstance.classIsMissing()) {
      double d = makePrediction(paramInstance);
      if (d != paramInstance.classValue()) {
        double d1;
        this.m_Mistakes++;
        if (d == 0.0D) {
          d1 = this.m_Alpha;
        } else {
          d1 = this.m_Beta;
        } 
        int i = paramInstance.numValues();
        int j = this.m_Train.classIndex();
        for (byte b = 0; b < i; b++) {
          if (paramInstance.index(b) != j && paramInstance.valueSparse(b) == 1.0D)
            this.m_predPosVector[paramInstance.index(b)] = this.m_predPosVector[paramInstance.index(b)] * d1; 
        } 
      } 
    } else {
      System.out.println("CLASS MISSING");
    } 
  }
  
  private void actualUpdateClassifierBalanced(Instance paramInstance) throws Exception {
    if (!paramInstance.classIsMissing()) {
      double d = makePredictionBalanced(paramInstance);
      if (d != paramInstance.classValue()) {
        double d1;
        double d2;
        this.m_Mistakes++;
        if (d == 0.0D) {
          d1 = this.m_Alpha;
          d2 = this.m_Beta;
        } else {
          d1 = this.m_Beta;
          d2 = this.m_Alpha;
        } 
        int i = paramInstance.numValues();
        int j = this.m_Train.classIndex();
        for (byte b = 0; b < i; b++) {
          if (paramInstance.index(b) != j && paramInstance.valueSparse(b) == 1.0D) {
            this.m_predPosVector[paramInstance.index(b)] = this.m_predPosVector[paramInstance.index(b)] * d1;
            this.m_predNegVector[paramInstance.index(b)] = this.m_predNegVector[paramInstance.index(b)] * d2;
          } 
        } 
      } 
    } else {
      System.out.println("CLASS MISSING");
    } 
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    this.m_ReplaceMissingValues.input(paramInstance);
    this.m_ReplaceMissingValues.batchFinished();
    Instance instance = this.m_ReplaceMissingValues.output();
    this.m_NominalToBinary.input(instance);
    this.m_NominalToBinary.batchFinished();
    instance = this.m_NominalToBinary.output();
    return this.m_Balanced ? makePredictionBalanced(instance) : makePrediction(instance);
  }
  
  private double makePrediction(Instance paramInstance) throws Exception {
    double d = 0.0D;
    int i = paramInstance.numValues();
    int j = this.m_Train.classIndex();
    for (byte b = 0; b < i; b++) {
      if (paramInstance.index(b) != j && paramInstance.valueSparse(b) == 1.0D)
        d += this.m_predPosVector[paramInstance.index(b)]; 
    } 
    return (d > this.m_actualThreshold) ? 1.0D : 0.0D;
  }
  
  private double makePredictionBalanced(Instance paramInstance) throws Exception {
    double d = 0.0D;
    int i = paramInstance.numValues();
    int j = this.m_Train.classIndex();
    for (byte b = 0; b < i; b++) {
      if (paramInstance.index(b) != j && paramInstance.valueSparse(b) == 1.0D)
        d += this.m_predPosVector[paramInstance.index(b)] - this.m_predNegVector[paramInstance.index(b)]; 
    } 
    return (d > this.m_actualThreshold) ? 1.0D : 0.0D;
  }
  
  public String toString() {
    if (this.m_predPosVector == null)
      return "Winnow: No model built yet."; 
    null = "Winnow\n\nAttribute weights\n\n";
    int i = this.m_Train.classIndex();
    if (!this.m_Balanced) {
      for (byte b = 0; b < this.m_Train.numAttributes(); b++) {
        if (b != i)
          null = null + "w" + b + " " + this.m_predPosVector[b] + "\n"; 
      } 
    } else {
      for (byte b = 0; b < this.m_Train.numAttributes(); b++) {
        if (b != i) {
          null = null + "w" + b + " p " + this.m_predPosVector[b];
          null = null + " n " + this.m_predNegVector[b];
          double d = this.m_predPosVector[b] - this.m_predNegVector[b];
          null = null + " d " + d + "\n";
        } 
      } 
    } 
    return null + "\nCumulated mistake count: " + this.m_Mistakes + "\n\n";
  }
  
  public String balancedTipText() {
    return "Whether to use the balanced version of the algorithm.";
  }
  
  public boolean getBalanced() {
    return this.m_Balanced;
  }
  
  public void setBalanced(boolean paramBoolean) {
    this.m_Balanced = paramBoolean;
  }
  
  public String alphaTipText() {
    return "Promotion coefficient alpha.";
  }
  
  public double getAlpha() {
    return this.m_Alpha;
  }
  
  public void setAlpha(double paramDouble) {
    this.m_Alpha = paramDouble;
  }
  
  public String betaTipText() {
    return "Demotion coefficient beta.";
  }
  
  public double getBeta() {
    return this.m_Beta;
  }
  
  public void setBeta(double paramDouble) {
    this.m_Beta = paramDouble;
  }
  
  public String thresholdTipText() {
    return "Prediction threshold (-1 means: set to number of attributes).";
  }
  
  public double getThreshold() {
    return this.m_Threshold;
  }
  
  public void setThreshold(double paramDouble) {
    this.m_Threshold = paramDouble;
  }
  
  public String defaultWeightTipText() {
    return "Initial value of weights/coefficients.";
  }
  
  public double getDefaultWeight() {
    return this.m_defaultWeight;
  }
  
  public void setDefaultWeight(double paramDouble) {
    this.m_defaultWeight = paramDouble;
  }
  
  public String numIterationsTipText() {
    return "The number of iterations to be performed.";
  }
  
  public int getNumIterations() {
    return this.m_numIterations;
  }
  
  public void setNumIterations(int paramInt) {
    this.m_numIterations = paramInt;
  }
  
  public String seedTipText() {
    return "Random number seed used for data shuffling (-1 means no randomization).";
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new Winnow(), paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\Winnow.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */