/*     */ package sit.mtLearner;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Iterator;
/*     */ import sit.TargetSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KNNLearner
/*     */   extends MTLearnerImpl
/*     */ {
/*     */   protected RowData[] LearnModel(TargetSet targets, RowData train, RowData test) {
/*  28 */     String appName = this.m_Sett.getAppName();
/*     */     
/*  30 */     writeCSV("train.csv" + appName, targets, train);
/*  31 */     writeCSV("test.csv" + appName, targets, test);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  38 */     NumericAttrType[] descriptive = test.m_Schema.getNumericAttrUse(1);
/*  39 */     int nrFeatures = descriptive.length;
/*  40 */     int nrTargets = targets.size();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  45 */     try { int benchmk_cnt = train.getNbRows();
/*     */ 
/*     */       
/*  48 */       String[] commands = { "/home/beau/SIT_evaluation/gent/top40/ga_basic_SIT", "config.txt", test.getNbRows() + "", nrFeatures + "", nrTargets + "", "train.csv" + appName, "test.csv" + appName, "result.csv" + appName, benchmk_cnt + "" };
/*     */ 
/*     */ 
/*     */       
/*  52 */       for (int i = 0; i < commands.length; i++) {
/*  53 */         System.out.print(commands[i] + " ");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  59 */       Process child = Runtime.getRuntime().exec(commands);
/*     */ 
/*     */ 
/*     */       
/*  63 */       BufferedReader input = new BufferedReader(new InputStreamReader(child.getInputStream())); String line;
/*  64 */       while ((line = input.readLine()) != null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  70 */       child.waitFor();
/*     */        }
/*     */     
/*  73 */     catch (IOException iOException) {  }
/*  74 */     catch (InterruptedException e)
/*     */     
/*  76 */     { e.printStackTrace(); }
/*     */ 
/*     */     
/*  79 */     RowData predictions = new RowData(test.m_Schema, test.getNbRows());
/*     */ 
/*     */     
/*  82 */     readResult(targets, predictions);
/*  83 */     RowData[] result = { test, predictions };
/*     */     
/*  85 */     return result;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  89 */     return "KNN";
/*     */   }
/*     */ 
/*     */   
/*     */   private RowData readResult(TargetSet targets, RowData result) {
/*     */     try {
/*  95 */       FileReader input = new FileReader("result.csv" + this.m_Sett.getAppName());
/*  96 */       BufferedReader bufRead = new BufferedReader(input);
/*  97 */       String line = bufRead.readLine();
/*     */       
/*  99 */       int count = 0;
/* 100 */       while (line != null) {
/* 101 */         DataTuple t = parseLine(line, targets, result.m_Schema);
/* 102 */         result.setTuple(t, count);
/* 103 */         count++;
/* 104 */         line = bufRead.readLine();
/*     */       } 
/*     */       
/* 107 */       bufRead.close();
/* 108 */       if (count == 0) {
/* 109 */         System.err.println("No results from KNN found???");
/*     */       }
/*     */     }
/* 112 */     catch (ArrayIndexOutOfBoundsException e) {
/* 113 */       System.out.println("no results file found?");
/* 114 */     } catch (IOException e) {
/* 115 */       e.printStackTrace();
/*     */     } 
/*     */ 
/*     */     
/* 119 */     return result;
/*     */   }
/*     */   
/*     */   private DataTuple parseLine(String line, TargetSet targets, ClusSchema schema) {
/* 123 */     DataTuple t = new DataTuple(schema);
/*     */     
/* 125 */     Iterator<NumericAttrType> trgts = targets.iterator();
/*     */     
/* 127 */     String[] values = line.split(",");
/* 128 */     Double[] doubles = new Double[values.length];
/*     */     
/* 130 */     for (int i = 0; i < values.length; i++) {
/* 131 */       doubles[i] = Double.valueOf(Double.parseDouble(values[i]));
/*     */     }
/*     */ 
/*     */     
/* 135 */     int count = 0;
/* 136 */     while (trgts.hasNext()) {
/* 137 */       NumericAttrType atr = trgts.next();
/* 138 */       atr.setNumeric(t, doubles[count].doubleValue());
/* 139 */       count++;
/*     */     } 
/*     */ 
/*     */     
/* 143 */     return t;
/*     */   }
/*     */   
/*     */   private void writeCSV(String fname, TargetSet targets, RowData data) {
/* 147 */     ClusSchema schema = this.m_Data.getSchema();
/* 148 */     NumericAttrType[] descriptive = schema.getNumericAttrUse(1);
/*     */ 
/*     */     
/* 151 */     PrintWriter p = null;
/*     */     
/* 153 */     try { p = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname))); }
/* 154 */     catch (FileNotFoundException e) { e.printStackTrace(); }
/*     */     
/* 156 */     for (int ti = 0; ti < data.getNbRows(); ti++) {
/* 157 */       DataTuple t = data.getTuple(ti);
/* 158 */       for (int j = 0; j < descriptive.length; j++) {
/* 159 */         double d = descriptive[j].getNumeric(t);
/* 160 */         p.print(d + ",");
/*     */       } 
/*     */       
/* 163 */       Iterator<ClusAttrType> i = targets.iterator();
/* 164 */       while (i.hasNext()) {
/* 165 */         double d = ((ClusAttrType)i.next()).getNumeric(t);
/* 166 */         if (i.hasNext()) {
/* 167 */           p.print(d + ","); continue;
/*     */         } 
/* 169 */         p.print(d);
/*     */       } 
/* 171 */       p.println();
/*     */     } 
/* 173 */     p.flush();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\mtLearner\KNNLearner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */