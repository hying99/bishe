/*     */ package addon.hmc.HMCConvertToSC;
/*     */ import clus.Clus;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.util.ClusException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class HMCConvertToSC {
/*     */   public void convert(String input, String output, boolean binary, boolean split) throws Exception {
/*  21 */     Clus clus = new Clus();
/*  22 */     String appname = FileUtil.getName(input) + ".s";
/*  23 */     clus.initializeAddOn(appname);
/*  24 */     ClusStatManager mgr = clus.getStatManager();
/*  25 */     Settings sett = clus.getSettings();
/*  26 */     ClassHierarchy hier = mgr.getHier();
/*  27 */     int sidx = hier.getType().getArrayIndex();
/*  28 */     String[] classterms = new String[hier.getTotal()];
/*  29 */     for (int i = 0; i < hier.getTotal(); i++) {
/*  30 */       ClassTerm term = hier.getTermAt(i);
/*  31 */       classterms[i] = term.toStringHuman(hier);
/*     */     } 
/*     */     
/*  34 */     if (split) {
/*  35 */       ClusRun run = clus.partitionData();
/*  36 */       RowData train = (RowData)run.getTrainingSet();
/*  37 */       boolean[][] classes = new boolean[train.getNbRows()][hier.getTotal()];
/*  38 */       for (int j = 0; j < train.getNbRows(); j++) {
/*  39 */         DataTuple tuple = train.getTuple(j);
/*  40 */         ClassesTuple tp = (ClassesTuple)tuple.getObjVal(sidx);
/*  41 */         Arrays.fill(classes[j], false);
/*  42 */         tp.fillBoolArrayNodeAndAncestors(classes[j]);
/*     */       } 
/*  44 */       writeArffToSC(output + ".train.arff", train, classterms, classes, binary);
/*  45 */       if (!sett.isNullTestFile()) {
/*  46 */         RowData test = run.getTestSet();
/*  47 */         classes = new boolean[test.getNbRows()][hier.getTotal()];
/*  48 */         for (int k = 0; k < test.getNbRows(); k++) {
/*  49 */           DataTuple tuple = test.getTuple(k);
/*  50 */           ClassesTuple tp = (ClassesTuple)tuple.getObjVal(sidx);
/*  51 */           Arrays.fill(classes[k], false);
/*  52 */           tp.fillBoolArrayNodeAndAncestors(classes[k]);
/*     */         } 
/*  54 */         writeArffToSC(output + ".test.arff", test, classterms, classes, binary);
/*     */       } 
/*  56 */       if (!sett.isNullPruneFile()) {
/*  57 */         RowData tune = (RowData)run.getPruneSet();
/*  58 */         classes = new boolean[tune.getNbRows()][hier.getTotal()];
/*  59 */         for (int k = 0; k < tune.getNbRows(); k++) {
/*  60 */           DataTuple tuple = tune.getTuple(k);
/*  61 */           ClassesTuple tp = (ClassesTuple)tuple.getObjVal(sidx);
/*  62 */           Arrays.fill(classes[k], false);
/*  63 */           tp.fillBoolArrayNodeAndAncestors(classes[k]);
/*     */         } 
/*  65 */         writeArffToSC(output + ".valid.arff", tune, classterms, classes, binary);
/*     */       } 
/*     */     } else {
/*  68 */       RowData data = clus.getData();
/*  69 */       boolean[][] classes = new boolean[data.getNbRows()][hier.getTotal()];
/*  70 */       for (int j = 0; j < data.getNbRows(); j++) {
/*  71 */         DataTuple tuple = data.getTuple(j);
/*  72 */         ClassesTuple tp = (ClassesTuple)tuple.getObjVal(sidx);
/*  73 */         Arrays.fill(classes[j], false);
/*  74 */         tp.fillBoolArrayNodeAndAncestors(classes[j]);
/*     */       } 
/*  76 */       writeArffToSC(output + ".arff", data, classterms, classes, binary);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void writeArffHeaderToSC(PrintWriter wrt, ClusSchema schema, String[] classterms, boolean binary) throws IOException, ClusException {
/*  81 */     wrt.println("@RELATION " + schema.getRelationName());
/*  82 */     wrt.println(); int i;
/*  83 */     for (i = 0; i < schema.getNbAttributes(); i++) {
/*  84 */       ClusAttrType type = schema.getAttrType(i);
/*  85 */       if (!type.isDisabled() && !type.getName().equals("class")) {
/*  86 */         wrt.print("@ATTRIBUTE ");
/*  87 */         wrt.print(StringUtils.printStr(type.getName(), 65));
/*  88 */         if (type.isKey()) {
/*  89 */           wrt.print("key");
/*     */         } else {
/*  91 */           type.writeARFFType(wrt);
/*     */         } 
/*  93 */         wrt.println();
/*     */       } 
/*     */     } 
/*  96 */     for (i = 0; i < classterms.length; i++) {
/*  97 */       if (!classterms[i].equals("root")) {
/*  98 */         wrt.print("@ATTRIBUTE ");
/*  99 */         wrt.print(classterms[i]);
/* 100 */         if (binary) {
/*     */           
/* 102 */           wrt.print("     numeric");
/*     */         } else {
/* 104 */           wrt.print("     hierarchical     p,n");
/*     */         } 
/* 106 */         wrt.println();
/*     */       } 
/*     */     } 
/* 109 */     wrt.println();
/*     */   }
/*     */   
/*     */   public static void writeArffToSC(String fname, RowData data, String[] classterms, boolean[][] classes, boolean binary) throws IOException, ClusException {
/* 113 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
/* 114 */     ClusSchema schema = data.getSchema();
/* 115 */     writeArffHeaderToSC(wrt, schema, classterms, binary);
/* 116 */     wrt.println("@DATA");
/* 117 */     for (int j = 0; j < data.getNbRows(); j++) {
/* 118 */       DataTuple tuple = data.getTuple(j);
/* 119 */       int aidx = 0; int i;
/* 120 */       for (i = 0; i < schema.getNbAttributes(); i++) {
/* 121 */         ClusAttrType type = schema.getAttrType(i);
/* 122 */         if (!type.isDisabled() && !type.getName().equals("class")) {
/* 123 */           if (aidx != 0) wrt.print(","); 
/* 124 */           wrt.print(type.getString(tuple));
/* 125 */           aidx++;
/*     */         } 
/*     */       } 
/* 128 */       for (i = 0; i < classterms.length; i++) {
/* 129 */         if (!classterms[i].equals("root")) {
/* 130 */           if (binary)
/* 131 */           { if (classes[j][i]) { wrt.print(",1"); }
/* 132 */             else { wrt.print(",0"); }
/*     */              }
/* 134 */           else if (classes[j][i]) { wrt.print(",p"); }
/* 135 */           else { wrt.print(",n"); }
/*     */         
/*     */         }
/*     */       } 
/* 139 */       wrt.println();
/*     */     } 
/* 141 */     wrt.close();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 145 */     int mainargs = 0;
/* 146 */     boolean binary = false;
/* 147 */     boolean split = false;
/* 148 */     boolean match = true;
/* 149 */     while (match && mainargs < args.length) {
/* 150 */       match = false;
/* 151 */       if (args[mainargs].equals("-binary")) {
/*     */ 
/*     */         
/* 154 */         binary = true;
/* 155 */         match = true;
/*     */       } 
/* 157 */       if (args[mainargs].equals("-split")) {
/* 158 */         split = true;
/* 159 */         match = true;
/*     */       } 
/* 161 */       if (match) mainargs++; 
/*     */     } 
/* 163 */     if (args.length - mainargs != 2) {
/* 164 */       System.out.println("Usage: HMCConvertToSC input output");
/* 165 */       System.exit(0);
/*     */     } 
/* 167 */     String input = args[mainargs];
/* 168 */     String output = args[mainargs + 1];
/* 169 */     HMCConvertToSC cnv = new HMCConvertToSC();
/*     */     try {
/* 171 */       cnv.convert(input, output, binary, split);
/* 172 */     } catch (Exception e) {
/* 173 */       System.err.println("Error: " + e);
/* 174 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\addon\hmc\HMCConvertToSC\HMCConvertToSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */