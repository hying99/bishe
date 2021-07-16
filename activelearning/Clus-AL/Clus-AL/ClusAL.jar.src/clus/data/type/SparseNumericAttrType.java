/*     */ package clus.data.type;
/*     */ 
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.SparseDataTuple;
/*     */ import clus.io.ClusSerializable;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SparseNumericAttrType
/*     */   extends NumericAttrType
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected Integer m_IntIndex;
/*     */   protected ArrayList<SparseDataTuple> m_Examples;
/*     */   protected double m_ExampleWeight;
/*     */   
/*     */   public SparseNumericAttrType(String name) {
/*  23 */     super(name);
/*  24 */     setSparse(true);
/*  25 */     this.m_Examples = new ArrayList<>();
/*     */   }
/*     */   
/*     */   public SparseNumericAttrType(NumericAttrType type) {
/*  29 */     super(type.getName());
/*  30 */     setIndex(type.getIndex());
/*  31 */     setSparse(true);
/*  32 */     this.m_Examples = new ArrayList<>();
/*     */   }
/*     */   
/*     */   public SparseNumericAttrType cloneType() {
/*  36 */     SparseNumericAttrType at = new SparseNumericAttrType(this.m_Name);
/*  37 */     cloneType(at);
/*  38 */     at.setIndex(getIndex());
/*  39 */     at.m_Sparse = this.m_Sparse;
/*  40 */     at.setExamples(getExamples());
/*  41 */     at.m_ExampleWeight = this.m_ExampleWeight;
/*  42 */     return at;
/*     */   }
/*     */   
/*     */   public void setIndex(int idx) {
/*  46 */     this.m_Index = idx;
/*  47 */     this.m_IntIndex = new Integer(idx);
/*     */   }
/*     */   
/*     */   public int getValueType() {
/*  51 */     return -1;
/*     */   }
/*     */   
/*     */   public ArrayList getExamples() {
/*  55 */     return this.m_Examples;
/*     */   }
/*     */   
/*     */   public double getExampleWeight() {
/*  59 */     return this.m_ExampleWeight;
/*     */   }
/*     */   
/*     */   public void setExamples(ArrayList<SparseDataTuple> ex) {
/*  63 */     this.m_Examples = ex;
/*     */   }
/*     */   
/*     */   public void resetExamples() {
/*  67 */     this.m_Examples = new ArrayList<>();
/*  68 */     this.m_ExampleWeight = 0.0D;
/*     */   }
/*     */   
/*     */   public void addExample(SparseDataTuple tuple) {
/*  72 */     this.m_Examples.add(tuple);
/*  73 */     this.m_ExampleWeight += tuple.getWeight();
/*     */   }
/*     */   
/*     */   public ArrayList pruneExampleList(RowData data) {
/*  77 */     ArrayList<SparseDataTuple> dataList = data.toArrayList();
/*  78 */     ArrayList<SparseDataTuple> newExamples = new ArrayList<>();
/*  79 */     for (int i = 0; i < this.m_Examples.size(); i++) {
/*  80 */       if (dataList.contains(this.m_Examples.get(i))) {
/*  81 */         newExamples.add(this.m_Examples.get(i));
/*     */       }
/*     */     } 
/*  84 */     return newExamples;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getNumeric(DataTuple tuple) {
/*  90 */     return ((SparseDataTuple)tuple).getDoubleValueSparse(getIndex());
/*     */   }
/*     */   
/*     */   public boolean isMissing(DataTuple tuple) {
/*  94 */     return (((SparseDataTuple)tuple).getDoubleValueSparse(this.m_IntIndex) == Double.POSITIVE_INFINITY);
/*     */   }
/*     */   
/*  97 */   protected static final Double[] DOUBLES = createPredefinedDoubles();
/*     */   
/*     */   protected static Double[] createPredefinedDoubles() {
/* 100 */     Double[] values = new Double[10];
/* 101 */     for (int i = 0; i < values.length; i++) {
/* 102 */       values[i] = new Double(i);
/*     */     }
/* 104 */     return values;
/*     */   }
/*     */   
/*     */   public void setNumeric(DataTuple tuple, double value) {
/* 108 */     Double d_value = null;
/* 109 */     for (int i = 0; i < DOUBLES.length; i++) {
/* 110 */       if (DOUBLES[i].doubleValue() == value) {
/* 111 */         d_value = DOUBLES[i];
/*     */       }
/*     */     } 
/* 114 */     if (d_value == null) {
/* 115 */       d_value = new Double(value);
/*     */     }
/* 117 */     ((SparseDataTuple)tuple).setDoubleValueSparse(d_value, this.m_IntIndex);
/*     */   }
/*     */   
/*     */   public ClusSerializable createRowSerializable() throws ClusException {
/* 121 */     return new MySerializable();
/*     */   }
/*     */   
/*     */   public class MySerializable
/*     */     extends ClusSerializable {
/*     */     public boolean read(ClusReader data, DataTuple tuple) throws IOException {
/* 127 */       if (!data.readNoSpace()) {
/* 128 */         return false;
/*     */       }
/* 130 */       double value = data.getFloat();
/* 131 */       SparseNumericAttrType.this.setNumeric(tuple, value);
/*     */ 
/*     */       
/* 134 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\type\SparseNumericAttrType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */