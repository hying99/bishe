/*    */ package jeans.resource;
/*    */ 
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.ItemListener;
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
/*    */ public class ListenersTable
/*    */ {
/* 30 */   protected static ListenersTable instance = null;
/* 31 */   protected Hashtable listeners = new Hashtable<>();
/*    */   
/*    */   public static ListenersTable getInstance() {
/* 34 */     if (instance == null)
/* 35 */       instance = new ListenersTable(); 
/* 36 */     return instance;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void addActionListener(String key, ActionListener listener) {
/* 43 */     this.listeners.put(key, listener);
/*    */   }
/*    */   
/*    */   public ActionListener getActionListener(String key) {
/* 47 */     return (ActionListener)this.listeners.get(key);
/*    */   }
/*    */   
/*    */   public void addItemListener(String key, ItemListener listener) {
/* 51 */     this.listeners.put(key, listener);
/*    */   }
/*    */   
/*    */   public ItemListener getItemListener(String key) {
/* 55 */     return (ItemListener)this.listeners.get(key);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\resource\ListenersTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */