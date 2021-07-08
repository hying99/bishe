package weka.associations.tertius;

import weka.core.Instance;

public class AttributeValueLiteral extends Literal {
  private String m_value;
  
  private int m_index;
  
  public AttributeValueLiteral(Predicate paramPredicate, String paramString, int paramInt1, int paramInt2, int paramInt3) {
    super(paramPredicate, paramInt2, paramInt3);
    this.m_value = paramString;
    this.m_index = paramInt1;
  }
  
  public boolean satisfies(Instance paramInstance) {
    return (this.m_index == -1) ? (positive() ? paramInstance.isMissing(getPredicate().getIndex()) : (!paramInstance.isMissing(getPredicate().getIndex()))) : (paramInstance.isMissing(getPredicate().getIndex()) ? (positive() ? false : ((this.m_missing != 0))) : (positive() ? ((paramInstance.value(getPredicate().getIndex()) == this.m_index)) : ((paramInstance.value(getPredicate().getIndex()) != this.m_index))));
  }
  
  public boolean negationSatisfies(Instance paramInstance) {
    return (this.m_index == -1) ? (positive() ? (!paramInstance.isMissing(getPredicate().getIndex())) : paramInstance.isMissing(getPredicate().getIndex())) : (paramInstance.isMissing(getPredicate().getIndex()) ? (positive() ? ((this.m_missing != 0)) : false) : (positive() ? ((paramInstance.value(getPredicate().getIndex()) != this.m_index)) : ((paramInstance.value(getPredicate().getIndex()) == this.m_index))));
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (negative())
      stringBuffer.append("not "); 
    stringBuffer.append(getPredicate().toString() + " = " + this.m_value);
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\tertius\AttributeValueLiteral.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */