/*    */ package jeans.tree;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IntervalTreeNodeRB
/*    */ {
/*    */   public boolean red;
/*    */   public int key;
/*    */   public int high;
/*    */   public int maxHigh;
/*    */   public int value;
/*    */   public IntervalTreeNodeRB left;
/*    */   public IntervalTreeNodeRB right;
/*    */   public IntervalTreeNodeRB parent;
/*    */   
/*    */   public IntervalTreeNodeRB() {}
/*    */   
/*    */   public IntervalTreeNodeRB(int min, int max, int value) {
/* 35 */     this.key = min;
/* 36 */     this.high = this.maxHigh = max;
/* 37 */     this.value = value;
/*    */   }
/*    */   
/*    */   public void print(IntervalTreeNodeRB nil, IntervalTreeNodeRB root) {
/* 41 */     System.out.println("k = " + this.key + " h = " + this.high + " maxhigh = " + this.maxHigh);
/* 42 */     System.out.print("l.key = ");
/* 43 */     if (this.left == nil) { System.out.println("NULL"); } else { System.out.println(this.left.key); }
/* 44 */      System.out.print("r.key = ");
/* 45 */     if (this.right == nil) { System.out.println("NULL"); } else { System.out.println(this.right.key); }
/* 46 */      System.out.print("p.key = ");
/* 47 */     if (this.parent == root) { System.out.println("NULL"); } else { System.out.println(this.parent.key); }
/*    */   
/*    */   }
/*    */   public void print() {
/* 51 */     System.out.println("[" + this.key + "," + this.high + " (" + this.maxHigh + ")]: " + this.value);
/*    */   }
/*    */   
/*    */   public void setMinMax(int value) {
/* 55 */     this.key = this.high = this.maxHigh = value;
/*    */   }
/*    */   
/*    */   public void setAllNodes(IntervalTreeNodeRB node) {
/* 59 */     this.left = this.right = this.parent = node;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\tree\IntervalTreeNodeRB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */