/*     */ package clus.model.test;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.main.Settings;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
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
/*     */ public abstract class NodeTest
/*     */   implements Serializable
/*     */ {
/*     */   public static final int UNKNOWN = -1;
/*     */   public static final int N_EQ = 0;
/*     */   public static final int S_EQ = 1;
/*     */   public static final int H_EQ = 2;
/*     */   public boolean m_UnknownBranch;
/*     */   public double m_UnknownFreq;
/*     */   public double[] m_BranchFreq;
/*     */   public double m_HeuristicValue;
/*     */   
/*     */   public final boolean hasUnknownBranch() {
/*  48 */     return this.m_UnknownBranch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int predictWeighted(DataTuple paramDataTuple);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int nominalPredict(int value) {
/*  65 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int numericPredict(double value) {
/*  70 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int nominalPredictWeighted(int value) {
/*  75 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int numericPredictWeighted(double value) {
/*  80 */     return -1;
/*     */   }
/*     */   
/*     */   public boolean isUnknown(DataTuple tuple) {
/*  84 */     return getType().isMissing(tuple);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final double getProportion(int branch) {
/*  93 */     return this.m_BranchFreq[branch];
/*     */   }
/*     */   
/*     */   public final void setProportion(int branch, double prop) {
/*  97 */     this.m_BranchFreq[branch] = prop;
/*     */   }
/*     */   
/*     */   public final void setProportion(double[] prop) {
/* 101 */     this.m_BranchFreq = prop;
/*     */   }
/*     */   
/*     */   public final void setPosFreq(double prop) {
/* 105 */     this.m_BranchFreq[0] = prop;
/* 106 */     this.m_BranchFreq[1] = 1.0D - prop;
/*     */   }
/*     */   
/*     */   public final double getPosFreq() {
/* 110 */     return this.m_BranchFreq[0];
/*     */   }
/*     */   
/*     */   public final double getUnknownFreq() {
/* 114 */     return this.m_UnknownFreq;
/*     */   }
/*     */   
/*     */   public final void setUnknownFreq(double unk) {
/* 118 */     this.m_UnknownFreq = unk;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isBinary() {
/* 127 */     return (this.m_BranchFreq.length == 2);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getNbChildren() {
/* 132 */     return this.m_BranchFreq.length;
/*     */   }
/*     */   
/*     */   public final void setArity(int arity) {
/* 136 */     this.m_BranchFreq = new double[arity];
/*     */   }
/*     */ 
/*     */   
/*     */   public final int updateArity() {
/* 141 */     return getNbChildren();
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
/*     */ 
/*     */   
/*     */   public void preprocess(int mode) {}
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
/*     */   public boolean isSoft() {
/* 168 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int softEquals(NodeTest test) {
/* 173 */     return equals(test) ? 2 : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasConstants() {
/* 179 */     return true;
/*     */   }
/*     */   
/*     */   public NodeTest getBranchTest(int i) {
/* 183 */     return null;
/*     */   }
/*     */   
/*     */   public NodeTest simplifyConjunction(NodeTest other) {
/* 187 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean equals(NodeTest paramNodeTest);
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 198 */     return 1111;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ClusAttrType getType();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void setType(ClusAttrType paramClusAttrType);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getString();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBranchString(int i) {
/* 220 */     return null;
/*     */   }
/*     */   
/*     */   public String getBranchLabel(int i) {
/* 224 */     return getBranchString(i);
/*     */   }
/*     */   
/*     */   public boolean hasBranchLabels() {
/* 228 */     return false;
/*     */   }
/*     */   
/*     */   public void setHeuristicValue(double value) {
/* 232 */     this.m_HeuristicValue = value;
/*     */   }
/*     */   
/*     */   public double getHeuristicValue() {
/* 236 */     return this.m_HeuristicValue;
/*     */   }
/*     */   
/*     */   public final void attachModel(HashMap table) throws ClusException {
/* 240 */     ClusAttrType type = getType();
/* 241 */     ClusAttrType ntype = (ClusAttrType)table.get(type.getName());
/* 242 */     if (ntype == null) throw new ClusException("Attribute " + type.getName() + " not in dataset"); 
/* 243 */     ClusAttrType ctype = type.cloneType();
/*     */     
/* 245 */     ctype.copyArrayIndex(ntype);
/* 246 */     setType(ctype);
/*     */   }
/*     */   
/*     */   public int getNbLines() {
/* 250 */     return 1;
/*     */   }
/*     */   
/*     */   public String getLine(int i) {
/* 254 */     return getString();
/*     */   }
/*     */   
/*     */   public final String getTestString() {
/* 258 */     String str = getString();
/* 259 */     if (Settings.SHOW_BRANCH_FREQ && 
/* 260 */       getPosFreq() != Double.NEGATIVE_INFINITY) {
/* 261 */       String bfr = ClusFormat.ONE_AFTER_DOT.format(getPosFreq() * 100.0D);
/* 262 */       str = str + " (" + bfr + "%)";
/*     */     } 
/*     */     
/* 265 */     if (Settings.SHOW_UNKNOWN_FREQ) {
/* 266 */       String unk = ClusFormat.ONE_AFTER_DOT.format(getUnknownFreq() * 100.0D);
/* 267 */       str = str + " (miss: " + unk + "%)";
/*     */     } 
/* 269 */     return str;
/*     */   }
/*     */   
/*     */   public final String getTestString(int idx) {
/* 273 */     String str = getBranchString(idx);
/* 274 */     if (Settings.SHOW_BRANCH_FREQ) {
/* 275 */       String bfr = ClusFormat.ONE_AFTER_DOT.format(getProportion(idx) * 100.0D);
/* 276 */       str = str + " (" + bfr + "%)";
/*     */     } 
/* 278 */     if (idx == 0 && Settings.SHOW_UNKNOWN_FREQ) {
/* 279 */       String unk = ClusFormat.ONE_AFTER_DOT.format(getUnknownFreq() * 100.0D);
/* 280 */       str = str + " (miss:" + unk + "%)";
/*     */     } 
/* 282 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 287 */     return getString();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\test\NodeTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */