package weka.core.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLOptions {
  public static final String TAG_OPTION = "option";
  
  public static final String TAG_OPTIONS = "options";
  
  public static final String ATT_NAME = "name";
  
  public static final String ATT_TYPE = "type";
  
  public static final String ATT_VALUE = "value";
  
  public static final String VAL_TYPE_FLAG = "flag";
  
  public static final String VAL_TYPE_SINGLE = "single";
  
  public static final String VAL_TYPE_HYPHENS = "hyphens";
  
  public static final String VAL_TYPE_QUOTES = "quotes";
  
  public static final String VAL_TYPE_CLASSIFIER = "classifier";
  
  public static final String ROOT_NODE = "options";
  
  public static final String DOCTYPE = "<!DOCTYPE options\n[\n   <!ELEMENT options (option)*>\n   <!ATTLIST options type CDATA \"classifier\">\n   <!ATTLIST options value CDATA \"\">\n   <!ELEMENT option (#PCDATA | options)*>\n   <!ATTLIST option name CDATA #REQUIRED>\n   <!ATTLIST option type (flag | single | hyphens | quotes) \"single\">\n]\n>";
  
  protected XMLDocument m_XMLDocument = null;
  
  public XMLOptions() throws Exception {
    this.m_XMLDocument = new XMLDocument();
    this.m_XMLDocument.setRootNode("options");
    this.m_XMLDocument.setDocType("<!DOCTYPE options\n[\n   <!ELEMENT options (option)*>\n   <!ATTLIST options type CDATA \"classifier\">\n   <!ATTLIST options value CDATA \"\">\n   <!ELEMENT option (#PCDATA | options)*>\n   <!ATTLIST option name CDATA #REQUIRED>\n   <!ATTLIST option type (flag | single | hyphens | quotes) \"single\">\n]\n>");
    setValidating(true);
  }
  
  public XMLOptions(String paramString) throws Exception {
    this();
    getXMLDocument().read(paramString);
  }
  
  public XMLOptions(File paramFile) throws Exception {
    this();
    getXMLDocument().read(paramFile);
  }
  
  public XMLOptions(InputStream paramInputStream) throws Exception {
    this();
    getXMLDocument().read(paramInputStream);
  }
  
  public XMLOptions(Reader paramReader) throws Exception {
    this();
    getXMLDocument().read(paramReader);
  }
  
  public boolean getValidating() {
    return this.m_XMLDocument.getValidating();
  }
  
  public void setValidating(boolean paramBoolean) throws Exception {
    this.m_XMLDocument.setValidating(paramBoolean);
  }
  
  public Document getDocument() {
    return fixHyphens(this.m_XMLDocument.getDocument());
  }
  
  public XMLDocument getXMLDocument() {
    return this.m_XMLDocument;
  }
  
  protected Document fixHyphens(Document paramDocument) {
    NodeList nodeList = paramDocument.getDocumentElement().getElementsByTagName("option");
    Vector vector = new Vector();
    byte b;
    for (b = 0; b < nodeList.getLength(); b++) {
      if (((Element)nodeList.item(b)).getAttribute("type").equals("hyphens"))
        vector.add(nodeList.item(b)); 
    } 
    for (b = 0; b < vector.size(); b++) {
      Node node1 = vector.get(b);
      boolean bool = true;
      Node node2;
      for (node2 = node1; node2.getNextSibling() != null; node2 = node2.getNextSibling()) {
        if (node2.getNextSibling().getNodeType() == 1) {
          bool = false;
          break;
        } 
      } 
      if (!bool) {
        node2 = node1.getParentNode();
        node2.removeChild(node1);
        node2.appendChild(node1);
      } 
    } 
    return paramDocument;
  }
  
  protected int getQuotesLevel(Node paramNode) {
    byte b = 0;
    while (paramNode.getParentNode() != null) {
      if (!(paramNode instanceof Element))
        continue; 
      if (paramNode.getNodeName().equals("option") && ((Element)paramNode).getAttribute("type").equals("quotes"))
        b++; 
      paramNode = paramNode.getParentNode();
    } 
    return b;
  }
  
  protected String toCommandLine(String paramString, Element paramElement, int paramInt) {
    String str = "";
    if (paramElement.getNodeName().equals("options")) {
      if (paramElement.getAttribute("type").equals("classifier"))
        str = str + paramElement.getAttribute("value"); 
      Vector vector = XMLDocument.getChildTags(paramElement);
      for (byte b = 0; b < vector.size(); b++)
        str = toCommandLine(str, vector.get(b), paramInt + 1); 
    } else if (paramElement.getNodeName().equals("option")) {
      str = str + " -" + paramElement.getAttribute("name");
      Vector vector = XMLDocument.getChildTags(paramElement);
      NodeList nodeList = paramElement.getChildNodes();
      if (paramElement.getAttribute("type").equals("single")) {
        if (nodeList.getLength() > 0 && !nodeList.item(0).getNodeValue().trim().equals(""))
          str = str + " " + nodeList.item(0).getNodeValue().trim(); 
      } else if (paramElement.getAttribute("type").equals("hyphens")) {
        str = str + " " + ((Element)vector.get(0)).getAttribute("value");
        vector = XMLDocument.getChildTags(vector.get(0));
        String str1 = "";
        for (byte b = 0; b < vector.size(); b++)
          str1 = toCommandLine(str1, vector.get(b), paramInt + 1); 
        str1 = str1.trim();
        if (!str1.equals(""))
          str = str + " -- " + str1; 
      } else if (paramElement.getAttribute("type").equals("quotes")) {
        str = str + " ";
        byte b;
        for (b = 1; b < getQuotesLevel(paramElement); b++)
          str = str + "\\"; 
        str = str + "\"";
        String str1 = "";
        for (b = 0; b < vector.size(); b++)
          str1 = toCommandLine(str1, vector.get(b), paramInt + 1); 
        str = str + str1.trim();
        for (b = 1; b < getQuotesLevel(paramElement); b++)
          str = str + "\\"; 
        str = str + "\"";
      } 
    } 
    paramString = paramString + " " + str.trim();
    return paramString.trim();
  }
  
  public String toCommandLine() throws Exception {
    return toCommandLine(new String(), getDocument().getDocumentElement(), 0);
  }
  
  public String[] toArray() throws Exception {
    String str1 = toCommandLine();
    Vector vector = new Vector();
    boolean bool1 = false;
    boolean bool2 = false;
    String str2 = "";
    for (byte b = 0; b < str1.length(); b++) {
      boolean bool = true;
      switch (str1.charAt(b)) {
        case '\\':
          bool2 = true;
          break;
        case '"':
          if (!bool2) {
            bool1 = !bool1 ? true : false;
            bool = false;
          } 
          bool2 = false;
          break;
        case ' ':
          if (!bool1) {
            vector.add(str2.replaceAll("\\\\\"", "\""));
            bool = false;
            str2 = "";
          } 
          break;
      } 
      if (bool)
        str2 = str2 + "" + str1.charAt(b); 
    } 
    if (!str2.equals(""))
      vector.add(str2); 
    return vector.<String>toArray(new String[1]);
  }
  
  public String toString() {
    return getXMLDocument().toString();
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    if (paramArrayOfString.length > 0) {
      System.out.println("\nXML:\n\n" + (new XMLOptions(paramArrayOfString[0])).toString());
      System.out.println("\nCommandline:\n\n" + (new XMLOptions(paramArrayOfString[0])).toCommandLine());
      System.out.println("\nString array:\n");
      String[] arrayOfString = (new XMLOptions(paramArrayOfString[0])).toArray();
      for (byte b = 0; b < arrayOfString.length; b++)
        System.out.println(arrayOfString[b]); 
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\xml\XMLOptions.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */