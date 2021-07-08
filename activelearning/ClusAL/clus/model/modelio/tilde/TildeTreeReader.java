/*     */ package clus.model.modelio.tilde;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.model.test.FakeTest;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.io.IOException;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.MStreamTokenizer;
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
/*     */ public class TildeTreeReader
/*     */ {
/*     */   protected TildeOutReader m_Parent;
/*     */   
/*     */   public TildeTreeReader(TildeOutReader parent) {
/*  38 */     this.m_Parent = parent;
/*     */   }
/*     */   
/*     */   public ClusNode readTree() throws IOException {
/*  42 */     String head = getTillEOL();
/*  43 */     if (this.m_Parent.getDebug()) System.out.println("Head: " + head); 
/*  44 */     ClusNode node = doReadTree(head);
/*  45 */     return node;
/*     */   }
/*     */   
/*     */   public ClusNode doReadTree(String name) throws IOException {
/*  49 */     ClusNode tree = new ClusNode();
/*  50 */     boolean isleaf = cleanString(getTillEOL(), tree);
/*  51 */     if (!isleaf) {
/*  52 */       skipEntry("yes");
/*  53 */       ClusNode left = doReadTree("yes");
/*  54 */       tree.addChild((Node)left);
/*  55 */       skipEntry("no");
/*  56 */       ClusNode right = doReadTree("no");
/*  57 */       tree.addChild((Node)right);
/*     */     } 
/*  59 */     return tree;
/*     */   }
/*     */   
/*     */   public void readLeaf(MStreamTokenizer tokens, ClusNode node) throws IOException {
/*  63 */     int mode = this.m_Parent.getMode();
/*  64 */     if (mode == 1) {
/*  65 */       tokens.readToken();
/*  66 */       tokens.readChar(']');
/*  67 */       tokens.readToken();
/*  68 */       tokens.readChar('[');
/*  69 */       tokens.readChar('[');
/*     */     } 
/*  71 */     ClusStatManager mgr = this.m_Parent.getStatMgr();
/*  72 */     ClassificationStat stat = (ClassificationStat)mgr.createClusteringStat();
/*  73 */     if (mode == 2) {
/*  74 */       int nbdim = this.m_Parent.getDim();
/*  75 */       double[] propvec = new double[nbdim];
/*  76 */       for (int i = 0; i < nbdim; i++) {
/*  77 */         if (i != 0) tokens.readChar(','); 
/*  78 */         propvec[i] = tokens.readFloat();
/*  79 */         System.out.println("Found = " + propvec[i]);
/*     */       } 
/*  81 */       tokens.readChar(']');
/*  82 */       double count = tokens.readFloat();
/*  83 */       for (int j = 0; j < nbdim; j++) {
/*  84 */         stat.setCount(j, 0, count * propvec[j]);
/*  85 */         stat.setCount(j, 1, count * (1.0D - propvec[j]));
/*     */       } 
/*  87 */       stat.m_SumWeight = count;
/*     */     } else {
/*  89 */       int nb = 1;
/*  90 */       double weight = 0.0D;
/*  91 */       for (int i = 0; i < nb; i++) {
/*  92 */         if (i > 0) tokens.readChar(','); 
/*  93 */         tokens.readToken();
/*  94 */         tokens.readChar(':');
/*  95 */         double nbclass = tokens.readFloat();
/*  96 */         stat.setCount(0, i, nbclass);
/*  97 */         weight += nbclass;
/*     */       } 
/*  99 */       stat.m_SumWeight = weight;
/*     */     } 
/* 101 */     stat.calcMean();
/* 102 */     node.setClusteringStat((ClusStatistic)stat);
/*     */   }
/*     */   
/*     */   public boolean cleanString(String str, ClusNode node) throws IOException {
/* 106 */     String res = str;
/* 107 */     int qps = res.indexOf('?');
/* 108 */     boolean isleaf = true;
/* 109 */     if (qps != -1) {
/* 110 */       isleaf = false;
/* 111 */       res = res.substring(0, qps) + "?";
/*     */     } 
/* 113 */     int count = 0;
/* 114 */     int nbpar = 0;
/* 115 */     String token = "";
/* 116 */     boolean ena = true;
/* 117 */     boolean crena = true;
/* 118 */     FakeTest test = new FakeTest();
/* 119 */     test.setLine(res);
/* 120 */     StringBuffer result = new StringBuffer();
/* 121 */     MStreamTokenizer tokens = MStreamTokenizer.createStringParser(res);
/* 122 */     tokens.setCharTokens(",[]():");
/* 123 */     while (token != null) {
/* 124 */       token = tokens.getToken();
/* 125 */       if (token != null) {
/* 126 */         if (!isleaf) {
/* 127 */           if (token.equals("(")) nbpar++; 
/* 128 */           if (token.equals(")")) nbpar--; 
/* 129 */           if (nbpar == 0 && token.equals(",")) {
/* 130 */             crena = false;
/* 131 */             if (ena) {
/* 132 */               result.append(',');
/* 133 */               test.addLine(result.toString());
/* 134 */               result.setLength(0);
/*     */             } else {
/* 136 */               ena = true;
/*     */             } 
/*     */           } 
/* 139 */         } else if (token.equals("[")) {
/* 140 */           readLeaf(tokens, node);
/*     */           break;
/*     */         } 
/* 143 */         if (ena && crena) result.append(token); 
/* 144 */         crena = true;
/* 145 */         count++;
/*     */       } 
/*     */     } 
/* 148 */     if (!isleaf)
/* 149 */     { if (this.m_Parent.getDebug()) System.out.println("Test: " + res); 
/* 150 */       if (result.length() > 0) test.addLine(result.toString()); 
/* 151 */       node.setTest((NodeTest)test); }
/*     */     
/* 153 */     else if (this.m_Parent.getDebug()) { System.out.println("Leaf: " + node.getClusteringStat()); }
/*     */ 
/*     */     
/* 156 */     return isleaf;
/*     */   }
/*     */   
/*     */   public void skipEntry(String which) throws IOException {
/* 160 */     MStreamTokenizer tokens = this.m_Parent.getStream();
/* 161 */     while (tokens.isNextTokenIn("|+-") != null);
/* 162 */     String token = tokens.readToken();
/* 163 */     if (!token.equals(which)) {
/* 164 */       System.out.println("Error token: " + token + " must be " + which);
/*     */     }
/* 166 */     tokens.readChar(':');
/*     */   }
/*     */   
/*     */   public boolean isLeaf(String node) {
/* 170 */     return (node.indexOf('?') == -1);
/*     */   }
/*     */   
/*     */   public String getTillEOL() throws IOException {
/* 174 */     MStreamTokenizer tokens = this.m_Parent.getStream();
/* 175 */     String str = null;
/* 176 */     while (str == null) {
/* 177 */       str = tokens.readTillEol().trim();
/* 178 */       if (str.equals("")) str = null; 
/*     */     } 
/* 180 */     return str;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\modelio\tilde\TildeTreeReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */