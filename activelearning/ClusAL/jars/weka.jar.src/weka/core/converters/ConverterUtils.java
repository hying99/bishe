package weka.core.converters;

import java.io.IOException;
import java.io.Serializable;
import java.io.StreamTokenizer;

public class ConverterUtils implements Serializable {
  public static void getFirstToken(StreamTokenizer paramStreamTokenizer) throws IOException {
    while (paramStreamTokenizer.nextToken() == 10);
    if (paramStreamTokenizer.ttype == 39 || paramStreamTokenizer.ttype == 34) {
      paramStreamTokenizer.ttype = -3;
    } else if (paramStreamTokenizer.ttype == -3 && paramStreamTokenizer.sval.equals("?")) {
      paramStreamTokenizer.ttype = 63;
    } 
  }
  
  public static void getToken(StreamTokenizer paramStreamTokenizer) throws IOException {
    paramStreamTokenizer.nextToken();
    if (paramStreamTokenizer.ttype == 10)
      return; 
    if (paramStreamTokenizer.ttype == 39 || paramStreamTokenizer.ttype == 34) {
      paramStreamTokenizer.ttype = -3;
    } else if (paramStreamTokenizer.ttype == -3 && paramStreamTokenizer.sval.equals("?")) {
      paramStreamTokenizer.ttype = 63;
    } 
  }
  
  public static void errms(StreamTokenizer paramStreamTokenizer, String paramString) throws IOException {
    throw new IOException(paramString + ", read " + paramStreamTokenizer.toString());
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\ConverterUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */