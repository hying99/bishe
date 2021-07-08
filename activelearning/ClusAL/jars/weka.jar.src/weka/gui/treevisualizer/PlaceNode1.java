package weka.gui.treevisualizer;

public class PlaceNode1 implements NodePlace {
  private double[] m_levels;
  
  private int m_noLevels;
  
  private int[] m_levelNode;
  
  private double m_yRatio;
  
  public void place(Node paramNode) {
    this.m_noLevels = Node.getHeight(paramNode, 0) + 1;
    this.m_yRatio = 1.0D / this.m_noLevels;
    this.m_levels = new double[this.m_noLevels];
    this.m_levelNode = new int[this.m_noLevels];
    byte b;
    for (b = 0; b < this.m_noLevels; b++) {
      this.m_levels[b] = 1.0D;
      this.m_levelNode[b] = 0;
    } 
    setNumOfNodes(paramNode, 0);
    for (b = 0; b < this.m_noLevels; b++)
      this.m_levels[b] = 1.0D / this.m_levels[b]; 
    placer(paramNode, 0);
  }
  
  private void setNumOfNodes(Node paramNode, int paramInt) {
    this.m_levels[++paramInt] = this.m_levels[++paramInt] + 1.0D;
    Edge edge;
    for (byte b = 0; (edge = paramNode.getChild(b)) != null && paramNode.getCVisible(); b++)
      setNumOfNodes(edge.getTarget(), paramInt); 
  }
  
  private void placer(Node paramNode, int paramInt) {
    this.m_levelNode[++paramInt] = this.m_levelNode[++paramInt] + 1;
    paramNode.setCenter(this.m_levelNode[paramInt] * this.m_levels[paramInt]);
    paramNode.setTop(paramInt * this.m_yRatio);
    Edge edge;
    for (byte b = 0; (edge = paramNode.getChild(b)) != null && paramNode.getCVisible(); b++)
      placer(edge.getTarget(), paramInt); 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\treevisualizer\PlaceNode1.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */