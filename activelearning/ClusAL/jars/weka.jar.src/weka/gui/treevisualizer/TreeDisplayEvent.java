package weka.gui.treevisualizer;

public class TreeDisplayEvent {
  public static final int NO_COMMAND = 0;
  
  public static final int ADD_CHILDREN = 1;
  
  public static final int REMOVE_CHILDREN = 2;
  
  public static final int ACCEPT = 3;
  
  public static final int CLASSIFY_CHILD = 4;
  
  public static final int SEND_INSTANCES = 5;
  
  private int m_command = 0;
  
  private String m_nodeId;
  
  public TreeDisplayEvent(int paramInt, String paramString) {
    if (paramInt == 1 || paramInt == 2 || paramInt == 3 || paramInt == 4 || paramInt == 5)
      this.m_command = paramInt; 
    this.m_nodeId = paramString;
  }
  
  public int getCommand() {
    return this.m_command;
  }
  
  public String getID() {
    return this.m_nodeId;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\treevisualizer\TreeDisplayEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */