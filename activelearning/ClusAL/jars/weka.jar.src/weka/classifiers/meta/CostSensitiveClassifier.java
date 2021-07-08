package weka.classifiers.meta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

public class CostSensitiveClassifier extends Classifier implements OptionHandler, Drawable {
  public static final int MATRIX_ON_DEMAND = 1;
  
  public static final int MATRIX_SUPPLIED = 2;
  
  public static final Tag[] TAGS_MATRIX_SOURCE = new Tag[] { new Tag(1, "Load cost matrix on demand"), new Tag(2, "Use explicit cost matrix") };
  
  protected int m_MatrixSource = 1;
  
  protected File m_OnDemandDirectory = new File(System.getProperty("user.dir"));
  
  protected String m_CostFile;
  
  protected CostMatrix m_CostMatrix = new CostMatrix(1);
  
  protected Classifier m_Classifier = (Classifier)new ZeroR();
  
  protected int m_Seed = 1;
  
  protected boolean m_MinimizeExpectedCost;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(5);
    vector.addElement(new Option("\tMinimize expected misclassification cost. Default is to\n\treweight training instances according to costs per class", "M", 0, "-M"));
    vector.addElement(new Option("\tFull class name of classifier to use. (required)\n\teg: weka.classifiers.bayes.NaiveBayes", "W", 1, "-W <class name>"));
    vector.addElement(new Option("\tFile name of a cost matrix to use. If this is not supplied,\n\ta cost matrix will be loaded on demand. The name of the\n\ton-demand file is the relation name of the training data\n\tplus \".cost\", and the path to the on-demand file is\n\tspecified with the -N option.", "C", 1, "-C <cost file name>"));
    vector.addElement(new Option("\tName of a directory to search for cost files when loading\n\tcosts on demand (default current directory).", "N", 1, "-N <directory>"));
    vector.addElement(new Option("\tSeed used when reweighting via resampling. (Default 1)", "S", 1, "-S <num>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setMinimizeExpectedCost(Utils.getFlag('M', paramArrayOfString));
    String str1 = Utils.getOption('S', paramArrayOfString);
    if (str1.length() != 0) {
      setSeed(Integer.parseInt(str1));
    } else {
      setSeed(1);
    } 
    String str2 = Utils.getOption('W', paramArrayOfString);
    if (str2.length() == 0)
      throw new Exception("A classifier must be specified with the -W option."); 
    setClassifier(Classifier.forName(str2, Utils.partitionOptions(paramArrayOfString)));
    String str3 = Utils.getOption('C', paramArrayOfString);
    if (str3.length() != 0) {
      try {
        setCostMatrix(new CostMatrix(new BufferedReader(new FileReader(str3))));
      } catch (Exception exception) {
        setCostMatrix(null);
      } 
      setCostMatrixSource(new SelectedTag(2, TAGS_MATRIX_SOURCE));
      this.m_CostFile = str3;
    } else {
      setCostMatrixSource(new SelectedTag(1, TAGS_MATRIX_SOURCE));
    } 
    String str4 = Utils.getOption('D', paramArrayOfString);
    if (str4.length() != 0)
      setOnDemandDirectory(new File(str4)); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler)
      arrayOfString1 = this.m_Classifier.getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 9];
    int i = 0;
    if (this.m_MatrixSource == 2) {
      if (this.m_CostFile != null) {
        arrayOfString2[i++] = "-C";
        arrayOfString2[i++] = "" + this.m_CostFile;
      } 
    } else {
      arrayOfString2[i++] = "-N";
      arrayOfString2[i++] = "" + getOnDemandDirectory();
    } 
    arrayOfString2[i++] = "-S";
    arrayOfString2[i++] = "" + getSeed();
    if (getMinimizeExpectedCost())
      arrayOfString2[i++] = "-M"; 
    if (getClassifier() != null) {
      arrayOfString2[i++] = "-W";
      arrayOfString2[i++] = getClassifier().getClass().getName();
    } 
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public String globalInfo() {
    return "A metaclassifier that makes its base classifier cost-sensitive. Two methods can be used to introduce cost-sensitivity: reweighting training instances according to the total cost assigned to each class; or predicting the class with minimum expected misclassification cost (rather than the most likely class). Performance can often be improved by using a Bagged classifier to improve the probability estimates of the base classifier.";
  }
  
  public String costMatrixSourceTipText() {
    return "Sets where to get the cost matrix. The two options areto use the supplied explicit cost matrix (the setting of the costMatrix property), or to load a cost matrix from a file when required (this file will be loaded from the directory set by the onDemandDirectory property and will be named relation_name" + CostMatrix.FILE_EXTENSION + ").";
  }
  
  public SelectedTag getCostMatrixSource() {
    return new SelectedTag(this.m_MatrixSource, TAGS_MATRIX_SOURCE);
  }
  
  public void setCostMatrixSource(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_MATRIX_SOURCE)
      this.m_MatrixSource = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public String onDemandDirectoryTipText() {
    return "Sets the directory where cost files are loaded from. This option is used when the costMatrixSource is set to \"On Demand\".";
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
  
  public String minimizeExpectedCostTipText() {
    return "Sets whether the minimum expected cost criteria will be used. If this is false, the training data will be reweighted according to the costs assigned to each class. If true, the minimum expected cost criteria will be used.";
  }
  
  public boolean getMinimizeExpectedCost() {
    return this.m_MinimizeExpectedCost;
  }
  
  public void setMinimizeExpectedCost(boolean paramBoolean) {
    this.m_MinimizeExpectedCost = paramBoolean;
  }
  
  public String classifierTipText() {
    return "Sets the Classifier used as the basis for the cost-sensitive classification.";
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_Classifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_Classifier;
  }
  
  protected String getClassifierSpec() {
    Classifier classifier = getClassifier();
    return (classifier instanceof OptionHandler) ? (classifier.getClass().getName() + " " + Utils.joinOptions(classifier.getOptions())) : classifier.getClass().getName();
  }
  
  public String costMatrixTipText() {
    return "Sets the cost matrix explicitly. This matrix is used if the costMatrixSource property is set to \"Supplied\".";
  }
  
  public CostMatrix getCostMatrix() {
    return this.m_CostMatrix;
  }
  
  public void setCostMatrix(CostMatrix paramCostMatrix) {
    this.m_CostMatrix = paramCostMatrix;
    this.m_MatrixSource = 2;
  }
  
  public String seedTipText() {
    return "Sets the random number seed when reweighting instances. Ignored when using minimum expected cost criteria.";
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (this.m_Classifier == null)
      throw new Exception("No base classifier has been set!"); 
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("Class attribute must be nominal!"); 
    if (this.m_MatrixSource == 1) {
      String str = paramInstances.relationName() + CostMatrix.FILE_EXTENSION;
      File file = new File(getOnDemandDirectory(), str);
      if (!file.exists())
        throw new Exception("On-demand cost file doesn't exist: " + file); 
      setCostMatrix(new CostMatrix(new BufferedReader(new FileReader(file))));
    } else if (this.m_CostMatrix == null) {
      this.m_CostMatrix = new CostMatrix(paramInstances.numClasses());
      this.m_CostMatrix.readOldFormat(new BufferedReader(new FileReader(this.m_CostFile)));
    } 
    if (!this.m_MinimizeExpectedCost) {
      Random random = null;
      if (!(this.m_Classifier instanceof weka.core.WeightedInstancesHandler))
        random = new Random(this.m_Seed); 
      paramInstances = this.m_CostMatrix.applyCostMatrix(paramInstances, random);
    } 
    this.m_Classifier.buildClassifier(paramInstances);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (!this.m_MinimizeExpectedCost)
      return this.m_Classifier.distributionForInstance(paramInstance); 
    double[] arrayOfDouble1 = this.m_Classifier.distributionForInstance(paramInstance);
    double[] arrayOfDouble2 = this.m_CostMatrix.expectedCosts(arrayOfDouble1);
    int i = Utils.minIndex(arrayOfDouble2);
    for (byte b = 0; b < arrayOfDouble1.length; b++) {
      if (b == i) {
        arrayOfDouble1[b] = 1.0D;
      } else {
        arrayOfDouble1[b] = 0.0D;
      } 
    } 
    return arrayOfDouble1;
  }
  
  public int graphType() {
    return (this.m_Classifier instanceof Drawable) ? ((Drawable)this.m_Classifier).graphType() : 0;
  }
  
  public String graph() throws Exception {
    if (this.m_Classifier instanceof Drawable)
      return ((Drawable)this.m_Classifier).graph(); 
    throw new Exception("Classifier: " + getClassifierSpec() + " cannot be graphed");
  }
  
  public String toString() {
    if (this.m_Classifier == null)
      return "CostSensitiveClassifier: No model built yet."; 
    null = "CostSensitiveClassifier using ";
    if (this.m_MinimizeExpectedCost) {
      null = null + "minimized expected misclasification cost\n";
    } else {
      null = null + "reweighted training instances\n";
    } 
    return null + "\n" + getClassifierSpec() + "\n\nClassifier Model\n" + this.m_Classifier.toString() + "\n\nCost Matrix\n" + this.m_CostMatrix.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new CostSensitiveClassifier(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\CostSensitiveClassifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */