package weka.core.xml;

import java.util.Vector;
import javax.swing.DefaultListModel;
import org.w3c.dom.Element;

public class XMLBasicSerialization extends XMLSerialization {
  public void clear() throws Exception {
    super.clear();
    this.m_CustomMethods.read().add(DefaultListModel.class, XMLSerializationMethodHandler.findReadMethod(this, "readDefaultListModel"));
    this.m_CustomMethods.write().add(DefaultListModel.class, XMLSerializationMethodHandler.findWriteMethod(this, "writeDefaultListModel"));
  }
  
  public void writeDefaultListModel(Element paramElement, Object paramObject, String paramString) throws Exception {
    DefaultListModel defaultListModel = (DefaultListModel)paramObject;
    Element element = (Element)paramElement.appendChild(this.m_Document.getDocument().createElement("object"));
    element.setAttribute("name", paramString);
    element.setAttribute("class", paramObject.getClass().getName());
    element.setAttribute("primitive", "no");
    element.setAttribute("array", "no");
    for (byte b = 0; b < defaultListModel.getSize(); b++)
      invokeWriteToXML(element, defaultListModel.get(b), Integer.toString(b)); 
  }
  
  public Object readDefaultListModel(Element paramElement) throws Exception {
    Object object = null;
    Vector vector = XMLDocument.getChildTags(paramElement);
    DefaultListModel defaultListModel = new DefaultListModel();
    defaultListModel.setSize(vector.size());
    for (byte b = 0; b < vector.size(); b++) {
      Element element = vector.get(b);
      defaultListModel.set(Integer.parseInt(element.getAttribute("name")), invokeReadFromXML(element));
    } 
    return defaultListModel;
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {}
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\xml\XMLBasicSerialization.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */