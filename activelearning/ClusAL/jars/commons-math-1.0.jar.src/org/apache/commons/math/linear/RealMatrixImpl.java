/*     */ package org.apache.commons.math.linear;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.util.MathUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RealMatrixImpl
/*     */   implements RealMatrix, Serializable
/*     */ {
/*     */   static final long serialVersionUID = 4237564493130426188L;
/*  56 */   private double[][] data = (double[][])null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   private double[][] lu = (double[][])null;
/*     */ 
/*     */   
/*  64 */   private int[] permutation = null;
/*     */ 
/*     */   
/*  67 */   private int parity = 1;
/*     */ 
/*     */   
/*  70 */   protected static double TOO_SMALL = 1.0E-11D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrixImpl() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrixImpl(int rowDimension, int columnDimension) {
/*  85 */     this.data = new double[rowDimension][columnDimension];
/*  86 */     this.lu = (double[][])null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrixImpl(double[][] d) {
/* 101 */     int nRows = d.length;
/* 102 */     if (nRows == 0) {
/* 103 */       throw new IllegalArgumentException("Matrix must have at least one row.");
/*     */     }
/*     */     
/* 106 */     int nCols = (d[0]).length;
/* 107 */     if (nCols == 0) {
/* 108 */       throw new IllegalArgumentException("Matrix must have at least one column.");
/*     */     }
/*     */     
/* 111 */     for (int row = 1; row < nRows; row++) {
/* 112 */       if ((d[row]).length != nCols) {
/* 113 */         throw new IllegalArgumentException("All input rows must have the same length.");
/*     */       }
/*     */     } 
/*     */     
/* 117 */     copyIn(d);
/* 118 */     this.lu = (double[][])null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrixImpl(double[] v) {
/* 131 */     int nRows = v.length;
/* 132 */     this.data = new double[nRows][1];
/* 133 */     for (int row = 0; row < nRows; row++) {
/* 134 */       this.data[row][0] = v[row];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix copy() {
/* 144 */     return new RealMatrixImpl(copyOut());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix add(RealMatrix m) throws IllegalArgumentException {
/* 155 */     if (getColumnDimension() != m.getColumnDimension() || getRowDimension() != m.getRowDimension())
/*     */     {
/* 157 */       throw new IllegalArgumentException("matrix dimension mismatch");
/*     */     }
/* 159 */     int rowCount = getRowDimension();
/* 160 */     int columnCount = getColumnDimension();
/* 161 */     double[][] outData = new double[rowCount][columnCount];
/* 162 */     for (int row = 0; row < rowCount; row++) {
/* 163 */       for (int col = 0; col < columnCount; col++) {
/* 164 */         outData[row][col] = this.data[row][col] + m.getEntry(row, col);
/*     */       }
/*     */     } 
/* 167 */     return new RealMatrixImpl(outData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix subtract(RealMatrix m) throws IllegalArgumentException {
/* 178 */     if (getColumnDimension() != m.getColumnDimension() || getRowDimension() != m.getRowDimension())
/*     */     {
/* 180 */       throw new IllegalArgumentException("matrix dimension mismatch");
/*     */     }
/* 182 */     int rowCount = getRowDimension();
/* 183 */     int columnCount = getColumnDimension();
/* 184 */     double[][] outData = new double[rowCount][columnCount];
/* 185 */     for (int row = 0; row < rowCount; row++) {
/* 186 */       for (int col = 0; col < columnCount; col++) {
/* 187 */         outData[row][col] = this.data[row][col] - m.getEntry(row, col);
/*     */       }
/*     */     } 
/* 190 */     return new RealMatrixImpl(outData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix scalarAdd(double d) {
/* 200 */     int rowCount = getRowDimension();
/* 201 */     int columnCount = getColumnDimension();
/* 202 */     double[][] outData = new double[rowCount][columnCount];
/* 203 */     for (int row = 0; row < rowCount; row++) {
/* 204 */       for (int col = 0; col < columnCount; col++) {
/* 205 */         outData[row][col] = this.data[row][col] + d;
/*     */       }
/*     */     } 
/* 208 */     return new RealMatrixImpl(outData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix scalarMultiply(double d) {
/* 217 */     int rowCount = getRowDimension();
/* 218 */     int columnCount = getColumnDimension();
/* 219 */     double[][] outData = new double[rowCount][columnCount];
/* 220 */     for (int row = 0; row < rowCount; row++) {
/* 221 */       for (int col = 0; col < columnCount; col++) {
/* 222 */         outData[row][col] = this.data[row][col] * d;
/*     */       }
/*     */     } 
/* 225 */     return new RealMatrixImpl(outData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix multiply(RealMatrix m) throws IllegalArgumentException {
/* 236 */     if (getColumnDimension() != m.getRowDimension()) {
/* 237 */       throw new IllegalArgumentException("Matrices are not multiplication compatible.");
/*     */     }
/* 239 */     int nRows = getRowDimension();
/* 240 */     int nCols = m.getColumnDimension();
/* 241 */     int nSum = getColumnDimension();
/* 242 */     double[][] outData = new double[nRows][nCols];
/* 243 */     double sum = 0.0D;
/* 244 */     for (int row = 0; row < nRows; row++) {
/* 245 */       for (int col = 0; col < nCols; col++) {
/* 246 */         sum = 0.0D;
/* 247 */         for (int i = 0; i < nSum; i++) {
/* 248 */           sum += this.data[row][i] * m.getEntry(i, col);
/*     */         }
/* 250 */         outData[row][col] = sum;
/*     */       } 
/*     */     } 
/* 253 */     return new RealMatrixImpl(outData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix preMultiply(RealMatrix m) throws IllegalArgumentException {
/* 264 */     return m.multiply(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] getData() {
/* 275 */     return copyOut();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] getDataRef() {
/* 286 */     return this.data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getNorm() {
/* 294 */     double maxColSum = 0.0D;
/* 295 */     for (int col = 0; col < getColumnDimension(); col++) {
/* 296 */       double sum = 0.0D;
/* 297 */       for (int row = 0; row < getRowDimension(); row++) {
/* 298 */         sum += Math.abs(this.data[row][col]);
/*     */       }
/* 300 */       maxColSum = Math.max(maxColSum, sum);
/*     */     } 
/* 302 */     return maxColSum;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix getSubMatrix(int startRow, int endRow, int startColumn, int endColumn) throws MatrixIndexException {
/* 319 */     if (startRow < 0 || startRow > endRow || endRow > this.data.length || startColumn < 0 || startColumn > endColumn || endColumn > (this.data[0]).length)
/*     */     {
/*     */       
/* 322 */       throw new MatrixIndexException("invalid row or column index selection");
/*     */     }
/*     */     
/* 325 */     RealMatrixImpl subMatrix = new RealMatrixImpl(endRow - startRow + 1, endColumn - startColumn + 1);
/*     */     
/* 327 */     double[][] subMatrixData = subMatrix.getDataRef();
/* 328 */     for (int i = startRow; i <= endRow; i++) {
/* 329 */       for (int j = startColumn; j <= endColumn; j++) {
/* 330 */         subMatrixData[i - startRow][j - startColumn] = this.data[i][j];
/*     */       }
/*     */     } 
/* 333 */     return subMatrix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns) throws MatrixIndexException {
/* 349 */     if (selectedRows.length * selectedColumns.length == 0) {
/* 350 */       throw new MatrixIndexException("selected row and column index arrays must be non-empty");
/*     */     }
/*     */     
/* 353 */     RealMatrixImpl subMatrix = new RealMatrixImpl(selectedRows.length, selectedColumns.length);
/*     */     
/* 355 */     double[][] subMatrixData = subMatrix.getDataRef();
/*     */     try {
/* 357 */       for (int i = 0; i < selectedRows.length; i++) {
/* 358 */         for (int j = 0; j < selectedColumns.length; j++) {
/* 359 */           subMatrixData[i][j] = this.data[selectedRows[i]][selectedColumns[j]];
/*     */         }
/*     */       }
/*     */     
/* 363 */     } catch (ArrayIndexOutOfBoundsException e) {
/* 364 */       throw new MatrixIndexException("matrix dimension mismatch");
/*     */     } 
/* 366 */     return subMatrix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix getRowMatrix(int row) throws MatrixIndexException {
/* 378 */     if (!isValidCoordinate(row, 0)) {
/* 379 */       throw new MatrixIndexException("illegal row argument");
/*     */     }
/* 381 */     int ncols = getColumnDimension();
/* 382 */     double[][] out = new double[1][ncols];
/* 383 */     System.arraycopy(this.data[row], 0, out[0], 0, ncols);
/* 384 */     return new RealMatrixImpl(out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix getColumnMatrix(int column) throws MatrixIndexException {
/* 396 */     if (!isValidCoordinate(0, column)) {
/* 397 */       throw new MatrixIndexException("illegal column argument");
/*     */     }
/* 399 */     int nRows = getRowDimension();
/* 400 */     double[][] out = new double[nRows][1];
/* 401 */     for (int row = 0; row < nRows; row++) {
/* 402 */       out[row][0] = this.data[row][column];
/*     */     }
/* 404 */     return new RealMatrixImpl(out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] getRow(int row) throws MatrixIndexException {
/* 418 */     if (!isValidCoordinate(row, 0)) {
/* 419 */       throw new MatrixIndexException("illegal row argument");
/*     */     }
/* 421 */     int ncols = getColumnDimension();
/* 422 */     double[] out = new double[ncols];
/* 423 */     System.arraycopy(this.data[row], 0, out, 0, ncols);
/* 424 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] getColumn(int col) throws MatrixIndexException {
/* 438 */     if (!isValidCoordinate(0, col)) {
/* 439 */       throw new MatrixIndexException("illegal column argument");
/*     */     }
/* 441 */     int nRows = getRowDimension();
/* 442 */     double[] out = new double[nRows];
/* 443 */     for (int row = 0; row < nRows; row++) {
/* 444 */       out[row] = this.data[row][col];
/*     */     }
/* 446 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getEntry(int row, int column) throws MatrixIndexException {
/* 466 */     if (!isValidCoordinate(row, column)) {
/* 467 */       throw new MatrixIndexException("matrix entry does not exist");
/*     */     }
/* 469 */     return this.data[row][column];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix transpose() {
/* 478 */     int nRows = getRowDimension();
/* 479 */     int nCols = getColumnDimension();
/* 480 */     RealMatrixImpl out = new RealMatrixImpl(nCols, nRows);
/* 481 */     double[][] outData = out.getDataRef();
/* 482 */     for (int row = 0; row < nRows; row++) {
/* 483 */       for (int col = 0; col < nCols; col++) {
/* 484 */         outData[col][row] = this.data[row][col];
/*     */       }
/*     */     } 
/* 487 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix inverse() throws InvalidMatrixException {
/* 497 */     return solve(getIdentity(getRowDimension()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDeterminant() throws InvalidMatrixException {
/* 505 */     if (!isSquare()) {
/* 506 */       throw new InvalidMatrixException("matrix is not square");
/*     */     }
/* 508 */     if (isSingular()) {
/* 509 */       return 0.0D;
/*     */     }
/* 511 */     double det = this.parity;
/* 512 */     for (int i = 0; i < getRowDimension(); i++) {
/* 513 */       det *= this.lu[i][i];
/*     */     }
/* 515 */     return det;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSquare() {
/* 523 */     return (getColumnDimension() == getRowDimension());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSingular() {
/* 530 */     if (this.lu == null) {
/*     */       try {
/* 532 */         luDecompose();
/* 533 */         return false;
/* 534 */       } catch (InvalidMatrixException ex) {
/* 535 */         return true;
/*     */       } 
/*     */     }
/* 538 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRowDimension() {
/* 546 */     return this.data.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getColumnDimension() {
/* 553 */     return (this.data[0]).length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getTrace() throws IllegalArgumentException {
/* 561 */     if (!isSquare()) {
/* 562 */       throw new IllegalArgumentException("matrix is not square");
/*     */     }
/* 564 */     double trace = this.data[0][0];
/* 565 */     for (int i = 1; i < getRowDimension(); i++) {
/* 566 */       trace += this.data[i][i];
/*     */     }
/* 568 */     return trace;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] operate(double[] v) throws IllegalArgumentException {
/* 577 */     if (v.length != getColumnDimension()) {
/* 578 */       throw new IllegalArgumentException("vector has wrong length");
/*     */     }
/* 580 */     int nRows = getRowDimension();
/* 581 */     int nCols = getColumnDimension();
/* 582 */     double[] out = new double[v.length];
/* 583 */     for (int row = 0; row < nRows; row++) {
/* 584 */       double sum = 0.0D;
/* 585 */       for (int i = 0; i < nCols; i++) {
/* 586 */         sum += this.data[row][i] * v[i];
/*     */       }
/* 588 */       out[row] = sum;
/*     */     } 
/* 590 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] preMultiply(double[] v) throws IllegalArgumentException {
/* 599 */     int nRows = getRowDimension();
/* 600 */     if (v.length != nRows) {
/* 601 */       throw new IllegalArgumentException("vector has wrong length");
/*     */     }
/* 603 */     int nCols = getColumnDimension();
/* 604 */     double[] out = new double[nCols];
/* 605 */     for (int col = 0; col < nCols; col++) {
/* 606 */       double sum = 0.0D;
/* 607 */       for (int i = 0; i < nRows; i++) {
/* 608 */         sum += this.data[i][col] * v[i];
/*     */       }
/* 610 */       out[col] = sum;
/*     */     } 
/* 612 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] solve(double[] b) throws IllegalArgumentException, InvalidMatrixException {
/* 627 */     int nRows = getRowDimension();
/* 628 */     if (b.length != nRows) {
/* 629 */       throw new IllegalArgumentException("constant vector has wrong length");
/*     */     }
/* 631 */     RealMatrix bMatrix = new RealMatrixImpl(b);
/* 632 */     double[][] solution = ((RealMatrixImpl)solve(bMatrix)).getDataRef();
/* 633 */     double[] out = new double[nRows];
/* 634 */     for (int row = 0; row < nRows; row++) {
/* 635 */       out[row] = solution[row][0];
/*     */     }
/* 637 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RealMatrix solve(RealMatrix b) throws IllegalArgumentException, InvalidMatrixException {
/* 652 */     if (b.getRowDimension() != getRowDimension()) {
/* 653 */       throw new IllegalArgumentException("Incorrect row dimension");
/*     */     }
/* 655 */     if (!isSquare()) {
/* 656 */       throw new InvalidMatrixException("coefficient matrix is not square");
/*     */     }
/* 658 */     if (isSingular()) {
/* 659 */       throw new InvalidMatrixException("Matrix is singular.");
/*     */     }
/*     */     
/* 662 */     int nCol = getColumnDimension();
/* 663 */     int nColB = b.getColumnDimension();
/* 664 */     int nRowB = b.getRowDimension();
/*     */ 
/*     */     
/* 667 */     double[][] bp = new double[nRowB][nColB];
/* 668 */     for (int row = 0; row < nRowB; row++) {
/* 669 */       for (int i = 0; i < nColB; i++) {
/* 670 */         bp[row][i] = b.getEntry(this.permutation[row], i);
/*     */       }
/*     */     } 
/*     */     
/*     */     int col;
/* 675 */     for (col = 0; col < nCol; col++) {
/* 676 */       for (int i = col + 1; i < nCol; i++) {
/* 677 */         for (int j = 0; j < nColB; j++) {
/* 678 */           bp[i][j] = bp[i][j] - bp[col][j] * this.lu[i][col];
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 684 */     for (col = nCol - 1; col >= 0; col--) {
/* 685 */       for (int j = 0; j < nColB; j++) {
/* 686 */         bp[col][j] = bp[col][j] / this.lu[col][col];
/*     */       }
/* 688 */       for (int i = 0; i < col; i++) {
/* 689 */         for (int k = 0; k < nColB; k++) {
/* 690 */           bp[i][k] = bp[i][k] - bp[col][k] * this.lu[i][col];
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 695 */     RealMatrixImpl outMat = new RealMatrixImpl(bp);
/* 696 */     return outMat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void luDecompose() throws InvalidMatrixException {
/* 719 */     int nRows = getRowDimension();
/* 720 */     int nCols = getColumnDimension();
/* 721 */     if (nRows != nCols) {
/* 722 */       throw new InvalidMatrixException("LU decomposition requires that the matrix be square.");
/*     */     }
/* 724 */     this.lu = getData();
/*     */ 
/*     */     
/* 727 */     this.permutation = new int[nRows];
/* 728 */     for (int row = 0; row < nRows; row++) {
/* 729 */       this.permutation[row] = row;
/*     */     }
/* 731 */     this.parity = 1;
/*     */ 
/*     */     
/* 734 */     for (int col = 0; col < nCols; col++) {
/*     */       
/* 736 */       double sum = 0.0D;
/*     */ 
/*     */       
/* 739 */       for (int i = 0; i < col; i++) {
/* 740 */         sum = this.lu[i][col];
/* 741 */         for (int k = 0; k < i; k++) {
/* 742 */           sum -= this.lu[i][k] * this.lu[k][col];
/*     */         }
/* 744 */         this.lu[i][col] = sum;
/*     */       } 
/*     */ 
/*     */       
/* 748 */       int max = col;
/* 749 */       double largest = 0.0D; int j;
/* 750 */       for (j = col; j < nRows; j++) {
/* 751 */         sum = this.lu[j][col];
/* 752 */         for (int k = 0; k < col; k++) {
/* 753 */           sum -= this.lu[j][k] * this.lu[k][col];
/*     */         }
/* 755 */         this.lu[j][col] = sum;
/*     */ 
/*     */         
/* 758 */         if (Math.abs(sum) > largest) {
/* 759 */           largest = Math.abs(sum);
/* 760 */           max = j;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 765 */       if (Math.abs(this.lu[max][col]) < TOO_SMALL) {
/* 766 */         this.lu = (double[][])null;
/* 767 */         throw new InvalidMatrixException("matrix is singular");
/*     */       } 
/*     */ 
/*     */       
/* 771 */       if (max != col) {
/* 772 */         double tmp = 0.0D;
/* 773 */         for (int k = 0; k < nCols; k++) {
/* 774 */           tmp = this.lu[max][k];
/* 775 */           this.lu[max][k] = this.lu[col][k];
/* 776 */           this.lu[col][k] = tmp;
/*     */         } 
/* 778 */         int temp = this.permutation[max];
/* 779 */         this.permutation[max] = this.permutation[col];
/* 780 */         this.permutation[col] = temp;
/* 781 */         this.parity = -this.parity;
/*     */       } 
/*     */ 
/*     */       
/* 785 */       for (j = col + 1; j < nRows; j++) {
/* 786 */         this.lu[j][col] = this.lu[j][col] / this.lu[col][col];
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 796 */     StringBuffer res = new StringBuffer();
/* 797 */     res.append("RealMatrixImpl{");
/* 798 */     if (this.data != null) {
/* 799 */       for (int i = 0; i < this.data.length; i++) {
/* 800 */         if (i > 0)
/* 801 */           res.append(","); 
/* 802 */         res.append("{");
/* 803 */         for (int j = 0; j < (this.data[0]).length; j++) {
/* 804 */           if (j > 0)
/* 805 */             res.append(","); 
/* 806 */           res.append(this.data[i][j]);
/*     */         } 
/* 808 */         res.append("}");
/*     */       } 
/*     */     }
/* 811 */     res.append("}");
/* 812 */     return res.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 824 */     if (object == this) {
/* 825 */       return true;
/*     */     }
/* 827 */     if (!(object instanceof RealMatrixImpl)) {
/* 828 */       return false;
/*     */     }
/* 830 */     RealMatrix m = (RealMatrix)object;
/* 831 */     int nRows = getRowDimension();
/* 832 */     int nCols = getColumnDimension();
/* 833 */     if (m.getColumnDimension() != nCols || m.getRowDimension() != nRows) {
/* 834 */       return false;
/*     */     }
/* 836 */     for (int row = 0; row < nRows; row++) {
/* 837 */       for (int col = 0; col < nCols; col++) {
/* 838 */         if (this.data[row][col] != m.getEntry(row, col)) {
/* 839 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/* 843 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 852 */     int ret = 7;
/* 853 */     int nRows = getRowDimension();
/* 854 */     int nCols = getColumnDimension();
/* 855 */     ret = ret * 31 + nRows;
/* 856 */     ret = ret * 31 + nCols;
/* 857 */     for (int row = 0; row < nRows; row++) {
/* 858 */       for (int col = 0; col < nCols; col++) {
/* 859 */         ret = ret * 31 + (11 * (row + 1) + 17 * (col + 1)) * MathUtils.hash(this.data[row][col]);
/*     */       }
/*     */     } 
/*     */     
/* 863 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected RealMatrix getIdentity(int dimension) {
/* 875 */     RealMatrixImpl out = new RealMatrixImpl(dimension, dimension);
/* 876 */     double[][] d = out.getDataRef();
/* 877 */     for (int row = 0; row < dimension; row++) {
/* 878 */       for (int col = 0; col < dimension; col++) {
/* 879 */         d[row][col] = (row == col) ? 1.0D : 0.0D;
/*     */       }
/*     */     } 
/* 882 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected RealMatrix getLUMatrix() throws InvalidMatrixException {
/* 913 */     if (this.lu == null) {
/* 914 */       luDecompose();
/*     */     }
/* 916 */     return new RealMatrixImpl(this.lu);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int[] getPermutation() {
/* 932 */     int[] out = new int[this.permutation.length];
/* 933 */     System.arraycopy(this.permutation, 0, out, 0, this.permutation.length);
/* 934 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double[][] copyOut() {
/* 945 */     int nRows = getRowDimension();
/* 946 */     double[][] out = new double[nRows][getColumnDimension()];
/*     */     
/* 948 */     for (int i = 0; i < nRows; i++) {
/* 949 */       System.arraycopy(this.data[i], 0, out[i], 0, (this.data[i]).length);
/*     */     }
/* 951 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void copyIn(double[][] in) {
/* 960 */     int nRows = in.length;
/* 961 */     int nCols = (in[0]).length;
/* 962 */     this.data = new double[nRows][nCols];
/* 963 */     System.arraycopy(in, 0, this.data, 0, in.length);
/* 964 */     for (int i = 0; i < nRows; i++) {
/* 965 */       System.arraycopy(in[i], 0, this.data[i], 0, nCols);
/*     */     }
/* 967 */     this.lu = (double[][])null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isValidCoordinate(int row, int col) {
/* 978 */     int nRows = getRowDimension();
/* 979 */     int nCols = getColumnDimension();
/*     */     
/* 981 */     return (row >= 0 && row <= nRows - 1 && col >= 0 && col <= nCols - 1);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\linear\RealMatrixImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */