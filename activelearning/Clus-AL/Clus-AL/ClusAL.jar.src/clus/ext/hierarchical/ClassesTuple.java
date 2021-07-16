/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.util.ClusException;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.StringTokenizer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClassesTuple
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   private ClassesValue[] m_Tuple;
/*     */   protected int m_Count;
/*     */   
/*     */   public ClassesTuple() {}
/*     */   
/*     */   public ClassesTuple(String constr, StringTable table) throws ClusException {
/*  44 */     if (constr.equals(ClassesValue.EMPTY_SET_INDICATOR)) {
/*  45 */       this.m_Tuple = new ClassesValue[0];
/*     */     } else {
/*  47 */       int idx = 0;
/*  48 */       StringTokenizer tokens = new StringTokenizer(constr, "@");
/*  49 */       int tlen = tokens.countTokens();
/*  50 */       this.m_Tuple = new ClassesValue[tlen];
/*  51 */       while (tokens.hasMoreTokens()) {
/*  52 */         String a = tokens.nextToken();
/*     */         
/*  54 */         ClassesValue val = new ClassesValue(a, table);
/*  55 */         this.m_Tuple[idx++] = val;
/*     */       } 
/*  57 */       if (tlen == 0) {
/*  58 */         new ClusException("Number of classes should be >= 1");
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public ClassesTuple(int size) {
/*  64 */     this.m_Tuple = new ClassesValue[size];
/*     */   }
/*     */   
/*     */   public boolean isRoot() {
/*  68 */     if ((getM_Tuple()).length == 0) {
/*  69 */       return true;
/*     */     }
/*  71 */     if ((getM_Tuple()).length == 1) {
/*  72 */       return getM_Tuple()[0].isRoot();
/*     */     }
/*  74 */     return false;
/*     */   }
/*     */   
/*     */   public boolean hasClass(int index) {
/*  78 */     for (int i = 0; i < (getM_Tuple()).length; i++) {
/*  79 */       ClassesValue val = getClass(i);
/*  80 */       if (index == val.getIndex()) {
/*  81 */         return true;
/*     */       }
/*     */     } 
/*  84 */     return false;
/*     */   }
/*     */   
/*     */   public ClassesTuple toFlat(StringTable table) {
/*  88 */     ClassesTuple tuple = new ClassesTuple((getM_Tuple()).length);
/*  89 */     for (int i = 0; i < (getM_Tuple()).length; i++) {
/*  90 */       tuple.setItemAt(getClass(i).toFlat(table), i);
/*     */     }
/*  92 */     return tuple;
/*     */   }
/*     */   
/*     */   public void setLength(int size) {
/*  96 */     ClassesValue[] old = getM_Tuple();
/*  97 */     setM_Tuple(new ClassesValue[size]);
/*  98 */     System.arraycopy(old, 0, getM_Tuple(), 0, size);
/*     */   }
/*     */   
/*     */   public boolean equalsTuple(ClassesTuple other) {
/* 102 */     if ((getM_Tuple()).length != (other.getM_Tuple()).length) {
/* 103 */       return false;
/*     */     }
/* 105 */     for (int i = 0; i < (getM_Tuple()).length; i++) {
/* 106 */       if (!this.m_Tuple[i].equalsValue(other.m_Tuple[i])) {
/* 107 */         return false;
/*     */       }
/*     */     } 
/* 110 */     return true;
/*     */   }
/*     */   
/*     */   public final void setSize(int size) {
/* 114 */     setM_Tuple(new ClassesValue[size]);
/*     */   }
/*     */   
/*     */   public final void setItemAt(ClassesValue item, int pos) {
/* 118 */     getM_Tuple()[pos] = item;
/*     */   }
/*     */   
/*     */   public final void addItem(ClassesValue item) {
/* 122 */     getM_Tuple()[this.m_Count++] = item;
/*     */   }
/*     */   
/*     */   public int getPosition(int idx) {
/* 126 */     return getM_Tuple()[idx].getIndex();
/*     */   }
/*     */   
/*     */   public final int getNbClasses() {
/* 130 */     return (getM_Tuple()).length;
/*     */   }
/*     */   
/*     */   public final ClassesValue getClass(int idx) {
/* 134 */     return getM_Tuple()[idx];
/*     */   }
/*     */   
/*     */   public void updateDistribution(double[] distr, double weight) {
/* 138 */     for (int i = 0; i < getNbClasses(); i++) {
/* 139 */       distr[getClass(i).getIndex()] = distr[getClass(i).getIndex()] + weight;
/*     */     }
/*     */   }
/*     */   
/*     */   public final boolean[] getVectorBoolean(ClassHierarchy hier) {
/* 144 */     boolean[] vec = new boolean[hier.getTotal()];
/* 145 */     for (int i = 0; i < getNbClasses(); i++) {
/* 146 */       vec[getClass(i).getIndex()] = true;
/*     */     }
/* 148 */     return vec;
/*     */   }
/*     */   
/*     */   public final double[] getVector(ClassHierarchy hier) {
/* 152 */     double[] vec = new double[hier.getTotal()];
/* 153 */     for (int i = 0; i < getNbClasses(); i++) {
/* 154 */       vec[getClass(i).getIndex()] = 1.0D;
/*     */     }
/* 156 */     return vec;
/*     */   }
/*     */   
/*     */   public final boolean[] getVectorBooleanNodeAndAncestors(ClassHierarchy hier) {
/* 160 */     boolean[] vec = new boolean[hier.getTotal()];
/* 161 */     fillBoolArrayNodeAndAncestors(vec);
/* 162 */     return vec;
/*     */   }
/*     */   
/*     */   public final double[] getVectorNodeAndAncestors(ClassHierarchy hier) {
/* 166 */     double[] vec = new double[hier.getTotal()];
/* 167 */     for (int i = 0; i < getNbClasses(); i++) {
/* 168 */       ClassesValue val = getClass(i);
/* 169 */       val.getTerm().fillVectorNodeAndAncestors(vec);
/*     */     } 
/* 171 */     return vec;
/*     */   }
/*     */   
/*     */   public final void fillBoolArrayNodeAndAncestors(boolean[] interms) {
/* 175 */     for (int i = 0; i < getNbClasses(); i++) {
/* 176 */       ClassesValue val = getClass(i);
/* 177 */       val.getTerm().fillBoolArrayNodeAndAncestors(interms);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void addIntermediateElems(ClassHierarchy hier, boolean[] alllabels, ArrayList<ClassesValue> added) {
/* 182 */     fillBoolArrayNodeAndAncestors(alllabels);
/* 183 */     for (int i = 0; i < hier.getTotal(); i++) {
/* 184 */       if (alllabels[i]) {
/* 185 */         ClassTerm term = hier.getTermAt(i);
/* 186 */         ClassesValue val = new ClassesValue(term);
/* 187 */         val.setIntermediate(term.hasChildrenIn(alllabels));
/* 188 */         added.add(val);
/*     */       } 
/*     */     } 
/* 191 */     setM_Tuple(new ClassesValue[added.size()]);
/* 192 */     System.arraycopy(added.toArray(), 0, getM_Tuple(), 0, added.size());
/*     */   }
/*     */   
/*     */   public final void cloneFrom(ClassesTuple tuple) {
/* 196 */     int size = (tuple.getM_Tuple()).length;
/* 197 */     setM_Tuple(new ClassesValue[size]);
/* 198 */     System.arraycopy(tuple.getM_Tuple(), 0, getM_Tuple(), 0, size);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 202 */     return toString(null, '@', false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toStringHuman(ClassHierarchy hier) {
/* 208 */     return toString(hier, ',', false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toStringData(ClassHierarchy hier) {
/* 214 */     return toString(hier, '@', false);
/*     */   }
/*     */   
/*     */   public String toString(ClassHierarchy hier, char separator, boolean allowinterm) {
/* 218 */     if (getNbClasses() > 0) {
/* 219 */       int idx = 0;
/* 220 */       StringBuffer buf = new StringBuffer();
/* 221 */       for (int i = 0; i < getNbClasses(); i++) {
/* 222 */         ClassesValue val = getClass(i);
/* 223 */         if (allowinterm || !val.isIntermediate()) {
/* 224 */           if (idx != 0) {
/* 225 */             buf.append(separator);
/*     */           }
/* 227 */           buf.append(val.toStringData(hier));
/* 228 */           idx++;
/*     */         } 
/*     */       } 
/* 231 */       return buf.toString();
/*     */     } 
/* 233 */     return "none";
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAllIntermediate(boolean inter) {
/* 238 */     for (int i = 0; i < (getM_Tuple()).length; i++) {
/* 239 */       getM_Tuple()[i].setIntermediate(inter);
/*     */     }
/*     */   }
/*     */   
/*     */   public final void addToHierarchy(ClassHierarchy hier) {
/* 244 */     for (int i = 0; i < getNbClasses(); i++) {
/* 245 */       hier.addClass(getClass(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public final void addHierarchyIndices(ClassHierarchy hier) throws ClusException {
/* 250 */     for (int i = 0; i < getNbClasses(); i++) {
/* 251 */       ClassesValue val = getClass(i);
/* 252 */       ClassTerm term = hier.getClassTerm(val);
/* 253 */       val.setClassTerm(term);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void removeLabels(boolean[] removed) {
/* 258 */     ArrayList<ClassesValue> left = new ArrayList(); int i;
/* 259 */     for (i = 0; i < getNbClasses(); i++) {
/* 260 */       ClassesValue val = getClass(i);
/* 261 */       if (!removed[val.getIndex()]) {
/* 262 */         left.add(val);
/*     */       }
/*     */     } 
/* 265 */     setM_Tuple(new ClassesValue[left.size()]);
/* 266 */     for (i = 0; i < left.size(); i++) {
/* 267 */       getM_Tuple()[i] = left.get(i);
/*     */     }
/*     */   }
/*     */   
/*     */   public static ClassesTuple readFromFile(String fname, ClassHierarchy hier) throws ClusException, IOException {
/* 272 */     int idx = 0;
/* 273 */     BufferedReader input = new BufferedReader(new FileReader(fname));
/* 274 */     StringBuffer classes = new StringBuffer();
/* 275 */     String line = input.readLine();
/* 276 */     while (line != null) {
/* 277 */       line = line.trim();
/* 278 */       if (!line.equals("")) {
/* 279 */         if (idx != 0) {
/* 280 */           classes.append("@");
/*     */         }
/* 282 */         classes.append(line);
/*     */       } 
/* 284 */       line = input.readLine();
/* 285 */       idx++;
/*     */     } 
/* 287 */     input.close();
/* 288 */     ClassesTuple tuple = new ClassesTuple(classes.toString(), hier.getType().getTable());
/* 289 */     tuple.addHierarchyIndices(hier);
/* 290 */     return tuple;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassesValue[] getM_Tuple() {
/* 297 */     return this.m_Tuple;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setM_Tuple(ClassesValue[] m_Tuple) {
/* 304 */     this.m_Tuple = m_Tuple;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\ClassesTuple.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */