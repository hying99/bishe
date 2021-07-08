package weka.associations.tertius;

import java.util.Iterator;
import weka.core.Instance;
import weka.core.Instances;

public class Head extends LiteralSet {
  public Head() {}
  
  public Head(Instances paramInstances) {
    super(paramInstances);
  }
  
  public boolean canKeep(Instance paramInstance, Literal paramLiteral) {
    return paramLiteral.negationSatisfies(paramInstance);
  }
  
  public boolean isIncludedIn(Rule paramRule) {
    Iterator iterator = enumerateLiterals();
    while (iterator.hasNext()) {
      Literal literal = iterator.next();
      if (!paramRule.headContains(literal) && !paramRule.bodyContains(literal.getNegation()))
        return false; 
    } 
    return true;
  }
  
  public String toString() {
    Iterator iterator = enumerateLiterals();
    if (!iterator.hasNext())
      return "FALSE"; 
    StringBuffer stringBuffer = new StringBuffer();
    while (iterator.hasNext()) {
      stringBuffer.append(iterator.next().toString());
      if (iterator.hasNext())
        stringBuffer.append(" or "); 
    } 
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\tertius\Head.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */