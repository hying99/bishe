/*     */ package jeans.tree;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import jeans.util.Executer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IntervalTreeRB
/*     */ {
/*     */   public IntervalTreeNodeRB root;
/*     */   public IntervalTreeNodeRB nil;
/*     */   public int min;
/*     */   public int max;
/*     */   public int maxval;
/*     */   public Executer executer;
/*     */   
/*     */   public IntervalTreeRB() {
/*  36 */     this.nil = new IntervalTreeNodeRB();
/*  37 */     this.nil.setMinMax(-2147483648);
/*  38 */     this.nil.setAllNodes(this.nil);
/*  39 */     this.root = new IntervalTreeNodeRB();
/*  40 */     this.root.setMinMax(2147483647);
/*  41 */     this.root.setAllNodes(this.nil);
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
/*     */   public void leftRotate(IntervalTreeNodeRB x) {
/*  67 */     IntervalTreeNodeRB y = x.right;
/*  68 */     x.right = y.left;
/*  69 */     if (y.left != this.nil) y.left.parent = x;
/*     */     
/*  71 */     y.parent = x.parent;
/*     */ 
/*     */     
/*  74 */     if (x == x.parent.left) {
/*  75 */       x.parent.left = y;
/*     */     } else {
/*  77 */       x.parent.right = y;
/*     */     } 
/*  79 */     y.left = x;
/*  80 */     x.parent = y;
/*  81 */     x.maxHigh = Math.max(x.left.maxHigh, Math.max(x.right.maxHigh, x.high));
/*  82 */     y.maxHigh = Math.max(x.maxHigh, Math.max(y.right.maxHigh, y.high));
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
/*     */   public void rightRotate(IntervalTreeNodeRB y) {
/* 108 */     IntervalTreeNodeRB x = y.left;
/* 109 */     y.left = x.right;
/* 110 */     if (this.nil != x.right) x.right.parent = y;
/*     */ 
/*     */ 
/*     */     
/* 114 */     x.parent = y.parent;
/* 115 */     if (y == y.parent.left) {
/* 116 */       y.parent.left = x;
/*     */     } else {
/* 118 */       y.parent.right = x;
/*     */     } 
/* 120 */     x.right = y;
/* 121 */     y.parent = x;
/* 122 */     y.maxHigh = Math.max(y.left.maxHigh, Math.max(y.right.maxHigh, y.high));
/* 123 */     x.maxHigh = Math.max(x.left.maxHigh, Math.max(y.maxHigh, x.high));
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
/*     */   public void treeInsertHelp(IntervalTreeNodeRB z) {
/* 140 */     z.left = z.right = this.nil;
/* 141 */     IntervalTreeNodeRB y = this.root;
/* 142 */     IntervalTreeNodeRB x = this.root.left;
/* 143 */     while (x != this.nil) {
/* 144 */       y = x;
/* 145 */       if (x.key > z.key) {
/* 146 */         x = x.left; continue;
/*     */       } 
/* 148 */       x = x.right;
/*     */     } 
/*     */     
/* 151 */     z.parent = y;
/* 152 */     if (y == this.root || y.key > z.key) {
/*     */       
/* 154 */       y.left = z;
/*     */     } else {
/* 156 */       y.right = z;
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
/*     */   public void fixUpMaxHigh(IntervalTreeNodeRB x) {
/* 169 */     while (x != this.root) {
/* 170 */       x.maxHigh = Math.max(x.high, Math.max(x.left.maxHigh, x.right.maxHigh));
/* 171 */       x = x.parent;
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
/*     */   public IntervalTreeNodeRB insert(int min, int max, int value) {
/* 191 */     IntervalTreeNodeRB x = new IntervalTreeNodeRB(min, max, value);
/* 192 */     treeInsertHelp(x);
/* 193 */     fixUpMaxHigh(x.parent);
/* 194 */     IntervalTreeNodeRB newNode = x;
/* 195 */     x.red = true;
/* 196 */     while (x.parent.red) {
/* 197 */       if (x.parent == x.parent.parent.left) {
/* 198 */         IntervalTreeNodeRB intervalTreeNodeRB = x.parent.parent.right;
/* 199 */         if (intervalTreeNodeRB.red) {
/* 200 */           x.parent.red = false;
/* 201 */           intervalTreeNodeRB.red = false;
/* 202 */           x.parent.parent.red = true;
/* 203 */           x = x.parent.parent; continue;
/*     */         } 
/* 205 */         if (x == x.parent.right) {
/* 206 */           x = x.parent;
/* 207 */           leftRotate(x);
/*     */         } 
/* 209 */         x.parent.red = false;
/* 210 */         x.parent.parent.red = true;
/* 211 */         rightRotate(x.parent.parent);
/*     */ 
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 217 */       IntervalTreeNodeRB y = x.parent.parent.left;
/* 218 */       if (y.red) {
/* 219 */         x.parent.red = false;
/* 220 */         y.red = false;
/* 221 */         x.parent.parent.red = true;
/* 222 */         x = x.parent.parent; continue;
/*     */       } 
/* 224 */       if (x == x.parent.left) {
/* 225 */         x = x.parent;
/* 226 */         rightRotate(x);
/*     */       } 
/* 228 */       x.parent.red = false;
/* 229 */       x.parent.parent.red = true;
/* 230 */       leftRotate(x.parent.parent);
/*     */     } 
/*     */ 
/*     */     
/* 234 */     this.root.left.red = false;
/* 235 */     return newNode;
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
/*     */   IntervalTreeNodeRB getSuccessorOf(IntervalTreeNodeRB x) {
/*     */     IntervalTreeNodeRB y;
/* 248 */     if (this.nil != (y = x.right)) {
/* 249 */       while (y.left != this.nil) {
/* 250 */         y = y.left;
/*     */       }
/* 252 */       return y;
/*     */     } 
/* 254 */     y = x.parent;
/* 255 */     while (x == y.right) {
/* 256 */       x = y;
/* 257 */       y = y.parent;
/*     */     } 
/* 259 */     if (y == this.root) return this.nil; 
/* 260 */     return y;
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
/*     */   IntervalTreeNodeRB getPredecessorOf(IntervalTreeNodeRB x) {
/*     */     IntervalTreeNodeRB y;
/* 274 */     if (this.nil != (y = x.left)) {
/* 275 */       while (y.right != this.nil) {
/* 276 */         y = y.right;
/*     */       }
/* 278 */       return y;
/*     */     } 
/* 280 */     y = x.parent;
/* 281 */     while (x == y.left) {
/* 282 */       if (y == this.root) return this.nil; 
/* 283 */       x = y;
/* 284 */       y = y.parent;
/*     */     } 
/* 286 */     return y;
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
/*     */   public void deleteFixUp(IntervalTreeNodeRB x) {
/* 301 */     IntervalTreeNodeRB rootLeft = this.root.left;
/* 302 */     while (!x.red && rootLeft != x) {
/* 303 */       if (x == x.parent.left) {
/* 304 */         IntervalTreeNodeRB intervalTreeNodeRB = x.parent.right;
/* 305 */         if (intervalTreeNodeRB.red) {
/* 306 */           intervalTreeNodeRB.red = false;
/* 307 */           x.parent.red = true;
/* 308 */           leftRotate(x.parent);
/* 309 */           intervalTreeNodeRB = x.parent.right;
/*     */         } 
/* 311 */         if (!intervalTreeNodeRB.right.red && !intervalTreeNodeRB.left.red) {
/* 312 */           intervalTreeNodeRB.red = true;
/* 313 */           x = x.parent; continue;
/*     */         } 
/* 315 */         if (!intervalTreeNodeRB.right.red) {
/* 316 */           intervalTreeNodeRB.left.red = false;
/* 317 */           intervalTreeNodeRB.red = true;
/* 318 */           rightRotate(intervalTreeNodeRB);
/* 319 */           intervalTreeNodeRB = x.parent.right;
/*     */         } 
/* 321 */         intervalTreeNodeRB.red = x.parent.red;
/* 322 */         x.parent.red = false;
/* 323 */         intervalTreeNodeRB.right.red = false;
/* 324 */         leftRotate(x.parent);
/* 325 */         x = rootLeft;
/*     */         continue;
/*     */       } 
/* 328 */       IntervalTreeNodeRB w = x.parent.left;
/* 329 */       if (w.red) {
/* 330 */         w.red = false;
/* 331 */         x.parent.red = true;
/* 332 */         rightRotate(x.parent);
/* 333 */         w = x.parent.left;
/*     */       } 
/* 335 */       if (!w.right.red && !w.left.red) {
/* 336 */         w.red = true;
/* 337 */         x = x.parent; continue;
/*     */       } 
/* 339 */       if (!w.left.red) {
/* 340 */         w.right.red = false;
/* 341 */         w.red = true;
/* 342 */         leftRotate(w);
/* 343 */         w = x.parent.left;
/*     */       } 
/* 345 */       w.red = x.parent.red;
/* 346 */       x.parent.red = false;
/* 347 */       w.left.red = false;
/* 348 */       rightRotate(x.parent);
/* 349 */       x = rootLeft;
/*     */     } 
/*     */ 
/*     */     
/* 353 */     x.red = false;
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
/*     */   public void deleteNode(IntervalTreeNodeRB z) {
/* 369 */     IntervalTreeNodeRB y = (z.left == this.nil || z.right == this.nil) ? z : getSuccessorOf(z);
/* 370 */     IntervalTreeNodeRB x = (y.left == this.nil) ? y.right : y.left;
/* 371 */     if (this.root == (x.parent = y.parent)) {
/* 372 */       this.root.left = x;
/*     */     }
/* 374 */     else if (y == y.parent.left) {
/* 375 */       y.parent.left = x;
/*     */     } else {
/* 377 */       y.parent.right = x;
/*     */     } 
/*     */     
/* 380 */     if (y != z) {
/*     */       
/* 382 */       y.maxHigh = Integer.MIN_VALUE;
/* 383 */       y.left = z.left;
/* 384 */       y.right = z.right;
/* 385 */       y.parent = z.parent;
/* 386 */       z.right.parent = y;
/* 387 */       if (z == z.parent.left) {
/* 388 */         z.parent.left = y;
/*     */       } else {
/* 390 */         z.parent.right = y;
/*     */       } 
/* 392 */       fixUpMaxHigh(x.parent);
/* 393 */       if (!y.red) {
/* 394 */         y.red = z.red;
/* 395 */         deleteFixUp(x);
/*     */       } else {
/* 397 */         y.red = z.red;
/*     */       } 
/*     */     } else {
/* 400 */       fixUpMaxHigh(x.parent);
/* 401 */       if (!y.red) deleteFixUp(x);
/*     */     
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
/*     */   static boolean overlap(int a1, int a2, int b1, int b2) {
/* 414 */     if (a1 <= b1) {
/* 415 */       return (b1 <= a2);
/*     */     }
/* 417 */     return (a1 <= b2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void print(IntervalTreeNodeRB x) {
/* 428 */     if (x != this.nil) {
/* 429 */       print(x.left);
/* 430 */       x.print();
/* 431 */       print(x.right);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void print() {
/* 436 */     print(this.root.left);
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
/*     */   public boolean findOverlappingIntervals(IntervalTreeNodeRB x, ArrayList<IntervalTreeNodeRB> list) {
/* 451 */     if (x != this.nil) {
/* 452 */       if (overlap(this.min, this.max, x.key, x.high)) {
/* 453 */         list.add(x);
/* 454 */         if (x.left.maxHigh >= this.min) {
/* 455 */           if (findOverlappingIntervals(x.left, list)) {
/* 456 */             findOverlappingIntervals(x.right, list);
/*     */           }
/*     */         } else {
/* 459 */           findOverlappingIntervals(x.right, list);
/*     */         } 
/* 461 */         return true;
/*     */       } 
/* 463 */       if (x.left.maxHigh >= this.min) {
/* 464 */         if (findOverlappingIntervals(x.left, list)) {
/* 465 */           findOverlappingIntervals(x.right, list);
/* 466 */           return true;
/*     */         } 
/* 468 */         return false;
/*     */       } 
/*     */       
/* 471 */       return findOverlappingIntervals(x.right, list);
/*     */     } 
/*     */ 
/*     */     
/* 475 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList findOverlappingIntervals(int min, int max) {
/* 480 */     this.min = min;
/* 481 */     this.max = max;
/* 482 */     ArrayList list = new ArrayList();
/* 483 */     findOverlappingIntervals(this.root.left, list);
/* 484 */     return list;
/*     */   }
/*     */   
/*     */   public boolean findOverlappingIntervalsMax(IntervalTreeNodeRB x) {
/* 488 */     if (x != this.nil) {
/* 489 */       if (overlap(this.min, this.max, x.key, x.high)) {
/* 490 */         if (x.value > this.maxval) {
/* 491 */           this.maxval = x.value;
/*     */         }
/* 493 */         if (x.left.maxHigh >= this.min) {
/* 494 */           if (findOverlappingIntervalsMax(x.left)) {
/* 495 */             findOverlappingIntervalsMax(x.right);
/*     */           }
/*     */         } else {
/* 498 */           findOverlappingIntervalsMax(x.right);
/*     */         } 
/* 500 */         return true;
/*     */       } 
/* 502 */       if (x.left.maxHigh >= this.min) {
/* 503 */         if (findOverlappingIntervalsMax(x.left)) {
/* 504 */           findOverlappingIntervalsMax(x.right);
/* 505 */           return true;
/*     */         } 
/* 507 */         return false;
/*     */       } 
/*     */       
/* 510 */       return findOverlappingIntervalsMax(x.right);
/*     */     } 
/*     */ 
/*     */     
/* 514 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int findOverlappingIntervalsMax(int min, int max) {
/* 519 */     this.min = min;
/* 520 */     this.max = max;
/* 521 */     this.maxval = Integer.MIN_VALUE;
/* 522 */     findOverlappingIntervalsMax(this.root.left);
/* 523 */     return this.maxval;
/*     */   }
/*     */   
/*     */   public int findOverlappingIntervalsMax(int min, int max, int init) {
/* 527 */     this.min = min;
/* 528 */     this.max = max;
/* 529 */     this.maxval = init;
/* 530 */     findOverlappingIntervalsMax(this.root.left);
/* 531 */     return this.maxval;
/*     */   }
/*     */   
/*     */   public void findMaxRecursive(IntervalTreeNodeRB x) {
/* 535 */     if (x != this.nil) {
/* 536 */       if (x.value > this.maxval) {
/* 537 */         this.maxval = x.value;
/*     */       }
/* 539 */       findMaxRecursive(x.left);
/* 540 */       findMaxRecursive(x.right);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int findMax(int init) {
/* 545 */     this.maxval = init;
/* 546 */     findMaxRecursive(this.root.left);
/* 547 */     return this.maxval;
/*     */   }
/*     */   
/*     */   public void executeRecursive(IntervalTreeNodeRB x) {
/* 551 */     if (x != this.nil) {
/* 552 */       this.executer.execute(x);
/* 553 */       executeRecursive(x.left);
/* 554 */       executeRecursive(x.right);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void execute(Executer exec) {
/* 559 */     this.executer = exec;
/* 560 */     executeRecursive(this.root.left);
/*     */   }
/*     */   
/*     */   public IntervalTreeNodeRB findOverlappingInterval(int min, int max) {
/* 564 */     IntervalTreeNodeRB x = this.root.left;
/* 565 */     while (x != this.nil && !overlap(min, max, x.key, x.high)) {
/* 566 */       if (x.left.maxHigh >= min) {
/* 567 */         x = x.left; continue;
/*     */       } 
/* 569 */       x = x.right;
/*     */     } 
/*     */     
/* 572 */     return x;
/*     */   }
/*     */   
/*     */   public void addInterval(int min, int max, int value) {
/* 576 */     IntervalTreeNodeRB x = findOverlappingInterval(min, max);
/* 577 */     while (x != this.nil) {
/* 578 */       int xmin = x.key;
/* 579 */       int xmax = x.high;
/* 580 */       int xvalue = x.value;
/* 581 */       deleteNode(x);
/* 582 */       if (xmin < min) {
/* 583 */         insert(xmin, min - 1, xvalue);
/*     */       }
/* 585 */       if (xmax > max) {
/* 586 */         insert(max + 1, xmax, xvalue);
/*     */       }
/* 588 */       x = findOverlappingInterval(min, max);
/*     */     } 
/* 590 */     insert(min, max, value);
/*     */   }
/*     */   
/*     */   public int checkMaxHighFieldsHelper(IntervalTreeNodeRB y, int currentHigh, int match) {
/* 594 */     if (y != this.nil) {
/* 595 */       match = (checkMaxHighFieldsHelper(y.left, currentHigh, match) != 0) ? 1 : match;
/* 596 */       VERIFY("y.high <= currentHigh", (y.high <= currentHigh));
/* 597 */       if (y.high == currentHigh) match = 1; 
/* 598 */       match = (checkMaxHighFieldsHelper(y.right, currentHigh, match) != 0) ? 1 : match;
/*     */     } 
/* 600 */     return match;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkMaxHighFields(IntervalTreeNodeRB x) {
/* 606 */     if (x != this.nil) {
/* 607 */       checkMaxHighFields(x.left);
/* 608 */       if (checkMaxHighFieldsHelper(x, x.maxHigh, 0) <= 0) {
/* 609 */         System.out.println("Error found in CheckMaxHighFields.");
/*     */       }
/* 611 */       checkMaxHighFields(x.right);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void VERIFY(String descr, boolean val) {
/* 616 */     if (!val) {
/* 617 */       System.out.println("Assertion: '" + descr + "' fails.");
/*     */     }
/*     */   }
/*     */   
/*     */   public void checkAssumptions() {
/* 622 */     VERIFY("nil.key == Integer.MIN_VALUE", (this.nil.key == Integer.MIN_VALUE));
/* 623 */     VERIFY("nil.high == Integer.MIN_VALUE", (this.nil.high == Integer.MIN_VALUE));
/* 624 */     VERIFY("nil.maxHigh == Integer.MIN_VALUE", (this.nil.maxHigh == Integer.MIN_VALUE));
/* 625 */     VERIFY("root.key == Integer.MAX_VALUE", (this.root.key == Integer.MAX_VALUE));
/* 626 */     VERIFY("root.high == Integer.MAX_VALUE", (this.root.high == Integer.MAX_VALUE));
/* 627 */     VERIFY("root.maxHigh == Integer.MAX_VALUE", (this.root.maxHigh == Integer.MAX_VALUE));
/* 628 */     VERIFY("nil.red == 0", !this.nil.red);
/* 629 */     VERIFY("root.red == 0", !this.root.red);
/* 630 */     checkMaxHighFields(this.root.left);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\tree\IntervalTreeRB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */