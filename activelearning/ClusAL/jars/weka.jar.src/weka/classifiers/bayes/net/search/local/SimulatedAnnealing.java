package weka.classifiers.bayes.net.search.local;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class SimulatedAnnealing extends LocalScoreSearchAlgorithm {
  double m_fTStart = 10.0D;
  
  double m_fDelta = 0.999D;
  
  int m_nRuns = 10000;
  
  boolean m_bUseArcReversal = false;
  
  int m_nSeed = 1;
  
  Random m_random;
  
  public void buildStructure(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    super.buildStructure(paramBayesNet, paramInstances);
    this.m_random = new Random(this.m_nSeed);
    double[] arrayOfDouble = new double[paramInstances.numAttributes()];
    double d1 = 0.0D;
    for (byte b1 = 0; b1 < paramInstances.numAttributes(); b1++) {
      arrayOfDouble[b1] = calcNodeScore(b1);
      d1 += arrayOfDouble[b1];
    } 
    double d2 = d1;
    BayesNet bayesNet = new BayesNet();
    bayesNet.m_Instances = paramInstances;
    bayesNet.initStructure();
    copyParentSets(bayesNet, paramBayesNet);
    double d3 = this.m_fTStart;
    for (byte b2 = 0; b2 < this.m_nRuns; b2++) {
      boolean bool = false;
      double d = 0.0D;
      while (!bool) {
        int i = Math.abs(this.m_random.nextInt()) % paramInstances.numAttributes();
        int j;
        for (j = Math.abs(this.m_random.nextInt()) % paramInstances.numAttributes(); i == j; j = Math.abs(this.m_random.nextInt()) % paramInstances.numAttributes());
        if (isArc(paramBayesNet, j, i)) {
          bool = true;
          paramBayesNet.getParentSet(j).deleteParent(i, paramInstances);
          double d4 = calcNodeScore(j);
          d = d4 - arrayOfDouble[j];
          if (d3 * Math.log((Math.abs(this.m_random.nextInt()) % 10000) / 10000.0D + 1.0E-100D) < d) {
            d1 += d;
            arrayOfDouble[j] = d4;
            continue;
          } 
          paramBayesNet.getParentSet(j).addParent(i, paramInstances);
          continue;
        } 
        if (addArcMakesSense(paramBayesNet, paramInstances, j, i)) {
          bool = true;
          double d4 = calcScoreWithExtraParent(j, i);
          d = d4 - arrayOfDouble[j];
          if (d3 * Math.log((Math.abs(this.m_random.nextInt()) % 10000) / 10000.0D + 1.0E-100D) < d) {
            paramBayesNet.getParentSet(j).addParent(i, paramInstances);
            arrayOfDouble[j] = d4;
            d1 += d;
          } 
        } 
      } 
      if (d1 > d2)
        copyParentSets(bayesNet, paramBayesNet); 
      d3 *= this.m_fDelta;
    } 
    copyParentSets(paramBayesNet, bayesNet);
  }
  
  void copyParentSets(BayesNet paramBayesNet1, BayesNet paramBayesNet2) {
    int i = paramBayesNet2.getNrOfNodes();
    for (byte b = 0; b < i; b++)
      paramBayesNet1.getParentSet(b).copy(paramBayesNet2.getParentSet(b)); 
  }
  
  public double getDelta() {
    return this.m_fDelta;
  }
  
  public double getTStart() {
    return this.m_fTStart;
  }
  
  public int getRuns() {
    return this.m_nRuns;
  }
  
  public void setDelta(double paramDouble) {
    this.m_fDelta = paramDouble;
  }
  
  public void setTStart(double paramDouble) {
    this.m_fTStart = paramDouble;
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
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tStart temperature\n", "A", 1, "-A <float>"));
    vector.addElement(new Option("\tNumber of runs\n", "U", 1, "-U <integer>"));
    vector.addElement(new Option("\tDelta temperature\n", "D", 1, "-D <float>"));
    vector.addElement(new Option("\tRandom number seed\n", "R", 1, "-R <seed>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('A', paramArrayOfString);
    if (str1.length() != 0)
      setTStart(Double.parseDouble(str1)); 
    String str2 = Utils.getOption('U', paramArrayOfString);
    if (str2.length() != 0)
      setRuns(Integer.parseInt(str2)); 
    String str3 = Utils.getOption('D', paramArrayOfString);
    if (str3.length() != 0)
      setDelta(Double.parseDouble(str3)); 
    String str4 = Utils.getOption('R', paramArrayOfString);
    if (str4.length() != 0)
      setSeed(Integer.parseInt(str4)); 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[8 + arrayOfString1.length];
    byte b1 = 0;
    arrayOfString2[b1++] = "-A";
    arrayOfString2[b1++] = "" + getTStart();
    arrayOfString2[b1++] = "-U";
    arrayOfString2[b1++] = "" + getRuns();
    arrayOfString2[b1++] = "-D";
    arrayOfString2[b1++] = "" + getDelta();
    arrayOfString2[b1++] = "-R";
    arrayOfString2[b1++] = "" + getSeed();
    for (byte b2 = 0; b2 < arrayOfString1.length; b2++)
      arrayOfString2[b1++] = arrayOfString1[b2]; 
    while (b1 < arrayOfString2.length)
      arrayOfString2[b1++] = ""; 
    return arrayOfString2;
  }
  
  public String globalInfo() {
    return "This Bayes Network learning algorithm uses the general purpose search method of simulated annealing to find a well scoring network structure.";
  }
  
  public String TStartTipText() {
    return "Sets the start temperature of the simulated annealing search. The start temperature determines the probability that a step in the 'wrong' direction in the search space is accepted. The higher the temperature, the higher the probability of acceptance.";
  }
  
  public String runsTipText() {
    return "Sets the number of iterations to be performed by the simulated annealing search.";
  }
  
  public String deltaTipText() {
    return "Sets the factor with which the temperature (and thus the acceptance probability of steps in the wrong direction in the search space) is decreased in each iteration.";
  }
  
  public String seedTipText() {
    return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\local\SimulatedAnnealing.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */