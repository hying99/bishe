package weka.core;

import java.io.Serializable;

public class SingleIndex implements Serializable {
  protected String m_IndexString = "";
  
  protected int m_SelectedIndex = -1;
  
  protected int m_Upper = -1;
  
  public SingleIndex() {}
  
  public SingleIndex(String paramString) {
    setSingleIndex(paramString);
  }
  
  public void setUpper(int paramInt) {
    if (paramInt >= 0) {
      this.m_Upper = paramInt;
      setValue();
    } 
  }
  
  public String getSingleIndex() {
    return this.m_IndexString;
  }
  
  public void setSingleIndex(String paramString) {
    this.m_IndexString = paramString;
    this.m_SelectedIndex = -1;
  }
  
  public String toString() {
    if (this.m_IndexString.equals(""))
      return "No index set"; 
    if (this.m_Upper == -1)
      throw new RuntimeException("Upper limit has not been specified"); 
    return this.m_IndexString;
  }
  
  public int getIndex() {
    if (this.m_IndexString.equals(""))
      throw new RuntimeException("No index set"); 
    if (this.m_Upper == -1)
      throw new RuntimeException("No upper limit has been specified for index"); 
    return this.m_SelectedIndex;
  }
  
  public static String indexToString(int paramInt) {
    return "" + (paramInt + 1);
  }
  
  protected void setValue() {
    if (this.m_IndexString.equals(""))
      throw new RuntimeException("No index set"); 
    if (this.m_IndexString.toLowerCase().equals("first")) {
      this.m_SelectedIndex = 0;
    } else if (this.m_IndexString.toLowerCase().equals("last")) {
      this.m_SelectedIndex = this.m_Upper;
    } else {
      this.m_SelectedIndex = Integer.parseInt(this.m_IndexString) - 1;
      if (this.m_SelectedIndex < 0) {
        this.m_IndexString = "";
        throw new IllegalArgumentException("Index must be greater than zero");
      } 
      if (this.m_SelectedIndex > this.m_Upper) {
        this.m_IndexString = "";
        throw new IllegalArgumentException("Index is too large");
      } 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0)
        throw new Exception("Usage: SingleIndex <indexspec>"); 
      SingleIndex singleIndex = new SingleIndex();
      singleIndex.setSingleIndex(paramArrayOfString[0]);
      singleIndex.setUpper(9);
      System.out.println("Input: " + paramArrayOfString[0] + "\n" + singleIndex.toString());
      int i = singleIndex.getIndex();
      System.out.println(i + "");
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\SingleIndex.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */