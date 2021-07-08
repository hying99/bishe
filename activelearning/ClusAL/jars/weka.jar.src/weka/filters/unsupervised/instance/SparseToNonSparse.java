package weka.filters.unsupervised.instance;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class SparseToNonSparse extends Filter implements UnsupervisedFilter, StreamableFilter {
  public String globalInfo() {
    return "An instance filter that converts all incoming sparse instances into non-sparse format.";
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
    Instance instance = null;
    if (paramInstance instanceof weka.core.SparseInstance) {
      instance = new Instance(paramInstance.weight(), paramInstance.toDoubleArray());
      instance.setDataset(paramInstance.dataset());
    } else {
      instance = paramInstance;
    } 
    push(instance);
    return true;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new SparseToNonSparse(), paramArrayOfString);
      } else {
        Filter.filterFile(new SparseToNonSparse(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\instance\SparseToNonSparse.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */