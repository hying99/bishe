package weka.classifiers.rules.part;

import weka.classifiers.trees.j48.ClassifierSplitModel;
import weka.classifiers.trees.j48.Distribution;
import weka.classifiers.trees.j48.ModelSelection;
import weka.classifiers.trees.j48.NoSplit;
import weka.core.Instances;
import weka.core.Utils;

public class PruneableDecList extends ClassifierDecList {
  public PruneableDecList(ModelSelection paramModelSelection, int paramInt) {
    super(paramModelSelection, paramInt);
  }
  
  public void buildRule(Instances paramInstances1, Instances paramInstances2) throws Exception {
    buildDecList(paramInstances1, paramInstances2, false);
    cleanup(new Instances(paramInstances1, 0));
  }
  
  public void buildDecList(Instances paramInstances1, Instances paramInstances2, boolean paramBoolean) throws Exception {
    this.m_train = null;
    this.m_isLeaf = false;
    this.m_isEmpty = false;
    this.m_sons = null;
    this.indeX = 0;
    double d = paramInstances1.sumOfWeights();
    NoSplit noSplit = new NoSplit(new Distribution(paramInstances1));
    if (paramBoolean) {
      this.m_localModel = (ClassifierSplitModel)noSplit;
    } else {
      this.m_localModel = this.m_toSelectModel.selectModel(paramInstances1, paramInstances2);
    } 
    this.m_test = new Distribution(paramInstances2, this.m_localModel);
    if (this.m_localModel.numSubsets() > 1) {
      int i;
      Instances[] arrayOfInstances1 = this.m_localModel.split(paramInstances1);
      Instances[] arrayOfInstances2 = this.m_localModel.split(paramInstances2);
      paramInstances1 = null;
      paramInstances2 = null;
      this.m_sons = new ClassifierDecList[this.m_localModel.numSubsets()];
      byte b1 = 0;
      do {
        b1++;
        i = chooseIndex();
        if (i == -1) {
          for (byte b = 0; b < this.m_sons.length; b++) {
            if (this.m_sons[b] == null)
              this.m_sons[b] = getNewDecList(arrayOfInstances1[b], arrayOfInstances2[b], true); 
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
        this.m_sons[i] = getNewDecList(arrayOfInstances1[i], arrayOfInstances2[i], false);
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
  
  protected ClassifierDecList getNewDecList(Instances paramInstances1, Instances paramInstances2, boolean paramBoolean) throws Exception {
    PruneableDecList pruneableDecList = new PruneableDecList(this.m_toSelectModel, this.m_minNumObj);
    pruneableDecList.buildDecList(paramInstances1, paramInstances2, paramBoolean);
    return pruneableDecList;
  }
  
  protected void pruneEnd() throws Exception {
    double d2 = errorsForTree();
    double d1 = errorsForLeaf();
    if (Utils.smOrEq(d1, d2)) {
      this.m_isLeaf = true;
      this.m_sons = null;
      this.m_localModel = (ClassifierSplitModel)new NoSplit(localModel().distribution());
    } 
  }
  
  private double errorsForTree() throws Exception {
    if (this.m_isLeaf)
      return errorsForLeaf(); 
    double d = 0.0D;
    for (byte b = 0; b < this.m_sons.length; b++) {
      if (Utils.eq(son(b).localModel().distribution().total(), 0.0D)) {
        d += this.m_test.perBag(b) - this.m_test.perClassPerBag(b, localModel().distribution().maxClass());
      } else {
        d += ((PruneableDecList)son(b)).errorsForTree();
      } 
    } 
    return d;
  }
  
  private double errorsForLeaf() throws Exception {
    return this.m_test.total() - this.m_test.perClass(localModel().distribution().maxClass());
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\part\PruneableDecList.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */