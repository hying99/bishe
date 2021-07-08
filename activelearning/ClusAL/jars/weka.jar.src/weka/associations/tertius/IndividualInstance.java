package weka.associations.tertius;

import weka.core.Instance;
import weka.core.Instances;

public class IndividualInstance extends Instance {
  private Instances m_parts;
  
  public IndividualInstance(Instance paramInstance, Instances paramInstances) {
    super(paramInstance);
    this.m_parts = paramInstances;
  }
  
  public IndividualInstance(IndividualInstance paramIndividualInstance) {
    super(paramIndividualInstance);
    this.m_parts = paramIndividualInstance.m_parts;
  }
  
  public Object copy() {
    IndividualInstance individualInstance = new IndividualInstance(this);
    individualInstance.m_Dataset = this.m_Dataset;
    return individualInstance;
  }
  
  public Instances getParts() {
    return this.m_parts;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\tertius\IndividualInstance.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */