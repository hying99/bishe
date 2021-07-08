/*     */ package clus.model;
/*     */ 
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.processor.ClusEnsemblePredictionWriter;
/*     */ import clus.model.processor.ClusModelProcessor;
/*     */ import clus.model.processor.ModelProcessorCollection;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
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
/*     */ public class ClusModelInfo
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int TRAIN_ERR = 0;
/*     */   public static final int TEST_ERR = 1;
/*     */   public static final int VALID_ERR = 2;
/*     */   public static final int XVAL_PREDS = 3;
/*     */   protected String m_Name;
/*     */   protected boolean m_HasName = false;
/*     */   protected boolean m_ShouldSave = true;
/*     */   protected boolean m_ShouldWritePredictions = true;
/*     */   protected boolean m_ShouldPruneInvalid;
/*     */   protected int m_ModelSize;
/*     */   protected int m_NbModels;
/*     */   protected double m_Score;
/*     */   protected ClusModel m_Model;
/*     */   public ClusErrorList m_TrainErr;
/*     */   public ClusErrorList m_TestErr;
/*     */   public ClusErrorList m_ValidErr;
/*     */   public ClusErrorList m_ExtraErr;
/*     */   protected ClusStatManager m_Manager;
/*     */   protected transient ModelProcessorCollection m_TrainModelProc;
/*     */   protected transient ModelProcessorCollection m_TestModelProc;
/*     */   protected transient ModelProcessorCollection m_ValidModelProc;
/*     */   protected transient ClusEnsemblePredictionWriter m_TrainPreds;
/*     */   protected transient ClusEnsemblePredictionWriter m_TestPreds;
/*     */   
/*     */   public ClusModelInfo(String name) {
/*  57 */     this.m_Name = name;
/*  58 */     this.m_HasName = false;
/*     */   }
/*     */   
/*     */   public void setAllErrorsClone(ClusErrorList train, ClusErrorList test, ClusErrorList valid) {
/*  62 */     this.m_TrainErr = null;
/*  63 */     this.m_TestErr = null;
/*  64 */     this.m_ValidErr = null;
/*  65 */     if (train != null) {
/*  66 */       this.m_TrainErr = train.getErrorClone();
/*     */     }
/*  68 */     if (test != null) {
/*  69 */       this.m_TestErr = test.getErrorClone();
/*     */     }
/*  71 */     if (valid != null) {
/*  72 */       this.m_ValidErr = valid.getErrorClone();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setSelectedErrorsClone(ClusErrorList train, ClusErrorList test, ClusErrorList valid) {
/*  77 */     this.m_TrainErr = null;
/*  78 */     this.m_TestErr = null;
/*  79 */     this.m_ValidErr = null;
/*  80 */     if (train != null) {
/*  81 */       this.m_TrainErr = train.getErrorClone(getName());
/*     */     }
/*  83 */     if (test != null) {
/*  84 */       this.m_TestErr = test.getErrorClone(getName());
/*     */     }
/*  86 */     if (valid != null) {
/*  87 */       this.m_ValidErr = valid.getErrorClone(getName());
/*     */     }
/*     */   }
/*     */   
/*     */   public final String getName() {
/*  92 */     return this.m_Name;
/*     */   }
/*     */   
/*     */   public final ClusModel getModel() {
/*  96 */     return this.m_Model;
/*     */   }
/*     */   
/*     */   public final double getScore() {
/* 100 */     return this.m_Score;
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/* 104 */     return this.m_Manager;
/*     */   }
/*     */   
/*     */   public ClusSchema getSchema() {
/* 108 */     return this.m_Manager.getSchema();
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/* 112 */     return this.m_Manager.getSettings();
/*     */   }
/*     */   
/*     */   public final ClusErrorList getTrainingError() {
/* 116 */     return this.m_TrainErr;
/*     */   }
/*     */   
/*     */   public final ClusErrorList getTestError() {
/* 120 */     return this.m_TestErr;
/*     */   }
/*     */   
/*     */   public final ClusErrorList getValidationError() {
/* 124 */     return this.m_ValidErr;
/*     */   }
/*     */   
/*     */   public void setStatManager(ClusStatManager mgr) {
/* 128 */     this.m_Manager = mgr;
/*     */   }
/*     */   
/*     */   public final void setScore(double score) {
/* 132 */     this.m_Score = score;
/*     */   }
/*     */   
/*     */   public void check() {
/* 136 */     System.out.println("MI = " + this.m_TestErr);
/* 137 */     System.exit(1);
/*     */   }
/*     */   
/*     */   public void clearAll() {
/* 141 */     this.m_TrainModelProc = null;
/* 142 */     this.m_TestModelProc = null;
/*     */   }
/*     */   
/*     */   public final void addModelProcessor(int type, ClusModelProcessor proc) {
/* 146 */     ModelProcessorCollection coll = getAddModelProcessors(type);
/* 147 */     coll.addModelProcessor(proc);
/*     */   }
/*     */   
/*     */   public final void addEnsemblePredictionWriter(int type, ClusEnsemblePredictionWriter wrtr) {
/* 151 */     if (type == 1) {
/* 152 */       this.m_TestPreds = wrtr;
/*     */     }
/* 154 */     if (type == 0) {
/* 155 */       this.m_TrainPreds = wrtr;
/*     */     }
/*     */   }
/*     */   
/*     */   public final void addCheckModelProcessor(int type, ClusModelProcessor proc) {
/* 160 */     ModelProcessorCollection coll = getAddModelProcessors(type);
/* 161 */     if (coll.addCheckModelProcessor(proc)) {
/* 162 */       proc.addModelInfo(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public final ModelProcessorCollection getAddModelProcessors(int type) {
/* 167 */     if (type == 0) {
/* 168 */       if (this.m_TrainModelProc == null) {
/* 169 */         this.m_TrainModelProc = new ModelProcessorCollection();
/*     */       }
/* 171 */       return this.m_TrainModelProc;
/* 172 */     }  if (type == 1) {
/* 173 */       if (this.m_TestModelProc == null) {
/* 174 */         this.m_TestModelProc = new ModelProcessorCollection();
/*     */       }
/* 176 */       return this.m_TestModelProc;
/*     */     } 
/* 178 */     if (this.m_ValidModelProc == null) {
/* 179 */       this.m_ValidModelProc = new ModelProcessorCollection();
/*     */     }
/* 181 */     return this.m_ValidModelProc;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ModelProcessorCollection getModelProcessors(int type) {
/* 186 */     if (type == 0)
/* 187 */       return this.m_TrainModelProc; 
/* 188 */     if (type == 1) {
/* 189 */       return this.m_TestModelProc;
/*     */     }
/* 191 */     return this.m_ValidModelProc;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ClusEnsemblePredictionWriter getEnsemblePredictionWriter(int type) {
/* 196 */     if (type == 1) {
/* 197 */       return this.m_TestPreds;
/*     */     }
/* 199 */     if (type == 0) {
/* 200 */       return this.m_TrainPreds;
/*     */     }
/* 202 */     return null;
/*     */   }
/*     */   
/*     */   public final void initEnsemblePredictionWriter(int type) {
/* 206 */     String fname = "";
/* 207 */     if (type == 1 && this.m_TestPreds == null) {
/* 208 */       fname = getSettings().getAppName() + ".ens.test.preds";
/* 209 */       this.m_TestPreds = new ClusEnsemblePredictionWriter(fname, getSchema(), getSettings());
/*     */     } 
/* 211 */     if (type == 0 && this.m_TrainPreds == null) {
/* 212 */       fname = getSettings().getAppName() + ".ens.train.preds";
/* 213 */       this.m_TrainPreds = new ClusEnsemblePredictionWriter(fname, getSchema(), getSettings());
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void initModelProcessors(int type, ClusSchema schema) throws IOException, ClusException {
/* 218 */     ModelProcessorCollection coll = getModelProcessors(type);
/* 219 */     if (coll != null) {
/* 220 */       coll.initialize(this.m_Model, schema);
/*     */     }
/*     */   }
/*     */   
/*     */   public final void initAllModelProcessors(int type, ClusSchema schema) throws IOException, ClusException {
/* 225 */     ModelProcessorCollection coll = getModelProcessors(type);
/*     */     
/* 227 */     if (coll != null) {
/* 228 */       coll.initializeAll(schema);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public final void termModelProcessors(int type) throws IOException {
/* 234 */     ModelProcessorCollection coll = getModelProcessors(type);
/* 235 */     if (coll != null) {
/* 236 */       coll.terminate(this.m_Model);
/*     */     }
/*     */   }
/*     */   
/*     */   public final void termAllModelProcessors(int type) throws IOException {
/* 241 */     ModelProcessorCollection coll = getModelProcessors(type);
/* 242 */     if (coll != null) {
/* 243 */       coll.terminateAll();
/*     */     }
/*     */   }
/*     */   
/*     */   public final void terminateEnsemblePredictionWriter(int type) {
/* 248 */     if (type == 1) {
/* 249 */       this.m_TestPreds.closeWriter();
/*     */     }
/* 251 */     if (type == 0) {
/* 252 */       this.m_TrainPreds.closeWriter();
/*     */     }
/*     */   }
/*     */   
/*     */   public final void copyModelProcessors(ClusModelInfo target) {
/* 257 */     copyModelProcessors(0, target);
/* 258 */     copyModelProcessors(1, target);
/*     */   }
/*     */   
/*     */   public final void copyModelProcessors(int type, ClusModelInfo target) {
/* 262 */     ModelProcessorCollection coll = getModelProcessors(type);
/* 263 */     if (coll == null) {
/*     */       return;
/*     */     }
/* 266 */     for (int i = 0; i < coll.size(); i++) {
/* 267 */       ClusModelProcessor mproc = coll.getModelProcessor(i);
/* 268 */       if (mproc.shouldProcessModel(target)) {
/* 269 */         target.addCheckModelProcessor(type, mproc);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public final ClusModelInfo cloneModelInfo() {
/* 275 */     ClusModelInfo clone = new ClusModelInfo(this.m_Name);
/* 276 */     clone.setAllErrorsClone(this.m_TrainErr, this.m_TestErr, this.m_ValidErr);
/* 277 */     clone.setShouldSave(this.m_ShouldSave);
/* 278 */     clone.setShouldWritePredictions(this.m_ShouldWritePredictions);
/* 279 */     clone.setPruneInvalid(this.m_ShouldPruneInvalid);
/* 280 */     return clone;
/*     */   }
/*     */   
/*     */   public final void setModel(ClusModel model) {
/* 284 */     this.m_Model = model;
/*     */   }
/*     */   
/*     */   public final void deleteModel() {
/* 288 */     this.m_Model = null;
/*     */   }
/*     */   
/*     */   public final void setTestError(ClusErrorList err) {
/* 292 */     this.m_TestErr = err;
/*     */   }
/*     */   
/*     */   public final void setTrainError(ClusErrorList err) {
/* 296 */     this.m_TrainErr = err;
/*     */   }
/*     */   
/*     */   public final void setValidationError(ClusErrorList err) {
/* 300 */     this.m_ValidErr = err;
/*     */   }
/*     */   
/*     */   public void setExtraError(int type, ClusErrorList parent) {
/* 304 */     this.m_ExtraErr = parent;
/*     */   }
/*     */   
/*     */   public boolean hasExtraError(int type) {
/* 308 */     return (this.m_ExtraErr != null);
/*     */   }
/*     */   
/*     */   public ClusErrorList getExtraError(int type) {
/* 312 */     return this.m_ExtraErr;
/*     */   }
/*     */   
/*     */   public final void setName(String name) {
/* 316 */     this.m_Name = name;
/* 317 */     this.m_HasName = true;
/*     */   }
/*     */   
/*     */   public final boolean hasName() {
/* 321 */     return this.m_HasName;
/*     */   }
/*     */   
/*     */   public final void setShouldSave(boolean save) {
/* 325 */     this.m_ShouldSave = save;
/*     */   }
/*     */   
/*     */   public final boolean shouldSave() {
/* 329 */     return this.m_ShouldSave;
/*     */   }
/*     */   
/*     */   public boolean shouldWritePredictions() {
/* 333 */     return this.m_ShouldWritePredictions;
/*     */   }
/*     */   
/*     */   public void setShouldWritePredictions(boolean value) {
/* 337 */     this.m_ShouldWritePredictions = value;
/*     */   }
/*     */   
/*     */   public final void setPruneInvalid(boolean prune) {
/* 341 */     this.m_ShouldPruneInvalid = prune;
/*     */   }
/*     */   
/*     */   public final boolean shouldPruneInvalid() {
/* 345 */     return this.m_ShouldPruneInvalid;
/*     */   }
/*     */   
/*     */   public final ClusErrorList getError(int traintest) {
/* 349 */     if (traintest == 0)
/* 350 */       return this.m_TrainErr; 
/* 351 */     if (traintest == 2)
/* 352 */       return this.m_ValidErr; 
/* 353 */     if (traintest == 1) {
/* 354 */       return this.m_TestErr;
/*     */     }
/* 356 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ClusErrorList getCreateTestError() {
/* 361 */     if (this.m_TestErr == null) {
/* 362 */       this.m_TestErr = this.m_TrainErr.getErrorClone();
/*     */     }
/* 364 */     return this.m_TestErr;
/*     */   }
/*     */   
/*     */   public final boolean hasTestError() {
/* 368 */     return (this.m_TestErr != null);
/*     */   }
/*     */   
/*     */   public final boolean hasTrainError() {
/* 372 */     return (this.m_TrainErr != null);
/*     */   }
/*     */   
/*     */   public final boolean hasValidError() {
/* 376 */     return (this.m_ValidErr != null);
/*     */   }
/*     */   
/*     */   public final String getModelInfo() {
/* 380 */     if (this.m_Model == null) {
/* 381 */       return "No model available";
/*     */     }
/* 383 */     return this.m_Model.getModelInfo();
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getModelSize() {
/* 388 */     if (this.m_Model == null) {
/* 389 */       return this.m_ModelSize;
/*     */     }
/* 391 */     return this.m_Model.getModelSize();
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getNbModels() {
/* 396 */     if (this.m_Model == null) {
/* 397 */       return this.m_NbModels;
/*     */     }
/* 399 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean hasModel() {
/* 404 */     return (getNbModels() > 0);
/*     */   }
/*     */   
/*     */   public final void updateName(ClusModelInfo other) throws ClusException {
/* 408 */     if (hasName()) {
/* 409 */       if (other.hasName() && !getName().equals(other.getName())) {
/* 410 */         throw new ClusException("Combining error measures of different models: " + getName() + " <> " + other.getName());
/*     */       }
/* 412 */     } else if (other.hasName()) {
/* 413 */       setName(other.getName());
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void add(ClusModelInfo other) throws ClusException {
/* 418 */     updateName(other);
/* 419 */     this.m_ModelSize += other.getModelSize();
/* 420 */     this.m_NbModels += other.getNbModels();
/* 421 */     if (other.hasTrainError()) {
/* 422 */       this.m_TrainErr.add(other.getTrainingError());
/*     */     }
/* 424 */     if (other.hasValidError()) {
/* 425 */       this.m_ValidErr.add(other.getValidationError());
/*     */     }
/* 427 */     if (other.hasTestError()) {
/* 428 */       ClusErrorList mytesterr = getCreateTestError();
/* 429 */       mytesterr.add(other.getTestError());
/*     */     } 
/*     */   }
/*     */   
/*     */   public String toString() {
/* 434 */     return "ModelInfo '" + getName() + "' Size: " + getModelSize();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\ClusModelInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */