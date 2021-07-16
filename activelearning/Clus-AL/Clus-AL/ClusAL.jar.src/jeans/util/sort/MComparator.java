package jeans.util.sort;

public interface MComparator {
  public static final int LESS = -1;
  
  public static final int EQUAL = 0;
  
  public static final int MORE = 1;
  
  int compare(Object paramObject1, Object paramObject2);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\sort\MComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */