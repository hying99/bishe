package weka.core;

public class ContingencyTables {
  private static double log2 = Math.log(2.0D);
  
  public static double chiSquared(double[][] paramArrayOfdouble, boolean paramBoolean) {
    int i = (paramArrayOfdouble.length - 1) * ((paramArrayOfdouble[0]).length - 1);
    return Statistics.chiSquaredProbability(chiVal(paramArrayOfdouble, paramBoolean), i);
  }
  
  public static double chiVal(double[][] paramArrayOfdouble, boolean paramBoolean) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    boolean bool = true;
    int j = paramArrayOfdouble.length;
    int k = (paramArrayOfdouble[0]).length;
    double[] arrayOfDouble1 = new double[j];
    double[] arrayOfDouble2 = new double[k];
    byte b;
    for (b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < k; b1++) {
        arrayOfDouble1[b] = arrayOfDouble1[b] + paramArrayOfdouble[b][b1];
        arrayOfDouble2[b1] = arrayOfDouble2[b1] + paramArrayOfdouble[b][b1];
        d3 += paramArrayOfdouble[b][b1];
      } 
    } 
    int i = (j - 1) * (k - 1);
    if (i > 1 || !paramBoolean) {
      bool = false;
    } else if (i <= 0) {
      return 0.0D;
    } 
    d2 = 0.0D;
    for (b = 0; b < j; b++) {
      if (Utils.gr(arrayOfDouble1[b], 0.0D))
        for (byte b1 = 0; b1 < k; b1++) {
          if (Utils.gr(arrayOfDouble2[b1], 0.0D)) {
            d1 = arrayOfDouble2[b1] * arrayOfDouble1[b] / d3;
            d2 += chiCell(paramArrayOfdouble[b][b1], d1, bool);
          } 
        }  
    } 
    return d2;
  }
  
  public static boolean cochransCriterion(double[][] paramArrayOfdouble) {
    double d1 = 0.0D;
    double d2 = 5.0D;
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    int i = paramArrayOfdouble.length;
    int j = (paramArrayOfdouble[0]).length;
    double[] arrayOfDouble1 = new double[i];
    double[] arrayOfDouble2 = new double[j];
    byte b4;
    for (b4 = 0; b4 < i; b4++) {
      for (byte b = 0; b < j; b++) {
        arrayOfDouble1[b4] = arrayOfDouble1[b4] + paramArrayOfdouble[b4][b];
        arrayOfDouble2[b] = arrayOfDouble2[b] + paramArrayOfdouble[b4][b];
        d1 += paramArrayOfdouble[b4][b];
      } 
    } 
    for (b4 = 0; b4 < i; b4++) {
      if (Utils.gr(arrayOfDouble1[b4], 0.0D))
        b2++; 
    } 
    byte b5;
    for (b5 = 0; b5 < j; b5++) {
      if (Utils.gr(arrayOfDouble2[b5], 0.0D))
        b3++; 
    } 
    for (b4 = 0; b4 < i; b4++) {
      if (Utils.gr(arrayOfDouble1[b4], 0.0D))
        for (b5 = 0; b5 < j; b5++) {
          if (Utils.gr(arrayOfDouble2[b5], 0.0D)) {
            double d = arrayOfDouble2[b5] * arrayOfDouble1[b4] / d1;
            if (Utils.sm(d, d2)) {
              if (Utils.sm(d, 1.0D))
                return false; 
              if (++b1 > (b2 * b3) / d2)
                return false; 
            } 
          } 
        }  
    } 
    return true;
  }
  
  public static double CramersV(double[][] paramArrayOfdouble) {
    double d = 0.0D;
    int i = paramArrayOfdouble.length;
    int j = (paramArrayOfdouble[0]).length;
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < j; b1++)
        d += paramArrayOfdouble[b][b1]; 
    } 
    int k = (i < j) ? (i - 1) : (j - 1);
    return (k == 0 || Utils.eq(d, 0.0D)) ? 0.0D : Math.sqrt(chiVal(paramArrayOfdouble, false) / d * k);
  }
  
  public static double entropy(double[] paramArrayOfdouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      d1 -= lnFunc(paramArrayOfdouble[b]);
      d2 += paramArrayOfdouble[b];
    } 
    return Utils.eq(d2, 0.0D) ? 0.0D : ((d1 + lnFunc(d2)) / d2 * log2);
  }
  
  public static double entropyConditionedOnColumns(double[][] paramArrayOfdouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < (paramArrayOfdouble[0]).length; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < paramArrayOfdouble.length; b1++) {
        d1 += lnFunc(paramArrayOfdouble[b1][b]);
        d += paramArrayOfdouble[b1][b];
      } 
      d1 -= lnFunc(d);
      d2 += d;
    } 
    return Utils.eq(d2, 0.0D) ? 0.0D : (-d1 / d2 * log2);
  }
  
  public static double entropyConditionedOnRows(double[][] paramArrayOfdouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < (paramArrayOfdouble[0]).length; b1++) {
        d1 += lnFunc(paramArrayOfdouble[b][b1]);
        d += paramArrayOfdouble[b][b1];
      } 
      d1 -= lnFunc(d);
      d2 += d;
    } 
    return Utils.eq(d2, 0.0D) ? 0.0D : (-d1 / d2 * log2);
  }
  
  public static double entropyConditionedOnRows(double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2, double paramDouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble2.length; b++) {
      double d3 = 0.0D;
      double d4 = 0.0D;
      for (byte b1 = 0; b1 < (paramArrayOfdouble2[0]).length; b1++) {
        d1 -= paramArrayOfdouble2[b][b1] * Math.log(paramArrayOfdouble1[b][b1] + 1.0D);
        d3 += paramArrayOfdouble1[b][b1];
        d4 += paramArrayOfdouble2[b][b1];
      } 
      d2 = d4;
      d1 += d4 * Math.log(d3 + paramDouble);
    } 
    return d1 / d2 * log2;
  }
  
  public static double entropyOverRows(double[][] paramArrayOfdouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < (paramArrayOfdouble[0]).length; b1++)
        d += paramArrayOfdouble[b][b1]; 
      d1 -= lnFunc(d);
      d2 += d;
    } 
    return Utils.eq(d2, 0.0D) ? 0.0D : ((d1 + lnFunc(d2)) / d2 * log2);
  }
  
  public static double entropyOverColumns(double[][] paramArrayOfdouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < (paramArrayOfdouble[0]).length; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < paramArrayOfdouble.length; b1++)
        d += paramArrayOfdouble[b1][b]; 
      d1 -= lnFunc(d);
      d2 += d;
    } 
    return Utils.eq(d2, 0.0D) ? 0.0D : ((d1 + lnFunc(d2)) / d2 * log2);
  }
  
  public static double gainRatio(double[][] paramArrayOfdouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    byte b;
    for (b = 0; b < (paramArrayOfdouble[0]).length; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < paramArrayOfdouble.length; b1++)
        d += paramArrayOfdouble[b1][b]; 
      d1 += lnFunc(d);
      d4 += d;
    } 
    d1 -= lnFunc(d4);
    for (b = 0; b < paramArrayOfdouble.length; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < (paramArrayOfdouble[0]).length; b1++) {
        d2 += lnFunc(paramArrayOfdouble[b][b1]);
        d += paramArrayOfdouble[b][b1];
      } 
      d3 += lnFunc(d);
    } 
    d2 -= d3;
    d3 -= lnFunc(d4);
    double d5 = d1 - d2;
    return Utils.eq(d3, 0.0D) ? 0.0D : (d5 / d3);
  }
  
  public static double log2MultipleHypergeometric(double[][] paramArrayOfdouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    byte b;
    for (b = 0; b < paramArrayOfdouble.length; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < (paramArrayOfdouble[b]).length; b1++)
        d += paramArrayOfdouble[b][b1]; 
      d1 += SpecialFunctions.lnFactorial(d);
      d2 += d;
    } 
    for (b = 0; b < (paramArrayOfdouble[0]).length; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < paramArrayOfdouble.length; b1++)
        d += paramArrayOfdouble[b1][b]; 
      d1 += SpecialFunctions.lnFactorial(d);
    } 
    for (b = 0; b < paramArrayOfdouble.length; b++) {
      for (byte b1 = 0; b1 < (paramArrayOfdouble[b]).length; b1++)
        d1 -= SpecialFunctions.lnFactorial(paramArrayOfdouble[b][b1]); 
    } 
    d1 -= SpecialFunctions.lnFactorial(d2);
    return -d1 / log2;
  }
  
  public static double[][] reduceMatrix(double[][] paramArrayOfdouble) {
    byte b4 = 0;
    byte b5 = 0;
    int i = paramArrayOfdouble.length;
    int j = (paramArrayOfdouble[0]).length;
    double[] arrayOfDouble1 = new double[i];
    double[] arrayOfDouble2 = new double[j];
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      for (byte b = 0; b < j; b++) {
        arrayOfDouble1[b1] = arrayOfDouble1[b1] + paramArrayOfdouble[b1][b];
        arrayOfDouble2[b] = arrayOfDouble2[b] + paramArrayOfdouble[b1][b];
      } 
    } 
    for (b1 = 0; b1 < i; b1++) {
      if (Utils.gr(arrayOfDouble1[b1], 0.0D))
        b4++; 
    } 
    byte b2;
    for (b2 = 0; b2 < j; b2++) {
      if (Utils.gr(arrayOfDouble2[b2], 0.0D))
        b5++; 
    } 
    double[][] arrayOfDouble = new double[b4][b5];
    byte b3 = 0;
    for (b1 = 0; b1 < i; b1++) {
      if (Utils.gr(arrayOfDouble1[b1], 0.0D)) {
        byte b = 0;
        for (b2 = 0; b2 < j; b2++) {
          if (Utils.gr(arrayOfDouble2[b2], 0.0D)) {
            arrayOfDouble[b3][b] = paramArrayOfdouble[b1][b2];
            b++;
          } 
        } 
        b3++;
      } 
    } 
    return arrayOfDouble;
  }
  
  public static double symmetricalUncertainty(double[][] paramArrayOfdouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    double d5 = 0.0D;
    byte b;
    for (b = 0; b < (paramArrayOfdouble[0]).length; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < paramArrayOfdouble.length; b1++)
        d += paramArrayOfdouble[b1][b]; 
      d2 += lnFunc(d);
      d1 += d;
    } 
    d2 -= lnFunc(d1);
    for (b = 0; b < paramArrayOfdouble.length; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < (paramArrayOfdouble[0]).length; b1++) {
        d += paramArrayOfdouble[b][b1];
        d4 += lnFunc(paramArrayOfdouble[b][b1]);
      } 
      d3 += lnFunc(d);
    } 
    d4 -= d3;
    d3 -= lnFunc(d1);
    d5 = d2 - d4;
    return (Utils.eq(d2, 0.0D) || Utils.eq(d3, 0.0D)) ? 0.0D : (2.0D * d5 / (d2 + d3));
  }
  
  public static double tauVal(double[][] paramArrayOfdouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    int i = paramArrayOfdouble.length;
    int j = (paramArrayOfdouble[0]).length;
    double[] arrayOfDouble = new double[j];
    for (byte b = 0; b < i; b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < j; b1++) {
        if (Utils.gr(paramArrayOfdouble[b][b1], d))
          d = paramArrayOfdouble[b][b1]; 
        arrayOfDouble[b1] = arrayOfDouble[b1] + paramArrayOfdouble[b][b1];
        d3 += paramArrayOfdouble[b][b1];
      } 
      d2 += d;
    } 
    if (Utils.eq(d3, 0.0D))
      return 0.0D; 
    d1 = arrayOfDouble[Utils.maxIndex(arrayOfDouble)];
    return (d2 - d1) / (d3 - d1);
  }
  
  private static double lnFunc(double paramDouble) {
    return (paramDouble < 1.0E-6D) ? 0.0D : (paramDouble * Math.log(paramDouble));
  }
  
  private static double chiCell(double paramDouble1, double paramDouble2, boolean paramBoolean) {
    if (Utils.smOrEq(paramDouble2, 0.0D))
      return 0.0D; 
    double d = Math.abs(paramDouble1 - paramDouble2);
    if (paramBoolean) {
      d -= 0.5D;
      if (d < 0.0D)
        d = 0.0D; 
    } 
    return d * d / paramDouble2;
  }
  
  public static void main(String[] paramArrayOfString) {
    double[] arrayOfDouble1 = { 10.0D, 5.0D, 20.0D };
    double[] arrayOfDouble2 = { 2.0D, 10.0D, 6.0D };
    double[] arrayOfDouble3 = { 5.0D, 10.0D, 10.0D };
    double[][] arrayOfDouble4 = new double[3][0];
    arrayOfDouble4[0] = arrayOfDouble1;
    arrayOfDouble4[1] = arrayOfDouble2;
    arrayOfDouble4[2] = arrayOfDouble3;
    for (byte b1 = 0; b1 < arrayOfDouble4.length; b1++) {
      for (byte b = 0; b < (arrayOfDouble4[b1]).length; b++)
        System.out.print(arrayOfDouble4[b1][b] + " "); 
      System.out.println();
    } 
    System.out.println("Chi-squared probability: " + chiSquared(arrayOfDouble4, false));
    System.out.println("Chi-squared value: " + chiVal(arrayOfDouble4, false));
    System.out.println("Cochran's criterion fullfilled: " + cochransCriterion(arrayOfDouble4));
    System.out.println("Cramer's V: " + CramersV(arrayOfDouble4));
    System.out.println("Entropy of first row: " + entropy(arrayOfDouble1));
    System.out.println("Entropy conditioned on columns: " + entropyConditionedOnColumns(arrayOfDouble4));
    System.out.println("Entropy conditioned on rows: " + entropyConditionedOnRows(arrayOfDouble4));
    System.out.println("Entropy conditioned on rows (with Laplace): " + entropyConditionedOnRows(arrayOfDouble4, arrayOfDouble4, 3.0D));
    System.out.println("Entropy of rows: " + entropyOverRows(arrayOfDouble4));
    System.out.println("Entropy of columns: " + entropyOverColumns(arrayOfDouble4));
    System.out.println("Gain ratio: " + gainRatio(arrayOfDouble4));
    System.out.println("Negative log2 of multiple hypergeometric probability: " + log2MultipleHypergeometric(arrayOfDouble4));
    System.out.println("Symmetrical uncertainty: " + symmetricalUncertainty(arrayOfDouble4));
    System.out.println("Tau value: " + tauVal(arrayOfDouble4));
    double[][] arrayOfDouble5 = new double[3][3];
    arrayOfDouble5[0][0] = 1.0D;
    arrayOfDouble5[0][1] = 0.0D;
    arrayOfDouble5[0][2] = 1.0D;
    arrayOfDouble5[1][0] = 0.0D;
    arrayOfDouble5[1][1] = 0.0D;
    arrayOfDouble5[1][2] = 0.0D;
    arrayOfDouble5[2][0] = 1.0D;
    arrayOfDouble5[2][1] = 0.0D;
    arrayOfDouble5[2][2] = 1.0D;
    System.out.println("Matrix with empty row and column: ");
    byte b2;
    for (b2 = 0; b2 < arrayOfDouble5.length; b2++) {
      for (byte b = 0; b < (arrayOfDouble5[b2]).length; b++)
        System.out.print(arrayOfDouble5[b2][b] + " "); 
      System.out.println();
    } 
    System.out.println("Reduced matrix: ");
    arrayOfDouble5 = reduceMatrix(arrayOfDouble5);
    for (b2 = 0; b2 < arrayOfDouble5.length; b2++) {
      for (byte b = 0; b < (arrayOfDouble5[b2]).length; b++)
        System.out.print(arrayOfDouble5[b2][b] + " "); 
      System.out.println();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\ContingencyTables.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */