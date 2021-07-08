package weka.core;

public class Option {
  private String m_Description;
  
  private String m_Synopsis;
  
  private String m_Name;
  
  private int m_NumArguments;
  
  public Option(String paramString1, String paramString2, int paramInt, String paramString3) {
    this.m_Description = paramString1;
    this.m_Name = paramString2;
    this.m_NumArguments = paramInt;
    this.m_Synopsis = paramString3;
  }
  
  public String description() {
    return this.m_Description;
  }
  
  public String name() {
    return this.m_Name;
  }
  
  public int numArguments() {
    return this.m_NumArguments;
  }
  
  public String synopsis() {
    return this.m_Synopsis;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Option.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */