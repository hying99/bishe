/*     */ package clus.data.type;
/*     */ 
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.ext.timeseries.TimeSeries;
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
/*     */ public class TimeSeriesAttrType
/*     */   extends ClusAttrType
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final String THIS_TYPE_NAME = "TimeSeries";
/*     */   public static final int THIS_TYPE = 5;
/*     */   public static boolean m_isEqualLength = true;
/*  43 */   int m_Length = -1;
/*     */   
/*     */   public TimeSeriesAttrType(String name) {
/*  46 */     super(name);
/*     */   }
/*     */   
/*     */   public ClusAttrType cloneType() {
/*  50 */     TimeSeriesAttrType tsat = new TimeSeriesAttrType(this.m_Name);
/*  51 */     return tsat;
/*     */   }
/*     */   
/*     */   public int getTypeIndex() {
/*  55 */     return 5;
/*     */   }
/*     */   
/*     */   public int getValueType() {
/*  59 */     return 2;
/*     */   }
/*     */   
/*     */   public String getTypeName() {
/*  63 */     return "TimeSeries";
/*     */   }
/*     */   
/*     */   public TimeSeries getTimeSeries(DataTuple tuple) {
/*  67 */     return (TimeSeries)tuple.getObjVal(this.m_ArrayIndex);
/*     */   }
/*     */   
/*     */   public void setTimeSeries(DataTuple tuple, TimeSeries value) {
/*  71 */     tuple.setObjectVal(value, this.m_ArrayIndex);
/*     */   }
/*     */   
/*     */   public String getString(DataTuple tuple) {
/*  75 */     TimeSeries ts_data = (TimeSeries)tuple.getObjVal(0);
/*  76 */     return ts_data.toString();
/*     */   }
/*     */   
/*     */   public ClusSerializable createRowSerializable() throws ClusException {
/*  80 */     return new MySerializable();
/*     */   }
/*     */   
/*     */   public boolean isEqualLength() {
/*  84 */     return m_isEqualLength;
/*     */   }
/*     */   
/*     */   public class MySerializable
/*     */     extends ClusSerializable {
/*     */     public String getString(DataTuple tuple) {
/*  90 */       TimeSeries ts_data = (TimeSeries)tuple.getObjVal(0);
/*  91 */       double[] data = ts_data.getValues();
/*  92 */       String str = "[";
/*  93 */       for (int k = 0; k < data.length; k++) {
/*  94 */         str.concat(String.valueOf(data[k]));
/*  95 */         if (k < data.length - 1) str.concat(", "); 
/*     */       } 
/*  97 */       str.concat("]");
/*  98 */       return str;
/*     */     }
/*     */     
/*     */     public boolean read(ClusReader data, DataTuple tuple) throws IOException {
/* 102 */       String str = data.readTimeSeries();
/* 103 */       if (str == null) return false; 
/* 104 */       TimeSeries value = new TimeSeries(str);
/* 105 */       tuple.setObjectVal(value, 0);
/* 106 */       if (TimeSeriesAttrType.this.m_Length != -1) {
/* 107 */         if (TimeSeriesAttrType.this.m_Length != value.length()) TimeSeriesAttrType.m_isEqualLength = false; 
/*     */       } else {
/* 109 */         TimeSeriesAttrType.this.m_Length = value.length();
/*     */       } 
/* 111 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeARFFType(PrintWriter wrt) throws ClusException {
/* 116 */     wrt.print("TimeSeries");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\type\TimeSeriesAttrType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */