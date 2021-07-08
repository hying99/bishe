package weka.classifiers.trees.j48;

import java.io.Serializable;
import java.util.Enumeration;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class Distribution implements Cloneable, Serializable {
  private double[][] m_perClassPerBag;
  
  private double[] m_perBag;
  
  private double[] m_perClass;
  
  private double totaL;
  
  public Distribution(int paramInt1, int paramInt2) {
    this.m_perClassPerBag = new double[paramInt1][0];
    this.m_perBag = new double[paramInt1];
    this.m_perClass = new double[paramInt2];
    for (byte b = 0; b < paramInt1; b++)
      this.m_perClassPerBag[b] = new double[paramInt2]; 
    this.totaL = 0.0D;
  }
  
  public Distribution(double[][] paramArrayOfdouble) {
    this.m_perClassPerBag = paramArrayOfdouble;
    this.m_perBag = new double[paramArrayOfdouble.length];
    this.m_perClass = new double[(paramArrayOfdouble[0]).length];
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      for (byte b1 = 0; b1 < (paramArrayOfdouble[b]).length; b1++) {
        this.m_perBag[b] = this.m_perBag[b] + paramArrayOfdouble[b][b1];
        this.m_perClass[b1] = this.m_perClass[b1] + paramArrayOfdouble[b][b1];
        this.totaL += paramArrayOfdouble[b][b1];
      } 
    } 
  }
  
  public Distribution(Instances paramInstances) throws Exception {
    this.m_perClassPerBag = new double[1][0];
    this.m_perBag = new double[1];
    this.totaL = 0.0D;
    this.m_perClass = new double[paramInstances.numClasses()];
    this.m_perClassPerBag[0] = new double[paramInstances.numClasses()];
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements())
      add(0, enumeration.nextElement()); 
  }
  
  public Distribution(Instances paramInstances, ClassifierSplitModel paramClassifierSplitModel) throws Exception {
    this.m_perClassPerBag = new double[paramClassifierSplitModel.numSubsets()][0];
    this.m_perBag = new double[paramClassifierSplitModel.numSubsets()];
    this.totaL = 0.0D;
    this.m_perClass = new double[paramInstances.numClasses()];
    for (byte b = 0; b < paramClassifierSplitModel.numSubsets(); b++)
      this.m_perClassPerBag[b] = new double[paramInstances.numClasses()]; 
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      int i = paramClassifierSplitModel.whichSubset(instance);
      if (i != -1) {
        add(i, instance);
        continue;
      } 
      double[] arrayOfDouble = paramClassifierSplitModel.weights(instance);
      addWeights(instance, arrayOfDouble);
    } 
  }
  
  public Distribution(Distribution paramDistribution) {
    this.totaL = paramDistribution.totaL;
    this.m_perClass = new double[paramDistribution.numClasses()];
    System.arraycopy(paramDistribution.m_perClass, 0, this.m_perClass, 0, paramDistribution.numClasses());
    this.m_perClassPerBag = new double[1][0];
    this.m_perClassPerBag[0] = new double[paramDistribution.numClasses()];
    System.arraycopy(paramDistribution.m_perClass, 0, this.m_perClassPerBag[0], 0, paramDistribution.numClasses());
    this.m_perBag = new double[1];
    this.m_perBag[0] = this.totaL;
  }
  
  public Distribution(Distribution paramDistribution, int paramInt) {
    this.totaL = paramDistribution.totaL;
    this.m_perClass = new double[paramDistribution.numClasses()];
    System.arraycopy(paramDistribution.m_perClass, 0, this.m_perClass, 0, paramDistribution.numClasses());
    this.m_perClassPerBag = new double[2][0];
    this.m_perClassPerBag[0] = new double[paramDistribution.numClasses()];
    System.arraycopy(paramDistribution.m_perClassPerBag[paramInt], 0, this.m_perClassPerBag[0], 0, paramDistribution.numClasses());
    this.m_perClassPerBag[1] = new double[paramDistribution.numClasses()];
    for (byte b = 0; b < paramDistribution.numClasses(); b++)
      this.m_perClassPerBag[1][b] = paramDistribution.m_perClass[b] - this.m_perClassPerBag[0][b]; 
    this.m_perBag = new double[2];
    this.m_perBag[0] = paramDistribution.m_perBag[paramInt];
    this.m_perBag[1] = this.totaL - this.m_perBag[0];
  }
  
  public final int actualNumBags() {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_perBag.length; b2++) {
      if (Utils.gr(this.m_perBag[b2], 0.0D))
        b1++; 
    } 
    return b1;
  }
  
  public final int actualNumClasses() {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_perClass.length; b2++) {
      if (Utils.gr(this.m_perClass[b2], 0.0D))
        b1++; 
    } 
    return b1;
  }
  
  public final int actualNumClasses(int paramInt) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_perClass.length; b2++) {
      if (Utils.gr(this.m_perClassPerBag[paramInt][b2], 0.0D))
        b1++; 
    } 
    return b1;
  }
  
  public final void add(int paramInt, Instance paramInstance) throws Exception {
    int i = (int)paramInstance.classValue();
    double d = paramInstance.weight();
    this.m_perClassPerBag[paramInt][i] = this.m_perClassPerBag[paramInt][i] + d;
    this.m_perBag[paramInt] = this.m_perBag[paramInt] + d;
    this.m_perClass[i] = this.m_perClass[i] + d;
    this.totaL += d;
  }
  
  public final void sub(int paramInt, Instance paramInstance) throws Exception {
    int i = (int)paramInstance.classValue();
    double d = paramInstance.weight();
    this.m_perClassPerBag[paramInt][i] = this.m_perClassPerBag[paramInt][i] - d;
    this.m_perBag[paramInt] = this.m_perBag[paramInt] - d;
    this.m_perClass[i] = this.m_perClass[i] - d;
    this.totaL -= d;
  }
  
  public final void add(int paramInt, double[] paramArrayOfdouble) {
    double d = Utils.sum(paramArrayOfdouble);
    byte b;
    for (b = 0; b < paramArrayOfdouble.length; b++)
      this.m_perClassPerBag[paramInt][b] = this.m_perClassPerBag[paramInt][b] + paramArrayOfdouble[b]; 
    this.m_perBag[paramInt] = this.m_perBag[paramInt] + d;
    for (b = 0; b < paramArrayOfdouble.length; b++)
      this.m_perClass[b] = this.m_perClass[b] + paramArrayOfdouble[b]; 
    this.totaL += d;
  }
  
  public final void addInstWithUnknown(Instances paramInstances, int paramInt) throws Exception {
    double[] arrayOfDouble = new double[this.m_perBag.length];
    byte b;
    for (b = 0; b < this.m_perBag.length; b++) {
      if (Utils.eq(this.totaL, 0.0D)) {
        arrayOfDouble[b] = 1.0D / arrayOfDouble.length;
      } else {
        arrayOfDouble[b] = this.m_perBag[b] / this.totaL;
      } 
    } 
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if (instance.isMissing(paramInt)) {
        int i = (int)instance.classValue();
        double d = instance.weight();
        this.m_perClass[i] = this.m_perClass[i] + d;
        this.totaL += d;
        for (b = 0; b < this.m_perBag.length; b++) {
          double d1 = arrayOfDouble[b] * d;
          this.m_perClassPerBag[b][i] = this.m_perClassPerBag[b][i] + d1;
          this.m_perBag[b] = this.m_perBag[b] + d1;
        } 
      } 
    } 
  }
  
  public final void addRange(int paramInt1, Instances paramInstances, int paramInt2, int paramInt3) throws Exception {
    double d = 0.0D;
    for (int i = paramInt2; i < paramInt3; i++) {
      Instance instance = paramInstances.instance(i);
      int j = (int)instance.classValue();
      d += instance.weight();
      this.m_perClassPerBag[paramInt1][j] = this.m_perClassPerBag[paramInt1][j] + instance.weight();
      this.m_perClass[j] = this.m_perClass[j] + instance.weight();
    } 
    this.m_perBag[paramInt1] = this.m_perBag[paramInt1] + d;
    this.totaL += d;
  }
  
  public final void addWeights(Instance paramInstance, double[] paramArrayOfdouble) throws Exception {
    int i = (int)paramInstance.classValue();
    for (byte b = 0; b < this.m_perBag.length; b++) {
      double d = paramInstance.weight() * paramArrayOfdouble[b];
      this.m_perClassPerBag[b][i] = this.m_perClassPerBag[b][i] + d;
      this.m_perBag[b] = this.m_perBag[b] + d;
      this.m_perClass[i] = this.m_perClass[i] + d;
      this.totaL += d;
    } 
  }
  
  public final boolean check(double paramDouble) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_perBag.length; b2++) {
      if (Utils.grOrEq(this.m_perBag[b2], paramDouble))
        b1++; 
    } 
    return (b1 > 1);
  }
  
  public final Object clone() {
    Distribution distribution = new Distribution(this.m_perBag.length, this.m_perClass.length);
    for (byte b1 = 0; b1 < this.m_perBag.length; b1++) {
      distribution.m_perBag[b1] = this.m_perBag[b1];
      for (byte b = 0; b < this.m_perClass.length; b++)
        distribution.m_perClassPerBag[b1][b] = this.m_perClassPerBag[b1][b]; 
    } 
    for (byte b2 = 0; b2 < this.m_perClass.length; b2++)
      distribution.m_perClass[b2] = this.m_perClass[b2]; 
    distribution.totaL = this.totaL;
    return distribution;
  }
  
  public final void del(int paramInt, Instance paramInstance) throws Exception {
    int i = (int)paramInstance.classValue();
    double d = paramInstance.weight();
    this.m_perClassPerBag[paramInt][i] = this.m_perClassPerBag[paramInt][i] - d;
    this.m_perBag[paramInt] = this.m_perBag[paramInt] - d;
    this.m_perClass[i] = this.m_perClass[i] - d;
    this.totaL -= d;
  }
  
  public final void delRange(int paramInt1, Instances paramInstances, int paramInt2, int paramInt3) throws Exception {
    double d = 0.0D;
    for (int i = paramInt2; i < paramInt3; i++) {
      Instance instance = paramInstances.instance(i);
      int j = (int)instance.classValue();
      d += instance.weight();
      this.m_perClassPerBag[paramInt1][j] = this.m_perClassPerBag[paramInt1][j] - instance.weight();
      this.m_perClass[j] = this.m_perClass[j] - instance.weight();
    } 
    this.m_perBag[paramInt1] = this.m_perBag[paramInt1] - d;
    this.totaL -= d;
  }
  
  public final String dumpDistribution() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.m_perBag.length; b++) {
      stringBuffer.append("Bag num " + b + "\n");
      for (byte b1 = 0; b1 < this.m_perClass.length; b1++)
        stringBuffer.append("Class num " + b1 + " " + this.m_perClassPerBag[b][b1] + "\n"); 
    } 
    return stringBuffer.toString();
  }
  
  public final void initialize() {
    byte b;
    for (b = 0; b < this.m_perClass.length; b++)
      this.m_perClass[b] = 0.0D; 
    for (b = 0; b < this.m_perBag.length; b++)
      this.m_perBag[b] = 0.0D; 
    for (b = 0; b < this.m_perBag.length; b++) {
      for (byte b1 = 0; b1 < this.m_perClass.length; b1++)
        this.m_perClassPerBag[b][b1] = 0.0D; 
    } 
    this.totaL = 0.0D;
  }
  
  public final double[][] matrix() {
    return this.m_perClassPerBag;
  }
  
  public final int maxBag() {
    double d = 0.0D;
    byte b = -1;
    for (byte b1 = 0; b1 < this.m_perBag.length; b1++) {
      if (Utils.grOrEq(this.m_perBag[b1], d)) {
        d = this.m_perBag[b1];
        b = b1;
      } 
    } 
    return b;
  }
  
  public final int maxClass() {
    double d = 0.0D;
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_perClass.length; b2++) {
      if (Utils.gr(this.m_perClass[b2], d)) {
        d = this.m_perClass[b2];
        b1 = b2;
      } 
    } 
    return b1;
  }
  
  public final int maxClass(int paramInt) {
    double d = 0.0D;
    byte b = 0;
    if (Utils.gr(this.m_perBag[paramInt], 0.0D)) {
      for (byte b1 = 0; b1 < this.m_perClass.length; b1++) {
        if (Utils.gr(this.m_perClassPerBag[paramInt][b1], d)) {
          d = this.m_perClassPerBag[paramInt][b1];
          b = b1;
        } 
      } 
      return b;
    } 
    return maxClass();
  }
  
  public final int numBags() {
    return this.m_perBag.length;
  }
  
  public final int numClasses() {
    return this.m_perClass.length;
  }
  
  public final double numCorrect() {
    return this.m_perClass[maxClass()];
  }
  
  public final double numCorrect(int paramInt) {
    return this.m_perClassPerBag[paramInt][maxClass(paramInt)];
  }
  
  public final double numIncorrect() {
    return this.totaL - numCorrect();
  }
  
  public final double numIncorrect(int paramInt) {
    return this.m_perBag[paramInt] - numCorrect(paramInt);
  }
  
  public final double perClassPerBag(int paramInt1, int paramInt2) {
    return this.m_perClassPerBag[paramInt1][paramInt2];
  }
  
  public final double perBag(int paramInt) {
    return this.m_perBag[paramInt];
  }
  
  public final double perClass(int paramInt) {
    return this.m_perClass[paramInt];
  }
  
  public final double laplaceProb(int paramInt) {
    return (this.m_perClass[paramInt] + 1.0D) / (this.totaL + actualNumClasses());
  }
  
  public final double laplaceProb(int paramInt1, int paramInt2) {
    return (this.m_perClassPerBag[paramInt2][paramInt1] + 1.0D) / (this.m_perBag[paramInt2] + actualNumClasses());
  }
  
  public final double prob(int paramInt) {
    return !Utils.eq(this.totaL, 0.0D) ? (this.m_perClass[paramInt] / this.totaL) : 0.0D;
  }
  
  public final double prob(int paramInt1, int paramInt2) {
    return Utils.gr(this.m_perBag[paramInt2], 0.0D) ? (this.m_perClassPerBag[paramInt2][paramInt1] / this.m_perBag[paramInt2]) : prob(paramInt1);
  }
  
  public final Distribution subtract(Distribution paramDistribution) {
    Distribution distribution = new Distribution(1, this.m_perClass.length);
    distribution.m_perBag[0] = this.totaL - paramDistribution.totaL;
    distribution.totaL = distribution.m_perBag[0];
    for (byte b = 0; b < this.m_perClass.length; b++) {
      distribution.m_perClassPerBag[0][b] = this.m_perClass[b] - paramDistribution.m_perClass[b];
      distribution.m_perClass[b] = distribution.m_perClassPerBag[0][b];
    } 
    return distribution;
  }
  
  public final double total() {
    return this.totaL;
  }
  
  public final void shift(int paramInt1, int paramInt2, Instance paramInstance) throws Exception {
    int i = (int)paramInstance.classValue();
    double d = paramInstance.weight();
    this.m_perClassPerBag[paramInt1][i] = this.m_perClassPerBag[paramInt1][i] - d;
    this.m_perClassPerBag[paramInt2][i] = this.m_perClassPerBag[paramInt2][i] + d;
    this.m_perBag[paramInt1] = this.m_perBag[paramInt1] - d;
    this.m_perBag[paramInt2] = this.m_perBag[paramInt2] + d;
  }
  
  public final void shiftRange(int paramInt1, int paramInt2, Instances paramInstances, int paramInt3, int paramInt4) throws Exception {
    for (int i = paramInt3; i < paramInt4; i++) {
      Instance instance = paramInstances.instance(i);
      int j = (int)instance.classValue();
      double d = instance.weight();
      this.m_perClassPerBag[paramInt1][j] = this.m_perClassPerBag[paramInt1][j] - d;
      this.m_perClassPerBag[paramInt2][j] = this.m_perClassPerBag[paramInt2][j] + d;
      this.m_perBag[paramInt1] = this.m_perBag[paramInt1] - d;
      this.m_perBag[paramInt2] = this.m_perBag[paramInt2] + d;
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\Distribution.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */