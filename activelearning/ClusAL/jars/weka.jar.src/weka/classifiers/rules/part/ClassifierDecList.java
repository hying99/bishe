package weka.classifiers.rules.part;

import java.io.Serializable;
import weka.classifiers.trees.j48.ClassifierSplitModel;
import weka.classifiers.trees.j48.Distribution;
import weka.classifiers.trees.j48.EntropySplitCrit;
import weka.classifiers.trees.j48.ModelSelection;
import weka.classifiers.trees.j48.NoSplit;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class ClassifierDecList implements Serializable {
  protected int m_minNumObj;
  
  protected static EntropySplitCrit m_splitCrit = new EntropySplitCrit();
  
  protected ModelSelection m_toSelectModel;
  
  protected ClassifierSplitModel m_localModel;
  
  protected ClassifierDecList[] m_sons;
  
  protected boolean m_isLeaf;
  
  protected boolean m_isEmpty;
  
  protected Instances m_train;
  
  protected Distribution m_test;
  
  protected int indeX;
  
  public ClassifierDecList(ModelSelection paramModelSelection, int paramInt) {
    this.m_toSelectModel = paramModelSelection;
    this.m_minNumObj = paramInt;
  }
  
  public void buildRule(Instances paramInstances) throws Exception {
    buildDecList(paramInstances, false);
    cleanup(new Instances(paramInstances, 0));
  }
  
  public void buildDecList(Instances paramInstances, boolean paramBoolean) throws Exception {
    this.m_train = null;
    this.m_test = null;
    this.m_isLeaf = false;
    this.m_isEmpty = false;
    this.m_sons = null;
    this.indeX = 0;
    double d = paramInstances.sumOfWeights();
    NoSplit noSplit = new NoSplit(new Distribution(paramInstances));
    if (paramBoolean) {
      this.m_localModel = (ClassifierSplitModel)noSplit;
    } else {
      this.m_localModel = this.m_toSelectModel.selectModel(paramInstances);
    } 
    if (this.m_localModel.numSubsets() > 1) {
      int i;
      Instances[] arrayOfInstances = this.m_localModel.split(paramInstances);
      paramInstances = null;
      this.m_sons = new ClassifierDecList[this.m_localModel.numSubsets()];
      byte b = 0;
      do {
        b++;
        i = chooseIndex();
        if (i == -1) {
          for (byte b1 = 0; b1 < this.m_sons.length; b1++) {
            if (this.m_sons[b1] == null)
              this.m_sons[b1] = getNewDecList(arrayOfInstances[b1], true); 
          } 
          if (b < 2) {
            this.m_localModel = (ClassifierSplitModel)noSplit;
            this.m_isLeaf = true;
            this.m_sons = null;
            if (Utils.eq(d, 0.0D))
              this.m_isEmpty = true; 
            return;
          } 
          i = 0;
          break;
        } 
        this.m_sons[i] = getNewDecList(arrayOfInstances[i], false);
      } while (b < this.m_sons.length && (this.m_sons[i]).m_isLeaf);
      this.indeX = chooseLastIndex();
    } else {
      this.m_isLeaf = true;
      if (Utils.eq(d, 0.0D))
        this.m_isEmpty = true; 
    } 
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    double d = -1.0D;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInstance.numClasses(); b2++) {
      double d1 = getProbs(b2, paramInstance, 1.0D);
      if (Utils.gr(d1, d)) {
        b1 = b2;
        d = d1;
      } 
    } 
    return Utils.eq(d, 0.0D) ? -1.0D : b1;
  }
  
  public final double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = new double[paramInstance.numClasses()];
    for (byte b = 0; b < arrayOfDouble.length; b++)
      arrayOfDouble[b] = getProbs(b, paramInstance, 1.0D); 
    return arrayOfDouble;
  }
  
  public double weight(Instance paramInstance) throws Exception {
    if (this.m_isLeaf)
      return 1.0D; 
    int i = this.m_localModel.whichSubset(paramInstance);
    return (i == -1) ? (this.m_localModel.weights(paramInstance)[this.indeX] * this.m_sons[this.indeX].weight(paramInstance)) : ((i == this.indeX) ? this.m_sons[this.indeX].weight(paramInstance) : 0.0D);
  }
  
  public final void cleanup(Instances paramInstances) {
    this.m_train = paramInstances;
    this.m_test = null;
    if (!this.m_isLeaf)
      for (byte b = 0; b < this.m_sons.length; b++) {
        if (this.m_sons[b] != null)
          this.m_sons[b].cleanup(paramInstances); 
      }  
  }
  
  public String toString() {
    try {
      StringBuffer stringBuffer = new StringBuffer();
      if (this.m_isLeaf) {
        stringBuffer.append(": ");
        stringBuffer.append(this.m_localModel.dumpLabel(0, this.m_train) + "\n");
      } else {
        dumpDecList(stringBuffer);
      } 
      return stringBuffer.toString();
    } catch (Exception exception) {
      return "Can't print rule.";
    } 
  }
  
  protected ClassifierDecList getNewDecList(Instances paramInstances, boolean paramBoolean) throws Exception {
    ClassifierDecList classifierDecList = new ClassifierDecList(this.m_toSelectModel, this.m_minNumObj);
    classifierDecList.buildDecList(paramInstances, paramBoolean);
    return classifierDecList;
  }
  
  public final int chooseIndex() {
    byte b = -1;
    double d = Double.MAX_VALUE;
    for (byte b1 = 0; b1 < this.m_sons.length; b1++) {
      if (son(b1) == null) {
        double d1;
        if (Utils.sm(localModel().distribution().perBag(b1), this.m_minNumObj)) {
          d1 = Double.MAX_VALUE;
        } else {
          d1 = 0.0D;
          for (byte b2 = 0; b2 < localModel().distribution().numClasses(); b2++)
            d1 -= m_splitCrit.logFunc(localModel().distribution().perClassPerBag(b1, b2)); 
          d1 += m_splitCrit.logFunc(localModel().distribution().perBag(b1));
          d1 /= localModel().distribution().perBag(b1);
        } 
        if (Utils.smOrEq(d1, 0.0D))
          return b1; 
        if (Utils.sm(d1, d)) {
          d = d1;
          b = b1;
        } 
      } 
    } 
    return b;
  }
  
  public final int chooseLastIndex() {
    byte b = 0;
    double d = Double.MAX_VALUE;
    if (!this.m_isLeaf)
      for (byte b1 = 0; b1 < this.m_sons.length; b1++) {
        if (son(b1) != null && Utils.grOrEq(localModel().distribution().perBag(b1), this.m_minNumObj)) {
          double d1 = son(b1).getSizeOfBranch();
          if (Utils.sm(d1, d)) {
            d = d1;
            b = b1;
          } 
        } 
      }  
    return b;
  }
  
  protected double getSizeOfBranch() {
    return this.m_isLeaf ? -localModel().distribution().total() : son(this.indeX).getSizeOfBranch();
  }
  
  private void dumpDecList(StringBuffer paramStringBuffer) throws Exception {
    paramStringBuffer.append(this.m_localModel.leftSide(this.m_train));
    paramStringBuffer.append(this.m_localModel.rightSide(this.indeX, this.m_train));
    if ((this.m_sons[this.indeX]).m_isLeaf) {
      paramStringBuffer.append(": ");
      paramStringBuffer.append(this.m_localModel.dumpLabel(this.indeX, this.m_train) + "\n");
    } else {
      paramStringBuffer.append(" AND\n");
      this.m_sons[this.indeX].dumpDecList(paramStringBuffer);
    } 
  }
  
  private void dumpTree(int paramInt, StringBuffer paramStringBuffer) throws Exception {
    for (byte b = 0; b < this.m_sons.length; b++) {
      paramStringBuffer.append("\n");
      for (byte b1 = 0; b1 < paramInt; b1++)
        paramStringBuffer.append("|   "); 
      paramStringBuffer.append(this.m_localModel.leftSide(this.m_train));
      paramStringBuffer.append(this.m_localModel.rightSide(b, this.m_train));
      if (this.m_sons[b] == null) {
        paramStringBuffer.append("null");
      } else if ((this.m_sons[b]).m_isLeaf) {
        paramStringBuffer.append(": ");
        paramStringBuffer.append(this.m_localModel.dumpLabel(b, this.m_train));
      } else {
        this.m_sons[b].dumpTree(paramInt + 1, paramStringBuffer);
      } 
    } 
  }
  
  private double getProbs(int paramInt, Instance paramInstance, double paramDouble) throws Exception {
    if (this.m_isLeaf)
      return paramDouble * localModel().classProb(paramInt, paramInstance, -1); 
    int i = localModel().whichSubset(paramInstance);
    if (i == -1) {
      double[] arrayOfDouble = localModel().weights(paramInstance);
      return son(this.indeX).getProbs(paramInt, paramInstance, arrayOfDouble[this.indeX] * paramDouble);
    } 
    return (i == this.indeX) ? son(this.indeX).getProbs(paramInt, paramInstance, paramDouble) : 0.0D;
  }
  
  protected ClassifierSplitModel localModel() {
    return this.m_localModel;
  }
  
  protected ClassifierDecList son(int paramInt) {
    return this.m_sons[paramInt];
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\part\ClassifierDecList.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */