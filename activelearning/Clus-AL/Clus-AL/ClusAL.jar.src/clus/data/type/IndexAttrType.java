/*     */ package clus.data.type;
/*     */ 
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.io.ClusSerializable;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
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
/*     */ public class IndexAttrType
/*     */   extends ClusAttrType
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int THIS_TYPE = 2;
/*     */   public static final String THIS_TYPE_NAME = "Index";
/*     */   protected int m_CrValue;
/*     */   protected int[] m_Index;
/*  42 */   protected int m_Max = Integer.MIN_VALUE;
/*  43 */   protected int m_Min = Integer.MAX_VALUE;
/*     */   
/*     */   public IndexAttrType(String name) {
/*  46 */     super(name);
/*     */   }
/*     */   
/*     */   public IndexAttrType(String name, int min, int max) {
/*  50 */     super(name);
/*  51 */     this.m_Max = max;
/*  52 */     this.m_Min = min;
/*     */   }
/*     */   
/*     */   public ClusAttrType cloneType() {
/*  56 */     IndexAttrType at = new IndexAttrType(this.m_Name, this.m_Min, this.m_Max);
/*  57 */     cloneType(at);
/*  58 */     return at;
/*     */   }
/*     */   
/*     */   public int getCrValue() {
/*  62 */     return this.m_CrValue;
/*     */   }
/*     */   
/*     */   public int getTypeIndex() {
/*  66 */     return 2;
/*     */   }
/*     */   
/*     */   public String getTypeName() {
/*  70 */     return "Index";
/*     */   }
/*     */   
/*     */   public int getValueType() {
/*  74 */     return 1;
/*     */   }
/*     */   
/*     */   public void setNbRows(int nb) {
/*  78 */     this.m_Index = new int[nb];
/*     */   }
/*     */   
/*     */   public void setValue(int row, int value) {
/*  82 */     this.m_Index[row] = value;
/*  83 */     if (value > this.m_Max) this.m_Max = value; 
/*  84 */     if (value < this.m_Min) this.m_Min = value; 
/*     */   }
/*     */   
/*     */   public int getMaxValue() {
/*  88 */     return this.m_Max;
/*     */   }
/*     */   
/*     */   public int getValue(int row) {
/*  92 */     return this.m_Index[row];
/*     */   }
/*     */   
/*     */   public int getNbRows() {
/*  96 */     return this.m_Index.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusSerializable createRowSerializable(RowData data, boolean istarget) throws ClusException {
/* 105 */     return new MyReader();
/*     */   }
/*     */   
/*     */   public class MyReader
/*     */     extends ClusSerializable {
/*     */     public int getValue(ClusReader data, String value) throws IOException {
/*     */       try {
/* 112 */         int ival = Integer.parseInt(value);
/* 113 */         if (ival > IndexAttrType.this.m_Max) IndexAttrType.this.m_Max = ival; 
/* 114 */         if (ival < IndexAttrType.this.m_Min) IndexAttrType.this.m_Min = ival; 
/* 115 */         return ival;
/* 116 */       } catch (NumberFormatException e) {
/* 117 */         throw new IOException("Illegal value '" + value + "' for attribute " + IndexAttrType.this.getName() + " at row " + (data.getRow() + 1));
/*     */       } 
/*     */     }
/*     */     
/*     */     public boolean read(ClusReader data, int row) throws IOException {
/* 122 */       String value = data.readString();
/* 123 */       if (value == null) return false; 
/* 124 */       IndexAttrType.this.m_Index[row] = getValue(data, value);
/* 125 */       return true;
/*     */     }
/*     */     
/*     */     public boolean read(ClusReader data, DataTuple tuple) throws IOException {
/* 129 */       String value = data.readString();
/* 130 */       if (value == null) return false; 
/* 131 */       IndexAttrType.this.m_CrValue = getValue(data, value);
/* 132 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\type\IndexAttrType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */