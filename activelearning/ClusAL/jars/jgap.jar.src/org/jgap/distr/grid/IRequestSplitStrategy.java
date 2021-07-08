package org.jgap.distr.grid;

import java.io.Serializable;

public interface IRequestSplitStrategy extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.2 $";
  
  JGAPRequest[] split(JGAPRequest paramJGAPRequest) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\IRequestSplitStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */