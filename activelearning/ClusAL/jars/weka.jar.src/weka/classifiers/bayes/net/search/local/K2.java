package weka.classifiers.bayes.net.search.local;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class K2 extends LocalScoreSearchAlgorithm {
  boolean m_bRandomOrder = false;
  
  public void buildStructure(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    super.buildStructure(paramBayesNet, paramInstances);
    int[] arrayOfInt = new int[paramInstances.numAttributes()];
    arrayOfInt[0] = paramInstances.classIndex();
    byte b1 = 0;
    for (byte b2 = 1; b2 < paramInstances.numAttributes(); b2++) {
      if (b1 == paramInstances.classIndex())
        b1++; 
      arrayOfInt[b2] = b1++;
    } 
    if (this.m_bRandomOrder) {
      byte b;
      Random random = new Random();
      if (getInitAsNaiveBayes()) {
        b = 0;
      } else {
        b = -1;
      } 
      for (byte b4 = 0; b4 < paramInstances.numAttributes(); b4++) {
        int i = Math.abs(random.nextInt()) % paramInstances.numAttributes();
        if (b4 != b && i != b) {
          int j = arrayOfInt[b4];
          arrayOfInt[b4] = arrayOfInt[i];
          arrayOfInt[i] = j;
        } 
      } 
    } 
    double[] arrayOfDouble = new double[paramInstances.numAttributes()];
    byte b3;
    for (b3 = 0; b3 < paramInstances.numAttributes(); b3++) {
      int i = arrayOfInt[b3];
      arrayOfDouble[i] = calcNodeScore(i);
    } 
    for (b3 = 1; b3 < paramInstances.numAttributes(); b3++) {
      int i = arrayOfInt[b3];
      double d = arrayOfDouble[i];
      boolean bool;
      for (bool = (paramBayesNet.getParentSet(i).getNrOfParents() < getMaxNrOfParents()) ? true : false; bool; bool = false) {
        int j = -1;
        for (byte b = 0; b < b3; b++) {
          int k = arrayOfInt[b];
          double d1 = calcScoreWithExtraParent(i, k);
          if (d1 > d) {
            d = d1;
            j = k;
          } 
        } 
        if (j != -1) {
          paramBayesNet.getParentSet(i).addParent(j, paramInstances);
          arrayOfDouble[i] = d;
          bool = (paramBayesNet.getParentSet(i).getNrOfParents() < getMaxNrOfParents()) ? true : false;
          continue;
        } 
      } 
    } 
  }
  
  public void setMaxNrOfParents(int paramInt) {
    this.m_nMaxNrOfParents = paramInt;
  }
  
  public int getMaxNrOfParents() {
    return this.m_nMaxNrOfParents;
  }
  
  public void setInitAsNaiveBayes(boolean paramBoolean) {
    this.m_bInitAsNaiveBayes = paramBoolean;
  }
  
  public boolean getInitAsNaiveBayes() {
    return this.m_bInitAsNaiveBayes;
  }
  
  public void setRandomOrder(boolean paramBoolean) {
    this.m_bRandomOrder = paramBoolean;
  }
  
  public boolean getRandomOrder() {
    return this.m_bRandomOrder;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(0);
    vector.addElement(new Option("\tInitial structure is empty (instead of Naive Bayes)\n", "N", 0, "-N"));
    vector.addElement(new Option("\tMaximum number of parents\n", "P", 1, "-P <nr of parents>"));
    vector.addElement(new Option("\tRandom order.\n\t(default false)", "R", 0, "-R"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setRandomOrder(Utils.getFlag('R', paramArrayOfString));
    this.m_bInitAsNaiveBayes = !Utils.getFlag('N', paramArrayOfString);
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0) {
      setMaxNrOfParents(Integer.parseInt(str));
    } else {
      setMaxNrOfParents(100000);
    } 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[4];
    byte b = 0;
    if (this.m_nMaxNrOfParents != 10000) {
      arrayOfString[b++] = "-P";
      arrayOfString[b++] = "" + this.m_nMaxNrOfParents;
    } 
    if (!this.m_bInitAsNaiveBayes)
      arrayOfString[b++] = "-N"; 
    if (getRandomOrder())
      arrayOfString[b++] = "-R"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String globalInfo() {
    return "This Bayes Network learning algorithm uses a hill climbing algorithm restricted by an order on the variables";
  }
  
  public String randomOrderTipText() {
    return "When set to true, the order of the nodes in the network is random. Default random order is false and the order of the nodes in the dataset is used. In any case, when the network was initialized as Naive Bayes Network, the class variable is first in the ordering though.";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\local\K2.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */