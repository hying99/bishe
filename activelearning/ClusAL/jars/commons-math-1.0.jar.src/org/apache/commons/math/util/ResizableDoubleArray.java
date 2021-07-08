/*     */ package org.apache.commons.math.util;
/*     */ 
/*     */ import java.io.Serializable;
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
/*     */ public class ResizableDoubleArray
/*     */   implements DoubleArray, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -3485529955529426875L;
/*     */   public static final int ADDITIVE_MODE = 1;
/*     */   public static final int MULTIPLICATIVE_MODE = 0;
/*  87 */   protected float contractionCriteria = 2.5F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  97 */   protected float expansionFactor = 2.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 103 */   protected int expansionMode = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 109 */   protected int initialCapacity = 16;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected double[] internalArray;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 120 */   protected int numElements = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 128 */   protected int startIndex = 0;
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
/*     */   public ResizableDoubleArray() {
/* 140 */     this.internalArray = new double[this.initialCapacity];
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
/*     */   public ResizableDoubleArray(int initialCapacity) {
/* 155 */     setInitialCapacity(initialCapacity);
/* 156 */     this.internalArray = new double[this.initialCapacity];
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
/*     */   public ResizableDoubleArray(int initialCapacity, float expansionFactor) {
/* 182 */     this.expansionFactor = expansionFactor;
/* 183 */     setInitialCapacity(initialCapacity);
/* 184 */     this.internalArray = new double[initialCapacity];
/* 185 */     setContractionCriteria(expansionFactor + 0.5F);
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
/*     */   public ResizableDoubleArray(int initialCapacity, float expansionFactor, float contractionCriteria) {
/* 209 */     this.expansionFactor = expansionFactor;
/* 210 */     setContractionCriteria(contractionCriteria);
/* 211 */     setInitialCapacity(initialCapacity);
/* 212 */     this.internalArray = new double[initialCapacity];
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
/*     */   public ResizableDoubleArray(int initialCapacity, float expansionFactor, float contractionCriteria, int expansionMode) {
/* 238 */     this.expansionFactor = expansionFactor;
/* 239 */     setContractionCriteria(contractionCriteria);
/* 240 */     setInitialCapacity(initialCapacity);
/* 241 */     setExpansionMode(expansionMode);
/* 242 */     this.internalArray = new double[initialCapacity];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void addElement(double value) {
/* 251 */     this.numElements++;
/* 252 */     if (this.startIndex + this.numElements > this.internalArray.length) {
/* 253 */       expand();
/*     */     }
/* 255 */     this.internalArray[this.startIndex + this.numElements - 1] = value;
/* 256 */     if (shouldContract()) {
/* 257 */       contract();
/*     */     }
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
/*     */   public synchronized double addElementRolling(double value) {
/* 278 */     double discarded = this.internalArray[this.startIndex];
/*     */     
/* 280 */     if (this.startIndex + this.numElements + 1 > this.internalArray.length) {
/* 281 */       expand();
/*     */     }
/*     */     
/* 284 */     this.startIndex++;
/*     */ 
/*     */     
/* 287 */     this.internalArray[this.startIndex + this.numElements - 1] = value;
/*     */ 
/*     */     
/* 290 */     if (shouldContract()) {
/* 291 */       contract();
/*     */     }
/* 293 */     return discarded;
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
/*     */   protected void checkContractExpand(float contractionCritera, float expansionFactor) {
/* 310 */     if (contractionCritera < expansionFactor) {
/* 311 */       String msg = "Contraction criteria can never be smaller than the expansion factor.  This would lead to a never ending loop of expansion and contraction as a newly expanded internal storage array would immediately satisfy the criteria for contraction";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 317 */       throw new IllegalArgumentException(msg);
/*     */     } 
/*     */     
/* 320 */     if (this.contractionCriteria <= 1.0D) {
/* 321 */       String msg = "The contraction criteria must be a number larger than one.  If the contractionCriteria is less than or equal to one an endless loop of contraction and expansion would ensue as an internalArray.length == numElements would satisfy the contraction criteria";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 327 */       throw new IllegalArgumentException(msg);
/*     */     } 
/*     */     
/* 330 */     if (expansionFactor <= 1.0D) {
/* 331 */       String msg = "The expansion factor must be a number greater than 1.0";
/*     */       
/* 333 */       throw new IllegalArgumentException(msg);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void clear() {
/* 342 */     this.numElements = 0;
/* 343 */     this.internalArray = new double[this.initialCapacity];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void contract() {
/* 352 */     double[] tempArray = new double[this.numElements + 1];
/*     */ 
/*     */     
/* 355 */     System.arraycopy(this.internalArray, this.startIndex, tempArray, 0, this.numElements);
/* 356 */     this.internalArray = tempArray;
/*     */ 
/*     */     
/* 359 */     this.startIndex = 0;
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
/*     */   public synchronized void discardFrontElements(int i) {
/* 373 */     if (i > this.numElements) {
/* 374 */       String msg = "Cannot discard more elements than arecontained in this array.";
/*     */       
/* 376 */       throw new IllegalArgumentException(msg);
/* 377 */     }  if (i < 0) {
/* 378 */       String msg = "Cannot discard a negative number of elements.";
/* 379 */       throw new IllegalArgumentException(msg);
/*     */     } 
/*     */     
/* 382 */     this.numElements -= i;
/* 383 */     this.startIndex += i;
/*     */     
/* 385 */     if (shouldContract()) {
/* 386 */       contract();
/*     */     }
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
/*     */   protected synchronized void expand() {
/* 405 */     int newSize = 0;
/* 406 */     if (this.expansionMode == 0) {
/* 407 */       newSize = (int)Math.ceil((this.internalArray.length * this.expansionFactor));
/*     */     } else {
/* 409 */       newSize = this.internalArray.length + Math.round(this.expansionFactor);
/*     */     } 
/* 411 */     double[] tempArray = new double[newSize];
/*     */ 
/*     */     
/* 414 */     System.arraycopy(this.internalArray, 0, tempArray, 0, this.internalArray.length);
/* 415 */     this.internalArray = tempArray;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized void expandTo(int size) {
/* 424 */     double[] tempArray = new double[size];
/*     */     
/* 426 */     System.arraycopy(this.internalArray, 0, tempArray, 0, this.internalArray.length);
/* 427 */     this.internalArray = tempArray;
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
/*     */   public float getContractionCriteria() {
/* 443 */     return this.contractionCriteria;
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
/*     */   public double getElement(int index) {
/* 455 */     double value = Double.NaN;
/* 456 */     if (index >= this.numElements) {
/* 457 */       String msg = "The index specified: " + index + " is larger than the current number of elements";
/*     */ 
/*     */       
/* 460 */       throw new ArrayIndexOutOfBoundsException(msg);
/* 461 */     }  if (index >= 0) {
/* 462 */       value = this.internalArray[this.startIndex + index];
/*     */     } else {
/* 464 */       String msg = "Elements cannot be retrieved from a negative array index";
/*     */       
/* 466 */       throw new ArrayIndexOutOfBoundsException(msg);
/*     */     } 
/* 468 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] getElements() {
/* 479 */     double[] elementArray = new double[this.numElements];
/* 480 */     System.arraycopy(this.internalArray, this.startIndex, elementArray, 0, this.numElements);
/*     */     
/* 482 */     return elementArray;
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
/*     */   public float getExpansionFactor() {
/* 498 */     return this.expansionFactor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getExpansionMode() {
/* 509 */     return this.expansionMode;
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
/*     */   int getInternalLength() {
/* 521 */     return this.internalArray.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumElements() {
/* 531 */     return this.numElements;
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
/*     */   public double[] getValues() {
/* 546 */     return this.internalArray;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContractionCriteria(float contractionCriteria) {
/* 555 */     checkContractExpand(contractionCriteria, getExpansionFactor());
/* 556 */     this.contractionCriteria = contractionCriteria;
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
/*     */   public synchronized void setElement(int index, double value) {
/* 573 */     if (index < 0) {
/* 574 */       String msg = "Cannot set an element at a negative index";
/* 575 */       throw new ArrayIndexOutOfBoundsException(msg);
/*     */     } 
/* 577 */     if (index + 1 > this.numElements) {
/* 578 */       this.numElements = index + 1;
/*     */     }
/* 580 */     if (this.startIndex + index >= this.internalArray.length) {
/* 581 */       expandTo(this.startIndex + index + 1);
/*     */     }
/* 583 */     this.internalArray[this.startIndex + index] = value;
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
/*     */   public void setExpansionFactor(float expansionFactor) {
/* 598 */     checkContractExpand(getContractionCriteria(), expansionFactor);
/*     */     
/* 600 */     this.expansionFactor = expansionFactor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExpansionMode(int expansionMode) {
/* 611 */     if (expansionMode != 0 && expansionMode != 1)
/*     */     {
/* 613 */       throw new IllegalArgumentException("Illegal expansionMode setting.");
/*     */     }
/* 615 */     this.expansionMode = expansionMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setInitialCapacity(int initialCapacity) {
/* 626 */     if (initialCapacity > 0) {
/* 627 */       this.initialCapacity = initialCapacity;
/*     */     } else {
/* 629 */       String msg = "The initial capacity supplied: " + initialCapacity + "must be a positive integer";
/*     */ 
/*     */       
/* 632 */       throw new IllegalArgumentException(msg);
/*     */     } 
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
/*     */   public synchronized void setNumElements(int i) {
/* 647 */     if (i < 0) {
/* 648 */       String msg = "Number of elements must be zero or a positive integer";
/*     */       
/* 650 */       throw new IllegalArgumentException(msg);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 655 */     if (this.startIndex + i > this.internalArray.length) {
/* 656 */       expandTo(this.startIndex + i);
/*     */     }
/*     */ 
/*     */     
/* 660 */     this.numElements = i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized boolean shouldContract() {
/* 670 */     if (this.expansionMode == 0) {
/* 671 */       return ((this.internalArray.length / this.numElements) > this.contractionCriteria);
/*     */     }
/* 673 */     return ((this.internalArray.length - this.numElements) > this.contractionCriteria);
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
/*     */   public int start() {
/* 687 */     return this.startIndex;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\mat\\util\ResizableDoubleArray.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */