package weka.core.converters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import weka.core.Instance;
import weka.core.Instances;

public class ArffLoader extends AbstractLoader implements FileSourcedConverter, BatchConverter, IncrementalConverter {
  public static String FILE_EXTENSION = Instances.FILE_EXTENSION;
  
  protected transient Instances m_structure = null;
  
  protected String m_File = (new File(System.getProperty("user.dir"))).getAbsolutePath();
  
  private transient Reader m_sourceReader = null;
  
  public String globalInfo() {
    return "Reads a source that is in arff (attribute relation file format) format. ";
  }
  
  public String getFileExtension() {
    return FILE_EXTENSION;
  }
  
  public String getFileDescription() {
    return "Arff data files";
  }
  
  public void reset() {
    this.m_structure = null;
    setRetrieval(0);
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
  
  public File retrieveFile() {
    return new File(this.m_File);
  }
  
  public void setFile(File paramFile) throws IOException {
    this.m_File = paramFile.getAbsolutePath();
    setSource(paramFile);
  }
  
  public void setSource(InputStream paramInputStream) throws IOException {
    this.m_sourceReader = new BufferedReader(new InputStreamReader(paramInputStream));
  }
  
  public Instances getStructure() throws IOException {
    if (this.m_sourceReader == null)
      throw new IOException("No source has been specified"); 
    if (this.m_structure == null)
      try {
        this.m_structure = new Instances(this.m_sourceReader, 1);
      } catch (Exception exception) {
        throw new IOException("Unable to determine structure as arff.");
      }  
    return new Instances(this.m_structure, 0);
  }
  
  public Instances getDataSet() throws IOException {
    if (this.m_sourceReader == null)
      throw new IOException("No source has been specified"); 
    if (getRetrieval() == 2)
      throw new IOException("Cannot mix getting Instances in both incremental and batch modes"); 
    setRetrieval(1);
    if (this.m_structure == null)
      getStructure(); 
    while (this.m_structure.readInstance(this.m_sourceReader));
    return new Instances(this.m_structure);
  }
  
  public Instance getNextInstance() throws IOException {
    if (this.m_structure == null)
      getStructure(); 
    if (getRetrieval() == 1)
      throw new IOException("Cannot mix getting Instances in both incremental and batch modes"); 
    setRetrieval(2);
    if (!this.m_structure.readInstance(this.m_sourceReader))
      return null; 
    Instance instance = this.m_structure.instance(0);
    this.m_structure.delete(0);
    return instance;
  }
  
  public static void main(String[] paramArrayOfString) {
    if (paramArrayOfString.length > 0) {
      File file = new File(paramArrayOfString[0]);
      try {
        Instance instance;
        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setSource(file);
        System.out.println(arffLoader.getStructure());
        do {
          instance = arffLoader.getNextInstance();
          if (instance == null)
            continue; 
          System.out.println(instance);
        } while (instance != null);
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } else {
      System.err.println("Usage:\n\tArffLoader <file.arff>\n");
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\ArffLoader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */