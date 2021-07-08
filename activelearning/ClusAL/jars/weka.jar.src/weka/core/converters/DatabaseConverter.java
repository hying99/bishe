package weka.core.converters;

public interface DatabaseConverter {
  String getUrl();
  
  String getUser();
  
  void setUrl(String paramString);
  
  void setUser(String paramString);
  
  void setPassword(String paramString);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\DatabaseConverter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */