/*     */ package clus.error;
/*     */ 
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.main.ClusOutput;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.IOException;
/*     */ import jeans.util.IntervalCollection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusErrorOutput
/*     */   extends ClusOutput
/*     */ {
/*     */   public ClusErrorOutput(String fname, ClusSchema schema, Settings sett) throws IOException {
/*  20 */     super(fname, schema, sett);
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusErrorOutput(String fname, Settings sett) throws IOException {
/*  25 */     super(fname, null, sett);
/*     */   }
/*     */   
/*     */   public ClusErrorOutput(ClusSchema schema, Settings sett) throws IOException {
/*  29 */     super(schema, sett);
/*     */   }
/*     */   
/*     */   public void writeHeader() throws IOException {
/*  33 */     this.m_Writer.println("@relation experiment");
/*  34 */     this.m_Writer.println("@attribute Dataset {" + this.m_Sett.getDataFile() + "}");
/*  35 */     this.m_Writer.println("@attribute Run numeric");
/*  36 */     this.m_Writer.println("@attribute Fold {01,02,03,04,05,06,07,08,09,10}");
/*  37 */     this.m_Writer.println("@attribute Algo string");
/*     */     
/*  39 */     this.m_Writer.println("@attribute Targets string");
/*  40 */     this.m_Writer.println("@attribute Descriptive string");
/*  41 */     this.m_Writer.println("@attribute MainTarget numeric");
/*     */ 
/*     */ 
/*     */     
/*  45 */     this.m_Writer.println("@attribute RMSE_Default numeric");
/*  46 */     this.m_Writer.println("@attribute RMSE_Original numeric");
/*  47 */     this.m_Writer.println("@attribute RMSE_Pruned numeric");
/*     */     
/*  49 */     this.m_Writer.println("@attribute WRMSE_Default numeric");
/*  50 */     this.m_Writer.println("@attribute WRMSE_Original numeric");
/*  51 */     this.m_Writer.println("@attribute WRMSE_Pruned numeric");
/*     */ 
/*     */     
/*  54 */     this.m_Writer.println("@attribute OriginalModelSize numeric");
/*  55 */     this.m_Writer.println("@attribute PrunedModelSize numeric");
/*     */ 
/*     */ 
/*     */     
/*  59 */     this.m_Writer.println("@attribute nrSupportTargets numeric");
/*  60 */     this.m_Writer.println("@attribute inductionTime numeric");
/*     */ 
/*     */ 
/*     */     
/*  64 */     this.m_Writer.println("@data");
/*     */     
/*  66 */     this.m_Writer.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeOutput(ClusRun cr, boolean detail, boolean outputtrain, double[] clusteringWeights) throws IOException, ClusException {
/*  72 */     this.m_Writer.print(this.m_Sett.getDataFile() + ",");
/*     */     
/*  74 */     this.m_Writer.print(this.m_Sett.getRandomSeed() + ",");
/*     */     
/*  76 */     this.m_Writer.print(cr.getIndexString() + ",");
/*     */     
/*  78 */     IntervalCollection targets = new IntervalCollection(this.m_Sett.getTarget());
/*  79 */     int nrTargets = targets.getMinIndex() - targets.getMaxIndex() + 1;
/*     */     
/*  81 */     this.m_Writer.print(this.m_Sett.getPruningMethodName() + ",");
/*     */     
/*  83 */     this.m_Writer.print(this.m_Sett.getTarget() + ",");
/*     */     
/*  85 */     this.m_Writer.print(this.m_Sett.getDescriptive() + ",");
/*     */     
/*  87 */     this.m_Writer.print(this.m_Sett.getMainTarget());
/*     */     
/*  89 */     if (this.m_Sett.getMainTarget().equals("Default")) {
/*     */       return;
/*     */     }
/*     */     
/*  93 */     int mt = (new Integer(this.m_Sett.getMainTarget())).intValue();
/*     */     
/*  95 */     int mt_idx = mt - targets.getMinIndex();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 103 */     ClusErrorList tr_err = cr.getTestError();
/* 104 */     if (tr_err != null) {
/* 105 */       for (int i = 1; i < 3; i++) {
/* 106 */         for (int m = 0; m < 3; m++) {
/* 107 */           ClusModelInfo inf = cr.getModelInfo(m);
/* 108 */           ClusErrorList parent = inf.getError(1);
/*     */           
/* 110 */           ClusError err2 = parent.getError(i);
/*     */ 
/*     */           
/* 113 */           this.m_Writer.print("," + err2.getModelErrorComponent(mt_idx));
/*     */         } 
/*     */       } 
/* 116 */       for (int k = 1; k < 3; k++) {
/* 117 */         ClusModelInfo inf = cr.getModelInfo(k);
/* 118 */         this.m_Writer.print("," + inf.getModelSize());
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 123 */     int supportTargetCounter = -1;
/*     */     
/* 125 */     for (int j = 0; j < clusteringWeights.length; j++) {
/* 126 */       if (clusteringWeights[j] == 1.0D)
/*     */       {
/* 128 */         supportTargetCounter++;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 133 */     this.m_Writer.print("," + supportTargetCounter);
/*     */     
/* 135 */     this.m_Writer.print("," + ClusFormat.FOUR_AFTER_DOT.format(cr.getInductionTime() / 1000.0D));
/*     */     
/* 137 */     this.m_Writer.println();
/* 138 */     this.m_Writer.flush();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\ClusErrorOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */