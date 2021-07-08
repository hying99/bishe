package weka.classifiers.lazy.kstar;

import weka.core.Instance;
import weka.core.Instances;

public class KStarNumericAttribute implements KStarConstants {
  protected Instances m_TrainSet;
  
  protected Instance m_Test;
  
  protected Instance m_Train;
  
  protected int m_AttrIndex;
  
  protected double m_Scale = 1.0D;
  
  protected double m_MissingProb = 1.0D;
  
  protected double m_AverageProb = 1.0D;
  
  protected double m_SmallestProb = 1.0D;
  
  protected double[] m_Distances;
  
  protected int[][] m_RandClassCols;
  
  protected int m_ActualCount = 0;
  
  protected KStarCache m_Cache;
  
  protected int m_NumInstances;
  
  protected int m_NumClasses;
  
  protected int m_NumAttributes;
  
  protected int m_ClassType;
  
  protected int m_MissingMode = 4;
  
  protected int m_BlendMethod = 1;
  
  protected int m_BlendFactor = 20;
  
  public KStarNumericAttribute(Instance paramInstance1, Instance paramInstance2, int paramInt, Instances paramInstances, int[][] paramArrayOfint, KStarCache paramKStarCache) {
    this.m_Test = paramInstance1;
    this.m_Train = paramInstance2;
    this.m_AttrIndex = paramInt;
    this.m_TrainSet = paramInstances;
    this.m_RandClassCols = paramArrayOfint;
    this.m_Cache = paramKStarCache;
    init();
  }
  
  private void init() {
    try {
      this.m_NumInstances = this.m_TrainSet.numInstances();
      this.m_NumClasses = this.m_TrainSet.numClasses();
      this.m_NumAttributes = this.m_TrainSet.numAttributes();
      this.m_ClassType = this.m_TrainSet.classAttribute().type();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public double transProb() {
    double d;
    String str = "(KStarNumericAttribute.transProb) ";
    if (this.m_Cache.containsKey(this.m_Test.value(this.m_AttrIndex))) {
      KStarCache.TableEntry tableEntry = this.m_Cache.getCacheValues(this.m_Test.value(this.m_AttrIndex));
      this.m_Scale = tableEntry.value;
      this.m_MissingProb = tableEntry.pmiss;
    } else {
      if (this.m_BlendMethod == 2) {
        this.m_Scale = scaleFactorUsingEntropy();
      } else {
        this.m_Scale = scaleFactorUsingBlend();
      } 
      this.m_Cache.store(this.m_Test.value(this.m_AttrIndex), this.m_Scale, this.m_MissingProb);
    } 
    if (this.m_Train.isMissing(this.m_AttrIndex)) {
      d = this.m_MissingProb;
    } else {
      double d1 = Math.abs(this.m_Test.value(this.m_AttrIndex) - this.m_Train.value(this.m_AttrIndex));
      d = PStar(d1, this.m_Scale);
    } 
    return d;
  }
  
  private double scaleFactorUsingBlend() {
    String str = "(KStarNumericAttribute.scaleFactorUsingBlend)";
    byte b3 = 0;
    boolean bool = false;
    double d1 = -1.0D;
    double d2 = -1.0D;
    double d7 = 9.0E300D;
    null = 1.0D;
    double d8 = 0.0D;
    double d9 = 0.0D;
    double d10 = 0.0D;
    KStarWrapper kStarWrapper1 = new KStarWrapper();
    KStarWrapper kStarWrapper2 = new KStarWrapper();
    KStarWrapper kStarWrapper3 = new KStarWrapper();
    this.m_Distances = new double[this.m_NumInstances];
    for (byte b2 = 0; b2 < this.m_NumInstances; b2++) {
      if (this.m_TrainSet.instance(b2).isMissing(this.m_AttrIndex)) {
        this.m_Distances[b2] = -1.0D;
      } else {
        this.m_Distances[b2] = Math.abs(this.m_TrainSet.instance(b2).value(this.m_AttrIndex) - this.m_Test.value(this.m_AttrIndex));
        if (this.m_Distances[b2] + 1.0E-5D < d2 || d2 == -1.0D)
          if (this.m_Distances[b2] + 1.0E-5D < d1 || d1 == -1.0D) {
            d2 = d1;
            d1 = this.m_Distances[b2];
            b3 = 1;
          } else if (Math.abs(this.m_Distances[b2] - d1) < 1.0E-5D) {
            b3++;
          } else {
            d2 = this.m_Distances[b2];
          }  
        this.m_ActualCount++;
      } 
    } 
    if (d2 == -1.0D || d1 == -1.0D) {
      null = 1.0D;
      this.m_SmallestProb = this.m_AverageProb = 1.0D;
      return null;
    } 
    double d3 = 1.0D / (d2 - d1);
    byte b1 = 0;
    double d6 = (this.m_ActualCount - b3) * this.m_BlendFactor / 100.0D + b3;
    if (this.m_BlendFactor == 0)
      d6++; 
    double d5 = 0.005D;
    double d4 = d3 * 16.0D;
    calculateSphereSize(d5, kStarWrapper1);
    kStarWrapper1.sphere -= d6;
    calculateSphereSize(d4, kStarWrapper2);
    kStarWrapper2.sphere -= d6;
    if (kStarWrapper1.sphere < 0.0D) {
      d10 = d5;
      d8 = kStarWrapper1.avgProb;
      d9 = kStarWrapper1.minProb;
    } else if (kStarWrapper2.sphere > 0.0D) {
      d10 = d4;
      d8 = kStarWrapper2.avgProb;
      d9 = kStarWrapper2.minProb;
    } else {
      while (true) {
        calculateSphereSize(d3, kStarWrapper3);
        kStarWrapper3.sphere -= d6;
        if (Math.abs(kStarWrapper3.sphere) < d7) {
          d7 = Math.abs(kStarWrapper3.sphere);
          d10 = d3;
          d8 = kStarWrapper3.avgProb;
          d9 = kStarWrapper3.minProb;
        } 
        if (Math.abs(kStarWrapper3.sphere) <= 0.01D)
          break; 
        if (kStarWrapper3.sphere > 0.0D) {
          double d = (d3 + d4) / 2.0D;
          d5 = d3;
          d3 = d;
        } else {
          double d = (d3 + d5) / 2.0D;
          d4 = d3;
          d3 = d;
        } 
        if (++b1 > 40) {
          d3 = d10;
          break;
        } 
      } 
    } 
    this.m_SmallestProb = d9;
    this.m_AverageProb = d8;
    switch (this.m_MissingMode) {
      case 1:
        this.m_MissingProb = 0.0D;
        break;
      case 3:
        this.m_MissingProb = 1.0D;
        break;
      case 2:
        this.m_MissingProb = this.m_SmallestProb;
        break;
      case 4:
        this.m_MissingProb = this.m_AverageProb;
        break;
    } 
    return d10;
  }
  
  private void calculateSphereSize(double paramDouble, KStarWrapper paramKStarWrapper) {
    String str = "(KStarNumericAttribute.calculateSphereSize)";
    double d2 = 1.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    for (byte b = 0; b < this.m_NumInstances; b++) {
      if (this.m_Distances[b] >= 0.0D) {
        double d5 = PStar(this.m_Distances[b], paramDouble);
        if (d2 > d5)
          d2 = d5; 
        double d6 = d5 / this.m_ActualCount;
        d3 += d6;
        d4 += d6 * d6;
      } 
    } 
    double d1 = (d4 == 0.0D) ? 0.0D : (d3 * d3 / d4);
    paramKStarWrapper.sphere = d1;
    paramKStarWrapper.avgProb = d3;
    paramKStarWrapper.minProb = d2;
  }
  
  private double scaleFactorUsingEntropy() {
    String str = "(KStarNumericAttribute.scaleFactorUsingEntropy)";
    if (this.m_ClassType != 1) {
      System.err.println("Error: " + str + " attribute class must be nominal!");
      System.exit(1);
    } 
    byte b2 = 0;
    double d1 = -1.0D;
    double d2 = -1.0D;
    double d7 = 0.0D;
    double d8 = 0.0D;
    double d11 = 0.0D;
    double d12 = 0.0D;
    double d13 = 0.0D;
    double d14 = 0.0D;
    null = 1.0D;
    KStarWrapper kStarWrapper1 = new KStarWrapper();
    KStarWrapper kStarWrapper2 = new KStarWrapper();
    KStarWrapper kStarWrapper3 = new KStarWrapper();
    this.m_Distances = new double[this.m_NumInstances];
    for (byte b1 = 0; b1 < this.m_NumInstances; b1++) {
      if (this.m_TrainSet.instance(b1).isMissing(this.m_AttrIndex)) {
        this.m_Distances[b1] = -1.0D;
      } else {
        this.m_Distances[b1] = Math.abs(this.m_TrainSet.instance(b1).value(this.m_AttrIndex) - this.m_Test.value(this.m_AttrIndex));
        if (this.m_Distances[b1] + 1.0E-5D < d2 || d2 == -1.0D)
          if (this.m_Distances[b1] + 1.0E-5D < d1 || d1 == -1.0D) {
            d2 = d1;
            d1 = this.m_Distances[b1];
            b2 = 1;
          } else if (Math.abs(this.m_Distances[b1] - d1) < 1.0E-5D) {
            b2++;
          } else {
            d2 = this.m_Distances[b1];
          }  
        this.m_ActualCount++;
      } 
    } 
    if (d2 == -1.0D || d1 == -1.0D) {
      null = 1.0D;
      this.m_SmallestProb = this.m_AverageProb = 1.0D;
      return null;
    } 
    double d3 = 1.0D / (d2 - d1);
    double d5 = 0.005D;
    double d4 = d3 * 8.0D;
    calculateEntropy(d4, kStarWrapper2);
    calculateEntropy(d5, kStarWrapper1);
    double d9 = kStarWrapper1.actEntropy - kStarWrapper2.actEntropy;
    double d10 = kStarWrapper1.randEntropy - kStarWrapper2.randEntropy;
    double d16 = d3 = d5;
    double d17 = 0.1D;
    double d15 = d17;
    double d18 = kStarWrapper1.avgProb;
    double d19 = kStarWrapper1.minProb;
    double d6 = (d4 - d5) / 20.0D;
    byte b3 = 0;
    do {
      double d20;
      b3++;
      double d21 = d17;
      d3 += Math.log(d3 + 1.0D) * d6;
      if (d3 <= d5) {
        d3 = d5;
        d17 = 0.0D;
        d20 = -1.0D;
      } else if (d3 >= d4) {
        d3 = d4;
        d17 = 0.0D;
        d20 = -1.0D;
      } else {
        calculateEntropy(d3, kStarWrapper3);
        kStarWrapper3.randEntropy = (kStarWrapper3.randEntropy - kStarWrapper2.randEntropy) / d10;
        kStarWrapper3.actEntropy = (kStarWrapper3.actEntropy - kStarWrapper2.actEntropy) / d10;
        d17 = kStarWrapper3.randEntropy - kStarWrapper3.actEntropy;
        if (d17 < 0.1D) {
          d17 = 0.1D;
          if (d6 < 0.0D) {
            d15 = d17;
            d16 = d5;
            d18 = kStarWrapper1.avgProb;
            d19 = kStarWrapper1.minProb;
            break;
          } 
        } 
        d20 = d17 - d21;
      } 
      if (d17 > d15) {
        d15 = d17;
        d16 = d3;
        d19 = kStarWrapper3.minProb;
        d18 = kStarWrapper3.avgProb;
      } 
      if (d20 >= 0.0D)
        continue; 
      if (Math.abs(d6) < 0.01D)
        break; 
      d6 /= -4.0D;
    } while (b3 <= 40);
    this.m_SmallestProb = d19;
    this.m_AverageProb = d18;
    switch (this.m_MissingMode) {
      case 1:
        this.m_MissingProb = 0.0D;
        break;
      case 3:
        this.m_MissingProb = 1.0D;
        break;
      case 2:
        this.m_MissingProb = this.m_SmallestProb;
        break;
      case 4:
        this.m_MissingProb = this.m_AverageProb;
        break;
    } 
    return d16;
  }
  
  private void calculateEntropy(double paramDouble, KStarWrapper paramKStarWrapper) {
    String str = "(KStarNumericAttribute.calculateEntropy)";
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 1.0D;
    double[][] arrayOfDouble = new double[6][this.m_NumClasses];
    int j;
    for (j = 0; j <= 5; j++) {
      for (byte b1 = 0; b1 < this.m_NumClasses; b1++)
        arrayOfDouble[j][b1] = 0.0D; 
    } 
    int i;
    for (i = 0; i < this.m_NumInstances; i++) {
      if (this.m_Distances[i] >= 0.0D) {
        double d5 = PStar(this.m_Distances[i], paramDouble);
        double d6 = d5 / this.m_ActualCount;
        d3 += d6;
        if (d5 < d4)
          d4 = d5; 
        for (byte b1 = 0; b1 <= 5; b1++)
          arrayOfDouble[b1][this.m_RandClassCols[b1][i]] = arrayOfDouble[b1][this.m_RandClassCols[b1][i]] + d6; 
      } 
    } 
    for (j = this.m_NumClasses - 1; j >= 0; j--) {
      double d = arrayOfDouble[5][j] / d3;
      if (d > 0.0D)
        d1 -= d * Math.log(d) / 0.693147181D; 
    } 
    for (byte b = 0; b < 5; b++) {
      for (i = this.m_NumClasses - 1; i >= 0; i--) {
        double d = arrayOfDouble[b][i] / d3;
        if (d > 0.0D)
          d2 -= d * Math.log(d) / 0.693147181D; 
      } 
    } 
    d2 /= 5.0D;
    paramKStarWrapper.actEntropy = d1;
    paramKStarWrapper.randEntropy = d2;
    paramKStarWrapper.avgProb = d3;
    paramKStarWrapper.minProb = d4;
  }
  
  private double PStar(double paramDouble1, double paramDouble2) {
    return paramDouble2 * Math.exp(-2.0D * paramDouble1 * paramDouble2);
  }
  
  public void setOptions(int paramInt1, int paramInt2, int paramInt3) {
    this.m_MissingMode = paramInt1;
    this.m_BlendMethod = paramInt2;
    this.m_BlendFactor = paramInt3;
  }
  
  public void setMissingMode(int paramInt) {
    this.m_MissingMode = paramInt;
  }
  
  public void setBlendMethod(int paramInt) {
    this.m_BlendMethod = paramInt;
  }
  
  public void setBlendFactor(int paramInt) {
    this.m_BlendFactor = paramInt;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\lazy\kstar\KStarNumericAttribute.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */