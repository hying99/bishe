package weka.classifiers.misc;

import java.io.Serializable;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

public class HyperPipes extends Classifier {
  protected int m_ClassIndex;
  
  protected Instances m_Instances;
  
  protected HyperPipe[] m_HyperPipes;
  
  public String globalInfo() {
    return "Class implementing a HyperPipe classifier. For each category a HyperPipe is constructed that contains all points of that category (essentially records the attribute bounds observed for each category). Test instances are classified according to the category that \"most contains the instance\". Does not handle numeric class, or missing values in test cases. Extremely simple algorithm, but has the advantage of being extremely fast, and works quite well when you have \"smegloads\" of attributes.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.classIndex() == -1)
      throw new Exception("No class attribute assigned"); 
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("HyperPipes: class attribute needs to be nominal!"); 
    this.m_ClassIndex = paramInstances.classIndex();
    this.m_Instances = new Instances(paramInstances, 0);
    this.m_HyperPipes = new HyperPipe[paramInstances.numClasses()];
    byte b;
    for (b = 0; b < this.m_HyperPipes.length; b++)
      this.m_HyperPipes[b] = new HyperPipe(this, new Instances(paramInstances, 0)); 
    for (b = 0; b < paramInstances.numInstances(); b++)
      updateClassifier(paramInstances.instance(b)); 
  }
  
  public void updateClassifier(Instance paramInstance) throws Exception {
    if (paramInstance.classIsMissing())
      return; 
    this.m_HyperPipes[(int)paramInstance.classValue()].addInstance(paramInstance);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = new double[this.m_HyperPipes.length];
    for (byte b = 0; b < this.m_HyperPipes.length; b++)
      arrayOfDouble[b] = this.m_HyperPipes[b].partialContains(paramInstance); 
    double d = Utils.sum(arrayOfDouble);
    if (d <= 0.0D) {
      for (byte b1 = 0; b1 < arrayOfDouble.length; b1++)
        arrayOfDouble[b1] = 1.0D / arrayOfDouble.length; 
      return arrayOfDouble;
    } 
    Utils.normalize(arrayOfDouble, d);
    return arrayOfDouble;
  }
  
  public String toString() {
    if (this.m_HyperPipes == null)
      return "HyperPipes classifier"; 
    StringBuffer stringBuffer = new StringBuffer("HyperPipes classifier\n");
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new HyperPipes(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
  
  class HyperPipe implements Serializable {
    protected double[][] m_NumericBounds;
    
    protected boolean[][] m_NominalBounds;
    
    private final HyperPipes this$0;
    
    public HyperPipe(HyperPipes this$0, Instances param1Instances) throws Exception {
      this.this$0 = this$0;
      this.m_NumericBounds = new double[param1Instances.numAttributes()][];
      this.m_NominalBounds = new boolean[param1Instances.numAttributes()][];
      byte b;
      for (b = 0; b < param1Instances.numAttributes(); b++) {
        switch (param1Instances.attribute(b).type()) {
          case 0:
            this.m_NumericBounds[b] = new double[2];
            this.m_NumericBounds[b][0] = Double.POSITIVE_INFINITY;
            this.m_NumericBounds[b][1] = Double.NEGATIVE_INFINITY;
            break;
          case 1:
            this.m_NominalBounds[b] = new boolean[param1Instances.attribute(b).numValues()];
            break;
          default:
            throw new UnsupportedAttributeTypeException("Cannot process string attributes!");
        } 
      } 
      for (b = 0; b < param1Instances.numInstances(); b++)
        addInstance(param1Instances.instance(b)); 
    }
    
    public void addInstance(Instance param1Instance) throws Exception {
      for (byte b = 0; b < param1Instance.numAttributes(); b++) {
        if (b != this.this$0.m_ClassIndex && !param1Instance.isMissing(b)) {
          double d = param1Instance.value(b);
          if (this.m_NumericBounds[b] != null) {
            if (d < this.m_NumericBounds[b][0])
              this.m_NumericBounds[b][0] = d; 
            if (d > this.m_NumericBounds[b][1])
              this.m_NumericBounds[b][1] = d; 
          } else {
            this.m_NominalBounds[b][(int)d] = true;
          } 
        } 
      } 
    }
    
    public double partialContains(Instance param1Instance) throws Exception {
      byte b1 = 0;
      for (byte b2 = 0; b2 < param1Instance.numAttributes(); b2++) {
        if (b2 != this.this$0.m_ClassIndex && !param1Instance.isMissing(b2)) {
          double d = param1Instance.value(b2);
          if (this.m_NumericBounds[b2] != null) {
            if (d >= this.m_NumericBounds[b2][0] && d <= this.m_NumericBounds[b2][1])
              b1++; 
          } else if (this.m_NominalBounds[b2][(int)d]) {
            b1++;
          } 
        } 
      } 
      return b1 / (param1Instance.numAttributes() - 1);
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\misc\HyperPipes.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */