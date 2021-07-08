package weka.classifiers.trees.m5;

import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class Rule implements Serializable {
  protected static int LEFT = 0;
  
  protected static int RIGHT = 1;
  
  private Instances m_instances;
  
  private int m_classIndex;
  
  private int m_numAttributes;
  
  private int m_numInstances;
  
  private int[] m_splitAtts;
  
  private double[] m_splitVals;
  
  private RuleNode[] m_internalNodes;
  
  private int[] m_relOps;
  
  private RuleNode m_ruleModel;
  
  protected RuleNode m_topOfTree;
  
  private double m_globalStdDev;
  
  private double m_globalAbsDev;
  
  private Instances m_covered;
  
  private int m_numCovered;
  
  private Instances m_notCovered;
  
  private boolean m_useTree = false;
  
  private boolean m_smoothPredictions = false;
  
  private boolean m_saveInstances;
  
  private boolean m_regressionTree;
  
  private boolean m_useUnpruned = false;
  
  private double m_minNumInstances = 4.0D;
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_instances = null;
    this.m_topOfTree = null;
    this.m_covered = null;
    this.m_notCovered = null;
    this.m_ruleModel = null;
    this.m_splitAtts = null;
    this.m_splitVals = null;
    this.m_relOps = null;
    this.m_internalNodes = null;
    this.m_instances = paramInstances;
    this.m_classIndex = this.m_instances.classIndex();
    this.m_numAttributes = this.m_instances.numAttributes();
    this.m_numInstances = this.m_instances.numInstances();
    this.m_globalStdDev = stdDev(this.m_classIndex, this.m_instances);
    this.m_globalAbsDev = absDev(this.m_classIndex, this.m_instances);
    this.m_topOfTree = new RuleNode(this.m_globalStdDev, this.m_globalAbsDev, null);
    this.m_topOfTree.setSaveInstances(this.m_saveInstances);
    this.m_topOfTree.setRegressionTree(this.m_regressionTree);
    this.m_topOfTree.setMinNumInstances(this.m_minNumInstances);
    this.m_topOfTree.buildClassifier(this.m_instances);
    if (!this.m_useUnpruned) {
      this.m_topOfTree.prune();
    } else {
      this.m_topOfTree.installLinearModels();
    } 
    if (this.m_smoothPredictions)
      this.m_topOfTree.installSmoothedModels(); 
    this.m_topOfTree.numLeaves(0);
    if (!this.m_useTree)
      makeRule(); 
    this.m_instances = new Instances(this.m_instances, 0);
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    if (this.m_useTree)
      return this.m_topOfTree.classifyInstance(paramInstance); 
    if (this.m_splitAtts.length > 0)
      for (byte b = 0; b < this.m_relOps.length; b++) {
        if (this.m_relOps[b] == LEFT) {
          if (paramInstance.value(this.m_splitAtts[b]) > this.m_splitVals[b])
            throw new Exception("Rule does not classify instance"); 
        } else if (paramInstance.value(this.m_splitAtts[b]) <= this.m_splitVals[b]) {
          throw new Exception("Rule does not classify instance");
        } 
      }  
    return this.m_ruleModel.classifyInstance(paramInstance);
  }
  
  public RuleNode topOfTree() {
    return this.m_topOfTree;
  }
  
  private void makeRule() throws Exception {
    RuleNode[] arrayOfRuleNode = new RuleNode[1];
    double[] arrayOfDouble = new double[1];
    this.m_notCovered = new Instances(this.m_instances, 0);
    this.m_covered = new Instances(this.m_instances, 0);
    arrayOfDouble[0] = -1.0D;
    arrayOfRuleNode[0] = null;
    this.m_topOfTree.findBestLeaf(arrayOfDouble, arrayOfRuleNode);
    RuleNode ruleNode = arrayOfRuleNode[0];
    if (ruleNode == null)
      throw new Exception("Unable to generate rule!"); 
    this.m_ruleModel = ruleNode;
    byte b1 = 0;
    while (ruleNode.parentNode() != null) {
      b1++;
      ruleNode = ruleNode.parentNode();
    } 
    ruleNode = arrayOfRuleNode[0];
    this.m_relOps = new int[b1];
    this.m_splitAtts = new int[b1];
    this.m_splitVals = new double[b1];
    if (this.m_smoothPredictions)
      this.m_internalNodes = new RuleNode[b1]; 
    byte b2;
    for (b2 = 0; ruleNode.parentNode() != null; b2++) {
      this.m_splitAtts[b2] = ruleNode.parentNode().splitAtt();
      this.m_splitVals[b2] = ruleNode.parentNode().splitVal();
      if (ruleNode.parentNode().leftNode() == ruleNode) {
        this.m_relOps[b2] = LEFT;
      } else {
        this.m_relOps[b2] = RIGHT;
      } 
      if (this.m_smoothPredictions)
        this.m_internalNodes[b2] = ruleNode.parentNode(); 
      ruleNode = ruleNode.parentNode();
    } 
    for (b2 = 0; b2 < this.m_numInstances; b2++) {
      boolean bool = true;
      for (byte b = 0; b < this.m_relOps.length; b++) {
        if (this.m_relOps[b] == LEFT) {
          if (this.m_instances.instance(b2).value(this.m_splitAtts[b]) > this.m_splitVals[b]) {
            this.m_notCovered.add(this.m_instances.instance(b2));
            bool = false;
            break;
          } 
        } else if (this.m_instances.instance(b2).value(this.m_splitAtts[b]) <= this.m_splitVals[b]) {
          this.m_notCovered.add(this.m_instances.instance(b2));
          bool = false;
          break;
        } 
      } 
      if (bool)
        this.m_numCovered++; 
    } 
  }
  
  public String toString() {
    return this.m_useTree ? treeToString() : ruleToString();
  }
  
  private String treeToString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_topOfTree == null)
      return "Tree/Rule has not been built yet!"; 
    stringBuffer.append("M5 " + (this.m_useUnpruned ? "unpruned " : "pruned ") + (this.m_regressionTree ? "regression " : "model ") + "tree:\n");
    if (this.m_smoothPredictions == true)
      stringBuffer.append("(using smoothed linear models)\n"); 
    stringBuffer.append(this.m_topOfTree.treeToString(0));
    stringBuffer.append(this.m_topOfTree.printLeafModels());
    stringBuffer.append("\nNumber of Rules : " + this.m_topOfTree.numberOfLinearModels());
    return stringBuffer.toString();
  }
  
  private String ruleToString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_splitAtts.length > 0) {
      stringBuffer.append("IF\n");
      for (int i = this.m_splitAtts.length - 1; i >= 0; i--) {
        stringBuffer.append("\t" + this.m_covered.attribute(this.m_splitAtts[i]).name() + " ");
        if (this.m_relOps[i] == 0) {
          stringBuffer.append("<= ");
        } else {
          stringBuffer.append("> ");
        } 
        stringBuffer.append(Utils.doubleToString(this.m_splitVals[i], 1, 3) + "\n");
      } 
      stringBuffer.append("THEN\n");
    } 
    if (this.m_ruleModel != null)
      try {
        stringBuffer.append(this.m_ruleModel.printNodeLinearModel());
        stringBuffer.append(" [" + this.m_numCovered);
        if (this.m_globalAbsDev > 0.0D) {
          stringBuffer.append("/" + Utils.doubleToString(100.0D * this.m_ruleModel.rootMeanSquaredError() / this.m_globalAbsDev, 1, 3) + "%]\n\n");
        } else {
          stringBuffer.append("]\n\n");
        } 
      } catch (Exception exception) {
        return "Can't print rule";
      }  
    return stringBuffer.toString();
  }
  
  public void setUnpruned(boolean paramBoolean) {
    this.m_useUnpruned = paramBoolean;
  }
  
  public boolean getUnpruned() {
    return this.m_useUnpruned;
  }
  
  public void setUseTree(boolean paramBoolean) {
    this.m_useTree = paramBoolean;
  }
  
  public boolean getUseTree() {
    return this.m_useTree;
  }
  
  public void setSmoothing(boolean paramBoolean) {
    this.m_smoothPredictions = paramBoolean;
  }
  
  public boolean getSmoothing() {
    return this.m_smoothPredictions;
  }
  
  public Instances notCoveredInstances() {
    return this.m_notCovered;
  }
  
  protected static final double stdDev(int paramInt, Instances paramInstances) {
    double d1;
    byte b2 = 0;
    double d2 = 0.0D;
    double d3 = 0.0D;
    for (byte b1 = 0; b1 <= paramInstances.numInstances() - 1; b1++) {
      b2++;
      double d = paramInstances.instance(b1).value(paramInt);
      d2 += d;
      d3 += d * d;
    } 
    if (b2 > 1) {
      double d = (d3 - d2 * d2 / b2) / b2;
      d = Math.abs(d);
      d1 = Math.sqrt(d);
    } else {
      d1 = 0.0D;
    } 
    return d1;
  }
  
  protected static final double absDev(int paramInt, Instances paramInstances) {
    double d3;
    double d1 = 0.0D;
    double d2 = 0.0D;
    byte b;
    for (b = 0; b <= paramInstances.numInstances() - 1; b++)
      d1 += paramInstances.instance(b).value(paramInt); 
    if (paramInstances.numInstances() > 1) {
      d1 /= paramInstances.numInstances();
      for (b = 0; b <= paramInstances.numInstances() - 1; b++)
        d2 += Math.abs(paramInstances.instance(b).value(paramInt) - d1); 
      d3 = d2 / paramInstances.numInstances();
    } else {
      d3 = 0.0D;
    } 
    return d3;
  }
  
  protected void setSaveInstances(boolean paramBoolean) {
    this.m_saveInstances = paramBoolean;
  }
  
  public boolean getRegressionTree() {
    return this.m_regressionTree;
  }
  
  public void setRegressionTree(boolean paramBoolean) {
    this.m_regressionTree = paramBoolean;
  }
  
  public void setMinNumInstances(double paramDouble) {
    this.m_minNumInstances = paramDouble;
  }
  
  public double getMinNumInstances() {
    return this.m_minNumInstances;
  }
  
  public RuleNode getM5RootNode() {
    return this.m_topOfTree;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\m5\Rule.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */