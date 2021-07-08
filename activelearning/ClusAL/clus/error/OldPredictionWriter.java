/*     */ package clus.error;
/*     */ 
/*     */ import clus.data.ClusData;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.StringTokenizer;
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
/*     */ public class OldPredictionWriter
/*     */ {
/*     */   protected PrintWriter m_Writer;
/*     */   protected ClusSchema m_Schema;
/*     */   protected MyArray m_Attrs;
/*     */   protected boolean m_Enabled;
/*     */   protected boolean m_MainEnabled;
/*     */   static OldPredictionWriter instance;
/*     */   
/*     */   public static void makeInstance(String fname, ClusSchema schema) {
/*     */     try {
/*  46 */       OldPredictionWriter inst = getInstance();
/*  47 */       inst.init(fname, schema);
/*  48 */     } catch (IOException e) {
/*  49 */       System.out.println(">>> IO ERROR <<<<<");
/*     */     } 
/*     */   }
/*     */   
/*     */   public static OldPredictionWriter getInstance() {
/*  54 */     if (instance == null) instance = new OldPredictionWriter(); 
/*  55 */     return instance;
/*     */   }
/*     */   
/*     */   public void setEnabled(boolean ena) {
/*  59 */     this.m_Enabled = ena;
/*     */   }
/*     */   
/*     */   public void setMainEnabled(boolean ena) {
/*  63 */     this.m_MainEnabled = ena;
/*     */   }
/*     */   
/*     */   public void init(String fname, ClusSchema schema) throws FileNotFoundException {
/*  67 */     this.m_Schema = schema;
/*  68 */     this.m_Writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
/*  69 */     this.m_Attrs = new MyArray();
/*  70 */     int nb = schema.getNbAttributes(); int i;
/*  71 */     for (i = 0; i < nb; i++) {
/*  72 */       ClusAttrType at = schema.getAttrType(i);
/*  73 */       if (at.getStatus() == 4) this.m_Attrs.addElement(at); 
/*     */     } 
/*  75 */     for (i = 0; i < nb; i++) {
/*  76 */       ClusAttrType at = schema.getAttrType(i);
/*  77 */       if (at.getStatus() == 1) this.m_Attrs.addElement(at); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void write(String strg) {
/*  82 */     this.m_Writer.println(strg);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addExample(ClusData data, int idx, ClusStatistic pred) {}
/*     */   
/*     */   public int writeAttributes(DataTuple tuple) {
/*  89 */     int nb = this.m_Attrs.size();
/*  90 */     for (int i = 0; i < nb; i++) {
/*  91 */       if (i != 0) this.m_Writer.print(","); 
/*  92 */       ClusAttrType at = (ClusAttrType)this.m_Attrs.elementAt(i);
/*  93 */       this.m_Writer.print(at.getString(tuple));
/*     */     } 
/*  95 */     return nb;
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple tuple, String pred) {
/*  99 */     if (this.m_Enabled && this.m_MainEnabled) {
/* 100 */       int nb = writeAttributes(tuple);
/* 101 */       if (nb > 0) this.m_Writer.print(","); 
/* 102 */       this.m_Writer.println(pred);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/* 107 */     if (this.m_Enabled && this.m_MainEnabled) {
/* 108 */       writeAttributes(tuple);
/* 109 */       StringTokenizer tokens = new StringTokenizer(pred.getString(), "[],");
/* 110 */       while (tokens.hasMoreTokens()) {
/* 111 */         this.m_Writer.print(",");
/* 112 */         this.m_Writer.print(tokens.nextToken());
/*     */       } 
/* 114 */       this.m_Writer.println();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void close() {
/* 119 */     this.m_Writer.close();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\OldPredictionWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */