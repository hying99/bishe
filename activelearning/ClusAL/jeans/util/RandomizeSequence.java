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
/*    */ public class RandomizeSequence
/*    */ {
/*    */   private boolean[] sequence;
/*    */   private int cr_nr;
/*    */   private int total;
/*    */   
/*    */   public RandomizeSequence(int mytotal) {
/* 70 */     this.total = mytotal; this.cr_nr = 0;
/* 71 */     this.sequence = new boolean[mytotal];
/*    */   }
/*    */   
/*    */   public int next() {
/* 75 */     int idx = random(this.total - this.cr_nr);
/* 76 */     int i = 0;
/* 77 */     while (this.sequence[i] || idx > 0) {
/* 78 */       if (!this.sequence[i]) idx--; 
/* 79 */       i++;
/*    */     } 
/* 81 */     this.sequence[i] = true;
/* 82 */     this.cr_nr++;
/* 83 */     return i;
/*    */   }
/*    */   
/*    */   public boolean isEmpty() {
/* 87 */     return (this.cr_nr >= this.total);
/*    */   }
/*    */   
/*    */   public static int random(int max) {
/* 91 */     return (int)Math.floor(max * Math.random());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\RandomizeSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */