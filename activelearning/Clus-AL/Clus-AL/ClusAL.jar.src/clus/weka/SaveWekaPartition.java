/*     */ package clus.weka;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Random;
/*     */ import weka.classifiers.CostMatrix;
/*     */ import weka.classifiers.Evaluation;
/*     */ import weka.core.Instance;
/*     */ import weka.core.Instances;
/*     */ import weka.core.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SaveWekaPartition
/*     */   extends Evaluation
/*     */ {
/*     */   public SaveWekaPartition(Instances data, CostMatrix costMatrix) throws Exception {
/*  33 */     super(data, costMatrix);
/*     */   }
/*     */   
/*     */   public void saveXVAL(Instances train, int numFolds, Random rnd, String fname) throws Exception {
/*  37 */     Instances data = new Instances(train);
/*  38 */     data.randomize(rnd);
/*  39 */     if (data.classAttribute().isNominal()) {
/*  40 */       data.stratify(numFolds);
/*     */     }
/*  42 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname + ".folds")));
/*  43 */     for (int i = 0; i < numFolds; i++) {
/*  44 */       System.out.println("Fold: " + i);
/*  45 */       Instances test_cv = data.testCV(numFolds, i);
/*  46 */       for (int j = 0; j < test_cv.numInstances(); j++) {
/*  47 */         Instance in = test_cv.instance(j);
/*     */         
/*  49 */         if (j != 0) wrt.print(","); 
/*  50 */         wrt.print(in.toString(0));
/*     */       } 
/*  52 */       wrt.println();
/*     */     } 
/*  54 */     wrt.close();
/*     */   }
/*     */   
/*     */   public static void savePartition(String[] options) throws Exception {
/*  58 */     Instances train = null, template = null;
/*  59 */     int seed = 1, folds = 10, classIndex = -1;
/*     */     
/*  61 */     BufferedReader trainReader = null;
/*  62 */     CostMatrix costMatrix = null;
/*     */     
/*  64 */     String classIndexString = Utils.getOption('c', options);
/*  65 */     if (classIndexString.length() != 0) {
/*  66 */       classIndex = Integer.parseInt(classIndexString);
/*     */     }
/*  68 */     String trainFileName = Utils.getOption('t', options);
/*  69 */     if (trainFileName.length() == 0) {
/*  70 */       throw new Exception("No training file and no object input file given.");
/*     */     }
/*  72 */     trainReader = new BufferedReader(new FileReader(trainFileName));
/*  73 */     template = train = new Instances(trainReader);
/*  74 */     if (classIndex != -1) {
/*  75 */       if (classIndex > train.numAttributes()) {
/*  76 */         throw new Exception("Index of class attribute too large.");
/*     */       }
/*  78 */       train.setClassIndex(classIndex - 1);
/*     */     } else {
/*  80 */       train.setClassIndex(train.numAttributes() - 1);
/*     */     } 
/*  82 */     String seedString = Utils.getOption('s', options);
/*  83 */     if (seedString.length() != 0) {
/*  84 */       seed = Integer.parseInt(seedString);
/*     */     }
/*  86 */     String foldsString = Utils.getOption('x', options);
/*  87 */     if (foldsString.length() != 0) {
/*  88 */       folds = Integer.parseInt(foldsString);
/*     */     }
/*  90 */     costMatrix = handleCostOption(Utils.getOption('m', options), template.numClasses());
/*  91 */     Utils.checkForRemainingOptions(options);
/*  92 */     SaveWekaPartition eval = new SaveWekaPartition(new Instances(template, 0), costMatrix);
/*  93 */     Random rnd = new Random(seed);
/*  94 */     eval.saveXVAL(train, folds, rnd, trainFileName + "." + seed);
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/*  99 */       savePartition(args);
/* 100 */     } catch (Exception ex) {
/* 101 */       System.err.println(ex.getMessage());
/* 102 */       ex.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\weka\SaveWekaPartition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */