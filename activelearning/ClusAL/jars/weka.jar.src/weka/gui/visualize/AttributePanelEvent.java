package weka.gui.visualize;

public class AttributePanelEvent {
  public boolean m_xChange;
  
  public boolean m_yChange;
  
  public int m_indexVal;
  
  public AttributePanelEvent(boolean paramBoolean1, boolean paramBoolean2, int paramInt) {
    this.m_xChange = paramBoolean1;
    this.m_yChange = paramBoolean2;
    this.m_indexVal = paramInt;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\AttributePanelEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */