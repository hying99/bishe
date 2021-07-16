/*    */ package clus.selection;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.ClusSchema;
/*    */ 
/*    */ public class CriterionBasedSelection {
/*    */   public static final boolean isMissing(DataTuple tuple, ClusAttrType[] attrs) {
/*  9 */     for (int i = 0; i < attrs.length; i++) {
/* 10 */       if (attrs[i].isMissing(tuple)) return true; 
/*    */     } 
/* 12 */     return false;
/*    */   }
/*    */   
/*    */   public static final void clearMissingFlagTargetAttrs(ClusSchema schema) {
/* 16 */     ClusAttrType[] targets = schema.getAllAttrUse(3);
/* 17 */     for (int i = 0; i < targets.length; i++) {
/* 18 */       targets[i].setNbMissing(0);
/*    */     }
/*    */   }
/*    */   
/*    */   public static final RowData removeMissingTarget(RowData data) {
/* 23 */     int nbrows = data.getNbRows();
/* 24 */     ClusAttrType[] targets = data.getSchema().getAllAttrUse(3);
/* 25 */     BitMapSelection sel = new BitMapSelection(nbrows);
/* 26 */     for (int i = 0; i < nbrows; i++) {
/* 27 */       DataTuple tuple = data.getTuple(i);
/* 28 */       if (!isMissing(tuple, targets)) {
/* 29 */         sel.select(i);
/*    */       }
/*    */     } 
/* 32 */     if (sel.getNbSelected() != nbrows) {
/* 33 */       System.out.println("Tuples with missing target: " + (nbrows - sel
/* 34 */           .getNbSelected()));
/* 35 */       return (RowData)data.selectFrom(sel);
/*    */     } 
/* 37 */     return data;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\selection\CriterionBasedSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */