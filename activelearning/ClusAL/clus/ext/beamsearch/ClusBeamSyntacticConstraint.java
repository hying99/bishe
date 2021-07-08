/*    */ package clus.ext.beamsearch;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.main.ClusRun;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.model.ClusModel;
/*    */ import clus.model.modelio.ClusTreeReader;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.util.ClusException;
/*    */ import java.io.IOException;
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
/*    */ public class ClusBeamSyntacticConstraint
/*    */ {
/*    */   ClusNode m_Constraint;
/*    */   ArrayList<ClusStatistic> m_ConstraintPredictions;
/*    */   
/*    */   public ClusBeamSyntacticConstraint(ClusRun run) throws ClusException, IOException {
/* 42 */     initializeConstraint(run);
/* 43 */     ClusStatManager mgr = run.getStatManager();
/* 44 */     mgr.getSchema().attachModel((ClusModel)this.m_Constraint);
/* 45 */     createConstrStat(this.m_Constraint, mgr, (RowData)run.getTrainingSet());
/* 46 */     setConstraintPredictions(getPredictions(run));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void initializeConstraint(ClusRun run) throws IOException {
/* 52 */     ClusStatManager csm = run.getStatManager();
/* 53 */     ClusTreeReader rdr = new ClusTreeReader();
/* 54 */     String bconstrFile = csm.getSettings().getBeamConstraintFile();
/* 55 */     this.m_Constraint = rdr.loadTree(bconstrFile, csm.getSchema());
/* 56 */     this.m_Constraint.setClusteringStat(csm.createClusteringStat());
/* 57 */     this.m_Constraint.setTargetStat(csm.createTargetStat());
/*    */   }
/*    */   
/*    */   public void createConstrStat(ClusNode node, ClusStatManager mgr, RowData data) {
/* 61 */     if (node.getTest() == null) { node.makeLeaf(); }
/*    */     else
/* 63 */     { for (int j = 0; j < node.getNbChildren(); j++) {
/* 64 */         ClusNode child = (ClusNode)node.getChild(j);
/* 65 */         RowData subset = data.applyWeighted(node.getTest(), j);
/* 66 */         child.initClusteringStat(mgr, subset);
/* 67 */         child.initTargetStat(mgr, subset);
/* 68 */         child.getTargetStat().calcMean();
/* 69 */         createConstrStat(child, mgr, subset);
/*    */       }  }
/*    */   
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ArrayList<ClusStatistic> getPredictions(ClusRun run) {
/* 80 */     RowData train = (RowData)run.getTrainingSet();
/* 81 */     ArrayList<ClusStatistic> predictions = new ArrayList<>();
/* 82 */     for (int i = 0; i < train.getNbRows(); i++) {
/* 83 */       DataTuple tuple = train.getTuple(i);
/* 84 */       predictions.add(this.m_Constraint.predictWeighted(tuple));
/*    */     } 
/* 86 */     return predictions;
/*    */   }
/*    */   
/*    */   public ArrayList<ClusStatistic> getConstraintPredictions() {
/* 90 */     return this.m_ConstraintPredictions;
/*    */   }
/*    */   
/*    */   public void setConstraintPredictions(ArrayList<ClusStatistic> predictions) {
/* 94 */     this.m_ConstraintPredictions = predictions;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\beamsearch\ClusBeamSyntacticConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */