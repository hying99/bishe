package weka.classifiers.lazy;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class IB1 extends Classifier implements UpdateableClassifier {
  private Instances m_Train;
  
  private double[] m_MinArray;
  
  private double[] m_MaxArray;
  
  public String globalInfo() {
    return "Nearest-neighbour classifier. Uses normalized Euclidean distance to find the training instance closest to the given test instance, and predicts the same class as this training instance. If multiple instances have the same (smallest) distance to the test instance, the first one found is used.  For more information, see \n\nAha, D., and D. Kibler (1991) \"Instance-based learning algorithms\", Machine Learning, vol.6, pp. 37-66.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual checkForStringAttributes : ()Z
    //   4: ifeq -> 17
    //   7: new weka/core/UnsupportedAttributeTypeException
    //   10: dup
    //   11: ldc 'Cannot handle string attributes!'
    //   13: invokespecial <init> : (Ljava/lang/String;)V
    //   16: athrow
    //   17: aload_0
    //   18: new weka/core/Instances
    //   21: dup
    //   22: aload_1
    //   23: iconst_0
    //   24: aload_1
    //   25: invokevirtual numInstances : ()I
    //   28: invokespecial <init> : (Lweka/core/Instances;II)V
    //   31: putfield m_Train : Lweka/core/Instances;
    //   34: aload_0
    //   35: getfield m_Train : Lweka/core/Instances;
    //   38: invokevirtual deleteWithMissingClass : ()V
    //   41: aload_0
    //   42: aload_0
    //   43: getfield m_Train : Lweka/core/Instances;
    //   46: invokevirtual numAttributes : ()I
    //   49: newarray double
    //   51: putfield m_MinArray : [D
    //   54: aload_0
    //   55: aload_0
    //   56: getfield m_Train : Lweka/core/Instances;
    //   59: invokevirtual numAttributes : ()I
    //   62: newarray double
    //   64: putfield m_MaxArray : [D
    //   67: iconst_0
    //   68: istore_2
    //   69: iload_2
    //   70: aload_0
    //   71: getfield m_Train : Lweka/core/Instances;
    //   74: invokevirtual numAttributes : ()I
    //   77: if_icmpge -> 102
    //   80: aload_0
    //   81: getfield m_MinArray : [D
    //   84: iload_2
    //   85: aload_0
    //   86: getfield m_MaxArray : [D
    //   89: iload_2
    //   90: ldc2_w NaN
    //   93: dup2_x2
    //   94: dastore
    //   95: dastore
    //   96: iinc #2, 1
    //   99: goto -> 69
    //   102: aload_0
    //   103: getfield m_Train : Lweka/core/Instances;
    //   106: invokevirtual enumerateInstances : ()Ljava/util/Enumeration;
    //   109: astore_2
    //   110: aload_2
    //   111: invokeinterface hasMoreElements : ()Z
    //   116: ifeq -> 135
    //   119: aload_0
    //   120: aload_2
    //   121: invokeinterface nextElement : ()Ljava/lang/Object;
    //   126: checkcast weka/core/Instance
    //   129: invokespecial updateMinMax : (Lweka/core/Instance;)V
    //   132: goto -> 110
    //   135: return
  }
  
  public void updateClassifier(Instance paramInstance) throws Exception {
    if (!this.m_Train.equalHeaders(paramInstance.dataset()))
      throw new Exception("Incompatible instance types"); 
    if (paramInstance.classIsMissing())
      return; 
    this.m_Train.add(paramInstance);
    updateMinMax(paramInstance);
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    if (this.m_Train.numInstances() == 0)
      throw new Exception("No training instances!"); 
    double d1 = Double.MAX_VALUE;
    double d2 = 0.0D;
    updateMinMax(paramInstance);
    Enumeration enumeration = this.m_Train.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if (!instance.classIsMissing()) {
        double d = distance(paramInstance, instance);
        if (d < d1) {
          d1 = d;
          d2 = instance.classValue();
        } 
      } 
    } 
    return d2;
  }
  
  public String toString() {
    return "IB1 classifier";
  }
  
  private double distance(Instance paramInstance1, Instance paramInstance2) {
    double d = 0.0D;
    for (byte b = 0; b < this.m_Train.numAttributes(); b++) {
      if (b != this.m_Train.classIndex())
        if (this.m_Train.attribute(b).isNominal()) {
          if (paramInstance1.isMissing(b) || paramInstance2.isMissing(b) || (int)paramInstance1.value(b) != (int)paramInstance2.value(b))
            d++; 
        } else {
          double d1;
          if (paramInstance1.isMissing(b) || paramInstance2.isMissing(b)) {
            if (paramInstance1.isMissing(b) && paramInstance2.isMissing(b)) {
              d1 = 1.0D;
            } else {
              if (paramInstance2.isMissing(b)) {
                d1 = norm(paramInstance1.value(b), b);
              } else {
                d1 = norm(paramInstance2.value(b), b);
              } 
              if (d1 < 0.5D)
                d1 = 1.0D - d1; 
            } 
          } else {
            d1 = norm(paramInstance1.value(b), b) - norm(paramInstance2.value(b), b);
          } 
          d += d1 * d1;
        }  
    } 
    return d;
  }
  
  private double norm(double paramDouble, int paramInt) {
    return (Double.isNaN(this.m_MinArray[paramInt]) || Utils.eq(this.m_MaxArray[paramInt], this.m_MinArray[paramInt])) ? 0.0D : ((paramDouble - this.m_MinArray[paramInt]) / (this.m_MaxArray[paramInt] - this.m_MinArray[paramInt]));
  }
  
  private void updateMinMax(Instance paramInstance) {
    for (byte b = 0; b < this.m_Train.numAttributes(); b++) {
      if (this.m_Train.attribute(b).isNumeric() && !paramInstance.isMissing(b))
        if (Double.isNaN(this.m_MinArray[b])) {
          this.m_MinArray[b] = paramInstance.value(b);
          this.m_MaxArray[b] = paramInstance.value(b);
        } else if (paramInstance.value(b) < this.m_MinArray[b]) {
          this.m_MinArray[b] = paramInstance.value(b);
        } else if (paramInstance.value(b) > this.m_MaxArray[b]) {
          this.m_MaxArray[b] = paramInstance.value(b);
        }  
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new IB1(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\lazy\IB1.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */