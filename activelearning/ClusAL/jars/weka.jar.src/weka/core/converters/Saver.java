package weka.core.converters;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;

public interface Saver extends Serializable {
  public static final int NONE = 0;
  
  public static final int BATCH = 1;
  
  public static final int INCREMENTAL = 2;
  
  void setDestination(File paramFile) throws IOException;
  
  void setDestination(OutputStream paramOutputStream) throws IOException;
  
  void setRetrieval(int paramInt);
  
  String getFileExtension() throws Exception;
  
  void setFile(File paramFile) throws IOException;
  
  void setFilePrefix(String paramString) throws Exception;
  
  String filePrefix() throws Exception;
  
  void setDir(String paramString) throws IOException;
  
  void setDirAndPrefix(String paramString1, String paramString2) throws IOException;
  
  String retrieveDir() throws IOException;
  
  void setInstances(Instances paramInstances);
  
  void writeBatch() throws IOException;
  
  void writeIncremental(Instance paramInstance) throws IOException;
  
  int getWriteMode();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\Saver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */