package weka.classifiers.meta;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.SingleClassifierEnhancer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.MakeIndicator;

public class ClassificationViaRegression extends SingleClassifierEnhancer {
  private Classifier[] m_Classifiers;
  
  private MakeIndicator[] m_ClassFilters;
  
  public String globalInfo() {
    return "Class for doing classification using regression methods. Class is binarized and one regression model is built for each class value. For more information, see, for example\n\nE. Frank, Y. Wang, S. Inglis, G. Holmes, and I.H. Witten (1998) \"Using model trees for classification\", Machine Learning, Vol.32, No.1, pp. 63-76.";
  }
  
  protected String defaultClassifierString() {
    return "weka.classifiers.trees.M5P";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("ClassificationViaRegression can't handle a numeric class!"); 
    this.m_Classifiers = Classifier.makeCopies(this.m_Classifier, paramInstances.numClasses());
    this.m_ClassFilters = new MakeIndicator[paramInstances.numClasses()];
    for (byte b = 0; b < paramInstances.numClasses(); b++) {
      this.m_ClassFilters[b] = new MakeIndicator();
      this.m_ClassFilters[b].setAttributeIndex("" + (paramInstances.classIndex() + 1));
      this.m_ClassFilters[b].setValueIndex(b);
      this.m_ClassFilters[b].setNumeric(true);
      this.m_ClassFilters[b].setInputFormat(paramInstances);
      Instances instances = Filter.useFilter(paramInstances, (Filter)this.m_ClassFilters[b]);
      this.m_Classifiers[b].buildClassifier(instances);
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = new double[paramInstance.numClasses()];
    double d1 = 0.0D;
    double d2 = Double.MIN_VALUE;
    double d3 = Double.MAX_VALUE;
    for (byte b = 0; b < paramInstance.numClasses(); b++) {
      this.m_ClassFilters[b].input(paramInstance);
      this.m_ClassFilters[b].batchFinished();
      Instance instance = this.m_ClassFilters[b].output();
      arrayOfDouble[b] = this.m_Classifiers[b].classifyInstance(instance);
      if (arrayOfDouble[b] > 1.0D)
        arrayOfDouble[b] = 1.0D; 
      if (arrayOfDouble[b] < 0.0D)
        arrayOfDouble[b] = 0.0D; 
      d1 += arrayOfDouble[b];
    } 
    if (d1 != 0.0D)
      Utils.normalize(arrayOfDouble, d1); 
    return arrayOfDouble;
  }
  
  public String toString() {
    if (this.m_Classifiers == null)
      return "Classification via Regression: No model built yet."; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Classification via Regression\n\n");
    for (byte b = 0; b < this.m_Classifiers.length; b++) {
      stringBuffer.append("Classifier for class with index " + b + ":\n\n");
      stringBuffer.append(this.m_Classifiers[b].toString() + "\n\n");
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      ClassificationViaRegression classificationViaRegression = new ClassificationViaRegression();
      System.out.println(Evaluation.evaluateModel((Classifier)classificationViaRegression, paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\ClassificationViaRegression.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */