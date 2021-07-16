/*     */ package clus.data.type;
/*     */ 
/*     */ import clus.algo.kNN.BasicDistance;
/*     */ import clus.data.cols.ColTarget;
/*     */ import clus.data.cols.attribute.ClusAttribute;
/*     */ import clus.data.rows.DataPreprocs;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.io.ClusSerializable;
/*     */ import clus.main.Settings;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
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
/*     */ public abstract class ClusAttrType
/*     */   implements Serializable, Comparable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int STATUS_DISABLED = 0;
/*     */   public static final int STATUS_TARGET = 1;
/*     */   public static final int STATUS_CLUSTER_NO_TARGET = 2;
/*     */   public static final int STATUS_NORMAL = 3;
/*     */   public static final int STATUS_KEY = 4;
/*     */   public static final int NB_STATUS = 5;
/*     */   public static final int ATTR_USE_ALL = 0;
/*     */   public static final int ATTR_USE_DESCRIPTIVE = 1;
/*     */   public static final int ATTR_USE_CLUSTERING = 2;
/*     */   public static final int ATTR_USE_TARGET = 3;
/*     */   public static final int ATTR_USE_KEY = 4;
/*     */   public static final int NB_ATTR_USE = 5;
/*     */   public static final int VALUE_TYPE_NONE = -1;
/*     */   public static final int VALUE_TYPE_INT = 0;
/*     */   public static final int VALUE_TYPE_DOUBLE = 1;
/*     */   public static final int VALUE_TYPE_OBJECT = 2;
/*     */   public static final int VALUE_TYPE_BITWISEINT = 3;
/*     */   public static final int NB_VALUE_TYPES = 4;
/*     */   public static final int NB_TYPES = 5;
/*     */   public static final int THIS_TYPE = -1;
/*     */   protected String m_Name;
/*     */   protected int m_Index;
/*     */   protected int m_ArrayIndex;
/*     */   protected int m_NbMissing;
/*     */   protected ClusSchema m_Schema;
/*  71 */   protected int m_Status = 3;
/*     */   
/*     */   protected boolean m_IsDescriptive;
/*     */   
/*     */   public ClusAttrType(String name) {
/*  76 */     this.m_Name = name;
/*  77 */     this.m_Index = -1;
/*  78 */     this.m_ArrayIndex = -1;
/*     */   }
/*     */   protected boolean m_IsClustering; private BasicDistance $dist;
/*     */   public void setSchema(ClusSchema schema) {
/*  82 */     this.m_Schema = schema;
/*     */   }
/*     */   
/*     */   public ClusSchema getSchema() {
/*  86 */     return this.m_Schema;
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/*  90 */     return this.m_Schema.getSettings();
/*     */   }
/*     */   
/*     */   public abstract ClusAttrType cloneType();
/*     */   
/*     */   public void cloneType(ClusAttrType type) {
/*  96 */     type.m_NbMissing = this.m_NbMissing;
/*  97 */     type.m_Status = this.m_Status;
/*  98 */     type.m_IsDescriptive = this.m_IsDescriptive;
/*  99 */     type.m_IsClustering = this.m_IsClustering;
/*     */   }
/*     */   
/*     */   public void copyArrayIndex(ClusAttrType type) {
/* 103 */     this.m_ArrayIndex = type.m_ArrayIndex;
/*     */   }
/*     */   
/*     */   public abstract int getTypeIndex();
/*     */   
/*     */   public abstract int getValueType();
/*     */   
/*     */   public abstract String getTypeName();
/*     */   
/*     */   public int intHasMissing() {
/* 113 */     return (this.m_NbMissing > 0) ? 1 : 0;
/*     */   }
/*     */   
/*     */   public boolean hasMissing() {
/* 117 */     return (this.m_NbMissing > 0);
/*     */   }
/*     */   
/*     */   public int getNbMissing() {
/* 121 */     return this.m_NbMissing;
/*     */   }
/*     */   
/*     */   public void incNbMissing() {
/* 125 */     this.m_NbMissing++;
/*     */   }
/*     */   
/*     */   public void setNbMissing(int nb) {
/* 129 */     this.m_NbMissing = nb;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 133 */     return this.m_Name;
/*     */   }
/*     */   
/*     */   public void setName(String name) {
/* 137 */     this.m_Name = name;
/*     */   }
/*     */   
/*     */   public int getIndex() {
/* 141 */     return this.m_Index;
/*     */   }
/*     */   
/*     */   public void setIndex(int idx) {
/* 145 */     this.m_Index = idx;
/*     */   }
/*     */   
/*     */   public final int getArrayIndex() {
/* 149 */     return this.m_ArrayIndex;
/*     */   }
/*     */   
/*     */   public void setArrayIndex(int idx) {
/* 153 */     this.m_ArrayIndex = idx;
/*     */   }
/*     */   
/*     */   public int getStatus() {
/* 157 */     return this.m_Status;
/*     */   }
/*     */   
/*     */   public void setStatus(int status) {
/* 161 */     this.m_Status = status;
/*     */   }
/*     */   
/*     */   public boolean isTarget() {
/* 165 */     return (this.m_Status == 1);
/*     */   }
/*     */   
/*     */   public boolean isDisabled() {
/* 169 */     return (this.m_Status == 0);
/*     */   }
/*     */   
/*     */   public boolean isKey() {
/* 173 */     return (this.m_Status == 4);
/*     */   }
/*     */   
/*     */   public boolean isClustering() {
/* 177 */     return this.m_IsClustering;
/*     */   }
/*     */   
/*     */   public void setClustering(boolean clust) {
/* 181 */     this.m_IsClustering = clust;
/*     */   }
/*     */   
/*     */   public void setDescriptive(boolean descr) {
/* 185 */     this.m_IsDescriptive = descr;
/*     */   }
/*     */   
/*     */   public boolean isDescriptive() {
/* 189 */     return this.m_IsDescriptive;
/*     */   }
/*     */   
/*     */   public int getMaxNbStats() {
/* 193 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setReader(boolean start_stop) {}
/*     */   
/*     */   public ClusAttrType getType() {
/* 200 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNbRows(int nbrows) {}
/*     */   
/*     */   public int getNominal(DataTuple tuple) {
/* 207 */     return -1;
/*     */   }
/*     */   
/*     */   public double getNumeric(DataTuple tuple) {
/* 211 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */   
/*     */   public boolean isMissing(DataTuple tuple) {
/* 215 */     return true;
/*     */   }
/*     */   
/*     */   public void updatePredictWriterSchema(ClusSchema schema) {
/* 219 */     schema.addAttrType(cloneType());
/*     */   }
/*     */   
/*     */   public String getPredictionWriterString(DataTuple tuple) {
/* 223 */     return getString(tuple);
/*     */   }
/*     */   
/*     */   public String getString(DataTuple tuple) {
/* 227 */     return "err";
/*     */   }
/*     */   
/*     */   public int compareValue(DataTuple t1, DataTuple t2) {
/* 231 */     return -5;
/*     */   }
/*     */ 
/*     */   
/*     */   public void getPreprocs(DataPreprocs pps, boolean single) {}
/*     */   
/*     */   public ClusSerializable createRowSerializable() throws ClusException {
/* 238 */     throw new ClusException("Attribute " + getName() + " does not support row data");
/*     */   }
/*     */   
/*     */   public ClusAttribute createTargetAttr(ColTarget target) throws ClusException {
/* 242 */     throw new ClusException("Attribute " + getName() + " can not be target: incompatible type");
/*     */   }
/*     */   
/*     */   public String toString() {
/* 246 */     return getName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void initializeBeforeLoadingData() throws IOException, ClusException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void initializeFrom(ClusAttrType other_type) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeARFFType(PrintWriter wrt) throws ClusException {
/* 260 */     throw new ClusException("Type: " + getClass().getName() + " can't be written to a .arff file");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getBasicDistance(DataTuple t1, DataTuple t2) {
/* 271 */     return this.$dist.getDistance(this, t1, t2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBasicDistance(BasicDistance dist) {
/* 278 */     this.$dist = dist;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(Object o) {
/* 286 */     ClusAttrType c = (ClusAttrType)o;
/*     */     
/* 288 */     if (c.m_Index > this.m_Index)
/* 289 */       return 1; 
/* 290 */     if (c.m_Index < this.m_Index)
/* 291 */       return -1; 
/* 292 */     return 0;
/*     */   }
/*     */   
/*     */   public boolean isSparse() {
/* 296 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\type\ClusAttrType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */