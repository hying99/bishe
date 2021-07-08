package weka.filters.unsupervised.instance;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class NonSparseToSparse extends Filter implements UnsupervisedFilter, StreamableFilter {
  public String globalInfo() {
    return "An instance filter that converts all incoming instances into sparse format.";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    SparseInstance sparseInstance = new SparseInstance(paramInstance);
    sparseInstance.setDataset(paramInstance.dataset());
    push((Instance)sparseInstance);
    return true;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new NonSparseToSparse(), paramArrayOfString);
      } else {
        Filter.filterFile(new NonSparseToSparse(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\instance\NonSparseToSparse.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */