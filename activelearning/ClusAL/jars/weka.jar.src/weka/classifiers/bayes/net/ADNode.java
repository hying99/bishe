package weka.classifiers.bayes.net;

import java.io.FileReader;
import java.io.Serializable;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class ADNode implements Serializable {
  static final int MIN_RECORD_SIZE = 0;
  
  public VaryNode[] m_VaryNodes;
  
  public Instance[] m_Instances;
  
  public int m_nCount;
  
  public int m_nStartNode;
  
  public static VaryNode makeVaryNode(int paramInt, FastVector paramFastVector, Instances paramInstances) {
    VaryNode varyNode = new VaryNode(paramInt);
    int i = paramInstances.attribute(paramInt).numValues();
    FastVector[] arrayOfFastVector = new FastVector[i];
    int j;
    for (j = 0; j < i; j++)
      arrayOfFastVector[j] = new FastVector(); 
    for (j = 0; j < paramFastVector.size(); j++) {
      int k = ((Integer)paramFastVector.elementAt(j)).intValue();
      arrayOfFastVector[(int)paramInstances.instance(k).value(paramInt)].addElement(new Integer(k));
    } 
    j = arrayOfFastVector[0].size();
    byte b1 = 0;
    byte b2;
    for (b2 = 1; b2 < i; b2++) {
      if (arrayOfFastVector[b2].size() > j) {
        j = arrayOfFastVector[b2].size();
        b1 = b2;
      } 
    } 
    varyNode.m_nMCV = b1;
    varyNode.m_ADNodes = new ADNode[i];
    for (b2 = 0; b2 < i; b2++) {
      if (b2 == b1 || arrayOfFastVector[b2].size() == 0) {
        varyNode.m_ADNodes[b2] = null;
      } else {
        varyNode.m_ADNodes[b2] = makeADTree(paramInt + 1, arrayOfFastVector[b2], paramInstances);
      } 
    } 
    return varyNode;
  }
  
  public static ADNode makeADTree(int paramInt, FastVector paramFastVector, Instances paramInstances) {
    ADNode aDNode = new ADNode();
    aDNode.m_nCount = paramFastVector.size();
    aDNode.m_nStartNode = paramInt;
    if (paramFastVector.size() < 0) {
      aDNode.m_Instances = new Instance[paramFastVector.size()];
      for (byte b = 0; b < paramFastVector.size(); b++)
        aDNode.m_Instances[b] = paramInstances.instance(((Integer)paramFastVector.elementAt(b)).intValue()); 
    } else {
      aDNode.m_VaryNodes = new VaryNode[paramInstances.numAttributes() - paramInt];
      for (int i = paramInt; i < paramInstances.numAttributes(); i++)
        aDNode.m_VaryNodes[i - paramInt] = makeVaryNode(i, paramFastVector, paramInstances); 
    } 
    return aDNode;
  }
  
  public static ADNode makeADTree(Instances paramInstances) {
    FastVector fastVector = new FastVector(paramInstances.numInstances());
    for (byte b = 0; b < paramInstances.numInstances(); b++)
      fastVector.addElement(new Integer(b)); 
    return makeADTree(0, fastVector, paramInstances);
  }
  
  public void getCounts(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3, int paramInt1, int paramInt2, boolean paramBoolean) {
    if (paramInt1 >= paramArrayOfint2.length) {
      if (paramBoolean) {
        paramArrayOfint1[paramInt2] = paramArrayOfint1[paramInt2] - this.m_nCount;
      } else {
        paramArrayOfint1[paramInt2] = paramArrayOfint1[paramInt2] + this.m_nCount;
      } 
      return;
    } 
    if (this.m_VaryNodes != null) {
      this.m_VaryNodes[paramArrayOfint2[paramInt1] - this.m_nStartNode].getCounts(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3, paramInt1, paramInt2, this, paramBoolean);
    } else {
      for (byte b = 0; b < this.m_Instances.length; b++) {
        int i = paramInt2;
        Instance instance = this.m_Instances[b];
        for (int j = paramInt1; j < paramArrayOfint2.length; j++)
          i += paramArrayOfint3[j] * (int)instance.value(paramArrayOfint2[j]); 
        if (paramBoolean) {
          paramArrayOfint1[i] = paramArrayOfint1[i] - 1;
        } else {
          paramArrayOfint1[i] = paramArrayOfint1[i] + 1;
        } 
      } 
    } 
  }
  
  public void print() {
    String str = new String();
    byte b;
    for (b = 0; b < this.m_nStartNode; b++)
      str = str + "  "; 
    System.out.println(str + "Count = " + this.m_nCount);
    if (this.m_VaryNodes != null) {
      for (b = 0; b < this.m_VaryNodes.length; b++) {
        System.out.println(str + "Node " + (b + this.m_nStartNode));
        this.m_VaryNodes[b].print(str);
      } 
    } else {
      System.out.println(this.m_Instances);
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Instances instances = new Instances(new FileReader("\\iris.2.arff"));
      ADNode aDNode = makeADTree(instances);
      int[] arrayOfInt1 = new int[12];
      int[] arrayOfInt2 = new int[3];
      int[] arrayOfInt3 = new int[3];
      arrayOfInt2[0] = 0;
      arrayOfInt2[1] = 3;
      arrayOfInt2[2] = 4;
      arrayOfInt3[0] = 2;
      arrayOfInt3[1] = 1;
      arrayOfInt3[2] = 4;
      aDNode.print();
      aDNode.getCounts(arrayOfInt1, arrayOfInt2, arrayOfInt3, 0, 0, false);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\ADNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */