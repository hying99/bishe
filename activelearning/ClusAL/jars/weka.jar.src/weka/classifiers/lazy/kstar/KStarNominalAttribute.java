package weka.classifiers.lazy.kstar;

import weka.core.Instance;
import weka.core.Instances;

public class KStarNominalAttribute implements KStarConstants {
  protected Instances m_TrainSet;
  
  protected Instance m_Test;
  
  protected Instance m_Train;
  
  protected int m_AttrIndex;
  
  protected double m_Stop = 1.0D;
  
  protected double m_MissingProb = 1.0D;
  
  protected double m_AverageProb = 1.0D;
  
  protected double m_SmallestProb = 1.0D;
  
  protected int m_TotalCount;
  
  protected int[] m_Distribution;
  
  protected int[][] m_RandClassCols;
  
  protected KStarCache m_Cache;
  
  protected int m_NumInstances;
  
  protected int m_NumClasses;
  
  protected int m_NumAttributes;
  
  protected int m_ClassType;
  
  protected int m_MissingMode = 4;
  
  protected int m_BlendMethod = 1;
  
  protected int m_BlendFactor = 20;
  
  public KStarNominalAttribute(Instance paramInstance1, Instance paramInstance2, int paramInt, Instances paramInstances, int[][] paramArrayOfint, KStarCache paramKStarCache) {
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
    String str = "(KStarNominalAttribute.transProb) ";
    double d = 0.0D;
    if (this.m_Cache.containsKey(this.m_Test.value(this.m_AttrIndex))) {
      KStarCache.TableEntry tableEntry = this.m_Cache.getCacheValues(this.m_Test.value(this.m_AttrIndex));
      this.m_Stop = tableEntry.value;
      this.m_MissingProb = tableEntry.pmiss;
    } else {
      generateAttrDistribution();
      if (this.m_BlendMethod == 2) {
        this.m_Stop = stopProbUsingEntropy();
      } else {
        this.m_Stop = stopProbUsingBlend();
      } 
      this.m_Cache.store(this.m_Test.value(this.m_AttrIndex), this.m_Stop, this.m_MissingProb);
    } 
    if (this.m_Train.isMissing(this.m_AttrIndex)) {
      d = this.m_MissingProb;
    } else {
      try {
        d = (1.0D - this.m_Stop) / this.m_Test.attribute(this.m_AttrIndex).numValues();
        if ((int)this.m_Test.value(this.m_AttrIndex) == (int)this.m_Train.value(this.m_AttrIndex))
          d += this.m_Stop; 
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } 
    return d;
  }
  
  private double stopProbUsingEntropy() {
    double d1;
    String str = "(KStarNominalAttribute.stopProbUsingEntropy)";
    if (this.m_ClassType != 1) {
      System.err.println("Error: " + str + " attribute class must be nominal!");
      System.exit(1);
    } 
    byte b = 0;
    double d4 = 0.0D;
    double d5 = 0.0D;
    double d6 = 0.0D;
    double d7 = 0.0D;
    KStarWrapper kStarWrapper1 = new KStarWrapper();
    KStarWrapper kStarWrapper2 = new KStarWrapper();
    KStarWrapper kStarWrapper3 = new KStarWrapper();
    double d2 = 0.005D;
    double d3 = 0.995D;
    calculateEntropy(d3, kStarWrapper2);
    calculateEntropy(d2, kStarWrapper1);
    if (kStarWrapper2.avgProb == 0.0D) {
      calculateEntropy(d2, kStarWrapper3);
    } else {
      double d8 = d2;
      double d10 = 0.05D;
      d4 = kStarWrapper1.minProb;
      d5 = kStarWrapper1.avgProb;
      d7 = d8 = d3;
      d10 = -0.05D;
      d4 = kStarWrapper2.minProb;
      d5 = kStarWrapper2.avgProb;
      double d9 = 0.0D;
      b = 0;
      do {
        double d12;
        b++;
        double d11 = d9;
        d8 += d10;
        if (d8 <= d2) {
          d8 = d2;
          d9 = 0.0D;
          d12 = -1.0D;
        } else if (d8 >= d3) {
          d8 = d3;
          d9 = 0.0D;
          d12 = -1.0D;
        } else {
          calculateEntropy(d8, kStarWrapper3);
          d9 = kStarWrapper3.randEntropy - kStarWrapper3.actEntropy;
          if (d9 < 0.0D) {
            d9 = 0.0D;
            if (Math.abs(d10) < 0.05D && d6 == 0.0D) {
              d7 = d2;
              d4 = kStarWrapper1.minProb;
              d5 = kStarWrapper1.avgProb;
              break;
            } 
          } 
          d12 = d9 - d11;
        } 
        if (d9 > d6) {
          d6 = d9;
          d7 = d8;
          d4 = kStarWrapper3.minProb;
          d5 = kStarWrapper3.avgProb;
        } 
        if (d12 >= 0.0D)
          continue; 
        if (Math.abs(d10) < 0.01D)
          break; 
        d10 /= -2.0D;
      } while (b <= 40);
    } 
    this.m_SmallestProb = d4;
    this.m_AverageProb = d5;
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
    if (Math.abs(d5 - this.m_TotalCount) < 1.0E-5D) {
      d1 = 1.0D;
    } else {
      d1 = d7;
    } 
    return d1;
  }
  
  private void calculateEntropy(double paramDouble, KStarWrapper paramKStarWrapper) {
    String str = "(KStarNominalAttribute.calculateEntropy)";
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
      Instance instance = this.m_TrainSet.instance(i);
      if (!instance.isMissing(this.m_AttrIndex)) {
        double d5 = PStar(this.m_Test, instance, this.m_AttrIndex, paramDouble);
        double d6 = d5 / this.m_TotalCount;
        if (d5 < d4)
          d4 = d5; 
        d3 += d6;
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
  
  private double stopProbUsingBlend() {
    double d1;
    String str = "(KStarNominalAttribute.stopProbUsingBlend) ";
    byte b = 0;
    KStarWrapper kStarWrapper1 = new KStarWrapper();
    KStarWrapper kStarWrapper2 = new KStarWrapper();
    KStarWrapper kStarWrapper3 = new KStarWrapper();
    int i = (int)this.m_Test.value(this.m_AttrIndex);
    double d2 = (this.m_TotalCount - this.m_Distribution[i]) * this.m_BlendFactor / 100.0D + this.m_Distribution[i];
    double d5 = 1.0D - this.m_BlendFactor / 100.0D;
    double d3 = 0.005D;
    double d4 = 0.995D;
    calculateSphereSize(i, d3, kStarWrapper1);
    kStarWrapper1.sphere -= d2;
    calculateSphereSize(i, d4, kStarWrapper2);
    kStarWrapper2.sphere -= d2;
    if (kStarWrapper2.avgProb == 0.0D) {
      calculateSphereSize(i, d5, kStarWrapper3);
    } else if (kStarWrapper2.sphere > 0.0D) {
      d5 = d4;
      kStarWrapper3.avgProb = kStarWrapper2.avgProb;
    } else {
      while (true) {
        b++;
        calculateSphereSize(i, d5, kStarWrapper3);
        kStarWrapper3.sphere -= d2;
        if (Math.abs(kStarWrapper3.sphere) <= 0.01D || b >= 40)
          break; 
        if (kStarWrapper3.sphere > 0.0D) {
          d3 = d5;
          d5 = (d4 + d3) / 2.0D;
          continue;
        } 
        d4 = d5;
        d5 = (d4 + d3) / 2.0D;
      } 
    } 
    this.m_SmallestProb = kStarWrapper3.minProb;
    this.m_AverageProb = kStarWrapper3.avgProb;
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
    if (Math.abs(kStarWrapper3.avgProb - this.m_TotalCount) < 1.0E-5D) {
      d1 = 1.0D;
    } else {
      d1 = d5;
    } 
    return d1;
  }
  
  private void calculateSphereSize(int paramInt, double paramDouble, KStarWrapper paramKStarWrapper) {
    String str = "(KStarNominalAttribute.calculateSphereSize) ";
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d4 = 1.0D;
    double d5 = 0.0D;
    for (byte b = 0; b < this.m_Distribution.length; b++) {
      int i = this.m_Distribution[b];
      if (i != 0) {
        double d;
        if (paramInt == b) {
          d = (paramDouble + (1.0D - paramDouble) / this.m_Distribution.length) / this.m_TotalCount;
          d1 += d * i;
          d2 += d * d * i;
        } else {
          d = (1.0D - paramDouble) / this.m_Distribution.length / this.m_TotalCount;
          d1 += d * i;
          d2 += d * d * i;
        } 
        if (d4 > d * this.m_TotalCount)
          d4 = d * this.m_TotalCount; 
      } 
    } 
    d5 = d1;
    double d3 = (d2 == 0.0D) ? 0.0D : (d1 * d1 / d2);
    paramKStarWrapper.sphere = d3;
    paramKStarWrapper.avgProb = d5;
    paramKStarWrapper.minProb = d4;
  }
  
  private double PStar(Instance paramInstance1, Instance paramInstance2, int paramInt, double paramDouble) {
    double d;
    String str = "(KStarNominalAttribute.PStar) ";
    int i = 0;
    try {
      i = paramInstance1.attribute(paramInt).numValues();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    if ((int)paramInstance1.value(paramInt) == (int)paramInstance2.value(paramInt)) {
      d = paramDouble + (1.0D - paramDouble) / i;
    } else {
      d = (1.0D - paramDouble) / i;
    } 
    return d;
  }
  
  private void generateAttrDistribution() {
    String str = "(KStarNominalAttribute.generateAttrDistribution)";
    this.m_Distribution = new int[this.m_TrainSet.attribute(this.m_AttrIndex).numValues()];
    for (byte b = 0; b < this.m_NumInstances; b++) {
      Instance instance = this.m_TrainSet.instance(b);
      if (!instance.isMissing(this.m_AttrIndex)) {
        this.m_TotalCount++;
        this.m_Distribution[(int)instance.value(this.m_AttrIndex)] = this.m_Distribution[(int)instance.value(this.m_AttrIndex)] + 1;
      } 
    } 
  }
  
  public void setOptions(int paramInt1, int paramInt2, int paramInt3) {
    this.m_MissingMode = paramInt1;
    this.m_BlendMethod = paramInt2;
    this.m_BlendFactor = paramInt3;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\lazy\kstar\KStarNominalAttribute.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */