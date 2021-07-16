/*    */ package clus.algo.tdidt.processor;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.data.type.StringAttrType;
/*    */ import clus.main.Settings;
/*    */ import clus.model.ClusModel;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ import jeans.tree.LeafTreeIterator;
/*    */ import jeans.tree.Node;
/*    */ import jeans.util.MyArray;
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
/*    */ public class NodeExampleCollector
/*    */   extends BasicExampleCollector
/*    */ {
/*    */   protected String m_FName;
/*    */   protected MyArray m_Attrs;
/*    */   protected boolean m_Missing;
/*    */   protected Settings m_Sett;
/*    */   
/*    */   public NodeExampleCollector(String fname, boolean missing, Settings sett) {
/* 44 */     this.m_FName = fname;
/* 45 */     this.m_Missing = missing;
/* 46 */     this.m_Sett = sett;
/*    */   }
/*    */   
/*    */   public void initialize(ClusModel model, ClusSchema schema) {
/* 50 */     this.m_Attrs = new MyArray();
/* 51 */     int nb = schema.getNbAttributes(); int i;
/* 52 */     for (i = 0; i < nb; i++) {
/* 53 */       ClusAttrType at = schema.getAttrType(i);
/* 54 */       if (at.getStatus() == 4) this.m_Attrs.addElement(at); 
/*    */     } 
/* 56 */     if (this.m_Attrs.size() == 0)
/* 57 */       for (i = 0; i < nb; i++) {
/* 58 */         ClusAttrType at = schema.getAttrType(i);
/* 59 */         if (at.getStatus() == 1) this.m_Attrs.addElement(at);
/*    */       
/*    */       }  
/* 62 */     super.initialize(model, schema);
/*    */   }
/*    */   
/*    */   public void terminate(ClusModel model) throws IOException {
/* 66 */     ClusNode root = (ClusNode)model;
/* 67 */     writeFile(root);
/* 68 */     root.clearVisitors();
/*    */   }
/*    */   
/*    */   public final void writeFile(ClusNode root) throws IOException {
/* 72 */     PrintWriter wrt = this.m_Sett.getFileAbsoluteWriter(this.m_FName);
/* 73 */     LeafTreeIterator iter = new LeafTreeIterator((Node)root);
/* 74 */     while (iter.hasMoreNodes()) {
/* 75 */       ClusNode node = (ClusNode)iter.getNextNode();
/* 76 */       MyArray visitor = (MyArray)node.getVisitor();
/* 77 */       wrt.print("leaf(" + node.getID() + ",[");
/* 78 */       for (int i = 0; i < visitor.size(); i++) {
/* 79 */         DataTuple tuple = (DataTuple)visitor.elementAt(i);
/* 80 */         if (i > 0) wrt.print(","); 
/* 81 */         if (this.m_Missing) wrt.print("(" + tuple.getWeight() + ","); 
/* 82 */         int attrsize = this.m_Attrs.size();
/* 83 */         if (attrsize > 1) wrt.print("["); 
/* 84 */         for (int j = 0; j < this.m_Attrs.size(); j++) {
/*    */           
/*    */           try {
/* 87 */             StringAttrType at = (StringAttrType)this.m_Attrs.elementAt(j);
/* 88 */             wrt.print(at.getString(tuple));
/*    */           }
/* 90 */           catch (ClassCastException classCastException) {}
/*    */         } 
/*    */ 
/*    */         
/* 94 */         if (this.m_Attrs.size() > 1) wrt.print("]"); 
/* 95 */         if (this.m_Missing) wrt.print(")"); 
/*    */       } 
/* 97 */       wrt.println("]).");
/*    */     } 
/* 99 */     wrt.close();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\tdidt\processor\NodeExampleCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */