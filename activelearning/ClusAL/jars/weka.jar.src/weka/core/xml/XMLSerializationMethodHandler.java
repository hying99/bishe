package weka.core.xml;

import java.lang.reflect.Method;
import org.w3c.dom.Element;

public class XMLSerializationMethodHandler {
  protected MethodHandler m_ReadMethods = null;
  
  protected MethodHandler m_WriteMethods = null;
  
  protected Object owner = null;
  
  public XMLSerializationMethodHandler(Object paramObject) throws Exception {
    this.owner = paramObject;
    this.m_ReadMethods = new MethodHandler();
    this.m_WriteMethods = new MethodHandler();
    clear();
  }
  
  protected void addMethods(MethodHandler paramMethodHandler, Method paramMethod, Method[] paramArrayOfMethod) {
    for (byte b = 0; b < paramArrayOfMethod.length; b++) {
      Method method = paramArrayOfMethod[b];
      if (!paramMethod.equals(method) && paramMethod.getReturnType().equals(method.getReturnType()) && (paramMethod.getParameterTypes()).length == (method.getParameterTypes()).length) {
        boolean bool = true;
        for (byte b1 = 0; b1 < (paramMethod.getParameterTypes()).length; b1++) {
          if (!paramMethod.getParameterTypes()[b1].equals(method.getParameterTypes()[b1])) {
            bool = false;
            break;
          } 
        } 
        if (bool) {
          String str = method.getName();
          str = str.replaceAll("read|write", "");
          str = str.substring(0, 1).toLowerCase() + str.substring(1);
          paramMethodHandler.add(str, method);
        } 
      } 
    } 
  }
  
  protected void addMethods() throws Exception {
    Class[] arrayOfClass = new Class[1];
    arrayOfClass[0] = Element.class;
    Method method = this.owner.getClass().getMethod("readFromXML", arrayOfClass);
    addMethods(this.m_ReadMethods, method, this.owner.getClass().getMethods());
    arrayOfClass = new Class[3];
    arrayOfClass[0] = Element.class;
    arrayOfClass[1] = Object.class;
    arrayOfClass[2] = String.class;
    method = this.owner.getClass().getMethod("writeToXML", arrayOfClass);
    addMethods(this.m_WriteMethods, method, this.owner.getClass().getMethods());
  }
  
  public static Method findReadMethod(Object paramObject, String paramString) {
    Method method = null;
    Class[] arrayOfClass = new Class[1];
    arrayOfClass[0] = Element.class;
    try {
      method = paramObject.getClass().getMethod(paramString, arrayOfClass);
    } catch (Exception exception) {
      method = null;
    } 
    return method;
  }
  
  public static Method findWriteMethod(Object paramObject, String paramString) {
    Method method = null;
    Class[] arrayOfClass = new Class[3];
    arrayOfClass[0] = Element.class;
    arrayOfClass[1] = Object.class;
    arrayOfClass[2] = String.class;
    try {
      method = paramObject.getClass().getMethod(paramString, arrayOfClass);
    } catch (Exception exception) {
      method = null;
    } 
    return method;
  }
  
  public void clear() {
    this.m_ReadMethods.clear();
    this.m_WriteMethods.clear();
    try {
      addMethods();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public MethodHandler read() {
    return this.m_ReadMethods;
  }
  
  public MethodHandler write() {
    return this.m_WriteMethods;
  }
  
  public String toString() {
    return "Read Methods:\n" + read() + "\n\n" + "Write Methods:\n" + write();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\xml\XMLSerializationMethodHandler.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */