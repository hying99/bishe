/*     */ package clus.activelearning.data;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.ext.hierarchical.ClassesValue;
/*     */ import clus.util.ClusException;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
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
/*     */ public class OracleAnswer
/*     */ {
/*  29 */   private HashSet<String> m_PossibleLabels = new HashSet<>();
/*  30 */   private final HashSet<String> m_NegativeAnswers = new HashSet<>();
/*  31 */   private final HashSet<String> m_PositiveAnswers = new HashSet<>();
/*  32 */   private final HashSet<String> m_NewPositiveAnswers = new HashSet<>();
/*  33 */   private final HashSet<String> m_NewNegativeAnswers = new HashSet<>();
/*  34 */   private final HashSet<String> m_NewNegativeAnswersNoHierarchy = new HashSet<>(); private final HashSet<String> m_PositiveInfered; private final HashSet<String> m_NegativeInfered; public OracleAnswer(ClassesTuple ct) {
/*  35 */     for (ClassesValue m_Tuple : ct.getM_Tuple()) {
/*  36 */       this.m_PossibleLabels.add(m_Tuple.toString());
/*     */     }
/*     */ 
/*     */     
/*  40 */     this.m_QueryCounter = 0;
/*  41 */     this.m_InstanceCost = null;
/*  42 */     this.m_PositiveInfered = new HashSet<>();
/*  43 */     this.m_NegativeInfered = new HashSet<>();
/*     */   }
/*     */   private Double m_InstanceCost; private int m_QueryCounter;
/*     */   public void addCost(Double cost) {
/*  47 */     if (this.m_InstanceCost == null) {
/*  48 */       this.m_InstanceCost = Double.valueOf(0.0D);
/*     */     }
/*  50 */     this.m_InstanceCost = Double.valueOf(this.m_InstanceCost.doubleValue() + cost.doubleValue());
/*     */   }
/*     */   
/*     */   public Double getInstanceCost() {
/*  54 */     return this.m_InstanceCost;
/*     */   }
/*     */   
/*     */   public HashSet<String> getAnswers() {
/*  58 */     HashSet<String> answers = (HashSet<String>)this.m_PositiveAnswers.clone();
/*  59 */     answers.addAll(this.m_NegativeAnswers);
/*  60 */     return answers;
/*     */   }
/*     */   
/*     */   public int getNewPositiveLabels() {
/*  64 */     return this.m_NewPositiveAnswers.size();
/*     */   }
/*     */   
/*     */   public int getNewNegativeLabels() {
/*  68 */     return this.m_NewNegativeAnswers.size();
/*     */   }
/*     */   
/*     */   public int getNewNegativeLabelsNoHierarchy() {
/*  72 */     return this.m_NewNegativeAnswersNoHierarchy.size();
/*     */   }
/*     */   
/*     */   public HashSet<String> getNewAnsweredLabels() {
/*  76 */     HashSet<String> newAnswers = (HashSet<String>)this.m_NewPositiveAnswers.clone();
/*  77 */     newAnswers.addAll(this.m_NewNegativeAnswers);
/*  78 */     return newAnswers;
/*     */   }
/*     */   
/*     */   public boolean queriedBefore(String label) {
/*  82 */     return (this.m_PositiveAnswers.contains(label) || this.m_NegativeAnswers.contains(label) || this.m_NewPositiveAnswers.contains(label) || this.m_NewNegativeAnswers.contains(label));
/*     */   }
/*     */   
/*     */   public float getUnasweredSubtree(ClassHierarchy h, String label) {
/*  86 */     HashSet<String> descendants = null;
/*  87 */     float descendantsSize = 0.0F;
/*     */     
/*     */     try {
/*  90 */       descendants = new HashSet<>(h.getDescendantsString(label));
/*  91 */       descendantsSize = descendants.size();
/*  92 */       descendants.removeAll(this.m_PositiveAnswers);
/*  93 */       descendants.removeAll(this.m_NegativeAnswers);
/*     */     }
/*  95 */     catch (ClusException ex) {
/*  96 */       Logger.getLogger(OracleAnswer.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */     } 
/*     */ 
/*     */     
/* 100 */     return descendantsSize / descendants.size();
/*     */   }
/*     */   
/*     */   public boolean queriedPositively(String label) {
/* 104 */     return this.m_PositiveAnswers.contains(label);
/*     */   }
/*     */   
/*     */   public boolean queriedNegatively(String label) {
/* 108 */     return this.m_NegativeAnswers.contains(label);
/*     */   }
/*     */   
/*     */   public void finishAnswering() {
/* 112 */     this.m_PositiveAnswers.addAll(this.m_NewPositiveAnswers);
/* 113 */     this.m_NegativeAnswers.addAll(this.m_NewNegativeAnswers);
/* 114 */     this.m_NewPositiveAnswers.clear();
/* 115 */     this.m_NewNegativeAnswers.clear();
/* 116 */     this.m_NewNegativeAnswersNoHierarchy.clear();
/* 117 */     this.m_NegativeInfered.clear();
/* 118 */     this.m_PositiveInfered.clear();
/*     */   }
/*     */   
/*     */   public void reset() {
/* 122 */     this.m_PositiveAnswers.clear();
/* 123 */     this.m_NewPositiveAnswers.clear();
/* 124 */     this.m_NegativeAnswers.clear();
/* 125 */     this.m_NewNegativeAnswers.clear();
/* 126 */     this.m_NewNegativeAnswersNoHierarchy.clear();
/*     */   }
/*     */   
/*     */   public HashSet<String> getNewPositiveAnswers() {
/* 130 */     return this.m_NewPositiveAnswers;
/*     */   }
/*     */   
/*     */   public boolean queriedBefore() {
/* 134 */     return (this.m_PositiveAnswers.size() > 0 || this.m_NegativeAnswers.size() > 0);
/*     */   }
/*     */   
/*     */   public HashSet<String> getPositiveAnswers() {
/* 138 */     HashSet<String> positiveAnswers = (HashSet<String>)this.m_PositiveAnswers.clone();
/* 139 */     positiveAnswers.addAll(this.m_NewPositiveAnswers);
/* 140 */     return positiveAnswers;
/*     */   }
/*     */   
/*     */   public HashSet<String> getNegativeAnswers() {
/* 144 */     HashSet<String> negativeAnswers = (HashSet<String>)this.m_NegativeAnswers.clone();
/* 145 */     negativeAnswers.addAll(this.m_NewNegativeAnswers);
/* 146 */     return negativeAnswers;
/*     */   }
/*     */   
/*     */   public boolean isDone(int hierarchySize) {
/* 150 */     return (hierarchySize == this.m_PositiveAnswers.size() + this.m_NegativeAnswers.size());
/*     */   }
/*     */   
/*     */   public String getDeepestAncestor(String label, ClassHierarchy h) throws ClusException {
/* 154 */     ClassTerm ct = h.getClassTermTree(new ClassesValue(label, h.getType().getTable()));
/*     */     
/*     */     while (true) {
/* 157 */       String parentLabel = ct.getParent(0).toPathString();
/* 158 */       ct = h.getClassTermTree(new ClassesValue(parentLabel, h.getType().getTable()));
/* 159 */       if (queriedBefore(parentLabel)) {
/* 160 */         return parentLabel;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public HashSet<String> getNewLabels() {
/* 166 */     HashSet<String> answeredClasses = (HashSet<String>)this.m_NewPositiveAnswers.clone();
/* 167 */     answeredClasses.addAll(this.m_NewNegativeAnswersNoHierarchy);
/* 168 */     return answeredClasses;
/*     */   }
/*     */   
/*     */   public int getNewLabelsAmount() {
/* 172 */     HashSet<String> answeredClasses = (HashSet<String>)this.m_NewPositiveAnswers.clone();
/* 173 */     answeredClasses.addAll(this.m_NewNegativeAnswers);
/* 174 */     return answeredClasses.size();
/*     */   }
/*     */   
/*     */   public void addNewPositiveLabel(String label) throws ClusException {
/* 178 */     this.m_NewPositiveAnswers.add(label);
/*     */   }
/*     */   
/*     */   public void addNewPositiveLabelHCAL(LinkedList<String> path) throws ClusException {
/* 182 */     LinkedList<String> realpath = new LinkedList<>(path);
/* 183 */     HashSet<String> pathRemoved = (HashSet<String>)getPositiveAnswers().clone();
/* 184 */     pathRemoved.addAll(getNewPositiveAnswers());
/* 185 */     realpath.removeAll(pathRemoved);
/* 186 */     this.m_NewPositiveAnswers.addAll(realpath);
/*     */   }
/*     */   
/*     */   public void addNewNegativeLabelHCAL(String label, ClassHierarchy h) throws ClusException {
/* 190 */     LinkedList<String> descendants = h.getDescendantsString(label);
/* 191 */     HashSet<String> subTreeRemoved = (HashSet<String>)getNegativeAnswers().clone();
/* 192 */     subTreeRemoved.addAll(getNegativeAnswers());
/* 193 */     descendants.removeAll(subTreeRemoved);
/* 194 */     this.m_NewNegativeAnswers.addAll(descendants);
/* 195 */     this.m_NewNegativeAnswersNoHierarchy.add(label);
/*     */   }
/*     */   public void addNewNegativeLabel(String label, ClassHierarchy h) throws ClusException {
/* 198 */     LinkedList<String> descendants = h.getDescendantsString(label);
/* 199 */     this.m_NewNegativeAnswers.addAll(descendants);
/* 200 */     this.m_NewNegativeAnswersNoHierarchy.add(label);
/*     */   }
/*     */   
/*     */   public void addPositiveInferedLabel(String label) {
/* 204 */     this.m_PositiveInfered.add(label);
/*     */   }
/*     */   
/*     */   public void addNegativeInferedLabel(String answer, ClassHierarchy h) throws ClusException {
/* 208 */     LinkedList<String> descendants = h.getDescendantsString(answer);
/* 209 */     this.m_NegativeInfered.addAll(descendants);
/*     */   }
/*     */   
/*     */   public boolean inferedBefore(String answer) {
/* 213 */     return (this.m_PositiveInfered.contains(answer) || this.m_NegativeInfered.contains(answer));
/*     */   }
/*     */   
/*     */   public boolean hasBeenPartiallyAnswered(ClassHierarchy h, String label) throws ClusException {
/* 217 */     LinkedList<String> ancestors = h.getAncestorsString(label);
/* 218 */     ancestors.remove(label);
/* 219 */     for (String ancestor : ancestors) {
/* 220 */       if (this.m_PositiveAnswers.contains(ancestor) || this.m_NewPositiveAnswers.contains(ancestor) || this.m_NegativeAnswers.contains(ancestor) || this.m_NewNegativeAnswers.contains(ancestor)) {
/* 221 */         return true;
/*     */       }
/*     */     } 
/* 224 */     return false;
/*     */   }
/*     */   
/*     */   public HashSet<String> getPositiveChildren(ClassHierarchy h) throws ClusException {
/* 228 */     HashSet<String> positives = this.m_PositiveAnswers;
/* 229 */     HashSet<String> children = new HashSet<>();
/* 230 */     for (String positive : positives) {
/* 231 */       children.addAll(h.getChildren(positive));
/*     */     }
/*     */     
/* 234 */     return children;
/*     */   }
/*     */   
/*     */   public void labelTuple(DataTuple tuple, ClassHierarchy h) throws ClusException {
/* 238 */     HashSet<String> newLabels = getNewPositiveAnswers();
/* 239 */     if (!newLabels.isEmpty()) {
/* 240 */       for (String newLabel : newLabels) {
/* 241 */         tuple.addLabel(newLabel, h.getType().getTable(), h);
/*     */       }
/*     */     } else {
/* 244 */       tuple.addNeutralLabel();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void inferTuple(DataTuple tuple, ClassHierarchy h) throws ClusException {
/* 249 */     HashSet<String> newLabels = this.m_PositiveInfered;
/* 250 */     if (!newLabels.isEmpty()) {
/* 251 */       for (String newLabel : newLabels) {
/* 252 */         tuple.addLabel(newLabel, h.getType().getTable(), h);
/*     */       }
/*     */     } else {
/* 255 */       tuple.addNeutralLabel();
/*     */     } 
/*     */   }
/*     */   
/*     */   public double getUnansweredAmount(int hierarchySize) {
/* 260 */     return (hierarchySize - this.m_NegativeAnswers.size() - this.m_PositiveAnswers.size());
/*     */   }
/*     */ 
/*     */   
/*     */   public double getUnanweredProb(int hierarchySize) {
/* 265 */     return 1.0D - (this.m_NegativeAnswers.size() + this.m_PositiveAnswers.size()) / hierarchySize;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 270 */     String str = "";
/* 271 */     str = str + "m_PositiveAnswer" + this.m_PositiveAnswers.toString() + "\n";
/* 272 */     str = str + "m_NegativeAnswers" + this.m_NegativeAnswers.toString() + "\n";
/* 273 */     str = str + "m_PossibleLabels" + getPossibleLabels() + "\n";
/* 274 */     str = str + "m_newPositive" + this.m_NewPositiveAnswers + "\n";
/* 275 */     str = str + "m_newNegative" + this.m_NewNegativeAnswers + "\n";
/* 276 */     str = str + "m_newNegativeNoHierarchy" + this.m_NewNegativeAnswersNoHierarchy + "\n";
/* 277 */     str = str + "m_newPositiveInfered" + this.m_PositiveInfered + "\n";
/* 278 */     str = str + "m_newNegativeInfered" + this.m_NegativeInfered + "\n";
/* 279 */     return str;
/*     */   }
/*     */   
/*     */   public void printOracleAnswer() {
/* 283 */     System.out.println("m_PositiveAnswer" + this.m_PositiveAnswers.toString());
/* 284 */     System.out.println("m_NegativeAnswers" + this.m_NegativeAnswers.toString());
/* 285 */     System.out.println("m_PossibleLabels" + getPossibleLabels().toString());
/* 286 */     System.out.println("m_newPositive" + this.m_NewPositiveAnswers);
/* 287 */     System.out.println("m_newNegative" + this.m_NewNegativeAnswers);
/* 288 */     System.out.println("m_newNegativeNoHierarchy" + this.m_NewNegativeAnswersNoHierarchy);
/* 289 */     System.out.println("m_newPositiveInfered" + this.m_PositiveInfered);
/* 290 */     System.out.println("m_newNegativeInfered" + this.m_NegativeInfered);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getNegativeInferedSize() {
/* 295 */     return this.m_NegativeInfered.size();
/*     */   }
/*     */   
/*     */   public double getPositiveInferedSize() {
/* 299 */     return this.m_PositiveInfered.size();
/*     */   }
/*     */   
/*     */   public double getCorrectPositiveInferedSize() {
/* 303 */     double totalInfered = this.m_PositiveInfered.size();
/* 304 */     if (totalInfered == 0.0D) {
/* 305 */       return 0.0D;
/*     */     }
/* 307 */     HashSet<String> intersection = new HashSet<>(this.m_PositiveInfered);
/* 308 */     intersection.retainAll(this.m_PossibleLabels);
/* 309 */     return intersection.size();
/*     */   }
/*     */   
/*     */   public double getCorrectNegativeInferedSize() {
/* 313 */     double totalInfered = this.m_NegativeInfered.size();
/* 314 */     if (totalInfered == 0.0D) {
/* 315 */       return 0.0D;
/*     */     }
/* 317 */     HashSet<String> intersection = new HashSet<>(this.m_NegativeInfered);
/* 318 */     intersection.removeAll(this.m_PossibleLabels);
/* 319 */     return intersection.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSSMALvote(String label, boolean positiveOrNegative) {
/* 324 */     int vote = 0;
/* 325 */     if (this.m_PositiveAnswers.contains(label) || this.m_NewPositiveAnswers.contains(label)) {
/* 326 */       vote = 1;
/* 327 */     } else if (this.m_NegativeAnswers.contains(label) || this.m_NewNegativeAnswers.contains(label)) {
/* 328 */       vote = -1;
/*     */     } 
/* 330 */     if (!positiveOrNegative) {
/* 331 */       vote *= -1;
/*     */     }
/* 333 */     return vote;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashSet<String> getPossibleLabels() {
/* 340 */     return this.m_PossibleLabels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPossibleLabels(HashSet<String> m_PossibleLabels) {
/* 347 */     this.m_PossibleLabels = m_PossibleLabels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getQueryCounter() {
/* 354 */     return this.m_QueryCounter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setQueryCounter(int m_QueryCounter) {
/* 361 */     this.m_QueryCounter = m_QueryCounter;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\data\OracleAnswer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */