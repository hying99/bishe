/*    */ package jeans.util.array;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.Hashtable;
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
/*    */ public class StringTable
/*    */ {
/* 29 */   protected Hashtable table = new Hashtable<>();
/*    */   
/*    */   public String get(String strg) {
/* 32 */     String found = (String)this.table.get(strg);
/* 33 */     if (found == null) {
/* 34 */       this.table.put(strg, strg);
/* 35 */       found = strg;
/*    */     } 
/* 37 */     return found;
/*    */   }
/*    */   
/*    */   public void print() {
/* 41 */     for (Enumeration<String> e = this.table.elements(); e.hasMoreElements(); ) {
/* 42 */       String str = e.nextElement();
/* 43 */       System.out.println(str);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\array\StringTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */