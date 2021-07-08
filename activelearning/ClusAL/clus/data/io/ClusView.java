/*     */ package clus.data.io;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.io.ClusSerializable;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
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
/*     */ public class ClusView
/*     */ {
/*  35 */   protected ArrayList m_Attr = new ArrayList();
/*     */   
/*     */   public int getNbAttributes() {
/*  38 */     return this.m_Attr.size();
/*     */   }
/*     */   
/*     */   public ClusSerializable getAttribute(int idx) {
/*  42 */     return this.m_Attr.get(idx);
/*     */   }
/*     */   
/*     */   public void addAttribute(ClusSerializable attr) {
/*  46 */     this.m_Attr.add(attr);
/*     */   }
/*     */   
/*     */   public RowData readData(ClusReader reader, ClusSchema schema) throws IOException, ClusException {
/*  50 */     schema.setReader(true);
/*  51 */     ArrayList<DataTuple> items = new ArrayList();
/*  52 */     DataTuple tuple = readDataTupleFirst(reader, schema);
/*  53 */     while (tuple != null) {
/*  54 */       items.add(tuple);
/*  55 */       tuple = readDataTupleNext(reader, schema);
/*     */     } 
/*  57 */     for (int j = 0; j < this.m_Attr.size(); j++) {
/*  58 */       ClusSerializable attr = this.m_Attr.get(j);
/*  59 */       attr.term(schema);
/*     */     } 
/*     */     
/*  62 */     schema.setReader(false);
/*  63 */     return new RowData(items, schema);
/*     */   }
/*     */   public DataTuple readDataTupleFirst(ClusReader reader, ClusSchema schema) throws IOException, ClusException {
/*  66 */     if (!reader.hasMoreTokens()) {
/*  67 */       return null;
/*     */     }
/*  69 */     boolean sparse = reader.isNextChar(123);
/*  70 */     if (sparse) {
/*  71 */       this.m_Attr.clear();
/*  72 */       schema.ensureSparse();
/*  73 */       schema.createNormalView(this);
/*     */     } 
/*  75 */     return readDataTuple(reader, schema, sparse);
/*     */   }
/*     */   
/*     */   public DataTuple readDataTupleNext(ClusReader reader, ClusSchema schema) throws IOException {
/*  79 */     if (!reader.hasMoreTokens()) {
/*  80 */       return null;
/*     */     }
/*  82 */     boolean sparse = reader.isNextChar(123);
/*  83 */     if (sparse && !schema.isSparse()) {
/*  84 */       throw new IOException("Sparse tuple found in a non-sparse data set (at row " + (reader.getRow() + 1) + ")");
/*     */     }
/*  86 */     return readDataTuple(reader, schema, sparse);
/*     */   }
/*     */   
/*     */   public DataTuple readDataTuple(ClusReader reader, ClusSchema schema) throws IOException {
/*  90 */     if (!reader.hasMoreTokens()) {
/*  91 */       return null;
/*     */     }
/*  93 */     boolean sparse = reader.isNextChar(123);
/*  94 */     return readDataTuple(reader, schema, sparse);
/*     */   }
/*     */   
/*     */   public DataTuple readDataTuple(ClusReader reader, ClusSchema schema, boolean sparse) throws IOException {
/*  98 */     DataTuple tuple = schema.createTuple();
/*  99 */     if (sparse) {
/* 100 */       while (!reader.isNextChar(125)) {
/* 101 */         int idx = reader.readIntIndex();
/* 102 */         if (idx < 1 || idx > this.m_Attr.size()) {
/* 103 */           throw new IOException("Error attribute index '" + idx + "' out of range [1," + this.m_Attr.size() + "] at row " + (reader.getRow() + 1));
/*     */         }
/* 105 */         ClusSerializable attr = this.m_Attr.get(idx - 1);
/* 106 */         if (!attr.read(reader, tuple)) {
/* 107 */           throw new IOException("Error reading attirbute " + this.m_Attr + " at row " + (reader.getRow() + 1));
/*     */         }
/*     */       } 
/* 110 */     } else if (this.m_Attr.size() > 0) {
/* 111 */       ClusSerializable attr_0 = this.m_Attr.get(0);
/* 112 */       if (!attr_0.read(reader, tuple)) {
/* 113 */         return null;
/*     */       }
/* 115 */       for (int j = 1; j < this.m_Attr.size(); j++) {
/* 116 */         ClusSerializable attr = this.m_Attr.get(j);
/* 117 */         if (!attr.read(reader, tuple)) {
/* 118 */           throw new IOException("Error reading attirbute " + this.m_Attr + " at row " + (reader.getRow() + 1));
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 123 */     if (reader.isNextCharNoSpace(123)) {
/* 124 */       if (!reader.readNoSpace()) {
/* 125 */         throw new IOException("Error reading tuple weight at row " + (reader.getRow() + 1));
/*     */       }
/* 127 */       tuple.setWeight(reader.getFloat());
/* 128 */       if (!reader.isNextChar(125)) {
/* 129 */         throw new IOException("Expected closing '}' after tuple weight at row " + (reader.getRow() + 1));
/*     */       }
/*     */     } 
/* 132 */     reader.readEol();
/* 133 */     return tuple;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\io\ClusView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */