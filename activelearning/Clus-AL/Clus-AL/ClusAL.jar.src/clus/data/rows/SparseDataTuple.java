/*     */ package clus.data.rows;
/*     */ 
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.SparseNumericAttrType;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
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
/*     */ public class SparseDataTuple
/*     */   extends DataTuple
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  35 */   protected HashMap m_Map = new HashMap<>();
/*     */   
/*     */   public SparseDataTuple(ClusSchema schema) {
/*  38 */     super(schema);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDoubleValueSparse(Double val, Integer index) {
/*  46 */     this.m_Map.put(index, val);
/*     */   }
/*     */   
/*     */   public double getDoubleValueSparse(int index) {
/*  50 */     Double value = (Double)this.m_Map.get(new Integer(index));
/*  51 */     return (value != null) ? value.doubleValue() : 0.0D;
/*     */   }
/*     */   
/*     */   public double getDoubleValueSparse(Integer index) {
/*  55 */     Double value = (Double)this.m_Map.get(index);
/*  56 */     return (value != null) ? value.doubleValue() : 0.0D;
/*     */   }
/*     */   
/*     */   public Object[] getAttributeIndices() {
/*  60 */     return this.m_Map.keySet().toArray();
/*     */   }
/*     */   
/*     */   public void addExampleToAttributes() {
/*  64 */     Object[] indices = getAttributeIndices();
/*  65 */     for (int i = 0; i < indices.length; i++) {
/*  66 */       int index = ((Integer)indices[i]).intValue();
/*  67 */       SparseNumericAttrType attr = (SparseNumericAttrType)getSchema().getAttrType(index);
/*  68 */       attr.addExample(this);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final SparseDataTuple cloneTuple() {
/*  73 */     SparseDataTuple res = new SparseDataTuple();
/*  74 */     cloneTuple(res);
/*  75 */     res.m_Map = this.m_Map;
/*  76 */     return res;
/*     */   }
/*     */   
/*     */   public SparseDataTuple deepCloneTuple() {
/*  80 */     SparseDataTuple res = new SparseDataTuple();
/*  81 */     if (this.m_Ints != null) {
/*  82 */       res.m_Ints = new int[this.m_Ints.length];
/*  83 */       System.arraycopy(this.m_Ints, 0, res.m_Ints, 0, this.m_Ints.length);
/*     */     } 
/*  85 */     if (this.m_Doubles != null) {
/*  86 */       res.m_Doubles = new double[this.m_Doubles.length];
/*  87 */       System.arraycopy(this.m_Doubles, 0, res.m_Doubles, 0, this.m_Doubles.length);
/*     */     } 
/*  89 */     if (this.m_Objects != null) {
/*  90 */       res.m_Objects = new Object[this.m_Objects.length];
/*  91 */       System.arraycopy(this.m_Objects, 0, res.m_Objects, 0, this.m_Objects.length);
/*     */     } 
/*  93 */     res.m_Weight = this.m_Weight;
/*  94 */     res.m_Index = this.m_Index;
/*  95 */     res.m_Folds = this.m_Folds;
/*  96 */     res.m_Schema = this.m_Schema;
/*  97 */     res.m_Map = this.m_Map;
/*  98 */     return res;
/*     */   }
/*     */   
/*     */   public SparseDataTuple deepActiveCloneTuple() {
/* 102 */     SparseDataTuple res = new SparseDataTuple();
/* 103 */     if (this.m_Ints != null) {
/* 104 */       res.m_Ints = new int[this.m_Ints.length];
/* 105 */       System.arraycopy(this.m_Ints, 0, res.m_Ints, 0, this.m_Ints.length);
/*     */     } 
/* 107 */     if (this.m_Doubles != null) {
/* 108 */       res.m_Doubles = new double[this.m_Doubles.length];
/* 109 */       System.arraycopy(this.m_Doubles, 0, res.m_Doubles, 0, this.m_Doubles.length);
/*     */     } 
/* 111 */     if (this.m_Objects != null) {
/* 112 */       res.m_Objects = new Object[this.m_Objects.length];
/* 113 */       System.arraycopy(this.m_Objects, 0, res.m_Objects, 0, this.m_Objects.length);
/*     */     } 
/* 115 */     res.m_Weight = this.m_Weight;
/* 116 */     res.m_Index = this.m_Index;
/* 117 */     res.m_Folds = this.m_Folds;
/* 118 */     res.m_Schema = this.m_Schema;
/* 119 */     res.m_Map = this.m_Map;
/* 120 */     res.m_OracleAnswer = this.m_OracleAnswer;
/* 121 */     res.m_ActiveIndex = this.m_ActiveIndex;
/*     */     
/* 123 */     return res;
/*     */   }
/*     */   
/*     */   public void writeTuple(PrintWriter wrt) {
/* 127 */     ClusSchema schema = getSchema();
/* 128 */     int aidx = 0;
/* 129 */     wrt.print("{");
/*     */     
/* 131 */     Iterator<Integer> it = this.m_Map.keySet().iterator();
/* 132 */     while (it.hasNext()) {
/* 133 */       Integer idx = it.next();
/* 134 */       ClusAttrType clusAttrType = schema.getAttrType(idx.intValue());
/* 135 */       if (!clusAttrType.isDisabled()) {
/* 136 */         if (aidx != 0) {
/* 137 */           wrt.print(",");
/*     */         }
/* 139 */         int nidx = idx.intValue() + 1;
/* 140 */         wrt.print(nidx + " " + clusAttrType.getString(this));
/* 141 */         aidx++;
/*     */       } 
/*     */     } 
/*     */     
/* 145 */     ClusAttrType[] type = schema.getNonSparseAttributes();
/* 146 */     for (int i = 0; i < type.length; i++) {
/* 147 */       if (!type[i].isDisabled()) {
/* 148 */         if (aidx != 0) {
/* 149 */           wrt.print(",");
/*     */         }
/* 151 */         int nidx = type[i].getIndex() + 1;
/* 152 */         wrt.print(nidx + " " + type[i].getString(this));
/* 153 */         aidx++;
/*     */       } 
/*     */     } 
/* 156 */     wrt.println("}");
/*     */   }
/*     */   
/*     */   public SparseDataTuple() {}
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\rows\SparseDataTuple.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */