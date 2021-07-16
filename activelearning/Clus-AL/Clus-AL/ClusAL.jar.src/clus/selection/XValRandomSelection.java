/*     */ package clus.selection;
/*     */ 
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusRandom;
/*     */ import java.util.Random;
/*     */ import jeans.util.array.MyIntArray;
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
/*     */ public class XValRandomSelection
/*     */   extends XValMainSelection
/*     */ {
/*     */   protected int[] m_Selection;
/*     */   protected Random m_Random;
/*     */   
/*     */   public XValRandomSelection(int nbtot, int folds) throws ClusException {
/*  44 */     this(nbtot, folds, ClusRandom.getRandom(1));
/*     */   }
/*     */   
/*     */   public XValRandomSelection(int nbtot, int folds, Random random) throws ClusException {
/*  48 */     super(folds, nbtot);
/*  49 */     if (folds == nbtot) {
/*  50 */       createLeaveOneOutXVAL(nbtot);
/*     */     } else {
/*  52 */       createRegularXVAL(nbtot, folds, random);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getFold(int row) {
/*  57 */     return this.m_Selection[row];
/*     */   }
/*     */   
/*     */   public void printDebug() {
/*  61 */     System.out.println("XVAL: " + MyIntArray.print(this.m_Selection));
/*     */   }
/*     */   
/*     */   public void createLeaveOneOutXVAL(int nbtot) {
/*  65 */     this.m_Selection = new int[nbtot];
/*  66 */     for (int i = 0; i < nbtot; i++) {
/*  67 */       this.m_Selection[i] = i;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void createRegularXVAL(int nbtot, int folds, Random random) throws ClusException {
/*  73 */     this.m_Random = random;
/*  74 */     int max = nbtot / folds;
/*  75 */     XValGroup[] grps = new XValGroup[folds];
/*  76 */     for (int i = 0; i < folds; ) { grps[i] = new XValGroup(max + 1); i++; }
/*  77 */      int from = devide2(grps, 0, nbtot, max);
/*  78 */     if (from != -1) {
/*  79 */       int ok = devide2(grps, from, nbtot, max + 1);
/*  80 */       if (ok != -1) throw new ClusException("Error partitioning xval data"); 
/*     */     } 
/*  82 */     this.m_Selection = new int[nbtot];
/*  83 */     for (int j = 0; j < folds; j++) {
/*  84 */       XValGroup gr = grps[j];
/*  85 */       for (int k = 0; k < gr.getNbElements(); k++) {
/*  86 */         this.m_Selection[gr.getElement(k)] = j;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int devide2(XValGroup[] grps, int from, int till, int max) {
/*  95 */     while (from < till) {
/*  96 */       int grp = this.m_Random.nextInt(grps.length);
/*  97 */       if (add_to_group(from, grps, grp, max)) {
/*  98 */         from++; continue;
/*     */       } 
/* 100 */       return from;
/*     */     } 
/*     */     
/* 103 */     return -1;
/*     */   }
/*     */   
/*     */   public boolean add_to_group(int from, XValGroup[] grps, int grp, int max) {
/* 107 */     int nbg = grps.length;
/* 108 */     int ctr = 0;
/* 109 */     while (ctr < nbg) {
/* 110 */       XValGroup gr = grps[grp];
/* 111 */       if (gr.add(from, max)) return true; 
/* 112 */       grp = (grp + 1) % nbg;
/* 113 */       ctr++;
/*     */     } 
/* 115 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\selection\XValRandomSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */