package weka.gui;

import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;

public class HierarchyPropertyParser implements Serializable {
  private TreeNode m_Root = new TreeNode();
  
  private TreeNode m_Current;
  
  private String m_Seperator = ".";
  
  private int m_Depth = 0;
  
  public HierarchyPropertyParser() {
    this.m_Root.parent = null;
    this.m_Root.children = new Vector();
    goToRoot();
  }
  
  public HierarchyPropertyParser(String paramString1, String paramString2) throws Exception {
    this();
    build(paramString1, paramString2);
  }
  
  public void setSeperator(String paramString) {
    this.m_Seperator = paramString;
  }
  
  public String getSeperator() {
    return this.m_Seperator;
  }
  
  public void build(String paramString1, String paramString2) throws Exception {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString1, paramString2);
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken().trim();
      if (!isHierachic(str))
        throw new Exception("The given property is not inhierachy structure with seperators!"); 
      add(str);
    } 
    goToRoot();
  }
  
  public synchronized void add(String paramString) {
    String[] arrayOfString = tokenize(paramString);
    if (this.m_Root.value == null)
      this.m_Root.value = arrayOfString[0]; 
    buildBranch(this.m_Root, arrayOfString, 1);
  }
  
  private void buildBranch(TreeNode paramTreeNode, String[] paramArrayOfString, int paramInt) {
    if (paramInt == paramArrayOfString.length) {
      paramTreeNode.children = null;
      return;
    } 
    if (paramInt > this.m_Depth - 1)
      this.m_Depth = paramInt + 1; 
    Vector vector = paramTreeNode.children;
    int i = search(vector, paramArrayOfString[paramInt]);
    if (i != -1) {
      TreeNode treeNode = vector.elementAt(i);
      if (treeNode.children == null)
        treeNode.children = new Vector(); 
      buildBranch(treeNode, paramArrayOfString, paramInt + 1);
    } else {
      TreeNode treeNode = new TreeNode();
      treeNode.parent = paramTreeNode;
      treeNode.value = paramArrayOfString[paramInt];
      treeNode.children = new Vector();
      treeNode.level = paramInt;
      if (paramTreeNode != this.m_Root) {
        paramTreeNode.context += this.m_Seperator + paramTreeNode.value;
      } else {
        treeNode.context = paramTreeNode.value;
      } 
      vector.addElement(treeNode);
      buildBranch(treeNode, paramArrayOfString, paramInt + 1);
    } 
  }
  
  public String[] tokenize(String paramString) {
    Vector vector = new Vector();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, this.m_Seperator);
    while (stringTokenizer.hasMoreTokens())
      vector.addElement(stringTokenizer.nextToken()); 
    String[] arrayOfString = new String[vector.size()];
    for (byte b = 0; b < vector.size(); b++)
      arrayOfString[b] = vector.elementAt(b); 
    return arrayOfString;
  }
  
  public boolean contains(String paramString) {
    String[] arrayOfString = tokenize(paramString);
    return !arrayOfString[0].equals(this.m_Root.value) ? false : isContained(this.m_Root, arrayOfString, 1);
  }
  
  private boolean isContained(TreeNode paramTreeNode, String[] paramArrayOfString, int paramInt) {
    if (paramInt == paramArrayOfString.length)
      return true; 
    if (paramInt > paramArrayOfString.length)
      return false; 
    Vector vector = paramTreeNode.children;
    int i = search(vector, paramArrayOfString[paramInt]);
    if (i != -1) {
      TreeNode treeNode = vector.elementAt(i);
      return isContained(treeNode, paramArrayOfString, paramInt + 1);
    } 
    return false;
  }
  
  public boolean isHierachic(String paramString) {
    int i = paramString.indexOf(this.m_Seperator);
    return !(i == paramString.length() - 1 || i == -1);
  }
  
  public int search(Vector paramVector, String paramString) {
    if (paramVector == null)
      return -1; 
    for (byte b = 0; b < paramVector.size(); b++) {
      if (paramString.equals(((TreeNode)paramVector.elementAt(b)).value))
        return b; 
    } 
    return -1;
  }
  
  public synchronized boolean goTo(String paramString) {
    if (!isHierachic(paramString)) {
      if (this.m_Root.value.equals(paramString)) {
        goToRoot();
        return true;
      } 
      return false;
    } 
    TreeNode treeNode = this.m_Current;
    this.m_Current = new TreeNode();
    goToRoot();
    String[] arrayOfString = tokenize(paramString);
    if (!this.m_Current.value.equals(arrayOfString[0]))
      return false; 
    for (byte b = 1; b < arrayOfString.length; b++) {
      int i = search(this.m_Current.children, arrayOfString[b]);
      if (i == -1) {
        this.m_Current = treeNode;
        return false;
      } 
      this.m_Current = this.m_Current.children.elementAt(i);
    } 
    return true;
  }
  
  public synchronized boolean goDown(String paramString) {
    if (!isHierachic(paramString))
      return goToChild(paramString); 
    TreeNode treeNode = this.m_Current;
    this.m_Current = new TreeNode();
    String[] arrayOfString = tokenize(paramString);
    int i = search(treeNode.children, arrayOfString[0]);
    if (i == -1) {
      this.m_Current = treeNode;
      return false;
    } 
    this.m_Current = treeNode.children.elementAt(i);
    for (byte b = 1; b < arrayOfString.length; b++) {
      i = search(this.m_Current.children, arrayOfString[b]);
      if (i == -1) {
        this.m_Current = treeNode;
        return false;
      } 
      this.m_Current = this.m_Current.children.elementAt(i);
    } 
    return true;
  }
  
  public synchronized void goToRoot() {
    this.m_Current = this.m_Root;
  }
  
  public synchronized void goToParent() {
    if (this.m_Current.parent != null)
      this.m_Current = this.m_Current.parent; 
  }
  
  public synchronized boolean goToChild(String paramString) {
    if (this.m_Current.children == null)
      return false; 
    int i = search(this.m_Current.children, paramString);
    if (i == -1)
      return false; 
    this.m_Current = this.m_Current.children.elementAt(i);
    return true;
  }
  
  public synchronized void goToChild(int paramInt) throws Exception {
    if (this.m_Current.children == null || paramInt < 0 || paramInt >= this.m_Current.children.size())
      throw new Exception("Position out of range or leaf reached"); 
    this.m_Current = this.m_Current.children.elementAt(paramInt);
  }
  
  public synchronized int numChildren() {
    return (this.m_Current.children == null) ? 0 : this.m_Current.children.size();
  }
  
  public synchronized String[] childrenValues() {
    if (this.m_Current.children == null)
      return null; 
    Vector vector = this.m_Current.children;
    String[] arrayOfString = new String[vector.size()];
    for (byte b = 0; b < vector.size(); b++)
      arrayOfString[b] = ((TreeNode)vector.elementAt(b)).value; 
    return arrayOfString;
  }
  
  public synchronized String parentValue() {
    return (this.m_Current.parent != null) ? this.m_Current.parent.value : null;
  }
  
  public synchronized boolean isLeafReached() {
    return (this.m_Current.children == null);
  }
  
  public synchronized boolean isRootReached() {
    return (this.m_Current.parent == null);
  }
  
  public synchronized String getValue() {
    return this.m_Current.value;
  }
  
  public synchronized int getLevel() {
    return this.m_Current.level;
  }
  
  public int depth() {
    return this.m_Depth;
  }
  
  public synchronized String context() {
    return this.m_Current.context;
  }
  
  public synchronized String fullValue() {
    return (this.m_Current == this.m_Root) ? this.m_Root.value : (this.m_Current.context + this.m_Seperator + this.m_Current.value);
  }
  
  public String showTree() {
    return showNode(this.m_Root, null);
  }
  
  private String showNode(TreeNode paramTreeNode, boolean[] paramArrayOfboolean) {
    StringBuffer stringBuffer = new StringBuffer();
    byte b;
    for (b = 0; b < paramTreeNode.level - 1; b++) {
      if (paramArrayOfboolean[b]) {
        stringBuffer.append("  |       ");
      } else {
        stringBuffer.append("          ");
      } 
    } 
    if (paramTreeNode.level != 0)
      stringBuffer.append("  |------ "); 
    stringBuffer.append(paramTreeNode.value + "(" + paramTreeNode.level + ")" + "[" + paramTreeNode.context + "]\n");
    if (paramTreeNode.children != null)
      for (b = 0; b < paramTreeNode.children.size(); b++) {
        boolean[] arrayOfBoolean = new boolean[paramTreeNode.level + 1];
        int i = paramTreeNode.level;
        if (paramArrayOfboolean != null)
          for (byte b1 = 0; b1 < i; b1++)
            arrayOfBoolean[b1] = paramArrayOfboolean[b1];  
        if (b == paramTreeNode.children.size() - 1) {
          arrayOfBoolean[i] = false;
        } else {
          arrayOfBoolean[i] = true;
        } 
        stringBuffer.append(showNode(paramTreeNode.children.elementAt(b), arrayOfBoolean));
      }  
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("node1.node1_1.node1_1_1.node1_1_1_1, ");
    stringBuffer.append("node1.node1_1.node1_1_1.node1_1_1_2, ");
    stringBuffer.append("node1.node1_1.node1_1_1.node1_1_1_3, ");
    stringBuffer.append("node1.node1_1.node1_1_2.node1_1_2_1, ");
    stringBuffer.append("node1.node1_1.node1_1_3.node1_1_3_1, ");
    stringBuffer.append("node1.node1_2.node1_2_1.node1_2_1_1, ");
    stringBuffer.append("node1.node1_2.node1_2_3.node1_2_3_1, ");
    stringBuffer.append("node1.node1_3.node1_3_3.node1_3_3_1, ");
    stringBuffer.append("node1.node1_3.node1_3_3.node1_3_3_2, ");
    String str = stringBuffer.toString();
    try {
      HierarchyPropertyParser hierarchyPropertyParser = new HierarchyPropertyParser(str, ", ");
      System.out.println("seperator: " + hierarchyPropertyParser.getSeperator());
      System.out.println("depth: " + hierarchyPropertyParser.depth());
      System.out.println("The tree:\n\n" + hierarchyPropertyParser.showTree());
      hierarchyPropertyParser.goToRoot();
      System.out.println("goto: " + hierarchyPropertyParser.goTo("node1.node1_2.node1_2_1") + ": " + hierarchyPropertyParser.getValue() + " | " + hierarchyPropertyParser.fullValue() + " leaf? " + hierarchyPropertyParser.isLeafReached());
      System.out.println("go down(wrong): " + hierarchyPropertyParser.goDown("node1"));
      System.out.println("Stay still? " + hierarchyPropertyParser.getValue());
      System.out.println("go to child: " + hierarchyPropertyParser.goToChild("node1_2_1_1") + ": " + hierarchyPropertyParser.getValue() + " | " + hierarchyPropertyParser.fullValue() + " leaf? " + hierarchyPropertyParser.isLeafReached() + " root? " + hierarchyPropertyParser.isRootReached());
      System.out.println("parent: " + hierarchyPropertyParser.parentValue());
      System.out.println("level: " + hierarchyPropertyParser.getLevel());
      System.out.println("context: " + hierarchyPropertyParser.context());
      hierarchyPropertyParser.goToRoot();
      System.out.println("After gotoRoot. leaf? " + hierarchyPropertyParser.isLeafReached() + " root? " + hierarchyPropertyParser.isRootReached());
      System.out.println("Go down(correct): " + hierarchyPropertyParser.goDown("node1_1.node1_1_1") + " value: " + hierarchyPropertyParser.getValue() + " | " + hierarchyPropertyParser.fullValue() + " level: " + hierarchyPropertyParser.getLevel() + " leaf? " + hierarchyPropertyParser.isLeafReached() + " root? " + hierarchyPropertyParser.isRootReached());
      hierarchyPropertyParser.goToParent();
      System.out.println("value: " + hierarchyPropertyParser.getValue() + " | " + hierarchyPropertyParser.fullValue());
      System.out.println("level: " + hierarchyPropertyParser.getLevel());
      String[] arrayOfString = hierarchyPropertyParser.childrenValues();
      for (byte b = 0; b < arrayOfString.length; b++) {
        System.out.print("children " + b + ": " + arrayOfString[b]);
        hierarchyPropertyParser.goDown(arrayOfString[b]);
        System.out.println("real value: " + hierarchyPropertyParser.getValue() + " | " + hierarchyPropertyParser.fullValue() + "(level: " + hierarchyPropertyParser.getLevel() + ")");
        hierarchyPropertyParser.goToParent();
      } 
      System.out.println("Another way to go to root:" + hierarchyPropertyParser.goTo("node1") + ": " + hierarchyPropertyParser.getValue() + " | " + hierarchyPropertyParser.fullValue());
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
  
  private class TreeNode {
    public TreeNode parent;
    
    public String value;
    
    public Vector children;
    
    public int level;
    
    public String context;
    
    private final HierarchyPropertyParser this$0;
    
    private TreeNode(HierarchyPropertyParser this$0) {
      HierarchyPropertyParser.this = HierarchyPropertyParser.this;
      this.parent = null;
      this.value = null;
      this.children = null;
      this.level = 0;
      this.context = null;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\HierarchyPropertyParser.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */