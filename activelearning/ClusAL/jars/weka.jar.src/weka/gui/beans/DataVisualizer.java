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

public class DataVisualizer extends JPanel implements DataSourceListener, TrainingSetListener, TestSetListener, Visible, UserRequestAcceptor, Serializable, BeanContextChild {
  protected BeanVisual m_visual;
  
  protected transient Instances m_visualizeDataSet;
  
  protected transient JFrame m_popupFrame;
  
  protected boolean m_framePoppedUp = false;
  
  protected boolean m_design;
  
  protected transient BeanContext m_beanContext = null;
  
  private VisualizePanel m_visPanel;
  
  protected BeanContextChildSupport m_bcSupport = new BeanContextChildSupport(this);
  
  public DataVisualizer() {
    appearanceFinal();
  }
  
  public String globalInfo() {
    return "Visualize incoming data/training/test sets in a 2D scatter plot.";
  }
  
  protected void appearanceDesign() {
    this.m_visPanel = null;
    removeAll();
    this.m_visual = new BeanVisual("DataVisualizer", "weka/gui/beans/icons/DefaultDataVisualizer.gif", "weka/gui/beans/icons/DefaultDataVisualizer_animated.gif");
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
  
  public void acceptTrainingSet(TrainingSetEvent paramTrainingSetEvent) {
    Instances instances = paramTrainingSetEvent.getTrainingSet();
    DataSetEvent dataSetEvent = new DataSetEvent(this, instances);
    acceptDataSet(dataSetEvent);
  }
  
  public void acceptTestSet(TestSetEvent paramTestSetEvent) {
    Instances instances = paramTestSetEvent.getTestSet();
    DataSetEvent dataSetEvent = new DataSetEvent(this, instances);
    acceptDataSet(dataSetEvent);
  }
  
  public synchronized void acceptDataSet(DataSetEvent paramDataSetEvent) {
    if (paramDataSetEvent.isStructureOnly())
      return; 
    this.m_visualizeDataSet = new Instances(paramDataSetEvent.getDataSet());
    if (this.m_visualizeDataSet.classIndex() < 0)
      this.m_visualizeDataSet.setClassIndex(this.m_visualizeDataSet.numAttributes() - 1); 
    if (!this.m_design)
      try {
        setInstances(this.m_visualizeDataSet);
      } catch (Exception exception) {
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
    if (this.m_visualizeDataSet != null)
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
  
  public void setInstances(Instances paramInstances) throws Exception {
    if (this.m_design)
      throw new Exception("This method is not to be used during design time. It is meant to be used if this bean is being used programatically as as stand alone component."); 
    this.m_visualizeDataSet = paramInstances;
    PlotData2D plotData2D = new PlotData2D(this.m_visualizeDataSet);
    plotData2D.setPlotName(this.m_visualizeDataSet.relationName());
    try {
      this.m_visPanel.setMasterPlot(plotData2D);
    } catch (Exception exception) {
      System.err.println("Problem setting up visualization (DataVisualizer)");
      exception.printStackTrace();
    } 
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Show plot") == 0) {
      try {
        if (!this.m_framePoppedUp) {
          this.m_framePoppedUp = true;
          VisualizePanel visualizePanel = new VisualizePanel();
          PlotData2D plotData2D = new PlotData2D(this.m_visualizeDataSet);
          plotData2D.setPlotName(this.m_visualizeDataSet.relationName());
          try {
            visualizePanel.setMasterPlot(plotData2D);
          } catch (Exception exception) {
            System.err.println("Problem setting up visualization (DataVisualizer)");
            exception.printStackTrace();
          } 
          JFrame jFrame = new JFrame("Visualize");
          jFrame.setSize(800, 600);
          jFrame.getContentPane().setLayout(new BorderLayout());
          jFrame.getContentPane().add((Component)visualizePanel, "Center");
          jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
                private final JFrame val$jf;
                
                private final DataVisualizer this$0;
                
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
      throw new IllegalArgumentException(paramString + " not supported (DataVisualizer)");
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length != 1) {
        System.err.println("Usage: DataVisualizer <dataset>");
        System.exit(1);
      } 
      BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
      Instances instances = new Instances(bufferedReader);
      JFrame jFrame = new JFrame();
      jFrame.getContentPane().setLayout(new BorderLayout());
      DataVisualizer dataVisualizer = new DataVisualizer();
      dataVisualizer.setInstances(instances);
      jFrame.getContentPane().add(dataVisualizer, "Center");
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\DataVisualizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */