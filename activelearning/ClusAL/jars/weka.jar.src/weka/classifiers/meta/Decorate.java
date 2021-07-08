package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

public class Decorate extends Classifier implements OptionHandler {
  protected boolean m_Debug = false;
  
  protected Classifier m_Classifier = (Classifier)new J48();
  
  protected Vector m_Committee = null;
  
  protected int m_DesiredSize = 15;
  
  protected int m_NumIterations = 50;
  
  protected int m_Seed = 0;
  
  protected double m_ArtSize = 1.0D;
  
  protected Random m_Random = new Random(0L);
  
  protected Vector m_AttributeStats = null;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(8);
    vector.addElement(new Option("\tTurn on debugging output.", "D", 0, "-D"));
    vector.addElement(new Option("\tDesired size of ensemble.\n\t(default 15)", "I", 1, "-I"));
    vector.addElement(new Option("\tMaximum number of Decorate iterations.\n\t(default 50)", "M", 1, "-M"));
    vector.addElement(new Option("\tFull name of base classifier.\n\t(default weka.classifiers.trees.J48)", "W", 1, "-W"));
    vector.addElement(new Option("\tSeed for random number generator.\n\tIf set to -1, use a random seed.\n\t(default 0)", "S", 1, "-S"));
    vector.addElement(new Option("\tFactor that determines number of artificial examples to generate.\n\tSpecified proportional to training set size.\n\t(default 1.0)", "R", 1, "-R"));
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to classifier " + this.m_Classifier.getClass().getName() + ":"));
      Enumeration enumeration = this.m_Classifier.listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setDebug(Utils.getFlag('D', paramArrayOfString));
    String str1 = Utils.getOption('I', paramArrayOfString);
    if (str1.length() != 0) {
      setDesiredSize(Integer.parseInt(str1));
    } else {
      setDesiredSize(15);
    } 
    String str2 = Utils.getOption('M', paramArrayOfString);
    if (str2.length() != 0) {
      setNumIterations(Integer.parseInt(str2));
    } else {
      setNumIterations(50);
    } 
    String str3 = Utils.getOption('S', paramArrayOfString);
    if (str3.length() != 0) {
      setSeed(Integer.parseInt(str3));
    } else {
      setSeed(0);
    } 
    String str4 = Utils.getOption('R', paramArrayOfString);
    if (str4.length() != 0) {
      setArtificialSize(Double.parseDouble(str4));
    } else {
      setArtificialSize(1.0D);
    } 
    String str5 = Utils.getOption('W', paramArrayOfString);
    if (str5.length() != 0)
      setClassifier(Classifier.forName(str5, Utils.partitionOptions(paramArrayOfString))); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler)
      arrayOfString1 = this.m_Classifier.getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 12];
    int i = 0;
    if (getDebug())
      arrayOfString2[i++] = "-D"; 
    arrayOfString2[i++] = "-S";
    arrayOfString2[i++] = "" + getSeed();
    arrayOfString2[i++] = "-I";
    arrayOfString2[i++] = "" + getDesiredSize();
    arrayOfString2[i++] = "-M";
    arrayOfString2[i++] = "" + getNumIterations();
    arrayOfString2[i++] = "-R";
    arrayOfString2[i++] = "" + getArtificialSize();
    if (getClassifier() != null) {
      arrayOfString2[i++] = "-W";
      arrayOfString2[i++] = getClassifier().getClass().getName();
    } 
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public String desiredSizeTipText() {
    return "the desired number of member classifiers in the Decorate ensemble. Decorate may terminate before this size is reached (depending on the value of numIterations). Larger ensemble sizes usually lead to more accurate models, but increases training time and model complexity.";
  }
  
  public String numIterationsTipText() {
    return "the maximum number of Decorate iterations to run. Each iteration generates a classifier, but does not necessarily add it to the ensemble. Decorate stops when the desired ensemble size is reached. This parameter should be greater than equal to the desiredSize. If the desiredSize is not being reached it may help to increase this value.";
  }
  
  public String artificialSizeTipText() {
    return "determines the number of artificial examples to use during training. Specified as a proportion of the training data. Higher values can increase ensemble diversity.";
  }
  
  public String seedTipText() {
    return "seed for random number generator used for creating artificial data. Set to -1 to use a random seed.";
  }
  
  public String classifierTipText() {
    return "the desired base learner for the ensemble.";
  }
  
  public String globalInfo() {
    return "DECORATE is a meta-learner for building diverse ensembles of classifiers by using specially constructed artificial training examples. Comprehensive experiments have demonstrated that this technique is consistently more accurate than the base classifier, Bagging and Random Forests.Decorate also obtains higher accuracy than Boosting on small training sets, and achieves comparable performance on larger training sets. For more details see: P. Melville & R. J. Mooney. Constructing diverse classifier ensembles using artificial training examples (IJCAI 2003).\nP. Melville & R. J. Mooney. Creating diversity in ensembles using artificial data (submitted).";
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_Classifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_Classifier;
  }
  
  public double getArtificialSize() {
    return this.m_ArtSize;
  }
  
  public void setArtificialSize(double paramDouble) {
    this.m_ArtSize = paramDouble;
  }
  
  public int getDesiredSize() {
    return this.m_DesiredSize;
  }
  
  public void setDesiredSize(int paramInt) {
    this.m_DesiredSize = paramInt;
  }
  
  public void setNumIterations(int paramInt) {
    this.m_NumIterations = paramInt;
  }
  
  public int getNumIterations() {
    return this.m_NumIterations;
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (this.m_Classifier == null)
      throw new Exception("A base classifier has not been specified!"); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Decorate can't handle a numeric class!"); 
    if (this.m_NumIterations < this.m_DesiredSize)
      throw new Exception("Max number of iterations must be >= desired ensemble size!"); 
    if (this.m_Seed == -1) {
      this.m_Random = new Random();
    } else {
      this.m_Random = new Random(this.m_Seed);
    } 
    byte b1 = 1;
    byte b2 = 1;
    Instances instances1 = new Instances(paramInstances);
    instances1.deleteWithMissingClass();
    Instances instances2 = null;
    int i = (int)(Math.abs(this.m_ArtSize) * instances1.numInstances());
    if (i == 0)
      i = 1; 
    computeStats(paramInstances);
    this.m_Committee = new Vector();
    Classifier classifier = this.m_Classifier;
    classifier.buildClassifier(instances1);
    this.m_Committee.add(classifier);
    double d = computeError(instances1);
    if (this.m_Debug)
      System.out.println("Initialize:\tClassifier " + b1 + " added to ensemble. Ensemble error = " + d); 
    while (b1 < this.m_DesiredSize && b2 < this.m_NumIterations) {
      instances2 = generateArtificialData(i, paramInstances);
      labelData(instances2);
      addInstances(instances1, instances2);
      Classifier[] arrayOfClassifier = Classifier.makeCopies(this.m_Classifier, 1);
      classifier = arrayOfClassifier[0];
      classifier.buildClassifier(instances1);
      removeInstances(instances1, i);
      this.m_Committee.add(classifier);
      double d1 = computeError(instances1);
      if (d1 <= d) {
        b1++;
        d = d1;
        if (this.m_Debug)
          System.out.println("Iteration: " + (1 + b2) + "\tClassifier " + b1 + " added to ensemble. Ensemble error = " + d); 
      } else {
        this.m_Committee.removeElementAt(this.m_Committee.size() - 1);
      } 
      b2++;
    } 
  }
  
  protected void computeStats(Instances paramInstances) throws Exception {
    int i = paramInstances.numAttributes();
    this.m_AttributeStats = new Vector(i);
    for (byte b = 0; b < i; b++) {
      if (paramInstances.attribute(b).isNominal()) {
        int[] arrayOfInt = (paramInstances.attributeStats(b)).nominalCounts;
        double[] arrayOfDouble1 = new double[arrayOfInt.length];
        if (arrayOfDouble1.length < 2)
          throw new Exception("Nominal attribute has less than two distinct values!"); 
        for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
          arrayOfDouble1[b1] = (arrayOfInt[b1] + 1); 
        Utils.normalize(arrayOfDouble1);
        double[] arrayOfDouble2 = new double[arrayOfDouble1.length - 1];
        arrayOfDouble2[0] = arrayOfDouble1[0];
        for (byte b2 = 1; b2 < arrayOfDouble2.length; b2++)
          arrayOfDouble2[b2] = arrayOfDouble2[b2 - 1] + arrayOfDouble1[b2]; 
        this.m_AttributeStats.add(b, arrayOfDouble2);
      } else if (paramInstances.attribute(b).isNumeric()) {
        double[] arrayOfDouble = new double[2];
        arrayOfDouble[0] = paramInstances.meanOrMode(b);
        arrayOfDouble[1] = Math.sqrt(paramInstances.variance(b));
        this.m_AttributeStats.add(b, arrayOfDouble);
      } else {
        System.err.println("Decorate can only handle numeric and nominal values.");
      } 
    } 
  }
  
  protected Instances generateArtificialData(int paramInt, Instances paramInstances) {
    int i = paramInstances.numAttributes();
    Instances instances = new Instances(paramInstances, paramInt);
    for (byte b = 0; b < paramInt; b++) {
      double[] arrayOfDouble = new double[i];
      for (byte b1 = 0; b1 < i; b1++) {
        if (paramInstances.attribute(b1).isNominal()) {
          double[] arrayOfDouble1 = this.m_AttributeStats.get(b1);
          arrayOfDouble[b1] = selectIndexProbabilistically(arrayOfDouble1);
        } else if (paramInstances.attribute(b1).isNumeric()) {
          double[] arrayOfDouble1 = this.m_AttributeStats.get(b1);
          arrayOfDouble[b1] = this.m_Random.nextGaussian() * arrayOfDouble1[1] + arrayOfDouble1[0];
        } else {
          System.err.println("Decorate can only handle numeric and nominal values.");
        } 
      } 
      Instance instance = new Instance(1.0D, arrayOfDouble);
      instances.add(instance);
    } 
    return instances;
  }
  
  protected void labelData(Instances paramInstances) throws Exception {
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = paramInstances.instance(b);
      double[] arrayOfDouble = distributionForInstance(instance);
      instance.setClassValue(inverseLabel(arrayOfDouble));
    } 
  }
  
  protected int inverseLabel(double[] paramArrayOfdouble) throws Exception {
    double[] arrayOfDouble1 = new double[paramArrayOfdouble.length];
    for (byte b1 = 0; b1 < paramArrayOfdouble.length; b1++) {
      if (paramArrayOfdouble[b1] == 0.0D) {
        arrayOfDouble1[b1] = Double.MAX_VALUE / paramArrayOfdouble.length;
      } else {
        arrayOfDouble1[b1] = 1.0D / paramArrayOfdouble[b1];
      } 
    } 
    Utils.normalize(arrayOfDouble1);
    double[] arrayOfDouble2 = new double[arrayOfDouble1.length];
    arrayOfDouble2[0] = arrayOfDouble1[0];
    for (byte b2 = 1; b2 < arrayOfDouble1.length; b2++)
      arrayOfDouble2[b2] = arrayOfDouble1[b2] + arrayOfDouble2[b2 - 1]; 
    if (Double.isNaN(arrayOfDouble2[arrayOfDouble1.length - 1]))
      System.err.println("Cumulative class membership probability is NaN!"); 
    return selectIndexProbabilistically(arrayOfDouble2);
  }
  
  protected int selectIndexProbabilistically(double[] paramArrayOfdouble) {
    double d = this.m_Random.nextDouble();
    byte b;
    for (b = 0; b < paramArrayOfdouble.length && d > paramArrayOfdouble[b]; b++);
    return b;
  }
  
  protected void removeInstances(Instances paramInstances, int paramInt) {
    int i = paramInstances.numInstances();
    for (int j = i - 1; j > i - 1 - paramInt; j--)
      paramInstances.delete(j); 
  }
  
  protected void addInstances(Instances paramInstances1, Instances paramInstances2) {
    for (byte b = 0; b < paramInstances2.numInstances(); b++)
      paramInstances1.add(paramInstances2.instance(b)); 
  }
  
  protected double computeError(Instances paramInstances) throws Exception {
    double d = 0.0D;
    int i = paramInstances.numInstances();
    for (byte b = 0; b < i; b++) {
      Instance instance = paramInstances.instance(b);
      if (instance.classValue() != (int)classifyInstance(instance))
        d++; 
    } 
    return d / i;
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (paramInstance.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Decorate can't handle a numeric class!"); 
    double[] arrayOfDouble = new double[paramInstance.numClasses()];
    for (byte b = 0; b < this.m_Committee.size(); b++) {
      Classifier classifier = this.m_Committee.get(b);
      double[] arrayOfDouble1 = classifier.distributionForInstance(paramInstance);
      for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
        arrayOfDouble[b1] = arrayOfDouble[b1] + arrayOfDouble1[b1]; 
    } 
    if (Utils.eq(Utils.sum(arrayOfDouble), 0.0D))
      return arrayOfDouble; 
    Utils.normalize(arrayOfDouble);
    return arrayOfDouble;
  }
  
  public String toString() {
    if (this.m_Committee == null)
      return "Decorate: No model built yet."; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Decorate base classifiers: \n\n");
    for (byte b = 0; b < this.m_Committee.size(); b++)
      stringBuffer.append(((Classifier)this.m_Committee.get(b)).toString() + "\n\n"); 
    stringBuffer.append("Number of classifier in the ensemble: " + this.m_Committee.size() + "\n");
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new Decorate(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\Decorate.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */