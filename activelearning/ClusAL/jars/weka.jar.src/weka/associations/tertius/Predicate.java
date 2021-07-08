package weka.associations.tertius;

import java.io.Serializable;
import java.util.ArrayList;

public class Predicate implements Serializable {
  private ArrayList m_literals = new ArrayList();
  
  private String m_name;
  
  private int m_index;
  
  private boolean m_isClass;
  
  public Predicate(String paramString, int paramInt, boolean paramBoolean) {
    this.m_name = paramString;
    this.m_index = paramInt;
    this.m_isClass = paramBoolean;
  }
  
  public void addLiteral(Literal paramLiteral) {
    this.m_literals.add(paramLiteral);
  }
  
  public Literal getLiteral(int paramInt) {
    return this.m_literals.get(paramInt);
  }
  
  public int getIndex() {
    return this.m_index;
  }
  
  public int indexOf(Literal paramLiteral) {
    int i = this.m_literals.indexOf(paramLiteral);
    return (i != -1) ? i : this.m_literals.indexOf(paramLiteral.getNegation());
  }
  
  public int numLiterals() {
    return this.m_literals.size();
  }
  
  public boolean isClass() {
    return this.m_isClass;
  }
  
  public String toString() {
    return this.m_name;
  }
  
  public String description() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(toString() + "\n");
    for (byte b = 0; b < numLiterals(); b++) {
      Literal literal1 = getLiteral(b);
      Literal literal2 = literal1.getNegation();
      stringBuffer.append("\t" + literal1 + "\t" + literal2 + "\n");
    } 
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\tertius\Predicate.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */