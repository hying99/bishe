package weka.experiment;

import java.io.IOException;
import java.io.ObjectInput;
import java.lang.reflect.Method;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public final class RemoteEngine_Stub extends RemoteStub implements Compute, Remote {
  private static final Operation[] operations = new Operation[] { new Operation("java.lang.Object checkStatus(java.lang.Object)"), new Operation("java.lang.Object executeTask(weka.experiment.Task)") };
  
  private static final long interfaceHash = -8065814382466137525L;
  
  private static final long serialVersionUID = 2L;
  
  private static boolean useNewInvoke;
  
  private static Method $method_checkStatus_0;
  
  private static Method $method_executeTask_1;
  
  static Class array$Ljava$lang$Object;
  
  static {
    try {
      RemoteRef.class.getMethod("invoke", new Class[] { Remote.class, Method.class, (array$Ljava$lang$Object != null) ? array$Ljava$lang$Object : (array$Ljava$lang$Object = class$("[Ljava.lang.Object;")), long.class });
      useNewInvoke = true;
      $method_checkStatus_0 = Compute.class.getMethod("checkStatus", new Class[] { Object.class });
      $method_executeTask_1 = Compute.class.getMethod("executeTask", new Class[] { Task.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      useNewInvoke = false;
    } 
  }
  
  public RemoteEngine_Stub() {}
  
  public RemoteEngine_Stub(RemoteRef paramRemoteRef) {
    super(paramRemoteRef);
  }
  
  public Object checkStatus(Object paramObject) throws Exception {
    Object object;
    if (useNewInvoke)
      return this.ref.invoke(this, $method_checkStatus_0, new Object[] { paramObject }, 8527603492100504454L); 
    RemoteCall remoteCall = this.ref.newCall(this, operations, 0, -8065814382466137525L);
    try {
      object = remoteCall.getOutputStream();
      object.writeObject(paramObject);
    } catch (IOException null) {
      throw new MarshalException("error marshalling arguments", object);
    } 
    this.ref.invoke(remoteCall);
    try {
      ObjectInput objectInput = remoteCall.getInputStream();
      object = objectInput.readObject();
    } catch (IOException iOException) {
      throw new UnmarshalException("error unmarshalling return", iOException);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new UnmarshalException("error unmarshalling return", classNotFoundException);
    } finally {
      this.ref.done(remoteCall);
    } 
    return object;
  }
  
  public Object executeTask(Task paramTask) throws RemoteException {
    try {
      Object object;
      if (useNewInvoke)
        return this.ref.invoke(this, $method_executeTask_1, new Object[] { paramTask }, 3272821424511182813L); 
      RemoteCall remoteCall = this.ref.newCall(this, operations, 1, -8065814382466137525L);
      try {
        object = remoteCall.getOutputStream();
        object.writeObject(paramTask);
      } catch (IOException null) {
        throw new MarshalException("error marshalling arguments", object);
      } 
      this.ref.invoke(remoteCall);
      try {
        ObjectInput objectInput = remoteCall.getInputStream();
        object = objectInput.readObject();
      } catch (IOException iOException) {
        throw new UnmarshalException("error unmarshalling return", iOException);
      } catch (ClassNotFoundException classNotFoundException) {
        throw new UnmarshalException("error unmarshalling return", classNotFoundException);
      } finally {
        this.ref.done(remoteCall);
      } 
      return object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\RemoteEngine_Stub.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */