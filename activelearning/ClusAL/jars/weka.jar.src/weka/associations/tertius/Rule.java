package weka.associations.tertius;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import weka.core.Instance;
import weka.core.Instances;

public class Rule implements Serializable, Cloneable {
  private Body m_body = new Body();
  
  private Head m_head = new Head();
  
  private boolean m_repeatPredicate;
  
  private int m_maxLiterals;
  
  private boolean m_negBody;
  
  private boolean m_negHead;
  
  private boolean m_classRule;
  
  private boolean m_singleHead;
  
  private int m_numInstances;
  
  private ArrayList m_counterInstances;
  
  private int m_counter;
  
  private double m_confirmation;
  
  private double m_optimistic;
  
  public static Comparator confirmationComparator = new Comparator() {
      public int compare(Object param1Object1, Object param1Object2) {
        Rule rule1 = (Rule)param1Object1;
        Rule rule2 = (Rule)param1Object2;
        double d1 = rule1.getConfirmation();
        double d2 = rule2.getConfirmation();
        return (d1 > d2) ? -1 : ((d1 < d2) ? 1 : 0);
      }
    };
  
  public static Comparator observedComparator = new Comparator() {
      public int compare(Object param1Object1, Object param1Object2) {
        Rule rule1 = (Rule)param1Object1;
        Rule rule2 = (Rule)param1Object2;
        double d1 = rule1.getObservedFrequency();
        double d2 = rule2.getObservedFrequency();
        return (d1 < d2) ? -1 : ((d1 > d2) ? 1 : 0);
      }
    };
  
  public static Comparator optimisticComparator = new Comparator() {
      public int compare(Object param1Object1, Object param1Object2) {
        Rule rule1 = (Rule)param1Object1;
        Rule rule2 = (Rule)param1Object2;
        double d1 = rule1.getOptimistic();
        double d2 = rule2.getOptimistic();
        return (d1 > d2) ? -1 : ((d1 < d2) ? 1 : 0);
      }
    };
  
  public static Comparator confirmationThenObservedComparator = new Comparator() {
      public int compare(Object param1Object1, Object param1Object2) {
        int i = Rule.confirmationComparator.compare(param1Object1, param1Object2);
        return (i != 0) ? i : Rule.observedComparator.compare(param1Object1, param1Object2);
      }
    };
  
  public static Comparator optimisticThenObservedComparator = new Comparator() {
      public int compare(Object param1Object1, Object param1Object2) {
        int i = Rule.optimisticComparator.compare(param1Object1, param1Object2);
        return (i != 0) ? i : Rule.observedComparator.compare(param1Object1, param1Object2);
      }
    };
  
  public Rule(boolean paramBoolean1, int paramInt, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5) {
    this.m_repeatPredicate = paramBoolean1;
    this.m_maxLiterals = paramInt;
    this.m_negBody = (paramBoolean2 && !paramBoolean5);
    this.m_negHead = (paramBoolean3 && !paramBoolean5);
    this.m_classRule = paramBoolean4;
    this.m_singleHead = (paramBoolean4 || paramBoolean5);
  }
  
  public Rule(Instances paramInstances, boolean paramBoolean1, int paramInt, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5) {
    this.m_repeatPredicate = paramBoolean1;
    this.m_maxLiterals = paramInt;
    this.m_negBody = (paramBoolean2 && !paramBoolean5);
    this.m_negHead = (paramBoolean3 && !paramBoolean5);
    this.m_classRule = paramBoolean4;
    this.m_singleHead = (paramBoolean4 || paramBoolean5);
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
      ((Rule)object).m_body = (Body)this.m_body.clone();
      ((Rule)object).m_head = (Head)this.m_head.clone();
      if (this.m_counterInstances != null)
        ((Rule)object).m_counterInstances = (ArrayList)this.m_counterInstances.clone(); 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.exit(0);
    } 
    return object;
  }
  
  public boolean counterInstance(Instance paramInstance) {
    return (this.m_body.counterInstance(paramInstance) && this.m_head.counterInstance(paramInstance));
  }
  
  public void upDate(Instances paramInstances) {
    Enumeration enumeration = paramInstances.enumerateInstances();
    this.m_numInstances = paramInstances.numInstances();
    this.m_counter = 0;
    while (enumeration.hasMoreElements()) {
      if (counterInstance(enumeration.nextElement()))
        this.m_counter++; 
    } 
    this.m_head.upDate(paramInstances);
    this.m_body.upDate(paramInstances);
  }
  
  public double getConfirmation() {
    return this.m_confirmation;
  }
  
  public double getOptimistic() {
    return this.m_optimistic;
  }
  
  public double getExpectedNumber() {
    return this.m_body.getCounterInstancesNumber() * this.m_head.getCounterInstancesNumber() / this.m_numInstances;
  }
  
  public double getExpectedFrequency() {
    return getExpectedNumber() / this.m_numInstances;
  }
  
  public int getObservedNumber() {
    return (this.m_counterInstances != null) ? this.m_counterInstances.size() : this.m_counter;
  }
  
  public double getObservedFrequency() {
    return getObservedNumber() / this.m_numInstances;
  }
  
  public double getTPRate() {
    int i = this.m_body.getCounterInstancesNumber() - getObservedNumber();
    int j = this.m_numInstances - this.m_head.getCounterInstancesNumber() - i;
    return i / (i + j);
  }
  
  public double getFPRate() {
    int i = getObservedNumber();
    int j = this.m_head.getCounterInstancesNumber() - i;
    return i / (i + j);
  }
  
  public void calculateConfirmation() {
    double d1 = getExpectedFrequency();
    double d2 = getObservedFrequency();
    if (d1 == 0.0D || d1 == 1.0D) {
      this.m_confirmation = 0.0D;
    } else {
      this.m_confirmation = (d1 - d2) / (Math.sqrt(d1) - d1);
    } 
  }
  
  public void calculateOptimistic() {
    double d;
    int i = getObservedNumber();
    int j = this.m_body.getCounterInstancesNumber();
    int k = this.m_head.getCounterInstancesNumber();
    int m = this.m_numInstances;
    if (i <= j - k) {
      d = (k * (j - i)) / (m * m);
    } else if (i <= k - j) {
      d = (j * (k - i)) / (m * m);
    } else {
      d = ((k + j - i) * (k + j - i)) / (4 * m * m);
    } 
    if (d == 0.0D || d == 1.0D) {
      this.m_optimistic = 0.0D;
    } else {
      this.m_optimistic = d / (Math.sqrt(d) - d);
    } 
  }
  
  public boolean isEmpty() {
    return (this.m_head.isEmpty() && this.m_body.isEmpty());
  }
  
  public int numLiterals() {
    return this.m_body.numLiterals() + this.m_head.numLiterals();
  }
  
  private Rule addTermToBody(Literal paramLiteral) {
    if ((!this.m_negBody && paramLiteral.negative()) || (this.m_classRule && paramLiteral.getPredicate().isClass()) || (paramLiteral instanceof IndividualLiteral && ((IndividualLiteral)paramLiteral).getType() - this.m_body.getType() > 1 && ((IndividualLiteral)paramLiteral).getType() - this.m_head.getType() > 1))
      return null; 
    Rule rule = (Rule)clone();
    rule.m_body.addElement(paramLiteral);
    if (this.m_counterInstances != null)
      for (int i = rule.m_counterInstances.size() - 1; i >= 0; i--) {
        Instance instance = rule.m_counterInstances.get(i);
        if (!rule.m_body.canKeep(instance, paramLiteral))
          rule.m_counterInstances.remove(i); 
      }  
    return rule;
  }
  
  private Rule addTermToHead(Literal paramLiteral) {
    if ((!this.m_negHead && paramLiteral.negative()) || (this.m_classRule && !paramLiteral.getPredicate().isClass()) || (this.m_singleHead && !this.m_head.isEmpty()) || (paramLiteral instanceof IndividualLiteral && ((IndividualLiteral)paramLiteral).getType() != IndividualLiteral.INDIVIDUAL_PROPERTY))
      return null; 
    Rule rule = (Rule)clone();
    rule.m_head.addElement(paramLiteral);
    if (this.m_counterInstances != null)
      for (int i = rule.m_counterInstances.size() - 1; i >= 0; i--) {
        Instance instance = rule.m_counterInstances.get(i);
        if (!rule.m_head.canKeep(instance, paramLiteral))
          rule.m_counterInstances.remove(i); 
      }  
    return rule;
  }
  
  private SimpleLinkedList refine(Predicate paramPredicate, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) {
    SimpleLinkedList simpleLinkedList = new SimpleLinkedList();
    for (int i = paramInt1; i < paramInt2; i++) {
      Literal literal1 = paramPredicate.getLiteral(i);
      if (paramBoolean1) {
        Rule rule = addTermToBody(literal1);
        if (rule != null)
          simpleLinkedList.add(rule); 
      } 
      if (paramBoolean2) {
        Rule rule = addTermToHead(literal1);
        if (rule != null)
          simpleLinkedList.add(rule); 
      } 
      Literal literal2 = literal1.getNegation();
      if (literal2 != null) {
        if (paramBoolean1) {
          Rule rule = addTermToBody(literal2);
          if (rule != null)
            simpleLinkedList.add(rule); 
        } 
        if (paramBoolean2) {
          Rule rule = addTermToHead(literal2);
          if (rule != null)
            simpleLinkedList.add(rule); 
        } 
      } 
    } 
    return simpleLinkedList;
  }
  
  public SimpleLinkedList refine(ArrayList paramArrayList) {
    SimpleLinkedList simpleLinkedList = new SimpleLinkedList();
    if (numLiterals() == this.m_maxLiterals)
      return simpleLinkedList; 
    if (isEmpty()) {
      for (byte b = 0; b < paramArrayList.size(); b++) {
        Predicate predicate = paramArrayList.get(b);
        simpleLinkedList.addAll(refine(predicate, 0, predicate.numLiterals(), true, true));
      } 
    } else if (this.m_body.isEmpty() || this.m_head.isEmpty()) {
      boolean bool1;
      boolean bool2;
      Body body;
      if (this.m_body.isEmpty()) {
        Head head = this.m_head;
        bool1 = true;
        bool2 = false;
      } else {
        body = this.m_body;
        bool1 = false;
        bool2 = true;
      } 
      Literal literal = body.getLastLiteral();
      Predicate predicate = literal.getPredicate();
      if (this.m_repeatPredicate)
        simpleLinkedList.addAll(refine(predicate, predicate.indexOf(literal) + 1, predicate.numLiterals(), bool1, bool2)); 
      for (int i = paramArrayList.indexOf(predicate) + 1; i < paramArrayList.size(); i++) {
        predicate = paramArrayList.get(i);
        simpleLinkedList.addAll(refine(predicate, 0, predicate.numLiterals(), bool1, bool2));
      } 
    } else {
      Predicate predicate3;
      int n;
      Literal literal1 = this.m_body.getLastLiteral();
      Literal literal2 = this.m_head.getLastLiteral();
      Predicate predicate1 = literal1.getPredicate();
      Predicate predicate2 = literal2.getPredicate();
      int i = predicate1.indexOf(literal1);
      int j = predicate2.indexOf(literal2);
      int k = paramArrayList.indexOf(predicate1);
      int m = paramArrayList.indexOf(predicate2);
      boolean bool1 = (this.m_head.numLiterals() == 1 && (k < m || (k == m && i < j))) ? true : false;
      boolean bool2 = (this.m_body.numLiterals() == 1 && (m < k || (m == k && j < i))) ? true : false;
      if (bool1 || bool2) {
        Predicate predicate;
        int i2;
        if (bool1) {
          predicate = predicate1;
          i2 = i;
          predicate3 = predicate2;
          n = j;
        } else {
          predicate = predicate2;
          i2 = j;
          predicate3 = predicate1;
          n = i;
        } 
        if (paramArrayList.indexOf(predicate) < paramArrayList.indexOf(predicate3)) {
          if (this.m_repeatPredicate)
            simpleLinkedList.addAll(refine(predicate, i2 + 1, predicate.numLiterals(), bool1, bool2)); 
          for (int i3 = paramArrayList.indexOf(predicate) + 1; i3 < paramArrayList.indexOf(predicate3); i3++) {
            Predicate predicate4 = paramArrayList.get(i3);
            simpleLinkedList.addAll(refine(predicate4, 0, predicate4.numLiterals(), bool1, bool2));
          } 
          if (this.m_repeatPredicate)
            simpleLinkedList.addAll(refine(predicate3, 0, n, bool1, bool2)); 
        } else if (this.m_repeatPredicate) {
          simpleLinkedList.addAll(refine(predicate, i2 + 1, n, bool1, bool2));
        } 
      } 
      if (paramArrayList.indexOf(predicate1) > paramArrayList.indexOf(predicate2)) {
        predicate3 = predicate1;
        n = predicate1.indexOf(literal1);
      } else if (paramArrayList.indexOf(predicate1) < paramArrayList.indexOf(predicate2)) {
        predicate3 = predicate2;
        n = predicate2.indexOf(literal2);
      } else {
        predicate3 = predicate1;
        if (i > j) {
          n = predicate1.indexOf(literal1);
        } else {
          n = predicate2.indexOf(literal2);
        } 
      } 
      if (this.m_repeatPredicate)
        simpleLinkedList.addAll(refine(predicate3, n + 1, predicate3.numLiterals(), true, true)); 
      for (int i1 = paramArrayList.indexOf(predicate3) + 1; i1 < paramArrayList.size(); i1++) {
        Predicate predicate = paramArrayList.get(i1);
        simpleLinkedList.addAll(refine(predicate, 0, predicate.numLiterals(), true, true));
      } 
    } 
    return simpleLinkedList;
  }
  
  public boolean subsumes(Rule paramRule) {
    return (numLiterals() > paramRule.numLiterals()) ? false : ((this.m_body.isIncludedIn(paramRule) && this.m_head.isIncludedIn(paramRule)));
  }
  
  public boolean sameClauseAs(Rule paramRule) {
    return (numLiterals() == paramRule.numLiterals() && subsumes(paramRule));
  }
  
  public boolean equivalentTo(Rule paramRule) {
    return (numLiterals() == paramRule.numLiterals() && this.m_head.negationIncludedIn(paramRule.m_body) && this.m_body.negationIncludedIn(paramRule.m_head));
  }
  
  public boolean bodyContains(Literal paramLiteral) {
    return this.m_body.contains(paramLiteral);
  }
  
  public boolean headContains(Literal paramLiteral) {
    return this.m_head.contains(paramLiteral);
  }
  
  public boolean overFrequencyThreshold(double paramDouble) {
    return (this.m_body.overFrequencyThreshold(paramDouble) && this.m_head.overFrequencyThreshold(paramDouble));
  }
  
  public boolean hasTrueBody() {
    return (!this.m_body.isEmpty() && this.m_body.hasMaxCounterInstances());
  }
  
  public boolean hasFalseHead() {
    return (!this.m_head.isEmpty() && this.m_head.hasMaxCounterInstances());
  }
  
  public String valuesToString() {
    StringBuffer stringBuffer = new StringBuffer();
    DecimalFormat decimalFormat = new DecimalFormat("0.000000");
    stringBuffer.append(decimalFormat.format(getConfirmation()));
    stringBuffer.append(" ");
    stringBuffer.append(decimalFormat.format(getObservedFrequency()));
    return stringBuffer.toString();
  }
  
  public String rocToString() {
    StringBuffer stringBuffer = new StringBuffer();
    DecimalFormat decimalFormat = new DecimalFormat("0.000000");
    stringBuffer.append(decimalFormat.format(getConfirmation()));
    stringBuffer.append(" ");
    stringBuffer.append(decimalFormat.format(getTPRate()));
    stringBuffer.append(" ");
    stringBuffer.append(decimalFormat.format(getFPRate()));
    return stringBuffer.toString();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(this.m_body.toString());
    stringBuffer.append(" ==> ");
    stringBuffer.append(this.m_head.toString());
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\tertius\Rule.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */