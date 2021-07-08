package weka.associations.tertius;

import java.util.Iterator;
import weka.core.Instance;
import weka.core.Instances;

public class Body extends LiteralSet {
  public Body() {}
  
  public Body(Instances paramInstances) {
    super(paramInstances);
  }
  
  public boolean canKeep(Instance paramInstance, Literal paramLiteral) {
    return paramLiteral.satisfies(paramInstance);
  }
  
  public boolean isIncludedIn(Rule paramRule) {
    Iterator iterator = enumerateLiterals();
    while (iterator.hasNext()) {
      Literal literal = iterator.next();
      if (!paramRule.bodyContains(literal) && !paramRule.headContains(literal.getNegation()))
        return false; 
    } 
    return true;
  }
  
  public String toString() {
    Iterator iterator = enumerateLiterals();
    if (!iterator.hasNext())
      return "TRUE"; 
    StringBuffer stringBuffer = new StringBuffer();
    while (iterator.hasNext()) {
      stringBuffer.append(iterator.next().toString());
      if (iterator.hasNext())
        stringBuffer.append(" and "); 
    } 
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\tertius\Body.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */