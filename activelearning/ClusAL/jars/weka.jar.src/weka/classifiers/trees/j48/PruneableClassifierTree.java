package weka.classifiers.trees.j48;

import java.util.Random;
import weka.core.Instances;
import weka.core.Utils;

public class PruneableClassifierTree extends ClassifierTree {
  private boolean pruneTheTree = false;
  
  private int numSets = 3;
  
  private boolean m_cleanup = true;
  
  private int m_seed = 1;
  
  public PruneableClassifierTree(ModelSelection paramModelSelection, boolean paramBoolean1, int paramInt1, boolean paramBoolean2, int paramInt2) throws Exception {
    super(paramModelSelection);
    this.pruneTheTree = paramBoolean1;
    this.numSets = paramInt1;
    this.m_cleanup = paramBoolean2;
    this.m_seed = paramInt2;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.classAttribute().isNumeric())
      throw new Exception("Class is numeric!"); 
    paramInstances = new Instances(paramInstances);
    Random random = new Random(this.m_seed);
    paramInstances.deleteWithMissingClass();
    paramInstances.stratify(this.numSets);
    buildTree(paramInstances.trainCV(this.numSets, this.numSets - 1, random), paramInstances.testCV(this.numSets, this.numSets - 1), false);
    if (this.pruneTheTree)
      prune(); 
    if (this.m_cleanup)
      cleanup(new Instances(paramInstances, 0)); 
  }
  
  public void prune() throws Exception {
    if (!this.m_isLeaf) {
      for (byte b = 0; b < this.m_sons.length; b++)
        son(b).prune(); 
      if (Utils.smOrEq(errorsForLeaf(), errorsForTree())) {
        this.m_sons = null;
        this.m_isLeaf = true;
        this.m_localModel = new NoSplit(localModel().distribution());
      } 
    } 
  }
  
  protected ClassifierTree getNewTree(Instances paramInstances1, Instances paramInstances2) throws Exception {
    PruneableClassifierTree pruneableClassifierTree = new PruneableClassifierTree(this.m_toSelectModel, this.pruneTheTree, this.numSets, this.m_cleanup, this.m_seed);
    pruneableClassifierTree.buildTree(paramInstances1, paramInstances2, false);
    return pruneableClassifierTree;
  }
  
  private double errorsForTree() throws Exception {
    double d = 0.0D;
    if (this.m_isLeaf)
      return errorsForLeaf(); 
    for (byte b = 0; b < this.m_sons.length; b++) {
      if (Utils.eq(localModel().distribution().perBag(b), 0.0D)) {
        d += this.m_test.perBag(b) - this.m_test.perClassPerBag(b, localModel().distribution().maxClass());
      } else {
        d += son(b).errorsForTree();
      } 
    } 
    return d;
  }
  
  private double errorsForLeaf() throws Exception {
    return this.m_test.total() - this.m_test.perClass(localModel().distribution().maxClass());
  }
  
  private ClassifierSplitModel localModel() {
    return this.m_localModel;
  }
  
  private PruneableClassifierTree son(int paramInt) {
    return (PruneableClassifierTree)this.m_sons[paramInt];
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\PruneableClassifierTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */