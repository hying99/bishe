package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.Logger;

public class StripChart extends JPanel implements ChartListener, InstanceListener, Visible, BeanCommon, UserRequestAcceptor, Serializable {
  protected Color[] m_colorList = new Color[] { Color.green, Color.red, Color.blue, Color.cyan, Color.pink, new Color(255, 0, 255), Color.orange, new Color(255, 0, 0), new Color(0, 255, 0), Color.white };
  
  private transient JFrame m_outputFrame = null;
  
  private transient StripPlotter m_plotPanel = null;
  
  private transient Image m_osi = null;
  
  private int m_iheight;
  
  private int m_iwidth;
  
  private double m_max = 1.0D;
  
  private double m_min = 0.0D;
  
  private boolean m_yScaleUpdate = false;
  
  private double m_oldMax;
  
  private double m_oldMin;
  
  private Font m_labelFont = new Font("Monospaced", 0, 10);
  
  private FontMetrics m_labelMetrics;
  
  private Vector m_legendText = new Vector();
  
  private JPanel m_scalePanel = new JPanel(this) {
      private final StripChart this$0;
      
      public void paintComponent(Graphics param1Graphics) {
        super.paintComponent(param1Graphics);
        if (this.this$0.m_labelMetrics == null)
          this.this$0.m_labelMetrics = param1Graphics.getFontMetrics(this.this$0.m_labelFont); 
        param1Graphics.setFont(this.this$0.m_labelFont);
        int i = this.this$0.m_labelMetrics.getAscent();
        String str = "" + this.this$0.m_max;
        param1Graphics.setColor(this.this$0.m_colorList[this.this$0.m_colorList.length - 1]);
        param1Graphics.drawString(str, 1, i - 2);
        str = "" + (this.this$0.m_min + (this.this$0.m_max - this.this$0.m_min) / 2.0D);
        param1Graphics.drawString(str, 1, getHeight() / 2 + i / 2);
        str = "" + this.this$0.m_min;
        param1Graphics.drawString(str, 1, getHeight() - 1);
      }
    };
  
  private JPanel m_legendPanel = new JPanel(this) {
      private final StripChart this$0;
      
      public void paintComponent(Graphics param1Graphics) {
        super.paintComponent(param1Graphics);
        if (this.this$0.m_labelMetrics == null)
          this.this$0.m_labelMetrics = param1Graphics.getFontMetrics(this.this$0.m_labelFont); 
        int i = this.this$0.m_labelMetrics.getAscent();
        byte b1 = 10;
        int j = i + 15;
        param1Graphics.setFont(this.this$0.m_labelFont);
        for (byte b2 = 0; b2 < this.this$0.m_legendText.size(); b2++) {
          String str = this.this$0.m_legendText.elementAt(b2);
          param1Graphics.setColor(this.this$0.m_colorList[b2 % this.this$0.m_colorList.length]);
          param1Graphics.drawString(str, b1, j);
          j += i;
        } 
        this.this$0.revalidate();
      }
    };
  
  private LinkedList m_dataList = new LinkedList();
  
  private double[] m_previousY = new double[1];
  
  private transient Thread m_updateHandler;
  
  protected BeanVisual m_visual = new BeanVisual("StripChart", "weka/gui/beans/icons/StripChart.gif", "weka/gui/beans/icons/StripChart_animated.gif");
  
  private Object m_listenee = null;
  
  private transient Logger m_log = null;
  
  private int m_xValFreq = 500;
  
  private int m_xCount = 0;
  
  private int m_refreshWidth = 1;
  
  private int m_refreshFrequency = 5;
  
  ChartEvent m_ce = new ChartEvent(this);
  
  double[] m_dataPoint = null;
  
  public StripChart() {
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
    initPlot();
  }
  
  public String globalInfo() {
    return "Visualize incremental classifier performance as a scrolling plot.";
  }
  
  public String xLabelFreqTipText() {
    return "Show x axis labels this often";
  }
  
  public void setXLabelFreq(int paramInt) {
    this.m_xValFreq = paramInt;
    setRefreshWidth();
  }
  
  public int getXLabelFreq() {
    return this.m_xValFreq;
  }
  
  public String refreshFreqTipText() {
    return "Plot every x'th data point";
  }
  
  public void setRefreshFreq(int paramInt) {
    this.m_refreshFrequency = paramInt;
    setRefreshWidth();
  }
  
  public int getRefreshFreq() {
    return this.m_refreshFrequency;
  }
  
  private void setRefreshWidth() {
    this.m_refreshWidth = 1;
    if (this.m_labelMetrics == null) {
      getGraphics().setFont(this.m_labelFont);
      this.m_labelMetrics = getGraphics().getFontMetrics(this.m_labelFont);
    } 
    int i = this.m_labelMetrics.stringWidth("99000");
    int j = getXLabelFreq() / getRefreshFreq();
    if (j < 1)
      j = 1; 
    if (j * this.m_refreshWidth < i + 5)
      this.m_refreshWidth *= (i + 5) / j + 1; 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    try {
      paramObjectInputStream.defaultReadObject();
      initPlot();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  private void initPlot() {
    this.m_plotPanel = new StripPlotter();
    this.m_plotPanel.setBackground(Color.black);
    this.m_scalePanel.setBackground(Color.black);
    this.m_legendPanel.setBackground(Color.black);
    this.m_xCount = 0;
  }
  
  private void startHandler() {
    if (this.m_updateHandler == null) {
      this.m_updateHandler = new Thread(this) {
          private double[] dataPoint;
          
          private final StripChart this$0;
          
          public void run() {
            while (true) {
              if (this.this$0.m_outputFrame != null) {
                synchronized (this.this$0.m_dataList) {
                  while (this.this$0.m_dataList.isEmpty()) {
                    try {
                      this.this$0.m_dataList.wait();
                    } catch (InterruptedException interruptedException) {
                      return;
                    } 
                  } 
                  this.dataPoint = this.this$0.m_dataList.remove(0);
                } 
                if (this.this$0.m_outputFrame != null)
                  this.this$0.updateChart(this.dataPoint); 
              } 
            } 
          }
        };
      this.m_updateHandler.start();
    } 
  }
  
  public void showChart() {
    if (this.m_outputFrame == null) {
      this.m_outputFrame = new JFrame("Strip Chart");
      this.m_outputFrame.getContentPane().setLayout(new BorderLayout());
      this.m_outputFrame.getContentPane().add(this.m_legendPanel, "West");
      this.m_outputFrame.getContentPane().add(this.m_plotPanel, "Center");
      this.m_outputFrame.getContentPane().add(this.m_scalePanel, "East");
      this.m_legendPanel.setMinimumSize(new Dimension(100, getHeight()));
      this.m_legendPanel.setPreferredSize(new Dimension(100, getHeight()));
      this.m_scalePanel.setMinimumSize(new Dimension(30, getHeight()));
      this.m_scalePanel.setPreferredSize(new Dimension(30, getHeight()));
      Font font = new Font("Monospaced", 0, 12);
      this.m_legendPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.gray, Color.darkGray), "Legend", 2, 0, font, Color.blue));
      this.m_outputFrame.addWindowListener(new WindowAdapter(this) {
            private final StripChart this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              if (this.this$0.m_updateHandler != null) {
                System.err.println("Interrupting");
                this.this$0.m_updateHandler.interrupt();
                this.this$0.m_updateHandler = null;
              } 
              synchronized (this.this$0.m_dataList) {
                this.this$0.m_dataList = new LinkedList();
              } 
              this.this$0.m_outputFrame.dispose();
              this.this$0.m_outputFrame = null;
            }
          });
      this.m_outputFrame.pack();
      this.m_outputFrame.setSize(600, 150);
      this.m_outputFrame.setResizable(false);
      this.m_outputFrame.setVisible(true);
      int i = this.m_plotPanel.getWidth();
      int j = this.m_plotPanel.getHeight();
      this.m_osi = this.m_plotPanel.createImage(i, j);
      Graphics graphics = this.m_osi.getGraphics();
      graphics.fillRect(0, 0, i, j);
      this.m_previousY[0] = -1.0D;
      setRefreshWidth();
      if (this.m_updateHandler == null) {
        System.err.println("Starting handler");
        startHandler();
      } 
    } else {
      this.m_outputFrame.toFront();
    } 
  }
  
  private int convertToPanelY(double paramDouble) {
    int i = this.m_plotPanel.getHeight();
    double d = (paramDouble - this.m_min) / (this.m_max - this.m_min);
    d *= i;
    d = i - d;
    return (int)d;
  }
  
  protected void updateChart(double[] paramArrayOfdouble) {
    if (this.m_previousY[0] == -1.0D) {
      int i = this.m_plotPanel.getWidth();
      int j = this.m_plotPanel.getHeight();
      this.m_osi = this.m_plotPanel.createImage(i, j);
      Graphics graphics = this.m_osi.getGraphics();
      graphics.fillRect(0, 0, i, j);
      this.m_previousY[0] = convertToPanelY(0.0D);
      this.m_iheight = j;
      this.m_iwidth = i;
    } 
    if (paramArrayOfdouble.length - 1 != this.m_previousY.length) {
      this.m_previousY = new double[paramArrayOfdouble.length - 1];
      for (byte b1 = 0; b1 < paramArrayOfdouble.length - 1; b1++)
        this.m_previousY[b1] = convertToPanelY(0.0D); 
    } 
    Graphics graphics1 = this.m_osi.getGraphics();
    Graphics graphics2 = this.m_plotPanel.getGraphics();
    graphics1.copyArea(this.m_refreshWidth, 0, this.m_iwidth - this.m_refreshWidth, this.m_iheight, -this.m_refreshWidth, 0);
    graphics1.setColor(Color.black);
    graphics1.fillRect(this.m_iwidth - this.m_refreshWidth, 0, this.m_iwidth, this.m_iheight);
    if (this.m_yScaleUpdate) {
      String str1 = numToString(this.m_oldMax);
      String str2 = numToString(this.m_oldMin);
      String str3 = numToString((this.m_oldMax - this.m_oldMin) / 2.0D);
      if (this.m_labelMetrics == null)
        this.m_labelMetrics = graphics2.getFontMetrics(this.m_labelFont); 
      graphics1.setFont(this.m_labelFont);
      int i = this.m_labelMetrics.stringWidth(str1);
      int j = this.m_labelMetrics.stringWidth(str2);
      int k = this.m_labelMetrics.stringWidth(str3);
      int m = this.m_labelMetrics.getAscent();
      graphics1.setColor(this.m_colorList[this.m_colorList.length - 1]);
      graphics1.drawString(str1, this.m_iwidth - i, m - 2);
      graphics1.drawString(str3, this.m_iwidth - k, this.m_iheight / 2 + m / 2);
      graphics1.drawString(str2, this.m_iwidth - j, this.m_iheight - 1);
      this.m_yScaleUpdate = false;
    } 
    for (byte b = 0; b < paramArrayOfdouble.length - 1; b++) {
      graphics1.setColor(this.m_colorList[b % this.m_colorList.length]);
      double d = convertToPanelY(paramArrayOfdouble[b]);
      graphics1.drawLine(this.m_iwidth - this.m_refreshWidth, (int)this.m_previousY[b], this.m_iwidth - 1, (int)d);
      this.m_previousY[b] = d;
      if (paramArrayOfdouble[paramArrayOfdouble.length - 1] % this.m_xValFreq == 0.0D) {
        String str = numToString(paramArrayOfdouble[b]);
        if (this.m_labelMetrics == null)
          this.m_labelMetrics = graphics2.getFontMetrics(this.m_labelFont); 
        int i = this.m_labelMetrics.getAscent();
        if (d - i < 0.0D)
          d += i; 
        int j = this.m_labelMetrics.stringWidth(str);
        graphics1.setFont(this.m_labelFont);
        graphics1.drawString(str, this.m_iwidth - j, (int)d);
      } 
    } 
    if (paramArrayOfdouble[paramArrayOfdouble.length - 1] % this.m_xValFreq == 0.0D) {
      String str = "" + (int)paramArrayOfdouble[paramArrayOfdouble.length - 1];
      graphics1.setColor(this.m_colorList[this.m_colorList.length - 1]);
      int i = this.m_labelMetrics.stringWidth(str);
      graphics1.setFont(this.m_labelFont);
      graphics1.drawString(str, this.m_iwidth - i, this.m_iheight - 1);
    } 
    graphics2.drawImage(this.m_osi, 0, 0, this.m_plotPanel);
  }
  
  private static String numToString(double paramDouble) {
    byte b1 = 1;
    int i = (int)Math.abs(paramDouble);
    double d = Math.abs(paramDouble) - i;
    byte b2 = (i > 0) ? (int)(Math.log(i) / Math.log(10.0D)) : 1;
    b1 = (d > 0.0D) ? ((int)Math.abs(Math.log(Math.abs(paramDouble)) / Math.log(10.0D)) + 2) : 1;
    if (b1 > 5)
      b1 = 1; 
    return Utils.doubleToString(paramDouble, b2 + 1 + b1, b1);
  }
  
  public void acceptInstance(InstanceEvent paramInstanceEvent) {
    if (paramInstanceEvent.getStatus() == 0) {
      Instances instances = paramInstanceEvent.getStructure();
      this.m_legendText = new Vector();
      this.m_max = 1.0D;
      this.m_min = 0.0D;
      byte b1 = 0;
      for (b1 = 0; b1 < instances.numAttributes(); b1++) {
        if (b1 > 10) {
          b1--;
          break;
        } 
        this.m_legendText.addElement(instances.attribute(b1).name());
        this.m_legendPanel.repaint();
        this.m_scalePanel.repaint();
      } 
      this.m_dataPoint = new double[b1];
      this.m_xCount = 0;
      return;
    } 
    Instance instance = paramInstanceEvent.getInstance();
    for (byte b = 0; b < this.m_dataPoint.length; b++) {
      if (!instance.isMissing(b))
        this.m_dataPoint[b] = instance.value(b); 
    } 
    acceptDataPoint(this.m_dataPoint);
    this.m_xCount++;
  }
  
  public void acceptDataPoint(ChartEvent paramChartEvent) {
    if (paramChartEvent.getReset()) {
      this.m_xCount = 0;
      this.m_max = 1.0D;
      this.m_min = 0.0D;
    } 
    if (this.m_outputFrame != null) {
      boolean bool = false;
      if ((((paramChartEvent.getLegendText() != null) ? 1 : 0) & ((paramChartEvent.getLegendText() != this.m_legendText) ? 1 : 0)) != 0) {
        this.m_legendText = paramChartEvent.getLegendText();
        bool = true;
      } 
      if (paramChartEvent.getMin() != this.m_min || paramChartEvent.getMax() != this.m_max) {
        this.m_oldMax = this.m_max;
        this.m_oldMin = this.m_min;
        this.m_max = paramChartEvent.getMax();
        this.m_min = paramChartEvent.getMin();
        bool = true;
        this.m_yScaleUpdate = true;
      } 
      if (bool) {
        this.m_legendPanel.repaint();
        this.m_scalePanel.repaint();
      } 
      acceptDataPoint(paramChartEvent.getDataPoint());
    } 
    this.m_xCount++;
  }
  
  public void acceptDataPoint(double[] paramArrayOfdouble) {
    if (this.m_outputFrame != null && this.m_xCount % this.m_refreshFrequency == 0) {
      double[] arrayOfDouble = new double[paramArrayOfdouble.length + 1];
      arrayOfDouble[arrayOfDouble.length - 1] = this.m_xCount;
      System.arraycopy(paramArrayOfdouble, 0, arrayOfDouble, 0, paramArrayOfdouble.length);
      for (byte b = 0; b < paramArrayOfdouble.length; b++) {
        if (paramArrayOfdouble[b] < this.m_min) {
          this.m_oldMin = this.m_min;
          this.m_min = paramArrayOfdouble[b];
          this.m_yScaleUpdate = true;
        } 
        if (paramArrayOfdouble[b] > this.m_max) {
          this.m_oldMax = this.m_max;
          this.m_max = paramArrayOfdouble[b];
          this.m_yScaleUpdate = true;
        } 
      } 
      if (this.m_yScaleUpdate) {
        this.m_scalePanel.repaint();
        this.m_yScaleUpdate = false;
      } 
      synchronized (this.m_dataList) {
        this.m_dataList.add(this.m_dataList.size(), arrayOfDouble);
        this.m_dataList.notifyAll();
      } 
    } 
  }
  
  public void setVisual(BeanVisual paramBeanVisual) {
    this.m_visual = paramBeanVisual;
  }
  
  public BeanVisual getVisual() {
    return this.m_visual;
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/StripChart.gif", "weka/gui/beans/icons/StripChart_animated.gif");
  }
  
  public void stop() {}
  
  public void setLog(Logger paramLogger) {
    this.m_log = paramLogger;
  }
  
  public boolean connectionAllowed(String paramString) {
    return (this.m_listenee == null);
  }
  
  public void connectionNotification(String paramString, Object paramObject) {
    if (connectionAllowed(paramString))
      this.m_listenee = paramObject; 
  }
  
  public void disconnectionNotification(String paramString, Object paramObject) {
    this.m_listenee = null;
  }
  
  public Enumeration enumerateRequests() {
    Vector vector = new Vector(0);
    vector.addElement("Show chart");
    return vector.elements();
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Show chart") == 0) {
      showChart();
    } else {
      throw new IllegalArgumentException(paramString + " not supported (StripChart)");
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Weka Knowledge Flow : StipChart");
      jFrame.getContentPane().setLayout(new BorderLayout());
      StripChart stripChart = new StripChart();
      jFrame.getContentPane().add(stripChart, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      stripChart.showChart();
      Random random = new Random(1L);
      for (byte b = 0; b < 'ϼ'; b++) {
        double[] arrayOfDouble = new double[1];
        arrayOfDouble[0] = random.nextDouble();
        stripChart.acceptDataPoint(arrayOfDouble);
      } 
      System.err.println("Done sending data");
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  private class StripPlotter extends JPanel {
    private final StripChart this$0;
    
    private StripPlotter(StripChart this$0) {
      StripChart.this = StripChart.this;
    }
    
    public void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      if (StripChart.this.m_osi != null)
        param1Graphics.drawImage(StripChart.this.m_osi, 0, 0, this); 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\StripChart.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */