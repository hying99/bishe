package weka.classifiers.trees.j48;

import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public final class NBTreeNoSplit extends ClassifierSplitModel {
  private NaiveBayesUpdateable m_nb;
  
  private Discretize m_disc;
  
  private double m_errors;
  
  public final void buildClassifier(Instances paramInstances) throws Exception {
    this.m_nb = new NaiveBayesUpdateable();
    this.m_disc = new Discretize();
    this.m_disc.setInputFormat(paramInstances);
    Instances instances = Filter.useFilter(paramInstances, (Filter)this.m_disc);
    this.m_nb.buildClassifier(instances);
    if (instances.numInstances() >= 5)
      this.m_errors = crossValidate(this.m_nb, instances, new Random(1L)); 
    this.m_numSubsets = 1;
  }
  
  public double getErrors() {
    return this.m_errors;
  }
  
  public Discretize getDiscretizer() {
    return this.m_disc;
  }
  
  public NaiveBayesUpdateable getNaiveBayesModel() {
    return this.m_nb;
  }
  
  public final int whichSubset(Instance paramInstance) {
    return 0;
  }
  
  public final double[] weights(Instance paramInstance) {
    return null;
  }
  
  public final String leftSide(Instances paramInstances) {
    return "";
  }
  
  public final String rightSide(int paramInt, Instances paramInstances) {
    return "";
  }
  
  public final String sourceExpression(int paramInt, Instances paramInstances) {
    return "true";
  }
  
  public double classProb(int paramInt1, Instance paramInstance, int paramInt2) throws Exception {
    this.m_disc.input(paramInstance);
    Instance instance = this.m_disc.output();
    return this.m_nb.distributionForInstance(instance)[paramInt1];
  }
  
  public String toString() {
    return this.m_nb.toString();
  }
  
  public static double crossValidate(NaiveBayesUpdateable paramNaiveBayesUpdateable, Instances paramInstances, Random paramRandom) throws Exception {
    Classifier[] arrayOfClassifier = Classifier.makeCopies((Classifier)paramNaiveBayesUpdateable, 5);
    Evaluation evaluation = new Evaluation(paramInstances);
    for (byte b = 0; b < 5; b++) {
      Instances instances = paramInstances.testCV(5, b);
      for (byte b1 = 0; b1 < instances.numInstances(); b1++) {
        instances.instance(b1).setWeight(-instances.instance(b1).weight());
        ((NaiveBayesUpdateable)arrayOfClassifier[b]).updateClassifier(instances.instance(b1));
        instances.instance(b1).setWeight(-instances.instance(b1).weight());
      } 
      evaluation.evaluateModel(arrayOfClassifier[b], instances);
    } 
    return evaluation.incorrect();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\NBTreeNoSplit.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */