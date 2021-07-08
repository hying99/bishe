/*      */ package org.apache.commons.math.linear;
/*      */ 
/*      */ import java.io.Serializable;
/*      */ import java.math.BigDecimal;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BigMatrixImpl
/*      */   implements BigMatrix, Serializable
/*      */ {
/*      */   static final long serialVersionUID = -1011428905656140431L;
/*   54 */   private static final BigDecimal ZERO = new BigDecimal(0.0D);
/*      */ 
/*      */   
/*   57 */   private static final BigDecimal ONE = new BigDecimal(1.0D);
/*      */ 
/*      */   
/*   60 */   private BigDecimal[][] data = (BigDecimal[][])null;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   65 */   private BigDecimal[][] lu = (BigDecimal[][])null;
/*      */ 
/*      */   
/*   68 */   private int[] permutation = null;
/*      */ 
/*      */   
/*   71 */   private int parity = 1;
/*      */ 
/*      */   
/*   74 */   private int roundingMode = 4;
/*      */ 
/*      */   
/*   77 */   private int scale = 64;
/*      */ 
/*      */   
/*   80 */   protected static BigDecimal TOO_SMALL = new BigDecimal(1.0E-11D);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrixImpl() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrixImpl(int rowDimension, int columnDimension) {
/*   95 */     this.data = new BigDecimal[rowDimension][columnDimension];
/*   96 */     this.lu = (BigDecimal[][])null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrixImpl(BigDecimal[][] d) {
/*  111 */     int nRows = d.length;
/*  112 */     if (nRows == 0) {
/*  113 */       throw new IllegalArgumentException("Matrix must have at least one row.");
/*      */     }
/*      */     
/*  116 */     int nCols = (d[0]).length;
/*  117 */     if (nCols == 0) {
/*  118 */       throw new IllegalArgumentException("Matrix must have at least one column.");
/*      */     }
/*      */     
/*  121 */     for (int row = 1; row < nRows; row++) {
/*  122 */       if ((d[row]).length != nCols) {
/*  123 */         throw new IllegalArgumentException("All input rows must have the same length.");
/*      */       }
/*      */     } 
/*      */     
/*  127 */     copyIn(d);
/*  128 */     this.lu = (BigDecimal[][])null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrixImpl(double[][] d) {
/*  143 */     int nRows = d.length;
/*  144 */     if (nRows == 0) {
/*  145 */       throw new IllegalArgumentException("Matrix must have at least one row.");
/*      */     }
/*      */     
/*  148 */     int nCols = (d[0]).length;
/*  149 */     if (nCols == 0) {
/*  150 */       throw new IllegalArgumentException("Matrix must have at least one column.");
/*      */     }
/*      */     
/*  153 */     for (int row = 1; row < nRows; row++) {
/*  154 */       if ((d[row]).length != nCols) {
/*  155 */         throw new IllegalArgumentException("All input rows must have the same length.");
/*      */       }
/*      */     } 
/*      */     
/*  159 */     copyIn(d);
/*  160 */     this.lu = (BigDecimal[][])null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrixImpl(String[][] d) {
/*  173 */     int nRows = d.length;
/*  174 */     if (nRows == 0) {
/*  175 */       throw new IllegalArgumentException("Matrix must have at least one row.");
/*      */     }
/*      */     
/*  178 */     int nCols = (d[0]).length;
/*  179 */     if (nCols == 0) {
/*  180 */       throw new IllegalArgumentException("Matrix must have at least one column.");
/*      */     }
/*      */     
/*  183 */     for (int row = 1; row < nRows; row++) {
/*  184 */       if ((d[row]).length != nCols) {
/*  185 */         throw new IllegalArgumentException("All input rows must have the same length.");
/*      */       }
/*      */     } 
/*      */     
/*  189 */     copyIn(d);
/*  190 */     this.lu = (BigDecimal[][])null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrixImpl(BigDecimal[] v) {
/*  203 */     int nRows = v.length;
/*  204 */     this.data = new BigDecimal[nRows][1];
/*  205 */     for (int row = 0; row < nRows; row++) {
/*  206 */       this.data[row][0] = v[row];
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix copy() {
/*  216 */     return new BigMatrixImpl(copyOut());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix add(BigMatrix m) throws IllegalArgumentException {
/*  227 */     if (getColumnDimension() != m.getColumnDimension() || getRowDimension() != m.getRowDimension())
/*      */     {
/*  229 */       throw new IllegalArgumentException("matrix dimension mismatch");
/*      */     }
/*  231 */     int rowCount = getRowDimension();
/*  232 */     int columnCount = getColumnDimension();
/*  233 */     BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
/*  234 */     for (int row = 0; row < rowCount; row++) {
/*  235 */       for (int col = 0; col < columnCount; col++) {
/*  236 */         outData[row][col] = this.data[row][col].add(m.getEntry(row, col));
/*      */       }
/*      */     } 
/*  239 */     return new BigMatrixImpl(outData);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix subtract(BigMatrix m) throws IllegalArgumentException {
/*  250 */     if (getColumnDimension() != m.getColumnDimension() || getRowDimension() != m.getRowDimension())
/*      */     {
/*  252 */       throw new IllegalArgumentException("matrix dimension mismatch");
/*      */     }
/*  254 */     int rowCount = getRowDimension();
/*  255 */     int columnCount = getColumnDimension();
/*  256 */     BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
/*  257 */     for (int row = 0; row < rowCount; row++) {
/*  258 */       for (int col = 0; col < columnCount; col++) {
/*  259 */         outData[row][col] = this.data[row][col].subtract(m.getEntry(row, col));
/*      */       }
/*      */     } 
/*  262 */     return new BigMatrixImpl(outData);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix scalarAdd(BigDecimal d) {
/*  272 */     int rowCount = getRowDimension();
/*  273 */     int columnCount = getColumnDimension();
/*  274 */     BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
/*  275 */     for (int row = 0; row < rowCount; row++) {
/*  276 */       for (int col = 0; col < columnCount; col++) {
/*  277 */         outData[row][col] = this.data[row][col].add(d);
/*      */       }
/*      */     } 
/*  280 */     return new BigMatrixImpl(outData);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix scalarMultiply(BigDecimal d) {
/*  289 */     int rowCount = getRowDimension();
/*  290 */     int columnCount = getColumnDimension();
/*  291 */     BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
/*  292 */     for (int row = 0; row < rowCount; row++) {
/*  293 */       for (int col = 0; col < columnCount; col++) {
/*  294 */         outData[row][col] = this.data[row][col].multiply(d);
/*      */       }
/*      */     } 
/*  297 */     return new BigMatrixImpl(outData);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix multiply(BigMatrix m) throws IllegalArgumentException {
/*  308 */     if (getColumnDimension() != m.getRowDimension()) {
/*  309 */       throw new IllegalArgumentException("Matrices are not multiplication compatible.");
/*      */     }
/*  311 */     int nRows = getRowDimension();
/*  312 */     int nCols = m.getColumnDimension();
/*  313 */     int nSum = getColumnDimension();
/*  314 */     BigDecimal[][] outData = new BigDecimal[nRows][nCols];
/*  315 */     BigDecimal sum = ZERO;
/*  316 */     for (int row = 0; row < nRows; row++) {
/*  317 */       for (int col = 0; col < nCols; col++) {
/*  318 */         sum = ZERO;
/*  319 */         for (int i = 0; i < nSum; i++) {
/*  320 */           sum = sum.add(this.data[row][i].multiply(m.getEntry(i, col)));
/*      */         }
/*  322 */         outData[row][col] = sum;
/*      */       } 
/*      */     } 
/*  325 */     return new BigMatrixImpl(outData);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix preMultiply(BigMatrix m) throws IllegalArgumentException {
/*  336 */     return m.multiply(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal[][] getData() {
/*  347 */     return copyOut();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double[][] getDataAsDoubleArray() {
/*  359 */     int nRows = getRowDimension();
/*  360 */     int nCols = getColumnDimension();
/*  361 */     double[][] d = new double[nRows][nCols];
/*  362 */     for (int i = 0; i < nRows; i++) {
/*  363 */       for (int j = 0; j < nCols; j++) {
/*  364 */         d[i][j] = this.data[i][j].doubleValue();
/*      */       }
/*      */     } 
/*  367 */     return d;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal[][] getDataRef() {
/*  378 */     return this.data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getRoundingMode() {
/*  388 */     return this.roundingMode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRoundingMode(int roundingMode) {
/*  397 */     this.roundingMode = roundingMode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getScale() {
/*  407 */     return this.scale;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setScale(int scale) {
/*  416 */     this.scale = scale;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal getNorm() {
/*  426 */     BigDecimal maxColSum = ZERO;
/*  427 */     for (int col = 0; col < getColumnDimension(); col++) {
/*  428 */       BigDecimal sum = ZERO;
/*  429 */       for (int row = 0; row < getRowDimension(); row++) {
/*  430 */         sum = sum.add(this.data[row][col].abs());
/*      */       }
/*  432 */       maxColSum = maxColSum.max(sum);
/*      */     } 
/*  434 */     return maxColSum;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix getSubMatrix(int startRow, int endRow, int startColumn, int endColumn) throws MatrixIndexException {
/*  451 */     if (startRow < 0 || startRow > endRow || endRow > this.data.length || startColumn < 0 || startColumn > endColumn || endColumn > (this.data[0]).length)
/*      */     {
/*      */       
/*  454 */       throw new MatrixIndexException("invalid row or column index selection");
/*      */     }
/*      */     
/*  457 */     BigMatrixImpl subMatrix = new BigMatrixImpl(endRow - startRow + 1, endColumn - startColumn + 1);
/*      */     
/*  459 */     BigDecimal[][] subMatrixData = subMatrix.getDataRef();
/*  460 */     for (int i = startRow; i <= endRow; i++) {
/*  461 */       for (int j = startColumn; j <= endColumn; j++) {
/*  462 */         subMatrixData[i - startRow][j - startColumn] = this.data[i][j];
/*      */       }
/*      */     } 
/*  465 */     return subMatrix;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns) throws MatrixIndexException {
/*  481 */     if (selectedRows.length * selectedColumns.length == 0) {
/*  482 */       throw new MatrixIndexException("selected row and column index arrays must be non-empty");
/*      */     }
/*      */     
/*  485 */     BigMatrixImpl subMatrix = new BigMatrixImpl(selectedRows.length, selectedColumns.length);
/*      */     
/*  487 */     BigDecimal[][] subMatrixData = subMatrix.getDataRef();
/*      */     try {
/*  489 */       for (int i = 0; i < selectedRows.length; i++) {
/*  490 */         for (int j = 0; j < selectedColumns.length; j++) {
/*  491 */           subMatrixData[i][j] = this.data[selectedRows[i]][selectedColumns[j]];
/*      */         }
/*      */       }
/*      */     
/*  495 */     } catch (ArrayIndexOutOfBoundsException e) {
/*  496 */       throw new MatrixIndexException("matrix dimension mismatch");
/*      */     } 
/*  498 */     return subMatrix;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix getRowMatrix(int row) throws MatrixIndexException {
/*  510 */     if (!isValidCoordinate(row, 0)) {
/*  511 */       throw new MatrixIndexException("illegal row argument");
/*      */     }
/*  513 */     int ncols = getColumnDimension();
/*  514 */     BigDecimal[][] out = new BigDecimal[1][ncols];
/*  515 */     System.arraycopy(this.data[row], 0, out[0], 0, ncols);
/*  516 */     return new BigMatrixImpl(out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix getColumnMatrix(int column) throws MatrixIndexException {
/*  528 */     if (!isValidCoordinate(0, column)) {
/*  529 */       throw new MatrixIndexException("illegal column argument");
/*      */     }
/*  531 */     int nRows = getRowDimension();
/*  532 */     BigDecimal[][] out = new BigDecimal[nRows][1];
/*  533 */     for (int row = 0; row < nRows; row++) {
/*  534 */       out[row][0] = this.data[row][column];
/*      */     }
/*  536 */     return new BigMatrixImpl(out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal[] getRow(int row) throws MatrixIndexException {
/*  550 */     if (!isValidCoordinate(row, 0)) {
/*  551 */       throw new MatrixIndexException("illegal row argument");
/*      */     }
/*  553 */     int ncols = getColumnDimension();
/*  554 */     BigDecimal[] out = new BigDecimal[ncols];
/*  555 */     System.arraycopy(this.data[row], 0, out, 0, ncols);
/*  556 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double[] getRowAsDoubleArray(int row) throws MatrixIndexException {
/*  571 */     if (!isValidCoordinate(row, 0)) {
/*  572 */       throw new MatrixIndexException("illegal row argument");
/*      */     }
/*  574 */     int ncols = getColumnDimension();
/*  575 */     double[] out = new double[ncols];
/*  576 */     for (int i = 0; i < ncols; i++) {
/*  577 */       out[i] = this.data[row][i].doubleValue();
/*      */     }
/*  579 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal[] getColumn(int col) throws MatrixIndexException {
/*  593 */     if (!isValidCoordinate(0, col)) {
/*  594 */       throw new MatrixIndexException("illegal column argument");
/*      */     }
/*  596 */     int nRows = getRowDimension();
/*  597 */     BigDecimal[] out = new BigDecimal[nRows];
/*  598 */     for (int i = 0; i < nRows; i++) {
/*  599 */       out[i] = this.data[i][col];
/*      */     }
/*  601 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double[] getColumnAsDoubleArray(int col) throws MatrixIndexException {
/*  616 */     if (!isValidCoordinate(0, col)) {
/*  617 */       throw new MatrixIndexException("illegal column argument");
/*      */     }
/*  619 */     int nrows = getRowDimension();
/*  620 */     double[] out = new double[nrows];
/*  621 */     for (int i = 0; i < nrows; i++) {
/*  622 */       out[i] = this.data[i][col].doubleValue();
/*      */     }
/*  624 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal getEntry(int row, int column) throws MatrixIndexException {
/*  644 */     if (!isValidCoordinate(row, column)) {
/*  645 */       throw new MatrixIndexException("matrix entry does not exist");
/*      */     }
/*  647 */     return this.data[row][column];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getEntryAsDouble(int row, int column) throws MatrixIndexException {
/*  667 */     return getEntry(row, column).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix transpose() {
/*  676 */     int nRows = getRowDimension();
/*  677 */     int nCols = getColumnDimension();
/*  678 */     BigMatrixImpl out = new BigMatrixImpl(nCols, nRows);
/*  679 */     BigDecimal[][] outData = out.getDataRef();
/*  680 */     for (int row = 0; row < nRows; row++) {
/*  681 */       for (int col = 0; col < nCols; col++) {
/*  682 */         outData[col][row] = this.data[row][col];
/*      */       }
/*      */     } 
/*  685 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix inverse() throws InvalidMatrixException {
/*  695 */     return solve(getIdentity(getRowDimension()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal getDeterminant() throws InvalidMatrixException {
/*  705 */     if (!isSquare()) {
/*  706 */       throw new InvalidMatrixException("matrix is not square");
/*      */     }
/*  708 */     if (isSingular()) {
/*  709 */       return ZERO;
/*      */     }
/*  711 */     BigDecimal det = (this.parity == 1) ? ONE : ONE.negate();
/*  712 */     for (int i = 0; i < getRowDimension(); i++) {
/*  713 */       det = det.multiply(this.lu[i][i]);
/*      */     }
/*  715 */     return det;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSquare() {
/*  724 */     return (getColumnDimension() == getRowDimension());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSingular() {
/*  732 */     if (this.lu == null) {
/*      */       try {
/*  734 */         luDecompose();
/*  735 */         return false;
/*  736 */       } catch (InvalidMatrixException ex) {
/*  737 */         return true;
/*      */       } 
/*      */     }
/*  740 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getRowDimension() {
/*  750 */     return this.data.length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getColumnDimension() {
/*  759 */     return (this.data[0]).length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal getTrace() throws IllegalArgumentException {
/*  771 */     if (!isSquare()) {
/*  772 */       throw new IllegalArgumentException("matrix is not square");
/*      */     }
/*  774 */     BigDecimal trace = this.data[0][0];
/*  775 */     for (int i = 1; i < getRowDimension(); i++) {
/*  776 */       trace = trace.add(this.data[i][i]);
/*      */     }
/*  778 */     return trace;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal[] operate(BigDecimal[] v) throws IllegalArgumentException {
/*  789 */     if (v.length != getColumnDimension()) {
/*  790 */       throw new IllegalArgumentException("vector has wrong length");
/*      */     }
/*  792 */     int nRows = getRowDimension();
/*  793 */     int nCols = getColumnDimension();
/*  794 */     BigDecimal[] out = new BigDecimal[v.length];
/*  795 */     for (int row = 0; row < nRows; row++) {
/*  796 */       BigDecimal sum = ZERO;
/*  797 */       for (int i = 0; i < nCols; i++) {
/*  798 */         sum = sum.add(this.data[row][i].multiply(v[i]));
/*      */       }
/*  800 */       out[row] = sum;
/*      */     } 
/*  802 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal[] operate(double[] v) throws IllegalArgumentException {
/*  813 */     BigDecimal[] bd = new BigDecimal[v.length];
/*  814 */     for (int i = 0; i < bd.length; i++) {
/*  815 */       bd[i] = new BigDecimal(v[i]);
/*      */     }
/*  817 */     return operate(bd);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal[] preMultiply(BigDecimal[] v) throws IllegalArgumentException {
/*  828 */     int nRows = getRowDimension();
/*  829 */     if (v.length != nRows) {
/*  830 */       throw new IllegalArgumentException("vector has wrong length");
/*      */     }
/*  832 */     int nCols = getColumnDimension();
/*  833 */     BigDecimal[] out = new BigDecimal[nCols];
/*  834 */     for (int col = 0; col < nCols; col++) {
/*  835 */       BigDecimal sum = ZERO;
/*  836 */       for (int i = 0; i < nRows; i++) {
/*  837 */         sum = sum.add(this.data[i][col].multiply(v[i]));
/*      */       }
/*  839 */       out[col] = sum;
/*      */     } 
/*  841 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal[] solve(BigDecimal[] b) throws IllegalArgumentException, InvalidMatrixException {
/*  856 */     int nRows = getRowDimension();
/*  857 */     if (b.length != nRows) {
/*  858 */       throw new IllegalArgumentException("constant vector has wrong length");
/*      */     }
/*  860 */     BigMatrix bMatrix = new BigMatrixImpl(b);
/*  861 */     BigDecimal[][] solution = ((BigMatrixImpl)solve(bMatrix)).getDataRef();
/*  862 */     BigDecimal[] out = new BigDecimal[nRows];
/*  863 */     for (int row = 0; row < nRows; row++) {
/*  864 */       out[row] = solution[row][0];
/*      */     }
/*  866 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal[] solve(double[] b) throws IllegalArgumentException, InvalidMatrixException {
/*  881 */     BigDecimal[] bd = new BigDecimal[b.length];
/*  882 */     for (int i = 0; i < bd.length; i++) {
/*  883 */       bd[i] = new BigDecimal(b[i]);
/*      */     }
/*  885 */     return solve(bd);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigMatrix solve(BigMatrix b) throws IllegalArgumentException, InvalidMatrixException {
/*  900 */     if (b.getRowDimension() != getRowDimension()) {
/*  901 */       throw new IllegalArgumentException("Incorrect row dimension");
/*      */     }
/*  903 */     if (!isSquare()) {
/*  904 */       throw new InvalidMatrixException("coefficient matrix is not square");
/*      */     }
/*  906 */     if (isSingular()) {
/*  907 */       throw new InvalidMatrixException("Matrix is singular.");
/*      */     }
/*      */     
/*  910 */     int nCol = getColumnDimension();
/*  911 */     int nColB = b.getColumnDimension();
/*  912 */     int nRowB = b.getRowDimension();
/*      */ 
/*      */     
/*  915 */     BigDecimal[][] bp = new BigDecimal[nRowB][nColB];
/*  916 */     for (int row = 0; row < nRowB; row++) {
/*  917 */       for (int i = 0; i < nColB; i++) {
/*  918 */         bp[row][i] = b.getEntry(this.permutation[row], i);
/*      */       }
/*      */     } 
/*      */     
/*      */     int col;
/*  923 */     for (col = 0; col < nCol; col++) {
/*  924 */       for (int i = col + 1; i < nCol; i++) {
/*  925 */         for (int j = 0; j < nColB; j++) {
/*  926 */           bp[i][j] = bp[i][j].subtract(bp[col][j].multiply(this.lu[i][col]));
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  932 */     for (col = nCol - 1; col >= 0; col--) {
/*  933 */       for (int j = 0; j < nColB; j++) {
/*  934 */         bp[col][j] = bp[col][j].divide(this.lu[col][col], this.scale, this.roundingMode);
/*      */       }
/*  936 */       for (int i = 0; i < col; i++) {
/*  937 */         for (int k = 0; k < nColB; k++) {
/*  938 */           bp[i][k] = bp[i][k].subtract(bp[col][k].multiply(this.lu[i][col]));
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  943 */     BigMatrixImpl outMat = new BigMatrixImpl(bp);
/*  944 */     return outMat;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void luDecompose() throws InvalidMatrixException {
/*  967 */     int nRows = getRowDimension();
/*  968 */     int nCols = getColumnDimension();
/*  969 */     if (nRows != nCols) {
/*  970 */       throw new InvalidMatrixException("LU decomposition requires that the matrix be square.");
/*      */     }
/*  972 */     this.lu = getData();
/*      */ 
/*      */     
/*  975 */     this.permutation = new int[nRows];
/*  976 */     for (int row = 0; row < nRows; row++) {
/*  977 */       this.permutation[row] = row;
/*      */     }
/*  979 */     this.parity = 1;
/*      */ 
/*      */     
/*  982 */     for (int col = 0; col < nCols; col++) {
/*      */       
/*  984 */       BigDecimal sum = ZERO;
/*      */ 
/*      */       
/*  987 */       for (int i = 0; i < col; i++) {
/*  988 */         sum = this.lu[i][col];
/*  989 */         for (int k = 0; k < i; k++) {
/*  990 */           sum = sum.subtract(this.lu[i][k].multiply(this.lu[k][col]));
/*      */         }
/*  992 */         this.lu[i][col] = sum;
/*      */       } 
/*      */ 
/*      */       
/*  996 */       int max = col;
/*  997 */       BigDecimal largest = ZERO; int j;
/*  998 */       for (j = col; j < nRows; j++) {
/*  999 */         sum = this.lu[j][col];
/* 1000 */         for (int k = 0; k < col; k++) {
/* 1001 */           sum = sum.subtract(this.lu[j][k].multiply(this.lu[k][col]));
/*      */         }
/* 1003 */         this.lu[j][col] = sum;
/*      */ 
/*      */         
/* 1006 */         if (sum.abs().compareTo(largest) == 1) {
/* 1007 */           largest = sum.abs();
/* 1008 */           max = j;
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1013 */       if (this.lu[max][col].abs().compareTo(TOO_SMALL) <= 0) {
/* 1014 */         this.lu = (BigDecimal[][])null;
/* 1015 */         throw new InvalidMatrixException("matrix is singular");
/*      */       } 
/*      */ 
/*      */       
/* 1019 */       if (max != col) {
/* 1020 */         BigDecimal tmp = ZERO;
/* 1021 */         for (int k = 0; k < nCols; k++) {
/* 1022 */           tmp = this.lu[max][k];
/* 1023 */           this.lu[max][k] = this.lu[col][k];
/* 1024 */           this.lu[col][k] = tmp;
/*      */         } 
/* 1026 */         int temp = this.permutation[max];
/* 1027 */         this.permutation[max] = this.permutation[col];
/* 1028 */         this.permutation[col] = temp;
/* 1029 */         this.parity = -this.parity;
/*      */       } 
/*      */ 
/*      */       
/* 1033 */       for (j = col + 1; j < nRows; j++) {
/* 1034 */         this.lu[j][col] = this.lu[j][col].divide(this.lu[col][col], this.scale, this.roundingMode);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1046 */     StringBuffer res = new StringBuffer();
/* 1047 */     res.append("BigMatrixImpl{");
/* 1048 */     if (this.data != null) {
/* 1049 */       for (int i = 0; i < this.data.length; i++) {
/* 1050 */         if (i > 0)
/* 1051 */           res.append(","); 
/* 1052 */         res.append("{");
/* 1053 */         for (int j = 0; j < (this.data[0]).length; j++) {
/* 1054 */           if (j > 0)
/* 1055 */             res.append(","); 
/* 1056 */           res.append(this.data[i][j]);
/*      */         } 
/* 1058 */         res.append("}");
/*      */       } 
/*      */     }
/* 1061 */     res.append("}");
/* 1062 */     return res.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object object) {
/* 1075 */     if (object == this) {
/* 1076 */       return true;
/*      */     }
/* 1078 */     if (!(object instanceof BigMatrixImpl)) {
/* 1079 */       return false;
/*      */     }
/* 1081 */     BigMatrix m = (BigMatrix)object;
/* 1082 */     int nRows = getRowDimension();
/* 1083 */     int nCols = getColumnDimension();
/* 1084 */     if (m.getColumnDimension() != nCols || m.getRowDimension() != nRows) {
/* 1085 */       return false;
/*      */     }
/* 1087 */     for (int row = 0; row < nRows; row++) {
/* 1088 */       for (int col = 0; col < nCols; col++) {
/* 1089 */         if (!this.data[row][col].equals(m.getEntry(row, col))) {
/* 1090 */           return false;
/*      */         }
/*      */       } 
/*      */     } 
/* 1094 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1103 */     int ret = 7;
/* 1104 */     int nRows = getRowDimension();
/* 1105 */     int nCols = getColumnDimension();
/* 1106 */     ret = ret * 31 + nRows;
/* 1107 */     ret = ret * 31 + nCols;
/* 1108 */     for (int row = 0; row < nRows; row++) {
/* 1109 */       for (int col = 0; col < nCols; col++) {
/* 1110 */         ret = ret * 31 + (11 * (row + 1) + 17 * (col + 1)) * this.data[row][col].hashCode();
/*      */       }
/*      */     } 
/*      */     
/* 1114 */     return ret;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BigMatrix getIdentity(int dimension) {
/* 1126 */     BigMatrixImpl out = new BigMatrixImpl(dimension, dimension);
/* 1127 */     BigDecimal[][] d = out.getDataRef();
/* 1128 */     for (int row = 0; row < dimension; row++) {
/* 1129 */       for (int col = 0; col < dimension; col++) {
/* 1130 */         d[row][col] = (row == col) ? ONE : ZERO;
/*      */       }
/*      */     } 
/* 1133 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BigMatrix getLUMatrix() throws InvalidMatrixException {
/* 1164 */     if (this.lu == null) {
/* 1165 */       luDecompose();
/*      */     }
/* 1167 */     return new BigMatrixImpl(this.lu);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int[] getPermutation() {
/* 1183 */     int[] out = new int[this.permutation.length];
/* 1184 */     System.arraycopy(this.permutation, 0, out, 0, this.permutation.length);
/* 1185 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private BigDecimal[][] copyOut() {
/* 1196 */     int nRows = getRowDimension();
/* 1197 */     BigDecimal[][] out = new BigDecimal[nRows][getColumnDimension()];
/*      */     
/* 1199 */     for (int i = 0; i < nRows; i++) {
/* 1200 */       System.arraycopy(this.data[i], 0, out[i], 0, (this.data[i]).length);
/*      */     }
/* 1202 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void copyIn(BigDecimal[][] in) {
/* 1211 */     int nRows = in.length;
/* 1212 */     int nCols = (in[0]).length;
/* 1213 */     this.data = new BigDecimal[nRows][nCols];
/* 1214 */     System.arraycopy(in, 0, this.data, 0, in.length);
/* 1215 */     for (int i = 0; i < nRows; i++) {
/* 1216 */       System.arraycopy(in[i], 0, this.data[i], 0, nCols);
/*      */     }
/* 1218 */     this.lu = (BigDecimal[][])null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void copyIn(double[][] in) {
/* 1227 */     int nRows = in.length;
/* 1228 */     int nCols = (in[0]).length;
/* 1229 */     this.data = new BigDecimal[nRows][nCols];
/* 1230 */     for (int i = 0; i < nRows; i++) {
/* 1231 */       for (int j = 0; j < nCols; j++) {
/* 1232 */         this.data[i][j] = new BigDecimal(in[i][j]);
/*      */       }
/*      */     } 
/* 1235 */     this.lu = (BigDecimal[][])null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void copyIn(String[][] in) {
/* 1245 */     int nRows = in.length;
/* 1246 */     int nCols = (in[0]).length;
/* 1247 */     this.data = new BigDecimal[nRows][nCols];
/* 1248 */     for (int i = 0; i < nRows; i++) {
/* 1249 */       for (int j = 0; j < nCols; j++) {
/* 1250 */         this.data[i][j] = new BigDecimal(in[i][j]);
/*      */       }
/*      */     } 
/* 1253 */     this.lu = (BigDecimal[][])null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isValidCoordinate(int row, int col) {
/* 1264 */     int nRows = getRowDimension();
/* 1265 */     int nCols = getColumnDimension();
/*      */     
/* 1267 */     return (row >= 0 && row < nRows && col >= 0 && col < nCols);
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\linear\BigMatrixImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */