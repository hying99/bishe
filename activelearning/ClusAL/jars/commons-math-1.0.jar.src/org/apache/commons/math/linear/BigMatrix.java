package org.apache.commons.math.linear;

import java.math.BigDecimal;

public interface BigMatrix {
  BigMatrix copy();
  
  BigMatrix add(BigMatrix paramBigMatrix) throws IllegalArgumentException;
  
  BigMatrix subtract(BigMatrix paramBigMatrix) throws IllegalArgumentException;
  
  BigMatrix scalarAdd(BigDecimal paramBigDecimal);
  
  BigMatrix scalarMultiply(BigDecimal paramBigDecimal);
  
  BigMatrix multiply(BigMatrix paramBigMatrix) throws IllegalArgumentException;
  
  BigMatrix preMultiply(BigMatrix paramBigMatrix) throws IllegalArgumentException;
  
  BigDecimal[][] getData();
  
  double[][] getDataAsDoubleArray();
  
  int getRoundingMode();
  
  BigDecimal getNorm();
  
  BigMatrix getSubMatrix(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws MatrixIndexException;
  
  BigMatrix getSubMatrix(int[] paramArrayOfint1, int[] paramArrayOfint2) throws MatrixIndexException;
  
  BigMatrix getRowMatrix(int paramInt) throws MatrixIndexException;
  
  BigMatrix getColumnMatrix(int paramInt) throws MatrixIndexException;
  
  BigDecimal[] getRow(int paramInt) throws MatrixIndexException;
  
  double[] getRowAsDoubleArray(int paramInt) throws MatrixIndexException;
  
  BigDecimal[] getColumn(int paramInt) throws MatrixIndexException;
  
  double[] getColumnAsDoubleArray(int paramInt) throws MatrixIndexException;
  
  BigDecimal getEntry(int paramInt1, int paramInt2) throws MatrixIndexException;
  
  double getEntryAsDouble(int paramInt1, int paramInt2) throws MatrixIndexException;
  
  BigMatrix transpose();
  
  BigMatrix inverse() throws InvalidMatrixException;
  
  BigDecimal getDeterminant() throws InvalidMatrixException;
  
  boolean isSquare();
  
  boolean isSingular();
  
  int getRowDimension();
  
  int getColumnDimension();
  
  BigDecimal getTrace();
  
  BigDecimal[] operate(BigDecimal[] paramArrayOfBigDecimal) throws IllegalArgumentException;
  
  BigDecimal[] preMultiply(BigDecimal[] paramArrayOfBigDecimal) throws IllegalArgumentException;
  
  BigDecimal[] solve(BigDecimal[] paramArrayOfBigDecimal) throws IllegalArgumentException, InvalidMatrixException;
  
  BigMatrix solve(BigMatrix paramBigMatrix) throws IllegalArgumentException, InvalidMatrixException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\linear\BigMatrix.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */