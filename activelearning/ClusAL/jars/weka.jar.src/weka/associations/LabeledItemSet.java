package weka.associations;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class LabeledItemSet extends ItemSet implements Serializable {
  protected int m_classLabel;
  
  protected int m_ruleSupCounter;
  
  public LabeledItemSet(int paramInt1, int paramInt2) {
    super(paramInt1);
    this.m_classLabel = paramInt2;
  }
  
  public static FastVector deleteItemSets(FastVector paramFastVector, int paramInt1, int paramInt2) {
    FastVector fastVector = new FastVector(paramFastVector.size());
    for (byte b = 0; b < paramFastVector.size(); b++) {
      LabeledItemSet labeledItemSet = (LabeledItemSet)paramFastVector.elementAt(b);
      if (labeledItemSet.m_ruleSupCounter >= paramInt1 && labeledItemSet.m_ruleSupCounter <= paramInt2)
        fastVector.addElement(labeledItemSet); 
    } 
    return fastVector;
  }
  
  public final boolean equals(Object paramObject) {
    return !equalCondset(paramObject) ? false : (!(this.m_classLabel != ((LabeledItemSet)paramObject).m_classLabel));
  }
  
  public final boolean equalCondset(Object paramObject) {
    if (paramObject == null || !paramObject.getClass().equals(getClass()))
      return false; 
    if (this.m_items.length != (((ItemSet)paramObject).items()).length)
      return false; 
    for (byte b = 0; b < this.m_items.length; b++) {
      if (this.m_items[b] != ((ItemSet)paramObject).itemAt(b))
        return false; 
    } 
    return true;
  }
  
  public static Hashtable getHashtable(FastVector paramFastVector, int paramInt) {
    Hashtable hashtable = new Hashtable(paramInt);
    for (byte b = 0; b < paramFastVector.size(); b++) {
      LabeledItemSet labeledItemSet = (LabeledItemSet)paramFastVector.elementAt(b);
      hashtable.put(labeledItemSet, new Integer(labeledItemSet.m_classLabel));
    } 
    return hashtable;
  }
  
  public static FastVector mergeAllItemSets(FastVector paramFastVector, int paramInt1, int paramInt2) {
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < paramFastVector.size(); b++) {
      LabeledItemSet labeledItemSet = (LabeledItemSet)paramFastVector.elementAt(b);
      int i;
      label40: for (i = b + 1; i < paramFastVector.size(); i++) {
        LabeledItemSet labeledItemSet2;
        for (labeledItemSet2 = (LabeledItemSet)paramFastVector.elementAt(i); labeledItemSet.m_classLabel != labeledItemSet2.m_classLabel; labeledItemSet2 = (LabeledItemSet)paramFastVector.elementAt(i)) {
          if (++i == paramFastVector.size())
            break label40; 
        } 
        LabeledItemSet labeledItemSet1 = new LabeledItemSet(paramInt2, labeledItemSet.m_classLabel);
        labeledItemSet1.m_items = new int[labeledItemSet.m_items.length];
        byte b1 = 0;
        byte b2 = 0;
        while (b1 < paramInt1) {
          if (labeledItemSet.m_items[b2] == labeledItemSet2.m_items[b2]) {
            if (labeledItemSet.m_items[b2] != -1)
              b1++; 
            labeledItemSet1.m_items[b2] = labeledItemSet.m_items[b2];
            b2++;
            continue;
          } 
          break label40;
        } 
        while (b2 < labeledItemSet.m_items.length && (labeledItemSet.m_items[b2] == -1 || labeledItemSet2.m_items[b2] == -1)) {
          if (labeledItemSet.m_items[b2] != -1) {
            labeledItemSet1.m_items[b2] = labeledItemSet.m_items[b2];
          } else {
            labeledItemSet1.m_items[b2] = labeledItemSet2.m_items[b2];
          } 
          b2++;
        } 
        if (b2 == labeledItemSet.m_items.length) {
          labeledItemSet1.m_ruleSupCounter = 0;
          labeledItemSet1.m_counter = 0;
          fastVector.addElement(labeledItemSet1);
        } 
      } 
    } 
    return fastVector;
  }
  
  public static Instances divide(Instances paramInstances, boolean paramBoolean) throws Exception {
    Instances instances = new Instances(paramInstances);
    if (paramInstances.classIndex() < 0)
      throw new Exception("For class association rule mining a class attribute has to be specified."); 
    if (paramBoolean) {
      for (byte b = 0; b < instances.numAttributes(); b++) {
        if (b != instances.classIndex()) {
          instances.deleteAttributeAt(b);
          b--;
        } 
      } 
      return instances;
    } 
    instances.setClassIndex(-1);
    instances.deleteAttributeAt(paramInstances.classIndex());
    return instances;
  }
  
  public static FastVector singletons(Instances paramInstances1, Instances paramInstances2) throws Exception {
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < paramInstances1.numAttributes(); b++) {
      if (paramInstances1.attribute(b).isNumeric())
        throw new Exception("Can't handle numeric attributes!"); 
      for (byte b1 = 0; b1 < paramInstances1.attribute(b).numValues(); b1++) {
        for (byte b2 = 0; b2 < paramInstances2.attribute(0).numValues(); b2++) {
          LabeledItemSet labeledItemSet = new LabeledItemSet(paramInstances1.numInstances(), b2);
          labeledItemSet.m_items = new int[paramInstances1.numAttributes()];
          for (byte b3 = 0; b3 < paramInstances1.numAttributes(); b3++)
            labeledItemSet.m_items[b3] = -1; 
          labeledItemSet.m_items[b] = b1;
          fastVector.addElement(labeledItemSet);
        } 
      } 
    } 
    return fastVector;
  }
  
  public static FastVector pruneItemSets(FastVector paramFastVector, Hashtable paramHashtable) {
    FastVector fastVector = new FastVector(paramFastVector.size());
    for (byte b = 0; b < paramFastVector.size(); b++) {
      LabeledItemSet labeledItemSet = (LabeledItemSet)paramFastVector.elementAt(b);
      byte b1;
      for (b1 = 0; b1 < labeledItemSet.m_items.length; b1++) {
        if (labeledItemSet.m_items[b1] != -1) {
          int i = labeledItemSet.m_items[b1];
          labeledItemSet.m_items[b1] = -1;
          if (paramHashtable.get(labeledItemSet) != null && labeledItemSet.m_classLabel == ((Integer)paramHashtable.get(labeledItemSet)).intValue()) {
            labeledItemSet.m_items[b1] = i;
          } else {
            labeledItemSet.m_items[b1] = i;
            break;
          } 
        } 
      } 
      if (b1 == labeledItemSet.m_items.length)
        fastVector.addElement(labeledItemSet); 
    } 
    return fastVector;
  }
  
  public final int support() {
    return this.m_ruleSupCounter;
  }
  
  public final void upDateCounter(Instance paramInstance1, Instance paramInstance2) {
    if (containedBy(paramInstance1)) {
      this.m_counter++;
      if (this.m_classLabel == paramInstance2.value(0))
        this.m_ruleSupCounter++; 
    } 
  }
  
  public static void upDateCounters(FastVector paramFastVector, Instances paramInstances1, Instances paramInstances2) {
    for (byte b = 0; b < paramInstances1.numInstances(); b++) {
      Enumeration enumeration = paramFastVector.elements();
      while (enumeration.hasMoreElements())
        ((LabeledItemSet)enumeration.nextElement()).upDateCounter(paramInstances1.instance(b), paramInstances2.instance(b)); 
    } 
  }
  
  public final FastVector[] generateRules(double paramDouble, boolean paramBoolean) {
    FastVector fastVector1 = new FastVector();
    FastVector fastVector2 = new FastVector();
    FastVector fastVector3 = new FastVector();
    FastVector[] arrayOfFastVector = new FastVector[3];
    ItemSet itemSet1 = new ItemSet(this.m_totalTransactions);
    ItemSet itemSet2 = new ItemSet(this.m_totalTransactions);
    int[] arrayOfInt1 = new int[this.m_items.length];
    int[] arrayOfInt2 = new int[1];
    System.arraycopy(this.m_items, 0, arrayOfInt1, 0, this.m_items.length);
    itemSet2.setItem(arrayOfInt2);
    itemSet1.setItem(arrayOfInt1);
    itemSet2.setItemAt(this.m_classLabel, 0);
    itemSet2.setCounter(this.m_ruleSupCounter);
    itemSet1.setCounter(this.m_counter);
    fastVector1.addElement(itemSet1);
    fastVector2.addElement(itemSet2);
    fastVector3.addElement(new Double(this.m_ruleSupCounter / this.m_counter));
    arrayOfFastVector[0] = fastVector1;
    arrayOfFastVector[1] = fastVector2;
    arrayOfFastVector[2] = fastVector3;
    if (!paramBoolean)
      pruneRules(arrayOfFastVector, paramDouble); 
    return arrayOfFastVector;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\LabeledItemSet.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */