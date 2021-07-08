/*     */ package jeans.util;
/*     */ 
/*     */ import java.util.Vector;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TerminalHistoryList
/*     */ {
/*  35 */   private Vector ops = new Vector();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int firstNewOp;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addOperation(Object op) {
/*  47 */     this.ops.addElement(op);
/*  48 */     this.firstNewOp = this.ops.size();
/*     */   }
/*     */   
/*     */   public boolean containsOperation(Object op) {
/*  52 */     return this.ops.contains(op);
/*     */   }
/*     */   
/*     */   public void removeOperation(Object op) {
/*  56 */     this.ops.removeElement(op);
/*  57 */     if (this.firstNewOp > this.ops.size()) this.firstNewOp = this.ops.size();
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object undoOperation() {
/*  68 */     this.firstNewOp--;
/*  69 */     return this.ops.elementAt(this.firstNewOp);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object redoOperation() {
/*  78 */     this.firstNewOp++;
/*  79 */     Object op = this.ops.elementAt(this.firstNewOp);
/*  80 */     return op;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearHistory() {
/*  89 */     this.ops.setSize(0);
/*  90 */     this.firstNewOp = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int lastDoneOperation() {
/*  99 */     return this.firstNewOp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int totalNumberOfOperations() {
/* 106 */     return this.ops.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canUndo() {
/* 114 */     return (lastDoneOperation() > 0);
/*     */   }
/*     */   
/*     */   public boolean canRedo() {
/* 118 */     return (lastDoneOperation() < totalNumberOfOperations() - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Vector getOperations() {
/* 126 */     return this.ops;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\TerminalHistoryList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */