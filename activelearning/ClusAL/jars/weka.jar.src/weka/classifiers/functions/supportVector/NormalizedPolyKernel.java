package weka.classifiers.functions.supportVector;

import weka.core.Instance;
import weka.core.Instances;

public class NormalizedPolyKernel extends PolyKernel {
  public NormalizedPolyKernel(Instances paramInstances, int paramInt, double paramDouble, boolean paramBoolean) {
    super(paramInstances, paramInt, paramDouble, paramBoolean);
  }
  
  public double eval(int paramInt1, int paramInt2, Instance paramInstance) throws Exception {
    double d = Math.sqrt(super.eval(paramInt1, paramInt1, paramInstance) * super.eval(paramInt2, paramInt2, this.m_data.instance(paramInt2)));
    return (d != 0.0D) ? (super.eval(paramInt1, paramInt2, paramInstance) / d) : 0.0D;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\supportVector\NormalizedPolyKernel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */