package org.jgap.data;

import java.util.Map;

public interface IDataElement {
  public static final String CVS_REVISION = "$Revision: 1.4 $";
  
  void setAttribute(String paramString1, String paramString2) throws Exception;
  
  void appendChild(IDataElement paramIDataElement) throws Exception;
  
  String getTagName();
  
  IDataElementList getElementsByTagName(String paramString);
  
  IDataElementList getChildNodes();
  
  String getAttribute(String paramString);
  
  Map getAttributes();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\IDataElement.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */