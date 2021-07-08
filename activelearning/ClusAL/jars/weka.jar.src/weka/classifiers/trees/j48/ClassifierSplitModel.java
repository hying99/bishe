package weka.classifiers.trees.j48;

import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public abstract class ClassifierSplitModel implements Cloneable, Serializable {
  protected Distribution m_distribution;
  
  protected int m_numSubsets;
  
  public Object clone() {
    Object object = null;
    try {
      object = super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
    return object;
  }
  
  public abstract void buildClassifier(Instances paramInstances) throws Exception;
  
  public final boolean checkModel() {
    return (this.m_numSubsets > 0);
  }
  
  public final double classifyInstance(Instance paramInstance) throws Exception {
    int i = whichSubset(paramInstance);
    return (i > -1) ? this.m_distribution.maxClass(i) : this.m_distribution.maxClass();
  }
  
  public double classProb(int paramInt1, Instance paramInstance, int paramInt2) throws Exception {
    if (paramInt2 > -1)
      return this.m_distribution.prob(paramInt1, paramInt2); 
    double[] arrayOfDouble = weights(paramInstance);
    if (arrayOfDouble == null)
      return this.m_distribution.prob(paramInt1); 
    double d = 0.0D;
    for (byte b = 0; b < arrayOfDouble.length; b++)
      d += arrayOfDouble[b] * this.m_distribution.prob(paramInt1, b); 
    return d;
  }
  
  public double classProbLaplace(int paramInt1, Instance paramInstance, int paramInt2) throws Exception {
    if (paramInt2 > -1)
      return this.m_distribution.laplaceProb(paramInt1, paramInt2); 
    double[] arrayOfDouble = weights(paramInstance);
    if (arrayOfDouble == null)
      return this.m_distribution.laplaceProb(paramInt1); 
    double d = 0.0D;
    for (byte b = 0; b < arrayOfDouble.length; b++)
      d += arrayOfDouble[b] * this.m_distribution.laplaceProb(paramInt1, b); 
    return d;
  }
  
  public double codingCost() {
    return 0.0D;
  }
  
  public final Distribution distribution() {
    return this.m_distribution;
  }
  
  public abstract String leftSide(Instances paramInstances);
  
  public abstract String rightSide(int paramInt, Instances paramInstances);
  
  public final String dumpLabel(int paramInt, Instances paramInstances) throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(paramInstances.classAttribute().value(this.m_distribution.maxClass(paramInt)));
    stringBuffer.append(" (" + Utils.roundDouble(this.m_distribution.perBag(paramInt), 2));
    if (Utils.gr(this.m_distribution.numIncorrect(paramInt), 0.0D))
      stringBuffer.append("/" + Utils.roundDouble(this.m_distribution.numIncorrect(paramInt), 2)); 
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  public final String sourceClass(int paramInt, Instances paramInstances) throws Exception {
    System.err.println("sourceClass");
    return (new StringBuffer(this.m_distribution.maxClass(paramInt))).toString();
  }
  
  public abstract String sourceExpression(int paramInt, Instances paramInstances);
  
  public final String dumpModel(Instances paramInstances) throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.m_numSubsets; b++) {
      stringBuffer.append(leftSide(paramInstances) + rightSide(b, paramInstances) + ": ");
      stringBuffer.append(dumpLabel(b, paramInstances) + "\n");
    } 
    return stringBuffer.toString();
  }
  
  public final int numSubsets() {
    return this.m_numSubsets;
  }
  
  public void resetDistribution(Instances paramInstances) throws Exception {
    this.m_distribution = new Distribution(paramInstances, this);
  }
  
  public final Instances[] split(Instances paramInstances) throws Exception {
    Instances[] arrayOfInstances = new Instances[this.m_numSubsets];
    byte b2;
    for (b2 = 0; b2 < this.m_numSubsets; b2++)
      arrayOfInstances[b2] = new Instances(paramInstances, paramInstances.numInstances()); 
    for (byte b1 = 0; b1 < paramInstances.numInstances(); b1++) {
      Instance instance = paramInstances.instance(b1);
      double[] arrayOfDouble = weights(instance);
      int i = whichSubset(instance);
      if (i > -1) {
        arrayOfInstances[i].add(instance);
      } else {
        for (b2 = 0; b2 < this.m_numSubsets; b2++) {
          if (Utils.gr(arrayOfDouble[b2], 0.0D)) {
            double d = arrayOfDouble[b2] * instance.weight();
            arrayOfInstances[b2].add(instance);
            arrayOfInstances[b2].lastInstance().setWeight(d);
          } 
        } 
      } 
    } 
    for (b2 = 0; b2 < this.m_numSubsets; b2++)
      arrayOfInstances[b2].compactify(); 
    return arrayOfInstances;
  }
  
  public abstract double[] weights(Instance paramInstance);
  
  public abstract int whichSubset(Instance paramInstance) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\ClassifierSplitModel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */