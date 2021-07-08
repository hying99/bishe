/*     */ package org.apache.commons.math.random;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Serializable;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.math.stat.descriptive.StatisticalSummary;
/*     */ import org.apache.commons.math.stat.descriptive.SummaryStatistics;
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
/*     */ public class EmpiricalDistributionImpl
/*     */   implements Serializable, EmpiricalDistribution
/*     */ {
/*     */   static final long serialVersionUID = -6773236347582113490L;
/*  65 */   private ArrayList binStats = null;
/*     */ 
/*     */   
/*  68 */   SummaryStatistics sampleStats = null;
/*     */ 
/*     */   
/*  71 */   private int binCount = 1000;
/*     */ 
/*     */   
/*     */   private boolean loaded = false;
/*     */ 
/*     */   
/*  77 */   private double[] upperBounds = null;
/*     */ 
/*     */   
/*  80 */   private RandomData randomData = new RandomDataImpl();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EmpiricalDistributionImpl() {
/*  86 */     this.binStats = new ArrayList();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EmpiricalDistributionImpl(int binCount) {
/*  95 */     this.binCount = binCount;
/*  96 */     this.binStats = new ArrayList();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void load(double[] in) {
/* 106 */     DataAdapter da = new ArrayDataAdapter(this, in);
/*     */     try {
/* 108 */       da.computeStats();
/* 109 */       fillBinStats(in);
/* 110 */     } catch (Exception e) {
/* 111 */       throw new RuntimeException(e.getMessage());
/*     */     } 
/* 113 */     this.loaded = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void load(URL url) throws IOException {
/* 124 */     BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
/*     */     
/*     */     try {
/* 127 */       DataAdapter da = new StreamDataAdapter(this, in);
/*     */       try {
/* 129 */         da.computeStats();
/* 130 */       } catch (Exception e) {
/* 131 */         throw new IOException(e.getMessage());
/*     */       } 
/* 133 */       in = new BufferedReader(new InputStreamReader(url.openStream()));
/* 134 */       fillBinStats(in);
/* 135 */       this.loaded = true;
/*     */     } finally {
/* 137 */       if (in != null) {
/*     */         try {
/* 139 */           in.close();
/* 140 */         } catch (Exception ex) {}
/*     */       }
/*     */     } 
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
/*     */   public void load(File file) throws IOException {
/* 154 */     BufferedReader in = new BufferedReader(new FileReader(file));
/*     */     try {
/* 156 */       DataAdapter da = new StreamDataAdapter(this, in);
/*     */       try {
/* 158 */         da.computeStats();
/* 159 */       } catch (Exception e) {
/* 160 */         throw new IOException(e.getMessage());
/*     */       } 
/* 162 */       in = new BufferedReader(new FileReader(file));
/* 163 */       fillBinStats(in);
/* 164 */       this.loaded = true;
/*     */     } finally {
/* 166 */       if (in != null) {
/*     */         try {
/* 168 */           in.close();
/* 169 */         } catch (Exception ex) {}
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private abstract class DataAdapter
/*     */   {
/*     */     private final EmpiricalDistributionImpl this$0;
/*     */     
/*     */     private DataAdapter(EmpiricalDistributionImpl this$0) {
/* 180 */       EmpiricalDistributionImpl.this = EmpiricalDistributionImpl.this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public abstract void computeBinStats(double param1Double1, double param1Double2) throws Exception;
/*     */ 
/*     */ 
/*     */     
/*     */     public abstract void computeStats() throws Exception;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class DataAdapterFactory
/*     */   {
/*     */     private final EmpiricalDistributionImpl this$0;
/*     */ 
/*     */ 
/*     */     
/*     */     private DataAdapterFactory(EmpiricalDistributionImpl this$0) {
/* 202 */       EmpiricalDistributionImpl.this = EmpiricalDistributionImpl.this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public EmpiricalDistributionImpl.DataAdapter getAdapter(Object in) {
/* 210 */       if (in instanceof BufferedReader) {
/* 211 */         BufferedReader inputStream = (BufferedReader)in;
/* 212 */         return new EmpiricalDistributionImpl.StreamDataAdapter(EmpiricalDistributionImpl.this, inputStream);
/* 213 */       }  if (in instanceof double[]) {
/* 214 */         double[] inputArray = (double[])in;
/* 215 */         return new EmpiricalDistributionImpl.ArrayDataAdapter(EmpiricalDistributionImpl.this, inputArray);
/*     */       } 
/* 217 */       throw new IllegalArgumentException("Input data comes from the unsupported source");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class StreamDataAdapter
/*     */     extends DataAdapter
/*     */   {
/*     */     BufferedReader inputStream;
/*     */ 
/*     */ 
/*     */     
/*     */     private final EmpiricalDistributionImpl this$0;
/*     */ 
/*     */ 
/*     */     
/*     */     public StreamDataAdapter(EmpiricalDistributionImpl this$0, BufferedReader in) {
/* 236 */       super(this$0); this.this$0 = this$0;
/* 237 */       this.inputStream = in;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void computeBinStats(double min, double delta) throws IOException {
/* 248 */       String str = null;
/* 249 */       double val = 0.0D;
/* 250 */       while ((str = this.inputStream.readLine()) != null) {
/* 251 */         val = Double.parseDouble(str);
/* 252 */         SummaryStatistics stats = this.this$0.binStats.get(Math.max((int)Math.ceil((val - min) / delta) - 1, 0));
/*     */ 
/*     */         
/* 255 */         stats.addValue(val);
/*     */       } 
/*     */       
/* 258 */       this.inputStream.close();
/* 259 */       this.inputStream = null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void computeStats() throws IOException {
/* 267 */       String str = null;
/* 268 */       double val = 0.0D;
/* 269 */       this.this$0.sampleStats = SummaryStatistics.newInstance();
/* 270 */       while ((str = this.inputStream.readLine()) != null) {
/* 271 */         val = (new Double(str)).doubleValue();
/* 272 */         this.this$0.sampleStats.addValue(val);
/*     */       } 
/* 274 */       this.inputStream.close();
/* 275 */       this.inputStream = null;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class ArrayDataAdapter
/*     */     extends DataAdapter
/*     */   {
/*     */     private double[] inputArray;
/*     */ 
/*     */     
/*     */     private final EmpiricalDistributionImpl this$0;
/*     */ 
/*     */ 
/*     */     
/*     */     public ArrayDataAdapter(EmpiricalDistributionImpl this$0, double[] in) {
/* 293 */       super(this$0); this.this$0 = this$0;
/* 294 */       this.inputArray = in;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void computeStats() throws IOException {
/* 302 */       this.this$0.sampleStats = SummaryStatistics.newInstance();
/* 303 */       for (int i = 0; i < this.inputArray.length; i++) {
/* 304 */         this.this$0.sampleStats.addValue(this.inputArray[i]);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void computeBinStats(double min, double delta) throws IOException {
/* 316 */       for (int i = 0; i < this.inputArray.length; i++) {
/* 317 */         SummaryStatistics stats = this.this$0.binStats.get(Math.max((int)Math.ceil((this.inputArray[i] - min) / delta) - 1, 0));
/*     */ 
/*     */ 
/*     */         
/* 321 */         stats.addValue(this.inputArray[i]);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fillBinStats(Object in) throws IOException {
/* 334 */     double min = this.sampleStats.getMin();
/* 335 */     double max = this.sampleStats.getMax();
/* 336 */     double delta = (max - min) / (new Double(this.binCount)).doubleValue();
/* 337 */     double[] binUpperBounds = new double[this.binCount];
/* 338 */     binUpperBounds[0] = min + delta; int i;
/* 339 */     for (i = 1; i < this.binCount - 1; i++) {
/* 340 */       binUpperBounds[i] = binUpperBounds[i - 1] + delta;
/*     */     }
/* 342 */     binUpperBounds[this.binCount - 1] = max;
/*     */ 
/*     */     
/* 345 */     if (!this.binStats.isEmpty()) {
/* 346 */       this.binStats.clear();
/*     */     }
/* 348 */     for (i = 0; i < this.binCount; i++) {
/* 349 */       SummaryStatistics stats = SummaryStatistics.newInstance();
/* 350 */       this.binStats.add(i, stats);
/*     */     } 
/*     */ 
/*     */     
/* 354 */     DataAdapterFactory aFactory = new DataAdapterFactory();
/* 355 */     DataAdapter da = aFactory.getAdapter(in);
/*     */     try {
/* 357 */       da.computeBinStats(min, delta);
/* 358 */     } catch (Exception e) {
/* 359 */       if (e instanceof RuntimeException) {
/* 360 */         throw new RuntimeException(e.getMessage());
/*     */       }
/* 362 */       throw new IOException(e.getMessage());
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 367 */     this.upperBounds = new double[this.binCount];
/* 368 */     this.upperBounds[0] = ((SummaryStatistics)this.binStats.get(0)).getN() / this.sampleStats.getN();
/*     */ 
/*     */     
/* 371 */     for (int j = 1; j < this.binCount - 1; j++) {
/* 372 */       this.upperBounds[j] = this.upperBounds[j - 1] + ((SummaryStatistics)this.binStats.get(j)).getN() / this.sampleStats.getN();
/*     */     }
/*     */ 
/*     */     
/* 376 */     this.upperBounds[this.binCount - 1] = 1.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getNextValue() throws IllegalStateException {
/* 387 */     if (!this.loaded) {
/* 388 */       throw new IllegalStateException("distribution not loaded");
/*     */     }
/*     */ 
/*     */     
/* 392 */     double x = Math.random();
/*     */ 
/*     */     
/* 395 */     for (int i = 0; i < this.binCount; i++) {
/* 396 */       if (x <= this.upperBounds[i]) {
/* 397 */         SummaryStatistics stats = this.binStats.get(i);
/* 398 */         if (stats.getN() > 0L) {
/* 399 */           if (stats.getStandardDeviation() > 0.0D) {
/* 400 */             return this.randomData.nextGaussian(stats.getMean(), stats.getStandardDeviation());
/*     */           }
/*     */           
/* 403 */           return stats.getMean();
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 408 */     throw new RuntimeException("No bin selected");
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
/*     */   public StatisticalSummary getSampleStats() {
/* 420 */     return (StatisticalSummary)this.sampleStats;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBinCount() {
/* 429 */     return this.binCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getBinStats() {
/* 440 */     return this.binStats;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] getUpperBounds() {
/* 451 */     return this.upperBounds;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLoaded() {
/* 460 */     return this.loaded;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\random\EmpiricalDistributionImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */