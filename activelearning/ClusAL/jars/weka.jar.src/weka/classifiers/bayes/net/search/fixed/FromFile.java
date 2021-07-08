package weka.classifiers.bayes.net.search.fixed;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.BIFReader;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class FromFile extends SearchAlgorithm {
  String m_sBIFFile = "";
  
  public void buildStructure(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    BIFReader bIFReader = new BIFReader();
    bIFReader.processFile(this.m_sBIFFile);
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      int i = bIFReader.getNode(paramBayesNet.getNodeName(b));
      ParentSet parentSet = bIFReader.getParentSet(i);
      for (byte b1 = 0; b1 < parentSet.getNrOfParents(); b1++) {
        String str = bIFReader.getNodeName(parentSet.getParent(b1));
        byte b2;
        for (b2 = 0; b2 < paramInstances.numAttributes() && !paramBayesNet.getNodeName(b2).equals(str); b2++);
        if (b2 >= paramInstances.numAttributes())
          throw new Exception("Could not find attribute " + str + " from BIF file in data"); 
        paramBayesNet.getParentSet(b).addParent(b2, paramInstances);
      } 
    } 
  }
  
  public void setBIFFile(String paramString) {
    this.m_sBIFFile = paramString;
  }
  
  public String getBIFFile() {
    return this.m_sBIFFile;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(0);
    vector.addElement(new Option("\tName of file containing network structure in BIF format\n", "B", 1, "-B <BIF File>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setBIFFile(Utils.getOption('B', paramArrayOfString));
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[2];
    byte b = 0;
    arrayOfString[b++] = "-B";
    arrayOfString[b++] = "" + getBIFFile();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\fixed\FromFile.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */