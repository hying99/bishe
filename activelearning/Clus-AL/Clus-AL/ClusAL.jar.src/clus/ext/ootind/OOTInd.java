/*     */ package clus.ext.ootind;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.data.ClusData;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.optxval.OptXValNode;
/*     */ import clus.main.Settings;
/*     */ import clus.selection.BaggingSelection;
/*     */ import clus.selection.XValMainSelection;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.Date;
/*     */ import jeans.util.MyArray;
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
/*     */ 
/*     */ public class OOTInd
/*     */ {
/*     */   protected Clus m_Clus;
/*     */   
/*     */   public OOTInd(Clus clus) {
/*  51 */     this.m_Clus = clus;
/*     */   }
/*     */   
/*     */   public ClusInductionAlgorithm createInduce(ClusSchema schema, Settings sett, CMDLineArgs cargs) throws ClusException, IOException {
/*  55 */     schema.addIndices(0);
/*  56 */     int nb_num = schema.getNbNumericDescriptiveAttributes();
/*  57 */     if (Settings.XVAL_OVERLAP && nb_num > 0) return new OOTIndOV(schema, sett); 
/*  58 */     return new OOTIndNO(schema, sett);
/*     */   }
/*     */   
/*     */   public final void addFoldNrs(RowData set, XValMainSelection sel) {
/*  62 */     int nb = set.getNbRows();
/*  63 */     int nbf = sel.getNbFolds() + 1;
/*  64 */     for (int i = 0; i < nb; i++) {
/*  65 */       int fold = sel.getFold(i) + 1;
/*  66 */       DataTuple tuple = set.getTuple(i);
/*  67 */       tuple.m_Folds = new int[nbf];
/*  68 */       tuple.m_Folds[0] = 1;
/*  69 */       for (int j = 1; j < nbf; j++) {
/*  70 */         if (j != fold) tuple.m_Folds[j] = 1; 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public final void addSetNrs(RowData set, MyArray sels, int nbsets) {
/*  75 */     int nb = set.getNbRows();
/*  76 */     for (int i = 0; i < nb; i++) {
/*  77 */       DataTuple tuple = set.getTuple(i);
/*  78 */       tuple.m_Folds = new int[nbsets];
/*  79 */       for (int j = 0; j < nbsets; j++) {
/*  80 */         BaggingSelection sel = (BaggingSelection)sels.elementAt(j);
/*  81 */         tuple.m_Folds[j] = sel.getCount(i);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void baggingRun(String appname, Date date) throws IOException, ClusException {
/*  87 */     Settings sett = this.m_Clus.getSettings();
/*  88 */     ClusSchema schema = this.m_Clus.getSchema();
/*  89 */     RowData set = this.m_Clus.getRowDataClone();
/*     */     
/*  91 */     int nbrows = set.getNbRows();
/*  92 */     int nbsets = sett.getBaggingSets();
/*  93 */     MyArray sels = new MyArray();
/*  94 */     for (int i = 0; i < nbsets; i++) {
/*  95 */       sels.addElement(new BaggingSelection(nbrows, this.m_Clus.getSettings().getEnsembleBagSize()));
/*     */     }
/*     */     
/*  98 */     addSetNrs(set, sels, nbsets);
/*     */     
/* 100 */     OOTInduce induce = (OOTInduce)this.m_Clus.getInduce();
/* 101 */     induce.initialize(nbsets);
/* 102 */     induce.SHOULD_OPTIMIZE = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 109 */     OptXValNode root = null;
/* 110 */     int nbr = 0;
/*     */     while (true) {
/* 112 */       root = induce.ootInduce(set);
/* 113 */       nbr++;
/*     */     } 
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void xvalRun(String appname, Date date) throws IOException, ClusException {
/* 150 */     Settings sett = this.m_Clus.getSettings();
/* 151 */     ClusSchema schema = this.m_Clus.getSchema();
/* 152 */     RowData set = this.m_Clus.getRowDataClone();
/* 153 */     XValMainSelection sel = schema.getXValSelection((ClusData)set);
/* 154 */     addFoldNrs(set, sel);
/* 155 */     OOTInduce induce = (OOTInduce)this.m_Clus.getInduce();
/* 156 */     induce.initialize(sel.getNbFolds() + 1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 162 */     OptXValNode root = null;
/* 163 */     int nbr = 0;
/*     */     while (true) {
/* 165 */       root = induce.ootInduce(set);
/* 166 */       nbr++;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ootind\OOTInd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */