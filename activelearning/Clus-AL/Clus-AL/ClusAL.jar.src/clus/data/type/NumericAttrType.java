/*     */ package clus.data.type;
/*     */ 
/*     */ import clus.algo.kNN.NumericStatistic;
/*     */ import clus.data.cols.ColTarget;
/*     */ import clus.data.cols.attribute.ClusAttribute;
/*     */ import clus.data.cols.attribute.NumericTarget;
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.io.ClusSerializable;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
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
/*     */ public class NumericAttrType
/*     */   extends ClusAttrType
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int THIS_TYPE = 1;
/*     */   public static final String THIS_TYPE_NAME = "Numeric";
/*     */   public static final double MISSING = InfinityD;
/*     */   protected boolean m_Sparse;
/*     */   private NumericStatistic $stat;
/*     */   
/*     */   public NumericAttrType(String name) {
/*  52 */     super(name);
/*     */   }
/*     */   
/*     */   public ClusAttrType cloneType() {
/*  56 */     NumericAttrType at = new NumericAttrType(this.m_Name);
/*  57 */     cloneType(at);
/*  58 */     at.m_Sparse = this.m_Sparse;
/*  59 */     return at;
/*     */   }
/*     */   
/*     */   public boolean isSparse() {
/*  63 */     return this.m_Sparse;
/*     */   }
/*     */   
/*     */   public void setSparse(boolean sparse) {
/*  67 */     this.m_Sparse = sparse;
/*     */   }
/*     */   
/*     */   public int getTypeIndex() {
/*  71 */     return 1;
/*     */   }
/*     */   
/*     */   public String getTypeName() {
/*  75 */     return "Numeric";
/*     */   }
/*     */   
/*     */   public int getValueType() {
/*  79 */     return 1;
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
/*     */   public int getMaxNbStats() {
/*  91 */     return 2;
/*     */   }
/*     */   
/*     */   public String getString(DataTuple tuple) {
/*  95 */     double val = getNumeric(tuple);
/*     */     
/*  97 */     if (getStatus() == 4) {
/*  98 */       return String.valueOf((int)val);
/*     */     }
/* 100 */     return (val == Double.POSITIVE_INFINITY) ? "?" : String.valueOf(val);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMissing(DataTuple tuple) {
/* 105 */     return (tuple.m_Doubles[this.m_ArrayIndex] == Double.POSITIVE_INFINITY);
/*     */   }
/*     */   
/*     */   public double getNumeric(DataTuple tuple) {
/* 109 */     return tuple.getDoubleVal(this.m_ArrayIndex);
/*     */   }
/*     */   
/*     */   public void setNumeric(DataTuple tuple, double value) {
/* 113 */     tuple.setDoubleVal(value, this.m_ArrayIndex);
/*     */   }
/*     */   
/*     */   public int compareValue(DataTuple t1, DataTuple t2) {
/* 117 */     double v1 = t1.m_Doubles[this.m_ArrayIndex];
/* 118 */     double v2 = t2.m_Doubles[this.m_ArrayIndex];
/* 119 */     if (v1 == v2) return 0; 
/* 120 */     return (v1 > v2) ? 1 : -1;
/*     */   }
/*     */   
/*     */   public ClusAttribute createTargetAttr(ColTarget target) {
/* 124 */     return (ClusAttribute)new NumericTarget(target, this, getArrayIndex());
/*     */   }
/*     */   
/*     */   public void writeARFFType(PrintWriter wrt) throws ClusException {
/* 128 */     wrt.print("numeric");
/*     */   }
/*     */   
/*     */   public ClusSerializable createRowSerializable() throws ClusException {
/* 132 */     return new MySerializable();
/*     */   }
/*     */   
/*     */   public class MySerializable extends ClusSerializable { public int m_NbZero;
/*     */     public int m_NbNeg;
/*     */     public int m_NbTotal;
/*     */     
/*     */     public boolean read(ClusReader data, DataTuple tuple) throws IOException {
/* 140 */       if (!data.readNoSpace()) return false; 
/* 141 */       double val = data.getFloat();
/* 142 */       tuple.setDoubleVal(val, NumericAttrType.this.getArrayIndex());
/* 143 */       if (val == Double.POSITIVE_INFINITY) {
/* 144 */         NumericAttrType.this.incNbMissing();
/* 145 */         this.m_NbZero++;
/*     */       } 
/* 147 */       if (val == 0.0D) {
/* 148 */         this.m_NbZero++;
/* 149 */       } else if (val < 0.0D) {
/* 150 */         this.m_NbNeg++;
/*     */       } 
/* 152 */       this.m_NbTotal++;
/* 153 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public void term(ClusSchema schema) {
/* 158 */       if (this.m_NbNeg == 0 && this.m_NbZero > this.m_NbTotal * 5 / 10) {
/* 159 */         NumericAttrType.this.setSparse(true);
/*     */       }
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatistic(NumericStatistic stat) {
/* 167 */     this.$stat = stat;
/*     */   }
/*     */   public NumericStatistic getStatistic() {
/* 170 */     return this.$stat;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\type\NumericAttrType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */