package weka.experiment.xml;

import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import org.w3c.dom.Element;
import weka.classifiers.Classifier;
import weka.core.xml.XMLBasicSerialization;
import weka.core.xml.XMLDocument;
import weka.core.xml.XMLSerializationMethodHandler;
import weka.experiment.Experiment;
import weka.experiment.PropertyNode;
import weka.experiment.ResultProducer;
import weka.experiment.SplitEvaluator;

public class XMLExperiment extends XMLBasicSerialization {
  public static final String NAME_CLASSFIRST = "classFirst";
  
  public static final String NAME_PROPERTYNODE_VALUE = "value";
  
  public static final String NAME_PROPERTYNODE_PARENTCLASS = "parentClass";
  
  public static final String NAME_PROPERTYNODE_PROPERTY = "property";
  
  public void clear() throws Exception {
    super.clear();
    this.m_Properties.addIgnored("__root__.options");
    this.m_Properties.addIgnored(Experiment.class, "options");
    this.m_Properties.addAllowed(Classifier.class, "debug");
    this.m_Properties.addAllowed(Classifier.class, "options");
    this.m_Properties.addAllowed(SplitEvaluator.class, "options");
    this.m_Properties.addAllowed(ResultProducer.class, "options");
    this.m_CustomMethods.read().add(PropertyNode.class, XMLSerializationMethodHandler.findReadMethod(this, "readPropertyNode"));
    this.m_CustomMethods.write().add(PropertyNode.class, XMLSerializationMethodHandler.findWriteMethod(this, "writePropertyNode"));
  }
  
  protected void writePostProcess(Object paramObject) throws Exception {
    Experiment experiment = (Experiment)paramObject;
    Element element = addElement(this.m_Document.getDocument().getDocumentElement(), "classFirst", Boolean.class.getName(), false, false);
    element.appendChild(element.getOwnerDocument().createTextNode((new Boolean(false)).toString()));
  }
  
  protected Object readPostProcess(Object paramObject) throws Exception {
    Experiment experiment = (Experiment)paramObject;
    Vector vector = XMLDocument.getChildTags(this.m_Document.getDocument().getDocumentElement());
    for (byte b = 0; b < vector.size(); b++) {
      Element element = vector.get(b);
      if (element.getAttribute("name").equals("classFirst")) {
        experiment.classFirst((new Boolean(XMLDocument.getContent(element))).booleanValue());
        break;
      } 
    } 
    return paramObject;
  }
  
  public void writePropertyNode(Element paramElement, Object paramObject, String paramString) throws Exception {
    PropertyNode propertyNode = (PropertyNode)paramObject;
    Element element = (Element)paramElement.appendChild(this.m_Document.getDocument().createElement("object"));
    element.setAttribute("name", paramString);
    element.setAttribute("class", propertyNode.getClass().getName());
    element.setAttribute("primitive", "no");
    element.setAttribute("array", "no");
    if (propertyNode.value != null)
      invokeWriteToXML(element, propertyNode.value, "value"); 
    if (propertyNode.parentClass != null)
      invokeWriteToXML(element, propertyNode.parentClass.getName(), "parentClass"); 
    if (propertyNode.property != null)
      invokeWriteToXML(element, propertyNode.property.getDisplayName(), "property"); 
    if (propertyNode.value != null && propertyNode.property != null && propertyNode.property.getPropertyType().isPrimitive()) {
      Vector vector = XMLDocument.getChildTags(element);
      for (byte b = 0; b < vector.size(); b++) {
        Element element1 = vector.get(b);
        if (element1.getAttribute("name").equals("value")) {
          element1.setAttribute("class", propertyNode.property.getPropertyType().getName());
          element1.setAttribute("primitive", "yes");
        } 
      } 
    } 
  }
  
  public Object readPropertyNode(Element paramElement) throws Exception {
    Class clazz;
    PropertyNode propertyNode = null;
    Vector vector = XMLDocument.getChildTags(paramElement);
    Object object = null;
    String str1 = null;
    String str2 = null;
    for (byte b = 0; b < vector.size(); b++) {
      Element element = vector.get(b);
      if (element.getAttribute("name").equals("value"))
        if (stringToBoolean(element.getAttribute("primitive"))) {
          object = getPrimitive(element);
        } else {
          object = invokeReadFromXML(element);
        }  
      if (element.getAttribute("name").equals("parentClass"))
        str1 = XMLDocument.getContent(element); 
      if (element.getAttribute("name").equals("property"))
        str2 = XMLDocument.getContent(element); 
    } 
    if (str1 != null) {
      clazz = Class.forName(str1);
    } else {
      clazz = null;
    } 
    if (clazz != null) {
      propertyNode = new PropertyNode(object, new PropertyDescriptor(str2, clazz), clazz);
    } else {
      propertyNode = new PropertyNode(object);
    } 
    return propertyNode;
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    if (paramArrayOfString.length > 0)
      if (paramArrayOfString[0].toLowerCase().endsWith(".xml")) {
        System.out.println((new XMLExperiment()).read(paramArrayOfString[0]).toString());
      } else {
        FileInputStream fileInputStream = new FileInputStream(paramArrayOfString[0]);
        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        (new XMLExperiment()).write(new BufferedOutputStream(new FileOutputStream(paramArrayOfString[0] + ".xml")), object);
        FileOutputStream fileOutputStream = new FileOutputStream(paramArrayOfString[0] + ".exp");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(fileOutputStream));
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
      }  
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\xml\XMLExperiment.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */