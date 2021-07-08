package weka.classifiers.functions.pace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Vector;

public class Matrix implements Cloneable, Serializable {
  protected double[][] A;
  
  protected int m;
  
  protected int n;
  
  public Matrix(int paramInt1, int paramInt2) {
    this.m = paramInt1;
    this.n = paramInt2;
    this.A = new double[paramInt1][paramInt2];
  }
  
  public Matrix(int paramInt1, int paramInt2, double paramDouble) {
    this.m = paramInt1;
    this.n = paramInt2;
    this.A = new double[paramInt1][paramInt2];
    for (byte b = 0; b < paramInt1; b++) {
      for (byte b1 = 0; b1 < paramInt2; b1++)
        this.A[b][b1] = paramDouble; 
    } 
  }
  
  public Matrix(double[][] paramArrayOfdouble) {
    this.m = paramArrayOfdouble.length;
    this.n = (paramArrayOfdouble[0]).length;
    for (byte b = 0; b < this.m; b++) {
      if ((paramArrayOfdouble[b]).length != this.n)
        throw new IllegalArgumentException("All rows must have the same length."); 
    } 
    this.A = paramArrayOfdouble;
  }
  
  public Matrix(double[][] paramArrayOfdouble, int paramInt1, int paramInt2) {
    this.A = paramArrayOfdouble;
    this.m = paramInt1;
    this.n = paramInt2;
  }
  
  public Matrix(double[] paramArrayOfdouble, int paramInt) {
    this.m = paramInt;
    this.n = (paramInt != 0) ? (paramArrayOfdouble.length / paramInt) : 0;
    if (paramInt * this.n != paramArrayOfdouble.length)
      throw new IllegalArgumentException("Array length must be a multiple of m."); 
    this.A = new double[paramInt][this.n];
    for (byte b = 0; b < paramInt; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        this.A[b][b1] = paramArrayOfdouble[b + b1 * paramInt]; 
    } 
  }
  
  public static Matrix constructWithCopy(double[][] paramArrayOfdouble) {
    int i = paramArrayOfdouble.length;
    int j = (paramArrayOfdouble[0]).length;
    Matrix matrix = new Matrix(i, j);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < i; b++) {
      if ((paramArrayOfdouble[b]).length != j)
        throw new IllegalArgumentException("All rows must have the same length."); 
      for (byte b1 = 0; b1 < j; b1++)
        arrayOfDouble[b][b1] = paramArrayOfdouble[b][b1]; 
    } 
    return matrix;
  }
  
  public Matrix copy() {
    Matrix matrix = new Matrix(this.m, this.n);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b][b1] = this.A[b][b1]; 
    } 
    return matrix;
  }
  
  public Object clone() {
    return copy();
  }
  
  public double[][] getArray() {
    return this.A;
  }
  
  public double[][] getArrayCopy() {
    double[][] arrayOfDouble = new double[this.m][this.n];
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b][b1] = this.A[b][b1]; 
    } 
    return arrayOfDouble;
  }
  
  public double[] getColumnPackedCopy() {
    double[] arrayOfDouble = new double[this.m * this.n];
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b + b1 * this.m] = this.A[b][b1]; 
    } 
    return arrayOfDouble;
  }
  
  public double[] getRowPackedCopy() {
    double[] arrayOfDouble = new double[this.m * this.n];
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b * this.n + b1] = this.A[b][b1]; 
    } 
    return arrayOfDouble;
  }
  
  public int getRowDimension() {
    return this.m;
  }
  
  public int getColumnDimension() {
    return this.n;
  }
  
  public double get(int paramInt1, int paramInt2) {
    return this.A[paramInt1][paramInt2];
  }
  
  public Matrix getMatrix(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Matrix matrix = new Matrix(paramInt2 - paramInt1 + 1, paramInt4 - paramInt3 + 1);
    double[][] arrayOfDouble = matrix.getArray();
    try {
      for (int i = paramInt1; i <= paramInt2; i++) {
        for (int j = paramInt3; j <= paramInt4; j++)
          arrayOfDouble[i - paramInt1][j - paramInt3] = this.A[i][j]; 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException("Submatrix indices");
    } 
    return matrix;
  }
  
  public Matrix getMatrix(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    Matrix matrix = new Matrix(paramArrayOfint1.length, paramArrayOfint2.length);
    double[][] arrayOfDouble = matrix.getArray();
    try {
      for (byte b = 0; b < paramArrayOfint1.length; b++) {
        for (byte b1 = 0; b1 < paramArrayOfint2.length; b1++)
          arrayOfDouble[b][b1] = this.A[paramArrayOfint1[b]][paramArrayOfint2[b1]]; 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException("Submatrix indices");
    } 
    return matrix;
  }
  
  public Matrix getMatrix(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    Matrix matrix = new Matrix(paramInt2 - paramInt1 + 1, paramArrayOfint.length);
    double[][] arrayOfDouble = matrix.getArray();
    try {
      for (int i = paramInt1; i <= paramInt2; i++) {
        for (byte b = 0; b < paramArrayOfint.length; b++)
          arrayOfDouble[i - paramInt1][b] = this.A[i][paramArrayOfint[b]]; 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException("Submatrix indices");
    } 
    return matrix;
  }
  
  public Matrix getMatrix(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    Matrix matrix = new Matrix(paramArrayOfint.length, paramInt2 - paramInt1 + 1);
    double[][] arrayOfDouble = matrix.getArray();
    try {
      for (byte b = 0; b < paramArrayOfint.length; b++) {
        for (int i = paramInt1; i <= paramInt2; i++)
          arrayOfDouble[b][i - paramInt1] = this.A[paramArrayOfint[b]][i]; 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException("Submatrix indices");
    } 
    return matrix;
  }
  
  public void set(int paramInt1, int paramInt2, double paramDouble) {
    this.A[paramInt1][paramInt2] = paramDouble;
  }
  
  public void setMatrix(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Matrix paramMatrix) {
    try {
      for (int i = paramInt1; i <= paramInt2; i++) {
        for (int j = paramInt3; j <= paramInt4; j++)
          this.A[i][j] = paramMatrix.get(i - paramInt1, j - paramInt3); 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException("Submatrix indices");
    } 
  }
  
  public void setMatrix(int[] paramArrayOfint1, int[] paramArrayOfint2, Matrix paramMatrix) {
    try {
      for (byte b = 0; b < paramArrayOfint1.length; b++) {
        for (byte b1 = 0; b1 < paramArrayOfint2.length; b1++)
          this.A[paramArrayOfint1[b]][paramArrayOfint2[b1]] = paramMatrix.get(b, b1); 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException("Submatrix indices");
    } 
  }
  
  public void setMatrix(int[] paramArrayOfint, int paramInt1, int paramInt2, Matrix paramMatrix) {
    try {
      for (byte b = 0; b < paramArrayOfint.length; b++) {
        for (int i = paramInt1; i <= paramInt2; i++)
          this.A[paramArrayOfint[b]][i] = paramMatrix.get(b, i - paramInt1); 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException("Submatrix indices");
    } 
  }
  
  public void setMatrix(int paramInt1, int paramInt2, int[] paramArrayOfint, Matrix paramMatrix) {
    try {
      for (int i = paramInt1; i <= paramInt2; i++) {
        for (byte b = 0; b < paramArrayOfint.length; b++)
          this.A[i][paramArrayOfint[b]] = paramMatrix.get(i - paramInt1, b); 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException("Submatrix indices");
    } 
  }
  
  public Matrix transpose() {
    Matrix matrix = new Matrix(this.n, this.m);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b1][b] = this.A[b][b1]; 
    } 
    return matrix;
  }
  
  public double norm1() {
    double d = 0.0D;
    for (byte b = 0; b < this.n; b++) {
      double d1 = 0.0D;
      for (byte b1 = 0; b1 < this.m; b1++)
        d1 += Math.abs(this.A[b1][b]); 
      d = Math.max(d, d1);
    } 
    return d;
  }
  
  public double normInf() {
    double d = 0.0D;
    for (byte b = 0; b < this.m; b++) {
      double d1 = 0.0D;
      for (byte b1 = 0; b1 < this.n; b1++)
        d1 += Math.abs(this.A[b][b1]); 
      d = Math.max(d, d1);
    } 
    return d;
  }
  
  public double normF() {
    double d = 0.0D;
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        d = Maths.hypot(d, this.A[b][b1]); 
    } 
    return d;
  }
  
  public Matrix uminus() {
    Matrix matrix = new Matrix(this.m, this.n);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b][b1] = -this.A[b][b1]; 
    } 
    return matrix;
  }
  
  public Matrix plus(Matrix paramMatrix) {
    checkMatrixDimensions(paramMatrix);
    Matrix matrix = new Matrix(this.m, this.n);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b][b1] = this.A[b][b1] + paramMatrix.A[b][b1]; 
    } 
    return matrix;
  }
  
  public Matrix plusEquals(Matrix paramMatrix) {
    checkMatrixDimensions(paramMatrix);
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        this.A[b][b1] = this.A[b][b1] + paramMatrix.A[b][b1]; 
    } 
    return this;
  }
  
  public Matrix minus(Matrix paramMatrix) {
    checkMatrixDimensions(paramMatrix);
    Matrix matrix = new Matrix(this.m, this.n);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b][b1] = this.A[b][b1] - paramMatrix.A[b][b1]; 
    } 
    return matrix;
  }
  
  public Matrix minusEquals(Matrix paramMatrix) {
    checkMatrixDimensions(paramMatrix);
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        this.A[b][b1] = this.A[b][b1] - paramMatrix.A[b][b1]; 
    } 
    return this;
  }
  
  public Matrix arrayTimes(Matrix paramMatrix) {
    checkMatrixDimensions(paramMatrix);
    Matrix matrix = new Matrix(this.m, this.n);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b][b1] = this.A[b][b1] * paramMatrix.A[b][b1]; 
    } 
    return matrix;
  }
  
  public Matrix arrayTimesEquals(Matrix paramMatrix) {
    checkMatrixDimensions(paramMatrix);
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        this.A[b][b1] = this.A[b][b1] * paramMatrix.A[b][b1]; 
    } 
    return this;
  }
  
  public Matrix arrayRightDivide(Matrix paramMatrix) {
    checkMatrixDimensions(paramMatrix);
    Matrix matrix = new Matrix(this.m, this.n);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b][b1] = this.A[b][b1] / paramMatrix.A[b][b1]; 
    } 
    return matrix;
  }
  
  public Matrix arrayRightDivideEquals(Matrix paramMatrix) {
    checkMatrixDimensions(paramMatrix);
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        this.A[b][b1] = this.A[b][b1] / paramMatrix.A[b][b1]; 
    } 
    return this;
  }
  
  public Matrix arrayLeftDivide(Matrix paramMatrix) {
    checkMatrixDimensions(paramMatrix);
    Matrix matrix = new Matrix(this.m, this.n);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b][b1] = paramMatrix.A[b][b1] / this.A[b][b1]; 
    } 
    return matrix;
  }
  
  public Matrix arrayLeftDivideEquals(Matrix paramMatrix) {
    checkMatrixDimensions(paramMatrix);
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        this.A[b][b1] = paramMatrix.A[b][b1] / this.A[b][b1]; 
    } 
    return this;
  }
  
  public Matrix times(double paramDouble) {
    Matrix matrix = new Matrix(this.m, this.n);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        arrayOfDouble[b][b1] = paramDouble * this.A[b][b1]; 
    } 
    return matrix;
  }
  
  public Matrix timesEquals(double paramDouble) {
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++)
        this.A[b][b1] = paramDouble * this.A[b][b1]; 
    } 
    return this;
  }
  
  public Matrix times(Matrix paramMatrix) {
    if (paramMatrix.m != this.n)
      throw new IllegalArgumentException("Matrix inner dimensions must agree."); 
    Matrix matrix = new Matrix(this.m, paramMatrix.n);
    double[][] arrayOfDouble = matrix.getArray();
    double[] arrayOfDouble1 = new double[this.n];
    for (byte b = 0; b < paramMatrix.n; b++) {
      byte b1;
      for (b1 = 0; b1 < this.n; b1++)
        arrayOfDouble1[b1] = paramMatrix.A[b1][b]; 
      for (b1 = 0; b1 < this.m; b1++) {
        double[] arrayOfDouble2 = this.A[b1];
        double d = 0.0D;
        for (byte b2 = 0; b2 < this.n; b2++)
          d += arrayOfDouble2[b2] * arrayOfDouble1[b2]; 
        arrayOfDouble[b1][b] = d;
      } 
    } 
    return matrix;
  }
  
  public double trace() {
    double d = 0.0D;
    for (byte b = 0; b < Math.min(this.m, this.n); b++)
      d += this.A[b][b]; 
    return d;
  }
  
  public static Matrix random(int paramInt1, int paramInt2) {
    Matrix matrix = new Matrix(paramInt1, paramInt2);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < paramInt1; b++) {
      for (byte b1 = 0; b1 < paramInt2; b1++)
        arrayOfDouble[b][b1] = Math.random(); 
    } 
    return matrix;
  }
  
  public static Matrix identity(int paramInt1, int paramInt2) {
    Matrix matrix = new Matrix(paramInt1, paramInt2);
    double[][] arrayOfDouble = matrix.getArray();
    for (byte b = 0; b < paramInt1; b++) {
      for (byte b1 = 0; b1 < paramInt2; b1++)
        arrayOfDouble[b][b1] = (b == b1) ? 1.0D : 0.0D; 
    } 
    return matrix;
  }
  
  public void print(int paramInt1, int paramInt2) {
    print(new PrintWriter(System.out, true), paramInt1, paramInt2);
  }
  
  public void print(PrintWriter paramPrintWriter, int paramInt1, int paramInt2) {
    DecimalFormat decimalFormat = new DecimalFormat();
    decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
    decimalFormat.setMinimumIntegerDigits(1);
    decimalFormat.setMaximumFractionDigits(paramInt2);
    decimalFormat.setMinimumFractionDigits(paramInt2);
    decimalFormat.setGroupingUsed(false);
    print(paramPrintWriter, decimalFormat, paramInt1 + 2);
  }
  
  public void print(NumberFormat paramNumberFormat, int paramInt) {
    print(new PrintWriter(System.out, true), paramNumberFormat, paramInt);
  }
  
  public void print(PrintWriter paramPrintWriter, NumberFormat paramNumberFormat, int paramInt) {
    paramPrintWriter.println();
    for (byte b = 0; b < this.m; b++) {
      for (byte b1 = 0; b1 < this.n; b1++) {
        String str = paramNumberFormat.format(this.A[b][b1]);
        int i = Math.max(1, paramInt - str.length());
        for (byte b2 = 0; b2 < i; b2++)
          paramPrintWriter.print(' '); 
        paramPrintWriter.print(str);
      } 
      paramPrintWriter.println();
    } 
    paramPrintWriter.println();
  }
  
  public static Matrix read(BufferedReader paramBufferedReader) throws IOException {
    StreamTokenizer streamTokenizer = new StreamTokenizer(paramBufferedReader);
    streamTokenizer.resetSyntax();
    streamTokenizer.wordChars(0, 255);
    streamTokenizer.whitespaceChars(0, 32);
    streamTokenizer.eolIsSignificant(true);
    Vector vector = new Vector();
    while (streamTokenizer.nextToken() == 10);
    if (streamTokenizer.ttype == -1)
      throw new IOException("Unexpected EOF on matrix read."); 
    while (true) {
      vector.addElement(Double.valueOf(streamTokenizer.sval));
      if (streamTokenizer.nextToken() != -3) {
        int i = vector.size();
        double[] arrayOfDouble = new double[i];
        int j;
        for (j = 0; j < i; j++)
          arrayOfDouble[j] = ((Double)vector.elementAt(j)).doubleValue(); 
        vector.removeAllElements();
        vector.addElement(arrayOfDouble);
        while (streamTokenizer.nextToken() == -3) {
          vector.addElement(arrayOfDouble = new double[i]);
          j = 0;
          while (true) {
            if (j >= i)
              throw new IOException("Row " + vector.size() + " is too long."); 
            arrayOfDouble[j++] = Double.valueOf(streamTokenizer.sval).doubleValue();
            if (streamTokenizer.nextToken() != -3 && j < i)
              throw new IOException("Row " + vector.size() + " is too short."); 
          } 
        } 
        j = vector.size();
        double[][] arrayOfDouble1 = new double[j][];
        vector.copyInto((Object[])arrayOfDouble1);
        return new Matrix(arrayOfDouble1);
      } 
    } 
  }
  
  private void checkMatrixDimensions(Matrix paramMatrix) {
    if (paramMatrix.m != this.m || paramMatrix.n != this.n)
      throw new IllegalArgumentException("Matrix dimensions must agree."); 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\Matrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */