/*    */ package clus.main;
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
/*    */ public class Global
/*    */ {
/*    */   public static int itemsetcpt;
/*    */   public static int treecpt;
/*    */   
/*    */   public static int get_itemsetcpt() {
/* 30 */     return itemsetcpt;
/*    */   }
/*    */ 
/*    */   
/*    */   public static void set_itemsetcpt(int i) {
/* 35 */     itemsetcpt = i;
/*    */   }
/*    */   
/*    */   public static void inc_itemsetcpt() {
/* 39 */     int i = get_itemsetcpt();
/*    */     
/* 41 */     i++;
/* 42 */     set_itemsetcpt(i);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static int get_treecpt() {
/* 48 */     return treecpt;
/*    */   }
/*    */ 
/*    */   
/*    */   public static void set_treecpt(int i) {
/* 53 */     treecpt = i;
/*    */   }
/*    */   
/*    */   public static void inc_treecpt() {
/* 57 */     int i = get_treecpt();
/*    */     
/* 59 */     i++;
/* 60 */     set_treecpt(i);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\main\Global.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */