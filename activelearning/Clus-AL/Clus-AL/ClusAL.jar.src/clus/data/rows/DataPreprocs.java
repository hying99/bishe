/*    */ package clus.data.rows;
/*    */ 
/*    */ import clus.util.ClusException;
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
/*    */ 
/*    */ public class DataPreprocs
/*    */ {
/* 30 */   protected MyArray m_Preprocs = new MyArray();
/*    */   
/*    */   public void addPreproc(TuplePreproc pp) {
/* 33 */     this.m_Preprocs.addElement(pp);
/*    */   }
/*    */   
/*    */   public int getNbPasses() {
/* 37 */     int passes = 0;
/* 38 */     int nb = this.m_Preprocs.size();
/* 39 */     for (int i = 0; i < nb; i++) {
/* 40 */       TuplePreproc pp = (TuplePreproc)this.m_Preprocs.elementAt(i);
/* 41 */       passes = Math.max(passes, pp.getNbPasses());
/*    */     } 
/* 43 */     return passes;
/*    */   }
/*    */   
/*    */   public void preproc(int pass, DataTuple tuple) throws ClusException {
/* 47 */     int nb = this.m_Preprocs.size();
/* 48 */     for (int i = 0; i < nb; i++) {
/* 49 */       TuplePreproc pp = (TuplePreproc)this.m_Preprocs.elementAt(i);
/* 50 */       if (pass < pp.getNbPasses()) pp.preproc(pass, tuple); 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void preprocSingle(DataTuple tuple) throws ClusException {
/* 55 */     int nb = this.m_Preprocs.size();
/* 56 */     for (int i = 0; i < nb; i++) {
/* 57 */       TuplePreproc pp = (TuplePreproc)this.m_Preprocs.elementAt(i);
/* 58 */       pp.preprocSingle(tuple);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void done(int pass) throws ClusException {
/* 63 */     int nb = this.m_Preprocs.size();
/* 64 */     for (int i = 0; i < nb; i++) {
/* 65 */       TuplePreproc pp = (TuplePreproc)this.m_Preprocs.elementAt(i);
/* 66 */       if (pass < pp.getNbPasses()) pp.done(pass); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\rows\DataPreprocs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */