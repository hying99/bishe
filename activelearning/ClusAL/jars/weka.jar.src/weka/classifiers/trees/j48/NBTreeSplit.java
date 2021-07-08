package weka.classifiers.trees.j48;

import java.util.Random;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public class NBTreeSplit extends ClassifierSplitModel {
  private int m_complexityIndex;
  
  private int m_attIndex;
  
  private int m_minNoObj;
  
  private double m_splitPoint;
  
  private double m_sumOfWeights;
  
  private double m_errors;
  
  private C45Split m_c45S;
  
  NBTreeNoSplit m_globalNB;
  
  public NBTreeSplit(int paramInt1, int paramInt2, double paramDouble) {
    this.m_attIndex = paramInt1;
    this.m_minNoObj = paramInt2;
    this.m_sumOfWeights = paramDouble;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_numSubsets = 0;
    this.m_splitPoint = Double.MAX_VALUE;
    this.m_errors = 0.0D;
    if (this.m_globalNB != null)
      this.m_errors = this.m_globalNB.getErrors(); 
    if (paramInstances.attribute(this.m_attIndex).isNominal()) {
      this.m_complexityIndex = paramInstances.attribute(this.m_attIndex).numValues();
      handleEnumeratedAttribute(paramInstances);
    } else {
      this.m_complexityIndex = 2;
      paramInstances.sort(paramInstances.attribute(this.m_attIndex));
      handleNumericAttribute(paramInstances);
    } 
  }
  
  public final int attIndex() {
    return this.m_attIndex;
  }
  
  private void handleEnumeratedAttribute(Instances paramInstances) throws Exception {
    this.m_c45S = new C45Split(this.m_attIndex, 2, this.m_sumOfWeights);
    this.m_c45S.buildClassifier(paramInstances);
    if (this.m_c45S.numSubsets() == 0)
      return; 
    this.m_errors = 0.0D;
    Instances[] arrayOfInstances = new Instances[this.m_complexityIndex];
    int i;
    for (i = 0; i < this.m_complexityIndex; i++)
      arrayOfInstances[i] = new Instances(paramInstances, 0); 
    for (byte b1 = 0; b1 < paramInstances.numInstances(); b1++) {
      Instance instance = paramInstances.instance(b1);
      i = this.m_c45S.whichSubset(instance);
      if (i > -1) {
        arrayOfInstances[i].add((Instance)instance.copy());
      } else {
        double[] arrayOfDouble = this.m_c45S.weights(instance);
        for (byte b = 0; b < this.m_complexityIndex; b++) {
          try {
            Instance instance1 = (Instance)instance.copy();
            if (arrayOfDouble.length == this.m_complexityIndex) {
              instance1.setWeight(instance1.weight() * arrayOfDouble[b]);
            } else {
              instance1.setWeight(instance1.weight() / this.m_complexityIndex);
            } 
            arrayOfInstances[b].add(instance1);
          } catch (Exception exception) {
            exception.printStackTrace();
            System.err.println("*** " + this.m_complexityIndex);
            System.err.println(arrayOfDouble.length);
            System.exit(1);
          } 
        } 
      } 
    } 
    Random random = new Random(1L);
    byte b2 = 0;
    for (byte b3 = 0; b3 < this.m_complexityIndex; b3++) {
      if (arrayOfInstances[b3].numInstances() >= 5) {
        b2++;
        Discretize discretize = new Discretize();
        discretize.setInputFormat(arrayOfInstances[b3]);
        arrayOfInstances[b3] = Filter.useFilter(arrayOfInstances[b3], (Filter)discretize);
        arrayOfInstances[b3].randomize(random);
        arrayOfInstances[b3].stratify(5);
        NaiveBayesUpdateable naiveBayesUpdateable = new NaiveBayesUpdateable();
        naiveBayesUpdateable.buildClassifier(arrayOfInstances[b3]);
        this.m_errors += NBTreeNoSplit.crossValidate(naiveBayesUpdateable, arrayOfInstances[b3], random);
      } else {
        for (byte b = 0; b < arrayOfInstances[b3].numInstances(); b++)
          this.m_errors += arrayOfInstances[b3].instance(b).weight(); 
      } 
    } 
    if (b2 > 1)
      this.m_numSubsets = this.m_complexityIndex; 
  }
  
  private void handleNumericAttribute(Instances paramInstances) throws Exception {
    this.m_c45S = new C45Split(this.m_attIndex, 2, this.m_sumOfWeights);
    this.m_c45S.buildClassifier(paramInstances);
    if (this.m_c45S.numSubsets() == 0)
      return; 
    this.m_errors = 0.0D;
    Instances[] arrayOfInstances = new Instances[this.m_complexityIndex];
    arrayOfInstances[0] = new Instances(paramInstances, 0);
    arrayOfInstances[1] = new Instances(paramInstances, 0);
    int i = -1;
    for (byte b1 = 0; b1 < paramInstances.numInstances(); b1++) {
      Instance instance = paramInstances.instance(b1);
      i = this.m_c45S.whichSubset(instance);
      if (i != -1) {
        arrayOfInstances[i].add((Instance)instance.copy());
      } else {
        double[] arrayOfDouble = this.m_c45S.weights(instance);
        for (byte b = 0; b < this.m_complexityIndex; b++) {
          Instance instance1 = (Instance)instance.copy();
          if (arrayOfDouble.length == this.m_complexityIndex) {
            instance1.setWeight(instance1.weight() * arrayOfDouble[b]);
          } else {
            instance1.setWeight(instance1.weight() / this.m_complexityIndex);
          } 
          arrayOfInstances[b].add(instance1);
        } 
      } 
    } 
    Random random = new Random(1L);
    byte b2 = 0;
    for (byte b3 = 0; b3 < this.m_complexityIndex; b3++) {
      if (arrayOfInstances[b3].numInstances() > 5) {
        b2++;
        Discretize discretize = new Discretize();
        discretize.setInputFormat(arrayOfInstances[b3]);
        arrayOfInstances[b3] = Filter.useFilter(arrayOfInstances[b3], (Filter)discretize);
        arrayOfInstances[b3].randomize(random);
        arrayOfInstances[b3].stratify(5);
        NaiveBayesUpdateable naiveBayesUpdateable = new NaiveBayesUpdateable();
        naiveBayesUpdateable.buildClassifier(arrayOfInstances[b3]);
        this.m_errors += NBTreeNoSplit.crossValidate(naiveBayesUpdateable, arrayOfInstances[b3], random);
      } else {
        for (byte b = 0; b < arrayOfInstances[b3].numInstances(); b++)
          this.m_errors += arrayOfInstances[b3].instance(b).weight(); 
      } 
    } 
    if (b2 > 1)
      this.m_numSubsets = this.m_complexityIndex; 
  }
  
  public final int whichSubset(Instance paramInstance) throws Exception {
    return this.m_c45S.whichSubset(paramInstance);
  }
  
  public final double[] weights(Instance paramInstance) {
    return this.m_c45S.weights(paramInstance);
  }
  
  public final String sourceExpression(int paramInt, Instances paramInstances) {
    return this.m_c45S.sourceExpression(paramInt, paramInstances);
  }
  
  public final String rightSide(int paramInt, Instances paramInstances) {
    return this.m_c45S.rightSide(paramInt, paramInstances);
  }
  
  public final String leftSide(Instances paramInstances) {
    return this.m_c45S.leftSide(paramInstances);
  }
  
  public double classProb(int paramInt1, Instance paramInstance, int paramInt2) throws Exception {
    if (paramInt2 > -1)
      return this.m_globalNB.classProb(paramInt1, paramInstance, paramInt2); 
    throw new Exception("This shouldn't happen!!!");
  }
  
  public NBTreeNoSplit getGlobalModel() {
    return this.m_globalNB;
  }
  
  public void setGlobalModel(NBTreeNoSplit paramNBTreeNoSplit) {
    this.m_globalNB = paramNBTreeNoSplit;
  }
  
  public double getErrors() {
    return this.m_errors;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\NBTreeSplit.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */