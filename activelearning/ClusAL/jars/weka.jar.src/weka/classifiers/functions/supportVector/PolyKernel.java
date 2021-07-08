package weka.classifiers.functions.supportVector;

import weka.core.Instance;
import weka.core.Instances;

public class PolyKernel extends CachedKernel {
  private boolean m_lowerOrder = false;
  
  private double m_exponent = 1.0D;
  
  public PolyKernel(Instances paramInstances, int paramInt, double paramDouble, boolean paramBoolean) {
    super(paramInstances, paramInt);
    this.m_exponent = paramDouble;
    this.m_lowerOrder = paramBoolean;
    this.m_data = paramInstances;
  }
  
  protected double evaluate(int paramInt1, int paramInt2, Instance paramInstance) throws Exception {
    double d;
    if (paramInt1 == paramInt2) {
      d = dotProd(paramInstance, paramInstance);
    } else {
      d = dotProd(paramInstance, this.m_data.instance(paramInt2));
    } 
    if (this.m_lowerOrder)
      d++; 
    if (this.m_exponent != 1.0D)
      d = Math.pow(d, this.m_exponent); 
    return d;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\supportVector\PolyKernel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */