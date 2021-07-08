package weka.core.converters;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import weka.core.Instance;
import weka.core.Instances;

public class SerializedInstancesLoader extends AbstractLoader implements FileSourcedConverter, BatchConverter, IncrementalConverter {
  public static String FILE_EXTENSION = Instances.SERIALIZED_OBJ_FILE_EXTENSION;
  
  protected String m_File = (new File(System.getProperty("user.dir"))).getAbsolutePath();
  
  protected Instances m_Dataset = null;
  
  protected int m_IncrementalIndex = 0;
  
  public void reset() {
    this.m_Dataset = null;
    this.m_IncrementalIndex = 0;
  }
  
  public String getFileExtension() {
    return FILE_EXTENSION;
  }
  
  public String getFileDescription() {
    return "Binary serialized instances";
  }
  
  public File retrieveFile() {
    return new File(this.m_File);
  }
  
  public void setFile(File paramFile) throws IOException {
    this.m_File = paramFile.getAbsolutePath();
    setSource(paramFile);
  }
  
  public void setSource(File paramFile) throws IOException {
    reset();
    if (paramFile == null)
      throw new IOException("Source file object is null!"); 
    try {
      setSource(new FileInputStream(paramFile));
    } catch (FileNotFoundException fileNotFoundException) {
      throw new IOException("File not found");
    } 
  }
  
  public void setSource(InputStream paramInputStream) throws IOException {
    ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(paramInputStream));
    try {
      this.m_Dataset = (Instances)objectInputStream.readObject();
    } catch (ClassNotFoundException classNotFoundException) {
      throw new IOException("Could not deserialize instances from this source.");
    } 
  }
  
  public Instances getStructure() throws IOException {
    if (this.m_Dataset == null)
      throw new IOException("No source has been specified"); 
    return new Instances(this.m_Dataset, 0);
  }
  
  public Instances getDataSet() throws IOException {
    if (this.m_Dataset == null)
      throw new IOException("No source has been specified"); 
    return this.m_Dataset;
  }
  
  public Instance getNextInstance() throws IOException {
    if (this.m_Dataset == null)
      throw new IOException("No source has been specified"); 
    return (this.m_IncrementalIndex == this.m_Dataset.numInstances()) ? null : this.m_Dataset.instance(this.m_IncrementalIndex++);
  }
  
  public static void main(String[] paramArrayOfString) {
    if (paramArrayOfString.length > 0) {
      File file = new File(paramArrayOfString[0]);
      try {
        Instance instance;
        SerializedInstancesLoader serializedInstancesLoader = new SerializedInstancesLoader();
        serializedInstancesLoader.setSource(file);
        System.out.println(serializedInstancesLoader.getStructure());
        do {
          instance = serializedInstancesLoader.getNextInstance();
          if (instance == null)
            continue; 
          System.out.println(instance);
        } while (instance != null);
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } else {
      System.err.println("Usage:\n\tSerializedInstancesLoader <file>\n");
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\SerializedInstancesLoader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */