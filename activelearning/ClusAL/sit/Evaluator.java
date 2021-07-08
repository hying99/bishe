/*     */ package sit;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.error.MSError;
/*     */ import clus.error.MisclassificationError;
/*     */ import clus.error.PearsonCorrelation;
/*     */ import clus.error.RMSError;
/*     */ import clus.error.RelativeError;
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
/*     */ public final class Evaluator
/*     */ {
/*     */   public static final double getPearsonCorrelation(ArrayList<RowData[]> folds, int errorIdx) {
/*  29 */     RowData[] temp = folds.get(0);
/*  30 */     ClusSchema schema = temp[0].getSchema();
/*  31 */     ClusErrorList parent = new ClusErrorList();
/*  32 */     NumericAttrType[] num = schema.getNumericAttrUse(0);
/*  33 */     PearsonCorrelation error = new PearsonCorrelation(parent, num);
/*  34 */     parent.addError((ClusError)error);
/*  35 */     for (int f = 0; f < folds.size(); f++) {
/*  36 */       RowData[] fold = folds.get(f);
/*     */       
/*  38 */       for (int t = 0; t < fold[0].getNbRows(); t++) {
/*  39 */         DataTuple tuple_real = fold[0].getTuple(t);
/*  40 */         DataTuple tuple_prediction = fold[1].getTuple(t);
/*  41 */         parent.addExample(tuple_real, tuple_prediction);
/*     */       } 
/*     */     } 
/*     */     
/*  45 */     if (errorIdx == -1)
/*     */     {
/*  47 */       return 0.0D;
/*     */     }
/*     */     
/*  50 */     return error.getModelErrorComponent(errorIdx);
/*     */   }
/*     */ 
/*     */   
/*     */   public static final double getPearsonCorrelation(RowData[] data, int errorIdx) {
/*  55 */     if (errorIdx == -1) {
/*  56 */       return 0.0D;
/*     */     }
/*  58 */     ClusSchema schema = data[0].getSchema();
/*  59 */     ClusErrorList parent = new ClusErrorList();
/*  60 */     NumericAttrType[] num = schema.getNumericAttrUse(0);
/*  61 */     PearsonCorrelation error = new PearsonCorrelation(parent, num);
/*  62 */     parent.addError((ClusError)error);
/*  63 */     for (int t = 0; t < data[0].getNbRows(); t++) {
/*  64 */       DataTuple tuple_real = data[0].getTuple(t);
/*  65 */       DataTuple tuple_prediction = data[1].getTuple(t);
/*  66 */       parent.addExample(tuple_real, tuple_prediction);
/*     */     } 
/*     */ 
/*     */     
/*  70 */     return error.getModelErrorComponent(errorIdx);
/*     */   }
/*     */ 
/*     */   
/*     */   public static final double getMSE(RowData[] data, int errorIdx) {
/*  75 */     if (errorIdx == -1) {
/*  76 */       return 0.0D;
/*     */     }
/*  78 */     ClusSchema schema = data[0].getSchema();
/*  79 */     ClusErrorList parent = new ClusErrorList();
/*  80 */     NumericAttrType[] num = schema.getNumericAttrUse(0);
/*  81 */     MSError error = new MSError(parent, num);
/*  82 */     parent.addError((ClusError)error);
/*  83 */     for (int t = 0; t < data[0].getNbRows(); t++) {
/*  84 */       DataTuple tuple_real = data[0].getTuple(t);
/*  85 */       DataTuple tuple_prediction = data[1].getTuple(t);
/*  86 */       parent.addExample(tuple_real, tuple_prediction);
/*     */     } 
/*     */     
/*  89 */     double err = error.getModelErrorComponent(errorIdx);
/*     */     
/*  91 */     return err;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final MSError getMSE(RowData[] data) {
/*  97 */     ClusSchema schema = data[0].getSchema();
/*  98 */     ClusErrorList parent = new ClusErrorList();
/*  99 */     NumericAttrType[] num = schema.getNumericAttrUse(0);
/* 100 */     MSError error = new MSError(parent, num);
/* 101 */     parent.addError((ClusError)error);
/* 102 */     for (int t = 0; t < data[0].getNbRows(); t++) {
/* 103 */       DataTuple tuple_real = data[0].getTuple(t);
/* 104 */       DataTuple tuple_prediction = data[1].getTuple(t);
/* 105 */       parent.addExample(tuple_real, tuple_prediction);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 110 */     return error;
/*     */   }
/*     */   
/*     */   public static final double getMSE(ArrayList<RowData[]> folds, int errorIdx) {
/* 114 */     RowData[] temp = folds.get(0);
/* 115 */     ClusSchema schema = temp[0].getSchema();
/* 116 */     ClusErrorList parent = new ClusErrorList();
/* 117 */     NumericAttrType[] num = schema.getNumericAttrUse(0);
/* 118 */     MSError error = new MSError(parent, num);
/* 119 */     parent.addError((ClusError)error);
/* 120 */     for (int f = 0; f < folds.size(); f++) {
/* 121 */       RowData[] fold = folds.get(f);
/*     */       
/* 123 */       for (int t = 0; t < fold[0].getNbRows(); t++) {
/* 124 */         DataTuple tuple_real = fold[0].getTuple(t);
/* 125 */         DataTuple tuple_prediction = fold[1].getTuple(t);
/* 126 */         parent.addExample(tuple_real, tuple_prediction);
/*     */       } 
/*     */     } 
/*     */     
/* 130 */     if (errorIdx == -1)
/*     */     {
/* 132 */       return 0.0D;
/*     */     }
/* 134 */     return error.getModelErrorComponent(errorIdx);
/*     */   }
/*     */   
/*     */   public static final double getMisclassificationError(ArrayList<RowData[]> folds, int errorIdx) {
/* 138 */     RowData[] temp = folds.get(0);
/* 139 */     ClusSchema schema = temp[0].getSchema();
/* 140 */     ClusErrorList parent = new ClusErrorList();
/* 141 */     NominalAttrType[] nom = schema.getNominalAttrUse(0);
/* 142 */     MisclassificationError error = new MisclassificationError(parent, nom);
/* 143 */     parent.addError((ClusError)error);
/* 144 */     for (int f = 0; f < folds.size(); f++) {
/* 145 */       RowData[] fold = folds.get(f);
/*     */       
/* 147 */       for (int t = 0; t < fold[0].getNbRows(); t++) {
/* 148 */         DataTuple tuple_real = fold[0].getTuple(t);
/* 149 */         DataTuple tuple_prediction = fold[1].getTuple(t);
/* 150 */         parent.addExample(tuple_real, tuple_prediction);
/*     */       } 
/*     */     } 
/*     */     
/* 154 */     if (errorIdx == -1)
/*     */     {
/* 156 */       return 0.0D;
/*     */     }
/* 158 */     return error.getModelErrorComponent(errorIdx);
/*     */   }
/*     */   
/*     */   public static final double getMisclassificationError(RowData[] data, int errorIdx) {
/* 162 */     if (errorIdx == -1) {
/* 163 */       return 0.0D;
/*     */     }
/* 165 */     ClusSchema schema = data[0].getSchema();
/* 166 */     ClusErrorList parent = new ClusErrorList();
/* 167 */     NominalAttrType[] nom = schema.getNominalAttrUse(0);
/* 168 */     MisclassificationError error = new MisclassificationError(parent, nom);
/* 169 */     parent.addError((ClusError)error);
/* 170 */     for (int t = 0; t < data[0].getNbRows(); t++) {
/* 171 */       DataTuple tuple_real = data[0].getTuple(t);
/* 172 */       DataTuple tuple_prediction = data[1].getTuple(t);
/* 173 */       parent.addExample(tuple_real, tuple_prediction);
/*     */     } 
/*     */     
/* 176 */     double err = error.getModelErrorComponent(errorIdx);
/* 177 */     return err;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final double getRelativeError(ArrayList<RowData[]> folds, int errorIdx) {
/* 182 */     RowData[] temp = folds.get(0);
/* 183 */     ClusSchema schema = temp[0].getSchema();
/* 184 */     ClusErrorList parent = new ClusErrorList();
/* 185 */     NumericAttrType[] num = schema.getNumericAttrUse(0);
/* 186 */     RelativeError error = new RelativeError(parent, num);
/* 187 */     parent.addError((ClusError)error);
/* 188 */     for (int f = 0; f < folds.size(); f++) {
/* 189 */       RowData[] fold = folds.get(f);
/*     */       
/* 191 */       for (int t = 0; t < fold[0].getNbRows(); t++) {
/* 192 */         DataTuple tuple_real = fold[0].getTuple(t);
/* 193 */         DataTuple tuple_prediction = fold[1].getTuple(t);
/* 194 */         parent.addExample(tuple_real, tuple_prediction);
/*     */       } 
/*     */     } 
/*     */     
/* 198 */     if (errorIdx == -1)
/*     */     {
/* 200 */       return 0.0D;
/*     */     }
/* 202 */     return error.getModelErrorComponent(errorIdx);
/*     */   }
/*     */ 
/*     */   
/*     */   public static final double getRelativeError(RowData[] data, int errorIdx) {
/* 207 */     if (errorIdx == -1) {
/* 208 */       return 0.0D;
/*     */     }
/* 210 */     ClusSchema schema = data[0].getSchema();
/* 211 */     ClusErrorList parent = new ClusErrorList();
/* 212 */     NumericAttrType[] num = schema.getNumericAttrUse(0);
/* 213 */     RelativeError error = new RelativeError(parent, num);
/* 214 */     parent.addError((ClusError)error);
/* 215 */     for (int t = 0; t < data[0].getNbRows(); t++) {
/* 216 */       DataTuple tuple_real = data[0].getTuple(t);
/* 217 */       DataTuple tuple_prediction = data[1].getTuple(t);
/* 218 */       parent.addExample(tuple_real, tuple_prediction);
/*     */     } 
/*     */     
/* 221 */     double err = error.getModelErrorComponent(errorIdx);
/* 222 */     return err;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final double getRMSE(RowData[] data, int errorIdx) {
/* 227 */     if (errorIdx == -1) {
/* 228 */       return 0.0D;
/*     */     }
/* 230 */     ClusSchema schema = data[0].getSchema();
/* 231 */     ClusErrorList parent = new ClusErrorList();
/* 232 */     NumericAttrType[] num = schema.getNumericAttrUse(0);
/* 233 */     RMSError error = new RMSError(parent, num);
/* 234 */     parent.addError((ClusError)error);
/* 235 */     for (int t = 0; t < data[0].getNbRows(); t++) {
/* 236 */       DataTuple tuple_real = data[0].getTuple(t);
/* 237 */       DataTuple tuple_prediction = data[1].getTuple(t);
/* 238 */       parent.addExample(tuple_real, tuple_prediction);
/*     */     } 
/*     */     
/* 241 */     double err = error.getModelErrorComponent(errorIdx);
/* 242 */     return err;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\Evaluator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */