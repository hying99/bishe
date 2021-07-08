/*     */ package clus.heuristic;
/*     */ 
/*     */ import clus.data.rows.RowData;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.GeneticDistanceStat;
/*     */ 
/*     */ 
/*     */ public abstract class GeneticDistanceHeuristic
/*     */   extends ClusHeuristic
/*     */ {
/*     */   protected RowData m_Data;
/*     */   protected RowData m_RootData;
/*     */   protected int[] m_DataIndices;
/*     */   protected int[] m_ComplDataIndices;
/*     */   
/*     */   public String getName() {
/*  18 */     return "GeneticDistanceHeuristic";
/*     */   }
/*     */ 
/*     */   
/*     */   public void setData(RowData data) {
/*  23 */     this.m_Data = data;
/*  24 */     this.m_DataIndices = constructIndexVector(this.m_Data);
/*  25 */     this.m_ComplDataIndices = constructComplIndexVector(this.m_RootData, this.m_DataIndices);
/*     */   }
/*     */   
/*     */   public int[] constructIndexVector(RowData data) {
/*  29 */     int nb = data.getNbRows();
/*  30 */     int[] resultvector = new int[nb];
/*  31 */     for (int i = 0; i < nb; i++) {
/*  32 */       int index = data.getTuple(i).getIndex();
/*  33 */       resultvector[i] = index;
/*     */     } 
/*  35 */     return resultvector;
/*     */   }
/*     */   
/*     */   public int[] constructIndexVector(RowData data, ClusStatistic stat) {
/*  39 */     GeneticDistanceStat gstat = (GeneticDistanceStat)stat;
/*  40 */     int nb = (int)gstat.m_SumWeight;
/*  41 */     int[] resultvector = new int[nb];
/*  42 */     for (int i = 0; i < nb; i++) {
/*  43 */       int tupleindex = gstat.getTupleIndex(i);
/*  44 */       int origindex = data.getTuple(tupleindex).getIndex();
/*  45 */       resultvector[i] = origindex;
/*     */     } 
/*  47 */     return resultvector;
/*     */   }
/*     */   
/*     */   public int[] constructComplIndexVector(RowData data, int[] indices) {
/*  51 */     int totalnb = data.getNbRows();
/*  52 */     int indnb = indices.length;
/*  53 */     int complnb = totalnb - indnb;
/*  54 */     int[] resultvector = new int[complnb];
/*  55 */     int indexpos = 0;
/*  56 */     int currindex = indices[indexpos];
/*  57 */     int j = 0;
/*  58 */     for (int i = 0; i < totalnb; i++) {
/*  59 */       int index = data.getTuple(i).getIndex();
/*  60 */       if (currindex > index) {
/*  61 */         resultvector[j] = index;
/*  62 */         j++;
/*     */       }
/*  64 */       else if (currindex == index) {
/*  65 */         indexpos++;
/*  66 */         if (indexpos >= indnb) { currindex = 1000000; }
/*  67 */         else { currindex = indices[indexpos]; }
/*     */       
/*  69 */       } else if (currindex < index) {
/*  70 */         System.err.println("GeneticDistanceHeuristic : Something is wrong with datatuple indices!");
/*     */       } 
/*     */     } 
/*  73 */     return resultvector;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDistance(String[] seq1, String[] seq2) {
/*  83 */     switch (Settings.m_PhylogenyDM.getValue()) {
/*     */       case 1:
/*  85 */         return getEditDistance(seq1, seq2);
/*     */       case 0:
/*  87 */         return getPDistance(seq1, seq2);
/*     */       case 2:
/*  89 */         return getJukesCantorDistance(seq1, seq2);
/*     */       case 3:
/*  91 */         return getKimuraDistance(seq1, seq2);
/*     */       case 4:
/*  93 */         return getAminoKimuraDistance(seq1, seq2);
/*     */     } 
/*  95 */     return 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getEditDistance(String[] seq1, String[] seq2) {
/* 102 */     double p = 0.0D;
/* 103 */     for (int i = 0; i < seq1.length; i++) {
/* 104 */       if (!seq1[i].equals(seq2[i])) {
/* 105 */         p++;
/*     */       }
/*     */     } 
/* 108 */     return p;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getPDistance(String[] seq1, String[] seq2) {
/* 113 */     double p = 0.0D;
/* 114 */     int nb = 0;
/* 115 */     for (int i = 0; i < seq1.length; i++) {
/* 116 */       if (!seq1[i].equals("?") && !seq2[i].equals("?") && !seq1[i].equals("-") && !seq2[i].equals("-"))
/*     */       {
/*     */         
/* 119 */         if (!seq1[i].equals(seq2[i])) {
/* 120 */           p++;
/* 121 */           nb++;
/*     */         } else {
/*     */           
/* 124 */           nb++;
/*     */         } 
/*     */       }
/*     */     } 
/* 128 */     double p_distance = p / nb;
/* 129 */     if (p_distance == Double.POSITIVE_INFINITY) System.out.println("p: " + p + " nb: " + nb + " " + seq1 + " " + seq2); 
/* 130 */     if (p_distance == Double.NEGATIVE_INFINITY) System.out.println("p: " + p + " nb: " + nb + " " + seq1 + " " + seq2); 
/* 131 */     return p_distance;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getJukesCantorDistance(String[] seq1, String[] seq2) {
/* 136 */     double jk_distance, p_distance = getPDistance(seq1, seq2);
/*     */     
/* 138 */     if (p_distance > 0.749D) {
/* 139 */       jk_distance = 2.1562D;
/* 140 */       System.out.println("Warning: infinite Jukes Cantor distance (p-distance => 0.75), set to 2.1562");
/*     */     } else {
/* 142 */       jk_distance = -0.75D * Math.log(1.0D - 4.0D * p_distance / 3.0D);
/* 143 */     }  return jk_distance;
/*     */   }
/*     */   
/*     */   public double getKimuraDistance(String[] seq1, String[] seq2) {
/* 147 */     int nb = 0;
/* 148 */     int ti = 0;
/* 149 */     int tv = 0;
/* 150 */     for (int i = 0; i < seq1.length; i++) {
/* 151 */       if (!seq1[i].equals("?") && !seq2[i].equals("?") && !seq1[i].equals("-") && !seq2[i].equals("-")) {
/*     */ 
/*     */         
/* 154 */         nb++;
/* 155 */         if (!seq1[i].equals(seq2[i]))
/* 156 */           if (seq1[i].equals("A")) {
/* 157 */             if (seq2[i].equals("G"))
/* 158 */             { ti++; }
/*     */             else
/* 160 */             { tv++; } 
/* 161 */           } else if (seq1[i].equals("C")) {
/* 162 */             if (seq2[i].equals("T"))
/* 163 */             { ti++; }
/*     */             else
/* 165 */             { tv++; } 
/* 166 */           } else if (seq1[i].equals("G")) {
/* 167 */             if (seq2[i].equals("A"))
/* 168 */             { ti++; }
/*     */             else
/* 170 */             { tv++; } 
/* 171 */           } else if (seq1[i].equals("T")) {
/* 172 */             if (seq2[i].equals("C")) {
/* 173 */               ti++;
/*     */             } else {
/* 175 */               tv++;
/*     */             } 
/*     */           }  
/*     */       } 
/*     */     } 
/* 180 */     double ti_ratio = ti / nb;
/* 181 */     double tv_ratio = tv / nb;
/*     */     
/* 183 */     double term1 = Math.log10(1.0D / (1.0D - 2.0D * ti_ratio - tv_ratio));
/* 184 */     double term2 = Math.log10(1.0D / (1.0D - 2.0D * tv_ratio));
/* 185 */     double kimura = term1 + term2;
/*     */ 
/*     */     
/* 188 */     return kimura;
/*     */   }
/*     */   
/*     */   public double getAminoKimuraDistance(String[] seq1, String[] seq2) {
/* 192 */     double kimura, p_distance = getPDistance(seq1, seq2);
/*     */     
/* 194 */     if (p_distance > 0.8541D) {
/* 195 */       kimura = 12.84D;
/* 196 */       System.out.println("Warning: infinite AminoKimura distances (p-distance > 0.85), set to 12.84");
/*     */     } else {
/* 198 */       kimura = -1.0D * Math.log(1.0D - p_distance - 0.2D * Math.pow(p_distance, 2.0D));
/* 199 */     }  return kimura;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\heuristic\GeneticDistanceHeuristic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */