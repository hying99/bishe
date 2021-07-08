package weka.gui.graphvisualizer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import weka.core.FastVector;

public class BIFParser implements GraphConstants {
  protected FastVector m_nodes;
  
  protected FastVector m_edges;
  
  protected String graphName;
  
  protected String inString;
  
  protected InputStream inStream;
  
  public BIFParser(String paramString, FastVector paramFastVector1, FastVector paramFastVector2) {
    this.m_nodes = paramFastVector1;
    this.m_edges = paramFastVector2;
    this.inString = paramString;
  }
  
  public BIFParser(InputStream paramInputStream, FastVector paramFastVector1, FastVector paramFastVector2) {
    this.m_nodes = paramFastVector1;
    this.m_edges = paramFastVector2;
    this.inStream = paramInputStream;
  }
  
  public String parse() throws Exception {
    Document document = null;
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setIgnoringElementContentWhitespace(true);
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    if (this.inStream != null) {
      document = documentBuilder.parse(this.inStream);
    } else if (this.inString != null) {
      document = documentBuilder.parse(new InputSource(new StringReader(this.inString)));
    } else {
      throw new Exception("No input given");
    } 
    NodeList nodeList1 = document.getElementsByTagName("NETWORK");
    if (nodeList1.getLength() == 0)
      throw new BIFFormatException("NETWORK tag not found"); 
    NodeList nodeList2 = ((Element)nodeList1.item(0)).getElementsByTagName("NAME");
    this.graphName = nodeList2.item(0).getFirstChild().getNodeValue();
    nodeList1 = document.getElementsByTagName("VARIABLE");
    byte b1;
    for (b1 = 0; b1 < nodeList1.getLength(); b1++) {
      nodeList2 = ((Element)nodeList1.item(b1)).getElementsByTagName("NAME");
      if (nodeList2.getLength() > 1)
        throw new BIFFormatException("More than one name tags found for variable no. " + (b1 + 1)); 
      String str = nodeList2.item(0).getFirstChild().getNodeValue();
      GraphNode graphNode = new GraphNode(str, str, 3);
      this.m_nodes.addElement(graphNode);
      nodeList2 = ((Element)nodeList1.item(b1)).getElementsByTagName("PROPERTY");
      byte b;
      for (b = 0; b < nodeList2.getLength(); b++) {
        if (nodeList2.item(b).getFirstChild().getNodeValue().startsWith("position")) {
          String str1 = nodeList2.item(b).getFirstChild().getNodeValue();
          graphNode.x = Integer.parseInt(str1.substring(str1.indexOf('(') + 1, str1.indexOf(',')).trim());
          graphNode.y = Integer.parseInt(str1.substring(str1.indexOf(',') + 1, str1.indexOf(')')).trim());
          break;
        } 
      } 
      nodeList2 = ((Element)nodeList1.item(b1)).getElementsByTagName("OUTCOME");
      graphNode.outcomes = new String[nodeList2.getLength()];
      for (b = 0; b < nodeList2.getLength(); b++)
        graphNode.outcomes[b] = nodeList2.item(b).getFirstChild().getNodeValue(); 
    } 
    nodeList1 = document.getElementsByTagName("DEFINITION");
    for (b1 = 0; b1 < nodeList1.getLength(); b1++) {
      nodeList2 = ((Element)nodeList1.item(b1)).getElementsByTagName("FOR");
      String str1 = nodeList2.item(0).getFirstChild().getNodeValue();
      GraphNode graphNode = (GraphNode)this.m_nodes.elementAt(0);
      int i;
      for (i = 1; i < this.m_nodes.size() && !graphNode.ID.equals(str1); i++)
        graphNode = (GraphNode)this.m_nodes.elementAt(i); 
      nodeList2 = ((Element)nodeList1.item(b1)).getElementsByTagName("GIVEN");
      i = 1;
      for (byte b3 = 0; b3 < nodeList2.getLength(); b3++) {
        str1 = nodeList2.item(b3).getFirstChild().getNodeValue();
        GraphNode graphNode1 = (GraphNode)this.m_nodes.elementAt(0);
        for (byte b = 1; b < this.m_nodes.size() && !graphNode1.ID.equals(str1); b++)
          graphNode1 = (GraphNode)this.m_nodes.elementAt(b); 
        this.m_edges.addElement(new GraphEdge(this.m_nodes.indexOf(graphNode1), this.m_nodes.indexOf(graphNode), 1));
        i *= graphNode1.outcomes.length;
      } 
      nodeList2 = ((Element)nodeList1.item(b1)).getElementsByTagName("TABLE");
      if (nodeList2.getLength() > 1)
        throw new BIFFormatException("More than one Probability Table for " + graphNode.ID); 
      String str2 = nodeList2.item(0).getFirstChild().getNodeValue();
      StringTokenizer stringTokenizer = new StringTokenizer(str2, " \n\t");
      if (i * graphNode.outcomes.length > stringTokenizer.countTokens())
        throw new BIFFormatException("Probability Table for " + graphNode.ID + " contains more values than it should"); 
      if (i * graphNode.outcomes.length < stringTokenizer.countTokens())
        throw new BIFFormatException("Probability Table for " + graphNode.ID + " contains less values than it should"); 
      graphNode.probs = new double[i][graphNode.outcomes.length];
      for (byte b4 = 0; b4 < i; b4++) {
        for (byte b = 0; b < graphNode.outcomes.length; b++) {
          try {
            graphNode.probs[b4][b] = Double.parseDouble(stringTokenizer.nextToken());
          } catch (NumberFormatException numberFormatException) {
            throw numberFormatException;
          } 
        } 
      } 
    } 
    int[] arrayOfInt1 = new int[this.m_nodes.size()];
    int[] arrayOfInt2 = new int[this.m_nodes.size()];
    byte b2;
    for (b2 = 0; b2 < this.m_edges.size(); b2++) {
      GraphEdge graphEdge = (GraphEdge)this.m_edges.elementAt(b2);
      arrayOfInt1[graphEdge.src] = arrayOfInt1[graphEdge.src] + 1;
      arrayOfInt2[graphEdge.dest] = arrayOfInt2[graphEdge.dest] + 1;
    } 
    for (b2 = 0; b2 < this.m_edges.size(); b2++) {
      GraphEdge graphEdge = (GraphEdge)this.m_edges.elementAt(b2);
      GraphNode graphNode1 = (GraphNode)this.m_nodes.elementAt(graphEdge.src);
      GraphNode graphNode2 = (GraphNode)this.m_nodes.elementAt(graphEdge.dest);
      if (graphNode1.edges == null) {
        graphNode1.edges = new int[arrayOfInt1[graphEdge.src]][2];
        for (byte b3 = 0; b3 < graphNode1.edges.length; b3++)
          graphNode1.edges[b3][0] = -1; 
      } 
      if (graphNode2.prnts == null) {
        graphNode2.prnts = new int[arrayOfInt2[graphEdge.dest]];
        for (byte b3 = 0; b3 < graphNode2.prnts.length; b3++)
          graphNode2.prnts[b3] = -1; 
      } 
      byte b;
      for (b = 0; graphNode1.edges[b][0] != -1; b++);
      graphNode1.edges[b][0] = graphEdge.dest;
      graphNode1.edges[b][1] = graphEdge.type;
      for (b = 0; graphNode2.prnts[b] != -1; b++);
      graphNode2.prnts[b] = graphEdge.src;
    } 
    return this.graphName;
  }
  
  public static void writeXMLBIF03(String paramString1, String paramString2, FastVector paramFastVector1, FastVector paramFastVector2) {
    try {
      FileWriter fileWriter = new FileWriter(paramString1);
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("<?xml version=\"1.0\"?>\n");
      stringBuffer.append("<!-- DTD for the XMLBIF 0.3 format -->\n");
      stringBuffer.append("<!DOCTYPE BIF [\n");
      stringBuffer.append("\t<!ELEMENT BIF ( NETWORK )*>\n");
      stringBuffer.append("\t      <!ATTLIST BIF VERSION CDATA #REQUIRED>\n");
      stringBuffer.append("\t<!ELEMENT NETWORK ( NAME, ( PROPERTY | VARIABLE | DEFINITION )* )>\n");
      stringBuffer.append("\t<!ELEMENT NAME (#PCDATA)>\n");
      stringBuffer.append("\t<!ELEMENT VARIABLE ( NAME, ( OUTCOME |  PROPERTY )* ) >\n");
      stringBuffer.append("\t      <!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">\n");
      stringBuffer.append("\t<!ELEMENT OUTCOME (#PCDATA)>\n");
      stringBuffer.append("\t<!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >\n");
      stringBuffer.append("\t<!ELEMENT FOR (#PCDATA)>\n");
      stringBuffer.append("\t<!ELEMENT GIVEN (#PCDATA)>\n");
      stringBuffer.append("\t<!ELEMENT TABLE (#PCDATA)>\n");
      stringBuffer.append("\t<!ELEMENT PROPERTY (#PCDATA)>\n");
      stringBuffer.append("]>\n");
      stringBuffer.append("\n");
      stringBuffer.append("\n");
      stringBuffer.append("<BIF VERSION=\"0.3\">\n");
      stringBuffer.append("<NETWORK>\n");
      stringBuffer.append("<NAME>" + XMLNormalize(paramString2) + "</NAME>\n");
      byte b;
      for (b = 0; b < paramFastVector1.size(); b++) {
        GraphNode graphNode = (GraphNode)paramFastVector1.elementAt(b);
        if (graphNode.nodeType == 3) {
          stringBuffer.append("<VARIABLE TYPE=\"nature\">\n");
          stringBuffer.append("\t<NAME>" + XMLNormalize(graphNode.ID) + "</NAME>\n");
          if (graphNode.outcomes != null) {
            for (byte b1 = 0; b1 < graphNode.outcomes.length; b1++)
              stringBuffer.append("\t<OUTCOME>" + XMLNormalize(graphNode.outcomes[b1]) + "</OUTCOME>\n"); 
          } else {
            stringBuffer.append("\t<OUTCOME>true</OUTCOME>\n");
          } 
          stringBuffer.append("\t<PROPERTY>position = (" + graphNode.x + "," + graphNode.y + ")</PROPERTY>\n");
          stringBuffer.append("</VARIABLE>\n");
        } 
      } 
      for (b = 0; b < paramFastVector1.size(); b++) {
        GraphNode graphNode = (GraphNode)paramFastVector1.elementAt(b);
        if (graphNode.nodeType == 3) {
          stringBuffer.append("<DEFINITION>\n");
          stringBuffer.append("<FOR>" + XMLNormalize(graphNode.ID) + "</FOR>\n");
          int i = 1;
          if (graphNode.prnts != null)
            for (byte b2 = 0; b2 < graphNode.prnts.length; b2++) {
              GraphNode graphNode1 = (GraphNode)paramFastVector1.elementAt(graphNode.prnts[b2]);
              stringBuffer.append("\t<GIVEN>" + XMLNormalize(graphNode1.ID) + "</GIVEN>\n");
              if (graphNode1.outcomes != null)
                i *= graphNode1.outcomes.length; 
            }  
          stringBuffer.append("<TABLE>\n");
          for (byte b1 = 0; b1 < i; b1++) {
            if (graphNode.outcomes != null) {
              for (byte b2 = 0; b2 < graphNode.outcomes.length; b2++)
                stringBuffer.append(graphNode.probs[b1][b2] + " "); 
            } else {
              stringBuffer.append("1");
            } 
            stringBuffer.append('\n');
          } 
          stringBuffer.append("</TABLE>\n");
          stringBuffer.append("</DEFINITION>\n");
        } 
      } 
      stringBuffer.append("</NETWORK>\n");
      stringBuffer.append("</BIF>\n");
      fileWriter.write(stringBuffer.toString());
      fileWriter.close();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  private static String XMLNormalize(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      switch (c) {
        case '&':
          stringBuffer.append("&amp;");
          break;
        case '\'':
          stringBuffer.append("&apos;");
          break;
        case '"':
          stringBuffer.append("&quot;");
          break;
        case '<':
          stringBuffer.append("&lt;");
          break;
        case '>':
          stringBuffer.append("&gt;");
          break;
        default:
          stringBuffer.append(c);
          break;
      } 
    } 
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\graphvisualizer\BIFParser.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */