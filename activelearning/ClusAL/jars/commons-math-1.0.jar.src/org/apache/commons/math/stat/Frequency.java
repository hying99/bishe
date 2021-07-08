/*     */ package org.apache.commons.math.stat;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.TreeMap;
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
/*     */ public class Frequency
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = -3845586908418844111L;
/*  46 */   private TreeMap freqTable = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Frequency() {
/*  52 */     this.freqTable = new TreeMap();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Frequency(Comparator comparator) {
/*  61 */     this.freqTable = new TreeMap(comparator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  71 */     NumberFormat nf = NumberFormat.getPercentInstance();
/*  72 */     StringBuffer outBuffer = new StringBuffer();
/*  73 */     outBuffer.append("Value \t Freq. \t Pct. \t Cum Pct. \n");
/*  74 */     Iterator iter = this.freqTable.keySet().iterator();
/*  75 */     while (iter.hasNext()) {
/*  76 */       Object value = iter.next();
/*  77 */       outBuffer.append(value);
/*  78 */       outBuffer.append('\t');
/*  79 */       outBuffer.append(getCount(value));
/*  80 */       outBuffer.append('\t');
/*  81 */       outBuffer.append(nf.format(getPct(value)));
/*  82 */       outBuffer.append('\t');
/*  83 */       outBuffer.append(nf.format(getCumPct(value)));
/*  84 */       outBuffer.append('\n');
/*     */     } 
/*  86 */     return outBuffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addValue(Object v) {
/*  96 */     Object obj = v;
/*  97 */     if (v instanceof Integer) {
/*  98 */       obj = new Long(((Integer)v).longValue());
/*     */     }
/*     */     try {
/* 101 */       Long count = (Long)this.freqTable.get(obj);
/* 102 */       if (count == null) {
/* 103 */         this.freqTable.put(obj, new Long(1L));
/*     */       } else {
/* 105 */         this.freqTable.put(obj, new Long(count.longValue() + 1L));
/*     */       } 
/* 107 */     } catch (ClassCastException ex) {
/*     */       
/* 109 */       throw new IllegalArgumentException("Value not comparable to existing values.");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addValue(int v) {
/* 119 */     addValue(new Long(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addValue(Integer v) {
/* 128 */     addValue(new Long(v.longValue()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addValue(long v) {
/* 137 */     addValue(new Long(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addValue(char v) {
/* 146 */     addValue(new Character(v));
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 151 */     this.freqTable.clear();
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
/*     */   public Iterator valuesIterator() {
/* 164 */     return this.freqTable.keySet().iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getSumFreq() {
/* 175 */     long result = 0L;
/* 176 */     Iterator iterator = this.freqTable.values().iterator();
/* 177 */     while (iterator.hasNext()) {
/* 178 */       result += ((Long)iterator.next()).longValue();
/*     */     }
/* 180 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCount(Object v) {
/* 190 */     if (v instanceof Integer) {
/* 191 */       return getCount(((Integer)v).longValue());
/*     */     }
/* 193 */     long result = 0L;
/*     */     try {
/* 195 */       Long count = (Long)this.freqTable.get(v);
/* 196 */       if (count != null) {
/* 197 */         result = count.longValue();
/*     */       }
/* 199 */     } catch (ClassCastException ex) {}
/*     */ 
/*     */     
/* 202 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCount(int v) {
/* 212 */     return getCount(new Long(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCount(long v) {
/* 222 */     return getCount(new Long(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCount(char v) {
/* 232 */     return getCount(new Character(v));
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
/*     */   public double getPct(Object v) {
/* 247 */     if (getSumFreq() == 0L) {
/* 248 */       return Double.NaN;
/*     */     }
/* 250 */     return getCount(v) / getSumFreq();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getPct(int v) {
/* 261 */     return getPct(new Long(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getPct(long v) {
/* 272 */     return getPct(new Long(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getPct(char v) {
/* 283 */     return getPct(new Character(v));
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
/*     */   public long getCumFreq(Object v) {
/* 297 */     if (getSumFreq() == 0L) {
/* 298 */       return 0L;
/*     */     }
/* 300 */     if (v instanceof Integer) {
/* 301 */       return getCumFreq(((Integer)v).longValue());
/*     */     }
/* 303 */     Comparator c = this.freqTable.comparator();
/* 304 */     if (c == null) {
/* 305 */       c = new NaturalComparator();
/*     */     }
/* 307 */     long result = 0L;
/*     */     
/*     */     try {
/* 310 */       Long value = (Long)this.freqTable.get(v);
/* 311 */       if (value != null) {
/* 312 */         result = value.longValue();
/*     */       }
/* 314 */     } catch (ClassCastException ex) {
/* 315 */       return result;
/*     */     } 
/*     */     
/* 318 */     if (c.compare(v, this.freqTable.firstKey()) < 0) {
/* 319 */       return 0L;
/*     */     }
/*     */     
/* 322 */     if (c.compare(v, this.freqTable.lastKey()) >= 0) {
/* 323 */       return getSumFreq();
/*     */     }
/*     */     
/* 326 */     Iterator values = valuesIterator();
/* 327 */     while (values.hasNext()) {
/* 328 */       Object nextValue = values.next();
/* 329 */       if (c.compare(v, nextValue) > 0) {
/* 330 */         result += getCount(nextValue); continue;
/*     */       } 
/* 332 */       return result;
/*     */     } 
/*     */     
/* 335 */     return result;
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
/*     */   public long getCumFreq(int v) {
/* 347 */     return getCumFreq(new Long(v));
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
/*     */   public long getCumFreq(long v) {
/* 359 */     return getCumFreq(new Long(v));
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
/*     */   public long getCumFreq(char v) {
/* 371 */     return getCumFreq(new Character(v));
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
/*     */   public double getCumPct(Object v) {
/* 388 */     if (getSumFreq() == 0L) {
/* 389 */       return Double.NaN;
/*     */     }
/* 391 */     return getCumFreq(v) / getSumFreq();
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
/*     */   public double getCumPct(int v) {
/* 404 */     return getCumPct(new Long(v));
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
/*     */   public double getCumPct(long v) {
/* 417 */     return getCumPct(new Long(v));
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
/*     */   public double getCumPct(char v) {
/* 430 */     return getCumPct(new Character(v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class NaturalComparator
/*     */     implements Comparator
/*     */   {
/*     */     private NaturalComparator() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int compare(Object o1, Object o2) {
/* 452 */       return ((Comparable)o1).compareTo(o2);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\Frequency.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */