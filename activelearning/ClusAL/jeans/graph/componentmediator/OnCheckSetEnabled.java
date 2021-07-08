/*    */ package jeans.graph.componentmediator;
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
/*    */ class OnCheckSetEnabled
/*    */   implements ComponentAction
/*    */ {
/*    */   private ComponentWrapper source;
/*    */   private String group;
/*    */   private boolean invert;
/*    */   
/*    */   public OnCheckSetEnabled(ComponentWrapper source, String group, boolean invert) {
/* 32 */     this.source = source;
/* 33 */     this.group = group;
/* 34 */     this.invert = invert;
/*    */   }
/*    */   
/*    */   public void execute(ComponentMediator mediator) {
/* 38 */     boolean enable = mediator.isCheck(this.source);
/* 39 */     if (this.invert) enable = !enable; 
/* 40 */     mediator.setEnabled(this.group, enable);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\componentmediator\OnCheckSetEnabled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */