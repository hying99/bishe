package weka.attributeSelection;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Statistics;
import weka.core.Tag;
import weka.core.Utils;
import weka.experiment.PairedStats;
import weka.experiment.Stats;

public class RaceSearch extends ASSearch implements RankedOutputSearch, OptionHandler {
  private Instances m_Instances = null;
  
  private static final int FORWARD_RACE = 0;
  
  private static final int BACKWARD_RACE = 1;
  
  private static final int SCHEMATA_RACE = 2;
  
  private static final int RANK_RACE = 3;
  
  public static final Tag[] TAGS_SELECTION = new Tag[] { new Tag(0, "Forward selection race"), new Tag(1, "Backward elimination race"), new Tag(2, "Schemata race"), new Tag(3, "Rank race") };
  
  private int m_raceType = 0;
  
  private static final int TEN_FOLD = 0;
  
  private static final int LEAVE_ONE_OUT = 1;
  
  public static final Tag[] XVALTAGS_SELECTION = new Tag[] { new Tag(0, "10 Fold"), new Tag(1, "Leave-one-out") };
  
  private int m_xvalType = 0;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private int m_totalEvals;
  
  private double m_bestMerit = -1.7976931348623157E308D;
  
  private HoldOutSubsetEvaluator m_theEvaluator = null;
  
  private double m_sigLevel = 0.001D;
  
  private double m_delta = 0.001D;
  
  private int m_samples = 20;
  
  private int m_numFolds = 10;
  
  private ASEvaluation m_ASEval = new GainRatioAttributeEval();
  
  private int[] m_Ranking;
  
  private boolean m_debug = false;
  
  private boolean m_rankingRequested = false;
  
  private double[][] m_rankedAtts;
  
  private int m_rankedSoFar;
  
  private int m_numToSelect = -1;
  
  private int m_calculatedNumToSelect = -1;
  
  private double m_threshold = -1.7976931348623157E308D;
  
  public String globalInfo() {
    return "Races the cross validation error of competing attribute subsets. Use in conjuction with a ClassifierSubsetEval. RaceSearch has four modes:\n\nforward selection races all single attribute additions to a base set (initially  no attributes), selects the winner to become the new base set and then iterates until there is no improvement over the base set. \n\nBackward elimination is similar but the initial base set has all attributes included and races all single attribute deletions. \n\nSchemata search is a bit different. Each iteration a series of races are run in parallel. Each race in a set determines whether a particular attribute should be included or not---ie the race is between the attribute being \"in\" or \"out\". The other attributes for this race are included or excluded randomly at each point in the evaluation. As soon as one race has a clear winner (ie it has been decided whether a particular attribute should be inor not) then the next set of races begins, using the result of the winning race from the previous iteration as new base set.\n\nRank race first ranks the attributes using an attribute evaluator and then races the ranking. The race includes no attributes, the top ranked attribute, the top two attributes, the top three attributes, etc.\n\nIt is also possible to generate a raked list of attributes through the forward racing process. If generateRanking is set to true then a complete forward race will be run---that is, racing continues until all attributes have been selected. The order that they are added in determines a complete ranking of all the attributes.\n\nRacing uses paired and unpaired t-tests on cross-validation errors of competing subsets. When there is a significant difference between the means of the errors of two competing subsets then the poorer of the two can be eliminated from the race. Similarly, if there is no significant difference between the mean errors of two competing subsets and they are within some threshold of each other, then one can be eliminated from the race. ";
  }
  
  public String raceTypeTipText() {
    return "Set the type of search.";
  }
  
  public void setRaceType(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_SELECTION)
      this.m_raceType = paramSelectedTag.getSelectedTag().getID(); 
    if (this.m_raceType == 2 && !this.m_rankingRequested) {
      try {
        setFoldsType(new SelectedTag(1, XVALTAGS_SELECTION));
        setSignificanceLevel(0.01D);
      } catch (Exception exception) {}
    } else {
      try {
        setFoldsType(new SelectedTag(0, XVALTAGS_SELECTION));
        setSignificanceLevel(0.001D);
      } catch (Exception exception) {}
    } 
  }
  
  public SelectedTag getRaceType() {
    return new SelectedTag(this.m_raceType, TAGS_SELECTION);
  }
  
  public String significanceLevelTipText() {
    return "Set the significance level to use for t-test comparisons.";
  }
  
  public void setSignificanceLevel(double paramDouble) {
    this.m_sigLevel = paramDouble;
  }
  
  public double getSignificanceLevel() {
    return this.m_sigLevel;
  }
  
  public String thresholdTipText() {
    return "Set the error threshold by which to consider two subsets equivalent.";
  }
  
  public void setThreshold(double paramDouble) {
    this.m_delta = paramDouble;
  }
  
  public double getThreshold() {
    return this.m_delta;
  }
  
  public String foldsTipText() {
    return "Set the number of folds to use for x-val error estimation. Leave-one-out is selected automatically for schemata search.";
  }
  
  public void setFoldsType(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == XVALTAGS_SELECTION)
      this.m_xvalType = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public SelectedTag getFoldsType() {
    return new SelectedTag(this.m_xvalType, XVALTAGS_SELECTION);
  }
  
  public String debugTipText() {
    return "Turn on verbose output for monitoring the search's progress.";
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_debug;
  }
  
  public String attributeEvaluatorTipText() {
    return "Attribute evaluator to use for generating an initial ranking. Use in conjunction with a rank race";
  }
  
  public void setAttributeEvaluator(ASEvaluation paramASEvaluation) {
    this.m_ASEval = paramASEvaluation;
  }
  
  public ASEvaluation getAttributeEvaluator() {
    return this.m_ASEval;
  }
  
  public String generateRankingTipText() {
    return "Use the racing process to generate a ranked list of attributes. Using this mode forces the race to be a forward type and then races until all attributes have been added, thus giving a ranked list";
  }
  
  public void setGenerateRanking(boolean paramBoolean) {
    this.m_rankingRequested = paramBoolean;
    if (this.m_rankingRequested)
      try {
        setRaceType(new SelectedTag(0, TAGS_SELECTION));
      } catch (Exception exception) {} 
  }
  
  public boolean getGenerateRanking() {
    return this.m_rankingRequested;
  }
  
  public String numToSelectTipText() {
    return "Specify the number of attributes to retain. Use in conjunction with generateRanking. The default value (-1) indicates that all attributes are to be retained. Use either this option or a threshold to reduce the attribute set.";
  }
  
  public void setNumToSelect(int paramInt) {
    this.m_numToSelect = paramInt;
  }
  
  public int getNumToSelect() {
    return this.m_numToSelect;
  }
  
  public int getCalculatedNumToSelect() {
    if (this.m_numToSelect >= 0)
      this.m_calculatedNumToSelect = this.m_numToSelect; 
    return this.m_calculatedNumToSelect;
  }
  
  public String selectionThresholdTipText() {
    return "Set threshold by which attributes can be discarded. Default value results in no attributes being discarded. Use in conjunction with generateRanking";
  }
  
  public void setSelectionThreshold(double paramDouble) {
    this.m_threshold = paramDouble;
  }
  
  public double getSelectionThreshold() {
    return this.m_threshold;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(8);
    vector.addElement(new Option("\tType of race to perform.\n\t(default = 0).", "R", 1, "-R <0 = forward | 1 = backward race | 2 = schemata | 3 = rank>"));
    vector.addElement(new Option("\tSignificance level for comaparisons\n\t(default = 0.001(forward/backward/rank)/0.01(schemata)).", "L", 1, "-L <significance>"));
    vector.addElement(new Option("\tThreshold for error comparison.\n\t(default = 0.001).", "T", 1, "-T <threshold>"));
    vector.addElement(new Option("\tAttribute ranker to use if doing a \n\trank search. Place any\n\tevaluator options LAST on the\n\tcommand line following a \"--\".\n\teg. -A weka.attributeSelection.GainRatioAttributeEval ... -- -M.\n\t(default = GainRatioAttributeEval)", "A", 1, "-A <attribute evaluator>"));
    vector.addElement(new Option("\tFolds for cross validation\n\t(default = 0 (1 if schemata race)", "F", 1, "-F <0 = 10 fold | 1 = leave-one-out>"));
    vector.addElement(new Option("\tGenerate a ranked list of attributes.\n\tForces the search to be forward\n.\tand races until all attributes have\n\tselected, thus producing a ranking.", "Q", 0, "-Q"));
    vector.addElement(new Option("\tSpecify number of attributes to retain from \n\tthe ranking. Overides -T. Use in conjunction with -Q", "N", 1, "-N <num to select>"));
    vector.addElement(new Option("\tSpecify a theshold by which attributes\n\tmay be discarded from the ranking.\n\tUse in conjuction with -Q", "T", 1, "-T <threshold>"));
    vector.addElement(new Option("\tVerbose output for monitoring the search.", "Z", 0, "-Z"));
    if (this.m_ASEval != null && this.m_ASEval instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to evaluator " + this.m_ASEval.getClass().getName() + ":"));
      Enumeration enumeration = ((OptionHandler)this.m_ASEval).listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('R', paramArrayOfString);
    if (str.length() != 0)
      setRaceType(new SelectedTag(Integer.parseInt(str), TAGS_SELECTION)); 
    str = Utils.getOption('F', paramArrayOfString);
    if (str.length() != 0)
      setFoldsType(new SelectedTag(Integer.parseInt(str), XVALTAGS_SELECTION)); 
    str = Utils.getOption('L', paramArrayOfString);
    if (str.length() != 0) {
      Double double_ = Double.valueOf(str);
      setSignificanceLevel(double_.doubleValue());
    } 
    str = Utils.getOption('T', paramArrayOfString);
    if (str.length() != 0) {
      Double double_ = Double.valueOf(str);
      setThreshold(double_.doubleValue());
    } 
    str = Utils.getOption('A', paramArrayOfString);
    if (str.length() != 0)
      setAttributeEvaluator(ASEvaluation.forName(str, Utils.partitionOptions(paramArrayOfString))); 
    setGenerateRanking(Utils.getFlag('Q', paramArrayOfString));
    str = Utils.getOption('T', paramArrayOfString);
    if (str.length() != 0) {
      Double double_ = Double.valueOf(str);
      setThreshold(double_.doubleValue());
    } 
    str = Utils.getOption('N', paramArrayOfString);
    if (str.length() != 0)
      setNumToSelect(Integer.parseInt(str)); 
    setDebug(Utils.getFlag('Z', paramArrayOfString));
  }
  
  public String[] getOptions() {
    int i = 0;
    String[] arrayOfString1 = new String[0];
    if (this.m_ASEval != null && this.m_ASEval instanceof OptionHandler)
      arrayOfString1 = ((OptionHandler)this.m_ASEval).getOptions(); 
    String[] arrayOfString2 = new String[17 + arrayOfString1.length];
    arrayOfString2[i++] = "-R";
    arrayOfString2[i++] = "" + this.m_raceType;
    arrayOfString2[i++] = "-L";
    arrayOfString2[i++] = "" + getSignificanceLevel();
    arrayOfString2[i++] = "-T";
    arrayOfString2[i++] = "" + getThreshold();
    arrayOfString2[i++] = "-F";
    arrayOfString2[i++] = "" + this.m_xvalType;
    if (getGenerateRanking())
      arrayOfString2[i++] = "-Q"; 
    arrayOfString2[i++] = "-N";
    arrayOfString2[i++] = "" + getNumToSelect();
    arrayOfString2[i++] = "-J";
    arrayOfString2[i++] = "" + getSelectionThreshold();
    if (getDebug())
      arrayOfString2[i++] = "-Z"; 
    if (getAttributeEvaluator() != null) {
      arrayOfString2[i++] = "-A";
      arrayOfString2[i++] = getAttributeEvaluator().getClass().getName();
      arrayOfString2[i++] = "--";
      System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
      i += arrayOfString1.length;
    } 
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public int[] search(ASEvaluation paramASEvaluation, Instances paramInstances) throws Exception {
    if (!(paramASEvaluation instanceof SubsetEvaluator))
      throw new Exception(paramASEvaluation.getClass().getName() + " is not a " + "Subset evaluator! (RaceSearch)"); 
    if (paramASEvaluation instanceof UnsupervisedSubsetEvaluator)
      throw new Exception("Can't use an unsupervised subset evaluator (RaceSearch)."); 
    if (!(paramASEvaluation instanceof HoldOutSubsetEvaluator))
      throw new Exception("Must use a HoldOutSubsetEvaluator, eg. weka.attributeSelection.ClassifierSubsetEval (RaceSearch)"); 
    if (!(paramASEvaluation instanceof ErrorBasedMeritEvaluator))
      throw new Exception("Only error based subset evaluators can be used, eg. weka.attributeSelection.ClassifierSubsetEval (RaceSearch)"); 
    this.m_Instances = paramInstances;
    this.m_Instances.deleteWithMissingClass();
    if (this.m_Instances.numInstances() == 0)
      throw new Exception("All instances have missing class! (RaceSearch)"); 
    if (this.m_rankingRequested && this.m_numToSelect > this.m_Instances.numAttributes() - 1)
      throw new Exception("More attributes requested than exist in the data (RaceSearch)."); 
    this.m_theEvaluator = (HoldOutSubsetEvaluator)paramASEvaluation;
    this.m_numAttribs = this.m_Instances.numAttributes();
    this.m_classIndex = this.m_Instances.classIndex();
    if (this.m_rankingRequested) {
      this.m_rankedAtts = new double[this.m_numAttribs - 1][2];
      this.m_rankedSoFar = 0;
    } 
    if (this.m_xvalType == 1) {
      this.m_numFolds = paramInstances.numInstances();
    } else {
      this.m_numFolds = 10;
    } 
    Random random = new Random(1L);
    paramInstances.randomize(random);
    int[] arrayOfInt = null;
    switch (this.m_raceType) {
      case 0:
      case 1:
        arrayOfInt = hillclimbRace(paramInstances, random);
        break;
      case 2:
        arrayOfInt = schemataRace(paramInstances, random);
        break;
      case 3:
        arrayOfInt = rankRace(paramInstances, random);
        break;
    } 
    return arrayOfInt;
  }
  
  public double[][] rankedAttributes() throws Exception {
    if (!this.m_rankingRequested)
      throw new Exception("Need to request a ranked list of attributes before attributes can be ranked (RaceSearch)."); 
    if (this.m_rankedAtts == null)
      throw new Exception("Search must be performed before attributes can be ranked (RaceSearch)."); 
    double[][] arrayOfDouble = new double[this.m_rankedSoFar][2];
    for (byte b = 0; b < this.m_rankedSoFar; b++) {
      arrayOfDouble[b][0] = this.m_rankedAtts[b][0];
      arrayOfDouble[b][1] = this.m_rankedAtts[b][1];
    } 
    if (this.m_numToSelect <= 0)
      if (this.m_threshold == -1.7976931348623157E308D) {
        this.m_calculatedNumToSelect = arrayOfDouble.length;
      } else {
        determineNumToSelectFromThreshold(arrayOfDouble);
      }  
    return arrayOfDouble;
  }
  
  private void determineNumToSelectFromThreshold(double[][] paramArrayOfdouble) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfdouble.length; b2++) {
      if (paramArrayOfdouble[b2][1] > this.m_threshold)
        b1++; 
    } 
    this.m_calculatedNumToSelect = b1;
  }
  
  private String printSets(char[][] paramArrayOfchar) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramArrayOfchar.length; b++) {
      for (byte b1 = 0; b1 < this.m_numAttribs; b1++)
        stringBuffer.append(paramArrayOfchar[b][b1]); 
      stringBuffer.append('\n');
    } 
    return stringBuffer.toString();
  }
  
  private int[] schemataRace(Instances paramInstances, Random paramRandom) throws Exception {
    int i = this.m_numAttribs - 1;
    Random random = new Random(42L);
    int j = paramInstances.numInstances();
    Stats[][] arrayOfStats = new Stats[i][2];
    char[][][] arrayOfChar = new char[i][2][this.m_numAttribs - 1];
    char[] arrayOfChar1 = new char[this.m_numAttribs];
    int k;
    for (k = 0; k < this.m_numAttribs; k++)
      arrayOfChar1[k] = '*'; 
    k = 0;
    byte b1;
    for (b1 = 0; b1 < this.m_numAttribs; b1++) {
      if (b1 != this.m_classIndex) {
        arrayOfChar[k][0] = (char[])arrayOfChar1.clone();
        arrayOfChar[k][1] = (char[])arrayOfChar1.clone();
        arrayOfChar[k][0][b1] = '1';
        arrayOfChar[k++][1][b1] = '0';
      } 
    } 
    if (this.m_debug) {
      System.err.println("Initial sets:\n");
      for (b1 = 0; b1 < i; b1++)
        System.err.print(printSets(arrayOfChar[b1]) + "--------------\n"); 
    } 
    BitSet bitSet = new BitSet(this.m_numAttribs);
    char[] arrayOfChar2 = new char[this.m_numAttribs];
    boolean[] arrayOfBoolean = new boolean[this.m_numAttribs];
    byte b2 = 0;
    label130: while (i > 0) {
      boolean bool = false;
      byte b;
      for (b = 0; b < i; b++) {
        arrayOfStats[b][0] = new Stats();
        arrayOfStats[b][1] = new Stats();
      } 
      b = 0;
      label129: while (!bool) {
        int m;
        for (m = 0; m < this.m_numAttribs; m++) {
          if (m != this.m_classIndex)
            if (!arrayOfBoolean[m]) {
              if (random.nextDouble() < 0.5D) {
                bitSet.set(m);
              } else {
                bitSet.clear(m);
              } 
            } else if (arrayOfChar1[m] == '1') {
              bitSet.set(m);
            } else {
              bitSet.clear(m);
            }  
        } 
        m = Math.abs(random.nextInt() % j);
        Instances instances1 = paramInstances.trainCV(j, m, new Random(1L));
        Instances instances2 = paramInstances.testCV(j, m);
        Instance instance = instances2.instance(0);
        b++;
        this.m_theEvaluator.buildEvaluator(instances1);
        double d = -this.m_theEvaluator.evaluateSubset(bitSet, instance, true);
        b2++;
        byte b3;
        for (b3 = 0; b3 < this.m_numAttribs; b3++) {
          if (bitSet.get(b3)) {
            arrayOfChar2[b3] = '1';
          } else {
            arrayOfChar2[b3] = '0';
          } 
        } 
        for (b3 = 0; b3 < i; b3++) {
          if (((arrayOfStats[b3][0]).count + (arrayOfStats[b3][1]).count) / 2.0D > j)
            break label130; 
          for (byte b4 = 0; b4 < 2; b4++) {
            boolean bool1 = true;
            for (byte b5 = 0; b5 < this.m_numAttribs; b5++) {
              if (arrayOfChar[b3][b4][b5] != '*' && arrayOfChar[b3][b4][b5] != arrayOfChar2[b5]) {
                bool1 = false;
                break;
              } 
            } 
            if (bool1) {
              arrayOfStats[b3][b4].add(d);
              if ((arrayOfStats[b3][0]).count > this.m_samples && (arrayOfStats[b3][1]).count > this.m_samples) {
                arrayOfStats[b3][0].calculateDerived();
                arrayOfStats[b3][1].calculateDerived();
                double d1 = ttest(arrayOfStats[b3][0], arrayOfStats[b3][1]);
                if (d1 < this.m_sigLevel) {
                  if ((arrayOfStats[b3][0]).mean < (arrayOfStats[b3][1]).mean) {
                    arrayOfChar1 = (char[])arrayOfChar[b3][0].clone();
                    this.m_bestMerit = (arrayOfStats[b3][0]).mean;
                    if (this.m_debug)
                      System.err.println("contender 0 won "); 
                  } else {
                    arrayOfChar1 = (char[])arrayOfChar[b3][1].clone();
                    this.m_bestMerit = (arrayOfStats[b3][1]).mean;
                    if (this.m_debug)
                      System.err.println("contender 1 won"); 
                  } 
                  if (this.m_debug) {
                    System.err.println(new String(arrayOfChar[b3][0]) + " " + new String(arrayOfChar[b3][1]));
                    System.err.println("Means : " + (arrayOfStats[b3][0]).mean + " vs" + (arrayOfStats[b3][1]).mean);
                    System.err.println("Evaluations so far : " + b2);
                  } 
                  bool = true;
                  continue label129;
                } 
              } 
            } 
          } 
        } 
      } 
      if (--i > 0 && bool) {
        arrayOfChar = new char[i][2][this.m_numAttribs - 1];
        arrayOfStats = new Stats[i][2];
        byte b3;
        for (b3 = 0; b3 < this.m_numAttribs; b3++) {
          if (b3 != this.m_classIndex && !arrayOfBoolean[b3] && arrayOfChar1[b3] != '*') {
            arrayOfBoolean[b3] = true;
            break;
          } 
        } 
        k = 0;
        for (b3 = 0; b3 < i; b3++) {
          arrayOfChar[b3][0] = (char[])arrayOfChar1.clone();
          arrayOfChar[b3][1] = (char[])arrayOfChar1.clone();
          for (int m = k; m < this.m_numAttribs; m++) {
            if (m != this.m_classIndex && arrayOfChar[b3][0][m] == '*') {
              arrayOfChar[b3][0][m] = '1';
              arrayOfChar[b3][1][m] = '0';
              k = m + 1;
              break;
            } 
          } 
        } 
        if (this.m_debug) {
          System.err.println("Next sets:\n");
          for (b3 = 0; b3 < i; b3++)
            System.err.print(printSets(arrayOfChar[b3]) + "--------------\n"); 
        } 
      } 
    } 
    if (this.m_debug)
      System.err.println("Total evaluations : " + b2); 
    return attributeList(arrayOfChar1);
  }
  
  private double ttest(Stats paramStats1, Stats paramStats2) throws Exception {
    double d1 = paramStats1.count;
    double d2 = paramStats2.count;
    double d3 = paramStats1.stdDev * paramStats1.stdDev;
    double d4 = paramStats2.stdDev * paramStats2.stdDev;
    double d5 = paramStats1.mean;
    double d6 = paramStats2.mean;
    double d7 = d1 + d2 - 2.0D;
    double d8 = ((d1 - 1.0D) * d3 + (d2 - 1.0D) * d4) / d7;
    double d9 = (d5 - d6) / Math.sqrt(d8 * (1.0D / d1 + 1.0D / d2));
    return Statistics.incompleteBeta(d7 / 2.0D, 0.5D, d7 / (d7 + d9 * d9));
  }
  
  private int[] rankRace(Instances paramInstances, Random paramRandom) throws Exception {
    char[] arrayOfChar1 = new char[this.m_numAttribs];
    int i;
    for (i = 0; i < this.m_numAttribs; i++) {
      if (i == this.m_classIndex) {
        arrayOfChar1[i] = '-';
      } else {
        arrayOfChar1[i] = '0';
      } 
    } 
    i = this.m_numAttribs - 1;
    char[][] arrayOfChar = new char[i + 1][this.m_numAttribs];
    if (this.m_ASEval instanceof AttributeEvaluator) {
      Ranker ranker = new Ranker();
      ((AttributeEvaluator)this.m_ASEval).buildEvaluator(paramInstances);
      this.m_Ranking = ranker.search(this.m_ASEval, paramInstances);
    } else {
      GreedyStepwise greedyStepwise = new GreedyStepwise();
      greedyStepwise.setGenerateRanking(true);
      ((SubsetEvaluator)this.m_ASEval).buildEvaluator(paramInstances);
      greedyStepwise.search(this.m_ASEval, paramInstances);
      double[][] arrayOfDouble1 = greedyStepwise.rankedAttributes();
      this.m_Ranking = new int[arrayOfDouble1.length];
      for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
        this.m_Ranking[b1] = (int)arrayOfDouble1[b1][0]; 
    } 
    arrayOfChar[0] = (char[])arrayOfChar1.clone();
    for (byte b = 0; b < this.m_Ranking.length; b++) {
      arrayOfChar[b + 1] = (char[])arrayOfChar[b].clone();
      arrayOfChar[b + 1][this.m_Ranking[b]] = '1';
    } 
    if (this.m_debug)
      System.err.println("Initial sets:\n" + printSets(arrayOfChar)); 
    double[] arrayOfDouble = raceSubsets(arrayOfChar, paramInstances, true, paramRandom);
    double d = arrayOfDouble[1];
    char[] arrayOfChar2 = (char[])arrayOfChar[(int)arrayOfDouble[0]].clone();
    this.m_bestMerit = d;
    return attributeList(arrayOfChar2);
  }
  
  private int[] hillclimbRace(Instances paramInstances, Random paramRandom) throws Exception {
    char[] arrayOfChar = new char[this.m_numAttribs];
    boolean bool1 = false;
    int i;
    for (i = 0; i < this.m_numAttribs; i++) {
      if (i != this.m_classIndex) {
        if (this.m_raceType == 0) {
          arrayOfChar[i] = '0';
        } else {
          arrayOfChar[i] = '1';
        } 
      } else {
        arrayOfChar[i] = '-';
      } 
    } 
    i = this.m_numAttribs - 1;
    char[][] arrayOfChar1 = new char[i + 1][this.m_numAttribs];
    arrayOfChar1[0] = (char[])arrayOfChar.clone();
    byte b1 = 1;
    for (byte b2 = 0; b2 < this.m_numAttribs; b2++) {
      if (b2 != this.m_classIndex) {
        arrayOfChar1[b1] = (char[])arrayOfChar.clone();
        if (this.m_raceType == 1) {
          arrayOfChar1[b1++][b2] = '0';
        } else {
          arrayOfChar1[b1++][b2] = '1';
        } 
      } 
    } 
    if (this.m_debug)
      System.err.println("Initial sets:\n" + printSets(arrayOfChar1)); 
    double[] arrayOfDouble = raceSubsets(arrayOfChar1, paramInstances, true, paramRandom);
    double d = arrayOfDouble[1];
    this.m_bestMerit = d;
    arrayOfChar = (char[])arrayOfChar1[(int)arrayOfDouble[0]].clone();
    if (this.m_rankingRequested) {
      this.m_rankedAtts[this.m_rankedSoFar][0] = (int)(arrayOfDouble[0] - 1.0D);
      this.m_rankedAtts[this.m_rankedSoFar][1] = arrayOfDouble[1];
      this.m_rankedSoFar++;
    } 
    boolean bool2 = true;
    while (bool2 && --i != 0) {
      int j = 0;
      arrayOfChar1 = new char[i + 1][this.m_numAttribs];
      for (byte b = 0; b < i + 1; b++) {
        arrayOfChar1[b] = (char[])arrayOfChar.clone();
        if (b > 0)
          for (int k = j; k < this.m_numAttribs; k++) {
            if (this.m_raceType == 1) {
              if (k != this.m_classIndex && arrayOfChar1[b][k] != '0') {
                arrayOfChar1[b][k] = '0';
                j = k + 1;
                break;
              } 
            } else if (k != this.m_classIndex && arrayOfChar1[b][k] != '1') {
              arrayOfChar1[b][k] = '1';
              j = k + 1;
              break;
            } 
          }  
      } 
      if (this.m_debug)
        System.err.println("Next set : \n" + printSets(arrayOfChar1)); 
      bool2 = false;
      arrayOfDouble = raceSubsets(arrayOfChar1, paramInstances, true, paramRandom);
      String str1 = new String(arrayOfChar);
      String str2 = new String(arrayOfChar1[(int)arrayOfDouble[0]]);
      if (str1.compareTo(str2) != 0 && (arrayOfDouble[1] < d || this.m_rankingRequested)) {
        bool2 = true;
        d = arrayOfDouble[1];
        this.m_bestMerit = d;
        if (this.m_rankingRequested)
          for (byte b3 = 0; b3 < arrayOfChar.length; b3++) {
            if (str2.charAt(b3) != str1.charAt(b3)) {
              this.m_rankedAtts[this.m_rankedSoFar][0] = b3;
              this.m_rankedAtts[this.m_rankedSoFar][1] = arrayOfDouble[1];
              this.m_rankedSoFar++;
            } 
          }  
        arrayOfChar = (char[])arrayOfChar1[(int)arrayOfDouble[0]].clone();
      } 
    } 
    return attributeList(arrayOfChar);
  }
  
  private int[] attributeList(char[] paramArrayOfchar) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_numAttribs; b2++) {
      if (paramArrayOfchar[b2] == '1')
        b1++; 
    } 
    int[] arrayOfInt = new int[b1];
    b1 = 0;
    for (byte b3 = 0; b3 < this.m_numAttribs; b3++) {
      if (paramArrayOfchar[b3] == '1')
        arrayOfInt[b1++] = b3; 
    } 
    return arrayOfInt;
  }
  
  private double[] raceSubsets(char[][] paramArrayOfchar, Instances paramInstances, boolean paramBoolean, Random paramRandom) throws Exception {
    ASEvaluation[] arrayOfASEvaluation = HoldOutSubsetEvaluator.makeCopies(this.m_theEvaluator, paramArrayOfchar.length);
    boolean[] arrayOfBoolean = new boolean[paramArrayOfchar.length];
    Stats[] arrayOfStats = new Stats[paramArrayOfchar.length];
    PairedStats[][] arrayOfPairedStats = new PairedStats[paramArrayOfchar.length][paramArrayOfchar.length];
    byte b1 = this.m_rankingRequested ? 1 : 0;
    for (byte b2 = 0; b2 < paramArrayOfchar.length; b2++) {
      arrayOfStats[b2] = new Stats();
      for (int j = b2 + 1; j < paramArrayOfchar.length; j++)
        arrayOfPairedStats[b2][j] = new PairedStats(this.m_sigLevel); 
    } 
    BitSet[] arrayOfBitSet = new BitSet[paramArrayOfchar.length];
    for (byte b3 = 0; b3 < paramArrayOfchar.length; b3++) {
      arrayOfBitSet[b3] = new BitSet(this.m_numAttribs);
      for (byte b = 0; b < this.m_numAttribs; b++) {
        if (paramArrayOfchar[b3][b] == '1')
          arrayOfBitSet[b3].set(b); 
      } 
    } 
    double[] arrayOfDouble1 = new double[paramArrayOfchar.length];
    byte b4 = 0;
    byte b5 = 0;
    Object object = null;
    int i = 1;
    b5 = 0;
    byte b6;
    label146: for (b6 = 0; b6 < this.m_numFolds; b6++) {
      Instances instances1 = paramInstances.trainCV(this.m_numFolds, b6, new Random(1L));
      Instances instances2 = paramInstances.testCV(this.m_numFolds, b6);
      i = instances2.numInstances();
      byte b;
      for (b = b1; b < paramArrayOfchar.length; b++) {
        if (!arrayOfBoolean[b])
          arrayOfASEvaluation[b].buildEvaluator(instances1); 
      } 
      for (b = 0; b < instances2.numInstances(); b++) {
        Instance instance = instances2.instance(b);
        b5++;
        byte b9;
        for (b9 = b1; b9 < paramArrayOfchar.length; b9++) {
          if (!arrayOfBoolean[b9])
            if (b == 0) {
              arrayOfDouble1[b9] = -((HoldOutSubsetEvaluator)arrayOfASEvaluation[b9]).evaluateSubset(arrayOfBitSet[b9], instance, true);
            } else {
              arrayOfDouble1[b9] = -((HoldOutSubsetEvaluator)arrayOfASEvaluation[b9]).evaluateSubset(arrayOfBitSet[b9], instance, false);
            }  
        } 
        for (b9 = b1; b9 < paramArrayOfchar.length; b9++) {
          if (!arrayOfBoolean[b9]) {
            arrayOfStats[b9].add(arrayOfDouble1[b9]);
            for (int j = b9 + 1; j < paramArrayOfchar.length; j++) {
              if (!arrayOfBoolean[j])
                arrayOfPairedStats[b9][j].add(arrayOfDouble1[b9], arrayOfDouble1[j]); 
            } 
          } 
        } 
        if (b5 > this.m_samples - 1 && b4 < paramArrayOfchar.length - 1)
          for (b9 = 0; b9 < paramArrayOfchar.length; b9++) {
            if (!arrayOfBoolean[b9])
              for (int j = b9 + 1; j < paramArrayOfchar.length; j++) {
                if (!arrayOfBoolean[j]) {
                  arrayOfPairedStats[b9][j].calculateDerived();
                  if ((arrayOfPairedStats[b9][j]).differencesSignificance == 0 && (Utils.eq((arrayOfPairedStats[b9][j]).differencesStats.mean, 0.0D) || Utils.gr(this.m_delta, Math.abs((arrayOfPairedStats[b9][j]).differencesStats.mean)))) {
                    if (Utils.eq((arrayOfPairedStats[b9][j]).differencesStats.mean, 0.0D)) {
                      if (paramBoolean) {
                        if (b9 != 0) {
                          arrayOfBoolean[b9] = true;
                        } else {
                          arrayOfBoolean[j] = true;
                        } 
                        b4++;
                      } else {
                        arrayOfBoolean[b9] = true;
                      } 
                      if (this.m_debug)
                        System.err.println("Eliminating (identical) " + b9 + " " + arrayOfBitSet[b9].toString() + " vs " + j + " " + arrayOfBitSet[j].toString() + " after " + b5 + " evaluations\n" + "\nerror " + b9 + " : " + (arrayOfPairedStats[b9][j]).xStats.mean + " vs " + j + " : " + (arrayOfPairedStats[b9][j]).yStats.mean + " diff : " + (arrayOfPairedStats[b9][j]).differencesStats.mean); 
                    } else {
                      if ((arrayOfPairedStats[b9][j]).xStats.mean > (arrayOfPairedStats[b9][j]).yStats.mean) {
                        arrayOfBoolean[b9] = true;
                        b4++;
                        if (this.m_debug)
                          System.err.println("Eliminating (near identical) " + b9 + " " + arrayOfBitSet[b9].toString() + " vs " + j + " " + arrayOfBitSet[j].toString() + " after " + b5 + " evaluations\n" + "\nerror " + b9 + " : " + (arrayOfPairedStats[b9][j]).xStats.mean + " vs " + j + " : " + (arrayOfPairedStats[b9][j]).yStats.mean + " diff : " + (arrayOfPairedStats[b9][j]).differencesStats.mean); 
                        break;
                      } 
                      arrayOfBoolean[j] = true;
                      b4++;
                      if (this.m_debug)
                        System.err.println("Eliminating (near identical) " + j + " " + arrayOfBitSet[j].toString() + " vs " + b9 + " " + arrayOfBitSet[b9].toString() + " after " + b5 + " evaluations\n" + "\nerror " + j + " : " + (arrayOfPairedStats[b9][j]).yStats.mean + " vs " + b9 + " : " + (arrayOfPairedStats[b9][j]).xStats.mean + " diff : " + (arrayOfPairedStats[b9][j]).differencesStats.mean); 
                    } 
                  } else if ((arrayOfPairedStats[b9][j]).differencesSignificance != 0) {
                    if ((arrayOfPairedStats[b9][j]).differencesSignificance > 0) {
                      arrayOfBoolean[b9] = true;
                      b4++;
                      if (this.m_debug)
                        System.err.println("Eliminating (-worse) " + b9 + " " + arrayOfBitSet[b9].toString() + " vs " + j + " " + arrayOfBitSet[j].toString() + " after " + b5 + " evaluations" + "\nerror " + b9 + " : " + (arrayOfPairedStats[b9][j]).xStats.mean + " vs " + j + " : " + (arrayOfPairedStats[b9][j]).yStats.mean); 
                      break;
                    } 
                    arrayOfBoolean[j] = true;
                    b4++;
                    if (this.m_debug)
                      System.err.println("Eliminating (worse) " + j + " " + arrayOfBitSet[j].toString() + " vs " + b9 + " " + arrayOfBitSet[b9].toString() + " after " + b5 + " evaluations" + "\nerror " + j + " : " + (arrayOfPairedStats[b9][j]).yStats.mean + " vs " + b9 + " : " + (arrayOfPairedStats[b9][j]).xStats.mean); 
                  } 
                } 
              }  
          }  
        if (b4 == paramArrayOfchar.length - 1 && paramBoolean && !arrayOfBoolean[0] && !this.m_rankingRequested)
          break label146; 
      } 
    } 
    if (this.m_debug)
      System.err.println("*****eliminated count: " + b4); 
    double d = Double.MAX_VALUE;
    byte b7 = 0;
    for (byte b8 = b1; b8 < paramArrayOfchar.length; b8++) {
      if (!arrayOfBoolean[b8]) {
        arrayOfStats[b8].calculateDerived();
        if (this.m_debug)
          System.err.println("Remaining error: " + arrayOfBitSet[b8].toString() + " " + (arrayOfStats[b8]).mean); 
        if ((arrayOfStats[b8]).mean < d) {
          d = (arrayOfStats[b8]).mean;
          b7 = b8;
        } 
      } 
    } 
    double[] arrayOfDouble2 = new double[2];
    arrayOfDouble2[0] = b7;
    arrayOfDouble2[1] = d;
    if (this.m_debug) {
      System.err.print("Best set from race : ");
      for (byte b = 0; b < this.m_numAttribs; b++) {
        if (paramArrayOfchar[b7][b] == '1') {
          System.err.print('1');
        } else {
          System.err.print('0');
        } 
      } 
      System.err.println(" :" + d + " Processed : " + b5 + "\n" + arrayOfStats[b7].toString());
    } 
    return arrayOfDouble2;
  }
  
  public String toString() {
    byte b;
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\tRaceSearch.\n\tRace type : ");
    switch (this.m_raceType) {
      case 0:
        stringBuffer.append("forward selection race\n\tBase set : no attributes");
        break;
      case 1:
        stringBuffer.append("backward elimination race\n\tBase set : all attributes");
        break;
      case 2:
        stringBuffer.append("schemata race\n\tBase set : no attributes");
        break;
      case 3:
        stringBuffer.append("rank race\n\tBase set : no attributes\n\t");
        stringBuffer.append("Attribute evaluator : " + getAttributeEvaluator().getClass().getName() + " ");
        if (this.m_ASEval instanceof OptionHandler) {
          String[] arrayOfString = new String[0];
          arrayOfString = ((OptionHandler)this.m_ASEval).getOptions();
          for (byte b1 = 0; b1 < arrayOfString.length; b1++)
            stringBuffer.append(arrayOfString[b1] + ' '); 
        } 
        stringBuffer.append("\n");
        stringBuffer.append("\tAttribute ranking : \n");
        i = (int)(Math.log(this.m_Ranking.length) / Math.log(10.0D) + 1.0D);
        for (b = 0; b < this.m_Ranking.length; b++)
          stringBuffer.append("\t " + Utils.doubleToString((this.m_Ranking[b] + 1), i, 0) + " " + this.m_Instances.attribute(this.m_Ranking[b]).name() + '\n'); 
        break;
    } 
    stringBuffer.append("\n\tCross validation mode : ");
    if (this.m_xvalType == 0) {
      stringBuffer.append("10 fold");
    } else {
      stringBuffer.append("Leave-one-out");
    } 
    stringBuffer.append("\n\tMerit of best subset found : ");
    int i = 3;
    double d = this.m_bestMerit - (int)this.m_bestMerit;
    if (Math.abs(this.m_bestMerit) > 0.0D)
      i = (int)Math.abs(Math.log(Math.abs(this.m_bestMerit)) / Math.log(10.0D)) + 2; 
    if (Math.abs(d) > 0.0D) {
      d = Math.abs(Math.log(Math.abs(d)) / Math.log(10.0D)) + 3.0D;
    } else {
      d = 2.0D;
    } 
    stringBuffer.append(Utils.doubleToString(Math.abs(this.m_bestMerit), i + (int)d, (int)d) + "\n");
    return stringBuffer.toString();
  }
  
  protected void resetOptions() {
    this.m_sigLevel = 0.001D;
    this.m_delta = 0.001D;
    this.m_ASEval = new GainRatioAttributeEval();
    this.m_Ranking = null;
    this.m_raceType = 0;
    this.m_debug = false;
    this.m_theEvaluator = null;
    this.m_bestMerit = -1.7976931348623157E308D;
    this.m_numFolds = 10;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\RaceSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */