package weka.associations;

import java.io.Serializable;
import weka.core.Instances;
import weka.core.SerializedObject;
import weka.core.Utils;

public abstract class Associator implements Cloneable, Serializable {
  public abstract void buildAssociations(Instances paramInstances) throws Exception;
  
  public static Associator forName(String paramString, String[] paramArrayOfString) throws Exception {
    return (Associator)Utils.forName(Associator.class, paramString, paramArrayOfString);
  }
  
  public static Associator[] makeCopies(Associator paramAssociator, int paramInt) throws Exception {
    if (paramAssociator == null)
      throw new Exception("No model associator set"); 
    Associator[] arrayOfAssociator = new Associator[paramInt];
    SerializedObject serializedObject = new SerializedObject(paramAssociator);
    for (byte b = 0; b < arrayOfAssociator.length; b++)
      arrayOfAssociator[b] = (Associator)serializedObject.getObject(); 
    return arrayOfAssociator;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\Associator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */