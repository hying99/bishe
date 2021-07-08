package weka.experiment;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class LearningRateResultProducer implements ResultListener, ResultProducer, OptionHandler, AdditionalMeasureProducer {
  protected Instances m_Instances;
  
  protected ResultListener m_ResultListener = new CSVResultListener();
  
  protected ResultProducer m_ResultProducer = new AveragingResultProducer();
  
  protected String[] m_AdditionalMeasures = null;
  
  protected int m_LowerSize = 0;
  
  protected int m_UpperSize = -1;
  
  protected int m_StepSize = 10;
  
  protected int m_CurrentSize = 0;
  
  public static String STEP_FIELD_NAME = "Total_instances";
  
  public String globalInfo() {
    return "Tells a sub-ResultProducer to reproduce the current run for varying sized subsamples of the dataset. Normally used with an AveragingResultProducer and CrossValidationResultProducer combo to generate learning curve results.";
  }
  
  public String[] determineColumnConstraints(ResultProducer paramResultProducer) throws Exception {
    return null;
  }
  
  public void doRunKeys(int paramInt) throws Exception {
    if (this.m_ResultProducer == null)
      throw new Exception("No ResultProducer set"); 
    if (this.m_ResultListener == null)
      throw new Exception("No ResultListener set"); 
    if (this.m_Instances == null)
      throw new Exception("No Instances set"); 
    this.m_ResultProducer.setResultListener(this);
    this.m_ResultProducer.setInstances(this.m_Instances);
    if (this.m_LowerSize == 0) {
      this.m_CurrentSize = this.m_StepSize;
    } else {
      this.m_CurrentSize = this.m_LowerSize;
    } 
    while (this.m_CurrentSize <= this.m_Instances.numInstances() && (this.m_UpperSize == -1 || this.m_CurrentSize <= this.m_UpperSize)) {
      this.m_ResultProducer.doRunKeys(paramInt);
      this.m_CurrentSize += this.m_StepSize;
    } 
  }
  
  public void doRun(int paramInt) throws Exception {
    if (this.m_ResultProducer == null)
      throw new Exception("No ResultProducer set"); 
    if (this.m_ResultListener == null)
      throw new Exception("No ResultListener set"); 
    if (this.m_Instances == null)
      throw new Exception("No Instances set"); 
    Instances instances = new Instances(this.m_Instances);
    instances.randomize(new Random(paramInt));
    if (instances.classAttribute().isNominal())
      instances.stratify(this.m_StepSize); 
    this.m_ResultProducer.setResultListener(this);
    if (this.m_LowerSize == 0) {
      this.m_CurrentSize = this.m_StepSize;
    } else {
      this.m_CurrentSize = this.m_LowerSize;
    } 
    while (this.m_CurrentSize <= this.m_Instances.numInstances() && (this.m_UpperSize == -1 || this.m_CurrentSize <= this.m_UpperSize)) {
      this.m_ResultProducer.setInstances(new Instances(instances, 0, this.m_CurrentSize));
      this.m_ResultProducer.doRun(paramInt);
      this.m_CurrentSize += this.m_StepSize;
    } 
  }
  
  public void preProcess(ResultProducer paramResultProducer) throws Exception {
    if (this.m_ResultListener == null)
      throw new Exception("No ResultListener set"); 
    this.m_ResultListener.preProcess(this);
  }
  
  public void preProcess() throws Exception {
    if (this.m_ResultProducer == null)
      throw new Exception("No ResultProducer set"); 
    this.m_ResultProducer.setResultListener(this);
    this.m_ResultProducer.preProcess();
  }
  
  public void postProcess(ResultProducer paramResultProducer) throws Exception {
    this.m_ResultListener.postProcess(this);
  }
  
  public void postProcess() throws Exception {
    this.m_ResultProducer.postProcess();
  }
  
  public void acceptResult(ResultProducer paramResultProducer, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) throws Exception {
    if (this.m_ResultProducer != paramResultProducer)
      throw new Error("Unrecognized ResultProducer sending results!!"); 
    Object[] arrayOfObject = new Object[paramArrayOfObject1.length + 1];
    System.arraycopy(paramArrayOfObject1, 0, arrayOfObject, 0, paramArrayOfObject1.length);
    arrayOfObject[paramArrayOfObject1.length] = new String("" + this.m_CurrentSize);
    this.m_ResultListener.acceptResult(this, arrayOfObject, paramArrayOfObject2);
  }
  
  public boolean isResultRequired(ResultProducer paramResultProducer, Object[] paramArrayOfObject) throws Exception {
    if (this.m_ResultProducer != paramResultProducer)
      throw new Error("Unrecognized ResultProducer sending results!!"); 
    Object[] arrayOfObject = new Object[paramArrayOfObject.length + 1];
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, paramArrayOfObject.length);
    arrayOfObject[paramArrayOfObject.length] = new String("" + this.m_CurrentSize);
    return this.m_ResultListener.isResultRequired(this, arrayOfObject);
  }
  
  public String[] getKeyNames() throws Exception {
    String[] arrayOfString1 = this.m_ResultProducer.getKeyNames();
    String[] arrayOfString2 = new String[arrayOfString1.length + 1];
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, arrayOfString1.length);
    arrayOfString2[arrayOfString1.length] = STEP_FIELD_NAME;
    return arrayOfString2;
  }
  
  public Object[] getKeyTypes() throws Exception {
    Object[] arrayOfObject1 = this.m_ResultProducer.getKeyTypes();
    Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 1];
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length);
    arrayOfObject2[arrayOfObject1.length] = "";
    return arrayOfObject2;
  }
  
  public String[] getResultNames() throws Exception {
    return this.m_ResultProducer.getResultNames();
  }
  
  public Object[] getResultTypes() throws Exception {
    return this.m_ResultProducer.getResultTypes();
  }
  
  public String getCompatibilityState() {
    String str = " ";
    if (this.m_ResultProducer == null) {
      str = str + "<null ResultProducer>";
    } else {
      str = str + "-W " + this.m_ResultProducer.getClass().getName();
    } 
    str = str + " -- " + this.m_ResultProducer.getCompatibilityState();
    return str.trim();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tThe number of steps in the learning rate curve.\n\t(default 10)", "X", 1, "-X <num steps>"));
    vector.addElement(new Option("\tThe full class name of a ResultProducer.\n\teg: weka.experiment.CrossValidationResultProducer", "W", 1, "-W <class name>"));
    if (this.m_ResultProducer != null && this.m_ResultProducer instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to result producer " + this.m_ResultProducer.getClass().getName() + ":"));
      Enumeration enumeration = ((OptionHandler)this.m_ResultProducer).listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('S', paramArrayOfString);
    if (str1.length() != 0) {
      setStepSize(Integer.parseInt(str1));
    } else {
      setStepSize(10);
    } 
    String str2 = Utils.getOption('L', paramArrayOfString);
    if (str2.length() != 0) {
      setLowerSize(Integer.parseInt(str2));
    } else {
      setLowerSize(0);
    } 
    String str3 = Utils.getOption('U', paramArrayOfString);
    if (str3.length() != 0) {
      setUpperSize(Integer.parseInt(str3));
    } else {
      setUpperSize(-1);
    } 
    String str4 = Utils.getOption('W', paramArrayOfString);
    if (str4.length() == 0)
      throw new Exception("A ResultProducer must be specified with the -W option."); 
    setResultProducer((ResultProducer)Utils.forName(ResultProducer.class, str4, null));
    if (getResultProducer() instanceof OptionHandler)
      ((OptionHandler)getResultProducer()).setOptions(Utils.partitionOptions(paramArrayOfString)); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_ResultProducer != null && this.m_ResultProducer instanceof OptionHandler)
      arrayOfString1 = ((OptionHandler)this.m_ResultProducer).getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 9];
    int i = 0;
    arrayOfString2[i++] = "-S";
    arrayOfString2[i++] = "" + getStepSize();
    arrayOfString2[i++] = "-L";
    arrayOfString2[i++] = "" + getLowerSize();
    arrayOfString2[i++] = "-U";
    arrayOfString2[i++] = "" + getUpperSize();
    if (getResultProducer() != null) {
      arrayOfString2[i++] = "-W";
      arrayOfString2[i++] = getResultProducer().getClass().getName();
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
    if (this.m_ResultProducer != null) {
      System.err.println("LearningRateResultProducer: setting additional measures for ResultProducer");
      this.m_ResultProducer.setAdditionalMeasures(this.m_AdditionalMeasures);
    } 
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector();
    if (this.m_ResultProducer instanceof AdditionalMeasureProducer) {
      Enumeration enumeration = ((AdditionalMeasureProducer)this.m_ResultProducer).enumerateMeasures();
      while (enumeration.hasMoreElements()) {
        String str = enumeration.nextElement();
        vector.addElement(str);
      } 
    } 
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (this.m_ResultProducer instanceof AdditionalMeasureProducer)
      return ((AdditionalMeasureProducer)this.m_ResultProducer).getMeasure(paramString); 
    throw new IllegalArgumentException("LearningRateResultProducer: Can't return value for : " + paramString + ". " + this.m_ResultProducer.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
  }
  
  public String lowerSizeTipText() {
    return "Set the minmum number of instances in a dataset. Setting zero here will actually use <stepSize> number of instances at the first step (since it makes no sense to use zero instances :-))";
  }
  
  public int getLowerSize() {
    return this.m_LowerSize;
  }
  
  public void setLowerSize(int paramInt) {
    this.m_LowerSize = paramInt;
  }
  
  public String upperSizeTipText() {
    return "Set the maximum number of instances in a dataset. Setting -1 sets no upper limit (other than the total number of instances in the full dataset)";
  }
  
  public int getUpperSize() {
    return this.m_UpperSize;
  }
  
  public void setUpperSize(int paramInt) {
    this.m_UpperSize = paramInt;
  }
  
  public String stepSizeTipText() {
    return "Set the number of instances to add at each step.";
  }
  
  public int getStepSize() {
    return this.m_StepSize;
  }
  
  public void setStepSize(int paramInt) {
    this.m_StepSize = paramInt;
  }
  
  public void setResultListener(ResultListener paramResultListener) {
    this.m_ResultListener = paramResultListener;
  }
  
  public String resultProducerTipText() {
    return "Set the resultProducer for which learning rate results should be generated.";
  }
  
  public ResultProducer getResultProducer() {
    return this.m_ResultProducer;
  }
  
  public void setResultProducer(ResultProducer paramResultProducer) {
    this.m_ResultProducer = paramResultProducer;
    this.m_ResultProducer.setResultListener(this);
  }
  
  public String toString() {
    String str = "LearningRateResultProducer: ";
    str = str + getCompatibilityState();
    if (this.m_Instances == null) {
      str = str + ": <null Instances>";
    } else {
      str = str + ": " + Utils.backQuoteChars(this.m_Instances.relationName());
    } 
    return str;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\LearningRateResultProducer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */