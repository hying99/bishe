package weka.core.converters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import weka.core.Option;

public class SerializedInstancesSaver extends AbstractFileSaver implements BatchConverter, IncrementalConverter {
  public SerializedInstancesSaver() {
    resetOptions();
  }
  
  public String globalInfo() {
    return "Serializes the instances to a file with extension bsi.";
  }
  
  public String getFileDescription() {
    return "Serializes the instaces to a file";
  }
  
  public void resetOptions() {
    super.resetOptions();
    setFileExtension(".bsi");
  }
  
  public void setDestination(File paramFile) throws IOException {
    boolean bool = false;
    String str = paramFile.getAbsolutePath();
    if (retrieveFile() != null)
      try {
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
        if (bool)
          setFile(paramFile); 
      } catch (Exception exception) {
        throw new IOException("Cannot create a new output file. Standard out is used.");
      } finally {
        if (!bool) {
          System.err.println("Cannot create a new output file. Standard out is used.");
          setFile((File)null);
        } 
      }  
  }
  
  public void writeBatch() throws IOException {
    resetWriter();
    if (getRetrieval() == 2)
      throw new IOException("Batch and incremental saving cannot be mixed."); 
    if (getInstances() == null)
      throw new IOException("No instances to save"); 
    setRetrieval(1);
    if (retrieveFile() == null)
      throw new IOException("No output to standard out for serialization."); 
    setWriteMode(0);
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(retrieveFile())));
    objectOutputStream.writeObject(getInstances());
    objectOutputStream.flush();
    objectOutputStream.close();
    setWriteMode(1);
  }
  
  public static void main(String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer();
    try {
      SerializedInstancesSaver serializedInstancesSaver = new SerializedInstancesSaver();
      stringBuffer.append("\n\nSerializedInstancesSaver options:\n\n");
      Enumeration enumeration = serializedInstancesSaver.listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        stringBuffer.append(option.synopsis() + '\n');
        stringBuffer.append(option.description() + '\n');
      } 
      try {
        serializedInstancesSaver.setOptions(paramArrayOfString);
      } catch (Exception exception) {
        System.out.println("\n" + stringBuffer);
        System.exit(1);
      } 
      serializedInstancesSaver.writeBatch();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\SerializedInstancesSaver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */