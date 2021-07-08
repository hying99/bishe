package weka.filters.unsupervised.attribute;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class NumericTransform extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  private Range m_Cols = new Range();
  
  private String m_Class = "java.lang.Math";
  
  private String m_Method = "abs";
  
  private static Class[] PARAM = new Class[] { double.class };
  
  public String globalInfo() {
    return "Transforms numeric attributes using a given transformation method.";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    if (this.m_Class == null)
      throw new IllegalStateException("No class has been set."); 
    if (this.m_Method == null)
      throw new IllegalStateException("No method has been set."); 
    super.setInputFormat(paramInstances);
    this.m_Cols.setUpper(paramInstances.numAttributes() - 1);
    setOutputFormat(paramInstances);
    return true;
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    Instance instance;
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    Method method = Class.forName(this.m_Class).getMethod(this.m_Method, new Class[] { double.class });
    double[] arrayOfDouble = new double[paramInstance.numAttributes()];
    Double[] arrayOfDouble1 = new Double[1];
    for (byte b = 0; b < paramInstance.numAttributes(); b++) {
      if (paramInstance.isMissing(b)) {
        arrayOfDouble[b] = Instance.missingValue();
      } else if (this.m_Cols.isInRange(b) && paramInstance.attribute(b).isNumeric()) {
        arrayOfDouble1[0] = new Double(paramInstance.value(b));
        Double double_ = (Double)method.invoke(null, (Object[])arrayOfDouble1);
        if (double_.isNaN() || double_.isInfinite()) {
          arrayOfDouble[b] = Instance.missingValue();
        } else {
          arrayOfDouble[b] = double_.doubleValue();
        } 
      } else {
        arrayOfDouble[b] = paramInstance.value(b);
      } 
    } 
    SparseInstance sparseInstance = null;
    if (paramInstance instanceof SparseInstance) {
      sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble);
    } else {
      instance = new Instance(paramInstance.weight(), arrayOfDouble);
    } 
    instance.setDataset(paramInstance.dataset());
    push(instance);
    return true;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tSpecify list of columns to transform. First and last are\n\tvalid indexes (default none). Non-numeric columns are \n\tskipped.", "R", 1, "-R <index1,index2-index4,...>"));
    vector.addElement(new Option("\tInvert matching sense.", "V", 0, "-V"));
    vector.addElement(new Option("\tSets the class containing transformation method.\n\t(default java.lang.Math)", "C", 1, "-C <string>"));
    vector.addElement(new Option("\tSets the method. (default abs)", "M", 1, "-M <string>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setAttributeIndices(Utils.getOption('R', paramArrayOfString));
    setInvertSelection(Utils.getFlag('V', paramArrayOfString));
    String str1 = Utils.getOption('C', paramArrayOfString);
    if (str1.length() != 0)
      setClassName(str1); 
    String str2 = Utils.getOption('M', paramArrayOfString);
    if (str2.length() != 0)
      setMethodName(str2); 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[7];
    byte b = 0;
    if (getInvertSelection())
      arrayOfString[b++] = "-V"; 
    if (!getAttributeIndices().equals("")) {
      arrayOfString[b++] = "-R";
      arrayOfString[b++] = getAttributeIndices();
    } 
    if (this.m_Class != null) {
      arrayOfString[b++] = "-C";
      arrayOfString[b++] = getClassName();
    } 
    if (this.m_Method != null) {
      arrayOfString[b++] = "-M";
      arrayOfString[b++] = getMethodName();
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String classNameTipText() {
    return "Name of the class containing the method used for the transformation.";
  }
  
  public String getClassName() {
    return this.m_Class;
  }
  
  public void setClassName(String paramString) throws ClassNotFoundException {
    this.m_Class = paramString;
  }
  
  public String methodNameTipText() {
    return "Name of the method used for the transformation.";
  }
  
  public String getMethodName() {
    return this.m_Method;
  }
  
  public void setMethodName(String paramString) throws NoSuchMethodException {
    this.m_Method = paramString;
  }
  
  public String invertSelectionTipText() {
    return "Whether to process the inverse of the given attribute ranges.";
  }
  
  public boolean getInvertSelection() {
    return this.m_Cols.getInvert();
  }
  
  public void setInvertSelection(boolean paramBoolean) {
    this.m_Cols.setInvert(paramBoolean);
  }
  
  public String attributeIndicesTipText() {
    return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
  }
  
  public String getAttributeIndices() {
    return this.m_Cols.getRanges();
  }
  
  public void setAttributeIndices(String paramString) {
    this.m_Cols.setRanges(paramString);
  }
  
  public void setAttributeIndicesArray(int[] paramArrayOfint) {
    setAttributeIndices(Range.indicesToRangeList(paramArrayOfint));
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new NumericTransform(), paramArrayOfString);
      } else {
        Filter.filterFile(new NumericTransform(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\NumericTransform.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */