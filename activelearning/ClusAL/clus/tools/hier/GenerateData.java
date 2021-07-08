/*     */ package clus.tools.hier;
/*     */ 
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.ext.hierarchical.HierIO;
/*     */ import clus.util.ClusFormat;
/*     */ import clus.util.ClusRandom;
/*     */ import java.util.Arrays;
/*     */ import jeans.io.MyFile;
/*     */ import jeans.math.SingleStat;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.MyArray;
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
/*     */ public class GenerateData
/*     */ {
/*  36 */   public static double MEAN_BRANCH = 2.75D;
/*  37 */   public static int MAX_BRANCH = 3;
/*  38 */   public static int MAX_DEPTH = 4;
/*  39 */   public static int MAX_CLASS = 1;
/*  40 */   public static int NB_DATA = 10000;
/*  41 */   public static double MAX_ERROR = 0.001D;
/*     */   
/*     */   public static ClassTerm createSimpleHierarchy() {
/*  44 */     ClassTerm term = new ClassTerm();
/*  45 */     createSimpleHierarchy(term, 0);
/*  46 */     return term;
/*     */   }
/*     */   
/*     */   public static void createSimpleHierarchy(ClassTerm term, int depth) {
/*  50 */     if (depth >= MAX_DEPTH)
/*  51 */       return;  for (int i = 0; i < MAX_BRANCH; i++) {
/*  52 */       ClassTerm child = new ClassTerm(String.valueOf(i + 1));
/*  53 */       child.addParent(child);
/*  54 */       term.addChild((Node)child);
/*  55 */       createSimpleHierarchy(child, depth + 1);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static ClassTerm createInitHierarchy() {
/*  60 */     ClassTerm term = new ClassTerm();
/*  61 */     createInitHier(term, 0);
/*  62 */     return term;
/*     */   }
/*     */   
/*     */   public static void createInitHier(ClassTerm term, int depth) {
/*  66 */     if (depth >= MAX_DEPTH)
/*  67 */       return;  int arity = ClusRandom.nextInt(3, (int)Math.floor(2.0D * MEAN_BRANCH));
/*  68 */     for (int i = 0; i < arity; i++) {
/*  69 */       ClassTerm child = new ClassTerm(String.valueOf(i + 1));
/*  70 */       child.addParent(term);
/*  71 */       term.addChild((Node)child);
/*  72 */       createInitHier(child, depth + 1);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static ClassTerm createHierarchy() {
/*  77 */     boolean done = false;
/*  78 */     boolean increase = true;
/*  79 */     int count = 0;
/*  80 */     ClassTerm root = createInitHierarchy();
/*  81 */     MyArray terms = new MyArray();
/*  82 */     addAll(root, terms);
/*  83 */     MyArray sel_from = new MyArray();
/*  84 */     SingleStat stat = new SingleStat();
/*  85 */     while (!done) {
/*  86 */       int nb_possible = 0;
/*  87 */       sel_from.removeAllElements();
/*  88 */       if (increase) {
/*  89 */         for (int j = 0; j < terms.size(); j++) {
/*  90 */           ClassTerm trm = (ClassTerm)terms.elementAt(j);
/*  91 */           if (trm.getLevel() < MAX_DEPTH) {
/*  92 */             nb_possible++;
/*  93 */             sel_from.addElement(trm);
/*     */           } 
/*     */         } 
/*  96 */         int to_expand = ClusRandom.nextInt(3, nb_possible);
/*  97 */         ClassTerm myexp = (ClassTerm)sel_from.elementAt(to_expand);
/*  98 */         int name = myexp.getNbChildren();
/*  99 */         ClassTerm child = new ClassTerm(String.valueOf(name + 1));
/* 100 */         child.addParent(myexp);
/* 101 */         myexp.addChild((Node)child);
/* 102 */         terms.addElement(child);
/*     */       } else {
/* 104 */         for (int j = 0; j < terms.size(); j++) {
/* 105 */           ClassTerm trm = (ClassTerm)terms.elementAt(j);
/* 106 */           if (trm.getNbChildren() > 0) {
/* 107 */             nb_possible++;
/* 108 */             sel_from.addElement(trm);
/*     */           } 
/*     */         } 
/*     */         
/* 112 */         int to_decrease = ClusRandom.nextInt(3, nb_possible);
/* 113 */         ClassTerm mydecr = (ClassTerm)sel_from.elementAt(to_decrease);
/* 114 */         int nbch = mydecr.getNbChildren();
/* 115 */         int to_remove = ClusRandom.nextInt(3, nbch);
/* 116 */         mydecr.removeChild(to_remove);
/* 117 */         mydecr.numberChildren();
/* 118 */         terms.removeAllElements();
/* 119 */         addAll(root, terms);
/*     */       } 
/*     */       
/* 122 */       boolean depth_constraint = false;
/* 123 */       for (int i = 0; i < terms.size(); i++) {
/* 124 */         ClassTerm trm = (ClassTerm)terms.elementAt(i);
/* 125 */         if (trm.getLevel() == MAX_DEPTH) depth_constraint = true; 
/*     */       } 
/* 127 */       stat.reset();
/* 128 */       root.getMeanBranch(null, stat);
/* 129 */       if (depth_constraint) {
/* 130 */         if (Math.abs(stat.getMean() - MEAN_BRANCH) < MAX_ERROR)
/* 131 */         { done = true; }
/*     */         
/* 133 */         else if (stat.getMean() < MEAN_BRANCH) { increase = true; }
/* 134 */         else { increase = false; }
/*     */       
/*     */       } else {
/* 137 */         increase = true;
/*     */       } 
/* 139 */       if (count % 100 == 0) {
/* 140 */         System.out.println("Current: " + stat.getMean() + " " + terms.size());
/*     */       }
/* 142 */       count++;
/*     */     } 
/* 144 */     return root;
/*     */   }
/*     */   
/*     */   public static void addAll(ClassTerm root, MyArray cls) {
/* 148 */     cls.addElement(root);
/* 149 */     for (int i = 0; i < root.getNbChildren(); i++) {
/* 150 */       addAll((ClassTerm)root.getChild(i), cls);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void addClasses(ClassTerm root, MyArray cls) {
/* 155 */     if (root.atBottomLevel()) cls.addElement(root); 
/* 156 */     for (int i = 0; i < root.getNbChildren(); i++) {
/* 157 */       addClasses((ClassTerm)root.getChild(i), cls);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void addAttributes(ClassTerm root, MyArray cls) {
/* 162 */     if (!root.atBottomLevel()) cls.addElement(root); 
/* 163 */     for (int i = 0; i < root.getNbChildren(); i++) {
/* 164 */       addAttributes((ClassTerm)root.getChild(i), cls);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void calcMeanDepth(ClassTerm root, SingleStat stat) {
/* 169 */     stat.addFloat(root.getLevel());
/* 170 */     for (int i = 0; i < root.getNbChildren(); i++) {
/* 171 */       calcMeanDepth((ClassTerm)root.getChild(i), stat);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 177 */     MAX_BRANCH = Integer.parseInt(args[0]);
/* 178 */     MAX_DEPTH = Integer.parseInt(args[1]);
/* 179 */     MAX_CLASS = Integer.parseInt(args[2]);
/* 180 */     int RND = Integer.parseInt(args[3]);
/*     */     
/* 182 */     System.out.println("Branch: " + MAX_BRANCH);
/* 183 */     System.out.println("Depth:  " + MAX_DEPTH);
/* 184 */     System.out.println("Class:  " + MAX_CLASS);
/* 185 */     System.out.println("Random: " + RND);
/*     */     
/* 187 */     ClusRandom.initialize(RND);
/* 188 */     ClassTerm root = createSimpleHierarchy();
/*     */     
/* 190 */     HierIO io = new HierIO();
/* 191 */     System.out.println("Hierarchy:");
/* 192 */     io.writeHierarchy(root, ClusFormat.OUT_WRITER);
/*     */     
/* 194 */     SingleStat hb = new SingleStat();
/* 195 */     root.getMeanBranch(null, hb);
/* 196 */     System.out.println("Mean branching factor: " + hb);
/*     */     
/* 198 */     MyArray cls = new MyArray();
/* 199 */     addClasses(root, cls);
/*     */     
/* 201 */     MyArray attr = new MyArray();
/* 202 */     addAttributes(root, attr);
/*     */     
/* 204 */     MyFile file = new MyFile("artificial.arff");
/* 205 */     file.log("@relation artificial");
/* 206 */     file.log();
/*     */     
/* 208 */     for (int i = 0; i < attr.size(); i++) {
/* 209 */       ClassTerm at = (ClassTerm)attr.elementAt(i);
/* 210 */       for (int k = 0; k < at.getNbChildren(); k++) {
/* 211 */         file.log("@attribute\t" + at + "_" + k + "\t{0,1}");
/*     */       }
/*     */     } 
/* 214 */     file.log("@attribute\ttarget\tclasses");
/*     */     
/* 216 */     file.log();
/* 217 */     file.log("@data");
/*     */     
/* 219 */     ClassHierarchy hier = new ClassHierarchy(root);
/* 220 */     hier.numberHierarchy();
/* 221 */     MyArray target = new MyArray();
/* 222 */     boolean[] include = new boolean[hier.getTotal()];
/*     */     
/* 224 */     int nb_class = 0;
/* 225 */     SingleStat meb = new SingleStat();
/* 226 */     for (int j = 0; j < NB_DATA; j++) {
/* 227 */       StringBuffer buf = new StringBuffer();
/*     */       
/* 229 */       Arrays.fill(include, false);
/* 230 */       target.removeAllElements();
/* 231 */       int nb_classes = ClusRandom.nextInt(3, MAX_CLASS) + 1; int k;
/* 232 */       for (k = 0; k < nb_classes; k++) {
/* 233 */         boolean found = false;
/* 234 */         ClassTerm mcls = null;
/* 235 */         while (!found) {
/* 236 */           int idx = ClusRandom.nextInt(3, cls.size());
/* 237 */           mcls = (ClassTerm)cls.elementAt(idx);
/* 238 */           if (!include[mcls.getIndex()]) found = true;
/*     */         
/*     */         } 
/* 241 */         target.addElement(mcls);
/*     */       } 
/*     */       
/* 244 */       nb_class += nb_classes;
/* 245 */       meb.addMean(hier.getMeanBranch(include));
/*     */       
/* 247 */       for (k = 0; k < attr.size(); k++) {
/* 248 */         ClassTerm at = (ClassTerm)attr.elementAt(k);
/* 249 */         if (include[at.getIndex()]) {
/* 250 */           int nb_match = 0;
/* 251 */           for (int m = 0; m < at.getNbChildren(); m++) {
/* 252 */             ClassTerm child = (ClassTerm)at.getChild(m);
/* 253 */             if (include[child.getIndex()]) {
/* 254 */               nb_match++;
/* 255 */               buf.append('1');
/*     */             } else {
/* 257 */               buf.append('0');
/*     */             } 
/* 259 */             buf.append(',');
/*     */           } 
/*     */         } else {
/* 262 */           for (int m = 0; m < at.getNbChildren(); m++) {
/* 263 */             int value = ClusRandom.nextInt(3, 2);
/* 264 */             buf.append(value);
/* 265 */             buf.append(',');
/*     */           } 
/*     */         } 
/*     */       } 
/* 269 */       for (k = 0; k < target.size(); k++) {
/* 270 */         if (k != 0) buf.append('@'); 
/* 271 */         buf.append(target.elementAt(k));
/*     */       } 
/* 273 */       file.log(buf.toString());
/*     */     } 
/* 275 */     file.close();
/*     */     
/* 277 */     SingleStat md = new SingleStat();
/* 278 */     calcMeanDepth(root, md);
/*     */     
/* 280 */     MyFile info = new MyFile("hier.info");
/* 281 */     info.log("Setting Mean Branch: " + MEAN_BRANCH);
/* 282 */     info.log("Hier Branch: " + hb);
/* 283 */     info.log("Hier Max Depth: " + MAX_DEPTH);
/* 284 */     info.log("Hier Mean Depth: " + md);
/* 285 */     info.log("Hier Nodes: " + attr.size());
/* 286 */     info.log("Hier Leaves: " + cls.size());
/* 287 */     info.log("Example Max Class: " + MAX_CLASS);
/* 288 */     double mean_cls = nb_class / NB_DATA;
/* 289 */     info.log("Example Mean Class: " + mean_cls);
/* 290 */     info.log("Example Branch: " + meb);
/* 291 */     info.log();
/* 292 */     io.writeHierarchy(root, info.getWriter());
/* 293 */     info.close();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\tools\hier\GenerateData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */