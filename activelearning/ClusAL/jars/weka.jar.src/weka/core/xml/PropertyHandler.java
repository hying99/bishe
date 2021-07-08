package weka.core.xml;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

public class PropertyHandler {
  protected Hashtable m_Ignored = null;
  
  protected Hashtable m_Allowed = null;
  
  public PropertyHandler() {
    this.m_Ignored = new Hashtable();
    this.m_Allowed = new Hashtable();
  }
  
  public Enumeration ignored() {
    return this.m_Ignored.keys();
  }
  
  public void addIgnored(String paramString) {
    HashSet hashSet = new HashSet();
    hashSet.add(paramString);
    this.m_Ignored.put(paramString, hashSet);
  }
  
  public void addIgnored(Class paramClass, String paramString) {
    HashSet hashSet;
    if (this.m_Ignored.contains(paramClass)) {
      hashSet = (HashSet)this.m_Ignored.get(paramClass);
    } else {
      hashSet = new HashSet();
      this.m_Ignored.put(paramClass, hashSet);
    } 
    hashSet.add(paramString);
  }
  
  public boolean removeIgnored(String paramString) {
    return (this.m_Ignored.remove(paramString) != null);
  }
  
  public boolean removeIgnored(Class paramClass, String paramString) {
    HashSet hashSet;
    if (this.m_Ignored.contains(paramClass)) {
      hashSet = (HashSet)this.m_Ignored.get(paramClass);
    } else {
      hashSet = new HashSet();
    } 
    return hashSet.remove(paramString);
  }
  
  public boolean isIgnored(String paramString) {
    return this.m_Ignored.contains(paramString);
  }
  
  public boolean isIgnored(Class paramClass, String paramString) {
    HashSet hashSet;
    if (this.m_Ignored.contains(paramClass)) {
      hashSet = (HashSet)this.m_Ignored.get(paramClass);
    } else {
      hashSet = new HashSet();
    } 
    return hashSet.contains(paramString);
  }
  
  public boolean isIgnored(Object paramObject, String paramString) {
    boolean bool = false;
    Enumeration enumeration = ignored();
    while (enumeration.hasMoreElements()) {
      Class clazz2 = (Class)enumeration.nextElement();
      if (!(clazz2 instanceof Class))
        continue; 
      Class clazz1 = clazz2;
      if (clazz1.isInstance(paramObject)) {
        HashSet hashSet = (HashSet)this.m_Ignored.get(clazz1);
        bool = hashSet.contains(paramString);
        break;
      } 
    } 
    return bool;
  }
  
  public Enumeration allowed() {
    return this.m_Allowed.keys();
  }
  
  public void addAllowed(Class paramClass, String paramString) {
    HashSet hashSet = (HashSet)this.m_Allowed.get(paramClass);
    if (hashSet == null) {
      hashSet = new HashSet();
      this.m_Allowed.put(paramClass, hashSet);
    } 
    hashSet.add(paramString);
  }
  
  public boolean removeAllowed(Class paramClass, String paramString) {
    boolean bool = false;
    HashSet hashSet = (HashSet)this.m_Allowed.get(paramClass);
    if (hashSet != null)
      bool = hashSet.remove(paramString); 
    return bool;
  }
  
  public boolean isAllowed(Class paramClass, String paramString) {
    boolean bool = true;
    HashSet hashSet = (HashSet)this.m_Allowed.get(paramClass);
    if (hashSet != null)
      bool = hashSet.contains(paramString); 
    return bool;
  }
  
  public boolean isAllowed(Object paramObject, String paramString) {
    boolean bool = true;
    Enumeration enumeration = allowed();
    while (enumeration.hasMoreElements()) {
      Class clazz = enumeration.nextElement();
      if (clazz.isInstance(paramObject)) {
        HashSet hashSet = (HashSet)this.m_Allowed.get(clazz);
        bool = hashSet.contains(paramString);
        break;
      } 
    } 
    return bool;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\xml\PropertyHandler.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */