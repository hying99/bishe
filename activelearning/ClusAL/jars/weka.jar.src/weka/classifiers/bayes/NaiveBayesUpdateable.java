package weka.classifiers.bayes;

import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;

public class NaiveBayesUpdateable extends NaiveBayes implements UpdateableClassifier {
  public String globalInfo() {
    return "Class for a Naive Bayes classifier using estimator classes. This is the updateable version of NaiveBayes.This classifier will use a default precision of 0.1 for numeric attributes when buildClassifier is called with zero training instances.\n\nFor more information on Naive Bayes classifiers, see\n\nGeorge H. John and Pat Langley (1995). Estimating Continuous Distributions in Bayesian Classifiers. Proceedings of the Eleventh Conference on Uncertainty in Artificial Intelligence. pp. 338-345. Morgan Kaufmann, San Mateo.\n\n";
  }
  
  public void setUseSupervisedDiscretization(boolean paramBoolean) {
    if (paramBoolean)
      throw new IllegalArgumentException("Can't use discretization in NaiveBayesUpdateable!"); 
    this.m_UseDiscretization = false;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new NaiveBayesUpdateable(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\NaiveBayesUpdateable.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */