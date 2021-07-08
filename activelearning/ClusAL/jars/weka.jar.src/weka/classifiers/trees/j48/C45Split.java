package weka.classifiers.trees.j48;

import java.util.Enumeration;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class C45Split extends ClassifierSplitModel {
  private int m_complexityIndex;
  
  private int m_attIndex;
  
  private int m_minNoObj;
  
  private double m_splitPoint;
  
  private double m_infoGain;
  
  private double m_gainRatio;
  
  private double m_sumOfWeights;
  
  private int m_index;
  
  private static InfoGainSplitCrit infoGainCrit = new InfoGainSplitCrit();
  
  private static GainRatioSplitCrit gainRatioCrit = new GainRatioSplitCrit();
  
  public C45Split(int paramInt1, int paramInt2, double paramDouble) {
    this.m_attIndex = paramInt1;
    this.m_minNoObj = paramInt2;
    this.m_sumOfWeights = paramDouble;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_numSubsets = 0;
    this.m_splitPoint = Double.MAX_VALUE;
    this.m_infoGain = 0.0D;
    this.m_gainRatio = 0.0D;
    if (paramInstances.attribute(this.m_attIndex).isNominal()) {
      this.m_complexityIndex = paramInstances.attribute(this.m_attIndex).numValues();
      this.m_index = this.m_complexityIndex;
      handleEnumeratedAttribute(paramInstances);
    } else {
      this.m_complexityIndex = 2;
      this.m_index = 0;
      paramInstances.sort(paramInstances.attribute(this.m_attIndex));
      handleNumericAttribute(paramInstances);
    } 
  }
  
  public final int attIndex() {
    return this.m_attIndex;
  }
  
  public final double classProb(int paramInt1, Instance paramInstance, int paramInt2) throws Exception {
    if (paramInt2 <= -1) {
      double[] arrayOfDouble = weights(paramInstance);
      if (arrayOfDouble == null)
        return this.m_distribution.prob(paramInt1); 
      double d = 0.0D;
      for (byte b = 0; b < arrayOfDouble.length; b++)
        d += arrayOfDouble[b] * this.m_distribution.prob(paramInt1, b); 
      return d;
    } 
    return Utils.gr(this.m_distribution.perBag(paramInt2), 0.0D) ? this.m_distribution.prob(paramInt1, paramInt2) : this.m_distribution.prob(paramInt1);
  }
  
  public final double codingCost() {
    return Utils.log2(this.m_index);
  }
  
  public final double gainRatio() {
    return this.m_gainRatio;
  }
  
  private void handleEnumeratedAttribute(Instances paramInstances) throws Exception {
    this.m_distribution = new Distribution(this.m_complexityIndex, paramInstances.numClasses());
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if (!instance.isMissing(this.m_attIndex))
        this.m_distribution.add((int)instance.value(this.m_attIndex), instance); 
    } 
    if (this.m_distribution.check(this.m_minNoObj)) {
      this.m_numSubsets = this.m_complexityIndex;
      this.m_infoGain = infoGainCrit.splitCritValue(this.m_distribution, this.m_sumOfWeights);
      this.m_gainRatio = gainRatioCrit.splitCritValue(this.m_distribution, this.m_sumOfWeights, this.m_infoGain);
    } 
  }
  
  private void handleNumericAttribute(Instances paramInstances) throws Exception {
    byte b2 = 1;
    byte b3 = 0;
    int i = -1;
    this.m_distribution = new Distribution(2, paramInstances.numClasses());
    Enumeration enumeration = paramInstances.enumerateInstances();
    byte b4;
    for (b4 = 0; enumeration.hasMoreElements(); b4++) {
      Instance instance = enumeration.nextElement();
      if (instance.isMissing(this.m_attIndex))
        break; 
      this.m_distribution.add(1, instance);
    } 
    byte b1 = b4;
    double d2 = 0.1D * this.m_distribution.total() / paramInstances.numClasses();
    if (Utils.smOrEq(d2, this.m_minNoObj)) {
      d2 = this.m_minNoObj;
    } else if (Utils.gr(d2, 25.0D)) {
      d2 = 25.0D;
    } 
    if (Utils.sm(b1, 2.0D * d2))
      return; 
    double d1 = infoGainCrit.oldEnt(this.m_distribution);
    while (b2 < b1) {
      if (paramInstances.instance(b2 - 1).value(this.m_attIndex) + 1.0E-5D < paramInstances.instance(b2).value(this.m_attIndex)) {
        this.m_distribution.shiftRange(1, 0, paramInstances, b3, b2);
        if (Utils.grOrEq(this.m_distribution.perBag(0), d2) && Utils.grOrEq(this.m_distribution.perBag(1), d2)) {
          double d = infoGainCrit.splitCritValue(this.m_distribution, this.m_sumOfWeights, d1);
          if (Utils.gr(d, this.m_infoGain)) {
            this.m_infoGain = d;
            i = b2 - 1;
          } 
          this.m_index++;
        } 
        b3 = b2;
      } 
      b2++;
    } 
    if (this.m_index == 0)
      return; 
    this.m_infoGain -= Utils.log2(this.m_index) / this.m_sumOfWeights;
    if (Utils.smOrEq(this.m_infoGain, 0.0D))
      return; 
    this.m_numSubsets = 2;
    this.m_splitPoint = (paramInstances.instance(i + 1).value(this.m_attIndex) + paramInstances.instance(i).value(this.m_attIndex)) / 2.0D;
    this.m_distribution = new Distribution(2, paramInstances.numClasses());
    this.m_distribution.addRange(0, paramInstances, 0, i + 1);
    this.m_distribution.addRange(1, paramInstances, i + 1, b1);
    this.m_gainRatio = gainRatioCrit.splitCritValue(this.m_distribution, this.m_sumOfWeights, this.m_infoGain);
  }
  
  public final double infoGain() {
    return this.m_infoGain;
  }
  
  public final String leftSide(Instances paramInstances) {
    return paramInstances.attribute(this.m_attIndex).name();
  }
  
  public final String rightSide(int paramInt, Instances paramInstances) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramInstances.attribute(this.m_attIndex).isNominal()) {
      stringBuffer.append(" = " + paramInstances.attribute(this.m_attIndex).value(paramInt));
    } else if (paramInt == 0) {
      stringBuffer.append(" <= " + Utils.doubleToString(this.m_splitPoint, 6));
    } else {
      stringBuffer.append(" > " + Utils.doubleToString(this.m_splitPoint, 6));
    } 
    return stringBuffer.toString();
  }
  
  public final String sourceExpression(int paramInt, Instances paramInstances) {
    StringBuffer stringBuffer = null;
    if (paramInt < 0)
      return "i[" + this.m_attIndex + "] == null"; 
    if (paramInstances.attribute(this.m_attIndex).isNominal()) {
      stringBuffer = new StringBuffer("i[");
      stringBuffer.append(this.m_attIndex).append("]");
      stringBuffer.append(".equals(\"").append(paramInstances.attribute(this.m_attIndex).value(paramInt)).append("\")");
    } else {
      stringBuffer = new StringBuffer("((Double) i[");
      stringBuffer.append(this.m_attIndex).append("])");
      if (paramInt == 0) {
        stringBuffer.append(".doubleValue() <= ").append(this.m_splitPoint);
      } else {
        stringBuffer.append(".doubleValue() > ").append(this.m_splitPoint);
      } 
    } 
    return stringBuffer.toString();
  }
  
  public final void setSplitPoint(Instances paramInstances) {
    double d = -1.7976931348623157E308D;
    if (paramInstances.attribute(this.m_attIndex).isNumeric() && this.m_numSubsets > 1) {
      Enumeration enumeration = paramInstances.enumerateInstances();
      while (enumeration.hasMoreElements()) {
        Instance instance = enumeration.nextElement();
        if (!instance.isMissing(this.m_attIndex)) {
          double d1 = instance.value(this.m_attIndex);
          if (Utils.gr(d1, d) && Utils.smOrEq(d1, this.m_splitPoint))
            d = d1; 
        } 
      } 
      this.m_splitPoint = d;
    } 
  }
  
  public final double[][] minsAndMaxs(Instances paramInstances, double[][] paramArrayOfdouble, int paramInt) {
    double[][] arrayOfDouble = new double[paramInstances.numAttributes()][2];
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      arrayOfDouble[b][0] = paramArrayOfdouble[b][0];
      arrayOfDouble[b][1] = paramArrayOfdouble[b][1];
      if (b == this.m_attIndex)
        if (paramInstances.attribute(this.m_attIndex).isNominal()) {
          arrayOfDouble[this.m_attIndex][1] = 1.0D;
        } else {
          arrayOfDouble[this.m_attIndex][1 - paramInt] = this.m_splitPoint;
        }  
    } 
    return arrayOfDouble;
  }
  
  public void resetDistribution(Instances paramInstances) throws Exception {
    Instances instances = new Instances(paramInstances, paramInstances.numInstances());
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      if (whichSubset(paramInstances.instance(b)) > -1)
        instances.add(paramInstances.instance(b)); 
    } 
    Distribution distribution = new Distribution(instances, this);
    distribution.addInstWithUnknown(paramInstances, this.m_attIndex);
    this.m_distribution = distribution;
  }
  
  public final double[] weights(Instance paramInstance) {
    if (paramInstance.isMissing(this.m_attIndex)) {
      double[] arrayOfDouble = new double[this.m_numSubsets];
      for (byte b = 0; b < this.m_numSubsets; b++)
        arrayOfDouble[b] = this.m_distribution.perBag(b) / this.m_distribution.total(); 
      return arrayOfDouble;
    } 
    return null;
  }
  
  public final int whichSubset(Instance paramInstance) throws Exception {
    return paramInstance.isMissing(this.m_attIndex) ? -1 : (paramInstance.attribute(this.m_attIndex).isNominal() ? (int)paramInstance.value(this.m_attIndex) : (Utils.smOrEq(paramInstance.value(this.m_attIndex), this.m_splitPoint) ? 0 : 1));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\C45Split.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */