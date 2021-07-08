package weka.classifiers.rules;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;

public class NNge extends Classifier implements UpdateableClassifier, OptionHandler {
  private Instances m_Train;
  
  private Exemplar m_Exemplars;
  
  private Exemplar[] m_ExemplarsByClass;
  
  double[] m_MinArray;
  
  double[] m_MaxArray;
  
  private int m_NumAttemptsOfGene = 5;
  
  private int m_NumFoldersMI = 5;
  
  private double[] m_MissingVector;
  
  private int[][][] m_MI_NumAttrClassInter;
  
  private int[][] m_MI_NumAttrInter;
  
  private double[] m_MI_MaxArray;
  
  private double[] m_MI_MinArray;
  
  private int[][][] m_MI_NumAttrClassValue;
  
  private int[][] m_MI_NumAttrValue;
  
  private int[] m_MI_NumClass;
  
  private int m_MI_NumInst;
  
  private double[] m_MI;
  
  public String globalInfo() {
    return "Nearest-neighbor-like algorithm using non-nested generalized exemplars (which are hyperrectangles that can be viewed as if-then rules). For more information, see \n\nBrent Martin, (1995) \"Instance-Based learning : Nearest Neighbor With Generalization\", Master Thesis, University of Waikato, Hamilton, New Zealand\n\nSylvain Roy (2002) \"Nearest Neighbor With Generalization\",Unpublished, University of Canterbury, Christchurch, New Zealand\n\n";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (!paramInstances.attribute(paramInstances.classIndex()).isNominal())
      throw new UnsupportedAttributeTypeException("Class type must be nominal!"); 
    paramInstances = new Instances(paramInstances);
    this.m_Train = new Instances(paramInstances, 0);
    this.m_Exemplars = null;
    this.m_ExemplarsByClass = new Exemplar[this.m_Train.numClasses()];
    byte b;
    for (b = 0; b < this.m_Train.numClasses(); b++)
      this.m_ExemplarsByClass[b] = null; 
    this.m_MaxArray = new double[this.m_Train.numAttributes()];
    this.m_MinArray = new double[this.m_Train.numAttributes()];
    for (b = 0; b < this.m_Train.numAttributes(); b++) {
      this.m_MinArray[b] = Double.POSITIVE_INFINITY;
      this.m_MaxArray[b] = Double.NEGATIVE_INFINITY;
    } 
    this.m_MI_MinArray = new double[paramInstances.numAttributes()];
    this.m_MI_MaxArray = new double[paramInstances.numAttributes()];
    this.m_MI_NumAttrClassInter = new int[paramInstances.numAttributes()][][];
    this.m_MI_NumAttrInter = new int[paramInstances.numAttributes()][];
    this.m_MI_NumAttrClassValue = new int[paramInstances.numAttributes()][][];
    this.m_MI_NumAttrValue = new int[paramInstances.numAttributes()][];
    this.m_MI_NumClass = new int[paramInstances.numClasses()];
    this.m_MI = new double[paramInstances.numAttributes()];
    this.m_MI_NumInst = 0;
    for (b = 0; b < paramInstances.numClasses(); b++)
      this.m_MI_NumClass[b] = 0; 
    for (b = 0; b < paramInstances.numAttributes(); b++) {
      if (b != paramInstances.classIndex()) {
        this.m_MI_MinArray[b] = Double.NaN;
        this.m_MI_MaxArray[b] = Double.NaN;
        this.m_MI[b] = Double.NaN;
        if (paramInstances.attribute(b).isNumeric()) {
          this.m_MI_NumAttrInter[b] = new int[this.m_NumFoldersMI];
          for (byte b2 = 0; b2 < this.m_NumFoldersMI; b2++)
            this.m_MI_NumAttrInter[b][b2] = 0; 
        } else {
          this.m_MI_NumAttrValue[b] = new int[paramInstances.attribute(b).numValues() + 1];
          for (byte b2 = 0; b2 < paramInstances.attribute(b).numValues() + 1; b2++)
            this.m_MI_NumAttrValue[b][b2] = 0; 
        } 
        this.m_MI_NumAttrClassInter[b] = new int[paramInstances.numClasses()][];
        this.m_MI_NumAttrClassValue[b] = new int[paramInstances.numClasses()][];
        for (byte b1 = 0; b1 < paramInstances.numClasses(); b1++) {
          if (paramInstances.attribute(b).isNumeric()) {
            this.m_MI_NumAttrClassInter[b][b1] = new int[this.m_NumFoldersMI];
            for (byte b2 = 0; b2 < this.m_NumFoldersMI; b2++)
              this.m_MI_NumAttrClassInter[b][b1][b2] = 0; 
          } else if (paramInstances.attribute(b).isNominal()) {
            this.m_MI_NumAttrClassValue[b][b1] = new int[paramInstances.attribute(b).numValues() + 1];
            for (byte b2 = 0; b2 < paramInstances.attribute(b).numValues() + 1; b2++)
              this.m_MI_NumAttrClassValue[b][b1][b2] = 0; 
          } 
        } 
      } 
    } 
    this.m_MissingVector = new double[paramInstances.numAttributes()];
    for (b = 0; b < paramInstances.numAttributes(); b++) {
      if (b == paramInstances.classIndex()) {
        this.m_MissingVector[b] = Double.NaN;
      } else {
        this.m_MissingVector[b] = paramInstances.attribute(b).numValues();
      } 
    } 
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements())
      update(enumeration.nextElement()); 
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    if (!this.m_Train.equalHeaders(paramInstance.dataset()))
      throw new Exception("NNge.classifyInstance : Incompatible instance types !"); 
    Exemplar exemplar = nearestExemplar(paramInstance);
    if (exemplar == null)
      throw new Exception("NNge.classifyInstance : NNge hasn't been trained !"); 
    return exemplar.classValue();
  }
  
  public void updateClassifier(Instance paramInstance) throws Exception {
    if (!this.m_Train.equalHeaders(paramInstance.dataset()))
      throw new Exception("Incompatible instance types"); 
    update(paramInstance);
  }
  
  private void update(Instance paramInstance) throws Exception {
    if (paramInstance.classIsMissing())
      return; 
    paramInstance.replaceMissingValues(this.m_MissingVector);
    this.m_Train.add(paramInstance);
    updateMinMax(paramInstance);
    updateMI(paramInstance);
    Exemplar exemplar = nearestExemplar(paramInstance);
    if (exemplar == null) {
      Exemplar exemplar1 = new Exemplar(this, this.m_Train, 10, paramInstance.classValue());
      exemplar1.generalise(paramInstance);
      initWeight(exemplar1);
      addExemplar(exemplar1);
      return;
    } 
    adjust(paramInstance, exemplar);
    generalise(paramInstance);
  }
  
  private Exemplar nearestExemplar(Instance paramInstance) {
    if (this.m_Exemplars == null)
      return null; 
    Exemplar exemplar1 = this.m_Exemplars;
    Exemplar exemplar2 = this.m_Exemplars;
    double d = exemplar1.squaredDistance(paramInstance);
    while (exemplar1.next != null) {
      exemplar1 = exemplar1.next;
      double d1 = exemplar1.squaredDistance(paramInstance);
      if (d1 < d) {
        d = d1;
        exemplar2 = exemplar1;
      } 
    } 
    return exemplar2;
  }
  
  private Exemplar nearestExemplar(Instance paramInstance, double paramDouble) {
    if (this.m_ExemplarsByClass[(int)paramDouble] == null)
      return null; 
    Exemplar exemplar1 = this.m_ExemplarsByClass[(int)paramDouble];
    Exemplar exemplar2 = this.m_ExemplarsByClass[(int)paramDouble];
    double d = exemplar1.squaredDistance(paramInstance);
    while (exemplar1.nextWithClass != null) {
      exemplar1 = exemplar1.nextWithClass;
      double d1 = exemplar1.squaredDistance(paramInstance);
      if (d1 < d) {
        d = d1;
        exemplar2 = exemplar1;
      } 
    } 
    return exemplar2;
  }
  
  private void generalise(Instance paramInstance) throws Exception {
    Exemplar exemplar1 = this.m_ExemplarsByClass[(int)paramInstance.classValue()];
    for (byte b = 0; b < this.m_NumAttemptsOfGene && exemplar1 != null; b++) {
      Exemplar exemplar3 = exemplar1;
      Exemplar exemplar4 = exemplar1;
      double d = exemplar1.squaredDistance(paramInstance);
      while (exemplar4.nextWithClass != null) {
        exemplar4 = exemplar4.nextWithClass;
        double d1 = exemplar4.squaredDistance(paramInstance);
        if (d1 < d) {
          d = d1;
          exemplar3 = exemplar4;
        } 
      } 
      if (exemplar3 == exemplar1)
        exemplar1 = exemplar1.nextWithClass; 
      removeExemplar(exemplar3);
      exemplar3.preGeneralise(paramInstance);
      if (!detectOverlapping(exemplar3)) {
        exemplar3.validateGeneralisation();
        addExemplar(exemplar3);
        return;
      } 
      exemplar3.cancelGeneralisation();
      addExemplar(exemplar3);
    } 
    Exemplar exemplar2 = new Exemplar(this, this.m_Train, 5, paramInstance.classValue());
    exemplar2.generalise(paramInstance);
    initWeight(exemplar2);
    addExemplar(exemplar2);
  }
  
  private void adjust(Instance paramInstance, Exemplar paramExemplar) throws Exception {
    if (paramInstance.classValue() == paramExemplar.classValue()) {
      paramExemplar.incrPositiveCount();
    } else {
      paramExemplar.incrNegativeCount();
      if (paramExemplar.holds(paramInstance))
        prune(paramExemplar, paramInstance); 
    } 
  }
  
  private void prune(Exemplar paramExemplar, Instance paramInstance) throws Exception {
    removeExemplar(paramExemplar);
    byte b1 = -1;
    byte b2 = -1;
    double d = Double.POSITIVE_INFINITY;
    byte b3 = -1;
    int i = -1;
    byte b4;
    for (b4 = 0; b4 < this.m_Train.numAttributes(); b4++) {
      if (b4 != this.m_Train.classIndex())
        if (this.m_Train.attribute(b4).isNumeric()) {
          double d1;
          double d2 = this.m_MaxArray[b4] - this.m_MinArray[b4];
          if (d2 != 0.0D) {
            d1 = Math.min(paramExemplar.getMaxBorder(b4) - paramInstance.value(b4), paramInstance.value(b4) - paramExemplar.getMinBorder(b4)) / d2;
          } else {
            d1 = Double.POSITIVE_INFINITY;
          } 
          byte b = 0;
          int j = b;
          Enumeration enumeration1 = paramExemplar.enumerateInstances();
          while (enumeration1.hasMoreElements()) {
            Instance instance = enumeration1.nextElement();
            if (instance.value(b4) < paramInstance.value(b4)) {
              j++;
              continue;
            } 
            if (instance.value(b4) > paramInstance.value(b4))
              b++; 
          } 
          j = Math.max(j, b);
          if (d1 < d) {
            d = d1;
            i = j;
            b1 = b4;
          } else if (d1 == d && j > i) {
            i = j;
            b1 = b4;
          } 
        } else {
          Enumeration enumeration1 = paramExemplar.enumerateInstances();
          byte b = 0;
          while (enumeration1.hasMoreElements()) {
            if (((Instance)enumeration1.nextElement()).value(b4) != paramInstance.value(b4))
              b++; 
          } 
          if (b > b3) {
            b3 = b;
            b2 = b4;
          } 
        }  
    } 
    if (b1 == -1 && b2 == -1) {
      b4 = 0;
    } else if (b1 == -1) {
      b4 = b2;
    } else if (b2 == -1) {
      b4 = b1;
    } else if (b3 > i) {
      b4 = b2;
    } else {
      b4 = b1;
    } 
    Exemplar exemplar1 = new Exemplar(this, this.m_Train, 10, paramExemplar.classValue());
    Exemplar exemplar2 = new Exemplar(this, this.m_Train, 10, paramExemplar.classValue());
    LinkedList linkedList = new LinkedList();
    Enumeration enumeration = paramExemplar.enumerateInstances();
    if (this.m_Train.attribute(b4).isNumeric()) {
      while (enumeration.hasMoreElements()) {
        Instance instance = enumeration.nextElement();
        if (instance.value(b4) > paramInstance.value(b4)) {
          exemplar1.generalise(instance);
          continue;
        } 
        if (instance.value(b4) < paramInstance.value(b4)) {
          exemplar2.generalise(instance);
          continue;
        } 
        if (notEqualFeatures(instance, paramInstance))
          linkedList.add(instance); 
      } 
    } else {
      while (enumeration.hasMoreElements()) {
        Instance instance = enumeration.nextElement();
        if (instance.value(b4) != paramInstance.value(b4)) {
          exemplar1.generalise(instance);
          continue;
        } 
        if (notEqualFeatures(instance, paramInstance))
          linkedList.add(instance); 
      } 
    } 
    while (linkedList.size() != 0) {
      Instance instance = linkedList.removeFirst();
      exemplar1.preGeneralise(instance);
      if (!exemplar1.holds(paramInstance)) {
        exemplar1.validateGeneralisation();
        continue;
      } 
      exemplar1.cancelGeneralisation();
      exemplar2.preGeneralise(instance);
      if (!exemplar2.holds(paramInstance)) {
        exemplar2.validateGeneralisation();
        continue;
      } 
      exemplar2.cancelGeneralisation();
      Exemplar exemplar = new Exemplar(this, this.m_Train, 3, instance.classValue());
      exemplar.generalise(instance);
      initWeight(exemplar);
      addExemplar(exemplar);
    } 
    if (exemplar1.numInstances() != 0) {
      initWeight(exemplar1);
      addExemplar(exemplar1);
    } 
    if (exemplar2.numInstances() != 0) {
      initWeight(exemplar2);
      addExemplar(exemplar2);
    } 
  }
  
  private boolean notEqualFeatures(Instance paramInstance1, Instance paramInstance2) {
    for (byte b = 0; b < this.m_Train.numAttributes(); b++) {
      if (b != this.m_Train.classIndex() && paramInstance1.value(b) != paramInstance2.value(b))
        return true; 
    } 
    return false;
  }
  
  private boolean detectOverlapping(Exemplar paramExemplar) {
    for (Exemplar exemplar = this.m_Exemplars; exemplar != null; exemplar = exemplar.next) {
      if (paramExemplar.overlaps(exemplar))
        return true; 
    } 
    return false;
  }
  
  private void updateMinMax(Instance paramInstance) {
    for (byte b = 0; b < this.m_Train.numAttributes(); b++) {
      if (this.m_Train.classIndex() != b && !this.m_Train.attribute(b).isNominal()) {
        if (paramInstance.value(b) < this.m_MinArray[b])
          this.m_MinArray[b] = paramInstance.value(b); 
        if (paramInstance.value(b) > this.m_MaxArray[b])
          this.m_MaxArray[b] = paramInstance.value(b); 
      } 
    } 
  }
  
  private void updateMI(Instance paramInstance) throws Exception {
    if (this.m_NumFoldersMI < 1)
      throw new Exception("NNge.updateMI : incorrect number of folders ! Option I must be greater than 1."); 
    this.m_MI_NumClass[(int)paramInstance.classValue()] = this.m_MI_NumClass[(int)paramInstance.classValue()] + 1;
    this.m_MI_NumInst++;
    for (byte b = 0; b < this.m_Train.numAttributes(); b++) {
      if (this.m_Train.classIndex() != b)
        if (this.m_Train.attribute(b).isNumeric()) {
          if (Double.isNaN(this.m_MI_MaxArray[b]) || Double.isNaN(this.m_MI_MinArray[b]) || this.m_MI_MaxArray[b] < paramInstance.value(b) || paramInstance.value(b) < this.m_MI_MinArray[b]) {
            if (Double.isNaN(this.m_MI_MaxArray[b]))
              this.m_MI_MaxArray[b] = paramInstance.value(b); 
            if (Double.isNaN(this.m_MI_MinArray[b]))
              this.m_MI_MinArray[b] = paramInstance.value(b); 
            if (this.m_MI_MaxArray[b] < paramInstance.value(b))
              this.m_MI_MaxArray[b] = paramInstance.value(b); 
            if (this.m_MI_MinArray[b] > paramInstance.value(b))
              this.m_MI_MinArray[b] = paramInstance.value(b); 
            double d = (this.m_MI_MaxArray[b] - this.m_MI_MinArray[b]) / this.m_NumFoldersMI;
            for (byte b2 = 0; b2 < this.m_NumFoldersMI; b2++) {
              this.m_MI_NumAttrInter[b][b2] = 0;
              for (byte b3 = 0; b3 < this.m_Train.numClasses(); b3++) {
                this.m_MI_NumAttrClassInter[b][b3][b2] = 0;
                Enumeration enumeration = this.m_Train.enumerateInstances();
                while (enumeration.hasMoreElements()) {
                  Instance instance = enumeration.nextElement();
                  if (this.m_MI_MinArray[b] + b2 * d <= instance.value(b) && instance.value(b) <= this.m_MI_MinArray[b] + (b2 + 1) * d && instance.classValue() == b3) {
                    this.m_MI_NumAttrInter[b][b2] = this.m_MI_NumAttrInter[b][b2] + 1;
                    this.m_MI_NumAttrClassInter[b][b3][b2] = this.m_MI_NumAttrClassInter[b][b3][b2] + 1;
                  } 
                } 
              } 
            } 
          } else {
            double d = (this.m_MI_MaxArray[b] - this.m_MI_MinArray[b]) / this.m_NumFoldersMI;
            for (byte b2 = 0; b2 < this.m_NumFoldersMI; b2++) {
              if (this.m_MI_MinArray[b] + b2 * d <= paramInstance.value(b) && paramInstance.value(b) <= this.m_MI_MinArray[b] + (b2 + 1) * d) {
                this.m_MI_NumAttrInter[b][b2] = this.m_MI_NumAttrInter[b][b2] + 1;
                this.m_MI_NumAttrClassInter[b][(int)paramInstance.classValue()][b2] = this.m_MI_NumAttrClassInter[b][(int)paramInstance.classValue()][b2] + 1;
              } 
            } 
          } 
          this.m_MI[b] = 0.0D;
          for (byte b1 = 0; b1 < this.m_NumFoldersMI; b1++) {
            for (byte b2 = 0; b2 < this.m_Train.numClasses(); b2++) {
              double d1 = this.m_MI_NumAttrClassInter[b][b2][b1] / this.m_MI_NumInst;
              double d2 = this.m_MI_NumClass[b2] / this.m_MI_NumInst;
              double d3 = this.m_MI_NumAttrInter[b][b1] / this.m_MI_NumInst;
              if (d1 != 0.0D)
                this.m_MI[b] = this.m_MI[b] + d1 * Utils.log2(d1 / d2 * d3); 
            } 
          } 
        } else if (this.m_Train.attribute(b).isNominal()) {
          this.m_MI_NumAttrValue[b][(int)paramInstance.value(b)] = this.m_MI_NumAttrValue[b][(int)paramInstance.value(b)] + 1;
          this.m_MI_NumAttrClassValue[b][(int)paramInstance.classValue()][(int)paramInstance.value(b)] = this.m_MI_NumAttrClassValue[b][(int)paramInstance.classValue()][(int)paramInstance.value(b)] + 1;
          this.m_MI[b] = 0.0D;
          for (byte b1 = 0; b1 < this.m_Train.attribute(b).numValues() + 1; b1++) {
            for (byte b2 = 0; b2 < this.m_Train.numClasses(); b2++) {
              double d1 = this.m_MI_NumAttrClassValue[b][b2][b1] / this.m_MI_NumInst;
              double d2 = this.m_MI_NumClass[b2] / this.m_MI_NumInst;
              double d3 = this.m_MI_NumAttrValue[b][b1] / this.m_MI_NumInst;
              if (d1 != 0.0D)
                this.m_MI[b] = this.m_MI[b] + d1 * Utils.log2(d1 / d2 * d3); 
            } 
          } 
        } else {
          throw new Exception("NNge.updateMI : Cannot deal with 'string attribute'.");
        }  
    } 
  }
  
  private void initWeight(Exemplar paramExemplar) {
    int i = 0;
    int j = 0;
    byte b = 0;
    Exemplar exemplar = this.m_Exemplars;
    if (exemplar == null) {
      paramExemplar.setPositiveCount(1);
      paramExemplar.setNegativeCount(0);
      return;
    } 
    while (exemplar != null) {
      i += exemplar.getPositiveCount();
      j += exemplar.getNegativeCount();
      b++;
      exemplar = exemplar.next;
    } 
    paramExemplar.setPositiveCount(i / b);
    paramExemplar.setNegativeCount(j / b);
  }
  
  private void addExemplar(Exemplar paramExemplar) {
    paramExemplar.next = this.m_Exemplars;
    if (this.m_Exemplars != null)
      this.m_Exemplars.previous = paramExemplar; 
    paramExemplar.previous = null;
    this.m_Exemplars = paramExemplar;
    paramExemplar.nextWithClass = this.m_ExemplarsByClass[(int)paramExemplar.classValue()];
    if (this.m_ExemplarsByClass[(int)paramExemplar.classValue()] != null)
      (this.m_ExemplarsByClass[(int)paramExemplar.classValue()]).previousWithClass = paramExemplar; 
    paramExemplar.previousWithClass = null;
    this.m_ExemplarsByClass[(int)paramExemplar.classValue()] = paramExemplar;
  }
  
  private void removeExemplar(Exemplar paramExemplar) {
    if (this.m_Exemplars == paramExemplar) {
      this.m_Exemplars = paramExemplar.next;
      if (this.m_Exemplars != null)
        this.m_Exemplars.previous = null; 
    } else {
      paramExemplar.previous.next = paramExemplar.next;
      if (paramExemplar.next != null)
        paramExemplar.next.previous = paramExemplar.previous; 
    } 
    paramExemplar.next = paramExemplar.previous = null;
    if (this.m_ExemplarsByClass[(int)paramExemplar.classValue()] == paramExemplar) {
      this.m_ExemplarsByClass[(int)paramExemplar.classValue()] = paramExemplar.nextWithClass;
      if (this.m_ExemplarsByClass[(int)paramExemplar.classValue()] != null)
        (this.m_ExemplarsByClass[(int)paramExemplar.classValue()]).previousWithClass = null; 
    } else {
      paramExemplar.previousWithClass.nextWithClass = paramExemplar.nextWithClass;
      if (paramExemplar.nextWithClass != null)
        paramExemplar.nextWithClass.previousWithClass = paramExemplar.previousWithClass; 
    } 
    paramExemplar.nextWithClass = paramExemplar.previousWithClass = null;
  }
  
  private double attrWeight(int paramInt) {
    return this.m_MI[paramInt];
  }
  
  public String toString() {
    Exemplar exemplar = this.m_Exemplars;
    if (this.m_MinArray == null)
      return "No classifier built"; 
    int[] arrayOfInt1 = new int[this.m_Train.numClasses()];
    int[] arrayOfInt2 = new int[this.m_Train.numClasses()];
    byte b1;
    for (b1 = 0; b1 < arrayOfInt1.length; b1++) {
      arrayOfInt1[b1] = 0;
      arrayOfInt2[b1] = 0;
    } 
    byte b2 = 0;
    byte b3 = 0;
    null = "\nNNGE classifier\n\nRules generated :\n";
    while (exemplar != null) {
      null = null + "\tclass " + this.m_Train.attribute(this.m_Train.classIndex()).value((int)exemplar.classValue()) + " IF : ";
      null = null + exemplar.toRules() + "\n";
      b2++;
      arrayOfInt1[(int)exemplar.classValue()] = arrayOfInt1[(int)exemplar.classValue()] + 1;
      if (exemplar.numInstances() == 1) {
        b3++;
        arrayOfInt2[(int)exemplar.classValue()] = arrayOfInt2[(int)exemplar.classValue()] + 1;
      } 
      exemplar = exemplar.next;
    } 
    null = null + "\nStat :\n";
    for (b1 = 0; b1 < arrayOfInt1.length; b1++)
      null = null + "\tclass " + this.m_Train.attribute(this.m_Train.classIndex()).value(b1) + " : " + Integer.toString(arrayOfInt1[b1]) + " exemplar(s) including " + Integer.toString(arrayOfInt1[b1] - arrayOfInt2[b1]) + " Hyperrectangle(s) and " + Integer.toString(arrayOfInt2[b1]) + " Single(s).\n"; 
    null = null + "\n\tTotal : " + Integer.toString(b2) + " exemplars(s) including " + Integer.toString(b2 - b3) + " Hyperrectangle(s) and " + Integer.toString(b3) + " Single(s).\n";
    null = null + "\n";
    null = null + "\tFeature weights : ";
    String str = "[";
    for (byte b4 = 0; b4 < this.m_Train.numAttributes(); b4++) {
      if (b4 != this.m_Train.classIndex()) {
        null = null + str + Double.toString(attrWeight(b4));
        str = " ";
      } 
    } 
    null = null + "]";
    return null + "\n\n";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tNumber of attempts of generalisation.\n", "G", 1, "-G <value>"));
    vector.addElement(new Option("\tNumber of folder for computing the mutual information.\n", "I", 1, "-I <value>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('G', paramArrayOfString);
    if (str.length() != 0) {
      this.m_NumAttemptsOfGene = Integer.parseInt(str);
      if (this.m_NumAttemptsOfGene < 1)
        throw new Exception("NNge.setOptions : G option's value must be greater than 1."); 
    } else {
      this.m_NumAttemptsOfGene = 5;
    } 
    str = Utils.getOption('I', paramArrayOfString);
    if (str.length() != 0) {
      this.m_NumFoldersMI = Integer.parseInt(str);
      if (this.m_NumFoldersMI < 1)
        throw new Exception("NNge.setOptions : I option's value must be greater than 1."); 
    } else {
      this.m_NumFoldersMI = 5;
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[5];
    byte b = 0;
    arrayOfString[b++] = "-G";
    arrayOfString[b++] = "" + this.m_NumAttemptsOfGene;
    arrayOfString[b++] = "-I";
    arrayOfString[b++] = "" + this.m_NumFoldersMI;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String numAttemptsOfGeneOptionTipText() {
    return "Sets the number of attempts for generalization.";
  }
  
  public int getNumAttemptsOfGeneOption() {
    return this.m_NumAttemptsOfGene;
  }
  
  public void setNumAttemptsOfGeneOption(int paramInt) {
    this.m_NumAttemptsOfGene = paramInt;
  }
  
  public String numFoldersMIOptionTipText() {
    return "Sets the number of folder for mutual information.";
  }
  
  public int getNumFoldersMIOption() {
    return this.m_NumFoldersMI;
  }
  
  public void setNumFoldersMIOption(int paramInt) {
    this.m_NumFoldersMI = paramInt;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new NNge(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
  
  private class Exemplar extends Instances {
    private Exemplar previous;
    
    private Exemplar next;
    
    private Exemplar previousWithClass;
    
    private Exemplar nextWithClass;
    
    private NNge m_NNge;
    
    private double m_ClassValue;
    
    private int m_PositiveCount;
    
    private int m_NegativeCount;
    
    private double[] m_MaxBorder;
    
    private double[] m_MinBorder;
    
    private boolean[][] m_Range;
    
    private double[] m_PreMaxBorder;
    
    private double[] m_PreMinBorder;
    
    private boolean[][] m_PreRange;
    
    private Instance m_PreInst;
    
    private final NNge this$0;
    
    private Exemplar(NNge this$0, NNge param1NNge1, Instances param1Instances, int param1Int, double param1Double) {
      super(param1Instances, param1Int);
      NNge.this = NNge.this;
      this.previous = null;
      this.next = null;
      this.previousWithClass = null;
      this.nextWithClass = null;
      this.m_PositiveCount = 1;
      this.m_NegativeCount = 0;
      this.m_PreMaxBorder = null;
      this.m_PreMinBorder = null;
      this.m_PreRange = (boolean[][])null;
      this.m_PreInst = null;
      this.m_NNge = param1NNge1;
      this.m_ClassValue = param1Double;
      this.m_MinBorder = new double[numAttributes()];
      this.m_MaxBorder = new double[numAttributes()];
      this.m_Range = new boolean[numAttributes()][];
      for (byte b = 0; b < numAttributes(); b++) {
        if (attribute(b).isNumeric()) {
          this.m_MinBorder[b] = Double.POSITIVE_INFINITY;
          this.m_MaxBorder[b] = Double.NEGATIVE_INFINITY;
          this.m_Range[b] = null;
        } else {
          this.m_MinBorder[b] = Double.NaN;
          this.m_MaxBorder[b] = Double.NaN;
          this.m_Range[b] = new boolean[attribute(b).numValues() + 1];
          for (byte b1 = 0; b1 < attribute(b).numValues() + 1; b1++)
            this.m_Range[b][b1] = false; 
        } 
      } 
    }
    
    private void generalise(Instance param1Instance) throws Exception {
      if (this.m_ClassValue != param1Instance.classValue())
        throw new Exception("Exemplar.generalise : Incompatible instance's class."); 
      add(param1Instance);
      for (byte b = 0; b < numAttributes(); b++) {
        if (param1Instance.isMissing(b))
          throw new Exception("Exemplar.generalise : Generalisation with missing feature impossible."); 
        if (b != classIndex())
          if (attribute(b).isNumeric()) {
            if (this.m_MaxBorder[b] < param1Instance.value(b))
              this.m_MaxBorder[b] = param1Instance.value(b); 
            if (param1Instance.value(b) < this.m_MinBorder[b])
              this.m_MinBorder[b] = param1Instance.value(b); 
          } else {
            this.m_Range[b][(int)param1Instance.value(b)] = true;
          }  
      } 
    }
    
    private void preGeneralise(Instance param1Instance) throws Exception {
      if (this.m_ClassValue != param1Instance.classValue())
        throw new Exception("Exemplar.preGeneralise : Incompatible instance's class."); 
      this.m_PreInst = param1Instance;
      this.m_PreRange = new boolean[numAttributes()][];
      this.m_PreMinBorder = new double[numAttributes()];
      this.m_PreMaxBorder = new double[numAttributes()];
      byte b;
      for (b = 0; b < numAttributes(); b++) {
        if (attribute(b).isNumeric()) {
          this.m_PreMinBorder[b] = this.m_MinBorder[b];
          this.m_PreMaxBorder[b] = this.m_MaxBorder[b];
        } else {
          this.m_PreRange[b] = new boolean[attribute(b).numValues() + 1];
          for (byte b1 = 0; b1 < attribute(b).numValues() + 1; b1++)
            this.m_PreRange[b][b1] = this.m_Range[b][b1]; 
        } 
      } 
      for (b = 0; b < numAttributes(); b++) {
        if (param1Instance.isMissing(b))
          throw new Exception("Exemplar.preGeneralise : Generalisation with missing feature impossible."); 
        if (b != classIndex())
          if (attribute(b).isNumeric()) {
            if (this.m_MaxBorder[b] < param1Instance.value(b))
              this.m_MaxBorder[b] = param1Instance.value(b); 
            if (param1Instance.value(b) < this.m_MinBorder[b])
              this.m_MinBorder[b] = param1Instance.value(b); 
          } else {
            this.m_Range[b][(int)param1Instance.value(b)] = true;
          }  
      } 
    }
    
    private void validateGeneralisation() throws Exception {
      if (this.m_PreInst == null)
        throw new Exception("Exemplar.validateGeneralisation : validateGeneralisation called without previous call to preGeneralise!"); 
      add(this.m_PreInst);
      this.m_PreRange = (boolean[][])null;
      this.m_PreMinBorder = null;
      this.m_PreMaxBorder = null;
    }
    
    private void cancelGeneralisation() throws Exception {
      if (this.m_PreInst == null)
        throw new Exception("Exemplar.cancelGeneralisation : cancelGeneralisation called without previous call to preGeneralise!"); 
      this.m_PreInst = null;
      this.m_Range = this.m_PreRange;
      this.m_MinBorder = this.m_PreMinBorder;
      this.m_MaxBorder = this.m_PreMaxBorder;
      this.m_PreRange = (boolean[][])null;
      this.m_PreMinBorder = null;
      this.m_PreMaxBorder = null;
    }
    
    private boolean holds(Instance param1Instance) {
      if (numInstances() == 0)
        return false; 
      for (byte b = 0; b < numAttributes(); b++) {
        if (b != classIndex() && !holds(b, param1Instance.value(b)))
          return false; 
      } 
      return true;
    }
    
    private boolean holds(int param1Int, double param1Double) {
      return (numAttributes() == 0) ? false : (attribute(param1Int).isNumeric() ? ((this.m_MinBorder[param1Int] <= param1Double && param1Double <= this.m_MaxBorder[param1Int])) : this.m_Range[param1Int][(int)param1Double]);
    }
    
    private boolean overlaps(Exemplar param1Exemplar) {
      if (param1Exemplar.isEmpty() || isEmpty())
        return false; 
      for (byte b = 0; b < numAttributes(); b++) {
        if (b != classIndex()) {
          if (attribute(b).isNumeric() && (param1Exemplar.m_MaxBorder[b] < this.m_MinBorder[b] || param1Exemplar.m_MinBorder[b] > this.m_MaxBorder[b]))
            return false; 
          if (attribute(b).isNominal()) {
            boolean bool = false;
            for (byte b1 = 0; b1 < attribute(b).numValues() + 1; b1++) {
              if (this.m_Range[b][b1] && param1Exemplar.m_Range[b][b1]) {
                bool = true;
                break;
              } 
            } 
            if (!bool)
              return false; 
          } 
        } 
      } 
      return true;
    }
    
    private double attrDistance(Instance param1Instance, int param1Int) {
      if (param1Instance.isMissing(param1Int))
        return 0.0D; 
      if (attribute(param1Int).isNumeric()) {
        double d = this.m_NNge.m_MaxArray[param1Int] - this.m_NNge.m_MinArray[param1Int];
        if (d <= 0.0D)
          d = 1.0D; 
        return (this.m_MaxBorder[param1Int] < param1Instance.value(param1Int)) ? ((param1Instance.value(param1Int) - this.m_MaxBorder[param1Int]) / d) : ((param1Instance.value(param1Int) < this.m_MinBorder[param1Int]) ? ((this.m_MinBorder[param1Int] - param1Instance.value(param1Int)) / d) : 0.0D);
      } 
      return holds(param1Int, param1Instance.value(param1Int)) ? 0.0D : 1.0D;
    }
    
    private double squaredDistance(Instance param1Instance) {
      double d = 0.0D;
      byte b1 = 0;
      for (byte b2 = 0; b2 < param1Instance.numAttributes(); b2++) {
        if (b2 != classIndex()) {
          double d1 = this.m_NNge.attrWeight(b2) * attrDistance(param1Instance, b2);
          d1 *= d1;
          d += d1;
          if (!param1Instance.isMissing(b2))
            b1++; 
        } 
      } 
      return (b1 == 0) ? 0.0D : (d / (b1 * b1));
    }
    
    private double weight() {
      return (this.m_PositiveCount + this.m_NegativeCount) / this.m_PositiveCount;
    }
    
    private double classValue() {
      return this.m_ClassValue;
    }
    
    private double getMinBorder(int param1Int) throws Exception {
      if (!attribute(param1Int).isNumeric())
        throw new Exception("Exception.getMinBorder : not numeric attribute !"); 
      if (numInstances() == 0)
        throw new Exception("Exception.getMinBorder : empty Exemplar !"); 
      return this.m_MinBorder[param1Int];
    }
    
    private double getMaxBorder(int param1Int) throws Exception {
      if (!attribute(param1Int).isNumeric())
        throw new Exception("Exception.getMaxBorder : not numeric attribute !"); 
      if (numInstances() == 0)
        throw new Exception("Exception.getMaxBorder : empty Exemplar !"); 
      return this.m_MaxBorder[param1Int];
    }
    
    private int getPositiveCount() {
      return this.m_PositiveCount;
    }
    
    private int getNegativeCount() {
      return this.m_NegativeCount;
    }
    
    private void setPositiveCount(int param1Int) {
      this.m_PositiveCount = param1Int;
    }
    
    private void setNegativeCount(int param1Int) {
      this.m_NegativeCount = param1Int;
    }
    
    private void incrPositiveCount() {
      this.m_PositiveCount++;
    }
    
    private void incrNegativeCount() {
      this.m_NegativeCount++;
    }
    
    private boolean isEmpty() {
      return (numInstances() == 0);
    }
    
    private String toString2() {
      Enumeration enumeration = null;
      null = "Exemplar[";
      if (numInstances() == 0)
        return null + "Empty]"; 
      null = null + "{";
      enumeration = enumerateInstances();
      while (enumeration.hasMoreElements())
        null = null + "<" + enumeration.nextElement().toString() + "> "; 
      null = null.substring(0, null.length() - 1);
      return null + "} {" + toRules() + "} p=" + this.m_PositiveCount + " n=" + this.m_NegativeCount + "]";
    }
    
    private String toRules() {
      if (numInstances() == 0)
        return "No Rules (Empty Exemplar)"; 
      null = "";
      String str = "";
      for (byte b = 0; b < numAttributes(); b++) {
        if (b != classIndex())
          if (attribute(b).isNumeric()) {
            if (this.m_MaxBorder[b] != this.m_MinBorder[b]) {
              null = null + str + this.m_MinBorder[b] + "<=" + attribute(b).name() + "<=" + this.m_MaxBorder[b];
            } else {
              null = null + str + attribute(b).name() + "=" + this.m_MaxBorder[b];
            } 
            str = " ^ ";
          } else {
            null = null + str + attribute(b).name() + " in {";
            String str1 = "";
            for (byte b1 = 0; b1 < attribute(b).numValues() + 1; b1++) {
              if (this.m_Range[b][b1]) {
                null = null + str1;
                if (b1 == attribute(b).numValues()) {
                  null = null + "?";
                } else {
                  null = null + attribute(b).value(b1);
                } 
                str1 = ",";
              } 
            } 
            null = null + "}";
            str = " ^ ";
          }  
      } 
      return null + "  (" + numInstances() + ")";
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\NNge.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */