/*     */ package clus.model.test;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.util.ClusRandom;
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
/*     */ public class SubsetTest
/*     */   extends NodeTest
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int[] m_Values;
/*     */   protected NominalAttrType m_Type;
/*     */   protected double m_PosFreq;
/*     */   protected int m_MissIndex;
/*     */   
/*     */   public SubsetTest(NominalAttrType attr, int nb, boolean[] isin, double posfreq) {
/*  40 */     this.m_Type = attr;
/*  41 */     setArity(2);
/*  42 */     setPosFreq(posfreq);
/*  43 */     this.m_Values = initValues(nb, isin);
/*  44 */     this.m_MissIndex = attr.getNbValues();
/*     */   }
/*     */ 
/*     */   
/*     */   public SubsetTest(NominalAttrType attr, int nb) {
/*  49 */     setArity(2);
/*  50 */     this.m_Type = attr;
/*  51 */     this.m_Values = new int[nb];
/*  52 */     this.m_MissIndex = attr.getNbValues();
/*     */   }
/*     */   
/*     */   public ClusAttrType getType() {
/*  56 */     return (ClusAttrType)this.m_Type;
/*     */   }
/*     */   
/*     */   public void setType(ClusAttrType type) {
/*  60 */     this.m_Type = (NominalAttrType)type;
/*     */   }
/*     */   
/*     */   public String getString() {
/*  64 */     if (this.m_Values.length == 1) {
/*  65 */       return this.m_Type.getName() + " = " + this.m_Type.getValue(this.m_Values[0]);
/*     */     }
/*  67 */     StringBuffer buffer = new StringBuffer();
/*  68 */     buffer.append(this.m_Type.getName());
/*  69 */     if (this.m_Values.length == 0) {
/*  70 */       buffer.append(" in ?");
/*     */     } else {
/*  72 */       buffer.append(" in {");
/*  73 */       for (int i = 0; i < this.m_Values.length; i++) {
/*  74 */         if (i != 0) {
/*  75 */           buffer.append(",");
/*     */         }
/*  77 */         buffer.append(this.m_Type.getValue(this.m_Values[i]));
/*     */       } 
/*  79 */       buffer.append("}");
/*     */     } 
/*  81 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasConstants() {
/*  86 */     return (this.m_Values.length > 0);
/*     */   }
/*     */   
/*     */   public int getNbValues() {
/*  90 */     return this.m_Values.length;
/*     */   }
/*     */   
/*     */   public int getValue(int i) {
/*  94 */     return this.m_Values[i];
/*     */   }
/*     */   
/*     */   public void setValue(int idx, int val) {
/*  98 */     this.m_Values[idx] = val;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(NodeTest test) {
/* 103 */     if (this.m_Type != test.getType()) {
/* 104 */       return false;
/*     */     }
/* 106 */     SubsetTest ntest = (SubsetTest)test;
/*     */     
/* 108 */     int nb = this.m_Values.length;
/* 109 */     int[] ovalues = ntest.m_Values;
/* 110 */     if (nb != ovalues.length) {
/* 111 */       return false;
/*     */     }
/* 113 */     for (int i = 0; i < nb; i++) {
/* 114 */       if (this.m_Values[i] != ovalues[i]) {
/* 115 */         return false;
/*     */       }
/*     */     } 
/* 118 */     return true;
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 122 */     int code = this.m_Type.getIndex() * 1000;
/* 123 */     for (int i = 0; i < this.m_Values.length; i++) {
/* 124 */       code += this.m_Values[i];
/*     */     }
/* 126 */     return code + this.m_Values.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int nominalPredict(int value) {
/* 131 */     if (value == this.m_MissIndex) {
/* 132 */       return (ClusRandom.nextDouble(0) < this.m_PosFreq) ? 0 : 1;
/*     */     }
/*     */ 
/*     */     
/* 136 */     for (int i = 0; i < this.m_Values.length; i++) {
/* 137 */       if (this.m_Values[i] == value) {
/* 138 */         return 0;
/*     */       }
/*     */     } 
/* 141 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int nominalPredictWeighted(int value) {
/* 146 */     if (value == this.m_MissIndex)
/*     */     {
/* 148 */       return hasUnknownBranch() ? 2 : -1;
/*     */     }
/*     */     
/* 151 */     for (int i = 0; i < this.m_Values.length; i++) {
/* 152 */       if (this.m_Values[i] == value) {
/* 153 */         return 0;
/*     */       }
/*     */     } 
/* 156 */     return 1;
/*     */   }
/*     */   
/*     */   public int predictWeighted(DataTuple tuple) {
/* 160 */     int val = this.m_Type.getNominal(tuple);
/* 161 */     return nominalPredictWeighted(val);
/*     */   }
/*     */   
/*     */   public NodeTest getBranchTest(int i) {
/* 165 */     if (i == 0) {
/* 166 */       return this;
/*     */     }
/* 168 */     int pos = 0;
/* 169 */     int nb = this.m_Type.getNbValues() - getNbValues();
/* 170 */     SubsetTest test = new SubsetTest(this.m_Type, nb);
/* 171 */     boolean[] isin = getIsInArray();
/* 172 */     for (int j = 0; j < isin.length; j++) {
/* 173 */       if (!isin[j]) {
/* 174 */         test.setValue(pos++, j);
/*     */       }
/*     */     } 
/* 177 */     test.setPosFreq(1.0D - getPosFreq());
/* 178 */     return test;
/*     */   }
/*     */ 
/*     */   
/*     */   public NodeTest simplifyConjunction(NodeTest other) {
/* 183 */     if (getType() != other.getType()) {
/* 184 */       return null;
/*     */     }
/* 186 */     if (other instanceof SubsetTest) {
/* 187 */       SubsetTest oset = (SubsetTest)other;
/* 188 */       boolean[] isin_me = getIsInArray();
/* 189 */       boolean[] isin_other = oset.getIsInArray();
/* 190 */       int count = 0;
/* 191 */       for (int i = 0; i < isin_me.length; i++) {
/* 192 */         if (isin_me[i] && isin_other[i]) {
/* 193 */           count++;
/*     */         }
/*     */       } 
/* 196 */       int pos = 0;
/* 197 */       SubsetTest test = new SubsetTest(this.m_Type, count);
/* 198 */       for (int j = 0; j < isin_me.length; j++) {
/* 199 */         if (isin_me[j] && isin_other[j]) {
/* 200 */           test.setValue(pos++, j);
/*     */         }
/*     */       } 
/* 203 */       test.setPosFreq(Math.min(getPosFreq(), oset.getPosFreq()));
/* 204 */       return test;
/*     */     } 
/* 206 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean[] getIsInArray() {
/* 212 */     boolean[] res = new boolean[this.m_Type.getNbValues()];
/* 213 */     for (int i = 0; i < getNbValues(); i++) {
/* 214 */       res[getValue(i)] = true;
/*     */     }
/* 216 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] initValues(int nb, boolean[] isin) {
/* 224 */     int i = 0;
/* 225 */     int[] values = new int[nb];
/* 226 */     for (int j = 0; j < isin.length; j++) {
/* 227 */       if (isin[j]) {
/* 228 */         values[i++] = j;
/*     */       }
/*     */     } 
/* 231 */     return values;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\test\SubsetTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */