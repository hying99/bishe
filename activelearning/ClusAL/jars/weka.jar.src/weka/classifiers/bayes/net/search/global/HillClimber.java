package weka.classifiers.bayes.net.search.global;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class HillClimber extends GlobalScoreSearchAlgorithm {
  boolean m_bUseArcReversal = false;
  
  protected void search(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    this.m_BayesNet = paramBayesNet;
    double d = calcScore(paramBayesNet);
    for (Operation operation = getOptimalOperation(paramBayesNet, paramInstances); operation != null && operation.m_fScore > d; operation = getOptimalOperation(paramBayesNet, paramInstances)) {
      performOperation(paramBayesNet, paramInstances, operation);
      d = operation.m_fScore;
    } 
  }
  
  boolean isNotTabu(Operation paramOperation) {
    return true;
  }
  
  Operation getOptimalOperation(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    Operation operation = new Operation(this);
    operation = findBestArcToAdd(paramBayesNet, paramInstances, operation);
    operation = findBestArcToDelete(paramBayesNet, paramInstances, operation);
    if (getUseArcReversal())
      operation = findBestArcToReverse(paramBayesNet, paramInstances, operation); 
    return (operation.m_fScore == -1.0E100D) ? null : operation;
  }
  
  void performOperation(BayesNet paramBayesNet, Instances paramInstances, Operation paramOperation) throws Exception {
    switch (paramOperation.m_nOperation) {
      case 0:
        applyArcAddition(paramBayesNet, paramOperation.m_nHead, paramOperation.m_nTail, paramInstances);
        if (paramBayesNet.getDebug())
          System.out.print("Add " + paramOperation.m_nHead + " -> " + paramOperation.m_nTail); 
        break;
      case 1:
        applyArcDeletion(paramBayesNet, paramOperation.m_nHead, paramOperation.m_nTail, paramInstances);
        if (paramBayesNet.getDebug())
          System.out.print("Del " + paramOperation.m_nHead + " -> " + paramOperation.m_nTail); 
        break;
      case 2:
        applyArcDeletion(paramBayesNet, paramOperation.m_nHead, paramOperation.m_nTail, paramInstances);
        applyArcAddition(paramBayesNet, paramOperation.m_nTail, paramOperation.m_nHead, paramInstances);
        if (paramBayesNet.getDebug())
          System.out.print("Rev " + paramOperation.m_nHead + " -> " + paramOperation.m_nTail); 
        break;
    } 
  }
  
  void applyArcAddition(BayesNet paramBayesNet, int paramInt1, int paramInt2, Instances paramInstances) {
    ParentSet parentSet = paramBayesNet.getParentSet(paramInt1);
    parentSet.addParent(paramInt2, paramInstances);
  }
  
  void applyArcDeletion(BayesNet paramBayesNet, int paramInt1, int paramInt2, Instances paramInstances) {
    ParentSet parentSet = paramBayesNet.getParentSet(paramInt1);
    parentSet.deleteParent(paramInt2, paramInstances);
  }
  
  Operation findBestArcToAdd(BayesNet paramBayesNet, Instances paramInstances, Operation paramOperation) throws Exception {
    int i = paramInstances.numAttributes();
    for (byte b = 0; b < i; b++) {
      if (paramBayesNet.getParentSet(b).getNrOfParents() < this.m_nMaxNrOfParents)
        for (byte b1 = 0; b1 < i; b1++) {
          if (addArcMakesSense(paramBayesNet, paramInstances, b, b1)) {
            Operation operation = new Operation(this, b1, b, 0);
            double d = calcScoreWithExtraParent(operation.m_nHead, operation.m_nTail);
            if (d > paramOperation.m_fScore && isNotTabu(operation)) {
              paramOperation = operation;
              paramOperation.m_fScore = d;
            } 
          } 
        }  
    } 
    return paramOperation;
  }
  
  Operation findBestArcToDelete(BayesNet paramBayesNet, Instances paramInstances, Operation paramOperation) throws Exception {
    int i = paramInstances.numAttributes();
    for (byte b = 0; b < i; b++) {
      ParentSet parentSet = paramBayesNet.getParentSet(b);
      for (byte b1 = 0; b1 < parentSet.getNrOfParents(); b1++) {
        Operation operation = new Operation(this, parentSet.getParent(b1), b, 1);
        double d = calcScoreWithMissingParent(operation.m_nHead, operation.m_nTail);
        if (d > paramOperation.m_fScore && isNotTabu(operation)) {
          paramOperation = operation;
          paramOperation.m_fScore = d;
        } 
      } 
    } 
    return paramOperation;
  }
  
  Operation findBestArcToReverse(BayesNet paramBayesNet, Instances paramInstances, Operation paramOperation) throws Exception {
    int i = paramInstances.numAttributes();
    for (byte b = 0; b < i; b++) {
      ParentSet parentSet = paramBayesNet.getParentSet(b);
      for (byte b1 = 0; b1 < parentSet.getNrOfParents(); b1++) {
        int j = parentSet.getParent(b1);
        if (reverseArcMakesSense(paramBayesNet, paramInstances, b, j) && paramBayesNet.getParentSet(j).getNrOfParents() < this.m_nMaxNrOfParents) {
          Operation operation = new Operation(this, parentSet.getParent(b1), b, 2);
          double d = calcScoreWithReversedParent(operation.m_nHead, operation.m_nTail);
          if (d > paramOperation.m_fScore && isNotTabu(operation)) {
            paramOperation = operation;
            paramOperation.m_fScore = d;
          } 
        } 
      } 
    } 
    return paramOperation;
  }
  
  public void setMaxNrOfParents(int paramInt) {
    this.m_nMaxNrOfParents = paramInt;
  }
  
  public int getMaxNrOfParents() {
    return this.m_nMaxNrOfParents;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tMaximum number of parents\n", "P", 1, "-P <nr of parents>"));
    vector.addElement(new Option("\tUse arc reversal operation.\n\t(default false)", "R", 0, "-R"));
    vector.addElement(new Option("\tInitial structure is empty (instead of Naive Bayes)\n", "N", 0, "-N"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setUseArcReversal(Utils.getFlag('R', paramArrayOfString));
    setInitAsNaiveBayes(!Utils.getFlag('N', paramArrayOfString));
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0) {
      setMaxNrOfParents(Integer.parseInt(str));
    } else {
      setMaxNrOfParents(100000);
    } 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[7 + arrayOfString1.length];
    byte b1 = 0;
    if (getUseArcReversal())
      arrayOfString2[b1++] = "-R"; 
    if (!getInitAsNaiveBayes())
      arrayOfString2[b1++] = "-N"; 
    if (this.m_nMaxNrOfParents != 10000) {
      arrayOfString2[b1++] = "-P";
      arrayOfString2[b1++] = "" + this.m_nMaxNrOfParents;
    } 
    for (byte b2 = 0; b2 < arrayOfString1.length; b2++)
      arrayOfString2[b1++] = arrayOfString1[b2]; 
    while (b1 < arrayOfString2.length)
      arrayOfString2[b1++] = ""; 
    return arrayOfString2;
  }
  
  public void setInitAsNaiveBayes(boolean paramBoolean) {
    this.m_bInitAsNaiveBayes = paramBoolean;
  }
  
  public boolean getInitAsNaiveBayes() {
    return this.m_bInitAsNaiveBayes;
  }
  
  public boolean getUseArcReversal() {
    return this.m_bUseArcReversal;
  }
  
  public void setUseArcReversal(boolean paramBoolean) {
    this.m_bUseArcReversal = paramBoolean;
  }
  
  public String globalInfo() {
    return "This Bayes Network learning algorithm uses a hill climbing algorithm adding, deleting and reversing arcs. The search is not restricted by an order on the variables (unlike K2). The difference with B and B2 is that this hill climber also considers arrows part of the naive Bayes structure for deletion.";
  }
  
  public String useArcReversalTipText() {
    return "When set to true, the arc reversal operation is used in the search.";
  }
  
  class Operation implements Serializable {
    static final int OPERATION_ADD = 0;
    
    static final int OPERATION_DEL = 1;
    
    static final int OPERATION_REVERSE = 2;
    
    public int m_nTail;
    
    public int m_nHead;
    
    public int m_nOperation;
    
    public double m_fScore;
    
    private final HillClimber this$0;
    
    public Operation(HillClimber this$0) {
      this.this$0 = this$0;
      this.m_fScore = -1.0E100D;
    }
    
    public Operation(HillClimber this$0, int param1Int1, int param1Int2, int param1Int3) {
      this.this$0 = this$0;
      this.m_fScore = -1.0E100D;
      this.m_nHead = param1Int2;
      this.m_nTail = param1Int1;
      this.m_nOperation = param1Int3;
    }
    
    public boolean equals(Operation param1Operation) {
      return (param1Operation == null) ? false : ((this.m_nOperation == param1Operation.m_nOperation && this.m_nHead == param1Operation.m_nHead && this.m_nTail == param1Operation.m_nTail));
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\global\HillClimber.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */