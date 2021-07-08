package weka.classifiers.meta;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.core.Instance;
import weka.core.Instances;

public class Vote extends MultipleClassifiersCombiner {
  public String globalInfo() {
    return "Class for combining classifiers using unweighted average of probability estimates (classification) or numeric predictions (regression).";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    Instances instances = new Instances(paramInstances);
    instances.deleteWithMissingClass();
    for (byte b = 0; b < this.m_Classifiers.length; b++)
      getClassifier(b).buildClassifier(paramInstances); 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = getClassifier(0).distributionForInstance(paramInstance);
    byte b;
    for (b = 1; b < this.m_Classifiers.length; b++) {
      double[] arrayOfDouble1 = getClassifier(b).distributionForInstance(paramInstance);
      for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
        arrayOfDouble[b1] = arrayOfDouble[b1] + arrayOfDouble1[b1]; 
    } 
    for (b = 0; b < arrayOfDouble.length; b++)
      arrayOfDouble[b] = arrayOfDouble[b] / this.m_Classifiers.length; 
    return arrayOfDouble;
  }
  
  public String toString() {
    if (this.m_Classifiers == null)
      return "Vote: No model built yet."; 
    String str = "Vote combines";
    str = str + " the probability distributions of these base learners:\n";
    for (byte b = 0; b < this.m_Classifiers.length; b++)
      str = str + '\t' + getClassifierSpec(b) + '\n'; 
    return str;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new Vote(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\Vote.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */