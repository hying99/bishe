package weka.attributeSelection;

import java.io.Serializable;
import weka.core.Instances;
import weka.core.SerializedObject;
import weka.core.Utils;

public abstract class ASEvaluation implements Serializable {
  public abstract void buildEvaluator(Instances paramInstances) throws Exception;
  
  public int[] postProcess(int[] paramArrayOfint) throws Exception {
    return paramArrayOfint;
  }
  
  public static ASEvaluation forName(String paramString, String[] paramArrayOfString) throws Exception {
    return (ASEvaluation)Utils.forName(ASEvaluation.class, paramString, paramArrayOfString);
  }
  
  public static ASEvaluation[] makeCopies(ASEvaluation paramASEvaluation, int paramInt) throws Exception {
    if (paramASEvaluation == null)
      throw new Exception("No model evaluator set"); 
    ASEvaluation[] arrayOfASEvaluation = new ASEvaluation[paramInt];
    SerializedObject serializedObject = new SerializedObject(paramASEvaluation);
    for (byte b = 0; b < arrayOfASEvaluation.length; b++)
      arrayOfASEvaluation[b] = (ASEvaluation)serializedObject.getObject(); 
    return arrayOfASEvaluation;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\ASEvaluation.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */