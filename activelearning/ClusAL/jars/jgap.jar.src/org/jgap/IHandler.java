package org.jgap;

public interface IHandler {
  public static final String CVS_REVISION = "$Revision: 1.3 $";
  
  boolean isHandlerFor(Object paramObject, Class paramClass);
  
  Object perform(Object paramObject1, Class paramClass, Object paramObject2) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\IHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */