package clus.model;

import clus.data.rows.DataTuple;
import clus.data.rows.RowData;
import clus.main.ClusRun;
import clus.statistic.ClusStatistic;
import clus.statistic.StatisticPrintInfo;
import clus.util.ClusException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import jeans.util.MyArray;

public interface ClusModel {
  public static final int DEFAULT = 0;
  
  public static final int ORIGINAL = 1;
  
  public static final int PRUNED = 2;
  
  public static final int RULES = 2;
  
  public static final int PRUNE_INVALID = 0;
  
  public static final int TRAIN = 0;
  
  public static final int TEST = 1;
  
  ClusStatistic predictWeighted(DataTuple paramDataTuple);
  
  void applyModelProcessors(DataTuple paramDataTuple, MyArray paramMyArray) throws IOException;
  
  int getModelSize();
  
  String getModelInfo();
  
  void printModel(PrintWriter paramPrintWriter);
  
  void printModel(PrintWriter paramPrintWriter, StatisticPrintInfo paramStatisticPrintInfo);
  
  void printModelAndExamples(PrintWriter paramPrintWriter, StatisticPrintInfo paramStatisticPrintInfo, RowData paramRowData);
  
  void printModelToQuery(PrintWriter paramPrintWriter, ClusRun paramClusRun, int paramInt1, int paramInt2, boolean paramBoolean);
  
  void printModelToPythonScript(PrintWriter paramPrintWriter);
  
  void attachModel(HashMap paramHashMap) throws ClusException;
  
  void retrieveStatistics(ArrayList paramArrayList);
  
  ClusModel prune(int paramInt);
  
  int getID();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\ClusModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */