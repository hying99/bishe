package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import weka.core.FastVector;
import weka.gui.ResultHistoryPanel;
import weka.gui.graphvisualizer.BIFFormatException;
import weka.gui.graphvisualizer.GraphVisualizer;
import weka.gui.treevisualizer.NodePlace;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

public class GraphViewer extends JPanel implements Visible, GraphListener, UserRequestAcceptor, Serializable {
  protected BeanVisual m_visual = new BeanVisual("GraphViewer", "weka/gui/beans/icons/DefaultGraph.gif", "weka/gui/beans/icons/DefaultGraph_animated.gif");
  
  private transient JFrame m_resultsFrame = null;
  
  protected transient ResultHistoryPanel m_history = new ResultHistoryPanel(null);
  
  public GraphViewer() {
    setUpResultHistory();
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
  }
  
  public String globalInfo() {
    return "Graphically visualize trees or graphs produced by classifiers/clusterers.";
  }
  
  private void setUpResultHistory() {
    if (this.m_history == null)
      this.m_history = new ResultHistoryPanel(null); 
    this.m_history.setBorder(BorderFactory.createTitledBorder("Graph list"));
    this.m_history.setHandleRightClicks(false);
    this.m_history.getList().addMouseListener((MouseListener)new ResultHistoryPanel.RMouseAdapter(this) {
          private final GraphViewer this$0;
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            int i = this.this$0.m_history.getList().locationToIndex(param1MouseEvent.getPoint());
            if (i != -1) {
              String str = this.this$0.m_history.getNameAtIndex(i);
              this.this$0.doPopup(str);
            } 
          }
        });
  }
  
  public synchronized void acceptGraph(GraphEvent paramGraphEvent) {
    FastVector fastVector = new FastVector();
    if (this.m_history == null)
      setUpResultHistory(); 
    String str = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
    str = str + paramGraphEvent.getGraphTitle();
    fastVector.addElement(new Integer(paramGraphEvent.getGraphType()));
    fastVector.addElement(paramGraphEvent.getGraphString());
    this.m_history.addResult(str, new StringBuffer());
    this.m_history.addObject(str, fastVector);
  }
  
  public void setVisual(BeanVisual paramBeanVisual) {
    this.m_visual = paramBeanVisual;
  }
  
  public BeanVisual getVisual() {
    return this.m_visual;
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/DefaultGraph.gif", "weka/gui/beans/icons/DefaultGraph_animated.gif");
  }
  
  public void showResults() {
    if (this.m_resultsFrame == null) {
      if (this.m_history == null)
        setUpResultHistory(); 
      this.m_resultsFrame = new JFrame("Graph Viewer");
      this.m_resultsFrame.getContentPane().setLayout(new BorderLayout());
      this.m_resultsFrame.getContentPane().add((Component)this.m_history, "Center");
      this.m_resultsFrame.addWindowListener(new WindowAdapter(this) {
            private final GraphViewer this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.this$0.m_resultsFrame.dispose();
              this.this$0.m_resultsFrame = null;
            }
          });
      this.m_resultsFrame.pack();
      this.m_resultsFrame.setVisible(true);
    } else {
      this.m_resultsFrame.toFront();
    } 
  }
  
  private void doPopup(String paramString) {
    FastVector fastVector = (FastVector)this.m_history.getNamedObject(paramString);
    int i = ((Integer)fastVector.firstElement()).intValue();
    String str = (String)fastVector.lastElement();
    if (i == 1) {
      JFrame jFrame = new JFrame("Weka Classifier Tree Visualizer: " + paramString);
      jFrame.setSize(500, 400);
      jFrame.getContentPane().setLayout(new BorderLayout());
      TreeVisualizer treeVisualizer = new TreeVisualizer(null, str, (NodePlace)new PlaceNode2());
      jFrame.getContentPane().add((Component)treeVisualizer, "Center");
      jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
            private final JFrame val$jf;
            
            private final GraphViewer this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
            }
          });
      jFrame.setVisible(true);
    } 
    if (i == 2) {
      JFrame jFrame = new JFrame("Weka Classifier Graph Visualizer: " + paramString);
      jFrame.setSize(500, 400);
      jFrame.getContentPane().setLayout(new BorderLayout());
      GraphVisualizer graphVisualizer = new GraphVisualizer();
      try {
        graphVisualizer.readBIF(str);
      } catch (BIFFormatException bIFFormatException) {
        System.err.println("unable to visualize BayesNet");
        bIFFormatException.printStackTrace();
      } 
      graphVisualizer.layoutGraph();
      jFrame.getContentPane().add((Component)graphVisualizer, "Center");
      jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
            private final JFrame val$jf;
            
            private final GraphViewer this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
            }
          });
      jFrame.setVisible(true);
    } 
  }
  
  public Enumeration enumerateRequests() {
    Vector vector = new Vector(0);
    vector.addElement("Show results");
    return vector.elements();
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Show results") == 0) {
      showResults();
    } else {
      throw new IllegalArgumentException(paramString + " not supported (GraphViewer)");
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\GraphViewer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */