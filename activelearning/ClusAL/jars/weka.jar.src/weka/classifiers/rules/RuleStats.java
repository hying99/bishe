package weka.classifiers.rules;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class RuleStats implements Serializable {
  private Instances m_Data = null;
  
  private FastVector m_Ruleset = null;
  
  private FastVector m_SimpleStats = null;
  
  private FastVector m_Filtered = null;
  
  private double m_Total = -1.0D;
  
  private static double REDUNDANCY_FACTOR = 0.5D;
  
  private double MDL_THEORY_WEIGHT = 1.0D;
  
  private FastVector m_Distributions = null;
  
  public RuleStats() {}
  
  public RuleStats(Instances paramInstances, FastVector paramFastVector) {
    this();
    this.m_Data = paramInstances;
    this.m_Ruleset = paramFastVector;
  }
  
  public void setNumAllConds(double paramDouble) {
    if (paramDouble < 0.0D) {
      this.m_Total = numAllConditions(this.m_Data);
    } else {
      this.m_Total = paramDouble;
    } 
  }
  
  public void setData(Instances paramInstances) {
    this.m_Data = paramInstances;
  }
  
  public Instances getData() {
    return this.m_Data;
  }
  
  public void setRuleset(FastVector paramFastVector) {
    this.m_Ruleset = paramFastVector;
  }
  
  public FastVector getRuleset() {
    return this.m_Ruleset;
  }
  
  public int getRulesetSize() {
    return this.m_Ruleset.size();
  }
  
  public double[] getSimpleStats(int paramInt) {
    return (this.m_SimpleStats != null && paramInt < this.m_SimpleStats.size()) ? (double[])this.m_SimpleStats.elementAt(paramInt) : null;
  }
  
  public Instances[] getFiltered(int paramInt) {
    return (this.m_Filtered != null && paramInt < this.m_Filtered.size()) ? (Instances[])this.m_Filtered.elementAt(paramInt) : null;
  }
  
  public double[] getDistributions(int paramInt) {
    return (this.m_Distributions != null && paramInt < this.m_Distributions.size()) ? (double[])this.m_Distributions.elementAt(paramInt) : null;
  }
  
  public void setMDLTheoryWeight(double paramDouble) {
    this.MDL_THEORY_WEIGHT = paramDouble;
  }
  
  public static double numAllConditions(Instances paramInstances) {
    double d = 0.0D;
    Enumeration enumeration = paramInstances.enumerateAttributes();
    while (enumeration.hasMoreElements()) {
      Attribute attribute = enumeration.nextElement();
      if (attribute.isNominal()) {
        d += attribute.numValues();
        continue;
      } 
      d += 2.0D * paramInstances.numDistinctValues(attribute);
    } 
    return d;
  }
  
  public void countData() {
    if (this.m_Filtered != null || this.m_Ruleset == null || this.m_Data == null)
      return; 
    int i = this.m_Ruleset.size();
    this.m_Filtered = new FastVector(i);
    this.m_SimpleStats = new FastVector(i);
    this.m_Distributions = new FastVector(i);
    Instances instances = new Instances(this.m_Data);
    for (byte b = 0; b < i; b++) {
      double[] arrayOfDouble1 = new double[6];
      double[] arrayOfDouble2 = new double[this.m_Data.classAttribute().numValues()];
      Instances[] arrayOfInstances = computeSimpleStats(b, instances, arrayOfDouble1, arrayOfDouble2);
      this.m_Filtered.addElement(arrayOfInstances);
      this.m_SimpleStats.addElement(arrayOfDouble1);
      this.m_Distributions.addElement(arrayOfDouble2);
      instances = arrayOfInstances[1];
    } 
  }
  
  public void countData(int paramInt, Instances paramInstances, double[][] paramArrayOfdouble) {
    if (this.m_Filtered != null || this.m_Ruleset == null)
      return; 
    int i = this.m_Ruleset.size();
    this.m_Filtered = new FastVector(i);
    this.m_SimpleStats = new FastVector(i);
    Instances[] arrayOfInstances = new Instances[2];
    arrayOfInstances[1] = paramInstances;
    int j;
    for (j = 0; j < paramInt; j++) {
      this.m_SimpleStats.addElement(paramArrayOfdouble[j]);
      if (j + 1 == paramInt) {
        this.m_Filtered.addElement(arrayOfInstances);
      } else {
        this.m_Filtered.addElement(new Object());
      } 
    } 
    for (j = paramInt; j < i; j++) {
      double[] arrayOfDouble = new double[6];
      Instances[] arrayOfInstances1 = computeSimpleStats(j, arrayOfInstances[1], arrayOfDouble, null);
      this.m_Filtered.addElement(arrayOfInstances1);
      this.m_SimpleStats.addElement(arrayOfDouble);
      arrayOfInstances = arrayOfInstances1;
    } 
  }
  
  private Instances[] computeSimpleStats(int paramInt, Instances paramInstances, double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) {
    Rule rule = (Rule)this.m_Ruleset.elementAt(paramInt);
    Instances[] arrayOfInstances = new Instances[2];
    arrayOfInstances[0] = new Instances(paramInstances, paramInstances.numInstances());
    arrayOfInstances[1] = new Instances(paramInstances, paramInstances.numInstances());
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = paramInstances.instance(b);
      double d = instance.weight();
      if (rule.covers(instance)) {
        arrayOfInstances[0].add(instance);
        paramArrayOfdouble1[0] = paramArrayOfdouble1[0] + d;
        if ((int)instance.classValue() == (int)rule.getConsequent()) {
          paramArrayOfdouble1[2] = paramArrayOfdouble1[2] + d;
        } else {
          paramArrayOfdouble1[4] = paramArrayOfdouble1[4] + d;
        } 
        if (paramArrayOfdouble2 != null)
          paramArrayOfdouble2[(int)instance.classValue()] = paramArrayOfdouble2[(int)instance.classValue()] + d; 
      } else {
        arrayOfInstances[1].add(instance);
        paramArrayOfdouble1[1] = paramArrayOfdouble1[1] + d;
        if ((int)instance.classValue() != (int)rule.getConsequent()) {
          paramArrayOfdouble1[3] = paramArrayOfdouble1[3] + d;
        } else {
          paramArrayOfdouble1[5] = paramArrayOfdouble1[5] + d;
        } 
      } 
    } 
    return arrayOfInstances;
  }
  
  public void addAndUpdate(Rule paramRule) {
    if (this.m_Ruleset == null)
      this.m_Ruleset = new FastVector(); 
    this.m_Ruleset.addElement(paramRule);
    Instances instances = (this.m_Filtered == null) ? this.m_Data : ((Instances[])this.m_Filtered.lastElement())[1];
    double[] arrayOfDouble1 = new double[6];
    double[] arrayOfDouble2 = new double[this.m_Data.classAttribute().numValues()];
    Instances[] arrayOfInstances = computeSimpleStats(this.m_Ruleset.size() - 1, instances, arrayOfDouble1, arrayOfDouble2);
    if (this.m_Filtered == null)
      this.m_Filtered = new FastVector(); 
    this.m_Filtered.addElement(arrayOfInstances);
    if (this.m_SimpleStats == null)
      this.m_SimpleStats = new FastVector(); 
    this.m_SimpleStats.addElement(arrayOfDouble1);
    if (this.m_Distributions == null)
      this.m_Distributions = new FastVector(); 
    this.m_Distributions.addElement(arrayOfDouble2);
  }
  
  public static double subsetDL(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d = Utils.gr(paramDouble3, 0.0D) ? (-paramDouble2 * Utils.log2(paramDouble3)) : 0.0D;
    d -= (paramDouble1 - paramDouble2) * Utils.log2(1.0D - paramDouble3);
    return d;
  }
  
  public double theoryDL(int paramInt) {
    double d1 = ((Rule)this.m_Ruleset.elementAt(paramInt)).size();
    if (d1 == 0.0D)
      return 0.0D; 
    double d2 = Utils.log2(d1);
    if (d1 > 1.0D)
      d2 += 2.0D * Utils.log2(d2); 
    d2 += subsetDL(this.m_Total, d1, d1 / this.m_Total);
    return this.MDL_THEORY_WEIGHT * REDUNDANCY_FACTOR * d2;
  }
  
  public static double dataDL(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5) {
    double d2;
    double d3;
    double d1 = Utils.log2(paramDouble2 + paramDouble3 + 1.0D);
    if (Utils.gr(paramDouble2, paramDouble3)) {
      double d = paramDouble1 * (paramDouble4 + paramDouble5);
      d2 = subsetDL(paramDouble2, paramDouble4, d / paramDouble2);
      d3 = Utils.gr(paramDouble3, 0.0D) ? subsetDL(paramDouble3, paramDouble5, paramDouble5 / paramDouble3) : 0.0D;
    } else {
      double d = (1.0D - paramDouble1) * (paramDouble4 + paramDouble5);
      d2 = Utils.gr(paramDouble2, 0.0D) ? subsetDL(paramDouble2, paramDouble4, paramDouble4 / paramDouble2) : 0.0D;
      d3 = subsetDL(paramDouble3, paramDouble5, d / paramDouble3);
    } 
    return d1 + d2 + d3;
  }
  
  public double potential(int paramInt, double paramDouble, double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, boolean paramBoolean) {
    double d1 = paramArrayOfdouble1[0] - paramArrayOfdouble2[0];
    double d2 = paramArrayOfdouble1[1] + paramArrayOfdouble2[0];
    double d3 = paramArrayOfdouble1[4] - paramArrayOfdouble2[4];
    double d4 = paramArrayOfdouble1[5] + paramArrayOfdouble2[2];
    double d5 = dataDL(paramDouble, paramArrayOfdouble1[0], paramArrayOfdouble1[1], paramArrayOfdouble1[4], paramArrayOfdouble1[5]);
    double d6 = theoryDL(paramInt);
    double d7 = dataDL(paramDouble, d1, d2, d3, d4);
    double d8 = d5 + d6 - d7;
    double d9 = paramArrayOfdouble2[4] / paramArrayOfdouble2[0];
    boolean bool = Utils.grOrEq(d9, 0.5D);
    if (!paramBoolean)
      bool = false; 
    if (Utils.grOrEq(d8, 0.0D) || bool) {
      paramArrayOfdouble1[0] = d1;
      paramArrayOfdouble1[1] = d2;
      paramArrayOfdouble1[4] = d3;
      paramArrayOfdouble1[5] = d4;
      return d8;
    } 
    return Double.NaN;
  }
  
  public double minDataDLIfDeleted(int paramInt, double paramDouble, boolean paramBoolean) {
    double[] arrayOfDouble = new double[6];
    int i = this.m_Ruleset.size() - 1 - paramInt;
    FastVector fastVector = new FastVector(i);
    for (byte b = 0; b < paramInt; b++) {
      arrayOfDouble[0] = arrayOfDouble[0] + ((double[])this.m_SimpleStats.elementAt(b))[0];
      arrayOfDouble[2] = arrayOfDouble[2] + ((double[])this.m_SimpleStats.elementAt(b))[2];
      arrayOfDouble[4] = arrayOfDouble[4] + ((double[])this.m_SimpleStats.elementAt(b))[4];
    } 
    Instances instances = (paramInt == 0) ? this.m_Data : ((Instances[])this.m_Filtered.elementAt(paramInt - 1))[1];
    for (int j = paramInt + 1; j < this.m_Ruleset.size(); j++) {
      double[] arrayOfDouble1 = new double[6];
      Instances[] arrayOfInstances = computeSimpleStats(j, instances, arrayOfDouble1, null);
      fastVector.addElement(arrayOfDouble1);
      arrayOfDouble[0] = arrayOfDouble[0] + arrayOfDouble1[0];
      arrayOfDouble[2] = arrayOfDouble[2] + arrayOfDouble1[2];
      arrayOfDouble[4] = arrayOfDouble[4] + arrayOfDouble1[4];
      instances = arrayOfInstances[1];
    } 
    if (i > 0) {
      arrayOfDouble[1] = ((double[])fastVector.lastElement())[1];
      arrayOfDouble[3] = ((double[])fastVector.lastElement())[3];
      arrayOfDouble[5] = ((double[])fastVector.lastElement())[5];
    } else if (paramInt > 0) {
      arrayOfDouble[1] = ((double[])this.m_SimpleStats.elementAt(paramInt - 1))[1];
      arrayOfDouble[3] = ((double[])this.m_SimpleStats.elementAt(paramInt - 1))[3];
      arrayOfDouble[5] = ((double[])this.m_SimpleStats.elementAt(paramInt - 1))[5];
    } else {
      arrayOfDouble[1] = ((double[])this.m_SimpleStats.elementAt(0))[0] + ((double[])this.m_SimpleStats.elementAt(0))[1];
      arrayOfDouble[3] = ((double[])this.m_SimpleStats.elementAt(0))[3] + ((double[])this.m_SimpleStats.elementAt(0))[4];
      arrayOfDouble[5] = ((double[])this.m_SimpleStats.elementAt(0))[2] + ((double[])this.m_SimpleStats.elementAt(0))[5];
    } 
    double d1 = 0.0D;
    for (int k = paramInt + 1; k < this.m_Ruleset.size(); k++) {
      double[] arrayOfDouble1 = (double[])fastVector.elementAt(k - paramInt - 1);
      double d = potential(k, paramDouble, arrayOfDouble, arrayOfDouble1, paramBoolean);
      if (!Double.isNaN(d))
        d1 += d; 
    } 
    double d2 = dataDL(paramDouble, arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[4], arrayOfDouble[5]);
    return d2 - d1;
  }
  
  public double minDataDLIfExists(int paramInt, double paramDouble, boolean paramBoolean) {
    double[] arrayOfDouble = new double[6];
    for (byte b = 0; b < this.m_SimpleStats.size(); b++) {
      arrayOfDouble[0] = arrayOfDouble[0] + ((double[])this.m_SimpleStats.elementAt(b))[0];
      arrayOfDouble[2] = arrayOfDouble[2] + ((double[])this.m_SimpleStats.elementAt(b))[2];
      arrayOfDouble[4] = arrayOfDouble[4] + ((double[])this.m_SimpleStats.elementAt(b))[4];
      if (b == this.m_SimpleStats.size() - 1) {
        arrayOfDouble[1] = ((double[])this.m_SimpleStats.elementAt(b))[1];
        arrayOfDouble[3] = ((double[])this.m_SimpleStats.elementAt(b))[3];
        arrayOfDouble[5] = ((double[])this.m_SimpleStats.elementAt(b))[5];
      } 
    } 
    double d1 = 0.0D;
    for (int i = paramInt + 1; i < this.m_SimpleStats.size(); i++) {
      double[] arrayOfDouble1 = getSimpleStats(i);
      double d = potential(i, paramDouble, arrayOfDouble, arrayOfDouble1, paramBoolean);
      if (!Double.isNaN(d))
        d1 += d; 
    } 
    double d2 = dataDL(paramDouble, arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[4], arrayOfDouble[5]);
    return d2 - d1;
  }
  
  public double relativeDL(int paramInt, double paramDouble, boolean paramBoolean) {
    return minDataDLIfExists(paramInt, paramDouble, paramBoolean) + theoryDL(paramInt) - minDataDLIfDeleted(paramInt, paramDouble, paramBoolean);
  }
  
  public void reduceDL(double paramDouble, boolean paramBoolean) {
    boolean bool = false;
    double[] arrayOfDouble = new double[6];
    for (byte b = 0; b < this.m_SimpleStats.size(); b++) {
      arrayOfDouble[0] = arrayOfDouble[0] + ((double[])this.m_SimpleStats.elementAt(b))[0];
      arrayOfDouble[2] = arrayOfDouble[2] + ((double[])this.m_SimpleStats.elementAt(b))[2];
      arrayOfDouble[4] = arrayOfDouble[4] + ((double[])this.m_SimpleStats.elementAt(b))[4];
      if (b == this.m_SimpleStats.size() - 1) {
        arrayOfDouble[1] = ((double[])this.m_SimpleStats.elementAt(b))[1];
        arrayOfDouble[3] = ((double[])this.m_SimpleStats.elementAt(b))[3];
        arrayOfDouble[5] = ((double[])this.m_SimpleStats.elementAt(b))[5];
      } 
    } 
    double d = 0.0D;
    for (int i = this.m_SimpleStats.size() - 1; i >= 0; i--) {
      double[] arrayOfDouble1 = (double[])this.m_SimpleStats.elementAt(i);
      double d1 = potential(i, paramDouble, arrayOfDouble, arrayOfDouble1, paramBoolean);
      if (!Double.isNaN(d1))
        if (i == this.m_SimpleStats.size() - 1) {
          removeLast();
        } else {
          this.m_Ruleset.removeElementAt(i);
          bool = true;
        }  
    } 
    if (bool) {
      this.m_Filtered = null;
      this.m_SimpleStats = null;
      countData();
    } 
  }
  
  public void removeLast() {
    int i = this.m_Ruleset.size() - 1;
    this.m_Ruleset.removeElementAt(i);
    this.m_Filtered.removeElementAt(i);
    this.m_SimpleStats.removeElementAt(i);
    if (this.m_Distributions != null)
      this.m_Distributions.removeElementAt(i); 
  }
  
  public static Instances rmCoveredBySuccessives(Instances paramInstances, FastVector paramFastVector, int paramInt) {
    Instances instances = new Instances(paramInstances, 0);
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = paramInstances.instance(b);
      boolean bool = false;
      for (int i = paramInt + 1; i < paramFastVector.size(); i++) {
        Rule rule = (Rule)paramFastVector.elementAt(i);
        if (rule.covers(instance)) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        instances.add(instance); 
    } 
    return instances;
  }
  
  public static final Instances stratify(Instances paramInstances, int paramInt, Random paramRandom) {
    if (!paramInstances.classAttribute().isNominal())
      return paramInstances; 
    Instances instances = new Instances(paramInstances, 0);
    Instances[] arrayOfInstances = new Instances[paramInstances.numClasses()];
    byte b;
    for (b = 0; b < arrayOfInstances.length; b++)
      arrayOfInstances[b] = new Instances(paramInstances, 0); 
    for (b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = paramInstances.instance(b);
      arrayOfInstances[(int)instance.classValue()].add(instance);
    } 
    for (b = 0; b < arrayOfInstances.length; b++)
      arrayOfInstances[b].randomize(paramRandom); 
    b = 0;
    while (b < paramInt) {
      int i = b;
      byte b1 = 0;
      while (true) {
        while (i >= arrayOfInstances[b1].numInstances()) {
          i -= arrayOfInstances[b1].numInstances();
          if (++b1 >= arrayOfInstances.length)
            b++; 
        } 
        instances.add(arrayOfInstances[b1].instance(i));
        i += paramInt;
      } 
    } 
    return instances;
  }
  
  public double combinedDL(double paramDouble1, double paramDouble2) {
    double d = 0.0D;
    if (getRulesetSize() > 0) {
      double[] arrayOfDouble = (double[])this.m_SimpleStats.lastElement();
      for (int i = getRulesetSize() - 2; i >= 0; i--) {
        arrayOfDouble[0] = arrayOfDouble[0] + getSimpleStats(i)[0];
        arrayOfDouble[2] = arrayOfDouble[2] + getSimpleStats(i)[2];
        arrayOfDouble[4] = arrayOfDouble[4] + getSimpleStats(i)[4];
      } 
      d += dataDL(paramDouble1, arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[4], arrayOfDouble[5]);
    } else {
      double d1 = 0.0D;
      for (byte b1 = 0; b1 < this.m_Data.numInstances(); b1++) {
        if ((int)this.m_Data.instance(b1).classValue() == (int)paramDouble2)
          d1 += this.m_Data.instance(b1).weight(); 
      } 
      d += dataDL(paramDouble1, 0.0D, this.m_Data.sumOfWeights(), 0.0D, d1);
    } 
    for (byte b = 0; b < getRulesetSize(); b++)
      d += theoryDL(b); 
    return d;
  }
  
  public static final Instances[] partition(Instances paramInstances, int paramInt) {
    Instances[] arrayOfInstances = new Instances[2];
    int i = paramInstances.numInstances() * (paramInt - 1) / paramInt;
    arrayOfInstances[0] = new Instances(paramInstances, 0, i);
    arrayOfInstances[1] = new Instances(paramInstances, i, paramInstances.numInstances() - i);
    return arrayOfInstances;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\RuleStats.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */