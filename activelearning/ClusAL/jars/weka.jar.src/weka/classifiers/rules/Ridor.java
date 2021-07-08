package weka.classifiers.rules;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.AdditionalMeasureProducer;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class Ridor extends Classifier implements OptionHandler, AdditionalMeasureProducer, WeightedInstancesHandler {
  private int m_Folds = 3;
  
  private int m_Shuffle = 1;
  
  private Random m_Random = null;
  
  private int m_Seed = 1;
  
  private boolean m_IsAllErr = false;
  
  private boolean m_IsMajority = false;
  
  private Ridor_node m_Root = null;
  
  private Attribute m_Class;
  
  private double m_Cover;
  
  private double m_Err;
  
  private double m_MinNo = 2.0D;
  
  public String globalInfo() {
    return "The implementation of a RIpple-DOwn Rule learner. It generates a default rule first and then the exceptions for the default rule with the least (weighted) error rate.  Then it generates the \"best\" exceptions for each exception and iterates until pure.  Thus it performs a tree-like expansion of exceptions.The exceptions are a set of rules that predict classes other than the default. IREP is used to generate the exceptions.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    Instances instances = new Instances(paramInstances);
    if (instances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (Utils.eq(instances.sumOfWeights(), 0.0D))
      throw new Exception("No training data."); 
    instances.deleteWithMissingClass();
    if (Utils.eq(instances.sumOfWeights(), 0.0D))
      throw new Exception("The class labels of all the training data are missing."); 
    int i = instances.numClasses();
    this.m_Root = new Ridor_node();
    this.m_Class = paramInstances.classAttribute();
    if (!this.m_Class.isNominal())
      throw new UnsupportedClassTypeException("Only nominal class, please."); 
    int j = instances.classIndex();
    this.m_Cover = instances.sumOfWeights();
    FastVector fastVector = new FastVector(2);
    fastVector.addElement("otherClasses");
    fastVector.addElement("defClass");
    Attribute attribute = new Attribute("newClass", fastVector);
    instances.insertAttributeAt(attribute, j);
    instances.setClassIndex(j);
    Instances[] arrayOfInstances = new Instances[i];
    byte b;
    for (b = 0; b < i; b++)
      arrayOfInstances[b] = new Instances(instances, instances.numInstances()); 
    for (b = 0; b < instances.numInstances(); b++) {
      Instance instance = instances.instance(b);
      instance.setClassValue(0.0D);
      arrayOfInstances[(int)instance.value(j + 1)].add(instance);
    } 
    for (b = 0; b < i; b++)
      arrayOfInstances[b].deleteAttributeAt(j + 1); 
    this.m_Root.findRules(arrayOfInstances, 0);
  }
  
  public double classifyInstance(Instance paramInstance) {
    return classify(this.m_Root, paramInstance);
  }
  
  private double classify(Ridor_node paramRidor_node, Instance paramInstance) {
    double d = paramRidor_node.getDefClass();
    RidorRule[] arrayOfRidorRule = paramRidor_node.getRules();
    if (arrayOfRidorRule != null) {
      Ridor_node[] arrayOfRidor_node = paramRidor_node.getExcepts();
      for (byte b = 0; b < arrayOfRidor_node.length; b++) {
        if (arrayOfRidorRule[b].isCover(paramInstance)) {
          d = classify(arrayOfRidor_node[b], paramInstance);
          break;
        } 
      } 
    } 
    return d;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(5);
    vector.addElement(new Option("\tSet number of folds for IREP\n\tOne fold is used as pruning set.\n\t(default 3)", "F", 1, "-F <number of folds>"));
    vector.addElement(new Option("\tSet number of shuffles to randomize\n\tthe data in order to get better rule.\n\t(default 10)", "S", 1, "-S <number of shuffles>"));
    vector.addElement(new Option("\tSet flag of whether use the error rate \n\tof all the data to select the default class\n\tin each step. If not set, the learner will only use\tthe error rate in the pruning data", "A", 0, "-A"));
    vector.addElement(new Option("\t Set flag of whether use the majority class as\n\tthe default class in each step instead of \n\tchoosing default class based on the error rate\n\t(if the flag is not set)", "M", 0, "-M"));
    vector.addElement(new Option("\tSet the minimal weights of instances\n\twithin a split.\n\t(default 2.0)", "N", 1, "-N <min. weights>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('F', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_Folds = Integer.parseInt(str1);
    } else {
      this.m_Folds = 3;
    } 
    String str2 = Utils.getOption('S', paramArrayOfString);
    if (str2.length() != 0) {
      this.m_Shuffle = Integer.parseInt(str2);
    } else {
      this.m_Shuffle = 1;
    } 
    String str3 = Utils.getOption('s', paramArrayOfString);
    if (str3.length() != 0) {
      this.m_Seed = Integer.parseInt(str3);
    } else {
      this.m_Seed = 1;
    } 
    String str4 = Utils.getOption('N', paramArrayOfString);
    if (str4.length() != 0) {
      this.m_MinNo = Double.parseDouble(str4);
    } else {
      this.m_MinNo = 2.0D;
    } 
    this.m_IsAllErr = Utils.getFlag('A', paramArrayOfString);
    this.m_IsMajority = Utils.getFlag('M', paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[8];
    byte b = 0;
    arrayOfString[b++] = "-F";
    arrayOfString[b++] = "" + this.m_Folds;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + this.m_Shuffle;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + this.m_MinNo;
    if (this.m_IsAllErr)
      arrayOfString[b++] = "-A"; 
    if (this.m_IsMajority)
      arrayOfString[b++] = "-M"; 
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
  
  public String shuffleTipText() {
    return "Determines how often the data is shuffled before a rule is chosen. If > 1, a rule is learned multiple times and the most accurate rule is chosen.";
  }
  
  public void setShuffle(int paramInt) {
    this.m_Shuffle = paramInt;
  }
  
  public int getShuffle() {
    return this.m_Shuffle;
  }
  
  public String seedTipText() {
    return "The seed used for randomizing the data.";
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public String wholeDataErrTipText() {
    return "Whether worth of rule is computed based on all the data or just based on data covered by rule.";
  }
  
  public void setWholeDataErr(boolean paramBoolean) {
    this.m_IsAllErr = paramBoolean;
  }
  
  public boolean getWholeDataErr() {
    return this.m_IsAllErr;
  }
  
  public String majorityClassTipText() {
    return "Whether the majority class is used as default.";
  }
  
  public void setMajorityClass(boolean paramBoolean) {
    this.m_IsMajority = paramBoolean;
  }
  
  public boolean getMajorityClass() {
    return this.m_IsMajority;
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
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(1);
    vector.addElement("measureNumRules");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.compareToIgnoreCase("measureNumRules") == 0)
      return numRules(); 
    throw new IllegalArgumentException(paramString + " not supported (Ripple down rule learner)");
  }
  
  private double numRules() {
    int i = 0;
    if (this.m_Root != null)
      i = this.m_Root.size(); 
    return (i + 1);
  }
  
  public String toString() {
    return (this.m_Root == null) ? "RIpple DOwn Rule Learner(Ridor): No model built yet." : ("RIpple DOwn Rule Learner(Ridor) rules\n--------------------------------------\n\n" + this.m_Root.toString() + "\nTotal number of rules (incl. the default rule): " + (int)numRules());
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new Ridor(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  private class NominalAntd extends Antd {
    private double[] accurate;
    
    private double[] coverage;
    
    private double[] infoGain;
    
    private final Ridor this$0;
    
    public NominalAntd(Ridor this$0, Attribute param1Attribute) {
      super(this$0, param1Attribute);
      this.this$0 = this$0;
      int i = this.att.numValues();
      this.accurate = new double[i];
      this.coverage = new double[i];
      this.infoGain = new double[i];
    }
    
    public Instances[] splitData(Instances param1Instances, double param1Double1, double param1Double2) {
      // Byte code:
      //   0: aload_0
      //   1: getfield att : Lweka/core/Attribute;
      //   4: invokevirtual numValues : ()I
      //   7: istore #6
      //   9: iload #6
      //   11: anewarray weka/core/Instances
      //   14: astore #7
      //   16: iconst_0
      //   17: istore #8
      //   19: iload #8
      //   21: iload #6
      //   23: if_icmpge -> 73
      //   26: aload_0
      //   27: getfield accurate : [D
      //   30: iload #8
      //   32: aload_0
      //   33: getfield coverage : [D
      //   36: iload #8
      //   38: aload_0
      //   39: getfield infoGain : [D
      //   42: iload #8
      //   44: dconst_0
      //   45: dup2_x2
      //   46: dastore
      //   47: dup2_x2
      //   48: dastore
      //   49: dastore
      //   50: aload #7
      //   52: iload #8
      //   54: new weka/core/Instances
      //   57: dup
      //   58: aload_1
      //   59: aload_1
      //   60: invokevirtual numInstances : ()I
      //   63: invokespecial <init> : (Lweka/core/Instances;I)V
      //   66: aastore
      //   67: iinc #8, 1
      //   70: goto -> 19
      //   73: iconst_0
      //   74: istore #8
      //   76: iload #8
      //   78: aload_1
      //   79: invokevirtual numInstances : ()I
      //   82: if_icmpge -> 176
      //   85: aload_1
      //   86: iload #8
      //   88: invokevirtual instance : (I)Lweka/core/Instance;
      //   91: astore #9
      //   93: aload #9
      //   95: aload_0
      //   96: getfield att : Lweka/core/Attribute;
      //   99: invokevirtual isMissing : (Lweka/core/Attribute;)Z
      //   102: ifne -> 170
      //   105: aload #9
      //   107: aload_0
      //   108: getfield att : Lweka/core/Attribute;
      //   111: invokevirtual value : (Lweka/core/Attribute;)D
      //   114: d2i
      //   115: istore #10
      //   117: aload #7
      //   119: iload #10
      //   121: aaload
      //   122: aload #9
      //   124: invokevirtual add : (Lweka/core/Instance;)V
      //   127: aload_0
      //   128: getfield coverage : [D
      //   131: iload #10
      //   133: dup2
      //   134: daload
      //   135: aload #9
      //   137: invokevirtual weight : ()D
      //   140: dadd
      //   141: dastore
      //   142: aload #9
      //   144: invokevirtual classValue : ()D
      //   147: dload #4
      //   149: invokestatic eq : (DD)Z
      //   152: ifeq -> 170
      //   155: aload_0
      //   156: getfield accurate : [D
      //   159: iload #10
      //   161: dup2
      //   162: daload
      //   163: aload #9
      //   165: invokevirtual weight : ()D
      //   168: dadd
      //   169: dastore
      //   170: iinc #8, 1
      //   173: goto -> 76
      //   176: iconst_0
      //   177: istore #8
      //   179: iconst_0
      //   180: istore #9
      //   182: iload #9
      //   184: iload #6
      //   186: if_icmpge -> 263
      //   189: aload_0
      //   190: getfield coverage : [D
      //   193: iload #9
      //   195: daload
      //   196: dstore #10
      //   198: dload #10
      //   200: aload_0
      //   201: getfield this$0 : Lweka/classifiers/rules/Ridor;
      //   204: invokestatic access$1000 : (Lweka/classifiers/rules/Ridor;)D
      //   207: invokestatic grOrEq : (DD)Z
      //   210: ifeq -> 257
      //   213: aload_0
      //   214: getfield accurate : [D
      //   217: iload #9
      //   219: daload
      //   220: dstore #12
      //   222: dload #10
      //   224: dconst_0
      //   225: invokestatic eq : (DD)Z
      //   228: ifne -> 254
      //   231: aload_0
      //   232: getfield infoGain : [D
      //   235: iload #9
      //   237: dload #12
      //   239: dload #12
      //   241: dload #10
      //   243: ddiv
      //   244: invokestatic log2 : (D)D
      //   247: dload_2
      //   248: invokestatic log2 : (D)D
      //   251: dsub
      //   252: dmul
      //   253: dastore
      //   254: iinc #8, 1
      //   257: iinc #9, 1
      //   260: goto -> 182
      //   263: iload #8
      //   265: iconst_2
      //   266: if_icmpge -> 271
      //   269: aconst_null
      //   270: areturn
      //   271: aload_0
      //   272: aload_0
      //   273: getfield infoGain : [D
      //   276: invokestatic maxIndex : ([D)I
      //   279: i2d
      //   280: putfield value : D
      //   283: aload_0
      //   284: aload_0
      //   285: getfield coverage : [D
      //   288: aload_0
      //   289: getfield value : D
      //   292: d2i
      //   293: daload
      //   294: putfield cover : D
      //   297: aload_0
      //   298: aload_0
      //   299: getfield accurate : [D
      //   302: aload_0
      //   303: getfield value : D
      //   306: d2i
      //   307: daload
      //   308: putfield accu : D
      //   311: aload_0
      //   312: getfield cover : D
      //   315: dconst_0
      //   316: invokestatic eq : (DD)Z
      //   319: ifne -> 338
      //   322: aload_0
      //   323: aload_0
      //   324: getfield accu : D
      //   327: aload_0
      //   328: getfield cover : D
      //   331: ddiv
      //   332: putfield accuRate : D
      //   335: goto -> 343
      //   338: aload_0
      //   339: dconst_0
      //   340: putfield accuRate : D
      //   343: aload_0
      //   344: aload_0
      //   345: getfield infoGain : [D
      //   348: aload_0
      //   349: getfield value : D
      //   352: d2i
      //   353: daload
      //   354: putfield maxInfoGain : D
      //   357: aload #7
      //   359: areturn
    }
    
    public boolean isCover(Instance param1Instance) {
      boolean bool = false;
      if (!param1Instance.isMissing(this.att) && Utils.eq(param1Instance.value(this.att), this.value))
        bool = true; 
      return bool;
    }
    
    public String toString() {
      return this.att.name() + " = " + this.att.value((int)this.value);
    }
  }
  
  private class NumericAntd extends Antd {
    private double splitPoint;
    
    private final Ridor this$0;
    
    public NumericAntd(Ridor this$0, Attribute param1Attribute) {
      super(this$0, param1Attribute);
      this.this$0 = this$0;
      this.splitPoint = Double.NaN;
    }
    
    public double getSplitPoint() {
      return this.splitPoint;
    }
    
    public Instances[] splitData(Instances param1Instances, double param1Double1, double param1Double2) {
      Instances instances = new Instances(param1Instances);
      instances.sort(this.att);
      int i = instances.numInstances();
      byte b1 = 1;
      byte b2 = 0;
      byte b3 = b1;
      this.maxInfoGain = 0.0D;
      this.value = 0.0D;
      double d1 = 0.1D * instances.sumOfWeights() / 2.0D;
      if (Utils.smOrEq(d1, this.this$0.m_MinNo)) {
        d1 = this.this$0.m_MinNo;
      } else if (Utils.gr(d1, 25.0D)) {
        d1 = 25.0D;
      } 
      double d2 = 0.0D;
      double d3 = 0.0D;
      double d4 = 0.0D;
      double d5 = 0.0D;
      byte b4;
      for (b4 = 0; b4 < instances.numInstances(); b4++) {
        Instance instance = instances.instance(b4);
        if (instance.isMissing(this.att)) {
          i = b4;
          break;
        } 
        d3 += instance.weight();
        if (Utils.eq(instance.classValue(), param1Double2))
          d5 += instance.weight(); 
      } 
      if (Utils.sm(d3, 2.0D * d1))
        return null; 
      if (i == 0)
        return null; 
      this.splitPoint = instances.instance(i - 1).value(this.att);
      while (b1 < i) {
        if (!Utils.eq(instances.instance(b1).value(this.att), instances.instance(b2).value(this.att))) {
          for (b4 = b2; b4 < b1; b4++) {
            Instance instance = instances.instance(b4);
            d2 += instance.weight();
            d3 -= instance.weight();
            if (Utils.eq(instances.instance(b4).classValue(), param1Double2)) {
              d4 += instance.weight();
              d5 -= instance.weight();
            } 
          } 
          if (Utils.sm(d2, d1) || Utils.sm(d3, d1)) {
            b2 = b1;
          } else {
            boolean bool;
            double d10;
            double d11;
            double d12;
            double d13;
            double d6 = 0.0D;
            double d7 = 0.0D;
            if (!Utils.eq(d2, 0.0D))
              d6 = d4 / d2; 
            if (!Utils.eq(d3, 0.0D))
              d7 = d5 / d3; 
            double d8 = Utils.eq(d6, 0.0D) ? 0.0D : (d4 * (Utils.log2(d6) - Utils.log2(param1Double1)));
            double d9 = Utils.eq(d7, 0.0D) ? 0.0D : (d5 * (Utils.log2(d7) - Utils.log2(param1Double1)));
            if (Utils.gr(d8, d9) || (Utils.eq(d8, d9) && Utils.grOrEq(d6, d7))) {
              bool = true;
              d11 = d8;
              d10 = d6;
              d13 = d4;
              d12 = d2;
            } else {
              bool = false;
              d11 = d9;
              d10 = d7;
              d13 = d5;
              d12 = d3;
            } 
            boolean bool1 = Utils.gr(d11, this.maxInfoGain);
            if (bool1) {
              this.splitPoint = (instances.instance(b1).value(this.att) + instances.instance(b2).value(this.att)) / 2.0D;
              this.value = (bool ? false : true);
              this.accuRate = d10;
              this.accu = d13;
              this.cover = d12;
              this.maxInfoGain = d11;
              b3 = b1;
            } 
            b2 = b1;
          } 
        } 
        b1++;
      } 
      Instances[] arrayOfInstances = new Instances[2];
      arrayOfInstances[0] = new Instances(instances, 0, b3);
      arrayOfInstances[1] = new Instances(instances, b3, i - b3);
      return arrayOfInstances;
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
    
    protected double accuRate;
    
    protected double cover;
    
    protected double accu;
    
    private final Ridor this$0;
    
    public Antd(Ridor this$0, Attribute param1Attribute) {
      this.this$0 = this$0;
      this.att = param1Attribute;
      this.value = Double.NaN;
      this.maxInfoGain = 0.0D;
      this.accuRate = Double.NaN;
      this.cover = Double.NaN;
      this.accu = Double.NaN;
    }
    
    public abstract Instances[] splitData(Instances param1Instances, double param1Double1, double param1Double2);
    
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
    
    public double getAccuRate() {
      return this.accuRate;
    }
    
    public double getAccu() {
      return this.accu;
    }
    
    public double getCover() {
      return this.cover;
    }
  }
  
  private class RidorRule implements WeightedInstancesHandler, Serializable {
    private double m_Class;
    
    private Attribute m_ClassAttribute;
    
    protected FastVector m_Antds;
    
    private double m_WorthRate;
    
    private double m_Worth;
    
    private double m_CoverP;
    
    private double m_CoverG;
    
    private double m_AccuG;
    
    private final Ridor this$0;
    
    private RidorRule(Ridor this$0) {
      Ridor.this = Ridor.this;
      this.m_Class = -1.0D;
      this.m_Antds = null;
      this.m_WorthRate = 0.0D;
      this.m_Worth = 0.0D;
      this.m_CoverP = 0.0D;
      this.m_CoverG = 0.0D;
      this.m_AccuG = 0.0D;
    }
    
    public void setPredictedClass(double param1Double) {
      this.m_Class = param1Double;
    }
    
    public double getPredictedClass() {
      return this.m_Class;
    }
    
    public void buildClassifier(Instances param1Instances) throws Exception {
      this.m_ClassAttribute = param1Instances.classAttribute();
      if (!this.m_ClassAttribute.isNominal())
        throw new UnsupportedClassTypeException(" Only nominal class, please."); 
      if (param1Instances.numClasses() != 2)
        throw new Exception(" Only 2 classes, please."); 
      Instances instances1 = new Instances(param1Instances);
      if (Utils.eq(instances1.sumOfWeights(), 0.0D))
        throw new Exception(" No training data."); 
      instances1.deleteWithMissingClass();
      if (Utils.eq(instances1.sumOfWeights(), 0.0D))
        throw new Exception(" The class labels of all the training data are missing."); 
      if (instances1.numInstances() < Ridor.this.m_Folds)
        throw new Exception(" Not enough data for REP."); 
      this.m_Antds = new FastVector();
      Ridor.this.m_Random = new Random(Ridor.this.m_Seed);
      instances1.randomize(Ridor.this.m_Random);
      instances1.stratify(Ridor.this.m_Folds);
      Instances instances2 = instances1.trainCV(Ridor.this.m_Folds, Ridor.this.m_Folds - 1, Ridor.this.m_Random);
      Instances instances3 = instances1.testCV(Ridor.this.m_Folds, Ridor.this.m_Folds - 1);
      grow(instances2);
      prune(instances3);
    }
    
    public Instances[] coveredByRule(Instances param1Instances) {
      Instances[] arrayOfInstances = new Instances[2];
      arrayOfInstances[0] = new Instances(param1Instances, param1Instances.numInstances());
      arrayOfInstances[1] = new Instances(param1Instances, param1Instances.numInstances());
      for (byte b = 0; b < param1Instances.numInstances(); b++) {
        Instance instance = param1Instances.instance(b);
        if (isCover(instance)) {
          arrayOfInstances[0].add(instance);
        } else {
          arrayOfInstances[1].add(instance);
        } 
      } 
      return arrayOfInstances;
    }
    
    public boolean isCover(Instance param1Instance) {
      boolean bool = true;
      for (byte b = 0; b < this.m_Antds.size(); b++) {
        Ridor.Antd antd = (Ridor.Antd)this.m_Antds.elementAt(b);
        if (!antd.isCover(param1Instance)) {
          bool = false;
          break;
        } 
      } 
      return bool;
    }
    
    public boolean hasAntds() {
      return (this.m_Antds == null) ? false : ((this.m_Antds.size() > 0));
    }
    
    private void grow(Instances param1Instances) {
      Instances instances = new Instances(param1Instances);
      this.m_AccuG = computeDefAccu(instances);
      this.m_CoverG = instances.sumOfWeights();
      double d = this.m_AccuG / this.m_CoverG;
      boolean[] arrayOfBoolean = new boolean[instances.numAttributes()];
      int i;
      for (i = 0; i < arrayOfBoolean.length; i++)
        arrayOfBoolean[i] = false; 
      i = arrayOfBoolean.length;
      boolean bool = true;
      while (bool) {
        double d1 = 0.0D;
        Ridor.NominalAntd nominalAntd = null;
        Instances instances1 = null;
        Enumeration enumeration = instances.enumerateAttributes();
        byte b = -1;
        while (enumeration.hasMoreElements()) {
          Ridor.NominalAntd nominalAntd1;
          Attribute attribute = enumeration.nextElement();
          b++;
          Ridor.NumericAntd numericAntd = null;
          if (attribute.isNumeric()) {
            numericAntd = new Ridor.NumericAntd(Ridor.this, attribute);
          } else {
            nominalAntd1 = new Ridor.NominalAntd(Ridor.this, attribute);
          } 
          if (!arrayOfBoolean[b]) {
            Instances instances2 = computeInfoGain(instances, d, nominalAntd1);
            if (instances2 != null) {
              double d2 = nominalAntd1.getMaxInfoGain();
              if (Utils.gr(d2, d1)) {
                nominalAntd = nominalAntd1;
                instances1 = instances2;
                d1 = d2;
              } 
            } 
          } 
        } 
        if (nominalAntd == null)
          return; 
        if (!nominalAntd.getAttr().isNumeric()) {
          arrayOfBoolean[nominalAntd.getAttr().index()] = true;
          i--;
        } 
        this.m_Antds.addElement(nominalAntd);
        instances = instances1;
        d = nominalAntd.getAccuRate();
        if (Utils.eq(instances.sumOfWeights(), 0.0D) || Utils.eq(d, 1.0D) || i == 0)
          bool = false; 
      } 
    }
    
    private Instances computeInfoGain(Instances param1Instances, double param1Double, Ridor.Antd param1Antd) {
      Instances instances = new Instances(param1Instances);
      Instances[] arrayOfInstances = param1Antd.splitData(instances, param1Double, this.m_Class);
      return (arrayOfInstances != null) ? arrayOfInstances[(int)param1Antd.getAttrValue()] : null;
    }
    
    private void prune(Instances param1Instances) {
      // Byte code:
      //   0: new weka/core/Instances
      //   3: dup
      //   4: aload_1
      //   5: invokespecial <init> : (Lweka/core/Instances;)V
      //   8: astore_2
      //   9: aload_2
      //   10: invokevirtual sumOfWeights : ()D
      //   13: dstore_3
      //   14: dconst_0
      //   15: dstore #5
      //   17: dconst_0
      //   18: dstore #7
      //   20: aload_0
      //   21: getfield m_Antds : Lweka/core/FastVector;
      //   24: invokevirtual size : ()I
      //   27: istore #9
      //   29: iload #9
      //   31: ifne -> 35
      //   34: return
      //   35: iload #9
      //   37: newarray double
      //   39: astore #10
      //   41: iload #9
      //   43: newarray double
      //   45: astore #11
      //   47: iload #9
      //   49: newarray double
      //   51: astore #12
      //   53: iconst_0
      //   54: istore #13
      //   56: iload #13
      //   58: iload #9
      //   60: if_icmpge -> 87
      //   63: aload #10
      //   65: iload #13
      //   67: aload #11
      //   69: iload #13
      //   71: aload #12
      //   73: iload #13
      //   75: dconst_0
      //   76: dup2_x2
      //   77: dastore
      //   78: dup2_x2
      //   79: dastore
      //   80: dastore
      //   81: iinc #13, 1
      //   84: goto -> 56
      //   87: iconst_0
      //   88: istore #13
      //   90: iload #13
      //   92: iload #9
      //   94: if_icmpge -> 270
      //   97: aload_0
      //   98: getfield m_Antds : Lweka/core/FastVector;
      //   101: iload #13
      //   103: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   106: checkcast weka/classifiers/rules/Ridor$Antd
      //   109: astore #14
      //   111: aload #14
      //   113: invokevirtual getAttr : ()Lweka/core/Attribute;
      //   116: astore #15
      //   118: new weka/core/Instances
      //   121: dup
      //   122: aload_2
      //   123: invokespecial <init> : (Lweka/core/Instances;)V
      //   126: astore #16
      //   128: new weka/core/Instances
      //   131: dup
      //   132: aload #16
      //   134: aload #16
      //   136: invokevirtual numInstances : ()I
      //   139: invokespecial <init> : (Lweka/core/Instances;I)V
      //   142: astore_2
      //   143: iconst_0
      //   144: istore #17
      //   146: iload #17
      //   148: aload #16
      //   150: invokevirtual numInstances : ()I
      //   153: if_icmpge -> 238
      //   156: aload #16
      //   158: iload #17
      //   160: invokevirtual instance : (I)Lweka/core/Instance;
      //   163: astore #18
      //   165: aload #18
      //   167: aload #15
      //   169: invokevirtual isMissing : (Lweka/core/Attribute;)Z
      //   172: ifne -> 232
      //   175: aload #14
      //   177: aload #18
      //   179: invokevirtual isCover : (Lweka/core/Instance;)Z
      //   182: ifeq -> 232
      //   185: aload #11
      //   187: iload #13
      //   189: dup2
      //   190: daload
      //   191: aload #18
      //   193: invokevirtual weight : ()D
      //   196: dadd
      //   197: dastore
      //   198: aload_2
      //   199: aload #18
      //   201: invokevirtual add : (Lweka/core/Instance;)V
      //   204: aload #18
      //   206: invokevirtual classValue : ()D
      //   209: aload_0
      //   210: getfield m_Class : D
      //   213: invokestatic eq : (DD)Z
      //   216: ifeq -> 232
      //   219: aload #12
      //   221: iload #13
      //   223: dup2
      //   224: daload
      //   225: aload #18
      //   227: invokevirtual weight : ()D
      //   230: dadd
      //   231: dastore
      //   232: iinc #17, 1
      //   235: goto -> 146
      //   238: aload #11
      //   240: iload #13
      //   242: daload
      //   243: dconst_0
      //   244: dcmpl
      //   245: ifeq -> 264
      //   248: aload #10
      //   250: iload #13
      //   252: aload #12
      //   254: iload #13
      //   256: daload
      //   257: aload #11
      //   259: iload #13
      //   261: daload
      //   262: ddiv
      //   263: dastore
      //   264: iinc #13, 1
      //   267: goto -> 90
      //   270: iload #9
      //   272: iconst_1
      //   273: isub
      //   274: istore #13
      //   276: iload #13
      //   278: ifle -> 314
      //   281: aload #10
      //   283: iload #13
      //   285: daload
      //   286: aload #10
      //   288: iload #13
      //   290: iconst_1
      //   291: isub
      //   292: daload
      //   293: invokestatic sm : (DD)Z
      //   296: ifeq -> 314
      //   299: aload_0
      //   300: getfield m_Antds : Lweka/core/FastVector;
      //   303: iload #13
      //   305: invokevirtual removeElementAt : (I)V
      //   308: iinc #13, -1
      //   311: goto -> 276
      //   314: aload_0
      //   315: getfield m_Antds : Lweka/core/FastVector;
      //   318: invokevirtual size : ()I
      //   321: iconst_1
      //   322: if_icmpne -> 357
      //   325: aload_0
      //   326: aload_1
      //   327: invokespecial computeDefAccu : (Lweka/core/Instances;)D
      //   330: dstore #5
      //   332: dload #5
      //   334: dload_3
      //   335: ddiv
      //   336: dstore #7
      //   338: aload #10
      //   340: iconst_0
      //   341: daload
      //   342: dload #7
      //   344: invokestatic sm : (DD)Z
      //   347: ifeq -> 357
      //   350: aload_0
      //   351: getfield m_Antds : Lweka/core/FastVector;
      //   354: invokevirtual removeAllElements : ()V
      //   357: aload_0
      //   358: getfield m_Antds : Lweka/core/FastVector;
      //   361: invokevirtual size : ()I
      //   364: istore #13
      //   366: iload #13
      //   368: ifeq -> 437
      //   371: aload_0
      //   372: aload #12
      //   374: iload #13
      //   376: iconst_1
      //   377: isub
      //   378: daload
      //   379: putfield m_Worth : D
      //   382: aload_0
      //   383: aload #10
      //   385: iload #13
      //   387: iconst_1
      //   388: isub
      //   389: daload
      //   390: putfield m_WorthRate : D
      //   393: aload_0
      //   394: aload #11
      //   396: iload #13
      //   398: iconst_1
      //   399: isub
      //   400: daload
      //   401: putfield m_CoverP : D
      //   404: aload_0
      //   405: getfield m_Antds : Lweka/core/FastVector;
      //   408: invokevirtual lastElement : ()Ljava/lang/Object;
      //   411: checkcast weka/classifiers/rules/Ridor$Antd
      //   414: astore #14
      //   416: aload_0
      //   417: aload #14
      //   419: invokevirtual getCover : ()D
      //   422: putfield m_CoverG : D
      //   425: aload_0
      //   426: aload #14
      //   428: invokevirtual getAccu : ()D
      //   431: putfield m_AccuG : D
      //   434: goto -> 454
      //   437: aload_0
      //   438: dload #5
      //   440: putfield m_Worth : D
      //   443: aload_0
      //   444: dload #7
      //   446: putfield m_WorthRate : D
      //   449: aload_0
      //   450: dload_3
      //   451: putfield m_CoverP : D
      //   454: return
    }
    
    private double computeDefAccu(Instances param1Instances) {
      double d = 0.0D;
      for (byte b = 0; b < param1Instances.numInstances(); b++) {
        Instance instance = param1Instances.instance(b);
        if (Utils.eq(instance.classValue(), this.m_Class))
          d += instance.weight(); 
      } 
      return d;
    }
    
    public double getWorthRate() {
      return this.m_WorthRate;
    }
    
    public double getWorth() {
      return this.m_Worth;
    }
    
    public double getCoverP() {
      return this.m_CoverP;
    }
    
    public double getCoverG() {
      return this.m_CoverG;
    }
    
    public double getAccuG() {
      return this.m_AccuG;
    }
    
    public String toString(String param1String1, String param1String2) {
      StringBuffer stringBuffer = new StringBuffer();
      if (this.m_Antds.size() > 0) {
        for (byte b = 0; b < this.m_Antds.size() - 1; b++)
          stringBuffer.append("(" + ((Ridor.Antd)this.m_Antds.elementAt(b)).toString() + ") and "); 
        stringBuffer.append("(" + ((Ridor.Antd)this.m_Antds.lastElement()).toString() + ")");
      } 
      stringBuffer.append(" => " + param1String1 + " = " + param1String2);
      stringBuffer.append("  (" + this.m_CoverG + "/" + (this.m_CoverG - this.m_AccuG) + ") [" + this.m_CoverP + "/" + (this.m_CoverP - this.m_Worth) + "]");
      return stringBuffer.toString();
    }
    
    public String toString() {
      return toString(this.m_ClassAttribute.name(), this.m_ClassAttribute.value((int)this.m_Class));
    }
  }
  
  private class Ridor_node implements Serializable {
    private double defClass;
    
    private Ridor.RidorRule[] rules;
    
    private Ridor_node[] excepts;
    
    private int level;
    
    private final Ridor this$0;
    
    private Ridor_node(Ridor this$0) {
      Ridor.this = Ridor.this;
      this.defClass = Double.NaN;
      this.rules = null;
      this.excepts = null;
    }
    
    public double getDefClass() {
      return this.defClass;
    }
    
    public Ridor.RidorRule[] getRules() {
      return this.rules;
    }
    
    public Ridor_node[] getExcepts() {
      return this.excepts;
    }
    
    public void findRules(Instances[] param1ArrayOfInstances, int param1Int) throws Exception {
      Vector vector = null;
      byte b = -1;
      double[] arrayOfDouble = new double[param1ArrayOfInstances.length];
      byte b1 = 0;
      this.level = param1Int + 1;
      for (byte b2 = 0; b2 < param1ArrayOfInstances.length; b2++) {
        arrayOfDouble[b2] = param1ArrayOfInstances[b2].sumOfWeights();
        if (Utils.grOrEq(arrayOfDouble[b2], Ridor.this.m_Folds))
          b1++; 
      } 
      if (b1 <= 1) {
        this.defClass = Utils.maxIndex(arrayOfDouble);
        return;
      } 
      double d = Utils.sum(arrayOfDouble);
      if (Ridor.this.m_IsMajority) {
        this.defClass = Utils.maxIndex(arrayOfDouble);
        Instances instances = new Instances(param1ArrayOfInstances[(int)this.defClass]);
        int j = instances.classIndex();
        byte b5;
        for (b5 = 0; b5 < instances.numInstances(); b5++)
          instances.instance(b5).setClassValue(1.0D); 
        for (b5 = 0; b5 < param1ArrayOfInstances.length; b5++) {
          if (b5 != (int)this.defClass)
            if (instances.numInstances() >= param1ArrayOfInstances[b5].numInstances()) {
              instances = append(instances, param1ArrayOfInstances[b5]);
            } else {
              instances = append(param1ArrayOfInstances[b5], instances);
            }  
        } 
        instances.setClassIndex(j);
        double d1 = d - arrayOfDouble[(int)this.defClass];
        vector = new Vector();
        buildRuleset(instances, d1, vector);
        if (vector.size() == 0)
          return; 
      } else {
        double d1 = arrayOfDouble[Utils.maxIndex(arrayOfDouble)] / d;
        for (byte b5 = 0; b5 < param1ArrayOfInstances.length; b5++) {
          if (arrayOfDouble[b5] >= Ridor.this.m_Folds) {
            Instances instances = new Instances(param1ArrayOfInstances[b5]);
            int j = instances.classIndex();
            byte b6;
            for (b6 = 0; b6 < instances.numInstances(); b6++)
              instances.instance(b6).setClassValue(1.0D); 
            for (b6 = 0; b6 < param1ArrayOfInstances.length; b6++) {
              if (b6 != b5)
                if (instances.numInstances() >= param1ArrayOfInstances[b6].numInstances()) {
                  instances = append(instances, param1ArrayOfInstances[b6]);
                } else {
                  instances = append(param1ArrayOfInstances[b6], instances);
                }  
            } 
            instances.setClassIndex(j);
            double d2 = instances.sumOfWeights() - arrayOfDouble[b5];
            Vector vector1 = new Vector();
            double d3 = buildRuleset(instances, d2, vector1);
            if (Utils.gr(d3, d1)) {
              vector = vector1;
              d1 = d3;
              b = b5;
            } 
          } 
        } 
        if (vector == null) {
          this.defClass = Utils.maxIndex(arrayOfDouble);
          return;
        } 
        this.defClass = b;
      } 
      int i = vector.size();
      this.rules = new Ridor.RidorRule[i];
      this.excepts = new Ridor_node[i];
      for (byte b3 = 0; b3 < i; b3++)
        this.rules[b3] = vector.elementAt(b3); 
      Instances[] arrayOfInstances = param1ArrayOfInstances;
      if (this.level == 1)
        Ridor.this.m_Err = d - arrayOfInstances[(int)this.defClass].sumOfWeights(); 
      arrayOfInstances[(int)this.defClass] = new Instances(arrayOfInstances[(int)this.defClass], 0);
      for (byte b4 = 0; b4 < i; b4++) {
        Instances[][] arrayOfInstances1 = divide(this.rules[b4], arrayOfInstances);
        Instances[] arrayOfInstances2 = arrayOfInstances1[0];
        this.excepts[b4] = new Ridor_node();
        this.excepts[b4].findRules(arrayOfInstances2, this.level);
      } 
    }
    
    private double buildRuleset(Instances param1Instances, double param1Double, Vector param1Vector) throws Exception {
      Instances instances = new Instances(param1Instances);
      double d1 = 0.0D;
      double d2 = instances.sumOfWeights();
      while (param1Double >= Ridor.this.m_Folds) {
        Ridor.RidorRule ridorRule1 = null;
        double d4 = -1.0D;
        double d5 = -1.0D;
        Ridor.RidorRule ridorRule2 = new Ridor.RidorRule();
        ridorRule2.setPredictedClass(0.0D);
        for (byte b1 = 0; b1 < Ridor.this.m_Shuffle; b1++) {
          double d7;
          double d8;
          if (Ridor.this.m_Shuffle > 1)
            instances.randomize(Ridor.this.m_Random); 
          ridorRule2.buildClassifier(instances);
          if (Ridor.this.m_IsAllErr) {
            d7 = (ridorRule2.getWorth() + ridorRule2.getAccuG()) / (ridorRule2.getCoverP() + ridorRule2.getCoverG());
            d8 = ridorRule2.getWorth() + ridorRule2.getAccuG();
          } else {
            d7 = ridorRule2.getWorthRate();
            d8 = ridorRule2.getWorth();
          } 
          if (Utils.gr(d7, d4) || (Utils.eq(d7, d4) && Utils.gr(d8, d5))) {
            ridorRule1 = ridorRule2;
            d4 = d7;
            d5 = d8;
          } 
        } 
        if (ridorRule1 == null)
          throw new Exception("Something wrong here inside findRule()!"); 
        if (Utils.sm(d4, 0.5D) || !ridorRule1.hasAntds())
          break; 
        Instances instances1 = new Instances(instances);
        instances = new Instances(instances1, 0);
        param1Double = 0.0D;
        double d6 = 0.0D;
        for (byte b2 = 0; b2 < instances1.numInstances(); b2++) {
          Instance instance = instances1.instance(b2);
          if (!ridorRule1.isCover(instance)) {
            instances.add(instance);
            if (Utils.eq(instance.classValue(), 0.0D))
              param1Double += instance.weight(); 
          } else {
            d6 += instance.weight();
          } 
        } 
        d1 += computeWeightedAcRt(d4, d6, d2);
        param1Vector.addElement(ridorRule1);
      } 
      double d3 = (instances.sumOfWeights() - param1Double) / d2;
      d1 += d3;
      return d1;
    }
    
    private Instances append(Instances param1Instances1, Instances param1Instances2) {
      Instances instances = new Instances(param1Instances1);
      for (byte b = 0; b < param1Instances2.numInstances(); b++)
        instances.add(param1Instances2.instance(b)); 
      return instances;
    }
    
    private double computeWeightedAcRt(double param1Double1, double param1Double2, double param1Double3) {
      return param1Double1 * param1Double2 / param1Double3;
    }
    
    private Instances[][] divide(Ridor.RidorRule param1RidorRule, Instances[] param1ArrayOfInstances) {
      int i = param1ArrayOfInstances.length;
      Instances[][] arrayOfInstances = new Instances[2][i];
      for (byte b = 0; b < i; b++) {
        Instances[] arrayOfInstances1 = param1RidorRule.coveredByRule(param1ArrayOfInstances[b]);
        arrayOfInstances[0][b] = arrayOfInstances1[0];
        arrayOfInstances[1][b] = arrayOfInstances1[1];
      } 
      return arrayOfInstances;
    }
    
    public int size() {
      int i = 0;
      if (this.rules != null) {
        for (byte b = 0; b < this.rules.length; b++)
          i += this.excepts[b].size(); 
        i += this.rules.length;
      } 
      return i;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      if (this.level == 1)
        stringBuffer.append(Ridor.this.m_Class.name() + " = " + Ridor.this.m_Class.value((int)getDefClass()) + "  (" + Ridor.this.m_Cover + "/" + Ridor.this.m_Err + ")\n"); 
      if (this.rules != null)
        for (byte b = 0; b < this.rules.length; b++) {
          for (byte b1 = 0; b1 < this.level; b1++)
            stringBuffer.append("         "); 
          String str = Ridor.this.m_Class.value((int)this.excepts[b].getDefClass());
          stringBuffer.append("  Except " + this.rules[b].toString(Ridor.this.m_Class.name(), str) + "\n" + this.excepts[b].toString());
        }  
      return stringBuffer.toString();
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\Ridor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */