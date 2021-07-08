/*     */ package org.apache.commons.math.random;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
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
/*     */ public class ValueServer
/*     */ {
/*  47 */   private int mode = 5;
/*     */ 
/*     */   
/*  50 */   private URL valuesFileURL = null;
/*     */ 
/*     */   
/*  53 */   private double mu = 0.0D;
/*     */ 
/*     */   
/*  56 */   private double sigma = 0.0D;
/*     */ 
/*     */   
/*  59 */   private EmpiricalDistribution empiricalDistribution = null;
/*     */ 
/*     */   
/*  62 */   private BufferedReader filePointer = null;
/*     */ 
/*     */   
/*  65 */   private RandomDataImpl randomData = new RandomDataImpl();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DIGEST_MODE = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int REPLAY_MODE = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int UNIFORM_MODE = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int EXPONENTIAL_MODE = 3;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int GAUSSIAN_MODE = 4;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int CONSTANT_MODE = 5;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getNext() throws IOException {
/*  99 */     switch (this.mode) { case 0:
/* 100 */         return getNextDigest();
/* 101 */       case 1: return getNextReplay();
/* 102 */       case 2: return getNextUniform();
/* 103 */       case 3: return getNextExponential();
/* 104 */       case 4: return getNextGaussian();
/* 105 */       case 5: return this.mu; }
/* 106 */      throw new IllegalStateException("Bad mode: " + this.mode);
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
/*     */   public void fill(double[] values) throws IOException {
/* 118 */     for (int i = 0; i < values.length; i++) {
/* 119 */       values[i] = getNext();
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
/*     */   public double[] fill(int length) throws IOException {
/* 132 */     double[] out = new double[length];
/* 133 */     for (int i = 0; i < length; i++) {
/* 134 */       out[i] = getNext();
/*     */     }
/* 136 */     return out;
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
/*     */   public void computeDistribution() throws IOException {
/* 152 */     this.empiricalDistribution = new EmpiricalDistributionImpl();
/* 153 */     this.empiricalDistribution.load(this.valuesFileURL);
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
/*     */   public void computeDistribution(int binCount) throws IOException {
/* 172 */     this.empiricalDistribution = new EmpiricalDistributionImpl(binCount);
/* 173 */     this.empiricalDistribution.load(this.valuesFileURL);
/* 174 */     this.mu = this.empiricalDistribution.getSampleStats().getMean();
/* 175 */     this.sigma = this.empiricalDistribution.getSampleStats().getStandardDeviation();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMode() {
/* 182 */     return this.mode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMode(int mode) {
/* 189 */     this.mode = mode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URL getValuesFileURL() {
/* 197 */     return this.valuesFileURL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValuesFileURL(String url) throws MalformedURLException {
/* 206 */     this.valuesFileURL = new URL(url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValuesFileURL(URL url) {
/* 214 */     this.valuesFileURL = url;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EmpiricalDistribution getEmpiricalDistribution() {
/* 221 */     return this.empiricalDistribution;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetReplayFile() throws IOException {
/* 230 */     if (this.filePointer != null) {
/*     */       try {
/* 232 */         this.filePointer.close();
/* 233 */         this.filePointer = null;
/* 234 */       } catch (IOException ex) {}
/*     */     }
/*     */ 
/*     */     
/* 238 */     this.filePointer = new BufferedReader(new InputStreamReader(this.valuesFileURL.openStream()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeReplayFile() throws IOException {
/* 247 */     if (this.filePointer != null) {
/* 248 */       this.filePointer.close();
/* 249 */       this.filePointer = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMu() {
/* 257 */     return this.mu;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMu(double mu) {
/* 264 */     this.mu = mu;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getSigma() {
/* 271 */     return this.sigma;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSigma(double sigma) {
/* 278 */     this.sigma = sigma;
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
/*     */   private double getNextDigest() {
/* 294 */     if (this.empiricalDistribution == null || this.empiricalDistribution.getBinStats().size() == 0)
/*     */     {
/* 296 */       throw new IllegalStateException("Digest not initialized");
/*     */     }
/* 298 */     return this.empiricalDistribution.getNextValue();
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
/*     */   private double getNextReplay() throws IOException {
/* 316 */     String str = null;
/* 317 */     if (this.filePointer == null) {
/* 318 */       resetReplayFile();
/*     */     }
/* 320 */     if ((str = this.filePointer.readLine()) == null) {
/* 321 */       closeReplayFile();
/* 322 */       resetReplayFile();
/* 323 */       str = this.filePointer.readLine();
/*     */     } 
/* 325 */     return (new Double(str)).doubleValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double getNextUniform() {
/* 334 */     return this.randomData.nextUniform(0.0D, 2.0D * this.mu);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double getNextExponential() {
/* 343 */     return this.randomData.nextExponential(this.mu);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double getNextGaussian() {
/* 353 */     return this.randomData.nextGaussian(this.mu, this.sigma);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\random\ValueServer.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */