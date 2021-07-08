/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.main.Settings;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
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
/*     */ public class ClusBeam
/*     */ {
/*     */   TreeMap m_Tree;
/*     */   Collection m_Values;
/*     */   int m_MaxWidth;
/*     */   int m_CrWidth;
/*     */   boolean m_RemoveEqualHeur;
/*  41 */   double m_MinValue = Double.NEGATIVE_INFINITY;
/*     */   double m_BeamSimilarity;
/*     */   
/*     */   public ClusBeam(int width, boolean rmEqHeur) {
/*  45 */     this.m_Tree = new TreeMap<>();
/*  46 */     this.m_Values = this.m_Tree.values();
/*  47 */     this.m_MaxWidth = width;
/*  48 */     this.m_RemoveEqualHeur = rmEqHeur;
/*     */   }
/*     */ 
/*     */   
/*     */   public int addIfNotIn(ClusBeamModel model) {
/*  53 */     Double key = new Double(model.getValue());
/*  54 */     ClusBeamTreeElem found = (ClusBeamTreeElem)this.m_Tree.get(key);
/*  55 */     if (found == null) {
/*  56 */       this.m_Tree.put(key, new ClusBeamTreeElem(model));
/*  57 */       return 1;
/*     */     } 
/*  59 */     if (this.m_RemoveEqualHeur) {
/*  60 */       found.setObject(model);
/*  61 */       return 0;
/*     */     } 
/*  63 */     return found.addIfNotIn(model);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeMin() {
/*  69 */     Object first_key = this.m_Tree.firstKey();
/*  70 */     ClusBeamTreeElem min_node = (ClusBeamTreeElem)this.m_Tree.get(first_key);
/*  71 */     if (min_node.hasList()) {
/*  72 */       min_node.removeFirst();
/*     */     } else {
/*  74 */       this.m_Tree.remove(first_key);
/*     */     } 
/*     */   }
/*     */   
/*     */   public ClusBeamModel getBestAndSmallestModel() {
/*  79 */     ClusBeamTreeElem elem = (ClusBeamTreeElem)this.m_Tree.get(this.m_Tree.lastKey());
/*  80 */     if (elem.hasList()) {
/*  81 */       double value = Double.POSITIVE_INFINITY;
/*  82 */       ClusBeamModel result = null;
/*  83 */       ArrayList<ClusBeamModel> arr = elem.getOthers();
/*  84 */       for (int i = 0; i < arr.size(); i++) {
/*  85 */         ClusBeamModel model = arr.get(i);
/*  86 */         int size = model.getModel().getModelSize();
/*  87 */         if (size < value) {
/*  88 */           value = size;
/*  89 */           result = model;
/*     */         } 
/*     */       } 
/*  92 */       return result;
/*     */     } 
/*  94 */     return (ClusBeamModel)elem.getObject();
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusBeamModel getBestModel() {
/*  99 */     ClusBeamTreeElem elem = (ClusBeamTreeElem)this.m_Tree.get(this.m_Tree.lastKey());
/* 100 */     return (ClusBeamModel)elem.getAnObject();
/*     */   }
/*     */   
/*     */   public ClusBeamModel getWorstModel() {
/* 104 */     ClusBeamTreeElem elem = (ClusBeamTreeElem)this.m_Tree.get(this.m_Tree.firstKey());
/* 105 */     return (ClusBeamModel)elem.getAnObject();
/*     */   }
/*     */   
/*     */   public double computeMinValue() {
/* 109 */     return ((Double)this.m_Tree.firstKey()).doubleValue();
/*     */   }
/*     */   
/*     */   public void addModel(ClusBeamModel model) {
/* 113 */     double value = model.getValue();
/* 114 */     if (this.m_MaxWidth == -1) {
/*     */ 
/*     */ 
/*     */       
/* 118 */       this.m_CrWidth += addIfNotIn(model);
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 123 */     else if (this.m_CrWidth < this.m_MaxWidth) {
/* 124 */       this.m_CrWidth += addIfNotIn(model);
/* 125 */       if (this.m_CrWidth == this.m_MaxWidth) {
/* 126 */         this.m_MinValue = computeMinValue();
/*     */       }
/* 128 */     } else if (value >= this.m_MinValue && 
/* 129 */       addIfNotIn(model) == 1) {
/* 130 */       removeMin();
/* 131 */       double min = computeMinValue();
/*     */       
/* 133 */       this.m_MinValue = min;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void print(PrintWriter wrt, int best_n) {
/* 140 */     ArrayList<ClusBeamModel> lst = toArray();
/* 141 */     for (int i = 0; i < Math.min(best_n, lst.size()); i++) {
/* 142 */       if (i != 0) wrt.println(); 
/* 143 */       ClusBeamModel mdl = lst.get(lst.size() - i - 1);
/* 144 */       ClusNode tree = (ClusNode)mdl.getModel();
/* 145 */       double error = Double.NaN;
/* 146 */       wrt.println("Model: " + i + " value: " + mdl.getValue() + " error: " + error + " parent: " + mdl.getParentModelIndex());
/* 147 */       tree.printModel(wrt);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Iterator getIterator() {
/* 152 */     return this.m_Values.iterator();
/*     */   }
/*     */   
/*     */   public ArrayList toArray() {
/* 156 */     ArrayList lst = new ArrayList();
/* 157 */     Iterator<ClusBeamTreeElem> iter = this.m_Values.iterator();
/* 158 */     while (iter.hasNext()) {
/* 159 */       ClusBeamTreeElem elem = iter.next();
/* 160 */       elem.addAll(lst);
/*     */     } 
/* 162 */     return lst;
/*     */   }
/*     */   
/*     */   public int getMaxWidth() {
/* 166 */     return this.m_MaxWidth;
/*     */   }
/*     */   
/*     */   public int getCrWidth() {
/* 170 */     return this.m_CrWidth;
/*     */   }
/*     */   
/*     */   public double getMinValue() {
/* 174 */     return this.m_MinValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void print() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int removeMinUpdated(ClusBeamModel candidate) {
/* 194 */     if (this.m_CrWidth < this.m_MaxWidth) {
/* 195 */       this.m_CrWidth += addIfNotIn(candidate);
/*     */ 
/*     */       
/* 198 */       return 1;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 204 */     double candidateSimilarity = 1.0D - candidate.getDistanceToBeam() / getCrWidth();
/* 205 */     double currentMin = candidate.getValue() - Settings.BEAM_SIMILARITY * candidateSimilarity;
/* 206 */     ArrayList<ClusBeamModel> arr = toArray();
/*     */     
/* 208 */     int bsize = arr.size();
/* 209 */     int min_pos = bsize;
/* 210 */     for (int i = 0; i < bsize; i++) {
/* 211 */       double modelSimilarity = 1.0D - ((ClusBeamModel)arr.get(i)).getDistanceToBeam() / getCrWidth();
/*     */       
/* 213 */       double modelUpdatedHeur = ((ClusBeamModel)arr.get(i)).getValue() - Settings.BEAM_SIMILARITY * modelSimilarity;
/* 214 */       if (currentMin == modelUpdatedHeur && this.m_RemoveEqualHeur) {
/*     */ 
/*     */         
/* 217 */         min_pos = bsize;
/*     */         break;
/*     */       } 
/* 220 */       if (currentMin > modelUpdatedHeur) {
/* 221 */         min_pos = i;
/* 222 */         currentMin = modelUpdatedHeur;
/*     */       } 
/*     */     } 
/* 225 */     if (min_pos != bsize) {
/* 226 */       TreeMap<Object, Object> temp = new TreeMap<>();
/*     */ 
/*     */       
/* 229 */       for (int j = 0; j <= bsize; j++) {
/* 230 */         if (j != min_pos) {
/* 231 */           ClusBeamModel cbm; if (j != bsize) { cbm = arr.get(j); }
/* 232 */           else { cbm = candidate; }
/* 233 */            ClusBeamTreeElem found = (ClusBeamTreeElem)temp.get(Double.valueOf(cbm.getValue()));
/* 234 */           if (found == null) { temp.put(Double.valueOf(cbm.getValue()), new ClusBeamTreeElem(cbm)); }
/* 235 */           else { found.addIfNotIn(cbm); }
/*     */         
/*     */         } 
/* 238 */       }  this.m_Tree = temp;
/* 239 */       this.m_Values = this.m_Tree.values();
/* 240 */       this.m_MinValue = computeMinValue();
/* 241 */       return 1;
/*     */     } 
/* 243 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public int removeMinUpdatedOpt(ClusBeamModel candidate, ClusBeamModelDistance distance) {
/* 248 */     if (this.m_CrWidth < this.m_MaxWidth) {
/* 249 */       this.m_CrWidth += addIfNotIn(candidate);
/*     */ 
/*     */       
/* 252 */       return 1;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 258 */     double candidateSimilarity = 1.0D - candidate.getDistanceToBeam() / getCrWidth();
/* 259 */     double currentMin = candidate.getValue() - Settings.BEAM_SIMILARITY * candidateSimilarity;
/* 260 */     ArrayList<ClusBeamModel> arr = toArray();
/* 261 */     int bsize = arr.size();
/* 262 */     int min_pos = bsize;
/*     */ 
/*     */     
/* 265 */     for (int i = 0; i < bsize; i++) {
/*     */       
/* 267 */       double modelDistance = 1.0D - ((ClusBeamModel)arr.get(i)).getDistanceToBeam() / getCrWidth();
/* 268 */       double modelUpdatedHeur = ((ClusBeamModel)arr.get(i)).getValue() - Settings.BEAM_SIMILARITY * modelDistance;
/*     */       
/* 270 */       if (currentMin == modelUpdatedHeur && this.m_RemoveEqualHeur) {
/*     */ 
/*     */         
/* 273 */         min_pos = bsize;
/*     */         break;
/*     */       } 
/* 276 */       if (currentMin > modelUpdatedHeur) {
/* 277 */         min_pos = i;
/* 278 */         currentMin = modelUpdatedHeur;
/*     */       } 
/*     */     } 
/* 281 */     distance.deductFromBeamOpt(this, candidate, min_pos);
/* 282 */     if (min_pos != bsize) {
/* 283 */       TreeMap<Object, Object> temp = new TreeMap<>();
/*     */ 
/*     */       
/* 286 */       for (int j = 0; j <= bsize; j++) {
/* 287 */         if (j != min_pos) {
/* 288 */           ClusBeamModel cbm; if (j != bsize) { cbm = arr.get(j); }
/* 289 */           else { cbm = candidate; }
/* 290 */            ClusBeamTreeElem found = (ClusBeamTreeElem)temp.get(Double.valueOf(cbm.getValue()));
/* 291 */           if (found == null) { temp.put(Double.valueOf(cbm.getValue()), new ClusBeamTreeElem(cbm)); }
/* 292 */           else { found.addIfNotIn(cbm); }
/*     */         
/*     */         } 
/* 295 */       }  this.m_Tree = temp;
/* 296 */       this.m_Values = this.m_Tree.values();
/* 297 */       this.m_MinValue = computeMinValue();
/* 298 */       return 1;
/*     */     } 
/* 300 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean modelAlreadyIn(ClusBeamModel model) {
/* 310 */     ArrayList<ClusBeamModel> arr = toArray();
/*     */     
/* 312 */     for (int k = 0; k < arr.size(); k++) {
/* 313 */       ClusBeamModel bmodel = arr.get(k);
/* 314 */       if (((ClusNode)bmodel.getModel()).equals(model.getModel())) return true; 
/*     */     } 
/* 316 */     return false;
/*     */   }
/*     */   
/*     */   public void setBeamSimilarity(double similarity) {
/* 320 */     this.m_BeamSimilarity = similarity;
/*     */   }
/*     */   
/*     */   public double getBeamSimilarity() {
/* 324 */     return this.m_BeamSimilarity;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\beamsearch\ClusBeam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */