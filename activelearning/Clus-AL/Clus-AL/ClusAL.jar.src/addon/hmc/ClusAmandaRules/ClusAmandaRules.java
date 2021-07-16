/*     */ package addon.hmc.ClusAmandaRules;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.algo.rules.ClusRule;
/*     */ import clus.algo.rules.ClusRuleClassifier;
/*     */ import clus.algo.rules.ClusRuleSet;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.ext.hierarchical.WHTDStatistic;
/*     */ import clus.main.ClusOutput;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.model.test.InverseNumericTest;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.model.test.NumericTest;
/*     */ import clus.model.test.SubsetTest;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Date;
/*     */ import jeans.util.MStreamTokenizer;
/*     */ import jeans.util.StringUtils;
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
/*     */ 
/*     */ public class ClusAmandaRules
/*     */   implements CMDLineArgsProvider
/*     */ {
/*  49 */   private static String[] g_Options = new String[] { "sgene" };
/*  50 */   private static int[] g_OptionArities = new int[] { 1 };
/*     */   
/*     */   protected Clus m_Clus;
/*     */   
/*     */   public void run(String[] args) throws IOException, ClusException {
/*  55 */     this.m_Clus = new Clus();
/*  56 */     Settings sett = this.m_Clus.getSettings();
/*  57 */     CMDLineArgs cargs = new CMDLineArgs(this);
/*  58 */     cargs.process(args);
/*  59 */     if (cargs.allOK()) {
/*  60 */       sett.setDate(new Date());
/*  61 */       sett.setAppName(cargs.getMainArg(0));
/*  62 */       this.m_Clus.initSettings(cargs);
/*     */       
/*  64 */       ClusRuleClassifier clusRuleClassifier = new ClusRuleClassifier(this.m_Clus);
/*  65 */       this.m_Clus.initialize(cargs, (ClusInductionAlgorithmType)clusRuleClassifier);
/*  66 */       ClusRuleSet set = loadRules(cargs.getMainArg(1));
/*  67 */       ClusRun cr = this.m_Clus.partitionData();
/*  68 */       pruneInsignificantRules(cr, set);
/*  69 */       if (cargs.hasOption("sgene")) {
/*  70 */         showValuesForGene(cr, set, cargs.getOptionValue("sgene"));
/*     */       } else {
/*  72 */         evaluateRuleSet(cr, set);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public ClusRuleSet loadRules(String file) throws IOException, ClusException {
/*  78 */     ClusRuleSet set = new ClusRuleSet(this.m_Clus.getStatManager());
/*     */     
/*  80 */     ClusStatistic default_stat = this.m_Clus.getStatManager().createStatistic(3);
/*  81 */     default_stat.calcMean();
/*  82 */     set.setTargetStat(default_stat);
/*     */     
/*  84 */     MStreamTokenizer tokens = new MStreamTokenizer(file);
/*  85 */     while (tokens.hasMoreTokens()) {
/*  86 */       String token = tokens.getToken();
/*  87 */       if (token.equalsIgnoreCase("RULE")) {
/*  88 */         String number = tokens.getToken();
/*  89 */         if (StringUtils.isInteger(number) && tokens.isNextToken(':')) {
/*  90 */           System.out.println("Reading rule: " + number);
/*  91 */           ClusRule rule = loadRule(tokens, number);
/*  92 */           set.add(rule);
/*  93 */           rule.printModel();
/*     */         } 
/*     */       } 
/*     */     } 
/*  97 */     return set;
/*     */   }
/*     */   
/*     */   public ClusRule loadRule(MStreamTokenizer tokens, String number) throws IOException, ClusException {
/* 101 */     ClusRule rule = new AmandaRule(this.m_Clus.getStatManager());
/* 102 */     ClusSchema schema = this.m_Clus.getSchema();
/* 103 */     while (tokens.hasMoreTokens()) {
/* 104 */       SubsetTest subsetTest; String attrname = tokens.getToken();
/* 105 */       if (attrname.equals("->")) {
/* 106 */         if (!tokens.getToken().equalsIgnoreCase("CLASS")) {
/* 107 */           throw new ClusException("'Class' expected after '->' while reading rule " + number);
/*     */         }
/* 109 */         addClass(rule, tokens.getToken());
/*     */         break;
/*     */       } 
/* 112 */       ClusAttrType type = schema.getAttrType(attrname);
/* 113 */       if (type == null) {
/* 114 */         throw new ClusException("Can't find attribute: '" + attrname + "' while reading rule " + number);
/*     */       }
/* 116 */       NodeTest test = null;
/* 117 */       if (type instanceof clus.data.type.NumericAttrType) {
/* 118 */         String compare = tokens.getToken();
/* 119 */         String bound_str = tokens.getToken();
/*     */         try {
/* 121 */           double bound = Double.parseDouble(bound_str);
/* 122 */           if (compare.equals(">")) {
/* 123 */             NumericTest numericTest = new NumericTest(type, bound, 0.0D);
/*     */           } else {
/* 125 */             InverseNumericTest inverseNumericTest = new InverseNumericTest(type, bound, 0.0D);
/*     */           } 
/* 127 */         } catch (NumberFormatException e) {
/* 128 */           throw new ClusException("Error reading numeric bound: '" + bound_str + "' in test on '" + type.getName() + "' while reading rule " + number);
/*     */         }
/*     */       
/* 131 */       } else if (tokens.isNextToken("=")) {
/* 132 */         NominalAttrType nominal = (NominalAttrType)type;
/* 133 */         boolean[] isin = new boolean[nominal.getNbValues()];
/* 134 */         String value = tokens.getToken();
/* 135 */         Integer res = nominal.getValueIndex(value);
/* 136 */         if (res == null) {
/* 137 */           throw new ClusException("Value '" + value + "' not in domain of '" + type.getName() + "' while reading rule " + number);
/*     */         }
/* 139 */         isin[res.intValue()] = true;
/* 140 */         subsetTest = new SubsetTest(nominal, 1, isin, 0.0D);
/*     */       } else {
/* 142 */         throw new ClusException("Expected '=' after nominal attribute '" + type.getName() + "' while reading rule " + number);
/*     */       } 
/*     */       
/* 145 */       rule.addTest((NodeTest)subsetTest);
/*     */     } 
/* 147 */     return rule;
/*     */   }
/*     */   
/*     */   void addClass(ClusRule rule, String classstr) throws IOException, ClusException {
/* 151 */     WHTDStatistic stat = (WHTDStatistic)this.m_Clus.getStatManager().createStatistic(3);
/* 152 */     stat.calcMean();
/* 153 */     ClassHierarchy hier = stat.getHier();
/* 154 */     ClassesTuple tuple = new ClassesTuple(classstr, hier.getType().getTable());
/* 155 */     tuple.addHierarchyIndices(hier);
/* 156 */     stat.setMeanTuple(tuple);
/* 157 */     rule.setTargetStat((ClusStatistic)stat);
/*     */   }
/*     */   
/*     */   void pruneInsignificantRules(ClusRun cr, ClusRuleSet rules) throws IOException, ClusException {
/* 161 */     RowData prune = (RowData)cr.getPruneSet();
/* 162 */     if (prune == null)
/* 163 */       return;  WHTDStatistic stat = (WHTDStatistic)this.m_Clus.getStatManager().createStatistic(3);
/* 164 */     WHTDStatistic global = (WHTDStatistic)stat.cloneStat();
/* 165 */     prune.calcTotalStat((ClusStatistic)global);
/* 166 */     global.calcMean();
/* 167 */     Settings sett = this.m_Clus.getSettings();
/* 168 */     boolean useBonferroni = sett.isUseBonferroni();
/* 169 */     double sigLevel = sett.getHierPruneInSig();
/* 170 */     if (sigLevel == 0.0D)
/* 171 */       return;  for (int i = 0; i < rules.getModelSize(); i++) {
/* 172 */       ClusRule rule = rules.getRule(i);
/* 173 */       RowData data = rule.computeCovered(prune);
/* 174 */       WHTDStatistic orig = (WHTDStatistic)rule.getTargetStat();
/* 175 */       WHTDStatistic valid = (WHTDStatistic)orig.cloneStat();
/* 176 */       for (int j = 0; j < data.getNbRows(); j++) {
/* 177 */         DataTuple tuple = data.getTuple(j);
/* 178 */         valid.updateWeighted(tuple, j);
/*     */       } 
/* 180 */       valid.calcMean();
/* 181 */       WHTDStatistic pred = (WHTDStatistic)orig.cloneStat();
/* 182 */       pred.copy((ClusStatistic)orig);
/* 183 */       pred.calcMean();
/* 184 */       pred.setValidationStat(valid);
/* 185 */       pred.setGlobalStat(global);
/* 186 */       if (useBonferroni) {
/* 187 */         pred.setSigLevel(sigLevel / rules.getModelSize());
/*     */       } else {
/* 189 */         pred.setSigLevel(sigLevel);
/*     */       } 
/* 191 */       pred.setMeanTuple(orig.getDiscretePred());
/* 192 */       pred.performSignificanceTest();
/* 193 */       rule.setTargetStat((ClusStatistic)pred);
/*     */     } 
/*     */   }
/*     */   
/*     */   void evaluateRuleSet(ClusRun cr, ClusRuleSet rules) throws IOException, ClusException {
/* 198 */     Settings sett = this.m_Clus.getSettings();
/* 199 */     ClusOutput output = new ClusOutput(sett.getAppName() + ".rules.out", this.m_Clus.getSchema(), sett);
/* 200 */     ClusModelInfo info = cr.addModelInfo(0);
/* 201 */     info.setStatManager(this.m_Clus.getStatManager());
/* 202 */     info.setModel((ClusModel)rules);
/* 203 */     info.setName("Rules");
/* 204 */     this.m_Clus.addModelErrorMeasures(cr);
/* 205 */     this.m_Clus.calcError(cr, null);
/* 206 */     output.writeHeader();
/* 207 */     output.writeOutput(cr, true, sett.isOutTrainError());
/* 208 */     output.close();
/*     */   }
/*     */   
/*     */   public void showValuesForGene(ClusRun cr, ClusRuleSet rules, String gene) throws IOException, ClusException {
/* 212 */     DataTuple tuple = null;
/* 213 */     if (cr.getTrainingSet() != null) {
/* 214 */       System.out.println("Searching for gene in training set");
/* 215 */       tuple = ((RowData)cr.getTrainingSet()).findTupleByKey(gene);
/*     */     } 
/* 217 */     if (tuple == null && cr.getPruneSet() != null) {
/* 218 */       System.out.println("Searching for gene in validation set");
/* 219 */       tuple = ((RowData)cr.getPruneSet()).findTupleByKey(gene);
/*     */     } 
/* 221 */     if (tuple == null && cr.getTestSet() != null) {
/* 222 */       System.out.println("Searching for gene in test set");
/* 223 */       tuple = cr.getTestSet().findTupleByKey(gene);
/*     */     } 
/* 225 */     if (tuple == null) {
/* 226 */       System.out.println("Can't find gene in data set");
/*     */     } else {
/* 228 */       Settings sett = this.m_Clus.getSettings();
/* 229 */       PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(sett.getAppName() + ".sgene")));
/* 230 */       for (int i = 0; i < rules.getModelSize(); i++) {
/* 231 */         AmandaRule rule = (AmandaRule)rules.getRule(i);
/* 232 */         rule.printModel(wrt);
/* 233 */         wrt.println();
/* 234 */         if (rule.covers(tuple)) { wrt.println("Rule covers gene: " + gene); }
/* 235 */         else { wrt.println("Rule does not cover: " + gene); }
/* 236 */          wrt.println();
/* 237 */         for (int j = 0; j < rule.getModelSize(); j++) {
/* 238 */           NodeTest test = rule.getTest(j);
/* 239 */           ClusAttrType type = test.getType();
/* 240 */           wrt.println("Test " + j + ": " + test.getString() + " -> value for " + gene + " = " + type.getString(tuple) + " Covers: " + rule.doTest(test, tuple));
/*     */         } 
/* 242 */         wrt.println();
/*     */       } 
/* 244 */       wrt.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   public String[] getOptionArgs() {
/* 249 */     return g_Options;
/*     */   }
/*     */   
/*     */   public int[] getOptionArgArities() {
/* 253 */     return g_OptionArities;
/*     */   }
/*     */   
/*     */   public int getNbMainArgs() {
/* 257 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void showHelp() {}
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/* 265 */       ClusAmandaRules rules = new ClusAmandaRules();
/* 266 */       rules.run(args);
/* 267 */     } catch (IOException io) {
/* 268 */       System.out.println("IO Error: " + io.getMessage());
/* 269 */     } catch (ClusException cl) {
/* 270 */       System.out.println("Error: " + cl.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\addon\hmc\ClusAmandaRules\ClusAmandaRules.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */