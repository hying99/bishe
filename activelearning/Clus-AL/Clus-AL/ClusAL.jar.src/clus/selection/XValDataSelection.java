/*    */ package clus.selection;
/*    */ 
/*    */ import clus.data.type.IndexAttrType;
/*    */ import clus.util.ClusException;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.LineNumberReader;
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
/*    */ public class XValDataSelection
/*    */   extends XValMainSelection
/*    */ {
/*    */   protected IndexAttrType m_Attr;
/*    */   
/*    */   public XValDataSelection(IndexAttrType type) {
/* 35 */     super(type.getMaxValue(), type.getNbRows());
/* 36 */     this.m_Attr = type;
/*    */   }
/*    */   
/*    */   public int getFold(int row) {
/* 40 */     return this.m_Attr.getValue(row) - 1;
/*    */   }
/*    */   
/*    */   public static XValDataSelection readFoldsFile(String fname, int nbrows) throws IOException, ClusException {
/* 44 */     IndexAttrType attr = new IndexAttrType("XVAL");
/* 45 */     attr.setNbRows(nbrows);
/* 46 */     for (int i = 0; i < nbrows; i++) {
/* 47 */       attr.setValue(i, -1);
/*    */     }
/* 49 */     int fold = 0;
/* 50 */     LineNumberReader rdr = new LineNumberReader(new InputStreamReader(new FileInputStream(fname)));
/* 51 */     String line = rdr.readLine();
/* 52 */     while (line != null) {
/* 53 */       line = line.trim();
/* 54 */       if (!line.equals("")) {
/* 55 */         fold++;
/* 56 */         String[] tokens = line.split("[\\,\\s]+");
/* 57 */         for (int k = 0; k < tokens.length; k++) {
/*    */           try {
/* 59 */             int exid = Integer.parseInt(tokens[k]);
/* 60 */             if (attr.getValue(exid) != -1) {
/* 61 */               throw new ClusException("Example id " + exid + " occurs twice in folds file: " + fname);
/*    */             }
/* 63 */             attr.setValue(exid, fold);
/*    */           }
/* 65 */           catch (NumberFormatException e) {
/* 66 */             throw new ClusException("Illegal number: " + tokens[k] + " in folds file: " + fname);
/*    */           } 
/*    */         } 
/*    */       } 
/* 70 */       line = rdr.readLine();
/*    */     } 
/* 72 */     for (int j = 0; j < nbrows; j++) {
/* 73 */       if (attr.getValue(j) == -1) {
/* 74 */         throw new ClusException("Folds file does not define fold for example " + (j + 1) + ": " + fname);
/*    */       }
/*    */     } 
/* 77 */     return new XValDataSelection(attr);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\selection\XValDataSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */