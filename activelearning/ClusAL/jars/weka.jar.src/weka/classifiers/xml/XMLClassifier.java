package weka.classifiers.xml;

import weka.classifiers.Classifier;
import weka.core.xml.XMLBasicSerialization;

public class XMLClassifier extends XMLBasicSerialization {
  public void clear() throws Exception {
    super.clear();
    this.m_Properties.addAllowed(Classifier.class, "debug");
    this.m_Properties.addAllowed(Classifier.class, "options");
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\xml\XMLClassifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */