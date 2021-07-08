package weka.core.xml;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import weka.core.Version;

public class XMLSerialization {
  public static final String TAG_OBJECT = "object";
  
  public static final String ATT_VERSION = "version";
  
  public static final String ATT_NAME = "name";
  
  public static final String ATT_CLASS = "class";
  
  public static final String ATT_PRIMITIVE = "primitive";
  
  public static final String ATT_ARRAY = "array";
  
  public static final String VAL_YES = "yes";
  
  public static final String VAL_NO = "no";
  
  public static final String VAL_ROOT = "__root__";
  
  public static final String ROOT_NODE = "object";
  
  public static final String DOCTYPE = "<!DOCTYPE object\n[\n   <!ELEMENT object (#PCDATA | object)*>\n   <!ATTLIST object name      CDATA #REQUIRED>\n   <!ATTLIST object class     CDATA #REQUIRED>\n   <!ATTLIST object primitive CDATA \"yes\">\n   <!ATTLIST object array     CDATA \"no\">\n   <!ATTLIST object version   CDATA \"3.4.4\">\n]\n>";
  
  protected XMLDocument m_Document = null;
  
  protected PropertyHandler m_Properties = null;
  
  protected XMLSerializationMethodHandler m_CustomMethods = null;
  
  protected Hashtable m_ClassnameOverride = null;
  
  public XMLSerialization() throws Exception {
    clear();
  }
  
  public void clear() throws Exception {
    this.m_Document = new XMLDocument();
    this.m_Document.setValidating(true);
    this.m_Document.newDocument("<!DOCTYPE object\n[\n   <!ELEMENT object (#PCDATA | object)*>\n   <!ATTLIST object name      CDATA #REQUIRED>\n   <!ATTLIST object class     CDATA #REQUIRED>\n   <!ATTLIST object primitive CDATA \"yes\">\n   <!ATTLIST object array     CDATA \"no\">\n   <!ATTLIST object version   CDATA \"3.4.4\">\n]\n>", "object");
    this.m_Properties = new PropertyHandler();
    this.m_CustomMethods = new XMLSerializationMethodHandler(this);
    this.m_ClassnameOverride = new Hashtable();
    this.m_ClassnameOverride.put(File.class, File.class.getName());
    setVersion("3.4.4");
  }
  
  private void setVersion(String paramString) {
    Document document = this.m_Document.getDocument();
    document.getDocumentElement().setAttribute("version", paramString);
  }
  
  public String getVersion() {
    Document document = this.m_Document.getDocument();
    return document.getDocumentElement().getAttribute("version");
  }
  
  private void checkVersion() {
    Version version = new Version();
    String str = getVersion();
    if (str.equals("")) {
      System.out.println("WARNING: has no version!");
    } else if (version.isOlder(str)) {
      System.out.println("WARNING: loading a newer version (" + str + " > " + "3.4.4" + ")!");
    } else if (version.isNewer(str)) {
      System.out.println("NOTE: loading an older version (" + str + " < " + "3.4.4" + ")!");
    } 
  }
  
  protected Hashtable getDescriptors(Object paramObject) throws Exception {
    Hashtable hashtable = new Hashtable();
    BeanInfo beanInfo = Introspector.getBeanInfo(paramObject.getClass());
    PropertyDescriptor[] arrayOfPropertyDescriptor = beanInfo.getPropertyDescriptors();
    for (byte b = 0; b < arrayOfPropertyDescriptor.length; b++) {
      if (arrayOfPropertyDescriptor[b].getReadMethod() != null && arrayOfPropertyDescriptor[b].getWriteMethod() != null && !this.m_Properties.isIgnored(arrayOfPropertyDescriptor[b].getDisplayName()) && !this.m_Properties.isIgnored(paramObject, arrayOfPropertyDescriptor[b].getDisplayName()) && this.m_Properties.isAllowed(paramObject, arrayOfPropertyDescriptor[b].getDisplayName()))
        hashtable.put(arrayOfPropertyDescriptor[b].getDisplayName(), arrayOfPropertyDescriptor[b]); 
    } 
    return hashtable;
  }
  
  protected String getPath(Element paramElement) {
    String str;
    for (str = paramElement.getAttribute("name"); paramElement.getParentNode() != paramElement.getOwnerDocument(); str = paramElement.getAttribute("name") + "." + str)
      paramElement = (Element)paramElement.getParentNode(); 
    return str;
  }
  
  protected String booleanToString(boolean paramBoolean) {
    return paramBoolean ? "yes" : "no";
  }
  
  protected boolean stringToBoolean(String paramString) {
    return paramString.equals("yes") ? true : (paramString.equalsIgnoreCase("true"));
  }
  
  protected Element addElement(Element paramElement, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2) {
    Element element;
    if (paramElement == null) {
      element = this.m_Document.getDocument().getDocumentElement();
    } else {
      element = (Element)paramElement.appendChild(this.m_Document.getDocument().createElement("object"));
    } 
    element.setAttribute("name", paramString1);
    element.setAttribute("class", paramString2);
    element.setAttribute("primitive", booleanToString(paramBoolean1));
    element.setAttribute("array", booleanToString(paramBoolean2));
    return element;
  }
  
  protected String overrideClassname(Object paramObject) {
    String str = paramObject.getClass().getName();
    Enumeration enumeration = this.m_ClassnameOverride.keys();
    while (enumeration.hasMoreElements()) {
      Class clazz = enumeration.nextElement();
      if (clazz.isInstance(paramObject)) {
        str = (String)this.m_ClassnameOverride.get(clazz);
        break;
      } 
    } 
    return str;
  }
  
  protected String overrideClassname(String paramString) {
    String str = paramString;
    Enumeration enumeration = this.m_ClassnameOverride.keys();
    while (enumeration.hasMoreElements()) {
      Class clazz = enumeration.nextElement();
      if (clazz.getName().equals(paramString)) {
        str = (String)this.m_ClassnameOverride.get(clazz);
        break;
      } 
    } 
    return str;
  }
  
  protected PropertyDescriptor determineDescriptor(String paramString1, String paramString2) {
    PropertyDescriptor propertyDescriptor = null;
    try {
      propertyDescriptor = new PropertyDescriptor(paramString2, Class.forName(paramString1));
    } catch (Exception exception) {
      propertyDescriptor = null;
    } 
    return propertyDescriptor;
  }
  
  public void writeToXML(Element paramElement, Object paramObject, String paramString) throws Exception {
    String str;
    boolean bool;
    Object object = null;
    boolean bool1 = paramObject.getClass().isArray();
    if (bool1) {
      str = paramObject.getClass().getComponentType().getName();
      bool = paramObject.getClass().getComponentType().isPrimitive();
    } else {
      PropertyDescriptor propertyDescriptor = null;
      if (paramElement != null)
        propertyDescriptor = determineDescriptor(paramElement.getAttribute("class"), paramString); 
      if (propertyDescriptor != null) {
        bool = propertyDescriptor.getPropertyType().isPrimitive();
      } else {
        bool = paramObject.getClass().isPrimitive();
      } 
      if (bool) {
        str = propertyDescriptor.getPropertyType().getName();
      } else {
        object = paramObject;
        str = paramObject.getClass().getName();
      } 
    } 
    if (paramElement != null && stringToBoolean(paramElement.getAttribute("array")) && stringToBoolean(paramElement.getAttribute("primitive"))) {
      bool = true;
      str = paramElement.getAttribute("class");
    } 
    if (object != null) {
      str = overrideClassname(object);
    } else {
      str = overrideClassname(str);
    } 
    Element element = addElement(paramElement, paramString, str, bool, bool1);
    if (bool1) {
      for (byte b = 0; b < Array.getLength(paramObject); b++)
        invokeWriteToXML(element, Array.get(paramObject, b), Integer.toString(b)); 
    } else if (bool) {
      element.appendChild(element.getOwnerDocument().createTextNode(paramObject.toString()));
    } else {
      Hashtable hashtable = getDescriptors(paramObject);
      if (hashtable.size() == 0) {
        if (!paramObject.toString().equals(""))
          element.appendChild(element.getOwnerDocument().createTextNode(paramObject.toString())); 
      } else {
        Enumeration enumeration = hashtable.keys();
        while (enumeration.hasMoreElements()) {
          String str1 = enumeration.nextElement().toString();
          if (this.m_Properties.isIgnored(getPath(element) + "." + str1) || this.m_Properties.isIgnored(paramObject, getPath(element) + "." + str1) || !this.m_Properties.isAllowed(paramObject, str1))
            continue; 
          PropertyDescriptor propertyDescriptor = (PropertyDescriptor)hashtable.get(str1);
          Method method = propertyDescriptor.getReadMethod();
          Object object1 = method.invoke(paramObject, null);
          invokeWriteToXML(element, object1, str1);
        } 
      } 
    } 
  }
  
  protected void invokeWriteToXML(Element paramElement, Object paramObject, String paramString) throws Exception {
    if (paramObject == null)
      return; 
    try {
      Method method;
      boolean bool = paramObject.getClass().isArray();
      if (this.m_CustomMethods.write().contains(paramString)) {
        method = this.m_CustomMethods.write().get(paramObject.getClass());
      } else if (!bool && this.m_CustomMethods.write().contains(paramObject.getClass())) {
        method = this.m_CustomMethods.write().get(paramObject.getClass());
      } else {
        method = null;
      } 
      if (method != null) {
        Class[] arrayOfClass = new Class[3];
        arrayOfClass[0] = Element.class;
        arrayOfClass[1] = Object.class;
        arrayOfClass[2] = String.class;
        Object[] arrayOfObject = new Object[3];
        arrayOfObject[0] = paramElement;
        arrayOfObject[1] = paramObject;
        arrayOfObject[2] = paramString;
        method.invoke(this, arrayOfObject);
      } else {
        writeToXML(paramElement, paramObject, paramString);
      } 
    } catch (Exception exception) {
      System.out.println("PROBLEM (write): " + paramString);
      throw (Exception)exception.fillInStackTrace();
    } 
  }
  
  protected void writePostProcess(Object paramObject) throws Exception {}
  
  public XMLDocument toXML(Object paramObject) throws Exception {
    clear();
    invokeWriteToXML(null, paramObject, "__root__");
    writePostProcess(paramObject);
    return this.m_Document;
  }
  
  protected PropertyDescriptor getDescriptorByName(Object paramObject, String paramString) throws Exception {
    PropertyDescriptor propertyDescriptor = null;
    PropertyDescriptor[] arrayOfPropertyDescriptor = Introspector.getBeanInfo(paramObject.getClass()).getPropertyDescriptors();
    for (byte b = 0; b < arrayOfPropertyDescriptor.length; b++) {
      if (arrayOfPropertyDescriptor[b].getDisplayName().equals(paramString)) {
        propertyDescriptor = arrayOfPropertyDescriptor[b];
        break;
      } 
    } 
    return propertyDescriptor;
  }
  
  protected Class determineClass(String paramString) throws Exception {
    Class clazz;
    if (paramString.equals(boolean.class.getName())) {
      clazz = boolean.class;
    } else if (paramString.equals(byte.class.getName())) {
      clazz = byte.class;
    } else if (paramString.equals(char.class.getName())) {
      clazz = char.class;
    } else if (paramString.equals(double.class.getName())) {
      clazz = double.class;
    } else if (paramString.equals(float.class.getName())) {
      clazz = float.class;
    } else if (paramString.equals(int.class.getName())) {
      clazz = int.class;
    } else if (paramString.equals(long.class.getName())) {
      clazz = long.class;
    } else if (paramString.equals(short.class.getName())) {
      clazz = short.class;
    } else {
      clazz = Class.forName(paramString);
    } 
    return clazz;
  }
  
  protected Object getPrimitive(Element paramElement) throws Exception {
    Class clazz = determineClass(paramElement.getAttribute("class"));
    Object object = Array.newInstance(clazz, 1);
    if (clazz == boolean.class) {
      Array.set(object, 0, new Boolean(XMLDocument.getContent(paramElement)));
    } else if (clazz == byte.class) {
      Array.set(object, 0, new Byte(XMLDocument.getContent(paramElement)));
    } else if (clazz == char.class) {
      Array.set(object, 0, new Character(XMLDocument.getContent(paramElement).charAt(0)));
    } else if (clazz == double.class) {
      Array.set(object, 0, new Double(XMLDocument.getContent(paramElement)));
    } else if (clazz == float.class) {
      Array.set(object, 0, new Float(XMLDocument.getContent(paramElement)));
    } else if (clazz == int.class) {
      Array.set(object, 0, new Integer(XMLDocument.getContent(paramElement)));
    } else if (clazz == long.class) {
      Array.set(object, 0, new Long(XMLDocument.getContent(paramElement)));
    } else if (clazz == short.class) {
      Array.set(object, 0, new Short(XMLDocument.getContent(paramElement)));
    } else {
      throw new Exception("Cannot get primitive for class '" + clazz.getName() + "'!");
    } 
    return Array.get(object, 0);
  }
  
  public Object readFromXML(Element paramElement) throws Exception {
    Object object = null;
    String str2 = paramElement.getAttribute("name");
    String str1 = paramElement.getAttribute("class");
    boolean bool1 = stringToBoolean(paramElement.getAttribute("primitive"));
    boolean bool2 = stringToBoolean(paramElement.getAttribute("array"));
    Vector vector = XMLDocument.getChildTags(paramElement);
    Class clazz = determineClass(str1);
    if (bool2) {
      object = Array.newInstance(clazz, vector.size());
      for (byte b = 0; b < vector.size(); b++) {
        Element element = vector.get(b);
        Array.set(object, Integer.parseInt(element.getAttribute("name")), invokeReadFromXML(element));
      } 
    } else if (vector.size() == 0) {
      if (bool1) {
        object = getPrimitive(paramElement);
      } else {
        Class[] arrayOfClass = new Class[1];
        arrayOfClass[0] = String.class;
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = XMLDocument.getContent(paramElement);
        try {
          Constructor constructor = clazz.getConstructor(arrayOfClass);
          object = constructor.newInstance(arrayOfObject);
        } catch (Exception exception) {
          try {
            object = clazz.newInstance();
          } catch (Exception exception1) {
            object = null;
            System.out.println("ERROR: Can't instantiate '" + str1 + "'!");
          } 
        } 
      } 
    } else {
      object = clazz.newInstance();
      Hashtable hashtable = getDescriptors(object);
      for (byte b = 0; b < vector.size(); b++) {
        Element element = vector.get(b);
        String str = element.getAttribute("name");
        if (this.m_Properties.isIgnored(getPath(element)) || this.m_Properties.isIgnored(object, getPath(element)) || !this.m_Properties.isAllowed(object, str))
          continue; 
        PropertyDescriptor propertyDescriptor = (PropertyDescriptor)hashtable.get(str);
        if (propertyDescriptor == null) {
          if (!this.m_CustomMethods.read().contains(str))
            System.out.println("WARNING: unknown property '" + str2 + "." + str + "'!"); 
          continue;
        } 
        Method method = propertyDescriptor.getWriteMethod();
        Object[] arrayOfObject = new Object[1];
        Object object1 = invokeReadFromXML(element);
        Class clazz1 = method.getParameterTypes()[0];
        if (clazz1.isArray()) {
          if (Array.getLength(object1) == 0)
            continue; 
          arrayOfObject[0] = object1;
        } else {
          arrayOfObject[0] = object1;
        } 
        method.invoke(object, arrayOfObject);
        continue;
      } 
    } 
    return object;
  }
  
  protected Object invokeReadFromXML(Element paramElement) throws Exception {
    try {
      Method method;
      boolean bool = stringToBoolean(paramElement.getAttribute("array"));
      if (this.m_CustomMethods.read().contains(paramElement.getAttribute("name"))) {
        method = this.m_CustomMethods.read().get(paramElement.getAttribute("name"));
      } else if (!bool && this.m_CustomMethods.read().contains(determineClass(paramElement.getAttribute("class")))) {
        method = this.m_CustomMethods.read().get(determineClass(paramElement.getAttribute("class")));
      } else {
        method = null;
      } 
      if (method != null) {
        Class[] arrayOfClass = new Class[1];
        arrayOfClass[0] = Element.class;
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = paramElement;
        return method.invoke(this, arrayOfObject);
      } 
      return readFromXML(paramElement);
    } catch (Exception exception) {
      System.out.println("PROBLEM (read): " + paramElement.getAttribute("name"));
      throw (Exception)exception.fillInStackTrace();
    } 
  }
  
  protected Object readPostProcess(Object paramObject) throws Exception {
    return paramObject;
  }
  
  public Object fromXML(Document paramDocument) throws Exception {
    if (!paramDocument.getDocumentElement().getNodeName().equals("object"))
      throw new Exception("Expected 'object' as root element, but found '" + paramDocument.getDocumentElement().getNodeName() + "'!"); 
    this.m_Document.setDocument(paramDocument);
    checkVersion();
    return readPostProcess(invokeReadFromXML(this.m_Document.getDocument().getDocumentElement()));
  }
  
  public Object read(String paramString) throws Exception {
    return fromXML(this.m_Document.read(paramString));
  }
  
  public Object read(File paramFile) throws Exception {
    return fromXML(this.m_Document.read(paramFile));
  }
  
  public Object read(InputStream paramInputStream) throws Exception {
    return fromXML(this.m_Document.read(paramInputStream));
  }
  
  public Object read(Reader paramReader) throws Exception {
    return fromXML(this.m_Document.read(paramReader));
  }
  
  public void write(String paramString, Object paramObject) throws Exception {
    toXML(paramObject).write(paramString);
  }
  
  public void write(File paramFile, Object paramObject) throws Exception {
    toXML(paramObject).write(paramFile);
  }
  
  public void write(OutputStream paramOutputStream, Object paramObject) throws Exception {
    toXML(paramObject).write(paramOutputStream);
  }
  
  public void write(Writer paramWriter, Object paramObject) throws Exception {
    toXML(paramObject).write(paramWriter);
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    if (paramArrayOfString.length > 0)
      if (paramArrayOfString[0].toLowerCase().endsWith(".xml")) {
        System.out.println((new XMLSerialization()).read(paramArrayOfString[0]).toString());
      } else {
        FileInputStream fileInputStream = new FileInputStream(paramArrayOfString[0]);
        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        (new XMLSerialization()).write(new BufferedOutputStream(new FileOutputStream(paramArrayOfString[0] + ".xml")), object);
        FileOutputStream fileOutputStream = new FileOutputStream(paramArrayOfString[0] + ".exp");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(fileOutputStream));
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
      }  
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\xml\XMLSerialization.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */