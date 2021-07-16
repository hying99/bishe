/*     */ package clus.weka;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import jeans.util.cmdline.CMDLineArgs;
/*     */ import weka.classifiers.Classifier;
/*     */ import weka.core.Instances;
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
/*     */ public class ClusWekaClassifier
/*     */   extends ClusInductionAlgorithmType
/*     */ {
/*     */   protected String m_Options;
/*     */   protected Classifier m_Classifier;
/*     */   protected ClusToWekaData m_Data;
/*     */   protected ClusStatManager m_Manager;
/*     */   
/*     */   public ClusWekaClassifier(Clus clus, String opts) throws ClusException {
/*  52 */     super(clus);
/*  53 */     this.m_Options = opts;
/*  54 */     String[] split = opts.split("\\s+");
/*  55 */     String[] options = new String[split.length - 1];
/*  56 */     System.arraycopy(split, 1, options, 0, options.length);
/*     */     try {
/*  58 */       System.out.println("Loading classifier: " + split[0]);
/*  59 */       this.m_Classifier = Classifier.forName(split[0], options);
/*  60 */     } catch (Exception e) {
/*  61 */       throw new ClusException("Weka Error: " + e.getClass().getName() + ": " + e.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   public ClusInductionAlgorithm createInduce(ClusSchema schema, Settings sett, CMDLineArgs cargs) throws ClusException, IOException {
/*  66 */     return new ClusWekaInduce(schema, sett);
/*     */   }
/*     */   
/*     */   public void printInfo() {
/*  70 */     System.out.println("Weka Classifier: " + this.m_Options);
/*     */   }
/*     */   
/*     */   public void initializeInduce(ClusInductionAlgorithm induce, CMDLineArgs cargs) {
/*  74 */     this.m_Data = new ClusToWekaData(induce.getSchema());
/*  75 */     this.m_Manager = induce.getStatManager();
/*     */   }
/*     */   
/*     */   public ClusStatistic createStatistic() {
/*  79 */     return this.m_Manager.createClusteringStat();
/*     */   }
/*     */   
/*     */   public Instances getDummyData() {
/*  83 */     return this.m_Data.getDummyData();
/*     */   }
/*     */   
/*     */   public ClusModel induceSingle(ClusRun cr) throws ClusException {
/*  87 */     ClusWekaModel result = new ClusWekaModel();
/*  88 */     RowData data = (RowData)cr.getTrainingSet();
/*     */     try {
/*  90 */       Classifier copy = Classifier.makeCopy(this.m_Classifier);
/*  91 */       copy.buildClassifier(this.m_Data.convertData(data));
/*  92 */       result.setClassifier(copy);
/*  93 */       result.setParent(this);
/*  94 */       return result;
/*  95 */     } catch (Exception e) {
/*  96 */       throw new ClusException("Weka Error: " + e.getClass().getName() + ": " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void pruneAll(ClusRun cr) throws ClusException, IOException {}
/*     */   
/*     */   public ClusModel pruneSingle(ClusModel model, ClusRun cr) throws ClusException, IOException {
/* 104 */     return model;
/*     */   }
/*     */   
/*     */   public class ClusWekaInduce
/*     */     extends ClusInductionAlgorithm {
/*     */     public ClusWekaInduce(ClusSchema schema, Settings sett) throws ClusException, IOException {
/* 110 */       super(schema, sett);
/*     */     }
/*     */     
/*     */     public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/* 114 */       return ClusWekaClassifier.this.induceSingle(cr);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\weka\ClusWekaClassifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */