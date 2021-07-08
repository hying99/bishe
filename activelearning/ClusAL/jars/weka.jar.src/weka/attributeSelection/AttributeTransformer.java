package weka.attributeSelection;

import weka.core.Instance;
import weka.core.Instances;

public interface AttributeTransformer {
  Instances transformedHeader() throws Exception;
  
  Instances transformedData() throws Exception;
  
  Instance convertInstance(Instance paramInstance) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\AttributeTransformer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */