/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.util.ClusException;
/*     */ import java.io.Serializable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClassesValue
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  37 */   public static String HIERARCY_SEPARATOR = "/";
/*  38 */   public static String ABUNDANCE_SEPARATOR = ":";
/*  39 */   public static String EMPTY_SET_INDICATOR = "?";
/*     */   
/*     */   public static final int NO_ABUNDANCE = 0;
/*     */   
/*     */   public static final int PATH_ABUNDANCE = 1;
/*     */   public static final int NODE_ABUNDANCE = 2;
/*     */   protected String[] m_Path;
/*     */   protected boolean m_Intermediate;
/*  47 */   protected double m_Abundance = 1.0D;
/*     */   protected ClassTerm m_ClassTerm;
/*     */   
/*     */   public ClassesValue(String constr, StringTable table) throws ClusException {
/*  51 */     StringTokenizer tokens = new StringTokenizer(constr, HIERARCY_SEPARATOR + ABUNDANCE_SEPARATOR);
/*  52 */     int plen = tokens.countTokens();
/*  53 */     this.m_Path = new String[plen];
/*  54 */     if (plen == 0) {
/*  55 */       throw new ClusException("Path length should be >= 1");
/*     */     }
/*  57 */     int idx = 0;
/*  58 */     while (tokens.hasMoreTokens()) {
/*  59 */       String st = table.get(tokens.nextToken());
/*  60 */       if (st.equals("0")) {
/*  61 */         String[] old_path = this.m_Path;
/*  62 */         this.m_Path = new String[idx];
/*  63 */         System.arraycopy(old_path, 0, this.m_Path, 0, this.m_Path.length);
/*  64 */         while (tokens.hasMoreTokens()) {
/*  65 */           st = table.get(tokens.nextToken());
/*  66 */           if (!st.equals("0")) {
/*  67 */             throw new ClusException("Hierarchical class must not contain internal zeros");
/*     */           }
/*     */         } 
/*     */         return;
/*     */       } 
/*  72 */       this.m_Path[idx] = st;
/*     */       
/*  74 */       idx++;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ClassesValue(ClassTerm term) {
/*  80 */     this.m_ClassTerm = term;
/*     */   }
/*     */   
/*     */   public ClassesValue(ClassTerm term, double abundance) {
/*  84 */     this.m_Abundance = abundance;
/*  85 */     this.m_ClassTerm = term;
/*     */   }
/*     */   
/*     */   public ClassesValue(int len) {
/*  89 */     this.m_Path = new String[len];
/*     */   }
/*     */   
/*     */   public boolean isRoot() {
/*  93 */     return (getTerm().getIndex() == -1);
/*     */   }
/*     */   
/*     */   public ClassTerm getTerm() {
/*  97 */     return this.m_ClassTerm;
/*     */   }
/*     */   
/*     */   public void setClassTerm(ClassTerm term) {
/* 101 */     this.m_ClassTerm = term;
/*     */   }
/*     */   
/*     */   public final int getIndex() {
/* 105 */     return this.m_ClassTerm.getIndex();
/*     */   }
/*     */   
/*     */   public ClassesValue toFlat(StringTable table) {
/* 109 */     ClassesValue val = new ClassesValue(1);
/* 110 */     val.setPath(table.get(toPathString()), 0);
/* 111 */     return val;
/*     */   }
/*     */   
/*     */   public double getAbundance() {
/* 115 */     return this.m_Abundance;
/*     */   }
/*     */   
/*     */   public void setAbundance(double abundance) {
/* 119 */     this.m_Abundance = abundance;
/*     */   }
/*     */   
/*     */   public void setIntermediate(boolean inter) {
/* 123 */     this.m_Intermediate = inter;
/*     */   }
/*     */   
/*     */   public boolean isIntermediate() {
/* 127 */     return this.m_Intermediate;
/*     */   }
/*     */   
/*     */   public static void setHSeparator(String hsep) {
/* 131 */     HIERARCY_SEPARATOR = hsep;
/*     */   }
/*     */   
/*     */   public static void setEmptySetIndicator(String empty) {
/* 135 */     EMPTY_SET_INDICATOR = empty;
/*     */   }
/*     */   
/*     */   public boolean equalsValue(ClassesValue other) {
/* 139 */     if (this.m_Path.length != other.m_Path.length) {
/* 140 */       return false;
/*     */     }
/* 142 */     for (int i = 0; i < this.m_Path.length; i++) {
/* 143 */       if (!this.m_Path[i].equals(other.m_Path[i])) {
/* 144 */         return false;
/*     */       }
/*     */     } 
/* 147 */     return true;
/*     */   }
/*     */   
/*     */   public String getMostSpecificClass() {
/* 151 */     return this.m_Path[this.m_Path.length - 1];
/*     */   }
/*     */   
/*     */   public String getClassID(int level) {
/* 155 */     return this.m_Path[level];
/*     */   }
/*     */   
/*     */   public int getNbLevels() {
/* 159 */     return this.m_Path.length;
/*     */   }
/*     */   
/*     */   public void setPath(String strg, int i) {
/* 163 */     this.m_Path[i] = strg;
/*     */   }
/*     */   
/*     */   public final void addHierarchyIndices(ClassHierarchy hier) throws ClusException {
/* 167 */     ClassTerm term = hier.getClassTerm(this);
/* 168 */     setClassTerm(term);
/*     */   }
/*     */   
/*     */   public String toPathString() {
/* 172 */     ClassTerm term = getTerm();
/* 173 */     if (term != null) {
/* 174 */       return term.toPathString();
/*     */     }
/* 176 */     StringBuffer buf = new StringBuffer();
/* 177 */     for (int i = 0; i < getNbLevels(); i++) {
/* 178 */       if (i != 0) {
/* 179 */         buf.append('/');
/*     */       }
/* 181 */       buf.append(getClassID(i));
/*     */     } 
/* 183 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String toStringData(ClassHierarchy hier) {
/* 187 */     if (hier != null && hier.isDAG()) {
/* 188 */       return getTerm().getID();
/*     */     }
/* 190 */     return toPathString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toStringWithDepths(ClassHierarchy hier) {
/* 195 */     if (hier != null && hier.isDAG()) {
/* 196 */       ClassTerm term = getTerm();
/* 197 */       if (term.getMinDepth() == term.getMaxDepth()) {
/* 198 */         return getTerm().getID() + "[" + term.getMinDepth() + "]";
/*     */       }
/* 200 */       return getTerm().getID() + "[" + term.getMinDepth() + ";" + term.getMaxDepth() + "]";
/*     */     } 
/*     */     
/* 203 */     return toPathString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 208 */     return toPathString();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\hierarchical\ClassesValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */