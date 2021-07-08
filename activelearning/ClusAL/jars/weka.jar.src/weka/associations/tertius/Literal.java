package weka.associations.tertius;

import java.io.Serializable;
import weka.core.Instance;

public abstract class Literal implements Serializable {
  private Predicate m_predicate;
  
  public static final int NEG = 0;
  
  public static final int POS = 1;
  
  private int m_sign;
  
  private Literal m_negation;
  
  protected int m_missing;
  
  public Literal(Predicate paramPredicate, int paramInt1, int paramInt2) {
    this.m_predicate = paramPredicate;
    this.m_sign = paramInt1;
    this.m_negation = null;
    this.m_missing = paramInt2;
  }
  
  public Predicate getPredicate() {
    return this.m_predicate;
  }
  
  public Literal getNegation() {
    return this.m_negation;
  }
  
  public void setNegation(Literal paramLiteral) {
    this.m_negation = paramLiteral;
  }
  
  public boolean positive() {
    return (this.m_sign == 1);
  }
  
  public boolean negative() {
    return (this.m_sign == 0);
  }
  
  public abstract boolean satisfies(Instance paramInstance);
  
  public abstract boolean negationSatisfies(Instance paramInstance);
  
  public abstract String toString();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\tertius\Literal.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */