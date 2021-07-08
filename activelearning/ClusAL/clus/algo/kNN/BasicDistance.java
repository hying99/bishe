package clus.algo.kNN;

import clus.data.rows.DataTuple;
import clus.data.type.ClusAttrType;

public abstract class BasicDistance {
  public abstract double getDistance(ClusAttrType paramClusAttrType, DataTuple paramDataTuple1, DataTuple paramDataTuple2);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\kNN\BasicDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */