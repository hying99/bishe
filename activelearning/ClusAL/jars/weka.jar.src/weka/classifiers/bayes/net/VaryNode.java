package weka.classifiers.bayes.net;

import java.io.Serializable;

public class VaryNode implements Serializable {
  public int m_iNode;
  
  public int m_nMCV;
  
  public ADNode[] m_ADNodes;
  
  public VaryNode(int paramInt) {
    this.m_iNode = paramInt;
  }
  
  public void getCounts(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3, int paramInt1, int paramInt2, ADNode paramADNode, boolean paramBoolean) {
    int i = paramArrayOfint2[paramInt1];
    for (byte b = 0; b < this.m_ADNodes.length; b++) {
      if (b != this.m_nMCV) {
        if (this.m_ADNodes[b] != null)
          this.m_ADNodes[b].getCounts(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3, paramInt1 + 1, paramInt2 + paramArrayOfint3[paramInt1] * b, paramBoolean); 
      } else {
        paramADNode.getCounts(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3, paramInt1 + 1, paramInt2 + paramArrayOfint3[paramInt1] * b, paramBoolean);
        for (byte b1 = 0; b1 < this.m_ADNodes.length; b1++) {
          if (b1 != this.m_nMCV && this.m_ADNodes[b1] != null)
            this.m_ADNodes[b1].getCounts(paramArrayOfint1, paramArrayOfint2, paramArrayOfint3, paramInt1 + 1, paramInt2 + paramArrayOfint3[paramInt1] * b, !paramBoolean); 
        } 
      } 
    } 
  }
  
  public void print(String paramString) {
    for (byte b = 0; b < this.m_ADNodes.length; b++) {
      System.out.print(paramString + b + ": ");
      if (this.m_ADNodes[b] == null) {
        if (b == this.m_nMCV) {
          System.out.println("MCV");
        } else {
          System.out.println("null");
        } 
      } else {
        System.out.println();
        this.m_ADNodes[b].print();
      } 
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\VaryNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */