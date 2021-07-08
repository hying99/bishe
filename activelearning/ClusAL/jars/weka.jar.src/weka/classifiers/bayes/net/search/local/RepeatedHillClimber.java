package weka.classifiers.bayes.net.search.local;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class RepeatedHillClimber extends HillClimber {
  int m_nRuns = 10;
  
  int m_nSeed = 1;
  
  Random m_random;
  
  protected void search(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    this.m_random = new Random(getSeed());
    double d2 = 0.0D;
    for (byte b1 = 0; b1 < paramInstances.numAttributes(); b1++)
      d2 += calcNodeScore(b1); 
    double d1 = d2;
    BayesNet bayesNet = new BayesNet();
    bayesNet.m_Instances = paramInstances;
    bayesNet.initStructure();
    copyParentSets(bayesNet, paramBayesNet);
    for (byte b2 = 0; b2 < this.m_nRuns; b2++) {
      generateRandomNet(paramBayesNet, paramInstances);
      super.search(paramBayesNet, paramInstances);
      d2 = 0.0D;
      for (byte b = 0; b < paramInstances.numAttributes(); b++)
        d2 += calcNodeScore(b); 
      if (d2 > d1) {
        d1 = d2;
        copyParentSets(bayesNet, paramBayesNet);
      } 
    } 
    copyParentSets(paramBayesNet, bayesNet);
    bayesNet = null;
    this.m_Cache = null;
  }
  
  void generateRandomNet(BayesNet paramBayesNet, Instances paramInstances) {
    int i = paramInstances.numAttributes();
    int j;
    for (j = 0; j < i; j++) {
      ParentSet parentSet = paramBayesNet.getParentSet(j);
      while (parentSet.getNrOfParents() > 0)
        parentSet.deleteLastParent(paramInstances); 
    } 
    if (getInitAsNaiveBayes()) {
      j = paramInstances.classIndex();
      for (byte b1 = 0; b1 < i; b1++) {
        if (b1 != j)
          paramBayesNet.getParentSet(b1).addParent(j, paramInstances); 
      } 
    } 
    j = this.m_random.nextInt(i * i);
    for (byte b = 0; b < j; b++) {
      int k = this.m_random.nextInt(i);
      int m = this.m_random.nextInt(i);
      if (paramBayesNet.getParentSet(m).getNrOfParents() < getMaxNrOfParents() && addArcMakesSense(paramBayesNet, paramInstances, m, k))
        paramBayesNet.getParentSet(m).addParent(k, paramInstances); 
    } 
  }
  
  void copyParentSets(BayesNet paramBayesNet1, BayesNet paramBayesNet2) {
    int i = paramBayesNet2.getNrOfNodes();
    for (byte b = 0; b < i; b++)
      paramBayesNet1.getParentSet(b).copy(paramBayesNet2.getParentSet(b)); 
  }
  
  public int getRuns() {
    return this.m_nRuns;
  }
  
  public void setRuns(int paramInt) {
    this.m_nRuns = paramInt;
  }
  
  public int getSeed() {
    return this.m_nSeed;
  }
  
  public void setSeed(int paramInt) {
    this.m_nSeed = paramInt;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tNumber of runs\n", "U", 1, "-U <integer>"));
    vector.addElement(new Option("\tRandom number seed\n", "R", 1, "-R <seed>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('U', paramArrayOfString);
    if (str1.length() != 0)
      setRuns(Integer.parseInt(str1)); 
    String str2 = Utils.getOption('R', paramArrayOfString);
    if (str2.length() != 0)
      setSeed(Integer.parseInt(str2)); 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[7 + arrayOfString1.length];
    byte b1 = 0;
    arrayOfString2[b1++] = "-U";
    arrayOfString2[b1++] = "" + getRuns();
    arrayOfString2[b1++] = "-R";
    arrayOfString2[b1++] = "" + getSeed();
    for (byte b2 = 0; b2 < arrayOfString1.length; b2++)
      arrayOfString2[b1++] = arrayOfString1[b2]; 
    while (b1 < arrayOfString2.length)
      arrayOfString2[b1++] = ""; 
    return arrayOfString2;
  }
  
  public String globalInfo() {
    return "This Bayes Network learning algorithm repeatedly uses hill climbing starting with a randomly generated network structure and return the best structure of the various runs.";
  }
  
  public String runsTipText() {
    return "Sets the number of times hill climbing is performed.";
  }
  
  public String seedTipText() {
    return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\local\RepeatedHillClimber.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */