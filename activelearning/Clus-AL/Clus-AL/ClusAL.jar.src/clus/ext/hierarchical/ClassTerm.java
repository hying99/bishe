/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import jeans.math.SingleStat;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.StringUtils;
/*     */ import jeans.util.compound.IndexedItem;
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
/*     */ public class ClassTerm
/*     */   extends IndexedItem
/*     */   implements Node, Comparable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected String m_ID;
/*  41 */   protected HashMap m_Hash = new HashMap<>();
/*  42 */   protected ArrayList m_SubTerms = new ArrayList();
/*  43 */   protected ArrayList m_Parents = new ArrayList();
/*  44 */   protected int m_MinDepth = Integer.MAX_VALUE;
/*  45 */   protected int m_MaxDepth = 0;
/*     */   
/*     */   public ClassTerm() {
/*  48 */     this.m_ID = "root";
/*     */   }
/*     */   
/*     */   public ClassTerm(String id) {
/*  52 */     this.m_ID = id;
/*     */   }
/*     */   
/*     */   public void addParent(ClassTerm term) {
/*  56 */     this.m_Parents.add(term);
/*     */   }
/*     */   
/*     */   public int getNbParents() {
/*  60 */     return this.m_Parents.size();
/*     */   }
/*     */   
/*     */   public ClassTerm getParent(int i) {
/*  64 */     return this.m_Parents.get(i);
/*     */   }
/*     */   
/*     */   public boolean isNumeric() {
/*  68 */     for (int i = 0; i < this.m_ID.length(); i++) {
/*  69 */       if (this.m_ID.charAt(i) < '0' || this.m_ID.charAt(i) > '9') {
/*  70 */         return false;
/*     */       }
/*     */     } 
/*  73 */     return true;
/*     */   }
/*     */   
/*     */   public int compareTo(Object o) {
/*  77 */     ClassTerm other = (ClassTerm)o;
/*  78 */     String s1 = getID();
/*  79 */     String s2 = other.getID();
/*  80 */     if (s1.equals(s2))
/*  81 */       return 0; 
/*  82 */     if (isNumeric() && other.isNumeric()) {
/*  83 */       int i1 = Integer.parseInt(s1);
/*  84 */       int i2 = Integer.parseInt(s2);
/*  85 */       return (i1 > i2) ? 1 : -1;
/*     */     } 
/*  87 */     return s1.compareTo(s2);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addClass(ClassesValue val, int level, ClassHierarchy hier) {
/*  92 */     String cl_idx = val.getClassID(level);
/*  93 */     if (!cl_idx.equals("0")) {
/*  94 */       ClassTerm found = (ClassTerm)this.m_Hash.get(cl_idx);
/*  95 */       if (found == null) {
/*  96 */         boolean is_dag = hier.isDAG();
/*  97 */         if (is_dag) {
/*     */           
/*  99 */           found = hier.getClassTermByNameAddIfNotIn(cl_idx);
/*     */         } else {
/* 101 */           found = new ClassTerm(cl_idx);
/*     */         } 
/*     */ 
/*     */         
/* 105 */         found.addParent(this);
/* 106 */         this.m_Hash.put(cl_idx, found);
/* 107 */         this.m_SubTerms.add(found);
/*     */       } 
/* 109 */       level++;
/* 110 */       if (level < val.getNbLevels()) {
/* 111 */         found.addClass(val, level, hier);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void sortChildrenByID() {
/* 117 */     Collections.sort(this.m_SubTerms);
/*     */   }
/*     */   
/*     */   public void getMeanBranch(boolean[] enabled, SingleStat stat) {
/* 121 */     int nb_branch = 0;
/* 122 */     for (int i = 0; i < getNbChildren(); i++) {
/* 123 */       ClassTerm child = (ClassTerm)getChild(i);
/* 124 */       if (enabled == null || enabled[child.getIndex()]) {
/* 125 */         nb_branch++;
/* 126 */         child.getMeanBranch(enabled, stat);
/*     */       } 
/*     */     } 
/* 129 */     if (nb_branch != 0) {
/* 130 */       stat.addFloat(nb_branch);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean hasChildrenIn(boolean[] enable) {
/* 135 */     for (int i = 0; i < getNbChildren(); i++) {
/* 136 */       ClassTerm child = (ClassTerm)getChild(i);
/* 137 */       if (enable[child.getIndex()]) {
/* 138 */         return true;
/*     */       }
/*     */     } 
/* 141 */     return false;
/*     */   }
/*     */   
/*     */   public Node getParent() {
/* 145 */     return null;
/*     */   }
/*     */   
/*     */   public Node getChild(int idx) {
/* 149 */     return this.m_SubTerms.get(idx);
/*     */   }
/*     */   
/*     */   public int getNbChildren() {
/* 153 */     return this.m_SubTerms.size();
/*     */   }
/*     */   
/*     */   public boolean atTopLevel() {
/* 157 */     return (this.m_Parents.size() == 0);
/*     */   }
/*     */   
/*     */   public boolean atBottomLevel() {
/* 161 */     return (this.m_SubTerms.size() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setParent(Node parent) {}
/*     */   
/*     */   public final String getID() {
/* 168 */     return this.m_ID;
/*     */   }
/*     */   
/*     */   public final void setID(String id) {
/* 172 */     this.m_ID = id;
/*     */   }
/*     */   
/*     */   public String getKeysVector() {
/* 176 */     StringBuffer buf = new StringBuffer();
/* 177 */     ArrayList keys = new ArrayList(this.m_Hash.keySet());
/* 178 */     for (int i = 0; i < keys.size(); i++) {
/* 179 */       if (i != 0) {
/* 180 */         buf.append(", ");
/*     */       }
/* 182 */       buf.append(keys.get(i));
/*     */     } 
/* 184 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public final ClassTerm getByName(String name) {
/* 188 */     return (ClassTerm)this.m_Hash.get(name);
/*     */   }
/*     */   
/*     */   public final ClassTerm getCTParent() {
/* 192 */     return null;
/*     */   }
/*     */   
/*     */   public final void print(int tabs, PrintWriter wrt, double[] counts, double[] weights) {
/* 196 */     for (int i = 0; i < this.m_SubTerms.size(); i++) {
/* 197 */       ClassTerm subterm = this.m_SubTerms.get(i);
/* 198 */       wrt.print(StringUtils.makeString(' ', tabs) + subterm.getID());
/* 199 */       int nb_par = subterm.getNbParents();
/* 200 */       if (nb_par > 1) {
/*     */         
/* 202 */         int p_idx = 0;
/* 203 */         StringBuffer buf = new StringBuffer();
/* 204 */         buf.append("(p: ");
/* 205 */         for (int j = 0; j < nb_par; j++) {
/* 206 */           ClassTerm cr_par = subterm.getParent(j);
/* 207 */           if (cr_par != this) {
/* 208 */             if (p_idx != 0) {
/* 209 */               buf.append(",");
/*     */             }
/* 211 */             buf.append(cr_par.getID());
/* 212 */             p_idx++;
/*     */           } 
/*     */         } 
/* 215 */         buf.append(")");
/* 216 */         wrt.print(" " + buf.toString());
/*     */       } 
/* 218 */       int no = subterm.getIndex();
/* 219 */       if (no == -1) {
/* 220 */         wrt.print(": [error index -1]");
/*     */       } else {
/* 222 */         if (counts != null) {
/* 223 */           double count = counts[no];
/* 224 */           wrt.print(": " + ClusFormat.FOUR_AFTER_DOT.format(count));
/*     */         } 
/* 226 */         if (weights != null) {
/* 227 */           double weight = weights[no];
/* 228 */           wrt.print(": " + ClusFormat.THREE_AFTER_DOT.format(weight));
/*     */         } 
/*     */       } 
/* 231 */       wrt.println();
/* 232 */       subterm.print(tabs + 6, wrt, counts, weights);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void fillVectorNodeAndAncestors(double[] array) {
/* 237 */     int idx = getIndex();
/* 238 */     if (idx != -1 && array[idx] == 0.0D) {
/* 239 */       array[idx] = 1.0D;
/* 240 */       for (int i = 0; i < getNbParents(); i++) {
/* 241 */         getParent(i).fillVectorNodeAndAncestors(array);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void fillBoolArrayNodeAndAncestors(boolean[] array) {
/* 247 */     int idx = getIndex();
/* 248 */     if (idx != -1 && !array[idx]) {
/* 249 */       array[idx] = true;
/* 250 */       for (int i = 0; i < getNbParents(); i++) {
/* 251 */         getParent(i).fillBoolArrayNodeAndAncestors(array);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void fillBoolArrayNodeAndAncestors(Boolean[] array) {
/* 257 */     int idx = getIndex();
/* 258 */     if (idx != -1 && !array[idx].booleanValue()) {
/* 259 */       array[idx] = Boolean.valueOf(true);
/* 260 */       for (int i = 0; i < getNbParents(); i++) {
/* 261 */         getParent(i).fillBoolArrayNodeAndAncestors(array);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void fillBoolDescendants(Boolean[] ids) {
/* 267 */     ids[getIndex()] = Boolean.valueOf(true);
/* 268 */     for (int i = 0; i < this.m_SubTerms.size(); i++) {
/* 269 */       ClassTerm subterm = this.m_SubTerms.get(i);
/* 270 */       subterm.fillBoolDescendants(ids);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void getLeavesIds(Boolean[] ids, int subRootId) {
/* 276 */     int nbc = this.m_SubTerms.size();
/* 277 */     if (nbc == 0 && subRootId != getIndex()) {
/* 278 */       ids[getIndex()] = Boolean.valueOf(true);
/*     */     } else {
/* 280 */       for (int i = 0; i < this.m_SubTerms.size(); i++) {
/* 281 */         ClassTerm subterm = this.m_SubTerms.get(i);
/* 282 */         subterm.getLeavesIds(ids, subRootId);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getNbLeaves() {
/* 288 */     int nbc = this.m_SubTerms.size();
/* 289 */     if (nbc == 0) {
/* 290 */       return 1;
/*     */     }
/* 292 */     int total = 0;
/* 293 */     for (int i = 0; i < this.m_SubTerms.size(); i++) {
/* 294 */       ClassTerm subterm = this.m_SubTerms.get(i);
/* 295 */       total += subterm.getNbLeaves();
/*     */     } 
/* 297 */     return total;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addChild(Node node) {
/* 302 */     String id = ((ClassTerm)node).getID();
/* 303 */     this.m_Hash.put(id, node);
/* 304 */     this.m_SubTerms.add(node);
/*     */   }
/*     */   
/*     */   public void addChildCheckAndParent(ClassTerm node) {
/* 308 */     String id = node.getID();
/* 309 */     if (!this.m_Hash.containsKey(id)) {
/* 310 */       this.m_Hash.put(id, node);
/* 311 */       this.m_SubTerms.add(node);
/* 312 */       node.addParent(this);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChild(int idx) {
/* 318 */     ClassTerm child = (ClassTerm)getChild(idx);
/* 319 */     this.m_Hash.remove(child.getID());
/* 320 */     this.m_SubTerms.remove(idx);
/*     */   }
/*     */   
/*     */   public void numberChildren() {
/* 324 */     this.m_Hash.clear();
/* 325 */     for (int i = 0; i < this.m_SubTerms.size(); i++) {
/* 326 */       ClassTerm subterm = this.m_SubTerms.get(i);
/* 327 */       String key = String.valueOf(i + 1);
/* 328 */       subterm.setID(key);
/* 329 */       this.m_Hash.put(key, subterm);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void removeChild(Node node) {
/* 334 */     ClassTerm child = (ClassTerm)node;
/* 335 */     this.m_Hash.remove(child.getID());
/* 336 */     this.m_SubTerms.remove(child);
/*     */   }
/*     */   public boolean atFirstLevel() {
/* 339 */     ClassTerm parent = this.m_Parents.get(0);
/* 340 */     return parent.atTopLevel();
/*     */   }
/*     */   
/*     */   public int getLevel() {
/* 344 */     int depth = 0;
/* 345 */     ClassTerm parent = getCTParent();
/* 346 */     while (parent != null) {
/* 347 */       parent = parent.getCTParent();
/* 348 */       depth++;
/*     */     } 
/* 350 */     return depth;
/*     */   }
/*     */   
/*     */   public int getMaxDepth() {
/* 354 */     return this.m_MaxDepth;
/*     */   }
/*     */   
/*     */   public int getMinDepth() {
/* 358 */     return this.m_MinDepth;
/*     */   }
/*     */   
/*     */   public void setMinDepth(int depth) {
/* 362 */     this.m_MinDepth = depth;
/*     */   }
/*     */   
/*     */   public void setMaxDepth(int depth) {
/* 366 */     this.m_MaxDepth = depth;
/*     */   }
/*     */   
/*     */   public String toPathString() {
/* 370 */     return toPathString("/");
/*     */   }
/*     */   
/*     */   public String toPathString(String sep) {
/* 374 */     if (getIndex() == -1) {
/* 375 */       return "R";
/*     */     }
/* 377 */     ClassTerm term = this;
/* 378 */     String path = term.getID();
/*     */     while (true) {
/* 380 */       int nb_par = term.getNbParents();
/* 381 */       if (nb_par != 1) {
/* 382 */         return "P" + sep + path;
/*     */       }
/* 384 */       term = term.getParent(0);
/* 385 */       if (term.getIndex() == -1) {
/* 386 */         return path;
/*     */       }
/* 388 */       path = term.getID() + sep + path;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 394 */     return toStringHuman(null);
/*     */   }
/*     */   
/*     */   public String toStringHuman(ClassHierarchy hier) {
/* 398 */     if (hier != null && hier.isDAG()) {
/* 399 */       return getID();
/*     */     }
/* 401 */     return toPathString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getCommonAncestor(String id) {
/* 406 */     for (int i = 0; i < getNbParents(); i++) {
/* 407 */       ClassTerm ct = getParent(i);
/* 408 */       if (ct.getID().equals(id))
/* 409 */         return true; 
/* 410 */       if (ct.getCommonAncestor(id)) {
/* 411 */         return true;
/*     */       }
/*     */     } 
/* 414 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\ClassTerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */