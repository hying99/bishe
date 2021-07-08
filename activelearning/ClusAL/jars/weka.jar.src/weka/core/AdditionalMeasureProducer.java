package weka.core;

import java.util.Enumeration;

public interface AdditionalMeasureProducer {
  Enumeration enumerateMeasures();
  
  double getMeasure(String paramString);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\AdditionalMeasureProducer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */