/*    */ package clus.ext.exhaustivesearch;
/*    */ 
/*    */ import clus.algo.ClusInductionAlgorithm;
/*    */ import clus.algo.split.NominalSplit;
/*    */ import clus.algo.tdidt.ClusDecisionTree;
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.ClusData;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.ext.beamsearch.ClusBeamModel;
/*    */ import clus.main.ClusRun;
/*    */ import clus.main.Settings;
/*    */ import clus.model.ClusModel;
/*    */ import clus.model.ClusModelInfo;
/*    */ import clus.model.modelio.ClusModelCollectionIO;
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
/*    */ public class ClusExhaustiveInduce
/*    */   extends ClusInductionAlgorithm
/*    */ {
/*    */   protected NominalSplit m_Split;
/*    */   protected ClusExhaustiveSearch m_Search;
/*    */   
/*    */   public ClusExhaustiveInduce(ClusSchema schema, Settings sett, ClusExhaustiveSearch search) throws ClusException, IOException {
/* 47 */     super(schema, sett);
/* 48 */     this.m_Search = search;
/*    */   }
/*    */   
/*    */   public void initializeHeuristic() {
/* 52 */     this.m_Search.initializeHeuristic();
/*    */   }
/*    */ 
/*    */   
/*    */   public ClusData createData() {
/* 57 */     return (ClusData)new RowData(this.m_Schema);
/*    */   }
/*    */   
/*    */   public boolean isModelWriter() {
/* 61 */     return true;
/*    */   }
/*    */   
/*    */   public void writeModel(ClusModelCollectionIO strm) throws IOException {
/* 65 */     this.m_Search.writeModel(strm);
/*    */   }
/*    */   
/*    */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/* 69 */     return null;
/*    */   }
/*    */   
/*    */   public void induceAll(ClusRun cr) throws ClusException, IOException {
/* 73 */     this.m_Search.exhaustiveSearch(cr);
/* 74 */     ClusModelInfo def_model = cr.addModelInfo(0);
/* 75 */     def_model.setModel(ClusDecisionTree.induceDefault(cr));
/* 76 */     def_model.setName("Default");
/* 77 */     ArrayList<ClusBeamModel> lst = this.m_Search.getBeam().toArray();
/* 78 */     for (int i = 0; i < lst.size(); i++) {
/*    */       
/* 80 */       ClusBeamModel mdl = lst.get(i);
/* 81 */       ClusNode tree = (ClusNode)mdl.getModel();
/*    */ 
/*    */       
/* 84 */       ClusModelInfo model_info = cr.addModelInfo(i);
/* 85 */       model_info.setModel((ClusModel)tree);
/* 86 */       model_info.setName("Beam " + (i + 1));
/* 87 */       model_info.clearAll();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\exhaustivesearch\ClusExhaustiveInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */