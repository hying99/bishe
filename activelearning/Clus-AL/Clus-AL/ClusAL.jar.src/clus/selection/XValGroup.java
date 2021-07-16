/*    */ package clus.selection;
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
/*    */ public class XValGroup
/*    */ {
/*    */   protected int[] m_Elements;
/*    */   protected int m_NbElements;
/*    */   
/*    */   public XValGroup(int max) {
/* 31 */     this.m_Elements = new int[max];
/*    */   }
/*    */   
/*    */   public boolean add(int which, int max) {
/* 35 */     if (this.m_NbElements >= max) return false; 
/* 36 */     this.m_Elements[this.m_NbElements++] = which;
/* 37 */     return true;
/*    */   }
/*    */   
/*    */   public int getNbElements() {
/* 41 */     return this.m_NbElements;
/*    */   }
/*    */   
/*    */   public int getElement(int idx) {
/* 45 */     return this.m_Elements[idx];
/*    */   }
/*    */   
/*    */   public void print() {
/* 49 */     System.out.print("[");
/* 50 */     for (int i = 0; i < getNbElements(); i++) {
/* 51 */       if (i != 0) System.out.print(","); 
/* 52 */       System.out.print(getElement(i));
/*    */     } 
/* 54 */     System.out.println("]");
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\selection\XValGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */