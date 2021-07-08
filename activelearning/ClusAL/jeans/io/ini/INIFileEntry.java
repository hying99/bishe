/*    */ package jeans.io.ini;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ import jeans.util.MStreamTokenizer;
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
/*    */ public abstract class INIFileEntry
/*    */   extends INIFileNode
/*    */ {
/*    */   protected String m_Name;
/*    */   
/*    */   public INIFileEntry(String name) {
/* 34 */     super(name);
/*    */   }
/*    */   
/*    */   public boolean isSectionGroup() {
/* 38 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isSection() {
/* 42 */     return false;
/*    */   }
/*    */   
/*    */   public abstract String getStringValue();
/*    */   
/*    */   public abstract void setValue(String paramString) throws IOException;
/*    */   
/*    */   public void build(MStreamTokenizer tokens) throws IOException {
/* 50 */     String line = tokens.readTillEol();
/* 51 */     if (line == null || line.equals("")) {
/* 52 */       throw new IOException("Expected value for entry '" + getName() + "'" + tokens.getFileNameForErrorMsg());
/*    */     }
/* 54 */     setValue(line.trim());
/*    */   }
/*    */   
/*    */   public void save(PrintWriter writer) throws IOException {
/* 58 */     writer.println(getName() + " = " + getStringValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\ini\INIFileEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */