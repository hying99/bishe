package weka.core;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.StringTokenizer;

public class Matrix implements Cloneable, Serializable {
  protected double[][] m_Elements;
  
  public Matrix(int paramInt1, int paramInt2) {
    this.m_Elements = new double[paramInt1][paramInt2];
    initialize();
  }
  
  public Matrix(double[][] paramArrayOfdouble) throws Exception {
    this.m_Elements = new double[paramArrayOfdouble.length][(paramArrayOfdouble[0]).length];
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      for (byte b1 = 0; b1 < (paramArrayOfdouble[0]).length; b1++)
        this.m_Elements[b][b1] = paramArrayOfdouble[b][b1]; 
    } 
  }
  
  public Matrix(Reader paramReader) throws Exception {
    LineNumberReader lineNumberReader = new LineNumberReader(paramReader);
    String str;
    byte b;
    for (b = -1; (str = lineNumberReader.readLine()) != null; b++) {
      if (str.startsWith("%"))
        continue; 
      StringTokenizer stringTokenizer = new StringTokenizer(str);
      if (!stringTokenizer.hasMoreTokens())
        continue; 
      if (b < 0) {
        int i = Integer.parseInt(stringTokenizer.nextToken());
        if (!stringTokenizer.hasMoreTokens())
          throw new Exception("Line " + lineNumberReader.getLineNumber() + ": expected number of columns"); 
        int j = Integer.parseInt(stringTokenizer.nextToken());
        this.m_Elements = new double[i][j];
        initialize();
        b++;
        continue;
      } 
      if (b == numRows())
        throw new Exception("Line " + lineNumberReader.getLineNumber() + ": too many rows provided"); 
      for (byte b1 = 0; b1 < numColumns(); b1++) {
        if (!stringTokenizer.hasMoreTokens())
          throw new Exception("Line " + lineNumberReader.getLineNumber() + ": too few matrix elements provided"); 
        this.m_Elements[b][b1] = Double.valueOf(stringTokenizer.nextToken()).doubleValue();
      } 
    } 
    if (b == -1)
      throw new Exception("Line " + lineNumberReader.getLineNumber() + ": expected number of rows"); 
    if (b != numRows())
      throw new Exception("Line " + lineNumberReader.getLineNumber() + ": too few rows provided"); 
  }
  
  public Object clone() throws CloneNotSupportedException {
    Matrix matrix = (Matrix)super.clone();
    matrix.m_Elements = new double[numRows()][numColumns()];
    for (byte b = 0; b < numRows(); b++) {
      for (byte b1 = 0; b1 < numColumns(); b1++)
        matrix.m_Elements[b][b1] = this.m_Elements[b][b1]; 
    } 
    return matrix;
  }
  
  public void write(Writer paramWriter) throws Exception {
    paramWriter.write("% Rows\tColumns\n");
    paramWriter.write("" + numRows() + "\t" + numColumns() + "\n");
    paramWriter.write("% Matrix elements\n");
    for (byte b = 0; b < numRows(); b++) {
      for (byte b1 = 0; b1 < numColumns(); b1++)
        paramWriter.write("" + this.m_Elements[b][b1] + "\t"); 
      paramWriter.write("\n");
    } 
    paramWriter.flush();
  }
  
  protected void initialize() {
    for (byte b = 0; b < numRows(); b++) {
      for (byte b1 = 0; b1 < numColumns(); b1++)
        this.m_Elements[b][b1] = 0.0D; 
    } 
  }
  
  public final double getElement(int paramInt1, int paramInt2) {
    return this.m_Elements[paramInt1][paramInt2];
  }
  
  public final void addElement(int paramInt1, int paramInt2, double paramDouble) {
    this.m_Elements[paramInt1][paramInt2] = this.m_Elements[paramInt1][paramInt2] + paramDouble;
  }
  
  public final int numRows() {
    return this.m_Elements.length;
  }
  
  public final int numColumns() {
    return (this.m_Elements[0]).length;
  }
  
  public final void setElement(int paramInt1, int paramInt2, double paramDouble) {
    this.m_Elements[paramInt1][paramInt2] = paramDouble;
  }
  
  public final void setRow(int paramInt, double[] paramArrayOfdouble) {
    for (byte b = 0; b < paramArrayOfdouble.length; b++)
      this.m_Elements[paramInt][b] = paramArrayOfdouble[b]; 
  }
  
  public double[] getRow(int paramInt) {
    double[] arrayOfDouble = new double[numColumns()];
    for (byte b = 0; b < arrayOfDouble.length; b++)
      arrayOfDouble[b] = this.m_Elements[paramInt][b]; 
    return arrayOfDouble;
  }
  
  public double[] getColumn(int paramInt) {
    double[] arrayOfDouble = new double[numRows()];
    for (byte b = 0; b < arrayOfDouble.length; b++)
      arrayOfDouble[b] = this.m_Elements[b][paramInt]; 
    return arrayOfDouble;
  }
  
  public final void setColumn(int paramInt, double[] paramArrayOfdouble) {
    for (byte b = 0; b < this.m_Elements.length; b++)
      this.m_Elements[b][paramInt] = paramArrayOfdouble[b]; 
  }
  
  public String toString() {
    double d = 0.0D;
    boolean bool = false;
    int i;
    for (i = 0; i < this.m_Elements.length; i++) {
      for (byte b1 = 0; b1 < (this.m_Elements[i]).length; b1++) {
        double d1 = this.m_Elements[i][b1];
        if (d1 < 0.0D)
          d1 *= -10.0D; 
        if (d1 > d)
          d = d1; 
        double d2 = d1 - Math.rint(d1);
        if (!bool && Math.log(d2) / Math.log(10.0D) >= -2.0D)
          bool = true; 
      } 
    } 
    i = (int)(Math.log(d) / Math.log(10.0D) + (bool ? 4 : true));
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.m_Elements.length; b++) {
      for (byte b1 = 0; b1 < (this.m_Elements[b]).length; b1++)
        stringBuffer.append(" ").append(Utils.doubleToString(this.m_Elements[b][b1], i, bool ? 2 : 0)); 
      stringBuffer.append("\n");
    } 
    return stringBuffer.toString();
  }
  
  public final Matrix add(Matrix paramMatrix) {
    Matrix matrix;
    int i = this.m_Elements.length;
    int j = (this.m_Elements[0]).length;
    try {
      matrix = (Matrix)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      matrix = new Matrix(i, j);
    } 
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++)
        matrix.m_Elements[b][b1] = this.m_Elements[b1][b] + paramMatrix.m_Elements[b1][b]; 
    } 
    return matrix;
  }
  
  public final Matrix transpose() {
    int i = this.m_Elements.length;
    int j = (this.m_Elements[0]).length;
    Matrix matrix = new Matrix(j, i);
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++)
        matrix.m_Elements[b][b1] = this.m_Elements[b1][b]; 
    } 
    return matrix;
  }
  
  public boolean isSymmetric() {
    int i = this.m_Elements.length;
    int j = (this.m_Elements[0]).length;
    if (i != j)
      return false; 
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < b; b1++) {
        if (this.m_Elements[b][b1] != this.m_Elements[b1][b])
          return false; 
      } 
    } 
    return true;
  }
  
  public final Matrix multiply(Matrix paramMatrix) {
    int i = this.m_Elements.length;
    int j = (this.m_Elements[0]).length;
    int k = paramMatrix.m_Elements.length;
    int m = (paramMatrix.m_Elements[0]).length;
    Matrix matrix = new Matrix(i, m);
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < m; b1++) {
        for (byte b2 = 0; b2 < j; b2++)
          matrix.m_Elements[b][b1] = matrix.m_Elements[b][b1] + this.m_Elements[b][b2] * paramMatrix.m_Elements[b2][b1]; 
      } 
    } 
    return matrix;
  }
  
  public final double[] regression(Matrix paramMatrix, double paramDouble) {
    if (paramMatrix.numColumns() > 1)
      throw new IllegalArgumentException("Only one dependent variable allowed"); 
    int i = (this.m_Elements[0]).length;
    double[] arrayOfDouble = new double[i];
    Matrix matrix = transpose();
    boolean bool = true;
    while (true) {
      Matrix matrix1 = matrix.multiply(this);
      for (byte b1 = 0; b1 < i; b1++)
        matrix1.setElement(b1, b1, matrix1.getElement(b1, b1) + paramDouble); 
      Matrix matrix2 = matrix.multiply(paramMatrix);
      for (byte b2 = 0; b2 < i; b2++)
        arrayOfDouble[b2] = matrix2.m_Elements[b2][0]; 
      try {
        matrix1.solve(arrayOfDouble);
        bool = true;
      } catch (Exception exception) {
        paramDouble *= 10.0D;
        bool = false;
      } 
      if (bool)
        return arrayOfDouble; 
    } 
  }
  
  public final double[] regression(Matrix paramMatrix, double[] paramArrayOfdouble, double paramDouble) {
    if (paramArrayOfdouble.length != numRows())
      throw new IllegalArgumentException("Incorrect number of weights provided"); 
    Matrix matrix1 = new Matrix(numRows(), numColumns());
    Matrix matrix2 = new Matrix(numRows(), 1);
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      double d = Math.sqrt(paramArrayOfdouble[b]);
      for (byte b1 = 0; b1 < numColumns(); b1++)
        matrix1.setElement(b, b1, getElement(b, b1) * d); 
      matrix2.setElement(b, 0, paramMatrix.getElement(b, 0) * d);
    } 
    return matrix1.regression(matrix2, paramDouble);
  }
  
  public Matrix getL() throws Exception {
    int i = this.m_Elements.length;
    int j = (this.m_Elements[0]).length;
    double[][] arrayOfDouble = new double[i][j];
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < b && b1 < j; b1++)
        arrayOfDouble[b][b1] = this.m_Elements[b][b1]; 
      if (b < j)
        arrayOfDouble[b][b] = 1.0D; 
    } 
    return new Matrix(arrayOfDouble);
  }
  
  public Matrix getU() throws Exception {
    int i = this.m_Elements.length;
    int j = (this.m_Elements[0]).length;
    double[][] arrayOfDouble = new double[i][j];
    for (byte b = 0; b < i; b++) {
      for (byte b1 = b; b1 < j; b1++)
        arrayOfDouble[b][b1] = this.m_Elements[b][b1]; 
    } 
    return new Matrix(arrayOfDouble);
  }
  
  public int[] LUDecomposition() throws Exception {
    int i = this.m_Elements.length;
    int j = (this.m_Elements[0]).length;
    int[] arrayOfInt = new int[i];
    double[] arrayOfDouble = new double[i];
    byte b;
    for (b = 0; b < i; b++) {
      double d = Math.abs(this.m_Elements[b][0]);
      for (byte b1 = 1; b1 < j; b1++) {
        double d1;
        if ((d1 = Math.abs(this.m_Elements[b][b1])) > d)
          d = d1; 
      } 
      if (d < 1.0E-9D)
        throw new Exception("Matrix is singular!"); 
      arrayOfDouble[b] = 1.0D / d;
    } 
    for (b = 1; b < i; b++)
      arrayOfInt[b] = b; 
    for (b = 0; b < j; b++) {
      int k;
      for (k = 0; k <= b && k < i; k++) {
        double d1 = 0.0D;
        for (byte b1 = 0; b1 < k; b1++)
          d1 += this.m_Elements[k][b1] * this.m_Elements[b1][b]; 
        this.m_Elements[k][b] = this.m_Elements[k][b] - d1;
      } 
      for (k = b + 1; k < i; k++) {
        double d1 = 0.0D;
        for (byte b1 = 0; b1 < b; b1++)
          d1 += this.m_Elements[k][b1] * this.m_Elements[b1][b]; 
        this.m_Elements[k][b] = this.m_Elements[k][b] - d1;
      } 
      double d = Math.abs(this.m_Elements[b][b]) * arrayOfDouble[b];
      int m = b;
      int n;
      for (n = b + 1; n < i; n++) {
        if (Math.abs(this.m_Elements[n][b]) * arrayOfDouble[n] > d) {
          m = n;
          d = Math.abs(this.m_Elements[n][b]) * arrayOfDouble[n];
        } 
      } 
      if (m != b) {
        for (n = 0; n < j; n++) {
          double d2 = this.m_Elements[b][n];
          this.m_Elements[b][n] = this.m_Elements[m][n];
          this.m_Elements[m][n] = d2;
        } 
        n = arrayOfInt[b];
        arrayOfInt[b] = arrayOfInt[m];
        arrayOfInt[m] = n;
        double d1 = arrayOfDouble[b];
        arrayOfDouble[b] = arrayOfDouble[m];
        arrayOfDouble[m] = n;
      } 
      if (this.m_Elements[b][b] == 0.0D)
        throw new Exception("Matrix is singular"); 
      for (n = b + 1; n < i; n++)
        this.m_Elements[n][b] = this.m_Elements[n][b] / this.m_Elements[b][b]; 
    } 
    return arrayOfInt;
  }
  
  public void solve(double[] paramArrayOfdouble) throws Exception {
    int i = this.m_Elements.length;
    int j = (this.m_Elements[0]).length;
    double[] arrayOfDouble1 = new double[paramArrayOfdouble.length];
    double[] arrayOfDouble2 = new double[paramArrayOfdouble.length];
    double[] arrayOfDouble3 = new double[paramArrayOfdouble.length];
    int[] arrayOfInt = LUDecomposition();
    int k;
    for (k = 0; k < arrayOfInt.length; k++)
      arrayOfDouble1[k] = paramArrayOfdouble[arrayOfInt[k]]; 
    arrayOfDouble3[0] = arrayOfDouble1[0];
    for (k = 1; k < i; k++) {
      double d = 0.0D;
      for (byte b = 0; b < k; b++)
        d += this.m_Elements[k][b] * arrayOfDouble3[b]; 
      arrayOfDouble3[k] = arrayOfDouble1[k] - d;
    } 
    arrayOfDouble2[j - 1] = arrayOfDouble3[j - 1] / this.m_Elements[j - 1][j - 1];
    for (k = j - 2; k >= 0; k--) {
      double d = 0.0D;
      for (int m = k + 1; m < j; m++)
        d += this.m_Elements[k][m] * arrayOfDouble2[m]; 
      arrayOfDouble2[k] = (arrayOfDouble3[k] - d) / this.m_Elements[k][k];
    } 
    for (k = 0; k < arrayOfInt.length; k++)
      paramArrayOfdouble[k] = arrayOfDouble2[k]; 
  }
  
  public void eigenvalueDecomposition(double[][] paramArrayOfdouble, double[] paramArrayOfdouble1) throws Exception {
    if (!isSymmetric())
      throw new Exception("EigenvalueDecomposition: Matrix must be symmetric."); 
    int i = numRows();
    double[] arrayOfDouble = new double[i];
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < i; b1++)
        paramArrayOfdouble[b][b1] = this.m_Elements[b][b1]; 
    } 
    tred2(paramArrayOfdouble, paramArrayOfdouble1, arrayOfDouble, i);
    tql2(paramArrayOfdouble, paramArrayOfdouble1, arrayOfDouble, i);
  }
  
  private void tred2(double[][] paramArrayOfdouble, double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, int paramInt) throws Exception {
    int i;
    for (i = 0; i < paramInt; i++)
      paramArrayOfdouble1[i] = paramArrayOfdouble[paramInt - 1][i]; 
    for (i = paramInt - 1; i > 0; i--) {
      double d1 = 0.0D;
      double d2 = 0.0D;
      byte b;
      for (b = 0; b < i; b++)
        d1 += Math.abs(paramArrayOfdouble1[b]); 
      if (d1 == 0.0D) {
        paramArrayOfdouble2[i] = paramArrayOfdouble1[i - 1];
        for (b = 0; b < i; b++) {
          paramArrayOfdouble1[b] = paramArrayOfdouble[i - 1][b];
          paramArrayOfdouble[i][b] = 0.0D;
          paramArrayOfdouble[b][i] = 0.0D;
        } 
      } else {
        for (b = 0; b < i; b++) {
          paramArrayOfdouble1[b] = paramArrayOfdouble1[b] / d1;
          d2 += paramArrayOfdouble1[b] * paramArrayOfdouble1[b];
        } 
        double d3 = paramArrayOfdouble1[i - 1];
        double d4 = Math.sqrt(d2);
        if (d3 > 0.0D)
          d4 = -d4; 
        paramArrayOfdouble2[i] = d1 * d4;
        d2 -= d3 * d4;
        paramArrayOfdouble1[i - 1] = d3 - d4;
        byte b1;
        for (b1 = 0; b1 < i; b1++)
          paramArrayOfdouble2[b1] = 0.0D; 
        for (b1 = 0; b1 < i; b1++) {
          d3 = paramArrayOfdouble1[b1];
          paramArrayOfdouble[b1][i] = d3;
          d4 = paramArrayOfdouble2[b1] + paramArrayOfdouble[b1][b1] * d3;
          for (int j = b1 + 1; j <= i - 1; j++) {
            d4 += paramArrayOfdouble[j][b1] * paramArrayOfdouble1[j];
            paramArrayOfdouble2[j] = paramArrayOfdouble2[j] + paramArrayOfdouble[j][b1] * d3;
          } 
          paramArrayOfdouble2[b1] = d4;
        } 
        d3 = 0.0D;
        for (b1 = 0; b1 < i; b1++) {
          paramArrayOfdouble2[b1] = paramArrayOfdouble2[b1] / d2;
          d3 += paramArrayOfdouble2[b1] * paramArrayOfdouble1[b1];
        } 
        double d5 = d3 / (d2 + d2);
        byte b2;
        for (b2 = 0; b2 < i; b2++)
          paramArrayOfdouble2[b2] = paramArrayOfdouble2[b2] - d5 * paramArrayOfdouble1[b2]; 
        for (b2 = 0; b2 < i; b2++) {
          d3 = paramArrayOfdouble1[b2];
          d4 = paramArrayOfdouble2[b2];
          for (byte b3 = b2; b3 <= i - 1; b3++)
            paramArrayOfdouble[b3][b2] = paramArrayOfdouble[b3][b2] - d3 * paramArrayOfdouble2[b3] + d4 * paramArrayOfdouble1[b3]; 
          paramArrayOfdouble1[b2] = paramArrayOfdouble[i - 1][b2];
          paramArrayOfdouble[i][b2] = 0.0D;
        } 
      } 
      paramArrayOfdouble1[i] = d2;
    } 
    for (i = 0; i < paramInt - 1; i++) {
      paramArrayOfdouble[paramInt - 1][i] = paramArrayOfdouble[i][i];
      paramArrayOfdouble[i][i] = 1.0D;
      double d = paramArrayOfdouble1[i + 1];
      if (d != 0.0D) {
        byte b1;
        for (b1 = 0; b1 <= i; b1++)
          paramArrayOfdouble1[b1] = paramArrayOfdouble[b1][i + 1] / d; 
        for (b1 = 0; b1 <= i; b1++) {
          double d1 = 0.0D;
          byte b2;
          for (b2 = 0; b2 <= i; b2++)
            d1 += paramArrayOfdouble[b2][i + 1] * paramArrayOfdouble[b2][b1]; 
          for (b2 = 0; b2 <= i; b2++)
            paramArrayOfdouble[b2][b1] = paramArrayOfdouble[b2][b1] - d1 * paramArrayOfdouble1[b2]; 
        } 
      } 
      for (byte b = 0; b <= i; b++)
        paramArrayOfdouble[b][i + 1] = 0.0D; 
    } 
    for (i = 0; i < paramInt; i++) {
      paramArrayOfdouble1[i] = paramArrayOfdouble[paramInt - 1][i];
      paramArrayOfdouble[paramInt - 1][i] = 0.0D;
    } 
    paramArrayOfdouble[paramInt - 1][paramInt - 1] = 1.0D;
    paramArrayOfdouble2[0] = 0.0D;
  }
  
  private void tql2(double[][] paramArrayOfdouble, double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, int paramInt) throws Exception {
    for (byte b1 = 1; b1 < paramInt; b1++)
      paramArrayOfdouble2[b1 - 1] = paramArrayOfdouble2[b1]; 
    paramArrayOfdouble2[paramInt - 1] = 0.0D;
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = Math.pow(2.0D, -52.0D);
    for (byte b2 = 0; b2 < paramInt; b2++) {
      d2 = Math.max(d2, Math.abs(paramArrayOfdouble1[b2]) + Math.abs(paramArrayOfdouble2[b2]));
      byte b;
      for (b = b2; b < paramInt && Math.abs(paramArrayOfdouble2[b]) > d3 * d2; b++);
      if (b > b2) {
        int i = 0;
        do {
          i++;
          double d4 = paramArrayOfdouble1[b2];
          double d5 = (paramArrayOfdouble1[b2 + 1] - d4) / 2.0D * paramArrayOfdouble2[b2];
          double d6 = hypot(d5, 1.0D);
          if (d5 < 0.0D)
            d6 = -d6; 
          paramArrayOfdouble1[b2] = paramArrayOfdouble2[b2] / (d5 + d6);
          paramArrayOfdouble1[b2 + 1] = paramArrayOfdouble2[b2] * (d5 + d6);
          double d7 = paramArrayOfdouble1[b2 + 1];
          double d8 = d4 - paramArrayOfdouble1[b2];
          for (int j = b2 + 2; j < paramInt; j++)
            paramArrayOfdouble1[j] = paramArrayOfdouble1[j] - d8; 
          d1 += d8;
          d5 = paramArrayOfdouble1[b];
          double d9 = 1.0D;
          double d10 = d9;
          double d11 = d9;
          double d12 = paramArrayOfdouble2[b2 + 1];
          double d13 = 0.0D;
          double d14 = 0.0D;
          for (int k = b - 1; k >= b2; k--) {
            d11 = d10;
            d10 = d9;
            d14 = d13;
            d4 = d9 * paramArrayOfdouble2[k];
            d8 = d9 * d5;
            d6 = hypot(d5, paramArrayOfdouble2[k]);
            paramArrayOfdouble2[k + 1] = d13 * d6;
            d13 = paramArrayOfdouble2[k] / d6;
            d9 = d5 / d6;
            d5 = d9 * paramArrayOfdouble1[k] - d13 * d4;
            paramArrayOfdouble1[k + 1] = d8 + d13 * (d9 * d4 + d13 * paramArrayOfdouble1[k]);
            for (byte b3 = 0; b3 < paramInt; b3++) {
              d8 = paramArrayOfdouble[b3][k + 1];
              paramArrayOfdouble[b3][k + 1] = d13 * paramArrayOfdouble[b3][k] + d9 * d8;
              paramArrayOfdouble[b3][k] = d9 * paramArrayOfdouble[b3][k] - d13 * d8;
            } 
          } 
          d5 = -d13 * d14 * d11 * d12 * paramArrayOfdouble2[b2] / d7;
          paramArrayOfdouble2[b2] = d13 * d5;
          paramArrayOfdouble1[b2] = d9 * d5;
        } while (Math.abs(paramArrayOfdouble2[b2]) > d3 * d2);
      } 
      paramArrayOfdouble1[b2] = paramArrayOfdouble1[b2] + d1;
      paramArrayOfdouble2[b2] = 0.0D;
    } 
  }
  
  protected static double hypot(double paramDouble1, double paramDouble2) {
    double d;
    if (Math.abs(paramDouble1) > Math.abs(paramDouble2)) {
      d = paramDouble2 / paramDouble1;
      d = Math.abs(paramDouble1) * Math.sqrt(1.0D + d * d);
    } else if (paramDouble2 != 0.0D) {
      d = paramDouble1 / paramDouble2;
      d = Math.abs(paramDouble2) * Math.sqrt(1.0D + d * d);
    } else {
      d = 0.0D;
    } 
    return d;
  }
  
  public boolean testEigen(Matrix paramMatrix, double[] paramArrayOfdouble, boolean paramBoolean) throws Exception {
    boolean bool = true;
    if (paramBoolean) {
      System.out.println("--- test Eigenvectors and Eigenvalues of Matrix A --------");
      System.out.println("Matrix A \n" + this);
      System.out.println("Matrix V, the columns are the Eigenvectors\n" + paramMatrix);
      System.out.println("the Eigenvalues are");
      for (byte b1 = 0; b1 < paramArrayOfdouble.length; b1++)
        System.out.println(Utils.doubleToString(paramArrayOfdouble[b1], 2)); 
      System.out.println("\n---");
    } 
    double[][] arrayOfDouble = new double[paramMatrix.numRows()][1];
    Matrix matrix = new Matrix(arrayOfDouble);
    for (byte b = 0; b < paramMatrix.numRows(); b++) {
      double[] arrayOfDouble1 = paramMatrix.getColumn(b);
      double d = 0.0D;
      for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
        d += Math.pow(arrayOfDouble1[b1], 2.0D); 
      d = Math.pow(d, 0.5D);
      matrix.setColumn(0, paramMatrix.getColumn(b));
      if (paramBoolean)
        System.out.println("Eigenvektor " + b + " =\n" + matrix + "\nNorm " + d); 
      Matrix matrix1 = multiply(matrix);
      if (paramBoolean) {
        System.out.println("this x Eigenvektor " + b + " =\n");
        for (byte b3 = 0; b3 < paramMatrix.numRows(); b3++)
          System.out.print(Utils.doubleToString(matrix1.getElement(b3, 0), 2) + "  "); 
        System.out.println(" ");
        System.out.println(" ");
        System.out.println("Eigenvektor " + b + " x Eigenvalue " + Utils.doubleToString(paramArrayOfdouble[b], 2) + " =");
      } 
      for (byte b2 = 0; b2 < paramMatrix.numRows() && bool; b2++) {
        double d1 = matrix.getElement(b2, 0) * paramArrayOfdouble[b];
        double d2 = d1 - matrix1.getElement(b2, 0);
        bool = (Math.abs(d2) < Utils.SMALL) ? true : false;
        if (Math.abs(d2) > Utils.SMALL)
          System.out.println("OOOOOOps"); 
        if (paramBoolean)
          System.out.print(Utils.doubleToString(d1, 2) + "  "); 
      } 
      if (paramBoolean) {
        System.out.println(" ");
        System.out.println("---");
      } 
    } 
    return bool;
  }
  
  public static void main(String[] paramArrayOfString) {
    double[] arrayOfDouble1 = { 2.3D, 1.2D, 5.0D };
    double[] arrayOfDouble2 = { 5.2D, 1.4D, 9.0D };
    double[] arrayOfDouble3 = { 4.0D, 7.0D, 8.0D };
    double[] arrayOfDouble4 = { 1.0D, 2.0D, 3.0D };
    try {
      double[][] arrayOfDouble5 = { { 1.0D, 2.0D, 3.0D }, { 2.0D, 5.0D, 6.0D }, { 3.0D, 6.0D, 9.0D } };
      Matrix matrix1 = new Matrix(arrayOfDouble5);
      int i = matrix1.numRows();
      double[][] arrayOfDouble6 = new double[i][i];
      double[] arrayOfDouble7 = new double[i];
      double[] arrayOfDouble8 = new double[i];
      matrix1.eigenvalueDecomposition(arrayOfDouble6, arrayOfDouble7);
      Matrix matrix2 = new Matrix(arrayOfDouble6);
      Matrix matrix3 = new Matrix(2, 3);
      Matrix matrix4 = new Matrix(3, 2);
      System.out.println("Number of columns for a: " + matrix3.numColumns());
      System.out.println("Number of rows for a: " + matrix3.numRows());
      matrix3.setRow(0, arrayOfDouble1);
      matrix3.setRow(1, arrayOfDouble2);
      matrix4.setColumn(0, arrayOfDouble1);
      matrix4.setColumn(1, arrayOfDouble2);
      System.out.println("a:\n " + matrix3);
      System.out.println("b:\n " + matrix4);
      System.out.println("a (0, 0): " + matrix3.getElement(0, 0));
      System.out.println("a transposed:\n " + matrix3.transpose());
      System.out.println("a * b:\n " + matrix3.multiply(matrix4));
      Matrix matrix5 = new Matrix(3, 1);
      matrix5.setColumn(0, arrayOfDouble3);
      System.out.println("r:\n " + matrix5);
      System.out.println("Coefficients of regression of b on r: ");
      double[] arrayOfDouble9 = matrix4.regression(matrix5, 1.0E-8D);
      byte b;
      for (b = 0; b < arrayOfDouble9.length; b++)
        System.out.print(arrayOfDouble9[b] + " "); 
      System.out.println();
      System.out.println("Weights: ");
      for (b = 0; b < arrayOfDouble4.length; b++)
        System.out.print(arrayOfDouble4[b] + " "); 
      System.out.println();
      System.out.println("Coefficients of weighted regression of b on r: ");
      arrayOfDouble9 = matrix4.regression(matrix5, arrayOfDouble4, 1.0E-8D);
      for (b = 0; b < arrayOfDouble9.length; b++)
        System.out.print(arrayOfDouble9[b] + " "); 
      System.out.println();
      matrix3.setElement(0, 0, 6.0D);
      System.out.println("a with (0, 0) set to 6:\n " + matrix3);
      matrix3.write(new FileWriter("main.matrix"));
      System.out.println("wrote matrix to \"main.matrix\"\n" + matrix3);
      matrix3 = new Matrix(new FileReader("main.matrix"));
      System.out.println("read matrix from \"main.matrix\"\n" + matrix3);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Matrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */