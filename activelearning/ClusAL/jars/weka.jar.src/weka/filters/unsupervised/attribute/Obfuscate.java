package weka.filters.unsupervised.attribute;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class Obfuscate extends Filter implements UnsupervisedFilter, StreamableFilter {
  public String globalInfo() {
    return "An instance filter that obfuscates all strings in the data";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      FastVector fastVector1;
      byte b1;
      Attribute attribute1 = paramInstances.attribute(b);
      Attribute attribute2 = null;
      switch (attribute1.type()) {
        case 0:
          attribute2 = new Attribute("A" + (b + 1));
          break;
        case 1:
          fastVector1 = new FastVector();
          for (b1 = 0; b1 < attribute1.numValues(); b1++)
            fastVector1.addElement("V" + (b1 + 1)); 
          attribute2 = new Attribute("A" + (b + 1), fastVector1);
          break;
        default:
          attribute2 = (Attribute)attribute1.copy();
          System.err.println("Not converting attribute: " + attribute1.name());
          break;
      } 
      fastVector.addElement(attribute2);
    } 
    Instances instances = new Instances("R", fastVector, 10);
    setOutputFormat(instances);
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
        Filter.batchFilterFile(new Obfuscate(), paramArrayOfString);
      } else {
        Filter.filterFile(new Obfuscate(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\Obfuscate.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */