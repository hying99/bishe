package org.jgap;

import java.io.Serializable;

public interface Gene extends Comparable, Serializable {
  public static final String CVS_REVISION = "$Revision: 1.25 $";
  
  public static final String PERSISTENT_FIELD_DELIMITER = ":";
  
  Gene newGene();
  
  void setAllele(Object paramObject);
  
  Object getAllele();
  
  String getPersistentRepresentation() throws UnsupportedOperationException;
  
  void setValueFromPersistentRepresentation(String paramString) throws UnsupportedOperationException, UnsupportedRepresentationException;
  
  void setToRandomValue(RandomGenerator paramRandomGenerator);
  
  void cleanup();
  
  String toString();
  
  int size();
  
  void applyMutation(int paramInt, double paramDouble);
  
  void setApplicationData(Object paramObject);
  
  Object getApplicationData();
  
  void setCompareApplicationData(boolean paramBoolean);
  
  boolean isCompareApplicationData();
  
  double getEnergy();
  
  void setEnergy(double paramDouble);
  
  void setConstraintChecker(IGeneConstraintChecker paramIGeneConstraintChecker);
  
  Configuration getConfiguration();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\Gene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */