package weka.filters.unsupervised.attribute;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;

public class TimeSeriesTranslate extends AbstractTimeSeries {
  public String globalInfo() {
    return "An instance filter that assumes instances form time-series data and replaces attribute values in the current instance with the equivalent attribute attribute values of some previous (or future) instance. For instances where the desired value is unknown either the instance may  be dropped, or missing values used.";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    Instances instances = new Instances(paramInstances, 0);
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      if (this.m_SelectedCols.isInRange(b))
        if (instances.attribute(b).isNominal() || instances.attribute(b).isNumeric()) {
          instances.renameAttribute(b, instances.attribute(b).name() + ((this.m_InstanceRange < 0) ? 45 : 43) + Math.abs(this.m_InstanceRange));
        } else {
          throw new UnsupportedAttributeTypeException("Only numeric and nominal attributes may be  manipulated in time series.");
        }  
    } 
    setOutputFormat(instances);
    return true;
  }
  
  protected Instance mergeInstances(Instance paramInstance1, Instance paramInstance2) {
    Instance instance;
    Instances instances = outputFormatPeek();
    double[] arrayOfDouble = new double[instances.numAttributes()];
    for (byte b = 0; b < arrayOfDouble.length; b++) {
      if (this.m_SelectedCols.isInRange(b)) {
        if (paramInstance1 != null) {
          arrayOfDouble[b] = paramInstance1.value(b);
        } else {
          arrayOfDouble[b] = Instance.missingValue();
        } 
      } else {
        arrayOfDouble[b] = paramInstance2.value(b);
      } 
    } 
    SparseInstance sparseInstance = null;
    if (paramInstance2 instanceof SparseInstance) {
      sparseInstance = new SparseInstance(paramInstance2.weight(), arrayOfDouble);
    } else {
      instance = new Instance(paramInstance2.weight(), arrayOfDouble);
    } 
    instance.setDataset(paramInstance2.dataset());
    return instance;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new TimeSeriesTranslate(), paramArrayOfString);
      } else {
        Filter.filterFile(new TimeSeriesTranslate(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\TimeSeriesTranslate.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */