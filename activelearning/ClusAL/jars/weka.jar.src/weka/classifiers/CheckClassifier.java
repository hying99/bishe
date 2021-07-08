package weka.classifiers;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.rules.ZeroR;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class CheckClassifier implements OptionHandler {
  protected Classifier m_Classifier = (Classifier)new ZeroR();
  
  protected String[] m_ClassifierOptions;
  
  protected String m_AnalysisResults;
  
  protected boolean m_Debug;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tTurn on debugging output.", "D", 0, "-D"));
    vector.addElement(new Option("\tFull name of the classifier analysed.\n\teg: weka.classifiers.bayes.NaiveBayes", "W", 1, "-W"));
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to classifier " + this.m_Classifier.getClass().getName() + ":"));
      Enumeration enumeration = this.m_Classifier.listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setDebug(Utils.getFlag('D', paramArrayOfString));
    String str = Utils.getOption('W', paramArrayOfString);
    if (str.length() == 0)
      throw new Exception("A classifier must be specified with the -W option."); 
    setClassifier(Classifier.forName(str, Utils.partitionOptions(paramArrayOfString)));
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler)
      arrayOfString1 = this.m_Classifier.getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 4];
    int i = 0;
    if (getDebug())
      arrayOfString2[i++] = "-D"; 
    if (getClassifier() != null) {
      arrayOfString2[i++] = "-W";
      arrayOfString2[i++] = getClassifier().getClass().getName();
    } 
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public void doTests() {
    if (getClassifier() == null) {
      System.out.println("\n=== No classifier set ===");
      return;
    } 
    System.out.println("\n=== Check on Classifier: " + getClassifier().getClass().getName() + " ===\n");
    canTakeOptions();
    boolean bool1 = updateableClassifier();
    boolean bool2 = weightedInstancesHandler();
    testsPerClassType(false, bool1, bool2);
    testsPerClassType(true, bool1, bool2);
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_Classifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_Classifier;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      CheckClassifier checkClassifier = new CheckClassifier();
      try {
        checkClassifier.setOptions(paramArrayOfString);
        Utils.checkForRemainingOptions(paramArrayOfString);
      } catch (Exception exception) {
        String str = exception.getMessage() + "\nCheckClassifier Options:\n\n";
        Enumeration enumeration = checkClassifier.listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          str = str + option.synopsis() + "\n" + option.description() + "\n";
        } 
        throw new Exception(str);
      } 
      checkClassifier.doTests();
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
  
  protected void testsPerClassType(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    boolean bool1 = canPredict(true, false, paramBoolean1);
    boolean bool2 = canPredict(false, true, paramBoolean1);
    if (bool1 || bool2) {
      if (paramBoolean3)
        instanceWeights(bool1, bool2, paramBoolean1); 
      if (!paramBoolean1)
        canHandleNClasses(bool1, bool2, 4); 
      canHandleZeroTraining(bool1, bool2, paramBoolean1);
      boolean bool3 = canHandleMissing(bool1, bool2, paramBoolean1, true, false, 20);
      if (bool3)
        canHandleMissing(bool1, bool2, paramBoolean1, true, false, 100); 
      boolean bool4 = canHandleMissing(bool1, bool2, paramBoolean1, false, true, 20);
      if (bool4)
        canHandleMissing(bool1, bool2, paramBoolean1, false, true, 100); 
      correctBuildInitialisation(bool1, bool2, paramBoolean1);
      datasetIntegrity(bool1, bool2, paramBoolean1, bool3, bool4);
      doesntUseTestClassVal(bool1, bool2, paramBoolean1);
      if (paramBoolean2)
        updatingEquality(bool1, bool2, paramBoolean1); 
    } 
  }
  
  protected boolean canTakeOptions() {
    System.out.print("options...");
    if (this.m_Classifier instanceof OptionHandler) {
      System.out.println("yes");
      if (this.m_Debug) {
        System.out.println("\n=== Full report ===");
        Enumeration enumeration = this.m_Classifier.listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          System.out.print(option.synopsis() + "\n" + option.description() + "\n");
        } 
        System.out.println("\n");
      } 
      return true;
    } 
    System.out.println("no");
    return false;
  }
  
  protected boolean updateableClassifier() {
    System.out.print("updateable classifier...");
    if (this.m_Classifier instanceof UpdateableClassifier) {
      System.out.println("yes");
      return true;
    } 
    System.out.println("no");
    return false;
  }
  
  protected boolean weightedInstancesHandler() {
    System.out.print("weighted instances classifier...");
    if (this.m_Classifier instanceof weka.core.WeightedInstancesHandler) {
      System.out.println("yes");
      return true;
    } 
    System.out.println("no");
    return false;
  }
  
  protected boolean canPredict(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    System.out.print("basic predict");
    printAttributeSummary(paramBoolean1, paramBoolean2, paramBoolean3);
    System.out.print("...");
    FastVector fastVector = new FastVector();
    fastVector.addElement("nominal");
    fastVector.addElement("numeric");
    byte b1 = 20;
    byte b2 = 20;
    byte b3 = 2;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    return runBasicTest(paramBoolean1, paramBoolean2, paramBoolean3, bool1, bool2, bool3, b1, b2, b3, fastVector);
  }
  
  protected boolean canHandleNClasses(boolean paramBoolean1, boolean paramBoolean2, int paramInt) {
    System.out.print("more than two class problems");
    printAttributeSummary(paramBoolean1, paramBoolean2, false);
    System.out.print("...");
    FastVector fastVector = new FastVector();
    fastVector.addElement("number");
    fastVector.addElement("class");
    byte b1 = 20;
    byte b2 = 20;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    return runBasicTest(paramBoolean1, paramBoolean2, false, bool1, bool2, bool3, b1, b2, paramInt, fastVector);
  }
  
  protected boolean canHandleZeroTraining(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    System.out.print("handle zero training instances");
    printAttributeSummary(paramBoolean1, paramBoolean2, paramBoolean3);
    System.out.print("...");
    FastVector fastVector = new FastVector();
    fastVector.addElement("train");
    fastVector.addElement("value");
    boolean bool1 = false;
    byte b1 = 20;
    byte b2 = 2;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    return runBasicTest(paramBoolean1, paramBoolean2, paramBoolean3, bool2, bool3, bool4, bool1, b1, b2, fastVector);
  }
  
  protected boolean correctBuildInitialisation(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    System.out.print("correct initialisation during buildClassifier");
    printAttributeSummary(paramBoolean1, paramBoolean2, paramBoolean3);
    System.out.print("...");
    byte b1 = 20;
    byte b2 = 20;
    byte b3 = 2;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    Instances instances1 = null;
    Instances instances2 = null;
    Instances instances3 = null;
    Instances instances4 = null;
    Classifier classifier = null;
    Evaluation evaluation1 = null;
    Evaluation evaluation2 = null;
    Evaluation evaluation3 = null;
    boolean bool4 = false;
    byte b4 = 0;
    try {
      instances1 = makeTestDataset(42, b1, paramBoolean1 ? 2 : 0, paramBoolean2 ? 1 : 0, b3, paramBoolean3);
      instances3 = makeTestDataset(84, b1, paramBoolean1 ? 3 : 0, paramBoolean2 ? 2 : 0, b3, paramBoolean3);
      instances2 = makeTestDataset(24, b2, paramBoolean1 ? 2 : 0, paramBoolean2 ? 1 : 0, b3, paramBoolean3);
      instances4 = makeTestDataset(48, b2, paramBoolean1 ? 3 : 0, paramBoolean2 ? 2 : 0, b3, paramBoolean3);
      if (paramBoolean1) {
        instances1.deleteAttributeAt(0);
        instances2.deleteAttributeAt(0);
        instances3.deleteAttributeAt(0);
        instances4.deleteAttributeAt(0);
      } 
      if (bool1) {
        addMissing(instances1, bool1, bool2, bool3);
        addMissing(instances2, Math.min(bool1, 50), bool2, bool3);
        addMissing(instances3, bool1, bool2, bool3);
        addMissing(instances4, Math.min(bool1, 50), bool2, bool3);
      } 
      classifier = Classifier.makeCopies(getClassifier(), 1)[0];
      evaluation1 = new Evaluation(instances1);
      evaluation2 = new Evaluation(instances1);
      evaluation3 = new Evaluation(instances3);
    } catch (Exception exception) {
      throw new Error("Error setting up for tests: " + exception.getMessage());
    } 
    try {
      b4 = 0;
      classifier.buildClassifier(instances1);
      bool4 = true;
      if (!testWRTZeroR(classifier, evaluation1, instances1, instances2))
        throw new Exception("Scheme performs worse than ZeroR"); 
      b4 = 1;
      bool4 = false;
      classifier.buildClassifier(instances3);
      bool4 = true;
      if (!testWRTZeroR(classifier, evaluation3, instances3, instances4))
        throw new Exception("Scheme performs worse than ZeroR"); 
      b4 = 2;
      bool4 = false;
      classifier.buildClassifier(instances1);
      bool4 = true;
      if (!testWRTZeroR(classifier, evaluation2, instances1, instances2))
        throw new Exception("Scheme performs worse than ZeroR"); 
      b4 = 3;
      if (!evaluation1.equals(evaluation2)) {
        if (this.m_Debug) {
          System.out.println("\n=== Full report ===\n" + evaluation1.toSummaryString("\nFirst buildClassifier()", true) + "\n\n");
          System.out.println(evaluation2.toSummaryString("\nSecond buildClassifier()", true) + "\n\n");
        } 
        throw new Exception("Results differ between buildClassifier calls");
      } 
      System.out.println("yes");
      return true;
    } catch (Exception exception) {
      String str = exception.getMessage().toLowerCase();
      if (str.indexOf("worse than zeror") >= 0) {
        System.out.println("warning: performs worse than ZeroR");
      } else {
        System.out.println("no");
      } 
      if (this.m_Debug) {
        System.out.println("\n=== Full Report ===");
        System.out.print("Problem during");
        if (bool4) {
          System.out.print(" testing");
        } else {
          System.out.print(" training");
        } 
        switch (b4) {
          case 0:
            System.out.print(" of dataset 1");
            break;
          case 1:
            System.out.print(" of dataset 2");
            break;
          case 2:
            System.out.print(" of dataset 1 (2nd build)");
            break;
          case 3:
            System.out.print(", comparing results from builds of dataset 1");
            break;
        } 
        System.out.println(": " + exception.getMessage() + "\n");
        System.out.println("here are the datasets:\n");
        System.out.println("=== Train1 Dataset ===\n" + instances1.toString() + "\n");
        System.out.println("=== Test1 Dataset ===\n" + instances2.toString() + "\n\n");
        System.out.println("=== Train2 Dataset ===\n" + instances3.toString() + "\n");
        System.out.println("=== Test2 Dataset ===\n" + instances4.toString() + "\n\n");
      } 
      return false;
    } 
  }
  
  protected boolean canHandleMissing(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, int paramInt) {
    if (paramInt == 100)
      System.out.print("100% "); 
    System.out.print("missing");
    if (paramBoolean4) {
      System.out.print(" predictor");
      if (paramBoolean5)
        System.out.print(" and"); 
    } 
    if (paramBoolean5)
      System.out.print(" class"); 
    System.out.print(" values");
    printAttributeSummary(paramBoolean1, paramBoolean2, paramBoolean3);
    System.out.print("...");
    FastVector fastVector = new FastVector();
    fastVector.addElement("missing");
    fastVector.addElement("value");
    fastVector.addElement("train");
    byte b1 = 20;
    byte b2 = 20;
    byte b3 = 2;
    return runBasicTest(paramBoolean1, paramBoolean2, paramBoolean3, paramInt, paramBoolean4, paramBoolean5, b1, b2, b3, fastVector);
  }
  
  protected boolean updatingEquality(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    System.out.print("incremental training produces the same results as batch training");
    printAttributeSummary(paramBoolean1, paramBoolean2, paramBoolean3);
    System.out.print("...");
    byte b1 = 20;
    byte b2 = 20;
    byte b3 = 2;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    Instances instances1 = null;
    Instances instances2 = null;
    Classifier[] arrayOfClassifier = null;
    Evaluation evaluation1 = null;
    Evaluation evaluation2 = null;
    boolean bool4 = false;
    try {
      instances1 = makeTestDataset(42, b1, paramBoolean1 ? 2 : 0, paramBoolean2 ? 1 : 0, b3, paramBoolean3);
      instances2 = makeTestDataset(24, b2, paramBoolean1 ? 2 : 0, paramBoolean2 ? 1 : 0, b3, paramBoolean3);
      if (paramBoolean1) {
        instances1.deleteAttributeAt(0);
        instances2.deleteAttributeAt(0);
      } 
      if (bool1) {
        addMissing(instances1, bool1, bool2, bool3);
        addMissing(instances2, Math.min(bool1, 50), bool2, bool3);
      } 
      arrayOfClassifier = Classifier.makeCopies(getClassifier(), 2);
      evaluation1 = new Evaluation(instances1);
      evaluation2 = new Evaluation(instances1);
      arrayOfClassifier[0].buildClassifier(instances1);
      testWRTZeroR(arrayOfClassifier[0], evaluation1, instances1, instances2);
    } catch (Exception exception) {
      throw new Error("Error setting up for tests: " + exception.getMessage());
    } 
    try {
      arrayOfClassifier[1].buildClassifier(new Instances(instances1, 0));
      for (byte b = 0; b < instances1.numInstances(); b++)
        ((UpdateableClassifier)arrayOfClassifier[1]).updateClassifier(instances1.instance(b)); 
      bool4 = true;
      testWRTZeroR(arrayOfClassifier[1], evaluation2, instances1, instances2);
      if (!evaluation1.equals(evaluation2)) {
        System.out.println("no");
        if (this.m_Debug) {
          System.out.println("\n=== Full Report ===");
          System.out.println("Results differ between batch and incrementally built models.\nDepending on the classifier, this may be OK");
          System.out.println("Here are the results:\n");
          System.out.println(evaluation1.toSummaryString("\nbatch built results\n", true));
          System.out.println(evaluation2.toSummaryString("\nincrementally built results\n", true));
          System.out.println("Here are the datasets:\n");
          System.out.println("=== Train Dataset ===\n" + instances1.toString() + "\n");
          System.out.println("=== Test Dataset ===\n" + instances2.toString() + "\n\n");
        } 
        return false;
      } 
      System.out.println("yes");
      return true;
    } catch (Exception exception) {
      System.out.print("Problem during");
      if (bool4) {
        System.out.print(" testing");
      } else {
        System.out.print(" training");
      } 
      System.out.println(": " + exception.getMessage() + "\n");
      return false;
    } 
  }
  
  protected boolean doesntUseTestClassVal(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    System.out.print("classifier ignores test instance class vals");
    printAttributeSummary(paramBoolean1, paramBoolean2, paramBoolean3);
    System.out.print("...");
    byte b1 = 40;
    byte b2 = 20;
    byte b3 = 2;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    Instances instances1 = null;
    Instances instances2 = null;
    Classifier[] arrayOfClassifier = null;
    Evaluation evaluation1 = null;
    Evaluation evaluation2 = null;
    boolean bool4 = false;
    try {
      instances1 = makeTestDataset(43, b1, paramBoolean1 ? 3 : 0, paramBoolean2 ? 2 : 0, b3, paramBoolean3);
      instances2 = makeTestDataset(24, b2, paramBoolean1 ? 3 : 0, paramBoolean2 ? 2 : 0, b3, paramBoolean3);
      if (paramBoolean1) {
        instances1.deleteAttributeAt(0);
        instances2.deleteAttributeAt(0);
      } 
      if (bool1) {
        addMissing(instances1, bool1, bool2, bool3);
        addMissing(instances2, Math.min(bool1, 50), bool2, bool3);
      } 
      arrayOfClassifier = Classifier.makeCopies(getClassifier(), 2);
      evaluation1 = new Evaluation(instances1);
      evaluation2 = new Evaluation(instances1);
      arrayOfClassifier[0].buildClassifier(instances1);
      arrayOfClassifier[1].buildClassifier(instances1);
    } catch (Exception exception) {
      throw new Error("Error setting up for tests: " + exception.getMessage());
    } 
    try {
      for (byte b = 0; b < instances2.numInstances(); b++) {
        Instance instance1 = instances2.instance(b);
        Instance instance2 = (Instance)instance1.copy();
        instance2.setDataset(instances2);
        instance2.setClassMissing();
        double[] arrayOfDouble1 = arrayOfClassifier[0].distributionForInstance(instance1);
        double[] arrayOfDouble2 = arrayOfClassifier[1].distributionForInstance(instance2);
        for (byte b4 = 0; b4 < arrayOfDouble1.length; b4++) {
          if (arrayOfDouble1[b4] != arrayOfDouble2[b4])
            throw new Exception("Prediction different for instance " + (b + 1)); 
        } 
      } 
      System.out.println("yes");
      return true;
    } catch (Exception exception) {
      System.out.println("no");
      if (this.m_Debug) {
        System.out.println("\n=== Full Report ===");
        if (bool4) {
          System.out.println("Results differ between non-missing and missing test class values.");
        } else {
          System.out.print("Problem during testing");
          System.out.println(": " + exception.getMessage() + "\n");
        } 
        System.out.println("Here are the datasets:\n");
        System.out.println("=== Train Dataset ===\n" + instances1.toString() + "\n");
        System.out.println("=== Train Weights ===\n");
        for (byte b = 0; b < instances1.numInstances(); b++)
          System.out.println(" " + (b + 1) + "    " + instances1.instance(b).weight()); 
        System.out.println("=== Test Dataset ===\n" + instances2.toString() + "\n\n");
        System.out.println("(test weights all 1.0\n");
      } 
      return false;
    } 
  }
  
  protected boolean instanceWeights(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    System.out.print("classifier uses instance weights");
    printAttributeSummary(paramBoolean1, paramBoolean2, paramBoolean3);
    System.out.print("...");
    byte b1 = 40;
    byte b2 = 20;
    byte b3 = 2;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    Instances instances1 = null;
    Instances instances2 = null;
    Classifier[] arrayOfClassifier = null;
    Evaluation evaluation1 = null;
    Evaluation evaluation2 = null;
    boolean bool4 = false;
    boolean bool5 = false;
    try {
      instances1 = makeTestDataset(43, b1, paramBoolean1 ? 3 : 0, paramBoolean2 ? 2 : 0, b3, paramBoolean3);
      instances2 = makeTestDataset(24, b2, paramBoolean1 ? 3 : 0, paramBoolean2 ? 2 : 0, b3, paramBoolean3);
      if (paramBoolean1) {
        instances1.deleteAttributeAt(0);
        instances2.deleteAttributeAt(0);
      } 
      if (bool1) {
        addMissing(instances1, bool1, bool2, bool3);
        addMissing(instances2, Math.min(bool1, 50), bool2, bool3);
      } 
      arrayOfClassifier = Classifier.makeCopies(getClassifier(), 2);
      evaluation1 = new Evaluation(instances1);
      evaluation2 = new Evaluation(instances1);
      arrayOfClassifier[0].buildClassifier(instances1);
      testWRTZeroR(arrayOfClassifier[0], evaluation1, instances1, instances2);
    } catch (Exception exception) {
      throw new Error("Error setting up for tests: " + exception.getMessage());
    } 
    try {
      for (byte b4 = 0; b4 < instances1.numInstances(); b4++)
        instances1.instance(b4).setWeight(0.0D); 
      Random random = new Random(1L);
      for (byte b5 = 0; b5 < instances1.numInstances() / 2; b5++) {
        int i = Math.abs(random.nextInt()) % instances1.numInstances();
        int j = Math.abs(random.nextInt()) % 10 + 1;
        instances1.instance(i).setWeight(j);
      } 
      arrayOfClassifier[1].buildClassifier(instances1);
      bool4 = true;
      testWRTZeroR(arrayOfClassifier[1], evaluation2, instances1, instances2);
      if (evaluation1.equals(evaluation2)) {
        bool5 = true;
        throw new Exception("evalFail");
      } 
      System.out.println("yes");
      return true;
    } catch (Exception exception) {
      System.out.println("no");
      if (this.m_Debug) {
        System.out.println("\n=== Full Report ===");
        if (bool5) {
          System.out.println("Results don't differ between non-weighted and weighted instance models.");
          System.out.println("Here are the results:\n");
          System.out.println(evaluation1.toSummaryString("\nboth methods\n", true));
        } else {
          System.out.print("Problem during");
          if (bool4) {
            System.out.print(" testing");
          } else {
            System.out.print(" training");
          } 
          System.out.println(": " + exception.getMessage() + "\n");
        } 
        System.out.println("Here are the datasets:\n");
        System.out.println("=== Train Dataset ===\n" + instances1.toString() + "\n");
        System.out.println("=== Train Weights ===\n");
        for (byte b = 0; b < instances1.numInstances(); b++)
          System.out.println(" " + (b + 1) + "    " + instances1.instance(b).weight()); 
        System.out.println("=== Test Dataset ===\n" + instances2.toString() + "\n\n");
        System.out.println("(test weights all 1.0\n");
      } 
      return false;
    } 
  }
  
  protected boolean datasetIntegrity(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5) {
    System.out.print("classifier doesn't alter original datasets");
    printAttributeSummary(paramBoolean1, paramBoolean2, paramBoolean3);
    System.out.print("...");
    byte b1 = 20;
    byte b2 = 20;
    byte b3 = 2;
    byte b4 = 20;
    Instances instances1 = null;
    Instances instances2 = null;
    Classifier classifier = null;
    Evaluation evaluation = null;
    boolean bool = false;
    try {
      instances1 = makeTestDataset(42, b1, paramBoolean1 ? 2 : 0, paramBoolean2 ? 1 : 0, b3, paramBoolean3);
      instances2 = makeTestDataset(24, b2, paramBoolean1 ? 2 : 0, paramBoolean2 ? 1 : 0, b3, paramBoolean3);
      if (paramBoolean1) {
        instances1.deleteAttributeAt(0);
        instances2.deleteAttributeAt(0);
      } 
      if (b4 > 0) {
        addMissing(instances1, b4, paramBoolean4, paramBoolean5);
        addMissing(instances2, Math.min(b4, 50), paramBoolean4, paramBoolean5);
      } 
      classifier = Classifier.makeCopies(getClassifier(), 1)[0];
      evaluation = new Evaluation(instances1);
    } catch (Exception exception) {
      throw new Error("Error setting up for tests: " + exception.getMessage());
    } 
    try {
      Instances instances3 = new Instances(instances1);
      Instances instances4 = new Instances(instances2);
      classifier.buildClassifier(instances3);
      compareDatasets(instances1, instances3);
      bool = true;
      testWRTZeroR(classifier, evaluation, instances3, instances4);
      compareDatasets(instances2, instances4);
      System.out.println("yes");
      return true;
    } catch (Exception exception) {
      System.out.println("no");
      if (this.m_Debug) {
        System.out.println("\n=== Full Report ===");
        System.out.print("Problem during");
        if (bool) {
          System.out.print(" testing");
        } else {
          System.out.print(" training");
        } 
        System.out.println(": " + exception.getMessage() + "\n");
        System.out.println("Here are the datasets:\n");
        System.out.println("=== Train Dataset ===\n" + instances1.toString() + "\n");
        System.out.println("=== Test Dataset ===\n" + instances2.toString() + "\n\n");
      } 
      return false;
    } 
  }
  
  protected boolean runBasicTest(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt1, boolean paramBoolean4, boolean paramBoolean5, int paramInt2, int paramInt3, int paramInt4, FastVector paramFastVector) {
    Instances instances1 = null;
    Instances instances2 = null;
    Classifier classifier = null;
    Evaluation evaluation = null;
    boolean bool = false;
    try {
      instances1 = makeTestDataset(42, paramInt2, paramBoolean1 ? 2 : 0, paramBoolean2 ? 1 : 0, paramInt4, paramBoolean3);
      instances2 = makeTestDataset(24, paramInt3, paramBoolean1 ? 2 : 0, paramBoolean2 ? 1 : 0, paramInt4, paramBoolean3);
      if (paramBoolean1) {
        instances1.deleteAttributeAt(0);
        instances2.deleteAttributeAt(0);
      } 
      if (paramInt1 > 0) {
        addMissing(instances1, paramInt1, paramBoolean4, paramBoolean5);
        addMissing(instances2, Math.min(paramInt1, 50), paramBoolean4, paramBoolean5);
      } 
      classifier = Classifier.makeCopies(getClassifier(), 1)[0];
      evaluation = new Evaluation(instances1);
    } catch (Exception exception) {
      throw new Error("Error setting up for tests: " + exception.getMessage());
    } 
    try {
      classifier.buildClassifier(instances1);
      bool = true;
      if (!testWRTZeroR(classifier, evaluation, instances1, instances2))
        throw new Exception("Scheme performs worse than ZeroR"); 
      System.out.println("yes");
      return true;
    } catch (Exception exception) {
      boolean bool1 = false;
      String str = exception.getMessage().toLowerCase();
      if (str.indexOf("worse than zeror") >= 0) {
        System.out.println("warning: performs worse than ZeroR");
      } else {
        for (byte b = 0; b < paramFastVector.size(); b++) {
          if (str.indexOf((String)paramFastVector.elementAt(b)) >= 0)
            bool1 = true; 
        } 
        System.out.println("no" + (bool1 ? " (OK error message)" : ""));
      } 
      if (this.m_Debug) {
        System.out.println("\n=== Full Report ===");
        System.out.print("Problem during");
        if (bool) {
          System.out.print(" testing");
        } else {
          System.out.print(" training");
        } 
        System.out.println(": " + exception.getMessage() + "\n");
        if (!bool1) {
          if (paramFastVector.size() > 0) {
            System.out.print("Error message doesn't mention ");
            for (byte b = 0; b < paramFastVector.size(); b++) {
              if (b != 0)
                System.out.print(" or "); 
              System.out.print('"' + (String)paramFastVector.elementAt(b) + '"');
            } 
          } 
          System.out.println("here are the datasets:\n");
          System.out.println("=== Train Dataset ===\n" + instances1.toString() + "\n");
          System.out.println("=== Test Dataset ===\n" + instances2.toString() + "\n\n");
        } 
      } 
      return false;
    } 
  }
  
  protected boolean testWRTZeroR(Classifier paramClassifier, Evaluation paramEvaluation, Instances paramInstances1, Instances paramInstances2) throws Exception {
    paramEvaluation.evaluateModel(paramClassifier, paramInstances2);
    try {
      ZeroR zeroR = new ZeroR();
      zeroR.buildClassifier(paramInstances1);
      Evaluation evaluation = new Evaluation(paramInstances1);
      evaluation.evaluateModel((Classifier)zeroR, paramInstances2);
      return Utils.grOrEq(evaluation.errorRate(), paramEvaluation.errorRate());
    } catch (Exception exception) {
      throw new Error("Problem determining ZeroR performance: " + exception.getMessage());
    } 
  }
  
  protected void compareDatasets(Instances paramInstances1, Instances paramInstances2) throws Exception {
    if (!paramInstances2.equalHeaders(paramInstances1))
      throw new Exception("header has been modified"); 
    if (paramInstances2.numInstances() != paramInstances1.numInstances())
      throw new Exception("number of instances has changed"); 
    for (byte b = 0; b < paramInstances2.numInstances(); b++) {
      Instance instance1 = paramInstances1.instance(b);
      Instance instance2 = paramInstances2.instance(b);
      for (byte b1 = 0; b1 < instance1.numAttributes(); b1++) {
        if (instance1.isMissing(b1)) {
          if (!instance2.isMissing(b1))
            throw new Exception("instances have changed"); 
        } else if (instance1.value(b1) != instance2.value(b1)) {
          throw new Exception("instances have changed");
        } 
        if (instance1.weight() != instance2.weight())
          throw new Exception("instance weights have changed"); 
      } 
    } 
  }
  
  protected void addMissing(Instances paramInstances, int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    int i = paramInstances.classIndex();
    Random random = new Random(1L);
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = paramInstances.instance(b);
      for (byte b1 = 0; b1 < paramInstances.numAttributes(); b1++) {
        if (((b1 == i && paramBoolean2) || (b1 != i && paramBoolean1)) && Math.abs(random.nextInt()) % 100 < paramInt)
          instance.setMissing(b1); 
      } 
    } 
  }
  
  protected Instances makeTestDataset(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean) throws Exception {
    int i = paramInt3 + paramInt4 + 1;
    Random random = new Random(paramInt1);
    FastVector fastVector = new FastVector(i);
    byte b1;
    for (b1 = 0; b1 < paramInt3; b1++) {
      FastVector fastVector1 = new FastVector(b1 + 1);
      for (byte b = 0; b <= b1; b++)
        fastVector1.addElement("a" + (b1 + 1) + "l" + (b + 1)); 
      fastVector.addElement(new Attribute("Nominal" + (b1 + 1), fastVector1));
    } 
    for (b1 = 0; b1 < paramInt4; b1++)
      fastVector.addElement(new Attribute("Numeric" + (b1 + 1))); 
    if (paramBoolean) {
      fastVector.addElement(new Attribute("Class"));
    } else {
      FastVector fastVector1 = new FastVector();
      for (byte b = 0; b < paramInt5; b++)
        fastVector1.addElement("cl" + (b + 1)); 
      fastVector.addElement(new Attribute("Class", fastVector1));
    } 
    Instances instances = new Instances("CheckSet", fastVector, paramInt2);
    instances.setClassIndex(instances.numAttributes() - 1);
    for (byte b2 = 0; b2 < paramInt2; b2++) {
      Instance instance = new Instance(i);
      instance.setDataset(instances);
      if (paramBoolean) {
        instance.setClassValue(random.nextFloat() * 0.25D + (Math.abs(random.nextInt()) % Math.max(2, paramInt3)));
      } else {
        instance.setClassValue((Math.abs(random.nextInt()) % instances.numClasses()));
      } 
      double d1 = instance.classValue();
      double d2 = 0.0D;
      for (byte b = 0; b < i - 1; b++) {
        switch (instances.attribute(b).type()) {
          case 0:
            d2 = d1 * 4.0D + (random.nextFloat() * 1.0F) - 0.5D;
            instance.setValue(b, d2);
            break;
          case 1:
            if (random.nextFloat() < 0.2D) {
              d2 = (Math.abs(random.nextInt()) % instances.attribute(b).numValues());
            } else {
              d2 = ((int)d1 % instances.attribute(b).numValues());
            } 
            instance.setValue(b, d2);
            break;
          case 2:
            System.err.println("Huh? this bit isn't implemented yet");
            break;
        } 
      } 
      instances.add(instance);
    } 
    return instances;
  }
  
  protected void printAttributeSummary(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    if (paramBoolean3) {
      System.out.print(" (numeric class,");
    } else {
      System.out.print(" (nominal class,");
    } 
    if (paramBoolean2) {
      System.out.print(" numeric");
      if (paramBoolean1)
        System.out.print(" &"); 
    } 
    if (paramBoolean1)
      System.out.print(" nominal"); 
    System.out.print(" predictors)");
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\CheckClassifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */