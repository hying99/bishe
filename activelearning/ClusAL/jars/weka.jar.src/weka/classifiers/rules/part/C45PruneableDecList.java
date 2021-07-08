package weka.classifiers.rules.part;

import weka.classifiers.trees.j48.ClassifierSplitModel;
import weka.classifiers.trees.j48.Distribution;
import weka.classifiers.trees.j48.ModelSelection;
import weka.classifiers.trees.j48.NoSplit;
import weka.classifiers.trees.j48.Stats;
import weka.core.Instances;
import weka.core.Utils;

public class C45PruneableDecList extends ClassifierDecList {
  private double CF = 0.25D;
  
  public C45PruneableDecList(ModelSelection paramModelSelection, double paramDouble, int paramInt) throws Exception {
    super(paramModelSelection, paramInt);
    this.CF = paramDouble;
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
      byte b1 = 0;
      do {
        b1++;
        i = chooseIndex();
        if (i == -1) {
          for (byte b = 0; b < this.m_sons.length; b++) {
            if (this.m_sons[b] == null)
              this.m_sons[b] = getNewDecList(arrayOfInstances[b], true); 
          } 
          if (b1 < 2) {
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
      } while (b1 < this.m_sons.length && (this.m_sons[i]).m_isLeaf);
      byte b2;
      for (b2 = 0; b2 < this.m_sons.length && this.m_sons[b2] != null && (this.m_sons[b2]).m_isLeaf; b2++);
      if (b2 == this.m_sons.length) {
        pruneEnd();
        if (!this.m_isLeaf)
          this.indeX = chooseLastIndex(); 
      } else {
        this.indeX = chooseLastIndex();
      } 
    } else {
      this.m_isLeaf = true;
      if (Utils.eq(d, 0.0D))
        this.m_isEmpty = true; 
    } 
  }
  
  protected ClassifierDecList getNewDecList(Instances paramInstances, boolean paramBoolean) throws Exception {
    C45PruneableDecList c45PruneableDecList = new C45PruneableDecList(this.m_toSelectModel, this.CF, this.m_minNumObj);
    c45PruneableDecList.buildDecList(paramInstances, paramBoolean);
    return c45PruneableDecList;
  }
  
  protected void pruneEnd() {
    double d2 = getEstimatedErrorsForTree();
    double d1 = getEstimatedErrorsForLeaf();
    if (Utils.smOrEq(d1, d2 + 0.1D)) {
      this.m_isLeaf = true;
      this.m_sons = null;
      this.m_localModel = (ClassifierSplitModel)new NoSplit(localModel().distribution());
    } 
  }
  
  private double getEstimatedErrorsForTree() {
    if (this.m_isLeaf)
      return getEstimatedErrorsForLeaf(); 
    double d = 0.0D;
    for (byte b = 0; b < this.m_sons.length; b++) {
      if (!Utils.eq(son(b).localModel().distribution().total(), 0.0D))
        d += ((C45PruneableDecList)son(b)).getEstimatedErrorsForTree(); 
    } 
    return d;
  }
  
  public double getEstimatedErrorsForLeaf() {
    double d = localModel().distribution().numIncorrect();
    return d + Stats.addErrs(localModel().distribution().total(), d, (float)this.CF);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\part\C45PruneableDecList.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */