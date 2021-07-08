package weka.experiment;

import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.DefaultListModel;
import weka.core.AdditionalMeasureProducer;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.xml.KOML;
import weka.core.xml.XMLOptions;
import weka.experiment.xml.XMLExperiment;

public class Experiment implements Serializable, OptionHandler {
  public static String FILE_EXTENSION = ".exp";
  
  protected ResultListener m_ResultListener = new InstancesResultListener();
  
  protected ResultProducer m_ResultProducer = new RandomSplitResultProducer();
  
  protected int m_RunLower = 1;
  
  protected int m_RunUpper = 10;
  
  protected DefaultListModel m_Datasets = new DefaultListModel();
  
  protected boolean m_UsePropertyIterator = false;
  
  protected PropertyNode[] m_PropertyPath;
  
  protected Object m_PropertyArray;
  
  protected String m_Notes = "";
  
  protected String[] m_AdditionalMeasures = null;
  
  protected boolean m_ClassFirst = false;
  
  protected boolean m_AdvanceDataSetFirst = true;
  
  boolean m_m_AdvanceRunFirst;
  
  protected transient int m_RunNumber;
  
  protected transient int m_DatasetNumber;
  
  protected transient int m_PropertyNumber;
  
  protected transient boolean m_Finished = true;
  
  protected transient Instances m_CurrentInstances;
  
  protected transient int m_CurrentProperty;
  
  public void classFirst(boolean paramBoolean) {
    this.m_ClassFirst = paramBoolean;
  }
  
  public boolean getAdvanceDataSetFirst() {
    return this.m_AdvanceDataSetFirst;
  }
  
  public void setAdvanceDataSetFirst(boolean paramBoolean) {
    this.m_AdvanceDataSetFirst = paramBoolean;
  }
  
  public boolean getUsePropertyIterator() {
    return this.m_UsePropertyIterator;
  }
  
  public void setUsePropertyIterator(boolean paramBoolean) {
    this.m_UsePropertyIterator = paramBoolean;
  }
  
  public PropertyNode[] getPropertyPath() {
    return this.m_PropertyPath;
  }
  
  public void setPropertyPath(PropertyNode[] paramArrayOfPropertyNode) {
    this.m_PropertyPath = paramArrayOfPropertyNode;
  }
  
  public void setPropertyArray(Object paramObject) {
    this.m_PropertyArray = paramObject;
  }
  
  public Object getPropertyArray() {
    return this.m_PropertyArray;
  }
  
  public int getPropertyArrayLength() {
    return Array.getLength(this.m_PropertyArray);
  }
  
  public Object getPropertyArrayValue(int paramInt) {
    return Array.get(this.m_PropertyArray, paramInt);
  }
  
  public int getCurrentRunNumber() {
    return this.m_RunNumber;
  }
  
  public int getCurrentDatasetNumber() {
    return this.m_DatasetNumber;
  }
  
  public int getCurrentPropertyNumber() {
    return this.m_PropertyNumber;
  }
  
  public void initialize() throws Exception {
    this.m_RunNumber = getRunLower();
    this.m_DatasetNumber = 0;
    this.m_PropertyNumber = 0;
    this.m_CurrentProperty = -1;
    this.m_CurrentInstances = null;
    this.m_Finished = false;
    if (this.m_UsePropertyIterator && this.m_PropertyArray == null)
      throw new Exception("Null array for property iterator"); 
    if (getRunLower() > getRunUpper())
      throw new Exception("Lower run number is greater than upper run number"); 
    if (getDatasets().size() == 0)
      throw new Exception("No datasets have been specified"); 
    if (this.m_ResultProducer == null)
      throw new Exception("No ResultProducer set"); 
    if (this.m_ResultListener == null)
      throw new Exception("No ResultListener set"); 
    determineAdditionalResultMeasures();
    this.m_ResultProducer.setResultListener(this.m_ResultListener);
    this.m_ResultProducer.setAdditionalMeasures(this.m_AdditionalMeasures);
    this.m_ResultProducer.preProcess();
    String[] arrayOfString = this.m_ResultListener.determineColumnConstraints(this.m_ResultProducer);
    if (arrayOfString != null)
      this.m_ResultProducer.setAdditionalMeasures(arrayOfString); 
  }
  
  private void determineAdditionalResultMeasures() throws Exception {
    this.m_AdditionalMeasures = null;
    FastVector fastVector = new FastVector();
    if (this.m_ResultProducer instanceof AdditionalMeasureProducer) {
      Enumeration enumeration = ((AdditionalMeasureProducer)this.m_ResultProducer).enumerateMeasures();
      while (enumeration.hasMoreElements()) {
        String str = enumeration.nextElement();
        if (str.startsWith("measure")) {
          if (fastVector.indexOf(str) == -1)
            fastVector.addElement(str); 
          continue;
        } 
        throw new Exception("Additional measures in " + this.m_ResultProducer.getClass().getName() + " must obey the naming convention" + " of starting with \"measure\"");
      } 
    } 
    if (this.m_UsePropertyIterator && this.m_PropertyArray != null)
      for (byte b = 0; b < Array.getLength(this.m_PropertyArray); b++) {
        Object object = Array.get(this.m_PropertyArray, b);
        if (object instanceof AdditionalMeasureProducer) {
          Enumeration enumeration = ((AdditionalMeasureProducer)object).enumerateMeasures();
          while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            if (str.startsWith("measure")) {
              if (fastVector.indexOf(str) == -1)
                fastVector.addElement(str); 
              continue;
            } 
            throw new Exception("Additional measures in " + object.getClass().getName() + " must obey the naming convention" + " of starting with \"measure\"");
          } 
        } 
      }  
    if (fastVector.size() > 0) {
      this.m_AdditionalMeasures = new String[fastVector.size()];
      for (byte b = 0; b < fastVector.size(); b++)
        this.m_AdditionalMeasures[b] = (String)fastVector.elementAt(b); 
    } 
  }
  
  protected void setProperty(int paramInt, Object paramObject) throws Exception {
    PropertyDescriptor propertyDescriptor = (this.m_PropertyPath[paramInt]).property;
    Object object = null;
    if (paramInt < this.m_PropertyPath.length - 1) {
      Method method1 = propertyDescriptor.getReadMethod();
      Object[] arrayOfObject1 = new Object[0];
      object = method1.invoke(paramObject, arrayOfObject1);
      setProperty(paramInt + 1, object);
    } else {
      object = Array.get(this.m_PropertyArray, this.m_PropertyNumber);
    } 
    Method method = propertyDescriptor.getWriteMethod();
    Object[] arrayOfObject = { object };
    method.invoke(paramObject, arrayOfObject);
  }
  
  public boolean hasMoreIterations() {
    return !this.m_Finished;
  }
  
  public void nextIteration() throws Exception {
    if (this.m_UsePropertyIterator && this.m_CurrentProperty != this.m_PropertyNumber) {
      setProperty(0, this.m_ResultProducer);
      this.m_CurrentProperty = this.m_PropertyNumber;
    } 
    if (this.m_CurrentInstances == null) {
      File file = getDatasets().elementAt(this.m_DatasetNumber);
      FileReader fileReader = new FileReader(file);
      Instances instances = new Instances(new BufferedReader(fileReader));
      if (this.m_ClassFirst) {
        instances.setClassIndex(0);
      } else {
        instances.setClassIndex(instances.numAttributes() - 1);
      } 
      this.m_CurrentInstances = instances;
      this.m_ResultProducer.setInstances(this.m_CurrentInstances);
    } 
    this.m_ResultProducer.doRun(this.m_RunNumber);
    advanceCounters();
  }
  
  public void advanceCounters() {
    if (this.m_AdvanceDataSetFirst) {
      this.m_RunNumber++;
      if (this.m_RunNumber > getRunUpper()) {
        this.m_RunNumber = getRunLower();
        this.m_DatasetNumber++;
        this.m_CurrentInstances = null;
        if (this.m_DatasetNumber >= getDatasets().size()) {
          this.m_DatasetNumber = 0;
          if (this.m_UsePropertyIterator) {
            this.m_PropertyNumber++;
            if (this.m_PropertyNumber >= Array.getLength(this.m_PropertyArray))
              this.m_Finished = true; 
          } else {
            this.m_Finished = true;
          } 
        } 
      } 
    } else {
      this.m_RunNumber++;
      if (this.m_RunNumber > getRunUpper()) {
        this.m_RunNumber = getRunLower();
        if (this.m_UsePropertyIterator) {
          this.m_PropertyNumber++;
          if (this.m_PropertyNumber >= Array.getLength(this.m_PropertyArray)) {
            this.m_PropertyNumber = 0;
            this.m_DatasetNumber++;
            this.m_CurrentInstances = null;
            if (this.m_DatasetNumber >= getDatasets().size())
              this.m_Finished = true; 
          } 
        } else {
          this.m_DatasetNumber++;
          this.m_CurrentInstances = null;
          if (this.m_DatasetNumber >= getDatasets().size())
            this.m_Finished = true; 
        } 
      } 
    } 
  }
  
  public void runExperiment() {
    while (hasMoreIterations()) {
      try {
        nextIteration();
      } catch (Exception exception) {
        exception.printStackTrace();
        System.err.println(exception.getMessage());
        advanceCounters();
      } 
    } 
  }
  
  public void postProcess() throws Exception {
    this.m_ResultProducer.postProcess();
  }
  
  public DefaultListModel getDatasets() {
    return this.m_Datasets;
  }
  
  public void setDatasets(DefaultListModel paramDefaultListModel) {
    this.m_Datasets = paramDefaultListModel;
  }
  
  public ResultListener getResultListener() {
    return this.m_ResultListener;
  }
  
  public void setResultListener(ResultListener paramResultListener) {
    this.m_ResultListener = paramResultListener;
  }
  
  public ResultProducer getResultProducer() {
    return this.m_ResultProducer;
  }
  
  public void setResultProducer(ResultProducer paramResultProducer) {
    this.m_ResultProducer = paramResultProducer;
  }
  
  public int getRunUpper() {
    return this.m_RunUpper;
  }
  
  public void setRunUpper(int paramInt) {
    this.m_RunUpper = paramInt;
  }
  
  public int getRunLower() {
    return this.m_RunLower;
  }
  
  public void setRunLower(int paramInt) {
    this.m_RunLower = paramInt;
  }
  
  public String getNotes() {
    return this.m_Notes;
  }
  
  public void setNotes(String paramString) {
    this.m_Notes = paramString;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tThe lower run number to start the experiment from.\n\t(default 1)", "L", 1, "-L <num>"));
    vector.addElement(new Option("\tThe upper run number to end the experiment at (inclusive).\n\t(default 10)", "U", 1, "-U <num>"));
    vector.addElement(new Option("\tThe dataset to run the experiment on.\n\t(required, may be specified multiple times)", "T", 1, "-T <arff file>"));
    vector.addElement(new Option("\tThe full class name of a ResultProducer (required).\n\teg: weka.experiment.RandomSplitResultProducer", "P", 1, "-P <class name>"));
    vector.addElement(new Option("\tThe full class name of a ResultListener (required).\n\teg: weka.experiment.CSVResultListener", "D", 1, "-D <class name>"));
    vector.addElement(new Option("\tA string containing any notes about the experiment.\n\t(default none)", "N", 1, "-N <string>"));
    if (this.m_ResultProducer != null && this.m_ResultProducer instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to result producer " + this.m_ResultProducer.getClass().getName() + ":"));
      Enumeration enumeration = ((OptionHandler)this.m_ResultProducer).listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('L', paramArrayOfString);
    if (str1.length() != 0) {
      setRunLower(Integer.parseInt(str1));
    } else {
      setRunLower(1);
    } 
    String str2 = Utils.getOption('U', paramArrayOfString);
    if (str2.length() != 0) {
      setRunUpper(Integer.parseInt(str2));
    } else {
      setRunUpper(10);
    } 
    if (getRunLower() > getRunUpper())
      throw new Exception("Lower (" + getRunLower() + ") is greater than upper (" + getRunUpper() + ")"); 
    setNotes(Utils.getOption('N', paramArrayOfString));
    getDatasets().removeAllElements();
    while (true) {
      String str = Utils.getOption('T', paramArrayOfString);
      if (str.length() != 0) {
        File file = new File(str);
        getDatasets().addElement(file);
      } 
      if (str.length() == 0) {
        if (getDatasets().size() == 0)
          throw new Exception("Required: -T <arff file name>"); 
        String str3 = Utils.getOption('D', paramArrayOfString);
        if (str3.length() == 0)
          throw new Exception("Required: -D <ResultListener class name>"); 
        str3 = str3.trim();
        int i = str3.indexOf(' ');
        String str4 = str3;
        String str5 = "";
        String[] arrayOfString = null;
        if (i != -1) {
          str4 = str3.substring(0, i);
          str5 = str3.substring(i).trim();
          arrayOfString = Utils.splitOptions(str5);
        } 
        setResultListener((ResultListener)Utils.forName(ResultListener.class, str4, arrayOfString));
        String str6 = Utils.getOption('P', paramArrayOfString);
        if (str6.length() == 0)
          throw new Exception("Required: -P <ResultProducer class name>"); 
        setResultProducer((ResultProducer)Utils.forName(ResultProducer.class, str6, Utils.partitionOptions(paramArrayOfString)));
        return;
      } 
    } 
  }
  
  public String[] getOptions() {
    this.m_UsePropertyIterator = false;
    this.m_PropertyPath = null;
    this.m_PropertyArray = null;
    String[] arrayOfString1 = new String[0];
    if (this.m_ResultProducer != null && this.m_ResultProducer instanceof OptionHandler)
      arrayOfString1 = ((OptionHandler)this.m_ResultProducer).getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + getDatasets().size() * 2 + 11];
    int i = 0;
    arrayOfString2[i++] = "-L";
    arrayOfString2[i++] = "" + getRunLower();
    arrayOfString2[i++] = "-U";
    arrayOfString2[i++] = "" + getRunUpper();
    if (getDatasets().size() != 0)
      for (byte b = 0; b < getDatasets().size(); b++) {
        arrayOfString2[i++] = "-T";
        arrayOfString2[i++] = getDatasets().elementAt(b).toString();
      }  
    if (getResultListener() != null) {
      arrayOfString2[i++] = "-D";
      arrayOfString2[i++] = getResultListener().getClass().getName();
    } 
    if (getResultProducer() != null) {
      arrayOfString2[i++] = "-P";
      arrayOfString2[i++] = getResultProducer().getClass().getName();
    } 
    if (!getNotes().equals("")) {
      arrayOfString2[i++] = "-N";
      arrayOfString2[i++] = getNotes();
    } 
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public String toString() {
    String str = "Runs from: " + this.m_RunLower + " to: " + this.m_RunUpper + '\n';
    str = str + "Datasets:";
    byte b;
    for (b = 0; b < this.m_Datasets.size(); b++)
      str = str + " " + this.m_Datasets.elementAt(b); 
    str = str + '\n';
    str = str + "Custom property iterator: " + (this.m_UsePropertyIterator ? "on" : "off") + "\n";
    if (this.m_UsePropertyIterator) {
      if (this.m_PropertyPath == null)
        throw new Error("*** null propertyPath ***"); 
      if (this.m_PropertyArray == null)
        throw new Error("*** null propertyArray ***"); 
      if (this.m_PropertyPath.length > 1) {
        str = str + "Custom property path:\n";
        for (b = 0; b < this.m_PropertyPath.length - 1; b++) {
          PropertyNode propertyNode = this.m_PropertyPath[b];
          str = str + "" + (b + 1) + "  " + propertyNode.parentClass.getName() + "::" + propertyNode.toString() + ' ' + propertyNode.value.toString() + '\n';
        } 
      } 
      str = str + "Custom property name:" + this.m_PropertyPath[this.m_PropertyPath.length - 1].toString() + '\n';
      str = str + "Custom property values:\n";
      for (b = 0; b < Array.getLength(this.m_PropertyArray); b++) {
        Object object = Array.get(this.m_PropertyArray, b);
        str = str + " " + (b + 1) + " " + object.getClass().getName() + " " + object.toString() + '\n';
      } 
    } 
    str = str + "ResultProducer: " + this.m_ResultProducer + '\n';
    str = str + "ResultListener: " + this.m_ResultListener + '\n';
    if (!getNotes().equals(""))
      str = str + "Notes: " + getNotes(); 
    return str;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Experiment experiment = null;
      String str1 = Utils.getOption("xml", paramArrayOfString);
      if (!str1.equals(""))
        paramArrayOfString = (new XMLOptions(str1)).toArray(); 
      String str2 = Utils.getOption('l', paramArrayOfString);
      String str3 = Utils.getOption('s', paramArrayOfString);
      boolean bool = Utils.getFlag('r', paramArrayOfString);
      if (str2.length() == 0) {
        experiment = new Experiment();
        try {
          experiment.setOptions(paramArrayOfString);
          Utils.checkForRemainingOptions(paramArrayOfString);
        } catch (Exception exception) {
          exception.printStackTrace();
          String str = "Usage:\n\n-l <exp file>\n\tLoad experiment from file (default use cli options)\n-s <exp file>\n\tSave experiment to file after setting other options\n\t(default don't save)\n-r\n\tRun experiment (default don't run)\n-xml <filename | xml-string>\n\tget options from XML-Data instead from parameters\n\n";
          Enumeration enumeration = experiment.listOptions();
          while (enumeration.hasMoreElements()) {
            Option option = enumeration.nextElement();
            str = str + option.synopsis() + "\n";
            str = str + option.description() + "\n";
          } 
          throw new Exception(str + "\n" + exception.getMessage());
        } 
      } else {
        String str;
        if (KOML.isPresent() && str2.toLowerCase().endsWith(".koml")) {
          experiment = (Experiment)KOML.read(str2);
        } else if (str2.toLowerCase().endsWith(".xml")) {
          XMLExperiment xMLExperiment = new XMLExperiment();
          experiment = (Experiment)xMLExperiment.read(str2);
        } else {
          FileInputStream fileInputStream = new FileInputStream(str2);
          ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
          experiment = (Experiment)objectInputStream.readObject();
          objectInputStream.close();
        } 
        do {
          str = Utils.getOption('T', paramArrayOfString);
          if (str.length() == 0)
            continue; 
          File file = new File(str);
          experiment.getDatasets().addElement(file);
        } while (str.length() != 0);
      } 
      System.err.println("Experiment:\n" + experiment.toString());
      if (str3.length() != 0)
        if (KOML.isPresent() && str3.toLowerCase().endsWith(".koml")) {
          KOML.write(str3, experiment);
        } else if (str3.toLowerCase().endsWith(".xml")) {
          XMLExperiment xMLExperiment = new XMLExperiment();
          xMLExperiment.write(str3, experiment);
        } else {
          FileOutputStream fileOutputStream = new FileOutputStream(str3);
          ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(fileOutputStream));
          objectOutputStream.writeObject(experiment);
          objectOutputStream.close();
        }  
      if (bool) {
        System.err.println("Initializing...");
        experiment.initialize();
        System.err.println("Iterating...");
        experiment.runExperiment();
        System.err.println("Postprocessing...");
        experiment.postProcess();
      } 
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\Experiment.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */