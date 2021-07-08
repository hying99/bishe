package weka.core.converters;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import weka.core.Instance;
import weka.core.Instances;

public abstract class AbstractSaver implements Saver {
  protected static final int WRITE = 0;
  
  protected static final int WAIT = 1;
  
  protected static final int CANCEL = 2;
  
  protected static final int STRUCTURE_READY = 3;
  
  private Instances m_instances;
  
  protected int m_retrieval;
  
  private int m_writeMode;
  
  public void resetOptions() {
    this.m_instances = null;
    this.m_writeMode = 1;
  }
  
  public void resetStructure() {
    this.m_instances = null;
    this.m_writeMode = 1;
  }
  
  public void setRetrieval(int paramInt) {
    this.m_retrieval = paramInt;
  }
  
  protected int getRetrieval() {
    return this.m_retrieval;
  }
  
  protected void setWriteMode(int paramInt) {
    this.m_writeMode = paramInt;
  }
  
  public int getWriteMode() {
    return this.m_writeMode;
  }
  
  public void setInstances(Instances paramInstances) {
    if (this.m_retrieval == 2) {
      if (setStructure(paramInstances) == 2)
        cancel(); 
    } else {
      this.m_instances = paramInstances;
    } 
  }
  
  public Instances getInstances() {
    return this.m_instances;
  }
  
  public void setDestination(File paramFile) throws IOException {
    throw new IOException("Writing to a file not supported");
  }
  
  public void setDestination(OutputStream paramOutputStream) throws IOException {
    throw new IOException("Writing to an outputstream not supported");
  }
  
  public int setStructure(Instances paramInstances) {
    if (this.m_writeMode == 1 && paramInstances != null) {
      this.m_instances = paramInstances;
      this.m_writeMode = 3;
    } else if (paramInstances == null || this.m_writeMode != 3 || !paramInstances.equalHeaders(this.m_instances)) {
      this.m_instances = null;
      if (this.m_writeMode != 1)
        System.err.println("A structure cannot be set up during an active incremental saving process."); 
      this.m_writeMode = 2;
    } 
    return this.m_writeMode;
  }
  
  public void cancel() {
    if (this.m_writeMode == 2)
      resetOptions(); 
  }
  
  public void writeIncremental(Instance paramInstance) throws IOException {
    throw new IOException("No Incremental saving possible.");
  }
  
  public abstract void writeBatch() throws IOException;
  
  public String getFileExtension() throws Exception {
    throw new Exception("Saving in a file not supported.");
  }
  
  public void setFile(File paramFile) throws IOException {
    throw new IOException("Saving in a file not supported.");
  }
  
  public void setFilePrefix(String paramString) throws Exception {
    throw new Exception("Saving in a file not supported.");
  }
  
  public String filePrefix() throws Exception {
    throw new Exception("Saving in a file not supported.");
  }
  
  public void setDir(String paramString) throws IOException {
    throw new IOException("Saving in a file not supported.");
  }
  
  public void setDirAndPrefix(String paramString1, String paramString2) throws IOException {
    throw new IOException("Saving in a file not supported.");
  }
  
  public String retrieveDir() throws IOException {
    throw new IOException("Saving in a file not supported.");
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\AbstractSaver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */