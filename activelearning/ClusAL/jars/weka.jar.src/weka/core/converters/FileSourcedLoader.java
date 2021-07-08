package weka.core.converters;

import java.io.File;
import java.io.IOException;

public interface FileSourcedLoader {
  String getFileExtension();
  
  String getFileDescription();
  
  void setFile(File paramFile) throws IOException;
  
  File getFile();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\FileSourcedLoader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */