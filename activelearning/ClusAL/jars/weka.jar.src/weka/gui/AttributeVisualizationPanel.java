package weka.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import weka.core.AttributeStats;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.visualize.PrintablePanel;

public class AttributeVisualizationPanel extends PrintablePanel {
  protected Instances m_data;
  
  protected AttributeStats m_as;
  
  protected int m_attribIndex;
  
  protected int m_maxValue;
  
  protected int[] m_histBarCounts;
  
  int[][] m_histBarClassCounts;
  
  protected double m_barRange;
  
  protected int m_classIndex;
  
  private Thread m_hc;
  
  private boolean m_threadRun = false;
  
  protected JComboBox m_colorAttrib;
  
  private FontMetrics m_fm;
  
  private Integer m_locker = new Integer(1);
  
  private FastVector m_colorList = new FastVector();
  
  private static final Color[] m_defaultColors = new Color[] { Color.blue, Color.red, Color.cyan, new Color(75, 123, 130), Color.pink, Color.green, Color.orange, new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 255, 0) };
  
  public AttributeVisualizationPanel() {
    this(false);
  }
  
  public AttributeVisualizationPanel(boolean paramBoolean) {
    setFont(new Font("Default", 0, 9));
    this.m_fm = getFontMetrics(getFont());
    setToolTipText("");
    FlowLayout flowLayout = new FlowLayout(0);
    setLayout(flowLayout);
    addComponentListener(new ComponentAdapter(this) {
          private final AttributeVisualizationPanel this$0;
          
          public void componentResized(ComponentEvent param1ComponentEvent) {
            if (this.this$0.m_data != null)
              this.this$0.calcGraph(); 
          }
        });
    this.m_colorAttrib = new JComboBox();
    this.m_colorAttrib.addItemListener(new ItemListener(this) {
          private final AttributeVisualizationPanel this$0;
          
          public void itemStateChanged(ItemEvent param1ItemEvent) {
            if (param1ItemEvent.getStateChange() == 1) {
              this.this$0.m_classIndex = this.this$0.m_colorAttrib.getSelectedIndex() - 1;
              if (this.this$0.m_as != null)
                this.this$0.setAttribute(this.this$0.m_attribIndex); 
            } 
          }
        });
    if (paramBoolean) {
      add(this.m_colorAttrib);
      validate();
    } 
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_attribIndex = 0;
    this.m_as = null;
    this.m_data = paramInstances;
    if (this.m_colorAttrib != null) {
      this.m_colorAttrib.removeAllItems();
      this.m_colorAttrib.addItem("No class");
      for (byte b = 0; b < this.m_data.numAttributes(); b++)
        this.m_colorAttrib.addItem(new String("Class: " + this.m_data.attribute(b).name() + " " + (this.m_data.attribute(b).isNominal() ? "(Nom)" : ""))); 
      if (this.m_data.classIndex() >= 0) {
        this.m_colorAttrib.setSelectedIndex(this.m_data.classIndex() + 1);
      } else {
        this.m_colorAttrib.setSelectedIndex(this.m_data.numAttributes());
      } 
    } 
    if (this.m_data.classIndex() >= 0) {
      this.m_classIndex = this.m_data.classIndex();
    } else {
      this.m_classIndex = this.m_data.numAttributes() - 1;
    } 
  }
  
  public JComboBox getColorBox() {
    return this.m_colorAttrib;
  }
  
  public int getColoringIndex() {
    return this.m_classIndex;
  }
  
  public void setColoringIndex(int paramInt) {
    this.m_classIndex = paramInt;
    if (this.m_colorAttrib != null) {
      this.m_colorAttrib.setSelectedIndex(paramInt + 1);
    } else {
      setAttribute(this.m_attribIndex);
    } 
  }
  
  public void setAttribute(int paramInt) {
    synchronized (this.m_locker) {
      this.m_threadRun = true;
      this.m_attribIndex = paramInt;
      this.m_as = this.m_data.attributeStats(this.m_attribIndex);
    } 
    calcGraph();
  }
  
  public void calcGraph() {
    synchronized (this.m_locker) {
      this.m_threadRun = true;
      if (this.m_as.nominalCounts != null) {
        this.m_hc = new BarCalc();
        this.m_hc.setPriority(1);
        this.m_hc.start();
      } else if (this.m_as.numericStats != null) {
        this.m_hc = new HistCalc();
        this.m_hc.setPriority(1);
        this.m_hc.start();
      } else {
        this.m_histBarCounts = null;
        this.m_histBarClassCounts = (int[][])null;
        repaint();
        this.m_threadRun = false;
      } 
    } 
  }
  
  public String getToolTipText(MouseEvent paramMouseEvent) {
    if (this.m_as != null && this.m_as.nominalCounts != null) {
      byte b1;
      float f = getWidth() / this.m_as.nominalCounts.length;
      int i = 0;
      int j = 0;
      if (f > 5.0F) {
        b1 = (int)Math.floor((f * 0.8F));
      } else {
        b1 = 1;
      } 
      i += (int)((Math.floor((f * 0.1F)) < 1.0D) ? 1.0D : Math.floor((f * 0.1F)));
      if (getWidth() - this.m_as.nominalCounts.length * b1 + (int)((Math.floor((f * 0.2F)) < 1.0D) ? 1.0D : Math.floor((f * 0.2F))) * this.m_as.nominalCounts.length > 2)
        i += (getWidth() - this.m_as.nominalCounts.length * b1 + (int)((Math.floor((f * 0.2F)) < 1.0D) ? 1.0D : Math.floor((f * 0.2F))) * this.m_as.nominalCounts.length) / 2; 
      for (byte b2 = 0; b2 < this.m_as.nominalCounts.length; b2++) {
        float f1 = (getHeight() - this.m_fm.getHeight()) / this.m_maxValue;
        j = getHeight() - Math.round(this.m_as.nominalCounts[b2] * f1);
        if (paramMouseEvent.getX() >= i && paramMouseEvent.getX() <= i + b1 && paramMouseEvent.getY() >= getHeight() - Math.round(this.m_as.nominalCounts[b2] * f1))
          return this.m_data.attribute(this.m_attribIndex).value(b2) + " [" + this.m_as.nominalCounts[b2] + "]"; 
        i = i + b1 + (int)((Math.floor((f * 0.2F)) < 1.0D) ? 1.0D : Math.floor((f * 0.2F)));
      } 
    } else if (!this.m_threadRun && (this.m_histBarCounts != null || this.m_histBarClassCounts != null)) {
      int i = 0;
      boolean bool = false;
      double d = this.m_as.numericStats.min;
      if (this.m_classIndex >= 0 && this.m_data.attribute(this.m_classIndex).isNominal()) {
        byte b = ((getWidth() - 6) / this.m_histBarClassCounts.length < 1) ? 1 : ((getWidth() - 6) / this.m_histBarClassCounts.length);
        i = 3;
        if (getWidth() - i + this.m_histBarClassCounts.length * b > 5)
          i += (getWidth() - i + this.m_histBarClassCounts.length * b) / 2; 
        float f = (getHeight() - this.m_fm.getHeight()) / this.m_maxValue;
        if (paramMouseEvent.getX() - i >= 0) {
          int j = (int)((paramMouseEvent.getX() - i) / (b + 1.0E-10D));
          if (j == 0) {
            int k = 0;
            for (byte b1 = 0; b1 < (this.m_histBarClassCounts[0]).length; b1++)
              k += this.m_histBarClassCounts[0][b1]; 
            return "<html><center><font face=Dialog size=-1>" + k + "<br>" + "[" + Utils.doubleToString(d + this.m_barRange * j, 3) + ", " + Utils.doubleToString(d + this.m_barRange * (j + 1), 3) + "]" + "</font></center></html>";
          } 
          if (j < this.m_histBarClassCounts.length) {
            int k = 0;
            for (byte b1 = 0; b1 < (this.m_histBarClassCounts[j]).length; b1++)
              k += this.m_histBarClassCounts[j][b1]; 
            return "<html><center><font face=Dialog size=-1>" + k + "<br>(" + Utils.doubleToString(d + this.m_barRange * j, 3) + ", " + Utils.doubleToString(d + this.m_barRange * (j + 1), 3) + "]</font></center></html>";
          } 
        } 
      } else {
        byte b = ((getWidth() - 6) / this.m_histBarCounts.length < 1) ? 1 : ((getWidth() - 6) / this.m_histBarCounts.length);
        i = 3;
        if (getWidth() - i + this.m_histBarCounts.length * b > 5)
          i += (getWidth() - i + this.m_histBarCounts.length * b) / 2; 
        float f = (getHeight() - this.m_fm.getHeight()) / this.m_maxValue;
        if (paramMouseEvent.getX() - i >= 0) {
          int j = (int)((paramMouseEvent.getX() - i) / (b + 1.0E-10D));
          if (j == 0)
            return "<html><center><font face=Dialog size=-1>" + this.m_histBarCounts[0] + "<br>" + "[" + Utils.doubleToString(d + this.m_barRange * j, 3) + ", " + Utils.doubleToString(d + this.m_barRange * (j + 1), 3) + "]" + "</font></center></html>"; 
          if (j < this.m_histBarCounts.length)
            return "<html><center><font face=Dialog size=-1>" + this.m_histBarCounts[j] + "<br>" + "(" + Utils.doubleToString(d + this.m_barRange * j, 3) + ", " + Utils.doubleToString(d + this.m_barRange * (j + 1), 3) + "]" + "</font></center></html>"; 
        } 
      } 
    } 
    return "Click left mouse button while holding <ctrl> and <alt> to display a save dialog.";
  }
  
  public void paintComponent(Graphics paramGraphics) {
    paramGraphics.clearRect(0, 0, getWidth(), getHeight());
    if (this.m_as != null)
      if (!this.m_threadRun) {
        int i = 0;
        if (this.m_colorAttrib != null)
          i = this.m_colorAttrib.getHeight() + (this.m_colorAttrib.getLocation()).y; 
        if (this.m_as.nominalCounts != null && (this.m_histBarClassCounts != null || this.m_histBarCounts != null)) {
          int j = 0;
          int k = 0;
          if (this.m_classIndex >= 0 && this.m_data.attribute(this.m_classIndex).isNominal()) {
            byte b1;
            float f = getWidth() / this.m_histBarClassCounts.length;
            if (f > 5.0F) {
              b1 = (int)Math.floor((f * 0.8F));
            } else {
              b1 = 1;
            } 
            j += (int)((Math.floor((f * 0.1F)) < 1.0D) ? 1.0D : Math.floor((f * 0.1F)));
            if (getWidth() - this.m_histBarClassCounts.length * b1 + (int)((Math.floor((f * 0.2F)) < 1.0D) ? 1.0D : Math.floor((f * 0.2F))) * this.m_histBarClassCounts.length > 2)
              j += (getWidth() - this.m_histBarClassCounts.length * b1 + (int)((Math.floor((f * 0.2F)) < 1.0D) ? 1.0D : Math.floor((f * 0.2F))) * this.m_histBarClassCounts.length) / 2; 
            int m = 0;
            for (byte b2 = 0; b2 < this.m_histBarClassCounts.length; b2++) {
              float f1 = (getHeight() - this.m_fm.getHeight() - i) / this.m_maxValue;
              k = getHeight();
              for (byte b = 0; b < (this.m_histBarClassCounts[b2]).length; b++) {
                m += this.m_histBarClassCounts[b2][b];
                k -= Math.round(this.m_histBarClassCounts[b2][b] * f1);
                paramGraphics.setColor((Color)this.m_colorList.elementAt(b));
                paramGraphics.fillRect(j, k, b1, Math.round(this.m_histBarClassCounts[b2][b] * f1));
                paramGraphics.setColor(Color.black);
              } 
              if (this.m_fm.stringWidth(Integer.toString(m)) < f)
                paramGraphics.drawString(Integer.toString(m), j, k - 1); 
              j = j + b1 + (int)((Math.floor((f * 0.2F)) < 1.0D) ? 1.0D : Math.floor((f * 0.2F)));
              m = 0;
            } 
          } else {
            byte b1;
            float f = getWidth() / this.m_histBarCounts.length;
            if (f > 5.0F) {
              b1 = (int)Math.floor((f * 0.8F));
            } else {
              b1 = 1;
            } 
            j += (int)((Math.floor((f * 0.1F)) < 1.0D) ? 1.0D : Math.floor((f * 0.1F)));
            if (getWidth() - this.m_histBarCounts.length * b1 + (int)((Math.floor((f * 0.2F)) < 1.0D) ? 1.0D : Math.floor((f * 0.2F))) * this.m_histBarCounts.length > 2)
              j += (getWidth() - this.m_histBarCounts.length * b1 + (int)((Math.floor((f * 0.2F)) < 1.0D) ? 1.0D : Math.floor((f * 0.2F))) * this.m_histBarCounts.length) / 2; 
            for (byte b2 = 0; b2 < this.m_histBarCounts.length; b2++) {
              float f1 = (getHeight() - this.m_fm.getHeight() - i) / this.m_maxValue;
              k = getHeight() - Math.round(this.m_histBarCounts[b2] * f1);
              paramGraphics.fillRect(j, k, b1, Math.round(this.m_histBarCounts[b2] * f1));
              if (this.m_fm.stringWidth(Integer.toString(this.m_histBarCounts[b2])) < f)
                paramGraphics.drawString(Integer.toString(this.m_histBarCounts[b2]), j, k - 1); 
              j = j + b1 + (int)((Math.floor((f * 0.2F)) < 1.0D) ? 1.0D : Math.floor((f * 0.2F)));
            } 
          } 
        } else if (this.m_as.numericStats != null && (this.m_histBarClassCounts != null || this.m_histBarCounts != null)) {
          int j = 0;
          int k = 0;
          if (this.m_classIndex >= 0 && this.m_data.attribute(this.m_classIndex).isNominal()) {
            byte b1 = ((getWidth() - 6) / this.m_histBarClassCounts.length < 1) ? 1 : ((getWidth() - 6) / this.m_histBarClassCounts.length);
            j = 3;
            if (getWidth() - j + this.m_histBarClassCounts.length * b1 > 5)
              j += (getWidth() - j + this.m_histBarClassCounts.length * b1) / 2; 
            for (byte b2 = 0; b2 < this.m_histBarClassCounts.length; b2++) {
              float f = (getHeight() - this.m_fm.getHeight() - i - 19.0F) / this.m_maxValue;
              k = getHeight() - 19;
              int m = 0;
              for (byte b = 0; b < (this.m_histBarClassCounts[b2]).length; b++) {
                k -= Math.round(this.m_histBarClassCounts[b2][b] * f);
                paramGraphics.setColor((Color)this.m_colorList.elementAt(b));
                if (b1 > 1) {
                  paramGraphics.fillRect(j, k, b1, Math.round(this.m_histBarClassCounts[b2][b] * f));
                } else if (this.m_histBarClassCounts[b2][b] * f > 0.0F) {
                  paramGraphics.drawLine(j, k, j, k + Math.round(this.m_histBarClassCounts[b2][b] * f));
                } 
                paramGraphics.setColor(Color.black);
                m += this.m_histBarClassCounts[b2][b];
              } 
              if (this.m_fm.stringWidth(" " + Integer.toString(m)) < b1)
                paramGraphics.drawString(" " + Integer.toString(m), j, k - 1); 
              j += b1;
            } 
            j = 3;
            if (getWidth() - j + this.m_histBarClassCounts.length * b1 > 5)
              j += (getWidth() - j + this.m_histBarClassCounts.length * b1) / 2; 
            paramGraphics.drawLine(j, getHeight() - 17, (b1 == 1) ? (j + b1 * this.m_histBarClassCounts.length - 1) : (j + b1 * this.m_histBarClassCounts.length), getHeight() - 17);
            paramGraphics.drawLine(j, getHeight() - 16, j, getHeight() - 12);
            paramGraphics.drawString(Utils.doubleToString(this.m_as.numericStats.min, 2), j, getHeight() - 12 + this.m_fm.getHeight());
            paramGraphics.drawLine(j + b1 * this.m_histBarClassCounts.length / 2, getHeight() - 16, j + b1 * this.m_histBarClassCounts.length / 2, getHeight() - 12);
            paramGraphics.drawString(Utils.doubleToString(this.m_as.numericStats.max / 2.0D + this.m_as.numericStats.min / 2.0D, 2), j + b1 * this.m_histBarClassCounts.length / 2 - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max / 2.0D + this.m_as.numericStats.min / 2.0D, 2)) / 2, getHeight() - 12 + this.m_fm.getHeight());
            paramGraphics.drawLine((b1 == 1) ? (j + b1 * this.m_histBarClassCounts.length - 1) : (j + b1 * this.m_histBarClassCounts.length), getHeight() - 16, (b1 == 1) ? (j + b1 * this.m_histBarClassCounts.length - 1) : (j + b1 * this.m_histBarClassCounts.length), getHeight() - 12);
            paramGraphics.drawString(Utils.doubleToString(this.m_as.numericStats.max, 2), (b1 == 1) ? (j + b1 * this.m_histBarClassCounts.length - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max, 2)) - 1) : (j + b1 * this.m_histBarClassCounts.length - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max, 2))), getHeight() - 12 + this.m_fm.getHeight());
          } else {
            byte b1 = ((getWidth() - 6) / this.m_histBarCounts.length < 1) ? 1 : ((getWidth() - 6) / this.m_histBarCounts.length);
            j = 3;
            if (getWidth() - j + this.m_histBarCounts.length * b1 > 5)
              j += (getWidth() - j + this.m_histBarCounts.length * b1) / 2; 
            for (byte b2 = 0; b2 < this.m_histBarCounts.length; b2++) {
              float f = (getHeight() - this.m_fm.getHeight() - i - 19.0F) / this.m_maxValue;
              k = getHeight() - Math.round(this.m_histBarCounts[b2] * f) - 19;
              if (b1 > 1) {
                paramGraphics.drawRect(j, k, b1, Math.round(this.m_histBarCounts[b2] * f));
              } else if (this.m_histBarCounts[b2] * f > 0.0F) {
                paramGraphics.drawLine(j, k, j, k + Math.round(this.m_histBarCounts[b2] * f));
              } 
              if (this.m_fm.stringWidth(" " + Integer.toString(this.m_histBarCounts[b2])) < b1)
                paramGraphics.drawString(" " + Integer.toString(this.m_histBarCounts[b2]), j, k - 1); 
              j += b1;
            } 
            j = 3;
            if (getWidth() - j + this.m_histBarCounts.length * b1 > 5)
              j += (getWidth() - j + this.m_histBarCounts.length * b1) / 2; 
            paramGraphics.drawLine(j, getHeight() - 17, (b1 == 1) ? (j + b1 * this.m_histBarCounts.length - 1) : (j + b1 * this.m_histBarCounts.length), getHeight() - 17);
            paramGraphics.drawLine(j, getHeight() - 16, j, getHeight() - 12);
            paramGraphics.drawString(Utils.doubleToString(this.m_as.numericStats.min, 2), j, getHeight() - 12 + this.m_fm.getHeight());
            paramGraphics.drawLine(j + b1 * this.m_histBarCounts.length / 2, getHeight() - 16, j + b1 * this.m_histBarCounts.length / 2, getHeight() - 12);
            paramGraphics.drawString(Utils.doubleToString(this.m_as.numericStats.max / 2.0D + this.m_as.numericStats.min / 2.0D, 2), j + b1 * this.m_histBarCounts.length / 2 - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max / 2.0D + this.m_as.numericStats.min / 2.0D, 2)) / 2, getHeight() - 12 + this.m_fm.getHeight());
            paramGraphics.drawLine((b1 == 1) ? (j + b1 * this.m_histBarCounts.length - 1) : (j + b1 * this.m_histBarCounts.length), getHeight() - 16, (b1 == 1) ? (j + b1 * this.m_histBarCounts.length - 1) : (j + b1 * this.m_histBarCounts.length), getHeight() - 12);
            paramGraphics.drawString(Utils.doubleToString(this.m_as.numericStats.max, 2), (b1 == 1) ? (j + b1 * this.m_histBarCounts.length - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max, 2)) - 1) : (j + b1 * this.m_histBarCounts.length - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max, 2))), getHeight() - 12 + this.m_fm.getHeight());
          } 
        } else {
          paramGraphics.clearRect(0, 0, getWidth(), getHeight());
          paramGraphics.drawString("Attribute is neither numeric nor nominal.", getWidth() / 2 - this.m_fm.stringWidth("Attribute is neither numeric nor nominal.") / 2, getHeight() / 2 - this.m_fm.getHeight() / 2);
        } 
      } else {
        paramGraphics.clearRect(0, 0, getWidth(), getHeight());
        paramGraphics.drawString("Calculating. Please Wait...", getWidth() / 2 - this.m_fm.stringWidth("Calculating. Please Wait...") / 2, getHeight() / 2 - this.m_fm.getHeight() / 2);
      }  
  }
  
  public static void main(String[] paramArrayOfString) {
    if (paramArrayOfString.length != 3) {
      JFrame jFrame = new JFrame("AttribVisualization");
      AttributeVisualizationPanel attributeVisualizationPanel = new AttributeVisualizationPanel();
      try {
        Instances instances = new Instances(new FileReader(paramArrayOfString[0]));
        attributeVisualizationPanel.setInstances(instances);
        System.out.println("Loaded: " + paramArrayOfString[0] + "\nRelation: " + attributeVisualizationPanel.m_data.relationName() + "\nAttributes: " + attributeVisualizationPanel.m_data.numAttributes());
        attributeVisualizationPanel.setAttribute(Integer.parseInt(paramArrayOfString[1]));
      } catch (Exception exception) {
        exception.printStackTrace();
        System.exit(-1);
      } 
      System.out.println("The attributes are: ");
      for (byte b = 0; b < attributeVisualizationPanel.m_data.numAttributes(); b++)
        System.out.println(attributeVisualizationPanel.m_data.attribute(b).name()); 
      jFrame.setSize(500, 300);
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add((Component)attributeVisualizationPanel, "Center");
      jFrame.setDefaultCloseOperation(3);
      jFrame.setVisible(true);
    } else {
      System.out.println("Usage: java AttributeVisualizationPanel [arff file] [index of attribute]");
    } 
  }
  
  private class HistCalc extends Thread {
    private final AttributeVisualizationPanel this$0;
    
    private HistCalc(AttributeVisualizationPanel this$0) {
      AttributeVisualizationPanel.this = AttributeVisualizationPanel.this;
    }
    
    public void run() {
      synchronized (AttributeVisualizationPanel.this.m_locker) {
        if (AttributeVisualizationPanel.this.m_classIndex >= 0 && AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).isNominal()) {
          double d1 = 0.0D;
          d1 = 3.49D * AttributeVisualizationPanel.this.m_as.numericStats.stdDev * Math.pow(AttributeVisualizationPanel.this.m_data.numInstances(), -0.3333333333333333D);
          int i = Math.max(1, (int)Math.round((AttributeVisualizationPanel.this.m_as.numericStats.max - AttributeVisualizationPanel.this.m_as.numericStats.min) / d1));
          if (i > AttributeVisualizationPanel.this.getWidth()) {
            i = AttributeVisualizationPanel.this.getWidth() - 6;
            if (i < 1)
              i = 1; 
          } 
          int[][] arrayOfInt = new int[i][AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).numValues() + 1];
          double d2 = (AttributeVisualizationPanel.this.m_as.numericStats.max - AttributeVisualizationPanel.this.m_as.numericStats.min) / arrayOfInt.length;
          AttributeVisualizationPanel.this.m_maxValue = 0;
          if (AttributeVisualizationPanel.this.m_colorList.size() == 0)
            AttributeVisualizationPanel.this.m_colorList.addElement(Color.black); 
          int j;
          for (j = AttributeVisualizationPanel.this.m_colorList.size(); j < AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).numValues() + 1; j++) {
            Color color = AttributeVisualizationPanel.m_defaultColors[(j - 1) % 10];
            int k = (j - 1) / 10;
            k *= 2;
            for (byte b = 0; b < k; b++)
              color = color.darker(); 
            AttributeVisualizationPanel.this.m_colorList.addElement(color);
          } 
          for (j = 0; j < AttributeVisualizationPanel.this.m_data.numInstances(); j++) {
            int k = 0;
            try {
              if (!AttributeVisualizationPanel.this.m_data.instance(j).isMissing(AttributeVisualizationPanel.this.m_attribIndex)) {
                k = (int)Math.ceil((float)((AttributeVisualizationPanel.this.m_data.instance(j).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / d2));
                if (k == 0) {
                  if (AttributeVisualizationPanel.this.m_data.instance(j).isMissing(AttributeVisualizationPanel.this.m_classIndex)) {
                    arrayOfInt[k][0] = arrayOfInt[k][0] + 1;
                  } else {
                    arrayOfInt[k][(int)AttributeVisualizationPanel.this.m_data.instance(j).value(AttributeVisualizationPanel.this.m_classIndex) + 1] = arrayOfInt[k][(int)AttributeVisualizationPanel.this.m_data.instance(j).value(AttributeVisualizationPanel.this.m_classIndex) + 1] + 1;
                  } 
                } else if (AttributeVisualizationPanel.this.m_data.instance(j).isMissing(AttributeVisualizationPanel.this.m_classIndex)) {
                  arrayOfInt[k - 1][0] = arrayOfInt[k - 1][0] + 1;
                } else {
                  arrayOfInt[k - 1][(int)AttributeVisualizationPanel.this.m_data.instance(j).value(AttributeVisualizationPanel.this.m_classIndex) + 1] = arrayOfInt[k - 1][(int)AttributeVisualizationPanel.this.m_data.instance(j).value(AttributeVisualizationPanel.this.m_classIndex) + 1] + 1;
                } 
              } 
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
              System.out.println("t:" + k + " barRange:" + d2 + " histLength:" + arrayOfInt.length + " value:" + AttributeVisualizationPanel.this.m_data.instance(j).value(AttributeVisualizationPanel.this.m_attribIndex) + " min:" + AttributeVisualizationPanel.this.m_as.numericStats.min + " sumResult:" + (AttributeVisualizationPanel.this.m_data.instance(j).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) + " divideResult:" + (float)((AttributeVisualizationPanel.this.m_data.instance(j).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / d2) + " finalResult:" + Math.ceil((float)((AttributeVisualizationPanel.this.m_data.instance(j).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / d2)));
            } 
          } 
          for (j = 0; j < arrayOfInt.length; j++) {
            int k = 0;
            for (byte b = 0; b < (arrayOfInt[j]).length; b++)
              k += arrayOfInt[j][b]; 
            if (AttributeVisualizationPanel.this.m_maxValue < k)
              AttributeVisualizationPanel.this.m_maxValue = k; 
          } 
          AttributeVisualizationPanel.this.m_histBarClassCounts = arrayOfInt;
          AttributeVisualizationPanel.this.m_barRange = d2;
        } else {
          double d1 = 3.49D * AttributeVisualizationPanel.this.m_as.numericStats.stdDev * Math.pow(AttributeVisualizationPanel.this.m_data.numInstances(), -0.3333333333333333D);
          int i = Math.max(1, (int)Math.round((AttributeVisualizationPanel.this.m_as.numericStats.max - AttributeVisualizationPanel.this.m_as.numericStats.min) / d1));
          if (i > AttributeVisualizationPanel.this.getWidth()) {
            i = AttributeVisualizationPanel.this.getWidth() - 6;
            if (i < 1)
              i = 1; 
          } 
          int[] arrayOfInt = new int[i];
          double d2 = (AttributeVisualizationPanel.this.m_as.numericStats.max - AttributeVisualizationPanel.this.m_as.numericStats.min) / arrayOfInt.length;
          AttributeVisualizationPanel.this.m_maxValue = 0;
          for (byte b = 0; b < AttributeVisualizationPanel.this.m_data.numInstances(); b++) {
            int j = 0;
            try {
              j = (int)Math.ceil((float)((AttributeVisualizationPanel.this.m_data.instance(b).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / d2));
              if (j == 0) {
                arrayOfInt[j] = arrayOfInt[j] + 1;
                if (arrayOfInt[j] > AttributeVisualizationPanel.this.m_maxValue)
                  AttributeVisualizationPanel.this.m_maxValue = arrayOfInt[j]; 
              } else {
                arrayOfInt[j - 1] = arrayOfInt[j - 1] + 1;
                if (arrayOfInt[j - 1] > AttributeVisualizationPanel.this.m_maxValue)
                  AttributeVisualizationPanel.this.m_maxValue = arrayOfInt[j - 1]; 
              } 
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
              arrayIndexOutOfBoundsException.printStackTrace();
              System.out.println("t:" + j + " barRange:" + d2 + " histLength:" + arrayOfInt.length + " value:" + AttributeVisualizationPanel.this.m_data.instance(b).value(AttributeVisualizationPanel.this.m_attribIndex) + " min:" + AttributeVisualizationPanel.this.m_as.numericStats.min + " sumResult:" + (AttributeVisualizationPanel.this.m_data.instance(b).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) + " divideResult:" + (float)((AttributeVisualizationPanel.this.m_data.instance(b).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / d2) + " finalResult:" + Math.ceil((float)((AttributeVisualizationPanel.this.m_data.instance(b).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / d2)));
            } 
          } 
          AttributeVisualizationPanel.this.m_histBarCounts = arrayOfInt;
          AttributeVisualizationPanel.this.m_barRange = d2;
        } 
        AttributeVisualizationPanel.this.m_threadRun = false;
        AttributeVisualizationPanel.this.repaint();
      } 
    }
  }
  
  private class BarCalc extends Thread {
    private final AttributeVisualizationPanel this$0;
    
    private BarCalc(AttributeVisualizationPanel this$0) {
      AttributeVisualizationPanel.this = AttributeVisualizationPanel.this;
    }
    
    public void run() {
      synchronized (AttributeVisualizationPanel.this.m_locker) {
        if (AttributeVisualizationPanel.this.m_classIndex >= 0 && AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).isNominal()) {
          int[][] arrayOfInt = new int[AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_attribIndex).numValues()][AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).numValues() + 1];
          AttributeVisualizationPanel.this.m_maxValue = AttributeVisualizationPanel.this.m_as.nominalCounts[0];
          int i;
          for (i = 0; i < AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_attribIndex).numValues(); i++) {
            if (AttributeVisualizationPanel.this.m_as.nominalCounts[i] > AttributeVisualizationPanel.this.m_maxValue)
              AttributeVisualizationPanel.this.m_maxValue = AttributeVisualizationPanel.this.m_as.nominalCounts[i]; 
          } 
          if (AttributeVisualizationPanel.this.m_colorList.size() == 0)
            AttributeVisualizationPanel.this.m_colorList.addElement(Color.black); 
          for (i = AttributeVisualizationPanel.this.m_colorList.size(); i < AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).numValues() + 1; i++) {
            Color color = AttributeVisualizationPanel.m_defaultColors[(i - 1) % 10];
            int j = (i - 1) / 10;
            j *= 2;
            for (byte b = 0; b < j; b++)
              color = color.darker(); 
            AttributeVisualizationPanel.this.m_colorList.addElement(color);
          } 
          for (i = 0; i < AttributeVisualizationPanel.this.m_data.numInstances(); i++) {
            if (!AttributeVisualizationPanel.this.m_data.instance(i).isMissing(AttributeVisualizationPanel.this.m_attribIndex))
              if (AttributeVisualizationPanel.this.m_data.instance(i).isMissing(AttributeVisualizationPanel.this.m_classIndex)) {
                arrayOfInt[(int)AttributeVisualizationPanel.this.m_data.instance(i).value(AttributeVisualizationPanel.this.m_attribIndex)][0] = arrayOfInt[(int)AttributeVisualizationPanel.this.m_data.instance(i).value(AttributeVisualizationPanel.this.m_attribIndex)][0] + 1;
              } else {
                arrayOfInt[(int)AttributeVisualizationPanel.this.m_data.instance(i).value(AttributeVisualizationPanel.this.m_attribIndex)][(int)AttributeVisualizationPanel.this.m_data.instance(i).value(AttributeVisualizationPanel.this.m_classIndex) + 1] = arrayOfInt[(int)AttributeVisualizationPanel.this.m_data.instance(i).value(AttributeVisualizationPanel.this.m_attribIndex)][(int)AttributeVisualizationPanel.this.m_data.instance(i).value(AttributeVisualizationPanel.this.m_classIndex) + 1] + 1;
              }  
          } 
          AttributeVisualizationPanel.this.m_threadRun = false;
          AttributeVisualizationPanel.this.m_histBarClassCounts = arrayOfInt;
          AttributeVisualizationPanel.this.repaint();
        } else {
          int[] arrayOfInt = new int[AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_attribIndex).numValues()];
          AttributeVisualizationPanel.this.m_maxValue = AttributeVisualizationPanel.this.m_as.nominalCounts[0];
          byte b;
          for (b = 0; b < AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_attribIndex).numValues(); b++) {
            if (AttributeVisualizationPanel.this.m_as.nominalCounts[b] > AttributeVisualizationPanel.this.m_maxValue)
              AttributeVisualizationPanel.this.m_maxValue = AttributeVisualizationPanel.this.m_as.nominalCounts[b]; 
          } 
          for (b = 0; b < AttributeVisualizationPanel.this.m_data.numInstances(); b++)
            arrayOfInt[(int)AttributeVisualizationPanel.this.m_data.instance(b).value(AttributeVisualizationPanel.this.m_attribIndex)] = arrayOfInt[(int)AttributeVisualizationPanel.this.m_data.instance(b).value(AttributeVisualizationPanel.this.m_attribIndex)] + 1; 
          AttributeVisualizationPanel.this.m_threadRun = false;
          AttributeVisualizationPanel.this.m_histBarCounts = arrayOfInt;
          AttributeVisualizationPanel.this.repaint();
        } 
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\AttributeVisualizationPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */