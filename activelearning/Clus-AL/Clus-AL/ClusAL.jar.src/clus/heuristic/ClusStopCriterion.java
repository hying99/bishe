package clus.heuristic;

import clus.statistic.ClusStatistic;

public interface ClusStopCriterion {
  boolean stopCriterion(ClusStatistic paramClusStatistic1, ClusStatistic paramClusStatistic2, ClusStatistic paramClusStatistic3);
  
  boolean stopCriterion(ClusStatistic paramClusStatistic, ClusStatistic[] paramArrayOfClusStatistic, int paramInt);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\heuristic\ClusStopCriterion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */