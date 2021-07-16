/*      */ package clus.activelearning.algo;
/*      */ 
/*      */ import clus.Clus;
/*      */ import clus.activelearning.algoHMC.RandomSamplingHMC;
/*      */ import clus.activelearning.data.OracleAnswer;
/*      */ import clus.activelearning.indexing.Indexer;
/*      */ import clus.activelearning.indexing.LabelIndex;
/*      */ import clus.activelearning.indexing.LabelIndexer;
/*      */ import clus.activelearning.indexing.TupleIndex;
/*      */ import clus.activelearning.indexing.TupleIndexer;
/*      */ import clus.activelearning.iteration.ClusActiveLearningIteration;
/*      */ import clus.activelearning.iteration.Iteration;
/*      */ import clus.data.rows.DataTuple;
/*      */ import clus.data.rows.RowData;
/*      */ import clus.ext.hierarchical.ClassHierarchy;
/*      */ import clus.ext.hierarchical.ClassTerm;
/*      */ import clus.ext.hierarchical.ClassesTuple;
/*      */ import clus.util.ClusException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import jeans.resource.ResourceInfo;
/*      */ import jeans.util.array.StringTable;
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
/*      */ public abstract class ClusActiveLearningAlgorithm
/*      */ {
/*      */   private String m_Name;
/*      */   private RowData m_UnlabeledData;
/*      */   private int m_BatchSize;
/*      */   private double m_Budget;
/*      */   private double m_BudgetLeftovers;
/*      */   private int m_IterationCounter;
/*      */   private boolean m_PartialLabelling;
/*      */   private boolean m_SemiPartialLabelling;
/*      */   private double m_PartialLabellingWeight;
/*      */   private int m_LabelsAvailable;
/*      */   private String[] m_Labels;
/*      */   private HashMap<String, Double> m_LabelsCost;
/*      */   private ClassHierarchy m_Hierarchy;
/*      */   private HashMap<String, LinkedList<String>> m_Paths;
/*      */   private ClusActiveLearningIteration m_Iteration;
/*      */   private HashMap<String, Integer> m_LabelQueryCounter;
/*      */   private long m_QueryBuildingTimeTotal;
/*      */   private double m_BudgetSpent;
/*      */   private int m_LabelsQueried;
/*      */   private int m_LabelsAnswered;
/*      */   private int m_PositiveQueried;
/*      */   private int m_NegativeQueried;
/*      */   private int m_NegativeAnswered;
/*      */   private RowData m_LabeledData;
/*      */   private boolean[] m_RemovedIndex;
/*      */   
/*      */   public ClusActiveLearningAlgorithm(Clus clus, String name) throws ClusException, Exception {
/*   69 */     this.m_Name = name;
/*   70 */     this.m_UnlabeledData = clus.getM_ActiveData();
/*   71 */     this.m_LabeledData = clus.getData();
/*   72 */     this.m_Hierarchy = clus.getStatManager().getHier();
/*      */     
/*   74 */     this.m_BatchSize = clus.getSettings().getBatchSize();
/*   75 */     this.m_Budget = clus.getSettings().getBudgetPerIteration();
/*   76 */     setIterationCounter(0);
/*   77 */     this.m_PartialLabelling = clus.getSettings().getPartialLabelling().booleanValue();
/*      */ 
/*      */     
/*   80 */     initializeLabels();
/*      */     
/*   82 */     initializeLabelCosts(clus.getSettings().getLabelCost().getDoubleVector());
/*   83 */     initializeClassTuples();
/*   84 */     initializeActiveIndex();
/*   85 */     initializePaths();
/*      */     
/*   87 */     initializeTotalAvailableAmount();
/*   88 */     initializeRemovedIndex();
/*      */     
/*   90 */     startIteration();
/*      */   }
/*      */   
/*      */   private void initializeRemovedIndex() {
/*   94 */     RowData rowData = getUnlabeledData();
/*   95 */     setRemovedIndex(new boolean[rowData.getNbRows()]);
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
/*      */   private void initializePaths() throws ClusException {
/*  114 */     this.m_Paths = new HashMap<>();
/*      */     
/*  116 */     ClassHierarchy h = getHierarchy();
/*  117 */     for (int i = 0; i < h.getTotal(); i++) {
/*  118 */       ClassTerm ct = h.getTermAt(i);
/*  119 */       LinkedList<String> path = initializePath(ct);
/*  120 */       getPaths().put(ct.toPathString(), path);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private LinkedList<String> initializePath(ClassTerm ct) {
/*  126 */     LinkedList<String> path = new LinkedList<>();
/*  127 */     while (getHierarchy().getRoot() != ct) {
/*  128 */       path.add(ct.toPathString());
/*  129 */       ct = ct.getParent(0);
/*      */     } 
/*  131 */     Collections.reverse(path);
/*  132 */     return path;
/*      */   }
/*      */   
/*      */   private void initializeTotalAvailableAmount() {
/*  136 */     int nbRows = getUnlabeledData().getNbRows();
/*  137 */     int hierarchySize = getHierarchy().getTotal();
/*  138 */     setAvailableLabels(nbRows * hierarchySize);
/*      */   }
/*      */   
/*      */   private void initializeLabels() {
/*  142 */     setLabelQueryCounter(new HashMap<>());
/*  143 */     this.m_Labels = new String[getHierarchy().getTotal()];
/*  144 */     for (int i = 0; i < getHierarchy().getTotal(); i++) {
/*  145 */       this.m_Labels[i] = getHierarchy().getTermAt(i).toPathString();
/*  146 */       getLabelQueryCounter().put(this.m_Labels[i], Integer.valueOf(0));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void initializeActiveIndex() {
/*  152 */     int nbRows = this.m_UnlabeledData.getNbRows();
/*  153 */     for (int i = 0; i < nbRows; i++) {
/*  154 */       this.m_UnlabeledData.getTuple(i).setActiveIndex(i + 1);
/*      */     }
/*      */   }
/*      */   
/*      */   private void initializeLabelCosts(double[] weights) throws ClusException, Exception {
/*  159 */     ClassHierarchy h = getHierarchy();
/*  160 */     setLabelsCost(new HashMap<>());
/*  161 */     if (weights == null) {
/*  162 */       for (String label : getLabels()) {
/*  163 */         getLabelsCost().put(label, Double.valueOf(1.0D));
/*      */       }
/*      */     }
/*  166 */     else if (weights.length == h.getDepth() + 1) {
/*  167 */       for (String label : getLabels()) {
/*  168 */         getLabelsCost().put(label, Double.valueOf(weights[(label.split("/")).length - 1]));
/*      */       }
/*      */     } else {
/*  171 */       throw new ClusException("Labels Cost should match the size of the hierarchy, got " + weights.length + " values for a hierarchy with " + (h.getDepth() + 1) + " levels ");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void startIteration() {
/*      */     TupleIndexer tupleIndexer1, tupleIndexer2;
/*  179 */     Indexer indexerPositive = null;
/*  180 */     Indexer indexerNegative = null;
/*      */     
/*  182 */     if (this.m_PartialLabelling) {
/*  183 */       LabelIndexer labelIndexer1 = new LabelIndexer(getBatchsize());
/*  184 */       LabelIndexer labelIndexer2 = new LabelIndexer(getBatchsize());
/*      */     } else {
/*  186 */       tupleIndexer1 = new TupleIndexer(getBatchsize());
/*  187 */       tupleIndexer2 = new TupleIndexer(getBatchsize());
/*      */     } 
/*  189 */     ClusActiveLearningIteration iteration = new ClusActiveLearningIteration(getLabelQueryCounter(), getIterationCounter(), getBudget() + getBudgetLeftovers(), getAvaliableLabels(), (Indexer)tupleIndexer1, (Indexer)tupleIndexer2);
/*      */ 
/*      */     
/*  192 */     setIteration(iteration);
/*      */   }
/*      */   
/*      */   protected void finishIteration() {
/*  196 */     ClusActiveLearningIteration iteration = getIteration();
/*  197 */     iteration.setQueryBuildingTime((long)((ResourceInfo.getTime() - iteration.getQueryBuildingTime()) / 1000.0D));
/*      */     
/*  199 */     setQueryBuildingTimeTotal(getQueryBuildingTimeTotal() + iteration.getQueryBuildingTime());
/*  200 */     setBudgetLeftovers(getIteration().getBudget() - iteration.getBudgetSpent());
/*  201 */     setBudgetSpent(getBudgetSpent() + iteration.getBudgetSpent());
/*  202 */     iteration.setIsDone((iteration.getLabelsAvailable() == 0));
/*      */     
/*  204 */     setAvailableLabels(iteration.getLabelsAvailable());
/*  205 */     setLabelsQueried(getLabelsQueried() + iteration.getLabelsQueried());
/*  206 */     setLabelsAnswered(getLabelsAnswered() + iteration.getLabelsAnswered());
/*  207 */     setNegativeQueried(getNegativeQueried() + iteration.getNegativeAnswerNoHierarchy());
/*  208 */     setNegativeAnswered(getNegativeAnswered() + iteration.getNegativeAnswer());
/*      */     
/*  210 */     setPositiveQueried(getPositiveQueried() + iteration.getPositiveAnswers());
/*  211 */     setLabelQueryCounter(iteration.getLabelCounter());
/*      */     
/*  213 */     increaseIteration();
/*      */   }
/*      */   
/*      */   public boolean isDone() {
/*  217 */     return getIteration().isDone();
/*      */   }
/*      */   
/*      */   private void initializeClassTuples() throws ClusException {
/*  221 */     int nbRows = getUnlabeledData().getNbRows();
/*  222 */     for (int i = 0; i < nbRows; i++) {
/*  223 */       ClassesTuple ct = (ClassesTuple)this.m_UnlabeledData.getTuple(i).getObjVal(0);
/*  224 */       OracleAnswer answer = new OracleAnswer(ct);
/*  225 */       getUnlabeledData().getTuple(i).setOracleAnswer(answer);
/*  226 */       this.m_UnlabeledData.getTuple(i).setObjectVal(null, 0);
/*      */ 
/*      */       
/*  229 */       answer.addCost(Double.valueOf(1.0D));
/*  230 */       answer.reset();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected RowData getLabeledDataset(LinkedList<TupleIndex> selectedIndexes) throws ClusException {
/*  238 */     HashSet<Integer> indexesQueried = selectTuplesByIndex(selectedIndexes);
/*  239 */     ArrayList<DataTuple> queriedData = getTuplesByIndex(indexesQueried);
/*  240 */     getUnlabeledData().removeRows(indexesQueried);
/*  241 */     return new RowData(queriedData, getUnlabeledData().getSchema().cloneSchema());
/*      */   }
/*      */   
/*      */   public RowData getTuplesByIndexer(LinkedList<TupleIndex> ti) {
/*  245 */     ArrayList<DataTuple> data = new ArrayList<>();
/*  246 */     for (TupleIndex t : ti) {
/*  247 */       data.add(getUnlabeledData().getTupleByActiveIndex(t.getIndex()));
/*      */     }
/*  249 */     return new RowData(data, getUnlabeledData().getSchema().cloneSchema());
/*      */   }
/*      */ 
/*      */   
/*      */   public HashSet<Integer> selectTuplesByIndex(LinkedList<TupleIndex> selectedIndexes) {
/*  254 */     HashSet<Integer> indexesQueried = new HashSet<>();
/*  255 */     ClassHierarchy h = getHierarchy();
/*      */     
/*  257 */     ClusActiveLearningIteration iteration = getIteration();
/*  258 */     for (TupleIndex i : selectedIndexes) {
/*  259 */       OracleAnswer oracleAnswer = this.m_UnlabeledData.getTupleByActiveIndex(i.getIndex()).getOracleAnswer();
/*      */       try {
/*  261 */         double cost = oracleAnswer.getInstanceCost().doubleValue();
/*  262 */         if (iteration.isAffordable(cost)) {
/*  263 */           iteration.updateBudget(cost);
/*  264 */           indexesQueried.add(Integer.valueOf(i.getIndex()));
/*  265 */           iteration.increaseSelectedAmount();
/*      */         } 
/*  267 */       } catch (ClusException ex) {
/*  268 */         Logger.getLogger(RandomSamplingHMC.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*      */       } 
/*      */     } 
/*  271 */     return indexesQueried;
/*      */   }
/*      */   
/*      */   public double getCost(String queried) throws ClusException {
/*  275 */     return ((Double)getLabelsCost().get(queried)).doubleValue();
/*      */   }
/*      */   
/*      */   public ArrayList<DataTuple> getTuplesByIndex(HashSet<Integer> indexes) throws ClusException {
/*  279 */     ArrayList<DataTuple> data = new ArrayList<>();
/*  280 */     ClassHierarchy h = getHierarchy();
/*  281 */     StringTable stringTable = h.getType().getTable();
/*  282 */     ClusActiveLearningIteration iteration = getIteration();
/*  283 */     int hierarchySize = h.getTotal();
/*  284 */     for (Iterator<Integer> iterator = indexes.iterator(); iterator.hasNext(); ) { int i = ((Integer)iterator.next()).intValue();
/*  285 */       DataTuple dataTuple = this.m_UnlabeledData.getTupleByActiveIndex(i);
/*  286 */       dataTuple.addAllPossibleLabelsFromOracle(h, stringTable);
/*  287 */       data.add(dataTuple);
/*  288 */       iteration.updateTotalLabels(hierarchySize);
/*  289 */       getRemovedIndex()[i - 1] = true; }
/*      */ 
/*      */ 
/*      */     
/*  293 */     return data;
/*      */   }
/*      */   
/*      */   protected RowData getPartialLabelledDataset(LinkedList<LabelIndex> instances) throws ClusException {
/*  297 */     HashMap<Integer, DataTuple> queriedInstances = new HashMap<>();
/*      */ 
/*      */ 
/*      */     
/*  301 */     getIteration().setM_Query(instances);
/*  302 */     for (LabelIndex instance : instances) {
/*  303 */       DataTuple tuple = getUnlabeledData().getTupleByActiveIndex(instance.getIndex());
/*  304 */       OracleAnswer oracleAnswer = tuple.getOracleAnswer();
/*      */       
/*  306 */       queriedInstances.put(Integer.valueOf(instance.getIndex()), tuple);
/*  307 */       if (getName().equals("UncertaintySamplingHCAL")) {
/*  308 */         getAnswerFromOracleHCAL(instance, oracleAnswer, tuple); continue;
/*      */       } 
/*  310 */       getAnswerFromOracle(instance, oracleAnswer, tuple);
/*      */     } 
/*      */     
/*  313 */     ArrayList<DataTuple> queriedDataset = new ArrayList<>(queriedInstances.values());
/*  314 */     RowData rowData = new RowData(queriedDataset, getUnlabeledData().getSchema());
/*  315 */     HashSet<Integer> totallyAnsweredTuplesIndexes = finishAnswering(rowData, (Iteration)getIteration());
/*  316 */     this.m_UnlabeledData.removeRows(totallyAnsweredTuplesIndexes);
/*  317 */     return rowData;
/*      */   }
/*      */   
/*      */   private void getAnswerFromOracleHCAL(LabelIndex i, OracleAnswer oracleAnswer, DataTuple tuple) throws ClusException {
/*  321 */     getAnswersFromOracleHCAL(i, oracleAnswer);
/*  322 */     oracleAnswer.labelTuple(tuple, this.m_Hierarchy);
/*  323 */     this.m_Iteration.addNewLabels(oracleAnswer.getNewLabels());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void getAnswersFromOracleHCAL(LabelIndex i, OracleAnswer oracleAnswer) throws ClusException {
/*  329 */     ClassHierarchy h = getHierarchy();
/*  330 */     ClusActiveLearningIteration iteration = getIteration();
/*  331 */     int batchSize = getBatchsize();
/*      */     
/*  333 */     String queried = i.getLabel();
/*  334 */     double cost = getCost(queried);
/*      */     
/*  336 */     LabelIndexer positive = (LabelIndexer)iteration.getPositiveIndexer();
/*  337 */     LabelIndexer negative = (LabelIndexer)iteration.getNegativeIndexer();
/*      */     
/*  339 */     if (iteration.isLabelAffordable(cost)) {
/*  340 */       int depth = (queried.split("/")).length;
/*  341 */       if (iteration.fitBatchSizeHCAL(batchSize, depth)) {
/*  342 */         if (oracleAnswer.getPossibleLabels().contains(queried)) {
/*  343 */           LinkedList<String> realpath = new LinkedList<>(getPaths().get(queried));
/*  344 */           HashSet<String> pathRemoved = (HashSet<String>)oracleAnswer.getPositiveAnswers().clone();
/*  345 */           pathRemoved.addAll(oracleAnswer.getNewPositiveAnswers());
/*      */           
/*  347 */           realpath.removeAll(pathRemoved);
/*      */           
/*  349 */           for (String d : realpath) {
/*  350 */             positive.add(i.getIndex(), d, i.getMeasure());
/*      */           }
/*  352 */           oracleAnswer.addNewPositiveLabelHCAL(getPaths().get(queried));
/*  353 */           iteration.updateBudget(cost);
/*  354 */           iteration.increaseSelectedAmountHCAL(depth);
/*  355 */           iteration.increasePositiveLabelCounter(queried);
/*      */         } else {
/*      */           
/*  358 */           oracleAnswer.addNewNegativeLabelHCAL(queried, h);
/*  359 */           iteration.updateBudget(cost);
/*  360 */           iteration.increaseSelectedAmountHCAL(depth);
/*  361 */           iteration.increaseNegativeLabelCounter(queried, h);
/*  362 */           for (String d : h.getDescendantsString(queried)) {
/*  363 */             negative.add(i.getIndex(), d, i.getMeasure());
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HashSet<Integer> finishAnswering(RowData rowData, Iteration iteration) throws ClusException {
/*  375 */     int nbRows = rowData.getNbRows();
/*  376 */     HashSet<Integer> indexesToRemove = new HashSet<>();
/*  377 */     int hierarchySize = getHierarchy().getTotal();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  383 */     for (int i = 0; i < nbRows; i++) {
/*  384 */       DataTuple dataTuple = rowData.getTuple(i);
/*  385 */       OracleAnswer oracleAnswer = dataTuple.getOracleAnswer();
/*  386 */       int positiveAmount = oracleAnswer.getNewPositiveLabels();
/*  387 */       int negativeAmount = oracleAnswer.getNewNegativeLabels();
/*  388 */       int negativeAmountNoHierarchy = oracleAnswer.getNewNegativeLabelsNoHierarchy();
/*  389 */       int queriedAmount = positiveAmount + negativeAmount;
/*  390 */       int answeredAmount = positiveAmount + negativeAmountNoHierarchy;
/*  391 */       iteration.updateTotalLabels(queriedAmount);
/*  392 */       iteration.updateAnsweredLabels(answeredAmount);
/*  393 */       iteration.updatePositiveAmount(positiveAmount);
/*  394 */       iteration.updateNegativeAmount(negativeAmount);
/*  395 */       iteration.updateNegativeAmountHierarchy(negativeAmountNoHierarchy);
/*  396 */       oracleAnswer.finishAnswering();
/*  397 */       if (oracleAnswer.isDone(hierarchySize)) {
/*  398 */         indexesToRemove.add(Integer.valueOf(dataTuple.m_ActiveIndex));
/*  399 */         getRemovedIndex()[dataTuple.m_ActiveIndex - 1] = true;
/*      */       } 
/*      */     } 
/*      */     
/*  403 */     return indexesToRemove;
/*      */   }
/*      */   
/*      */   private void getAnswerFromOracle(LabelIndex i, OracleAnswer oracleAnswer, DataTuple tuple) throws ClusException {
/*  407 */     getAnswersFromOracle(i, oracleAnswer);
/*  408 */     oracleAnswer.labelTuple(tuple, this.m_Hierarchy);
/*  409 */     this.m_Iteration.addNewLabels(oracleAnswer.getNewLabels());
/*      */   }
/*      */ 
/*      */   
/*      */   private void getAnswersFromOracle(LabelIndex i, OracleAnswer oracleAnswer) throws ClusException {
/*  414 */     LinkedList<String> queriedLabel = getPaths().get(i.getLabel());
/*      */     
/*  416 */     ClassHierarchy h = getHierarchy();
/*  417 */     ClusActiveLearningIteration iteration = getIteration();
/*  418 */     int batchSize = getBatchsize();
/*  419 */     LabelIndexer positive = (LabelIndexer)iteration.getPositiveIndexer();
/*  420 */     LabelIndexer negative = (LabelIndexer)iteration.getNegativeIndexer();
/*  421 */     for (String queried : queriedLabel) {
/*      */       
/*  423 */       if (!oracleAnswer.queriedBefore(queried)) {
/*  424 */         double cost = getCost(queried);
/*  425 */         if (iteration.isLabelAffordable(cost)) {
/*  426 */           if (iteration.fitBatchSize(batchSize)) {
/*  427 */             if (oracleAnswer.getPossibleLabels().contains(queried)) {
/*  428 */               oracleAnswer.addNewPositiveLabel(queried);
/*  429 */               iteration.updateBudget(cost);
/*  430 */               iteration.increaseSelectedAmount();
/*  431 */               iteration.increasePositiveLabelCounter(queried);
/*  432 */               positive.add(i.getIndex(), queried, i.getMeasure()); continue;
/*      */             } 
/*  434 */             oracleAnswer.addNewNegativeLabel(queried, h);
/*  435 */             for (String d : h.getDescendantsString(queried)) {
/*  436 */               negative.add(i.getIndex(), d, i.getMeasure());
/*      */             }
/*  438 */             iteration.updateBudget(cost);
/*  439 */             iteration.increaseSelectedAmount();
/*  440 */             iteration.increaseNegativeLabelCounter(queried, h);
/*      */             break;
/*      */           } 
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected LinkedList<TupleIndex> buildTupleMaxIndexer(double[] measure) throws ClusException {
/*  452 */     TupleIndexer tupleIndexer = new TupleIndexer(getBatchsize());
/*      */ 
/*      */     
/*  455 */     ClusActiveLearningIteration iteration = getIteration();
/*  456 */     for (int i = 0; i < measure.length; i++) {
/*  457 */       DataTuple tuple = getUnlabeledData().getTuple(i);
/*  458 */       OracleAnswer answer = tuple.getOracleAnswer();
/*  459 */       double cost = answer.getInstanceCost().doubleValue();
/*  460 */       if (iteration.isAffordable(cost)) {
/*  461 */         tupleIndexer.addMax(tuple.m_ActiveIndex, measure[i]);
/*      */       }
/*      */     } 
/*      */     
/*  465 */     return tupleIndexer.getIndexer();
/*      */   }
/*      */   
/*      */   protected LinkedList<TupleIndex> buildTupleMinIndexer(double[] measure) throws ClusException {
/*  469 */     TupleIndexer tupleIndexer = new TupleIndexer(getBatchsize());
/*      */ 
/*      */     
/*  472 */     ClusActiveLearningIteration iteration = getIteration();
/*  473 */     for (int i = 0; i < measure.length; i++) {
/*  474 */       DataTuple tuple = getUnlabeledData().getTuple(i);
/*  475 */       OracleAnswer answer = tuple.getOracleAnswer();
/*  476 */       double cost = answer.getInstanceCost().doubleValue();
/*  477 */       if (iteration.isAffordable(cost)) {
/*  478 */         tupleIndexer.addMin(tuple.m_ActiveIndex, measure[i]);
/*      */       }
/*      */     } 
/*  481 */     return tupleIndexer.getIndexer();
/*      */   }
/*      */   
/*      */   protected LinkedList<TupleIndex> buildTupleRandomIndexer() throws ClusException {
/*  485 */     TupleIndexer tupleIndexer = new TupleIndexer(getBatchsize());
/*      */ 
/*      */     
/*  488 */     ClusActiveLearningIteration iteration = getIteration();
/*      */     
/*  490 */     int nbRows = getUnlabeledData().getNbRows();
/*  491 */     for (int i = 0; i < nbRows; i++) {
/*  492 */       DataTuple tuple = getUnlabeledData().getTuple(i);
/*  493 */       OracleAnswer answer = tuple.getOracleAnswer();
/*  494 */       double cost = answer.getInstanceCost().doubleValue();
/*  495 */       if (iteration.isAffordable(cost)) {
/*  496 */         tupleIndexer.add(tuple.m_ActiveIndex);
/*      */       }
/*      */     } 
/*  499 */     tupleIndexer.shuffle();
/*      */ 
/*      */ 
/*      */     
/*  503 */     return tupleIndexer.getIndexer();
/*      */   }
/*      */   
/*      */   protected LinkedList<LabelIndex> buildLabelRandomIndexer() throws ClusException {
/*  507 */     LabelIndexer labelIndexer = new LabelIndexer(getBatchsize());
/*  508 */     ClusActiveLearningIteration iteration = getIteration();
/*      */ 
/*      */     
/*  511 */     int nbRows = getUnlabeledData().getNbRows();
/*      */ 
/*      */     
/*  514 */     for (int i = 0; i < nbRows; i++) {
/*  515 */       DataTuple tuple = getUnlabeledData().getTuple(i);
/*  516 */       OracleAnswer answer = tuple.getOracleAnswer();
/*  517 */       for (String m_Label : getLabels()) {
/*  518 */         String label = m_Label;
/*  519 */         if (!answer.queriedBefore(label)) {
/*  520 */           Double cost = Double.valueOf(getCost(label));
/*  521 */           if (iteration.isLabelAffordable(cost.doubleValue())) {
/*  522 */             labelIndexer.add(tuple.m_ActiveIndex, label);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*  527 */     labelIndexer.shuffle();
/*      */ 
/*      */     
/*  530 */     return labelIndexer.getIndexer();
/*      */   }
/*      */   
/*      */   protected LinkedList<LabelIndex> buildLabelMaxIndexer(double[][] measure) throws ClusException {
/*  534 */     LabelIndexer labelIndexer = new LabelIndexer(getBatchsize());
/*      */ 
/*      */ 
/*      */     
/*  538 */     ClusActiveLearningIteration iteration = getIteration();
/*      */     
/*  540 */     for (int i = 0; i < measure.length; i++) {
/*  541 */       DataTuple tuple = getUnlabeledData().getTuple(i);
/*  542 */       OracleAnswer answer = tuple.getOracleAnswer();
/*  543 */       for (int j = 0; j < (measure[i]).length; j++) {
/*  544 */         String label = getLabels()[j];
/*  545 */         if (!answer.queriedBefore(label)) {
/*  546 */           double cost = getCost(label);
/*  547 */           if (iteration.isLabelAffordable(cost)) {
/*  548 */             labelIndexer.addMax(tuple.m_ActiveIndex, label, measure[i][j]);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*  553 */     Collections.sort(labelIndexer.getIndexer(), Collections.reverseOrder());
/*      */ 
/*      */ 
/*      */     
/*  557 */     return labelIndexer.getIndexer();
/*      */   }
/*      */   
/*      */   public LinkedList<LabelIndex> buildLabelMinIndexer(double[][] criteria) throws ClusException {
/*  561 */     LabelIndexer labelIndexer = new LabelIndexer(getBatchsize());
/*      */ 
/*      */ 
/*      */     
/*  565 */     ClusActiveLearningIteration iteration = getIteration();
/*      */ 
/*      */     
/*  568 */     for (int i = 0; i < criteria.length; i++) {
/*  569 */       DataTuple tuple = getUnlabeledData().getTuple(i);
/*  570 */       OracleAnswer answer = tuple.getOracleAnswer();
/*  571 */       for (int j = 0; j < (criteria[i]).length; j++) {
/*  572 */         String label = getLabels()[j];
/*  573 */         if (!answer.queriedBefore(label)) {
/*  574 */           double cost = getCost(label);
/*  575 */           if (iteration.isLabelAffordable(cost)) {
/*  576 */             labelIndexer.addMin(tuple.m_ActiveIndex, label, criteria[i][j]);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*  581 */     Collections.sort(labelIndexer.getIndexer());
/*      */ 
/*      */ 
/*      */     
/*  585 */     return labelIndexer.getIndexer();
/*      */   }
/*      */   
/*      */   public RowData getTuplesByHighestMeasure(double[] measure) {
/*  589 */     TupleIndexer tupleIndexer = new TupleIndexer(getBatchsize());
/*      */     
/*  591 */     ArrayList<DataTuple> data = new ArrayList<>();
/*  592 */     for (int i = 0; i < measure.length; i++) {
/*  593 */       DataTuple tuple = getUnlabeledData().getTuple(i);
/*  594 */       tupleIndexer.addMax(tuple.m_ActiveIndex, measure[i]);
/*      */     } 
/*      */     
/*  597 */     tupleIndexer.getIndexer().stream().map(ti -> getUnlabeledData().getTupleByActiveIndex(ti.getIndex())).forEachOrdered(dataTuple -> data.add(dataTuple));
/*      */ 
/*      */     
/*  600 */     RowData rowData = new RowData(data, getUnlabeledData().getSchema());
/*  601 */     return rowData;
/*      */   }
/*      */   
/*      */   public LinkedList<TupleIndex> getTupleIndexerByHighestMeasure(double[] measure) {
/*  605 */     TupleIndexer tupleIndexer = new TupleIndexer(getBatchsize());
/*      */ 
/*      */ 
/*      */     
/*  609 */     for (int i = 0; i < measure.length; i++)
/*      */     {
/*      */ 
/*      */       
/*  613 */       tupleIndexer.addMax(i, measure[i]);
/*      */     }
/*      */     
/*  616 */     return tupleIndexer.getIndexer();
/*      */   }
/*      */   
/*      */   public LinkedList<LabelIndex> buildLabelMaxIndexerSemi(RowData rowData, double[][] measure) throws ClusException {
/*  620 */     LabelIndexer labelIndexer = new LabelIndexer(getBatchsize());
/*      */ 
/*      */ 
/*      */     
/*  624 */     ClusActiveLearningIteration iteration = getIteration();
/*      */     
/*  626 */     for (int i = 0; i < measure.length; i++) {
/*  627 */       DataTuple tuple = rowData.getTuple(i);
/*  628 */       OracleAnswer answer = tuple.getOracleAnswer();
/*  629 */       for (int j = 0; j < (measure[i]).length; j++) {
/*  630 */         String label = getLabels()[j];
/*  631 */         if (!answer.queriedBefore(label)) {
/*  632 */           double cost = getCost(label);
/*  633 */           if (iteration.isLabelAffordable(cost)) {
/*  634 */             labelIndexer.addMax(tuple.m_ActiveIndex, label, measure[i][j]);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*  639 */     Collections.sort(labelIndexer.getIndexer(), Collections.reverseOrder());
/*      */ 
/*      */     
/*  642 */     return labelIndexer.getIndexer();
/*      */   }
/*      */   
/*      */   public RowData getTuplesByLowestMeasure(double[] measure) {
/*  646 */     TupleIndexer tupleIndexer = new TupleIndexer(getBatchsize());
/*      */     
/*  648 */     ArrayList<DataTuple> data = new ArrayList<>();
/*  649 */     for (int i = 0; i < measure.length; i++) {
/*  650 */       DataTuple tuple = getUnlabeledData().getTuple(i);
/*  651 */       tupleIndexer.addMin(tuple.m_ActiveIndex, measure[i]);
/*      */     } 
/*      */     
/*  654 */     for (TupleIndex ti : tupleIndexer.getIndexer()) {
/*  655 */       DataTuple dataTuple = getUnlabeledData().getTupleByActiveIndex(ti.getIndex());
/*  656 */       data.add(dataTuple);
/*      */     } 
/*  658 */     RowData rowData = new RowData(data, getUnlabeledData().getSchema());
/*  659 */     return rowData;
/*      */   }
/*      */   
/*      */   public LinkedList<LabelIndex> buildLabelMinIndexerSemi(RowData rowData, double[][] measure) throws ClusException {
/*  663 */     LabelIndexer labelIndexer = new LabelIndexer(getBatchsize());
/*      */ 
/*      */ 
/*      */     
/*  667 */     ClusActiveLearningIteration iteration = getIteration();
/*      */     
/*  669 */     for (int i = 0; i < measure.length; i++) {
/*  670 */       DataTuple tuple = rowData.getTuple(i);
/*  671 */       OracleAnswer answer = tuple.getOracleAnswer();
/*  672 */       for (int j = 0; j < (measure[i]).length; j++) {
/*  673 */         String label = getLabels()[j];
/*  674 */         if (!answer.queriedBefore(label)) {
/*  675 */           double cost = getCost(label);
/*  676 */           if (iteration.isLabelAffordable(cost)) {
/*  677 */             labelIndexer.addMin(tuple.m_ActiveIndex, label, measure[i][j]);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*  682 */     Collections.sort(labelIndexer.getIndexer());
/*      */     
/*  684 */     return labelIndexer.getIndexer();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getLabels() {
/*  691 */     return this.m_Labels;
/*      */   }
/*      */   
/*      */   public void increaseIteration() {
/*  695 */     setIterationCounter(getIterationCounter() + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RowData getUnlabeledData() {
/*  702 */     return this.m_UnlabeledData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUnlabeledData(RowData m_Data) {
/*  709 */     this.m_UnlabeledData = m_Data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getBatchsize() {
/*  716 */     return this.m_BatchSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBatchsize(int batchsize) {
/*  723 */     this.m_BatchSize = batchsize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getBudget() {
/*  730 */     return this.m_Budget;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBudget(double budget) {
/*  737 */     this.m_Budget = budget;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isPartialLabelling() {
/*  744 */     return this.m_PartialLabelling;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPartialLabelling(boolean m_PartialLabelling) {
/*  751 */     this.m_PartialLabelling = m_PartialLabelling;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getPartialLabellingWeight() {
/*  758 */     return this.m_PartialLabellingWeight;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPartialLabellingWeight(double m_PartialLabellingWeight) {
/*  765 */     this.m_PartialLabellingWeight = m_PartialLabellingWeight;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClassHierarchy getHierarchy() {
/*  775 */     return this.m_Hierarchy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setHierarchy(ClassHierarchy m_Hierarchy) {
/*  782 */     this.m_Hierarchy = m_Hierarchy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HashMap<String, LinkedList<String>> getPaths() {
/*  789 */     return this.m_Paths;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusActiveLearningIteration getIteration() {
/*  796 */     return this.m_Iteration;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIteration(ClusActiveLearningIteration m_Iteration) {
/*  803 */     this.m_Iteration = m_Iteration;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getIterationCounter() {
/*  810 */     return this.m_IterationCounter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setIterationCounter(int m_IterationCounter) {
/*  817 */     this.m_IterationCounter = m_IterationCounter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getAvaliableLabels() {
/*  824 */     return this.m_LabelsAvailable;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAvailableLabels(int m_TotalLabels) {
/*  831 */     this.m_LabelsAvailable = m_TotalLabels;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getBudgetSpent() {
/*  838 */     return this.m_BudgetSpent;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBudgetSpent(double m_BudgetSpent) {
/*  845 */     this.m_BudgetSpent = m_BudgetSpent;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getLabelsQueried() {
/*  852 */     return this.m_LabelsQueried;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLabelsQueried(int m_labelsQueried) {
/*  858 */     this.m_LabelsQueried = m_labelsQueried;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSemiPartialLabelling() {
/*  865 */     return this.m_SemiPartialLabelling;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSemiPartialLabelling(boolean m_SemiPartialLabelling) {
/*  872 */     this.m_SemiPartialLabelling = m_SemiPartialLabelling;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HashMap<String, Integer> getLabelQueryCounter() {
/*  879 */     return this.m_LabelQueryCounter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLabelQueryCounter(HashMap<String, Integer> m_LabelQueryCounter) {
/*  886 */     this.m_LabelQueryCounter = m_LabelQueryCounter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RowData getLabeledData() {
/*  893 */     return this.m_LabeledData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLabeledData(RowData m_LabeledData) {
/*  900 */     this.m_LabeledData = m_LabeledData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean[] getRemovedIndex() {
/*  907 */     return this.m_RemovedIndex;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRemovedIndex(boolean[] m_RemovedIndex) {
/*  914 */     this.m_RemovedIndex = m_RemovedIndex;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getLabelsAnswered() {
/*  921 */     return this.m_LabelsAnswered;
/*      */   }
/*      */   
/*      */   public void setLabelsAnswered(int labelsAnswered) {
/*  925 */     this.m_LabelsAnswered = labelsAnswered;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPositiveQueried() {
/*  932 */     return this.m_PositiveQueried;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPositiveQueried(int m_PositiveQueried) {
/*  939 */     this.m_PositiveQueried = m_PositiveQueried;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getNegativeQueried() {
/*  946 */     return this.m_NegativeQueried;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNegativeQueried(int m_NegativeQueried) {
/*  953 */     this.m_NegativeQueried = m_NegativeQueried;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getNegativeAnswered() {
/*  960 */     return this.m_NegativeAnswered;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNegativeAnswered(int m_NegativeAnswered) {
/*  967 */     this.m_NegativeAnswered = m_NegativeAnswered;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getName() {
/*  974 */     return this.m_Name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setName(String m_Name) {
/*  981 */     this.m_Name = m_Name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getQueryBuildingTimeTotal() {
/*  988 */     return this.m_QueryBuildingTimeTotal;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setQueryBuildingTimeTotal(long m_QueryBuildingTime) {
/*  995 */     this.m_QueryBuildingTimeTotal = m_QueryBuildingTime;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getBudgetLeftovers() {
/* 1002 */     return this.m_BudgetLeftovers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBudgetLeftovers(double m_BudgetLeftovers) {
/* 1009 */     this.m_BudgetLeftovers = m_BudgetLeftovers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HashMap<String, Double> getLabelsCost() {
/* 1016 */     return this.m_LabelsCost;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLabelsCost(HashMap<String, Double> m_LabelsCost) {
/* 1023 */     this.m_LabelsCost = m_LabelsCost;
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algo\ClusActiveLearningAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */