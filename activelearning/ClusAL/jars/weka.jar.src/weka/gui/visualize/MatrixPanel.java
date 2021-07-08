package weka.gui.visualize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import weka.core.FastVector;
import weka.core.Instances;
import weka.gui.ExtensionFileFilter;

public class MatrixPanel extends JPanel {
  private final Plot m_plotsPanel;
  
  protected final ClassPanel m_cp = new ClassPanel();
  
  protected JPanel optionsPanel;
  
  protected JSplitPane jp;
  
  protected JButton m_updateBt = new JButton("Update");
  
  protected JButton m_selAttrib = new JButton("Select Attributes");
  
  protected Instances m_data = null;
  
  protected JList m_attribList = new JList();
  
  protected final JScrollPane m_js = new JScrollPane();
  
  protected JComboBox m_classAttrib = new JComboBox();
  
  protected JSlider m_plotSize = new JSlider(50, 500, 100);
  
  protected JSlider m_pointSize = new JSlider(1, 10, 1);
  
  protected JSlider m_jitter = new JSlider(0, 20, 0);
  
  private Random rnd = new Random();
  
  private int[][] jitterVals;
  
  private int datapointSize = 1;
  
  protected JTextField m_resamplePercent = new JTextField(5);
  
  protected JButton m_resampleBt = new JButton("SubSample % :");
  
  protected JTextField m_rseed = new JTextField(5);
  
  private final JLabel m_plotSizeLb = new JLabel("PlotSize: [100]");
  
  private final JLabel m_pointSizeLb = new JLabel("PointSize: [10]");
  
  private int[] m_selectedAttribs;
  
  private int m_classIndex;
  
  private int[][] m_points;
  
  private int[] m_pointColors;
  
  private boolean[][] m_missing;
  
  private int[] m_type;
  
  private Dimension m_plotLBSizeD;
  
  private Dimension m_pointLBSizeD;
  
  private FastVector m_colorList = new FastVector();
  
  private static final Color[] m_defaultColors = new Color[] { 
      Color.blue, Color.red, Color.cyan, new Color(75, 123, 130), Color.pink, Color.green, Color.orange, new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 255, 0), 
      Color.black };
  
  private final Color fontColor = new Color(98, 101, 156);
  
  private final Font f = new Font("Dialog", 1, 11);
  
  public MatrixPanel() {
    this.m_rseed.setText("1");
    this.m_selAttrib.addActionListener(new ActionListener(this) {
          private final MatrixPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            JDialog jDialog = new JDialog((JFrame)this.this$0.getTopLevelAncestor(), "Attribute Selection Panel", true);
            JPanel jPanel = new JPanel();
            JScrollPane jScrollPane = new JScrollPane(this.this$0.m_attribList);
            JButton jButton1 = new JButton("OK");
            JButton jButton2 = new JButton("Cancel");
            int[] arrayOfInt = this.this$0.m_attribList.getSelectedIndices();
            jButton1.addActionListener((ActionListener)new Object(this, jDialog));
            jButton2.addActionListener((ActionListener)new Object(this, arrayOfInt, jDialog));
            jDialog.addWindowListener((WindowListener)new Object(this, arrayOfInt, jDialog));
            jPanel.add(jButton1);
            jPanel.add(jButton2);
            jDialog.getContentPane().add(jScrollPane, "Center");
            jDialog.getContentPane().add(jPanel, "South");
            if ((jScrollPane.getPreferredSize()).width < 200) {
              jDialog.setSize(250, 250);
            } else {
              jDialog.setSize((jScrollPane.getPreferredSize()).width + 10, 250);
            } 
            jDialog.setLocation((this.this$0.m_selAttrib.getLocationOnScreen()).x, (this.this$0.m_selAttrib.getLocationOnScreen()).y - jDialog.getHeight());
            jDialog.setVisible(true);
          }
        });
    this.m_updateBt.addActionListener(new ActionListener(this) {
          private final MatrixPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.initInternalFields();
            MatrixPanel.Plot plot = this.this$0.m_plotsPanel;
            plot.setCellSize(this.this$0.m_plotSize.getValue());
            Dimension dimension = new Dimension(this.this$0.m_selectedAttribs.length * (plot.cellSize + plot.extpad) + 2, this.this$0.m_selectedAttribs.length * (plot.cellSize + plot.extpad) + 2);
            plot.setPreferredSize(dimension);
            plot.setSize(plot.getPreferredSize());
            plot.setJitter(this.this$0.m_jitter.getValue());
            this.this$0.m_js.revalidate();
            this.this$0.m_cp.setColours(this.this$0.m_colorList);
            this.this$0.m_cp.setCindex(this.this$0.m_classIndex);
            this.this$0.repaint();
          }
        });
    this.m_updateBt.setPreferredSize(this.m_selAttrib.getPreferredSize());
    this.m_plotSize.addChangeListener(new ChangeListener(this) {
          private final MatrixPanel this$0;
          
          public void stateChanged(ChangeEvent param1ChangeEvent) {
            this.this$0.m_plotSizeLb.setText("PlotSize: [" + this.this$0.m_plotSize.getValue() + "]");
            this.this$0.m_plotSizeLb.setPreferredSize(this.this$0.m_plotLBSizeD);
            this.this$0.m_jitter.setMaximum(this.this$0.m_plotSize.getValue() / 5);
          }
        });
    this.m_pointSize.addChangeListener(new ChangeListener(this) {
          private final MatrixPanel this$0;
          
          public void stateChanged(ChangeEvent param1ChangeEvent) {
            this.this$0.m_pointSizeLb.setText("PointSize: [" + this.this$0.m_pointSize.getValue() + "]");
            this.this$0.m_pointSizeLb.setPreferredSize(this.this$0.m_pointLBSizeD);
            this.this$0.datapointSize = this.this$0.m_pointSize.getValue();
          }
        });
    this.m_resampleBt.addActionListener(new ActionListener(this) {
          private final MatrixPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            JLabel jLabel1 = new JLabel("Random Seed: ");
            JTextField jTextField1 = this.this$0.m_rseed;
            JLabel jLabel2 = new JLabel("Subsample as");
            JLabel jLabel3 = new JLabel("% of input: ");
            JTextField jTextField2 = new JTextField(5);
            jTextField2.setText(this.this$0.m_resamplePercent.getText());
            JButton jButton = new JButton("Done");
            Object object = new Object(this, (JFrame)this.this$0.getTopLevelAncestor(), "Subsample % Panel", true, jTextField2);
            object.setDefaultCloseOperation(2);
            jButton.addActionListener((ActionListener)new Object(this, (JDialog)object));
            GridBagLayout gridBagLayout = new GridBagLayout();
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            JPanel jPanel1 = new JPanel(gridBagLayout);
            gridBagConstraints.anchor = 17;
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = new Insets(0, 2, 2, 2);
            gridBagConstraints.gridwidth = -1;
            jPanel1.add(jLabel1, gridBagConstraints);
            gridBagConstraints.weightx = 0.0D;
            gridBagConstraints.gridwidth = 0;
            gridBagConstraints.weightx = 1.0D;
            jPanel1.add(jTextField1, gridBagConstraints);
            gridBagConstraints.insets = new Insets(8, 2, 0, 2);
            gridBagConstraints.weightx = 0.0D;
            jPanel1.add(jLabel2, gridBagConstraints);
            gridBagConstraints.insets = new Insets(0, 2, 2, 2);
            gridBagConstraints.gridwidth = -1;
            jPanel1.add(jLabel3, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            gridBagConstraints.weightx = 1.0D;
            jPanel1.add(jTextField2, gridBagConstraints);
            gridBagConstraints.insets = new Insets(8, 2, 2, 2);
            JPanel jPanel2 = new JPanel(gridBagLayout);
            gridBagConstraints.fill = 2;
            gridBagConstraints.gridwidth = 0;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 0.0D;
            jPanel2.add(jPanel1, gridBagConstraints);
            gridBagConstraints.insets = new Insets(8, 4, 8, 4);
            jPanel2.add(jButton, gridBagConstraints);
            object.getContentPane().setLayout(new BorderLayout());
            object.getContentPane().add(jPanel2, "North");
            object.pack();
            object.setLocation((this.this$0.m_resampleBt.getLocationOnScreen()).x, (this.this$0.m_resampleBt.getLocationOnScreen()).y - object.getHeight());
            object.setVisible(true);
          }
        });
    this.optionsPanel = new JPanel(new GridBagLayout());
    JPanel jPanel1 = new JPanel(new BorderLayout());
    JPanel jPanel2 = new JPanel(new GridBagLayout());
    JPanel jPanel3 = new JPanel(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    this.m_plotLBSizeD = this.m_plotSizeLb.getPreferredSize();
    this.m_pointLBSizeD = this.m_pointSizeLb.getPreferredSize();
    this.m_pointSizeLb.setText("PointSize: [1]");
    this.m_pointSizeLb.setPreferredSize(this.m_pointLBSizeD);
    this.m_resampleBt.setPreferredSize(this.m_selAttrib.getPreferredSize());
    gridBagConstraints.fill = 2;
    gridBagConstraints.anchor = 18;
    gridBagConstraints.insets = new Insets(2, 2, 2, 2);
    jPanel3.add(this.m_plotSizeLb, gridBagConstraints);
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.gridwidth = 0;
    jPanel3.add(this.m_plotSize, gridBagConstraints);
    gridBagConstraints.weightx = 0.0D;
    gridBagConstraints.gridwidth = -1;
    jPanel3.add(this.m_pointSizeLb, gridBagConstraints);
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.gridwidth = 0;
    jPanel3.add(this.m_pointSize, gridBagConstraints);
    gridBagConstraints.weightx = 0.0D;
    gridBagConstraints.gridwidth = -1;
    jPanel3.add(new JLabel("Jitter: "), gridBagConstraints);
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.gridwidth = 0;
    jPanel3.add(this.m_jitter, gridBagConstraints);
    jPanel3.add(this.m_classAttrib, gridBagConstraints);
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.fill = 0;
    jPanel2.add(this.m_updateBt, gridBagConstraints);
    jPanel2.add(this.m_selAttrib, gridBagConstraints);
    gridBagConstraints.gridwidth = -1;
    gridBagConstraints.weightx = 0.0D;
    gridBagConstraints.fill = 3;
    gridBagConstraints.anchor = 17;
    jPanel2.add(this.m_resampleBt, gridBagConstraints);
    gridBagConstraints.gridwidth = 0;
    jPanel2.add(this.m_resamplePercent, gridBagConstraints);
    jPanel1.setBorder(BorderFactory.createTitledBorder("Class Colour"));
    jPanel1.add(this.m_cp, "South");
    gridBagConstraints.insets = new Insets(8, 5, 2, 5);
    gridBagConstraints.anchor = 16;
    gridBagConstraints.fill = 2;
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.gridwidth = -1;
    this.optionsPanel.add(jPanel3, gridBagConstraints);
    gridBagConstraints.gridwidth = 0;
    this.optionsPanel.add(jPanel2, gridBagConstraints);
    this.optionsPanel.add(jPanel1, gridBagConstraints);
    addComponentListener(new ComponentAdapter(this) {
          private final MatrixPanel this$0;
          
          public void componentResized(ComponentEvent param1ComponentEvent) {
            this.this$0.m_js.setMinimumSize(new Dimension(this.this$0.getWidth(), this.this$0.getHeight() - (this.this$0.optionsPanel.getPreferredSize()).height - 10));
            this.this$0.jp.setDividerLocation(this.this$0.getHeight() - (this.this$0.optionsPanel.getPreferredSize()).height - 10);
          }
        });
    this.optionsPanel.setMinimumSize(new Dimension(0, 0));
    this.jp = new JSplitPane(0, this.m_js, this.optionsPanel);
    this.jp.setOneTouchExpandable(true);
    this.jp.setResizeWeight(1.0D);
    setLayout(new BorderLayout());
    add(this.jp, "Center");
    for (byte b = 0; b < m_defaultColors.length - 1; b++)
      this.m_colorList.addElement(m_defaultColors[b]); 
    this.m_selectedAttribs = this.m_attribList.getSelectedIndices();
    this.m_plotsPanel = new Plot(this);
    this.m_plotsPanel.setLayout((LayoutManager)null);
    this.m_js.getHorizontalScrollBar().setUnitIncrement(10);
    this.m_js.getVerticalScrollBar().setUnitIncrement(10);
    this.m_js.setViewportView(this.m_plotsPanel);
    this.m_js.setColumnHeaderView(this.m_plotsPanel.getColHeader());
    this.m_js.setRowHeaderView(this.m_plotsPanel.getRowHeader());
    JLabel jLabel = new JLabel(" Plot Matrix");
    jLabel.setFont(this.f);
    jLabel.setForeground(this.fontColor);
    jLabel.setHorizontalTextPosition(0);
    this.m_js.setCorner("UPPER_LEFT_CORNER", jLabel);
    this.m_cp.setInstances(this.m_data);
    this.m_cp.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
    this.m_cp.addRepaintNotify(this.m_plotsPanel);
  }
  
  public void initInternalFields() {
    Instances instances = this.m_data;
    this.m_classIndex = this.m_classAttrib.getSelectedIndex();
    this.m_selectedAttribs = this.m_attribList.getSelectedIndices();
    double d1 = 0.0D;
    double d2 = 0.0D;
    if (Double.parseDouble(this.m_resamplePercent.getText()) < 100.0D) {
      instances = new Instances(this.m_data, 0, this.m_data.numInstances());
      instances.randomize(new Random(Integer.parseInt(this.m_rseed.getText())));
      instances = new Instances(this.m_data, 0, (int)Math.round(Double.parseDouble(this.m_resamplePercent.getText()) / 100.0D * this.m_data.numInstances()));
    } 
    this.m_points = new int[instances.numInstances()][this.m_selectedAttribs.length];
    this.m_pointColors = new int[instances.numInstances()];
    this.m_missing = new boolean[instances.numInstances()][this.m_selectedAttribs.length + 1];
    this.m_type = new int[2];
    this.jitterVals = new int[instances.numInstances()][2];
    if (!instances.attribute(this.m_classIndex).isNumeric()) {
      int i;
      for (i = this.m_colorList.size(); i < instances.attribute(this.m_classIndex).numValues() + 1; i++) {
        Color color = m_defaultColors[i % 10];
        int j = i / 10;
        j *= 2;
        for (byte b = 0; b < j; b++)
          color = color.darker(); 
        this.m_colorList.addElement(color);
      } 
      for (i = 0; i < instances.numInstances(); i++) {
        if (instances.instance(i).isMissing(this.m_classIndex)) {
          this.m_pointColors[i] = m_defaultColors.length - 1;
        } else {
          this.m_pointColors[i] = (int)instances.instance(i).value(this.m_classIndex);
        } 
        this.jitterVals[i][0] = this.rnd.nextInt(this.m_jitter.getValue() + 1) - this.m_jitter.getValue() / 2;
        this.jitterVals[i][1] = this.rnd.nextInt(this.m_jitter.getValue() + 1) - this.m_jitter.getValue() / 2;
      } 
    } else {
      byte b = 0;
      while (true) {
        if (b < instances.numInstances())
          if (!instances.instance(b).isMissing(this.m_classIndex)) {
            d1 = d2 = instances.instance(b).value(this.m_classIndex);
          } else {
            b++;
            continue;
          }  
        b = 1;
        break;
      } 
      while (b < instances.numInstances()) {
        if (!instances.instance(b).isMissing(this.m_classIndex)) {
          if (d1 > instances.instance(b).value(this.m_classIndex))
            d1 = instances.instance(b).value(this.m_classIndex); 
          if (d2 < instances.instance(b).value(this.m_classIndex))
            d2 = instances.instance(b).value(this.m_classIndex); 
        } 
        b++;
      } 
      for (b = 0; b < instances.numInstances(); b++) {
        double d = (instances.instance(b).value(this.m_classIndex) - d1) / (d2 - d1);
        d = d * 240.0D + 15.0D;
        this.m_pointColors[b] = (int)d;
        this.jitterVals[b][0] = this.rnd.nextInt(this.m_jitter.getValue() + 1) - this.m_jitter.getValue() / 2;
        this.jitterVals[b][1] = this.rnd.nextInt(this.m_jitter.getValue() + 1) - this.m_jitter.getValue() / 2;
      } 
    } 
    double[] arrayOfDouble1 = new double[this.m_selectedAttribs.length];
    double d3 = 0.0D;
    double[] arrayOfDouble2 = new double[this.m_selectedAttribs.length];
    double d4 = this.m_plotSize.getValue();
    double d5 = 0.0D;
    double d6 = 0.0D;
    byte b1;
    for (b1 = 0; b1 < this.m_selectedAttribs.length; b1++) {
      byte b = 0;
      while (true) {
        if (b < instances.numInstances()) {
          arrayOfDouble1[b1] = d3 = 0.0D;
          if (!instances.instance(b).isMissing(this.m_selectedAttribs[b1])) {
            arrayOfDouble1[b1] = d3 = instances.instance(b).value(this.m_selectedAttribs[b1]);
          } else {
            b++;
            continue;
          } 
        } 
        b = b;
        break;
      } 
      while (b < instances.numInstances()) {
        if (!instances.instance(b).isMissing(this.m_selectedAttribs[b1])) {
          if (instances.instance(b).value(this.m_selectedAttribs[b1]) < arrayOfDouble1[b1])
            arrayOfDouble1[b1] = instances.instance(b).value(this.m_selectedAttribs[b1]); 
          if (instances.instance(b).value(this.m_selectedAttribs[b1]) > d3)
            d3 = instances.instance(b).value(this.m_selectedAttribs[b1]); 
        } 
        b++;
      } 
      arrayOfDouble2[b1] = d4 / (d3 - arrayOfDouble1[b1]);
    } 
    b1 = 0;
    byte b2;
    for (b2 = 0; b2 < this.m_selectedAttribs.length; b2++) {
      if (instances.attribute(this.m_selectedAttribs[b2]).isNominal() || instances.attribute(this.m_selectedAttribs[b2]).isString()) {
        d5 = d4 / instances.attribute(this.m_selectedAttribs[b2]).numValues();
        d6 = d5 / 2.0D;
        for (byte b = 0; b < instances.numInstances(); b++) {
          this.m_points[b][b2] = (int)Math.round(d6 + d5 * instances.instance(b).value(this.m_selectedAttribs[b2]));
          if (instances.instance(b).isMissing(this.m_selectedAttribs[b2])) {
            this.m_missing[b][b2] = true;
            if (this.m_selectedAttribs[b2] == this.m_classIndex) {
              this.m_missing[b][(this.m_missing[0]).length - 1] = true;
              b1 = 1;
            } 
          } 
        } 
      } else {
        for (byte b = 0; b < instances.numInstances(); b++) {
          this.m_points[b][b2] = (int)Math.round((instances.instance(b).value(this.m_selectedAttribs[b2]) - arrayOfDouble1[b2]) * arrayOfDouble2[b2]);
          if (instances.instance(b).isMissing(this.m_selectedAttribs[b2])) {
            this.m_missing[b][b2] = true;
            if (this.m_selectedAttribs[b2] == this.m_classIndex) {
              this.m_missing[b][(this.m_missing[0]).length - 1] = true;
              b1 = 1;
            } 
          } 
        } 
      } 
    } 
    if (instances.attribute(this.m_classIndex).isNominal() || instances.attribute(this.m_classIndex).isString()) {
      this.m_type[0] = 1;
      this.m_type[1] = instances.attribute(this.m_classIndex).numValues();
    } else {
      this.m_type[1] = 0;
      this.m_type[0] = 0;
    } 
    if (b1 == 0)
      for (b2 = 0; b2 < instances.numInstances(); b2++) {
        if (instances.instance(b2).isMissing(this.m_classIndex))
          this.m_missing[b2][(this.m_missing[0]).length - 1] = true; 
      }  
    this.m_cp.setColours(this.m_colorList);
  }
  
  public void setupAttribLists() {
    String[] arrayOfString = new String[this.m_data.numAttributes()];
    this.m_classAttrib.removeAllItems();
    for (byte b = 0; b < arrayOfString.length; b++) {
      String str;
      switch (this.m_data.attribute(b).type()) {
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
      arrayOfString[b] = new String("Colour: " + this.m_data.attribute(b).name() + " " + str);
      this.m_classAttrib.addItem(arrayOfString[b]);
    } 
    this.m_classAttrib.setSelectedIndex(arrayOfString.length - 1);
    this.m_attribList.setListData(arrayOfString);
    this.m_attribList.setSelectionInterval(0, arrayOfString.length - 1);
  }
  
  public void setPercent() {
    if (this.m_data.numInstances() > 700) {
      double d = 500.0D / this.m_data.numInstances() * 100.0D;
      d *= 100.0D;
      d = Math.round(d);
      d /= 100.0D;
      this.m_resamplePercent.setText("" + d);
    } else {
      this.m_resamplePercent.setText("100");
    } 
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_data = paramInstances;
    setPercent();
    setupAttribLists();
    this.m_rseed.setText("1");
    initInternalFields();
    this.m_cp.setInstances(this.m_data);
    this.m_cp.setCindex(this.m_classIndex);
    this.m_updateBt.doClick();
  }
  
  public static void main(String[] paramArrayOfString) {
    JFrame jFrame = new JFrame("Weka Explorer: MatrixPanel");
    JButton jButton = new JButton("Set Instances");
    Instances instances = null;
    try {
      if (paramArrayOfString.length == 1) {
        instances = new Instances(new BufferedReader(new FileReader(paramArrayOfString[0])));
      } else {
        System.out.println("Usage: MatrixPanel <arff file>");
        System.exit(-1);
      } 
    } catch (IOException iOException) {
      iOException.printStackTrace();
      System.exit(-1);
    } 
    MatrixPanel matrixPanel = new MatrixPanel();
    matrixPanel.setInstances(instances);
    jButton.addActionListener(new ActionListener(jFrame, matrixPanel) {
          private final JFrame val$jf;
          
          private final MatrixPanel val$mp;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            JFileChooser jFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
            ExtensionFileFilter extensionFileFilter = new ExtensionFileFilter("arff", "Arff data files");
            jFileChooser.setFileFilter((FileFilter)extensionFileFilter);
            int i = jFileChooser.showOpenDialog(this.val$jf);
            if (i == 0)
              try {
                System.out.println("You chose to open this file: " + jFileChooser.getSelectedFile().getName());
                Instances instances = new Instances(new FileReader(jFileChooser.getSelectedFile().getAbsolutePath()));
                this.val$mp.setInstances(instances);
              } catch (Exception exception) {
                exception.printStackTrace();
              }  
          }
        });
    jFrame.getContentPane().setLayout(new BorderLayout());
    jFrame.getContentPane().add(matrixPanel, "Center");
    jFrame.getContentPane().add(jButton, "South");
    jFrame.getContentPane().setFont(new Font("SansSerif", 0, 11));
    jFrame.setDefaultCloseOperation(3);
    jFrame.setSize(800, 600);
    jFrame.setVisible(true);
    jFrame.repaint();
  }
  
  private class Plot extends JPanel implements MouseMotionListener, MouseListener {
    int extpad;
    
    int intpad;
    
    int cellSize;
    
    int cellRange;
    
    int lastx;
    
    int lasty;
    
    int jitter;
    
    Rectangle r;
    
    FontMetrics fm;
    
    int lastxpos;
    
    int lastypos;
    
    JPanel jPlColHeader;
    
    JPanel jPlRowHeader;
    
    private final MatrixPanel this$0;
    
    public Plot(MatrixPanel this$0) {
      this.this$0 = this$0;
      this.extpad = 3;
      this.intpad = 4;
      this.cellSize = 100;
      this.cellRange = 100;
      this.lastx = 0;
      this.lasty = 0;
      this.jitter = 0;
      setToolTipText("blah");
      addMouseMotionListener(this);
      addMouseListener(this);
      initialize();
    }
    
    public void initialize() {
      this.lastxpos = this.lastypos = 0;
      this.cellRange = this.cellSize;
      this.cellSize = this.cellRange + 2 * this.intpad;
      this.jPlColHeader = (JPanel)new Object(this);
      this.jPlRowHeader = (JPanel)new Object(this);
      this.jPlColHeader.setFont(this.this$0.f);
      this.jPlRowHeader.setFont(this.this$0.f);
      setFont(this.this$0.f);
    }
    
    public JPanel getRowHeader() {
      return this.jPlRowHeader;
    }
    
    public JPanel getColHeader() {
      return this.jPlColHeader;
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {
      Graphics graphics = getGraphics();
      int i = this.extpad;
      int j = this.extpad;
      for (int k = this.this$0.m_selectedAttribs.length - 1; k >= 0; k--) {
        for (byte b = 0; b < this.this$0.m_selectedAttribs.length; b++) {
          if (param1MouseEvent.getX() >= i && param1MouseEvent.getX() <= i + this.cellSize + this.extpad && param1MouseEvent.getY() >= j && param1MouseEvent.getY() <= j + this.cellSize + this.extpad) {
            if (i != this.lastxpos || j != this.lastypos) {
              graphics.setColor(Color.red);
              graphics.drawRect(i - 1, j - 1, this.cellSize + 1, this.cellSize + 1);
              if (this.lastxpos != 0 && this.lastypos != 0) {
                graphics.setColor(getBackground().darker());
                graphics.drawRect(this.lastxpos - 1, this.lastypos - 1, this.cellSize + 1, this.cellSize + 1);
              } 
              this.lastxpos = i;
              this.lastypos = j;
            } 
            return;
          } 
          i += this.cellSize + this.extpad;
        } 
        i = this.extpad;
        j += this.cellSize + this.extpad;
      } 
      if (this.lastxpos != 0 && this.lastypos != 0) {
        graphics.setColor(getBackground().darker());
        graphics.drawRect(this.lastxpos - 1, this.lastypos - 1, this.cellSize + 1, this.cellSize + 1);
      } 
      this.lastxpos = this.lastypos = 0;
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {}
    
    public void mouseClicked(MouseEvent param1MouseEvent) {
      byte b = 0;
      int i = 0;
      boolean bool = false;
      int j = this.extpad;
      int k = this.extpad;
      for (i = this.this$0.m_selectedAttribs.length - 1; i >= 0; i--) {
        for (b = 0; b < this.this$0.m_selectedAttribs.length; b++) {
          if (param1MouseEvent.getX() >= j && param1MouseEvent.getX() <= j + this.cellSize + this.extpad && param1MouseEvent.getY() >= k && param1MouseEvent.getY() <= k + this.cellSize + this.extpad) {
            bool = true;
            break;
          } 
          j += this.cellSize + this.extpad;
        } 
        if (bool == true)
          break; 
        j = this.extpad;
        k += this.cellSize + this.extpad;
      } 
      if (!bool)
        return; 
      JFrame jFrame = new JFrame("Weka Explorer: Visualizing " + this.this$0.m_data.relationName());
      VisualizePanel visualizePanel = new VisualizePanel();
      try {
        PlotData2D plotData2D = new PlotData2D(this.this$0.m_data);
        plotData2D.setPlotName("Master Plot");
        visualizePanel.setMasterPlot(plotData2D);
        visualizePanel.setXIndex(this.this$0.m_selectedAttribs[b]);
        visualizePanel.setYIndex(this.this$0.m_selectedAttribs[i]);
        visualizePanel.m_ColourCombo.setSelectedIndex(this.this$0.m_classIndex);
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      jFrame.getContentPane().add(visualizePanel);
      jFrame.setSize(800, 600);
      jFrame.setVisible(true);
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {}
    
    public void mouseReleased(MouseEvent param1MouseEvent) {}
    
    public void setJitter(int param1Int) {
      this.jitter = param1Int;
    }
    
    public void setCellSize(int param1Int) {
      this.cellSize = param1Int;
      initialize();
    }
    
    public String getToolTipText(MouseEvent param1MouseEvent) {
      int i = this.extpad;
      int j = this.extpad;
      for (int k = this.this$0.m_selectedAttribs.length - 1; k >= 0; k--) {
        for (byte b = 0; b < this.this$0.m_selectedAttribs.length; b++) {
          if (param1MouseEvent.getX() >= i && param1MouseEvent.getX() <= i + this.cellSize + this.extpad && param1MouseEvent.getY() >= j && param1MouseEvent.getY() <= j + this.cellSize + this.extpad)
            return "X: " + this.this$0.m_data.attribute(this.this$0.m_selectedAttribs[b]).name() + " Y: " + this.this$0.m_data.attribute(this.this$0.m_selectedAttribs[k]).name() + " (click to enlarge)"; 
          i += this.cellSize + this.extpad;
        } 
        i = this.extpad;
        j += this.cellSize + this.extpad;
      } 
      return "Matrix Panel";
    }
    
    public void paintGraph(Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Graphics.setColor(getBackground().darker().darker());
      param1Graphics.drawRect(param1Int3 - 1, param1Int4 - 1, this.cellSize + 1, this.cellSize + 1);
      param1Graphics.setColor(Color.white);
      param1Graphics.fillRect(param1Int3, param1Int4, this.cellSize, this.cellSize);
      for (byte b = 0; b < this.this$0.m_points.length; b++) {
        if (!this.this$0.m_missing[b][param1Int2] && !this.this$0.m_missing[b][param1Int1]) {
          int i;
          int j;
          if (this.this$0.m_type[0] == 0) {
            if (this.this$0.m_missing[b][(this.this$0.m_missing[0]).length - 1]) {
              param1Graphics.setColor(MatrixPanel.m_defaultColors[MatrixPanel.m_defaultColors.length - 1]);
            } else {
              param1Graphics.setColor(new Color(this.this$0.m_pointColors[b], 150, 255 - this.this$0.m_pointColors[b]));
            } 
          } else {
            param1Graphics.setColor((Color)this.this$0.m_colorList.elementAt(this.this$0.m_pointColors[b]));
          } 
          if (this.this$0.m_points[b][param1Int1] + this.this$0.jitterVals[b][0] < 0 || this.this$0.m_points[b][param1Int1] + this.this$0.jitterVals[b][0] > this.cellRange) {
            if (this.cellRange - this.this$0.m_points[b][param1Int2] + this.this$0.jitterVals[b][1] < 0 || this.cellRange - this.this$0.m_points[b][param1Int2] + this.this$0.jitterVals[b][1] > this.cellRange) {
              i = this.intpad + this.this$0.m_points[b][param1Int1];
              j = this.intpad + this.cellRange - this.this$0.m_points[b][param1Int2];
            } else {
              i = this.intpad + this.this$0.m_points[b][param1Int1];
              j = this.intpad + this.cellRange - this.this$0.m_points[b][param1Int2] + this.this$0.jitterVals[b][1];
            } 
          } else if (this.cellRange - this.this$0.m_points[b][param1Int2] + this.this$0.jitterVals[b][1] < 0 || this.cellRange - this.this$0.m_points[b][param1Int2] + this.this$0.jitterVals[b][1] > this.cellRange) {
            i = this.intpad + this.this$0.m_points[b][param1Int1] + this.this$0.jitterVals[b][0];
            j = this.intpad + this.cellRange - this.this$0.m_points[b][param1Int2];
          } else {
            i = this.intpad + this.this$0.m_points[b][param1Int1] + this.this$0.jitterVals[b][0];
            j = this.intpad + this.cellRange - this.this$0.m_points[b][param1Int2] + this.this$0.jitterVals[b][1];
          } 
          if (this.this$0.datapointSize == 1) {
            param1Graphics.drawLine(i + param1Int3, j + param1Int4, i + param1Int3, j + param1Int4);
          } else {
            param1Graphics.drawOval(i + param1Int3 - this.this$0.datapointSize / 2, j + param1Int4 - this.this$0.datapointSize / 2, this.this$0.datapointSize, this.this$0.datapointSize);
          } 
        } 
      } 
      param1Graphics.setColor(this.this$0.fontColor);
    }
    
    public void paintME(Graphics param1Graphics) {
      this.r = param1Graphics.getClipBounds();
      param1Graphics.setColor(getBackground());
      param1Graphics.fillRect(this.r.x, this.r.y, this.r.width, this.r.height);
      param1Graphics.setColor(this.this$0.fontColor);
      int i = 0;
      int j = 0;
      boolean bool = false;
      i = this.extpad;
      j = this.extpad;
      for (int k = this.this$0.m_selectedAttribs.length - 1; k >= 0; k--) {
        if (j + this.cellSize < this.r.y) {
          j += this.cellSize + this.extpad;
        } else {
          if (j > this.r.y + this.r.height)
            break; 
          for (byte b = 0; b < this.this$0.m_selectedAttribs.length; b++) {
            if (i + this.cellSize < this.r.x) {
              i += this.cellSize + this.extpad;
            } else {
              if (i > this.r.x + this.r.width)
                break; 
              paintGraph(param1Graphics, b, k, i, j);
              i += this.cellSize + this.extpad;
            } 
          } 
          i = this.extpad;
          j += this.cellSize + this.extpad;
        } 
      } 
    }
    
    public void paintComponent(Graphics param1Graphics) {
      paintME(param1Graphics);
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\MatrixPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */