package org.jgap.data;

public interface IDataCreators {
  public static final String CVS_REVISION = "$Revision: 1.6 $";
  
  void setTree(IDataElementList paramIDataElementList);
  
  IDataElementList getTree();
  
  IDataCreators newDocument() throws Exception;
  
  void appendChild(IDataElement paramIDataElement) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\IDataCreators.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */