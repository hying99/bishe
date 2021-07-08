package weka.filters.unsupervised.instance;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.SingleIndex;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class RemoveWithValues extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  private SingleIndex m_AttIndex = new SingleIndex("last");
  
  protected Range m_Values = new Range("first-last");
  
  protected double m_Value = 0.0D;
  
  protected boolean m_MatchMissingValues = false;
  
  protected boolean m_ModifyHeader = false;
  
  protected int[] m_NominalMapping;
  
  public String globalInfo() {
    return "Filters instances according to the value of an attribute.";
  }
  
  public RemoveWithValues() {
    this.m_Values.setInvert(true);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(5);
    vector.addElement(new Option("\tChoose attribute to be used for selection.", "C", 1, "-C <num>"));
    vector.addElement(new Option("\tNumeric value to be used for selection on numeric\n\tattribute.\n\tInstances with values smaller than given value will\n\tbe selected. (default 0)", "S", 1, "-S <num>"));
    vector.addElement(new Option("\tRange of label indices to be used for selection on\n\tnominal attribute.\n\tFirst and last are valid indexes. (default all values)", "L", 1, "-L <index1,index2-index4,...>"));
    vector.addElement(new Option("\tMissing values count as a match. This setting is\n\tindependent of the -V option.\n\t(default missing values don't match)", "M", 0, "-M"));
    vector.addElement(new Option("\tInvert matching sense.", "V", 0, "-V"));
    vector.addElement(new Option("\tWhen selecting on nominal attributes, removes header\n\treferences to excluded values.", "H", 0, "-H"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('C', paramArrayOfString);
    if (str1.length() != 0) {
      setAttributeIndex(str1);
    } else {
      setAttributeIndex("last");
    } 
    String str2 = Utils.getOption('S', paramArrayOfString);
    if (str2.length() != 0) {
      setSplitPoint((new Double(str2)).doubleValue());
    } else {
      setSplitPoint(0.0D);
    } 
    String str3 = Utils.getOption('L', paramArrayOfString);
    if (str3.length() != 0) {
      setNominalIndices(str3);
    } else {
      setNominalIndices("first-last");
    } 
    setInvertSelection(Utils.getFlag('V', paramArrayOfString));
    setMatchMissingValues(Utils.getFlag('M', paramArrayOfString));
    setModifyHeader(Utils.getFlag('H', paramArrayOfString));
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[8];
    byte b = 0;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSplitPoint();
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + getAttributeIndex();
    if (!getNominalIndices().equals("")) {
      arrayOfString[b++] = "-L";
      arrayOfString[b++] = getNominalIndices();
    } 
    if (getInvertSelection())
      arrayOfString[b++] = "-V"; 
    if (getModifyHeader())
      arrayOfString[b++] = "-H"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_AttIndex.setUpper(paramInstances.numAttributes() - 1);
    if (!isNumeric() && !isNominal())
      throw new UnsupportedAttributeTypeException("Can only handle numeric or nominal attributes."); 
    this.m_Values.setUpper(paramInstances.attribute(this.m_AttIndex.getIndex()).numValues() - 1);
    if (isNominal() && this.m_ModifyHeader) {
      paramInstances = new Instances(paramInstances, 0);
      Attribute attribute = paramInstances.attribute(this.m_AttIndex.getIndex());
      int[] arrayOfInt = this.m_Values.getSelection();
      FastVector fastVector = new FastVector();
      byte b;
      for (b = 0; b < arrayOfInt.length; b++)
        fastVector.addElement(attribute.value(arrayOfInt[b])); 
      paramInstances.deleteAttributeAt(this.m_AttIndex.getIndex());
      paramInstances.insertAttributeAt(new Attribute(attribute.name(), fastVector), this.m_AttIndex.getIndex());
      this.m_NominalMapping = new int[attribute.numValues()];
      for (b = 0; b < this.m_NominalMapping.length; b++) {
        boolean bool = false;
        for (byte b1 = 0; b1 < arrayOfInt.length; b1++) {
          if (arrayOfInt[b1] == b) {
            this.m_NominalMapping[b] = b1;
            bool = true;
            break;
          } 
        } 
        if (!bool)
          this.m_NominalMapping[b] = -1; 
      } 
    } 
    setOutputFormat(paramInstances);
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (paramInstance.isMissing(this.m_AttIndex.getIndex())) {
      if (!getMatchMissingValues()) {
        push((Instance)paramInstance.copy());
        return true;
      } 
      return false;
    } 
    if (isNumeric())
      if (!this.m_Values.getInvert()) {
        if (paramInstance.value(this.m_AttIndex.getIndex()) < this.m_Value) {
          push((Instance)paramInstance.copy());
          return true;
        } 
      } else if (paramInstance.value(this.m_AttIndex.getIndex()) >= this.m_Value) {
        push((Instance)paramInstance.copy());
        return true;
      }  
    if (isNominal() && this.m_Values.isInRange((int)paramInstance.value(this.m_AttIndex.getIndex()))) {
      Instance instance = (Instance)paramInstance.copy();
      if (getModifyHeader())
        instance.setValue(this.m_AttIndex.getIndex(), this.m_NominalMapping[(int)paramInstance.value(this.m_AttIndex.getIndex())]); 
      push(instance);
      return true;
    } 
    return false;
  }
  
  public boolean isNominal() {
    return (getInputFormat() == null) ? false : getInputFormat().attribute(this.m_AttIndex.getIndex()).isNominal();
  }
  
  public boolean isNumeric() {
    return (getInputFormat() == null) ? false : getInputFormat().attribute(this.m_AttIndex.getIndex()).isNumeric();
  }
  
  public String modifyHeaderTipText() {
    return "When selecting on nominal attributes, removes header references to excluded values.";
  }
  
  public boolean getModifyHeader() {
    return this.m_ModifyHeader;
  }
  
  public void setModifyHeader(boolean paramBoolean) {
    this.m_ModifyHeader = paramBoolean;
  }
  
  public String attributeIndexTipText() {
    return "Choose attribute to be used for selection (default last).";
  }
  
  public String getAttributeIndex() {
    return this.m_AttIndex.getSingleIndex();
  }
  
  public void setAttributeIndex(String paramString) {
    this.m_AttIndex.setSingleIndex(paramString);
  }
  
  public String splitPointTipText() {
    return "Numeric value to be used for selection on numeric attribute. Instances with values smaller than given value will be selected.";
  }
  
  public double getSplitPoint() {
    return this.m_Value;
  }
  
  public void setSplitPoint(double paramDouble) {
    this.m_Value = paramDouble;
  }
  
  public String matchMissingValuesTipText() {
    return "Missing values count as a match. This setting is independent of the invertSelection option.";
  }
  
  public boolean getMatchMissingValues() {
    return this.m_MatchMissingValues;
  }
  
  public void setMatchMissingValues(boolean paramBoolean) {
    this.m_MatchMissingValues = paramBoolean;
  }
  
  public String invertSelectionTipText() {
    return "Invert matching sense.";
  }
  
  public boolean getInvertSelection() {
    return !this.m_Values.getInvert();
  }
  
  public void setInvertSelection(boolean paramBoolean) {
    this.m_Values.setInvert(!paramBoolean);
  }
  
  public String nominalIndicesTipText() {
    return "Range of label indices to be used for selection on nominal attribute. First and last are valid indexes.";
  }
  
  public String getNominalIndices() {
    return this.m_Values.getRanges();
  }
  
  public void setNominalIndices(String paramString) {
    this.m_Values.setRanges(paramString);
  }
  
  public void setNominalIndicesArr(int[] paramArrayOfint) {
    String str = "";
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      if (b == 0) {
        str = "" + (paramArrayOfint[b] + 1);
      } else {
        str = str + "," + (paramArrayOfint[b] + 1);
      } 
    } 
    setNominalIndices(str);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new RemoveWithValues(), paramArrayOfString);
      } else {
        Filter.filterFile(new RemoveWithValues(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\instance\RemoveWithValues.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */