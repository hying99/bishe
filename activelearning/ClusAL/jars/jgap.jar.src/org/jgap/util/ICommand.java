package org.jgap.util;

public interface ICommand {
  public static final String CVS_REVISION = "$Revision: 1.3 $";
  
  CommandResult execute(Object paramObject) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\ICommand.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */