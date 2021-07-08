package weka.gui.graphvisualizer;

public class GraphNode implements GraphConstants {
  String ID;
  
  String lbl;
  
  String[] outcomes;
  
  double[][] probs;
  
  int x = 0;
  
  int y = 0;
  
  int[] prnts;
  
  int[][] edges;
  
  int nodeType = 3;
  
  public GraphNode(String paramString1, String paramString2) {
    this.ID = paramString1;
    this.lbl = paramString2;
    this.nodeType = 3;
  }
  
  public GraphNode(String paramString1, String paramString2, int paramInt) {
    this.ID = paramString1;
    this.lbl = paramString2;
    this.nodeType = paramInt;
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof GraphNode && ((GraphNode)paramObject).ID.equalsIgnoreCase(this.ID));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\graphvisualizer\GraphNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */