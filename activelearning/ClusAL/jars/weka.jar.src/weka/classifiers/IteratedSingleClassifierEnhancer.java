package weka.classifiers;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public abstract class IteratedSingleClassifierEnhancer extends SingleClassifierEnhancer {
  protected Classifier[] m_Classifiers;
  
  protected int m_NumIterations = 10;
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (this.m_Classifier == null)
      throw new Exception("A base classifier has not been specified!"); 
    this.m_Classifiers = Classifier.makeCopies(this.m_Classifier, this.m_NumIterations);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tNumber of iterations.\n\t(default 10)", "I", 1, "-I <num>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('I', paramArrayOfString);
    if (str.length() != 0) {
      setNumIterations(Integer.parseInt(str));
    } else {
      setNumIterations(10);
    } 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 2];
    byte b = 0;
    arrayOfString2[b++] = "-I";
    arrayOfString2[b++] = "" + getNumIterations();
    System.arraycopy(arrayOfString1, 0, arrayOfString2, b, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public String numIterationsTipText() {
    return "The number of iterations to be performed.";
  }
  
  public void setNumIterations(int paramInt) {
    this.m_NumIterations = paramInt;
  }
  
  public int getNumIterations() {
    return this.m_NumIterations;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\IteratedSingleClassifierEnhancer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */