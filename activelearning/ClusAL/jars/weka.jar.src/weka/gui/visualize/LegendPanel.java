package weka.gui.visualize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import weka.core.FastVector;
import weka.core.Instances;

public class LegendPanel extends JScrollPane {
  protected FastVector m_plots;
  
  protected JPanel m_span = null;
  
  protected FastVector m_Repainters = new FastVector();
  
  public LegendPanel() {
    setBackground(Color.blue);
    setVerticalScrollBarPolicy(22);
  }
  
  public void setPlotList(FastVector paramFastVector) {
    this.m_plots = paramFastVector;
    updateLegends();
  }
  
  public void addRepaintNotify(Component paramComponent) {
    this.m_Repainters.addElement(paramComponent);
  }
  
  private void updateLegends() {
    if (this.m_span == null)
      this.m_span = new JPanel(); 
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    this.m_span.setPreferredSize(new Dimension((this.m_span.getPreferredSize()).width, (this.m_plots.size() + 1) * 20));
    this.m_span.setMaximumSize(new Dimension((this.m_span.getPreferredSize()).width, (this.m_plots.size() + 1) * 20));
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    this.m_span.removeAll();
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
    gridBagConstraints.insets = new Insets(2, 4, 2, 4);
    for (byte b = 0; b < this.m_plots.size(); b++) {
      LegendEntry legendEntry = new LegendEntry(this, (PlotData2D)this.m_plots.elementAt(b), b);
      gridBagConstraints.gridy = b;
      this.m_span.add(legendEntry, gridBagConstraints);
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length < 1) {
        System.err.println("Usage : weka.gui.visualize.LegendPanel <dataset> [dataset2], [dataset3],...");
        System.exit(1);
      } 
      JFrame jFrame = new JFrame("Weka Explorer: Legend");
      jFrame.setSize(100, 100);
      jFrame.getContentPane().setLayout(new BorderLayout());
      LegendPanel legendPanel = new LegendPanel();
      jFrame.getContentPane().add(legendPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      FastVector fastVector = new FastVector();
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        System.err.println("Loading instances from " + paramArrayOfString[b]);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[b]));
        Instances instances = new Instances(bufferedReader);
        PlotData2D plotData2D = new PlotData2D(instances);
        if (b != 1) {
          plotData2D.m_useCustomColour = true;
          plotData2D.m_customColour = Color.red;
        } 
        plotData2D.setPlotName(instances.relationName());
        fastVector.addElement(plotData2D);
      } 
      legendPanel.setPlotList(fastVector);
      jFrame.setVisible(true);
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
  
  protected class LegendEntry extends JPanel {
    private PlotData2D m_plotData;
    
    private int m_dataIndex;
    
    private JLabel m_legendText;
    
    private JPanel m_pointShape;
    
    private final LegendPanel this$0;
    
    public LegendEntry(LegendPanel this$0, PlotData2D param1PlotData2D, int param1Int) {
      this.this$0 = this$0;
      this.m_plotData = null;
      this.m_plotData = param1PlotData2D;
      this.m_dataIndex = param1Int;
      if (this.m_plotData.m_useCustomColour)
        addMouseListener((MouseListener)new Object(this, this$0)); 
      this.m_legendText = new JLabel(this.m_plotData.m_plotName);
      if (this.m_plotData.m_useCustomColour)
        this.m_legendText.setForeground(this.m_plotData.m_customColour); 
      setLayout(new BorderLayout());
      add(this.m_legendText, "Center");
      this.m_pointShape = (JPanel)new Object(this);
      this.m_pointShape.setPreferredSize(new Dimension(20, 20));
      this.m_pointShape.setMinimumSize(new Dimension(20, 20));
      add(this.m_pointShape, "West");
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\LegendPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */