/*     */ package org.jgap.gp.impl;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import org.jgap.gp.function.Add;
/*     */ import org.jgap.gp.function.Add3;
/*     */ import org.jgap.gp.function.AddAndStore;
/*     */ import org.jgap.gp.function.And;
/*     */ import org.jgap.gp.function.Cosine;
/*     */ import org.jgap.gp.function.Equals;
/*     */ import org.jgap.gp.function.Exp;
/*     */ import org.jgap.gp.function.ForLoop;
/*     */ import org.jgap.gp.function.If;
/*     */ import org.jgap.gp.function.IfElse;
/*     */ import org.jgap.gp.function.Increment;
/*     */ import org.jgap.gp.function.Multiply;
/*     */ import org.jgap.gp.function.Multiply3;
/*     */ import org.jgap.gp.function.Not;
/*     */ import org.jgap.gp.function.Or;
/*     */ import org.jgap.gp.function.Pow;
/*     */ import org.jgap.gp.function.ReadTerminal;
/*     */ import org.jgap.gp.function.Sine;
/*     */ import org.jgap.gp.function.StoreTerminal;
/*     */ import org.jgap.gp.function.SubProgram;
/*     */ import org.jgap.gp.function.Subtract;
/*     */ import org.jgap.gp.function.TransferMemory;
/*     */ import org.jgap.gp.function.Xor;
/*     */ import org.jgap.util.tree.TreeBranchRenderer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JGAPTreeBranchRenderer
/*     */   implements TreeBranchRenderer
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*     */   
/*     */   public Color getBranchColor(Object a_node, int a_level) {
/*  41 */     String name = ((JGAPTreeNode)a_node).getName();
/*  42 */     Color out = Color.white;
/*  43 */     if (name.equals(IfElse.class.getName())) {
/*  44 */       out = new Color(255, 30, 30);
/*     */     }
/*  46 */     else if (name.equals(Add.class.getName())) {
/*  47 */       out = new Color(86, 140, 0);
/*     */     }
/*  49 */     else if (name.equals(AddAndStore.class.getName())) {
/*  50 */       out = new Color(44, 200, 70);
/*     */     }
/*  52 */     else if (name.equals(Add3.class.getName())) {
/*  53 */       out = new Color(0, 86, 22);
/*     */     }
/*  55 */     else if (name.equals(Subtract.class.getName())) {
/*  56 */       out = new Color(171, 0, 0);
/*     */     }
/*  58 */     else if (name.equals(Multiply.class.getName())) {
/*  59 */       out = new Color(85, 0, 85);
/*     */     }
/*  61 */     else if (name.equals(Multiply3.class.getName())) {
/*  62 */       out = new Color(0, 190, 171);
/*     */     }
/*  64 */     else if (name.equals(Equals.class.getName())) {
/*  65 */       out = new Color(0, 0, 255);
/*     */     }
/*  67 */     else if (name.equals(Or.class.getName())) {
/*  68 */       out = new Color(20, 200, 40);
/*     */     }
/*  70 */     else if (name.equals(Xor.class.getName())) {
/*  71 */       out = new Color(10, 150, 80);
/*     */     }
/*  73 */     else if (name.equals(And.class.getName())) {
/*  74 */       out = new Color(90, 100, 90);
/*     */     }
/*  76 */     else if (name.equals(If.class.getName())) {
/*  77 */       out = new Color(200, 250, 100);
/*     */     }
/*  79 */     else if (name.equals(Not.class.getName())) {
/*  80 */       out = new Color(240, 50, 0);
/*     */     }
/*  82 */     else if (name.equals(Sine.class.getName())) {
/*  83 */       out = new Color(50, 10, 0);
/*     */     }
/*  85 */     else if (name.equals(Cosine.class.getName())) {
/*  86 */       out = new Color(50, 200, 0);
/*     */     }
/*  88 */     else if (name.equals(Exp.class.getName())) {
/*  89 */       out = new Color(200, 0, 50);
/*     */     }
/*  91 */     else if (name.equals(Pow.class.getName())) {
/*  92 */       out = new Color(100, 50, 150);
/*     */     }
/*  94 */     else if (name.equals(SubProgram.class.getName())) {
/*  95 */       out = new Color(33, 66, 99);
/*     */     }
/*  97 */     else if (name.equals(StoreTerminal.class.getName())) {
/*  98 */       out = new Color(100, 40, 200);
/*     */     }
/* 100 */     else if (name.equals(ReadTerminal.class.getName())) {
/* 101 */       out = new Color(200, 80, 100);
/*     */     }
/* 103 */     else if (name.equals(TransferMemory.class.getName())) {
/* 104 */       out = new Color(100, 200, 40);
/*     */     }
/* 106 */     else if (name.equals(ForLoop.class.getName())) {
/* 107 */       out = new Color(77, 240, 110);
/*     */     }
/* 109 */     else if (name.equals(Increment.class.getName())) {
/* 110 */       out = new Color(150, 150, 40);
/*     */     } 
/* 112 */     return out;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\JGAPTreeBranchRenderer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */