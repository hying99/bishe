/*    */ package clus.ext.constraint;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.model.modelio.ClusTreeReader;
/*    */ import java.io.IOException;
/*    */ import java.util.HashMap;
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
/*    */ public class ClusConstraintFile
/*    */ {
/*    */   public static ClusConstraintFile m_Instance;
/* 38 */   HashMap m_Constraints = new HashMap<>();
/*    */   
/*    */   public static ClusConstraintFile getInstance() {
/* 41 */     if (m_Instance == null) m_Instance = new ClusConstraintFile(); 
/* 42 */     return m_Instance;
/*    */   }
/*    */   
/*    */   public ClusNode get(String fname) {
/* 46 */     return (ClusNode)this.m_Constraints.get(fname);
/*    */   }
/*    */   
/*    */   public ClusNode getClone(String fname) {
/* 50 */     return (ClusNode)get(fname).cloneTree();
/*    */   }
/*    */   
/*    */   public void load(String fname, ClusSchema schema) throws IOException {
/* 54 */     ClusTreeReader rdr = new ClusTreeReader();
/* 55 */     ClusNode root = rdr.loadTree(fname, schema);
/* 56 */     System.out.println("Constraint: ");
/* 57 */     root.printTree();
/* 58 */     this.m_Constraints.put(fname, root);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\constraint\ClusConstraintFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */