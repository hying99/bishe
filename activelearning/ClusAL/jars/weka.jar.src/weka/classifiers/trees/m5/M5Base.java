package weka.classifiers.trees.m5;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.core.AdditionalMeasureProducer;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public abstract class M5Base extends Classifier implements OptionHandler, AdditionalMeasureProducer {
  private Instances m_instances;
  
  private int m_classIndex;
  
  private int m_numAttributes;
  
  private int m_numInstances;
  
  protected FastVector m_ruleSet;
  
  private boolean m_generateRules = false;
  
  private boolean m_unsmoothedPredictions = false;
  
  private ReplaceMissingValues m_replaceMissing;
  
  private NominalToBinary m_nominalToBinary;
  
  protected boolean m_saveInstances = false;
  
  protected boolean m_regressionTree;
  
  protected boolean m_useUnpruned = false;
  
  protected double m_minNumInstances = 4.0D;
  
  public M5Base() {
    this.m_useUnpruned = false;
    this.m_minNumInstances = 4.0D;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tUse unpruned tree/rules\n", "N", 0, "-N"));
    vector.addElement(new Option("\tUse unsmoothed predictions\n", "U", 0, "-U"));
    vector.addElement(new Option("\tBuild regression tree/rule rather than a model tree/rule\n", "R", 0, "-R"));
    vector.addElement(new Option("\tSet minimum number of instances per leaf\n\t(default 4)", "M", 1, "-M <minimum number of instances>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setUnpruned(Utils.getFlag('N', paramArrayOfString));
    setUseUnsmoothed(Utils.getFlag('U', paramArrayOfString));
    setBuildRegressionTree(Utils.getFlag('R', paramArrayOfString));
    String str = Utils.getOption('M', paramArrayOfString);
    if (str.length() != 0)
      setMinNumInstances((new Double(str)).doubleValue()); 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[5];
    byte b = 0;
    if (getUnpruned())
      arrayOfString[b++] = "-N"; 
    if (getUseUnsmoothed())
      arrayOfString[b++] = "-U"; 
    if (getBuildRegressionTree())
      arrayOfString[b++] = "-R"; 
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getMinNumInstances();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void setUnpruned(boolean paramBoolean) {
    this.m_useUnpruned = paramBoolean;
  }
  
  public boolean getUnpruned() {
    return this.m_useUnpruned;
  }
  
  protected void setGenerateRules(boolean paramBoolean) {
    this.m_generateRules = paramBoolean;
  }
  
  protected boolean getGenerateRules() {
    return this.m_generateRules;
  }
  
  public void setUseUnsmoothed(boolean paramBoolean) {
    this.m_unsmoothedPredictions = paramBoolean;
  }
  
  public boolean getUseUnsmoothed() {
    return this.m_unsmoothedPredictions;
  }
  
  public boolean getBuildRegressionTree() {
    return this.m_regressionTree;
  }
  
  public void setBuildRegressionTree(boolean paramBoolean) {
    this.m_regressionTree = paramBoolean;
  }
  
  public void setMinNumInstances(double paramDouble) {
    this.m_minNumInstances = paramDouble;
  }
  
  public double getMinNumInstances() {
    return this.m_minNumInstances;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    this.m_instances = new Instances(paramInstances);
    this.m_replaceMissing = new ReplaceMissingValues();
    this.m_instances.deleteWithMissingClass();
    this.m_replaceMissing.setInputFormat(this.m_instances);
    this.m_instances = Filter.useFilter(this.m_instances, (Filter)this.m_replaceMissing);
    this.m_nominalToBinary = new NominalToBinary();
    this.m_nominalToBinary.setInputFormat(this.m_instances);
    this.m_instances = Filter.useFilter(this.m_instances, (Filter)this.m_nominalToBinary);
    this.m_instances.randomize(new Random(1L));
    this.m_classIndex = this.m_instances.classIndex();
    this.m_numAttributes = this.m_instances.numAttributes();
    this.m_numInstances = this.m_instances.numInstances();
    this.m_ruleSet = new FastVector();
    if (this.m_generateRules) {
      Instances instances = this.m_instances;
      double d1 = 0.0D;
      double d2 = 0.0D;
      do {
        Rule rule = new Rule();
        rule.setSmoothing(!this.m_unsmoothedPredictions);
        rule.setRegressionTree(this.m_regressionTree);
        rule.setUnpruned(this.m_useUnpruned);
        rule.setSaveInstances(false);
        rule.setMinNumInstances(this.m_minNumInstances);
        rule.buildClassifier(instances);
        this.m_ruleSet.addElement(rule);
        instances = rule.notCoveredInstances();
      } while (instances.numInstances() > 0);
    } else {
      Rule rule = new Rule();
      rule.setUseTree(true);
      rule.setSmoothing(!this.m_unsmoothedPredictions);
      rule.setSaveInstances(this.m_saveInstances);
      rule.setRegressionTree(this.m_regressionTree);
      rule.setUnpruned(this.m_useUnpruned);
      rule.setMinNumInstances(this.m_minNumInstances);
      Instances instances = this.m_instances;
      rule.buildClassifier(instances);
      this.m_ruleSet.addElement(rule);
      this.m_instances = new Instances(this.m_instances, 0);
    } 
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    double d = 0.0D;
    boolean bool = false;
    this.m_replaceMissing.input(paramInstance);
    paramInstance = this.m_replaceMissing.output();
    this.m_nominalToBinary.input(paramInstance);
    paramInstance = this.m_nominalToBinary.output();
    if (this.m_ruleSet == null)
      throw new Exception("Classifier has not been built yet!"); 
    if (!this.m_generateRules) {
      Rule rule = (Rule)this.m_ruleSet.elementAt(0);
      return rule.classifyInstance(paramInstance);
    } 
    for (byte b = 0; b < this.m_ruleSet.size(); b++) {
      boolean bool1 = false;
      Rule rule = (Rule)this.m_ruleSet.elementAt(b);
      try {
        d = rule.classifyInstance(paramInstance);
        bool = true;
      } catch (Exception exception) {
        bool1 = true;
      } 
      if (!bool1)
        break; 
    } 
    if (!bool)
      System.out.println("Error in predicting (DecList)"); 
    return d;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_ruleSet == null)
      return "Classifier hasn't been built yet!"; 
    if (this.m_generateRules) {
      stringBuffer.append("M5 " + ((this.m_useUnpruned == true) ? "unpruned " : "pruned ") + ((this.m_regressionTree == true) ? "regression " : "model ") + "rules ");
      if (!this.m_unsmoothedPredictions)
        stringBuffer.append("\n(using smoothed linear models) "); 
      stringBuffer.append(":\n");
      stringBuffer.append("Number of Rules : " + this.m_ruleSet.size() + "\n\n");
      for (byte b = 0; b < this.m_ruleSet.size(); b++) {
        Rule rule = (Rule)this.m_ruleSet.elementAt(b);
        stringBuffer.append("Rule: " + (b + 1) + "\n");
        stringBuffer.append(rule.toString());
      } 
    } else {
      Rule rule = (Rule)this.m_ruleSet.elementAt(0);
      stringBuffer.append(rule.toString());
    } 
    return stringBuffer.toString();
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(1);
    vector.addElement("measureNumRules");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.compareToIgnoreCase("measureNumRules") == 0)
      return measureNumRules(); 
    throw new IllegalArgumentException(paramString + " not supported (M5)");
  }
  
  public double measureNumRules() {
    return this.m_generateRules ? this.m_ruleSet.size() : ((Rule)this.m_ruleSet.elementAt(0)).m_topOfTree.numberOfLinearModels();
  }
  
  public RuleNode getM5RootNode() {
    Rule rule = (Rule)this.m_ruleSet.elementAt(0);
    return rule.getM5RootNode();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\m5\M5Base.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */