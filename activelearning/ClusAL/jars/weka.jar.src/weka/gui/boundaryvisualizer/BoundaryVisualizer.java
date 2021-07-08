package weka.gui.boundaryvisualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.visualize.ClassPanel;

public class BoundaryVisualizer extends JPanel {
  private Instances m_trainingInstances;
  
  private Classifier m_classifier;
  
  protected int m_plotAreaWidth = 512;
  
  protected int m_plotAreaHeight = 384;
  
  protected BoundaryPanel m_boundaryPanel;
  
  protected JComboBox m_classAttBox = new JComboBox();
  
  protected JComboBox m_xAttBox = new JComboBox();
  
  protected JComboBox m_yAttBox = new JComboBox();
  
  protected Dimension COMBO_SIZE = new Dimension(this.m_plotAreaWidth / 2, (this.m_classAttBox.getPreferredSize()).height);
  
  protected JButton m_startBut = new JButton("Start");
  
  protected JCheckBox m_plotTrainingData = new JCheckBox("Plot training data");
  
  protected JPanel m_controlPanel;
  
  protected ClassPanel m_classPanel = new ClassPanel();
  
  private AxisPanel m_xAxisPanel;
  
  private AxisPanel m_yAxisPanel;
  
  private double m_maxX;
  
  private double m_maxY;
  
  private double m_minX;
  
  private double m_minY;
  
  private int m_xIndex;
  
  private int m_yIndex;
  
  private KDDataGenerator m_dataGenerator;
  
  private int m_numberOfSamplesFromEachRegion;
  
  private int m_generatorSamplesBase;
  
  private int m_kernelBandwidth;
  
  private JTextField m_regionSamplesText = new JTextField("0");
  
  private JTextField m_generatorSamplesText = new JTextField("0");
  
  private JTextField m_kernelBandwidthText = new JTextField("3  ");
  
  public BoundaryVisualizer() {
    setLayout(new BorderLayout());
    this.m_classAttBox.setMinimumSize(this.COMBO_SIZE);
    this.m_classAttBox.setPreferredSize(this.COMBO_SIZE);
    this.m_classAttBox.setMaximumSize(this.COMBO_SIZE);
    this.m_classAttBox.addActionListener(new ActionListener(this) {
          private final BoundaryVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_classPanel.setCindex(this.this$0.m_classAttBox.getSelectedIndex());
          }
        });
    this.m_xAttBox.setMinimumSize(this.COMBO_SIZE);
    this.m_xAttBox.setPreferredSize(this.COMBO_SIZE);
    this.m_xAttBox.setMaximumSize(this.COMBO_SIZE);
    this.m_yAttBox.setMinimumSize(this.COMBO_SIZE);
    this.m_yAttBox.setPreferredSize(this.COMBO_SIZE);
    this.m_yAttBox.setMaximumSize(this.COMBO_SIZE);
    this.m_classPanel.setMinimumSize(new Dimension((int)this.COMBO_SIZE.getWidth() * 2, (int)this.COMBO_SIZE.getHeight() * 2));
    this.m_classPanel.setPreferredSize(new Dimension((int)this.COMBO_SIZE.getWidth() * 2, (int)this.COMBO_SIZE.getHeight() * 2));
    this.m_controlPanel = new JPanel();
    this.m_controlPanel.setLayout(new BorderLayout());
    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createTitledBorder("Class Attribute"));
    jPanel1.add(this.m_classAttBox);
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new GridLayout(2, 1));
    jPanel2.setBorder(BorderFactory.createTitledBorder("Visualization Attributes"));
    jPanel2.add(this.m_xAttBox);
    jPanel2.add(this.m_yAttBox);
    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new BorderLayout());
    jPanel3.add(jPanel1, "North");
    jPanel3.add(jPanel2, "Center");
    JPanel jPanel4 = new JPanel();
    jPanel4.setBorder(BorderFactory.createTitledBorder("Sampling control"));
    jPanel4.setLayout(new GridLayout(3, 1));
    JPanel jPanel5 = new JPanel();
    jPanel5.setLayout(new BorderLayout());
    JPanel jPanel6 = new JPanel();
    jPanel6.setLayout(new BorderLayout());
    jPanel6.add(new JLabel(" Base for sampling (r)"), "Center");
    jPanel6.add(this.m_generatorSamplesText, "West");
    jPanel4.add(jPanel6);
    JPanel jPanel7 = new JPanel();
    jPanel7.setLayout(new BorderLayout());
    jPanel7.add(new JLabel(" Num. locations per pixel"), "Center");
    jPanel7.add(this.m_regionSamplesText, "West");
    jPanel4.add(jPanel7);
    JPanel jPanel8 = new JPanel();
    jPanel8.setLayout(new BorderLayout());
    jPanel8.add(new JLabel(" Kernel bandwidth (k)"), "Center");
    jPanel8.add(this.m_kernelBandwidthText, "West");
    jPanel4.add(jPanel8);
    jPanel5.add(jPanel4, "North");
    JPanel jPanel9 = new JPanel();
    jPanel9.setBorder(BorderFactory.createTitledBorder("Plotting"));
    jPanel9.setLayout(new BorderLayout());
    jPanel9.add(this.m_startBut, "Center");
    jPanel9.add(this.m_plotTrainingData, "West");
    jPanel5.add(jPanel9, "South");
    this.m_controlPanel.add(jPanel3, "West");
    this.m_controlPanel.add(jPanel5, "Center");
    JPanel jPanel10 = new JPanel();
    jPanel10.setBorder(BorderFactory.createTitledBorder("Class color"));
    jPanel10.add((Component)this.m_classPanel);
    this.m_controlPanel.add(jPanel10, "South");
    add(this.m_controlPanel, "North");
    this.m_boundaryPanel = new BoundaryPanel(this.m_plotAreaWidth, this.m_plotAreaHeight);
    this.m_numberOfSamplesFromEachRegion = this.m_boundaryPanel.getNumSamplesPerRegion();
    this.m_regionSamplesText.setText("" + this.m_numberOfSamplesFromEachRegion + "  ");
    this.m_generatorSamplesBase = (int)this.m_boundaryPanel.getGeneratorSamplesBase();
    this.m_generatorSamplesText.setText("" + this.m_generatorSamplesBase + "  ");
    this.m_dataGenerator = new KDDataGenerator();
    this.m_kernelBandwidth = this.m_dataGenerator.getKernelBandwidth();
    this.m_kernelBandwidthText.setText("" + this.m_kernelBandwidth + "  ");
    this.m_boundaryPanel.setDataGenerator(this.m_dataGenerator);
    add(this.m_boundaryPanel, "Center");
    this.m_xAxisPanel = new AxisPanel(this, false);
    add(this.m_xAxisPanel, "South");
    this.m_yAxisPanel = new AxisPanel(this, true);
    add(this.m_yAxisPanel, "West");
    this.m_startBut.setEnabled(false);
    this.m_startBut.addActionListener(new ActionListener(this) {
          private final BoundaryVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_startBut.getText().equals("Start")) {
              if (this.this$0.m_trainingInstances != null && this.this$0.m_classifier != null)
                try {
                  int i = this.this$0.m_numberOfSamplesFromEachRegion;
                  try {
                    i = Integer.parseInt(this.this$0.m_regionSamplesText.getText().trim());
                  } catch (Exception exception) {
                    this.this$0.m_regionSamplesText.setText("" + i);
                  } 
                  this.this$0.m_numberOfSamplesFromEachRegion = i;
                  this.this$0.m_boundaryPanel.setNumSamplesPerRegion(i);
                  i = this.this$0.m_generatorSamplesBase;
                  try {
                    i = Integer.parseInt(this.this$0.m_generatorSamplesText.getText().trim());
                  } catch (Exception exception) {
                    this.this$0.m_generatorSamplesText.setText("" + i);
                  } 
                  this.this$0.m_generatorSamplesBase = i;
                  this.this$0.m_boundaryPanel.setGeneratorSamplesBase(i);
                  i = this.this$0.m_kernelBandwidth;
                  try {
                    i = Integer.parseInt(this.this$0.m_kernelBandwidthText.getText().trim());
                  } catch (Exception exception) {
                    this.this$0.m_kernelBandwidthText.setText("" + i);
                  } 
                  this.this$0.m_kernelBandwidth = i;
                  this.this$0.m_dataGenerator.setKernelBandwidth(i);
                  this.this$0.m_trainingInstances.setClassIndex(this.this$0.m_classAttBox.getSelectedIndex());
                  this.this$0.m_boundaryPanel.setClassifier(this.this$0.m_classifier);
                  this.this$0.m_boundaryPanel.setTrainingData(this.this$0.m_trainingInstances);
                  this.this$0.m_boundaryPanel.setXAttribute(this.this$0.m_xIndex);
                  this.this$0.m_boundaryPanel.setYAttribute(this.this$0.m_yIndex);
                  this.this$0.m_boundaryPanel.setPlotTrainingData(this.this$0.m_plotTrainingData.isSelected());
                  this.this$0.m_boundaryPanel.start();
                  this.this$0.m_startBut.setText("Stop");
                  this.this$0.setControlEnabledStatus(false);
                } catch (Exception exception) {
                  exception.printStackTrace();
                }  
            } else {
              this.this$0.m_boundaryPanel.stopPlotting();
              this.this$0.m_startBut.setText("Start");
              this.this$0.setControlEnabledStatus(true);
            } 
          }
        });
    this.m_boundaryPanel.addActionListener(new ActionListener(this) {
          private final BoundaryVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_startBut.setText("Start");
            this.this$0.setControlEnabledStatus(true);
          }
        });
    this.m_classPanel.addActionListener(new ActionListener(this) {
          private final BoundaryVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            try {
              FastVector fastVector = this.this$0.m_boundaryPanel.getColors();
              FileOutputStream fileOutputStream = new FileOutputStream("colors.ser");
              ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
              objectOutputStream.writeObject(fastVector);
              objectOutputStream.flush();
              objectOutputStream.close();
            } catch (Exception exception) {}
            this.this$0.m_boundaryPanel.replot();
          }
        });
  }
  
  private void setControlEnabledStatus(boolean paramBoolean) {
    this.m_classAttBox.setEnabled(paramBoolean);
    this.m_xAttBox.setEnabled(paramBoolean);
    this.m_yAttBox.setEnabled(paramBoolean);
    this.m_regionSamplesText.setEnabled(paramBoolean);
    this.m_generatorSamplesText.setEnabled(paramBoolean);
    this.m_kernelBandwidthText.setEnabled(paramBoolean);
    this.m_plotTrainingData.setEnabled(paramBoolean);
  }
  
  public void setClassifier(Classifier paramClassifier) throws Exception {
    this.m_classifier = paramClassifier;
  }
  
  private void computeBounds() {
    String str1 = (String)this.m_xAttBox.getSelectedItem();
    if (str1 == null)
      return; 
    str1 = Utils.removeSubstring(str1, "X: ");
    str1 = Utils.removeSubstring(str1, " (Num)");
    String str2 = (String)this.m_yAttBox.getSelectedItem();
    str2 = Utils.removeSubstring(str2, "Y: ");
    str2 = Utils.removeSubstring(str2, " (Num)");
    this.m_xIndex = -1;
    this.m_yIndex = -1;
    byte b;
    for (b = 0; b < this.m_trainingInstances.numAttributes(); b++) {
      if (this.m_trainingInstances.attribute(b).name().equals(str1))
        this.m_xIndex = b; 
      if (this.m_trainingInstances.attribute(b).name().equals(str2))
        this.m_yIndex = b; 
    } 
    if (this.m_xIndex != -1 && this.m_yIndex != -1) {
      this.m_minX = Double.MAX_VALUE;
      this.m_minY = Double.MAX_VALUE;
      this.m_maxX = Double.MIN_VALUE;
      this.m_maxY = Double.MIN_VALUE;
      for (b = 0; b < this.m_trainingInstances.numInstances(); b++) {
        Instance instance = this.m_trainingInstances.instance(b);
        if (!instance.isMissing(this.m_xIndex)) {
          double d = instance.value(this.m_xIndex);
          if (d < this.m_minX)
            this.m_minX = d; 
          if (d > this.m_maxX)
            this.m_maxX = d; 
        } 
        if (!instance.isMissing(this.m_yIndex)) {
          double d = instance.value(this.m_yIndex);
          if (d < this.m_minY)
            this.m_minY = d; 
          if (d > this.m_maxY)
            this.m_maxY = d; 
        } 
      } 
    } 
  }
  
  public Instances getInstances() {
    return this.m_trainingInstances;
  }
  
  public void setInstances(Instances paramInstances) throws Exception {
    if (paramInstances.numAttributes() < 3)
      throw new Exception("Not enough attributes in the data to visualize!"); 
    this.m_trainingInstances = paramInstances;
    this.m_classPanel.setInstances(this.m_trainingInstances);
    String[] arrayOfString = new String[this.m_trainingInstances.numAttributes()];
    Vector vector1 = new Vector();
    Vector vector2 = new Vector();
    for (byte b = 0; b < this.m_trainingInstances.numAttributes(); b++) {
      arrayOfString[b] = this.m_trainingInstances.attribute(b).name();
      if (this.m_trainingInstances.attribute(b).isNominal()) {
        arrayOfString[b] = arrayOfString[b] + " (Nom)";
      } else {
        arrayOfString[b] = arrayOfString[b] + " (Num)";
      } 
      if (this.m_trainingInstances.attribute(b).isNumeric()) {
        vector1.addElement("X: " + arrayOfString[b]);
        vector2.addElement("Y: " + arrayOfString[b]);
      } 
    } 
    this.m_classAttBox.setModel(new DefaultComboBoxModel(arrayOfString));
    this.m_xAttBox.setModel(new DefaultComboBoxModel(vector1));
    this.m_yAttBox.setModel(new DefaultComboBoxModel(vector2));
    if (vector1.size() > 1)
      this.m_yAttBox.setSelectedIndex(1); 
    this.m_classAttBox.addActionListener(new ActionListener(this) {
          private final BoundaryVisualizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.configureForClassAttribute();
          }
        });
    this.m_xAttBox.addItemListener(new ItemListener(this, vector1) {
          private final Vector val$xAttNames;
          
          private final BoundaryVisualizer this$0;
          
          public void itemStateChanged(ItemEvent param1ItemEvent) {
            if (param1ItemEvent.getStateChange() == 1) {
              if (this.val$xAttNames.size() > 1 && this.this$0.m_xAttBox.getSelectedIndex() == this.this$0.m_yAttBox.getSelectedIndex())
                this.this$0.m_xAttBox.setSelectedIndex((this.this$0.m_xAttBox.getSelectedIndex() + 1) % this.val$xAttNames.size()); 
              this.this$0.computeBounds();
              this.this$0.repaint();
            } 
          }
        });
    this.m_yAttBox.addItemListener(new ItemListener(this, vector1) {
          private final Vector val$xAttNames;
          
          private final BoundaryVisualizer this$0;
          
          public void itemStateChanged(ItemEvent param1ItemEvent) {
            if (param1ItemEvent.getStateChange() == 1) {
              if (this.val$xAttNames.size() > 1 && this.this$0.m_yAttBox.getSelectedIndex() == this.this$0.m_xAttBox.getSelectedIndex())
                this.this$0.m_yAttBox.setSelectedIndex((this.this$0.m_yAttBox.getSelectedIndex() + 1) % this.val$xAttNames.size()); 
              this.this$0.computeBounds();
              this.this$0.repaint();
            } 
          }
        });
    computeBounds();
    revalidate();
    repaint();
  }
  
  private void configureForClassAttribute() {
    int i = this.m_classAttBox.getSelectedIndex();
    if (i >= 0)
      if (!this.m_trainingInstances.attribute(i).isNominal()) {
        this.m_startBut.setEnabled(false);
      } else {
        this.m_startBut.setEnabled(true);
        FastVector fastVector = new FastVector();
        for (byte b = 0; b < this.m_trainingInstances.attribute(i).numValues(); b++) {
          fastVector.addElement(BoundaryPanel.DEFAULT_COLORS[b % BoundaryPanel.DEFAULT_COLORS.length]);
          this.m_classPanel.setColours(fastVector);
          this.m_boundaryPanel.setColors(fastVector);
        } 
      }  
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length < 2) {
        System.err.println("Usage : BoundaryPanel <dataset> <classifier [classifier options]>");
        System.exit(1);
      } 
      JFrame jFrame = new JFrame("Weka classification boundary visualizer");
      jFrame.getContentPane().setLayout(new BorderLayout());
      BoundaryVisualizer boundaryVisualizer = new BoundaryVisualizer();
      jFrame.getContentPane().add(boundaryVisualizer, "Center");
      jFrame.setSize(boundaryVisualizer.getMinimumSize());
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      jFrame.setResizable(false);
      Dimension dimension = jFrame.getSize();
      System.err.println("Loading instances from : " + paramArrayOfString[0]);
      BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
      Instances instances = new Instances(bufferedReader);
      boundaryVisualizer.setInstances(instances);
      String[] arrayOfString = null;
      if (paramArrayOfString.length > 2) {
        arrayOfString = new String[paramArrayOfString.length - 2];
        for (byte b = 2; b < paramArrayOfString.length; b++)
          arrayOfString[b - 2] = paramArrayOfString[b]; 
      } 
      Classifier classifier = Classifier.forName(paramArrayOfString[1], arrayOfString);
      boundaryVisualizer.setClassifier(classifier);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  private class AxisPanel extends JPanel {
    private static final int MAX_PRECISION = 10;
    
    private boolean m_vertical;
    
    private final int PAD = 5;
    
    private FontMetrics m_fontMetrics;
    
    private int m_fontHeight;
    
    private final BoundaryVisualizer this$0;
    
    public AxisPanel(BoundaryVisualizer this$0, boolean param1Boolean) {
      this.this$0 = this$0;
      this.m_vertical = false;
      this.PAD = 5;
      this.m_vertical = param1Boolean;
      setBackground(Color.black);
      String str = getFont().getFamily();
      Font font = new Font(str, 0, 10);
      setFont(font);
    }
    
    public Dimension getPreferredSize() {
      if (this.m_fontMetrics == null) {
        Graphics graphics = getGraphics();
        this.m_fontMetrics = graphics.getFontMetrics();
        this.m_fontHeight = this.m_fontMetrics.getHeight();
      } 
      return !this.m_vertical ? new Dimension((getSize()).width, 7 + this.m_fontHeight) : new Dimension(50, (getSize()).height);
    }
    
    public void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      setBackground(Color.black);
      if (this.m_fontMetrics == null) {
        this.m_fontMetrics = param1Graphics.getFontMetrics();
        this.m_fontHeight = this.m_fontMetrics.getHeight();
      } 
      Dimension dimension1 = getSize();
      Dimension dimension2 = this.this$0.m_boundaryPanel.getSize();
      param1Graphics.setColor(Color.gray);
      int i = this.m_fontMetrics.getAscent();
      if (!this.m_vertical) {
        param1Graphics.drawLine(dimension1.width, 5, dimension1.width - dimension2.width, 5);
        if (this.this$0.getInstances() != null) {
          byte b1 = 1;
          byte b2 = 1;
          int j = (int)Math.abs(this.this$0.m_maxX);
          double d = Math.abs(this.this$0.m_maxX) - j;
          byte b3 = (j > 0) ? (int)(Math.log(j) / Math.log(10.0D)) : 1;
          b1 = (d > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.this$0.m_maxX)) / Math.log(10.0D)) + 2) : 1;
          if (b1 > 10)
            b1 = 1; 
          String str1 = Utils.doubleToString(this.this$0.m_maxX, b3 + 1 + b1, b1);
          j = (int)Math.abs(this.this$0.m_minX);
          d = Math.abs(this.this$0.m_minX) - j;
          b3 = (j > 0) ? (int)(Math.log(j) / Math.log(10.0D)) : 1;
          b2 = (d > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.this$0.m_minX)) / Math.log(10.0D)) + 2) : 1;
          if (b2 > 10)
            b2 = 1; 
          String str2 = Utils.doubleToString(this.this$0.m_minX, b3 + 1 + b2, b2);
          param1Graphics.drawString(str2, dimension1.width - dimension2.width, 5 + i + 2);
          int k = this.m_fontMetrics.stringWidth(str1);
          param1Graphics.drawString(str1, dimension1.width - k, 5 + i + 2);
        } 
      } else {
        param1Graphics.drawLine(dimension1.width - 5, 0, dimension1.width - 5, dimension2.height);
        if (this.this$0.getInstances() != null) {
          byte b1 = 1;
          byte b2 = 1;
          int j = (int)Math.abs(this.this$0.m_maxY);
          double d = Math.abs(this.this$0.m_maxY) - j;
          byte b3 = (j > 0) ? (int)(Math.log(j) / Math.log(10.0D)) : 1;
          b1 = (d > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.this$0.m_maxY)) / Math.log(10.0D)) + 2) : 1;
          if (b1 > 10)
            b1 = 1; 
          String str1 = Utils.doubleToString(this.this$0.m_maxY, b3 + 1 + b1, b1);
          j = (int)Math.abs(this.this$0.m_minY);
          d = Math.abs(this.this$0.m_minY) - j;
          b3 = (j > 0) ? (int)(Math.log(j) / Math.log(10.0D)) : 1;
          b2 = (d > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.this$0.m_minY)) / Math.log(10.0D)) + 2) : 1;
          if (b2 > 10)
            b2 = 1; 
          String str2 = Utils.doubleToString(this.this$0.m_minY, b3 + 1 + b2, b2);
          int k = this.m_fontMetrics.stringWidth(str2);
          param1Graphics.drawString(str2, dimension1.width - 5 - k - 2, dimension2.height);
          k = this.m_fontMetrics.stringWidth(str1);
          param1Graphics.drawString(str1, dimension1.width - 5 - k - 2, i);
        } 
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\boundaryvisualizer\BoundaryVisualizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */