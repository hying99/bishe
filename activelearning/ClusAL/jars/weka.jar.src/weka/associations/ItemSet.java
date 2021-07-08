package weka.associations;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class ItemSet implements Serializable {
  protected int[] m_items;
  
  protected int m_counter;
  
  protected int m_totalTransactions;
  
  public ItemSet(int paramInt) {
    this.m_totalTransactions = paramInt;
  }
  
  public ItemSet(int paramInt, int[] paramArrayOfint) {
    this.m_totalTransactions = paramInt;
    this.m_items = paramArrayOfint;
    this.m_counter = 1;
  }
  
  public ItemSet(int[] paramArrayOfint) {
    this.m_items = paramArrayOfint;
    this.m_counter = 0;
  }
  
  public final boolean containedBy(Instance paramInstance) {
    for (byte b = 0; b < paramInstance.numAttributes(); b++) {
      if (this.m_items[b] > -1) {
        if (paramInstance.isMissing(b))
          return false; 
        if (this.m_items[b] != (int)paramInstance.value(b))
          return false; 
      } 
    } 
    return true;
  }
  
  public static FastVector deleteItemSets(FastVector paramFastVector, int paramInt1, int paramInt2) {
    FastVector fastVector = new FastVector(paramFastVector.size());
    for (byte b = 0; b < paramFastVector.size(); b++) {
      ItemSet itemSet = (ItemSet)paramFastVector.elementAt(b);
      if (itemSet.m_counter >= paramInt1 && itemSet.m_counter <= paramInt2)
        fastVector.addElement(itemSet); 
    } 
    return fastVector;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !paramObject.getClass().equals(getClass()))
      return false; 
    if (this.m_items.length != ((ItemSet)paramObject).m_items.length)
      return false; 
    for (byte b = 0; b < this.m_items.length; b++) {
      if (this.m_items[b] != ((ItemSet)paramObject).m_items[b])
        return false; 
    } 
    return true;
  }
  
  public static Hashtable getHashtable(FastVector paramFastVector, int paramInt) {
    Hashtable hashtable = new Hashtable(paramInt);
    for (byte b = 0; b < paramFastVector.size(); b++) {
      ItemSet itemSet = (ItemSet)paramFastVector.elementAt(b);
      hashtable.put(itemSet, new Integer(itemSet.m_counter));
    } 
    return hashtable;
  }
  
  public final int hashCode() {
    long l = 0L;
    for (int i = this.m_items.length - 1; i >= 0; i--)
      l += (i * this.m_items[i]); 
    return (int)l;
  }
  
  public static FastVector mergeAllItemSets(FastVector paramFastVector, int paramInt1, int paramInt2) {
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < paramFastVector.size(); b++) {
      ItemSet itemSet = (ItemSet)paramFastVector.elementAt(b);
      int i;
      label32: for (i = b + 1; i < paramFastVector.size(); i++) {
        ItemSet itemSet2 = (ItemSet)paramFastVector.elementAt(i);
        ItemSet itemSet1 = new ItemSet(paramInt2);
        itemSet1.m_items = new int[itemSet.m_items.length];
        byte b1 = 0;
        byte b2 = 0;
        while (b1 < paramInt1) {
          if (itemSet.m_items[b2] == itemSet2.m_items[b2]) {
            if (itemSet.m_items[b2] != -1)
              b1++; 
            itemSet1.m_items[b2] = itemSet.m_items[b2];
            b2++;
            continue;
          } 
          break label32;
        } 
        while (b2 < itemSet.m_items.length && (itemSet.m_items[b2] == -1 || itemSet2.m_items[b2] == -1)) {
          if (itemSet.m_items[b2] != -1) {
            itemSet1.m_items[b2] = itemSet.m_items[b2];
          } else {
            itemSet1.m_items[b2] = itemSet2.m_items[b2];
          } 
          b2++;
        } 
        if (b2 == itemSet.m_items.length) {
          itemSet1.m_counter = 0;
          fastVector.addElement(itemSet1);
        } 
      } 
    } 
    return fastVector;
  }
  
  public static FastVector pruneItemSets(FastVector paramFastVector, Hashtable paramHashtable) {
    FastVector fastVector = new FastVector(paramFastVector.size());
    for (byte b = 0; b < paramFastVector.size(); b++) {
      ItemSet itemSet = (ItemSet)paramFastVector.elementAt(b);
      byte b1;
      for (b1 = 0; b1 < itemSet.m_items.length; b1++) {
        if (itemSet.m_items[b1] != -1) {
          int i = itemSet.m_items[b1];
          itemSet.m_items[b1] = -1;
          if (paramHashtable.get(itemSet) == null) {
            itemSet.m_items[b1] = i;
            break;
          } 
          itemSet.m_items[b1] = i;
        } 
      } 
      if (b1 == itemSet.m_items.length)
        fastVector.addElement(itemSet); 
    } 
    return fastVector;
  }
  
  public static void pruneRules(FastVector[] paramArrayOfFastVector, double paramDouble) {
    FastVector fastVector1 = new FastVector(paramArrayOfFastVector[0].size());
    FastVector fastVector2 = new FastVector(paramArrayOfFastVector[1].size());
    FastVector fastVector3 = new FastVector(paramArrayOfFastVector[2].size());
    for (byte b = 0; b < paramArrayOfFastVector[0].size(); b++) {
      if (((Double)paramArrayOfFastVector[2].elementAt(b)).doubleValue() >= paramDouble) {
        fastVector1.addElement(paramArrayOfFastVector[0].elementAt(b));
        fastVector2.addElement(paramArrayOfFastVector[1].elementAt(b));
        fastVector3.addElement(paramArrayOfFastVector[2].elementAt(b));
      } 
    } 
    paramArrayOfFastVector[0] = fastVector1;
    paramArrayOfFastVector[1] = fastVector2;
    paramArrayOfFastVector[2] = fastVector3;
  }
  
  public static FastVector singletons(Instances paramInstances) throws Exception {
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      if (paramInstances.attribute(b).isNumeric())
        throw new Exception("Can't handle numeric attributes!"); 
      for (byte b1 = 0; b1 < paramInstances.attribute(b).numValues(); b1++) {
        ItemSet itemSet = new ItemSet(paramInstances.numInstances());
        itemSet.m_items = new int[paramInstances.numAttributes()];
        for (byte b2 = 0; b2 < paramInstances.numAttributes(); b2++)
          itemSet.m_items[b2] = -1; 
        itemSet.m_items[b] = b1;
        fastVector.addElement(itemSet);
      } 
    } 
    return fastVector;
  }
  
  public int support() {
    return this.m_counter;
  }
  
  public String toString(Instances paramInstances) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      if (this.m_items[b] != -1) {
        stringBuffer.append(paramInstances.attribute(b).name() + '=');
        stringBuffer.append(paramInstances.attribute(b).value(this.m_items[b]) + ' ');
      } 
    } 
    stringBuffer.append(this.m_counter);
    return stringBuffer.toString();
  }
  
  public void upDateCounter(Instance paramInstance) {
    if (containedBy(paramInstance))
      this.m_counter++; 
  }
  
  public static void upDateCounters(FastVector paramFastVector, Instances paramInstances) {
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      Enumeration enumeration = paramFastVector.elements();
      while (enumeration.hasMoreElements())
        ((ItemSet)enumeration.nextElement()).upDateCounter(paramInstances.instance(b)); 
    } 
  }
  
  public int counter() {
    return this.m_counter;
  }
  
  public int[] items() {
    return this.m_items;
  }
  
  public int itemAt(int paramInt) {
    return this.m_items[paramInt];
  }
  
  public void setCounter(int paramInt) {
    this.m_counter = paramInt;
  }
  
  public void setItem(int[] paramArrayOfint) {
    this.m_items = paramArrayOfint;
  }
  
  public void setItemAt(int paramInt1, int paramInt2) {
    this.m_items[paramInt2] = paramInt1;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\ItemSet.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */