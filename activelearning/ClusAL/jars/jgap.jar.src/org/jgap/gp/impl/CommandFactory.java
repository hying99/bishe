/*    */ package org.jgap.gp.impl;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.gp.CommandGene;
/*    */ import org.jgap.gp.function.Pop;
/*    */ import org.jgap.gp.function.Push;
/*    */ import org.jgap.gp.function.ReadTerminal;
/*    */ import org.jgap.gp.function.StoreTerminal;
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
/*    */ public class CommandFactory
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*    */   
/*    */   public static CommandGene[] createStoreCommands(CommandGene[] a_target, GPConfiguration a_conf, Class a_type, String a_prefix, int a_count) throws InvalidConfigurationException {
/* 32 */     CommandGene[] result = new CommandGene[a_count * 2 + a_target.length]; int i;
/* 33 */     for (i = 0; i < a_target.length; i++) {
/* 34 */       result[i] = a_target[i];
/*    */     }
/* 36 */     for (i = 0; i < a_count; i++) {
/* 37 */       result[i * 2 + a_target.length] = (CommandGene)new StoreTerminal(a_conf, a_prefix + i, a_type);
/*    */       
/* 39 */       result[i * 2 + 1 + a_target.length] = (CommandGene)new ReadTerminal(a_conf, a_type, a_prefix + i);
/*    */     } 
/*    */ 
/*    */     
/* 43 */     return result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CommandGene[] createWriteOnlyCommands(CommandGene[] a_target, GPConfiguration a_conf, Class a_type, String a_prefix, int a_count, boolean a_noValidation) throws InvalidConfigurationException {
/* 52 */     CommandGene[] result = new CommandGene[a_count + a_target.length]; int i;
/* 53 */     for (i = 0; i < a_target.length; i++) {
/* 54 */       result[i] = a_target[i];
/*    */     }
/* 56 */     for (i = 0; i < a_count; i++) {
/* 57 */       StoreTerminal storeTerminal = new StoreTerminal(a_conf, a_prefix + i, a_type);
/*    */       
/* 59 */       storeTerminal.setNoValidation(a_noValidation);
/* 60 */       result[i + a_target.length] = (CommandGene)storeTerminal;
/*    */     } 
/* 62 */     return result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CommandGene[] createReadOnlyCommands(CommandGene[] a_target, GPConfiguration a_conf, Class a_type, String a_prefix, int a_count, int a_startIndex, boolean a_noValidation) throws InvalidConfigurationException {
/* 72 */     CommandGene[] result = new CommandGene[a_count + a_target.length]; int i;
/* 73 */     for (i = 0; i < a_target.length; i++) {
/* 74 */       result[i] = a_target[i];
/*    */     }
/* 76 */     for (i = 0; i < a_count; i++) {
/* 77 */       ReadTerminal readTerminal = new ReadTerminal(a_conf, a_type, a_prefix + (i + a_startIndex));
/*    */       
/* 79 */       readTerminal.setNoValidation(a_noValidation);
/* 80 */       result[i + a_target.length] = (CommandGene)readTerminal;
/*    */     } 
/* 82 */     return result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CommandGene[] createStackCommands(CommandGene[] a_target, GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/* 89 */     CommandGene[] result = new CommandGene[a_target.length + 2];
/* 90 */     for (int i = 0; i < a_target.length; i++) {
/* 91 */       result[i] = a_target[i];
/*    */     }
/* 93 */     result[a_target.length] = (CommandGene)new Push(a_conf, a_type);
/* 94 */     result[a_target.length + 1] = (CommandGene)new Pop(a_conf, a_type);
/* 95 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\CommandFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */