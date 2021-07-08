package weka.classifiers.trees;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.m5.M5Base;
import weka.classifiers.trees.m5.Rule;
import weka.core.Drawable;
import weka.core.Option;
import weka.core.Utils;

public class M5P extends M5Base implements Drawable {
  public M5P() {
    setGenerateRules(false);
  }
  
  public int graphType() {
    return 1;
  }
  
  public String graph() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("digraph M5Tree {\n");
    Rule rule = (Rule)this.m_ruleSet.elementAt(0);
    rule.topOfTree().graph(stringBuffer);
    stringBuffer.append("}\n");
    return stringBuffer.toString();
  }
  
  public void setSaveInstances(boolean paramBoolean) {
    this.m_saveInstances = paramBoolean;
  }
  
  public boolean getSaveInstances() {
    return this.m_saveInstances;
  }
  
  public Enumeration listOptions() {
    Enumeration enumeration = super.listOptions();
    Vector vector = new Vector();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    vector.addElement(new Option("\tSave instances at the nodes in\n\tthe tree (for visualization purposes)\n", "L", 0, "-L"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setSaveInstances(Utils.getFlag('L', paramArrayOfString));
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 1];
    int i = arrayOfString1.length;
    for (byte b = 0; b < i; b++)
      arrayOfString2[b] = arrayOfString1[b]; 
    if (getSaveInstances())
      arrayOfString2[i++] = "-L"; 
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new M5P(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\M5P.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */