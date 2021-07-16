/*     */ package addon.hmc.HMCNodeWiseModels.hmcnwmodels;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.algo.tdidt.ClusDecisionTree;
/*     */ import clus.algo.tdidt.tune.CDTTuneFTest;
/*     */ import clus.data.ClusData;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.MemoryTupleIterator;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.SparseDataTuple;
/*     */ import clus.data.rows.TupleIterator;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.ensembles.ClusEnsembleClassifier;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.ext.hierarchical.ClassesAttrType;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.ext.hierarchical.ClassesValue;
/*     */ import clus.main.ClusOutput;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.modelio.ClusModelCollectionIO;
/*     */ import clus.util.ClusException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.Hashtable;
/*     */ import jeans.util.array.StringTable;
/*     */ import jeans.util.cmdline.CMDLineArgs;
/*     */ import jeans.util.cmdline.CMDLineArgsProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HMCNodeWiseModels
/*     */   implements CMDLineArgsProvider
/*     */ {
/*  47 */   private static String[] g_Options = new String[] { "forest" };
/*  48 */   private static int[] g_OptionArities = new int[] { 0 };
/*     */   
/*     */   protected Clus m_Clus;
/*     */   protected CMDLineArgs m_Cargs;
/*  52 */   protected StringTable m_Table = new StringTable();
/*     */   protected Hashtable m_Mappings;
/*     */   protected double[] m_FTests;
/*     */   
/*     */   public void run(String[] args) throws IOException, ClusException, ClassNotFoundException {
/*  57 */     this.m_Clus = new Clus();
/*  58 */     Settings sett = this.m_Clus.getSettings();
/*  59 */     this.m_Cargs = new CMDLineArgs(this);
/*  60 */     this.m_Cargs.process(args);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  68 */     if (this.m_Cargs.allOK()) {
/*  69 */       CDTTuneFTest cDTTuneFTest; (new File("hsc")).mkdir();
/*  70 */       (new File("hsc/out")).mkdir();
/*  71 */       (new File("hsc/model")).mkdir();
/*  72 */       sett.setDate(new Date());
/*  73 */       sett.setAppName(this.m_Cargs.getMainArg(0));
/*  74 */       this.m_Clus.initSettings(this.m_Cargs);
/*  75 */       ClusDecisionTree clss = new ClusDecisionTree(this.m_Clus);
/*  76 */       if (sett.getFTestArray().isVector()) {
/*  77 */         this.m_FTests = sett.getFTestArray().getDoubleVector();
/*  78 */         cDTTuneFTest = new CDTTuneFTest((ClusInductionAlgorithmType)clss, sett.getFTestArray().getDoubleVector());
/*     */       } 
/*  80 */       this.m_Clus.initialize(this.m_Cargs, (ClusInductionAlgorithmType)cDTTuneFTest);
/*  81 */       doRun();
/*     */     } else {
/*  83 */       System.out.println("m_Cargs nok");
/*     */     } 
/*     */   }
/*     */   public RowData getNodeData(RowData train, int nodeid) {
/*  87 */     ArrayList<DataTuple> selected = new ArrayList();
/*  88 */     for (int i = 0; i < train.getNbRows(); i++) {
/*     */       DataTuple tuple;
/*  90 */       if (this.m_Clus.getSchema().isSparse()) {
/*  91 */         SparseDataTuple sparseDataTuple = (SparseDataTuple)train.getTuple(i);
/*     */       } else {
/*  93 */         tuple = train.getTuple(i);
/*     */       } 
/*  95 */       ClassesTuple target = (ClassesTuple)tuple.getObjVal(0);
/*  96 */       if (nodeid == -1 || target.hasClass(nodeid)) {
/*  97 */         selected.add(tuple);
/*     */       }
/*     */     } 
/* 100 */     return new RowData(selected, train.getSchema());
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData createChildData(RowData nodeData, ClassesAttrType ctype, int childid) throws ClusException {
/* 105 */     ClassHierarchy chier = ctype.getHier();
/* 106 */     ClassesValue one = new ClassesValue("1", ctype.getTable());
/* 107 */     chier.addClass(one);
/* 108 */     chier.initialize();
/* 109 */     one.addHierarchyIndices(chier);
/* 110 */     RowData childData = new RowData(ctype.getSchema(), nodeData.getNbRows());
/* 111 */     for (int j = 0; j < nodeData.getNbRows(); j++) {
/* 112 */       DataTuple tuple; ClassesTuple clss = null;
/*     */       
/* 114 */       if (this.m_Clus.getSchema().isSparse()) {
/* 115 */         SparseDataTuple sparseDataTuple = (SparseDataTuple)nodeData.getTuple(j);
/*     */       } else {
/* 117 */         tuple = nodeData.getTuple(j);
/*     */       } 
/* 119 */       ClassesTuple target = (ClassesTuple)tuple.getObjVal(0);
/* 120 */       if (target.hasClass(childid)) {
/* 121 */         clss = new ClassesTuple(1);
/* 122 */         clss.addItem(new ClassesValue(one.getTerm()));
/*     */       } else {
/* 124 */         clss = new ClassesTuple(0);
/*     */       } 
/* 126 */       DataTuple new_tuple = tuple.deepCloneTuple();
/* 127 */       new_tuple.setSchema(ctype.getSchema());
/* 128 */       new_tuple.setObjectVal(clss, 0);
/* 129 */       childData.setTuple(new_tuple, j);
/*     */     } 
/* 131 */     return childData;
/*     */   }
/*     */   
/*     */   public ClusSchema createChildSchema(ClusSchema oschema, ClassesAttrType ctype, String name) throws ClusException, IOException {
/* 135 */     ClusSchema cschema = new ClusSchema(name);
/* 136 */     for (int j = 0; j < oschema.getNbAttributes(); j++) {
/* 137 */       ClusAttrType atype = oschema.getAttrType(j);
/* 138 */       if (!(atype instanceof ClassesAttrType)) {
/* 139 */         ClusAttrType copy_atype = atype.cloneType();
/* 140 */         cschema.addAttrType(copy_atype);
/*     */       } 
/*     */     } 
/* 143 */     cschema.addAttrType((ClusAttrType)ctype);
/* 144 */     cschema.initializeSettings(this.m_Clus.getSettings());
/* 145 */     if (oschema.isSparse()) {
/* 146 */       cschema.setSparse();
/*     */     }
/* 148 */     return cschema;
/*     */   }
/*     */ 
/*     */   
/*     */   public void doOneNode(ClassTerm node, ClassHierarchy hier, RowData train, RowData valid, RowData test) throws ClusException, IOException {
/* 153 */     RowData nodeData = getNodeData(train, node.getIndex());
/* 154 */     String nodeName = node.toPathString("=");
/*     */     
/* 156 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 157 */       ClusDecisionTree clusDecisionTree; CDTTuneFTest cDTTuneFTest; ClassTerm child = (ClassTerm)node.getChild(i);
/* 158 */       String childName = child.toPathString("=");
/* 159 */       ClassesAttrType ctype = new ClassesAttrType(nodeName + "-" + childName);
/* 160 */       ClusSchema cschema = createChildSchema(train.getSchema(), ctype, "REL-" + nodeName + "-" + childName);
/* 161 */       RowData childData = createChildData(nodeData, ctype, child.getIndex());
/*     */       
/* 163 */       if (this.m_Cargs.hasOption("forest")) {
/* 164 */         this.m_Clus.getSettings().setEnsembleMode(true);
/* 165 */         ClusEnsembleClassifier clusEnsembleClassifier = new ClusEnsembleClassifier(this.m_Clus);
/*     */       } else {
/* 167 */         clusDecisionTree = new ClusDecisionTree(this.m_Clus);
/*     */       } 
/* 169 */       if (this.m_FTests != null) {
/* 170 */         cDTTuneFTest = new CDTTuneFTest((ClusInductionAlgorithmType)clusDecisionTree, this.m_FTests);
/*     */       }
/* 172 */       this.m_Clus.recreateInduce(this.m_Cargs, (ClusInductionAlgorithmType)cDTTuneFTest, cschema, childData);
/* 173 */       String name = this.m_Clus.getSettings().getAppName() + "-" + nodeName + "-" + childName;
/* 174 */       ClusRun cr = new ClusRun(childData.cloneData(), this.m_Clus.getSummary());
/* 175 */       cr.copyTrainingData();
/* 176 */       if (valid != null) {
/* 177 */         RowData validNodeData = getNodeData(valid, node.getIndex());
/* 178 */         RowData validChildData = createChildData(validNodeData, ctype, child.getIndex());
/*     */         
/* 180 */         cr.setPruneSet((ClusData)validChildData, null);
/*     */       } 
/*     */       
/* 183 */       if (test != null) {
/* 184 */         RowData testNodeData = getNodeData(test, node.getIndex());
/* 185 */         RowData testChildData = createChildData(testNodeData, ctype, child.getIndex());
/* 186 */         MemoryTupleIterator memoryTupleIterator = testChildData.getIterator();
/* 187 */         cr.setTestSet((TupleIterator)memoryTupleIterator);
/*     */       } 
/*     */       
/* 190 */       this.m_Clus.initializeSummary((ClusInductionAlgorithmType)cDTTuneFTest);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 200 */       ClusOutput output = new ClusOutput("hsc/out/" + name + ".out", cschema, this.m_Clus.getSettings());
/* 201 */       this.m_Clus.getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/* 202 */       this.m_Clus.induce(cr, (ClusInductionAlgorithmType)cDTTuneFTest);
/*     */ 
/*     */ 
/*     */       
/* 206 */       output.writeHeader();
/* 207 */       output.writeOutput(cr, true, this.m_Clus.getSettings().isOutTrainError());
/* 208 */       output.close();
/* 209 */       ClusModelCollectionIO io = new ClusModelCollectionIO();
/* 210 */       io.addModel(cr.addModelInfo(1));
/* 211 */       io.save("hsc/model/" + name + ".model");
/*     */     } 
/*     */   }
/*     */   
/*     */   public void computeRecursive(ClassTerm node, ClassHierarchy hier, RowData train, RowData valid, RowData test, boolean[] computed) throws ClusException, IOException {
/* 216 */     if (!computed[node.getIndex()]) {
/*     */       
/* 218 */       computed[node.getIndex()] = true;
/* 219 */       doOneNode(node, hier, train, valid, test);
/*     */       
/* 221 */       for (int i = 0; i < node.getNbChildren(); i++) {
/* 222 */         ClassTerm child = (ClassTerm)node.getChild(i);
/* 223 */         computeRecursive(child, hier, train, valid, test, computed);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void computeRecursiveRoot(ClassTerm node, ClassHierarchy hier, RowData train, RowData valid, RowData test, boolean[] computed) throws ClusException, IOException {
/* 229 */     doOneNode(node, hier, train, valid, test);
/* 230 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 231 */       ClassTerm child = (ClassTerm)node.getChild(i);
/*     */       
/* 233 */       computeRecursive(child, hier, train, valid, test, computed);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void doRun() throws IOException, ClusException, ClassNotFoundException {
/* 238 */     Settings sett = this.m_Clus.getSettings();
/* 239 */     ClusRun cr = this.m_Clus.partitionData();
/* 240 */     RowData train = (RowData)cr.getTrainingSet();
/* 241 */     RowData valid = (RowData)cr.getPruneSet();
/* 242 */     RowData test = cr.getTestSet();
/* 243 */     ClusStatManager mgr = this.m_Clus.getStatManager();
/* 244 */     ClassHierarchy hier = mgr.getHier();
/* 245 */     ClassTerm root = hier.getRoot();
/* 246 */     boolean[] computed = new boolean[hier.getTotal()];
/* 247 */     computeRecursiveRoot(root, hier, train, valid, test, computed);
/*     */   }
/*     */   
/*     */   public String[] getOptionArgs() {
/* 251 */     return g_Options;
/*     */   }
/*     */   
/*     */   public int[] getOptionArgArities() {
/* 255 */     return g_OptionArities;
/*     */   }
/*     */   
/*     */   public int getNbMainArgs() {
/* 259 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void showHelp() {}
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/* 267 */       HMCNodeWiseModels m = new HMCNodeWiseModels();
/* 268 */       m.run(args);
/* 269 */     } catch (IOException io) {
/* 270 */       System.out.println("IO Error: " + io.getMessage());
/* 271 */     } catch (ClusException cl) {
/* 272 */       System.out.println("Error: " + cl.getMessage());
/* 273 */       cl.printStackTrace();
/* 274 */     } catch (ClassNotFoundException cn) {
/* 275 */       System.out.println("Error: " + cn.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\addon\hmc\HMCNodeWiseModels\hmcnwmodels\HMCNodeWiseModels.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */