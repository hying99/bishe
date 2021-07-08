package weka.gui.graphvisualizer;

public class GraphEdge {
  protected int src;
  
  protected int dest;
  
  protected int type;
  
  protected String srcLbl;
  
  protected String destLbl;
  
  public GraphEdge(int paramInt1, int paramInt2, int paramInt3) {
    this.src = paramInt1;
    this.dest = paramInt2;
    this.type = paramInt3;
    this.srcLbl = null;
    this.destLbl = null;
  }
  
  public GraphEdge(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2) {
    this.src = paramInt1;
    this.dest = paramInt2;
    this.type = paramInt3;
    this.srcLbl = paramString1;
    this.destLbl = paramString2;
  }
  
  public String toString() {
    return "(" + this.src + "," + this.dest + "," + this.type + ")";
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof GraphEdge && ((GraphEdge)paramObject).src == this.src && ((GraphEdge)paramObject).dest == this.dest && ((GraphEdge)paramObject).type == this.type);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\graphvisualizer\GraphEdge.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */