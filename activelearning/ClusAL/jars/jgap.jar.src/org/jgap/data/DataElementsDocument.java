/*    */ package org.jgap.data;
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
/*    */ public class DataElementsDocument
/*    */   implements IDataCreators
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*    */   private IDataElementList m_tree;
/*    */   
/*    */   public void setTree(IDataElementList a_tree) {
/* 28 */     this.m_tree = a_tree;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IDataElementList getTree() {
/* 38 */     return this.m_tree;
/*    */   }
/*    */ 
/*    */   
/*    */   public DataElementsDocument() throws Exception {
/* 43 */     this.m_tree = new DataElementList();
/*    */   }
/*    */ 
/*    */   
/*    */   public IDataCreators newDocument() throws Exception {
/* 48 */     return new DataElementsDocument();
/*    */   }
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
/*    */   public void appendChild(IDataElement a_newChild) throws Exception {
/* 61 */     this.m_tree.add(a_newChild);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\DataElementsDocument.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */