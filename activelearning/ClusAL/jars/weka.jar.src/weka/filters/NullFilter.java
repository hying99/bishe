package weka.filters;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class NullFilter extends Filter {
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    return false;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new NullFilter(), paramArrayOfString);
      } else {
        Filter.filterFile(new NullFilter(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filters\NullFilter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */