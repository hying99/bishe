package weka.filters;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class AllFilter extends Filter {
  public String globalInfo() {
    return "An instance filter that passes all instances through unmodified. Primarily for testing purposes.";
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
    push((Instance)paramInstance.copy());
    return true;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new AllFilter(), paramArrayOfString);
      } else {
        Filter.filterFile(new AllFilter(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filters\AllFilter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */