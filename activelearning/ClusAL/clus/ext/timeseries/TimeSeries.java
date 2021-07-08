/*     */ package clus.ext.timeseries;
/*     */ 
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.Serializable;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.StringTokenizer;
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
/*     */ public class TimeSeries
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   private double[] m_Values;
/*     */   private double m_TSWeight;
/*     */   
/*     */   public TimeSeries(String values) {
/*  40 */     values = values.trim();
/*  41 */     values = values.replace("[", "");
/*  42 */     values = values.replace("]", "");
/*     */ 
/*     */     
/*  45 */     StringTokenizer st = new StringTokenizer(values, ",");
/*  46 */     this.m_Values = new double[st.countTokens()];
/*  47 */     int i = 0;
/*  48 */     while (st.hasMoreTokens()) {
/*  49 */       this.m_Values[i++] = Double.parseDouble(st.nextToken());
/*     */     }
/*     */   }
/*     */   
/*     */   public TimeSeries(double[] values) {
/*  54 */     this.m_Values = new double[values.length];
/*  55 */     System.arraycopy(values, 0, this.m_Values, 0, values.length);
/*     */   }
/*     */   
/*     */   public TimeSeries(int size) {
/*  59 */     this.m_Values = new double[size];
/*  60 */     for (int i = 0; i < size; ) { this.m_Values[i] = 0.0D; i++; }
/*     */   
/*     */   }
/*     */   public TimeSeries(TimeSeries series) {
/*  64 */     this(series.getValues());
/*     */   }
/*     */   
/*     */   public int length() {
/*  68 */     if (this.m_Values == null)
/*  69 */       return 0; 
/*  70 */     return this.m_Values.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] getValues() {
/*  75 */     double[] result = new double[this.m_Values.length];
/*  76 */     System.arraycopy(this.m_Values, 0, result, 0, this.m_Values.length);
/*  77 */     return result;
/*     */   }
/*     */   
/*     */   public double[] getValuesNoCopy() {
/*  81 */     return this.m_Values;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getValue(int index) {
/*  89 */     return this.m_Values[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSize(int size) {
/*  96 */     this.m_Values = new double[size];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resize(int newSize, String method) {
/* 105 */     double[] oldValues = getValues();
/* 106 */     int oldSize = length();
/* 107 */     double[] values = new double[newSize];
/*     */ 
/*     */     
/* 110 */     double precision = 1.0E-8D;
/* 111 */     if (method.compareTo("linear") == 0) {
/* 112 */       for (int i = 0; i < newSize; i++) {
/* 113 */         int tmpOriginal = (int)Math.floor((i * oldSize / newSize) + precision);
/* 114 */         double w = (i * oldSize / newSize - tmpOriginal);
/* 115 */         if (Math.abs(w) < precision) {
/* 116 */           values[i] = oldValues[tmpOriginal];
/*     */         } else {
/*     */           
/* 119 */           values[i] = oldValues[tmpOriginal] * (1.0D - w) + w * oldValues[tmpOriginal + 1];
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rescale(double min, double max) {
/* 131 */     double tmpMin = min();
/* 132 */     double tmpMax = max();
/* 133 */     if (tmpMax == tmpMin) { for (int i = 0; i < length(); ) { this.m_Values[i] = (max - min) / 2.0D; i++; }  }
/* 134 */     else { for (int i = 0; i < length(); ) { this.m_Values[i] = (this.m_Values[i] - tmpMin) / (tmpMax - tmpMin) * (max - min) + min; i++; }
/*     */        }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double min() {
/* 142 */     double r = Double.POSITIVE_INFINITY;
/* 143 */     for (int i = 0; i < length(); i++) {
/* 144 */       if (r > this.m_Values[i]) r = this.m_Values[i]; 
/*     */     } 
/* 146 */     return r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double max() {
/* 154 */     double r = Double.NEGATIVE_INFINITY;
/* 155 */     for (int i = 0; i < length(); i++) {
/* 156 */       if (r < this.m_Values[i]) r = this.m_Values[i]; 
/*     */     } 
/* 158 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValues(double[] values) {
/* 163 */     System.arraycopy(values, 0, this.m_Values, 0, values.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(int index, double value) {
/* 171 */     this.m_Values[index] = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 180 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 181 */     StringBuffer a = new StringBuffer("[");
/* 182 */     for (int i = 0; i < length() - 1; i++) {
/* 183 */       a.append(fr.format(this.m_Values[i]));
/* 184 */       a.append(',');
/*     */     } 
/* 186 */     if (length() > 0)
/* 187 */       a.append(fr.format(this.m_Values[length() - 1])); 
/* 188 */     a.append(']');
/* 189 */     return a.toString();
/*     */   }
/*     */   
/*     */   public double geTSWeight() {
/* 193 */     return this.m_TSWeight;
/*     */   }
/*     */   
/*     */   public void setTSWeight(double weight) {
/* 197 */     this.m_TSWeight = weight;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\timeseries\TimeSeries.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */