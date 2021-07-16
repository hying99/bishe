/*     */ package clus.error;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.text.NumberFormat;
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
/*     */ 
/*     */ public abstract class ClusError
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int DETAIL_NEVER_VISIBLE = 0;
/*     */   public static final int DETAIL_SMALL = 1;
/*     */   public static final int DETAIL_ALWAYS_VISIBLE = 2;
/*     */   public static final int DETAIL_VERY_SMALL = 3;
/*     */   protected static final String DEFAULT_ERROR = "Default error: ";
/*     */   protected static final String TREE_ERROR = "Tree error: ";
/*     */   protected static final String RELATIVE_ERROR = "Relative error: ";
/*     */   protected static final String DEFAULT_POSTFIX = " ";
/*     */   protected static final String TREE_POSTFIX = "    ";
/*     */   protected static final String RELATIVE_POSTFIX = "";
/*     */   protected int m_Dim;
/*     */   protected ClusErrorList m_Parent;
/*     */   
/*     */   public ClusError(ClusErrorList par, int dim) {
/*  59 */     this.m_Dim = dim;
/*  60 */     this.m_Parent = par;
/*     */   }
/*     */   
/*     */   public ClusError(ClusErrorList par) {
/*  64 */     this(par, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean shouldBeLow() {
/*  70 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isMultiLine() {
/*  74 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isComputeForModel(String name) {
/*  78 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWeights(ClusAttributeWeights weights) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getName();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/*  97 */     System.err.println(getClass().getName() + ": reset() not implemented!");
/*     */   }
/*     */   
/*     */   public void normalize(double[] fac) {
/* 101 */     System.err.println(getClass().getName() + ": normalize() not implemented!");
/*     */   }
/*     */   
/*     */   public void add(ClusError other) {
/* 105 */     System.err.println(getClass().getName() + ": add() not implemented!");
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/* 109 */     System.err.println(getClass().getName() + ": addExample() not implemented!");
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple real, DataTuple pred) {
/* 113 */     System.err.println(getClass().getName() + ": addExample() not implemented!");
/*     */   }
/*     */   
/*     */   public void addInvalid(DataTuple tuple) {
/* 117 */     System.err.println(getClass().getName() + ": addInvalid() not implemented!");
/*     */   }
/*     */   
/*     */   public void compute(RowData data, ClusModel model) throws ClusException {
/* 121 */     System.err.println(getClass().getName() + ": compute() not implemented!");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateFromGlobalMeasure(ClusError global) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getModelError() {
/* 137 */     return getModelErrorAdditive() / getNbExamples();
/*     */   }
/*     */ 
/*     */   
/*     */   public double getModelErrorAdditive() {
/* 142 */     System.out.println(getClass().getName() + "::getModelErrorAdditive() not implemented!");
/* 143 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public double getModelErrorStandardError() {
/* 147 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */   
/*     */   public double getModelErrorComponent(int i) {
/* 151 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public abstract ClusError getErrorClone(ClusErrorList paramClusErrorList);
/*     */   
/*     */   public ClusError getErrorClone() {
/* 157 */     return getErrorClone(getParent());
/*     */   }
/*     */   
/*     */   public int getDetailLevel() {
/* 161 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public double get_error_classif() {
/* 166 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public double get_accuracy() {
/* 171 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public double get_precision() {
/* 176 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public double get_recall() {
/* 181 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public double get_auc() {
/* 186 */     return 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void showModelError(PrintWriter out, String bName, int detail) throws IOException {
/* 197 */     showModelError(out, detail);
/*     */   }
/*     */   
/*     */   public void showModelError(PrintWriter out, int detail) {
/* 201 */     for (int i = 0; i < this.m_Dim; i++) {
/* 202 */       if (i != 0) {
/* 203 */         out.print(", ");
/*     */       }
/* 205 */       out.print(getModelErrorComponent(i));
/*     */     } 
/* 207 */     out.println();
/*     */   }
/*     */   
/*     */   public void showRelativeModelError(PrintWriter out, int detail) {
/* 211 */     showModelError(out, detail);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusErrorList getParent() {
/* 220 */     return this.m_Parent;
/*     */   }
/*     */   
/*     */   public String getPrefix() {
/* 224 */     return this.m_Parent.getPrefix();
/*     */   }
/*     */   
/*     */   public int getNbExamples() {
/* 228 */     return this.m_Parent.getNbExamples();
/*     */   }
/*     */   
/*     */   public int getNbTotal() {
/* 232 */     return this.m_Parent.getNbTotal();
/*     */   }
/*     */   
/*     */   public int getNbCover() {
/* 236 */     return this.m_Parent.getNbCover();
/*     */   }
/*     */   
/*     */   public double getCoverage() {
/* 240 */     int nb = getNbTotal();
/* 241 */     return (nb == 0) ? 0.0D : (getNbCover() / nb);
/*     */   }
/*     */   
/*     */   public NumberFormat getFormat() {
/* 245 */     return this.m_Parent.getFormat();
/*     */   }
/*     */   
/*     */   public int getDimension() {
/* 249 */     return this.m_Dim;
/*     */   }
/*     */   
/*     */   public String showDoubleArray(double[] arr) {
/* 253 */     NumberFormat fr = getFormat();
/* 254 */     StringBuffer buf = new StringBuffer();
/* 255 */     buf.append("[");
/* 256 */     for (int i = 0; i < arr.length; i++) {
/* 257 */       if (i != 0) {
/* 258 */         buf.append(",");
/*     */       }
/* 260 */       buf.append(fr.format(arr[i]));
/*     */     } 
/* 262 */     buf.append("]");
/* 263 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String showDoubleArray(double[] arr1, double[] arr2) {
/* 267 */     NumberFormat fr = getFormat();
/* 268 */     StringBuffer buf = new StringBuffer();
/* 269 */     buf.append("[");
/* 270 */     for (int i = 0; i < arr1.length; i++) {
/* 271 */       double el = (arr2[i] != 0.0D) ? (arr1[i] / arr2[i]) : 0.0D;
/* 272 */       if (i != 0) {
/* 273 */         buf.append(",");
/*     */       }
/* 275 */       buf.append(fr.format(el));
/*     */     } 
/* 277 */     buf.append("]");
/* 278 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String showDoubleArray(double[] arr1, double div) {
/* 282 */     NumberFormat fr = getFormat();
/* 283 */     StringBuffer buf = new StringBuffer();
/* 284 */     buf.append("[");
/* 285 */     for (int i = 0; i < arr1.length; i++) {
/* 286 */       double el = (div != 0.0D) ? (arr1[i] / div) : 0.0D;
/* 287 */       if (i != 0) {
/* 288 */         buf.append(",");
/*     */       }
/* 290 */       buf.append(fr.format(el));
/*     */     } 
/* 292 */     buf.append("]");
/* 293 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public double computeLeafError(ClusStatistic stat) {
/* 297 */     System.out.println(getClass().getName() + "::computeLeafError() not yet implemented");
/* 298 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public double computeTreeErrorClusteringAbsolute(ClusNode tree) {
/* 302 */     if (tree.atBottomLevel()) {
/* 303 */       return computeLeafError(tree.getClusteringStat());
/*     */     }
/* 305 */     double result = 0.0D;
/* 306 */     for (int i = 0; i < tree.getNbChildren(); i++) {
/* 307 */       ClusNode child = (ClusNode)tree.getChild(i);
/* 308 */       result += computeTreeErrorClusteringAbsolute(child);
/*     */     } 
/* 310 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\ClusError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */