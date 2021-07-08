/*    */ package clus.model.pmml.tildepmml;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import java.util.Vector;
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
/*    */ public class MiningSchema
/*    */ {
/*    */   private int nbOfFields;
/*    */   private Vector miningFields;
/*    */   
/*    */   public MiningSchema(Vector $miningFields) {
/* 35 */     this.nbOfFields = $miningFields.size();
/* 36 */     this.miningFields = $miningFields;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNbOfFields() {
/* 43 */     return this.nbOfFields;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void addMiningField(MiningField $miningField) {
/* 49 */     this.miningFields.add($miningField);
/* 50 */     this.nbOfFields++;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public MiningField getMiningFieldAt(int index) {
/* 56 */     return this.miningFields.elementAt(index);
/*    */   }
/*    */ 
/*    */   
/*    */   public void print(PrintWriter outStream) {
/* 61 */     boolean empty1 = true;
/* 62 */     int counter = 0;
/*    */     
/* 64 */     if (this.nbOfFields > 0) {
/* 65 */       outStream.write("<MiningSchema>\n");
/* 66 */       empty1 = false;
/*    */     } 
/*    */     
/* 69 */     while (this.nbOfFields > 0) {
/* 70 */       ((MiningField)this.miningFields.elementAt(counter)).print(outStream);
/* 71 */       this.nbOfFields--;
/* 72 */       counter++;
/*    */     } 
/* 74 */     if (!empty1) outStream.write("</MiningSchema>\n"); 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\pmml\tildepmml\MiningSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */