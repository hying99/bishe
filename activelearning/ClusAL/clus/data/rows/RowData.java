/*     */ package clus.data.rows;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.ClusData;
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.model.test.ClusRuleConstraintInduceTest;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.model.test.SoftTest;
/*     */ import clus.selection.ClusSelection;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusRandom;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Random;
/*     */ import jeans.util.compound.DoubleObject;
/*     */ import jeans.util.sort.MSortable;
/*     */ import jeans.util.sort.MSorter;
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
/*     */ public class RowData
/*     */   extends ClusData
/*     */   implements MSortable, Serializable
/*     */ {
/*     */   public int m_Index;
/*     */   public ClusSchema m_Schema;
/*     */   public DataTuple[] m_Data;
/*     */   
/*     */   public RowData(ClusSchema schema) {
/*  52 */     this.m_Schema = schema;
/*     */   }
/*     */   
/*     */   public RowData(ClusSchema schema, int size) {
/*  56 */     this.m_Schema = schema;
/*  57 */     resizeEmpty(size);
/*     */   }
/*     */   
/*     */   public RowData(RowData data) {
/*  61 */     this((Object[])data.m_Data, data.getNbRows());
/*  62 */     this.m_Schema = data.m_Schema;
/*     */   }
/*     */   
/*     */   public RowData(Object[] data, int size) {
/*  66 */     this.m_Data = new DataTuple[size];
/*  67 */     System.arraycopy(data, 0, this.m_Data, 0, size);
/*  68 */     setNbRows(size);
/*     */   }
/*     */   
/*     */   public RowData(ArrayList list, ClusSchema schema) {
/*  72 */     this.m_Schema = schema;
/*  73 */     setFromList(list);
/*     */   }
/*     */   
/*     */   public String toString() {
/*  77 */     return toString("");
/*     */   }
/*     */   
/*     */   public String toString(String prefix) {
/*  81 */     StringBuffer sb = new StringBuffer();
/*  82 */     for (int i = 0; i < getNbRows(); i++) {
/*  83 */       sb.append(prefix);
/*  84 */       sb.append(getTuple(i).toString() + "\n");
/*     */     } 
/*  86 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public String getSummary() {
/*  90 */     return getSummary("");
/*     */   }
/*     */   
/*     */   public String getSummary(String prefix) {
/*  94 */     StringBuffer sb = new StringBuffer();
/*     */     
/*  96 */     DataTuple temp = getTuple(0);
/*  97 */     int nda = temp.getSchema().getNbNumericDescriptiveAttributes();
/*  98 */     double[] avg = new double[nda];
/*  99 */     double[] min = new double[nda];
/* 100 */     double[] max = new double[nda];
/* 101 */     double[] stddev = new double[nda];
/* 102 */     Arrays.fill(avg, 0.0D);
/* 103 */     Arrays.fill(stddev, 0.0D);
/* 104 */     Arrays.fill(min, Double.MAX_VALUE);
/* 105 */     Arrays.fill(max, Double.MIN_VALUE);
/* 106 */     int nbrows = getNbRows(); int i;
/* 107 */     for (i = 0; i < nbrows; i++) {
/* 108 */       temp = getTuple(i);
/* 109 */       ClusSchema schema = temp.getSchema();
/* 110 */       for (int j = 0; j < schema.getNbNumericDescriptiveAttributes(); j++) {
/* 111 */         NumericAttrType numericAttrType = schema.getNumericAttrUse(1)[j];
/* 112 */         double tmpvalue = numericAttrType.getNumeric(temp);
/* 113 */         if (tmpvalue > max[j]) {
/* 114 */           max[j] = tmpvalue;
/*     */         }
/* 116 */         if (tmpvalue < min[j]) {
/* 117 */           min[j] = tmpvalue;
/*     */         }
/* 119 */         avg[j] = avg[j] + tmpvalue;
/* 120 */         stddev[j] = stddev[j] + tmpvalue * tmpvalue;
/*     */       } 
/*     */     } 
/* 123 */     for (i = 0; i < nda; i++) {
/* 124 */       avg[i] = avg[i] / nbrows;
/* 125 */       stddev[i] = (stddev[i] - nbrows * avg[i] * avg[i]) / nbrows;
/* 126 */       stddev[i] = Math.sqrt(stddev[i]);
/* 127 */       min[i] = Math.round(min[i] * 100.0D) / 100.0D;
/* 128 */       max[i] = Math.round(max[i] * 100.0D) / 100.0D;
/* 129 */       avg[i] = Math.round(avg[i] * 100.0D) / 100.0D;
/* 130 */       stddev[i] = Math.round(stddev[i] * 100.0D) / 100.0D;
/*     */     } 
/*     */     
/* 133 */     sb.append(prefix + "Min: " + Arrays.toString(min) + "\n");
/* 134 */     sb.append(prefix + "Max: " + Arrays.toString(max) + "\n");
/* 135 */     sb.append(prefix + "Avg: " + Arrays.toString(avg) + "\n");
/* 136 */     sb.append(prefix + "StdDev: " + Arrays.toString(stddev) + "\n");
/* 137 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public ArrayList toArrayList() {
/* 141 */     ArrayList array = new ArrayList();
/* 142 */     addTo(array);
/* 143 */     return array;
/*     */   }
/*     */   
/*     */   public void addTo(ArrayList<DataTuple> array) {
/* 147 */     for (int i = 0; i < getNbRows(); i++) {
/* 148 */       array.add(getTuple(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public DataTuple createTuple() {
/* 153 */     return new DataTuple(this.m_Schema);
/*     */   }
/*     */   
/*     */   public static RowData readData(String fname, ClusSchema schema) throws ClusException, IOException {
/* 157 */     schema.addIndices(0);
/* 158 */     ClusReader reader = new ClusReader(fname, schema.getSettings());
/* 159 */     RowData data = schema.createNormalView().readData(reader, schema);
/* 160 */     reader.close();
/* 161 */     return data;
/*     */   }
/*     */   
/*     */   public ClusData cloneData() {
/* 165 */     RowData res = new RowData(this.m_Schema, this.m_NbRows);
/* 166 */     System.arraycopy(this.m_Data, 0, res.m_Data, 0, this.m_NbRows);
/* 167 */     return res;
/*     */   }
/*     */   
/*     */   public RowData shallowCloneData() {
/* 171 */     RowData res = new RowData(this.m_Schema, this.m_NbRows);
/* 172 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 173 */       res.setTuple(this.m_Data[i].cloneTuple(), i);
/*     */     }
/* 175 */     return res;
/*     */   }
/*     */   
/*     */   public ClusData deepCloneData() {
/* 179 */     RowData res = new RowData(this.m_Schema, this.m_NbRows);
/* 180 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 181 */       res.setTuple(this.m_Data[i].deepCloneTuple(), i);
/*     */     }
/* 183 */     return res;
/*     */   }
/*     */   
/*     */   public final ClusSchema getSchema() {
/* 187 */     return this.m_Schema;
/*     */   }
/*     */   
/*     */   public void setSchema(ClusSchema schema) {
/* 191 */     this.m_Schema = schema;
/*     */   }
/*     */   
/*     */   public void sortSparse(NumericAttrType at, RowDataSortHelper helper) {
/* 195 */     int nbmiss = 0, nbzero = 0, nbother = 0;
/* 196 */     helper.resize(this.m_NbRows + 1);
/* 197 */     DataTuple[] missing = helper.missing;
/* 198 */     DataTuple[] zero = helper.zero;
/* 199 */     DoubleObject[] other = helper.other;
/* 200 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 201 */       double data = at.getNumeric(this.m_Data[i]);
/* 202 */       if (data == 0.0D) {
/* 203 */         zero[nbzero++] = this.m_Data[i];
/* 204 */       } else if (data == Double.POSITIVE_INFINITY) {
/* 205 */         missing[nbmiss++] = this.m_Data[i];
/* 206 */       } else if (data > 0.0D) {
/* 207 */         other[nbother++].set(data, this.m_Data[i]);
/*     */       } else {
/* 209 */         System.err.println("Sparse attribute has negative value!");
/* 210 */         System.exit(-1);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 215 */     MSorter.quickSort(helper, 0, nbother);
/*     */ 
/*     */     
/* 218 */     int pos = 0; int j;
/* 219 */     for (j = 0; j < nbmiss; j++) {
/* 220 */       this.m_Data[pos++] = missing[j];
/*     */     }
/* 222 */     for (j = 0; j < nbother; j++) {
/* 223 */       this.m_Data[pos++] = (DataTuple)other[j].getObject();
/*     */     }
/* 225 */     for (j = 0; j < nbzero; j++) {
/* 226 */       this.m_Data[pos++] = zero[j];
/*     */     }
/*     */   }
/*     */   
/*     */   public void sort(NumericAttrType at) {
/* 231 */     this.m_Index = at.getArrayIndex();
/* 232 */     MSorter.quickSort(this, 0, this.m_NbRows);
/*     */   }
/*     */   
/*     */   public double getDouble(int i) {
/* 236 */     return this.m_Data[i].getDoubleVal(this.m_Index);
/*     */   }
/*     */   
/*     */   public boolean compare(int i, int j) {
/* 240 */     return (this.m_Data[i].getDoubleVal(this.m_Index) < this.m_Data[j].getDoubleVal(this.m_Index));
/*     */   }
/*     */   
/*     */   public void swap(int i, int j) {
/* 244 */     DataTuple temp = this.m_Data[i];
/* 245 */     this.m_Data[i] = this.m_Data[j];
/* 246 */     this.m_Data[j] = temp;
/*     */   }
/*     */   
/*     */   public DataTuple findTupleByKey(String key_value) {
/* 250 */     ClusAttrType[] key = getSchema().getAllAttrUse(4);
/* 251 */     if (key.length > 0) {
/* 252 */       ClusAttrType key_attr = key[0];
/* 253 */       for (int i = 0; i < getNbRows(); i++) {
/* 254 */         DataTuple tuple = getTuple(i);
/* 255 */         if (key_attr.getString(tuple).equals(key_value)) {
/* 256 */           return tuple;
/*     */         }
/*     */       } 
/*     */     } 
/* 260 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusData selectFrom(ClusSelection sel) {
/* 265 */     int nbsel = sel.getNbSelected();
/* 266 */     RowData res = new RowData(this.m_Schema, nbsel);
/* 267 */     if (sel.supportsReplacement()) {
/* 268 */       for (int i = 0; i < nbsel; i++) {
/* 269 */         res.setTuple(this.m_Data[sel.getIndex(i)], i);
/*     */       }
/*     */     } else {
/* 272 */       int s_subset = 0;
/* 273 */       for (int i = 0; i < this.m_NbRows; i++) {
/* 274 */         if (sel.isSelected(i)) {
/* 275 */           res.setTuple(this.m_Data[i], s_subset++);
/*     */         }
/*     */       } 
/*     */     } 
/* 279 */     return res;
/*     */   }
/*     */   
/*     */   public ClusData select(ClusSelection sel) {
/* 283 */     int s_data = 0;
/* 284 */     int s_subset = 0;
/* 285 */     DataTuple[] old = this.m_Data;
/* 286 */     int nbsel = sel.getNbSelected();
/* 287 */     this.m_Data = new DataTuple[this.m_NbRows - nbsel];
/* 288 */     RowData res = new RowData(this.m_Schema, nbsel);
/* 289 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 290 */       if (sel.isSelected(i)) {
/* 291 */         res.setTuple(old[i], s_subset++);
/*     */       } else {
/* 293 */         setTuple(old[i], s_data++);
/*     */       } 
/*     */     } 
/* 296 */     this.m_NbRows -= nbsel;
/* 297 */     return res;
/*     */   }
/*     */   
/*     */   public void update(ClusSelection sel) {
/* 301 */     int s_data = 0;
/* 302 */     DataTuple[] old = this.m_Data;
/* 303 */     int nbsel = sel.getNbSelected();
/* 304 */     this.m_Data = new DataTuple[nbsel];
/* 305 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 306 */       if (sel.isSelected(i)) {
/* 307 */         DataTuple nt = old[i].multiplyWeight(sel.getWeight(i));
/* 308 */         setTuple(nt, s_data++);
/*     */       } 
/*     */     } 
/* 311 */     this.m_NbRows = nbsel;
/*     */   }
/*     */   
/*     */   public final double getSumWeights() {
/* 315 */     double sum = 0.0D;
/* 316 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 317 */       sum += this.m_Data[i].getWeight();
/*     */     }
/* 319 */     return sum;
/*     */   }
/*     */   
/*     */   public final boolean containsFold(DataTuple tuple, int[] folds) {
/* 323 */     for (int i = 0; i < folds.length; i++) {
/* 324 */       if (tuple.m_Folds[folds[i]] > 0) {
/* 325 */         return true;
/*     */       }
/*     */     } 
/* 328 */     return false;
/*     */   }
/*     */   
/*     */   public final void optimize2(int[] folds) {
/* 332 */     int nbsel = 0;
/* 333 */     int s_data = 0;
/* 334 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 335 */       if (containsFold(this.m_Data[i], folds)) {
/* 336 */         nbsel++;
/*     */       }
/*     */     } 
/* 339 */     DataTuple[] old = this.m_Data;
/* 340 */     this.m_Data = new DataTuple[nbsel];
/* 341 */     for (int j = 0; j < this.m_NbRows; j++) {
/* 342 */       if (containsFold(old[j], folds)) {
/* 343 */         setTuple(old[j], s_data++);
/*     */       }
/*     */     } 
/* 346 */     this.m_NbRows = nbsel;
/*     */   }
/*     */   
/*     */   public void insert(ClusData data, ClusSelection sel) {
/* 350 */     int s_data = 0;
/* 351 */     int s_subset = 0;
/* 352 */     DataTuple[] old = this.m_Data;
/* 353 */     RowData other = (RowData)data;
/* 354 */     resizeEmpty(this.m_NbRows + sel.getNbSelected());
/* 355 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 356 */       if (sel.isSelected(i)) {
/* 357 */         setTuple(other.getTuple(s_subset++), i);
/*     */       } else {
/* 359 */         setTuple(old[s_data++], i);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public final RowData getFoldData(int fold) {
/* 365 */     int idx = 0;
/* 366 */     int nbsel = 0;
/*     */     
/* 368 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 369 */       if (this.m_Data[i].getIndex() != fold) {
/* 370 */         nbsel++;
/*     */       }
/*     */     } 
/*     */     
/* 374 */     RowData res = new RowData(this.m_Schema, nbsel);
/* 375 */     for (int j = 0; j < this.m_NbRows; j++) {
/* 376 */       DataTuple tuple = this.m_Data[j];
/* 377 */       if (tuple.getIndex() != fold) {
/* 378 */         res.setTuple(tuple, idx++);
/*     */       }
/*     */     } 
/* 381 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final RowData getFoldData2(int fold) {
/* 391 */     int idx = 0;
/* 392 */     int nbsel = 0;
/*     */     
/* 394 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 395 */       if ((this.m_Data[i]).m_Folds[fold] != 0) {
/* 396 */         nbsel++;
/*     */       }
/*     */     } 
/*     */     
/* 400 */     RowData res = new RowData(this.m_Schema, nbsel);
/* 401 */     for (int j = 0; j < this.m_NbRows; j++) {
/* 402 */       DataTuple tuple = this.m_Data[j];
/* 403 */       int factor = (this.m_Data[j]).m_Folds[fold];
/* 404 */       if (factor != 0) {
/* 405 */         DataTuple t2 = (factor == 1) ? tuple : tuple.changeWeight(tuple.getWeight() * factor);
/* 406 */         res.setTuple(t2, idx++);
/*     */       } 
/*     */     } 
/* 409 */     return res;
/*     */   }
/*     */   
/*     */   public final RowData getOVFoldData(int fold) {
/* 413 */     int idx = 0;
/* 414 */     int nbsel = 0;
/*     */     
/* 416 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 417 */       int efold = (this.m_Data[i]).m_Index;
/* 418 */       if (efold != -1) {
/* 419 */         if (efold != fold) {
/* 420 */           nbsel++;
/*     */         }
/* 422 */       } else if (Arrays.binarySearch((this.m_Data[i]).m_Folds, fold) >= 0) {
/* 423 */         nbsel++;
/*     */       } 
/*     */     } 
/*     */     
/* 427 */     RowData res = new RowData(this.m_Schema, nbsel);
/* 428 */     for (int j = 0; j < this.m_NbRows; j++) {
/* 429 */       DataTuple tuple = this.m_Data[j];
/* 430 */       int efold = tuple.m_Index;
/* 431 */       if (efold != -1) {
/* 432 */         if (efold != fold) {
/* 433 */           res.setTuple(tuple, idx++);
/*     */         }
/* 435 */       } else if (Arrays.binarySearch((this.m_Data[j]).m_Folds, fold) >= 0) {
/* 436 */         res.setTuple(tuple, idx++);
/*     */       } 
/*     */     } 
/* 439 */     return res;
/*     */   }
/*     */   
/*     */   public final boolean checkData() {
/* 443 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 444 */       DataTuple tuple = this.m_Data[i];
/* 445 */       if (tuple.m_Index == -1 && tuple.m_Folds == null) {
/* 446 */         return false;
/*     */       }
/* 448 */       if (tuple.m_Index != -1 && tuple.m_Folds != null) {
/* 449 */         return false;
/*     */       }
/*     */     } 
/* 452 */     return true;
/*     */   }
/*     */   
/*     */   public final DataTuple getTuple(int i) {
/* 456 */     return this.m_Data[i];
/*     */   }
/*     */   
/*     */   public final DataTuple getTupleByActiveIndex(int index) {
/* 460 */     int nbRows = getNbRows();
/* 461 */     for (int i = 0; i < nbRows; i++) {
/* 462 */       if ((this.m_Data[i]).m_ActiveIndex == index) {
/* 463 */         return this.m_Data[i];
/*     */       }
/*     */     } 
/* 466 */     return null;
/*     */   }
/*     */   
/*     */   public final void setTuple(DataTuple tuple, int i) {
/* 470 */     this.m_Data[i] = tuple;
/*     */   }
/*     */   
/*     */   public final RowData applyWeighted(NodeTest test, int branch) {
/* 474 */     int nb = 0;
/* 475 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 476 */       int pred = test.predictWeighted(this.m_Data[i]);
/* 477 */       if (pred == branch || pred == -1) {
/* 478 */         nb++;
/*     */       }
/*     */     } 
/* 481 */     int idx = 0;
/*     */     
/* 483 */     RowData res = new RowData(this.m_Schema, nb);
/* 484 */     double prop = test.getProportion(branch);
/*     */ 
/*     */ 
/*     */     
/* 488 */     for (int j = 0; j < this.m_NbRows; j++) {
/* 489 */       DataTuple tuple = this.m_Data[j];
/*     */       
/* 491 */       int pred = test.predictWeighted(tuple);
/* 492 */       if (pred == branch) {
/* 493 */         res.setTuple(tuple, idx++);
/* 494 */       } else if (pred == -1) {
/*     */ 
/*     */         
/* 497 */         DataTuple ntuple = tuple.multiplyWeight(prop);
/*     */         
/* 499 */         res.setTuple(ntuple, idx++);
/*     */       } 
/*     */     } 
/*     */     
/* 503 */     return res;
/*     */   }
/*     */   
/*     */   public final RowData apply(NodeTest test, int branch) {
/* 507 */     int nb = 0;
/* 508 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 509 */       int pred = test.predictWeighted(this.m_Data[i]);
/* 510 */       if (pred == branch) {
/* 511 */         nb++;
/*     */       }
/*     */     } 
/* 514 */     int idx = 0;
/* 515 */     RowData res = new RowData(this.m_Schema, nb);
/* 516 */     for (int j = 0; j < this.m_NbRows; j++) {
/* 517 */       DataTuple tuple = this.m_Data[j];
/* 518 */       int pred = test.predictWeighted(tuple);
/* 519 */       if (pred == branch) {
/* 520 */         res.setTuple(tuple, idx++);
/*     */       }
/*     */     } 
/* 523 */     return res;
/*     */   }
/*     */   
/*     */   public final RowData applyConstraint(ClusRuleConstraintInduceTest test, int branch) {
/* 527 */     boolean order = test.isSmallerThanTest();
/* 528 */     if (order) {
/* 529 */       return applyConstraintTrue(test, branch);
/*     */     }
/* 531 */     return applyConstraintFalse(test, branch);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private RowData applyConstraintTrue(ClusRuleConstraintInduceTest test, int branch) {
/* 537 */     int nb = 0;
/* 538 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 539 */       int pred = test.predictWeighted(this.m_Data[i]);
/* 540 */       if (pred != branch) {
/* 541 */         nb++;
/*     */       }
/*     */     } 
/* 544 */     int idx = 0;
/* 545 */     RowData res = new RowData(this.m_Schema, nb);
/* 546 */     for (int j = 0; j < this.m_NbRows; j++) {
/* 547 */       DataTuple tuple = this.m_Data[j];
/* 548 */       int pred = test.predictWeighted(tuple);
/* 549 */       if (pred != branch) {
/* 550 */         res.setTuple(tuple, idx++);
/*     */       }
/*     */     } 
/* 553 */     return res;
/*     */   }
/*     */   
/*     */   private RowData applyConstraintFalse(ClusRuleConstraintInduceTest test, int branch) {
/* 557 */     int nb = 0;
/* 558 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 559 */       int pred = test.predictWeighted(this.m_Data[i]);
/* 560 */       if (pred == branch) {
/* 561 */         nb++;
/*     */       }
/*     */     } 
/* 564 */     int idx = 0;
/* 565 */     RowData res = new RowData(this.m_Schema, nb);
/* 566 */     for (int j = 0; j < this.m_NbRows; j++) {
/* 567 */       DataTuple tuple = this.m_Data[j];
/* 568 */       int pred = test.predictWeighted(tuple);
/* 569 */       if (pred == branch) {
/* 570 */         res.setTuple(tuple, idx++);
/*     */       }
/*     */     } 
/* 573 */     return res;
/*     */   }
/*     */   
/*     */   public final RowData applySoft(SoftTest test, int branch) {
/* 577 */     int nb = 0;
/* 578 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 579 */       nb += test.softPredictNb(this.m_Data[i], branch);
/*     */     }
/* 581 */     int idx = 0;
/* 582 */     RowData res = new RowData(this.m_Schema, nb);
/* 583 */     for (int j = 0; j < this.m_NbRows; j++) {
/* 584 */       idx = test.softPredict(res, this.m_Data[j], idx, branch);
/*     */     }
/* 586 */     return res;
/*     */   }
/*     */   
/*     */   public final RowData applySoft2(SoftTest test, int branch) {
/* 590 */     int nb = 0;
/* 591 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 592 */       nb += test.softPredictNb2(this.m_Data[i], branch);
/*     */     }
/* 594 */     int idx = 0;
/* 595 */     RowData res = new RowData(this.m_Schema, nb);
/* 596 */     for (int j = 0; j < this.m_NbRows; j++) {
/* 597 */       idx = test.softPredict2(res, this.m_Data[j], idx, branch);
/*     */     }
/* 599 */     return res;
/*     */   }
/*     */   
/*     */   public void resize(int nbrows) {
/* 603 */     this.m_Data = new DataTuple[nbrows];
/* 604 */     for (int i = 0; i < nbrows; i++) {
/* 605 */       this.m_Data[i] = new DataTuple(this.m_Schema);
/*     */     }
/* 607 */     this.m_NbRows = nbrows;
/*     */   }
/*     */   
/*     */   public void resizeEmpty(int nbrows) {
/* 611 */     this.m_Data = new DataTuple[nbrows];
/* 612 */     this.m_NbRows = nbrows;
/*     */   }
/*     */   
/*     */   public void showDebug(ClusSchema schema) {
/* 616 */     System.out.println("Data: " + this.m_NbRows + " Size: " + this.m_Data.length);
/* 617 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 618 */       DataTuple tuple = getTuple(i);
/* 619 */       if (tuple == null) {
/* 620 */         System.out.println("? ");
/*     */       } else {
/* 622 */         ClusAttrType at = schema.getAttrType(0);
/* 623 */         System.out.println(at.getString(tuple));
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 631 */     System.out.println();
/*     */   }
/*     */ 
/*     */   
/*     */   public void attach(ClusNode node) {}
/*     */   
/*     */   public void calcTotalStatBitVector(ClusStatistic stat) {
/* 638 */     stat.setSDataSize(getNbRows());
/* 639 */     calcTotalStat(stat);
/* 640 */     stat.optimizePreCalc(this);
/*     */   }
/*     */   
/*     */   public void calcTotalStat(ClusStatistic stat) {
/* 644 */     for (int i = 0; i < this.m_NbRows; i++)
/*     */     {
/* 646 */       stat.updateWeighted(this.m_Data[i], i);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void calcPosAndMissStat(NodeTest test, int branch, ClusStatistic pos, ClusStatistic miss) {
/* 654 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 655 */       DataTuple tuple = this.m_Data[i];
/* 656 */       int pred = test.predictWeighted(tuple);
/* 657 */       if (pred == branch) {
/* 658 */         pos.updateWeighted(this.m_Data[i], i);
/* 659 */       } else if (pred == -1) {
/* 660 */         miss.updateWeighted(this.m_Data[i], i);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public final boolean isSoft() {
/* 666 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 667 */       DataTuple tuple = this.m_Data[i];
/* 668 */       if (tuple.m_Index == -1) {
/* 669 */         return true;
/*     */       }
/*     */     } 
/* 672 */     return false;
/*     */   }
/*     */   
/*     */   public final void calcXValTotalStat(ClusStatistic[] tot) {
/* 676 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 677 */       DataTuple tuple = this.m_Data[i];
/* 678 */       tot[tuple.getIndex()].updateWeighted(tuple, i);
/*     */     } 
/*     */   }
/*     */   public final void calcXValTotalStat(ClusStatistic[] tot, ClusStatistic[] extra) {
/*     */     int i;
/* 683 */     for (i = 0; i < extra.length; i++) {
/* 684 */       extra[i].reset();
/*     */     }
/* 686 */     for (i = 0; i < this.m_NbRows; i++) {
/* 687 */       DataTuple tuple = this.m_Data[i];
/* 688 */       if (tuple.m_Index != -1) {
/* 689 */         tot[tuple.m_Index].updateWeighted(tuple, i);
/*     */       } else {
/* 691 */         int[] folds = tuple.m_Folds;
/* 692 */         for (int j = 0; j < folds.length; j++) {
/* 693 */           extra[folds[j]].updateWeighted(tuple, i);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void calcError(ClusNode node, ClusErrorList par) {
/* 700 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 701 */       DataTuple tuple = getTuple(i);
/* 702 */       ClusStatistic stat = node.predictWeighted(tuple);
/* 703 */       par.addExample(tuple, stat);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void preprocess(int pass, DataPreprocs pps) throws ClusException {
/* 708 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 709 */       DataTuple tuple = this.m_Data[i];
/* 710 */       pps.preproc(pass, tuple);
/*     */     } 
/*     */   }
/*     */   public final void showTable() {
/*     */     int i;
/* 715 */     for (i = 0; i < this.m_Schema.getNbAttributes(); i++) {
/* 716 */       ClusAttrType type = this.m_Schema.getAttrType(i);
/* 717 */       if (i != 0) {
/* 718 */         System.out.print(",");
/*     */       }
/* 720 */       System.out.print(type.getName());
/*     */     } 
/* 722 */     System.out.println();
/* 723 */     for (i = 0; i < this.m_NbRows; i++) {
/* 724 */       DataTuple tuple = getTuple(i);
/* 725 */       for (int j = 0; j < this.m_Schema.getNbAttributes(); j++) {
/* 726 */         ClusAttrType type = this.m_Schema.getAttrType(j);
/* 727 */         if (j != 0) {
/* 728 */           System.out.print(",");
/*     */         }
/* 730 */         System.out.print(type.getString(tuple));
/*     */       } 
/* 732 */       System.out.println();
/*     */     } 
/*     */   }
/*     */   
/*     */   public double[] getNumeric(int idx) {
/* 737 */     return (this.m_Data[idx]).m_Doubles;
/*     */   }
/*     */   
/*     */   public int[] getNominal(int idx) {
/* 741 */     return (this.m_Data[idx]).m_Ints;
/*     */   }
/*     */   
/*     */   public MemoryTupleIterator getIterator() {
/* 745 */     return new MemoryTupleIterator(this);
/*     */   }
/*     */   
/*     */   public void addIndices() {
/* 749 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 750 */       this.m_Data[i].setIndex(i);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(RowData d) {
/* 760 */     if (d.getNbRows() != getNbRows()) {
/* 761 */       return false;
/*     */     }
/* 763 */     if (!this.m_Schema.equals(this.m_Schema)) {
/* 764 */       return false;
/*     */     }
/* 766 */     for (int i = 0; i < getNbRows(); i++) {
/* 767 */       if (!d.getTuple(i).equals(getTuple(i))) {
/* 768 */         return false;
/*     */       }
/*     */     } 
/* 771 */     return false;
/*     */   }
/*     */   
/*     */   public void addDataTuples(RowData rowData) {
/* 775 */     for (int i = 0; i < rowData.getNbRows(); i++) {
/* 776 */       add(rowData.getTuple(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public void addOrReplace(RowData rowData) {
/* 781 */     for (int i = 0; i < rowData.getNbRows(); i++) {
/* 782 */       DataTuple newTuple = rowData.getTuple(i);
/* 783 */       int indexToUpdate = findIndexOfQueriedBeforeTuple(newTuple.m_ActiveIndex);
/*     */       
/* 785 */       if (indexToUpdate != -1) {
/*     */         
/* 787 */         setTuple(newTuple, indexToUpdate);
/*     */       } else {
/*     */         
/* 790 */         addActive(rowData.getTuple(i));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public int findIndexOfQueriedBeforeTuple(int index) {
/* 796 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 797 */       if (index == (getTuple(i)).m_ActiveIndex) {
/* 798 */         return i;
/*     */       }
/*     */     } 
/* 801 */     return -1;
/*     */   }
/*     */   public void add(DataTuple tuple) {
/*     */     DataTuple[] newdata;
/* 805 */     setNbRows(getNbRows() + 1);
/*     */     
/* 807 */     if (this.m_Data != null) {
/* 808 */       newdata = Arrays.<DataTuple>copyOf(this.m_Data, getNbRows());
/*     */     } else {
/* 810 */       newdata = new DataTuple[getNbRows()];
/*     */     } 
/* 812 */     newdata[getNbRows() - 1] = tuple.cloneTuple();
/* 813 */     this.m_Data = newdata;
/*     */   }
/*     */   public void addActive(DataTuple tuple) {
/*     */     DataTuple[] newdata;
/* 817 */     setNbRows(getNbRows() + 1);
/*     */     
/* 819 */     if (this.m_Data != null) {
/* 820 */       newdata = Arrays.<DataTuple>copyOf(this.m_Data, getNbRows());
/*     */     } else {
/* 822 */       newdata = new DataTuple[getNbRows()];
/*     */     } 
/* 824 */     newdata[getNbRows() - 1] = tuple.deepActiveCloneTuple();
/* 825 */     this.m_Data = newdata;
/*     */   }
/*     */   
/*     */   public void addAll(RowData data1, RowData data2) {
/* 829 */     int size = data1.getNbRows() + data2.getNbRows();
/* 830 */     setNbRows(size);
/* 831 */     this.m_Data = new DataTuple[size]; int i;
/* 832 */     for (i = 0; i < data1.getNbRows(); i++) {
/* 833 */       this.m_Data[i] = data1.getTuple(i).cloneTuple();
/*     */     }
/* 835 */     for (i = 0; i < data2.getNbRows(); i++) {
/* 836 */       data2.getTuple(i).cloneTuple();
/* 837 */       this.m_Data[i + data1.getNbRows()] = data2.getTuple(i).cloneTuple();
/*     */     } 
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
/*     */   public RowData sample(int N) {
/* 852 */     if (N < 0) {
/* 853 */       throw new IllegalArgumentException("N should be larger than or equal to zero");
/*     */     }
/* 855 */     int nbRows = getNbRows();
/* 856 */     if (N == 0) {
/* 857 */       return new RowData(this);
/*     */     }
/* 859 */     ArrayList<DataTuple> res = new ArrayList<>();
/*     */ 
/*     */     
/* 862 */     for (int size = 0; size < N; size++) {
/* 863 */       int i = ClusRandom.nextInt(6, nbRows);
/* 864 */       res.add(getTuple(i));
/*     */     } 
/* 866 */     return new RowData(res, getSchema().cloneSchema());
/*     */   }
/*     */   
/*     */   public double getSum(double[] vector) {
/* 870 */     double sum = 0.0D;
/* 871 */     for (int i = 0; i < vector.length; ) { sum += vector[i]; i++; }
/* 872 */      return sum;
/*     */   }
/*     */   
/*     */   public void removeRows(HashSet<Integer> indexes) {
/* 876 */     ArrayList<DataTuple> newData = new ArrayList<>();
/*     */     
/* 878 */     for (int i = 0; i < getNbRows(); i++) {
/* 879 */       DataTuple dataTuple = this.m_Data[i];
/* 880 */       if (!indexes.contains(Integer.valueOf((this.m_Data[i]).m_ActiveIndex))) {
/* 881 */         newData.add(this.m_Data[i].deepActiveCloneTuple());
/*     */       }
/*     */     } 
/* 884 */     setFromList(newData);
/*     */   }
/*     */   
/*     */   public void removeRowsWithoutPartial(HashSet<Integer> indexes) {
/* 888 */     ArrayList<DataTuple> newData = new ArrayList<>();
/*     */     
/* 890 */     for (int i = 0; i < getNbRows(); i++) {
/* 891 */       DataTuple dataTuple = this.m_Data[i];
/* 892 */       if (!indexes.contains(Integer.valueOf(i))) {
/* 893 */         newData.add(dataTuple.deepActiveCloneTuple());
/*     */       }
/*     */     } 
/* 896 */     setFromList(newData);
/*     */   }
/*     */   
/*     */   public void setFromList(ArrayList<DataTuple> list) {
/* 900 */     this.m_Data = new DataTuple[list.size()];
/* 901 */     for (int i = 0; i < list.size(); i++) {
/* 902 */       this.m_Data[i] = list.get(i);
/*     */     }
/* 904 */     setNbRows(list.size());
/*     */   }
/*     */   
/*     */   public RowData sampleWeighted(Random random) {
/* 908 */     return sampleWeighted(random, getNbRows());
/*     */   }
/*     */   
/*     */   public RowData sampleWeighted(Random random, int N) {
/* 912 */     double[] weight_acc = new double[getNbRows()];
/* 913 */     weight_acc[0] = getTuple(0).getWeight();
/* 914 */     for (int i = 1; i < getNbRows(); i++) {
/* 915 */       DataTuple tuple = getTuple(i);
/* 916 */       weight_acc[i] = weight_acc[i - 1] + tuple.getWeight();
/*     */     } 
/* 918 */     double tot_w = weight_acc[getNbRows() - 1];
/* 919 */     ArrayList<DataTuple> res = new ArrayList();
/* 920 */     for (int j = 0; j < N; j++) {
/* 921 */       double rnd = random.nextDouble() * tot_w;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 926 */       int loc = Arrays.binarySearch(weight_acc, rnd);
/* 927 */       if (loc < 0) {
/* 928 */         loc = -loc - 1;
/*     */       }
/* 930 */       DataTuple restuple = getTuple(loc).changeWeight(1.0D);
/* 931 */       res.add(restuple);
/*     */     } 
/* 933 */     return new RowData(res, getSchema());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\rows\RowData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */