package weka.gui.beans;

import java.util.Enumeration;

public interface UserRequestAcceptor {
  Enumeration enumerateRequests();
  
  void performRequest(String paramString);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\UserRequestAcceptor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */