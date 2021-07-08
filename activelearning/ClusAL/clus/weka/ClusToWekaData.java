/*     */ package clus.weka;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import java.util.ArrayList;
/*     */ import weka.core.Attribute;
/*     */ import weka.core.FastVector;
/*     */ import weka.core.Instance;
/*     */ import weka.core.Instances;
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
/*     */ public class ClusToWekaData
/*     */ {
/*     */   protected ClusSchema m_Schema;
/*  38 */   protected ArrayList m_NomAttrs = new ArrayList();
/*  39 */   protected ArrayList m_NumAttrs = new ArrayList();
/*  40 */   protected FastVector m_WekaTypes = new FastVector();
/*     */   protected ClusAttributeWeights m_Weights;
/*     */   protected Instances m_Instances;
/*     */   protected int m_Target;
/*     */   
/*     */   public ClusToWekaData(ClusSchema schema) {
/*  46 */     this.m_Schema = schema;
/*  47 */     for (int i = 0; i < this.m_Schema.getNbAttributes(); i++) {
/*  48 */       ClusAttrType type = this.m_Schema.getAttrType(i);
/*  49 */       if (type.getStatus() != 0)
/*  50 */         if (type instanceof NumericAttrType) {
/*  51 */           this.m_NumAttrs.add(type);
/*     */         } else {
/*  53 */           this.m_NomAttrs.add(type);
/*     */         }  
/*     */     } 
/*     */     int j;
/*  57 */     for (j = 0; j < this.m_NumAttrs.size(); j++) {
/*  58 */       NumericAttrType type = this.m_NumAttrs.get(j);
/*  59 */       if (type.getStatus() == 1) {
/*  60 */         this.m_Target = this.m_WekaTypes.size();
/*     */       }
/*  62 */       this.m_WekaTypes.addElement(new Attribute(type.getName()));
/*     */     } 
/*  64 */     for (j = 0; j < this.m_NomAttrs.size(); j++) {
/*  65 */       NominalAttrType type = this.m_NomAttrs.get(j);
/*  66 */       if (type.getStatus() == 1) {
/*  67 */         this.m_Target = this.m_WekaTypes.size();
/*     */       }
/*  69 */       FastVector values = new FastVector();
/*  70 */       for (int k = 0; k < type.getNbValues(); k++) {
/*  71 */         values.addElement(type.getValue(k));
/*     */       }
/*  73 */       this.m_WekaTypes.addElement(new Attribute(type.getName(), values));
/*     */     } 
/*  75 */     this.m_Instances = new Instances(this.m_Schema.getRelationName(), this.m_WekaTypes, 0);
/*  76 */     this.m_Instances.setClassIndex(getClassIndex());
/*     */   }
/*     */   
/*     */   public int getIndex(String name) {
/*  80 */     for (int i = 0; i < this.m_WekaTypes.size(); i++) {
/*  81 */       Attribute attr = (Attribute)this.m_WekaTypes.elementAt(i);
/*  82 */       if (attr.name().equals(name)) return i; 
/*     */     } 
/*  84 */     return -1;
/*     */   }
/*     */   
/*     */   public void setTargetWeights(ClusAttributeWeights weights) {
/*  88 */     this.m_Weights = weights;
/*     */   }
/*     */   
/*     */   public int getClassIndex() {
/*  92 */     return this.m_Target;
/*     */   }
/*     */   
/*     */   public Instances getDummyData() {
/*  96 */     return this.m_Instances;
/*     */   }
/*     */   
/*     */   public Instance convertInstance(DataTuple tuple) {
/* 100 */     double[] values = new double[this.m_WekaTypes.size()];
/* 101 */     for (int j = 0; j < values.length; j++) {
/* 102 */       values[j] = Instance.missingValue();
/*     */     }
/* 104 */     int pos = 0; int i;
/* 105 */     for (i = 0; i < this.m_NumAttrs.size(); i++) {
/* 106 */       NumericAttrType type = this.m_NumAttrs.get(i);
/* 107 */       if (type.getStatus() == 1) {
/* 108 */         double weight = (this.m_Weights == null) ? 1.0D : this.m_Weights.getWeight((ClusAttrType)type);
/* 109 */         values[pos++] = type.getNumeric(tuple) * Math.sqrt(weight);
/*     */       } else {
/* 111 */         values[pos++] = type.getNumeric(tuple);
/*     */       } 
/*     */     } 
/* 114 */     for (i = 0; i < this.m_NomAttrs.size(); i++) {
/* 115 */       NominalAttrType type = this.m_NomAttrs.get(i);
/* 116 */       values[pos++] = type.getNominal(tuple);
/*     */     } 
/* 118 */     return new Instance(tuple.getWeight(), values);
/*     */   }
/*     */   
/*     */   public Instances convertData(RowData data) {
/* 122 */     Instances weka_data = new Instances(this.m_Schema.getRelationName(), this.m_WekaTypes, data.getNbRows());
/* 123 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 124 */       weka_data.add(convertInstance(data.getTuple(i)));
/*     */     }
/* 126 */     weka_data.setClassIndex(this.m_WekaTypes.size() - 1);
/* 127 */     return weka_data;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\weka\ClusToWekaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */