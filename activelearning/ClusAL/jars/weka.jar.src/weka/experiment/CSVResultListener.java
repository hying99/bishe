package weka.experiment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class CSVResultListener implements ResultListener, OptionHandler {
  protected ResultProducer m_RP;
  
  protected File m_OutputFile;
  
  protected String m_OutputFileName;
  
  protected transient PrintWriter m_Out;
  
  public CSVResultListener() {
    File file;
    this.m_OutputFile = null;
    this.m_OutputFileName = "";
    this.m_Out = new PrintWriter(System.out, true);
    try {
      file = File.createTempFile("weka_experiment", ".csv");
      file.deleteOnExit();
    } catch (Exception exception) {
      System.err.println("Cannot create temp file, writing to standard out.");
      file = new File("-");
    } 
    setOutputFile(file);
    setOutputFileName("");
  }
  
  public String globalInfo() {
    return "Takes results from a result producer and assembles them into comma separated value form.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tThe filename where output will be stored. Use - for stdout.\n\t(default temp file)", "O", 1, "-O <file name>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('O', paramArrayOfString);
    if (str.length() != 0) {
      setOutputFile(new File(str));
    } else {
      File file;
      try {
        file = File.createTempFile("weka_experiment", null);
        file.deleteOnExit();
      } catch (Exception exception) {
        System.err.println("Cannot create temp file, writing to standard out.");
        file = new File("-");
      } 
      setOutputFile(file);
      setOutputFileName("");
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[2];
    byte b = 0;
    arrayOfString[b++] = "-O";
    arrayOfString[b++] = getOutputFile().getName();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String outputFileTipText() {
    return "File to save to. Use '-' to write to standard out.";
  }
  
  public File getOutputFile() {
    return this.m_OutputFile;
  }
  
  public void setOutputFile(File paramFile) {
    this.m_OutputFile = paramFile;
    setOutputFileName(paramFile.getName());
  }
  
  public String outputFileName() {
    return this.m_OutputFileName;
  }
  
  public void setOutputFileName(String paramString) {
    this.m_OutputFileName = paramString;
  }
  
  public void preProcess(ResultProducer paramResultProducer) throws Exception {
    this.m_RP = paramResultProducer;
    if (this.m_OutputFile == null || this.m_OutputFile.getName().equals("-")) {
      this.m_Out = new PrintWriter(System.out, true);
    } else {
      this.m_Out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(this.m_OutputFile)), true);
    } 
    printResultNames(this.m_RP);
  }
  
  public void postProcess(ResultProducer paramResultProducer) throws Exception {
    if (this.m_OutputFile != null && !this.m_OutputFile.getName().equals("-"))
      this.m_Out.close(); 
  }
  
  public String[] determineColumnConstraints(ResultProducer paramResultProducer) throws Exception {
    return null;
  }
  
  public void acceptResult(ResultProducer paramResultProducer, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) throws Exception {
    if (this.m_RP != paramResultProducer)
      throw new Error("Unrecognized ResultProducer sending results!!"); 
    byte b;
    for (b = 0; b < paramArrayOfObject1.length; b++) {
      if (b != 0)
        this.m_Out.print(','); 
      if (paramArrayOfObject1[b] == null) {
        this.m_Out.print("?");
      } else {
        this.m_Out.print(Utils.quote(paramArrayOfObject1[b].toString()));
      } 
    } 
    for (b = 0; b < paramArrayOfObject2.length; b++) {
      this.m_Out.print(',');
      if (paramArrayOfObject2[b] == null) {
        this.m_Out.print("?");
      } else {
        this.m_Out.print(Utils.quote(paramArrayOfObject2[b].toString()));
      } 
    } 
    this.m_Out.println("");
  }
  
  public boolean isResultRequired(ResultProducer paramResultProducer, Object[] paramArrayOfObject) throws Exception {
    return true;
  }
  
  private void printResultNames(ResultProducer paramResultProducer) throws Exception {
    String[] arrayOfString1 = paramResultProducer.getKeyNames();
    for (byte b1 = 0; b1 < arrayOfString1.length; b1++) {
      if (b1 != 0)
        this.m_Out.print(','); 
      if (arrayOfString1[b1] == null) {
        this.m_Out.print("?");
      } else {
        this.m_Out.print("Key_" + arrayOfString1[b1].toString());
      } 
    } 
    String[] arrayOfString2 = paramResultProducer.getResultNames();
    for (byte b2 = 0; b2 < arrayOfString2.length; b2++) {
      this.m_Out.print(',');
      if (arrayOfString2[b2] == null) {
        this.m_Out.print("?");
      } else {
        this.m_Out.print(arrayOfString2[b2].toString());
      } 
    } 
    this.m_Out.println("");
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\CSVResultListener.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */