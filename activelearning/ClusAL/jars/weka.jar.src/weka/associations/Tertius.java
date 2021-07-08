package weka.associations;

import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import weka.associations.tertius.AttributeValueLiteral;
import weka.associations.tertius.IndividualInstances;
import weka.associations.tertius.IndividualLiteral;
import weka.associations.tertius.Literal;
import weka.associations.tertius.Predicate;
import weka.associations.tertius.Rule;
import weka.associations.tertius.SimpleLinkedList;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;

public class Tertius extends Associator implements OptionHandler, Runnable {
  private SimpleLinkedList m_results;
  
  private int m_hypotheses;
  
  private int m_explored;
  
  private Date m_time;
  
  private TextField m_valuesText;
  
  private Instances m_instances;
  
  private ArrayList m_predicates;
  
  private int m_status;
  
  private static final int NORMAL = 0;
  
  private static final int MEMORY = 1;
  
  private static final int STOP = 2;
  
  private int m_best;
  
  private double m_frequencyThreshold;
  
  private double m_confirmationThreshold;
  
  private double m_noiseThreshold;
  
  private boolean m_repeat;
  
  private int m_numLiterals;
  
  private static final int NONE = 0;
  
  private static final int BODY = 1;
  
  private static final int HEAD = 2;
  
  private static final int ALL = 3;
  
  private static final Tag[] TAGS_NEGATION = new Tag[] { new Tag(0, "None"), new Tag(1, "Body"), new Tag(2, "Head"), new Tag(3, "Both") };
  
  private int m_negation;
  
  private boolean m_classification;
  
  private int m_classIndex;
  
  private boolean m_horn;
  
  private boolean m_equivalent;
  
  private boolean m_sameClause;
  
  private boolean m_subsumption;
  
  public static final int EXPLICIT = 0;
  
  public static final int IMPLICIT = 1;
  
  public static final int SIGNIFICANT = 2;
  
  private static final Tag[] TAGS_MISSING = new Tag[] { new Tag(0, "Matches all"), new Tag(1, "Matches none"), new Tag(2, "Significant") };
  
  private int m_missing;
  
  private boolean m_roc;
  
  private String m_partsString;
  
  private Instances m_parts;
  
  private static final int NO = 0;
  
  private static final int OUT = 1;
  
  private static final int WINDOW = 2;
  
  private static final Tag[] TAGS_VALUES = new Tag[] { new Tag(0, "No"), new Tag(1, "stdout"), new Tag(2, "Window") };
  
  private int m_printValues;
  
  public Tertius() {
    resetOptions();
  }
  
  public String globalInfo() {
    return "Finds rules according to confirmation measure.";
  }
  
  public void resetOptions() {
    this.m_best = 10;
    this.m_frequencyThreshold = 0.0D;
    this.m_confirmationThreshold = 0.0D;
    this.m_noiseThreshold = 1.0D;
    this.m_repeat = false;
    this.m_numLiterals = 4;
    this.m_negation = 0;
    this.m_classification = false;
    this.m_classIndex = 0;
    this.m_horn = false;
    this.m_equivalent = true;
    this.m_sameClause = true;
    this.m_subsumption = true;
    this.m_missing = 0;
    this.m_roc = false;
    this.m_partsString = "";
    this.m_parts = null;
    this.m_printValues = 0;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(17);
    vector.addElement(new Option("\tSet maximum number of confirmation  values in the result. (default: 10)", "K", 1, "-K <number of values in result>"));
    vector.addElement(new Option("\tSet frequency threshold for pruning. (default: 0)", "F", 1, "-F <frequency threshold>"));
    vector.addElement(new Option("\tSet confirmation threshold. (default: 0)", "C", 1, "-C <confirmation threshold>"));
    vector.addElement(new Option("\tSet noise threshold : maximum frequency of counter-examples.\n\t0 gives only satisfied rules. (default: 1)", "N", 1, "-N <noise threshold>"));
    vector.addElement(new Option("\tAllow attributes to be repeated in a same rule.", "R", 0, "-R"));
    vector.addElement(new Option("\tSet maximum number of literals in a rule. (default: 4)", "L", 1, "-L <number of literals>"));
    vector.addElement(new Option("\tSet the negations in the rule. (default: 0)", "G", 1, "-G <0=no negation | 1=body | 2=head | 3=body and head>"));
    vector.addElement(new Option("\tConsider only classification rules.", "S", 0, "-S"));
    vector.addElement(new Option("\tSet index of class attribute. (default: last).", "c", 1, "-c <class index>"));
    vector.addElement(new Option("\tConsider only horn clauses.", "H", 0, "-H"));
    vector.addElement(new Option("\tKeep equivalent rules.", "E", 0, "-E"));
    vector.addElement(new Option("\tKeep same clauses.", "M", 0, "-M"));
    vector.addElement(new Option("\tKeep subsumed rules.", "T", 0, "-T"));
    vector.addElement(new Option("\tSet the way to handle missing values. (default: 0)", "I", 1, "-I <0=always match | 1=never match | 2=significant>"));
    vector.addElement(new Option("\tUse ROC analysis. ", "O", 0, "-O"));
    vector.addElement(new Option("\tSet the file containing the parts of the individual for individual-based learning.", "p", 1, "-p <name of file>"));
    vector.addElement(new Option("\tSet output of current values. (default: 0)", "P", 1, "-P <0=no output | 1=on stdout | 2=in separate window>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str1 = Utils.getOption('K', paramArrayOfString);
    if (str1.length() != 0) {
      try {
        this.m_best = Integer.parseInt(str1);
      } catch (Exception exception) {
        throw new Exception("Invalid value for -K option: " + exception.getMessage() + ".");
      } 
      if (this.m_best < 1)
        throw new Exception("Number of confirmation values has to be greater than one!"); 
    } 
    String str2 = Utils.getOption('F', paramArrayOfString);
    if (str2.length() != 0) {
      try {
        this.m_frequencyThreshold = (new Double(str2)).doubleValue();
      } catch (Exception exception) {
        throw new Exception("Invalid value for -F option: " + exception.getMessage() + ".");
      } 
      if (this.m_frequencyThreshold < 0.0D || this.m_frequencyThreshold > 1.0D)
        throw new Exception("Frequency threshold has to be between zero and one!"); 
    } 
    String str3 = Utils.getOption('C', paramArrayOfString);
    if (str3.length() != 0) {
      try {
        this.m_confirmationThreshold = (new Double(str3)).doubleValue();
      } catch (Exception exception) {
        throw new Exception("Invalid value for -C option: " + exception.getMessage() + ".");
      } 
      if (this.m_confirmationThreshold < 0.0D || this.m_confirmationThreshold > 1.0D)
        throw new Exception("Confirmation threshold has to be between zero and one!"); 
      if (str1.length() != 0)
        throw new Exception("Specifying both a number of confirmation values and a confirmation threshold doesn't make sense!"); 
      if (this.m_confirmationThreshold != 0.0D)
        this.m_best = 0; 
    } 
    String str4 = Utils.getOption('N', paramArrayOfString);
    if (str4.length() != 0) {
      try {
        this.m_noiseThreshold = (new Double(str4)).doubleValue();
      } catch (Exception exception) {
        throw new Exception("Invalid value for -N option: " + exception.getMessage() + ".");
      } 
      if (this.m_noiseThreshold < 0.0D || this.m_noiseThreshold > 1.0D)
        throw new Exception("Noise threshold has to be between zero and one!"); 
    } 
    this.m_repeat = Utils.getFlag('R', paramArrayOfString);
    String str5 = Utils.getOption('L', paramArrayOfString);
    if (str5.length() != 0) {
      try {
        this.m_numLiterals = Integer.parseInt(str5);
      } catch (Exception exception) {
        throw new Exception("Invalid value for -L option: " + exception.getMessage() + ".");
      } 
      if (this.m_numLiterals < 1)
        throw new Exception("Number of literals has to be greater than one!"); 
    } 
    String str6 = Utils.getOption('G', paramArrayOfString);
    if (str6.length() != 0) {
      SelectedTag selectedTag;
      int i;
      try {
        i = Integer.parseInt(str6);
      } catch (Exception exception) {
        throw new Exception("Invalid value for -G option: " + exception.getMessage() + ".");
      } 
      try {
        selectedTag = new SelectedTag(i, TAGS_NEGATION);
      } catch (Exception exception) {
        throw new Exception("Value for -G option has to be between zero and three!");
      } 
      setNegation(selectedTag);
    } 
    this.m_classification = Utils.getFlag('S', paramArrayOfString);
    String str7 = Utils.getOption('c', paramArrayOfString);
    if (str7.length() != 0)
      try {
        this.m_classIndex = Integer.parseInt(str7);
      } catch (Exception exception) {
        throw new Exception("Invalid value for -c option: " + exception.getMessage() + ".");
      }  
    this.m_horn = Utils.getFlag('H', paramArrayOfString);
    if (this.m_horn && this.m_negation != 0)
      throw new Exception("Considering horn clauses doesn't make sense if negation allowed!"); 
    this.m_equivalent = !Utils.getFlag('E', paramArrayOfString);
    this.m_sameClause = !Utils.getFlag('M', paramArrayOfString);
    this.m_subsumption = !Utils.getFlag('T', paramArrayOfString);
    String str8 = Utils.getOption('I', paramArrayOfString);
    if (str8.length() != 0) {
      SelectedTag selectedTag;
      int i;
      try {
        i = Integer.parseInt(str8);
      } catch (Exception exception) {
        throw new Exception("Invalid value for -I option: " + exception.getMessage() + ".");
      } 
      try {
        selectedTag = new SelectedTag(i, TAGS_MISSING);
      } catch (Exception exception) {
        throw new Exception("Value for -I option has to be between zero and two!");
      } 
      setMissingValues(selectedTag);
    } 
    this.m_roc = Utils.getFlag('O', paramArrayOfString);
    this.m_partsString = Utils.getOption('p', paramArrayOfString);
    if (this.m_partsString.length() != 0) {
      BufferedReader bufferedReader;
      try {
        bufferedReader = new BufferedReader(new FileReader(this.m_partsString));
      } catch (Exception exception) {
        throw new Exception("Can't open file " + exception.getMessage() + ".");
      } 
      this.m_parts = new Instances(bufferedReader);
    } 
    String str9 = Utils.getOption('P', paramArrayOfString);
    if (str9.length() != 0) {
      SelectedTag selectedTag;
      int i;
      try {
        i = Integer.parseInt(str9);
      } catch (Exception exception) {
        throw new Exception("Invalid value for -P option: " + exception.getMessage() + ".");
      } 
      try {
        selectedTag = new SelectedTag(i, TAGS_VALUES);
      } catch (Exception exception) {
        throw new Exception("Value for -P option has to be between zero and two!");
      } 
      setValuesOutput(selectedTag);
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[24];
    byte b = 0;
    arrayOfString[b++] = "-K";
    arrayOfString[b++] = "" + this.m_best;
    arrayOfString[b++] = "-F";
    arrayOfString[b++] = "" + this.m_frequencyThreshold;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + this.m_confirmationThreshold;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + this.m_noiseThreshold;
    if (this.m_repeat)
      arrayOfString[b++] = "-R"; 
    arrayOfString[b++] = "-L";
    arrayOfString[b++] = "" + this.m_numLiterals;
    arrayOfString[b++] = "-G";
    arrayOfString[b++] = "" + this.m_negation;
    if (this.m_classification)
      arrayOfString[b++] = "-S"; 
    arrayOfString[b++] = "-c";
    arrayOfString[b++] = "" + this.m_classIndex;
    if (this.m_horn)
      arrayOfString[b++] = "-H"; 
    if (!this.m_equivalent)
      arrayOfString[b++] = "-E"; 
    if (!this.m_sameClause)
      arrayOfString[b++] = "-M"; 
    if (!this.m_subsumption)
      arrayOfString[b++] = "-T"; 
    arrayOfString[b++] = "-I";
    arrayOfString[b++] = "" + this.m_missing;
    if (this.m_roc)
      arrayOfString[b++] = "-O"; 
    arrayOfString[b++] = "-p";
    arrayOfString[b++] = "" + this.m_partsString;
    arrayOfString[b++] = "-P";
    arrayOfString[b++] = "" + this.m_printValues;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String confirmationValuesTipText() {
    return "Number of best confirmation values to find.";
  }
  
  public int getConfirmationValues() {
    return this.m_best;
  }
  
  public void setConfirmationValues(int paramInt) throws Exception {
    this.m_best = paramInt;
  }
  
  public String frequencyThresholdTipText() {
    return "Minimum proportion of instances satisfying head and body of rules";
  }
  
  public double getFrequencyThreshold() {
    return this.m_frequencyThreshold;
  }
  
  public void setFrequencyThreshold(double paramDouble) {
    this.m_frequencyThreshold = paramDouble;
  }
  
  public String confirmationThresholdTipText() {
    return "Minimum confirmation of the rules.";
  }
  
  public double getConfirmationThreshold() {
    return this.m_confirmationThreshold;
  }
  
  public void setConfirmationThreshold(double paramDouble) {
    this.m_confirmationThreshold = paramDouble;
    if (paramDouble != 0.0D)
      this.m_best = 0; 
  }
  
  public String noiseThresholdTipText() {
    return "Maximum proportion of counter-instances of rules. If set to 0, only satisfied rules will be given.";
  }
  
  public double getNoiseThreshold() {
    return this.m_noiseThreshold;
  }
  
  public void setNoiseThreshold(double paramDouble) {
    this.m_noiseThreshold = paramDouble;
  }
  
  public String repeatLiteralsTipText() {
    return "Repeated attributes allowed.";
  }
  
  public boolean getRepeatLiterals() {
    return this.m_repeat;
  }
  
  public void setRepeatLiterals(boolean paramBoolean) {
    this.m_repeat = paramBoolean;
  }
  
  public String numberLiteralsTipText() {
    return "Maximum number of literals in a rule.";
  }
  
  public int getNumberLiterals() {
    return this.m_numLiterals;
  }
  
  public void setNumberLiterals(int paramInt) {
    this.m_numLiterals = paramInt;
  }
  
  public String negationTipText() {
    return "Set the type of negation allowed in the rule. Negation can be allowed in the body, in the head, in both or in none.";
  }
  
  public SelectedTag getNegation() {
    return new SelectedTag(this.m_negation, TAGS_NEGATION);
  }
  
  public void setNegation(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_NEGATION)
      this.m_negation = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public String classificationTipText() {
    return "Find only rules with the class in the head.";
  }
  
  public boolean getClassification() {
    return this.m_classification;
  }
  
  public void setClassification(boolean paramBoolean) {
    this.m_classification = paramBoolean;
  }
  
  public String classIndexTipText() {
    return "Index of the class attribute. If set to 0, the class will be the last attribute.";
  }
  
  public int getClassIndex() {
    return this.m_classIndex;
  }
  
  public void setClassIndex(int paramInt) {
    this.m_classIndex = paramInt;
  }
  
  public String hornClausesTipText() {
    return "Find rules with a single conclusion literal only.";
  }
  
  public boolean getHornClauses() {
    return this.m_horn;
  }
  
  public void setHornClauses(boolean paramBoolean) {
    this.m_horn = paramBoolean;
  }
  
  public String equivalentTipText() {
    return "Keep equivalent rules. A rule r2 is equivalent to a rule r1 if the body of r2 is the negation of the head of r1, and the head of r2 is the negation of the body of r1.";
  }
  
  public boolean disabled_getEquivalent() {
    return !this.m_equivalent;
  }
  
  public void disabled_setEquivalent(boolean paramBoolean) {
    this.m_equivalent = !paramBoolean;
  }
  
  public String sameClauseTipText() {
    return "Keep rules corresponding to the same clauses. If set to false, only the rule with the best confirmation value and rules with a lower number of counter-instances will be kept.";
  }
  
  public boolean disabled_getSameClause() {
    return !this.m_sameClause;
  }
  
  public void disabled_setSameClause(boolean paramBoolean) {
    this.m_sameClause = !paramBoolean;
  }
  
  public String subsumptionTipText() {
    return "Keep subsumed rules. If set to false, subsumed rules will only be kept if they have a better confirmation or a lower number of counter-instances.";
  }
  
  public boolean disabled_getSubsumption() {
    return !this.m_subsumption;
  }
  
  public void disabled_setSubsumption(boolean paramBoolean) {
    this.m_subsumption = !paramBoolean;
  }
  
  public String missingValuesTipText() {
    return "Set the way to handle missing values. Missing values can be set to match any value, or never match values or to be significant and possibly appear in rules.";
  }
  
  public SelectedTag getMissingValues() {
    return new SelectedTag(this.m_missing, TAGS_MISSING);
  }
  
  public void setMissingValues(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_MISSING)
      this.m_missing = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public String rocAnalysisTipText() {
    return "Return TP-rate and FP-rate for each rule found.";
  }
  
  public boolean getRocAnalysis() {
    return this.m_roc;
  }
  
  public void setRocAnalysis(boolean paramBoolean) {
    this.m_roc = paramBoolean;
  }
  
  public String partFileTipText() {
    return "Set file containing the parts of the individual for individual-based learning.";
  }
  
  public File disabled_getPartFile() {
    return new File(this.m_partsString);
  }
  
  public void disabled_setPartFile(File paramFile) throws Exception {
    this.m_partsString = paramFile.getAbsolutePath();
    if (this.m_partsString.length() != 0) {
      BufferedReader bufferedReader;
      try {
        bufferedReader = new BufferedReader(new FileReader(this.m_partsString));
      } catch (Exception exception) {
        throw new Exception("Can't open file " + exception.getMessage() + ".");
      } 
      this.m_parts = new Instances(bufferedReader);
    } 
  }
  
  public String valuesOutputTipText() {
    return "Give visual feedback during the search. The current best and worst values can be output either to stdout or to a separate window.";
  }
  
  public SelectedTag getValuesOutput() {
    return new SelectedTag(this.m_printValues, TAGS_VALUES);
  }
  
  public void setValuesOutput(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_VALUES)
      this.m_printValues = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  private Predicate buildPredicate(Instances paramInstances, Attribute paramAttribute, boolean paramBoolean) throws Exception {
    Predicate predicate;
    boolean bool2 = (this.m_parts != null) ? true : false;
    int i = (paramInstances == this.m_parts) ? IndividualLiteral.PART_PROPERTY : IndividualLiteral.INDIVIDUAL_PROPERTY;
    if (paramAttribute.isNumeric())
      throw new Exception("Can't handle numeric attributes!"); 
    boolean bool1 = ((paramInstances.attributeStats(paramAttribute.index())).missingCount > 0) ? true : false;
    if (bool2) {
      predicate = new Predicate(paramInstances.relationName() + "." + paramAttribute.name(), paramAttribute.index(), paramBoolean);
    } else {
      predicate = new Predicate(paramAttribute.name(), paramAttribute.index(), paramBoolean);
    } 
    if (paramAttribute.numValues() == 2 && (!bool1 || this.m_missing == 0)) {
      AttributeValueLiteral attributeValueLiteral1;
      AttributeValueLiteral attributeValueLiteral2;
      if (bool2) {
        IndividualLiteral individualLiteral1 = new IndividualLiteral(predicate, paramAttribute.value(0), 0, 1, this.m_missing, i);
        IndividualLiteral individualLiteral2 = new IndividualLiteral(predicate, paramAttribute.value(1), 1, 1, this.m_missing, i);
      } else {
        attributeValueLiteral1 = new AttributeValueLiteral(predicate, paramAttribute.value(0), 0, 1, this.m_missing);
        attributeValueLiteral2 = new AttributeValueLiteral(predicate, paramAttribute.value(1), 1, 1, this.m_missing);
      } 
      attributeValueLiteral1.setNegation((Literal)attributeValueLiteral2);
      attributeValueLiteral2.setNegation((Literal)attributeValueLiteral1);
      predicate.addLiteral((Literal)attributeValueLiteral1);
    } else {
      for (byte b = 0; b < paramAttribute.numValues(); b++) {
        AttributeValueLiteral attributeValueLiteral;
        if (bool2) {
          IndividualLiteral individualLiteral = new IndividualLiteral(predicate, paramAttribute.value(b), b, 1, this.m_missing, i);
        } else {
          attributeValueLiteral = new AttributeValueLiteral(predicate, paramAttribute.value(b), b, 1, this.m_missing);
        } 
        if (this.m_negation != 0) {
          AttributeValueLiteral attributeValueLiteral1;
          if (bool2) {
            IndividualLiteral individualLiteral = new IndividualLiteral(predicate, paramAttribute.value(b), b, 0, this.m_missing, i);
          } else {
            attributeValueLiteral1 = new AttributeValueLiteral(predicate, paramAttribute.value(b), b, 0, this.m_missing);
          } 
          attributeValueLiteral.setNegation((Literal)attributeValueLiteral1);
          attributeValueLiteral1.setNegation((Literal)attributeValueLiteral);
        } 
        predicate.addLiteral((Literal)attributeValueLiteral);
      } 
      if (bool1 && this.m_missing == 2) {
        AttributeValueLiteral attributeValueLiteral;
        if (bool2) {
          IndividualLiteral individualLiteral = new IndividualLiteral(predicate, "?", -1, 1, this.m_missing, i);
        } else {
          attributeValueLiteral = new AttributeValueLiteral(predicate, "?", -1, 1, this.m_missing);
        } 
        if (this.m_negation != 0) {
          AttributeValueLiteral attributeValueLiteral1;
          if (bool2) {
            IndividualLiteral individualLiteral = new IndividualLiteral(predicate, "?", -1, 0, this.m_missing, i);
          } else {
            attributeValueLiteral1 = new AttributeValueLiteral(predicate, "?", -1, 0, this.m_missing);
          } 
          attributeValueLiteral.setNegation((Literal)attributeValueLiteral1);
          attributeValueLiteral1.setNegation((Literal)attributeValueLiteral);
        } 
        predicate.addLiteral((Literal)attributeValueLiteral);
      } 
    } 
    return predicate;
  }
  
  private ArrayList buildPredicates() throws Exception {
    ArrayList arrayList = new ArrayList();
    Enumeration enumeration = this.m_instances.enumerateAttributes();
    boolean bool = (this.m_parts != null) ? true : false;
    while (enumeration.hasMoreElements()) {
      Attribute attribute1 = enumeration.nextElement();
      if (!bool || !attribute1.name().equals("id")) {
        Predicate predicate = buildPredicate(this.m_instances, attribute1, false);
        arrayList.add(predicate);
      } 
    } 
    Attribute attribute = this.m_instances.classAttribute();
    if (!bool || !attribute.name().equals("id")) {
      Predicate predicate = buildPredicate(this.m_instances, attribute, true);
      arrayList.add(predicate);
    } 
    if (bool) {
      enumeration = this.m_parts.enumerateAttributes();
      while (enumeration.hasMoreElements()) {
        attribute = enumeration.nextElement();
        if (!attribute.name().equals("id")) {
          Predicate predicate = buildPredicate(this.m_parts, attribute, false);
          arrayList.add(predicate);
        } 
      } 
    } 
    return arrayList;
  }
  
  private int numValuesInResult() {
    byte b = 0;
    SimpleLinkedList.LinkedListIterator linkedListIterator = this.m_results.iterator();
    if (!linkedListIterator.hasNext())
      return b; 
    for (Rule rule = (Rule)linkedListIterator.next(); linkedListIterator.hasNext(); rule = rule1) {
      Rule rule1 = (Rule)linkedListIterator.next();
      if (rule.getConfirmation() > rule1.getConfirmation())
        b++; 
    } 
    return b + 1;
  }
  
  private boolean canRefine(Rule paramRule) {
    if (paramRule.isEmpty())
      return true; 
    if (this.m_best != 0) {
      if (numValuesInResult() < this.m_best)
        return true; 
      Rule rule = (Rule)this.m_results.getLast();
      return (paramRule.getOptimistic() >= rule.getConfirmation());
    } 
    return true;
  }
  
  private boolean canCalculateOptimistic(Rule paramRule) {
    return (paramRule.hasTrueBody() || paramRule.hasFalseHead()) ? false : (!!paramRule.overFrequencyThreshold(this.m_frequencyThreshold));
  }
  
  private boolean canExplore(Rule paramRule) {
    if (paramRule.getOptimistic() < this.m_confirmationThreshold)
      return false; 
    if (this.m_best != 0) {
      if (numValuesInResult() < this.m_best)
        return true; 
      Rule rule = (Rule)this.m_results.getLast();
      return (paramRule.getOptimistic() >= rule.getConfirmation());
    } 
    return true;
  }
  
  private boolean canStoreInNodes(Rule paramRule) {
    return !(paramRule.getObservedNumber() == 0);
  }
  
  private boolean canCalculateConfirmation(Rule paramRule) {
    return !(paramRule.getObservedFrequency() > this.m_noiseThreshold);
  }
  
  private boolean canStoreInResults(Rule paramRule) {
    if (paramRule.getConfirmation() < this.m_confirmationThreshold)
      return false; 
    if (this.m_best != 0) {
      if (numValuesInResult() < this.m_best)
        return true; 
      Rule rule = (Rule)this.m_results.getLast();
      return (paramRule.getConfirmation() >= rule.getConfirmation());
    } 
    return true;
  }
  
  private void addResult(Rule paramRule) {
    boolean bool = false;
    SimpleLinkedList.LinkedListIterator linkedListIterator = this.m_results.iterator();
    while (linkedListIterator.hasNext()) {
      Rule rule = (Rule)linkedListIterator.next();
      if (Rule.confirmationThenObservedComparator.compare(rule, paramRule) > 0) {
        linkedListIterator.addBefore(paramRule);
        bool = true;
        break;
      } 
      if ((this.m_subsumption || this.m_sameClause || this.m_equivalent) && rule.subsumes(paramRule)) {
        if (rule.numLiterals() == paramRule.numLiterals()) {
          if (rule.equivalentTo(paramRule)) {
            if (this.m_equivalent)
              return; 
            continue;
          } 
          if (this.m_sameClause && Rule.confirmationComparator.compare(rule, paramRule) < 0)
            return; 
          continue;
        } 
        if (this.m_subsumption && Rule.observedComparator.compare(rule, paramRule) <= 0)
          return; 
      } 
    } 
    if (!bool)
      this.m_results.add(paramRule); 
    SimpleLinkedList.LinkedListInverseIterator linkedListInverseIterator = this.m_results.inverseIterator();
    while (linkedListInverseIterator.hasPrevious()) {
      Rule rule = (Rule)linkedListInverseIterator.previous();
      if (Rule.confirmationThenObservedComparator.compare(rule, paramRule) < 0)
        break; 
      if (rule != paramRule && paramRule.subsumes(rule)) {
        if (rule.numLiterals() == paramRule.numLiterals()) {
          if (!rule.equivalentTo(paramRule) && this.m_sameClause && Rule.confirmationComparator.compare(rule, paramRule) > 0)
            linkedListInverseIterator.remove(); 
          continue;
        } 
        if (this.m_subsumption && Rule.observedComparator.compare(paramRule, rule) <= 0)
          linkedListInverseIterator.remove(); 
      } 
    } 
    if (this.m_best != 0 && numValuesInResult() > this.m_best) {
      Rule rule = (Rule)this.m_results.getLast();
      linkedListInverseIterator = this.m_results.inverseIterator();
      while (linkedListInverseIterator.hasPrevious()) {
        Rule rule1 = (Rule)linkedListInverseIterator.previous();
        if (Rule.confirmationComparator.compare(rule1, rule) < 0)
          break; 
        linkedListInverseIterator.remove();
      } 
    } 
    printValues();
  }
  
  public void buildAssociations(Instances paramInstances) throws Exception {
    Frame frame = null;
    if (this.m_parts == null) {
      this.m_instances = paramInstances;
    } else {
      this.m_instances = (Instances)new IndividualInstances(paramInstances, this.m_parts);
    } 
    this.m_results = new SimpleLinkedList();
    this.m_hypotheses = 0;
    this.m_explored = 0;
    this.m_status = 0;
    if (this.m_classIndex == 0) {
      this.m_instances.setClassIndex(paramInstances.numAttributes() - 1);
    } else {
      if (this.m_classIndex > paramInstances.numAttributes() || this.m_classIndex < 0)
        throw new Exception("Class index has to be between zero and the number of attributes!"); 
      this.m_instances.setClassIndex(this.m_classIndex - 1);
    } 
    if (this.m_printValues == 2) {
      this.m_valuesText = new TextField(37);
      this.m_valuesText.setEditable(false);
      this.m_valuesText.setFont(new Font("Monospaced", 0, 12));
      Label label = new Label("Best and worst current values:");
      Button button = new Button("Stop search");
      button.addActionListener(new ActionListener(this) {
            private final Tertius this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.m_status = 2;
            }
          });
      frame = new Frame("Tertius status");
      frame.setResizable(false);
      frame.add(this.m_valuesText, "Center");
      frame.add(button, "South");
      frame.add(label, "North");
      frame.pack();
      frame.setVisible(true);
    } else if (this.m_printValues == 1) {
      System.out.println("Best and worst current values:");
    } 
    Date date1 = new Date();
    this.m_predicates = buildPredicates();
    beginSearch();
    Date date2 = new Date();
    if (this.m_printValues == 2)
      frame.dispose(); 
    this.m_time = new Date(date2.getTime() - date1.getTime());
  }
  
  public void run() {
    try {
      search();
    } catch (OutOfMemoryError outOfMemoryError) {
      System.gc();
      this.m_status = 1;
    } 
    endSearch();
  }
  
  private synchronized void beginSearch() throws Exception {
    Thread thread = new Thread(this);
    thread.start();
    try {
      wait();
    } catch (InterruptedException interruptedException) {
      this.m_status = 2;
    } 
  }
  
  private synchronized void endSearch() {
    notify();
  }
  
  public void search() {
    SimpleLinkedList simpleLinkedList = new SimpleLinkedList();
    boolean bool1 = (this.m_negation == 1 || this.m_negation == 3) ? true : false;
    boolean bool2 = (this.m_negation == 2 || this.m_negation == 3) ? true : false;
    simpleLinkedList.add(new Rule(this.m_repeat, this.m_numLiterals, bool1, bool2, this.m_classification, this.m_horn));
    printValues();
    while (this.m_status != 2 && !simpleLinkedList.isEmpty()) {
      Rule rule = (Rule)simpleLinkedList.removeFirst();
      if (canRefine(rule)) {
        SimpleLinkedList simpleLinkedList1 = rule.refine(this.m_predicates);
        SimpleLinkedList.LinkedListIterator linkedListIterator = simpleLinkedList1.iterator();
        while (linkedListIterator.hasNext()) {
          this.m_hypotheses++;
          Rule rule1 = (Rule)linkedListIterator.next();
          rule1.upDate(this.m_instances);
          if (canCalculateOptimistic(rule1)) {
            rule1.calculateOptimistic();
            if (canExplore(rule1)) {
              this.m_explored++;
              if (!canStoreInNodes(rule1))
                linkedListIterator.remove(); 
              if (canCalculateConfirmation(rule1)) {
                rule1.calculateConfirmation();
                if (canStoreInResults(rule1))
                  addResult(rule1); 
              } 
              continue;
            } 
            linkedListIterator.remove();
            continue;
          } 
          linkedListIterator.remove();
        } 
        simpleLinkedList1.sort(Rule.optimisticThenObservedComparator);
        simpleLinkedList.merge(simpleLinkedList1, Rule.optimisticThenObservedComparator);
      } 
    } 
  }
  
  private void printValues() {
    if (this.m_printValues == 0)
      return; 
    if (this.m_results.isEmpty()) {
      if (this.m_printValues == 1) {
        System.out.print("0.000000 0.000000 - 0.000000 0.000000");
      } else {
        this.m_valuesText.setText("0.000000 0.000000 - 0.000000 0.000000");
      } 
    } else {
      Rule rule1 = (Rule)this.m_results.getFirst();
      Rule rule2 = (Rule)this.m_results.getLast();
      String str = rule1.valuesToString() + " - " + rule2.valuesToString();
      if (this.m_printValues == 1) {
        System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
        System.out.print(str);
      } else {
        this.m_valuesText.setText(str);
      } 
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm 'min' ss 's' SSS 'ms'");
    SimpleLinkedList.LinkedListIterator linkedListIterator = this.m_results.iterator();
    int i = this.m_results.size();
    byte b = 0;
    stringBuffer.append("\nTertius\n=======\n\n");
    while (linkedListIterator.hasNext()) {
      Rule rule = (Rule)linkedListIterator.next();
      stringBuffer.append(Utils.doubleToString(b + 1.0D, (int)(Math.log(i) / Math.log(10.0D) + 1.0D), 0) + ". ");
      stringBuffer.append("/* ");
      if (this.m_roc) {
        stringBuffer.append(rule.rocToString());
      } else {
        stringBuffer.append(rule.valuesToString());
      } 
      stringBuffer.append(" */ ");
      stringBuffer.append(rule.toString());
      stringBuffer.append("\n");
      b++;
    } 
    stringBuffer.append("\nNumber of hypotheses considered: " + this.m_hypotheses);
    stringBuffer.append("\nNumber of hypotheses explored: " + this.m_explored);
    stringBuffer.append("\nTime: " + simpleDateFormat.format(this.m_time));
    if (this.m_status == 1) {
      stringBuffer.append("\n\nNot enough memory to continue the search");
    } else if (this.m_status == 2) {
      stringBuffer.append("\n\nSearch interrupted");
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    Tertius tertius = new Tertius();
    StringBuffer stringBuffer = new StringBuffer();
    try {
      BufferedReader bufferedReader;
      stringBuffer.append("\n\nTertius options:\n\n");
      stringBuffer.append("-t <name of training file>\n");
      stringBuffer.append("\tSet training file.\n");
      Enumeration enumeration = tertius.listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        stringBuffer.append(option.synopsis() + "\n");
        stringBuffer.append(option.description() + "\n");
      } 
      String str = Utils.getOption('t', paramArrayOfString);
      if (str.length() == 0)
        throw new Exception("No training file given!"); 
      try {
        bufferedReader = new BufferedReader(new FileReader(str));
      } catch (Exception exception) {
        throw new Exception("Can't open file " + exception.getMessage() + ".");
      } 
      Instances instances = new Instances(bufferedReader);
      tertius.setOptions(paramArrayOfString);
      Utils.checkForRemainingOptions(paramArrayOfString);
      tertius.buildAssociations(instances);
      System.out.println(tertius);
    } catch (Exception exception) {
      System.err.println("\nWeka exception: " + exception.getMessage() + stringBuffer);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\Tertius.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */