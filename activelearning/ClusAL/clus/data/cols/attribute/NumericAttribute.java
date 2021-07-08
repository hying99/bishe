/*     */ package clus.data.cols.attribute;
/*     */ 
/*     */ import clus.data.cols.ColTarget;
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.selection.ClusSelection;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import jeans.util.MyArray;
/*     */ import jeans.util.sort.DoubleIndexSorter;
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
/*     */ public class NumericAttribute
/*     */   extends NumericAttrBase
/*     */ {
/*  37 */   public int DEBUG = 0;
/*     */   
/*     */   public double[] m_Data;
/*     */   public int[] m_Index;
/*     */   protected int m_NbRows;
/*     */   
/*     */   public NumericAttribute(NumericAttrType type) {
/*  44 */     super(type);
/*     */   }
/*     */   
/*     */   public void resize(int rows) {
/*  48 */     this.m_NbRows = rows;
/*  49 */     this.m_Data = new double[rows];
/*     */   }
/*     */   
/*     */   public ClusAttribute select(ClusSelection sel, int nbsel) {
/*  53 */     int s_data = 0;
/*  54 */     int s_subset = 0;
/*  55 */     double[] data = this.m_Data;
/*  56 */     this.m_Data = new double[this.m_NbRows - nbsel];
/*  57 */     double[] subset = new double[nbsel];
/*  58 */     for (int i = 0; i < this.m_NbRows; i++) {
/*  59 */       if (sel.isSelected(i)) { subset[s_subset++] = data[i]; }
/*  60 */       else { this.m_Data[s_data++] = data[i]; }
/*     */     
/*  62 */     }  this.m_NbRows -= nbsel;
/*  63 */     NumericAttribute s_attr = new NumericAttribute(this.m_Type);
/*  64 */     s_attr.m_Data = subset;
/*  65 */     s_attr.m_NbRows = nbsel;
/*  66 */     return s_attr;
/*     */   }
/*     */   
/*     */   public void insert(ClusAttribute attr, ClusSelection sel, int nb_new) {
/*  70 */     int s_data = 0;
/*  71 */     int s_subset = 0;
/*  72 */     double[] data = this.m_Data;
/*  73 */     this.m_Data = new double[nb_new];
/*  74 */     double[] subset = ((NumericAttribute)attr).m_Data;
/*  75 */     for (int i = 0; i < nb_new; i++) {
/*  76 */       if (sel.isSelected(i)) { this.m_Data[i] = subset[s_subset++]; }
/*  77 */       else { this.m_Data[i] = data[s_data++]; }
/*     */     
/*  79 */     }  this.m_NbRows = nb_new;
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepare() {
/*  84 */     DoubleIndexSorter sorter = DoubleIndexSorter.getInstance();
/*  85 */     sorter.setData(this.m_Data);
/*  86 */     sorter.sort();
/*  87 */     this.m_Index = sorter.getIndex();
/*  88 */     if (this.DEBUG == 1) {
/*     */       try {
/*  90 */         PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("debug/attr-" + getName())));
/*  91 */         for (int i = 0; i < this.m_Data.length; ) { writer.println(this.m_Data[i] + "\t" + this.m_Index[i]); i++; }
/*  92 */          writer.close();
/*  93 */       } catch (IOException iOException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void unprepare() {
/*  99 */     this.m_Data = DoubleIndexSorter.unsort(this.m_Data, this.m_Index);
/* 100 */     this.m_Index = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void findBestTest(MyArray leaves, ColTarget target, ClusStatManager smanager) {
/* 105 */     int nb = leaves.size();
/* 106 */     for (int i = 0; i < nb; i++);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     int first = 0;
/* 115 */     if (this.m_Type.hasMissing()) {
/* 116 */       while (first < this.m_NbRows && this.m_Data[first] == Double.POSITIVE_INFINITY)
/*     */       {
/*     */ 
/*     */         
/* 120 */         first++;
/*     */       }
/* 122 */       for (int k = 0; k < nb; k++);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 127 */       for (int k = 0; k < nb; k++);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     for (int j = first; j < this.m_NbRows; j++);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void split(ColTarget target) {
/* 151 */     for (int i = 0; i < this.m_NbRows; i++);
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
/*     */   public boolean read(ClusReader data, int row) throws IOException {
/* 166 */     if (!data.readNoSpace()) return false; 
/* 167 */     double val = this.m_Data[row] = data.getFloat();
/* 168 */     if (val == Double.POSITIVE_INFINITY) this.m_Type.incNbMissing(); 
/* 169 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\cols\attribute\NumericAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */