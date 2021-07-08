/*     */ package clus.ext.ilevelc;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import jeans.util.DisjointSetForest;
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
/*     */ public class DerivedConstraintsComputer
/*     */ {
/*     */   public ArrayList m_Points;
/*     */   public ArrayList m_Constraints;
/*     */   
/*     */   public DerivedConstraintsComputer(ArrayList points, ArrayList constr) {
/*  36 */     this.m_Points = points;
/*  37 */     this.m_Constraints = constr;
/*     */   }
/*     */   
/*     */   public void compute() {
/*  41 */     indexPoints();
/*  42 */     DisjointSetForest dsf = createDSFWithMustLinks();
/*  43 */     ArrayList[] comps = assignPointsToComponents(dsf);
/*  44 */     HashSet set = createCannotLinkSet(dsf);
/*  45 */     this.m_Constraints.clear();
/*  46 */     addTransitiveClosureOfMustLinks(comps);
/*  47 */     addCannotLinkConstraints(set, comps);
/*     */   }
/*     */   
/*     */   public DisjointSetForest createDSFWithMustLinks() {
/*  51 */     DisjointSetForest dsf = new DisjointSetForest(this.m_Points.size());
/*  52 */     dsf.makeSets(this.m_Points.size());
/*  53 */     for (int i = 0; i < this.m_Constraints.size(); i++) {
/*  54 */       ILevelConstraint ic = this.m_Constraints.get(i);
/*  55 */       int type = ic.getType();
/*  56 */       if (type == 0) {
/*  57 */         int t1 = ic.getT1().getIndex();
/*  58 */         int t2 = ic.getT2().getIndex();
/*  59 */         dsf.union(t1, t2);
/*     */       } 
/*     */     } 
/*  62 */     return dsf;
/*     */   }
/*     */   
/*     */   public ArrayList[] assignPointsToComponents(DisjointSetForest dsf) {
/*  66 */     int nbComps = dsf.numberComponents();
/*  67 */     ArrayList[] comps = new ArrayList[nbComps]; int i;
/*  68 */     for (i = 0; i < nbComps; i++) {
/*  69 */       comps[i] = new ArrayList();
/*     */     }
/*  71 */     for (i = 0; i < this.m_Points.size(); i++) {
/*  72 */       comps[dsf.getComponent(i)].add(this.m_Points.get(i));
/*     */     }
/*  74 */     return comps;
/*     */   }
/*     */   
/*     */   public HashSet createCannotLinkSet(DisjointSetForest dsf) {
/*  78 */     HashSet<int[]> set = new HashSet();
/*  79 */     for (int i = 0; i < this.m_Constraints.size(); i++) {
/*  80 */       ILevelConstraint ic = this.m_Constraints.get(i);
/*  81 */       int type = ic.getType();
/*  82 */       if (type == 1) {
/*  83 */         int c1 = dsf.getComponent(ic.getT1().getIndex());
/*  84 */         int c2 = dsf.getComponent(ic.getT2().getIndex());
/*  85 */         set.add(makeEdge(c1, c2));
/*     */       } 
/*     */     } 
/*  88 */     return set;
/*     */   }
/*     */   
/*     */   public void addTransitiveClosureOfMustLinks(ArrayList[] comps) {
/*  92 */     for (int i = 0; i < comps.length; i++) {
/*  93 */       ArrayList<DataTuple> compcomps = comps[i];
/*  94 */       for (int j = 0; j < compcomps.size(); j++) {
/*  95 */         DataTuple tj = compcomps.get(j);
/*  96 */         for (int k = j + 1; k < compcomps.size(); k++) {
/*  97 */           DataTuple tk = compcomps.get(k);
/*  98 */           this.m_Constraints.add(new ILevelConstraint(tj, tk, 0));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addCannotLinkConstraints(HashSet set, ArrayList[] comps) {
/* 105 */     Iterator<int[]> edges = set.iterator();
/* 106 */     while (edges.hasNext()) {
/* 107 */       int[] edge = edges.next();
/* 108 */       ArrayList<DataTuple> comp1 = comps[edge[0]];
/* 109 */       ArrayList<DataTuple> comp2 = comps[edge[1]];
/* 110 */       for (int j = 0; j < comp1.size(); j++) {
/* 111 */         DataTuple tj = comp1.get(j);
/* 112 */         for (int k = 0; k < comp2.size(); k++) {
/* 113 */           DataTuple tk = comp2.get(k);
/* 114 */           this.m_Constraints.add(new ILevelConstraint(tj, tk, 1));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public int[] makeEdge(int i1, int i2) {
/* 121 */     int[] edge = new int[2];
/* 122 */     edge[0] = Math.min(i1, i2);
/* 123 */     edge[1] = Math.max(i1, i2);
/* 124 */     return edge;
/*     */   }
/*     */   
/*     */   public void indexPoints() {
/* 128 */     for (int i = 0; i < this.m_Points.size(); i++) {
/* 129 */       DataTuple tuple = this.m_Points.get(i);
/* 130 */       tuple.setIndex(i);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ilevelc\DerivedConstraintsComputer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */