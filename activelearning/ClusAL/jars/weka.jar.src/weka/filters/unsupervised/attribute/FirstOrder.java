package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.SparseInstance;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class FirstOrder extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  protected Range m_DeltaCols = new Range();
  
  public String globalInfo() {
    return "This instance filter takes a range of N numeric attributes and replaces them with N-1 numeric attributes, the values of which are the difference between consecutive attribute values from the original instance. eg: \n\nOriginal attribute values\n\n   0.1, 0.2, 0.3, 0.1, 0.3\n\nNew attribute values\n\n   0.1, 0.1, -0.2, 0.2\n\nThe range of attributes used is taken in numeric order. That is, a range spec of 7-11,3-5 will use the attribute ordering 3,4,5,7,8,9,10,11 for the differences, NOT 7,8,9,10,11,3,4,5.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tSpecify list of columns to take the differences between.\n\tFirst and last are valid indexes.\n\t(default none)", "R", 1, "-R <index1,index2-index4,...>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('R', paramArrayOfString);
    if (str.length() != 0) {
      setAttributeIndices(str);
    } else {
      setAttributeIndices("");
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[2];
    byte b = 0;
    if (!getAttributeIndices().equals("")) {
      arrayOfString[b++] = "-R";
      arrayOfString[b++] = getAttributeIndices();
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_DeltaCols.setUpper(getInputFormat().numAttributes() - 1);
    byte b1 = 0;
    for (int i = getInputFormat().numAttributes() - 1; i >= 0; i--) {
      if (this.m_DeltaCols.isInRange(i)) {
        b1++;
        if (!getInputFormat().attribute(i).isNumeric())
          throw new UnsupportedAttributeTypeException("Selected attributes must be all numeric"); 
      } 
    } 
    if (b1 == 1)
      throw new Exception("Cannot select only one attribute."); 
    FastVector fastVector = new FastVector();
    boolean bool = false;
    String str = null;
    for (byte b2 = 0; b2 < paramInstances.numAttributes(); b2++) {
      if (this.m_DeltaCols.isInRange(b2)) {
        if (bool) {
          Attribute attribute = new Attribute(str);
          fastVector.addElement(attribute);
        } 
        str = paramInstances.attribute(b2).name();
        str = "'FO " + str.replace('\'', ' ').trim() + '\'';
        bool = true;
      } else {
        fastVector.addElement(paramInstances.attribute(b2).copy());
      } 
    } 
    setOutputFormat(new Instances(paramInstances.relationName(), fastVector, 0));
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    Instance instance;
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    Instances instances = outputFormatPeek();
    double[] arrayOfDouble = new double[instances.numAttributes()];
    boolean bool = false;
    double d = Instance.missingValue();
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < instances.numAttributes()) {
      if (this.m_DeltaCols.isInRange(b1)) {
        if (bool) {
          if (Instance.isMissingValue(d) || paramInstance.isMissing(b1)) {
            arrayOfDouble[b2++] = Instance.missingValue();
          } else {
            arrayOfDouble[b2++] = paramInstance.value(b1) - d;
          } 
        } else {
          bool = true;
        } 
        d = paramInstance.value(b1);
      } else {
        arrayOfDouble[b2++] = paramInstance.value(b1);
      } 
      b1++;
    } 
    SparseInstance sparseInstance = null;
    if (paramInstance instanceof SparseInstance) {
      sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble);
    } else {
      instance = new Instance(paramInstance.weight(), arrayOfDouble);
    } 
    copyStringValues(instance, false, paramInstance.dataset(), getInputStringIndex(), getOutputFormat(), getOutputStringIndex());
    instance.setDataset(getOutputFormat());
    push(instance);
    return true;
  }
  
  public String attributeIndicesTipText() {
    return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
  }
  
  public String getAttributeIndices() {
    return this.m_DeltaCols.getRanges();
  }
  
  public void setAttributeIndices(String paramString) throws Exception {
    this.m_DeltaCols.setRanges(paramString);
  }
  
  public void setAttributeIndicesArray(int[] paramArrayOfint) throws Exception {
    setAttributeIndices(Range.indicesToRangeList(paramArrayOfint));
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new FirstOrder(), paramArrayOfString);
      } else {
        Filter.filterFile(new FirstOrder(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\FirstOrder.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */