package weka.associations.tertius;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import weka.core.Instance;
import weka.core.Instances;

public abstract class LiteralSet implements Serializable, Cloneable {
  private ArrayList m_literals = new ArrayList();
  
  private Literal m_lastLiteral = null;
  
  private int m_numInstances;
  
  private ArrayList m_counterInstances = null;
  
  private int m_counter;
  
  private int m_type = -1;
  
  public LiteralSet() {}
  
  public LiteralSet(Instances paramInstances) {
    this();
    this.m_numInstances = paramInstances.numInstances();
    this.m_counterInstances = new ArrayList(this.m_numInstances);
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements())
      this.m_counterInstances.add(enumeration.nextElement()); 
  }
  
  public Object clone() {
    Object object = null;
    try {
      object = super.clone();
      ((LiteralSet)object).m_literals = (ArrayList)this.m_literals.clone();
      if (this.m_counterInstances != null)
        ((LiteralSet)object).m_counterInstances = (ArrayList)this.m_counterInstances.clone(); 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.exit(0);
    } 
    return object;
  }
  
  public void upDate(Instances paramInstances) {
    Enumeration enumeration = paramInstances.enumerateInstances();
    this.m_numInstances = paramInstances.numInstances();
    this.m_counter = 0;
    while (enumeration.hasMoreElements()) {
      if (counterInstance(enumeration.nextElement()))
        this.m_counter++; 
    } 
  }
  
  public int getCounterInstancesNumber() {
    return (this.m_counterInstances != null) ? this.m_counterInstances.size() : this.m_counter;
  }
  
  public double getCounterInstancesFrequency() {
    return getCounterInstancesNumber() / this.m_numInstances;
  }
  
  public boolean overFrequencyThreshold(double paramDouble) {
    return (getCounterInstancesFrequency() >= paramDouble);
  }
  
  public boolean hasMaxCounterInstances() {
    return (getCounterInstancesNumber() == this.m_numInstances);
  }
  
  public void addElement(Literal paramLiteral) {
    this.m_literals.add(paramLiteral);
    this.m_lastLiteral = paramLiteral;
    if (paramLiteral instanceof IndividualLiteral) {
      int i = ((IndividualLiteral)paramLiteral).getType();
      if (i > this.m_type)
        this.m_type = i; 
    } 
    if (this.m_counterInstances != null)
      for (int i = this.m_counterInstances.size() - 1; i >= 0; i--) {
        Instance instance = this.m_counterInstances.get(i);
        if (!canKeep(instance, paramLiteral))
          this.m_counterInstances.remove(i); 
      }  
  }
  
  public final boolean isEmpty() {
    return (this.m_literals.size() == 0);
  }
  
  public final int numLiterals() {
    return this.m_literals.size();
  }
  
  public final Iterator enumerateLiterals() {
    return this.m_literals.iterator();
  }
  
  public Literal getLastLiteral() {
    return this.m_lastLiteral;
  }
  
  public boolean negationIncludedIn(LiteralSet paramLiteralSet) {
    Iterator iterator = enumerateLiterals();
    while (iterator.hasNext()) {
      Literal literal = iterator.next();
      if (!paramLiteralSet.contains(literal.getNegation()))
        return false; 
    } 
    return true;
  }
  
  public boolean contains(Literal paramLiteral) {
    return this.m_literals.contains(paramLiteral);
  }
  
  public int getType() {
    return this.m_type;
  }
  
  public boolean counterInstance(Instance paramInstance1, Instance paramInstance2) {
    Iterator iterator = enumerateLiterals();
    while (iterator.hasNext()) {
      IndividualLiteral individualLiteral = iterator.next();
      if (individualLiteral.getType() == IndividualLiteral.INDIVIDUAL_PROPERTY && !canKeep(paramInstance1, individualLiteral))
        return false; 
      if (individualLiteral.getType() == IndividualLiteral.PART_PROPERTY && !canKeep(paramInstance2, individualLiteral))
        return false; 
    } 
    return true;
  }
  
  public boolean counterInstance(Instance paramInstance) {
    if (paramInstance instanceof IndividualInstance && this.m_type == IndividualLiteral.PART_PROPERTY) {
      Enumeration enumeration = ((IndividualInstance)paramInstance).getParts().enumerateInstances();
      while (enumeration.hasMoreElements()) {
        if (counterInstance(paramInstance, enumeration.nextElement()))
          return true; 
      } 
      return false;
    } 
    Iterator iterator = enumerateLiterals();
    while (iterator.hasNext()) {
      Literal literal = iterator.next();
      if (!canKeep(paramInstance, literal))
        return false; 
    } 
    return true;
  }
  
  public abstract boolean canKeep(Instance paramInstance, Literal paramLiteral);
  
  public abstract boolean isIncludedIn(Rule paramRule);
  
  public abstract String toString();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\tertius\LiteralSet.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */