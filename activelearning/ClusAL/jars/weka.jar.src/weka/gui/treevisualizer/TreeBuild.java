package weka.gui.treevisualizer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Hashtable;
import java.util.Vector;

public class TreeBuild {
  private String m_graphName;
  
  private Vector m_aNodes;
  
  private Vector m_aEdges;
  
  private Vector m_nodes;
  
  private Vector m_edges;
  
  private InfoObject m_grObj;
  
  private InfoObject m_noObj;
  
  private InfoObject m_edObj;
  
  private boolean m_digraph;
  
  private StreamTokenizer m_st;
  
  private Hashtable m_colorTable = new Hashtable();
  
  public TreeBuild() {
    Colors colors = new Colors();
    for (byte b = 0; b < colors.m_cols.length; b++)
      this.m_colorTable.put((colors.m_cols[b]).m_name, (colors.m_cols[b]).m_col); 
  }
  
  public Node create(Reader paramReader) {
    this.m_nodes = new Vector(50, 50);
    this.m_edges = new Vector(50, 50);
    this.m_grObj = new InfoObject(this, "graph");
    this.m_noObj = new InfoObject(this, "node");
    this.m_edObj = new InfoObject(this, "edge");
    this.m_digraph = false;
    this.m_st = new StreamTokenizer(new BufferedReader(paramReader));
    setSyntax();
    graph();
    return generateStructures();
  }
  
  private Node generateStructures() {
    this.m_aNodes = new Vector(50, 50);
    this.m_aEdges = new Vector(50, 50);
    byte b1;
    for (b1 = 0; b1 < this.m_nodes.size(); b1++) {
      String str2;
      Integer integer1;
      Integer integer2;
      Color color1;
      Color color2;
      InfoObject infoObject = this.m_nodes.elementAt(b1);
      String str1 = infoObject.m_id;
      if (infoObject.m_label == null) {
        if (this.m_noObj.m_label == null) {
          str2 = "";
        } else {
          str2 = this.m_noObj.m_label;
        } 
      } else {
        str2 = infoObject.m_label;
      } 
      if (infoObject.m_shape == null) {
        if (this.m_noObj.m_shape == null) {
          integer1 = new Integer(2);
        } else {
          integer1 = getShape(this.m_noObj.m_shape);
        } 
      } else {
        integer1 = getShape(infoObject.m_shape);
      } 
      if (integer1 == null)
        integer1 = new Integer(2); 
      if (infoObject.m_style == null) {
        if (this.m_noObj.m_style == null) {
          integer2 = new Integer(1);
        } else {
          integer2 = getStyle(this.m_noObj.m_style);
        } 
      } else {
        integer2 = getStyle(infoObject.m_style);
      } 
      if (integer2 == null)
        integer2 = new Integer(1); 
      if (infoObject.m_fontSize == null) {
        if (this.m_noObj.m_fontSize == null) {
          byte b = 12;
        } else {
          int i = Integer.valueOf(this.m_noObj.m_fontSize).intValue();
        } 
      } else {
        int i = Integer.valueOf(infoObject.m_fontSize).intValue();
      } 
      if (infoObject.m_fontColor == null) {
        if (this.m_noObj.m_fontColor == null) {
          color1 = Color.black;
        } else {
          color1 = (Color)this.m_colorTable.get(this.m_noObj.m_fontColor.toLowerCase());
        } 
      } else {
        color1 = (Color)this.m_colorTable.get(infoObject.m_fontColor.toLowerCase());
      } 
      if (color1 == null)
        color1 = Color.black; 
      if (infoObject.m_color == null) {
        if (this.m_noObj.m_color == null) {
          color2 = Color.gray;
        } else {
          color2 = (Color)this.m_colorTable.get(this.m_noObj.m_color.toLowerCase());
        } 
      } else {
        color2 = (Color)this.m_colorTable.get(infoObject.m_color.toLowerCase());
      } 
      if (color2 == null)
        color2 = Color.gray; 
      this.m_aNodes.addElement(new Node(str2, str1, integer2.intValue(), integer1.intValue(), color2, infoObject.m_data));
    } 
    for (b1 = 0; b1 < this.m_edges.size(); b1++) {
      String str2;
      Integer integer1;
      Integer integer2;
      Color color1;
      Color color2;
      InfoObject infoObject = this.m_edges.elementAt(b1);
      String str1 = infoObject.m_id;
      if (infoObject.m_label == null) {
        if (this.m_noObj.m_label == null) {
          str2 = "";
        } else {
          str2 = this.m_noObj.m_label;
        } 
      } else {
        str2 = infoObject.m_label;
      } 
      if (infoObject.m_shape == null) {
        if (this.m_noObj.m_shape == null) {
          integer1 = new Integer(2);
        } else {
          integer1 = getShape(this.m_noObj.m_shape);
        } 
      } else {
        integer1 = getShape(infoObject.m_shape);
      } 
      if (integer1 == null)
        integer1 = new Integer(2); 
      if (infoObject.m_style == null) {
        if (this.m_noObj.m_style == null) {
          integer2 = new Integer(1);
        } else {
          integer2 = getStyle(this.m_noObj.m_style);
        } 
      } else {
        integer2 = getStyle(infoObject.m_style);
      } 
      if (integer2 == null)
        integer2 = new Integer(1); 
      if (infoObject.m_fontSize == null) {
        if (this.m_noObj.m_fontSize == null) {
          byte b = 12;
        } else {
          int i = Integer.valueOf(this.m_noObj.m_fontSize).intValue();
        } 
      } else {
        int i = Integer.valueOf(infoObject.m_fontSize).intValue();
      } 
      if (infoObject.m_fontColor == null) {
        if (this.m_noObj.m_fontColor == null) {
          color1 = Color.black;
        } else {
          color1 = (Color)this.m_colorTable.get(this.m_noObj.m_fontColor.toLowerCase());
        } 
      } else {
        color1 = (Color)this.m_colorTable.get(infoObject.m_fontColor.toLowerCase());
      } 
      if (color1 == null)
        color1 = Color.black; 
      if (infoObject.m_color == null) {
        if (this.m_noObj.m_color == null) {
          color2 = Color.white;
        } else {
          color2 = (Color)this.m_colorTable.get(this.m_noObj.m_color.toLowerCase());
        } 
      } else {
        color2 = (Color)this.m_colorTable.get(infoObject.m_color.toLowerCase());
      } 
      if (color2 == null)
        color2 = Color.white; 
      this.m_aEdges.addElement(new Edge(str2, infoObject.m_source, infoObject.m_target));
    } 
    Node node1 = null;
    Node node2 = null;
    byte b2;
    for (b2 = 0; b2 < this.m_aEdges.size(); b2++) {
      b1 = 0;
      boolean bool = false;
      Edge edge = this.m_aEdges.elementAt(b2);
      for (byte b = 0; b < this.m_aNodes.size(); b++) {
        Node node = this.m_aNodes.elementAt(b);
        if (node.getRefer().equals(edge.getRtarget())) {
          b1 = 1;
          node2 = node;
        } 
        if (node.getRefer().equals(edge.getRsource())) {
          bool = true;
          node1 = node;
        } 
        if (b1 == 1 && bool == true)
          break; 
      } 
      if (node2 != node1) {
        edge.setTarget(node2);
        edge.setSource(node1);
      } else {
        System.out.println("logic error");
      } 
    } 
    for (b2 = 0; b2 < this.m_aNodes.size(); b2++) {
      if (((Node)this.m_aNodes.elementAt(b2)).getParent(0) == null)
        node1 = this.m_aNodes.elementAt(b2); 
    } 
    return node1;
  }
  
  private Integer getShape(String paramString) {
    return (paramString.equalsIgnoreCase("box") || paramString.equalsIgnoreCase("rectangle")) ? new Integer(1) : (paramString.equalsIgnoreCase("oval") ? new Integer(2) : (paramString.equalsIgnoreCase("diamond") ? new Integer(3) : null));
  }
  
  private Integer getStyle(String paramString) {
    return paramString.equalsIgnoreCase("filled") ? new Integer(1) : null;
  }
  
  private void setSyntax() {
    this.m_st.resetSyntax();
    this.m_st.eolIsSignificant(false);
    this.m_st.slashStarComments(true);
    this.m_st.slashSlashComments(true);
    this.m_st.whitespaceChars(0, 32);
    this.m_st.wordChars(33, 255);
    this.m_st.ordinaryChar(91);
    this.m_st.ordinaryChar(93);
    this.m_st.ordinaryChar(123);
    this.m_st.ordinaryChar(125);
    this.m_st.ordinaryChar(45);
    this.m_st.ordinaryChar(62);
    this.m_st.ordinaryChar(47);
    this.m_st.ordinaryChar(42);
    this.m_st.quoteChar(34);
    this.m_st.whitespaceChars(59, 59);
    this.m_st.ordinaryChar(61);
  }
  
  private void alterSyntax() {
    this.m_st.resetSyntax();
    this.m_st.wordChars(0, 255);
    this.m_st.slashStarComments(false);
    this.m_st.slashSlashComments(false);
    this.m_st.ordinaryChar(10);
    this.m_st.ordinaryChar(13);
  }
  
  private void nextToken(String paramString) {
    int i = 0;
    try {
      i = this.m_st.nextToken();
    } catch (IOException iOException) {}
    if (i == -1) {
      System.out.println("eof , " + paramString);
    } else if (i == -2) {
      System.out.println("got a number , " + paramString);
    } 
  }
  
  private void graph() {
    boolean bool = true;
    nextToken("expected 'digraph'");
    if (this.m_st.sval.equalsIgnoreCase("digraph")) {
      this.m_digraph = true;
    } else {
      System.out.println("expected 'digraph'");
    } 
    nextToken("expected a Graph Name");
    if (this.m_st.sval != null) {
      this.m_graphName = this.m_st.sval;
    } else {
      System.out.println("expected a Graph Name");
    } 
    nextToken("expected '{'");
    if (this.m_st.ttype == 123) {
      stmtList();
    } else {
      System.out.println("expected '{'");
    } 
  }
  
  private void stmtList() {
    boolean bool = true;
    while (bool) {
      nextToken("expects a STMT_LIST item or '}'");
      if (this.m_st.ttype == 125) {
        bool = false;
        continue;
      } 
      if (this.m_st.sval.equalsIgnoreCase("graph") || this.m_st.sval.equalsIgnoreCase("node") || this.m_st.sval.equalsIgnoreCase("edge")) {
        this.m_st.pushBack();
        attrStmt();
        continue;
      } 
      if (this.m_st.sval != null) {
        nodeId(this.m_st.sval, 0);
        continue;
      } 
      System.out.println("expects a STMT_LIST item or '}'");
    } 
  }
  
  private void attrStmt() {
    nextToken("expected 'graph' or 'node' or 'edge'");
    if (this.m_st.sval.equalsIgnoreCase("graph")) {
      nextToken("expected a '['");
      if (this.m_st.ttype == 91) {
        attrList(this.m_grObj);
      } else {
        System.out.println("expected a '['");
      } 
    } else if (this.m_st.sval.equalsIgnoreCase("node")) {
      nextToken("expected a '['");
      if (this.m_st.ttype == 91) {
        attrList(this.m_noObj);
      } else {
        System.out.println("expected a '['");
      } 
    } else if (this.m_st.sval.equalsIgnoreCase("edge")) {
      nextToken("expected a '['");
      if (this.m_st.ttype == 91) {
        attrList(this.m_edObj);
      } else {
        System.out.println("expected a '['");
      } 
    } else {
      System.out.println("expected 'graph' or 'node' or 'edge'");
    } 
  }
  
  private void nodeId(String paramString, int paramInt) {
    nextToken("error occurred in node_id");
    if (this.m_st.ttype == 125) {
      if (paramInt == 0)
        this.m_nodes.addElement(new InfoObject(this, paramString)); 
      this.m_st.pushBack();
    } else if (this.m_st.ttype == 45) {
      nextToken("error occurred checking for an edge");
      if (this.m_st.ttype == 62) {
        edgeStmt(paramString);
      } else {
        System.out.println("error occurred checking for an edge");
      } 
    } else if (this.m_st.ttype == 91) {
      if (paramInt == 0) {
        this.m_nodes.addElement(new InfoObject(this, paramString));
        attrList(this.m_nodes.lastElement());
      } else {
        attrList(this.m_edges.lastElement());
      } 
    } else if (this.m_st.sval != null) {
      if (paramInt == 0)
        this.m_nodes.addElement(new InfoObject(this, paramString)); 
      this.m_st.pushBack();
    } else {
      System.out.println("error occurred in node_id");
    } 
  }
  
  private void edgeStmt(String paramString) {
    nextToken("error getting target of edge");
    if (this.m_st.sval != null) {
      this.m_edges.addElement(new InfoObject(this, "an edge ,no id"));
      ((InfoObject)this.m_edges.lastElement()).m_source = paramString;
      ((InfoObject)this.m_edges.lastElement()).m_target = this.m_st.sval;
      nodeId(this.m_st.sval, 1);
    } else {
      System.out.println("error getting target of edge");
    } 
  }
  
  private void attrList(InfoObject paramInfoObject) {
    boolean bool = true;
    while (bool) {
      nextToken("error in attr_list");
      if (this.m_st.ttype == 93) {
        bool = false;
        continue;
      } 
      if (this.m_st.sval.equalsIgnoreCase("color")) {
        nextToken("error getting color");
        if (this.m_st.ttype == 61) {
          nextToken("error getting color");
          if (this.m_st.sval != null) {
            paramInfoObject.m_color = this.m_st.sval;
            continue;
          } 
          System.out.println("error getting color");
          continue;
        } 
        System.out.println("error getting color");
        continue;
      } 
      if (this.m_st.sval.equalsIgnoreCase("fontcolor")) {
        nextToken("error getting font color");
        if (this.m_st.ttype == 61) {
          nextToken("error getting font color");
          if (this.m_st.sval != null) {
            paramInfoObject.m_fontColor = this.m_st.sval;
            continue;
          } 
          System.out.println("error getting font color");
          continue;
        } 
        System.out.println("error getting font color");
        continue;
      } 
      if (this.m_st.sval.equalsIgnoreCase("fontsize")) {
        nextToken("error getting font size");
        if (this.m_st.ttype == 61) {
          nextToken("error getting font size");
          if (this.m_st.sval != null) {
            paramInfoObject.m_fontSize = this.m_st.sval;
            continue;
          } 
          System.out.println("error getting font size");
          continue;
        } 
        System.out.println("error getting font size");
        continue;
      } 
      if (this.m_st.sval.equalsIgnoreCase("label")) {
        nextToken("error getting label");
        if (this.m_st.ttype == 61) {
          nextToken("error getting label");
          if (this.m_st.sval != null) {
            paramInfoObject.m_label = this.m_st.sval;
            continue;
          } 
          System.out.println("error getting label");
          continue;
        } 
        System.out.println("error getting label");
        continue;
      } 
      if (this.m_st.sval.equalsIgnoreCase("shape")) {
        nextToken("error getting shape");
        if (this.m_st.ttype == 61) {
          nextToken("error getting shape");
          if (this.m_st.sval != null) {
            paramInfoObject.m_shape = this.m_st.sval;
            continue;
          } 
          System.out.println("error getting shape");
          continue;
        } 
        System.out.println("error getting shape");
        continue;
      } 
      if (this.m_st.sval.equalsIgnoreCase("style")) {
        nextToken("error getting style");
        if (this.m_st.ttype == 61) {
          nextToken("error getting style");
          if (this.m_st.sval != null) {
            paramInfoObject.m_style = this.m_st.sval;
            continue;
          } 
          System.out.println("error getting style");
          continue;
        } 
        System.out.println("error getting style");
        continue;
      } 
      if (this.m_st.sval.equalsIgnoreCase("data")) {
        nextToken("error getting data");
        if (this.m_st.ttype == 61) {
          alterSyntax();
          paramInfoObject.m_data = new String("");
          while (true) {
            nextToken("error getting data");
            if (this.m_st.sval != null && paramInfoObject.m_data != null && this.m_st.sval.equals(",")) {
              setSyntax();
              continue;
            } 
            if (this.m_st.sval != null) {
              paramInfoObject.m_data = paramInfoObject.m_data.concat(this.m_st.sval);
              continue;
            } 
            if (this.m_st.ttype == 13) {
              paramInfoObject.m_data = paramInfoObject.m_data.concat("\r");
              continue;
            } 
            if (this.m_st.ttype == 10) {
              paramInfoObject.m_data = paramInfoObject.m_data.concat("\n");
              continue;
            } 
            System.out.println("error getting data");
          } 
        } 
        System.out.println("error getting data");
      } 
    } 
  }
  
  private class InfoObject {
    public String m_id;
    
    public String m_color;
    
    public String m_fontColor;
    
    public String m_fontSize;
    
    public String m_label;
    
    public String m_shape;
    
    public String m_style;
    
    public String m_source;
    
    public String m_target;
    
    public String m_data;
    
    private final TreeBuild this$0;
    
    public InfoObject(TreeBuild this$0, String param1String) {
      this.this$0 = this$0;
      this.m_id = param1String;
      this.m_color = null;
      this.m_fontColor = null;
      this.m_fontSize = null;
      this.m_label = null;
      this.m_shape = null;
      this.m_style = null;
      this.m_source = null;
      this.m_target = null;
      this.m_data = null;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\treevisualizer\TreeBuild.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */