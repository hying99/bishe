package weka.classifiers;

import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Random;
import weka.core.Instances;
import weka.core.Matrix;
import weka.core.Utils;

public class CostMatrix extends Matrix {
  public static String FILE_EXTENSION = ".cost";
  
  public CostMatrix(CostMatrix paramCostMatrix) {
    super(paramCostMatrix.size(), paramCostMatrix.size());
    for (byte b = 0; b < paramCostMatrix.size(); b++) {
      for (byte b1 = 0; b1 < paramCostMatrix.size(); b1++)
        setElement(b, b1, paramCostMatrix.getElement(b, b1)); 
    } 
  }
  
  public CostMatrix(int paramInt) {
    super(paramInt, paramInt);
  }
  
  public CostMatrix(Reader paramReader) throws Exception {
    super(paramReader);
    if (numRows() != numColumns())
      throw new Exception("Trying to create a non-square cost matrix"); 
  }
  
  public void initialize() {
    for (byte b = 0; b < size(); b++) {
      for (byte b1 = 0; b1 < size(); b1++)
        setElement(b, b1, (b == b1) ? 0.0D : 1.0D); 
    } 
  }
  
  public int size() {
    return numColumns();
  }
  
  public Instances applyCostMatrix(Instances paramInstances, Random paramRandom) throws Exception {
    double d1 = 0.0D;
    if (paramInstances.classIndex() < 0)
      throw new Exception("Class index is not set!"); 
    if (size() != paramInstances.numClasses())
      throw new Exception("Misclassification cost matrix has wrong format!"); 
    double[] arrayOfDouble2 = new double[paramInstances.numClasses()];
    double[] arrayOfDouble1 = new double[paramInstances.numClasses()];
    byte b1;
    for (b1 = 0; b1 < paramInstances.numInstances(); b1++)
      arrayOfDouble1[(int)paramInstances.instance(b1).classValue()] = arrayOfDouble1[(int)paramInstances.instance(b1).classValue()] + paramInstances.instance(b1).weight(); 
    double d2 = Utils.sum(arrayOfDouble1);
    for (b1 = 0; b1 < size(); b1++) {
      if (!Utils.eq(getElement(b1, b1), 0.0D)) {
        CostMatrix costMatrix = new CostMatrix(this);
        costMatrix.normalize();
        return costMatrix.applyCostMatrix(paramInstances, paramRandom);
      } 
    } 
    for (b1 = 0; b1 < paramInstances.numClasses(); b1++) {
      double d = 0.0D;
      for (byte b = 0; b < paramInstances.numClasses(); b++) {
        if (Utils.sm(getElement(b1, b), 0.0D))
          throw new Exception("Neg. weights in misclassification cost matrix!"); 
        d += getElement(b1, b);
      } 
      arrayOfDouble2[b1] = d * d2;
      d1 += d * arrayOfDouble1[b1];
    } 
    for (b1 = 0; b1 < paramInstances.numClasses(); b1++)
      arrayOfDouble2[b1] = arrayOfDouble2[b1] / d1; 
    double[] arrayOfDouble3 = new double[paramInstances.numInstances()];
    for (b1 = 0; b1 < paramInstances.numInstances(); b1++)
      arrayOfDouble3[b1] = paramInstances.instance(b1).weight() * arrayOfDouble2[(int)paramInstances.instance(b1).classValue()]; 
    if (paramRandom != null)
      return paramInstances.resampleWithWeights(paramRandom, arrayOfDouble3); 
    Instances instances = new Instances(paramInstances);
    for (byte b2 = 0; b2 < paramInstances.numInstances(); b2++)
      instances.instance(b2).setWeight(arrayOfDouble3[b2]); 
    return instances;
  }
  
  public double[] expectedCosts(double[] paramArrayOfdouble) throws Exception {
    if (paramArrayOfdouble.length != size())
      throw new Exception("Length of probability estimates don't match cost matrix"); 
    double[] arrayOfDouble = new double[size()];
    for (byte b = 0; b < size(); b++) {
      for (byte b1 = 0; b1 < size(); b1++)
        arrayOfDouble[b] = arrayOfDouble[b] + paramArrayOfdouble[b1] * getElement(b, b1); 
    } 
    return arrayOfDouble;
  }
  
  public double getMaxCost(int paramInt) {
    double d = Double.NEGATIVE_INFINITY;
    for (byte b = 0; b < size(); b++) {
      double d1 = getElement(paramInt, b);
      if (d1 > d)
        d = d1; 
    } 
    return d;
  }
  
  public void normalize() {
    for (byte b = 0; b < size(); b++) {
      double d = getElement(b, b);
      for (byte b1 = 0; b1 < size(); b1++)
        setElement(b1, b, getElement(b1, b) - d); 
    } 
  }
  
  public void readOldFormat(Reader paramReader) throws Exception {
    StreamTokenizer streamTokenizer = new StreamTokenizer(paramReader);
    initialize();
    streamTokenizer.commentChar(37);
    streamTokenizer.eolIsSignificant(true);
    int i;
    while (-1 != (i = streamTokenizer.nextToken())) {
      if (i == 10)
        continue; 
      if (i != -2)
        throw new Exception("Only numbers and comments allowed in cost file!"); 
      double d1 = streamTokenizer.nval;
      if (!Utils.eq((int)d1, d1))
        throw new Exception("First number in line has to be index of a class!"); 
      if ((int)d1 >= size())
        throw new Exception("Class index out of range!"); 
      if (-1 == (i = streamTokenizer.nextToken()))
        throw new Exception("Premature end of file!"); 
      if (i == 10)
        throw new Exception("Premature end of line!"); 
      if (i != -2)
        throw new Exception("Only numbers and comments allowed in cost file!"); 
      double d2 = streamTokenizer.nval;
      if (!Utils.eq((int)d2, d2))
        throw new Exception("Second number in line has to be index of a class!"); 
      if ((int)d2 >= size())
        throw new Exception("Class index out of range!"); 
      if ((int)d2 == (int)d1)
        throw new Exception("Diagonal of cost matrix non-zero!"); 
      if (-1 == (i = streamTokenizer.nextToken()))
        throw new Exception("Premature end of file!"); 
      if (i == 10)
        throw new Exception("Premature end of line!"); 
      if (i != -2)
        throw new Exception("Only numbers and comments allowed in cost file!"); 
      double d3 = streamTokenizer.nval;
      if (!Utils.gr(d3, 0.0D))
        throw new Exception("Only positive weights allowed!"); 
      setElement((int)d1, (int)d2, d3);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\CostMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */