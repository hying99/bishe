package weka.associations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;
import weka.core.AttributeStats;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class Apriori extends Associator implements OptionHandler {
  protected double m_minSupport;
  
  protected double m_upperBoundMinSupport;
  
  protected double m_lowerBoundMinSupport;
  
  protected static final int CONFIDENCE = 0;
  
  protected static final int LIFT = 1;
  
  protected static final int LEVERAGE = 2;
  
  protected static final int CONVICTION = 3;
  
  public static final Tag[] TAGS_SELECTION = new Tag[] { new Tag(0, "Confidence"), new Tag(1, "Lift"), new Tag(2, "Leverage"), new Tag(3, "Conviction") };
  
  protected int m_metricType = 0;
  
  protected double m_minMetric;
  
  protected int m_numRules;
  
  protected double m_delta;
  
  protected double m_significanceLevel;
  
  protected int m_cycles;
  
  protected FastVector m_Ls;
  
  protected FastVector m_hashtables;
  
  protected FastVector[] m_allTheRules;
  
  protected Instances m_instances;
  
  protected boolean m_outputItemSets;
  
  protected boolean m_removeMissingCols;
  
  protected boolean m_verbose;
  
  public String globalInfo() {
    return "Finds association rules.";
  }
  
  public Apriori() {
    resetOptions();
  }
  
  public void resetOptions() {
    this.m_removeMissingCols = false;
    this.m_verbose = false;
    this.m_delta = 0.05D;
    this.m_minMetric = 0.9D;
    this.m_numRules = 10;
    this.m_lowerBoundMinSupport = 0.1D;
    this.m_upperBoundMinSupport = 1.0D;
    this.m_significanceLevel = -1.0D;
    this.m_outputItemSets = false;
  }
  
  protected Instances removeMissingColumns(Instances paramInstances) throws Exception {
    int i = paramInstances.numInstances();
    StringBuffer stringBuffer = new StringBuffer();
    byte b1 = 0;
    boolean bool = true;
    int j = 0;
    for (byte b2 = 0; b2 < paramInstances.numAttributes(); b2++) {
      AttributeStats attributeStats = paramInstances.attributeStats(b2);
      if (this.m_upperBoundMinSupport == 1.0D && j != i) {
        int[] arrayOfInt = attributeStats.nominalCounts;
        if (arrayOfInt[Utils.maxIndex(arrayOfInt)] > j)
          j = arrayOfInt[Utils.maxIndex(arrayOfInt)]; 
      } 
      if (attributeStats.missingCount == i) {
        if (bool) {
          stringBuffer.append(b2 + 1);
          bool = false;
        } else {
          stringBuffer.append("," + (b2 + 1));
        } 
        b1++;
      } 
    } 
    if (this.m_verbose)
      System.err.println("Removed : " + b1 + " columns with all missing " + "values."); 
    if (this.m_upperBoundMinSupport == 1.0D && j != i) {
      this.m_upperBoundMinSupport = j / i;
      if (this.m_verbose)
        System.err.println("Setting upper bound min support to : " + this.m_upperBoundMinSupport); 
    } 
    if (stringBuffer.toString().length() > 0) {
      Remove remove = new Remove();
      remove.setAttributeIndices(stringBuffer.toString());
      remove.setInvertSelection(false);
      remove.setInputFormat(paramInstances);
      return Filter.useFilter(paramInstances, (Filter)remove);
    } 
    return paramInstances;
  }
  
  public void buildAssociations(Instances paramInstances) throws Exception {
    int i = 0;
    if (paramInstances.checkForStringAttributes())
      throw new Exception("Can't handle string attributes!"); 
    if (this.m_removeMissingCols)
      paramInstances = removeMissingColumns(paramInstances); 
    this.m_cycles = 0;
    this.m_minSupport = this.m_upperBoundMinSupport - this.m_delta;
    this.m_minSupport = (this.m_minSupport < this.m_lowerBoundMinSupport) ? this.m_lowerBoundMinSupport : this.m_minSupport;
    do {
      this.m_Ls = new FastVector();
      this.m_hashtables = new FastVector();
      this.m_allTheRules = new FastVector[6];
      this.m_allTheRules[0] = new FastVector();
      this.m_allTheRules[1] = new FastVector();
      this.m_allTheRules[2] = new FastVector();
      if (this.m_metricType != 0 || this.m_significanceLevel != -1.0D) {
        this.m_allTheRules[3] = new FastVector();
        this.m_allTheRules[4] = new FastVector();
        this.m_allTheRules[5] = new FastVector();
      } 
      FastVector[] arrayOfFastVector = new FastVector[6];
      arrayOfFastVector[0] = new FastVector();
      arrayOfFastVector[1] = new FastVector();
      arrayOfFastVector[2] = new FastVector();
      if (this.m_metricType != 0 || this.m_significanceLevel != -1.0D) {
        arrayOfFastVector[3] = new FastVector();
        arrayOfFastVector[4] = new FastVector();
        arrayOfFastVector[5] = new FastVector();
      } 
      findLargeItemSets(paramInstances);
      if (this.m_significanceLevel != -1.0D || this.m_metricType != 0) {
        findRulesBruteForce();
      } else {
        findRulesQuickly();
      } 
      double[] arrayOfDouble2 = new double[this.m_allTheRules[2].size()];
      int j;
      for (j = 0; j < this.m_allTheRules[2].size(); j++)
        arrayOfDouble2[j] = ((AprioriItemSet)this.m_allTheRules[1].elementAt(j)).support(); 
      int[] arrayOfInt = Utils.stableSort(arrayOfDouble2);
      for (j = 0; j < this.m_allTheRules[2].size(); j++) {
        arrayOfFastVector[0].addElement(this.m_allTheRules[0].elementAt(arrayOfInt[j]));
        arrayOfFastVector[1].addElement(this.m_allTheRules[1].elementAt(arrayOfInt[j]));
        arrayOfFastVector[2].addElement(this.m_allTheRules[2].elementAt(arrayOfInt[j]));
        if (this.m_metricType != 0 || this.m_significanceLevel != -1.0D) {
          arrayOfFastVector[3].addElement(this.m_allTheRules[3].elementAt(arrayOfInt[j]));
          arrayOfFastVector[4].addElement(this.m_allTheRules[4].elementAt(arrayOfInt[j]));
          arrayOfFastVector[5].addElement(this.m_allTheRules[5].elementAt(arrayOfInt[j]));
        } 
      } 
      this.m_allTheRules[0].removeAllElements();
      this.m_allTheRules[1].removeAllElements();
      this.m_allTheRules[2].removeAllElements();
      if (this.m_metricType != 0 || this.m_significanceLevel != -1.0D) {
        this.m_allTheRules[3].removeAllElements();
        this.m_allTheRules[4].removeAllElements();
        this.m_allTheRules[5].removeAllElements();
      } 
      double[] arrayOfDouble1 = new double[arrayOfFastVector[2].size()];
      j = 2 + this.m_metricType;
      int k;
      for (k = 0; k < arrayOfFastVector[2].size(); k++)
        arrayOfDouble1[k] = ((Double)arrayOfFastVector[j].elementAt(k)).doubleValue(); 
      arrayOfInt = Utils.stableSort(arrayOfDouble1);
      for (k = arrayOfFastVector[0].size() - 1; k >= arrayOfFastVector[0].size() - this.m_numRules && k >= 0; k--) {
        this.m_allTheRules[0].addElement(arrayOfFastVector[0].elementAt(arrayOfInt[k]));
        this.m_allTheRules[1].addElement(arrayOfFastVector[1].elementAt(arrayOfInt[k]));
        this.m_allTheRules[2].addElement(arrayOfFastVector[2].elementAt(arrayOfInt[k]));
        if (this.m_metricType != 0 || this.m_significanceLevel != -1.0D) {
          this.m_allTheRules[3].addElement(arrayOfFastVector[3].elementAt(arrayOfInt[k]));
          this.m_allTheRules[4].addElement(arrayOfFastVector[4].elementAt(arrayOfInt[k]));
          this.m_allTheRules[5].addElement(arrayOfFastVector[5].elementAt(arrayOfInt[k]));
        } 
      } 
      if (this.m_verbose && this.m_Ls.size() > 1)
        System.out.println(toString()); 
      this.m_minSupport -= this.m_delta;
      i = (int)(this.m_minSupport * paramInstances.numInstances() + 0.5D);
      this.m_cycles++;
    } while (this.m_allTheRules[0].size() < this.m_numRules && Utils.grOrEq(this.m_minSupport, this.m_lowerBoundMinSupport) && i >= 1);
    this.m_minSupport += this.m_delta;
  }
  
  public Enumeration listOptions() {
    String str1 = "\tThe required number of rules. (default = " + this.m_numRules + ")";
    String str2 = "\tThe minimum confidence of a rule. (default = " + this.m_minMetric + ")";
    String str3 = "\tThe delta by which the minimum support is decreased in\n";
    String str4 = "\teach iteration. (default = " + this.m_delta + ")";
    String str5 = "\tThe lower bound for the minimum support. (default = " + this.m_lowerBoundMinSupport + ")";
    String str6 = "\tIf used, rules are tested for significance at\n";
    String str7 = "\tthe given level. Slower. (default = no significance testing)";
    String str8 = "\tIf set the itemsets found are also output. (default = no)";
    String str9 = "\tThe metric type by which to rank rules. (default = confidence)";
    FastVector fastVector = new FastVector(9);
    fastVector.addElement(new Option(str1, "N", 1, "-N <required number of rules output>"));
    fastVector.addElement(new Option(str9, "T", 1, "-T <0=confidence | 1=lift | 2=leverage | 3=Conviction>"));
    fastVector.addElement(new Option(str2, "C", 1, "-C <minimum metric score of a rule>"));
    fastVector.addElement(new Option(str3 + str4, "D", 1, "-D <delta for minimum support>"));
    fastVector.addElement(new Option("\tUpper bound for minimum support. (default = 1.0)", "U", 1, "-U <upper bound for minimum support>"));
    fastVector.addElement(new Option(str5, "M", 1, "-M <lower bound for minimum support>"));
    fastVector.addElement(new Option(str6 + str7, "S", 1, "-S <significance level>"));
    fastVector.addElement(new Option(str8, "S", 0, "-I"));
    fastVector.addElement(new Option("\tRemove columns that contain all missing values (default = no)", "R", 0, "-R"));
    fastVector.addElement(new Option("\tReport progress iteratively. (default = no)", "V", 0, "-V"));
    return fastVector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str1 = Utils.getOption('N', paramArrayOfString);
    String str2 = Utils.getOption('C', paramArrayOfString);
    String str3 = Utils.getOption('D', paramArrayOfString);
    String str4 = Utils.getOption('U', paramArrayOfString);
    String str5 = Utils.getOption('M', paramArrayOfString);
    String str6 = Utils.getOption('S', paramArrayOfString);
    String str7 = Utils.getOption('T', paramArrayOfString);
    if (str7.length() != 0)
      setMetricType(new SelectedTag(Integer.parseInt(str7), TAGS_SELECTION)); 
    if (str1.length() != 0)
      this.m_numRules = Integer.parseInt(str1); 
    if (str2.length() != 0)
      this.m_minMetric = (new Double(str2)).doubleValue(); 
    if (str3.length() != 0)
      this.m_delta = (new Double(str3)).doubleValue(); 
    if (str4.length() != 0)
      setUpperBoundMinSupport((new Double(str4)).doubleValue()); 
    if (str5.length() != 0)
      this.m_lowerBoundMinSupport = (new Double(str5)).doubleValue(); 
    if (str6.length() != 0)
      this.m_significanceLevel = (new Double(str6)).doubleValue(); 
    this.m_outputItemSets = Utils.getFlag('I', paramArrayOfString);
    this.m_verbose = Utils.getFlag('V', paramArrayOfString);
    setRemoveAllMissingCols(Utils.getFlag('R', paramArrayOfString));
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[16];
    byte b = 0;
    if (this.m_outputItemSets)
      arrayOfString[b++] = "-I"; 
    if (getRemoveAllMissingCols())
      arrayOfString[b++] = "-R"; 
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + this.m_numRules;
    arrayOfString[b++] = "-T";
    arrayOfString[b++] = "" + this.m_metricType;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + this.m_minMetric;
    arrayOfString[b++] = "-D";
    arrayOfString[b++] = "" + this.m_delta;
    arrayOfString[b++] = "-U";
    arrayOfString[b++] = "" + this.m_upperBoundMinSupport;
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + this.m_lowerBoundMinSupport;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + this.m_significanceLevel;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_Ls.size() <= 1)
      return "\nNo large itemsets and rules found!\n"; 
    stringBuffer.append("\nApriori\n=======\n\n");
    stringBuffer.append("Minimum support: " + Utils.doubleToString(this.m_minSupport, 2) + '\n');
    stringBuffer.append("Minimum metric <");
    switch (this.m_metricType) {
      case 0:
        stringBuffer.append("confidence>: ");
        break;
      case 1:
        stringBuffer.append("lift>: ");
        break;
      case 2:
        stringBuffer.append("leverage>: ");
        break;
      case 3:
        stringBuffer.append("conviction>: ");
        break;
    } 
    stringBuffer.append(Utils.doubleToString(this.m_minMetric, 2) + '\n');
    if (this.m_significanceLevel != -1.0D)
      stringBuffer.append("Significance level: " + Utils.doubleToString(this.m_significanceLevel, 2) + '\n'); 
    stringBuffer.append("Number of cycles performed: " + this.m_cycles + '\n');
    stringBuffer.append("\nGenerated sets of large itemsets:\n");
    byte b;
    for (b = 0; b < this.m_Ls.size(); b++) {
      stringBuffer.append("\nSize of set of large itemsets L(" + (b + 1) + "): " + ((FastVector)this.m_Ls.elementAt(b)).size() + '\n');
      if (this.m_outputItemSets) {
        stringBuffer.append("\nLarge Itemsets L(" + (b + 1) + "):\n");
        for (byte b1 = 0; b1 < ((FastVector)this.m_Ls.elementAt(b)).size(); b1++)
          stringBuffer.append(((AprioriItemSet)((FastVector)this.m_Ls.elementAt(b)).elementAt(b1)).toString(this.m_instances) + "\n"); 
      } 
    } 
    stringBuffer.append("\nBest rules found:\n\n");
    for (b = 0; b < this.m_allTheRules[0].size(); b++) {
      stringBuffer.append(Utils.doubleToString(b + 1.0D, (int)(Math.log(this.m_numRules) / Math.log(10.0D) + 1.0D), 0) + ". " + ((AprioriItemSet)this.m_allTheRules[0].elementAt(b)).toString(this.m_instances) + " ==> " + ((AprioriItemSet)this.m_allTheRules[1].elementAt(b)).toString(this.m_instances) + "    conf:(" + Utils.doubleToString(((Double)this.m_allTheRules[2].elementAt(b)).doubleValue(), 2) + ")");
      if (this.m_metricType != 0 || this.m_significanceLevel != -1.0D) {
        stringBuffer.append(((this.m_metricType == 1) ? " <" : "") + " lift:(" + Utils.doubleToString(((Double)this.m_allTheRules[3].elementAt(b)).doubleValue(), 2) + ")" + ((this.m_metricType == 1) ? ">" : ""));
        stringBuffer.append(((this.m_metricType == 2) ? " <" : "") + " lev:(" + Utils.doubleToString(((Double)this.m_allTheRules[4].elementAt(b)).doubleValue(), 2) + ")");
        stringBuffer.append(" [" + (int)(((Double)this.m_allTheRules[4].elementAt(b)).doubleValue() * this.m_instances.numInstances()) + "]" + ((this.m_metricType == 2) ? ">" : ""));
        stringBuffer.append(((this.m_metricType == 3) ? " <" : "") + " conv:(" + Utils.doubleToString(((Double)this.m_allTheRules[5].elementAt(b)).doubleValue(), 2) + ")" + ((this.m_metricType == 3) ? ">" : ""));
      } 
      stringBuffer.append('\n');
    } 
    return stringBuffer.toString();
  }
  
  public String removeAllMissingColsTipText() {
    return "Remove columns with all missing values.";
  }
  
  public void setRemoveAllMissingCols(boolean paramBoolean) {
    this.m_removeMissingCols = paramBoolean;
  }
  
  public boolean getRemoveAllMissingCols() {
    return this.m_removeMissingCols;
  }
  
  public String upperBoundMinSupportTipText() {
    return "Upper bound for minimum support. Start iteratively decreasing minimum support from this value.";
  }
  
  public double getUpperBoundMinSupport() {
    return this.m_upperBoundMinSupport;
  }
  
  public void setUpperBoundMinSupport(double paramDouble) {
    this.m_upperBoundMinSupport = paramDouble;
  }
  
  public String lowerBoundMinSupportTipText() {
    return "Lower bound for minimum support.";
  }
  
  public double getLowerBoundMinSupport() {
    return this.m_lowerBoundMinSupport;
  }
  
  public void setLowerBoundMinSupport(double paramDouble) {
    this.m_lowerBoundMinSupport = paramDouble;
  }
  
  public SelectedTag getMetricType() {
    return new SelectedTag(this.m_metricType, TAGS_SELECTION);
  }
  
  public String metricTypeTipText() {
    return "Set the type of metric by which to rank rules. Confidence is the proportion of the examples covered by the premise that are also covered by the consequence. Lift is confidence divided by the proportion of all examples that are covered by the consequence. This is a measure of the importance of the association that is independent of support. Leverage is the proportion of additional examples covered by both the premise and consequence above those expected if the premise and consequence were independent of each other. The total number of examples that this represents is presented in brackets following the leverage. Conviction is another measure of departure from independence and furthermore takes into account implicaton. Conviction is given by P(premise)P(!consequence) / P(premise, !consequence).";
  }
  
  public void setMetricType(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_SELECTION)
      this.m_metricType = paramSelectedTag.getSelectedTag().getID(); 
    if (this.m_significanceLevel != -1.0D && this.m_metricType != 0)
      this.m_metricType = 0; 
    if (this.m_metricType == 0)
      setMinMetric(0.9D); 
    if (this.m_metricType == 1 || this.m_metricType == 3)
      setMinMetric(1.1D); 
    if (this.m_metricType == 2)
      setMinMetric(0.1D); 
  }
  
  public String minMetricTipText() {
    return "Minimum metric score. Consider only rules with scores higher than this value.";
  }
  
  public double getMinMetric() {
    return this.m_minMetric;
  }
  
  public void setMinMetric(double paramDouble) {
    this.m_minMetric = paramDouble;
  }
  
  public String numRulesTipText() {
    return "Number of rules to find.";
  }
  
  public int getNumRules() {
    return this.m_numRules;
  }
  
  public void setNumRules(int paramInt) {
    this.m_numRules = paramInt;
  }
  
  public String deltaTipText() {
    return "Iteratively decrease support by this factor. Reduces support until min support is reached or required number of rules has been generated.";
  }
  
  public double getDelta() {
    return this.m_delta;
  }
  
  public void setDelta(double paramDouble) {
    this.m_delta = paramDouble;
  }
  
  public String significanceLevelTipText() {
    return "Significance level. Significance test (confidence metric only).";
  }
  
  public double getSignificanceLevel() {
    return this.m_significanceLevel;
  }
  
  public void setSignificanceLevel(double paramDouble) {
    this.m_significanceLevel = paramDouble;
  }
  
  private void findLargeItemSets(Instances paramInstances) throws Exception {
    byte b = 0;
    this.m_instances = paramInstances;
    int i = (int)(this.m_minSupport * paramInstances.numInstances() + 0.5D);
    int j = (int)(this.m_upperBoundMinSupport * paramInstances.numInstances() + 0.5D);
    FastVector fastVector = AprioriItemSet.singletons(paramInstances);
    AprioriItemSet.upDateCounters(fastVector, paramInstances);
    fastVector = AprioriItemSet.deleteItemSets(fastVector, i, j);
    if (fastVector.size() == 0)
      return; 
    do {
      this.m_Ls.addElement(fastVector);
      FastVector fastVector1 = fastVector;
      fastVector = AprioriItemSet.mergeAllItemSets(fastVector1, b, paramInstances.numInstances());
      Hashtable hashtable = AprioriItemSet.getHashtable(fastVector1, fastVector1.size());
      this.m_hashtables.addElement(hashtable);
      fastVector = AprioriItemSet.pruneItemSets(fastVector, hashtable);
      AprioriItemSet.upDateCounters(fastVector, paramInstances);
      fastVector = AprioriItemSet.deleteItemSets(fastVector, i, j);
      b++;
    } while (fastVector.size() > 0);
  }
  
  private void findRulesBruteForce() throws Exception {
    for (byte b = 1; b < this.m_Ls.size(); b++) {
      FastVector fastVector = (FastVector)this.m_Ls.elementAt(b);
      Enumeration enumeration = fastVector.elements();
      while (enumeration.hasMoreElements()) {
        AprioriItemSet aprioriItemSet = enumeration.nextElement();
        FastVector[] arrayOfFastVector = aprioriItemSet.generateRulesBruteForce(this.m_minMetric, this.m_metricType, this.m_hashtables, b + 1, this.m_instances.numInstances(), this.m_significanceLevel);
        for (byte b1 = 0; b1 < arrayOfFastVector[0].size(); b1++) {
          this.m_allTheRules[0].addElement(arrayOfFastVector[0].elementAt(b1));
          this.m_allTheRules[1].addElement(arrayOfFastVector[1].elementAt(b1));
          this.m_allTheRules[2].addElement(arrayOfFastVector[2].elementAt(b1));
          this.m_allTheRules[3].addElement(arrayOfFastVector[3].elementAt(b1));
          this.m_allTheRules[4].addElement(arrayOfFastVector[4].elementAt(b1));
          this.m_allTheRules[5].addElement(arrayOfFastVector[5].elementAt(b1));
        } 
      } 
    } 
  }
  
  private void findRulesQuickly() throws Exception {
    for (byte b = 1; b < this.m_Ls.size(); b++) {
      FastVector fastVector = (FastVector)this.m_Ls.elementAt(b);
      Enumeration enumeration = fastVector.elements();
      while (enumeration.hasMoreElements()) {
        AprioriItemSet aprioriItemSet = enumeration.nextElement();
        FastVector[] arrayOfFastVector = aprioriItemSet.generateRules(this.m_minMetric, this.m_hashtables, b + 1);
        for (byte b1 = 0; b1 < arrayOfFastVector[0].size(); b1++) {
          this.m_allTheRules[0].addElement(arrayOfFastVector[0].elementAt(b1));
          this.m_allTheRules[1].addElement(arrayOfFastVector[1].elementAt(b1));
          this.m_allTheRules[2].addElement(arrayOfFastVector[2].elementAt(b1));
        } 
      } 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer();
    Apriori apriori = new Apriori();
    try {
      stringBuffer.append("\n\nApriori options:\n\n");
      stringBuffer.append("-t <training file>\n");
      stringBuffer.append("\tThe name of the training file.\n");
      Enumeration enumeration = apriori.listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        stringBuffer.append(option.synopsis() + '\n');
        stringBuffer.append(option.description() + '\n');
      } 
      String str = Utils.getOption('t', paramArrayOfString);
      if (str.length() == 0)
        throw new Exception("No training file given!"); 
      apriori.setOptions(paramArrayOfString);
      BufferedReader bufferedReader = new BufferedReader(new FileReader(str));
      apriori.buildAssociations(new Instances(bufferedReader));
      System.out.println(apriori);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println("\n" + exception.getMessage() + stringBuffer);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\Apriori.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */