package weka.classifiers.bayes.net.estimate;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instance;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class BayesNetEstimator implements OptionHandler, Serializable {
  protected double m_fAlpha = 0.5D;
  
  public void estimateCPTs(BayesNet paramBayesNet) throws Exception {
    throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
  }
  
  public void updateClassifier(BayesNet paramBayesNet, Instance paramInstance) throws Exception {
    throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
  }
  
  public double[] distributionForInstance(BayesNet paramBayesNet, Instance paramInstance) throws Exception {
    throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
  }
  
  public void initCPTs(BayesNet paramBayesNet) throws Exception {
    throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tInitial count (alpha)\n", "A", 1, "-A <alpha>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('A', paramArrayOfString);
    if (str.length() != 0) {
      this.m_fAlpha = (new Float(str)).floatValue();
    } else {
      this.m_fAlpha = 0.5D;
    } 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[2];
    byte b = 0;
    arrayOfString[b++] = "-A";
    arrayOfString[b++] = "" + this.m_fAlpha;
    return arrayOfString;
  }
  
  public void setAlpha(double paramDouble) {
    this.m_fAlpha = paramDouble;
  }
  
  public double getAlpha() {
    return this.m_fAlpha;
  }
  
  public String alphaTipText() {
    return "Alpha is used for estimating the probability tables and can be interpreted as the initial count on each value.";
  }
  
  public String globalInfo() {
    return "An Estimator is an algorithm for finding the conditional probability tables of the Bayes Network.";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\estimate\BayesNetEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */