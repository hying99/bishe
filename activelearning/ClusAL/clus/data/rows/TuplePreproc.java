package clus.data.rows;

import clus.util.ClusException;

public interface TuplePreproc {
  int getNbPasses();
  
  void preproc(int paramInt, DataTuple paramDataTuple) throws ClusException;
  
  void preprocSingle(DataTuple paramDataTuple) throws ClusException;
  
  void done(int paramInt) throws ClusException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\rows\TuplePreproc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */