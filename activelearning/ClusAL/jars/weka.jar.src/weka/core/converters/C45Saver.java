package weka.core.converters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class C45Saver extends AbstractFileSaver implements BatchConverter, IncrementalConverter, OptionHandler {
  public C45Saver() {
    resetOptions();
  }
  
  public String globalInfo() {
    return "Writes to a destination that is in the format used by the C4.5 algorithm.\nTherefore it outputs a names and a data file.";
  }
  
  public String getFileDescription() {
    return "C4.5 file format";
  }
  
  public void resetOptions() {
    super.resetOptions();
    setFileExtension(".names");
  }
  
  public void writeIncremental(Instance paramInstance) throws IOException {
    int i = getWriteMode();
    Instances instances = getInstances();
    PrintWriter printWriter = null;
    if (instances != null) {
      if (instances.classIndex() == -1) {
        instances.setClassIndex(instances.numAttributes() - 1);
        System.err.println("No class specified. Last attribute is used as class attribute.");
      } 
      if (instances.attribute(instances.classIndex()).isNumeric())
        throw new IOException("To save in C4.5 format the class attribute cannot be numeric."); 
    } 
    if (getRetrieval() == 1 || getRetrieval() == 0)
      throw new IOException("Batch and incremental saving cannot be mixed."); 
    if (retrieveFile() == null || getWriter() == null)
      throw new IOException("C4.5 format requires two files. Therefore no output to standard out can be generated.\nPlease specifiy output files using the -o option."); 
    printWriter = new PrintWriter(getWriter());
    if (i == 1) {
      if (instances == null) {
        setWriteMode(2);
        if (paramInstance != null)
          System.err.println("Structure(Header Information) has to be set in advance"); 
      } else {
        setWriteMode(3);
      } 
      i = getWriteMode();
    } 
    if (i == 2) {
      if (printWriter != null)
        printWriter.close(); 
      cancel();
    } 
    if (i == 3) {
      setWriteMode(0);
      byte b;
      for (b = 0; b < instances.attribute(instances.classIndex()).numValues(); b++) {
        printWriter.write(instances.attribute(instances.classIndex()).value(b));
        if (b < instances.attribute(instances.classIndex()).numValues() - 1) {
          printWriter.write(",");
        } else {
          printWriter.write(".\n");
        } 
      } 
      for (b = 0; b < instances.numAttributes(); b++) {
        if (b != instances.classIndex()) {
          printWriter.write(instances.attribute(b).name() + ": ");
          if (instances.attribute(b).isNumeric() || instances.attribute(b).isDate()) {
            printWriter.write("continuous.\n");
          } else {
            Attribute attribute = instances.attribute(b);
            for (byte b1 = 0; b1 < attribute.numValues(); b1++) {
              printWriter.write(attribute.value(b1));
              if (b1 < attribute.numValues() - 1) {
                printWriter.write(",");
              } else {
                printWriter.write(".\n");
              } 
            } 
          } 
        } 
      } 
      printWriter.flush();
      printWriter.close();
      i = getWriteMode();
      String str = retrieveFile().getAbsolutePath();
      setFileExtension(".data");
      str = str.substring(0, str.lastIndexOf('.')) + getFileExtension();
      File file = new File(str);
      try {
        setFile(file);
        setDestination(file);
      } catch (Exception exception) {
        throw new IOException("Cannot create data file, only names file created.");
      } 
      if (retrieveFile() == null || getWriter() == null)
        throw new IOException("Cannot create data file, only names file created."); 
      printWriter = new PrintWriter(getWriter());
    } 
    if (i == 0) {
      if (instances == null)
        throw new IOException("No instances information available."); 
      if (paramInstance != null) {
        for (byte b = 0; b < paramInstance.numAttributes(); b++) {
          if (b != instances.classIndex())
            if (paramInstance.isMissing(b)) {
              printWriter.write("?,");
            } else if (instances.attribute(b).isNominal() || instances.attribute(b).isString()) {
              printWriter.write(instances.attribute(b).value((int)paramInstance.value(b)) + ",");
            } else {
              printWriter.write("" + paramInstance.value(b) + ",");
            }  
        } 
        if (paramInstance.isMissing(instances.classIndex())) {
          printWriter.write("?");
        } else {
          printWriter.write(instances.attribute(instances.classIndex()).value((int)paramInstance.value(instances.classIndex())));
        } 
        printWriter.write("\n");
        this.m_incrementalCounter++;
        if (this.m_incrementalCounter > 100) {
          this.m_incrementalCounter = 0;
          printWriter.flush();
        } 
      } else {
        if (printWriter != null) {
          printWriter.flush();
          printWriter.close();
        } 
        setFileExtension(".names");
        this.m_incrementalCounter = 0;
        resetStructure();
      } 
    } 
  }
  
  public void writeBatch() throws IOException {
    Instances instances = getInstances();
    if (instances == null)
      throw new IOException("No instances to save"); 
    if (instances.classIndex() == -1) {
      instances.setClassIndex(instances.numAttributes() - 1);
      System.err.println("No class specified. Last attribute is used as class attribute.");
    } 
    if (instances.attribute(instances.classIndex()).isNumeric())
      throw new IOException("To save in C4.5 format the class attribute cannot be numeric."); 
    if (getRetrieval() == 2)
      throw new IOException("Batch and incremental saving cannot be mixed."); 
    setRetrieval(1);
    if (retrieveFile() == null || getWriter() == null)
      throw new IOException("C4.5 format requires two files. Therefore no output to standard out can be generated.\nPlease specifiy output files using the -o option."); 
    setWriteMode(0);
    setFileExtension(".names");
    PrintWriter printWriter = new PrintWriter(getWriter());
    byte b1;
    for (b1 = 0; b1 < instances.attribute(instances.classIndex()).numValues(); b1++) {
      printWriter.write(instances.attribute(instances.classIndex()).value(b1));
      if (b1 < instances.attribute(instances.classIndex()).numValues() - 1) {
        printWriter.write(",");
      } else {
        printWriter.write(".\n");
      } 
    } 
    for (b1 = 0; b1 < instances.numAttributes(); b1++) {
      if (b1 != instances.classIndex()) {
        printWriter.write(instances.attribute(b1).name() + ": ");
        if (instances.attribute(b1).isNumeric() || instances.attribute(b1).isDate()) {
          printWriter.write("continuous.\n");
        } else {
          Attribute attribute = instances.attribute(b1);
          for (byte b = 0; b < attribute.numValues(); b++) {
            printWriter.write(attribute.value(b));
            if (b < attribute.numValues() - 1) {
              printWriter.write(",");
            } else {
              printWriter.write(".\n");
            } 
          } 
        } 
      } 
    } 
    printWriter.flush();
    printWriter.close();
    String str = retrieveFile().getAbsolutePath();
    setFileExtension(".data");
    str = str.substring(0, str.lastIndexOf('.')) + getFileExtension();
    File file = new File(str);
    try {
      setFile(file);
      setDestination(retrieveFile());
    } catch (Exception exception) {
      throw new IOException("Cannot create data file, only names file created.");
    } 
    if (retrieveFile() == null || getWriter() == null)
      throw new IOException("Cannot create data file, only names file created."); 
    printWriter = new PrintWriter(getWriter());
    for (byte b2 = 0; b2 < instances.numInstances(); b2++) {
      Instance instance = instances.instance(b2);
      for (byte b = 0; b < instance.numAttributes(); b++) {
        if (b != instances.classIndex())
          if (instance.isMissing(b)) {
            printWriter.write("?,");
          } else if (instances.attribute(b).isNominal() || instances.attribute(b).isString()) {
            printWriter.write(instances.attribute(b).value((int)instance.value(b)) + ",");
          } else {
            printWriter.write("" + instance.value(b) + ",");
          }  
      } 
      if (instance.isMissing(instances.classIndex())) {
        printWriter.write("?");
      } else {
        printWriter.write(instances.attribute(instances.classIndex()).value((int)instance.value(instances.classIndex())));
      } 
      printWriter.write("\n");
    } 
    printWriter.flush();
    printWriter.close();
    setFileExtension(".names");
    setWriteMode(1);
  }
  
  public Enumeration listOptions() {
    FastVector fastVector = new FastVector(3);
    fastVector.addElement(new Option("The input file", "i", 1, "-i <the input file>"));
    fastVector.addElement(new Option("The output file", "o", 1, "-o <the output file>"));
    fastVector.addElement(new Option("The class index", "c", 1, "-c <the class index>"));
    return fastVector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('o', paramArrayOfString);
    String str2 = Utils.getOption('i', paramArrayOfString);
    String str3 = Utils.getOption('c', paramArrayOfString);
    ArffLoader arffLoader = new ArffLoader();
    resetOptions();
    if (str2.length() != 0) {
      try {
        File file = new File(str2);
        arffLoader.setFile(file);
        setInstances(arffLoader.getDataSet());
      } catch (Exception exception) {
        throw new IOException("No data set loaded. Data set has to be arff format.");
      } 
    } else {
      throw new IOException("No data set to save.");
    } 
    if (str1.length() != 0) {
      if (!str1.endsWith(getFileExtension()))
        if (str1.lastIndexOf('.') != -1) {
          str1 = str1.substring(0, str1.lastIndexOf('.')) + getFileExtension();
        } else {
          str1 = str1 + getFileExtension();
        }  
      try {
        File file = new File(str1);
        setFile(file);
      } catch (Exception exception) {
        throw new IOException("Cannot create output file.");
      } finally {
        setDestination(retrieveFile());
      } 
    } 
    if (str3.length() != 0) {
      if (str3.equals("first")) {
        getInstances().setClassIndex(0);
      } else if (str3.equals("last")) {
        getInstances().setClassIndex(getInstances().numAttributes() - 1);
      } else {
        int i = Integer.parseInt(str3);
        if (i >= 0 && i < getInstances().numAttributes()) {
          getInstances().setClassIndex(i);
        } else {
          throw new IOException("Invalid class index");
        } 
      } 
    } else {
      getInstances().setClassIndex(getInstances().numAttributes() - 1);
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[10];
    byte b = 0;
    if (retrieveFile() != null) {
      arrayOfString[b++] = "-o";
      arrayOfString[b++] = "" + retrieveFile();
    } else {
      arrayOfString[b++] = "-o";
      arrayOfString[b++] = "";
    } 
    if (getInstances() != null) {
      arrayOfString[b++] = "-i";
      arrayOfString[b++] = "" + getInstances().relationName();
      arrayOfString[b++] = "-c";
      arrayOfString[b++] = "" + getInstances().classIndex();
    } else {
      arrayOfString[b++] = "-i";
      arrayOfString[b++] = "";
      arrayOfString[b++] = "-c";
      arrayOfString[b++] = "";
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public static void main(String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer();
    try {
      C45Saver c45Saver = new C45Saver();
      stringBuffer.append("\n\nC45Saver options:\n\n");
      Enumeration enumeration = c45Saver.listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        stringBuffer.append(option.synopsis() + '\n');
        stringBuffer.append(option.description() + '\n');
      } 
      try {
        c45Saver.setOptions(paramArrayOfString);
      } catch (Exception exception) {
        System.out.println("\n" + stringBuffer);
        System.exit(1);
      } 
      c45Saver.writeBatch();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\C45Saver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */