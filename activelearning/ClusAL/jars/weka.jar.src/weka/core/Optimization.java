package weka.core;

public abstract class Optimization {
  protected double m_ALF = 1.0E-4D;
  
  protected double m_BETA = 0.9D;
  
  protected double m_TOLX = 1.0E-6D;
  
  protected double m_STPMX = 100.0D;
  
  protected int m_MAXITS = 200;
  
  protected static boolean m_Debug = false;
  
  protected double m_f;
  
  private double m_Slope;
  
  private boolean m_IsZeroStep = false;
  
  private double[] m_X;
  
  protected static double m_Epsilon = 1.0D;
  
  protected static double m_Zero;
  
  protected abstract double objectiveFunction(double[] paramArrayOfdouble) throws Exception;
  
  protected abstract double[] evaluateGradient(double[] paramArrayOfdouble) throws Exception;
  
  protected double[] evaluateHessian(double[] paramArrayOfdouble, int paramInt) throws Exception {
    return null;
  }
  
  public double getMinFunction() {
    return this.m_f;
  }
  
  public void setMaxIteration(int paramInt) {
    this.m_MAXITS = paramInt;
  }
  
  public void setDebug(boolean paramBoolean) {
    m_Debug = paramBoolean;
  }
  
  public double[] getVarbValues() {
    return this.m_X;
  }
  
  public double[] lnsrch(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, double[] paramArrayOfdouble3, double paramDouble, boolean[] paramArrayOfboolean, double[][] paramArrayOfdouble, DynamicIntArray paramDynamicIntArray) throws Exception {
    double d2;
    int i = paramArrayOfdouble1.length;
    byte b = -1;
    double d4 = Double.POSITIVE_INFINITY;
    double d5 = this.m_f;
    double d7 = 0.0D;
    double d8 = 0.0D;
    double d9 = 1.0D;
    double[] arrayOfDouble = new double[i];
    double d6 = 0.0D;
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      if (!paramArrayOfboolean[b1])
        d6 += paramArrayOfdouble3[b1] * paramArrayOfdouble3[b1]; 
    } 
    d6 = Math.sqrt(d6);
    if (m_Debug)
      System.err.println("fold:  " + Utils.doubleToString(d5, 10, 7) + "\n" + "sum:  " + Utils.doubleToString(d6, 10, 7) + "\n" + "stpmax:  " + Utils.doubleToString(paramDouble, 10, 7)); 
    if (d6 > paramDouble) {
      for (b1 = 0; b1 < i; b1++) {
        if (!paramArrayOfboolean[b1])
          paramArrayOfdouble3[b1] = paramArrayOfdouble3[b1] * paramDouble / d6; 
      } 
    } else {
      d9 = paramDouble / d6;
    } 
    this.m_Slope = 0.0D;
    for (b1 = 0; b1 < i; b1++) {
      arrayOfDouble[b1] = paramArrayOfdouble1[b1];
      if (!paramArrayOfboolean[b1])
        this.m_Slope += paramArrayOfdouble2[b1] * paramArrayOfdouble3[b1]; 
    } 
    if (m_Debug)
      System.err.print("slope:  " + Utils.doubleToString(this.m_Slope, 10, 7) + "\n"); 
    if (Math.abs(this.m_Slope) <= m_Zero) {
      if (m_Debug)
        System.err.println("Gradient and direction orthogonal -- Min. found with current fixed variables (or all variables fixed). Try to release some variables now."); 
      return arrayOfDouble;
    } 
    if (this.m_Slope > m_Zero) {
      if (m_Debug)
        for (byte b3 = 0; b3 < arrayOfDouble.length; b3++)
          System.err.println(b3 + ": isFixed=" + paramArrayOfboolean[b3] + ", x=" + arrayOfDouble[b3] + ", grad=" + paramArrayOfdouble2[b3] + ", direct=" + paramArrayOfdouble3[b3]);  
      throw new Exception("g'*p positive! -- Try to debug from here: line 327.");
    } 
    double d3 = 0.0D;
    for (b1 = 0; b1 < i; b1++) {
      if (!paramArrayOfboolean[b1]) {
        double d = Math.abs(paramArrayOfdouble3[b1]) / Math.max(Math.abs(arrayOfDouble[b1]), 1.0D);
        if (d > d3)
          d3 = d; 
      } 
    } 
    if (d3 > m_Zero) {
      d2 = this.m_TOLX / d3;
    } else {
      if (m_Debug)
        System.err.println("Zero directions for all free variables -- Min. found with current fixed variables (or all variables fixed). Try to release some variables now."); 
      return arrayOfDouble;
    } 
    for (b1 = 0; b1 < i; b1++) {
      if (!paramArrayOfboolean[b1])
        if (paramArrayOfdouble3[b1] < -m_Epsilon && !Double.isNaN(paramArrayOfdouble[0][b1])) {
          double d = (paramArrayOfdouble[0][b1] - paramArrayOfdouble1[b1]) / paramArrayOfdouble3[b1];
          if (d <= m_Zero) {
            if (m_Debug)
              System.err.println("Fix variable " + b1 + " to lower bound " + paramArrayOfdouble[0][b1] + " from value " + paramArrayOfdouble1[b1]); 
            arrayOfDouble[b1] = paramArrayOfdouble[0][b1];
            paramArrayOfboolean[b1] = true;
            d4 = 0.0D;
            paramArrayOfdouble[0][b1] = Double.NaN;
            paramDynamicIntArray.addElement(b1);
          } else if (d4 > d) {
            d4 = d;
            b = b1;
          } 
        } else if (paramArrayOfdouble3[b1] > m_Epsilon && !Double.isNaN(paramArrayOfdouble[1][b1])) {
          double d = (paramArrayOfdouble[1][b1] - paramArrayOfdouble1[b1]) / paramArrayOfdouble3[b1];
          if (d <= m_Zero) {
            if (m_Debug)
              System.err.println("Fix variable " + b1 + " to upper bound " + paramArrayOfdouble[1][b1] + " from value " + paramArrayOfdouble1[b1]); 
            arrayOfDouble[b1] = paramArrayOfdouble[1][b1];
            paramArrayOfboolean[b1] = true;
            d4 = 0.0D;
            paramArrayOfdouble[1][b1] = Double.NaN;
            paramDynamicIntArray.addElement(b1);
          } else if (d4 > d) {
            d4 = d;
            b = b1;
          } 
        }  
    } 
    if (m_Debug) {
      System.err.println("alamin: " + Utils.doubleToString(d2, 10, 7));
      System.err.println("alpha: " + Utils.doubleToString(d4, 10, 7));
    } 
    if (d4 <= m_Zero) {
      this.m_IsZeroStep = true;
      if (m_Debug)
        System.err.println("Alpha too small, try again"); 
      return arrayOfDouble;
    } 
    double d1 = d4;
    if (d1 > 1.0D)
      d1 = 1.0D; 
    double d10 = d5;
    double d11 = d1;
    double d12 = d1;
    double d13 = 0.0D;
    double d14 = this.m_f;
    double d15 = this.m_f;
    for (byte b2 = 0;; b2++) {
      double d;
      if (m_Debug)
        System.err.println("\nLine search iteration: " + b2); 
      for (b1 = 0; b1 < i; b1++) {
        if (!paramArrayOfboolean[b1]) {
          arrayOfDouble[b1] = paramArrayOfdouble1[b1] + d1 * paramArrayOfdouble3[b1];
          if (!Double.isNaN(paramArrayOfdouble[0][b1]) && arrayOfDouble[b1] < paramArrayOfdouble[0][b1]) {
            arrayOfDouble[b1] = paramArrayOfdouble[0][b1];
          } else if (!Double.isNaN(paramArrayOfdouble[1][b1]) && arrayOfDouble[b1] > paramArrayOfdouble[1][b1]) {
            arrayOfDouble[b1] = paramArrayOfdouble[1][b1];
          } 
        } 
      } 
      this.m_f = objectiveFunction(arrayOfDouble);
      if (Double.isNaN(this.m_f))
        throw new Exception("Objective function value is NaN!"); 
      while (Double.isInfinite(this.m_f)) {
        if (m_Debug)
          System.err.println("Too large m_f.  Shrink step by half."); 
        d1 *= 0.5D;
        if (d1 <= m_Epsilon) {
          if (m_Debug)
            System.err.println("Wrong starting points, change them!"); 
          return arrayOfDouble;
        } 
        for (b1 = 0; b1 < i; b1++) {
          if (!paramArrayOfboolean[b1])
            arrayOfDouble[b1] = paramArrayOfdouble1[b1] + d1 * paramArrayOfdouble3[b1]; 
        } 
        this.m_f = objectiveFunction(arrayOfDouble);
        if (Double.isNaN(this.m_f))
          throw new Exception("Objective function value is NaN!"); 
        d10 = Double.POSITIVE_INFINITY;
      } 
      if (m_Debug) {
        System.err.println("obj. function: " + Utils.doubleToString(this.m_f, 10, 7));
        System.err.println("threshold: " + Utils.doubleToString(d5 + this.m_ALF * d1 * this.m_Slope, 10, 7));
      } 
      if (this.m_f <= d5 + this.m_ALF * d1 * this.m_Slope) {
        if (m_Debug)
          System.err.println("Sufficient function decrease (alpha condition): "); 
        double[] arrayOfDouble1 = evaluateGradient(arrayOfDouble);
        d13 = 0.0D;
        for (b1 = 0; b1 < i; b1++) {
          if (!paramArrayOfboolean[b1])
            d13 += arrayOfDouble1[b1] * paramArrayOfdouble3[b1]; 
        } 
        if (d13 >= this.m_BETA * this.m_Slope) {
          if (m_Debug)
            System.err.println("Increasing derivatives (beta condition): "); 
          if (b != -1 && d1 >= d4) {
            if (paramArrayOfdouble3[b] > 0.0D) {
              arrayOfDouble[b] = paramArrayOfdouble[1][b];
              paramArrayOfdouble[1][b] = Double.NaN;
            } else {
              arrayOfDouble[b] = paramArrayOfdouble[0][b];
              paramArrayOfdouble[0][b] = Double.NaN;
            } 
            if (m_Debug)
              System.err.println("Fix variable " + b + " to bound " + arrayOfDouble[b] + " from value " + paramArrayOfdouble1[b]); 
            paramArrayOfboolean[b] = true;
            paramDynamicIntArray.addElement(b);
          } 
          return arrayOfDouble;
        } 
        if (!b2) {
          double d17 = Math.min(d4, d9);
          if (m_Debug)
            System.err.println("Alpha condition holds, increase alpha... "); 
          while (d1 < d17 && this.m_f <= d5 + this.m_ALF * d1 * this.m_Slope) {
            d12 = d1;
            d15 = this.m_f;
            d1 *= 2.0D;
            if (d1 >= d17)
              d1 = d17; 
            for (b1 = 0; b1 < i; b1++) {
              if (!paramArrayOfboolean[b1])
                arrayOfDouble[b1] = paramArrayOfdouble1[b1] + d1 * paramArrayOfdouble3[b1]; 
            } 
            this.m_f = objectiveFunction(arrayOfDouble);
            if (Double.isNaN(this.m_f))
              throw new Exception("Objective function value is NaN!"); 
            arrayOfDouble1 = evaluateGradient(arrayOfDouble);
            d13 = 0.0D;
            for (b1 = 0; b1 < i; b1++) {
              if (!paramArrayOfboolean[b1])
                d13 += arrayOfDouble1[b1] * paramArrayOfdouble3[b1]; 
            } 
            if (d13 >= this.m_BETA * this.m_Slope) {
              if (m_Debug)
                System.err.println("Increasing derivatives (beta condition): \nnewSlope = " + Utils.doubleToString(d13, 10, 7)); 
              if (b != -1 && d1 >= d4) {
                if (paramArrayOfdouble3[b] > 0.0D) {
                  arrayOfDouble[b] = paramArrayOfdouble[1][b];
                  paramArrayOfdouble[1][b] = Double.NaN;
                } else {
                  arrayOfDouble[b] = paramArrayOfdouble[0][b];
                  paramArrayOfdouble[0][b] = Double.NaN;
                } 
                if (m_Debug)
                  System.err.println("Fix variable " + b + " to bound " + arrayOfDouble[b] + " from value " + paramArrayOfdouble1[b]); 
                paramArrayOfboolean[b] = true;
                paramDynamicIntArray.addElement(b);
              } 
              return arrayOfDouble;
            } 
          } 
          d11 = d1;
          d14 = this.m_f;
          break;
        } 
        if (m_Debug)
          System.err.println("Alpha condition holds."); 
        d11 = d7;
        d12 = d1;
        d15 = this.m_f;
        break;
      } 
      if (d1 < d2) {
        if (d10 < d5) {
          d1 = Math.min(1.0D, d4);
          for (b1 = 0; b1 < i; b1++) {
            if (!paramArrayOfboolean[b1])
              arrayOfDouble[b1] = paramArrayOfdouble1[b1] + d1 * paramArrayOfdouble3[b1]; 
          } 
          if (m_Debug)
            System.err.println("No feasible lambda: still take alpha=" + d1); 
          if (b != -1 && d1 >= d4) {
            if (paramArrayOfdouble3[b] > 0.0D) {
              arrayOfDouble[b] = paramArrayOfdouble[1][b];
              paramArrayOfdouble[1][b] = Double.NaN;
            } else {
              arrayOfDouble[b] = paramArrayOfdouble[0][b];
              paramArrayOfdouble[0][b] = Double.NaN;
            } 
            if (m_Debug)
              System.err.println("Fix variable " + b + " to bound " + arrayOfDouble[b] + " from value " + paramArrayOfdouble1[b]); 
            paramArrayOfboolean[b] = true;
            paramDynamicIntArray.addElement(b);
          } 
        } else {
          for (b1 = 0; b1 < i; b1++)
            arrayOfDouble[b1] = paramArrayOfdouble1[b1]; 
          this.m_f = d5;
          if (m_Debug)
            System.err.println("Cannot find feasible lambda"); 
        } 
        return arrayOfDouble;
      } 
      if (!b2) {
        if (!Double.isInfinite(d10))
          d10 = this.m_f; 
        d = -0.5D * d1 * this.m_Slope / ((this.m_f - d5) / d1 - this.m_Slope);
      } else {
        double d19 = this.m_f - d5 - d1 * this.m_Slope;
        double d20 = d14 - d5 - d7 * this.m_Slope;
        double d17 = (d19 / d1 * d1 - d20 / d7 * d7) / (d1 - d7);
        double d18 = (-d7 * d19 / d1 * d1 + d1 * d20 / d7 * d7) / (d1 - d7);
        if (d17 == 0.0D) {
          d = -this.m_Slope / 2.0D * d18;
        } else {
          d8 = d18 * d18 - 3.0D * d17 * this.m_Slope;
          if (d8 < 0.0D)
            d8 = 0.0D; 
          double d21 = -d18 + Math.sqrt(d8);
          if (d21 >= Double.MAX_VALUE) {
            d21 = Double.MAX_VALUE;
            if (m_Debug)
              System.err.print("-b+sqrt(disc) too large! Set it to MAX_VALUE."); 
          } 
          d = d21 / 3.0D * d17;
        } 
        if (m_Debug)
          System.err.print("Cubic interpolation: \na:   " + Utils.doubleToString(d17, 10, 7) + "\n" + "b:   " + Utils.doubleToString(d18, 10, 7) + "\n" + "disc:   " + Utils.doubleToString(d8, 10, 7) + "\n" + "tmplam:   " + d + "\n" + "alam:   " + Utils.doubleToString(d1, 10, 7) + "\n"); 
        if (d > 0.5D * d1)
          d = 0.5D * d1; 
      } 
      d7 = d1;
      d14 = this.m_f;
      d1 = Math.max(d, 0.1D * d1);
      if (d1 > d4)
        throw new Exception("Sth. wrong in lnsrch:Lambda infeasible!(lambda=" + d1 + ", alpha=" + d4 + ", upper=" + d + "|" + (-d4 * this.m_Slope / 2.0D * ((this.m_f - d5) / d4 - this.m_Slope)) + ", m_f=" + this.m_f + ", fold=" + d5 + ", slope=" + this.m_Slope); 
    } 
    double d16 = d11 - d12;
    if (m_Debug)
      System.err.println("Last stage of searching for beta condition (alam between " + Utils.doubleToString(d12, 10, 7) + " and " + Utils.doubleToString(d11, 10, 7) + ")...\n" + "Quadratic Interpolation(QI):\n" + "Last newSlope = " + Utils.doubleToString(d13, 10, 7)); 
    while (d13 < this.m_BETA * this.m_Slope && d16 >= d2) {
      double d = -0.5D * d13 * d16 * d16 / (d14 - d15 - d13 * d16);
      if (m_Debug)
        System.err.println("fhi = " + d14 + "\n" + "flo = " + d15 + "\n" + "ldiff = " + d16 + "\n" + "lincr (using QI) = " + d + "\n"); 
      if (d < 0.2D * d16)
        d = 0.2D * d16; 
      d1 = d12 + d;
      if (d1 >= d11) {
        d1 = d11;
        d = d16;
      } 
      for (b1 = 0; b1 < i; b1++) {
        if (!paramArrayOfboolean[b1])
          arrayOfDouble[b1] = paramArrayOfdouble1[b1] + d1 * paramArrayOfdouble3[b1]; 
      } 
      this.m_f = objectiveFunction(arrayOfDouble);
      if (Double.isNaN(this.m_f))
        throw new Exception("Objective function value is NaN!"); 
      if (this.m_f > d5 + this.m_ALF * d1 * this.m_Slope) {
        d16 = d;
        d14 = this.m_f;
        continue;
      } 
      double[] arrayOfDouble1 = evaluateGradient(arrayOfDouble);
      d13 = 0.0D;
      for (b1 = 0; b1 < i; b1++) {
        if (!paramArrayOfboolean[b1])
          d13 += arrayOfDouble1[b1] * paramArrayOfdouble3[b1]; 
      } 
      if (d13 < this.m_BETA * this.m_Slope) {
        d12 = d1;
        d16 -= d;
        d15 = this.m_f;
      } 
    } 
    if (d13 < this.m_BETA * this.m_Slope) {
      if (m_Debug)
        System.err.println("Beta condition cannot be satisfied, take alpha condition"); 
      d1 = d12;
      for (b1 = 0; b1 < i; b1++) {
        if (!paramArrayOfboolean[b1])
          arrayOfDouble[b1] = paramArrayOfdouble1[b1] + d1 * paramArrayOfdouble3[b1]; 
      } 
      this.m_f = d15;
    } else if (m_Debug) {
      System.err.println("Both alpha and beta conditions are satisfied. alam=" + Utils.doubleToString(d1, 10, 7));
    } 
    if (b != -1 && d1 >= d4) {
      if (paramArrayOfdouble3[b] > 0.0D) {
        arrayOfDouble[b] = paramArrayOfdouble[1][b];
        paramArrayOfdouble[1][b] = Double.NaN;
      } else {
        arrayOfDouble[b] = paramArrayOfdouble[0][b];
        paramArrayOfdouble[0][b] = Double.NaN;
      } 
      if (m_Debug)
        System.err.println("Fix variable " + b + " to bound " + arrayOfDouble[b] + " from value " + paramArrayOfdouble1[b]); 
      paramArrayOfboolean[b] = true;
      paramDynamicIntArray.addElement(b);
    } 
    return arrayOfDouble;
  }
  
  public double[] findArgmin(double[] paramArrayOfdouble, double[][] paramArrayOfdouble1) throws Exception {
    int i = paramArrayOfdouble.length;
    boolean[] arrayOfBoolean = new boolean[i];
    double[][] arrayOfDouble = new double[2][i];
    DynamicIntArray dynamicIntArray1 = new DynamicIntArray(this, paramArrayOfdouble1.length);
    DynamicIntArray dynamicIntArray2 = null;
    DynamicIntArray dynamicIntArray3 = null;
    this.m_f = objectiveFunction(paramArrayOfdouble);
    if (Double.isNaN(this.m_f))
      throw new Exception("Objective function value is NaN!"); 
    double d1 = 0.0D;
    double[] arrayOfDouble1 = evaluateGradient(paramArrayOfdouble);
    double[] arrayOfDouble2 = new double[i];
    double[] arrayOfDouble3 = new double[i];
    double[] arrayOfDouble4 = new double[i];
    double[] arrayOfDouble5 = new double[i];
    Matrix matrix = new Matrix(i, i);
    double[] arrayOfDouble6 = new double[i];
    for (byte b1 = 0; b1 < i; b1++) {
      matrix.setRow(b1, new double[i]);
      matrix.setElement(b1, b1, 1.0D);
      arrayOfDouble6[b1] = 1.0D;
      arrayOfDouble4[b1] = -arrayOfDouble1[b1];
      d1 += arrayOfDouble1[b1] * arrayOfDouble1[b1];
      arrayOfDouble5[b1] = paramArrayOfdouble[b1];
      arrayOfDouble[0][b1] = paramArrayOfdouble1[0][b1];
      arrayOfDouble[1][b1] = paramArrayOfdouble1[1][b1];
      arrayOfBoolean[b1] = false;
    } 
    double d2 = this.m_STPMX * Math.max(Math.sqrt(d1), i);
    for (byte b2 = 0; b2 < this.m_MAXITS; b2++) {
      if (m_Debug)
        System.err.println("\nIteration # " + b2 + ":"); 
      double[] arrayOfDouble8 = arrayOfDouble5;
      double[] arrayOfDouble7 = arrayOfDouble1;
      if (m_Debug)
        System.err.println("Line search ... "); 
      this.m_IsZeroStep = false;
      arrayOfDouble5 = lnsrch(arrayOfDouble5, arrayOfDouble1, arrayOfDouble4, d2, arrayOfBoolean, arrayOfDouble, dynamicIntArray1);
      if (m_Debug)
        System.err.println("Line search finished."); 
      if (this.m_IsZeroStep) {
        for (byte b = 0; b < dynamicIntArray1.size(); b++) {
          int j = dynamicIntArray1.elementAt(b);
          matrix.setRow(j, new double[i]);
          matrix.setColumn(j, new double[i]);
          arrayOfDouble6[j] = 0.0D;
        } 
        arrayOfDouble1 = evaluateGradient(arrayOfDouble5);
        b2--;
      } else {
        boolean bool1 = false;
        double d3 = 0.0D;
        for (byte b = 0; b < i; b++) {
          arrayOfDouble3[b] = arrayOfDouble5[b] - arrayOfDouble8[b];
          double d = Math.abs(arrayOfDouble3[b]) / Math.max(Math.abs(arrayOfDouble5[b]), 1.0D);
          if (d > d3)
            d3 = d; 
        } 
        if (d3 < m_Zero) {
          if (m_Debug)
            System.err.println("\nDeltaX converge: " + d3); 
          bool1 = true;
        } 
        arrayOfDouble1 = evaluateGradient(arrayOfDouble5);
        d3 = 0.0D;
        double d4 = 0.0D;
        double d5 = 0.0D;
        double d6 = 0.0D;
        double d7 = 0.0D;
        int j;
        for (j = 0; j < i; j++) {
          if (!arrayOfBoolean[j]) {
            arrayOfDouble2[j] = arrayOfDouble1[j] - arrayOfDouble7[j];
            d4 += arrayOfDouble3[j] * arrayOfDouble2[j];
            d5 += arrayOfDouble3[j] * arrayOfDouble3[j];
            d6 += arrayOfDouble2[j] * arrayOfDouble2[j];
          } else {
            d7 += arrayOfDouble3[j] * (arrayOfDouble1[j] - arrayOfDouble7[j]);
          } 
          double d = Math.abs(arrayOfDouble1[j]) * Math.max(Math.abs(arrayOfDouble4[j]), 1.0D) / Math.max(Math.abs(this.m_f), 1.0D);
          if (d > d3)
            d3 = d; 
        } 
        if (d3 < m_Zero) {
          if (m_Debug)
            System.err.println("Gradient converge: " + d3); 
          bool1 = true;
        } 
        if (m_Debug)
          System.err.println("dg'*dx=" + (d4 + d7)); 
        if (Math.abs(d4 + d7) < m_Zero)
          bool1 = true; 
        j = dynamicIntArray1.size();
        boolean bool2 = true;
        if (bool1) {
          if (m_Debug)
            System.err.println("Test any release possible ..."); 
          if (dynamicIntArray2 != null)
            dynamicIntArray3 = (DynamicIntArray)dynamicIntArray2.copy(); 
          dynamicIntArray2 = new DynamicIntArray(this, dynamicIntArray1.size());
          int k;
          for (k = j - 1; k >= 0; k--) {
            double d9;
            int m = dynamicIntArray1.elementAt(k);
            double[] arrayOfDouble11 = evaluateHessian(arrayOfDouble5, m);
            double d8 = 0.0D;
            if (arrayOfDouble11 != null)
              for (byte b5 = 0; b5 < arrayOfDouble11.length; b5++) {
                if (!arrayOfBoolean[b5])
                  d8 += arrayOfDouble11[b5] * arrayOfDouble4[b5]; 
              }  
            if (arrayOfDouble5[m] >= paramArrayOfdouble1[1][m]) {
              d9 = -arrayOfDouble1[m];
            } else if (arrayOfDouble5[m] <= paramArrayOfdouble1[0][m]) {
              d9 = arrayOfDouble1[m];
            } else {
              throw new Exception("x[" + m + "] not fixed on the" + " bounds where it should have been!");
            } 
            double d10 = d9 + d8;
            if (m_Debug)
              System.err.println("Variable " + m + ": Lagrangian=" + d9 + "|" + d10); 
            boolean bool = (2.0D * Math.abs(d8) < Math.min(Math.abs(d9), Math.abs(d10))) ? true : false;
            if (d9 * d10 > 0.0D && bool && d10 < 0.0D) {
              dynamicIntArray2.addElement(m);
              dynamicIntArray1.removeElementAt(k);
              bool1 = false;
            } 
            if (arrayOfDouble11 == null && dynamicIntArray2 != null && dynamicIntArray2.equal(dynamicIntArray3))
              bool1 = true; 
          } 
          if (bool1) {
            if (m_Debug)
              System.err.println("Minimum found."); 
            this.m_f = objectiveFunction(arrayOfDouble5);
            if (Double.isNaN(this.m_f))
              throw new Exception("Objective function value is NaN!"); 
            return arrayOfDouble5;
          } 
          for (k = 0; k < dynamicIntArray2.size(); k++) {
            int m = dynamicIntArray2.elementAt(k);
            arrayOfBoolean[m] = false;
            if (arrayOfDouble5[m] <= paramArrayOfdouble1[0][m]) {
              arrayOfDouble[0][m] = paramArrayOfdouble1[0][m];
              if (m_Debug)
                System.err.println("Free variable " + m + " from bound " + arrayOfDouble[0][m]); 
            } else {
              arrayOfDouble[1][m] = paramArrayOfdouble1[1][m];
              if (m_Debug)
                System.err.println("Free variable " + m + " from bound " + arrayOfDouble[1][m]); 
            } 
            matrix.setElement(m, m, 1.0D);
            arrayOfDouble6[m] = 1.0D;
            bool2 = false;
          } 
        } 
        if (d4 < Math.max(m_Zero * Math.sqrt(d5) * Math.sqrt(d6), m_Zero)) {
          if (m_Debug)
            System.err.println("dg'*dx negative!"); 
          bool2 = false;
        } 
        if (bool2) {
          double d = 1.0D / d4;
          updateCholeskyFactor(matrix, arrayOfDouble6, arrayOfDouble2, d, arrayOfBoolean);
          d = 1.0D / this.m_Slope;
          updateCholeskyFactor(matrix, arrayOfDouble6, arrayOfDouble7, d, arrayOfBoolean);
        } 
      } 
      Matrix matrix1 = new Matrix(i, i);
      double[] arrayOfDouble9 = new double[i];
      for (byte b3 = 0; b3 < i; b3++) {
        if (!arrayOfBoolean[b3]) {
          arrayOfDouble9[b3] = -arrayOfDouble1[b3];
        } else {
          arrayOfDouble9[b3] = 0.0D;
        } 
        for (byte b = b3; b < i; b++) {
          if (!arrayOfBoolean[b] && !arrayOfBoolean[b3])
            matrix1.setElement(b, b3, matrix.getElement(b, b3) * arrayOfDouble6[b3]); 
        } 
      } 
      double[] arrayOfDouble10 = solveTriangle(matrix1, arrayOfDouble9, true, arrayOfBoolean);
      matrix1 = null;
      byte b4;
      for (b4 = 0; b4 < arrayOfDouble10.length; b4++) {
        if (Double.isNaN(arrayOfDouble10[b4]))
          throw new Exception("L*direct[" + b4 + "] is NaN!" + "|-g=" + arrayOfDouble9[b4] + "|" + arrayOfBoolean[b4] + "|diag=" + arrayOfDouble6[b4]); 
      } 
      arrayOfDouble4 = solveTriangle(matrix, arrayOfDouble10, false, arrayOfBoolean);
      for (b4 = 0; b4 < arrayOfDouble4.length; b4++) {
        if (Double.isNaN(arrayOfDouble4[b4]))
          throw new Exception("direct is NaN!"); 
      } 
    } 
    if (m_Debug)
      System.err.println("Cannot find minimum -- too many interations!"); 
    this.m_X = arrayOfDouble5;
    return null;
  }
  
  public static double[] solveTriangle(Matrix paramMatrix, double[] paramArrayOfdouble, boolean paramBoolean, boolean[] paramArrayOfboolean) {
    int i = paramArrayOfdouble.length;
    double[] arrayOfDouble = new double[i];
    if (paramArrayOfboolean == null)
      paramArrayOfboolean = new boolean[i]; 
    if (paramBoolean) {
      byte b;
      for (b = 0; b < i && paramArrayOfboolean[b]; b++)
        arrayOfDouble[b] = 0.0D; 
      if (b < i) {
        arrayOfDouble[b] = paramArrayOfdouble[b] / paramMatrix.getElement(b, b);
        while (b < i) {
          if (!paramArrayOfboolean[b]) {
            double d = paramArrayOfdouble[b];
            for (byte b1 = 0; b1 < b; b1++)
              d -= paramMatrix.getElement(b, b1) * arrayOfDouble[b1]; 
            arrayOfDouble[b] = d / paramMatrix.getElement(b, b);
          } else {
            arrayOfDouble[b] = 0.0D;
          } 
          b++;
        } 
      } 
    } else {
      int j;
      for (j = i - 1; j >= 0 && paramArrayOfboolean[j]; j--)
        arrayOfDouble[j] = 0.0D; 
      if (j >= 0) {
        arrayOfDouble[j] = paramArrayOfdouble[j] / paramMatrix.getElement(j, j);
        while (j >= 0) {
          if (!paramArrayOfboolean[j]) {
            double d = paramArrayOfdouble[j];
            for (int k = j + 1; k < i; k++)
              d -= paramMatrix.getElement(k, j) * arrayOfDouble[k]; 
            arrayOfDouble[j] = d / paramMatrix.getElement(j, j);
          } else {
            arrayOfDouble[j] = 0.0D;
          } 
          j--;
        } 
      } 
    } 
    return arrayOfDouble;
  }
  
  protected void updateCholeskyFactor(Matrix paramMatrix, double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, double paramDouble, boolean[] paramArrayOfboolean) throws Exception {
    int i = paramArrayOfdouble2.length;
    double[] arrayOfDouble = new double[i];
    byte b;
    for (b = 0; b < paramArrayOfdouble2.length; b++) {
      if (!paramArrayOfboolean[b]) {
        arrayOfDouble[b] = paramArrayOfdouble2[b];
      } else {
        arrayOfDouble[b] = 0.0D;
      } 
    } 
    if (paramDouble > 0.0D) {
      double d = paramDouble;
      for (b = 0; b < i; b++) {
        if (!paramArrayOfboolean[b]) {
          double d1 = arrayOfDouble[b];
          double d3 = paramArrayOfdouble1[b];
          double d4 = d3 + d * d1 * d1;
          paramArrayOfdouble1[b] = d4;
          double d2 = d1 * d / d4;
          d *= d3 / d4;
          for (int j = b + 1; j < i; j++) {
            if (!paramArrayOfboolean[j]) {
              double d5 = paramMatrix.getElement(j, b);
              arrayOfDouble[j] = arrayOfDouble[j] - d1 * d5;
              paramMatrix.setElement(j, b, d5 + d2 * arrayOfDouble[j]);
            } else {
              paramMatrix.setElement(j, b, 0.0D);
            } 
          } 
        } 
      } 
    } else {
      double[] arrayOfDouble1 = solveTriangle(paramMatrix, paramArrayOfdouble2, true, paramArrayOfboolean);
      double d1 = 0.0D;
      for (byte b1 = 0; b1 < i; b1++) {
        if (!paramArrayOfboolean[b1])
          d1 += arrayOfDouble1[b1] * arrayOfDouble1[b1] / paramArrayOfdouble1[b1]; 
      } 
      double d2 = 1.0D + paramDouble * d1;
      d2 = (d2 < 0.0D) ? 0.0D : Math.sqrt(d2);
      double d3 = paramDouble;
      double d4 = paramDouble / (1.0D + d2);
      for (byte b2 = 0; b2 < i; b2++) {
        if (!paramArrayOfboolean[b2]) {
          double d9 = paramArrayOfdouble1[b2];
          double d5 = arrayOfDouble1[b2] * arrayOfDouble1[b2] / d9;
          double d8 = 1.0D + d4 * d5;
          d1 -= d5;
          if (d1 < 0.0D)
            d1 = 0.0D; 
          double d10 = d4 * d4 * d5 * d1;
          if (b2 < i - 1 && d10 <= m_Zero)
            d10 = m_Zero; 
          double d7 = d8 * d8 + d10;
          paramArrayOfdouble1[b2] = d7 * d9;
          if (Double.isNaN(paramArrayOfdouble1[b2]))
            throw new Exception("d[" + b2 + "] NaN! P=" + arrayOfDouble1[b2] + ",d=" + d9 + ",t=" + d1 + ",p=" + d5 + ",sigma=" + d4 + ",sclar=" + paramDouble); 
          double d6 = d3 * arrayOfDouble1[b2] / d7 * d9;
          d3 /= d7;
          d7 = Math.sqrt(d7);
          double d11 = d4;
          d4 *= (1.0D + d7) / d7 * (d8 + d7);
          if (b2 < i - 1 && (Double.isNaN(d4) || Double.isInfinite(d4)))
            throw new Exception("sigma NaN/Inf! rho=" + d7 + ",theta=" + d8 + ",P[" + b2 + "]=" + arrayOfDouble1[b2] + ",p=" + d5 + ",d=" + d9 + ",t=" + d1 + ",oldsigma=" + d11); 
          for (int j = b2 + 1; j < i; j++) {
            if (!paramArrayOfboolean[j]) {
              double d = paramMatrix.getElement(j, b2);
              arrayOfDouble[j] = arrayOfDouble[j] - arrayOfDouble1[b2] * d;
              paramMatrix.setElement(j, b2, d + d6 * arrayOfDouble[j]);
            } else {
              paramMatrix.setElement(j, b2, 0.0D);
            } 
          } 
        } 
      } 
    } 
  }
  
  static {
    while (1.0D + m_Epsilon > 1.0D)
      m_Epsilon /= 2.0D; 
    m_Epsilon *= 2.0D;
    m_Zero = Math.sqrt(m_Epsilon);
    if (m_Debug)
      System.err.print("Machine precision is " + m_Epsilon + " and zero set to " + m_Zero); 
  }
  
  private class DynamicIntArray {
    private int[] m_Objects;
    
    private int m_Size;
    
    private int m_CapacityIncrement;
    
    private int m_CapacityMultiplier;
    
    private final Optimization this$0;
    
    public DynamicIntArray(Optimization this$0, int param1Int) {
      this.this$0 = this$0;
      this.m_Size = 0;
      this.m_CapacityIncrement = 1;
      this.m_CapacityMultiplier = 2;
      this.m_Objects = new int[param1Int];
    }
    
    public final void addElement(int param1Int) {
      if (this.m_Size == this.m_Objects.length) {
        int[] arrayOfInt = new int[this.m_CapacityMultiplier * (this.m_Objects.length + this.m_CapacityIncrement)];
        System.arraycopy(this.m_Objects, 0, arrayOfInt, 0, this.m_Size);
        this.m_Objects = arrayOfInt;
      } 
      this.m_Objects[this.m_Size] = param1Int;
      this.m_Size++;
    }
    
    public final Object copy() {
      DynamicIntArray dynamicIntArray = new DynamicIntArray(this.this$0, this.m_Objects.length);
      dynamicIntArray.m_Size = this.m_Size;
      dynamicIntArray.m_CapacityIncrement = this.m_CapacityIncrement;
      dynamicIntArray.m_CapacityMultiplier = this.m_CapacityMultiplier;
      System.arraycopy(this.m_Objects, 0, dynamicIntArray.m_Objects, 0, this.m_Size);
      return dynamicIntArray;
    }
    
    public final int elementAt(int param1Int) {
      return this.m_Objects[param1Int];
    }
    
    private boolean equal(DynamicIntArray param1DynamicIntArray) {
      if (param1DynamicIntArray == null || size() != param1DynamicIntArray.size())
        return false; 
      int i = size();
      int[] arrayOfInt1 = Utils.sort(this.m_Objects);
      int[] arrayOfInt2 = Utils.sort(param1DynamicIntArray.m_Objects);
      for (byte b = 0; b < i; b++) {
        if (this.m_Objects[arrayOfInt1[b]] != param1DynamicIntArray.m_Objects[arrayOfInt2[b]])
          return false; 
      } 
      return true;
    }
    
    public final void removeElementAt(int param1Int) {
      System.arraycopy(this.m_Objects, param1Int + 1, this.m_Objects, param1Int, this.m_Size - param1Int - 1);
      this.m_Size--;
    }
    
    public final void removeAllElements() {
      this.m_Objects = new int[this.m_Objects.length];
      this.m_Size = 0;
    }
    
    public final int size() {
      return this.m_Size;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Optimization.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */