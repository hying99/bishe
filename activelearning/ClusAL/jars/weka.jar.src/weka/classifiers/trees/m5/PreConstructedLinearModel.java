package weka.classifiers.trees.m5;

import java.io.Serializable;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class PreConstructedLinearModel extends Classifier implements Serializable {
  private double[] m_coefficients;
  
  private double m_intercept;
  
  private Instances m_instancesHeader;
  
  private int m_numParameters;
  
  public PreConstructedLinearModel(double[] paramArrayOfdouble, double paramDouble) {
    this.m_coefficients = paramArrayOfdouble;
    this.m_intercept = paramDouble;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfdouble.length; b2++) {
      if (paramArrayOfdouble[b2] != 0.0D)
        b1++; 
    } 
    this.m_numParameters = b1;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_instancesHeader = new Instances(paramInstances, 0);
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    double d = 0.0D;
    for (byte b = 0; b < this.m_coefficients.length; b++) {
      if (b != paramInstance.classIndex() && !paramInstance.isMissing(b))
        d += this.m_coefficients[b] * paramInstance.value(b); 
    } 
    d += this.m_intercept;
    return d;
  }
  
  public int numParameters() {
    return this.m_numParameters;
  }
  
  public double[] coefficients() {
    return this.m_coefficients;
  }
  
  public double intercept() {
    return this.m_intercept;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\n" + this.m_instancesHeader.classAttribute().name() + " = ");
    boolean bool = true;
    for (byte b = 0; b < this.m_coefficients.length; b++) {
      if (this.m_coefficients[b] != 0.0D) {
        double d = this.m_coefficients[b];
        if (bool) {
          stringBuffer.append("\n\t" + Utils.doubleToString(d, 12, 4).trim() + " * " + this.m_instancesHeader.attribute(b).name() + " ");
          bool = false;
        } else {
          stringBuffer.append("\n\t" + ((this.m_coefficients[b] < 0.0D) ? ("- " + Utils.doubleToString(Math.abs(d), 12, 4).trim()) : ("+ " + Utils.doubleToString(Math.abs(d), 12, 4).trim())) + " * " + this.m_instancesHeader.attribute(b).name() + " ");
        } 
      } 
    } 
    stringBuffer.append("\n\t" + ((this.m_intercept < 0.0D) ? "- " : "+ ") + Utils.doubleToString(Math.abs(this.m_intercept), 12, 4).trim());
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\m5\PreConstructedLinearModel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */