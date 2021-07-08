package weka.classifiers.bayes.net.search.global;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class GeneticSearch extends GlobalScoreSearchAlgorithm {
  int m_nRuns = 10;
  
  int m_nPopulationSize = 10;
  
  int m_nDescendantPopulationSize = 100;
  
  boolean m_bUseCrossOver = true;
  
  boolean m_bUseMutation = true;
  
  boolean m_bUseTournamentSelection = false;
  
  int m_nSeed = 1;
  
  Random m_random = null;
  
  static boolean[] g_bIsSquare;
  
  protected void search(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    if (getDescendantPopulationSize() < getPopulationSize())
      throw new Exception("Descendant PopulationSize should be at least Population Size"); 
    if (!getUseCrossOver() && !getUseMutation())
      throw new Exception("At least one of mutation or cross-over should be used"); 
    this.m_random = new Random(this.m_nSeed);
    double d = calcScore(paramBayesNet);
    BayesNet bayesNet = new BayesNet();
    bayesNet.m_Instances = paramInstances;
    bayesNet.initStructure();
    copyParentSets(bayesNet, paramBayesNet);
    BayesNetRepresentation[] arrayOfBayesNetRepresentation = new BayesNetRepresentation[getPopulationSize()];
    double[] arrayOfDouble = new double[getPopulationSize()];
    byte b;
    for (b = 0; b < getPopulationSize(); b++) {
      arrayOfBayesNetRepresentation[b] = new BayesNetRepresentation(this, paramInstances.numAttributes());
      arrayOfBayesNetRepresentation[b].randomInit();
      if (arrayOfBayesNetRepresentation[b].getScore() > d) {
        copyParentSets(bayesNet, paramBayesNet);
        d = arrayOfBayesNetRepresentation[b].getScore();
      } 
    } 
    for (b = 0; b < this.m_nRuns; b++) {
      BayesNetRepresentation[] arrayOfBayesNetRepresentation1 = new BayesNetRepresentation[getDescendantPopulationSize()];
      for (byte b1 = 0; b1 < getDescendantPopulationSize(); b1++) {
        arrayOfBayesNetRepresentation1[b1] = arrayOfBayesNetRepresentation[this.m_random.nextInt(getPopulationSize())].copy();
        if (getUseMutation()) {
          if (getUseCrossOver() && this.m_random.nextBoolean()) {
            arrayOfBayesNetRepresentation1[b1].crossOver(arrayOfBayesNetRepresentation[this.m_random.nextInt(getPopulationSize())]);
          } else {
            arrayOfBayesNetRepresentation1[b1].mutate();
          } 
        } else {
          arrayOfBayesNetRepresentation1[b1].crossOver(arrayOfBayesNetRepresentation[this.m_random.nextInt(getPopulationSize())]);
        } 
        if (arrayOfBayesNetRepresentation1[b1].getScore() > d) {
          copyParentSets(bayesNet, paramBayesNet);
          d = arrayOfBayesNetRepresentation1[b1].getScore();
        } 
      } 
      boolean[] arrayOfBoolean = new boolean[getDescendantPopulationSize()];
      for (byte b2 = 0; b2 < getPopulationSize(); b2++) {
        int i = 0;
        if (this.m_bUseTournamentSelection) {
          for (i = this.m_random.nextInt(getDescendantPopulationSize()); arrayOfBoolean[i]; i = (i + 1) % getDescendantPopulationSize());
          int j;
          for (j = this.m_random.nextInt(getDescendantPopulationSize()); arrayOfBoolean[j]; j = (j + 1) % getDescendantPopulationSize());
          if (arrayOfBayesNetRepresentation1[j].getScore() > arrayOfBayesNetRepresentation1[i].getScore())
            i = j; 
        } else {
          while (arrayOfBoolean[i])
            i++; 
          double d1 = arrayOfBayesNetRepresentation1[i].getScore();
          for (byte b3 = 0; b3 < getDescendantPopulationSize(); b3++) {
            if (!arrayOfBoolean[b3] && arrayOfBayesNetRepresentation1[b3].getScore() > d1) {
              d1 = arrayOfBayesNetRepresentation1[b3].getScore();
              i = b3;
            } 
          } 
        } 
        arrayOfBayesNetRepresentation[b2] = arrayOfBayesNetRepresentation1[i];
        arrayOfBoolean[i] = true;
      } 
    } 
    copyParentSets(paramBayesNet, bayesNet);
    bayesNet = null;
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
  
  public Enumeration listOptions() {
    Vector vector = new Vector(7);
    vector.addElement(new Option("\tPopulation size\n", "L", 1, "-L <integer>"));
    vector.addElement(new Option("\tDescendant population size\n", "A", 1, "-A <integer>"));
    vector.addElement(new Option("\tNumber of runs\n", "U", 1, "-U <integer>"));
    vector.addElement(new Option("\tUse mutation.\n\t(default true)", "M", 0, "-M"));
    vector.addElement(new Option("\tUse cross-over.\n\t(default true)", "C", 0, "-C"));
    vector.addElement(new Option("\tUse tournament selection (true) or maximum subpopulatin (false).\n\t(default false)", "O", 0, "-O"));
    vector.addElement(new Option("\tRandom number seed\n", "R", 1, "-R <seed>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('L', paramArrayOfString);
    if (str1.length() != 0)
      setPopulationSize(Integer.parseInt(str1)); 
    String str2 = Utils.getOption('A', paramArrayOfString);
    if (str2.length() != 0)
      setDescendantPopulationSize(Integer.parseInt(str2)); 
    String str3 = Utils.getOption('U', paramArrayOfString);
    if (str3.length() != 0)
      setRuns(Integer.parseInt(str3)); 
    String str4 = Utils.getOption('R', paramArrayOfString);
    if (str4.length() != 0)
      setSeed(Integer.parseInt(str4)); 
    setUseMutation(Utils.getFlag('M', paramArrayOfString));
    setUseCrossOver(Utils.getFlag('C', paramArrayOfString));
    setUseTournamentSelection(Utils.getFlag('O', paramArrayOfString));
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[11 + arrayOfString1.length];
    byte b1 = 0;
    arrayOfString2[b1++] = "-L";
    arrayOfString2[b1++] = "" + getPopulationSize();
    arrayOfString2[b1++] = "-A";
    arrayOfString2[b1++] = "" + getDescendantPopulationSize();
    arrayOfString2[b1++] = "-U";
    arrayOfString2[b1++] = "" + getRuns();
    arrayOfString2[b1++] = "-R";
    arrayOfString2[b1++] = "" + getSeed();
    if (getUseMutation())
      arrayOfString2[b1++] = "-M"; 
    if (getUseCrossOver())
      arrayOfString2[b1++] = "-C"; 
    if (getUseTournamentSelection())
      arrayOfString2[b1++] = "-O"; 
    for (byte b2 = 0; b2 < arrayOfString1.length; b2++)
      arrayOfString2[b1++] = arrayOfString1[b2]; 
    while (b1 < arrayOfString2.length)
      arrayOfString2[b1++] = ""; 
    return arrayOfString2;
  }
  
  public boolean getUseCrossOver() {
    return this.m_bUseCrossOver;
  }
  
  public boolean getUseMutation() {
    return this.m_bUseMutation;
  }
  
  public int getDescendantPopulationSize() {
    return this.m_nDescendantPopulationSize;
  }
  
  public int getPopulationSize() {
    return this.m_nPopulationSize;
  }
  
  public void setUseCrossOver(boolean paramBoolean) {
    this.m_bUseCrossOver = paramBoolean;
  }
  
  public void setUseMutation(boolean paramBoolean) {
    this.m_bUseMutation = paramBoolean;
  }
  
  public boolean getUseTournamentSelection() {
    return this.m_bUseTournamentSelection;
  }
  
  public void setUseTournamentSelection(boolean paramBoolean) {
    this.m_bUseTournamentSelection = paramBoolean;
  }
  
  public void setDescendantPopulationSize(int paramInt) {
    this.m_nDescendantPopulationSize = paramInt;
  }
  
  public void setPopulationSize(int paramInt) {
    this.m_nPopulationSize = paramInt;
  }
  
  public int getSeed() {
    return this.m_nSeed;
  }
  
  public void setSeed(int paramInt) {
    this.m_nSeed = paramInt;
  }
  
  public String globalInfo() {
    return "This Bayes Network learning algorithm uses genetic search for finding a well scoring Bayes network structure. Genetic search works by having a population of Bayes network structures and allow them to mutate and apply cross over to get offspring. The best network structure found during the process is returned.";
  }
  
  public String runsTipText() {
    return "Sets the number of generations of Bayes network structure populations.";
  }
  
  public String seedTipText() {
    return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
  }
  
  public String populationSizeTipText() {
    return "Sets the size of the population of network structures that is selected each generation.";
  }
  
  public String descendantPopulationSizeTipText() {
    return "Sets the size of the population of descendants that is created each generation.";
  }
  
  public String useMutationTipText() {
    return "Determines whether mutation is allowed. Mutation flips a bit in the bit representation of the network structure. At least one of mutation or cross-over should be used.";
  }
  
  public String useCrossOverTipText() {
    return "Determines whether cross-over is allowed. Cross over combined the bit representations of network structure by taking a random first k bits of oneand adding the remainder of the other. At least one of mutation or cross-over should be used.";
  }
  
  public String useTournamentSelectionTipText() {
    return "Determines the method of selecting a population. When set to true, tournament selection is used (pick two at random and the highest is allowed to continue). When set to false, the top scoring network structures are selected.";
  }
  
  class BayesNetRepresentation {
    int m_nNodes;
    
    boolean[] m_bits;
    
    double m_fScore;
    
    private final GeneticSearch this$0;
    
    public double getScore() {
      return this.m_fScore;
    }
    
    BayesNetRepresentation(GeneticSearch this$0, int param1Int) {
      this.this$0 = this$0;
      this.m_nNodes = 0;
      this.m_fScore = 0.0D;
      this.m_nNodes = param1Int;
    }
    
    public void randomInit() {
      while (true) {
        this.m_bits = new boolean[this.m_nNodes * this.m_nNodes];
        byte b = 0;
        while (b < this.m_nNodes) {
          while (true) {
            int i = this.this$0.m_random.nextInt(this.m_nNodes * this.m_nNodes);
            if (!isSquare(i)) {
              this.m_bits[i] = true;
              b++;
            } 
          } 
        } 
        if (!hasCycles()) {
          calcGlobalScore();
          return;
        } 
      } 
    }
    
    void calcGlobalScore() {
      byte b;
      for (b = 0; b < this.m_nNodes; b++) {
        ParentSet parentSet = this.this$0.m_BayesNet.getParentSet(b);
        while (parentSet.getNrOfParents() > 0)
          parentSet.deleteLastParent(this.this$0.m_BayesNet.m_Instances); 
      } 
      for (b = 0; b < this.m_nNodes; b++) {
        ParentSet parentSet = this.this$0.m_BayesNet.getParentSet(b);
        for (byte b1 = 0; b1 < this.m_nNodes; b1++) {
          if (this.m_bits[b1 + b * this.m_nNodes])
            parentSet.addParent(b1, this.this$0.m_BayesNet.m_Instances); 
        } 
      } 
      try {
        this.m_fScore = this.this$0.calcScore(this.this$0.m_BayesNet);
      } catch (Exception exception) {}
    }
    
    public boolean hasCycles() {
      boolean[] arrayOfBoolean = new boolean[this.m_nNodes];
      for (byte b = 0; b < this.m_nNodes; b++) {
        boolean bool = false;
        for (byte b1 = 0; !bool && b1 < this.m_nNodes; b1++) {
          if (!arrayOfBoolean[b1]) {
            boolean bool1 = true;
            for (byte b2 = 0; b2 < this.m_nNodes; b2++) {
              if (this.m_bits[b2 + b1 * this.m_nNodes] && !arrayOfBoolean[b2])
                bool1 = false; 
            } 
            if (bool1) {
              arrayOfBoolean[b1] = true;
              bool = true;
            } 
          } 
        } 
        if (!bool)
          return true; 
      } 
      return false;
    }
    
    BayesNetRepresentation copy() {
      BayesNetRepresentation bayesNetRepresentation = new BayesNetRepresentation(this.this$0, this.m_nNodes);
      bayesNetRepresentation.m_bits = new boolean[this.m_bits.length];
      for (byte b = 0; b < this.m_nNodes * this.m_nNodes; b++)
        bayesNetRepresentation.m_bits[b] = this.m_bits[b]; 
      bayesNetRepresentation.m_fScore = this.m_fScore;
      return bayesNetRepresentation;
    }
    
    void mutate() {
      while (true) {
        int i = this.this$0.m_random.nextInt(this.m_nNodes * this.m_nNodes);
        if (!isSquare(i)) {
          this.m_bits[i] = !this.m_bits[i];
          if (!hasCycles()) {
            calcGlobalScore();
            return;
          } 
        } 
      } 
    }
    
    void crossOver(BayesNetRepresentation param1BayesNetRepresentation) {
      boolean[] arrayOfBoolean = new boolean[this.m_bits.length];
      int i;
      for (i = 0; i < this.m_bits.length; i++)
        arrayOfBoolean[i] = this.m_bits[i]; 
      i = this.m_bits.length;
      while (true) {
        int j;
        for (j = i; j < this.m_bits.length; j++)
          this.m_bits[j] = arrayOfBoolean[j]; 
        i = this.this$0.m_random.nextInt(this.m_bits.length);
        for (j = i; j < this.m_bits.length; j++)
          this.m_bits[j] = param1BayesNetRepresentation.m_bits[j]; 
        if (!hasCycles()) {
          calcGlobalScore();
          return;
        } 
      } 
    }
    
    boolean isSquare(int param1Int) {
      if (GeneticSearch.g_bIsSquare == null || GeneticSearch.g_bIsSquare.length < param1Int) {
        GeneticSearch.g_bIsSquare = new boolean[this.m_nNodes * this.m_nNodes];
        for (byte b = 0; b < this.m_nNodes; b++)
          GeneticSearch.g_bIsSquare[b * this.m_nNodes + b] = true; 
      } 
      return GeneticSearch.g_bIsSquare[param1Int];
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\global\GeneticSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */