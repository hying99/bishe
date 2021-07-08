/*    */ package clus.error;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.statistic.CombStat;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ 
/*    */ public class ClusSumError
/*    */   extends ClusError
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/* 13 */   protected ArrayList m_Errors = new ArrayList();
/*    */   
/*    */   public ClusSumError(ClusErrorList par) {
/* 16 */     super(par);
/*    */   }
/*    */   
/*    */   public double getModelError() {
/* 20 */     int dim = 0;
/* 21 */     double result = 0.0D;
/* 22 */     for (int i = 0; i < this.m_Errors.size(); i++) {
/* 23 */       ClusError err = this.m_Errors.get(i);
/* 24 */       result += err.getModelError() * err.getDimension();
/* 25 */       dim += err.getDimension();
/*    */     } 
/* 27 */     return result / dim;
/*    */   }
/*    */   
/*    */   public void reset() {
/* 31 */     for (int i = 0; i < this.m_Errors.size(); i++) {
/* 32 */       ClusError err = this.m_Errors.get(i);
/* 33 */       err.reset();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void add(ClusError other) {
/* 38 */     ClusSumError others = (ClusSumError)other;
/* 39 */     for (int i = 0; i < this.m_Errors.size(); i++) {
/* 40 */       ClusError err = this.m_Errors.get(i);
/* 41 */       err.add(others.getComponent(i));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/* 47 */     CombStat stat = (CombStat)pred;
/* 48 */     getComponent(0).addExample(tuple, (ClusStatistic)stat.getRegressionStat());
/* 49 */     getComponent(1).addExample(tuple, (ClusStatistic)stat.getClassificationStat());
/*    */   }
/*    */   
/*    */   public double computeLeafError(ClusStatistic stat) {
/* 53 */     CombStat cstat = (CombStat)stat;
/* 54 */     return getComponent(0).computeLeafError((ClusStatistic)cstat.getRegressionStat()) + 
/* 55 */       getComponent(1).computeLeafError((ClusStatistic)cstat.getClassificationStat());
/*    */   }
/*    */   
/*    */   public void addExample(DataTuple real, DataTuple pred) {
/* 59 */     for (int i = 0; i < this.m_Errors.size(); i++) {
/* 60 */       ClusError err = this.m_Errors.get(i);
/* 61 */       err.addExample(real, pred);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void addInvalid(DataTuple tuple) {
/* 66 */     for (int i = 0; i < this.m_Errors.size(); i++) {
/* 67 */       ClusError err = this.m_Errors.get(i);
/* 68 */       err.addInvalid(tuple);
/*    */     } 
/*    */   }
/*    */   
/*    */   public ClusError getErrorClone(ClusErrorList par) {
/* 73 */     ClusSumError result = new ClusSumError(par);
/* 74 */     for (int i = 0; i < this.m_Errors.size(); i++) {
/* 75 */       ClusError err = this.m_Errors.get(i);
/* 76 */       result.addComponent(err.getErrorClone(par));
/*    */     } 
/* 78 */     return result;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 82 */     StringBuffer name = new StringBuffer();
/* 83 */     for (int i = 0; i < this.m_Errors.size(); i++) {
/* 84 */       ClusError err = this.m_Errors.get(i);
/* 85 */       if (i != 0) name.append(", "); 
/* 86 */       name.append(err.getName());
/*    */     } 
/* 88 */     return name.toString();
/*    */   }
/*    */   
/*    */   public void addComponent(ClusError err) {
/* 92 */     this.m_Errors.add(err);
/*    */   }
/*    */   
/*    */   public ClusError getComponent(int i) {
/* 96 */     return this.m_Errors.get(i);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\ClusSumError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */