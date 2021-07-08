package weka.attributeSelection;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Vector;
import weka.core.ContingencyTables;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public class CfsSubsetEval extends SubsetEvaluator implements OptionHandler {
  private Instances m_trainInstances;
  
  private Discretize m_disTransform;
  
  private int m_classIndex;
  
  private boolean m_isNumeric;
  
  private int m_numAttribs;
  
  private int m_numInstances;
  
  private boolean m_missingSeperate;
  
  private boolean m_locallyPredictive;
  
  private float[][] m_corr_matrix;
  
  private double[] m_std_devs;
  
  private double m_c_Threshold;
  
  public String globalInfo() {
    return "CfsSubsetEval :\n\nEvaluates the worth of a subset of attributes by considering the individual predictive ability of each feature along with the degree of redundancy between them.\n\nSubsets of features that are highly correlated with the class while having low intercorrelation are preferred.\n";
  }
  
  public CfsSubsetEval() {
    resetOptions();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tTreat missing values as a seperate\n\tvalue.", "M", 0, "-M"));
    vector.addElement(new Option("\tInclude locally predictive attributes.", "L", 0, "-L"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    setMissingSeperate(Utils.getFlag('M', paramArrayOfString));
    setLocallyPredictive(Utils.getFlag('L', paramArrayOfString));
  }
  
  public String locallyPredictiveTipText() {
    return "Identify locally predictive attributes. Iteratively adds attributes with the highest correlation with the class as long as there is not already an attribute in the subset that has a higher correlation with the attribute in question";
  }
  
  public void setLocallyPredictive(boolean paramBoolean) {
    this.m_locallyPredictive = paramBoolean;
  }
  
  public boolean getLocallyPredictive() {
    return this.m_locallyPredictive;
  }
  
  public String missingSeperateTipText() {
    return "Treat missing as a separate value. Otherwise, counts for missing values are distributed across other values in proportion to their frequency.";
  }
  
  public void setMissingSeperate(boolean paramBoolean) {
    this.m_missingSeperate = paramBoolean;
  }
  
  public boolean getMissingSeperate() {
    return this.m_missingSeperate;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[2];
    byte b = 0;
    if (getMissingSeperate())
      arrayOfString[b++] = "-M"; 
    if (getLocallyPredictive())
      arrayOfString[b++] = "-L"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void buildEvaluator(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    this.m_trainInstances = paramInstances;
    this.m_trainInstances.deleteWithMissingClass();
    this.m_classIndex = this.m_trainInstances.classIndex();
    this.m_numAttribs = this.m_trainInstances.numAttributes();
    this.m_numInstances = this.m_trainInstances.numInstances();
    this.m_isNumeric = this.m_trainInstances.attribute(this.m_classIndex).isNumeric();
    if (!this.m_isNumeric) {
      this.m_disTransform = new Discretize();
      this.m_disTransform.setUseBetterEncoding(true);
      this.m_disTransform.setInputFormat(this.m_trainInstances);
      this.m_trainInstances = Filter.useFilter(this.m_trainInstances, (Filter)this.m_disTransform);
    } 
    this.m_std_devs = new double[this.m_numAttribs];
    this.m_corr_matrix = new float[this.m_numAttribs][];
    byte b;
    for (b = 0; b < this.m_numAttribs; b++)
      this.m_corr_matrix[b] = new float[b + 1]; 
    for (b = 0; b < this.m_corr_matrix.length; b++) {
      this.m_corr_matrix[b][b] = 1.0F;
      this.m_std_devs[b] = 1.0D;
    } 
    for (b = 0; b < this.m_numAttribs; b++) {
      for (byte b1 = 0; b1 < (this.m_corr_matrix[b]).length - 1; b1++)
        this.m_corr_matrix[b][b1] = -999.0F; 
    } 
  }
  
  public double evaluateSubset(BitSet paramBitSet) throws Exception {
    double d1 = 0.0D;
    double d2 = 0.0D;
    byte b;
    for (b = 0; b < this.m_numAttribs; b++) {
      if (b != this.m_classIndex && paramBitSet.get(b)) {
        int i;
        byte b1;
        if (b > this.m_classIndex) {
          i = b;
          b1 = this.m_classIndex;
        } else {
          b1 = b;
          i = this.m_classIndex;
        } 
        if (this.m_corr_matrix[i][b1] == -999.0F) {
          float f = correlate(b, this.m_classIndex);
          this.m_corr_matrix[i][b1] = f;
          d1 += this.m_std_devs[b] * f;
        } else {
          d1 += this.m_std_devs[b] * this.m_corr_matrix[i][b1];
        } 
      } 
    } 
    for (b = 0; b < this.m_numAttribs; b++) {
      if (b != this.m_classIndex && paramBitSet.get(b)) {
        d2 += 1.0D * this.m_std_devs[b] * this.m_std_devs[b];
        for (byte b1 = 0; b1 < (this.m_corr_matrix[b]).length - 1; b1++) {
          if (paramBitSet.get(b1))
            if (this.m_corr_matrix[b][b1] == -999.0F) {
              float f = correlate(b, b1);
              this.m_corr_matrix[b][b1] = f;
              d2 += 2.0D * this.m_std_devs[b] * this.m_std_devs[b1] * f;
            } else {
              d2 += 2.0D * this.m_std_devs[b] * this.m_std_devs[b1] * this.m_corr_matrix[b][b1];
            }  
        } 
      } 
    } 
    if (d2 < 0.0D)
      d2 *= -1.0D; 
    if (d2 == 0.0D)
      return 0.0D; 
    double d3 = d1 / Math.sqrt(d2);
    if (d3 < 0.0D)
      d3 *= -1.0D; 
    return d3;
  }
  
  private float correlate(int paramInt1, int paramInt2) {
    if (!this.m_isNumeric)
      return (float)symmUncertCorr(paramInt1, paramInt2); 
    boolean bool1 = this.m_trainInstances.attribute(paramInt1).isNumeric();
    boolean bool2 = this.m_trainInstances.attribute(paramInt2).isNumeric();
    return (bool1 && bool2) ? (float)num_num(paramInt1, paramInt2) : (bool2 ? (float)num_nom2(paramInt1, paramInt2) : (bool1 ? (float)num_nom2(paramInt2, paramInt1) : (float)nom_nom(paramInt1, paramInt2)));
  }
  
  private double symmUncertCorr(int paramInt1, int paramInt2) {
    double d1 = 0.0D;
    boolean bool = false;
    double d3 = 0.0D;
    if (paramInt1 == this.m_classIndex || paramInt2 == this.m_classIndex)
      bool = true; 
    int i = this.m_trainInstances.attribute(paramInt1).numValues() + 1;
    int j = this.m_trainInstances.attribute(paramInt2).numValues() + 1;
    double[][] arrayOfDouble = new double[i][j];
    double[] arrayOfDouble1 = new double[i];
    double[] arrayOfDouble2 = new double[j];
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      arrayOfDouble1[b1] = 0.0D;
      for (byte b = 0; b < j; b++) {
        arrayOfDouble2[b] = 0.0D;
        arrayOfDouble[b1][b] = 0.0D;
      } 
    } 
    for (b1 = 0; b1 < this.m_numInstances; b1++) {
      int k;
      int m;
      Instance instance = this.m_trainInstances.instance(b1);
      if (instance.isMissing(paramInt1)) {
        k = i - 1;
      } else {
        k = (int)instance.value(paramInt1);
      } 
      if (instance.isMissing(paramInt2)) {
        m = j - 1;
      } else {
        m = (int)instance.value(paramInt2);
      } 
      arrayOfDouble[k][m] = arrayOfDouble[k][m] + 1.0D;
    } 
    for (b1 = 0; b1 < i; b1++) {
      arrayOfDouble1[b1] = 0.0D;
      for (byte b = 0; b < j; b++) {
        arrayOfDouble1[b1] = arrayOfDouble1[b1] + arrayOfDouble[b1][b];
        d1 += arrayOfDouble[b1][b];
      } 
    } 
    byte b2;
    for (b2 = 0; b2 < j; b2++) {
      arrayOfDouble2[b2] = 0.0D;
      for (b1 = 0; b1 < i; b1++)
        arrayOfDouble2[b2] = arrayOfDouble2[b2] + arrayOfDouble[b1][b2]; 
    } 
    if (!this.m_missingSeperate && arrayOfDouble1[i - 1] < this.m_numInstances && arrayOfDouble2[j - 1] < this.m_numInstances) {
      double[] arrayOfDouble3 = new double[arrayOfDouble1.length];
      double[] arrayOfDouble4 = new double[arrayOfDouble2.length];
      double[][] arrayOfDouble5 = new double[arrayOfDouble1.length][arrayOfDouble2.length];
      for (b1 = 0; b1 < i; b1++)
        System.arraycopy(arrayOfDouble[b1], 0, arrayOfDouble5[b1], 0, arrayOfDouble2.length); 
      System.arraycopy(arrayOfDouble1, 0, arrayOfDouble3, 0, arrayOfDouble1.length);
      System.arraycopy(arrayOfDouble2, 0, arrayOfDouble4, 0, arrayOfDouble2.length);
      double d = arrayOfDouble1[i - 1] + arrayOfDouble2[j - 1] - arrayOfDouble[i - 1][j - 1];
      if (arrayOfDouble1[i - 1] > 0.0D)
        for (b2 = 0; b2 < j - 1; b2++) {
          if (arrayOfDouble[i - 1][b2] > 0.0D) {
            for (b1 = 0; b1 < i - 1; b1++) {
              d3 = arrayOfDouble3[b1] / (d1 - arrayOfDouble3[i - 1]) * arrayOfDouble[i - 1][b2];
              arrayOfDouble[b1][b2] = arrayOfDouble[b1][b2] + d3;
              arrayOfDouble1[b1] = arrayOfDouble1[b1] + d3;
            } 
            arrayOfDouble[i - 1][b2] = 0.0D;
          } 
        }  
      arrayOfDouble1[i - 1] = 0.0D;
      if (arrayOfDouble2[j - 1] > 0.0D)
        for (b1 = 0; b1 < i - 1; b1++) {
          if (arrayOfDouble[b1][j - 1] > 0.0D) {
            for (b2 = 0; b2 < j - 1; b2++) {
              d3 = arrayOfDouble4[b2] / (d1 - arrayOfDouble4[j - 1]) * arrayOfDouble[b1][j - 1];
              arrayOfDouble[b1][b2] = arrayOfDouble[b1][b2] + d3;
              arrayOfDouble2[b2] = arrayOfDouble2[b2] + d3;
            } 
            arrayOfDouble[b1][j - 1] = 0.0D;
          } 
        }  
      arrayOfDouble2[j - 1] = 0.0D;
      if (arrayOfDouble[i - 1][j - 1] > 0.0D && d != d1) {
        for (b1 = 0; b1 < i - 1; b1++) {
          for (b2 = 0; b2 < j - 1; b2++) {
            d3 = arrayOfDouble5[b1][b2] / (d1 - d) * arrayOfDouble5[i - 1][j - 1];
            arrayOfDouble[b1][b2] = arrayOfDouble[b1][b2] + d3;
            arrayOfDouble1[b1] = arrayOfDouble1[b1] + d3;
            arrayOfDouble2[b2] = arrayOfDouble2[b2] + d3;
          } 
        } 
        arrayOfDouble[i - 1][j - 1] = 0.0D;
      } 
    } 
    double d2 = ContingencyTables.symmetricalUncertainty(arrayOfDouble);
    return Utils.eq(d2, 0.0D) ? ((bool == true) ? 0.0D : 1.0D) : d2;
  }
  
  private double num_num(int paramInt1, int paramInt2) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = this.m_trainInstances.meanOrMode(this.m_trainInstances.attribute(paramInt1));
    double d5 = this.m_trainInstances.meanOrMode(this.m_trainInstances.attribute(paramInt2));
    for (byte b = 0; b < this.m_numInstances; b++) {
      Instance instance = this.m_trainInstances.instance(b);
      double d6 = instance.isMissing(paramInt1) ? 0.0D : (instance.value(paramInt1) - d4);
      double d7 = instance.isMissing(paramInt2) ? 0.0D : (instance.value(paramInt2) - d5);
      d1 += d6 * d7;
      d2 += d6 * d6;
      d3 += d7 * d7;
    } 
    if (d2 != 0.0D && this.m_std_devs[paramInt1] == 1.0D)
      this.m_std_devs[paramInt1] = Math.sqrt(d2 / this.m_numInstances); 
    if (d3 != 0.0D && this.m_std_devs[paramInt2] == 1.0D)
      this.m_std_devs[paramInt2] = Math.sqrt(d3 / this.m_numInstances); 
    if (d2 * d3 > 0.0D) {
      double d = d1 / Math.sqrt(d2 * d3);
      return (d < 0.0D) ? -d : d;
    } 
    return (paramInt1 != this.m_classIndex && paramInt2 != this.m_classIndex) ? 1.0D : 0.0D;
  }
  
  private double num_nom2(int paramInt1, int paramInt2) {
    // Byte code:
    //   0: aload_0
    //   1: getfield m_trainInstances : Lweka/core/Instances;
    //   4: aload_0
    //   5: getfield m_trainInstances : Lweka/core/Instances;
    //   8: iload_1
    //   9: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   12: invokevirtual meanOrMode : (Lweka/core/Attribute;)D
    //   15: d2i
    //   16: istore #9
    //   18: aload_0
    //   19: getfield m_trainInstances : Lweka/core/Instances;
    //   22: aload_0
    //   23: getfield m_trainInstances : Lweka/core/Instances;
    //   26: iload_2
    //   27: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   30: invokevirtual meanOrMode : (Lweka/core/Attribute;)D
    //   33: dstore #10
    //   35: dconst_0
    //   36: dstore #12
    //   38: dconst_0
    //   39: dstore #18
    //   41: dconst_0
    //   42: dstore #22
    //   44: aload_0
    //   45: getfield m_missingSeperate : Z
    //   48: ifne -> 65
    //   51: aload_0
    //   52: getfield m_trainInstances : Lweka/core/Instances;
    //   55: iload_1
    //   56: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   59: invokevirtual numValues : ()I
    //   62: goto -> 78
    //   65: aload_0
    //   66: getfield m_trainInstances : Lweka/core/Instances;
    //   69: iload_1
    //   70: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   73: invokevirtual numValues : ()I
    //   76: iconst_1
    //   77: iadd
    //   78: istore #24
    //   80: iload #24
    //   82: newarray double
    //   84: astore #25
    //   86: iload #24
    //   88: newarray double
    //   90: astore #26
    //   92: iload #24
    //   94: newarray double
    //   96: astore #27
    //   98: iconst_0
    //   99: istore_3
    //   100: iload_3
    //   101: iload #24
    //   103: if_icmpge -> 127
    //   106: aload #26
    //   108: iload_3
    //   109: aload #27
    //   111: iload_3
    //   112: aload #25
    //   114: iload_3
    //   115: dconst_0
    //   116: dup2_x2
    //   117: dastore
    //   118: dup2_x2
    //   119: dastore
    //   120: dastore
    //   121: iinc #3, 1
    //   124: goto -> 100
    //   127: iconst_0
    //   128: istore_3
    //   129: iload_3
    //   130: aload_0
    //   131: getfield m_numInstances : I
    //   134: if_icmpge -> 203
    //   137: aload_0
    //   138: getfield m_trainInstances : Lweka/core/Instances;
    //   141: iload_3
    //   142: invokevirtual instance : (I)Lweka/core/Instance;
    //   145: astore #8
    //   147: aload #8
    //   149: iload_1
    //   150: invokevirtual isMissing : (I)Z
    //   153: ifeq -> 179
    //   156: aload_0
    //   157: getfield m_missingSeperate : Z
    //   160: ifne -> 170
    //   163: iload #9
    //   165: istore #4
    //   167: goto -> 188
    //   170: iload #24
    //   172: iconst_1
    //   173: isub
    //   174: istore #4
    //   176: goto -> 188
    //   179: aload #8
    //   181: iload_1
    //   182: invokevirtual value : (I)D
    //   185: d2i
    //   186: istore #4
    //   188: aload #25
    //   190: iload #4
    //   192: dup2
    //   193: daload
    //   194: dconst_1
    //   195: dadd
    //   196: dastore
    //   197: iinc #3, 1
    //   200: goto -> 129
    //   203: iconst_0
    //   204: istore #5
    //   206: iload #5
    //   208: aload_0
    //   209: getfield m_numInstances : I
    //   212: if_icmpge -> 388
    //   215: aload_0
    //   216: getfield m_trainInstances : Lweka/core/Instances;
    //   219: iload #5
    //   221: invokevirtual instance : (I)Lweka/core/Instance;
    //   224: astore #8
    //   226: aload #8
    //   228: iload_2
    //   229: invokevirtual isMissing : (I)Z
    //   232: ifeq -> 239
    //   235: dconst_0
    //   236: goto -> 248
    //   239: aload #8
    //   241: iload_2
    //   242: invokevirtual value : (I)D
    //   245: dload #10
    //   247: dsub
    //   248: dstore #16
    //   250: dload #12
    //   252: dload #16
    //   254: dload #16
    //   256: dmul
    //   257: dadd
    //   258: dstore #12
    //   260: iconst_0
    //   261: istore_3
    //   262: iload_3
    //   263: iload #24
    //   265: if_icmpge -> 382
    //   268: aload #8
    //   270: iload_1
    //   271: invokevirtual isMissing : (I)Z
    //   274: ifeq -> 318
    //   277: aload_0
    //   278: getfield m_missingSeperate : Z
    //   281: ifne -> 300
    //   284: iload_3
    //   285: iload #9
    //   287: if_icmpne -> 294
    //   290: dconst_1
    //   291: goto -> 295
    //   294: dconst_0
    //   295: dstore #6
    //   297: goto -> 337
    //   300: iload_3
    //   301: iload #24
    //   303: iconst_1
    //   304: isub
    //   305: if_icmpne -> 312
    //   308: dconst_1
    //   309: goto -> 313
    //   312: dconst_0
    //   313: dstore #6
    //   315: goto -> 337
    //   318: iload_3
    //   319: i2d
    //   320: aload #8
    //   322: iload_1
    //   323: invokevirtual value : (I)D
    //   326: dcmpl
    //   327: ifne -> 334
    //   330: dconst_1
    //   331: goto -> 335
    //   334: dconst_0
    //   335: dstore #6
    //   337: dload #6
    //   339: aload #25
    //   341: iload_3
    //   342: daload
    //   343: aload_0
    //   344: getfield m_numInstances : I
    //   347: i2d
    //   348: ddiv
    //   349: dsub
    //   350: dstore #14
    //   352: aload #26
    //   354: iload_3
    //   355: dup2
    //   356: daload
    //   357: dload #14
    //   359: dload #14
    //   361: dmul
    //   362: dadd
    //   363: dastore
    //   364: aload #27
    //   366: iload_3
    //   367: dup2
    //   368: daload
    //   369: dload #14
    //   371: dload #16
    //   373: dmul
    //   374: dadd
    //   375: dastore
    //   376: iinc #3, 1
    //   379: goto -> 262
    //   382: iinc #5, 1
    //   385: goto -> 206
    //   388: iconst_0
    //   389: istore_3
    //   390: dconst_0
    //   391: dstore #6
    //   393: iload_3
    //   394: iload #24
    //   396: if_icmpge -> 526
    //   399: dload #6
    //   401: aload #25
    //   403: iload_3
    //   404: daload
    //   405: aload_0
    //   406: getfield m_numInstances : I
    //   409: i2d
    //   410: ddiv
    //   411: aload #26
    //   413: iload_3
    //   414: daload
    //   415: aload_0
    //   416: getfield m_numInstances : I
    //   419: i2d
    //   420: ddiv
    //   421: dmul
    //   422: dadd
    //   423: dstore #6
    //   425: aload #26
    //   427: iload_3
    //   428: daload
    //   429: dload #12
    //   431: dmul
    //   432: dconst_0
    //   433: dcmpl
    //   434: ifle -> 487
    //   437: aload #27
    //   439: iload_3
    //   440: daload
    //   441: aload #26
    //   443: iload_3
    //   444: daload
    //   445: dload #12
    //   447: dmul
    //   448: invokestatic sqrt : (D)D
    //   451: ddiv
    //   452: dstore #20
    //   454: dload #20
    //   456: dconst_0
    //   457: dcmpg
    //   458: ifge -> 466
    //   461: dload #20
    //   463: dneg
    //   464: dstore #20
    //   466: dload #18
    //   468: aload #25
    //   470: iload_3
    //   471: daload
    //   472: aload_0
    //   473: getfield m_numInstances : I
    //   476: i2d
    //   477: ddiv
    //   478: dload #20
    //   480: dmul
    //   481: dadd
    //   482: dstore #18
    //   484: goto -> 520
    //   487: iload_1
    //   488: aload_0
    //   489: getfield m_classIndex : I
    //   492: if_icmpeq -> 520
    //   495: iload_2
    //   496: aload_0
    //   497: getfield m_classIndex : I
    //   500: if_icmpeq -> 520
    //   503: dload #18
    //   505: aload #25
    //   507: iload_3
    //   508: daload
    //   509: aload_0
    //   510: getfield m_numInstances : I
    //   513: i2d
    //   514: ddiv
    //   515: dconst_1
    //   516: dmul
    //   517: dadd
    //   518: dstore #18
    //   520: iinc #3, 1
    //   523: goto -> 393
    //   526: dload #6
    //   528: dconst_0
    //   529: dcmpl
    //   530: ifeq -> 555
    //   533: aload_0
    //   534: getfield m_std_devs : [D
    //   537: iload_1
    //   538: daload
    //   539: dconst_1
    //   540: dcmpl
    //   541: ifne -> 555
    //   544: aload_0
    //   545: getfield m_std_devs : [D
    //   548: iload_1
    //   549: dload #6
    //   551: invokestatic sqrt : (D)D
    //   554: dastore
    //   555: dload #12
    //   557: dconst_0
    //   558: dcmpl
    //   559: ifeq -> 590
    //   562: aload_0
    //   563: getfield m_std_devs : [D
    //   566: iload_2
    //   567: daload
    //   568: dconst_1
    //   569: dcmpl
    //   570: ifne -> 590
    //   573: aload_0
    //   574: getfield m_std_devs : [D
    //   577: iload_2
    //   578: dload #12
    //   580: aload_0
    //   581: getfield m_numInstances : I
    //   584: i2d
    //   585: ddiv
    //   586: invokestatic sqrt : (D)D
    //   589: dastore
    //   590: dload #18
    //   592: dconst_0
    //   593: dcmpl
    //   594: ifne -> 616
    //   597: iload_1
    //   598: aload_0
    //   599: getfield m_classIndex : I
    //   602: if_icmpeq -> 616
    //   605: iload_2
    //   606: aload_0
    //   607: getfield m_classIndex : I
    //   610: if_icmpeq -> 616
    //   613: dconst_1
    //   614: dstore #18
    //   616: dload #18
    //   618: dreturn
  }
  
  private double nom_nom(int paramInt1, int paramInt2) {
    // Byte code:
    //   0: aload_0
    //   1: getfield m_trainInstances : Lweka/core/Instances;
    //   4: aload_0
    //   5: getfield m_trainInstances : Lweka/core/Instances;
    //   8: iload_1
    //   9: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   12: invokevirtual meanOrMode : (Lweka/core/Attribute;)D
    //   15: d2i
    //   16: istore #13
    //   18: aload_0
    //   19: getfield m_trainInstances : Lweka/core/Instances;
    //   22: aload_0
    //   23: getfield m_trainInstances : Lweka/core/Instances;
    //   26: iload_2
    //   27: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   30: invokevirtual meanOrMode : (Lweka/core/Attribute;)D
    //   33: d2i
    //   34: istore #14
    //   36: dconst_0
    //   37: dstore #19
    //   39: dconst_0
    //   40: dstore #23
    //   42: aload_0
    //   43: getfield m_missingSeperate : Z
    //   46: ifne -> 63
    //   49: aload_0
    //   50: getfield m_trainInstances : Lweka/core/Instances;
    //   53: iload_1
    //   54: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   57: invokevirtual numValues : ()I
    //   60: goto -> 76
    //   63: aload_0
    //   64: getfield m_trainInstances : Lweka/core/Instances;
    //   67: iload_1
    //   68: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   71: invokevirtual numValues : ()I
    //   74: iconst_1
    //   75: iadd
    //   76: istore #25
    //   78: aload_0
    //   79: getfield m_missingSeperate : Z
    //   82: ifne -> 99
    //   85: aload_0
    //   86: getfield m_trainInstances : Lweka/core/Instances;
    //   89: iload_2
    //   90: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   93: invokevirtual numValues : ()I
    //   96: goto -> 112
    //   99: aload_0
    //   100: getfield m_trainInstances : Lweka/core/Instances;
    //   103: iload_2
    //   104: invokevirtual attribute : (I)Lweka/core/Attribute;
    //   107: invokevirtual numValues : ()I
    //   110: iconst_1
    //   111: iadd
    //   112: istore #26
    //   114: iload #25
    //   116: iload #26
    //   118: multianewarray[[D 2
    //   122: astore #27
    //   124: iload #25
    //   126: newarray double
    //   128: astore #28
    //   130: iload #26
    //   132: newarray double
    //   134: astore #29
    //   136: iload #25
    //   138: newarray double
    //   140: astore #30
    //   142: iload #26
    //   144: newarray double
    //   146: astore #31
    //   148: iload #25
    //   150: iload #26
    //   152: multianewarray[[D 2
    //   156: astore #32
    //   158: iconst_0
    //   159: istore_3
    //   160: iload_3
    //   161: iload #25
    //   163: if_icmpge -> 182
    //   166: aload #28
    //   168: iload_3
    //   169: aload #30
    //   171: iload_3
    //   172: dconst_0
    //   173: dup2_x2
    //   174: dastore
    //   175: dastore
    //   176: iinc #3, 1
    //   179: goto -> 160
    //   182: iconst_0
    //   183: istore #4
    //   185: iload #4
    //   187: iload #26
    //   189: if_icmpge -> 210
    //   192: aload #29
    //   194: iload #4
    //   196: aload #31
    //   198: iload #4
    //   200: dconst_0
    //   201: dup2_x2
    //   202: dastore
    //   203: dastore
    //   204: iinc #4, 1
    //   207: goto -> 185
    //   210: iconst_0
    //   211: istore_3
    //   212: iload_3
    //   213: iload #25
    //   215: if_icmpge -> 256
    //   218: iconst_0
    //   219: istore #4
    //   221: iload #4
    //   223: iload #26
    //   225: if_icmpge -> 250
    //   228: aload #32
    //   230: iload_3
    //   231: aaload
    //   232: iload #4
    //   234: aload #27
    //   236: iload_3
    //   237: aaload
    //   238: iload #4
    //   240: dconst_0
    //   241: dup2_x2
    //   242: dastore
    //   243: dastore
    //   244: iinc #4, 1
    //   247: goto -> 221
    //   250: iinc #3, 1
    //   253: goto -> 212
    //   256: iconst_0
    //   257: istore_3
    //   258: iload_3
    //   259: aload_0
    //   260: getfield m_numInstances : I
    //   263: if_icmpge -> 394
    //   266: aload_0
    //   267: getfield m_trainInstances : Lweka/core/Instances;
    //   270: iload_3
    //   271: invokevirtual instance : (I)Lweka/core/Instance;
    //   274: astore #12
    //   276: aload #12
    //   278: iload_1
    //   279: invokevirtual isMissing : (I)Z
    //   282: ifeq -> 308
    //   285: aload_0
    //   286: getfield m_missingSeperate : Z
    //   289: ifne -> 299
    //   292: iload #13
    //   294: istore #5
    //   296: goto -> 317
    //   299: iload #25
    //   301: iconst_1
    //   302: isub
    //   303: istore #5
    //   305: goto -> 317
    //   308: aload #12
    //   310: iload_1
    //   311: invokevirtual value : (I)D
    //   314: d2i
    //   315: istore #5
    //   317: aload #12
    //   319: iload_2
    //   320: invokevirtual isMissing : (I)Z
    //   323: ifeq -> 349
    //   326: aload_0
    //   327: getfield m_missingSeperate : Z
    //   330: ifne -> 340
    //   333: iload #14
    //   335: istore #6
    //   337: goto -> 358
    //   340: iload #26
    //   342: iconst_1
    //   343: isub
    //   344: istore #6
    //   346: goto -> 358
    //   349: aload #12
    //   351: iload_2
    //   352: invokevirtual value : (I)D
    //   355: d2i
    //   356: istore #6
    //   358: aload #27
    //   360: iload #5
    //   362: aaload
    //   363: iload #6
    //   365: dup2
    //   366: daload
    //   367: dconst_1
    //   368: dadd
    //   369: dastore
    //   370: aload #28
    //   372: iload #5
    //   374: dup2
    //   375: daload
    //   376: dconst_1
    //   377: dadd
    //   378: dastore
    //   379: aload #29
    //   381: iload #6
    //   383: dup2
    //   384: daload
    //   385: dconst_1
    //   386: dadd
    //   387: dastore
    //   388: iinc #3, 1
    //   391: goto -> 258
    //   394: iconst_0
    //   395: istore #7
    //   397: iload #7
    //   399: aload_0
    //   400: getfield m_numInstances : I
    //   403: if_icmpge -> 769
    //   406: aload_0
    //   407: getfield m_trainInstances : Lweka/core/Instances;
    //   410: iload #7
    //   412: invokevirtual instance : (I)Lweka/core/Instance;
    //   415: astore #12
    //   417: iconst_0
    //   418: istore #4
    //   420: iload #4
    //   422: iload #26
    //   424: if_icmpge -> 534
    //   427: aload #12
    //   429: iload_2
    //   430: invokevirtual isMissing : (I)Z
    //   433: ifeq -> 479
    //   436: aload_0
    //   437: getfield m_missingSeperate : Z
    //   440: ifne -> 460
    //   443: iload #4
    //   445: iload #14
    //   447: if_icmpne -> 454
    //   450: dconst_1
    //   451: goto -> 455
    //   454: dconst_0
    //   455: dstore #10
    //   457: goto -> 499
    //   460: iload #4
    //   462: iload #26
    //   464: iconst_1
    //   465: isub
    //   466: if_icmpne -> 473
    //   469: dconst_1
    //   470: goto -> 474
    //   473: dconst_0
    //   474: dstore #10
    //   476: goto -> 499
    //   479: iload #4
    //   481: i2d
    //   482: aload #12
    //   484: iload_2
    //   485: invokevirtual value : (I)D
    //   488: dcmpl
    //   489: ifne -> 496
    //   492: dconst_1
    //   493: goto -> 497
    //   496: dconst_0
    //   497: dstore #10
    //   499: dload #10
    //   501: aload #29
    //   503: iload #4
    //   505: daload
    //   506: aload_0
    //   507: getfield m_numInstances : I
    //   510: i2d
    //   511: ddiv
    //   512: dsub
    //   513: dstore #17
    //   515: aload #31
    //   517: iload #4
    //   519: dup2
    //   520: daload
    //   521: dload #17
    //   523: dload #17
    //   525: dmul
    //   526: dadd
    //   527: dastore
    //   528: iinc #4, 1
    //   531: goto -> 420
    //   534: iconst_0
    //   535: istore_3
    //   536: iload_3
    //   537: iload #25
    //   539: if_icmpge -> 763
    //   542: aload #12
    //   544: iload_1
    //   545: invokevirtual isMissing : (I)Z
    //   548: ifeq -> 592
    //   551: aload_0
    //   552: getfield m_missingSeperate : Z
    //   555: ifne -> 574
    //   558: iload_3
    //   559: iload #13
    //   561: if_icmpne -> 568
    //   564: dconst_1
    //   565: goto -> 569
    //   568: dconst_0
    //   569: dstore #8
    //   571: goto -> 611
    //   574: iload_3
    //   575: iload #25
    //   577: iconst_1
    //   578: isub
    //   579: if_icmpne -> 586
    //   582: dconst_1
    //   583: goto -> 587
    //   586: dconst_0
    //   587: dstore #8
    //   589: goto -> 611
    //   592: iload_3
    //   593: i2d
    //   594: aload #12
    //   596: iload_1
    //   597: invokevirtual value : (I)D
    //   600: dcmpl
    //   601: ifne -> 608
    //   604: dconst_1
    //   605: goto -> 609
    //   608: dconst_0
    //   609: dstore #8
    //   611: dload #8
    //   613: aload #28
    //   615: iload_3
    //   616: daload
    //   617: aload_0
    //   618: getfield m_numInstances : I
    //   621: i2d
    //   622: ddiv
    //   623: dsub
    //   624: dstore #15
    //   626: aload #30
    //   628: iload_3
    //   629: dup2
    //   630: daload
    //   631: dload #15
    //   633: dload #15
    //   635: dmul
    //   636: dadd
    //   637: dastore
    //   638: iconst_0
    //   639: istore #4
    //   641: iload #4
    //   643: iload #26
    //   645: if_icmpge -> 757
    //   648: aload #12
    //   650: iload_2
    //   651: invokevirtual isMissing : (I)Z
    //   654: ifeq -> 700
    //   657: aload_0
    //   658: getfield m_missingSeperate : Z
    //   661: ifne -> 681
    //   664: iload #4
    //   666: iload #14
    //   668: if_icmpne -> 675
    //   671: dconst_1
    //   672: goto -> 676
    //   675: dconst_0
    //   676: dstore #10
    //   678: goto -> 720
    //   681: iload #4
    //   683: iload #26
    //   685: iconst_1
    //   686: isub
    //   687: if_icmpne -> 694
    //   690: dconst_1
    //   691: goto -> 695
    //   694: dconst_0
    //   695: dstore #10
    //   697: goto -> 720
    //   700: iload #4
    //   702: i2d
    //   703: aload #12
    //   705: iload_2
    //   706: invokevirtual value : (I)D
    //   709: dcmpl
    //   710: ifne -> 717
    //   713: dconst_1
    //   714: goto -> 718
    //   717: dconst_0
    //   718: dstore #10
    //   720: dload #10
    //   722: aload #29
    //   724: iload #4
    //   726: daload
    //   727: aload_0
    //   728: getfield m_numInstances : I
    //   731: i2d
    //   732: ddiv
    //   733: dsub
    //   734: dstore #17
    //   736: aload #32
    //   738: iload_3
    //   739: aaload
    //   740: iload #4
    //   742: dup2
    //   743: daload
    //   744: dload #15
    //   746: dload #17
    //   748: dmul
    //   749: dadd
    //   750: dastore
    //   751: iinc #4, 1
    //   754: goto -> 641
    //   757: iinc #3, 1
    //   760: goto -> 536
    //   763: iinc #7, 1
    //   766: goto -> 397
    //   769: iconst_0
    //   770: istore_3
    //   771: iload_3
    //   772: iload #25
    //   774: if_icmpge -> 909
    //   777: iconst_0
    //   778: istore #4
    //   780: iload #4
    //   782: iload #26
    //   784: if_icmpge -> 903
    //   787: aload #30
    //   789: iload_3
    //   790: daload
    //   791: aload #31
    //   793: iload #4
    //   795: daload
    //   796: dmul
    //   797: dconst_0
    //   798: dcmpl
    //   799: ifle -> 861
    //   802: aload #32
    //   804: iload_3
    //   805: aaload
    //   806: iload #4
    //   808: daload
    //   809: aload #30
    //   811: iload_3
    //   812: daload
    //   813: aload #31
    //   815: iload #4
    //   817: daload
    //   818: dmul
    //   819: invokestatic sqrt : (D)D
    //   822: ddiv
    //   823: dstore #21
    //   825: dload #21
    //   827: dconst_0
    //   828: dcmpg
    //   829: ifge -> 837
    //   832: dload #21
    //   834: dneg
    //   835: dstore #21
    //   837: dload #19
    //   839: aload #27
    //   841: iload_3
    //   842: aaload
    //   843: iload #4
    //   845: daload
    //   846: aload_0
    //   847: getfield m_numInstances : I
    //   850: i2d
    //   851: ddiv
    //   852: dload #21
    //   854: dmul
    //   855: dadd
    //   856: dstore #19
    //   858: goto -> 897
    //   861: iload_1
    //   862: aload_0
    //   863: getfield m_classIndex : I
    //   866: if_icmpeq -> 897
    //   869: iload_2
    //   870: aload_0
    //   871: getfield m_classIndex : I
    //   874: if_icmpeq -> 897
    //   877: dload #19
    //   879: aload #27
    //   881: iload_3
    //   882: aaload
    //   883: iload #4
    //   885: daload
    //   886: aload_0
    //   887: getfield m_numInstances : I
    //   890: i2d
    //   891: ddiv
    //   892: dconst_1
    //   893: dmul
    //   894: dadd
    //   895: dstore #19
    //   897: iinc #4, 1
    //   900: goto -> 780
    //   903: iinc #3, 1
    //   906: goto -> 771
    //   909: iconst_0
    //   910: istore_3
    //   911: dconst_0
    //   912: dstore #8
    //   914: iload_3
    //   915: iload #25
    //   917: if_icmpge -> 952
    //   920: dload #8
    //   922: aload #28
    //   924: iload_3
    //   925: daload
    //   926: aload_0
    //   927: getfield m_numInstances : I
    //   930: i2d
    //   931: ddiv
    //   932: aload #30
    //   934: iload_3
    //   935: daload
    //   936: aload_0
    //   937: getfield m_numInstances : I
    //   940: i2d
    //   941: ddiv
    //   942: dmul
    //   943: dadd
    //   944: dstore #8
    //   946: iinc #3, 1
    //   949: goto -> 914
    //   952: dload #8
    //   954: dconst_0
    //   955: dcmpl
    //   956: ifeq -> 981
    //   959: aload_0
    //   960: getfield m_std_devs : [D
    //   963: iload_1
    //   964: daload
    //   965: dconst_1
    //   966: dcmpl
    //   967: ifne -> 981
    //   970: aload_0
    //   971: getfield m_std_devs : [D
    //   974: iload_1
    //   975: dload #8
    //   977: invokestatic sqrt : (D)D
    //   980: dastore
    //   981: iconst_0
    //   982: istore #4
    //   984: dconst_0
    //   985: dstore #10
    //   987: iload #4
    //   989: iload #26
    //   991: if_icmpge -> 1028
    //   994: dload #10
    //   996: aload #29
    //   998: iload #4
    //   1000: daload
    //   1001: aload_0
    //   1002: getfield m_numInstances : I
    //   1005: i2d
    //   1006: ddiv
    //   1007: aload #31
    //   1009: iload #4
    //   1011: daload
    //   1012: aload_0
    //   1013: getfield m_numInstances : I
    //   1016: i2d
    //   1017: ddiv
    //   1018: dmul
    //   1019: dadd
    //   1020: dstore #10
    //   1022: iinc #4, 1
    //   1025: goto -> 987
    //   1028: dload #10
    //   1030: dconst_0
    //   1031: dcmpl
    //   1032: ifeq -> 1057
    //   1035: aload_0
    //   1036: getfield m_std_devs : [D
    //   1039: iload_2
    //   1040: daload
    //   1041: dconst_1
    //   1042: dcmpl
    //   1043: ifne -> 1057
    //   1046: aload_0
    //   1047: getfield m_std_devs : [D
    //   1050: iload_2
    //   1051: dload #10
    //   1053: invokestatic sqrt : (D)D
    //   1056: dastore
    //   1057: dload #19
    //   1059: dconst_0
    //   1060: dcmpl
    //   1061: ifne -> 1083
    //   1064: iload_1
    //   1065: aload_0
    //   1066: getfield m_classIndex : I
    //   1069: if_icmpeq -> 1083
    //   1072: iload_2
    //   1073: aload_0
    //   1074: getfield m_classIndex : I
    //   1077: if_icmpeq -> 1083
    //   1080: dconst_1
    //   1081: dstore #19
    //   1083: dload #19
    //   1085: dreturn
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_trainInstances == null) {
      stringBuffer.append("CFS subset evaluator has not been built yet\n");
    } else {
      stringBuffer.append("\tCFS Subset Evaluator\n");
      if (this.m_missingSeperate)
        stringBuffer.append("\tTreating missing values as a seperate value\n"); 
      if (this.m_locallyPredictive)
        stringBuffer.append("\tIncluding locally predictive attributes\n"); 
    } 
    return stringBuffer.toString();
  }
  
  private void addLocallyPredictive(BitSet paramBitSet) {
    boolean bool1 = false;
    boolean bool2 = true;
    double d = -1.0D;
    byte b = 0;
    BitSet bitSet = (BitSet)paramBitSet.clone();
    while (!bool1) {
      d = -1.0D;
      byte b1;
      for (b1 = 0; b1 < this.m_numAttribs; b1++) {
        int i;
        byte b2;
        if (b1 > this.m_classIndex) {
          i = b1;
          b2 = this.m_classIndex;
        } else {
          b2 = b1;
          i = this.m_classIndex;
        } 
        if (!bitSet.get(b1) && b1 != this.m_classIndex) {
          if (this.m_corr_matrix[i][b2] == -999.0F) {
            float f = correlate(b1, this.m_classIndex);
            this.m_corr_matrix[i][b2] = f;
          } 
          if (this.m_corr_matrix[i][b2] > d) {
            d = this.m_corr_matrix[i][b2];
            b = b1;
          } 
        } 
      } 
      if (d == -1.0D) {
        bool1 = true;
        continue;
      } 
      bool2 = true;
      bitSet.set(b);
      for (b1 = 0; b1 < this.m_numAttribs; b1++) {
        byte b2;
        byte b3;
        if (b1 > b) {
          b2 = b1;
          b3 = b;
        } else {
          b2 = b;
          b3 = b1;
        } 
        if (paramBitSet.get(b1)) {
          if (this.m_corr_matrix[b2][b3] == -999.0F) {
            float f = correlate(b1, b);
            this.m_corr_matrix[b2][b3] = f;
          } 
          if (this.m_corr_matrix[b2][b3] > d - this.m_c_Threshold) {
            bool2 = false;
            break;
          } 
        } 
      } 
      if (bool2)
        paramBitSet.set(b); 
    } 
  }
  
  public int[] postProcess(int[] paramArrayOfint) throws Exception {
    byte b1 = 0;
    if (!this.m_locallyPredictive)
      return paramArrayOfint; 
    BitSet bitSet = new BitSet(this.m_numAttribs);
    byte b2;
    for (b2 = 0; b2 < paramArrayOfint.length; b2++)
      bitSet.set(paramArrayOfint[b2]); 
    addLocallyPredictive(bitSet);
    for (b2 = 0; b2 < this.m_numAttribs; b2++) {
      if (bitSet.get(b2))
        b1++; 
    } 
    int[] arrayOfInt = new int[b1];
    b1 = 0;
    for (byte b3 = 0; b3 < this.m_numAttribs; b3++) {
      if (bitSet.get(b3))
        arrayOfInt[b1++] = b3; 
    } 
    return arrayOfInt;
  }
  
  protected void resetOptions() {
    this.m_trainInstances = null;
    this.m_missingSeperate = false;
    this.m_locallyPredictive = false;
    this.m_c_Threshold = 0.0D;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(AttributeSelection.SelectAttributes(new CfsSubsetEval(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\CfsSubsetEval.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */