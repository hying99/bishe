package weka.associations.tertius;

import java.util.Enumeration;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class IndividualInstances extends Instances {
  public IndividualInstances(Instances paramInstances1, Instances paramInstances2) throws Exception {
    super(paramInstances1, paramInstances1.numInstances());
    Attribute attribute1 = attribute("id");
    if (attribute1 == null)
      throw new Exception("No identifier found in individuals dataset."); 
    Attribute attribute2 = paramInstances2.attribute("id");
    if (attribute2 == null)
      throw new Exception("No identifier found in parts dataset."); 
    Enumeration enumeration = paramInstances1.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      Instances instances = new Instances(paramInstances2, 0);
      Enumeration enumeration1 = paramInstances2.enumerateInstances();
      while (enumeration1.hasMoreElements()) {
        Instance instance1 = enumeration1.nextElement();
        if (instance.value(attribute1) == instance1.value(attribute2))
          instances.add(instance1); 
      } 
      add(new IndividualInstance(instance, instances));
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\tertius\IndividualInstances.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */