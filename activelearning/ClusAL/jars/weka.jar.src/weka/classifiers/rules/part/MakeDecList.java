package weka.classifiers.rules.part;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.trees.j48.ModelSelection;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

public class MakeDecList implements Serializable {
  private Vector theRules;
  
  private double CF = 0.25D;
  
  private int minNumObj;
  
  private ModelSelection toSelectModeL;
  
  private int numSetS = 3;
  
  private boolean reducedErrorPruning = false;
  
  private boolean unpruned = false;
  
  private int m_seed = 1;
  
  public MakeDecList(ModelSelection paramModelSelection, int paramInt) {
    this.toSelectModeL = paramModelSelection;
    this.reducedErrorPruning = false;
    this.unpruned = true;
    this.minNumObj = paramInt;
  }
  
  public MakeDecList(ModelSelection paramModelSelection, double paramDouble, int paramInt) {
    this.toSelectModeL = paramModelSelection;
    this.CF = paramDouble;
    this.reducedErrorPruning = false;
    this.unpruned = false;
    this.minNumObj = paramInt;
  }
  
  public MakeDecList(ModelSelection paramModelSelection, int paramInt1, int paramInt2, int paramInt3) {
    this.toSelectModeL = paramModelSelection;
    this.numSetS = paramInt1;
    this.reducedErrorPruning = true;
    this.unpruned = false;
    this.minNumObj = paramInt2;
    this.m_seed = paramInt3;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    Instances instances1;
    Instances instances2;
    byte b = 0;
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Class is numeric!"); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    this.theRules = new Vector();
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    if (paramInstances.numInstances() == 0)
      throw new Exception("No training instances/Only instances with missing class!"); 
    if (this.reducedErrorPruning && !this.unpruned) {
      Random random = new Random(this.m_seed);
      paramInstances.randomize(random);
      paramInstances.stratify(this.numSetS);
      instances1 = paramInstances.trainCV(this.numSetS, this.numSetS - 1, random);
      instances2 = paramInstances.testCV(this.numSetS, this.numSetS - 1);
    } else {
      instances1 = paramInstances;
      instances2 = null;
    } 
    while (Utils.gr(instances1.numInstances(), 0.0D)) {
      ClassifierDecList classifierDecList;
      if (this.unpruned) {
        classifierDecList = new ClassifierDecList(this.toSelectModeL, this.minNumObj);
        classifierDecList.buildRule(instances1);
      } else if (this.reducedErrorPruning) {
        classifierDecList = new PruneableDecList(this.toSelectModeL, this.minNumObj);
        classifierDecList.buildRule(instances1, instances2);
      } else {
        classifierDecList = new C45PruneableDecList(this.toSelectModeL, this.CF, this.minNumObj);
        classifierDecList.buildRule(instances1);
      } 
      b++;
      Instances instances = new Instances(instances1, instances1.numInstances());
      Enumeration enumeration = instances1.enumerateInstances();
      while (enumeration.hasMoreElements()) {
        Instance instance = enumeration.nextElement();
        double d = classifierDecList.weight(instance);
        if (Utils.sm(d, 1.0D)) {
          instance.setWeight(instance.weight() * (1.0D - d));
          instances.add(instance);
        } 
      } 
      instances.compactify();
      instances1 = instances;
      if (this.reducedErrorPruning && !this.unpruned) {
        Instances instances3 = new Instances(instances2, instances2.numInstances());
        enumeration = instances2.enumerateInstances();
        while (enumeration.hasMoreElements()) {
          Instance instance = enumeration.nextElement();
          double d = classifierDecList.weight(instance);
          if (Utils.sm(d, 1.0D)) {
            instance.setWeight(instance.weight() * (1.0D - d));
            instances3.add(instance);
          } 
        } 
        instances3.compactify();
        instances2 = instances3;
      } 
      this.theRules.addElement(classifierDecList);
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.theRules.size(); b++)
      stringBuffer.append((ClassifierDecList)this.theRules.elementAt(b) + "\n"); 
    stringBuffer.append("Number of Rules  : \t" + this.theRules.size() + "\n");
    return stringBuffer.toString();
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    double d = -1.0D;
    byte b1 = 0;
    double[] arrayOfDouble = distributionForInstance(paramInstance);
    for (byte b2 = 0; b2 < arrayOfDouble.length; b2++) {
      if (Utils.gr(arrayOfDouble[b2], d)) {
        b1 = b2;
        d = arrayOfDouble[b2];
      } 
    } 
    return b1;
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble1 = null;
    double d = 1.0D;
    double[] arrayOfDouble2 = new double[paramInstance.numClasses()];
    for (byte b = 0; Utils.gr(d, 0.0D); b++) {
      double d1 = ((ClassifierDecList)this.theRules.elementAt(b)).weight(paramInstance);
      if (Utils.gr(d1, 0.0D)) {
        arrayOfDouble1 = ((ClassifierDecList)this.theRules.elementAt(b)).distributionForInstance(paramInstance);
        for (byte b1 = 0; b1 < arrayOfDouble2.length; b1++)
          arrayOfDouble2[b1] = arrayOfDouble2[b1] + d * arrayOfDouble1[b1]; 
        d *= 1.0D - d1;
      } 
    } 
    return arrayOfDouble2;
  }
  
  public int numRules() {
    return this.theRules.size();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\part\MakeDecList.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */