package weka.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Summarizable;
import weka.core.Utils;

public class CostSensitiveClassifierSplitEvaluator extends ClassifierSplitEvaluator {
  protected File m_OnDemandDirectory = new File(System.getProperty("user.dir"));
  
  private static final int RESULT_SIZE = 23;
  
  public String globalInfo() {
    return " SplitEvaluator that produces results for a classification scheme on a nominal class attribute, including weighted misclassification costs.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    vector.addElement(new Option("\tName of a directory to search for cost files when loading\n\tcosts on demand (default current directory).", "D", 1, "-D <directory>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('D', paramArrayOfString);
    if (str.length() != 0)
      setOnDemandDirectory(new File(str)); 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 3];
    int i = 0;
    arrayOfString2[i++] = "-D";
    arrayOfString2[i++] = "" + getOnDemandDirectory();
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public String onDemandDirectoryTipText() {
    return "The directory to look in for cost files. This directory will be searched for cost files when loading on demand.";
  }
  
  public File getOnDemandDirectory() {
    return this.m_OnDemandDirectory;
  }
  
  public void setOnDemandDirectory(File paramFile) {
    if (paramFile.isDirectory()) {
      this.m_OnDemandDirectory = paramFile;
    } else {
      this.m_OnDemandDirectory = new File(paramFile.getParent());
    } 
  }
  
  public Object[] getResultTypes() {
    byte b1 = (this.m_AdditionalMeasures != null) ? this.m_AdditionalMeasures.length : 0;
    Object[] arrayOfObject = new Object[23 + b1];
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
    if (b2 != 23 + b1)
      throw new Error("ResultTypes didn't fit RESULT_SIZE"); 
    return arrayOfObject;
  }
  
  public String[] getResultNames() {
    byte b1 = (this.m_AdditionalMeasures != null) ? this.m_AdditionalMeasures.length : 0;
    String[] arrayOfString = new String[23 + b1];
    byte b2 = 0;
    arrayOfString[b2++] = "Number_of_instances";
    arrayOfString[b2++] = "Number_correct";
    arrayOfString[b2++] = "Number_incorrect";
    arrayOfString[b2++] = "Number_unclassified";
    arrayOfString[b2++] = "Percent_correct";
    arrayOfString[b2++] = "Percent_incorrect";
    arrayOfString[b2++] = "Percent_unclassified";
    arrayOfString[b2++] = "Total_cost";
    arrayOfString[b2++] = "Average_cost";
    arrayOfString[b2++] = "Mean_absolute_error";
    arrayOfString[b2++] = "Root_mean_squared_error";
    arrayOfString[b2++] = "Relative_absolute_error";
    arrayOfString[b2++] = "Root_relative_squared_error";
    arrayOfString[b2++] = "SF_prior_entropy";
    arrayOfString[b2++] = "SF_scheme_entropy";
    arrayOfString[b2++] = "SF_entropy_gain";
    arrayOfString[b2++] = "SF_mean_prior_entropy";
    arrayOfString[b2++] = "SF_mean_scheme_entropy";
    arrayOfString[b2++] = "SF_mean_entropy_gain";
    arrayOfString[b2++] = "KB_information";
    arrayOfString[b2++] = "KB_mean_information";
    arrayOfString[b2++] = "KB_relative_information";
    arrayOfString[b2++] = "Summary";
    for (byte b3 = 0; b3 < b1; b3++)
      arrayOfString[b2++] = this.m_AdditionalMeasures[b3]; 
    if (b2 != 23 + b1)
      throw new Error("ResultNames didn't fit RESULT_SIZE"); 
    return arrayOfString;
  }
  
  public Object[] getResult(Instances paramInstances1, Instances paramInstances2) throws Exception {
    if (paramInstances1.classAttribute().type() != 1)
      throw new Exception("Class attribute is not nominal!"); 
    if (this.m_Template == null)
      throw new Exception("No classifier has been specified"); 
    byte b1 = (this.m_AdditionalMeasures != null) ? this.m_AdditionalMeasures.length : 0;
    Object[] arrayOfObject = new Object[23 + b1];
    String str = paramInstances1.relationName() + CostMatrix.FILE_EXTENSION;
    File file = new File(getOnDemandDirectory(), str);
    if (!file.exists())
      throw new Exception("On-demand cost file doesn't exist: " + file); 
    CostMatrix costMatrix = new CostMatrix(new BufferedReader(new FileReader(file)));
    Evaluation evaluation = new Evaluation(paramInstances1, costMatrix);
    this.m_Classifier = Classifier.makeCopy(this.m_Template);
    this.m_Classifier.buildClassifier(paramInstances1);
    evaluation.evaluateModel(this.m_Classifier, paramInstances2);
    this.m_result = evaluation.toSummaryString();
    byte b2 = 0;
    arrayOfObject[b2++] = new Double(evaluation.numInstances());
    arrayOfObject[b2++] = new Double(evaluation.correct());
    arrayOfObject[b2++] = new Double(evaluation.incorrect());
    arrayOfObject[b2++] = new Double(evaluation.unclassified());
    arrayOfObject[b2++] = new Double(evaluation.pctCorrect());
    arrayOfObject[b2++] = new Double(evaluation.pctIncorrect());
    arrayOfObject[b2++] = new Double(evaluation.pctUnclassified());
    arrayOfObject[b2++] = new Double(evaluation.totalCost());
    arrayOfObject[b2++] = new Double(evaluation.avgCost());
    arrayOfObject[b2++] = new Double(evaluation.meanAbsoluteError());
    arrayOfObject[b2++] = new Double(evaluation.rootMeanSquaredError());
    arrayOfObject[b2++] = new Double(evaluation.relativeAbsoluteError());
    arrayOfObject[b2++] = new Double(evaluation.rootRelativeSquaredError());
    arrayOfObject[b2++] = new Double(evaluation.SFPriorEntropy());
    arrayOfObject[b2++] = new Double(evaluation.SFSchemeEntropy());
    arrayOfObject[b2++] = new Double(evaluation.SFEntropyGain());
    arrayOfObject[b2++] = new Double(evaluation.SFMeanPriorEntropy());
    arrayOfObject[b2++] = new Double(evaluation.SFMeanSchemeEntropy());
    arrayOfObject[b2++] = new Double(evaluation.SFMeanEntropyGain());
    arrayOfObject[b2++] = new Double(evaluation.KBInformation());
    arrayOfObject[b2++] = new Double(evaluation.KBMeanInformation());
    arrayOfObject[b2++] = new Double(evaluation.KBRelativeInformation());
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
    if (b2 != 23 + b1)
      throw new Error("Results didn't fit RESULT_SIZE"); 
    return arrayOfObject;
  }
  
  public String toString() {
    String str = "CostSensitiveClassifierSplitEvaluator: ";
    return (this.m_Template == null) ? (str + "<null> classifier") : (str + this.m_Template.getClass().getName() + " " + this.m_ClassifierOptions + "(version " + this.m_ClassifierVersion + ")");
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\CostSensitiveClassifierSplitEvaluator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */