package weka.experiment;

import java.io.ObjectStreamClass;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Summarizable;
import weka.core.Utils;

public class RegressionSplitEvaluator implements SplitEvaluator, OptionHandler, AdditionalMeasureProducer {
  protected Classifier m_Template = (Classifier)new ZeroR();
  
  protected Classifier m_Classifier;
  
  protected String[] m_AdditionalMeasures = null;
  
  protected boolean[] m_doesProduce = null;
  
  protected String m_result = null;
  
  protected String m_ClassifierOptions = "";
  
  protected String m_ClassifierVersion = "";
  
  private static final int KEY_SIZE = 3;
  
  private static final int RESULT_SIZE = 15;
  
  public RegressionSplitEvaluator() {
    updateOptions();
  }
  
  public String globalInfo() {
    return "A SplitEvaluator that produces results for a classification scheme on a numeric class attribute.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tThe full class name of the classifier.\n\teg: weka.classifiers.bayes.NaiveBayes", "W", 1, "-W <class name>"));
    if (this.m_Template != null && this.m_Template instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to classifier " + this.m_Template.getClass().getName() + ":"));
      Enumeration enumeration = this.m_Template.listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('W', paramArrayOfString);
    if (str.length() == 0)
      throw new Exception("A classifier must be specified with the -W option."); 
    setClassifier(Classifier.forName(str, null));
    if (getClassifier() instanceof OptionHandler) {
      getClassifier().setOptions(Utils.partitionOptions(paramArrayOfString));
      updateOptions();
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_Template != null && this.m_Template instanceof OptionHandler)
      arrayOfString1 = this.m_Template.getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 3];
    int i = 0;
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
  
  public void setAdditionalMeasures(String[] paramArrayOfString) {
    this.m_AdditionalMeasures = paramArrayOfString;
    if (this.m_AdditionalMeasures != null && this.m_AdditionalMeasures.length > 0) {
      this.m_doesProduce = new boolean[this.m_AdditionalMeasures.length];
      if (this.m_Template instanceof AdditionalMeasureProducer) {
        Enumeration enumeration = ((AdditionalMeasureProducer)this.m_Template).enumerateMeasures();
        while (enumeration.hasMoreElements()) {
          String str = enumeration.nextElement();
          for (byte b = 0; b < this.m_AdditionalMeasures.length; b++) {
            if (str.compareToIgnoreCase(this.m_AdditionalMeasures[b]) == 0)
              this.m_doesProduce[b] = true; 
          } 
        } 
      } 
    } else {
      this.m_doesProduce = null;
    } 
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector();
    if (this.m_Template instanceof AdditionalMeasureProducer) {
      Enumeration enumeration = ((AdditionalMeasureProducer)this.m_Template).enumerateMeasures();
      while (enumeration.hasMoreElements()) {
        String str = enumeration.nextElement();
        vector.addElement(str);
      } 
    } 
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (this.m_Template instanceof AdditionalMeasureProducer) {
      if (this.m_Classifier == null)
        throw new IllegalArgumentException("ClassifierSplitEvaluator: Can't return result for measure, classifier has not been built yet."); 
      return ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(paramString);
    } 
    throw new IllegalArgumentException("ClassifierSplitEvaluator: Can't return value for : " + paramString + ". " + this.m_Template.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
  }
  
  public Object[] getKeyTypes() {
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = "";
    arrayOfObject[1] = "";
    arrayOfObject[2] = "";
    return arrayOfObject;
  }
  
  public String[] getKeyNames() {
    String[] arrayOfString = new String[3];
    arrayOfString[0] = "Scheme";
    arrayOfString[1] = "Scheme_options";
    arrayOfString[2] = "Scheme_version_ID";
    return arrayOfString;
  }
  
  public Object[] getKey() {
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = this.m_Template.getClass().getName();
    arrayOfObject[1] = this.m_ClassifierOptions;
    arrayOfObject[2] = this.m_ClassifierVersion;
    return arrayOfObject;
  }
  
  public Object[] getResultTypes() {
    byte b1 = (this.m_AdditionalMeasures != null) ? this.m_AdditionalMeasures.length : 0;
    Object[] arrayOfObject = new Object[15 + b1];
    Double double_ = new Double(0.0D);
    byte b2 = 0;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = double_;
    arrayOfObject[b2++] = "";
    for (byte b3 = 0; b3 < b1; b3++)
      arrayOfObject[b2++] = double_; 
    if (b2 != 15 + b1)
      throw new Error("ResultTypes didn't fit RESULT_SIZE"); 
    return arrayOfObject;
  }
  
  public String[] getResultNames() {
    byte b1 = (this.m_AdditionalMeasures != null) ? this.m_AdditionalMeasures.length : 0;
    String[] arrayOfString = new String[15 + b1];
    byte b2 = 0;
    arrayOfString[b2++] = "Number_of_instances";
    arrayOfString[b2++] = "Mean_absolute_error";
    arrayOfString[b2++] = "Root_mean_squared_error";
    arrayOfString[b2++] = "Relative_absolute_error";
    arrayOfString[b2++] = "Root_relative_squared_error";
    arrayOfString[b2++] = "Correlation_coefficient";
    arrayOfString[b2++] = "SF_prior_entropy";
    arrayOfString[b2++] = "SF_scheme_entropy";
    arrayOfString[b2++] = "SF_entropy_gain";
    arrayOfString[b2++] = "SF_mean_prior_entropy";
    arrayOfString[b2++] = "SF_mean_scheme_entropy";
    arrayOfString[b2++] = "SF_mean_entropy_gain";
    arrayOfString[b2++] = "Time_training";
    arrayOfString[b2++] = "Time_testing";
    arrayOfString[b2++] = "Summary";
    for (byte b3 = 0; b3 < b1; b3++)
      arrayOfString[b2++] = this.m_AdditionalMeasures[b3]; 
    if (b2 != 15 + b1)
      throw new Error("ResultNames didn't fit RESULT_SIZE"); 
    return arrayOfString;
  }
  
  public Object[] getResult(Instances paramInstances1, Instances paramInstances2) throws Exception {
    if (paramInstances1.classAttribute().type() != 0)
      throw new Exception("Class attribute is not numeric!"); 
    if (this.m_Template == null)
      throw new Exception("No classifier has been specified"); 
    byte b1 = (this.m_AdditionalMeasures != null) ? this.m_AdditionalMeasures.length : 0;
    Object[] arrayOfObject = new Object[15 + b1];
    Evaluation evaluation = new Evaluation(paramInstances1);
    this.m_Classifier = Classifier.makeCopy(this.m_Template);
    long l1 = System.currentTimeMillis();
    this.m_Classifier.buildClassifier(paramInstances1);
    long l2 = System.currentTimeMillis() - l1;
    long l3 = System.currentTimeMillis();
    evaluation.evaluateModel(this.m_Classifier, paramInstances2);
    long l4 = System.currentTimeMillis() - l3;
    this.m_result = evaluation.toSummaryString();
    byte b2 = 0;
    arrayOfObject[b2++] = new Double(evaluation.numInstances());
    arrayOfObject[b2++] = new Double(evaluation.meanAbsoluteError());
    arrayOfObject[b2++] = new Double(evaluation.rootMeanSquaredError());
    arrayOfObject[b2++] = new Double(evaluation.relativeAbsoluteError());
    arrayOfObject[b2++] = new Double(evaluation.rootRelativeSquaredError());
    arrayOfObject[b2++] = new Double(evaluation.correlationCoefficient());
    arrayOfObject[b2++] = new Double(evaluation.SFPriorEntropy());
    arrayOfObject[b2++] = new Double(evaluation.SFSchemeEntropy());
    arrayOfObject[b2++] = new Double(evaluation.SFEntropyGain());
    arrayOfObject[b2++] = new Double(evaluation.SFMeanPriorEntropy());
    arrayOfObject[b2++] = new Double(evaluation.SFMeanSchemeEntropy());
    arrayOfObject[b2++] = new Double(evaluation.SFMeanEntropyGain());
    arrayOfObject[b2++] = new Double(l2 / 1000.0D);
    arrayOfObject[b2++] = new Double(l4 / 1000.0D);
    if (this.m_Classifier instanceof Summarizable) {
      arrayOfObject[b2++] = ((Summarizable)this.m_Classifier).toSummaryString();
    } else {
      arrayOfObject[b2++] = null;
    } 
    for (byte b3 = 0; b3 < b1; b3++) {
      if (this.m_doesProduce[b3]) {
        try {
          double d = ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(this.m_AdditionalMeasures[b3]);
          if (!Instance.isMissingValue(d)) {
            Double double_ = new Double(d);
            arrayOfObject[b2++] = double_;
          } else {
            arrayOfObject[b2++] = null;
          } 
        } catch (Exception exception) {
          System.err.println(exception);
        } 
      } else {
        arrayOfObject[b2++] = null;
      } 
    } 
    if (b2 != 15 + b1)
      throw new Error("Results didn't fit RESULT_SIZE"); 
    return arrayOfObject;
  }
  
  public String classifierTipText() {
    return "The classifier to use.";
  }
  
  public Classifier getClassifier() {
    return this.m_Template;
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_Template = paramClassifier;
    updateOptions();
    System.err.println("RegressionSplitEvaluator: In set classifier");
  }
  
  protected void updateOptions() {
    if (this.m_Template instanceof OptionHandler) {
      this.m_ClassifierOptions = Utils.joinOptions(this.m_Template.getOptions());
    } else {
      this.m_ClassifierOptions = "";
    } 
    if (this.m_Template instanceof java.io.Serializable) {
      ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(this.m_Template.getClass());
      this.m_ClassifierVersion = "" + objectStreamClass.getSerialVersionUID();
    } else {
      this.m_ClassifierVersion = "";
    } 
  }
  
  public void setClassifierName(String paramString) throws Exception {
    try {
      setClassifier((Classifier)Class.forName(paramString).newInstance());
    } catch (Exception exception) {
      throw new Exception("Can't find Classifier with class name: " + paramString);
    } 
  }
  
  public String getRawResultOutput() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_Classifier == null)
      return "<null> classifier"; 
    stringBuffer.append(toString());
    stringBuffer.append("Classifier model: \n" + this.m_Classifier.toString() + '\n');
    if (this.m_result != null) {
      stringBuffer.append(this.m_result);
      if (this.m_doesProduce != null)
        for (byte b = 0; b < this.m_doesProduce.length; b++) {
          if (this.m_doesProduce[b])
            try {
              double d = ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(this.m_AdditionalMeasures[b]);
              if (!Instance.isMissingValue(d)) {
                Double double_ = new Double(d);
                stringBuffer.append(this.m_AdditionalMeasures[b] + " : " + double_ + '\n');
              } else {
                stringBuffer.append(this.m_AdditionalMeasures[b] + " : " + '?' + '\n');
              } 
            } catch (Exception exception) {
              System.err.println(exception);
            }  
        }  
    } 
    return stringBuffer.toString();
  }
  
  public String toString() {
    String str = "RegressionSplitEvaluator: ";
    return (this.m_Template == null) ? (str + "<null> classifier") : (str + this.m_Template.getClass().getName() + " " + this.m_ClassifierOptions + "(version " + this.m_ClassifierVersion + ")");
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\RegressionSplitEvaluator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */