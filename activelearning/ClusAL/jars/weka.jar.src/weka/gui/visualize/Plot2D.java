package weka.gui.visualize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Utils;

public class Plot2D extends PrintablePanel {
  public static final int MAX_SHAPES = 5;
  
  public static final int ERROR_SHAPE = 1000;
  
  public static final int MISSING_SHAPE = 2000;
  
  public static final int CONST_AUTOMATIC_SHAPE = -1;
  
  public static final int X_SHAPE = 0;
  
  public static final int PLUS_SHAPE = 1;
  
  public static final int DIAMOND_SHAPE = 2;
  
  public static final int TRIANGLEUP_SHAPE = 3;
  
  public static final int TRIANGLEDOWN_SHAPE = 4;
  
  public static final int DEFAULT_SHAPE_SIZE = 2;
  
  protected Color m_axisColour = Color.green;
  
  protected Color m_backgroundColour = Color.black;
  
  protected FastVector m_plots = new FastVector();
  
  protected PlotData2D m_masterPlot = null;
  
  protected String m_masterName = "master plot";
  
  protected Instances m_plotInstances = null;
  
  protected Plot2DCompanion m_plotCompanion = null;
  
  protected JFrame m_InstanceInfo = null;
  
  protected JTextArea m_InstanceInfoText = new JTextArea();
  
  protected FastVector m_colorList;
  
  protected Color[] m_DefaultColors = new Color[] { Color.blue, Color.red, Color.green, Color.cyan, Color.pink, new Color(255, 0, 255), Color.orange, new Color(255, 0, 0), new Color(0, 255, 0), Color.white };
  
  protected int m_xIndex = 0;
  
  protected int m_yIndex = 0;
  
  protected int m_cIndex = 0;
  
  protected int m_sIndex = 0;
  
  protected double m_maxX;
  
  protected double m_minX;
  
  protected double m_maxY;
  
  protected double m_minY;
  
  protected double m_maxC;
  
  protected double m_minC;
  
  protected final int m_axisPad = 5;
  
  protected final int m_tickSize = 5;
  
  protected int m_XaxisStart = 0;
  
  protected int m_YaxisStart = 0;
  
  protected int m_XaxisEnd = 0;
  
  protected int m_YaxisEnd = 0;
  
  protected boolean m_plotResize = true;
  
  protected boolean m_axisChanged = false;
  
  protected int[][] m_drawnPoints;
  
  protected Font m_labelFont;
  
  protected FontMetrics m_labelMetrics = null;
  
  protected int m_JitterVal = 0;
  
  protected Random m_JRand = new Random(0L);
  
  protected double[][] m_pointLookup = (double[][])null;
  
  public Plot2D() {
    setProperties();
    setBackground(this.m_backgroundColour);
    this.m_InstanceInfoText.setFont(new Font("Monospaced", 0, 12));
    this.m_InstanceInfoText.setEditable(false);
    this.m_drawnPoints = new int[getWidth()][getHeight()];
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
  
  private void setProperties() {
    if (VisualizeUtils.VISUALIZE_PROPERTIES != null) {
      String str1 = getClass().getName();
      String str2 = str1 + ".axisColour";
      String str3 = str1 + ".backgroundColour";
      String str4 = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(str2);
      if (str4 != null)
        this.m_axisColour = VisualizeUtils.processColour(str4, this.m_axisColour); 
      String str5 = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(str3);
      if (str5 != null)
        this.m_backgroundColour = VisualizeUtils.processColour(str5, this.m_backgroundColour); 
    } 
  }
  
  private boolean checkPoints(double paramDouble1, double paramDouble2) {
    return !(paramDouble1 < 0.0D || paramDouble1 > (getSize()).width || paramDouble2 < 0.0D || paramDouble2 > (getSize()).height);
  }
  
  public void setPlotCompanion(Plot2DCompanion paramPlot2DCompanion) {
    this.m_plotCompanion = paramPlot2DCompanion;
  }
  
  public void setJitter(int paramInt) {
    if (this.m_plotInstances.numAttributes() > 0 && this.m_plotInstances.numInstances() > 0 && paramInt >= 0) {
      this.m_JitterVal = paramInt;
      this.m_JRand = new Random(this.m_JitterVal);
      this.m_drawnPoints = new int[this.m_XaxisEnd - this.m_XaxisStart + 1][this.m_YaxisEnd - this.m_YaxisStart + 1];
      updatePturb();
      repaint();
    } 
  }
  
  public void setColours(FastVector paramFastVector) {
    this.m_colorList = paramFastVector;
  }
  
  public void setXindex(int paramInt) {
    this.m_xIndex = paramInt;
    for (byte b = 0; b < this.m_plots.size(); b++)
      ((PlotData2D)this.m_plots.elementAt(b)).setXindex(this.m_xIndex); 
    determineBounds();
    if (this.m_JitterVal != 0)
      updatePturb(); 
    this.m_axisChanged = true;
    repaint();
  }
  
  public void setYindex(int paramInt) {
    this.m_yIndex = paramInt;
    for (byte b = 0; b < this.m_plots.size(); b++)
      ((PlotData2D)this.m_plots.elementAt(b)).setYindex(this.m_yIndex); 
    determineBounds();
    if (this.m_JitterVal != 0)
      updatePturb(); 
    this.m_axisChanged = true;
    repaint();
  }
  
  public void setCindex(int paramInt) {
    this.m_cIndex = paramInt;
    for (byte b = 0; b < this.m_plots.size(); b++)
      ((PlotData2D)this.m_plots.elementAt(b)).setCindex(this.m_cIndex); 
    determineBounds();
    this.m_axisChanged = true;
    repaint();
  }
  
  public FastVector getPlots() {
    return this.m_plots;
  }
  
  public PlotData2D getMasterPlot() {
    return this.m_masterPlot;
  }
  
  public double getMaxX() {
    return this.m_maxX;
  }
  
  public double getMaxY() {
    return this.m_maxY;
  }
  
  public double getMinX() {
    return this.m_minX;
  }
  
  public double getMinY() {
    return this.m_minY;
  }
  
  public double getMaxC() {
    return this.m_maxC;
  }
  
  public double getMinC() {
    return this.m_minC;
  }
  
  public void setInstances(Instances paramInstances) throws Exception {
    PlotData2D plotData2D = new PlotData2D(paramInstances);
    plotData2D.setPlotName("master plot");
    setMasterPlot(plotData2D);
  }
  
  public void setMasterPlot(PlotData2D paramPlotData2D) throws Exception {
    if (paramPlotData2D.m_plotInstances == null)
      throw new Exception("No instances in plot data!"); 
    removeAllPlots();
    this.m_masterPlot = paramPlotData2D;
    this.m_plots.addElement(this.m_masterPlot);
    this.m_plotInstances = this.m_masterPlot.m_plotInstances;
    this.m_xIndex = 0;
    this.m_yIndex = 0;
    this.m_cIndex = 0;
    determineBounds();
  }
  
  public void removeAllPlots() {
    this.m_masterPlot = null;
    this.m_plotInstances = null;
    this.m_plots = new FastVector();
    this.m_xIndex = 0;
    this.m_yIndex = 0;
    this.m_cIndex = 0;
  }
  
  public void addPlot(PlotData2D paramPlotData2D) throws Exception {
    if (paramPlotData2D.m_plotInstances == null)
      throw new Exception("No instances in plot data!"); 
    if (this.m_masterPlot != null) {
      if (!this.m_masterPlot.m_plotInstances.equalHeaders(paramPlotData2D.m_plotInstances))
        throw new Exception("Plot2D :Plot data's instances are incompatable  with master plot"); 
    } else {
      this.m_masterPlot = paramPlotData2D;
      this.m_plotInstances = this.m_masterPlot.m_plotInstances;
    } 
    this.m_plots.addElement(paramPlotData2D);
    setXindex(this.m_xIndex);
    setYindex(this.m_yIndex);
    setCindex(this.m_cIndex);
  }
  
  private void setFonts(Graphics paramGraphics) {
    if (this.m_labelMetrics == null) {
      this.m_labelFont = new Font("Monospaced", 0, 12);
      this.m_labelMetrics = paramGraphics.getFontMetrics(this.m_labelFont);
    } 
    paramGraphics.setFont(this.m_labelFont);
  }
  
  public void searchPoints(int paramInt1, int paramInt2, boolean paramBoolean) {
    if (this.m_masterPlot.m_plotInstances != null) {
      int i = 0;
      for (byte b1 = 0; b1 < this.m_masterPlot.m_plotInstances.numAttributes(); b1++) {
        if (this.m_masterPlot.m_plotInstances.attribute(b1).name().length() > i)
          i = this.m_masterPlot.m_plotInstances.attribute(b1).name().length(); 
      } 
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b2 = 0; b2 < this.m_plots.size(); b2++) {
        PlotData2D plotData2D = (PlotData2D)this.m_plots.elementAt(b2);
        for (byte b = 0; b < plotData2D.m_plotInstances.numInstances(); b++) {
          if (plotData2D.m_pointLookup[b][0] != Double.NEGATIVE_INFINITY) {
            double d1 = plotData2D.m_pointLookup[b][0] + plotData2D.m_pointLookup[b][2];
            double d2 = plotData2D.m_pointLookup[b][1] + plotData2D.m_pointLookup[b][3];
            double d3 = plotData2D.m_shapeSize[b];
            if (paramInt1 >= d1 - d3 && paramInt1 <= d1 + d3 && paramInt2 >= d2 - d3 && paramInt2 <= d2 + d3) {
              stringBuffer.append("\nPlot : " + plotData2D.m_plotName + "\nInstance: " + b + "\n");
              for (byte b3 = 0; b3 < plotData2D.m_plotInstances.numAttributes(); b3++) {
                for (byte b4 = 0; b4 < i - plotData2D.m_plotInstances.attribute(b3).name().length(); b4++)
                  stringBuffer.append(" "); 
                stringBuffer.append(plotData2D.m_plotInstances.attribute(b3).name());
                stringBuffer.append(" : ");
                if (plotData2D.m_plotInstances.instance(b).isMissing(b3)) {
                  stringBuffer.append("Missing");
                } else if (plotData2D.m_plotInstances.attribute(b3).isNominal()) {
                  stringBuffer.append(plotData2D.m_plotInstances.attribute(b3).value((int)plotData2D.m_plotInstances.instance(b).value(b3)));
                } else {
                  stringBuffer.append(plotData2D.m_plotInstances.instance(b).value(b3));
                } 
                stringBuffer.append("\n");
              } 
            } 
          } 
        } 
      } 
      if (stringBuffer.length() > 0)
        if (paramBoolean || this.m_InstanceInfo == null) {
          JTextArea jTextArea = new JTextArea();
          jTextArea.setFont(new Font("Monospaced", 0, 12));
          jTextArea.setEditable(false);
          jTextArea.setText(stringBuffer.toString());
          JFrame jFrame1 = new JFrame("Weka : Instance info");
          JFrame jFrame2 = this.m_InstanceInfo;
          jFrame1.addWindowListener(new WindowAdapter(this, paramBoolean, jFrame2, jFrame1) {
                private final boolean val$newFrame;
                
                private final JFrame val$testf;
                
                private final JFrame val$jf;
                
                private final Plot2D this$0;
                
                public void windowClosing(WindowEvent param1WindowEvent) {
                  if (!this.val$newFrame || this.val$testf == null)
                    this.this$0.m_InstanceInfo = null; 
                  this.val$jf.dispose();
                }
              });
          jFrame1.getContentPane().setLayout(new BorderLayout());
          jFrame1.getContentPane().add(new JScrollPane(jTextArea), "Center");
          jFrame1.pack();
          jFrame1.setSize(320, 400);
          jFrame1.setVisible(true);
          if (this.m_InstanceInfo == null) {
            this.m_InstanceInfo = jFrame1;
            this.m_InstanceInfoText = jTextArea;
          } 
        } else {
          this.m_InstanceInfoText.setText(stringBuffer.toString());
        }  
    } 
  }
  
  public void determineBounds() {
    this.m_minX = ((PlotData2D)this.m_plots.elementAt(0)).m_minX;
    this.m_maxX = ((PlotData2D)this.m_plots.elementAt(0)).m_maxX;
    this.m_minY = ((PlotData2D)this.m_plots.elementAt(0)).m_minY;
    this.m_maxY = ((PlotData2D)this.m_plots.elementAt(0)).m_maxY;
    this.m_minC = ((PlotData2D)this.m_plots.elementAt(0)).m_minC;
    this.m_maxC = ((PlotData2D)this.m_plots.elementAt(0)).m_maxC;
    for (byte b = 1; b < this.m_plots.size(); b++) {
      double d = ((PlotData2D)this.m_plots.elementAt(b)).m_minX;
      if (d < this.m_minX)
        this.m_minX = d; 
      d = ((PlotData2D)this.m_plots.elementAt(b)).m_maxX;
      if (d > this.m_maxX)
        this.m_maxX = d; 
      d = ((PlotData2D)this.m_plots.elementAt(b)).m_minY;
      if (d < this.m_minY)
        this.m_minY = d; 
      d = ((PlotData2D)this.m_plots.elementAt(b)).m_maxY;
      if (d > this.m_maxY)
        this.m_maxY = d; 
      d = ((PlotData2D)this.m_plots.elementAt(b)).m_minC;
      if (d < this.m_minC)
        this.m_minC = d; 
      d = ((PlotData2D)this.m_plots.elementAt(b)).m_maxC;
      if (d > this.m_maxC)
        this.m_maxC = d; 
    } 
    fillLookup();
    repaint();
  }
  
  public double convertToAttribX(double paramDouble) {
    double d1 = (this.m_XaxisEnd - this.m_XaxisStart);
    double d2 = (paramDouble - this.m_XaxisStart) * (this.m_maxX - this.m_minX) / d1;
    d2 += this.m_minX;
    return d2;
  }
  
  public double convertToAttribY(double paramDouble) {
    double d = (this.m_YaxisEnd - this.m_YaxisStart);
    null = (paramDouble - this.m_YaxisEnd) * (this.m_maxY - this.m_minY) / d;
    return -(null - this.m_minY);
  }
  
  int pturbX(double paramDouble1, double paramDouble2) {
    int i = 0;
    if (this.m_JitterVal > 0) {
      i = (int)(this.m_JitterVal * paramDouble2 / 2.0D);
      if (paramDouble1 + i < this.m_XaxisStart || paramDouble1 + i > this.m_XaxisEnd)
        i *= -1; 
    } 
    return i;
  }
  
  public double convertToPanelX(double paramDouble) {
    double d1 = (paramDouble - this.m_minX) / (this.m_maxX - this.m_minX);
    double d2 = d1 * (this.m_XaxisEnd - this.m_XaxisStart);
    d2 += this.m_XaxisStart;
    return d2;
  }
  
  int pturbY(double paramDouble1, double paramDouble2) {
    int i = 0;
    if (this.m_JitterVal > 0) {
      i = (int)(this.m_JitterVal * paramDouble2 / 2.0D);
      if (paramDouble1 + i < this.m_YaxisStart || paramDouble1 + i > this.m_YaxisEnd)
        i *= -1; 
    } 
    return i;
  }
  
  public double convertToPanelY(double paramDouble) {
    double d = (paramDouble - this.m_minY) / (this.m_maxY - this.m_minY);
    null = d * (this.m_YaxisEnd - this.m_YaxisStart);
    return this.m_YaxisEnd - null;
  }
  
  private static void drawX(Graphics paramGraphics, double paramDouble1, double paramDouble2, int paramInt) {
    paramGraphics.drawLine((int)(paramDouble1 - paramInt), (int)(paramDouble2 - paramInt), (int)(paramDouble1 + paramInt), (int)(paramDouble2 + paramInt));
    paramGraphics.drawLine((int)(paramDouble1 + paramInt), (int)(paramDouble2 - paramInt), (int)(paramDouble1 - paramInt), (int)(paramDouble2 + paramInt));
  }
  
  private static void drawPlus(Graphics paramGraphics, double paramDouble1, double paramDouble2, int paramInt) {
    paramGraphics.drawLine((int)(paramDouble1 - paramInt), (int)paramDouble2, (int)(paramDouble1 + paramInt), (int)paramDouble2);
    paramGraphics.drawLine((int)paramDouble1, (int)(paramDouble2 - paramInt), (int)paramDouble1, (int)(paramDouble2 + paramInt));
  }
  
  private static void drawDiamond(Graphics paramGraphics, double paramDouble1, double paramDouble2, int paramInt) {
    paramGraphics.drawLine((int)(paramDouble1 - paramInt), (int)paramDouble2, (int)paramDouble1, (int)(paramDouble2 - paramInt));
    paramGraphics.drawLine((int)paramDouble1, (int)(paramDouble2 - paramInt), (int)(paramDouble1 + paramInt), (int)paramDouble2);
    paramGraphics.drawLine((int)(paramDouble1 + paramInt), (int)paramDouble2, (int)paramDouble1, (int)(paramDouble2 + paramInt));
    paramGraphics.drawLine((int)paramDouble1, (int)(paramDouble2 + paramInt), (int)(paramDouble1 - paramInt), (int)paramDouble2);
  }
  
  private static void drawTriangleUp(Graphics paramGraphics, double paramDouble1, double paramDouble2, int paramInt) {
    paramGraphics.drawLine((int)paramDouble1, (int)(paramDouble2 - paramInt), (int)(paramDouble1 - paramInt), (int)(paramDouble2 + paramInt));
    paramGraphics.drawLine((int)(paramDouble1 - paramInt), (int)(paramDouble2 + paramInt), (int)(paramDouble1 + paramInt), (int)(paramDouble2 + paramInt));
    paramGraphics.drawLine((int)(paramDouble1 + paramInt), (int)(paramDouble2 + paramInt), (int)paramDouble1, (int)(paramDouble2 - paramInt));
  }
  
  private static void drawTriangleDown(Graphics paramGraphics, double paramDouble1, double paramDouble2, int paramInt) {
    paramGraphics.drawLine((int)paramDouble1, (int)(paramDouble2 + paramInt), (int)(paramDouble1 - paramInt), (int)(paramDouble2 - paramInt));
    paramGraphics.drawLine((int)(paramDouble1 - paramInt), (int)(paramDouble2 - paramInt), (int)(paramDouble1 + paramInt), (int)(paramDouble2 - paramInt));
    paramGraphics.drawLine((int)(paramDouble1 + paramInt), (int)(paramDouble2 - paramInt), (int)paramDouble1, (int)(paramDouble2 + paramInt));
  }
  
  protected static void drawDataPoint(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt1, int paramInt2, Graphics paramGraphics) {
    drawDataPoint(paramDouble1, paramDouble2, paramInt1, paramInt2, paramGraphics);
    paramGraphics.drawLine((int)paramDouble1, (int)paramDouble2, (int)paramDouble3, (int)paramDouble4);
  }
  
  protected static void drawDataPoint(double paramDouble1, double paramDouble2, int paramInt1, int paramInt2, Graphics paramGraphics) {
    int i;
    int j;
    Font font = new Font("Monospaced", 0, 12);
    FontMetrics fontMetrics = paramGraphics.getFontMetrics(font);
    if (paramInt1 == 0)
      paramInt1 = 1; 
    if (paramInt2 != 1000 && paramInt2 != 2000)
      paramInt2 %= 5; 
    switch (paramInt2) {
      case 0:
        drawX(paramGraphics, paramDouble1, paramDouble2, paramInt1);
        break;
      case 1:
        drawPlus(paramGraphics, paramDouble1, paramDouble2, paramInt1);
        break;
      case 2:
        drawDiamond(paramGraphics, paramDouble1, paramDouble2, paramInt1);
        break;
      case 3:
        drawTriangleUp(paramGraphics, paramDouble1, paramDouble2, paramInt1);
        break;
      case 4:
        drawTriangleDown(paramGraphics, paramDouble1, paramDouble2, paramInt1);
        break;
      case 1000:
        paramGraphics.drawRect((int)(paramDouble1 - paramInt1), (int)(paramDouble2 - paramInt1), paramInt1 * 2, paramInt1 * 2);
        break;
      case 2000:
        i = fontMetrics.getAscent();
        j = fontMetrics.stringWidth("M");
        paramGraphics.drawString("M", (int)(paramDouble1 - (j / 2)), (int)(paramDouble2 + (i / 2)));
        break;
    } 
  }
  
  private void updatePturb() {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < this.m_plots.size(); b++) {
      PlotData2D plotData2D = (PlotData2D)this.m_plots.elementAt(b);
      for (byte b1 = 0; b1 < plotData2D.m_plotInstances.numInstances(); b1++) {
        if (!plotData2D.m_plotInstances.instance(b1).isMissing(this.m_xIndex) && !plotData2D.m_plotInstances.instance(b1).isMissing(this.m_yIndex)) {
          if (this.m_JitterVal > 0) {
            d1 = this.m_JRand.nextGaussian();
            d2 = this.m_JRand.nextGaussian();
          } 
          plotData2D.m_pointLookup[b1][2] = pturbX(plotData2D.m_pointLookup[b1][0], d1);
          plotData2D.m_pointLookup[b1][3] = pturbY(plotData2D.m_pointLookup[b1][1], d2);
        } 
      } 
    } 
  }
  
  private void fillLookup() {
    for (byte b = 0; b < this.m_plots.size(); b++) {
      PlotData2D plotData2D = (PlotData2D)this.m_plots.elementAt(b);
      if (plotData2D.m_plotInstances.numInstances() > 0 && plotData2D.m_plotInstances.numAttributes() > 0)
        for (byte b1 = 0; b1 < plotData2D.m_plotInstances.numInstances(); b1++) {
          if (plotData2D.m_plotInstances.instance(b1).isMissing(this.m_xIndex) || plotData2D.m_plotInstances.instance(b1).isMissing(this.m_yIndex)) {
            plotData2D.m_pointLookup[b1][0] = Double.NEGATIVE_INFINITY;
            plotData2D.m_pointLookup[b1][1] = Double.NEGATIVE_INFINITY;
          } else {
            double d1 = convertToPanelX(plotData2D.m_plotInstances.instance(b1).value(this.m_xIndex));
            double d2 = convertToPanelY(plotData2D.m_plotInstances.instance(b1).value(this.m_yIndex));
            plotData2D.m_pointLookup[b1][0] = d1;
            plotData2D.m_pointLookup[b1][1] = d2;
          } 
        }  
    } 
  }
  
  private void paintData(Graphics paramGraphics) {
    for (byte b = 0; b < this.m_plots.size(); b++) {
      PlotData2D plotData2D = (PlotData2D)this.m_plots.elementAt(b);
      for (byte b1 = 0; b1 < plotData2D.m_plotInstances.numInstances(); b1++) {
        if (!plotData2D.m_plotInstances.instance(b1).isMissing(this.m_xIndex) && !plotData2D.m_plotInstances.instance(b1).isMissing(this.m_yIndex)) {
          double d1 = plotData2D.m_pointLookup[b1][0] + plotData2D.m_pointLookup[b1][2];
          double d2 = plotData2D.m_pointLookup[b1][1] + plotData2D.m_pointLookup[b1][3];
          double d3 = 0.0D;
          double d4 = 0.0D;
          if (b1 > 0) {
            d3 = plotData2D.m_pointLookup[b1 - 1][0] + plotData2D.m_pointLookup[b1 - 1][2];
            d4 = plotData2D.m_pointLookup[b1 - 1][1] + plotData2D.m_pointLookup[b1 - 1][3];
          } 
          int i = (int)d1 - this.m_XaxisStart;
          int j = (int)d2 - this.m_YaxisStart;
          if (i >= 0 && j >= 0 && (this.m_drawnPoints[i][j] == b1 || this.m_drawnPoints[i][j] == 0 || plotData2D.m_displayAllPoints == true)) {
            this.m_drawnPoints[i][j] = b1;
            if (plotData2D.m_plotInstances.attribute(this.m_cIndex).isNominal()) {
              Color color;
              if (plotData2D.m_plotInstances.attribute(this.m_cIndex).numValues() > this.m_colorList.size() && !plotData2D.m_useCustomColour)
                extendColourMap(plotData2D.m_plotInstances.attribute(this.m_cIndex).numValues()); 
              if (plotData2D.m_plotInstances.instance(b1).isMissing(this.m_cIndex)) {
                color = Color.gray;
              } else {
                int k = (int)plotData2D.m_plotInstances.instance(b1).value(this.m_cIndex);
                color = (Color)this.m_colorList.elementAt(k);
              } 
              if (!plotData2D.m_useCustomColour) {
                paramGraphics.setColor(color);
              } else {
                paramGraphics.setColor(plotData2D.m_customColour);
              } 
              if (plotData2D.m_plotInstances.instance(b1).isMissing(this.m_cIndex)) {
                if (plotData2D.m_connectPoints[b1] == true) {
                  drawDataPoint(d1, d2, d3, d4, plotData2D.m_shapeSize[b1], 2000, paramGraphics);
                } else {
                  drawDataPoint(d1, d2, plotData2D.m_shapeSize[b1], 2000, paramGraphics);
                } 
              } else if (plotData2D.m_shapeType[b1] == -1) {
                if (plotData2D.m_connectPoints[b1] == true) {
                  drawDataPoint(d1, d2, d3, d4, plotData2D.m_shapeSize[b1], b, paramGraphics);
                } else {
                  drawDataPoint(d1, d2, plotData2D.m_shapeSize[b1], b, paramGraphics);
                } 
              } else if (plotData2D.m_connectPoints[b1] == true) {
                drawDataPoint(d1, d2, d3, d4, plotData2D.m_shapeSize[b1], plotData2D.m_shapeType[b1], paramGraphics);
              } else {
                drawDataPoint(d1, d2, plotData2D.m_shapeSize[b1], plotData2D.m_shapeType[b1], paramGraphics);
              } 
            } else {
              Color color = null;
              if (!plotData2D.m_plotInstances.instance(b1).isMissing(this.m_cIndex)) {
                double d = (plotData2D.m_plotInstances.instance(b1).value(this.m_cIndex) - this.m_minC) / (this.m_maxC - this.m_minC);
                d = d * 240.0D + 15.0D;
                color = new Color((int)d, 150, (int)(255.0D - d));
              } else {
                color = Color.gray;
              } 
              if (!plotData2D.m_useCustomColour) {
                paramGraphics.setColor(color);
              } else {
                paramGraphics.setColor(plotData2D.m_customColour);
              } 
              if (plotData2D.m_plotInstances.instance(b1).isMissing(this.m_cIndex)) {
                if (plotData2D.m_connectPoints[b1] == true) {
                  drawDataPoint(d1, d2, d3, d4, plotData2D.m_shapeSize[b1], 2000, paramGraphics);
                } else {
                  drawDataPoint(d1, d2, plotData2D.m_shapeSize[b1], 2000, paramGraphics);
                } 
              } else if (plotData2D.m_shapeType[b1] == -1) {
                if (plotData2D.m_connectPoints[b1] == true) {
                  drawDataPoint(d1, d2, d3, d4, plotData2D.m_shapeSize[b1], b, paramGraphics);
                } else {
                  drawDataPoint(d1, d2, plotData2D.m_shapeSize[b1], b, paramGraphics);
                } 
              } else if (plotData2D.m_connectPoints[b1] == true) {
                drawDataPoint(d1, d2, d3, d4, plotData2D.m_shapeSize[b1], plotData2D.m_shapeType[b1], paramGraphics);
              } else {
                drawDataPoint(d1, d2, plotData2D.m_shapeSize[b1], plotData2D.m_shapeType[b1], paramGraphics);
              } 
            } 
          } 
        } 
      } 
    } 
  }
  
  private void paintAxis(Graphics paramGraphics) {
    setFonts(paramGraphics);
    int i = this.m_XaxisStart;
    int j = this.m_XaxisEnd;
    int k = this.m_YaxisStart;
    int m = this.m_YaxisEnd;
    this.m_plotResize = false;
    int n = getHeight();
    int i1 = getWidth();
    int i2 = this.m_labelMetrics.getAscent();
    int i3 = 0;
    int i4 = 0;
    byte b1 = 1;
    byte b2 = 1;
    byte b3 = 1;
    int i5 = (int)Math.abs(this.m_maxX);
    double d = Math.abs(this.m_maxX) - i5;
    byte b4 = (i5 > 0) ? (int)(Math.log(i5) / Math.log(10.0D)) : 1;
    b1 = (d > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.m_maxX)) / Math.log(10.0D)) + 2) : 1;
    if (b1 > VisualizeUtils.MAX_PRECISION)
      b1 = 1; 
    String str1 = Utils.doubleToString(this.m_maxX, b4 + 1 + b1, b1);
    i5 = (int)Math.abs(this.m_minX);
    d = Math.abs(this.m_minX) - i5;
    b4 = (i5 > 0) ? (int)(Math.log(i5) / Math.log(10.0D)) : 1;
    b2 = (d > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.m_minX)) / Math.log(10.0D)) + 2) : 1;
    if (b2 > VisualizeUtils.MAX_PRECISION)
      b2 = 1; 
    String str2 = Utils.doubleToString(this.m_minX, b4 + 1 + b2, b2);
    i3 = this.m_labelMetrics.stringWidth(str1);
    byte b5 = 1;
    byte b6 = 1;
    byte b7 = 1;
    i5 = (int)Math.abs(this.m_maxY);
    d = Math.abs(this.m_maxY) - i5;
    b4 = (i5 > 0) ? (int)(Math.log(i5) / Math.log(10.0D)) : 1;
    b5 = (d > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.m_maxY)) / Math.log(10.0D)) + 2) : 1;
    if (b5 > VisualizeUtils.MAX_PRECISION)
      b5 = 1; 
    String str3 = Utils.doubleToString(this.m_maxY, b4 + 1 + b5, b5);
    i5 = (int)Math.abs(this.m_minY);
    d = Math.abs(this.m_minY) - i5;
    b4 = (i5 > 0) ? (int)(Math.log(i5) / Math.log(10.0D)) : 1;
    b6 = (d > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(this.m_minY)) / Math.log(10.0D)) + 2) : 1;
    if (b6 > VisualizeUtils.MAX_PRECISION)
      b6 = 1; 
    String str4 = Utils.doubleToString(this.m_minY, b4 + 1 + b6, b6);
    if (this.m_plotInstances.attribute(this.m_yIndex).isNumeric()) {
      i4 = (this.m_labelMetrics.stringWidth(str3) > this.m_labelMetrics.stringWidth(str4)) ? this.m_labelMetrics.stringWidth(str3) : this.m_labelMetrics.stringWidth(str4);
      i4 += this.m_labelMetrics.stringWidth("M");
    } else {
      i4 = this.m_labelMetrics.stringWidth("MM");
    } 
    this.m_YaxisStart = 5;
    this.m_XaxisStart = 10 + i4;
    this.m_XaxisEnd = i1 - 5 - i3 / 2;
    this.m_YaxisEnd = n - 5 - 2 * i2 - 5;
    paramGraphics.setColor(this.m_axisColour);
    if (this.m_plotInstances.attribute(this.m_xIndex).isNumeric()) {
      if (i1 > 2 * i3) {
        paramGraphics.drawString(str1, this.m_XaxisEnd - i3 / 2, this.m_YaxisEnd + i2 + 5);
        i3 = this.m_labelMetrics.stringWidth(str2);
        paramGraphics.drawString(str2, this.m_XaxisStart - i3 / 2, this.m_YaxisEnd + i2 + 5);
        if (i1 > 3 * i3 && this.m_plotInstances.attribute(this.m_xIndex).isNumeric()) {
          double d1 = this.m_minX + (this.m_maxX - this.m_minX) / 2.0D;
          i5 = (int)Math.abs(d1);
          d = Math.abs(d1) - i5;
          b4 = (i5 > 0) ? (int)(Math.log(i5) / Math.log(10.0D)) : 1;
          b3 = (d > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(d1)) / Math.log(10.0D)) + 2) : 1;
          if (b3 > VisualizeUtils.MAX_PRECISION)
            b3 = 1; 
          String str = Utils.doubleToString(d1, b4 + 1 + b3, b3);
          int i6 = this.m_labelMetrics.stringWidth(str);
          double d2 = this.m_XaxisStart + (this.m_XaxisEnd - this.m_XaxisStart) / 2.0D;
          paramGraphics.drawString(str, (int)(d2 - i6 / 2.0D), this.m_YaxisEnd + i2 + 5);
          paramGraphics.drawLine((int)d2, this.m_YaxisEnd, (int)d2, this.m_YaxisEnd + 5);
        } 
      } 
    } else {
      int i6 = this.m_plotInstances.attribute(this.m_xIndex).numValues();
      int i7 = (i6 % 2 > 0) ? (i6 / 2 + 1) : (i6 / 2);
      int i8 = (this.m_XaxisEnd - this.m_XaxisStart) / i6;
      for (byte b = 0; b < i6; b++) {
        String str = this.m_plotInstances.attribute(this.m_xIndex).value(b);
        int i9 = this.m_labelMetrics.stringWidth(str);
        if (i9 > i8) {
          int i11 = i9 / str.length();
          int i10 = (i9 - i8) / i11;
          if (i10 == 0)
            i10 = 1; 
          str = str.substring(0, str.length() - i10);
          i9 = this.m_labelMetrics.stringWidth(str);
        } 
        if (b == 0) {
          paramGraphics.drawString(str, (int)convertToPanelX(b), this.m_YaxisEnd + i2 + 5);
        } else if (b == i6 - 1) {
          if (b % 2 == 0) {
            paramGraphics.drawString(str, this.m_XaxisEnd - i9, this.m_YaxisEnd + i2 + 5);
          } else {
            paramGraphics.drawString(str, this.m_XaxisEnd - i9, this.m_YaxisEnd + 2 * i2 + 5);
          } 
        } else if (b % 2 == 0) {
          paramGraphics.drawString(str, (int)convertToPanelX(b) - i9 / 2, this.m_YaxisEnd + i2 + 5);
        } else {
          paramGraphics.drawString(str, (int)convertToPanelX(b) - i9 / 2, this.m_YaxisEnd + 2 * i2 + 5);
        } 
        paramGraphics.drawLine((int)convertToPanelX(b), this.m_YaxisEnd, (int)convertToPanelX(b), this.m_YaxisEnd + 5);
      } 
    } 
    if (this.m_plotInstances.attribute(this.m_yIndex).isNumeric()) {
      if (n > 2 * i2) {
        paramGraphics.drawString(str3, this.m_XaxisStart - i4 - 5, this.m_YaxisStart + i2);
        paramGraphics.drawString(str4, this.m_XaxisStart - i4 - 5, this.m_YaxisEnd);
        if (i1 > 3 * i2 && this.m_plotInstances.attribute(this.m_yIndex).isNumeric()) {
          double d1 = this.m_minY + (this.m_maxY - this.m_minY) / 2.0D;
          i5 = (int)Math.abs(d1);
          d = Math.abs(d1) - i5;
          b4 = (i5 > 0) ? (int)(Math.log(i5) / Math.log(10.0D)) : 1;
          b7 = (d > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(d1)) / Math.log(10.0D)) + 2) : 1;
          if (b7 > VisualizeUtils.MAX_PRECISION)
            b7 = 1; 
          String str = Utils.doubleToString(d1, b4 + 1 + b7, b7);
          int i6 = this.m_labelMetrics.stringWidth(str);
          double d2 = this.m_YaxisStart + (this.m_YaxisEnd - this.m_YaxisStart) / 2.0D;
          paramGraphics.drawString(str, this.m_XaxisStart - i6 - 5 - 1, (int)(d2 + i2 / 2.0D));
          paramGraphics.drawLine(this.m_XaxisStart - 5, (int)d2, this.m_XaxisStart, (int)d2);
        } 
      } 
    } else {
      int i6 = this.m_plotInstances.attribute(this.m_yIndex).numValues();
      int i7 = (i6 % 2 == 0) ? (i6 / 2) : (i6 / 2 + 1);
      int i8 = (this.m_YaxisEnd - this.m_XaxisStart) / i7;
      int i9 = this.m_labelMetrics.stringWidth("M");
      for (byte b = 0; b < i6; b++) {
        if (i8 >= 2 * i2) {
          String str = this.m_plotInstances.attribute(this.m_yIndex).value(b);
          int i10 = (i8 / i2 > str.length()) ? str.length() : (i8 / i2);
          for (byte b8 = 0; b8 < i10; b8++) {
            String str5 = str.substring(b8, b8 + 1);
            if (str.charAt(b8) == '_' || str.charAt(b8) == '-')
              str5 = "|"; 
            if (b == 0) {
              paramGraphics.drawString(str5, this.m_XaxisStart - i9 - 5 - 1, (int)convertToPanelY(b) - (i10 - 1) * i2 + b8 * i2 + i2 / 2);
            } else if (b == i6 - 1) {
              if (b % 2 == 0) {
                paramGraphics.drawString(str5, this.m_XaxisStart - i9 - 5 - 1, (int)convertToPanelY(b) + b8 * i2 + i2 / 2);
              } else {
                paramGraphics.drawString(str5, this.m_XaxisStart - 2 * i9 - 5 - 1, (int)convertToPanelY(b) + b8 * i2 + i2 / 2);
              } 
            } else if (b % 2 == 0) {
              paramGraphics.drawString(str5, this.m_XaxisStart - i9 - 5 - 1, (int)convertToPanelY(b) - (i10 - 1) * i2 / 2 + b8 * i2 + i2 / 2);
            } else {
              paramGraphics.drawString(str5, this.m_XaxisStart - 2 * i9 - 5 - 1, (int)convertToPanelY(b) - (i10 - 1) * i2 / 2 + b8 * i2 + i2 / 2);
            } 
          } 
        } 
        paramGraphics.drawLine(this.m_XaxisStart - 5, (int)convertToPanelY(b), this.m_XaxisStart, (int)convertToPanelY(b));
      } 
    } 
    paramGraphics.drawLine(this.m_XaxisStart, this.m_YaxisStart, this.m_XaxisStart, this.m_YaxisEnd);
    paramGraphics.drawLine(this.m_XaxisStart, this.m_YaxisEnd, this.m_XaxisEnd, this.m_YaxisEnd);
    if (this.m_XaxisStart != i || this.m_XaxisEnd != j || this.m_YaxisStart != k || this.m_YaxisEnd != m)
      this.m_plotResize = true; 
  }
  
  private void extendColourMap(int paramInt) {
    for (int i = this.m_colorList.size(); i < paramInt; i++) {
      Color color = this.m_DefaultColors[i % 10];
      int j = i / 10;
      j *= 2;
      for (byte b = 0; b < j; b++)
        color = color.brighter(); 
      this.m_colorList.addElement(color);
    } 
  }
  
  public void paintComponent(Graphics paramGraphics) {
    super.paintComponent(paramGraphics);
    if (this.m_plotInstances != null && this.m_plotInstances.numInstances() > 0 && this.m_plotInstances.numAttributes() > 0) {
      if (this.m_plotCompanion != null)
        this.m_plotCompanion.prePlot(paramGraphics); 
      this.m_JRand = new Random(this.m_JitterVal);
      paintAxis(paramGraphics);
      if (this.m_axisChanged || this.m_plotResize) {
        int i = this.m_XaxisEnd - this.m_XaxisStart;
        int j = this.m_YaxisEnd - this.m_YaxisStart;
        if (i < 10)
          i = 10; 
        if (j < 10)
          j = 10; 
        this.m_drawnPoints = new int[i + 1][j + 1];
        fillLookup();
        this.m_plotResize = false;
        this.m_axisChanged = false;
      } 
      paintData(paramGraphics);
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length < 1) {
        System.err.println("Usage : weka.gui.visualize.Plot2D <dataset> [<dataset> <dataset>...]");
        System.exit(1);
      } 
      JFrame jFrame = new JFrame("Weka Explorer: Visualize");
      jFrame.setSize(500, 400);
      jFrame.getContentPane().setLayout(new BorderLayout());
      Plot2D plot2D = new Plot2D();
      jFrame.getContentPane().add(plot2D, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      plot2D.addMouseListener(new MouseAdapter(plot2D) {
            private final Plot2D val$p2;
            
            public void mouseClicked(MouseEvent param1MouseEvent) {
              if ((param1MouseEvent.getModifiers() & 0x10) == 16) {
                this.val$p2.searchPoints(param1MouseEvent.getX(), param1MouseEvent.getY(), false);
              } else {
                this.val$p2.searchPoints(param1MouseEvent.getX(), param1MouseEvent.getY(), true);
              } 
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
            plot2D.setMasterPlot(plotData2D);
            plot2D.setXindex(2);
            plot2D.setYindex(3);
            plot2D.setCindex(instances.classIndex());
          } else {
            plotData2D.setPlotName("Plot " + (b + 1));
            plotData2D.m_useCustomColour = true;
            plotData2D.m_customColour = (b % 2 == 0) ? Color.red : Color.blue;
            plot2D.addPlot(plotData2D);
          } 
        }  
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\Plot2D.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */