/*    */ package org.jgap.xml;
/*    */ 
/*    */ import javax.xml.parsers.DocumentBuilder;
/*    */ import javax.xml.parsers.DocumentBuilderFactory;
/*    */ import org.jgap.data.DocumentBuilderBase;
/*    */ import org.jgap.data.IDataCreators;
/*    */ import org.w3c.dom.Document;
/*    */ import org.w3c.dom.Element;
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
/*    */ public class XMLDocumentBuilder
/*    */   extends DocumentBuilderBase
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*    */   
/*    */   protected void setAttribute(Object a_xmlElement, String a_key, String a_value) {
/* 30 */     ((Element)a_xmlElement).setAttribute(a_key, a_value);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Object documentAppendChild(Object a_xmlDocument, Object a_element) {
/* 35 */     return ((Document)a_xmlDocument).appendChild((Element)a_element);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Object elementAppendChild(Object a_xmlElement, Object a_element) {
/* 40 */     return ((Element)a_xmlElement).appendChild((Element)a_element);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Object createElement(Object a_doc, Object a_xmlElement, String a_tagName) {
/* 45 */     return ((Document)a_doc).createElement(a_tagName);
/*    */   }
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
/*    */   public Object buildDocument(IDataCreators a_document) throws Exception {
/* 61 */     DocumentBuilder m_documentCreator = DocumentBuilderFactory.newInstance().newDocumentBuilder();
/*    */     
/* 63 */     Document xmlDoc = m_documentCreator.newDocument();
/* 64 */     return buildDocument(a_document, xmlDoc);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\xml\XMLDocumentBuilder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */