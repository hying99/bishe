package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
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

public class AddCluster extends Filter implements UnsupervisedFilter, OptionHandler {
  protected Clusterer m_Clusterer = (Clusterer)new SimpleKMeans();
  
  protected Range m_IgnoreAttributesRange = null;
  
  protected Filter m_removeAttributes = new Remove();
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_removeAttributes = null;
    return false;
  }
  
  public boolean batchFinished() throws Exception {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    Instances instances1 = getInputFormat();
    Instances instances2 = instances1;
    if (this.m_IgnoreAttributesRange != null || instances1.classIndex() >= 0) {
      instances2 = new Instances(instances1);
      this.m_removeAttributes = new Remove();
      String str = "";
      if (this.m_IgnoreAttributesRange != null)
        str = str + this.m_IgnoreAttributesRange.getRanges(); 
      if (instances1.classIndex() >= 0)
        if (str.length() > 0) {
          str = str + "," + (instances1.classIndex() + 1);
        } else {
          str = "" + (instances1.classIndex() + 1);
        }  
      ((Remove)this.m_removeAttributes).setAttributeIndices(str);
      ((Remove)this.m_removeAttributes).setInvertSelection(false);
      this.m_removeAttributes.setInputFormat(instances1);
      for (byte b1 = 0; b1 < instances1.numInstances(); b1++)
        this.m_removeAttributes.input(instances1.instance(b1)); 
      this.m_removeAttributes.batchFinished();
      instances2 = this.m_removeAttributes.getOutputFormat();
      Instance instance;
      while ((instance = this.m_removeAttributes.output()) != null)
        instances2.add(instance); 
    } 
    this.m_Clusterer.buildClusterer(instances2);
    Instances instances3 = new Instances(instances1, 0);
    FastVector fastVector = new FastVector(this.m_Clusterer.numberOfClusters());
    byte b;
    for (b = 0; b < this.m_Clusterer.numberOfClusters(); b++)
      fastVector.addElement("cluster" + (b + 1)); 
    instances3.insertAttributeAt(new Attribute("cluster", fastVector), instances3.numAttributes());
    setOutputFormat(instances3);
    for (b = 0; b < instances1.numInstances(); b++)
      convertInstance(instances1.instance(b)); 
    flushInput();
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (outputFormatPeek() != null) {
      convertInstance(paramInstance);
      return true;
    } 
    bufferInput(paramInstance);
    return false;
  }
  
  protected void convertInstance(Instance paramInstance) throws Exception {
    Instance instance2;
    Instance instance1 = paramInstance;
    double[] arrayOfDouble = new double[paramInstance.numAttributes() + 1];
    for (byte b = 0; b < paramInstance.numAttributes(); b++)
      arrayOfDouble[b] = instance1.value(b); 
    Instance instance3 = null;
    if (this.m_removeAttributes != null) {
      this.m_removeAttributes.input(paramInstance);
      instance3 = this.m_removeAttributes.output();
    } else {
      instance3 = paramInstance;
    } 
    arrayOfDouble[paramInstance.numAttributes()] = this.m_Clusterer.clusterInstance(instance3);
    if (instance1 instanceof SparseInstance) {
      SparseInstance sparseInstance = new SparseInstance(instance1.weight(), arrayOfDouble);
    } else {
      instance2 = new Instance(instance1.weight(), arrayOfDouble);
    } 
    copyStringValues(instance1, false, instance1.dataset(), getOutputStringIndex(), getOutputFormat(), getOutputStringIndex());
    push(instance2);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tFull class name of clusterer to use, followed\n\tby scheme options. (required)\n\teg: \"weka.clusterers.SimpleKMeans -N 3\"", "W", 1, "-W <clusterer specification>"));
    vector.addElement(new Option("\tThe range of attributes the clusterer should ignore.\n", "I", 1, "-I <att1,att2-att4,...>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('W', paramArrayOfString);
    if (str1.length() == 0)
      throw new Exception("A clusterer must be specified with the -W option."); 
    String[] arrayOfString = Utils.splitOptions(str1);
    if (arrayOfString.length == 0)
      throw new Exception("Invalid clusterer specification string"); 
    String str2 = arrayOfString[0];
    arrayOfString[0] = "";
    setClusterer(Clusterer.forName(str2, arrayOfString));
    setIgnoredAttributeIndices(Utils.getOption('I', paramArrayOfString));
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[5];
    byte b = 0;
    arrayOfString[b++] = "-W";
    arrayOfString[b++] = "" + getClustererSpec();
    if (!getIgnoredAttributeIndices().equals("")) {
      arrayOfString[b++] = "-I";
      arrayOfString[b++] = getIgnoredAttributeIndices();
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String globalInfo() {
    return "A filter that adds a new nominal attribute representing the cluster assigned to each instance by the specified clustering algorithm.";
  }
  
  public String clustererTipText() {
    return "The clusterer to assign clusters with.";
  }
  
  public void setClusterer(Clusterer paramClusterer) {
    this.m_Clusterer = paramClusterer;
  }
  
  public Clusterer getClusterer() {
    return this.m_Clusterer;
  }
  
  protected String getClustererSpec() {
    Clusterer clusterer = getClusterer();
    return (clusterer instanceof OptionHandler) ? (clusterer.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)clusterer).getOptions())) : clusterer.getClass().getName();
  }
  
  public String ignoredAttributeIndicesTipText() {
    return "The range of attributes to be ignored by the clusterer. eg: first-3,5,9-last";
  }
  
  public String getIgnoredAttributeIndices() {
    return (this.m_IgnoreAttributesRange == null) ? "" : this.m_IgnoreAttributesRange.getRanges();
  }
  
  public void setIgnoredAttributeIndices(String paramString) {
    if (paramString == null || paramString.length() == 0) {
      this.m_IgnoreAttributesRange = null;
    } else {
      this.m_IgnoreAttributesRange = new Range();
      this.m_IgnoreAttributesRange.setRanges(paramString);
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new AddCluster(), paramArrayOfString);
      } else {
        Filter.filterFile(new AddCluster(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\AddCluster.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */