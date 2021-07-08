/*    */ package clus.gui;
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
/*    */ public class ClusFileTreeElem
/*    */ {
/* 30 */   protected int m_Type = -1;
/*    */   
/*    */   protected String m_SName;
/*    */   
/*    */   public ClusFileTreeElem(String sname, String lname) {
/* 35 */     this.m_SName = sname;
/* 36 */     this.m_LName = lname;
/*    */   }
/*    */   protected String m_LName; protected Object m_Obj1;
/*    */   public String getFullName() {
/* 40 */     return this.m_LName;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 44 */     return this.m_SName;
/*    */   }
/*    */   
/*    */   public int getType() {
/* 48 */     return this.m_Type;
/*    */   }
/*    */   
/*    */   public void setType(int type) {
/* 52 */     this.m_Type = type;
/*    */   }
/*    */   
/*    */   public Object getObject1() {
/* 56 */     return this.m_Obj1;
/*    */   }
/*    */   
/*    */   public void setObject1(Object obj) {
/* 60 */     this.m_Obj1 = obj;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\gui\ClusFileTreeElem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */