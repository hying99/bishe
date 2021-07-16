/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.Settings;
/*     */ import clus.util.ClusException;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.LineNumberReader;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import jeans.math.SingleStat;
/*     */ import jeans.tree.CompleteTreeIterator;
/*     */ import jeans.util.array.StringTable;
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
/*     */ public class ClassHierarchy
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int TEST = 0;
/*     */   public static final int ERROR = 1;
/*     */   public static final int TREE = 0;
/*     */   public static final int DAG = 1;
/*  48 */   protected int m_MaxDepth = 0;
/*  49 */   protected int m_HierType = 0;
/*     */   protected ClassesTuple m_Eval;
/*  51 */   public ArrayList m_ClassList = new ArrayList();
/*  52 */   private HashMap m_ClassMap = new HashMap<>();
/*     */   private ClassTerm m_Root;
/*     */   protected NumericAttrType[] m_DummyTypes;
/*     */   protected boolean m_IsLocked;
/*     */   protected transient double[] m_Weights;
/*  57 */   protected transient Hashtable m_ErrorWeights = new Hashtable<>();
/*     */   
/*     */   protected transient ClassesAttrType m_Type;
/*     */   public HashMap<String, LinkedList<String>> m_Paths;
/*     */   public static final char DFS_WHITE = '\000';
/*     */   
/*     */   public ClassHierarchy(ClassesAttrType type) {
/*  64 */     this(new ClassTerm());
/*  65 */     setType(type);
/*     */   } public static final char DFS_GRAY = '\001'; public static final char DFS_BLACK = '\002';
/*     */   public ClassHierarchy() {}
/*     */   public ClassHierarchy(ClassTerm root) {
/*  69 */     this.m_Root = root;
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/*  73 */     return this.m_Type.getSettings();
/*     */   }
/*     */   
/*     */   public final void setType(ClassesAttrType type) {
/*  77 */     this.m_Type = type;
/*     */   }
/*     */   
/*     */   public final ClassesAttrType getType() {
/*  81 */     return this.m_Type;
/*     */   }
/*     */   
/*     */   public final void addClass(ClassesValue val) {
/*  85 */     if (!isLocked()) {
/*  86 */       getM_Root().addClass(val, 0, this);
/*     */     }
/*     */   }
/*     */   
/*     */   public final void print(PrintWriter wrt) {
/*  91 */     getM_Root().print(0, wrt, (double[])null, (double[])null);
/*     */   }
/*     */   
/*     */   public final void print(PrintWriter wrt, double[] counts, double[] weights) {
/*  95 */     getM_Root().print(0, wrt, counts, weights);
/*     */   }
/*     */   
/*     */   public final void print(PrintWriter wrt, double[] counts) {
/*  99 */     getM_Root().print(0, wrt, counts, this.m_Weights);
/*     */   }
/*     */   
/*     */   public final int getMaxDepth() {
/* 103 */     return getM_Root().getMaxDepth();
/*     */   }
/*     */   
/*     */   public final ClassTerm getRoot() {
/* 107 */     return getM_Root();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final void initClassListRecursiveTree(ClassTerm term) {
/* 113 */     getM_ClassList().add(term);
/* 114 */     term.sortChildrenByID();
/* 115 */     for (int i = 0; i < term.getNbChildren(); i++) {
/* 116 */       initClassListRecursiveTree((ClassTerm)term.getChild(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public final void initClassListRecursiveDAG(ClassTerm term, HashSet<String> set) {
/* 121 */     if (!set.contains(term.getID())) {
/*     */       
/* 123 */       getM_ClassList().add(term);
/* 124 */       term.sortChildrenByID();
/* 125 */       for (int i = 0; i < term.getNbChildren(); i++) {
/* 126 */         initClassListRecursiveDAG((ClassTerm)term.getChild(i), set);
/*     */       }
/* 128 */       set.add(term.getID());
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void numberHierarchy() {
/* 133 */     getM_Root().setIndex(-1);
/* 134 */     getM_Root().sortChildrenByID();
/* 135 */     getM_ClassList().clear();
/* 136 */     if (isDAG()) {
/*     */       
/* 138 */       HashSet set = new HashSet();
/* 139 */       for (int j = 0; j < getM_Root().getNbChildren(); j++) {
/* 140 */         initClassListRecursiveDAG((ClassTerm)getM_Root().getChild(j), set);
/*     */       }
/*     */     } else {
/* 143 */       for (int j = 0; j < getM_Root().getNbChildren(); j++) {
/* 144 */         initClassListRecursiveTree((ClassTerm)getM_Root().getChild(j));
/*     */       }
/*     */     } 
/* 147 */     for (int i = 0; i < getTotal(); i++) {
/* 148 */       ClassTerm term = getTermAt(i);
/* 149 */       term.setIndex(i);
/*     */     } 
/*     */ 
/*     */     
/* 153 */     setLocked(true);
/*     */   }
/*     */   
/*     */   void getAllParentChildTuplesRecursive(ClassTerm node, boolean[] visited, ArrayList<String> parentchilds) {
/* 157 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 158 */       ClassTerm child = (ClassTerm)node.getChild(i);
/* 159 */       parentchilds.add(node.getID() + "/" + child.getID());
/* 160 */       if (!visited[child.getIndex()]) {
/*     */         
/* 162 */         visited[child.getIndex()] = true;
/* 163 */         getAllParentChildTuplesRecursive(child, visited, parentchilds);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public ArrayList getAllParentChildTuples() {
/* 169 */     ArrayList parentchilds = new ArrayList();
/* 170 */     boolean[] visited = new boolean[getTotal()];
/* 171 */     getAllParentChildTuplesRecursive(getM_Root(), visited, parentchilds);
/* 172 */     return parentchilds;
/*     */   }
/*     */   
/*     */   void getAllPathsRecursive(ClassTerm node, String crpath, boolean[] visited, ArrayList<String> paths) {
/* 176 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 177 */       ClassTerm child = (ClassTerm)node.getChild(i);
/* 178 */       String new_path = (node.getIndex() == -1) ? "" : (crpath + "/");
/* 179 */       new_path = new_path + child.getID();
/* 180 */       paths.add(new_path);
/* 181 */       if (!visited[child.getIndex()]) {
/*     */         
/* 183 */         visited[child.getIndex()] = true;
/* 184 */         getAllPathsRecursive(child, new_path, visited, paths);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public ArrayList getAllPaths() {
/* 190 */     ArrayList paths = new ArrayList();
/* 191 */     boolean[] visited = new boolean[getTotal()];
/* 192 */     getAllPathsRecursive(getM_Root(), "", visited, paths);
/* 193 */     return paths;
/*     */   }
/*     */   
/*     */   public void addAllClasses(ClassesTuple tuple, boolean[] matrix) {
/* 197 */     int idx = 0;
/* 198 */     tuple.setSize(countOnes(matrix));
/* 199 */     for (int i = 0; i < getTotal(); i++) {
/* 200 */       if (matrix[i]) {
/* 201 */         tuple.setItemAt(new ClassesValue(getTermAt(i), 1.0D), idx++);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void fillBooleanMatrixMaj(double[] mean, boolean[] matrix, double treshold) {
/* 207 */     for (int i = 0; i < getTotal(); i++) {
/* 208 */       ClassTerm term = getTermAt(i);
/* 209 */       if (mean[term.getIndex()] >= treshold / 100.0D) {
/* 210 */         matrix[term.getIndex()] = true;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void removeParentNodesRec(ClassTerm node, boolean[] matrix) {
/* 216 */     if (matrix[node.getIndex()]) {
/* 217 */       ClassTerm parent = node.getCTParent();
/* 218 */       while (parent.getIndex() != -1 && matrix[parent.getIndex()]) {
/* 219 */         matrix[parent.getIndex()] = false;
/* 220 */         parent = parent.getCTParent();
/*     */       } 
/*     */     } 
/* 223 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 224 */       removeParentNodesRec((ClassTerm)node.getChild(i), matrix);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void removeParentNodes(ClassTerm node, boolean[] matrix) {
/* 229 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 230 */       removeParentNodesRec((ClassTerm)node.getChild(i), matrix);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeParentNodesRecursive(ClassTerm term, boolean[] array) {
/* 235 */     for (int i = 0; i < term.getNbParents(); i++) {
/* 236 */       ClassTerm par = term.getParent(i);
/* 237 */       if (par.getIndex() != -1 && array[par.getIndex()]) {
/* 238 */         array[par.getIndex()] = false;
/* 239 */         removeParentNodesRecursive(par, array);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void removeParentNodes(boolean[] array) {
/* 245 */     for (int i = 0; i < getTotal(); i++) {
/* 246 */       ClassTerm term = getTermAt(i);
/* 247 */       if (term.getIndex() != -1 && array[term.getIndex()]) {
/* 248 */         removeParentNodesRecursive(term, array);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public static int countOnes(boolean[] matrix) {
/* 254 */     int count = 0;
/* 255 */     for (int i = 0; i < matrix.length; i++) {
/* 256 */       if (matrix[i]) {
/* 257 */         count++;
/*     */       }
/*     */     } 
/* 260 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClassesTuple getBestTupleMajNoParents(double[] mean, double treshold) {
/* 265 */     boolean[] classes = new boolean[getTotal()];
/* 266 */     fillBooleanMatrixMaj(mean, classes, treshold);
/* 267 */     removeParentNodes(getRoot(), classes);
/* 268 */     ClassesTuple tuple = new ClassesTuple();
/* 269 */     addAllClasses(tuple, classes);
/* 270 */     return tuple;
/*     */   }
/*     */   
/*     */   public ClassesTuple getBestTupleMaj(double[] mean, double treshold) {
/* 274 */     boolean[] classes = new boolean[getTotal()];
/* 275 */     fillBooleanMatrixMaj(mean, classes, treshold);
/* 276 */     ClassesTuple tuple = new ClassesTuple();
/* 277 */     addAllClasses(tuple, classes);
/* 278 */     return tuple;
/*     */   }
/*     */   
/*     */   public ClassesTuple getTuple(boolean[] nodes) {
/* 282 */     ClassesTuple result = new ClassesTuple();
/* 283 */     addAllClasses(result, nodes);
/* 284 */     return result;
/*     */   }
/*     */   
/*     */   public final CompleteTreeIterator getNoRootIter() {
/* 288 */     CompleteTreeIterator iter = new CompleteTreeIterator(getM_Root());
/* 289 */     if (iter.hasMoreNodes()) {
/* 290 */       iter.getNextNode();
/*     */     }
/* 292 */     return iter;
/*     */   }
/*     */   
/*     */   public final CompleteTreeIterator getRootIter() {
/* 296 */     return new CompleteTreeIterator(getM_Root());
/*     */   }
/*     */   
/*     */   public final double[] getWeights() {
/* 300 */     return this.m_Weights;
/*     */   }
/*     */   
/*     */   public final void calcWeights() {
/* 304 */     HierNodeWeights ws = new HierNodeWeights();
/* 305 */     int wtype = getSettings().getHierWType();
/* 306 */     double widec = getSettings().getHierWParam();
/* 307 */     ws.initExponentialDepthWeights(this, wtype, widec);
/* 308 */     this.m_Weights = ws.getWeights();
/*     */   }
/*     */   
/*     */   public final void calcMaxDepth() {
/* 312 */     this.m_MaxDepth = 0;
/* 313 */     for (int i = 0; i < getTotal(); i++) {
/* 314 */       ClassTerm term = getTermAt(i);
/* 315 */       this.m_MaxDepth = Math.max(this.m_MaxDepth, term.getMaxDepth());
/*     */     } 
/*     */   }
/*     */   
/*     */   public final SingleStat getMeanBranch(boolean[] enabled) {
/* 320 */     SingleStat stat = new SingleStat();
/* 321 */     getM_Root().getMeanBranch(enabled, stat);
/* 322 */     return stat;
/*     */   }
/*     */   
/*     */   public final int getTotal() {
/* 326 */     return getM_ClassList().size();
/*     */   }
/*     */   
/*     */   public final int getDepth() {
/* 330 */     return this.m_MaxDepth;
/*     */   }
/*     */   
/*     */   public final int[] getClassesByLevel() {
/* 334 */     int[] res = new int[getDepth() + 2];
/* 335 */     countClassesRecursive(getM_Root(), 0, res);
/* 336 */     return res;
/*     */   }
/*     */   
/*     */   public final void countClassesRecursive(ClassTerm root, int depth, int[] cls) {
/* 340 */     cls[depth] = cls[depth] + 1;
/* 341 */     for (int i = 0; i < root.getNbChildren(); i++) {
/* 342 */       countClassesRecursive((ClassTerm)root.getChild(i), depth + 1, cls);
/*     */     }
/*     */   }
/*     */   
/*     */   public final void initialize() {
/* 347 */     numberHierarchy();
/* 348 */     calcWeights();
/* 349 */     calcMaxDepth();
/* 350 */     ClusSchema schema = this.m_Type.getSchema();
/* 351 */     int maxIndex = schema.getNbAttributes();
/* 352 */     this.m_DummyTypes = new NumericAttrType[getTotal()];
/* 353 */     for (int i = 0; i < getTotal(); i++) {
/* 354 */       NumericAttrType type = new NumericAttrType("H" + i);
/* 355 */       type.setIndex(maxIndex++);
/* 356 */       type.setSchema(schema);
/* 357 */       this.m_DummyTypes[i] = type;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean[] removeInfrequentClasses(WHTDStatistic stat, double minfreq) {
/* 362 */     boolean[] removed = new boolean[getTotal()];
/* 363 */     ArrayList<ClassTerm> new_cls = new ArrayList(); int i;
/* 364 */     for (i = 0; i < getTotal(); i++) {
/* 365 */       double mean = stat.getMean(i);
/* 366 */       if (mean == 0.0D || mean < minfreq) {
/* 367 */         ClassTerm trm = getTermAt(i);
/* 368 */         for (int j = 0; j < trm.getNbParents(); j++) {
/* 369 */           ClassTerm par = trm.getParent(j);
/* 370 */           par.removeChild(trm);
/*     */         } 
/* 372 */         removed[trm.getIndex()] = true;
/*     */       } else {
/* 374 */         new_cls.add(getTermAt(i));
/*     */       } 
/*     */     } 
/* 377 */     getM_ClassList().clear();
/* 378 */     getM_ClassMap().clear();
/* 379 */     if (isDAG()) {
/* 380 */       for (i = 0; i < new_cls.size(); i++) {
/* 381 */         ClassTerm trm = new_cls.get(i);
/* 382 */         getM_ClassMap().put(trm.getID(), trm);
/*     */       } 
/*     */     }
/* 385 */     return removed;
/*     */   }
/*     */   
/*     */   public final NumericAttrType[] getDummyAttrs() {
/* 389 */     return this.m_DummyTypes;
/*     */   }
/*     */   
/*     */   public final void showSummary() {
/* 393 */     int leaves = 0;
/* 394 */     int depth = getMaxDepth();
/* 395 */     System.out.println("Depth: " + depth);
/* 396 */     System.out.println("Nodes: " + getTotal());
/* 397 */     ClassTerm root = getRoot();
/* 398 */     int nb = root.getNbChildren();
/* 399 */     for (int i = 0; i < nb; i++) {
/* 400 */       ClassTerm chi = (ClassTerm)root.getChild(i);
/* 401 */       int nbl = chi.getNbLeaves();
/* 402 */       System.out.println("Child " + i + ": " + chi.getID() + " " + nbl);
/* 403 */       leaves += nbl;
/*     */     } 
/* 405 */     System.out.println("Leaves: " + leaves);
/*     */   }
/*     */   
/*     */   public final ClassTerm getClassTermTree(ClassesValue vl) throws ClusException {
/* 409 */     int pos = 0;
/* 410 */     int nb_level = vl.getNbLevels();
/* 411 */     ClassTerm subterm = getM_Root();
/*     */     while (true) {
/* 413 */       if (pos >= nb_level) {
/* 414 */         return subterm;
/*     */       }
/* 416 */       String lookup = vl.getClassID(pos);
/* 417 */       if (lookup.equals("0")) {
/* 418 */         return subterm;
/*     */       }
/* 420 */       ClassTerm found = subterm.getByName(lookup);
/* 421 */       if (found == null) {
/* 422 */         throw new ClusException("Classes value not in tree hierarchy: " + vl.toPathString() + " (lookup: " + lookup + ", term: " + subterm.toPathString() + ", subterms: " + subterm.getKeysVector() + ")");
/*     */       }
/* 424 */       subterm = found;
/*     */       
/* 426 */       pos++;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final ClassTerm getClassTermDAG(ClassesValue vl) throws ClusException {
/* 432 */     ClassTerm term = getClassTermByName(vl.getMostSpecificClass());
/* 433 */     if (term == null) {
/* 434 */       throw new ClusException("Classes value not in DAG hierarchy: " + vl.toPathString());
/*     */     }
/* 436 */     return term;
/*     */   }
/*     */   
/*     */   public final ClassTerm getClassTerm(ClassesValue vl) throws ClusException {
/* 440 */     if (isTree()) {
/* 441 */       return getClassTermTree(vl);
/*     */     }
/* 443 */     return getClassTermDAG(vl);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getClassIndex(ClassesValue vl) throws ClusException {
/* 448 */     return getClassTerm(vl).getIndex();
/*     */   }
/*     */   
/*     */   public final double getWeight(int idx) {
/* 452 */     return this.m_Weights[idx];
/*     */   }
/*     */   
/*     */   public final void setEvalClasses(ClassesTuple eval) {
/* 456 */     this.m_Eval = eval;
/*     */   }
/*     */   
/*     */   public final ClassesTuple getEvalClasses() {
/* 460 */     return this.m_Eval;
/*     */   }
/*     */   
/*     */   public final boolean[] getEvalClassesVector() {
/* 464 */     if (this.m_Eval == null) {
/* 465 */       boolean[] res = new boolean[getTotal()];
/* 466 */       Arrays.fill(res, true);
/* 467 */       return res;
/*     */     } 
/* 469 */     return this.m_Eval.getVectorBoolean(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addChildrenToRoot() {
/* 475 */     Iterator<ClassTerm> iter = getM_ClassMap().values().iterator();
/* 476 */     while (iter.hasNext()) {
/* 477 */       ClassTerm term = iter.next();
/* 478 */       if (term != getM_Root() && term.atTopLevel()) {
/* 479 */         getM_Root().addChildCheckAndParent(term);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addParentChildTuple(String parent, String child) throws ClusException {
/* 485 */     ClassTerm parent_t = getClassTermByNameAddIfNotIn(parent);
/* 486 */     ClassTerm child_t = getClassTermByNameAddIfNotIn(child);
/* 487 */     if (parent_t.getByName(child) != null) {
/* 488 */       throw new ClusException("Duplicate parent-child relation '" + parent + "' -> '" + child + "' in DAG definition in .arff file");
/*     */     }
/* 490 */     parent_t.addChildCheckAndParent(child_t);
/*     */   }
/*     */   
/*     */   public void loadDAG(String[] cls) throws IOException, ClusException {
/* 494 */     addClassTerm("root", getRoot());
/* 495 */     for (int i = 0; i < cls.length; i++) {
/* 496 */       String[] rel = cls[i].split("\\s*\\/\\s*");
/* 497 */       if (rel.length != 2)
/*     */       {
/* 499 */         throw new ClusException("Illegal parent child tuple in .arff");
/*     */       }
/* 501 */       String parent = rel[0];
/* 502 */       String child = rel[1];
/* 503 */       addParentChildTuple(parent, child);
/*     */     } 
/* 505 */     addChildrenToRoot();
/*     */   }
/*     */   
/*     */   public void loadDAG(String fname) throws IOException, ClusException {
/* 509 */     String line = null;
/* 510 */     LineNumberReader rdr = new LineNumberReader(new FileReader(fname));
/* 511 */     while ((line = rdr.readLine()) != null) {
/* 512 */       line = line.trim();
/* 513 */       if (!line.equals("")) {
/* 514 */         String[] rel = line.split("\\s*\\,\\s*");
/* 515 */         if (rel.length != 2) {
/* 516 */           throw new ClusException("Illegal line '" + line + "' in DAG definition file: '" + fname + "'");
/*     */         }
/* 518 */         String parent = rel[0];
/* 519 */         String child = rel[1];
/* 520 */         addParentChildTuple(parent, child);
/*     */       } 
/*     */     } 
/* 523 */     rdr.close();
/* 524 */     addChildrenToRoot();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void findCycleRecursive(ClassTerm term, char[] visited, ClassTerm[] pi, boolean[] hasCycle) {
/* 532 */     visited[term.getIndex()] = '\001';
/* 533 */     for (int i = 0; i < term.getNbChildren(); i++) {
/* 534 */       ClassTerm child = (ClassTerm)term.getChild(i);
/* 535 */       if (visited[child.getIndex()] == '\000') {
/* 536 */         pi[child.getIndex()] = term;
/* 537 */         findCycleRecursive(child, visited, pi, hasCycle);
/* 538 */       } else if (visited[child.getIndex()] == '\001') {
/* 539 */         System.out.println("Cycle: ");
/* 540 */         System.out.print("(" + term.getID() + "," + child.getID() + ")");
/* 541 */         ClassTerm w = term;
/*     */         while (true) {
/* 543 */           System.out.print("; (" + w.getID() + "," + pi[w.getIndex()].getID() + ")");
/* 544 */           w = pi[w.getIndex()];
/* 545 */           if (w == child)
/* 546 */           { System.out.println();
/* 547 */             hasCycle[0] = true; break; } 
/*     */         } 
/*     */       } 
/* 550 */     }  visited[term.getIndex()] = '\002';
/*     */   }
/*     */   
/*     */   public void findCycle() {
/* 554 */     char[] visited = new char[getTotal()];
/* 555 */     ClassTerm[] pi = new ClassTerm[getTotal()];
/* 556 */     boolean[] hasCycle = new boolean[1];
/* 557 */     Arrays.fill(visited, false);
/* 558 */     for (int i = 0; i < getM_ClassList().size(); i++) {
/* 559 */       ClassTerm term = getTermAt(i);
/* 560 */       if (visited[term.getIndex()] == '\000') {
/* 561 */         findCycleRecursive(term, visited, pi, hasCycle);
/*     */       }
/*     */     } 
/* 564 */     if (hasCycle[0]) {
/* 565 */       System.exit(-1);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeTargets(RowData data, ClusSchema schema, String name) throws ClusException, IOException {
/* 570 */     double[] wis = getWeights();
/* 571 */     PrintWriter wrt = new PrintWriter(new FileWriter(name + ".weights"));
/* 572 */     wrt.print("weights(X) :- X = [");
/* 573 */     for (int i = 0; i < wis.length; i++) {
/* 574 */       if (i != 0) {
/* 575 */         wrt.print(",");
/*     */       }
/* 577 */       wrt.print(wis[i]);
/*     */     } 
/* 579 */     wrt.println("].");
/* 580 */     wrt.println();
/* 581 */     ClassTerm[] terms = new ClassTerm[wis.length];
/* 582 */     CompleteTreeIterator iter = getRootIter();
/* 583 */     while (iter.hasMoreNodes()) {
/* 584 */       ClassTerm node = (ClassTerm)iter.getNextNode();
/* 585 */       if (node.getIndex() != -1) {
/* 586 */         terms[node.getIndex()] = node;
/*     */       }
/*     */     } 
/* 589 */     for (int j = 0; j < wis.length; j++) {
/* 590 */       wrt.print("% class " + terms[j] + ": ");
/* 591 */       wrt.println(wis[j]);
/*     */     } 
/* 593 */     wrt.close();
/* 594 */     ClusAttrType[] keys = schema.getAllAttrUse(4);
/* 595 */     int sidx = getType().getArrayIndex();
/* 596 */     wrt = new PrintWriter(new FileWriter(name + ".targets"));
/* 597 */     for (int k = 0; k < data.getNbRows(); k++) {
/* 598 */       DataTuple tuple = data.getTuple(k);
/* 599 */       int pos = 0;
/* 600 */       for (int m = 0; m < keys.length; m++) {
/* 601 */         if (pos != 0) {
/* 602 */           wrt.print(",");
/*     */         }
/* 604 */         wrt.print(keys[m].getString(tuple));
/* 605 */         pos++;
/*     */       } 
/* 607 */       ClassesTuple target = (ClassesTuple)tuple.getObjVal(sidx);
/* 608 */       double[] vec = target.getVectorNodeAndAncestors(this);
/* 609 */       wrt.print(",");
/* 610 */       wrt.print(target.toString());
/* 611 */       wrt.print(",[");
/* 612 */       for (int n = 0; n < vec.length; n++) {
/* 613 */         if (n != 0) {
/* 614 */           wrt.print(",");
/*     */         }
/* 616 */         wrt.print(vec[n]);
/*     */       } 
/* 618 */       wrt.println("]");
/*     */     } 
/* 620 */     wrt.close();
/*     */   }
/*     */   
/*     */   public LinkedList<String> getAncestorsString(String id) throws ClusException {
/* 624 */     LinkedList<String> ancestors = new LinkedList<>();
/* 625 */     ClassTerm ct = getClassTermTree(new ClassesValue(id, getType().getTable()));
/* 626 */     getAncestorsString(ct, ancestors);
/* 627 */     ancestors.remove("R");
/* 628 */     return ancestors;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLabelDepth(String label) throws ClusException {
/* 637 */     return getAncestorsString(label).size();
/*     */   }
/*     */   
/*     */   public void getAncestorsString(ClassTerm ct, LinkedList<String> ancestors) {
/* 641 */     ancestors.add(ct.toPathString());
/* 642 */     for (int i = 0; i < ct.getNbParents(); i++) {
/* 643 */       getAncestorsString(ct.getParent(i), ancestors);
/*     */     }
/*     */   }
/*     */   
/*     */   public LinkedList<String> getDescendantsString(String id) throws ClusException {
/* 648 */     LinkedList<String> descendants = new LinkedList<>();
/* 649 */     ClassTerm ct = getClassTermTree(new ClassesValue(id, getType().getTable()));
/* 650 */     getDescendantsString(ct, descendants);
/* 651 */     return descendants;
/*     */   }
/*     */   public HashSet<String> getChildren(String id) throws ClusException {
/* 654 */     HashSet<String> children = new HashSet<>();
/* 655 */     ClassTerm ct = getClassTermTree(new ClassesValue(id, getType().getTable()));
/* 656 */     for (int i = 0; i < ct.getNbChildren(); i++) {
/* 657 */       children.add(((ClassTerm)ct.getChild(i)).toPathString());
/*     */     }
/* 659 */     return children;
/*     */   }
/*     */   public void getDescendantsString(ClassTerm ct, LinkedList<String> descendants) {
/* 662 */     descendants.add(ct.toPathString());
/* 663 */     for (int i = 0; i < ct.getNbChildren(); i++) {
/* 664 */       getDescendantsString((ClassTerm)ct.getChild(i), descendants);
/*     */     }
/*     */   }
/*     */   
/*     */   public LinkedList<String> getLeaves() throws ClusException {
/* 669 */     LinkedList<String> leaves = new LinkedList<>();
/* 670 */     getLeaves(this.m_Root, leaves);
/* 671 */     return leaves;
/*     */   }
/*     */   
/*     */   private void getLeaves(ClassTerm ct, LinkedList<String> leaves) {
/* 675 */     if (ct.getNbChildren() == 0) {
/* 676 */       leaves.add(ct.toPathString());
/*     */     } else {
/* 678 */       for (int i = 0; i < ct.getNbChildren(); i++) {
/* 679 */         getLeaves((ClassTerm)ct.getChild(i), leaves);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setLocked(boolean lock) {
/* 685 */     this.m_IsLocked = lock;
/*     */   }
/*     */   
/*     */   public boolean isLocked() {
/* 689 */     return this.m_IsLocked;
/*     */   }
/*     */   
/*     */   public void setHierType(int type) {
/* 693 */     this.m_HierType = type;
/*     */   }
/*     */   
/*     */   public boolean isTree() {
/* 697 */     return (this.m_HierType == 0);
/*     */   }
/*     */   
/*     */   public boolean isDAG() {
/* 701 */     return (this.m_HierType == 1);
/*     */   }
/*     */   
/*     */   public ClassTerm getClassTermByNameAddIfNotIn(String id) {
/* 705 */     ClassTerm found = getClassTermByName(id);
/* 706 */     if (found == null) {
/* 707 */       found = new ClassTerm(id);
/* 708 */       addClassTerm(id, found);
/*     */     } 
/* 710 */     return found;
/*     */   }
/*     */   
/*     */   public ClassTerm getClassTermByName(String id) {
/* 714 */     return (ClassTerm)getM_ClassMap().get(id);
/*     */   }
/*     */   
/*     */   public void addClassTerm(String id, ClassTerm term) {
/* 718 */     getM_ClassMap().put(id, term);
/*     */   }
/*     */   
/*     */   public void addClassTerm(ClassTerm term) {
/* 722 */     getM_ClassList().add(term);
/*     */   }
/*     */   
/*     */   public ClassTerm getTermAt(int i) {
/* 726 */     return getM_ClassList().get(i);
/*     */   }
/*     */   
/*     */   public ClassesValue createValueByName(String name, StringTable table) throws ClusException {
/* 730 */     ClassesValue val = new ClassesValue(name, table);
/* 731 */     ClassTerm term = getClassTerm(val);
/* 732 */     val.setClassTerm(term);
/* 733 */     return val;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList getM_ClassList() {
/* 740 */     return this.m_ClassList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setM_ClassList(ArrayList m_ClassList) {
/* 747 */     this.m_ClassList = m_ClassList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassTerm getM_Root() {
/* 754 */     return this.m_Root;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setM_Root(ClassTerm m_Root) {
/* 761 */     this.m_Root = m_Root;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashMap getM_ClassMap() {
/* 768 */     return this.m_ClassMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setM_ClassMap(HashMap m_ClassMap) {
/* 775 */     this.m_ClassMap = m_ClassMap;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\ClassHierarchy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */