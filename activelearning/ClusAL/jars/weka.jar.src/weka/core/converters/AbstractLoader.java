package weka.core.converters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import weka.core.Instance;
import weka.core.Instances;

public abstract class AbstractLoader implements Loader {
  protected static final int NONE = 0;
  
  protected static final int BATCH = 1;
  
  protected static final int INCREMENTAL = 2;
  
  protected int m_retrieval;
  
  protected void setRetrieval(int paramInt) {
    this.m_retrieval = paramInt;
  }
  
  protected int getRetrieval() {
    return this.m_retrieval;
  }
  
  public void setSource(File paramFile) throws IOException {
    throw new IOException("Setting File as source not supported");
  }
  
  public void setSource(InputStream paramInputStream) throws IOException {
    throw new IOException("Setting InputStream as source not supported");
  }
  
  public abstract Instances getStructure() throws IOException;
  
  public abstract Instances getDataSet() throws IOException;
  
  public abstract Instance getNextInstance() throws IOException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\AbstractLoader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */