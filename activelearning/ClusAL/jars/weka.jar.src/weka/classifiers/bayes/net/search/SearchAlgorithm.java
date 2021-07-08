package weka.classifiers.bayes.net.search;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.Instances;
import weka.core.OptionHandler;

public class SearchAlgorithm implements OptionHandler, Serializable {
  protected int m_nMaxNrOfParents = 1;
  
  protected boolean m_bInitAsNaiveBayes = true;
  
  protected boolean m_bMarkovBlanketClassifier = false;
  
  protected boolean addArcMakesSense(BayesNet paramBayesNet, Instances paramInstances, int paramInt1, int paramInt2) {
    if (paramInt1 == paramInt2)
      return false; 
    if (isArc(paramBayesNet, paramInt1, paramInt2))
      return false; 
    int i = paramInstances.numAttributes();
    boolean[] arrayOfBoolean = new boolean[i];
    byte b;
    for (b = 0; b < i; b++)
      arrayOfBoolean[b] = false; 
    paramBayesNet.getParentSet(paramInt1).addParent(paramInt2, paramInstances);
    for (b = 0; b < i; b++) {
      boolean bool = false;
      for (byte b1 = 0; !bool && b1 < i; b1++) {
        if (!arrayOfBoolean[b1]) {
          boolean bool1 = true;
          for (byte b2 = 0; b2 < paramBayesNet.getParentSet(b1).getNrOfParents(); b2++) {
            if (!arrayOfBoolean[paramBayesNet.getParentSet(b1).getParent(b2)])
              bool1 = false; 
          } 
          if (bool1) {
            arrayOfBoolean[b1] = true;
            bool = true;
          } 
        } 
      } 
      if (!bool) {
        paramBayesNet.getParentSet(paramInt1).deleteLastParent(paramInstances);
        return false;
      } 
    } 
    paramBayesNet.getParentSet(paramInt1).deleteLastParent(paramInstances);
    return true;
  }
  
  protected boolean reverseArcMakesSense(BayesNet paramBayesNet, Instances paramInstances, int paramInt1, int paramInt2) {
    if (paramInt1 == paramInt2)
      return false; 
    if (!isArc(paramBayesNet, paramInt1, paramInt2))
      return false; 
    int i = paramInstances.numAttributes();
    boolean[] arrayOfBoolean = new boolean[i];
    byte b;
    for (b = 0; b < i; b++)
      arrayOfBoolean[b] = false; 
    paramBayesNet.getParentSet(paramInt2).addParent(paramInt1, paramInstances);
    for (b = 0; b < i; b++) {
      boolean bool = false;
      for (byte b1 = 0; !bool && b1 < i; b1++) {
        if (!arrayOfBoolean[b1]) {
          ParentSet parentSet = paramBayesNet.getParentSet(b1);
          boolean bool1 = true;
          for (byte b2 = 0; b2 < parentSet.getNrOfParents(); b2++) {
            if (!arrayOfBoolean[parentSet.getParent(b2)] && (b1 != paramInt1 || parentSet.getParent(b2) != paramInt2))
              bool1 = false; 
          } 
          if (bool1) {
            arrayOfBoolean[b1] = true;
            bool = true;
          } 
        } 
      } 
      if (!bool) {
        paramBayesNet.getParentSet(paramInt2).deleteLastParent(paramInstances);
        return false;
      } 
    } 
    paramBayesNet.getParentSet(paramInt2).deleteLastParent(paramInstances);
    return true;
  }
  
  protected boolean isArc(BayesNet paramBayesNet, int paramInt1, int paramInt2) {
    for (byte b = 0; b < paramBayesNet.getParentSet(paramInt1).getNrOfParents(); b++) {
      if (paramBayesNet.getParentSet(paramInt1).getParent(b) == paramInt2)
        return true; 
    } 
    return false;
  }
  
  public Enumeration listOptions() {
    return (new Vector(0)).elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {}
  
  public String[] getOptions() {
    return new String[0];
  }
  
  public String toString() {
    return "SearchAlgorithm\n";
  }
  
  public void buildStructure(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    if (this.m_bInitAsNaiveBayes) {
      int i = paramInstances.classIndex();
      for (byte b = 0; b < paramInstances.numAttributes(); b++) {
        if (b != i)
          paramBayesNet.getParentSet(b).addParent(i, paramInstances); 
      } 
    } 
    search(paramBayesNet, paramInstances);
    if (this.m_bMarkovBlanketClassifier)
      doMarkovBlanketCorrection(paramBayesNet, paramInstances); 
  }
  
  protected void search(BayesNet paramBayesNet, Instances paramInstances) throws Exception {}
  
  protected void doMarkovBlanketCorrection(BayesNet paramBayesNet, Instances paramInstances) {
    int i = paramInstances.classIndex();
    ParentSet parentSet = new ParentSet();
    int j = 0;
    parentSet.addParent(i, paramInstances);
    while (j != parentSet.getNrOfParents()) {
      j = parentSet.getNrOfParents();
      for (byte b1 = 0; b1 < j; b1++) {
        int k = parentSet.getParent(b1);
        ParentSet parentSet1 = paramBayesNet.getParentSet(k);
        for (byte b2 = 0; b2 < parentSet1.getNrOfParents(); b2++) {
          if (!parentSet.contains(parentSet1.getParent(b2)))
            parentSet.addParent(parentSet1.getParent(b2), paramInstances); 
        } 
      } 
    } 
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      boolean bool = (b == i) ? true : false;
      bool = (paramBayesNet.getParentSet(b).contains(i) || paramBayesNet.getParentSet(i).contains(b)) ? true : false;
      for (byte b1 = 0; !bool && b1 < paramInstances.numAttributes(); b1++)
        bool = (paramBayesNet.getParentSet(b1).contains(b) && paramBayesNet.getParentSet(b1).contains(i)) ? true : false; 
      if (!bool)
        if (parentSet.contains(b) && paramBayesNet.getParentSet(i).getCardinalityOfParents() < 1024) {
          paramBayesNet.getParentSet(i).addParent(b, paramInstances);
        } else {
          paramBayesNet.getParentSet(b).addParent(i, paramInstances);
        }  
    } 
  }
  
  public void setMarkovBlanketClassifier(boolean paramBoolean) {
    this.m_bMarkovBlanketClassifier = paramBoolean;
  }
  
  public boolean getMarkovBlanketClassifier() {
    return this.m_bMarkovBlanketClassifier;
  }
  
  public String maxNrOfParentsTipText() {
    return "Set the maximum number of parents a node in the Bayes net can have. When initialized as Naive Bayes, setting this parameter to 1 results in a Naive Bayes classifier. When set to 2, a Tree Augmented Bayes Network (TAN) is learned, and when set >2, a Bayes Net Augmented Bayes Network (BAN) is learned. By setting it to a value much larger than the number of nodes in the network (the default of 100000 pretty much guarantees this), no restriction on the number of parents is enforced";
  }
  
  public String initAsNaiveBayesTipText() {
    return "When set to true (default), the initial network used for structure learning is a Naive Bayes Network, that is, a network with an arrow from the classifier node to each other node. When set to false, an empty network is used as initial network structure";
  }
  
  public String markovBlanketClassifierTipText() {
    return "When set to true (default is false), after a network structure is learned a Markov Blanket correction is applied to the network structure. This ensures that all nodes in the network are part of the Markov blanket of the classifier node.";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\SearchAlgorithm.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */