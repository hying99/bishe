/*     */ package org.jgap.data;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public abstract class DocumentBuilderBase
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*     */   
/*     */   public Object buildDocument(IDataCreators a_dataholder, Object a_document) throws Exception {
/*  45 */     IDataElementList tree = a_dataholder.getTree();
/*  46 */     int len = tree.getLength();
/*     */     
/*  48 */     for (int i = 0; i < len; i++) {
/*  49 */       IDataElement elem = tree.item(i);
/*  50 */       doTraverse(elem, a_document, null);
/*     */     } 
/*  52 */     return a_document;
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
/*     */   private void doTraverse(IDataElement a_elem, Object a_document, Object a_Element) throws Exception {
/*  69 */     String tagName = a_elem.getTagName();
/*  70 */     Object element = createElementGeneric(a_document, a_Element, tagName);
/*  71 */     Map attributes = a_elem.getAttributes();
/*  72 */     Set keys = attributes.keySet();
/*  73 */     Iterator<String> it = keys.iterator();
/*     */     
/*  75 */     while (it.hasNext()) {
/*  76 */       String key = it.next();
/*  77 */       String value = (String)attributes.get(key);
/*  78 */       setAttribute(element, key, value);
/*     */     } 
/*  80 */     IDataElementList list = a_elem.getChildNodes();
/*  81 */     if (list != null) {
/*  82 */       for (int j = 0; j < list.getLength(); j++) {
/*  83 */         IDataElement elem2 = list.item(j);
/*  84 */         doTraverse(elem2, a_document, element);
/*     */       } 
/*     */     }
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
/*     */   private Object createElementGeneric(Object a_document, Object a_element, String a_tagName) {
/*     */     Object element;
/* 103 */     if (a_element == null) {
/* 104 */       element = createElement(a_document, null, a_tagName);
/* 105 */       documentAppendChild(a_document, element);
/*     */     } else {
/*     */       
/* 108 */       Object xmlElement2 = createElement(a_document, a_element, a_tagName);
/* 109 */       elementAppendChild(a_element, xmlElement2);
/* 110 */       element = xmlElement2;
/*     */     } 
/* 112 */     return element;
/*     */   }
/*     */   
/*     */   protected abstract Object documentAppendChild(Object paramObject1, Object paramObject2);
/*     */   
/*     */   protected abstract Object elementAppendChild(Object paramObject1, Object paramObject2);
/*     */   
/*     */   protected abstract Object createElement(Object paramObject1, Object paramObject2, String paramString);
/*     */   
/*     */   protected abstract void setAttribute(Object paramObject, String paramString1, String paramString2);
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\DocumentBuilderBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */