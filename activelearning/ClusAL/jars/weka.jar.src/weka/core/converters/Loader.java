package weka.core.converters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;

public interface Loader extends Serializable {
  void setSource(File paramFile) throws IOException;
  
  void setSource(InputStream paramInputStream) throws IOException;
  
  Instances getStructure() throws IOException;
  
  Instances getDataSet() throws IOException;
  
  Instance getNextInstance() throws IOException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\Loader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */