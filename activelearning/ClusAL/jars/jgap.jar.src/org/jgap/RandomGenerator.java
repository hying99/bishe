package org.jgap;

import java.io.Serializable;

public interface RandomGenerator extends Serializable {
  public static final String CVS_REVISION = "$Revision: 1.7 $";
  
  int nextInt();
  
  int nextInt(int paramInt);
  
  long nextLong();
  
  double nextDouble();
  
  float nextFloat();
  
  boolean nextBoolean();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\RandomGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */