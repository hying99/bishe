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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HistoryList
/*     */ {
/*  39 */   private static HistoryList instance = null;
/*     */   
/*  41 */   private Vector ops = new Vector();
/*     */ 
/*     */   
/*     */   private int firstNewOp;
/*     */ 
/*     */ 
/*     */   
/*     */   public static HistoryList getInstance() {
/*  49 */     if (instance == null) instance = new HistoryList(); 
/*  50 */     return instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addOperation(Operation op) {
/*  59 */     if (this.firstNewOp < this.ops.size()) this.ops.setSize(this.firstNewOp); 
/*  60 */     this.ops.addElement(op);
/*  61 */     this.firstNewOp++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Operation undoOperation() {
/*  72 */     this.firstNewOp--;
/*  73 */     Operation op = this.ops.elementAt(this.firstNewOp);
/*  74 */     op.undo();
/*  75 */     return op;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Operation redoOperation() {
/*  84 */     Operation op = this.ops.elementAt(this.firstNewOp);
/*  85 */     op.redo();
/*  86 */     this.firstNewOp++;
/*  87 */     return op;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearHistory() {
/*  96 */     this.ops.setSize(0);
/*  97 */     this.firstNewOp = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int lastDoneOperation() {
/* 106 */     return this.firstNewOp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int totalNumberOfOperations() {
/* 113 */     return this.ops.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canUndo() {
/* 121 */     return (lastDoneOperation() > 0);
/*     */   }
/*     */   
/*     */   public boolean canRedo() {
/* 125 */     return (lastDoneOperation() != totalNumberOfOperations());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Vector getOperations() {
/* 133 */     return this.ops;
/*     */   }
/*     */   
/*     */   public void undoOperations(int number) {
/* 137 */     for (int ctr = 0; ctr < number; ctr++)
/* 138 */       undoOperation(); 
/*     */   }
/*     */   
/*     */   public void redoOperations(int number) {
/* 142 */     for (int ctr = 0; ctr < number; ctr++)
/* 143 */       redoOperation(); 
/*     */   }
/*     */   
/*     */   public Object getState() {
/* 147 */     return new HistoryState(this.ops, this.firstNewOp);
/*     */   }
/*     */   
/*     */   public Object getEmptyState() {
/* 151 */     return new HistoryState();
/*     */   }
/*     */   
/*     */   public void setState(Object object) {
/* 155 */     HistoryState state = (HistoryState)object;
/* 156 */     this.ops = state.getOperations();
/* 157 */     this.firstNewOp = state.getFirstNewOp();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\HistoryList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */