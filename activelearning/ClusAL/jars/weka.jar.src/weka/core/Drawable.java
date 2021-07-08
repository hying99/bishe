package weka.core;

public interface Drawable {
  public static final int NOT_DRAWABLE = 0;
  
  public static final int TREE = 1;
  
  public static final int BayesNet = 2;
  
  int graphType();
  
  String graph() throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Drawable.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */