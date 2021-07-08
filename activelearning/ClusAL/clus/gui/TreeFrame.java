/*     */ package clus.gui;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.model.modelio.ClusModelCollectionIO;
/*     */ import clus.util.ClusException;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.TreeMap;
/*     */ import javax.swing.DefaultListModel;
/*     */ import javax.swing.JCheckBoxMenuItem;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuBar;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.event.TreeSelectionEvent;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import jeans.graph.PercentLayout;
/*     */ import jeans.graph.WindowClosingListener;
/*     */ import jeans.util.FileUtil;
/*     */ 
/*     */ public class TreeFrame extends JFrame {
/*  54 */   public static final int[] m_Permute = new int[] { 1, 2, 0 }; public static final long serialVersionUID = 1L; public static final int INITIAL_WD = 800;
/*     */   public static final int INITIAL_HI = 600;
/*  56 */   double m_VPerc = 0.75D;
/*  57 */   double m_HPerc = 0.8D; JSplitPane m_HSplit; JSplitPane m_VSplit;
/*     */   JSplitPane m_HRSplit;
/*     */   ShowHierarchy m_ShowHier;
/*     */   TreePanel m_TreePanel;
/*     */   JTextField m_Stat;
/*     */   JTextArea m_Info;
/*     */   JSlider m_Fac;
/*     */   JSlider m_Sig;
/*     */   JCheckBoxMenuItem m_Rel;
/*     */   JCheckBoxMenuItem m_Horz;
/*     */   JTree m_Tree;
/*     */   DefaultTreeModel m_TreeModel;
/*     */   DefaultMutableTreeNode m_Root;
/*     */   JList m_DSList;
/*     */   DefaultListModel m_DSListModel;
/*  72 */   JTextArea m_TextArea = new JTextArea(); JMenuItem m_Find; JMenuItem m_Open;
/*     */   JMenuItem m_ShowSett;
/*  74 */   JFileChooser m_FileChoose = new JFileChooser();
/*  75 */   TreeMap m_Files = new TreeMap<>();
/*     */   
/*     */   public TreeFrame(String title, TreePanel tpanel, ShowHierarchy sh) {
/*  78 */     super(title);
/*  79 */     this.m_ShowHier = sh;
/*  80 */     this.m_TreePanel = tpanel;
/*  81 */     JPanel panel = new JPanel();
/*     */ 
/*     */ 
/*     */     
/*  85 */     panel.setLayout(new BorderLayout(3, 3));
/*  86 */     this.m_TextArea.setEditable(false);
/*  87 */     this.m_HSplit = new JSplitPane(0, this.m_TreePanel, this.m_TextArea);
/*  88 */     this.m_VSplit = new JSplitPane(1, this.m_HSplit, makeRightPanel());
/*  89 */     panel.add(this.m_VSplit, "Center");
/*     */     
/*  91 */     setContentPane(panel);
/*  92 */     setJMenuBar(createMenu());
/*  93 */     addComponentListener(new MyResizeListener());
/*  94 */     this.m_FileChoose.setCurrentDirectory(FileUtil.getCurrentDir());
/*     */   }
/*     */   
/*     */   public JMenuBar createMenu() {
/*  98 */     JMenuBar menu = new JMenuBar();
/*  99 */     JMenu file = new JMenu("File");
/*     */ 
/*     */     
/* 102 */     file.add(this.m_Find = new JMenuItem("Find Data Sets"));
/* 103 */     this.m_Find.addActionListener(new MyFindListener());
/* 104 */     JMenu sett = new JMenu("Settings");
/* 105 */     sett.add(this.m_Horz = new JCheckBoxMenuItem("Horizontal"));
/* 106 */     this.m_Horz.addActionListener(new MyHorzListener());
/* 107 */     this.m_Horz.setSelected(true);
/* 108 */     sett.add(this.m_ShowSett = new JMenuItem("Show settings"));
/* 109 */     this.m_ShowSett.addActionListener(new MyShowSettingsListener());
/* 110 */     menu.add(file);
/* 111 */     menu.add(sett);
/* 112 */     return menu;
/*     */   }
/*     */   
/*     */   public void init() {
/* 116 */     pack();
/* 117 */     setSize(800, 600);
/*     */   }
/*     */   
/*     */   public void setDividers(int wd, int hi) {
/* 121 */     int vval = (int)(this.m_VPerc * wd);
/* 122 */     int hval = (int)(this.m_HPerc * hi);
/* 123 */     this.m_VSplit.setDividerLocation(vval);
/* 124 */     this.m_HSplit.setDividerLocation(hval);
/* 125 */     this.m_HRSplit.setDividerLocation((int)(0.25D * hi));
/*     */   }
/*     */   
/*     */   public JPanel makeRightPanel() {
/* 129 */     JPanel panel = new JPanel();
/* 130 */     if (this.m_ShowHier != null) {
/* 131 */       panel.setLayout((LayoutManager)new PercentLayout("100% p p p p", 3, 15, true));
/* 132 */       panel.add(this.m_ShowHier);
/*     */ 
/*     */       
/* 135 */       panel.add(new JLabel("Scale factor"));
/* 136 */       panel.add(this.m_Fac = new JSlider(0, 0, 100, 10));
/* 137 */       this.m_Fac.addChangeListener(new MyScaleListener());
/* 138 */       panel.add(new JLabel("Significance level"));
/* 139 */       panel.add(this.m_Sig = new JSlider(0, 0, 100, 50));
/* 140 */       this.m_Sig.addChangeListener(new MySignificanceListener());
/*     */     } else {
/* 142 */       this.m_Root = new DefaultMutableTreeNode("Root");
/* 143 */       this.m_TreeModel = new DefaultTreeModel(this.m_Root);
/* 144 */       this.m_Tree = new JTree(this.m_TreeModel);
/*     */ 
/*     */       
/* 147 */       this.m_Tree.getSelectionModel().setSelectionMode(1);
/* 148 */       this.m_Tree.addTreeSelectionListener(new MyFileTreeListener());
/* 149 */       JScrollPane treepane = new JScrollPane(this.m_Tree);
/* 150 */       this.m_DSListModel = new DefaultListModel();
/* 151 */       this.m_DSList = new JList(this.m_DSListModel);
/* 152 */       this.m_DSList.setSelectionMode(0);
/* 153 */       this.m_DSList.addListSelectionListener(new MyListListener());
/* 154 */       JScrollPane listpane = new JScrollPane(this.m_DSList);
/* 155 */       this.m_HRSplit = new JSplitPane(0, listpane, treepane);
/* 156 */       panel.setLayout((LayoutManager)new PercentLayout("100%", 3, 15, true));
/* 157 */       panel.add(this.m_HRSplit);
/*     */     } 
/* 159 */     return panel;
/*     */   }
/*     */   
/*     */   public JPanel makeBottomPanel() {
/* 163 */     JPanel panel = new JPanel();
/*     */     
/* 165 */     JPanel spanel = new JPanel();
/* 166 */     spanel.setLayout((LayoutManager)new PercentLayout("p 100%", 3, 15, false));
/* 167 */     spanel.add(new JLabel("Statistic: "));
/* 168 */     spanel.add(this.m_Stat = new JTextField());
/* 169 */     this.m_Stat.setEditable(false);
/*     */     
/* 171 */     JPanel ipanel = new JPanel();
/* 172 */     ipanel.setLayout((LayoutManager)new PercentLayout("p 100%", 3, 15, false));
/* 173 */     JPanel sub = new JPanel();
/* 174 */     sub.setLayout((LayoutManager)new PercentLayout("p 100%d", 3, 0, true));
/* 175 */     sub.add(new JLabel("Info: "));
/* 176 */     ipanel.add(sub);
/* 177 */     ipanel.add(this.m_Info = new JTextArea());
/* 178 */     this.m_Info.setEditable(false);
/* 179 */     this.m_Info.setFont(new Font("MonoSpaced", 0, 12));
/*     */     
/* 181 */     panel.setLayout(new BorderLayout(3, 3));
/* 182 */     panel.add(spanel, "North");
/* 183 */     panel.add(ipanel, "Center");
/*     */     
/* 185 */     return panel;
/*     */   }
/*     */   
/*     */   public DefaultMutableTreeNode getParentFNode(String[] path) {
/* 189 */     int pos = 0;
/* 190 */     DefaultMutableTreeNode curr = this.m_Root;
/* 191 */     while (pos < path.length - 1) {
/* 192 */       String pstr = path[pos];
/* 193 */       boolean found = false;
/* 194 */       for (int i = 0; i < curr.getChildCount(); i++) {
/* 195 */         DefaultMutableTreeNode ch = (DefaultMutableTreeNode)curr.getChildAt(i);
/* 196 */         if (pstr.equals(ch.getUserObject())) {
/* 197 */           found = true;
/* 198 */           curr = ch;
/*     */           break;
/*     */         } 
/*     */       } 
/* 202 */       if (!found) {
/* 203 */         int ipos = 0;
/* 204 */         while (ipos < curr.getChildCount() && 
/* 205 */           cmpName(curr.getChildAt(ipos).toString(), pstr)) {
/* 206 */           ipos++;
/*     */         }
/* 208 */         DefaultMutableTreeNode ch = new DefaultMutableTreeNode(pstr);
/* 209 */         this.m_TreeModel.insertNodeInto(ch, curr, ipos);
/* 210 */         curr = ch;
/*     */       } 
/* 212 */       pos++;
/*     */     } 
/* 214 */     return curr;
/*     */   }
/*     */   
/*     */   public static boolean isNumber(String s) {
/* 218 */     for (int i = 0; i < s.length(); i++) {
/* 219 */       if (!Character.isDigit(s.charAt(i))) return false; 
/*     */     } 
/* 221 */     return true;
/*     */   }
/*     */   
/*     */   public boolean cmpName(String s1, String s2) {
/* 225 */     if (isNumber(s2) && isNumber(s1)) {
/*     */       try {
/* 227 */         return (Integer.parseInt(s1) <= Integer.parseInt(s2));
/* 228 */       } catch (NumberFormatException e) {
/* 229 */         return false;
/*     */       } 
/*     */     }
/* 232 */     return (s1.compareTo(s2) <= 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void openDataSet(String file) throws IOException {
/* 237 */     String path = (String)this.m_Files.get(file);
/* 238 */     if (path != null) {
/* 239 */       openDir(path, file);
/*     */     }
/*     */   }
/*     */   
/*     */   public String[] doPermute(String[] input) {
/* 244 */     String[] res = new String[input.length];
/* 245 */     for (int i = 0; i < input.length; i++) {
/* 246 */       res[i] = input[i];
/*     */     }
/* 248 */     int max = 0; int j;
/* 249 */     for (j = 0; j < m_Permute.length; j++) {
/* 250 */       max = Math.max(max, m_Permute[j]);
/*     */     }
/* 252 */     if (max < input.length && m_Permute.length <= input.length) {
/* 253 */       for (j = 0; j < m_Permute.length; j++) {
/* 254 */         res[j] = input[m_Permute[j]];
/*     */       }
/*     */     }
/* 257 */     return res;
/*     */   }
/*     */   
/*     */   public String correctUnderscores(String fn, String ds) {
/* 261 */     if (ds != null) {
/* 262 */       int pos = fn.indexOf(ds);
/* 263 */       if (pos != -1) {
/* 264 */         StringBuffer res = new StringBuffer(fn);
/* 265 */         for (int j = 0; j < ds.length(); j++) {
/* 266 */           if (res.charAt(pos + j) == '-') {
/* 267 */             res.setCharAt(pos + j, '_');
/*     */           }
/*     */         } 
/* 270 */         return res.toString();
/*     */       } 
/*     */     } 
/* 273 */     return fn;
/*     */   }
/*     */   
/*     */   public DefaultMutableTreeNode addFile(String full, String ds) {
/* 277 */     String fn = FileUtil.removePath(full);
/* 278 */     fn = correctUnderscores(fn, ds);
/* 279 */     String[] name = FileUtil.getName(fn).split("\\-");
/* 280 */     String chname = name[name.length - 1];
/* 281 */     DefaultMutableTreeNode parent = getParentFNode(name);
/* 282 */     ClusFileTreeElem elem = new ClusFileTreeElem(chname, full);
/* 283 */     DefaultMutableTreeNode child = new DefaultMutableTreeNode(elem);
/* 284 */     int ipos = 0;
/* 285 */     while (ipos < parent.getChildCount() && 
/* 286 */       cmpName(parent.getChildAt(ipos).toString(), chname)) {
/* 287 */       ipos++;
/*     */     }
/* 289 */     this.m_TreeModel.insertNodeInto(child, parent, ipos);
/* 290 */     return child;
/*     */   }
/*     */   
/*     */   public void openDir(String dir, String ds) throws IOException {
/* 294 */     for (int i = this.m_Root.getChildCount() - 1; i >= 0; i--) {
/* 295 */       DefaultMutableTreeNode ch = (DefaultMutableTreeNode)this.m_Root.getChildAt(i);
/* 296 */       this.m_TreeModel.removeNodeFromParent(ch);
/*     */     } 
/* 298 */     File dir_file = new File(dir);
/* 299 */     ArrayList<String> files = new ArrayList();
/* 300 */     System.out.println("Searching for models in: " + dir);
/* 301 */     FileUtil.recursiveFindAll(dir_file, ".model", files);
/* 302 */     FileUtil.recursiveFindAll(dir_file, ".tree", files); int j;
/* 303 */     for (j = 0; j < files.size(); j++) {
/* 304 */       String full = files.get(j);
/* 305 */       addFile(full, ds);
/*     */     } 
/* 307 */     for (j = 0; j < this.m_Root.getChildCount(); j++) {
/* 308 */       DefaultMutableTreeNode ch = (DefaultMutableTreeNode)this.m_Root.getChildAt(j);
/* 309 */       this.m_Tree.expandPath(new TreePath((Object[])ch.getPath()));
/* 310 */       System.out.println("Expanding: " + ch);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setTree(ClusNode root, ClusStatManager mgr) throws ClusException {
/* 315 */     this.m_TreePanel.setTree(root, true);
/* 316 */     if (mgr != null) this.m_TreePanel.setStatManager(mgr); 
/* 317 */     showInfo(root);
/*     */   }
/*     */   
/*     */   public void showInfo(ClusNode root) throws ClusException {
/* 321 */     StringBuffer buf = new StringBuffer();
/* 322 */     buf.append("Size: " + root.getModelSize() + " (Leaves: " + root.getNbLeaves() + ")\n");
/* 323 */     ClusAttributeWeights scale = this.m_TreePanel.createClusAttributeWeights();
/* 324 */     String relerr = ClusFormat.SIX_AFTER_DOT.format(root.estimateError(scale));
/* 325 */     String abserr = "" + root.estimateErrorAbsolute(scale);
/* 326 */     buf.append("Examples: " + (root.getClusteringStat()).m_SumWeight + "\n");
/* 327 */     buf.append("Error: " + relerr + " (" + abserr + ") ss = " + root.estimateClusteringSS(scale) + " (" + scale.getName() + ")\n");
/* 328 */     buf.append("Statistic: " + root.getClusteringStat());
/* 329 */     this.m_TextArea.setText(buf.toString());
/*     */   }
/*     */   
/*     */   public void showSettings() {
/*     */     try {
/* 334 */       ClusStatManager mgr = this.m_TreePanel.getStatManager();
/* 335 */       Settings sett = mgr.getSettings();
/* 336 */       PrintWriter wrt = new PrintWriter(new OutputStreamWriter(System.out));
/* 337 */       sett.show(wrt);
/* 338 */       wrt.flush();
/* 339 */     } catch (IOException ex) {
/* 340 */       System.err.println("IOError: " + ex.getMessage());
/* 341 */       ex.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addModelList(ClusModelCollectionIO io, ClusFileTreeElem elem, DefaultMutableTreeNode node) {
/* 346 */     int pos = 0;
/* 347 */     elem.setObject1(io);
/* 348 */     for (int i = 0; i < io.getNbModels(); i++) {
/* 349 */       ClusModelInfo m = io.getModelInfo(i);
/* 350 */       ClusFileTreeElem celem = new ClusFileTreeElem(m.getName(), "");
/* 351 */       celem.setObject1(m);
/* 352 */       celem.setType(0);
/* 353 */       DefaultMutableTreeNode ch = new DefaultMutableTreeNode(celem);
/* 354 */       this.m_TreeModel.insertNodeInto(ch, node, pos++);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void loadModelType(ClusFileTreeElem elem, DefaultMutableTreeNode node) throws ClusException {
/* 359 */     ClusModelInfo m = (ClusModelInfo)elem.getObject1();
/* 360 */     if (m.getModel() instanceof ClusNode) {
/* 361 */       ClusNode root = (ClusNode)m.getModel();
/* 362 */       root.updateTree();
/* 363 */       setTree(root, m.getStatManager());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void loadModelType2(ClusFileTreeElem elem) throws ClusException {
/* 368 */     ClusModelCollectionIO io = (ClusModelCollectionIO)elem.getObject1();
/* 369 */     if (io.getNbModels() > 0) {
/* 370 */       ClusModelInfo m = io.getModelInfo(0);
/* 371 */       if (m.getModel() instanceof ClusNode) {
/* 372 */         ClusNode root = (ClusNode)m.getModel();
/* 373 */         root.updateTree();
/* 374 */         setTree(root, m.getStatManager());
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void loadModel(DefaultMutableTreeNode node) throws ClusException {
/* 380 */     ClusFileTreeElem elem = (ClusFileTreeElem)node.getUserObject();
/*     */     try {
/* 382 */       if (elem.getType() != -1) {
/* 383 */         loadModelType(elem, node);
/*     */         return;
/*     */       } 
/* 386 */       System.out.println("Name: " + elem.getFullName());
/* 387 */       ClusModelCollectionIO io = ClusModelCollectionIO.load(elem.getFullName());
/* 388 */       addModelList(io, elem, node);
/* 389 */       loadModelType2(elem);
/* 390 */     } catch (IOException e) {
/* 391 */       JOptionPane.showMessageDialog(this, "Can't open model: " + elem.getFullName());
/* 392 */       System.err.println("IOError: " + e.getMessage());
/* 393 */     } catch (ClassNotFoundException e) {
/* 394 */       System.err.println("Error " + e.getMessage());
/* 395 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   private class MyScaleListener implements ChangeListener { private MyScaleListener() {}
/*     */     
/*     */     public void stateChanged(ChangeEvent e) {
/* 402 */       JSlider source = (JSlider)e.getSource();
/* 403 */       if (!source.getValueIsAdjusting()) {
/* 404 */         int val = source.getValue();
/* 405 */         TreeFrame.this.m_ShowHier.setFac(val / 10.0D);
/* 406 */         TreeFrame.this.m_ShowHier.updateScreen();
/*     */       } 
/*     */     } }
/*     */   
/*     */   private class MySignificanceListener implements ChangeListener {
/*     */     private MySignificanceListener() {}
/*     */     
/*     */     public void stateChanged(ChangeEvent e) {
/* 414 */       JSlider source = (JSlider)e.getSource();
/* 415 */       if (!source.getValueIsAdjusting()) {
/* 416 */         int val = source.getValue();
/* 417 */         TreeFrame.this.m_ShowHier.setSig(val / 100.0D);
/* 418 */         TreeFrame.this.m_ShowHier.renewTree();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private class MyHorzListener implements ActionListener { private MyHorzListener() {}
/*     */     
/*     */     public void actionPerformed(ActionEvent e) {
/* 426 */       JCheckBoxMenuItem source = (JCheckBoxMenuItem)e.getSource();
/* 427 */       TreeFrame.this.m_TreePanel.setHorzVert(source.isSelected());
/*     */     } }
/*     */   
/*     */   private class MyFileTreeListener implements TreeSelectionListener {
/*     */     private MyFileTreeListener() {}
/*     */     
/*     */     public void valueChanged(TreeSelectionEvent e) {
/*     */       try {
/* 435 */         DefaultMutableTreeNode node = (DefaultMutableTreeNode)TreeFrame.this.m_Tree.getLastSelectedPathComponent();
/* 436 */         if (node == null)
/* 437 */           return;  if (node.isLeaf()) {
/* 438 */           TreeFrame.this.loadModel(node);
/* 439 */         } else if (node.getUserObject() instanceof ClusFileTreeElem) {
/* 440 */           ClusFileTreeElem elem = (ClusFileTreeElem)node.getUserObject();
/* 441 */           TreeFrame.this.loadModelType2(elem);
/*     */         } 
/* 443 */       } catch (ClusException ex) {
/* 444 */         System.err.println("Clus error: " + ex.getMessage());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class MyResizeListener
/*     */     implements ComponentListener
/*     */   {
/*     */     private MyResizeListener() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void componentHidden(ComponentEvent e) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void componentMoved(ComponentEvent e) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void componentResized(ComponentEvent e) {
/* 467 */       Dimension d = ((JFrame)e.getSource()).getSize();
/* 468 */       TreeFrame.this.setDividers(d.width, d.height);
/*     */     }
/*     */     
/*     */     public void componentShown(ComponentEvent e) {}
/*     */   }
/*     */   
/*     */   private class MyShowSettingsListener implements ActionListener { private MyShowSettingsListener() {}
/*     */     
/*     */     public void actionPerformed(ActionEvent e) {
/* 477 */       TreeFrame.this.showSettings();
/*     */     } }
/*     */   
/*     */   private class MyListListener implements ListSelectionListener {
/*     */     private MyListListener() {}
/*     */     
/*     */     public void valueChanged(ListSelectionEvent e) {
/* 484 */       if (!e.getValueIsAdjusting()) {
/* 485 */         String file = TreeFrame.this.m_DSList.getSelectedValue();
/* 486 */         if (file != null)
/*     */           try {
/* 488 */             TreeFrame.this.openDataSet(file);
/* 489 */           } catch (IOException ex) {
/* 490 */             JOptionPane.showMessageDialog(TreeFrame.this, "Error: " + ex.getMessage());
/*     */           }  
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private class MyFindListener implements ActionListener {
/*     */     private MyFindListener() {}
/*     */     
/*     */     public void actionPerformed(ActionEvent e) {
/* 500 */       TreeFrame.this.m_FileChoose.setFileSelectionMode(1);
/* 501 */       int res = TreeFrame.this.m_FileChoose.showOpenDialog(TreeFrame.this);
/* 502 */       if (res == 0) {
/*     */         try {
/* 504 */           File file = TreeFrame.this.m_FileChoose.getSelectedFile();
/* 505 */           System.out.println("Opening: " + file.getName());
/* 506 */           ArrayList<String> list = FileUtil.recursiveFind(file, ".s");
/* 507 */           for (int i = 0; i < list.size(); i++) {
/* 508 */             String full = list.get(i);
/* 509 */             String dsname = FileUtil.getName(FileUtil.removePath(full));
/* 510 */             String dspath = FileUtil.getPath(full);
/* 511 */             TreeFrame.this.m_Files.put(dsname, dspath);
/* 512 */             System.out.println("Name = " + dsname);
/*     */           } 
/* 514 */           TreeFrame.this.m_DSListModel.clear();
/* 515 */           Iterator<String> iter = TreeFrame.this.m_Files.keySet().iterator();
/* 516 */           while (iter.hasNext()) {
/* 517 */             String name = iter.next();
/* 518 */             TreeFrame.this.m_DSListModel.addElement(name);
/*     */           } 
/* 520 */         } catch (IOException ex) {
/* 521 */           JOptionPane.showMessageDialog(TreeFrame.this, "Error: " + ex.getMessage());
/*     */         } 
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static TreeFrame createFrame(ClusStatManager manager, ClusNode root, ClassHierarchy hier) {
/*     */     TreeFrame frame;
/* 529 */     String[] lines = new String[0];
/* 530 */     TreePanel tpanel = new TreePanel(root, lines);
/* 531 */     tpanel.setStatManager(manager);
/* 532 */     if (manager == null) {
/* 533 */       frame = new TreeFrame("Clus", tpanel, null);
/*     */     } else {
/* 535 */       ClusSchema schema = manager.getSchema();
/* 536 */       if (hier != null) {
/* 537 */         ShowHierarchy sh = new ShowHierarchy(root, hier);
/* 538 */         frame = new TreeFrame(schema.getRelationName(), tpanel, sh);
/*     */       } else {
/* 540 */         frame = new TreeFrame(schema.getRelationName(), tpanel, null);
/*     */       } 
/*     */     } 
/* 543 */     tpanel.setFrame(frame);
/* 544 */     frame.init();
/* 545 */     frame.setDividers(800, 600);
/* 546 */     return frame;
/*     */   }
/*     */ 
/*     */   
/*     */   public static TreeFrame showTree(String fname) throws ClusException, IOException, ClassNotFoundException {
/* 551 */     TreeFrame frame = createFrame((ClusStatManager)null, (ClusNode)null, (ClassHierarchy)null);
/* 552 */     frame.addWindowListener((WindowListener)new WindowClosingListener(frame, 0));
/* 553 */     DefaultMutableTreeNode node = frame.addFile(fname, (String)null);
/* 554 */     frame.loadModel(node);
/* 555 */     frame.setVisible(true);
/* 556 */     return frame;
/*     */   }
/*     */ 
/*     */   
/*     */   public static TreeFrame start(ClusStatManager manager, String opendir) throws IOException {
/* 561 */     TreeFrame frame = createFrame(manager, (ClusNode)null, (ClassHierarchy)null);
/* 562 */     frame.addWindowListener((WindowListener)new WindowClosingListener(frame, 0));
/* 563 */     frame.openDir(opendir, (String)null);
/* 564 */     frame.setVisible(true);
/* 565 */     return frame;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\gui\TreeFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */