package weka.classifiers.trees.j48;

import java.io.Serializable;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;

public class ClassifierTree implements Drawable, Serializable {
  protected ModelSelection m_toSelectModel;
  
  protected ClassifierSplitModel m_localModel;
  
  protected ClassifierTree[] m_sons;
  
  protected boolean m_isLeaf;
  
  protected boolean m_isEmpty;
  
  protected Instances m_train;
  
  protected Distribution m_test;
  
  protected int m_id;
  
  private static long PRINTED_NODES = 0L;
  
  protected static long nextID() {
    return PRINTED_NODES++;
  }
  
  protected static void resetID() {
    PRINTED_NODES = 0L;
  }
  
  public ClassifierTree(ModelSelection paramModelSelection) {
    this.m_toSelectModel = paramModelSelection;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    buildTree(paramInstances, false);
  }
  
  public void buildTree(Instances paramInstances, boolean paramBoolean) throws Exception {
    if (paramBoolean)
      this.m_train = paramInstances; 
    this.m_test = null;
    this.m_isLeaf = false;
    this.m_isEmpty = false;
    this.m_sons = null;
    this.m_localModel = this.m_toSelectModel.selectModel(paramInstances);
    if (this.m_localModel.numSubsets() > 1) {
      Instances[] arrayOfInstances = this.m_localModel.split(paramInstances);
      paramInstances = null;
      this.m_sons = new ClassifierTree[this.m_localModel.numSubsets()];
      for (byte b = 0; b < this.m_sons.length; b++) {
        this.m_sons[b] = getNewTree(arrayOfInstances[b]);
        arrayOfInstances[b] = null;
      } 
    } else {
      this.m_isLeaf = true;
      if (Utils.eq(paramInstances.sumOfWeights(), 0.0D))
        this.m_isEmpty = true; 
      paramInstances = null;
    } 
  }
  
  public void buildTree(Instances paramInstances1, Instances paramInstances2, boolean paramBoolean) throws Exception {
    if (paramBoolean)
      this.m_train = paramInstances1; 
    this.m_isLeaf = false;
    this.m_isEmpty = false;
    this.m_sons = null;
    this.m_localModel = this.m_toSelectModel.selectModel(paramInstances1, paramInstances2);
    this.m_test = new Distribution(paramInstances2, this.m_localModel);
    if (this.m_localModel.numSubsets() > 1) {
      Instances[] arrayOfInstances1 = this.m_localModel.split(paramInstances1);
      Instances[] arrayOfInstances2 = this.m_localModel.split(paramInstances2);
      paramInstances1 = paramInstances2 = null;
      this.m_sons = new ClassifierTree[this.m_localModel.numSubsets()];
      for (byte b = 0; b < this.m_sons.length; b++) {
        this.m_sons[b] = getNewTree(arrayOfInstances1[b], arrayOfInstances2[b]);
        arrayOfInstances1[b] = null;
        arrayOfInstances2[b] = null;
      } 
    } else {
      this.m_isLeaf = true;
      if (Utils.eq(paramInstances1.sumOfWeights(), 0.0D))
        this.m_isEmpty = true; 
      paramInstances1 = paramInstances2 = null;
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
    return b1;
  }
  
  public final void cleanup(Instances paramInstances) {
    this.m_train = paramInstances;
    this.m_test = null;
    if (!this.m_isLeaf)
      for (byte b = 0; b < this.m_sons.length; b++)
        this.m_sons[b].cleanup(paramInstances);  
  }
  
  public final double[] distributionForInstance(Instance paramInstance, boolean paramBoolean) throws Exception {
    double[] arrayOfDouble = new double[paramInstance.numClasses()];
    for (byte b = 0; b < arrayOfDouble.length; b++) {
      if (!paramBoolean) {
        arrayOfDouble[b] = getProbs(b, paramInstance, 1.0D);
      } else {
        arrayOfDouble[b] = getProbsLaplace(b, paramInstance, 1.0D);
      } 
    } 
    return arrayOfDouble;
  }
  
  public int assignIDs(int paramInt) {
    int i = paramInt + 1;
    this.m_id = i;
    if (this.m_sons != null)
      for (byte b = 0; b < this.m_sons.length; b++)
        i = this.m_sons[b].assignIDs(i);  
    return i;
  }
  
  public int graphType() {
    return 1;
  }
  
  public String graph() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    assignIDs(-1);
    stringBuffer.append("digraph J48Tree {\n");
    if (this.m_isLeaf) {
      stringBuffer.append("N" + this.m_id + " [label=\"" + this.m_localModel.dumpLabel(0, this.m_train) + "\" " + "shape=box style=filled ");
      if (this.m_train != null && this.m_train.numInstances() > 0) {
        stringBuffer.append("data =\n" + this.m_train + "\n");
        stringBuffer.append(",\n");
      } 
      stringBuffer.append("]\n");
    } else {
      stringBuffer.append("N" + this.m_id + " [label=\"" + this.m_localModel.leftSide(this.m_train) + "\" ");
      if (this.m_train != null && this.m_train.numInstances() > 0) {
        stringBuffer.append("data =\n" + this.m_train + "\n");
        stringBuffer.append(",\n");
      } 
      stringBuffer.append("]\n");
      graphTree(stringBuffer);
    } 
    return stringBuffer.toString() + "}\n";
  }
  
  public String prefix() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_isLeaf) {
      stringBuffer.append("[" + this.m_localModel.dumpLabel(0, this.m_train) + "]");
    } else {
      prefixTree(stringBuffer);
    } 
    return stringBuffer.toString();
  }
  
  public StringBuffer[] toSource(String paramString) throws Exception {
    StringBuffer[] arrayOfStringBuffer = new StringBuffer[2];
    if (this.m_isLeaf) {
      arrayOfStringBuffer[0] = new StringBuffer("    p = " + this.m_localModel.distribution().maxClass(0) + ";\n");
      arrayOfStringBuffer[1] = new StringBuffer("");
    } else {
      StringBuffer stringBuffer1 = new StringBuffer();
      String str = "      ";
      StringBuffer stringBuffer2 = new StringBuffer();
      long l = nextID();
      stringBuffer1.append("  static double N").append(Integer.toHexString(this.m_localModel.hashCode()) + l).append("(Object []i) {\n").append("    double p = Double.NaN;\n");
      stringBuffer1.append("    if (").append(this.m_localModel.sourceExpression(-1, this.m_train)).append(") {\n");
      stringBuffer1.append("      p = ").append(this.m_localModel.distribution().maxClass(0)).append(";\n");
      stringBuffer1.append("    } ");
      for (byte b = 0; b < this.m_sons.length; b++) {
        stringBuffer1.append("else if (" + this.m_localModel.sourceExpression(b, this.m_train) + ") {\n");
        if ((this.m_sons[b]).m_isLeaf) {
          stringBuffer1.append("      p = " + this.m_localModel.distribution().maxClass(b) + ";\n");
        } else {
          StringBuffer[] arrayOfStringBuffer1 = this.m_sons[b].toSource(paramString);
          stringBuffer1.append(arrayOfStringBuffer1[0]);
          stringBuffer2.append(arrayOfStringBuffer1[1]);
        } 
        stringBuffer1.append("    } ");
        if (b == this.m_sons.length - 1)
          stringBuffer1.append('\n'); 
      } 
      stringBuffer1.append("    return p;\n  }\n");
      arrayOfStringBuffer[0] = new StringBuffer("    p = " + paramString + ".N");
      arrayOfStringBuffer[0].append(Integer.toHexString(this.m_localModel.hashCode()) + l).append("(i);\n");
      arrayOfStringBuffer[1] = stringBuffer1.append(stringBuffer2);
    } 
    return arrayOfStringBuffer;
  }
  
  public int numLeaves() {
    int i = 0;
    if (this.m_isLeaf)
      return 1; 
    for (byte b = 0; b < this.m_sons.length; b++)
      i += this.m_sons[b].numLeaves(); 
    return i;
  }
  
  public int numNodes() {
    int i = 1;
    if (!this.m_isLeaf)
      for (byte b = 0; b < this.m_sons.length; b++)
        i += this.m_sons[b].numNodes();  
    return i;
  }
  
  public String toString() {
    try {
      StringBuffer stringBuffer = new StringBuffer();
      if (this.m_isLeaf) {
        stringBuffer.append(": ");
        stringBuffer.append(this.m_localModel.dumpLabel(0, this.m_train));
      } else {
        dumpTree(0, stringBuffer);
      } 
      stringBuffer.append("\n\nNumber of Leaves  : \t" + numLeaves() + "\n");
      stringBuffer.append("\nSize of the tree : \t" + numNodes() + "\n");
      return stringBuffer.toString();
    } catch (Exception exception) {
      return "Can't print classification tree.";
    } 
  }
  
  protected ClassifierTree getNewTree(Instances paramInstances) throws Exception {
    ClassifierTree classifierTree = new ClassifierTree(this.m_toSelectModel);
    classifierTree.buildTree(paramInstances, false);
    return classifierTree;
  }
  
  protected ClassifierTree getNewTree(Instances paramInstances1, Instances paramInstances2) throws Exception {
    ClassifierTree classifierTree = new ClassifierTree(this.m_toSelectModel);
    classifierTree.buildTree(paramInstances1, paramInstances2, false);
    return classifierTree;
  }
  
  private void dumpTree(int paramInt, StringBuffer paramStringBuffer) throws Exception {
    for (byte b = 0; b < this.m_sons.length; b++) {
      paramStringBuffer.append("\n");
      for (byte b1 = 0; b1 < paramInt; b1++)
        paramStringBuffer.append("|   "); 
      paramStringBuffer.append(this.m_localModel.leftSide(this.m_train));
      paramStringBuffer.append(this.m_localModel.rightSide(b, this.m_train));
      if ((this.m_sons[b]).m_isLeaf) {
        paramStringBuffer.append(": ");
        paramStringBuffer.append(this.m_localModel.dumpLabel(b, this.m_train));
      } else {
        this.m_sons[b].dumpTree(paramInt + 1, paramStringBuffer);
      } 
    } 
  }
  
  private void graphTree(StringBuffer paramStringBuffer) throws Exception {
    for (byte b = 0; b < this.m_sons.length; b++) {
      paramStringBuffer.append("N" + this.m_id + "->" + "N" + (this.m_sons[b]).m_id + " [label=\"" + this.m_localModel.rightSide(b, this.m_train).trim() + "\"]\n");
      if ((this.m_sons[b]).m_isLeaf) {
        paramStringBuffer.append("N" + (this.m_sons[b]).m_id + " [label=\"" + this.m_localModel.dumpLabel(b, this.m_train) + "\" " + "shape=box style=filled ");
        if (this.m_train != null && this.m_train.numInstances() > 0) {
          paramStringBuffer.append("data =\n" + (this.m_sons[b]).m_train + "\n");
          paramStringBuffer.append(",\n");
        } 
        paramStringBuffer.append("]\n");
      } else {
        paramStringBuffer.append("N" + (this.m_sons[b]).m_id + " [label=\"" + (this.m_sons[b]).m_localModel.leftSide(this.m_train) + "\" ");
        if (this.m_train != null && this.m_train.numInstances() > 0) {
          paramStringBuffer.append("data =\n" + (this.m_sons[b]).m_train + "\n");
          paramStringBuffer.append(",\n");
        } 
        paramStringBuffer.append("]\n");
        this.m_sons[b].graphTree(paramStringBuffer);
      } 
    } 
  }
  
  private void prefixTree(StringBuffer paramStringBuffer) throws Exception {
    paramStringBuffer.append("[");
    paramStringBuffer.append(this.m_localModel.leftSide(this.m_train) + ":");
    byte b;
    for (b = 0; b < this.m_sons.length; b++) {
      if (b > 0)
        paramStringBuffer.append(",\n"); 
      paramStringBuffer.append(this.m_localModel.rightSide(b, this.m_train));
    } 
    for (b = 0; b < this.m_sons.length; b++) {
      if ((this.m_sons[b]).m_isLeaf) {
        paramStringBuffer.append("[");
        paramStringBuffer.append(this.m_localModel.dumpLabel(b, this.m_train));
        paramStringBuffer.append("]");
      } else {
        this.m_sons[b].prefixTree(paramStringBuffer);
      } 
    } 
    paramStringBuffer.append("]");
  }
  
  private double getProbsLaplace(int paramInt, Instance paramInstance, double paramDouble) throws Exception {
    double d = 0.0D;
    if (this.m_isLeaf)
      return paramDouble * localModel().classProbLaplace(paramInt, paramInstance, -1); 
    int i = localModel().whichSubset(paramInstance);
    if (i == -1) {
      double[] arrayOfDouble = localModel().weights(paramInstance);
      for (byte b = 0; b < this.m_sons.length; b++) {
        if (!(son(b)).m_isEmpty)
          if (!(son(b)).m_isLeaf) {
            d += son(b).getProbsLaplace(paramInt, paramInstance, arrayOfDouble[b] * paramDouble);
          } else {
            d += paramDouble * arrayOfDouble[b] * localModel().classProbLaplace(paramInt, paramInstance, b);
          }  
      } 
      return d;
    } 
    return (son(i)).m_isLeaf ? (paramDouble * localModel().classProbLaplace(paramInt, paramInstance, i)) : son(i).getProbsLaplace(paramInt, paramInstance, paramDouble);
  }
  
  private double getProbs(int paramInt, Instance paramInstance, double paramDouble) throws Exception {
    double d = 0.0D;
    if (this.m_isLeaf)
      return paramDouble * localModel().classProb(paramInt, paramInstance, -1); 
    int i = localModel().whichSubset(paramInstance);
    if (i == -1) {
      double[] arrayOfDouble = localModel().weights(paramInstance);
      for (byte b = 0; b < this.m_sons.length; b++) {
        if (!(son(b)).m_isEmpty)
          d += son(b).getProbs(paramInt, paramInstance, arrayOfDouble[b] * paramDouble); 
      } 
      return d;
    } 
    return (son(i)).m_isEmpty ? (paramDouble * localModel().classProb(paramInt, paramInstance, i)) : son(i).getProbs(paramInt, paramInstance, paramDouble);
  }
  
  private ClassifierSplitModel localModel() {
    return this.m_localModel;
  }
  
  private ClassifierTree son(int paramInt) {
    return this.m_sons[paramInt];
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\ClassifierTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */