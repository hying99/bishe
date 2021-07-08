/*     */ package clus.main;
/*     */ 
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
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
/*     */ public abstract class ClusModelInfoList
/*     */   implements Serializable
/*     */ {
/*     */   public static final int TRAINSET = 0;
/*     */   public static final int TESTSET = 1;
/*     */   public static final int VALIDATIONSET = 2;
/*  40 */   protected ClusModelInfo m_AllModelsMI = new ClusModelInfo("AllModels");
/*  41 */   protected ArrayList m_Models = new ArrayList();
/*     */   
/*     */   protected long m_IndTime;
/*     */   
/*     */   protected long m_PrepTime;
/*     */   
/*     */   protected long m_PruneTime;
/*     */   
/*     */   public int getNbModels() {
/*  50 */     return this.m_Models.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusModelInfo getModelInfo(int i) {
/*  60 */     if (i >= this.m_Models.size()) {
/*  61 */       return null;
/*     */     }
/*  63 */     return this.m_Models.get(i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusModelInfo getModelInfoFallback(int i, int j) {
/*  73 */     ClusModelInfo info = getModelInfo(i);
/*  74 */     if (info == null) {
/*  75 */       info = getModelInfo(j);
/*     */     }
/*  77 */     return info;
/*     */   }
/*     */   
/*     */   public ClusModelInfo getAllModelsMI() {
/*  81 */     return this.m_AllModelsMI;
/*     */   }
/*     */   
/*     */   public void resetAllModelsMI() {
/*  85 */     this.m_AllModelsMI = new ClusModelInfo("AllModels");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setModelInfo(int i, ClusModelInfo info) {
/*  95 */     this.m_Models.set(i, info);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusModel getModel(int i) {
/* 105 */     return getModelInfo(i).getModel();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getModelName(int i) {
/* 115 */     return getModelInfo(i).getName();
/*     */   }
/*     */   
/*     */   public void setModels(ArrayList models) {
/* 119 */     this.m_Models = models;
/*     */   }
/*     */   
/*     */   public void showModelInfos() {
/* 123 */     for (int i = 0; i < getNbModels(); i++) {
/* 124 */       ClusModelInfo info = getModelInfo(i);
/* 125 */       System.out.println("Model " + i + " name: '" + info.getName() + "'");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusModelInfo initModelInfo(int i) {
/* 135 */     String name = "M" + (i + 1);
/* 136 */     if (i == 0) {
/* 137 */       name = "Default";
/*     */     }
/* 139 */     if (i == 1) {
/* 140 */       name = "Original";
/*     */     }
/* 142 */     if (i == 2) {
/* 143 */       name = "Pruned";
/*     */     }
/* 145 */     ClusModelInfo inf = new ClusModelInfo(name);
/* 146 */     initModelInfo(inf);
/* 147 */     return inf;
/*     */   }
/*     */   
/*     */   public void initModelInfo(ClusModelInfo inf) {
/* 151 */     inf.setSelectedErrorsClone(getTrainError(), getTestError(), getValidationError());
/* 152 */     inf.setStatManager(getStatManager());
/*     */   }
/*     */   
/*     */   public ClusModelInfo addModelInfo(String name) {
/* 156 */     ClusModelInfo inf = new ClusModelInfo(name);
/* 157 */     addModelInfo(inf);
/* 158 */     return inf;
/*     */   }
/*     */   
/*     */   public void addModelInfo(ClusModelInfo inf) {
/* 162 */     initModelInfo(inf);
/* 163 */     this.m_Models.add(inf);
/*     */   }
/*     */   public void addModelInfo2(ClusModelInfo inf) {
/* 166 */     this.m_Models.add(inf);
/*     */   }
/*     */   
/*     */   public ClusModelInfo addModelInfo(int i) {
/* 170 */     while (i >= this.m_Models.size()) {
/* 171 */       this.m_Models.add(null);
/*     */     }
/* 173 */     ClusModelInfo inf = this.m_Models.get(i);
/* 174 */     if (inf == null) {
/* 175 */       inf = initModelInfo(i);
/* 176 */       this.m_Models.set(i, inf);
/*     */     } 
/* 178 */     return inf;
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract ClusStatManager getStatManager();
/*     */ 
/*     */   
/*     */   public abstract ClusErrorList getTrainError();
/*     */ 
/*     */   
/*     */   public abstract ClusErrorList getTestError();
/*     */ 
/*     */   
/*     */   public abstract ClusErrorList getValidationError();
/*     */ 
/*     */   
/*     */   public ArrayList cloneModels() {
/* 195 */     int nb_models = getNbModels();
/* 196 */     ArrayList<ClusModelInfo> clones = new ArrayList();
/* 197 */     for (int i = 0; i < nb_models; i++) {
/* 198 */       ClusModelInfo my = getModelInfo(i);
/* 199 */       if (my != null) {
/* 200 */         my = my.cloneModelInfo();
/*     */       }
/* 202 */       clones.add(my);
/*     */     } 
/* 204 */     return clones;
/*     */   }
/*     */   
/*     */   public void deleteModels() {
/* 208 */     int nb_models = getNbModels();
/* 209 */     for (int i = 0; i < nb_models; i++) {
/* 210 */       ClusModelInfo my = getModelInfo(i);
/* 211 */       my.deleteModel();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void checkModelInfo() {
/* 216 */     int nb_models = getNbModels();
/* 217 */     for (int i = 0; i < nb_models; i++) {
/* 218 */       ClusModelInfo my = getModelInfo(i);
/* 219 */       my.check();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean hasModel(int i) {
/* 224 */     ClusModelInfo my = getModelInfo(i);
/* 225 */     return (my.getNbModels() > 0);
/*     */   }
/*     */   
/*     */   public void copyAllModelsMIs() {
/* 229 */     ClusModelInfo allmi = getAllModelsMI();
/* 230 */     int nb_models = getNbModels();
/* 231 */     for (int i = 0; i < nb_models; i++) {
/* 232 */       ClusModelInfo my = getModelInfo(i);
/* 233 */       if (my != null) {
/* 234 */         allmi.copyModelProcessors(my);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void initModelProcessors(int type, ClusSchema schema) throws IOException, ClusException {
/* 240 */     ClusModelInfo allmi = getAllModelsMI();
/*     */     
/* 242 */     allmi.initAllModelProcessors(type, schema);
/*     */     
/* 244 */     for (int i = 0; i < getNbModels(); i++) {
/* 245 */       ClusModelInfo mi = getModelInfo(i);
/* 246 */       if (mi != null) {
/* 247 */         mi.initModelProcessors(type, schema);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void initEnsemblePredictionsWriter(int type) {
/* 253 */     ClusModelInfo mi = getModelInfo(1);
/* 254 */     mi.initEnsemblePredictionWriter(type);
/*     */   }
/*     */   
/*     */   public void termModelProcessors(int type) throws IOException, ClusException {
/* 258 */     ClusModelInfo allmi = getAllModelsMI();
/* 259 */     allmi.termAllModelProcessors(type);
/* 260 */     for (int i = 0; i < getNbModels(); i++) {
/* 261 */       ClusModelInfo mi = getModelInfo(i);
/* 262 */       if (mi != null) {
/* 263 */         mi.termModelProcessors(type);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void termEnsemblePredictionsWriter(int type) {
/* 269 */     ClusModelInfo mi = getModelInfo(1);
/* 270 */     mi.terminateEnsemblePredictionWriter(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setInductionTime(long time) {
/* 279 */     this.m_IndTime = time;
/*     */   }
/*     */   
/*     */   public final long getInductionTime() {
/* 283 */     return this.m_IndTime;
/*     */   }
/*     */   
/*     */   public final void setPruneTime(long time) {
/* 287 */     this.m_PruneTime = time;
/*     */   }
/*     */   
/*     */   public final long getPruneTime() {
/* 291 */     return this.m_PruneTime;
/*     */   }
/*     */   
/*     */   public final void setPrepareTime(long time) {
/* 295 */     this.m_PrepTime = time;
/*     */   }
/*     */   
/*     */   public final long getPrepareTime() {
/* 299 */     return this.m_PrepTime;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\main\ClusModelInfoList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */