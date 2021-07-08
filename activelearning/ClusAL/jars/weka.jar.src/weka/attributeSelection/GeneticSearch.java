package weka.attributeSelection;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.Utils;

public class GeneticSearch extends ASSearch implements StartSetHandler, OptionHandler {
  private int[] m_starting;
  
  private Range m_startRange;
  
  private boolean m_hasClass;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private GABitSet[] m_population;
  
  private int m_popSize;
  
  private GABitSet m_best;
  
  private int m_bestFeatureCount;
  
  private int m_lookupTableSize;
  
  private Hashtable m_lookupTable;
  
  private Random m_random;
  
  private int m_seed;
  
  private double m_pCrossover;
  
  private double m_pMutation;
  
  private double m_sumFitness;
  
  private double m_maxFitness;
  
  private double m_minFitness;
  
  private double m_avgFitness;
  
  private int m_maxGenerations;
  
  private int m_reportFrequency;
  
  private StringBuffer m_generationReports;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tSpecify a starting set of attributes.\n\tEg. 1,3,5-7.If supplied, the starting set becomes\n\tone member of the initial random\n\tpopulation.", "P", 1, "-P <start set>"));
    vector.addElement(new Option("\tSet the size of the population.\n\t(default = 10).", "Z", 1, "-Z <population size>"));
    vector.addElement(new Option("\tSet the number of generations.\n\t(default = 20)", "G", 1, "-G <number of generations>"));
    vector.addElement(new Option("\tSet the probability of crossover.\n\t(default = 0.6)", "C", 1, "-C <probability of crossover>"));
    vector.addElement(new Option("\tSet the probability of mutation.\n\t(default = 0.033)", "M", 1, "-M <probability of mutation>"));
    vector.addElement(new Option("\tSet frequency of generation reports.\n\te.g, setting the value to 5 will \n\t report every 5th generation\n\t(default = number of generations)", "R", 1, "-R <report frequency>"));
    vector.addElement(new Option("\tSet the random number seed.\n\t(default = 1)", "S", 1, "-S <seed>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0)
      setStartSet(str); 
    str = Utils.getOption('Z', paramArrayOfString);
    if (str.length() != 0)
      setPopulationSize(Integer.parseInt(str)); 
    str = Utils.getOption('G', paramArrayOfString);
    if (str.length() != 0) {
      setMaxGenerations(Integer.parseInt(str));
      setReportFrequency(Integer.parseInt(str));
    } 
    str = Utils.getOption('C', paramArrayOfString);
    if (str.length() != 0)
      setCrossoverProb((new Double(str)).doubleValue()); 
    str = Utils.getOption('M', paramArrayOfString);
    if (str.length() != 0)
      setMutationProb((new Double(str)).doubleValue()); 
    str = Utils.getOption('R', paramArrayOfString);
    if (str.length() != 0)
      setReportFrequency(Integer.parseInt(str)); 
    str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0)
      setSeed(Integer.parseInt(str)); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[14];
    byte b = 0;
    if (!getStartSet().equals("")) {
      arrayOfString[b++] = "-P";
      arrayOfString[b++] = "" + startSetToString();
    } 
    arrayOfString[b++] = "-Z";
    arrayOfString[b++] = "" + getPopulationSize();
    arrayOfString[b++] = "-G";
    arrayOfString[b++] = "" + getMaxGenerations();
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + getCrossoverProb();
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getMutationProb();
    arrayOfString[b++] = "-R";
    arrayOfString[b++] = "" + getReportFrequency();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSeed();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String startSetTipText() {
    return "Set a start point for the search. This is specified as a comma seperated list off attribute indexes starting at 1. It can include ranges. Eg. 1,2,5-9,17. The start set becomes one of the population members of the initial population.";
  }
  
  public void setStartSet(String paramString) throws Exception {
    this.m_startRange.setRanges(paramString);
  }
  
  public String getStartSet() {
    return this.m_startRange.getRanges();
  }
  
  public String seedTipText() {
    return "Set the random seed.";
  }
  
  public void setSeed(int paramInt) {
    this.m_seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_seed;
  }
  
  public String reportFrequencyTipText() {
    return "Set how frequently reports are generated. Default is equal to the number of generations meaning that a report will be printed for initial and final generations. Setting the value to 5 will result in a report being printed every 5 generations.";
  }
  
  public void setReportFrequency(int paramInt) {
    this.m_reportFrequency = paramInt;
  }
  
  public int getReportFrequency() {
    return this.m_reportFrequency;
  }
  
  public String mutationProbTipText() {
    return "Set the probability of mutation occuring.";
  }
  
  public void setMutationProb(double paramDouble) {
    this.m_pMutation = paramDouble;
  }
  
  public double getMutationProb() {
    return this.m_pMutation;
  }
  
  public String crossoverProbTipText() {
    return "Set the probability of crossover. This is the probability that two population members will exchange genetic material.";
  }
  
  public void setCrossoverProb(double paramDouble) {
    this.m_pCrossover = paramDouble;
  }
  
  public double getCrossoverProb() {
    return this.m_pCrossover;
  }
  
  public String maxGenerationsTipText() {
    return "Set the number of generations to evaluate.";
  }
  
  public void setMaxGenerations(int paramInt) {
    this.m_maxGenerations = paramInt;
  }
  
  public int getMaxGenerations() {
    return this.m_maxGenerations;
  }
  
  public String populationSizeTipText() {
    return "Set the population size. This is the number of individuals (attribute sets) in the population.";
  }
  
  public void setPopulationSize(int paramInt) {
    this.m_popSize = paramInt;
  }
  
  public int getPopulationSize() {
    return this.m_popSize;
  }
  
  public String globalInfo() {
    return "GeneticSearch :\n\nPerforms a search using the simple genetic algorithm described in Goldberg (1989).\n";
  }
  
  public GeneticSearch() {
    resetOptions();
  }
  
  private String startSetToString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_starting == null)
      return getStartSet(); 
    for (byte b = 0; b < this.m_starting.length; b++) {
      boolean bool = false;
      if (!this.m_hasClass || (this.m_hasClass == true && b != this.m_classIndex)) {
        stringBuffer.append(this.m_starting[b] + 1);
        bool = true;
      } 
      if (b == this.m_starting.length - 1) {
        stringBuffer.append("");
      } else if (bool) {
        stringBuffer.append(",");
      } 
    } 
    return stringBuffer.toString();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\tGenetic search.\n\tStart set: ");
    if (this.m_starting == null) {
      stringBuffer.append("no attributes\n");
    } else {
      stringBuffer.append(startSetToString() + "\n");
    } 
    stringBuffer.append("\tPopulation size: " + this.m_popSize);
    stringBuffer.append("\n\tNumber of generations: " + this.m_maxGenerations);
    stringBuffer.append("\n\tProbability of crossover: " + Utils.doubleToString(this.m_pCrossover, 6, 3));
    stringBuffer.append("\n\tProbability of mutation: " + Utils.doubleToString(this.m_pMutation, 6, 3));
    stringBuffer.append("\n\tReport frequency: " + this.m_reportFrequency);
    stringBuffer.append("\n\tRandom number seed: " + this.m_seed + "\n");
    stringBuffer.append(this.m_generationReports.toString());
    return stringBuffer.toString();
  }
  
  public int[] search(ASEvaluation paramASEvaluation, Instances paramInstances) throws Exception {
    this.m_best = null;
    this.m_generationReports = new StringBuffer();
    if (!(paramASEvaluation instanceof SubsetEvaluator))
      throw new Exception(paramASEvaluation.getClass().getName() + " is not a " + "Subset evaluator!"); 
    if (paramASEvaluation instanceof UnsupervisedSubsetEvaluator) {
      this.m_hasClass = false;
    } else {
      this.m_hasClass = true;
      this.m_classIndex = paramInstances.classIndex();
    } 
    SubsetEvaluator subsetEvaluator = (SubsetEvaluator)paramASEvaluation;
    this.m_numAttribs = paramInstances.numAttributes();
    this.m_startRange.setUpper(this.m_numAttribs - 1);
    if (!getStartSet().equals(""))
      this.m_starting = this.m_startRange.getSelection(); 
    this.m_lookupTable = new Hashtable(this.m_lookupTableSize);
    this.m_random = new Random(this.m_seed);
    this.m_population = new GABitSet[this.m_popSize];
    initPopulation();
    evaluatePopulation(subsetEvaluator);
    populationStatistics();
    scalePopulation();
    checkBest();
    this.m_generationReports.append(populationReport(0));
    for (byte b = 1; b <= this.m_maxGenerations; b++) {
      generation();
      evaluatePopulation(subsetEvaluator);
      populationStatistics();
      scalePopulation();
      boolean bool = checkBest();
      if (b == this.m_maxGenerations || b % this.m_reportFrequency == 0 || bool == true) {
        this.m_generationReports.append(populationReport(b));
        if (bool == true)
          break; 
      } 
    } 
    return attributeList(this.m_best.getChromosome());
  }
  
  private int[] attributeList(BitSet paramBitSet) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_numAttribs; b2++) {
      if (paramBitSet.get(b2))
        b1++; 
    } 
    int[] arrayOfInt = new int[b1];
    b1 = 0;
    for (byte b3 = 0; b3 < this.m_numAttribs; b3++) {
      if (paramBitSet.get(b3))
        arrayOfInt[b1++] = b3; 
    } 
    return arrayOfInt;
  }
  
  private boolean checkBest() throws Exception {
    int j = this.m_numAttribs;
    double d = -1.7976931348623157E308D;
    GABitSet gABitSet = null;
    boolean bool = false;
    int k = Integer.MAX_VALUE;
    if (this.m_maxFitness - this.m_minFitness > 0.0D) {
      for (byte b = 0; b < this.m_popSize; b++) {
        if (this.m_population[b].getObjective() > d) {
          d = this.m_population[b].getObjective();
          gABitSet = this.m_population[b];
          k = countFeatures(gABitSet.getChromosome());
        } else if (Utils.eq(this.m_population[b].getObjective(), d)) {
          int m = countFeatures(this.m_population[b].getChromosome());
          if (m < k) {
            d = this.m_population[b].getObjective();
            gABitSet = this.m_population[b];
            k = m;
          } 
        } 
      } 
    } else {
      for (byte b = 0; b < this.m_popSize; b++) {
        BitSet bitSet1 = this.m_population[b].getChromosome();
        int m = countFeatures(bitSet1);
        if (m < j) {
          j = m;
          gABitSet = this.m_population[b];
          d = gABitSet.getObjective();
        } 
      } 
      bool = true;
    } 
    int i = 0;
    BitSet bitSet = gABitSet.getChromosome();
    i = countFeatures(bitSet);
    if (this.m_best == null) {
      this.m_best = (GABitSet)gABitSet.clone();
      this.m_bestFeatureCount = i;
    } else if (d > this.m_best.getObjective()) {
      this.m_best = (GABitSet)gABitSet.clone();
      this.m_bestFeatureCount = i;
    } else if (Utils.eq(this.m_best.getObjective(), d) && i < this.m_bestFeatureCount) {
      this.m_best = (GABitSet)gABitSet.clone();
      this.m_bestFeatureCount = i;
    } 
    return bool;
  }
  
  private int countFeatures(BitSet paramBitSet) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_numAttribs; b2++) {
      if (paramBitSet.get(b2))
        b1++; 
    } 
    return b1;
  }
  
  private void generation() throws Exception {
    byte b2 = 0;
    double d = -1.7976931348623157E308D;
    int i = 0;
    GABitSet[] arrayOfGABitSet = new GABitSet[this.m_popSize];
    byte b1;
    for (b1 = 0; b1 < this.m_popSize; b1++) {
      if (this.m_population[b1].getFitness() > d) {
        b2 = b1;
        d = this.m_population[b1].getFitness();
        i = countFeatures(this.m_population[b1].getChromosome());
      } else if (Utils.eq(this.m_population[b1].getFitness(), d)) {
        int j = countFeatures(this.m_population[b1].getChromosome());
        if (j < i) {
          b2 = b1;
          d = this.m_population[b1].getFitness();
          i = j;
        } 
      } 
    } 
    arrayOfGABitSet[0] = (GABitSet)this.m_population[b2].clone();
    arrayOfGABitSet[1] = arrayOfGABitSet[0];
    for (b2 = 2; b2 < this.m_popSize; b2 += 2) {
      int j = select();
      int k = select();
      arrayOfGABitSet[b2] = (GABitSet)this.m_population[j].clone();
      arrayOfGABitSet[b2 + 1] = (GABitSet)this.m_population[k].clone();
      if (j == k) {
        int m;
        if (this.m_hasClass) {
          while ((m = Math.abs(this.m_random.nextInt()) % this.m_numAttribs) == this.m_classIndex);
        } else {
          m = this.m_random.nextInt() % this.m_numAttribs;
        } 
        if (arrayOfGABitSet[b2].get(m)) {
          arrayOfGABitSet[b2].clear(m);
        } else {
          arrayOfGABitSet[b2].set(m);
        } 
      } else {
        double d1 = this.m_random.nextDouble();
        if (this.m_numAttribs >= 3 && d1 < this.m_pCrossover) {
          int m = Math.abs(this.m_random.nextInt());
          m %= this.m_numAttribs - 2;
          m++;
          for (b1 = 0; b1 < m; b1++) {
            if (this.m_population[j].get(b1)) {
              arrayOfGABitSet[b2 + 1].set(b1);
            } else {
              arrayOfGABitSet[b2 + 1].clear(b1);
            } 
            if (this.m_population[k].get(b1)) {
              arrayOfGABitSet[b2].set(b1);
            } else {
              arrayOfGABitSet[b2].clear(b1);
            } 
          } 
        } 
        for (byte b = 0; b < 2; b++) {
          for (b1 = 0; b1 < this.m_numAttribs; b1++) {
            d1 = this.m_random.nextDouble();
            if (d1 < this.m_pMutation && (!this.m_hasClass || b1 != this.m_classIndex))
              if (arrayOfGABitSet[b2 + b].get(b1)) {
                arrayOfGABitSet[b2 + b].clear(b1);
              } else {
                arrayOfGABitSet[b2 + b].set(b1);
              }  
          } 
        } 
      } 
    } 
    this.m_population = arrayOfGABitSet;
  }
  
  private int select() {
    double d2 = 0.0D;
    double d1 = this.m_random.nextDouble() * this.m_sumFitness;
    byte b;
    for (b = 0; b < this.m_popSize; b++) {
      d2 += this.m_population[b].getFitness();
      if (d2 >= d1)
        break; 
    } 
    return b;
  }
  
  private void evaluatePopulation(SubsetEvaluator paramSubsetEvaluator) throws Exception {
    for (byte b = 0; b < this.m_popSize; b++) {
      if (!this.m_lookupTable.containsKey(this.m_population[b].getChromosome())) {
        double d = paramSubsetEvaluator.evaluateSubset(this.m_population[b].getChromosome());
        this.m_population[b].setObjective(d);
        this.m_lookupTable.put(this.m_population[b].getChromosome(), this.m_population[b]);
      } else {
        GABitSet gABitSet = (GABitSet)this.m_lookupTable.get(this.m_population[b].getChromosome());
        this.m_population[b].setObjective(gABitSet.getObjective());
      } 
    } 
  }
  
  private void initPopulation() throws Exception {
    byte b2 = 0;
    if (this.m_starting != null) {
      this.m_population[0] = new GABitSet(this);
      for (byte b = 0; b < this.m_starting.length; b++) {
        if (this.m_starting[b] != this.m_classIndex)
          this.m_population[0].set(this.m_starting[b]); 
      } 
      b2 = 1;
    } 
    for (byte b1 = b2; b1 < this.m_popSize; b1++) {
      this.m_population[b1] = new GABitSet(this);
      int i = this.m_random.nextInt();
      i = i % this.m_numAttribs - 1;
      if (i < 0)
        i *= -1; 
      if (i == 0)
        i = 1; 
      for (byte b = 0; b < i; b++) {
        int j;
        boolean bool = false;
        while (true) {
          j = this.m_random.nextInt();
          if (j < 0)
            j *= -1; 
          j %= this.m_numAttribs;
          if (this.m_hasClass) {
            if (j != this.m_classIndex)
              bool = true; 
          } else {
            bool = true;
          } 
          if (bool) {
            if (j > this.m_numAttribs)
              throw new Exception("Problem in population init"); 
            break;
          } 
        } 
        this.m_population[b1].set(j);
      } 
    } 
  }
  
  private void populationStatistics() {
    this.m_sumFitness = this.m_minFitness = this.m_maxFitness = this.m_population[0].getObjective();
    for (byte b = 1; b < this.m_popSize; b++) {
      this.m_sumFitness += this.m_population[b].getObjective();
      if (this.m_population[b].getObjective() > this.m_maxFitness) {
        this.m_maxFitness = this.m_population[b].getObjective();
      } else if (this.m_population[b].getObjective() < this.m_minFitness) {
        this.m_minFitness = this.m_population[b].getObjective();
      } 
    } 
    this.m_avgFitness = this.m_sumFitness / this.m_popSize;
  }
  
  private void scalePopulation() {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 2.0D;
    if (this.m_minFitness > (d3 * this.m_avgFitness - this.m_maxFitness) / (d3 - 1.0D)) {
      double d = this.m_maxFitness - this.m_avgFitness;
      d1 = (d3 - 1.0D) * this.m_avgFitness / d;
      d2 = this.m_avgFitness * (this.m_maxFitness - d3 * this.m_avgFitness) / d;
    } else {
      double d = this.m_avgFitness - this.m_minFitness;
      d1 = this.m_avgFitness / d;
      d2 = -this.m_minFitness * this.m_avgFitness / d;
    } 
    this.m_sumFitness = 0.0D;
    for (byte b = 0; b < this.m_popSize; b++) {
      if (d1 == Double.POSITIVE_INFINITY || d1 == Double.NEGATIVE_INFINITY || d2 == Double.POSITIVE_INFINITY || d2 == Double.NEGATIVE_INFINITY) {
        this.m_population[b].setFitness(this.m_population[b].getObjective());
      } else {
        this.m_population[b].setFitness(Math.abs(d1 * this.m_population[b].getObjective() + d2));
      } 
      this.m_sumFitness += this.m_population[b].getFitness();
    } 
  }
  
  private String populationReport(int paramInt) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramInt == 0) {
      stringBuffer.append("\nInitial population\n");
    } else {
      stringBuffer.append("\nGeneration: " + paramInt + "\n");
    } 
    stringBuffer.append("merit   \tscaled  \tsubset\n");
    for (byte b = 0; b < this.m_popSize; b++) {
      stringBuffer.append(Utils.doubleToString(Math.abs(this.m_population[b].getObjective()), 8, 5) + "\t" + Utils.doubleToString(this.m_population[b].getFitness(), 8, 5) + "\t");
      stringBuffer.append(printPopMember(this.m_population[b].getChromosome()) + "\n");
    } 
    return stringBuffer.toString();
  }
  
  private String printPopMember(BitSet paramBitSet) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.m_numAttribs; b++) {
      if (paramBitSet.get(b))
        stringBuffer.append((b + 1) + " "); 
    } 
    return stringBuffer.toString();
  }
  
  private String printPopChrom(BitSet paramBitSet) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.m_numAttribs; b++) {
      if (paramBitSet.get(b)) {
        stringBuffer.append("1");
      } else {
        stringBuffer.append("0");
      } 
    } 
    return stringBuffer.toString();
  }
  
  private void resetOptions() {
    this.m_population = null;
    this.m_popSize = 20;
    this.m_lookupTableSize = 1001;
    this.m_pCrossover = 0.6D;
    this.m_pMutation = 0.033D;
    this.m_maxGenerations = 20;
    this.m_reportFrequency = this.m_maxGenerations;
    this.m_starting = null;
    this.m_startRange = new Range();
    this.m_seed = 1;
  }
  
  protected class GABitSet implements Cloneable, Serializable {
    private BitSet m_chromosome;
    
    private double m_objective;
    
    private double m_fitness;
    
    private final GeneticSearch this$0;
    
    public GABitSet(GeneticSearch this$0) {
      this.this$0 = this$0;
      this.m_objective = -1.7976931348623157E308D;
      this.m_chromosome = new BitSet();
    }
    
    public Object clone() throws CloneNotSupportedException {
      GABitSet gABitSet = new GABitSet(this.this$0);
      gABitSet.setObjective(getObjective());
      gABitSet.setFitness(getFitness());
      gABitSet.setChromosome((BitSet)this.m_chromosome.clone());
      return gABitSet;
    }
    
    public void setObjective(double param1Double) {
      this.m_objective = param1Double;
    }
    
    public double getObjective() {
      return this.m_objective;
    }
    
    public void setFitness(double param1Double) {
      this.m_fitness = param1Double;
    }
    
    public double getFitness() {
      return this.m_fitness;
    }
    
    public BitSet getChromosome() {
      return this.m_chromosome;
    }
    
    public void setChromosome(BitSet param1BitSet) {
      this.m_chromosome = param1BitSet;
    }
    
    public void clear(int param1Int) {
      this.m_chromosome.clear(param1Int);
    }
    
    public void set(int param1Int) {
      this.m_chromosome.set(param1Int);
    }
    
    public boolean get(int param1Int) {
      return this.m_chromosome.get(param1Int);
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\GeneticSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */