/*     */ package clus.model.processor;
/*     */ 
/*     */ import clus.data.io.ARFFFile;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.StringAttrType;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import jeans.util.MyArray;
/*     */ import jeans.util.StringUtils;
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
/*     */ public class PredictionWriter
/*     */   extends ClusModelProcessor
/*     */ {
/*     */   protected String m_Fname;
/*     */   protected PrintWriter m_Writer;
/*     */   protected MyArray m_Attrs;
/*     */   protected boolean m_Global;
/*     */   protected Settings m_Sett;
/*     */   protected StringBuffer m_ModelParts;
/*     */   protected ClusSchema m_OutSchema;
/*     */   protected ClusStatistic m_Target;
/*     */   protected boolean m_Initialized;
/*     */   protected String m_ToPrint;
/*  50 */   protected ArrayList m_ModelNames = new ArrayList();
/*  51 */   protected HashSet m_ModelNamesMap = new HashSet();
/*     */   
/*     */   public PredictionWriter(String fname, Settings sett, ClusStatistic target) {
/*  54 */     this.m_Fname = fname;
/*  55 */     this.m_Sett = sett;
/*  56 */     this.m_Target = target;
/*  57 */     this.m_ModelParts = new StringBuffer();
/*     */   }
/*     */   
/*     */   public boolean shouldProcessModel(ClusModelInfo info) {
/*  61 */     return (info.shouldWritePredictions() && !info.getName().equals("Default"));
/*     */   }
/*     */   
/*     */   public void addModelInfo(ClusModelInfo info) {
/*  65 */     if (!this.m_ModelNamesMap.contains(info.getName())) {
/*  66 */       this.m_ModelNamesMap.add(info.getName());
/*  67 */       this.m_ModelNames.add(info.getName());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addTargetAttributesForEachModel() {
/*  72 */     for (int i = 0; i < this.m_ModelNames.size(); i++) {
/*  73 */       String mn = this.m_ModelNames.get(i);
/*  74 */       this.m_Target.addPredictWriterSchema(mn, this.m_OutSchema);
/*  75 */       this.m_OutSchema.addAttrType((ClusAttrType)new StringAttrType(mn + "-models"));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void println(String line) {
/*  80 */     if (this.m_Initialized) {
/*  81 */       this.m_Writer.println(line);
/*     */     } else {
/*  83 */       this.m_ToPrint = line;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void initializeAll(ClusSchema schema) throws IOException, ClusException {
/*  88 */     if (this.m_Initialized) {
/*     */       return;
/*     */     }
/*  91 */     if (!this.m_Global) {
/*  92 */       doInitialize(schema);
/*     */     }
/*  94 */     addTargetAttributesForEachModel();
/*     */     
/*  96 */     ARFFFile.writeArffHeader(this.m_Writer, this.m_OutSchema);
/*  97 */     this.m_Writer.println("@DATA");
/*  98 */     if (this.m_ToPrint != null) {
/*  99 */       this.m_Writer.println(this.m_ToPrint);
/* 100 */       this.m_ToPrint = null;
/*     */     } 
/* 102 */     this.m_Initialized = true;
/*     */   }
/*     */   
/*     */   public void terminateAll() throws IOException {
/* 106 */     if (!this.m_Global) {
/* 107 */       close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void globalInitialize(ClusSchema schema) throws IOException, ClusException {
/* 112 */     this.m_Global = true;
/* 113 */     doInitialize(schema);
/*     */   }
/*     */   
/*     */   public PrintWriter getWrt() {
/* 117 */     return this.m_Writer;
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 121 */     this.m_Writer.close();
/*     */   }
/*     */   
/*     */   public boolean needsModelUpdate() {
/* 125 */     return true;
/*     */   }
/*     */   
/*     */   public void modelUpdate(DataTuple tuple, ClusModel model) throws IOException {
/* 129 */     if (this.m_ModelParts.length() != 0) {
/* 130 */       this.m_ModelParts.append("+");
/*     */     }
/* 132 */     this.m_ModelParts.append(String.valueOf(model.getID()));
/*     */   }
/*     */ 
/*     */   
/*     */   public void exampleUpdate(DataTuple tuple) {
/* 137 */     for (int j = 0; j < this.m_Attrs.size(); j++) {
/* 138 */       if (j != 0) {
/* 139 */         this.m_Writer.print(",");
/*     */       }
/* 141 */       ClusAttrType at = (ClusAttrType)this.m_Attrs.elementAt(j);
/* 142 */       this.m_Writer.print(at.getPredictionWriterString(tuple));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void exampleDone() {
/* 147 */     this.m_Writer.println();
/* 148 */     this.m_ModelParts.setLength(0);
/*     */   }
/*     */   
/*     */   public void exampleUpdate(DataTuple tuple, ClusStatistic distr) {
/* 152 */     this.m_Writer.print(",");
/* 153 */     if (distr == null) {
/* 154 */       this.m_Writer.print("???");
/*     */     } else {
/*     */       
/* 157 */       this.m_Writer.print(distr.getPredictWriterString(tuple));
/*     */     } 
/* 159 */     this.m_Writer.print(",\"" + this.m_ModelParts + "\"");
/* 160 */     this.m_ModelParts.setLength(0);
/*     */   }
/*     */   
/*     */   private void doInitialize(ClusSchema schema) throws IOException, ClusException {
/* 164 */     this.m_Attrs = new MyArray();
/* 165 */     int nb = schema.getNbAttributes();
/* 166 */     this.m_OutSchema = new ClusSchema(StringUtils.removeSingleQuote(schema.getRelationName()) + "-predictions");
/* 167 */     this.m_OutSchema.setSettings(schema.getSettings()); int i;
/* 168 */     for (i = 0; i < nb; i++) {
/* 169 */       ClusAttrType at = schema.getAttrType(i);
/* 170 */       if (at.getStatus() == 4) {
/* 171 */         this.m_Attrs.addElement(at);
/* 172 */         this.m_OutSchema.addAttrType(at.cloneType());
/*     */       } 
/*     */     } 
/* 175 */     for (i = 0; i < nb; i++) {
/* 176 */       ClusAttrType at = schema.getAttrType(i);
/* 177 */       if (at.getStatus() == 1) {
/* 178 */         this.m_Attrs.addElement(at);
/* 179 */         at.updatePredictWriterSchema(this.m_OutSchema);
/*     */       } 
/*     */     } 
/* 182 */     this.m_Writer = this.m_Sett.getFileAbsoluteWriter(this.m_Fname);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\processor\PredictionWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */