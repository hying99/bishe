/*     */ package clus.data.rows;
/*     */ 
/*     */ import clus.activelearning.data.OracleAnswer;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.util.ClusException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataTuple
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected ClusSchema m_Schema;
/*     */   public int[] m_Ints;
/*     */   public double[] m_Doubles;
/*     */   public Object[] m_Objects;
/*     */   public double m_Weight;
/*     */   public int m_Index;
/*     */   public int[] m_Folds;
/*     */   private int m_TimesQueried;
/*     */   protected OracleAnswer m_OracleAnswer;
/*     */   public int m_ActiveIndex;
/*  58 */   private LinkedList<DataTuple> m_SortedNeighbours = new LinkedList<>();
/*     */ 
/*     */   
/*     */   protected DataTuple() {}
/*     */ 
/*     */   
/*     */   public DataTuple(ClusSchema schema) {
/*  65 */     int nb_int = schema.getNbInts();
/*  66 */     if (nb_int > 0) {
/*  67 */       this.m_Ints = new int[nb_int];
/*     */     }
/*  69 */     int nb_double = schema.getNbDoubles();
/*  70 */     if (nb_double > 0) {
/*  71 */       this.m_Doubles = new double[nb_double];
/*     */     }
/*  73 */     int nb_obj = schema.getNbObjects();
/*  74 */     if (nb_obj > 0) {
/*  75 */       this.m_Objects = new Object[nb_obj];
/*     */     }
/*     */     
/*  78 */     this.m_Weight = 1.0D;
/*  79 */     this.m_Schema = schema;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ClusSchema getSchema() {
/*  84 */     return this.m_Schema;
/*     */   }
/*     */   
/*     */   public void setActiveIndex(int index) {
/*  88 */     this.m_ActiveIndex = index;
/*     */   }
/*     */   
/*     */   public DataTuple cloneTuple() {
/*  92 */     DataTuple res = new DataTuple();
/*  93 */     cloneTuple(res);
/*  94 */     return res;
/*     */   }
/*     */   
/*     */   public void cloneTuple(DataTuple res) {
/*  98 */     res.m_Ints = this.m_Ints;
/*  99 */     res.m_Doubles = this.m_Doubles;
/* 100 */     res.m_Objects = this.m_Objects;
/* 101 */     res.m_Weight = this.m_Weight;
/* 102 */     res.m_Index = this.m_Index;
/* 103 */     res.m_Folds = this.m_Folds;
/* 104 */     res.m_Schema = this.m_Schema;
/*     */   }
/*     */   
/*     */   public void addAllPossibleLabelsFromOracle(ClassHierarchy h, StringTable table) throws ClusException {
/* 108 */     for (String label : getOracleAnswer().getPossibleLabels()) {
/* 109 */       addLabel(label, table, h);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addLabel(String newLabel, StringTable table, ClassHierarchy h) throws ClusException {
/*     */     ClassesTuple ct;
/* 115 */     if (this.m_Objects[0] == null) {
/* 116 */       ct = new ClassesTuple(newLabel, table);
/* 117 */       this.m_Objects = (Object[])new ClassesTuple[1];
/* 118 */       this.m_Objects[0] = ct;
/*     */     } else {
/* 120 */       ct = (ClassesTuple)this.m_Objects[0];
/* 121 */       for (int i = 0; i < (ct.getM_Tuple()).length; i++) {
/* 122 */         newLabel = newLabel + "@" + ct.getM_Tuple()[i].toPathString();
/*     */       }
/* 124 */       ct = new ClassesTuple(newLabel, table);
/* 125 */       this.m_Objects = (Object[])new ClassesTuple[1];
/* 126 */       this.m_Objects[0] = ct;
/*     */     } 
/* 128 */     ct.addHierarchyIndices(h);
/*     */     
/* 130 */     ct.addIntermediateElems(h, ct.getVectorBooleanNodeAndAncestors(h), new ArrayList());
/*     */   }
/*     */ 
/*     */   
/*     */   public void addNeutralLabel() {
/* 135 */     if (this.m_Objects[0] == null) {
/* 136 */       ClassesTuple clss = new ClassesTuple(0);
/* 137 */       setObjectVal(clss, 0);
/*     */     } 
/*     */   }
/*     */   
/*     */   public double euclDistance(DataTuple other) {
/* 142 */     double result = 0.0D;
/* 143 */     for (int i = 0; i < this.m_Doubles.length; i++) {
/* 144 */       double t = getDoubleVal(i) - other.getDoubleVal(i);
/* 145 */       t *= t;
/* 146 */       result += t;
/*     */     } 
/* 148 */     return Math.sqrt(result);
/*     */   }
/*     */   
/*     */   public DataTuple deepCloneTuple() {
/* 152 */     DataTuple res = new DataTuple();
/* 153 */     if (this.m_Ints != null) {
/* 154 */       res.m_Ints = new int[this.m_Ints.length];
/* 155 */       System.arraycopy(this.m_Ints, 0, res.m_Ints, 0, this.m_Ints.length);
/*     */     } 
/* 157 */     if (this.m_Doubles != null) {
/* 158 */       res.m_Doubles = new double[this.m_Doubles.length];
/* 159 */       System.arraycopy(this.m_Doubles, 0, res.m_Doubles, 0, this.m_Doubles.length);
/*     */     } 
/* 161 */     if (this.m_Objects != null) {
/* 162 */       res.m_Objects = new Object[this.m_Objects.length];
/* 163 */       System.arraycopy(this.m_Objects, 0, res.m_Objects, 0, this.m_Objects.length);
/*     */     } 
/* 165 */     res.m_Weight = this.m_Weight;
/* 166 */     res.m_Index = this.m_Index;
/* 167 */     res.m_Folds = this.m_Folds;
/* 168 */     res.m_Schema = this.m_Schema;
/* 169 */     return res;
/*     */   }
/*     */   
/*     */   public DataTuple deepActiveCloneTuple() {
/* 173 */     DataTuple res = new DataTuple();
/* 174 */     if (this.m_Ints != null) {
/* 175 */       res.m_Ints = new int[this.m_Ints.length];
/* 176 */       System.arraycopy(this.m_Ints, 0, res.m_Ints, 0, this.m_Ints.length);
/*     */     } 
/* 178 */     if (this.m_Doubles != null) {
/* 179 */       res.m_Doubles = new double[this.m_Doubles.length];
/* 180 */       System.arraycopy(this.m_Doubles, 0, res.m_Doubles, 0, this.m_Doubles.length);
/*     */     } 
/* 182 */     if (this.m_Objects != null) {
/* 183 */       res.m_Objects = new Object[this.m_Objects.length];
/* 184 */       System.arraycopy(this.m_Objects, 0, res.m_Objects, 0, this.m_Objects.length);
/*     */     } 
/* 186 */     res.m_Weight = this.m_Weight;
/* 187 */     res.m_Index = this.m_Index;
/* 188 */     res.m_Folds = this.m_Folds;
/* 189 */     res.m_Schema = this.m_Schema;
/* 190 */     res.m_OracleAnswer = this.m_OracleAnswer;
/* 191 */     res.m_ActiveIndex = this.m_ActiveIndex;
/* 192 */     return res;
/*     */   }
/*     */   
/*     */   public final DataTuple changeWeight(double weight) {
/* 196 */     DataTuple res = cloneTuple();
/* 197 */     res.m_Weight = weight;
/* 198 */     return res;
/*     */   }
/*     */   
/*     */   public final DataTuple multiplyWeight(double weight) {
/* 202 */     DataTuple res = cloneTuple();
/* 203 */     this.m_Weight *= weight;
/*     */ 
/*     */ 
/*     */     
/* 207 */     return res;
/*     */   }
/*     */   
/*     */   public final int getClassification() {
/* 211 */     return -1;
/*     */   }
/*     */   
/*     */   public final boolean hasNumMissing(int idx) {
/* 215 */     return (this.m_Doubles[idx] == Double.POSITIVE_INFINITY);
/*     */   }
/*     */   
/*     */   public final double getDoubleVal(int idx) {
/* 219 */     return this.m_Doubles[idx];
/*     */   }
/*     */   
/*     */   public final int getIntVal(int idx) {
/* 223 */     return this.m_Ints[idx];
/*     */   }
/*     */   
/*     */   public final Object getObjVal(int idx) {
/* 227 */     return this.m_Objects[idx];
/*     */   }
/*     */   
/*     */   public final void setIntVal(int value, int idx) {
/* 231 */     this.m_Ints[idx] = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void setDoubleVal(double value, int idx) {
/* 236 */     this.m_Doubles[idx] = value;
/*     */   }
/*     */   
/*     */   public final void setObjectVal(Object value, int idx) {
/* 240 */     this.m_Objects[idx] = value;
/*     */   }
/*     */   
/*     */   public final void setIndex(int idx) {
/* 244 */     this.m_Index = idx;
/*     */   }
/*     */   
/*     */   public final int getIndex() {
/* 248 */     return this.m_Index;
/*     */   }
/*     */   
/*     */   public final double getWeight() {
/* 252 */     return this.m_Weight;
/*     */   }
/*     */   
/*     */   public final void setWeight(double weight) {
/* 256 */     this.m_Weight = weight;
/*     */   }
/*     */   
/*     */   public final void setSchema(ClusSchema schema) {
/* 260 */     this.m_Schema = schema;
/*     */   }
/*     */   
/*     */   public void writeTuple(PrintWriter wrt) {
/* 264 */     int aidx = 0;
/* 265 */     ClusSchema schema = getSchema();
/* 266 */     for (int i = 0; i < schema.getNbAttributes(); i++) {
/* 267 */       ClusAttrType type = schema.getAttrType(i);
/* 268 */       if (!type.isDisabled()) {
/* 269 */         if (aidx != 0) {
/* 270 */           wrt.print(",");
/*     */         }
/* 272 */         wrt.print(type.getString(this));
/* 273 */         aidx++;
/*     */       } 
/*     */     } 
/* 276 */     wrt.println();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 280 */     int aidx = 0;
/* 281 */     StringBuffer buf = new StringBuffer();
/* 282 */     ClusSchema schema = getSchema();
/* 283 */     if (schema != null) {
/* 284 */       for (int i = 0; i < schema.getNbAttributes(); i++) {
/* 285 */         ClusAttrType type = schema.getAttrType(i);
/* 286 */         if (!type.isDisabled()) {
/* 287 */           if (aidx != 0) {
/* 288 */             buf.append(",");
/*     */           }
/* 290 */           buf.append(type.getString(this));
/* 291 */           aidx++;
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 296 */       for (int i = 0; i < this.m_Objects.length; i++) {
/* 297 */         if (i != 0) {
/* 298 */           buf.append(",");
/*     */         }
/* 300 */         buf.append(this.m_Objects[i].toString());
/*     */       } 
/*     */     } 
/* 303 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public void increaseQueryCounter() {
/* 307 */     setTimesQueried(getTimesQueried() + 1);
/*     */   }
/*     */   
/*     */   public boolean hasBeenQueriedBefore() {
/* 311 */     return (getTimesQueried() > 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTimesQueried() {
/* 318 */     return this.m_TimesQueried;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTimesQueried(int timesQueried) {
/* 325 */     this.m_TimesQueried = timesQueried;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OracleAnswer getOracleAnswer() {
/* 332 */     return this.m_OracleAnswer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOracleAnswer(OracleAnswer m_OracleAnswer) {
/* 339 */     this.m_OracleAnswer = m_OracleAnswer;
/*     */   }
/*     */   
/*     */   public ClassesTuple getObjectVal(int i) {
/* 343 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LinkedList<DataTuple> getSortedNeighbours() {
/* 350 */     return this.m_SortedNeighbours;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSortedNeighbours(LinkedList<DataTuple> m_SortedNeighbours) {
/* 357 */     this.m_SortedNeighbours = m_SortedNeighbours;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\rows\DataTuple.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */