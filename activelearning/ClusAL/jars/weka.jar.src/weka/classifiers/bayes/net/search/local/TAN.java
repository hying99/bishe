package weka.classifiers.bayes.net.search.local;

import java.util.Enumeration;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;

public class TAN extends LocalScoreSearchAlgorithm {
  public void buildStructure(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    this.m_bInitAsNaiveBayes = true;
    this.m_nMaxNrOfParents = 2;
    super.buildStructure(paramBayesNet, paramInstances);
    int i = paramInstances.numAttributes();
    for (byte b1 = 0; b1 < i; b1++) {
      if (b1 != paramInstances.classIndex())
        paramBayesNet.getParentSet(b1).addParent(paramInstances.classIndex(), paramInstances); 
    } 
    double[] arrayOfDouble = new double[paramInstances.numAttributes()];
    for (byte b2 = 0; b2 < i; b2++)
      arrayOfDouble[b2] = calcNodeScore(b2); 
    double[][] arrayOfDouble1 = new double[i][i];
    int j;
    for (j = 0; j < i; j++) {
      for (byte b = 0; b < i; b++) {
        if (j != b)
          arrayOfDouble1[j][b] = calcScoreWithExtraParent(j, b); 
      } 
    } 
    j = paramInstances.classIndex();
    int[] arrayOfInt1 = new int[i - 1];
    int[] arrayOfInt2 = new int[i - 1];
    boolean[] arrayOfBoolean1 = new boolean[i];
    byte b3 = -1;
    byte b4 = -1;
    double d = 0.0D;
    byte b5;
    for (b5 = 0; b5 < i; b5++) {
      if (b5 != j)
        for (byte b = 0; b < i; b++) {
          if (b5 != b && b != j && (b3 == -1 || arrayOfDouble1[b5][b] - arrayOfDouble[b5] > d)) {
            d = arrayOfDouble1[b5][b] - arrayOfDouble[b5];
            b3 = b;
            b4 = b5;
          } 
        }  
    } 
    arrayOfInt1[0] = b3;
    arrayOfInt2[0] = b4;
    arrayOfBoolean1[b3] = true;
    arrayOfBoolean1[b4] = true;
    for (byte b6 = 1; b6 < i - 2; b6++) {
      b3 = -1;
      for (b5 = 0; b5 < i; b5++) {
        if (b5 != j)
          for (byte b = 0; b < i; b++) {
            if (b5 != b && b != j && (arrayOfBoolean1[b5] || arrayOfBoolean1[b]) && (!arrayOfBoolean1[b5] || !arrayOfBoolean1[b]) && (b3 == -1 || arrayOfDouble1[b5][b] - arrayOfDouble[b5] > d)) {
              d = arrayOfDouble1[b5][b] - arrayOfDouble[b5];
              b3 = b;
              b4 = b5;
            } 
          }  
      } 
      arrayOfInt1[b6] = b3;
      arrayOfInt2[b6] = b4;
      arrayOfBoolean1[b3] = true;
      arrayOfBoolean1[b4] = true;
    } 
    boolean[] arrayOfBoolean2 = new boolean[i];
    for (byte b7 = 0; b7 < i - 2; b7++) {
      if (!arrayOfBoolean2[arrayOfInt1[b7]]) {
        paramBayesNet.getParentSet(arrayOfInt1[b7]).addParent(arrayOfInt2[b7], paramInstances);
        arrayOfBoolean2[arrayOfInt1[b7]] = true;
      } else {
        if (arrayOfBoolean2[arrayOfInt2[b7]])
          throw new Exception("Bug condition found: too many arrows"); 
        paramBayesNet.getParentSet(arrayOfInt2[b7]).addParent(arrayOfInt1[b7], paramInstances);
        arrayOfBoolean2[arrayOfInt2[b7]] = true;
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\local\TAN.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */