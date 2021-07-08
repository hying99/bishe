package org.jgap.data;

public interface IDataElementList {
  public static final String CVS_REVISION = "$Revision: 1.4 $";
  
  IDataElement item(int paramInt);
  
  int getLength();
  
  void add(IDataElement paramIDataElement);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\IDataElementList.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */