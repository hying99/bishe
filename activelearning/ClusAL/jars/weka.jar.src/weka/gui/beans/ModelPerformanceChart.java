package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import weka.core.Instances;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;

public class ModelPerformanceChart extends JPanel implements ThresholdDataListener, Visible, UserRequestAcceptor, Serializable, BeanContextChild {
  protected BeanVisual m_visual;
  
  protected transient PlotData2D m_masterPlot;
  
  protected transient JFrame m_popupFrame;
  
  protected boolean m_framePoppedUp = false;
  
  protected boolean m_design;
  
  protected transient BeanContext m_beanContext = null;
  
  private transient VisualizePanel m_visPanel;
  
  protected BeanContextChildSupport m_bcSupport = new BeanContextChildSupport(this);
  
  public ModelPerformanceChart() {
    appearanceFinal();
  }
  
  public String globalInfo() {
    return "Visualize performance charts (such as ROC).";
  }
  
  protected void appearanceDesign() {
    removeAll();
    this.m_visual = new BeanVisual("ModelPerformanceChart", "weka/gui/beans/icons/DefaultDataVisualizer.gif", "weka/gui/beans/icons/DefaultDataVisualizer_animated.gif");
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
  }
  
  protected void appearanceFinal() {
    removeAll();
    setLayout(new BorderLayout());
    setUpFinal();
  }
  
  protected void setUpFinal() {
    if (this.m_visPanel == null)
      this.m_visPanel = new VisualizePanel(); 
    add((Component)this.m_visPanel, "Center");
  }
  
  public synchronized void acceptDataSet(ThresholdDataEvent paramThresholdDataEvent) {
    if (this.m_visPanel == null)
      this.m_visPanel = new VisualizePanel(); 
    if (this.m_masterPlot == null)
      this.m_masterPlot = paramThresholdDataEvent.getDataSet(); 
    try {
      if (!this.m_masterPlot.getPlotInstances().relationName().equals(paramThresholdDataEvent.getDataSet().getPlotInstances().relationName())) {
        this.m_masterPlot = paramThresholdDataEvent.getDataSet();
        this.m_visPanel.setMasterPlot(this.m_masterPlot);
        this.m_visPanel.validate();
        this.m_visPanel.repaint();
      } else {
        this.m_visPanel.addPlot(paramThresholdDataEvent.getDataSet());
        this.m_visPanel.validate();
        this.m_visPanel.repaint();
      } 
      this.m_visPanel.setXIndex(4);
      this.m_visPanel.setYIndex(5);
    } catch (Exception exception) {
      System.err.println("Problem setting up visualization (ModelPerformanceChart)");
      exception.printStackTrace();
    } 
  }
  
  public void setVisual(BeanVisual paramBeanVisual) {
    this.m_visual = paramBeanVisual;
  }
  
  public BeanVisual getVisual() {
    return this.m_visual;
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/DefaultDataVisualizer.gif", "weka/gui/beans/icons/DefaultDataVisualizer_animated.gif");
  }
  
  public Enumeration enumerateRequests() {
    Vector vector = new Vector(0);
    if (this.m_masterPlot != null)
      vector.addElement("Show plot"); 
    return vector.elements();
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    this.m_bcSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    this.m_bcSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) {
    this.m_bcSupport.addVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) {
    this.m_bcSupport.removeVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
  
  public void setBeanContext(BeanContext paramBeanContext) {
    this.m_beanContext = paramBeanContext;
    this.m_design = this.m_beanContext.isDesignTime();
    if (this.m_design) {
      appearanceDesign();
    } else {
      appearanceFinal();
    } 
  }
  
  public BeanContext getBeanContext() {
    return this.m_beanContext;
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Show plot") == 0) {
      try {
        if (!this.m_framePoppedUp) {
          this.m_framePoppedUp = true;
          JFrame jFrame = new JFrame("Model Performance Chart");
          jFrame.setSize(800, 600);
          jFrame.getContentPane().setLayout(new BorderLayout());
          jFrame.getContentPane().add((Component)this.m_visPanel, "Center");
          jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
                private final JFrame val$jf;
                
                private final ModelPerformanceChart this$0;
                
                public void windowClosing(WindowEvent param1WindowEvent) {
                  this.val$jf.dispose();
                  this.this$0.m_framePoppedUp = false;
                }
              });
          jFrame.setVisible(true);
          this.m_popupFrame = jFrame;
        } else {
          this.m_popupFrame.toFront();
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
        this.m_framePoppedUp = false;
      } 
    } else {
      throw new IllegalArgumentException(paramString + " not supported (Model Performance Chart)");
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length != 1) {
        System.err.println("Usage: ModelPerformanceChart <dataset>");
        System.exit(1);
      } 
      BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
      Instances instances = new Instances(bufferedReader);
      JFrame jFrame = new JFrame();
      jFrame.getContentPane().setLayout(new BorderLayout());
      ModelPerformanceChart modelPerformanceChart = new ModelPerformanceChart();
      PlotData2D plotData2D = new PlotData2D(instances);
      plotData2D.setPlotName(instances.relationName());
      ThresholdDataEvent thresholdDataEvent = new ThresholdDataEvent(modelPerformanceChart, plotData2D);
      modelPerformanceChart.acceptDataSet(thresholdDataEvent);
      jFrame.getContentPane().add(modelPerformanceChart, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.setSize(800, 600);
      jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ModelPerformanceChart.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */