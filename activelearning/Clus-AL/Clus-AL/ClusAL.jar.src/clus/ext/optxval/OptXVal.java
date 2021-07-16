/*     */ package clus.ext.optxval;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.data.ClusData;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.main.Settings;
/*     */ import clus.selection.XValMainSelection;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Date;
/*     */ import jeans.util.array.MDoubleArray;
/*     */ import jeans.util.cmdline.CMDLineArgs;
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
/*     */ 
/*     */ 
/*     */ public class OptXVal
/*     */ {
/*     */   protected Clus m_Clus;
/*     */   
/*     */   public OptXVal(Clus clus) {
/*  49 */     this.m_Clus = clus;
/*     */   }
/*     */   
/*     */   public ClusInductionAlgorithm createInduce(ClusSchema schema, Settings sett, CMDLineArgs cargs) throws ClusException, IOException {
/*  53 */     schema.addIndices(0);
/*  54 */     int nb_num = schema.getNbNumericDescriptiveAttributes();
/*  55 */     if (Settings.XVAL_OVERLAP && nb_num > 0) return new OptXValIndOV(schema, sett); 
/*  56 */     return new OptXValIndNO(schema, sett);
/*     */   }
/*     */   
/*     */   public final void addFoldNrs(RowData set, XValMainSelection sel) {
/*  60 */     int nb = set.getNbRows();
/*  61 */     for (int i = 0; i < nb; i++) {
/*  62 */       int fold = sel.getFold(i);
/*  63 */       DataTuple tuple = set.getTuple(i);
/*  64 */       tuple.setIndex(fold + 1);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static final void showFoldsInfo(PrintWriter writer, Object root) {
/*  69 */     OptXValBinTree bintree = OptXValBinTree.convertTree(root);
/*     */     
/*  71 */     double[] fis = bintree.getFIs();
/*  72 */     double[] nodes = bintree.getNodes();
/*  73 */     double[] times = bintree.getTimes();
/*  74 */     MDoubleArray.divide(fis, nodes);
/*     */     
/*  76 */     writer.println("FoldsInfo");
/*  77 */     writer.println("Nodes:  " + MDoubleArray.toString(nodes));
/*  78 */     writer.println("f(i-1): " + MDoubleArray.toString(fis));
/*  79 */     writer.println("Time:   " + MDoubleArray.toString(times));
/*     */   }
/*     */   
/*     */   public static final void showForest(PrintWriter writer, OptXValNode root) {
/*  83 */     writer.println("XVal Forest");
/*  84 */     writer.println("***********");
/*  85 */     writer.println();
/*  86 */     showFoldsInfo(writer, root);
/*  87 */     writer.println();
/*  88 */     root.printTree(writer, "");
/*  89 */     writer.println();
/*     */   }
/*     */   
/*     */   public final void xvalRun(String appname, Date date) throws IOException, ClusException {
/*  93 */     Settings sett = this.m_Clus.getSettings();
/*  94 */     ClusSchema schema = this.m_Clus.getSchema();
/*  95 */     RowData set = this.m_Clus.getRowDataClone();
/*  96 */     XValMainSelection sel = schema.getXValSelection((ClusData)set);
/*  97 */     addFoldNrs(set, sel);
/*  98 */     OptXValInduce induce = (OptXValInduce)this.m_Clus.getInduce();
/*  99 */     induce.initialize(sel.getNbFolds());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 105 */     OptXValNode root = null;
/* 106 */     int nbr = 0;
/*     */     while (true) {
/* 108 */       root = induce.optXVal(set);
/* 109 */       nbr++;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\optxval\OptXVal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */