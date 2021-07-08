package weka.core;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

public class ProtectedProperties extends Properties {
  private boolean closed = false;
  
  public ProtectedProperties(Properties paramProperties) {
    Enumeration enumeration = paramProperties.propertyNames();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      String str2 = paramProperties.getProperty(str1);
      super.setProperty(str1, str2);
    } 
    this.closed = true;
  }
  
  public Object setProperty(String paramString1, String paramString2) {
    if (this.closed)
      throw new UnsupportedOperationException("ProtectedProperties cannot be modified!"); 
    return super.setProperty(paramString1, paramString2);
  }
  
  public void load(InputStream paramInputStream) {
    throw new UnsupportedOperationException("ProtectedProperties cannot be modified!");
  }
  
  public void clear() {
    throw new UnsupportedOperationException("ProtectedProperties cannot be modified!");
  }
  
  public Object put(Object paramObject1, Object paramObject2) {
    if (this.closed)
      throw new UnsupportedOperationException("ProtectedProperties cannot be modified!"); 
    return super.put(paramObject1, paramObject2);
  }
  
  public void putAll(Map paramMap) {
    throw new UnsupportedOperationException("ProtectedProperties cannot be modified!");
  }
  
  public Object remove(Object paramObject) {
    throw new UnsupportedOperationException("ProtectedProperties cannot be modified!");
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\ProtectedProperties.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */