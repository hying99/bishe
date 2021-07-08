package weka.experiment;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Compute extends Remote {
  Object executeTask(Task paramTask) throws RemoteException;
  
  Object checkStatus(Object paramObject) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\Compute.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */