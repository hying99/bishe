/*    */ package org.jgap.gui;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jgap.data.config.ConfigData;
/*    */ import org.jgap.data.config.IConfigInfo;
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
/*    */ public class MockConfigInfoForTesting
/*    */   implements IConfigInfo
/*    */ {
/*    */   static final String CVS_REVISION = "$Revision: 1.1 $";
/*    */   private ConfigData m_conData;
/*    */   
/*    */   public MockConfigInfoForTesting() {
/* 33 */     this.m_conData = new ConfigData();
/*    */     
/* 35 */     this.m_conData.setNS("Configurable");
/*    */     
/* 37 */     this.m_conData.addTextData("text1", "text1_value");
/* 38 */     this.m_conData.addTextData("text2", "text2_value");
/* 39 */     this.m_conData.addTextData("text3", "text3_value");
/*    */ 
/*    */     
/* 42 */     String listName = "";
/* 43 */     for (int i = 0; i < 3; i++) {
/* 44 */       listName = "List" + (i + 1);
/* 45 */       List<String> listData = new ArrayList();
/* 46 */       listData.add(listName + "_value_1");
/* 47 */       listData.add(listName + "_value_2");
/* 48 */       listData.add(listName + "_value_3");
/* 49 */       this.m_conData.addListData(listName, listData);
/*    */     } 
/*    */   }
/*    */   
/*    */   public ConfigData getConfigData() {
/* 54 */     return this.m_conData;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getFileName() {
/* 63 */     return "jgapTmp.con";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gui\MockConfigInfoForTesting.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */