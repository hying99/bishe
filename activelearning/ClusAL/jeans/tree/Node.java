package jeans.tree;

public interface Node {
  Node getParent();
  
  Node getChild(int paramInt);
  
  int getNbChildren();
  
  boolean atTopLevel();
  
  boolean atBottomLevel();
  
  void addChild(Node paramNode);
  
  void removeChild(Node paramNode);
  
  void setParent(Node paramNode);
  
  int getLevel();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\tree\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */