package weka.classifiers.bayes.net.search.global;

import java.util.Enumeration;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;

public class TAN extends GlobalScoreSearchAlgorithm {
  public void buildStructure(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    this.m_BayesNet = paramBayesNet;
    this.m_bInitAsNaiveBayes = true;
    this.m_nMaxNrOfParents = 2;
    super.buildStructure(paramBayesNet, paramInstances);
    int i = paramInstances.numAttributes();
    int j;
    for (j = 0; j < i; j++) {
      if (j != paramInstances.classIndex())
        paramBayesNet.getParentSet(j).addParent(paramInstances.classIndex(), paramInstances); 
    } 
    j = paramInstances.classIndex();
    int[] arrayOfInt1 = new int[i - 1];
    int[] arrayOfInt2 = new int[i - 1];
    boolean[] arrayOfBoolean1 = new boolean[i];
    byte b1 = -1;
    byte b2 = -1;
    double d = 0.0D;
    byte b3;
    for (b3 = 0; b3 < i; b3++) {
      if (b3 != j)
        for (byte b = 0; b < i; b++) {
          if (b3 != b && b != j) {
            double d1 = calcScoreWithExtraParent(b3, b);
            if (b1 == -1 || d1 > d) {
              d = d1;
              b1 = b;
              b2 = b3;
            } 
          } 
        }  
    } 
    arrayOfInt1[0] = b1;
    arrayOfInt2[0] = b2;
    arrayOfBoolean1[b1] = true;
    arrayOfBoolean1[b2] = true;
    for (byte b4 = 1; b4 < i - 2; b4++) {
      b1 = -1;
      for (b3 = 0; b3 < i; b3++) {
        if (b3 != j)
          for (byte b = 0; b < i; b++) {
            if (b3 != b && b != j && (arrayOfBoolean1[b3] || arrayOfBoolean1[b]) && (!arrayOfBoolean1[b3] || !arrayOfBoolean1[b])) {
              double d1 = calcScoreWithExtraParent(b3, b);
              if (b1 == -1 || d1 > d) {
                d = d1;
                b1 = b;
                b2 = b3;
              } 
            } 
          }  
      } 
      arrayOfInt1[b4] = b1;
      arrayOfInt2[b4] = b2;
      arrayOfBoolean1[b1] = true;
      arrayOfBoolean1[b2] = true;
    } 
    boolean[] arrayOfBoolean2 = new boolean[i];
    for (byte b5 = 0; b5 < i - 2; b5++) {
      if (!arrayOfBoolean2[arrayOfInt1[b5]]) {
        paramBayesNet.getParentSet(arrayOfInt1[b5]).addParent(arrayOfInt2[b5], paramInstances);
        arrayOfBoolean2[arrayOfInt1[b5]] = true;
      } else {
        if (arrayOfBoolean2[arrayOfInt2[b5]])
          throw new Exception("Bug condition found: too many arrows"); 
        paramBayesNet.getParentSet(arrayOfInt2[b5]).addParent(arrayOfInt1[b5], paramInstances);
        arrayOfBoolean2[arrayOfInt2[b5]] = true;
      } 
    } 
  }
  
  public Enumeration listOptions() {
    return super.listOptions();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    return new String[0];
  }
  
  public String globalInfo() {
    return "This Bayes Network learning algorithm determines the maximum weight spanning tree  and returns a Naive Bayes network augmented with a tree.";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\global\TAN.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */