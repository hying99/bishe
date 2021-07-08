package weka.gui.visualize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

public class AttributePanel extends JScrollPane {
  protected Instances m_plotInstances = null;
  
  protected double m_maxC;
  
  protected double m_minC;
  
  protected int m_cIndex;
  
  protected int m_xIndex;
  
  protected int m_yIndex;
  
  protected FastVector m_colorList;
  
  protected Color[] m_DefaultColors = new Color[] { Color.blue, Color.red, Color.green, Color.cyan, Color.pink, new Color(255, 0, 255), Color.orange, new Color(255, 0, 0), new Color(0, 255, 0), Color.white };
  
  protected FastVector m_Listeners = new FastVector();
  
  protected int[] m_heights;
  
  protected JPanel m_span = null;
  
  protected Color m_barColour = Color.black;
  
  private void setProperties() {
    if (VisualizeUtils.VISUALIZE_PROPERTIES != null) {
      String str1 = getClass().getName();
      String str2 = str1 + ".barColour";
      String str3 = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(str2);
      if (str3 != null)
        this.m_barColour = VisualizeUtils.processColour(str3, this.m_barColour); 
    } 
  }
  
  public AttributePanel() {
    setProperties();
    setBackground(Color.blue);
    setVerticalScrollBarPolicy(22);
    this.m_colorList = new FastVector(10);
    for (int i = this.m_colorList.size(); i < 10; i++) {
      Color color = this.m_DefaultColors[i % 10];
      int j = i / 10;
      j *= 2;
      for (byte b = 0; b < j; b++)
        color = color.darker(); 
      this.m_colorList.addElement(color);
    } 
  }
  
  public void addAttributePanelListener(AttributePanelListener paramAttributePanelListener) {
    this.m_Listeners.addElement(paramAttributePanelListener);
  }
  
  public void setCindex(int paramInt, double paramDouble1, double paramDouble2) {
    this.m_cIndex = paramInt;
    this.m_maxC = paramDouble1;
    this.m_minC = paramDouble2;
    if (this.m_span != null) {
      if (this.m_plotInstances.numAttributes() > 0 && this.m_cIndex < this.m_plotInstances.numAttributes() && this.m_plotInstances.attribute(this.m_cIndex).isNominal() && this.m_plotInstances.attribute(this.m_cIndex).numValues() > this.m_colorList.size())
        extendColourMap(); 
      repaint();
    } 
  }
  
  public void setCindex(int paramInt) {
    this.m_cIndex = paramInt;
    if (this.m_span != null) {
      if (this.m_cIndex < this.m_plotInstances.numAttributes() && this.m_plotInstances.attribute(this.m_cIndex).isNumeric()) {
        double d1 = Double.POSITIVE_INFINITY;
        double d2 = Double.NEGATIVE_INFINITY;
        for (byte b = 0; b < this.m_plotInstances.numInstances(); b++) {
          if (!this.m_plotInstances.instance(b).isMissing(this.m_cIndex)) {
            double d = this.m_plotInstances.instance(b).value(this.m_cIndex);
            if (d < d1)
              d1 = d; 
            if (d > d2)
              d2 = d; 
          } 
        } 
        this.m_minC = d1;
        this.m_maxC = d2;
      } else if (this.m_plotInstances.attribute(this.m_cIndex).numValues() > this.m_colorList.size()) {
        extendColourMap();
      } 
      repaint();
    } 
  }
  
  private void extendColourMap() {
    if (this.m_plotInstances.attribute(this.m_cIndex).isNominal())
      for (int i = this.m_colorList.size(); i < this.m_plotInstances.attribute(this.m_cIndex).numValues(); i++) {
        Color color = this.m_DefaultColors[i % 10];
        int j = i / 10;
        j *= 2;
        for (byte b = 0; b < j; b++)
          color = color.brighter(); 
        this.m_colorList.addElement(color);
      }  
  }
  
  public void setColours(FastVector paramFastVector) {
    this.m_colorList = paramFastVector;
  }
  
  public void setInstances(Instances paramInstances) throws Exception {
    if (paramInstances.numAttributes() > 512)
      throw new Exception("Can't display more than 512 attributes!"); 
    if (this.m_span == null)
      this.m_span = new JPanel(this) {
          private final AttributePanel this$0;
          
          public void paintComponent(Graphics param1Graphics) {
            super.paintComponent(param1Graphics);
            param1Graphics.setColor(Color.red);
            if (this.this$0.m_yIndex != this.this$0.m_xIndex) {
              param1Graphics.drawString("X", 5, this.this$0.m_xIndex * 20 + 16);
              param1Graphics.drawString("Y", 5, this.this$0.m_yIndex * 20 + 16);
            } else {
              param1Graphics.drawString("B", 5, this.this$0.m_xIndex * 20 + 16);
            } 
          }
        }; 
    this.m_span.removeAll();
    this.m_plotInstances = paramInstances;
    if (paramInstances.numInstances() > 0 && paramInstances.numAttributes() > 0) {
      JPanel jPanel1 = new JPanel();
      JPanel jPanel2 = new JPanel();
      this.m_heights = new int[paramInstances.numInstances()];
      this.m_cIndex = paramInstances.numAttributes() - 1;
      for (byte b1 = 0; b1 < paramInstances.numInstances(); b1++)
        this.m_heights[b1] = (int)(Math.random() * 19.0D); 
      this.m_span.setPreferredSize(new Dimension((this.m_span.getPreferredSize()).width, (this.m_cIndex + 1) * 20));
      this.m_span.setMaximumSize(new Dimension((this.m_span.getMaximumSize()).width, (this.m_cIndex + 1) * 20));
      GridBagLayout gridBagLayout1 = new GridBagLayout();
      GridBagLayout gridBagLayout2 = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      jPanel1.setLayout(gridBagLayout1);
      this.m_span.setLayout(gridBagLayout2);
      gridBagConstraints.anchor = 10;
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.weightx = 5.0D;
      gridBagConstraints.fill = 2;
      gridBagConstraints.gridwidth = 1;
      gridBagConstraints.gridheight = 1;
      gridBagConstraints.insets = new Insets(0, 0, 0, 0);
      jPanel1.add(this.m_span, gridBagConstraints);
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.weightx = 5.0D;
      gridBagConstraints.fill = 1;
      gridBagConstraints.gridwidth = 1;
      gridBagConstraints.gridheight = 1;
      gridBagConstraints.weighty = 5.0D;
      gridBagConstraints.insets = new Insets(0, 0, 0, 0);
      jPanel1.add(jPanel2, gridBagConstraints);
      gridBagConstraints.weighty = 0.0D;
      setViewportView(jPanel1);
      gridBagConstraints.anchor = 10;
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.weightx = 5.0D;
      gridBagConstraints.fill = 2;
      gridBagConstraints.gridwidth = 1;
      gridBagConstraints.gridheight = 1;
      gridBagConstraints.weighty = 5.0D;
      gridBagConstraints.insets = new Insets(2, 20, 2, 4);
      for (byte b2 = 0; b2 < paramInstances.numAttributes(); b2++) {
        AttributeSpacing attributeSpacing = new AttributeSpacing(this, paramInstances.attribute(b2), b2);
        gridBagConstraints.gridy = b2;
        this.m_span.add(attributeSpacing, gridBagConstraints);
      } 
    } 
  }
  
  public void setX(int paramInt) {
    if (this.m_span != null) {
      this.m_xIndex = paramInt;
      this.m_span.repaint();
    } 
  }
  
  public void setY(int paramInt) {
    if (this.m_span != null) {
      this.m_yIndex = paramInt;
      this.m_span.repaint();
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length < 1) {
        System.err.println("Usage : weka.gui.visualize.AttributePanel <dataset> [class col]");
        System.exit(1);
      } 
      JFrame jFrame = new JFrame("Weka Explorer: Attribute");
      jFrame.setSize(100, 100);
      jFrame.getContentPane().setLayout(new BorderLayout());
      AttributePanel attributePanel = new AttributePanel();
      attributePanel.addAttributePanelListener(new AttributePanelListener() {
            public void attributeSelectionChange(AttributePanelEvent param1AttributePanelEvent) {
              if (param1AttributePanelEvent.m_xChange) {
                System.err.println("X index changed to : " + param1AttributePanelEvent.m_indexVal);
              } else {
                System.err.println("Y index changed to : " + param1AttributePanelEvent.m_indexVal);
              } 
            }
          });
      jFrame.getContentPane().add(attributePanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      if (paramArrayOfString.length >= 1) {
        System.err.println("Loading instances from " + paramArrayOfString[0]);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
        Instances instances = new Instances(bufferedReader);
        instances.setClassIndex(instances.numAttributes() - 1);
        attributePanel.setInstances(instances);
      } 
      if (paramArrayOfString.length > 1) {
        attributePanel.setCindex(Integer.parseInt(paramArrayOfString[1]) - 1);
      } else {
        attributePanel.setCindex(0);
      } 
      jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  protected class AttributeSpacing extends JPanel {
    protected double m_maxVal;
    
    protected double m_minVal;
    
    protected Attribute m_attrib;
    
    protected int m_attribIndex;
    
    protected int[] m_cached;
    
    protected boolean[][] m_pointDrawn;
    
    protected int m_oldWidth;
    
    private final AttributePanel this$0;
    
    public AttributeSpacing(AttributePanel this$0, Attribute param1Attribute, int param1Int) {
      this.this$0 = this$0;
      this.m_oldWidth = -9000;
      this.m_attrib = param1Attribute;
      this.m_attribIndex = param1Int;
      setBackground(this$0.m_barColour);
      setPreferredSize(new Dimension(0, 20));
      setMinimumSize(new Dimension(0, 20));
      this.m_cached = new int[this$0.m_plotInstances.numInstances()];
      double d1 = Double.POSITIVE_INFINITY;
      double d2 = Double.NEGATIVE_INFINITY;
      if (this$0.m_plotInstances.attribute(this.m_attribIndex).isNominal()) {
        this.m_minVal = 0.0D;
        this.m_maxVal = (this$0.m_plotInstances.attribute(this.m_attribIndex).numValues() - 1);
      } else {
        for (byte b = 0; b < this$0.m_plotInstances.numInstances(); b++) {
          if (!this$0.m_plotInstances.instance(b).isMissing(this.m_attribIndex)) {
            double d = this$0.m_plotInstances.instance(b).value(this.m_attribIndex);
            if (d < d1)
              d1 = d; 
            if (d > d2)
              d2 = d; 
          } 
        } 
        this.m_minVal = d1;
        this.m_maxVal = d2;
        if (d1 == d2) {
          this.m_maxVal += 0.05D;
          this.m_minVal -= 0.05D;
        } 
      } 
      addMouseListener((MouseListener)new Object(this));
    }
    
    private double convertToPanel(double param1Double) {
      double d1 = (param1Double - this.m_minVal) / (this.m_maxVal - this.m_minVal);
      double d2 = d1 * (getWidth() - 10);
      return d2 + 4.0D;
    }
    
    public void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      int i = getWidth();
      if (this.this$0.m_plotInstances != null && this.this$0.m_plotInstances.numAttributes() > 0 && this.this$0.m_plotInstances.numInstances() > 0) {
        if (this.m_oldWidth != i) {
          this.m_pointDrawn = new boolean[i][20];
          for (byte b = 0; b < this.this$0.m_plotInstances.numInstances(); b++) {
            if (!this.this$0.m_plotInstances.instance(b).isMissing(this.m_attribIndex) && !this.this$0.m_plotInstances.instance(b).isMissing(this.this$0.m_cIndex)) {
              this.m_cached[b] = (int)convertToPanel(this.this$0.m_plotInstances.instance(b).value(this.m_attribIndex));
              if (this.m_pointDrawn[this.m_cached[b] % i][this.this$0.m_heights[b]]) {
                this.m_cached[b] = -9000;
              } else {
                this.m_pointDrawn[this.m_cached[b] % i][this.this$0.m_heights[b]] = true;
              } 
            } else {
              this.m_cached[b] = -9000;
            } 
          } 
          this.m_oldWidth = i;
        } 
        if (this.this$0.m_plotInstances.attribute(this.this$0.m_cIndex).isNominal()) {
          for (byte b = 0; b < this.this$0.m_plotInstances.numInstances(); b++) {
            if (this.m_cached[b] != -9000) {
              int j = this.m_cached[b];
              int k = this.this$0.m_heights[b];
              if (this.this$0.m_plotInstances.attribute(this.m_attribIndex).isNominal())
                j += (int)(Math.random() * 5.0D) - 2; 
              int m = (int)this.this$0.m_plotInstances.instance(b).value(this.this$0.m_cIndex);
              param1Graphics.setColor((Color)this.this$0.m_colorList.elementAt(m % this.this$0.m_colorList.size()));
              param1Graphics.drawRect(j, k, 1, 1);
            } 
          } 
        } else {
          for (byte b = 0; b < this.this$0.m_plotInstances.numInstances(); b++) {
            if (this.m_cached[b] != -9000) {
              double d = (this.this$0.m_plotInstances.instance(b).value(this.this$0.m_cIndex) - this.this$0.m_minC) / (this.this$0.m_maxC - this.this$0.m_minC);
              d = d * 240.0D + 15.0D;
              param1Graphics.setColor(new Color((int)d, 150, (int)(255.0D - d)));
              int j = this.m_cached[b];
              int k = this.this$0.m_heights[b];
              if (this.this$0.m_plotInstances.attribute(this.m_attribIndex).isNominal())
                j += (int)(Math.random() * 5.0D) - 2; 
              param1Graphics.drawRect(j, k, 1, 1);
            } 
          } 
        } 
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\AttributePanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */