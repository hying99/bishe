package weka.classifiers.trees.j48;

import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

public class C45PruneableClassifierTree extends ClassifierTree {
  boolean m_pruneTheTree = false;
  
  float m_CF = 0.25F;
  
  boolean m_subtreeRaising = true;
  
  boolean m_cleanup = true;
  
  public C45PruneableClassifierTree(ModelSelection paramModelSelection, boolean paramBoolean1, float paramFloat, boolean paramBoolean2, boolean paramBoolean3) throws Exception {
    super(paramModelSelection);
    this.m_pruneTheTree = paramBoolean1;
    this.m_CF = paramFloat;
    this.m_subtreeRaising = paramBoolean2;
    this.m_cleanup = paramBoolean3;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Class is numeric!"); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    buildTree(paramInstances, this.m_subtreeRaising);
    collapse();
    if (this.m_pruneTheTree)
      prune(); 
    if (this.m_cleanup)
      cleanup(new Instances(paramInstances, 0)); 
  }
  
  public final void collapse() {
    if (!this.m_isLeaf) {
      double d1 = getTrainingErrors();
      double d2 = localModel().distribution().numIncorrect();
      if (d1 >= d2 - 0.001D) {
        this.m_sons = null;
        this.m_isLeaf = true;
        this.m_localModel = new NoSplit(localModel().distribution());
      } else {
        for (byte b = 0; b < this.m_sons.length; b++)
          son(b).collapse(); 
      } 
    } 
  }
  
  public void prune() throws Exception {
    if (!this.m_isLeaf) {
      double d1;
      for (byte b = 0; b < this.m_sons.length; b++)
        son(b).prune(); 
      int i = localModel().distribution().maxBag();
      if (this.m_subtreeRaising) {
        d1 = son(i).getEstimatedErrorsForBranch(this.m_train);
      } else {
        d1 = Double.MAX_VALUE;
      } 
      double d2 = getEstimatedErrorsForDistribution(localModel().distribution());
      double d3 = getEstimatedErrors();
      if (Utils.smOrEq(d2, d3 + 0.1D) && Utils.smOrEq(d2, d1 + 0.1D)) {
        this.m_sons = null;
        this.m_isLeaf = true;
        this.m_localModel = new NoSplit(localModel().distribution());
        return;
      } 
      if (Utils.smOrEq(d1, d3 + 0.1D)) {
        C45PruneableClassifierTree c45PruneableClassifierTree = son(i);
        this.m_sons = c45PruneableClassifierTree.m_sons;
        this.m_localModel = c45PruneableClassifierTree.localModel();
        this.m_isLeaf = c45PruneableClassifierTree.m_isLeaf;
        newDistribution(this.m_train);
        prune();
      } 
    } 
  }
  
  protected ClassifierTree getNewTree(Instances paramInstances) throws Exception {
    C45PruneableClassifierTree c45PruneableClassifierTree = new C45PruneableClassifierTree(this.m_toSelectModel, this.m_pruneTheTree, this.m_CF, this.m_subtreeRaising, this.m_cleanup);
    c45PruneableClassifierTree.buildTree(paramInstances, this.m_subtreeRaising);
    return c45PruneableClassifierTree;
  }
  
  private double getEstimatedErrors() {
    double d = 0.0D;
    if (this.m_isLeaf)
      return getEstimatedErrorsForDistribution(localModel().distribution()); 
    for (byte b = 0; b < this.m_sons.length; b++)
      d += son(b).getEstimatedErrors(); 
    return d;
  }
  
  private double getEstimatedErrorsForBranch(Instances paramInstances) throws Exception {
    double d = 0.0D;
    if (this.m_isLeaf)
      return getEstimatedErrorsForDistribution(new Distribution(paramInstances)); 
    Distribution distribution = (localModel()).m_distribution;
    localModel().resetDistribution(paramInstances);
    Instances[] arrayOfInstances = localModel().split(paramInstances);
    (localModel()).m_distribution = distribution;
    for (byte b = 0; b < this.m_sons.length; b++)
      d += son(b).getEstimatedErrorsForBranch(arrayOfInstances[b]); 
    return d;
  }
  
  private double getEstimatedErrorsForDistribution(Distribution paramDistribution) {
    return Utils.eq(paramDistribution.total(), 0.0D) ? 0.0D : (paramDistribution.numIncorrect() + Stats.addErrs(paramDistribution.total(), paramDistribution.numIncorrect(), this.m_CF));
  }
  
  private double getTrainingErrors() {
    double d = 0.0D;
    if (this.m_isLeaf)
      return localModel().distribution().numIncorrect(); 
    for (byte b = 0; b < this.m_sons.length; b++)
      d += son(b).getTrainingErrors(); 
    return d;
  }
  
  private ClassifierSplitModel localModel() {
    return this.m_localModel;
  }
  
  private void newDistribution(Instances paramInstances) throws Exception {
    localModel().resetDistribution(paramInstances);
    this.m_train = paramInstances;
    if (!this.m_isLeaf) {
      Instances[] arrayOfInstances = localModel().split(paramInstances);
      for (byte b = 0; b < this.m_sons.length; b++)
        son(b).newDistribution(arrayOfInstances[b]); 
    } else if (!Utils.eq(paramInstances.sumOfWeights(), 0.0D)) {
      this.m_isEmpty = false;
    } 
  }
  
  private C45PruneableClassifierTree son(int paramInt) {
    return (C45PruneableClassifierTree)this.m_sons[paramInt];
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\C45PruneableClassifierTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */