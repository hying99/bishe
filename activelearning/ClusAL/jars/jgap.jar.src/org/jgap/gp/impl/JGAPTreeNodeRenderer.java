/*    */ package org.jgap.gp.impl;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import org.jgap.gp.terminal.Constant;
/*    */ import org.jgap.gp.terminal.False;
/*    */ import org.jgap.gp.terminal.NOP;
/*    */ import org.jgap.gp.terminal.Terminal;
/*    */ import org.jgap.gp.terminal.True;
/*    */ import org.jgap.gp.terminal.Variable;
/*    */ import org.jgap.util.tree.TreeNodeRenderer;
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
/*    */ public class JGAPTreeNodeRenderer
/*    */   implements TreeNodeRenderer
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*    */   
/*    */   public Color getNodeColor(Object a_node, int a_level) {
/*    */     Color out;
/* 42 */     String name = ((JGAPTreeNode)a_node).getName();
/*    */     
/* 44 */     if (name.equals(Constant.class.getName()))
/* 45 */     { out = Color.orange; }
/*    */     
/* 47 */     else if (name.equals(Variable.class.getName()))
/* 48 */     { out = Color.green; }
/*    */     
/* 50 */     else if (name.equals(Terminal.class.getName()))
/* 51 */     { out = Color.yellow; }
/*    */     
/* 53 */     else if (name.equals(NOP.class.getName()))
/* 54 */     { out = new Color(255, 255, 255); }
/*    */     
/* 56 */     else if (name.equals(True.class.getName()))
/* 57 */     { out = Color.blue; }
/*    */     
/* 59 */     else if (name.equals(False.class.getName()))
/* 60 */     { out = Color.gray; }
/*    */     else
/*    */     
/* 63 */     { switch (a_level)
/*    */       { case 0:
/* 65 */           out = Color.orange;
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
/*    */ 
/*    */           
/* 92 */           return out;case 1: out = new Color(240, 200, 100); return out;case 2: out = new Color(200, 140, 80); return out;case 3: out = new Color(140, 240, 180); return out;case 4: out = new Color(140, 180, 220); return out; }  if (a_level <= 7) { int amt = (8 - a_level) * 32; if (amt >= 256) amt = 255;  out = new Color(amt, amt, amt); } else { out = Color.black; }  }  return out;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\JGAPTreeNodeRenderer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */