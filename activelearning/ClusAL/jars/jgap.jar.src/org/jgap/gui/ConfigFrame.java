/*     */ package org.jgap.gui;
/*     */ 
/*     */ import info.clearthought.layout.TableLayout;
/*     */ import info.clearthought.layout.TableLayoutConstraints;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.swing.DefaultListModel;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import org.jgap.data.config.ConfigData;
/*     */ import org.jgap.data.config.ConfigProperty;
/*     */ import org.jgap.data.config.ConfigWriter;
/*     */ import org.jgap.data.config.Configurable;
/*     */ import org.jgap.data.config.IConfigInfo;
/*     */ import org.jgap.data.config.MetaConfig;
/*     */ import org.jgap.data.config.MetaConfigException;
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
/*     */ public class ConfigFrame
/*     */   extends JFrame
/*     */   implements IConfigInfo
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.19 $";
/*     */   private Object m_conHandler;
/*     */   private boolean m_isRoot;
/*     */   private List m_panels;
/*     */   private List m_listProps;
/*     */   private List m_textProps;
/*     */   private List m_listGroups;
/*     */   private List m_textGroups;
/*     */   private JPanel m_listPanel;
/*     */   private JPanel m_textPanel;
/*     */   private JPanel m_configPanel;
/*     */   private JButton m_configButton;
/*     */   private ConfigButtonListener m_cbl;
/*     */   private JTextField m_fileName;
/*     */   private JButton m_configureButton;
/*     */   private JTextField m_configItem;
/*     */   private Configurable m_conObj;
/*     */   private ConfigFrame m_parent;
/*     */   private static final String m_defaultConfigFile = "jgap.con";
/*     */   
/*     */   ConfigFrame(ConfigFrame a_parent, String a_title, boolean a_isRoot) {
/*  90 */     super(a_title);
/*  91 */     this.m_panels = Collections.synchronizedList(new ArrayList());
/*  92 */     this.m_textProps = Collections.synchronizedList(new ArrayList());
/*  93 */     this.m_listProps = Collections.synchronizedList(new ArrayList());
/*  94 */     this.m_listGroups = Collections.synchronizedList(new ArrayList());
/*  95 */     this.m_textGroups = Collections.synchronizedList(new ArrayList());
/*  96 */     this.m_cbl = new ConfigButtonListener(this);
/*  97 */     this.m_isRoot = a_isRoot;
/*  98 */     this.m_parent = a_parent;
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
/*     */   public void createAndShowGUI(Object a_conHandler) {
/* 110 */     JFrame.setDefaultLookAndFeelDecorated(true);
/* 111 */     this.m_conHandler = a_conHandler;
/*     */     
/* 113 */     pack();
/* 114 */     setVisible(true);
/* 115 */     setBounds(100, 100, 300, 300);
/* 116 */     setSize(500, 300);
/*     */     try {
/* 118 */       MetaConfig mt = MetaConfig.getInstance();
/*     */     }
/* 120 */     catch (MetaConfigException mcEx) {
/* 121 */       JOptionPane.showMessageDialog(null, "Exception while parsing JGAP Meta Config file " + mcEx.getMessage(), "Meta Config Exception", 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 128 */     catch (Exception ex) {
/* 129 */       JOptionPane.showMessageDialog(null, "Exception while parsing JGAP Meta Config file " + ex.getMessage(), "Meta Config Exception", 0);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 136 */     setup();
/* 137 */     show();
/* 138 */     if (this.m_isRoot) {
/* 139 */       setDefaultCloseOperation(3);
/*     */     } else {
/*     */       
/* 142 */       setDefaultCloseOperation(2);
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
/*     */   public ConfigData getConfigData() {
/* 155 */     ConfigData cd = new ConfigData();
/* 156 */     cd.setNS(this.m_conHandler.getClass().getName());
/*     */ 
/*     */     
/*     */     try {
/* 160 */       Iterator<ListGroup> lIter = this.m_listGroups.iterator();
/* 161 */       while (lIter.hasNext()) {
/* 162 */         ListGroup lg = lIter.next();
/* 163 */         List<?> values = Collections.synchronizedList(new ArrayList());
/* 164 */         Enumeration<String> e = lg.getOutListModel().elements();
/* 165 */         while (e.hasMoreElements()) {
/* 166 */           String val = e.nextElement();
/* 167 */           values.add(val);
/*     */         } 
/* 169 */         cd.addListData(lg.getProp().getName(), values);
/*     */       } 
/*     */ 
/*     */       
/* 173 */       Iterator<TextGroup> tIter = this.m_textGroups.iterator();
/* 174 */       while (tIter.hasNext()) {
/* 175 */         TextGroup tg = tIter.next();
/* 176 */         cd.addTextData(tg.getProp().getName(), tg.getTextField().getText());
/*     */       }
/*     */     
/* 179 */     } catch (ClassCastException cex) {
/* 180 */       JOptionPane.showMessageDialog(null, cex.getMessage(), "ConfigFrame.getConfigData():Configuration Error", 1);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 185 */     return cd;
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
/*     */   public String getFileName() {
/* 197 */     if (this.m_isRoot) {
/* 198 */       String fName = this.m_fileName.getText();
/*     */       
/* 200 */       if (fName.equals("")) {
/* 201 */         fName = "jgap.con";
/*     */       }
/* 203 */       return fName;
/*     */     } 
/*     */     
/* 206 */     return this.m_parent.getFileName();
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
/*     */   private void setup() {
/* 221 */     int numLists = 0, numTexts = 0;
/* 222 */     List props = null;
/*     */     
/*     */     try {
/* 225 */       props = MetaConfig.getInstance().getConfigProperty(this.m_conHandler.getClass().getName());
/*     */     }
/* 227 */     catch (Exception ex) {
/* 228 */       JOptionPane.showMessageDialog(null, ex.getMessage(), "Configuration Error: Could not get properties for class " + this.m_conHandler.getClass().getName(), 1);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 234 */     if (props == null) {
/* 235 */       JOptionPane.showMessageDialog(null, "setup():No Configurable Properties in this Configuration", "Configuration Message", 1);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 242 */     Iterator<ConfigProperty> iter = props.iterator();
/* 243 */     while (iter.hasNext()) {
/*     */       try {
/* 245 */         ConfigProperty prop = iter.next();
/* 246 */         if (prop.getWidget().equals("JList")) {
/* 247 */           numLists++;
/* 248 */           this.m_listProps.add(prop); continue;
/*     */         } 
/* 250 */         if (prop.getWidget().equals("JTextField")) {
/* 251 */           numTexts++;
/* 252 */           this.m_textProps.add(prop);
/*     */           
/*     */           continue;
/*     */         } 
/* 256 */         JOptionPane.showMessageDialog(null, "Unknown Widget " + prop.getWidget(), "Configuration Error", 1);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 262 */       catch (ClassCastException cex) {
/* 263 */         JOptionPane.showMessageDialog(null, cex.getMessage(), "ConfigError.setup():Configuration Error: Invalid cast", 1);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 271 */     if (numLists == 0 && numTexts == 0) {
/* 272 */       JOptionPane.showMessageDialog(null, "No Configurable Properties in this Configuration", "Configuration Information", 1);
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 281 */     int numPanels = 2;
/* 282 */     if (numLists > 0 && numTexts > 0) {
/* 283 */       numPanels = 3;
/*     */     }
/*     */     
/* 286 */     addWidgets(numPanels, numLists, numTexts);
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
/*     */   private void addWidgets(int a_numPanels, int a_numLists, int a_numTexts) {
/*     */     try {
/* 302 */       a_numPanels = 3;
/*     */       
/* 304 */       double[][] tableArray = new double[2][a_numPanels];
/* 305 */       double perPanel = 1.0D / a_numPanels;
/* 306 */       int i = 0;
/* 307 */       for (i = 0; i < a_numPanels - 1; i++) {
/* 308 */         tableArray[1][i] = perPanel;
/*     */       }
/*     */       
/* 311 */       tableArray[1][i] = -1.0D;
/*     */       
/* 313 */       tableArray[0][0] = -1.0D;
/* 314 */       getContentPane().setLayout((LayoutManager)new TableLayout(tableArray));
/*     */       
/* 316 */       int panelsAdded = 0;
/*     */       
/* 318 */       if (a_numLists > 0) {
/*     */ 
/*     */ 
/*     */         
/* 322 */         int numCols = 3 * a_numLists;
/*     */         
/* 324 */         double[][] arrayOfDouble = new double[2][numCols];
/* 325 */         double space = 1.0D / a_numLists;
/*     */         
/* 327 */         double listSpace = space * 0.4D;
/* 328 */         double buttonSpace = space * 0.2D;
/* 329 */         for (int itr = 0; itr < a_numLists; itr++) {
/* 330 */           arrayOfDouble[0][3 * itr] = listSpace;
/* 331 */           arrayOfDouble[0][3 * itr + 1] = buttonSpace;
/* 332 */           arrayOfDouble[0][3 * itr + 2] = listSpace;
/*     */         } 
/*     */         
/* 335 */         arrayOfDouble[1][0] = -1.0D;
/* 336 */         this.m_listPanel = new JPanel();
/* 337 */         this.m_panels.add(this.m_listPanel);
/* 338 */         this.m_listPanel.setLayout((LayoutManager)new TableLayout(arrayOfDouble));
/* 339 */         getContentPane().add(this.m_listPanel, new TableLayoutConstraints(0, panelsAdded, 0, panelsAdded, 2, 2));
/*     */ 
/*     */ 
/*     */         
/* 343 */         panelsAdded++;
/*     */         
/* 345 */         Iterator<ConfigProperty> iter = this.m_listProps.iterator();
/*     */ 
/*     */         
/* 348 */         for (int itr1 = 0; itr1 < a_numLists && iter.hasNext(); itr1++) {
/* 349 */           ListGroup lg = new ListGroup(this);
/* 350 */           this.m_listGroups.add(lg);
/* 351 */           ConfigProperty prop = iter.next();
/* 352 */           lg.setProp(prop);
/* 353 */           this.m_listPanel.add(lg.getListScroller(), new TableLayoutConstraints(3 * itr1, 0, 3 * itr1, 0, 1, 1));
/*     */ 
/*     */ 
/*     */           
/* 357 */           this.m_listPanel.add(lg.getLButton(), new TableLayoutConstraints(3 * itr1 + 1, 0, 3 * itr1 + 1, 0, 1, 0));
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 362 */           this.m_listPanel.add(lg.getRButton(), new TableLayoutConstraints(3 * itr1 + 1, 0, 3 * itr1 + 1, 0, 1, 3));
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 367 */           Iterator valIter = prop.getValuesIter();
/* 368 */           while (valIter.hasNext()) {
/* 369 */             lg.getListModel().addElement(valIter.next());
/*     */           }
/* 371 */           this.m_listPanel.add(lg.getOutListScroller(), new TableLayoutConstraints(3 * itr1 + 2, 0, 3 * itr1 + 2, 0, 1, 1));
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 378 */       if (a_numTexts > 0) {
/*     */         
/* 380 */         int numCols = a_numTexts * 2;
/* 381 */         double[][] arrayOfDouble = new double[2][numCols];
/*     */         
/* 383 */         double perText = 1.0D / numCols;
/* 384 */         int itr = 0;
/*     */         
/* 386 */         for (itr = 0; itr < numCols - 1; itr++) {
/* 387 */           arrayOfDouble[0][itr] = perText;
/*     */         }
/* 389 */         arrayOfDouble[0][itr] = -1.0D;
/*     */         
/* 391 */         arrayOfDouble[1][0] = -1.0D;
/* 392 */         this.m_textPanel = new JPanel();
/* 393 */         this.m_panels.add(this.m_textPanel);
/* 394 */         this.m_textPanel.setLayout((LayoutManager)new TableLayout(arrayOfDouble));
/* 395 */         getContentPane().add(this.m_textPanel, new TableLayoutConstraints(0, panelsAdded, 0, panelsAdded, 2, 2));
/*     */ 
/*     */         
/* 398 */         panelsAdded++;
/*     */ 
/*     */         
/* 401 */         Iterator<ConfigProperty> iter = this.m_textProps.iterator();
/*     */         
/* 403 */         for (int itr1 = 0; itr1 < a_numTexts && iter.hasNext(); itr1++) {
/* 404 */           TextGroup tg = new TextGroup();
/* 405 */           this.m_textGroups.add(tg);
/* 406 */           ConfigProperty prop = iter.next();
/* 407 */           tg.setProp(prop);
/* 408 */           JLabel label = tg.getLabel();
/* 409 */           label.setText(prop.getName());
/* 410 */           this.m_textPanel.add(label, new TableLayoutConstraints(itr1, 0, itr1, 0, 3, 1));
/*     */ 
/*     */           
/* 413 */           this.m_textPanel.add(tg.getTextField(), new TableLayoutConstraints(itr1 + 1, 0, itr1 + 1, 0, 0, 1));
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 420 */       double[][] panelSize = new double[2][4];
/*     */       
/* 422 */       panelSize[0][0] = 0.25D;
/* 423 */       panelSize[0][1] = 0.25D;
/* 424 */       panelSize[0][2] = 0.25D;
/* 425 */       panelSize[0][3] = 0.25D;
/*     */       
/* 427 */       panelSize[1][0] = -1.0D;
/* 428 */       this.m_configPanel = new JPanel();
/* 429 */       this.m_panels.add(this.m_configPanel);
/* 430 */       this.m_configPanel.setLayout((LayoutManager)new TableLayout(panelSize));
/* 431 */       getContentPane().add(this.m_configPanel, new TableLayoutConstraints(0, panelsAdded, 0, panelsAdded, 2, 2));
/*     */ 
/*     */ 
/*     */       
/* 435 */       this.m_configItem = new JTextField(50);
/* 436 */       this.m_configPanel.add(this.m_configItem, new TableLayoutConstraints(0, 0, 0, 0, 3, 1));
/*     */ 
/*     */ 
/*     */       
/* 440 */       this.m_configureButton = new JButton("Configure");
/* 441 */       this.m_configureButton.addActionListener(this.m_cbl);
/* 442 */       this.m_configPanel.add(this.m_configureButton, new TableLayoutConstraints(1, 0, 1, 0, 0, 1));
/*     */ 
/*     */ 
/*     */       
/* 446 */       if (this.m_isRoot) {
/* 447 */         this.m_fileName = new JTextField("jgap.con");
/* 448 */         this.m_configPanel.add(this.m_fileName, new TableLayoutConstraints(2, 0, 2, 0, 3, 1));
/*     */ 
/*     */         
/* 451 */         this.m_configButton = new JButton("Generate");
/* 452 */         this.m_configButton.addActionListener(this.m_cbl);
/* 453 */         this.m_configPanel.add(this.m_configButton, new TableLayoutConstraints(3, 0, 3, 0, 0, 1));
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 458 */         this.m_configButton = new JButton("Save Configuration");
/* 459 */         this.m_configButton.addActionListener(this.m_cbl);
/* 460 */         this.m_configPanel.add(this.m_configButton, new TableLayoutConstraints(3, 0, 3, 0, 0, 1));
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 465 */     catch (Exception ex) {
/* 466 */       JOptionPane.showMessageDialog(null, "Exception" + ex.toString(), "This is the title", 1);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public class ListGroup
/*     */   {
/*     */     private JList m_list;
/*     */ 
/*     */ 
/*     */     
/*     */     private DefaultListModel m_listModel;
/*     */ 
/*     */ 
/*     */     
/*     */     private JScrollPane m_listScroller;
/*     */ 
/*     */ 
/*     */     
/*     */     private JList m_outList;
/*     */ 
/*     */ 
/*     */     
/*     */     private DefaultListModel m_outListModel;
/*     */ 
/*     */ 
/*     */     
/*     */     private JScrollPane m_outListScroller;
/*     */ 
/*     */ 
/*     */     
/*     */     private ConfigFrame.ConfigListSelectionListener m_outListListener;
/*     */ 
/*     */     
/*     */     private JButton m_lButton;
/*     */ 
/*     */     
/*     */     private JButton m_rButton;
/*     */ 
/*     */     
/*     */     private ConfigProperty m_prop;
/*     */ 
/*     */     
/*     */     private ConfigFrame.ListButtonListener m_listBL;
/*     */ 
/*     */     
/*     */     private ConfigFrame m_frame;
/*     */ 
/*     */ 
/*     */     
/*     */     ListGroup(ConfigFrame a_frame) {
/* 519 */       this.m_frame = a_frame;
/*     */       
/* 521 */       this.m_listModel = new DefaultListModel();
/* 522 */       this.m_list = new JList(this.m_listModel);
/* 523 */       this.m_list.setSelectionMode(1);
/* 524 */       this.m_list.setLayoutOrientation(2);
/* 525 */       this.m_list.setVisibleRowCount(-1);
/* 526 */       this.m_listScroller = new JScrollPane(this.m_list);
/* 527 */       this.m_listScroller.setPreferredSize(new Dimension(250, 80));
/*     */       
/* 529 */       this.m_outListModel = new DefaultListModel();
/* 530 */       this.m_outList = new JList(this.m_outListModel);
/* 531 */       this.m_outList.setSelectionMode(0);
/* 532 */       this.m_outList.setLayoutOrientation(2);
/* 533 */       this.m_outList.setVisibleRowCount(-1);
/* 534 */       this.m_outListScroller = new JScrollPane(this.m_outList);
/* 535 */       this.m_outListListener = new ConfigFrame.ConfigListSelectionListener(this.m_frame, this.m_outList);
/* 536 */       this.m_outList.getSelectionModel().addListSelectionListener(this.m_outListListener);
/* 537 */       this.m_outListScroller.setPreferredSize(new Dimension(250, 80));
/*     */       
/* 539 */       this.m_listBL = new ConfigFrame.ListButtonListener(this);
/* 540 */       this.m_lButton = new JButton("<-");
/* 541 */       this.m_lButton.addActionListener(this.m_listBL);
/* 542 */       this.m_rButton = new JButton("->");
/* 543 */       this.m_rButton.addActionListener(this.m_listBL);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ConfigProperty getProp() {
/* 554 */       return this.m_prop;
/*     */     }
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
/*     */     public void setProp(ConfigProperty a_prop) {
/* 567 */       this.m_prop = a_prop;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JList getList() {
/* 577 */       return this.m_list;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public DefaultListModel getListModel() {
/* 587 */       return this.m_listModel;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JScrollPane getListScroller() {
/* 597 */       return this.m_listScroller;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JList getOutList() {
/* 607 */       return this.m_outList;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public DefaultListModel getOutListModel() {
/* 618 */       return this.m_outListModel;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JScrollPane getOutListScroller() {
/* 628 */       return this.m_outListScroller;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JButton getLButton() {
/* 638 */       return this.m_lButton;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JButton getRButton() {
/* 648 */       return this.m_rButton;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void leftButtonPressed() {
/* 658 */       int[] indices = this.m_outList.getSelectedIndices();
/* 659 */       for (int i = 0; i < indices.length; i++) {
/* 660 */         String removed = this.m_outListModel.remove(indices[0]);
/* 661 */         this.m_listModel.addElement(removed);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void rightButtonPressed() {
/* 672 */       int[] indices = this.m_list.getSelectedIndices();
/* 673 */       for (int i = 0; i < indices.length; i++) {
/* 674 */         String removed = this.m_listModel.remove(indices[0]);
/* 675 */         this.m_outListModel.addElement(removed);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   class TextGroup
/*     */   {
/* 694 */     private JTextField m_textField = new JTextField(20);
/* 695 */     private JLabel m_label = new JLabel();
/*     */     private ConfigProperty m_prop;
/*     */     
/*     */     public ConfigProperty getProp() {
/* 699 */       return this.m_prop;
/*     */     }
/*     */     
/*     */     public void setProp(ConfigProperty a_prop) {
/* 703 */       this.m_prop = a_prop;
/*     */     }
/*     */     
/*     */     public JTextField getTextField() {
/* 707 */       return this.m_textField;
/*     */     }
/*     */     
/*     */     public JLabel getLabel() {
/* 711 */       return this.m_label;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   class ConfigButtonListener
/*     */     implements ActionListener
/*     */   {
/*     */     private ConfigFrame m_frame;
/*     */ 
/*     */ 
/*     */     
/*     */     ConfigButtonListener(ConfigFrame a_frame) {
/* 725 */       this.m_frame = a_frame;
/*     */     }
/*     */ 
/*     */     
/*     */     public void actionPerformed(ActionEvent a_e) {
/* 730 */       if (a_e.getActionCommand().equals("Configure")) {
/* 731 */         String conStr = ConfigFrame.this.m_configItem.getText();
/* 732 */         if (conStr.equals("")) {
/* 733 */           JOptionPane.showMessageDialog(null, "Configurable name is empty, cannot configure.", "Configuration Error", 1);
/*     */         } else {
/*     */           try {
/*     */             Class<?> conClass;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 742 */             ConfigFrame.this.m_conObj = null;
/*     */             try {
/* 744 */               conClass = Class.forName(conStr);
/*     */             }
/* 746 */             catch (ClassNotFoundException cnfEx) {
/* 747 */               JOptionPane.showMessageDialog(null, cnfEx.getMessage(), "Configuration Error: Class not found", 1);
/*     */ 
/*     */               
/*     */               return;
/*     */             } 
/*     */ 
/*     */             
/*     */             try {
/* 755 */               ConfigFrame.this.m_conObj = (Configurable)conClass.newInstance();
/*     */             
/*     */             }
/* 758 */             catch (Exception ex) {
/* 759 */               JOptionPane.showMessageDialog(null, ex.getMessage(), "Configuration Error:Could not create object", 1);
/*     */ 
/*     */               
/*     */               return;
/*     */             } 
/*     */ 
/*     */             
/*     */             try {
/* 767 */               SwingUtilities.invokeLater(new Runnable() {
/*     */                     public void run() {
/*     */                       try {
/* 770 */                         GUIManager.getInstance().showFrame(ConfigFrame.ConfigButtonListener.this.m_frame, ConfigFrame.this.m_conObj);
/*     */                       }
/* 772 */                       catch (Exception ex) {
/* 773 */                         JOptionPane.showMessageDialog(null, ex.getMessage(), "Configuration Error:Could not create new Frame", 0);
/*     */                       
/*     */                       }
/*     */                     
/*     */                     }
/*     */                   });
/*     */             
/*     */             }
/* 781 */             catch (Exception ex) {
/* 782 */               JOptionPane.showMessageDialog(null, ex.getMessage(), "Configuration Error:Could not create new frame", 0);
/*     */             
/*     */             }
/*     */ 
/*     */           
/*     */           }
/* 788 */           catch (Exception ex) {
/* 789 */             JOptionPane.showMessageDialog(null, ex.getMessage(), "Configuration Error", 1);
/*     */           
/*     */           }
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 798 */         ConfigWriter.getInstance().write(this.m_frame);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public class ListButtonListener
/*     */     implements ActionListener
/*     */   {
/*     */     private ConfigFrame.ListGroup m_lg;
/*     */ 
/*     */ 
/*     */     
/*     */     ListButtonListener(ConfigFrame.ListGroup a_lg) {
/* 813 */       this.m_lg = a_lg;
/*     */     }
/*     */ 
/*     */     
/*     */     public void actionPerformed(ActionEvent a_e) {
/* 818 */       if (a_e.getActionCommand().equals("<-")) {
/*     */         
/* 820 */         this.m_lg.leftButtonPressed();
/*     */       }
/*     */       else {
/*     */         
/* 824 */         this.m_lg.rightButtonPressed();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public class ConfigListSelectionListener
/*     */     implements ListSelectionListener
/*     */   {
/*     */     private JList m_list;
/*     */ 
/*     */     
/*     */     private ConfigFrame m_frame;
/*     */ 
/*     */ 
/*     */     
/*     */     public ConfigListSelectionListener(ConfigFrame a_frame, JList a_list) {
/* 842 */       this.m_list = a_list;
/* 843 */       this.m_frame = a_frame;
/*     */     }
/*     */     
/*     */     public void valueChanged(ListSelectionEvent a_e) {
/* 847 */       Object[] values = this.m_list.getSelectedValues();
/* 848 */       if (values.length > 0) {
/* 849 */         String value = (String)values[0];
/* 850 */         ConfigFrame.this.notifySelection(value);
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
/*     */   private void notifySelection(String a_value) {
/* 862 */     this.m_configItem.setText(a_value);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gui\ConfigFrame.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */