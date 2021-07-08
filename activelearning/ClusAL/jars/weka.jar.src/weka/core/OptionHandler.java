package weka.core;

import java.util.Enumeration;

public interface OptionHandler {
  Enumeration listOptions();
  
  void setOptions(String[] paramArrayOfString) throws Exception;
  
  String[] getOptions();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\OptionHandler.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */