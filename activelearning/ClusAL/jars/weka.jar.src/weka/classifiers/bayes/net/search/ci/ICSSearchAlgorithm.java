package weka.classifiers.bayes.net.search.ci;

import java.io.FileReader;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.core.Instances;

public class ICSSearchAlgorithm extends CISearchAlgorithm {
  private int m_nMaxCardinality = 2;
  
  String name(int paramInt) {
    return this.m_instances.attribute(paramInt).name();
  }
  
  int maxn() {
    return this.m_instances.numAttributes();
  }
  
  public void setMaxCardinality(int paramInt) {
    this.m_nMaxCardinality = paramInt;
  }
  
  public int getMaxCardinality() {
    return this.m_nMaxCardinality;
  }
  
  protected void search(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    this.m_BayesNet = paramBayesNet;
    this.m_instances = paramInstances;
    boolean[][] arrayOfBoolean1 = new boolean[maxn() + 1][];
    boolean[][] arrayOfBoolean2 = new boolean[maxn() + 1][];
    SeparationSet[][] arrayOfSeparationSet = new SeparationSet[maxn() + 1][];
    byte b;
    for (b = 0; b < maxn() + 1; b++) {
      arrayOfBoolean1[b] = new boolean[maxn()];
      arrayOfBoolean2[b] = new boolean[maxn()];
      arrayOfSeparationSet[b] = new SeparationSet[maxn()];
    } 
    calcDependencyGraph(arrayOfBoolean1, arrayOfSeparationSet);
    calcVeeNodes(arrayOfBoolean1, arrayOfBoolean2, arrayOfSeparationSet);
    calcArcDirections(arrayOfBoolean1, arrayOfBoolean2);
    for (b = 0; b < maxn(); b++) {
      ParentSet parentSet = this.m_BayesNet.getParentSet(b);
      while (parentSet.getNrOfParents() > 0)
        parentSet.deleteLastParent(this.m_instances); 
      for (byte b1 = 0; b1 < maxn(); b1++) {
        if (arrayOfBoolean2[b1][b])
          parentSet.addParent(b1, this.m_instances); 
      } 
    } 
  }
  
  void calcDependencyGraph(boolean[][] paramArrayOfboolean, SeparationSet[][] paramArrayOfSeparationSet) {
    byte b;
    for (b = 0; b < maxn(); b++) {
      for (byte b1 = 0; b1 < maxn(); b1++)
        paramArrayOfboolean[b][b1] = true; 
    } 
    for (b = 0; b < maxn(); b++)
      paramArrayOfboolean[b][b] = false; 
    for (b = 0; b <= getMaxCardinality(); b++) {
      byte b1;
      for (b1 = 0; b1 <= maxn() - 2; b1++) {
        for (int i = b1 + 1; i < maxn(); i++) {
          if (paramArrayOfboolean[b1][i]) {
            SeparationSet separationSet = existsSepSet(b1, i, b, paramArrayOfboolean);
            if (separationSet != null) {
              paramArrayOfboolean[b1][i] = false;
              paramArrayOfboolean[i][b1] = false;
              paramArrayOfSeparationSet[b1][i] = separationSet;
              paramArrayOfSeparationSet[i][b1] = separationSet;
              System.err.print("I(" + name(b1) + ", {");
              for (byte b2 = 0; b2 < b; b2++)
                System.err.print(name(separationSet.m_set[b2]) + " "); 
              System.err.print("} ," + name(i) + ")\n");
            } 
          } 
        } 
      } 
      System.err.print(b + " ");
      for (b1 = 0; b1 < maxn(); b1++)
        System.err.print(name(b1) + " "); 
      System.err.print('\n');
      for (b1 = 0; b1 < maxn(); b1++) {
        for (byte b2 = 0; b2 < maxn(); b2++) {
          if (paramArrayOfboolean[b1][b2]) {
            System.err.print("X ");
          } else {
            System.err.print(". ");
          } 
        } 
        System.err.print(name(b1) + " ");
        System.err.print('\n');
      } 
    } 
  }
  
  SeparationSet existsSepSet(int paramInt1, int paramInt2, int paramInt3, boolean[][] paramArrayOfboolean) {
    int i;
    SeparationSet separationSet = new SeparationSet(this);
    separationSet.m_set[paramInt3] = -1;
    if (paramInt3 > 0) {
      separationSet.m_set[0] = next(-1, paramInt1, paramInt2, paramArrayOfboolean);
      for (byte b = 1; b < paramInt3; b++)
        separationSet.m_set[b] = next(separationSet.m_set[b - 1], paramInt1, paramInt2, paramArrayOfboolean); 
    } 
    if (paramInt3 > 0) {
      i = maxn() - separationSet.m_set[paramInt3 - 1] - 1;
    } else {
      i = 0;
    } 
    while (i) {
      if (isConditionalIndependent(paramInt2, paramInt1, separationSet.m_set, paramInt3))
        return separationSet; 
      if (paramInt3 > 0)
        separationSet.m_set[paramInt3 - 1] = next(separationSet.m_set[paramInt3 - 1], paramInt1, paramInt2, paramArrayOfboolean); 
      for (i = paramInt3 - 1; i >= 0 && separationSet.m_set[i] >= maxn(); i = paramInt3 - 1) {
        for (i = paramInt3 - 1; i >= 0 && separationSet.m_set[i] >= maxn(); i--);
        if (i < 0)
          break; 
        separationSet.m_set[i] = next(separationSet.m_set[i], paramInt1, paramInt2, paramArrayOfboolean);
        for (int j = i + 1; j < paramInt3; j++)
          separationSet.m_set[j] = next(separationSet.m_set[j - 1], paramInt1, paramInt2, paramArrayOfboolean); 
      } 
    } 
    return null;
  }
  
  int next(int paramInt1, int paramInt2, int paramInt3, boolean[][] paramArrayOfboolean) {
    while (++paramInt1 < maxn() && (!paramArrayOfboolean[paramInt2][paramInt1] || !paramArrayOfboolean[paramInt3][paramInt1] || paramInt1 == paramInt3))
      paramInt1++; 
    return paramInt1;
  }
  
  void calcVeeNodes(boolean[][] paramArrayOfboolean1, boolean[][] paramArrayOfboolean2, SeparationSet[][] paramArrayOfSeparationSet) {
    byte b;
    for (b = 0; b < maxn(); b++) {
      for (byte b1 = 0; b1 < maxn(); b1++)
        paramArrayOfboolean2[b][b1] = false; 
    } 
    for (b = 0; b < maxn() - 1; b++) {
      for (int i = b + 1; i < maxn(); i++) {
        if (!paramArrayOfboolean1[b][i])
          for (byte b1 = 0; b1 < maxn(); b1++) {
            if ((((b1 != b && b1 != i && paramArrayOfboolean1[b][b1] && paramArrayOfboolean1[i][b1]) ? 1 : 0) & (!paramArrayOfSeparationSet[b][i].contains(b1) ? 1 : 0)) != 0) {
              paramArrayOfboolean2[b][b1] = true;
              paramArrayOfboolean2[i][b1] = true;
            } 
          }  
      } 
    } 
  }
  
  void calcArcDirections(boolean[][] paramArrayOfboolean1, boolean[][] paramArrayOfboolean2) {
    boolean bool;
    do {
      bool = false;
      byte b;
      for (b = 0; b < maxn(); b++) {
        for (byte b1 = 0; b1 < maxn(); b1++) {
          if (b != b1 && paramArrayOfboolean2[b][b1])
            for (byte b2 = 0; b2 < maxn(); b2++) {
              if (b != b2 && b1 != b2 && paramArrayOfboolean1[b1][b2] && !paramArrayOfboolean1[b][b2] && !paramArrayOfboolean2[b1][b2] && !paramArrayOfboolean2[b2][b1]) {
                paramArrayOfboolean2[b1][b2] = true;
                bool = true;
              } 
            }  
        } 
      } 
      for (b = 0; b < maxn(); b++) {
        for (byte b1 = 0; b1 < maxn(); b1++) {
          if (b != b1 && paramArrayOfboolean2[b][b1])
            for (byte b2 = 0; b2 < maxn(); b2++) {
              if (b != b2 && b1 != b2 && paramArrayOfboolean1[b][b2] && paramArrayOfboolean2[b1][b2] && !paramArrayOfboolean2[b][b2] && !paramArrayOfboolean2[b2][b]) {
                paramArrayOfboolean2[b][b2] = true;
                bool = true;
              } 
            }  
        } 
      } 
      for (b = 0; b < maxn(); b++) {
        for (byte b1 = 0; b1 < maxn(); b1++) {
          if (b != b1 && paramArrayOfboolean2[b][b1])
            for (byte b2 = 0; b2 < maxn(); b2++) {
              if (b2 != b && b2 != b1 && paramArrayOfboolean2[b2][b1] && !paramArrayOfboolean1[b2][b])
                for (byte b3 = 0; b3 < maxn(); b3++) {
                  if (b3 != b && b3 != b1 && b3 != b2 && paramArrayOfboolean1[b3][b] && !paramArrayOfboolean2[b3][b] && !paramArrayOfboolean2[b][b3] && paramArrayOfboolean1[b3][b1] && !paramArrayOfboolean2[b3][b1] && !paramArrayOfboolean2[b1][b3] && paramArrayOfboolean1[b3][b2] && !paramArrayOfboolean2[b3][b2] && !paramArrayOfboolean2[b2][b3]) {
                    paramArrayOfboolean2[b3][b1] = true;
                    bool = true;
                  } 
                }  
            }  
        } 
      } 
      for (b = 0; b < maxn(); b++) {
        for (byte b1 = 0; b1 < maxn(); b1++) {
          if (b != b1 && paramArrayOfboolean2[b1][b])
            for (byte b2 = 0; b2 < maxn(); b2++) {
              if (b2 != b && b2 != b1 && paramArrayOfboolean1[b2][b1] && !paramArrayOfboolean2[b2][b1] && !paramArrayOfboolean2[b1][b2] && paramArrayOfboolean1[b2][b] && !paramArrayOfboolean2[b2][b] && !paramArrayOfboolean2[b][b2])
                for (byte b3 = 0; b3 < maxn(); b3++) {
                  if (b3 != b && b3 != b1 && b3 != b2 && paramArrayOfboolean1[b3][b] && !paramArrayOfboolean2[b3][b] && !paramArrayOfboolean2[b][b3] && paramArrayOfboolean1[b3][b2] && !paramArrayOfboolean2[b3][b2] && !paramArrayOfboolean2[b2][b3]) {
                    paramArrayOfboolean2[b][b3] = true;
                    paramArrayOfboolean2[b2][b3] = true;
                    bool = true;
                  } 
                }  
            }  
        } 
      } 
      if (bool)
        continue; 
      for (b = 0; !bool && b < maxn(); b++) {
        for (byte b1 = 0; !bool && b1 < maxn(); b1++) {
          if (paramArrayOfboolean1[b][b1] && !paramArrayOfboolean2[b][b1] && !paramArrayOfboolean2[b1][b]) {
            paramArrayOfboolean2[b][b1] = true;
            bool = true;
          } 
        } 
      } 
    } while (bool);
  }
  
  public String maxCardinalityTipText() {
    return "When determining whether an edge exists a search is performed for a set Z that separates the nodes. MaxCardinality determines the maximum size of the set Z. This greatly influences the length of the search. Default value is 2.";
  }
  
  public String globalInfo() {
    return "This Bayes Network learning algorithm uses conditional independence tests to find a skeleton, finds V-nodes and applies a set of rules to find the directions of the remaining arrows.";
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      BayesNet bayesNet = new BayesNet();
      bayesNet.setSearchAlgorithm((SearchAlgorithm)new ICSSearchAlgorithm());
      Instances instances = new Instances(new FileReader("C:\\eclipse\\workspace\\weka\\data\\contact-lenses.arff"));
      instances.setClassIndex(instances.numAttributes() - 1);
      bayesNet.buildClassifier(instances);
      System.out.println(bayesNet.toString());
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  class SeparationSet {
    public int[] m_set;
    
    private final ICSSearchAlgorithm this$0;
    
    public SeparationSet(ICSSearchAlgorithm this$0) {
      this.this$0 = this$0;
      this.m_set = new int[this$0.getMaxCardinality() + 1];
    }
    
    public boolean contains(int param1Int) {
      for (byte b = 0; b < this.this$0.getMaxCardinality() && this.m_set[b] != -1; b++) {
        if (this.m_set[b] == param1Int)
          return true; 
      } 
      return false;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\ci\ICSSearchAlgorithm.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */