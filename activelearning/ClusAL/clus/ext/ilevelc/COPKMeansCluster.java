/*    */ package clus.ext.ilevelc;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.NumericAttrType;
/*    */ import clus.main.ClusStatManager;
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class COPKMeansCluster
/*    */   implements Serializable
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected int m_Index;
/*    */   protected ClusStatManager m_Mgr;
/* 38 */   protected ArrayList m_Data = new ArrayList();
/*    */   protected ILevelCStatistic m_Center;
/*    */   
/*    */   public COPKMeansCluster(DataTuple tuple, ClusStatManager mgr) {
/* 42 */     this.m_Mgr = mgr;
/* 43 */     this.m_Data.add(tuple);
/* 44 */     this.m_Center = (ILevelCStatistic)mgr.getStatistic(2).cloneStat();
/* 45 */     updateCenter();
/*    */   }
/*    */   
/*    */   public ILevelCStatistic getCenter() {
/* 49 */     return this.m_Center;
/*    */   }
/*    */   
/*    */   public ClusStatManager getStatManager() {
/* 53 */     return this.m_Mgr;
/*    */   }
/*    */   
/*    */   public void clearData() {
/* 57 */     this.m_Data.clear();
/*    */   }
/*    */   
/*    */   public void addData(DataTuple tuple) {
/* 61 */     this.m_Data.add(tuple);
/*    */   }
/*    */   
/*    */   public void updateCenter() {
/* 65 */     this.m_Center.reset();
/* 66 */     for (int i = 0; i < this.m_Data.size(); i++) {
/* 67 */       DataTuple tuple = this.m_Data.get(i);
/* 68 */       this.m_Center.updateWeighted(tuple, tuple.getWeight());
/*    */     } 
/* 70 */     this.m_Center.calcMean();
/*    */   }
/*    */   
/*    */   public double computeDistance(DataTuple tuple) {
/* 74 */     double dist = 0.0D;
/* 75 */     double[] num = this.m_Center.getNumericPred();
/* 76 */     for (int j = 0; j < this.m_Center.getNbAttributes(); j++) {
/* 77 */       NumericAttrType att = this.m_Center.getAttribute(j);
/* 78 */       double v1 = num[j];
/* 79 */       double v2 = tuple.getDoubleVal(att.getArrayIndex());
/* 80 */       dist += (v1 - v2) * (v1 - v2);
/*    */     } 
/* 82 */     return Math.sqrt(dist);
/*    */   }
/*    */   
/*    */   public void setIndex(int i) {
/* 86 */     this.m_Index = i;
/* 87 */     this.m_Center.setClusterID(i);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ilevelc\COPKMeansCluster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */