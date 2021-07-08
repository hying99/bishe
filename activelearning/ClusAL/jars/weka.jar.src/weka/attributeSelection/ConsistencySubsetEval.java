package weka.attributeSelection;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public class ConsistencySubsetEval extends SubsetEvaluator {
  private Instances m_trainInstances;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private int m_numInstances;
  
  private Discretize m_disTransform;
  
  private Hashtable m_table;
  
  public String globalInfo() {
    return "ConsistencySubsetEval :\n\nEvaluates the worth of a subset of attributes by the level of consistency in the class values when the training instances are projected onto the subset of attributes. \n\nConsistency of any subset can never be lower than that of the full set of attributes, hence the usual practice is to use this subset evaluator in conjunction with a Random or Exhaustive search which looks for the smallest subset with consistency equal to that of the full set of attributes.\n";
  }
  
  public ConsistencySubsetEval() {
    resetOptions();
  }
  
  private void resetOptions() {
    this.m_trainInstances = null;
  }
  
  public void buildEvaluator(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    this.m_trainInstances = paramInstances;
    this.m_trainInstances.deleteWithMissingClass();
    this.m_classIndex = this.m_trainInstances.classIndex();
    if (this.m_classIndex < 0)
      throw new Exception("Consistency subset evaluator requires a class attribute!"); 
    if (this.m_trainInstances.classAttribute().isNumeric())
      throw new Exception("Consistency subset evaluator can't handle a numeric class attribute!"); 
    this.m_numAttribs = this.m_trainInstances.numAttributes();
    this.m_numInstances = this.m_trainInstances.numInstances();
    this.m_disTransform = new Discretize();
    this.m_disTransform.setUseBetterEncoding(true);
    this.m_disTransform.setInputFormat(this.m_trainInstances);
    this.m_trainInstances = Filter.useFilter(this.m_trainInstances, (Filter)this.m_disTransform);
  }
  
  public double evaluateSubset(BitSet paramBitSet) throws Exception {
    byte b2 = 0;
    byte b1;
    for (b1 = 0; b1 < this.m_numAttribs; b1++) {
      if (paramBitSet.get(b1))
        b2++; 
    } 
    double[] arrayOfDouble = new double[b2];
    byte b3 = 0;
    int[] arrayOfInt = new int[b2];
    for (b1 = 0; b1 < this.m_numAttribs; b1++) {
      if (paramBitSet.get(b1))
        arrayOfInt[b3++] = b1; 
    } 
    this.m_table = new Hashtable((int)(this.m_numInstances * 1.5D));
    for (b1 = 0; b1 < this.m_numInstances; b1++) {
      Instance instance = this.m_trainInstances.instance(b1);
      for (byte b = 0; b < arrayOfInt.length; b++) {
        if (arrayOfInt[b] == this.m_classIndex)
          throw new Exception("A subset should not contain the class!"); 
        if (instance.isMissing(arrayOfInt[b])) {
          arrayOfDouble[b] = Double.MAX_VALUE;
        } else {
          arrayOfDouble[b] = instance.value(arrayOfInt[b]);
        } 
      } 
      insertIntoTable(instance, arrayOfDouble);
    } 
    return consistencyCount();
  }
  
  private double consistencyCount() {
    Enumeration enumeration = this.m_table.keys();
    double d;
    for (d = 0.0D; enumeration.hasMoreElements(); d -= arrayOfDouble[i]) {
      hashKey hashKey = enumeration.nextElement();
      double[] arrayOfDouble = (double[])this.m_table.get(hashKey);
      d += Utils.sum(arrayOfDouble);
      int i = Utils.maxIndex(arrayOfDouble);
    } 
    d /= this.m_numInstances;
    return 1.0D - d;
  }
  
  private void insertIntoTable(Instance paramInstance, double[] paramArrayOfdouble) throws Exception {
    hashKey hashKey = new hashKey(this, paramArrayOfdouble);
    double[] arrayOfDouble = (double[])this.m_table.get(hashKey);
    if (arrayOfDouble == null) {
      double[] arrayOfDouble1 = new double[this.m_trainInstances.classAttribute().numValues()];
      arrayOfDouble1[(int)paramInstance.classValue()] = paramInstance.weight();
      this.m_table.put(hashKey, arrayOfDouble1);
    } else {
      arrayOfDouble[(int)paramInstance.classValue()] = arrayOfDouble[(int)paramInstance.classValue()] + paramInstance.weight();
      this.m_table.put(hashKey, arrayOfDouble);
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_trainInstances == null) {
      stringBuffer.append("\tConsistency subset evaluator has not been built yet\n");
    } else {
      stringBuffer.append("\tConsistency Subset Evaluator\n");
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(AttributeSelection.SelectAttributes(new ConsistencySubsetEval(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
  
  public class hashKey implements Serializable {
    private double[] attributes;
    
    private boolean[] missing;
    
    private String[] values;
    
    private int key;
    
    private final ConsistencySubsetEval this$0;
    
    public hashKey(ConsistencySubsetEval this$0, Instance param1Instance, int param1Int) throws Exception {
      this.this$0 = this$0;
      int i = param1Instance.classIndex();
      this.key = -999;
      this.attributes = new double[param1Int];
      this.missing = new boolean[param1Int];
      for (byte b = 0; b < param1Int; b++) {
        if (b == i) {
          this.missing[b] = true;
        } else {
          this.missing[b] = param1Instance.isMissing(b);
          if (!param1Instance.isMissing(b))
            this.attributes[b] = param1Instance.value(b); 
        } 
      } 
    }
    
    public String toString(Instances param1Instances, int param1Int) {
      int i = param1Instances.classIndex();
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < this.attributes.length; b++) {
        if (b != i)
          if (this.missing[b]) {
            stringBuffer.append("?");
            for (byte b1 = 0; b1 < param1Int; b1++)
              stringBuffer.append(" "); 
          } else {
            String str = param1Instances.attribute(b).value((int)this.attributes[b]);
            StringBuffer stringBuffer1 = new StringBuffer(str);
            for (byte b1 = 0; b1 < param1Int - str.length() + 1; b1++)
              stringBuffer1.append(" "); 
            stringBuffer.append(stringBuffer1);
          }  
      } 
      return stringBuffer.toString();
    }
    
    public hashKey(ConsistencySubsetEval this$0, double[] param1ArrayOfdouble) {
      this.this$0 = this$0;
      int i = param1ArrayOfdouble.length;
      this.key = -999;
      this.attributes = new double[i];
      this.missing = new boolean[i];
      for (byte b = 0; b < i; b++) {
        if (param1ArrayOfdouble[b] == Double.MAX_VALUE) {
          this.missing[b] = true;
        } else {
          this.missing[b] = false;
          this.attributes[b] = param1ArrayOfdouble[b];
        } 
      } 
    }
    
    public int hashCode() {
      int i = 0;
      if (this.key != -999)
        return this.key; 
      for (byte b = 0; b < this.attributes.length; b++) {
        if (this.missing[b]) {
          i += b * 13;
        } else {
          i = (int)(i + (b * 5) * (this.attributes[b] + 1.0D));
        } 
      } 
      if (this.key == -999)
        this.key = i; 
      return i;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null || !param1Object.getClass().equals(getClass()))
        return false; 
      boolean bool = true;
      if (param1Object instanceof hashKey) {
        hashKey hashKey1 = (hashKey)param1Object;
        for (byte b = 0; b < this.attributes.length; b++) {
          boolean bool1 = hashKey1.missing[b];
          if (this.missing[b] || bool1) {
            if ((this.missing[b] && !bool1) || (!this.missing[b] && bool1)) {
              bool = false;
              break;
            } 
          } else if (this.attributes[b] != hashKey1.attributes[b]) {
            bool = false;
            break;
          } 
        } 
      } else {
        return false;
      } 
      return bool;
    }
    
    public void print_hash_code() {
      System.out.println("Hash val: " + hashCode());
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\ConsistencySubsetEval.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */