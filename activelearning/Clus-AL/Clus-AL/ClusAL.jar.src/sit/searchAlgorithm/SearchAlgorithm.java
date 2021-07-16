package sit.searchAlgorithm;

import clus.data.type.ClusAttrType;
import clus.main.Settings;
import sit.TargetSet;
import sit.mtLearner.MTLearner;

public interface SearchAlgorithm {
  TargetSet search(ClusAttrType paramClusAttrType, TargetSet paramTargetSet);
  
  void setMTLearner(MTLearner paramMTLearner);
  
  String getName();
  
  void setSettings(Settings paramSettings);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\sit\searchAlgorithm\SearchAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */