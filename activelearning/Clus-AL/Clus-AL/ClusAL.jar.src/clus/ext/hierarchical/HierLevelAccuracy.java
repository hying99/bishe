/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
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
/*     */ 
/*     */ public class HierLevelAccuracy
/*     */   extends ClusError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected ClassHierarchy m_Hier;
/*     */   protected double[] m_CorrectLevel;
/*     */   protected double[] m_CountLevel;
/*     */   protected double m_Correct;
/*     */   protected double m_Predicted;
/*     */   protected boolean[] m_ActualArr;
/*     */   protected boolean[] m_PredLevelErr;
/*     */   protected int m_MaxDepth;
/*     */   
/*     */   public HierLevelAccuracy(ClusErrorList par, ClassHierarchy hier) {
/*  51 */     super(par, hier.getMaxDepth());
/*  52 */     this.m_Hier = hier;
/*  53 */     this.m_CorrectLevel = new double[this.m_Dim];
/*  54 */     this.m_CountLevel = new double[this.m_Dim];
/*  55 */     this.m_ActualArr = new boolean[hier.getTotal()];
/*  56 */     this.m_PredLevelErr = new boolean[this.m_Dim];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void update(ClassTerm node, int depth, double[] predarr) {
/*  66 */     boolean has_pred = (predarr[node.getIndex()] >= 0.5D);
/*  67 */     boolean has_actual = this.m_ActualArr[node.getIndex()];
/*  68 */     if ((has_pred || has_actual) && 
/*  69 */       depth > this.m_MaxDepth) this.m_MaxDepth = depth;
/*     */     
/*  71 */     if (has_pred != has_actual) {
/*  72 */       this.m_PredLevelErr[depth] = true;
/*     */     }
/*  74 */     for (int i = 0; i < node.getNbChildren(); i++) {
/*  75 */       update((ClassTerm)node.getChild(i), depth + 1, predarr);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addInvalid(DataTuple tuple) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getModelError() {
/* 108 */     int nb = getNbExamples();
/* 109 */     return (nb == 0) ? 0.0D : (1.0D - this.m_Correct / nb);
/*     */   }
/*     */   
/*     */   public double getErrorComp(int i) {
/* 113 */     double nb = this.m_CountLevel[i];
/* 114 */     return (nb == 0.0D) ? 0.0D : (this.m_CorrectLevel[i] / nb);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getAccuracy() {
/* 119 */     return (this.m_Predicted == 0.0D) ? 0.0D : (this.m_Correct / this.m_Predicted);
/*     */   }
/*     */   
/*     */   public double getRecall() {
/* 123 */     int nb = getNbExamples();
/* 124 */     return (nb == 0) ? 0.0D : (this.m_Predicted / nb);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getOverallAccuracy() {
/* 129 */     int nb = getNbExamples();
/* 130 */     return (nb == 0) ? 0.0D : (this.m_Correct / nb);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 134 */     return "Hierarchical accuracy by level";
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 138 */     return new HierLevelAccuracy(par, this.m_Hier);
/*     */   }
/*     */   
/*     */   public void reset() {
/* 142 */     Arrays.fill(this.m_CorrectLevel, 0.0D);
/* 143 */     Arrays.fill(this.m_CountLevel, 0.0D);
/* 144 */     this.m_Correct = 0.0D;
/* 145 */     this.m_Predicted = 0.0D;
/*     */   }
/*     */   
/*     */   public void add(ClusError other) {
/* 149 */     HierLevelAccuracy acc = (HierLevelAccuracy)other;
/* 150 */     this.m_Correct += acc.m_Correct;
/* 151 */     this.m_Predicted += acc.m_Predicted;
/* 152 */     for (int i = 0; i < this.m_Dim; i++) {
/* 153 */       this.m_CorrectLevel[i] = this.m_CorrectLevel[i] + acc.m_CorrectLevel[i];
/* 154 */       this.m_CountLevel[i] = this.m_CountLevel[i] + acc.m_CountLevel[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void showModelError(PrintWriter out, int detail) {
/* 159 */     NumberFormat fr = getFormat();
/* 160 */     StringBuffer buf = new StringBuffer();
/* 161 */     buf.append("[");
/*     */     
/* 163 */     for (int i = 0; i < this.m_Dim; i++) {
/* 164 */       if (i != 0) buf.append(","); 
/* 165 */       buf.append(fr.format(getErrorComp(i)));
/*     */     } 
/* 167 */     buf.append("]");
/* 168 */     buf.append(", Acc: ");
/* 169 */     buf.append(fr.format(getAccuracy()));
/* 170 */     buf.append(", Rec: ");
/* 171 */     buf.append(fr.format(getRecall()));
/* 172 */     buf.append(", AccAll: ");
/* 173 */     buf.append(fr.format(getOverallAccuracy()));
/* 174 */     out.println(buf.toString());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\HierLevelAccuracy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */