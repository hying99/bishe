package weka.attributeSelection;

import java.io.Serializable;
import weka.core.Instances;
import weka.core.Utils;

public abstract class ASSearch implements Serializable {
  public abstract int[] search(ASEvaluation paramASEvaluation, Instances paramInstances) throws Exception;
  
  public static ASSearch forName(String paramString, String[] paramArrayOfString) throws Exception {
    return (ASSearch)Utils.forName(ASSearch.class, paramString, paramArrayOfString);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\ASSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */