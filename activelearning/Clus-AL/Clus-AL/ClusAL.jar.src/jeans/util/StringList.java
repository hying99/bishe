/*    */ package jeans.util;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.Vector;
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
/*    */ public class StringList
/*    */ {
/*    */   protected Vector m_strings;
/*    */   
/*    */   public void addLine(String line) {
/* 32 */     this.m_strings.addElement(line);
/*    */   }
/*    */   
/*    */   public Enumeration getLines() {
/* 36 */     return this.m_strings.elements();
/*    */   }
/*    */   
/*    */   public int getSize() {
/* 40 */     return this.m_strings.size();
/*    */   }
/*    */   
/*    */   public String getLine(int idx) {
/* 44 */     return this.m_strings.elementAt(idx);
/*    */   }
/*    */   
/*    */   public void print() {
/* 48 */     for (int ctr = 0; ctr < getSize(); ctr++)
/* 49 */       System.out.println(getLine(ctr)); 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\StringList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */