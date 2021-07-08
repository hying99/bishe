package weka.classifiers.rules;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.ContingencyTables;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class ConjunctiveRule extends Classifier implements OptionHandler, WeightedInstancesHandler {
  private int m_Folds = 3;
  
  private Attribute m_ClassAttribute;
  
  protected FastVector m_Antds = null;
  
  protected double[] m_DefDstr = null;
  
  protected double[] m_Cnsqt = null;
  
  private int m_NumClasses = 0;
  
  private long m_Seed = 1L;
  
  private Random m_Random = null;
  
  private FastVector m_Targets;
  
  private boolean m_IsExclude = false;
  
  private double m_MinNo = 2.0D;
  
  private int m_NumAntds = -1;
  
  public String globalInfo() {
    return "This class implements a single conjunctive rule learner that can predict for numeric and nominal class labels.\n\nA rule consists of antecedents \"AND\"ed together and the consequent (class value) for the classification/regression.  In this case, the consequent is the distribution of the available classes (or mean for a numeric value) in the dataset. If the test instance is not covered by this rule, then it's predicted using the default class distributions/value of the data not covered by the rule in the training data.This learner selects an antecedent by computing the Information Gain of each antecendent and prunes the generated rule using Reduced Error Prunning (REP) or simple pre-pruning based on the number of antecedents.\n\nFor classification, the Information of one antecedent is the weighted average of the entropies of both the data covered and not covered by the rule.\nFor regression, the Information is the weighted average of the mean-squared errors of both the data covered and not covered by the rule.\n\nIn pruning, weighted average of the accuracy rates on the pruning data is used for classification while the weighted average of the mean-squared errors on the pruning data is used for regression.\n\n";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tSet number of folds for REP\n\tOne fold is used as pruning set.\n\t(default 3)", "N", 1, "-N <number of folds>"));
    vector.addElement(new Option("\tSet if NOT uses randomization\n\t(default:use randomization)", "R", 0, "-R"));
    vector.addElement(new Option("\tSet whether consider the exclusive\n\texpressions for nominal attributes\n\t(default false)", "E", 0, "-E"));
    vector.addElement(new Option("\tSet the minimal weights of instances\n\twithin a split.\n\t(default 2.0)", "M", 1, "-M <min. weights>"));
    vector.addElement(new Option("\tSet number of antecedents for pre-pruning\n\tif -1, then REP is used\n\t(default -1)", "P", 1, "-P <number of antecedents>"));
    vector.addElement(new Option("\tSet the seed of randomization\n\t(default 1)", "S", 1, "-S <seed>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('N', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_Folds = Integer.parseInt(str1);
    } else {
      this.m_Folds = 3;
    } 
    String str2 = Utils.getOption('M', paramArrayOfString);
    if (str2.length() != 0) {
      this.m_MinNo = Double.parseDouble(str2);
    } else {
      this.m_MinNo = 2.0D;
    } 
    String str3 = Utils.getOption('S', paramArrayOfString);
    if (str3.length() != 0) {
      this.m_Seed = Integer.parseInt(str3);
    } else {
      this.m_Seed = 1L;
    } 
    String str4 = Utils.getOption('P', paramArrayOfString);
    if (str4.length() != 0) {
      this.m_NumAntds = Integer.parseInt(str4);
    } else {
      this.m_NumAntds = -1;
    } 
    this.m_IsExclude = Utils.getFlag('E', paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[9];
    byte b = 0;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + this.m_Folds;
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + this.m_MinNo;
    arrayOfString[b++] = "-P";
    arrayOfString[b++] = "" + this.m_NumAntds;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + this.m_Seed;
    if (this.m_IsExclude)
      arrayOfString[b++] = "-E"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String foldsTipText() {
    return "Determines the amount of data used for pruning. One fold is used for pruning, the rest for growing the rules.";
  }
  
  public void setFolds(int paramInt) {
    this.m_Folds = paramInt;
  }
  
  public int getFolds() {
    return this.m_Folds;
  }
  
  public String seedTipText() {
    return "The seed used for randomizing the data.";
  }
  
  public void setSeed(long paramLong) {
    this.m_Seed = paramLong;
  }
  
  public long getSeed() {
    return this.m_Seed;
  }
  
  public String exclusiveTipText() {
    return "Set whether to consider exclusive expressions for nominal attribute splits.";
  }
  
  public boolean getExclusive() {
    return this.m_IsExclude;
  }
  
  public void setExclusive(boolean paramBoolean) {
    this.m_IsExclude = paramBoolean;
  }
  
  public String minNoTipText() {
    return "The minimum total weight of the instances in a rule.";
  }
  
  public void setMinNo(double paramDouble) {
    this.m_MinNo = paramDouble;
  }
  
  public double getMinNo() {
    return this.m_MinNo;
  }
  
  public String numAntdsTipText() {
    return "Set the number of antecedents allowed in the rule if pre-pruning is used.  If this value is other than -1, then pre-pruning will be used, otherwise the rule uses reduced-error pruning.";
  }
  
  public void setNumAntds(int paramInt) {
    this.m_NumAntds = paramInt;
  }
  
  public int getNumAntds() {
    return this.m_NumAntds;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    Instances instances = new Instances(paramInstances);
    if (instances.numInstances() == 0)
      throw new Exception("No training data!"); 
    instances.deleteWithMissingClass();
    if (instances.numInstances() == 0)
      throw new Exception("Not training data without missing class values."); 
    if (instances.numInstances() < this.m_Folds)
      throw new Exception("Not enough data for REP."); 
    this.m_ClassAttribute = instances.classAttribute();
    if (this.m_ClassAttribute.isNominal()) {
      this.m_NumClasses = this.m_ClassAttribute.numValues();
    } else {
      this.m_NumClasses = 1;
    } 
    this.m_Antds = new FastVector();
    this.m_DefDstr = new double[this.m_NumClasses];
    this.m_Cnsqt = new double[this.m_NumClasses];
    this.m_Targets = new FastVector();
    this.m_Random = new Random(this.m_Seed);
    if (this.m_NumAntds != -1) {
      grow(instances);
    } else {
      instances.randomize(this.m_Random);
      instances.stratify(this.m_Folds);
      Instances instances1 = instances.trainCV(this.m_Folds, this.m_Folds - 1, this.m_Random);
      Instances instances2 = instances.testCV(this.m_Folds, this.m_Folds - 1);
      grow(instances1);
      prune(instances2);
    } 
    if (this.m_ClassAttribute.isNominal()) {
      Utils.normalize(this.m_Cnsqt);
      if (Utils.gr(Utils.sum(this.m_DefDstr), 0.0D))
        Utils.normalize(this.m_DefDstr); 
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (paramInstance == null)
      throw new Exception("Testing instance is NULL!"); 
    return isCover(paramInstance) ? this.m_Cnsqt : this.m_DefDstr;
  }
  
  public boolean isCover(Instance paramInstance) {
    boolean bool = true;
    for (byte b = 0; b < this.m_Antds.size(); b++) {
      Antd antd = (Antd)this.m_Antds.elementAt(b);
      if (!antd.isCover(paramInstance)) {
        bool = false;
        break;
      } 
    } 
    return bool;
  }
  
  public boolean hasAntds() {
    return (this.m_Antds == null) ? false : ((this.m_Antds.size() > 0));
  }
  
  private void grow(Instances paramInstances) {
    Instances instances = new Instances(paramInstances);
    double d = paramInstances.sumOfWeights();
    if (this.m_NumAntds != 0) {
      double d1;
      double[][] arrayOfDouble1 = new double[2][this.m_NumClasses];
      byte b1;
      for (b1 = 0; b1 < this.m_NumClasses; b1++) {
        arrayOfDouble1[0][b1] = 0.0D;
        arrayOfDouble1[1][b1] = 0.0D;
      } 
      if (this.m_ClassAttribute.isNominal()) {
        for (b1 = 0; b1 < instances.numInstances(); b1++) {
          Instance instance = instances.instance(b1);
          arrayOfDouble1[0][(int)instance.classValue()] = arrayOfDouble1[0][(int)instance.classValue()] + instance.weight();
        } 
        d1 = ContingencyTables.entropy(arrayOfDouble1[0]);
      } else {
        for (b1 = 0; b1 < instances.numInstances(); b1++) {
          Instance instance = instances.instance(b1);
          arrayOfDouble1[0][0] = arrayOfDouble1[0][0] + instance.weight() * instance.classValue();
        } 
        double d5 = arrayOfDouble1[0][0] / d;
        d1 = meanSquaredError(instances, d5) * instances.sumOfWeights();
      } 
      double[][] arrayOfDouble2 = new double[2][this.m_NumClasses];
      for (byte b2 = 0; b2 < this.m_NumClasses; b2++) {
        if (this.m_ClassAttribute.isNominal()) {
          arrayOfDouble2[0][b2] = arrayOfDouble1[0][b2];
          arrayOfDouble2[1][b2] = arrayOfDouble1[1][b2];
        } else {
          arrayOfDouble2[0][b2] = arrayOfDouble1[0][b2] / d;
          arrayOfDouble2[1][b2] = arrayOfDouble1[1][b2];
        } 
      } 
      this.m_Targets.addElement(arrayOfDouble2);
      boolean[] arrayOfBoolean = new boolean[instances.numAttributes()];
      int i;
      for (i = 0; i < arrayOfBoolean.length; i++)
        arrayOfBoolean[i] = false; 
      i = arrayOfBoolean.length;
      double d2 = 0.0D;
      double d3 = 0.0D;
      double d4 = 0.0D;
      boolean bool = true;
      while (bool) {
        double d5 = 0.0D;
        NominalAntd nominalAntd = null;
        Instances instances1 = null;
        Instances instances2 = null;
        Enumeration enumeration = instances.enumerateAttributes();
        byte b = -1;
        while (enumeration.hasMoreElements()) {
          NominalAntd nominalAntd1;
          Attribute attribute = enumeration.nextElement();
          b++;
          NumericAntd numericAntd = null;
          if (this.m_ClassAttribute.isNominal()) {
            if (attribute.isNumeric()) {
              numericAntd = new NumericAntd(this, attribute, arrayOfDouble1[1]);
            } else {
              nominalAntd1 = new NominalAntd(this, attribute, arrayOfDouble1[1]);
            } 
          } else if (attribute.isNumeric()) {
            numericAntd = new NumericAntd(this, attribute, d2, d3, d4);
          } else {
            nominalAntd1 = new NominalAntd(this, attribute, d2, d3, d4);
          } 
          if (!arrayOfBoolean[b]) {
            Instances[] arrayOfInstances = computeInfoGain(instances, d1, nominalAntd1);
            if (arrayOfInstances != null) {
              double d6 = nominalAntd1.getMaxInfoGain();
              boolean bool1 = Utils.gr(d6, d5);
              if (bool1) {
                nominalAntd = nominalAntd1;
                instances1 = arrayOfInstances[0];
                instances2 = arrayOfInstances[1];
                d5 = d6;
              } 
            } 
          } 
        } 
        if (nominalAntd == null)
          break; 
        if (!nominalAntd.getAttr().isNumeric()) {
          arrayOfBoolean[nominalAntd.getAttr().index()] = true;
          i--;
        } 
        this.m_Antds.addElement(nominalAntd);
        instances = instances1;
        int j;
        for (j = 0; j < instances2.numInstances(); j++) {
          Instance instance = instances2.instance(j);
          if (this.m_ClassAttribute.isNumeric()) {
            d2 += instance.weight() * instance.classValue() * instance.classValue();
            d3 += instance.weight() * instance.classValue();
            d4 += instance.weight();
            arrayOfDouble1[0][0] = arrayOfDouble1[0][0] - instance.weight() * instance.classValue();
            arrayOfDouble1[1][0] = arrayOfDouble1[1][0] + instance.weight() * instance.classValue();
          } else {
            arrayOfDouble1[0][(int)instance.classValue()] = arrayOfDouble1[0][(int)instance.classValue()] - instance.weight();
            arrayOfDouble1[1][(int)instance.classValue()] = arrayOfDouble1[1][(int)instance.classValue()] + instance.weight();
          } 
        } 
        arrayOfDouble2 = new double[2][this.m_NumClasses];
        for (j = 0; j < this.m_NumClasses; j++) {
          if (this.m_ClassAttribute.isNominal()) {
            arrayOfDouble2[0][j] = arrayOfDouble1[0][j];
            arrayOfDouble2[1][j] = arrayOfDouble1[1][j];
          } else {
            arrayOfDouble2[0][j] = arrayOfDouble1[0][j] / (d - d4);
            arrayOfDouble2[1][j] = arrayOfDouble1[1][j] / d4;
          } 
        } 
        this.m_Targets.addElement(arrayOfDouble2);
        d1 = nominalAntd.getInfo();
        j = (this.m_NumAntds == -1) ? Integer.MAX_VALUE : this.m_NumAntds;
        if (Utils.eq(instances.sumOfWeights(), 0.0D) || i == 0 || this.m_Antds.size() >= j)
          bool = false; 
      } 
    } 
    this.m_Cnsqt = ((double[][])this.m_Targets.lastElement())[0];
    this.m_DefDstr = ((double[][])this.m_Targets.lastElement())[1];
  }
  
  private Instances[] computeInfoGain(Instances paramInstances, double paramDouble, Antd paramAntd) {
    Instances instances1 = new Instances(paramInstances);
    Instances[] arrayOfInstances1 = paramAntd.splitData(instances1, paramDouble);
    Instances[] arrayOfInstances2 = new Instances[2];
    Instances instances2 = new Instances(instances1, 0);
    Instances instances3 = new Instances(instances1, 0);
    if (arrayOfInstances1 == null)
      return null; 
    byte b;
    for (b = 0; b < arrayOfInstances1.length - 1; b++) {
      if (b == (int)paramAntd.getAttrValue()) {
        instances2 = arrayOfInstances1[b];
      } else {
        for (byte b1 = 0; b1 < arrayOfInstances1[b].numInstances(); b1++)
          instances3.add(arrayOfInstances1[b].instance(b1)); 
      } 
    } 
    if (paramAntd.getAttr().isNominal()) {
      if (((NominalAntd)paramAntd).isIn()) {
        arrayOfInstances2[0] = new Instances(instances2);
        arrayOfInstances2[1] = new Instances(instances3);
      } else {
        arrayOfInstances2[0] = new Instances(instances3);
        arrayOfInstances2[1] = new Instances(instances2);
      } 
    } else {
      arrayOfInstances2[0] = new Instances(instances2);
      arrayOfInstances2[1] = new Instances(instances3);
    } 
    for (b = 0; b < arrayOfInstances1[arrayOfInstances1.length - 1].numInstances(); b++)
      arrayOfInstances2[1].add(arrayOfInstances1[arrayOfInstances1.length - 1].instance(b)); 
    return arrayOfInstances2;
  }
  
  private void prune(Instances paramInstances) {
    double d2;
    Instances instances1 = new Instances(paramInstances);
    Instances instances2 = new Instances(instances1, 0);
    double d1 = instances1.sumOfWeights();
    if (this.m_ClassAttribute.isNumeric()) {
      d2 = meanSquaredError(paramInstances, ((double[][])this.m_Targets.firstElement())[0][0]);
    } else {
      int k = Utils.maxIndex(((double[][])this.m_Targets.firstElement())[0]);
      d2 = computeAccu(paramInstances, k) / d1;
    } 
    int i = this.m_Antds.size();
    if (i == 0) {
      this.m_Cnsqt = ((double[][])this.m_Targets.lastElement())[0];
      this.m_DefDstr = ((double[][])this.m_Targets.lastElement())[1];
      return;
    } 
    double[] arrayOfDouble = new double[i];
    int j;
    for (j = 0; j < i; j++) {
      double d3;
      double d4;
      Antd antd = (Antd)this.m_Antds.elementAt(j);
      Attribute attribute = antd.getAttr();
      Instances instances = new Instances(instances1);
      if (Utils.eq(instances.sumOfWeights(), 0.0D))
        break; 
      instances1 = new Instances(instances, instances.numInstances());
      for (byte b = 0; b < instances.numInstances(); b++) {
        Instance instance = instances.instance(b);
        if (antd.isCover(instance)) {
          instances1.add(instance);
        } else {
          instances2.add(instance);
        } 
      } 
      double[][] arrayOfDouble1 = (double[][])this.m_Targets.elementAt(j + 1);
      if (this.m_ClassAttribute.isNominal()) {
        int k = Utils.maxIndex(arrayOfDouble1[0]);
        int m = Utils.maxIndex(arrayOfDouble1[1]);
        d3 = computeAccu(instances1, k);
        d4 = computeAccu(instances2, m);
      } else {
        double d5 = arrayOfDouble1[0][0];
        double d6 = arrayOfDouble1[1][0];
        d3 = instances1.sumOfWeights() * meanSquaredError(instances1, d5);
        d4 = instances2.sumOfWeights() * meanSquaredError(instances2, d6);
      } 
      arrayOfDouble[j] = (d3 + d4) / d1;
    } 
    j = i - 1;
    while (j > 0) {
      double d;
      if (this.m_ClassAttribute.isNominal()) {
        if (Utils.sm(arrayOfDouble[j], 1.0D)) {
          d = (arrayOfDouble[j] - arrayOfDouble[j - 1]) / arrayOfDouble[j];
        } else {
          d = arrayOfDouble[j] - arrayOfDouble[j - 1];
        } 
      } else if (Utils.sm(arrayOfDouble[j], 1.0D)) {
        d = (arrayOfDouble[j - 1] - arrayOfDouble[j]) / arrayOfDouble[j];
      } else {
        d = arrayOfDouble[j - 1] - arrayOfDouble[j];
      } 
      if (Utils.smOrEq(d, 0.0D)) {
        this.m_Antds.removeElementAt(j);
        this.m_Targets.removeElementAt(j + 1);
        j--;
      } 
    } 
    if (this.m_Antds.size() == 1) {
      double d;
      if (this.m_ClassAttribute.isNominal()) {
        if (Utils.sm(arrayOfDouble[0], 1.0D)) {
          d = (arrayOfDouble[0] - d2) / arrayOfDouble[0];
        } else {
          d = arrayOfDouble[0] - d2;
        } 
      } else if (Utils.sm(arrayOfDouble[0], 1.0D)) {
        d = (d2 - arrayOfDouble[0]) / arrayOfDouble[0];
      } else {
        d = d2 - arrayOfDouble[0];
      } 
      if (Utils.smOrEq(d, 0.0D)) {
        this.m_Antds.removeAllElements();
        this.m_Targets.removeElementAt(1);
      } 
    } 
    this.m_Cnsqt = ((double[][])this.m_Targets.lastElement())[0];
    this.m_DefDstr = ((double[][])this.m_Targets.lastElement())[1];
  }
  
  private double computeAccu(Instances paramInstances, int paramInt) {
    double d = 0.0D;
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = paramInstances.instance(b);
      if ((int)instance.classValue() == paramInt)
        d += instance.weight(); 
    } 
    return d;
  }
  
  private double meanSquaredError(Instances paramInstances, double paramDouble) {
    if (Utils.eq(paramInstances.sumOfWeights(), 0.0D))
      return 0.0D; 
    double d1 = 0.0D;
    double d2 = paramInstances.sumOfWeights();
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = paramInstances.instance(b);
      d1 += instance.weight() * (instance.classValue() - paramDouble) * (instance.classValue() - paramDouble);
    } 
    return d1 / d2;
  }
  
  public String toString(String paramString1, String paramString2) {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_Antds.size() > 0) {
      for (byte b = 0; b < this.m_Antds.size() - 1; b++)
        stringBuffer.append("(" + ((Antd)this.m_Antds.elementAt(b)).toString() + ") and "); 
      stringBuffer.append("(" + ((Antd)this.m_Antds.lastElement()).toString() + ")");
    } 
    stringBuffer.append(" => " + paramString1 + " = " + paramString2);
    return stringBuffer.toString();
  }
  
  public String toString() {
    String str1 = "\n\nSingle conjunctive rule learner:\n--------------------------------\n";
    String str2 = null;
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_ClassAttribute != null)
      if (this.m_ClassAttribute.isNominal()) {
        str2 = toString(this.m_ClassAttribute.name(), this.m_ClassAttribute.value(Utils.maxIndex(this.m_Cnsqt)));
        stringBuffer.append("\n\nClass distributions:\nCovered by the rule:\n");
        byte b;
        for (b = 0; b < this.m_Cnsqt.length; b++)
          stringBuffer.append(this.m_ClassAttribute.value(b) + "\t"); 
        stringBuffer.append('\n');
        for (b = 0; b < this.m_Cnsqt.length; b++)
          stringBuffer.append(Utils.doubleToString(this.m_Cnsqt[b], 6) + "\t"); 
        stringBuffer.append("\n\nNot covered by the rule:\n");
        for (b = 0; b < this.m_DefDstr.length; b++)
          stringBuffer.append(this.m_ClassAttribute.value(b) + "\t"); 
        stringBuffer.append('\n');
        for (b = 0; b < this.m_DefDstr.length; b++)
          stringBuffer.append(Utils.doubleToString(this.m_DefDstr[b], 6) + "\t"); 
      } else {
        str2 = toString(this.m_ClassAttribute.name(), Utils.doubleToString(this.m_Cnsqt[0], 6));
      }  
    return str1 + str2 + stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new ConjunctiveRule(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  class NominalAntd extends Antd {
    private double[][] stats;
    
    private double[] coverage;
    
    private boolean isIn;
    
    private final ConjunctiveRule this$0;
    
    public NominalAntd(ConjunctiveRule this$0, Attribute param1Attribute, double[] param1ArrayOfdouble) {
      super(this$0, param1Attribute, param1ArrayOfdouble);
      this.this$0 = this$0;
      int i = this.att.numValues();
      this.stats = new double[i][this$0.m_NumClasses];
      this.coverage = new double[i];
      this.isIn = true;
    }
    
    public NominalAntd(ConjunctiveRule this$0, Attribute param1Attribute, double param1Double1, double param1Double2, double param1Double3) {
      super(this$0, param1Attribute, param1Double1, param1Double2, param1Double3);
      this.this$0 = this$0;
      int i = this.att.numValues();
      this.stats = (double[][])null;
      this.coverage = new double[i];
      this.isIn = true;
    }
    
    public Instances[] splitData(Instances param1Instances, double param1Double) {
      // Byte code:
      //   0: aload_0
      //   1: getfield att : Lweka/core/Attribute;
      //   4: invokevirtual numValues : ()I
      //   7: istore #4
      //   9: iload #4
      //   11: iconst_1
      //   12: iadd
      //   13: anewarray weka/core/Instances
      //   16: astore #5
      //   18: iload #4
      //   20: newarray double
      //   22: astore #6
      //   24: iload #4
      //   26: newarray double
      //   28: astore #7
      //   30: dconst_0
      //   31: dstore #8
      //   33: dconst_0
      //   34: dstore #10
      //   36: dconst_0
      //   37: dstore #12
      //   39: dconst_0
      //   40: dstore #14
      //   42: aload_1
      //   43: invokevirtual sumOfWeights : ()D
      //   46: dstore #16
      //   48: aload_0
      //   49: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   52: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   55: newarray double
      //   57: astore #18
      //   59: aload_0
      //   60: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   63: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   66: newarray double
      //   68: astore #19
      //   70: iconst_0
      //   71: istore #20
      //   73: iload #20
      //   75: aload_0
      //   76: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   79: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   82: if_icmpge -> 103
      //   85: aload #18
      //   87: iload #20
      //   89: aload #19
      //   91: iload #20
      //   93: dconst_0
      //   94: dup2_x2
      //   95: dastore
      //   96: dastore
      //   97: iinc #20, 1
      //   100: goto -> 73
      //   103: iconst_0
      //   104: istore #20
      //   106: iload #20
      //   108: iload #4
      //   110: if_icmpge -> 195
      //   113: aload_0
      //   114: getfield coverage : [D
      //   117: iload #20
      //   119: aload #6
      //   121: iload #20
      //   123: aload #7
      //   125: iload #20
      //   127: dconst_0
      //   128: dup2_x2
      //   129: dastore
      //   130: dup2_x2
      //   131: dastore
      //   132: dastore
      //   133: aload_0
      //   134: getfield stats : [[D
      //   137: ifnull -> 172
      //   140: iconst_0
      //   141: istore #21
      //   143: iload #21
      //   145: aload_0
      //   146: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   149: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   152: if_icmpge -> 172
      //   155: aload_0
      //   156: getfield stats : [[D
      //   159: iload #20
      //   161: aaload
      //   162: iload #21
      //   164: dconst_0
      //   165: dastore
      //   166: iinc #21, 1
      //   169: goto -> 143
      //   172: aload #5
      //   174: iload #20
      //   176: new weka/core/Instances
      //   179: dup
      //   180: aload_1
      //   181: aload_1
      //   182: invokevirtual numInstances : ()I
      //   185: invokespecial <init> : (Lweka/core/Instances;I)V
      //   188: aastore
      //   189: iinc #20, 1
      //   192: goto -> 106
      //   195: aload #5
      //   197: iload #4
      //   199: new weka/core/Instances
      //   202: dup
      //   203: aload_1
      //   204: aload_1
      //   205: invokevirtual numInstances : ()I
      //   208: invokespecial <init> : (Lweka/core/Instances;I)V
      //   211: aastore
      //   212: iconst_0
      //   213: istore #20
      //   215: iload #20
      //   217: aload_1
      //   218: invokevirtual numInstances : ()I
      //   221: if_icmpge -> 563
      //   224: aload_1
      //   225: iload #20
      //   227: invokevirtual instance : (I)Lweka/core/Instance;
      //   230: astore #21
      //   232: aload #21
      //   234: aload_0
      //   235: getfield att : Lweka/core/Attribute;
      //   238: invokevirtual isMissing : (Lweka/core/Attribute;)Z
      //   241: ifne -> 421
      //   244: aload #21
      //   246: aload_0
      //   247: getfield att : Lweka/core/Attribute;
      //   250: invokevirtual value : (Lweka/core/Attribute;)D
      //   253: d2i
      //   254: istore #22
      //   256: aload #5
      //   258: iload #22
      //   260: aaload
      //   261: aload #21
      //   263: invokevirtual add : (Lweka/core/Instance;)V
      //   266: aload_0
      //   267: getfield coverage : [D
      //   270: iload #22
      //   272: dup2
      //   273: daload
      //   274: aload #21
      //   276: invokevirtual weight : ()D
      //   279: dadd
      //   280: dastore
      //   281: aload_0
      //   282: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   285: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   288: invokevirtual isNominal : ()Z
      //   291: ifeq -> 336
      //   294: aload_0
      //   295: getfield stats : [[D
      //   298: iload #22
      //   300: aaload
      //   301: aload #21
      //   303: invokevirtual classValue : ()D
      //   306: d2i
      //   307: dup2
      //   308: daload
      //   309: aload #21
      //   311: invokevirtual weight : ()D
      //   314: dadd
      //   315: dastore
      //   316: aload #18
      //   318: aload #21
      //   320: invokevirtual classValue : ()D
      //   323: d2i
      //   324: dup2
      //   325: daload
      //   326: aload #21
      //   328: invokevirtual weight : ()D
      //   331: dadd
      //   332: dastore
      //   333: goto -> 557
      //   336: aload #6
      //   338: iload #22
      //   340: dup2
      //   341: daload
      //   342: aload #21
      //   344: invokevirtual weight : ()D
      //   347: aload #21
      //   349: invokevirtual classValue : ()D
      //   352: dmul
      //   353: aload #21
      //   355: invokevirtual classValue : ()D
      //   358: dmul
      //   359: dadd
      //   360: dastore
      //   361: aload #7
      //   363: iload #22
      //   365: dup2
      //   366: daload
      //   367: aload #21
      //   369: invokevirtual weight : ()D
      //   372: aload #21
      //   374: invokevirtual classValue : ()D
      //   377: dmul
      //   378: dadd
      //   379: dastore
      //   380: dload #8
      //   382: aload #21
      //   384: invokevirtual weight : ()D
      //   387: aload #21
      //   389: invokevirtual classValue : ()D
      //   392: dmul
      //   393: aload #21
      //   395: invokevirtual classValue : ()D
      //   398: dmul
      //   399: dadd
      //   400: dstore #8
      //   402: dload #10
      //   404: aload #21
      //   406: invokevirtual weight : ()D
      //   409: aload #21
      //   411: invokevirtual classValue : ()D
      //   414: dmul
      //   415: dadd
      //   416: dstore #10
      //   418: goto -> 557
      //   421: aload #5
      //   423: iload #4
      //   425: aaload
      //   426: aload #21
      //   428: invokevirtual add : (Lweka/core/Instance;)V
      //   431: aload_0
      //   432: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   435: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   438: invokevirtual isNominal : ()Z
      //   441: ifeq -> 481
      //   444: aload #18
      //   446: aload #21
      //   448: invokevirtual classValue : ()D
      //   451: d2i
      //   452: dup2
      //   453: daload
      //   454: aload #21
      //   456: invokevirtual weight : ()D
      //   459: dadd
      //   460: dastore
      //   461: aload #19
      //   463: aload #21
      //   465: invokevirtual classValue : ()D
      //   468: d2i
      //   469: dup2
      //   470: daload
      //   471: aload #21
      //   473: invokevirtual weight : ()D
      //   476: dadd
      //   477: dastore
      //   478: goto -> 557
      //   481: dload #8
      //   483: aload #21
      //   485: invokevirtual weight : ()D
      //   488: aload #21
      //   490: invokevirtual classValue : ()D
      //   493: dmul
      //   494: aload #21
      //   496: invokevirtual classValue : ()D
      //   499: dmul
      //   500: dadd
      //   501: dstore #8
      //   503: dload #10
      //   505: aload #21
      //   507: invokevirtual weight : ()D
      //   510: aload #21
      //   512: invokevirtual classValue : ()D
      //   515: dmul
      //   516: dadd
      //   517: dstore #10
      //   519: dload #12
      //   521: aload #21
      //   523: invokevirtual weight : ()D
      //   526: aload #21
      //   528: invokevirtual classValue : ()D
      //   531: dmul
      //   532: aload #21
      //   534: invokevirtual classValue : ()D
      //   537: dmul
      //   538: dadd
      //   539: dstore #12
      //   541: dload #14
      //   543: aload #21
      //   545: invokevirtual weight : ()D
      //   548: aload #21
      //   550: invokevirtual classValue : ()D
      //   553: dmul
      //   554: dadd
      //   555: dstore #14
      //   557: iinc #20, 1
      //   560: goto -> 215
      //   563: aload_0
      //   564: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   567: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   570: invokevirtual isNominal : ()Z
      //   573: ifeq -> 591
      //   576: dload #16
      //   578: aload_0
      //   579: getfield uncover : [D
      //   582: invokestatic sum : ([D)D
      //   585: dadd
      //   586: dstore #20
      //   588: goto -> 600
      //   591: dload #16
      //   593: aload_0
      //   594: getfield uncoverSum : D
      //   597: dadd
      //   598: dstore #20
      //   600: ldc2_w 1.7976931348623157E308
      //   603: dstore #22
      //   605: aload_0
      //   606: dconst_0
      //   607: putfield maxInfoGain : D
      //   610: iconst_0
      //   611: istore #24
      //   613: iconst_0
      //   614: istore #25
      //   616: iload #25
      //   618: iload #4
      //   620: if_icmpge -> 652
      //   623: aload_0
      //   624: getfield coverage : [D
      //   627: iload #25
      //   629: daload
      //   630: aload_0
      //   631: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   634: invokestatic access$100 : (Lweka/classifiers/rules/ConjunctiveRule;)D
      //   637: invokestatic grOrEq : (DD)Z
      //   640: ifeq -> 646
      //   643: iinc #24, 1
      //   646: iinc #25, 1
      //   649: goto -> 616
      //   652: iload #24
      //   654: iconst_2
      //   655: if_icmpge -> 677
      //   658: aload_0
      //   659: dconst_0
      //   660: putfield maxInfoGain : D
      //   663: aload_0
      //   664: dload_2
      //   665: putfield inform : D
      //   668: aload_0
      //   669: ldc2_w NaN
      //   672: putfield value : D
      //   675: aconst_null
      //   676: areturn
      //   677: iconst_0
      //   678: istore #25
      //   680: iload #25
      //   682: iload #4
      //   684: if_icmpge -> 1340
      //   687: aload_0
      //   688: getfield coverage : [D
      //   691: iload #25
      //   693: daload
      //   694: dstore #26
      //   696: dload #26
      //   698: aload_0
      //   699: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   702: invokestatic access$100 : (Lweka/classifiers/rules/ConjunctiveRule;)D
      //   705: invokestatic sm : (DD)Z
      //   708: ifeq -> 714
      //   711: goto -> 1334
      //   714: aload_0
      //   715: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   718: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   721: invokevirtual isNominal : ()Z
      //   724: ifeq -> 841
      //   727: aload_0
      //   728: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   731: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   734: newarray double
      //   736: astore #32
      //   738: iconst_0
      //   739: istore #33
      //   741: iload #33
      //   743: aload_0
      //   744: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   747: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   750: if_icmpge -> 788
      //   753: aload #32
      //   755: iload #33
      //   757: aload #18
      //   759: iload #33
      //   761: daload
      //   762: aload_0
      //   763: getfield stats : [[D
      //   766: iload #25
      //   768: aaload
      //   769: iload #33
      //   771: daload
      //   772: dsub
      //   773: aload_0
      //   774: getfield uncover : [D
      //   777: iload #33
      //   779: daload
      //   780: dadd
      //   781: dastore
      //   782: iinc #33, 1
      //   785: goto -> 741
      //   788: dload #20
      //   790: dload #26
      //   792: dsub
      //   793: dstore #33
      //   795: aload_0
      //   796: aload_0
      //   797: getfield stats : [[D
      //   800: iload #25
      //   802: aaload
      //   803: dload #26
      //   805: invokevirtual entropy : ([DD)D
      //   808: dstore #28
      //   810: aload_0
      //   811: aload #32
      //   813: dload #33
      //   815: invokevirtual entropy : ([DD)D
      //   818: dstore #35
      //   820: dload_2
      //   821: dload #28
      //   823: dload #26
      //   825: dmul
      //   826: dload #35
      //   828: dload #33
      //   830: dmul
      //   831: dadd
      //   832: dload #20
      //   834: ddiv
      //   835: dsub
      //   836: dstore #30
      //   838: goto -> 911
      //   841: dload #20
      //   843: dload #26
      //   845: dsub
      //   846: dstore #32
      //   848: aload_0
      //   849: aload #6
      //   851: iload #25
      //   853: daload
      //   854: aload #7
      //   856: iload #25
      //   858: daload
      //   859: dload #26
      //   861: invokevirtual wtMeanSqErr : (DDD)D
      //   864: dload #26
      //   866: ddiv
      //   867: dstore #28
      //   869: dload_2
      //   870: dload #28
      //   872: dload #26
      //   874: dmul
      //   875: dsub
      //   876: aload_0
      //   877: dload #8
      //   879: aload #6
      //   881: iload #25
      //   883: daload
      //   884: dsub
      //   885: aload_0
      //   886: getfield uncoverWtSq : D
      //   889: dadd
      //   890: dload #10
      //   892: aload #7
      //   894: iload #25
      //   896: daload
      //   897: dsub
      //   898: aload_0
      //   899: getfield uncoverWtVl : D
      //   902: dadd
      //   903: dload #32
      //   905: invokevirtual wtMeanSqErr : (DDD)D
      //   908: dsub
      //   909: dstore #30
      //   911: iconst_1
      //   912: istore #32
      //   914: aload_0
      //   915: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   918: invokestatic access$300 : (Lweka/classifiers/rules/ConjunctiveRule;)Z
      //   921: ifeq -> 1267
      //   924: aload_0
      //   925: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   928: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   931: invokevirtual isNominal : ()Z
      //   934: ifeq -> 1111
      //   937: aload_0
      //   938: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   941: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   944: newarray double
      //   946: astore #37
      //   948: aload_0
      //   949: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   952: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   955: newarray double
      //   957: astore #38
      //   959: iconst_0
      //   960: istore #39
      //   962: iload #39
      //   964: aload_0
      //   965: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   968: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   971: if_icmpge -> 1036
      //   974: aload #37
      //   976: iload #39
      //   978: aload_0
      //   979: getfield stats : [[D
      //   982: iload #25
      //   984: aaload
      //   985: iload #39
      //   987: daload
      //   988: aload #19
      //   990: iload #39
      //   992: daload
      //   993: dadd
      //   994: aload_0
      //   995: getfield uncover : [D
      //   998: iload #39
      //   1000: daload
      //   1001: dadd
      //   1002: dastore
      //   1003: aload #38
      //   1005: iload #39
      //   1007: aload #18
      //   1009: iload #39
      //   1011: daload
      //   1012: aload_0
      //   1013: getfield stats : [[D
      //   1016: iload #25
      //   1018: aaload
      //   1019: iload #39
      //   1021: daload
      //   1022: dsub
      //   1023: aload #19
      //   1025: iload #39
      //   1027: daload
      //   1028: dsub
      //   1029: dastore
      //   1030: iinc #39, 1
      //   1033: goto -> 962
      //   1036: aload #19
      //   1038: invokestatic sum : ([D)D
      //   1041: dstore #39
      //   1043: dload #26
      //   1045: dload #39
      //   1047: dadd
      //   1048: aload_0
      //   1049: getfield uncover : [D
      //   1052: invokestatic sum : ([D)D
      //   1055: dadd
      //   1056: dstore #41
      //   1058: aload_0
      //   1059: aload #38
      //   1061: dload #16
      //   1063: dload #26
      //   1065: dsub
      //   1066: dload #39
      //   1068: dsub
      //   1069: invokevirtual entropy : ([DD)D
      //   1072: dstore #35
      //   1074: aload_0
      //   1075: aload #37
      //   1077: dload #41
      //   1079: invokevirtual entropy : ([DD)D
      //   1082: dstore #43
      //   1084: dload_2
      //   1085: dload #35
      //   1087: dload #16
      //   1089: dload #26
      //   1091: dsub
      //   1092: dload #39
      //   1094: dsub
      //   1095: dmul
      //   1096: dload #43
      //   1098: dload #41
      //   1100: dmul
      //   1101: dadd
      //   1102: dload #20
      //   1104: ddiv
      //   1105: dsub
      //   1106: dstore #33
      //   1108: goto -> 1226
      //   1111: aload #5
      //   1113: iload #4
      //   1115: aaload
      //   1116: invokevirtual sumOfWeights : ()D
      //   1119: dstore #37
      //   1121: dload #26
      //   1123: aload_0
      //   1124: getfield uncoverSum : D
      //   1127: dadd
      //   1128: dload #37
      //   1130: dadd
      //   1131: dstore #39
      //   1133: aload_0
      //   1134: dload #8
      //   1136: aload #6
      //   1138: iload #25
      //   1140: daload
      //   1141: dsub
      //   1142: dload #12
      //   1144: dsub
      //   1145: dload #10
      //   1147: aload #7
      //   1149: iload #25
      //   1151: daload
      //   1152: dsub
      //   1153: dload #14
      //   1155: dsub
      //   1156: dload #16
      //   1158: dload #26
      //   1160: dsub
      //   1161: dload #37
      //   1163: dsub
      //   1164: invokevirtual wtMeanSqErr : (DDD)D
      //   1167: dload #16
      //   1169: dload #26
      //   1171: dsub
      //   1172: dload #37
      //   1174: dsub
      //   1175: ddiv
      //   1176: dstore #35
      //   1178: dload_2
      //   1179: dload #35
      //   1181: dload #16
      //   1183: dload #26
      //   1185: dsub
      //   1186: dload #37
      //   1188: dsub
      //   1189: dmul
      //   1190: dsub
      //   1191: aload_0
      //   1192: aload #6
      //   1194: iload #25
      //   1196: daload
      //   1197: aload_0
      //   1198: getfield uncoverWtSq : D
      //   1201: dadd
      //   1202: dload #12
      //   1204: dadd
      //   1205: aload #7
      //   1207: iload #25
      //   1209: daload
      //   1210: aload_0
      //   1211: getfield uncoverWtVl : D
      //   1214: dadd
      //   1215: dload #14
      //   1217: dadd
      //   1218: dload #39
      //   1220: invokevirtual wtMeanSqErr : (DDD)D
      //   1223: dsub
      //   1224: dstore #33
      //   1226: dload #33
      //   1228: dload #30
      //   1230: invokestatic gr : (DD)Z
      //   1233: ifne -> 1256
      //   1236: dload #33
      //   1238: dload #30
      //   1240: invokestatic eq : (DD)Z
      //   1243: ifeq -> 1267
      //   1246: dload #35
      //   1248: dload #28
      //   1250: invokestatic sm : (DD)Z
      //   1253: ifeq -> 1267
      //   1256: dload #33
      //   1258: dstore #30
      //   1260: dload #35
      //   1262: dstore #28
      //   1264: iconst_0
      //   1265: istore #32
      //   1267: dload #30
      //   1269: aload_0
      //   1270: getfield maxInfoGain : D
      //   1273: invokestatic gr : (DD)Z
      //   1276: ifne -> 1301
      //   1279: dload #30
      //   1281: aload_0
      //   1282: getfield maxInfoGain : D
      //   1285: invokestatic eq : (DD)Z
      //   1288: ifeq -> 1334
      //   1291: dload #28
      //   1293: dload #22
      //   1295: invokestatic sm : (DD)Z
      //   1298: ifeq -> 1334
      //   1301: aload_0
      //   1302: iload #25
      //   1304: i2d
      //   1305: putfield value : D
      //   1308: aload_0
      //   1309: dload #30
      //   1311: putfield maxInfoGain : D
      //   1314: aload_0
      //   1315: aload_0
      //   1316: getfield maxInfoGain : D
      //   1319: dload_2
      //   1320: dsub
      //   1321: putfield inform : D
      //   1324: dload #28
      //   1326: dstore #22
      //   1328: aload_0
      //   1329: iload #32
      //   1331: putfield isIn : Z
      //   1334: iinc #25, 1
      //   1337: goto -> 680
      //   1340: aload #5
      //   1342: areturn
    }
    
    public boolean isCover(Instance param1Instance) {
      boolean bool = false;
      if (!param1Instance.isMissing(this.att))
        if (this.isIn) {
          if (Utils.eq(param1Instance.value(this.att), this.value))
            bool = true; 
        } else if (!Utils.eq(param1Instance.value(this.att), this.value)) {
          bool = true;
        }  
      return bool;
    }
    
    public boolean isIn() {
      return this.isIn;
    }
    
    public String toString() {
      String str = this.isIn ? " = " : " != ";
      return this.att.name() + str + this.att.value((int)this.value);
    }
  }
  
  private class NumericAntd extends Antd {
    private double splitPoint;
    
    private final ConjunctiveRule this$0;
    
    public NumericAntd(ConjunctiveRule this$0, Attribute param1Attribute, double[] param1ArrayOfdouble) {
      super(this$0, param1Attribute, param1ArrayOfdouble);
      this.this$0 = this$0;
      this.splitPoint = Double.NaN;
    }
    
    public NumericAntd(ConjunctiveRule this$0, Attribute param1Attribute, double param1Double1, double param1Double2, double param1Double3) {
      super(this$0, param1Attribute, param1Double1, param1Double2, param1Double3);
      this.this$0 = this$0;
      this.splitPoint = Double.NaN;
    }
    
    public double getSplitPoint() {
      return this.splitPoint;
    }
    
    public Instances[] splitData(Instances param1Instances, double param1Double) {
      // Byte code:
      //   0: new weka/core/Instances
      //   3: dup
      //   4: aload_1
      //   5: invokespecial <init> : (Lweka/core/Instances;)V
      //   8: astore #4
      //   10: aload #4
      //   12: aload_0
      //   13: getfield att : Lweka/core/Attribute;
      //   16: invokevirtual sort : (Lweka/core/Attribute;)V
      //   19: aload #4
      //   21: invokevirtual numInstances : ()I
      //   24: istore #5
      //   26: aload_0
      //   27: dconst_0
      //   28: putfield maxInfoGain : D
      //   31: aload_0
      //   32: dconst_0
      //   33: putfield value : D
      //   36: aload_0
      //   37: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   40: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   43: invokevirtual isNominal : ()Z
      //   46: ifeq -> 118
      //   49: ldc2_w 0.1
      //   52: aload #4
      //   54: invokevirtual sumOfWeights : ()D
      //   57: dmul
      //   58: aload_0
      //   59: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   62: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   65: invokevirtual numValues : ()I
      //   68: i2d
      //   69: ddiv
      //   70: dstore #6
      //   72: dload #6
      //   74: aload_0
      //   75: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   78: invokestatic access$100 : (Lweka/classifiers/rules/ConjunctiveRule;)D
      //   81: invokestatic smOrEq : (DD)Z
      //   84: ifeq -> 99
      //   87: aload_0
      //   88: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   91: invokestatic access$100 : (Lweka/classifiers/rules/ConjunctiveRule;)D
      //   94: dstore #6
      //   96: goto -> 127
      //   99: dload #6
      //   101: ldc2_w 25.0
      //   104: invokestatic gr : (DD)Z
      //   107: ifeq -> 127
      //   110: ldc2_w 25.0
      //   113: dstore #6
      //   115: goto -> 127
      //   118: aload_0
      //   119: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   122: invokestatic access$100 : (Lweka/classifiers/rules/ConjunctiveRule;)D
      //   125: dstore #6
      //   127: aconst_null
      //   128: astore #8
      //   130: aconst_null
      //   131: astore #9
      //   133: aconst_null
      //   134: astore #10
      //   136: aload_0
      //   137: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   140: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   143: invokevirtual isNominal : ()Z
      //   146: ifeq -> 221
      //   149: aload_0
      //   150: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   153: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   156: newarray double
      //   158: astore #8
      //   160: aload_0
      //   161: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   164: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   167: newarray double
      //   169: astore #9
      //   171: aload_0
      //   172: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   175: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   178: newarray double
      //   180: astore #10
      //   182: iconst_0
      //   183: istore #11
      //   185: iload #11
      //   187: aload_0
      //   188: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   191: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   194: if_icmpge -> 221
      //   197: aload #8
      //   199: iload #11
      //   201: aload #9
      //   203: iload #11
      //   205: aload #10
      //   207: iload #11
      //   209: dconst_0
      //   210: dup2_x2
      //   211: dastore
      //   212: dup2_x2
      //   213: dastore
      //   214: dastore
      //   215: iinc #11, 1
      //   218: goto -> 185
      //   221: dconst_0
      //   222: dstore #11
      //   224: dconst_0
      //   225: dstore #13
      //   227: dconst_0
      //   228: dstore #15
      //   230: dconst_0
      //   231: dstore #17
      //   233: dconst_0
      //   234: dstore #19
      //   236: dconst_0
      //   237: dstore #21
      //   239: iconst_1
      //   240: istore #23
      //   242: iconst_0
      //   243: istore #24
      //   245: iload #23
      //   247: istore #25
      //   249: iconst_0
      //   250: istore #26
      //   252: iload #26
      //   254: aload #4
      //   256: invokevirtual numInstances : ()I
      //   259: if_icmpge -> 377
      //   262: aload #4
      //   264: iload #26
      //   266: invokevirtual instance : (I)Lweka/core/Instance;
      //   269: astore #27
      //   271: aload #27
      //   273: aload_0
      //   274: getfield att : Lweka/core/Attribute;
      //   277: invokevirtual isMissing : (Lweka/core/Attribute;)Z
      //   280: ifeq -> 290
      //   283: iload #26
      //   285: istore #5
      //   287: goto -> 377
      //   290: dload #13
      //   292: aload #27
      //   294: invokevirtual weight : ()D
      //   297: dadd
      //   298: dstore #13
      //   300: aload_0
      //   301: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   304: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   307: invokevirtual isNominal : ()Z
      //   310: ifeq -> 333
      //   313: aload #9
      //   315: aload #27
      //   317: invokevirtual classValue : ()D
      //   320: d2i
      //   321: dup2
      //   322: daload
      //   323: aload #27
      //   325: invokevirtual weight : ()D
      //   328: dadd
      //   329: dastore
      //   330: goto -> 371
      //   333: dload #17
      //   335: aload #27
      //   337: invokevirtual weight : ()D
      //   340: aload #27
      //   342: invokevirtual classValue : ()D
      //   345: dmul
      //   346: aload #27
      //   348: invokevirtual classValue : ()D
      //   351: dmul
      //   352: dadd
      //   353: dstore #17
      //   355: dload #21
      //   357: aload #27
      //   359: invokevirtual weight : ()D
      //   362: aload #27
      //   364: invokevirtual classValue : ()D
      //   367: dmul
      //   368: dadd
      //   369: dstore #21
      //   371: iinc #26, 1
      //   374: goto -> 252
      //   377: dload #13
      //   379: ldc2_w 2.0
      //   382: dload #6
      //   384: dmul
      //   385: invokestatic sm : (DD)Z
      //   388: ifeq -> 393
      //   391: aconst_null
      //   392: areturn
      //   393: dconst_0
      //   394: dstore #26
      //   396: dconst_0
      //   397: dstore #28
      //   399: new weka/core/Instances
      //   402: dup
      //   403: aload #4
      //   405: iconst_0
      //   406: invokespecial <init> : (Lweka/core/Instances;I)V
      //   409: astore #30
      //   411: iload #5
      //   413: istore #31
      //   415: iload #31
      //   417: aload #4
      //   419: invokevirtual numInstances : ()I
      //   422: if_icmpge -> 518
      //   425: aload #4
      //   427: iload #31
      //   429: invokevirtual instance : (I)Lweka/core/Instance;
      //   432: astore #32
      //   434: aload #30
      //   436: aload #32
      //   438: invokevirtual add : (Lweka/core/Instance;)V
      //   441: aload_0
      //   442: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   445: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   448: invokevirtual isNominal : ()Z
      //   451: ifeq -> 474
      //   454: aload #10
      //   456: aload #32
      //   458: invokevirtual classValue : ()D
      //   461: d2i
      //   462: dup2
      //   463: daload
      //   464: aload #32
      //   466: invokevirtual weight : ()D
      //   469: dadd
      //   470: dastore
      //   471: goto -> 512
      //   474: dload #26
      //   476: aload #32
      //   478: invokevirtual weight : ()D
      //   481: aload #32
      //   483: invokevirtual classValue : ()D
      //   486: dmul
      //   487: aload #32
      //   489: invokevirtual classValue : ()D
      //   492: dmul
      //   493: dadd
      //   494: dstore #26
      //   496: dload #28
      //   498: aload #32
      //   500: invokevirtual weight : ()D
      //   503: aload #32
      //   505: invokevirtual classValue : ()D
      //   508: dmul
      //   509: dadd
      //   510: dstore #28
      //   512: iinc #31, 1
      //   515: goto -> 415
      //   518: iload #5
      //   520: ifne -> 525
      //   523: aconst_null
      //   524: areturn
      //   525: aload_0
      //   526: aload #4
      //   528: iload #5
      //   530: iconst_1
      //   531: isub
      //   532: invokevirtual instance : (I)Lweka/core/Instance;
      //   535: aload_0
      //   536: getfield att : Lweka/core/Attribute;
      //   539: invokevirtual value : (Lweka/core/Attribute;)D
      //   542: putfield splitPoint : D
      //   545: iload #23
      //   547: iload #5
      //   549: if_icmpge -> 1395
      //   552: aload #4
      //   554: iload #23
      //   556: invokevirtual instance : (I)Lweka/core/Instance;
      //   559: aload_0
      //   560: getfield att : Lweka/core/Attribute;
      //   563: invokevirtual value : (Lweka/core/Attribute;)D
      //   566: aload #4
      //   568: iload #24
      //   570: invokevirtual instance : (I)Lweka/core/Instance;
      //   573: aload_0
      //   574: getfield att : Lweka/core/Attribute;
      //   577: invokevirtual value : (Lweka/core/Attribute;)D
      //   580: invokestatic eq : (DD)Z
      //   583: ifne -> 1389
      //   586: iload #24
      //   588: istore #31
      //   590: iload #31
      //   592: iload #23
      //   594: if_icmpge -> 758
      //   597: aload #4
      //   599: iload #31
      //   601: invokevirtual instance : (I)Lweka/core/Instance;
      //   604: astore #32
      //   606: dload #11
      //   608: aload #32
      //   610: invokevirtual weight : ()D
      //   613: dadd
      //   614: dstore #11
      //   616: dload #13
      //   618: aload #32
      //   620: invokevirtual weight : ()D
      //   623: dsub
      //   624: dstore #13
      //   626: aload_0
      //   627: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   630: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   633: invokevirtual isNominal : ()Z
      //   636: ifeq -> 676
      //   639: aload #8
      //   641: aload #32
      //   643: invokevirtual classValue : ()D
      //   646: d2i
      //   647: dup2
      //   648: daload
      //   649: aload #32
      //   651: invokevirtual weight : ()D
      //   654: dadd
      //   655: dastore
      //   656: aload #9
      //   658: aload #32
      //   660: invokevirtual classValue : ()D
      //   663: d2i
      //   664: dup2
      //   665: daload
      //   666: aload #32
      //   668: invokevirtual weight : ()D
      //   671: dsub
      //   672: dastore
      //   673: goto -> 752
      //   676: dload #15
      //   678: aload #32
      //   680: invokevirtual weight : ()D
      //   683: aload #32
      //   685: invokevirtual classValue : ()D
      //   688: dmul
      //   689: aload #32
      //   691: invokevirtual classValue : ()D
      //   694: dmul
      //   695: dadd
      //   696: dstore #15
      //   698: dload #19
      //   700: aload #32
      //   702: invokevirtual weight : ()D
      //   705: aload #32
      //   707: invokevirtual classValue : ()D
      //   710: dmul
      //   711: dadd
      //   712: dstore #19
      //   714: dload #17
      //   716: aload #32
      //   718: invokevirtual weight : ()D
      //   721: aload #32
      //   723: invokevirtual classValue : ()D
      //   726: dmul
      //   727: aload #32
      //   729: invokevirtual classValue : ()D
      //   732: dmul
      //   733: dsub
      //   734: dstore #17
      //   736: dload #21
      //   738: aload #32
      //   740: invokevirtual weight : ()D
      //   743: aload #32
      //   745: invokevirtual classValue : ()D
      //   748: dmul
      //   749: dsub
      //   750: dstore #21
      //   752: iinc #31, 1
      //   755: goto -> 590
      //   758: dload #11
      //   760: dload #6
      //   762: invokestatic sm : (DD)Z
      //   765: ifne -> 778
      //   768: dload #13
      //   770: dload #6
      //   772: invokestatic sm : (DD)Z
      //   775: ifeq -> 785
      //   778: iload #23
      //   780: istore #24
      //   782: goto -> 1389
      //   785: dconst_0
      //   786: dstore #31
      //   788: dconst_0
      //   789: dstore #33
      //   791: aload_0
      //   792: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   795: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   798: invokevirtual isNominal : ()Z
      //   801: ifeq -> 827
      //   804: aload_0
      //   805: aload #8
      //   807: dload #11
      //   809: invokevirtual entropy : ([DD)D
      //   812: dstore #31
      //   814: aload_0
      //   815: aload #9
      //   817: dload #13
      //   819: invokevirtual entropy : ([DD)D
      //   822: dstore #33
      //   824: goto -> 857
      //   827: aload_0
      //   828: dload #15
      //   830: dload #19
      //   832: dload #11
      //   834: invokevirtual wtMeanSqErr : (DDD)D
      //   837: dload #11
      //   839: ddiv
      //   840: dstore #31
      //   842: aload_0
      //   843: dload #17
      //   845: dload #21
      //   847: dload #13
      //   849: invokevirtual wtMeanSqErr : (DDD)D
      //   852: dload #13
      //   854: ddiv
      //   855: dstore #33
      //   857: aload_0
      //   858: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   861: invokestatic access$000 : (Lweka/classifiers/rules/ConjunctiveRule;)Lweka/core/Attribute;
      //   864: invokevirtual isNominal : ()Z
      //   867: ifeq -> 1085
      //   870: aload #4
      //   872: invokevirtual sumOfWeights : ()D
      //   875: dstore #48
      //   877: dload #48
      //   879: aload_0
      //   880: getfield uncover : [D
      //   883: invokestatic sum : ([D)D
      //   886: dadd
      //   887: dstore #52
      //   889: aconst_null
      //   890: astore #56
      //   892: aload_0
      //   893: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   896: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   899: newarray double
      //   901: astore #56
      //   903: iconst_0
      //   904: istore #57
      //   906: iload #57
      //   908: aload_0
      //   909: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   912: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   915: if_icmpge -> 948
      //   918: aload #56
      //   920: iload #57
      //   922: aload_0
      //   923: getfield uncover : [D
      //   926: iload #57
      //   928: daload
      //   929: aload #9
      //   931: iload #57
      //   933: daload
      //   934: dadd
      //   935: aload #10
      //   937: iload #57
      //   939: daload
      //   940: dadd
      //   941: dastore
      //   942: iinc #57, 1
      //   945: goto -> 906
      //   948: dload #52
      //   950: dload #11
      //   952: dsub
      //   953: dstore #50
      //   955: aload_0
      //   956: aload #56
      //   958: dload #50
      //   960: invokevirtual entropy : ([DD)D
      //   963: dstore #54
      //   965: dload #31
      //   967: dload #11
      //   969: dmul
      //   970: dload #54
      //   972: dload #50
      //   974: dmul
      //   975: dadd
      //   976: dload #52
      //   978: ddiv
      //   979: dstore #44
      //   981: dload_2
      //   982: dload #44
      //   984: dsub
      //   985: dstore #36
      //   987: aload_0
      //   988: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   991: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   994: newarray double
      //   996: astore #56
      //   998: iconst_0
      //   999: istore #57
      //   1001: iload #57
      //   1003: aload_0
      //   1004: getfield this$0 : Lweka/classifiers/rules/ConjunctiveRule;
      //   1007: invokestatic access$200 : (Lweka/classifiers/rules/ConjunctiveRule;)I
      //   1010: if_icmpge -> 1043
      //   1013: aload #56
      //   1015: iload #57
      //   1017: aload_0
      //   1018: getfield uncover : [D
      //   1021: iload #57
      //   1023: daload
      //   1024: aload #8
      //   1026: iload #57
      //   1028: daload
      //   1029: dadd
      //   1030: aload #10
      //   1032: iload #57
      //   1034: daload
      //   1035: dadd
      //   1036: dastore
      //   1037: iinc #57, 1
      //   1040: goto -> 1001
      //   1043: dload #52
      //   1045: dload #13
      //   1047: dsub
      //   1048: dstore #50
      //   1050: aload_0
      //   1051: aload #56
      //   1053: dload #50
      //   1055: invokevirtual entropy : ([DD)D
      //   1058: dstore #54
      //   1060: dload #33
      //   1062: dload #13
      //   1064: dmul
      //   1065: dload #54
      //   1067: dload #50
      //   1069: dmul
      //   1070: dadd
      //   1071: dload #52
      //   1073: ddiv
      //   1074: dstore #46
      //   1076: dload_2
      //   1077: dload #46
      //   1079: dsub
      //   1080: dstore #38
      //   1082: goto -> 1246
      //   1085: aload #4
      //   1087: invokevirtual sumOfWeights : ()D
      //   1090: dstore #48
      //   1092: dload #17
      //   1094: dload #26
      //   1096: dadd
      //   1097: aload_0
      //   1098: getfield uncoverWtSq : D
      //   1101: dadd
      //   1102: dstore #50
      //   1104: dload #21
      //   1106: dload #28
      //   1108: dadd
      //   1109: aload_0
      //   1110: getfield uncoverWtVl : D
      //   1113: dadd
      //   1114: dstore #52
      //   1116: dload #48
      //   1118: dload #11
      //   1120: dsub
      //   1121: aload_0
      //   1122: getfield uncoverSum : D
      //   1125: dadd
      //   1126: dstore #54
      //   1128: dload #11
      //   1130: dconst_0
      //   1131: invokestatic eq : (DD)Z
      //   1134: ifeq -> 1141
      //   1137: dconst_0
      //   1138: goto -> 1146
      //   1141: dload #31
      //   1143: dload #11
      //   1145: dmul
      //   1146: dstore #44
      //   1148: dload #44
      //   1150: aload_0
      //   1151: dload #50
      //   1153: dload #52
      //   1155: dload #54
      //   1157: invokevirtual wtMeanSqErr : (DDD)D
      //   1160: dadd
      //   1161: dstore #44
      //   1163: dload_2
      //   1164: dload #44
      //   1166: dsub
      //   1167: dstore #36
      //   1169: dload #15
      //   1171: dload #26
      //   1173: dadd
      //   1174: aload_0
      //   1175: getfield uncoverWtSq : D
      //   1178: dadd
      //   1179: dstore #50
      //   1181: dload #19
      //   1183: dload #28
      //   1185: dadd
      //   1186: aload_0
      //   1187: getfield uncoverWtVl : D
      //   1190: dadd
      //   1191: dstore #52
      //   1193: dload #48
      //   1195: dload #13
      //   1197: dsub
      //   1198: aload_0
      //   1199: getfield uncoverSum : D
      //   1202: dadd
      //   1203: dstore #54
      //   1205: dload #13
      //   1207: dconst_0
      //   1208: invokestatic eq : (DD)Z
      //   1211: ifeq -> 1218
      //   1214: dconst_0
      //   1215: goto -> 1223
      //   1218: dload #33
      //   1220: dload #13
      //   1222: dmul
      //   1223: dstore #46
      //   1225: dload #46
      //   1227: aload_0
      //   1228: dload #50
      //   1230: dload #52
      //   1232: dload #54
      //   1234: invokevirtual wtMeanSqErr : (DDD)D
      //   1237: dadd
      //   1238: dstore #46
      //   1240: dload_2
      //   1241: dload #46
      //   1243: dsub
      //   1244: dstore #38
      //   1246: dload #36
      //   1248: dload #38
      //   1250: invokestatic gr : (DD)Z
      //   1253: ifne -> 1276
      //   1256: dload #36
      //   1258: dload #38
      //   1260: invokestatic eq : (DD)Z
      //   1263: ifeq -> 1290
      //   1266: dload #31
      //   1268: dload #33
      //   1270: invokestatic sm : (DD)Z
      //   1273: ifeq -> 1290
      //   1276: iconst_1
      //   1277: istore #35
      //   1279: dload #36
      //   1281: dstore #42
      //   1283: dload #44
      //   1285: dstore #40
      //   1287: goto -> 1301
      //   1290: iconst_0
      //   1291: istore #35
      //   1293: dload #38
      //   1295: dstore #42
      //   1297: dload #46
      //   1299: dstore #40
      //   1301: dload #42
      //   1303: aload_0
      //   1304: getfield maxInfoGain : D
      //   1307: invokestatic gr : (DD)Z
      //   1310: istore #48
      //   1312: iload #48
      //   1314: ifeq -> 1385
      //   1317: aload_0
      //   1318: aload #4
      //   1320: iload #23
      //   1322: invokevirtual instance : (I)Lweka/core/Instance;
      //   1325: aload_0
      //   1326: getfield att : Lweka/core/Attribute;
      //   1329: invokevirtual value : (Lweka/core/Attribute;)D
      //   1332: aload #4
      //   1334: iload #24
      //   1336: invokevirtual instance : (I)Lweka/core/Instance;
      //   1339: aload_0
      //   1340: getfield att : Lweka/core/Attribute;
      //   1343: invokevirtual value : (Lweka/core/Attribute;)D
      //   1346: dadd
      //   1347: ldc2_w 2.0
      //   1350: ddiv
      //   1351: putfield splitPoint : D
      //   1354: aload_0
      //   1355: iload #35
      //   1357: ifeq -> 1364
      //   1360: iconst_0
      //   1361: goto -> 1365
      //   1364: iconst_1
      //   1365: i2d
      //   1366: putfield value : D
      //   1369: aload_0
      //   1370: dload #40
      //   1372: putfield inform : D
      //   1375: aload_0
      //   1376: dload #42
      //   1378: putfield maxInfoGain : D
      //   1381: iload #23
      //   1383: istore #25
      //   1385: iload #23
      //   1387: istore #24
      //   1389: iinc #23, 1
      //   1392: goto -> 545
      //   1395: iconst_3
      //   1396: anewarray weka/core/Instances
      //   1399: astore #31
      //   1401: aload #31
      //   1403: iconst_0
      //   1404: new weka/core/Instances
      //   1407: dup
      //   1408: aload #4
      //   1410: iconst_0
      //   1411: iload #25
      //   1413: invokespecial <init> : (Lweka/core/Instances;II)V
      //   1416: aastore
      //   1417: aload #31
      //   1419: iconst_1
      //   1420: new weka/core/Instances
      //   1423: dup
      //   1424: aload #4
      //   1426: iload #25
      //   1428: iload #5
      //   1430: iload #25
      //   1432: isub
      //   1433: invokespecial <init> : (Lweka/core/Instances;II)V
      //   1436: aastore
      //   1437: aload #31
      //   1439: iconst_2
      //   1440: new weka/core/Instances
      //   1443: dup
      //   1444: aload #30
      //   1446: invokespecial <init> : (Lweka/core/Instances;)V
      //   1449: aastore
      //   1450: aload #31
      //   1452: areturn
    }
    
    public boolean isCover(Instance param1Instance) {
      boolean bool = false;
      if (!param1Instance.isMissing(this.att))
        if (Utils.eq(this.value, 0.0D)) {
          if (Utils.smOrEq(param1Instance.value(this.att), this.splitPoint))
            bool = true; 
        } else if (Utils.gr(param1Instance.value(this.att), this.splitPoint)) {
          bool = true;
        }  
      return bool;
    }
    
    public String toString() {
      String str = Utils.eq(this.value, 0.0D) ? " <= " : " > ";
      return this.att.name() + str + Utils.doubleToString(this.splitPoint, 6);
    }
  }
  
  private abstract class Antd implements Serializable {
    protected Attribute att;
    
    protected double value;
    
    protected double maxInfoGain;
    
    protected double inform;
    
    protected double uncoverWtSq;
    
    protected double uncoverWtVl;
    
    protected double uncoverSum;
    
    protected double[] uncover;
    
    private final ConjunctiveRule this$0;
    
    public Antd(ConjunctiveRule this$0, Attribute param1Attribute, double[] param1ArrayOfdouble) {
      this.this$0 = this$0;
      this.att = param1Attribute;
      this.value = Double.NaN;
      this.maxInfoGain = 0.0D;
      this.inform = Double.NaN;
      this.uncover = param1ArrayOfdouble;
    }
    
    public Antd(ConjunctiveRule this$0, Attribute param1Attribute, double param1Double1, double param1Double2, double param1Double3) {
      this.this$0 = this$0;
      this.att = param1Attribute;
      this.value = Double.NaN;
      this.maxInfoGain = 0.0D;
      this.inform = Double.NaN;
      this.uncoverWtSq = param1Double1;
      this.uncoverWtVl = param1Double2;
      this.uncoverSum = param1Double3;
    }
    
    public abstract Instances[] splitData(Instances param1Instances, double param1Double);
    
    public abstract boolean isCover(Instance param1Instance);
    
    public abstract String toString();
    
    public Attribute getAttr() {
      return this.att;
    }
    
    public double getAttrValue() {
      return this.value;
    }
    
    public double getMaxInfoGain() {
      return this.maxInfoGain;
    }
    
    public double getInfo() {
      return this.inform;
    }
    
    protected double wtMeanSqErr(double param1Double1, double param1Double2, double param1Double3) {
      return Utils.smOrEq(param1Double3, 1.0E-6D) ? 0.0D : (param1Double1 - param1Double2 * param1Double2 / param1Double3);
    }
    
    protected double entropy(double[] param1ArrayOfdouble, double param1Double) {
      if (Utils.smOrEq(param1Double, 1.0E-6D))
        return 0.0D; 
      double d = 0.0D;
      for (byte b = 0; b < param1ArrayOfdouble.length; b++) {
        if (!Utils.eq(param1ArrayOfdouble[b], 0.0D))
          d -= param1ArrayOfdouble[b] * Utils.log2(param1ArrayOfdouble[b]); 
      } 
      d += param1Double * Utils.log2(param1Double);
      d /= param1Double;
      return d;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\ConjunctiveRule.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */