package weka.gui.visualize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.gui.ExtensionFileFilter;
import weka.gui.Logger;

public class VisualizePanel extends PrintablePanel {
  protected Color[] m_DefaultColors = new Color[] { Color.blue, Color.red, Color.green, Color.cyan, Color.pink, new Color(255, 0, 255), Color.orange, new Color(255, 0, 0), new Color(0, 255, 0), Color.white };
  
  protected JComboBox m_XCombo = new JComboBox();
  
  protected JComboBox m_YCombo = new JComboBox();
  
  protected JComboBox m_ColourCombo = new JComboBox();
  
  protected JComboBox m_ShapeCombo = new JComboBox();
  
  protected JButton m_submit = new JButton("Submit");
  
  protected JButton m_cancel = new JButton("Clear");
  
  protected JButton m_saveBut = new JButton("Save");
  
  private Dimension COMBO_SIZE = new Dimension(250, (this.m_saveBut.getPreferredSize()).height);
  
  protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  protected FileFilter m_ArffFilter = (FileFilter)new ExtensionFileFilter(Instances.FILE_EXTENSION, "Arff data files");
  
  protected JLabel m_JitterLab = new JLabel("Jitter", 4);
  
  protected JSlider m_Jitter = new JSlider(0, 50, 0);
  
  protected PlotPanel m_plot = new PlotPanel(this);
  
  protected AttributePanel m_attrib = new AttributePanel();
  
  protected LegendPanel m_legendPanel = new LegendPanel();
  
  protected JPanel m_plotSurround = new JPanel();
  
  protected JPanel m_classSurround = new JPanel();
  
  protected ActionListener listener = null;
  
  protected VisualizePanelListener m_splitListener = null;
  
  protected String m_plotName = "";
  
  protected ClassPanel m_classPanel = new ClassPanel();
  
  protected FastVector m_colorList;
  
  protected String m_preferredXDimension = null;
  
  protected String m_preferredYDimension = null;
  
  protected String m_preferredColourDimension = null;
  
  protected boolean m_showAttBars = true;
  
  protected Logger m_Log;
  
  public void setLog(Logger paramLogger) {
    this.m_Log = paramLogger;
  }
  
  public VisualizePanel(VisualizePanelListener paramVisualizePanelListener) {
    this();
    this.m_splitListener = paramVisualizePanelListener;
  }
  
  private void setProperties(String paramString) {
    if (VisualizeUtils.VISUALIZE_PROPERTIES != null) {
      String str = getClass().getName();
      if (paramString == null) {
        String str1 = str + ".displayAttributeBars";
        String str2 = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(str1);
        if (str2 == null) {
          this.m_showAttBars = true;
        } else if (str2.compareTo("true") == 0 || str2.compareTo("on") == 0) {
          this.m_showAttBars = true;
        } else {
          this.m_showAttBars = false;
        } 
      } else {
        String str1 = str + "." + paramString + ".XDimension";
        String str2 = str + "." + paramString + ".YDimension";
        String str3 = str + "." + paramString + ".ColourDimension";
        this.m_preferredXDimension = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(str1);
        this.m_preferredYDimension = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(str2);
        this.m_preferredColourDimension = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(str3);
      } 
    } 
  }
  
  public VisualizePanel() {
    setProperties((String)null);
    this.m_FileChooser.setFileFilter(this.m_ArffFilter);
    this.m_FileChooser.setFileSelectionMode(0);
    this.m_XCombo.setToolTipText("Select the attribute for the x axis");
    this.m_YCombo.setToolTipText("Select the attribute for the y axis");
    this.m_ColourCombo.setToolTipText("Select the attribute to colour on");
    this.m_ShapeCombo.setToolTipText("Select the shape to use for data selection");
    this.m_XCombo.setPreferredSize(this.COMBO_SIZE);
    this.m_YCombo.setPreferredSize(this.COMBO_SIZE);
    this.m_ColourCombo.setPreferredSize(this.COMBO_SIZE);
    this.m_ShapeCombo.setPreferredSize(this.COMBO_SIZE);
    this.m_XCombo.setMaximumSize(this.COMBO_SIZE);
    this.m_YCombo.setMaximumSize(this.COMBO_SIZE);
    this.m_ColourCombo.setMaximumSize(this.COMBO_SIZE);
    this.m_ShapeCombo.setMaximumSize(this.COMBO_SIZE);
    this.m_XCombo.setMinimumSize(this.COMBO_SIZE);
    this.m_YCombo.setMinimumSize(this.COMBO_SIZE);
    this.m_ColourCombo.setMinimumSize(this.COMBO_SIZE);
    this.m_ShapeCombo.setMinimumSize(this.COMBO_SIZE);
    this.m_XCombo.setEnabled(false);
    this.m_YCombo.setEnabled(false);
    this.m_ColourCombo.setEnabled(false);
    this.m_ShapeCombo.setEnabled(false);
    this.m_classPanel.addRepaintNotify(this);
    this.m_legendPanel.addRepaintNotify(this);
    this.m_colorList = new FastVector(10);
    for (int i = this.m_colorList.size(); i < 10; i++) {
      Color color = this.m_DefaultColors[i % 10];
      int j = i / 10;
      j *= 2;
      for (byte b = 0; b < j; b++)
        color = color.darker(); 
      this.m_colorList.addElement(color);
    } 
    this.m_plot.setColours(this.m_colorList);
    this.m_classPanel.setColours(this.m_colorList);
    this.m_attrib.setColours(this.m_colorList);
    this.m_attrib.addAttributePanelListener(new AttributePanelListener(this) {
          private final VisualizePanel this$0;
          
          public void attributeSelectionChange(AttributePanelEvent param1AttributePanelEvent) {
            if (param1AttributePanelEvent.m_xChange) {
              this.this$0.m_XCombo.setSelectedIndex(param1AttributePanelEvent.m_indexVal);
            } else {
              this.this$0.m_YCombo.setSelectedIndex(param1AttributePanelEvent.m_indexVal);
            } 
          }
        });
    this.m_XCombo.addActionListener(new ActionListener(this) {
          private final VisualizePanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            int i = this.this$0.m_XCombo.getSelectedIndex();
            if (i < 0)
              i = 0; 
            this.this$0.m_plot.setXindex(i);
            if (this.this$0.listener != null)
              this.this$0.listener.actionPerformed(param1ActionEvent); 
          }
        });
    this.m_YCombo.addActionListener(new ActionListener(this) {
          private final VisualizePanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            int i = this.this$0.m_YCombo.getSelectedIndex();
            if (i < 0)
              i = 0; 
            this.this$0.m_plot.setYindex(i);
            if (this.this$0.listener != null)
              this.this$0.listener.actionPerformed(param1ActionEvent); 
          }
        });
    this.m_ColourCombo.addActionListener(new ActionListener(this) {
          private final VisualizePanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            int i = this.this$0.m_ColourCombo.getSelectedIndex();
            if (i < 0)
              i = 0; 
            this.this$0.m_plot.setCindex(i);
            if (this.this$0.listener != null)
              this.this$0.listener.actionPerformed(param1ActionEvent); 
          }
        });
    this.m_ShapeCombo.addActionListener(new ActionListener(this) {
          private final VisualizePanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            int i = this.this$0.m_ShapeCombo.getSelectedIndex();
            if (i < 0)
              i = 0; 
            this.this$0.m_plot.setSindex(i);
            if (this.this$0.listener != null)
              this.this$0.listener.actionPerformed(param1ActionEvent); 
          }
        });
    this.m_Jitter.addChangeListener(new ChangeListener(this) {
          private final VisualizePanel this$0;
          
          public void stateChanged(ChangeEvent param1ChangeEvent) {
            this.this$0.m_plot.setJitter(this.this$0.m_Jitter.getValue());
          }
        });
    this.m_saveBut.setEnabled(false);
    this.m_saveBut.setToolTipText("Save the visible instances to a file");
    this.m_saveBut.addActionListener(new ActionListener(this) {
          private final VisualizePanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.saveVisibleInstances();
          }
        });
    JPanel jPanel1 = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    this.m_XCombo.setLightWeightPopupEnabled(false);
    this.m_YCombo.setLightWeightPopupEnabled(false);
    this.m_ColourCombo.setLightWeightPopupEnabled(false);
    this.m_ShapeCombo.setLightWeightPopupEnabled(false);
    jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    jPanel1.setLayout(gridBagLayout1);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.insets = new Insets(0, 2, 0, 2);
    jPanel1.add(this.m_XCombo, gridBagConstraints);
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.gridheight = 1;
    jPanel1.add(this.m_YCombo, gridBagConstraints);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.gridheight = 1;
    jPanel1.add(this.m_ColourCombo, gridBagConstraints);
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.gridheight = 1;
    jPanel1.add(this.m_ShapeCombo, gridBagConstraints);
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new GridLayout(1, 3));
    jPanel2.add(this.m_submit);
    jPanel2.add(this.m_cancel);
    jPanel2.add(this.m_saveBut);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.gridheight = 1;
    jPanel1.add(jPanel2, gridBagConstraints);
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.insets = new Insets(10, 0, 0, 5);
    jPanel1.add(this.m_JitterLab, gridBagConstraints);
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.insets = new Insets(10, 0, 0, 0);
    jPanel1.add(this.m_Jitter, gridBagConstraints);
    this.m_classSurround = new JPanel();
    this.m_classSurround.setBorder(BorderFactory.createTitledBorder("Class colour"));
    this.m_classSurround.setLayout(new BorderLayout());
    this.m_classPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
    this.m_classSurround.add(this.m_classPanel, "Center");
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    this.m_plotSurround.setBorder(BorderFactory.createTitledBorder("Plot"));
    this.m_plotSurround.setLayout(gridBagLayout2);
    gridBagConstraints.fill = 1;
    gridBagConstraints.insets = new Insets(0, 0, 0, 10);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 3.0D;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weighty = 5.0D;
    this.m_plotSurround.add(this.m_plot, gridBagConstraints);
    if (this.m_showAttBars) {
      gridBagConstraints.insets = new Insets(0, 0, 0, 0);
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.gridwidth = 1;
      gridBagConstraints.gridheight = 1;
      gridBagConstraints.weighty = 5.0D;
      this.m_plotSurround.add(this.m_attrib, gridBagConstraints);
    } 
    setLayout(new BorderLayout());
    add(jPanel1, "North");
    add(this.m_plotSurround, "Center");
    add(this.m_classSurround, "South");
    String[] arrayOfString = new String[4];
    arrayOfString[0] = "Select Instance";
    arrayOfString[1] = "Rectangle";
    arrayOfString[2] = "Polygon";
    arrayOfString[3] = "Polyline";
    this.m_ShapeCombo.setModel(new DefaultComboBoxModel(arrayOfString));
    this.m_ShapeCombo.setEnabled(true);
  }
  
  private void saveVisibleInstances() {
    FastVector fastVector = this.m_plot.m_plot2D.getPlots();
    if (fastVector != null) {
      PlotData2D plotData2D = (PlotData2D)fastVector.elementAt(0);
      Instances instances = new Instances(plotData2D.getPlotInstances());
      int i;
      for (i = 1; i < fastVector.size(); i++) {
        PlotData2D plotData2D1 = (PlotData2D)fastVector.elementAt(i);
        Instances instances1 = plotData2D1.getPlotInstances();
        for (byte b = 0; b < instances1.numInstances(); b++)
          instances.add(instances1.instance(b)); 
      } 
      try {
        i = this.m_FileChooser.showSaveDialog(this);
        if (i == 0) {
          File file1 = this.m_FileChooser.getSelectedFile();
          if (!file1.getName().toLowerCase().endsWith(Instances.FILE_EXTENSION))
            file1 = new File(file1.getParent(), file1.getName() + Instances.FILE_EXTENSION); 
          File file2 = file1;
          BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file2));
          bufferedWriter.write(instances.toString());
          bufferedWriter.close();
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } 
  }
  
  public void setColourIndex(int paramInt) {
    if (paramInt >= 0) {
      this.m_ColourCombo.setSelectedIndex(paramInt);
    } else {
      this.m_ColourCombo.setSelectedIndex(0);
    } 
    this.m_ColourCombo.setEnabled(false);
  }
  
  public void setXIndex(int paramInt) throws Exception {
    if (paramInt >= 0 && paramInt < this.m_XCombo.getItemCount()) {
      this.m_XCombo.setSelectedIndex(paramInt);
    } else {
      throw new Exception("x index is out of range!");
    } 
  }
  
  public int getXIndex() {
    return this.m_XCombo.getSelectedIndex();
  }
  
  public void setYIndex(int paramInt) throws Exception {
    if (paramInt >= 0 && paramInt < this.m_YCombo.getItemCount()) {
      this.m_YCombo.setSelectedIndex(paramInt);
    } else {
      throw new Exception("y index is out of range!");
    } 
  }
  
  public int getYIndex() {
    return this.m_YCombo.getSelectedIndex();
  }
  
  public int getCIndex() {
    return this.m_ColourCombo.getSelectedIndex();
  }
  
  public int getSIndex() {
    return this.m_ShapeCombo.getSelectedIndex();
  }
  
  public void setSIndex(int paramInt) throws Exception {
    if (paramInt >= 0 && paramInt < this.m_ShapeCombo.getItemCount()) {
      this.m_ShapeCombo.setSelectedIndex(paramInt);
    } else {
      throw new Exception("s index is out of range!");
    } 
  }
  
  public void addActionListener(ActionListener paramActionListener) {
    this.listener = paramActionListener;
  }
  
  public void setName(String paramString) {
    this.m_plotName = paramString;
  }
  
  public String getName() {
    return this.m_plotName;
  }
  
  public Instances getInstances() {
    return this.m_plot.m_plotInstances;
  }
  
  protected void newColorAttribute(int paramInt, Instances paramInstances) {
    if (paramInstances.attribute(paramInt).isNominal()) {
      for (int i = this.m_colorList.size(); i < paramInstances.attribute(paramInt).numValues(); i++) {
        Color color = this.m_DefaultColors[i % 10];
        int j = i / 10;
        j *= 2;
        for (byte b = 0; b < j; b++)
          color = color.brighter(); 
        this.m_colorList.addElement(color);
      } 
      this.m_plot.setColours(this.m_colorList);
      this.m_attrib.setColours(this.m_colorList);
      this.m_classPanel.setColours(this.m_colorList);
    } 
  }
  
  public void setShapes(FastVector paramFastVector) {
    this.m_plot.setShapes(paramFastVector);
  }
  
  public void setInstances(Instances paramInstances) {
    if (paramInstances.numAttributes() > 0 && paramInstances.numInstances() > 0)
      newColorAttribute(paramInstances.numAttributes() - 1, paramInstances); 
    PlotData2D plotData2D = new PlotData2D(paramInstances);
    plotData2D.setPlotName(paramInstances.relationName());
    try {
      setMasterPlot(plotData2D);
    } catch (Exception exception) {
      System.err.println(exception);
      exception.printStackTrace();
    } 
  }
  
  public void setUpComboBoxes(Instances paramInstances) {
    setProperties(paramInstances.relationName());
    byte b1 = -1;
    byte b2 = -1;
    if (paramInstances.numAttributes() > 1)
      b2 = 1; 
    byte b3 = -1;
    String[] arrayOfString1 = new String[paramInstances.numAttributes()];
    String[] arrayOfString2 = new String[paramInstances.numAttributes()];
    String[] arrayOfString3 = new String[paramInstances.numAttributes()];
    String[] arrayOfString4 = new String[4];
    for (byte b = 0; b < arrayOfString1.length; b++) {
      String str = "";
      switch (paramInstances.attribute(b).type()) {
        case 1:
          str = " (Nom)";
          break;
        case 0:
          str = " (Num)";
          break;
        case 2:
          str = " (Str)";
          break;
        default:
          str = " (???)";
          break;
      } 
      arrayOfString1[b] = "X: " + paramInstances.attribute(b).name() + str;
      arrayOfString2[b] = "Y: " + paramInstances.attribute(b).name() + str;
      arrayOfString3[b] = "Colour: " + paramInstances.attribute(b).name() + str;
      if (this.m_preferredXDimension != null && this.m_preferredXDimension.compareTo(paramInstances.attribute(b).name()) == 0)
        b1 = b; 
      if (this.m_preferredYDimension != null && this.m_preferredYDimension.compareTo(paramInstances.attribute(b).name()) == 0)
        b2 = b; 
      if (this.m_preferredColourDimension != null && this.m_preferredColourDimension.compareTo(paramInstances.attribute(b).name()) == 0)
        b3 = b; 
    } 
    this.m_XCombo.setModel(new DefaultComboBoxModel(arrayOfString1));
    this.m_YCombo.setModel(new DefaultComboBoxModel(arrayOfString2));
    this.m_ColourCombo.setModel(new DefaultComboBoxModel(arrayOfString3));
    this.m_XCombo.setEnabled(true);
    this.m_YCombo.setEnabled(true);
    if (this.m_splitListener == null) {
      this.m_ColourCombo.setEnabled(true);
      this.m_ColourCombo.setSelectedIndex(paramInstances.numAttributes() - 1);
    } 
    this.m_plotSurround.setBorder(BorderFactory.createTitledBorder("Plot: " + paramInstances.relationName()));
    try {
      if (b1 != -1)
        setXIndex(b1); 
      if (b2 != -1)
        setYIndex(b2); 
      if (b3 != -1)
        this.m_ColourCombo.setSelectedIndex(b3); 
    } catch (Exception exception) {
      System.err.println("Problem setting preferred Visualization dimensions");
    } 
  }
  
  public void setMasterPlot(PlotData2D paramPlotData2D) throws Exception {
    this.m_plot.setMasterPlot(paramPlotData2D);
    setUpComboBoxes(paramPlotData2D.m_plotInstances);
    this.m_saveBut.setEnabled(true);
    repaint();
  }
  
  public void addPlot(PlotData2D paramPlotData2D) throws Exception {
    this.m_plot.addPlot(paramPlotData2D);
    if (this.m_plot.m_plot2D.getMasterPlot() != null)
      setUpComboBoxes(paramPlotData2D.m_plotInstances); 
    this.m_saveBut.setEnabled(true);
    repaint();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length < 1) {
        System.err.println("Usage : weka.gui.visualize.VisualizePanel <dataset> [<dataset> <dataset>...]");
        System.exit(1);
      } 
      JFrame jFrame = new JFrame("Weka Explorer: Visualize");
      jFrame.setSize(500, 400);
      jFrame.getContentPane().setLayout(new BorderLayout());
      VisualizePanel visualizePanel = new VisualizePanel();
      jFrame.getContentPane().add(visualizePanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.setVisible(true);
      if (paramArrayOfString.length >= 1)
        for (byte b = 0; b < paramArrayOfString.length; b++) {
          System.err.println("Loading instances from " + paramArrayOfString[b]);
          BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[b]));
          Instances instances = new Instances(bufferedReader);
          instances.setClassIndex(instances.numAttributes() - 1);
          PlotData2D plotData2D = new PlotData2D(instances);
          if (b == 0) {
            plotData2D.setPlotName("Master plot");
            visualizePanel.setMasterPlot(plotData2D);
          } else {
            plotData2D.setPlotName("Plot " + (b + 1));
            plotData2D.m_useCustomColour = true;
            plotData2D.m_customColour = (b % 2 == 0) ? Color.red : Color.blue;
            visualizePanel.addPlot(plotData2D);
          } 
        }  
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  protected class PlotPanel extends JPanel implements Plot2DCompanion {
    protected Plot2D m_plot2D;
    
    protected Instances m_plotInstances;
    
    protected PlotData2D m_originalPlot;
    
    protected int m_xIndex;
    
    protected int m_yIndex;
    
    protected int m_cIndex;
    
    protected int m_sIndex;
    
    private final int m_axisPad = 5;
    
    private final int m_tickSize = 5;
    
    private int m_XaxisStart;
    
    private int m_YaxisStart;
    
    private int m_XaxisEnd;
    
    private int m_YaxisEnd;
    
    private boolean m_createShape;
    
    private FastVector m_shapes;
    
    private FastVector m_shapePoints;
    
    private Dimension m_newMousePos;
    
    private final VisualizePanel this$0;
    
    public PlotPanel(VisualizePanel this$0) {
      this.this$0 = this$0;
      this.m_plot2D = new Plot2D();
      this.m_plotInstances = null;
      this.m_originalPlot = null;
      this.m_xIndex = 0;
      this.m_yIndex = 0;
      this.m_cIndex = 0;
      this.m_sIndex = 0;
      this.m_axisPad = 5;
      this.m_tickSize = 5;
      this.m_XaxisStart = 0;
      this.m_YaxisStart = 0;
      this.m_XaxisEnd = 0;
      this.m_YaxisEnd = 0;
      setBackground(this.m_plot2D.getBackground());
      setLayout(new BorderLayout());
      add(this.m_plot2D, "Center");
      this.m_plot2D.setPlotCompanion(this);
      this.m_createShape = false;
      this.m_shapes = null;
      this.m_shapePoints = null;
      this.m_newMousePos = new Dimension();
      addMouseListener((MouseListener)new Object(this, this$0));
      addMouseMotionListener((MouseMotionListener)new Object(this));
      this$0.m_submit.addActionListener((ActionListener)new Object(this, this$0));
      this$0.m_cancel.addActionListener((ActionListener)new Object(this, this$0));
    }
    
    public FastVector getShapes() {
      return this.m_shapes;
    }
    
    public void cancelShapes() {
      if (this.this$0.m_splitListener == null) {
        this.this$0.m_submit.setText("Reset");
        this.this$0.m_submit.setActionCommand("Reset");
        if (this.m_originalPlot == null || this.m_originalPlot.m_plotInstances == this.m_plotInstances) {
          this.this$0.m_submit.setEnabled(false);
        } else {
          this.this$0.m_submit.setEnabled(true);
        } 
      } else {
        this.this$0.m_submit.setEnabled(false);
      } 
      this.m_createShape = false;
      this.m_shapePoints = null;
      this.m_shapes = null;
      repaint();
    }
    
    public void setShapes(FastVector param1FastVector) {
      if (param1FastVector != null) {
        this.m_shapes = new FastVector(param1FastVector.size());
        for (byte b = 0; b < param1FastVector.size(); b++) {
          FastVector fastVector = new FastVector(((FastVector)param1FastVector.elementAt(b)).size());
          this.m_shapes.addElement(fastVector);
          for (byte b1 = 0; b1 < ((FastVector)param1FastVector.elementAt(b)).size(); b1++)
            fastVector.addElement(((FastVector)param1FastVector.elementAt(b)).elementAt(b1)); 
        } 
      } else {
        this.m_shapes = null;
      } 
      repaint();
    }
    
    private boolean checkPoints(double param1Double1, double param1Double2) {
      return !(param1Double1 < 0.0D || param1Double1 > (getSize()).width || param1Double2 < 0.0D || param1Double2 > (getSize()).height);
    }
    
    public boolean inSplit(Instance param1Instance) {
      if (this.m_shapes != null)
        for (byte b = 0; b < this.m_shapes.size(); b++) {
          FastVector fastVector = (FastVector)this.m_shapes.elementAt(b);
          if (((Double)fastVector.elementAt(0)).intValue() == 1) {
            double d1 = ((Double)fastVector.elementAt(1)).doubleValue();
            double d2 = ((Double)fastVector.elementAt(2)).doubleValue();
            double d3 = ((Double)fastVector.elementAt(3)).doubleValue();
            double d4 = ((Double)fastVector.elementAt(4)).doubleValue();
            if (param1Instance.value(this.m_xIndex) >= d1 && param1Instance.value(this.m_xIndex) <= d3 && param1Instance.value(this.m_yIndex) <= d2 && param1Instance.value(this.m_yIndex) >= d4)
              return true; 
          } else if (((Double)fastVector.elementAt(0)).intValue() == 2) {
            if (inPoly(fastVector, param1Instance.value(this.m_xIndex), param1Instance.value(this.m_yIndex)))
              return true; 
          } else if (((Double)fastVector.elementAt(0)).intValue() == 3 && inPolyline(fastVector, param1Instance.value(this.m_xIndex), param1Instance.value(this.m_yIndex))) {
            return true;
          } 
        }  
      return false;
    }
    
    private boolean inPolyline(FastVector param1FastVector, double param1Double1, double param1Double2) {
      byte b1 = 0;
      for (byte b2 = 1; b2 < param1FastVector.size() - 4; b2 += 2) {
        double d6 = ((Double)param1FastVector.elementAt(b2 + 1)).doubleValue();
        double d8 = ((Double)param1FastVector.elementAt(b2 + 3)).doubleValue();
        double d5 = ((Double)param1FastVector.elementAt(b2)).doubleValue();
        double d7 = ((Double)param1FastVector.elementAt(b2 + 2)).doubleValue();
        double d4 = d8 - d6;
        double d3 = d7 - d5;
        if (b2 == 1 && b2 == param1FastVector.size() - 6) {
          if (d4 != 0.0D) {
            double d = (param1Double2 - d6) / d4;
            if (d3 * d + d5 >= param1Double1)
              b1++; 
          } 
        } else if (b2 == 1) {
          if ((param1Double2 < d8 && d4 > 0.0D) || (param1Double2 > d8 && d4 < 0.0D)) {
            double d = (param1Double2 - d6) / d4;
            if (d3 * d + d5 >= param1Double1)
              b1++; 
          } 
        } else if (b2 == param1FastVector.size() - 6) {
          if ((param1Double2 <= d6 && d4 < 0.0D) || (param1Double2 >= d6 && d4 > 0.0D)) {
            double d = (param1Double2 - d6) / d4;
            if (d3 * d + d5 >= param1Double1)
              b1++; 
          } 
        } else if (((d6 <= param1Double2 && param1Double2 < d8) || (d8 < param1Double2 && param1Double2 <= d6)) && d4 != 0.0D) {
          double d = (param1Double2 - d6) / d4;
          if (d3 * d + d5 >= param1Double1)
            b1++; 
        } 
      } 
      double d1 = ((Double)param1FastVector.elementAt(param1FastVector.size() - 2)).doubleValue();
      double d2 = ((Double)param1FastVector.elementAt(param1FastVector.size() - 1)).doubleValue();
      if (d1 > d2) {
        if (d1 >= param1Double2 && param1Double2 > d2)
          b1++; 
      } else if (d1 >= param1Double2 || param1Double2 > d2) {
        b1++;
      } 
      return (b1 % 2 == 1);
    }
    
    private boolean inPoly(FastVector param1FastVector, double param1Double1, double param1Double2) {
      byte b1 = 0;
      for (byte b2 = 1; b2 < param1FastVector.size() - 2; b2 += 2) {
        double d1 = ((Double)param1FastVector.elementAt(b2 + 1)).doubleValue();
        double d2 = ((Double)param1FastVector.elementAt(b2 + 3)).doubleValue();
        if ((d1 <= param1Double2 && param1Double2 < d2) || (d2 < param1Double2 && param1Double2 <= d1)) {
          double d = d2 - d1;
          if (d != 0.0D) {
            double d5 = ((Double)param1FastVector.elementAt(b2)).doubleValue();
            double d6 = ((Double)param1FastVector.elementAt(b2 + 2)).doubleValue();
            double d3 = d6 - d5;
            double d4 = (param1Double2 - d1) / d;
            if (d3 * d4 + d5 >= param1Double1)
              b1++; 
          } 
        } 
      } 
      return (b1 % 2 == 1);
    }
    
    public void setJitter(int param1Int) {
      this.m_plot2D.setJitter(param1Int);
    }
    
    public void setXindex(int param1Int) {
      if (param1Int != this.m_xIndex)
        cancelShapes(); 
      this.m_xIndex = param1Int;
      this.m_plot2D.setXindex(param1Int);
      if (this.this$0.m_showAttBars)
        this.this$0.m_attrib.setX(param1Int); 
    }
    
    public void setYindex(int param1Int) {
      if (param1Int != this.m_yIndex)
        cancelShapes(); 
      this.m_yIndex = param1Int;
      this.m_plot2D.setYindex(param1Int);
      if (this.this$0.m_showAttBars)
        this.this$0.m_attrib.setY(param1Int); 
    }
    
    public void setCindex(int param1Int) {
      this.m_cIndex = param1Int;
      this.m_plot2D.setCindex(param1Int);
      if (this.this$0.m_showAttBars)
        this.this$0.m_attrib.setCindex(param1Int, this.m_plot2D.getMaxC(), this.m_plot2D.getMinC()); 
      this.this$0.m_classPanel.setCindex(param1Int);
      repaint();
    }
    
    public void setSindex(int param1Int) {
      if (param1Int != this.m_sIndex) {
        this.m_shapePoints = null;
        this.m_createShape = false;
      } 
      this.m_sIndex = param1Int;
      repaint();
    }
    
    public void setMasterPlot(PlotData2D param1PlotData2D) throws Exception {
      this.m_plot2D.removeAllPlots();
      addPlot(param1PlotData2D);
    }
    
    public void addPlot(PlotData2D param1PlotData2D) throws Exception {
      if (this.m_plot2D.getPlots().size() == 0) {
        this.m_plot2D.addPlot(param1PlotData2D);
        if (this.this$0.m_plotSurround.getComponentCount() > 1 && this.this$0.m_plotSurround.getComponent(1) == this.this$0.m_attrib && this.this$0.m_showAttBars) {
          try {
            this.this$0.m_attrib.setInstances(param1PlotData2D.m_plotInstances);
            this.this$0.m_attrib.setCindex(0);
            this.this$0.m_attrib.setX(0);
            this.this$0.m_attrib.setY(0);
          } catch (Exception exception) {
            this.this$0.m_plotSurround.remove(this.this$0.m_attrib);
            System.err.println("Warning : data contains more attributes than can be displayed as attribute bars.");
            if (this.this$0.m_Log != null)
              this.this$0.m_Log.logMessage("Warning : data contains more attributes than can be displayed as attribute bars."); 
          } 
        } else if (this.this$0.m_showAttBars) {
          try {
            this.this$0.m_attrib.setInstances(param1PlotData2D.m_plotInstances);
            this.this$0.m_attrib.setCindex(0);
            this.this$0.m_attrib.setX(0);
            this.this$0.m_attrib.setY(0);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = 1;
            gridBagConstraints.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.weighty = 5.0D;
            this.this$0.m_plotSurround.add(this.this$0.m_attrib, gridBagConstraints);
          } catch (Exception exception) {
            System.err.println("Warning : data contains more attributes than can be displayed as attribute bars.");
            if (this.this$0.m_Log != null)
              this.this$0.m_Log.logMessage("Warning : data contains more attributes than can be displayed as attribute bars."); 
          } 
        } 
        this.this$0.m_classPanel.setInstances(param1PlotData2D.m_plotInstances);
        plotReset(param1PlotData2D.m_plotInstances, param1PlotData2D.getCindex());
        if (param1PlotData2D.m_useCustomColour) {
          this.this$0.remove(this.this$0.m_classSurround);
          switchToLegend();
          this.this$0.m_legendPanel.setPlotList(this.m_plot2D.getPlots());
          this.this$0.m_ColourCombo.setEnabled(false);
        } 
      } else {
        if (!param1PlotData2D.m_useCustomColour) {
          this.this$0.add(this.this$0.m_classSurround, "South");
          this.this$0.m_ColourCombo.setEnabled(true);
        } 
        if (this.m_plot2D.getPlots().size() == 1)
          switchToLegend(); 
        this.m_plot2D.addPlot(param1PlotData2D);
        this.this$0.m_legendPanel.setPlotList(this.m_plot2D.getPlots());
      } 
    }
    
    protected void switchToLegend() {
      if (this.this$0.m_plotSurround.getComponentCount() > 1 && this.this$0.m_plotSurround.getComponent(1) == this.this$0.m_attrib)
        this.this$0.m_plotSurround.remove(this.this$0.m_attrib); 
      if (this.this$0.m_plotSurround.getComponentCount() > 1 && this.this$0.m_plotSurround.getComponent(1) == this.this$0.m_legendPanel)
        return; 
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 1;
      gridBagConstraints.insets = new Insets(0, 0, 0, 0);
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.gridwidth = 1;
      gridBagConstraints.gridheight = 1;
      gridBagConstraints.weighty = 5.0D;
      this.this$0.m_plotSurround.add(this.this$0.m_legendPanel, gridBagConstraints);
      setSindex(0);
      this.this$0.m_ShapeCombo.setEnabled(false);
    }
    
    private void plotReset(Instances param1Instances, int param1Int) {
      if (this.this$0.m_splitListener == null) {
        this.this$0.m_submit.setText("Reset");
        this.this$0.m_submit.setActionCommand("Reset");
        if (this.m_originalPlot == null || this.m_originalPlot.m_plotInstances == param1Instances) {
          this.this$0.m_submit.setEnabled(false);
        } else {
          this.this$0.m_submit.setEnabled(true);
        } 
      } else {
        this.this$0.m_submit.setEnabled(false);
      } 
      this.m_plotInstances = param1Instances;
      if (this.this$0.m_splitListener != null)
        this.m_plotInstances.randomize(new Random()); 
      this.m_xIndex = 0;
      this.m_yIndex = 0;
      this.m_cIndex = param1Int;
      cancelShapes();
    }
    
    public void setColours(FastVector param1FastVector) {
      this.m_plot2D.setColours(param1FastVector);
      this.this$0.m_colorList = param1FastVector;
    }
    
    private void drawShapes(Graphics param1Graphics) {
      if (this.m_shapes != null)
        for (byte b = 0; b < this.m_shapes.size(); b++) {
          FastVector fastVector = (FastVector)this.m_shapes.elementAt(b);
          if (((Double)fastVector.elementAt(0)).intValue() == 1) {
            int i = (int)this.m_plot2D.convertToPanelX(((Double)fastVector.elementAt(1)).doubleValue());
            int j = (int)this.m_plot2D.convertToPanelY(((Double)fastVector.elementAt(2)).doubleValue());
            int k = (int)this.m_plot2D.convertToPanelX(((Double)fastVector.elementAt(3)).doubleValue());
            int m = (int)this.m_plot2D.convertToPanelY(((Double)fastVector.elementAt(4)).doubleValue());
            param1Graphics.setColor(Color.gray);
            param1Graphics.fillRect(i, j, k - i, m - j);
            param1Graphics.setColor(Color.black);
            param1Graphics.drawRect(i, j, k - i, m - j);
          } else if (((Double)fastVector.elementAt(0)).intValue() == 2) {
            int[] arrayOfInt1 = getXCoords(fastVector);
            int[] arrayOfInt2 = getYCoords(fastVector);
            param1Graphics.setColor(Color.gray);
            param1Graphics.fillPolygon(arrayOfInt1, arrayOfInt2, (fastVector.size() - 1) / 2);
            param1Graphics.setColor(Color.black);
            param1Graphics.drawPolyline(arrayOfInt1, arrayOfInt2, (fastVector.size() - 1) / 2);
          } else if (((Double)fastVector.elementAt(0)).intValue() == 3) {
            FastVector fastVector1 = makePolygon(fastVector);
            int[] arrayOfInt1 = getXCoords(fastVector1);
            int[] arrayOfInt2 = getYCoords(fastVector1);
            param1Graphics.setColor(Color.gray);
            param1Graphics.fillPolygon(arrayOfInt1, arrayOfInt2, (fastVector1.size() - 1) / 2);
            param1Graphics.setColor(Color.black);
            param1Graphics.drawPolyline(arrayOfInt1, arrayOfInt2, (fastVector1.size() - 1) / 2);
          } 
        }  
      if (this.m_shapePoints != null && (((Double)this.m_shapePoints.elementAt(0)).intValue() == 2 || ((Double)this.m_shapePoints.elementAt(0)).intValue() == 3)) {
        param1Graphics.setColor(Color.black);
        param1Graphics.setXORMode(Color.white);
        int[] arrayOfInt1 = getXCoords(this.m_shapePoints);
        int[] arrayOfInt2 = getYCoords(this.m_shapePoints);
        param1Graphics.drawPolyline(arrayOfInt1, arrayOfInt2, (this.m_shapePoints.size() - 1) / 2);
        this.m_newMousePos.width = (int)Math.ceil(this.m_plot2D.convertToPanelX(((Double)this.m_shapePoints.elementAt(this.m_shapePoints.size() - 2)).doubleValue()));
        this.m_newMousePos.height = (int)Math.ceil(this.m_plot2D.convertToPanelY(((Double)this.m_shapePoints.elementAt(this.m_shapePoints.size() - 1)).doubleValue()));
        param1Graphics.drawLine((int)Math.ceil(this.m_plot2D.convertToPanelX(((Double)this.m_shapePoints.elementAt(this.m_shapePoints.size() - 2)).doubleValue())), (int)Math.ceil(this.m_plot2D.convertToPanelY(((Double)this.m_shapePoints.elementAt(this.m_shapePoints.size() - 1)).doubleValue())), this.m_newMousePos.width, this.m_newMousePos.height);
        param1Graphics.setPaintMode();
      } 
    }
    
    private double[] lineIntersect(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7) {
      double d1 = -100.0D;
      double d2 = -100.0D;
      if (param1Double5 == 0.0D) {
        if ((param1Double1 <= param1Double7 && param1Double7 < param1Double3) || (param1Double1 >= param1Double7 && param1Double7 > param1Double3)) {
          double d3 = param1Double1 - param1Double3;
          double d4 = (param1Double7 - param1Double3) / d3;
          d2 = (param1Double2 - param1Double4) * d4 + param1Double4;
          if (0.0D <= d2 && d2 <= param1Double6) {
            d1 = param1Double7;
          } else {
            d1 = -100.0D;
          } 
        } 
      } else if (param1Double6 == 0.0D && ((param1Double2 <= param1Double7 && param1Double7 < param1Double4) || (param1Double2 >= param1Double7 && param1Double7 > param1Double4))) {
        double d3 = param1Double2 - param1Double4;
        double d4 = (param1Double7 - param1Double4) / d3;
        d1 = (param1Double1 - param1Double3) * d4 + param1Double3;
        if (0.0D <= d1 && d1 <= param1Double5) {
          d2 = param1Double7;
        } else {
          d1 = -100.0D;
        } 
      } 
      double[] arrayOfDouble = new double[2];
      arrayOfDouble[0] = d1;
      arrayOfDouble[1] = d2;
      return arrayOfDouble;
    }
    
    private FastVector makePolygon(FastVector param1FastVector) {
      double[] arrayOfDouble;
      FastVector fastVector = new FastVector(param1FastVector.size() + 10);
      byte b1 = 0;
      byte b2 = 0;
      for (byte b3 = 0; b3 < param1FastVector.size() - 2; b3++)
        fastVector.addElement(new Double(((Double)param1FastVector.elementAt(b3)).doubleValue())); 
      double d1 = this.m_plot2D.convertToPanelX(((Double)param1FastVector.elementAt(1)).doubleValue());
      double d2 = this.m_plot2D.convertToPanelY(((Double)param1FastVector.elementAt(2)).doubleValue());
      double d3 = this.m_plot2D.convertToPanelX(((Double)param1FastVector.elementAt(3)).doubleValue());
      double d4 = this.m_plot2D.convertToPanelY(((Double)param1FastVector.elementAt(4)).doubleValue());
      if (d1 < 0.0D) {
        arrayOfDouble = lineIntersect(d1, d2, d3, d4, 0.0D, getHeight(), 0.0D);
        b1 = 0;
        if (arrayOfDouble[0] < 0.0D)
          if (d2 < 0.0D) {
            arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, 0.0D);
            b1 = 1;
          } else {
            arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, getHeight());
            b1 = 3;
          }  
      } else if (d1 > getWidth()) {
        arrayOfDouble = lineIntersect(d1, d2, d3, d4, 0.0D, getHeight(), getWidth());
        b1 = 2;
        if (arrayOfDouble[0] < 0.0D)
          if (d2 < 0.0D) {
            arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, 0.0D);
            b1 = 1;
          } else {
            arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, getHeight());
            b1 = 3;
          }  
      } else if (d2 < 0.0D) {
        arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, 0.0D);
        b1 = 1;
      } else {
        arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, getHeight());
        b1 = 3;
      } 
      fastVector.setElementAt(new Double(this.m_plot2D.convertToAttribX(arrayOfDouble[0])), 1);
      fastVector.setElementAt(new Double(this.m_plot2D.convertToAttribY(arrayOfDouble[1])), 2);
      d1 = this.m_plot2D.convertToPanelX(((Double)param1FastVector.elementAt(param1FastVector.size() - 4)).doubleValue());
      d2 = this.m_plot2D.convertToPanelY(((Double)param1FastVector.elementAt(param1FastVector.size() - 3)).doubleValue());
      d3 = this.m_plot2D.convertToPanelX(((Double)param1FastVector.elementAt(param1FastVector.size() - 6)).doubleValue());
      d4 = this.m_plot2D.convertToPanelY(((Double)param1FastVector.elementAt(param1FastVector.size() - 5)).doubleValue());
      if (d1 < 0.0D) {
        arrayOfDouble = lineIntersect(d1, d2, d3, d4, 0.0D, getHeight(), 0.0D);
        b2 = 0;
        if (arrayOfDouble[0] < 0.0D)
          if (d2 < 0.0D) {
            arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, 0.0D);
            b2 = 1;
          } else {
            arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, getHeight());
            b2 = 3;
          }  
      } else if (d1 > getWidth()) {
        arrayOfDouble = lineIntersect(d1, d2, d3, d4, 0.0D, getHeight(), getWidth());
        b2 = 2;
        if (arrayOfDouble[0] < 0.0D)
          if (d2 < 0.0D) {
            arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, 0.0D);
            b2 = 1;
          } else {
            arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, getHeight());
            b2 = 3;
          }  
      } else if (d2 < 0.0D) {
        arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, 0.0D);
        b2 = 1;
      } else {
        arrayOfDouble = lineIntersect(d1, d2, d3, d4, getWidth(), 0.0D, getHeight());
        b2 = 3;
      } 
      fastVector.setElementAt(new Double(this.m_plot2D.convertToAttribX(arrayOfDouble[0])), fastVector.size() - 2);
      fastVector.setElementAt(new Double(this.m_plot2D.convertToAttribY(arrayOfDouble[1])), fastVector.size() - 1);
      int i = getWidth() * (b2 & 0x1 ^ (b2 & 0x2) / 2);
      int j = getHeight() * (b2 & 0x2) / 2;
      if (inPolyline(param1FastVector, this.m_plot2D.convertToAttribX(i), this.m_plot2D.convertToAttribY(j))) {
        fastVector.addElement(new Double(this.m_plot2D.convertToAttribX(i)));
        fastVector.addElement(new Double(this.m_plot2D.convertToAttribY(j)));
        int k;
        for (k = (b2 + 1) % 4; k != b1; k = (k + 1) % 4) {
          i = getWidth() * (k & 0x1 ^ (k & 0x2) / 2);
          j = getHeight() * (k & 0x2) / 2;
          fastVector.addElement(new Double(this.m_plot2D.convertToAttribX(i)));
          fastVector.addElement(new Double(this.m_plot2D.convertToAttribY(j)));
        } 
      } else {
        i = getWidth() * (b2 & 0x2) / 2;
        j = getHeight() * (0x1 & (b2 & 0x1 ^ (b2 & 0x2) / 2 ^ 0xFFFFFFFF));
        if (inPolyline(param1FastVector, this.m_plot2D.convertToAttribX(i), this.m_plot2D.convertToAttribY(j))) {
          fastVector.addElement(new Double(this.m_plot2D.convertToAttribX(i)));
          fastVector.addElement(new Double(this.m_plot2D.convertToAttribY(j)));
          int k;
          for (k = (b2 + 3) % 4; k != b1; k = (k + 3) % 4) {
            i = getWidth() * (k & 0x2) / 2;
            j = getHeight() * (0x1 & (k & 0x1 ^ (k & 0x2) / 2 ^ 0xFFFFFFFF));
            fastVector.addElement(new Double(this.m_plot2D.convertToAttribX(i)));
            fastVector.addElement(new Double(this.m_plot2D.convertToAttribY(j)));
          } 
        } 
      } 
      return fastVector;
    }
    
    private int[] getXCoords(FastVector param1FastVector) {
      int i = (param1FastVector.size() - 1) / 2;
      int[] arrayOfInt = new int[i];
      for (byte b = 0; b < i; b++)
        arrayOfInt[b] = (int)this.m_plot2D.convertToPanelX(((Double)param1FastVector.elementAt(b * 2 + 1)).doubleValue()); 
      return arrayOfInt;
    }
    
    private int[] getYCoords(FastVector param1FastVector) {
      int i = (param1FastVector.size() - 1) / 2;
      int[] arrayOfInt = new int[i];
      for (byte b = 0; b < i; b++)
        arrayOfInt[b] = (int)this.m_plot2D.convertToPanelY(((Double)param1FastVector.elementAt(b * 2 + 2)).doubleValue()); 
      return arrayOfInt;
    }
    
    public void prePlot(Graphics param1Graphics) {
      paintComponent(param1Graphics);
      if (this.m_plotInstances != null)
        drawShapes(param1Graphics); 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\VisualizePanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */