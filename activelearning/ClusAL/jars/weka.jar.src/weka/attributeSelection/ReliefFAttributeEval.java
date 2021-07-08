package weka.attributeSelection;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class ReliefFAttributeEval extends AttributeEvaluator implements OptionHandler {
  private Instances m_trainInstances;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private int m_numInstances;
  
  private boolean m_numericClass;
  
  private int m_numClasses;
  
  private double m_ndc;
  
  private double[] m_nda;
  
  private double[] m_ndcda;
  
  private double[] m_weights;
  
  private double[] m_classProbs;
  
  private int m_sampleM;
  
  private int m_Knn;
  
  private double[][][] m_karray;
  
  private double[] m_maxArray;
  
  private double[] m_minArray;
  
  private double[] m_worst;
  
  private int[] m_index;
  
  private int[] m_stored;
  
  private int m_seed;
  
  private double[] m_weightsByRank;
  
  private int m_sigma;
  
  private boolean m_weightByDistance;
  
  public String globalInfo() {
    return "ReliefFAttributeEval :\n\nEvaluates the worth of an attribute by repeatedly sampling an instance and considering the value of the given attribute for the nearest instance of the same and different class. Can operate on both discrete and continuous class data.\n";
  }
  
  public ReliefFAttributeEval() {
    resetOptions();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tSpecify the number of instances to\n\tsample when estimating attributes.\n\tIf not specified, then all instances\n\twill be used.", "M", 1, "-M <num instances>"));
    vector.addElement(new Option("\tSeed for randomly sampling instances.\n\t(Default = 1)", "D", 1, "-D <seed>"));
    vector.addElement(new Option("\tNumber of nearest neighbours (k) used\n\tto estimate attribute relevances\n\t(Default = 10).", "K", 1, "-K <number of neighbours>"));
    vector.addElement(new Option("\tWeight nearest neighbours by distance\n", "W", 0, "-W"));
    vector.addElement(new Option("\tSpecify sigma value (used in an exp\n\tfunction to control how quickly\n\tweights for more distant instances\n\tdecrease. Use in conjunction with -W.\n\tSensible value=1/5 to 1/10 of the\n\tnumber of nearest neighbours.\n\t(Default = 2)", "A", 1, "-A <num>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    setWeightByDistance(Utils.getFlag('W', paramArrayOfString));
    String str = Utils.getOption('M', paramArrayOfString);
    if (str.length() != 0)
      setSampleSize(Integer.parseInt(str)); 
    str = Utils.getOption('D', paramArrayOfString);
    if (str.length() != 0)
      setSeed(Integer.parseInt(str)); 
    str = Utils.getOption('K', paramArrayOfString);
    if (str.length() != 0)
      setNumNeighbours(Integer.parseInt(str)); 
    str = Utils.getOption('A', paramArrayOfString);
    if (str.length() != 0) {
      setWeightByDistance(true);
      setSigma(Integer.parseInt(str));
    } 
  }
  
  public String sigmaTipText() {
    return "Set influence of nearest neighbours. Used in an exp function to control how quickly weights decrease for more distant instances. Use in conjunction with weightByDistance. Sensible values = 1/5 to 1/10 the number of nearest neighbours.";
  }
  
  public void setSigma(int paramInt) throws Exception {
    if (paramInt <= 0)
      throw new Exception("value of sigma must be > 0!"); 
    this.m_sigma = paramInt;
  }
  
  public int getSigma() {
    return this.m_sigma;
  }
  
  public String numNeighboursTipText() {
    return "Number of nearest neighbours for attribute estimation.";
  }
  
  public void setNumNeighbours(int paramInt) {
    this.m_Knn = paramInt;
  }
  
  public int getNumNeighbours() {
    return this.m_Knn;
  }
  
  public String seedTipText() {
    return "Random seed for sampling instances.";
  }
  
  public void setSeed(int paramInt) {
    this.m_seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_seed;
  }
  
  public String sampleSizeTipText() {
    return "Number of instances to sample. Default (-1) indicates that all instances will be used for attribute estimation.";
  }
  
  public void setSampleSize(int paramInt) {
    this.m_sampleM = paramInt;
  }
  
  public int getSampleSize() {
    return this.m_sampleM;
  }
  
  public String weightByDistanceTipText() {
    return "Weight nearest neighbours by their distance.";
  }
  
  public void setWeightByDistance(boolean paramBoolean) {
    this.m_weightByDistance = paramBoolean;
  }
  
  public boolean getWeightByDistance() {
    return this.m_weightByDistance;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[9];
    byte b = 0;
    if (getWeightByDistance())
      arrayOfString[b++] = "-W"; 
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getSampleSize();
    arrayOfString[b++] = "-D";
    arrayOfString[b++] = "" + getSeed();
    arrayOfString[b++] = "-K";
    arrayOfString[b++] = "" + getNumNeighbours();
    arrayOfString[b++] = "-A";
    arrayOfString[b++] = "" + getSigma();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_trainInstances == null) {
      stringBuffer.append("ReliefF feature evaluator has not been built yet\n");
    } else {
      stringBuffer.append("\tReliefF Ranking Filter");
      stringBuffer.append("\n\tInstances sampled: ");
      if (this.m_sampleM == -1) {
        stringBuffer.append("all\n");
      } else {
        stringBuffer.append(this.m_sampleM + "\n");
      } 
      stringBuffer.append("\tNumber of nearest neighbours (k): " + this.m_Knn + "\n");
      if (this.m_weightByDistance) {
        stringBuffer.append("\tExponentially decreasing (with distance) influence for\n\tnearest neighbours. Sigma: " + this.m_sigma + "\n");
      } else {
        stringBuffer.append("\tEqual influence nearest neighbours\n");
      } 
    } 
    return stringBuffer.toString();
  }
  
  public void buildEvaluator(Instances paramInstances) throws Exception {
    // Byte code:
    //   0: new java/util/Random
    //   3: dup
    //   4: aload_0
    //   5: getfield m_seed : I
    //   8: i2l
    //   9: invokespecial <init> : (J)V
    //   12: astore #4
    //   14: aload_1
    //   15: invokevirtual checkForStringAttributes : ()Z
    //   18: ifeq -> 31
    //   21: new weka/core/UnsupportedAttributeTypeException
    //   24: dup
    //   25: ldc 'Can't handle string attributes!'
    //   27: invokespecial <init> : (Ljava/lang/String;)V
    //   30: athrow
    //   31: aload_0
    //   32: aload_1
    //   33: putfield m_trainInstances : Lweka/core/Instances;
    //   36: aload_0
    //   37: aload_0
    //   38: getfield m_trainInstances : Lweka/core/Instances;
    //   41: invokevirtual classIndex : ()I
    //   44: putfield m_classIndex : I
    //   47: aload_0
    //   48: aload_0
    //   49: getfield m_trainInstances : Lweka/core/Instances;
    //   52: invokevirtual numAttributes : ()I
    //   55: putfield m_numAttribs : I
    //   58: aload_0
    //   59: aload_0
    //   60: getfield m_trainInstances : Lweka/core/Instances;
    //   63: invokevirtual numInstances : ()I
    //   66: putfield m_numInstances : I
    //   69: aload_0
    //   70: getfield m_trainInstances : Lweka/core/Instances;
    //   73: aload_0
    //   74: getfield m_classIndex : I
    //   77: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   80: invokevirtual isNumeric : ()Z
    //   83: ifeq -> 94
    //   86: aload_0
    //   87: iconst_1
    //   88: putfield m_numericClass : Z
    //   91: goto -> 99
    //   94: aload_0
    //   95: iconst_0
    //   96: putfield m_numericClass : Z
    //   99: aload_0
    //   100: getfield m_numericClass : Z
    //   103: ifne -> 127
    //   106: aload_0
    //   107: aload_0
    //   108: getfield m_trainInstances : Lweka/core/Instances;
    //   111: aload_0
    //   112: getfield m_classIndex : I
    //   115: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   118: invokevirtual numValues : ()I
    //   121: putfield m_numClasses : I
    //   124: goto -> 157
    //   127: aload_0
    //   128: dconst_0
    //   129: putfield m_ndc : D
    //   132: aload_0
    //   133: iconst_1
    //   134: putfield m_numClasses : I
    //   137: aload_0
    //   138: aload_0
    //   139: getfield m_numAttribs : I
    //   142: newarray double
    //   144: putfield m_nda : [D
    //   147: aload_0
    //   148: aload_0
    //   149: getfield m_numAttribs : I
    //   152: newarray double
    //   154: putfield m_ndcda : [D
    //   157: aload_0
    //   158: getfield m_weightByDistance : Z
    //   161: ifeq -> 222
    //   164: aload_0
    //   165: aload_0
    //   166: getfield m_Knn : I
    //   169: newarray double
    //   171: putfield m_weightsByRank : [D
    //   174: iconst_0
    //   175: istore #5
    //   177: iload #5
    //   179: aload_0
    //   180: getfield m_Knn : I
    //   183: if_icmpge -> 222
    //   186: aload_0
    //   187: getfield m_weightsByRank : [D
    //   190: iload #5
    //   192: iload #5
    //   194: i2d
    //   195: aload_0
    //   196: getfield m_sigma : I
    //   199: i2d
    //   200: ddiv
    //   201: iload #5
    //   203: i2d
    //   204: aload_0
    //   205: getfield m_sigma : I
    //   208: i2d
    //   209: ddiv
    //   210: dmul
    //   211: dneg
    //   212: invokestatic exp : (D)D
    //   215: dastore
    //   216: iinc #5, 1
    //   219: goto -> 177
    //   222: aload_0
    //   223: aload_0
    //   224: getfield m_numAttribs : I
    //   227: newarray double
    //   229: putfield m_weights : [D
    //   232: aload_0
    //   233: aload_0
    //   234: getfield m_numClasses : I
    //   237: aload_0
    //   238: getfield m_Knn : I
    //   241: iconst_2
    //   242: multianewarray[[[D 3
    //   246: putfield m_karray : [[[D
    //   249: aload_0
    //   250: getfield m_numericClass : Z
    //   253: ifne -> 343
    //   256: aload_0
    //   257: aload_0
    //   258: getfield m_numClasses : I
    //   261: newarray double
    //   263: putfield m_classProbs : [D
    //   266: iconst_0
    //   267: istore #5
    //   269: iload #5
    //   271: aload_0
    //   272: getfield m_numInstances : I
    //   275: if_icmpge -> 310
    //   278: aload_0
    //   279: getfield m_classProbs : [D
    //   282: aload_0
    //   283: getfield m_trainInstances : Lweka/core/Instances;
    //   286: iload #5
    //   288: invokevirtual instance : (I)Lweka/core/Instance;
    //   291: aload_0
    //   292: getfield m_classIndex : I
    //   295: invokevirtual value : (I)D
    //   298: d2i
    //   299: dup2
    //   300: daload
    //   301: dconst_1
    //   302: dadd
    //   303: dastore
    //   304: iinc #5, 1
    //   307: goto -> 269
    //   310: iconst_0
    //   311: istore #5
    //   313: iload #5
    //   315: aload_0
    //   316: getfield m_numClasses : I
    //   319: if_icmpge -> 343
    //   322: aload_0
    //   323: getfield m_classProbs : [D
    //   326: iload #5
    //   328: dup2
    //   329: daload
    //   330: aload_0
    //   331: getfield m_numInstances : I
    //   334: i2d
    //   335: ddiv
    //   336: dastore
    //   337: iinc #5, 1
    //   340: goto -> 313
    //   343: aload_0
    //   344: aload_0
    //   345: getfield m_numClasses : I
    //   348: newarray double
    //   350: putfield m_worst : [D
    //   353: aload_0
    //   354: aload_0
    //   355: getfield m_numClasses : I
    //   358: newarray int
    //   360: putfield m_index : [I
    //   363: aload_0
    //   364: aload_0
    //   365: getfield m_numClasses : I
    //   368: newarray int
    //   370: putfield m_stored : [I
    //   373: aload_0
    //   374: aload_0
    //   375: getfield m_numAttribs : I
    //   378: newarray double
    //   380: putfield m_minArray : [D
    //   383: aload_0
    //   384: aload_0
    //   385: getfield m_numAttribs : I
    //   388: newarray double
    //   390: putfield m_maxArray : [D
    //   393: iconst_0
    //   394: istore #5
    //   396: iload #5
    //   398: aload_0
    //   399: getfield m_numAttribs : I
    //   402: if_icmpge -> 429
    //   405: aload_0
    //   406: getfield m_minArray : [D
    //   409: iload #5
    //   411: aload_0
    //   412: getfield m_maxArray : [D
    //   415: iload #5
    //   417: ldc2_w NaN
    //   420: dup2_x2
    //   421: dastore
    //   422: dastore
    //   423: iinc #5, 1
    //   426: goto -> 396
    //   429: iconst_0
    //   430: istore #5
    //   432: iload #5
    //   434: aload_0
    //   435: getfield m_numInstances : I
    //   438: if_icmpge -> 460
    //   441: aload_0
    //   442: aload_0
    //   443: getfield m_trainInstances : Lweka/core/Instances;
    //   446: iload #5
    //   448: invokevirtual instance : (I)Lweka/core/Instance;
    //   451: invokespecial updateMinMax : (Lweka/core/Instance;)V
    //   454: iinc #5, 1
    //   457: goto -> 432
    //   460: aload_0
    //   461: getfield m_sampleM : I
    //   464: aload_0
    //   465: getfield m_numInstances : I
    //   468: if_icmpgt -> 478
    //   471: aload_0
    //   472: getfield m_sampleM : I
    //   475: ifge -> 486
    //   478: aload_0
    //   479: getfield m_numInstances : I
    //   482: istore_3
    //   483: goto -> 491
    //   486: aload_0
    //   487: getfield m_sampleM : I
    //   490: istore_3
    //   491: iconst_0
    //   492: istore #5
    //   494: iload #5
    //   496: iload_3
    //   497: if_icmpge -> 660
    //   500: iload_3
    //   501: aload_0
    //   502: getfield m_numInstances : I
    //   505: if_icmpne -> 514
    //   508: iload #5
    //   510: istore_2
    //   511: goto -> 525
    //   514: aload #4
    //   516: invokevirtual nextInt : ()I
    //   519: aload_0
    //   520: getfield m_numInstances : I
    //   523: irem
    //   524: istore_2
    //   525: iload_2
    //   526: ifge -> 533
    //   529: iload_2
    //   530: iconst_m1
    //   531: imul
    //   532: istore_2
    //   533: aload_0
    //   534: getfield m_trainInstances : Lweka/core/Instances;
    //   537: iload_2
    //   538: invokevirtual instance : (I)Lweka/core/Instance;
    //   541: aload_0
    //   542: getfield m_classIndex : I
    //   545: invokevirtual isMissing : (I)Z
    //   548: ifne -> 654
    //   551: iconst_0
    //   552: istore #6
    //   554: iload #6
    //   556: aload_0
    //   557: getfield m_numClasses : I
    //   560: if_icmpge -> 629
    //   563: aload_0
    //   564: getfield m_index : [I
    //   567: iload #6
    //   569: aload_0
    //   570: getfield m_stored : [I
    //   573: iload #6
    //   575: iconst_0
    //   576: dup_x2
    //   577: iastore
    //   578: iastore
    //   579: iconst_0
    //   580: istore #7
    //   582: iload #7
    //   584: aload_0
    //   585: getfield m_Knn : I
    //   588: if_icmpge -> 623
    //   591: aload_0
    //   592: getfield m_karray : [[[D
    //   595: iload #6
    //   597: aaload
    //   598: iload #7
    //   600: aaload
    //   601: iconst_0
    //   602: aload_0
    //   603: getfield m_karray : [[[D
    //   606: iload #6
    //   608: aaload
    //   609: iload #7
    //   611: aaload
    //   612: iconst_1
    //   613: dconst_0
    //   614: dup2_x2
    //   615: dastore
    //   616: dastore
    //   617: iinc #7, 1
    //   620: goto -> 582
    //   623: iinc #6, 1
    //   626: goto -> 554
    //   629: aload_0
    //   630: iload_2
    //   631: invokespecial findKHitMiss : (I)V
    //   634: aload_0
    //   635: getfield m_numericClass : Z
    //   638: ifeq -> 649
    //   641: aload_0
    //   642: iload_2
    //   643: invokespecial updateWeightsNumericClass : (I)V
    //   646: goto -> 654
    //   649: aload_0
    //   650: iload_2
    //   651: invokespecial updateWeightsDiscreteClass : (I)V
    //   654: iinc #5, 1
    //   657: goto -> 494
    //   660: iconst_0
    //   661: istore #5
    //   663: iload #5
    //   665: aload_0
    //   666: getfield m_numAttribs : I
    //   669: if_icmpge -> 754
    //   672: iload #5
    //   674: aload_0
    //   675: getfield m_classIndex : I
    //   678: if_icmpeq -> 748
    //   681: aload_0
    //   682: getfield m_numericClass : Z
    //   685: ifeq -> 734
    //   688: aload_0
    //   689: getfield m_weights : [D
    //   692: iload #5
    //   694: aload_0
    //   695: getfield m_ndcda : [D
    //   698: iload #5
    //   700: daload
    //   701: aload_0
    //   702: getfield m_ndc : D
    //   705: ddiv
    //   706: aload_0
    //   707: getfield m_nda : [D
    //   710: iload #5
    //   712: daload
    //   713: aload_0
    //   714: getfield m_ndcda : [D
    //   717: iload #5
    //   719: daload
    //   720: dsub
    //   721: iload_3
    //   722: i2d
    //   723: aload_0
    //   724: getfield m_ndc : D
    //   727: dsub
    //   728: ddiv
    //   729: dsub
    //   730: dastore
    //   731: goto -> 748
    //   734: aload_0
    //   735: getfield m_weights : [D
    //   738: iload #5
    //   740: dup2
    //   741: daload
    //   742: dconst_1
    //   743: iload_3
    //   744: i2d
    //   745: ddiv
    //   746: dmul
    //   747: dastore
    //   748: iinc #5, 1
    //   751: goto -> 663
    //   754: return
  }
  
  public double evaluateAttribute(int paramInt) throws Exception {
    return this.m_weights[paramInt];
  }
  
  protected void resetOptions() {
    this.m_trainInstances = null;
    this.m_sampleM = -1;
    this.m_Knn = 10;
    this.m_sigma = 2;
    this.m_weightByDistance = false;
    this.m_seed = 1;
  }
  
  private double norm(double paramDouble, int paramInt) {
    return (Double.isNaN(this.m_minArray[paramInt]) || Utils.eq(this.m_maxArray[paramInt], this.m_minArray[paramInt])) ? 0.0D : ((paramDouble - this.m_minArray[paramInt]) / (this.m_maxArray[paramInt] - this.m_minArray[paramInt]));
  }
  
  private void updateMinMax(Instance paramInstance) {
    try {
      for (byte b = 0; b < paramInstance.numValues(); b++) {
        if (paramInstance.attributeSparse(b).isNumeric() && !paramInstance.isMissingSparse(b))
          if (Double.isNaN(this.m_minArray[paramInstance.index(b)])) {
            this.m_minArray[paramInstance.index(b)] = paramInstance.valueSparse(b);
            this.m_maxArray[paramInstance.index(b)] = paramInstance.valueSparse(b);
          } else if (paramInstance.valueSparse(b) < this.m_minArray[paramInstance.index(b)]) {
            this.m_minArray[paramInstance.index(b)] = paramInstance.valueSparse(b);
          } else if (paramInstance.valueSparse(b) > this.m_maxArray[paramInstance.index(b)]) {
            this.m_maxArray[paramInstance.index(b)] = paramInstance.valueSparse(b);
          }  
      } 
    } catch (Exception exception) {
      System.err.println(exception);
      exception.printStackTrace();
    } 
  }
  
  private double difference(int paramInt, double paramDouble1, double paramDouble2) {
    switch (this.m_trainInstances.attribute(paramInt).type()) {
      case 1:
        return (Instance.isMissingValue(paramDouble1) || Instance.isMissingValue(paramDouble2)) ? (1.0D - 1.0D / this.m_trainInstances.attribute(paramInt).numValues()) : (((int)paramDouble1 != (int)paramDouble2) ? 1.0D : 0.0D);
      case 0:
        if (Instance.isMissingValue(paramDouble1) || Instance.isMissingValue(paramDouble2)) {
          double d;
          if (Instance.isMissingValue(paramDouble1) && Instance.isMissingValue(paramDouble2))
            return 1.0D; 
          if (Instance.isMissingValue(paramDouble2)) {
            d = norm(paramDouble1, paramInt);
          } else {
            d = norm(paramDouble2, paramInt);
          } 
          if (d < 0.5D)
            d = 1.0D - d; 
          return d;
        } 
        return Math.abs(norm(paramDouble1, paramInt) - norm(paramDouble2, paramInt));
    } 
    return 0.0D;
  }
  
  private double distance(Instance paramInstance1, Instance paramInstance2) {
    double d = 0.0D;
    byte b1 = 0;
    byte b2 = 0;
    while (true) {
      if (b1 < paramInstance1.numValues() || b2 < paramInstance2.numValues()) {
        int i;
        int j;
        double d1;
        if (b1 >= paramInstance1.numValues()) {
          i = this.m_trainInstances.numAttributes();
        } else {
          i = paramInstance1.index(b1);
        } 
        if (b2 >= paramInstance2.numValues()) {
          j = this.m_trainInstances.numAttributes();
        } else {
          j = paramInstance2.index(b2);
        } 
        if (i == this.m_trainInstances.classIndex()) {
          b1++;
          continue;
        } 
        if (j == this.m_trainInstances.classIndex()) {
          b2++;
          continue;
        } 
        if (i == j) {
          d1 = difference(i, paramInstance1.valueSparse(b1), paramInstance2.valueSparse(b2));
          b1++;
          b2++;
        } else if (i > j) {
          d1 = difference(j, 0.0D, paramInstance2.valueSparse(b2));
          b2++;
        } else {
          d1 = difference(i, paramInstance1.valueSparse(b1), 0.0D);
          b1++;
        } 
        d += d1;
        continue;
      } 
      return d;
    } 
  }
  
  private void updateWeightsNumericClass(int paramInt) {
    int[] arrayOfInt = null;
    double[] arrayOfDouble = null;
    double d = 1.0D;
    Instance instance = this.m_trainInstances.instance(paramInt);
    if (this.m_weightByDistance) {
      arrayOfDouble = new double[this.m_stored[0]];
      byte b1 = 0;
      d = 0.0D;
      while (b1 < this.m_stored[0]) {
        arrayOfDouble[b1] = this.m_karray[0][b1][0];
        d += this.m_weightsByRank[b1];
        b1++;
      } 
      arrayOfInt = Utils.sort(arrayOfDouble);
    } 
    byte b = 0;
    while (b < this.m_stored[0]) {
      double d1;
      if (this.m_weightByDistance) {
        d1 = difference(this.m_classIndex, instance.value(this.m_classIndex), this.m_trainInstances.instance((int)this.m_karray[0][arrayOfInt[b]][1]).value(this.m_classIndex));
        d1 *= this.m_weightsByRank[b] / d;
      } else {
        d1 = difference(this.m_classIndex, instance.value(this.m_classIndex), this.m_trainInstances.instance((int)this.m_karray[0][b][1]).value(this.m_classIndex));
        d1 *= 1.0D / this.m_stored[0];
      } 
      this.m_ndc += d1;
      Instance instance1 = this.m_weightByDistance ? this.m_trainInstances.instance((int)this.m_karray[0][arrayOfInt[b]][1]) : this.m_trainInstances.instance((int)this.m_karray[0][b][1]);
      double d2 = difference(this.m_classIndex, instance.value(this.m_classIndex), instance1.value(this.m_classIndex));
      byte b1 = 0;
      byte b2 = 0;
      while (true) {
        if (b1 < instance.numValues() || b2 < instance1.numValues()) {
          int i;
          int j;
          int k;
          if (b1 >= instance.numValues()) {
            j = this.m_trainInstances.numAttributes();
          } else {
            j = instance.index(b1);
          } 
          if (b2 >= instance1.numValues()) {
            k = this.m_trainInstances.numAttributes();
          } else {
            k = instance1.index(b2);
          } 
          if (j == this.m_trainInstances.classIndex()) {
            b1++;
            continue;
          } 
          if (k == this.m_trainInstances.classIndex()) {
            b2++;
            continue;
          } 
          d1 = 0.0D;
          double d3 = 0.0D;
          if (j == k) {
            i = j;
            d1 = difference(i, instance.valueSparse(b1), instance1.valueSparse(b2));
            b1++;
            b2++;
          } else if (j > k) {
            i = k;
            d1 = difference(i, 0.0D, instance1.valueSparse(b2));
            b2++;
          } else {
            i = j;
            d1 = difference(i, instance.valueSparse(b1), 0.0D);
            b1++;
          } 
          d3 = d2 * d1;
          if (this.m_weightByDistance) {
            d3 *= this.m_weightsByRank[b] / d;
          } else {
            d3 *= 1.0D / this.m_stored[0];
          } 
          this.m_ndcda[i] = this.m_ndcda[i] + d3;
          if (this.m_weightByDistance) {
            d1 *= this.m_weightsByRank[b] / d;
          } else {
            d1 *= 1.0D / this.m_stored[0];
          } 
          this.m_nda[i] = this.m_nda[i] + d1;
          continue;
        } 
        b++;
      } 
    } 
  }
  
  private void updateWeightsDiscreteClass(int paramInt) {
    double d1 = this.m_numInstances;
    double d3 = 1.0D;
    int[] arrayOfInt = null;
    double d4 = 1.0D;
    int[][] arrayOfInt1 = (int[][])null;
    double[] arrayOfDouble = null;
    Instance instance = this.m_trainInstances.instance(paramInt);
    int i = (int)this.m_trainInstances.instance(paramInt).value(this.m_classIndex);
    if (this.m_weightByDistance) {
      double[] arrayOfDouble1 = new double[this.m_stored[i]];
      byte b3 = 0;
      d4 = 0.0D;
      while (b3 < this.m_stored[i]) {
        arrayOfDouble1[b3] = this.m_karray[i][b3][0];
        d4 += this.m_weightsByRank[b3];
        b3++;
      } 
      arrayOfInt = Utils.sort(arrayOfDouble1);
      arrayOfInt1 = new int[this.m_numClasses][1];
      arrayOfDouble = new double[this.m_numClasses];
      for (byte b4 = 0; b4 < this.m_numClasses; b4++) {
        if (b4 != i) {
          double[] arrayOfDouble2 = new double[this.m_stored[b4]];
          b3 = 0;
          arrayOfDouble[b4] = 0.0D;
          while (b3 < this.m_stored[b4]) {
            arrayOfDouble2[b3] = this.m_karray[b4][b3][0];
            arrayOfDouble[b4] = arrayOfDouble[b4] + this.m_weightsByRank[b3];
            b3++;
          } 
          arrayOfInt1[b4] = Utils.sort(arrayOfDouble2);
        } 
      } 
    } 
    if (this.m_numClasses > 2)
      d3 = 1.0D - this.m_classProbs[i]; 
    byte b1 = 0;
    double d2 = 0.0D;
    while (b1 < this.m_stored[i]) {
      Instance instance1 = this.m_weightByDistance ? this.m_trainInstances.instance((int)this.m_karray[i][arrayOfInt[b1]][1]) : this.m_trainInstances.instance((int)this.m_karray[i][b1][1]);
      byte b3 = 0;
      byte b4 = 0;
      while (true) {
        if (b3 < instance.numValues() || b4 < instance1.numValues()) {
          int j;
          int k;
          int m;
          if (b3 >= instance.numValues()) {
            k = this.m_trainInstances.numAttributes();
          } else {
            k = instance.index(b3);
          } 
          if (b4 >= instance1.numValues()) {
            m = this.m_trainInstances.numAttributes();
          } else {
            m = instance1.index(b4);
          } 
          if (k == this.m_trainInstances.classIndex()) {
            b3++;
            continue;
          } 
          if (m == this.m_trainInstances.classIndex()) {
            b4++;
            continue;
          } 
          if (k == m) {
            j = k;
            d2 = difference(j, instance.valueSparse(b3), instance1.valueSparse(b4));
            b3++;
            b4++;
          } else if (k > m) {
            j = m;
            d2 = difference(j, 0.0D, instance1.valueSparse(b4));
            b4++;
          } else {
            j = k;
            d2 = difference(j, instance.valueSparse(b3), 0.0D);
            b3++;
          } 
          if (this.m_weightByDistance) {
            d2 *= this.m_weightsByRank[b1] / d4;
          } else if (this.m_stored[i] > 0) {
            d2 /= this.m_stored[i];
          } 
          this.m_weights[j] = this.m_weights[j] - d2;
          continue;
        } 
        b1++;
      } 
    } 
    d2 = 0.0D;
    for (byte b2 = 0; b2 < this.m_numClasses; b2++) {
      if (b2 != i) {
        b1 = 0;
        double d = 0.0D;
        while (b1 < this.m_stored[b2]) {
          Instance instance1 = this.m_weightByDistance ? this.m_trainInstances.instance((int)this.m_karray[b2][arrayOfInt1[b2][b1]][1]) : this.m_trainInstances.instance((int)this.m_karray[b2][b1][1]);
          byte b3 = 0;
          byte b4 = 0;
          while (true) {
            if (b3 < instance.numValues() || b4 < instance1.numValues()) {
              int j;
              int k;
              int m;
              if (b3 >= instance.numValues()) {
                k = this.m_trainInstances.numAttributes();
              } else {
                k = instance.index(b3);
              } 
              if (b4 >= instance1.numValues()) {
                m = this.m_trainInstances.numAttributes();
              } else {
                m = instance1.index(b4);
              } 
              if (k == this.m_trainInstances.classIndex()) {
                b3++;
                continue;
              } 
              if (m == this.m_trainInstances.classIndex()) {
                b4++;
                continue;
              } 
              if (k == m) {
                j = k;
                d2 = difference(j, instance.valueSparse(b3), instance1.valueSparse(b4));
                b3++;
                b4++;
              } else if (k > m) {
                j = m;
                d2 = difference(j, 0.0D, instance1.valueSparse(b4));
                b4++;
              } else {
                j = k;
                d2 = difference(j, instance.valueSparse(b3), 0.0D);
                b3++;
              } 
              if (this.m_weightByDistance) {
                d2 *= this.m_weightsByRank[b1] / arrayOfDouble[b2];
              } else if (this.m_stored[b2] > 0) {
                d2 /= this.m_stored[b2];
              } 
              if (this.m_numClasses > 2) {
                this.m_weights[j] = this.m_weights[j] + this.m_classProbs[b2] / d3 * d2;
                continue;
              } 
              this.m_weights[j] = this.m_weights[j] + d2;
              continue;
            } 
            b1++;
          } 
        } 
      } 
    } 
  }
  
  private void findKHitMiss(int paramInt) {
    double d = 0.0D;
    Instance instance = this.m_trainInstances.instance(paramInt);
    for (byte b = 0; b < this.m_numInstances; b++) {
      if (b != paramInt) {
        int i;
        Instance instance1 = this.m_trainInstances.instance(b);
        d = distance(instance1, instance);
        if (this.m_numericClass) {
          i = 0;
        } else {
          i = (int)this.m_trainInstances.instance(b).value(this.m_classIndex);
        } 
        if (this.m_stored[i] < this.m_Knn) {
          this.m_karray[i][this.m_stored[i]][0] = d;
          this.m_karray[i][this.m_stored[i]][1] = b;
          this.m_stored[i] = this.m_stored[i] + 1;
          byte b1 = 0;
          double d1 = -1.0D;
          while (b1 < this.m_stored[i]) {
            if (this.m_karray[i][b1][0] > d1) {
              d1 = this.m_karray[i][b1][0];
              this.m_index[i] = b1;
            } 
            b1++;
          } 
          this.m_worst[i] = d1;
        } else if (d < this.m_karray[i][this.m_index[i]][0]) {
          this.m_karray[i][this.m_index[i]][0] = d;
          this.m_karray[i][this.m_index[i]][1] = b;
          byte b1 = 0;
          double d1 = -1.0D;
          while (b1 < this.m_stored[i]) {
            if (this.m_karray[i][b1][0] > d1) {
              d1 = this.m_karray[i][b1][0];
              this.m_index[i] = b1;
            } 
            b1++;
          } 
          this.m_worst[i] = d1;
        } 
      } 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(AttributeSelection.SelectAttributes(new ReliefFAttributeEval(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\ReliefFAttributeEval.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */