/*     */ package clus.model.processor;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import jeans.util.MyArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ModelProcessorCollection
/*     */   extends MyArray
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   
/*     */   public final void addModelProcessor(ClusModelProcessor proc) {
/*  41 */     addElement(proc);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean addCheckModelProcessor(ClusModelProcessor proc) {
/*  46 */     for (int j = 0; j < size(); j++) {
/*  47 */       ClusModelProcessor proc2 = (ClusModelProcessor)elementAt(j);
/*  48 */       if (proc == proc2) {
/*  49 */         return false;
/*     */       }
/*     */     } 
/*  52 */     addElement(proc);
/*  53 */     return true;
/*     */   }
/*     */   
/*     */   public final void initialize(ClusModel model, ClusSchema schema) throws IOException, ClusException {
/*  57 */     if (model != null) {
/*  58 */       for (int i = 0; i < size(); i++) {
/*  59 */         ClusModelProcessor proc = (ClusModelProcessor)elementAt(i);
/*  60 */         proc.initialize(model, schema);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public final void initializeAll(ClusSchema schema) throws IOException, ClusException {
/*  66 */     for (int i = 0; i < size(); i++) {
/*  67 */       ClusModelProcessor proc = (ClusModelProcessor)elementAt(i);
/*  68 */       proc.initializeAll(schema);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final void terminate(ClusModel model) throws IOException {
/*  74 */     if (model != null) {
/*  75 */       for (int i = 0; i < size(); i++) {
/*  76 */         ClusModelProcessor proc = (ClusModelProcessor)elementAt(i);
/*  77 */         proc.terminate(model);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public final void terminateAll() throws IOException {
/*  83 */     for (int i = 0; i < size(); i++) {
/*  84 */       ClusModelProcessor proc = (ClusModelProcessor)elementAt(i);
/*  85 */       proc.terminateAll();
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void modelDone() throws IOException {
/*  90 */     for (int j = 0; j < size(); j++) {
/*  91 */       ClusModelProcessor proc = (ClusModelProcessor)elementAt(j);
/*  92 */       proc.modelDone();
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void exampleUpdate(DataTuple tuple) throws IOException {
/*  97 */     for (int j = 0; j < size(); j++) {
/*  98 */       ClusModelProcessor proc = (ClusModelProcessor)elementAt(j);
/*  99 */       proc.exampleUpdate(tuple);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void exampleDone() throws IOException {
/* 104 */     for (int j = 0; j < size(); j++) {
/* 105 */       ClusModelProcessor proc = (ClusModelProcessor)elementAt(j);
/* 106 */       proc.exampleDone();
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void exampleUpdate(DataTuple tuple, ClusStatistic distr) throws IOException {
/* 111 */     for (int j = 0; j < size(); j++) {
/* 112 */       ClusModelProcessor proc = (ClusModelProcessor)elementAt(j);
/* 113 */       proc.exampleUpdate(tuple, distr);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final boolean needsModelUpdate() throws IOException {
/* 118 */     for (int j = 0; j < size(); j++) {
/* 119 */       ClusModelProcessor proc = (ClusModelProcessor)elementAt(j);
/* 120 */       if (proc.needsModelUpdate()) {
/* 121 */         return true;
/*     */       }
/*     */     } 
/* 124 */     return false;
/*     */   }
/*     */   
/*     */   public final ClusModelProcessor getModelProcessor(int i) {
/* 128 */     return (ClusModelProcessor)elementAt(i);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\processor\ModelProcessorCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */