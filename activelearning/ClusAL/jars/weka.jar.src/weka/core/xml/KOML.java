package weka.core.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class KOML {
  protected static boolean m_Present = false;
  
  public static final String FILE_EXTENSION = ".koml";
  
  private static void checkForKOML() {
    try {
      Class.forName("fr.dyade.koala.xml.koml.KOMLSerializer");
      m_Present = true;
    } catch (Exception exception) {
      m_Present = false;
    } 
  }
  
  public static boolean isPresent() {
    return m_Present;
  }
  
  public static Object read(String paramString) throws Exception {
    return read(new FileInputStream(paramString));
  }
  
  public static Object read(File paramFile) throws Exception {
    return read(new FileInputStream(paramFile));
  }
  
  public static Object read(InputStream paramInputStream) throws Exception {
    Object object2 = null;
    Class clazz = Class.forName("fr.dyade.koala.xml.koml.KOMLDeserializer");
    Class[] arrayOfClass1 = new Class[2];
    arrayOfClass1[0] = InputStream.class;
    arrayOfClass1[1] = boolean.class;
    Object[] arrayOfObject1 = new Object[2];
    arrayOfObject1[0] = paramInputStream;
    arrayOfObject1[1] = new Boolean(false);
    Constructor constructor = clazz.getConstructor(arrayOfClass1);
    Object object1 = constructor.newInstance(arrayOfObject1);
    Class[] arrayOfClass2 = new Class[0];
    Method method1 = clazz.getMethod("readObject", arrayOfClass2);
    Object[] arrayOfObject2 = new Object[0];
    Class[] arrayOfClass3 = new Class[0];
    Method method2 = clazz.getMethod("close", arrayOfClass3);
    Object[] arrayOfObject3 = new Object[0];
    try {
      object2 = method1.invoke(object1, arrayOfObject2);
    } catch (Exception exception) {
      object2 = null;
    } finally {
      method2.invoke(object1, arrayOfObject3);
    } 
    return object2;
  }
  
  public static boolean write(String paramString, Object paramObject) throws Exception {
    return write(new FileOutputStream(paramString), paramObject);
  }
  
  public static boolean write(File paramFile, Object paramObject) throws Exception {
    return write(new FileOutputStream(paramFile), paramObject);
  }
  
  public static boolean write(OutputStream paramOutputStream, Object paramObject) throws Exception {
    boolean bool = false;
    Class clazz = Class.forName("fr.dyade.koala.xml.koml.KOMLSerializer");
    Class[] arrayOfClass1 = new Class[2];
    arrayOfClass1[0] = OutputStream.class;
    arrayOfClass1[1] = boolean.class;
    Object[] arrayOfObject1 = new Object[2];
    arrayOfObject1[0] = paramOutputStream;
    arrayOfObject1[1] = new Boolean(false);
    Constructor constructor = clazz.getConstructor(arrayOfClass1);
    Object object = constructor.newInstance(arrayOfObject1);
    Class[] arrayOfClass2 = new Class[1];
    arrayOfClass2[0] = Object.class;
    Method method1 = clazz.getMethod("addObject", arrayOfClass2);
    Object[] arrayOfObject2 = new Object[1];
    arrayOfObject2[0] = paramObject;
    Class[] arrayOfClass3 = new Class[0];
    Method method2 = clazz.getMethod("close", arrayOfClass3);
    Object[] arrayOfObject3 = new Object[0];
    try {
      method1.invoke(object, arrayOfObject2);
      bool = true;
    } catch (Exception exception) {
      bool = false;
    } finally {
      method2.invoke(object, arrayOfObject3);
    } 
    return bool;
  }
  
  static {
    checkForKOML();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\xml\KOML.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */