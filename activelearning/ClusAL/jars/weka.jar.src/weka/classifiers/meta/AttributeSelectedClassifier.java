package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Vector;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.AdditionalMeasureProducer;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class AttributeSelectedClassifier extends Classifier implements OptionHandler, Drawable, AdditionalMeasureProducer {
  protected Classifier m_Classifier = (Classifier)new ZeroR();
  
  protected AttributeSelection m_AttributeSelection = null;
  
  protected ASEvaluation m_Evaluator = (ASEvaluation)new CfsSubsetEval();
  
  protected ASSearch m_Search = (ASSearch)new BestFirst();
  
  protected Instances m_ReducedHeader;
  
  protected int m_numClasses;
  
  protected double m_numAttributesSelected;
  
  protected double m_selectionTime;
  
  protected double m_totalTime;
  
  public String globalInfo() {
    return "Dimensionality of training and test data is reduced by attribute selection before being passed on to a classifier.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tFull class name of classifier to use, followed\n\tby scheme options. (required)\n\teg: \"weka.classifiers.bayes.NaiveBayes -D\"", "W", 1, "-W <classifier specification>"));
    vector.addElement(new Option("\tFull class name of attribute evaluator, followed\n\tby its options. (required)\n\teg: \"weka.attributeSelection.CfsSubsetEval -L\"", "E", 1, "-E <attribute evaluator specification>"));
    vector.addElement(new Option("\tFull class name of search method, followed\n\tby its options. (required)\n\teg: \"weka.attributeSelection.BestFirst -D 1\"", "S", 1, "-S <search method specification>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('W', paramArrayOfString);
    if (str1.length() == 0)
      throw new Exception("A classifier must be specified with the -W option."); 
    String[] arrayOfString1 = Utils.splitOptions(str1);
    if (arrayOfString1.length == 0)
      throw new Exception("Invalid classifier specification string"); 
    String str2 = arrayOfString1[0];
    arrayOfString1[0] = "";
    setClassifier(Classifier.forName(str2, arrayOfString1));
    String str3 = Utils.getOption('E', paramArrayOfString);
    if (str3.length() == 0)
      throw new Exception("An attribute evaluator must be specified with the -E option."); 
    String[] arrayOfString2 = Utils.splitOptions(str3);
    if (arrayOfString2.length == 0)
      throw new Exception("Invalid attribute evaluator specification string"); 
    String str4 = arrayOfString2[0];
    arrayOfString2[0] = "";
    setEvaluator(ASEvaluation.forName(str4, arrayOfString2));
    String str5 = Utils.getOption('S', paramArrayOfString);
    if (str5.length() == 0)
      throw new Exception("A search method must be specified with the -S option."); 
    String[] arrayOfString3 = Utils.splitOptions(str5);
    if (arrayOfString3.length == 0)
      throw new Exception("Invalid search specification string"); 
    String str6 = arrayOfString3[0];
    arrayOfString3[0] = "";
    setSearch(ASSearch.forName(str6, arrayOfString3));
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[6];
    byte b = 0;
    arrayOfString[b++] = "-W";
    arrayOfString[b++] = "" + getClassifierSpec();
    arrayOfString[b++] = "-E";
    arrayOfString[b++] = "" + getEvaluatorSpec();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSearchSpec();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String classifierTipText() {
    return "Set the classifier to use";
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_Classifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_Classifier;
  }
  
  protected String getClassifierSpec() {
    Classifier classifier = getClassifier();
    return (classifier instanceof OptionHandler) ? (classifier.getClass().getName() + " " + Utils.joinOptions(classifier.getOptions())) : classifier.getClass().getName();
  }
  
  public String evaluatorTipText() {
    return "Set the attribute evaluator to use. This evaluator is used during the attribute selection phase before the classifier is invoked.";
  }
  
  public void setEvaluator(ASEvaluation paramASEvaluation) {
    this.m_Evaluator = paramASEvaluation;
  }
  
  public ASEvaluation getEvaluator() {
    return this.m_Evaluator;
  }
  
  protected String getEvaluatorSpec() {
    ASEvaluation aSEvaluation = getEvaluator();
    return (aSEvaluation instanceof OptionHandler) ? (aSEvaluation.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)aSEvaluation).getOptions())) : aSEvaluation.getClass().getName();
  }
  
  public String searchTipText() {
    return "Set the search method. This search method is used during the attribute selection phase before the classifier is invoked.";
  }
  
  public void setSearch(ASSearch paramASSearch) {
    this.m_Search = paramASSearch;
  }
  
  public ASSearch getSearch() {
    return this.m_Search;
  }
  
  protected String getSearchSpec() {
    ASSearch aSSearch = getSearch();
    return (aSSearch instanceof OptionHandler) ? (aSSearch.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)aSSearch).getOptions())) : aSSearch.getClass().getName();
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (this.m_Classifier == null)
      throw new Exception("No base classifier has been set!"); 
    if (this.m_Evaluator == null)
      throw new Exception("No attribute evaluator has been set!"); 
    if (this.m_Search == null)
      throw new Exception("No search method has been set!"); 
    Instances instances = new Instances(paramInstances);
    instances.deleteWithMissingClass();
    if (instances.numInstances() == 0) {
      this.m_Classifier.buildClassifier(instances);
      return;
    } 
    if (instances.classAttribute().isNominal()) {
      this.m_numClasses = instances.classAttribute().numValues();
    } else {
      this.m_numClasses = 1;
    } 
    this.m_AttributeSelection = new AttributeSelection();
    this.m_AttributeSelection.setEvaluator(this.m_Evaluator);
    this.m_AttributeSelection.setSearch(this.m_Search);
    long l1 = System.currentTimeMillis();
    this.m_AttributeSelection.SelectAttributes(instances);
    long l2 = System.currentTimeMillis();
    instances = this.m_AttributeSelection.reduceDimensionality(instances);
    this.m_Classifier.buildClassifier(instances);
    long l3 = System.currentTimeMillis();
    this.m_numAttributesSelected = this.m_AttributeSelection.numberAttributesSelected();
    this.m_ReducedHeader = new Instances(instances, 0);
    this.m_selectionTime = (l2 - l1);
    this.m_totalTime = (l3 - l1);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    Instance instance;
    if (this.m_AttributeSelection == null) {
      instance = paramInstance;
    } else {
      instance = this.m_AttributeSelection.reduceDimensionality(paramInstance);
    } 
    return this.m_Classifier.distributionForInstance(instance);
  }
  
  public int graphType() {
    return (this.m_Classifier instanceof Drawable) ? ((Drawable)this.m_Classifier).graphType() : 0;
  }
  
  public String graph() throws Exception {
    if (this.m_Classifier instanceof Drawable)
      return ((Drawable)this.m_Classifier).graph(); 
    throw new Exception("Classifier: " + getClassifierSpec() + " cannot be graphed");
  }
  
  public String toString() {
    if (this.m_AttributeSelection == null)
      return "AttributeSelectedClassifier: No attribute selection possible.\n\n" + this.m_Classifier.toString(); 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("AttributeSelectedClassifier:\n\n");
    stringBuffer.append(this.m_AttributeSelection.toResultsString());
    stringBuffer.append("\n\nHeader of reduced data:\n" + this.m_ReducedHeader.toString());
    stringBuffer.append("\n\nClassifier Model\n" + this.m_Classifier.toString());
    return stringBuffer.toString();
  }
  
  public double measureNumAttributesSelected() {
    return this.m_numAttributesSelected;
  }
  
  public double measureSelectionTime() {
    return this.m_selectionTime;
  }
  
  public double measureTime() {
    return this.m_totalTime;
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(3);
    vector.addElement("measureNumAttributesSelected");
    vector.addElement("measureSelectionTime");
    vector.addElement("measureTime");
    if (this.m_Classifier instanceof AdditionalMeasureProducer) {
      Enumeration enumeration = ((AdditionalMeasureProducer)this.m_Classifier).enumerateMeasures();
      while (enumeration.hasMoreElements()) {
        String str = enumeration.nextElement();
        vector.addElement(str);
      } 
    } 
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.compareToIgnoreCase("measureNumAttributesSelected") == 0)
      return measureNumAttributesSelected(); 
    if (paramString.compareToIgnoreCase("measureSelectionTime") == 0)
      return measureSelectionTime(); 
    if (paramString.compareToIgnoreCase("measureTime") == 0)
      return measureTime(); 
    if (this.m_Classifier instanceof AdditionalMeasureProducer)
      return ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(paramString); 
    throw new IllegalArgumentException(paramString + " not supported (AttributeSelectedClassifier)");
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new AttributeSelectedClassifier(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\AttributeSelectedClassifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */