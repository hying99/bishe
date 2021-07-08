package weka.gui.graphvisualizer;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import weka.core.FastVector;

public class DotParser implements GraphConstants {
  protected FastVector m_nodes;
  
  protected FastVector m_edges;
  
  protected Reader m_input;
  
  protected String m_graphName;
  
  public DotParser(Reader paramReader, FastVector paramFastVector1, FastVector paramFastVector2) {
    this.m_nodes = paramFastVector1;
    this.m_edges = paramFastVector2;
    this.m_input = paramReader;
  }
  
  public String parse() {
    StreamTokenizer streamTokenizer = new StreamTokenizer(new BufferedReader(this.m_input));
    setSyntax(streamTokenizer);
    graph(streamTokenizer);
    return this.m_graphName;
  }
  
  protected void setSyntax(StreamTokenizer paramStreamTokenizer) {
    paramStreamTokenizer.resetSyntax();
    paramStreamTokenizer.eolIsSignificant(false);
    paramStreamTokenizer.slashStarComments(true);
    paramStreamTokenizer.slashSlashComments(true);
    paramStreamTokenizer.whitespaceChars(0, 32);
    paramStreamTokenizer.wordChars(33, 255);
    paramStreamTokenizer.ordinaryChar(91);
    paramStreamTokenizer.ordinaryChar(93);
    paramStreamTokenizer.ordinaryChar(123);
    paramStreamTokenizer.ordinaryChar(125);
    paramStreamTokenizer.ordinaryChar(45);
    paramStreamTokenizer.ordinaryChar(62);
    paramStreamTokenizer.ordinaryChar(47);
    paramStreamTokenizer.ordinaryChar(42);
    paramStreamTokenizer.quoteChar(34);
    paramStreamTokenizer.whitespaceChars(59, 59);
    paramStreamTokenizer.ordinaryChar(61);
  }
  
  protected void graph(StreamTokenizer paramStreamTokenizer) {
    try {
      paramStreamTokenizer.nextToken();
      if (paramStreamTokenizer.ttype == -3)
        if (paramStreamTokenizer.sval.equalsIgnoreCase("digraph")) {
          paramStreamTokenizer.nextToken();
          if (paramStreamTokenizer.ttype == -3) {
            this.m_graphName = paramStreamTokenizer.sval;
            paramStreamTokenizer.nextToken();
          } 
          while (paramStreamTokenizer.ttype != 123) {
            System.err.println("Error at line " + paramStreamTokenizer.lineno() + " ignoring token " + paramStreamTokenizer.sval);
            paramStreamTokenizer.nextToken();
            if (paramStreamTokenizer.ttype == -1)
              return; 
          } 
          stmtList(paramStreamTokenizer);
        } else if (paramStreamTokenizer.sval.equalsIgnoreCase("graph")) {
          System.err.println("Error. Undirected graphs cannot be used");
        } else {
          System.err.println("Error. Expected graph or digraph at line " + paramStreamTokenizer.lineno());
        }  
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    int[] arrayOfInt1 = new int[this.m_nodes.size()];
    int[] arrayOfInt2 = new int[this.m_nodes.size()];
    byte b;
    for (b = 0; b < this.m_edges.size(); b++) {
      GraphEdge graphEdge = (GraphEdge)this.m_edges.elementAt(b);
      arrayOfInt1[graphEdge.src] = arrayOfInt1[graphEdge.src] + 1;
      arrayOfInt2[graphEdge.dest] = arrayOfInt2[graphEdge.dest] + 1;
    } 
    for (b = 0; b < this.m_edges.size(); b++) {
      GraphEdge graphEdge = (GraphEdge)this.m_edges.elementAt(b);
      GraphNode graphNode1 = (GraphNode)this.m_nodes.elementAt(graphEdge.src);
      GraphNode graphNode2 = (GraphNode)this.m_nodes.elementAt(graphEdge.dest);
      if (graphNode1.edges == null) {
        graphNode1.edges = new int[arrayOfInt1[graphEdge.src]][2];
        for (byte b2 = 0; b2 < graphNode1.edges.length; b2++)
          graphNode1.edges[b2][1] = 0; 
      } 
      if (graphNode2.prnts == null) {
        graphNode2.prnts = new int[arrayOfInt2[graphEdge.dest]];
        for (byte b2 = 0; b2 < graphNode2.prnts.length; b2++)
          graphNode2.prnts[b2] = -1; 
      } 
      byte b1;
      for (b1 = 0; graphNode1.edges[b1][1] != 0; b1++);
      graphNode1.edges[b1][0] = graphEdge.dest;
      graphNode1.edges[b1][1] = graphEdge.type;
      for (b1 = 0; graphNode2.prnts[b1] != -1; b1++);
      graphNode2.prnts[b1] = graphEdge.src;
    } 
  }
  
  protected void stmtList(StreamTokenizer paramStreamTokenizer) throws Exception {
    paramStreamTokenizer.nextToken();
    if (paramStreamTokenizer.ttype == 125 || paramStreamTokenizer.ttype == -1)
      return; 
    stmt(paramStreamTokenizer);
    stmtList(paramStreamTokenizer);
  }
  
  protected void stmt(StreamTokenizer paramStreamTokenizer) {
    if (!paramStreamTokenizer.sval.equalsIgnoreCase("graph") && !paramStreamTokenizer.sval.equalsIgnoreCase("node") && !paramStreamTokenizer.sval.equalsIgnoreCase("edge"))
      try {
        nodeID(paramStreamTokenizer);
        int i = this.m_nodes.indexOf(new GraphNode(paramStreamTokenizer.sval, null));
        paramStreamTokenizer.nextToken();
        if (paramStreamTokenizer.ttype == 91) {
          nodeStmt(paramStreamTokenizer, i);
        } else if (paramStreamTokenizer.ttype == 45) {
          edgeStmt(paramStreamTokenizer, i);
        } else {
          System.err.println("error at lineno " + paramStreamTokenizer.lineno() + " in stmt");
        } 
      } catch (Exception exception) {
        System.err.println("error at lineno " + paramStreamTokenizer.lineno() + " in stmtException");
        exception.printStackTrace();
      }  
  }
  
  protected void nodeID(StreamTokenizer paramStreamTokenizer) throws Exception {
    if (paramStreamTokenizer.ttype == 34 || paramStreamTokenizer.ttype == -3 || (paramStreamTokenizer.ttype >= 97 && paramStreamTokenizer.ttype <= 122) || (paramStreamTokenizer.ttype >= 65 && paramStreamTokenizer.ttype <= 90)) {
      if (this.m_nodes != null && !this.m_nodes.contains(new GraphNode(paramStreamTokenizer.sval, null)))
        this.m_nodes.addElement(new GraphNode(paramStreamTokenizer.sval, paramStreamTokenizer.sval)); 
    } else {
      throw new Exception();
    } 
  }
  
  protected void nodeStmt(StreamTokenizer paramStreamTokenizer, int paramInt) throws Exception {
    paramStreamTokenizer.nextToken();
    GraphNode graphNode = (GraphNode)this.m_nodes.elementAt(paramInt);
    if (paramStreamTokenizer.ttype == 93 || paramStreamTokenizer.ttype == -1)
      return; 
    if (paramStreamTokenizer.ttype == -3)
      if (paramStreamTokenizer.sval.equalsIgnoreCase("label")) {
        paramStreamTokenizer.nextToken();
        if (paramStreamTokenizer.ttype == 61) {
          paramStreamTokenizer.nextToken();
          if (paramStreamTokenizer.ttype == -3 || paramStreamTokenizer.ttype == 34) {
            graphNode.lbl = paramStreamTokenizer.sval;
          } else {
            System.err.println("couldn't find label at line " + paramStreamTokenizer.lineno());
            paramStreamTokenizer.pushBack();
          } 
        } else {
          System.err.println("couldn't find label at line " + paramStreamTokenizer.lineno());
          paramStreamTokenizer.pushBack();
        } 
      } else if (paramStreamTokenizer.sval.equalsIgnoreCase("color")) {
        paramStreamTokenizer.nextToken();
        if (paramStreamTokenizer.ttype == 61) {
          paramStreamTokenizer.nextToken();
          if (paramStreamTokenizer.ttype != -3 && paramStreamTokenizer.ttype != 34) {
            System.err.println("couldn't find color at line " + paramStreamTokenizer.lineno());
            paramStreamTokenizer.pushBack();
          } 
        } else {
          System.err.println("couldn't find color at line " + paramStreamTokenizer.lineno());
          paramStreamTokenizer.pushBack();
        } 
      } else if (paramStreamTokenizer.sval.equalsIgnoreCase("style")) {
        paramStreamTokenizer.nextToken();
        if (paramStreamTokenizer.ttype == 61) {
          paramStreamTokenizer.nextToken();
          if (paramStreamTokenizer.ttype != -3 && paramStreamTokenizer.ttype != 34) {
            System.err.println("couldn't find style at line " + paramStreamTokenizer.lineno());
            paramStreamTokenizer.pushBack();
          } 
        } else {
          System.err.println("couldn't find style at line " + paramStreamTokenizer.lineno());
          paramStreamTokenizer.pushBack();
        } 
      }  
    nodeStmt(paramStreamTokenizer, paramInt);
  }
  
  protected void edgeStmt(StreamTokenizer paramStreamTokenizer, int paramInt) throws Exception {
    paramStreamTokenizer.nextToken();
    GraphEdge graphEdge = null;
    if (paramStreamTokenizer.ttype == 62) {
      paramStreamTokenizer.nextToken();
      if (paramStreamTokenizer.ttype == 123) {
        while (true) {
          paramStreamTokenizer.nextToken();
          if (paramStreamTokenizer.ttype == 125)
            break; 
          nodeID(paramStreamTokenizer);
          graphEdge = new GraphEdge(paramInt, this.m_nodes.indexOf(new GraphNode(paramStreamTokenizer.sval, null)), 1);
          if (this.m_edges != null && !this.m_edges.contains(graphEdge))
            this.m_edges.addElement(graphEdge); 
        } 
      } else {
        nodeID(paramStreamTokenizer);
        graphEdge = new GraphEdge(paramInt, this.m_nodes.indexOf(new GraphNode(paramStreamTokenizer.sval, null)), 1);
        if (this.m_edges != null && !this.m_edges.contains(graphEdge))
          this.m_edges.addElement(graphEdge); 
      } 
    } else {
      if (paramStreamTokenizer.ttype == 45) {
        System.err.println("Error at line " + paramStreamTokenizer.lineno() + ". Cannot deal with undirected edges");
        if (paramStreamTokenizer.ttype == -3)
          paramStreamTokenizer.pushBack(); 
        return;
      } 
      System.err.println("Error at line " + paramStreamTokenizer.lineno() + " in edgeStmt");
      if (paramStreamTokenizer.ttype == -3)
        paramStreamTokenizer.pushBack(); 
      return;
    } 
    paramStreamTokenizer.nextToken();
    if (paramStreamTokenizer.ttype == 91) {
      edgeAttrib(paramStreamTokenizer, graphEdge);
    } else {
      paramStreamTokenizer.pushBack();
    } 
  }
  
  protected void edgeAttrib(StreamTokenizer paramStreamTokenizer, GraphEdge paramGraphEdge) throws Exception {
    paramStreamTokenizer.nextToken();
    if (paramStreamTokenizer.ttype == 93 || paramStreamTokenizer.ttype == -1)
      return; 
    if (paramStreamTokenizer.ttype == -3)
      if (paramStreamTokenizer.sval.equalsIgnoreCase("label")) {
        paramStreamTokenizer.nextToken();
        if (paramStreamTokenizer.ttype == 61) {
          paramStreamTokenizer.nextToken();
          if (paramStreamTokenizer.ttype == -3 || paramStreamTokenizer.ttype == 34) {
            System.err.println("found label " + paramStreamTokenizer.sval);
          } else {
            System.err.println("couldn't find label at line " + paramStreamTokenizer.lineno());
            paramStreamTokenizer.pushBack();
          } 
        } else {
          System.err.println("couldn't find label at line " + paramStreamTokenizer.lineno());
          paramStreamTokenizer.pushBack();
        } 
      } else if (paramStreamTokenizer.sval.equalsIgnoreCase("color")) {
        paramStreamTokenizer.nextToken();
        if (paramStreamTokenizer.ttype == 61) {
          paramStreamTokenizer.nextToken();
          if (paramStreamTokenizer.ttype != -3 && paramStreamTokenizer.ttype != 34) {
            System.err.println("couldn't find color at line " + paramStreamTokenizer.lineno());
            paramStreamTokenizer.pushBack();
          } 
        } else {
          System.err.println("couldn't find color at line " + paramStreamTokenizer.lineno());
          paramStreamTokenizer.pushBack();
        } 
      } else if (paramStreamTokenizer.sval.equalsIgnoreCase("style")) {
        paramStreamTokenizer.nextToken();
        if (paramStreamTokenizer.ttype == 61) {
          paramStreamTokenizer.nextToken();
          if (paramStreamTokenizer.ttype != -3 && paramStreamTokenizer.ttype != 34) {
            System.err.println("couldn't find style at line " + paramStreamTokenizer.lineno());
            paramStreamTokenizer.pushBack();
          } 
        } else {
          System.err.println("couldn't find style at line " + paramStreamTokenizer.lineno());
          paramStreamTokenizer.pushBack();
        } 
      }  
    edgeAttrib(paramStreamTokenizer, paramGraphEdge);
  }
  
  public static void writeDOT(String paramString1, String paramString2, FastVector paramFastVector1, FastVector paramFastVector2) {
    try {
      FileWriter fileWriter = new FileWriter(paramString1);
      fileWriter.write("digraph ", 0, "digraph ".length());
      if (paramString2 != null)
        fileWriter.write(paramString2 + " ", 0, paramString2.length() + 1); 
      fileWriter.write("{\n", 0, "{\n".length());
      for (byte b = 0; b < paramFastVector2.size(); b++) {
        GraphEdge graphEdge = (GraphEdge)paramFastVector2.elementAt(b);
        fileWriter.write(((GraphNode)paramFastVector1.elementAt(graphEdge.src)).ID, 0, ((GraphNode)paramFastVector1.elementAt(graphEdge.src)).ID.length());
        fileWriter.write("->", 0, "->".length());
        fileWriter.write(((GraphNode)paramFastVector1.elementAt(graphEdge.dest)).ID + "\n", 0, ((GraphNode)paramFastVector1.elementAt(graphEdge.dest)).ID.length() + 1);
      } 
      fileWriter.write("}\n", 0, "}\n".length());
      fileWriter.close();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\graphvisualizer\DotParser.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */