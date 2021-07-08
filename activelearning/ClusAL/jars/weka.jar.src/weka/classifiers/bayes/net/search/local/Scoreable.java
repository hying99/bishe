package weka.classifiers.bayes.net.search.local;

public interface Scoreable {
  public static final int BAYES = 0;
  
  public static final int BDeu = 1;
  
  public static final int MDL = 2;
  
  public static final int ENTROPY = 3;
  
  public static final int AIC = 4;
  
  double logScore(int paramInt);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\local\Scoreable.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */