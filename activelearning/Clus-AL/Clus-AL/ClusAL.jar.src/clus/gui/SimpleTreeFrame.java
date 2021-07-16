/*    */ package clus.gui;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.model.modelio.tilde.TildeOutReader;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.event.WindowListener;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ import jeans.graph.WindowClosingListener;
/*    */ import jeans.io.ObjectLoadStream;
/*    */ import jeans.util.FileUtil;
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
/*    */ public class SimpleTreeFrame
/*    */   extends JFrame
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   public static final int INITIAL_WD = 800;
/*    */   public static final int INITIAL_HI = 600;
/*    */   TreePanel m_TreePanel;
/*    */   
/*    */   public SimpleTreeFrame(String title, TreePanel tpanel) {
/* 48 */     super(title);
/* 49 */     this.m_TreePanel = tpanel;
/* 50 */     JPanel panel = new JPanel();
/* 51 */     panel.setLayout(new BorderLayout(3, 3));
/* 52 */     panel.add(this.m_TreePanel, "Center");
/* 53 */     setContentPane(panel);
/*    */   }
/*    */   
/*    */   public void init() {
/* 57 */     pack();
/* 58 */     setSize(800, 600);
/*    */   }
/*    */   
/*    */   public static SimpleTreeFrame createFrame(String title, ClusNode root) {
/* 62 */     String[] lines = new String[0];
/* 63 */     TreePanel tpanel = new TreePanel(root, lines);
/* 64 */     SimpleTreeFrame frame = new SimpleTreeFrame(title, tpanel);
/* 65 */     frame.init();
/* 66 */     return frame;
/*    */   }
/*    */   
/*    */   public static SimpleTreeFrame loadTree(InputStream strm) throws IOException, ClassNotFoundException {
/* 70 */     ObjectLoadStream open = new ObjectLoadStream(strm);
/* 71 */     ClusSchema schema = (ClusSchema)open.readObject();
/* 72 */     ClusNode root = (ClusNode)open.readObject();
/* 73 */     open.close();
/* 74 */     return createFrame(schema.getRelationName(), root);
/*    */   }
/*    */   
/*    */   public static SimpleTreeFrame loadTildeTree(InputStream strm) throws IOException, ClassNotFoundException {
/* 78 */     TildeOutReader reader = new TildeOutReader(strm);
/* 79 */     reader.doParse();
/* 80 */     ClusNode root = reader.getTree();
/* 81 */     reader.close();
/* 82 */     return createFrame("TildeTree", root);
/*    */   }
/*    */   
/*    */   public static SimpleTreeFrame showTree(String fname) throws IOException, ClassNotFoundException {
/*    */     SimpleTreeFrame frame;
/* 87 */     if (FileUtil.getExtension(fname).equals("out")) {
/* 88 */       frame = loadTildeTree(new FileInputStream(fname));
/*    */     } else {
/* 90 */       frame = loadTree(new FileInputStream(fname));
/*    */     } 
/* 92 */     frame.addWindowListener((WindowListener)new WindowClosingListener(frame, 0));
/* 93 */     frame.setVisible(true);
/* 94 */     return frame;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\gui\SimpleTreeFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */