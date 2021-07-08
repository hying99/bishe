package weka.attributeSelection;

public interface RankedOutputSearch {
  double[][] rankedAttributes() throws Exception;
  
  void setThreshold(double paramDouble);
  
  double getThreshold();
  
  void setNumToSelect(int paramInt);
  
  int getNumToSelect();
  
  int getCalculatedNumToSelect();
  
  void setGenerateRanking(boolean paramBoolean);
  
  boolean getGenerateRanking();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\RankedOutputSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */