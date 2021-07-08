package weka.experiment;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class DatabaseResultProducer extends DatabaseResultListener implements ResultProducer, OptionHandler, AdditionalMeasureProducer {
  protected Instances m_Instances;
  
  protected ResultListener m_ResultListener = new CSVResultListener();
  
  protected ResultProducer m_ResultProducer = new CrossValidationResultProducer();
  
  protected String[] m_AdditionalMeasures = null;
  
  public String globalInfo() {
    return "Examines a database and extracts out the results produced by the specified ResultProducer and submits them to the specified ResultListener. If a result needs to be generated, the ResultProducer is used to obtain the result.";
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
    this.m_ResultProducer.doRunKeys(paramInt);
  }
  
  public void doRun(int paramInt) throws Exception {
    if (this.m_ResultProducer == null)
      throw new Exception("No ResultProducer set"); 
    if (this.m_ResultListener == null)
      throw new Exception("No ResultListener set"); 
    if (this.m_Instances == null)
      throw new Exception("No Instances set"); 
    this.m_ResultProducer.setResultListener(this);
    this.m_ResultProducer.setInstances(this.m_Instances);
    this.m_ResultProducer.doRun(paramInt);
  }
  
  public void preProcess(ResultProducer paramResultProducer) throws Exception {
    super.preProcess(paramResultProducer);
    if (this.m_ResultListener == null)
      throw new Exception("No ResultListener set"); 
    this.m_ResultListener.preProcess(this);
  }
  
  public void postProcess(ResultProducer paramResultProducer) throws Exception {
    super.postProcess(paramResultProducer);
    this.m_ResultListener.postProcess(this);
  }
  
  public void preProcess() throws Exception {
    if (this.m_ResultProducer == null)
      throw new Exception("No ResultProducer set"); 
    this.m_ResultProducer.setResultListener(this);
    this.m_ResultProducer.preProcess();
  }
  
  public void postProcess() throws Exception {
    this.m_ResultProducer.postProcess();
  }
  
  public void acceptResult(ResultProducer paramResultProducer, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) throws Exception {
    if (this.m_ResultProducer != paramResultProducer)
      throw new Error("Unrecognized ResultProducer sending results!!"); 
    boolean bool1 = this.m_ResultListener.isResultRequired(this, paramArrayOfObject1);
    boolean bool2 = super.isResultRequired(paramResultProducer, paramArrayOfObject1);
    if (bool2 && paramArrayOfObject2 != null)
      super.acceptResult(paramResultProducer, paramArrayOfObject1, paramArrayOfObject2); 
    if (bool1)
      this.m_ResultListener.acceptResult(this, paramArrayOfObject1, paramArrayOfObject2); 
  }
  
  public boolean isResultRequired(ResultProducer paramResultProducer, Object[] paramArrayOfObject) throws Exception {
    if (this.m_ResultProducer != paramResultProducer)
      throw new Error("Unrecognized ResultProducer sending results!!"); 
    boolean bool1 = this.m_ResultListener.isResultRequired(this, paramArrayOfObject);
    boolean bool2 = super.isResultRequired(paramResultProducer, paramArrayOfObject);
    if (!bool2 && bool1) {
      Object[] arrayOfObject = getResultFromTable(this.m_ResultsTableName, paramResultProducer, paramArrayOfObject);
      System.err.println("Got result from database: " + DatabaseUtils.arrayToString(arrayOfObject));
      this.m_ResultListener.acceptResult(this, paramArrayOfObject, arrayOfObject);
      return false;
    } 
    return (bool1 || bool2);
  }
  
  public String[] getKeyNames() throws Exception {
    return this.m_ResultProducer.getKeyNames();
  }
  
  public Object[] getKeyTypes() throws Exception {
    return this.m_ResultProducer.getKeyTypes();
  }
  
  public String[] getResultNames() throws Exception {
    return this.m_ResultProducer.getResultNames();
  }
  
  public Object[] getResultTypes() throws Exception {
    return this.m_ResultProducer.getResultTypes();
  }
  
  public String getCompatibilityState() {
    String str = "";
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
    vector.addElement(new Option("\tThe name of the database field to cache over.\n\teg: \"Fold\" (default none)", "F", 1, "-F <field name>"));
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
    setCacheKeyName(Utils.getOption('F', paramArrayOfString));
    String str = Utils.getOption('W', paramArrayOfString);
    if (str.length() == 0)
      throw new Exception("A ResultProducer must be specified with the -W option."); 
    setResultProducer((ResultProducer)Utils.forName(ResultProducer.class, str, null));
    if (getResultProducer() instanceof OptionHandler)
      ((OptionHandler)getResultProducer()).setOptions(Utils.partitionOptions(paramArrayOfString)); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_ResultProducer != null && this.m_ResultProducer instanceof OptionHandler)
      arrayOfString1 = ((OptionHandler)this.m_ResultProducer).getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 8];
    int i = 0;
    if (!getCacheKeyName().equals("")) {
      arrayOfString2[i++] = "-F";
      arrayOfString2[i++] = getCacheKeyName();
    } 
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
      System.err.println("DatabaseResultProducer: setting additional measures for ResultProducer");
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
    throw new IllegalArgumentException("DatabaseResultProducer: Can't return value for : " + paramString + ". " + this.m_ResultProducer.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
  }
  
  public void setResultListener(ResultListener paramResultListener) {
    this.m_ResultListener = paramResultListener;
  }
  
  public String resultProducerTipText() {
    return "Set the result producer to use. If some results are not found in the source database then this result producer is used to generate them.";
  }
  
  public ResultProducer getResultProducer() {
    return this.m_ResultProducer;
  }
  
  public void setResultProducer(ResultProducer paramResultProducer) {
    this.m_ResultProducer = paramResultProducer;
  }
  
  public String toString() {
    String str = "DatabaseResultProducer: ";
    str = str + getCompatibilityState();
    if (this.m_Instances == null) {
      str = str + ": <null Instances>";
    } else {
      str = str + ": " + Utils.backQuoteChars(this.m_Instances.relationName());
    } 
    return str;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\DatabaseResultProducer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */