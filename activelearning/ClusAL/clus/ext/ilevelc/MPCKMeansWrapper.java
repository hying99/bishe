/*     */ package clus.ext.ilevelc;
/*     */ 
/*     */ import clus.data.io.ARFFFile;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.LineNumberReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
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
/*     */ public class MPCKMeansWrapper
/*     */ {
/*     */   protected ClusStatManager m_Manager;
/*     */   
/*     */   public MPCKMeansWrapper(ClusStatManager statManager) {
/*  41 */     this.m_Manager = statManager;
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/*  45 */     return this.m_Manager;
/*     */   }
/*     */   
/*     */   public static void writeStream(InputStream in) throws IOException {
/*  49 */     int ch = -1;
/*  50 */     StringBuffer sb = new StringBuffer();
/*  51 */     while ((ch = in.read()) != -1) {
/*  52 */       sb.append((char)ch);
/*     */     }
/*  54 */     System.out.println(sb.toString());
/*     */   }
/*     */   
/*     */   public double computeRandIndex(RowData data, int[] assign, String tpe) {
/*  58 */     int a = 0;
/*  59 */     int b = 0;
/*  60 */     int nbex = data.getNbRows();
/*  61 */     ClusSchema schema = data.getSchema();
/*  62 */     NominalAttrType classtype = (NominalAttrType)schema.getAttrType(schema.getNbAttributes() - 1);
/*  63 */     for (int i = 0; i < nbex; i++) {
/*  64 */       DataTuple ti = data.getTuple(i);
/*  65 */       int cia = classtype.getNominal(ti);
/*  66 */       int cib = assign[ti.getIndex()];
/*  67 */       for (int j = i + 1; j < nbex; j++) {
/*  68 */         DataTuple tj = data.getTuple(j);
/*  69 */         int cja = classtype.getNominal(tj);
/*  70 */         int cjb = assign[tj.getIndex()];
/*  71 */         if (cia == cja && cib == cjb) a++; 
/*  72 */         if (cia != cja && cib != cjb) b++; 
/*     */       } 
/*     */     } 
/*  75 */     double rand = 1.0D * (a + b) / (nbex * (nbex - 1) / 2);
/*  76 */     System.out.println(tpe + "Rand = " + rand + " (nbex = " + nbex + ")");
/*  77 */     return rand;
/*     */   }
/*     */   
/*     */   public ClusModel induce(RowData data, RowData test, ArrayList<ILevelConstraint> constraints, int cls) throws IOException, ClusException {
/*  81 */     String main = getStatManager().getSettings().getAppName();
/*  82 */     String datf = main + "-temp-MPCKMeans.arff";
/*  83 */     String cons = main + "-temp-MPCKMeans.cons";
/*  84 */     String outf = main + "-temp-MPCKMeans.assign";
/*  85 */     System.out.println("Calling MPCKMeans: " + main);
/*     */     
/*  87 */     FileUtil.delete(datf);
/*  88 */     FileUtil.delete(cons);
/*  89 */     FileUtil.delete(outf);
/*     */     
/*  91 */     ARFFFile.writeArff(datf, data);
/*  92 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(cons)));
/*  93 */     for (int i = 0; i < constraints.size(); i++) {
/*  94 */       ILevelConstraint ic = constraints.get(i);
/*  95 */       int type = ic.getType();
/*  96 */       int t1 = ic.getT1().getIndex();
/*  97 */       int t2 = ic.getT2().getIndex();
/*  98 */       if (t1 >= t2) {
/*  99 */         int temp = t1;
/* 100 */         t1 = t2;
/* 101 */         t2 = temp;
/*     */       } 
/* 103 */       int mtype = (type == 0) ? 1 : -1;
/* 104 */       if (t1 != t2) {
/* 105 */         wrt.println(t1 + "\t" + t2 + "\t" + mtype);
/*     */       }
/*     */     } 
/* 108 */     wrt.close();
/* 109 */     String script = System.getenv("MPCKMEANS_SCRIPT");
/* 110 */     System.out.println("Running script: " + script);
/* 111 */     if (script == null) return (ClusModel)new SimpleClusterModel(null, getStatManager()); 
/*     */     try {
/* 113 */       String line = "";
/* 114 */       int[] assign = new int[data.getNbRows()];
/* 115 */       Arrays.fill(assign, -1);
/* 116 */       String cmdline = "-D " + datf + " -C " + cons + " -O " + outf;
/* 117 */       Process proc = Runtime.getRuntime().exec(script + " " + cmdline);
/* 118 */       proc.waitFor();
/* 119 */       writeStream(proc.getInputStream());
/* 120 */       writeStream(proc.getErrorStream());
/* 121 */       LineNumberReader rdr = new LineNumberReader(new InputStreamReader(new FileInputStream(outf)));
/* 122 */       while ((line = rdr.readLine()) != null) {
/* 123 */         line = line.trim();
/* 124 */         if (!line.equals("")) {
/* 125 */           String[] arr = line.split("\t");
/* 126 */           if (arr.length != 2) {
/* 127 */             throw new ClusException("MPCKMeans error in output");
/*     */           }
/* 129 */           int idx = Integer.parseInt(arr[0]);
/* 130 */           int cl = Integer.parseInt(arr[1]);
/* 131 */           assign[idx] = cl;
/*     */         } 
/*     */       } 
/* 134 */       rdr.close();
/* 135 */       System.out.println("--------the file" + cons + "is not deleted !!!");
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 140 */       computeRandIndex(data, assign, "All data: ");
/* 141 */       if (test != null) computeRandIndex(test, assign, "Test data: "); 
/* 142 */       return (ClusModel)new SimpleClusterModel(assign, getStatManager());
/* 143 */     } catch (InterruptedException interruptedException) {
/*     */       
/* 145 */       return (ClusModel)new SimpleClusterModel(null, getStatManager());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ilevelc\MPCKMeansWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */