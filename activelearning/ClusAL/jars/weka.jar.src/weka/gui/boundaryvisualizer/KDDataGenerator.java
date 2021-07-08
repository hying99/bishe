package weka.gui.boundaryvisualizer;

import java.io.Serializable;
import java.util.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class KDDataGenerator implements DataGenerator, Serializable {
  private Instances m_instances;
  
  private double[] m_standardDeviations;
  
  private double[] m_globalMeansOrModes;
  
  private double m_minStdDev = 1.0E-5D;
  
  private double m_laplaceConst = 1.0D;
  
  private int m_seed = 1;
  
  private Random m_random;
  
  private boolean[] m_weightingDimensions;
  
  private double[] m_weightingValues;
  
  private static double m_normConst = Math.sqrt(6.283185307179586D);
  
  private int m_kernelBandwidth = 3;
  
  private double[][] m_kernelParams;
  
  protected double[] m_Min;
  
  protected double[] m_Max;
  
  public void buildGenerator(Instances paramInstances) throws Exception {
    this.m_random = new Random(this.m_seed);
    this.m_instances = paramInstances;
    this.m_standardDeviations = new double[this.m_instances.numAttributes()];
    this.m_globalMeansOrModes = new double[this.m_instances.numAttributes()];
    if (this.m_weightingDimensions == null)
      this.m_weightingDimensions = new boolean[this.m_instances.numAttributes()]; 
    for (byte b = 0; b < this.m_instances.numAttributes(); b++) {
      if (b != this.m_instances.classIndex())
        this.m_globalMeansOrModes[b] = this.m_instances.meanOrMode(b); 
    } 
    this.m_kernelParams = new double[this.m_instances.numInstances()][this.m_instances.numAttributes()];
    computeParams();
  }
  
  public double[] getWeights() {
    double[] arrayOfDouble = new double[this.m_instances.numInstances()];
    for (byte b = 0; b < this.m_instances.numInstances(); b++) {
      double d = 1.0D;
      for (byte b1 = 0; b1 < this.m_instances.numAttributes(); b1++) {
        if (this.m_weightingDimensions[b1]) {
          double d1 = 0.0D;
          if (!this.m_instances.instance(b).isMissing(b1)) {
            d1 = this.m_instances.instance(b).value(b1);
          } else {
            d1 = this.m_globalMeansOrModes[b1];
          } 
          double d2 = 1.0D;
          d2 = normalDens(this.m_weightingValues[b1], d1, this.m_kernelParams[b][b1]);
          d *= d2;
        } 
      } 
      arrayOfDouble[b] = d;
    } 
    return arrayOfDouble;
  }
  
  private double[] computeCumulativeDistribution(double[] paramArrayOfdouble) {
    double[] arrayOfDouble = new double[paramArrayOfdouble.length];
    double d = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      d += paramArrayOfdouble[b];
      arrayOfDouble[b] = d;
    } 
    return arrayOfDouble;
  }
  
  public double[][] generateInstances(int[] paramArrayOfint) throws Exception {
    double[][] arrayOfDouble = new double[this.m_instances.numInstances()][];
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      arrayOfDouble[paramArrayOfint[b]] = new double[this.m_instances.numAttributes()];
      for (byte b1 = 0; b1 < this.m_instances.numAttributes(); b1++) {
        if (!this.m_weightingDimensions[b1] && b1 != this.m_instances.classIndex())
          if (this.m_instances.attribute(b1).isNumeric()) {
            double d1 = 0.0D;
            double d2 = this.m_random.nextGaussian();
            if (!this.m_instances.instance(paramArrayOfint[b]).isMissing(b1)) {
              d1 = this.m_instances.instance(paramArrayOfint[b]).value(b1);
            } else {
              d1 = this.m_globalMeansOrModes[b1];
            } 
            d2 *= this.m_kernelParams[paramArrayOfint[b]][b1];
            d2 += d1;
            arrayOfDouble[paramArrayOfint[b]][b1] = d2;
          } else {
            double[] arrayOfDouble1 = new double[this.m_instances.attribute(b1).numValues()];
            for (byte b2 = 0; b2 < arrayOfDouble1.length; b2++)
              arrayOfDouble1[b2] = this.m_laplaceConst; 
            if (!this.m_instances.instance(paramArrayOfint[b]).isMissing(b1)) {
              arrayOfDouble1[(int)this.m_instances.instance(paramArrayOfint[b]).value(b1)] = arrayOfDouble1[(int)this.m_instances.instance(paramArrayOfint[b]).value(b1)] + 1.0D;
            } else {
              arrayOfDouble1[(int)this.m_globalMeansOrModes[b1]] = arrayOfDouble1[(int)this.m_globalMeansOrModes[b1]] + 1.0D;
            } 
            Utils.normalize(arrayOfDouble1);
            double[] arrayOfDouble2 = computeCumulativeDistribution(arrayOfDouble1);
            double d = this.m_random.nextDouble();
            byte b3 = 0;
            for (byte b4 = 0; b4 < arrayOfDouble2.length; b4++) {
              if (d <= arrayOfDouble2[b4]) {
                b3 = b4;
                break;
              } 
            } 
            arrayOfDouble[paramArrayOfint[b]][b1] = b3;
          }  
      } 
    } 
    return arrayOfDouble;
  }
  
  private double normalDens(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d = paramDouble1 - paramDouble2;
    return 1.0D / m_normConst * paramDouble3 * Math.exp(-(d * d / 2.0D * paramDouble3 * paramDouble3));
  }
  
  public void setWeightingDimensions(boolean[] paramArrayOfboolean) {
    this.m_weightingDimensions = paramArrayOfboolean;
  }
  
  public void setWeightingValues(double[] paramArrayOfdouble) {
    this.m_weightingValues = paramArrayOfdouble;
  }
  
  public int getNumGeneratingModels() {
    return (this.m_instances != null) ? this.m_instances.numInstances() : 0;
  }
  
  public void setKernelBandwidth(int paramInt) {
    this.m_kernelBandwidth = paramInt;
  }
  
  public int getKernelBandwidth() {
    return this.m_kernelBandwidth;
  }
  
  public void setSeed(int paramInt) {
    this.m_seed = paramInt;
    this.m_random = new Random(this.m_seed);
  }
  
  private double distance(Instance paramInstance1, Instance paramInstance2) {
    double d = 0.0D;
    for (byte b = 0; b < this.m_instances.numAttributes(); b++) {
      if (b != this.m_instances.classIndex()) {
        double d1;
        double d2 = this.m_globalMeansOrModes[b];
        double d3 = this.m_globalMeansOrModes[b];
        switch (this.m_instances.attribute(b).type()) {
          case 0:
            if (!paramInstance1.isMissing(b))
              d2 = paramInstance1.value(b); 
            if (!paramInstance2.isMissing(b))
              d3 = paramInstance2.value(b); 
            d1 = norm(d2, b) - norm(d3, b);
            break;
          default:
            d1 = 0.0D;
            break;
        } 
        d += d1 * d1;
      } 
    } 
    return Math.sqrt(d);
  }
  
  private double norm(double paramDouble, int paramInt) {
    return (Double.isNaN(this.m_Min[paramInt]) || Utils.eq(this.m_Max[paramInt], this.m_Min[paramInt])) ? 0.0D : ((paramDouble - this.m_Min[paramInt]) / (this.m_Max[paramInt] - this.m_Min[paramInt]));
  }
  
  private void updateMinMax(Instance paramInstance) {
    for (byte b = 0; b < this.m_instances.numAttributes(); b++) {
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
  
  private void computeParams() throws Exception {
    // Byte code:
    //   0: aload_0
    //   1: aload_0
    //   2: getfield m_instances : Lweka/core/Instances;
    //   5: invokevirtual numAttributes : ()I
    //   8: newarray double
    //   10: putfield m_Min : [D
    //   13: aload_0
    //   14: aload_0
    //   15: getfield m_instances : Lweka/core/Instances;
    //   18: invokevirtual numAttributes : ()I
    //   21: newarray double
    //   23: putfield m_Max : [D
    //   26: iconst_0
    //   27: istore_1
    //   28: iload_1
    //   29: aload_0
    //   30: getfield m_instances : Lweka/core/Instances;
    //   33: invokevirtual numAttributes : ()I
    //   36: if_icmpge -> 61
    //   39: aload_0
    //   40: getfield m_Min : [D
    //   43: iload_1
    //   44: aload_0
    //   45: getfield m_Max : [D
    //   48: iload_1
    //   49: ldc2_w NaN
    //   52: dup2_x2
    //   53: dastore
    //   54: dastore
    //   55: iinc #1, 1
    //   58: goto -> 28
    //   61: iconst_0
    //   62: istore_1
    //   63: iload_1
    //   64: aload_0
    //   65: getfield m_instances : Lweka/core/Instances;
    //   68: invokevirtual numInstances : ()I
    //   71: if_icmpge -> 92
    //   74: aload_0
    //   75: aload_0
    //   76: getfield m_instances : Lweka/core/Instances;
    //   79: iload_1
    //   80: invokevirtual instance : (I)Lweka/core/Instance;
    //   83: invokespecial updateMinMax : (Lweka/core/Instance;)V
    //   86: iinc #1, 1
    //   89: goto -> 63
    //   92: aload_0
    //   93: getfield m_instances : Lweka/core/Instances;
    //   96: invokevirtual numInstances : ()I
    //   99: newarray double
    //   101: astore_1
    //   102: iconst_0
    //   103: istore_2
    //   104: iload_2
    //   105: aload_0
    //   106: getfield m_instances : Lweka/core/Instances;
    //   109: invokevirtual numInstances : ()I
    //   112: if_icmpge -> 327
    //   115: aload_0
    //   116: getfield m_instances : Lweka/core/Instances;
    //   119: iload_2
    //   120: invokevirtual instance : (I)Lweka/core/Instance;
    //   123: astore_3
    //   124: iconst_0
    //   125: istore #4
    //   127: iload #4
    //   129: aload_0
    //   130: getfield m_instances : Lweka/core/Instances;
    //   133: invokevirtual numInstances : ()I
    //   136: if_icmpge -> 163
    //   139: aload_1
    //   140: iload #4
    //   142: aload_0
    //   143: aload_3
    //   144: aload_0
    //   145: getfield m_instances : Lweka/core/Instances;
    //   148: iload #4
    //   150: invokevirtual instance : (I)Lweka/core/Instance;
    //   153: invokespecial distance : (Lweka/core/Instance;Lweka/core/Instance;)D
    //   156: dastore
    //   157: iinc #4, 1
    //   160: goto -> 127
    //   163: aload_1
    //   164: invokestatic sort : ([D)[I
    //   167: astore #4
    //   169: aload_0
    //   170: getfield m_kernelBandwidth : I
    //   173: istore #5
    //   175: aload_1
    //   176: aload #4
    //   178: iload #5
    //   180: iaload
    //   181: daload
    //   182: dstore #6
    //   184: dload #6
    //   186: dconst_0
    //   187: dcmpg
    //   188: ifgt -> 253
    //   191: iload #5
    //   193: iconst_1
    //   194: iadd
    //   195: istore #8
    //   197: iload #8
    //   199: aload #4
    //   201: arraylength
    //   202: if_icmpge -> 236
    //   205: aload_1
    //   206: aload #4
    //   208: iload #8
    //   210: iaload
    //   211: daload
    //   212: dload #6
    //   214: dcmpl
    //   215: ifle -> 230
    //   218: aload_1
    //   219: aload #4
    //   221: iload #8
    //   223: iaload
    //   224: daload
    //   225: dstore #6
    //   227: goto -> 236
    //   230: iinc #8, 1
    //   233: goto -> 197
    //   236: dload #6
    //   238: dconst_0
    //   239: dcmpg
    //   240: ifgt -> 253
    //   243: new java/lang/Exception
    //   246: dup
    //   247: ldc 'All training instances coincide with test instance!'
    //   249: invokespecial <init> : (Ljava/lang/String;)V
    //   252: athrow
    //   253: iconst_0
    //   254: istore #8
    //   256: iload #8
    //   258: aload_0
    //   259: getfield m_instances : Lweka/core/Instances;
    //   262: invokevirtual numAttributes : ()I
    //   265: if_icmpge -> 321
    //   268: aload_0
    //   269: getfield m_Max : [D
    //   272: iload #8
    //   274: daload
    //   275: aload_0
    //   276: getfield m_Min : [D
    //   279: iload #8
    //   281: daload
    //   282: dsub
    //   283: dconst_0
    //   284: dcmpl
    //   285: ifle -> 315
    //   288: aload_0
    //   289: getfield m_kernelParams : [[D
    //   292: iload_2
    //   293: aaload
    //   294: iload #8
    //   296: dload #6
    //   298: aload_0
    //   299: getfield m_Max : [D
    //   302: iload #8
    //   304: daload
    //   305: aload_0
    //   306: getfield m_Min : [D
    //   309: iload #8
    //   311: daload
    //   312: dsub
    //   313: dmul
    //   314: dastore
    //   315: iinc #8, 1
    //   318: goto -> 256
    //   321: iinc #2, 1
    //   324: goto -> 104
    //   327: return
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\boundaryvisualizer\KDDataGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */