package weka.core.xml;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLDocument {
  public static final String PI = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
  
  protected DocumentBuilderFactory m_Factory = null;
  
  protected DocumentBuilder m_Builder = null;
  
  protected boolean m_Validating = false;
  
  protected Document m_Document = null;
  
  protected String m_DocType = null;
  
  protected String m_RootNode = null;
  
  public XMLDocument() throws Exception {
    this.m_Factory = DocumentBuilderFactory.newInstance();
    setDocType(null);
    setRootNode(null);
    setValidating(false);
  }
  
  public XMLDocument(String paramString) throws Exception {
    this();
    read(paramString);
  }
  
  public XMLDocument(File paramFile) throws Exception {
    this();
    read(paramFile);
  }
  
  public XMLDocument(InputStream paramInputStream) throws Exception {
    this();
    read(paramInputStream);
  }
  
  public XMLDocument(Reader paramReader) throws Exception {
    this();
    read(paramReader);
  }
  
  public DocumentBuilderFactory getFactory() {
    return this.m_Factory;
  }
  
  public DocumentBuilder getBuilder() {
    return this.m_Builder;
  }
  
  public boolean getValidating() {
    return this.m_Validating;
  }
  
  public void setValidating(boolean paramBoolean) throws Exception {
    this.m_Validating = paramBoolean;
    this.m_Factory.setValidating(paramBoolean);
    this.m_Builder = this.m_Factory.newDocumentBuilder();
    clear();
  }
  
  public Document getDocument() {
    return this.m_Document;
  }
  
  public void setDocument(Document paramDocument) {
    this.m_Document = paramDocument;
  }
  
  public void setDocType(String paramString) {
    this.m_DocType = paramString;
  }
  
  public String getDocType() {
    return this.m_DocType;
  }
  
  public void setRootNode(String paramString) {
    if (paramString == null) {
      this.m_RootNode = "root";
    } else {
      this.m_RootNode = paramString;
    } 
  }
  
  public String getRootNode() {
    return this.m_RootNode;
  }
  
  public void clear() {
    newDocument(getDocType(), getRootNode());
  }
  
  public Document newDocument(String paramString1, String paramString2) {
    this.m_Document = getBuilder().newDocument();
    this.m_Document.appendChild(this.m_Document.createElement(paramString2));
    setDocType(paramString1);
    return getDocument();
  }
  
  public Document read(String paramString) throws Exception {
    return (paramString.toLowerCase().indexOf("<?xml") > -1) ? read(new ByteArrayInputStream(paramString.getBytes())) : read(new File(paramString));
  }
  
  public Document read(File paramFile) throws Exception {
    this.m_Document = getBuilder().parse(paramFile);
    return getDocument();
  }
  
  public Document read(InputStream paramInputStream) throws Exception {
    this.m_Document = getBuilder().parse(paramInputStream);
    return getDocument();
  }
  
  public Document read(Reader paramReader) throws Exception {
    this.m_Document = getBuilder().parse(new InputSource(paramReader));
    return getDocument();
  }
  
  public void write(String paramString) throws Exception {
    write(new File(paramString));
  }
  
  public void write(File paramFile) throws Exception {
    write(new BufferedWriter(new FileWriter(paramFile)));
  }
  
  public void write(OutputStream paramOutputStream) throws Exception {
    String str = toString();
    paramOutputStream.write(str.getBytes(), 0, str.length());
    paramOutputStream.flush();
  }
  
  public void write(Writer paramWriter) throws Exception {
    paramWriter.write(toString());
    paramWriter.flush();
  }
  
  public static Vector getChildTags(Node paramNode) {
    Vector vector = new Vector();
    NodeList nodeList = paramNode.getChildNodes();
    for (byte b = 0; b < nodeList.getLength(); b++) {
      if (nodeList.item(b) instanceof Element)
        vector.add(nodeList.item(b)); 
    } 
    return vector;
  }
  
  public static String getContent(Element paramElement) {
    String str = "";
    NodeList nodeList = paramElement.getChildNodes();
    for (byte b = 0; b < nodeList.getLength(); b++) {
      Node node = nodeList.item(b);
      if (node.getNodeType() == 3)
        str = str + node.getNodeValue(); 
    } 
    return str.trim();
  }
  
  protected StringBuffer toString(StringBuffer paramStringBuffer, Node paramNode, int paramInt) {
    String str = "";
    for (byte b = 0; b < paramInt; b++)
      str = str + "   "; 
    if (paramNode.getNodeType() == 3) {
      if (!paramNode.getNodeValue().trim().equals(""))
        paramStringBuffer.append(str + paramNode.getNodeValue().trim() + "\n"); 
    } else if (paramNode.getNodeType() == 8) {
      paramStringBuffer.append(str + "<!--" + paramNode.getNodeValue() + "-->\n");
    } else {
      paramStringBuffer.append(str + "<" + paramNode.getNodeName());
      if (paramNode.hasAttributes()) {
        NamedNodeMap namedNodeMap = paramNode.getAttributes();
        for (byte b1 = 0; b1 < namedNodeMap.getLength(); b1++) {
          Node node = namedNodeMap.item(b1);
          paramStringBuffer.append(" " + node.getNodeName() + "=\"" + node.getNodeValue() + "\"");
        } 
      } 
      if (paramNode.hasChildNodes()) {
        NodeList nodeList = paramNode.getChildNodes();
        if (nodeList.getLength() == 1 && nodeList.item(0).getNodeType() == 3) {
          paramStringBuffer.append(">");
          paramStringBuffer.append(nodeList.item(0).getNodeValue().trim());
          paramStringBuffer.append("</" + paramNode.getNodeName() + ">\n");
        } else {
          paramStringBuffer.append(">\n");
          for (byte b1 = 0; b1 < nodeList.getLength(); b1++) {
            Node node = nodeList.item(b1);
            toString(paramStringBuffer, node, paramInt + 1);
          } 
          paramStringBuffer.append(str + "</" + paramNode.getNodeName() + ">\n");
        } 
      } else {
        paramStringBuffer.append("/>\n");
      } 
    } 
    return paramStringBuffer;
  }
  
  public void print() {
    System.out.println(toString());
  }
  
  public String toString() {
    String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\n";
    if (getDocType() != null)
      str = str + getDocType() + "\n\n"; 
    return toString(new StringBuffer(str), getDocument().getDocumentElement(), 0).toString();
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    if (paramArrayOfString.length > 0) {
      XMLDocument xMLDocument = new XMLDocument();
      xMLDocument.read(paramArrayOfString[0]);
      xMLDocument.print();
      if (paramArrayOfString.length > 1)
        xMLDocument.write(paramArrayOfString[1]); 
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\xml\XMLDocument.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */