/*     */ package clus.data.cols.attribute;
/*     */ 
/*     */ import clus.data.cols.ColTarget;
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.selection.ClusSelection;
/*     */ import java.io.IOException;
/*     */ import jeans.util.MyArray;
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
/*     */ public class NominalAttribute
/*     */   extends NominalAttrBase
/*     */ {
/*     */   public int[] m_Data;
/*     */   protected int m_NbRows;
/*     */   protected static final boolean DEBUG = true;
/*     */   
/*     */   public NominalAttribute(NominalAttrType type) {
/*  42 */     super(type);
/*     */   }
/*     */   
/*     */   public void resize(int rows) {
/*  46 */     this.m_NbRows = rows;
/*  47 */     this.m_Data = new int[rows];
/*     */   }
/*     */   
/*     */   public ClusAttribute select(ClusSelection sel, int nbsel) {
/*  51 */     int s_data = 0;
/*  52 */     int s_subset = 0;
/*  53 */     int[] data = this.m_Data;
/*  54 */     this.m_Data = new int[this.m_NbRows - nbsel];
/*  55 */     int[] subset = new int[nbsel];
/*  56 */     for (int i = 0; i < this.m_NbRows; i++) {
/*  57 */       if (sel.isSelected(i)) { subset[s_subset++] = data[i]; }
/*  58 */       else { this.m_Data[s_data++] = data[i]; }
/*     */     
/*  60 */     }  this.m_NbRows -= nbsel;
/*  61 */     NominalAttribute s_attr = new NominalAttribute(this.m_Type);
/*  62 */     s_attr.m_Data = subset;
/*  63 */     s_attr.m_NbRows = nbsel;
/*  64 */     return s_attr;
/*     */   }
/*     */   
/*     */   public void insert(ClusAttribute attr, ClusSelection sel, int nb_new) {
/*  68 */     int s_data = 0;
/*  69 */     int s_subset = 0;
/*  70 */     int[] data = this.m_Data;
/*  71 */     this.m_Data = new int[nb_new];
/*  72 */     int[] subset = ((NominalAttribute)attr).m_Data;
/*  73 */     for (int i = 0; i < nb_new; i++) {
/*  74 */       if (sel.isSelected(i)) { this.m_Data[i] = subset[s_subset++]; }
/*  75 */       else { this.m_Data[i] = data[s_data++]; }
/*     */     
/*  77 */     }  this.m_NbRows = nb_new;
/*     */   }
/*     */ 
/*     */   
/*     */   public void findBestTest(MyArray leaves, ColTarget target, ClusStatManager smanager) {
/*  82 */     int nb = leaves.size();
/*     */ 
/*     */     
/*  85 */     for (int i = 0; i < nb; i++);
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
/*  98 */     findSplit(leaves, smanager);
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
/*     */   public void findSplit(MyArray leaves, ClusStatManager smanager) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void split(ColTarget target) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean read(ClusReader data, int row) throws IOException {
/* 131 */     String value = data.readString();
/* 132 */     if (value == null) return false; 
/* 133 */     if (value.equals("?")) {
/* 134 */       this.m_Type.incNbMissing();
/* 135 */       this.m_Data[row] = this.m_Type.getNbValues();
/*     */     } else {
/* 137 */       Integer i = this.m_Type.getValueIndex(value);
/* 138 */       if (i != null) {
/* 139 */         this.m_Data[row] = i.intValue();
/*     */       } else {
/* 141 */         throw new IOException("Illegal value '" + value + "' for attribute " + getName() + " at row " + (row + 1));
/*     */       } 
/*     */     } 
/* 144 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\cols\attribute\NominalAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */