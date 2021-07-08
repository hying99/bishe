package weka.gui.beans;

import java.util.EventObject;

public class GraphEvent extends EventObject {
  protected String m_graphString;
  
  protected String m_graphTitle;
  
  protected int m_graphType;
  
  public GraphEvent(Object paramObject, String paramString1, String paramString2, int paramInt) {
    super(paramObject);
    this.m_graphString = paramString1;
    this.m_graphTitle = paramString2;
    this.m_graphType = paramInt;
  }
  
  public String getGraphString() {
    return this.m_graphString;
  }
  
  public String getGraphTitle() {
    return this.m_graphTitle;
  }
  
  public int getGraphType() {
    return this.m_graphType;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\GraphEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */