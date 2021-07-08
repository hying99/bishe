package weka.experiment;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonMismatchException;

public final class RemoteEngine_Skel implements Skeleton {
  private static final Operation[] operations = new Operation[] { new Operation("java.lang.Object checkStatus(java.lang.Object)"), new Operation("java.lang.Object executeTask(weka.experiment.Task)") };
  
  private static final long interfaceHash = -8065814382466137525L;
  
  public void dispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt, long paramLong) throws Exception {
    Object object1;
    Object object2;
    if (paramInt < 0) {
      if (paramLong == 8527603492100504454L) {
        paramInt = 0;
      } else if (paramLong == 3272821424511182813L) {
        paramInt = 1;
      } else {
        throw new UnmarshalException("invalid method hash");
      } 
    } else if (paramLong != -8065814382466137525L) {
      throw new SkeletonMismatchException("interface hash mismatch");
    } 
    RemoteEngine remoteEngine = (RemoteEngine)paramRemote;
    switch (paramInt) {
      case 0:
        try {
          ObjectInput objectInput = paramRemoteCall.getInputStream();
          object1 = objectInput.readObject();
        } catch (IOException iOException) {
          throw new UnmarshalException("error unmarshalling arguments", iOException);
        } catch (ClassNotFoundException classNotFoundException) {
          throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
        } finally {
          paramRemoteCall.releaseInputStream();
        } 
        null = remoteEngine.checkStatus(object1);
        try {
          ObjectOutput objectOutput = paramRemoteCall.getResultStream(true);
          objectOutput.writeObject(null);
        } catch (IOException iOException) {
          throw new MarshalException("error marshalling return", iOException);
        } 
        return;
      case 1:
        try {
          ObjectInput objectInput = paramRemoteCall.getInputStream();
          object1 = objectInput.readObject();
        } catch (IOException iOException) {
          throw new UnmarshalException("error unmarshalling arguments", iOException);
        } catch (ClassNotFoundException classNotFoundException) {
          throw new UnmarshalException("error unmarshalling arguments", classNotFoundException);
        } finally {
          paramRemoteCall.releaseInputStream();
        } 
        object2 = remoteEngine.executeTask((Task)object1);
        try {
          ObjectOutput objectOutput = paramRemoteCall.getResultStream(true);
          objectOutput.writeObject(object2);
        } catch (IOException iOException) {
          throw new MarshalException("error marshalling return", iOException);
        } 
        return;
    } 
    throw new UnmarshalException("invalid method number");
  }
  
  public Operation[] getOperations() {
    return (Operation[])operations.clone();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\RemoteEngine_Skel.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */