package weka.core;

public class Tag {
  protected int m_ID;
  
  protected String m_Readable;
  
  public Tag(int paramInt, String paramString) {
    this.m_ID = paramInt;
    this.m_Readable = paramString;
  }
  
  public int getID() {
    return this.m_ID;
  }
  
  public String getReadable() {
    return this.m_Readable;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Tag.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */