package weka.gui.treevisualizer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.Timer;
import weka.core.Instances;
import weka.gui.visualize.PrintablePanel;
import weka.gui.visualize.VisualizePanel;

public class TreeVisualizer extends PrintablePanel implements MouseMotionListener, MouseListener, ActionListener, ItemListener {
  private NodePlace m_placer;
  
  private Node m_topNode;
  
  private Dimension m_viewPos;
  
  private Dimension m_viewSize;
  
  private Font m_currentFont;
  
  private FontMetrics m_fontSize;
  
  private int m_numNodes;
  
  private int m_numLevels;
  
  private NodeInfo[] m_nodes;
  
  private EdgeInfo[] m_edges;
  
  private Timer m_frameLimiter;
  
  private int m_mouseState;
  
  private Dimension m_oldMousePos;
  
  private Dimension m_newMousePos;
  
  private boolean m_clickAvailable;
  
  private Dimension m_nViewPos;
  
  private Dimension m_nViewSize;
  
  private int m_scaling;
  
  private JPopupMenu m_winMenu;
  
  private JMenuItem m_topN;
  
  private JMenuItem m_fitToScreen;
  
  private JMenuItem m_autoScale;
  
  private JMenu m_selectFont;
  
  private ButtonGroup m_selectFontGroup;
  
  private JRadioButtonMenuItem m_size24;
  
  private JRadioButtonMenuItem m_size22;
  
  private JRadioButtonMenuItem m_size20;
  
  private JRadioButtonMenuItem m_size18;
  
  private JRadioButtonMenuItem m_size16;
  
  private JRadioButtonMenuItem m_size14;
  
  private JRadioButtonMenuItem m_size12;
  
  private JRadioButtonMenuItem m_size10;
  
  private JRadioButtonMenuItem m_size8;
  
  private JRadioButtonMenuItem m_size6;
  
  private JRadioButtonMenuItem m_size4;
  
  private JRadioButtonMenuItem m_size2;
  
  private JRadioButtonMenuItem m_size1;
  
  private JMenuItem m_accept;
  
  private JPopupMenu m_nodeMenu;
  
  private JMenuItem m_visualise;
  
  private JMenuItem m_addChildren;
  
  private JMenuItem m_remChildren;
  
  private JMenuItem m_classifyChild;
  
  private JMenuItem m_sendInstances;
  
  private int m_focusNode;
  
  private int m_highlightNode;
  
  private TreeDisplayListener m_listener;
  
  private JTextField m_searchString;
  
  private JDialog m_searchWin;
  
  private JRadioButton m_caseSen;
  
  public TreeVisualizer(TreeDisplayListener paramTreeDisplayListener, String paramString, NodePlace paramNodePlace) {
    setBorder(BorderFactory.createTitledBorder("Tree View"));
    this.m_listener = paramTreeDisplayListener;
    TreeBuild treeBuild = new TreeBuild();
    Node node = null;
    PlaceNode2 placeNode2 = new PlaceNode2();
    node = treeBuild.create(new StringReader(paramString));
    this.m_highlightNode = 5;
    this.m_topNode = node;
    this.m_placer = paramNodePlace;
    this.m_placer.place(this.m_topNode);
    this.m_viewPos = new Dimension(0, 0);
    this.m_viewSize = new Dimension(800, 600);
    this.m_nViewPos = new Dimension(0, 0);
    this.m_nViewSize = new Dimension(800, 600);
    this.m_scaling = 0;
    this.m_numNodes = Node.getCount(this.m_topNode, 0);
    this.m_numLevels = Node.getHeight(this.m_topNode, 0);
    this.m_nodes = new NodeInfo[this.m_numNodes];
    this.m_edges = new EdgeInfo[this.m_numNodes - 1];
    arrayFill(this.m_topNode, this.m_nodes, this.m_edges);
    changeFontSize(12);
    this.m_mouseState = 0;
    this.m_oldMousePos = new Dimension(0, 0);
    this.m_newMousePos = new Dimension(0, 0);
    this.m_frameLimiter = new Timer(120, this);
    this.m_winMenu = new JPopupMenu();
    this.m_topN = new JMenuItem("Center on Top Node");
    this.m_topN.setActionCommand("Center on Top Node");
    this.m_fitToScreen = new JMenuItem("Fit to Screen");
    this.m_fitToScreen.setActionCommand("Fit to Screen");
    this.m_selectFont = new JMenu("Select Font");
    this.m_selectFont.setActionCommand("Select Font");
    this.m_autoScale = new JMenuItem("Auto Scale");
    this.m_autoScale.setActionCommand("Auto Scale");
    this.m_selectFontGroup = new ButtonGroup();
    this.m_accept = new JMenuItem("Accept The Tree");
    this.m_accept.setActionCommand("Accept The Tree");
    this.m_winMenu.add(this.m_topN);
    this.m_winMenu.addSeparator();
    this.m_winMenu.add(this.m_fitToScreen);
    this.m_winMenu.add(this.m_autoScale);
    this.m_winMenu.addSeparator();
    this.m_winMenu.addSeparator();
    this.m_winMenu.add(this.m_selectFont);
    this.m_winMenu.addSeparator();
    if (this.m_listener != null)
      this.m_winMenu.add(this.m_accept); 
    this.m_topN.addActionListener(this);
    this.m_fitToScreen.addActionListener(this);
    this.m_autoScale.addActionListener(this);
    this.m_accept.addActionListener(this);
    this.m_size24 = new JRadioButtonMenuItem("Size 24", false);
    this.m_size22 = new JRadioButtonMenuItem("Size 22", false);
    this.m_size20 = new JRadioButtonMenuItem("Size 20", false);
    this.m_size18 = new JRadioButtonMenuItem("Size 18", false);
    this.m_size16 = new JRadioButtonMenuItem("Size 16", false);
    this.m_size14 = new JRadioButtonMenuItem("Size 14", false);
    this.m_size12 = new JRadioButtonMenuItem("Size 12", true);
    this.m_size10 = new JRadioButtonMenuItem("Size 10", false);
    this.m_size8 = new JRadioButtonMenuItem("Size 8", false);
    this.m_size6 = new JRadioButtonMenuItem("Size 6", false);
    this.m_size4 = new JRadioButtonMenuItem("Size 4", false);
    this.m_size2 = new JRadioButtonMenuItem("Size 2", false);
    this.m_size1 = new JRadioButtonMenuItem("Size 1", false);
    this.m_size24.setActionCommand("Size 24");
    this.m_size22.setActionCommand("Size 22");
    this.m_size20.setActionCommand("Size 20");
    this.m_size18.setActionCommand("Size 18");
    this.m_size16.setActionCommand("Size 16");
    this.m_size14.setActionCommand("Size 14");
    this.m_size12.setActionCommand("Size 12");
    this.m_size10.setActionCommand("Size 10");
    this.m_size8.setActionCommand("Size 8");
    this.m_size6.setActionCommand("Size 6");
    this.m_size4.setActionCommand("Size 4");
    this.m_size2.setActionCommand("Size 2");
    this.m_size1.setActionCommand("Size 1");
    this.m_selectFontGroup.add(this.m_size24);
    this.m_selectFontGroup.add(this.m_size22);
    this.m_selectFontGroup.add(this.m_size20);
    this.m_selectFontGroup.add(this.m_size18);
    this.m_selectFontGroup.add(this.m_size16);
    this.m_selectFontGroup.add(this.m_size14);
    this.m_selectFontGroup.add(this.m_size12);
    this.m_selectFontGroup.add(this.m_size10);
    this.m_selectFontGroup.add(this.m_size8);
    this.m_selectFontGroup.add(this.m_size6);
    this.m_selectFontGroup.add(this.m_size4);
    this.m_selectFontGroup.add(this.m_size2);
    this.m_selectFontGroup.add(this.m_size1);
    this.m_selectFont.add(this.m_size24);
    this.m_selectFont.add(this.m_size22);
    this.m_selectFont.add(this.m_size20);
    this.m_selectFont.add(this.m_size18);
    this.m_selectFont.add(this.m_size16);
    this.m_selectFont.add(this.m_size14);
    this.m_selectFont.add(this.m_size12);
    this.m_selectFont.add(this.m_size10);
    this.m_selectFont.add(this.m_size8);
    this.m_selectFont.add(this.m_size6);
    this.m_selectFont.add(this.m_size4);
    this.m_selectFont.add(this.m_size2);
    this.m_selectFont.add(this.m_size1);
    this.m_size24.addItemListener(this);
    this.m_size22.addItemListener(this);
    this.m_size20.addItemListener(this);
    this.m_size18.addItemListener(this);
    this.m_size16.addItemListener(this);
    this.m_size14.addItemListener(this);
    this.m_size12.addItemListener(this);
    this.m_size10.addItemListener(this);
    this.m_size8.addItemListener(this);
    this.m_size6.addItemListener(this);
    this.m_size4.addItemListener(this);
    this.m_size2.addItemListener(this);
    this.m_size1.addItemListener(this);
    this.m_nodeMenu = new JPopupMenu();
    this.m_visualise = new JMenuItem("Visualize The Node");
    this.m_visualise.setActionCommand("Visualize The Node");
    this.m_visualise.addActionListener(this);
    this.m_nodeMenu.add(this.m_visualise);
    if (this.m_listener != null) {
      this.m_remChildren = new JMenuItem("Remove Child Nodes");
      this.m_remChildren.setActionCommand("Remove Child Nodes");
      this.m_remChildren.addActionListener(this);
      this.m_nodeMenu.add(this.m_remChildren);
      this.m_classifyChild = new JMenuItem("Use Classifier...");
      this.m_classifyChild.setActionCommand("classify_child");
      this.m_classifyChild.addActionListener(this);
      this.m_nodeMenu.add(this.m_classifyChild);
    } 
    this.m_focusNode = -1;
    this.m_highlightNode = -1;
    addMouseMotionListener(this);
    addMouseListener(this);
    this.m_frameLimiter.setRepeats(false);
    this.m_frameLimiter.start();
  }
  
  public TreeVisualizer(TreeDisplayListener paramTreeDisplayListener, Node paramNode, NodePlace paramNodePlace) {
    setBorder(BorderFactory.createTitledBorder("Tree View"));
    this.m_listener = paramTreeDisplayListener;
    this.m_topNode = paramNode;
    this.m_placer = paramNodePlace;
    this.m_placer.place(this.m_topNode);
    this.m_viewPos = new Dimension(0, 0);
    this.m_viewSize = new Dimension(800, 600);
    this.m_nViewPos = new Dimension(0, 0);
    this.m_nViewSize = new Dimension(800, 600);
    this.m_scaling = 0;
    this.m_numNodes = Node.getCount(this.m_topNode, 0);
    this.m_numLevels = Node.getHeight(this.m_topNode, 0);
    this.m_nodes = new NodeInfo[this.m_numNodes];
    this.m_edges = new EdgeInfo[this.m_numNodes - 1];
    arrayFill(this.m_topNode, this.m_nodes, this.m_edges);
    changeFontSize(12);
    this.m_mouseState = 0;
    this.m_oldMousePos = new Dimension(0, 0);
    this.m_newMousePos = new Dimension(0, 0);
    this.m_frameLimiter = new Timer(120, this);
    this.m_winMenu = new JPopupMenu();
    this.m_topN = new JMenuItem("Center on Top Node");
    this.m_topN.setActionCommand("Center on Top Node");
    this.m_fitToScreen = new JMenuItem("Fit to Screen");
    this.m_fitToScreen.setActionCommand("Fit to Screen");
    this.m_selectFont = new JMenu("Select Font");
    this.m_selectFont.setActionCommand("Select Font");
    this.m_autoScale = new JMenuItem("Auto Scale");
    this.m_autoScale.setActionCommand("Auto Scale");
    this.m_selectFontGroup = new ButtonGroup();
    this.m_accept = new JMenuItem("Accept The Tree");
    this.m_accept.setActionCommand("Accept The Tree");
    this.m_winMenu.add(this.m_topN);
    this.m_winMenu.addSeparator();
    this.m_winMenu.add(this.m_fitToScreen);
    this.m_winMenu.add(this.m_autoScale);
    this.m_winMenu.addSeparator();
    this.m_winMenu.addSeparator();
    this.m_winMenu.add(this.m_selectFont);
    this.m_winMenu.addSeparator();
    if (this.m_listener != null)
      this.m_winMenu.add(this.m_accept); 
    this.m_topN.addActionListener(this);
    this.m_fitToScreen.addActionListener(this);
    this.m_autoScale.addActionListener(this);
    this.m_accept.addActionListener(this);
    this.m_size24 = new JRadioButtonMenuItem("Size 24", false);
    this.m_size22 = new JRadioButtonMenuItem("Size 22", false);
    this.m_size20 = new JRadioButtonMenuItem("Size 20", false);
    this.m_size18 = new JRadioButtonMenuItem("Size 18", false);
    this.m_size16 = new JRadioButtonMenuItem("Size 16", false);
    this.m_size14 = new JRadioButtonMenuItem("Size 14", false);
    this.m_size12 = new JRadioButtonMenuItem("Size 12", true);
    this.m_size10 = new JRadioButtonMenuItem("Size 10", false);
    this.m_size8 = new JRadioButtonMenuItem("Size 8", false);
    this.m_size6 = new JRadioButtonMenuItem("Size 6", false);
    this.m_size4 = new JRadioButtonMenuItem("Size 4", false);
    this.m_size2 = new JRadioButtonMenuItem("Size 2", false);
    this.m_size1 = new JRadioButtonMenuItem("Size 1", false);
    this.m_size24.setActionCommand("Size 24");
    this.m_size22.setActionCommand("Size 22");
    this.m_size20.setActionCommand("Size 20");
    this.m_size18.setActionCommand("Size 18");
    this.m_size16.setActionCommand("Size 16");
    this.m_size14.setActionCommand("Size 14");
    this.m_size12.setActionCommand("Size 12");
    this.m_size10.setActionCommand("Size 10");
    this.m_size8.setActionCommand("Size 8");
    this.m_size6.setActionCommand("Size 6");
    this.m_size4.setActionCommand("Size 4");
    this.m_size2.setActionCommand("Size 2");
    this.m_size1.setActionCommand("Size 1");
    this.m_selectFontGroup.add(this.m_size24);
    this.m_selectFontGroup.add(this.m_size22);
    this.m_selectFontGroup.add(this.m_size20);
    this.m_selectFontGroup.add(this.m_size18);
    this.m_selectFontGroup.add(this.m_size16);
    this.m_selectFontGroup.add(this.m_size14);
    this.m_selectFontGroup.add(this.m_size12);
    this.m_selectFontGroup.add(this.m_size10);
    this.m_selectFontGroup.add(this.m_size8);
    this.m_selectFontGroup.add(this.m_size6);
    this.m_selectFontGroup.add(this.m_size4);
    this.m_selectFontGroup.add(this.m_size2);
    this.m_selectFontGroup.add(this.m_size1);
    this.m_selectFont.add(this.m_size24);
    this.m_selectFont.add(this.m_size22);
    this.m_selectFont.add(this.m_size20);
    this.m_selectFont.add(this.m_size18);
    this.m_selectFont.add(this.m_size16);
    this.m_selectFont.add(this.m_size14);
    this.m_selectFont.add(this.m_size12);
    this.m_selectFont.add(this.m_size10);
    this.m_selectFont.add(this.m_size8);
    this.m_selectFont.add(this.m_size6);
    this.m_selectFont.add(this.m_size4);
    this.m_selectFont.add(this.m_size2);
    this.m_selectFont.add(this.m_size1);
    this.m_size24.addItemListener(this);
    this.m_size22.addItemListener(this);
    this.m_size20.addItemListener(this);
    this.m_size18.addItemListener(this);
    this.m_size16.addItemListener(this);
    this.m_size14.addItemListener(this);
    this.m_size12.addItemListener(this);
    this.m_size10.addItemListener(this);
    this.m_size8.addItemListener(this);
    this.m_size6.addItemListener(this);
    this.m_size4.addItemListener(this);
    this.m_size2.addItemListener(this);
    this.m_size1.addItemListener(this);
    this.m_nodeMenu = new JPopupMenu();
    this.m_visualise = new JMenuItem("Visualize The Node");
    this.m_visualise.setActionCommand("Visualize The Node");
    this.m_visualise.addActionListener(this);
    this.m_nodeMenu.add(this.m_visualise);
    if (this.m_listener != null) {
      this.m_remChildren = new JMenuItem("Remove Child Nodes");
      this.m_remChildren.setActionCommand("Remove Child Nodes");
      this.m_remChildren.addActionListener(this);
      this.m_nodeMenu.add(this.m_remChildren);
      this.m_classifyChild = new JMenuItem("Use Classifier...");
      this.m_classifyChild.setActionCommand("classify_child");
      this.m_classifyChild.addActionListener(this);
      this.m_nodeMenu.add(this.m_classifyChild);
      this.m_sendInstances = new JMenuItem("Add Instances To Viewer");
      this.m_sendInstances.setActionCommand("send_instances");
      this.m_sendInstances.addActionListener(this);
      this.m_nodeMenu.add(this.m_sendInstances);
    } 
    this.m_focusNode = -1;
    this.m_highlightNode = -1;
    addMouseMotionListener(this);
    addMouseListener(this);
    this.m_frameLimiter.setRepeats(false);
    this.m_frameLimiter.start();
  }
  
  public void fitToScreen() {
    getScreenFit(this.m_viewPos, this.m_viewSize);
    repaint();
  }
  
  private void getScreenFit(Dimension paramDimension1, Dimension paramDimension2) {
    int i = 1000000;
    int j = -1000000;
    int k = 1000000;
    int m = -1000000;
    byte b1 = 0;
    int n = -1000000;
    int i1 = -1000000;
    for (byte b2 = 0; b2 < this.m_numNodes; b2++) {
      calcScreenCoords(b2);
      if ((this.m_nodes[b2]).m_center - (this.m_nodes[b2]).m_side < i)
        i = (this.m_nodes[b2]).m_center - (this.m_nodes[b2]).m_side; 
      if ((this.m_nodes[b2]).m_center < k)
        k = (this.m_nodes[b2]).m_center; 
      if ((this.m_nodes[b2]).m_center + (this.m_nodes[b2]).m_side > j)
        j = (this.m_nodes[b2]).m_center + (this.m_nodes[b2]).m_side; 
      if ((this.m_nodes[b2]).m_center > m) {
        m = (this.m_nodes[b2]).m_center;
        b1 = b2;
      } 
      if ((this.m_nodes[b2]).m_top + (this.m_nodes[b2]).m_height > n)
        n = (this.m_nodes[b2]).m_top + (this.m_nodes[b2]).m_height; 
      if ((this.m_nodes[b2]).m_top > i1)
        i1 = (this.m_nodes[b2]).m_top; 
    } 
    paramDimension2.width = getWidth();
    paramDimension2.width -= k - i + j - m + 30;
    paramDimension2.height = getHeight() - n + i1 - 40;
    if ((this.m_nodes[b1]).m_node.getCenter() != 0.0D && k != m)
      paramDimension2.width = (int)(paramDimension2.width / (this.m_nodes[b1]).m_node.getCenter()); 
    if (paramDimension2.width < 10)
      paramDimension2.width = 10; 
    if (paramDimension2.height < 10)
      paramDimension2.height = 10; 
    paramDimension1.width = (k - i + j - m) / 2 + 15;
    paramDimension1.height = (n - i1) / 2 + 20;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    if (paramActionEvent.getActionCommand() == null) {
      if (this.m_scaling == 0) {
        repaint();
      } else {
        animateScaling(this.m_nViewPos, this.m_nViewSize, this.m_scaling);
      } 
    } else if (paramActionEvent.getActionCommand().equals("Fit to Screen")) {
      Dimension dimension1 = new Dimension();
      Dimension dimension2 = new Dimension();
      getScreenFit(dimension1, dimension2);
      animateScaling(dimension1, dimension2, 10);
    } else if (paramActionEvent.getActionCommand().equals("Center on Top Node")) {
      int i = (int)(this.m_topNode.getCenter() * this.m_viewSize.width);
      int j = (int)(this.m_topNode.getTop() * this.m_viewSize.height);
      Dimension dimension = new Dimension((getSize()).width / 2 - i, (getSize()).width / 6 - j);
      animateScaling(dimension, this.m_viewSize, 10);
    } else if (paramActionEvent.getActionCommand().equals("Auto Scale")) {
      autoScale();
    } else if (paramActionEvent.getActionCommand().equals("Visualize The Node")) {
      if (this.m_focusNode >= 0) {
        Instances instances;
        if ((instances = (this.m_nodes[this.m_focusNode]).m_node.getInstances()) != null) {
          VisualizePanel visualizePanel = new VisualizePanel();
          visualizePanel.setInstances(instances);
          JFrame jFrame = new JFrame();
          jFrame.setSize(400, 300);
          jFrame.getContentPane().add((Component)visualizePanel);
          jFrame.setVisible(true);
        } else {
          JOptionPane.showMessageDialog((Component)this, "Sorry, there is no availble Instances data for this Node.", "Sorry!", 2);
        } 
      } else {
        JOptionPane.showMessageDialog((Component)this, "Error, there is no selected Node to perform this operation on.", "Error!", 0);
      } 
    } else if (paramActionEvent.getActionCommand().equals("Create Child Nodes")) {
      if (this.m_focusNode >= 0) {
        if (this.m_listener != null) {
          this.m_listener.userCommand(new TreeDisplayEvent(1, (this.m_nodes[this.m_focusNode]).m_node.getRefer()));
        } else {
          JOptionPane.showMessageDialog((Component)this, "Sorry, there is no available Decision Tree to perform this operation on.", "Sorry!", 2);
        } 
      } else {
        JOptionPane.showMessageDialog((Component)this, "Error, there is no selected Node to perform this operation on.", "Error!", 0);
      } 
    } else if (paramActionEvent.getActionCommand().equals("Remove Child Nodes")) {
      if (this.m_focusNode >= 0) {
        if (this.m_listener != null) {
          this.m_listener.userCommand(new TreeDisplayEvent(2, (this.m_nodes[this.m_focusNode]).m_node.getRefer()));
        } else {
          JOptionPane.showMessageDialog((Component)this, "Sorry, there is no available Decsion Tree to perform this operation on.", "Sorry!", 2);
        } 
      } else {
        JOptionPane.showMessageDialog((Component)this, "Error, there is no selected Node to perform this operation on.", "Error!", 0);
      } 
    } else if (paramActionEvent.getActionCommand().equals("classify_child")) {
      if (this.m_focusNode >= 0) {
        if (this.m_listener != null) {
          this.m_listener.userCommand(new TreeDisplayEvent(4, (this.m_nodes[this.m_focusNode]).m_node.getRefer()));
        } else {
          JOptionPane.showMessageDialog((Component)this, "Sorry, there is no available Decsion Tree to perform this operation on.", "Sorry!", 2);
        } 
      } else {
        JOptionPane.showMessageDialog((Component)this, "Error, there is no selected Node to perform this operation on.", "Error!", 0);
      } 
    } else if (paramActionEvent.getActionCommand().equals("send_instances")) {
      if (this.m_focusNode >= 0) {
        if (this.m_listener != null) {
          this.m_listener.userCommand(new TreeDisplayEvent(5, (this.m_nodes[this.m_focusNode]).m_node.getRefer()));
        } else {
          JOptionPane.showMessageDialog((Component)this, "Sorry, there is no available Decsion Tree to perform this operation on.", "Sorry!", 2);
        } 
      } else {
        JOptionPane.showMessageDialog((Component)this, "Error, there is no selected Node to perform this operation on.", "Error!", 0);
      } 
    } else if (paramActionEvent.getActionCommand().equals("Accept The Tree")) {
      if (this.m_listener != null) {
        this.m_listener.userCommand(new TreeDisplayEvent(3, null));
      } else {
        JOptionPane.showMessageDialog((Component)this, "Sorry, there is no available Decision Tree to perform this operation on.", "Sorry!", 2);
      } 
    } 
  }
  
  public void itemStateChanged(ItemEvent paramItemEvent) {
    JRadioButtonMenuItem jRadioButtonMenuItem = (JRadioButtonMenuItem)paramItemEvent.getSource();
    if (jRadioButtonMenuItem.getActionCommand().equals("Size 24")) {
      changeFontSize(24);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 22")) {
      changeFontSize(22);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 20")) {
      changeFontSize(20);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 18")) {
      changeFontSize(18);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 16")) {
      changeFontSize(16);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 14")) {
      changeFontSize(14);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 12")) {
      changeFontSize(12);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 10")) {
      changeFontSize(10);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 8")) {
      changeFontSize(8);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 6")) {
      changeFontSize(6);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 4")) {
      changeFontSize(4);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 2")) {
      changeFontSize(2);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Size 1")) {
      changeFontSize(1);
    } else if (jRadioButtonMenuItem.getActionCommand().equals("Hide Descendants")) {
    
    } 
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {
    if (this.m_clickAvailable) {
      byte b = -1;
      for (byte b1 = 0; b1 < this.m_numNodes; b1++) {
        if ((this.m_nodes[b1]).m_quad == 18) {
          calcScreenCoords(b1);
          if (paramMouseEvent.getX() <= (this.m_nodes[b1]).m_center + (this.m_nodes[b1]).m_side && paramMouseEvent.getX() >= (this.m_nodes[b1]).m_center - (this.m_nodes[b1]).m_side && paramMouseEvent.getY() >= (this.m_nodes[b1]).m_top && paramMouseEvent.getY() <= (this.m_nodes[b1]).m_top + (this.m_nodes[b1]).m_height)
            b = b1; 
          (this.m_nodes[b1]).m_top = 32000;
        } 
      } 
      this.m_focusNode = b;
      if (this.m_focusNode != -1)
        if (this.m_listener != null) {
          actionPerformed(new ActionEvent(this, 32000, "Create Child Nodes"));
        } else {
          actionPerformed(new ActionEvent(this, 32000, "Visualize The Node"));
        }  
    } 
  }
  
  public void mousePressed(MouseEvent paramMouseEvent) {
    this.m_frameLimiter.setRepeats(true);
    if ((paramMouseEvent.getModifiers() & 0x10) != 0 && !paramMouseEvent.isAltDown() && this.m_mouseState == 0 && this.m_scaling == 0) {
      if ((paramMouseEvent.getModifiers() & 0x2) != 0 && (paramMouseEvent.getModifiers() & 0x1) == 0) {
        this.m_mouseState = 2;
      } else if ((paramMouseEvent.getModifiers() & 0x1) != 0 && (paramMouseEvent.getModifiers() & 0x2) == 0) {
        this.m_oldMousePos.width = paramMouseEvent.getX();
        this.m_oldMousePos.height = paramMouseEvent.getY();
        this.m_newMousePos.width = paramMouseEvent.getX();
        this.m_newMousePos.height = paramMouseEvent.getY();
        this.m_mouseState = 3;
        Graphics graphics = getGraphics();
        graphics.setColor(Color.black);
        graphics.setXORMode(Color.white);
        graphics.drawRect(this.m_oldMousePos.width, this.m_oldMousePos.height, this.m_newMousePos.width - this.m_oldMousePos.width, this.m_newMousePos.height - this.m_oldMousePos.height);
        graphics.dispose();
      } else {
        this.m_oldMousePos.width = paramMouseEvent.getX();
        this.m_oldMousePos.height = paramMouseEvent.getY();
        this.m_newMousePos.width = paramMouseEvent.getX();
        this.m_newMousePos.height = paramMouseEvent.getY();
        this.m_mouseState = 1;
        this.m_frameLimiter.start();
      } 
    } else if (this.m_mouseState != 0 || this.m_scaling == 0) {
    
    } 
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent) {
    if (this.m_mouseState == 1) {
      this.m_clickAvailable = true;
    } else {
      this.m_clickAvailable = false;
    } 
    if (this.m_mouseState == 2 && mouseInBounds(paramMouseEvent)) {
      this.m_mouseState = 0;
      Dimension dimension1 = new Dimension(this.m_viewSize.width / 2, this.m_viewSize.height / 2);
      if (dimension1.width < 10)
        dimension1.width = 10; 
      if (dimension1.height < 10)
        dimension1.height = 10; 
      Dimension dimension2 = getSize();
      Dimension dimension3 = new Dimension((int)((dimension2.width / 2) - (dimension2.width / 2.0D - this.m_viewPos.width) / 2.0D), (int)((dimension2.height / 2) - (dimension2.height / 2.0D - this.m_viewPos.height) / 2.0D));
      animateScaling(dimension3, dimension1, 10);
    } else if (this.m_mouseState == 3) {
      this.m_mouseState = 0;
      Graphics graphics = getGraphics();
      graphics.setColor(Color.black);
      graphics.setXORMode(Color.white);
      graphics.drawRect(this.m_oldMousePos.width, this.m_oldMousePos.height, this.m_newMousePos.width - this.m_oldMousePos.width, this.m_newMousePos.height - this.m_oldMousePos.height);
      graphics.dispose();
      int i = this.m_newMousePos.width - this.m_oldMousePos.width;
      int j = this.m_newMousePos.height - this.m_oldMousePos.height;
      if (i >= 1 && j >= 1 && mouseInBounds(paramMouseEvent) && (getSize()).width / i <= 6 && (getSize()).height / j <= 6) {
        Dimension dimension1 = new Dimension();
        Dimension dimension2 = new Dimension();
        double d1 = (getSize()).width / i;
        double d2 = (getSize()).height / j;
        dimension2.width = (int)((this.m_oldMousePos.width - this.m_viewPos.width) * -d1);
        dimension2.height = (int)((this.m_oldMousePos.height - this.m_viewPos.height) * -d2);
        dimension1.width = (int)(this.m_viewSize.width * d1);
        dimension1.height = (int)(this.m_viewSize.height * d2);
        animateScaling(dimension2, dimension1, 10);
      } 
    } else if (this.m_mouseState == 0 && this.m_scaling == 0) {
      this.m_mouseState = 0;
      setFont(new Font("A Name", 0, 12));
      byte b = -1;
      for (byte b1 = 0; b1 < this.m_numNodes; b1++) {
        if ((this.m_nodes[b1]).m_quad == 18) {
          calcScreenCoords(b1);
          if (paramMouseEvent.getX() <= (this.m_nodes[b1]).m_center + (this.m_nodes[b1]).m_side && paramMouseEvent.getX() >= (this.m_nodes[b1]).m_center - (this.m_nodes[b1]).m_side && paramMouseEvent.getY() >= (this.m_nodes[b1]).m_top && paramMouseEvent.getY() <= (this.m_nodes[b1]).m_top + (this.m_nodes[b1]).m_height)
            b = b1; 
          (this.m_nodes[b1]).m_top = 32000;
        } 
      } 
      if (b == -1) {
        this.m_winMenu.show((Component)this, paramMouseEvent.getX(), paramMouseEvent.getY());
      } else {
        this.m_focusNode = b;
        this.m_nodeMenu.show((Component)this, paramMouseEvent.getX(), paramMouseEvent.getY());
      } 
      setFont(this.m_currentFont);
    } else if (this.m_mouseState == 1) {
      this.m_mouseState = 0;
      this.m_frameLimiter.stop();
      repaint();
    } 
  }
  
  private boolean mouseInBounds(MouseEvent paramMouseEvent) {
    return !(paramMouseEvent.getX() < 0 || paramMouseEvent.getY() < 0 || paramMouseEvent.getX() > (getSize()).width || paramMouseEvent.getY() > (getSize()).height);
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent) {
    if (this.m_mouseState == 1) {
      this.m_oldMousePos.width = this.m_newMousePos.width;
      this.m_oldMousePos.height = this.m_newMousePos.height;
      this.m_newMousePos.width = paramMouseEvent.getX();
      this.m_newMousePos.height = paramMouseEvent.getY();
      this.m_viewPos.width += this.m_newMousePos.width - this.m_oldMousePos.width;
      this.m_viewPos.height += this.m_newMousePos.height - this.m_oldMousePos.height;
    } else if (this.m_mouseState == 3) {
      Graphics graphics = getGraphics();
      graphics.setColor(Color.black);
      graphics.setXORMode(Color.white);
      graphics.drawRect(this.m_oldMousePos.width, this.m_oldMousePos.height, this.m_newMousePos.width - this.m_oldMousePos.width, this.m_newMousePos.height - this.m_oldMousePos.height);
      this.m_newMousePos.width = paramMouseEvent.getX();
      this.m_newMousePos.height = paramMouseEvent.getY();
      graphics.drawRect(this.m_oldMousePos.width, this.m_oldMousePos.height, this.m_newMousePos.width - this.m_oldMousePos.width, this.m_newMousePos.height - this.m_oldMousePos.height);
      graphics.dispose();
    } 
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
  
  public void mouseEntered(MouseEvent paramMouseEvent) {}
  
  public void mouseExited(MouseEvent paramMouseEvent) {}
  
  public void setHighlight(String paramString) {
    for (byte b = 0; b < this.m_numNodes; b++) {
      if (paramString.equals((this.m_nodes[b]).m_node.getRefer()))
        this.m_highlightNode = b; 
    } 
    repaint();
  }
  
  public void paintComponent(Graphics paramGraphics) {
    paramGraphics.clearRect(0, 0, (getSize()).width, (getSize()).height);
    paramGraphics.setClip(3, 7, getWidth() - 6, getHeight() - 10);
    painter(paramGraphics);
    paramGraphics.setClip(0, 0, getWidth(), getHeight());
  }
  
  private void painter(Graphics paramGraphics) {
    double d1 = (-this.m_viewPos.width - 50) / this.m_viewSize.width;
    double d2 = ((getSize()).width - this.m_viewPos.width + 50) / this.m_viewSize.width;
    double d3 = (-this.m_viewPos.height - 50) / this.m_viewSize.height;
    double d4 = ((getSize()).height - this.m_viewPos.height + 50) / this.m_viewSize.height;
    byte b1 = 0;
    byte b2 = 0;
    byte b3;
    for (b3 = 0; b3 < this.m_numNodes; b3++) {
      Node node = (this.m_nodes[b3]).m_node;
      if ((this.m_nodes[b3]).m_change) {
        double d5 = node.getTop();
        if (d5 < d3) {
          b1 = 8;
        } else if (d5 > d4) {
          b1 = 32;
        } else {
          b1 = 16;
        } 
      } 
      double d = node.getCenter();
      if (d < d1) {
        b2 = 4;
      } else if (d > d2) {
        b2 = 1;
      } else {
        b2 = 2;
      } 
      (this.m_nodes[b3]).m_quad = b1 | b2;
      if ((this.m_nodes[b3]).m_parent >= 0) {
        int i = (this.m_nodes[(this.m_edges[(this.m_nodes[b3]).m_parent]).m_parent]).m_quad;
        int j = (this.m_nodes[b3]).m_quad;
        if ((j & 0x8) != 8 && (i & 0x20) != 32 && ((j & 0x4) != 4 || (i & 0x4) != 4) && ((j & 0x1) != 1 || (i & 0x1) != 1))
          drawLine((this.m_nodes[b3]).m_parent, paramGraphics); 
      } 
    } 
    for (b3 = 0; b3 < this.m_numNodes; b3++) {
      if ((this.m_nodes[b3]).m_quad == 18)
        drawNode(b3, paramGraphics); 
    } 
    if (this.m_highlightNode >= 0 && this.m_highlightNode < this.m_numNodes && (this.m_nodes[this.m_highlightNode]).m_quad == 18) {
      Color color = (this.m_nodes[this.m_highlightNode]).m_node.getColor();
      paramGraphics.setColor(new Color((color.getRed() + 125) % 256, (color.getGreen() + 125) % 256, (color.getBlue() + 125) % 256));
      if ((this.m_nodes[this.m_highlightNode]).m_node.getShape() == 1) {
        paramGraphics.drawRect((this.m_nodes[this.m_highlightNode]).m_center - (this.m_nodes[this.m_highlightNode]).m_side, (this.m_nodes[this.m_highlightNode]).m_top, (this.m_nodes[this.m_highlightNode]).m_width, (this.m_nodes[this.m_highlightNode]).m_height);
        paramGraphics.drawRect((this.m_nodes[this.m_highlightNode]).m_center - (this.m_nodes[this.m_highlightNode]).m_side + 1, (this.m_nodes[this.m_highlightNode]).m_top + 1, (this.m_nodes[this.m_highlightNode]).m_width - 2, (this.m_nodes[this.m_highlightNode]).m_height - 2);
      } else if ((this.m_nodes[this.m_highlightNode]).m_node.getShape() == 2) {
        paramGraphics.drawOval((this.m_nodes[this.m_highlightNode]).m_center - (this.m_nodes[this.m_highlightNode]).m_side, (this.m_nodes[this.m_highlightNode]).m_top, (this.m_nodes[this.m_highlightNode]).m_width, (this.m_nodes[this.m_highlightNode]).m_height);
        paramGraphics.drawOval((this.m_nodes[this.m_highlightNode]).m_center - (this.m_nodes[this.m_highlightNode]).m_side + 1, (this.m_nodes[this.m_highlightNode]).m_top + 1, (this.m_nodes[this.m_highlightNode]).m_width - 2, (this.m_nodes[this.m_highlightNode]).m_height - 2);
      } 
    } 
    for (b3 = 0; b3 < this.m_numNodes; b3++)
      (this.m_nodes[b3]).m_top = 32000; 
  }
  
  private void drawNode(int paramInt, Graphics paramGraphics) {
    paramGraphics.setColor((this.m_nodes[paramInt]).m_node.getColor());
    paramGraphics.setPaintMode();
    calcScreenCoords(paramInt);
    int i = (this.m_nodes[paramInt]).m_center - (this.m_nodes[paramInt]).m_side;
    int j = (this.m_nodes[paramInt]).m_top;
    if ((this.m_nodes[paramInt]).m_node.getShape() == 1) {
      paramGraphics.fill3DRect(i, j, (this.m_nodes[paramInt]).m_width, (this.m_nodes[paramInt]).m_height, true);
      drawText(i, j, paramInt, false, paramGraphics);
    } else if ((this.m_nodes[paramInt]).m_node.getShape() == 2) {
      paramGraphics.fillOval(i, j, (this.m_nodes[paramInt]).m_width, (this.m_nodes[paramInt]).m_height);
      drawText(i, j + (int)((this.m_nodes[paramInt]).m_height * 0.15D), paramInt, false, paramGraphics);
    } 
  }
  
  private void drawLine(int paramInt, Graphics paramGraphics) {
    int i = (this.m_edges[paramInt]).m_parent;
    int j = (this.m_edges[paramInt]).m_child;
    calcScreenCoords(j);
    calcScreenCoords(i);
    paramGraphics.setColor(Color.black);
    paramGraphics.setPaintMode();
    if (this.m_currentFont.getSize() < 2) {
      paramGraphics.drawLine((this.m_nodes[i]).m_center, (this.m_nodes[i]).m_top + (this.m_nodes[i]).m_height, (this.m_nodes[j]).m_center, (this.m_nodes[j]).m_top);
    } else {
      int k = (this.m_nodes[j]).m_center - (this.m_nodes[i]).m_center;
      int m = (this.m_nodes[j]).m_top - (this.m_nodes[i]).m_top + (this.m_nodes[i]).m_height;
      int n = k / 2;
      int i1 = m / 2;
      int i2 = (this.m_nodes[i]).m_center + n;
      int i3 = (this.m_nodes[i]).m_top + (this.m_nodes[i]).m_height + i1;
      int i4 = (this.m_edges[paramInt]).m_tb;
      int i5 = (int)(k / m * (i1 - i4)) + (this.m_nodes[i]).m_center;
      drawText(i2 - (this.m_edges[paramInt]).m_side, i3 - i4, paramInt, true, paramGraphics);
      if (i5 > i2 - (this.m_edges[paramInt]).m_side && i5 < i2 + (this.m_edges[paramInt]).m_side) {
        paramGraphics.drawLine((this.m_nodes[i]).m_center, (this.m_nodes[i]).m_top + (this.m_nodes[i]).m_height, i5, i3 - i4);
        paramGraphics.drawLine(i2 * 2 - i5, i3 + i4, (this.m_nodes[j]).m_center, (this.m_nodes[j]).m_top);
      } else {
        i4 = (this.m_edges[paramInt]).m_side;
        if (k < 0)
          i4 *= -1; 
        i5 = (int)(m / k * (n - i4)) + (this.m_nodes[i]).m_top + (this.m_nodes[i]).m_height;
        paramGraphics.drawLine((this.m_nodes[i]).m_center, (this.m_nodes[i]).m_top + (this.m_nodes[i]).m_height, i2 - i4, i5);
        paramGraphics.drawLine(i2 + i4, i3 * 2 - i5, (this.m_nodes[j]).m_center, (this.m_nodes[j]).m_top);
      } 
    } 
  }
  
  private void drawText(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, Graphics paramGraphics) {
    paramGraphics.setPaintMode();
    paramGraphics.setColor(Color.black);
    if (paramBoolean) {
      Edge edge = (this.m_edges[paramInt3]).m_edge;
      String str;
      for (byte b = 0; (str = edge.getLine(b)) != null; b++)
        paramGraphics.drawString(str, ((this.m_edges[paramInt3]).m_width - this.m_fontSize.stringWidth(str)) / 2 + paramInt1, paramInt2 + (b + 1) * this.m_fontSize.getHeight()); 
    } else {
      Node node = (this.m_nodes[paramInt3]).m_node;
      String str;
      for (byte b = 0; (str = node.getLine(b)) != null; b++)
        paramGraphics.drawString(str, ((this.m_nodes[paramInt3]).m_width - this.m_fontSize.stringWidth(str)) / 2 + paramInt1, paramInt2 + (b + 1) * this.m_fontSize.getHeight()); 
    } 
  }
  
  private void calcScreenCoords(int paramInt) {
    if ((this.m_nodes[paramInt]).m_top == 32000) {
      (this.m_nodes[paramInt]).m_top = (int)((this.m_nodes[paramInt]).m_node.getTop() * this.m_viewSize.height) + this.m_viewPos.height;
      (this.m_nodes[paramInt]).m_center = (int)((this.m_nodes[paramInt]).m_node.getCenter() * this.m_viewSize.width) + this.m_viewPos.width;
    } 
  }
  
  private void autoScale() {
    Dimension dimension1 = new Dimension(10, 10);
    if (this.m_numNodes <= 1)
      return; 
    int i = ((this.m_nodes[0]).m_height + 40) * this.m_numLevels;
    if (i > dimension1.height)
      dimension1.height = i; 
    int j;
    for (j = 0; j < this.m_numNodes - 1; j++) {
      calcScreenCoords(j);
      calcScreenCoords(j + 1);
      if (!(this.m_nodes[j + 1]).m_change) {
        i = (this.m_nodes[j + 1]).m_center - (this.m_nodes[j]).m_center;
        if (i <= 0)
          i = 1; 
        i = (6 + (this.m_nodes[j]).m_side + (this.m_nodes[j + 1]).m_side) * this.m_viewSize.width / i;
        if (i > dimension1.width)
          dimension1.width = i; 
      } 
      i = ((this.m_nodes[j + 1]).m_height + 40) * this.m_numLevels;
      if (i > dimension1.height)
        dimension1.height = i; 
    } 
    j = (this.m_nodes[(this.m_edges[0]).m_parent]).m_top;
    int k = (this.m_nodes[(this.m_edges[0]).m_child]).m_top;
    i = k - j;
    if (i <= 0)
      i = 1; 
    i = (60 + (this.m_edges[0]).m_height + (this.m_nodes[(this.m_edges[0]).m_parent]).m_height) * this.m_viewSize.height / i;
    if (i > dimension1.height)
      dimension1.height = i; 
    for (byte b1 = 0; b1 < this.m_numNodes - 2; b1++) {
      if (!(this.m_nodes[(this.m_edges[b1 + 1]).m_child]).m_change) {
        int m = (this.m_nodes[(this.m_edges[b1]).m_child]).m_center - (this.m_nodes[(this.m_edges[b1]).m_parent]).m_center;
        m /= 2;
        m += (this.m_nodes[(this.m_edges[b1]).m_parent]).m_center;
        int n = (this.m_nodes[(this.m_edges[b1 + 1]).m_child]).m_center - (this.m_nodes[(this.m_edges[b1 + 1]).m_parent]).m_center;
        n /= 2;
        n += (this.m_nodes[(this.m_edges[b1 + 1]).m_parent]).m_center;
        i = n - m;
        if (i <= 0)
          i = 1; 
        i = (12 + (this.m_edges[b1]).m_side + (this.m_edges[b1 + 1]).m_side) * this.m_viewSize.width / i;
        if (i > dimension1.width)
          dimension1.width = i; 
      } 
      j = (this.m_nodes[(this.m_edges[b1 + 1]).m_parent]).m_top;
      k = (this.m_nodes[(this.m_edges[b1 + 1]).m_child]).m_top;
      i = k - j;
      if (i <= 0)
        i = 1; 
      i = (60 + (this.m_edges[b1 + 1]).m_height + (this.m_nodes[(this.m_edges[b1 + 1]).m_parent]).m_height) * this.m_viewSize.height / i;
      if (i > dimension1.height)
        dimension1.height = i; 
    } 
    Dimension dimension2 = getSize();
    Dimension dimension3 = new Dimension();
    dimension3.width = (int)((dimension2.width / 2) - (dimension2.width / 2.0D - this.m_viewPos.width) / this.m_viewSize.width * dimension1.width);
    dimension3.height = (int)((dimension2.height / 2) - (dimension2.height / 2.0D - this.m_viewPos.height) / this.m_viewSize.height * dimension1.height);
    for (byte b2 = 0; b2 < this.m_numNodes; b2++)
      (this.m_nodes[b2]).m_top = 32000; 
    animateScaling(dimension3, dimension1, 10);
  }
  
  private void animateScaling(Dimension paramDimension1, Dimension paramDimension2, int paramInt) {
    if (paramInt == 0) {
      System.out.println("the timer didn't end in time");
      this.m_scaling = 0;
    } else {
      if (this.m_scaling == 0) {
        this.m_frameLimiter.start();
        this.m_nViewPos.width = paramDimension1.width;
        this.m_nViewPos.height = paramDimension1.height;
        this.m_nViewSize.width = paramDimension2.width;
        this.m_nViewSize.height = paramDimension2.height;
        this.m_scaling = paramInt;
      } 
      int i = (paramDimension2.width - this.m_viewSize.width) / paramInt;
      int j = (paramDimension2.height - this.m_viewSize.height) / paramInt;
      int k = (paramDimension1.width - this.m_viewPos.width) / paramInt;
      int m = (paramDimension1.height - this.m_viewPos.height) / paramInt;
      this.m_viewSize.width += i;
      this.m_viewSize.height += j;
      this.m_viewPos.width += k;
      this.m_viewPos.height += m;
      repaint();
      this.m_scaling--;
      if (this.m_scaling == 0)
        this.m_frameLimiter.stop(); 
    } 
  }
  
  private void changeFontSize(int paramInt) {
    setFont(this.m_currentFont = new Font("A Name", 0, paramInt));
    this.m_fontSize = getFontMetrics(getFont());
    for (byte b = 0; b < this.m_numNodes; b++) {
      Dimension dimension = (this.m_nodes[b]).m_node.stringSize(this.m_fontSize);
      if ((this.m_nodes[b]).m_node.getShape() == 1) {
        (this.m_nodes[b]).m_height = dimension.height + 10;
        (this.m_nodes[b]).m_width = dimension.width + 8;
        (this.m_nodes[b]).m_side = (this.m_nodes[b]).m_width / 2;
      } else if ((this.m_nodes[b]).m_node.getShape() == 2) {
        (this.m_nodes[b]).m_height = (int)((dimension.height + 2) * 1.6D);
        (this.m_nodes[b]).m_width = (int)((dimension.width + 2) * 1.6D);
        (this.m_nodes[b]).m_side = (this.m_nodes[b]).m_width / 2;
      } 
      if (b < this.m_numNodes - 1) {
        dimension = (this.m_edges[b]).m_edge.stringSize(this.m_fontSize);
        (this.m_edges[b]).m_height = dimension.height + 8;
        (this.m_edges[b]).m_width = dimension.width + 8;
        (this.m_edges[b]).m_side = (this.m_edges[b]).m_width / 2;
        (this.m_edges[b]).m_tb = (this.m_edges[b]).m_height / 2;
      } 
    } 
  }
  
  private void arrayFill(Node paramNode, NodeInfo[] paramArrayOfNodeInfo, EdgeInfo[] paramArrayOfEdgeInfo) {
    if (paramNode == null || paramArrayOfNodeInfo == null)
      System.exit(1); 
    paramArrayOfNodeInfo[0] = new NodeInfo();
    (paramArrayOfNodeInfo[0]).m_node = paramNode;
    (paramArrayOfNodeInfo[0]).m_parent = -1;
    (paramArrayOfNodeInfo[0]).m_change = true;
    byte b2 = 1;
    double d = paramNode.getTop();
    for (byte b1 = 0; b1 < b2; b1++) {
      Node node = (paramArrayOfNodeInfo[b1]).m_node;
      Edge edge;
      for (byte b = 0; (edge = node.getChild(b)) != null; b++) {
        Node node1 = edge.getTarget();
        paramArrayOfNodeInfo[b2] = new NodeInfo();
        (paramArrayOfNodeInfo[b2]).m_node = node1;
        (paramArrayOfNodeInfo[b2]).m_parent = b2 - 1;
        paramArrayOfEdgeInfo[b2 - 1] = new EdgeInfo();
        (paramArrayOfEdgeInfo[b2 - 1]).m_edge = edge;
        (paramArrayOfEdgeInfo[b2 - 1]).m_parent = b1;
        (paramArrayOfEdgeInfo[b2 - 1]).m_child = b2;
        if (d != node1.getTop()) {
          (paramArrayOfNodeInfo[b2]).m_change = true;
          d = node1.getTop();
        } else {
          (paramArrayOfNodeInfo[b2]).m_change = false;
        } 
        b2++;
      } 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      TreeBuild treeBuild = new TreeBuild();
      Node node = null;
      PlaceNode2 placeNode2 = new PlaceNode2();
      node = treeBuild.create(new FileReader(paramArrayOfString[0]));
      int i = Node.getCount(node, 0);
      TreeVisualizer treeVisualizer = new TreeVisualizer(null, node, placeNode2);
      treeVisualizer.setSize(800, 600);
      JFrame jFrame = new JFrame();
      Container container = jFrame.getContentPane();
      container.add((Component)treeVisualizer);
      jFrame.setSize(800, 600);
      jFrame.setVisible(true);
    } catch (IOException iOException) {}
  }
  
  private class EdgeInfo {
    int m_parent;
    
    int m_child;
    
    int m_side;
    
    int m_tb;
    
    int m_width;
    
    int m_height;
    
    Edge m_edge;
    
    private final TreeVisualizer this$0;
    
    private EdgeInfo(TreeVisualizer this$0) {
      TreeVisualizer.this = TreeVisualizer.this;
    }
  }
  
  private class NodeInfo {
    int m_top;
    
    int m_center;
    
    int m_side;
    
    int m_width;
    
    int m_height;
    
    boolean m_change;
    
    int m_parent;
    
    int m_quad;
    
    Node m_node;
    
    private final TreeVisualizer this$0;
    
    private NodeInfo(TreeVisualizer this$0) {
      TreeVisualizer.this = TreeVisualizer.this;
      this.m_top = 32000;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\treevisualizer\TreeVisualizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */