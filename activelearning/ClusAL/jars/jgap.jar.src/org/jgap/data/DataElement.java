/*    */ package org.jgap.data;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ public class DataElement
/*    */   implements IDataElement
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*    */   private IDataElementList m_elements;
/*    */   private Map m_attributes;
/*    */   private String m_tagName;
/*    */   
/*    */   public DataElement(String a_tagName) {
/* 33 */     this.m_elements = new DataElementList();
/* 34 */     this.m_attributes = new HashMap<Object, Object>();
/* 35 */     this.m_tagName = a_tagName;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setAttribute(String a_name, String a_value) throws Exception {
/* 40 */     this.m_attributes.put(a_name, a_value);
/*    */   }
/*    */ 
/*    */   
/*    */   public void appendChild(IDataElement a_newChild) throws Exception {
/* 45 */     this.m_elements.add(a_newChild);
/*    */   }
/*    */   
/*    */   public String getTagName() {
/* 49 */     return this.m_tagName;
/*    */   }
/*    */   
/*    */   public IDataElementList getElementsByTagName(String a_name) {
/* 53 */     IDataElementList ret = new DataElementList();
/* 54 */     for (int i = 0; i < this.m_elements.getLength(); i++) {
/* 55 */       if (this.m_elements.item(i).getTagName().equals(a_name)) {
/* 56 */         ret.add(this.m_elements.item(i));
/*    */       }
/*    */     } 
/* 59 */     return ret;
/*    */   }
/*    */   
/*    */   public short getNodeType() {
/* 63 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getNodeValue() throws Exception {
/* 68 */     return null;
/*    */   }
/*    */   
/*    */   public IDataElementList getChildNodes() {
/* 72 */     return this.m_elements;
/*    */   }
/*    */   
/*    */   public String getAttribute(String a_name) {
/* 76 */     return (String)this.m_attributes.get(a_name);
/*    */   }
/*    */   
/*    */   public Map getAttributes() {
/* 80 */     return this.m_attributes;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\DataElement.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */