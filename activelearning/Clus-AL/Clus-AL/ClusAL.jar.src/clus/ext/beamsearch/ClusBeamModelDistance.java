/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.main.ClusRun;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClusStatistic;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusBeamModelDistance
/*     */ {
/*     */   RowData m_Data;
/*     */   int m_NbRows;
/*     */   boolean isBeamUpdated = false;
/*     */   
/*     */   public ClusBeamModelDistance(ClusRun run, ClusBeam beam) {
/*  44 */     this.m_Data = (RowData)run.getTrainingSet();
/*  45 */     if (this.m_Data == null) {
/*  46 */       System.err.println(getClass().getName() + ": ClusBeamTreeDistance(): Error while reading the train data");
/*  47 */       System.exit(1);
/*     */     } 
/*  49 */     this.m_NbRows = this.m_Data.getNbRows();
/*  50 */     fillBeamWithPredictions(beam);
/*     */   }
/*     */   
/*     */   public void fillBeamWithPredictions(ClusBeam beam) {
/*  54 */     ArrayList<ClusBeamModel> arr = beam.toArray();
/*     */     
/*  56 */     for (int k = 0; k < arr.size(); k++) {
/*  57 */       ClusBeamModel model = arr.get(k);
/*  58 */       model.setModelPredictions(getPredictions(model.getModel()));
/*     */     } 
/*     */   }
/*     */   
/*     */   public ArrayList<ClusStatistic> getPredictions(ClusModel model) {
/*  63 */     ArrayList<ClusStatistic> predictions = new ArrayList<>();
/*  64 */     for (int i = 0; i < this.m_NbRows; i++) {
/*  65 */       DataTuple tuple = this.m_Data.getTuple(i);
/*  66 */       ClusStatistic stat = model.predictWeighted(tuple);
/*  67 */       predictions.add(tuple.getIndex(), stat);
/*     */     } 
/*     */     
/*  70 */     return predictions;
/*     */   }
/*     */   
/*     */   public static ArrayList<ClusStatistic> getPredictionsDataSet(ClusModel model, RowData train, boolean isNum) {
/*  74 */     ArrayList<ClusStatistic> predictions = new ArrayList<>();
/*  75 */     for (int i = 0; i < train.getNbRows(); i++) {
/*  76 */       DataTuple tuple = train.getTuple(i);
/*  77 */       ClusStatistic stat = model.predictWeighted(tuple);
/*  78 */       predictions.add(stat);
/*     */     } 
/*  80 */     return predictions;
/*     */   }
/*     */   
/*     */   public static double getDistance(ArrayList<ClusStatistic> a, ArrayList<ClusStatistic> b) {
/*  84 */     double result = 0.0D;
/*  85 */     for (int i = 0; i < a.size(); i++) {
/*  86 */       ClusStatistic first = a.get(i);
/*  87 */       ClusStatistic second = b.get(i);
/*  88 */       result += first.getSquaredDistance(second);
/*     */     } 
/*  90 */     return result / a.size();
/*     */   }
/*     */   
/*     */   public void calculatePredictionDistances(ClusBeam beam, ClusBeamModel candidate) {
/*  94 */     ArrayList<ClusBeamModel> arr = beam.toArray();
/*     */ 
/*     */     
/*  97 */     ArrayList<ClusStatistic> predCandidate = candidate.getModelPredictions();
/*     */     
/*  99 */     double candidateDist = 0.0D;
/* 100 */     for (int i = 0; i < arr.size(); i++) {
/* 101 */       ClusBeamModel beamModel1 = arr.get(i);
/* 102 */       ArrayList<ClusStatistic> predModel1 = beamModel1.getModelPredictions();
/* 103 */       double dist = 0.0D;
/* 104 */       for (int j = 0; j < arr.size(); j++) {
/* 105 */         ClusBeamModel beamModel2 = arr.get(j);
/* 106 */         ArrayList<ClusStatistic> predModel2 = beamModel2.getModelPredictions();
/* 107 */         dist += getDistance(predModel1, predModel2);
/*     */       } 
/* 109 */       double cdist = getDistance(predModel1, predCandidate);
/* 110 */       dist += cdist;
/* 111 */       candidateDist += cdist;
/*     */ 
/*     */       
/* 114 */       beamModel1.setDistanceToBeam(dist);
/*     */     } 
/*     */ 
/*     */     
/* 118 */     candidate.setDistanceToBeam(candidateDist);
/*     */   }
/*     */   
/*     */   public void calculatePredictionDistancesOpt(ClusBeam beam, ClusBeamModel candidate) {
/* 122 */     ArrayList<ClusBeamModel> arr = beam.toArray();
/* 123 */     int size = arr.size();
/*     */ 
/*     */     
/* 126 */     ArrayList<ClusStatistic> predCandidate = candidate.getModelPredictions();
/* 127 */     double candidateDist = 0.0D;
/* 128 */     double[] tempDist = new double[size];
/* 129 */     double temp = 0.0D;
/* 130 */     for (int i = 0; i < size - 1; i++) {
/* 131 */       ClusBeamModel beamModel1 = arr.get(i);
/* 132 */       ArrayList<ClusStatistic> predModel1 = beamModel1.getModelPredictions();
/* 133 */       for (int j = i + 1; j < size; j++) {
/* 134 */         ClusBeamModel beamModel2 = arr.get(j);
/* 135 */         ArrayList<ClusStatistic> predModel2 = beamModel2.getModelPredictions();
/* 136 */         temp = getDistance(predModel1, predModel2);
/* 137 */         tempDist[i] = tempDist[i] + temp;
/* 138 */         tempDist[j] = tempDist[j] + temp;
/*     */       } 
/* 140 */       temp = getDistance(predModel1, predCandidate);
/* 141 */       tempDist[i] = tempDist[i] + temp;
/* 142 */       candidateDist += temp;
/* 143 */       beamModel1.setDistanceToBeam(tempDist[i]);
/*     */     } 
/* 145 */     temp = getDistance(((ClusBeamModel)arr.get(size - 1)).getModelPredictions(), predCandidate);
/* 146 */     tempDist[size - 1] = tempDist[size - 1] + temp;
/* 147 */     candidateDist += temp;
/* 148 */     ((ClusBeamModel)arr.get(size - 1)).setDistanceToBeam(tempDist[size - 1]);
/* 149 */     candidate.setDistanceToBeam(candidateDist);
/*     */   }
/*     */   
/*     */   public void addDistToCandOpt(ClusBeam beam, ClusBeamModel candidate) {
/* 153 */     ArrayList<ClusBeamModel> arr = beam.toArray();
/* 154 */     int size = arr.size();
/*     */     
/* 156 */     ArrayList<ClusStatistic> candidatepredictions = candidate.getModelPredictions();
/* 157 */     double dist = 0.0D;
/* 158 */     double candidatedist = 0.0D;
/*     */     
/* 160 */     for (int i = 0; i < size; i++) {
/* 161 */       ClusBeamModel model = arr.get(i);
/* 162 */       dist = getDistance(model.getModelPredictions(), candidatepredictions);
/* 163 */       candidatedist += dist;
/* 164 */       double distance = model.getDistanceToBeam();
/* 165 */       distance += dist;
/* 166 */       model.setDistanceToBeam(distance);
/*     */     } 
/* 168 */     candidate.setDistanceToBeam(candidatedist);
/*     */   }
/*     */ 
/*     */   
/*     */   public void deductFromBeamOpt(ClusBeam beam, ClusBeamModel candidate, int position) {
/* 173 */     ArrayList<ClusBeamModel> arr = beam.toArray();
/* 174 */     int size = arr.size();
/* 175 */     ArrayList<ClusStatistic> candidatepredictions = candidate.getModelPredictions();
/*     */     
/* 177 */     double dist = 0.0D;
/*     */     
/* 179 */     if (position == size) {
/*     */       
/* 181 */       for (int i = 0; i < size; i++) {
/* 182 */         ClusBeamModel model = arr.get(i);
/* 183 */         dist = getDistance(model.getModelPredictions(), candidatepredictions);
/* 184 */         double distance = model.getDistanceToBeam();
/* 185 */         distance -= dist;
/* 186 */         model.setDistanceToBeam(distance);
/*     */       } 
/*     */     } else {
/* 189 */       ClusBeamModel exitmodel = arr.get(position);
/* 190 */       ArrayList<ClusStatistic> exitpredictions = exitmodel.getModelPredictions();
/* 191 */       for (int j = 0; j < size; j++) {
/* 192 */         if (j != position) {
/* 193 */           ClusBeamModel model = arr.get(j);
/* 194 */           dist = getDistance(model.getModelPredictions(), exitpredictions);
/* 195 */           double d = model.getDistanceToBeam();
/* 196 */           d -= dist;
/* 197 */           model.setDistanceToBeam(d);
/*     */         } 
/*     */       } 
/* 200 */       dist = getDistance(candidatepredictions, exitpredictions);
/* 201 */       double distance = candidate.getDistanceToBeam();
/* 202 */       distance -= dist;
/* 203 */       candidate.setDistanceToBeam(distance);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getIsBeamUpdated() {
/* 209 */     return this.isBeamUpdated;
/*     */   }
/*     */   
/*     */   public void setIsBeamUpdated(boolean update) {
/* 213 */     this.isBeamUpdated = update;
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
/*     */   public static double calcBeamSimilarity(ArrayList<ClusBeamModel> beam, RowData data, boolean isNum) {
/* 225 */     ArrayList<ArrayList<ClusStatistic>> predictions = new ArrayList<>();
/* 226 */     double result = 0.0D;
/*     */     
/* 228 */     for (int i = 0; i < beam.size(); i++) {
/*     */       try {
/* 230 */         ClusBeamModel model = beam.get(i);
/* 231 */         predictions.add(getPredictionsDataSet(model.getModel(), data, isNum));
/* 232 */       } catch (ClassCastException e) {
/* 233 */         ClusModel model = (ClusModel)beam.get(i);
/* 234 */         predictions.add(getPredictionsDataSet(model, data, isNum));
/*     */       } 
/*     */     } 
/* 237 */     for (int m = 0; m < predictions.size(); m++) {
/* 238 */       double dist = 0.0D;
/* 239 */       for (int n = 0; n < predictions.size(); n++) {
/* 240 */         dist += getDistance(predictions.get(m), predictions.get(n));
/*     */       }
/* 242 */       dist = 1.0D - dist / beam.size();
/*     */       
/* 244 */       result += dist;
/*     */     } 
/* 246 */     return result / beam.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDistToConstraint(ClusBeamModel model, ClusBeamSyntacticConstraint constraint) {
/* 256 */     return 1.0D - getDistance(model.getModelPredictions(), constraint.getConstraintPredictions());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\beamsearch\ClusBeamModelDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */