/*     */ package clus.ext.sspd;
/*     */ 
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.IntegerAttrType;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClusDistance;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import jeans.math.matrix.MSymMatrix;
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
/*     */ public class SSPDMatrix
/*     */   extends ClusDistance
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected MSymMatrix m_Matrix;
/*     */   protected IntegerAttrType m_Target;
/*     */   
/*     */   public SSPDMatrix(int size) {
/*  79 */     this.m_Matrix = new MSymMatrix(size);
/*     */   }
/*     */   
/*     */   public void setTarget(ClusAttrType[] target) throws ClusException {
/*  83 */     if (target.length != 1) {
/*  84 */       throw new ClusException("Only one target allowed in SSPD modus");
/*     */     }
/*  86 */     this.m_Target = (IntegerAttrType)target[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public static SSPDMatrix read(String filename, Settings sett) throws IOException {
/*  91 */     ClusReader reader = new ClusReader(filename, sett);
/*  92 */     int nb = 0;
/*  93 */     while (!reader.isEol()) {
/*  94 */       reader.readFloat();
/*  95 */       nb++;
/*     */     } 
/*  97 */     System.out.println("Loading SSPD Matrix: " + filename + " (Size: " + nb + ")");
/*  98 */     SSPDMatrix matrix = new SSPDMatrix(nb);
/*  99 */     reader.reOpen();
/* 100 */     for (int i = 0; i < nb; i++) {
/* 101 */       for (int j = 0; j < nb; j++) {
/* 102 */         double value = reader.readFloat();
/* 103 */         matrix.m_Matrix.add_sym(i, j, value * value);
/*     */       } 
/* 105 */       if (!reader.isEol()) throw new IOException("SSPD Matrix is not square"); 
/*     */     } 
/* 107 */     reader.close();
/*     */ 
/*     */     
/* 110 */     return matrix;
/*     */   }
/*     */   
/*     */   public double calcDistance(DataTuple t1, DataTuple t2) {
/* 114 */     int idx = this.m_Target.getArrayIndex();
/* 115 */     int i1 = t1.getIntVal(idx);
/* 116 */     int i2 = t2.getIntVal(idx);
/* 117 */     return this.m_Matrix.get(i1, i2);
/*     */   }
/*     */   
/*     */   public String getDistanceName() {
/* 121 */     return "SSPD Matrix";
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\sspd\SSPDMatrix.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */