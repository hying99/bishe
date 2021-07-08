/*     */ package clus.ext.ensembles;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.error.Accuracy;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.error.RelativeError;
/*     */ import clus.model.ClusModel;
/*     */ import clus.selection.OOBSelection;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class ClusEnsembleFeatureRanking
/*     */ {
/*  30 */   HashMap m_AllAttributes = new HashMap<>();
/*  31 */   HashMap m_FeatureRankByName = new HashMap<>();
/*  32 */   TreeMap m_FeatureRanks = new TreeMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void initializeAttributes(ClusAttrType[] descriptive) {
/*  40 */     int num = -1;
/*  41 */     int nom = -1;
/*     */     
/*  43 */     for (int i = 0; i < descriptive.length; i++) {
/*  44 */       ClusAttrType type = descriptive[i];
/*  45 */       if (!type.isDisabled()) {
/*  46 */         double[] info = new double[3];
/*  47 */         if (type.getTypeIndex() == 0) {
/*  48 */           nom++;
/*  49 */           info[0] = 0.0D;
/*  50 */           info[1] = nom;
/*     */         } 
/*  52 */         if (type.getTypeIndex() == 1) {
/*  53 */           num++;
/*  54 */           info[0] = 1.0D;
/*  55 */           info[1] = num;
/*     */         } 
/*  57 */         info[2] = 0.0D;
/*     */         
/*  59 */         this.m_AllAttributes.put(type.getName(), info);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sortFeatureRanks() {
/*  66 */     Set attributes = this.m_AllAttributes.keySet();
/*  67 */     Iterator<String> iter = attributes.iterator();
/*  68 */     while (iter.hasNext()) {
/*  69 */       String attr = iter.next();
/*  70 */       double score = ((double[])this.m_AllAttributes.get(attr))[2] / ClusEnsembleInduce.getMaxNbBags();
/*  71 */       ArrayList<String> attrs = new ArrayList();
/*  72 */       if (this.m_FeatureRanks.containsKey(Double.valueOf(score)))
/*  73 */         attrs = (ArrayList)this.m_FeatureRanks.get(Double.valueOf(score)); 
/*  74 */       attrs.add(attr);
/*  75 */       this.m_FeatureRanks.put(Double.valueOf(score), attrs);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void convertRanksByName() {
/*  80 */     TreeMap sorted = (TreeMap)this.m_FeatureRanks.clone();
/*  81 */     while (!sorted.isEmpty()) {
/*  82 */       double score = ((Double)sorted.lastKey()).doubleValue();
/*  83 */       ArrayList attrs = new ArrayList();
/*  84 */       attrs = (ArrayList)sorted.get(sorted.lastKey());
/*  85 */       for (int i = 0; i < attrs.size(); i++)
/*  86 */         this.m_FeatureRankByName.put(attrs.get(i), Double.valueOf(score)); 
/*  87 */       sorted.remove(sorted.lastKey());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void printRanking(String fname) throws IOException {
/*  92 */     TreeMap sorted = (TreeMap)this.m_FeatureRanks.clone();
/*  93 */     File franking = new File(fname + ".fimp");
/*  94 */     FileWriter wrtr = new FileWriter(franking);
/*  95 */     wrtr.write("Ranking via Random Forests\n");
/*  96 */     wrtr.write("--------------------------\n");
/*  97 */     while (!sorted.isEmpty()) {
/*     */       
/*  99 */       wrtr.write(writeRow((ArrayList)sorted.get(sorted.lastKey()), ((Double)sorted.lastKey()).doubleValue()));
/* 100 */       sorted.remove(sorted.lastKey());
/*     */     } 
/* 102 */     wrtr.flush();
/* 103 */     wrtr.close();
/* 104 */     System.out.println("Feature Ranking via Random Forests written in " + franking.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData createRandomizedOOBdata(OOBSelection selection, RowData data, int type, int position) {
/* 115 */     RowData result = data;
/* 116 */     Random rndm = new Random(data.getNbRows());
/* 117 */     for (int i = 0; i < result.getNbRows(); i++) {
/*     */       
/* 119 */       int rnd = i + rndm.nextInt(result.getNbRows() - i);
/* 120 */       DataTuple first = result.getTuple(i);
/* 121 */       DataTuple second = result.getTuple(rnd);
/* 122 */       if (type == 0)
/* 123 */       { int swap = first.getIntVal(position);
/* 124 */         first.setIntVal(second.getIntVal(position), position);
/* 125 */         second.setIntVal(swap, position); }
/* 126 */       else if (type == 1)
/* 127 */       { double swap = first.getDoubleVal(position);
/* 128 */         first.setDoubleVal(second.getDoubleVal(position), position);
/* 129 */         second.setDoubleVal(swap, position); }
/* 130 */       else { System.err.println("Error at the Random Permutations"); }
/*     */     
/* 132 */     }  return result;
/*     */   }
/*     */   
/*     */   public void fillWithAttributesInTree(ClusNode node, ArrayList<String> attributes) {
/* 136 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 137 */       String att = node.getTest().getType().getName();
/* 138 */       if (!attributes.contains(att))
/*     */       {
/* 140 */         attributes.add(att);
/*     */       }
/* 142 */       fillWithAttributesInTree((ClusNode)node.getChild(i), attributes);
/*     */     } 
/*     */   }
/*     */   
/*     */   public double calcAverageError(RowData data, ClusModel model) throws ClusException {
/* 147 */     ClusSchema schema = data.getSchema();
/*     */     
/* 149 */     ClusErrorList error = new ClusErrorList();
/* 150 */     NumericAttrType[] num = schema.getNumericAttrUse(3);
/* 151 */     NominalAttrType[] nom = schema.getNominalAttrUse(3);
/* 152 */     if (nom.length != 0)
/* 153 */     { error.addError((ClusError)new Accuracy(error, nom)); }
/* 154 */     else if (num.length != 0)
/*     */     
/* 156 */     { error.addError((ClusError)new RelativeError(error, num)); }
/* 157 */     else { System.err.println("Supported only nominal or numeric targets!"); }
/*     */     
/* 159 */     schema.attachModel(model);
/*     */     
/* 161 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 162 */       DataTuple tuple = data.getTuple(i);
/* 163 */       ClusStatistic pred = model.predictWeighted(tuple);
/* 164 */       error.addExample(tuple, pred);
/*     */     } 
/*     */     
/* 167 */     double err = error.getFirstError().getModelError();
/* 168 */     return err;
/*     */   }
/*     */ 
/*     */   
/*     */   public String writeRow(ArrayList<String> attributes, double value) {
/* 173 */     String output = "";
/* 174 */     for (int i = 0; i < attributes.size(); i++) {
/* 175 */       String attr = attributes.get(i);
/* 176 */       attr = attr.replaceAll("\\[", "");
/* 177 */       attr = attr.replaceAll("\\]", "");
/* 178 */       output = output + attr + "\t" + value + "\n";
/*     */     } 
/* 180 */     return output;
/*     */   }
/*     */ 
/*     */   
/*     */   public TreeMap getFeatureRanks() {
/* 185 */     return this.m_FeatureRanks;
/*     */   }
/*     */ 
/*     */   
/*     */   public HashMap getFeatureRanksByName() {
/* 190 */     return this.m_FeatureRankByName;
/*     */   }
/*     */   
/*     */   public double[] getAttributeInfo(String attribute) {
/* 194 */     return (double[])this.m_AllAttributes.get(attribute);
/*     */   }
/*     */   
/*     */   public void putAttributeInfo(String attribute, double[] info) {
/* 198 */     this.m_AllAttributes.put(attribute, info);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ensembles\ClusEnsembleFeatureRanking.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */