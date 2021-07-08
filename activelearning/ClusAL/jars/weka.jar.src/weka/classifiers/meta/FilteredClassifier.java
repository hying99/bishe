package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class FilteredClassifier extends Classifier implements Drawable {
  protected Classifier m_Classifier = (Classifier)new ZeroR();
  
  protected Filter m_Filter = (Filter)new AttributeSelection();
  
  protected Instances m_FilteredInstances;
  
  public String globalInfo() {
    return "Class for running an arbitrary classifier on data that has been passed through an arbitrary filter, providing the only way to access \"supervised\" filters in the Explorer. Like the classifier, the structure of the filter is based exclusively on the training data and test instances will be processed by the filter without changing its structure.";
  }
  
  public FilteredClassifier() {
    this((Classifier)new ZeroR(), (Filter)new AttributeSelection());
  }
  
  public FilteredClassifier(Classifier paramClassifier, Filter paramFilter) {
    this.m_Classifier = paramClassifier;
    this.m_Filter = paramFilter;
  }
  
  public int graphType() {
    return (this.m_Classifier instanceof Drawable) ? ((Drawable)this.m_Classifier).graphType() : 0;
  }
  
  public String graph() throws Exception {
    if (this.m_Classifier instanceof Drawable)
      return ((Drawable)this.m_Classifier).graph(); 
    throw new Exception("Classifier: " + getClassifierSpec() + " cannot be graphed");
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tFull class name of classifier to use, followed\n\tby scheme options.\n\teg: \"weka.classifiers.bayes.NaiveBayes -D\"", "W", 1, "-W <classifier specification>"));
    vector.addElement(new Option("\tFull class name of filter to use, followed\n\tby filter options.\n\teg: \"weka.filters.AttributeFilter -V -R 1,2\"", "F", 1, "-F <filter specification>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('W', paramArrayOfString);
    if (str1.length() > 0) {
      String[] arrayOfString = Utils.splitOptions(str1);
      if (arrayOfString.length == 0)
        throw new IllegalArgumentException("Invalid classifier specification string"); 
      String str = arrayOfString[0];
      arrayOfString[0] = "";
      setClassifier(Classifier.forName(str, arrayOfString));
    } else {
      setClassifier((Classifier)new ZeroR());
    } 
    String str2 = Utils.getOption('F', paramArrayOfString);
    if (str2.length() > 0) {
      String[] arrayOfString = Utils.splitOptions(str2);
      if (arrayOfString.length == 0)
        throw new IllegalArgumentException("Invalid filter specification string"); 
      String str = arrayOfString[0];
      arrayOfString[0] = "";
      setFilter((Filter)Utils.forName(Filter.class, str, arrayOfString));
    } else {
      setFilter((Filter)new AttributeSelection());
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[4];
    byte b = 0;
    arrayOfString[b++] = "-W";
    arrayOfString[b++] = "" + getClassifierSpec();
    arrayOfString[b++] = "-F";
    arrayOfString[b++] = "" + getFilterSpec();
    return arrayOfString;
  }
  
  public String classifierTipText() {
    return "The classifier to be used.";
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
  
  public String filterTipText() {
    return "The filter to be used.";
  }
  
  public void setFilter(Filter paramFilter) {
    this.m_Filter = paramFilter;
  }
  
  public Filter getFilter() {
    return this.m_Filter;
  }
  
  protected String getFilterSpec() {
    Filter filter = getFilter();
    return (filter instanceof OptionHandler) ? (filter.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)filter).getOptions())) : filter.getClass().getName();
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (this.m_Classifier == null)
      throw new Exception("No base classifiers have been set!"); 
    this.m_Filter.setInputFormat(paramInstances);
    paramInstances = Filter.useFilter(paramInstances, this.m_Filter);
    this.m_FilteredInstances = paramInstances.stringFreeStructure();
    this.m_Classifier.buildClassifier(paramInstances);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (this.m_Filter.numPendingOutput() > 0)
      throw new Exception("Filter output queue not empty!"); 
    if (!this.m_Filter.input(paramInstance))
      throw new Exception("Filter didn't make the test instance immediately available!"); 
    this.m_Filter.batchFinished();
    Instance instance = this.m_Filter.output();
    return this.m_Classifier.distributionForInstance(instance);
  }
  
  public String toString() {
    return (this.m_FilteredInstances == null) ? "FilteredClassifier: No model built yet." : ("FilteredClassifier using " + getClassifierSpec() + " on data filtered through " + getFilterSpec() + "\n\nFiltered Header\n" + this.m_FilteredInstances.toString() + "\n\nClassifier Model\n" + this.m_Classifier.toString());
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new FilteredClassifier(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\FilteredClassifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */