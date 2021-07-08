package weka.experiment;

import java.io.File;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class RandomSplitResultProducer implements ResultProducer, OptionHandler, AdditionalMeasureProducer {
  protected Instances m_Instances;
  
  protected ResultListener m_ResultListener = new CSVResultListener();
  
  protected double m_TrainPercent = 66.0D;
  
  protected boolean m_randomize = true;
  
  protected SplitEvaluator m_SplitEvaluator = new ClassifierSplitEvaluator();
  
  protected String[] m_AdditionalMeasures = null;
  
  protected boolean m_debugOutput = false;
  
  protected OutputZipper m_ZipDest = null;
  
  protected File m_OutputFile = new File(new File(System.getProperty("user.dir")), "splitEvalutorOut.zip");
  
  public static String DATASET_FIELD_NAME = "Dataset";
  
  public static String RUN_FIELD_NAME = "Run";
  
  public static String TIMESTAMP_FIELD_NAME = "Date_time";
  
  public String globalInfo() {
    return "Performs a random train and test using a supplied evaluator.";
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
  }
  
  public void setAdditionalMeasures(String[] paramArrayOfString) {
    this.m_AdditionalMeasures = paramArrayOfString;
    if (this.m_SplitEvaluator != null) {
      System.err.println("RandomSplitResultProducer: setting additional measures for split evaluator");
      this.m_SplitEvaluator.setAdditionalMeasures(this.m_AdditionalMeasures);
    } 
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector();
    if (this.m_SplitEvaluator instanceof AdditionalMeasureProducer) {
      Enumeration enumeration = ((AdditionalMeasureProducer)this.m_SplitEvaluator).enumerateMeasures();
      while (enumeration.hasMoreElements()) {
        String str = enumeration.nextElement();
        vector.addElement(str);
      } 
    } 
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (this.m_SplitEvaluator instanceof AdditionalMeasureProducer)
      return ((AdditionalMeasureProducer)this.m_SplitEvaluator).getMeasure(paramString); 
    throw new IllegalArgumentException("RandomSplitResultProducer: Can't return value for : " + paramString + ". " + this.m_SplitEvaluator.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
  }
  
  public void setResultListener(ResultListener paramResultListener) {
    this.m_ResultListener = paramResultListener;
  }
  
  public static Double getTimestamp() {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    double d = (calendar.get(1) * 10000 + (calendar.get(2) + 1) * 100 + calendar.get(5)) + calendar.get(11) / 100.0D + calendar.get(12) / 10000.0D;
    return new Double(d);
  }
  
  public void preProcess() throws Exception {
    if (this.m_SplitEvaluator == null)
      throw new Exception("No SplitEvalutor set"); 
    if (this.m_ResultListener == null)
      throw new Exception("No ResultListener set"); 
    this.m_ResultListener.preProcess(this);
  }
  
  public void postProcess() throws Exception {
    this.m_ResultListener.postProcess(this);
    if (this.m_debugOutput && this.m_ZipDest != null) {
      this.m_ZipDest.finished();
      this.m_ZipDest = null;
    } 
  }
  
  public void doRunKeys(int paramInt) throws Exception {
    if (this.m_Instances == null)
      throw new Exception("No Instances set"); 
    Object[] arrayOfObject1 = this.m_SplitEvaluator.getKey();
    Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 2];
    arrayOfObject2[0] = Utils.backQuoteChars(this.m_Instances.relationName());
    arrayOfObject2[1] = "" + paramInt;
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 2, arrayOfObject1.length);
    if (this.m_ResultListener.isResultRequired(this, arrayOfObject2))
      try {
        this.m_ResultListener.acceptResult(this, arrayOfObject2, null);
      } catch (Exception exception) {
        throw exception;
      }  
  }
  
  public void doRun(int paramInt) throws Exception {
    if (getRawOutput() && this.m_ZipDest == null)
      this.m_ZipDest = new OutputZipper(this.m_OutputFile); 
    if (this.m_Instances == null)
      throw new Exception("No Instances set"); 
    Object[] arrayOfObject1 = this.m_SplitEvaluator.getKey();
    Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 2];
    arrayOfObject2[0] = Utils.backQuoteChars(this.m_Instances.relationName());
    arrayOfObject2[1] = "" + paramInt;
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 2, arrayOfObject1.length);
    if (this.m_ResultListener.isResultRequired(this, arrayOfObject2)) {
      Instances instances2;
      Instances instances3;
      Instances instances1 = new Instances(this.m_Instances);
      if (!this.m_randomize) {
        int i = Utils.round(instances1.numInstances() * this.m_TrainPercent / 100.0D);
        int j = instances1.numInstances() - i;
        instances2 = new Instances(instances1, 0, i);
        instances3 = new Instances(instances1, i, j);
      } else {
        Random random = new Random(paramInt);
        instances1.randomize(random);
        if (instances1.classAttribute().isNominal()) {
          int i = instances1.numClasses();
          Instances[] arrayOfInstances = new Instances[i + 1];
          for (byte b1 = 0; b1 < i + 1; b1++)
            arrayOfInstances[b1] = new Instances(instances1, 10); 
          Enumeration enumeration = instances1.enumerateInstances();
          while (enumeration.hasMoreElements()) {
            Instance instance = enumeration.nextElement();
            if (instance.classIsMissing()) {
              arrayOfInstances[i].add(instance);
              continue;
            } 
            arrayOfInstances[(int)instance.classValue()].add(instance);
          } 
          byte b2;
          for (b2 = 0; b2 < i + 1; b2++)
            arrayOfInstances[b2].compactify(); 
          instances2 = new Instances(instances1, instances1.numInstances());
          instances3 = new Instances(instances1, instances1.numInstances());
          for (b2 = 0; b2 < i + 1; b2++) {
            int j = Utils.probRound(arrayOfInstances[b2].numInstances() * this.m_TrainPercent / 100.0D, random);
            int k;
            for (k = 0; k < j; k++)
              instances2.add(arrayOfInstances[b2].instance(k)); 
            for (k = j; k < arrayOfInstances[b2].numInstances(); k++)
              instances3.add(arrayOfInstances[b2].instance(k)); 
            arrayOfInstances[b2] = null;
          } 
          instances2.compactify();
          instances3.compactify();
          instances2.randomize(random);
          instances3.randomize(random);
        } else {
          int i = Utils.probRound(instances1.numInstances() * this.m_TrainPercent / 100.0D, random);
          int j = instances1.numInstances() - i;
          instances2 = new Instances(instances1, 0, i);
          instances3 = new Instances(instances1, i, j);
        } 
      } 
      try {
        Object[] arrayOfObject3 = this.m_SplitEvaluator.getResult(instances2, instances3);
        Object[] arrayOfObject4 = new Object[arrayOfObject3.length + 1];
        arrayOfObject4[0] = getTimestamp();
        System.arraycopy(arrayOfObject3, 0, arrayOfObject4, 1, arrayOfObject3.length);
        if (this.m_debugOutput) {
          String str = ("" + paramInt + "." + Utils.backQuoteChars(instances1.relationName()) + "." + this.m_SplitEvaluator.toString()).replace(' ', '_');
          str = Utils.removeSubstring(str, "weka.classifiers.");
          str = Utils.removeSubstring(str, "weka.filters.");
          str = Utils.removeSubstring(str, "weka.attributeSelection.");
          this.m_ZipDest.zipit(this.m_SplitEvaluator.getRawResultOutput(), str);
        } 
        this.m_ResultListener.acceptResult(this, arrayOfObject2, arrayOfObject4);
      } catch (Exception exception) {
        throw exception;
      } 
    } 
  }
  
  public String[] getKeyNames() {
    String[] arrayOfString1 = this.m_SplitEvaluator.getKeyNames();
    String[] arrayOfString2 = new String[arrayOfString1.length + 2];
    arrayOfString2[0] = DATASET_FIELD_NAME;
    arrayOfString2[1] = RUN_FIELD_NAME;
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 2, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public Object[] getKeyTypes() {
    Object[] arrayOfObject = this.m_SplitEvaluator.getKeyTypes();
    String[] arrayOfString = new String[arrayOfObject.length + 2];
    arrayOfString[0] = new String();
    arrayOfString[1] = new String();
    System.arraycopy(arrayOfObject, 0, arrayOfString, 2, arrayOfObject.length);
    return (Object[])arrayOfString;
  }
  
  public String[] getResultNames() {
    String[] arrayOfString1 = this.m_SplitEvaluator.getResultNames();
    String[] arrayOfString2 = new String[arrayOfString1.length + 1];
    arrayOfString2[0] = TIMESTAMP_FIELD_NAME;
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 1, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public Object[] getResultTypes() {
    Object[] arrayOfObject1 = this.m_SplitEvaluator.getResultTypes();
    Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 1];
    arrayOfObject2[0] = new Double(0.0D);
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 1, arrayOfObject1.length);
    return arrayOfObject2;
  }
  
  public String getCompatibilityState() {
    String str = "-P " + this.m_TrainPercent;
    if (!getRandomizeData())
      str = str + " -R"; 
    if (this.m_SplitEvaluator == null) {
      str = str + " <null SplitEvaluator>";
    } else {
      str = str + " -W " + this.m_SplitEvaluator.getClass().getName();
    } 
    return str + " --";
  }
  
  public String outputFileTipText() {
    return "Set the destination for saving raw output. If the rawOutput option is selected, then output from the splitEvaluator for individual train-test splits is saved. If the destination is a directory, then each output is saved to an individual gzip file; if the destination is a file, then each output is saved as an entry in a zip file.";
  }
  
  public File getOutputFile() {
    return this.m_OutputFile;
  }
  
  public void setOutputFile(File paramFile) {
    this.m_OutputFile = paramFile;
  }
  
  public String randomizeDataTipText() {
    return "Do not randomize dataset and do not perform probabilistic rounding if true";
  }
  
  public boolean getRandomizeData() {
    return this.m_randomize;
  }
  
  public void setRandomizeData(boolean paramBoolean) {
    this.m_randomize = paramBoolean;
  }
  
  public String rawOutputTipText() {
    return "Save raw output (useful for debugging). If set, then output is sent to the destination specified by outputFile";
  }
  
  public boolean getRawOutput() {
    return this.m_debugOutput;
  }
  
  public void setRawOutput(boolean paramBoolean) {
    this.m_debugOutput = paramBoolean;
  }
  
  public String trainPercentTipText() {
    return "Set the percentage of data to use for training.";
  }
  
  public double getTrainPercent() {
    return this.m_TrainPercent;
  }
  
  public void setTrainPercent(double paramDouble) {
    this.m_TrainPercent = paramDouble;
  }
  
  public String splitEvaluatorTipText() {
    return "The evaluator to apply to the test data. This may be a classifier, regression scheme etc.";
  }
  
  public SplitEvaluator getSplitEvaluator() {
    return this.m_SplitEvaluator;
  }
  
  public void setSplitEvaluator(SplitEvaluator paramSplitEvaluator) {
    this.m_SplitEvaluator = paramSplitEvaluator;
    this.m_SplitEvaluator.setAdditionalMeasures(this.m_AdditionalMeasures);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(5);
    vector.addElement(new Option("\tThe percentage of instances to use for training.\n\t(default 66)", "P", 1, "-P <percent>"));
    vector.addElement(new Option("Save raw split evaluator output.", "D", 0, "-D"));
    vector.addElement(new Option("\tThe filename where raw output will be stored.\n\tIf a directory name is specified then then individual\n\toutputs will be gzipped, otherwise all output will be\n\tzipped to the named file. Use in conjuction with -D.\t(default splitEvalutorOut.zip)", "O", 1, "-O <file/directory name/path>"));
    vector.addElement(new Option("\tThe full class name of a SplitEvaluator.\n\teg: weka.experiment.ClassifierSplitEvaluator", "W", 1, "-W <class name>"));
    vector.addElement(new Option("\tSet when data is not to be randomized and the data sets' size.\n\tIs not to be determined via probabilistic rounding.", "R", 0, "-R"));
    if (this.m_SplitEvaluator != null && this.m_SplitEvaluator instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to split evaluator " + this.m_SplitEvaluator.getClass().getName() + ":"));
      Enumeration enumeration = ((OptionHandler)this.m_SplitEvaluator).listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setRawOutput(Utils.getFlag('D', paramArrayOfString));
    setRandomizeData(!Utils.getFlag('R', paramArrayOfString));
    String str1 = Utils.getOption('O', paramArrayOfString);
    if (str1.length() != 0)
      setOutputFile(new File(str1)); 
    String str2 = Utils.getOption('P', paramArrayOfString);
    if (str2.length() != 0) {
      setTrainPercent((new Double(str2)).doubleValue());
    } else {
      setTrainPercent(66.0D);
    } 
    String str3 = Utils.getOption('W', paramArrayOfString);
    if (str3.length() == 0)
      throw new Exception("A SplitEvaluator must be specified with the -W option."); 
    setSplitEvaluator((SplitEvaluator)Utils.forName(SplitEvaluator.class, str3, null));
    if (getSplitEvaluator() instanceof OptionHandler)
      ((OptionHandler)getSplitEvaluator()).setOptions(Utils.partitionOptions(paramArrayOfString)); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_SplitEvaluator != null && this.m_SplitEvaluator instanceof OptionHandler)
      arrayOfString1 = ((OptionHandler)this.m_SplitEvaluator).getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 9];
    int i = 0;
    arrayOfString2[i++] = "-P";
    arrayOfString2[i++] = "" + getTrainPercent();
    if (getRawOutput())
      arrayOfString2[i++] = "-D"; 
    if (!getRandomizeData())
      arrayOfString2[i++] = "-R"; 
    arrayOfString2[i++] = "-O";
    arrayOfString2[i++] = getOutputFile().getName();
    if (getSplitEvaluator() != null) {
      arrayOfString2[i++] = "-W";
      arrayOfString2[i++] = getSplitEvaluator().getClass().getName();
    } 
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public String toString() {
    String str = "RandomSplitResultProducer: ";
    str = str + getCompatibilityState();
    if (this.m_Instances == null) {
      str = str + ": <null Instances>";
    } else {
      str = str + ": " + Utils.backQuoteChars(this.m_Instances.relationName());
    } 
    return str;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\RandomSplitResultProducer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */