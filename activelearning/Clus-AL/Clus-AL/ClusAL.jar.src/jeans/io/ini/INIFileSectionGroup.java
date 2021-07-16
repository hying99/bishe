/*    */ package jeans.io.ini;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ import java.util.Vector;
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
/*    */ public class INIFileSectionGroup
/*    */   extends INIFileNode
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/* 32 */   protected Vector m_hSections = new Vector();
/*    */   protected INIFileSection m_hPrototype;
/*    */   
/*    */   public INIFileSectionGroup(String name) {
/* 36 */     super(name);
/*    */   }
/*    */   
/*    */   public boolean isSectionGroup() {
/* 40 */     return true;
/*    */   }
/*    */   
/*    */   public boolean isSection() {
/* 44 */     return false;
/*    */   }
/*    */   
/*    */   public void setPrototype(INIFileSection sec) {
/* 48 */     this.m_hPrototype = sec;
/*    */   }
/*    */   
/*    */   public INIFileSection getPrototype() {
/* 52 */     return this.m_hPrototype;
/*    */   }
/*    */   
/*    */   public INIFileNode cloneNode() {
/* 56 */     INIFileSectionGroup sec = new INIFileSectionGroup(getName());
/* 57 */     sec.setPrototype((INIFileSection)getPrototype().cloneNode());
/* 58 */     return sec;
/*    */   }
/*    */   
/*    */   public int getNbSections() {
/* 62 */     return this.m_hSections.size();
/*    */   }
/*    */   
/*    */   public INIFileSection getSectionAt(int idx) {
/* 66 */     return this.m_hSections.elementAt(idx);
/*    */   }
/*    */   
/*    */   public void addSection(INIFileSection section) {
/* 70 */     this.m_hSections.addElement(section);
/* 71 */     section.setParent(this);
/*    */   }
/*    */   
/*    */   public void save(PrintWriter writer) throws IOException {
/* 75 */     String groupName = getName();
/* 76 */     for (int idx = 0; idx < getNbSections(); idx++) {
/* 77 */       INIFileSection section = getSectionAt(idx);
/* 78 */       section.save(groupName, writer);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\ini\INIFileSectionGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */