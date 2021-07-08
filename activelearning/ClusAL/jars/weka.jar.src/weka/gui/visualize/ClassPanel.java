package weka.gui.visualize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Utils;

public class ClassPanel extends JPanel {
  private boolean m_isEnabled = false;
  
  private boolean m_isNumeric = false;
  
  private final int m_spectrumHeight = 5;
  
  private double m_maxC;
  
  private double m_minC;
  
  private final int m_tickSize = 5;
  
  private FontMetrics m_labelMetrics = null;
  
  private Font m_labelFont = null;
  
  private int m_HorizontalPad = 0;
  
  private int m_precisionC;
  
  private int m_fieldWidthC;
  
  private int m_oldWidth = -9000;
  
  private Instances m_Instances = null;
  
  private int m_cIndex;
  
  private FastVector m_colorList = new FastVector(10);
  
  private FastVector m_Repainters = new FastVector();
  
  private FastVector m_ColourChangeListeners = new FastVector();
  
  protected Color[] m_DefaultColors = new Color[] { Color.blue, Color.red, Color.green, Color.cyan, Color.pink, new Color(255, 0, 255), Color.orange, new Color(255, 0, 0), new Color(0, 255, 0), Color.white };
  
  public ClassPanel() {
    for (int i = this.m_colorList.size(); i < 10; i++) {
      Color color = this.m_DefaultColors[i % 10];
      int j = i / 10;
      j *= 2;
      for (byte b = 0; b < j; b++)
        color = color.darker(); 
      this.m_colorList.addElement(color);
    } 
  }
  
  public void addRepaintNotify(Component paramComponent) {
    this.m_Repainters.addElement(paramComponent);
  }
  
  public void addActionListener(ActionListener paramActionListener) {
    this.m_ColourChangeListeners.addElement(paramActionListener);
  }
  
  private void setFonts(Graphics paramGraphics) {
    if (this.m_labelMetrics == null) {
      this.m_labelFont = new Font("Monospaced", 0, 12);
      this.m_labelMetrics = paramGraphics.getFontMetrics(this.m_labelFont);
      int i = this.m_labelMetrics.getAscent();
      if (getHeight() < 3 * i) {
        this.m_labelFont = new Font("Monospaced", 0, 11);
        this.m_labelMetrics = paramGraphics.getFontMetrics(this.m_labelFont);
      } 
    } 
    paramGraphics.setFont(this.m_labelFont);
  }
  
  public void setOn(boolean paramBoolean) {
    this.m_isEnabled = paramBoolean;
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
  }
  
  public void setCindex(int paramInt) {
    if (this.m_Instances.numAttributes() > 0) {
      this.m_cIndex = paramInt;
      if (this.m_Instances.attribute(this.m_cIndex).isNumeric()) {
        setNumeric();
      } else {
        if (this.m_Instances.attribute(this.m_cIndex).numValues() > this.m_colorList.size())
          extendColourMap(); 
        setNominal();
      } 
    } 
  }
  
  private void extendColourMap() {
    if (this.m_Instances.attribute(this.m_cIndex).isNominal())
      for (int i = this.m_colorList.size(); i < this.m_Instances.attribute(this.m_cIndex).numValues(); i++) {
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
  
  protected void setNominal() {
    this.m_isNumeric = false;
    this.m_HorizontalPad = 0;
    setOn(true);
    this.m_oldWidth = -9000;
    repaint();
  }
  
  protected void setNumeric() {
    this.m_isNumeric = true;
    double d1 = Double.POSITIVE_INFINITY;
    double d2 = Double.NEGATIVE_INFINITY;
    int i;
    for (i = 0; i < this.m_Instances.numInstances(); i++) {
      if (!this.m_Instances.instance(i).isMissing(this.m_cIndex)) {
        double d = this.m_Instances.instance(i).value(this.m_cIndex);
        if (d < d1)
          d1 = d; 
        if (d > d2)
          d2 = d; 
      } 
    } 
    if (d1 == Double.POSITIVE_INFINITY)
      d1 = d2 = 0.0D; 
    this.m_minC = d1;
    this.m_maxC = d2;
    i = (int)Math.abs(this.m_maxC);
    double d3 = Math.abs(this.m_maxC) - i;
    byte b = (i > 0) ? (int)(Math.log(i) / Math.log(10.0D)) : 1;
    this.m_precisionC = (d3 > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.m_maxC)) / Math.log(10.0D)) + 2) : 1;
    if (this.m_precisionC > VisualizeUtils.MAX_PRECISION)
      this.m_precisionC = 1; 
    String str = Utils.doubleToString(this.m_maxC, b + 1 + this.m_precisionC, this.m_precisionC);
    if (this.m_labelMetrics != null)
      this.m_HorizontalPad = this.m_labelMetrics.stringWidth(str); 
    i = (int)Math.abs(this.m_minC);
    d3 = Math.abs(this.m_minC) - i;
    b = (i > 0) ? (int)(Math.log(i) / Math.log(10.0D)) : 1;
    this.m_precisionC = (d3 > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.m_minC)) / Math.log(10.0D)) + 2) : 1;
    if (this.m_precisionC > VisualizeUtils.MAX_PRECISION)
      this.m_precisionC = 1; 
    str = Utils.doubleToString(this.m_minC, b + 1 + this.m_precisionC, this.m_precisionC);
    if (this.m_labelMetrics != null && this.m_labelMetrics.stringWidth(str) > this.m_HorizontalPad)
      this.m_HorizontalPad = this.m_labelMetrics.stringWidth(str); 
    setOn(true);
    repaint();
  }
  
  protected void paintNominal(Graphics paramGraphics) {
    byte b;
    setFonts(paramGraphics);
    int i = this.m_Instances.attribute(this.m_cIndex).numValues();
    int j = 0;
    int k = 0;
    int m = getWidth();
    int n = this.m_labelMetrics.getAscent();
    int i1;
    for (i1 = 0; i1 < i; i1++) {
      if (this.m_Instances.attribute(this.m_cIndex).value(i1).length() > j) {
        j = this.m_Instances.attribute(this.m_cIndex).value(i1).length();
        k = i1;
      } 
    } 
    j = this.m_labelMetrics.stringWidth(this.m_Instances.attribute(this.m_cIndex).value(k));
    if ((m - 2 * this.m_HorizontalPad) / (j + 5) >= i) {
      b = 1;
    } else {
      b = 2;
    } 
    i1 = this.m_HorizontalPad;
    int i2 = 1 + n;
    int i3 = (b == 1) ? i : (i / 2);
    int i4;
    for (i4 = 0; i4 < i3; i4++) {
      paramGraphics.setColor((Color)this.m_colorList.elementAt(i4));
      if (i3 * j > m - this.m_HorizontalPad * 2) {
        String str = this.m_Instances.attribute(this.m_cIndex).value(i4);
        int i5 = this.m_labelMetrics.stringWidth(str);
        int i6 = 0;
        if (i5 > (m - this.m_HorizontalPad * 2) / i3) {
          int i7 = i5 / str.length();
          i6 = (i5 - (m - this.m_HorizontalPad * 2) / i3) / i7;
          if (i6 <= 0)
            i6 = 0; 
          str = str.substring(0, str.length() - i6);
          i5 = this.m_labelMetrics.stringWidth(str);
        } 
        NomLabel nomLabel = new NomLabel(this, str, i4);
        nomLabel.setFont(paramGraphics.getFont());
        nomLabel.setSize(this.m_labelMetrics.stringWidth(nomLabel.getText()), this.m_labelMetrics.getAscent() + 4);
        add(nomLabel);
        nomLabel.setLocation(i1, i2);
        nomLabel.setForeground((Color)this.m_colorList.elementAt(i4 % this.m_colorList.size()));
        i1 += i5 + 2;
      } else {
        NomLabel nomLabel = new NomLabel(this, this.m_Instances.attribute(this.m_cIndex).value(i4), i4);
        nomLabel.setFont(paramGraphics.getFont());
        nomLabel.setSize(this.m_labelMetrics.stringWidth(nomLabel.getText()), this.m_labelMetrics.getAscent() + 4);
        add(nomLabel);
        nomLabel.setLocation(i1, i2);
        nomLabel.setForeground((Color)this.m_colorList.elementAt(i4 % this.m_colorList.size()));
        i1 += (m - this.m_HorizontalPad * 2) / i3;
      } 
    } 
    i1 = this.m_HorizontalPad;
    i2 = 1 + n + 5 + n;
    for (i4 = i3; i4 < i; i4++) {
      paramGraphics.setColor((Color)this.m_colorList.elementAt(i4));
      if ((i - i3 + 1) * j > m - this.m_HorizontalPad * 2) {
        String str = this.m_Instances.attribute(this.m_cIndex).value(i4);
        int i5 = this.m_labelMetrics.stringWidth(str);
        int i6 = 0;
        if (i5 > (m - this.m_HorizontalPad * 2) / (i - i3 + 1)) {
          int i7 = i5 / str.length();
          i6 = (i5 - (m - this.m_HorizontalPad * 2) / (i - i3)) / i7;
          if (i6 <= 0)
            i6 = 0; 
          str = str.substring(0, str.length() - i6);
          i5 = this.m_labelMetrics.stringWidth(str);
        } 
        NomLabel nomLabel = new NomLabel(this, str, i4);
        nomLabel.setFont(paramGraphics.getFont());
        nomLabel.setSize(this.m_labelMetrics.stringWidth(nomLabel.getText()), this.m_labelMetrics.getAscent() + 4);
        add(nomLabel);
        nomLabel.setLocation(i1, i2);
        nomLabel.setForeground((Color)this.m_colorList.elementAt(i4 % this.m_colorList.size()));
        i1 += i5 + 2;
      } else {
        NomLabel nomLabel = new NomLabel(this, this.m_Instances.attribute(this.m_cIndex).value(i4), i4);
        nomLabel.setFont(paramGraphics.getFont());
        nomLabel.setSize(this.m_labelMetrics.stringWidth(nomLabel.getText()), this.m_labelMetrics.getAscent() + 4);
        add(nomLabel);
        nomLabel.setLocation(i1, i2);
        nomLabel.setForeground((Color)this.m_colorList.elementAt(i4 % this.m_colorList.size()));
        i1 += (m - this.m_HorizontalPad * 2) / (i - i3);
      } 
    } 
  }
  
  protected void paintNumeric(Graphics paramGraphics) {
    setFonts(paramGraphics);
    if (this.m_HorizontalPad == 0)
      setCindex(this.m_cIndex); 
    int i = getWidth();
    double d1 = 15.0D;
    double d2 = 240.0D / (i - this.m_HorizontalPad * 2);
    int j = this.m_labelMetrics.getAscent();
    int k;
    for (k = this.m_HorizontalPad; k < i - this.m_HorizontalPad; k++) {
      Color color = new Color((int)d1, 150, (int)(255.0D - d1));
      paramGraphics.setColor(color);
      paramGraphics.drawLine(k, 0, k, 5);
      d1 += d2;
    } 
    k = (int)Math.abs(this.m_maxC);
    double d3 = Math.abs(this.m_maxC) - k;
    byte b = (k > 0) ? (int)(Math.log(k) / Math.log(10.0D)) : 1;
    this.m_precisionC = (d3 > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.m_maxC)) / Math.log(10.0D)) + 2) : 1;
    if (this.m_precisionC > VisualizeUtils.MAX_PRECISION)
      this.m_precisionC = 1; 
    String str = Utils.doubleToString(this.m_maxC, b + 1 + this.m_precisionC, this.m_precisionC);
    int m = this.m_labelMetrics.stringWidth(str);
    int n = m;
    if (i > 2 * n) {
      paramGraphics.setColor(Color.black);
      paramGraphics.drawLine(this.m_HorizontalPad, 10, i - this.m_HorizontalPad, 10);
      paramGraphics.drawLine(i - this.m_HorizontalPad, 10, i - this.m_HorizontalPad, 15);
      paramGraphics.drawString(str, i - this.m_HorizontalPad - m / 2, 15 + j);
      paramGraphics.drawLine(this.m_HorizontalPad, 10, this.m_HorizontalPad, 15);
      k = (int)Math.abs(this.m_minC);
      d3 = Math.abs(this.m_minC) - k;
      b = (k > 0) ? (int)(Math.log(k) / Math.log(10.0D)) : 1;
      this.m_precisionC = (d3 > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.m_minC)) / Math.log(10.0D)) + 2) : 1;
      if (this.m_precisionC > VisualizeUtils.MAX_PRECISION)
        this.m_precisionC = 1; 
      str = Utils.doubleToString(this.m_minC, b + 1 + this.m_precisionC, this.m_precisionC);
      m = this.m_labelMetrics.stringWidth(str);
      paramGraphics.drawString(str, this.m_HorizontalPad - m / 2, 15 + j);
      if (i > 3 * n) {
        double d = this.m_minC + (this.m_maxC - this.m_minC) / 2.0D;
        paramGraphics.drawLine(this.m_HorizontalPad + (i - 2 * this.m_HorizontalPad) / 2, 10, this.m_HorizontalPad + (i - 2 * this.m_HorizontalPad) / 2, 15);
        k = (int)Math.abs(d);
        d3 = Math.abs(d) - k;
        b = (k > 0) ? (int)(Math.log(k) / Math.log(10.0D)) : 1;
        this.m_precisionC = (d3 > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(d)) / Math.log(10.0D)) + 2) : 1;
        if (this.m_precisionC > VisualizeUtils.MAX_PRECISION)
          this.m_precisionC = 1; 
        str = Utils.doubleToString(d, b + 1 + this.m_precisionC, this.m_precisionC);
        m = this.m_labelMetrics.stringWidth(str);
        paramGraphics.drawString(str, this.m_HorizontalPad + (i - 2 * this.m_HorizontalPad) / 2 - m / 2, 15 + j);
      } 
    } 
  }
  
  public void paintComponent(Graphics paramGraphics) {
    super.paintComponent(paramGraphics);
    if (this.m_isEnabled)
      if (this.m_isNumeric) {
        this.m_oldWidth = -9000;
        removeAll();
        paintNumeric(paramGraphics);
      } else if (this.m_Instances != null && this.m_Instances.numInstances() > 0 && this.m_Instances.numAttributes() > 0 && this.m_oldWidth != getWidth()) {
        removeAll();
        this.m_oldWidth = getWidth();
        paintNominal(paramGraphics);
      }  
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length < 1) {
        System.err.println("Usage : weka.gui.visualize.ClassPanel <dataset> [class col]");
        System.exit(1);
      } 
      JFrame jFrame = new JFrame("Weka Explorer: Class");
      jFrame.setSize(500, 100);
      jFrame.getContentPane().setLayout(new BorderLayout());
      ClassPanel classPanel = new ClassPanel();
      jFrame.getContentPane().add(classPanel, "Center");
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
        classPanel.setInstances(instances);
      } 
      if (paramArrayOfString.length > 1) {
        classPanel.setCindex(Integer.parseInt(paramArrayOfString[1]) - 1);
      } else {
        classPanel.setCindex(0);
      } 
      jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  private class NomLabel extends JLabel {
    private int m_index;
    
    private final ClassPanel this$0;
    
    public NomLabel(ClassPanel this$0, String param1String, int param1Int) {
      super(param1String);
      this.this$0 = this$0;
      this.m_index = 0;
      this.m_index = param1Int;
      addMouseListener((MouseListener)new Object(this, this$0));
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\ClassPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */