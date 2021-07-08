package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class RemoveType extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  protected Remove m_attributeFilter = new Remove();
  
  protected int m_attTypeToDelete = 2;
  
  protected boolean m_invert = false;
  
  public static final Tag[] TAGS_ATTRIBUTETYPE = new Tag[] { new Tag(1, "Delete nominal attributes"), new Tag(0, "Delete numeric attributes"), new Tag(2, "Delete string attributes"), new Tag(3, "Delete date attributes") };
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    int[] arrayOfInt1 = new int[paramInstances.numAttributes()];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInstances.numAttributes(); b2++) {
      if ((b2 != paramInstances.classIndex() || this.m_invert) && paramInstances.attribute(b2).type() == this.m_attTypeToDelete)
        arrayOfInt1[b1++] = b2; 
    } 
    int[] arrayOfInt2 = new int[b1];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, b1);
    this.m_attributeFilter.setAttributeIndicesArray(arrayOfInt2);
    this.m_attributeFilter.setInvertSelection(this.m_invert);
    boolean bool = this.m_attributeFilter.setInputFormat(paramInstances);
    Instances instances = this.m_attributeFilter.getOutputFormat();
    instances.setRelationName(paramInstances.relationName());
    setOutputFormat(instances);
    return bool;
  }
  
  public boolean input(Instance paramInstance) {
    return this.m_attributeFilter.input(paramInstance);
  }
  
  public boolean batchFinished() throws Exception {
    return this.m_attributeFilter.batchFinished();
  }
  
  public Instance output() {
    return this.m_attributeFilter.output();
  }
  
  public Instance outputPeek() {
    return this.m_attributeFilter.outputPeek();
  }
  
  public int numPendingOutput() {
    return this.m_attributeFilter.numPendingOutput();
  }
  
  public boolean isOutputFormatDefined() {
    return this.m_attributeFilter.isOutputFormatDefined();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tAttribute type to delete. Valid options are \"nominal\", \"numeric\", \"string\" and \"date\". (default \"string\")", "T", 1, "-T <nominal|numeric|string|date>"));
    vector.addElement(new Option("\tInvert matching sense (i.e. only keep specified columns)", "V", 0, "-V"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('T', paramArrayOfString);
    if (str.length() != 0)
      setAttributeTypeString(str); 
    setInvertSelection(Utils.getFlag('V', paramArrayOfString));
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[3];
    byte b = 0;
    if (getInvertSelection())
      arrayOfString[b++] = "-V"; 
    arrayOfString[b++] = "-T";
    arrayOfString[b++] = getAttributeTypeString();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String globalInfo() {
    return "Removes attributes of a given type.";
  }
  
  public String attributeTypeTipText() {
    return "The type of attribute to remove.";
  }
  
  public void setAttributeType(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_ATTRIBUTETYPE)
      this.m_attTypeToDelete = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public SelectedTag getAttributeType() {
    return new SelectedTag(this.m_attTypeToDelete, TAGS_ATTRIBUTETYPE);
  }
  
  public String invertSelectionTipText() {
    return "Determines whether action is to select or delete. If set to true, only the specified attributes will be kept; If set to false, specified attributes will be deleted.";
  }
  
  public boolean getInvertSelection() {
    return this.m_invert;
  }
  
  public void setInvertSelection(boolean paramBoolean) {
    this.m_invert = paramBoolean;
  }
  
  protected String getAttributeTypeString() {
    return (this.m_attTypeToDelete == 1) ? "nominal" : ((this.m_attTypeToDelete == 0) ? "numeric" : ((this.m_attTypeToDelete == 2) ? "string" : ((this.m_attTypeToDelete == 3) ? "date" : "unknown")));
  }
  
  protected void setAttributeTypeString(String paramString) {
    paramString = paramString.toLowerCase();
    if (paramString.equals("nominal")) {
      this.m_attTypeToDelete = 1;
    } else if (paramString.equals("numeric")) {
      this.m_attTypeToDelete = 0;
    } else if (paramString.equals("string")) {
      this.m_attTypeToDelete = 2;
    } else if (paramString.equals("date")) {
      this.m_attTypeToDelete = 3;
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new RemoveType(), paramArrayOfString);
      } else {
        Filter.filterFile(new RemoveType(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\RemoveType.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */