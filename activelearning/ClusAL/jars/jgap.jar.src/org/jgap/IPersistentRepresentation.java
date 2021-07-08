package org.jgap;

public interface IPersistentRepresentation {
  public static final String CVS_REVISION = "$Revision: 1.2 $";
  
  String getPersistentRepresentation();
  
  void setValueFromPersistentRepresentation(String paramString) throws UnsupportedRepresentationException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\IPersistentRepresentation.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */