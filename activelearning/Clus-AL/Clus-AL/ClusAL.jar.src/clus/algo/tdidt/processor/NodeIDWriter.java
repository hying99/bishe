/*    */ package clus.algo.tdidt.processor;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.main.Settings;
/*    */ import clus.model.ClusModel;
/*    */ import clus.model.processor.ClusModelProcessor;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
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
/*    */ 
/*    */ 
/*    */ public class NodeIDWriter
/*    */   extends ClusModelProcessor
/*    */ {
/*    */   protected boolean m_Missing;
/*    */   protected String m_Fname;
/*    */   protected PrintWriter m_Writer;
/*    */   protected ClusSchema m_Schema;
/*    */   protected MyArray m_Attrs;
/*    */   protected boolean m_First;
/*    */   protected Settings m_Sett;
/*    */   
/*    */   public NodeIDWriter(String fname, boolean missing, Settings sett) {
/* 47 */     this.m_Fname = fname;
/* 48 */     this.m_Missing = missing;
/* 49 */     this.m_Sett = sett;
/*    */   }
/*    */   
/*    */   public void initialize(ClusModel model, ClusSchema schema) throws IOException {
/* 53 */     this.m_Attrs = new MyArray();
/* 54 */     int nb = schema.getNbAttributes(); int i;
/* 55 */     for (i = 0; i < nb; i++) {
/* 56 */       ClusAttrType at = schema.getAttrType(i);
/* 57 */       if (at.getStatus() == 4) this.m_Attrs.addElement(at); 
/*    */     } 
/* 59 */     if (this.m_Attrs.size() == 0)
/* 60 */       for (i = 0; i < nb; i++) {
/* 61 */         ClusAttrType at = schema.getAttrType(i);
/* 62 */         if (at.getStatus() == 1) this.m_Attrs.addElement(at);
/*    */       
/*    */       }  
/* 65 */     this.m_First = true;
/* 66 */     this.m_Writer = this.m_Sett.getFileAbsoluteWriter(this.m_Fname);
/*    */   }
/*    */   
/*    */   public void terminate(ClusModel model) throws IOException {
/* 70 */     this.m_Writer.close();
/*    */   }
/*    */   
/*    */   public boolean needsModelUpdate() {
/* 74 */     return true;
/*    */   }
/*    */   
/*    */   public void modelUpdate(DataTuple tuple, ClusModel model) {
/* 78 */     ClusNode node = (ClusNode)model;
/* 79 */     if (this.m_First) {
/* 80 */       this.m_Writer.print("pred(");
/* 81 */       for (int j = 0; j < this.m_Attrs.size(); j++) {
/* 82 */         ClusAttrType at = (ClusAttrType)this.m_Attrs.elementAt(j);
/* 83 */         this.m_Writer.print(at.getString(tuple));
/*    */       } 
/* 85 */       this.m_First = false;
/*    */     } 
/* 87 */     this.m_Writer.print(",");
/* 88 */     if (this.m_Missing) {
/* 89 */       this.m_Writer.print("(" + tuple.getWeight() + "," + node.getID() + ")");
/*    */     } else {
/* 91 */       this.m_Writer.print(node.getID());
/*    */     } 
/*    */   }
/*    */   
/*    */   public void modelDone() {
/* 96 */     this.m_Writer.println(").");
/* 97 */     this.m_First = true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\tdidt\processor\NodeIDWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */