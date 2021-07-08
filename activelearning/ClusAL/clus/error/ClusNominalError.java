/*    */ package clus.error;
/*    */ 
/*    */ import clus.data.type.NominalAttrType;
/*    */ import clus.statistic.ClassificationStat;
/*    */ import clus.statistic.ClusStatistic;
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
/*    */ 
/*    */ public abstract class ClusNominalError
/*    */   extends ClusError
/*    */ {
/*    */   protected int[] m_Default;
/*    */   protected NominalAttrType[] m_Attrs;
/*    */   
/*    */   public ClusNominalError(ClusErrorList par, NominalAttrType[] nom) {
/* 34 */     super(par, nom.length);
/* 35 */     this.m_Attrs = nom;
/*    */   }
/*    */   
/*    */   public NominalAttrType getAttr(int i) {
/* 39 */     return this.m_Attrs[i];
/*    */   }
/*    */   
/*    */   public ClusNominalError(ClusErrorList par, int nb_nominal) {
/* 43 */     super(par, nb_nominal);
/*    */   }
/*    */   
/*    */   public void setDefault(int[] value) {
/* 47 */     this.m_Default = value;
/*    */   }
/*    */   
/*    */   public void setDefault(ClusStatistic pred) {
/* 51 */     this.m_Default = ((ClassificationStat)pred).m_MajorityClasses;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\ClusNominalError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */