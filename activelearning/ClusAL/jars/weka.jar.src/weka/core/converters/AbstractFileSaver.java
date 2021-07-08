package weka.core.converters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import weka.core.FastVector;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public abstract class AbstractFileSaver extends AbstractSaver implements OptionHandler, FileSourcedConverter {
  private File m_outputFile;
  
  private BufferedWriter m_writer;
  
  private String FILE_EXTENSION;
  
  private String m_prefix;
  
  private String m_dir;
  
  protected int m_incrementalCounter;
  
  public void resetOptions() {
    super.resetOptions();
    this.m_outputFile = null;
    this.m_writer = null;
    this.m_prefix = "";
    this.m_dir = "";
    this.m_incrementalCounter = 0;
  }
  
  public BufferedWriter getWriter() {
    return this.m_writer;
  }
  
  public void resetWriter() {
    this.m_writer = null;
  }
  
  public String getFileExtension() {
    return this.FILE_EXTENSION;
  }
  
  protected void setFileExtension(String paramString) {
    this.FILE_EXTENSION = paramString;
  }
  
  public File retrieveFile() {
    return this.m_outputFile;
  }
  
  public void setFile(File paramFile) throws IOException {
    this.m_outputFile = paramFile;
  }
  
  public void setFilePrefix(String paramString) {
    this.m_prefix = paramString;
  }
  
  public String filePrefix() {
    return this.m_prefix;
  }
  
  public void setDir(String paramString) {
    this.m_dir = paramString;
  }
  
  public String retrieveDir() {
    return this.m_dir;
  }
  
  public Enumeration listOptions() {
    FastVector fastVector = new FastVector(2);
    fastVector.addElement(new Option("The input file", "i", 1, "-i <the input file>"));
    fastVector.addElement(new Option("The output file", "o", 1, "-o <the output file>"));
    return fastVector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('o', paramArrayOfString);
    String str2 = Utils.getOption('i', paramArrayOfString);
    ArffLoader arffLoader = new ArffLoader();
    resetOptions();
    if (str2.length() != 0) {
      try {
        File file = new File(str2);
        arffLoader.setFile(file);
        setInstances(arffLoader.getDataSet());
      } catch (Exception exception) {
        throw new IOException("No data set loaded. Data set has to be in ARFF format.");
      } 
    } else {
      throw new IOException("No data set to save.");
    } 
    if (str1.length() != 0) {
      if (!str1.endsWith(this.FILE_EXTENSION))
        if (str1.lastIndexOf('.') != -1) {
          str1 = str1.substring(0, str1.lastIndexOf('.')) + this.FILE_EXTENSION;
        } else {
          str1 = str1 + this.FILE_EXTENSION;
        }  
      try {
        File file = new File(str1);
        setFile(file);
      } catch (Exception exception) {
        throw new IOException("Cannot create output file. Standard out is used.");
      } finally {
        setDestination(this.m_outputFile);
      } 
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[10];
    byte b = 0;
    if (this.m_outputFile != null) {
      arrayOfString[b++] = "-o";
      arrayOfString[b++] = "" + this.m_outputFile;
    } else {
      arrayOfString[b++] = "-o";
      arrayOfString[b++] = "";
    } 
    if (getInstances() != null) {
      arrayOfString[b++] = "-i";
      arrayOfString[b++] = "" + getInstances().relationName();
    } else {
      arrayOfString[b++] = "-i";
      arrayOfString[b++] = "";
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void cancel() {
    if (getWriteMode() == 2) {
      if (this.m_outputFile != null && this.m_outputFile.exists() && this.m_outputFile.delete())
        System.out.println("File deleted."); 
      resetOptions();
    } 
  }
  
  public void setDestination(File paramFile) throws IOException {
    boolean bool = false;
    String str = paramFile.getAbsolutePath();
    if (this.m_outputFile != null)
      try {
        if (paramFile.exists())
          if (paramFile.delete()) {
            System.out.println("File exists and will be overridden.");
          } else {
            throw new IOException("File already exists.");
          }  
        if (str.lastIndexOf(File.separatorChar) == -1) {
          bool = paramFile.createNewFile();
        } else {
          String str1 = str.substring(0, str.lastIndexOf(File.separatorChar));
          File file = new File(str1);
          if (file.exists()) {
            bool = paramFile.createNewFile();
          } else {
            file.mkdirs();
            bool = paramFile.createNewFile();
          } 
        } 
        if (bool) {
          this.m_outputFile = paramFile;
          setDestination(new FileOutputStream(this.m_outputFile));
        } 
      } catch (Exception exception) {
        throw new IOException("Cannot create a new output file. Standard out is used.");
      } finally {
        if (!bool) {
          System.err.println("Cannot create a new output file. Standard out is used.");
          this.m_outputFile = null;
        } 
      }  
  }
  
  public void setDestination(OutputStream paramOutputStream) throws IOException {
    this.m_writer = new BufferedWriter(new OutputStreamWriter(paramOutputStream));
  }
  
  public void setDirAndPrefix(String paramString1, String paramString2) {
    try {
      if (this.m_dir.equals(""))
        this.m_dir = System.getProperty("user.dir"); 
      if (this.m_prefix.equals("")) {
        setFile(new File(this.m_dir + File.separator + paramString1 + paramString2 + this.FILE_EXTENSION));
      } else {
        setFile(new File(this.m_dir + File.separator + this.m_prefix + "_" + paramString1 + paramString2 + this.FILE_EXTENSION));
      } 
      setDestination(this.m_outputFile);
    } catch (Exception exception) {
      System.err.println("File prefix and/or directory could not have been set.");
      exception.printStackTrace();
    } 
  }
  
  public abstract String getFileDescription();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\AbstractFileSaver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */