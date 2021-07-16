/*     */ package clus.algo.optimizer.HCAL;
/*     */ 
/*     */ import clus.activelearning.data.OracleAnswer;
/*     */ import clus.algo.optimizer.Item;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HCALItem
/*     */   extends Item
/*     */ {
/*     */   private OracleAnswer m_OracleAnswer;
/*     */   private double m_Cost;
/*     */   private double m_Informativeness;
/*     */   private String m_Label;
/*     */   private int m_ActiveIndex;
/*     */   
/*     */   public HCALItem(boolean chosen, OracleAnswer oracleAnswer, int activeIndex, double cost, double informativeness, String label) {
/*  24 */     super(Boolean.valueOf(chosen));
/*  25 */     this.m_OracleAnswer = oracleAnswer;
/*  26 */     this.m_Cost = cost;
/*  27 */     this.m_ActiveIndex = activeIndex;
/*  28 */     this.m_Informativeness = informativeness;
/*  29 */     this.m_Label = label;
/*     */   }
/*     */   
/*     */   public HCALItem(HCALItem oldItem) {
/*  33 */     super(oldItem.isActive());
/*  34 */     this.m_OracleAnswer = oldItem.getOracleAnswer();
/*  35 */     this.m_Cost = oldItem.getCost();
/*  36 */     this.m_ActiveIndex = oldItem.getActiveIndex();
/*  37 */     this.m_Informativeness = oldItem.getInformativeness();
/*  38 */     this.m_Label = oldItem.getLabel();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OracleAnswer getOracleAnswer() {
/*  45 */     return this.m_OracleAnswer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOracleAnswer(OracleAnswer m_OracleAnswer) {
/*  52 */     this.m_OracleAnswer = m_OracleAnswer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCost() {
/*  59 */     return this.m_Cost;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCost(double m_Cost) {
/*  66 */     this.m_Cost = m_Cost;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getInformativeness() {
/*  73 */     return this.m_Informativeness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInformativeness(double m_Informativeness) {
/*  80 */     this.m_Informativeness = m_Informativeness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLabel() {
/*  87 */     return this.m_Label;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLabel(String m_Label) {
/*  94 */     this.m_Label = m_Label;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getActiveIndex() {
/* 101 */     return this.m_ActiveIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setActiveIndex(int m_ActiveIndex) {
/* 108 */     this.m_ActiveIndex = m_ActiveIndex;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\optimizer\HCAL\HCALItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */