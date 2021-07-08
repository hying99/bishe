package weka.associations;

import java.io.Serializable;
import java.util.Hashtable;
import weka.core.Instances;

public class RuleItem implements Comparable, Serializable {
  protected ItemSet m_premise;
  
  protected ItemSet m_consequence;
  
  protected double m_accuracy;
  
  protected int m_genTime;
  
  public RuleItem() {}
  
  public RuleItem(RuleItem paramRuleItem) {
    this.m_premise = paramRuleItem.m_premise;
    this.m_consequence = paramRuleItem.m_consequence;
    this.m_accuracy = paramRuleItem.m_accuracy;
    this.m_genTime = paramRuleItem.m_genTime;
  }
  
  public RuleItem(ItemSet paramItemSet1, ItemSet paramItemSet2, int paramInt1, int paramInt2, double[] paramArrayOfdouble, Hashtable paramHashtable) {
    this.m_premise = paramItemSet1;
    this.m_consequence = paramItemSet2;
    this.m_accuracy = RuleGeneration.expectation(paramInt2, this.m_premise.m_counter, paramArrayOfdouble, paramHashtable);
    if (Double.isNaN(this.m_accuracy) || this.m_accuracy < 0.0D)
      this.m_accuracy = Double.MIN_VALUE; 
    this.m_consequence.m_counter = paramInt2;
    this.m_genTime = paramInt1;
  }
  
  public RuleItem generateRuleItem(ItemSet paramItemSet1, ItemSet paramItemSet2, Instances paramInstances, int paramInt1, int paramInt2, double[] paramArrayOfdouble, Hashtable paramHashtable) {
    ItemSet itemSet = new ItemSet(paramInstances.numInstances());
    itemSet.m_items = new int[paramItemSet2.m_items.length];
    System.arraycopy(paramItemSet1.m_items, 0, itemSet.m_items, 0, paramItemSet1.m_items.length);
    int i;
    for (i = 0; i < paramItemSet2.m_items.length; i++) {
      if (paramItemSet2.m_items[i] != -1)
        itemSet.m_items[i] = paramItemSet2.m_items[i]; 
    } 
    for (i = 0; i < paramInstances.numInstances(); i++)
      itemSet.upDateCounter(paramInstances.instance(i)); 
    i = itemSet.support();
    return (i > paramInt2) ? new RuleItem(paramItemSet1, paramItemSet2, paramInt1, i, paramArrayOfdouble, paramHashtable) : null;
  }
  
  public int compareTo(Object paramObject) {
    if (this.m_accuracy == ((RuleItem)paramObject).m_accuracy) {
      if (this.m_genTime == ((RuleItem)paramObject).m_genTime)
        return 0; 
      if (this.m_genTime > ((RuleItem)paramObject).m_genTime)
        return -1; 
      if (this.m_genTime < ((RuleItem)paramObject).m_genTime)
        return 1; 
    } 
    return (this.m_accuracy < ((RuleItem)paramObject).m_accuracy) ? -1 : 1;
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject == null) ? false : ((this.m_premise.equals(((RuleItem)paramObject).m_premise) && this.m_consequence.equals(((RuleItem)paramObject).m_consequence)));
  }
  
  public double accuracy() {
    return this.m_accuracy;
  }
  
  public ItemSet premise() {
    return this.m_premise;
  }
  
  public ItemSet consequence() {
    return this.m_consequence;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\RuleItem.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */