/*     */ package org.jgap.event;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.jgap.util.ICloneable;
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
/*     */ public class EventManager
/*     */   implements IEventManager, ICloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*  35 */   private HashMap m_listeners = new HashMap<Object, Object>();
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
/*     */   public synchronized void addEventListener(String a_eventName, GeneticEventListener a_eventListenerToAdd) {
/*  52 */     List<GeneticEventListener> eventListeners = (List)this.m_listeners.get(a_eventName);
/*  53 */     if (eventListeners == null) {
/*  54 */       eventListeners = new LinkedList();
/*  55 */       this.m_listeners.put(a_eventName, eventListeners);
/*     */     } 
/*  57 */     eventListeners.add(a_eventListenerToAdd);
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
/*     */   public synchronized void removeEventListener(String a_eventName, GeneticEventListener a_eventListenerToRemove) {
/*  75 */     List eventListeners = (List)this.m_listeners.get(a_eventName);
/*  76 */     if (eventListeners != null) {
/*  77 */       eventListeners.remove(a_eventListenerToRemove);
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
/*     */   public synchronized void fireGeneticEvent(GeneticEvent a_eventToFire) {
/*  92 */     List eventListeners = (List)this.m_listeners.get(a_eventToFire.getEventName());
/*     */     
/*  94 */     if (eventListeners != null) {
/*     */ 
/*     */       
/*  97 */       Iterator<GeneticEventListener> listenerIterator = eventListeners.iterator();
/*  98 */       while (listenerIterator.hasNext()) {
/*  99 */         ((GeneticEventListener)listenerIterator.next()).geneticEventFired(a_eventToFire);
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
/*     */   public int hashCode() {
/* 112 */     int result = this.m_listeners.hashCode();
/* 113 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 123 */     EventManager result = new EventManager();
/* 124 */     if (!this.m_listeners.isEmpty()) {
/* 125 */       result.m_listeners = (HashMap)this.m_listeners.clone();
/*     */     }
/* 127 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\event\EventManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */