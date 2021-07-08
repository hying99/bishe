package weka.associations;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import weka.core.ContingencyTables;
import weka.core.FastVector;
import weka.core.Instances;

public class AprioriItemSet extends ItemSet implements Serializable {
  public AprioriItemSet(int paramInt) {
    super(paramInt);
  }
  
  public static double confidenceForRule(AprioriItemSet paramAprioriItemSet1, AprioriItemSet paramAprioriItemSet2) {
    return paramAprioriItemSet2.m_counter / paramAprioriItemSet1.m_counter;
  }
  
  public double liftForRule(AprioriItemSet paramAprioriItemSet1, AprioriItemSet paramAprioriItemSet2, int paramInt) {
    double d = confidenceForRule(paramAprioriItemSet1, paramAprioriItemSet2);
    return d / paramInt / this.m_totalTransactions;
  }
  
  public double leverageForRule(AprioriItemSet paramAprioriItemSet1, AprioriItemSet paramAprioriItemSet2, int paramInt1, int paramInt2) {
    double d1 = paramAprioriItemSet2.m_counter / this.m_totalTransactions;
    double d2 = paramInt1 / this.m_totalTransactions * paramInt2 / this.m_totalTransactions;
    return d1 - d2;
  }
  
  public double convictionForRule(AprioriItemSet paramAprioriItemSet1, AprioriItemSet paramAprioriItemSet2, int paramInt1, int paramInt2) {
    double d1 = paramInt1 * (this.m_totalTransactions - paramInt2) / this.m_totalTransactions;
    double d2 = (paramInt1 - paramAprioriItemSet2.m_counter + 1);
    if (d1 < 0.0D || d2 < 0.0D) {
      System.err.println("*** " + d1 + " " + d2);
      System.err.println("premis count: " + paramInt1 + " consequence count " + paramInt2 + " total trans " + this.m_totalTransactions);
    } 
    return d1 / d2;
  }
  
  public FastVector[] generateRules(double paramDouble, FastVector paramFastVector, int paramInt) {
    FastVector fastVector1 = new FastVector();
    FastVector fastVector2 = new FastVector();
    FastVector fastVector3 = new FastVector();
    FastVector[] arrayOfFastVector1 = new FastVector[3];
    Hashtable hashtable = (Hashtable)paramFastVector.elementAt(paramInt - 2);
    byte b;
    for (b = 0; b < this.m_items.length; b++) {
      if (this.m_items[b] != -1) {
        AprioriItemSet aprioriItemSet1 = new AprioriItemSet(this.m_totalTransactions);
        AprioriItemSet aprioriItemSet2 = new AprioriItemSet(this.m_totalTransactions);
        aprioriItemSet1.m_items = new int[this.m_items.length];
        aprioriItemSet2.m_items = new int[this.m_items.length];
        aprioriItemSet2.m_counter = this.m_counter;
        for (byte b1 = 0; b1 < this.m_items.length; b1++)
          aprioriItemSet2.m_items[b1] = -1; 
        System.arraycopy(this.m_items, 0, aprioriItemSet1.m_items, 0, this.m_items.length);
        aprioriItemSet1.m_items[b] = -1;
        aprioriItemSet2.m_items[b] = this.m_items[b];
        aprioriItemSet1.m_counter = ((Integer)hashtable.get(aprioriItemSet1)).intValue();
        fastVector1.addElement(aprioriItemSet1);
        fastVector2.addElement(aprioriItemSet2);
        fastVector3.addElement(new Double(confidenceForRule(aprioriItemSet1, aprioriItemSet2)));
      } 
    } 
    arrayOfFastVector1[0] = fastVector1;
    arrayOfFastVector1[1] = fastVector2;
    arrayOfFastVector1[2] = fastVector3;
    pruneRules(arrayOfFastVector1, paramDouble);
    FastVector[] arrayOfFastVector2 = moreComplexRules(arrayOfFastVector1, paramInt, 1, paramDouble, paramFastVector);
    if (arrayOfFastVector2 != null)
      for (b = 0; b < arrayOfFastVector2[0].size(); b++) {
        arrayOfFastVector1[0].addElement(arrayOfFastVector2[0].elementAt(b));
        arrayOfFastVector1[1].addElement(arrayOfFastVector2[1].elementAt(b));
        arrayOfFastVector1[2].addElement(arrayOfFastVector2[2].elementAt(b));
      }  
    return arrayOfFastVector1;
  }
  
  public final FastVector[] generateRulesBruteForce(double paramDouble1, int paramInt1, FastVector paramFastVector, int paramInt2, int paramInt3, double paramDouble2) throws Exception {
    FastVector fastVector1 = new FastVector();
    FastVector fastVector2 = new FastVector();
    FastVector fastVector3 = new FastVector();
    FastVector fastVector4 = new FastVector();
    FastVector fastVector5 = new FastVector();
    FastVector fastVector6 = new FastVector();
    FastVector[] arrayOfFastVector = new FastVector[6];
    double[][] arrayOfDouble = new double[2][2];
    int i = (int)Math.pow(2.0D, paramInt2);
    for (byte b = 1; b < i; b++) {
      byte b1 = 0;
      int j;
      for (j = b; j > 0; j /= 2) {
        if (j % 2 == 1)
          b1++; 
      } 
      if (b1 < paramInt2) {
        Hashtable hashtable1 = (Hashtable)paramFastVector.elementAt(b1 - 1);
        Hashtable hashtable2 = (Hashtable)paramFastVector.elementAt(paramInt2 - b1 - 1);
        AprioriItemSet aprioriItemSet1 = new AprioriItemSet(this.m_totalTransactions);
        AprioriItemSet aprioriItemSet2 = new AprioriItemSet(this.m_totalTransactions);
        aprioriItemSet1.m_items = new int[this.m_items.length];
        aprioriItemSet2.m_items = new int[this.m_items.length];
        aprioriItemSet2.m_counter = this.m_counter;
        j = b;
        for (byte b2 = 0; b2 < this.m_items.length; b2++) {
          if (this.m_items[b2] != -1) {
            if (j % 2 == 1) {
              aprioriItemSet1.m_items[b2] = this.m_items[b2];
              aprioriItemSet2.m_items[b2] = -1;
            } else {
              aprioriItemSet1.m_items[b2] = -1;
              aprioriItemSet2.m_items[b2] = this.m_items[b2];
            } 
            j /= 2;
          } else {
            aprioriItemSet1.m_items[b2] = -1;
            aprioriItemSet2.m_items[b2] = -1;
          } 
        } 
        aprioriItemSet1.m_counter = ((Integer)hashtable1.get(aprioriItemSet1)).intValue();
        int k = ((Integer)hashtable2.get(aprioriItemSet2)).intValue();
        if (paramInt1 == 0) {
          arrayOfDouble[0][0] = aprioriItemSet2.m_counter;
          arrayOfDouble[0][1] = (aprioriItemSet1.m_counter - aprioriItemSet2.m_counter);
          arrayOfDouble[1][0] = (k - aprioriItemSet2.m_counter);
          arrayOfDouble[1][1] = (paramInt3 - aprioriItemSet1.m_counter - k + aprioriItemSet2.m_counter);
          double d2 = ContingencyTables.chiSquared(arrayOfDouble, false);
          double d1 = confidenceForRule(aprioriItemSet1, aprioriItemSet2);
          if (d1 >= paramDouble1 && d2 <= paramDouble2) {
            fastVector1.addElement(aprioriItemSet1);
            fastVector2.addElement(aprioriItemSet2);
            fastVector3.addElement(new Double(d1));
            fastVector4.addElement(new Double(liftForRule(aprioriItemSet1, aprioriItemSet2, k)));
            fastVector5.addElement(new Double(leverageForRule(aprioriItemSet1, aprioriItemSet2, aprioriItemSet1.m_counter, k)));
            fastVector6.addElement(new Double(convictionForRule(aprioriItemSet1, aprioriItemSet2, aprioriItemSet1.m_counter, k)));
          } 
        } else {
          double d1;
          double d2 = confidenceForRule(aprioriItemSet1, aprioriItemSet2);
          double d3 = liftForRule(aprioriItemSet1, aprioriItemSet2, k);
          double d4 = leverageForRule(aprioriItemSet1, aprioriItemSet2, aprioriItemSet1.m_counter, k);
          double d5 = convictionForRule(aprioriItemSet1, aprioriItemSet2, aprioriItemSet1.m_counter, k);
          switch (paramInt1) {
            case 1:
              d1 = d3;
              break;
            case 2:
              d1 = d4;
              break;
            case 3:
              d1 = d5;
              break;
            default:
              throw new Exception("ItemSet: Unknown metric type!");
          } 
          if (d1 >= paramDouble1) {
            fastVector1.addElement(aprioriItemSet1);
            fastVector2.addElement(aprioriItemSet2);
            fastVector3.addElement(new Double(d2));
            fastVector4.addElement(new Double(d3));
            fastVector5.addElement(new Double(d4));
            fastVector6.addElement(new Double(d5));
          } 
        } 
      } 
    } 
    arrayOfFastVector[0] = fastVector1;
    arrayOfFastVector[1] = fastVector2;
    arrayOfFastVector[2] = fastVector3;
    arrayOfFastVector[3] = fastVector4;
    arrayOfFastVector[4] = fastVector5;
    arrayOfFastVector[5] = fastVector6;
    return arrayOfFastVector;
  }
  
  public final AprioriItemSet subtract(AprioriItemSet paramAprioriItemSet) {
    AprioriItemSet aprioriItemSet = new AprioriItemSet(this.m_totalTransactions);
    aprioriItemSet.m_items = new int[this.m_items.length];
    for (byte b = 0; b < this.m_items.length; b++) {
      if (paramAprioriItemSet.m_items[b] == -1) {
        aprioriItemSet.m_items[b] = this.m_items[b];
      } else {
        aprioriItemSet.m_items[b] = -1;
      } 
    } 
    aprioriItemSet.m_counter = 0;
    return aprioriItemSet;
  }
  
  private final FastVector[] moreComplexRules(FastVector[] paramArrayOfFastVector, int paramInt1, int paramInt2, double paramDouble, FastVector paramFastVector) {
    FastVector fastVector1 = new FastVector();
    FastVector fastVector2 = new FastVector();
    if (paramInt1 > paramInt2 + 1) {
      Hashtable hashtable = (Hashtable)paramFastVector.elementAt(paramInt1 - paramInt2 - 2);
      FastVector fastVector = mergeAllItemSets(paramArrayOfFastVector[1], paramInt2 - 1, this.m_totalTransactions);
      Enumeration enumeration = fastVector.elements();
      while (enumeration.hasMoreElements()) {
        AprioriItemSet aprioriItemSet2 = enumeration.nextElement();
        aprioriItemSet2.m_counter = this.m_counter;
        AprioriItemSet aprioriItemSet1 = subtract(aprioriItemSet2);
        aprioriItemSet1.m_counter = ((Integer)hashtable.get(aprioriItemSet1)).intValue();
        fastVector1.addElement(aprioriItemSet1);
        fastVector2.addElement(new Double(confidenceForRule(aprioriItemSet1, aprioriItemSet2)));
      } 
      FastVector[] arrayOfFastVector1 = new FastVector[3];
      arrayOfFastVector1[0] = fastVector1;
      arrayOfFastVector1[1] = fastVector;
      arrayOfFastVector1[2] = fastVector2;
      pruneRules(arrayOfFastVector1, paramDouble);
      FastVector[] arrayOfFastVector2 = moreComplexRules(arrayOfFastVector1, paramInt1, paramInt2 + 1, paramDouble, paramFastVector);
      if (arrayOfFastVector2 != null)
        for (byte b = 0; b < arrayOfFastVector2[0].size(); b++) {
          arrayOfFastVector1[0].addElement(arrayOfFastVector2[0].elementAt(b));
          arrayOfFastVector1[1].addElement(arrayOfFastVector2[1].elementAt(b));
          arrayOfFastVector1[2].addElement(arrayOfFastVector2[2].elementAt(b));
        }  
      return arrayOfFastVector1;
    } 
    return null;
  }
  
  public final String toString(Instances paramInstances) {
    return super.toString(paramInstances);
  }
  
  public static FastVector singletons(Instances paramInstances) throws Exception {
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      if (paramInstances.attribute(b).isNumeric())
        throw new Exception("Can't handle numeric attributes!"); 
      for (byte b1 = 0; b1 < paramInstances.attribute(b).numValues(); b1++) {
        AprioriItemSet aprioriItemSet = new AprioriItemSet(paramInstances.numInstances());
        aprioriItemSet.m_items = new int[paramInstances.numAttributes()];
        for (byte b2 = 0; b2 < paramInstances.numAttributes(); b2++)
          aprioriItemSet.m_items[b2] = -1; 
        aprioriItemSet.m_items[b] = b1;
        fastVector.addElement(aprioriItemSet);
      } 
    } 
    return fastVector;
  }
  
  public static FastVector mergeAllItemSets(FastVector paramFastVector, int paramInt1, int paramInt2) {
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < paramFastVector.size(); b++) {
      ItemSet itemSet = (ItemSet)paramFastVector.elementAt(b);
      int i;
      label32: for (i = b + 1; i < paramFastVector.size(); i++) {
        ItemSet itemSet1 = (ItemSet)paramFastVector.elementAt(i);
        AprioriItemSet aprioriItemSet = new AprioriItemSet(paramInt2);
        aprioriItemSet.m_items = new int[itemSet.m_items.length];
        byte b1 = 0;
        byte b2 = 0;
        while (b1 < paramInt1) {
          if (itemSet.m_items[b2] == itemSet1.m_items[b2]) {
            if (itemSet.m_items[b2] != -1)
              b1++; 
            aprioriItemSet.m_items[b2] = itemSet.m_items[b2];
            b2++;
            continue;
          } 
          break label32;
        } 
        while (b2 < itemSet.m_items.length && (itemSet.m_items[b2] == -1 || itemSet1.m_items[b2] == -1)) {
          if (itemSet.m_items[b2] != -1) {
            aprioriItemSet.m_items[b2] = itemSet.m_items[b2];
          } else {
            aprioriItemSet.m_items[b2] = itemSet1.m_items[b2];
          } 
          b2++;
        } 
        if (b2 == itemSet.m_items.length) {
          aprioriItemSet.m_counter = 0;
          fastVector.addElement(aprioriItemSet);
        } 
      } 
    } 
    return fastVector;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\AprioriItemSet.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */