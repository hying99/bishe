/*     */ package clus.data.type;
/*     */ 
/*     */ import clus.algo.kNN.NominalStatistic;
/*     */ import clus.data.cols.ColTarget;
/*     */ import clus.data.cols.attribute.ClusAttribute;
/*     */ import clus.data.cols.attribute.NominalTarget;
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.io.ClusSerializable;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.StringTokenizer;
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
/*     */ public class NominalAttrType
/*     */   extends ClusAttrType
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  44 */   public static final String[] BINARY_NAMES = new String[] { "1", "0" };
/*     */   
/*     */   public static final int THIS_TYPE = 0;
/*     */   
/*     */   public static final String THIS_TYPE_NAME = "Nominal";
/*     */   
/*     */   public int m_NbValues;
/*     */   public String[] m_Values;
/*     */   protected transient Hashtable m_Hash;
/*     */   private NominalStatistic $stat;
/*     */   
/*     */   public NominalAttrType(String name, String type) {
/*  56 */     super(name);
/*  57 */     int len = type.length();
/*  58 */     StringTokenizer tokens = new StringTokenizer(type.substring(1, len - 1), ",");
/*  59 */     ArrayList<String> values = new ArrayList();
/*  60 */     while (tokens.hasMoreTokens()) {
/*  61 */       String value = tokens.nextToken().trim();
/*  62 */       if (!value.equals("?")) values.add(value); 
/*     */     } 
/*  64 */     if (values.size() == 2 && values.get(0).equals("0") && values.get(1).equals("1")) {
/*     */       
/*  66 */       values.set(0, "1");
/*  67 */       values.set(1, "0");
/*     */     } 
/*  69 */     this.m_NbValues = values.size();
/*  70 */     this.m_Values = new String[this.m_NbValues];
/*  71 */     for (int i = 0; i < this.m_NbValues; i++) {
/*  72 */       this.m_Values[i] = values.get(i);
/*     */     }
/*  74 */     createHash();
/*     */   }
/*     */   
/*     */   public String[] getValues() {
/*  78 */     return this.m_Values;
/*     */   }
/*     */   
/*     */   public NominalAttrType(String name, String[] values) {
/*  82 */     super(name);
/*  83 */     this.m_NbValues = values.length;
/*  84 */     this.m_Values = values;
/*     */   }
/*     */   
/*     */   public NominalAttrType(String name, ArrayList values) {
/*  88 */     super(name);
/*  89 */     this.m_NbValues = values.size();
/*  90 */     this.m_Values = (String[])values.toArray((Object[])new String[values.size()]);
/*     */   }
/*     */   
/*     */   public NominalAttrType(String name) {
/*  94 */     super(name);
/*  95 */     this.m_NbValues = 2;
/*  96 */     this.m_Values = BINARY_NAMES;
/*     */   }
/*     */   
/*     */   public NominalAttrType(String name, int nbvalues) {
/* 100 */     super(name);
/* 101 */     this.m_NbValues = nbvalues;
/* 102 */     this.m_Values = new String[nbvalues];
/*     */   }
/*     */   
/*     */   public final void setValue(int idx, String name) {
/* 106 */     this.m_Values[idx] = name;
/*     */   }
/*     */   
/*     */   public ClusAttrType cloneType() {
/* 110 */     NominalAttrType at = new NominalAttrType(this.m_Name, this.m_Values);
/* 111 */     cloneType(at);
/* 112 */     return at;
/*     */   }
/*     */   
/*     */   public int getTypeIndex() {
/* 116 */     return 0;
/*     */   }
/*     */   
/*     */   public String getTypeName() {
/* 120 */     return "Nominal";
/*     */   }
/*     */   
/*     */   public int getValueType() {
/* 124 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getNbValues() {
/* 131 */     return this.m_NbValues;
/*     */   }
/*     */   
/*     */   public final int getNbValuesInclMissing() {
/* 135 */     return this.m_NbValues + ((this.m_NbMissing > 0) ? 1 : 0);
/*     */   }
/*     */   
/*     */   public String getValue(int idx) {
/* 139 */     return this.m_Values[idx];
/*     */   }
/*     */   
/*     */   public String getValueOrMissing(int idx) {
/* 143 */     return (idx < this.m_Values.length) ? this.m_Values[idx] : "?";
/*     */   }
/*     */   
/*     */   public Integer getValueIndex(String value) {
/* 147 */     return (Integer)this.m_Hash.get(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxNbStats() {
/* 152 */     return this.m_NbValues + 1;
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
/*     */   public void createHash() {
/* 164 */     this.m_Hash = new Hashtable<>();
/* 165 */     for (int i = 0; i < this.m_NbValues; i++) {
/* 166 */       this.m_Hash.put(this.m_Values[i], new Integer(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public String getTypeString() {
/* 171 */     StringBuffer res = new StringBuffer();
/* 172 */     res.append("{");
/* 173 */     for (int i = 0; i < this.m_NbValues; i++) {
/* 174 */       if (i != 0) res.append(","); 
/* 175 */       res.append(this.m_Values[i]);
/*     */     } 
/* 177 */     res.append("}");
/* 178 */     return res.toString();
/*     */   }
/*     */   
/*     */   public String getString(DataTuple tuple) {
/* 182 */     int idx = getNominal(tuple);
/* 183 */     return (idx >= this.m_NbValues) ? "?" : this.m_Values[idx];
/*     */   }
/*     */   
/*     */   public boolean isMissing(DataTuple tuple) {
/* 187 */     return (getNominal(tuple) >= this.m_NbValues);
/*     */   }
/*     */   
/*     */   public int getNominal(DataTuple tuple) {
/* 191 */     return tuple.getIntVal(this.m_ArrayIndex);
/*     */   }
/*     */   
/*     */   public void setNominal(DataTuple tuple, int intvalue) {
/* 195 */     tuple.setIntVal(intvalue, getArrayIndex());
/*     */   }
/*     */   
/*     */   public int compareValue(DataTuple t1, DataTuple t2) {
/* 199 */     int i1 = getNominal(t1);
/* 200 */     int i2 = getNominal(t2);
/* 201 */     return (i1 == i2) ? 0 : 1;
/*     */   }
/*     */   
/*     */   public ClusAttribute createTargetAttr(ColTarget target) {
/* 205 */     return (ClusAttribute)new NominalTarget(target, this, getArrayIndex());
/*     */   }
/*     */   
/*     */   public ClusSerializable createRowSerializable() throws ClusException {
/* 209 */     return new MySerializable();
/*     */   }
/*     */   
/*     */   public void writeARFFType(PrintWriter wrt) throws ClusException {
/* 213 */     wrt.print(getTypeString());
/*     */   }
/*     */   
/*     */   public class MySerializable
/*     */     extends ClusSerializable {
/*     */     public boolean read(ClusReader data, DataTuple tuple) throws IOException {
/* 219 */       String value = data.readString();
/* 220 */       if (value == null) return false; 
/* 221 */       if (value.equals("?")) {
/* 222 */         NominalAttrType.this.incNbMissing();
/* 223 */         NominalAttrType.this.setNominal(tuple, NominalAttrType.this.getNbValues());
/*     */       } else {
/* 225 */         Integer i = NominalAttrType.this.getValueIndex(value);
/* 226 */         if (i != null) {
/* 227 */           NominalAttrType.this.setNominal(tuple, i.intValue());
/*     */         } else {
/* 229 */           throw new IOException("Illegal value '" + value + "' for attribute " + NominalAttrType.this.getName() + " at row " + (data.getRow() + 1));
/*     */         } 
/*     */       } 
/* 232 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatistic(NominalStatistic stat) {
/* 239 */     this.$stat = stat;
/*     */   }
/*     */   public NominalStatistic getStatistic() {
/* 242 */     return this.$stat;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\type\NominalAttrType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */