/*      */ package clus.data.type;
/*      */ 
/*      */ import clus.data.ClusData;
/*      */ import clus.data.io.ClusView;
/*      */ import clus.data.rows.DataPreprocs;
/*      */ import clus.data.rows.DataTuple;
/*      */ import clus.data.rows.SparseDataTuple;
/*      */ import clus.io.ClusSerializable;
/*      */ import clus.io.DummySerializable;
/*      */ import clus.main.Settings;
/*      */ import clus.model.ClusModel;
/*      */ import clus.selection.XValDataSelection;
/*      */ import clus.selection.XValMainSelection;
/*      */ import clus.selection.XValRandomSelection;
/*      */ import clus.util.ClusException;
/*      */ import clus.util.ClusFormat;
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.StringTokenizer;
/*      */ import jeans.util.IntervalCollection;
/*      */ import jeans.util.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ClusSchema
/*      */   implements Serializable
/*      */ {
/*      */   public static final long serialVersionUID = 1L;
/*      */   public static final int ROWS = 0;
/*      */   public static final int COLS = 1;
/*      */   protected boolean m_IsSparse;
/*      */   protected String m_Relation;
/*      */   protected int m_NbAttrs;
/*      */   protected int m_ActiveNbAttrs;
/*      */   protected int m_NbInts;
/*      */   protected int m_NbDoubles;
/*      */   protected int m_NbObjects;
/*      */   private int m_NbSparse;
/*   52 */   protected ArrayList m_Attr = new ArrayList();
/*      */   
/*      */   protected ClusAttrType[][] m_AllAttrUse;
/*      */   
/*      */   protected NominalAttrType[][] m_NominalAttrUse;
/*      */   protected NumericAttrType[][] m_NumericAttrUse;
/*      */   protected TimeSeriesAttrType[][] m_TimeSeriesAttrUse;
/*      */   protected ClusAttrType[] m_NonSparse;
/*      */   protected Settings m_Settings;
/*      */   protected IndexAttrType m_TSAttr;
/*   62 */   protected IntervalCollection m_Target = IntervalCollection.EMPTY;
/*   63 */   protected IntervalCollection m_Disabled = IntervalCollection.EMPTY;
/*   64 */   protected IntervalCollection m_Clustering = IntervalCollection.EMPTY;
/*   65 */   protected IntervalCollection m_Descriptive = IntervalCollection.EMPTY;
/*   66 */   protected IntervalCollection m_Key = IntervalCollection.EMPTY;
/*      */ 
/*      */ 
/*      */   
/*      */   protected int[] m_NbVt;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusSchema(String name) {
/*   76 */     this.m_Relation = name;
/*      */   }
/*      */   
/*      */   public ClusSchema(String name, String descr) {
/*   80 */     this.m_Relation = name;
/*   81 */     addFromString(descr);
/*      */   }
/*      */   
/*      */   public void setSettings(Settings sett) {
/*   85 */     this.m_Settings = sett;
/*      */   }
/*      */ 
/*      */   
/*      */   public void initializeSettings(Settings sett) throws ClusException, IOException {
/*   90 */     setSettings(sett);
/*   91 */     setTestSet(-1);
/*      */     
/*   93 */     setTarget(new IntervalCollection(sett.getTarget()));
/*   94 */     setDisabled(new IntervalCollection(sett.getDisabled()));
/*   95 */     setClustering(new IntervalCollection(sett.getClustering()));
/*      */     
/*   97 */     setDescriptive(new IntervalCollection(sett.getDescriptive()));
/*   98 */     setKey(new IntervalCollection(sett.getKey()));
/*   99 */     updateAttributeUse();
/*  100 */     addIndices(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void initializeActiveSettings(Settings sett) throws ClusException, IOException {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void initialize() throws ClusException, IOException {
/*  130 */     updateAttributeUse();
/*  131 */     addIndices(0);
/*      */   }
/*      */   
/*      */   public Settings getSettings() {
/*  135 */     return this.m_Settings;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final String getRelationName() {
/*  142 */     return this.m_Relation;
/*      */   }
/*      */   
/*      */   public final void setRelationName(String name) {
/*  146 */     this.m_Relation = name;
/*      */   }
/*      */   
/*      */   public ClusSchema cloneSchema() {
/*  150 */     ClusSchema result = new ClusSchema(getRelationName());
/*  151 */     result.setSettings(getSettings());
/*  152 */     for (int i = 0; i < getNbAttributes(); i++) {
/*  153 */       ClusAttrType attr = getAttrType(i);
/*  154 */       ClusAttrType copy = attr.cloneType();
/*  155 */       result.addAttrType(copy);
/*      */     } 
/*  157 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int getNbAttributes() {
/*  166 */     return this.m_NbAttrs;
/*      */   }
/*      */   
/*      */   public final int getActiveNbAttributes() {
/*  170 */     return this.m_ActiveNbAttrs;
/*      */   }
/*      */   
/*      */   public final ClusAttrType getAttrType(int idx) {
/*  174 */     return this.m_Attr.get(idx);
/*      */   }
/*      */   
/*      */   public final ClusAttrType getAttrType(String name) {
/*  178 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/*  179 */       ClusAttrType attr = this.m_Attr.get(j);
/*  180 */       if (name.equals(attr.getName())) {
/*  181 */         return attr;
/*      */       }
/*      */     } 
/*  184 */     return null;
/*      */   }
/*      */   
/*      */   public final ClusAttrType[] getAllAttrUse(int attruse) {
/*  188 */     return this.m_AllAttrUse[attruse];
/*      */   }
/*      */   
/*      */   public final int getNbAllAttrUse(int attruse) {
/*  192 */     return (this.m_AllAttrUse[attruse]).length;
/*      */   }
/*      */   
/*      */   public TimeSeriesAttrType[] getTimeSeriesAttrUse(int attruse) {
/*  196 */     return this.m_TimeSeriesAttrUse[attruse];
/*      */   }
/*      */   
/*      */   public final NominalAttrType[] getNominalAttrUse(int attruse) {
/*  200 */     return this.m_NominalAttrUse[attruse];
/*      */   }
/*      */   
/*      */   public final int getNbNominalAttrUse(int attruse) {
/*  204 */     return (this.m_NominalAttrUse[attruse]).length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final NumericAttrType[] getNumericAttrUse(int attruse) {
/*  215 */     return this.m_NumericAttrUse[attruse];
/*      */   }
/*      */   
/*      */   public final int getNbNumericAttrUse(int attruse) {
/*  219 */     return (this.m_NumericAttrUse[attruse]).length;
/*      */   }
/*      */   
/*      */   public final ClusAttrType[] getDescriptiveAttributes() {
/*  223 */     return this.m_AllAttrUse[1];
/*      */   }
/*      */   
/*      */   public final ClusAttrType[] getKeyAttribute() {
/*  227 */     return this.m_AllAttrUse[4];
/*      */   }
/*      */   
/*      */   public final int getNbDescriptiveAttributes() {
/*  231 */     return getNbAllAttrUse(1);
/*      */   }
/*      */   
/*      */   public final ClusAttrType[] getTargetAttributes() {
/*  235 */     return this.m_AllAttrUse[3];
/*      */   }
/*      */   
/*      */   public final int getNbTargetAttributes() {
/*  239 */     return getNbAllAttrUse(3);
/*      */   }
/*      */   
/*      */   public final int getNbNominalDescriptiveAttributes() {
/*  243 */     return getNbNominalAttrUse(1);
/*      */   }
/*      */   
/*      */   public final int getNbNumericDescriptiveAttributes() {
/*  247 */     return getNbNumericAttrUse(1);
/*      */   }
/*      */   
/*      */   public final int getNbNominalTargetAttributes() {
/*  251 */     return getNbNominalAttrUse(3);
/*      */   }
/*      */   
/*      */   public final int getNbNumericTargetAttributes() {
/*  255 */     return getNbNumericAttrUse(3);
/*      */   }
/*      */   
/*      */   public final int getNbInts() {
/*  259 */     return this.m_NbInts;
/*      */   }
/*      */   
/*      */   public final int getNbDoubles() {
/*  263 */     return this.m_NbDoubles;
/*      */   }
/*      */   
/*      */   public final int getNbObjects() {
/*  267 */     return this.m_NbObjects;
/*      */   }
/*      */   
/*      */   public final boolean hasAttributeType(int attruse, int attrtype) {
/*  271 */     ClusAttrType[] all = getAllAttrUse(attruse);
/*  272 */     for (int i = 0; i < all.length; i++) {
/*  273 */       if (all[i].getTypeIndex() == attrtype) {
/*  274 */         return true;
/*      */       }
/*      */     } 
/*  277 */     return false;
/*      */   }
/*      */   
/*      */   public ClusAttrType getLastNonDisabledType() {
/*  281 */     int nb = getNbAttributes() - 1;
/*  282 */     while (nb >= 0 && getAttrType(nb).isDisabled()) {
/*  283 */       nb--;
/*      */     }
/*  285 */     if (nb >= 0) {
/*  286 */       return getAttrType(nb);
/*      */     }
/*  288 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void addAttrType(ClusAttrType attr) {
/*  298 */     this.m_Attr.add(attr);
/*  299 */     attr.setIndex(this.m_NbAttrs++);
/*  300 */     attr.setSchema(this);
/*      */   }
/*      */   
/*      */   public final void setAttrType(ClusAttrType attr, int idx) {
/*  304 */     this.m_Attr.set(idx, attr);
/*  305 */     attr.setIndex(idx);
/*  306 */     attr.setSchema(this);
/*      */   }
/*      */   
/*      */   public final void addFromString(String descr) {
/*  310 */     StringTokenizer tokens = new StringTokenizer(descr, "[]");
/*  311 */     while (tokens.hasMoreTokens()) {
/*  312 */       String type = tokens.nextToken();
/*  313 */       String name = tokens.hasMoreTokens() ? tokens.nextToken() : "";
/*  314 */       if (type.equals("f")) {
/*  315 */         addAttrType(new NumericAttrType(name));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean hasMissing() {
/*  326 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/*  327 */       ClusAttrType attr = this.m_Attr.get(j);
/*  328 */       if (attr.hasMissing()) {
/*  329 */         return true;
/*      */       }
/*      */     } 
/*  332 */     return false;
/*      */   }
/*      */   
/*      */   public final double getTotalInputNbMissing() {
/*  336 */     int nb_miss = 0;
/*  337 */     ClusAttrType[] attrs = getDescriptiveAttributes();
/*  338 */     for (int j = 0; j < attrs.length; j++) {
/*  339 */       ClusAttrType at = attrs[j];
/*  340 */       nb_miss += at.getNbMissing();
/*      */     } 
/*  342 */     return nb_miss;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final IntervalCollection getTarget() {
/*  453 */     return this.m_Target;
/*      */   }
/*      */   
/*      */   public final IntervalCollection getDisabled() {
/*  457 */     return this.m_Disabled;
/*      */   }
/*      */   
/*      */   public final IntervalCollection getClustering() {
/*  461 */     return this.m_Clustering;
/*      */   }
/*      */   
/*      */   public final IntervalCollection getDescriptive() {
/*  465 */     return this.m_Descriptive;
/*      */   }
/*      */   
/*      */   public final void setTarget(IntervalCollection coll) {
/*  469 */     this.m_Target = coll;
/*      */   }
/*      */   
/*      */   public final void setDisabled(IntervalCollection coll) {
/*  473 */     this.m_Disabled = coll;
/*      */   }
/*      */   
/*      */   public final void setClustering(IntervalCollection coll) {
/*  477 */     this.m_Clustering = coll;
/*      */   }
/*      */   
/*      */   public final void setDescriptive(IntervalCollection coll) {
/*  481 */     this.m_Descriptive = coll;
/*      */   }
/*      */   
/*      */   public final void setKey(IntervalCollection coll) {
/*  485 */     this.m_Key = coll;
/*      */   }
/*      */   
/*      */   public final void updateAttributeUse() throws ClusException, IOException {
/*  489 */     boolean[] keys = new boolean[getNbAttributes() + 1]; int i;
/*  490 */     for (i = 0; i < getNbAttributes(); i++) {
/*  491 */       ClusAttrType type = getAttrType(i);
/*  492 */       if (type.getStatus() == 4) {
/*  493 */         keys[type.getIndex() + 1] = true;
/*      */       }
/*      */     } 
/*      */     
/*  497 */     this.m_Key.add(keys);
/*  498 */     if (this.m_Target.isDefault()) {
/*      */       
/*  500 */       this.m_Target.clear();
/*  501 */       boolean[] bits = new boolean[getNbAttributes() + 1];
/*  502 */       this.m_Disabled.toBits(bits);
/*  503 */       int target = bits.length - 1;
/*  504 */       while (target >= 0 && (bits[target] || keys[target])) {
/*  505 */         target--;
/*      */       }
/*  507 */       if (target > 0) {
/*  508 */         this.m_Target.addInterval(target, target);
/*      */       }
/*      */     } else {
/*      */       
/*  512 */       this.m_Disabled.subtract(this.m_Target);
/*      */     } 
/*  514 */     if (this.m_Clustering.isDefault()) {
/*      */       
/*  516 */       this.m_Clustering.copyFrom(this.m_Target);
/*      */     } else {
/*  518 */       this.m_Disabled.subtract(this.m_Clustering);
/*      */     } 
/*  520 */     if (this.m_Descriptive.isDefault()) {
/*      */       
/*  522 */       this.m_Descriptive.clear();
/*  523 */       this.m_Descriptive.addInterval(1, getNbAttributes());
/*  524 */       this.m_Descriptive.subtract(this.m_Target);
/*  525 */       this.m_Descriptive.subtract(this.m_Disabled);
/*  526 */       this.m_Descriptive.subtract(this.m_Key);
/*      */     } else {
/*  528 */       this.m_Disabled.subtract(this.m_Descriptive);
/*      */     } 
/*      */     
/*  531 */     this.m_Disabled.subtract(this.m_Key);
/*  532 */     checkRange(this.m_Key, "key");
/*  533 */     checkRange(this.m_Disabled, "disabled");
/*  534 */     checkRange(this.m_Target, "target");
/*  535 */     checkRange(this.m_Clustering, "clustering");
/*  536 */     checkRange(this.m_Descriptive, "descriptive");
/*  537 */     setStatusAll(3);
/*  538 */     setStatus(this.m_Disabled, 0, true);
/*  539 */     setStatus(this.m_Target, 1, true);
/*  540 */     setStatus(this.m_Clustering, 2, false);
/*  541 */     setStatus(this.m_Key, 4, true);
/*  542 */     setDescriptiveAll(false);
/*  543 */     setDescriptive(this.m_Descriptive, true);
/*  544 */     setClusteringAll(false);
/*  545 */     setClustering(this.m_Clustering, true);
/*      */     
/*  547 */     for (i = 0; i < getNbAttributes(); i++) {
/*  548 */       ClusAttrType at = getAttrType(i);
/*  549 */       at.initializeBeforeLoadingData();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearAttributeStatusClusteringAndTarget() {
/*  556 */     for (int i = 0; i < getNbAttributes(); i++) {
/*  557 */       ClusAttrType at = getAttrType(i);
/*  558 */       if (at.getStatus() != 0 && at.getStatus() != 4) {
/*  559 */         at.setStatus(3);
/*      */       }
/*      */     } 
/*  562 */     setClusteringAll(false);
/*      */   }
/*      */   
/*      */   public void initDescriptiveAttributes() {
/*  566 */     setDescriptiveAll(false);
/*  567 */     setDescriptive(this.m_Descriptive, true);
/*      */   }
/*      */   
/*      */   public final void checkRange(IntervalCollection coll, String type) throws ClusException {
/*  571 */     if (coll.getMinIndex() < 0) {
/*  572 */       throw new ClusException("Range for " + type + " attributes goes below zero: '" + coll + "'");
/*      */     }
/*  574 */     if (coll.getMaxIndex() > getNbAttributes()) {
/*  575 */       throw new ClusException("Range for " + type + " attributes: '" + coll + "' out of range (there are only " + getNbAttributes() + " attributes)");
/*      */     }
/*      */   }
/*      */   
/*      */   public final void checkActiveRange(IntervalCollection coll, String type) throws ClusException {
/*  580 */     if (coll.getMinIndex() < 0) {
/*  581 */       throw new ClusException("Range for " + type + " attributes goes below zero: '" + coll + "'");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  586 */     if (coll.getMaxIndex() > getActiveNbAttributes()) {
/*  587 */       throw new ClusException("Range for " + type + " attributes: '" + coll + "' out of range (there are only " + getNbAttributes() + " attributes)");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setStatus(IntervalCollection coll, int status, boolean force) {
/*  596 */     coll.reset();
/*  597 */     while (coll.hasMoreInts()) {
/*  598 */       ClusAttrType at = getAttrType(coll.nextInt() - 1);
/*  599 */       if (force || at.getStatus() == 3) {
/*  600 */         at.setStatus(status);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void setActiveStatus(IntervalCollection coll, int status, boolean force) {
/*  606 */     coll.reset();
/*  607 */     while (coll.hasMoreInts()) {
/*  608 */       ClusAttrType at = getAttrType(coll.nextInt() - 1);
/*  609 */       if (force || at.getStatus() == 3) {
/*  610 */         at.setStatus(status);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void setStatusAll(int status) {
/*  616 */     for (int i = 0; i < getNbAttributes(); i++) {
/*  617 */       ClusAttrType at = getAttrType(i);
/*  618 */       at.setStatus(status);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void setActiveStatusAll(int status) {
/*  623 */     for (int i = 0; i < getActiveNbAttributes(); i++) {
/*  624 */       ClusAttrType at = getAttrType(i);
/*  625 */       at.setStatus(status);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void setDescriptive(IntervalCollection coll, boolean descr) {
/*  630 */     coll.reset();
/*  631 */     while (coll.hasMoreInts()) {
/*  632 */       ClusAttrType at = getAttrType(coll.nextInt() - 1);
/*  633 */       at.setDescriptive(descr);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void setActiveDescriptive(IntervalCollection coll, boolean descr) {
/*  638 */     coll.reset();
/*  639 */     while (coll.hasMoreInts()) {
/*  640 */       ClusAttrType at = getAttrType(coll.nextInt() - 1);
/*  641 */       at.setDescriptive(descr);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void setActiveDescriptiveAll(boolean descr) {
/*  646 */     for (int i = 0; i < getActiveNbAttributes(); i++) {
/*  647 */       ClusAttrType at = getAttrType(i);
/*  648 */       at.setDescriptive(descr);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void setDescriptiveAll(boolean descr) {
/*  653 */     for (int i = 0; i < getNbAttributes(); i++) {
/*  654 */       ClusAttrType at = getAttrType(i);
/*  655 */       at.setDescriptive(descr);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void setClustering(IntervalCollection coll, boolean clust) {
/*  660 */     coll.reset();
/*  661 */     while (coll.hasMoreInts()) {
/*  662 */       ClusAttrType at = getAttrType(coll.nextInt() - 1);
/*  663 */       at.setClustering(clust);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void setActiveClustering(IntervalCollection coll, boolean clust) {
/*  668 */     coll.reset();
/*  669 */     while (coll.hasMoreInts()) {
/*  670 */       ClusAttrType at = getAttrType(coll.nextInt() - 1);
/*  671 */       at.setClustering(clust);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void setClusteringAll(boolean clust) {
/*  676 */     for (int i = 0; i < getNbAttributes(); i++) {
/*  677 */       ClusAttrType at = getAttrType(i);
/*  678 */       at.setClustering(clust);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void setActiveClusteringAll(boolean clust) {
/*  683 */     for (int i = 0; i < getActiveNbAttributes(); i++) {
/*  684 */       ClusAttrType at = getAttrType(i);
/*  685 */       at.setClustering(clust);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void addIndices(int type) throws ClusException {
/*  690 */     if (type == 1) {
/*      */ 
/*      */ 
/*      */       
/*  694 */       addColsIndex();
/*      */     } else {
/*  696 */       addRowsIndex();
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void showDebug() {
/*  701 */     System.out.println("Nb ints: " + getNbInts());
/*  702 */     System.out.println("Nb double: " + getNbDoubles());
/*  703 */     System.out.println("Nb obj: " + getNbObjects());
/*  704 */     System.out.println("Idx   Name                          Descr Status    Ref   Type             Sparse Missing");
/*  705 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/*  706 */       ClusAttrType at = this.m_Attr.get(j);
/*  707 */       System.out.print(StringUtils.printInt(j + 1, 6));
/*  708 */       System.out.print(StringUtils.printStrMax(at.getName(), 29));
/*  709 */       if (at.isDescriptive()) {
/*  710 */         System.out.print(" Yes   ");
/*      */       } else {
/*  712 */         System.out.print(" No    ");
/*      */       } 
/*  714 */       switch (at.getStatus()) {
/*      */         case 3:
/*  716 */           System.out.print("          ");
/*      */           break;
/*      */         case 0:
/*  719 */           System.out.print("Disabled  ");
/*      */           break;
/*      */         case 1:
/*  722 */           System.out.print("Target    ");
/*      */           break;
/*      */         case 2:
/*  725 */           System.out.print("Cluster   ");
/*      */           break;
/*      */         case 4:
/*  728 */           System.out.print("Key       ");
/*      */           break;
/*      */         default:
/*  731 */           System.out.print("Error     ");
/*      */           break;
/*      */       } 
/*  734 */       System.out.print(StringUtils.printInt(at.getArrayIndex(), 6));
/*  735 */       System.out.print(StringUtils.printStr(at.getTypeName(), 16));
/*  736 */       if (at instanceof NumericAttrType) {
/*  737 */         if (((NumericAttrType)at).isSparse()) {
/*  738 */           System.out.print(" Yes");
/*      */         } else {
/*  740 */           System.out.print(" No ");
/*      */         } 
/*      */       } else {
/*  743 */         System.out.print(" ?  ");
/*      */       } 
/*  745 */       System.out.print("    ");
/*  746 */       System.out.print(StringUtils.printStr(ClusFormat.TWO_AFTER_DOT.format(at.getNbMissing()), 8));
/*  747 */       System.out.println();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean isRegression() {
/*  753 */     return (getNbNumericTargetAttributes() > 0);
/*      */   }
/*      */   
/*      */   public final void setReader(boolean start_stop) {
/*  757 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/*  758 */       ClusAttrType attr = this.m_Attr.get(j);
/*  759 */       if (attr.getStatus() != 0) {
/*  760 */         attr.setReader(start_stop);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void getPreprocs(DataPreprocs pps, boolean single) {
/*  766 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/*  767 */       ClusAttrType attr = this.m_Attr.get(j);
/*  768 */       if (attr.getStatus() != 0) {
/*  769 */         attr.getPreprocs(pps, single);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public final int getMaxNbStats() {
/*  775 */     int max = 0;
/*  776 */     ClusAttrType[] descr = getAllAttrUse(1);
/*  777 */     for (int i = 0; i < descr.length; i++) {
/*  778 */       max = Math.max(descr[i].getMaxNbStats(), max);
/*      */     }
/*  780 */     return max;
/*      */   }
/*      */   
/*      */   public final XValMainSelection getXValSelection(ClusData data) throws ClusException {
/*  784 */     if (this.m_TSAttr == null) {
/*  785 */       return (XValMainSelection)new XValRandomSelection(data.getNbRows(), getSettings().getXValFolds());
/*      */     }
/*  787 */     return (XValMainSelection)new XValDataSelection(this.m_TSAttr);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void setTestSet(int id) {
/*  792 */     if (id != -1) {
/*  793 */       System.out.println("Setting test set ID: " + id);
/*  794 */       ClusAttrType type = this.m_Attr.get(id);
/*  795 */       this.m_Attr.set(id, this.m_TSAttr = new IndexAttrType(type.getName()));
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void attachModel(ClusModel model) throws ClusException {
/*  800 */     HashMap table = buildAttributeHash();
/*  801 */     model.attachModel(table);
/*      */   }
/*      */   
/*      */   public final HashMap buildAttributeHash() throws ClusException {
/*  805 */     HashMap<Object, Object> hash = new HashMap<>();
/*  806 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/*  807 */       ClusAttrType at = this.m_Attr.get(j);
/*  808 */       if (hash.containsKey(at.getName())) {
/*  809 */         throw new ClusException("Duplicate attribute name: '" + at.getName() + "'");
/*      */       }
/*  811 */       hash.put(at.getName(), at);
/*      */     } 
/*      */     
/*  814 */     return hash;
/*      */   }
/*      */   
/*      */   public void initializeFrom(ClusSchema schema) throws ClusException {
/*  818 */     if (schema.isSparse()) {
/*  819 */       ensureSparse();
/*      */     }
/*      */     
/*  822 */     int this_nb = getNbAttributes();
/*  823 */     int other_nb = schema.getNbAttributes();
/*  824 */     if (other_nb > this_nb) {
/*  825 */       throw new ClusException("To few attributes in data set " + this_nb + " < " + other_nb);
/*      */     }
/*  827 */     for (int i = 0; i < other_nb; i++) {
/*  828 */       ClusAttrType this_type = getAttrType(i);
/*  829 */       ClusAttrType other_type = schema.getAttrType(i);
/*  830 */       if (!this_type.getName().equals(other_type.getName())) {
/*  831 */         throw new ClusException("Attribute names do not align: '" + other_type.getName() + "' expected at position " + (i + 1) + " but found '" + this_type
/*  832 */             .getName() + "'");
/*      */       }
/*  834 */       if (this_type.getClass() != other_type.getClass()) {
/*  835 */         throw new ClusException("Attribute types do not match for '" + other_type.getName() + "' expected '" + other_type
/*  836 */             .getClass().getName() + "' but found '" + this_type.getClass().getName() + "'");
/*      */       }
/*  838 */       this_type.initializeFrom(other_type);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void addColsIndex() {
/*  843 */     int idx = 0;
/*  844 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/*  845 */       ClusAttrType at = this.m_Attr.get(j);
/*  846 */       if (at.getStatus() == 3) {
/*  847 */         at.setArrayIndex(idx++);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public static ClusAttrType[] vectorToAttrArray(ArrayList<ClusAttrType> list) {
/*  853 */     ClusAttrType[] res = new ClusAttrType[list.size()];
/*  854 */     for (int i = 0; i < list.size(); i++) {
/*  855 */       res[i] = list.get(i);
/*      */     }
/*  857 */     return res;
/*      */   }
/*      */   
/*      */   public static NominalAttrType[] vectorToNominalAttrArray(ArrayList<NominalAttrType> list) {
/*  861 */     NominalAttrType[] res = new NominalAttrType[list.size()];
/*  862 */     for (int i = 0; i < list.size(); i++) {
/*  863 */       res[i] = list.get(i);
/*      */     }
/*  865 */     return res;
/*      */   }
/*      */   
/*      */   public static NumericAttrType[] vectorToNumericAttrArray(ArrayList<NumericAttrType> list) {
/*  869 */     NumericAttrType[] res = new NumericAttrType[list.size()];
/*  870 */     for (int i = 0; i < list.size(); i++) {
/*  871 */       res[i] = list.get(i);
/*      */     }
/*  873 */     return res;
/*      */   }
/*      */   
/*      */   public static TimeSeriesAttrType[] vectorToTimeSeriesAttrArray(ArrayList<TimeSeriesAttrType> list) {
/*  877 */     TimeSeriesAttrType[] res = new TimeSeriesAttrType[list.size()];
/*  878 */     for (int i = 0; i < list.size(); i++) {
/*  879 */       res[i] = list.get(i);
/*      */     }
/*  881 */     return res;
/*      */   }
/*      */   
/*      */   public ArrayList collectAttributes(int attruse, int attrtype) {
/*  885 */     ArrayList<ClusAttrType> result = new ArrayList();
/*  886 */     for (int i = 0; i < getNbAttributes(); i++) {
/*  887 */       ClusAttrType type = getAttrType(i);
/*  888 */       if (attrtype == -1 || attrtype == type.getTypeIndex()) {
/*  889 */         switch (attruse) {
/*      */           case 0:
/*  891 */             if (type.getStatus() != 0 && type.getStatus() != 4) {
/*  892 */               result.add(type);
/*      */             }
/*      */             break;
/*      */           case 2:
/*  896 */             if (type.isClustering()) {
/*  897 */               result.add(type);
/*      */             }
/*      */             break;
/*      */           case 1:
/*  901 */             if (type.isDescriptive()) {
/*  902 */               result.add(type);
/*      */             }
/*      */             break;
/*      */           case 3:
/*  906 */             if (type.getStatus() == 1) {
/*  907 */               result.add(type);
/*      */             }
/*      */             break;
/*      */           case 4:
/*  911 */             if (type.getStatus() == 4) {
/*  912 */               result.add(type);
/*      */             }
/*      */             break;
/*      */         } 
/*      */       }
/*      */     } 
/*  918 */     return result;
/*      */   }
/*      */   
/*      */   public boolean isSparse() {
/*  922 */     return this.m_IsSparse;
/*      */   }
/*      */   
/*      */   public void setSparse() {
/*  926 */     this.m_IsSparse = true;
/*      */   }
/*      */   
/*      */   public ClusAttrType[] getNonSparseAttributes() {
/*  930 */     return this.m_NonSparse;
/*      */   }
/*      */   
/*      */   public void ensureSparse() {
/*  934 */     LinkedList<Integer> sparseIndex = new LinkedList<>();
/*  935 */     if (!this.m_IsSparse) {
/*  936 */       int nbSparse = 0;
/*  937 */       ArrayList<ClusAttrType> nonSparse = new ArrayList<>();
/*  938 */       for (int i = 0; i < getNbAttributes(); i++) {
/*  939 */         ClusAttrType type = getAttrType(i);
/*  940 */         if (type.isDescriptive() && type.getTypeIndex() == 1) {
/*  941 */           SparseNumericAttrType nt = new SparseNumericAttrType((NumericAttrType)type);
/*  942 */           nt.setStatus(type.getStatus());
/*  943 */           nt.setDescriptive(true);
/*      */           
/*  945 */           setAttrType(nt, i);
/*  946 */           sparseIndex.add(Integer.valueOf(i));
/*      */ 
/*      */ 
/*      */           
/*  950 */           nbSparse++;
/*      */         } else {
/*  952 */           nonSparse.add(type);
/*      */         } 
/*      */       } 
/*  955 */       this.m_NonSparse = vectorToAttrArray(nonSparse);
/*      */       
/*  957 */       this.m_IsSparse = true;
/*      */       
/*  959 */       addRowsIndex();
/*  960 */       int sparseCounter = 0;
/*  961 */       for (int j = 0; j < getNbAttributes(); j++) {
/*  962 */         ClusAttrType type = getAttrType(j);
/*      */         
/*  964 */         if (type.isDescriptive() && type.getTypeIndex() == 1) {
/*  965 */           type.setIndex(((Integer)sparseIndex.get(sparseCounter)).intValue());
/*  966 */           type.setArrayIndex(((Integer)sparseIndex.get(sparseCounter)).intValue());
/*  967 */           sparseCounter++;
/*      */         } 
/*      */       } 
/*  970 */       setM_NbSparse(nbSparse);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void printInfo() {
/*  975 */     if (getSettings().getVerbose() >= 1) {
/*  976 */       System.out.println("Space required by nominal attributes: " + (this.m_NbVt[0] * 4) + " bytes/tuple regular, " + (this.m_NbVt[3] * 4) + " bytes/tuple bitwise");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void addRowsIndex() {
/*  982 */     this.m_NbVt = new int[4];
/*  983 */     int bitPosition = 0;
/*  984 */     int nbBitwise = 0;
/*  985 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/*  986 */       ClusAttrType at = this.m_Attr.get(j);
/*  987 */       int vtype = at.getValueType();
/*  988 */       if (vtype == -1 || at.getStatus() == 0) {
/*  989 */         at.setArrayIndex(-1);
/*  990 */       } else if (vtype != 3) {
/*  991 */         this.m_NbVt[vtype] = this.m_NbVt[vtype] + 1; int sidx = this.m_NbVt[vtype];
/*  992 */         at.setArrayIndex(sidx);
/*      */       } else {
/*  994 */         nbBitwise++;
/*  995 */         BitwiseNominalAttrType bat = (BitwiseNominalAttrType)at;
/*  996 */         int nextBitPosition = bitPosition + bat.getNbBits();
/*  997 */         if (nextBitPosition > 32) {
/*      */           
/*  999 */           this.m_NbVt[vtype] = this.m_NbVt[vtype] + 1;
/* 1000 */           int sidx = this.m_NbVt[vtype];
/* 1001 */           bat.setArrayIndex(sidx);
/* 1002 */           bat.setBitPosition(0);
/* 1003 */           bitPosition = bat.getNbBits();
/*      */         } else {
/* 1005 */           int sidx = this.m_NbVt[vtype];
/* 1006 */           bat.setArrayIndex(sidx);
/* 1007 */           bat.setBitPosition(bitPosition);
/* 1008 */           bitPosition = nextBitPosition;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1013 */     if (nbBitwise > 0) {
/* 1014 */       this.m_NbVt[3] = this.m_NbVt[3] + 1;
/*      */     }
/* 1016 */     this.m_NbInts = Math.max(this.m_NbVt[0], this.m_NbVt[3]);
/* 1017 */     this.m_NbDoubles = this.m_NbVt[1];
/* 1018 */     this.m_NbObjects = this.m_NbVt[2];
/*      */ 
/*      */     
/* 1021 */     this.m_AllAttrUse = new ClusAttrType[5][];
/* 1022 */     this.m_NominalAttrUse = new NominalAttrType[5][];
/* 1023 */     this.m_NumericAttrUse = new NumericAttrType[5][];
/* 1024 */     this.m_TimeSeriesAttrUse = new TimeSeriesAttrType[5][];
/* 1025 */     for (int attruse = 0; attruse <= 4; attruse++) {
/* 1026 */       this.m_AllAttrUse[attruse] = vectorToAttrArray(collectAttributes(attruse, -1));
/* 1027 */       this.m_NominalAttrUse[attruse] = vectorToNominalAttrArray(collectAttributes(attruse, 0));
/* 1028 */       this.m_NumericAttrUse[attruse] = vectorToNumericAttrArray(collectAttributes(attruse, 1));
/* 1029 */       this.m_TimeSeriesAttrUse[attruse] = vectorToTimeSeriesAttrArray(collectAttributes(attruse, 5));
/*      */     } 
/*      */   }
/*      */   
/*      */   public DataTuple createTuple() {
/* 1034 */     return this.m_IsSparse ? (DataTuple)new SparseDataTuple(this) : new DataTuple(this);
/*      */   }
/*      */   
/*      */   public ClusView createNormalView() throws ClusException {
/* 1038 */     ClusView view = new ClusView();
/* 1039 */     createNormalView(view);
/* 1040 */     return view;
/*      */   }
/*      */   
/*      */   public void createNormalView(ClusView view) throws ClusException {
/* 1044 */     int nb = getNbAttributes();
/* 1045 */     for (int j = 0; j < nb; j++) {
/* 1046 */       ClusAttrType at = getAttrType(j);
/* 1047 */       int status = at.getStatus();
/* 1048 */       if (status == 0) {
/* 1049 */         view.addAttribute((ClusSerializable)new DummySerializable());
/*      */       } else {
/* 1051 */         view.addAttribute(at.createRowSerializable());
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public String toString() {
/* 1057 */     int aidx = 0;
/* 1058 */     StringBuffer buf = new StringBuffer();
/* 1059 */     for (int i = 0; i < getNbAttributes(); i++) {
/* 1060 */       ClusAttrType type = getAttrType(i);
/* 1061 */       if (!type.isDisabled()) {
/* 1062 */         if (aidx != 0) {
/* 1063 */           buf.append(",");
/*      */         }
/* 1065 */         buf.append(type.getName());
/* 1066 */         aidx++;
/*      */       } 
/*      */     } 
/* 1069 */     return buf.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getM_NbSparse() {
/* 1076 */     return this.m_NbSparse;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setM_NbSparse(int m_NbSparse) {
/* 1083 */     this.m_NbSparse = m_NbSparse;
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\type\ClusSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */