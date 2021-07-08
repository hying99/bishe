package weka.classifiers;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SerializedObject;
import weka.core.Utils;

public abstract class Classifier implements Cloneable, Serializable, OptionHandler {
  protected boolean m_Debug = false;
  
  public abstract void buildClassifier(Instances paramInstances) throws Exception;
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    double d;
    byte b1;
    byte b2;
    double[] arrayOfDouble = distributionForInstance(paramInstance);
    if (arrayOfDouble == null)
      throw new Exception("Null distribution predicted"); 
    switch (paramInstance.classAttribute().type()) {
      case 1:
        d = 0.0D;
        b1 = 0;
        for (b2 = 0; b2 < arrayOfDouble.length; b2++) {
          if (arrayOfDouble[b2] > d) {
            b1 = b2;
            d = arrayOfDouble[b2];
          } 
        } 
        return (d > 0.0D) ? b1 : Instance.missingValue();
      case 0:
        return arrayOfDouble[0];
    } 
    return Instance.missingValue();
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double d;
    double[] arrayOfDouble = new double[paramInstance.numClasses()];
    switch (paramInstance.classAttribute().type()) {
      case 1:
        d = classifyInstance(paramInstance);
        if (Instance.isMissingValue(d))
          return arrayOfDouble; 
        arrayOfDouble[(int)d] = 1.0D;
        return arrayOfDouble;
      case 0:
        arrayOfDouble[0] = classifyInstance(paramInstance);
        return arrayOfDouble;
    } 
    return arrayOfDouble;
  }
  
  public static Classifier forName(String paramString, String[] paramArrayOfString) throws Exception {
    return (Classifier)Utils.forName(Classifier.class, paramString, paramArrayOfString);
  }
  
  public static Classifier makeCopy(Classifier paramClassifier) throws Exception {
    return (Classifier)(new SerializedObject(paramClassifier)).getObject();
  }
  
  public static Classifier[] makeCopies(Classifier paramClassifier, int paramInt) throws Exception {
    if (paramClassifier == null)
      throw new Exception("No model classifier set"); 
    Classifier[] arrayOfClassifier = new Classifier[paramInt];
    SerializedObject serializedObject = new SerializedObject(paramClassifier);
    for (byte b = 0; b < arrayOfClassifier.length; b++)
      arrayOfClassifier[b] = (Classifier)serializedObject.getObject(); 
    return arrayOfClassifier;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tIf set, classifier is run in debug mode and\n\tmay output additional info to the console", "D", 0, "-D"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setDebug(Utils.getFlag('D', paramArrayOfString));
  }
  
  public String[] getOptions() {
    String[] arrayOfString;
    if (getDebug()) {
      arrayOfString = new String[1];
      arrayOfString[0] = "-D";
    } else {
      arrayOfString = new String[0];
    } 
    return arrayOfString;
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public String debugTipText() {
    return "If set to true, classifier may output additional info to the console.";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\Classifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */