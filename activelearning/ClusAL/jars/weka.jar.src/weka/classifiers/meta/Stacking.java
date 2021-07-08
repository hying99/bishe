package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomizableMultipleClassifiersCombiner;
import weka.classifiers.rules.ZeroR;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

public class Stacking extends RandomizableMultipleClassifiersCombiner {
  protected Classifier m_MetaClassifier = (Classifier)new ZeroR();
  
  protected Instances m_MetaFormat = null;
  
  protected Instances m_BaseFormat = null;
  
  protected int m_NumFolds = 10;
  
  public String globalInfo() {
    return "Combines several classifiers using the stacking method. Can do classification or regression. For more information, see\n\nDavid H. Wolpert (1992). \"Stacked generalization\". Neural Networks, 5:241-259, Pergamon Press.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option(metaOption(), "M", 0, "-M <scheme specification>"));
    vector.addElement(new Option("\tSets the number of cross-validation folds.", "X", 1, "-X <number of folds>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  protected String metaOption() {
    return "\tFull name of meta classifier, followed by options.\n\t(default: \"weka.classifiers.rules.Zero\")";
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('X', paramArrayOfString);
    if (str.length() != 0) {
      setNumFolds(Integer.parseInt(str));
    } else {
      setNumFolds(10);
    } 
    processMetaOptions(paramArrayOfString);
    super.setOptions(paramArrayOfString);
  }
  
  protected void processMetaOptions(String[] paramArrayOfString) throws Exception {
    String str2;
    String str1 = Utils.getOption('M', paramArrayOfString);
    String[] arrayOfString = Utils.splitOptions(str1);
    if (arrayOfString.length == 0) {
      str2 = "weka.classifiers.rules.ZeroR";
    } else {
      str2 = arrayOfString[0];
      arrayOfString[0] = "";
    } 
    setMetaClassifier(Classifier.forName(str2, arrayOfString));
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 4];
    byte b = 0;
    arrayOfString2[b++] = "-X";
    arrayOfString2[b++] = "" + getNumFolds();
    arrayOfString2[b++] = "-M";
    arrayOfString2[b++] = getMetaClassifier().getClass().getName() + " " + Utils.joinOptions(getMetaClassifier().getOptions());
    System.arraycopy(arrayOfString1, 0, arrayOfString2, b, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public String numFoldsTipText() {
    return "The number of folds used for cross-validation.";
  }
  
  public int getNumFolds() {
    return this.m_NumFolds;
  }
  
  public void setNumFolds(int paramInt) throws Exception {
    if (paramInt < 0)
      throw new IllegalArgumentException("Stacking: Number of cross-validation folds must be positive."); 
    this.m_NumFolds = paramInt;
  }
  
  public String metaClassifierTipText() {
    return "The meta classifiers to be used.";
  }
  
  public void setMetaClassifier(Classifier paramClassifier) {
    this.m_MetaClassifier = paramClassifier;
  }
  
  public Classifier getMetaClassifier() {
    return this.m_MetaClassifier;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (this.m_MetaClassifier == null)
      throw new IllegalArgumentException("No meta classifier has been set"); 
    if (!paramInstances.classAttribute().isNominal() && !paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Class attribute has to be nominal or numeric!"); 
    Instances instances = new Instances(paramInstances);
    this.m_BaseFormat = new Instances(paramInstances, 0);
    instances.deleteWithMissingClass();
    if (instances.numInstances() == 0)
      throw new IllegalArgumentException("No training instances without missing class!"); 
    Random random = new Random(this.m_Seed);
    instances.randomize(random);
    if (instances.classAttribute().isNominal())
      instances.stratify(this.m_NumFolds); 
    generateMetaLevel(instances, random);
    for (byte b = 0; b < this.m_Classifiers.length; b++)
      getClassifier(b).buildClassifier(instances); 
  }
  
  protected void generateMetaLevel(Instances paramInstances, Random paramRandom) throws Exception {
    Instances instances = metaFormat(paramInstances);
    this.m_MetaFormat = new Instances(instances, 0);
    for (byte b = 0; b < this.m_NumFolds; b++) {
      Instances instances1 = paramInstances.trainCV(this.m_NumFolds, b, paramRandom);
      for (byte b1 = 0; b1 < this.m_Classifiers.length; b1++)
        getClassifier(b1).buildClassifier(instances1); 
      Instances instances2 = paramInstances.testCV(this.m_NumFolds, b);
      for (byte b2 = 0; b2 < instances2.numInstances(); b2++)
        instances.add(metaInstance(instances2.instance(b2))); 
    } 
    this.m_MetaClassifier.buildClassifier(instances);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    return this.m_MetaClassifier.distributionForInstance(metaInstance(paramInstance));
  }
  
  public String toString() {
    if (this.m_Classifiers.length == 0)
      return "Stacking: No base schemes entered."; 
    if (this.m_MetaClassifier == null)
      return "Stacking: No meta scheme selected."; 
    if (this.m_MetaFormat == null)
      return "Stacking: No model built yet."; 
    null = "Stacking\n\nBase classifiers\n\n";
    for (byte b = 0; b < this.m_Classifiers.length; b++)
      null = null + getClassifier(b).toString() + "\n\n"; 
    null = null + "\n\nMeta classifier\n\n";
    return null + this.m_MetaClassifier.toString();
  }
  
  protected Instances metaFormat(Instances paramInstances) throws Exception {
    FastVector fastVector = new FastVector();
    boolean bool = false;
    for (byte b = 0; b < this.m_Classifiers.length; b++) {
      Classifier classifier = getClassifier(b);
      String str = classifier.getClass().getName();
      if (this.m_BaseFormat.classAttribute().isNumeric()) {
        fastVector.addElement(new Attribute(str));
      } else {
        for (byte b1 = 0; b1 < this.m_BaseFormat.classAttribute().numValues(); b1++)
          fastVector.addElement(new Attribute(str + ":" + this.m_BaseFormat.classAttribute().value(b1))); 
      } 
    } 
    fastVector.addElement(this.m_BaseFormat.classAttribute().copy());
    Instances instances = new Instances("Meta format", fastVector, 0);
    instances.setClassIndex(instances.numAttributes() - 1);
    return instances;
  }
  
  protected Instance metaInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = new double[this.m_MetaFormat.numAttributes()];
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_Classifiers.length; b2++) {
      Classifier classifier = getClassifier(b2);
      if (this.m_BaseFormat.classAttribute().isNumeric()) {
        arrayOfDouble[b1++] = classifier.classifyInstance(paramInstance);
      } else {
        double[] arrayOfDouble1 = classifier.distributionForInstance(paramInstance);
        for (byte b = 0; b < arrayOfDouble1.length; b++)
          arrayOfDouble[b1++] = arrayOfDouble1[b]; 
      } 
    } 
    arrayOfDouble[b1] = paramInstance.classValue();
    Instance instance = new Instance(1.0D, arrayOfDouble);
    instance.setDataset(this.m_MetaFormat);
    return instance;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new Stacking(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\Stacking.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */