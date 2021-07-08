package weka.filters.unsupervised.attribute;

import weka.core.Instances;
import weka.filters.Filter;

public abstract class PotentialClassIgnorer extends Filter {
  protected boolean m_IgnoreClass = false;
  
  protected int m_ClassIndex = -1;
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    boolean bool = super.setInputFormat(paramInstances);
    if (this.m_IgnoreClass) {
      this.m_ClassIndex = inputFormatPeek().classIndex();
      inputFormatPeek().setClassIndex(-1);
    } 
    return bool;
  }
  
  public final Instances getOutputFormat() {
    if (this.m_IgnoreClass)
      outputFormatPeek().setClassIndex(this.m_ClassIndex); 
    return super.getOutputFormat();
  }
  
  public void setIgnoreClass(boolean paramBoolean) {
    this.m_IgnoreClass = paramBoolean;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\PotentialClassIgnorer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */