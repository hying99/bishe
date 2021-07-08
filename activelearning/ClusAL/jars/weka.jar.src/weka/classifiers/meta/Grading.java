package weka.classifiers.meta;

import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class Grading extends Stacking {
  protected Classifier[] m_MetaClassifiers = null;
  
  protected double[] m_InstPerClass = null;
  
  public String globalInfo() {
    return "Implements Grading. The base classifiers are \"graded\". For more information, see\n\nSeewald A.K., Fuernkranz J. (2001): An Evaluation of Grading Classifiers, in Hoffmann F. et al. (eds.), Advances in Intelligent Data Analysis, 4th International Conference, IDA 2001, Proceedings, Springer, Berlin/Heidelberg/New York/Tokyo, pp.115-124, 2001";
  }
  
  protected void generateMetaLevel(Instances paramInstances, Random paramRandom) throws Exception {
    this.m_MetaFormat = metaFormat(paramInstances);
    Instances[] arrayOfInstances = new Instances[this.m_Classifiers.length];
    byte b;
    for (b = 0; b < this.m_Classifiers.length; b++)
      arrayOfInstances[b] = metaFormat(paramInstances); 
    for (b = 0; b < this.m_NumFolds; b++) {
      Instances instances1 = paramInstances.trainCV(this.m_NumFolds, b, paramRandom);
      Instances instances2 = paramInstances.testCV(this.m_NumFolds, b);
      for (byte b1 = 0; b1 < this.m_Classifiers.length; b1++) {
        getClassifier(b1).buildClassifier(instances1);
        for (byte b2 = 0; b2 < instances2.numInstances(); b2++)
          arrayOfInstances[b1].add(metaInstance(instances2.instance(b2), b1)); 
      } 
    } 
    this.m_InstPerClass = new double[paramInstances.numClasses()];
    for (b = 0; b < paramInstances.numClasses(); b++)
      this.m_InstPerClass[b] = 0.0D; 
    for (b = 0; b < paramInstances.numInstances(); b++)
      this.m_InstPerClass[(int)paramInstances.instance(b).classValue()] = this.m_InstPerClass[(int)paramInstances.instance(b).classValue()] + 1.0D; 
    this.m_MetaClassifiers = Classifier.makeCopies(this.m_MetaClassifier, this.m_Classifiers.length);
    for (b = 0; b < this.m_Classifiers.length; b++)
      this.m_MetaClassifiers[b].buildClassifier(arrayOfInstances[b]); 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    byte b = 0;
    int i = this.m_Classifiers.length;
    double[] arrayOfDouble1 = new double[i];
    int j;
    for (j = 0; j < i; j++) {
      double[] arrayOfDouble = this.m_MetaClassifiers[j].distributionForInstance(metaInstance(paramInstance, j));
      if (this.m_MetaClassifiers[j].classifyInstance(metaInstance(paramInstance, j)) == 1.0D) {
        arrayOfDouble1[j] = arrayOfDouble[1];
      } else {
        arrayOfDouble1[j] = -arrayOfDouble[0];
      } 
    } 
    if (arrayOfDouble1[Utils.maxIndex(arrayOfDouble1)] < 0.0D) {
      for (j = 0; j < i; j++)
        arrayOfDouble1[j] = 1.0D + arrayOfDouble1[j]; 
    } else {
      for (j = 0; j < i; j++) {
        if (arrayOfDouble1[j] < 0.0D)
          arrayOfDouble1[j] = 0.0D; 
      } 
    } 
    double[] arrayOfDouble2 = new double[paramInstance.numClasses()];
    for (j = 0; j < paramInstance.numClasses(); j++)
      arrayOfDouble2[j] = 0.0D; 
    for (j = 0; j < i; j++) {
      int n = (int)this.m_Classifiers[j].classifyInstance(paramInstance);
      arrayOfDouble2[n] = arrayOfDouble2[n] + arrayOfDouble1[j];
    } 
    double d = arrayOfDouble2[Utils.maxIndex(arrayOfDouble2)];
    j = -100;
    int k = -1;
    int m;
    for (m = 0; m < paramInstance.numClasses(); m++) {
      if (arrayOfDouble2[m] == d) {
        b++;
        if (this.m_InstPerClass[m] > j) {
          j = (int)this.m_InstPerClass[m];
          k = m;
        } 
      } 
    } 
    if (b == 1) {
      m = Utils.maxIndex(arrayOfDouble2);
    } else {
      m = k;
    } 
    double[] arrayOfDouble3 = new double[paramInstance.numClasses()];
    arrayOfDouble3[m] = 1.0D;
    return arrayOfDouble3;
  }
  
  public String toString() {
    if (this.m_Classifiers.length == 0)
      return "Grading: No base schemes entered."; 
    if (this.m_MetaClassifiers.length == 0)
      return "Grading: No meta scheme selected."; 
    if (this.m_MetaFormat == null)
      return "Grading: No model built yet."; 
    String str = "Grading\n\nBase classifiers\n\n";
    byte b;
    for (b = 0; b < this.m_Classifiers.length; b++)
      str = str + getClassifier(b).toString() + "\n\n"; 
    str = str + "\n\nMeta classifiers\n\n";
    for (b = 0; b < this.m_Classifiers.length; b++)
      str = str + this.m_MetaClassifiers[b].toString() + "\n\n"; 
    return str;
  }
  
  protected Instances metaFormat(Instances paramInstances) throws Exception {
    FastVector fastVector1 = new FastVector();
    for (byte b = 0; b < paramInstances.numAttributes() - 1; b++)
      fastVector1.addElement(paramInstances.attribute(b)); 
    FastVector fastVector2 = new FastVector(2);
    fastVector2.addElement("0");
    fastVector2.addElement("1");
    fastVector1.addElement(new Attribute("PredConf", fastVector2));
    Instances instances = new Instances("Meta format", fastVector1, 0);
    instances.setClassIndex(instances.numAttributes() - 1);
    return instances;
  }
  
  protected Instance metaInstance(Instance paramInstance, int paramInt) throws Exception {
    double[] arrayOfDouble1 = new double[this.m_MetaFormat.numAttributes()];
    byte b1;
    for (b1 = 0; b1 < paramInstance.numAttributes() - 1; b1++)
      arrayOfDouble1[b1] = paramInstance.value(b1); 
    Classifier classifier = getClassifier(paramInt);
    if (this.m_BaseFormat.classAttribute().isNumeric())
      throw new Exception("Class Attribute must not be numeric!"); 
    double[] arrayOfDouble2 = classifier.distributionForInstance(paramInstance);
    byte b2 = 0;
    double d2 = arrayOfDouble2[0];
    for (byte b3 = 1; b3 < arrayOfDouble2.length; b3++) {
      if (arrayOfDouble2[b3] > d2) {
        d2 = arrayOfDouble2[b3];
        b2 = b3;
      } 
    } 
    double d1 = (paramInstance.classValue() == b2) ? 1.0D : 0.0D;
    arrayOfDouble1[b1] = d1;
    Instance instance = new Instance(1.0D, arrayOfDouble1);
    instance.setDataset(this.m_MetaFormat);
    return instance;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new Grading(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\Grading.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */