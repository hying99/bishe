package weka.filters.supervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeTransformer;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.Ranker;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;

public class AttributeSelection extends Filter implements SupervisedFilter, OptionHandler {
  private weka.attributeSelection.AttributeSelection m_trainSelector;
  
  private ASEvaluation m_ASEvaluator;
  
  private ASSearch m_ASSearch;
  
  private String[] m_FilterOptions;
  
  private int[] m_SelectedAttributes;
  
  public String globalInfo() {
    return "A supervised attribute filter that can be used to select attributes. It is very flexible and allows various search and evaluation methods to be combined.";
  }
  
  public AttributeSelection() {
    resetOptions();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tSets search method for subset evaluators.", "S", 1, "-S <\"Name of search class [search options]\">"));
    vector.addElement(new Option("\tSets attribute/subset evaluator.", "E", 1, "-E <\"Name of attribute/subset evaluation class [evaluator options]\">"));
    if (this.m_ASEvaluator != null && this.m_ASEvaluator instanceof OptionHandler) {
      Enumeration enumeration = ((OptionHandler)this.m_ASEvaluator).listOptions();
      vector.addElement(new Option("", "", 0, "\nOptions specific to evaluator " + this.m_ASEvaluator.getClass().getName() + ":"));
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    if (this.m_ASSearch != null && this.m_ASSearch instanceof OptionHandler) {
      Enumeration enumeration = ((OptionHandler)this.m_ASSearch).listOptions();
      vector.addElement(new Option("", "", 0, "\nOptions specific to search " + this.m_ASSearch.getClass().getName() + ":"));
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    if (Utils.getFlag('X', paramArrayOfString))
      throw new Exception("Cross validation is not a valid option when using attribute selection as a Filter."); 
    String str = Utils.getOption('E', paramArrayOfString);
    if (str.length() != 0) {
      str = str.trim();
      int i = str.indexOf(' ');
      String str1 = str;
      String str2 = "";
      String[] arrayOfString = null;
      if (i != -1) {
        str1 = str.substring(0, i);
        str2 = str.substring(i).trim();
        arrayOfString = Utils.splitOptions(str2);
      } 
      setEvaluator(ASEvaluation.forName(str1, arrayOfString));
    } 
    if (this.m_ASEvaluator instanceof weka.attributeSelection.AttributeEvaluator)
      setSearch((ASSearch)new Ranker()); 
    str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0) {
      str = str.trim();
      int i = str.indexOf(' ');
      String str1 = str;
      String str2 = "";
      String[] arrayOfString = null;
      if (i != -1) {
        str1 = str.substring(0, i);
        str2 = str.substring(i).trim();
        arrayOfString = Utils.splitOptions(str2);
      } 
      setSearch(ASSearch.forName(str1, arrayOfString));
    } 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    String[] arrayOfString2 = new String[0];
    byte b = 0;
    if (this.m_ASEvaluator instanceof OptionHandler)
      arrayOfString1 = ((OptionHandler)this.m_ASEvaluator).getOptions(); 
    if (this.m_ASSearch instanceof OptionHandler)
      arrayOfString2 = ((OptionHandler)this.m_ASSearch).getOptions(); 
    String[] arrayOfString3 = new String[10];
    arrayOfString3[b++] = "-E";
    arrayOfString3[b++] = getEvaluator().getClass().getName() + " " + Utils.joinOptions(arrayOfString1);
    arrayOfString3[b++] = "-S";
    arrayOfString3[b++] = getSearch().getClass().getName() + " " + Utils.joinOptions(arrayOfString2);
    while (b < arrayOfString3.length)
      arrayOfString3[b++] = ""; 
    return arrayOfString3;
  }
  
  public String evaluatorTipText() {
    return "Determines how attributes/attribute subsets are evaluated.";
  }
  
  public void setEvaluator(ASEvaluation paramASEvaluation) {
    this.m_ASEvaluator = paramASEvaluation;
  }
  
  public String searchTipText() {
    return "Determines the search method.";
  }
  
  public void setSearch(ASSearch paramASSearch) {
    this.m_ASSearch = paramASSearch;
  }
  
  public ASEvaluation getEvaluator() {
    return this.m_ASEvaluator;
  }
  
  public ASSearch getSearch() {
    return this.m_ASSearch;
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (isOutputFormatDefined()) {
      convertInstance(paramInstance);
      return true;
    } 
    bufferInput(paramInstance);
    return false;
  }
  
  public boolean batchFinished() throws Exception {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (!isOutputFormatDefined()) {
      this.m_trainSelector.setEvaluator(this.m_ASEvaluator);
      this.m_trainSelector.setSearch(this.m_ASSearch);
      this.m_trainSelector.SelectAttributes(getInputFormat());
      this.m_SelectedAttributes = this.m_trainSelector.selectedAttributes();
      if (this.m_SelectedAttributes == null)
        throw new Exception("No selected attributes\n"); 
      setOutputFormat();
      for (byte b = 0; b < getInputFormat().numInstances(); b++)
        convertInstance(getInputFormat().instance(b)); 
      flushInput();
    } 
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  protected void setOutputFormat() throws Exception {
    Instances instances1;
    if (this.m_SelectedAttributes == null) {
      setOutputFormat(null);
      return;
    } 
    FastVector fastVector = new FastVector(this.m_SelectedAttributes.length);
    if (this.m_ASEvaluator instanceof AttributeTransformer) {
      instances1 = ((AttributeTransformer)this.m_ASEvaluator).transformedData();
    } else {
      instances1 = getInputFormat();
    } 
    for (byte b = 0; b < this.m_SelectedAttributes.length; b++)
      fastVector.addElement(instances1.attribute(this.m_SelectedAttributes[b]).copy()); 
    Instances instances2 = new Instances(getInputFormat().relationName(), fastVector, 0);
    if (!(this.m_ASEvaluator instanceof weka.attributeSelection.UnsupervisedSubsetEvaluator) && !(this.m_ASEvaluator instanceof weka.attributeSelection.UnsupervisedAttributeEvaluator))
      instances2.setClassIndex(this.m_SelectedAttributes.length - 1); 
    setOutputFormat(instances2);
  }
  
  protected void convertInstance(Instance paramInstance) throws Exception {
    boolean bool = false;
    double[] arrayOfDouble = new double[getOutputFormat().numAttributes()];
    if (this.m_ASEvaluator instanceof AttributeTransformer) {
      Instance instance = ((AttributeTransformer)this.m_ASEvaluator).convertInstance(paramInstance);
      for (byte b = 0; b < this.m_SelectedAttributes.length; b++) {
        int i = this.m_SelectedAttributes[b];
        arrayOfDouble[b] = instance.value(i);
      } 
    } else {
      for (byte b = 0; b < this.m_SelectedAttributes.length; b++) {
        int i = this.m_SelectedAttributes[b];
        arrayOfDouble[b] = paramInstance.value(i);
      } 
    } 
    if (paramInstance instanceof SparseInstance) {
      push((Instance)new SparseInstance(paramInstance.weight(), arrayOfDouble));
    } else {
      push(new Instance(paramInstance.weight(), arrayOfDouble));
    } 
  }
  
  protected void resetOptions() {
    this.m_trainSelector = new weka.attributeSelection.AttributeSelection();
    setEvaluator((ASEvaluation)new CfsSubsetEval());
    setSearch((ASSearch)new BestFirst());
    this.m_SelectedAttributes = null;
    this.m_FilterOptions = null;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new AttributeSelection(), paramArrayOfString);
      } else {
        Filter.filterFile(new AttributeSelection(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filters\supervised\attribute\AttributeSelection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */