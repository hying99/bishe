package weka.gui.boundaryvisualizer;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.Random;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class BoundaryPanel extends JPanel {
  public static final Color[] DEFAULT_COLORS = new Color[] { Color.red, Color.green, Color.blue, new Color(0, 255, 255), new Color(255, 0, 255), new Color(255, 255, 0), new Color(255, 255, 255), new Color(0, 0, 0) };
  
  protected FastVector m_Colors = new FastVector();
  
  protected Instances m_trainingData;
  
  protected Classifier m_classifier;
  
  protected DataGenerator m_dataGenerator;
  
  private int m_classIndex = -1;
  
  protected int m_xAttribute;
  
  protected int m_yAttribute;
  
  protected double m_minX;
  
  protected double m_minY;
  
  protected double m_maxX;
  
  protected double m_maxY;
  
  private double m_rangeX;
  
  private double m_rangeY;
  
  protected double m_pixHeight;
  
  protected double m_pixWidth;
  
  protected Image m_osi = null;
  
  protected int m_panelWidth;
  
  protected int m_panelHeight;
  
  protected int m_numOfSamplesPerRegion = 2;
  
  protected int m_numOfSamplesPerGenerator;
  
  protected double m_samplesBase = 2.0D;
  
  private Vector m_listeners = new Vector();
  
  private PlotPanel m_plotPanel = new PlotPanel(this);
  
  private Thread m_plotThread = null;
  
  protected boolean m_stopPlotting = false;
  
  protected boolean m_stopReplotting = false;
  
  private Double m_dummy = new Double(1.0D);
  
  private boolean m_pausePlotting = false;
  
  private int m_size = 1;
  
  private boolean m_initialTiling;
  
  private Random m_random = null;
  
  protected double[][][] m_probabilityCache;
  
  protected boolean m_plotTrainingData = true;
  
  public BoundaryPanel(int paramInt1, int paramInt2) {
    ToolTipManager.sharedInstance().setDismissDelay(2147483647);
    this.m_panelWidth = paramInt1;
    this.m_panelHeight = paramInt2;
    setLayout(new BorderLayout());
    this.m_plotPanel.setMinimumSize(new Dimension(this.m_panelWidth, this.m_panelHeight));
    this.m_plotPanel.setPreferredSize(new Dimension(this.m_panelWidth, this.m_panelHeight));
    this.m_plotPanel.setMaximumSize(new Dimension(this.m_panelWidth, this.m_panelHeight));
    add(this.m_plotPanel, "Center");
    setPreferredSize(this.m_plotPanel.getPreferredSize());
    setMaximumSize(this.m_plotPanel.getMaximumSize());
    setMinimumSize(this.m_plotPanel.getMinimumSize());
    this.m_random = new Random(1L);
    for (byte b = 0; b < DEFAULT_COLORS.length; b++)
      this.m_Colors.addElement(new Color(DEFAULT_COLORS[b].getRed(), DEFAULT_COLORS[b].getGreen(), DEFAULT_COLORS[b].getBlue())); 
    this.m_probabilityCache = new double[this.m_panelHeight][this.m_panelWidth][];
  }
  
  public void setNumSamplesPerRegion(int paramInt) {
    this.m_numOfSamplesPerRegion = paramInt;
  }
  
  public int getNumSamplesPerRegion() {
    return this.m_numOfSamplesPerRegion;
  }
  
  public void setGeneratorSamplesBase(double paramDouble) {
    this.m_samplesBase = paramDouble;
  }
  
  public double getGeneratorSamplesBase() {
    return this.m_samplesBase;
  }
  
  protected void initialize() {
    int i = this.m_plotPanel.getWidth();
    int j = this.m_plotPanel.getHeight();
    this.m_osi = this.m_plotPanel.createImage(i, j);
    Graphics graphics = this.m_osi.getGraphics();
    graphics.fillRect(0, 0, i, j);
  }
  
  public void stopPlotting() {
    this.m_stopPlotting = true;
  }
  
  protected void computeMinMaxAtts() {
    this.m_minX = Double.MAX_VALUE;
    this.m_minY = Double.MAX_VALUE;
    this.m_maxX = Double.MIN_VALUE;
    this.m_maxY = Double.MIN_VALUE;
    for (byte b = 0; b < this.m_trainingData.numInstances(); b++) {
      Instance instance = this.m_trainingData.instance(b);
      double d1 = instance.value(this.m_xAttribute);
      double d2 = instance.value(this.m_yAttribute);
      if (d1 != Instance.missingValue()) {
        if (d1 < this.m_minX)
          this.m_minX = d1; 
        if (d1 > this.m_maxX)
          this.m_maxX = d1; 
      } 
      if (d2 != Instance.missingValue()) {
        if (d2 < this.m_minY)
          this.m_minY = d2; 
        if (d2 > this.m_maxY)
          this.m_maxY = d2; 
      } 
    } 
    this.m_rangeX = this.m_maxX - this.m_minX;
    this.m_rangeY = this.m_maxY - this.m_minY;
    this.m_pixWidth = this.m_rangeX / this.m_panelWidth;
    this.m_pixHeight = this.m_rangeY / this.m_panelHeight;
  }
  
  private double getRandomX(int paramInt) {
    double d = this.m_minX + paramInt * this.m_pixWidth;
    return d + this.m_random.nextDouble() * this.m_pixWidth;
  }
  
  private double getRandomY(int paramInt) {
    double d = this.m_minY + paramInt * this.m_pixHeight;
    return d + this.m_random.nextDouble() * this.m_pixHeight;
  }
  
  public void start() throws Exception {
    this.m_numOfSamplesPerGenerator = (int)Math.pow(this.m_samplesBase, (this.m_trainingData.numAttributes() - 3));
    this.m_stopReplotting = true;
    if (this.m_trainingData == null)
      throw new Exception("No training data set (BoundaryPanel)"); 
    if (this.m_classifier == null)
      throw new Exception("No classifier set (BoundaryPanel)"); 
    if (this.m_dataGenerator == null)
      throw new Exception("No data generator set (BoundaryPanel)"); 
    if (this.m_trainingData.attribute(this.m_xAttribute).isNominal() || this.m_trainingData.attribute(this.m_yAttribute).isNominal())
      throw new Exception("Visualization dimensions must be numeric (BoundaryPanel)"); 
    computeMinMaxAtts();
    if (this.m_plotThread == null) {
      this.m_plotThread = new PlotThread(this);
      this.m_plotThread.setPriority(1);
      this.m_plotThread.start();
    } 
  }
  
  protected void plotTrainingData() {
    Graphics2D graphics2D = (Graphics2D)this.m_osi.getGraphics();
    Graphics graphics = this.m_plotPanel.getGraphics();
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < this.m_trainingData.numInstances(); b++) {
      if (!this.m_trainingData.instance(b).isMissing(this.m_xAttribute) && !this.m_trainingData.instance(b).isMissing(this.m_yAttribute)) {
        d1 = this.m_trainingData.instance(b).value(this.m_xAttribute);
        d2 = this.m_trainingData.instance(b).value(this.m_yAttribute);
        int i = convertToPanelX(d1);
        int j = convertToPanelY(d2);
        Color color = (Color)this.m_Colors.elementAt((int)this.m_trainingData.instance(b).value(this.m_classIndex) % this.m_Colors.size());
        if (color.equals(Color.white)) {
          graphics2D.setColor(Color.black);
        } else {
          graphics2D.setColor(Color.white);
        } 
        graphics2D.fillOval(i - 3, j - 3, 7, 7);
        graphics2D.setColor(color);
        graphics2D.fillOval(i - 2, j - 2, 5, 5);
      } 
    } 
    graphics.drawImage(this.m_osi, 0, 0, this.m_plotPanel);
  }
  
  private int convertToPanelX(double paramDouble) {
    double d = (paramDouble - this.m_minX) / this.m_rangeX;
    d *= this.m_panelWidth;
    return (int)d;
  }
  
  private int convertToPanelY(double paramDouble) {
    double d = (paramDouble - this.m_minY) / this.m_rangeY;
    d *= this.m_panelHeight;
    d = this.m_panelHeight - d;
    return (int)d;
  }
  
  private double convertFromPanelX(double paramDouble) {
    paramDouble /= this.m_panelWidth;
    paramDouble *= this.m_rangeX;
    return paramDouble + this.m_minX;
  }
  
  private double convertFromPanelY(double paramDouble) {
    paramDouble = this.m_panelHeight - paramDouble;
    paramDouble /= this.m_panelHeight;
    paramDouble *= this.m_rangeY;
    return paramDouble + this.m_minY;
  }
  
  protected void plotPoint(int paramInt1, int paramInt2, double[] paramArrayOfdouble, boolean paramBoolean) {
    plotPoint(paramInt1, paramInt2, 1, 1, paramArrayOfdouble, paramBoolean);
  }
  
  private void plotPoint(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfdouble, boolean paramBoolean) {
    Graphics graphics = this.m_osi.getGraphics();
    if (paramBoolean) {
      graphics.setXORMode(Color.white);
      graphics.drawLine(0, paramInt2, this.m_panelWidth - 1, paramInt2);
      update();
      graphics.drawLine(0, paramInt2, this.m_panelWidth - 1, paramInt2);
    } 
    graphics.setPaintMode();
    float[] arrayOfFloat1 = new float[3];
    float[] arrayOfFloat2 = new float[3];
    byte b;
    for (b = 0; b < paramArrayOfdouble.length; b++) {
      Color color = (Color)this.m_Colors.elementAt(b % this.m_Colors.size());
      color.getRGBColorComponents(arrayOfFloat2);
      for (byte b1 = 0; b1 < 3; b1++)
        arrayOfFloat1[b1] = (float)(arrayOfFloat1[b1] + paramArrayOfdouble[b] * arrayOfFloat2[b1]); 
    } 
    for (b = 0; b < 3; b++) {
      if (arrayOfFloat1[b] < 0.0F) {
        arrayOfFloat1[b] = 0.0F;
      } else if (arrayOfFloat1[b] > 1.0F) {
        arrayOfFloat1[b] = 1.0F;
      } 
    } 
    graphics.setColor(new Color(arrayOfFloat1[0], arrayOfFloat1[1], arrayOfFloat1[2]));
    graphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  private void update() {
    Graphics graphics = this.m_plotPanel.getGraphics();
    graphics.drawImage(this.m_osi, 0, 0, this.m_plotPanel);
  }
  
  public void setTrainingData(Instances paramInstances) throws Exception {
    this.m_trainingData = paramInstances;
    if (this.m_trainingData.classIndex() < 0)
      throw new Exception("No class attribute set (BoundaryPanel)"); 
    this.m_classIndex = this.m_trainingData.classIndex();
  }
  
  public void addActionListener(ActionListener paramActionListener) {
    this.m_listeners.add(paramActionListener);
  }
  
  public void removeActionListener(ActionListener paramActionListener) {
    this.m_listeners.removeElement(paramActionListener);
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_classifier = paramClassifier;
  }
  
  public void setDataGenerator(DataGenerator paramDataGenerator) {
    this.m_dataGenerator = paramDataGenerator;
  }
  
  public void setXAttribute(int paramInt) throws Exception {
    if (this.m_trainingData == null)
      throw new Exception("No training data set (BoundaryPanel)"); 
    if (paramInt < 0 || paramInt > this.m_trainingData.numAttributes())
      throw new Exception("X attribute out of range (BoundaryPanel)"); 
    if (this.m_trainingData.attribute(paramInt).isNominal())
      throw new Exception("Visualization dimensions must be numeric (BoundaryPanel)"); 
    if (this.m_trainingData.numDistinctValues(paramInt) < 2)
      throw new Exception("Too few distinct values for X attribute (BoundaryPanel)"); 
    this.m_xAttribute = paramInt;
  }
  
  public void setYAttribute(int paramInt) throws Exception {
    if (this.m_trainingData == null)
      throw new Exception("No training data set (BoundaryPanel)"); 
    if (paramInt < 0 || paramInt > this.m_trainingData.numAttributes())
      throw new Exception("X attribute out of range (BoundaryPanel)"); 
    if (this.m_trainingData.attribute(paramInt).isNominal())
      throw new Exception("Visualization dimensions must be numeric (BoundaryPanel)"); 
    if (this.m_trainingData.numDistinctValues(paramInt) < 2)
      throw new Exception("Too few distinct values for Y attribute (BoundaryPanel)"); 
    this.m_yAttribute = paramInt;
  }
  
  public void setColors(FastVector paramFastVector) {
    synchronized (this.m_Colors) {
      this.m_Colors = paramFastVector;
    } 
    replot();
  }
  
  public void setPlotTrainingData(boolean paramBoolean) {
    this.m_plotTrainingData = paramBoolean;
  }
  
  public boolean getPlotTrainingData() {
    return this.m_plotTrainingData;
  }
  
  public FastVector getColors() {
    return this.m_Colors;
  }
  
  public void replot() {
    if (this.m_probabilityCache[0][0] == null)
      return; 
    this.m_stopReplotting = true;
    this.m_pausePlotting = true;
    try {
      Thread.sleep(300L);
    } catch (Exception exception) {}
    Thread thread = new Thread(this) {
        private final BoundaryPanel this$0;
        
        public void run() {
          this.this$0.m_stopReplotting = false;
          int i = this.this$0.m_size / 2;
          int j;
          label52: for (j = 0; j < this.this$0.m_panelHeight; j += this.this$0.m_size) {
            int k = 0;
            while (k < this.this$0.m_panelWidth) {
              if (this.this$0.m_probabilityCache[j][k] != null) {
                if (this.this$0.m_stopReplotting)
                  break label52; 
                boolean bool = (k == 0 && j % 2 == 0) ? true : false;
                if (j < this.this$0.m_panelHeight && k < this.this$0.m_panelWidth)
                  if (this.this$0.m_initialTiling || this.this$0.m_size == 1) {
                    if (this.this$0.m_probabilityCache[j][k] == null)
                      break label52; 
                    this.this$0.plotPoint(k, j, this.this$0.m_size, this.this$0.m_size, this.this$0.m_probabilityCache[j][k], bool);
                  } else {
                    if (this.this$0.m_probabilityCache[j + i][k] == null)
                      break label52; 
                    this.this$0.plotPoint(k, j + i, i, i, this.this$0.m_probabilityCache[j + i][k], bool);
                    if (this.this$0.m_probabilityCache[j + i][k + i] == null)
                      break label52; 
                    this.this$0.plotPoint(k + i, j + i, i, i, this.this$0.m_probabilityCache[j + i][k + i], bool);
                    if (this.this$0.m_probabilityCache[j][k + i] == null)
                      break label52; 
                    this.this$0.plotPoint(k + i, j, i, i, this.this$0.m_probabilityCache[j + i][k], bool);
                  }  
                k += this.this$0.m_size;
                continue;
              } 
              break label52;
            } 
          } 
          this.this$0.update();
          if (this.this$0.m_plotTrainingData)
            this.this$0.plotTrainingData(); 
          this.this$0.m_pausePlotting = false;
          if (!this.this$0.m_stopPlotting)
            synchronized (this.this$0.m_dummy) {
              this.this$0.m_dummy.notifyAll();
            }  
        }
      };
    thread.start();
  }
  
  protected void saveImage(String paramString) {
    try {
      BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(paramString));
      JPEGImageEncoder jPEGImageEncoder = JPEGCodec.createJPEGEncoder(bufferedOutputStream);
      BufferedImage bufferedImage = new BufferedImage(this.m_panelWidth, this.m_panelHeight, 1);
      Graphics2D graphics2D = bufferedImage.createGraphics();
      graphics2D.drawImage(this.m_osi, 0, 0, this.m_panelWidth, this.m_panelHeight, null);
      JPEGEncodeParam jPEGEncodeParam = jPEGImageEncoder.getDefaultJPEGEncodeParam(bufferedImage);
      jPEGEncodeParam.setQuality(1.0F, false);
      jPEGImageEncoder.setJPEGEncodeParam(jPEGEncodeParam);
      jPEGImageEncoder.encode(bufferedImage);
      bufferedOutputStream.flush();
      bufferedOutputStream.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length < 8) {
        System.err.println("Usage : BoundaryPanel <dataset> <class col> <xAtt> <yAtt> <base> <# loc/pixel> <kernel bandwidth> <display width> <display height> <classifier [classifier options]>");
        System.exit(1);
      } 
      JFrame jFrame = new JFrame("Weka classification boundary visualizer");
      jFrame.getContentPane().setLayout(new BorderLayout());
      System.err.println("Loading instances from : " + paramArrayOfString[0]);
      BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
      Instances instances = new Instances(bufferedReader);
      instances.setClassIndex(Integer.parseInt(paramArrayOfString[1]));
      int i = Integer.parseInt(paramArrayOfString[2]);
      int j = Integer.parseInt(paramArrayOfString[3]);
      int k = Integer.parseInt(paramArrayOfString[4]);
      int m = Integer.parseInt(paramArrayOfString[5]);
      int n = Integer.parseInt(paramArrayOfString[6]);
      int i1 = Integer.parseInt(paramArrayOfString[7]);
      int i2 = Integer.parseInt(paramArrayOfString[8]);
      String str = paramArrayOfString[9];
      BoundaryPanel boundaryPanel = new BoundaryPanel(i1, i2);
      boundaryPanel.addActionListener(new ActionListener(str, boundaryPanel, instances, i, j) {
            private final String val$classifierName;
            
            private final BoundaryPanel val$bv;
            
            private final Instances val$i;
            
            private final int val$xatt;
            
            private final int val$yatt;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              String str = this.val$classifierName.substring(this.val$classifierName.lastIndexOf('.') + 1, this.val$classifierName.length());
              this.val$bv.saveImage(str + "_" + this.val$i.relationName() + "_X" + this.val$xatt + "_Y" + this.val$yatt + ".jpg");
            }
          });
      jFrame.getContentPane().add(boundaryPanel, "Center");
      jFrame.setSize(boundaryPanel.getMinimumSize());
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      boundaryPanel.repaint();
      String[] arrayOfString = null;
      if (paramArrayOfString.length > 10) {
        arrayOfString = new String[paramArrayOfString.length - 10];
        for (byte b = 10; b < paramArrayOfString.length; b++)
          arrayOfString[b - 10] = paramArrayOfString[b]; 
      } 
      Classifier classifier = Classifier.forName(paramArrayOfString[9], arrayOfString);
      KDDataGenerator kDDataGenerator = new KDDataGenerator();
      kDDataGenerator.setKernelBandwidth(n);
      boundaryPanel.setDataGenerator(kDDataGenerator);
      boundaryPanel.setNumSamplesPerRegion(m);
      boundaryPanel.setGeneratorSamplesBase(k);
      boundaryPanel.setClassifier(classifier);
      boundaryPanel.setTrainingData(instances);
      boundaryPanel.setXAttribute(i);
      boundaryPanel.setYAttribute(j);
      try {
        FileInputStream fileInputStream = new FileInputStream("colors.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        FastVector fastVector = (FastVector)objectInputStream.readObject();
        boundaryPanel.setColors(fastVector);
      } catch (Exception exception) {
        System.err.println("No color map file");
      } 
      boundaryPanel.start();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  protected class PlotThread extends Thread {
    double[] m_weightingAttsValues;
    
    boolean[] m_attsToWeightOn;
    
    double[] m_vals;
    
    double[] m_dist;
    
    Instance m_predInst;
    
    private final BoundaryPanel this$0;
    
    protected PlotThread(BoundaryPanel this$0) {
      this.this$0 = this$0;
    }
    
    public void run() {
      this.this$0.m_stopPlotting = false;
      try {
        this.this$0.initialize();
        this.this$0.repaint();
        this.this$0.m_probabilityCache = new double[this.this$0.m_panelHeight][this.this$0.m_panelWidth][];
        this.this$0.m_classifier.buildClassifier(this.this$0.m_trainingData);
        this.m_attsToWeightOn = new boolean[this.this$0.m_trainingData.numAttributes()];
        this.m_attsToWeightOn[this.this$0.m_xAttribute] = true;
        this.m_attsToWeightOn[this.this$0.m_yAttribute] = true;
        this.this$0.m_dataGenerator.setWeightingDimensions(this.m_attsToWeightOn);
        this.this$0.m_dataGenerator.buildGenerator(this.this$0.m_trainingData);
        this.m_weightingAttsValues = new double[this.m_attsToWeightOn.length];
        this.m_vals = new double[this.this$0.m_trainingData.numAttributes()];
        this.m_predInst = new Instance(1.0D, this.m_vals);
        this.m_predInst.setDataset(this.this$0.m_trainingData);
        this.this$0.m_size = 16;
        this.this$0.m_initialTiling = true;
        int i;
        label84: for (i = 0; i <= this.this$0.m_panelHeight; i += this.this$0.m_size) {
          for (int j = 0; j <= this.this$0.m_panelWidth; j += this.this$0.m_size) {
            if (this.this$0.m_stopPlotting)
              break label84; 
            if (this.this$0.m_pausePlotting)
              synchronized (this.this$0.m_dummy) {
                try {
                  this.this$0.m_dummy.wait();
                } catch (InterruptedException interruptedException) {
                  this.this$0.m_pausePlotting = false;
                } 
              }  
            this.this$0.plotPoint(j, i, this.this$0.m_size, this.this$0.m_size, calculateRegionProbs(j, i), (j == 0));
          } 
        } 
        if (!this.this$0.m_stopPlotting)
          this.this$0.m_initialTiling = false; 
        label86: for (i = this.this$0.m_size / 2; this.this$0.m_size > 1; i /= 2) {
          for (int j = 0; j <= this.this$0.m_panelHeight; j += this.this$0.m_size) {
            for (int k = 0; k <= this.this$0.m_panelWidth; k += this.this$0.m_size) {
              if (this.this$0.m_stopPlotting)
                break label86; 
              if (this.this$0.m_pausePlotting)
                synchronized (this.this$0.m_dummy) {
                  try {
                    this.this$0.m_dummy.wait();
                  } catch (InterruptedException interruptedException) {
                    this.this$0.m_pausePlotting = false;
                  } 
                }  
              boolean bool = (k == 0 && j % 2 == 0) ? true : false;
              this.this$0.plotPoint(k, j + i, i, i, calculateRegionProbs(k, j + i), bool);
              this.this$0.plotPoint(k + i, j + i, i, i, calculateRegionProbs(k + i, j + i), bool);
              this.this$0.plotPoint(k + i, j, i, i, calculateRegionProbs(k + i, j), bool);
            } 
          } 
          this.this$0.m_size = i;
        } 
        this.this$0.update();
        if (this.this$0.m_plotTrainingData)
          this.this$0.plotTrainingData(); 
      } catch (Exception exception) {
        exception.printStackTrace();
      } finally {
        Vector vector;
        this.this$0.m_plotThread = null;
        ActionEvent actionEvent = new ActionEvent(this, 0, "");
        synchronized (this) {
          vector = (Vector)this.this$0.m_listeners.clone();
        } 
        for (byte b = 0; b < vector.size(); b++) {
          ActionListener actionListener = vector.elementAt(b);
          actionListener.actionPerformed(actionEvent);
        } 
      } 
    }
    
    private double[] calculateRegionProbs(int param1Int1, int param1Int2) throws Exception {
      double[] arrayOfDouble = new double[this.this$0.m_trainingData.classAttribute().numValues()];
      for (byte b = 0; b < this.this$0.m_numOfSamplesPerRegion; b++) {
        double[] arrayOfDouble1 = new double[this.this$0.m_trainingData.classAttribute().numValues()];
        this.m_weightingAttsValues[this.this$0.m_xAttribute] = this.this$0.getRandomX(param1Int1);
        this.m_weightingAttsValues[this.this$0.m_yAttribute] = this.this$0.getRandomY(this.this$0.m_panelHeight - param1Int2 - 1);
        this.this$0.m_dataGenerator.setWeightingValues(this.m_weightingAttsValues);
        double[] arrayOfDouble2 = this.this$0.m_dataGenerator.getWeights();
        double d1 = Utils.sum(arrayOfDouble2);
        int[] arrayOfInt1 = Utils.sort(arrayOfDouble2);
        int[] arrayOfInt2 = new int[arrayOfInt1.length];
        double d2 = 0.0D;
        double d3 = 0.99D * d1;
        int i = arrayOfDouble2.length - 1;
        byte b1 = 0;
        int j;
        for (j = arrayOfDouble2.length - 1; j >= 0; j--) {
          arrayOfInt2[i--] = arrayOfInt1[j];
          d2 += arrayOfDouble2[arrayOfInt1[j]];
          b1++;
          if (d2 > d3)
            break; 
        } 
        arrayOfInt1 = new int[b1];
        System.arraycopy(arrayOfInt2, i + 1, arrayOfInt1, 0, b1);
        for (j = 0; j < this.this$0.m_numOfSamplesPerGenerator; j++) {
          this.this$0.m_dataGenerator.setWeightingValues(this.m_weightingAttsValues);
          double[][] arrayOfDouble3 = this.this$0.m_dataGenerator.generateInstances(arrayOfInt1);
          for (byte b2 = 0; b2 < arrayOfDouble3.length; b2++) {
            if (arrayOfDouble3[b2] != null) {
              System.arraycopy(arrayOfDouble3[b2], 0, this.m_vals, 0, this.m_vals.length);
              this.m_vals[this.this$0.m_xAttribute] = this.m_weightingAttsValues[this.this$0.m_xAttribute];
              this.m_vals[this.this$0.m_yAttribute] = this.m_weightingAttsValues[this.this$0.m_yAttribute];
              this.m_dist = this.this$0.m_classifier.distributionForInstance(this.m_predInst);
              for (byte b3 = 0; b3 < arrayOfDouble1.length; b3++)
                arrayOfDouble1[b3] = arrayOfDouble1[b3] + this.m_dist[b3] * arrayOfDouble2[b2]; 
            } 
          } 
        } 
        for (j = 0; j < arrayOfDouble.length; j++)
          arrayOfDouble[j] = arrayOfDouble[j] + arrayOfDouble1[j] * d1; 
      } 
      Utils.normalize(arrayOfDouble);
      if (param1Int2 < this.this$0.m_panelHeight && param1Int1 < this.this$0.m_panelWidth) {
        this.this$0.m_probabilityCache[param1Int2][param1Int1] = new double[arrayOfDouble.length];
        System.arraycopy(arrayOfDouble, 0, this.this$0.m_probabilityCache[param1Int2][param1Int1], 0, arrayOfDouble.length);
      } 
      return arrayOfDouble;
    }
  }
  
  private class PlotPanel extends JPanel {
    private final BoundaryPanel this$0;
    
    public PlotPanel(BoundaryPanel this$0) {
      this.this$0 = this$0;
      setToolTipText("");
    }
    
    public void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      if (this.this$0.m_osi != null)
        param1Graphics.drawImage(this.this$0.m_osi, 0, 0, this); 
    }
    
    public String getToolTipText(MouseEvent param1MouseEvent) {
      if (this.this$0.m_probabilityCache == null)
        return null; 
      if (this.this$0.m_probabilityCache[param1MouseEvent.getY()][param1MouseEvent.getX()] == null)
        return null; 
      String str = "(X: " + Utils.doubleToString(this.this$0.convertFromPanelX(param1MouseEvent.getX()), 2) + " Y: " + Utils.doubleToString(this.this$0.convertFromPanelY(param1MouseEvent.getY()), 2) + ") ";
      for (byte b = 0; b < this.this$0.m_trainingData.classAttribute().numValues(); b++)
        str = str + Utils.doubleToString(this.this$0.m_probabilityCache[param1MouseEvent.getY()][param1MouseEvent.getX()][b], 3) + " "; 
      return str;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\boundaryvisualizer\BoundaryPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */