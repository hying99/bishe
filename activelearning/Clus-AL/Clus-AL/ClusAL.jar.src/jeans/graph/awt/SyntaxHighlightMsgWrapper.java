/*    */ package jeans.graph.awt;
/*    */ 
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import jeans.graph.SyntaxHighlighter;
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
/*    */ public class SyntaxHighlightMsgWrapper
/*    */   extends MessageWrapper
/*    */ {
/*    */   protected SyntaxHighlighter m_lighter;
/*    */   
/*    */   public SyntaxHighlightMsgWrapper(String msg, SyntaxHighlighter lighter) {
/* 34 */     super(msg);
/* 35 */     this.m_lighter = lighter;
/*    */   }
/*    */   
/*    */   public void drawWrapped(Graphics g, int xpos, int ypos) {
/* 39 */     int nb = getNbLines();
/* 40 */     if (nb != 0) {
/* 41 */       boolean done = false;
/* 42 */       int txtHi = getHeight() / nb;
/* 43 */       int xp = xpos;
/* 44 */       int crline = 0;
/* 45 */       int crpos = 0;
/* 46 */       this.m_lighter.parseString(this.m_msg);
/* 47 */       FontMetrics fm = g.getFontMetrics();
/* 48 */       while (!done) {
/* 49 */         String token = this.m_lighter.getColorToken();
/* 50 */         if (token == null) {
/* 51 */           done = true; continue;
/*    */         } 
/* 53 */         g.setColor(this.m_lighter.getColor());
/* 54 */         int len = token.length();
/* 55 */         while (len > 0 && crline < nb) {
/* 56 */           if (crpos < getLoBoundary(crline)) {
/* 57 */             int delta = getLoBoundary(crline) - crpos;
/* 58 */             token = token.substring(delta);
/* 59 */             len -= delta;
/* 60 */             crpos += delta;
/*    */           } 
/* 62 */           if (crpos + len > getHiBoundary(crline)) {
/* 63 */             int delta = getHiBoundary(crline) - crpos;
/* 64 */             g.drawString(token.substring(0, delta), xp, ypos);
/* 65 */             token = token.substring(delta);
/* 66 */             ypos += txtHi;
/* 67 */             xp = xpos;
/* 68 */             crline++;
/* 69 */             len -= delta;
/* 70 */             crpos += delta; continue;
/*    */           } 
/* 72 */           g.drawString(token, xp, ypos);
/* 73 */           xp += fm.stringWidth(token);
/* 74 */           crpos += len;
/* 75 */           len = 0;
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\awt\SyntaxHighlightMsgWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */