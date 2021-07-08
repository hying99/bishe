package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class MultiBoostAB extends AdaBoostM1 {
  protected int m_NumSubCmtys = 3;
  
  protected Random m_Random = null;
  
  public String globalInfo() {
    return "Class for boosting a classifier using the MultiBoosting method.\n\nMultiBoosting is an extension to the highly successful AdaBoost technique for forming decision committees. MultiBoosting can be viewed as combining AdaBoost with wagging. It is able to harness both AdaBoost's high bias and variance reduction with wagging's superior variance reduction. Using C4.5 as the base learning algorithm, Multi-boosting is demonstrated to produce decision committees with lower error than either AdaBoost or wagging significantly more often than the reverse over a large representative cross-section of UCI data sets. It offers the further advantage over AdaBoost of suiting parallel execution. For more information, see\n\nGeoffrey I. Webb (2000). \"MultiBoosting: A Technique for Combining Boosting and Wagging\".  Machine Learning, 40(2): 159-196, Kluwer Academic Publishers, Boston";
  }
  
  public Enumeration listOptions() {
    Enumeration enumeration = super.listOptions();
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tNumber of sub-committees. (Default 10)", "C", 1, "-C <num>"));
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('C', paramArrayOfString);
    if (str.length() != 0) {
      setNumSubCmtys(Integer.parseInt(str));
    } else {
      setNumSubCmtys(3);
    } 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 2];
    arrayOfString2[0] = "-C";
    arrayOfString2[1] = "" + getNumSubCmtys();
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 2, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public String numSubCmtysTipText() {
    return "Sets the (approximate) number of subcommittees.";
  }
  
  public void setNumSubCmtys(int paramInt) {
    this.m_NumSubCmtys = paramInt;
  }
  
  public int getNumSubCmtys() {
    return this.m_NumSubCmtys;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_Random = new Random(this.m_Seed);
    super.buildClassifier(paramInstances);
    this.m_Random = null;
  }
  
  protected void setWeights(Instances paramInstances, double paramDouble) throws Exception {
    int i = this.m_Classifiers.length / this.m_NumSubCmtys;
    if ((this.m_NumIterations + 1) % i == 0) {
      System.err.println(this.m_NumIterations + " " + i);
      double d1 = paramInstances.sumOfWeights();
      for (byte b1 = 0; b1 < paramInstances.numInstances(); b1++)
        paramInstances.instance(b1).setWeight(-Math.log(this.m_Random.nextDouble() * 9999.0D / 10000.0D)); 
      double d2 = paramInstances.sumOfWeights();
      for (byte b2 = 0; b2 < paramInstances.numInstances(); b2++)
        paramInstances.instance(b2).setWeight(paramInstances.instance(b2).weight() * d1 / d2); 
    } else {
      super.setWeights(paramInstances, paramDouble);
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_NumIterations == 0) {
      stringBuffer.append("MultiBoostAB: No model built yet.\n");
    } else if (this.m_NumIterations == 1) {
      stringBuffer.append("MultiBoostAB: No boosting possible, one classifier used!\n");
      stringBuffer.append(this.m_Classifiers[0].toString() + "\n");
    } else {
      stringBuffer.append("MultiBoostAB: Base classifiers and their weights: \n\n");
      for (byte b = 0; b < this.m_NumIterations; b++) {
        stringBuffer.append(this.m_Classifiers[b].toString() + "\n\n");
        stringBuffer.append("Weight: " + Utils.roundDouble(this.m_Betas[b], 2) + "\n\n");
      } 
      stringBuffer.append("Number of performed Iterations: " + this.m_NumIterations + "\n");
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new MultiBoostAB(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\MultiBoostAB.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */