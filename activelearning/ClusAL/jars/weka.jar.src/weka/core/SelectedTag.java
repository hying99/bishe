package weka.core;

public class SelectedTag {
  protected int m_Selected;
  
  protected Tag[] m_Tags;
  
  public SelectedTag(int paramInt, Tag[] paramArrayOfTag) {
    for (byte b = 0; b < paramArrayOfTag.length; b++) {
      if (paramArrayOfTag[b].getID() == paramInt) {
        this.m_Selected = b;
        this.m_Tags = paramArrayOfTag;
        return;
      } 
    } 
    throw new IllegalArgumentException("Selected tag is not valid");
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !paramObject.getClass().equals(getClass()))
      return false; 
    SelectedTag selectedTag = (SelectedTag)paramObject;
    return (selectedTag.getTags() == this.m_Tags && selectedTag.getSelectedTag() == this.m_Tags[this.m_Selected]);
  }
  
  public Tag getSelectedTag() {
    return this.m_Tags[this.m_Selected];
  }
  
  public Tag[] getTags() {
    return this.m_Tags;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\SelectedTag.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */