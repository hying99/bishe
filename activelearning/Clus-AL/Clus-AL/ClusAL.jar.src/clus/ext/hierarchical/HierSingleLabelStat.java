/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HierSingleLabelStat
/*     */   extends WHTDStatistic
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   
/*     */   public HierSingleLabelStat(ClassHierarchy hier, int comp) {
/*  43 */     super(hier, comp);
/*     */   }
/*     */   
/*     */   public HierSingleLabelStat(ClassHierarchy hier, boolean onlymean, int comp) {
/*  47 */     super(hier, onlymean, comp);
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneStat() {
/*  51 */     return (ClusStatistic)new HierSingleLabelStat(this.m_Hier, false, this.m_Compatibility);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addPredictWriterSchema(String prefix, ClusSchema schema) {
/*  57 */     float biggest = 0.0F;
/*  58 */     int prediction = 0;
/*  59 */     int count = 0;
/*     */     
/*  61 */     ClassHierarchy hier = getHier();
/*  62 */     ArrayList<String> leafClasses = new ArrayList();
/*  63 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/*  64 */       ClassTerm term = hier.getTermAt(i);
/*  65 */       if (term.getNbChildren() == 0) {
/*  66 */         leafClasses.add(term.toStringHuman(hier));
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  76 */     NominalAttrType nominalAttrType = new NominalAttrType(prefix + "-class", leafClasses);
/*  77 */     schema.addAttrType((ClusAttrType)nominalAttrType);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPredictWriterString() {
/*  82 */     double biggest = 0.0D;
/*  83 */     int prediction = -1;
/*     */     
/*  85 */     StringBuffer buf = new StringBuffer();
/*     */     
/*  87 */     ClassHierarchy hier = getHier();
/*     */     
/*  89 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/*  90 */       ClassTerm classTerm = hier.getTermAt(i);
/*     */       
/*  92 */       if (classTerm.getNbChildren() == 0)
/*     */       {
/*  94 */         if (prediction == -1) {
/*  95 */           prediction = i;
/*  96 */           biggest = this.m_Means[i];
/*     */         }
/*  98 */         else if (this.m_Means[i] > biggest) {
/*  99 */           prediction = i;
/* 100 */           biggest = this.m_Means[i];
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 108 */     ClassTerm term = hier.getTermAt(prediction);
/* 109 */     return term.toStringHuman(hier);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\HierSingleLabelStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */