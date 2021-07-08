package org.jgap.data.config;

import java.util.List;
import org.jgap.InvalidConfigurationException;

public interface ConfigurationHandler {
  public static final String CVS_REVISION = "$Revision: 1.5 $";
  
  String getName();
  
  List getConfigProperties();
  
  void readConfig() throws ConfigException, InvalidConfigurationException;
  
  String getNS();
  
  void setConfigurable(Configurable paramConfigurable);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\config\ConfigurationHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */