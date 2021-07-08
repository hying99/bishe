package weka.gui.beans;

import java.util.EventObject;

public class TextEvent extends EventObject {
  protected String m_text;
  
  protected String m_textTitle;
  
  public TextEvent(Object paramObject, String paramString1, String paramString2) {
    super(paramObject);
    this.m_text = paramString1;
    this.m_textTitle = paramString2;
  }
  
  public String getText() {
    return this.m_text;
  }
  
  public String getTextTitle() {
    return this.m_textTitle;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\TextEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */