/*     */ package jeans.math.evaluate;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import jeans.math.MNumber;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Evaluator
/*     */ {
/*     */   public static final int INFIX = 0;
/*     */   public static final int PREFIX = 1;
/*     */   public static final int POSTFIX = 2;
/*     */   public static final int CONSTANT = 3;
/*     */   private int maxLevel;
/*  37 */   private Hashtable protoTypes = new Hashtable<>();
/*     */   
/*     */   public void addPrefixUnaryExpression(String name, int level, UnaryExpression function) {
/*  40 */     addPrototype(name, new ExpressionPrototype(level, 1, 1, function));
/*     */   }
/*     */   
/*     */   public void addPostfixUnaryExpression(String name, int level, UnaryExpression function) {
/*  44 */     addPrototype(name, new ExpressionPrototype(level, 1, 2, function));
/*     */   }
/*     */   
/*     */   public void addInfixBinaryExpression(String name, int level, BinaryExpression function) {
/*  48 */     addPrototype(name, new ExpressionPrototype(level, 2, 0, function));
/*     */   }
/*     */   
/*     */   public void addConstant(String name, MNumber value) {
/*  52 */     addPrototype(name, new ExpressionPrototype(0, 0, 3, new ValueExpression(value)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addPrototype(String name, ExpressionPrototype proto) {
/*  60 */     this.protoTypes.put(name, proto);
/*  61 */     this.maxLevel = Math.max(this.maxLevel, proto.getLevel());
/*     */   }
/*     */   
/*     */   public ExpressionPrototype getPrototype(String name) {
/*  65 */     return (ExpressionPrototype)this.protoTypes.get(name);
/*     */   }
/*     */   
/*     */   public int getMaxLevel() {
/*  69 */     return this.maxLevel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression evaluate(String strg) throws EvaluateException {
/* 167 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\math\evaluate\Evaluator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */