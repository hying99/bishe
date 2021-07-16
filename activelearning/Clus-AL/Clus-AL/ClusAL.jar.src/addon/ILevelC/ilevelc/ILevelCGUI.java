/*     */ package addon.ILevelC.ilevelc;
/*     */ 
/*     */ import clus.data.io.ARFFFile;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.ext.ilevelc.DerivedConstraintsComputer;
/*     */ import clus.ext.ilevelc.ILevelConstraint;
/*     */ import clus.util.ClusException;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import jeans.util.FileUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ILevelCGUI
/*     */   extends JFrame
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final double LOGSIZE = 500.0D;
/*     */   protected JComboBox m_Combo;
/*     */   protected JComboBox m_Class;
/*     */   protected ILevelCComponent m_Canvas;
/*     */   protected ClusSchema m_Schema;
/*     */   protected JButton m_Load;
/*     */   protected JButton m_Save;
/*     */   protected JButton m_Closure;
/*     */   protected JFileChooser m_Chooser;
/*     */   protected NominalAttrType m_ClassAttr;
/*     */   
/*     */   public ILevelCGUI() throws ClusException, IOException {
/*  53 */     super("ILevelCGUI");
/*  54 */     this.m_Chooser = new JFileChooser();
/*  55 */     this.m_Chooser.setCurrentDirectory(new File("."));
/*  56 */     this.m_Schema = new ClusSchema("2DData");
/*  57 */     this.m_Schema.addAttrType((ClusAttrType)new NumericAttrType("X"));
/*  58 */     this.m_Schema.addAttrType((ClusAttrType)new NumericAttrType("Y"));
/*  59 */     this.m_Schema.addAttrType((ClusAttrType)(this.m_ClassAttr = new NominalAttrType("CLASS", 2)));
/*  60 */     this.m_ClassAttr.setValue(0, "pos");
/*  61 */     this.m_ClassAttr.setValue(1, "neg");
/*  62 */     this.m_Schema.initialize();
/*  63 */     JPanel panel = new JPanel();
/*  64 */     JPanel top = new JPanel();
/*  65 */     JPanel bot = new JPanel();
/*  66 */     top.setLayout(new BorderLayout(3, 3));
/*  67 */     bot.setLayout(new BorderLayout(3, 3));
/*  68 */     top.add(this.m_Combo = new JComboBox(), "Center");
/*  69 */     top.add(this.m_Load = new JButton("Load"), "West");
/*  70 */     top.add(this.m_Save = new JButton("Save"), "East");
/*  71 */     bot.add(this.m_Class = new JComboBox(), "West");
/*  72 */     bot.add(this.m_Closure = new JButton("Compute Closure"), "East");
/*  73 */     this.m_Save.addActionListener(new SaveListener());
/*  74 */     this.m_Load.addActionListener(new LoadListener());
/*  75 */     this.m_Closure.addActionListener(new ClosureListener());
/*  76 */     panel.setLayout(new BorderLayout());
/*  77 */     this.m_Combo.addItem("Add object");
/*  78 */     this.m_Combo.addItem("Delete object");
/*  79 */     this.m_Combo.addItem("Add must link");
/*  80 */     this.m_Combo.addItem("Add cannot link");
/*  81 */     this.m_Combo.setSelectedIndex(0);
/*  82 */     this.m_Class.addItem("pos");
/*  83 */     this.m_Class.addItem("neg");
/*  84 */     this.m_Class.setSelectedIndex(0);
/*  85 */     panel.add(top, "North");
/*  86 */     panel.add(this.m_Canvas = new ILevelCComponent(), "Center");
/*  87 */     panel.add(bot, "South");
/*  88 */     this.m_Canvas.addMouseListener(new ILevelMouseListener());
/*  89 */     setContentPane(panel);
/*     */   }
/*     */   
/*     */   public int getClassValue() {
/*  93 */     return this.m_Class.getSelectedIndex();
/*     */   }
/*     */   
/*     */   public JFileChooser getFileOpen() {
/*  97 */     return this.m_Chooser;
/*     */   }
/*     */   
/*     */   public ClusSchema getSchema() {
/* 101 */     return this.m_Schema;
/*     */   }
/*     */   
/*     */   public class ILevelCComponent
/*     */     extends JComponent
/*     */   {
/*     */     public static final long serialVersionUID = 1L;
/* 108 */     public int m_FirstIndex = -1;
/* 109 */     public ArrayList m_Points = new ArrayList();
/* 110 */     public ArrayList m_Constraints = new ArrayList();
/*     */     
/*     */     public void computeClosure() {
/* 113 */       DerivedConstraintsComputer comp = new DerivedConstraintsComputer(this.m_Points, this.m_Constraints);
/* 114 */       comp.compute();
/* 115 */       repaint();
/*     */     }
/*     */     
/*     */     public void indexPoints() {
/* 119 */       for (int i = 0; i < this.m_Points.size(); i++) {
/* 120 */         DataTuple tuple = this.m_Points.get(i);
/* 121 */         tuple.setIndex(i);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void load() {
/* 126 */       int returnVal = ILevelCGUI.this.getFileOpen().showOpenDialog(this);
/* 127 */       if (returnVal == 0) {
/* 128 */         String fname = ILevelCGUI.this.getFileOpen().getSelectedFile().getAbsolutePath();
/*     */         try {
/* 130 */           RowData data = ARFFFile.readArff(fname);
/* 131 */           this.m_Points = data.toArrayList();
/* 132 */           indexPoints();
/* 133 */           this.m_Constraints.clear();
/* 134 */           String mname = FileUtil.getName(fname) + ".ilevelc";
/* 135 */           ILevelConstraint.loadConstraints(mname, this.m_Constraints, this.m_Points);
/* 136 */           repaint();
/* 137 */         } catch (Exception e) {
/* 138 */           System.out.println("Error saving: " + fname);
/* 139 */           System.out.println("Exception: " + e);
/* 140 */           e.printStackTrace();
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     public void save() {
/* 146 */       int returnVal = ILevelCGUI.this.getFileOpen().showSaveDialog(this);
/* 147 */       if (returnVal == 0) {
/* 148 */         String fname = ILevelCGUI.this.getFileOpen().getSelectedFile().getAbsolutePath();
/*     */         try {
/* 150 */           RowData data = new RowData(this.m_Points, ILevelCGUI.this.getSchema());
/* 151 */           ARFFFile.writeArff(fname, data);
/* 152 */           String mname = FileUtil.getName(fname);
/* 153 */           PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(mname + ".ilevelc")));
/* 154 */           indexPoints();
/* 155 */           wrt.println("TYPE,T1,T2");
/* 156 */           for (int i = 0; i < this.m_Constraints.size(); i++) {
/* 157 */             ILevelConstraint ic = this.m_Constraints.get(i);
/* 158 */             int type = ic.getType();
/* 159 */             DataTuple t1 = ic.getT1();
/* 160 */             DataTuple t2 = ic.getT2();
/* 161 */             wrt.println(String.valueOf(type) + "," + t1.getIndex() + "," + t2.getIndex());
/*     */           } 
/* 163 */           wrt.close();
/* 164 */         } catch (Exception e) {
/* 165 */           System.out.println("Error saving: " + fname);
/* 166 */           System.out.println("Exception: " + e);
/* 167 */           e.printStackTrace();
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     public int getClosestPoint(int x, int y) {
/* 173 */       int select = -1;
/* 174 */       double mindist = Double.POSITIVE_INFINITY;
/* 175 */       for (int i = 0; i < this.m_Points.size(); i++) {
/* 176 */         DataTuple tuple = this.m_Points.get(i);
/* 177 */         double ptx = tuple.getDoubleVal(0);
/* 178 */         double pty = tuple.getDoubleVal(1);
/* 179 */         double dist = Math.sqrt((ptx - x) * (ptx - x) + (pty - y) * (pty - y));
/* 180 */         if (dist < mindist) {
/* 181 */           mindist = dist;
/* 182 */           select = i;
/*     */         } 
/*     */       } 
/* 185 */       return select;
/*     */     }
/*     */     
/*     */     public boolean isIn(int x, int y) {
/* 189 */       for (int i = 0; i < this.m_Points.size(); i++) {
/* 190 */         DataTuple tuple = this.m_Points.get(i);
/* 191 */         double ptx = tuple.getDoubleVal(0);
/* 192 */         double pty = tuple.getDoubleVal(1);
/* 193 */         if (ptx == x && pty == y) return true; 
/*     */       } 
/* 195 */       return false;
/*     */     }
/*     */     
/*     */     public void paintComponent(Graphics g) {
/* 199 */       Dimension dim = getSize(); int i;
/* 200 */       for (i = 0; i < this.m_Points.size(); i++) {
/* 201 */         DataTuple tuple = this.m_Points.get(i);
/* 202 */         int cls = tuple.getIntVal(0);
/* 203 */         if (cls == 0) { g.setColor(Color.blue); }
/* 204 */         else { g.setColor(Color.black); }
/* 205 */          g.fillOval((int)(tuple.getDoubleVal(0) * dim.getWidth() / 500.0D) - 3, (int)(tuple.getDoubleVal(1) * dim.getHeight() / 500.0D) - 3, 6, 6);
/*     */       } 
/* 207 */       for (i = 0; i < this.m_Constraints.size(); i++) {
/* 208 */         ILevelConstraint ic = this.m_Constraints.get(i);
/* 209 */         int type = ic.getType();
/* 210 */         DataTuple t1 = ic.getT1();
/* 211 */         DataTuple t2 = ic.getT2();
/* 212 */         if (type == 0) { g.setColor(Color.green); }
/* 213 */         else { g.setColor(Color.red); }
/* 214 */          g.drawLine((int)(t1.getDoubleVal(0) * dim.getWidth() / 500.0D), (int)(t1.getDoubleVal(1) * dim.getHeight() / 500.0D), (int)(t2.getDoubleVal(0) * dim.getWidth() / 500.0D), (int)(t2.getDoubleVal(1) * dim.getHeight() / 500.0D));
/*     */       } 
/*     */     }
/*     */     
/*     */     public void addPoint(int x, int y) {
/* 219 */       int option = ILevelCGUI.this.m_Combo.getSelectedIndex();
/* 220 */       Dimension dim = getSize();
/* 221 */       x = (int)Math.floor(500.0D * x / dim.getWidth());
/* 222 */       y = (int)Math.floor(500.0D * y / dim.getHeight());
/* 223 */       if (option == 0 && !isIn(x, y)) {
/* 224 */         DataTuple tuple = new DataTuple(ILevelCGUI.this.m_Schema);
/* 225 */         tuple.setDoubleVal(x, 0);
/* 226 */         tuple.setDoubleVal(y, 1);
/* 227 */         tuple.setIntVal(ILevelCGUI.this.getClassValue(), 0);
/* 228 */         this.m_Points.add(tuple);
/* 229 */       } else if (option == 1) {
/* 230 */         int sel = getClosestPoint(x, y);
/* 231 */         if (sel != -1) {
/* 232 */           this.m_Points.remove(sel);
/*     */         }
/* 234 */       } else if (option == 2 || option == 3) {
/* 235 */         int sel = getClosestPoint(x, y);
/* 236 */         if (this.m_FirstIndex == -1) {
/* 237 */           this.m_FirstIndex = sel;
/*     */         } else {
/* 239 */           DataTuple t2 = this.m_Points.get(sel);
/* 240 */           DataTuple t1 = this.m_Points.get(this.m_FirstIndex);
/* 241 */           ILevelConstraint cns = new ILevelConstraint(t1, t2, (option == 2) ? 0 : 1);
/* 242 */           this.m_Constraints.add(cns);
/* 243 */           this.m_FirstIndex = -1;
/*     */         } 
/*     */       } 
/* 246 */       repaint();
/*     */     }
/*     */   }
/*     */   
/*     */   public class SaveListener
/*     */     implements ActionListener {
/*     */     public void actionPerformed(ActionEvent e) {
/* 253 */       ILevelCGUI.this.m_Canvas.save();
/*     */     }
/*     */   }
/*     */   
/*     */   public class LoadListener
/*     */     implements ActionListener {
/*     */     public void actionPerformed(ActionEvent e) {
/* 260 */       ILevelCGUI.this.m_Canvas.load();
/*     */     }
/*     */   }
/*     */   
/*     */   public class ClosureListener
/*     */     implements ActionListener {
/*     */     public void actionPerformed(ActionEvent e) {
/* 267 */       ILevelCGUI.this.m_Canvas.computeClosure();
/*     */     }
/*     */   }
/*     */   
/*     */   public class ILevelMouseListener
/*     */     extends MouseAdapter {
/*     */     public void mousePressed(MouseEvent e) {
/* 274 */       ILevelCGUI.this.m_Canvas.addPoint((int)e.getPoint().getX(), (int)e.getPoint().getY());
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/* 280 */       ILevelCGUI gui = new ILevelCGUI();
/* 281 */       gui.setSize(new Dimension(800, 600));
/* 282 */       gui.setVisible(true);
/* 283 */     } catch (ClusException e) {
/* 284 */       System.out.println("Exception: " + e);
/* 285 */       e.printStackTrace();
/* 286 */     } catch (IOException e) {
/* 287 */       System.out.println("IOException: " + e);
/* 288 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\addon\ILevelC\ilevelc\ILevelCGUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */