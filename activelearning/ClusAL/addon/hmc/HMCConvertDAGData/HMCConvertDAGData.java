/*     */ package addon.hmc.HMCConvertDAGData;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.data.io.ARFFFile;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassesAttrType;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.ext.hierarchical.WHTDStatistic;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import jeans.util.FileUtil;
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
/*     */ public class HMCConvertDAGData
/*     */ {
/*     */   public static final boolean CREATE_TRAIN_TUNE_TEST_SPLIT = true;
/*     */   
/*     */   public void convert(String input, String output, double minfreq) throws Exception {
/*  44 */     Clus clus = new Clus();
/*  45 */     String appname = FileUtil.getName(input) + ".s";
/*  46 */     clus.initializeAddOn(appname);
/*  47 */     ClusStatManager mgr = clus.getStatManager();
/*  48 */     Settings sett = clus.getSettings();
/*  49 */     RowData data = clus.getData();
/*     */     
/*  51 */     ClusRun run = clus.partitionData();
/*  52 */     ClusStatistic[] stats = new ClusStatistic[1];
/*  53 */     stats[0] = mgr.createClusteringStat();
/*  54 */     data.calcTotalStats(stats);
/*  55 */     if (!sett.isNullTestFile()) {
/*  56 */       System.out.println("Loading: " + sett.getTestFile());
/*  57 */       if (minfreq != 0.0D) {
/*  58 */         RowData test = run.getTestSet();
/*  59 */         test.calcTotalStats(stats);
/*     */       } else {
/*  61 */         clus.updateStatistic(sett.getTestFile(), stats);
/*     */       } 
/*     */     } 
/*  64 */     if (!sett.isNullPruneFile()) {
/*  65 */       System.out.println("Loading: " + sett.getPruneFile());
/*  66 */       if (minfreq != 0.0D) {
/*  67 */         RowData tune = (RowData)run.getPruneSet();
/*  68 */         tune.calcTotalStats(stats);
/*     */       } else {
/*  70 */         clus.updateStatistic(sett.getPruneFile(), stats);
/*     */       } 
/*     */     } 
/*  73 */     ClusStatistic.calcMeans(stats);
/*  74 */     WHTDStatistic stat = (WHTDStatistic)stats[0];
/*  75 */     stat.showRootInfo();
/*  76 */     ClassHierarchy hier = mgr.getHier();
/*  77 */     boolean[] removed = hier.removeInfrequentClasses(stat, minfreq);
/*  78 */     if (minfreq != 0.0D) {
/*  79 */       ClassesAttrType type = hier.getType();
/*  80 */       removeLabelsFromData((RowData)run.getTrainingSet(), type, removed);
/*  81 */       if (!sett.isNullTestFile()) removeLabelsFromData(run.getTestSet(), type, removed); 
/*  82 */       if (!sett.isNullPruneFile()) removeLabelsFromData((RowData)run.getPruneSet(), type, removed); 
/*     */     } 
/*  84 */     hier.initialize();
/*  85 */     if (minfreq != 0.0D) {
/*  86 */       addIntermediateLabels((RowData)run.getTrainingSet(), hier);
/*  87 */       if (!sett.isNullTestFile()) addIntermediateLabels(run.getTestSet(), hier); 
/*  88 */       if (!sett.isNullPruneFile()) addIntermediateLabels((RowData)run.getPruneSet(), hier); 
/*     */     } 
/*  90 */     hier.showSummary();
/*  91 */     RowData train = (RowData)run.getTrainingSet();
/*  92 */     ARFFFile.writeArff(output + ".train.arff", train);
/*  93 */     if (!sett.isNullTestFile()) {
/*  94 */       RowData test = run.getTestSet();
/*  95 */       ARFFFile.writeArff(output + ".test.arff", test);
/*     */     } 
/*  97 */     if (!sett.isNullPruneFile()) {
/*  98 */       RowData tune = (RowData)run.getPruneSet();
/*  99 */       ARFFFile.writeArff(output + ".valid.arff", tune);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeLabelsFromData(RowData data, ClassesAttrType type, boolean[] removed) {
/* 107 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 108 */       ClassesTuple tuple = (ClassesTuple)data.getTuple(i).getObjVal(type.getArrayIndex());
/* 109 */       tuple.removeLabels(removed);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addIntermediateLabels(RowData data, ClassHierarchy hier) {
/* 114 */     ClassesAttrType type = hier.getType();
/* 115 */     ArrayList scratch = new ArrayList();
/* 116 */     boolean[] alllabels = new boolean[hier.getTotal()];
/* 117 */     ArrayList<DataTuple> leftdata = new ArrayList();
/* 118 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 119 */       DataTuple tuple = data.getTuple(i);
/* 120 */       scratch.clear();
/* 121 */       Arrays.fill(alllabels, false);
/* 122 */       ClassesTuple ct = (ClassesTuple)tuple.getObjVal(type.getArrayIndex());
/* 123 */       ct.addIntermediateElems(hier, alllabels, scratch);
/* 124 */       if (ct.getNbClasses() > 0) {
/* 125 */         leftdata.add(tuple);
/*     */       }
/*     */     } 
/* 128 */     data.setFromList(leftdata);
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 132 */     if (args.length != 2 && args.length != 4) {
/* 133 */       System.out.println("Usage: HMCConvertDAGData [-minfreq f] input.arff output.arff");
/* 134 */       System.exit(0);
/*     */     } 
/* 136 */     double minfreq = 0.0D;
/* 137 */     String input = args[0];
/* 138 */     String output = args[1];
/* 139 */     if (args[0].equals("-minfreq")) {
/* 140 */       minfreq = Double.parseDouble(args[1]) / 100.0D;
/* 141 */       input = args[2];
/* 142 */       output = args[3];
/*     */     } 
/* 144 */     HMCConvertDAGData cnv = new HMCConvertDAGData();
/*     */     try {
/* 146 */       cnv.convert(input, output, minfreq);
/* 147 */     } catch (Exception e) {
/* 148 */       System.err.println("Error: " + e);
/* 149 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\addon\hmc\HMCConvertDAGData\HMCConvertDAGData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */