package weka.classifiers.rules;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.m5.M5Base;

public class M5Rules extends M5Base {
  public String globalInfo() {
    return "Generates a decision list for regression problems using separate-and-conquer. In each iteration it builds a model tree using M5 and makes the \"best\" leaf into a rule. Reference:\n\nM. Hall, G. Holmes, E. Frank (1999).  \"Generating Rule Sets from Model Trees\". Proceedings of the Twelfth Australian Joint Conference on Artificial Intelligence, Sydney, Australia. Springer-Verlag, pp. 1-12.";
  }
  
  public M5Rules() {
    setGenerateRules(true);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new M5Rules(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\M5Rules.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */