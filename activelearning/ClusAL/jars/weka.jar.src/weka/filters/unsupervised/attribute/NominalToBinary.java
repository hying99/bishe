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
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class NominalToBinary extends Filter implements UnsupervisedFilter, OptionHandler {
  protected Range m_Columns = new Range();
  
  private int[][] m_Indices = (int[][])null;
  
  private boolean m_Numeric = true;
  
  public NominalToBinary() {
    setAttributeIndices("first-last");
  }
  
  public String globalInfo() {
    return "Converts all nominal attributes into binary numeric attributes. An attribute with k values is transformed into k binary attributes if the class is nominal (using the one-attribute-per-value approach). Binary attributes are left binary.If the class is numeric, you might want to use the supervised version of this filter.";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_Columns.setUpper(paramInstances.numAttributes() - 1);
    setOutputFormat();
    this.m_Indices = (int[][])null;
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    convertInstance(paramInstance);
    return true;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tSets if binary attributes are to be coded as nominal ones.", "N", 0, "-N"));
    vector.addElement(new Option("\tSpecifies list of columns to act on. First and last are valid indexes.\n\t(default: first-last)", "R", 1, "-R <col1,col2-col4,...>"));
    vector.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setBinaryAttributesNominal(Utils.getFlag('N', paramArrayOfString));
    String str = Utils.getOption('R', paramArrayOfString);
    if (str.length() != 0) {
      setAttributeIndices(str);
    } else {
      setAttributeIndices("first-last");
    } 
    setInvertSelection(Utils.getFlag('V', paramArrayOfString));
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[4];
    byte b = 0;
    if (getBinaryAttributesNominal())
      arrayOfString[b++] = "-N"; 
    if (!getAttributeIndices().equals("")) {
      arrayOfString[b++] = "-R";
      arrayOfString[b++] = getAttributeIndices();
    } 
    if (getInvertSelection())
      arrayOfString[b++] = "-V"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String binaryAttributesNominalTipText() {
    return "Whether resulting binary attributes will be nominal.";
  }
  
  public boolean getBinaryAttributesNominal() {
    return !this.m_Numeric;
  }
  
  public void setBinaryAttributesNominal(boolean paramBoolean) {
    this.m_Numeric = !paramBoolean;
  }
  
  public String invertSelectionTipText() {
    return "Set attribute selection mode. If false, only selected (numeric) attributes in the range will be discretized; if true, only non-selected attributes will be discretized.";
  }
  
  public boolean getInvertSelection() {
    return this.m_Columns.getInvert();
  }
  
  public void setInvertSelection(boolean paramBoolean) {
    this.m_Columns.setInvert(paramBoolean);
  }
  
  public String attributeIndicesTipText() {
    return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
  }
  
  public String getAttributeIndices() {
    return this.m_Columns.getRanges();
  }
  
  public void setAttributeIndices(String paramString) {
    this.m_Columns.setRanges(paramString);
  }
  
  private void setOutputFormat() {
    int i = getInputFormat().classIndex();
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (!attribute.isNominal() || b == getInputFormat().classIndex() || !this.m_Columns.isInRange(b)) {
        fastVector.addElement(attribute.copy());
      } else if (attribute.numValues() <= 2) {
        if (this.m_Numeric) {
          fastVector.addElement(new Attribute(attribute.name()));
        } else {
          fastVector.addElement(attribute.copy());
        } 
      } else {
        if (i >= 0 && b < getInputFormat().classIndex())
          i += attribute.numValues() - 1; 
        for (byte b1 = 0; b1 < attribute.numValues(); b1++) {
          StringBuffer stringBuffer = new StringBuffer(attribute.name() + "=");
          stringBuffer.append(attribute.value(b1));
          if (this.m_Numeric) {
            fastVector.addElement(new Attribute(stringBuffer.toString()));
          } else {
            FastVector fastVector1 = new FastVector(2);
            fastVector1.addElement("f");
            fastVector1.addElement("t");
            fastVector.addElement(new Attribute(stringBuffer.toString(), fastVector1));
          } 
        } 
      } 
    } 
    Instances instances = new Instances(getInputFormat().relationName(), fastVector, 0);
    instances.setClassIndex(i);
    setOutputFormat(instances);
  }
  
  private void convertInstance(Instance paramInstance) {
    Instance instance;
    double[] arrayOfDouble = new double[outputFormatPeek().numAttributes()];
    int i = 0;
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (!attribute.isNominal() || b == getInputFormat().classIndex() || !this.m_Columns.isInRange(b)) {
        arrayOfDouble[i] = paramInstance.value(b);
        i++;
      } else if (attribute.numValues() <= 2) {
        arrayOfDouble[i] = paramInstance.value(b);
        i++;
      } else {
        if (paramInstance.isMissing(b)) {
          for (byte b1 = 0; b1 < attribute.numValues(); b1++)
            arrayOfDouble[i + b1] = paramInstance.value(b); 
        } else {
          for (byte b1 = 0; b1 < attribute.numValues(); b1++) {
            if (b1 == (int)paramInstance.value(b)) {
              arrayOfDouble[i + b1] = 1.0D;
            } else {
              arrayOfDouble[i + b1] = 0.0D;
            } 
          } 
        } 
        i += attribute.numValues();
      } 
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
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new NominalToBinary(), paramArrayOfString);
      } else {
        Filter.filterFile(new NominalToBinary(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\NominalToBinary.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */