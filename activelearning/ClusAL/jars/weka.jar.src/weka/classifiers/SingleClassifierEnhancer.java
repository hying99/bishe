package weka.classifiers;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.rules.ZeroR;
import weka.core.Option;
import weka.core.Utils;

public abstract class SingleClassifierEnhancer extends Classifier {
  protected Classifier m_Classifier = (Classifier)new ZeroR();
  
  protected String defaultClassifierString() {
    return "weka.classifiers.rules.ZeroR";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    vector.addElement(new Option("\tFull name of base classifier.\n\t(default: " + defaultClassifierString() + ")", "W", 1, "-W"));
    vector.addElement(new Option("", "", 0, "\nOptions specific to classifier " + this.m_Classifier.getClass().getName() + ":"));
    enumeration = this.m_Classifier.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    super.setOptions(paramArrayOfString);
    String str = Utils.getOption('W', paramArrayOfString);
    if (str.length() > 0) {
      setClassifier(Classifier.forName(str, Utils.partitionOptions(paramArrayOfString)));
    } else {
      setClassifier((Classifier)Class.forName(defaultClassifierString()).newInstance());
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = this.m_Classifier.getOptions();
    int i = arrayOfString1.length;
    if (i > 0)
      i++; 
    String[] arrayOfString2 = super.getOptions();
    String[] arrayOfString3 = new String[arrayOfString2.length + i + 2];
    int j = 0;
    arrayOfString3[j++] = "-W";
    arrayOfString3[j++] = getClassifier().getClass().getName();
    System.arraycopy(arrayOfString2, 0, arrayOfString3, j, arrayOfString2.length);
    j += arrayOfString2.length;
    if (arrayOfString1.length > 0) {
      arrayOfString3[j++] = "--";
      System.arraycopy(arrayOfString1, 0, arrayOfString3, j, arrayOfString1.length);
    } 
    return arrayOfString3;
  }
  
  public String classifierTipText() {
    return "The base classifier to be used.";
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_Classifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_Classifier;
  }
  
  protected String getClassifierSpec() {
    Classifier classifier = getClassifier();
    return classifier.getClass().getName() + " " + Utils.joinOptions(classifier.getOptions());
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\SingleClassifierEnhancer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */