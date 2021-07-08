/*     */ package org.jgap.util;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
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
/*     */ public class TestHashcode
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*     */   private boolean m_verbose;
/*  36 */   private double m_AverageMin = 0.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   private double m_AverageMax = 1.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   private double m_StdDevMin = 1.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  60 */   private double m_StdDevMax = 2.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  68 */   private double m_fractionUnique = 0.9D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  74 */   private double m_actualFractionUnique = 0.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVerbose(boolean a_verbose) {
/*  84 */     this.m_verbose = a_verbose;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAverageMax(double a_averageMax) {
/*  95 */     this.m_AverageMax = a_averageMax;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAverageMin(double a_averageMin) {
/* 106 */     this.m_AverageMin = a_averageMin;
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
/*     */   public void setStdDevMax(double a_stdDevMax) {
/* 119 */     this.m_StdDevMax = a_stdDevMax;
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
/*     */   public void setStdDevMin(double a_stdDevMin) {
/* 132 */     this.m_StdDevMin = a_stdDevMin;
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
/*     */   public void setFractionUnique(double a_fractionUnique) {
/* 146 */     if (a_fractionUnique < 0.0D || a_fractionUnique > 1.0D) {
/* 147 */       throw new IllegalArgumentException("fractionUnique must be between 0.0 and 1.0");
/*     */     }
/*     */     
/* 150 */     this.m_fractionUnique = a_fractionUnique;
/*     */   }
/*     */   
/*     */   public double getActualFractionUnique() {
/* 154 */     return this.m_actualFractionUnique;
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
/*     */   public boolean testHashCodeUniqueness(List<E> a_ObjectList) {
/* 170 */     boolean result = false;
/*     */ 
/*     */     
/* 173 */     int numObjects = a_ObjectList.size();
/* 174 */     Hashtable<Object, Object> hashCodes = new Hashtable<Object, Object>();
/*     */ 
/*     */     
/* 177 */     for (int index = 0; index < numObjects; index++) {
/* 178 */       int hashcode = a_ObjectList.get(index).hashCode();
/* 179 */       Integer key = new Integer(hashcode);
/* 180 */       if (hashCodes.containsKey(key)) {
/* 181 */         int newvalue = ((Integer)hashCodes.get(key)).intValue() + 1;
/* 182 */         hashCodes.put(key, new Integer(newvalue));
/*     */       } else {
/*     */         
/* 185 */         hashCodes.put(key, new Integer(1));
/*     */       } 
/*     */     } 
/* 188 */     this.m_actualFractionUnique = hashCodes.size() / numObjects;
/* 189 */     if (this.m_actualFractionUnique < this.m_fractionUnique) {
/* 190 */       result = false;
/*     */     } else {
/*     */       
/* 193 */       result = true;
/*     */     } 
/* 195 */     return result;
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
/*     */   public boolean testHashCodeEquality(List<E> a_ObjectList) {
/* 210 */     long numObjects = a_ObjectList.size();
/*     */     
/* 212 */     if (numObjects < 2L) {
/* 213 */       return false;
/*     */     }
/* 215 */     int hashCode = a_ObjectList.get(0).hashCode();
/* 216 */     for (int index = 1; index < numObjects; index++) {
/* 217 */       if (hashCode != a_ObjectList.get(index).hashCode()) {
/* 218 */         return false;
/*     */       }
/*     */     } 
/* 221 */     return true;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean testDispersion(List a_ObjectList) {
/* 241 */     boolean result = false;
/* 242 */     int[] hashCodes = new int[a_ObjectList.size()];
/* 243 */     long numObjects = a_ObjectList.size();
/* 244 */     double average = 0.0D;
/*     */ 
/*     */     
/*     */     int index;
/*     */     
/* 249 */     for (index = 0; index < numObjects; index++) {
/* 250 */       hashCodes[index] = a_ObjectList.get(index).hashCode();
/*     */     }
/*     */ 
/*     */     
/* 254 */     for (index = 0; index < numObjects; index++) {
/* 255 */       average += hashCodes[index];
/*     */     }
/* 257 */     average /= numObjects;
/*     */ 
/*     */     
/* 260 */     double sumOfSquare = 0.0D;
/* 261 */     double squareOfSum = 0.0D;
/* 262 */     for (index = 0; index < numObjects; index++) {
/* 263 */       sumOfSquare += hashCodes[index] * hashCodes[index];
/*     */       
/* 265 */       squareOfSum += hashCodes[index];
/*     */     } 
/* 267 */     squareOfSum *= squareOfSum;
/* 268 */     double stdDev = sumOfSquare * numObjects - squareOfSum;
/* 269 */     stdDev /= (numObjects * (numObjects - 1L));
/* 270 */     stdDev = Math.sqrt(stdDev);
/*     */     
/* 272 */     if (this.m_verbose) {
/* 273 */       System.out.println("Average =" + average + " StdDev =" + stdDev);
/* 274 */       System.out.println("Average - StdDev =" + (average - stdDev));
/* 275 */       System.out.println("Average + StdDev =" + (average + stdDev));
/*     */     } 
/*     */     
/* 278 */     if (this.m_AverageMin < average && average < this.m_AverageMax) {
/* 279 */       result = true;
/*     */     } else {
/*     */       
/* 282 */       result = false;
/*     */     } 
/*     */     
/* 285 */     if (this.m_StdDevMin < stdDev && stdDev < this.m_StdDevMax) {
/* 286 */       int i = result & true;
/*     */     } else {
/*     */       
/* 289 */       result = false;
/*     */     } 
/* 291 */     return result;
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
/*     */   public static void main(String[] args) {
/* 304 */     TestHashcode th = new TestHashcode();
/* 305 */     List<BigDecimal> tl = new ArrayList();
/*     */     
/* 307 */     for (int com = 600000; com < 600100; com++) {
/* 308 */       tl.add(new BigDecimal(com));
/*     */     }
/* 310 */     th.testDispersion(tl);
/* 311 */     th.setFractionUnique(0.8D);
/* 312 */     th.testHashCodeUniqueness(tl);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\TestHashcode.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */