/*     */ package clus.model.processor;
/*     */ 
/*     */ import clus.data.io.ARFFFile;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.RegressionStatBase;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.ArrayList;
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
/*     */ public class ClusEnsemblePredictionWriter
/*     */   extends ClusModelProcessor
/*     */ {
/*     */   protected ClusSchema m_EnsPredSchema;
/*     */   protected Settings m_Sett;
/*     */   protected String m_Fname;
/*     */   protected MyArray m_Attrs;
/*     */   protected PrintWriter m_Writer;
/*     */   protected ArrayList m_AttributeNames;
/*     */   protected int m_NbTargetAttributes;
/*     */   static int m_Type;
/*     */   protected double[] m_StDev;
/*  39 */   static ArrayList m_Votes = new ArrayList();
/*  40 */   protected NumberFormat m_Format = ClusFormat.FOUR_AFTER_DOT;
/*     */   boolean m_Initialized = false;
/*     */   
/*     */   public ClusEnsemblePredictionWriter(String fname) {
/*  44 */     this.m_Fname = fname;
/*  45 */     this.m_AttributeNames = new ArrayList();
/*     */   }
/*     */   
/*     */   public ClusEnsemblePredictionWriter(String fname, ClusSchema schema, Settings sett) {
/*  49 */     this.m_Fname = fname;
/*  50 */     this.m_AttributeNames = new ArrayList();
/*  51 */     this.m_NbTargetAttributes = schema.getNbTargetAttributes();
/*  52 */     this.m_Sett = sett;
/*  53 */     this.m_Initialized = true;
/*     */     try {
/*  55 */       doInitialize(schema);
/*  56 */       ARFFFile.writeArffHeader(this.m_Writer, this.m_EnsPredSchema);
/*  57 */       this.m_Writer.flush();
/*  58 */       addEnsPredHeader(schema);
/*  59 */       this.m_Writer.flush();
/*  60 */     } catch (Exception e) {
/*  61 */       System.err.println("Error while writing ensemble prediction header!");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void addEnsPredHeader(ClusSchema schema) throws ClusException {
/*  68 */     int nb = schema.getNbAttributes();
/*  69 */     for (int i = 0; i < nb; i++) {
/*  70 */       ClusAttrType at = schema.getAttrType(i);
/*  71 */       if (at.getStatus() == 1) {
/*  72 */         this.m_Writer.print("@ATTRIBUTE ");
/*  73 */         this.m_Writer.print(StringUtils.printStr(at.getName() + "-pred", 65));
/*  74 */         at.writeARFFType(this.m_Writer);
/*  75 */         this.m_Writer.println();
/*     */       } 
/*     */     } 
/*     */     
/*  79 */     for (int j = 0; j < nb; j++) {
/*  80 */       ClusAttrType at = schema.getAttrType(j);
/*  81 */       if (at.getStatus() == 1) {
/*  82 */         this.m_Writer.print("@ATTRIBUTE ");
/*  83 */         this.m_Writer.print(StringUtils.printStr(at.getName() + "-stdev", 65));
/*  84 */         at.writeARFFType(this.m_Writer);
/*  85 */         this.m_Writer.println();
/*     */       } 
/*     */     } 
/*  88 */     this.m_Writer.println();
/*  89 */     this.m_Writer.print("@DATA");
/*  90 */     this.m_Writer.println();
/*     */   }
/*     */   
/*     */   private void doInitialize(ClusSchema schema) throws IOException, ClusException {
/*  94 */     this.m_Attrs = new MyArray();
/*  95 */     int nb = schema.getNbAttributes();
/*  96 */     this.m_EnsPredSchema = new ClusSchema(StringUtils.removeSingleQuote(schema.getRelationName()) + "-pred-distr");
/*  97 */     this.m_EnsPredSchema.setSettings(schema.getSettings());
/*  98 */     for (int i = 0; i < nb; i++) {
/*  99 */       ClusAttrType at = schema.getAttrType(i);
/* 100 */       if (at.getStatus() == 4) {
/* 101 */         this.m_Attrs.addElement(at);
/* 102 */         this.m_AttributeNames.add(at.getName());
/* 103 */         this.m_EnsPredSchema.addAttrType(at.cloneType());
/*     */       } 
/*     */     } 
/* 106 */     for (int j = 0; j < nb; j++) {
/* 107 */       ClusAttrType at = schema.getAttrType(j);
/* 108 */       if (at.getStatus() == 1) {
/* 109 */         this.m_Attrs.addElement(at);
/* 110 */         this.m_AttributeNames.add(at.getName());
/* 111 */         this.m_EnsPredSchema.addAttrType(at.cloneType());
/*     */       } 
/*     */     } 
/* 114 */     this.m_Writer = this.m_Sett.getFileAbsoluteWriter(this.m_Fname);
/*     */   }
/*     */   
/*     */   public void writePredictionsForTuple(DataTuple tuple, ClusStatistic distr) {
/* 118 */     this.m_Writer.print(getRealValues(tuple) + ",");
/* 119 */     this.m_Writer.print(getPrediction(distr) + ",");
/* 120 */     String[] voting = processVotes(m_Votes);
/* 121 */     this.m_Writer.print(getPredictionStDev() + "\n");
/* 122 */     this.m_Writer.flush();
/* 123 */     for (int i = 0; i < this.m_NbTargetAttributes; i++) {
/* 124 */       this.m_Writer.print("% Target = " + this.m_AttributeNames.get(i) + ": " + voting[i] + "\n");
/* 125 */       this.m_Writer.flush();
/*     */     } 
/*     */   }
/*     */   
/*     */   private String getRealValues(DataTuple tuple) {
/* 130 */     String real = "";
/* 131 */     for (int j = 0; j < this.m_Attrs.size(); j++) {
/* 132 */       if (j != 0) real = real + ","; 
/* 133 */       ClusAttrType at = (ClusAttrType)this.m_Attrs.elementAt(j);
/* 134 */       real = real + at.getPredictionWriterString(tuple);
/*     */     } 
/* 136 */     return real;
/*     */   }
/*     */   
/*     */   private String getPrediction(ClusStatistic distr) {
/* 140 */     String result = "";
/* 141 */     double[] pred = distr.getNumericPred();
/* 142 */     for (int i = 0; i < pred.length; i++) {
/* 143 */       if (i != 0) result = result + ","; 
/* 144 */       if (pred != null) {
/* 145 */         result = result + this.m_Format.format(pred[i]);
/*     */       } else {
/* 147 */         result = result + "?";
/*     */       } 
/*     */     } 
/* 150 */     return result;
/*     */   }
/*     */   
/*     */   private String getPredictionStDev() {
/* 154 */     String result = "";
/* 155 */     for (int i = 0; i < this.m_StDev.length; i++) {
/* 156 */       if (i != 0) result = result + ","; 
/* 157 */       result = result + this.m_Format.format(this.m_StDev[i]);
/*     */     } 
/* 159 */     return result;
/*     */   }
/*     */   
/*     */   private String[] processVotes(ArrayList<RegressionStatBase> votes) {
/* 163 */     String[] result = new String[this.m_NbTargetAttributes];
/* 164 */     this.m_StDev = new double[this.m_NbTargetAttributes];
/* 165 */     double[][] predicts = new double[this.m_NbTargetAttributes][votes.size()];
/*     */     
/* 167 */     for (int i = 0; i < votes.size(); i++) {
/* 168 */       RegressionStatBase stat = votes.get(i);
/* 169 */       if (i == 0) {
/* 170 */         for (int j = 0; j < stat.getNbAttributes(); j++) {
/* 171 */           predicts[j][i] = stat.getMean(j);
/* 172 */           result[j] = "" + this.m_Format.format(predicts[j][i]);
/*     */         } 
/*     */       } else {
/* 175 */         for (int j = 0; j < stat.getNbAttributes(); j++) {
/* 176 */           predicts[j][i] = stat.getMean(j);
/* 177 */           result[j] = result[j] + "," + this.m_Format.format(predicts[j][i]);
/*     */         } 
/*     */       } 
/*     */     } 
/* 181 */     this.m_StDev = calcStDev(predicts);
/* 182 */     return result;
/*     */   }
/*     */   
/*     */   private double[] calcStDev(double[][] values) {
/* 186 */     double[] result = new double[values.length];
/* 187 */     for (int i = 0; i < result.length; i++)
/* 188 */       result[i] = stDevOpt(values[i]); 
/* 189 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   private double stDevOpt(double[] a) {
/* 194 */     double avg = 0.0D;
/* 195 */     double summ = 0.0D;
/* 196 */     for (int i = 0; i < a.length; i++) {
/* 197 */       avg += a[i];
/* 198 */       summ += a[i] * a[i];
/*     */     } 
/* 200 */     avg /= a.length;
/* 201 */     double sd = (summ - a.length * avg * avg) / (a.length - 1);
/* 202 */     return Math.sqrt(sd);
/*     */   }
/*     */   
/*     */   public static int getType() {
/* 206 */     return m_Type;
/*     */   }
/*     */   
/*     */   public static void setType(int type) {
/* 210 */     m_Type = type;
/*     */   }
/*     */   
/*     */   public void closeWriter() {
/* 214 */     System.out.println("Ensemble predictions written in " + this.m_Fname);
/* 215 */     this.m_Writer.flush();
/* 216 */     this.m_Writer.close();
/*     */   }
/*     */   
/*     */   public static void setVotes(ArrayList votes) {
/* 220 */     m_Votes = votes;
/*     */   }
/*     */   
/*     */   public void printVotes() {
/* 224 */     System.out.println("Votes: " + m_Votes);
/*     */   }
/*     */   
/*     */   public boolean isInitialized() {
/* 228 */     return this.m_Initialized;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\processor\ClusEnsemblePredictionWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */