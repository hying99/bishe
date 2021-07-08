package weka.core.converters;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;

public class ArffSaver extends AbstractFileSaver implements BatchConverter, IncrementalConverter {
  public ArffSaver() {
    resetOptions();
  }
  
  public String globalInfo() {
    return "Writes to a destination that is in arff (attribute relation file format) format. ";
  }
  
  public String getFileDescription() {
    return "Arff data files";
  }
  
  public void resetOptions() {
    super.resetOptions();
    setFileExtension(".arff");
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
      Instances instances1 = new Instances(instances, 0);
      if (retrieveFile() == null || printWriter == null) {
        System.out.println(instances1.toString());
      } else {
        printWriter.print(instances1.toString());
        printWriter.print("\n");
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
      System.out.println(getInstances().toString());
      setWriteMode(1);
      return;
    } 
    PrintWriter printWriter = new PrintWriter(getWriter());
    printWriter.print(getInstances().toString());
    printWriter.flush();
    printWriter.close();
    setWriteMode(1);
  }
  
  public static void main(String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer();
    try {
      ArffSaver arffSaver = new ArffSaver();
      stringBuffer.append("\n\nArffSaver options:\n\n");
      Enumeration enumeration = arffSaver.listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        stringBuffer.append(option.synopsis() + '\n');
        stringBuffer.append(option.description() + '\n');
      } 
      try {
        arffSaver.setOptions(paramArrayOfString);
      } catch (Exception exception) {
        System.out.println("\n" + stringBuffer);
        System.exit(1);
      } 
      arffSaver.writeBatch();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\ArffSaver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */