package sit.mtLearner;

import clus.data.rows.RowData;
import clus.data.type.ClusAttrType;
import clus.main.Settings;
import sit.TargetSet;

public interface MTLearner {
  void init(RowData paramRowData, Settings paramSettings);
  
  void setMainTarget(ClusAttrType paramClusAttrType);
  
  RowData[] LearnModel(TargetSet paramTargetSet, int paramInt);
  
  RowData[] LearnModel(TargetSet paramTargetSet) throws Exception;
  
  void setTestData(RowData paramRowData);
  
  void initXVal(int paramInt);
  
  String getName();
  
  int initLOOXVal();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\sit\mtLearner\MTLearner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */