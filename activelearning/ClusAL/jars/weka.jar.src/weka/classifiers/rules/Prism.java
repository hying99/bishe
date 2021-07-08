package weka.classifiers.rules;

import java.io.Serializable;
import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;

public class Prism extends Classifier {
  private PrismRule m_rules;
  
  public String globalInfo() {
    return "Class for building and using a PRISM rule set for classification. Can only deal with nominal attributes. Can't deal with missing values. Doesn't do any pruning. For more information, see \n\nJ. Cendrowska (1987). \"PRISM: An algorithm for inducing modular rules\". International Journal of Man-Machine Studies. Vol.27, No.4, pp.349-370.";
  }
  
  public double classifyInstance(Instance paramInstance) {
    int i = this.m_rules.resultRules(paramInstance);
    return (i == -1) ? Instance.missingValue() : i;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    PrismRule prismRule = null;
    Test test1 = null;
    Test test2 = null;
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Prism can't handle a numeric class!"); 
    paramInstances = new Instances(paramInstances);
    Enumeration enumeration = paramInstances.enumerateAttributes();
    while (enumeration.hasMoreElements()) {
      Attribute attribute = enumeration.nextElement();
      if (!attribute.isNominal())
        throw new UnsupportedAttributeTypeException("Prism can only deal with nominal attributes!"); 
      Enumeration enumeration1 = paramInstances.enumerateInstances();
      while (enumeration1.hasMoreElements()) {
        if (((Instance)enumeration1.nextElement()).isMissing(attribute))
          throw new NoSupportForMissingValuesException("Prism can't handle attributes with missing values!"); 
      } 
    } 
    paramInstances.deleteWithMissingClass();
    if (paramInstances.numInstances() == 0)
      throw new Exception("No instances with a class value!"); 
    for (byte b = 0; b < paramInstances.numClasses(); b++) {
      for (Instances instances = paramInstances; contains(instances, b); instances = prismRule.notCoveredBy(instances)) {
        prismRule = addRule(prismRule, new PrismRule(this, instances, b));
        Instances instances1 = instances;
        while (prismRule.m_errors != 0) {
          test1 = new Test();
          byte b1 = 0;
          int j = b1;
          int i = j;
          enumeration = instances1.enumerateAttributes();
          while (enumeration.hasMoreElements()) {
            Attribute attribute = enumeration.nextElement();
            if (isMentionedIn(attribute, prismRule.m_test)) {
              b1++;
              continue;
            } 
            int k = attribute.numValues();
            int[] arrayOfInt1 = new int[k];
            int[] arrayOfInt2 = new int[k];
            for (byte b2 = 0; b2 < k; b2++) {
              arrayOfInt2[b2] = 0;
              arrayOfInt1[b2] = 0;
            } 
            Enumeration enumeration1 = instances1.enumerateInstances();
            while (enumeration1.hasMoreElements()) {
              Instance instance = enumeration1.nextElement();
              arrayOfInt1[(int)instance.value(attribute)] = arrayOfInt1[(int)instance.value(attribute)] + 1;
              if ((int)instance.classValue() == b)
                arrayOfInt2[(int)instance.value(attribute)] = arrayOfInt2[(int)instance.value(attribute)] + 1; 
            } 
            for (byte b3 = 0; b3 < k; b3++) {
              int m = arrayOfInt2[b3] * j - i * arrayOfInt1[b3];
              if (test1.m_attr == -1 || m > 0 || (m == 0 && arrayOfInt2[b3] > i)) {
                i = arrayOfInt2[b3];
                j = arrayOfInt1[b3];
                test1.m_attr = attribute.index();
                test1.m_val = b3;
                prismRule.m_errors = j - i;
              } 
            } 
          } 
          if (test1.m_attr == -1)
            break; 
          test2 = addTest(prismRule, test2, test1);
          instances1 = prismRule.coveredBy(instances1);
          if (b1 == paramInstances.numAttributes() - 1)
            break; 
        } 
      } 
    } 
  }
  
  private PrismRule addRule(PrismRule paramPrismRule1, PrismRule paramPrismRule2) {
    if (paramPrismRule1 == null) {
      this.m_rules = paramPrismRule2;
    } else {
      paramPrismRule1.m_next = paramPrismRule2;
    } 
    return paramPrismRule2;
  }
  
  private Test addTest(PrismRule paramPrismRule, Test paramTest1, Test paramTest2) {
    if (paramPrismRule.m_test == null) {
      paramPrismRule.m_test = paramTest2;
    } else {
      paramTest1.m_next = paramTest2;
    } 
    return paramTest2;
  }
  
  private static boolean contains(Instances paramInstances, int paramInt) throws Exception {
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      if ((int)((Instance)enumeration.nextElement()).classValue() == paramInt)
        return true; 
    } 
    return false;
  }
  
  private static boolean isMentionedIn(Attribute paramAttribute, Test paramTest) {
    return (paramTest == null) ? false : ((paramTest.m_attr == paramAttribute.index()) ? true : isMentionedIn(paramAttribute, paramTest.m_next));
  }
  
  public String toString() {
    return (this.m_rules == null) ? "Prism: No model built yet." : ("Prism rules\n----------\n" + this.m_rules.toString());
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new Prism(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
  
  private class Test implements Serializable {
    private int m_attr;
    
    private int m_val;
    
    private Test m_next;
    
    private final Prism this$0;
    
    private Test(Prism this$0) {
      Prism.this = Prism.this;
      this.m_attr = -1;
      this.m_next = null;
    }
    
    private boolean satisfies(Instance param1Instance) {
      return ((int)param1Instance.value(this.m_attr) == this.m_val) ? ((this.m_next == null) ? true : this.m_next.satisfies(param1Instance)) : false;
    }
  }
  
  private class PrismRule implements Serializable {
    private int m_classification;
    
    private Instances m_instances;
    
    private Prism.Test m_test;
    
    private int m_errors;
    
    private PrismRule m_next;
    
    private final Prism this$0;
    
    public PrismRule(Prism this$0, Instances param1Instances, int param1Int) throws Exception {
      this.this$0 = this$0;
      this.m_instances = param1Instances;
      this.m_classification = param1Int;
      this.m_test = null;
      this.m_next = null;
      this.m_errors = 0;
      Enumeration enumeration = param1Instances.enumerateInstances();
      while (enumeration.hasMoreElements()) {
        if ((int)((Instance)enumeration.nextElement()).classValue() != param1Int)
          this.m_errors++; 
      } 
      this.m_instances = new Instances(this.m_instances, 0);
    }
    
    public int resultRule(Instance param1Instance) {
      return (this.m_test == null || this.m_test.satisfies(param1Instance)) ? this.m_classification : -1;
    }
    
    public int resultRules(Instance param1Instance) {
      return (resultRule(param1Instance) != -1) ? this.m_classification : ((this.m_next != null) ? this.m_next.resultRules(param1Instance) : -1);
    }
    
    public Instances coveredBy(Instances param1Instances) {
      Instances instances = new Instances(param1Instances, param1Instances.numInstances());
      Enumeration enumeration = param1Instances.enumerateInstances();
      while (enumeration.hasMoreElements()) {
        Instance instance = enumeration.nextElement();
        if (resultRule(instance) != -1)
          instances.add(instance); 
      } 
      instances.compactify();
      return instances;
    }
    
    public Instances notCoveredBy(Instances param1Instances) {
      Instances instances = new Instances(param1Instances, param1Instances.numInstances());
      Enumeration enumeration = param1Instances.enumerateInstances();
      while (enumeration.hasMoreElements()) {
        Instance instance = enumeration.nextElement();
        if (resultRule(instance) == -1)
          instances.add(instance); 
      } 
      instances.compactify();
      return instances;
    }
    
    public String toString() {
      try {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.m_test != null) {
          stringBuffer.append("If ");
          for (Prism.Test test = this.m_test; test != null; test = test.m_next) {
            if (test.m_attr == -1) {
              stringBuffer.append("?");
            } else {
              stringBuffer.append(this.m_instances.attribute(test.m_attr).name() + " = " + this.m_instances.attribute(test.m_attr).value(test.m_val));
            } 
            if (test.m_next != null)
              stringBuffer.append("\n   and "); 
          } 
          stringBuffer.append(" then ");
        } 
        stringBuffer.append(this.m_instances.classAttribute().value(this.m_classification) + "\n");
        if (this.m_next != null)
          stringBuffer.append(this.m_next.toString()); 
        return stringBuffer.toString();
      } catch (Exception exception) {
        return "Can't print Prism classifier!";
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\Prism.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */