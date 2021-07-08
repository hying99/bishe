package weka.associations;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.TreeSet;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Statistics;
import weka.core.Utils;

public class RuleGeneration implements Serializable {
  protected int[] m_items;
  
  protected int m_counter;
  
  protected int m_totalTransactions;
  
  protected boolean m_change = false;
  
  protected double m_expectation;
  
  protected static final int MAX_N = 300;
  
  protected int m_minRuleCount;
  
  protected double[] m_midPoints;
  
  protected Hashtable m_priors;
  
  protected TreeSet m_best;
  
  protected int m_count;
  
  protected Instances m_instances;
  
  public RuleGeneration(ItemSet paramItemSet) {
    this.m_totalTransactions = paramItemSet.m_totalTransactions;
    this.m_counter = paramItemSet.m_counter;
    this.m_items = paramItemSet.m_items;
  }
  
  public static final double binomialDistribution(double paramDouble1, double paramDouble2, double paramDouble3) {
    if (paramDouble3 < 300.0D)
      return Math.pow(2.0D, Utils.log2(Math.pow(paramDouble1, paramDouble2)) + Utils.log2(Math.pow(1.0D - paramDouble1, paramDouble3 - paramDouble2)) + PriorEstimation.logbinomialCoefficient((int)paramDouble3, (int)paramDouble2)); 
    double d1 = paramDouble3 * paramDouble1;
    double d2 = Math.sqrt(paramDouble3 * (1.0D - paramDouble1) * paramDouble1);
    return Statistics.normalProbability((paramDouble2 + 0.5D - d1) / d2 * Math.sqrt(2.0D));
  }
  
  public static final double expectation(double paramDouble, int paramInt, double[] paramArrayOfdouble, Hashtable paramHashtable) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      Double double_ = (Double)paramHashtable.get(new Double(paramArrayOfdouble[b]));
      if (double_ != null && double_.doubleValue() != 0.0D) {
        double d = double_.doubleValue() * binomialDistribution(paramArrayOfdouble[b], paramDouble, paramInt);
        d2 += d;
        d1 += d * paramArrayOfdouble[b];
      } 
    } 
    if (d2 <= 0.0D || Double.isNaN(d2))
      System.out.println("RuleItem denominator: " + d2); 
    if (d1 <= 0.0D || Double.isNaN(d1))
      System.out.println("RuleItem numerator: " + d1); 
    return d1 / d2;
  }
  
  public TreeSet generateRules(int paramInt1, double[] paramArrayOfdouble, Hashtable paramHashtable, double paramDouble, Instances paramInstances, TreeSet paramTreeSet, int paramInt2) {
    boolean bool = false;
    FastVector fastVector1 = new FastVector();
    FastVector fastVector2 = new FastVector();
    boolean bool1 = false;
    RuleItem ruleItem = null;
    this.m_change = false;
    this.m_midPoints = paramArrayOfdouble;
    this.m_priors = paramHashtable;
    this.m_best = paramTreeSet;
    this.m_expectation = paramDouble;
    this.m_count = paramInt2;
    this.m_instances = paramInstances;
    ItemSet itemSet = null;
    itemSet = new ItemSet(this.m_totalTransactions);
    itemSet.m_items = new int[this.m_items.length];
    System.arraycopy(this.m_items, 0, itemSet.m_items, 0, this.m_items.length);
    itemSet.m_counter = this.m_counter;
    while (true) {
      this.m_minRuleCount = 1;
      while (expectation(this.m_minRuleCount, itemSet.m_counter, this.m_midPoints, this.m_priors) <= this.m_expectation) {
        this.m_minRuleCount++;
        if (this.m_minRuleCount > itemSet.m_counter)
          return this.m_best; 
      } 
      bool = false;
      for (byte b = 0; b < paramInstances.numAttributes(); b++) {
        if (b == 0) {
          for (byte b3 = 0; b3 < this.m_items.length; b3++) {
            if (this.m_items[b3] == -1)
              fastVector1 = singleConsequence(paramInstances, b3, fastVector1); 
          } 
          if (itemSet == null || fastVector1.size() == 0)
            return this.m_best; 
        } 
        FastVector fastVector = new FastVector();
        byte b1 = 0;
        do {
          byte b3 = 0;
          while (b3 < fastVector1.size()) {
            RuleItem ruleItem1 = new RuleItem();
            ruleItem = ruleItem1.generateRuleItem(itemSet, (ItemSet)fastVector1.elementAt(b3), paramInstances, this.m_count, this.m_minRuleCount, this.m_midPoints, this.m_priors);
            if (ruleItem != null) {
              fastVector.addElement(ruleItem);
              b3++;
              continue;
            } 
            fastVector1.removeElementAt(b3);
          } 
          if (b1 == b)
            break; 
          fastVector2 = fastVector1;
          fastVector1 = ItemSet.mergeAllItemSets(fastVector2, b1, paramInstances.numInstances());
          Hashtable hashtable = ItemSet.getHashtable(fastVector2, fastVector2.size());
          fastVector1 = ItemSet.pruneItemSets(fastVector1, hashtable);
          b1++;
        } while (fastVector1.size() > 0);
        for (byte b2 = 0; b2 < fastVector.size(); b2++) {
          ruleItem = (RuleItem)fastVector.elementAt(b2);
          this.m_count++;
          if (this.m_best.size() < paramInt1) {
            this.m_change = true;
            bool = removeRedundant(ruleItem);
          } else if (ruleItem.accuracy() > this.m_expectation) {
            this.m_expectation = ((RuleItem)this.m_best.first()).accuracy();
            boolean bool2 = this.m_best.remove(this.m_best.first());
            this.m_change = true;
            bool = removeRedundant(ruleItem);
            this.m_expectation = ((RuleItem)this.m_best.first()).accuracy();
            while (expectation(this.m_minRuleCount, (ruleItem.premise()).m_counter, this.m_midPoints, this.m_priors) < this.m_expectation) {
              this.m_minRuleCount++;
              if (this.m_minRuleCount > (ruleItem.premise()).m_counter)
                break; 
            } 
          } 
        } 
      } 
      if (!bool)
        return this.m_best; 
    } 
  }
  
  public static boolean aSubsumesB(RuleItem paramRuleItem1, RuleItem paramRuleItem2) {
    if (paramRuleItem1.m_accuracy < paramRuleItem2.m_accuracy)
      return false; 
    for (byte b = 0; b < (paramRuleItem1.premise()).m_items.length; b++) {
      if ((paramRuleItem1.premise()).m_items[b] != (paramRuleItem2.premise()).m_items[b] && (((paramRuleItem1.premise()).m_items[b] != -1 && (paramRuleItem2.premise()).m_items[b] != -1) || (paramRuleItem2.premise()).m_items[b] == -1))
        return false; 
      if ((paramRuleItem1.consequence()).m_items[b] != (paramRuleItem2.consequence()).m_items[b] && (((paramRuleItem1.consequence()).m_items[b] != -1 && (paramRuleItem2.consequence()).m_items[b] != -1) || (paramRuleItem1.consequence()).m_items[b] == -1))
        return false; 
    } 
    return true;
  }
  
  public static FastVector singleConsequence(Instances paramInstances, int paramInt, FastVector paramFastVector) {
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      if (b == paramInt)
        for (byte b1 = 0; b1 < paramInstances.attribute(b).numValues(); b1++) {
          ItemSet itemSet = new ItemSet(paramInstances.numInstances());
          itemSet.m_items = new int[paramInstances.numAttributes()];
          for (byte b2 = 0; b2 < paramInstances.numAttributes(); b2++)
            itemSet.m_items[b2] = -1; 
          itemSet.m_items[b] = b1;
          paramFastVector.addElement(itemSet);
        }  
    } 
    return paramFastVector;
  }
  
  public boolean removeRedundant(RuleItem paramRuleItem) {
    boolean bool = false;
    boolean bool1 = false;
    boolean bool2 = false;
    byte b1 = 0;
    Object[] arrayOfObject = this.m_best.toArray();
    for (byte b2 = 0; b2 < arrayOfObject.length; b2++) {
      RuleItem ruleItem = (RuleItem)arrayOfObject[b2];
      bool1 = aSubsumesB(ruleItem, paramRuleItem);
      bool2 = aSubsumesB(paramRuleItem, ruleItem);
      if (bool1) {
        b1 = 1;
        break;
      } 
      if (bool2) {
        boolean bool3 = this.m_best.remove(ruleItem);
        b1 = 2;
        bool = true;
      } 
    } 
    if (b1 == 0 || b1 == 2)
      this.m_best.add(paramRuleItem); 
    return bool;
  }
  
  public int count() {
    return this.m_count;
  }
  
  public boolean change() {
    return this.m_change;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\RuleGeneration.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */