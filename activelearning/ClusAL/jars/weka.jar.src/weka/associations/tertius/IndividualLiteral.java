package weka.associations.tertius;

public class IndividualLiteral extends AttributeValueLiteral {
  private int m_type;
  
  public static int INDIVIDUAL_PROPERTY = 0;
  
  public static int PART_PROPERTY = 1;
  
  public IndividualLiteral(Predicate paramPredicate, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super(paramPredicate, paramString, paramInt1, paramInt2, paramInt3);
    this.m_type = paramInt4;
  }
  
  public int getType() {
    return this.m_type;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\tertius\IndividualLiteral.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */