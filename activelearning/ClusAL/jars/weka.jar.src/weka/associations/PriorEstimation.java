package weka.associations;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Random;
import weka.core.Instances;
import weka.core.SpecialFunctions;
import weka.core.Utils;

public class PriorEstimation implements Serializable {
  protected int m_numRandRules;
  
  protected int m_numIntervals;
  
  protected static final int SEED = 0;
  
  protected static final int MAX_N = 1024;
  
  protected Random m_randNum;
  
  protected Instances m_instances;
  
  protected boolean m_CARs;
  
  protected Hashtable m_distribution;
  
  protected Hashtable m_priors;
  
  protected double m_sum;
  
  protected double[] m_midPoints;
  
  public PriorEstimation(Instances paramInstances, int paramInt1, int paramInt2, boolean paramBoolean) {
    this.m_instances = paramInstances;
    this.m_CARs = paramBoolean;
    this.m_numRandRules = paramInt1;
    this.m_numIntervals = paramInt2;
    this.m_randNum = this.m_instances.getRandomNumberGenerator(0L);
  }
  
  public final void generateDistribution() throws Exception {
    int i = this.m_instances.numAttributes();
    byte b2 = 0;
    boolean bool = false;
    this.m_distribution = new Hashtable(i * this.m_numIntervals);
    if (this.m_instances.numAttributes() == 0)
      throw new Exception("Dataset has no attributes!"); 
    if (this.m_instances.numAttributes() >= 1024)
      throw new Exception("Dataset has to many attributes for prior estimation!"); 
    if (this.m_instances.numInstances() == 0)
      throw new Exception("Dataset has no instances!"); 
    byte b3;
    for (b3 = 0; b3 < i; b3++) {
      if (this.m_instances.attribute(b3).isNumeric())
        throw new Exception("Can't handle numeric attributes!"); 
    } 
    if (this.m_numIntervals == 0 || this.m_numRandRules == 0)
      throw new Exception("Prior initialisation impossible"); 
    midPoints();
    for (byte b1 = 1; b1 <= i; b1++) {
      this.m_sum = 0.0D;
      b3 = 0;
      b2 = 0;
      bool = false;
      while (b3 < this.m_numRandRules) {
        int[] arrayOfInt1;
        RuleItem ruleItem;
        b2++;
        boolean bool1 = false;
        if (!this.m_CARs) {
          arrayOfInt1 = randomRule(i, b1, this.m_randNum);
          ruleItem = splitItemSet(this.m_randNum.nextInt(b1), arrayOfInt1);
        } else {
          arrayOfInt1 = randomCARule(i, b1, this.m_randNum);
          ruleItem = addCons(arrayOfInt1);
        } 
        int[] arrayOfInt2 = new int[i];
        for (byte b = 0; b < arrayOfInt1.length; b++) {
          if (ruleItem.m_premise.m_items[b] != -1) {
            arrayOfInt2[b] = ruleItem.m_premise.m_items[b];
          } else if (ruleItem.m_consequence.m_items[b] != -1) {
            arrayOfInt2[b] = ruleItem.m_consequence.m_items[b];
          } else {
            arrayOfInt2[b] = -1;
          } 
        } 
        ItemSet itemSet = new ItemSet(arrayOfInt2);
        updateCounters(itemSet);
        int j = itemSet.m_counter;
        if (j > 0)
          bool1 = true; 
        updateCounters(ruleItem.m_premise);
        b3++;
        if (bool1)
          buildDistribution(j / ruleItem.m_premise.m_counter, b1); 
      } 
      if (this.m_sum > 0.0D) {
        byte b;
        for (b = 0; b < this.m_midPoints.length; b++) {
          String str = String.valueOf(this.m_midPoints[b]).concat(String.valueOf(b1));
          Double double_ = (Double)this.m_distribution.remove(str);
          if (double_ == null) {
            this.m_distribution.put(str, new Double(1.0D / this.m_numIntervals));
            this.m_sum += 1.0D / this.m_numIntervals;
          } else {
            this.m_distribution.put(str, double_);
          } 
        } 
        for (b = 0; b < this.m_midPoints.length; b++) {
          double d = 0.0D;
          String str = String.valueOf(this.m_midPoints[b]).concat(String.valueOf(b1));
          Double double_ = (Double)this.m_distribution.remove(str);
          if (double_ != null) {
            d = double_.doubleValue() / this.m_sum;
            this.m_distribution.put(str, new Double(d));
          } 
        } 
      } else {
        for (byte b = 0; b < this.m_midPoints.length; b++) {
          String str = String.valueOf(this.m_midPoints[b]).concat(String.valueOf(b1));
          this.m_distribution.put(str, new Double(1.0D / this.m_numIntervals));
        } 
      } 
    } 
  }
  
  public final int[] randomRule(int paramInt1, int paramInt2, Random paramRandom) {
    int[] arrayOfInt = new int[paramInt1];
    int i;
    for (i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = -1; 
    i = paramInt2;
    if (i == paramInt1) {
      i = 0;
      for (byte b = 0; b < arrayOfInt.length; b++)
        arrayOfInt[b] = this.m_randNum.nextInt(this.m_instances.attribute(b).numValues()); 
    } 
    while (i > 0) {
      int j = paramRandom.nextInt(paramInt1);
      if (arrayOfInt[j] == -1) {
        i--;
        arrayOfInt[j] = this.m_randNum.nextInt(this.m_instances.attribute(j).numValues());
      } 
    } 
    return arrayOfInt;
  }
  
  public final int[] randomCARule(int paramInt1, int paramInt2, Random paramRandom) {
    int[] arrayOfInt = new int[paramInt1];
    int i;
    for (i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = -1; 
    if (paramInt2 == 1)
      return arrayOfInt; 
    i = paramInt2 - 1;
    if (i == paramInt1 - 1) {
      i = 0;
      for (byte b = 0; b < arrayOfInt.length; b++) {
        if (b != this.m_instances.classIndex())
          arrayOfInt[b] = this.m_randNum.nextInt(this.m_instances.attribute(b).numValues()); 
      } 
    } 
    while (i > 0) {
      int j = paramRandom.nextInt(paramInt1);
      if (arrayOfInt[j] == -1 && j != this.m_instances.classIndex()) {
        i--;
        arrayOfInt[j] = this.m_randNum.nextInt(this.m_instances.attribute(j).numValues());
      } 
    } 
    return arrayOfInt;
  }
  
  public final void buildDistribution(double paramDouble1, double paramDouble2) {
    double d = findIntervall(paramDouble1);
    String str = String.valueOf(d).concat(String.valueOf(paramDouble2));
    this.m_sum += paramDouble1;
    Double double_ = (Double)this.m_distribution.remove(str);
    if (double_ != null)
      paramDouble1 += double_.doubleValue(); 
    this.m_distribution.put(str, new Double(paramDouble1));
  }
  
  public final double findIntervall(double paramDouble) {
    if (paramDouble == 1.0D)
      return this.m_midPoints[this.m_midPoints.length - 1]; 
    int i = this.m_midPoints.length - 1;
    int j = 0;
    while (Math.abs(i - j) > 1) {
      int k = (j + i) / 2;
      if (paramDouble > this.m_midPoints[k])
        j = k + 1; 
      if (paramDouble < this.m_midPoints[k])
        i = k - 1; 
      if (paramDouble == this.m_midPoints[k])
        return this.m_midPoints[k]; 
    } 
    return (Math.abs(paramDouble - this.m_midPoints[j]) <= Math.abs(paramDouble - this.m_midPoints[i])) ? this.m_midPoints[j] : this.m_midPoints[i];
  }
  
  public final double calculatePriorSum(boolean paramBoolean, double paramDouble) {
    double d1 = 0.0D;
    double d2 = logbinomialCoefficient(this.m_instances.numAttributes(), this.m_instances.numAttributes() / 2);
    for (byte b = 1; b <= this.m_instances.numAttributes(); b++) {
      if (paramBoolean) {
        double d;
        String str = String.valueOf(paramDouble).concat(String.valueOf(b));
        Double double_ = (Double)this.m_distribution.get(str);
        if (double_ != null) {
          d = double_.doubleValue();
        } else {
          d = 0.0D;
        } 
        if (d != 0.0D) {
          double d3 = Utils.log2(d) - d2 + Utils.log2(Math.pow(2.0D, b) - 1.0D) + logbinomialCoefficient(this.m_instances.numAttributes(), b);
          d1 += Math.pow(2.0D, d3);
        } 
      } else {
        double d = Utils.log2(Math.pow(2.0D, b) - 1.0D) - d2 + logbinomialCoefficient(this.m_instances.numAttributes(), b);
        d1 += Math.pow(2.0D, d);
      } 
    } 
    return d1;
  }
  
  public static final double logbinomialCoefficient(int paramInt1, int paramInt2) {
    null = 1.0D;
    return (paramInt1 == paramInt2 || paramInt2 == 0) ? null : SpecialFunctions.log2Binomial(paramInt1, paramInt2);
  }
  
  public final Hashtable estimatePrior() throws Exception {
    Hashtable hashtable = new Hashtable(this.m_numIntervals);
    double d = calculatePriorSum(false, 1.0D);
    generateDistribution();
    for (byte b = 0; b < this.m_numIntervals; b++) {
      double d2 = this.m_midPoints[b];
      double d1 = calculatePriorSum(true, d2) / d;
      hashtable.put(new Double(d2), new Double(d1));
    } 
    return hashtable;
  }
  
  public final void midPoints() {
    this.m_midPoints = new double[this.m_numIntervals];
    for (byte b = 0; b < this.m_numIntervals; b++)
      this.m_midPoints[b] = midPoint(1.0D / this.m_numIntervals, b); 
  }
  
  public double midPoint(double paramDouble, int paramInt) {
    return paramDouble * paramInt + paramDouble / 2.0D;
  }
  
  public final double[] getMidPoints() {
    return this.m_midPoints;
  }
  
  public final RuleItem splitItemSet(int paramInt, int[] paramArrayOfint) {
    int[] arrayOfInt = new int[this.m_instances.numAttributes()];
    System.arraycopy(paramArrayOfint, 0, arrayOfInt, 0, paramArrayOfint.length);
    int i = paramInt;
    while (i > 0) {
      int j = this.m_randNum.nextInt(paramArrayOfint.length);
      if (arrayOfInt[j] != -1) {
        i--;
        arrayOfInt[j] = -1;
      } 
    } 
    if (paramInt == 0) {
      for (byte b = 0; b < paramArrayOfint.length; b++)
        paramArrayOfint[b] = -1; 
    } else {
      for (byte b = 0; b < paramArrayOfint.length; b++) {
        if (arrayOfInt[b] != -1)
          paramArrayOfint[b] = -1; 
      } 
    } 
    ItemSet itemSet1 = new ItemSet(paramArrayOfint);
    ItemSet itemSet2 = new ItemSet(arrayOfInt);
    RuleItem ruleItem = new RuleItem();
    ruleItem.m_premise = itemSet1;
    ruleItem.m_consequence = itemSet2;
    return ruleItem;
  }
  
  public final RuleItem addCons(int[] paramArrayOfint) {
    ItemSet itemSet1 = new ItemSet(paramArrayOfint);
    int[] arrayOfInt = new int[paramArrayOfint.length];
    for (byte b = 0; b < paramArrayOfint.length; b++)
      arrayOfInt[b] = -1; 
    arrayOfInt[this.m_instances.classIndex()] = this.m_randNum.nextInt(this.m_instances.attribute(this.m_instances.classIndex()).numValues());
    ItemSet itemSet2 = new ItemSet(arrayOfInt);
    RuleItem ruleItem = new RuleItem();
    ruleItem.m_premise = itemSet1;
    ruleItem.m_consequence = itemSet2;
    return ruleItem;
  }
  
  public final void updateCounters(ItemSet paramItemSet) {
    for (byte b = 0; b < this.m_instances.numInstances(); b++)
      paramItemSet.upDateCounter(this.m_instances.instance(b)); 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\PriorEstimation.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */