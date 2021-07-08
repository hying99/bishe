package weka.classifiers.functions.supportVector;

import weka.core.Instance;
import weka.core.Instances;

public class RBFKernel extends CachedKernel {
  private double[] m_kernelPrecalc;
  
  private double m_gamma = 0.01D;
  
  public RBFKernel(Instances paramInstances, int paramInt, double paramDouble) throws Exception {
    super(paramInstances, paramInt);
    this.m_gamma = paramDouble;
    this.m_kernelPrecalc = new double[paramInstances.numInstances()];
    for (byte b = 0; b < paramInstances.numInstances(); b++)
      this.m_kernelPrecalc[b] = dotProd(paramInstances.instance(b), paramInstances.instance(b)); 
  }
  
  protected double evaluate(int paramInt1, int paramInt2, Instance paramInstance) throws Exception {
    double d;
    if (paramInt1 == -1) {
      d = dotProd(paramInstance, paramInstance);
    } else {
      d = this.m_kernelPrecalc[paramInt1];
    } 
    Instance instance = this.m_data.instance(paramInt2);
    return Math.exp(this.m_gamma * (2.0D * dotProd(paramInstance, instance) - d - this.m_kernelPrecalc[paramInt2]));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\supportVector\RBFKernel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */