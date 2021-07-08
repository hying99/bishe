package weka.experiment;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import weka.core.AdditionalMeasureProducer;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class AveragingResultProducer implements ResultListener, ResultProducer, OptionHandler, AdditionalMeasureProducer {
  protected Instances m_Instances;
  
  protected ResultListener m_ResultListener = new CSVResultListener();
  
  protected ResultProducer m_ResultProducer = new CrossValidationResultProducer();
  
  protected String[] m_AdditionalMeasures = null;
  
  protected int m_ExpectedResultsPerAverage = 10;
  
  protected boolean m_CalculateStdDevs;
  
  protected String m_CountFieldName = "Num_" + CrossValidationResultProducer.FOLD_FIELD_NAME;
  
  protected String m_KeyFieldName = CrossValidationResultProducer.FOLD_FIELD_NAME;
  
  protected int m_KeyIndex = -1;
  
  protected FastVector m_Keys = new FastVector();
  
  protected FastVector m_Results = new FastVector();
  
  public String globalInfo() {
    return "Takes the results from a ResultProducer and submits the average to the result listener. Normally used with a CrossValidationResultProducer to perform n x m fold cross validation.";
  }
  
  protected int findKeyIndex() {
    this.m_KeyIndex = -1;
    try {
      if (this.m_ResultProducer != null) {
        String[] arrayOfString = this.m_ResultProducer.getKeyNames();
        for (byte b = 0; b < arrayOfString.length; b++) {
          if (arrayOfString[b].equals(this.m_KeyFieldName)) {
            this.m_KeyIndex = b;
            break;
          } 
        } 
      } 
    } catch (Exception exception) {}
    return this.m_KeyIndex;
  }
  
  public String[] determineColumnConstraints(ResultProducer paramResultProducer) throws Exception {
    return null;
  }
  
  protected Object[] determineTemplate(int paramInt) throws Exception {
    if (this.m_Instances == null)
      throw new Exception("No Instances set"); 
    this.m_ResultProducer.setInstances(this.m_Instances);
    this.m_Keys.removeAllElements();
    this.m_Results.removeAllElements();
    this.m_ResultProducer.doRunKeys(paramInt);
    checkForMultipleDifferences();
    Object[] arrayOfObject = (Object[])((Object[])this.m_Keys.elementAt(0)).clone();
    arrayOfObject[this.m_KeyIndex] = null;
    checkForDuplicateKeys(arrayOfObject);
    return arrayOfObject;
  }
  
  public void doRunKeys(int paramInt) throws Exception {
    Object[] arrayOfObject = determineTemplate(paramInt);
    String[] arrayOfString = new String[arrayOfObject.length - 1];
    System.arraycopy(arrayOfObject, 0, arrayOfString, 0, this.m_KeyIndex);
    System.arraycopy(arrayOfObject, this.m_KeyIndex + 1, arrayOfString, this.m_KeyIndex, arrayOfObject.length - this.m_KeyIndex - 1);
    this.m_ResultListener.acceptResult(this, (Object[])arrayOfString, null);
  }
  
  public void doRun(int paramInt) throws Exception {
    Object[] arrayOfObject = determineTemplate(paramInt);
    String[] arrayOfString = new String[arrayOfObject.length - 1];
    System.arraycopy(arrayOfObject, 0, arrayOfString, 0, this.m_KeyIndex);
    System.arraycopy(arrayOfObject, this.m_KeyIndex + 1, arrayOfString, this.m_KeyIndex, arrayOfObject.length - this.m_KeyIndex - 1);
    if (this.m_ResultListener.isResultRequired(this, (Object[])arrayOfString)) {
      this.m_Keys.removeAllElements();
      this.m_Results.removeAllElements();
      this.m_ResultProducer.doRun(paramInt);
      checkForMultipleDifferences();
      arrayOfObject = (Object[])((Object[])this.m_Keys.elementAt(0)).clone();
      arrayOfObject[this.m_KeyIndex] = null;
      checkForDuplicateKeys(arrayOfObject);
      doAverageResult(arrayOfObject);
    } 
  }
  
  protected boolean matchesTemplate(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) {
    if (paramArrayOfObject1.length != paramArrayOfObject2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfObject2.length; b++) {
      if (paramArrayOfObject1[b] != null && !paramArrayOfObject1[b].equals(paramArrayOfObject2[b]))
        return false; 
    } 
    return true;
  }
  
  protected void doAverageResult(Object[] paramArrayOfObject) throws Exception {
    String[] arrayOfString = new String[paramArrayOfObject.length - 1];
    System.arraycopy(paramArrayOfObject, 0, arrayOfString, 0, this.m_KeyIndex);
    System.arraycopy(paramArrayOfObject, this.m_KeyIndex + 1, arrayOfString, this.m_KeyIndex, paramArrayOfObject.length - this.m_KeyIndex - 1);
    if (this.m_ResultListener.isResultRequired(this, (Object[])arrayOfString)) {
      Object[] arrayOfObject1 = this.m_ResultProducer.getResultTypes();
      Stats[] arrayOfStats = new Stats[arrayOfObject1.length];
      for (byte b1 = 0; b1 < arrayOfStats.length; b1++)
        arrayOfStats[b1] = new Stats(); 
      Object[] arrayOfObject2 = getResultTypes();
      byte b2 = 0;
      for (byte b3 = 0; b3 < this.m_Keys.size(); b3++) {
        Object[] arrayOfObject = (Object[])this.m_Keys.elementAt(b3);
        if (matchesTemplate(paramArrayOfObject, arrayOfObject)) {
          Object[] arrayOfObject4 = (Object[])this.m_Results.elementAt(b3);
          b2++;
          for (byte b = 0; b < arrayOfObject1.length; b++) {
            if (arrayOfObject1[b] instanceof Double) {
              if (arrayOfObject4[b] == null && arrayOfStats[b] != null)
                arrayOfStats[b] = null; 
              if (arrayOfStats[b] != null) {
                double d = ((Double)arrayOfObject4[b]).doubleValue();
                arrayOfStats[b].add(d);
              } 
            } 
          } 
        } 
      } 
      if (b2 != this.m_ExpectedResultsPerAverage)
        throw new Exception("Expected " + this.m_ExpectedResultsPerAverage + " results matching key \"" + DatabaseUtils.arrayToString(paramArrayOfObject) + "\" but got " + b2); 
      arrayOfObject2[0] = new Double(b2);
      Object[] arrayOfObject3 = (Object[])this.m_Results.elementAt(0);
      byte b4 = 1;
      for (byte b5 = 0; b5 < arrayOfObject1.length; b5++) {
        if (arrayOfObject1[b5] instanceof Double) {
          if (arrayOfStats[b5] != null) {
            arrayOfStats[b5].calculateDerived();
            arrayOfObject2[b4++] = new Double((arrayOfStats[b5]).mean);
          } else {
            arrayOfObject2[b4++] = null;
          } 
          if (getCalculateStdDevs())
            if (arrayOfStats[b5] != null) {
              arrayOfObject2[b4++] = new Double((arrayOfStats[b5]).stdDev);
            } else {
              arrayOfObject2[b4++] = null;
            }  
        } else {
          arrayOfObject2[b4++] = arrayOfObject3[b5];
        } 
      } 
      this.m_ResultListener.acceptResult(this, (Object[])arrayOfString, arrayOfObject2);
    } 
  }
  
  protected void checkForDuplicateKeys(Object[] paramArrayOfObject) throws Exception {
    Hashtable hashtable = new Hashtable();
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_Keys.size(); b2++) {
      Object[] arrayOfObject = (Object[])this.m_Keys.elementAt(b2);
      if (matchesTemplate(paramArrayOfObject, arrayOfObject)) {
        if (hashtable.containsKey(arrayOfObject[this.m_KeyIndex]))
          throw new Exception("Duplicate result received:" + DatabaseUtils.arrayToString(arrayOfObject)); 
        b1++;
        hashtable.put(arrayOfObject[this.m_KeyIndex], arrayOfObject[this.m_KeyIndex]);
      } 
    } 
    if (b1 != this.m_ExpectedResultsPerAverage)
      throw new Exception("Expected " + this.m_ExpectedResultsPerAverage + " results matching key \"" + DatabaseUtils.arrayToString(paramArrayOfObject) + "\" but got " + b1); 
  }
  
  protected void checkForMultipleDifferences() throws Exception {
    Object[] arrayOfObject1 = (Object[])this.m_Keys.elementAt(0);
    Object[] arrayOfObject2 = (Object[])this.m_Keys.elementAt(this.m_Keys.size() - 1);
    for (byte b = 0; b < arrayOfObject1.length; b++) {
      if (b != this.m_KeyIndex && !arrayOfObject1[b].equals(arrayOfObject2[b]))
        throw new Exception("Keys differ on fields other than \"" + this.m_KeyFieldName + "\" -- time to implement multiple averaging"); 
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
    findKeyIndex();
    if (this.m_KeyIndex == -1)
      throw new Exception("No key field called " + this.m_KeyFieldName + " produced by " + this.m_ResultProducer.getClass().getName()); 
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
    this.m_Keys.addElement(paramArrayOfObject1);
    this.m_Results.addElement(paramArrayOfObject2);
  }
  
  public boolean isResultRequired(ResultProducer paramResultProducer, Object[] paramArrayOfObject) throws Exception {
    if (this.m_ResultProducer != paramResultProducer)
      throw new Error("Unrecognized ResultProducer sending results!!"); 
    return true;
  }
  
  public String[] getKeyNames() throws Exception {
    if (this.m_KeyIndex == -1)
      throw new Exception("No key field called " + this.m_KeyFieldName + " produced by " + this.m_ResultProducer.getClass().getName()); 
    String[] arrayOfString1 = this.m_ResultProducer.getKeyNames();
    String[] arrayOfString2 = new String[arrayOfString1.length - 1];
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, this.m_KeyIndex);
    System.arraycopy(arrayOfString1, this.m_KeyIndex + 1, arrayOfString2, this.m_KeyIndex, arrayOfString1.length - this.m_KeyIndex - 1);
    return arrayOfString2;
  }
  
  public Object[] getKeyTypes() throws Exception {
    if (this.m_KeyIndex == -1)
      throw new Exception("No key field called " + this.m_KeyFieldName + " produced by " + this.m_ResultProducer.getClass().getName()); 
    Object[] arrayOfObject = this.m_ResultProducer.getKeyTypes();
    String[] arrayOfString = new String[arrayOfObject.length - 1];
    System.arraycopy(arrayOfObject, 0, arrayOfString, 0, this.m_KeyIndex);
    System.arraycopy(arrayOfObject, this.m_KeyIndex + 1, arrayOfString, this.m_KeyIndex, arrayOfObject.length - this.m_KeyIndex - 1);
    return (Object[])arrayOfString;
  }
  
  public String[] getResultNames() throws Exception {
    String[] arrayOfString1 = this.m_ResultProducer.getResultNames();
    if (getCalculateStdDevs()) {
      Object[] arrayOfObject = this.m_ResultProducer.getResultTypes();
      byte b1 = 0;
      for (byte b2 = 0; b2 < arrayOfObject.length; b2++) {
        if (arrayOfObject[b2] instanceof Double)
          b1++; 
      } 
      String[] arrayOfString = new String[arrayOfString1.length + 1 + b1];
      arrayOfString[0] = this.m_CountFieldName;
      byte b3 = 1;
      for (byte b4 = 0; b4 < arrayOfString1.length; b4++) {
        arrayOfString[b3++] = "Avg_" + arrayOfString1[b4];
        if (arrayOfObject[b4] instanceof Double)
          arrayOfString[b3++] = "Dev_" + arrayOfString1[b4]; 
      } 
      return arrayOfString;
    } 
    String[] arrayOfString2 = new String[arrayOfString1.length + 1];
    arrayOfString2[0] = this.m_CountFieldName;
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 1, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public Object[] getResultTypes() throws Exception {
    Object[] arrayOfObject1 = this.m_ResultProducer.getResultTypes();
    if (getCalculateStdDevs()) {
      byte b1 = 0;
      for (byte b2 = 0; b2 < arrayOfObject1.length; b2++) {
        if (arrayOfObject1[b2] instanceof Double)
          b1++; 
      } 
      Object[] arrayOfObject = new Object[arrayOfObject1.length + 1 + b1];
      arrayOfObject[0] = new Double(0.0D);
      byte b3 = 1;
      for (byte b4 = 0; b4 < arrayOfObject1.length; b4++) {
        arrayOfObject[b3++] = arrayOfObject1[b4];
        if (arrayOfObject1[b4] instanceof Double)
          arrayOfObject[b3++] = new Double(0.0D); 
      } 
      return arrayOfObject;
    } 
    Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 1];
    arrayOfObject2[0] = new Double(0.0D);
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 1, arrayOfObject1.length);
    return arrayOfObject2;
  }
  
  public String getCompatibilityState() {
    String str = " -X " + getExpectedResultsPerAverage() + " ";
    if (getCalculateStdDevs())
      str = str + "-S "; 
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
    vector.addElement(new Option("\tThe name of the field to average over.\n\t(default \"Fold\")", "F", 1, "-F <field name>"));
    vector.addElement(new Option("\tThe number of results expected per average.\n\t(default 10)", "X", 1, "-X <num results>"));
    vector.addElement(new Option("\tCalculate standard deviations.\n\t(default only averages)", "S", 0, "-S"));
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
    String str1 = Utils.getOption('F', paramArrayOfString);
    if (str1.length() != 0) {
      setKeyFieldName(str1);
    } else {
      setKeyFieldName(CrossValidationResultProducer.FOLD_FIELD_NAME);
    } 
    String str2 = Utils.getOption('X', paramArrayOfString);
    if (str2.length() != 0) {
      setExpectedResultsPerAverage(Integer.parseInt(str2));
    } else {
      setExpectedResultsPerAverage(10);
    } 
    setCalculateStdDevs(Utils.getFlag('S', paramArrayOfString));
    String str3 = Utils.getOption('W', paramArrayOfString);
    if (str3.length() == 0)
      throw new Exception("A ResultProducer must be specified with the -W option."); 
    setResultProducer((ResultProducer)Utils.forName(ResultProducer.class, str3, null));
    if (getResultProducer() instanceof OptionHandler)
      ((OptionHandler)getResultProducer()).setOptions(Utils.partitionOptions(paramArrayOfString)); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_ResultProducer != null && this.m_ResultProducer instanceof OptionHandler)
      arrayOfString1 = ((OptionHandler)this.m_ResultProducer).getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 8];
    int i = 0;
    arrayOfString2[i++] = "-F";
    arrayOfString2[i++] = "" + getKeyFieldName();
    arrayOfString2[i++] = "-X";
    arrayOfString2[i++] = "" + getExpectedResultsPerAverage();
    if (getCalculateStdDevs())
      arrayOfString2[i++] = "-S"; 
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
      System.err.println("AveragingResultProducer: setting additional measures for ResultProducer");
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
    throw new IllegalArgumentException("AveragingResultProducer: Can't return value for : " + paramString + ". " + this.m_ResultProducer.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
  }
  
  public String calculateStdDevsTipText() {
    return "Record standard deviations for each run.";
  }
  
  public boolean getCalculateStdDevs() {
    return this.m_CalculateStdDevs;
  }
  
  public void setCalculateStdDevs(boolean paramBoolean) {
    this.m_CalculateStdDevs = paramBoolean;
  }
  
  public String expectedResultsPerAverageTipText() {
    return "Set the expected number of results to average per run. For example if a CrossValidationResultProducer is being used (with the number of folds set to 10), then the expected number of results per run is 10.";
  }
  
  public int getExpectedResultsPerAverage() {
    return this.m_ExpectedResultsPerAverage;
  }
  
  public void setExpectedResultsPerAverage(int paramInt) {
    this.m_ExpectedResultsPerAverage = paramInt;
  }
  
  public String keyFieldNameTipText() {
    return "Set the field name that will be unique for a run.";
  }
  
  public String getKeyFieldName() {
    return this.m_KeyFieldName;
  }
  
  public void setKeyFieldName(String paramString) {
    this.m_KeyFieldName = paramString;
    this.m_CountFieldName = "Num_" + this.m_KeyFieldName;
    findKeyIndex();
  }
  
  public void setResultListener(ResultListener paramResultListener) {
    this.m_ResultListener = paramResultListener;
  }
  
  public String resultProducerTipText() {
    return "Set the resultProducer for which results are to be averaged.";
  }
  
  public ResultProducer getResultProducer() {
    return this.m_ResultProducer;
  }
  
  public void setResultProducer(ResultProducer paramResultProducer) {
    this.m_ResultProducer = paramResultProducer;
    this.m_ResultProducer.setResultListener(this);
    findKeyIndex();
  }
  
  public String toString() {
    String str = "AveragingResultProducer: ";
    str = str + getCompatibilityState();
    if (this.m_Instances == null) {
      str = str + ": <null Instances>";
    } else {
      str = str + ": " + Utils.backQuoteChars(this.m_Instances.relationName());
    } 
    return str;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\AveragingResultProducer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */