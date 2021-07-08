package weka.classifiers.rules;

import java.io.Serializable;
import weka.core.Copyable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WeightedInstancesHandler;

public abstract class Rule implements WeightedInstancesHandler, Copyable, Serializable {
  public Object copy() {
    return this;
  }
  
  public abstract boolean covers(Instance paramInstance);
  
  public abstract void grow(Instances paramInstances) throws Exception;
  
  public abstract boolean hasAntds();
  
  public abstract double getConsequent();
  
  public abstract double size();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\Rule.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */