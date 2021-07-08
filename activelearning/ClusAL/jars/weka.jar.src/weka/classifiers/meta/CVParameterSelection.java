package weka.classifiers.meta;

import java.io.Serializable;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomizableSingleClassifierEnhancer;
import weka.core.Drawable;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Summarizable;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;

public class CVParameterSelection extends RandomizableSingleClassifierEnhancer implements Drawable, Summarizable {
  protected String[] m_ClassifierOptions;
  
  protected String[] m_BestClassifierOptions;
  
  protected String[] m_InitOptions;
  
  protected double m_BestPerformance;
  
  protected FastVector m_CVParams = new FastVector();
  
  protected int m_NumAttributes;
  
  protected int m_TrainFoldSize;
  
  protected int m_NumFolds = 10;
  
  protected String[] createOptions() {
    String[] arrayOfString = new String[this.m_ClassifierOptions.length + 2 * this.m_CVParams.size()];
    byte b1 = 0;
    int i = arrayOfString.length;
    for (byte b2 = 0; b2 < this.m_CVParams.size(); b2++) {
      CVParameter cVParameter = (CVParameter)this.m_CVParams.elementAt(b2);
      double d = cVParameter.m_ParamValue;
      if (cVParameter.m_RoundParam)
        d = (int)(d + 0.5D); 
      if (cVParameter.m_AddAtEnd) {
        arrayOfString[--i] = "" + Utils.doubleToString(d, 4);
        arrayOfString[--i] = "-" + cVParameter.m_ParamChar;
      } else {
        arrayOfString[b1++] = "-" + cVParameter.m_ParamChar;
        arrayOfString[b1++] = "" + Utils.doubleToString(d, 4);
      } 
    } 
    System.arraycopy(this.m_ClassifierOptions, 0, arrayOfString, b1, this.m_ClassifierOptions.length);
    return arrayOfString;
  }
  
  protected void findParamsByCrossValidation(int paramInt, Instances paramInstances, Random paramRandom) throws Exception {
    if (paramInt < this.m_CVParams.size()) {
      double d1;
      CVParameter cVParameter = (CVParameter)this.m_CVParams.elementAt(paramInt);
      switch ((int)(cVParameter.m_Lower - cVParameter.m_Upper + 0.5D)) {
        case 1:
          d1 = this.m_NumAttributes;
          break;
        case 2:
          d1 = this.m_TrainFoldSize;
          break;
        default:
          d1 = cVParameter.m_Upper;
          break;
      } 
      double d2 = (d1 - cVParameter.m_Lower) / (cVParameter.m_Steps - 1.0D);
      cVParameter.m_ParamValue = cVParameter.m_Lower;
      while (cVParameter.m_ParamValue <= d1) {
        findParamsByCrossValidation(paramInt + 1, paramInstances, paramRandom);
        cVParameter.m_ParamValue += d2;
      } 
    } else {
      Evaluation evaluation = new Evaluation(paramInstances);
      String[] arrayOfString = createOptions();
      if (this.m_Debug) {
        System.err.print("Setting options for " + this.m_Classifier.getClass().getName() + ":");
        for (byte b1 = 0; b1 < arrayOfString.length; b1++)
          System.err.print(" " + arrayOfString[b1]); 
        System.err.println("");
      } 
      this.m_Classifier.setOptions(arrayOfString);
      for (byte b = 0; b < this.m_NumFolds; b++) {
        Instances instances1 = paramInstances.trainCV(this.m_NumFolds, b, new Random(1L));
        Instances instances2 = paramInstances.testCV(this.m_NumFolds, b);
        this.m_Classifier.buildClassifier(instances1);
        evaluation.setPriors(instances1);
        evaluation.evaluateModel(this.m_Classifier, instances2);
      } 
      double d = evaluation.errorRate();
      if (this.m_Debug)
        System.err.println("Cross-validated error rate: " + Utils.doubleToString(d, 6, 4)); 
      if (this.m_BestPerformance == -99.0D || d < this.m_BestPerformance) {
        this.m_BestPerformance = d;
        this.m_BestClassifierOptions = createOptions();
      } 
    } 
  }
  
  public String globalInfo() {
    return "Class for performing parameter selection by cross-validation for any classifier. For more information, see:\nR. Kohavi (1995). Wrappers for Performance Enhancement and Oblivious Decision Graphs. PhD Thesis. Department of Computer Science, Stanford University.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tNumber of folds used for cross validation (default 10).", "X", 1, "-X <number of folds>"));
    vector.addElement(new Option("\tClassifier parameter options.\n\teg: \"N 1 5 10\" Sets an optimisation parameter for the\n\tclassifier with name -N, with lower bound 1, upper bound\n\t5, and 10 optimisation steps. The upper bound may be the\n\tcharacter 'A' or 'I' to substitute the number of\n\tattributes or instances in the training data,\n\trespectively. This parameter may be supplied more than\n\tonce to optimise over several classifier options\n\tsimultaneously.", "P", 1, "-P <classifier parameter>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('X', paramArrayOfString);
    if (str.length() != 0) {
      setNumFolds(Integer.parseInt(str));
    } else {
      setNumFolds(10);
    } 
    this.m_CVParams = new FastVector();
    while (true) {
      String str1 = Utils.getOption('P', paramArrayOfString);
      if (str1.length() != 0)
        addCVParameter(str1); 
      if (str1.length() == 0) {
        super.setOptions(paramArrayOfString);
        return;
      } 
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString1;
    if (this.m_InitOptions != null) {
      try {
        this.m_Classifier.setOptions((String[])this.m_InitOptions.clone());
        arrayOfString1 = super.getOptions();
        this.m_Classifier.setOptions((String[])this.m_BestClassifierOptions.clone());
      } catch (Exception exception) {
        throw new RuntimeException("CVParameterSelection: could not set options in getOptions().");
      } 
    } else {
      arrayOfString1 = super.getOptions();
    } 
    String[] arrayOfString2 = new String[arrayOfString1.length + this.m_CVParams.size() * 2 + 2];
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_CVParams.size(); b2++) {
      arrayOfString2[b1++] = "-P";
      arrayOfString2[b1++] = "" + getCVParameter(b2);
    } 
    arrayOfString2[b1++] = "-X";
    arrayOfString2[b1++] = "" + getNumFolds();
    System.arraycopy(arrayOfString1, 0, arrayOfString2, b1, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    Instances instances = new Instances(paramInstances);
    instances.deleteWithMissingClass();
    if (instances.numInstances() == 0)
      throw new IllegalArgumentException("No training instances without missing class."); 
    if (instances.numInstances() < this.m_NumFolds)
      throw new IllegalArgumentException("Number of training instances smaller than number of folds."); 
    if (!(this.m_Classifier instanceof weka.core.OptionHandler))
      throw new IllegalArgumentException("Base classifier should be OptionHandler."); 
    this.m_InitOptions = this.m_Classifier.getOptions();
    this.m_BestPerformance = -99.0D;
    this.m_NumAttributes = instances.numAttributes();
    Random random = new Random(this.m_Seed);
    instances.randomize(random);
    this.m_TrainFoldSize = instances.trainCV(this.m_NumFolds, 0).numInstances();
    if (this.m_CVParams.size() == 0) {
      this.m_Classifier.buildClassifier(instances);
      this.m_BestClassifierOptions = this.m_InitOptions;
      return;
    } 
    if (instances.classAttribute().isNominal())
      instances.stratify(this.m_NumFolds); 
    this.m_BestClassifierOptions = null;
    this.m_ClassifierOptions = this.m_Classifier.getOptions();
    for (byte b = 0; b < this.m_CVParams.size(); b++)
      Utils.getOption(((CVParameter)this.m_CVParams.elementAt(b)).m_ParamChar, this.m_ClassifierOptions); 
    findParamsByCrossValidation(0, instances, random);
    String[] arrayOfString = (String[])this.m_BestClassifierOptions.clone();
    this.m_Classifier.setOptions(arrayOfString);
    this.m_Classifier.buildClassifier(instances);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    return this.m_Classifier.distributionForInstance(paramInstance);
  }
  
  public void addCVParameter(String paramString) throws Exception {
    CVParameter cVParameter = new CVParameter(this, paramString);
    this.m_CVParams.addElement(cVParameter);
  }
  
  public String getCVParameter(int paramInt) {
    return (this.m_CVParams.size() <= paramInt) ? "" : ((CVParameter)this.m_CVParams.elementAt(paramInt)).toString();
  }
  
  public String CVParametersTipText() {
    return "Sets the scheme parameters which are to be set by cross-validation.\nThe format for each string should be:\nparam_char lower_bound upper_bound increment\neg to search a parameter -P from 1 to 10 by increments of 2:\n    \"P 1 10 2\" ";
  }
  
  public Object[] getCVParameters() {
    Object[] arrayOfObject = this.m_CVParams.toArray();
    String[] arrayOfString = new String[arrayOfObject.length];
    for (byte b = 0; b < arrayOfObject.length; b++)
      arrayOfString[b] = arrayOfObject[b].toString(); 
    return (Object[])arrayOfString;
  }
  
  public void setCVParameters(Object[] paramArrayOfObject) throws Exception {
    FastVector fastVector = this.m_CVParams;
    this.m_CVParams = new FastVector();
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      try {
        addCVParameter((String)paramArrayOfObject[b]);
      } catch (Exception exception) {
        this.m_CVParams = fastVector;
        throw exception;
      } 
    } 
  }
  
  public String numFoldsTipText() {
    return "Get the number of folds used for cross-validation.";
  }
  
  public int getNumFolds() {
    return this.m_NumFolds;
  }
  
  public void setNumFolds(int paramInt) throws Exception {
    if (paramInt < 0)
      throw new IllegalArgumentException("Stacking: Number of cross-validation folds must be positive."); 
    this.m_NumFolds = paramInt;
  }
  
  public int graphType() {
    return (this.m_Classifier instanceof Drawable) ? ((Drawable)this.m_Classifier).graphType() : 0;
  }
  
  public String graph() throws Exception {
    if (this.m_Classifier instanceof Drawable)
      return ((Drawable)this.m_Classifier).graph(); 
    throw new Exception("Classifier: " + this.m_Classifier.getClass().getName() + " " + Utils.joinOptions(this.m_BestClassifierOptions) + " cannot be graphed");
  }
  
  public String toString() {
    if (this.m_InitOptions == null)
      return "CVParameterSelection: No model built yet."; 
    null = "Cross-validated Parameter selection.\nClassifier: " + this.m_Classifier.getClass().getName() + "\n";
    try {
      for (byte b = 0; b < this.m_CVParams.size(); b++) {
        CVParameter cVParameter = (CVParameter)this.m_CVParams.elementAt(b);
        null = null + "Cross-validation Parameter: '-" + cVParameter.m_ParamChar + "'" + " ranged from " + cVParameter.m_Lower + " to ";
        switch ((int)(cVParameter.m_Lower - cVParameter.m_Upper + 0.5D)) {
          case 1:
            null = null + this.m_NumAttributes;
            break;
          case 2:
            null = null + this.m_TrainFoldSize;
            break;
          default:
            null = null + cVParameter.m_Upper;
            break;
        } 
        null = null + " with " + cVParameter.m_Steps + " steps\n";
      } 
    } catch (Exception exception) {
      null = null + exception.getMessage();
    } 
    return null + "Classifier Options: " + Utils.joinOptions(this.m_BestClassifierOptions) + "\n\n" + this.m_Classifier.toString();
  }
  
  public String toSummaryString() {
    String str = "Selected values: " + Utils.joinOptions(this.m_BestClassifierOptions);
    return str + '\n';
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new CVParameterSelection(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
  
  protected class CVParameter implements Serializable {
    private char m_ParamChar;
    
    private double m_Lower;
    
    private double m_Upper;
    
    private double m_Steps;
    
    private double m_ParamValue;
    
    private boolean m_AddAtEnd;
    
    private boolean m_RoundParam;
    
    private final CVParameterSelection this$0;
    
    public CVParameter(CVParameterSelection this$0, String param1String) throws Exception {
      this.this$0 = this$0;
      StreamTokenizer streamTokenizer = new StreamTokenizer(new StringReader(param1String));
      if (streamTokenizer.nextToken() != -3)
        throw new Exception("CVParameter " + param1String + ": Character parameter identifier expected"); 
      this.m_ParamChar = streamTokenizer.sval.charAt(0);
      if (streamTokenizer.nextToken() != -2)
        throw new Exception("CVParameter " + param1String + ": Numeric lower bound expected"); 
      this.m_Lower = streamTokenizer.nval;
      if (streamTokenizer.nextToken() == -2) {
        this.m_Upper = streamTokenizer.nval;
        if (this.m_Upper < this.m_Lower)
          throw new Exception("CVParameter " + param1String + ": Upper bound is less than lower bound"); 
      } else if (streamTokenizer.ttype == -3) {
        if (streamTokenizer.sval.toUpperCase().charAt(0) == 'A') {
          this.m_Upper = this.m_Lower - 1.0D;
        } else if (streamTokenizer.sval.toUpperCase().charAt(0) == 'I') {
          this.m_Upper = this.m_Lower - 2.0D;
        } else {
          throw new Exception("CVParameter " + param1String + ": Upper bound must be numeric, or 'A' or 'N'");
        } 
      } else {
        throw new Exception("CVParameter " + param1String + ": Upper bound must be numeric, or 'A' or 'N'");
      } 
      if (streamTokenizer.nextToken() != -2)
        throw new Exception("CVParameter " + param1String + ": Numeric number of steps expected"); 
      this.m_Steps = streamTokenizer.nval;
      if (streamTokenizer.nextToken() == -3 && streamTokenizer.sval.toUpperCase().charAt(0) == 'R')
        this.m_RoundParam = true; 
    }
    
    public String toString() {
      String str = this.m_ParamChar + " " + this.m_Lower + " ";
      switch ((int)(this.m_Lower - this.m_Upper + 0.5D)) {
        case 1:
          str = str + "A";
          break;
        case 2:
          str = str + "I";
          break;
        default:
          str = str + this.m_Upper;
          break;
      } 
      str = str + " " + this.m_Steps;
      if (this.m_RoundParam)
        str = str + " R"; 
      return str;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\CVParameterSelection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */