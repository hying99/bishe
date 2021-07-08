package weka.classifiers.trees;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.j48.ModelSelection;
import weka.classifiers.trees.j48.NBTreeClassifierTree;
import weka.classifiers.trees.j48.NBTreeModelSelection;
import weka.core.AdditionalMeasureProducer;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Summarizable;
import weka.core.WeightedInstancesHandler;

public class NBTree extends Classifier implements WeightedInstancesHandler, Drawable, Summarizable, AdditionalMeasureProducer {
  private int m_minNumObj = 30;
  
  private NBTreeClassifierTree m_root;
  
  public String globalInfo() {
    return "Class for generating a decision tree with naive Bayes classifiers at the leaves. For more information, see\n\nRon Kohavi (1996). Scaling up the accuracy of naive-Bayes classifiers: a decision tree hybrid. Procedings of the Second Internaltional Conference on Knoledge Discovery and Data Mining.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    NBTreeModelSelection nBTreeModelSelection = new NBTreeModelSelection(this.m_minNumObj, paramInstances);
    this.m_root = new NBTreeClassifierTree((ModelSelection)nBTreeModelSelection);
    this.m_root.buildClassifier(paramInstances);
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    return this.m_root.classifyInstance(paramInstance);
  }
  
  public final double[] distributionForInstance(Instance paramInstance) throws Exception {
    return this.m_root.distributionForInstance(paramInstance, false);
  }
  
  public String toString() {
    return (this.m_root == null) ? "No classifier built" : ("NBTree\n------------------\n" + this.m_root.toString());
  }
  
  public int graphType() {
    return 1;
  }
  
  public String graph() throws Exception {
    return this.m_root.graph();
  }
  
  public String toSummaryString() {
    return "Number of leaves: " + this.m_root.numLeaves() + "\n" + "Size of the tree: " + this.m_root.numNodes() + "\n";
  }
  
  public double measureTreeSize() {
    return this.m_root.numNodes();
  }
  
  public double measureNumLeaves() {
    return this.m_root.numLeaves();
  }
  
  public double measureNumRules() {
    return this.m_root.numLeaves();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.compareToIgnoreCase("measureNumRules") == 0)
      return measureNumRules(); 
    if (paramString.compareToIgnoreCase("measureTreeSize") == 0)
      return measureTreeSize(); 
    if (paramString.compareToIgnoreCase("measureNumLeaves") == 0)
      return measureNumLeaves(); 
    throw new IllegalArgumentException(paramString + " not supported (j48)");
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(3);
    vector.addElement("measureTreeSize");
    vector.addElement("measureNumLeaves");
    vector.addElement("measureNumRules");
    return vector.elements();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new NBTree(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\NBTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */