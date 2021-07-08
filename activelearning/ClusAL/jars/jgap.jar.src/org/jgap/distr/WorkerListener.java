package org.jgap.distr;

import java.io.IOException;

public abstract class WorkerListener {
  private static final String CVS_REVISION = "$Revision: 1.4 $";
  
  public abstract void listen() throws IOException;
  
  public abstract void stop();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\WorkerListener.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */