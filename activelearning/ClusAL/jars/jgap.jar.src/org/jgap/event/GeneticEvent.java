/*     */ package org.jgap.event;
/*     */ 
/*     */ import java.util.EventObject;
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
/*     */ public class GeneticEvent
/*     */   extends EventObject
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*     */   private Object m_value;
/*     */   public static final String GENOTYPE_EVOLVED_EVENT = "genotype_evolved_event";
/*     */   public static final String GPGENOTYPE_EVOLVED_EVENT = "gpgenotype_evolved_event";
/*     */   public static final String GPGENOTYPE_NEW_BEST_SOLUTION = "gpgenotype_best_solution";
/*     */   private final String m_eventName;
/*     */   
/*     */   public GeneticEvent(String a_eventName, Object a_source) {
/*  65 */     super(a_source);
/*  66 */     this.m_eventName = a_eventName;
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
/*     */   
/*     */   public GeneticEvent(String a_eventName, Object a_source, Object a_value) {
/*  85 */     this(a_eventName, a_source);
/*  86 */     this.m_value = a_value;
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
/*     */   public String getEventName() {
/*  99 */     return this.m_eventName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getValue() {
/* 109 */     return this.m_value;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\event\GeneticEvent.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */