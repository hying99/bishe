package org.apache.commons.math.random;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;

public interface EmpiricalDistribution {
  void load(double[] paramArrayOfdouble);
  
  void load(File paramFile) throws IOException;
  
  void load(URL paramURL) throws IOException;
  
  double getNextValue() throws IllegalStateException;
  
  StatisticalSummary getSampleStats() throws IllegalStateException;
  
  boolean isLoaded();
  
  int getBinCount();
  
  List getBinStats();
  
  double[] getUpperBounds();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\random\EmpiricalDistribution.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */