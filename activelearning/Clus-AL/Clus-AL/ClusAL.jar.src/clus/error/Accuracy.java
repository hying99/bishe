/*     */ package clus.error;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ public class Accuracy
/*     */   extends ClusNominalError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int[] m_NbCorrect;
/*     */   protected int[] m_NbKnown;
/*     */   
/*     */   public Accuracy(ClusErrorList par, NominalAttrType[] nom) {
/*  42 */     super(par, nom);
/*  43 */     this.m_NbCorrect = new int[this.m_Dim];
/*  44 */     this.m_NbKnown = new int[this.m_Dim];
/*     */   }
/*     */   
/*     */   public boolean shouldBeLow() {
/*  48 */     return false;
/*     */   }
/*     */   
/*     */   public void reset() {
/*  52 */     Arrays.fill(this.m_NbCorrect, 0);
/*  53 */     Arrays.fill(this.m_NbKnown, 0);
/*     */   }
/*     */   
/*     */   public void add(ClusError other) {
/*  57 */     Accuracy acc = (Accuracy)other;
/*  58 */     for (int i = 0; i < this.m_Dim; i++) {
/*  59 */       this.m_NbCorrect[i] = this.m_NbCorrect[i] + acc.m_NbCorrect[i];
/*  60 */       this.m_NbKnown[i] = this.m_NbKnown[i] + acc.m_NbKnown[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void showSummaryError(PrintWriter out, boolean detail) {
/*  65 */     showModelError(out, detail ? 1 : 0);
/*     */   }
/*     */   
/*     */   public double getAccuracy(int i) {
/*  69 */     return getModelErrorComponent(i);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getModelErrorComponent(int i) {
/*  74 */     return this.m_NbCorrect[i] / this.m_NbKnown[i];
/*     */   }
/*     */   
/*     */   public double getModelError() {
/*  78 */     double avg = 0.0D;
/*  79 */     for (int i = 0; i < this.m_Dim; i++) {
/*  80 */       avg += getModelErrorComponent(i);
/*     */     }
/*     */     
/*  83 */     return avg / this.m_Dim;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  87 */     return "Accuracy";
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/*  91 */     return new Accuracy(par, this.m_Attrs);
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/*  95 */     int[] predicted = pred.getNominalPred();
/*  96 */     for (int i = 0; i < this.m_Dim; i++) {
/*  97 */       NominalAttrType attr = getAttr(i);
/*  98 */       if (!attr.isMissing(tuple)) {
/*  99 */         if (attr.getNominal(tuple) == predicted[i]) this.m_NbCorrect[i] = this.m_NbCorrect[i] + 1; 
/* 100 */         this.m_NbKnown[i] = this.m_NbKnown[i] + 1;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple tuple, DataTuple pred) {
/* 106 */     for (int i = 0; i < this.m_Dim; i++) {
/* 107 */       NominalAttrType attr = getAttr(i);
/* 108 */       if (!attr.isMissing(tuple)) {
/* 109 */         if (attr.getNominal(tuple) == attr.getNominal(pred)) this.m_NbCorrect[i] = this.m_NbCorrect[i] + 1; 
/* 110 */         this.m_NbKnown[i] = this.m_NbKnown[i] + 1;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addInvalid(DataTuple tuple) {}
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\Accuracy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */