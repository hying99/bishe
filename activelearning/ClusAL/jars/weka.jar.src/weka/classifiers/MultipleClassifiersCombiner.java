package weka.classifiers;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.rules.ZeroR;
import weka.core.Option;
import weka.core.Utils;

public abstract class MultipleClassifiersCombiner extends Classifier {
  protected Classifier[] m_Classifiers = new Classifier[] { (Classifier)new ZeroR() };
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tFull class name of classifier to include, followed\n\tby scheme options. May be specified multiple times.\n\t(default: \"weka.classifiers.rules.ZeroR\")", "B", 1, "-B <classifier specification>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    Vector vector = new Vector();
    while (true) {
      Classifier[] arrayOfClassifier;
      String str1 = Utils.getOption('B', paramArrayOfString);
      if (str1.length() == 0) {
        if (vector.size() == 0)
          vector.addElement(new ZeroR()); 
        arrayOfClassifier = new Classifier[vector.size()];
        for (byte b = 0; b < arrayOfClassifier.length; b++)
          arrayOfClassifier[b] = (Classifier)vector.elementAt(b); 
        setClassifiers(arrayOfClassifier);
        return;
      } 
      String[] arrayOfString = Utils.splitOptions((String)arrayOfClassifier);
      if (arrayOfString.length == 0)
        throw new IllegalArgumentException("Invalid classifier specification string"); 
      String str2 = arrayOfString[0];
      arrayOfString[0] = "";
      vector.addElement(Classifier.forName(str2, arrayOfString));
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    byte b1 = 0;
    String[] arrayOfString2 = new String[arrayOfString1.length + this.m_Classifiers.length * 2];
    for (byte b2 = 0; b2 < this.m_Classifiers.length; b2++) {
      arrayOfString2[b1++] = "-B";
      arrayOfString2[b1++] = "" + getClassifierSpec(b2);
    } 
    System.arraycopy(arrayOfString1, 0, arrayOfString2, b1, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public String classifiersTipText() {
    return "The base classifiers to be used.";
  }
  
  public void setClassifiers(Classifier[] paramArrayOfClassifier) {
    this.m_Classifiers = paramArrayOfClassifier;
  }
  
  public Classifier[] getClassifiers() {
    return this.m_Classifiers;
  }
  
  public Classifier getClassifier(int paramInt) {
    return this.m_Classifiers[paramInt];
  }
  
  protected String getClassifierSpec(int paramInt) {
    if (this.m_Classifiers.length < paramInt)
      return ""; 
    Classifier classifier = getClassifier(paramInt);
    return classifier.getClass().getName() + " " + Utils.joinOptions(classifier.getOptions());
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\MultipleClassifiersCombiner.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */