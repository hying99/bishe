/*     */ package clus.heuristic;
/*     */ 
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.GeneticDistanceStat;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import jeans.list.BitList;
/*     */ import jeans.math.matrix.MSymMatrix;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GeneticDistanceHeuristicMatrix
/*     */   extends GeneticDistanceHeuristic
/*     */ {
/*     */   protected MSymMatrix m_DistMatrix;
/*     */   protected double m_SumAllDistances;
/*     */   protected double m_AvgAllDistances;
/*     */   protected double m_SumEntropyWithin;
/*  24 */   protected HashMap m_HeurComputed = new HashMap<>();
/*     */   protected double[] m_SumDistWithCompl;
/*  26 */   protected int m_SampleSize = 20;
/*     */   
/*     */   protected boolean m_Sampling = false;
/*     */   protected String[][] m_Sequences;
/*  30 */   public long m_SetDataTimer = 0L;
/*  31 */   public long m_HeurTimer = 0L;
/*     */   
/*     */   protected double m_RootSumAllDistances;
/*     */   
/*     */   protected double m_RootAvgAllDistances;
/*     */   protected double m_RootSumEntropyWithin;
/*     */   
/*     */   public void setInitialData(ClusStatistic stat, RowData data) {
/*  39 */     this.m_RootData = data;
/*  40 */     this.m_RootData.addIndices();
/*  41 */     constructMatrix(stat);
/*     */   }
/*     */ 
/*     */   
/*     */   public void constructMatrix(ClusStatistic stat) {
/*     */     try {
/*  47 */       this.m_DistMatrix = read(this.m_RootData.getSchema().getSettings(), stat);
/*     */     }
/*  49 */     catch (IOException e) {
/*  50 */       this.m_DistMatrix = new MSymMatrix(this.m_RootData.getNbRows());
/*  51 */       System.out.println("  Calculating Distance Matrix (Size: " + this.m_RootData.getNbRows() + ")");
/*  52 */       GeneticDistanceStat gstat = (GeneticDistanceStat)stat;
/*  53 */       this.m_Sequences = new String[this.m_RootData.getNbRows()][gstat.m_NbTarget];
/*  54 */       for (int i = 0; i < this.m_RootData.getNbRows(); i++) {
/*  55 */         DataTuple tuple1 = this.m_RootData.getTuple(i);
/*  56 */         int row = tuple1.getIndex();
/*  57 */         String[] str1 = new String[gstat.m_NbTarget];
/*  58 */         for (int t = 0; t < gstat.m_NbTarget; t++) {
/*  59 */           int nomvalue1 = gstat.m_Attrs[t].getNominal(tuple1);
/*  60 */           str1[t] = gstat.m_Attrs[t].getValueOrMissing(nomvalue1);
/*  61 */           this.m_Sequences[i][t] = str1[t];
/*     */         } 
/*     */ 
/*     */         
/*  65 */         for (int j = i + 1; j < this.m_RootData.getNbRows(); j++) {
/*  66 */           DataTuple tuple2 = this.m_RootData.getTuple(j);
/*  67 */           int col = tuple2.getIndex();
/*  68 */           String[] str2 = new String[gstat.m_NbTarget];
/*  69 */           for (int k = 0; k < gstat.m_NbTarget; k++) {
/*  70 */             int nomvalue2 = gstat.m_Attrs[k].getNominal(tuple2);
/*  71 */             str2[k] = gstat.m_Attrs[k].getValueOrMissing(nomvalue2);
/*     */           } 
/*  73 */           double distance = getDistance(str1, str2);
/*  74 */           this.m_DistMatrix.set_sym(row, col, distance);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public MSymMatrix read(Settings sett, ClusStatistic stat) throws IOException {
/*  89 */     String filename = sett.getPhylogenyDistanceMatrix();
/*  90 */     ClusReader reader = new ClusReader(filename, sett);
/*  91 */     int nb = (int)reader.readFloat();
/*  92 */     System.out.println("  Loading Distance Matrix: " + filename + " (Size: " + nb + ")");
/*  93 */     MSymMatrix matrix = new MSymMatrix(nb);
/*  94 */     for (int i = 0; i < nb; i++) {
/*  95 */       reader.readName();
/*  96 */       for (int j = 0; j < nb; j++) {
/*  97 */         double value = reader.readFloat();
/*  98 */         if (i <= j) matrix.set_sym(i, j, value); 
/*     */       } 
/* 100 */       reader.readTillEol();
/*     */     } 
/*     */     
/* 103 */     reader.close();
/* 104 */     System.out.println("  Matrix loaded");
/*     */ 
/*     */     
/* 107 */     if (this.m_RootData.getSchema().getSettings().getPhylogenyEntropyVsRootStop() > 0.0D || this.m_RootData.getSchema().getSettings().getPhylogenyEntropyVsParentStop() > 0.0D) {
/* 108 */       GeneticDistanceStat gstat = (GeneticDistanceStat)stat;
/* 109 */       this.m_Sequences = new String[this.m_RootData.getNbRows()][gstat.m_NbTarget];
/* 110 */       for (int j = 0; j < this.m_RootData.getNbRows(); j++) {
/* 111 */         DataTuple tuple1 = this.m_RootData.getTuple(j);
/* 112 */         int row = tuple1.getIndex();
/* 113 */         String[] str1 = new String[gstat.m_NbTarget];
/* 114 */         for (int t = 0; t < gstat.m_NbTarget; t++) {
/* 115 */           int nomvalue1 = gstat.m_Attrs[t].getNominal(tuple1);
/* 116 */           str1[t] = gstat.m_Attrs[t].getValueOrMissing(nomvalue1);
/* 117 */           this.m_Sequences[j][t] = str1[t];
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 122 */     return matrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setData(RowData data) {
/* 127 */     this.m_Data = data;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 132 */     this.m_HeurComputed.clear();
/* 133 */     this.m_DataIndices = constructIndexVector(this.m_Data);
/* 134 */     this.m_SumAllDistances = getSumPWDistancesWithin(this.m_DataIndices);
/* 135 */     if (this.m_RootData.getSchema().getSettings().getPhylogenyDistancesVsRootStop() > 0.0D || this.m_RootData.getSchema().getSettings().getPhylogenyDistancesVsParentStop() > 0.0D) {
/* 136 */       this.m_AvgAllDistances = getAvgPWDistancesWithin(this.m_DataIndices);
/*     */     }
/* 138 */     if (this.m_RootData.getSchema().getSettings().getPhylogenyEntropyVsRootStop() > 0.0D || this.m_RootData.getSchema().getSettings().getPhylogenyEntropyVsParentStop() > 0.0D) {
/* 139 */       this.m_SumEntropyWithin = getSumOfEntropyWithin(this.m_DataIndices);
/*     */     }
/*     */     
/* 142 */     this.m_ComplDataIndices = constructComplIndexVector(this.m_RootData, this.m_DataIndices);
/* 143 */     this.m_SumDistWithCompl = constructComplDistVector(this.m_DataIndices, this.m_ComplDataIndices);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 150 */     if (this.m_Data.getNbRows() == this.m_RootData.getNbRows()) {
/* 151 */       this.m_RootSumAllDistances = this.m_SumAllDistances;
/* 152 */       this.m_RootAvgAllDistances = this.m_AvgAllDistances;
/* 153 */       this.m_RootSumEntropyWithin = this.m_SumEntropyWithin;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] constructComplDistVector(int[] indices, int[] complIndices) {
/* 159 */     int nbindices = indices.length;
/* 160 */     int nbcomplindices = complIndices.length;
/* 161 */     double[] resultvector = new double[nbindices];
/*     */     
/* 163 */     for (int i = 0; i < nbindices; i++) {
/* 164 */       double sumdist = 0.0D;
/* 165 */       int matrixrow = indices[i];
/* 166 */       for (int j = 0; j < nbcomplindices; j++) {
/* 167 */         int matrixcol = complIndices[j];
/* 168 */         sumdist += this.m_DistMatrix.get(matrixrow, matrixcol);
/*     */       } 
/* 170 */       resultvector[i] = sumdist;
/*     */     } 
/* 172 */     return resultvector;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getMinPWDistanceBetween(int[] posindices, int[] negindices) {
/* 177 */     double mindist = Double.POSITIVE_INFINITY;
/* 178 */     for (int i = 0; i < posindices.length; i++) {
/* 179 */       int row = posindices[i];
/* 180 */       for (int j = 0; j < negindices.length; j++) {
/* 181 */         int col = negindices[j];
/* 182 */         double dist = this.m_DistMatrix.get(row, col);
/* 183 */         if (dist < mindist) {
/* 184 */           mindist = dist;
/*     */         }
/*     */       } 
/*     */     } 
/* 188 */     return mindist;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getSumPWDistanceBetween(int[] posindices, int[] negindices) {
/* 193 */     double dist = 0.0D;
/* 194 */     for (int i = 0; i < posindices.length; i++) {
/* 195 */       int row = posindices[i];
/* 196 */       for (int j = 0; j < negindices.length; j++) {
/* 197 */         int col = negindices[j];
/* 198 */         dist += this.m_DistMatrix.get(row, col);
/*     */       } 
/*     */     } 
/* 201 */     return dist;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getAvgPWDistanceBetween(int[] posindices, int[] negindices) {
/* 206 */     double dist = getSumPWDistanceBetween(posindices, negindices);
/* 207 */     double nbpairs = (posindices.length * negindices.length);
/* 208 */     return dist / nbpairs;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getSumPWDistancesWithin(int[] indices) {
/* 213 */     int nb_ex = indices.length;
/* 214 */     double sum = 0.0D;
/* 215 */     for (int i = 0; i < nb_ex; i++) {
/* 216 */       int row = indices[i];
/* 217 */       for (int j = i + 1; j < nb_ex; j++) {
/* 218 */         int col = indices[j];
/* 219 */         sum += this.m_DistMatrix.get(row, col);
/*     */       } 
/*     */     } 
/* 222 */     return sum;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getAvgPWDistancesWithin(int[] indices) {
/* 227 */     if (indices.length <= 1) {
/* 228 */       return 0.0D;
/*     */     }
/* 230 */     double dist = getSumPWDistancesWithin(indices);
/* 231 */     int nb_ex = indices.length;
/* 232 */     double nb_pairs = (nb_ex * (nb_ex - 1) / 2);
/* 233 */     return dist / nb_pairs;
/*     */   }
/*     */   
/*     */   public double getSumOfEntropyWithin(int[] indices) {
/* 237 */     double entropy = 0.0D;
/* 238 */     for (int t = 0; t < (this.m_Sequences[0]).length; t++) {
/* 239 */       int[] counters = new int[95];
/* 240 */       int nbnongaps = 0;
/* 241 */       double columnentropy = 0.0D; int i;
/* 242 */       for (i = 0; i < indices.length; i++) {
/* 243 */         int row = indices[i];
/*     */ 
/*     */         
/* 246 */         int code = this.m_Sequences[row][t].hashCode();
/* 247 */         counters[code] = counters[code] + 1;
/* 248 */         nbnongaps++;
/*     */       } 
/*     */ 
/*     */       
/* 252 */       for (i = 0; i < 95; i++) {
/*     */         
/* 254 */         if (counters[i] != 0) {
/*     */           
/* 256 */           double p = counters[i] / nbnongaps;
/* 257 */           double log = log2(p);
/* 258 */           double product = p * log;
/*     */           
/* 260 */           columnentropy -= product;
/*     */         } 
/*     */       } 
/*     */       
/* 264 */       entropy += columnentropy;
/*     */     } 
/* 266 */     return entropy;
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
/*     */   private static double log2(double x) {
/* 279 */     return Math.log(x) / Math.log(2.0D);
/*     */   }
/*     */ 
/*     */   
/*     */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 284 */     switch (Settings.m_PhylogenyCriterion.getValue()) {
/*     */       case 0:
/* 286 */         return calcHeuristicBranchLengths(c_tstat, c_pstat, (ClusStatistic)null);
/*     */       case 1:
/* 288 */         return calcHeuristicArslan(c_tstat, c_pstat, (ClusStatistic)null);
/*     */       case 2:
/* 290 */         return calcHeuristicMaxMinDistance(c_tstat, c_pstat, (ClusStatistic)null);
/*     */     } 
/* 292 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic[] array_stat, int nbsplit) {
/* 296 */     ClusStatistic p_stat = array_stat[0];
/* 297 */     ClusStatistic part_stat = array_stat[1];
/* 298 */     switch (Settings.m_PhylogenyCriterion.getValue()) {
/*     */       case 0:
/* 300 */         return calcHeuristicBranchLengths(c_tstat, p_stat, part_stat);
/*     */       case 1:
/* 302 */         return calcHeuristicArslan(c_tstat, p_stat, (ClusStatistic)null);
/*     */       case 2:
/* 304 */         return calcHeuristicMaxMinDistance(c_tstat, p_stat, (ClusStatistic)null);
/*     */     } 
/* 306 */     return 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double calcHeuristicArslan(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 317 */     GeneticDistanceStat tstat = (GeneticDistanceStat)c_tstat;
/* 318 */     GeneticDistanceStat pstat = (GeneticDistanceStat)c_pstat;
/* 319 */     GeneticDistanceStat nstat = tstat.cloneStat();
/* 320 */     nstat.copy((ClusStatistic)tstat);
/* 321 */     nstat.subtractFromThis((ClusStatistic)pstat);
/*     */ 
/*     */     
/* 324 */     double stop = checkStopCriterion(tstat, pstat, nstat);
/* 325 */     if (stop != 0.0D) {
/* 326 */       return stop;
/*     */     }
/*     */     
/* 329 */     double n_pos = pstat.m_SumWeight;
/* 330 */     double n_neg = nstat.m_SumWeight;
/*     */     
/* 332 */     int[] posindices = constructIndexVector(this.m_Data, (ClusStatistic)pstat);
/* 333 */     int[] negindices = constructIndexVector(this.m_Data, (ClusStatistic)nstat);
/*     */     
/* 335 */     double result = getAvgPWDistanceBetween(posindices, negindices);
/* 336 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double calcHeuristicMaxMinDistance(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 346 */     GeneticDistanceStat tstat = (GeneticDistanceStat)c_tstat;
/* 347 */     GeneticDistanceStat pstat = (GeneticDistanceStat)c_pstat;
/* 348 */     GeneticDistanceStat nstat = tstat.cloneStat();
/* 349 */     nstat.copy((ClusStatistic)tstat);
/* 350 */     nstat.subtractFromThis((ClusStatistic)pstat);
/*     */ 
/*     */     
/* 353 */     double stop = checkStopCriterion(tstat, pstat, nstat);
/* 354 */     if (stop != 0.0D) {
/* 355 */       return stop;
/*     */     }
/*     */     
/* 358 */     double n_pos = pstat.m_SumWeight;
/* 359 */     double n_neg = nstat.m_SumWeight;
/*     */     
/* 361 */     int[] posindices = constructIndexVector(this.m_Data, (ClusStatistic)pstat);
/* 362 */     int[] negindices = constructIndexVector(this.m_Data, (ClusStatistic)nstat);
/*     */     
/* 364 */     double result = getMinPWDistanceBetween(posindices, negindices);
/* 365 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] effcalculate(GeneticDistanceStat pstat, GeneticDistanceStat partition, int[] negindices) {
/* 376 */     String part1bits = partition.getBits().toString();
/*     */     
/* 378 */     double part1poswithin = -100000.0D;
/* 379 */     double part1negwithin = -100000.0D;
/*     */     
/* 381 */     ArrayList<Double> ResAl = (ArrayList)this.m_HeurComputed.get(part1bits);
/* 382 */     if (ResAl != null) {
/* 383 */       part1poswithin = ((Double)ResAl.get(1)).doubleValue();
/* 384 */       part1negwithin = ((Double)ResAl.get(2)).doubleValue();
/*     */     } else {
/*     */       
/* 387 */       System.out.println("------- Partition not found ------");
/*     */     } 
/*     */     
/* 390 */     int[] part1posindices = constructIndexVector(this.m_Data, (ClusStatistic)partition);
/*     */     
/* 392 */     GeneticDistanceStat p2stat = pstat.cloneStat();
/* 393 */     p2stat.copy((ClusStatistic)pstat);
/* 394 */     p2stat.subtractFromThis((ClusStatistic)partition);
/*     */     
/* 396 */     BitList part2bitl = p2stat.getBits();
/*     */     
/* 398 */     double part2poswithin = -100000.0D;
/* 399 */     ArrayList<Double> ResAl2 = (ArrayList)this.m_HeurComputed.get(part2bitl.toString());
/* 400 */     if (ResAl2 != null) {
/* 401 */       part2poswithin = ((Double)ResAl2.get(1)).doubleValue();
/*     */     } else {
/*     */       
/* 404 */       System.out.println("------- Partition2 not found ------");
/*     */     } 
/*     */     
/* 407 */     int[] part2posindices = constructIndexVector(this.m_Data, (ClusStatistic)p2stat);
/*     */     
/* 409 */     double poswithin = part1poswithin + part2poswithin + getSumPWDistanceBetween(part1posindices, part2posindices);
/* 410 */     double negwithin = part1negwithin - part2poswithin - getSumPWDistanceBetween(part2posindices, negindices);
/*     */     
/* 412 */     double[] result = new double[2];
/* 413 */     result[0] = poswithin;
/* 414 */     result[1] = negwithin;
/*     */     
/* 416 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double calcHeuristicBranchLengths(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic partition) {
/*     */     double result;
/* 426 */     GeneticDistanceStat tstat = (GeneticDistanceStat)c_tstat;
/* 427 */     GeneticDistanceStat pstat = (GeneticDistanceStat)c_pstat;
/*     */     
/* 429 */     GeneticDistanceStat nstat = tstat.cloneStat();
/* 430 */     nstat.copy((ClusStatistic)tstat);
/* 431 */     nstat.subtractFromThis((ClusStatistic)pstat);
/*     */ 
/*     */     
/* 434 */     double stop = checkStopCriterion(tstat, pstat, nstat);
/* 435 */     if (stop != 0.0D) {
/* 436 */       return stop;
/*     */     }
/*     */ 
/*     */     
/* 440 */     String key = pstat.getBits().toString();
/* 441 */     ArrayList<Double> ResAl = (ArrayList)this.m_HeurComputed.get(key);
/* 442 */     if (ResAl != null) {
/* 443 */       Double value = ResAl.get(0);
/* 444 */       return value.doubleValue();
/*     */     } 
/*     */ 
/*     */     
/* 448 */     key = pstat.getBits().toString();
/* 449 */     ResAl = (ArrayList<Double>)this.m_HeurComputed.get(key);
/* 450 */     if (ResAl != null) {
/* 451 */       Double value = ResAl.get(0);
/* 452 */       return value.doubleValue();
/*     */     } 
/*     */     
/* 455 */     int[] posindices = constructIndexVector(this.m_Data, (ClusStatistic)pstat);
/* 456 */     int[] negindices = constructIndexVector(this.m_Data, (ClusStatistic)nstat);
/*     */     
/* 458 */     double poswithin = 0.0D;
/* 459 */     double negwithin = 0.0D;
/*     */     
/* 461 */     if (partition != null) {
/* 462 */       GeneticDistanceStat part = (GeneticDistanceStat)partition;
/* 463 */       double[] withins = effcalculate(pstat, part, negindices);
/* 464 */       poswithin = withins[0];
/* 465 */       negwithin = withins[1];
/*     */     } else {
/*     */       
/* 468 */       poswithin = getSumPWDistancesWithin(posindices);
/* 469 */       negwithin = getSumPWDistancesWithin(negindices);
/*     */     } 
/*     */     
/* 472 */     double n_pos = pstat.m_SumWeight;
/* 473 */     double n_neg = tstat.m_SumWeight - pstat.m_SumWeight;
/*     */ 
/*     */ 
/*     */     
/* 477 */     if (this.m_Data.getNbRows() == this.m_RootData.getNbRows()) {
/* 478 */       result = (this.m_SumAllDistances + (n_neg - 1.0D) * poswithin + (n_pos - 1.0D) * negwithin) / n_pos * n_neg;
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 483 */       double sumDistPosToCompl = 0.0D;
/* 484 */       double sumDistNegToCompl = 0.0D;
/* 485 */       for (int i = 0; i < tstat.m_SumWeight; i++) {
/* 486 */         if (pstat.getBits().getBit(i)) {
/* 487 */           sumDistPosToCompl += this.m_SumDistWithCompl[i];
/*     */         } else {
/*     */           
/* 490 */           sumDistNegToCompl += this.m_SumDistWithCompl[i];
/*     */         } 
/*     */       } 
/* 493 */       double n_compl = this.m_ComplDataIndices.length;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 500 */       result = sumDistNegToCompl / n_neg * n_compl + sumDistPosToCompl / n_pos * n_compl + this.m_SumAllDistances / n_pos * n_neg + poswithin * (2.0D * n_neg - 1.0D) / n_pos * n_neg + negwithin * (2.0D * n_pos - 1.0D) / n_pos * n_neg;
/*     */     } 
/*     */     
/* 503 */     double finalresult = -1.0D * result;
/*     */ 
/*     */ 
/*     */     
/* 507 */     key = pstat.getBits().toString();
/* 508 */     ArrayList<Double> al = new ArrayList<>(3);
/* 509 */     al.add(new Double(finalresult));
/* 510 */     al.add(new Double(poswithin));
/* 511 */     al.add(new Double(negwithin));
/* 512 */     this.m_HeurComputed.put(key, al);
/*     */     
/* 514 */     key = nstat.getBits().toString();
/* 515 */     ArrayList<Double> al2 = new ArrayList<>(3);
/* 516 */     al2.add(new Double(finalresult));
/* 517 */     al2.add(new Double(negwithin));
/* 518 */     al2.add(new Double(poswithin));
/* 519 */     this.m_HeurComputed.put(key, al2);
/*     */     
/* 521 */     return finalresult;
/*     */   }
/*     */ 
/*     */   
/*     */   public double checkStopCriterion(GeneticDistanceStat tstat, GeneticDistanceStat pstat, GeneticDistanceStat nstat) {
/* 526 */     double n_pos = pstat.m_SumWeight;
/* 527 */     double n_neg = tstat.m_SumWeight - pstat.m_SumWeight;
/*     */ 
/*     */     
/* 530 */     if (n_pos < Settings.MINIMAL_WEIGHT || n_neg < Settings.MINIMAL_WEIGHT) {
/* 531 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 537 */     if (n_pos + n_neg != this.m_Data.getNbRows()) {
/* 538 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/* 540 */     if (Math.round(n_pos) != n_pos || Math.round(n_neg) != n_neg) {
/* 541 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 546 */     if (n_pos + n_neg == 2.0D * Settings.MINIMAL_WEIGHT) {
/* 547 */       return Double.POSITIVE_INFINITY;
/*     */     }
/*     */     
/* 550 */     return 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAcceptable(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 556 */     GeneticDistanceStat tstat = (GeneticDistanceStat)c_tstat;
/* 557 */     GeneticDistanceStat pstat = (GeneticDistanceStat)c_pstat;
/*     */     
/* 559 */     GeneticDistanceStat nstat = tstat.cloneStat();
/* 560 */     nstat.copy((ClusStatistic)tstat);
/* 561 */     nstat.subtractFromThis((ClusStatistic)pstat);
/*     */     
/* 563 */     double n_pos = pstat.m_SumWeight;
/* 564 */     double n_neg = tstat.m_SumWeight - pstat.m_SumWeight;
/*     */ 
/*     */ 
/*     */     
/* 568 */     if (this.m_RootData.getSchema().getSettings().getPhylogenyEntropyVsRootStop() > 0.0D && 
/* 569 */       this.m_SumEntropyWithin / this.m_RootSumEntropyWithin < this.m_RootData.getSchema().getSettings().getPhylogenyEntropyVsRootStop()) {
/* 570 */       if (this.m_RootData.getSchema().getSettings().getVerbose() > 2) {
/* 571 */         System.out.println("STOP: entropy at current node = " + this.m_SumEntropyWithin + ", at root = " + this.m_RootSumEntropyWithin);
/*     */       }
/* 573 */       return false;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 579 */     if (this.m_RootData.getSchema().getSettings().getPhylogenyDistancesVsRootStop() > 0.0D && 
/* 580 */       this.m_AvgAllDistances / this.m_RootAvgAllDistances < this.m_RootData.getSchema().getSettings().getPhylogenyDistancesVsRootStop()) {
/* 581 */       if (this.m_RootData.getSchema().getSettings().getVerbose() > 2) {
/* 582 */         System.out.println("STOP: AvgPWDistance at current node = " + this.m_AvgAllDistances + ", at root = " + this.m_RootAvgAllDistances);
/*     */       }
/* 584 */       return false;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 590 */     if (this.m_RootData.getSchema().getSettings().getPhylogenyDistancesVsParentStop() > 0.0D) {
/* 591 */       int[] posindices = constructIndexVector(this.m_Data, (ClusStatistic)pstat);
/* 592 */       int[] negindices = constructIndexVector(this.m_Data, (ClusStatistic)nstat);
/* 593 */       double poswithin = getAvgPWDistancesWithin(posindices);
/* 594 */       double negwithin = getAvgPWDistancesWithin(negindices);
/* 595 */       if ((n_pos * poswithin + n_neg * negwithin) / (n_pos + n_neg) / this.m_AvgAllDistances > this.m_RootData.getSchema().getSettings().getPhylogenyDistancesVsParentStop()) {
/* 596 */         if (this.m_RootData.getSchema().getSettings().getVerbose() > 2) {
/* 597 */           System.out.println("STOP: weighted sum of AvgPWDistances in children = " + ((n_pos * poswithin + n_neg * negwithin) / (n_pos + n_neg)) + ", AvgPWDistance at node = " + this.m_AvgAllDistances);
/*     */         }
/* 599 */         return false;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 605 */     if (this.m_RootData.getSchema().getSettings().getPhylogenyEntropyVsParentStop() > 0.0D) {
/* 606 */       int[] posindices = constructIndexVector(this.m_Data, (ClusStatistic)pstat);
/* 607 */       int[] negindices = constructIndexVector(this.m_Data, (ClusStatistic)nstat);
/* 608 */       double poswithin = getSumOfEntropyWithin(posindices);
/* 609 */       double negwithin = getSumOfEntropyWithin(negindices);
/* 610 */       if ((n_pos * poswithin + n_neg * negwithin) / (n_pos + n_neg) / this.m_SumEntropyWithin > this.m_RootData.getSchema().getSettings().getPhylogenyEntropyVsParentStop()) {
/* 611 */         if (this.m_RootData.getSchema().getSettings().getVerbose() > 2) {
/* 612 */           System.out.println("STOP: weighted sum of SumEntropies in children = " + ((n_pos * poswithin + n_neg * negwithin) / (n_pos + n_neg)) + ", SumEntropies at node = " + this.m_SumEntropyWithin);
/*     */         }
/* 614 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/* 618 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 623 */     switch (Settings.m_PhylogenyCriterion.getValue()) {
/*     */       case 0:
/* 625 */         return "GeneticDistanceHeuristicMatrix -> Minimize total branch length";
/*     */       case 1:
/* 627 */         return "GeneticDistanceHeuristicMatrix -> Maximize avg pairwise distance";
/*     */     } 
/* 629 */     return "GeneticDistanceHeuristicMatrix";
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\heuristic\GeneticDistanceHeuristicMatrix.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */