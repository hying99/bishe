package weka.classifiers.functions.supportVector;

import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;

public abstract class Kernel implements Serializable {
  protected Instances m_data;
  
  public abstract double eval(int paramInt1, int paramInt2, Instance paramInstance) throws Exception;
  
  public abstract void clean();
  
  public abstract int numEvals();
  
  public abstract int numCacheHits();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\supportVector\Kernel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */