/*    */ package org.jgap.data;
/*    */ 
/*    */ import java.util.List;
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
/*    */ public class DataElementList
/*    */   implements IDataElementList
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/* 29 */   private List m_list = new Vector();
/*    */ 
/*    */   
/*    */   public IDataElement item(int a_index) {
/* 33 */     return this.m_list.get(a_index);
/*    */   }
/*    */   
/*    */   public int getLength() {
/* 37 */     return this.m_list.size();
/*    */   }
/*    */   
/*    */   public void add(IDataElement a_element) {
/* 41 */     this.m_list.add(a_element);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\DataElementList.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */