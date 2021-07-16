/*    */ package jeans.util;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IntegerStack
/*    */ {
/*    */   protected int[] stack;
/*    */   protected int pos;
/*    */   protected int grow;
/*    */   protected int size;
/*    */   
/*    */   public IntegerStack() {
/* 33 */     this(10);
/*    */   }
/*    */   
/*    */   public IntegerStack(int grow) {
/* 37 */     this.grow = grow;
/* 38 */     this.size = 0;
/*    */   }
/*    */   
/*    */   public IntegerStack(IntegerStack stack) {
/* 42 */     this.pos = stack.pos;
/* 43 */     this.grow = stack.grow;
/* 44 */     this.size = this.pos;
/* 45 */     if (this.pos > 0) {
/* 46 */       this.stack = new int[this.pos];
/* 47 */       System.arraycopy(stack.stack, 0, this.stack, 0, this.pos);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void push(int value) {
/* 52 */     if (this.pos + 1 > this.size) grow(); 
/* 53 */     this.stack[this.pos++] = value;
/*    */   }
/*    */   
/*    */   public int pop() {
/* 57 */     int res = this.stack[--this.pos];
/* 58 */     if (this.pos < this.size - this.grow) shrink(); 
/* 59 */     return res;
/*    */   }
/*    */   
/*    */   public boolean isEmpty() {
/* 63 */     return (this.pos == 0);
/*    */   }
/*    */   
/*    */   public int getTop() {
/* 67 */     return this.stack[this.pos - 1];
/*    */   }
/*    */   
/*    */   public int getElementAt(int pos) {
/* 71 */     return this.stack[pos];
/*    */   }
/*    */   
/*    */   public void clear() {
/* 75 */     this.size = 0;
/* 76 */     this.stack = null;
/*    */   }
/*    */   
/*    */   public int getSize() {
/* 80 */     return this.pos;
/*    */   }
/*    */   
/*    */   private void grow() {
/* 84 */     int[] newStack = new int[this.size + this.grow];
/* 85 */     for (int ctr = 0; ctr < this.size; ctr++)
/* 86 */       newStack[ctr] = this.stack[ctr]; 
/* 87 */     this.stack = newStack;
/* 88 */     this.size += this.grow;
/*    */   }
/*    */   
/*    */   private void shrink() {
/* 92 */     this.size -= this.grow;
/* 93 */     int[] newStack = new int[this.size];
/* 94 */     for (int ctr = 0; ctr < this.size; ctr++)
/* 95 */       newStack[ctr] = this.stack[ctr]; 
/* 96 */     this.stack = newStack;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\IntegerStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */