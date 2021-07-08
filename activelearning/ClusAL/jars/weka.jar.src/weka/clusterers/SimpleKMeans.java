package weka.clusterers;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class SimpleKMeans extends Clusterer implements NumberOfClustersRequestable, OptionHandler, WeightedInstancesHandler {
  private ReplaceMissingValues m_ReplaceMissingFilter;
  
  private int m_NumClusters = 2;
  
  private Instances m_ClusterCentroids;
  
  private Instances m_ClusterStdDevs;
  
  private int[][][] m_ClusterNominalCounts;
  
  private int[] m_ClusterSizes;
  
  private int m_Seed = 10;
  
  private double[] m_Min;
  
  private double[] m_Max;
  
  private int m_Iterations = 0;
  
  private double[] m_squaredErrors;
  
  public String globalInfo() {
    return "Cluster data using the k means algorithm";
  }
  
  public void buildClusterer(Instances paramInstances) throws Exception {
    // Byte code:
    //   0: aload_0
    //   1: iconst_0
    //   2: putfield m_Iterations : I
    //   5: aload_1
    //   6: invokevirtual checkForStringAttributes : ()Z
    //   9: ifeq -> 22
    //   12: new java/lang/Exception
    //   15: dup
    //   16: ldc 'Can't handle string attributes!'
    //   18: invokespecial <init> : (Ljava/lang/String;)V
    //   21: athrow
    //   22: aload_0
    //   23: new weka/filters/unsupervised/attribute/ReplaceMissingValues
    //   26: dup
    //   27: invokespecial <init> : ()V
    //   30: putfield m_ReplaceMissingFilter : Lweka/filters/unsupervised/attribute/ReplaceMissingValues;
    //   33: aload_0
    //   34: getfield m_ReplaceMissingFilter : Lweka/filters/unsupervised/attribute/ReplaceMissingValues;
    //   37: aload_1
    //   38: invokevirtual setInputFormat : (Lweka/core/Instances;)Z
    //   41: pop
    //   42: aload_1
    //   43: aload_0
    //   44: getfield m_ReplaceMissingFilter : Lweka/filters/unsupervised/attribute/ReplaceMissingValues;
    //   47: invokestatic useFilter : (Lweka/core/Instances;Lweka/filters/Filter;)Lweka/core/Instances;
    //   50: astore_2
    //   51: aload_0
    //   52: aload_2
    //   53: invokevirtual numAttributes : ()I
    //   56: newarray double
    //   58: putfield m_Min : [D
    //   61: aload_0
    //   62: aload_2
    //   63: invokevirtual numAttributes : ()I
    //   66: newarray double
    //   68: putfield m_Max : [D
    //   71: iconst_0
    //   72: istore_3
    //   73: iload_3
    //   74: aload_2
    //   75: invokevirtual numAttributes : ()I
    //   78: if_icmpge -> 103
    //   81: aload_0
    //   82: getfield m_Min : [D
    //   85: iload_3
    //   86: aload_0
    //   87: getfield m_Max : [D
    //   90: iload_3
    //   91: ldc2_w NaN
    //   94: dup2_x2
    //   95: dastore
    //   96: dastore
    //   97: iinc #3, 1
    //   100: goto -> 73
    //   103: aload_0
    //   104: new weka/core/Instances
    //   107: dup
    //   108: aload_2
    //   109: aload_0
    //   110: getfield m_NumClusters : I
    //   113: invokespecial <init> : (Lweka/core/Instances;I)V
    //   116: putfield m_ClusterCentroids : Lweka/core/Instances;
    //   119: aload_2
    //   120: invokevirtual numInstances : ()I
    //   123: newarray int
    //   125: astore_3
    //   126: iconst_0
    //   127: istore #4
    //   129: iload #4
    //   131: aload_2
    //   132: invokevirtual numInstances : ()I
    //   135: if_icmpge -> 154
    //   138: aload_0
    //   139: aload_2
    //   140: iload #4
    //   142: invokevirtual instance : (I)Lweka/core/Instance;
    //   145: invokespecial updateMinMax : (Lweka/core/Instance;)V
    //   148: iinc #4, 1
    //   151: goto -> 129
    //   154: new java/util/Random
    //   157: dup
    //   158: aload_0
    //   159: getfield m_Seed : I
    //   162: i2l
    //   163: invokespecial <init> : (J)V
    //   166: astore #4
    //   168: new java/util/HashMap
    //   171: dup
    //   172: invokespecial <init> : ()V
    //   175: astore #6
    //   177: aconst_null
    //   178: astore #7
    //   180: aload_2
    //   181: invokevirtual numInstances : ()I
    //   184: iconst_1
    //   185: isub
    //   186: istore #8
    //   188: iload #8
    //   190: iflt -> 286
    //   193: aload #4
    //   195: iload #8
    //   197: iconst_1
    //   198: iadd
    //   199: invokevirtual nextInt : (I)I
    //   202: istore #5
    //   204: new weka/classifiers/rules/DecisionTable$hashKey
    //   207: dup
    //   208: aload_2
    //   209: iload #5
    //   211: invokevirtual instance : (I)Lweka/core/Instance;
    //   214: aload_2
    //   215: invokevirtual numAttributes : ()I
    //   218: invokespecial <init> : (Lweka/core/Instance;I)V
    //   221: astore #7
    //   223: aload #6
    //   225: aload #7
    //   227: invokevirtual containsKey : (Ljava/lang/Object;)Z
    //   230: ifne -> 255
    //   233: aload_0
    //   234: getfield m_ClusterCentroids : Lweka/core/Instances;
    //   237: aload_2
    //   238: iload #5
    //   240: invokevirtual instance : (I)Lweka/core/Instance;
    //   243: invokevirtual add : (Lweka/core/Instance;)V
    //   246: aload #6
    //   248: aload #7
    //   250: aconst_null
    //   251: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   254: pop
    //   255: aload_2
    //   256: iload #8
    //   258: iload #5
    //   260: invokevirtual swap : (II)V
    //   263: aload_0
    //   264: getfield m_ClusterCentroids : Lweka/core/Instances;
    //   267: invokevirtual numInstances : ()I
    //   270: aload_0
    //   271: getfield m_NumClusters : I
    //   274: if_icmpne -> 280
    //   277: goto -> 286
    //   280: iinc #8, -1
    //   283: goto -> 188
    //   286: aload_0
    //   287: aload_0
    //   288: getfield m_ClusterCentroids : Lweka/core/Instances;
    //   291: invokevirtual numInstances : ()I
    //   294: putfield m_NumClusters : I
    //   297: iconst_0
    //   298: istore #9
    //   300: aload_0
    //   301: getfield m_NumClusters : I
    //   304: anewarray weka/core/Instances
    //   307: astore #11
    //   309: aload_0
    //   310: aload_0
    //   311: getfield m_NumClusters : I
    //   314: newarray double
    //   316: putfield m_squaredErrors : [D
    //   319: aload_0
    //   320: aload_0
    //   321: getfield m_NumClusters : I
    //   324: aload_2
    //   325: invokevirtual numAttributes : ()I
    //   328: iconst_0
    //   329: multianewarray[[[I 3
    //   333: putfield m_ClusterNominalCounts : [[[I
    //   336: iload #9
    //   338: ifne -> 668
    //   341: iconst_0
    //   342: istore #10
    //   344: aload_0
    //   345: dup
    //   346: getfield m_Iterations : I
    //   349: iconst_1
    //   350: iadd
    //   351: putfield m_Iterations : I
    //   354: iconst_1
    //   355: istore #9
    //   357: iconst_0
    //   358: istore #8
    //   360: iload #8
    //   362: aload_2
    //   363: invokevirtual numInstances : ()I
    //   366: if_icmpge -> 410
    //   369: aload_2
    //   370: iload #8
    //   372: invokevirtual instance : (I)Lweka/core/Instance;
    //   375: astore #12
    //   377: aload_0
    //   378: aload #12
    //   380: iconst_1
    //   381: invokespecial clusterProcessedInstance : (Lweka/core/Instance;Z)I
    //   384: istore #13
    //   386: iload #13
    //   388: aload_3
    //   389: iload #8
    //   391: iaload
    //   392: if_icmpeq -> 398
    //   395: iconst_0
    //   396: istore #9
    //   398: aload_3
    //   399: iload #8
    //   401: iload #13
    //   403: iastore
    //   404: iinc #8, 1
    //   407: goto -> 360
    //   410: aload_0
    //   411: new weka/core/Instances
    //   414: dup
    //   415: aload_2
    //   416: aload_0
    //   417: getfield m_NumClusters : I
    //   420: invokespecial <init> : (Lweka/core/Instances;I)V
    //   423: putfield m_ClusterCentroids : Lweka/core/Instances;
    //   426: iconst_0
    //   427: istore #8
    //   429: iload #8
    //   431: aload_0
    //   432: getfield m_NumClusters : I
    //   435: if_icmpge -> 458
    //   438: aload #11
    //   440: iload #8
    //   442: new weka/core/Instances
    //   445: dup
    //   446: aload_2
    //   447: iconst_0
    //   448: invokespecial <init> : (Lweka/core/Instances;I)V
    //   451: aastore
    //   452: iinc #8, 1
    //   455: goto -> 429
    //   458: iconst_0
    //   459: istore #8
    //   461: iload #8
    //   463: aload_2
    //   464: invokevirtual numInstances : ()I
    //   467: if_icmpge -> 492
    //   470: aload #11
    //   472: aload_3
    //   473: iload #8
    //   475: iaload
    //   476: aaload
    //   477: aload_2
    //   478: iload #8
    //   480: invokevirtual instance : (I)Lweka/core/Instance;
    //   483: invokevirtual add : (Lweka/core/Instance;)V
    //   486: iinc #8, 1
    //   489: goto -> 461
    //   492: iconst_0
    //   493: istore #8
    //   495: iload #8
    //   497: aload_0
    //   498: getfield m_NumClusters : I
    //   501: if_icmpge -> 608
    //   504: aload_2
    //   505: invokevirtual numAttributes : ()I
    //   508: newarray double
    //   510: astore #12
    //   512: aload #11
    //   514: iload #8
    //   516: aaload
    //   517: invokevirtual numInstances : ()I
    //   520: ifne -> 529
    //   523: iinc #10, 1
    //   526: goto -> 602
    //   529: iconst_0
    //   530: istore #13
    //   532: iload #13
    //   534: aload_2
    //   535: invokevirtual numAttributes : ()I
    //   538: if_icmpge -> 585
    //   541: aload #12
    //   543: iload #13
    //   545: aload #11
    //   547: iload #8
    //   549: aaload
    //   550: iload #13
    //   552: invokevirtual meanOrMode : (I)D
    //   555: dastore
    //   556: aload_0
    //   557: getfield m_ClusterNominalCounts : [[[I
    //   560: iload #8
    //   562: aaload
    //   563: iload #13
    //   565: aload #11
    //   567: iload #8
    //   569: aaload
    //   570: iload #13
    //   572: invokevirtual attributeStats : (I)Lweka/core/AttributeStats;
    //   575: getfield nominalCounts : [I
    //   578: aastore
    //   579: iinc #13, 1
    //   582: goto -> 532
    //   585: aload_0
    //   586: getfield m_ClusterCentroids : Lweka/core/Instances;
    //   589: new weka/core/Instance
    //   592: dup
    //   593: dconst_1
    //   594: aload #12
    //   596: invokespecial <init> : (D[D)V
    //   599: invokevirtual add : (Lweka/core/Instance;)V
    //   602: iinc #8, 1
    //   605: goto -> 495
    //   608: iload #10
    //   610: ifle -> 633
    //   613: aload_0
    //   614: dup
    //   615: getfield m_NumClusters : I
    //   618: iload #10
    //   620: isub
    //   621: putfield m_NumClusters : I
    //   624: aload_0
    //   625: getfield m_NumClusters : I
    //   628: anewarray weka/core/Instances
    //   631: astore #11
    //   633: iload #9
    //   635: ifne -> 336
    //   638: aload_0
    //   639: aload_0
    //   640: getfield m_NumClusters : I
    //   643: newarray double
    //   645: putfield m_squaredErrors : [D
    //   648: aload_0
    //   649: aload_0
    //   650: getfield m_NumClusters : I
    //   653: aload_2
    //   654: invokevirtual numAttributes : ()I
    //   657: iconst_0
    //   658: multianewarray[[[I 3
    //   662: putfield m_ClusterNominalCounts : [[[I
    //   665: goto -> 336
    //   668: aload_0
    //   669: new weka/core/Instances
    //   672: dup
    //   673: aload_2
    //   674: aload_0
    //   675: getfield m_NumClusters : I
    //   678: invokespecial <init> : (Lweka/core/Instances;I)V
    //   681: putfield m_ClusterStdDevs : Lweka/core/Instances;
    //   684: aload_0
    //   685: aload_0
    //   686: getfield m_NumClusters : I
    //   689: newarray int
    //   691: putfield m_ClusterSizes : [I
    //   694: iconst_0
    //   695: istore #8
    //   697: iload #8
    //   699: aload_0
    //   700: getfield m_NumClusters : I
    //   703: if_icmpge -> 811
    //   706: aload_2
    //   707: invokevirtual numAttributes : ()I
    //   710: newarray double
    //   712: astore #12
    //   714: iconst_0
    //   715: istore #13
    //   717: iload #13
    //   719: aload_2
    //   720: invokevirtual numAttributes : ()I
    //   723: if_icmpge -> 773
    //   726: aload_2
    //   727: iload #13
    //   729: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   732: invokevirtual isNumeric : ()Z
    //   735: ifeq -> 759
    //   738: aload #12
    //   740: iload #13
    //   742: aload #11
    //   744: iload #8
    //   746: aaload
    //   747: iload #13
    //   749: invokevirtual variance : (I)D
    //   752: invokestatic sqrt : (D)D
    //   755: dastore
    //   756: goto -> 767
    //   759: aload #12
    //   761: iload #13
    //   763: invokestatic missingValue : ()D
    //   766: dastore
    //   767: iinc #13, 1
    //   770: goto -> 717
    //   773: aload_0
    //   774: getfield m_ClusterStdDevs : Lweka/core/Instances;
    //   777: new weka/core/Instance
    //   780: dup
    //   781: dconst_1
    //   782: aload #12
    //   784: invokespecial <init> : (D[D)V
    //   787: invokevirtual add : (Lweka/core/Instance;)V
    //   790: aload_0
    //   791: getfield m_ClusterSizes : [I
    //   794: iload #8
    //   796: aload #11
    //   798: iload #8
    //   800: aaload
    //   801: invokevirtual numInstances : ()I
    //   804: iastore
    //   805: iinc #8, 1
    //   808: goto -> 697
    //   811: return
  }
  
  private int clusterProcessedInstance(Instance paramInstance, boolean paramBoolean) {
    double d = 2.147483647E9D;
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_NumClusters; b2++) {
      double d1 = distance(paramInstance, this.m_ClusterCentroids.instance(b2));
      if (d1 < d) {
        d = d1;
        b1 = b2;
      } 
    } 
    if (paramBoolean)
      this.m_squaredErrors[b1] = this.m_squaredErrors[b1] + d; 
    return b1;
  }
  
  public int clusterInstance(Instance paramInstance) throws Exception {
    this.m_ReplaceMissingFilter.input(paramInstance);
    this.m_ReplaceMissingFilter.batchFinished();
    Instance instance = this.m_ReplaceMissingFilter.output();
    return clusterProcessedInstance(instance, false);
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
          i = this.m_ClusterCentroids.numAttributes();
        } else {
          i = paramInstance1.index(b1);
        } 
        if (b2 >= paramInstance2.numValues()) {
          j = this.m_ClusterCentroids.numAttributes();
        } else {
          j = paramInstance2.index(b2);
        } 
        if (i == this.m_ClusterCentroids.classIndex()) {
          b1++;
          continue;
        } 
        if (j == this.m_ClusterCentroids.classIndex()) {
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
        d += d1 * d1;
        continue;
      } 
      return d;
    } 
  }
  
  private double difference(int paramInt, double paramDouble1, double paramDouble2) {
    switch (this.m_ClusterCentroids.attribute(paramInt).type()) {
      case 1:
        return (Instance.isMissingValue(paramDouble1) || Instance.isMissingValue(paramDouble2) || (int)paramDouble1 != (int)paramDouble2) ? 1.0D : 0.0D;
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
        return norm(paramDouble1, paramInt) - norm(paramDouble2, paramInt);
    } 
    return 0.0D;
  }
  
  private double norm(double paramDouble, int paramInt) {
    return (Double.isNaN(this.m_Min[paramInt]) || Utils.eq(this.m_Max[paramInt], this.m_Min[paramInt])) ? 0.0D : ((paramDouble - this.m_Min[paramInt]) / (this.m_Max[paramInt] - this.m_Min[paramInt]));
  }
  
  private void updateMinMax(Instance paramInstance) {
    for (byte b = 0; b < this.m_ClusterCentroids.numAttributes(); b++) {
      if (!paramInstance.isMissing(b))
        if (Double.isNaN(this.m_Min[b])) {
          this.m_Min[b] = paramInstance.value(b);
          this.m_Max[b] = paramInstance.value(b);
        } else if (paramInstance.value(b) < this.m_Min[b]) {
          this.m_Min[b] = paramInstance.value(b);
        } else if (paramInstance.value(b) > this.m_Max[b]) {
          this.m_Max[b] = paramInstance.value(b);
        }  
    } 
  }
  
  public int numberOfClusters() throws Exception {
    return this.m_NumClusters;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tnumber of clusters. (default = 2).", "N", 1, "-N <num>"));
    vector.addElement(new Option("\trandom number seed.\n (default 10)", "S", 1, "-S <num>"));
    return vector.elements();
  }
  
  public String numClustersTipText() {
    return "set number of clusters";
  }
  
  public void setNumClusters(int paramInt) throws Exception {
    if (paramInt <= 0)
      throw new Exception("Number of clusters must be > 0"); 
    this.m_NumClusters = paramInt;
  }
  
  public int getNumClusters() {
    return this.m_NumClusters;
  }
  
  public String seedTipText() {
    return "random number seed";
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('N', paramArrayOfString);
    if (str.length() != 0)
      setNumClusters(Integer.parseInt(str)); 
    str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0)
      setSeed(Integer.parseInt(str)); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[4];
    byte b = 0;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + getNumClusters();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSeed();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    int i = 0;
    for (byte b1 = 0; b1 < this.m_NumClusters; b1++) {
      for (byte b = 0; b < this.m_ClusterCentroids.numAttributes(); b++) {
        if (this.m_ClusterCentroids.attribute(b).isNumeric()) {
          double d = Math.log(Math.abs(this.m_ClusterCentroids.instance(b1).value(b))) / Math.log(10.0D);
          d++;
          if ((int)d > i)
            i = (int)d; 
        } 
      } 
    } 
    StringBuffer stringBuffer = new StringBuffer();
    String str = "N/A";
    byte b2;
    for (b2 = 0; b2 < i + 2; b2++)
      str = str + " "; 
    stringBuffer.append("\nkMeans\n======\n");
    stringBuffer.append("\nNumber of iterations: " + this.m_Iterations + "\n");
    stringBuffer.append("Within cluster sum of squared errors: " + Utils.sum(this.m_squaredErrors));
    stringBuffer.append("\n\nCluster centroids:\n");
    for (b2 = 0; b2 < this.m_NumClusters; b2++) {
      stringBuffer.append("\nCluster " + b2 + "\n\t");
      stringBuffer.append("Mean/Mode: ");
      byte b;
      for (b = 0; b < this.m_ClusterCentroids.numAttributes(); b++) {
        if (this.m_ClusterCentroids.attribute(b).isNominal()) {
          stringBuffer.append(" " + this.m_ClusterCentroids.attribute(b).value((int)this.m_ClusterCentroids.instance(b2).value(b)));
        } else {
          stringBuffer.append(" " + Utils.doubleToString(this.m_ClusterCentroids.instance(b2).value(b), i + 5, 4));
        } 
      } 
      stringBuffer.append("\n\tStd Devs:  ");
      for (b = 0; b < this.m_ClusterStdDevs.numAttributes(); b++) {
        if (this.m_ClusterStdDevs.attribute(b).isNumeric()) {
          stringBuffer.append(" " + Utils.doubleToString(this.m_ClusterStdDevs.instance(b2).value(b), i + 5, 4));
        } else {
          stringBuffer.append(" " + str);
        } 
      } 
    } 
    stringBuffer.append("\n\n");
    return stringBuffer.toString();
  }
  
  public Instances getClusterCentroids() {
    return this.m_ClusterCentroids;
  }
  
  public Instances getClusterStandardDevs() {
    return this.m_ClusterStdDevs;
  }
  
  public int[][][] getClusterNominalCounts() {
    return this.m_ClusterNominalCounts;
  }
  
  public double getSquaredError() {
    return Utils.sum(this.m_squaredErrors);
  }
  
  public int[] getClusterSizes() {
    return this.m_ClusterSizes;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(ClusterEvaluation.evaluateClusterer(new SimpleKMeans(), paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\clusterers\SimpleKMeans.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */