/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.rows.DataPreprocs;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.StringAttrType;
/*     */ import clus.io.ClusSerializable;
/*     */ import clus.main.Settings;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
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
/*     */ public class ClassesAttrType
/*     */   extends ClusAttrType
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int THIS_TYPE = 2;
/*     */   public static final String THIS_TYPE_NAME = "Classes";
/*     */   protected transient String[] m_Labels;
/*  45 */   private transient StringTable m_Table = new StringTable();
/*     */   protected ClassHierarchy m_Hier;
/*     */   
/*     */   public ClassesAttrType(String name) {
/*  49 */     super(name);
/*  50 */     this.m_Hier = new ClassHierarchy(this);
/*     */   }
/*     */   
/*     */   public ClassesAttrType(String name, ClassHierarchy hier) {
/*  54 */     super(name);
/*  55 */     this.m_Hier = hier;
/*     */   }
/*     */   
/*     */   public ClassesAttrType(String name, String atype) {
/*  59 */     super(name);
/*  60 */     String classes = atype.substring("HIERARCHICAL".length()).trim();
/*  61 */     this.m_Labels = classes.split("\\s*\\,\\s*");
/*  62 */     this.m_Hier = new ClassHierarchy(this);
/*     */   }
/*     */   
/*     */   public StringTable getTable() {
/*  66 */     return getM_Table();
/*     */   }
/*     */   
/*     */   public ClassHierarchy getHier() {
/*  70 */     return this.m_Hier;
/*     */   }
/*     */   
/*     */   public ClusAttrType cloneType() {
/*  74 */     ClassesAttrType at = new ClassesAttrType(this.m_Name, this.m_Hier);
/*  75 */     cloneType(at);
/*  76 */     return at;
/*     */   }
/*     */   
/*     */   public int getTypeIndex() {
/*  80 */     return 2;
/*     */   }
/*     */   
/*     */   public String getTypeName() {
/*  84 */     return "Classes";
/*     */   }
/*     */   
/*     */   public int getValueType() {
/*  88 */     return 2;
/*     */   }
/*     */   
/*     */   public ClassesTuple getValue(DataTuple t1) {
/*  92 */     return (ClassesTuple)t1.getObjVal(getArrayIndex());
/*     */   }
/*     */   
/*     */   public void updatePredictWriterSchema(ClusSchema schema) {
/*  96 */     String name = getName();
/*  97 */     schema.addAttrType((ClusAttrType)new StringAttrType(name + "-a"));
/*  98 */     ClassHierarchy hier = getHier();
/*  99 */     String[] vals = { "1", "0" };
/* 100 */     for (int i = 0; i < hier.getTotal(); i++) {
/* 101 */       ClassTerm term = hier.getTermAt(i);
/* 102 */       schema.addAttrType((ClusAttrType)new NominalAttrType(name + "-a-" + term.toStringHuman(hier), vals));
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getPredictionWriterString(DataTuple tuple) {
/* 107 */     StringBuffer buf = new StringBuffer();
/* 108 */     buf.append(getString(tuple));
/* 109 */     buf.append(",");
/* 110 */     buf.append(getVectorString(tuple));
/* 111 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String getString(DataTuple tuple) {
/* 115 */     ClassesTuple ct = (ClassesTuple)tuple.m_Objects[this.m_ArrayIndex];
/* 116 */     return ct.toStringData(this.m_Hier);
/*     */   }
/*     */   
/*     */   public String getVectorString(DataTuple tuple) {
/* 120 */     ClassesTuple ct = (ClassesTuple)tuple.m_Objects[this.m_ArrayIndex];
/* 121 */     boolean[] vec = ct.getVectorBooleanNodeAndAncestors(this.m_Hier);
/* 122 */     StringBuffer buf = new StringBuffer();
/* 123 */     for (int i = 0; i < vec.length; i++) {
/* 124 */       if (i != 0) buf.append(","); 
/* 125 */       buf.append(vec[i] ? "1" : "0");
/*     */     } 
/* 127 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public ClusSerializable createRowSerializable() throws ClusException {
/* 131 */     return new MySerializable();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void getPreprocs(DataPreprocs pps, boolean single) {
/* 137 */     pps.addPreproc(new ClassHierarchyPreproc(this, true));
/*     */   }
/*     */   
/*     */   public void initializeBeforeLoadingData() throws IOException, ClusException {
/* 141 */     if (isDisabled()) {
/*     */       return;
/*     */     }
/*     */     
/* 145 */     if (getSettings().hasDefinitionFile()) {
/*     */       
/* 147 */       this.m_Hier.loadDAG(getSettings().getDefinitionFile());
/* 148 */       this.m_Hier.initialize();
/*     */     } 
/* 150 */     if (this.m_Labels != null) {
/*     */       
/* 152 */       if (getSettings().getHierType() == 1) {
/* 153 */         this.m_Hier.loadDAG(this.m_Labels);
/*     */       } else {
/* 155 */         for (int i = 0; i < this.m_Labels.length; i++) {
/* 156 */           if (!this.m_Labels[i].equals(ClassesValue.EMPTY_SET_INDICATOR)) {
/* 157 */             ClassesValue val = new ClassesValue(this.m_Labels[i], getM_Table());
/* 158 */             this.m_Hier.addClass(val);
/*     */           } 
/*     */         } 
/*     */       } 
/* 162 */       this.m_Hier.initialize();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void initializeFrom(ClusAttrType other_type) {
/* 167 */     ClassesAttrType other = (ClassesAttrType)other_type;
/* 168 */     this.m_Hier = other.getHier();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void initSettings(Settings sett) {
/* 174 */     if (sett.getHierType() == 1) {
/* 175 */       getHier().setHierType(1);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeARFFType(PrintWriter wrt) throws ClusException {
/*     */     ArrayList<String> list;
/* 181 */     if (getSettings().getHierType() == 1) {
/* 182 */       list = getHier().getAllParentChildTuples();
/*     */     } else {
/* 184 */       list = getHier().getAllPaths();
/*     */     } 
/* 186 */     wrt.print("hierarchical ");
/* 187 */     for (int i = 0; i < list.size(); i++) {
/* 188 */       if (i != 0) wrt.print(","); 
/* 189 */       wrt.print(list.get(i));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringTable getM_Table() {
/* 197 */     return this.m_Table;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setM_Table(StringTable m_Table) {
/* 204 */     this.m_Table = m_Table;
/*     */   }
/*     */   
/*     */   public class MySerializable
/*     */     extends ClusSerializable {
/*     */     public boolean read(ClusReader data, DataTuple tuple) throws IOException {
/* 210 */       String val = data.readString();
/* 211 */       if (val == null) return false;
/*     */       
/*     */       try {
/* 214 */         ClassesTuple ct = new ClassesTuple(val, ClassesAttrType.this.getM_Table());
/* 215 */         ct.setAllIntermediate(false);
/* 216 */         tuple.setObjectVal(ct, ClassesAttrType.this.getArrayIndex());
/* 217 */       } catch (ClusException e) {
/* 218 */         throw new IOException("Error parsing attribute " + ClassesAttrType.this.getName() + " '" + val + "' at row: " + (data.getRow() + 1));
/*     */       } 
/* 220 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\hierarchical\ClassesAttrType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */