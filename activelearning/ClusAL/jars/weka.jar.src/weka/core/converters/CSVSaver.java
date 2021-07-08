package weka.core.converters;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;

public class CSVSaver extends AbstractFileSaver implements BatchConverter, IncrementalConverter, FileSourcedConverter {
  public CSVSaver() {
    resetOptions();
  }
  
  public String globalInfo() {
    return "Writes to a destination that is in csv format";
  }
  
  public String getFileDescription() {
    return "CSV file: comma separated files";
  }
  
  public void resetOptions() {
    super.resetOptions();
    setFileExtension(".csv");
  }
  
  public void writeIncremental(Instance paramInstance) throws IOException {
    int i = getWriteMode();
    Instances instances = getInstances();
    PrintWriter printWriter = null;
    if (getRetrieval() == 1 || getRetrieval() == 0)
      throw new IOException("Batch and incremental saving cannot be mixed."); 
    if (getWriter() != null)
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
      if (retrieveFile() == null || printWriter == null) {
        for (byte b = 0; b < instances.numAttributes(); b++) {
          System.out.print(instances.attribute(b).name());
          if (b < instances.numAttributes() - 1) {
            System.out.print(",");
          } else {
            System.out.println();
          } 
        } 
      } else {
        for (byte b = 0; b < instances.numAttributes(); b++) {
          printWriter.print(instances.attribute(b).name());
          if (b < instances.numAttributes() - 1) {
            printWriter.print(",");
          } else {
            printWriter.println();
          } 
        } 
        printWriter.flush();
      } 
      i = getWriteMode();
    } 
    if (i == 0) {
      if (instances == null)
        throw new IOException("No instances information available."); 
      if (paramInstance != null) {
        if (retrieveFile() == null || printWriter == null) {
          System.out.println(paramInstance);
        } else {
          printWriter.println(paramInstance);
          this.m_incrementalCounter++;
          if (this.m_incrementalCounter > 100) {
            this.m_incrementalCounter = 0;
            printWriter.flush();
          } 
        } 
      } else {
        if (printWriter != null) {
          printWriter.flush();
          printWriter.close();
        } 
        this.m_incrementalCounter = 0;
        resetStructure();
      } 
    } 
  }
  
  public void writeBatch() throws IOException {
    if (getInstances() == null)
      throw new IOException("No instances to save"); 
    if (getRetrieval() == 2)
      throw new IOException("Batch and incremental saving cannot be mixed."); 
    setRetrieval(1);
    setWriteMode(0);
    if (retrieveFile() == null || getWriter() == null) {
      byte b1;
      for (b1 = 0; b1 < getInstances().numAttributes(); b1++) {
        System.out.print(getInstances().attribute(b1).name());
        if (b1 < getInstances().numAttributes() - 1) {
          System.out.print(",");
        } else {
          System.out.println();
        } 
      } 
      for (b1 = 0; b1 < getInstances().numInstances(); b1++)
        System.out.println(getInstances().instance(b1)); 
      setWriteMode(1);
      return;
    } 
    PrintWriter printWriter = new PrintWriter(getWriter());
    byte b;
    for (b = 0; b < getInstances().numAttributes(); b++) {
      printWriter.print(getInstances().attribute(b).name());
      if (b < getInstances().numAttributes() - 1) {
        printWriter.print(",");
      } else {
        printWriter.println();
      } 
    } 
    for (b = 0; b < getInstances().numInstances(); b++)
      printWriter.println(getInstances().instance(b)); 
    printWriter.flush();
    printWriter.close();
    setWriteMode(1);
  }
  
  public static void main(String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer();
    try {
      CSVSaver cSVSaver = new CSVSaver();
      stringBuffer.append("\n\nCSVSaver options:\n\n");
      Enumeration enumeration = cSVSaver.listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        stringBuffer.append(option.synopsis() + '\n');
        stringBuffer.append(option.description() + '\n');
      } 
      try {
        cSVSaver.setOptions(paramArrayOfString);
      } catch (Exception exception) {
        System.out.println("\n" + stringBuffer);
        System.exit(1);
      } 
      cSVSaver.writeBatch();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\CSVSaver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */