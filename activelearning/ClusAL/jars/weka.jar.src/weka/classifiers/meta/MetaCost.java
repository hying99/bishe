package weka.classifiers.meta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomizableSingleClassifierEnhancer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

public class MetaCost extends RandomizableSingleClassifierEnhancer {
  public static final int MATRIX_ON_DEMAND = 1;
  
  public static final int MATRIX_SUPPLIED = 2;
  
  public static final Tag[] TAGS_MATRIX_SOURCE = new Tag[] { new Tag(1, "Load cost matrix on demand"), new Tag(2, "Use explicit cost matrix") };
  
  protected int m_MatrixSource = 1;
  
  protected File m_OnDemandDirectory = new File(System.getProperty("user.dir"));
  
  protected String m_CostFile;
  
  protected CostMatrix m_CostMatrix = new CostMatrix(1);
  
  protected int m_NumIterations = 10;
  
  protected int m_BagSizePercent = 100;
  
  public String globalInfo() {
    return "This metaclassifier makes its base classifier cost-sensitive using the method specified in\n\nPedro Domingos (1999) \"MetaCost: A general method for making classifiers cost-sensitive\", Proceedings of the Fifth International Conference on Knowledge Discovery and Data Mining, pp 155-164.\n\nThis classifier should produce similar results to one created by passing the base learner to Bagging, which is in turn passed to a CostSensitiveClassifier operating on minimum expected cost. The difference is that MetaCost produces a single cost-sensitive classifier of the base learner, giving the benefits of fast classification and interpretable output (if the base learner itself is interpretable). This implementation  uses all bagging iterations when reclassifying training data (the MetaCost paper reports a marginal improvement when only those iterations containing each training instance are used in reclassifying that instance).";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tNumber of bagging iterations.\n\t(default 10)", "I", 1, "-I <num>"));
    vector.addElement(new Option("\tFile name of a cost matrix to use. If this is not supplied,\n\ta cost matrix will be loaded on demand. The name of the\n\ton-demand file is the relation name of the training data\n\tplus \".cost\", and the path to the on-demand file is\n\tspecified with the -N option.", "C", 1, "-C <cost file name>"));
    vector.addElement(new Option("\tName of a directory to search for cost files when loading\n\tcosts on demand (default current directory).", "N", 1, "-N <directory>"));
    vector.addElement(new Option("\tSize of each bag, as a percentage of the\n\ttraining set size. (default 100)", "P", 1, "-P"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('I', paramArrayOfString);
    if (str1.length() != 0) {
      setNumIterations(Integer.parseInt(str1));
    } else {
      setNumIterations(10);
    } 
    String str2 = Utils.getOption('P', paramArrayOfString);
    if (str2.length() != 0) {
      setBagSizePercent(Integer.parseInt(str2));
    } else {
      setBagSizePercent(100);
    } 
    String str3 = Utils.getOption('C', paramArrayOfString);
    if (str3.length() != 0) {
      setCostMatrix(new CostMatrix(new BufferedReader(new FileReader(str3))));
      setCostMatrixSource(new SelectedTag(2, TAGS_MATRIX_SOURCE));
      this.m_CostFile = str3;
    } else {
      setCostMatrixSource(new SelectedTag(1, TAGS_MATRIX_SOURCE));
    } 
    String str4 = Utils.getOption('N', paramArrayOfString);
    if (str4.length() != 0)
      setOnDemandDirectory(new File(str4)); 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString2;
    String[] arrayOfString1 = super.getOptions();
    if (this.m_MatrixSource == 2 && this.m_CostFile == null) {
      arrayOfString2 = new String[arrayOfString1.length + 4];
    } else {
      arrayOfString2 = new String[arrayOfString1.length + 6];
    } 
    byte b = 0;
    if (this.m_MatrixSource == 2) {
      if (this.m_CostFile != null) {
        arrayOfString2[b++] = "-C";
        arrayOfString2[b++] = "" + this.m_CostFile;
      } 
    } else {
      arrayOfString2[b++] = "-N";
      arrayOfString2[b++] = "" + getOnDemandDirectory();
    } 
    arrayOfString2[b++] = "-I";
    arrayOfString2[b++] = "" + getNumIterations();
    arrayOfString2[b++] = "-P";
    arrayOfString2[b++] = "" + getBagSizePercent();
    System.arraycopy(arrayOfString1, 0, arrayOfString2, b, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public String costMatrixSourceTipText() {
    return "Gets the source location method of the cost matrix. Will be one of MATRIX_ON_DEMAND or MATRIX_SUPPLIED.";
  }
  
  public SelectedTag getCostMatrixSource() {
    return new SelectedTag(this.m_MatrixSource, TAGS_MATRIX_SOURCE);
  }
  
  public void setCostMatrixSource(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_MATRIX_SOURCE)
      this.m_MatrixSource = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public String onDemandDirectoryTipText() {
    return "Name of directory to search for cost files when loading costs on demand.";
  }
  
  public File getOnDemandDirectory() {
    return this.m_OnDemandDirectory;
  }
  
  public void setOnDemandDirectory(File paramFile) {
    if (paramFile.isDirectory()) {
      this.m_OnDemandDirectory = paramFile;
    } else {
      this.m_OnDemandDirectory = new File(paramFile.getParent());
    } 
    this.m_MatrixSource = 1;
  }
  
  public String bagSizePercentTipText() {
    return "The size of each bag, as a percentage of the training set size.";
  }
  
  public int getBagSizePercent() {
    return this.m_BagSizePercent;
  }
  
  public void setBagSizePercent(int paramInt) {
    this.m_BagSizePercent = paramInt;
  }
  
  public String numIterationsTipText() {
    return "The number of bagging iterations.";
  }
  
  public void setNumIterations(int paramInt) {
    this.m_NumIterations = paramInt;
  }
  
  public int getNumIterations() {
    return this.m_NumIterations;
  }
  
  public String costMatrixTipText() {
    return "A misclassification cost matrix.";
  }
  
  public CostMatrix getCostMatrix() {
    return this.m_CostMatrix;
  }
  
  public void setCostMatrix(CostMatrix paramCostMatrix) {
    this.m_CostMatrix = paramCostMatrix;
    this.m_MatrixSource = 2;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("Class attribute must be nominal!"); 
    if (this.m_MatrixSource == 1) {
      String str = paramInstances.relationName() + CostMatrix.FILE_EXTENSION;
      File file = new File(getOnDemandDirectory(), str);
      if (!file.exists())
        throw new Exception("On-demand cost file doesn't exist: " + file); 
      setCostMatrix(new CostMatrix(new BufferedReader(new FileReader(file))));
    } 
    Bagging bagging = new Bagging();
    bagging.setClassifier(getClassifier());
    bagging.setSeed(getSeed());
    bagging.setNumIterations(getNumIterations());
    bagging.setBagSizePercent(getBagSizePercent());
    bagging.buildClassifier(paramInstances);
    Instances instances = new Instances(paramInstances);
    for (byte b = 0; b < instances.numInstances(); b++) {
      Instance instance = instances.instance(b);
      double[] arrayOfDouble = bagging.distributionForInstance(instance);
      int i = Utils.minIndex(this.m_CostMatrix.expectedCosts(arrayOfDouble));
      instance.setClassValue(i);
    } 
    this.m_Classifier.buildClassifier(instances);
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    return this.m_Classifier.classifyInstance(paramInstance);
  }
  
  protected String getClassifierSpec() {
    Classifier classifier = getClassifier();
    return classifier.getClass().getName() + " " + Utils.joinOptions(classifier.getOptions());
  }
  
  public String toString() {
    if (this.m_Classifier == null)
      return "MetaCost: No model built yet."; 
    null = "MetaCost cost sensitive classifier induction";
    null = null + "\nOptions: " + Utils.joinOptions(getOptions());
    return null + "\nBase learner: " + getClassifierSpec() + "\n\nClassifier Model\n" + this.m_Classifier.toString() + "\n\nCost Matrix\n" + this.m_CostMatrix.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new MetaCost(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\MetaCost.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */