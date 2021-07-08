package weka.classifiers.functions.pace;

import java.text.DecimalFormat;
import java.util.Random;

public class PaceMatrix extends Matrix {
  public PaceMatrix(int paramInt1, int paramInt2) {
    super(paramInt1, paramInt2);
  }
  
  public PaceMatrix(int paramInt1, int paramInt2, double paramDouble) {
    super(paramInt1, paramInt2, paramDouble);
  }
  
  public PaceMatrix(double[][] paramArrayOfdouble) {
    super(paramArrayOfdouble);
  }
  
  public PaceMatrix(double[][] paramArrayOfdouble, int paramInt1, int paramInt2) {
    super(paramArrayOfdouble, paramInt1, paramInt2);
  }
  
  public PaceMatrix(double[] paramArrayOfdouble, int paramInt) {
    super(paramArrayOfdouble, paramInt);
  }
  
  public PaceMatrix(DoubleVector paramDoubleVector) {
    this(paramDoubleVector.size(), 1);
    setMatrix(0, paramDoubleVector.size() - 1, 0, paramDoubleVector);
  }
  
  public PaceMatrix(Matrix paramMatrix) {
    super(paramMatrix.getRowDimension(), paramMatrix.getColumnDimension());
    this.A = paramMatrix.getArray();
  }
  
  public void setRowDimension(int paramInt) {
    this.m = paramInt;
  }
  
  public void setColumnDimension(int paramInt) {
    this.n = paramInt;
  }
  
  public Object clone() {
    PaceMatrix paceMatrix = new PaceMatrix(this.m, this.n);
    double[][] arrayOfDouble = paceMatrix.getArray();
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b][b1] = this.A[b][b1]; 
    } 
    return paceMatrix;
  }
  
  public void setPlus(int paramInt1, int paramInt2, double paramDouble) {
    this.A[paramInt1][paramInt2] = this.A[paramInt1][paramInt2] + paramDouble;
  }
  
  public void setTimes(int paramInt1, int paramInt2, double paramDouble) {
    this.A[paramInt1][paramInt2] = this.A[paramInt1][paramInt2] * paramDouble;
  }
  
  public void setMatrix(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble) {
    try {
      for (int i = paramInt1; i <= paramInt2; i++) {
        for (int j = paramInt3; j <= paramInt4; j++)
          this.A[i][j] = paramDouble; 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException("Index out of bounds");
    } 
  }
  
  public void setMatrix(int paramInt1, int paramInt2, int paramInt3, DoubleVector paramDoubleVector) {
    for (int i = paramInt1; i <= paramInt2; i++)
      this.A[i][paramInt3] = paramDoubleVector.get(i - paramInt1); 
  }
  
  public void setMatrix(double[] paramArrayOfdouble, boolean paramBoolean) {
    try {
      if (paramArrayOfdouble.length != this.m * this.n)
        throw new IllegalArgumentException("sizes not match."); 
      byte b = 0;
      if (paramBoolean) {
        for (byte b1 = 0; b1 < this.m; b1++) {
          for (byte b2 = 0; b2 < this.n; b2++) {
            this.A[b1][b2] = paramArrayOfdouble[b];
            b++;
          } 
        } 
      } else {
        for (byte b1 = 0; b1 < this.n; b1++) {
          for (byte b2 = 0; b2 < this.m; b2++) {
            this.A[b2][b1] = paramArrayOfdouble[b];
            b++;
          } 
        } 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException("Submatrix indices");
    } 
  }
  
  public double maxAbs() {
    double d = Math.abs(this.A[0][0]);
    for (byte b = 0; b < this.n; b++) {
      for (byte b1 = 0; b1 < this.m; b1++)
        d = Math.max(d, Math.abs(this.A[b1][b])); 
    } 
    return d;
  }
  
  public double maxAbs(int paramInt1, int paramInt2, int paramInt3) {
    double d = Math.abs(this.A[paramInt1][paramInt3]);
    for (int i = paramInt1 + 1; i <= paramInt2; i++)
      d = Math.max(d, Math.abs(this.A[i][paramInt3])); 
    return d;
  }
  
  public double minAbs(int paramInt1, int paramInt2, int paramInt3) {
    double d = Math.abs(this.A[paramInt1][paramInt3]);
    for (int i = paramInt1 + 1; i <= paramInt2; i++)
      d = Math.min(d, Math.abs(this.A[i][paramInt3])); 
    return d;
  }
  
  public boolean isEmpty() {
    return (this.m == 0 || this.n == 0) ? true : ((this.A == null));
  }
  
  public DoubleVector getColumn(int paramInt) {
    DoubleVector doubleVector = new DoubleVector(this.m);
    double[] arrayOfDouble = doubleVector.getArray();
    for (byte b = 0; b < this.m; b++)
      arrayOfDouble[b] = this.A[b][paramInt]; 
    return doubleVector;
  }
  
  public DoubleVector getColumn(int paramInt1, int paramInt2, int paramInt3) {
    DoubleVector doubleVector = new DoubleVector(paramInt2 - paramInt1 + 1);
    double[] arrayOfDouble = doubleVector.getArray();
    byte b = 0;
    for (int i = paramInt1; i <= paramInt2; i++) {
      arrayOfDouble[b] = this.A[i][paramInt3];
      b++;
    } 
    return doubleVector;
  }
  
  public double times(int paramInt1, int paramInt2, int paramInt3, PaceMatrix paramPaceMatrix, int paramInt4) {
    double d = 0.0D;
    for (int i = paramInt2; i <= paramInt3; i++)
      d += this.A[paramInt1][i] * paramPaceMatrix.A[i][paramInt4]; 
    return d;
  }
  
  protected DecimalFormat[] format() {
    return format(0, this.m - 1, 0, this.n - 1, 7, false);
  }
  
  protected DecimalFormat[] format(int paramInt) {
    return format(0, this.m - 1, 0, this.n - 1, paramInt, false);
  }
  
  protected DecimalFormat[] format(int paramInt, boolean paramBoolean) {
    return format(0, this.m - 1, 0, this.n - 1, paramInt, paramBoolean);
  }
  
  protected DecimalFormat format(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    FlexibleDecimalFormat flexibleDecimalFormat = new FlexibleDecimalFormat(paramInt4, paramBoolean);
    flexibleDecimalFormat.grouping(true);
    for (int i = paramInt1; i <= paramInt2; i++)
      flexibleDecimalFormat.update(this.A[i][paramInt3]); 
    return flexibleDecimalFormat;
  }
  
  protected DecimalFormat[] format(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean) {
    DecimalFormat[] arrayOfDecimalFormat = new DecimalFormat[paramInt4 - paramInt3 + 1];
    for (int i = paramInt3; i <= paramInt4; i++)
      arrayOfDecimalFormat[i] = format(paramInt1, paramInt2, i, paramInt5, paramBoolean); 
    return arrayOfDecimalFormat;
  }
  
  public String toString() {
    return toString(5, false);
  }
  
  public String toString(int paramInt, boolean paramBoolean) {
    if (isEmpty())
      return "null matrix"; 
    StringBuffer stringBuffer = new StringBuffer();
    DecimalFormat[] arrayOfDecimalFormat = format(paramInt, paramBoolean);
    byte b1 = 0;
    int i = 0;
    byte b2 = 80;
    int[] arrayOfInt = new int[this.n];
    byte b3 = 0;
    int j;
    for (j = 0; j < this.n; j++) {
      int k = arrayOfDecimalFormat[j].format(this.A[0][j]).length();
      if (i + 1 + k > b2 - 1) {
        arrayOfInt[b3++] = b1;
        i = 0;
        b1 = 0;
      } 
      i += 1 + k;
      b1++;
    } 
    arrayOfInt[b3] = b1;
    b3 = 0;
    j = 0;
    while (j < this.n) {
      for (byte b = 0; b < this.m; b++) {
        for (int k = j; k < j + arrayOfInt[b3]; k++)
          stringBuffer.append(" " + arrayOfDecimalFormat[k].format(this.A[b][k])); 
        stringBuffer.append("\n");
      } 
      j += arrayOfInt[b3];
      b3++;
      stringBuffer.append("\n");
    } 
    return stringBuffer.toString();
  }
  
  public double sum2(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    double d = 0.0D;
    if (paramBoolean) {
      for (int i = paramInt2; i <= paramInt3; i++)
        d += this.A[i][paramInt1] * this.A[i][paramInt1]; 
    } else {
      for (int i = paramInt2; i <= paramInt3; i++)
        d += this.A[paramInt1][i] * this.A[paramInt1][i]; 
    } 
    return d;
  }
  
  public double[] sum2(boolean paramBoolean) {
    int i = paramBoolean ? this.n : this.m;
    int j = paramBoolean ? this.m : this.n;
    double[] arrayOfDouble = new double[i];
    for (byte b = 0; b < i; b++)
      arrayOfDouble[b] = sum2(b, 0, j - 1, paramBoolean); 
    return arrayOfDouble;
  }
  
  public double[] h1(int paramInt1, int paramInt2) {
    double[] arrayOfDouble = new double[2];
    double d = sum2(paramInt1, paramInt2, this.m - 1, true);
    arrayOfDouble[0] = (this.A[paramInt2][paramInt1] >= 0.0D) ? -Math.sqrt(d) : Math.sqrt(d);
    this.A[paramInt2][paramInt1] = this.A[paramInt2][paramInt1] - arrayOfDouble[0];
    arrayOfDouble[1] = this.A[paramInt2][paramInt1] * arrayOfDouble[0];
    return arrayOfDouble;
  }
  
  public void h2(int paramInt1, int paramInt2, double paramDouble, PaceMatrix paramPaceMatrix, int paramInt3) {
    double d1 = 0.0D;
    int i;
    for (i = paramInt2; i < this.m; i++)
      d1 += this.A[i][paramInt1] * paramPaceMatrix.A[i][paramInt3]; 
    double d2 = d1 / paramDouble;
    for (i = paramInt2; i < this.m; i++)
      paramPaceMatrix.A[i][paramInt3] = paramPaceMatrix.A[i][paramInt3] + d2 * this.A[i][paramInt1]; 
  }
  
  public double[] g1(double paramDouble1, double paramDouble2) {
    double[] arrayOfDouble = new double[2];
    double d = Maths.hypot(paramDouble1, paramDouble2);
    if (d == 0.0D) {
      arrayOfDouble[0] = 1.0D;
      arrayOfDouble[1] = 0.0D;
    } else {
      arrayOfDouble[0] = paramDouble1 / d;
      arrayOfDouble[1] = paramDouble2 / d;
    } 
    return arrayOfDouble;
  }
  
  public void g2(double[] paramArrayOfdouble, int paramInt1, int paramInt2, int paramInt3) {
    double d = paramArrayOfdouble[0] * this.A[paramInt1][paramInt3] + paramArrayOfdouble[1] * this.A[paramInt2][paramInt3];
    this.A[paramInt2][paramInt3] = -paramArrayOfdouble[1] * this.A[paramInt1][paramInt3] + paramArrayOfdouble[0] * this.A[paramInt2][paramInt3];
    this.A[paramInt1][paramInt3] = d;
  }
  
  public void forward(PaceMatrix paramPaceMatrix, IntVector paramIntVector, int paramInt) {
    for (int i = paramInt; i < Math.min(paramIntVector.size(), this.m); i++)
      steplsqr(paramPaceMatrix, paramIntVector, i, mostExplainingColumn(paramPaceMatrix, paramIntVector, i), true); 
  }
  
  public int mostExplainingColumn(PaceMatrix paramPaceMatrix, IntVector paramIntVector, int paramInt) {
    int[] arrayOfInt = paramIntVector.getArray();
    double d = columnResponseExplanation(paramPaceMatrix, paramIntVector, paramInt, paramInt);
    int i = paramInt;
    for (int j = paramInt + 1; j < paramIntVector.size(); j++) {
      double d1 = columnResponseExplanation(paramPaceMatrix, paramIntVector, j, paramInt);
      if (d1 > d) {
        d = d1;
        i = j;
      } 
    } 
    return i;
  }
  
  public void backward(PaceMatrix paramPaceMatrix, IntVector paramIntVector, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i > paramInt2; i--)
      steplsqr(paramPaceMatrix, paramIntVector, i, leastExplainingColumn(paramPaceMatrix, paramIntVector, i, paramInt2), false); 
  }
  
  public int leastExplainingColumn(PaceMatrix paramPaceMatrix, IntVector paramIntVector, int paramInt1, int paramInt2) {
    int[] arrayOfInt = paramIntVector.getArray();
    double d = columnResponseExplanation(paramPaceMatrix, paramIntVector, paramInt1 - 1, paramInt1);
    int i = paramInt1 - 1;
    for (int j = paramInt2; j < paramInt1 - 1; j++) {
      double d1 = columnResponseExplanation(paramPaceMatrix, paramIntVector, j, paramInt1);
      if (d1 <= d) {
        d = d1;
        i = j;
      } 
    } 
    return i;
  }
  
  public double columnResponseExplanation(PaceMatrix paramPaceMatrix, IntVector paramIntVector, int paramInt1, int paramInt2) {
    double d;
    double[] arrayOfDouble = new double[this.n];
    int[] arrayOfInt = paramIntVector.getArray();
    if (paramInt1 == paramInt2 - 1) {
      d = paramPaceMatrix.A[paramInt1][0];
    } else if (paramInt1 > paramInt2 - 1) {
      int i = Math.min(this.n - 1, paramInt1);
      DoubleVector doubleVector1 = getColumn(paramInt2, i, arrayOfInt[paramInt1]);
      DoubleVector doubleVector2 = paramPaceMatrix.getColumn(paramInt2, i, 0);
      d = doubleVector2.innerProduct(doubleVector1) / doubleVector1.norm2();
    } else {
      int i;
      for (i = paramInt1 + 1; i < paramInt2; i++)
        arrayOfDouble[i] = this.A[paramInt1][arrayOfInt[i]]; 
      d = paramPaceMatrix.A[paramInt1][0];
      for (i = paramInt1 + 1; i < paramInt2; i++) {
        double[] arrayOfDouble1 = g1(arrayOfDouble[i], this.A[i][arrayOfInt[i]]);
        for (int j = i + 1; j < paramInt2; j++)
          arrayOfDouble[j] = -arrayOfDouble1[1] * arrayOfDouble[j] + arrayOfDouble1[0] * this.A[i][arrayOfInt[j]]; 
        d = -arrayOfDouble1[1] * d + arrayOfDouble1[0] * paramPaceMatrix.A[i][0];
      } 
    } 
    return d * d;
  }
  
  public void lsqr(PaceMatrix paramPaceMatrix, IntVector paramIntVector, int paramInt) {
    int[] arrayOfInt = paramIntVector.getArray();
    byte b = 0;
    int i;
    for (i = 0; i < paramInt; i++) {
      if (sum2(arrayOfInt[i], b, this.m - 1, true) > 1.0E-15D) {
        steplsqr(paramPaceMatrix, paramIntVector, b, i, true);
        b++;
      } else {
        paramIntVector.shiftToEnd(i);
        paramIntVector.setSize(paramIntVector.size() - 1);
        paramInt--;
        i--;
      } 
    } 
    for (i = paramInt; i < Math.min(paramIntVector.size(), this.m); i++) {
      if (sum2(arrayOfInt[i], b, this.m - 1, true) > 1.0E-15D) {
        steplsqr(paramPaceMatrix, paramIntVector, b, i, true);
        b++;
      } else {
        paramIntVector.shiftToEnd(i);
        paramIntVector.setSize(paramIntVector.size() - 1);
        i--;
      } 
    } 
    this.m = b;
    paramIntVector.setSize(b);
  }
  
  public void lsqrSelection(PaceMatrix paramPaceMatrix, IntVector paramIntVector, int paramInt) {
    int i = this.m;
    int j = paramIntVector.size();
    lsqr(paramPaceMatrix, paramIntVector, paramInt);
    if (j > 200 || j > i)
      forward(paramPaceMatrix, paramIntVector, paramInt); 
    backward(paramPaceMatrix, paramIntVector, paramIntVector.size(), paramInt);
  }
  
  public void positiveDiagonal(PaceMatrix paramPaceMatrix, IntVector paramIntVector) {
    int[] arrayOfInt = paramIntVector.getArray();
    for (byte b = 0; b < paramIntVector.size(); b++) {
      if (this.A[b][arrayOfInt[b]] < 0.0D) {
        for (byte b1 = b; b1 < paramIntVector.size(); b1++)
          this.A[b][arrayOfInt[b1]] = -this.A[b][arrayOfInt[b1]]; 
        paramPaceMatrix.A[b][0] = -paramPaceMatrix.A[b][0];
      } 
    } 
  }
  
  public void steplsqr(PaceMatrix paramPaceMatrix, IntVector paramIntVector, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramIntVector.size();
    int[] arrayOfInt = paramIntVector.getArray();
    if (paramBoolean) {
      int j = arrayOfInt[paramInt2];
      paramIntVector.swap(paramInt1, paramInt2);
      double[] arrayOfDouble = h1(j, paramInt1);
      int k;
      for (k = paramInt1 + 1; k < i; k++) {
        int m = arrayOfInt[k];
        h2(j, paramInt1, arrayOfDouble[1], this, m);
      } 
      h2(j, paramInt1, arrayOfDouble[1], paramPaceMatrix, 0);
      this.A[paramInt1][j] = arrayOfDouble[0];
      for (k = paramInt1 + 1; k < this.m; k++)
        this.A[k][j] = 0.0D; 
    } else {
      int j = arrayOfInt[paramInt2];
      for (int k = paramInt2; k < paramInt1 - 1; k++)
        arrayOfInt[k] = arrayOfInt[k + 1]; 
      arrayOfInt[paramInt1 - 1] = j;
      for (int m = paramInt2; m < paramInt1 - 1; m++) {
        double[] arrayOfDouble = g1(this.A[m][arrayOfInt[m]], this.A[m + 1][arrayOfInt[m]]);
        int n;
        for (n = m; n < i; n++)
          g2(arrayOfDouble, m, m + 1, arrayOfInt[n]); 
        for (n = 0; n < paramPaceMatrix.n; n++)
          paramPaceMatrix.g2(arrayOfDouble, m, m + 1, n); 
      } 
    } 
  }
  
  public void rsolve(PaceMatrix paramPaceMatrix, IntVector paramIntVector, int paramInt) {
    if (paramInt == 0)
      paramPaceMatrix.m = 0; 
    int[] arrayOfInt = paramIntVector.getArray();
    double[][] arrayOfDouble = paramPaceMatrix.getArray();
    for (byte b = 0; b < paramPaceMatrix.n; b++) {
      arrayOfDouble[paramInt - 1][b] = arrayOfDouble[paramInt - 1][b] / this.A[paramInt - 1][arrayOfInt[paramInt - 1]];
      for (int i = paramInt - 2; i >= 0; i--) {
        double d = 0.0D;
        for (int j = i + 1; j < paramInt; j++)
          d += this.A[i][arrayOfInt[j]] * arrayOfDouble[j][b]; 
        arrayOfDouble[i][b] = arrayOfDouble[i][b] - d;
        arrayOfDouble[i][b] = arrayOfDouble[i][b] / this.A[i][arrayOfInt[i]];
      } 
    } 
    paramPaceMatrix.m = paramInt;
  }
  
  public PaceMatrix rbind(PaceMatrix paramPaceMatrix) {
    if (this.n != paramPaceMatrix.n)
      throw new IllegalArgumentException("unequal numbers of rows."); 
    PaceMatrix paceMatrix = new PaceMatrix(this.m + paramPaceMatrix.m, this.n);
    paceMatrix.setMatrix(0, this.m - 1, 0, this.n - 1, this);
    paceMatrix.setMatrix(this.m, this.m + paramPaceMatrix.m - 1, 0, this.n - 1, paramPaceMatrix);
    return paceMatrix;
  }
  
  public PaceMatrix cbind(PaceMatrix paramPaceMatrix) {
    if (this.m != paramPaceMatrix.m)
      throw new IllegalArgumentException("unequal numbers of rows: " + this.m + " and " + paramPaceMatrix.m); 
    PaceMatrix paceMatrix = new PaceMatrix(this.m, this.n + paramPaceMatrix.n);
    paceMatrix.setMatrix(0, this.m - 1, 0, this.n - 1, this);
    paceMatrix.setMatrix(0, this.m - 1, this.n, this.n + paramPaceMatrix.n - 1, paramPaceMatrix);
    return paceMatrix;
  }
  
  public DoubleVector nnls(PaceMatrix paramPaceMatrix, IntVector paramIntVector) {
    byte b1 = 0;
    int i = -1;
    int j = paramIntVector.size();
    int[] arrayOfInt = paramIntVector.getArray();
    DoubleVector doubleVector = new DoubleVector(j);
    double[] arrayOfDouble = doubleVector.getArray();
    PaceMatrix paceMatrix = new PaceMatrix(j, 1);
    byte b2 = 0;
    while (true) {
      if (++b1 > 3 * j)
        throw new RuntimeException("Does not converge"); 
      int m = -1;
      double d2 = 0.0D;
      PaceMatrix paceMatrix1 = new PaceMatrix(paramPaceMatrix.transpose());
      int k;
      for (k = b2; k <= j - 1; k++) {
        double d = paceMatrix1.times(0, b2, this.m - 1, this, arrayOfInt[k]);
        if (d > d2) {
          d2 = d;
          m = k;
        } 
      } 
      if (m == -1) {
        doubleVector.setSize(b2);
        paramIntVector.setSize(b2);
        return doubleVector;
      } 
      paramIntVector.swap(b2, m);
      arrayOfDouble[++b2 - 1] = 0.0D;
      steplsqr(paramPaceMatrix, paramIntVector, b2 - 1, b2 - 1, true);
      double d1 = 0.0D;
      while (d1 < 1.5D) {
        for (k = 0; k <= b2 - 1; k++)
          paceMatrix.A[k][0] = paramPaceMatrix.A[k][0]; 
        rsolve(paceMatrix, paramIntVector, b2);
        d1 = 2.0D;
        i = -1;
        for (k = 0; k <= b2 - 1; k++) {
          if (paceMatrix.A[k][0] <= 0.0D) {
            double d = arrayOfDouble[k] / (arrayOfDouble[k] - paceMatrix.A[k][0]);
            if (d < d1) {
              d1 = d;
              i = k;
            } 
          } 
        } 
        if (d1 > 1.5D) {
          for (k = 0; k <= b2 - 1; k++)
            arrayOfDouble[k] = paceMatrix.A[k][0]; 
          continue;
        } 
        for (k = b2 - 1; k >= 0; k--) {
          if (k == i) {
            arrayOfDouble[k] = 0.0D;
            steplsqr(paramPaceMatrix, paramIntVector, b2, k, false);
            b2--;
          } else {
            arrayOfDouble[k] = arrayOfDouble[k] + d1 * (paceMatrix.A[k][0] - arrayOfDouble[k]);
          } 
        } 
      } 
    } 
  }
  
  public DoubleVector nnlse(PaceMatrix paramPaceMatrix1, PaceMatrix paramPaceMatrix2, PaceMatrix paramPaceMatrix3, IntVector paramIntVector) {
    double d = 1.0E-10D * Math.max(paramPaceMatrix2.maxAbs(), paramPaceMatrix3.maxAbs()) / Math.max(maxAbs(), paramPaceMatrix1.maxAbs());
    PaceMatrix paceMatrix1 = paramPaceMatrix2.rbind(new PaceMatrix(times(d)));
    PaceMatrix paceMatrix2 = paramPaceMatrix3.rbind(new PaceMatrix(paramPaceMatrix1.times(d)));
    return paceMatrix1.nnls(paceMatrix2, paramIntVector);
  }
  
  public DoubleVector nnlse1(PaceMatrix paramPaceMatrix, IntVector paramIntVector) {
    PaceMatrix paceMatrix1 = new PaceMatrix(1, this.n, 1.0D);
    PaceMatrix paceMatrix2 = new PaceMatrix(1, paramPaceMatrix.n, 1.0D);
    return nnlse(paramPaceMatrix, paceMatrix1, paceMatrix2, paramIntVector);
  }
  
  public static Matrix randomNormal(int paramInt1, int paramInt2) {
    Random random = new Random();
    Matrix matrix = new Matrix(paramInt1, paramInt2);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < paramInt1; b++) {
      for (byte b1 = 0; b1 < paramInt2; b1++)
        arrayOfDouble[b][b1] = random.nextGaussian(); 
    } 
    return matrix;
  }
  
  public static void main(String[] paramArrayOfString) {
    System.out.println("===========================================================");
    System.out.println("To test the pace estimators of linear model\ncoefficients.\n");
    double d1 = 2.0D;
    char c = 'È';
    double d2 = 100.0D;
    byte b1 = 20;
    double d3 = 0.0D;
    byte b2 = 20;
    double d4 = 5.0D;
    int i = 1 + b1 + b2;
    DoubleVector doubleVector1 = new DoubleVector(1 + b1 + b2);
    doubleVector1.set(0, d2);
    doubleVector1.set(1, b1, d3);
    doubleVector1.set(b1 + 1, b1 + b2, d4);
    System.out.println("The data set contains " + c + " observations plus " + (b1 + b2) + " variables.\n\nThe coefficients of the true model" + " are:\n\n" + doubleVector1);
    System.out.println("\nThe standard deviation of the error term is " + d1);
    System.out.println("===========================================================");
    PaceMatrix paceMatrix1 = new PaceMatrix(c, b1 + b2 + 1);
    paceMatrix1.setMatrix(0, c - 1, 0, 0, 1.0D);
    paceMatrix1.setMatrix(0, c - 1, 1, b1 + b2, random(c, b1 + b2));
    PaceMatrix paceMatrix2 = new PaceMatrix(paceMatrix1.times(new PaceMatrix(doubleVector1)).plusEquals(randomNormal(c, 1).times(d1)));
    IntVector intVector = IntVector.seq(0, b1 + b2);
    paceMatrix1.lsqrSelection(paceMatrix2, intVector, 1);
    paceMatrix1.positiveDiagonal(paceMatrix2, intVector);
    PaceMatrix paceMatrix3 = (PaceMatrix)paceMatrix2.clone();
    paceMatrix1.rsolve(paceMatrix3, intVector, intVector.size());
    DoubleVector doubleVector2 = paceMatrix3.getColumn(0).unpivoting(intVector, i);
    System.out.println("\nThe OLS estimate (through lsqr()) is: \n\n" + doubleVector2);
    System.out.println("\nQuadratic loss of the OLS estimate (||X b - X bHat||^2) = " + (new PaceMatrix(paceMatrix1.times(new PaceMatrix(doubleVector1.minus(doubleVector2))))).getColumn(0).sum2());
    System.out.println("===========================================================");
    System.out.println("             *** Pace estimation *** \n");
    DoubleVector doubleVector3 = paceMatrix2.getColumn(intVector.size(), c - 1, 0);
    double d5 = Math.sqrt(doubleVector3.sum2() / doubleVector3.size());
    System.out.println("Estimated standard deviation = " + d5);
    DoubleVector doubleVector4 = paceMatrix2.getColumn(0, intVector.size() - 1, 0).times(1.0D / d5);
    System.out.println("\naHat = \n" + doubleVector4);
    System.out.println("\n========= Based on chi-square mixture ============");
    ChisqMixture chisqMixture = new ChisqMixture();
    boolean bool = true;
    DoubleVector doubleVector5 = doubleVector4.square();
    chisqMixture.fit(doubleVector5, bool);
    System.out.println("\nEstimated mixing distribution is:\n" + chisqMixture);
    DoubleVector doubleVector6 = chisqMixture.pace2(doubleVector5);
    DoubleVector doubleVector7 = doubleVector6.sqrt().times(doubleVector4.sign());
    PaceMatrix paceMatrix4 = new PaceMatrix((new PaceMatrix(doubleVector7)).times(d5));
    paceMatrix1.rsolve(paceMatrix4, intVector, intVector.size());
    DoubleVector doubleVector8 = paceMatrix4.getColumn(0).unpivoting(intVector, i);
    System.out.println("\nThe pace2 estimate of coefficients = \n" + doubleVector8);
    System.out.println("Quadratic loss = " + (new PaceMatrix(paceMatrix1.times(new PaceMatrix(doubleVector1.minus(doubleVector8))))).getColumn(0).sum2());
    doubleVector6 = chisqMixture.pace4(doubleVector5);
    doubleVector7 = doubleVector6.sqrt().times(doubleVector4.sign());
    paceMatrix4 = new PaceMatrix((new PaceMatrix(doubleVector7)).times(d5));
    paceMatrix1.rsolve(paceMatrix4, intVector, intVector.size());
    doubleVector8 = paceMatrix4.getColumn(0).unpivoting(intVector, i);
    System.out.println("\nThe pace4 estimate of coefficients = \n" + doubleVector8);
    System.out.println("Quadratic loss = " + (new PaceMatrix(paceMatrix1.times(new PaceMatrix(doubleVector1.minus(doubleVector8))))).getColumn(0).sum2());
    doubleVector6 = chisqMixture.pace6(doubleVector5);
    doubleVector7 = doubleVector6.sqrt().times(doubleVector4.sign());
    paceMatrix4 = new PaceMatrix((new PaceMatrix(doubleVector7)).times(d5));
    paceMatrix1.rsolve(paceMatrix4, intVector, intVector.size());
    doubleVector8 = paceMatrix4.getColumn(0).unpivoting(intVector, i);
    System.out.println("\nThe pace6 estimate of coefficients = \n" + doubleVector8);
    System.out.println("Quadratic loss = " + (new PaceMatrix(paceMatrix1.times(new PaceMatrix(doubleVector1.minus(doubleVector8))))).getColumn(0).sum2());
    System.out.println("\n========= Based on normal mixture ============");
    NormalMixture normalMixture = new NormalMixture();
    normalMixture.fit(doubleVector4, bool);
    System.out.println("\nEstimated mixing distribution is:\n" + normalMixture);
    doubleVector7 = normalMixture.nestedEstimate(doubleVector4);
    paceMatrix4 = new PaceMatrix((new PaceMatrix(doubleVector7)).times(d5));
    paceMatrix1.rsolve(paceMatrix4, intVector, intVector.size());
    doubleVector8 = paceMatrix4.getColumn(0).unpivoting(intVector, i);
    System.out.println("The nested estimate of coefficients = \n" + doubleVector8);
    System.out.println("Quadratic loss = " + (new PaceMatrix(paceMatrix1.times(new PaceMatrix(doubleVector1.minus(doubleVector8))))).getColumn(0).sum2());
    doubleVector7 = normalMixture.subsetEstimate(doubleVector4);
    paceMatrix4 = new PaceMatrix((new PaceMatrix(doubleVector7)).times(d5));
    paceMatrix1.rsolve(paceMatrix4, intVector, intVector.size());
    doubleVector8 = paceMatrix4.getColumn(0).unpivoting(intVector, i);
    System.out.println("\nThe subset estimate of coefficients = \n" + doubleVector8);
    System.out.println("Quadratic loss = " + (new PaceMatrix(paceMatrix1.times(new PaceMatrix(doubleVector1.minus(doubleVector8))))).getColumn(0).sum2());
    doubleVector7 = normalMixture.empiricalBayesEstimate(doubleVector4);
    paceMatrix4 = new PaceMatrix((new PaceMatrix(doubleVector7)).times(d5));
    paceMatrix1.rsolve(paceMatrix4, intVector, intVector.size());
    doubleVector8 = paceMatrix4.getColumn(0).unpivoting(intVector, i);
    System.out.println("\nThe empirical Bayes estimate of coefficients = \n" + doubleVector8);
    System.out.println("Quadratic loss = " + (new PaceMatrix(paceMatrix1.times(new PaceMatrix(doubleVector1.minus(doubleVector8))))).getColumn(0).sum2());
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\PaceMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */