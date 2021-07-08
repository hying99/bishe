/*     */ package jeans.tree;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import jeans.util.MyArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MyNode
/*     */   implements Node, Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  35 */   protected MyArray m_Children = new MyArray();
/*     */   
/*     */   protected Node m_Parent;
/*     */   
/*     */   public MyNode() {}
/*     */   
/*     */   public MyNode(Node parent) {
/*  42 */     this.m_Parent = parent;
/*     */   }
/*     */   
/*     */   public MyNode getRoot() {
/*  46 */     if (this.m_Parent == null) return this; 
/*  47 */     return ((MyNode)this.m_Parent).getRoot();
/*     */   }
/*     */   
/*     */   public final int[] getPath() {
/*  51 */     Node node = this;
/*  52 */     int d = getLevel();
/*  53 */     int[] path = new int[d];
/*  54 */     for (int i = 0; i < d; i++) {
/*  55 */       Node parent = node.getParent();
/*  56 */       path[i] = ((MyNode)parent).indexOf(node);
/*  57 */       node = parent;
/*     */     } 
/*  59 */     return path;
/*     */   }
/*     */ 
/*     */   
/*     */   public final MyNode fromPath(int[] path, int skip) {
/*  64 */     MyNode crnode = this;
/*  65 */     for (int i = path.length - 1 - skip; i >= 0; i--) {
/*  66 */       int pos = path[i];
/*  67 */       if (pos < 0 || pos >= crnode.getNbChildren()) {
/*  68 */         return null;
/*     */       }
/*  70 */       crnode = (MyNode)crnode.getChild(pos);
/*     */     } 
/*     */     
/*  73 */     return crnode;
/*     */   }
/*     */   
/*     */   public final int indexOf(Node child) {
/*  77 */     for (int i = 0; i < getNbChildren(); i++) {
/*  78 */       if (child == getChild(i)) return i; 
/*     */     } 
/*  80 */     return -1;
/*     */   }
/*     */   
/*     */   public boolean equalsPath(int[] path) {
/*  84 */     int[] mypath = getPath();
/*  85 */     if (mypath.length != path.length) return false; 
/*  86 */     for (int i = 0; i < path.length; i++) {
/*  87 */       if (mypath[i] != path[i]) return false; 
/*  88 */     }  return true;
/*     */   }
/*     */   
/*     */   public static void showPath(int[] path, PrintWriter out) {
/*  92 */     for (int i = 0; i < path.length; i++) {
/*  93 */       if (i != 0) out.print(","); 
/*  94 */       out.print(path[i]);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void showPath(int[] path) {
/*  99 */     for (int i = 0; i < path.length; i++) {
/* 100 */       if (i != 0) System.out.print(","); 
/* 101 */       System.out.print(path[i]);
/*     */     } 
/*     */   }
/*     */   
/*     */   public MyNode cloneNode() {
/* 106 */     return new MyNode();
/*     */   }
/*     */ 
/*     */   
/*     */   public final void addChild(Node node) {
/* 111 */     node.setParent(this);
/* 112 */     this.m_Children.addElement(node);
/*     */   }
/*     */   
/*     */   public final void setChild(Node node, int idx) {
/* 116 */     node.setParent(this);
/* 117 */     this.m_Children.setElementAt(node, idx);
/*     */   }
/*     */   
/*     */   public final void removeChild(Node node) {
/* 121 */     node.setParent(null);
/* 122 */     this.m_Children.removeElement(node);
/*     */   }
/*     */   
/*     */   public final void removeChild(int idx) {
/* 126 */     MyNode child = (MyNode)getChild(idx);
/* 127 */     if (child != null) child.setParent(null); 
/* 128 */     this.m_Children.removeElementAt(idx);
/*     */   }
/*     */   
/*     */   public final void removeAllChildren() {
/* 132 */     int nb = getNbChildren();
/* 133 */     for (int i = 0; i < nb; i++) {
/* 134 */       Node node = getChild(i);
/* 135 */       node.setParent(null);
/*     */     } 
/* 137 */     this.m_Children.removeAllElements();
/*     */   }
/*     */   
/*     */   public final Node getParent() {
/* 141 */     return this.m_Parent;
/*     */   }
/*     */   
/*     */   public final void setParent(Node parent) {
/* 145 */     this.m_Parent = parent;
/*     */   }
/*     */   
/*     */   public final Node getChild(int idx) {
/* 149 */     return (Node)this.m_Children.elementAt(idx);
/*     */   }
/*     */   
/*     */   public final int getNbChildren() {
/* 153 */     return this.m_Children.size();
/*     */   }
/*     */   
/*     */   public final void setNbChildren(int nb) {
/* 157 */     this.m_Children.setSize(nb);
/*     */   }
/*     */   
/*     */   public final boolean atTopLevel() {
/* 161 */     return (this.m_Parent == null);
/*     */   }
/*     */   
/*     */   public final boolean atBottomLevel() {
/* 165 */     return (this.m_Children.size() == 0);
/*     */   }
/*     */   
/*     */   public final MyNode cloneTree() {
/* 169 */     MyNode clone = cloneNode();
/* 170 */     int arity = getNbChildren();
/* 171 */     clone.setNbChildren(arity);
/* 172 */     for (int i = 0; i < arity; i++) {
/* 173 */       MyNode node = (MyNode)getChild(i);
/* 174 */       clone.setChild(node.cloneTree(), i);
/*     */     } 
/* 176 */     return clone;
/*     */   }
/*     */   
/*     */   public final MyNode cloneTree(MyNode n1, MyNode n2) {
/* 180 */     if (n1 == this) {
/* 181 */       return n2;
/*     */     }
/* 183 */     MyNode clone = cloneNode();
/* 184 */     int arity = getNbChildren();
/* 185 */     clone.setNbChildren(arity);
/* 186 */     for (int i = 0; i < arity; i++) {
/* 187 */       MyNode node = (MyNode)getChild(i);
/* 188 */       clone.setChild(node.cloneTree(n1, n2), i);
/*     */     } 
/* 190 */     return clone;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getLevel() {
/* 195 */     int depth = 0;
/* 196 */     Node node = getParent();
/* 197 */     while (node != null) {
/* 198 */       depth++;
/* 199 */       node = node.getParent();
/*     */     } 
/* 201 */     return depth;
/*     */   }
/*     */   
/*     */   public final int getMaxLeafDepth() {
/* 205 */     int nb = getNbChildren();
/* 206 */     if (nb == 0) {
/* 207 */       return 1;
/*     */     }
/* 209 */     int max = 0;
/* 210 */     for (int i = 0; i < nb; i++) {
/* 211 */       MyNode node = (MyNode)getChild(i);
/* 212 */       max = Math.max(max, node.getMaxLeafDepth());
/*     */     } 
/* 214 */     return max + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getNbNodes() {
/* 219 */     int count = 1;
/* 220 */     int nb = getNbChildren();
/* 221 */     for (int i = 0; i < nb; i++) {
/* 222 */       MyNode node = (MyNode)getChild(i);
/* 223 */       count += node.getNbNodes();
/*     */     } 
/* 225 */     return count;
/*     */   }
/*     */   
/*     */   public final int getNbLeaves() {
/* 229 */     int nb = getNbChildren();
/* 230 */     if (nb == 0) {
/* 231 */       return 1;
/*     */     }
/* 233 */     int count = 0;
/* 234 */     for (int i = 0; i < nb; i++) {
/* 235 */       MyNode node = (MyNode)getChild(i);
/* 236 */       count += node.getNbLeaves();
/*     */     } 
/* 238 */     return count;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\tree\MyNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */