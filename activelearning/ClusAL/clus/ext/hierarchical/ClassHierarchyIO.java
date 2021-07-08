/*    */ package clus.ext.hierarchical;
/*    */ 
/*    */ import clus.util.ClusException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStreamWriter;
/*    */ import java.io.PrintWriter;
/*    */ import jeans.tree.CompleteTreeIterator;
/*    */ import jeans.util.MStreamTokenizer;
/*    */ import jeans.util.array.StringTable;
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
/*    */ public class ClassHierarchyIO
/*    */ {
/* 35 */   protected StringTable m_Table = new StringTable();
/*    */   
/*    */   public ClassHierarchy loadHierarchy(String fname) throws ClusException, IOException {
/* 38 */     ClassHierarchy hier = new ClassHierarchy((ClassTerm)null);
/* 39 */     loadHierarchy(fname, hier);
/* 40 */     return hier;
/*    */   }
/*    */   
/*    */   public ClassHierarchy loadHierarchy(String fname, ClassesAttrType type) throws ClusException, IOException {
/* 44 */     ClassHierarchy hier = new ClassHierarchy(type);
/* 45 */     loadHierarchy(fname, hier);
/* 46 */     return hier;
/*    */   }
/*    */   
/*    */   public void loadHierarchy(String fname, ClassHierarchy hier) throws ClusException, IOException {
/* 50 */     MStreamTokenizer tokens = new MStreamTokenizer(fname);
/* 51 */     String token = tokens.getToken();
/* 52 */     while (token != null) {
/* 53 */       ClassesTuple tuple = new ClassesTuple(token, this.m_Table);
/* 54 */       tuple.addToHierarchy(hier);
/* 55 */       token = tokens.getToken();
/*    */     } 
/*    */     
/* 58 */     tokens.close();
/*    */   }
/*    */   
/*    */   public void saveHierarchy(String fname, ClassHierarchy hier) throws IOException {
/* 62 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
/* 63 */     CompleteTreeIterator iter = hier.getNoRootIter();
/* 64 */     while (iter.hasMoreNodes()) {
/* 65 */       ClassTerm node = (ClassTerm)iter.getNextNode();
/* 66 */       wrt.println(node.toString());
/*    */     } 
/* 68 */     wrt.close();
/*    */   }
/*    */   
/*    */   public StringTable getStringTable() {
/* 72 */     return this.m_Table;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\hierarchical\ClassHierarchyIO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */