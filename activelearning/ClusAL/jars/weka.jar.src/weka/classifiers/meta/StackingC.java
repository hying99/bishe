package weka.classifiers.meta;

import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.MakeIndicator;
import weka.filters.unsupervised.attribute.Remove;

public class StackingC extends Stacking implements OptionHandler {
  protected Classifier[] m_MetaClassifiers = null;
  
  protected Remove m_attrFilter = null;
  
  protected MakeIndicator m_makeIndicatorFilter = null;
  
  public String globalInfo() {
    return "Implements StackingC (more efficient version of stacking). For more information, see\n\nSeewald A.K.: \"How to Make Stacking Better and Faster While Also Taking Care of an Unknown Weakness\", in Sammut C., Hoffmann A. (eds.), Proceedings of the Nineteenth International Conference on Machine Learning (ICML 2002), Morgan Kaufmann Publishers, pp.554-561, 2002.\n\nNote: requires meta classifier to be a numeric prediction scheme.";
  }
  
  public StackingC() {
    ((LinearRegression)getMetaClassifier()).setAttributeSelectionMethod(new SelectedTag(1, LinearRegression.TAGS_SELECTION));
  }
  
  protected String metaOption() {
    return "\tFull name of meta classifier, followed by options.\n\tMust be a numeric prediction scheme. Default: Linear Regression.";
  }
  
  protected void processMetaOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('M', paramArrayOfString);
    String[] arrayOfString = Utils.splitOptions(str);
    if (arrayOfString.length != 0) {
      String str1 = arrayOfString[0];
      arrayOfString[0] = "";
      setMetaClassifier(Classifier.forName(str1, arrayOfString));
    } else {
      ((LinearRegression)getMetaClassifier()).setAttributeSelectionMethod(new SelectedTag(1, LinearRegression.TAGS_SELECTION));
    } 
  }
  
  protected void generateMetaLevel(Instances paramInstances, Random paramRandom) throws Exception {
    Instances instances = metaFormat(paramInstances);
    this.m_MetaFormat = new Instances(instances, 0);
    for (byte b1 = 0; b1 < this.m_NumFolds; b1++) {
      Instances instances1 = paramInstances.trainCV(this.m_NumFolds, b1, paramRandom);
      for (byte b3 = 0; b3 < this.m_Classifiers.length; b3++)
        getClassifier(b3).buildClassifier(instances1); 
      Instances instances2 = paramInstances.testCV(this.m_NumFolds, b1);
      for (byte b4 = 0; b4 < instances2.numInstances(); b4++)
        instances.add(metaInstance(instances2.instance(b4))); 
    } 
    this.m_MetaClassifiers = Classifier.makeCopies(this.m_MetaClassifier, this.m_BaseFormat.numClasses());
    int[] arrayOfInt = new int[this.m_Classifiers.length + 1];
    arrayOfInt[this.m_Classifiers.length] = instances.numAttributes() - 1;
    for (byte b2 = 0; b2 < this.m_MetaClassifiers.length; b2++) {
      for (byte b = 0; b < this.m_Classifiers.length; b++)
        arrayOfInt[b] = this.m_BaseFormat.numClasses() * b + b2; 
      this.m_makeIndicatorFilter = new MakeIndicator();
      this.m_makeIndicatorFilter.setAttributeIndex("" + (instances.classIndex() + 1));
      this.m_makeIndicatorFilter.setNumeric(true);
      this.m_makeIndicatorFilter.setValueIndex(b2);
      this.m_makeIndicatorFilter.setInputFormat(instances);
      Instances instances1 = Filter.useFilter(instances, (Filter)this.m_makeIndicatorFilter);
      this.m_attrFilter = new Remove();
      this.m_attrFilter.setInvertSelection(true);
      this.m_attrFilter.setAttributeIndicesArray(arrayOfInt);
      this.m_attrFilter.setInputFormat(this.m_makeIndicatorFilter.getOutputFormat());
      instances1 = Filter.useFilter(instances1, (Filter)this.m_attrFilter);
      instances1.setClassIndex(instances1.numAttributes() - 1);
      this.m_MetaClassifiers[b2].buildClassifier(instances1);
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    int[] arrayOfInt = new int[this.m_Classifiers.length + 1];
    arrayOfInt[this.m_Classifiers.length] = this.m_MetaFormat.numAttributes() - 1;
    double[] arrayOfDouble = new double[this.m_BaseFormat.numClasses()];
    double d = 0.0D;
    for (byte b = 0; b < this.m_MetaClassifiers.length; b++) {
      for (byte b1 = 0; b1 < this.m_Classifiers.length; b1++)
        arrayOfInt[b1] = this.m_BaseFormat.numClasses() * b1 + b; 
      this.m_makeIndicatorFilter.setAttributeIndex("" + (this.m_MetaFormat.classIndex() + 1));
      this.m_makeIndicatorFilter.setNumeric(true);
      this.m_makeIndicatorFilter.setValueIndex(b);
      this.m_makeIndicatorFilter.setInputFormat(this.m_MetaFormat);
      this.m_makeIndicatorFilter.input(metaInstance(paramInstance));
      this.m_makeIndicatorFilter.batchFinished();
      Instance instance = this.m_makeIndicatorFilter.output();
      this.m_attrFilter.setAttributeIndicesArray(arrayOfInt);
      this.m_attrFilter.setInvertSelection(true);
      this.m_attrFilter.setInputFormat(this.m_makeIndicatorFilter.getOutputFormat());
      this.m_attrFilter.input(instance);
      this.m_attrFilter.batchFinished();
      instance = this.m_attrFilter.output();
      arrayOfDouble[b] = this.m_MetaClassifiers[b].classifyInstance(instance);
      if (arrayOfDouble[b] > 1.0D)
        arrayOfDouble[b] = 1.0D; 
      if (arrayOfDouble[b] < 0.0D)
        arrayOfDouble[b] = 0.0D; 
      d += arrayOfDouble[b];
    } 
    if (d != 0.0D)
      Utils.normalize(arrayOfDouble, d); 
    return arrayOfDouble;
  }
  
  public String toString() {
    if (this.m_MetaFormat == null)
      return "StackingC: No model built yet."; 
    String str = "StackingC\n\nBase classifiers\n\n";
    byte b;
    for (b = 0; b < this.m_Classifiers.length; b++)
      str = str + getClassifier(b).toString() + "\n\n"; 
    str = str + "\n\nMeta classifiers (one for each class)\n\n";
    for (b = 0; b < this.m_MetaClassifiers.length; b++)
      str = str + this.m_MetaClassifiers[b].toString() + "\n\n"; 
    return str;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new StackingC(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\StackingC.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */