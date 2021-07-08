package weka.classifiers.bayes.net.search.global;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;

public class GlobalScoreSearchAlgorithm extends SearchAlgorithm {
  BayesNet m_BayesNet;
  
  boolean m_bUseProb = true;
  
  int m_nNrOfFolds = 10;
  
  static final int LOOCV = 0;
  
  static final int KFOLDCV = 1;
  
  static final int CUMCV = 2;
  
  public static final Tag[] TAGS_CV_TYPE = new Tag[] { new Tag(0, "LOO-CV"), new Tag(1, "k-Fold-CV"), new Tag(2, "Cumulative-CV") };
  
  int m_nCVType = 0;
  
  public double calcScore(BayesNet paramBayesNet) throws Exception {
    switch (this.m_nCVType) {
      case 0:
        return leaveOneOutCV(paramBayesNet);
      case 2:
        return cumulativeCV(paramBayesNet);
      case 1:
        return kFoldCV(paramBayesNet, this.m_nNrOfFolds);
    } 
    throw new Exception("Unrecognized cross validation type encountered: " + this.m_nCVType);
  }
  
  public double calcScoreWithExtraParent(int paramInt1, int paramInt2) throws Exception {
    ParentSet parentSet = this.m_BayesNet.getParentSet(paramInt1);
    Instances instances = this.m_BayesNet.m_Instances;
    int i;
    for (i = 0; i < parentSet.getNrOfParents(); i++) {
      if (parentSet.getParent(i) == paramInt2)
        return -1.0E100D; 
    } 
    i = parentSet.getCardinalityOfParents() * instances.attribute(paramInt2).numValues();
    int j = instances.attribute(paramInt1).numValues();
    int[][] arrayOfInt = new int[i][j];
    parentSet.addParent(paramInt2, instances);
    double d = calcScore(this.m_BayesNet);
    parentSet.deleteLastParent(instances);
    return d;
  }
  
  public double calcScoreWithMissingParent(int paramInt1, int paramInt2) throws Exception {
    ParentSet parentSet = this.m_BayesNet.getParentSet(paramInt1);
    Instances instances = this.m_BayesNet.m_Instances;
    if (!parentSet.contains(paramInt2))
      return -1.0E100D; 
    int i = parentSet.deleteParent(paramInt2, instances);
    int j = parentSet.getCardinalityOfParents();
    int k = instances.attribute(paramInt1).numValues();
    int[][] arrayOfInt = new int[j][k];
    double d = calcScore(this.m_BayesNet);
    parentSet.addParent(paramInt2, i, instances);
    return d;
  }
  
  public double calcScoreWithReversedParent(int paramInt1, int paramInt2) throws Exception {
    ParentSet parentSet1 = this.m_BayesNet.getParentSet(paramInt1);
    ParentSet parentSet2 = this.m_BayesNet.getParentSet(paramInt2);
    Instances instances = this.m_BayesNet.m_Instances;
    if (!parentSet1.contains(paramInt2))
      return -1.0E100D; 
    int i = parentSet1.deleteParent(paramInt2, instances);
    parentSet2.addParent(paramInt1, instances);
    int j = parentSet1.getCardinalityOfParents();
    int k = instances.attribute(paramInt1).numValues();
    int[][] arrayOfInt = new int[j][k];
    double d = calcScore(this.m_BayesNet);
    parentSet2.deleteLastParent(instances);
    parentSet1.addParent(paramInt2, i, instances);
    return d;
  }
  
  public double leaveOneOutCV(BayesNet paramBayesNet) throws Exception {
    this.m_BayesNet = paramBayesNet;
    double d1 = 0.0D;
    double d2 = 0.0D;
    Instances instances = paramBayesNet.m_Instances;
    paramBayesNet.estimateCPTs();
    for (byte b = 0; b < instances.numInstances(); b++) {
      Instance instance = instances.instance(b);
      instance.setWeight(-instance.weight());
      paramBayesNet.updateClassifier(instance);
      d1 += accuracyIncrease(instance);
      d2 += instance.weight();
      instance.setWeight(-instance.weight());
      paramBayesNet.updateClassifier(instance);
    } 
    return d1 / d2;
  }
  
  public double cumulativeCV(BayesNet paramBayesNet) throws Exception {
    this.m_BayesNet = paramBayesNet;
    double d1 = 0.0D;
    double d2 = 0.0D;
    Instances instances = paramBayesNet.m_Instances;
    paramBayesNet.initCPTs();
    for (byte b = 0; b < instances.numInstances(); b++) {
      Instance instance = instances.instance(b);
      d1 += accuracyIncrease(instance);
      paramBayesNet.updateClassifier(instance);
      d2 += instance.weight();
    } 
    return d1 / d2;
  }
  
  public double kFoldCV(BayesNet paramBayesNet, int paramInt) throws Exception {
    this.m_BayesNet = paramBayesNet;
    double d1 = 0.0D;
    double d2 = 0.0D;
    Instances instances = paramBayesNet.m_Instances;
    paramBayesNet.estimateCPTs();
    int i = 0;
    int j = instances.numInstances() / paramInt;
    byte b = 1;
    while (i < instances.numInstances()) {
      int k;
      for (k = i; k < j; k++) {
        Instance instance = instances.instance(k);
        instance.setWeight(-instance.weight());
        paramBayesNet.updateClassifier(instance);
      } 
      for (k = i; k < j; k++) {
        Instance instance = instances.instance(k);
        instance.setWeight(-instance.weight());
        d1 += accuracyIncrease(instance);
        instance.setWeight(-instance.weight());
        d2 += instance.weight();
      } 
      for (k = i; k < j; k++) {
        Instance instance = instances.instance(k);
        instance.setWeight(-instance.weight());
        paramBayesNet.updateClassifier(instance);
      } 
      i = j;
      j = ++b * instances.numInstances() / paramInt;
    } 
    return d1 / d2;
  }
  
  double accuracyIncrease(Instance paramInstance) throws Exception {
    if (this.m_bUseProb) {
      double[] arrayOfDouble = this.m_BayesNet.distributionForInstance(paramInstance);
      return arrayOfDouble[(int)paramInstance.classValue()] * paramInstance.weight();
    } 
    return (this.m_BayesNet.classifyInstance(paramInstance) == paramInstance.classValue()) ? paramInstance.weight() : 0.0D;
  }
  
  public boolean getUseProb() {
    return this.m_bUseProb;
  }
  
  public void setUseProb(boolean paramBoolean) {
    this.m_bUseProb = paramBoolean;
  }
  
  public void setCVType(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_CV_TYPE)
      this.m_nCVType = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public SelectedTag getCVType() {
    return new SelectedTag(this.m_nCVType, TAGS_CV_TYPE);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tScore type (LOO-CV,k-Fold-CV,Cumulative-CV)\n", "S", 1, "-S [LOO-CV|k-Fold-CV|Cumulative-CV]"));
    vector.addElement(new Option("\tUse probabilistic or 0/1 scoring.\n\t(default probabilistic scoring)", "Q", 0, "-Q"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('S', paramArrayOfString);
    if (str.compareTo("LOO-CV") == 0)
      setCVType(new SelectedTag(0, TAGS_CV_TYPE)); 
    if (str.compareTo("k-Fold-CV") == 0)
      setCVType(new SelectedTag(1, TAGS_CV_TYPE)); 
    if (str.compareTo("Cumulative-CV") == 0)
      setCVType(new SelectedTag(2, TAGS_CV_TYPE)); 
    setUseProb(!Utils.getFlag('Q', paramArrayOfString));
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[3 + arrayOfString1.length];
    byte b = 0;
    arrayOfString2[b++] = "-S";
    switch (this.m_nCVType) {
      case 0:
        arrayOfString2[b++] = "LOO-CV";
        break;
      case 1:
        arrayOfString2[b++] = "k-Fold-CV";
        break;
      case 2:
        arrayOfString2[b++] = "Cumulative-CV";
        break;
    } 
    if (getUseProb())
      arrayOfString2[b++] = "-Q"; 
    while (b < arrayOfString2.length)
      arrayOfString2[b++] = ""; 
    return arrayOfString2;
  }
  
  public String CVTypeTipText() {
    return "Select cross validation strategy to be used in searching for networks.LOO-CV = Leave one out cross validation\nk-Fold-CV = k fold cross validation\nCumulative-CV = cumulative cross validation.";
  }
  
  public String useProbTipText() {
    return "If set to true, the probability of the class if returned in the estimate of the accuracy. If set to false, the accuracy estimate is only increased if the classifier returns exactly the correct class.";
  }
  
  public String globalInfo() {
    return "This Bayes Network learning algorithm uses cross validation to estimate classification accuracy.";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\global\GlobalScoreSearchAlgorithm.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */