package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.beancontext.BeanContext;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import weka.core.Instances;
import weka.gui.AttributeVisualizationPanel;

public class AttributeSummarizer extends DataVisualizer {
  protected int m_gridWidth = 4;
  
  protected int m_maxPlots = 100;
  
  protected int m_coloringIndex = -1;
  
  public AttributeSummarizer() {
    appearanceFinal();
  }
  
  public String globalInfo() {
    return "Plot summary bar charts for incoming data/training/test sets.";
  }
  
  public void setColoringIndex(int paramInt) {
    this.m_coloringIndex = paramInt;
  }
  
  public int getColoringIndex() {
    return this.m_coloringIndex;
  }
  
  public void setGridWidth(int paramInt) {
    if (paramInt > 0) {
      this.m_bcSupport.firePropertyChange("gridWidth", new Integer(this.m_gridWidth), new Integer(paramInt));
      this.m_gridWidth = paramInt;
    } 
  }
  
  public int getGridWidth() {
    return this.m_gridWidth;
  }
  
  public void setMaxPlots(int paramInt) {
    if (paramInt > 0) {
      this.m_bcSupport.firePropertyChange("maxPlots", new Integer(this.m_maxPlots), new Integer(paramInt));
      this.m_maxPlots = paramInt;
    } 
  }
  
  public int getMaxPlots() {
    return this.m_maxPlots;
  }
  
  public void setDesign(boolean paramBoolean) {
    this.m_design = true;
    appearanceDesign();
  }
  
  protected void appearanceDesign() {
    removeAll();
    this.m_visual = new BeanVisual("AttributeSummarizer", "weka/gui/beans/icons/AttributeSummarizer.gif", "weka/gui/beans/icons/AttributeSummarizer_animated.gif");
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
  }
  
  protected void appearanceFinal() {
    removeAll();
    setLayout(new BorderLayout());
  }
  
  protected void setUpFinal() {
    removeAll();
    JScrollPane jScrollPane = makePanel();
    add(jScrollPane, "Center");
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/DefaultDataVisualizer.gif", "weka/gui/beans/icons/DefaultDataVisualizer_animated.gif");
  }
  
  public Enumeration enumerateRequests() {
    Vector vector = new Vector(0);
    if (this.m_visualizeDataSet != null)
      vector.addElement("Show summaries"); 
    return vector.elements();
  }
  
  private JScrollPane makePanel() {
    String str = getFont().getFamily();
    Font font = new Font(str, 0, 10);
    JPanel jPanel = new JPanel();
    jPanel.setFont(font);
    int i = Math.min(this.m_visualizeDataSet.numAttributes(), this.m_maxPlots);
    int j = i / this.m_gridWidth;
    if (i % this.m_gridWidth != 0)
      j++; 
    jPanel.setLayout(new GridLayout(j, 4));
    for (byte b = 0; b < i; b++) {
      JPanel jPanel1 = new JPanel();
      jPanel1.setLayout(new BorderLayout());
      jPanel1.setBorder(BorderFactory.createTitledBorder(this.m_visualizeDataSet.attribute(b).name()));
      AttributeVisualizationPanel attributeVisualizationPanel = new AttributeVisualizationPanel();
      attributeVisualizationPanel.setInstances(this.m_visualizeDataSet);
      attributeVisualizationPanel.setColoringIndex(this.m_coloringIndex);
      jPanel1.add((Component)attributeVisualizationPanel, "Center");
      attributeVisualizationPanel.setAttribute(b);
      jPanel.add(jPanel1);
    } 
    Dimension dimension = new Dimension(830, j * 100);
    jPanel.setMinimumSize(dimension);
    jPanel.setMaximumSize(dimension);
    jPanel.setPreferredSize(dimension);
    return new JScrollPane(jPanel);
  }
  
  public void setBeanContext(BeanContext paramBeanContext) {
    this.m_beanContext = paramBeanContext;
    this.m_design = this.m_beanContext.isDesignTime();
    if (this.m_design)
      appearanceDesign(); 
  }
  
  public void setInstances(Instances paramInstances) throws Exception {
    if (this.m_design)
      throw new Exception("This method is not to be used during design time. It is meant to be used if this bean is being used programatically as as stand alone component."); 
    this.m_visualizeDataSet = paramInstances;
    setUpFinal();
  }
  
  public void performRequest(String paramString) {
    if (!this.m_design) {
      setUpFinal();
      return;
    } 
    if (paramString.compareTo("Show summaries") == 0) {
      try {
        if (!this.m_framePoppedUp) {
          this.m_framePoppedUp = true;
          JScrollPane jScrollPane = makePanel();
          JFrame jFrame = new JFrame("Visualize");
          jFrame.setSize(800, 600);
          jFrame.getContentPane().setLayout(new BorderLayout());
          jFrame.getContentPane().add(jScrollPane, "Center");
          jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
                private final JFrame val$jf;
                
                private final AttributeSummarizer this$0;
                
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
      throw new IllegalArgumentException(paramString + " not supported (AttributeSummarizer)");
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length != 1) {
        System.err.println("Usage: AttributeSummarizer <dataset>");
        System.exit(1);
      } 
      BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
      Instances instances = new Instances(bufferedReader);
      JFrame jFrame = new JFrame();
      jFrame.getContentPane().setLayout(new BorderLayout());
      AttributeSummarizer attributeSummarizer = new AttributeSummarizer();
      attributeSummarizer.setInstances(instances);
      jFrame.getContentPane().add(attributeSummarizer, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.setSize(830, 600);
      jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\AttributeSummarizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */