package weka.attributeSelection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileReader;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class AttributeSelection implements Serializable {
  private Instances m_trainInstances;
  
  private ASEvaluation m_ASEvaluator;
  
  private ASSearch m_searchMethod;
  
  private int m_numFolds;
  
  private StringBuffer m_selectionResults;
  
  private boolean m_doRank;
  
  private boolean m_doXval;
  
  private int m_seed;
  
  private double m_threshold;
  
  private int m_numToSelect;
  
  private int[] m_selectedAttributeSet;
  
  private double[][] m_attributeRanking;
  
  private AttributeTransformer m_transformer = null;
  
  private Remove m_attributeFilter = null;
  
  private double[][] m_rankResults = (double[][])null;
  
  private double[] m_subsetResults = null;
  
  private int m_trials = 0;
  
  public int numberAttributesSelected() throws Exception {
    int[] arrayOfInt = selectedAttributes();
    return arrayOfInt.length - 1;
  }
  
  public int[] selectedAttributes() throws Exception {
    if (this.m_selectedAttributeSet == null)
      throw new Exception("Attribute selection has not been performed yet!"); 
    return this.m_selectedAttributeSet;
  }
  
  public double[][] rankedAttributes() throws Exception {
    if (this.m_attributeRanking == null)
      throw new Exception("Ranking has not been performed"); 
    return this.m_attributeRanking;
  }
  
  public void setEvaluator(ASEvaluation paramASEvaluation) {
    this.m_ASEvaluator = paramASEvaluation;
  }
  
  public void setSearch(ASSearch paramASSearch) {
    this.m_searchMethod = paramASSearch;
    if (this.m_searchMethod instanceof RankedOutputSearch)
      setRanking(((RankedOutputSearch)this.m_searchMethod).getGenerateRanking()); 
  }
  
  public void setFolds(int paramInt) {
    this.m_numFolds = paramInt;
  }
  
  public void setRanking(boolean paramBoolean) {
    this.m_doRank = paramBoolean;
  }
  
  public void setXval(boolean paramBoolean) {
    this.m_doXval = paramBoolean;
  }
  
  public void setSeed(int paramInt) {
    this.m_seed = paramInt;
  }
  
  public void setThreshold(double paramDouble) {
    this.m_threshold = paramDouble;
  }
  
  public String toResultsString() {
    return this.m_selectionResults.toString();
  }
  
  public Instances reduceDimensionality(Instances paramInstances) throws Exception {
    if (this.m_attributeFilter == null)
      throw new Exception("No feature selection has been performed yet!"); 
    if (this.m_transformer != null) {
      Instances instances = new Instances(this.m_transformer.transformedHeader(), paramInstances.numInstances());
      for (byte b = 0; b < paramInstances.numInstances(); b++)
        instances.add(this.m_transformer.convertInstance(paramInstances.instance(b))); 
      return Filter.useFilter(instances, (Filter)this.m_attributeFilter);
    } 
    return Filter.useFilter(paramInstances, (Filter)this.m_attributeFilter);
  }
  
  public Instance reduceDimensionality(Instance paramInstance) throws Exception {
    if (this.m_attributeFilter == null)
      throw new Exception("No feature selection has been performed yet!"); 
    if (this.m_transformer != null)
      paramInstance = this.m_transformer.convertInstance(paramInstance); 
    this.m_attributeFilter.input(paramInstance);
    this.m_attributeFilter.batchFinished();
    return this.m_attributeFilter.output();
  }
  
  public AttributeSelection() {
    setFolds(10);
    setRanking(false);
    setXval(false);
    setSeed(1);
    setEvaluator(new CfsSubsetEval());
    setSearch(new GreedyStepwise());
    this.m_selectionResults = new StringBuffer();
    this.m_selectedAttributeSet = null;
    this.m_attributeRanking = (double[][])null;
  }
  
  public static String SelectAttributes(ASEvaluation paramASEvaluation, String[] paramArrayOfString) throws Exception {
    String str;
    Instances instances = null;
    ASSearch aSSearch = null;
    try {
      str = Utils.getOption('I', paramArrayOfString);
      if (str.length() == 0) {
        String str1 = Utils.getOption('S', paramArrayOfString);
        if (str1.length() != 0)
          aSSearch = (ASSearch)Class.forName(str1).newInstance(); 
        throw new Exception("No training file given.");
      } 
    } catch (Exception exception) {
      throw new Exception('\n' + exception.getMessage() + makeOptionString(paramASEvaluation, aSSearch));
    } 
    instances = new Instances(new FileReader(str));
    return SelectAttributes(paramASEvaluation, paramArrayOfString, instances);
  }
  
  public String CVResultsString() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    if ((this.m_subsetResults == null && this.m_rankResults == null) || this.m_trainInstances == null)
      throw new Exception("Attribute selection has not been performed yet!"); 
    int i = (int)(Math.log(this.m_trainInstances.numAttributes()) + 1.0D);
    stringBuffer.append("\n\n=== Attribute selection " + this.m_numFolds + " fold cross-validation ");
    if (!(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator) && !(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator) && this.m_trainInstances.classAttribute().isNominal()) {
      stringBuffer.append("(stratified), seed: ");
      stringBuffer.append(this.m_seed + " ===\n\n");
    } else {
      stringBuffer.append("seed: " + this.m_seed + " ===\n\n");
    } 
    if (this.m_searchMethod instanceof RankedOutputSearch && this.m_doRank == true) {
      stringBuffer.append("average merit      average rank  attribute\n");
      for (byte b1 = 0; b1 < (this.m_rankResults[0]).length; b1++) {
        this.m_rankResults[0][b1] = this.m_rankResults[0][b1] / this.m_numFolds;
        double d = this.m_rankResults[0][b1] * this.m_rankResults[0][b1] * this.m_numFolds;
        d = this.m_rankResults[2][b1] - d;
        d /= this.m_numFolds;
        if (d <= 0.0D) {
          d = 0.0D;
          this.m_rankResults[2][b1] = 0.0D;
        } else {
          this.m_rankResults[2][b1] = Math.sqrt(d);
        } 
        this.m_rankResults[1][b1] = this.m_rankResults[1][b1] / this.m_numFolds;
        d = this.m_rankResults[1][b1] * this.m_rankResults[1][b1] * this.m_numFolds;
        d = this.m_rankResults[3][b1] - d;
        d /= this.m_numFolds;
        if (d <= 0.0D) {
          d = 0.0D;
          this.m_rankResults[3][b1] = 0.0D;
        } else {
          this.m_rankResults[3][b1] = Math.sqrt(d);
        } 
      } 
      int[] arrayOfInt = Utils.sort(this.m_rankResults[1]);
      for (byte b2 = 0; b2 < arrayOfInt.length; b2++) {
        if (this.m_rankResults[1][arrayOfInt[b2]] > 0.0D)
          stringBuffer.append(Utils.doubleToString(Math.abs(this.m_rankResults[0][arrayOfInt[b2]]), 6, 3) + " +-" + Utils.doubleToString(this.m_rankResults[2][arrayOfInt[b2]], 6, 3) + "   " + Utils.doubleToString(this.m_rankResults[1][arrayOfInt[b2]], i + 2, 1) + " +-" + Utils.doubleToString(this.m_rankResults[3][arrayOfInt[b2]], 5, 2) + "  " + Utils.doubleToString((arrayOfInt[b2] + 1), i, 0) + " " + this.m_trainInstances.attribute(arrayOfInt[b2]).name() + "\n"); 
      } 
    } else {
      stringBuffer.append("number of folds (%)  attribute\n");
      for (byte b = 0; b < this.m_subsetResults.length; b++) {
        if (this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator || b != this.m_trainInstances.classIndex())
          stringBuffer.append(Utils.doubleToString(this.m_subsetResults[b], 12, 0) + "(" + Utils.doubleToString(this.m_subsetResults[b] / this.m_numFolds * 100.0D, 3, 0) + " %)  " + Utils.doubleToString((b + 1), i, 0) + " " + this.m_trainInstances.attribute(b).name() + "\n"); 
      } 
    } 
    return stringBuffer.toString();
  }
  
  public void selectAttributesCVSplit(Instances paramInstances) throws Exception {
    double[][] arrayOfDouble = (double[][])null;
    if (this.m_trainInstances == null)
      this.m_trainInstances = paramInstances; 
    if (this.m_rankResults == null && this.m_subsetResults == null) {
      this.m_subsetResults = new double[paramInstances.numAttributes()];
      this.m_rankResults = new double[4][paramInstances.numAttributes()];
    } 
    this.m_ASEvaluator.buildEvaluator(paramInstances);
    int[] arrayOfInt = this.m_searchMethod.search(this.m_ASEvaluator, paramInstances);
    arrayOfInt = this.m_ASEvaluator.postProcess(arrayOfInt);
    if (this.m_searchMethod instanceof RankedOutputSearch && this.m_doRank == true) {
      arrayOfDouble = ((RankedOutputSearch)this.m_searchMethod).rankedAttributes();
      for (byte b = 0; b < arrayOfDouble.length; b++) {
        this.m_rankResults[0][(int)arrayOfDouble[b][0]] = this.m_rankResults[0][(int)arrayOfDouble[b][0]] + arrayOfDouble[b][1];
        this.m_rankResults[2][(int)arrayOfDouble[b][0]] = this.m_rankResults[2][(int)arrayOfDouble[b][0]] + arrayOfDouble[b][1] * arrayOfDouble[b][1];
        this.m_rankResults[1][(int)arrayOfDouble[b][0]] = this.m_rankResults[1][(int)arrayOfDouble[b][0]] + (b + 1);
        this.m_rankResults[3][(int)arrayOfDouble[b][0]] = this.m_rankResults[3][(int)arrayOfDouble[b][0]] + ((b + 1) * (b + 1));
      } 
    } else {
      for (byte b = 0; b < arrayOfInt.length; b++)
        this.m_subsetResults[arrayOfInt[b]] = this.m_subsetResults[arrayOfInt[b]] + 1.0D; 
    } 
    this.m_trials++;
  }
  
  public String CrossValidateAttributes() throws Exception {
    Instances instances = new Instances(this.m_trainInstances);
    double[][] arrayOfDouble = (double[][])null;
    Random random = new Random(this.m_seed);
    instances.randomize(random);
    if (!(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator) && !(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator) && instances.classAttribute().isNominal())
      instances.stratify(this.m_numFolds); 
    for (byte b = 0; b < this.m_numFolds; b++) {
      Instances instances1 = instances.trainCV(this.m_numFolds, b, random);
      selectAttributesCVSplit(instances1);
    } 
    return CVResultsString();
  }
  
  public void SelectAttributes(Instances paramInstances) throws Exception {
    this.m_transformer = null;
    this.m_attributeFilter = null;
    this.m_trainInstances = paramInstances;
    if (this.m_doXval == true && this.m_ASEvaluator instanceof AttributeTransformer)
      throw new Exception("Can't cross validate an attribute transformer."); 
    if (this.m_ASEvaluator instanceof SubsetEvaluator && this.m_searchMethod instanceof Ranker)
      throw new Exception(this.m_ASEvaluator.getClass().getName() + " must use a search method other than Ranker"); 
    if (this.m_ASEvaluator instanceof AttributeEvaluator && !(this.m_searchMethod instanceof Ranker))
      throw new Exception("AttributeEvaluators must use the Ranker search method"); 
    if (this.m_searchMethod instanceof RankedOutputSearch)
      this.m_doRank = ((RankedOutputSearch)this.m_searchMethod).getGenerateRanking(); 
    if (!(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator) && !(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator) && this.m_trainInstances.classIndex() < 0)
      this.m_trainInstances.setClassIndex(this.m_trainInstances.numAttributes() - 1); 
    this.m_ASEvaluator.buildEvaluator(this.m_trainInstances);
    if (this.m_ASEvaluator instanceof AttributeTransformer) {
      this.m_trainInstances = ((AttributeTransformer)this.m_ASEvaluator).transformedHeader();
      this.m_transformer = (AttributeTransformer)this.m_ASEvaluator;
    } 
    int i = (int)(Math.log(this.m_trainInstances.numAttributes()) + 1.0D);
    int[] arrayOfInt = this.m_searchMethod.search(this.m_ASEvaluator, this.m_trainInstances);
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(this.m_searchMethod.getClass());
      PropertyDescriptor[] arrayOfPropertyDescriptor = beanInfo.getPropertyDescriptors();
      for (byte b = 0; b < arrayOfPropertyDescriptor.length; b++) {
        String str = arrayOfPropertyDescriptor[b].getDisplayName();
        Method method = arrayOfPropertyDescriptor[b].getReadMethod();
        Class clazz = method.getReturnType();
        if (clazz.equals(ASEvaluation.class)) {
          Class[] arrayOfClass = new Class[0];
          ASEvaluation aSEvaluation = (ASEvaluation)method.invoke(this.m_searchMethod, (Object[])arrayOfClass);
          if (aSEvaluation instanceof AttributeTransformer) {
            this.m_trainInstances = ((AttributeTransformer)aSEvaluation).transformedHeader();
            this.m_transformer = (AttributeTransformer)aSEvaluation;
          } 
        } 
      } 
    } catch (IntrospectionException introspectionException) {
      System.err.println("AttributeSelection: Couldn't introspect");
    } 
    arrayOfInt = this.m_ASEvaluator.postProcess(arrayOfInt);
    if (!this.m_doRank)
      this.m_selectionResults.append(printSelectionResults()); 
    if (this.m_searchMethod instanceof RankedOutputSearch && this.m_doRank == true) {
      this.m_attributeRanking = ((RankedOutputSearch)this.m_searchMethod).rankedAttributes();
      this.m_selectionResults.append(printSelectionResults());
      this.m_selectionResults.append("Ranked attributes:\n");
      this.m_numToSelect = ((RankedOutputSearch)this.m_searchMethod).getCalculatedNumToSelect();
      int j = 0;
      int k = 0;
      byte b;
      for (b = 0; b < this.m_numToSelect; b++) {
        double d = Math.abs(this.m_attributeRanking[b][1]) - (int)Math.abs(this.m_attributeRanking[b][1]);
        if (d > 0.0D)
          d = Math.abs(Math.log(Math.abs(d)) / Math.log(10.0D)) + 3.0D; 
        if (d > j)
          j = (int)d; 
        if (Math.abs(Math.log(Math.abs(this.m_attributeRanking[b][1])) / Math.log(10.0D)) + 1.0D > k && this.m_attributeRanking[b][1] > 0.0D)
          k = (int)Math.abs(Math.log(Math.abs(this.m_attributeRanking[b][1])) / Math.log(10.0D)) + 1; 
      } 
      for (b = 0; b < this.m_numToSelect; b++)
        this.m_selectionResults.append(Utils.doubleToString(this.m_attributeRanking[b][1], j + k + 1, j) + Utils.doubleToString(this.m_attributeRanking[b][0] + 1.0D, i + 1, 0) + " " + this.m_trainInstances.attribute((int)this.m_attributeRanking[b][0]).name() + "\n"); 
      if ((!(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator) && !(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator)) || this.m_trainInstances.classIndex() >= 0) {
        this.m_selectedAttributeSet = new int[this.m_numToSelect + 1];
        this.m_selectedAttributeSet[this.m_numToSelect] = this.m_trainInstances.classIndex();
      } else {
        this.m_selectedAttributeSet = new int[this.m_numToSelect];
      } 
      this.m_selectionResults.append("\nSelected attributes: ");
      for (b = 0; b < this.m_numToSelect; b++) {
        this.m_selectedAttributeSet[b] = (int)this.m_attributeRanking[b][0];
        if (b == this.m_numToSelect - 1) {
          this.m_selectionResults.append(((int)this.m_attributeRanking[b][0] + 1) + " : " + (b + 1) + "\n");
        } else {
          this.m_selectionResults.append((int)this.m_attributeRanking[b][0] + 1);
          this.m_selectionResults.append(",");
        } 
      } 
    } else {
      if ((!(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator) && !(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator)) || this.m_trainInstances.classIndex() >= 0) {
        this.m_selectedAttributeSet = new int[arrayOfInt.length + 1];
        this.m_selectedAttributeSet[arrayOfInt.length] = this.m_trainInstances.classIndex();
      } else {
        this.m_selectedAttributeSet = new int[arrayOfInt.length];
      } 
      byte b;
      for (b = 0; b < arrayOfInt.length; b++)
        this.m_selectedAttributeSet[b] = arrayOfInt[b]; 
      this.m_selectionResults.append("Selected attributes: ");
      for (b = 0; b < arrayOfInt.length; b++) {
        if (b == arrayOfInt.length - 1) {
          this.m_selectionResults.append((arrayOfInt[b] + 1) + " : " + arrayOfInt.length + "\n");
        } else {
          this.m_selectionResults.append((arrayOfInt[b] + 1) + ",");
        } 
      } 
      for (b = 0; b < arrayOfInt.length; b++)
        this.m_selectionResults.append("                     " + this.m_trainInstances.attribute(arrayOfInt[b]).name() + "\n"); 
    } 
    if (this.m_doXval == true)
      this.m_selectionResults.append(CrossValidateAttributes()); 
    if (this.m_selectedAttributeSet != null && !this.m_doXval) {
      this.m_attributeFilter = new Remove();
      this.m_attributeFilter.setAttributeIndicesArray(this.m_selectedAttributeSet);
      this.m_attributeFilter.setInvertSelection(true);
      this.m_attributeFilter.setInputFormat(this.m_trainInstances);
    } 
    this.m_trainInstances = new Instances(this.m_trainInstances, 0);
  }
  
  public static String SelectAttributes(ASEvaluation paramASEvaluation, String[] paramArrayOfString, Instances paramInstances) throws Exception {
    int i = 1;
    int j = 10;
    String[] arrayOfString = null;
    ASSearch aSSearch = null;
    boolean bool1 = false;
    int k = -1;
    double d = -1.7976931348623157E308D;
    boolean bool2 = false;
    StringBuffer stringBuffer = new StringBuffer();
    Range range = new Range();
    AttributeSelection attributeSelection = new AttributeSelection();
    try {
      String str5;
      if (Utils.getFlag('h', paramArrayOfString))
        bool2 = true; 
      String str4 = Utils.getOption('C', paramArrayOfString);
      if (str4.length() != 0)
        if (str4.equals("first")) {
          k = 1;
        } else if (str4.equals("last")) {
          k = paramInstances.numAttributes();
        } else {
          k = Integer.parseInt(str4);
        }  
      if (k != -1 && (k == 0 || k > paramInstances.numAttributes()))
        throw new Exception("Class index out of range."); 
      if (k != -1)
        paramInstances.setClassIndex(k - 1); 
      String str1 = Utils.getOption('X', paramArrayOfString);
      if (str1.length() != 0) {
        j = Integer.parseInt(str1);
        bool1 = true;
      } 
      attributeSelection.setFolds(j);
      attributeSelection.setXval(bool1);
      String str2 = Utils.getOption('N', paramArrayOfString);
      if (str2.length() != 0)
        i = Integer.parseInt(str2); 
      attributeSelection.setSeed(i);
      String str3 = Utils.getOption('S', paramArrayOfString);
      if (str3.length() == 0 && !(paramASEvaluation instanceof AttributeEvaluator))
        throw new Exception("No search method given."); 
      if (str3.length() != 0) {
        str3 = str3.trim();
        int m = str3.indexOf(' ');
        str5 = str3;
        String str = "";
        if (m != -1) {
          str5 = str3.substring(0, m);
          str = str3.substring(m).trim();
          arrayOfString = Utils.splitOptions(str);
        } 
      } else {
        try {
          str5 = new String("weka.attributeSelection.Ranker");
          aSSearch = (ASSearch)Class.forName(str5).newInstance();
        } catch (Exception exception) {
          throw new Exception("Can't create Ranker object");
        } 
      } 
      if (aSSearch == null)
        aSSearch = ASSearch.forName(str5, arrayOfString); 
      attributeSelection.setSearch(aSSearch);
    } catch (Exception exception) {
      throw new Exception('\n' + exception.getMessage() + makeOptionString(paramASEvaluation, aSSearch));
    } 
    try {
      if (paramASEvaluation instanceof OptionHandler)
        ((OptionHandler)paramASEvaluation).setOptions(paramArrayOfString); 
    } catch (Exception exception) {
      throw new Exception("\n" + exception.getMessage() + makeOptionString(paramASEvaluation, aSSearch));
    } 
    try {
      Utils.checkForRemainingOptions(paramArrayOfString);
    } catch (Exception exception) {
      throw new Exception('\n' + exception.getMessage() + makeOptionString(paramASEvaluation, aSSearch));
    } 
    if (bool2) {
      System.out.println(makeOptionString(paramASEvaluation, aSSearch));
      System.exit(0);
    } 
    attributeSelection.setEvaluator(paramASEvaluation);
    attributeSelection.SelectAttributes(paramInstances);
    return attributeSelection.toResultsString();
  }
  
  private String printSelectionResults() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\n\n=== Attribute Selection on all input data ===\n\nSearch Method:\n");
    stringBuffer.append(this.m_searchMethod.toString());
    stringBuffer.append("\nAttribute ");
    if (this.m_ASEvaluator instanceof SubsetEvaluator) {
      stringBuffer.append("Subset Evaluator (");
    } else {
      stringBuffer.append("Evaluator (");
    } 
    if (!(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator) && !(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator)) {
      stringBuffer.append("supervised, ");
      stringBuffer.append("Class (");
      if (this.m_trainInstances.attribute(this.m_trainInstances.classIndex()).isNumeric()) {
        stringBuffer.append("numeric): ");
      } else {
        stringBuffer.append("nominal): ");
      } 
      stringBuffer.append((this.m_trainInstances.classIndex() + 1) + " " + this.m_trainInstances.attribute(this.m_trainInstances.classIndex()).name() + "):\n");
    } else {
      stringBuffer.append("unsupervised):\n");
    } 
    stringBuffer.append(this.m_ASEvaluator.toString() + "\n");
    return stringBuffer.toString();
  }
  
  private static String makeOptionString(ASEvaluation paramASEvaluation, ASSearch paramASSearch) throws Exception {
    StringBuffer stringBuffer = new StringBuffer("");
    stringBuffer.append("\n\nGeneral options:\n\n");
    stringBuffer.append("-h display this help\n");
    stringBuffer.append("-I <name of input file>\n");
    stringBuffer.append("\tSets training file.\n");
    stringBuffer.append("-C <class index>\n");
    stringBuffer.append("\tSets the class index for supervised attribute\n");
    stringBuffer.append("\tselection. Default=last column.\n");
    stringBuffer.append("-S <Class name>\n");
    stringBuffer.append("\tSets search method for subset evaluators.\n");
    stringBuffer.append("-X <number of folds>\n");
    stringBuffer.append("\tPerform a cross validation.\n");
    stringBuffer.append("-N <random number seed>\n");
    stringBuffer.append("\tUse in conjunction with -X.\n");
    if (paramASEvaluation instanceof OptionHandler) {
      stringBuffer.append("\nOptions specific to " + paramASEvaluation.getClass().getName() + ":\n\n");
      Enumeration enumeration = ((OptionHandler)paramASEvaluation).listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        stringBuffer.append(option.synopsis() + '\n');
        stringBuffer.append(option.description() + "\n");
      } 
    } 
    if (paramASSearch != null) {
      if (paramASSearch instanceof OptionHandler) {
        stringBuffer.append("\nOptions specific to " + paramASSearch.getClass().getName() + ":\n\n");
        Enumeration enumeration = ((OptionHandler)paramASSearch).listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          stringBuffer.append(option.synopsis() + '\n');
          stringBuffer.append(option.description() + "\n");
        } 
      } 
    } else if (paramASEvaluation instanceof SubsetEvaluator) {
      System.out.println("No search method given.");
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0)
        throw new Exception("The first argument must be the name of an attribute/subset evaluator"); 
      String str = paramArrayOfString[0];
      paramArrayOfString[0] = "";
      ASEvaluation aSEvaluation = ASEvaluation.forName(str, null);
      System.out.println(SelectAttributes(aSEvaluation, paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\AttributeSelection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */