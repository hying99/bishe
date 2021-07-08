package org.apache.commons.math.linear;

public interface RealMatrix {
  RealMatrix copy();
  
  RealMatrix add(RealMatrix paramRealMatrix) throws IllegalArgumentException;
  
  RealMatrix subtract(RealMatrix paramRealMatrix) throws IllegalArgumentException;
  
  RealMatrix scalarAdd(double paramDouble);
  
  RealMatrix scalarMultiply(double paramDouble);
  
  RealMatrix multiply(RealMatrix paramRealMatrix) throws IllegalArgumentException;
  
  RealMatrix preMultiply(RealMatrix paramRealMatrix) throws IllegalArgumentException;
  
  double[][] getData();
  
  double getNorm();
  
  RealMatrix getSubMatrix(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws MatrixIndexException;
  
  RealMatrix getSubMatrix(int[] paramArrayOfint1, int[] paramArrayOfint2) throws MatrixIndexException;
  
  RealMatrix getRowMatrix(int paramInt) throws MatrixIndexException;
  
  RealMatrix getColumnMatrix(int paramInt) throws MatrixIndexException;
  
  double[] getRow(int paramInt) throws MatrixIndexException;
  
  double[] getColumn(int paramInt) throws MatrixIndexException;
  
  double getEntry(int paramInt1, int paramInt2) throws MatrixIndexException;
  
  RealMatrix transpose();
  
  RealMatrix inverse() throws InvalidMatrixException;
  
  double getDeterminant();
  
  boolean isSquare();
  
  boolean isSingular();
  
  int getRowDimension();
  
  int getColumnDimension();
  
  double getTrace();
  
  double[] operate(double[] paramArrayOfdouble) throws IllegalArgumentException;
  
  double[] preMultiply(double[] paramArrayOfdouble) throws IllegalArgumentException;
  
  double[] solve(double[] paramArrayOfdouble) throws IllegalArgumentException, InvalidMatrixException;
  
  RealMatrix solve(RealMatrix paramRealMatrix) throws IllegalArgumentException, InvalidMatrixException;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\linear\RealMatrix.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */