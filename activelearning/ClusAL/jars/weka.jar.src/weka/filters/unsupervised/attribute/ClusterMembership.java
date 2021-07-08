package weka.filters.unsupervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.clusterers.DensityBasedClusterer;
import weka.clusterers.EM;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class ClusterMembership extends Filter implements UnsupervisedFilter, OptionHandler {
  protected DensityBasedClusterer m_clusterer = (DensityBasedClusterer)new EM();
  
  protected DensityBasedClusterer[] m_clusterers;
  
  protected Range m_ignoreAttributesRange;
  
  protected Filter m_removeAttributes;
  
  protected double[] m_priors;
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    this.m_removeAttributes = null;
    this.m_priors = null;
    return false;
  }
  
  public boolean batchFinished() throws Exception {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (outputFormatPeek() == null) {
      Instances[] arrayOfInstances;
      Instances instances1 = getInputFormat();
      if (instances1.classIndex() >= 0 && instances1.classAttribute().isNominal()) {
        arrayOfInstances = new Instances[instances1.numClasses()];
        byte b;
        for (b = 0; b < instances1.numClasses(); b++)
          arrayOfInstances[b] = new Instances(instances1, instances1.numInstances()); 
        for (b = 0; b < instances1.numInstances(); b++)
          arrayOfInstances[(int)instances1.instance(b).classValue()].add(instances1.instance(b)); 
        this.m_priors = new double[instances1.numClasses()];
        for (b = 0; b < instances1.numClasses(); b++) {
          arrayOfInstances[b].compactify();
          this.m_priors[b] = arrayOfInstances[b].sumOfWeights();
        } 
        Utils.normalize(this.m_priors);
      } else {
        arrayOfInstances = new Instances[1];
        arrayOfInstances[0] = instances1;
        this.m_priors = new double[1];
        this.m_priors[0] = 1.0D;
      } 
      if (this.m_ignoreAttributesRange != null || instances1.classIndex() >= 0) {
        this.m_removeAttributes = new Remove();
        String str = "";
        if (this.m_ignoreAttributesRange != null)
          str = str + this.m_ignoreAttributesRange.getRanges(); 
        if (instances1.classIndex() >= 0)
          if (str.length() > 0) {
            str = str + "," + (instances1.classIndex() + 1);
          } else {
            str = "" + (instances1.classIndex() + 1);
          }  
        ((Remove)this.m_removeAttributes).setAttributeIndices(str);
        ((Remove)this.m_removeAttributes).setInvertSelection(false);
        ((Remove)this.m_removeAttributes).setInputFormat(instances1);
        for (byte b = 0; b < arrayOfInstances.length; b++)
          arrayOfInstances[b] = Filter.useFilter(arrayOfInstances[b], this.m_removeAttributes); 
      } 
      if (instances1.classIndex() <= 0 || !instances1.classAttribute().isNominal()) {
        this.m_clusterers = DensityBasedClusterer.makeCopies(this.m_clusterer, 1);
        this.m_clusterers[0].buildClusterer(arrayOfInstances[0]);
      } else {
        this.m_clusterers = DensityBasedClusterer.makeCopies(this.m_clusterer, instances1.numClasses());
        for (byte b = 0; b < this.m_clusterers.length; b++) {
          if (arrayOfInstances[b].numInstances() == 0) {
            this.m_clusterers[b] = null;
          } else {
            this.m_clusterers[b].buildClusterer(arrayOfInstances[b]);
          } 
        } 
      } 
      FastVector fastVector = new FastVector();
      for (byte b1 = 0; b1 < this.m_clusterers.length; b1++) {
        if (this.m_clusterers[b1] != null)
          for (byte b = 0; b < this.m_clusterers[b1].numberOfClusters(); b++)
            fastVector.addElement(new Attribute("pCluster_" + b1 + "_" + b));  
      } 
      if (instances1.classIndex() >= 0)
        fastVector.addElement(instances1.classAttribute().copy()); 
      fastVector.trimToSize();
      Instances instances2 = new Instances(instances1.relationName() + "_clusterMembership", fastVector, 0);
      if (instances1.classIndex() >= 0)
        instances2.setClassIndex(instances2.numAttributes() - 1); 
      setOutputFormat(instances2);
      for (byte b2 = 0; b2 < instances1.numInstances(); b2++)
        convertInstance(instances1.instance(b2)); 
    } 
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
  
  protected double[] logs2densities(int paramInt, Instance paramInstance) throws Exception {
    double[] arrayOfDouble = this.m_clusterers[paramInt].logJointDensitiesForInstance(paramInstance);
    for (byte b = 0; b < arrayOfDouble.length; b++)
      arrayOfDouble[b] = arrayOfDouble[b] + Math.log(this.m_priors[paramInt]); 
    return arrayOfDouble;
  }
  
  protected void convertInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble1 = new double[outputFormatPeek().numAttributes()];
    if (paramInstance.classIndex() >= 0) {
      arrayOfDouble2 = new double[outputFormatPeek().numAttributes() - 1];
    } else {
      arrayOfDouble2 = new double[outputFormatPeek().numAttributes()];
    } 
    int i = 0;
    for (byte b = 0; b < this.m_clusterers.length; b++) {
      if (this.m_clusterers[b] != null) {
        double[] arrayOfDouble;
        if (this.m_removeAttributes != null) {
          this.m_removeAttributes.input(paramInstance);
          arrayOfDouble = logs2densities(b, this.m_removeAttributes.output());
        } else {
          arrayOfDouble = logs2densities(b, paramInstance);
        } 
        System.arraycopy(arrayOfDouble, 0, arrayOfDouble2, i, arrayOfDouble.length);
        i += arrayOfDouble.length;
      } 
    } 
    double[] arrayOfDouble2 = Utils.logs2probs(arrayOfDouble2);
    System.arraycopy(arrayOfDouble2, 0, arrayOfDouble1, 0, arrayOfDouble2.length);
    if (paramInstance.classIndex() >= 0)
      arrayOfDouble1[arrayOfDouble1.length - 1] = paramInstance.classValue(); 
    push(new Instance(paramInstance.weight(), arrayOfDouble1));
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tFull name of clusterer to use (required).\n\teg: weka.clusterers.EM", "W", 1, "-W <clusterer name>"));
    vector.addElement(new Option("\tThe range of attributes the clusterer should ignore.\n\t(the class attribute is automatically ignored)", "I", 1, "-I <att1,att2-att4,...>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('W', paramArrayOfString);
    if (str.length() == 0)
      throw new Exception("A clusterer must be specified with the -W option."); 
    setDensityBasedClusterer((DensityBasedClusterer)Utils.forName(DensityBasedClusterer.class, str, Utils.partitionOptions(paramArrayOfString)));
    setIgnoredAttributeIndices(Utils.getOption('I', paramArrayOfString));
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_clusterer != null && this.m_clusterer instanceof OptionHandler)
      arrayOfString1 = ((OptionHandler)this.m_clusterer).getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 5];
    int i = 0;
    if (!getIgnoredAttributeIndices().equals("")) {
      arrayOfString2[i++] = "-I";
      arrayOfString2[i++] = getIgnoredAttributeIndices();
    } 
    if (this.m_clusterer != null) {
      arrayOfString2[i++] = "-W";
      arrayOfString2[i++] = getDensityBasedClusterer().getClass().getName();
    } 
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public String globalInfo() {
    return "A filter that uses a density-based clusterer to generate cluster membership values; filtered instances are composed of these values plus the class attribute (if set in the input data). If a (nominal) class attribute is set, the clusterer is run separately for each class. The class attribute (if set) and any user-specified attributes are ignored during the clustering operation";
  }
  
  public String clustererTipText() {
    return "The clusterer that will generate membership values for the instances.";
  }
  
  public void setDensityBasedClusterer(DensityBasedClusterer paramDensityBasedClusterer) {
    this.m_clusterer = paramDensityBasedClusterer;
  }
  
  public DensityBasedClusterer getDensityBasedClusterer() {
    return this.m_clusterer;
  }
  
  public String ignoredAttributeIndicesTipText() {
    return "The range of attributes to be ignored by the clusterer. eg: first-3,5,9-last";
  }
  
  public String getIgnoredAttributeIndices() {
    return (this.m_ignoreAttributesRange == null) ? "" : this.m_ignoreAttributesRange.getRanges();
  }
  
  public void setIgnoredAttributeIndices(String paramString) {
    if (paramString == null || paramString.length() == 0) {
      this.m_ignoreAttributesRange = null;
    } else {
      this.m_ignoreAttributesRange = new Range();
      this.m_ignoreAttributesRange.setRanges(paramString);
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new ClusterMembership(), paramArrayOfString);
      } else {
        Filter.filterFile(new ClusterMembership(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\ClusterMembership.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */