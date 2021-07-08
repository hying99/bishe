package weka.classifiers.trees.lmt;

import weka.classifiers.trees.j48.ClassifierSplitModel;
import weka.classifiers.trees.j48.Distribution;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class ResidualSplit extends ClassifierSplitModel {
  protected Attribute m_attribute;
  
  protected int m_attIndex;
  
  protected int m_numInstances;
  
  protected int m_numClasses;
  
  protected Instances m_data;
  
  protected double[][] m_dataZs;
  
  protected double[][] m_dataWs;
  
  protected double m_splitPoint;
  
  public ResidualSplit(int paramInt) {
    this.m_attIndex = paramInt;
  }
  
  public void buildClassifier(Instances paramInstances, double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2) throws Exception {
    this.m_numClasses = paramInstances.numClasses();
    this.m_numInstances = paramInstances.numInstances();
    if (this.m_numInstances == 0)
      throw new Exception("Can't build split on 0 instances"); 
    this.m_data = paramInstances;
    this.m_dataZs = paramArrayOfdouble1;
    this.m_dataWs = paramArrayOfdouble2;
    this.m_attribute = paramInstances.attribute(this.m_attIndex);
    if (this.m_attribute.isNominal()) {
      this.m_splitPoint = 0.0D;
      this.m_numSubsets = this.m_attribute.numValues();
    } else {
      getSplitPoint();
      this.m_numSubsets = 2;
    } 
    this.m_distribution = new Distribution(paramInstances, this);
  }
  
  protected boolean getSplitPoint() throws Exception {
    double[] arrayOfDouble1 = new double[this.m_numInstances];
    byte b1 = 0;
    Instances instances = new Instances(this.m_data);
    instances.sort(instances.attribute(this.m_attIndex));
    double d1 = instances.instance(0).value(this.m_attIndex);
    for (byte b2 = 0; b2 < this.m_numInstances - 1; b2++) {
      double d = instances.instance(b2 + 1).value(this.m_attIndex);
      if (!Utils.eq(d, d1))
        arrayOfDouble1[b1++] = (d1 + d) / 2.0D; 
      d1 = d;
    } 
    double[] arrayOfDouble2 = new double[b1];
    byte b;
    for (b = 0; b < b1; b++) {
      this.m_splitPoint = arrayOfDouble1[b];
      arrayOfDouble2[b] = entropyGain();
    } 
    b = -1;
    double d2 = -1.7976931348623157E308D;
    for (byte b3 = 0; b3 < b1; b3++) {
      if (arrayOfDouble2[b3] > d2) {
        d2 = arrayOfDouble2[b3];
        b = b3;
      } 
    } 
    if (b < 0)
      return false; 
    this.m_splitPoint = arrayOfDouble1[b];
    return true;
  }
  
  public double entropyGain() throws Exception {
    byte b1;
    if (this.m_attribute.isNominal()) {
      b1 = this.m_attribute.numValues();
    } else {
      b1 = 2;
    } 
    double[][][] arrayOfDouble1 = new double[b1][][];
    double[][][] arrayOfDouble2 = new double[b1][][];
    int[] arrayOfInt1 = new int[b1];
    byte b2;
    for (b2 = 0; b2 < this.m_numInstances; b2++) {
      int i = whichSubset(this.m_data.instance(b2));
      if (i < 0)
        throw new Exception("ResidualSplit: no support for splits on missing values"); 
      arrayOfInt1[i] = arrayOfInt1[i] + 1;
    } 
    for (b2 = 0; b2 < b1; b2++) {
      arrayOfDouble1[b2] = new double[arrayOfInt1[b2]][];
      arrayOfDouble2[b2] = new double[arrayOfInt1[b2]][];
    } 
    int[] arrayOfInt2 = new int[b1];
    for (byte b3 = 0; b3 < this.m_numInstances; b3++) {
      int i = whichSubset(this.m_data.instance(b3));
      arrayOfDouble1[i][arrayOfInt2[i]] = this.m_dataZs[b3];
      arrayOfDouble2[i][arrayOfInt2[i]] = this.m_dataWs[b3];
      arrayOfInt2[i] = arrayOfInt2[i] + 1;
    } 
    double d1 = entropy(this.m_dataZs, this.m_dataWs);
    double d2 = 0.0D;
    for (byte b4 = 0; b4 < b1; b4++)
      d2 += entropy(arrayOfDouble1[b4], arrayOfDouble2[b4]); 
    return d1 - d2;
  }
  
  protected double entropy(double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2) {
    double d = 0.0D;
    int i = paramArrayOfdouble1.length;
    for (byte b = 0; b < this.m_numClasses; b++) {
      double d1 = 0.0D;
      double d2 = 0.0D;
      byte b1;
      for (b1 = 0; b1 < i; b1++) {
        d1 += paramArrayOfdouble1[b1][b] * paramArrayOfdouble2[b1][b];
        d2 += paramArrayOfdouble2[b1][b];
      } 
      d1 /= d2;
      for (b1 = 0; b1 < i; b1++)
        d += paramArrayOfdouble2[b1][b] * Math.pow(paramArrayOfdouble1[b1][b] - d1, 2.0D); 
    } 
    return d;
  }
  
  public boolean checkModel(int paramInt) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_distribution.numBags(); b2++) {
      if (this.m_distribution.perBag(b2) >= paramInt)
        b1++; 
    } 
    return (b1 >= 2);
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
  
  public final int whichSubset(Instance paramInstance) throws Exception {
    return paramInstance.isMissing(this.m_attIndex) ? -1 : (paramInstance.attribute(this.m_attIndex).isNominal() ? (int)paramInstance.value(this.m_attIndex) : (Utils.smOrEq(paramInstance.value(this.m_attIndex), this.m_splitPoint) ? 0 : 1));
  }
  
  public void buildClassifier(Instances paramInstances) {}
  
  public final double[] weights(Instance paramInstance) {
    return null;
  }
  
  public final String sourceExpression(int paramInt, Instances paramInstances) {
    return "";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\lmt\ResidualSplit.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */