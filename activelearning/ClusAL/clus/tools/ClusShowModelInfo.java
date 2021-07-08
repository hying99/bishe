/*    */ package clus.tools;
/*    */ 
/*    */ import clus.error.ClusError;
/*    */ import clus.error.ClusErrorList;
/*    */ import clus.model.ClusModel;
/*    */ import clus.model.ClusModelInfo;
/*    */ import clus.model.modelio.ClusModelCollectionIO;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClusShowModelInfo
/*    */ {
/*    */   public static void main(String[] args) {
/* 37 */     if (args.length != 1) {
/* 38 */       System.out.println("Usage: java clus.tools.ClusShowModelInfo somefile.model");
/* 39 */       System.exit(1);
/*    */     } 
/*    */     try {
/* 42 */       PrintWriter output = new PrintWriter(System.out);
/* 43 */       ClusModelCollectionIO dot_model_file = ClusModelCollectionIO.load(args[0]);
/* 44 */       int nb_models = dot_model_file.getNbModels();
/* 45 */       output.println("This .model file contains: " + nb_models + " models.");
/*    */       
/* 47 */       for (int i = 0; i < 1; i++) {
/* 48 */         ClusModelInfo model_info = dot_model_file.getModelInfo(i);
/* 49 */         ClusModel model = model_info.getModel();
/* 50 */         output.println("Model: " + i + ", Name: " + model_info.getName());
/* 51 */         output.println("Size: " + model.getModelSize());
/* 52 */         output.println();
/* 53 */         model.printModel(output);
/* 54 */         ClusErrorList train_error = model_info.getTrainingError();
/* 55 */         output.println();
/* 56 */         if (train_error != null) {
/* 57 */           output.println("Training Error:");
/* 58 */           train_error.showError(output);
/*    */         } else {
/* 60 */           output.println("No Training Error Available");
/*    */         } 
/* 62 */         ClusErrorList test_error = model_info.getTestError();
/* 63 */         output.println();
/* 64 */         if (test_error != null) {
/* 65 */           output.println("Testing Error:");
/* 66 */           test_error.showError(output);
/*    */           
/* 68 */           ClusError err = test_error.getErrorByName("Classification Error");
/* 69 */           if (err != null) {
/* 70 */             for (int j = 0; j < err.getDimension(); j++) {
/* 71 */               output.println("Target: " + j + " error: " + err.getModelErrorComponent(j));
/*    */             }
/*    */           }
/*    */         } else {
/* 75 */           output.println("No Testing Error Available");
/*    */         } 
/*    */       } 
/* 78 */       output.flush();
/* 79 */     } catch (IOException e) {
/* 80 */       System.err.println("IO Error: " + e.getMessage());
/* 81 */     } catch (ClassNotFoundException e) {
/* 82 */       System.err.println("Error: " + e.getMessage());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\tools\ClusShowModelInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */