/*     */ package clus.activelearning.iteration;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusLabelInferingIteration
/*     */   extends Iteration
/*     */ {
/*     */   private double m_NegativeInfered;
/*     */   private double m_PositiveInfered;
/*     */   private double m_NegativeCorrect;
/*     */   private double m_PositiveCorrect;
/*     */   
/*     */   public ClusLabelInferingIteration(HashMap<String, Integer> labelCounter, int availableLabels) {
/*  22 */     super(labelCounter, availableLabels);
/*     */   }
/*     */   
/*     */   public void printIteration() {
/*  26 */     System.out.println("LABELS SELECTED" + getLabelsSelected());
/*  27 */     System.out.println("ANSWERED" + getLabelsQueried());
/*     */   }
/*     */   
/*     */   public void updateNegativeInfered(double amount) {
/*  31 */     setNegativeInfered(getNegativeInfered() + amount);
/*     */   }
/*     */   
/*     */   public void updatePositiveInfered(double amount) {
/*  35 */     setPositiveInfered(getPositiveInfered() + amount);
/*     */   }
/*     */   
/*     */   public void updatePositiveCorrectInfered(double amount) {
/*  39 */     setPositiveCorrect(getPositiveCorrect() + amount);
/*     */   }
/*     */   
/*     */   public void updateNegativeCorrectInfered(double amount) {
/*  43 */     setNegativeCorrect(getNegativeCorrect() + amount);
/*     */   }
/*     */   
/*     */   public double getCorrectPercentage() {
/*  47 */     return (this.m_NegativeCorrect + this.m_PositiveCorrect) / (this.m_NegativeInfered + this.m_PositiveInfered);
/*     */   }
/*     */   
/*     */   public double getCorrectPositivePercentage() {
/*  51 */     if (getPositiveCorrect() == 0.0D) {
/*  52 */       if (getPositiveInfered() == 0.0D) {
/*  53 */         return -1.0D;
/*     */       }
/*  55 */       return 0.0D;
/*     */     } 
/*     */     
/*  58 */     return getPositiveCorrect() / getPositiveInfered();
/*     */   }
/*     */   
/*     */   public double getCorrectNegativePercentage() {
/*  62 */     if (getNegativeCorrect() == 0.0D) {
/*  63 */       if (getNegativeInfered() == 0.0D) {
/*  64 */         return -1.0D;
/*     */       }
/*  66 */       return 0.0D;
/*     */     } 
/*     */     
/*  69 */     return getNegativeCorrect() / getNegativeInfered();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getNegativeInfered() {
/*  76 */     return this.m_NegativeInfered;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setNegativeInfered(double m_NegativeInfered) {
/*  83 */     this.m_NegativeInfered = m_NegativeInfered;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getPositiveInfered() {
/*  90 */     return this.m_PositiveInfered;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setPositiveInfered(double m_PositiveInfered) {
/*  97 */     this.m_PositiveInfered = m_PositiveInfered;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getNegativeCorrect() {
/* 104 */     return this.m_NegativeCorrect;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNegativeCorrect(double m_NegativeCorrectPercentage) {
/* 111 */     this.m_NegativeCorrect = m_NegativeCorrectPercentage;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getPositiveCorrect() {
/* 118 */     return this.m_PositiveCorrect;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPositiveCorrect(double m_PositiveCorrectPercentage) {
/* 125 */     this.m_PositiveCorrect = m_PositiveCorrectPercentage;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\iteration\ClusLabelInferingIteration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */