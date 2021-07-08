package weka.classifiers.functions;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.lmt.LogisticBase;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class SimpleLogistic extends Classifier implements OptionHandler, AdditionalMeasureProducer, WeightedInstancesHandler {
  protected LogisticBase m_boostedModel;
  
  protected NominalToBinary m_NominalToBinary = null;
  
  protected ReplaceMissingValues m_ReplaceMissingValues = null;
  
  protected int m_numBoostingIterations = 0;
  
  protected int m_maxBoostingIterations = 500;
  
  protected int m_heuristicStop = 50;
  
  protected boolean m_useCrossValidation = true;
  
  protected boolean m_errorOnProbabilities = false;
  
  public SimpleLogistic() {}
  
  public SimpleLogistic(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {}
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.classAttribute().type() != 1)
      throw new UnsupportedClassTypeException("Class attribute must be nominal."); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    if (paramInstances.numInstances() == 0)
      throw new Exception("No instances without missing class values in training file!"); 
    this.m_ReplaceMissingValues = new ReplaceMissingValues();
    this.m_ReplaceMissingValues.setInputFormat(paramInstances);
    paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_ReplaceMissingValues);
    this.m_NominalToBinary = new NominalToBinary();
    this.m_NominalToBinary.setInputFormat(paramInstances);
    paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_NominalToBinary);
    this.m_boostedModel = new LogisticBase(this.m_numBoostingIterations, this.m_useCrossValidation, this.m_errorOnProbabilities);
    this.m_boostedModel.setMaxIterations(this.m_maxBoostingIterations);
    this.m_boostedModel.setHeuristicStop(this.m_heuristicStop);
    this.m_boostedModel.buildClassifier(paramInstances);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    this.m_ReplaceMissingValues.input(paramInstance);
    paramInstance = this.m_ReplaceMissingValues.output();
    this.m_NominalToBinary.input(paramInstance);
    paramInstance = this.m_NominalToBinary.output();
    return this.m_boostedModel.distributionForInstance(paramInstance);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(5);
    vector.addElement(new Option("\tSet fixed number of iterations for LogitBoost\n", "I", 1, "-I <iterations>"));
    vector.addElement(new Option("\tUse stopping criterion on training set (instead of cross-validation)\n", "S", 0, "-S"));
    vector.addElement(new Option("\tUse error on probabilities (rmse) instead of misclassification error for stopping criterion\n", "P", 0, "-P"));
    vector.addElement(new Option("\tSet maximum number of boosting iterations\n", "M", 1, "-M <iterations>"));
    vector.addElement(new Option("\tSet parameter for heuristic for early stopping of LogitBoost.If enabled, the minimum is selected greedily, stopping if the current minimum has not changed for iter iterations. By default, heuristic is enabled withvalue 50. Set to zero to disable heuristic.\n", "H", 1, "-H <iterations>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('I', paramArrayOfString);
    if (str.length() != 0)
      setNumBoostingIterations((new Integer(str)).intValue()); 
    setUseCrossValidation(!Utils.getFlag('S', paramArrayOfString));
    setErrorOnProbabilities(Utils.getFlag('P', paramArrayOfString));
    str = Utils.getOption('L', paramArrayOfString);
    if (str.length() != 0)
      setMaxBoostingIterations((new Integer(str)).intValue()); 
    str = Utils.getOption('H', paramArrayOfString);
    if (str.length() != 0)
      setHeuristicStop((new Integer(str)).intValue()); 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[9];
    byte b = 0;
    arrayOfString[b++] = "-I";
    arrayOfString[b++] = "" + getNumBoostingIterations();
    if (!getUseCrossValidation())
      arrayOfString[b++] = "-S"; 
    if (getErrorOnProbabilities())
      arrayOfString[b++] = "-P"; 
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getMaxBoostingIterations();
    arrayOfString[b++] = "-H";
    arrayOfString[b++] = "" + getHeuristicStop();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public int getNumBoostingIterations() {
    return this.m_numBoostingIterations;
  }
  
  public boolean getUseCrossValidation() {
    return this.m_useCrossValidation;
  }
  
  public boolean getErrorOnProbabilities() {
    return this.m_errorOnProbabilities;
  }
  
  public int getMaxBoostingIterations() {
    return this.m_maxBoostingIterations;
  }
  
  public int getHeuristicStop() {
    return this.m_heuristicStop;
  }
  
  public void setNumBoostingIterations(int paramInt) {
    this.m_numBoostingIterations = paramInt;
  }
  
  public void setUseCrossValidation(boolean paramBoolean) {
    this.m_useCrossValidation = paramBoolean;
  }
  
  public void setErrorOnProbabilities(boolean paramBoolean) {
    this.m_errorOnProbabilities = paramBoolean;
  }
  
  public void setMaxBoostingIterations(int paramInt) {
    this.m_maxBoostingIterations = paramInt;
  }
  
  public void setHeuristicStop(int paramInt) {
    if (paramInt == 0) {
      this.m_heuristicStop = this.m_maxBoostingIterations;
    } else {
      this.m_heuristicStop = paramInt;
    } 
  }
  
  public int getNumRegressions() {
    return this.m_boostedModel.getNumRegressions();
  }
  
  public String toString() {
    return (this.m_boostedModel == null) ? "No model built" : ("SimpleLogistic:\n" + this.m_boostedModel.toString());
  }
  
  public double measureAttributesUsed() {
    return this.m_boostedModel.percentAttributesUsed();
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(3);
    vector.addElement("measureAttributesUsed");
    vector.addElement("measureNumIterations");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.compareToIgnoreCase("measureAttributesUsed") == 0)
      return measureAttributesUsed(); 
    if (paramString.compareToIgnoreCase("measureNumIterations") == 0)
      return getNumRegressions(); 
    throw new IllegalArgumentException(paramString + " not supported (SimpleLogistic)");
  }
  
  public String globalInfo() {
    return "Classifier for building linear logistic regression models. LogitBoost with simple regression functions as base learners is used for fitting the logistic models. The optimal number of LogitBoost iterations to perform is cross-validated, which leads to automatic attribute selection. For more information see: N.Landwehr, M.Hall, E. Frank 'Logistic Model Trees' (ECML 2003).";
  }
  
  public String numBoostingIterationsTipText() {
    return "Set fixed number of iterations for LogitBoost. If >= 0, this sets the number of LogitBoost iterations to perform. If < 0, the number is cross-validated or a stopping criterion on the training set is used (depending on the value of useCrossValidation).";
  }
  
  public String useCrossValidationTipText() {
    return "Sets whether the number of LogitBoost iterations is to be cross-validated or the stopping criterion on the training set should be used. If not set (and no fixed number of iterations was given), the number of LogitBoost iterations is used that minimizes the error on the training set (misclassification error or error on probabilities depending on errorOnProbabilities).";
  }
  
  public String errorOnProbabilitiesTipText() {
    return "Use error on the probabilties as error measure when determining the best number of LogitBoost iterations. If set, the number of LogitBoost iterations is chosen that minimizes the root mean squared error (either on the training set or in the cross-validation, depending on useCrossValidation).";
  }
  
  public String maxBoostingIterationsTipText() {
    return "Sets the maximum number of iterations for LogitBoost. Default value is 500, for very small/large datasets a lower/higher value might be preferable.";
  }
  
  public String heuristicStopTipText() {
    return "If heuristicStop > 0, the heuristic for greedy stopping while cross-validating the number of LogitBoost iterations is enabled. This means LogitBoost is stopped if no new error minimum has been reached in the last heuristicStop iterations. It is recommended to use this heuristic, it gives a large speed-up especially on small datasets. The default value is 50.";
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new SimpleLogistic(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\SimpleLogistic.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */