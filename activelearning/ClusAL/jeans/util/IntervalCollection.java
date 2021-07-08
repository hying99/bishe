/*     */ package jeans.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
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
/*     */ public class IntervalCollection
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  32 */   public static final IntervalCollection EMPTY = new IntervalCollection();
/*     */   
/*  34 */   protected static final String[] DELIMS = new String[] { "-", "," };
/*     */   
/*     */   protected int m_Total;
/*     */   protected int m_CrInt;
/*     */   protected boolean m_Done;
/*     */   protected boolean m_Default;
/*  40 */   protected ArrayList m_Interval = new ArrayList();
/*     */   
/*     */   public IntervalCollection(String str) {
/*  43 */     if (str.toUpperCase().equals("DEFAULT")) {
/*  44 */       this.m_Default = true;
/*     */       return;
/*     */     } 
/*  47 */     if (!str.toUpperCase().equals("NONE") && !str.toUpperCase().equals("EMPTY")) {
/*  48 */       ArrayList<Interval> intervals = new ArrayList();
/*  49 */       MultiDelimStringTokenizer tok = new MultiDelimStringTokenizer(DELIMS);
/*  50 */       tok.setLine(str);
/*  51 */       int first = -1, last = -1;
/*  52 */       while (tok.hasMoreTokens()) {
/*  53 */         String token = tok.nextToken();
/*  54 */         int delim = tok.lastDelim();
/*  55 */         first = Integer.parseInt(token);
/*  56 */         if (delim == 1) {
/*  57 */           if (tok.hasMoreTokens()) {
/*  58 */             token = tok.nextToken();
/*  59 */             last = Integer.parseInt(token);
/*     */           } else {
/*  61 */             last = first;
/*     */           } 
/*     */         } else {
/*  64 */           last = first;
/*     */         } 
/*  66 */         intervals.add(new Interval(first, last));
/*     */       } 
/*  68 */       initializeFromBits(toBits(intervals));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected IntervalCollection() {}
/*     */   
/*     */   public boolean isDefault() {
/*  76 */     return this.m_Default;
/*     */   }
/*     */   
/*     */   public void clear() {
/*  80 */     this.m_Default = false;
/*  81 */     this.m_Interval.clear();
/*  82 */     computeTotal();
/*     */   }
/*     */   
/*     */   public void addInterval(int first, int last) {
/*  86 */     this.m_Interval.add(new Interval(first, last));
/*  87 */     computeTotal();
/*     */   }
/*     */   
/*     */   public int getNbIntervals() {
/*  91 */     return this.m_Interval.size();
/*     */   }
/*     */   
/*     */   public Interval getInterval(int idx) {
/*  95 */     return this.m_Interval.get(idx);
/*     */   }
/*     */   
/*     */   public void copyFrom(IntervalCollection other) {
/*  99 */     this.m_Interval.clear();
/* 100 */     for (int i = 0; i < other.getNbIntervals(); i++) {
/* 101 */       this.m_Interval.add(other.getInterval(i));
/*     */     }
/* 103 */     computeTotal();
/*     */   }
/*     */   
/*     */   public void subtract(IntervalCollection other) {
/* 107 */     int max = Math.max(getMaxIndex(), other.getMaxIndex());
/* 108 */     boolean[] bits = new boolean[max + 1];
/* 109 */     toBits(bits);
/* 110 */     other.subtractFromBits(bits);
/* 111 */     initializeFromBits(bits);
/*     */   }
/*     */   
/*     */   public void add(boolean[] bits) {
/* 115 */     boolean[] this_bits = new boolean[Math.max(getMaxIndex() + 1, bits.length)];
/* 116 */     toBits(this_bits);
/* 117 */     for (int i = 0; i < bits.length; i++) {
/* 118 */       if (bits[i]) this_bits[i] = true; 
/*     */     } 
/* 120 */     initializeFromBits(this_bits);
/*     */   }
/*     */   
/*     */   public void initializeFromBits(boolean[] bits) {
/* 124 */     this.m_Interval.clear();
/* 125 */     boolean is_in = false;
/* 126 */     int start = -1;
/* 127 */     for (int i = 0; i < bits.length; i++) {
/* 128 */       if (bits[i]) {
/* 129 */         if (!is_in) {
/* 130 */           start = i;
/* 131 */           is_in = true;
/*     */         }
/*     */       
/* 134 */       } else if (is_in) {
/* 135 */         this.m_Interval.add(new Interval(start, i - 1));
/* 136 */         is_in = false;
/*     */       } 
/*     */     } 
/*     */     
/* 140 */     if (is_in) {
/* 141 */       this.m_Interval.add(new Interval(start, bits.length - 1));
/*     */     }
/* 143 */     computeTotal();
/*     */   }
/*     */   
/*     */   public void toBits(boolean[] bits) {
/* 147 */     toBits(this.m_Interval, bits);
/*     */   }
/*     */   
/*     */   public static void toBits(ArrayList<Interval> intervals, boolean[] bits) {
/* 151 */     for (int i = 0; i < intervals.size(); i++) {
/* 152 */       Interval interv = intervals.get(i);
/* 153 */       interv.toBits(bits);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean[] toBits() {
/* 158 */     return toBits(this.m_Interval);
/*     */   }
/*     */   
/*     */   public static boolean[] toBits(ArrayList intervals) {
/* 162 */     boolean[] bits = new boolean[getMaxIndex(intervals) + 1];
/* 163 */     toBits(intervals, bits);
/* 164 */     return bits;
/*     */   }
/*     */   
/*     */   public void subtractFromBits(boolean[] bits) {
/* 168 */     for (int i = 0; i < getNbIntervals(); i++) {
/* 169 */       Interval interv = getInterval(i);
/* 170 */       interv.subtractFromBits(bits);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getMaxIndex() {
/* 175 */     return getMaxIndex(this.m_Interval);
/*     */   }
/*     */   
/*     */   public static int getMaxIndex(ArrayList<Interval> intervals) {
/* 179 */     int max = 0;
/* 180 */     for (int i = 0; i < intervals.size(); i++) {
/* 181 */       Interval interv = intervals.get(i);
/* 182 */       max = Math.max(max, interv.getLast());
/*     */     } 
/* 184 */     return max;
/*     */   }
/*     */   
/*     */   public int getMinIndex() {
/* 188 */     return getMinIndex(this.m_Interval);
/*     */   }
/*     */   
/*     */   public static int getMinIndex(ArrayList<Interval> intervals) {
/* 192 */     int min = Integer.MAX_VALUE;
/* 193 */     for (int i = 0; i < intervals.size(); i++) {
/* 194 */       Interval interv = intervals.get(i);
/* 195 */       min = Math.min(min, interv.getFirst());
/*     */     } 
/* 197 */     return min;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 201 */     return (this.m_Interval.size() == 0);
/*     */   }
/*     */   
/*     */   public void reset() {
/* 205 */     this.m_CrInt = -1;
/* 206 */     this.m_Done = gotoNext();
/*     */   }
/*     */   
/*     */   public boolean hasMoreInts() {
/* 210 */     return this.m_Done;
/*     */   }
/*     */   
/*     */   public int nextInt() {
/* 214 */     int res = getInterval(this.m_CrInt).get();
/* 215 */     this.m_Done = gotoNext();
/* 216 */     return res;
/*     */   }
/*     */   
/*     */   public boolean gotoNext() {
/* 220 */     if (this.m_CrInt != -1) getInterval(this.m_CrInt).incr(); 
/* 221 */     if (this.m_CrInt == -1 || getInterval(this.m_CrInt).atEnd()) {
/* 222 */       this.m_CrInt++;
/* 223 */       if (this.m_CrInt >= getNbIntervals()) return false; 
/* 224 */       getInterval(this.m_CrInt).reset();
/*     */     } 
/* 226 */     return true;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 230 */     if (getNbIntervals() > 0) {
/* 231 */       StringBuffer buf = new StringBuffer();
/* 232 */       for (int i = 0; i < getNbIntervals(); i++) {
/* 233 */         Interval interv = getInterval(i);
/* 234 */         if (i != 0) buf.append(","); 
/* 235 */         buf.append(interv.toString());
/*     */       } 
/* 237 */       return buf.toString();
/*     */     } 
/* 239 */     return "None";
/*     */   }
/*     */ 
/*     */   
/*     */   protected void computeTotal() {
/* 244 */     int total = 0;
/* 245 */     for (int i = 0; i < this.m_Interval.size(); i++) {
/* 246 */       Interval interv = this.m_Interval.get(i);
/* 247 */       total += interv.getSize();
/*     */     } 
/* 249 */     this.m_Total = total;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\IntervalCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */