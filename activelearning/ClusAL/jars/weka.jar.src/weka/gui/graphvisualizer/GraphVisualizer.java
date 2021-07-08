package weka.gui.graphvisualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import weka.core.FastVector;
import weka.gui.ExtensionFileFilter;
import weka.gui.visualize.PrintablePanel;

public class GraphVisualizer extends JPanel implements GraphConstants, LayoutCompleteEventListener {
  protected FastVector m_nodes = new FastVector();
  
  protected FastVector m_edges = new FastVector();
  
  protected LayoutEngine m_le = new HierarchicalBCEngine(this.m_nodes, this.m_edges, this.paddedNodeWidth, this.nodeHeight);
  
  protected GraphPanel m_gp = new GraphPanel(this);
  
  protected String graphID;
  
  protected JButton m_jBtSave;
  
  private final String ICONPATH = "weka/gui/graphvisualizer/icons/";
  
  private FontMetrics fm = getFontMetrics(getFont());
  
  private double scale = 1.0D;
  
  private int nodeHeight = 2 * this.fm.getHeight();
  
  private int nodeWidth = 24;
  
  private int paddedNodeWidth = 32;
  
  private final JTextField jTfNodeWidth = new JTextField(3);
  
  private final JTextField jTfNodeHeight = new JTextField(3);
  
  private final JButton jBtLayout;
  
  private int maxStringWidth = 0;
  
  private int[] zoomPercents = new int[] { 
      10, 25, 50, 75, 100, 125, 150, 175, 200, 225, 
      250, 275, 300, 350, 400, 450, 500, 550, 600, 650, 
      700, 800, 900, 999 };
  
  JScrollPane m_js = new JScrollPane((Component)this.m_gp);
  
  public GraphVisualizer() {
    this.m_le.addLayoutCompleteEventListener(this);
    this.m_jBtSave = new JButton();
    URL uRL = ClassLoader.getSystemResource("weka/gui/graphvisualizer/icons/save.gif");
    if (uRL != null) {
      this.m_jBtSave.setIcon(new ImageIcon(uRL));
    } else {
      System.err.println("weka/gui/graphvisualizer/icons/save.gif not found for weka.gui.graphvisualizer.Graph");
    } 
    this.m_jBtSave.setToolTipText("Save Graph");
    this.m_jBtSave.addActionListener(new ActionListener(this) {
          private final GraphVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            JFileChooser jFileChooser = new JFileChooser(System.getProperty("user.dir"));
            ExtensionFileFilter extensionFileFilter1 = new ExtensionFileFilter(".dot", "DOT files");
            ExtensionFileFilter extensionFileFilter2 = new ExtensionFileFilter(".xml", "XML BIF files");
            jFileChooser.addChoosableFileFilter((FileFilter)extensionFileFilter1);
            jFileChooser.addChoosableFileFilter((FileFilter)extensionFileFilter2);
            jFileChooser.setDialogTitle("Save Graph As");
            int i = jFileChooser.showSaveDialog(this.this$0);
            if (i == 0)
              if (jFileChooser.getFileFilter() == extensionFileFilter2) {
                String str = jFileChooser.getSelectedFile().toString();
                if (!str.endsWith(".xml"))
                  str = str.concat(".xml"); 
                BIFParser.writeXMLBIF03(str, this.this$0.graphID, this.this$0.m_nodes, this.this$0.m_edges);
              } else {
                String str = jFileChooser.getSelectedFile().toString();
                if (!str.endsWith(".dot"))
                  str = str.concat(".dot"); 
                DotParser.writeDOT(str, this.this$0.graphID, this.this$0.m_nodes, this.this$0.m_edges);
              }  
          }
        });
    JButton jButton1 = new JButton();
    uRL = ClassLoader.getSystemResource("weka/gui/graphvisualizer/icons/zoomin.gif");
    if (uRL != null) {
      jButton1.setIcon(new ImageIcon(uRL));
    } else {
      System.err.println("weka/gui/graphvisualizer/icons/zoomin.gif not found for weka.gui.graphvisualizer.Graph");
    } 
    jButton1.setToolTipText("Zoom In");
    JButton jButton2 = new JButton();
    uRL = ClassLoader.getSystemResource("weka/gui/graphvisualizer/icons/zoomout.gif");
    if (uRL != null) {
      jButton2.setIcon(new ImageIcon(uRL));
    } else {
      System.err.println("weka/gui/graphvisualizer/icons/zoomout.gif not found for weka.gui.graphvisualizer.Graph");
    } 
    jButton2.setToolTipText("Zoom Out");
    JTextField jTextField = new JTextField("100%");
    jTextField.setMinimumSize(jTextField.getPreferredSize());
    jTextField.setHorizontalAlignment(0);
    jTextField.setToolTipText("Zoom");
    jTextField.addActionListener(new ActionListener(this, jButton2, jButton1) {
          private final JButton val$jBtZoomOut;
          
          private final JButton val$jBtZoomIn;
          
          private final GraphVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            JTextField jTextField = (JTextField)param1ActionEvent.getSource();
            try {
              int i = -1;
              i = jTextField.getText().indexOf('%');
              if (i == -1) {
                i = Integer.parseInt(jTextField.getText());
              } else {
                i = Integer.parseInt(jTextField.getText().substring(0, i));
              } 
              if (i <= 999)
                this.this$0.scale = i / 100.0D; 
              jTextField.setText((int)(this.this$0.scale * 100.0D) + "%");
              if (this.this$0.scale > 0.1D) {
                if (!this.val$jBtZoomOut.isEnabled())
                  this.val$jBtZoomOut.setEnabled(true); 
              } else {
                this.val$jBtZoomOut.setEnabled(false);
              } 
              if (this.this$0.scale < 9.99D) {
                if (!this.val$jBtZoomIn.isEnabled())
                  this.val$jBtZoomIn.setEnabled(true); 
              } else {
                this.val$jBtZoomIn.setEnabled(false);
              } 
              this.this$0.setAppropriateSize();
              this.this$0.m_gp.repaint();
              this.this$0.m_gp.invalidate();
              this.this$0.m_js.revalidate();
            } catch (NumberFormatException numberFormatException) {
              JOptionPane.showMessageDialog(this.this$0.getParent(), "Invalid integer entered for zoom.", "Error", 0);
              jTextField.setText((this.this$0.scale * 100.0D) + "%");
            } 
          }
        });
    jButton1.addActionListener(new ActionListener(this, jButton2, jTextField) {
          private final JButton val$jBtZoomOut;
          
          private final JTextField val$jTfZoom;
          
          private final GraphVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            int i = 0;
            int j = (int)(this.this$0.scale * 100.0D);
            if (j < 300) {
              i = j / 25;
            } else if (j < 700) {
              i = 6 + j / 50;
            } else {
              i = 13 + j / 100;
            } 
            if (j >= 999) {
              JButton jButton = (JButton)param1ActionEvent.getSource();
              jButton.setEnabled(false);
              return;
            } 
            if (j >= 10) {
              if (i >= 22) {
                JButton jButton = (JButton)param1ActionEvent.getSource();
                jButton.setEnabled(false);
              } 
              if (j == 10 && !this.val$jBtZoomOut.isEnabled())
                this.val$jBtZoomOut.setEnabled(true); 
              this.val$jTfZoom.setText(this.this$0.zoomPercents[i + 1] + "%");
              this.this$0.scale = this.this$0.zoomPercents[i + 1] / 100.0D;
            } else {
              if (!this.val$jBtZoomOut.isEnabled())
                this.val$jBtZoomOut.setEnabled(true); 
              this.val$jTfZoom.setText(this.this$0.zoomPercents[0] + "%");
              this.this$0.scale = this.this$0.zoomPercents[0] / 100.0D;
            } 
            this.this$0.setAppropriateSize();
            this.this$0.m_gp.repaint();
            this.this$0.m_gp.invalidate();
            this.this$0.m_js.revalidate();
          }
        });
    jButton2.addActionListener(new ActionListener(this, jTextField, jButton1) {
          private final JTextField val$jTfZoom;
          
          private final JButton val$jBtZoomIn;
          
          private final GraphVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            int i = 0;
            int j = (int)(this.this$0.scale * 100.0D);
            if (j < 300) {
              i = (int)Math.ceil(j / 25.0D);
            } else if (j < 700) {
              i = 6 + (int)Math.ceil(j / 50.0D);
            } else {
              i = 13 + (int)Math.ceil(j / 100.0D);
            } 
            if (j <= 10) {
              JButton jButton = (JButton)param1ActionEvent.getSource();
              jButton.setEnabled(false);
            } else if (j < 999) {
              if (i <= 1) {
                JButton jButton = (JButton)param1ActionEvent.getSource();
                jButton.setEnabled(false);
              } 
              this.val$jTfZoom.setText(this.this$0.zoomPercents[i - 1] + "%");
              this.this$0.scale = this.this$0.zoomPercents[i - 1] / 100.0D;
            } else {
              if (!this.val$jBtZoomIn.isEnabled())
                this.val$jBtZoomIn.setEnabled(true); 
              this.val$jTfZoom.setText(this.this$0.zoomPercents[22] + "%");
              this.this$0.scale = this.this$0.zoomPercents[22] / 100.0D;
            } 
            this.this$0.setAppropriateSize();
            this.this$0.m_gp.repaint();
            this.this$0.m_gp.invalidate();
            this.this$0.m_js.revalidate();
          }
        });
    JButton jButton3 = new JButton();
    uRL = ClassLoader.getSystemResource("weka/gui/graphvisualizer/icons/extra.gif");
    if (uRL != null) {
      jButton3.setIcon(new ImageIcon(uRL));
    } else {
      System.err.println("weka/gui/graphvisualizer/icons/extra.gif not found for weka.gui.graphvisualizer.Graph");
    } 
    jButton3.setToolTipText("Show/Hide extra controls");
    JCheckBox jCheckBox = new JCheckBox("Custom Node Size");
    JLabel jLabel1 = new JLabel("Width");
    JLabel jLabel2 = new JLabel("Height");
    this.jTfNodeWidth.setHorizontalAlignment(0);
    this.jTfNodeWidth.setText("" + this.nodeWidth);
    this.jTfNodeHeight.setHorizontalAlignment(0);
    this.jTfNodeHeight.setText("" + this.nodeHeight);
    jLabel1.setEnabled(false);
    this.jTfNodeWidth.setEnabled(false);
    jLabel2.setEnabled(false);
    this.jTfNodeHeight.setEnabled(false);
    jCheckBox.addActionListener(new ActionListener(this, jLabel1, jLabel2) {
          private final JLabel val$jLbNodeWidth;
          
          private final JLabel val$jLbNodeHeight;
          
          private final GraphVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (((JCheckBox)param1ActionEvent.getSource()).isSelected()) {
              this.val$jLbNodeWidth.setEnabled(true);
              this.this$0.jTfNodeWidth.setEnabled(true);
              this.val$jLbNodeHeight.setEnabled(true);
              this.this$0.jTfNodeHeight.setEnabled(true);
            } else {
              this.val$jLbNodeWidth.setEnabled(false);
              this.this$0.jTfNodeWidth.setEnabled(false);
              this.val$jLbNodeHeight.setEnabled(false);
              this.this$0.jTfNodeHeight.setEnabled(false);
              this.this$0.setAppropriateNodeSize();
            } 
          }
        });
    this.jBtLayout = new JButton("Layout Graph");
    this.jBtLayout.addActionListener(new ActionListener(this, jCheckBox) {
          private final JCheckBox val$jCbCustomNodeSize;
          
          private final GraphVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.val$jCbCustomNodeSize.isSelected()) {
              int i;
              int j;
              try {
                i = Integer.parseInt(this.this$0.jTfNodeWidth.getText());
              } catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(this.this$0.getParent(), "Invalid integer entered for node width.", "Error", 0);
                i = this.this$0.nodeWidth;
                this.this$0.jTfNodeWidth.setText("" + this.this$0.nodeWidth);
              } 
              try {
                j = Integer.parseInt(this.this$0.jTfNodeHeight.getText());
              } catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(this.this$0.getParent(), "Invalid integer entered for node height.", "Error", 0);
                j = this.this$0.nodeHeight;
                this.this$0.jTfNodeWidth.setText("" + this.this$0.nodeHeight);
              } 
              if (i != this.this$0.nodeWidth || j != this.this$0.nodeHeight) {
                this.this$0.nodeWidth = i;
                this.this$0.paddedNodeWidth = this.this$0.nodeWidth + 8;
                this.this$0.nodeHeight = j;
              } 
            } 
            JButton jButton = (JButton)param1ActionEvent.getSource();
            jButton.setEnabled(false);
            this.this$0.m_le.setNodeSize(this.this$0.paddedNodeWidth, this.this$0.nodeHeight);
            this.this$0.m_le.layoutGraph();
          }
        });
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    JPanel jPanel1 = new JPanel(new GridBagLayout());
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.anchor = 18;
    gridBagConstraints.fill = 0;
    jPanel1.add(this.m_le.getControlPanel(), gridBagConstraints);
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.insets = new Insets(8, 0, 0, 0);
    gridBagConstraints.anchor = 18;
    gridBagConstraints.gridwidth = 0;
    jPanel1.add(jCheckBox, gridBagConstraints);
    gridBagConstraints.insets = new Insets(0, 0, 0, 0);
    gridBagConstraints.gridwidth = 0;
    Container container = new Container();
    container.setLayout(new GridBagLayout());
    gridBagConstraints.gridwidth = -1;
    container.add(jLabel1, gridBagConstraints);
    gridBagConstraints.gridwidth = 0;
    container.add(this.jTfNodeWidth, gridBagConstraints);
    gridBagConstraints.gridwidth = -1;
    container.add(jLabel2, gridBagConstraints);
    gridBagConstraints.gridwidth = 0;
    container.add(this.jTfNodeHeight, gridBagConstraints);
    gridBagConstraints.fill = 2;
    jPanel1.add(container, gridBagConstraints);
    gridBagConstraints.anchor = 18;
    gridBagConstraints.insets = new Insets(8, 0, 0, 0);
    gridBagConstraints.fill = 2;
    jPanel1.add(this.jBtLayout, gridBagConstraints);
    gridBagConstraints.fill = 0;
    jPanel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("ExtraControls"), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
    jPanel1.setPreferredSize(new Dimension(0, 0));
    JToolBar jToolBar = new JToolBar();
    jToolBar.setFloatable(false);
    jToolBar.setLayout(new GridBagLayout());
    gridBagConstraints.anchor = 18;
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.insets = new Insets(0, 0, 0, 0);
    jToolBar.add(jPanel1, gridBagConstraints);
    gridBagConstraints.gridwidth = 1;
    jToolBar.add(this.m_jBtSave, gridBagConstraints);
    jToolBar.addSeparator(new Dimension(2, 2));
    jToolBar.add(jButton1, gridBagConstraints);
    gridBagConstraints.fill = 3;
    gridBagConstraints.weighty = 1.0D;
    JPanel jPanel2 = new JPanel(new BorderLayout());
    jPanel2.setPreferredSize(jTextField.getPreferredSize());
    jPanel2.setMinimumSize(jTextField.getPreferredSize());
    jPanel2.add(jTextField, "Center");
    jToolBar.add(jPanel2, gridBagConstraints);
    gridBagConstraints.weighty = 0.0D;
    gridBagConstraints.fill = 0;
    jToolBar.add(jButton2, gridBagConstraints);
    jToolBar.addSeparator(new Dimension(2, 2));
    jToolBar.add(jButton3, gridBagConstraints);
    jToolBar.addSeparator(new Dimension(4, 2));
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.fill = 1;
    jToolBar.add(this.m_le.getProgressBar(), gridBagConstraints);
    jButton3.addActionListener(new ActionListener(this, jPanel1, jToolBar) {
          private final JPanel val$p;
          
          private final JToolBar val$jTbTools;
          
          private final GraphVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            Dimension dimension = this.val$p.getPreferredSize();
            if (dimension.width == 0 || dimension.height == 0) {
              LayoutManager layoutManager = this.val$p.getLayout();
              Dimension dimension1 = layoutManager.preferredLayoutSize(this.val$p);
              this.val$p.setPreferredSize(dimension1);
              this.val$jTbTools.revalidate();
            } else {
              this.val$p.setPreferredSize(new Dimension(0, 0));
              this.val$jTbTools.revalidate();
            } 
          }
        });
    setLayout(new BorderLayout());
    add(jToolBar, "North");
    add(this.m_js, "Center");
  }
  
  protected void setAppropriateNodeSize() {
    if (this.maxStringWidth == 0)
      for (byte b = 0; b < this.m_nodes.size(); b++) {
        int i = this.fm.stringWidth(((GraphNode)this.m_nodes.elementAt(b)).lbl);
        if (i > this.maxStringWidth)
          this.maxStringWidth = i; 
      }  
    this.nodeWidth = this.maxStringWidth + 4;
    this.paddedNodeWidth = this.nodeWidth + 8;
    this.jTfNodeWidth.setText("" + this.nodeWidth);
    this.nodeHeight = 2 * this.fm.getHeight();
    this.jTfNodeHeight.setText("" + this.nodeHeight);
  }
  
  protected void setAppropriateSize() {
    int i = 0;
    int j = 0;
    this.m_gp.setScale(this.scale, this.scale);
    for (byte b = 0; b < this.m_nodes.size(); b++) {
      GraphNode graphNode = (GraphNode)this.m_nodes.elementAt(b);
      if (i < graphNode.x)
        i = graphNode.x; 
      if (j < graphNode.y)
        j = graphNode.y; 
    } 
    System.out.println("Scale: " + this.scale + " paddedWidth: " + this.paddedNodeWidth + " nodeHeight: " + this.nodeHeight + "\nmaxX: " + i + " maxY: " + j + " final: " + (int)((i + this.paddedNodeWidth + 2) * this.scale) + "," + (int)((j + this.nodeHeight + 2) * this.scale));
    this.m_gp.setPreferredSize(new Dimension((int)((i + this.paddedNodeWidth + 2) * this.scale), (int)((j + this.nodeHeight + 2) * this.scale)));
  }
  
  public void layoutCompleted(LayoutCompleteEvent paramLayoutCompleteEvent) {
    setAppropriateSize();
    this.m_gp.invalidate();
    this.m_js.revalidate();
    this.m_gp.repaint();
    this.jBtLayout.setEnabled(true);
  }
  
  public void layoutGraph() {
    if (this.m_le != null)
      this.m_le.layoutGraph(); 
  }
  
  public void readBIF(String paramString) throws BIFFormatException {
    BIFParser bIFParser = new BIFParser(paramString, this.m_nodes, this.m_edges);
    try {
      this.graphID = bIFParser.parse();
    } catch (BIFFormatException bIFFormatException) {
      System.out.println("BIF format error");
      bIFFormatException.printStackTrace();
    } catch (Exception exception) {
      exception.printStackTrace();
      return;
    } 
    setAppropriateNodeSize();
    if (this.m_le != null)
      this.m_le.setNodeSize(this.paddedNodeWidth, this.nodeHeight); 
  }
  
  public void readBIF(InputStream paramInputStream) throws BIFFormatException {
    BIFParser bIFParser = new BIFParser(paramInputStream, this.m_nodes, this.m_edges);
    try {
      this.graphID = bIFParser.parse();
    } catch (BIFFormatException bIFFormatException) {
      System.out.println("BIF format error");
      bIFFormatException.printStackTrace();
    } catch (Exception exception) {
      exception.printStackTrace();
      return;
    } 
    setAppropriateNodeSize();
    if (this.m_le != null)
      this.m_le.setNodeSize(this.paddedNodeWidth, this.nodeHeight); 
    setAppropriateSize();
  }
  
  public void readDOT(Reader paramReader) {
    DotParser dotParser = new DotParser(paramReader, this.m_nodes, this.m_edges);
    this.graphID = dotParser.parse();
    setAppropriateNodeSize();
    if (this.m_le != null) {
      this.m_le.setNodeSize(this.paddedNodeWidth, this.nodeHeight);
      this.jBtLayout.setEnabled(false);
      layoutGraph();
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    JFrame jFrame = new JFrame("Graph Visualizer");
    GraphVisualizer graphVisualizer = new GraphVisualizer();
    try {
      if (paramArrayOfString[0].endsWith(".xml")) {
        graphVisualizer.readBIF(new FileInputStream(paramArrayOfString[0]));
      } else {
        graphVisualizer.readDOT(new FileReader(paramArrayOfString[0]));
      } 
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } catch (BIFFormatException bIFFormatException) {
      bIFFormatException.printStackTrace();
      System.exit(-1);
    } 
    jFrame.getContentPane().add(graphVisualizer);
    jFrame.setDefaultCloseOperation(3);
    jFrame.setSize(800, 600);
    jFrame.setVisible(true);
  }
  
  private class GraphVisualizerMouseMotionListener extends MouseMotionAdapter {
    int x;
    
    int y;
    
    int nx;
    
    int ny;
    
    Rectangle r;
    
    GraphNode lastNode;
    
    private final GraphVisualizer this$0;
    
    private GraphVisualizerMouseMotionListener(GraphVisualizer this$0) {
      GraphVisualizer.this = GraphVisualizer.this;
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {
      Dimension dimension = GraphVisualizer.this.m_gp.getPreferredSize();
      this.x = this.y = this.nx = this.ny = 0;
      if (dimension.width < GraphVisualizer.this.m_gp.getWidth())
        this.nx = (int)((this.nx + GraphVisualizer.this.m_gp.getWidth() / 2 - dimension.width / 2) / GraphVisualizer.this.scale); 
      if (dimension.height < GraphVisualizer.this.m_gp.getHeight())
        this.ny = (int)((this.ny + GraphVisualizer.this.m_gp.getHeight() / 2 - dimension.height / 2) / GraphVisualizer.this.scale); 
      this.r = new Rectangle(0, 0, (int)(GraphVisualizer.this.paddedNodeWidth * GraphVisualizer.this.scale), (int)(GraphVisualizer.this.nodeHeight * GraphVisualizer.this.scale));
      this.x += param1MouseEvent.getX();
      this.y += param1MouseEvent.getY();
      byte b;
      for (b = 0; b < GraphVisualizer.this.m_nodes.size(); b++) {
        GraphNode graphNode = (GraphNode)GraphVisualizer.this.m_nodes.elementAt(b);
        this.r.x = (int)((this.nx + graphNode.x) * GraphVisualizer.this.scale);
        this.r.y = (int)((this.ny + graphNode.y) * GraphVisualizer.this.scale);
        if (this.r.contains(this.x, this.y)) {
          if (graphNode != this.lastNode) {
            GraphVisualizer.this.m_gp.highLight(graphNode);
            if (this.lastNode != null)
              GraphVisualizer.this.m_gp.highLight(this.lastNode); 
            this.lastNode = graphNode;
          } 
          break;
        } 
      } 
      if (b == GraphVisualizer.this.m_nodes.size() && this.lastNode != null) {
        GraphVisualizer.this.m_gp.repaint();
        this.lastNode = null;
      } 
    }
  }
  
  private class GraphVisualizerMouseListener extends MouseAdapter {
    int x;
    
    int y;
    
    int nx;
    
    int ny;
    
    Rectangle r;
    
    private final GraphVisualizer this$0;
    
    private GraphVisualizerMouseListener(GraphVisualizer this$0) {
      GraphVisualizer.this = GraphVisualizer.this;
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {
      Dimension dimension = GraphVisualizer.this.m_gp.getPreferredSize();
      this.x = this.y = this.nx = this.ny = 0;
      if (dimension.width < GraphVisualizer.this.m_gp.getWidth())
        this.nx = (int)((this.nx + GraphVisualizer.this.m_gp.getWidth() / 2 - dimension.width / 2) / GraphVisualizer.this.scale); 
      if (dimension.height < GraphVisualizer.this.m_gp.getHeight())
        this.ny = (int)((this.ny + GraphVisualizer.this.m_gp.getHeight() / 2 - dimension.height / 2) / GraphVisualizer.this.scale); 
      this.r = new Rectangle(0, 0, (int)(GraphVisualizer.this.paddedNodeWidth * GraphVisualizer.this.scale), (int)(GraphVisualizer.this.nodeHeight * GraphVisualizer.this.scale));
      this.x += param1MouseEvent.getX();
      this.y += param1MouseEvent.getY();
      for (byte b = 0; b < GraphVisualizer.this.m_nodes.size(); b++) {
        GraphNode graphNode = (GraphNode)GraphVisualizer.this.m_nodes.elementAt(b);
        this.r.x = (int)((this.nx + graphNode.x) * GraphVisualizer.this.scale);
        this.r.y = (int)((this.ny + graphNode.y) * GraphVisualizer.this.scale);
        if (this.r.contains(this.x, this.y)) {
          if (graphNode.probs == null)
            return; 
          int i = 1;
          if (graphNode.prnts != null) {
            for (byte b1 = 0; b1 < graphNode.prnts.length; b1++) {
              GraphNode graphNode1 = (GraphNode)GraphVisualizer.this.m_nodes.elementAt(graphNode.prnts[b1]);
              i *= graphNode1.outcomes.length;
            } 
            if (i > 1023) {
              System.err.println("Too many outcomes of parents can't display probabilities");
              return;
            } 
          } 
          GraphVisualizer.GraphVisualizerTableModel graphVisualizerTableModel = new GraphVisualizer.GraphVisualizerTableModel(GraphVisualizer.this, graphNode.probs, graphNode.outcomes);
          JTable jTable = new JTable(graphVisualizerTableModel);
          JScrollPane jScrollPane = new JScrollPane(jTable);
          if (graphNode.prnts != null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            JPanel jPanel = new JPanel(new GridBagLayout());
            int[] arrayOfInt1 = new int[graphNode.prnts.length];
            int[] arrayOfInt2 = new int[graphNode.prnts.length];
            gridBagConstraints.anchor = 18;
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = new Insets(0, 1, 0, 0);
            byte b1 = 0;
            int j = 0;
            boolean bool = false;
            while (true) {
              gridBagConstraints.gridwidth = 1;
              int k;
              for (k = 0; k < graphNode.prnts.length; k++) {
                GraphNode graphNode2 = (GraphNode)GraphVisualizer.this.m_nodes.elementAt(graphNode.prnts[k]);
                JLabel jLabel = new JLabel(graphNode2.outcomes[arrayOfInt1[k]]);
                jLabel.setFont(new Font("Dialog", 0, 12));
                jLabel.setOpaque(true);
                jLabel.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
                jLabel.setHorizontalAlignment(0);
                if (bool) {
                  jLabel.setBackground(jLabel.getBackground().darker());
                  jLabel.setForeground(Color.white);
                } else {
                  jLabel.setForeground(Color.black);
                } 
                j = (jLabel.getPreferredSize()).width;
                jLabel.setPreferredSize(new Dimension(j, jTable.getRowHeight()));
                if (arrayOfInt2[k] < j)
                  arrayOfInt2[k] = j; 
                j = 0;
                if (k == graphNode.prnts.length - 1) {
                  gridBagConstraints.gridwidth = 0;
                  bool = (bool == true) ? false : true;
                } 
                jPanel.add(jLabel, gridBagConstraints);
                b1++;
              } 
              for (k = graphNode.prnts.length - 1; k >= 0; k--) {
                GraphNode graphNode2 = (GraphNode)GraphVisualizer.this.m_nodes.elementAt(graphNode.prnts[k]);
                if (arrayOfInt1[k] == graphNode2.outcomes.length - 1 && k != 0) {
                  arrayOfInt1[k] = 0;
                } else {
                  arrayOfInt1[k] = arrayOfInt1[k] + 1;
                  break;
                } 
              } 
              GraphNode graphNode1 = (GraphNode)GraphVisualizer.this.m_nodes.elementAt(graphNode.prnts[0]);
              if (arrayOfInt1[0] == graphNode1.outcomes.length) {
                JLabel jLabel = (JLabel)jPanel.getComponent(b1 - 1);
                jPanel.remove(b1 - 1);
                jLabel.setPreferredSize(new Dimension((jLabel.getPreferredSize()).width, jTable.getRowHeight()));
                gridBagConstraints.gridwidth = 0;
                gridBagConstraints.weighty = 1.0D;
                jPanel.add(jLabel, gridBagConstraints);
                gridBagConstraints.weighty = 0.0D;
                gridBagConstraints.gridwidth = 1;
                JPanel jPanel1 = new JPanel(new GridBagLayout());
                for (byte b2 = 0; b2 < graphNode.prnts.length; b2++) {
                  JLabel jLabel1 = new JLabel(((GraphNode)GraphVisualizer.this.m_nodes.elementAt(graphNode.prnts[b2])).lbl);
                  jLabel1.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
                  Dimension dimension1 = jLabel1.getPreferredSize();
                  if (dimension1.width < arrayOfInt2[b2]) {
                    jLabel1.setPreferredSize(new Dimension(arrayOfInt2[b2], dimension1.height));
                    jLabel1.setHorizontalAlignment(0);
                    jLabel1.setMinimumSize(new Dimension(arrayOfInt2[b2], dimension1.height));
                  } else if (dimension1.width > arrayOfInt2[b2]) {
                    JLabel jLabel2 = (JLabel)jPanel.getComponent(b2);
                    jLabel2.setPreferredSize(new Dimension(dimension1.width, (jLabel2.getPreferredSize()).height));
                  } 
                  jPanel1.add(jLabel1, gridBagConstraints);
                } 
                jScrollPane.setRowHeaderView(jPanel);
                jScrollPane.setCorner("UPPER_LEFT_CORNER", jPanel1);
                break;
              } 
            } 
          } 
          JDialog jDialog = new JDialog((Frame)GraphVisualizer.this.getTopLevelAncestor(), "Probability Distribution Table For " + graphNode.lbl, true);
          jDialog.setSize(500, 400);
          jDialog.setLocation((GraphVisualizer.this.getLocation()).x + GraphVisualizer.this.getWidth() / 2 - 250, (GraphVisualizer.this.getLocation()).y + GraphVisualizer.this.getHeight() / 2 - 200);
          jDialog.getContentPane().setLayout(new BorderLayout());
          jDialog.getContentPane().add(jScrollPane, "Center");
          jDialog.setVisible(true);
          return;
        } 
      } 
    }
  }
  
  private class GraphVisualizerTableModel extends AbstractTableModel {
    final String[] columnNames;
    
    final double[][] data;
    
    private final GraphVisualizer this$0;
    
    public GraphVisualizerTableModel(GraphVisualizer this$0, double[][] param1ArrayOfdouble, String[] param1ArrayOfString) {
      this.this$0 = this$0;
      this.data = param1ArrayOfdouble;
      this.columnNames = param1ArrayOfString;
    }
    
    public int getColumnCount() {
      return this.columnNames.length;
    }
    
    public int getRowCount() {
      return this.data.length;
    }
    
    public String getColumnName(int param1Int) {
      return this.columnNames[param1Int];
    }
    
    public Object getValueAt(int param1Int1, int param1Int2) {
      return new Double(this.data[param1Int1][param1Int2]);
    }
    
    public Class getColumnClass(int param1Int) {
      return getValueAt(0, param1Int).getClass();
    }
    
    public boolean isCellEditable(int param1Int1, int param1Int2) {
      return false;
    }
  }
  
  private class GraphPanel extends PrintablePanel {
    private final GraphVisualizer this$0;
    
    public GraphPanel(GraphVisualizer this$0) {
      this.this$0 = this$0;
      addMouseListener(new GraphVisualizer.GraphVisualizerMouseListener());
      addMouseMotionListener(new GraphVisualizer.GraphVisualizerMouseMotionListener());
      setToolTipText("");
    }
    
    public String getToolTipText(MouseEvent param1MouseEvent) {
      Dimension dimension = this.this$0.m_gp.getPreferredSize();
      int m = 0;
      int k = m;
      int j = k;
      int i = j;
      if (dimension.width < this.this$0.m_gp.getWidth())
        k = (int)((k + this.this$0.m_gp.getWidth() / 2 - dimension.width / 2) / this.this$0.scale); 
      if (dimension.height < this.this$0.m_gp.getHeight())
        m = (int)((m + this.this$0.m_gp.getHeight() / 2 - dimension.height / 2) / this.this$0.scale); 
      Rectangle rectangle = new Rectangle(0, 0, (int)(this.this$0.paddedNodeWidth * this.this$0.scale), (int)(this.this$0.nodeHeight * this.this$0.scale));
      i += param1MouseEvent.getX();
      j += param1MouseEvent.getY();
      for (byte b = 0; b < this.this$0.m_nodes.size(); b++) {
        GraphNode graphNode = (GraphNode)this.this$0.m_nodes.elementAt(b);
        if (graphNode.nodeType != 3)
          return null; 
        rectangle.x = (int)((k + graphNode.x) * this.this$0.scale);
        rectangle.y = (int)((m + graphNode.y) * this.this$0.scale);
        if (rectangle.contains(i, j))
          return (graphNode.probs == null) ? graphNode.lbl : (graphNode.lbl + " (click to view the probability dist. table)"); 
      } 
      return null;
    }
    
    public void paintComponent(Graphics param1Graphics) {
      Graphics2D graphics2D = (Graphics2D)param1Graphics;
      RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
      graphics2D.setRenderingHints(renderingHints);
      graphics2D.scale(this.this$0.scale, this.this$0.scale);
      Rectangle rectangle = graphics2D.getClipBounds();
      graphics2D.clearRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      int i = 0;
      int j = 0;
      Dimension dimension = getPreferredSize();
      if (dimension.width < getWidth())
        i = (int)((i + getWidth() / 2 - dimension.width / 2) / this.this$0.scale); 
      if (dimension.height < getHeight())
        j = (int)((j + getHeight() / 2 - dimension.height / 2) / this.this$0.scale); 
      for (byte b = 0; b < this.this$0.m_nodes.size(); b++) {
        GraphNode graphNode = (GraphNode)this.this$0.m_nodes.elementAt(b);
        if (graphNode.nodeType == 3) {
          graphics2D.setColor(getBackground().darker().darker());
          graphics2D.fillOval(i + graphNode.x + this.this$0.paddedNodeWidth - this.this$0.nodeWidth - (this.this$0.paddedNodeWidth - this.this$0.nodeWidth) / 2, j + graphNode.y, this.this$0.nodeWidth, this.this$0.nodeHeight);
          graphics2D.setColor(Color.white);
          if (this.this$0.fm.stringWidth(graphNode.lbl) <= this.this$0.nodeWidth) {
            graphics2D.drawString(graphNode.lbl, i + graphNode.x + this.this$0.paddedNodeWidth / 2 - this.this$0.fm.stringWidth(graphNode.lbl) / 2, j + graphNode.y + this.this$0.nodeHeight / 2 + this.this$0.fm.getHeight() / 2 - 2);
          } else if (this.this$0.fm.stringWidth(graphNode.ID) <= this.this$0.nodeWidth) {
            graphics2D.drawString(graphNode.ID, i + graphNode.x + this.this$0.paddedNodeWidth / 2 - this.this$0.fm.stringWidth(graphNode.ID) / 2, j + graphNode.y + this.this$0.nodeHeight / 2 + this.this$0.fm.getHeight() / 2 - 2);
          } else if (this.this$0.fm.stringWidth(Integer.toString(b)) <= this.this$0.nodeWidth) {
            graphics2D.drawString(Integer.toString(b), i + graphNode.x + this.this$0.paddedNodeWidth / 2 - this.this$0.fm.stringWidth(Integer.toString(b)) / 2, j + graphNode.y + this.this$0.nodeHeight / 2 + this.this$0.fm.getHeight() / 2 - 2);
          } 
          graphics2D.setColor(Color.black);
        } else {
          graphics2D.drawLine(i + graphNode.x + this.this$0.paddedNodeWidth / 2, j + graphNode.y, i + graphNode.x + this.this$0.paddedNodeWidth / 2, j + graphNode.y + this.this$0.nodeHeight);
        } 
        if (graphNode.edges != null)
          for (byte b1 = 0; b1 < graphNode.edges.length; b1++) {
            if (graphNode.edges[b1][1] > 0) {
              GraphNode graphNode1 = (GraphNode)this.this$0.m_nodes.elementAt(graphNode.edges[b1][0]);
              int k = graphNode.x + this.this$0.paddedNodeWidth / 2;
              int m = graphNode.y + this.this$0.nodeHeight;
              int n = graphNode1.x + this.this$0.paddedNodeWidth / 2;
              int i1 = graphNode1.y;
              graphics2D.drawLine(i + k, j + m, i + n, j + i1);
              if (graphNode.edges[b1][1] == 1) {
                if (graphNode1.nodeType == 3)
                  drawArrow(graphics2D, i + k, j + m, i + n, j + i1); 
              } else if (graphNode.edges[b1][1] == 2) {
                if (graphNode.nodeType == 3)
                  drawArrow(graphics2D, i + n, j + i1, i + k, j + m); 
              } else if (graphNode.edges[b1][1] == 3) {
                if (graphNode.nodeType == 3)
                  drawArrow(graphics2D, i + n, j + i1, i + k, j + m); 
                if (graphNode1.nodeType == 3)
                  drawArrow(graphics2D, i + k, j + m, i + n, j + i1); 
              } 
            } 
          }  
      } 
    }
    
    protected void drawArrow(Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (param1Int1 == param1Int3) {
        if (param1Int2 < param1Int4) {
          param1Graphics.drawLine(param1Int3, param1Int4, param1Int3 + 4, param1Int4 - 8);
          param1Graphics.drawLine(param1Int3, param1Int4, param1Int3 - 4, param1Int4 - 8);
        } else {
          param1Graphics.drawLine(param1Int3, param1Int4, param1Int3 + 4, param1Int4 + 8);
          param1Graphics.drawLine(param1Int3, param1Int4, param1Int3 - 4, param1Int4 + 8);
        } 
      } else {
        double d4;
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        int i = 0;
        int j = 0;
        if (param1Int3 < param1Int1) {
          d2 = (param1Int1 - param1Int3);
          d1 = Math.sqrt(((param1Int3 - param1Int1) * (param1Int3 - param1Int1) + (param1Int4 - param1Int2) * (param1Int4 - param1Int2)));
          d4 = Math.acos(d2 / d1);
        } else {
          d2 = (param1Int1 - param1Int3);
          d1 = Math.sqrt(((param1Int3 - param1Int1) * (param1Int3 - param1Int1) + (param1Int4 - param1Int2) * (param1Int4 - param1Int2)));
          d4 = Math.acos(d2 / d1);
        } 
        double d5 = 0.5235987755982988D;
        d1 = 8.0D;
        d2 = Math.cos(d4 - d5) * d1;
        d3 = Math.sin(d4 - d5) * d1;
        i = (int)(param1Int3 + d2);
        if (param1Int2 < param1Int4) {
          j = (int)(param1Int4 - d3);
        } else {
          j = (int)(param1Int4 + d3);
        } 
        param1Graphics.drawLine(param1Int3, param1Int4, i, j);
        d2 = Math.cos(d4 + d5) * d1;
        d3 = Math.sin(d4 + d5) * d1;
        i = (int)(param1Int3 + d2);
        if (param1Int2 < param1Int4) {
          j = (int)(param1Int4 - d3);
        } else {
          j = (int)(param1Int4 + d3);
        } 
        param1Graphics.drawLine(param1Int3, param1Int4, i, j);
      } 
    }
    
    public void highLight(GraphNode param1GraphNode) {
      Graphics2D graphics2D = (Graphics2D)getGraphics();
      RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
      graphics2D.setRenderingHints(renderingHints);
      graphics2D.setPaintMode();
      graphics2D.scale(this.this$0.scale, this.this$0.scale);
      int i = 0;
      int j = 0;
      Dimension dimension = getPreferredSize();
      if (dimension.width < getWidth())
        i = (int)((i + getWidth() / 2 - dimension.width / 2) / this.this$0.scale); 
      if (dimension.height < getHeight())
        j = (int)((j + getHeight() / 2 - dimension.height / 2) / this.this$0.scale); 
      if (param1GraphNode.nodeType == 3) {
        graphics2D.setXORMode(Color.green);
        graphics2D.fillOval(i + param1GraphNode.x + this.this$0.paddedNodeWidth - this.this$0.nodeWidth - (this.this$0.paddedNodeWidth - this.this$0.nodeWidth) / 2, j + param1GraphNode.y, this.this$0.nodeWidth, this.this$0.nodeHeight);
        graphics2D.setXORMode(Color.red);
        if (this.this$0.fm.stringWidth(param1GraphNode.lbl) <= this.this$0.nodeWidth) {
          graphics2D.drawString(param1GraphNode.lbl, i + param1GraphNode.x + this.this$0.paddedNodeWidth / 2 - this.this$0.fm.stringWidth(param1GraphNode.lbl) / 2, j + param1GraphNode.y + this.this$0.nodeHeight / 2 + this.this$0.fm.getHeight() / 2 - 2);
        } else if (this.this$0.fm.stringWidth(param1GraphNode.ID) <= this.this$0.nodeWidth) {
          graphics2D.drawString(param1GraphNode.ID, i + param1GraphNode.x + this.this$0.paddedNodeWidth / 2 - this.this$0.fm.stringWidth(param1GraphNode.ID) / 2, j + param1GraphNode.y + this.this$0.nodeHeight / 2 + this.this$0.fm.getHeight() / 2 - 2);
        } else if (this.this$0.fm.stringWidth(Integer.toString(this.this$0.m_nodes.indexOf(param1GraphNode))) <= this.this$0.nodeWidth) {
          graphics2D.drawString(Integer.toString(this.this$0.m_nodes.indexOf(param1GraphNode)), i + param1GraphNode.x + this.this$0.paddedNodeWidth / 2 - this.this$0.fm.stringWidth(Integer.toString(this.this$0.m_nodes.indexOf(param1GraphNode))) / 2, j + param1GraphNode.y + this.this$0.nodeHeight / 2 + this.this$0.fm.getHeight() / 2 - 2);
        } 
        graphics2D.setXORMode(Color.green);
        if (param1GraphNode.edges != null)
          for (byte b = 0; b < param1GraphNode.edges.length; b++) {
            if (param1GraphNode.edges[b][1] == 1 || param1GraphNode.edges[b][1] == 3) {
              GraphNode graphNode = (GraphNode)this.this$0.m_nodes.elementAt(param1GraphNode.edges[b][0]);
              int k = param1GraphNode.x + this.this$0.paddedNodeWidth / 2;
              int m = param1GraphNode.y + this.this$0.nodeHeight;
              int n = graphNode.x + this.this$0.paddedNodeWidth / 2;
              int i1 = graphNode.y;
              graphics2D.drawLine(i + k, j + m, i + n, j + i1);
              if (param1GraphNode.edges[b][1] == 1) {
                if (graphNode.nodeType == 3)
                  drawArrow(graphics2D, i + k, j + m, i + n, j + i1); 
              } else if (param1GraphNode.edges[b][1] == 3) {
                if (param1GraphNode.nodeType == 3)
                  drawArrow(graphics2D, i + n, j + i1, i + k, j + m); 
                if (graphNode.nodeType == 3)
                  drawArrow(graphics2D, i + k, j + m, i + n, j + i1); 
              } 
              if (graphNode.nodeType == 3)
                graphics2D.fillOval(i + graphNode.x + this.this$0.paddedNodeWidth - this.this$0.nodeWidth - (this.this$0.paddedNodeWidth - this.this$0.nodeWidth) / 2, j + graphNode.y, this.this$0.nodeWidth, this.this$0.nodeHeight); 
              Vector vector = new Vector();
              while (graphNode.nodeType != 3 || vector.size() > 0) {
                if (vector.size() > 0) {
                  graphNode = vector.elementAt(0);
                  vector.removeElementAt(0);
                } 
                if (graphNode.nodeType != 3) {
                  graphics2D.drawLine(i + graphNode.x + this.this$0.paddedNodeWidth / 2, j + graphNode.y, i + graphNode.x + this.this$0.paddedNodeWidth / 2, j + graphNode.y + this.this$0.nodeHeight);
                  k = graphNode.x + this.this$0.paddedNodeWidth / 2;
                  m = graphNode.y + this.this$0.nodeHeight;
                  for (byte b1 = 0; b1 < graphNode.edges.length; b1++) {
                    if (graphNode.edges[b1][1] > 0) {
                      GraphNode graphNode1 = (GraphNode)this.this$0.m_nodes.elementAt(graphNode.edges[b1][0]);
                      graphics2D.drawLine(i + k, j + m, i + graphNode1.x + this.this$0.paddedNodeWidth / 2, j + graphNode1.y);
                      if (graphNode1.nodeType == 3) {
                        graphics2D.fillOval(i + graphNode1.x + this.this$0.paddedNodeWidth - this.this$0.nodeWidth - (this.this$0.paddedNodeWidth - this.this$0.nodeWidth) / 2, j + graphNode1.y, this.this$0.nodeWidth, this.this$0.nodeHeight);
                        drawArrow(graphics2D, i + k, j + m, i + graphNode1.x + this.this$0.paddedNodeWidth / 2, j + graphNode1.y);
                      } 
                      vector.addElement(graphNode1);
                    } 
                  } 
                } 
              } 
            } else if (param1GraphNode.edges[b][1] == -2 || param1GraphNode.edges[b][1] == -3) {
              GraphNode graphNode = (GraphNode)this.this$0.m_nodes.elementAt(param1GraphNode.edges[b][0]);
              int k = param1GraphNode.x + this.this$0.paddedNodeWidth / 2;
              int m = param1GraphNode.y;
              int n = graphNode.x + this.this$0.paddedNodeWidth / 2;
              int i1 = graphNode.y + this.this$0.nodeHeight;
              graphics2D.drawLine(i + k, j + m, i + n, j + i1);
              if (param1GraphNode.edges[b][1] == -3) {
                drawArrow(graphics2D, i + n, j + i1, i + k, j + m);
                if (graphNode.nodeType != 1)
                  drawArrow(graphics2D, i + k, j + m, i + n, j + i1); 
              } 
              byte b1 = b;
              while (graphNode.nodeType != 3) {
                graphics2D.drawLine(i + graphNode.x + this.this$0.paddedNodeWidth / 2, j + graphNode.y + this.this$0.nodeHeight, i + graphNode.x + this.this$0.paddedNodeWidth / 2, j + graphNode.y);
                k = graphNode.x + this.this$0.paddedNodeWidth / 2;
                m = graphNode.y;
                for (byte b2 = 0; b2 < graphNode.edges.length; b2++) {
                  if (graphNode.edges[b2][1] < 0) {
                    graphNode = (GraphNode)this.this$0.m_nodes.elementAt(graphNode.edges[b2][0]);
                    graphics2D.drawLine(i + k, j + m, i + graphNode.x + this.this$0.paddedNodeWidth / 2, j + graphNode.y + this.this$0.nodeHeight);
                    b1 = b2;
                    if (graphNode.nodeType != 1)
                      drawArrow(graphics2D, i + k, j + m, i + graphNode.x + this.this$0.paddedNodeWidth / 2, j + graphNode.y + this.this$0.nodeHeight); 
                    break;
                  } 
                } 
              } 
            } 
          }  
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\graphvisualizer\GraphVisualizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */