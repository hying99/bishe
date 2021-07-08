package weka.core.xml;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;

public class MethodHandler {
  protected Hashtable m_Methods = null;
  
  public MethodHandler() {
    this.m_Methods = new Hashtable();
  }
  
  public Enumeration keys() {
    return this.m_Methods.keys();
  }
  
  public void add(String paramString, Method paramMethod) {
    if (paramMethod != null)
      this.m_Methods.put(paramString, paramMethod); 
  }
  
  public void add(Class paramClass, Method paramMethod) {
    if (paramMethod != null)
      this.m_Methods.put(paramClass, paramMethod); 
  }
  
  public boolean remove(String paramString) {
    return (this.m_Methods.remove(paramString) != null);
  }
  
  public boolean remove(Class paramClass) {
    return (this.m_Methods.remove(paramClass) != null);
  }
  
  public boolean contains(String paramString) {
    return this.m_Methods.containsKey(paramString);
  }
  
  public boolean contains(Class paramClass) {
    return this.m_Methods.containsKey(paramClass);
  }
  
  public Method get(String paramString) {
    return (Method)this.m_Methods.get(paramString);
  }
  
  public Method get(Class paramClass) {
    return (Method)this.m_Methods.get(paramClass);
  }
  
  public int size() {
    return this.m_Methods.size();
  }
  
  public void clear() {
    this.m_Methods.clear();
  }
  
  public String toString() {
    return this.m_Methods.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\xml\MethodHandler.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */