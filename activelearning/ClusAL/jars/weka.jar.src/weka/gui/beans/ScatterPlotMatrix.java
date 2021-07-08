package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JFrame;
import weka.core.Instances;
import weka.gui.visualize.MatrixPanel;

public class ScatterPlotMatrix extends DataVisualizer {
  protected MatrixPanel m_matrixPanel;
  
  public ScatterPlotMatrix() {
    appearanceFinal();
  }
  
  public String globalInfo() {
    return "Visualize incoming data/training/test sets in a scatter plot matrix.";
  }
  
  protected void appearanceDesign() {
    this.m_matrixPanel = null;
    removeAll();
    this.m_visual = new BeanVisual("ScatterPlotMatrix", "weka/gui/beans/icons/ScatterPlotMatrix.gif", "weka/gui/beans/icons/ScatterPlotMatrix_animated.gif");
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
  }
  
  protected void appearanceFinal() {
    removeAll();
    setLayout(new BorderLayout());
    setUpFinal();
  }
  
  protected void setUpFinal() {
    if (this.m_matrixPanel == null)
      this.m_matrixPanel = new MatrixPanel(); 
    add((Component)this.m_matrixPanel, "Center");
  }
  
  public void setInstances(Instances paramInstances) throws Exception {
    if (this.m_design)
      throw new Exception("This method is not to be used during design time. It is meant to be used if this bean is being used programatically as as stand alone component."); 
    this.m_visualizeDataSet = paramInstances;
    this.m_matrixPanel.setInstances(this.m_visualizeDataSet);
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Show plot") == 0) {
      try {
        if (!this.m_framePoppedUp) {
          this.m_framePoppedUp = true;
          MatrixPanel matrixPanel = new MatrixPanel();
          matrixPanel.setInstances(this.m_visualizeDataSet);
          JFrame jFrame = new JFrame("Visualize");
          jFrame.setSize(800, 600);
          jFrame.getContentPane().setLayout(new BorderLayout());
          jFrame.getContentPane().add((Component)matrixPanel, "Center");
          jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
                private final JFrame val$jf;
                
                private final ScatterPlotMatrix this$0;
                
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
      throw new IllegalArgumentException(paramString + " not supported (ScatterPlotMatrix)");
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length != 1) {
        System.err.println("Usage: ScatterPlotMatrix <dataset>");
        System.exit(1);
      } 
      BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
      Instances instances = new Instances(bufferedReader);
      JFrame jFrame = new JFrame();
      jFrame.getContentPane().setLayout(new BorderLayout());
      ScatterPlotMatrix scatterPlotMatrix = new ScatterPlotMatrix();
      scatterPlotMatrix.setInstances(instances);
      jFrame.getContentPane().add(scatterPlotMatrix, "Center");
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ScatterPlotMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */