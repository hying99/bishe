/*    */ package clus.gui.statvis;
/*    */ 
/*    */ import clus.statistic.ClassificationStat;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import java.awt.Color;
/*    */ import jeans.graph.plot.MDistrInfo;
/*    */ import jeans.graph.swing.drawable.Drawable;
/*    */ import jeans.graph.swing.drawable.DrawableDistrGraph;
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
/*    */ public class ClassStatVis
/*    */   implements ClusStatVisualizer, MDistrInfo
/*    */ {
/* 34 */   public static final Color[] m_Colors = new Color[] { Color.red, Color.yellow, Color.cyan, Color.blue, Color.green, Color.white, Color.black };
/*    */   
/*    */   ClassificationStat m_Stat;
/*    */ 
/*    */   
/*    */   public ClassStatVis() {}
/*    */   
/*    */   public ClassStatVis(ClassificationStat stat) {
/* 42 */     this.m_Stat = stat;
/*    */   }
/*    */   
/*    */   public Drawable createInstance(ClusStatistic stat) {
/* 46 */     ClassStatVis sv = new ClassStatVis((ClassificationStat)stat);
/* 47 */     return (Drawable)new DrawableDistrGraph(0, 0, sv, (float)stat.m_SumWeight);
/*    */   }
/*    */   
/*    */   public int getNbBins() {
/* 51 */     return this.m_Stat.getNbClasses(0);
/*    */   }
/*    */   
/*    */   public float getBinCount(int idx) {
/* 55 */     return (float)this.m_Stat.getCount(0, idx);
/*    */   }
/*    */   
/*    */   public Color getBinColor(int idx) {
/* 59 */     return m_Colors[idx % m_Colors.length];
/*    */   }
/*    */   
/*    */   public static Color getBinColorStatic(int idx) {
/* 63 */     return m_Colors[idx % m_Colors.length];
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\gui\statvis\ClassStatVis.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */